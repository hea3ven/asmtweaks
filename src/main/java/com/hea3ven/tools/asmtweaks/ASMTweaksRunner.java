package com.hea3ven.tools.asmtweaks;

import java.util.HashSet;

import com.google.common.collect.ImmutableMap;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;

import net.minecraft.launchwrapper.IClassTransformer;

import com.hea3ven.tools.mappings.ClsMapping;
import com.hea3ven.tools.mappings.Desc;
import com.hea3ven.tools.mappings.MthdMapping;
import com.hea3ven.tools.mappings.ObfLevel;

public class ASMTweaksRunner implements IClassTransformer {
	private static Logger logger = LogManager.getLogger("asmtweaks.ASMTweaksManager");

	private final ASMTweaksManager mgr;
	private final HashSet<String> tweakedClsNames = new HashSet<>();
	private final HashSet<String> tweakedClsObfNames = new HashSet<>();

	private final HashSet<ASMTweak> tweaks = new HashSet<>();

	public ASMTweaksRunner(ASMTweaksManager mgr) {
		this.mgr = mgr;
	}

	public ASMTweaksManager getManager() {
		return mgr;
	}

	public void addTweak(ASMTweak tweak) {
		if (mgr.getConfig().isEnabled(tweak)) {
			tweak.configure(mgr.getConfig().getTweakConfig(tweak));
			tweaks.add(tweak);
			for (ASMMod mod : tweak.getModifications()) {
				if (mgr.isClient() || !mod.isClientSideOnly()) {
					ClsMapping cls = mgr.getMapping().getCls(mod.getClassName(), ObfLevel.DEOBF);
					if (cls != null) {
						String deobfName = cls.getPath(ObfLevel.DEOBF) != null ? cls.getPath(ObfLevel.DEOBF) :
								cls.getPath(ObfLevel.OBF);
						tweakedClsNames.add(deobfName);
						tweakedClsObfNames.add(cls.getPath(ObfLevel.OBF));
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

		if (mgr.getObfuscationMode() == ObfLevel.DEOBF) {
			if (!tweakedClsNames.contains(name))
				return basicClass;
		} else {
			if (!tweakedClsObfNames.contains(name))
				return basicClass;
		}

		ClsMapping clsMap = mgr.getMapping().getCls(name, ObfLevel.OBF);

		ClassNode cls = null;
		for (ASMTweak tweak : tweaks) {
			for (ASMMod mod : tweak.getModifications()) {
				if (mgr.isClient() || !mod.isClientSideOnly()) {
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
		if (clsName.matches(ObfLevel.DEOBF, mod.getClassName())) {
			if (cls == null)
				cls = ASMUtils.readClass(basicClass);
			logger.info("applying class modification from {} to {}({})", tweak.getName(),
					clsName.getPath(ObfLevel.OBF), clsName.getPath(ObfLevel.DEOBF));
			mod.handle(mgr, cls);
		}
		return cls;
	}

	private ClassNode handleMethodMod(ASMTweak tweak, ASMMethodMod mod, ClsMapping clsName, ClassNode cls,
			byte[] basicClass) {
		if (clsName.matches(ObfLevel.DEOBF, mod.getClassName())) {
			if (cls == null)
				cls = ASMUtils.readClass(basicClass);
			MthdMapping mthdName =
					mgr.getMapping().getMthd(mod.getMethodName(), mod.getMethodDesc(), ObfLevel.DEOBF);
			if (mthdName == null) {
				String methodName = mod.getMethodName();
				methodName = methodName.substring(methodName.lastIndexOf('/') + 1);
				mthdName = new MthdMapping(clsName,
						ImmutableMap.of(ObfLevel.OBF, methodName, ObfLevel.DEOBF, methodName),
						Desc.parse(mgr.getMapping(), mod.getMethodDesc()));
			}
			MethodNode mthd = ASMUtils.getMethod(cls, mthdName);
			if (mthd == null) {
				logger.error("could not find method {}({}) {}({}) for tweak {}",
						mthdName.getName(ObfLevel.OBF),
						mthdName.getName(ObfLevel.DEOBF), mthdName.getDesc().get(ObfLevel.OBF),
						mthdName.getDesc().get(ObfLevel.DEOBF),
						tweak.getName());
				throw new RuntimeException("failed patching a class");
			}
			logger.info("applying method modification from {} to {}({})", tweak.getName(),
					mthdName.getName(ObfLevel.OBF), mthdName.getName(ObfLevel.DEOBF));
			mod.handle(mgr, mthd);
		}
		return cls;
	}
}
