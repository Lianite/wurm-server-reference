// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.tools.xjc.grammar;

import com.sun.codemodel.JPrimitiveType;
import com.sun.codemodel.JClass;
import com.sun.codemodel.JType;
import org.xml.sax.Locator;

public abstract class TypeItem extends JavaItem
{
    public TypeItem(final String displayName, final Locator loc) {
        super(displayName, loc);
    }
    
    public abstract JType getType();
    
    public static void sort(final TypeItem[] t) {
        for (int i = 0; i < t.length - 1; ++i) {
            int k = i;
            JClass tk = toJClass(t[k]);
            for (int j = i + 1; j < t.length; ++j) {
                final JClass tj = toJClass(t[j]);
                if (tk.isAssignableFrom(tj)) {
                    k = j;
                    tk = tj;
                }
            }
            final TypeItem tmp = t[i];
            t[i] = t[k];
            t[k] = tmp;
        }
    }
    
    private static JClass toJClass(final TypeItem t) {
        final JType jt = t.getType();
        if (jt.isPrimitive()) {
            return ((JPrimitiveType)jt).getWrapperClass();
        }
        return (JClass)jt;
    }
    
    public String toString() {
        return this.getClass().getName() + '[' + this.getType().fullName() + ']';
    }
}
