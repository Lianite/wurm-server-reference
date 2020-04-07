// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.xml.xsom;

import java.util.List;

public interface XSIdentityConstraint extends XSComponent
{
    public static final short KEY = 0;
    public static final short KEYREF = 1;
    public static final short UNIQUE = 2;
    
    XSElementDecl getParent();
    
    String getName();
    
    String getTargetNamespace();
    
    short getCategory();
    
    XSXPath getSelector();
    
    List<XSXPath> getFields();
    
    XSIdentityConstraint getReferencedKey();
}
