// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.xml.bind.v2.runtime.reflect.opt;

import com.sun.xml.bind.v2.runtime.reflect.Accessor;

public class FieldAccessor_Double extends Accessor
{
    public FieldAccessor_Double() {
        super(Double.class);
    }
    
    public Object get(final Object bean) {
        return ((Bean)bean).f_double;
    }
    
    public void set(final Object bean, final Object value) {
        ((Bean)bean).f_double = (double)((value == null) ? Const.default_value_double : value);
    }
}
