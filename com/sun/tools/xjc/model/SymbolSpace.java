// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.tools.xjc.model;

import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JType;

public class SymbolSpace
{
    private JType type;
    private final JCodeModel codeModel;
    
    public SymbolSpace(final JCodeModel _codeModel) {
        this.codeModel = _codeModel;
    }
    
    public JType getType() {
        if (this.type == null) {
            return this.codeModel.ref(Object.class);
        }
        return this.type;
    }
    
    public void setType(final JType _type) {
        if (this.type == null) {
            this.type = _type;
        }
    }
    
    public String toString() {
        if (this.type == null) {
            return "undetermined";
        }
        return this.type.name();
    }
}
