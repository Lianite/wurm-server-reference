// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.tools.xjc.model;

import com.sun.codemodel.JType;
import com.sun.xml.bind.v2.runtime.Location;
import com.sun.xml.bind.v2.model.annotation.Locatable;
import com.sun.xml.bind.v2.model.core.NonElement;
import javax.activation.MimeType;
import com.sun.xml.bind.v2.model.core.ID;
import com.sun.codemodel.JExpression;
import com.sun.xml.xsom.XmlString;
import com.sun.codemodel.JClass;
import com.sun.tools.xjc.outline.Aspect;
import com.sun.tools.xjc.outline.Outline;
import com.sun.xml.bind.v2.model.core.Element;
import java.util.Iterator;
import org.xml.sax.Locator;
import java.util.Collection;
import com.sun.xml.xsom.XSComponent;
import javax.xml.namespace.QName;
import com.sun.tools.xjc.model.nav.NClass;
import com.sun.tools.xjc.model.nav.NType;
import com.sun.xml.bind.v2.model.core.EnumLeafInfo;

public final class CEnumLeafInfo implements EnumLeafInfo<NType, NClass>, NClass, CNonElement
{
    public final Model model;
    public final CClassInfoParent parent;
    public final String shortName;
    private final QName typeName;
    private final XSComponent source;
    public final CNonElement base;
    public final Collection<CEnumConstant> members;
    private final CCustomizations customizations;
    private final Locator sourceLocator;
    public String javadoc;
    
    public CEnumLeafInfo(final Model model, final QName typeName, final CClassInfoParent container, final String shortName, final CNonElement base, final Collection<CEnumConstant> _members, final XSComponent source, CCustomizations customizations, final Locator _sourceLocator) {
        this.model = model;
        this.parent = container;
        this.shortName = model.allocator.assignClassName(this.parent, shortName);
        this.base = base;
        this.members = _members;
        this.source = source;
        if (customizations == null) {
            customizations = CCustomizations.EMPTY;
        }
        this.customizations = customizations;
        this.sourceLocator = _sourceLocator;
        this.typeName = typeName;
        for (final CEnumConstant mem : this.members) {
            mem.setParent(this);
        }
        model.add(this);
    }
    
    public Locator getLocator() {
        return this.sourceLocator;
    }
    
    public QName getTypeName() {
        return this.typeName;
    }
    
    public NType getType() {
        return this;
    }
    
    public boolean canBeReferencedByIDREF() {
        return false;
    }
    
    public boolean isElement() {
        return false;
    }
    
    public QName getElementName() {
        return null;
    }
    
    public Element<NType, NClass> asElement() {
        return null;
    }
    
    public NClass getClazz() {
        return this;
    }
    
    public XSComponent getSchemaComponent() {
        return this.source;
    }
    
    public JClass toType(final Outline o, final Aspect aspect) {
        return o.getEnum(this).clazz;
    }
    
    public boolean isAbstract() {
        return false;
    }
    
    public boolean isBoxedType() {
        return false;
    }
    
    public String fullName() {
        return this.parent.fullName() + '.' + this.shortName;
    }
    
    public boolean isPrimitive() {
        return false;
    }
    
    public boolean isSimpleType() {
        return true;
    }
    
    public boolean needsValueField() {
        for (final CEnumConstant cec : this.members) {
            if (!cec.getName().equals(cec.getLexicalValue())) {
                return true;
            }
        }
        return false;
    }
    
    public JExpression createConstant(final Outline outline, final XmlString literal) {
        final JClass type = this.toType(outline, Aspect.EXPOSED);
        for (final CEnumConstant mem : this.members) {
            if (mem.getLexicalValue().equals(literal.value)) {
                return type.staticRef(mem.getName());
            }
        }
        return null;
    }
    
    @Deprecated
    public boolean isCollection() {
        return false;
    }
    
    @Deprecated
    public CAdapter getAdapterUse() {
        return null;
    }
    
    @Deprecated
    public CNonElement getInfo() {
        return this;
    }
    
    public ID idUse() {
        return ID.NONE;
    }
    
    public MimeType getExpectedMimeType() {
        return null;
    }
    
    public Collection<CEnumConstant> getConstants() {
        return this.members;
    }
    
    public NonElement<NType, NClass> getBaseType() {
        return this.base;
    }
    
    public CCustomizations getCustomizations() {
        return this.customizations;
    }
    
    public Locatable getUpstream() {
        throw new UnsupportedOperationException();
    }
    
    public Location getLocation() {
        throw new UnsupportedOperationException();
    }
}
