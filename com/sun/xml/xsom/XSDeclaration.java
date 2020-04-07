// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.xml.xsom;

public interface XSDeclaration extends XSComponent
{
    String getTargetNamespace();
    
    String getName();
    
    boolean isAnonymous();
    
    boolean isGlobal();
    
    boolean isLocal();
}
