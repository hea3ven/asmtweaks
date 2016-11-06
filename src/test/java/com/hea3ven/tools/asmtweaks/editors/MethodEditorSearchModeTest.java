package com.hea3ven.tools.asmtweaks.editors;

import org.junit.Test;
import org.objectweb.asm.tree.MethodNode;

import com.hea3ven.tools.asmtweaks.editors.opcodes.InsnOpcodes;
import com.hea3ven.tools.asmtweaks.editors.opcodes.MethodInsnOpcodes;
import com.hea3ven.tools.asmtweaks.editors.opcodes.VarInsnOpcodes;
import static org.junit.Assert.assertEquals;

public class MethodEditorSearchModeTest extends MethodEditorBaseTest {
	@Test
	public void matchOneNode() {
		MethodNode mthdNode = new MethodNode();
		MethodEditor editor = createMethodEditor1(mthdNode);

		editor.setSearchMode();
		boolean result = editor.apply(editor.newInstructionBuilder().varInsn(VarInsnOpcodes.ALOAD, 0));

		assertEquals(true, result);
		assertEquals(1, editor.getCursor());
	}

	@Test
	public void matchMultipleNodes() {
		MethodNode mthdNode = new MethodNode();
		MethodEditor editor = createMethodEditor1(mthdNode);

		editor.setSearchMode();
		boolean result = editor.apply(editor.newInstructionBuilder()
				.varInsn(VarInsnOpcodes.ALOAD, 0)
				.methodInsn(MethodInsnOpcodes.INVOKEVIRTUAL, "A", "A.b", "()Z"));

		assertEquals(true, result);
		assertEquals(7, editor.getCursor());
	}

	@Test
	public void noMatch() {
		MethodNode mthdNode = new MethodNode();
		MethodEditor editor = createMethodEditor1(mthdNode);

		editor.setSearchMode();
		boolean result = editor.apply(editor.newInstructionBuilder().varInsn(VarInsnOpcodes.ALOAD, 1));

		assertEquals(false, result);
		assertEquals(0, editor.getCursor());
	}

	@Test
	public void noMatchMultipleNodesAtTheEnd() {
		MethodNode mthdNode = new MethodNode();
		MethodEditor editor = createMethodEditor1(mthdNode);

		editor.setSearchMode();
		boolean result = editor.apply(editor.newInstructionBuilder()
				.varInsn(VarInsnOpcodes.ALOAD, 0)
				.methodInsn(MethodInsnOpcodes.INVOKEVIRTUAL, "A", "A.b", "()Z")
				.insn(InsnOpcodes.DADD));

		assertEquals(false, result);
		assertEquals(0, editor.getCursor());
	}

	@Test
	public void searchMultipleTimes() {
		MethodNode mthdNode = new MethodNode();
		MethodEditor editor = createMethodEditor1(mthdNode);

		editor.setSearchMode();
		editor.apply(editor.newInstructionBuilder().varInsn(VarInsnOpcodes.ALOAD, 0));
		boolean result = editor.apply(editor.newInstructionBuilder().varInsn(VarInsnOpcodes.ALOAD, 0));

		assertEquals(true, result);
		assertEquals(7, editor.getCursor());
	}

	@Test
	public void searchMultipleTimesSecondNoMatch() {
		MethodNode mthdNode = new MethodNode();
		MethodEditor editor = createMethodEditor1(mthdNode);

		editor.setSearchMode();
		editor.apply(editor.newInstructionBuilder().insn(InsnOpcodes.LADD));
		boolean result = editor.apply(editor.newInstructionBuilder().varInsn(VarInsnOpcodes.ALOAD, 0));

		assertEquals(true, result);
		assertEquals(7, editor.getCursor());
	}
}