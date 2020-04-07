// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.tools.xjc.model;

import com.sun.xml.bind.v2.model.core.PropertyKind;
import com.sun.istack.Nullable;
import org.xml.sax.Locator;
import com.sun.xml.xsom.XSComponent;
import javax.xml.namespace.QName;
import com.sun.tools.xjc.model.nav.NClass;
import com.sun.tools.xjc.model.nav.NType;
import com.sun.xml.bind.v2.model.core.AttributePropertyInfo;

public final class CAttributePropertyInfo extends CSingleTypePropertyInfo implements AttributePropertyInfo<NType, NClass>
{
    private final QName attName;
    private final boolean isRequired;
    
    public CAttributePropertyInfo(final String name, final XSComponent source, final CCustomizations customizations, final Locator locator, final QName attName, final TypeUse type, @Nullable final QName typeName, final boolean required) {
        super(name, type, typeName, source, customizations, locator);
        this.isRequired = required;
        this.attName = attName;
    }
    
    public boolean isRequired() {
        return this.isRequired;
    }
    
    public QName getXmlName() {
        return this.attName;
    }
    
    public boolean isUnboxable() {
        return this.isRequired && super.isUnboxable();
    }
    
    public boolean isOptionalPrimitive() {
        return !this.isRequired && super.isUnboxable();
    }
    
    public <V> V accept(final CPropertyVisitor<V> visitor) {
        return visitor.onAttribute(this);
    }
    
    public final PropertyKind kind() {
        return PropertyKind.ATTRIBUTE;
    }
}
