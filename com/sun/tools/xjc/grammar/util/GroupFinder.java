// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.tools.xjc.grammar.util;

import com.sun.msv.grammar.SequenceExp;
import com.sun.msv.grammar.InterleaveExp;
import com.sun.msv.grammar.ListExp;
import com.sun.msv.grammar.ElementExp;
import com.sun.msv.grammar.AttributeExp;
import com.sun.msv.grammar.ExpressionVisitorBoolean;
import com.sun.msv.grammar.Expression;
import com.sun.msv.grammar.util.ExpressionFinder;

public final class GroupFinder extends ExpressionFinder
{
    private static final ExpressionFinder theInstance;
    
    public static boolean find(final Expression e) {
        return e.visit((ExpressionVisitorBoolean)GroupFinder.theInstance);
    }
    
    public boolean onAttribute(final AttributeExp exp) {
        return false;
    }
    
    public boolean onElement(final ElementExp exp) {
        return false;
    }
    
    public boolean onList(final ListExp exp) {
        return false;
    }
    
    public boolean onInterleave(final InterleaveExp exp) {
        return true;
    }
    
    public boolean onSequence(final SequenceExp exp) {
        return true;
    }
    
    static {
        theInstance = new GroupFinder();
    }
}
