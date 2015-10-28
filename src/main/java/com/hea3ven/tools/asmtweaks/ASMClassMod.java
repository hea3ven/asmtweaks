package com.hea3ven.tools.asmtweaks;

import org.objectweb.asm.tree.ClassNode;

public interface ASMClassMod extends ASMMod {

	void handle(ASMTweaksManager mgr, ClassNode cls);

}
