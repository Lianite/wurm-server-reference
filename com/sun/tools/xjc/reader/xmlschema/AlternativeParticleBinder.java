// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.tools.xjc.reader.xmlschema;

import com.sun.xml.xsom.XSWildcard;
import com.sun.xml.xsom.XSModelGroupDecl;
import com.sun.codemodel.JDefinedClass;
import java.text.ParseException;
import com.sun.xml.xsom.XSModelGroup;
import com.sun.xml.xsom.XSDeclaration;
import com.sun.msv.grammar.trex.ElementPattern;
import com.sun.xml.xsom.XSElementDecl;
import com.sun.xml.xsom.XSTerm;
import com.sun.xml.xsom.XSComponent;
import com.sun.msv.grammar.Expression;
import com.sun.tools.xjc.grammar.ClassItem;
import com.sun.xml.xsom.XSParticle;
import com.sun.xml.xsom.visitor.XSTermFunction;

public class AlternativeParticleBinder extends ParticleBinder implements XSTermFunction, BGMBuilder.ParticleHandler
{
    private XSParticle parent;
    
    AlternativeParticleBinder(final BGMBuilder builder) {
        super(builder);
    }
    
    public Expression build(final XSParticle p, final ClassItem superClass) {
        return (Expression)this.particle(p);
    }
    
    public boolean checkFallback(final XSParticle p, final ClassItem superClass) {
        return false;
    }
    
    public Object particle(final XSParticle p) {
        this.builder.selector.bindToType((XSComponent)p);
        final XSParticle oldParent = this.parent;
        this.parent = p;
        final XSTerm t = p.getTerm();
        Expression exp;
        if (this.needSkip(t)) {
            final XSElementDecl e = t.asElementDecl();
            this.builder.particlesWithGlobalElementSkip.add(p);
            final ElementPattern eexp = this.builder.typeBuilder.elementDeclFlat(e);
            if (this.needSkippableElement(e)) {
                exp = this.pool.createChoice((Expression)eexp, (Expression)this.builder.selector.bindToType(e));
            }
            else {
                exp = (Expression)eexp;
            }
            exp = this.pool.createChoice(this.builder.getSubstitionGroupList(e), exp);
            exp = (Expression)this.builder.fieldBuilder.createFieldItem(this.computeLabel(p), false, exp, (XSComponent)p);
        }
        else {
            exp = t.apply((XSTermFunction<Expression>)this);
        }
        this.parent = oldParent;
        return this.builder.processMinMax(exp, p);
    }
    
    public Object elementDecl(final XSElementDecl decl) {
        final Expression typeExp = (Expression)this.builder.selector.bindToType(decl);
        if (typeExp != null) {
            return this.builder.fieldBuilder.createFieldItem(typeExp, (XSDeclaration)decl, false);
        }
        return this.builder.fieldBuilder.elementDecl(decl);
    }
    
    public Object modelGroup(final XSModelGroup group) {
        Expression typeExp = this.builder.selector.bindToType((XSComponent)group);
        if (typeExp == null) {
            if (group.getCompositor() == XSModelGroup.CHOICE || this.getLocalPropCustomization(this.parent) != null) {
                return this.builder.fieldBuilder.createFieldItem(this.builder.typeBuilder.build((XSComponent)group), group);
            }
            if (this.parent.getMaxOccurs() != 1) {
                try {
                    final JDefinedClass cls = this.builder.selector.getClassFactory().create(this.builder.getNameConverter().toClassName(NameGenerator.getName(this.builder, group)), group.getLocator());
                    final ClassItem ci = this.builder.grammar.createClassItem(cls, Expression.epsilon, group.getLocator());
                    this.builder.selector.queueBuild((XSComponent)group, ci);
                    typeExp = (Expression)ci;
                }
                catch (ParseException e) {
                    this.builder.errorReporter.error(group.getLocator(), "DefaultParticleBinder.UnableToGenerateNameFromModelGroup");
                    typeExp = null;
                }
            }
        }
        if (typeExp != null) {
            return this.builder.fieldBuilder.createFieldItem(typeExp, group);
        }
        return this.builder.applyRecursively(group, (BGMBuilder.ParticleHandler)this);
    }
    
    public Object modelGroupDecl(final XSModelGroupDecl decl) {
        final Expression typeExp = this.builder.selector.bindToType((XSComponent)decl);
        if (typeExp != null) {
            return this.builder.fieldBuilder.createFieldItem(typeExp, (XSDeclaration)decl, false);
        }
        this.builder.selector.pushClassFactory((JClassFactory)new PrefixedJClassFactoryImpl(this.builder, decl));
        final Object r = this.modelGroup(decl.getModelGroup());
        this.builder.selector.popClassFactory();
        return r;
    }
    
    public Object wildcard(final XSWildcard wc) {
        Expression typeExp = this.builder.selector.bindToType((XSComponent)wc);
        if (typeExp == null) {
            typeExp = this.builder.typeBuilder.build((XSComponent)wc);
        }
        return this.builder.fieldBuilder.createFieldItem("any", false, typeExp, (XSComponent)wc);
    }
}
