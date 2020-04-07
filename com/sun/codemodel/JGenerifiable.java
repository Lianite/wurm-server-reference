// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.codemodel;

public interface JGenerifiable
{
    JTypeVar generify(final String p0);
    
    JTypeVar generify(final String p0, final Class p1);
    
    JTypeVar generify(final String p0, final JClass p1);
    
    JTypeVar[] typeParams();
}
