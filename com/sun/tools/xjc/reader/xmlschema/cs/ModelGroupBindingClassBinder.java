// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.tools.xjc.reader.xmlschema.cs;

import com.sun.xml.xsom.XSWildcard;
import com.sun.xml.xsom.XSElementDecl;
import com.sun.xml.xsom.XSSimpleType;
import com.sun.xml.xsom.XSContentType;
import com.sun.xml.xsom.XSSchema;
import com.sun.xml.xsom.XSNotation;
import com.sun.xml.xsom.XSFacet;
import com.sun.xml.xsom.XSAttributeUse;
import com.sun.xml.xsom.XSAttributeDecl;
import com.sun.xml.xsom.XSAttGroupDecl;
import com.sun.xml.xsom.XSAnnotation;
import com.sun.xml.xsom.XSParticle;
import com.sun.codemodel.JPackage;
import com.sun.codemodel.JClassContainer;
import com.sun.xml.xsom.XSDeclaration;
import com.sun.xml.xsom.XSModelGroupDecl;
import com.sun.xml.xsom.XSComplexType;
import com.sun.codemodel.JDefinedClass;
import java.text.ParseException;
import com.sun.xml.xsom.XSComponent;
import com.sun.tools.xjc.reader.xmlschema.NameGenerator;
import com.sun.tools.xjc.grammar.ClassItem;
import com.sun.xml.xsom.XSModelGroup;
import java.util.HashSet;
import java.util.Set;

class ModelGroupBindingClassBinder extends AbstractBinderImpl
{
    private final ClassBinder base;
    private final Set topLevelChoices;
    
    ModelGroupBindingClassBinder(final ClassSelector classSelector, final ClassBinder base) {
        super(classSelector);
        this.topLevelChoices = new HashSet();
        this.base = base;
    }
    
    public Object modelGroup(final XSModelGroup mgroup) {
        ClassItem ci = this.base.modelGroup(mgroup);
        if (mgroup.getCompositor() == XSModelGroup.CHOICE && !this.topLevelChoices.contains(mgroup)) {
            if (ci == null && !this.builder.getGlobalBinding().isChoiceContentPropertyModelGroupBinding()) {
                try {
                    final JDefinedClass clazz = this.owner.getClassFactory().create(NameGenerator.getName(this.owner.builder, mgroup), mgroup.getLocator());
                    ci = this.wrapByClassItem(mgroup, clazz);
                }
                catch (ParseException e) {
                    this.builder.errorReceiver.error(mgroup.getLocator(), Messages.format("DefaultParticleBinder.UnableToGenerateNameFromModelGroup"));
                    ci = null;
                }
            }
            if (ci != null) {
                ci.hasGetContentMethod = true;
            }
        }
        return ci;
    }
    
    public Object complexType(final XSComplexType type) {
        final ClassItem ci = this.base.complexType(type);
        if (ci == null) {
            return null;
        }
        if (this.needsToHaveChoiceContentProperty(type)) {
            this.topLevelChoices.add(type.getContentType().asParticle().getTerm());
            ci.hasGetContentMethod = true;
        }
        return ci;
    }
    
    public Object modelGroupDecl(final XSModelGroupDecl decl) {
        ClassItem ci = this.base.modelGroupDecl(decl);
        if (ci != null) {
            return ci;
        }
        final JPackage pkg = this.owner.getPackage(decl.getTargetNamespace());
        final JDefinedClass clazz = this.owner.codeModelClassFactory.createInterface(pkg, this.deriveName(decl), decl.getLocator());
        ci = this.wrapByClassItem(decl, clazz);
        if (this.needsToHaveChoiceContentProperty(decl)) {
            ci.hasGetContentMethod = true;
        }
        return ci;
    }
    
    private boolean needsToHaveChoiceContentProperty(final XSComplexType type) {
        if (type.iterateDeclaredAttributeUses().hasNext()) {
            return false;
        }
        final XSParticle p = type.getContentType().asParticle();
        if (p == null) {
            return false;
        }
        if (p.getMaxOccurs() != 1) {
            return false;
        }
        final XSModelGroup mg = p.getTerm().asModelGroup();
        return mg != null && !this.builder.getGlobalBinding().isChoiceContentPropertyModelGroupBinding() && mg.getCompositor() == XSModelGroup.CHOICE;
    }
    
    private boolean needsToHaveChoiceContentProperty(final XSModelGroupDecl decl) {
        return decl.getModelGroup().getCompositor() == XSModelGroup.CHOICE;
    }
    
    public Object annotation(final XSAnnotation ann) {
        return this.base.annotation(ann);
    }
    
    public Object attGroupDecl(final XSAttGroupDecl decl) {
        return this.base.attGroupDecl(decl);
    }
    
    public Object attributeDecl(final XSAttributeDecl decl) {
        return this.base.attributeDecl(decl);
    }
    
    public Object attributeUse(final XSAttributeUse use) {
        return this.base.attributeUse(use);
    }
    
    public Object facet(final XSFacet facet) {
        return this.base.facet(facet);
    }
    
    public Object notation(final XSNotation notation) {
        return this.base.notation(notation);
    }
    
    public Object schema(final XSSchema schema) {
        return this.base.schema(schema);
    }
    
    public Object empty(final XSContentType empty) {
        return this.base.empty(empty);
    }
    
    public Object particle(final XSParticle particle) {
        return this.base.particle(particle);
    }
    
    public Object simpleType(final XSSimpleType simpleType) {
        return this.base.simpleType(simpleType);
    }
    
    public Object elementDecl(final XSElementDecl decl) {
        return this.base.elementDecl(decl);
    }
    
    public Object wildcard(final XSWildcard wc) {
        return this.base.wildcard(wc);
    }
}
