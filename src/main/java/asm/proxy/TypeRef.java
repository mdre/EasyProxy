package asm.proxy;
/**
 * Utilizada para almacenar las clases de referencia de los tipos básicos y el método para castear
 **/
public class TypeRef {
    public String toAsm;
    // "java/lang/Integer"
    public String castClass;
    // "intValue"
    public String castMethod;
    // "()I"
    public String descrptor;
    // default value
    public int asmReturnValue;
    public int defaultValue;
    public int opcode;
    
    public TypeRef(String toAsm, String castClass, String castMethod, String descrptor, int asmReturnValue, int defaultValue) {
        this.toAsm = toAsm;
        this.castClass = castClass;
        this.castMethod = castMethod;
        this.descrptor = descrptor;
        this.asmReturnValue = asmReturnValue;
        this.defaultValue = defaultValue;
    }

    @Override
    public String toString() {
        return "TypeRef [toAsm=" + toAsm + ", castClass=" + castClass + ", castMethod=" + castMethod + ", descrptor="
                + descrptor + "]";
    }

}