// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.codemodel;

public abstract class JExpressionImpl implements JExpression
{
    public final JExpression minus() {
        return JOp.minus(this);
    }
    
    public final JExpression not() {
        return JOp.not(this);
    }
    
    public final JExpression complement() {
        return JOp.complement(this);
    }
    
    public final JExpression incr() {
        return JOp.incr(this);
    }
    
    public final JExpression decr() {
        return JOp.decr(this);
    }
    
    public final JExpression plus(final JExpression right) {
        return JOp.plus(this, right);
    }
    
    public final JExpression minus(final JExpression right) {
        return JOp.minus(this, right);
    }
    
    public final JExpression mul(final JExpression right) {
        return JOp.mul(this, right);
    }
    
    public final JExpression div(final JExpression right) {
        return JOp.div(this, right);
    }
    
    public final JExpression mod(final JExpression right) {
        return JOp.mod(this, right);
    }
    
    public final JExpression shl(final JExpression right) {
        return JOp.shl(this, right);
    }
    
    public final JExpression shr(final JExpression right) {
        return JOp.shr(this, right);
    }
    
    public final JExpression shrz(final JExpression right) {
        return JOp.shrz(this, right);
    }
    
    public final JExpression band(final JExpression right) {
        return JOp.band(this, right);
    }
    
    public final JExpression bor(final JExpression right) {
        return JOp.bor(this, right);
    }
    
    public final JExpression cand(final JExpression right) {
        return JOp.cand(this, right);
    }
    
    public final JExpression cor(final JExpression right) {
        return JOp.cor(this, right);
    }
    
    public final JExpression xor(final JExpression right) {
        return JOp.xor(this, right);
    }
    
    public final JExpression lt(final JExpression right) {
        return JOp.lt(this, right);
    }
    
    public final JExpression lte(final JExpression right) {
        return JOp.lte(this, right);
    }
    
    public final JExpression gt(final JExpression right) {
        return JOp.gt(this, right);
    }
    
    public final JExpression gte(final JExpression right) {
        return JOp.gte(this, right);
    }
    
    public final JExpression eq(final JExpression right) {
        return JOp.eq(this, right);
    }
    
    public final JExpression ne(final JExpression right) {
        return JOp.ne(this, right);
    }
    
    public final JExpression _instanceof(final JType right) {
        return JOp._instanceof(this, right);
    }
    
    public final JInvocation invoke(final JMethod method) {
        return JExpr.invoke(this, method);
    }
    
    public final JInvocation invoke(final String method) {
        return JExpr.invoke(this, method);
    }
    
    public final JFieldRef ref(final JVar field) {
        return JExpr.ref(this, field);
    }
    
    public final JFieldRef ref(final String field) {
        return JExpr.ref(this, field);
    }
    
    public final JArrayCompRef component(final JExpression index) {
        return JExpr.component(this, index);
    }
}
