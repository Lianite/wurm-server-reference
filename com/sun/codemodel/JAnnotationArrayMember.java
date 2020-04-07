// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.codemodel;

import java.util.Iterator;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;

public final class JAnnotationArrayMember extends JAnnotationValue implements JAnnotatable
{
    private final List<JAnnotationValue> values;
    private final JCodeModel owner;
    
    JAnnotationArrayMember(final JCodeModel owner) {
        this.values = new ArrayList<JAnnotationValue>();
        this.owner = owner;
    }
    
    public JAnnotationArrayMember param(final String value) {
        final JAnnotationValue annotationValue = new JAnnotationStringValue(JExpr.lit(value));
        this.values.add(annotationValue);
        return this;
    }
    
    public JAnnotationArrayMember param(final boolean value) {
        final JAnnotationValue annotationValue = new JAnnotationStringValue(JExpr.lit(value));
        this.values.add(annotationValue);
        return this;
    }
    
    public JAnnotationArrayMember param(final int value) {
        final JAnnotationValue annotationValue = new JAnnotationStringValue(JExpr.lit(value));
        this.values.add(annotationValue);
        return this;
    }
    
    public JAnnotationArrayMember param(final float value) {
        final JAnnotationValue annotationValue = new JAnnotationStringValue(JExpr.lit(value));
        this.values.add(annotationValue);
        return this;
    }
    
    public JAnnotationArrayMember param(final Class value) {
        final JAnnotationValue annotationValue = new JAnnotationStringValue(JExpr.lit(value.getName()));
        this.values.add(annotationValue);
        return this;
    }
    
    public JAnnotationArrayMember param(final JType type) {
        final JClass clazz = type.boxify();
        final JAnnotationValue annotationValue = new JAnnotationStringValue(clazz.dotclass());
        this.values.add(annotationValue);
        return this;
    }
    
    public JAnnotationUse annotate(final Class<? extends Annotation> clazz) {
        return this.annotate(this.owner.ref(clazz));
    }
    
    public JAnnotationUse annotate(final JClass clazz) {
        final JAnnotationUse a = new JAnnotationUse(clazz);
        this.values.add(a);
        return a;
    }
    
    public <W extends JAnnotationWriter> W annotate2(final Class<W> clazz) {
        return TypedAnnotationWriter.create(clazz, this);
    }
    
    public JAnnotationArrayMember param(final JAnnotationUse value) {
        this.values.add(value);
        return this;
    }
    
    public void generate(final JFormatter f) {
        f.p('{').nl().i();
        boolean first = true;
        for (final JAnnotationValue aValue : this.values) {
            if (!first) {
                f.p(',').nl();
            }
            f.g(aValue);
            first = false;
        }
        f.nl().o().p('}');
    }
}
