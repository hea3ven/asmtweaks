package com.hea3ven.tools.asmtweaks.editors;

import org.junit.Test;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

import com.hea3ven.tools.asmtweaks.editors.opcodes.MethodInsnOpcodes;
import com.hea3ven.tools.asmtweaks.editors.opcodes.VarInsnOpcodes;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class MethodEditorRemoveModeTest extends MethodEditorBaseTest {
	@Test
	public void removeOneNode() {
		MethodNode mthdNode = new MethodNode();
		MethodEditor editor = createMethodEditor1(mthdNode);

		editor.Seek(1);
		editor.setRemoveMode();
		boolean result = editor.apply(editor.newInstructionBuilder().varInsn(VarInsnOpcodes.ALOAD, 0));

		assertEquals(true, result);
		assertEquals(1, editor.getCursor());
		assertEquals(8, getMethodNode(editor).instructions.size());
		assertTrue(getMethodNode(editor).instructions.get(1) instanceof MethodInsnNode);
	}

	@Test
	public void removeMultipleNodes() {
		MethodNode mthdNode = new MethodNode();
		MethodEditor editor = createMethodEditor1(mthdNode);

		editor.Seek(1);
		editor.setRemoveMode();
		boolean result = editor.apply(editor.newInstructionBuilder()
				.varInsn(VarInsnOpcodes.ALOAD, 0)
				.methodInsn(MethodInsnOpcodes.INVOKEVIRTUAL, "A", "A.a", "()J"));

		assertEquals(true, result);
		assertEquals(1, editor.getCursor());
		assertEquals(7, getMethodNode(editor).instructions.size());
		assertTrue(getMethodNode(editor).instructions.get(1) instanceof InsnNode);
		assertEquals(Opcodes.LCONST_1, getMethodNode(editor).instructions.get(1).getOpcode());
	}

	@Test
	public void removeNonExistentNode() {
		MethodNode mthdNode = new MethodNode();
		MethodEditor editor = createMethodEditor1(mthdNode);

		editor.Seek(1);
		editor.setRemoveMode();
		boolean result = editor.apply(editor.newInstructionBuilder().varInsn(VarInsnOpcodes.DLOAD, 0));

		assertEquals(false, result);
		assertEquals(1, editor.getCursor());
		assertEquals(9, getMethodNode(editor).instructions.size());
		assertTrue(getMethodNode(editor).instructions.get(1) instanceof VarInsnNode);
	}

	@Test
	public void removeMultipleSecondNonExistentNode() {
		MethodNode mthdNode = new MethodNode();
		MethodEditor editor = createMethodEditor1(mthdNode);

		editor.Seek(1);
		editor.setRemoveMode();
		boolean result = editor.apply(editor.newInstructionBuilder()
				.varInsn(VarInsnOpcodes.ALOAD, 0)
				.varInsn(VarInsnOpcodes.ALOAD, 0));

		assertEquals(false, result);
		assertEquals(1, editor.getCursor());
		assertEquals(8, getMethodNode(editor).instructions.size());
		assertTrue(getMethodNode(editor).instructions.get(1) instanceof MethodInsnNode);
	}
}
