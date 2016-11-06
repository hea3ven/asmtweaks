package com.hea3ven.tools.asmtweaks.editors;

import org.junit.Test;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

import com.hea3ven.tools.asmtweaks.editors.opcodes.MethodInsnOpcodes;
import com.hea3ven.tools.asmtweaks.editors.opcodes.VarInsnOpcodes;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class MethodEditorInsertModeTest extends MethodEditorBaseTest {
	@Test
	public void insertOneNode() {
		MethodNode mthdNode = new MethodNode();
		MethodEditor editor = createMethodEditor1(mthdNode);

		editor.setInsertMode();
		boolean result = editor.apply(editor.newInstructionBuilder().varInsn(VarInsnOpcodes.ALOAD, 0));

		assertEquals(true, result);
		assertEquals(1, editor.getCursor());
		assertEquals(10, getMethodNode(editor).instructions.size());
		assertTrue(getMethodNode(editor).instructions.get(0) instanceof VarInsnNode);
		assertTrue(getMethodNode(editor).instructions.get(1) instanceof LabelNode);
	}

	@Test
	public void insertMultipleNodes() {
		MethodNode mthdNode = new MethodNode();
		MethodEditor editor = createMethodEditor1(mthdNode);

		editor.setInsertMode();
		boolean result = editor.apply(editor.newInstructionBuilder()
				.varInsn(VarInsnOpcodes.ALOAD, 0)
				.methodInsn(MethodInsnOpcodes.INVOKEVIRTUAL, "A", "A.a", "()J"));

		assertEquals(true, result);
		assertEquals(2, editor.getCursor());
		assertEquals(11, getMethodNode(editor).instructions.size());
		assertTrue(getMethodNode(editor).instructions.get(0) instanceof VarInsnNode);
		assertTrue(getMethodNode(editor).instructions.get(1) instanceof MethodInsnNode);
		assertTrue(getMethodNode(editor).instructions.get(2) instanceof LabelNode);
	}
	@Test
	public void insertMultipleNodesInEmptyMethod() {
		MethodNode mthdNode = new MethodNode();
		MethodEditor editor = createMethodEditor(mthdNode);

		editor.setInsertMode();
		boolean result = editor.apply(editor.newInstructionBuilder()
				.varInsn(VarInsnOpcodes.ALOAD, 0)
				.methodInsn(MethodInsnOpcodes.INVOKEVIRTUAL, "A", "A.a", "()J"));

		assertEquals(true, result);
		assertEquals(2, editor.getCursor());
		assertEquals(2, getMethodNode(editor).instructions.size());
		assertTrue(getMethodNode(editor).instructions.get(0) instanceof VarInsnNode);
		assertTrue(getMethodNode(editor).instructions.get(1) instanceof MethodInsnNode);
	}
}
