// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.tools.xjc.reader.annotator;

import java.text.MessageFormat;
import java.util.ResourceBundle;

class Messages
{
    static final String ENUM_FACET_UNSUPPORTED = "DatatypeSimplifier.EnumFacetUnsupported";
    static final String PATTERN_FACET_UNSUPPORTED = "DatatypeSimplifier.PatternFacetUnsupported";
    static final String ERR_MULTIPLE_SUPERCLASS_BODY = "Normalizer.MultipleSuperClassBody";
    static final String ERR_MULTIPLE_INHERITANCE = "Normalizer.MultipleInheritance";
    public static final String ERR_BAD_SUPERCLASS_USE = "Normalizer.BadSuperClassUse";
    public static final String ERR_BAD_ITEM_USE = "Normalizer.BadItemUse";
    public static final String ERR_MISSING_SUPERCLASS_BODY = "Normalizer.MissingSuperClassBody";
    public static final String ERR_BAD_SUPERCLASS_MULTIPLICITY = "Normalizer.BadSuperClassMultiplicity";
    public static final String ERR_BAD_SUPERCLASS_BODY_MULTIPLICITY = "Normalizer.BadSuperClassBodyMultiplicity";
    public static final String ERR_BAD_INTERFACE_CLASS_MULTIPLICITY = "Normalizer.BadInterfaceToClassMultiplicity";
    public static final String ERR_CONFLICT_BETWEEN_USERTYPE_AND_ACTUALTYPE = "Normalizer.ConflictBetweenUserTypeAndActualType";
    public static final String ERR_DELEGATION_MULTIPLICITY_MUST_BE_1 = "Normalizer.DelegationMultiplicityMustBe1";
    public static final String ERR_DELEGATION_MUST_BE_INTERFACE = "Normalizer.DelegationMustBeInterface";
    public static final String ERR_EMPTY_PROPERTY = "Normalizer.EmptyProperty";
    static final String ERR_PROPERTYNAME_COLLISION = "FieldCollisionChecker.PropertyNameCollision";
    static final String ERR_PROPERTYNAME_COLLISION_SOURCE = "FieldCollisionChecker.PropertyNameCollision.Source";
    static final String ERR_RESERVEDWORD_COLLISION = "FieldCollisionChecker.ReservedWordCollision";
    
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
        final String text = ResourceBundle.getBundle(((Messages.class$com$sun$tools$xjc$reader$annotator$Messages == null) ? (Messages.class$com$sun$tools$xjc$reader$annotator$Messages = class$("com.sun.tools.xjc.reader.annotator.Messages")) : Messages.class$com$sun$tools$xjc$reader$annotator$Messages).getName()).getString(property);
        return MessageFormat.format(text, args);
    }
}
