// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.tools.xjc.grammar;

import org.xml.sax.Locator;
import com.sun.msv.grammar.OtherExp;

public abstract class JavaItem extends OtherExp
{
    public String name;
    public final Locator locator;
    
    public JavaItem(final String name, final Locator loc) {
        this.name = name;
        this.locator = loc;
    }
    
    public abstract Object visitJI(final JavaItemVisitor p0);
}
