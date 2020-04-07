// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.codemodel;

class JContinue implements JStatement
{
    private final JLabel label;
    
    JContinue(final JLabel _label) {
        this.label = _label;
    }
    
    public void state(final JFormatter f) {
        if (this.label == null) {
            f.p("continue;").nl();
        }
        else {
            f.p("continue").p(this.label.label).p(';').nl();
        }
    }
}
