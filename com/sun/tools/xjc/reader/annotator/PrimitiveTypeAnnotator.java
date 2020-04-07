// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.tools.xjc.reader.annotator;

import org.relaxng.datatype.Datatype;
import com.sun.tools.xjc.grammar.xducer.IdentityTransducer;
import com.sun.tools.xjc.grammar.xducer.BuiltinDatatypeTransducerFactory;
import com.sun.msv.datatype.xsd.XSDatatype;
import com.sun.msv.grammar.DataOrValueExp;
import com.sun.msv.grammar.DataExp;
import com.sun.msv.datatype.DatabindableDatatype;
import com.sun.tools.xjc.grammar.xducer.Transducer;
import com.sun.msv.datatype.xsd.StringType;
import com.sun.tools.xjc.grammar.xducer.EnumerationXducer;
import org.xml.sax.Locator;
import com.sun.codemodel.JClassContainer;
import com.sun.msv.grammar.ValueExp;
import com.sun.msv.grammar.ChoiceExp;
import com.sun.msv.grammar.NameClass;
import com.sun.msv.grammar.SimpleNameClass;
import com.sun.msv.grammar.AttributeExp;
import com.sun.msv.grammar.NameClassAndExpression;
import com.sun.msv.grammar.ElementExp;
import com.sun.tools.xjc.grammar.ClassCandidateItem;
import com.sun.tools.xjc.grammar.ClassItem;
import com.sun.tools.xjc.grammar.IgnoreItem;
import com.sun.tools.xjc.grammar.PrimitiveItem;
import com.sun.msv.grammar.OtherExp;
import com.sun.msv.grammar.ExpressionVisitorExpression;
import com.sun.msv.grammar.Expression;
import com.sun.msv.grammar.ReferenceExp;
import java.util.HashMap;
import java.util.HashSet;
import com.sun.codemodel.JPackage;
import java.util.Map;
import java.util.Set;
import com.sun.tools.xjc.util.CodeModelClassFactory;
import com.sun.tools.xjc.grammar.AnnotatedGrammar;
import com.sun.msv.grammar.ExpressionCloner;

class PrimitiveTypeAnnotator extends ExpressionCloner
{
    private final AnnotatedGrammar grammar;
    private final AnnotatorController controller;
    private final CodeModelClassFactory classFactory;
    private final Set visitedExps;
    private final Map primitiveItems;
    private JPackage currentPackage;
    
    PrimitiveTypeAnnotator(final AnnotatedGrammar _grammar, final AnnotatorController _controller) {
        super(_grammar.getPool());
        this.visitedExps = new HashSet();
        this.primitiveItems = new HashMap();
        this.grammar = _grammar;
        this.controller = _controller;
        this.classFactory = new CodeModelClassFactory(this.controller.getErrorReceiver());
        this.currentPackage = _grammar.codeModel._package("");
    }
    
    public Expression onRef(final ReferenceExp exp) {
        final JPackage oldPackage = this.currentPackage;
        if (this.controller.getPackageTracker().get(exp) != null) {
            this.currentPackage = this.controller.getPackageTracker().get(exp);
        }
        if (this.visitedExps.add(exp)) {
            Expression e = this.processEnumeration(exp.name, exp.exp);
            if (e == null) {
                e = exp.exp.visit((ExpressionVisitorExpression)this);
            }
            exp.exp = e;
        }
        this.currentPackage = oldPackage;
        return (Expression)exp;
    }
    
    public Expression onOther(final OtherExp exp) {
        if (exp instanceof PrimitiveItem) {
            return (Expression)exp;
        }
        if (exp instanceof IgnoreItem) {
            return (Expression)exp;
        }
        if (this.visitedExps.add(exp)) {
            String name = null;
            if (exp instanceof ClassItem) {
                name = ((ClassItem)exp).name;
            }
            if (exp instanceof ClassCandidateItem) {
                name = ((ClassCandidateItem)exp).name;
            }
            Expression e = null;
            if (name != null) {
                e = this.processEnumeration(name, exp.exp);
            }
            if (e == null) {
                e = exp.exp.visit((ExpressionVisitorExpression)this);
            }
            exp.exp = e;
        }
        return (Expression)exp;
    }
    
    public Expression onElement(final ElementExp exp) {
        if (this.visitedExps.add(exp)) {
            Expression e = this.processEnumeration((NameClassAndExpression)exp);
            if (e == null) {
                e = exp.contentModel.visit((ExpressionVisitorExpression)this);
            }
            exp.contentModel = e;
        }
        return (Expression)exp;
    }
    
    public Expression onAttribute(final AttributeExp exp) {
        if (this.visitedExps.contains(exp)) {
            return (Expression)exp;
        }
        Expression e = this.processEnumeration((NameClassAndExpression)exp);
        if (e == null) {
            e = exp.exp.visit((ExpressionVisitorExpression)this);
        }
        e = this.pool.createAttribute(exp.nameClass, e);
        this.visitedExps.add(e);
        return e;
    }
    
    public Expression processEnumeration(final NameClassAndExpression exp) {
        final NameClass nc = exp.getNameClass();
        if (!(nc instanceof SimpleNameClass)) {
            return null;
        }
        return this.processEnumeration(((SimpleNameClass)nc).localName + "Type", exp.getContentModel());
    }
    
    public Expression processEnumeration(final String className, final Expression exp) {
        if (className == null) {
            return null;
        }
        final Expression e = exp.visit((ExpressionVisitorExpression)new PrimitiveTypeAnnotator$1(this, this.pool));
        if (!(e instanceof ChoiceExp)) {
            return null;
        }
        final ChoiceExp cexp = (ChoiceExp)e;
        final Expression[] children = cexp.getChildren();
        for (int i = 0; i < children.length; ++i) {
            if (!(children[i] instanceof ValueExp)) {
                return null;
            }
        }
        int cnt = 1;
        String decoratedClassName;
        do {
            decoratedClassName = this.controller.getNameConverter().toClassName(className) + ((cnt++ == 1) ? "" : String.valueOf(cnt));
        } while (this.currentPackage._getClass(decoratedClassName) != null);
        final PrimitiveItem p = this.grammar.createPrimitiveItem((Transducer)new EnumerationXducer(this.controller.getNameConverter(), this.classFactory.createClass(this.currentPackage, decoratedClassName, null), (Expression)cexp, (Map)new HashMap(), (Locator)null), (DatabindableDatatype)StringType.theInstance, (Expression)cexp, null);
        this.primitiveItems.put(exp, p);
        return (Expression)p;
    }
    
    public Expression onData(final DataExp exp) {
        return this.onDataOrValue((DataOrValueExp)exp);
    }
    
    public Expression onValue(final ValueExp exp) {
        return this.onDataOrValue((DataOrValueExp)exp);
    }
    
    private Expression onDataOrValue(final DataOrValueExp exp) {
        if (this.primitiveItems.containsKey(exp)) {
            return this.primitiveItems.get(exp);
        }
        final Datatype dt = exp.getType();
        Transducer xducer;
        XSDatatype guard;
        if (dt instanceof XSDatatype) {
            xducer = BuiltinDatatypeTransducerFactory.get(this.grammar, (XSDatatype)dt);
            guard = (XSDatatype)dt;
        }
        else {
            xducer = (Transducer)new IdentityTransducer(this.grammar.codeModel);
            guard = (XSDatatype)StringType.theInstance;
        }
        final PrimitiveItem p = this.grammar.createPrimitiveItem(xducer, (DatabindableDatatype)guard, (Expression)exp, null);
        this.primitiveItems.put(exp, p);
        return (Expression)p;
    }
}
