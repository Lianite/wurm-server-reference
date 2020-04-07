// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.tools.xjc.api.impl.s2j;

import com.sun.xml.bind.v2.model.core.TypeInfo;
import java.util.Collection;
import java.util.Iterator;
import com.sun.tools.xjc.outline.FieldOutline;
import com.sun.tools.xjc.model.CElementInfo;
import com.sun.xml.bind.v2.model.core.ClassInfo;
import com.sun.tools.xjc.model.CReferencePropertyInfo;
import com.sun.xml.bind.v2.model.core.ReferencePropertyInfo;
import com.sun.tools.xjc.model.CTypeRef;
import com.sun.tools.xjc.model.CElementPropertyInfo;
import com.sun.tools.xjc.model.CPropertyInfo;
import java.util.ArrayList;
import com.sun.tools.xjc.model.CClassInfo;
import com.sun.tools.xjc.model.nav.NType;
import javax.xml.namespace.QName;
import com.sun.tools.xjc.api.Property;
import java.util.List;
import com.sun.tools.xjc.api.Mapping;
import com.sun.tools.xjc.model.CElement;

abstract class AbstractMappingImpl<InfoT extends CElement> implements Mapping
{
    protected final JAXBModelImpl parent;
    protected final InfoT clazz;
    private List<Property> drilldown;
    private boolean drilldownComputed;
    
    protected AbstractMappingImpl(final JAXBModelImpl parent, final InfoT clazz) {
        this.drilldown = null;
        this.drilldownComputed = false;
        this.parent = parent;
        this.clazz = clazz;
    }
    
    public final QName getElement() {
        return this.clazz.getElementName();
    }
    
    public final String getClazz() {
        return ((TypeInfo<NType, C>)this.clazz).getType().fullName();
    }
    
    public final List<? extends Property> getWrapperStyleDrilldown() {
        if (!this.drilldownComputed) {
            this.drilldownComputed = true;
            this.drilldown = this.calcDrilldown();
        }
        return this.drilldown;
    }
    
    protected abstract List<Property> calcDrilldown();
    
    protected List<Property> buildDrilldown(final CClassInfo typeBean) {
        final CClassInfo bc = typeBean.getBaseClass();
        List<Property> result;
        if (bc != null) {
            result = this.buildDrilldown(bc);
            if (result == null) {
                return null;
            }
        }
        else {
            result = new ArrayList<Property>();
        }
        for (final CPropertyInfo p : typeBean.getProperties()) {
            if (p instanceof CElementPropertyInfo) {
                final CElementPropertyInfo ep = (CElementPropertyInfo)p;
                final List<? extends CTypeRef> ref = ep.getTypes();
                if (ref.size() != 1) {
                    return null;
                }
                result.add(this.createPropertyImpl(ep, ((CTypeRef)ref.get(0)).getTagName()));
            }
            else {
                if (!(p instanceof ReferencePropertyInfo)) {
                    return null;
                }
                final CReferencePropertyInfo rp = (CReferencePropertyInfo)p;
                final Collection<CElement> elements = rp.getElements();
                if (elements.size() != 1) {
                    return null;
                }
                final CElement ref2 = elements.iterator().next();
                if (ref2 instanceof ClassInfo) {
                    result.add(this.createPropertyImpl(rp, ref2.getElementName()));
                }
                else {
                    final CElementInfo eref = (CElementInfo)ref2;
                    if (!eref.getSubstitutionMembers().isEmpty()) {
                        return null;
                    }
                    ElementAdapter fr;
                    if (rp.isCollection()) {
                        fr = new ElementCollectionAdapter(this.parent.outline.getField(rp), eref);
                    }
                    else {
                        fr = new ElementSingleAdapter(this.parent.outline.getField(rp), eref);
                    }
                    result.add(new PropertyImpl(this, fr, eref.getElementName()));
                }
            }
        }
        return result;
    }
    
    private Property createPropertyImpl(final CPropertyInfo p, final QName tagName) {
        return new PropertyImpl(this, this.parent.outline.getField(p), tagName);
    }
}
