package com.hea3ven.tools.asmtweaks.editors;

import org.junit.Test;

import com.hea3ven.tools.mappings.Mapping;
import com.hea3ven.tools.mappings.ObfLevel;
import static org.junit.Assert.assertEquals;

public class ASMContextTest {
	@Test
	public void getDescBuiltin() {
		ASMContext ctx = new ASMContext(new Mapping());

		String result = ctx.getTypeDesc(ObfLevel.OBF, "Z");

		assertEquals("Z", result);
	}

	@Test
	public void getDescMappedClsObfuscated() {
		ASMContext ctx = new ASMContext(new Mapping());

		ctx.getMapping().addCls("Test", "Tset");
		String result = ctx.getTypeDesc(ObfLevel.OBF, "LTest;");

		assertEquals("LTset;", result);
	}

	@Test
	public void getDescMappedClsDeobfuscated() {
		ASMContext ctx = new ASMContext(new Mapping());

		ctx.getMapping().addCls("Test", "Tset");
		String result = ctx.getTypeDesc(ObfLevel.DEOBF, "LTset;");

		assertEquals("LTset;", result);
	}

	@Test
	public void getDescObfuscatedMappedClsObfuscated() {
		ASMContext ctx = new ASMContext(new Mapping(), ObfLevel.OBF);

		ctx.getMapping().addCls("Test", "Tset");
		String result = ctx.getTypeDesc(ObfLevel.OBF, "LTest;");

		assertEquals("LTest;", result);
	}

	@Test
	public void getDescObfuscatedMappedClsDeobfuscated() {
		ASMContext ctx = new ASMContext(new Mapping(), ObfLevel.OBF);

		ctx.getMapping().addCls("Test", "Tset");
		String result = ctx.getTypeDesc(ObfLevel.DEOBF, "LTset;");

		assertEquals("LTest;", result);
	}

	@Test
	public void getDescImport() {
		ASMContext ctx = new ASMContext(new Mapping(), ObfLevel.OBF);

		ctx.addImport("com/test/Test");
		String result = ctx.getTypeDesc(ObfLevel.OBF, "LTest;");

		assertEquals("Lcom/test/Test;", result);
	}

	@Test
	public void getClsMappedClsObfuscated() {
		ASMContext ctx = new ASMContext(new Mapping());

		ctx.getMapping().addCls("Test", "Tset");
		String result = ctx.getCls(ObfLevel.OBF, "Test");

		assertEquals("Tset", result);
	}

	@Test
	public void getClsMappedClsDeobfuscated() {
		ASMContext ctx = new ASMContext(new Mapping());

		ctx.getMapping().addCls("Test", "Tset");
		String result = ctx.getCls(ObfLevel.DEOBF, "Tset");

		assertEquals("Tset", result);
	}

	@Test
	public void getClsObfuscatedMappedClsObfuscated() {
		ASMContext ctx = new ASMContext(new Mapping(), ObfLevel.OBF);

		ctx.getMapping().addCls("Test", "Tset");
		String result = ctx.getCls(ObfLevel.OBF, "Test");

		assertEquals("Test", result);
	}

	@Test
	public void getClsObfuscatedMappedClsDeobfuscated() {
		ASMContext ctx = new ASMContext(new Mapping(), ObfLevel.OBF);

		ctx.getMapping().addCls("Test", "Tset");
		String result = ctx.getCls(ObfLevel.DEOBF, "Tset");

		assertEquals("Test", result);
	}

	@Test
	public void getClsImport() {
		ASMContext ctx = new ASMContext(new Mapping(), ObfLevel.OBF);

		ctx.addImport("com/test/Test");
		String result = ctx.getCls(ObfLevel.OBF, "Test");

		assertEquals("com/test/Test", result);
	}

	@Test
	public void getFldMappedClsObfuscated() {
		ASMContext ctx = new ASMContext(new Mapping());

		ctx.getMapping().addFld("Test.a", "Tset.b", ObfLevel.OBF, "Z");
		String result = ctx.getFld(ObfLevel.OBF, "Test.a", "Z");

		assertEquals("b", result);
	}

	@Test
	public void getFldMappedClsDeobfuscated() {
		ASMContext ctx = new ASMContext(new Mapping());

		ctx.getMapping().addFld("Test.a", "Tset.b", ObfLevel.OBF, "Z");
		String result = ctx.getFld(ObfLevel.DEOBF, "Tset.b", "Z");

		assertEquals("b", result);
	}

	@Test
	public void getFldObfuscatedMappedClsObfuscated() {
		ASMContext ctx = new ASMContext(new Mapping(), ObfLevel.OBF);

		ctx.getMapping().addFld("Test.a", "Tset.b", ObfLevel.OBF, "Z");
		String result = ctx.getFld(ObfLevel.OBF, "Test.a", "Z");

		assertEquals("a", result);
	}

	@Test
	public void getFldObfuscatedMappedClsDeobfuscated() {
		ASMContext ctx = new ASMContext(new Mapping(), ObfLevel.OBF);

		ctx.getMapping().addFld("Test.a", "Tset.b", ObfLevel.OBF, "Z");
		String result = ctx.getFld(ObfLevel.DEOBF, "Tset.b", "Z");

		assertEquals("a", result);
	}

	@Test
	public void getFldImport() {
		ASMContext ctx = new ASMContext(new Mapping());

		ctx.addImport("com/test/Test");
		String result = ctx.getFld(ObfLevel.OBF, "Test.a", "Z");

		assertEquals("a", result);
	}

	@Test
	public void getFldImportMapped() {
		ASMContext ctx = new ASMContext(new Mapping());

		ctx.addImport("com/test/Test");
		ctx.getMapping().addFld("com/test/Test.a", "com/test/Test.b", ObfLevel.OBF, "Z");
		String result = ctx.getFld(ObfLevel.OBF, "Test.a", "Z");

		assertEquals("b", result);
	}

	@Test
	public void getMthdMappedClsObfuscated() {
		ASMContext ctx = new ASMContext(new Mapping());

		ctx.getMapping().addMthd("Test.a", "Tset.b", ObfLevel.OBF, "()Z");
		String result = ctx.getMthd(ObfLevel.OBF, "Test.a", "()Z");

		assertEquals("b", result);
	}

	@Test
	public void getMthdMappedClsDeobfuscated() {
		ASMContext ctx = new ASMContext(new Mapping());

		ctx.getMapping().addMthd("Test.a", "Tset.b", ObfLevel.OBF, "()Z");
		String result = ctx.getMthd(ObfLevel.DEOBF, "Tset.b", "()Z");

		assertEquals("b", result);
	}

	@Test
	public void getMthdObfuscatedMappedClsObfuscated() {
		ASMContext ctx = new ASMContext(new Mapping(), ObfLevel.OBF);

		ctx.getMapping().addMthd("Test.a", "Tset.b", ObfLevel.OBF, "()Z");
		String result = ctx.getMthd(ObfLevel.OBF, "Test.a", "()Z");

		assertEquals("a", result);
	}

	@Test
	public void getMthdObfuscatedMappedClsDeobfuscated() {
		ASMContext ctx = new ASMContext(new Mapping(), ObfLevel.OBF);

		ctx.getMapping().addMthd("Test.a", "Tset.b", ObfLevel.OBF, "()Z");
		String result = ctx.getMthd(ObfLevel.DEOBF, "Tset.b", "()Z");

		assertEquals("a", result);
	}

	@Test
	public void getMthdImport() {
		ASMContext ctx = new ASMContext(new Mapping());

		ctx.addImport("com/test/Test");
		String result = ctx.getMthd(ObfLevel.OBF, "Test.a", "()Z");

		assertEquals("a", result);
	}

	@Test
	public void getMthdImportMapped() {
		ASMContext ctx = new ASMContext(new Mapping());

		ctx.addImport("com/test/Test");
		ctx.getMapping().addMthd("com/test/Test.a", "com/test/Test.b", ObfLevel.OBF, "()Z");
		String result = ctx.getMthd(ObfLevel.OBF, "Test.a", "()Z");

		assertEquals("b", result);
	}

	@Test
	public void copyConstructorCopiesImports() {
		ASMContext tpl = new ASMContext();
		tpl.addImport("com/test/Test");

		ASMContext ctx = new ASMContext(tpl, new Mapping());

		String result = ctx.getCls(ObfLevel.OBF, "Test");

		assertEquals("com/test/Test", result);
	}
}