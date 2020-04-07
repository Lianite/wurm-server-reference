// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.tools.jxc.apt;

import java.text.MessageFormat;
import java.util.ResourceBundle;

enum Messages
{
    NON_EXISTENT_FILE, 
    UNRECOGNIZED_PARAMETER, 
    OPERAND_MISSING;
    
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
