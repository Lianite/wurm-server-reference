// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.tools.xjc.api.impl.s2j;

import com.sun.tools.xjc.model.TypeUse;
import com.sun.tools.xjc.api.TypeAndAnnotation;
import java.util.Collection;
import com.sun.tools.xjc.outline.PackageOutline;
import java.util.ArrayList;
import com.sun.codemodel.JClass;
import java.util.List;
import com.sun.codemodel.JCodeModel;
import com.sun.tools.xjc.api.ErrorListener;
import com.sun.tools.xjc.Plugin;
import java.util.Iterator;
import com.sun.tools.xjc.model.CElementInfo;
import com.sun.tools.xjc.model.nav.NClass;
import com.sun.tools.xjc.model.CClassInfo;
import java.util.HashMap;
import com.sun.tools.xjc.api.Mapping;
import javax.xml.namespace.QName;
import java.util.Map;
import com.sun.tools.xjc.model.Model;
import com.sun.tools.xjc.outline.Outline;
import com.sun.tools.xjc.api.S2JJAXBModel;

final class JAXBModelImpl implements S2JJAXBModel
{
    final Outline outline;
    private final Model model;
    private final Map<QName, Mapping> byXmlName;
    
    JAXBModelImpl(final Outline outline) {
        this.byXmlName = new HashMap<QName, Mapping>();
        this.model = outline.getModel();
        this.outline = outline;
        for (final CClassInfo ci : this.model.beans().values()) {
            if (!ci.isElement()) {
                continue;
            }
            this.byXmlName.put(ci.getElementName(), new BeanMappingImpl(this, ci));
        }
        for (final CElementInfo ei : this.model.getElementMappings((NClass)null).values()) {
            this.byXmlName.put(ei.getElementName(), new ElementMappingImpl(this, ei));
        }
    }
    
    public JCodeModel generateCode(final Plugin[] extensions, final ErrorListener errorListener) {
        return this.outline.getCodeModel();
    }
    
    public List<JClass> getAllObjectFactories() {
        final List<JClass> r = new ArrayList<JClass>();
        for (final PackageOutline pkg : this.outline.getAllPackageContexts()) {
            r.add(pkg.objectFactory());
        }
        return r;
    }
    
    public final Mapping get(final QName elementName) {
        return this.byXmlName.get(elementName);
    }
    
    public final Collection<? extends Mapping> getMappings() {
        return this.byXmlName.values();
    }
    
    public TypeAndAnnotation getJavaType(final QName xmlTypeName) {
        final TypeUse use = this.model.typeUses().get(xmlTypeName);
        if (use == null) {
            return null;
        }
        return new TypeAndAnnotationImpl(this.outline, use);
    }
    
    public final List<String> getClassList() {
        final List<String> classList = new ArrayList<String>();
        for (final PackageOutline p : this.outline.getAllPackageContexts()) {
            classList.add(p.objectFactory().fullName());
        }
        return classList;
    }
}
