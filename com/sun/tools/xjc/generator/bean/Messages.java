// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.tools.xjc.generator.bean;

import java.text.MessageFormat;
import java.util.ResourceBundle;

enum Messages
{
    METHOD_COLLISION, 
    ERR_UNUSABLE_NAME, 
    ERR_NAME_COLLISION, 
    ILLEGAL_CONSTRUCTOR_PARAM, 
    OBJECT_FACTORY_CONFLICT, 
    OBJECT_FACTORY_CONFLICT_RELATED;
    
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
