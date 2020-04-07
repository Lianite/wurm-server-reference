// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.tools.xjc.reader.decorator;

import com.sun.msv.grammar.Expression;
import com.sun.msv.reader.State;

public interface Decorator
{
    Expression decorate(final State p0, final Expression p1);
}
