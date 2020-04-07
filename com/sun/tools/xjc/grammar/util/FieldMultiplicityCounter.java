// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.tools.xjc.grammar.util;

import com.sun.tools.xjc.grammar.FieldUse;
import com.sun.msv.grammar.ExpressionVisitor;
import com.sun.tools.xjc.grammar.FieldItem;
import com.sun.msv.grammar.Expression;

public class FieldMultiplicityCounter extends MultiplicityCounter
{
    private final String name;
    
    private FieldMultiplicityCounter(final String _name) {
        this.name = _name;
    }
    
    public static Multiplicity count(final Expression exp, final FieldItem fi) {
        return (Multiplicity)exp.visit((ExpressionVisitor)new FieldMultiplicityCounter(fi.name));
    }
    
    public static Multiplicity count(final Expression exp, final FieldUse fu) {
        return (Multiplicity)exp.visit((ExpressionVisitor)new FieldMultiplicityCounter(fu.name));
    }
    
    protected Multiplicity isChild(final Expression exp) {
        if (!(exp instanceof FieldItem)) {
            return null;
        }
        final FieldItem fi = (FieldItem)exp;
        if (fi.name.equals(this.name)) {
            return fi.multiplicity;
        }
        return Multiplicity.zero;
    }
}
