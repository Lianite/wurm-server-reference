// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.xml.xsom;

import com.sun.xml.xsom.visitor.XSFunction;
import com.sun.xml.xsom.visitor.XSVisitor;
import org.xml.sax.Locator;

public interface XSComponent
{
    XSAnnotation getAnnotation();
    
    Locator getLocator();
    
    XSSchema getOwnerSchema();
    
    void visit(final XSVisitor p0);
    
    Object apply(final XSFunction p0);
}
