// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.xml.xsom.util;

import com.sun.xml.xsom.XSType;
import java.util.Set;

public class SimpleTypeSet extends TypeSet
{
    private final Set typeSet;
    
    public SimpleTypeSet(final Set s) {
        this.typeSet = s;
    }
    
    public boolean contains(final XSType type) {
        return this.typeSet.contains(type);
    }
}
