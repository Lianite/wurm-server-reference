// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.tools.xjc.reader.decorator;

import java.text.MessageFormat;
import java.util.ResourceBundle;

class Messages
{
    static final String ERR_INVALID_COLLECTION_TYPE = "InvalidCollectionType";
    static final String ERR_INVALID_ACCESS_MODIFIER = "InvalidAccessModifier";
    static final String ERR_INVALID_ACCESSOR = "InvalidAccessor";
    static final String ERR_UNDEFINED_ROLE = "UndefinedRole";
    static final String ERR_CLASS_NOT_FOUND = "ClassNotFound";
    static final String ERR_NAME_NEEDED = "NameNeeded";
    
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
        final String text = ResourceBundle.getBundle(((Messages.class$com$sun$tools$xjc$reader$decorator$Messages == null) ? (Messages.class$com$sun$tools$xjc$reader$decorator$Messages = class$("com.sun.tools.xjc.reader.decorator.Messages")) : Messages.class$com$sun$tools$xjc$reader$decorator$Messages).getName()).getString(property);
        return MessageFormat.format(text, args);
    }
}
