// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.istack.tools;

import java.util.Collection;

public class MaskingClassLoader extends ClassLoader
{
    private final String[] masks;
    
    public MaskingClassLoader(final String... masks) {
        this.masks = masks;
    }
    
    public MaskingClassLoader(final Collection<String> masks) {
        this((String[])masks.toArray(new String[masks.size()]));
    }
    
    public MaskingClassLoader(final ClassLoader parent, final String... masks) {
        super(parent);
        this.masks = masks;
    }
    
    public MaskingClassLoader(final ClassLoader parent, final Collection<String> masks) {
        this(parent, (String[])masks.toArray(new String[masks.size()]));
    }
    
    protected synchronized Class<?> loadClass(final String name, final boolean resolve) throws ClassNotFoundException {
        for (final String mask : this.masks) {
            if (name.startsWith(mask)) {
                throw new ClassNotFoundException();
            }
        }
        return super.loadClass(name, resolve);
    }
}
