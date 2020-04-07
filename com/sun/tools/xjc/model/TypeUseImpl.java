// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.tools.xjc.model;

import com.sun.codemodel.JExpr;
import com.sun.xml.bind.v2.ClassFactory;
import javax.xml.bind.annotation.adapters.XmlAdapter;
import com.sun.codemodel.JStringLiteral;
import com.sun.codemodel.JExpression;
import com.sun.xml.xsom.XmlString;
import com.sun.tools.xjc.outline.Outline;
import javax.activation.MimeType;
import com.sun.xml.bind.v2.model.core.ID;

final class TypeUseImpl implements TypeUse
{
    private final CNonElement coreType;
    private final boolean collection;
    private final CAdapter adapter;
    private final ID id;
    private final MimeType expectedMimeType;
    
    public TypeUseImpl(final CNonElement itemType, final boolean collection, final ID id, final MimeType expectedMimeType, final CAdapter adapter) {
        this.coreType = itemType;
        this.collection = collection;
        this.id = id;
        this.expectedMimeType = expectedMimeType;
        this.adapter = adapter;
    }
    
    public boolean isCollection() {
        return this.collection;
    }
    
    public CNonElement getInfo() {
        return this.coreType;
    }
    
    public CAdapter getAdapterUse() {
        return this.adapter;
    }
    
    public ID idUse() {
        return this.id;
    }
    
    public MimeType getExpectedMimeType() {
        return this.expectedMimeType;
    }
    
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof TypeUseImpl)) {
            return false;
        }
        final TypeUseImpl that = (TypeUseImpl)o;
        if (this.collection != that.collection) {
            return false;
        }
        if (this.id != that.id) {
            return false;
        }
        Label_0080: {
            if (this.adapter != null) {
                if (this.adapter.equals(that.adapter)) {
                    break Label_0080;
                }
            }
            else if (that.adapter == null) {
                break Label_0080;
            }
            return false;
        }
        if (this.coreType != null) {
            if (this.coreType.equals(that.coreType)) {
                return true;
            }
        }
        else if (that.coreType == null) {
            return true;
        }
        return false;
    }
    
    public int hashCode() {
        int result = (this.coreType != null) ? this.coreType.hashCode() : 0;
        result = 29 * result + (this.collection ? 1 : 0);
        result = 29 * result + ((this.adapter != null) ? this.adapter.hashCode() : 0);
        return result;
    }
    
    public JExpression createConstant(final Outline outline, final XmlString lexical) {
        if (this.isCollection()) {
            return null;
        }
        if (this.adapter == null) {
            return this.coreType.createConstant(outline, lexical);
        }
        final JExpression cons = this.coreType.createConstant(outline, lexical);
        final Class<? extends XmlAdapter> atype = this.adapter.getAdapterIfKnown();
        if (cons instanceof JStringLiteral && atype != null) {
            final JStringLiteral scons = (JStringLiteral)cons;
            final XmlAdapter a = ClassFactory.create(atype);
            try {
                final Object value = a.unmarshal(scons.str);
                if (value instanceof String) {
                    return JExpr.lit((String)value);
                }
            }
            catch (Exception ex) {}
        }
        return JExpr._new(this.adapter.getAdapterClass(outline)).invoke("unmarshal").arg(cons);
    }
}
