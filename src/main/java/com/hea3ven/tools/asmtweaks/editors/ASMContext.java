package com.hea3ven.tools.asmtweaks.editors;

import java.util.HashMap;
import java.util.Map;

import com.hea3ven.tools.mappings.*;

public class ASMContext {

	private final Mapping mapping;
	private final ObfLevel obfuscated;
	private final Map<String, String> imports = new HashMap<>();

	public ASMContext() {
		this(null, ObfLevel.DEOBF);
	}

	public ASMContext(Mapping mapping) {
		this(mapping, ObfLevel.DEOBF);
	}

	public ASMContext(Mapping mapping, ObfLevel obfuscated) {
		this.mapping = mapping;
		this.obfuscated = obfuscated;
	}

	public ASMContext(ASMContext template, Mapping mapping) {
		this(template, mapping, ObfLevel.DEOBF);
	}

	public ASMContext(ASMContext template, Mapping mapping, ObfLevel obfuscated) {
		imports.putAll(template.imports);
		this.mapping = mapping;
		this.obfuscated = obfuscated;
	}

	public Mapping getMapping() {
		return mapping;
	}

	public void addImport(String clsName) {
		imports.put(clsName.substring(clsName.lastIndexOf('/') + 1), clsName);
	}

	public String getType(ObfLevel level, String origDesc) {
		origDesc = getExpandedImportsDesc(origDesc);
		TypeDesc typeDesc = mapping.parseTypeDesc(level, origDesc);
		String typeDescName = typeDesc.get(obfuscated);
		if (typeDesc instanceof ClsTypeDesc) {
			typeDescName = typeDescName.substring(1, typeDescName.length() - 1);
		}
		return typeDescName;
	}

	public String getTypeDesc(ObfLevel level, String origDesc) {
		origDesc = getExpandedImportsDesc(origDesc);
		return mapping.parseTypeDesc(level, origDesc).get(obfuscated);
	}

	public String getDesc(ObfLevel level, String origDesc) {
		origDesc = getExpandedImportsDesc(origDesc);
		return mapping.parseDesc(level, origDesc).get(obfuscated);
	}

	public String getCls(ObfLevel level, String origCls) {
		if (imports.containsKey(origCls))
			origCls = imports.get(origCls);

		ClsMapping cls = mapping.getCls(origCls, level);
		return (cls != null) ? cls.getPath(obfuscated) : origCls;
	}

	public String getFld(ObfLevel level, String fldPath, String fldDesc) {
		int split = fldPath.lastIndexOf('.');
		String clsName = fldPath.substring(0, split);
		String fldName = fldPath.substring(split + 1);

		if (imports.containsKey(clsName))
			fldPath = imports.get(clsName) + '.' + fldName;

		FldMapping fld = mapping.getFld(fldPath, level);
		return (fld != null) ? fld.getName(obfuscated) : fldName;
	}

	public String getMthd(ObfLevel level, String mthdPath, String mthdDesc) {
		int split = mthdPath.lastIndexOf('.');
		String clsName = mthdPath.substring(0, split);
		String mthdName = mthdPath.substring(split + 1);

		if (imports.containsKey(clsName))
			mthdPath = imports.get(clsName) + '.' + mthdName;

		MthdMapping fld = mapping.getMthd(mthdPath, getExpandedImportsDesc(mthdDesc), level);
		return (fld != null) ? fld.getName(obfuscated) : mthdName;
	}

	private String getExpandedImportsDesc(String desc) {
		int start = desc.indexOf('L');
		while (start != -1) {
			int end = desc.indexOf(';', start);
			String clsName = desc.substring(start + 1, end);

			if (imports.containsKey(clsName))
				clsName = imports.get(clsName);

			desc = desc.substring(0, start + 1) + clsName + desc.substring(end);
			end = start + clsName.length() + 1;
			start = desc.indexOf('L', end + 1);
		}
		return desc;
	}
}
