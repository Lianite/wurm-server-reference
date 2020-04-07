// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.xml.bind.v2.model.runtime;

import javax.xml.namespace.QName;
import com.sun.xml.bind.v2.runtime.Transducer;
import java.lang.reflect.Type;
import com.sun.xml.bind.v2.model.core.LeafInfo;

public interface RuntimeLeafInfo extends LeafInfo<Type, Class>, RuntimeNonElement
{
     <V> Transducer<V> getTransducer();
    
    Class getClazz();
    
    QName[] getTypeNames();
}
