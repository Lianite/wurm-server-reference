// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.tools.xjc.model;

import com.sun.xml.bind.v2.model.core.PropertyInfo;
import com.sun.codemodel.JType;
import com.sun.tools.xjc.outline.Aspect;
import com.sun.tools.xjc.outline.Outline;
import java.util.Collection;
import java.util.HashSet;
import com.sun.xml.bind.v2.model.core.Element;
import java.util.Iterator;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlID;
import com.sun.codemodel.JCodeModel;
import java.util.ArrayList;
import com.sun.xml.xsom.XSComponent;
import org.xml.sax.Locator;
import com.sun.codemodel.JPackage;
import com.sun.codemodel.JClass;
import java.util.Set;
import java.util.List;
import com.sun.istack.Nullable;
import javax.xml.namespace.QName;
import javax.xml.bind.annotation.XmlIDREF;
import com.sun.tools.xjc.model.nav.NClass;
import com.sun.tools.xjc.model.nav.NType;
import com.sun.xml.bind.v2.model.core.ClassInfo;

public final class CClassInfo extends AbstractCElement implements ClassInfo<NType, NClass>, CClassInfoParent, CClass, NClass
{
    @XmlIDREF
    private CClass baseClass;
    private CClassInfo firstSubclass;
    private CClassInfo nextSibling;
    private final QName typeName;
    @Nullable
    private final QName elementName;
    private boolean isOrdered;
    private final List<CPropertyInfo> properties;
    public String javadoc;
    @XmlIDREF
    private final CClassInfoParent parent;
    public final String shortName;
    @Nullable
    private String implClass;
    public final Model model;
    private boolean hasAttributeWildcard;
    private static final Visitor<String> calcSqueezedName;
    private Set<JClass> _implements;
    private final List<Constructor> constructors;
    
    public CClassInfo(final Model model, final JPackage pkg, final String shortName, final Locator location, final QName typeName, final QName elementName, final XSComponent source, final CCustomizations customizations) {
        this(model, model.getPackage(pkg), shortName, location, typeName, elementName, source, customizations);
    }
    
    public CClassInfo(final Model model, final CClassInfoParent p, final String shortName, final Locator location, final QName typeName, final QName elementName, final XSComponent source, final CCustomizations customizations) {
        super(model, source, location, customizations);
        this.nextSibling = null;
        this.isOrdered = true;
        this.properties = new ArrayList<CPropertyInfo>();
        this._implements = null;
        this.constructors = new ArrayList<Constructor>(1);
        this.model = model;
        this.parent = p;
        this.shortName = model.allocator.assignClassName(this.parent, shortName);
        this.typeName = typeName;
        this.elementName = elementName;
        model.add(this);
    }
    
    public CClassInfo(final Model model, final JCodeModel cm, final String fullName, final Locator location, final QName typeName, final QName elementName, final XSComponent source, final CCustomizations customizations) {
        super(model, source, location, customizations);
        this.nextSibling = null;
        this.isOrdered = true;
        this.properties = new ArrayList<CPropertyInfo>();
        this._implements = null;
        this.constructors = new ArrayList<Constructor>(1);
        this.model = model;
        final int idx = fullName.indexOf(46);
        if (idx < 0) {
            this.parent = model.getPackage(cm.rootPackage());
            this.shortName = model.allocator.assignClassName(this.parent, fullName);
        }
        else {
            this.parent = model.getPackage(cm._package(fullName.substring(0, idx)));
            this.shortName = model.allocator.assignClassName(this.parent, fullName.substring(idx + 1));
        }
        this.typeName = typeName;
        this.elementName = elementName;
        model.add(this);
    }
    
    public boolean hasAttributeWildcard() {
        return this.hasAttributeWildcard;
    }
    
    public void hasAttributeWildcard(final boolean hasAttributeWildcard) {
        this.hasAttributeWildcard = hasAttributeWildcard;
    }
    
    public boolean hasSubClasses() {
        return this.firstSubclass != null;
    }
    
    public boolean declaresAttributeWildcard() {
        return this.hasAttributeWildcard && !this.inheritsAttributeWildcard();
    }
    
    public boolean inheritsAttributeWildcard() {
        for (CClassInfo c = this.getBaseClass(); c != null; c = c.getBaseClass()) {
            if (c.hasAttributeWildcard) {
                return true;
            }
        }
        return false;
    }
    
    public NClass getClazz() {
        return this;
    }
    
    public CClassInfo getScope() {
        return null;
    }
    
    @XmlID
    public String getName() {
        return this.fullName();
    }
    
    @XmlElement
    public String getSqueezedName() {
        return CClassInfo.calcSqueezedName.onBean(this);
    }
    
    public List<CPropertyInfo> getProperties() {
        return this.properties;
    }
    
    public boolean hasValueProperty() {
        throw new UnsupportedOperationException();
    }
    
    public CPropertyInfo getProperty(final String name) {
        for (final CPropertyInfo p : this.properties) {
            if (p.getName(false).equals(name)) {
                return p;
            }
        }
        return null;
    }
    
    public boolean hasProperties() {
        return !this.getProperties().isEmpty();
    }
    
    public boolean isElement() {
        return this.elementName != null;
    }
    
    @Deprecated
    public CNonElement getInfo() {
        return this;
    }
    
    public Element<NType, NClass> asElement() {
        if (this.isElement()) {
            return this;
        }
        return null;
    }
    
    public boolean isOrdered() {
        return this.isOrdered;
    }
    
    public boolean isFinal() {
        return false;
    }
    
    public void setOrdered(final boolean value) {
        this.isOrdered = value;
    }
    
    public QName getElementName() {
        return this.elementName;
    }
    
    public QName getTypeName() {
        return this.typeName;
    }
    
    public boolean isSimpleType() {
        throw new UnsupportedOperationException();
    }
    
    public String fullName() {
        final String r = this.parent.fullName();
        if (r.length() == 0) {
            return this.shortName;
        }
        return r + '.' + this.shortName;
    }
    
    public CClassInfoParent parent() {
        return this.parent;
    }
    
    public void setUserSpecifiedImplClass(final String implClass) {
        assert this.implClass == null;
        assert implClass != null;
        this.implClass = implClass;
    }
    
    public String getUserSpecifiedImplClass() {
        return this.implClass;
    }
    
    public void addProperty(final CPropertyInfo prop) {
        if (prop.ref().isEmpty()) {
            return;
        }
        prop.setParent(this);
        this.properties.add(prop);
    }
    
    public void setBaseClass(final CClass base) {
        assert this.baseClass == null;
        assert base != null;
        this.baseClass = base;
        assert this.nextSibling == null;
        if (base instanceof CClassInfo) {
            final CClassInfo realBase = (CClassInfo)base;
            this.nextSibling = realBase.firstSubclass;
            realBase.firstSubclass = this;
        }
    }
    
    public CClassInfo getBaseClass() {
        if (this.baseClass instanceof CClassInfo) {
            return (CClassInfo)this.baseClass;
        }
        return null;
    }
    
    public CClassRef getRefBaseClass() {
        if (this.baseClass instanceof CClassRef) {
            return (CClassRef)this.baseClass;
        }
        return null;
    }
    
    public Iterator<CClassInfo> listSubclasses() {
        return new Iterator<CClassInfo>() {
            CClassInfo cur = CClassInfo.this.firstSubclass;
            
            public boolean hasNext() {
                return this.cur != null;
            }
            
            public CClassInfo next() {
                final CClassInfo r = this.cur;
                this.cur = this.cur.nextSibling;
                return r;
            }
            
            public void remove() {
                throw new UnsupportedOperationException();
            }
        };
    }
    
    public CClassInfo getSubstitutionHead() {
        CClassInfo c;
        for (c = this.getBaseClass(); c != null && !c.isElement(); c = c.getBaseClass()) {}
        return c;
    }
    
    public void _implements(final JClass c) {
        if (this._implements == null) {
            this._implements = new HashSet<JClass>();
        }
        this._implements.add(c);
    }
    
    public void addConstructor(final String... fieldNames) {
        this.constructors.add(new Constructor(fieldNames));
    }
    
    public Collection<? extends Constructor> getConstructors() {
        return this.constructors;
    }
    
    public final <T> T accept(final Visitor<T> visitor) {
        return visitor.onBean(this);
    }
    
    public JPackage getOwnerPackage() {
        return this.parent.getOwnerPackage();
    }
    
    public final NClass getType() {
        return this;
    }
    
    public final JClass toType(final Outline o, final Aspect aspect) {
        switch (aspect) {
            case IMPLEMENTATION: {
                return o.getClazz(this).implRef;
            }
            case EXPOSED: {
                return o.getClazz(this).ref;
            }
            default: {
                throw new IllegalStateException();
            }
        }
    }
    
    public boolean isBoxedType() {
        return false;
    }
    
    public String toString() {
        return this.fullName();
    }
    
    static {
        calcSqueezedName = new Visitor<String>() {
            public String onBean(final CClassInfo bean) {
                return bean.parent.accept((Visitor<String>)this) + bean.shortName;
            }
            
            public String onElement(final CElementInfo element) {
                return element.parent.accept((Visitor<String>)this) + element.shortName();
            }
            
            public String onPackage(final JPackage pkg) {
                return "";
            }
        };
    }
}
