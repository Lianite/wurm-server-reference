// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.codemodel;

public interface JExpression extends JGenerable
{
    JExpression minus();
    
    JExpression not();
    
    JExpression complement();
    
    JExpression incr();
    
    JExpression decr();
    
    JExpression plus(final JExpression p0);
    
    JExpression minus(final JExpression p0);
    
    JExpression mul(final JExpression p0);
    
    JExpression div(final JExpression p0);
    
    JExpression mod(final JExpression p0);
    
    JExpression shl(final JExpression p0);
    
    JExpression shr(final JExpression p0);
    
    JExpression shrz(final JExpression p0);
    
    JExpression band(final JExpression p0);
    
    JExpression bor(final JExpression p0);
    
    JExpression cand(final JExpression p0);
    
    JExpression cor(final JExpression p0);
    
    JExpression xor(final JExpression p0);
    
    JExpression lt(final JExpression p0);
    
    JExpression lte(final JExpression p0);
    
    JExpression gt(final JExpression p0);
    
    JExpression gte(final JExpression p0);
    
    JExpression eq(final JExpression p0);
    
    JExpression ne(final JExpression p0);
    
    JExpression _instanceof(final JType p0);
    
    JInvocation invoke(final JMethod p0);
    
    JInvocation invoke(final String p0);
    
    JFieldRef ref(final JVar p0);
    
    JFieldRef ref(final String p0);
    
    JArrayCompRef component(final JExpression p0);
}
