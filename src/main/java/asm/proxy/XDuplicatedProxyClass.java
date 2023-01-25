package asm.proxy;

public class XDuplicatedProxyClass extends Exception {

    public XDuplicatedProxyClass() {
        super("Duplicated class detected! The class is already registered with a different IEasyProxyInterface"); 
    }
    
}
