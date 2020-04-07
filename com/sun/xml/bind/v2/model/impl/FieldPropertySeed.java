// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.xml.bind.v2.model.impl;

import com.sun.xml.bind.v2.runtime.Location;
import com.sun.xml.bind.v2.model.annotation.Locatable;
import java.lang.annotation.Annotation;

class FieldPropertySeed<TypeT, ClassDeclT, FieldT, MethodT> implements PropertySeed<TypeT, ClassDeclT, FieldT, MethodT>
{
    protected final FieldT field;
    private ClassInfoImpl<TypeT, ClassDeclT, FieldT, MethodT> parent;
    
    FieldPropertySeed(final ClassInfoImpl<TypeT, ClassDeclT, FieldT, MethodT> classInfo, final FieldT field) {
        this.parent = classInfo;
        this.field = field;
    }
    
    public <A extends Annotation> A readAnnotation(final Class<A> a) {
        return this.parent.reader().getFieldAnnotation(a, this.field, this);
    }
    
    public boolean hasAnnotation(final Class<? extends Annotation> annotationType) {
        return this.parent.reader().hasFieldAnnotation(annotationType, this.field);
    }
    
    public String getName() {
        return this.parent.nav().getFieldName(this.field);
    }
    
    public TypeT getRawType() {
        return this.parent.nav().getFieldType(this.field);
    }
    
    public Locatable getUpstream() {
        return this.parent;
    }
    
    public Location getLocation() {
        return this.parent.nav().getFieldLocation(this.field);
    }
}
