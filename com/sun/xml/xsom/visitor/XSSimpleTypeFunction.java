// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.xml.xsom.visitor;

import com.sun.xml.xsom.XSRestrictionSimpleType;
import com.sun.xml.xsom.XSUnionSimpleType;
import com.sun.xml.xsom.XSListSimpleType;

public interface XSSimpleTypeFunction
{
    Object listSimpleType(final XSListSimpleType p0);
    
    Object unionSimpleType(final XSUnionSimpleType p0);
    
    Object restrictionSimpleType(final XSRestrictionSimpleType p0);
}
