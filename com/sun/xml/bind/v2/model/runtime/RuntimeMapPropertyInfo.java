// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.xml.bind.v2.model.runtime;

import java.lang.reflect.Type;
import com.sun.xml.bind.v2.model.core.MapPropertyInfo;

public interface RuntimeMapPropertyInfo extends RuntimePropertyInfo, MapPropertyInfo<Type, Class>
{
    RuntimeNonElement getKeyType();
    
    RuntimeNonElement getValueType();
}
