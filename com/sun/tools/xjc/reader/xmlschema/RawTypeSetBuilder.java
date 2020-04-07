// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.tools.xjc.reader.xmlschema;

import com.sun.tools.xjc.model.CCustomizations;
import com.sun.tools.xjc.model.CClassInfoParent;
import com.sun.tools.xjc.reader.xmlschema.bindinfo.BIGlobalBinding;
import com.sun.tools.xjc.model.CClassInfo;
import com.sun.tools.xjc.model.Model;
import com.sun.tools.xjc.model.TypeUse;
import javax.activation.MimeType;
import com.sun.tools.xjc.reader.xmlschema.bindinfo.BIXSubstitutable;
import com.sun.tools.xjc.model.CAdapter;
import com.sun.tools.xjc.model.CNonElement;
import com.sun.xml.bind.v2.model.core.ID;
import com.sun.tools.xjc.model.CReferencePropertyInfo;
import com.sun.tools.xjc.model.CTypeRef;
import com.sun.tools.xjc.model.CElementPropertyInfo;
import com.sun.tools.xjc.model.CElement;
import com.sun.tools.xjc.model.CElementInfo;
import com.sun.tools.xjc.model.CClass;
import com.sun.xml.xsom.XSComponent;
import com.sun.xml.xsom.XSDeclaration;
import com.sun.xml.xsom.XSElementDecl;
import com.sun.xml.xsom.XSModelGroup;
import com.sun.xml.xsom.XSModelGroupDecl;
import com.sun.xml.xsom.XSWildcard;
import com.sun.tools.xjc.reader.xmlschema.bindinfo.BIDom;
import com.sun.xml.bind.v2.model.core.WildcardMode;
import com.sun.tools.xjc.reader.Ring;
import java.util.HashSet;
import com.sun.tools.xjc.model.Multiplicity;
import com.sun.xml.xsom.XSParticle;
import com.sun.tools.xjc.reader.RawTypeSet;
import javax.xml.namespace.QName;
import java.util.Set;
import com.sun.xml.xsom.visitor.XSTermVisitor;

public class RawTypeSetBuilder implements XSTermVisitor
{
    private final Set<QName> elementNames;
    private final Set<RawTypeSet.Ref> refs;
    protected final BGMBuilder builder;
    
    public static RawTypeSet build(final XSParticle p, final boolean optional) {
        final RawTypeSetBuilder rtsb = new RawTypeSetBuilder();
        rtsb.particle(p);
        Multiplicity mul = MultiplicityCounter.theInstance.particle(p);
        if (optional) {
            mul = mul.makeOptional();
        }
        return new RawTypeSet(rtsb.refs, mul);
    }
    
    public RawTypeSetBuilder() {
        this.elementNames = new HashSet<QName>();
        this.refs = new HashSet<RawTypeSet.Ref>();
        this.builder = Ring.get(BGMBuilder.class);
    }
    
    public Set<RawTypeSet.Ref> getRefs() {
        return this.refs;
    }
    
    private void particle(final XSParticle p) {
        final BIDom dom = this.builder.getLocalDomCustomization(p);
        if (dom != null) {
            dom.markAsAcknowledged();
            this.refs.add(new WildcardRef(WildcardMode.SKIP));
        }
        else {
            p.getTerm().visit(this);
        }
    }
    
    public void wildcard(final XSWildcard wc) {
        this.refs.add(new WildcardRef(wc));
    }
    
    public void modelGroupDecl(final XSModelGroupDecl decl) {
        this.modelGroup(decl.getModelGroup());
    }
    
    public void modelGroup(final XSModelGroup group) {
        for (final XSParticle p : group.getChildren()) {
            this.particle(p);
        }
    }
    
    public void elementDecl(final XSElementDecl decl) {
        final QName n = BGMBuilder.getName(decl);
        if (this.elementNames.add(n)) {
            final CElement elementBean = Ring.get(ClassSelector.class).bindToType(decl, null);
            if (elementBean == null) {
                this.refs.add(new XmlTypeRef(decl));
            }
            else if (elementBean instanceof CClass) {
                this.refs.add(new CClassRef(decl, (CClass)elementBean));
            }
            else {
                this.refs.add(new CElementInfoRef(decl, (CElementInfo)elementBean));
            }
        }
    }
    
    public static final class WildcardRef extends RawTypeSet.Ref
    {
        private final WildcardMode mode;
        
        WildcardRef(final XSWildcard wildcard) {
            this.mode = getMode(wildcard);
        }
        
        WildcardRef(final WildcardMode mode) {
            this.mode = mode;
        }
        
        private static WildcardMode getMode(final XSWildcard wildcard) {
            switch (wildcard.getMode()) {
                case 1: {
                    return WildcardMode.LAX;
                }
                case 2: {
                    return WildcardMode.STRICT;
                }
                case 3: {
                    return WildcardMode.SKIP;
                }
                default: {
                    throw new IllegalStateException();
                }
            }
        }
        
        protected CTypeRef toTypeRef(final CElementPropertyInfo ep) {
            throw new IllegalStateException();
        }
        
        protected void toElementRef(final CReferencePropertyInfo prop) {
            prop.setWildcard(this.mode);
        }
        
        protected RawTypeSet.Mode canBeType(final RawTypeSet parent) {
            return RawTypeSet.Mode.MUST_BE_REFERENCE;
        }
        
        protected boolean isListOfValues() {
            return false;
        }
        
        protected ID id() {
            return ID.NONE;
        }
    }
    
    public static final class CClassRef extends RawTypeSet.Ref
    {
        public final CClass target;
        public final XSElementDecl decl;
        
        CClassRef(final XSElementDecl decl, final CClass target) {
            this.decl = decl;
            this.target = target;
        }
        
        protected CTypeRef toTypeRef(final CElementPropertyInfo ep) {
            return new CTypeRef(this.target, this.decl);
        }
        
        protected void toElementRef(final CReferencePropertyInfo prop) {
            prop.getElements().add(this.target);
        }
        
        protected RawTypeSet.Mode canBeType(final RawTypeSet parent) {
            if (this.decl.getSubstitutables().size() > 1) {
                return RawTypeSet.Mode.MUST_BE_REFERENCE;
            }
            return RawTypeSet.Mode.SHOULD_BE_TYPEREF;
        }
        
        protected boolean isListOfValues() {
            return false;
        }
        
        protected ID id() {
            return ID.NONE;
        }
    }
    
    public final class CElementInfoRef extends RawTypeSet.Ref
    {
        public final CElementInfo target;
        public final XSElementDecl decl;
        
        CElementInfoRef(final XSElementDecl decl, final CElementInfo target) {
            this.decl = decl;
            this.target = target;
        }
        
        protected CTypeRef toTypeRef(final CElementPropertyInfo ep) {
            assert !this.target.isCollection();
            final CAdapter a = this.target.getProperty().getAdapter();
            if (a != null && ep != null) {
                ep.setAdapter(a);
            }
            return new CTypeRef(this.target.getContentType(), this.decl);
        }
        
        protected void toElementRef(final CReferencePropertyInfo prop) {
            prop.getElements().add(this.target);
        }
        
        protected RawTypeSet.Mode canBeType(final RawTypeSet parent) {
            if (this.decl.getSubstitutables().size() > 1) {
                return RawTypeSet.Mode.MUST_BE_REFERENCE;
            }
            final BIXSubstitutable subst = RawTypeSetBuilder.this.builder.getBindInfo(this.decl).get(BIXSubstitutable.class);
            if (subst != null) {
                subst.markAsAcknowledged();
                return RawTypeSet.Mode.MUST_BE_REFERENCE;
            }
            final CElementPropertyInfo p = this.target.getProperty();
            if ((parent.refs.size() > 1 || !parent.mul.isAtMostOnce()) && p.id() != ID.NONE) {
                return RawTypeSet.Mode.MUST_BE_REFERENCE;
            }
            if (parent.refs.size() > 1 && p.getAdapter() != null) {
                return RawTypeSet.Mode.MUST_BE_REFERENCE;
            }
            return RawTypeSet.Mode.SHOULD_BE_TYPEREF;
        }
        
        protected boolean isListOfValues() {
            return this.target.getProperty().isValueList();
        }
        
        protected ID id() {
            return this.target.getProperty().id();
        }
        
        protected MimeType getExpectedMimeType() {
            return this.target.getProperty().getExpectedMimeType();
        }
    }
    
    public static final class XmlTypeRef extends RawTypeSet.Ref
    {
        private final XSElementDecl decl;
        private final TypeUse target;
        
        public XmlTypeRef(final XSElementDecl decl) {
            this.decl = decl;
            final SimpleTypeBuilder stb = Ring.get(SimpleTypeBuilder.class);
            stb.refererStack.push(decl);
            final TypeUse r = Ring.get(ClassSelector.class).bindToType(decl.getType(), decl);
            stb.refererStack.pop();
            this.target = r;
        }
        
        protected CTypeRef toTypeRef(final CElementPropertyInfo ep) {
            if (ep != null && this.target.getAdapterUse() != null) {
                ep.setAdapter(this.target.getAdapterUse());
            }
            return new CTypeRef(this.target.getInfo(), this.decl);
        }
        
        protected void toElementRef(final CReferencePropertyInfo prop) {
            final CClassInfo scope = Ring.get(ClassSelector.class).getCurrentBean();
            final Model model = Ring.get(Model.class);
            final CCustomizations custs = Ring.get(BGMBuilder.class).getBindInfo(this.decl).toCustomizationList();
            if (this.target instanceof CClassInfo && Ring.get(BIGlobalBinding.class).isSimpleMode()) {
                final CClassInfo bean = new CClassInfo(model, scope, model.getNameConverter().toClassName(this.decl.getName()), this.decl.getLocator(), null, BGMBuilder.getName(this.decl), this.decl, custs);
                bean.setBaseClass((CClass)this.target);
                prop.getElements().add(bean);
            }
            else {
                final CElementInfo e = new CElementInfo(model, BGMBuilder.getName(this.decl), scope, this.target, this.decl.getDefaultValue(), this.decl, custs, this.decl.getLocator());
                prop.getElements().add(e);
            }
        }
        
        protected RawTypeSet.Mode canBeType(final RawTypeSet parent) {
            if ((parent.refs.size() > 1 || !parent.mul.isAtMostOnce()) && this.target.idUse() != ID.NONE) {
                return RawTypeSet.Mode.MUST_BE_REFERENCE;
            }
            if (parent.refs.size() > 1 && this.target.getAdapterUse() != null) {
                return RawTypeSet.Mode.MUST_BE_REFERENCE;
            }
            if (this.decl.isNillable() && parent.mul.isOptional()) {
                return RawTypeSet.Mode.CAN_BE_TYPEREF;
            }
            return RawTypeSet.Mode.SHOULD_BE_TYPEREF;
        }
        
        protected boolean isListOfValues() {
            return this.target.isCollection();
        }
        
        protected ID id() {
            return this.target.idUse();
        }
        
        protected MimeType getExpectedMimeType() {
            return this.target.getExpectedMimeType();
        }
    }
}
