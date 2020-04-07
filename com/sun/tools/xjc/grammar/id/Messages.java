// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.tools.xjc.grammar.id;

import java.text.MessageFormat;
import java.util.ResourceBundle;

class Messages
{
    static final String CONSTANT_IDREF_ERROR = "IDREFTransducer.ConstantIDREFError";
    
    static String format(final String property) {
        return format(property, (Object[])null);
    }
    
    static String format(final String property, final Object arg1) {
        return format(property, new Object[] { arg1 });
    }
    
    static String format(final String property, final Object arg1, final Object arg2) {
        return format(property, new Object[] { arg1, arg2 });
    }
    
    static String format(final String property, final Object arg1, final Object arg2, final Object arg3) {
        return format(property, new Object[] { arg1, arg2, arg3 });
    }
    
    static String format(final String property, final Object[] args) {
        final String text = ResourceBundle.getBundle(((Messages.class$com$sun$tools$xjc$grammar$id$Messages == null) ? (Messages.class$com$sun$tools$xjc$grammar$id$Messages = class$("com.sun.tools.xjc.grammar.id.Messages")) : Messages.class$com$sun$tools$xjc$grammar$id$Messages).getName()).getString(property);
        return MessageFormat.format(text, args);
    }
}
