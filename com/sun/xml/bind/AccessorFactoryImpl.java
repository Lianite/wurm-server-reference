// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.xml.bind;

import java.lang.reflect.Method;
import com.sun.xml.bind.v2.runtime.reflect.Accessor;
import java.lang.reflect.Field;

public class AccessorFactoryImpl implements AccessorFactory
{
    private static AccessorFactoryImpl instance;
    
    public static AccessorFactoryImpl getInstance() {
        return AccessorFactoryImpl.instance;
    }
    
    public Accessor createFieldAccessor(final Class bean, final Field field, final boolean readOnly) {
        return readOnly ? new Accessor.ReadOnlyFieldReflection(field) : new Accessor.FieldReflection(field);
    }
    
    public Accessor createPropertyAccessor(final Class bean, final Method getter, final Method setter) {
        if (getter == null) {
            return new Accessor.SetterOnlyReflection(setter);
        }
        if (setter == null) {
            return new Accessor.GetterOnlyReflection(getter);
        }
        return new Accessor.GetterSetterReflection(getter, setter);
    }
    
    static {
        AccessorFactoryImpl.instance = new AccessorFactoryImpl();
    }
}
