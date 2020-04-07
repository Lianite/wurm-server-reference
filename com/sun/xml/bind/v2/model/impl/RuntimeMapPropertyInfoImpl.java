// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.xml.bind.v2.model.impl;

import java.util.Collection;
import com.sun.xml.bind.v2.model.core.NonElement;
import com.sun.xml.bind.v2.model.runtime.RuntimeTypeInfo;
import java.util.List;
import com.sun.xml.bind.v2.model.runtime.RuntimeNonElement;
import com.sun.xml.bind.v2.runtime.reflect.Accessor;
import com.sun.xml.bind.v2.model.runtime.RuntimeMapPropertyInfo;
import java.lang.reflect.Method;
import java.lang.reflect.Field;
import java.lang.reflect.Type;

class RuntimeMapPropertyInfoImpl extends MapPropertyInfoImpl<Type, Class, Field, Method> implements RuntimeMapPropertyInfo
{
    private final Accessor acc;
    
    RuntimeMapPropertyInfoImpl(final RuntimeClassInfoImpl classInfo, final PropertySeed<Type, Class, Field, Method> seed) {
        super(classInfo, seed);
        this.acc = ((RuntimeClassInfoImpl.RuntimePropertySeed)seed).getAccessor();
    }
    
    public Accessor getAccessor() {
        return this.acc;
    }
    
    public boolean elementOnlyContent() {
        return true;
    }
    
    public RuntimeNonElement getKeyType() {
        return (RuntimeNonElement)super.getKeyType();
    }
    
    public RuntimeNonElement getValueType() {
        return (RuntimeNonElement)super.getValueType();
    }
    
    public List<? extends RuntimeTypeInfo> ref() {
        return (List<? extends RuntimeTypeInfo>)(List)super.ref();
    }
}
