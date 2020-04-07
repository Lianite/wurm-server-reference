// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.codemodel;

public class JArrayCompRef extends JExpressionImpl implements JAssignmentTarget
{
    private JExpression array;
    private JExpression index;
    
    JArrayCompRef(final JExpression array, final JExpression index) {
        if (array == null || index == null) {
            throw new NullPointerException();
        }
        this.array = array;
        this.index = index;
    }
    
    public void generate(final JFormatter f) {
        f.g(this.array).p('[').g(this.index).p(']');
    }
    
    public JExpression assign(final JExpression rhs) {
        return JExpr.assign(this, rhs);
    }
    
    public JExpression assignPlus(final JExpression rhs) {
        return JExpr.assignPlus(this, rhs);
    }
}
