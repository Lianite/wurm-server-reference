// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.xml.bind.v2.model.runtime;

import java.lang.reflect.Type;
import com.sun.xml.bind.v2.model.core.AttributePropertyInfo;

public interface RuntimeAttributePropertyInfo extends AttributePropertyInfo<Type, Class>, RuntimePropertyInfo, RuntimeNonElementRef
{
    RuntimeNonElement getTarget();
}
