// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.tools.xjc.grammar.ext;

import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JClassAlreadyExistsException;
import com.sun.codemodel.JType;
import com.sun.msv.grammar.ExpressionPool;
import com.sun.msv.grammar.trex.ElementPattern;
import com.sun.msv.grammar.ReferenceExp;
import org.xml.sax.Locator;
import com.sun.tools.xjc.grammar.AnnotatedGrammar;
import com.sun.msv.grammar.NameClass;
import com.sun.codemodel.JCodeModel;
import com.sun.msv.grammar.Expression;
import com.sun.tools.xjc.grammar.ExternalItem;

abstract class AbstractDOMItem extends ExternalItem
{
    private final Expression agm;
    protected final JCodeModel codeModel;
    
    public AbstractDOMItem(final NameClass _elementName, final AnnotatedGrammar grammar, final Locator loc) {
        super("dom", _elementName, loc);
        final ExpressionPool pool = grammar.getPool();
        this.codeModel = grammar.codeModel;
        final ReferenceExp any = new ReferenceExp((String)null);
        any.exp = pool.createMixed(pool.createZeroOrMore(pool.createChoice(pool.createAttribute(NameClass.ALL), (Expression)new ElementPattern(NameClass.ALL, (Expression)any))));
        this.exp = (Expression)new ElementPattern(_elementName, (Expression)any);
        this.agm = this.exp;
    }
    
    protected final JType createPhantomType(final String name) {
        try {
            final JDefinedClass def = this.codeModel._class(name);
            def.hide();
            return def;
        }
        catch (JClassAlreadyExistsException e) {
            return e.getExistingClass();
        }
    }
    
    public Expression createAGM(final ExpressionPool pool) {
        return this.agm;
    }
    
    public Expression createValidationFragment() {
        return this.agm;
    }
}
