// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.xml.xsom.impl.parser;

import com.sun.xml.xsom.XSType;
import com.sun.xml.xsom.impl.Ref;

public class SubstGroupBaseTypeRef implements Ref.Type
{
    private final Ref.Element e;
    
    public SubstGroupBaseTypeRef(final Ref.Element _e) {
        this.e = _e;
    }
    
    public XSType getType() {
        return this.e.get().getType();
    }
}
