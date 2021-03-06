package org.mve.invoke;

import org.mve.util.asm.ClassWriter;
import org.mve.util.asm.MethodWriter;
import org.mve.util.asm.Opcodes;
import org.mve.util.asm.OperandStack;
import org.mve.util.asm.attribute.CodeWriter;
import org.mve.util.asm.file.AccessFlag;

import java.lang.invoke.MethodType;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

public class MagicMethodAccessorGenerator extends MethodAccessorGenerator
{
	private final ClassWriter bytecode = this.bytecode();
	private final Method method = this.getMethod();
	private final int kind = this.kind();

	public MagicMethodAccessorGenerator(Method method, int kind)
	{
		super(method, kind);
	}

	@Override
	public void generate()
	{
		int modifiers = this.method.getModifiers();
		boolean statics = Modifier.isStatic(modifiers);
		boolean	interfaces = Modifier.isAbstract(modifiers);
		MethodWriter mw = new MethodWriter().set(AccessFlag.ACC_PUBLIC, "invoke", MethodType.methodType(Object.class, Object[].class).toMethodDescriptorString());
		this.bytecode.addMethod(mw);
		Generator.inline(mw);
		CodeWriter code = new CodeWriter();
		mw.addAttribute(code);
		int load = this.method.getParameterTypes().length + (statics ? 0 : 1);
		Class<?>[] parameters = this.method.getParameterTypes();
		for (int i=0; i<load; i++)
		{
			code.addInstruction(Opcodes.ALOAD_1)
				.addNumberInstruction(Opcodes.BIPUSH, i)
				.addInstruction(Opcodes.AALOAD);
			Class<?> parameterType;
			if ((statics || i > 0) && (parameterType = parameters[statics ? i : (i-1)]).isPrimitive())
			{
				unwarp(parameterType, code);
			}
		}
		code.addMethodInstruction(this.kind + 0xB6, Generator.getType(this.method.getDeclaringClass()), ReflectionFactory.ACCESSOR.getName(this.method), MethodType.methodType(this.method.getReturnType(), this.method.getParameterTypes()).toMethodDescriptorString(), interfaces);
		if (method.getReturnType() == void.class)
		{
			code.addInstruction(Opcodes.ACONST_NULL);
		}
		else
		{
			Generator.warp(method.getReturnType(), code);
		}
		code.addInstruction(Opcodes.ARETURN)
			.setMaxs(this.stack(), 2);
		if (statics && parameters.length == 0)
		{
			mw = new MethodWriter().set(AccessFlag.ACC_PUBLIC, "invoke", MethodType.methodType(Object.class).toMethodDescriptorString());
			this.bytecode.addMethod(mw);
			Generator.inline(mw);
			code = new CodeWriter();
			mw.addAttribute(code);
			code.addMethodInstruction(this.kind + 0xB6, Generator.getType(this.method.getDeclaringClass()), ReflectionFactory.ACCESSOR.getName(this.method), MethodType.methodType(this.method.getReturnType()).toMethodDescriptorString(), interfaces);
			if (method.getReturnType() == void.class)
			{
				code.addInstruction(Opcodes.ACONST_NULL);
			}
			else
			{
				Generator.warp(method.getReturnType(), code);
			}
			int ts = Generator.typeSize(this.method.getReturnType());
			code.addInstruction(Opcodes.ARETURN)
				.setMaxs(ts == 0 ? 1 : ts, 1);
		}
	}

	private int stack()
	{
		OperandStack stack = new OperandStack();
		int modifiers = this.method.getModifiers();
		boolean statics = Modifier.isStatic(modifiers);
		if (!statics)
		{
			stack.push();
		}
		Class<?>[] parameters = this.method.getParameterTypes();
		for (Class<?> c : parameters)
		{
			stack.push();
			if (c == double.class || c == long.class)
			{
				stack.push();
			}
		}
		for (Class<?> c : parameters)
		{
			stack.pop();
			if (c == double.class || c == long.class)
			{
				stack.pop();
			}
		}
		if (!statics)
		{
			stack.pop();
		}
		stack.push();
		if (this.method.getReturnType() == long.class || this.method.getReturnType() == double.class)
		{
			stack.push();
		}
		stack.pop();
		if (this.method.getReturnType() == long.class || this.method.getReturnType() == double.class)
		{
			stack.pop();
		}
		return stack.getMaxSize();
	}
}
