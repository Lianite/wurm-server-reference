// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.tools.xjc.model;

import com.sun.xml.bind.v2.model.core.PropertyKind;
import javax.xml.namespace.QName;
import org.xml.sax.Locator;
import com.sun.xml.xsom.XSComponent;
import com.sun.tools.xjc.model.nav.NClass;
import com.sun.tools.xjc.model.nav.NType;
import com.sun.xml.bind.v2.model.core.ValuePropertyInfo;

public final class CValuePropertyInfo extends CSingleTypePropertyInfo implements ValuePropertyInfo<NType, NClass>
{
    public CValuePropertyInfo(final String name, final XSComponent source, final CCustomizations customizations, final Locator locator, final TypeUse type, final QName typeName) {
        super(name, type, typeName, source, customizations, locator);
    }
    
    public final PropertyKind kind() {
        return PropertyKind.VALUE;
    }
    
    public <V> V accept(final CPropertyVisitor<V> visitor) {
        return visitor.onValue(this);
    }
}
