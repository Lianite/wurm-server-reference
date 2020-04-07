// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.tools.xjc.grammar.util;

import com.sun.msv.grammar.ElementExp;
import com.sun.msv.grammar.AttributeExp;
import com.sun.tools.xjc.grammar.ExternalItem;
import com.sun.msv.grammar.OtherExp;
import com.sun.msv.grammar.trex.ElementPattern;
import com.sun.msv.grammar.NameClass;
import java.util.HashMap;
import com.sun.msv.grammar.Expression;
import com.sun.msv.grammar.trex.TREXGrammar;
import com.sun.msv.grammar.ExpressionVisitorExpression;
import com.sun.msv.grammar.ExpressionPool;
import com.sun.msv.grammar.Grammar;
import com.sun.msv.grammar.ReferenceExp;
import java.util.Map;
import com.sun.msv.grammar.ExpressionCloner;

public class AnnotationRemover extends ExpressionCloner
{
    private final Map bookmarks;
    private final Map elements;
    private final ReferenceExp anyContent;
    
    public static Grammar remove(final Grammar src) {
        final ExpressionPool newPool = new ExpressionPool();
        final Expression newTop = src.getTopLevel().visit((ExpressionVisitorExpression)new AnnotationRemover(newPool));
        final TREXGrammar grammar = new TREXGrammar(newPool);
        grammar.exp = newTop;
        return (Grammar)grammar;
    }
    
    public static Expression remove(final Expression exp, final ExpressionPool pool) {
        return exp.visit((ExpressionVisitorExpression)new AnnotationRemover(pool));
    }
    
    private AnnotationRemover(final ExpressionPool pool) {
        super(pool);
        this.bookmarks = new HashMap();
        this.elements = new HashMap();
        this.anyContent = new ReferenceExp("anyContent");
        this.anyContent.exp = pool.createZeroOrMore(pool.createChoice((Expression)new ElementPattern(NameClass.ALL, (Expression)this.anyContent), pool.createChoice(pool.createAttribute(NameClass.ALL), Expression.anyString)));
    }
    
    public Expression onRef(final ReferenceExp exp) {
        if (!this.bookmarks.containsKey(exp)) {
            return exp.exp.visit((ExpressionVisitorExpression)this);
        }
        ReferenceExp target = this.bookmarks.get(exp);
        if (target == null) {
            target = new ReferenceExp(exp.name);
            target.exp = exp.exp.visit((ExpressionVisitorExpression)this);
            this.bookmarks.put(exp, target);
        }
        return (Expression)target;
    }
    
    public Expression onOther(final OtherExp exp) {
        if (exp instanceof ExternalItem) {
            return ((ExternalItem)exp).createAGM(this.pool);
        }
        return exp.exp.visit((ExpressionVisitorExpression)this);
    }
    
    public Expression onAttribute(final AttributeExp exp) {
        return this.pool.createAttribute(exp.nameClass, exp.exp.visit((ExpressionVisitorExpression)this));
    }
    
    public Expression onElement(final ElementExp exp) {
        ElementExp result = this.elements.get(exp);
        if (result != null) {
            return (Expression)result;
        }
        result = (ElementExp)new ElementPattern(exp.getNameClass(), Expression.nullSet);
        this.elements.put(exp, result);
        result.contentModel = exp.contentModel.visit((ExpressionVisitorExpression)this);
        result.ignoreUndeclaredAttributes = exp.ignoreUndeclaredAttributes;
        return (Expression)result;
    }
}
