// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.tools.xjc.api.impl.s2j;

import com.sun.tools.xjc.model.CTypeInfo;
import com.sun.tools.xjc.model.CElementPropertyInfo;
import com.sun.tools.xjc.model.CClassInfo;
import com.sun.tools.xjc.model.CNonElement;
import com.sun.tools.xjc.api.Property;
import java.util.List;
import com.sun.tools.xjc.model.CAdapter;
import com.sun.tools.xjc.model.TypeUse;
import com.sun.tools.xjc.model.TypeUseFactory;
import com.sun.tools.xjc.api.TypeAndAnnotation;
import com.sun.tools.xjc.model.CElementInfo;

final class ElementMappingImpl extends AbstractMappingImpl<CElementInfo>
{
    private final TypeAndAnnotation taa;
    
    protected ElementMappingImpl(final JAXBModelImpl parent, final CElementInfo elementInfo) {
        super(parent, elementInfo);
        TypeUse t = ((CElementInfo)this.clazz).getContentType();
        if (((CElementInfo)this.clazz).getProperty().isCollection()) {
            t = TypeUseFactory.makeCollection(t);
        }
        final CAdapter a = ((CElementInfo)this.clazz).getProperty().getAdapter();
        if (a != null) {
            t = TypeUseFactory.adapt(t, a);
        }
        this.taa = new TypeAndAnnotationImpl(parent.outline, t);
    }
    
    public TypeAndAnnotation getType() {
        return this.taa;
    }
    
    public final List<Property> calcDrilldown() {
        final CElementPropertyInfo p = ((CElementInfo)this.clazz).getProperty();
        if (p.getAdapter() != null) {
            return null;
        }
        if (p.isCollection()) {
            return null;
        }
        final CTypeInfo typeClass = p.ref().get(0);
        if (!(typeClass instanceof CClassInfo)) {
            return null;
        }
        final CClassInfo ci = (CClassInfo)typeClass;
        if (ci.isAbstract()) {
            return null;
        }
        if (!ci.isOrdered()) {
            return null;
        }
        return this.buildDrilldown(ci);
    }
}
