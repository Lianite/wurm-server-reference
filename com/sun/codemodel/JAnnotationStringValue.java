// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.codemodel;

final class JAnnotationStringValue extends JAnnotationValue
{
    private final JExpression value;
    
    JAnnotationStringValue(final JExpression value) {
        this.value = value;
    }
    
    public void generate(final JFormatter f) {
        f.g(this.value);
    }
}
