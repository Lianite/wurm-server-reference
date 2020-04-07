// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.xml.bind.v2.runtime.reflect.opt;

import com.sun.xml.bind.v2.runtime.reflect.Accessor;

public class FieldAccessor_Integer extends Accessor
{
    public FieldAccessor_Integer() {
        super(Integer.class);
    }
    
    public Object get(final Object bean) {
        return ((Bean)bean).f_int;
    }
    
    public void set(final Object bean, final Object value) {
        ((Bean)bean).f_int = (int)((value == null) ? Const.default_value_int : value);
    }
}
