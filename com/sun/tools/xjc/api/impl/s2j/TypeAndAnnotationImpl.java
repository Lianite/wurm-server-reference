// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.tools.xjc.api.impl.s2j;

import com.sun.xml.bind.v2.model.core.TypeInfo;
import javax.xml.bind.annotation.XmlList;
import com.sun.tools.xjc.model.nav.NClass;
import com.sun.tools.xjc.generator.annotation.spec.XmlJavaTypeAdapterWriter;
import java.lang.annotation.Annotation;
import javax.xml.bind.annotation.XmlAttachmentRef;
import com.sun.xml.bind.v2.runtime.SwaRefAdapter;
import com.sun.codemodel.JAnnotatable;
import com.sun.codemodel.JPrimitiveType;
import com.sun.tools.xjc.model.CAdapter;
import com.sun.tools.xjc.outline.Aspect;
import com.sun.tools.xjc.model.nav.NType;
import com.sun.codemodel.JType;
import com.sun.tools.xjc.outline.Outline;
import com.sun.tools.xjc.model.TypeUse;
import com.sun.tools.xjc.api.TypeAndAnnotation;

final class TypeAndAnnotationImpl implements TypeAndAnnotation
{
    private final TypeUse typeUse;
    private final Outline outline;
    
    public TypeAndAnnotationImpl(final Outline outline, final TypeUse typeUse) {
        this.typeUse = typeUse;
        this.outline = outline;
    }
    
    public JType getTypeClass() {
        final CAdapter a = this.typeUse.getAdapterUse();
        NType nt;
        if (a != null) {
            nt = (NType)a.customType;
        }
        else {
            nt = ((TypeInfo<NType, C>)this.typeUse.getInfo()).getType();
        }
        JType jt = nt.toType(this.outline, Aspect.EXPOSED);
        final JPrimitiveType prim = jt.boxify().getPrimitiveType();
        if (!this.typeUse.isCollection() && prim != null) {
            jt = prim;
        }
        if (this.typeUse.isCollection()) {
            jt = jt.array();
        }
        return jt;
    }
    
    public void annotate(final JAnnotatable programElement) {
        if (this.typeUse.getAdapterUse() == null && !this.typeUse.isCollection()) {
            return;
        }
        final CAdapter adapterUse = this.typeUse.getAdapterUse();
        if (adapterUse != null) {
            if (adapterUse.getAdapterIfKnown() == SwaRefAdapter.class) {
                programElement.annotate(XmlAttachmentRef.class);
            }
            else {
                programElement.annotate2(XmlJavaTypeAdapterWriter.class).value(((NClass)adapterUse.adapterType).toType(this.outline, Aspect.EXPOSED));
            }
        }
        if (this.typeUse.isCollection()) {
            programElement.annotate(XmlList.class);
        }
    }
    
    public String toString() {
        final StringBuilder builder = new StringBuilder();
        builder.append(this.getTypeClass());
        return builder.toString();
    }
    
    public boolean equals(final Object o) {
        if (!(o instanceof TypeAndAnnotationImpl)) {
            return false;
        }
        final TypeAndAnnotationImpl that = (TypeAndAnnotationImpl)o;
        return this.typeUse == that.typeUse;
    }
    
    public int hashCode() {
        return this.typeUse.hashCode();
    }
}
