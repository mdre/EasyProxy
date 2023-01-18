/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package asm.proxy;

import java.io.IOException;

/**
 *
 * @author Marcelo D. Ré {@literal <marcelo.re@gmail.com>}
 */
public class ASMFoo implements Comparable<ASMFoo> {

    String s;
    
    public ASMFoo() {
    }

    @Deprecated
    public void setS(String s) {
        this.s = s;
        System.out.println("ASMFoo.setS ");
    }

    @Override
    public int compareTo(ASMFoo t) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
    
    public ASMFoo setValues(int i, Integer II, ASMFoo foo, ASMFoo[] fooV, boolean b, float f, float[] f1, Float[][] f2) throws Exception {
        
        return this;
    }

    public boolean noParam() throws Exception, IOException {
        return true;
    }

    public boolean unparam(int i) {
        return true;
    }
    
    public boolean dosparam(int i, Integer II) {
        return true;
    }

    public boolean tresparam(int i, Integer II, float f) {
        return true;
    }

    public int muchosparam(boolean b1, char c2, byte b3, short s4, int i5, float f6, long l7, double d8, String s9,  Double... D10) {
        return 0;
    }

    public int toOverride(int i) {
        // método para pobar la llamada directa desde una superclase.
        return i;
    }

    public int toOverride2(int i) {
        // método para probar que el overide bloquea este código
        return i+10;
    }
}
