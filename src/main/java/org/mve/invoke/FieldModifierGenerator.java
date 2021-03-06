package org.mve.invoke;

import org.mve.util.asm.ClassWriter;
import org.mve.util.asm.MethodWriter;

import java.lang.reflect.Field;

public abstract class FieldModifierGenerator extends Generator
{
	private final Field field;

	public FieldModifierGenerator(Field field)
	{
		this.field = field;
	}

	public abstract void generate(MethodWriter method, ClassWriter classWriter);

	public Field getField()
	{
		return field;
	}
}
