// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.javaws.jnl;

public interface ResourceVisitor
{
    void visitJARDesc(final JARDesc p0);
    
    void visitPropertyDesc(final PropertyDesc p0);
    
    void visitPackageDesc(final PackageDesc p0);
    
    void visitExtensionDesc(final ExtensionDesc p0);
    
    void visitJREDesc(final JREDesc p0);
}
