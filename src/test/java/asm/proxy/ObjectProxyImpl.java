/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package asm.proxy;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Marcelo D. Ré {@literal <marcelo.re@gmail.com>}
 */
public class ObjectProxyImpl implements IObjectProxy, IEasyProxyInterceptor {
    private final static Logger LOGGER = Logger.getLogger(ObjectProxyImpl.class .getName());
    static {
        if (LOGGER.getLevel() == null) {
            LOGGER.setLevel(Level.INFO);
        }
    }

    String lastValue;

    @Override
    public Object intercept(Object target, Method method, Method superMethod, Object... args) throws Throwable {
        System.out.println("Intercepted: "+method.toString());
        System.out.println("superMethod: "+superMethod);
        System.out.println("anotations: "+Arrays.toString(method.getAnnotations()));
        
        Object result = null;
        switch(method.getName()) {
            case "___sayHello":
                this.___sayHello((String)args[0]);
                break;
            case "___getLast":
                result = this.___getLast();
                break;
            case "___getInterface":
                result = this;
                break;
            case "___testException":
                // try {
                    this.___testException((int)args[0]);
                // } catch (Throwable ex) {
                //     System.out.println("catch "+ex.toString());
                //     if (ex instanceof ExceptionTest) {
                //         System.out.println("---> volver a lanzar --->");
                //         throw (ExceptionTest)ex;
                //     }
                // }
                break;
            default:
                // try {
                    result = superMethod.invoke(target, args);
                // } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                //     // TODO Auto-generated catch block
                //     e.printStackTrace();
                // } catch (RuntimeException rte) {
                //     System.out.println("object proxy implementation catch rte");
                //     throw rte;
                // }
        }
        return result;
    }

    @Override
    public void ___sayHello(String s) {
        System.out.println("Hola "+s);
        lastValue = "EasyProxy";
    }

    @Override
    public String ___getLast() {
        return lastValue;
    }
    
    @Override
    public IObjectProxy ___getInterface() {
        System.out.println("test interface as parameter");
        return this;
    }

    @Override
    public void ___testException(int i) throws ExceptionTest, ExceptionTest2 {
        if (i == 1) { 
            throw new ExceptionTest("Exception test!!");
        } else {
            throw new ExceptionTest2("Exception test 2!!");
        }
    }
}
