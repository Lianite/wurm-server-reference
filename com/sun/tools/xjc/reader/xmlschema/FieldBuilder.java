// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.tools.xjc.reader.xmlschema;

import com.sun.msv.datatype.DatabindableDatatype;
import com.sun.tools.xjc.grammar.xducer.WhitespaceTransducer;
import com.sun.tools.xjc.generator.util.WhitespaceNormalizer;
import com.sun.msv.datatype.xsd.XSDatatype;
import com.sun.tools.xjc.grammar.xducer.BuiltinDatatypeTransducerFactory;
import com.sun.msv.datatype.xsd.BooleanType;
import com.sun.msv.grammar.NameClass;
import com.sun.msv.grammar.trex.ElementPattern;
import com.sun.tools.xjc.generator.field.XsiNilFieldRenderer;
import com.sun.msv.grammar.SimpleNameClass;
import com.sun.tools.xjc.grammar.SuperClassItem;
import com.sun.tools.xjc.generator.field.XsiTypeFieldRenderer;
import com.sun.xml.xsom.XSElementDecl;
import java.text.ParseException;
import com.sun.xml.xsom.XSDeclaration;
import com.sun.xml.xsom.impl.ModelGroupImpl;
import com.sun.xml.xsom.XSModelGroup;
import com.sun.xml.xsom.XSWildcard;
import com.sun.xml.xsom.XSModelGroupDecl;
import com.sun.xml.xsom.impl.AnnotationImpl;
import com.sun.xml.xsom.impl.SchemaImpl;
import com.sun.xml.xsom.impl.ParticleImpl;
import com.sun.xml.xsom.impl.Ref;
import com.sun.xml.xsom.XSTerm;
import com.sun.xml.xsom.XSContentType;
import com.sun.tools.xjc.grammar.ClassItem;
import com.sun.xml.xsom.XSParticle;
import com.sun.xml.xsom.XSSimpleType;
import com.sun.xml.xsom.XSComplexType;
import com.sun.tools.xjc.grammar.FieldItem;
import org.xml.sax.Locator;
import com.sun.tools.xjc.generator.field.ConstFieldRenderer;
import com.sun.tools.xjc.grammar.DefaultValue;
import com.sun.msv.grammar.ExpressionVisitorVoid;
import java.util.ArrayList;
import com.sun.tools.xjc.reader.xmlschema.bindinfo.BIProperty;
import com.sun.msv.grammar.AttributeExp;
import com.sun.xml.xsom.XSAttributeDecl;
import java.util.Iterator;
import com.sun.xml.xsom.XSAttributeUse;
import com.sun.xml.xsom.XSAttContainer;
import com.sun.xml.xsom.XSAttGroupDecl;
import com.sun.xml.xsom.visitor.XSFunction;
import com.sun.msv.grammar.Expression;
import com.sun.xml.xsom.XSComponent;
import com.sun.msv.grammar.ExpressionPool;

public class FieldBuilder extends AbstractXSFunctionImpl
{
    private final BGMBuilder builder;
    private final ExpressionPool pool;
    
    FieldBuilder(final BGMBuilder _builder) {
        this.builder = _builder;
        this.pool = this.builder.pool;
    }
    
    public final Expression build(final XSComponent sc) {
        return sc.apply((XSFunction<Expression>)this);
    }
    
    public Object attGroupDecl(final XSAttGroupDecl decl) {
        return this.attributeContainer(decl);
    }
    
    public Expression attributeContainer(final XSAttContainer decl) {
        Expression exp = Expression.epsilon;
        Iterator itr = decl.iterateAttGroups();
        while (itr.hasNext()) {
            exp = this.pool.createSequence(exp, this.build(itr.next()));
        }
        itr = decl.iterateDeclaredAttributeUses();
        while (itr.hasNext()) {
            exp = this.pool.createSequence(exp, this.build(itr.next()));
        }
        return exp;
    }
    
    public Object attributeDecl(final XSAttributeDecl arg0) {
        _assert(false);
        return null;
    }
    
    public Object attributeUse(final XSAttributeUse use) {
        final BIProperty cust = this.getPropCustomization(use);
        AttributeExp body = this.builder.typeBuilder._attributeDecl(use.getDecl());
        final Expression originalBody = body.exp;
        final boolean hasFixedValue = use.getFixedValue() != null;
        if (hasFixedValue) {
            final String token = use.getFixedValue();
            final Expression contents = FixedExpBuilder.build(body.exp, token, this.builder.grammar, use.getContext());
            if (contents == Expression.nullSet) {
                Locator loc;
                if (use.getDecl().getFixedValue() != null) {
                    loc = use.getDecl().getLocator();
                }
                else {
                    loc = use.getLocator();
                }
                this.builder.errorReporter.error(loc, "FieldBuilder.IncorrectFixedValue", (Object)token);
            }
            else {
                body = new AttributeExp(body.nameClass, contents);
            }
        }
        final boolean toConstant = BIProperty.getCustomization(this.builder, (XSComponent)use).isConstantProperty() && use.getFixedValue() != null;
        final String xmlName = use.getDecl().getName();
        final String defaultName = toConstant ? this.makeJavaConstName(xmlName) : this.makeJavaName(xmlName);
        final FieldItem exp = this.createFieldItem(defaultName, toConstant, (Expression)body, use);
        if (use.getDefaultValue() != null) {
            final String token2 = use.getDefaultValue();
            final Expression contents2 = FixedExpBuilder.build(body.exp, token2, this.builder.grammar, use.getContext());
            if (contents2 == Expression.nullSet) {
                Locator loc2;
                if (use.getDecl().getDefaultValue() != null) {
                    loc2 = use.getDecl().getLocator();
                }
                else {
                    loc2 = use.getLocator();
                }
                this.builder.errorReporter.error(loc2, "FieldBuilder.IncorrectDefaultValue", (Object)token2);
            }
            final ArrayList values = new ArrayList();
            contents2.visit((ExpressionVisitorVoid)new FieldBuilder$1(this, values));
            exp.defaultValues = values.toArray(new DefaultValue[values.size()]);
        }
        if (toConstant) {
            exp.realization = ConstFieldRenderer.theFactory;
        }
        if (hasFixedValue) {
            originalBody.visit((ExpressionVisitorVoid)new FieldBuilder$2(this, exp, use, cust));
        }
        if (!use.isRequired()) {
            return this.pool.createOptional((Expression)exp);
        }
        return exp;
    }
    
    private BIProperty getPropCustomization(final XSAttributeUse use) {
        final BIProperty cust = (BIProperty)this.builder.getBindInfo(use).get(BIProperty.NAME);
        if (cust != null) {
            return cust;
        }
        return (BIProperty)this.builder.getBindInfo(use.getDecl()).get(BIProperty.NAME);
    }
    
    public Object complexType(final XSComplexType type) {
        return this.builder.complexTypeBuilder.build(type);
    }
    
    public Object simpleType(final XSSimpleType type) {
        return this.simpleType(type, type.getOwnerSchema());
    }
    
    public Expression simpleType(final XSSimpleType type, final XSComponent property) {
        final BIProperty prop = BIProperty.getCustomization(this.builder, property);
        return (Expression)prop.createFieldItem("Value", false, this.builder.simpleTypeBuilder.build(type), (XSComponent)type);
    }
    
    public Object particle(final XSParticle p) {
        _assert(false);
        return null;
    }
    
    private Expression particle(final XSParticle p, final ClassItem superClass) {
        return this.builder.particleBinder.build(p, superClass);
    }
    
    public Object empty(final XSContentType ct) {
        return Expression.epsilon;
    }
    
    private XSParticle makeParticle(final XSTerm t) {
        return new ParticleImpl((SchemaImpl)null, (AnnotationImpl)null, (Ref.Term)t, t.getLocator());
    }
    
    public Object modelGroupDecl(final XSModelGroupDecl decl) {
        this.builder.selector.pushClassFactory((JClassFactory)new PrefixedJClassFactoryImpl(this.builder, decl));
        final Object r = this.build(decl.getModelGroup());
        this.builder.selector.popClassFactory();
        return r;
    }
    
    public Object wildcard(final XSWildcard wc) {
        return this.particle(this.makeParticle(wc), null);
    }
    
    public Object modelGroup(final XSModelGroup mg) {
        if (this.builder.getGlobalBinding().isModelGroupBinding()) {
            return this.builder.applyRecursively(mg, (BGMBuilder.ParticleHandler)new FieldBuilder$3(this));
        }
        final XSModelGroup mg2 = new ModelGroupImpl((SchemaImpl)mg.getOwnerSchema(), (AnnotationImpl)null, mg.getLocator(), mg.getCompositor(), mg.getChildren());
        return this.particle(this.makeParticle(mg2), null);
    }
    
    public FieldItem createFieldItem(final Expression typeExp, final XSDeclaration source, final boolean forConstant) {
        final String defaultName = this.builder.getNameConverter().toPropertyName(source.getName());
        return this.createFieldItem(defaultName, forConstant, typeExp, source);
    }
    
    public Expression createFieldItem(final Expression typeExp, final XSModelGroup modelGroup) {
        try {
            final String defaultName = NameGenerator.getName(this.builder, modelGroup);
            return (Expression)this.createFieldItem(defaultName, false, typeExp, modelGroup);
        }
        catch (ParseException e) {
            this.builder.errorReporter.error(modelGroup.getLocator(), "ClassSelector.ClassNameIsRequired");
            return Expression.epsilon;
        }
    }
    
    public FieldItem createFieldItem(final String defaultName, final boolean forConstant, final Expression typeExp, final XSComponent source) {
        final BIProperty cust = BIProperty.getCustomization(this.builder, source);
        return cust.createFieldItem(defaultName, forConstant, typeExp, source);
    }
    
    public Object elementDecl(final XSElementDecl decl) {
        final boolean isMappedToType = this.builder.selector.bindToType(decl) != null;
        if (!isMappedToType) {
            return this.createFieldItem((Expression)this.builder.typeBuilder.elementDeclFlat(decl), decl, false);
        }
        Expression type = this.builder.selector.bindToType((XSComponent)decl.getType());
        Expression body;
        if (type != null) {
            _assert(type instanceof ClassItem);
            final ClassItem defaultType = (ClassItem)type;
            if (decl.getType() instanceof XSComplexType && this.builder.getGlobalBinding().isTypeSubstitutionSupportEnabled()) {
                type = this.pool.createChoice(type, this.builder.getTypeSubstitutionList((XSComplexType)decl.getType(), false));
            }
            else {
                type = this.pool.createSequence(type, this.builder.createXsiTypeExp(decl));
            }
            if (this.builder.getGlobalBinding().isTypeSubstitutionSupportEnabled()) {
                final FieldItem fi = new FieldItem("ValueObject", type, decl.getLocator());
                fi.realization = new XsiTypeFieldRenderer.Factory(defaultType);
                fi.setDelegation(true);
                fi.javadoc = Messages.format("FieldBuilder.Javadoc.ValueObject", (Object)defaultType.getType().fullName(), (Object)fi.name);
                body = (Expression)fi;
            }
            else {
                body = (Expression)new SuperClassItem(type, decl.getLocator());
            }
        }
        else {
            this.builder.simpleTypeBuilder.refererStack.push(decl);
            body = this.build(decl.getType());
            this.builder.simpleTypeBuilder.refererStack.pop();
            body = this.pool.createSequence(body, this.builder.createXsiTypeExp(decl));
        }
        final SimpleNameClass name = new SimpleNameClass(decl.getTargetNamespace(), decl.getName());
        if (decl.isNillable()) {
            final FieldItem fi = new FieldItem("Nil", this.buildXsiNilExpForClass(), decl.getLocator());
            fi.realization = XsiNilFieldRenderer.theFactory;
            fi.javadoc = Messages.format("FieldBuilder.Javadoc.NilProperty");
            body.visit((ExpressionVisitorVoid)new FieldBuilder$4(this));
            body = this.pool.createChoice((Expression)fi, body);
        }
        return new ElementPattern((NameClass)name, body);
    }
    
    private Expression buildXsiNilExpForClass() {
        return (Expression)new AttributeExp((NameClass)new SimpleNameClass("http://www.w3.org/2001/XMLSchema-instance", "nil"), (Expression)this.builder.grammar.createPrimitiveItem(WhitespaceTransducer.create(BuiltinDatatypeTransducerFactory.get(this.builder.grammar, (XSDatatype)BooleanType.theInstance), this.builder.grammar.codeModel, WhitespaceNormalizer.COLLAPSE), (DatabindableDatatype)BooleanType.theInstance, this.pool.createData((XSDatatype)BooleanType.theInstance), null));
    }
    
    private String makeJavaName(final String xmlName) {
        return this.builder.getNameConverter().toPropertyName(xmlName);
    }
    
    private String makeJavaConstName(final String xmlName) {
        return this.builder.getNameConverter().toConstantName(xmlName);
    }
}
