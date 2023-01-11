package asm.proxy;

import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.RecordComponentVisitor;
import org.objectweb.asm.Type;

public class ASMFooTargetDumped implements Opcodes {

    public static byte[] dump() throws Exception {

        ClassWriter classWriter = new ClassWriter(0);
        FieldVisitor fieldVisitor;
        RecordComponentVisitor recordComponentVisitor;
        MethodVisitor methodVisitor;
        AnnotationVisitor annotationVisitor0;

        classWriter.visit(V18, ACC_PUBLIC | ACC_SUPER, "asm/proxy/ASMFooTarget", null, "asm/proxy/ASMFoo",
                new String[] { "asm/proxy/IObjectProxy" });

        {
            fieldVisitor = classWriter.visitField(0, "s", "Ljava/lang/String;", null, null);
            fieldVisitor.visitEnd();
        }
        {
            fieldVisitor = classWriter.visitField(0, "epi", "Lasm/proxy/IEasyProxyInterceptor;", null, null);
            fieldVisitor.visitEnd();
        }
        {
            fieldVisitor = classWriter.visitField(ACC_PRIVATE | ACC_STATIC, "superMethods", "Ljava/util/HashMap;",
                    "Ljava/util/HashMap<Ljava/lang/String;Ljava/lang/reflect/Method;>;", null);
            fieldVisitor.visitEnd();
        }
        {
            fieldVisitor = classWriter.visitField(ACC_PRIVATE | ACC_STATIC, "proxiedMethods", "Ljava/util/HashMap;",
                    "Ljava/util/HashMap<Ljava/lang/String;Ljava/lang/reflect/Method;>;", null);
            fieldVisitor.visitEnd();
        }
        {
            methodVisitor = classWriter.visitMethod(ACC_STATIC, "<clinit>", "()V", null, null);
            methodVisitor.visitCode();
            methodVisitor.visitTypeInsn(NEW, "java/util/HashMap");
            methodVisitor.visitInsn(DUP);
            methodVisitor.visitMethodInsn(INVOKESPECIAL, "java/util/HashMap", "<init>", "()V", false);
            methodVisitor.visitFieldInsn(PUTSTATIC, "asm/proxy/ASMFooTarget", "superMethods", "Ljava/util/HashMap;");
            methodVisitor.visitTypeInsn(NEW, "java/util/HashMap");
            methodVisitor.visitInsn(DUP);
            methodVisitor.visitMethodInsn(INVOKESPECIAL, "java/util/HashMap", "<init>", "()V", false);
            methodVisitor.visitFieldInsn(PUTSTATIC, "asm/proxy/ASMFooTarget", "proxiedMethods", "Ljava/util/HashMap;");
            methodVisitor.visitLdcInsn(Type.getType("Lasm/proxy/ASMFoo;"));
            methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Class", "getMethods",
                    "()[Ljava/lang/reflect/Method;", false);
            methodVisitor.visitInsn(DUP);
            methodVisitor.visitVarInsn(ASTORE, 3);
            methodVisitor.visitInsn(ARRAYLENGTH);
            methodVisitor.visitVarInsn(ISTORE, 2);
            methodVisitor.visitInsn(ICONST_0);
            methodVisitor.visitVarInsn(ISTORE, 1);
            Label label0 = new Label();
            methodVisitor.visitJumpInsn(GOTO, label0);
            Label label1 = new Label();
            methodVisitor.visitLabel(label1);
            methodVisitor.visitFrame(Opcodes.F_FULL, 4,
                    new Object[] { Opcodes.TOP, Opcodes.INTEGER, Opcodes.INTEGER, "[Ljava/lang/reflect/Method;" }, 0,
                    new Object[] {});
            methodVisitor.visitVarInsn(ALOAD, 3);
            methodVisitor.visitVarInsn(ILOAD, 1);
            methodVisitor.visitInsn(AALOAD);
            methodVisitor.visitVarInsn(ASTORE, 0);
            methodVisitor.visitVarInsn(ALOAD, 0);
            methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "java/lang/reflect/Method", "isSynthetic", "()Z", false);
            Label label2 = new Label();
            methodVisitor.visitJumpInsn(IFNE, label2);
            methodVisitor.visitFieldInsn(GETSTATIC, "asm/proxy/ASMFooTarget", "proxiedMethods", "Ljava/util/HashMap;");
            methodVisitor.visitVarInsn(ALOAD, 0);
            methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "java/lang/reflect/Method", "toString", "()Ljava/lang/String;",
                    false);
            methodVisitor.visitVarInsn(ALOAD, 0);
            methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "java/util/HashMap", "put",
                    "(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;", false);
            methodVisitor.visitInsn(POP);
            methodVisitor.visitLabel(label2);
            methodVisitor.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
            methodVisitor.visitIincInsn(1, 1);
            methodVisitor.visitLabel(label0);
            methodVisitor.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
            methodVisitor.visitVarInsn(ILOAD, 1);
            methodVisitor.visitVarInsn(ILOAD, 2);
            methodVisitor.visitJumpInsn(IF_ICMPLT, label1);
            methodVisitor.visitLdcInsn(Type.getType("Lasm/proxy/ASMFooTarget;"));
            methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Class", "getInterfaces", "()[Ljava/lang/Class;",
                    false);
            methodVisitor.visitInsn(ICONST_0);
            methodVisitor.visitInsn(AALOAD);
            methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Class", "getMethods",
                    "()[Ljava/lang/reflect/Method;", false);
            methodVisitor.visitInsn(DUP);
            methodVisitor.visitVarInsn(ASTORE, 3);
            methodVisitor.visitInsn(ARRAYLENGTH);
            methodVisitor.visitVarInsn(ISTORE, 2);
            methodVisitor.visitInsn(ICONST_0);
            methodVisitor.visitVarInsn(ISTORE, 1);
            Label label3 = new Label();
            methodVisitor.visitJumpInsn(GOTO, label3);
            Label label4 = new Label();
            methodVisitor.visitLabel(label4);
            methodVisitor.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
            methodVisitor.visitVarInsn(ALOAD, 3);
            methodVisitor.visitVarInsn(ILOAD, 1);
            methodVisitor.visitInsn(AALOAD);
            methodVisitor.visitVarInsn(ASTORE, 0);
            methodVisitor.visitVarInsn(ALOAD, 0);
            methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "java/lang/reflect/Method", "isSynthetic", "()Z", false);
            Label label5 = new Label();
            methodVisitor.visitJumpInsn(IFNE, label5);
            methodVisitor.visitFieldInsn(GETSTATIC, "asm/proxy/ASMFooTarget", "proxiedMethods", "Ljava/util/HashMap;");
            methodVisitor.visitVarInsn(ALOAD, 0);
            methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "java/lang/reflect/Method", "toString", "()Ljava/lang/String;",
                    false);
            methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "java/util/HashMap", "get",
                    "(Ljava/lang/Object;)Ljava/lang/Object;", false);
            methodVisitor.visitJumpInsn(IFNONNULL, label5);
            methodVisitor.visitFieldInsn(GETSTATIC, "asm/proxy/ASMFooTarget", "proxiedMethods", "Ljava/util/HashMap;");
            methodVisitor.visitVarInsn(ALOAD, 0);
            methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "java/lang/reflect/Method", "toString", "()Ljava/lang/String;",
                    false);
            methodVisitor.visitVarInsn(ALOAD, 0);
            methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "java/util/HashMap", "put",
                    "(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;", false);
            methodVisitor.visitInsn(POP);
            methodVisitor.visitLabel(label5);
            methodVisitor.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
            methodVisitor.visitIincInsn(1, 1);
            methodVisitor.visitLabel(label3);
            methodVisitor.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
            methodVisitor.visitVarInsn(ILOAD, 1);
            methodVisitor.visitVarInsn(ILOAD, 2);
            methodVisitor.visitJumpInsn(IF_ICMPLT, label4);
            methodVisitor.visitLdcInsn(Type.getType("Lasm/proxy/ASMFooTarget;"));
            methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Class", "getMethods",
                    "()[Ljava/lang/reflect/Method;", false);
            methodVisitor.visitInsn(DUP);
            methodVisitor.visitVarInsn(ASTORE, 3);
            methodVisitor.visitInsn(ARRAYLENGTH);
            methodVisitor.visitVarInsn(ISTORE, 2);
            methodVisitor.visitInsn(ICONST_0);
            methodVisitor.visitVarInsn(ISTORE, 1);
            Label label6 = new Label();
            methodVisitor.visitJumpInsn(GOTO, label6);
            Label label7 = new Label();
            methodVisitor.visitLabel(label7);
            methodVisitor.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
            methodVisitor.visitVarInsn(ALOAD, 3);
            methodVisitor.visitVarInsn(ILOAD, 1);
            methodVisitor.visitInsn(AALOAD);
            methodVisitor.visitVarInsn(ASTORE, 0);
            methodVisitor.visitVarInsn(ALOAD, 0);
            methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "java/lang/reflect/Method", "getName", "()Ljava/lang/String;",
                    false);
            methodVisitor.visitLdcInsn("$proxy");
            methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "java/lang/String", "endsWith", "(Ljava/lang/String;)Z",
                    false);
            Label label8 = new Label();
            methodVisitor.visitJumpInsn(IFEQ, label8);
            methodVisitor.visitFieldInsn(GETSTATIC, "asm/proxy/ASMFooTarget", "superMethods", "Ljava/util/HashMap;");
            methodVisitor.visitVarInsn(ALOAD, 0);
            methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "java/lang/reflect/Method", "toString", "()Ljava/lang/String;",
                    false);
            methodVisitor.visitVarInsn(ALOAD, 0);
            methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "java/util/HashMap", "put",
                    "(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;", false);
            methodVisitor.visitInsn(POP);
            methodVisitor.visitLabel(label8);
            methodVisitor.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
            methodVisitor.visitIincInsn(1, 1);
            methodVisitor.visitLabel(label6);
            methodVisitor.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
            methodVisitor.visitVarInsn(ILOAD, 1);
            methodVisitor.visitVarInsn(ILOAD, 2);
            methodVisitor.visitJumpInsn(IF_ICMPLT, label7);
            methodVisitor.visitInsn(RETURN);
            methodVisitor.visitMaxs(3, 4);
            methodVisitor.visitEnd();
        }
        {
            methodVisitor = classWriter.visitMethod(ACC_PUBLIC, "<init>", "(Lasm/proxy/IEasyProxyInterceptor;)V", null,
                    null);
            methodVisitor.visitCode();
            methodVisitor.visitVarInsn(ALOAD, 0);
            methodVisitor.visitMethodInsn(INVOKESPECIAL, "asm/proxy/ASMFoo", "<init>", "()V", false);
            methodVisitor.visitFieldInsn(GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
            methodVisitor.visitLdcInsn("inicializando... ");
            methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Ljava/lang/String;)V",
                    false);
            methodVisitor.visitVarInsn(ALOAD, 0);
            methodVisitor.visitVarInsn(ALOAD, 1);
            methodVisitor.visitFieldInsn(PUTFIELD, "asm/proxy/ASMFooTarget", "epi",
                    "Lasm/proxy/IEasyProxyInterceptor;");
            methodVisitor.visitFieldInsn(GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
            methodVisitor.visitLdcInsn("fin constructor.");
            methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Ljava/lang/String;)V",
                    false);
            methodVisitor.visitInsn(RETURN);
            methodVisitor.visitMaxs(2, 2);
            methodVisitor.visitEnd();
        }
        {
            methodVisitor = classWriter.visitMethod(ACC_PUBLIC, "setS", "(Ljava/lang/String;)V", null, null);
            methodVisitor.visitCode();
            Label label0 = new Label();
            Label label1 = new Label();
            Label label2 = new Label();
            methodVisitor.visitTryCatchBlock(label0, label1, label2, "java/lang/Throwable");
            methodVisitor.visitLabel(label0);
            methodVisitor.visitVarInsn(ALOAD, 0);
            methodVisitor.visitFieldInsn(GETFIELD, "asm/proxy/ASMFooTarget", "epi",
                    "Lasm/proxy/IEasyProxyInterceptor;");
            methodVisitor.visitVarInsn(ALOAD, 0);
            methodVisitor.visitFieldInsn(GETSTATIC, "asm/proxy/ASMFooTarget", "proxiedMethods", "Ljava/util/HashMap;");
            methodVisitor.visitLdcInsn("public void asm.proxy.ASMFoo.setS(java.lang.String)");
            methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "java/util/HashMap", "get",
                    "(Ljava/lang/Object;)Ljava/lang/Object;", false);
            methodVisitor.visitTypeInsn(CHECKCAST, "java/lang/reflect/Method");
            methodVisitor.visitFieldInsn(GETSTATIC, "asm/proxy/ASMFooTarget", "superMethods", "Ljava/util/HashMap;");
            methodVisitor.visitLdcInsn("public void asm.proxy.ASMFooTarget.setS$proxy(java.lang.String)");
            methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "java/util/HashMap", "get",
                    "(Ljava/lang/Object;)Ljava/lang/Object;", false);
            methodVisitor.visitTypeInsn(CHECKCAST, "java/lang/reflect/Method");
            methodVisitor.visitInsn(ICONST_1);
            methodVisitor.visitTypeInsn(ANEWARRAY, "java/lang/Object");
            methodVisitor.visitInsn(DUP);
            methodVisitor.visitInsn(ICONST_0);
            methodVisitor.visitVarInsn(ALOAD, 1);
            methodVisitor.visitInsn(AASTORE);
            methodVisitor.visitMethodInsn(INVOKEINTERFACE, "asm/proxy/IEasyProxyInterceptor", "intercept",
                    "(Ljava/lang/Object;Ljava/lang/reflect/Method;Ljava/lang/reflect/Method;[Ljava/lang/Object;)Ljava/lang/Object;",
                    true);
            methodVisitor.visitInsn(POP);
            methodVisitor.visitLabel(label1);
            Label label3 = new Label();
            methodVisitor.visitJumpInsn(GOTO, label3);
            methodVisitor.visitLabel(label2);
            methodVisitor.visitFrame(Opcodes.F_SAME1, 0, null, 1, new Object[] { "java/lang/Throwable" });
            methodVisitor.visitVarInsn(ASTORE, 2);
            methodVisitor.visitLdcInsn(Type.getType("Lasm/proxy/ASMFooTarget;"));
            methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Class", "getName", "()Ljava/lang/String;", false);
            methodVisitor.visitMethodInsn(INVOKESTATIC, "java/util/logging/Logger", "getLogger",
                    "(Ljava/lang/String;)Ljava/util/logging/Logger;", false);
            methodVisitor.visitFieldInsn(GETSTATIC, "java/util/logging/Level", "SEVERE", "Ljava/util/logging/Level;");
            methodVisitor.visitInsn(ACONST_NULL);
            methodVisitor.visitVarInsn(ALOAD, 2);
            methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "java/util/logging/Logger", "log",
                    "(Ljava/util/logging/Level;Ljava/lang/String;Ljava/lang/Throwable;)V", false);
            methodVisitor.visitLabel(label3);
            methodVisitor.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
            methodVisitor.visitInsn(RETURN);
            methodVisitor.visitMaxs(8, 3);
            methodVisitor.visitEnd();
        }
        {
            methodVisitor = classWriter.visitMethod(ACC_PUBLIC, "setS$proxy", "(Ljava/lang/String;)V", null, null);
            methodVisitor.visitCode();
            methodVisitor.visitVarInsn(ALOAD, 0);
            methodVisitor.visitVarInsn(ALOAD, 1);
            methodVisitor.visitMethodInsn(INVOKESPECIAL, "asm/proxy/ASMFoo", "setS", "(Ljava/lang/String;)V", false);
            methodVisitor.visitInsn(RETURN);
            methodVisitor.visitMaxs(2, 2);
            methodVisitor.visitEnd();
        }
        {
            methodVisitor = classWriter.visitMethod(ACC_PUBLIC, "sayHello", "(Ljava/lang/String;)V", null, null);
            methodVisitor.visitCode();
            Label label0 = new Label();
            Label label1 = new Label();
            Label label2 = new Label();
            methodVisitor.visitTryCatchBlock(label0, label1, label2, "java/lang/Throwable");
            methodVisitor.visitLabel(label0);
            methodVisitor.visitVarInsn(ALOAD, 0);
            methodVisitor.visitFieldInsn(GETFIELD, "asm/proxy/ASMFooTarget", "epi",
                    "Lasm/proxy/IEasyProxyInterceptor;");
            methodVisitor.visitVarInsn(ALOAD, 0);
            methodVisitor.visitFieldInsn(GETSTATIC, "asm/proxy/ASMFooTarget", "proxiedMethods", "Ljava/util/HashMap;");
            methodVisitor.visitLdcInsn("public void asm.proxy.ASMFoo.setS(java.lang.String)");
            methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "java/util/HashMap", "get",
                    "(Ljava/lang/Object;)Ljava/lang/Object;", false);
            methodVisitor.visitTypeInsn(CHECKCAST, "java/lang/reflect/Method");
            methodVisitor.visitInsn(ACONST_NULL);
            methodVisitor.visitInsn(ICONST_1);
            methodVisitor.visitTypeInsn(ANEWARRAY, "java/lang/Object");
            methodVisitor.visitInsn(DUP);
            methodVisitor.visitInsn(ICONST_0);
            methodVisitor.visitVarInsn(ALOAD, 1);
            methodVisitor.visitInsn(AASTORE);
            methodVisitor.visitMethodInsn(INVOKEINTERFACE, "asm/proxy/IEasyProxyInterceptor", "intercept",
                    "(Ljava/lang/Object;Ljava/lang/reflect/Method;Ljava/lang/reflect/Method;[Ljava/lang/Object;)Ljava/lang/Object;",
                    true);
            methodVisitor.visitInsn(POP);
            methodVisitor.visitLabel(label1);
            Label label3 = new Label();
            methodVisitor.visitJumpInsn(GOTO, label3);
            methodVisitor.visitLabel(label2);
            methodVisitor.visitFrame(Opcodes.F_SAME1, 0, null, 1, new Object[] { "java/lang/Throwable" });
            methodVisitor.visitVarInsn(ASTORE, 2);
            methodVisitor.visitLdcInsn(Type.getType("Lasm/proxy/ASMFooTarget;"));
            methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Class", "getName", "()Ljava/lang/String;", false);
            methodVisitor.visitMethodInsn(INVOKESTATIC, "java/util/logging/Logger", "getLogger",
                    "(Ljava/lang/String;)Ljava/util/logging/Logger;", false);
            methodVisitor.visitFieldInsn(GETSTATIC, "java/util/logging/Level", "SEVERE", "Ljava/util/logging/Level;");
            methodVisitor.visitInsn(ACONST_NULL);
            methodVisitor.visitVarInsn(ALOAD, 2);
            methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "java/util/logging/Logger", "log",
                    "(Ljava/util/logging/Level;Ljava/lang/String;Ljava/lang/Throwable;)V", false);
            methodVisitor.visitLabel(label3);
            methodVisitor.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
            methodVisitor.visitInsn(RETURN);
            methodVisitor.visitMaxs(8, 3);
            methodVisitor.visitEnd();
        }
        {
            methodVisitor = classWriter.visitMethod(ACC_PUBLIC, "setValues", "(ILasm/proxy/ASMFoo;Z)Lasm/proxy/ASMFoo;",
                    null, null);
            methodVisitor.visitCode();
            Label label0 = new Label();
            Label label1 = new Label();
            Label label2 = new Label();
            methodVisitor.visitTryCatchBlock(label0, label1, label2, "java/lang/Throwable");
            methodVisitor.visitInsn(ACONST_NULL);
            methodVisitor.visitVarInsn(ASTORE, 4);
            methodVisitor.visitLabel(label0);
            methodVisitor.visitVarInsn(ALOAD, 0);
            methodVisitor.visitFieldInsn(GETFIELD, "asm/proxy/ASMFooTarget", "epi",
                    "Lasm/proxy/IEasyProxyInterceptor;");
            methodVisitor.visitVarInsn(ALOAD, 0);
            methodVisitor.visitFieldInsn(GETSTATIC, "asm/proxy/ASMFooTarget", "proxiedMethods", "Ljava/util/HashMap;");
            methodVisitor.visitLdcInsn("public void asm.proxy.ASMFoo.setValues(java.lang.String)");
            methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "java/util/HashMap", "get",
                    "(Ljava/lang/Object;)Ljava/lang/Object;", false);
            methodVisitor.visitTypeInsn(CHECKCAST, "java/lang/reflect/Method");
            methodVisitor.visitFieldInsn(GETSTATIC, "asm/proxy/ASMFooTarget", "superMethods", "Ljava/util/HashMap;");
            methodVisitor.visitLdcInsn("public void asm.proxy.ASMFooTarget.setS$proxy(java.lang.String)");
            methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "java/util/HashMap", "get",
                    "(Ljava/lang/Object;)Ljava/lang/Object;", false);
            methodVisitor.visitTypeInsn(CHECKCAST, "java/lang/reflect/Method");
            methodVisitor.visitInsn(ICONST_1);
            methodVisitor.visitTypeInsn(ANEWARRAY, "java/lang/Object");
            methodVisitor.visitInsn(DUP);
            methodVisitor.visitInsn(ICONST_0);
            methodVisitor.visitVarInsn(ALOAD, 0);
            methodVisitor.visitFieldInsn(GETFIELD, "asm/proxy/ASMFooTarget", "s", "Ljava/lang/String;");
            methodVisitor.visitInsn(AASTORE);
            methodVisitor.visitMethodInsn(INVOKEINTERFACE, "asm/proxy/IEasyProxyInterceptor", "intercept",
                    "(Ljava/lang/Object;Ljava/lang/reflect/Method;Ljava/lang/reflect/Method;[Ljava/lang/Object;)Ljava/lang/Object;",
                    true);
            methodVisitor.visitTypeInsn(CHECKCAST, "asm/proxy/ASMFoo");
            methodVisitor.visitVarInsn(ASTORE, 4);
            methodVisitor.visitLabel(label1);
            Label label3 = new Label();
            methodVisitor.visitJumpInsn(GOTO, label3);
            methodVisitor.visitLabel(label2);
            methodVisitor
                    .visitFrame(
                            Opcodes.F_FULL, 5, new Object[] { "asm/proxy/ASMFooTarget", Opcodes.INTEGER,
                                    "asm/proxy/ASMFoo", Opcodes.INTEGER, "asm/proxy/ASMFoo" },
                            1, new Object[] { "java/lang/Throwable" });
            methodVisitor.visitVarInsn(ASTORE, 5);
            methodVisitor.visitLdcInsn(Type.getType("Lasm/proxy/ASMFooTarget;"));
            methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Class", "getName", "()Ljava/lang/String;", false);
            methodVisitor.visitMethodInsn(INVOKESTATIC, "java/util/logging/Logger", "getLogger",
                    "(Ljava/lang/String;)Ljava/util/logging/Logger;", false);
            methodVisitor.visitFieldInsn(GETSTATIC, "java/util/logging/Level", "SEVERE", "Ljava/util/logging/Level;");
            methodVisitor.visitInsn(ACONST_NULL);
            methodVisitor.visitVarInsn(ALOAD, 5);
            methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "java/util/logging/Logger", "log",
                    "(Ljava/util/logging/Level;Ljava/lang/String;Ljava/lang/Throwable;)V", false);
            methodVisitor.visitLabel(label3);
            methodVisitor.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
            methodVisitor.visitVarInsn(ALOAD, 4);
            methodVisitor.visitInsn(ARETURN);
            methodVisitor.visitMaxs(8, 6);
            methodVisitor.visitEnd();
        }
        classWriter.visitEnd();

        return classWriter.toByteArray();
    }
}
