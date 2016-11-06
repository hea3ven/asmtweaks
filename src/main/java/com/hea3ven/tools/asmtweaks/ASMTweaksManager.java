package com.hea3ven.tools.asmtweaks;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.hea3ven.tools.mappings.Mapping;
import com.hea3ven.tools.mappings.ObfLevel;

public class ASMTweaksManager {

	private static Logger logger = LogManager.getLogger("asmtweaks.ASMTweaksManager");

	private final String currentVersion;
	private final ObfLevel obfuscationMode;

	private ASMTweaksConfig config;

	private Mapping mapping;
	private final boolean client;

	public ASMTweaksManager(String currentVersion, ObfLevel obfuscationMode, boolean client) {
		logger.info("using mappings for version {}", currentVersion);
		this.currentVersion = currentVersion;
		this.obfuscationMode = obfuscationMode;
		this.client = client;
		config = new ASMTweaksConfig();
	}

	public String getCurrentVersion() {
		return currentVersion;
	}

	public ObfLevel getObfuscationMode() {
		return obfuscationMode;
	}

	public ASMTweaksConfig getConfig() {
		return config;
	}

	public void setMapping(Mapping mapping) {
		this.mapping = mapping;
	}

	public boolean isClient() {
		return client;
	}

	public Mapping getMapping() {
		return mapping;
	}

	public void error(String msg) {
		logger.error(msg);
		throw new RuntimeException("failed patching a class");
	}
}
