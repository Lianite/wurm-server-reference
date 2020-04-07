// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.xml.bind.v2.runtime.reflect.opt;

import com.sun.xml.bind.v2.runtime.reflect.Accessor;

public class FieldAccessor_Long extends Accessor
{
    public FieldAccessor_Long() {
        super(Long.class);
    }
    
    public Object get(final Object bean) {
        return ((Bean)bean).f_long;
    }
    
    public void set(final Object bean, final Object value) {
        ((Bean)bean).f_long = (long)((value == null) ? Const.default_value_long : value);
    }
}
