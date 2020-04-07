// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.tools.xjc.grammar.util;

import com.sun.msv.grammar.ElementExp;
import com.sun.msv.grammar.AttributeExp;
import com.sun.msv.grammar.ReferenceExp;
import com.sun.msv.grammar.ChoiceNameClass;
import java.util.HashSet;
import com.sun.msv.grammar.ExpressionVisitorVoid;
import com.sun.msv.grammar.Expression;
import java.util.Set;
import com.sun.msv.grammar.NameClass;
import com.sun.msv.grammar.util.ExpressionWalker;

public abstract class NameFinder extends ExpressionWalker
{
    private NameClass nc;
    private final Set visited;
    
    public static NameClass findElement(final Expression e) {
        return find(e, (NameFinder)new NameFinder$1());
    }
    
    public static NameClass findAttribute(final Expression e) {
        return find(e, (NameFinder)new NameFinder$2());
    }
    
    private static NameClass find(final Expression e, final NameFinder f) {
        e.visit((ExpressionVisitorVoid)f);
        if (f.nc == null) {
            return NameClass.NONE;
        }
        return f.nc.simplify();
    }
    
    protected NameFinder() {
        this.nc = null;
        this.visited = new HashSet();
    }
    
    protected void onName(final NameClass child) {
        if (this.nc == null) {
            this.nc = child;
        }
        else {
            this.nc = (NameClass)new ChoiceNameClass(this.nc, child);
        }
    }
    
    public void onRef(final ReferenceExp exp) {
        if (this.visited.add(exp)) {
            super.onRef(exp);
        }
    }
    
    public void onAttribute(final AttributeExp exp) {
    }
    
    public void onElement(final ElementExp exp) {
    }
}
