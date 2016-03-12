package com.hea3ven.tools.asmtweaks.editors;

import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

import com.hea3ven.tools.asmtweaks.ASMTweaksManager;
import com.hea3ven.tools.asmtweaks.ASMUtils;
import com.hea3ven.tools.mappings.ClsMapping;
import com.hea3ven.tools.mappings.ElementMapping;
import com.hea3ven.tools.mappings.FldMapping;
import com.hea3ven.tools.mappings.MthdMapping;

public class MethodEditor {
	private static final Logger logger = LogManager.getLogger("asmtweaks.MethodEditor");

	private final ASMTweaksManager mgr;
	private final MethodNode mthdNode;

	private int cursor = 0;

	private Map<String, ClsMapping> imports = new HashMap<>();
	private Mode mode;
	private ObfuscationMode obfuscation;

	public MethodEditor(ASMTweaksManager mgr, MethodNode mthdNode) {
		this.mgr = mgr;
		this.mthdNode = mthdNode;
		obfuscation = null;
	}

	public void addImport(String clsName) {
		ClsMapping cls = mgr.getClass(clsName);
		imports.put(cls.getDstName(), cls);
	}

	public void setObfuscation(ObfuscationMode obfuscation) {
		this.obfuscation = obfuscation;
	}

	private boolean getActualObfuscation() {
		return obfuscation != null ? obfuscation == ObfuscationMode.OBFUSCATED : mgr.isObfuscated();
	}

	public void Seek(int count) {
		cursor += count;
	}

	public <T extends AbstractInsnNode> T Get() {
		return (T) mthdNode.instructions.get(cursor);
	}

	public void setSearchMode() {
		mode = new SearchMode();
	}

	public void setRemoveMode() {
		mode = new RemoveMode();
	}

	public void setInsertMode() {
		mode = new InsertMode();
	}

	public MethodEditor methodInsn(int opcode, String owner, String name, String desc) {
		MthdMapping mthd = getMethod(name, desc);
		ClsMapping cls = getClass(owner);
		mode.apply(new MethodInsnNode(opcode,
				cls.getPath((obfuscation != ObfuscationMode.SRG) ? getActualObfuscation() : true),
				mthd.getName(getActualObfuscation()), mthd.getDesc().get(getActualObfuscation()),
				opcode == Opcodes.INVOKEINTERFACE));
		return this;
	}

	public MethodEditor varInsn(int opcode, int var) {
		mode.apply(new VarInsnNode(opcode, var));
		return this;
	}

	public MethodEditor fieldInsn(int opcode, String owner, String name, String desc) {
		FldMapping fld = getField(name, desc);
		mode.apply(new FieldInsnNode(opcode, getClass(owner).getPath(
				(obfuscation != ObfuscationMode.SRG) ? getActualObfuscation() : true),
				fld.getName(getActualObfuscation()), getDesc(desc)));
		return this;
	}

	public MethodEditor jumpInsn(int opcode, LabelNode label) {
		mode.apply(new JumpInsnNode(opcode, label));
		return this;
	}

	public void typeInsn(int opcode, String desc) {
		mode.apply(new TypeInsnNode(opcode, getClass(desc).getPath(getActualObfuscation())));
	}

	public void labelInsn(LabelNode node) {
		mode.apply(node);
	}

	public void insn(int opcode) {
		mode.apply(new InsnNode(opcode));
	}

	private ClsMapping getClass(String owner) {
		if (imports.containsKey(owner))
			return imports.get(owner);
		return mgr.getClass(owner);
	}

	private MthdMapping getMethod(String name, String desc) {
		int nameDiv = name.lastIndexOf('/');
		String clsName = name.substring(0, nameDiv);
		if (imports.containsKey(clsName)) {
			name = imports.get(clsName).getDstPath() + "/" + name.substring(nameDiv + 1);
		}
		return mgr.getMethod(name, getExpandDesc(desc));
	}

	private FldMapping getField(String name, String desc) {
		int nameDiv = name.lastIndexOf('/');
		String clsName = name.substring(0, nameDiv);
		if (imports.containsKey(clsName)) {
			name = imports.get(clsName).getDstPath() + "/" + name.substring(nameDiv + 1);
		}
		return mgr.getField(name);
	}

	private String getExpandDesc(String desc) {
		int start = desc.indexOf('L');
		while (start != -1) {
			int end = desc.indexOf(';', start);
			String clsName = desc.substring(start + 1, end);
			if (imports.containsKey(clsName)) {
				desc = desc.substring(0, start + 1) + imports.get(clsName).getDstPath() + desc.substring(end);
				end = start + imports.get(clsName).getDstPath().length() + 1;
			}
			start = desc.indexOf('L', end + 1);
		}
		return desc;
	}

	private String getDesc(String desc) {
		int start = desc.indexOf('L');
		while (start != -1) {
			int end = desc.indexOf(';', start);
			String clsName = desc.substring(start + 1, end);
			if (imports.containsKey(clsName)) {
				desc = desc.substring(0, start + 1) + imports.get(clsName).getPath(getActualObfuscation()) +
						desc.substring(end);
				end = start + imports.get(clsName).getPath(getActualObfuscation()).length() + 1;
			}
			start = desc.indexOf('L', end + 1);
		}
		return desc;
	}

	public ASMTweaksManager getManager() {
		return mgr;
	}

	private interface Mode {
		void apply(AbstractInsnNode node);
	}

	private class SearchMode implements Mode {
		@Override
		public void apply(AbstractInsnNode node) {
			boolean found = false;
			for (int pos = cursor; pos < mthdNode.instructions.size(); pos++) {
				AbstractInsnNode posNode = mthdNode.instructions.get(pos);
//				logger.info("Searching at {}", ASMUtils.nodeToString(posNode));
				if (ASMUtils.areNodesEqual(node, posNode)) {
					cursor = pos;
					found = true;
					break;
				}
			}
			if (!found) {
				logger.error("Could not find node ({})", ASMUtils.nodeToString(node));
				throw new MethodEditorException("Could not find node");
			}
		}
	}

	private class RemoveMode implements Mode {

		@Override
		public void apply(AbstractInsnNode node) {
			AbstractInsnNode cursorNode = mthdNode.instructions.get(cursor);
			if (!ASMUtils.areNodesEqual(node, cursorNode)) {
				logger.error("Expected node does not match ({}, {})", ASMUtils.nodeToString(node),
						ASMUtils.nodeToString(cursorNode));
				throw new MethodEditorException("Expected node does not match");
			}
			mthdNode.instructions.remove(cursorNode);
		}
	}

	private class InsertMode implements Mode {
		@Override
		public void apply(AbstractInsnNode node) {
			if (mthdNode.instructions.size() > 0)
				mthdNode.instructions.insert(mthdNode.instructions.get(cursor++), node);
			else
				mthdNode.instructions.add(node);
		}
	}
}
