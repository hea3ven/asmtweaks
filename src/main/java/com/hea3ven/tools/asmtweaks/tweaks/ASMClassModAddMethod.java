package com.hea3ven.tools.asmtweaks.tweaks;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;

import com.hea3ven.tools.asmtweaks.ASMClassMod;
import com.hea3ven.tools.asmtweaks.ASMTweaksManager;
import com.hea3ven.tools.asmtweaks.editors.MethodEditor;
import com.hea3ven.tools.mappings.MthdMapping;

public abstract class ASMClassModAddMethod implements ASMClassMod {
	private final String targetCls;
	private final String mthdName;
	private final String mthdDesc;

	public ASMClassModAddMethod(String targetCls, String mthdName, String mthdDesc) {
		this.targetCls = targetCls;
		this.mthdName = mthdName;
		this.mthdDesc = mthdDesc;
	}

	@Override
	public String getClassName() {
		return targetCls;
	}

	@Override
	public void handle(ASMTweaksManager mgr, ClassNode cls) {
		MthdMapping mthd = mgr.getMethod(mthdName, mthdDesc);
		String desc = mthd.getDesc().get(mgr.isObfuscated());
		MethodNode method =
				new MethodNode(Opcodes.ASM5, Opcodes.ACC_PUBLIC, mthd.getName(mgr.isObfuscated()), desc, null,
						null);

		handle(new MethodEditor(mgr, method));
		cls.methods.add(method);
	}

	abstract protected void handle(MethodEditor editor);
}
