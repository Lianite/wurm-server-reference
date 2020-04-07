// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.tools.xjc.model;

import java.util.Iterator;
import java.util.Collection;
import java.util.ArrayList;

public final class CCustomizations extends ArrayList<CPluginCustomization>
{
    CCustomizations next;
    private CCustomizable owner;
    public static final CCustomizations EMPTY;
    
    public CCustomizations() {
    }
    
    public CCustomizations(final Collection<? extends CPluginCustomization> cPluginCustomizations) {
        super(cPluginCustomizations);
    }
    
    void setParent(final Model model, final CCustomizable owner) {
        if (this.owner != null) {
            return;
        }
        this.next = model.customizations;
        model.customizations = this;
        assert owner != null;
        this.owner = owner;
    }
    
    public CCustomizable getOwner() {
        assert this.owner != null;
        return this.owner;
    }
    
    public CPluginCustomization find(final String nsUri) {
        for (final CPluginCustomization p : this) {
            if (this.fixNull(p.element.getNamespaceURI()).equals(nsUri)) {
                return p;
            }
        }
        return null;
    }
    
    public CPluginCustomization find(final String nsUri, final String localName) {
        for (final CPluginCustomization p : this) {
            if (this.fixNull(p.element.getNamespaceURI()).equals(nsUri) && this.fixNull(p.element.getLocalName()).equals(localName)) {
                return p;
            }
        }
        return null;
    }
    
    private String fixNull(final String s) {
        if (s == null) {
            return "";
        }
        return s;
    }
    
    public static CCustomizations merge(final CCustomizations lhs, final CCustomizations rhs) {
        if (lhs == null || lhs.isEmpty()) {
            return rhs;
        }
        if (rhs == null || rhs.isEmpty()) {
            return lhs;
        }
        final CCustomizations r = new CCustomizations(lhs);
        r.addAll(rhs);
        return r;
    }
    
    public boolean equals(final Object o) {
        return this == o;
    }
    
    public int hashCode() {
        return System.identityHashCode(this);
    }
    
    static {
        EMPTY = new CCustomizations();
    }
}
