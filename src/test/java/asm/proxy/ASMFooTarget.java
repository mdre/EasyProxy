/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package asm.proxy;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Marcelo D. Ré {@literal <marcelo.re@gmail.com>}
 */
public class ASMFooTarget extends ASMFoo implements IObjectProxy {
    
    String s;
    transient int tras;
    int soloint;

    IEasyProxyInterceptor epi;
    // Métodos que invocan a super con el código original
    private static HashMap<String,Method> superMethods = new HashMap<>();
    // Método que es derivado al proxy
    private static HashMap<String,Method> proxiedMethods = new HashMap<>();
    static {
        // recupear los métodos de la superclass
        for (java.lang.reflect.Method method : ASMFoo.class.getMethods()) {
            if (!method.isSynthetic()) {
                proxiedMethods.put(method.toString(), method);
                System.out.println(">> "+method.toString()
                                   + "\n  - ParType> "+ Arrays.toString(method.getParameterTypes())
                                   + "\n  - parametes "+ Arrays.toString(method.getParameters())
                                   + "\n  - exceptions "+ Arrays.toString(method.getGenericExceptionTypes())
                                   + "\n  - exceptions "+ Arrays.toString(method.getExceptionTypes())
                                   + "\n  - return type "+ method.getReturnType().getName()
                                   + "\n"

                );
                for (int i = 0; i < method.getParameterCount(); i++) {
                    System.out.println(" >>> " +  method.getParameters()[i].getType().toString() );
                }
            }
        }
        
        for (java.lang.reflect.Method method : ASMFooTarget.class.getInterfaces()[0].getMethods()) {
            if (!method.isSynthetic() && proxiedMethods.get(method.toString())==null) {
                proxiedMethods.put(method.toString(), method);
                // System.out.println(">> "+method.toString()+
                //                    "  - anotations: "+ Arrays.toString(method.getAnnotations())
                // );
            }
        }
        
        // System.out.println("\n\n\n");
        // referenciar los métodos proxy
        for (java.lang.reflect.Method method : ASMFooTarget.class.getMethods()) {
            if (method.getName().endsWith("$proxy")) {
                superMethods.put(method.toString(), method);
                // System.out.println(">> "+method.toString()+
                //                    "  - anotations: "+ Arrays.toString(method.getAnnotations())
                // );
                
            }
        }

        
    }

    public ASMFooTarget(IEasyProxyInterceptor epi) {
        System.out.println("inicializando... ");
        this.epi = epi;
        
        System.out.println("fin constructor.");
    }
    
    @Override
    public void setS(String s) {
        try {
            epi.intercept(this,
                          proxiedMethods.get("public void asm.proxy.ASMFoo.setS(java.lang.String)"),
                          superMethods.get("public void asm.proxy.ASMFooTarget.setS$proxy(java.lang.String)"),
                          s);
        } catch (Throwable ex) {
            Logger.getLogger(ASMFooTarget.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void setS$proxy(String s) {
        super.setS(s);
    }

    @Override
    public int compareTo(ASMFoo t) {
        try {
            return (int)epi.intercept(this,
                          proxiedMethods.get("public asm.proxy.ASMFoo asm.proxy.ASMFoo.unparam(int)"),
                          superMethods.get("public asm.proxy.ASMFoo asm.proxy.ASMFoo.setValues(int)"),
                          t);
        } catch (Throwable ex) {
            Logger.getLogger(ASMFooTarget.class.getName()).log(Level.SEVERE, null, ex);
        }
        return 0;
    }

    public int compareTo$proxy(ASMFoo t) {
        return super.compareTo(t);
    }


    @Override
    public ASMFoo setValues(int i, Integer II, ASMFoo foo, ASMFoo[] fooV, boolean b, float f,  float[] f1, Float[][] f2) throws Exception {
        try {
            return (ASMFoo)epi.intercept(this,
                          proxiedMethods.get("public asm.proxy.ASMFoo asm.proxy.ASMFoo.setValues(int,java.lang.Integer,asm.proxy.ASMFoo,boolean) throws java.lang.Exception"),
                          superMethods.get("public asm.proxy.ASMFoo asm.proxy.ASMFoo.setValues(int,java.lang.Integer,asm.proxy.ASMFoo,boolean) throws java.lang.Exception"),
                          i, II, foo, fooV, b, f, f1, f2);
        } catch (Throwable ex) {
            Logger.getLogger(ASMFooTarget.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public ASMFoo setValues$proxy(int i, Integer II, ASMFoo foo, ASMFoo[] fooV, boolean b, float f,  float[] f1, Float[][] f2) throws Exception{
        return super.setValues(i, II, foo, fooV, b, f, f1, f2);
    }

    @Override
    public void sayHello(String s) {
        // TODO Auto-generated method stub
        try {
            epi.intercept(this,
                          proxiedMethods.get("public void asm.proxy.ASMFoo.setS(java.lang.String)"),
                          null,
                          s);
        } catch (Throwable ex) {
            Logger.getLogger(ASMFooTarget.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public boolean noParam() throws Exception, IOException {
        try {
            return (Boolean)epi.intercept(this,
                          proxiedMethods.get("public asm.proxy.ASMFoo asm.proxy.ASMFoo.unparam(int)"),
                          superMethods.get("public asm.proxy.ASMFoo asm.proxy.ASMFoo.setValues(int)")
                          );
        } catch (Throwable ex) {
            Logger.getLogger(ASMFooTarget.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }


    @Override
    public boolean unparam(int i) {
        try {
            return (Boolean)epi.intercept(this,
                          proxiedMethods.get("public asm.proxy.ASMFoo asm.proxy.ASMFoo.unparam(int)"),
                          superMethods.get("public asm.proxy.ASMFoo asm.proxy.ASMFoo.setValues(int)"),
                          i);
        } catch (Throwable ex) {
            Logger.getLogger(ASMFooTarget.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    @Override
    public boolean dosparam(int i, Integer II) {
        try {
            return (Boolean)epi.intercept(this,
                          proxiedMethods.get("public asm.proxy.ASMFoo asm.proxy.ASMFoo.unparam(int)"),
                          superMethods.get("public asm.proxy.ASMFoo asm.proxy.ASMFoo.setValues(int)"),
                          i, II);
        } catch (Throwable ex) {
            Logger.getLogger(ASMFooTarget.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }
    
    @Override
    public boolean tresparam(int i, Integer II, float f) {
        try {
            return (Boolean)epi.intercept(this,
                          proxiedMethods.get("public asm.proxy.ASMFoo asm.proxy.ASMFoo.unparam(int)"),
                          superMethods.get("public asm.proxy.ASMFoo asm.proxy.ASMFoo.setValues(int)"),
                          i, II, f);
        } catch (Throwable ex) {
            Logger.getLogger(ASMFooTarget.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    @Override
    public int muchosparam(boolean b1, char c2, byte b3, short s4, int i5, float f6, long l7, double d8, String s9,  Double... D10) {

        try {
            return (int)epi.intercept(this,
                          proxiedMethods.get("public asm.proxy.ASMFoo asm.proxy.ASMFoo.unparam(int)"),
                          superMethods.get("public asm.proxy.ASMFoo asm.proxy.ASMFoo.setValues(int)"),
                          b1, c2, b3, s4, i5, f6, l7, d8, s9, D10);
        } catch (Throwable ex) {
            Logger.getLogger(ASMFooTarget.class.getName()).log(Level.SEVERE, null, ex);
        }
        return 0;
    }

    public int muchosparam$proxy(boolean b1, char c2, byte b3, short s4, int i5, float f6, long l7, double d8, String s9,  Double... D10) {
        return super.muchosparam(b1, c2, b3, s4, i5, f6, l7, d8, s9, D10);
    }


    public boolean retBol() {
        try {
            return (boolean)epi.intercept(this,
                          proxiedMethods.get("public asm.proxy.ASMFoo asm.proxy.ASMFoo.unparam(int)"),
                          superMethods.get("public asm.proxy.ASMFoo asm.proxy.ASMFoo.setValues(int)")
                          );
        } catch (Throwable ex) {
            Logger.getLogger(ASMFooTarget.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }
    public char retChar(){
        try {
            return (char)epi.intercept(this,
                          proxiedMethods.get("public asm.proxy.ASMFoo asm.proxy.ASMFoo.unparam(int)"),
                          superMethods.get("public asm.proxy.ASMFoo asm.proxy.ASMFoo.setValues(int)"));
        } catch (Throwable ex) {
            Logger.getLogger(ASMFooTarget.class.getName()).log(Level.SEVERE, null, ex);
        }
        return 0;
    }
    public byte retByte(){
        try {
            return (byte)epi.intercept(this,
                          proxiedMethods.get("public asm.proxy.ASMFoo asm.proxy.ASMFoo.unparam(int)"),
                          superMethods.get("public asm.proxy.ASMFoo asm.proxy.ASMFoo.setValues(int)"));
        } catch (Throwable ex) {
            Logger.getLogger(ASMFooTarget.class.getName()).log(Level.SEVERE, null, ex);
        }
        return 0;
    }

    public short retShort(){
        try {
            return (short)epi.intercept(this,
                          proxiedMethods.get("public asm.proxy.ASMFoo asm.proxy.ASMFoo.unparam(int)"),
                          superMethods.get("public asm.proxy.ASMFoo asm.proxy.ASMFoo.setValues(int)"));
        } catch (Throwable ex) {
            Logger.getLogger(ASMFooTarget.class.getName()).log(Level.SEVERE, null, ex);
        }
        return 0;
    }
    public int retInt(){
        try {
            return (int)epi.intercept(this,
                          proxiedMethods.get("public asm.proxy.ASMFoo asm.proxy.ASMFoo.unparam(int)"),
                          superMethods.get("public asm.proxy.ASMFoo asm.proxy.ASMFoo.setValues(int)"));
        } catch (Throwable ex) {
            Logger.getLogger(ASMFooTarget.class.getName()).log(Level.SEVERE, null, ex);
        }
        return 0;
    }
    public float retFloat(){
        try {
            return (float)epi.intercept(this,
                          proxiedMethods.get("public asm.proxy.ASMFoo asm.proxy.ASMFoo.unparam(int)"),
                          superMethods.get("public asm.proxy.ASMFoo asm.proxy.ASMFoo.setValues(int)"));
        } catch (Throwable ex) {
            Logger.getLogger(ASMFooTarget.class.getName()).log(Level.SEVERE, null, ex);
        }
        return 0;
    }
    public long retLong(){
        try {
            return (long)epi.intercept(this,
                          proxiedMethods.get("public asm.proxy.ASMFoo asm.proxy.ASMFoo.unparam(int)"),
                          superMethods.get("public asm.proxy.ASMFoo asm.proxy.ASMFoo.setValues(int)"));
        } catch (Throwable ex) {
            Logger.getLogger(ASMFooTarget.class.getName()).log(Level.SEVERE, null, ex);
        }
        return 0;
    }
    public double retDouble(){
        try {
            return (double)epi.intercept(this,
                          proxiedMethods.get("public asm.proxy.ASMFoo asm.proxy.ASMFoo.unparam(int)"),
                          superMethods.get("public asm.proxy.ASMFoo asm.proxy.ASMFoo.setValues(int)"));
        } catch (Throwable ex) {
            Logger.getLogger(ASMFooTarget.class.getName()).log(Level.SEVERE, null, ex);
        }
        return 0;
    }

    public String retString(){
        try {
            return (String)epi.intercept(this,
                          proxiedMethods.get("public asm.proxy.ASMFoo asm.proxy.ASMFoo.unparam(int)"),
                          superMethods.get("public asm.proxy.ASMFoo asm.proxy.ASMFoo.setValues(int)"));
        } catch (Throwable ex) {
            Logger.getLogger(ASMFooTarget.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    public Double[] retArrDouble() {
        try {
            return (Double[])epi.intercept(this,
                          proxiedMethods.get("public asm.proxy.ASMFoo asm.proxy.ASMFoo.unparam(int)"),
                          superMethods.get("public asm.proxy.ASMFoo asm.proxy.ASMFoo.setValues(int)"));
        } catch (Throwable ex) {
            Logger.getLogger(ASMFooTarget.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    public ASMFoo retObjectr() {
        try {
            return (ASMFoo)epi.intercept(this,
                          proxiedMethods.get("public asm.proxy.ASMFoo asm.proxy.ASMFoo.unparam(int)"),
                          superMethods.get("public asm.proxy.ASMFoo asm.proxy.ASMFoo.setValues(int)"));
        } catch (Throwable ex) {
            Logger.getLogger(ASMFooTarget.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    public void retVoid() {
        try {
            epi.intercept(this,
                          proxiedMethods.get("public asm.proxy.ASMFoo asm.proxy.ASMFoo.unparam(int)"),
                          superMethods.get("public asm.proxy.ASMFoo asm.proxy.ASMFoo.setValues(int)"));
        } catch (Throwable ex) {
            Logger.getLogger(ASMFooTarget.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
