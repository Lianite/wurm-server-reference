// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.tools.xjc.grammar.util;

import com.sun.xml.bind.JAXBAssertionError;
import com.sun.msv.grammar.ExpressionVisitorExpression;
import com.sun.msv.grammar.AttributeExp;
import com.sun.msv.grammar.OtherExp;
import com.sun.msv.grammar.ReferenceExp;
import com.sun.msv.grammar.Expression;
import com.sun.msv.grammar.ElementExp;
import java.util.HashSet;
import com.sun.msv.grammar.ExpressionPool;
import java.util.Stack;
import java.util.Set;
import com.sun.msv.grammar.ExpressionCloner;

public abstract class BreadthFirstExpressionCloner extends ExpressionCloner
{
    private final Set visitedExps;
    private final Stack queue;
    private boolean inLoop;
    
    protected BreadthFirstExpressionCloner(final ExpressionPool pool) {
        super(pool);
        this.visitedExps = new HashSet();
        this.queue = new Stack();
        this.inLoop = false;
    }
    
    public final Expression onElement(final ElementExp exp) {
        if (this.visitedExps.add(exp)) {
            this.queue.push(exp);
            this.processQueue();
        }
        return (Expression)exp;
    }
    
    public final Expression onRef(final ReferenceExp exp) {
        if (this.visitedExps.add(exp)) {
            this.queue.push(exp);
            this.processQueue();
        }
        return (Expression)exp;
    }
    
    public final Expression onOther(final OtherExp exp) {
        if (this.visitedExps.add(exp)) {
            this.queue.push(exp);
            this.processQueue();
        }
        return (Expression)exp;
    }
    
    public final Expression onAttribute(final AttributeExp exp) {
        if (this.visitedExps.contains(exp)) {
            return (Expression)exp;
        }
        final Expression e = this.pool.createAttribute(exp.nameClass, exp.exp.visit((ExpressionVisitorExpression)this));
        this.visitedExps.add(e);
        return e;
    }
    
    private void processQueue() {
        if (this.inLoop) {
            return;
        }
        this.inLoop = true;
        while (!this.queue.isEmpty()) {
            final Expression e = this.queue.pop();
            if (e instanceof ElementExp) {
                final ElementExp ee = (ElementExp)e;
                ee.contentModel = ee.contentModel.visit((ExpressionVisitorExpression)this);
            }
            else if (e instanceof ReferenceExp) {
                final ReferenceExp re = (ReferenceExp)e;
                re.exp = re.exp.visit((ExpressionVisitorExpression)this);
            }
            else {
                if (!(e instanceof OtherExp)) {
                    throw new JAXBAssertionError();
                }
                final OtherExp oe = (OtherExp)e;
                oe.exp = oe.exp.visit((ExpressionVisitorExpression)this);
            }
        }
        this.inLoop = false;
    }
}
