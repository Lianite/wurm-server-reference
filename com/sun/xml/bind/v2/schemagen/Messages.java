// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.xml.bind.v2.schemagen;

import java.text.MessageFormat;
import java.util.ResourceBundle;

enum Messages
{
    ANONYMOUS_TYPE_CYCLE;
    
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
