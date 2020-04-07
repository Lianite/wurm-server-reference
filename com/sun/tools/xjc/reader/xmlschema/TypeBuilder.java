// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.tools.xjc.reader.xmlschema;

import org.xml.sax.Locator;
import com.sun.msv.datatype.DatabindableDatatype;
import com.sun.tools.xjc.grammar.xducer.Transducer;
import com.sun.msv.datatype.xsd.XSDatatype;
import com.sun.msv.datatype.xsd.BooleanType;
import com.sun.msv.datatype.xsd.StringType;
import com.sun.tools.xjc.grammar.xducer.NilTransducer;
import com.sun.msv.grammar.trex.ElementPattern;
import com.sun.tools.xjc.grammar.ClassItem;
import com.sun.codemodel.JDefinedClass;
import com.sun.tools.xjc.grammar.TypeItem;
import java.util.Iterator;
import com.sun.xml.xsom.XSElementDecl;
import com.sun.xml.xsom.XSModelGroup;
import com.sun.xml.xsom.XSModelGroupDecl;
import com.sun.tools.xjc.grammar.ext.WildcardItem;
import com.sun.xml.xsom.XSWildcard;
import com.sun.xml.xsom.XSContentType;
import com.sun.xml.xsom.XSParticle;
import com.sun.xml.xsom.XSSimpleType;
import com.sun.xml.xsom.XSComplexType;
import com.sun.xml.xsom.XSAttributeUse;
import com.sun.msv.grammar.NameClass;
import com.sun.msv.grammar.SimpleNameClass;
import com.sun.msv.grammar.AttributeExp;
import com.sun.xml.xsom.XSAttributeDecl;
import com.sun.xml.xsom.XSAttGroupDecl;
import com.sun.xml.xsom.visitor.XSFunction;
import com.sun.msv.grammar.Expression;
import com.sun.xml.xsom.XSComponent;
import com.sun.msv.grammar.ExpressionPool;

public class TypeBuilder extends AbstractXSFunctionImpl implements BGMBuilder.ParticleHandler
{
    private final BGMBuilder builder;
    private final ExpressionPool pool;
    
    TypeBuilder(final BGMBuilder _builder) {
        this.builder = _builder;
        this.pool = this.builder.pool;
    }
    
    public final Expression build(final XSComponent sc) {
        return sc.apply((XSFunction<Expression>)this);
    }
    
    public Object attGroupDecl(final XSAttGroupDecl agd) {
        final Expression exp = this.builder.selector.bindToType((XSComponent)agd);
        if (exp != null) {
            return exp;
        }
        _assert(false);
        return null;
    }
    
    public Object attributeDecl(final XSAttributeDecl decl) {
        return this._attributeDecl(decl);
    }
    
    public AttributeExp _attributeDecl(final XSAttributeDecl decl) {
        this.builder.simpleTypeBuilder.refererStack.push(decl);
        final AttributeExp exp = (AttributeExp)this.pool.createAttribute((NameClass)new SimpleNameClass(decl.getTargetNamespace(), decl.getName()), this.builder.simpleTypeBuilder.build(decl.getType()));
        this.builder.simpleTypeBuilder.refererStack.pop();
        return exp;
    }
    
    public Object attributeUse(final XSAttributeUse use) {
        final Expression exp = this.builder.selector.bindToType((XSComponent)use);
        if (exp != null) {
            return exp;
        }
        _assert(false);
        return null;
    }
    
    public Object complexType(final XSComplexType type) {
        return this.builder.selector.bindToType(type);
    }
    
    public Object simpleType(final XSSimpleType type) {
        final Expression exp = this.builder.selector.bindToType((XSComponent)type);
        if (exp != null) {
            return exp;
        }
        return this.builder.simpleTypeBuilder.build(type);
    }
    
    public Object particle(final XSParticle p) {
        final Expression exp = this.builder.selector.bindToType((XSComponent)p);
        if (exp != null) {
            return exp;
        }
        return this.builder.processMinMax(this.build(p.getTerm()), p);
    }
    
    public Object empty(final XSContentType empty) {
        final Expression exp = this.builder.selector.bindToType((XSComponent)empty);
        if (exp != null) {
            return exp;
        }
        return Expression.epsilon;
    }
    
    public Object wildcard(final XSWildcard wc) {
        final Expression exp = this.builder.selector.bindToType((XSComponent)wc);
        if (exp != null) {
            return exp;
        }
        return new WildcardItem(this.builder.grammar.codeModel, wc);
    }
    
    public Object modelGroupDecl(final XSModelGroupDecl decl) {
        Expression exp = this.builder.selector.bindToType((XSComponent)decl);
        if (exp != null) {
            return exp;
        }
        this.builder.selector.pushClassFactory((JClassFactory)new PrefixedJClassFactoryImpl(this.builder, decl));
        exp = this.build(decl.getModelGroup());
        this.builder.selector.popClassFactory();
        return exp;
    }
    
    public Object modelGroup(final XSModelGroup mg) {
        final Expression exp = this.builder.selector.bindToType((XSComponent)mg);
        if (exp != null) {
            return exp;
        }
        return this.builder.applyRecursively(mg, (BGMBuilder.ParticleHandler)this);
    }
    
    public Object elementDecl(final XSElementDecl decl) {
        Expression exp = Expression.nullSet;
        for (final XSElementDecl e : decl.getSubstitutables()) {
            if (e.isAbstract()) {
                continue;
            }
            exp = this.pool.createChoice(exp, (Expression)this.elementDeclWithoutSubstGroup(e));
        }
        return exp;
    }
    
    private TypeItem elementDeclWithoutSubstGroup(final XSElementDecl decl) {
        final TypeItem ti = this.builder.selector.bindToType(decl);
        if (ti != null) {
            return ti;
        }
        final JDefinedClass cls = this.builder.selector.getClassFactory().create(this.builder.getNameConverter().toClassName(decl.getName()), decl.getLocator());
        cls._implements((TypeBuilder.class$javax$xml$bind$Element == null) ? (TypeBuilder.class$javax$xml$bind$Element = class$("javax.xml.bind.Element")) : TypeBuilder.class$javax$xml$bind$Element);
        final ClassItem ci = this.builder.grammar.createClassItem(cls, Expression.epsilon, decl.getLocator());
        this.builder.selector.queueBuild((XSComponent)decl, ci);
        return ci;
    }
    
    protected ElementPattern elementDeclFlat(final XSElementDecl decl) {
        this.builder.selector.bindToType(decl);
        final Expression type = this.builder.selector.bindToType((XSComponent)decl.getType());
        Expression body;
        if (type != null) {
            body = type;
        }
        else {
            this.builder.simpleTypeBuilder.refererStack.push(decl);
            body = this.builder.typeBuilder.build((XSComponent)decl.getType());
            this.builder.simpleTypeBuilder.refererStack.pop();
        }
        if (decl.isNillable()) {
            body = this.pool.createChoice(this.buildXsiNilExpForProperty(), body);
        }
        if (decl.getType().isComplexType() && this.builder.getGlobalBinding().isTypeSubstitutionSupportEnabled()) {
            body = this.pool.createChoice(body, this.builder.getTypeSubstitutionList(decl.getType().asComplexType(), false));
        }
        else {
            body = this.pool.createSequence(body, this.builder.createXsiTypeExp(decl));
        }
        final SimpleNameClass name = new SimpleNameClass(decl.getTargetNamespace(), decl.getName());
        return new ElementPattern((NameClass)name, body);
    }
    
    private Expression buildXsiNilExpForProperty() {
        return (Expression)new AttributeExp((NameClass)new SimpleNameClass("http://www.w3.org/2001/XMLSchema-instance", "nil"), (Expression)this.builder.grammar.createPrimitiveItem(new NilTransducer(this.builder.grammar.codeModel), (DatabindableDatatype)StringType.theInstance, this.pool.createValue((XSDatatype)BooleanType.theInstance, (Object)Boolean.TRUE), null));
    }
}
