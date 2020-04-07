// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.codemodel;

public class JClassAlreadyExistsException extends Exception
{
    private final JDefinedClass existing;
    
    public JClassAlreadyExistsException(final JDefinedClass _existing) {
        this.existing = _existing;
    }
    
    public JDefinedClass getExistingClass() {
        return this.existing;
    }
}
