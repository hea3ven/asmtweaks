package com.hea3ven.tools.asmtweaks.editors.opcodes;

import org.objectweb.asm.Opcodes;

public enum JumpInsnOpcodes {
	IFEQ(Opcodes.IFEQ),
	IFNE(Opcodes.IFNE),
	IFLT(Opcodes.IFLT),
	IFGE(Opcodes.IFGE),
	IFGT(Opcodes.IFGT),
	IFLE(Opcodes.IFLE),
	IF_ICMPEQ(Opcodes.IF_ICMPEQ),
	IF_ICMPNE(Opcodes.IF_ICMPNE),
	IF_ICMPLT(Opcodes.IF_ICMPLT),
	IF_ICMPGE(Opcodes.IF_ICMPGE),
	IF_ICMPGT(Opcodes.IF_ICMPGT),
	IF_ICMPLE(Opcodes.IF_ICMPLE),
	IF_ACMPEQ(Opcodes.IF_ACMPEQ),
	IF_ACMPNE(Opcodes.IF_ACMPNE),
	GOTO(Opcodes.GOTO),
	JSR(Opcodes.JSR),
	IFNULL(Opcodes.IFNULL),
	IFNONNULL(Opcodes.IFNONNULL);

	private final int opcode;

	JumpInsnOpcodes(int opcode) {
		this.opcode = opcode;
	}

	public int getOpcode() {
		return opcode;
	}
}
