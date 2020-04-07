// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.tools.xjc.reader.xmlschema.ct;

import com.sun.xml.bind.JAXBAssertionError;
import com.sun.msv.grammar.ExpressionPool;
import com.sun.tools.xjc.reader.xmlschema.BGMBuilder;

abstract class AbstractCTBuilder implements CTBuilder
{
    protected final ComplexTypeFieldBuilder builder;
    protected final BGMBuilder bgmBuilder;
    protected final ExpressionPool pool;
    
    protected AbstractCTBuilder(final ComplexTypeFieldBuilder _builder) {
        this.builder = _builder;
        this.bgmBuilder = this.builder.builder;
        this.pool = this.bgmBuilder.grammar.getPool();
    }
    
    protected static void _assert(final boolean b) {
        if (!b) {
            throw new JAXBAssertionError();
        }
    }
}
