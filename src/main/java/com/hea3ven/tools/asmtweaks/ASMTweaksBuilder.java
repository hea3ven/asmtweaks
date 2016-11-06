package com.hea3ven.tools.asmtweaks;

import LZMA.LzmaInputStream;

import java.io.IOException;
import java.io.InputStream;
import java.util.regex.Pattern;

import com.google.common.base.Throwables;
import com.google.common.collect.ImmutableMap;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;

import net.minecraft.launchwrapper.Launch;

import com.hea3ven.tools.mappings.IdentityMapping;
import com.hea3ven.tools.mappings.Mapping;
import com.hea3ven.tools.mappings.ObfLevel;
import com.hea3ven.tools.mappings.parser.IMappingsParser;
import com.hea3ven.tools.mappings.parser.enigma.EnigmaMappingsParser;
import com.hea3ven.tools.mappings.parser.srg.SrgMappingsParser;

public class ASMTweaksBuilder {

	private static final Logger logger = LogManager.getLogger("asmtweaks.ASMTweaksBuilder");

	private static ASMTweaksManager mgr =
			new ASMTweaksManager(discoverVersion(), discoverObfuscation(), discoverIsClient());

	private static final Mapping mapping;

	private static String discoverVersion() {
		InputStream stream =
				Launch.classLoader.getResourceAsStream("net/minecraft/server/MinecraftServer.class");
		ClassNode serverClass = ASMUtils.readClass(stream);
		VersionScannerVisitor versionScanner = new VersionScannerVisitor();
		for (MethodNode method : serverClass.methods)
			method.accept(versionScanner);
		if (versionScanner.version == null)
			throw new RuntimeException("could not detect the running version");
		return versionScanner.version;
	}

	private static ObfLevel discoverObfuscation() {
		if (Launch.classLoader.getResourceAsStream("a.class") != null) {
			if (Launch.classLoader.getResourceAsStream("forge_logo.png") != null) {
				logger.debug("Detected a forge obfuscated environment");
				return ForgeObfLevel.SRG;
			} else {
				logger.debug("Detected an obfuscated environment");
				return ObfLevel.OBF;
			}
		} else {
			logger.debug("Detected a deobfuscated environment");
			return ObfLevel.DEOBF;
		}
	}

	private static boolean discoverIsClient() {
		try (InputStream stream =
				Launch.classLoader.getResourceAsStream("net/minecraft/client/ClientBrandRetriever.class")) {
			return stream != null;
		} catch (IOException e) {
			return false;
		}
	}

	static {
		InputStream mappingsStream = ASMTweaksBuilder.class.getResourceAsStream(
				"/deobfuscation_data-" + mgr.getCurrentVersion() + ".lzma");
		if (mappingsStream != null) {
			mapping = new IdentityMapping();
			SrgMappingsParser srgParser = new SrgMappingsParser(mapping, ObfLevel.OBF, ForgeObfLevel.SRG);
			try {
				srgParser.parse(new LzmaInputStream(mappingsStream));
			} catch (IOException e) {
				Throwables.propagate(e);
			}
			mgr.setMapping(mapping);
		} else {
			logger.warn(
					"No mapping found either you are in the development environment or something went wrong");
			mapping = new IdentityMapping();
			mgr.setMapping(mapping);
		}
	}

	public static ASMTweaksBuilder create() {
		return new ASMTweaksBuilder();
	}

	private final ASMTweaksRunner runner;

	private ASMTweaksBuilder() {
		runner = new ASMTweaksRunner(mgr);
	}

	public ASMTweaksBuilder loadMappings(String resourcePath) {
		IMappingsParser parser =
				resourcePath.endsWith(".srg") ? new SrgMappingsParser() : new EnigmaMappingsParser();
		parser.setMapping(mapping);

		if (resourcePath.contains("%s"))
			resourcePath = String.format(resourcePath, mgr.getCurrentVersion());

		InputStream mappingsStream = this.getClass().getResourceAsStream(resourcePath);
		if (mappingsStream != null) {
			try {
				parser.parse(mappingsStream);
			} catch (IOException e) {
				Throwables.propagate(e);
			}
		}
		return this;
	}

	public ASMTweaksBuilder addFldSrg(String src, String dst) {
		mapping.addFld(ImmutableMap.of(ForgeObfLevel.SRG, src, ObfLevel.DEOBF, dst), ObfLevel.DEOBF, "I");
		return this;
	}

	public ASMTweaksBuilder addMthdSrg(String src, String dst, String desc) {
		mapping.addMthd(ImmutableMap.of(ForgeObfLevel.SRG, src, ObfLevel.DEOBF, dst), ObfLevel.DEOBF, desc);
		return this;
	}

	public ASMTweaksBuilder addTweak(ASMTweak tweak) {
		runner.addTweak(tweak);
		return this;
	}

	public ASMTweaksRunner build() {
		return runner;
	}

	private static class VersionScannerVisitor extends MethodVisitor {

		public VersionScannerVisitor() {
			super(Opcodes.ASM4);
		}

		public String version = null;

		Pattern normalVer = Pattern.compile("^\\d+\\.\\d+(\\.\\d+)?$");
		Pattern snapVer = Pattern.compile("^\\d\\dw\\d+[a-z]$");

		@Override
		public void visitLdcInsn(Object cst) {
			if (cst instanceof String) {
				String potentialVersion = (String) cst;
				if (normalVer.matcher(potentialVersion).matches())
					setVersion(potentialVersion);
				else if (snapVer.matcher(potentialVersion).matches())
					setVersion(potentialVersion);
			}
			super.visitLdcInsn(cst);
		}

		private void setVersion(String potentialVersion) {
			if (version != null && !version.equals(potentialVersion))
				throw new RuntimeException("could not detect running version, multiple matches");
			version = potentialVersion;
		}
	}
}
