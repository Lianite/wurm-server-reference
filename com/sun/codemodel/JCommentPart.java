// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.codemodel;

import java.util.Iterator;
import java.util.Collection;
import java.util.ArrayList;

public class JCommentPart extends ArrayList<Object>
{
    public JCommentPart append(final Object o) {
        this.add(o);
        return this;
    }
    
    public boolean add(final Object o) {
        this.flattenAppend(o);
        return true;
    }
    
    private void flattenAppend(final Object value) {
        if (value == null) {
            return;
        }
        if (value instanceof Object[]) {
            for (final Object o : (Object[])value) {
                this.flattenAppend(o);
            }
        }
        else if (value instanceof Collection) {
            for (final Object o2 : (Collection)value) {
                this.flattenAppend(o2);
            }
        }
        else {
            super.add(value);
        }
    }
    
    protected void format(final JFormatter f, final String indent) {
        if (!f.isPrinting()) {
            for (final Object o : this) {
                if (o instanceof JClass) {
                    f.g((JGenerable)o);
                }
            }
            return;
        }
        if (!this.isEmpty()) {
            f.p(indent);
        }
        for (final Object o : this) {
            if (o instanceof String) {
                String s = (String)o;
                int idx;
                while ((idx = s.indexOf(10)) != -1) {
                    final String line = s.substring(0, idx);
                    if (line.length() > 0) {
                        f.p(line);
                    }
                    s = s.substring(idx + 1);
                    f.nl().p(indent);
                }
                if (s.length() == 0) {
                    continue;
                }
                f.p(s);
            }
            else if (o instanceof JClass) {
                ((JClass)o).printLink(f);
            }
            else {
                if (!(o instanceof JType)) {
                    throw new IllegalStateException();
                }
                f.g((JGenerable)o);
            }
        }
        if (!this.isEmpty()) {
            f.nl();
        }
    }
}
