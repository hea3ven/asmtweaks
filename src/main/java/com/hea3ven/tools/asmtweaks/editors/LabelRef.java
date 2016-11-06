package com.hea3ven.tools.asmtweaks.editors;

import org.objectweb.asm.Label;

public class LabelRef {
	private final Label label;

	LabelRef(Label label) {
		this.label = label;
	}

	Label getLabel() {
		return label;
	}
}
