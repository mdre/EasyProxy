/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package asm.proxy;

import static org.junit.Assert.assertTrue;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.junit.Test;

/**
 *
 * @author Marcelo D. Ré {@literal <marcelo.re@gmail.com>}
 */
public class ProxyTest {
    private final static Logger LOGGER = Logger.getLogger(ProxyTest.class.getName());
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

    @Test
    public void testASM() {
        try {
            
            System.out.println("\n\n\n\ntestASM");
            EasyProxy ep = new EasyProxy();
            ObjectProxyImpl op = new ObjectProxyImpl();
            Class<ASMFoo> ft = (Class<ASMFoo>)ep.getClassProxy(ASMFoo.class, IObjectProxy.class);
            System.out.println("clase generada!!");
            System.out.println("");
            System.out.println("");
            System.out.println("crear una instancia...");
            ASMFoo  fti = ft.getConstructor(IEasyProxyInterceptor.class).newInstance(op);

            // Field fsuper = fti.getClass().getField("superMethods");
            // HashMap<String,Method> superMethods = (HashMap<String, Method>) fsuper.get(null);
            // System.out.println(superMethods);
            fti.setS("xxxxxxx");

            // invocar un método de la interfaz 
            ((IObjectProxy)fti).___sayHello("ASM");
            String result = ((IObjectProxy)fti).___getLast();
            assertTrue(result == "EasyProxy");

        } catch (NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
            Logger.getLogger(ProxyTest.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    @Test
    public void testEx() {
        try {
            System.out.println("\n\n\n\ntest class Extentions");
            EasyProxy ep = new EasyProxy();
            ObjectProxyImpl op = new ObjectProxyImpl();
            Class<ASMFooEx> ft = (Class<ASMFooEx>)ep.getClassProxy(ASMFooEx.class, IObjectProxy.class);
            System.out.println("clase generada!!");
            System.out.println("");
            System.out.println("");
            System.out.println("crear una instancia...");
            ASMFooEx  fti = ft.getConstructor(IEasyProxyInterceptor.class).newInstance(op);

            // Field fsuper = fti.getClass().getField("superMethods");
            // HashMap<String,Method> superMethods = (HashMap<String, Method>) fsuper.get(null);
            // System.out.println(superMethods);
            fti.setS("xxxxxxx");

            // invocar un método de la interfaz 
            ((IObjectProxy)fti).___sayHello("ASM");
            assertTrue(((IObjectProxy)fti).___getLast().equals("EasyProxy"));

            assertTrue(fti.toOverride(100) == 100);
            
            assertTrue(fti.toOverride2(100) == 100);

        } catch (NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
            Logger.getLogger(ProxyTest.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    
    public static void main(String[] args) {
        ProxyTest t = new ProxyTest();
        //t.testTarget();
        
        //t.testASM();
        t.testEx();
    }
}
