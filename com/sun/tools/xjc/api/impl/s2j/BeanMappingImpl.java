// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.tools.xjc.api.impl.s2j;

import com.sun.tools.xjc.api.Property;
import java.util.List;
import com.sun.tools.xjc.api.TypeAndAnnotation;
import com.sun.tools.xjc.model.TypeUse;
import com.sun.tools.xjc.model.CClassInfo;

final class BeanMappingImpl extends AbstractMappingImpl<CClassInfo>
{
    private final TypeAndAnnotationImpl taa;
    
    BeanMappingImpl(final JAXBModelImpl parent, final CClassInfo classInfo) {
        super(parent, classInfo);
        this.taa = new TypeAndAnnotationImpl(this.parent.outline, (TypeUse)this.clazz);
        assert classInfo.isElement();
    }
    
    public TypeAndAnnotation getType() {
        return this.taa;
    }
    
    public final String getTypeClass() {
        return this.getClazz();
    }
    
    public List<Property> calcDrilldown() {
        if (!((CClassInfo)this.clazz).isOrdered()) {
            return null;
        }
        return this.buildDrilldown((CClassInfo)this.clazz);
    }
}
