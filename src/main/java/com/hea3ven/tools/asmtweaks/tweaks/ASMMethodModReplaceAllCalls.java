package com.hea3ven.tools.asmtweaks.tweaks;

import org.objectweb.asm.Opcodes;

import com.hea3ven.tools.asmtweaks.editors.MethodEditor;

public class ASMMethodModReplaceAllCalls extends ASMMethodModEditCode {
	private final String mthdSrc;
	private final String mthdDst;
	private final String desc;
	private final Boolean obfuscated;

	public ASMMethodModReplaceAllCalls(String mthdName, String mthdDesc, String mthdSrc, String mthdDst,
			String desc) {
		this(mthdName, mthdDesc, mthdSrc, mthdDst, desc, null);
	}

	public ASMMethodModReplaceAllCalls(String mthdName, String mthdDesc, String mthdSrc, String mthdDst,
			String desc, Boolean obfuscated) {
		super(mthdName, mthdDesc);
		this.mthdSrc = mthdSrc;
		this.mthdDst = mthdDst;
		this.desc = desc;
		this.obfuscated = obfuscated;
	}

	@Override
	public void handle(MethodEditor editor) {
		editor.setObfuscation(obfuscated);
		editor.setSearchMode();
		editor.methodInsn(Opcodes.INVOKEVIRTUAL, mthdSrc.substring(0, mthdSrc.lastIndexOf('/')), mthdSrc,
				desc);
		editor.setRemoveMode();
		editor.methodInsn(Opcodes.INVOKEVIRTUAL, mthdSrc.substring(0, mthdSrc.lastIndexOf('/')), mthdSrc,
				desc);
		editor.setInsertMode();
		editor.Seek(-1);
		editor.methodInsn(Opcodes.INVOKEVIRTUAL, mthdDst.substring(0, mthdDst.lastIndexOf('/')), mthdDst,
				desc);
	}
}
