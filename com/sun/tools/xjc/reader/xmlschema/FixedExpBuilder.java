// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.tools.xjc.reader.xmlschema;

import com.sun.msv.grammar.MixedExp;
import com.sun.msv.grammar.InterleaveExp;
import com.sun.msv.grammar.ConcurExp;
import com.sun.msv.grammar.ElementExp;
import com.sun.msv.grammar.AttributeExp;
import com.sun.msv.grammar.ValueExp;
import com.sun.msv.grammar.DataExp;
import com.sun.msv.grammar.SequenceExp;
import com.sun.msv.grammar.OneOrMoreExp;
import com.sun.msv.grammar.ChoiceExp;
import com.sun.msv.datatype.xsd.XSDatatype;
import com.sun.msv.datatype.xsd.StringType;
import com.sun.xml.bind.JAXBAssertionError;
import com.sun.msv.grammar.ReferenceExp;
import java.util.StringTokenizer;
import com.sun.msv.grammar.ListExp;
import com.sun.tools.xjc.grammar.PrimitiveItem;
import com.sun.msv.grammar.OtherExp;
import com.sun.msv.grammar.Expression;
import org.relaxng.datatype.ValidationContext;
import com.sun.msv.grammar.ExpressionPool;
import com.sun.tools.xjc.grammar.AnnotatedGrammar;
import com.sun.msv.grammar.ExpressionVisitorExpression;

class FixedExpBuilder implements ExpressionVisitorExpression
{
    private final AnnotatedGrammar grammar;
    private final ExpressionPool pool;
    private String token;
    private final ValidationContext context;
    
    public static Expression build(final Expression exp, final String token, final AnnotatedGrammar grammar, final ValidationContext context) {
        return exp.visit((ExpressionVisitorExpression)new FixedExpBuilder(grammar, token, context));
    }
    
    private FixedExpBuilder(final AnnotatedGrammar _grammar, final String _token, final ValidationContext _context) {
        this.grammar = _grammar;
        this.pool = this.grammar.getPool();
        this.token = _token;
        this.context = _context;
    }
    
    public Expression onOther(final OtherExp exp) {
        if (!(exp instanceof PrimitiveItem)) {
            return exp.exp.visit((ExpressionVisitorExpression)this);
        }
        final PrimitiveItem pi = (PrimitiveItem)exp;
        final Expression body = exp.exp.visit((ExpressionVisitorExpression)this);
        if (body == Expression.nullSet) {
            return body;
        }
        return (Expression)this.grammar.createPrimitiveItem(pi.xducer, pi.guard, body, pi.locator);
    }
    
    public Expression onList(final ListExp exp) {
        final String oldToken = this.token;
        final Expression residual = exp.exp;
        Expression result = Expression.epsilon;
        final StringTokenizer tokens = new StringTokenizer(this.token);
        while (tokens.hasMoreTokens()) {
            this.token = tokens.nextToken();
            result = this.pool.createSequence(result, residual.visit((ExpressionVisitorExpression)this));
        }
        result = this.pool.createList(result);
        this.token = oldToken;
        return result;
    }
    
    public Expression onRef(final ReferenceExp exp) {
        return exp.exp.visit((ExpressionVisitorExpression)this);
    }
    
    private static void _assert(final boolean b) {
        if (!b) {
            throw new JAXBAssertionError();
        }
    }
    
    public Expression onAnyString() {
        return this.pool.createValue((XSDatatype)StringType.theInstance, (Object)this.token);
    }
    
    public Expression onChoice(final ChoiceExp exp) {
        final Expression r = exp.exp1.visit((ExpressionVisitorExpression)this);
        if (r != Expression.nullSet) {
            return r;
        }
        return exp.exp2.visit((ExpressionVisitorExpression)this);
    }
    
    public Expression onEpsilon() {
        return Expression.nullSet;
    }
    
    public Expression onNullSet() {
        return Expression.nullSet;
    }
    
    public Expression onOneOrMore(final OneOrMoreExp exp) {
        return exp.exp.visit((ExpressionVisitorExpression)this);
    }
    
    public Expression onSequence(final SequenceExp exp) {
        Expression r = exp.exp1.visit((ExpressionVisitorExpression)this);
        if (r == Expression.nullSet && exp.exp1.isEpsilonReducible()) {
            r = exp.exp2.visit((ExpressionVisitorExpression)this);
        }
        return r;
    }
    
    public Expression onData(final DataExp exp) {
        if (exp.dt.isValid(this.token, this.context)) {
            return this.pool.createValue(exp.dt, exp.name, exp.dt.createValue(this.token, this.context));
        }
        return Expression.nullSet;
    }
    
    public Expression onValue(final ValueExp exp) {
        if (exp.dt.sameValue(exp.value, exp.dt.createValue(this.token, this.context))) {
            return (Expression)exp;
        }
        return Expression.nullSet;
    }
    
    public Expression onAttribute(final AttributeExp exp) {
        _assert(false);
        return null;
    }
    
    public Expression onElement(final ElementExp exp) {
        _assert(false);
        return null;
    }
    
    public Expression onConcur(final ConcurExp p) {
        _assert(false);
        return null;
    }
    
    public Expression onInterleave(final InterleaveExp p) {
        _assert(false);
        return null;
    }
    
    public Expression onMixed(final MixedExp exp) {
        _assert(false);
        return null;
    }
}
