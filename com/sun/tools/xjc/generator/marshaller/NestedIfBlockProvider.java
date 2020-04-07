// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.tools.xjc.generator.marshaller;

import com.sun.codemodel.JExpression;

class NestedIfBlockProvider
{
    private final Context context;
    private int nestLevel;
    private IfThenElseBlockReference previous;
    
    NestedIfBlockProvider(final Context _context) {
        this.nestLevel = 0;
        this.previous = null;
        this.context = _context;
    }
    
    public void startBlock(final JExpression testExp) {
        this.startElse();
        ++this.nestLevel;
        this.previous = new IfThenElseBlockReference(this.context, testExp);
        this.context.pushNewBlock(this.previous.createThenProvider());
    }
    
    public void startElse() {
        if (this.previous != null) {
            this.context.popBlock();
            this.context.pushNewBlock(this.previous.createElseProvider());
        }
    }
    
    public void end() {
        while (this.nestLevel-- > 0) {
            this.context.popBlock();
        }
    }
}
