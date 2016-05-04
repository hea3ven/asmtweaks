package com.hea3ven.tools.asmtweaks.mapping;

import com.hea3ven.tools.mappings.*;

public class ForgeMapping extends IdentityMapping {

	private final Mapping mcpMapping = new Mapping();

	public Mapping getSrgMapping() {
		return mcpMapping;
	}

	@Override
	public ClsMapping getCls(String name) {
		ClsMapping clsMap = mcpMapping.getCls(name);
		if (clsMap != null && clsMap.getDstPath() != null)
			name = clsMap.getDstPath();
		return super.getCls(name);
	}

	@Override
	public MthdMapping getMthd(String name, String desc) {
		MthdMapping mthdMap = mcpMapping.getMthd(name, desc);
		if (mthdMap != null) {
			name = mthdMap.getDstPath();
			desc = mthdMap.getDesc().getDst();
		}
		return super.getMthd(name, desc);
	}

	@Override
	public FldMapping getFld(String name) {
		FldMapping fldMap = mcpMapping.getFld(name);
		if (fldMap != null)
			name = fldMap.getDstPath();
		return super.getFld(name);
	}
}
