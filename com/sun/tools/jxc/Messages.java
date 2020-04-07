// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.tools.jxc;

import java.text.MessageFormat;
import java.util.ResourceBundle;

enum Messages
{
    UNEXPECTED_NGCC_TOKEN, 
    BASEDIR_DOESNT_EXIST, 
    USAGE, 
    VERSION;
    
    private static final ResourceBundle rb;
    
    public String toString() {
        return this.format(new Object[0]);
    }
    
    public String format(final Object... args) {
        return MessageFormat.format(Messages.rb.getString(this.name()), args);
    }
    
    static {
        rb = ResourceBundle.getBundle(Messages.class.getPackage().getName() + ".MessageBundle");
    }
}
