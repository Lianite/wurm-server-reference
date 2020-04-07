// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.xml.xsom;

import com.sun.xml.xsom.visitor.XSTermFunction;
import com.sun.xml.xsom.visitor.XSTermVisitor;

public interface XSTerm extends XSComponent
{
    void visit(final XSTermVisitor p0);
    
    Object apply(final XSTermFunction p0);
    
    boolean isWildcard();
    
    boolean isModelGroupDecl();
    
    boolean isModelGroup();
    
    boolean isElementDecl();
    
    XSWildcard asWildcard();
    
    XSModelGroupDecl asModelGroupDecl();
    
    XSModelGroup asModelGroup();
    
    XSElementDecl asElementDecl();
}
