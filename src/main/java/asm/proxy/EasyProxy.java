/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package asm.proxy;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.text.rtf.RTFEditorKit;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

/**
 *
 * @author Marcelo D. Ré {@literal <marcelo.re@gmail.com>}
 */
public class EasyProxy implements Opcodes {

    private final static Logger LOGGER = Logger.getLogger(EasyProxy.class.getName());

    static {
        if (LOGGER.getLevel() == null) {
            LOGGER.setLevel(Level.WARNING);
        }
        
        // org.burningwave.core.assembler.StaticComponentContainer.Modules.exportAllToAll();
    }

    private String clazzSuffix = "_EasyProxy";
    // método a invocar en el interceptor
    private final String PROXYNAME = "interceptor";

    // Lista de metodos del objeto real
    private List<Method> methods = new ArrayList<>();
    // lista de metodos que implementa el proxy a través de la interface
    private List<Method> interceptorMethods = new ArrayList<>();

    MethodVisitor methodVisitor;

    Map<String,TypeRef> typesHelper = new HashMap<>();
    // Map<String,String> checkCast = new HashMap<>();

    private String outputDirectory = null;
    
    public EasyProxy() {

        typesHelper.put("boolean",new TypeRef("Z", "java/lang/Boolean", "booleanValue", "()Z",IRETURN, ICONST_0));
        typesHelper.put("char",new TypeRef("C", "java/lang/Character", "charValue", "()C",IRETURN, ICONST_0));
        typesHelper.put("byte",new TypeRef("B", "java/lang/Byte", "byteValue", "()B",IRETURN, ICONST_0));
        typesHelper.put("short",new TypeRef("S", "java/lang/Short", "shortValue", "()S",IRETURN, ICONST_0));
        typesHelper.put("int",new TypeRef("I", "java/lang/Integer", "intValue", "()I",IRETURN, ICONST_0));
        typesHelper.put("float",new TypeRef("F", "java/lang/Float", "floatValue", "()F",FRETURN, FCONST_0));
        typesHelper.put("long",new TypeRef("J", "java/lang/Long", "longValue", "()J",LRETURN, LCONST_0));
        typesHelper.put("double",new TypeRef("D", "java/lang/Double", "doubleValue", "()D",DRETURN, DCONST_0));
        
        // no es un tipo pero ayuda en la conversión
        typesHelper.put("void",new TypeRef("V", null, null, null,0,0));

    }

    public String getClazzSuffix() {
        return clazzSuffix;
    }

    public EasyProxy setClazzSuffix(String clazzSuffix) {
        this.clazzSuffix = clazzSuffix;
        return this;
    }

    public String getOutputDirectory() {
        return outputDirectory;
    }

    public EasyProxy setOutputDirectory(String outputDirectory) {
        this.outputDirectory = outputDirectory.endsWith("/")?outputDirectory:outputDirectory+"/";
        return this;
    }
    
    public Class<?> getProxyClass(Class<?> c) throws XDuplicatedProxyClass { 
        return getProxyClass(c, null);
    }
    
    /**
     * Generate a new class that proxies all the methods of the given class
     *
     * @param c
     * @param interceptor
     *
     * @return
     */
    public <T> Class<T> getProxyClass(Class<T> c, Class<?> interceptor) { // Class<? extends IEasyProxyInterceptor>
        Class<?> current = c;

        LOGGER.log(Level.FINEST,"\n\n\n================================================================="); 
        LOGGER.log(Level.FINEST,"Analizando clase: " + c.getName());
        LOGGER.log(Level.FINEST,"================================================================="); 
        // analizar la clase y recuperar todos los métodos que se van a implementar.
        LOGGER.log(Level.FINEST, "Class methods:");
        // while (current != Object.class) {
            LOGGER.log(Level.FINEST, "Analizando clase: " + current.getCanonicalName());
            for (Method declaredMethod : c.getMethods()) {
                if (!declaredMethod.isSynthetic() 
                    && !Modifier.isFinal(declaredMethod.getModifiers()) ) {
                    LOGGER.log(Level.FINEST, "to be proxied: " + declaredMethod.getName()
                            + " :  " + declaredMethod.isSynthetic()
                            + " : " + Arrays.toString(declaredMethod.getParameters())
                            + " : " + "declaring class: " + declaredMethod.getDeclaringClass().getName()
                            );
                    methods.add(declaredMethod);
                } else {
                    LOGGER.log(Level.FINEST, "ignored: " + declaredMethod.getName() + " :  "
                            + declaredMethod.isSynthetic() + " : " + Arrays.toString(declaredMethod.getParameters()));
                }
            }
            // current = current.getSuperclass();
        // }
        LOGGER.log(Level.FINEST, "\n\nInterceptor interface methods");
        // recuperar todos los métodos del interceptor
        String proxyInterface = "";
        if (interceptor!=null) {
            for (Method declaredMethod : interceptor.getMethods()) {
                if (!declaredMethod.isSynthetic()) {
                    LOGGER.log(Level.FINEST, "to be proxied: " + declaredMethod.getName() + " :  "
                            + declaredMethod.isSynthetic() + " : " + Arrays.toString(declaredMethod.getParameters()));
                    interceptorMethods.add(declaredMethod);
                } else {
                    LOGGER.log(Level.FINEST, "ignored: " + declaredMethod.getName() + " :  " + declaredMethod.isSynthetic()
                            + " : " + Arrays.toString(declaredMethod.getParameters()));
                }
            }
            proxyInterface = interceptor.getCanonicalName().replace(".", "/");
        }

        String clazzName = c.getCanonicalName().replace(".", "/") + clazzSuffix;
        String superName = c.getCanonicalName().replace(".", "/");
        
        LOGGER.log(Level.FINEST, "\n\n\nclazzName: " + clazzName);
        LOGGER.log(Level.FINEST, "super: " + superName);
        String pi = proxyInterface;
        LOGGER.log(Level.FINEST, "proxy interface: " + pi);
        // Comenzar a crear la clase
        ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES|ClassWriter.COMPUTE_MAXS);
        cw.visit(Opcodes.V11, // Java 11
                Opcodes.ACC_PUBLIC, // public class
                clazzName, // package and name
                null, // signature (null means not generic)
                superName, // superclass
                proxyInterface.isEmpty()?null:new String[] { proxyInterface }); // interfaces

        // agregar el campo interceptor
        cw.visitField(Opcodes.ACC_PUBLIC,
                PROXYNAME,
                Type.getDescriptor(IEasyProxyInterceptor.class),
                null, null).visitEnd();
        ;

        // Add field:
        // private java.util.HashMap<String, java.lang.reflect.Method> proxyMethods;
        // FIXME: pasar a ACC_PRIVATE
        cw.visitField(Opcodes.ACC_PUBLIC | Opcodes.ACC_STATIC, // access flags
                "superMethods", // field name
                Type.getDescriptor(HashMap.class), // field type
                "Ljava/util/HashMap<Ljava/lang/String;Ljava/lang/reflect/Method;>;", // signature
                null).visitEnd(); // default value

        cw.visitField(Opcodes.ACC_PUBLIC | Opcodes.ACC_STATIC, // access flags
                "proxiedMethods", // field name
                Type.getDescriptor(HashMap.class), // field type
                "Ljava/util/HashMap<Ljava/lang/String;Ljava/lang/reflect/Method;>;", // signature
                null).visitEnd(); // default value

        // insertar el método que inicializa la clase
        this.addInitMethod(cw, clazzName, superName, interceptor);

        /* Build constructor */
        MethodVisitor constructor = cw.visitMethod(
                Opcodes.ACC_PUBLIC, // public method
                "<init>", // method name
                "(Lasm/proxy/IEasyProxyInterceptor;)V", // descriptor
                null, // signature (null means not generic)
                null); // exceptions (array of strings)

        // crear el código del contructor.
        // public ASMFooTarget(IEasyProxyInterceptor epi) {
        // this.epi = epi;
        // }
        constructor.visitCode();
        constructor.visitVarInsn(Opcodes.ALOAD, 0); // Load "this" onto the stack

        constructor.visitMethodInsn(Opcodes.INVOKESPECIAL, // Invoke an instance method (non-virtual)
                superName, // Class on which the method is defined
                "<init>", // Name of the method
                "()V", // Descriptor
                false); // Is this class an interface?

        constructor.visitVarInsn(Opcodes.ALOAD, 0); // Load "this" onto the stack
        constructor.visitVarInsn(Opcodes.ALOAD, 1); // Load "epi" onto the stack

        LOGGER.log(Level.FINEST, "descriptor: " + Type.getDescriptor(IEasyProxyInterceptor.class));

        constructor.visitFieldInsn(Opcodes.PUTFIELD, clazzName, PROXYNAME,
                Type.getDescriptor(IEasyProxyInterceptor.class));
        
        if (LOGGER.getLevel() == Level.FINEST) {
            constructor.visitFieldInsn(GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
            constructor.visitTypeInsn(NEW, "java/lang/StringBuilder");
            constructor.visitInsn(DUP);
            constructor.visitLdcInsn("\n\nproxiedMethods: ");
            constructor.visitMethodInsn(INVOKESPECIAL, "java/lang/StringBuilder", "<init>", "(Ljava/lang/String;)V", false);
            constructor.visitFieldInsn(GETSTATIC, clazzName, "proxiedMethods", "Ljava/util/HashMap;");
            constructor.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(Ljava/lang/Object;)Ljava/lang/StringBuilder;", false);
            constructor.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "toString", "()Ljava/lang/String;", false);
            constructor.visitMethodInsn(INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Ljava/lang/String;)V", false);
            constructor.visitFieldInsn(GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
            constructor.visitTypeInsn(NEW, "java/lang/StringBuilder");
            constructor.visitInsn(DUP);
            constructor.visitLdcInsn("\n\nsuperMethods: ");
            constructor.visitMethodInsn(INVOKESPECIAL, "java/lang/StringBuilder", "<init>", "(Ljava/lang/String;)V", false);
            constructor.visitFieldInsn(GETSTATIC, clazzName, "superMethods", "Ljava/util/HashMap;");
            constructor.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(Ljava/lang/Object;)Ljava/lang/StringBuilder;", false);
            constructor.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "toString", "()Ljava/lang/String;", false);
            constructor.visitMethodInsn(INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Ljava/lang/String;)V", false);
        }


        constructor.visitInsn(Opcodes.RETURN);
        constructor.visitMaxs(0, 0);
        constructor.visitEnd();

        // para cada método de la clase sobreescribirlo y crear el proxy que lo invoque
        LOGGER.log(Level.FINEST,"");
        LOGGER.log(Level.FINEST,"");
        LOGGER.log(Level.FINEST,"");
        LOGGER.log(Level.FINEST,"=================================================================");
        LOGGER.log(Level.FINEST,"=================================================================");
        LOGGER.log(Level.FINEST,"=================================================================");
        LOGGER.log(Level.FINEST,"Generating proxy methods ");
        LOGGER.log(Level.FINEST,"=================================================================");
        LOGGER.log(Level.FINEST,"=================================================================");
        LOGGER.log(Level.FINEST,"=================================================================");
        for (Method method : methods) {
            LOGGER.log(Level.FINEST,"\n\n\n\nGenerating proxy method " + method.getName() + "...");
            this.generateProxy(cw, clazzName, superName, method);
        }

        // agregar interceptores para cada método de la interface 
        LOGGER.log(Level.FINEST,"");
        LOGGER.log(Level.FINEST,"");
        LOGGER.log(Level.FINEST,"");
        LOGGER.log(Level.FINEST,"=================================================================");
        LOGGER.log(Level.FINEST,"=================================================================");
        LOGGER.log(Level.FINEST,"=================================================================");
        LOGGER.log(Level.FINEST,"Generating interceptor methods ");
        LOGGER.log(Level.FINEST,"=================================================================");
        LOGGER.log(Level.FINEST,"=================================================================");
        LOGGER.log(Level.FINEST,"=================================================================");
        for (Method method : interceptorMethods) {
            LOGGER.log(Level.FINEST,"=================================================================");
            LOGGER.log(Level.FINEST,"\n\n\n\nGenerating interface interceptor's method " + method.getName() + "...");
            LOGGER.log(Level.FINEST,"=================================================================");
            this.generateInterceptorProxy(cw, clazzName, superName, method);
        }

        // Finalizar la clase y escribirla
        cw.visitEnd();
        byte[] data = cw.toByteArray();

        if (this.outputDirectory != null) {
            writeToFile(clazzName, data);
        }
        Class<T> r = (Class<T>)this.injectClass(c.getClassLoader(), clazzName.replace("/", "."), data);
        return r;
    }

    private Class<?> injectClass(ClassLoader cl, String clazzName, byte[] data) {
        LOGGER.log(Level.FINEST,"inyectando la clase en el ClassLoader....");
        Class<?> clazzLoader = ClassLoader.class;
        Class<?> dynamicallyGeneratedClass = null;
        try {
            dynamicallyGeneratedClass = ClassLoader.class.forName(clazzName);
            LOGGER.log(Level.FINEST,"la clase ya ha sido cargada!!!! utilizar la existente");
        } catch (ClassNotFoundException cnf) {
            LOGGER.log(Level.FINEST,"Clase no encontrada. Proceder a cargarla en el ClassLoader...");
            try {
                // Method defineClassMethod;
                // defineClassMethod = clazzLoader.getDeclaredMethod("defineClass", String.class, byte[].class, int.class, int.class);
                // defineClassMethod.setAccessible(true);
                // dynamicallyGeneratedClass = (Class<?>) defineClassMethod.invoke(cl, clazzName, data, 0, data.length);
                DynamicClassLoader dcl = new DynamicClassLoader(cl);
                dynamicallyGeneratedClass = dcl.defineClass(clazzName, data);
            
            // } catch (NoSuchMethodException | SecurityException | IllegalAccessException | InvocationTargetException e) {
            } catch ( SecurityException  e) {
                LOGGER.log(Level.FINEST,"ERROR al intentar insertar la clase en el ClassLoader!");
                e.printStackTrace();
            }
            
        }
        LOGGER.log(Level.FINEST,"clase cargada con éxito!");
        return dynamicallyGeneratedClass;
    }
    
    // private Class<?> injectClass(Class<?> c, String clazzName, byte[] data) {
    //     Class<?> dynamicallyGeneratedClass = null;

    //     // Obtener el objeto MethodHandles.Lookup que tiene acceso a los miembros privados de la clase original
    //     MethodHandles.Lookup originalClassLookup = MethodHandles.privateLookupIn(c, MethodHandles.lookup());

    //     // Obtener el ClassLoader de la clase original
    //     ClassLoader originalClassLoader = c.getClassLoader();

    //     // Definir la clase generada por ByteBuddy en el ClassLoader de la clase original
    //     Class<?> loadedClass = (Class<?>) originalClassLookup.findVirtual(ClassLoader.class, "defineClass", MethodType.methodType(Class.class, String.class, byte[].class, int.class, int.class))
    //         .invoke(originalClassLoader, clazzName, ((Class<?> ) generatedClass).getDeclaredField("BYTES").get(null), 0, ((Class<?> ) generatedClass).getDeclaredField("BYTES").get(null).length);





    //     LOGGER.log(Level.FINEST,"clase cargada con éxito!");
    //     return dynamicallyGeneratedClass;
    // }



    // Agrega un método estático que registra todos los métodos de la clase y del
    // interceptor
    private void addInitMethod(ClassWriter cw, String clazzName, String superName, Class<?> interceptor) {
        methodVisitor = cw.visitMethod(ACC_STATIC, "<clinit>", "()V", null, null);
        methodVisitor.visitCode();
        methodVisitor.visitTypeInsn(NEW, "java/util/HashMap");
        methodVisitor.visitInsn(DUP);
        methodVisitor.visitMethodInsn(INVOKESPECIAL, "java/util/HashMap", "<init>", "()V", false);
        methodVisitor.visitFieldInsn(PUTSTATIC, clazzName, "superMethods", "Ljava/util/HashMap;");
        methodVisitor.visitTypeInsn(NEW, "java/util/HashMap");
        methodVisitor.visitInsn(DUP);
        methodVisitor.visitMethodInsn(INVOKESPECIAL, "java/util/HashMap", "<init>", "()V", false);
        methodVisitor.visitFieldInsn(PUTSTATIC, clazzName, "proxiedMethods", "Ljava/util/HashMap;");
        methodVisitor.visitLdcInsn(Type.getType("L"+superName+";"));
        methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Class", "getMethods", "()[Ljava/lang/reflect/Method;", false);
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
        methodVisitor.visitFrame(Opcodes.F_FULL, 4, new Object[] {Opcodes.TOP, Opcodes.INTEGER, Opcodes.INTEGER, "[Ljava/lang/reflect/Method;"}, 0, new Object[] {});
        methodVisitor.visitVarInsn(ALOAD, 3);
        methodVisitor.visitVarInsn(ILOAD, 1);
        methodVisitor.visitInsn(AALOAD);
        methodVisitor.visitVarInsn(ASTORE, 0);
        methodVisitor.visitVarInsn(ALOAD, 0);
        // FIXME: should remove final methods
        methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "java/lang/reflect/Method", "isSynthetic", "()Z", false);
        Label label2 = new Label();
        methodVisitor.visitJumpInsn(IFNE, label2);
        methodVisitor.visitFieldInsn(GETSTATIC, clazzName, "proxiedMethods", "Ljava/util/HashMap;");
        methodVisitor.visitTypeInsn(NEW, "java/lang/StringBuilder");
        methodVisitor.visitInsn(DUP);
        methodVisitor.visitVarInsn(ALOAD, 0);
        methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "java/lang/reflect/Method", "getName", "()Ljava/lang/String;", false);
        methodVisitor.visitMethodInsn(INVOKESTATIC, "java/lang/String", "valueOf", "(Ljava/lang/Object;)Ljava/lang/String;", false);
        methodVisitor.visitMethodInsn(INVOKESPECIAL, "java/lang/StringBuilder", "<init>", "(Ljava/lang/String;)V", false);
        methodVisitor.visitVarInsn(ALOAD, 0);
        methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "java/lang/reflect/Method", "getParameterTypes", "()[Ljava/lang/Class;", false);
        methodVisitor.visitMethodInsn(INVOKESTATIC, "java/util/Arrays", "toString", "([Ljava/lang/Object;)Ljava/lang/String;", false);
        methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(Ljava/lang/String;)Ljava/lang/StringBuilder;", false);
        methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "toString", "()Ljava/lang/String;", false);
        methodVisitor.visitVarInsn(ALOAD, 0);
        methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "java/util/HashMap", "put", "(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;", false);
        methodVisitor.visitInsn(POP);
        methodVisitor.visitLabel(label2);
        methodVisitor.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
        methodVisitor.visitIincInsn(1, 1);
        methodVisitor.visitLabel(label0);
        methodVisitor.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
        methodVisitor.visitVarInsn(ILOAD, 1);
        methodVisitor.visitVarInsn(ILOAD, 2);
        methodVisitor.visitJumpInsn(IF_ICMPLT, label1);
        methodVisitor.visitLdcInsn(Type.getType("L"+clazzName+";"));
        methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Class", "getInterfaces", "()[Ljava/lang/Class;", false);
        methodVisitor.visitInsn(ICONST_0);
        methodVisitor.visitInsn(AALOAD);
        methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Class", "getMethods", "()[Ljava/lang/reflect/Method;", false);
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
        methodVisitor.visitFieldInsn(GETSTATIC, clazzName, "proxiedMethods", "Ljava/util/HashMap;");
        methodVisitor.visitVarInsn(ALOAD, 0);
        methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "java/lang/reflect/Method", "toString", "()Ljava/lang/String;", false);
        methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "java/util/HashMap", "get", "(Ljava/lang/Object;)Ljava/lang/Object;", false);
        methodVisitor.visitJumpInsn(IFNONNULL, label5);
        methodVisitor.visitFieldInsn(GETSTATIC, clazzName, "proxiedMethods", "Ljava/util/HashMap;");
        methodVisitor.visitTypeInsn(NEW, "java/lang/StringBuilder");
        methodVisitor.visitInsn(DUP);
        methodVisitor.visitVarInsn(ALOAD, 0);
        methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "java/lang/reflect/Method", "getName", "()Ljava/lang/String;", false);
        methodVisitor.visitMethodInsn(INVOKESTATIC, "java/lang/String", "valueOf", "(Ljava/lang/Object;)Ljava/lang/String;", false);
        methodVisitor.visitMethodInsn(INVOKESPECIAL, "java/lang/StringBuilder", "<init>", "(Ljava/lang/String;)V", false);
        methodVisitor.visitVarInsn(ALOAD, 0);
        methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "java/lang/reflect/Method", "getParameterTypes", "()[Ljava/lang/Class;", false);
        methodVisitor.visitMethodInsn(INVOKESTATIC, "java/util/Arrays", "toString", "([Ljava/lang/Object;)Ljava/lang/String;", false);
        methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(Ljava/lang/String;)Ljava/lang/StringBuilder;", false);
        methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "toString", "()Ljava/lang/String;", false);
        methodVisitor.visitVarInsn(ALOAD, 0);
        methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "java/util/HashMap", "put", "(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;", false);
        methodVisitor.visitInsn(POP);
        methodVisitor.visitLabel(label5);
        methodVisitor.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
        methodVisitor.visitIincInsn(1, 1);
        methodVisitor.visitLabel(label3);
        methodVisitor.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
        methodVisitor.visitVarInsn(ILOAD, 1);
        methodVisitor.visitVarInsn(ILOAD, 2);
        methodVisitor.visitJumpInsn(IF_ICMPLT, label4);
        methodVisitor.visitLdcInsn(Type.getType("L"+clazzName+";"));
        methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Class", "getMethods", "()[Ljava/lang/reflect/Method;", false);
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
        methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "java/lang/reflect/Method", "getName", "()Ljava/lang/String;", false);
        methodVisitor.visitLdcInsn("$proxy");
        methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "java/lang/String", "endsWith", "(Ljava/lang/String;)Z", false);
        Label label8 = new Label();
        methodVisitor.visitJumpInsn(IFEQ, label8);
        methodVisitor.visitFieldInsn(GETSTATIC, clazzName, "superMethods", "Ljava/util/HashMap;");
        methodVisitor.visitTypeInsn(NEW, "java/lang/StringBuilder");
        methodVisitor.visitInsn(DUP);
        methodVisitor.visitVarInsn(ALOAD, 0);
        methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "java/lang/reflect/Method", "getName", "()Ljava/lang/String;", false);
        methodVisitor.visitMethodInsn(INVOKESTATIC, "java/lang/String", "valueOf", "(Ljava/lang/Object;)Ljava/lang/String;", false);
        methodVisitor.visitMethodInsn(INVOKESPECIAL, "java/lang/StringBuilder", "<init>", "(Ljava/lang/String;)V", false);
        methodVisitor.visitVarInsn(ALOAD, 0);
        methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "java/lang/reflect/Method", "getParameterTypes", "()[Ljava/lang/Class;", false);
        methodVisitor.visitMethodInsn(INVOKESTATIC, "java/util/Arrays", "toString", "([Ljava/lang/Object;)Ljava/lang/String;", false);
        methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(Ljava/lang/String;)Ljava/lang/StringBuilder;", false);
        methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "toString", "()Ljava/lang/String;", false);
        methodVisitor.visitVarInsn(ALOAD, 0);
        methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "java/util/HashMap", "put", "(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;", false);
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
        methodVisitor.visitMaxs(4, 4);
        methodVisitor.visitEnd();
    }

    private void generateProxy(ClassWriter cw, String clazzName, String superName, Method method) {
        //ParType> [int, 
        //          class java.lang.Integer, 
        //          class asm.proxy.ASMFoo, 
        //          class [Lasm.proxy.ASMFoo;, 
        //          boolean, 
        //          float, 
        //          class [F, 
        //          class [[Ljava.lang.Float;
        //         ]
        // preparar la cadena de parametros
        LOGGER.log(Level.FINEST,"------------------------------------------------");
        LOGGER.log(Level.FINEST,"\n\n\nprocesando método: " + method.toString());
        LOGGER.log(Level.FINEST,"Type.getDescriptor: "+Type.getMethodDescriptor(method));
        String methodDescriptor = Type.getMethodDescriptor(method);
        
        LOGGER.log(Level.FINEST,"methodDescriptor: "+methodDescriptor);
        // calcular el tipo de return type
        TypeRef returnType = typesHelper.get(method.getReturnType().toString());
        if (returnType == null ) {
            // si es null quiere decir el tipo de retorno es una clase.
            returnType = new TypeRef("L"+method.getReturnType().toString().replace("class ","").replace("interface ","").replace(".", "/")+";",null,null,null,ARETURN,ACONST_NULL);
        }
        LOGGER.log(Level.FINEST,"returnType: "+returnType + "  --> "+method.getReturnType().toString());
        // determinar el cast para el valor devuelto por el interceptor 
        String returnCastType = "";
        if (typesHelper.get(method.getReturnType().toString()) == null){
            returnCastType = method.getReturnType().toString().replace("class ","").replace("interface ","").replace(".", "/");
        } else {
            returnCastType = typesHelper.get(method.getReturnType().toString()).castClass;
        }
        LOGGER.log(Level.FINEST,"return cast type: "+returnCastType);

        // procesar las excepciones
        String[] methodExceptions =  Arrays.stream(method.getGenericExceptionTypes())
                                            .map(t->t.getTypeName().replace("class ", "").replace("interface ","").replace(".", "/"))
                                            .toArray(size->new String[size]);
        LOGGER.log(Level.FINEST,"excepciones: "+Arrays.toString(methodExceptions));
        LOGGER.log(Level.FINEST,"");
        
        methodVisitor = cw.visitMethod(ACC_PUBLIC, method.getName(), methodDescriptor, null, methodExceptions);
        methodVisitor.visitCode();
        Label tryStart = new Label();
        Label tryEnd = new Label();
        Label catchThw = new Label();
        Label lblReturn = new Label();
        Label labelElse = new Label();
        methodVisitor.visitTryCatchBlock(tryStart, tryEnd, catchThw, "java/lang/Throwable");
        

        methodVisitor.visitVarInsn(ALOAD, 0);
        methodVisitor.visitFieldInsn(GETFIELD, clazzName, PROXYNAME, "Lasm/proxy/IEasyProxyInterceptor;");

        // Esto es la primera parte del IF que controla si el interceptor está en null
        // y de ser así llama al método original sin interceptar. Esto se coloca para poder invocar 
        // correctamente los métodos cuando son invocados desde el constructor por defecto de la clase padre
        // dado que en ese momento el interceptor aún no se ha asignado.
        methodVisitor.visitJumpInsn(IFNONNULL, tryStart);
        methodVisitor.visitVarInsn(ALOAD, 0);

        int stackOffset = 1;
        if (method.getParameterCount()>0) {
            // cargar a la pila todos los parámetros
            // cuando se inserta un Long o Double es necesario saltar un lugar en la pila.
            // methodVisitor.visitIntInsn(BIPUSH,method.getParameterCount());
            // methodVisitor.visitTypeInsn(ANEWARRAY, "java/lang/Object");
            
            // insetar todas los parametros en el vector. Para cada parámetro es necesario realuzar la conversión correspondiente.
            for (int i = 0; i < method.getParameterCount(); i++) {
                // methodVisitor.visitInsn(DUP);
                // methodVisitor.visitIntInsn(BIPUSH,i);
                switch(method.getParameters()[i].getType().toString()) {
                    case "boolean":
                        methodVisitor.visitVarInsn(ILOAD, stackOffset);
                        stackOffset += 1;
                        // methodVisitor.visitMethodInsn(INVOKESTATIC, "java/lang/Boolean", "valueOf", "(Z)Ljava/lang/Boolean;", false);
                        break;
                    case "char":
                        methodVisitor.visitVarInsn(ILOAD, stackOffset);
                        stackOffset += 1;
                        // methodVisitor.visitMethodInsn(INVOKESTATIC, "java/lang/Character", "valueOf", "(C)Ljava/lang/Character;", false);
                        break;
                    case "byte":
                        methodVisitor.visitVarInsn(ILOAD, stackOffset);
                        stackOffset += 1;
                        // methodVisitor.visitMethodInsn(INVOKESTATIC, "java/lang/Byte", "valueOf", "(B)Ljava/lang/Byte;", false);
                        break;
                    case "short":
                        methodVisitor.visitVarInsn(ILOAD, stackOffset);
                        stackOffset += 1;
                        // methodVisitor.visitMethodInsn(INVOKESTATIC, "java/lang/Short", "valueOf", "(S)Ljava/lang/Short;", false);
                        break;
                    case "int":
                        methodVisitor.visitVarInsn(ILOAD, stackOffset);
                        stackOffset += 1;
                        // methodVisitor.visitMethodInsn(INVOKESTATIC, "java/lang/Integer", "valueOf", "(I)Ljava/lang/Integer;", false);
                        break;
                    case "float":
                        methodVisitor.visitVarInsn(FLOAD, stackOffset);
                        stackOffset += 1;
                        // methodVisitor.visitMethodInsn(INVOKESTATIC, "java/lang/Float", "valueOf", "(F)Ljava/lang/Float;", false);
                        break;
                    case "long":
                        methodVisitor.visitVarInsn(LLOAD, stackOffset);
                        stackOffset += 2;
                        // methodVisitor.visitMethodInsn(INVOKESTATIC, "java/lang/Long", "valueOf", "(J)Ljava/lang/Long;", false);
                        break;
                    case "double":
                        methodVisitor.visitVarInsn(DLOAD, stackOffset);
                        stackOffset += 2;
                        // methodVisitor.visitMethodInsn(INVOKESTATIC, "java/lang/Double", "valueOf", "(D)Ljava/lang/Double;", false);
                        break;
                    default:
                        methodVisitor.visitVarInsn(ALOAD, stackOffset);
                        stackOffset += 1;
                }
                // methodVisitor.visitInsn(AASTORE);
            }
        }
        
        methodVisitor.visitMethodInsn(INVOKEVIRTUAL, clazzName, method.getName()+"$proxy", methodDescriptor, false);
        
        if (returnType.toAsm.equals("V")) {
            methodVisitor.visitJumpInsn(GOTO, lblReturn);        
        } else {
            // castear el resultado al tipo del retorno del método
            // LOGGER.log(Level.FINEST,"el método retorna valores. Preparar el cast");
            // methodVisitor.visitTypeInsn(CHECKCAST, returnCastType);
            // LOGGER.log(Level.FINEST,"checkcast: "+returnCastType);
            // if (returnType.castMethod!=null) {
            //     methodVisitor.visitMethodInsn(INVOKEVIRTUAL, returnType.castClass, returnType.castMethod, returnType.descrptor, false);
            // }
            // methodVisitor.visitLabel(tryEnd);
            methodVisitor.visitInsn(returnType.asmReturnValue);
        }
        
        // esto es el else que llama al interceptor en vez del método
        methodVisitor.visitLabel(tryStart);
        methodVisitor.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
        methodVisitor.visitVarInsn(ALOAD, 0);
        methodVisitor.visitFieldInsn(GETFIELD, clazzName, PROXYNAME, "Lasm/proxy/IEasyProxyInterceptor;");
        methodVisitor.visitVarInsn(ALOAD, 0);
        methodVisitor.visitFieldInsn(GETSTATIC, clazzName, "proxiedMethods", "Ljava/util/HashMap;");
        methodVisitor.visitLdcInsn(method.getName()+Arrays.toString(method.getParameterTypes()));
        methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "java/util/HashMap", "get",
                "(Ljava/lang/Object;)Ljava/lang/Object;", false);
        methodVisitor.visitTypeInsn(CHECKCAST, "java/lang/reflect/Method");
        methodVisitor.visitFieldInsn(GETSTATIC, clazzName, "superMethods", "Ljava/util/HashMap;");
        // Fixme: debería buscar una forma de referenciar que sea mas directa. 
        methodVisitor.visitLdcInsn(method.getName()+"$proxy"+Arrays.toString(method.getParameterTypes()));
        methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "java/util/HashMap", "get",
                "(Ljava/lang/Object;)Ljava/lang/Object;", false);
        methodVisitor.visitTypeInsn(CHECKCAST, "java/lang/reflect/Method");

        methodVisitor.visitIntInsn(BIPUSH,method.getParameterCount());
        methodVisitor.visitTypeInsn(ANEWARRAY, "java/lang/Object");
        
        // cuando se inserta un Long o Double es necesario saltar un lugar en la pila.
        stackOffset = 1;
        // insetar todas los parametros en el vector. Para cada parámetro es necesario realuzar la conversión correspondiente.
        for (int i = 0; i < method.getParameterCount(); i++) {
            methodVisitor.visitInsn(DUP);
            methodVisitor.visitIntInsn(BIPUSH,i);
            switch(method.getParameters()[i].getType().toString()) {
                case "boolean":
                    methodVisitor.visitVarInsn(ILOAD, stackOffset);
                    stackOffset += 1;
                    methodVisitor.visitMethodInsn(INVOKESTATIC, "java/lang/Boolean", "valueOf", "(Z)Ljava/lang/Boolean;", false);
                    break;
                case "char":
                    methodVisitor.visitVarInsn(ILOAD, stackOffset);
                    stackOffset += 1;
                    methodVisitor.visitMethodInsn(INVOKESTATIC, "java/lang/Character", "valueOf", "(C)Ljava/lang/Character;", false);
                    break;
                case "byte":
                    methodVisitor.visitVarInsn(ILOAD, stackOffset);
                    stackOffset += 1;
                    methodVisitor.visitMethodInsn(INVOKESTATIC, "java/lang/Byte", "valueOf", "(B)Ljava/lang/Byte;", false);
                    break;
                case "short":
                    methodVisitor.visitVarInsn(ILOAD, stackOffset);
                    stackOffset += 1;
                    methodVisitor.visitMethodInsn(INVOKESTATIC, "java/lang/Short", "valueOf", "(S)Ljava/lang/Short;", false);
                    break;
                case "int":
                    methodVisitor.visitVarInsn(ILOAD, stackOffset);
                    stackOffset += 1;
                    methodVisitor.visitMethodInsn(INVOKESTATIC, "java/lang/Integer", "valueOf", "(I)Ljava/lang/Integer;", false);
                    break;
                case "float":
                    methodVisitor.visitVarInsn(FLOAD, stackOffset);
                    stackOffset += 1;
                    methodVisitor.visitMethodInsn(INVOKESTATIC, "java/lang/Float", "valueOf", "(F)Ljava/lang/Float;", false);
                    break;
                case "long":
                    methodVisitor.visitVarInsn(LLOAD, stackOffset);
                    stackOffset += 2;
                    methodVisitor.visitMethodInsn(INVOKESTATIC, "java/lang/Long", "valueOf", "(J)Ljava/lang/Long;", false);
                    break;
                case "double":
                    methodVisitor.visitVarInsn(DLOAD, stackOffset);
                    stackOffset += 2;
                    methodVisitor.visitMethodInsn(INVOKESTATIC, "java/lang/Double", "valueOf", "(D)Ljava/lang/Double;", false);
                    break;
                default:
                    methodVisitor.visitVarInsn(ALOAD, stackOffset);
                    stackOffset += 1;
            }
            methodVisitor.visitInsn(AASTORE);
        }

        // invocar al interceptor
        methodVisitor.visitMethodInsn(INVOKEINTERFACE, "asm/proxy/IEasyProxyInterceptor", "intercept",
                "(Ljava/lang/Object;Ljava/lang/reflect/Method;Ljava/lang/reflect/Method;[Ljava/lang/Object;)Ljava/lang/Object;",
                true);
        
        if (returnType.toAsm.equals("V")) {
            methodVisitor.visitInsn(POP);
            methodVisitor.visitLabel(tryEnd);
            methodVisitor.visitJumpInsn(GOTO, lblReturn);
            
        } else {
            // castear el resultado al tipo del retorno del método
            LOGGER.log(Level.FINEST,"el método retorna valores. Preparar el cast");
            methodVisitor.visitTypeInsn(CHECKCAST, returnCastType);
            LOGGER.log(Level.FINEST,"checkcast: "+returnCastType);
            if (returnType.castMethod!=null) {
                methodVisitor.visitMethodInsn(INVOKEVIRTUAL, returnType.castClass, returnType.castMethod, returnType.descrptor, false);
            }
            methodVisitor.visitLabel(tryEnd);
            methodVisitor.visitInsn(returnType.asmReturnValue);
        }

        // iniciar el catch del Throwable
        methodVisitor.visitLabel(catchThw);
        methodVisitor.visitFrame(Opcodes.F_SAME1, 0, null, 1, new Object[] {"java/lang/Throwable"});
        methodVisitor.visitVarInsn(ASTORE, stackOffset);
        methodVisitor.visitVarInsn(ALOAD, stackOffset);
        
        methodVisitor.visitTypeInsn(INSTANCEOF, "java/lang/RuntimeException");
        labelElse = new Label();
        methodVisitor.visitJumpInsn(IFEQ, labelElse);
        methodVisitor.visitVarInsn(ALOAD, stackOffset);
        methodVisitor.visitTypeInsn(CHECKCAST, "java/lang/RuntimeException");
        methodVisitor.visitInsn(ATHROW);
        
        methodVisitor.visitLabel(labelElse);

        //desempaquetar los runtime exceptions
        methodVisitor.visitFrame(Opcodes.F_APPEND,1, new Object[] {"java/lang/Throwable"}, 0, null);
        methodVisitor.visitVarInsn(ALOAD, stackOffset); 
        methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Throwable", "getCause", "()Ljava/lang/Throwable;", false);
        methodVisitor.visitTypeInsn(INSTANCEOF, "java/lang/RuntimeException");
        labelElse = new Label();
        methodVisitor.visitJumpInsn(IFEQ, labelElse);
        methodVisitor.visitVarInsn(ALOAD, stackOffset);
        methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Throwable", "getCause", "()Ljava/lang/Throwable;", false);
        methodVisitor.visitTypeInsn(CHECKCAST, "java/lang/RuntimeException");
        methodVisitor.visitInsn(ATHROW);
        
        methodVisitor.visitLabel(labelElse);

        // armar un if para cada exception
        // boolean  append = true;
        for (String ex: methodExceptions) {
            labelElse = new Label();
            // if (append) {
            //     methodVisitor.visitFrame(Opcodes.F_APPEND,1, new Object[] {"java/lang/Throwable"}, 0, null);
            //     append = false;
            // } else {
                methodVisitor.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
            // }
            methodVisitor.visitVarInsn(ALOAD, stackOffset);
            methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Throwable", "getCause", "()Ljava/lang/Throwable;", false);
            methodVisitor.visitTypeInsn(INSTANCEOF, ex);
            
            methodVisitor.visitJumpInsn(IFEQ, labelElse);
            
            methodVisitor.visitVarInsn(ALOAD, stackOffset);
            methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Throwable", "getCause", "()Ljava/lang/Throwable;", false);
            methodVisitor.visitTypeInsn(CHECKCAST, ex);
            methodVisitor.visitInsn(ATHROW);

            methodVisitor.visitLabel(labelElse);
        }
        
        methodVisitor.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
        methodVisitor.visitLdcInsn(Type.getType("L"+clazzName+";"));
        methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Class", "getName", "()Ljava/lang/String;", false);
        methodVisitor.visitMethodInsn(INVOKESTATIC, "java/util/logging/Logger", "getLogger", "(Ljava/lang/String;)Ljava/util/logging/Logger;", false);
        methodVisitor.visitFieldInsn(GETSTATIC, "java/util/logging/Level", "SEVERE", "Ljava/util/logging/Level;");
        methodVisitor.visitInsn(ACONST_NULL);
        methodVisitor.visitVarInsn(ALOAD, stackOffset);
        methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "java/util/logging/Logger", "log", "(Ljava/util/logging/Level;Ljava/lang/String;Ljava/lang/Throwable;)V", false);

        methodVisitor.visitLabel(lblReturn);
        methodVisitor.visitFrame(Opcodes.F_CHOP, 1, null, 0, null);
        if (returnType.toAsm.equals("V")) {
            methodVisitor.visitInsn(RETURN);
        
        } else {
            
            methodVisitor.visitInsn(returnType.defaultValue);
            methodVisitor.visitInsn(returnType.asmReturnValue);
        }
        //int maxLocals = stackOffset+1; // getArgsLength(method.getParameterTypes())+1+(returnType.toAsm.equals("V")?1:0);
        //int maxStack = maxLocals + 5;
        methodVisitor.visitMaxs(0,0); //methodVisitor.visitMaxs(maxStack, maxLocals);
        //LOGGER.log(Level.FINEST,"visitMaxs("+maxStack+", "+maxLocals+")");
        
        methodVisitor.visitEnd();
        
        // ======================================================================================
        // ======================================================================================
        // ======================================================================================
        // crear el método que realmente invoca al super.
        LOGGER.log(Level.FINEST,"\n\nCreando la llamada a super...");
        LOGGER.log(Level.FINEST,"super.method: "+method.getName()+"$proxy" 
                    + methodDescriptor 
                    + "excepciones: "+ Arrays.toString(methodExceptions));
        methodVisitor = cw.visitMethod(ACC_PUBLIC, method.getName()+"$proxy", methodDescriptor, null, methodExceptions);
        methodVisitor.visitCode();
        
        // leer los parámetros
        methodVisitor.visitVarInsn(ALOAD, 0); // el primero es simpre "this"

        //methodVisitor.visitTypeInsn(ANEWARRAY, "java/lang/Object");
        
        // insetar todas los parametros en el vector. Para cada parámetro es necesario realuzar la conversión correspondiente.
        stackOffset = 1;
        for (int i = 0; i < method.getParameterCount(); i++) {
            LOGGER.log(Level.FINEST,"param: "+method.getParameters()[i].getType().toString());

            switch(method.getParameters()[i].getType().toString()) {
                case "boolean":
                    methodVisitor.visitVarInsn(ILOAD, stackOffset);
                    LOGGER.log(Level.FINEST,"boolean: ILOAD,"+stackOffset);
                    stackOffset+=1;
                    break;
                case "char":
                    methodVisitor.visitVarInsn(ILOAD, stackOffset);
                    LOGGER.log(Level.FINEST,"char: ILOAD,"+stackOffset);
                    stackOffset+=1;
                    break;
                case "byte":
                    methodVisitor.visitVarInsn(ILOAD, stackOffset);
                    LOGGER.log(Level.FINEST,"byte: ILOAD,"+stackOffset);
                    stackOffset+=1;
                    break;
                case "short":
                    methodVisitor.visitVarInsn(ILOAD, stackOffset);
                    LOGGER.log(Level.FINEST,"short: ILOAD,"+stackOffset);
                    stackOffset+=1;
                    break;
                case "int":
                    methodVisitor.visitVarInsn(ILOAD, stackOffset);
                    LOGGER.log(Level.FINEST,"int: ILOAD,"+stackOffset);
                    stackOffset+=1;
                    break;
                case "float":
                    methodVisitor.visitVarInsn(FLOAD, stackOffset);
                    LOGGER.log(Level.FINEST,"float: FLOAD,"+stackOffset);
                    stackOffset+=1;
                    break;
                case "long":
                    methodVisitor.visitVarInsn(LLOAD, stackOffset);
                    LOGGER.log(Level.FINEST,"long: LLOAD,"+stackOffset);
                    stackOffset+=2;
                    break;
                case "double":
                    methodVisitor.visitVarInsn(DLOAD, stackOffset);
                    LOGGER.log(Level.FINEST,"double: DLOAD,"+stackOffset);
                    stackOffset+=2;
                    break;
                default:
                    methodVisitor.visitVarInsn(ALOAD, stackOffset);
                    LOGGER.log(Level.FINEST,"class: ALOAD,"+stackOffset);
                    stackOffset+=1;
            }
        }
        
        
        methodVisitor.visitMethodInsn(INVOKESPECIAL, superName, method.getName(), methodDescriptor, false);
        LOGGER.log(Level.FINEST,"invokespecial: "+superName+" -> "+method.getName() 
                    + methodDescriptor 
                    );

        if (returnType.toAsm.equals("V")) {
            methodVisitor.visitInsn(RETURN);
        } else {
            // castear el resultado al tipo del retorno del método
            //methodVisitor.visitMethodInsn(INVOKEVIRTUAL, returnType.castClass, returnType.castMethod, returnType.descrptor, false);
            methodVisitor.visitInsn(returnType.asmReturnValue);
        }
        
        // el 1 corresponde a this
        //maxLocals = stackOffset; // getArgsLength(method.getParameterTypes())+1+(returnType.toAsm.equals("V")?1:0);
        //maxStack = maxLocals;
        //LOGGER.log(Level.FINEST,"visitMaxs("+maxStack+", "+maxLocals+")");
        LOGGER.log(Level.FINEST,"------------------------------------------------\n\n\n");
        
        // en classwriter el COMPUTE_FRAMES y COMPUTE_MAXS hacen los cálcules.
        methodVisitor.visitMaxs(0,0); //methodVisitor.visitMaxs(maxStack, maxLocals);
        methodVisitor.visitEnd();
    }

    private void generateInterceptorProxy(ClassWriter cw, String clazzName, String superName, Method method) {
        //ParType> [int, 
        //          class java.lang.Integer, 
        //          class asm.proxy.ASMFoo, 
        //          class [Lasm.proxy.ASMFoo;, 
        //          boolean, 
        //          float, 
        //          class [F, 
        //          class [[Ljava.lang.Float;
        //         ]
        // preparar la cadena de parametros
        LOGGER.log(Level.FINEST,"------------------------------------------------");
        LOGGER.log(Level.FINEST,"\n\n\nprocesando método de la interface: " + method.toString());
        LOGGER.log(Level.FINEST,"Type.getDescriptor: "+Type.getMethodDescriptor(method));
        String methodDescriptor = Type.getMethodDescriptor(method);
        
        LOGGER.log(Level.FINEST,"methodDescriptor: "+methodDescriptor);
        // calcular el tipo de return type
        TypeRef returnType = typesHelper.get(method.getReturnType().toString());
        if (returnType == null ) {
            // si es null quiere decir el tipo de retorno es una clase.
            returnType = new TypeRef("L"+method.getReturnType().toString().replace("class ","").replace("interface ","").replace(".", "/")+";",null,null,null,ARETURN,ACONST_NULL);
        }
        LOGGER.log(Level.FINEST,"returnType: "+returnType + "  --> "+method.getReturnType().toString());
        // determinar el cast para el valor devuelto por el interceptor 
        String returnCastType = "";
        if (typesHelper.get(method.getReturnType().toString()) == null){
            returnCastType = method.getReturnType().toString().replace("class ","").replace("interface ","").replace(".", "/");
        } else {
            returnCastType = typesHelper.get(method.getReturnType().toString()).castClass;
        }
        LOGGER.log(Level.FINEST,"return cast type: "+returnCastType);

        // procesar las excepciones
        String[] methodExceptions =  Arrays.stream(method.getGenericExceptionTypes())
                                            .map(t->t.getTypeName().replace("class ", "").replace(".", "/"))
                                            .toArray(size->new String[size]);
        LOGGER.log(Level.FINEST,"excepciones: "+Arrays.toString(methodExceptions));
        LOGGER.log(Level.FINEST,"");
        
        methodVisitor = cw.visitMethod(ACC_PUBLIC, method.getName(), methodDescriptor, null, methodExceptions);
        methodVisitor.visitCode();
        Label tryStart = new Label();
        Label tryEnd = new Label();
        Label catchThw = new Label();
        Label lblReturn = new Label();


        methodVisitor.visitTryCatchBlock(tryStart, tryEnd, catchThw, "java/lang/Throwable");
        methodVisitor.visitLabel(tryStart);
        methodVisitor.visitVarInsn(ALOAD, 0);
        methodVisitor.visitFieldInsn(GETFIELD, clazzName, PROXYNAME,
                "Lasm/proxy/IEasyProxyInterceptor;");
        methodVisitor.visitVarInsn(ALOAD, 0);
        methodVisitor.visitFieldInsn(GETSTATIC, clazzName, "proxiedMethods", "Ljava/util/HashMap;");
        methodVisitor.visitLdcInsn(method.getName()+Arrays.toString(method.getParameterTypes()));
        methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "java/util/HashMap", "get",
                "(Ljava/lang/Object;)Ljava/lang/Object;", false);
        methodVisitor.visitTypeInsn(CHECKCAST, "java/lang/reflect/Method");
        methodVisitor.visitFieldInsn(GETSTATIC, clazzName, "superMethods", "Ljava/util/HashMap;");
        // Fixme: acá se podría pasar null porque se corresponde a un método de la interface
        methodVisitor.visitLdcInsn(method.getName()+"$proxy"+Arrays.toString(method.getParameterTypes()));
        methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "java/util/HashMap", "get",
                "(Ljava/lang/Object;)Ljava/lang/Object;", false);
        methodVisitor.visitTypeInsn(CHECKCAST, "java/lang/reflect/Method");
        methodVisitor.visitIntInsn(BIPUSH,method.getParameterCount());
        methodVisitor.visitTypeInsn(ANEWARRAY, "java/lang/Object");
        
        // cuando se inserta un Long o Double es necesario saltar un lugar en la pila.
        int stackOffset = 1;
        // insetar todas los parametros en el vector. Para cada parámetro es necesario realuzar la conversión correspondiente.
        for (int i = 0; i < method.getParameterCount(); i++) {
            methodVisitor.visitInsn(DUP);
            methodVisitor.visitIntInsn(BIPUSH,i);
            switch(method.getParameters()[i].getType().toString()) {
                case "boolean":
                    methodVisitor.visitVarInsn(ILOAD, stackOffset);
                    stackOffset += 1;
                    methodVisitor.visitMethodInsn(INVOKESTATIC, "java/lang/Boolean", "valueOf", "(Z)Ljava/lang/Boolean;", false);
                    break;
                case "char":
                    methodVisitor.visitVarInsn(ILOAD, stackOffset);
                    stackOffset += 1;
                    methodVisitor.visitMethodInsn(INVOKESTATIC, "java/lang/Character", "valueOf", "(C)Ljava/lang/Character;", false);
                    break;
                case "byte":
                    methodVisitor.visitVarInsn(ILOAD, stackOffset);
                    stackOffset += 1;
                    methodVisitor.visitMethodInsn(INVOKESTATIC, "java/lang/Byte", "valueOf", "(B)Ljava/lang/Byte;", false);
                    break;
                case "short":
                    methodVisitor.visitVarInsn(ILOAD, stackOffset);
                    stackOffset += 1;
                    methodVisitor.visitMethodInsn(INVOKESTATIC, "java/lang/Short", "valueOf", "(S)Ljava/lang/Short;", false);
                    break;
                case "int":
                    methodVisitor.visitVarInsn(ILOAD, stackOffset);
                    stackOffset += 1;
                    methodVisitor.visitMethodInsn(INVOKESTATIC, "java/lang/Integer", "valueOf", "(I)Ljava/lang/Integer;", false);
                    break;
                case "float":
                    methodVisitor.visitVarInsn(FLOAD, stackOffset);
                    stackOffset += 1;
                    methodVisitor.visitMethodInsn(INVOKESTATIC, "java/lang/Float", "valueOf", "(F)Ljava/lang/Float;", false);
                    break;
                case "long":
                    methodVisitor.visitVarInsn(LLOAD, stackOffset);
                    stackOffset += 2;
                    methodVisitor.visitMethodInsn(INVOKESTATIC, "java/lang/Long", "valueOf", "(J)Ljava/lang/Long;", false);
                    break;
                case "double":
                    methodVisitor.visitVarInsn(DLOAD, stackOffset);
                    stackOffset += 2;
                    methodVisitor.visitMethodInsn(INVOKESTATIC, "java/lang/Double", "valueOf", "(D)Ljava/lang/Double;", false);
                    break;
                default:
                    methodVisitor.visitVarInsn(ALOAD, stackOffset);
                    stackOffset += 1;
            }
            methodVisitor.visitInsn(AASTORE);
        }

        // invocar al interceptor
        methodVisitor.visitMethodInsn(INVOKEINTERFACE, "asm/proxy/IEasyProxyInterceptor", "intercept",
                "(Ljava/lang/Object;Ljava/lang/reflect/Method;Ljava/lang/reflect/Method;[Ljava/lang/Object;)Ljava/lang/Object;",
                true);
        
        if (returnType.toAsm.equals("V")) {
            methodVisitor.visitInsn(POP);
            methodVisitor.visitLabel(tryEnd);
            methodVisitor.visitJumpInsn(GOTO, lblReturn);
            
        } else {
            // castear el resultado al tipo del retorno del método
            LOGGER.log(Level.FINEST,"el método retorna valores. Preparar el cast");
            methodVisitor.visitTypeInsn(CHECKCAST, returnCastType);
            LOGGER.log(Level.FINEST,"checkcast: "+returnCastType);
            if (returnType.castMethod!=null) {
                methodVisitor.visitMethodInsn(INVOKEVIRTUAL, returnType.castClass, returnType.castMethod, returnType.descrptor, false);
            }
            methodVisitor.visitLabel(tryEnd);
            methodVisitor.visitInsn(returnType.asmReturnValue);
        }

        // iniciar el catch del Throwable
        methodVisitor.visitLabel(catchThw);
        methodVisitor.visitFrame(Opcodes.F_SAME1, 0, null, 1, new Object[] {"java/lang/Throwable"});
        methodVisitor.visitVarInsn(ASTORE, stackOffset);
        methodVisitor.visitVarInsn(ALOAD, stackOffset);
        
        // runtime exceptions
        methodVisitor.visitTypeInsn(INSTANCEOF, "java/lang/RuntimeException");
        Label labelElse = new Label();
        methodVisitor.visitJumpInsn(IFEQ, labelElse);
        methodVisitor.visitVarInsn(ALOAD, stackOffset);
        methodVisitor.visitTypeInsn(CHECKCAST, "java/lang/RuntimeException");
        methodVisitor.visitInsn(ATHROW);
        
        methodVisitor.visitLabel(labelElse);

        // armar un if para cada exception
        boolean  append = true;
        for (String ex: methodExceptions) {
            labelElse = new Label();
            if (append) {
                methodVisitor.visitFrame(Opcodes.F_APPEND,1, new Object[] {"java/lang/Throwable"}, 0, null);
                append = false;
            } else {
                methodVisitor.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
            }
            methodVisitor.visitVarInsn(ALOAD, stackOffset);
            methodVisitor.visitTypeInsn(INSTANCEOF, ex);
            
            methodVisitor.visitJumpInsn(IFEQ, labelElse);
            
            methodVisitor.visitVarInsn(ALOAD, stackOffset);
            methodVisitor.visitTypeInsn(CHECKCAST, ex);
            methodVisitor.visitInsn(ATHROW);

            methodVisitor.visitLabel(labelElse);
        }
        
        methodVisitor.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
        methodVisitor.visitLdcInsn(Type.getType("L"+clazzName+";"));
        methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Class", "getName", "()Ljava/lang/String;", false);
        methodVisitor.visitMethodInsn(INVOKESTATIC, "java/util/logging/Logger", "getLogger", "(Ljava/lang/String;)Ljava/util/logging/Logger;", false);
        methodVisitor.visitFieldInsn(GETSTATIC, "java/util/logging/Level", "SEVERE", "Ljava/util/logging/Level;");
        methodVisitor.visitInsn(ACONST_NULL);
        methodVisitor.visitVarInsn(ALOAD, stackOffset);
        methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "java/util/logging/Logger", "log", "(Ljava/util/logging/Level;Ljava/lang/String;Ljava/lang/Throwable;)V", false);
        
        methodVisitor.visitLabel(lblReturn);
        methodVisitor.visitFrame(Opcodes.F_CHOP, 1, null, 0, null);
        if (returnType.toAsm.equals("V")) {
            methodVisitor.visitInsn(RETURN);
        
        } else {
            
            methodVisitor.visitInsn(returnType.defaultValue);
            methodVisitor.visitInsn(returnType.asmReturnValue);
        }
        //int maxLocals = stackOffset+1; // getArgsLength(method.getParameterTypes())+1+(returnType.toAsm.equals("V")?1:0);
        //int maxStack = maxLocals + 5;
        methodVisitor.visitMaxs(0,0); //methodVisitor.visitMaxs(maxStack, maxLocals);
        //LOGGER.log(Level.FINEST,"visitMaxs("+maxStack+", "+maxLocals+")");
        
        methodVisitor.visitEnd();
        
    }



    /**
     * Get the total length of args in local variable space. Note long and double
     * types take one
     * additional slot.
     *
     * @param parameterTypes
     *
     * @return
     */
    private int getArgsLength(Class[] parameterTypes) {
        int length = parameterTypes.length;
        for (Class type : parameterTypes) {
            String typeStr = type.getName();
            if (typeStr.equals("long") || typeStr.equals("double")) {
                length++;
            }
        }
        return length;
    }

    /**
     * Herramienta para realizar un volcado de la clase a disco.
     *
     * @param className   nombre del archivo a graba
     * @param myByteArray datos de la clase.
     */
    private void writeToFile(String className, byte[] myByteArray) {
        LOGGER.log(Level.FINER, "Escribiendo archivo a disco en /tmp/asm");
        try {
            File theDir = new File(this.outputDirectory);
            if (!theDir.exists()) {
                theDir.mkdir();
            }

            FileOutputStream fos = new FileOutputStream(
                    this.outputDirectory + className.substring(className.lastIndexOf("/")) + ".class");
            fos.write(myByteArray);
            fos.close();

        } catch (IOException ex) {
            Logger.getLogger(EasyProxy.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
    }

   
    
}
