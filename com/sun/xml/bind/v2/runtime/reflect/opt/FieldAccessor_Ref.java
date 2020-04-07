// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.xml.bind.v2.runtime.reflect.opt;

import com.sun.xml.bind.v2.runtime.reflect.Accessor;

public class FieldAccessor_Ref extends Accessor
{
    public FieldAccessor_Ref() {
        super(Ref.class);
    }
    
    public Object get(final Object bean) {
        return ((Bean)bean).f_ref;
    }
    
    public void set(final Object bean, final Object value) {
        ((Bean)bean).f_ref = (Ref)value;
    }
}
