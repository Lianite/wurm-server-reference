// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.tools.xjc.model;

import com.sun.xml.bind.v2.model.core.TypeInfo;
import com.sun.xml.bind.v2.model.core.ElementPropertyInfo;
import com.sun.xml.bind.v2.model.core.NonElement;
import com.sun.xml.bind.v2.model.core.Element;
import com.sun.xml.bind.v2.model.core.ClassInfo;
import java.util.HashSet;
import java.util.Collections;
import java.util.Collection;
import javax.xml.bind.annotation.XmlElement;
import com.sun.codemodel.JType;
import com.sun.tools.xjc.outline.Aspect;
import com.sun.tools.xjc.outline.Outline;
import java.util.List;
import com.sun.codemodel.JPackage;
import com.sun.tools.xjc.reader.Ring;
import com.sun.tools.xjc.reader.xmlschema.BGMBuilder;
import com.sun.tools.xjc.reader.xmlschema.bindinfo.BIFactoryMethod;
import javax.xml.bind.JAXBElement;
import com.sun.tools.xjc.model.nav.NavigatorImpl;
import com.sun.tools.xjc.reader.xmlschema.bindinfo.BIInlineBinaryData;
import com.sun.xml.xsom.XSComponent;
import org.xml.sax.Locator;
import com.sun.xml.xsom.XSElementDecl;
import com.sun.xml.xsom.XmlString;
import com.sun.istack.Nullable;
import java.util.Set;
import javax.xml.namespace.QName;
import com.sun.tools.xjc.model.nav.NClass;
import com.sun.tools.xjc.model.nav.NType;
import com.sun.xml.bind.v2.model.core.ElementInfo;

public final class CElementInfo extends AbstractCElement implements ElementInfo<NType, NClass>, NType, CClassInfoParent
{
    private final QName tagName;
    private NType type;
    private String className;
    public final CClassInfoParent parent;
    private CElementInfo substitutionHead;
    private Set<CElementInfo> substitutionMembers;
    private final Model model;
    private CElementPropertyInfo property;
    @Nullable
    private String squeezedName;
    
    public CElementInfo(final Model model, final QName tagName, final CClassInfoParent parent, final TypeUse contentType, final XmlString defaultValue, final XSElementDecl source, final CCustomizations customizations, final Locator location) {
        super(model, source, location, customizations);
        this.tagName = tagName;
        this.model = model;
        this.parent = parent;
        if (contentType != null) {
            this.initContentType(contentType, source, defaultValue);
        }
        model.add(this);
    }
    
    public CElementInfo(final Model model, final QName tagName, final CClassInfoParent parent, final String className, final CCustomizations customizations, final Locator location) {
        this(model, tagName, parent, null, null, null, customizations, location);
        this.className = className;
    }
    
    public void initContentType(final TypeUse contentType, @Nullable final XSElementDecl source, final XmlString defaultValue) {
        assert this.property == null;
        (this.property = new CElementPropertyInfo("Value", contentType.isCollection() ? CElementPropertyInfo.CollectionMode.REPEATED_VALUE : CElementPropertyInfo.CollectionMode.NOT_REPEATED, contentType.idUse(), contentType.getExpectedMimeType(), source, null, this.getLocator(), true)).setAdapter(contentType.getAdapterUse());
        BIInlineBinaryData.handle(source, this.property);
        this.property.getTypes().add(new CTypeRef(contentType.getInfo(), this.tagName, CTypeRef.getSimpleTypeName(source), true, defaultValue));
        this.type = NavigatorImpl.createParameterizedType(NavigatorImpl.theInstance.ref((Class)JAXBElement.class), this.getContentInMemoryType());
        final BIFactoryMethod factoryMethod = Ring.get(BGMBuilder.class).getBindInfo(source).get(BIFactoryMethod.class);
        if (factoryMethod != null) {
            factoryMethod.markAsAcknowledged();
            this.squeezedName = factoryMethod.name;
        }
    }
    
    public final String getDefaultValue() {
        return this.getProperty().getTypes().get(0).getDefaultValue();
    }
    
    public final JPackage _package() {
        return this.parent.getOwnerPackage();
    }
    
    public CNonElement getContentType() {
        return this.getProperty().ref().get(0);
    }
    
    public NType getContentInMemoryType() {
        if (this.getProperty().getAdapter() != null) {
            return (NType)this.getProperty().getAdapter().customType;
        }
        final NType itemType = ((TypeInfo<NType, C>)this.getContentType()).getType();
        if (!this.property.isCollection()) {
            return itemType;
        }
        return NavigatorImpl.createParameterizedType(List.class, itemType);
    }
    
    public CElementPropertyInfo getProperty() {
        return this.property;
    }
    
    public CClassInfo getScope() {
        if (this.parent instanceof CClassInfo) {
            return (CClassInfo)this.parent;
        }
        return null;
    }
    
    public NType getType() {
        return this;
    }
    
    public QName getElementName() {
        return this.tagName;
    }
    
    public JType toType(final Outline o, final Aspect aspect) {
        if (this.className == null) {
            return this.type.toType(o, aspect);
        }
        return o.getElement(this).implClass;
    }
    
    @XmlElement
    public String getSqueezedName() {
        if (this.squeezedName != null) {
            return this.squeezedName;
        }
        final StringBuilder b = new StringBuilder();
        final CClassInfo s = this.getScope();
        if (s != null) {
            b.append(s.getSqueezedName());
        }
        if (this.className != null) {
            b.append(this.className);
        }
        else {
            b.append(this.model.getNameConverter().toClassName(this.tagName.getLocalPart()));
        }
        return b.toString();
    }
    
    public CElementInfo getSubstitutionHead() {
        return this.substitutionHead;
    }
    
    public Collection<CElementInfo> getSubstitutionMembers() {
        if (this.substitutionMembers == null) {
            return (Collection<CElementInfo>)Collections.emptyList();
        }
        return this.substitutionMembers;
    }
    
    public void setSubstitutionHead(final CElementInfo substitutionHead) {
        assert this.substitutionHead == null;
        assert substitutionHead != null;
        this.substitutionHead = substitutionHead;
        if (substitutionHead.substitutionMembers == null) {
            substitutionHead.substitutionMembers = new HashSet<CElementInfo>();
        }
        substitutionHead.substitutionMembers.add(this);
    }
    
    public boolean isBoxedType() {
        return false;
    }
    
    public String fullName() {
        if (this.className == null) {
            return this.type.fullName();
        }
        final String r = this.parent.fullName();
        if (r.length() == 0) {
            return this.className;
        }
        return r + '.' + this.className;
    }
    
    public <T> T accept(final Visitor<T> visitor) {
        return visitor.onElement(this);
    }
    
    public JPackage getOwnerPackage() {
        return this.parent.getOwnerPackage();
    }
    
    public String shortName() {
        return this.className;
    }
    
    public boolean hasClass() {
        return this.className != null;
    }
}
