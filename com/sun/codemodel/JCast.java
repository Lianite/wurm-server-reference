// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.codemodel;

public class JCast extends JExpressionImpl
{
    private JType type;
    private JExpression object;
    
    JCast(final JType type, final JExpression object) {
        this.type = type;
        this.object = object;
    }
    
    public void generate(final JFormatter f) {
        f.p("((").g(this.type).p(')').g(this.object).p(')');
    }
}
