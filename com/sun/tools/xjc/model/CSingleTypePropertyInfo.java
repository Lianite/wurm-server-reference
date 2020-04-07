// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.tools.xjc.model;

import com.sun.xml.bind.v2.model.core.Adapter;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import javax.activation.MimeType;
import com.sun.xml.bind.v2.model.core.ID;
import org.xml.sax.Locator;
import com.sun.xml.xsom.XSComponent;
import javax.xml.namespace.QName;

abstract class CSingleTypePropertyInfo extends CPropertyInfo
{
    protected final TypeUse type;
    private final QName schemaType;
    
    protected CSingleTypePropertyInfo(final String name, final TypeUse type, final QName typeName, final XSComponent source, final CCustomizations customizations, final Locator locator) {
        super(name, type.isCollection(), source, customizations, locator);
        this.type = type;
        if (CPropertyInfo.needsExplicitTypeName(type, typeName)) {
            this.schemaType = typeName;
        }
        else {
            this.schemaType = null;
        }
    }
    
    public QName getSchemaType() {
        return this.schemaType;
    }
    
    public final ID id() {
        return this.type.idUse();
    }
    
    public final MimeType getExpectedMimeType() {
        return this.type.getExpectedMimeType();
    }
    
    public final List<? extends CTypeInfo> ref() {
        return Collections.singletonList((CTypeInfo)this.getTarget());
    }
    
    public final CNonElement getTarget() {
        final CNonElement r = this.type.getInfo();
        assert r != null;
        return r;
    }
    
    public final CAdapter getAdapter() {
        return this.type.getAdapterUse();
    }
    
    public final CSingleTypePropertyInfo getSource() {
        return this;
    }
}
