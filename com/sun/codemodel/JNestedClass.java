// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.codemodel;

class JNestedClass extends JDefinedClass implements JDeclaration
{
    private JDefinedClass outer;
    
    public JPackage _package() {
        return this.outer._package();
    }
    
    JNestedClass(final JDefinedClass outer, final int mods, final String name) {
        this(outer, mods, name, false);
    }
    
    JNestedClass(final JDefinedClass outer, final int mods, final String name, final boolean isInterface) {
        super(mods, name, isInterface, outer.owner());
        this.outer = null;
        this.outer = outer;
    }
    
    public String fullName() {
        return this.outer.fullName() + '.' + this.name();
    }
    
    public JClass outer() {
        return this.outer;
    }
    
    public JClassContainer parentContainer() {
        return this.outer;
    }
}
