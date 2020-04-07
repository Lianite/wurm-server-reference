// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.tools.xjc.api.impl.s2j;

import com.sun.codemodel.JType;
import com.sun.codemodel.JCodeModel;
import com.sun.tools.xjc.api.Mapping;
import javax.xml.namespace.QName;
import com.sun.tools.xjc.outline.FieldOutline;
import com.sun.tools.xjc.api.Property;

public final class PropertyImpl implements Property
{
    protected final FieldOutline fr;
    protected final QName elementName;
    protected final Mapping parent;
    protected final JCodeModel codeModel;
    
    PropertyImpl(final Mapping parent, final FieldOutline fr, final QName elementName) {
        this.parent = parent;
        this.fr = fr;
        this.elementName = elementName;
        this.codeModel = fr.getRawType().owner();
    }
    
    public final String name() {
        return this.fr.getPropertyInfo().getName(false);
    }
    
    public final QName elementName() {
        return this.elementName;
    }
    
    public final JType type() {
        return this.fr.getRawType();
    }
}
