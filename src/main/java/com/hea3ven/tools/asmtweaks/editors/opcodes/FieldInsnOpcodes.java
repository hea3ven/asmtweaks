package com.hea3ven.tools.asmtweaks.editors.opcodes;

import org.objectweb.asm.Opcodes;

public enum FieldInsnOpcodes {
	GETSTATIC(Opcodes.GETSTATIC),
	PUTSTATIC(Opcodes.PUTSTATIC),
	GETFIELD(Opcodes.GETFIELD),
	PUTFIELD(Opcodes.PUTFIELD);

	private final int opcode;

	FieldInsnOpcodes(int opcode) {
		this.opcode = opcode;
	}

	public int getOpcode() {
		return opcode;
	}
}
