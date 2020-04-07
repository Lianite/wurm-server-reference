// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.xml.bind.v2.model.runtime;

import com.sun.xml.bind.v2.runtime.reflect.Accessor;
import java.util.Collection;
import java.lang.reflect.Type;
import com.sun.xml.bind.v2.model.core.PropertyInfo;

public interface RuntimePropertyInfo extends PropertyInfo<Type, Class>
{
    Collection<? extends RuntimeTypeInfo> ref();
    
    Accessor getAccessor();
    
    boolean elementOnlyContent();
    
    Type getRawType();
    
    Type getIndividualType();
}
