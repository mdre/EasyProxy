package asm.proxy;

import java.io.IOException;

public class ASMFooEx extends ASMFoo {

    @Override
    public int toOverride(int i) {
        return super.toOverride(i);
    }

    @Override
    public int toOverride2(int i) {
        return i;
    }
}
