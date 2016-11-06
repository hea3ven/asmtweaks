package com.hea3ven.tools.asmtweaks.editors.opcodes;

import org.objectweb.asm.Opcodes;

public enum MethodInsnOpcodes {
	INVOKEVIRTUAL(Opcodes.INVOKEVIRTUAL),
	INVOKESPECIAL(Opcodes.INVOKESPECIAL),
	INVOKESTATIC(Opcodes.INVOKESTATIC),
	INVOKEINTERFACE(Opcodes.INVOKEINTERFACE);

	private final int opcode;

	MethodInsnOpcodes(int opcode) {
		this.opcode = opcode;
	}

	public int getOpcode() {
		return opcode;
	}
}
