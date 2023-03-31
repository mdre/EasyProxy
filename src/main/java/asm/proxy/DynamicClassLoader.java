
package asm.proxy;

import java.lang.Class;
import java.lang.ClassLoader;
import java.lang.String;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Marcelo D. Ré {@literal <marcelo.re@gmail.com>}
 */
public class DynamicClassLoader extends ClassLoader {
    private final static Logger LOGGER = Logger.getLogger(DynamicClassLoader.class .getName());
    static {
        if (LOGGER.getLevel() == null) {
            LOGGER.setLevel(Level.INFO);
        }
    }
    
    public DynamicClassLoader(ClassLoader parent) {
        super(parent);
    }


    public Class<?> defineClass(String name, byte[] b) {
        return super.defineClass(name, b, 0, b.length);
    }
}
