// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.tools.xjc.generator;

import com.sun.msv.grammar.ElementExp;
import com.sun.msv.grammar.AttributeExp;
import com.sun.tools.xjc.grammar.ExternalItem;
import com.sun.msv.grammar.OtherExp;
import com.sun.tools.xjc.grammar.ClassItem;
import com.sun.msv.grammar.Expression;
import com.sun.msv.grammar.trex.ElementPattern;
import com.sun.msv.grammar.NameClass;
import java.util.HashMap;
import com.sun.msv.grammar.ExpressionPool;
import com.sun.msv.grammar.ExpressionVisitorExpression;
import com.sun.msv.grammar.Grammar;
import com.sun.tools.xjc.grammar.AnnotatedGrammar;
import com.sun.xml.bind.GrammarImpl;
import java.util.ArrayList;
import com.sun.msv.grammar.ReferenceExp;
import java.util.Map;
import com.sun.msv.grammar.ExpressionCloner;

public class AGMBuilder extends ExpressionCloner
{
    private final Map class2agm;
    private final Map ref2exp;
    private final Map elem2exp;
    private final ReferenceExp anyContent;
    private final ArrayList plugs;
    private final GrammarImpl grammar;
    
    public static Grammar remove(final AnnotatedGrammar src) {
        final AGMBuilder builder = new AGMBuilder(src);
        builder.grammar.setTopLevel(src.getTopLevel().visit((ExpressionVisitorExpression)builder));
        builder.grammar.setPlugs((GrammarImpl.Plug[])builder.plugs.toArray(new GrammarImpl.Plug[0]));
        return (Grammar)builder.grammar;
    }
    
    private AGMBuilder(final AnnotatedGrammar grammar) {
        super(new ExpressionPool());
        this.class2agm = new HashMap();
        this.ref2exp = new HashMap();
        this.elem2exp = new HashMap();
        this.plugs = new ArrayList();
        this.grammar = new GrammarImpl(new ExpressionPool());
        this.anyContent = new ReferenceExp("anyContent");
        this.anyContent.exp = this.pool.createZeroOrMore(this.pool.createChoice((Expression)new ElementPattern(NameClass.ALL, (Expression)this.anyContent), this.pool.createChoice(this.pool.createAttribute(NameClass.ALL), Expression.anyString)));
        final ClassItem[] ci = grammar.getClasses();
        for (int i = 0; i < ci.length; ++i) {
            if (ci[i].agm.exp == null) {
                ci[i].agm.exp = ci[i].exp;
            }
            this.class2agm.put(ci[i], new ReferenceExp((String)null, (Expression)ci[i].agm));
        }
        for (int i = 0; i < ci.length; ++i) {
            final ReferenceExp e = this.class2agm.get(ci[i]);
            e.exp = e.exp.visit((ExpressionVisitorExpression)this);
        }
    }
    
    public Expression onRef(final ReferenceExp exp) {
        Expression e = this.ref2exp.get(exp);
        if (e != null) {
            return e;
        }
        e = exp.exp.visit((ExpressionVisitorExpression)this);
        this.ref2exp.put(exp, e);
        return e;
    }
    
    public Expression onOther(final OtherExp exp) {
        if (exp instanceof ExternalItem) {
            final Expression e = ((ExternalItem)exp).createAGM(this.pool);
            if (e instanceof GrammarImpl.Plug) {
                this.plugs.add(e);
            }
            return e;
        }
        if (exp instanceof ClassItem) {
            return this.class2agm.get(exp);
        }
        return exp.exp.visit((ExpressionVisitorExpression)this);
    }
    
    public Expression onAttribute(final AttributeExp exp) {
        return this.pool.createAttribute(exp.nameClass, exp.exp.visit((ExpressionVisitorExpression)this));
    }
    
    public Expression onElement(final ElementExp exp) {
        ElementExp result = this.elem2exp.get(exp);
        if (result == null) {
            result = (ElementExp)this.grammar.createElement(exp.getNameClass(), Expression.nullSet);
            this.elem2exp.put(exp, result);
            result.contentModel = exp.getContentModel().visit((ExpressionVisitorExpression)this);
            result.ignoreUndeclaredAttributes = exp.ignoreUndeclaredAttributes;
        }
        return (Expression)result;
    }
}
