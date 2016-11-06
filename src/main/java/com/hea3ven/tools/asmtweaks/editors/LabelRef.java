package com.hea3ven.tools.asmtweaks.editors;

import org.objectweb.asm.tree.LabelNode;

public class LabelRef {
	private final LabelNode label;

	LabelRef(LabelNode label) {
		this.label = label;
	}

	LabelNode getLabel() {
		return label;
	}
}
