// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.xml.bind.v2.runtime.reflect.opt;

import com.sun.xml.bind.v2.runtime.reflect.Accessor;

public class FieldAccessor_Short extends Accessor
{
    public FieldAccessor_Short() {
        super(Short.class);
    }
    
    public Object get(final Object bean) {
        return ((Bean)bean).f_short;
    }
    
    public void set(final Object bean, final Object value) {
        ((Bean)bean).f_short = (short)((value == null) ? Const.default_value_short : value);
    }
}
