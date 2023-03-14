package asm.proxy;

import java.lang.reflect.InvocationTargetException;

public class CachedType<T>  {
    Class<T> type;

    public CachedType(Class<T> type) {
        this.type = type;
    }

    public Class<T> getType() {
        return type;
    }

    public T newInstance(IEasyProxyInterceptor interceptor) throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
        return type.getConstructor(IEasyProxyInterceptor.class).newInstance(interceptor);
    }
}
