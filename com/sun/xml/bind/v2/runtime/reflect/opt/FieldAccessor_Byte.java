// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.xml.bind.v2.runtime.reflect.opt;

import com.sun.xml.bind.v2.runtime.reflect.Accessor;

public class FieldAccessor_Byte extends Accessor
{
    public FieldAccessor_Byte() {
        super(Byte.class);
    }
    
    public Object get(final Object bean) {
        return ((Bean)bean).f_byte;
    }
    
    public void set(final Object bean, final Object value) {
        ((Bean)bean).f_byte = (byte)((value == null) ? Const.default_value_byte : value);
    }
}
