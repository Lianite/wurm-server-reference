// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.tools.xjc.grammar;

import org.xml.sax.Locator;
import com.sun.msv.grammar.Expression;

public class SuperClassItem extends JavaItem
{
    public ClassItem definition;
    
    public SuperClassItem(final Expression exp, final Locator loc) {
        super("superClass-marker", loc);
        this.definition = null;
        this.exp = exp;
    }
    
    public Object visitJI(final JavaItemVisitor visitor) {
        return visitor.onSuper(this);
    }
}
