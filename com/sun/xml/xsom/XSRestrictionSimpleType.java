// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.xml.xsom;

import java.util.Iterator;

public interface XSRestrictionSimpleType extends XSSimpleType
{
    Iterator iterateDeclaredFacets();
    
    XSFacet getDeclaredFacet(final String p0);
}
