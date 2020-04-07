// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.xml.xsom.visitor;

import com.sun.xml.xsom.XSRestrictionSimpleType;
import com.sun.xml.xsom.XSUnionSimpleType;
import com.sun.xml.xsom.XSListSimpleType;

public interface XSSimpleTypeVisitor
{
    void listSimpleType(final XSListSimpleType p0);
    
    void unionSimpleType(final XSUnionSimpleType p0);
    
    void restrictionSimpleType(final XSRestrictionSimpleType p0);
}
