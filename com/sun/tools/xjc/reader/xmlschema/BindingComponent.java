// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.tools.xjc.reader.xmlschema;

import com.sun.tools.xjc.reader.Ring;

public abstract class BindingComponent
{
    protected BindingComponent() {
        Ring.add(this);
    }
    
    protected final ErrorReporter getErrorReporter() {
        return Ring.get(ErrorReporter.class);
    }
    
    protected final ClassSelector getClassSelector() {
        return Ring.get(ClassSelector.class);
    }
}
