package org.mve.invoke;

import org.mve.util.asm.ClassWriter;
import org.mve.util.asm.FieldWriter;
import org.mve.util.asm.MethodWriter;
import org.mve.util.asm.Opcodes;
import org.mve.util.asm.attribute.CodeWriter;
import org.mve.util.asm.file.AccessFlag;

import java.lang.invoke.MethodType;
import java.util.UUID;

public abstract class AccessorGenerator extends Generator
{
	private static final Unsafe UNSAFE = ReflectionFactory.UNSAFE;
	private static final MagicAccessor ACCESSOR = ReflectionFactory.ACCESSOR;
	private final ClassWriter bytecode = new ClassWriter();
	private final Class<?> target;

	public AccessorGenerator(Class<?> target)
	{
		this.target = target;
		this.bytecode.set(0x34, AccessFlag.ACC_PUBLIC | AccessFlag.ACC_SUPER, UUID.randomUUID().toString().toUpperCase(), CONSTANT_POOL[0], new String[]{});
		this.pregenerate(this.bytecode);
	}

	public void pregenerate(ClassWriter bytecode)
	{
		bytecode.addField(new FieldWriter()
			.set(AccessFlag.ACC_FINAL | AccessFlag.ACC_PRIVATE | AccessFlag.ACC_STATIC, "0", Generator.getSignature(Class.class))
		);
		bytecode.addMethod(new MethodWriter()
			.set(AccessFlag.ACC_PUBLIC, "getReflectionClass", MethodType.methodType(Class.class).toMethodDescriptorString())
			.addAttribute(new CodeWriter()
				.addFieldInstruction(Opcodes.GETSTATIC, this.bytecode.getName(), "0", Generator.getSignature(Class.class))
				.addInstruction(Opcodes.ARETURN)
				.setMaxs(1, 1)
			)
		);
	}

	public void postgenerate(Class<?> generated)
	{
		UNSAFE.putObjectVolatile(generated, UNSAFE.staticFieldOffset(ACCESSOR.getField(generated, "0")), target);
	}

	public ClassWriter bytecode()
	{
		return this.bytecode;
	}

	public abstract void generate();
}
