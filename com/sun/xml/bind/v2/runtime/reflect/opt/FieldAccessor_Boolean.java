// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.xml.bind.v2.runtime.reflect.opt;

import com.sun.xml.bind.v2.runtime.reflect.Accessor;

public class FieldAccessor_Boolean extends Accessor
{
    public FieldAccessor_Boolean() {
        super(Boolean.class);
    }
    
    public Object get(final Object bean) {
        return ((Bean)bean).f_boolean;
    }
    
    public void set(final Object bean, final Object value) {
        ((Bean)bean).f_boolean = (boolean)((value == null) ? Const.default_value_boolean : value);
    }
}
