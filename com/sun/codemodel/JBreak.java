// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.codemodel;

class JBreak implements JStatement
{
    private final JLabel label;
    
    JBreak(final JLabel _label) {
        this.label = _label;
    }
    
    public void state(final JFormatter f) {
        if (this.label == null) {
            f.p("break;").nl();
        }
        else {
            f.p("break").p(this.label.label).p(';').nl();
        }
    }
}
