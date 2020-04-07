// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.codemodel;

public class JDoLoop implements JStatement
{
    private JExpression test;
    private JBlock body;
    
    JDoLoop(final JExpression test) {
        this.body = null;
        this.test = test;
    }
    
    public JBlock body() {
        if (this.body == null) {
            this.body = new JBlock();
        }
        return this.body;
    }
    
    public void state(final JFormatter f) {
        f.p("do");
        if (this.body != null) {
            f.g(this.body);
        }
        else {
            f.p("{ }");
        }
        if (JOp.hasTopOp(this.test)) {
            f.p("while ").g(this.test);
        }
        else {
            f.p("while (").g(this.test).p(')');
        }
        f.p(';').nl();
    }
}
