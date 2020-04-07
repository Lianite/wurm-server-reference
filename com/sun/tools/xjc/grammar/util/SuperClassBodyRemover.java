// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.tools.xjc.grammar.util;

import java.util.HashSet;
import com.sun.msv.grammar.ExpressionPool;
import com.sun.tools.xjc.grammar.SuperClassItem;
import com.sun.msv.grammar.OtherExp;
import com.sun.msv.grammar.ReferenceExp;
import com.sun.msv.grammar.ElementExp;
import com.sun.msv.grammar.Expression;
import com.sun.msv.grammar.AttributeExp;
import com.sun.tools.xjc.grammar.ClassItem;
import com.sun.msv.grammar.ExpressionVisitorExpression;
import com.sun.tools.xjc.grammar.AnnotatedGrammar;
import java.util.Set;
import com.sun.msv.grammar.ExpressionCloner;

public class SuperClassBodyRemover extends ExpressionCloner
{
    private final Set visitedRefs;
    private ExpressionCloner remover;
    
    public static void remove(final AnnotatedGrammar g) {
        final SuperClassBodyRemover su = new SuperClassBodyRemover(g.getPool());
        final ClassItem[] cls = g.getClasses();
        for (int i = 0; i < cls.length; ++i) {
            cls[i].exp = cls[i].exp.visit((ExpressionVisitorExpression)su);
        }
    }
    
    public Expression onAttribute(final AttributeExp exp) {
        return this.pool.createAttribute(exp.nameClass, exp.exp.visit((ExpressionVisitorExpression)this));
    }
    
    public Expression onElement(final ElementExp exp) {
        if (this.visitedRefs.add(exp)) {
            exp.contentModel = exp.contentModel;
        }
        return (Expression)exp;
    }
    
    public Expression onRef(final ReferenceExp exp) {
        if (this.visitedRefs.add(exp)) {
            exp.exp = exp.exp.visit((ExpressionVisitorExpression)this);
        }
        return (Expression)exp;
    }
    
    public Expression onOther(final OtherExp exp) {
        if (exp instanceof SuperClassItem) {
            return exp.exp.visit((ExpressionVisitorExpression)this.remover);
        }
        if (this.visitedRefs.add(exp)) {
            exp.exp = exp.exp.visit((ExpressionVisitorExpression)this);
        }
        return (Expression)exp;
    }
    
    private SuperClassBodyRemover(final ExpressionPool pool) {
        super(pool);
        this.visitedRefs = new HashSet();
        this.remover = (ExpressionCloner)new SuperClassBodyRemover$1(this, pool);
    }
}
