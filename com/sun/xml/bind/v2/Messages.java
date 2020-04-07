// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.xml.bind.v2;

import java.text.MessageFormat;
import java.util.ResourceBundle;

enum Messages
{
    ILLEGAL_ENTRY, 
    ERROR_LOADING_CLASS, 
    INVALID_PROPERTY_VALUE, 
    UNSUPPORTED_PROPERTY, 
    BROKEN_CONTEXTPATH, 
    NO_DEFAULT_CONSTRUCTOR_IN_INNER_CLASS, 
    INVALID_TYPE_IN_MAP;
    
    private static final ResourceBundle rb;
    
    public String toString() {
        return this.format(new Object[0]);
    }
    
    public String format(final Object... args) {
        return MessageFormat.format(Messages.rb.getString(this.name()), args);
    }
    
    static {
        rb = ResourceBundle.getBundle(Messages.class.getName());
    }
}
