// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.codemodel;

class JReturn implements JStatement
{
    private JExpression expr;
    
    JReturn(final JExpression expr) {
        this.expr = expr;
    }
    
    public void state(final JFormatter f) {
        f.p("return ");
        if (this.expr != null) {
            f.g(this.expr);
        }
        f.p(';').nl();
    }
}
