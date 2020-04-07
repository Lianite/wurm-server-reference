// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.codemodel;

public class JStringLiteral extends JExpressionImpl
{
    public final String str;
    
    JStringLiteral(final String what) {
        this.str = what;
    }
    
    public void generate(final JFormatter f) {
        f.p(JExpr.quotify('\"', this.str));
    }
}
