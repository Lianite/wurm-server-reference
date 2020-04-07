// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.xml.xsom.util;

import com.sun.xml.xsom.XSType;

public abstract class TypeSet
{
    public abstract boolean contains(final XSType p0);
    
    public static TypeSet intersection(final TypeSet a, final TypeSet b) {
        return new TypeSet() {
            public boolean contains(final XSType type) {
                return a.contains(type) && b.contains(type);
            }
        };
    }
    
    public static TypeSet union(final TypeSet a, final TypeSet b) {
        return new TypeSet() {
            public boolean contains(final XSType type) {
                return a.contains(type) || b.contains(type);
            }
        };
    }
}
