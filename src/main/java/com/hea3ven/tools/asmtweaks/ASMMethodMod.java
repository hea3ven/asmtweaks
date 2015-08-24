package com.hea3ven.tools.asmtweaks;

import org.objectweb.asm.tree.MethodNode;

public interface ASMMethodMod extends ASMMod {

	String getClassName();

	String getMethodName();

	void handle(ASMTweaksManager mgr, MethodNode method);

}
