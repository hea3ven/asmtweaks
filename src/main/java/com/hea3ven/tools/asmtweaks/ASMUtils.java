package com.hea3ven.tools.asmtweaks;

import java.io.IOException;
import java.io.InputStream;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.*;

import com.hea3ven.tools.mappings.ClsMapping;
import com.hea3ven.tools.mappings.MthdMapping;

public class ASMUtils {
	public static ClassNode readClass(InputStream stream) {
		ClassReader classReader;
		try {
			classReader = new ClassReader(stream);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

		return readClass(classReader);
	}

	public static ClassNode readClass(byte[] basicClass) {
		return readClass(new ClassReader(basicClass));
	}

	private static ClassNode readClass(ClassReader classReader) {
		ClassNode classNode = new ClassNode();
		classReader.accept(classNode, 0);
		return classNode;
	}

	public static byte[] writeClass(ClassNode classNode) {
		ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS);
		classNode.accept(writer);
		return writer.toByteArray();
	}

	public static MethodNode getMethod(ClassNode classNode, MthdMapping mthd) {
		for (MethodNode m : classNode.methods) {
			if (mthd.matches(classNode.name, m.name, m.desc))
				return m;
		}
		return null;
	}

	public static String obfuscateDesc(ASMTweaksManager mgr, String deobfDesc) {
		StringBuilder obfDesc = new StringBuilder();
		int i = 0;
		while (i < deobfDesc.length()) {
			if (deobfDesc.charAt(i) != 'L') {
				obfDesc.append(deobfDesc.charAt(i));
				i++;
				continue;
			}
			int end = deobfDesc.indexOf(';', i);
			if (end == -1)
				throw new RuntimeException("missing ending ; in desc '" + deobfDesc + "'");
			String className = deobfDesc.substring(i + 1, end);
			ClsMapping cls = mgr.getMapping().getCls(className);
			obfDesc.append('L');
			obfDesc.append(cls != null ? cls.getSrcPath() : className);
			obfDesc.append(';');
			i = end + 1;
		}
		return obfDesc.toString();
	}

	public static boolean areNodesEqual(AbstractInsnNode node1, AbstractInsnNode node2) {
		if (node1.getType() != node2.getType())
			return false;
		if (node1.getOpcode() != node2.getOpcode())
			return false;
		if (node1 instanceof FieldInsnNode) {
			FieldInsnNode fldNode1 = (FieldInsnNode) node1;
			FieldInsnNode fldNode2 = (FieldInsnNode) node2;
			return fldNode1.owner.equals(fldNode2.owner) && fldNode1.name.equals(fldNode2.name) &&
					fldNode1.desc.equals(fldNode2.desc);
		}
		if (node1 instanceof MethodInsnNode) {
			MethodInsnNode mthdNode1 = (MethodInsnNode) node1;
			MethodInsnNode mthdNode2 = (MethodInsnNode) node2;
			return mthdNode1.owner.equals(mthdNode2.owner) && mthdNode1.name.equals(mthdNode2.name) &&
					mthdNode1.desc.equals(mthdNode2.desc) && mthdNode1.itf == mthdNode2.itf;
		}
		if (node1 instanceof JumpInsnNode) {
			JumpInsnNode jumpNode1 = (JumpInsnNode) node1;
			JumpInsnNode jumpNode2 = (JumpInsnNode) node2;
			return jumpNode1.label.equals(jumpNode2.label);
		}
		if (node1 instanceof TypeInsnNode) {
			TypeInsnNode typeNode1 = (TypeInsnNode) node1;
			TypeInsnNode typeNode2 = (TypeInsnNode) node2;
			return typeNode1.desc.equals(typeNode2.desc);
		}
		if (node1 instanceof VarInsnNode) {
			VarInsnNode varNode1 = (VarInsnNode) node1;
			VarInsnNode varNode2 = (VarInsnNode) node2;
			return varNode1.var == varNode2.var;
		}
		if (node1 instanceof LabelNode) {
			LabelNode labelNode1 = (LabelNode) node1;
			LabelNode labelNode2 = (LabelNode) node2;
			return labelNode1.equals(labelNode2);
		}
		if (node1 instanceof InsnNode) {
			return true;
		}
		return false;
	}

	public static String nodeToString(AbstractInsnNode node) {
		String result = "<" + node.getClass().getSimpleName() + " op(" + node.getOpcode() + ")";
		if (node instanceof FieldInsnNode) {
			FieldInsnNode fldNode = (FieldInsnNode) node;
			result += " " + fldNode.owner + "/" + fldNode.name + " " + fldNode.desc;
		}
		if (node instanceof MethodInsnNode) {
			MethodInsnNode mthdNode = (MethodInsnNode) node;
			result += " " + mthdNode.owner + "/" + mthdNode.name + " " + mthdNode.desc + " " + mthdNode.itf;
		}
		if (node instanceof JumpInsnNode) {
			JumpInsnNode jumpNode = (JumpInsnNode) node;
			result += " " + jumpNode.label;
		}
		if (node instanceof TypeInsnNode) {
			TypeInsnNode typeNode = (TypeInsnNode) node;
			result += " " + typeNode.desc;
		}
		if (node instanceof VarInsnNode) {
			VarInsnNode varNode = (VarInsnNode) node;
			result += " " + varNode.var;
		}
		if (node instanceof LabelNode) {
			LabelNode labelNode = (LabelNode) node;
			result += " " + labelNode;
		}
		result += ">";
		return result;
	}
}
