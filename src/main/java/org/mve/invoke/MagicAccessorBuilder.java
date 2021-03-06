package org.mve.invoke;

import org.mve.util.asm.ClassWriter;
import org.mve.util.asm.FieldWriter;
import org.mve.util.asm.Marker;
import org.mve.util.asm.MethodWriter;
import org.mve.util.asm.Opcodes;
import org.mve.util.asm.attribute.CodeWriter;
import org.mve.util.asm.attribute.SignatureWriter;
import org.mve.util.asm.attribute.SourceWriter;
import org.mve.util.asm.file.AccessFlag;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodType;
import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.security.ProtectionDomain;
import java.util.Arrays;

public class MagicAccessorBuilder
{
	public static ClassWriter build(String[] constantPool, boolean openJ9VM)
	{
		String className = "org/mve/invoke/ReflectionMagicAccessor";
		ClassWriter cw = new ClassWriter();
		cw.set(0x34, AccessFlag.ACC_PUBLIC | AccessFlag.ACC_SUPER | AccessFlag.ACC_FINAL, className, constantPool[0], new String[]{Generator.getType(MagicAccessor.class)});
		cw.addAttribute(new SourceWriter("MagicAccessor.java"));
		cw.addField(new FieldWriter().set(AccessFlag.ACC_PRIVATE | AccessFlag.ACC_STATIC | AccessFlag.ACC_FINAL, "0", Generator.getSignature(SecurityManager.class)));
		cw.addField(new FieldWriter().set(AccessFlag.ACC_PUBLIC | AccessFlag.ACC_STATIC | AccessFlag.ACC_FINAL, "1", "Lsun/management/VMManagementImpl;"));

		/*
		 * <clinit>
		 */
		{
			MethodWriter mw = new MethodWriter().set(AccessFlag.ACC_STATIC, "<clinit>", "()V");
			cw.addMethod(mw);
			CodeWriter code = new CodeWriter();
			mw.addAttribute(code);
			code.addTypeInstruction(Opcodes.NEW, Generator.getType(SecurityManager.class));
			code.addInstruction(Opcodes.DUP);
			code.addMethodInstruction(Opcodes.INVOKESPECIAL, Generator.getType(SecurityManager.class), "<init>", "()V", false);
			code.addFieldInstruction(Opcodes.PUTSTATIC, className, "0", Generator.getSignature(SecurityManager.class));
			code.addMethodInstruction(Opcodes.INVOKESTATIC, Generator.getType(ManagementFactory.class), "getRuntimeMXBean", MethodType.methodType(RuntimeMXBean.class).toMethodDescriptorString(), false)
				.addFieldInstruction(Opcodes.GETFIELD, "sun/management/RuntimeImpl", "jvm", "Lsun/management/VMManagement;")
				.addTypeInstruction(Opcodes.CHECKCAST, "sun/management/VMManagementImpl")
				.addFieldInstruction(Opcodes.PUTSTATIC, className, "1", "Lsun/management/VMManagementImpl;");
			code.addInstruction(Opcodes.RETURN);
			code.setMaxs(2, 0);
		}

		/*
		 * void setAccessible(AccessibleObject acc, boolean flag);
		 */
		{
			MethodWriter mw = new MethodWriter().set(AccessFlag.ACC_PUBLIC | AccessFlag.ACC_FINAL, "setAccessible", MethodType.methodType(void.class, AccessibleObject.class, boolean.class).toMethodDescriptorString());
			cw.addMethod(mw);
			CodeWriter code = new CodeWriter();
			mw.addAttribute(code);
			code.addInstruction(Opcodes.ALOAD_1);
			code.addInstruction(Opcodes.ILOAD_2);
			code.addFieldInstruction(Opcodes.PUTFIELD, Generator.getType(AccessibleObject.class), "override", Generator.getSignature(boolean.class));
			code.addInstruction(Opcodes.RETURN);
			code.setMaxs(2, 3);
		}

		// Class<?> forName(String name);
		{
			MethodWriter mw = new MethodWriter().set(AccessFlag.ACC_PUBLIC | AccessFlag.ACC_FINAL, "forName", MethodType.methodType(Class.class, String.class).toMethodDescriptorString());
			cw.addMethod(mw);
			CodeWriter code = new CodeWriter();
			mw.addAttribute(code);
			if (openJ9VM)
			{
				code.addInstruction(Opcodes.ALOAD_1)
					.addInstruction(Opcodes.ICONST_0)
					.addInstruction(Opcodes.ALOAD_0)
					.addMethodInstruction(Opcodes.INVOKESPECIAL, className, "getCallerClass", MethodType.methodType(Class.class).toMethodDescriptorString(), false)
					.addFieldInstruction(Opcodes.GETFIELD, Generator.getType(Class.class), "classLoader", Generator.getSignature(ClassLoader.class))
					.addMethodInstruction(Opcodes.INVOKESTATIC, Generator.getType(Class.class), "forNameImpl", MethodType.methodType(Class.class, String.class, boolean.class, ClassLoader.class).toMethodDescriptorString(), false)
					.addInstruction(Opcodes.ARETURN)
					.setMaxs(3, 2);
			}
			else
			{
				code.addInstruction(Opcodes.ALOAD_1)
					.addInstruction(Opcodes.ICONST_0)
					.addInstruction(Opcodes.ALOAD_0)
					.addMethodInstruction(Opcodes.INVOKESPECIAL, className, "getCallerClass", MethodType.methodType(Class.class).toMethodDescriptorString(), false)
					.addInstruction(Opcodes.DUP)
					.addInstruction(Opcodes.ASTORE_2)
					.addFieldInstruction(Opcodes.GETFIELD, Generator.getType(Class.class), "classLoader", Generator.getSignature(ClassLoader.class))
					.addInstruction(Opcodes.ALOAD_2)
					.addMethodInstruction(Opcodes.INVOKESTATIC, Generator.getType(Class.class), "forName0", MethodType.methodType(Class.class, String.class, boolean.class, ClassLoader.class, Class.class).toMethodDescriptorString(), false)
					.addInstruction(Opcodes.ARETURN)
					.setMaxs(4, 3);
			}
		}

		// Class<?> forName(String name, boolean initialize, ClassLoader loader);
		{
			MethodWriter mw = new MethodWriter().set(AccessFlag.ACC_PUBLIC | AccessFlag.ACC_FINAL, "forName", MethodType.methodType(Class.class, String.class, boolean.class, ClassLoader.class).toMethodDescriptorString());
			cw.addMethod(mw);
			CodeWriter code = new CodeWriter();
			mw.addAttribute(code);
			if (openJ9VM)
			{
				code.addInstruction(Opcodes.ALOAD_1)
					.addInstruction(Opcodes.ILOAD_2)
					.addInstruction(Opcodes.ALOAD_3)
					.addMethodInstruction(Opcodes.INVOKESPECIAL, Generator.getType(Class.class), "forNameImpl", MethodType.methodType(Class.class, String.class, boolean.class, ClassLoader.class).toMethodDescriptorString(), false)
					.addInstruction(Opcodes.ARETURN)
					.setMaxs(3, 4);
			}
			else
			{
				code.addInstruction(Opcodes.ALOAD_1)
					.addInstruction(Opcodes.ILOAD_2)
					.addInstruction(Opcodes.ALOAD_3)
					.addInstruction(Opcodes.ALOAD_0)
					.addMethodInstruction(Opcodes.INVOKESPECIAL, className, "getCallerClass", MethodType.methodType(Class.class).toMethodDescriptorString(), false)
					.addMethodInstruction(Opcodes.INVOKESTATIC, Generator.getType(Class.class), "forName0", MethodType.methodType(Class.class, String.class, boolean.class, ClassLoader.class, Class.class).toMethodDescriptorString(), false)
					.addInstruction(Opcodes.ARETURN)
					.setMaxs(4, 4);
			}
		}

		// Class<?> defineClass(ClassLoader loader, byte[] code);
		{
			MethodWriter mw = new MethodWriter()
				.set(AccessFlag.ACC_PUBLIC | AccessFlag.ACC_FINAL, "defineClass", MethodType.methodType(Class.class, ClassLoader.class, byte[].class).toMethodDescriptorString())
				.addAttribute(new CodeWriter()
					.addFieldInstruction(Opcodes.GETSTATIC, Generator.getType(ReflectionFactory.class), "UNSAFE", Generator.getSignature(Unsafe.class))
					.addInstruction(Opcodes.ACONST_NULL)
					.addInstruction(Opcodes.ALOAD_2)
					.addInstruction(Opcodes.ICONST_0)
					.addInstruction(Opcodes.ALOAD_2)
					.addInstruction(Opcodes.ARRAYLENGTH)
					.addInstruction(Opcodes.ALOAD_1)
					.addInstruction(Opcodes.ACONST_NULL)
					.addMethodInstruction(Opcodes.INVOKEINTERFACE, Generator.getType(Unsafe.class), "defineClass", MethodType.methodType(Class.class, String.class, byte[].class, int.class, int.class, ClassLoader.class, ProtectionDomain.class).toMethodDescriptorString(), true)
					.addInstruction(Opcodes.ARETURN)
					.setMaxs(7, 3)
				);
			cw.addMethod(mw);
		}

		/*
		 * Class<?> getCallerClass();
		 */
		{
			MethodWriter mw = new MethodWriter().set(AccessFlag.ACC_PUBLIC | AccessFlag.ACC_FINAL, "getCallerClass", MethodType.methodType(Class.class).toMethodDescriptorString());
			cw.addMethod(mw);
			CodeWriter code = new CodeWriter();
			mw.addAttribute(code);
			code.addFieldInstruction(Opcodes.GETSTATIC, className, "0", Generator.getSignature(SecurityManager.class));
			code.addMethodInstruction(Opcodes.INVOKEVIRTUAL, Generator.getType(SecurityManager.class), "getClassContext", MethodType.methodType(Class[].class).toMethodDescriptorString(), false);
			code.addInstruction(Opcodes.ICONST_2);
			code.addInstruction(Opcodes.AALOAD);
			code.addInstruction(Opcodes.ARETURN);
			code.setMaxs(2, 1);
		}

		/*
		 * Class<?>[] getClassContext();
		 */
		{
			MethodWriter mw = new MethodWriter()
				.set(AccessFlag.ACC_FINAL | AccessFlag.ACC_PUBLIC, "getClassContext", MethodType.methodType(Class[].class).toMethodDescriptorString())
				.addAttribute(new SignatureWriter("()[Ljava/lang/Class<*>;"));
			cw.addMethod(mw);
			CodeWriter code = new CodeWriter();
			mw.addAttribute(code);
			code.addFieldInstruction(Opcodes.GETSTATIC, className, "0", Generator.getSignature(SecurityManager.class));
			code.addMethodInstruction(Opcodes.INVOKEVIRTUAL, Generator.getType(SecurityManager.class), "getClassContext", MethodType.methodType(Class[].class).toMethodDescriptorString(), false);
			code.addInstruction(Opcodes.DUP);
			code.addInstruction(Opcodes.ARRAYLENGTH);
			code.addInstruction(Opcodes.ICONST_1);
			code.addInstruction(Opcodes.SWAP);
			code.addMethodInstruction(Opcodes.INVOKESTATIC, Generator.getType(Arrays.class), "copyOfRange", MethodType.methodType(Object[].class, Object[].class, int.class, int.class).toMethodDescriptorString(), false);
			code.addTypeInstruction(Opcodes.CHECKCAST, Generator.getType(Class[].class));
			code.addInstruction(Opcodes.ARETURN);
			code.setMaxs(3, 1);
		}

		/*
		 * <T> T construct(Class<?> target);
		 */
		{
			MethodWriter mw = new MethodWriter()
				.set(AccessFlag.ACC_PUBLIC | AccessFlag.ACC_FINAL, "construct", MethodType.methodType(Object.class, Class.class).toMethodDescriptorString())
				.addAttribute(new SignatureWriter("<T:Ljava/lang/Object;>(Ljava/lang/Class<*>;)TT;"));
			cw.addMethod(mw);
			CodeWriter code = new CodeWriter();
			mw.addAttribute(code);
			code.addInstruction(Opcodes.ALOAD_1);
			code.addInstruction(Opcodes.ICONST_0);
			code.addTypeInstruction(Opcodes.ANEWARRAY, Generator.getType(Class.class));
			code.addMethodInstruction(Opcodes.INVOKEVIRTUAL, Generator.getType(Class.class), "getDeclaredConstructor", MethodType.methodType(Constructor.class, Class[].class).toMethodDescriptorString(), false);
			code.addInstruction(Opcodes.DUP);
			code.addInstruction(Opcodes.ICONST_1);
			code.addFieldInstruction(Opcodes.PUTFIELD, Generator.getType(AccessibleObject.class), "override", Generator.getSignature(boolean.class));
			code.addInstruction(Opcodes.ICONST_0);
			code.addTypeInstruction(Opcodes.ANEWARRAY, Generator.getType(Object.class));
			code.addMethodInstruction(Opcodes.INVOKEVIRTUAL, Generator.getType(Constructor.class), "newInstance", MethodType.methodType(Object.class, Object[].class).toMethodDescriptorString(), false);
			code.addInstruction(Opcodes.ARETURN);
			code.setMaxs(3, 2);
		}

		/*
		 * <T> T construct(Class<?> target, Class<?>[] paramTypes, Object[] params);
		 */
		{
			MethodWriter mw = new MethodWriter().set(AccessFlag.ACC_PUBLIC | AccessFlag.ACC_FINAL, "construct", MethodType.methodType(Object.class, Class.class, Class[].class, Object[].class).toMethodDescriptorString());
			cw.addMethod(mw);
			CodeWriter code = new CodeWriter();
			mw.addAttribute(code);
			code.addInstruction(Opcodes.ALOAD_1);
			code.addInstruction(Opcodes.ALOAD_2);
			code.addMethodInstruction(Opcodes.INVOKEVIRTUAL, Generator.getType(Class.class), "getDeclaredConstructor", MethodType.methodType(Constructor.class, Class[].class).toMethodDescriptorString(), false);
			code.addInstruction(Opcodes.DUP);
			code.addInstruction(Opcodes.ICONST_1);
			code.addFieldInstruction(Opcodes.PUTFIELD, Generator.getType(AccessibleObject.class), "override", Generator.getSignature(boolean.class));
			code.addInstruction(Opcodes.ALOAD_3);
			code.addMethodInstruction(Opcodes.INVOKEVIRTUAL, Generator.getType(Constructor.class), "newInstance", MethodType.methodType(Object.class, Object[].class).toMethodDescriptorString(), false);
			code.addInstruction(Opcodes.ARETURN);
			code.setMaxs(3, 4);
		}

		/*
		 * Object invokeMethodHandle(MethodHandle handle, Object... args);
		 */
		{
			MethodWriter mw = new MethodWriter().set(AccessFlag.ACC_PUBLIC | AccessFlag.ACC_VARARGS, "invokeMethodHandle", MethodType.methodType(Object.class, MethodHandle.class, Object[].class).toMethodDescriptorString());
			cw.addMethod(mw);
			CodeWriter code = new CodeWriter();
			mw.addAttribute(code);
			code.addInstruction(Opcodes.ALOAD_1);
			code.addInstruction(Opcodes.ALOAD_2);
			code.addMethodInstruction(Opcodes.INVOKEVIRTUAL, Generator.getType(MethodHandle.class), "invokeWithArguments", MethodType.methodType(Object.class, Object[].class).toMethodDescriptorString(), false);
			code.addInstruction(Opcodes.ARETURN);
			code.setMaxs(2, 3);
		}

		/*
		 *Field getField(Class<?> target, String name);
		 */
		{
			Marker m1 = new Marker();
			Marker m2 = new Marker();
			Marker m3 = new Marker();
			MethodWriter mw = new MethodWriter()
				.set(AccessFlag.ACC_PUBLIC, "getField", MethodType.methodType(Field.class, Class.class, String.class).toMethodDescriptorString())
				.addAttribute(new CodeWriter()
					.addInstruction(Opcodes.ALOAD_0)
					.addInstruction(Opcodes.ALOAD_1)
					.addMethodInstruction(Opcodes.INVOKESPECIAL, className, "getFields", MethodType.methodType(Field[].class, Class.class).toMethodDescriptorString(), false)
					.addInstruction(Opcodes.ASTORE_3)
					.addInstruction(Opcodes.ALOAD_3)
					.addInstruction(Opcodes.ARRAYLENGTH)
					.addLocalVariableInstruction(Opcodes.ISTORE, 4)
					.addInstruction(Opcodes.ICONST_0)
					.addLocalVariableInstruction(Opcodes.ISTORE, 5)
					.mark(m1)
					.addLocalVariableInstruction(Opcodes.ILOAD, 5)
					.addLocalVariableInstruction(Opcodes.ILOAD, 4)
					.addJumpInstruction(Opcodes.IF_ICMPGE, m3)
					.addInstruction(Opcodes.ALOAD_3)
					.addLocalVariableInstruction(Opcodes.ILOAD, 5)
					.addInstruction(Opcodes.AALOAD)
					.addLocalVariableInstruction(Opcodes.ASTORE, 6)
					.addLocalVariableInstruction(Opcodes.ALOAD, 6)
					.addMethodInstruction(Opcodes.INVOKEVIRTUAL, Generator.getType(Field.class), "getName", "()Ljava/lang/String;", false)
					.addInstruction(Opcodes.ALOAD_2)
					.addMethodInstruction(Opcodes.INVOKEVIRTUAL, Generator.getType(String.class), "equals", "(Ljava/lang/Object;)Z", false)
					.addJumpInstruction(Opcodes.IFEQ, m2)
					.addLocalVariableInstruction(Opcodes.ALOAD, 6)
					.addInstruction(Opcodes.ARETURN)
					.mark(m2)
					.addIincInstruction(5, 1)
					.addJumpInstruction(Opcodes.GOTO, m1)
					.mark(m3)
					.addTypeInstruction(Opcodes.NEW, Generator.getType(NoSuchFieldException.class))
					.addInstruction(Opcodes.DUP)
					.addTypeInstruction(Opcodes.NEW, Generator.getType(StringBuilder.class))
					.addInstruction(Opcodes.DUP)
					.addMethodInstruction(Opcodes.INVOKESPECIAL, Generator.getType(StringBuilder.class), "<init>", "()V", false)
					.addInstruction(Opcodes.ALOAD_1)
					.addMethodInstruction(Opcodes.INVOKEVIRTUAL, Generator.getType(Class.class), "getTypeName", "()Ljava/lang/String;", false)
					.addMethodInstruction(Opcodes.INVOKEVIRTUAL, Generator.getType(StringBuilder.class), "append", "(Ljava/lang/String;)Ljava/lang/StringBuilder;", false)
					.addConstantInstruction(".")
					.addMethodInstruction(Opcodes.INVOKEVIRTUAL, Generator.getType(StringBuilder.class), "append", "(Ljava/lang/String;)Ljava/lang/StringBuilder;", false)
					.addInstruction(Opcodes.ALOAD_2)
					.addMethodInstruction(Opcodes.INVOKEVIRTUAL, Generator.getType(StringBuilder.class), "append", "(Ljava/lang/String;)Ljava/lang/StringBuilder;", false)
					.addMethodInstruction(Opcodes.INVOKEVIRTUAL, Generator.getType(StringBuilder.class), "toString", "()Ljava/lang/String;", false)
					.addMethodInstruction(Opcodes.INVOKESPECIAL, Generator.getType(NoSuchFieldException.class), "<init>", "(Ljava/lang/String;)V", false)
					.addInstruction(Opcodes.ATHROW)
					.setMaxs(4, 7)
				);
			cw.addMethod(mw);

		}

		/*
		 * Method getMethod(Class<?> clazz, String name, Class<?>... parameterTypes);
		 */
		{
			Marker m1 = new Marker();
			Marker m2 = new Marker();
			Marker m3 = new Marker();
			MethodWriter mw = new MethodWriter()
				.set(AccessFlag.ACC_PUBLIC | AccessFlag.ACC_VARARGS, "getMethod", MethodType.methodType(Method.class, Class.class, String.class, Class.class, Class[].class).toMethodDescriptorString())
				.addAttribute(new CodeWriter()
					.addInstruction(Opcodes.ALOAD_0)
					.addInstruction(Opcodes.ALOAD_1)
					.addMethodInstruction(Opcodes.INVOKESPECIAL, className, "getMethods", "(Ljava/lang/Class;)[Ljava/lang/reflect/Method;", false)
					.addLocalVariableInstruction(Opcodes.ASTORE, 5)
					.addInstruction(Opcodes.ICONST_0)
					.addLocalVariableInstruction(Opcodes.ISTORE, 6)
					.addLocalVariableInstruction(Opcodes.ALOAD, 5)
					.addInstruction(Opcodes.ARRAYLENGTH)
					.addLocalVariableInstruction(Opcodes.ISTORE, 7)
					.mark(m1)
					.addLocalVariableInstruction(Opcodes.ILOAD, 6)
					.addLocalVariableInstruction(Opcodes.ILOAD, 7)
					.addJumpInstruction(Opcodes.IF_ICMPGE, m3)
					.addLocalVariableInstruction(Opcodes.ALOAD, 5)
					.addLocalVariableInstruction(Opcodes.ILOAD, 6)
					.addInstruction(Opcodes.AALOAD)
					.addLocalVariableInstruction(Opcodes.ASTORE, 8)
					.addLocalVariableInstruction(Opcodes.ALOAD, 8)
					.addMethodInstruction(Opcodes.INVOKEVIRTUAL, Generator.getType(Method.class), "getName", MethodType.methodType(String.class).toMethodDescriptorString(), false)
					.addInstruction(Opcodes.ALOAD_2)
					.addMethodInstruction(Opcodes.INVOKEVIRTUAL, Generator.getType(String.class), "equals", MethodType.methodType(boolean.class, Object.class).toMethodDescriptorString(), false)
					.addJumpInstruction(Opcodes.IFEQ, m2)
					.addLocalVariableInstruction(Opcodes.ALOAD, 8)
					.addMethodInstruction(Opcodes.INVOKEVIRTUAL, Generator.getType(Method.class), "getReturnType", MethodType.methodType(Class.class).toMethodDescriptorString(), false)
					.addInstruction(Opcodes.ALOAD_3)
					.addMethodInstruction(Opcodes.INVOKEVIRTUAL, Generator.getType(Class.class), "equals", MethodType.methodType(boolean.class, Object.class).toMethodDescriptorString(), false)
					.addJumpInstruction(Opcodes.IFEQ, m2)
					.addLocalVariableInstruction(Opcodes.ALOAD, 8)
					.addMethodInstruction(Opcodes.INVOKEVIRTUAL, Generator.getType(Method.class), "getParameterTypes", MethodType.methodType(Class[].class).toMethodDescriptorString(), false)
					.addLocalVariableInstruction(Opcodes.ALOAD, 4)
					.addMethodInstruction(Opcodes.INVOKESTATIC, Generator.getType(Arrays.class), "equals", MethodType.methodType(boolean.class, Object[].class, Object[].class).toMethodDescriptorString(), false)
					.addJumpInstruction(Opcodes.IFEQ, m2)
					.addLocalVariableInstruction(Opcodes.ALOAD, 8)
					.addInstruction(Opcodes.ARETURN)
					.mark(m2)
					.addIincInstruction(6, 1)
					.addJumpInstruction(Opcodes.GOTO, m1)
					.mark(m3)
					.addTypeInstruction(Opcodes.NEW, Generator.getType(NoSuchMethodException.class))
					.addInstruction(Opcodes.DUP)
					.addTypeInstruction(Opcodes.NEW, Generator.getType(StringBuilder.class))
					.addInstruction(Opcodes.DUP)
					.addMethodInstruction(Opcodes.INVOKESPECIAL, Generator.getType(StringBuilder.class), "<init>", "()V", false)
					.addInstruction(Opcodes.ALOAD_1)
					.addMethodInstruction(Opcodes.INVOKEVIRTUAL, Generator.getType(Class.class), "getName", MethodType.methodType(String.class).toMethodDescriptorString(), false)
					.addMethodInstruction(Opcodes.INVOKEVIRTUAL, Generator.getType(StringBuilder.class), "append", MethodType.methodType(StringBuilder.class, String.class).toMethodDescriptorString(), false)
					.addConstantInstruction(".")
					.addMethodInstruction(Opcodes.INVOKEVIRTUAL, Generator.getType(StringBuilder.class), "append", MethodType.methodType(StringBuilder.class, String.class).toMethodDescriptorString(), false)
					.addInstruction(Opcodes.ALOAD_2)
					.addMethodInstruction(Opcodes.INVOKEVIRTUAL, Generator.getType(StringBuilder.class), "append", MethodType.methodType(StringBuilder.class, String.class).toMethodDescriptorString(), false)
					.addConstantInstruction(":")
					.addMethodInstruction(Opcodes.INVOKEVIRTUAL, Generator.getType(StringBuilder.class), "append", MethodType.methodType(StringBuilder.class, String.class).toMethodDescriptorString(), false)
					.addInstruction(Opcodes.ALOAD_3)
					.addLocalVariableInstruction(Opcodes.ALOAD, 4)
					.addMethodInstruction(Opcodes.INVOKESTATIC, Generator.getType(MethodType.class), "methodType", MethodType.methodType(MethodType.class, Class.class, Class[].class).toMethodDescriptorString(), false)
					.addMethodInstruction(Opcodes.INVOKEVIRTUAL, Generator.getType(MethodType.class), "toString", MethodType.methodType(String.class).toMethodDescriptorString(), false)
					.addMethodInstruction(Opcodes.INVOKEVIRTUAL, Generator.getType(StringBuilder.class), "append", MethodType.methodType(StringBuilder.class, String.class).toMethodDescriptorString(), false)
					.addMethodInstruction(Opcodes.INVOKEVIRTUAL, Generator.getType(StringBuilder.class), "toString", MethodType.methodType(String.class).toMethodDescriptorString(), false)
					.addMethodInstruction(Opcodes.INVOKESPECIAL, Generator.getType(NoSuchMethodException.class), "<init>", MethodType.methodType(void.class, String.class).toMethodDescriptorString(), false)
					.addInstruction(Opcodes.ATHROW)
					.setMaxs(5, 9)
				);
			cw.addMethod(mw);
		}

		/*
		 * <T> Constructor<T> getConstructor(Class<?> target, Class<?> parameterTypes);
		 */
		{
			Marker m1 = new Marker();
			Marker m2 = new Marker();
			Marker m3 = new Marker();
			MethodWriter mw = new MethodWriter().set(AccessFlag.ACC_PUBLIC | AccessFlag.ACC_VARARGS, "getConstructor", MethodType.methodType(Constructor.class, Class.class, Class[].class).toMethodDescriptorString());
			cw.addMethod(mw);
			CodeWriter code = new CodeWriter();
			mw.addAttribute(code);
			code.addInstruction(Opcodes.ALOAD_0);
			code.addInstruction(Opcodes.ALOAD_1);
			code.addMethodInstruction(Opcodes.INVOKESPECIAL, className, "getConstructors", MethodType.methodType(Constructor[].class, Class.class).toMethodDescriptorString(), false);
			code.addInstruction(Opcodes.ASTORE_3)
				.addInstruction(Opcodes.ALOAD_3)
				.addInstruction(Opcodes.ARRAYLENGTH)
				.addLocalVariableInstruction(Opcodes.ISTORE, 4)
				.addInstruction(Opcodes.ICONST_0)
				.addLocalVariableInstruction(Opcodes.ISTORE, 5)
				.mark(m1)
				.addLocalVariableInstruction(Opcodes.ILOAD, 5)
				.addLocalVariableInstruction(Opcodes.ILOAD, 4)
				.addJumpInstruction(Opcodes.IF_ICMPGE, m3)
				.addInstruction(Opcodes.ALOAD_3)
				.addLocalVariableInstruction(Opcodes.ILOAD, 5)
				.addInstruction(Opcodes.AALOAD)
				.addLocalVariableInstruction(Opcodes.ASTORE, 6)
				.addLocalVariableInstruction(Opcodes.ALOAD, 6)
				.addMethodInstruction(Opcodes.INVOKEVIRTUAL, "java/lang/reflect/Constructor", "getParameterTypes", "()[Ljava/lang/Class;", false)
				.addInstruction(Opcodes.ALOAD_2)
				.addMethodInstruction(Opcodes.INVOKESTATIC, "java/util/Arrays", "equals", "([Ljava/lang/Object;[Ljava/lang/Object;)Z", false)
				.addJumpInstruction(Opcodes.IFEQ, m2)
				.addLocalVariableInstruction(Opcodes.ALOAD, 6)
				.addInstruction(Opcodes.ARETURN)
				.mark(m2)
				.addIincInstruction(5, 1)
				.addJumpInstruction(Opcodes.GOTO, m1)
				.mark(m3);
			code.addTypeInstruction(Opcodes.NEW, Generator.getType(NoSuchMethodException.class));
			code.addInstruction(Opcodes.DUP);
			code.addTypeInstruction(Opcodes.NEW, Generator.getType(StringBuilder.class));
			code.addInstruction(Opcodes.DUP);
			code.addMethodInstruction(Opcodes.INVOKESPECIAL, Generator.getType(StringBuilder.class), "<init>", "()V", false);
			code.addInstruction(Opcodes.ALOAD_1);
			code.addMethodInstruction(Opcodes.INVOKESPECIAL, Generator.getType(Class.class), "getTypeName", "()Ljava/lang/String;", false);
			code.addMethodInstruction(Opcodes.INVOKEVIRTUAL, Generator.getType(StringBuilder.class), "append", MethodType.methodType(StringBuilder.class, String.class).toMethodDescriptorString(), false);
			code.addConstantInstruction(".<init>:");
			code.addMethodInstruction(Opcodes.INVOKEVIRTUAL, Generator.getType(StringBuilder.class), "append", MethodType.methodType(StringBuilder.class, String.class).toMethodDescriptorString(), false);
			code.addFieldInstruction(Opcodes.GETSTATIC, "java/lang/Void", "TYPE", "Ljava/lang/Class;");
			code.addInstruction(Opcodes.ALOAD_2)
				.addMethodInstruction(Opcodes.INVOKESTATIC, Generator.getType(MethodType.class), "methodType", MethodType.methodType(MethodType.class, Class.class, Class[].class).toMethodDescriptorString(), false)
				.addMethodInstruction(Opcodes.INVOKEVIRTUAL, Generator.getType(MethodType.class), "toString", MethodType.methodType(String.class).toMethodDescriptorString(), false);
			code.addMethodInstruction(Opcodes.INVOKEVIRTUAL, Generator.getType(StringBuilder.class), "append", MethodType.methodType(StringBuilder.class, String.class).toMethodDescriptorString(), false);
			code.addMethodInstruction(Opcodes.INVOKEVIRTUAL, Generator.getType(StringBuilder.class), "toString", "()Ljava/lang/String;", false);
			code.addMethodInstruction(Opcodes.INVOKESPECIAL, Generator.getType(NoSuchMethodException.class), "<init>", "(Ljava/lang/String;)V", false);
			code.addInstruction(Opcodes.ATHROW);
			code.setMaxs(5, 7);
		}

		/*
		 * Field[] getFields(Class<?>);
		 */
		{
			MethodWriter mw = new MethodWriter().set(AccessFlag.ACC_PUBLIC, "getFields", MethodType.methodType(Field[].class, Class.class).toMethodDescriptorString());
			cw.addMethod(mw);
			CodeWriter code = new CodeWriter();
			mw.addAttribute(code);
			code.addInstruction(Opcodes.ALOAD_1);
			if (!openJ9VM)
			{
				code.addInstruction(Opcodes.ICONST_0);
			}
			code.addMethodInstruction(Opcodes.INVOKESPECIAL, Generator.getType(Class.class), openJ9VM ? "getDeclaredFieldsImpl" : "getDeclaredFields0", MethodType.methodType(Field[].class, openJ9VM ? new Class<?>[0] : new Class<?>[]{boolean.class}).toMethodDescriptorString(), false);
			code.addInstruction(Opcodes.ARETURN);
			code.setMaxs(2, 2);
		}

		/*
		 * Method[] getMethods(Class<?>);
		 */
		{
			MethodWriter mw = new MethodWriter().set(AccessFlag.ACC_PUBLIC, "getMethods", MethodType.methodType(Method[].class, Class.class).toMethodDescriptorString());
			cw.addMethod(mw);
			CodeWriter code = new CodeWriter();
			mw.addAttribute(code);
			code.addInstruction(Opcodes.ALOAD_1);
			if (!openJ9VM)
			{
				code.addInstruction(Opcodes.ICONST_0);
			}
			code.addMethodInstruction(Opcodes.INVOKESPECIAL, Generator.getType(Class.class), openJ9VM ? "getDeclaredMethodsImpl" :"getDeclaredMethods0", MethodType.methodType(Method[].class, openJ9VM ? new Class<?>[0] : new Class<?>[]{boolean.class}).toMethodDescriptorString(), false);
			code.addInstruction(Opcodes.ARETURN);
			code.setMaxs(2, 2);
		}

		/*
		 * <T> Constructor<T>[] getConstructors(Class<?> target);
		 */
		{
			MethodWriter mw = new MethodWriter().set(AccessFlag.ACC_PUBLIC, "getConstructors", MethodType.methodType(Constructor[].class, Class.class).toMethodDescriptorString());
			cw.addMethod(mw);
			CodeWriter code = new CodeWriter();
			mw.addAttribute(code);
			code.addInstruction(Opcodes.ALOAD_1);
			if (!openJ9VM)
			{
				code.addInstruction(Opcodes.ICONST_0);
			}
			code.addMethodInstruction(Opcodes.INVOKESPECIAL, Generator.getType(Class.class), openJ9VM ? "getDeclaredConstructorsImpl" :"getDeclaredConstructors0", MethodType.methodType(Constructor[].class, openJ9VM ? new Class<?>[0] : new Class<?>[]{boolean.class}).toMethodDescriptorString(), false);
			code.addInstruction(Opcodes.ARETURN);
			code.setMaxs(2, 2);
		}

		/*
		 * void throwException(Throwable t);
		 */
		{
			MethodWriter mw = new MethodWriter().set(AccessFlag.ACC_PUBLIC, "throwException", "(Ljava/lang/Throwable;)V");
			cw.addMethod(mw);
			CodeWriter code = new CodeWriter();
			mw.addAttribute(code);
			code.addInstruction(Opcodes.ALOAD_1);
			code.addInstruction(Opcodes.ATHROW);
			code.setMaxs(1, 2);
		}

		/*
		 * void initialize(Object obj);
		 */
		{
			MethodWriter mw = new MethodWriter()
				.set(AccessFlag.ACC_PUBLIC, "initialize", MethodType.methodType(void.class, Object.class).toMethodDescriptorString())
				.addAttribute(new CodeWriter()
					.addInstruction(Opcodes.ALOAD_1)
					.addMethodInstruction(Opcodes.INVOKESPECIAL, Generator.getType(Object.class), "<init>", "()V", false)
					.addInstruction(Opcodes.RETURN)
					.setMaxs(1, 2)
				);
			cw.addMethod(mw);
		}

		/*
		 * String getName(Member member);
		 */
		{
//			Marker field = new Marker();
//			Marker method = new Marker();
//			Marker ret = new Marker();
			MethodWriter mw = new MethodWriter()
				.set(AccessFlag.ACC_PUBLIC, "getName", MethodType.methodType(String.class, Member.class).toMethodDescriptorString())
				.addAttribute(new CodeWriter()
					.addInstruction(Opcodes.ALOAD_1)
					.addMethodInstruction(Opcodes.INVOKEINTERFACE, Generator.getType(Member.class), "getName", MethodType.methodType(String.class).toMethodDescriptorString(), true)
					.addInstruction(Opcodes.ARETURN)
					.setMaxs(1, 2)
				);
			cw.addMethod(mw);
		}

		/*
		 * int getPID();
		 */
		{
			MethodWriter mw = new MethodWriter()
				.set(AccessFlag.ACC_PUBLIC, "getPID", MethodType.methodType(int.class).toMethodDescriptorString())
				.addAttribute(new CodeWriter()
					.addFieldInstruction(Opcodes.GETSTATIC, className, "1", "Lsun/management/VMManagementImpl;")
					.addMethodInstruction(Opcodes.INVOKEVIRTUAL, "sun/management/VMManagementImpl", "getProcessId", MethodType.methodType(int.class).toMethodDescriptorString(), false)
					.addInstruction(Opcodes.IRETURN)
					.setMaxs(1, 1)
				);
			cw.addMethod(mw);
		}

		return cw;
	}
}
