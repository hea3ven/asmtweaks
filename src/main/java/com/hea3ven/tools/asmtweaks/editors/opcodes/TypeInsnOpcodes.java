package com.hea3ven.tools.asmtweaks.editors.opcodes;

import org.objectweb.asm.Opcodes;

public enum TypeInsnOpcodes {
	NEW(Opcodes.NEW),
	ANEWARRAY(Opcodes.ANEWARRAY),
	CHECKCAST(Opcodes.CHECKCAST),
	INSTANCEOF(Opcodes.INSTANCEOF);

	private final int opcode;

	TypeInsnOpcodes(int opcode) {
		this.opcode = opcode;
	}

	public int getOpcode() {
		return opcode;
	}
}
