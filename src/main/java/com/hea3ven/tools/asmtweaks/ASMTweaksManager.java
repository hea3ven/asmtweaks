package com.hea3ven.tools.asmtweaks;

import java.util.HashSet;

import com.google.common.collect.Sets;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;

import net.minecraft.launchwrapper.IClassTransformer;

import com.hea3ven.tools.mappings.*;

public class ASMTweaksManager implements IClassTransformer {

	private static Logger logger = LogManager.getLogger("asmtweaks.ASMTweaksManager");

	private final String currentVersion;
	private final boolean isClient;

	private boolean detectedObfuscation = false;
	private boolean obfuscated = false;

	private ASMTweaksConfig config;

	private HashSet<String> tweakedClsNames = Sets.newHashSet();
	private HashSet<String> tweakedClsObfNames = Sets.newHashSet();

	private HashSet<ASMTweak> tweaks = Sets.newHashSet();

	private Mapping mapping;
	private Mapping srgMapping;

	public ASMTweaksManager(String currentVersion, boolean isClient) {
		logger.info("using mappings for version {}", currentVersion);
		this.currentVersion = currentVersion;
		this.isClient = isClient;
		config = new ASMTweaksConfig();
	}

	public String getCurrentVersion() {
		return currentVersion;
	}

	public boolean isObfuscated() {
		if (!detectedObfuscation)
			throw new RuntimeException("could not detect if running in an obfuscated environment");
		return obfuscated;
	}

	public ASMTweaksConfig getConfig() {
		return config;
	}

	public void setMapping(Mapping mapping) {
		this.mapping = mapping;
	}

	public void setSrgMapping(Mapping mapping) {
		this.srgMapping = mapping;
	}

	public ClsMapping getClass(String className) {
		if (obfuscated && srgMapping != null) {
			ClsMapping clsMap = srgMapping.getCls(className);
			if (clsMap != null && clsMap.getDstPath() != null)
				className = clsMap.getDstPath();
		}
		return mapping.getCls(className);
	}

	public MthdMapping getMethod(String methodName, String desc) {
		if (obfuscated && srgMapping != null) {
			MthdMapping mthdMap = srgMapping.getMthd(methodName, desc);
			if (mthdMap != null) {
				methodName = mthdMap.getDstPath();
				desc = mthdMap.getDesc().getDst();
			}
		}
		return mapping.getMthd(methodName, desc);
	}

	public FldMapping getField(String fieldName) {
		if (obfuscated && srgMapping != null) {
			FldMapping fldMap = srgMapping.getFld(fieldName);
			if (fldMap != null)
				fieldName = fldMap.getDstPath();
		}
		return mapping.getFld(fieldName);
	}

	public void addTweak(ASMTweak tweak) {
		if (config.isEnabled(tweak)) {
			tweak.configure(config.getTweakConfig(tweak));
			tweaks.add(tweak);
			for (ASMMod mod : tweak.getModifications()) {
				if (isClient || !mod.isClientSideOnly()) {
					ClsMapping cls = getClass(mod.getClassName());
					if (cls != null) {
						String deobfName = cls.getDstPath() != null ? cls.getDstPath() : cls.getSrcPath();
						tweakedClsNames.add(deobfName);
						tweakedClsObfNames.add(cls.getSrcPath());
					} else {
						tweakedClsNames.add(mod.getClassName());
						tweakedClsObfNames.add(mod.getClassName());
					}
				}
			}
		}
	}

	public byte[] transform(String name, String transformedName, byte[] basicClass) {
		if (basicClass == null)
			return null;

		name = name.replace(".", "/");

		if (!detectedObfuscation) {
			if (!tweakedClsNames.contains(name) && !tweakedClsObfNames.contains(name))
				return basicClass;
			obfuscated = !tweakedClsNames.contains(name);
			logger.info("detected that obfuscation is {}", obfuscated);
			detectedObfuscation = true;
		}

		if ((obfuscated && !tweakedClsObfNames.contains(name)) ||
				(!obfuscated && !tweakedClsNames.contains(name)))
			return basicClass;

		ClsMapping clsMap = mapping.getCls(name);
		if (clsMap == null)
			clsMap = new ClsMapping(name, name);

		ClassNode cls = null;
		for (ASMTweak tweak : tweaks) {
			for (ASMMod mod : tweak.getModifications()) {
				if (isClient || !mod.isClientSideOnly()) {
					if (mod instanceof ASMClassMod) {
						cls = handleClassMod(tweak, (ASMClassMod) mod, clsMap, cls, basicClass);
					} else if (mod instanceof ASMMethodMod) {
						cls = handleMethodMod(tweak, (ASMMethodMod) mod, clsMap, cls, basicClass);
					}
				}
			}
		}

		if (cls != null) {
			return ASMUtils.writeClass(cls);
		} else
			return basicClass;
	}

	private ClassNode handleClassMod(ASMTweak tweak, ASMClassMod mod, ClsMapping clsName, ClassNode cls,
			byte[] basicClass) {
		if (clsName.matches(mod.getClassName())) {
			if (cls == null)
				cls = ASMUtils.readClass(basicClass);
			logger.info("applying class modification from {} to {}({})", tweak.getName(),
					clsName.getSrcPath(), clsName.getDstPath());
			mod.handle(this, cls);
		}
		return cls;
	}

	private ClassNode handleMethodMod(ASMTweak tweak, ASMMethodMod mod, ClsMapping clsName, ClassNode cls,
			byte[] basicClass) {
		if (clsName.matches(mod.getClassName())) {
			if (cls == null)
				cls = ASMUtils.readClass(basicClass);
			MthdMapping mthdName = getMethod(mod.getMethodName(), mod.getMethodDesc());
			if (mthdName == null) {
				String methodName = mod.getMethodName();
				methodName = methodName.substring(methodName.lastIndexOf('/') + 1);
				mthdName = new MthdMapping(clsName, methodName, methodName,
						Desc.parse(mapping, mod.getMethodDesc()));
			}
			MethodNode mthd = ASMUtils.getMethod(cls, mthdName);
			if (mthd == null) {
				logger.error("could not find method {}({}) {}({}) for tweak {}", mthdName.getSrcName(),
						mthdName.getDstName(), mthdName.getDesc().getSrc(), mthdName.getDesc().getDst(),
						tweak.getName());
				throw new RuntimeException("failed patching a class");
			}
			logger.info("applying method modification from {} to {}({})", tweak.getName(),
					mthdName.getSrcName(), mthdName.getDstName());
			mod.handle(this, mthd);
		}
		return cls;
	}

	public void error(String msg) {
		logger.error(msg);
		throw new RuntimeException("failed patching a class");
	}
}
