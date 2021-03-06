package org.mve.invoke;

import org.mve.util.asm.ClassWriter;
import org.mve.util.asm.FieldWriter;
import org.mve.util.asm.file.AccessFlag;

import java.util.UUID;

public abstract class DynamicBind extends Generator
{
	private final ClassWriter bytecode = new ClassWriter();
	private final Class<?> target;
	private final Class<?> define;

	public DynamicBind(Class<?> handle, Class<?> target)
	{
		Class<?> c = Generator.checkAccessible(handle.getClassLoader(), target.getClassLoader()) ? handle : target;
		this.define = Generator.checkAccessible(c.getClassLoader()) ? c : ReflectionFactory.class;
		this.target = target;
		this.bytecode.set(0x34, 0x21, UUID.randomUUID().toString().toUpperCase(), CONSTANT_POOL[0], new String[]{Generator.getType(handle)});
		this.bytecode.addField(new FieldWriter().set(AccessFlag.ACC_PRIVATE | AccessFlag.ACC_STATIC, "0", Generator.getSignature(Class.class)));
	}

	public void postgenerate(Class<?> generated)
	{
		ReflectionFactory.UNSAFE.putObjectVolatile(generated, ReflectionFactory.UNSAFE.staticFieldOffset(ReflectionFactory.ACCESSOR.getField(generated, "0")), this.target);
	}

	public ClassWriter bytecode()
	{
		return bytecode;
	}

	public Class<?> target()
	{
		return target;
	}

	public Class<?> define()
	{
		return define;
	}

	public abstract void method(MethodKind implementation, MethodKind invocation, int kind);

	public abstract void field(MethodKind implementation, String operation, int kind);

	public abstract void construct(MethodKind implementation, MethodKind invocation);

	public abstract void instantiation(MethodKind implementation);

	public abstract void enumHelper();
}
