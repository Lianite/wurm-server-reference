// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.xml.bind.v2.model.impl;

import com.sun.xml.bind.v2.model.core.PropertyInfo;
import java.util.Collection;
import com.sun.xml.bind.v2.model.core.NonElement;
import com.sun.xml.bind.v2.model.runtime.RuntimePropertyInfo;
import java.util.List;
import com.sun.xml.bind.v2.model.runtime.RuntimeNonElement;
import com.sun.xml.bind.v2.model.runtime.RuntimeAttributePropertyInfo;
import java.lang.reflect.Method;
import java.lang.reflect.Field;
import java.lang.reflect.Type;

class RuntimeAttributePropertyInfoImpl extends AttributePropertyInfoImpl<Type, Class, Field, Method> implements RuntimeAttributePropertyInfo
{
    RuntimeAttributePropertyInfoImpl(final RuntimeClassInfoImpl classInfo, final PropertySeed<Type, Class, Field, Method> seed) {
        super(classInfo, seed);
    }
    
    public boolean elementOnlyContent() {
        return true;
    }
    
    public RuntimeNonElement getTarget() {
        return (RuntimeNonElement)super.getTarget();
    }
    
    public List<? extends RuntimeNonElement> ref() {
        return (List<? extends RuntimeNonElement>)super.ref();
    }
    
    public RuntimePropertyInfo getSource() {
        return this;
    }
    
    public void link() {
        this.getTransducer();
        super.link();
    }
}
