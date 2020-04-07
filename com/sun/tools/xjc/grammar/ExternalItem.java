// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.tools.xjc.grammar;

import com.sun.codemodel.JVar;
import com.sun.codemodel.JExpression;
import com.sun.tools.xjc.generator.marshaller.FieldMarshallerGenerator;
import com.sun.codemodel.JBlock;
import com.sun.tools.xjc.generator.GeneratorContext;
import com.sun.msv.grammar.ExpressionPool;
import com.sun.msv.grammar.trex.ElementPattern;
import com.sun.msv.grammar.Expression;
import org.xml.sax.Locator;
import com.sun.msv.grammar.NameClass;

public abstract class ExternalItem extends TypeItem
{
    public final NameClass elementName;
    
    public ExternalItem(final String displayName, final NameClass _elementName, final Locator loc) {
        super(displayName, loc);
        this.elementName = _elementName;
        this.exp = (Expression)new ElementPattern(NameClass.ALL, Expression.epsilon);
    }
    
    public abstract Expression createAGM(final ExpressionPool p0);
    
    public abstract Expression createValidationFragment();
    
    public abstract void generateMarshaller(final GeneratorContext p0, final JBlock p1, final FieldMarshallerGenerator p2, final JExpression p3);
    
    public abstract JExpression generateUnmarshaller(final GeneratorContext p0, final JExpression p1, final JBlock p2, final JExpression p3, final JVar p4, final JVar p5, final JVar p6, final JVar p7);
    
    public final Object visitJI(final JavaItemVisitor visitor) {
        return visitor.onExternal(this);
    }
}
