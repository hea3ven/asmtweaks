package com.hea3ven.tools.asmtweaks;

import java.util.HashSet;

import com.google.common.collect.Sets;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;

import net.minecraft.launchwrapper.IClassTransformer;

import com.hea3ven.tools.mappings.ClsMapping;
import com.hea3ven.tools.mappings.FldMapping;
import com.hea3ven.tools.mappings.Mapping;
import com.hea3ven.tools.mappings.MthdMapping;

public class ASMTweaksManager implements IClassTransformer {

	private static Logger logger = LogManager.getLogger("asmtweaks.ASMTweaksManager");

	private String currentVersion;

	private boolean detectedObfuscation = false;
	private boolean obfuscated = false;

	private ASMTweaksConfig config;

	private HashSet<ASMTweak> tweaks = Sets.newHashSet();
	private HashSet<ClsMapping> clssToTweak = Sets.newHashSet();

	private Mapping mapping;

	public ASMTweaksManager(String currentVersion) {
		logger.info("using mappings for version {}", currentVersion);
		this.currentVersion = currentVersion;
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

	public ClsMapping getClass(String className) {
		return mapping.getCls(className);
	}

	public MthdMapping getMethod(String methodName) {
		return mapping.getMthd(methodName);
	}

	public FldMapping getField(String fieldName) {
		return mapping.getFld(fieldName);
	}

	public void addTweak(ASMTweak tweak) {
		if (config.isEnabled(tweak)) {
			tweak.configure(config.getTweakConfig(tweak));
			tweaks.add(tweak);
			for (ASMMod mod : tweak.getModifications()) {
				if (mod instanceof ASMClassMod) {
					clssToTweak.add(getClass(((ASMClassMod) mod).getClassName()));
				} else if (mod instanceof ASMMethodMod) {
					clssToTweak.add(getClass(((ASMMethodMod) mod).getClassName()));
				}
			}
		}
	}

	public byte[] transform(String name, String transformedName, byte[] basicClass) {
		if (basicClass == null)
			return basicClass;

		ClsMapping clsMap = mapping.getCls(name);
		if (!clssToTweak.contains(clsMap))
			return basicClass;

		if (!detectedObfuscation) {
			obfuscated = name.equals(clsMap.getSrcName());
			logger.info("detected that obfuscation is {}", obfuscated);
			detectedObfuscation = true;
		}

		ClassNode cls = null;
		for (ASMTweak tweak : tweaks) {
			for (ASMMod mod : tweak.getModifications()) {
				if (mod instanceof ASMClassMod) {
					cls = handleClassMod(tweak, (ASMClassMod) mod, clsMap, cls, basicClass);
				} else if (mod instanceof ASMMethodMod) {
					cls = handleMethodMod(tweak, (ASMMethodMod) mod, clsMap, cls, basicClass);
				}
			}
		}

		if (cls != null) {
			return ASMUtils.writeClass(cls);
		} else
			return basicClass;
	}

	private ClassNode handleClassMod(ASMTweak tweak, ASMClassMod mod, ClsMapping clsName,
			ClassNode cls, byte[] basicClass) {
		if (clsName.matches(mod.getClassName())) {
			if (cls == null)
				cls = ASMUtils.readClass(basicClass);
			logger.info("applying class modification from {} to {}({})", tweak.getName(),
					clsName.getSrcPath(), clsName.getDstPath());
			mod.handle(this, cls);
		}
		return cls;

	}

	private ClassNode handleMethodMod(ASMTweak tweak, ASMMethodMod mod, ClsMapping clsName,
			ClassNode cls, byte[] basicClass) {
		if (clsName.matches(mod.getClassName())) {
			if (cls == null)
				cls = ASMUtils.readClass(basicClass);
			MthdMapping mthdName = getMethod(mod.getMethodName());
			MethodNode mthd = ASMUtils.getMethod(cls, mthdName.getSrcName(),
					mthdName.getDesc().getSrc());
			if (mthd == null) {
				logger.error("could not find method {}({}) {}({}) for tweak {}",
						mthdName.getSrcName(), mthdName.getDstName(), mthdName.getDesc().getSrc(),
						mthdName.getDesc().getDst(), tweak.getName());
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
