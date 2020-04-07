// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.xml.bind.v2.runtime.reflect.opt;

import com.sun.xml.bind.v2.runtime.reflect.Accessor;

public class FieldAccessor_Float extends Accessor
{
    public FieldAccessor_Float() {
        super(Float.class);
    }
    
    public Object get(final Object bean) {
        return ((Bean)bean).f_float;
    }
    
    public void set(final Object bean, final Object value) {
        ((Bean)bean).f_float = (float)((value == null) ? Const.default_value_float : value);
    }
}
