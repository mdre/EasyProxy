/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package asm.proxy;

import java.lang.reflect.Method;
import java.util.HashMap;

/**
 *
 * @author Marcelo D. Ré {@literal <marcelo.re@gmail.com>}
 */
public interface IEasyProxyInterceptor {
    public Object intercept(Object target, Method method, Method superMethod, Object... args)  throws Throwable ;
}
