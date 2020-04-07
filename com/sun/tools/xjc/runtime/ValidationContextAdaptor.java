// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.tools.xjc.runtime;

import org.relaxng.datatype.ValidationContext;

public final class ValidationContextAdaptor implements ValidationContext
{
    private final UnmarshallingContext core;
    
    public ValidationContextAdaptor(final UnmarshallingContext _context) {
        this.core = _context;
    }
    
    public String getBaseUri() {
        return this.core.getBaseUri();
    }
    
    public boolean isNotation(final String notationName) {
        return this.core.isNotation(notationName);
    }
    
    public boolean isUnparsedEntity(final String entityName) {
        return this.core.isUnparsedEntity(entityName);
    }
    
    public String resolveNamespacePrefix(final String prefix) {
        return this.core.resolveNamespacePrefix(prefix);
    }
}
