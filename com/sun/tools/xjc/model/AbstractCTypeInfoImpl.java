// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.tools.xjc.model;

import com.sun.xml.bind.v2.runtime.Location;
import com.sun.xml.bind.v2.model.annotation.Locatable;
import com.sun.codemodel.JExpression;
import com.sun.xml.xsom.XmlString;
import com.sun.tools.xjc.outline.Outline;
import javax.activation.MimeType;
import com.sun.xml.bind.v2.model.core.ID;
import com.sun.xml.xsom.XSComponent;

abstract class AbstractCTypeInfoImpl implements CTypeInfo
{
    private final CCustomizations customizations;
    private final XSComponent source;
    
    protected AbstractCTypeInfoImpl(final Model model, final XSComponent source, CCustomizations customizations) {
        if (customizations == null) {
            customizations = CCustomizations.EMPTY;
        }
        else {
            customizations.setParent(model, this);
        }
        this.customizations = customizations;
        this.source = source;
    }
    
    public final boolean isCollection() {
        return false;
    }
    
    public final CAdapter getAdapterUse() {
        return null;
    }
    
    public final ID idUse() {
        return ID.NONE;
    }
    
    public final XSComponent getSchemaComponent() {
        return this.source;
    }
    
    public final boolean canBeReferencedByIDREF() {
        throw new UnsupportedOperationException();
    }
    
    public MimeType getExpectedMimeType() {
        return null;
    }
    
    public CCustomizations getCustomizations() {
        return this.customizations;
    }
    
    public JExpression createConstant(final Outline outline, final XmlString lexical) {
        return null;
    }
    
    public final Locatable getUpstream() {
        throw new UnsupportedOperationException();
    }
    
    public final Location getLocation() {
        throw new UnsupportedOperationException();
    }
}
