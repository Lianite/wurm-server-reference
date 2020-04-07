// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.xml.bind.v2.model.impl;

import com.sun.xml.bind.v2.runtime.Transducer;
import com.sun.xml.bind.v2.model.nav.Navigator;
import com.sun.xml.bind.v2.model.runtime.RuntimeNonElement;
import java.lang.reflect.Type;

final class RuntimeAnyTypeImpl extends AnyTypeImpl<Type, Class> implements RuntimeNonElement
{
    static final RuntimeNonElement theInstance;
    
    private RuntimeAnyTypeImpl() {
        super(Navigator.REFLECTION);
    }
    
    public <V> Transducer<V> getTransducer() {
        return null;
    }
    
    static {
        theInstance = new RuntimeAnyTypeImpl();
    }
}
