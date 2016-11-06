package com.hea3ven.tools.asmtweaks.tweaks;

import com.hea3ven.tools.asmtweaks.editors.InstructionBuilder;
import com.hea3ven.tools.asmtweaks.editors.MethodEditor;
import com.hea3ven.tools.asmtweaks.editors.opcodes.MethodInsnOpcodes;
import com.hea3ven.tools.mappings.ObfLevel;

public class ASMMethodModReplaceAllCalls extends ASMMethodModEditCode {
	private final String mthdSrc;
	private final String mthdDst;
	private final String desc;
	private final ObfLevel obfuscated;

	public ASMMethodModReplaceAllCalls(String mthdName, String mthdDesc, String mthdSrc, String mthdDst,
			String desc) {
		this(mthdName, mthdDesc, mthdSrc, mthdDst, desc, null);
	}

	public ASMMethodModReplaceAllCalls(String mthdName, String mthdDesc, String mthdSrc, String mthdDst,
			String desc, ObfLevel obfuscated) {
		super(mthdName, mthdDesc);
		this.mthdSrc = mthdSrc;
		this.mthdDst = mthdDst;
		this.desc = desc;
		this.obfuscated = obfuscated;
	}

	@Override
	public void handle(MethodEditor editor) {
		editor.setLevel(obfuscated);

		InstructionBuilder target = editor.newInstructionBuilder()
				.methodInsn(MethodInsnOpcodes.INVOKEVIRTUAL, mthdSrc.substring(0, mthdSrc.lastIndexOf('.')),
						mthdSrc, desc);
		InstructionBuilder replacement = editor.newInstructionBuilder()
				.methodInsn(MethodInsnOpcodes.INVOKEVIRTUAL, mthdDst.substring(0, mthdDst.lastIndexOf('.')),
						mthdDst, desc);

		while (true) {
			editor.setSearchMode();
			if (!editor.apply(target))
				return;
			editor.setRemoveMode();
			editor.apply(target);
			editor.setInsertMode();
			editor.apply(replacement);
		}
	}
}
