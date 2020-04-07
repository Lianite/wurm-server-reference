// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.tools.xjc.generator;

import com.sun.codemodel.JType;
import com.sun.tools.xjc.grammar.TypeItem;
import java.util.Iterator;
import com.sun.tools.xjc.grammar.FieldItem;
import com.sun.tools.xjc.grammar.FieldUse;

public class JavadocBuilder
{
    public static String listPossibleTypes(final FieldUse fu) {
        final StringBuffer buf = new StringBuffer();
        for (final FieldItem fi : fu.items) {
            final TypeItem[] types = fi.listTypes();
            for (int i = 0; i < types.length; ++i) {
                final JType t = types[i].getType();
                if (t.isPrimitive() || t.isArray()) {
                    buf.append(t.fullName());
                }
                else {
                    buf.append("{@link " + t.fullName() + "}\n");
                }
            }
        }
        return buf.toString();
    }
}
