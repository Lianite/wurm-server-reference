// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.tools.xjc.reader.xmlschema.cs;

import com.sun.msv.grammar.ExpressionVisitorVoid;
import com.sun.tools.xjc.grammar.ext.WildcardItem;
import com.sun.xml.xsom.XSWildcard;
import com.sun.xml.xsom.XSModelGroupDecl;
import com.sun.xml.xsom.XSModelGroup;
import com.sun.msv.datatype.xsd.XSDatatype;
import com.sun.msv.datatype.xsd.BooleanType;
import com.sun.xml.xsom.XSSimpleType;
import com.sun.msv.grammar.trex.ElementPattern;
import com.sun.xml.xsom.XSElementDecl;
import com.sun.xml.xsom.XSTerm;
import com.sun.xml.xsom.XSParticle;
import com.sun.xml.xsom.XSContentType;
import com.sun.xml.xsom.XSComplexType;
import org.relaxng.datatype.Datatype;
import com.sun.msv.grammar.NameClass;
import com.sun.msv.util.StringPair;
import com.sun.msv.grammar.SimpleNameClass;
import org.relaxng.datatype.ValidationContext;
import com.sun.xml.xsom.XSAttributeDecl;
import java.util.Iterator;
import com.sun.xml.xsom.XSAttributeUse;
import com.sun.xml.xsom.XSAttContainer;
import com.sun.xml.xsom.XSAttGroupDecl;
import com.sun.xml.xsom.visitor.XSFunction;
import com.sun.msv.grammar.Expression;
import com.sun.tools.xjc.grammar.ClassItem;
import com.sun.xml.xsom.XSComponent;
import com.sun.msv.grammar.ExpressionPool;
import com.sun.tools.xjc.reader.xmlschema.BGMBuilder;
import com.sun.tools.xjc.reader.xmlschema.AbstractXSFunctionImpl;

final class AGMFragmentBuilder extends AbstractXSFunctionImpl
{
    private final BGMBuilder builder;
    private final ExpressionPool pool;
    private XSComponent root;
    private ClassItem superClass;
    
    AGMFragmentBuilder(final BGMBuilder builder) {
        this.builder = builder;
        this.pool = builder.pool;
    }
    
    public Expression build(final XSComponent sc, final ClassItem owner) {
        this.superClass = this.findSuperClass(owner);
        this.root = sc;
        return sc.apply((XSFunction<Expression>)this);
    }
    
    public Object attGroupDecl(final XSAttGroupDecl decl) {
        return this.attributeContainer(decl);
    }
    
    private Expression attributeContainer(final XSAttContainer cont) {
        Expression exp = Expression.epsilon;
        final Iterator itr = cont.iterateAttributeUses();
        while (itr.hasNext()) {
            exp = this.pool.createSequence(exp, this.recurse(itr.next()));
        }
        return exp;
    }
    
    public Object attributeDecl(final XSAttributeDecl decl) {
        return this.attribute(decl, decl.getFixedValue(), decl.getContext());
    }
    
    private Expression attribute(final XSAttributeDecl decl, final String fixedValue, final ValidationContext context) {
        final SimpleNameClass name = new SimpleNameClass(decl.getTargetNamespace(), decl.getName());
        final Datatype dt = (Datatype)this.builder.simpleTypeBuilder.datatypeBuilder.build(decl.getType());
        if (fixedValue != null) {
            final Object value = dt.createValue(fixedValue, context);
            return this.pool.createAttribute((NameClass)name, this.pool.createValue(dt, (StringPair)null, value));
        }
        return this.pool.createAttribute((NameClass)name, this.pool.createData(dt, (StringPair)null));
    }
    
    public Object attributeUse(final XSAttributeUse use) {
        final Expression e = this.attribute(use.getDecl(), use.getFixedValue(), use.getContext());
        if (use.isRequired()) {
            return e;
        }
        return this.pool.createOptional(e);
    }
    
    public Object complexType(final XSComplexType type) {
        final XSContentType content = type.getContentType();
        Expression body = this.recurse(content);
        if (type.isMixed()) {
            body = this.pool.createMixed(body);
        }
        body = this.pool.createSequence(body, this.attributeContainer(type));
        return body;
    }
    
    public Object empty(final XSContentType empty) {
        return Expression.epsilon;
    }
    
    public Object particle(final XSParticle particle) {
        final XSTerm t = particle.getTerm();
        Expression exp;
        if (this.builder.particlesWithGlobalElementSkip.contains(particle)) {
            final XSElementDecl e = t.asElementDecl();
            if (e.isAbstract()) {
                exp = Expression.nullSet;
            }
            else {
                final ElementPattern ep = this._elementDecl(e);
                if (e.getType().isComplexType()) {
                    exp = this.pool.createChoice((Expression)this.builder.selector.bindToType(e), (Expression)ep);
                }
                else {
                    exp = (Expression)ep;
                }
            }
        }
        else {
            exp = this.recurse(t);
        }
        if (t.isElementDecl()) {
            exp = this.pool.createChoice(exp, this.builder.getSubstitionGroupList(t.asElementDecl()));
        }
        return this.builder.processMinMax(exp, particle);
    }
    
    public Object simpleType(final XSSimpleType simpleType) {
        return this.pool.createData(this.builder.simpleTypeBuilder.datatypeBuilder.build(simpleType));
    }
    
    public Object elementDecl(final XSElementDecl decl) {
        if (decl.isAbstract()) {
            return Expression.nullSet;
        }
        return this._elementDecl(decl);
    }
    
    private ElementPattern _elementDecl(final XSElementDecl decl) {
        Expression body = this.recurse(decl.getType(), this.root == decl);
        if (decl.getType() instanceof XSComplexType && this.builder.getGlobalBinding().isTypeSubstitutionSupportEnabled()) {
            if (decl.getType().asComplexType().isAbstract()) {
                body = Expression.nullSet;
            }
            body = this.pool.createChoice(body, this.builder.getTypeSubstitutionList((XSComplexType)decl.getType(), true));
        }
        else {
            body = this.pool.createSequence(body, this.builder.createXsiTypeExp(decl));
        }
        if (decl.isNillable()) {
            body = this.pool.createChoice(this.pool.createAttribute((NameClass)new SimpleNameClass("http://www.w3.org/2001/XMLSchema-instance", "nil"), this.pool.createValue((XSDatatype)BooleanType.theInstance, (Object)Boolean.TRUE)), body);
        }
        return new ElementPattern((NameClass)new SimpleNameClass(decl.getTargetNamespace(), decl.getName()), body);
    }
    
    public Object modelGroup(final XSModelGroup group) {
        final XSModelGroup.Compositor comp = group.getCompositor();
        Expression exp;
        if (comp == XSModelGroup.CHOICE) {
            exp = Expression.nullSet;
        }
        else {
            exp = Expression.epsilon;
        }
        for (int i = 0; i < group.getSize(); ++i) {
            final Expression item = this.recurse(group.getChild(i));
            if (comp == XSModelGroup.CHOICE) {
                exp = this.pool.createChoice(exp, item);
            }
            if (comp == XSModelGroup.SEQUENCE) {
                exp = this.pool.createSequence(exp, item);
            }
            if (comp == XSModelGroup.ALL) {
                exp = this.pool.createInterleave(exp, item);
            }
        }
        return exp;
    }
    
    public Object modelGroupDecl(final XSModelGroupDecl decl) {
        return this.recurse(decl.getModelGroup());
    }
    
    public Object wildcard(final XSWildcard wc) {
        return new WildcardItem(this.builder.grammar.codeModel, wc);
    }
    
    private Expression recurse(final XSComponent sc) {
        return this.recurse(sc, true);
    }
    
    private Expression recurse(final XSComponent sc, final boolean superClassCheck) {
        final Expression e = this.builder.selector.bindToType(sc);
        if (e == null) {
            return sc.apply((XSFunction<Expression>)this);
        }
        if (this.superClass == e && superClassCheck) {
            return (Expression)this.superClass.agm;
        }
        return e;
    }
    
    private ClassItem findSuperClass(final ClassItem parent) {
        if (parent == null) {
            return null;
        }
        final ClassItem[] result = { null };
        parent.exp.visit((ExpressionVisitorVoid)new AGMFragmentBuilder$1(this, result));
        return result[0];
    }
}
