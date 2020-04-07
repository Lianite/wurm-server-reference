// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.tools.xjc.reader.annotator;

import com.sun.msv.grammar.AttributeExp;
import com.sun.msv.grammar.ElementExp;
import com.sun.tools.xjc.grammar.FieldItem;
import com.sun.tools.xjc.grammar.SuperClassItem;
import com.sun.msv.grammar.OtherExp;
import com.sun.msv.grammar.ExpressionVisitorExpression;
import com.sun.msv.grammar.Expression;
import com.sun.msv.grammar.ReferenceExp;
import java.util.HashSet;
import com.sun.msv.grammar.ExpressionPool;
import java.util.Set;
import com.sun.msv.grammar.ExpressionCloner;

public class EmptyJavaItemRemover extends ExpressionCloner
{
    private final Set visitedExps;
    
    public EmptyJavaItemRemover(final ExpressionPool pool) {
        super(pool);
        this.visitedExps = new HashSet();
    }
    
    public Expression onRef(final ReferenceExp exp) {
        if (!this.visitedExps.contains(exp)) {
            exp.exp = exp.exp.visit((ExpressionVisitorExpression)this);
            this.visitedExps.add(exp);
        }
        if (exp.exp == Expression.epsilon) {
            return Expression.epsilon;
        }
        return (Expression)exp;
    }
    
    public Expression onOther(final OtherExp exp) {
        if (!this.visitedExps.contains(exp)) {
            exp.exp = exp.exp.visit((ExpressionVisitorExpression)this);
            this.visitedExps.add(exp);
        }
        if ((exp instanceof SuperClassItem || exp instanceof FieldItem) && exp.exp == Expression.epsilon) {
            return Expression.epsilon;
        }
        return (Expression)exp;
    }
    
    public Expression onElement(final ElementExp exp) {
        if (!this.visitedExps.add(exp)) {
            return (Expression)exp;
        }
        exp.contentModel = exp.contentModel.visit((ExpressionVisitorExpression)this);
        return (Expression)exp;
    }
    
    public Expression onAttribute(final AttributeExp exp) {
        if (!this.visitedExps.add(exp)) {
            return (Expression)exp;
        }
        return this.pool.createAttribute(exp.nameClass, exp.exp.visit((ExpressionVisitorExpression)this));
    }
}
