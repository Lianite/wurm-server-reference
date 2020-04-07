// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.xml.bind.v2.model.runtime;

import com.sun.xml.bind.v2.runtime.Transducer;
import java.lang.reflect.Type;
import com.sun.xml.bind.v2.model.core.NonElement;

public interface RuntimeNonElement extends NonElement<Type, Class>, RuntimeTypeInfo
{
     <V> Transducer<V> getTransducer();
}
