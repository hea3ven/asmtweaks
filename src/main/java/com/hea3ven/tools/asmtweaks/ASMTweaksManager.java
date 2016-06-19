package com.hea3ven.tools.asmtweaks;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.hea3ven.tools.asmtweaks.editors.ObfuscationMode;
import com.hea3ven.tools.mappings.Mapping;

public class ASMTweaksManager {

	private static Logger logger = LogManager.getLogger("asmtweaks.ASMTweaksManager");

	private final String currentVersion;
	private final ObfuscationMode obfuscationMode;

	private final boolean isClient;

	private ASMTweaksConfig config;

	private Mapping mapping;
	private boolean client;

	public ASMTweaksManager(String currentVersion, ObfuscationMode obfuscationMode, boolean isClient) {
		logger.info("using mappings for version {}", currentVersion);
		this.currentVersion = currentVersion;
		this.obfuscationMode = obfuscationMode;
		this.isClient = isClient;
		config = new ASMTweaksConfig();
	}

	public String getCurrentVersion() {
		return currentVersion;
	}

	public ObfuscationMode getObfuscationMode() {
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
