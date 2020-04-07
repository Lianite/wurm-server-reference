// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.tools.xjc.generator.marshaller;

import com.sun.codemodel.JConditional;
import com.sun.tools.xjc.generator.util.BlockReference;
import com.sun.codemodel.JExpression;

class IfThenElseBlockReference
{
    private final JExpression testExp;
    private final BlockReference parent;
    private JConditional cond;
    private boolean swapped;
    
    IfThenElseBlockReference(final Context _context, final JExpression exp) {
        this.swapped = false;
        this.testExp = exp;
        this.parent = _context.getCurrentBlock();
    }
    
    public BlockReference createThenProvider() {
        return (BlockReference)new IfThenElseBlockReference$1(this);
    }
    
    public BlockReference createElseProvider() {
        return (BlockReference)new IfThenElseBlockReference$2(this);
    }
}
