package asm.proxy;

import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TypesCache {
    private Map<String,CachedType<?>> typesCache = new ConcurrentHashMap<>();
    
    private final static Logger LOGGER = Logger.getLogger(TypesCache.class.getName());
    static {
        if (LOGGER.getLevel() == null) {
            LOGGER.setLevel(Level.FINEST);
        }
        org.burningwave.core.assembler.StaticComponentContainer.Modules.exportAllToAll();
    }
    
    public TypesCache() {
    }

    /**
     * Find an existing type or insert a new one.
     * @param <T>
     * @param c
     * @param interceptorInterface
     * @return
     * @throws Exception
     */
    public synchronized <T>  CachedType<T> findOrInsert(Class<T>  c, Class<?> interceptorInterface, Callable<Class<?>> createIt) throws Exception {
        // primero buscar si la clase ya ha sido referenciada.
        LOGGER.log(Level.FINEST, "current cache: "+typesCache);
        
        LOGGER.log(Level.FINEST, "search for: "+c.getCanonicalName());
        CachedType cached = typesCache.get(c.getCanonicalName());
        if (cached == null) {
            // no se encontró la clase y hay que generar el proxy
            Class clazz = createIt.call();
            cached = new CachedType<>(clazz);
            typesCache.put(c.getCanonicalName(), cached);
        } else {
            // verificar que la clase sea instancia del inteceptor.
            LOGGER.log(Level.FINEST, "cached type: "+cached.getType().getCanonicalName()+" interfaces: "+Arrays.toString(cached.getType().getInterfaces()));
            LOGGER.log(Level.FINEST, "interceptor: "+interceptorInterface.getCanonicalName());
            if (!interceptorInterface.isAssignableFrom(cached.getType() ))
                throw new XDuplicatedProxyClass();
        }

        return cached;
    }

    public int getCachedClassesCount() {
        return typesCache.size();
    }

}
