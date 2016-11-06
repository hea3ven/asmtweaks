package com.hea3ven.tools.asmtweaks.editors;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.objectweb.asm.Label;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.MethodNode;

import com.hea3ven.tools.asmtweaks.ASMTweaksManager;
import com.hea3ven.tools.asmtweaks.ASMUtils;
import com.hea3ven.tools.mappings.ClsMapping;
import com.hea3ven.tools.mappings.ObfLevel;

public class MethodEditor {
	private static final Logger logger = LogManager.getLogger("asmtweaks.MethodEditor");

	private final ASMTweaksManager mgr;
	private final MethodNode mthdNode;

	private int cursor = 0;

	private Map<String, ClsMapping> imports = new HashMap<>();
	private Mode mode;
	private ObfLevel level;

	public MethodEditor(ASMTweaksManager mgr, MethodNode mthdNode) {
		this.mgr = mgr;
		this.mthdNode = mthdNode;
		level = null;
	}

	public void setLevel(ObfLevel level) {
		this.level = level;
	}

	public void Seek(int count) {
		cursor += count;
	}

	public Label GetLabel() {
		AbstractInsnNode node = mthdNode.instructions.get(cursor);
		if (!(node instanceof LabelNode))
			return null;
		return ((LabelNode) node).getLabel();
	}

	public void setSearchMode() {
		mode = new SearchMode();
	}

	public void setSearchExactMode() {
		mode = new SearchExactMode();
	}

	public void setRemoveMode() {
		mode = new RemoveMode();
	}

	public void setRemoveExactMode() {
		mode = new RemoveExactMode();
	}

	public void setInsertMode() {
		mode = new InsertMode();
	}

	public InstructionBuilder newInstructionBuilder(ASMContext template) {
		return new InstructionBuilder(new ASMContext(template, mgr.getMapping(),
				mgr.getObfuscationMode()));
	}

	public InstructionBuilder newInstructionBuilder() {
		return new InstructionBuilder(
				new ASMContext(mgr.getMapping(), mgr.getObfuscationMode()));
	}

	public boolean apply(InstructionBuilder insnBuilder) {
		return mode.apply(insnBuilder.build());
	}

	public ASMTweaksManager getManager() {
		return mgr;
	}

	public int getCursor() {
		return cursor;
	}

	public LabelRef createLabel() {
		return new LabelRef(new Label());
	}

	public LabelRef getLabel() {
		AbstractInsnNode node = mthdNode.instructions.get(cursor);
		return (node instanceof LabelNode) ? new LabelRef(((LabelNode) node).getLabel()) : null;
	}

	private interface Mode {

		boolean apply(List<AbstractInsnNode> nodes);
	}

	private class SearchMode implements Mode {
		@Override
		public boolean apply(List<AbstractInsnNode> nodes) {
			boolean found = false;
			for (int pos = cursor + 1; pos < mthdNode.instructions.size(); pos++) {
				AbstractInsnNode posNode = mthdNode.instructions.get(pos);
				if (ASMUtils.areNodesEqual(nodes.get(0), posNode)) {
					found = true;
					int off = 1;
					for (; pos + off < mthdNode.instructions.size() && off < nodes.size(); off++) {
						AbstractInsnNode pos2Node = mthdNode.instructions.get(pos + off);
						if (!ASMUtils.areNodesEqual(nodes.get(off), pos2Node)) {
							found = false;
							break;
						}
					}
					if (found && off < nodes.size())
						found = false;
					if (found && pos + off >= mthdNode.instructions.size() && off < nodes.size())
						found = false;
					if (found) {
						cursor = pos;
						break;
					}
				}
			}
			return found;
		}
	}

	private class SearchExactMode extends SearchMode {
		@Override
		public boolean apply(List<AbstractInsnNode> nodes) {
			if (!super.apply(nodes)) {
				logger.error("Could not find nodes ({})", ASMUtils.nodeToString(nodes.get(0)));
				throw new MethodEditorException("Could not find nodes");
			}
			return true;
		}
	}

	private class RemoveMode implements Mode {
		@Override
		public boolean apply(List<AbstractInsnNode> nodes) {
			for (AbstractInsnNode node : nodes) {
//			logger.info("Removing nodes {}", ASMUtils.nodeToString(nodes));
				AbstractInsnNode cursorNode = mthdNode.instructions.get(cursor);
				if (!ASMUtils.areNodesEqual(node, cursorNode)) {
					return false;
				}
				mthdNode.instructions.remove(cursorNode);
			}
			return true;
		}
	}

	private class RemoveExactMode extends RemoveMode {
		@Override
		public boolean apply(List<AbstractInsnNode> nodes) {
			if (!super.apply(nodes)) {
				logger.error("Expected node does not match");
				throw new MethodEditorException("Expected node does not match");
			}
			return true;
		}
	}

	private class InsertMode implements Mode {
		@Override
		public boolean apply(List<AbstractInsnNode> nodes) {
			for (AbstractInsnNode node : nodes) {
//			logger.info("Inserting nodes {}", ASMUtils.nodeToString(nodes));
				if (mthdNode.instructions.size() > 0) {
					if (cursor < mthdNode.instructions.size())
						mthdNode.instructions.insertBefore(mthdNode.instructions.get(cursor++), node);
					else {
						mthdNode.instructions.insert(mthdNode.instructions.getLast(), node);
						cursor = mthdNode.instructions.size();
					}
				} else {
					mthdNode.instructions.add(node);
					cursor = 1;
				}
			}
			return true;
		}
	}
}
