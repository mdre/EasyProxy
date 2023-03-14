/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package asm.proxy;

/**
 *
 * @author Marcelo D. Ré {@literal <marcelo.re@gmail.com>}
 */
public interface IObjectProxy { 
    public void ___sayHello(String s);
    public String ___getLast();
    public IObjectProxy ___getInterface();
    public void ___testException(int i) throws ExceptionTest, ExceptionTest2; 
}
