// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.codemodel;

public interface JAssignmentTarget extends JGenerable, JExpression
{
    JExpression assign(final JExpression p0);
    
    JExpression assignPlus(final JExpression p0);
}
