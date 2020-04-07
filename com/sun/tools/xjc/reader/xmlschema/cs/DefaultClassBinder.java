// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.tools.xjc.reader.xmlschema.cs;

import com.sun.codemodel.JJavaName;
import com.sun.tools.xjc.reader.xmlschema.bindinfo.BIClass;
import com.sun.tools.xjc.grammar.TypeItem;
import com.sun.tools.xjc.reader.xmlschema.JClassFactory;
import com.sun.xml.xsom.XSSchema;
import com.sun.xml.xsom.XSFacet;
import com.sun.xml.xsom.XSNotation;
import com.sun.xml.xsom.XSAnnotation;
import com.sun.xml.xsom.XSWildcard;
import com.sun.xml.xsom.XSParticle;
import com.sun.xml.xsom.XSSimpleType;
import com.sun.xml.xsom.XSAttributeUse;
import com.sun.xml.xsom.XSContentType;
import com.sun.xml.xsom.XSElementDecl;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JPackage;
import com.sun.tools.xjc.grammar.ClassItem;
import com.sun.tools.xjc.reader.xmlschema.bindinfo.BISchemaBinding;
import com.sun.codemodel.JClassContainer;
import com.sun.xml.xsom.XSDeclaration;
import com.sun.xml.xsom.XSComplexType;
import com.sun.xml.xsom.XSModelGroupDecl;
import java.text.ParseException;
import com.sun.tools.xjc.reader.xmlschema.NameGenerator;
import com.sun.xml.xsom.XSModelGroup;
import com.sun.xml.xsom.XSAttributeDecl;
import com.sun.xml.xsom.XSComponent;
import com.sun.xml.xsom.XSAttGroupDecl;

class DefaultClassBinder extends AbstractBinderImpl
{
    DefaultClassBinder(final ClassSelector classSelector) {
        super(classSelector);
    }
    
    public Object attGroupDecl(final XSAttGroupDecl decl) {
        return this.allow(decl, decl.getName());
    }
    
    public Object attributeDecl(final XSAttributeDecl decl) {
        return this.allow(decl, decl.getName());
    }
    
    public Object modelGroup(final XSModelGroup mgroup) {
        String defaultName;
        try {
            defaultName = NameGenerator.getName(this.owner.builder, mgroup);
        }
        catch (ParseException e) {
            defaultName = null;
        }
        return this.allow(mgroup, defaultName);
    }
    
    public Object modelGroupDecl(final XSModelGroupDecl decl) {
        return this.allow(decl, decl.getName());
    }
    
    public Object complexType(final XSComplexType type) {
        final ClassItem ci = this.allow(type, type.getName());
        if (ci != null) {
            return ci;
        }
        if (type.isGlobal()) {
            final JPackage pkg = this.owner.getPackage(type.getTargetNamespace());
            final JDefinedClass clazz = this.owner.codeModelClassFactory.createInterface(pkg, this.deriveName(type), type.getLocator());
            return this.wrapByClassItem(type, clazz);
        }
        String className = this.builder.getNameConverter().toClassName(type.getScope().getName());
        final BISchemaBinding sb = (BISchemaBinding)this.builder.getBindInfo(type.getOwnerSchema()).get(BISchemaBinding.NAME);
        if (sb != null) {
            className = sb.mangleAnonymousTypeClassName(className);
        }
        else {
            className += "Type";
        }
        return this.wrapByClassItem(type, this.getClassFactory(type).create(className, type.getLocator()));
    }
    
    public Object elementDecl(final XSElementDecl decl) {
        ClassItem r = this.allow(decl, decl.getName());
        if (r == null && decl.isGlobal()) {
            r = this.wrapByClassItem(decl, this.owner.codeModelClassFactory.createInterface(this.owner.getPackage(decl.getTargetNamespace()), this.deriveName(decl), decl.getLocator()));
        }
        if (r != null) {
            r.getTypeAsDefined()._implements((DefaultClassBinder.class$javax$xml$bind$Element == null) ? (DefaultClassBinder.class$javax$xml$bind$Element = class$("javax.xml.bind.Element")) : DefaultClassBinder.class$javax$xml$bind$Element);
        }
        return r;
    }
    
    public Object empty(final XSContentType ct) {
        return null;
    }
    
    public Object attributeUse(final XSAttributeUse use) {
        return this.never(use);
    }
    
    public Object simpleType(final XSSimpleType type) {
        this.builder.simpleTypeBuilder.refererStack.push(type);
        this.builder.simpleTypeBuilder.build(type);
        this.builder.simpleTypeBuilder.refererStack.pop();
        return this.never(type);
    }
    
    public Object particle(final XSParticle particle) {
        return this.never(particle);
    }
    
    public Object wildcard(final XSWildcard wc) {
        return this.never(wc);
    }
    
    public Object annotation(final XSAnnotation annon) {
        _assert(false);
        return null;
    }
    
    public Object notation(final XSNotation not) {
        _assert(false);
        return null;
    }
    
    public Object facet(final XSFacet decl) {
        _assert(false);
        return null;
    }
    
    public Object schema(final XSSchema schema) {
        _assert(false);
        return null;
    }
    
    private JClassFactory getClassFactory(final XSComponent component) {
        final JClassFactory cf = this.owner.getClassFactory();
        if (component instanceof XSComplexType) {
            final XSComplexType xsct = (XSComplexType)component;
            if (xsct.isLocal()) {
                final TypeItem parent = this.owner.bindToType(xsct.getScope());
                if (parent instanceof ClassItem) {
                    return (JClassFactory)new JClassFactoryImpl(this.owner, ((ClassItem)parent).getTypeAsDefined().parentContainer());
                }
            }
        }
        return cf;
    }
    
    private ClassItem never(final XSComponent component) {
        return null;
    }
    
    private ClassItem allow(final XSComponent component, String defaultBaseName) {
        final BIClass decl = (BIClass)this.builder.getBindInfo(component).get(BIClass.NAME);
        if (decl == null) {
            return null;
        }
        decl.markAsAcknowledged();
        String clsName = decl.getClassName();
        if (clsName == null) {
            if (defaultBaseName == null) {
                this.builder.errorReceiver.error(decl.getLocation(), Messages.format("ClassSelector.ClassNameIsRequired"));
                defaultBaseName = "undefined" + component.hashCode();
            }
            clsName = this.deriveName(defaultBaseName, component);
        }
        else if (!JJavaName.isJavaIdentifier(clsName)) {
            this.builder.errorReceiver.error(decl.getLocation(), Messages.format("ClassSelector.IncorrectClassName", (Object)clsName));
            clsName = "Undefined" + component.hashCode();
        }
        final JDefinedClass r = this.getClassFactory(component).create(clsName, decl.getLocation());
        if (decl.getJavadoc() != null) {
            r.javadoc().appendComment(decl.getJavadoc() + "\n\n");
        }
        final ClassItem ci = this.wrapByClassItem(component, r);
        final String implClass = decl.getUserSpecifiedImplClass();
        if (implClass != null) {
            ci.setUserSpecifiedImplClass(implClass);
        }
        return ci;
    }
}
