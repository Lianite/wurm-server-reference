// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.xml.bind.v2.model.runtime;

import java.lang.reflect.Type;
import com.sun.xml.bind.v2.model.core.ArrayInfo;

public interface RuntimeArrayInfo extends ArrayInfo<Type, Class>, RuntimeNonElement
{
    Class getType();
    
    RuntimeNonElement getItemType();
}
