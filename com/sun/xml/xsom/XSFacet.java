// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.xml.xsom;

import org.relaxng.datatype.ValidationContext;

public interface XSFacet extends XSComponent
{
    public static final String FACET_LENGTH = "length";
    public static final String FACET_MINLENGTH = "minLength";
    public static final String FACET_MAXLENGTH = "maxLength";
    public static final String FACET_PATTERN = "pattern";
    public static final String FACET_ENUMERATION = "enumeration";
    public static final String FACET_TOTALDIGITS = "totalDigits";
    public static final String FACET_FRACTIONDIGITS = "fractionDigits";
    public static final String FACET_MININCLUSIVE = "minInclusive";
    public static final String FACET_MAXINCLUSIVE = "maxInclusive";
    public static final String FACET_MINEXCLUSIVE = "minExclusive";
    public static final String FACET_MAXEXCLUSIVE = "maxExclusive";
    public static final String FACET_WHITESPACE = "whiteSpace";
    
    String getName();
    
    String getValue();
    
    ValidationContext getContext();
    
    boolean isFixed();
}
