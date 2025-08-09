package com.adasplus.adas.adas;

import dalvik.system.DexClassLoader;

public class AdasClassLoader extends DexClassLoader {
    public AdasClassLoader(String dexPath, String optimizedDirectory, String libraryPath, ClassLoader parent) {
        super(dexPath, optimizedDirectory, libraryPath, parent);
    }
}
