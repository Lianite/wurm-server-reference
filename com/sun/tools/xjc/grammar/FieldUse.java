// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.tools.xjc.grammar;

import java.util.Iterator;
import com.sun.tools.xjc.generator.field.FieldRendererFactory;
import java.util.HashSet;
import com.sun.tools.xjc.grammar.util.Multiplicity;
import java.util.Set;
import com.sun.codemodel.JType;
import com.sun.codemodel.JCodeModel;

public final class FieldUse
{
    public final String name;
    public final ClassItem owner;
    public final JCodeModel codeModel;
    public JType type;
    public final Set items;
    public Multiplicity multiplicity;
    
    protected FieldUse(final String name, final ClassItem _owner) {
        this.items = new HashSet();
        this.name = name;
        this.owner = _owner;
        this.codeModel = this.owner.owner.codeModel;
    }
    
    public final JCodeModel getCodeModel() {
        return this.owner.owner.codeModel;
    }
    
    public FieldRendererFactory getRealization() {
        final Iterator itr = this.items.iterator();
        while (itr.hasNext()) {
            final FieldRendererFactory frf = itr.next().realization;
            if (frf != null) {
                return frf;
            }
        }
        return null;
    }
    
    public DefaultValue[] getDefaultValues() {
        final Iterator itr = this.items.iterator();
        while (itr.hasNext()) {
            final DefaultValue[] dv = itr.next().defaultValues;
            if (dv != null) {
                return dv;
            }
        }
        return null;
    }
    
    public String getJavadoc() {
        final StringBuffer buf = new StringBuffer();
        final FieldItem[] items = this.getItems();
        for (int i = 0; i < items.length; ++i) {
            if (items[i].javadoc != null) {
                if (i != 0) {
                    buf.append("\n\n");
                }
                buf.append(items[i].javadoc);
            }
        }
        return buf.toString();
    }
    
    public boolean isUnboxable() {
        final FieldItem[] items = this.getItems();
        for (int i = 0; i < items.length; ++i) {
            if (!items[i].isUnboxable(this.codeModel)) {
                return false;
            }
        }
        return true;
    }
    
    public boolean isDelegated() {
        final FieldItem[] items = this.getItems();
        for (int i = 0; i < items.length; ++i) {
            if (items[i].isDelegated()) {
                return true;
            }
        }
        return false;
    }
    
    public void disableDelegation() {
        final FieldItem[] items = this.getItems();
        for (int i = 0; i < items.length; ++i) {
            items[i].setDelegation(false);
        }
    }
    
    public FieldItem[] getItems() {
        return this.items.toArray(new FieldItem[0]);
    }
}
