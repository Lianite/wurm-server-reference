// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.tools.xjc.reader.annotator;

import com.sun.msv.grammar.OtherExp;
import com.sun.msv.grammar.ReferenceExp;
import com.sun.msv.grammar.ElementExp;
import com.sun.msv.grammar.ExpressionVisitorExpression;
import com.sun.msv.grammar.AttributeExp;
import com.sun.msv.grammar.ConcurExp;
import com.sun.msv.grammar.Expression;
import java.util.HashSet;
import com.sun.msv.grammar.ExpressionPool;
import java.util.Set;
import java.io.PrintStream;
import com.sun.msv.grammar.ExpressionCloner;

class JavaItemRemover extends ExpressionCloner
{
    private static PrintStream debug;
    private final Set targets;
    private final Set visitedExps;
    
    JavaItemRemover(final ExpressionPool pool, final Set targets) {
        super(pool);
        this.visitedExps = new HashSet();
        this.targets = targets;
    }
    
    public Expression onNullSet() {
        throw new Error();
    }
    
    public Expression onConcur(final ConcurExp exp) {
        throw new Error();
    }
    
    public Expression onAttribute(final AttributeExp exp) {
        final Expression body = exp.exp.visit((ExpressionVisitorExpression)this);
        if (body == exp.exp) {
            return (Expression)exp;
        }
        return this.pool.createAttribute(exp.nameClass, body);
    }
    
    public Expression onElement(final ElementExp exp) {
        if (!this.visitedExps.add(exp)) {
            return (Expression)exp;
        }
        exp.contentModel = exp.contentModel.visit((ExpressionVisitorExpression)this);
        return (Expression)exp;
    }
    
    public Expression onRef(final ReferenceExp exp) {
        if (!this.visitedExps.add(exp)) {
            return (Expression)exp;
        }
        exp.exp = exp.exp.visit((ExpressionVisitorExpression)this);
        return (Expression)exp;
    }
    
    public Expression onOther(final OtherExp exp) {
        if (this.targets.contains(exp)) {
            if (JavaItemRemover.debug != null) {
                JavaItemRemover.debug.println(" " + exp + ": found and removed");
            }
            return exp.exp.visit((ExpressionVisitorExpression)this);
        }
        exp.exp = exp.exp.visit((ExpressionVisitorExpression)this);
        return (Expression)exp;
    }
    
    static {
        JavaItemRemover.debug = null;
    }
}
