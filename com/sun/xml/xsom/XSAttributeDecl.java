// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.xml.xsom;

import org.relaxng.datatype.ValidationContext;

public interface XSAttributeDecl extends XSDeclaration
{
    XSSimpleType getType();
    
    String getDefaultValue();
    
    String getFixedValue();
    
    ValidationContext getContext();
}
