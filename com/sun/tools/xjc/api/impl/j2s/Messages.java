// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.tools.xjc.api.impl.j2s;

import java.text.MessageFormat;
import java.util.ResourceBundle;

enum Messages
{
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
