// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.tools.xjc.grammar;

import com.sun.msv.grammar.ExpressionVisitorVoid;
import com.sun.msv.grammar.OtherExp;
import com.sun.msv.grammar.util.ExpressionWalker;

public abstract class BGMWalker extends ExpressionWalker implements JavaItemVisitor
{
    public void onOther(final OtherExp exp) {
        if (exp instanceof JavaItem) {
            ((JavaItem)exp).visitJI((JavaItemVisitor)this);
        }
        else {
            exp.exp.visit((ExpressionVisitorVoid)this);
        }
    }
    
    public Object onClass(final ClassItem item) {
        item.exp.visit((ExpressionVisitorVoid)this);
        return null;
    }
    
    public Object onField(final FieldItem item) {
        item.exp.visit((ExpressionVisitorVoid)this);
        return null;
    }
    
    public Object onIgnore(final IgnoreItem item) {
        item.exp.visit((ExpressionVisitorVoid)this);
        return null;
    }
    
    public Object onInterface(final InterfaceItem item) {
        item.exp.visit((ExpressionVisitorVoid)this);
        return null;
    }
    
    public Object onPrimitive(final PrimitiveItem item) {
        item.exp.visit((ExpressionVisitorVoid)this);
        return null;
    }
    
    public Object onSuper(final SuperClassItem item) {
        item.exp.visit((ExpressionVisitorVoid)this);
        return null;
    }
    
    public Object onExternal(final ExternalItem item) {
        item.exp.visit((ExpressionVisitorVoid)this);
        return null;
    }
}
