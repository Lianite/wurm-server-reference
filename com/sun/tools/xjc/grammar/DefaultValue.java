// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.tools.xjc.grammar;

import com.sun.codemodel.JExpression;
import com.sun.msv.grammar.ValueExp;
import com.sun.tools.xjc.grammar.xducer.Transducer;

public final class DefaultValue
{
    public final Transducer xducer;
    public final ValueExp value;
    
    public DefaultValue(final Transducer _xducer, final ValueExp _value) {
        this.xducer = _xducer;
        this.value = _value;
    }
    
    public JExpression generateConstant() {
        return this.xducer.generateConstant(this.value);
    }
}
