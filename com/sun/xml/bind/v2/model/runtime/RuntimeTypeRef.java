// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.xml.bind.v2.model.runtime;

import java.lang.reflect.Type;
import com.sun.xml.bind.v2.model.core.TypeRef;

public interface RuntimeTypeRef extends TypeRef<Type, Class>, RuntimeNonElementRef
{
    RuntimeNonElement getTarget();
    
    RuntimePropertyInfo getSource();
}
