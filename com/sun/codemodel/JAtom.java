// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.codemodel;

class JAtom extends JExpressionImpl
{
    String what;
    
    JAtom(final String what) {
        this.what = what;
    }
    
    public void generate(final JFormatter f) {
        f.p(this.what);
    }
}
