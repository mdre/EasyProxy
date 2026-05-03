package asm.proxy;

/**
 * Almacena las clases de referencia de los tipos básicos y el método para castear.
 * FIX: campos private final con getters para evitar mutación accidental del estado.
 */
public class TypeRef {
    // "Z", "I", "J", etc.
    private final String toAsm;
    // "java/lang/Integer"
    private final String castClass;
    // "intValue"
    private final String castMethod;
    // "()I"
    private final String descrptor;
    // opcode de retorno ASM (IRETURN, ARETURN, etc.)
    private final int asmReturnValue;
    // opcode del valor por defecto (ICONST_0, ACONST_NULL, etc.)
    private final int defaultValue;

    public TypeRef(String toAsm, String castClass, String castMethod, String descrptor, int asmReturnValue, int defaultValue) {
        this.toAsm = toAsm;
        this.castClass = castClass;
        this.castMethod = castMethod;
        this.descrptor = descrptor;
        this.asmReturnValue = asmReturnValue;
        this.defaultValue = defaultValue;
    }

    public String getToAsm()         { return toAsm; }
    public String getCastClass()     { return castClass; }
    public String getCastMethod()    { return castMethod; }
    public String getDescrptor()     { return descrptor; }
    public int    getAsmReturnValue(){ return asmReturnValue; }
    public int    getDefaultValue()  { return defaultValue; }

    @Override
    public String toString() {
        return "TypeRef [toAsm=" + toAsm + ", castClass=" + castClass
                + ", castMethod=" + castMethod + ", descrptor=" + descrptor + "]";
    }
}