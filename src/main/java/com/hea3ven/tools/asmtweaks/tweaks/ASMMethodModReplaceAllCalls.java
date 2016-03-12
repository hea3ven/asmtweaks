package com.hea3ven.tools.asmtweaks.tweaks;

import org.objectweb.asm.Opcodes;

import com.hea3ven.tools.asmtweaks.editors.MethodEditor;
import com.hea3ven.tools.asmtweaks.editors.MethodEditorException;
import com.hea3ven.tools.asmtweaks.editors.ObfuscationMode;

public class ASMMethodModReplaceAllCalls extends ASMMethodModEditCode {
	private final String mthdSrc;
	private final String mthdDst;
	private final String desc;
	private final ObfuscationMode obfuscated;

	public ASMMethodModReplaceAllCalls(String mthdName, String mthdDesc, String mthdSrc, String mthdDst,
			String desc) {
		this(mthdName, mthdDesc, mthdSrc, mthdDst, desc, null);
	}

	public ASMMethodModReplaceAllCalls(String mthdName, String mthdDesc, String mthdSrc, String mthdDst,
			String desc, ObfuscationMode obfuscated) {
		super(mthdName, mthdDesc);
		this.mthdSrc = mthdSrc;
		this.mthdDst = mthdDst;
		this.desc = desc;
		this.obfuscated = obfuscated;
	}

	@Override
	public void handle(MethodEditor editor) {
		editor.setObfuscation(obfuscated);
		while (true) {
			editor.setSearchMode();
			try {
				editor.methodInsn(Opcodes.INVOKEVIRTUAL, mthdSrc.substring(0, mthdSrc.lastIndexOf('/')),
						mthdSrc,
						desc);
			} catch (MethodEditorException e) {
				break;
			}
			editor.setRemoveMode();
			editor.methodInsn(Opcodes.INVOKEVIRTUAL, mthdSrc.substring(0, mthdSrc.lastIndexOf('/')), mthdSrc,
					desc);
			editor.setInsertMode();
			editor.Seek(-1);
			editor.methodInsn(Opcodes.INVOKEVIRTUAL, mthdDst.substring(0, mthdDst.lastIndexOf('/')), mthdDst,
					desc);
		}
	}
}
