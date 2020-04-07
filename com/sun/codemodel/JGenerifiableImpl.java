// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.codemodel;

import java.util.ArrayList;
import java.util.List;

abstract class JGenerifiableImpl implements JGenerifiable, JDeclaration
{
    private List<JTypeVar> typeVariables;
    
    JGenerifiableImpl() {
        this.typeVariables = null;
    }
    
    protected abstract JCodeModel owner();
    
    public void declare(final JFormatter f) {
        if (this.typeVariables != null) {
            f.p('<');
            for (int i = 0; i < this.typeVariables.size(); ++i) {
                if (i != 0) {
                    f.p(',');
                }
                f.d(this.typeVariables.get(i));
            }
            f.p('>');
        }
    }
    
    public JTypeVar generify(final String name) {
        final JTypeVar v = new JTypeVar(this.owner(), name);
        if (this.typeVariables == null) {
            this.typeVariables = new ArrayList<JTypeVar>(3);
        }
        this.typeVariables.add(v);
        return v;
    }
    
    public JTypeVar generify(final String name, final Class bound) {
        return this.generify(name, this.owner().ref(bound));
    }
    
    public JTypeVar generify(final String name, final JClass bound) {
        return this.generify(name).bound(bound);
    }
    
    public JTypeVar[] typeParams() {
        if (this.typeVariables == null) {
            return JTypeVar.EMPTY_ARRAY;
        }
        return this.typeVariables.toArray(new JTypeVar[this.typeVariables.size()]);
    }
}
