// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.tools.xjc.generator.marshaller;

import java.text.MessageFormat;
import java.util.ResourceBundle;

class Messages
{
    static final String TYPE_MISMATCH = "AbstractPassImpl.TypeMismatch";
    static /* synthetic */ Class class$com$sun$tools$xjc$generator$marshaller$Messages;
    
    static String format(final String property) {
        return format(property, null);
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
        final String text = ResourceBundle.getBundle(((Messages.class$com$sun$tools$xjc$generator$marshaller$Messages == null) ? (Messages.class$com$sun$tools$xjc$generator$marshaller$Messages = class$("com.sun.tools.xjc.generator.marshaller.Messages")) : Messages.class$com$sun$tools$xjc$generator$marshaller$Messages).getName()).getString(property);
        return MessageFormat.format(text, args);
    }
    
    static /* synthetic */ Class class$(final String x0) {
        try {
            return Class.forName(x0);
        }
        catch (ClassNotFoundException x) {
            throw new NoClassDefFoundError(x.getMessage());
        }
    }
}
