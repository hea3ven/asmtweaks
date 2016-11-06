package com.hea3ven.tools.asmtweaks.editors.opcodes;

import org.objectweb.asm.Opcodes;

public enum VarInsnOpcodes {
	ILOAD(Opcodes.ILOAD),
	LLOAD(Opcodes.LLOAD),
	FLOAD(Opcodes.FLOAD),
	DLOAD(Opcodes.DLOAD),
	ALOAD(Opcodes.ALOAD),
	ISTORE(Opcodes.ISTORE),
	LSTORE(Opcodes.LSTORE),
	FSTORE(Opcodes.FSTORE),
	DSTORE(Opcodes.DSTORE),
	ASTORE(Opcodes.ASTORE),
	RET(Opcodes.RET);

	private final int opcode;

	VarInsnOpcodes(int opcode) {
		this.opcode = opcode;
	}

	public int getOpcode() {
		return opcode;
	}
}
