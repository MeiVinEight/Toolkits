package org.mve.invoke;

import org.mve.util.asm.ClassWriter;
import org.mve.util.asm.MethodWriter;
import org.mve.util.asm.Opcodes;
import org.mve.util.asm.attribute.CodeWriter;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

public class UnsafeFieldSetterGenerator extends FieldSetterGenerator
{
	public UnsafeFieldSetterGenerator(Field field)
	{
		super(field);
	}

	@Override
	public void generate(MethodWriter method, ClassWriter classWriter)
	{
		Field field = this.getField();
		int modifiers = field.getModifiers();
		boolean statics = Modifier.isStatic(modifiers);
		long off = statics ? ReflectionFactory.UNSAFE.staticFieldOffset(field) : ReflectionFactory.UNSAFE.objectFieldOffset(field);

		CodeWriter code = new CodeWriter();
		method.addAttribute(code);

		Class<?> type = field.getType();
		int load;
		if (type == byte.class || type == short.class || type == int.class || type == boolean.class || type == char.class)
		{
			load = Opcodes.ILOAD_1;
		}
		else if (type == long.class)
		{
			load = Opcodes.LLOAD_1;
		}
		else if (type == float.class)
		{
			load = Opcodes.FLOAD_1;
		}
		else if (type == double.class)
		{
			load = Opcodes.DLOAD_1;
		}
		else
		{
			load = Opcodes.ALOAD_1;
		}

		code.addFieldInstruction(Opcodes.GETSTATIC, Generator.getType(ReflectionFactory.class), "UNSAFE", Generator.getSignature(Unsafe.class));
		if (statics)
		{
			code.addFieldInstruction(Opcodes.GETSTATIC, classWriter.getName(), "0", Generator.getSignature(Class.class));
		}
		else
		{
			code.addInstruction(Opcodes.ALOAD_1);
			load++;
		}
		code.addConstantInstruction(off)
			.addInstruction(load);
		Generator.unsafeput(type, code);
		code.addInstruction(Opcodes.RETURN)
			.setMaxs(4 + Generator.typeSize(type), (statics ? 1 : 2) + Generator.typeSize(type));
	}
}
