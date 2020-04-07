// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.tools.xjc.model;

import com.sun.xml.bind.v2.model.core.TypeInfo;
import com.sun.xml.bind.v2.model.core.NonElement;
import org.xml.sax.Locator;
import com.sun.codemodel.JType;
import com.sun.tools.xjc.outline.Aspect;
import com.sun.tools.xjc.outline.Outline;
import com.sun.xml.bind.v2.model.impl.ArrayInfoImpl;
import com.sun.xml.xsom.XSComponent;
import javax.xml.namespace.QName;
import com.sun.tools.xjc.model.nav.NClass;
import com.sun.tools.xjc.model.nav.NType;
import com.sun.xml.bind.v2.model.core.ArrayInfo;

public final class CArrayInfo extends AbstractCTypeInfoImpl implements ArrayInfo<NType, NClass>, CNonElement, NType
{
    private final CNonElement itemType;
    private final QName typeName;
    
    public CArrayInfo(final Model model, final CNonElement itemType, final XSComponent source, final CCustomizations customizations) {
        super(model, source, customizations);
        this.itemType = itemType;
        assert itemType.getTypeName() != null;
        this.typeName = ArrayInfoImpl.calcArrayTypeName(itemType.getTypeName());
    }
    
    public CNonElement getItemType() {
        return this.itemType;
    }
    
    public QName getTypeName() {
        return this.typeName;
    }
    
    public boolean isSimpleType() {
        return false;
    }
    
    @Deprecated
    public CNonElement getInfo() {
        return this;
    }
    
    public JType toType(final Outline o, final Aspect aspect) {
        return this.itemType.toType(o, aspect).array();
    }
    
    public NType getType() {
        return this;
    }
    
    public boolean isBoxedType() {
        return false;
    }
    
    public String fullName() {
        return ((TypeInfo<NType, C>)this.itemType).getType().fullName() + "[]";
    }
    
    public Locator getLocator() {
        return Model.EMPTY_LOCATOR;
    }
}
