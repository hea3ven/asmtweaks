package com.hea3ven.tools.asmtweaks.editors;

import javax.annotation.Nonnull;
import java.lang.reflect.Field;

import org.objectweb.asm.Label;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

import com.hea3ven.tools.asmtweaks.ASMTweaksManager;
import com.hea3ven.tools.mappings.IdentityMapping;
import com.hea3ven.tools.mappings.Mapping;
import com.hea3ven.tools.mappings.ObfLevel;

public class MethodEditorBaseTest {
	@Nonnull
	protected MethodEditor createMethodEditor(MethodNode mthdNode) {
		ASMTweaksManager mgr = new ASMTweaksManager("1.0.0", ObfLevel.DEOBF, true);
		Mapping mapping = new IdentityMapping();
		mgr.setMapping(mapping);
		return new MethodEditor(mgr, mthdNode);
	}

	@Nonnull
	protected MethodEditor createMethodEditor1(MethodNode mthdNode) {
		mthdNode.instructions.add(new LabelNode(new Label()));
		mthdNode.instructions.add(new VarInsnNode(Opcodes.ALOAD, 0));
		mthdNode.instructions.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "A", "a", "()J", false));
		mthdNode.instructions.add(new InsnNode(Opcodes.LCONST_1));
		mthdNode.instructions.add(new InsnNode(Opcodes.LADD));
		mthdNode.instructions.add(new FieldInsnNode(Opcodes.PUTFIELD, "A", "b", "J"));
		mthdNode.instructions.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "A", "b", "()Z", false));
		mthdNode.instructions.add(new VarInsnNode(Opcodes.ALOAD, 0));
		mthdNode.instructions.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "A", "b", "()Z", false));
		return createMethodEditor(mthdNode);
	}

	protected MethodNode getMethodNode(MethodEditor editor) {
		try {
			Field mthdNodeField = MethodEditor.class.getDeclaredField("mthdNode");
			mthdNodeField.setAccessible(true);
			return (MethodNode) mthdNodeField.get(editor);
		} catch (NoSuchFieldException | IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}
}
