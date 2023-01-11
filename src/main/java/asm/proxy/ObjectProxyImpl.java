/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package asm.proxy;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
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

    @Override
    public Object intercept(Object target, Method method, Method superMethod, Object... args) {
        System.out.println("Intercepted: "+method.toString());
        System.out.println("superMethod: "+superMethod);
        System.out.println("anotations: "+Arrays.toString(method.getAnnotations()));
        
        try {
            return superMethod.invoke(target, args);
        } catch (Throwable e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void sayHello(String s) {
        System.out.println("Hola "+s);
    }
    
}
