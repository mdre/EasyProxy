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
            LOGGER.setLevel(Level.INFO);
        }
        // org.burningwave.core.assembler.StaticComponentContainer.Modules.exportAllToAll();
    }
    
    public TypesCache() {
    }

    /**
     * Find an existing type or insert a new one.
     * FIX: se reemplaza synchronized sobre todo el método por una verificación con
     * bloqueo mínimo. El ConcurrentHashMap ya protege lecturas concurrentes; solo
     * se sincroniza el bloque de creación para evitar doble generación de proxies.
     */
    public <T> CachedType<T> findOrInsert(Class<T> c, Class<?> interceptorInterface, Callable<Class<?>> createIt) throws Exception {
        LOGGER.log(Level.FINEST, "current cache: "+typesCache);
        LOGGER.log(Level.FINEST, "search for: "+c.getCanonicalName());

        // Lectura sin lock — ConcurrentHashMap es thread-safe para get
        CachedType cached = typesCache.get(c.getCanonicalName());
        if (cached != null) {
            LOGGER.log(Level.FINEST, "cached type: "+cached.getType().getCanonicalName()+" interfaces: "+Arrays.toString(cached.getType().getInterfaces()));
            LOGGER.log(Level.FINEST, "interceptor: "+interceptorInterface.getCanonicalName());
            if (!interceptorInterface.isAssignableFrom(cached.getType()))
                throw new XDuplicatedProxyClass();
            return cached;
        }

        // Solo se bloquea al crear para evitar que dos hilos generen el mismo proxy
        synchronized (this) {
            // Re-verificar tras adquirir el lock (double-checked locking)
            cached = typesCache.get(c.getCanonicalName());
            if (cached == null) {
                Class clazz = createIt.call();
                cached = new CachedType<>(clazz);
                typesCache.put(c.getCanonicalName(), cached);
            } else {
                if (!interceptorInterface.isAssignableFrom(cached.getType()))
                    throw new XDuplicatedProxyClass();
            }
        }
        return cached;
    }

    public int getCachedClassesCount() {
        return typesCache.size();
    }

}
