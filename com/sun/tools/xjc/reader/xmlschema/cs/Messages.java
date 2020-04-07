// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.tools.xjc.reader.xmlschema.cs;

import java.text.MessageFormat;
import java.util.ResourceBundle;

class Messages
{
    static final String ERR_ABSTRACT_COMPLEX_TYPE = "ClassSelector.AbstractComplexType";
    static final String ERR_ABSTRACT_COMPLEX_TYPE_SOURCE = "ClassSelector.AbstractComplexType.SourceLocation";
    static final String JAVADOC_HEADING = "ClassSelector.JavadocHeading";
    static final String JAVADOC_LINE_UNKNOWN = "ClassSelector.JavadocLineUnknown";
    static final String ERR_RESERVED_CLASS_NAME = "ClassSelector.ReservedClassName";
    static final String ERR_CLASS_NAME_IS_REQUIRED = "ClassSelector.ClassNameIsRequired";
    static final String ERR_INCORRECT_CLASS_NAME = "ClassSelector.IncorrectClassName";
    static final String ERR_INCORRECT_PACKAGE_NAME = "ClassSelector.IncorrectPackageName";
    static final String ERR_UNABLE_TO_GENERATE_NAME_FROM_MODELGROUP = "DefaultParticleBinder.UnableToGenerateNameFromModelGroup";
    
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
        final String text = ResourceBundle.getBundle(((Messages.class$com$sun$tools$xjc$reader$xmlschema$cs$Messages == null) ? (Messages.class$com$sun$tools$xjc$reader$xmlschema$cs$Messages = class$("com.sun.tools.xjc.reader.xmlschema.cs.Messages")) : Messages.class$com$sun$tools$xjc$reader$xmlschema$cs$Messages).getName()).getString(property);
        return MessageFormat.format(text, args);
    }
}
