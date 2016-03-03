package com.hea3ven.tools.asmtweaks.tweaks;

import org.objectweb.asm.tree.MethodNode;

import com.hea3ven.tools.asmtweaks.ASMMethodMod;
import com.hea3ven.tools.asmtweaks.ASMTweaksManager;
import com.hea3ven.tools.asmtweaks.editors.MethodEditor;

public abstract class ASMMethodModEditCode implements ASMMethodMod {
	private final String mthdName;
	private final String mthdDesc;

	public ASMMethodModEditCode(String mthdName, String mthdDesc) {
		this.mthdName = mthdName;
		this.mthdDesc = mthdDesc;
	}

	@Override
	public String getClassName() {
		return mthdName.substring(0, mthdName.lastIndexOf('/'));
	}

	@Override
	public String getMethodName() {
		return mthdName;
	}

	@Override
	public String getMethodDesc() {
		return mthdDesc;
	}

	@Override
	public void handle(ASMTweaksManager mgr, MethodNode method) {
		handle(new MethodEditor(mgr, method));
	}

	protected abstract void handle(MethodEditor editor);
}
