package com.hea3ven.tools.asmtweaks.editors;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.objectweb.asm.tree.*;

import com.hea3ven.tools.asmtweaks.editors.opcodes.*;
import com.hea3ven.tools.mappings.ObfLevel;

public class InstructionBuilder {
	private final ASMContext ctx;
	private List<AbstractInsnNode> insns = new ArrayList<>();

	public InstructionBuilder(ASMContext ctx) {
		this.ctx = ctx;
	}

	public ASMContext getCtx() {
		return ctx;
	}

	public List<AbstractInsnNode> build() {
		Map<LabelNode, LabelNode> labels = insns.stream()
				.filter(e -> e instanceof LabelNode)
				.collect(
						Collectors.toMap(e -> (LabelNode) e, e -> new LabelNode(((LabelNode) e).getLabel())));
		return insns.stream().map(e -> e.clone(labels)).collect(Collectors.toList());
	}

	public InstructionBuilder label(LabelRef label) {
		insns.add(label.getLabel());
		return this;
	}

	public InstructionBuilder insn(InsnOpcodes opcode) {
		insns.add(new InsnNode(opcode.getOpcode()));
		return this;
	}

	public InstructionBuilder typeInsn(TypeInsnOpcodes opcode, String desc) {
		insns.add(new TypeInsnNode(opcode.getOpcode(), ctx.getType(ObfLevel.DEOBF, desc)));
		return this;
	}

	public InstructionBuilder jumpInsn(JumpInsnOpcodes opcode, LabelRef labelNode) {
		insns.add(new JumpInsnNode(opcode.getOpcode(), labelNode.getLabel()));
		return this;
	}

	public InstructionBuilder varInsn(VarInsnOpcodes opcode, int var) {
		insns.add(new VarInsnNode(opcode.getOpcode(), var));
		return this;
	}

	public InstructionBuilder fieldInsn(FieldInsnOpcodes opcode, String owner, String name, String desc) {
		insns.add(new FieldInsnNode(opcode.getOpcode(), ctx.getCls(ObfLevel.DEOBF, owner),
				ctx.getFld(ObfLevel.DEOBF, name, desc), ctx.getTypeDesc(ObfLevel.DEOBF, desc)));
		return this;
	}

	public InstructionBuilder methodInsn(MethodInsnOpcodes opcode, String owner, String name, String desc) {
		insns.add(new MethodInsnNode(opcode.getOpcode(), ctx.getCls(ObfLevel.DEOBF, owner),
				ctx.getMthd(ObfLevel.DEOBF, name, desc), ctx.getDesc(ObfLevel.DEOBF, desc),
				opcode == MethodInsnOpcodes.INVOKEINTERFACE));
		return this;
	}
}
