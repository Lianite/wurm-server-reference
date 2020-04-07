// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.codemodel;

import java.util.List;
import java.util.Iterator;

final class JTypeWildcard extends JClass
{
    private final JClass bound;
    
    JTypeWildcard(final JClass bound) {
        super(bound.owner());
        this.bound = bound;
    }
    
    public String name() {
        return "? extends " + this.bound.name();
    }
    
    public String fullName() {
        return "? extends " + this.bound.fullName();
    }
    
    public JPackage _package() {
        return null;
    }
    
    public JClass _extends() {
        if (this.bound != null) {
            return this.bound;
        }
        return this.owner().ref(Object.class);
    }
    
    public Iterator<JClass> _implements() {
        return this.bound._implements();
    }
    
    public boolean isInterface() {
        return false;
    }
    
    public boolean isAbstract() {
        return false;
    }
    
    protected JClass substituteParams(final JTypeVar[] variables, final List<JClass> bindings) {
        final JClass nb = this.bound.substituteParams(variables, bindings);
        if (nb == this.bound) {
            return this;
        }
        return new JTypeWildcard(nb);
    }
    
    public void generate(final JFormatter f) {
        if (this.bound._extends() == null) {
            f.p("?");
        }
        else {
            f.p("? extends").g(this.bound);
        }
    }
}
