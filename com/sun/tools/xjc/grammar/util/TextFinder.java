// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.tools.xjc.grammar.util;

import com.sun.msv.grammar.ValueExp;
import com.sun.msv.grammar.MixedExp;
import com.sun.msv.grammar.ListExp;
import com.sun.msv.grammar.DataExp;
import com.sun.msv.grammar.ElementExp;
import com.sun.msv.grammar.AttributeExp;
import com.sun.msv.grammar.ExpressionVisitorBoolean;
import com.sun.msv.grammar.Expression;
import com.sun.msv.grammar.util.ExpressionFinder;

public final class TextFinder extends ExpressionFinder
{
    private static final ExpressionFinder theInstance;
    
    public static boolean find(final Expression e) {
        return e.visit((ExpressionVisitorBoolean)TextFinder.theInstance);
    }
    
    public boolean onAttribute(final AttributeExp exp) {
        return false;
    }
    
    public boolean onElement(final ElementExp exp) {
        return false;
    }
    
    public boolean onAnyString() {
        return true;
    }
    
    public boolean onData(final DataExp exp) {
        return true;
    }
    
    public boolean onList(final ListExp exp) {
        return true;
    }
    
    public boolean onMixed(final MixedExp exp) {
        return true;
    }
    
    public boolean onValue(final ValueExp exp) {
        return true;
    }
    
    static {
        TextFinder.theInstance = (ExpressionFinder)new TextFinder();
    }
}
