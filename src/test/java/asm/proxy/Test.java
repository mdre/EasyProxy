/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package asm.proxy;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Marcelo D. Ré {@literal <marcelo.re@gmail.com>}
 */
public class Test {
    private final static Logger LOGGER = Logger.getLogger(Test.class.getName());
    static {
        if (LOGGER.getLevel() == null) {
            LOGGER.setLevel(Level.FINEST);
        }
    }
    
    private void testTarget() { 
        EasyProxy ep = new EasyProxy();
        ObjectProxyImpl op = new ObjectProxyImpl();
        ASMFooTarget asmt = new ASMFooTarget(op);
        asmt.setS("test");
        asmt = new ASMFooTarget(op);
        asmt.setS("test 2");
    }

    private void testASM() {
        try {
            
            System.out.println("\n\n\n\ntestASM");
            EasyProxy ep = new EasyProxy();
            ObjectProxyImpl op = new ObjectProxyImpl();
            Class<ASMFooTarget> ft = (Class<ASMFooTarget>)ep.getClassProxy(ASMFoo.class, IObjectProxy.class);
            System.out.println("clase generada!!");
            System.out.println("");
            System.out.println("");
            System.out.println("crear una instancia...");
            ASMFoo  fti = ft.getConstructor(IEasyProxyInterceptor.class).newInstance(op);

            // Field fsuper = fti.getClass().getField("superMethods");
            // HashMap<String,Method> superMethods = (HashMap<String, Method>) fsuper.get(null);
            // System.out.println(superMethods);
            fti.setS("xxxxxxx");
        } catch (NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
            Logger.getLogger(Test.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    
    public static void main(String[] args) {
        Test t = new Test();
        //t.testTarget();
        
        t.testASM();
    }
}
