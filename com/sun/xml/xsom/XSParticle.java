// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.xml.xsom;

public interface XSParticle extends XSContentType
{
    public static final int UNBOUNDED = -1;
    
    int getMinOccurs();
    
    int getMaxOccurs();
    
    XSTerm getTerm();
}
