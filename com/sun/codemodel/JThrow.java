// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.codemodel;

class JThrow implements JStatement
{
    private JExpression expr;
    
    JThrow(final JExpression expr) {
        this.expr = expr;
    }
    
    public void state(final JFormatter f) {
        f.p("throw");
        f.g(this.expr);
        f.p(';').nl();
    }
}
