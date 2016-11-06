package com.hea3ven.tools.asmtweaks.editors;

import java.lang.reflect.Field;
import java.util.List;

import org.junit.Test;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

import com.hea3ven.tools.asmtweaks.editors.opcodes.*;
import com.hea3ven.tools.mappings.Mapping;
import com.hea3ven.tools.mappings.ObfLevel;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class InstructionBuilderTest {

	private List<AbstractInsnNode> getNodes(InstructionBuilder builder) {
		try {
			Field insnsField = InstructionBuilder.class.getDeclaredField("insns");
			insnsField.setAccessible(true);
			//noinspection unchecked
			return (List<AbstractInsnNode>) insnsField.get(builder);
		} catch (NoSuchFieldException | IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}

	@Test
	public void insn() {
		InstructionBuilder builder = new InstructionBuilder(new ASMContext(new Mapping()));

		builder.insn(InsnOpcodes.IRETURN);

		List<AbstractInsnNode> result = getNodes(builder);
		assertEquals(1, result.size());
		assertEquals(Opcodes.IRETURN, result.get(0).getOpcode());
		assertTrue(result.get(0) instanceof InsnNode);
	}

	@Test
	public void label() {
		MethodEditor editor = new MethodEditor(null, null);
		InstructionBuilder builder = new InstructionBuilder(new ASMContext(new Mapping()));
		LabelRef lbl = editor.createLabel();

		builder.label(lbl);

		List<AbstractInsnNode> result = getNodes(builder);
		assertEquals(1, result.size());
		assertTrue(result.get(0) instanceof LabelNode);
	}

	@Test
	public void jumpInsn() {
		MethodEditor editor = new MethodEditor(null, null);
		InstructionBuilder builder = new InstructionBuilder(new ASMContext(new Mapping()));
		LabelRef lbl = editor.createLabel();

		builder.jumpInsn(JumpInsnOpcodes.IFEQ, lbl);

		List<AbstractInsnNode> result = getNodes(builder);
		assertEquals(1, result.size());
		assertTrue(result.get(0) instanceof JumpInsnNode);
	}

	@Test
	public void varInsn() {
		InstructionBuilder builder = new InstructionBuilder(new ASMContext(new Mapping()));

		builder.varInsn(VarInsnOpcodes.ALOAD, 3);

		List<AbstractInsnNode> result = getNodes(builder);
		assertEquals(1, result.size());
		assertEquals(Opcodes.ALOAD, result.get(0).getOpcode());
		assertTrue(result.get(0) instanceof VarInsnNode);
		assertEquals(3, ((VarInsnNode) result.get(0)).var);
	}

	@Test
	public void typeInsn() {
		InstructionBuilder builder = new InstructionBuilder(new ASMContext(new Mapping()));

		builder.typeInsn(TypeInsnOpcodes.INSTANCEOF, "LA;");

		List<AbstractInsnNode> result = getNodes(builder);
		assertEquals(1, result.size());
		assertEquals(Opcodes.INSTANCEOF, result.get(0).getOpcode());
		assertTrue(result.get(0) instanceof TypeInsnNode);
		assertEquals("LA;", ((TypeInsnNode) result.get(0)).desc);
	}

	@Test
	public void typeInsnUsesCtx() {
		final boolean[] ctxUsed = {false};
		InstructionBuilder builder = new InstructionBuilder(new ASMContext(new Mapping()) {
			@Override
			public String getTypeDesc(ObfLevel level, String origDesc) {
				ctxUsed[0] = true;
				return super.getTypeDesc(level, origDesc);
			}
		});

		builder.typeInsn(TypeInsnOpcodes.INSTANCEOF, "LA;");

		assertTrue(ctxUsed[0]);
	}

	@Test
	public void fieldInsn() {
		InstructionBuilder builder = new InstructionBuilder(new ASMContext(new Mapping()));

		builder.fieldInsn(FieldInsnOpcodes.PUTFIELD, "A", "A.a", "LA;");

		List<AbstractInsnNode> result = getNodes(builder);
		assertEquals(1, result.size());
		assertEquals(Opcodes.PUTFIELD, result.get(0).getOpcode());
		assertTrue(result.get(0) instanceof FieldInsnNode);
		assertEquals("A", ((FieldInsnNode) result.get(0)).owner);
		assertEquals("a", ((FieldInsnNode) result.get(0)).name);
		assertEquals("LA;", ((FieldInsnNode) result.get(0)).desc);
	}

	@Test
	public void fieldInsnUsesCtx() {
		final boolean[] ctxUsed = {false, false, false, false};
		InstructionBuilder builder = new InstructionBuilder(new ASMContext(new Mapping()) {
			@Override
			public String getCls(ObfLevel level, String origFld) {
				ctxUsed[0] = true;
				return super.getCls(level, origFld);
			}

			@Override
			public String getFld(ObfLevel level, String fldPath, String fldDesc) {
				ctxUsed[1] = true;
				return super.getFld(level, fldPath, fldDesc);
			}

			@Override
			public String getTypeDesc(ObfLevel level, String origDesc) {
				ctxUsed[2] = true;
				return super.getTypeDesc(level, origDesc);
			}
			@Override
			public String getDesc(ObfLevel level, String origDesc) {
				ctxUsed[3] = true;
				return super.getDesc(level, origDesc);
			}
		});

		builder.fieldInsn(FieldInsnOpcodes.PUTFIELD, "A", "A.a", "LA;");

		assertTrue(ctxUsed[0]);
		assertTrue(ctxUsed[1]);
		assertTrue(ctxUsed[2]);
		assertFalse(ctxUsed[3]);
	}

	@Test
	public void methodInsn() {
		InstructionBuilder builder = new InstructionBuilder(new ASMContext(new Mapping()));

		builder.methodInsn(MethodInsnOpcodes.INVOKEVIRTUAL, "A", "A.a", "()LA;");

		List<AbstractInsnNode> result = getNodes(builder);
		assertEquals(1, result.size());
		assertEquals(Opcodes.INVOKEVIRTUAL, result.get(0).getOpcode());
		assertTrue(result.get(0) instanceof MethodInsnNode);
		assertEquals("A", ((MethodInsnNode) result.get(0)).owner);
		assertEquals("a", ((MethodInsnNode) result.get(0)).name);
		assertEquals("()LA;", ((MethodInsnNode) result.get(0)).desc);
	}

	@Test
	public void methodInsnUsesCtx() {
		final boolean[] ctxUsed = {false, false, false};
		InstructionBuilder builder = new InstructionBuilder(new ASMContext(new Mapping()) {
			@Override
			public String getCls(ObfLevel level, String origFld) {
				ctxUsed[0] = true;
				return super.getCls(level, origFld);
			}

			@Override
			public String getMthd(ObfLevel level, String mthdPath, String mthdDesc) {
				ctxUsed[1] = true;
				return super.getMthd(level, mthdPath, mthdDesc);
			}

			@Override
			public String getDesc(ObfLevel level, String origDesc) {
				ctxUsed[2] = true;
				return super.getDesc(level, origDesc);
			}
		});

		builder.methodInsn(MethodInsnOpcodes.INVOKEVIRTUAL, "A", "A.a", "()LA;");

		assertTrue(ctxUsed[0]);
		assertTrue(ctxUsed[1]);
		assertTrue(ctxUsed[2]);
	}
}