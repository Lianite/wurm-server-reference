// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.tools.xjc.generator.marshaller;

import com.sun.xml.bind.JAXBAssertionError;
import com.sun.codemodel.JPrimitiveType;
import com.sun.codemodel.JExpr;
import com.sun.codemodel.JType;
import com.sun.codemodel.JExpression;
import com.sun.tools.xjc.generator.util.BlockReference;
import com.sun.codemodel.JBlock;

abstract class AbstractSideImpl implements Side
{
    protected final Context context;
    
    protected AbstractSideImpl(final Context _context) {
        this.context = _context;
    }
    
    protected final JBlock getBlock(final boolean create) {
        return this.context.getCurrentBlock().get(create);
    }
    
    protected final BlockReference createWhileBlock(final BlockReference parent, final JExpression expr) {
        return (BlockReference)new AbstractSideImpl$1(this, parent, expr);
    }
    
    protected final JExpression instanceOf(final JExpression obj, JType type) {
        if (this.context.codeModel.NULL == type) {
            return obj.eq(JExpr._null());
        }
        if (type instanceof JPrimitiveType) {
            type = ((JPrimitiveType)type).getWrapperClass();
        }
        return obj._instanceof(type);
    }
    
    protected static Object _assert(final boolean b) {
        if (!b) {
            throw new JAXBAssertionError();
        }
        return null;
    }
}
