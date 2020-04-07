// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.xml.bind.v2.runtime.reflect.opt;

import com.sun.xml.bind.v2.runtime.reflect.Accessor;

public class MethodAccessor_Double extends Accessor
{
    public MethodAccessor_Double() {
        super(Double.class);
    }
    
    public Object get(final Object bean) {
        return ((Bean)bean).get_double();
    }
    
    public void set(final Object bean, final Object value) {
        ((Bean)bean).set_double((value == null) ? Const.default_value_double : ((double)value));
    }
}
