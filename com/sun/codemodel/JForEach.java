// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.codemodel;

public final class JForEach implements JStatement
{
    private final JType type;
    private final String var;
    private JBlock body;
    private final JExpression collection;
    private final JVar loopVar;
    
    public JForEach(final JType vartype, final String variable, final JExpression collection) {
        this.body = null;
        this.type = vartype;
        this.var = variable;
        this.collection = collection;
        this.loopVar = new JVar(JMods.forVar(0), this.type, this.var, collection);
    }
    
    public JVar var() {
        return this.loopVar;
    }
    
    public JBlock body() {
        if (this.body == null) {
            this.body = new JBlock();
        }
        return this.body;
    }
    
    public void state(final JFormatter f) {
        f.p("for (");
        f.g(this.type).id(this.var).p(": ").g(this.collection);
        f.p(')');
        if (this.body != null) {
            f.g(this.body).nl();
        }
        else {
            f.p(';').nl();
        }
    }
}
