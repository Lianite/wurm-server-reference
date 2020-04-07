// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.tools.xjc.reader.xmlschema;

import com.sun.tools.xjc.reader.xmlschema.bindinfo.BIGlobalBinding;
import com.sun.tools.xjc.ErrorReceiver;
import com.sun.tools.xjc.model.CClassRef;
import com.sun.tools.xjc.reader.xmlschema.ct.ComplexTypeBindingMode;
import com.sun.tools.xjc.reader.xmlschema.ct.ComplexTypeFieldBuilder;
import com.sun.codemodel.JJavaName;
import com.sun.xml.xsom.XSSchema;
import com.sun.xml.xsom.XSFacet;
import com.sun.xml.xsom.XSNotation;
import com.sun.xml.xsom.XSAnnotation;
import com.sun.xml.xsom.XSWildcard;
import com.sun.xml.xsom.XSParticle;
import com.sun.xml.xsom.XSSimpleType;
import com.sun.xml.xsom.XSAttributeUse;
import com.sun.xml.xsom.XSXPath;
import com.sun.xml.xsom.XSIdentityConstraint;
import com.sun.xml.xsom.XSContentType;
import com.sun.tools.xjc.model.CCustomizations;
import com.sun.tools.xjc.model.CClass;
import com.sun.istack.Nullable;
import java.util.Iterator;
import java.util.Set;
import com.sun.istack.NotNull;
import com.sun.tools.xjc.reader.xmlschema.bindinfo.BIXSubstitutable;
import com.sun.tools.xjc.model.CClassInfoParent;
import com.sun.codemodel.JPackage;
import com.sun.xml.xsom.XSElementDecl;
import org.xml.sax.Locator;
import com.sun.tools.xjc.reader.xmlschema.bindinfo.BindInfo;
import com.sun.tools.xjc.reader.xmlschema.bindinfo.BIClass;
import javax.xml.namespace.QName;
import com.sun.tools.xjc.reader.xmlschema.bindinfo.BISchemaBinding;
import com.sun.tools.xjc.model.CElementInfo;
import com.sun.tools.xjc.model.CClassInfo;
import com.sun.xml.xsom.XSDeclaration;
import com.sun.xml.xsom.XSType;
import com.sun.xml.xsom.XSComplexType;
import com.sun.xml.xsom.XSModelGroupDecl;
import com.sun.xml.xsom.XSModelGroup;
import com.sun.xml.xsom.XSAttributeDecl;
import com.sun.xml.xsom.XSComponent;
import com.sun.tools.xjc.model.CElement;
import com.sun.xml.xsom.XSAttGroupDecl;
import com.sun.tools.xjc.reader.Ring;
import com.sun.tools.xjc.model.Model;

final class DefaultClassBinder implements ClassBinder
{
    private final SimpleTypeBuilder stb;
    private final Model model;
    protected final BGMBuilder builder;
    protected final ClassSelector selector;
    
    DefaultClassBinder() {
        this.stb = Ring.get(SimpleTypeBuilder.class);
        this.model = Ring.get(Model.class);
        this.builder = Ring.get(BGMBuilder.class);
        this.selector = Ring.get(ClassSelector.class);
    }
    
    public CElement attGroupDecl(final XSAttGroupDecl decl) {
        return this.allow(decl, decl.getName());
    }
    
    public CElement attributeDecl(final XSAttributeDecl decl) {
        return this.allow(decl, decl.getName());
    }
    
    public CElement modelGroup(final XSModelGroup mgroup) {
        return this.never();
    }
    
    public CElement modelGroupDecl(final XSModelGroupDecl decl) {
        return this.never();
    }
    
    public CElement complexType(final XSComplexType type) {
        final CElement ci = this.allow(type, type.getName());
        if (ci != null) {
            return ci;
        }
        final BindInfo bi = this.builder.getBindInfo(type);
        if (type.isGlobal()) {
            QName tagName = null;
            String className = this.deriveName(type);
            Locator loc = type.getLocator();
            if (this.getGlobalBinding().isSimpleMode()) {
                final XSElementDecl referer = this.getSoleElementReferer(type);
                if (referer != null && this.isCollapsable(referer)) {
                    tagName = BGMBuilder.getName(referer);
                    className = this.deriveName(referer);
                    loc = referer.getLocator();
                }
            }
            final JPackage pkg = this.selector.getPackage(type.getTargetNamespace());
            return new CClassInfo(this.model, pkg, className, loc, this.getTypeName(type), tagName, type, bi.toCustomizationList());
        }
        final XSElementDecl element = type.getScope();
        if (!element.isGlobal() || !this.isCollapsable(element)) {
            final CElement parentType = this.selector.isBound(element, type);
            CClassInfoParent scope;
            String className2;
            if (parentType != null && parentType instanceof CElementInfo && ((CElementInfo)parentType).hasClass()) {
                scope = (CElementInfo)parentType;
                className2 = "Type";
            }
            else {
                className2 = this.builder.getNameConverter().toClassName(element.getName());
                final BISchemaBinding sb = this.builder.getBindInfo(type.getOwnerSchema()).get(BISchemaBinding.class);
                if (sb != null) {
                    className2 = sb.mangleAnonymousTypeClassName(className2);
                }
                scope = this.selector.getClassScope();
            }
            return new CClassInfo(this.model, scope, className2, type.getLocator(), null, null, type, bi.toCustomizationList());
        }
        if (this.builder.getBindInfo(element).get(BIClass.class) != null) {
            return null;
        }
        return new CClassInfo(this.model, this.selector.getClassScope(), this.deriveName(element), element.getLocator(), null, BGMBuilder.getName(element), element, bi.toCustomizationList());
    }
    
    private QName getTypeName(final XSComplexType type) {
        if (type.getRedefinedBy() != null) {
            return null;
        }
        return BGMBuilder.getName(type);
    }
    
    private boolean isCollapsable(final XSElementDecl decl) {
        final XSType type = decl.getType();
        if (!type.isComplexType()) {
            return false;
        }
        if (decl.getSubstitutables().size() > 1 || decl.getSubstAffiliation() != null) {
            return false;
        }
        if (decl.isNillable()) {
            return false;
        }
        final BIXSubstitutable bixSubstitutable = this.builder.getBindInfo(decl).get(BIXSubstitutable.class);
        if (bixSubstitutable != null) {
            bixSubstitutable.markAsAcknowledged();
            return false;
        }
        if (this.getGlobalBinding().isSimpleMode() && decl.isGlobal()) {
            final XSElementDecl referer = this.getSoleElementReferer(decl.getType());
            if (referer != null) {
                assert referer == decl;
                return true;
            }
        }
        return type.isLocal() && type.isComplexType();
    }
    
    @Nullable
    private XSElementDecl getSoleElementReferer(@NotNull final XSType t) {
        final Set<XSComponent> referer = this.builder.getReferer(t);
        XSElementDecl sole = null;
        for (final XSComponent r : referer) {
            if (!(r instanceof XSElementDecl)) {
                return null;
            }
            final XSElementDecl x = (XSElementDecl)r;
            if (!x.isGlobal()) {
                continue;
            }
            if (sole != null) {
                return null;
            }
            sole = x;
        }
        return sole;
    }
    
    public CElement elementDecl(final XSElementDecl decl) {
        CElement r = this.allow(decl, decl.getName());
        if (r == null) {
            final QName tagName = BGMBuilder.getName(decl);
            final CCustomizations custs = this.builder.getBindInfo(decl).toCustomizationList();
            if (decl.isGlobal()) {
                if (this.isCollapsable(decl)) {
                    return this.selector.bindToType(decl.getType().asComplexType(), decl, true);
                }
                String className = null;
                if (this.getGlobalBinding().isGenerateElementClass()) {
                    className = this.deriveName(decl);
                }
                final CElementInfo cei = new CElementInfo(this.model, tagName, this.selector.getClassScope(), className, custs, decl.getLocator());
                this.selector.boundElements.put(decl, cei);
                this.stb.refererStack.push(decl);
                cei.initContentType(this.selector.bindToType(decl.getType(), decl), decl, decl.getDefaultValue());
                this.stb.refererStack.pop();
                r = cei;
            }
        }
        final XSElementDecl top = decl.getSubstAffiliation();
        if (top != null) {
            final CElement topci = this.selector.bindToType(top, decl);
            if (r instanceof CClassInfo && topci instanceof CClassInfo) {
                ((CClassInfo)r).setBaseClass((CClass)topci);
            }
            if (r instanceof CElementInfo && topci instanceof CElementInfo) {
                ((CElementInfo)r).setSubstitutionHead((CElementInfo)topci);
            }
        }
        return r;
    }
    
    public CClassInfo empty(final XSContentType ct) {
        return null;
    }
    
    public CClassInfo identityConstraint(final XSIdentityConstraint xsIdentityConstraint) {
        return this.never();
    }
    
    public CClassInfo xpath(final XSXPath xsxPath) {
        return this.never();
    }
    
    public CClassInfo attributeUse(final XSAttributeUse use) {
        return this.never();
    }
    
    public CElement simpleType(final XSSimpleType type) {
        final CElement c = this.allow(type, type.getName());
        if (c != null) {
            return c;
        }
        if (this.getGlobalBinding().isSimpleTypeSubstitution() && type.isGlobal()) {
            return new CClassInfo(this.model, this.selector.getClassScope(), this.deriveName(type), type.getLocator(), BGMBuilder.getName(type), null, type, null);
        }
        return this.never();
    }
    
    public CClassInfo particle(final XSParticle particle) {
        return this.never();
    }
    
    public CClassInfo wildcard(final XSWildcard wc) {
        return this.never();
    }
    
    public CClassInfo annotation(final XSAnnotation annon) {
        assert false;
        return null;
    }
    
    public CClassInfo notation(final XSNotation not) {
        assert false;
        return null;
    }
    
    public CClassInfo facet(final XSFacet decl) {
        assert false;
        return null;
    }
    
    public CClassInfo schema(final XSSchema schema) {
        assert false;
        return null;
    }
    
    private CClassInfo never() {
        return null;
    }
    
    private CElement allow(final XSComponent component, String defaultBaseName) {
        final BindInfo bindInfo = this.builder.getBindInfo(component);
        final BIClass decl = bindInfo.get(BIClass.class);
        if (decl == null) {
            return null;
        }
        decl.markAsAcknowledged();
        final String ref = decl.getExistingClassRef();
        if (ref != null) {
            if (JJavaName.isFullyQualifiedClassName(ref)) {
                if (component instanceof XSComplexType) {
                    Ring.get(ComplexTypeFieldBuilder.class).recordBindingMode((XSComplexType)component, ComplexTypeBindingMode.NORMAL);
                }
                return new CClassRef(this.model, component, decl, bindInfo.toCustomizationList());
            }
            Ring.get(ErrorReceiver.class).error(decl.getLocation(), Messages.format("ClassSelector.IncorrectClassName", ref));
        }
        String clsName = decl.getClassName();
        if (clsName == null) {
            if (defaultBaseName == null) {
                Ring.get(ErrorReceiver.class).error(decl.getLocation(), Messages.format("ClassSelector.ClassNameIsRequired", new Object[0]));
                defaultBaseName = "undefined" + component.hashCode();
            }
            clsName = this.builder.deriveName(defaultBaseName, component);
        }
        else if (!JJavaName.isJavaIdentifier(clsName)) {
            Ring.get(ErrorReceiver.class).error(decl.getLocation(), Messages.format("ClassSelector.IncorrectClassName", clsName));
            clsName = "Undefined" + component.hashCode();
        }
        QName typeName = null;
        QName elementName = null;
        if (component instanceof XSType) {
            final XSType t = (XSType)component;
            typeName = BGMBuilder.getName(t);
        }
        if (component instanceof XSElementDecl) {
            final XSElementDecl e = (XSElementDecl)component;
            elementName = BGMBuilder.getName(e);
        }
        if (component instanceof XSElementDecl && !this.isCollapsable((XSElementDecl)component)) {
            final XSElementDecl e = (XSElementDecl)component;
            final CElementInfo cei = new CElementInfo(this.model, elementName, this.selector.getClassScope(), clsName, bindInfo.toCustomizationList(), decl.getLocation());
            this.selector.boundElements.put(e, cei);
            this.stb.refererStack.push(component);
            cei.initContentType(this.selector.bindToType(e.getType(), e), e, e.getDefaultValue());
            this.stb.refererStack.pop();
            return cei;
        }
        final CClassInfo bt = new CClassInfo(this.model, this.selector.getClassScope(), clsName, decl.getLocation(), typeName, elementName, component, bindInfo.toCustomizationList());
        if (decl.getJavadoc() != null) {
            bt.javadoc = decl.getJavadoc() + "\n\n";
        }
        final String implClass = decl.getUserSpecifiedImplClass();
        if (implClass != null) {
            bt.setUserSpecifiedImplClass(implClass);
        }
        return bt;
    }
    
    private BIGlobalBinding getGlobalBinding() {
        return this.builder.getGlobalBinding();
    }
    
    private String deriveName(final XSDeclaration comp) {
        return this.builder.deriveName(comp.getName(), comp);
    }
    
    private String deriveName(final XSComplexType comp) {
        String seed = this.builder.deriveName(comp.getName(), comp);
        for (int cnt = comp.getRedefinedCount(); cnt > 0; --cnt) {
            seed = "Original" + seed;
        }
        return seed;
    }
}
