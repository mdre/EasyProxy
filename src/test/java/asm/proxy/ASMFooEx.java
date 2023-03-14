package asm.proxy;


public class ASMFooEx extends ASMFoo {

    @Override
    public int toOverride(int i) {
        return super.toOverride(i);
    }

    /**
     * sobreescribe completamente el método anterior y cambia el varlor de retorno.
     */
    @Override
    public int toOverride2(int i) {
        return i;
    }
}
