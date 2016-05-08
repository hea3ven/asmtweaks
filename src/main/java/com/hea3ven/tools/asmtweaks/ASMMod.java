package com.hea3ven.tools.asmtweaks;

public interface ASMMod {

	String getClassName();

	default boolean isClientSideOnly() {
		return false;
	}
}
