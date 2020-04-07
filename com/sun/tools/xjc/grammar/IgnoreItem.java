// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.tools.xjc.grammar;

import com.sun.msv.grammar.Expression;
import org.xml.sax.Locator;

public class IgnoreItem extends JavaItem
{
    public IgnoreItem(final Locator loc) {
        super("$ignore", loc);
    }
    
    public IgnoreItem(final Expression exp, final Locator loc) {
        this(loc);
        this.exp = exp;
    }
    
    public Object visitJI(final JavaItemVisitor visitor) {
        return visitor.onIgnore(this);
    }
}
