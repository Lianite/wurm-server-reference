// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.tools.xjc.model;

import com.sun.codemodel.JPackage;
import com.sun.tools.xjc.api.ClassNameAllocator;

final class ClassNameAllocatorWrapper implements ClassNameAllocator
{
    private final ClassNameAllocator core;
    
    ClassNameAllocatorWrapper(ClassNameAllocator core) {
        if (core == null) {
            core = new ClassNameAllocator() {
                public String assignClassName(final String packageName, final String className) {
                    return className;
                }
            };
        }
        this.core = core;
    }
    
    public String assignClassName(final String packageName, final String className) {
        return this.core.assignClassName(packageName, className);
    }
    
    public String assignClassName(final JPackage pkg, final String className) {
        return this.core.assignClassName(pkg.name(), className);
    }
    
    public String assignClassName(final CClassInfoParent parent, final String className) {
        if (parent instanceof CClassInfoParent.Package) {
            final CClassInfoParent.Package p = (CClassInfoParent.Package)parent;
            return this.assignClassName(p.pkg, className);
        }
        return className;
    }
}
