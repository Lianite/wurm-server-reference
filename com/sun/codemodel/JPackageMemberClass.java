// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.codemodel;

class JPackageMemberClass extends JDefinedClass implements JDeclaration
{
    private JPackage pkg;
    
    public final JPackage _package() {
        return this.pkg;
    }
    
    public JClassContainer parentContainer() {
        return this.pkg;
    }
    
    JPackageMemberClass(final JPackage pkg, final int mods, final String name) {
        this(pkg, mods, name, false);
    }
    
    JPackageMemberClass(final JPackage pkg, final int mods, final String name, final boolean isInterface) {
        super(mods, name, isInterface, pkg.owner());
        this.pkg = pkg;
    }
    
    public void declare(final JFormatter f) {
        if (!this.pkg.isUnnamed()) {
            f.nl().d(this.pkg);
            f.nl();
        }
        super.declare(f);
    }
}
