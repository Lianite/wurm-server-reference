// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.tools.xjc.grammar.util;

import com.sun.msv.grammar.relax.NoneType;
import com.sun.msv.grammar.DataExp;
import com.sun.msv.grammar.AttributeExp;
import com.sun.msv.grammar.ElementExp;
import com.sun.msv.grammar.OtherExp;
import com.sun.msv.grammar.ExpressionVisitorExpression;
import com.sun.msv.grammar.Expression;
import com.sun.msv.grammar.ReferenceExp;
import java.util.HashSet;
import com.sun.msv.grammar.ExpressionPool;
import java.util.Set;
import com.sun.msv.grammar.ExpressionCloner;

public class NotAllowedRemover extends ExpressionCloner
{
    private final Set visitedExps;
    
    public NotAllowedRemover(final ExpressionPool pool) {
        super(pool);
        this.visitedExps = new HashSet();
    }
    
    public Expression onRef(final ReferenceExp exp) {
        if (!this.visitedExps.contains(exp)) {
            exp.exp = exp.exp.visit((ExpressionVisitorExpression)this);
            this.visitedExps.add(exp);
        }
        if (exp.exp == Expression.nullSet) {
            return Expression.nullSet;
        }
        return (Expression)exp;
    }
    
    public Expression onOther(final OtherExp exp) {
        if (!this.visitedExps.contains(exp)) {
            exp.exp = exp.exp.visit((ExpressionVisitorExpression)this);
            this.visitedExps.add(exp);
        }
        if (exp.exp == Expression.nullSet) {
            return Expression.nullSet;
        }
        return (Expression)exp;
    }
    
    public Expression onElement(final ElementExp exp) {
        if (!this.visitedExps.add(exp)) {
            return (Expression)exp;
        }
        final Expression body = exp.contentModel.visit((ExpressionVisitorExpression)this);
        if (body == Expression.nullSet) {
            return Expression.nullSet;
        }
        exp.contentModel = body;
        return (Expression)exp;
    }
    
    public Expression onAttribute(final AttributeExp exp) {
        if (!this.visitedExps.add(exp)) {
            return (Expression)exp;
        }
        final Expression body = exp.exp.visit((ExpressionVisitorExpression)this);
        if (body == Expression.nullSet) {
            return Expression.nullSet;
        }
        return this.pool.createAttribute(exp.nameClass, body);
    }
    
    public Expression onData(final DataExp exp) {
        if (exp.dt instanceof NoneType) {
            return Expression.nullSet;
        }
        return (Expression)exp;
    }
}
