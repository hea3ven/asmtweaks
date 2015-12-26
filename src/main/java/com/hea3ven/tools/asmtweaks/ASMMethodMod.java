package com.hea3ven.tools.asmtweaks;

import org.objectweb.asm.tree.MethodNode;

public interface ASMMethodMod extends ASMMod {

	String getMethodName();

	String getMethodDesc();

	void handle(ASMTweaksManager mgr, MethodNode method);
}
