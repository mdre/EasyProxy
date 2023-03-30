package asm.proxy;

import java.util.function.Supplier;

public abstract interface LogSupplier extends Supplier<String> {
    
    static LogSupplier crear(String message, Object... args) {
        
        return new LogSupplier() {
            private Object[] _args = args;
            private String _message = message;
            @Override
            public String get() {
                return String.format(_message, _args[0]);
            }
        };
    };
}
