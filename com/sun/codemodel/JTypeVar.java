// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.codemodel;

import java.util.List;
import java.util.Iterator;

public final class JTypeVar extends JClass implements JDeclaration
{
    private final String name;
    private JClass bound;
    
    JTypeVar(final JCodeModel owner, final String _name) {
        super(owner);
        this.name = _name;
    }
    
    public String name() {
        return this.name;
    }
    
    public String fullName() {
        return this.name;
    }
    
    public JPackage _package() {
        return null;
    }
    
    public JTypeVar bound(final JClass c) {
        if (this.bound != null) {
            throw new IllegalArgumentException("type variable has an existing class bound " + this.bound);
        }
        this.bound = c;
        return this;
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
    
    public void declare(final JFormatter f) {
        f.id(this.name);
        if (this.bound != null) {
            f.p("extends").g(this.bound);
        }
    }
    
    protected JClass substituteParams(final JTypeVar[] variables, final List<JClass> bindings) {
        for (int i = 0; i < variables.length; ++i) {
            if (variables[i] == this) {
                return bindings.get(i);
            }
        }
        return this;
    }
    
    public void generate(final JFormatter f) {
        f.id(this.name);
    }
}
