package org.mve.invoke;

import org.mve.util.asm.ClassWriter;
import org.mve.util.asm.MethodWriter;
import org.mve.util.asm.file.AccessFlag;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

public class MagicDynamicBindFieldGenerator extends DynamicBindFieldGenerator
{
	public MagicDynamicBindFieldGenerator(Class<?> target, MethodKind implementation, String operation, int kind)
	{
		super(target, implementation, operation, kind);
	}

	@Override
	public void generate(ClassWriter bytecode)
	{
		Field field = ReflectionFactory.ACCESSOR.getField(getTarget(), operation());
		MethodWriter mw = new MethodWriter().set(AccessFlag.ACC_PUBLIC, implementation().name(), implementation().type().toMethodDescriptorString());
		bytecode.addMethod(mw);
		Generator.inline(mw);
		if (kind() == ReflectionFactory.KIND_PUT)
		{
			if (Modifier.isFinal(field.getModifiers()))
			{
				new UnsafeFieldSetterGenerator(field).generate(mw, bytecode);
			}
			else
			{
				new MagicFieldSetterGenerator(field).generate(mw, bytecode);
			}
		}
		else
		{
			new MagicFieldGetterGenerator(field).generate(mw, bytecode);
		}
	}
}
