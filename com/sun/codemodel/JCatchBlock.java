// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.codemodel;

public class JCatchBlock implements JGenerable
{
    JClass exception;
    private JVar var;
    private JBlock body;
    
    JCatchBlock(final JClass exception) {
        this.var = null;
        this.body = new JBlock();
        this.exception = exception;
    }
    
    public JVar param(final String name) {
        if (this.var != null) {
            throw new IllegalStateException();
        }
        return this.var = new JVar(JMods.forVar(0), this.exception, name, null);
    }
    
    public JBlock body() {
        return this.body;
    }
    
    public void generate(final JFormatter f) {
        if (this.var == null) {
            this.var = new JVar(JMods.forVar(0), this.exception, "_x", null);
        }
        f.p("catch (").b(this.var).p(')').g(this.body);
    }
}
