// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.tools.xjc.reader.annotator;

import org.xml.sax.Locator;
import com.sun.msv.datatype.DatabindableDatatype;
import com.sun.tools.xjc.grammar.xducer.Transducer;
import com.sun.msv.datatype.xsd.XSDatatype;
import com.sun.msv.datatype.xsd.StringType;
import com.sun.tools.xjc.grammar.xducer.IdentityTransducer;
import com.sun.msv.grammar.MixedExp;
import com.sun.msv.grammar.AttributeExp;
import com.sun.msv.grammar.ElementExp;
import com.sun.msv.grammar.OtherExp;
import com.sun.msv.grammar.ExpressionVisitorExpression;
import com.sun.msv.grammar.Expression;
import com.sun.msv.grammar.ReferenceExp;
import java.util.HashSet;
import com.sun.tools.xjc.grammar.AnnotatedGrammar;
import java.util.Set;
import com.sun.msv.grammar.ExpressionCloner;

public class MixedRemover extends ExpressionCloner
{
    private final Set visitedExps;
    private final AnnotatedGrammar grammar;
    
    public MixedRemover(final AnnotatedGrammar g) {
        super(g.getPool());
        this.visitedExps = new HashSet();
        this.grammar = g;
    }
    
    public Expression onRef(final ReferenceExp exp) {
        if (this.visitedExps.add(exp)) {
            exp.exp = exp.exp.visit((ExpressionVisitorExpression)this);
        }
        return (Expression)exp;
    }
    
    public Expression onOther(final OtherExp exp) {
        if (this.visitedExps.add(exp)) {
            exp.exp = exp.exp.visit((ExpressionVisitorExpression)this);
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
    
    public Expression onMixed(final MixedExp exp) {
        return this.pool.createInterleave(this.pool.createZeroOrMore((Expression)this.grammar.createPrimitiveItem((Transducer)new IdentityTransducer(this.grammar.codeModel), (DatabindableDatatype)StringType.theInstance, this.pool.createData((XSDatatype)StringType.theInstance), null)), exp.exp.visit((ExpressionVisitorExpression)this));
    }
}
