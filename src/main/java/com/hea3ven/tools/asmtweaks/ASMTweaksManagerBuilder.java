package com.hea3ven.tools.asmtweaks;

import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.regex.Pattern;

import com.google.common.base.Throwables;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;

import net.minecraft.launchwrapper.Launch;

import com.hea3ven.tools.mappings.Mapping;
import com.hea3ven.tools.mappings.parser.enigma.EnigmaMappingsParser;

public class ASMTweaksManagerBuilder {

	private ASMTweaksManager mgr;

	private static String discoverVersion() {
		InputStream stream = Launch.classLoader
				.getResourceAsStream("net/minecraft/server/MinecraftServer.class");
		ClassNode serverClass = ASMUtils.readClass(stream);
		VersionScannerVisitor versionScanner = new VersionScannerVisitor();
		Iterator<MethodNode> methodIter = serverClass.methods.iterator();
		while (methodIter.hasNext()) {
			MethodNode method = methodIter.next();
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
		Mapping mapping = null;
		try {
			mapping = parser.add(this.getClass().getResourceAsStream(
					string + "/" + mgr.getCurrentVersion() + ".mappings"));
		} catch (IOException e) {
			Throwables.propagate(e);
		}
		mgr.setMapping(mapping);
		return this;
	}

	public ASMTweaksManagerBuilder addTweak(ASMTweak tweak) {
		mgr.addTweak(tweak);
		return this;
	}

	public ASMTweaksManager build() {
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
}
