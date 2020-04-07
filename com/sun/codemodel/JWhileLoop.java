// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.codemodel;

public class JWhileLoop implements JStatement
{
    private JExpression test;
    private JBlock body;
    
    JWhileLoop(final JExpression test) {
        this.body = null;
        this.test = test;
    }
    
    public JExpression test() {
        return this.test;
    }
    
    public JBlock body() {
        if (this.body == null) {
            this.body = new JBlock();
        }
        return this.body;
    }
    
    public void state(final JFormatter f) {
        if (JOp.hasTopOp(this.test)) {
            f.p("while ").g(this.test);
        }
        else {
            f.p("while (").g(this.test).p(')');
        }
        if (this.body != null) {
            f.s(this.body);
        }
        else {
            f.p(';').nl();
        }
    }
}
