/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package asm.proxy;

import java.lang.reflect.InvocationTargetException;
import java.util.logging.Level;
import java.util.logging.Logger;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Test;

/**
 *
 * @author Marcelo D. Ré {@literal <marcelo.re@gmail.com>}
 */
public class ProxyTest {
    private final static Logger LOGGER = Logger.getLogger(ProxyTest.class.getName());
    static {
        if (LOGGER.getLevel() == null) {
            LOGGER.setLevel(Level.INFO);
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

        System.out.println("\n\n\nTest Interface Exception");
        try {
            asmt.___testException(1);
        } catch (ExceptionTest e) {
            System.out.println("Excepción capturada!! ");
            e.printStackTrace();
        } catch (Exception e) {
            System.out.println("\n\n\n\nERROR - ACA ESTÁ MAL!");
            e.printStackTrace();
        }

        try {
            asmt.___testRuntimeException();
        } catch (RuntimeExceptionTest e) {
            System.out.println("Runtime Excepción capturada!! ");
            e.printStackTrace();
        } 
    }

    @Test
    public void testASM() {
        System.out.println("\n\n\n\n");
        System.out.println("=================================================================");
        System.out.println("=================================================================");
        System.out.println("testASM");
        System.out.println("=================================================================");
        System.out.println("=================================================================");
        
        try {
            
            EasyProxy ep = new EasyProxy().setOutputDirectory("/tmp/asm");
            ObjectProxyImpl op = new ObjectProxyImpl();
            Class<ASMFoo> ft = (Class<ASMFoo>)ep.getProxyClass(ASMFoo.class, IObjectProxy.class);
            System.out.println("clase generada!!");
            System.out.println("");
            System.out.println("");
            System.out.println("crear una instancia...");
            ASMFoo  fti = ft.getConstructor(IEasyProxyInterceptor.class)
                                .newInstance(op);

            // Field fsuper = fti.getClass().getField("superMethods");
            // HashMap<String,Method> superMethods = (HashMap<String, Method>) fsuper.get(null);
            // System.out.println(superMethods);
            fti.setS("xxxxxxx");

            // invocar un método de la interfaz 
            ((IObjectProxy)fti).___sayHello("ASM");
            String result = ((IObjectProxy)fti).___getLast();
            assertTrue(result == "EasyProxy");


            // verioficar que las anotación existan
            assertEquals("Annotation Present", fti.testAnnotation());

        } catch (NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
            Logger.getLogger(ProxyTest.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    @Test
    public void testEx() {
        System.out.println("\n\n\n\n");
        System.out.println("=================================================================");
        System.out.println("=================================================================");
        System.out.println("test class Extentions");
        System.out.println("=================================================================");
        System.out.println("=================================================================");
        
        try {
            // precargar la clase para que el classloader tenga una referencia.
            // esto es para verificar que se inserte en un classloader exsitente.
            ASMFooEx asm = new ASMFooEx();

            ObjectProxyImpl op = new ObjectProxyImpl();
            Class<ASMFooEx> ft = new EasyProxy().setOutputDirectory("/tmp/asm").getProxyClass(ASMFooEx.class, IObjectProxy.class);
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
    public void testExEx() {
        System.out.println("\n\n\n\n");
        System.out.println("=================================================================");
        System.out.println("=================================================================");
        System.out.println("test class Extentions on a Extentions with a non-null constructor");
        System.out.println("=================================================================");
        System.out.println("=================================================================");
        
        try {
            // precargar la clase para que el classloader tenga una referencia.
            // esto es para verificar que se inserte en un classloader exsitente.
            // ASMFooEx asm = new ASMFooEx();

            ObjectProxyImpl op = new ObjectProxyImpl();
            Class<ASMFooExEx> ft = new EasyProxy()
                                        .setOutputDirectory("/tmp/asm")
                                        .getProxyClass(ASMFooExEx.class, IObjectProxy.class);
            System.out.println("clase generada!!");
            System.out.println("");
            System.out.println("");
            System.out.println("crear una instancia...");
            ASMFooExEx  fti = ft.getConstructor(IEasyProxyInterceptor.class).newInstance(op);

            assertEquals("ASMFooExEx",fti.s);
            assertEquals(2, fti.i);
        } catch (NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
            Logger.getLogger(ProxyTest.class.getName()).log(Level.SEVERE, null, ex);
        }
    }


    @Test
    public void testCache() {
        System.out.println("\n\n\n\n");
        System.out.println("=================================================================");
        System.out.println("=================================================================");
        System.out.println("test class Cache");
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
        System.out.println("\n\n\n\n");
        System.out.println("=================================================================");
        System.out.println("=================================================================");
        System.out.println("test interfaces");
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
        System.out.println("\n\n\n\n");
        System.out.println("=================================================================");
        System.out.println("=================================================================");
        System.out.println("test exceptions");
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

            IObjectProxy iop = (IObjectProxy)ft;
            assertThrows(ExceptionTest.class,()->iop.___testException(1));
            
            try {
                ft.stackOverflowTest();
                fail();
            } catch(Error e) {
                System.out.println("xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx");
                System.out.println("Excepción ERROR CORRECTAMENTE CAPTURADA");
                System.out.println("xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx");
                //e.printStackTrace();
            };

        } catch (NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | XDuplicatedProxyClass ex) {
            Logger.getLogger(ProxyTest.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    
    @Test
    public void testInterfacesException() {
        System.out.println("\n\n\n\n");
        System.out.println("=================================================================");
        System.out.println("=================================================================");
        System.out.println("test Interfaces exceptions");
        System.out.println("=================================================================");
        System.out.println("=================================================================");
        
        try {
            //ASMFooEx asm = new ASMFooEx();

            ObjectProxyImpl op = new ObjectProxyImpl();

            TypesCache tc = new TypesCache();

            ASMFooEx ft = tc.findOrInsert(ASMFooEx.class, IObjectProxy.class, ()->{
                                    Class clazz = new EasyProxy().getProxyClass(ASMFooEx.class, IObjectProxy.class);
                                    return clazz;
                                }).newInstance(op);
            System.out.println("clase generada!!");

            IObjectProxy iop = (IObjectProxy)ft;
            assertThrows(RuntimeExceptionTest.class,()-> iop.___testRuntimeException());
            assertThrows(ExceptionTest.class,()-> iop.___testException(1));
            assertThrows(ExceptionTest2.class,()-> iop.___testException(2));
        } catch (Throwable ex) {
            fail();
            ex.printStackTrace();
        }

    }
    

    // @Test
    // public void testEqual_y_hashCode() {
    //     System.out.println("\n\n\n\n");
    //     System.out.println("=================================================================");
    //     System.out.println("=================================================================");
    //     System.out.println("test Interfaces exceptions");
    //     System.out.println("=================================================================");
    //     System.out.println("=================================================================");
        
    //     try {
    //         //ASMFooEx asm = new ASMFooEx();

    //         ObjectProxyImpl op = new ObjectProxyImpl();

    //         TypesCache tc = new TypesCache();

    //         ASMFooEx ft = tc.findOrInsert(ASMFooEx.class, IObjectProxy.class, ()->{
    //                                 Class clazz = new EasyProxy().getProxyClass(ASMFooEx.class, IObjectProxy.class);
    //                                 return clazz;
    //                             }).newInstance(op);
    //         System.out.println("clase generada!!");

    //         assertEquals(42, ft.hashCode());
    //     } catch (Throwable ex) {
    //         fail();
    //         ex.printStackTrace();
    //     }

    // }


    public static void main(String[] args) {
        ProxyTest t = new ProxyTest();
        
        t.testTarget();
        
        // LOGGER.log(Level.INFO,LogSupplier.crear("texto %s", test.toString() ));
        //        t.testASM();
//        t.testEx();
//        t.testCache();
        
    }
}
