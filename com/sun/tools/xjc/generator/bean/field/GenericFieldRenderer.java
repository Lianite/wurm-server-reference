// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.tools.xjc.generator.bean.field;

import java.lang.reflect.InvocationTargetException;
import com.sun.tools.xjc.outline.FieldOutline;
import com.sun.tools.xjc.model.CPropertyInfo;
import com.sun.tools.xjc.generator.bean.ClassOutlineImpl;
import java.lang.reflect.Constructor;

public final class GenericFieldRenderer implements FieldRenderer
{
    private Constructor constructor;
    
    public GenericFieldRenderer(final Class fieldClass) {
        try {
            this.constructor = fieldClass.getDeclaredConstructor(ClassOutlineImpl.class, CPropertyInfo.class);
        }
        catch (NoSuchMethodException e) {
            throw new NoSuchMethodError(e.getMessage());
        }
    }
    
    public FieldOutline generate(final ClassOutlineImpl context, final CPropertyInfo prop) {
        try {
            return this.constructor.newInstance(context, prop);
        }
        catch (InstantiationException e) {
            throw new InstantiationError(e.getMessage());
        }
        catch (IllegalAccessException e2) {
            throw new IllegalAccessError(e2.getMessage());
        }
        catch (InvocationTargetException e3) {
            final Throwable t = e3.getTargetException();
            if (t instanceof RuntimeException) {
                throw (RuntimeException)t;
            }
            if (t instanceof Error) {
                throw (Error)t;
            }
            throw new AssertionError((Object)t);
        }
    }
}
