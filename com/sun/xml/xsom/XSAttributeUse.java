// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.xml.xsom;

import org.relaxng.datatype.ValidationContext;

public interface XSAttributeUse extends XSComponent
{
    boolean isRequired();
    
    XSAttributeDecl getDecl();
    
    String getDefaultValue();
    
    String getFixedValue();
    
    ValidationContext getContext();
}
