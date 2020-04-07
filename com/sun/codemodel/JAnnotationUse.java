// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.codemodel;

import java.util.Iterator;
import java.lang.annotation.Annotation;
import java.util.LinkedHashMap;
import java.util.Map;

public final class JAnnotationUse extends JAnnotationValue
{
    private final JClass clazz;
    private Map<String, JAnnotationValue> memberValues;
    
    JAnnotationUse(final JClass clazz) {
        this.clazz = clazz;
    }
    
    private JCodeModel owner() {
        return this.clazz.owner();
    }
    
    private void addValue(final String name, final JAnnotationValue annotationValue) {
        if (this.memberValues == null) {
            this.memberValues = new LinkedHashMap<String, JAnnotationValue>();
        }
        this.memberValues.put(name, annotationValue);
    }
    
    public JAnnotationUse param(final String name, final boolean value) {
        this.addValue(name, new JAnnotationStringValue(JExpr.lit(value)));
        return this;
    }
    
    public JAnnotationUse param(final String name, final int value) {
        this.addValue(name, new JAnnotationStringValue(JExpr.lit(value)));
        return this;
    }
    
    public JAnnotationUse param(final String name, final String value) {
        this.addValue(name, new JAnnotationStringValue(JExpr.lit(value)));
        return this;
    }
    
    public JAnnotationUse annotationParam(final String name, final Class<? extends Annotation> value) {
        final JAnnotationUse annotationUse = new JAnnotationUse(this.owner().ref(value));
        this.addValue(name, annotationUse);
        return annotationUse;
    }
    
    public JAnnotationUse param(final String name, final Enum value) {
        this.addValue(name, new JAnnotationValue() {
            public void generate(final JFormatter f) {
                f.t(JAnnotationUse.this.owner().ref(value.getDeclaringClass())).p('.').p(value.name());
            }
        });
        return this;
    }
    
    public JAnnotationUse param(final String name, final JEnumConstant value) {
        this.addValue(name, new JAnnotationStringValue(value));
        return this;
    }
    
    public JAnnotationUse param(final String name, final Class value) {
        return this.param(name, this.clazz.owner().ref(value));
    }
    
    public JAnnotationUse param(final String name, final JType type) {
        final JClass clazz = type.boxify();
        this.addValue(name, new JAnnotationStringValue(clazz.dotclass()));
        return this;
    }
    
    public JAnnotationArrayMember paramArray(final String name) {
        final JAnnotationArrayMember arrayMember = new JAnnotationArrayMember(this.owner());
        this.addValue(name, arrayMember);
        return arrayMember;
    }
    
    public JAnnotationUse annotate(final Class<? extends Annotation> clazz) {
        final JAnnotationUse annotationUse = new JAnnotationUse(this.owner().ref(clazz));
        return annotationUse;
    }
    
    public void generate(final JFormatter f) {
        f.p('@').g(this.clazz);
        if (this.memberValues != null) {
            f.p('(');
            boolean first = true;
            if (this.isOptimizable()) {
                f.g(this.memberValues.get("value"));
            }
            else {
                for (final Map.Entry<String, JAnnotationValue> mapEntry : this.memberValues.entrySet()) {
                    if (!first) {
                        f.p(',');
                    }
                    f.p(mapEntry.getKey()).p('=').g(mapEntry.getValue());
                    first = false;
                }
            }
            f.p(')');
        }
    }
    
    private boolean isOptimizable() {
        return this.memberValues.size() == 1 && this.memberValues.containsKey("value");
    }
}
