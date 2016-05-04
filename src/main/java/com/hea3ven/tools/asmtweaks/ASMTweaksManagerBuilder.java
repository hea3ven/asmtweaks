package com.hea3ven.tools.asmtweaks;

import LZMA.LzmaInputStream;

import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.regex.Pattern;

import com.google.common.base.Throwables;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;

import net.minecraft.launchwrapper.Launch;

import com.hea3ven.tools.mappings.*;
import com.hea3ven.tools.mappings.parser.enigma.EnigmaMappingsParser;
import com.hea3ven.tools.mappings.parser.srg.SrgMappingsParser;

public class ASMTweaksManagerBuilder {

	private static final Logger logger = LogManager.getLogger("asmtweaks.ASMTweaksBuilder");

	private ASMTweaksManager mgr;
	private Mapping srgMappings = new Mapping();

	private static String discoverVersion() {
		InputStream stream =
				Launch.classLoader.getResourceAsStream("net/minecraft/server/MinecraftServer.class");
		ClassNode serverClass = ASMUtils.readClass(stream);
		VersionScannerVisitor versionScanner = new VersionScannerVisitor();
		Iterator<MethodNode> methodIt = serverClass.methods.iterator();
		while (methodIt.hasNext()) {
			MethodNode method = methodIt.next();
			method.accept(versionScanner);
		}
		if (versionScanner.version == null)
			throw new RuntimeException("could not detect the running version");
		return versionScanner.version;
	}

	public ASMTweaksManagerBuilder() {
		this.mgr = new ASMTweaksManager(discoverVersion());
	}

	public ASMTweaksManagerBuilder loadMappings(String string) {
		EnigmaMappingsParser parser = new EnigmaMappingsParser();
		InputStream mappingsStream =
				this.getClass().getResourceAsStream(string + "/" + mgr.getCurrentVersion() + ".mappings");
		if (mappingsStream != null) {
			try {
				Mapping mapping = parser.add(mappingsStream);
				mgr.setMapping(new WrapperMapping(mapping));
			} catch (IOException e) {
				Throwables.propagate(e);
			}
		} else {
			mappingsStream = this.getClass()
					.getResourceAsStream("/deobfuscation_data-" + mgr.getCurrentVersion() + ".lzma");
			if (mappingsStream != null) {
				SrgMappingsParser srgParser = new SrgMappingsParser();
				try {
					Mapping mapping = srgParser.add(new LzmaInputStream(mappingsStream));
					mgr.setMapping(new WrapperMapping(mapping));
				} catch (IOException e) {
					Throwables.propagate(e);
				}
			} else {
				logger.warn(
						"No mappings found either you are in the development environment or something went wrong");
				mgr.setMapping(new IdentityMapping());
			}
		}
		return this;
	}

	public ASMTweaksManagerBuilder addFldSrg(String src, String dst) {
		srgMappings.addFld(src, dst);
		return this;
	}

	public ASMTweaksManagerBuilder addMthdSrg(String src, String dst, String desc) {
		srgMappings.addMthd(src, dst, desc);
		return this;
	}

	public ASMTweaksManagerBuilder addTweak(ASMTweak tweak) {
		mgr.addTweak(tweak);
		return this;
	}

	public ASMTweaksManager build() {
		if (srgMappings.getAll().size() > 0)
			mgr.setSrgMapping(srgMappings);
		return mgr;
	}

	public static class VersionScannerVisitor extends MethodVisitor {

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

	class WrapperMapping extends Mapping {
		private final Mapping other;

		public WrapperMapping(Mapping other) {
			this.other = other;
		}

		@Override
		public ClsMapping getCls(String name) {
			ClsMapping elem = other.getCls(name);
			if (elem == null) {
				elem = other.addCls(name, name);
			}
			if (elem.getDstPath() == null)
				elem = other.addCls(elem.getSrcPath(), elem.getSrcPath());
			return elem;
		}

		@Override
		public MthdMapping getMthd(String name, String desc) {
			MthdMapping elem = other.getMthd(name, desc);
			if (elem == null)
				elem = other.addMthd(name, name, desc);
			return elem;
		}

		@Override
		public FldMapping getFld(String name) {
			FldMapping elem = other.getFld(name);
			if (elem == null)
				elem = other.addFld(name, name);
			return elem;
		}
	}
}
