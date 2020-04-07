// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.codemodel;

import java.util.List;
import java.util.Collections;
import java.util.Iterator;

final class JDirectClass extends JClass
{
    private final String fullName;
    
    public JDirectClass(final JCodeModel _owner, final String fullName) {
        super(_owner);
        this.fullName = fullName;
    }
    
    public String name() {
        final int i = this.fullName.lastIndexOf(46);
        if (i >= 0) {
            return this.fullName.substring(i + 1);
        }
        return this.fullName;
    }
    
    public String fullName() {
        return this.fullName;
    }
    
    public JPackage _package() {
        final int i = this.fullName.lastIndexOf(46);
        if (i >= 0) {
            return this.owner()._package(this.fullName.substring(0, i));
        }
        return this.owner().rootPackage();
    }
    
    public JClass _extends() {
        return this.owner().ref(Object.class);
    }
    
    public Iterator<JClass> _implements() {
        return Collections.emptyList().iterator();
    }
    
    public boolean isInterface() {
        return false;
    }
    
    public boolean isAbstract() {
        return false;
    }
    
    protected JClass substituteParams(final JTypeVar[] variables, final List<JClass> bindings) {
        return this;
    }
}
