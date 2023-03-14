/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package asm.proxy;

import java.lang.reflect.InvocationTargetException;
import java.util.logging.Level;
import java.util.logging.Logger;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;
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
        ObjectProxyImpl op = new ObjectProxyImpl();
        ASMFooTarget asmt = new ASMFooTarget(op);
        // asmt.setS("test");
        // asmt = new ASMFooTarget(op);
        // asmt.setS("test 2");
        
        try {
            asmt.testRuntimeException();
        } catch (Throwable ex) {
            if (ex instanceof RuntimeException) {
                System.out.println("\n\n\n\n\n\nATRAPADA RUNTIME\n\n\n\n\n\n");
            } else {
                System.out.println("\n\n\n\nERROR - ACA ESTÁ MAL!");
                ex.printStackTrace();
            }
        } 
        

        try {
            asmt.testException(1);
        } catch (ExceptionTest e) {
            System.out.println("Excepción capturada!! ");
            e.printStackTrace();
        } catch (Exception e) {
            System.out.println("\n\n\n\nERROR - ACA ESTÁ MAL!");
            e.printStackTrace();
        }

        try {
            asmt.testException(2);
        } catch (ExceptionTest2 e) {
            System.out.println("Excepción 2 capturada!! ");
            e.printStackTrace();
        } catch (Exception e) {
            System.out.println("\n\n\n\nERROR - ACA ESTÁ MAL!");
            e.printStackTrace();
        }
    }

    @Test
    public void testASM() {
        System.out.println("=================================================================");
        System.out.println("=================================================================");
        System.out.println("\n\n\n\ntestASM");
        System.out.println("=================================================================");
        System.out.println("=================================================================");
        
        try {
            
            EasyProxy ep = new EasyProxy();
            ObjectProxyImpl op = new ObjectProxyImpl();
            Class<ASMFoo> ft = (Class<ASMFoo>)ep.getProxyClass(ASMFoo.class, IObjectProxy.class);
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
        System.out.println("=================================================================");
        System.out.println("=================================================================");
        System.out.println("\n\n\n\ntest class Extentions");
        System.out.println("=================================================================");
        System.out.println("=================================================================");
        
        try {
            // precargar la clase para que el classloader tenga una referencia.
            // esto es para verificar que se inserte en un classloader exsitente.
            ASMFooEx asm = new ASMFooEx();

            ObjectProxyImpl op = new ObjectProxyImpl();
            Class<ASMFooEx> ft = new EasyProxy().getProxyClass(ASMFooEx.class, IObjectProxy.class);
            System.out.println("clase generada!!");
            System.out.println("");
            System.out.println("");
            System.out.println("crear una instancia...");
            ASMFooEx  fti = ft.getConstructor(IEasyProxyInterceptor.class).newInstance(op);

            // Field fsuper = fti.getClass().getField("superMethods");
            // HashMap<String,Method> superMethods = (HashMap<String, Method>) fsuper.get(null);
            // System.out.println(superMethods);
            // fti.setS("xxxxxxx");

            // invocar un método de la interfaz 
            ((IObjectProxy)fti).___sayHello("ASM");
            assertTrue(((IObjectProxy)fti).___getLast().equals("EasyProxy"));

            assertTrue(fti.toOverride(100) == 100);
            
            // sobreescribe completamente el método anterior y cambia el varlor de retorno.
            assertTrue( fti.toOverride2(100) == 100);

        } catch (NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
            Logger.getLogger(ProxyTest.class.getName()).log(Level.SEVERE, null, ex);
        }
    }


    @Test
    public void testCache() {
        System.out.println("=================================================================");
        System.out.println("=================================================================");
        System.out.println("\n\n\n\ntest class Cache");
        System.out.println("=================================================================");
        System.out.println("=================================================================");
        
        try {
            // precargar la clase para que el classloader tenga una referencia.
            // esto es para verificar que se inserte en un classloader exsitente.
            ASMFooEx asm = new ASMFooEx();

            ObjectProxyImpl op = new ObjectProxyImpl();

            TypesCache tc = new TypesCache();

            ASMFooEx ft = tc.findOrInsert(ASMFooEx.class, IObjectProxy.class, ()->{
                                    Class clazz = new EasyProxy().getProxyClass(ASMFooEx.class, IObjectProxy.class);
                                    return clazz;
                                }).newInstance(op);
            System.out.println("clase generada!!");
            
            assertTrue(tc.getCachedClassesCount()==1);
            
            // verificar que tenga implementada la interface
            assertTrue(ft instanceof IObjectProxy);

            // Field fsuper = fti.getClass().getField("superMethods");
            // HashMap<String,Method> superMethods = (HashMap<String, Method>) fsuper.get(null);
            // System.out.println(superMethods);
            // ft.setS("xxxxxxx");


            // invocar un método de la interfaz 
            ((IObjectProxy)ft).___sayHello("ASM");
            assertTrue(((IObjectProxy)ft).___getLast().equals("EasyProxy"));

            assertTrue(ft.toOverride(100) == 100);
            
            // sobreescribe completamente el método anterior y cambia el varlor de retorno.
            assertTrue( ft.toOverride2(100) == 100);
            
            
            // pedir otra instancia de la misma clase
            ft = tc.findOrInsert(ASMFooEx.class, IObjectProxy.class, ()->{
                                    Class clazz = new EasyProxy().getProxyClass(ASMFooEx.class, IObjectProxy.class);
                                    return clazz;
                                }).newInstance(op);
            
            assertTrue(tc.getCachedClassesCount()==1);
            
            

        } catch (NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | XDuplicatedProxyClass ex) {
            Logger.getLogger(ProxyTest.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    
    @Test
    public void testInterfaces() {
        System.out.println("=================================================================");
        System.out.println("=================================================================");
        System.out.println("\n\n\n\ntest interfaces");
        System.out.println("=================================================================");
        System.out.println("=================================================================");
        
        try {
            // precargar la clase para que el classloader tenga una referencia.
            // esto es para verificar que se inserte en un classloader exsitente.
            ASMFooEx asm = new ASMFooEx();

            ObjectProxyImpl op = new ObjectProxyImpl();

            TypesCache tc = new TypesCache();

            ASMFooEx ft = tc.findOrInsert(ASMFooEx.class, IObjectProxy.class, ()->{
                                    Class clazz = new EasyProxy().getProxyClass(ASMFooEx.class, IObjectProxy.class);
                                    return clazz;
                                }).newInstance(op);
            System.out.println("clase generada!!");
            
            IObjectProxy iop = ((IObjectProxy)ft).___getInterface();
            
            assertNotNull(iop);

        } catch (NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | XDuplicatedProxyClass ex) {
            Logger.getLogger(ProxyTest.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    

    @Test
    public void testExceptions() {
        System.out.println("=================================================================");
        System.out.println("=================================================================");
        System.out.println("\n\n\n\ntest exceptions");
        System.out.println("=================================================================");
        System.out.println("=================================================================");
        
        try {
            // precargar la clase para que el classloader tenga una referencia.
            // esto es para verificar que se inserte en un classloader exsitente.
            ASMFooEx asm = new ASMFooEx();

            ObjectProxyImpl op = new ObjectProxyImpl();

            TypesCache tc = new TypesCache();

            ASMFooEx ft = tc.findOrInsert(ASMFooEx.class, IObjectProxy.class, ()->{
                                    Class clazz = new EasyProxy().getProxyClass(ASMFooEx.class, IObjectProxy.class);
                                    return clazz;
                                }).newInstance(op);
            System.out.println("clase generada!!");
            
            assertThrows(RuntimeExceptionTest.class,()->ft.testRuntimeException());

        } catch (NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | XDuplicatedProxyClass ex) {
            Logger.getLogger(ProxyTest.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    
    

    public static void main(String[] args) {
        ProxyTest t = new ProxyTest();
        
        t.testTarget();

//        t.testASM();
//        t.testEx();
//        t.testCache();
        
    }
}
