// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.tools.xjc.generator.bean;

import java.util.Map;
import javax.xml.namespace.QName;
import com.sun.tools.xjc.model.CClassInfo;
import com.sun.tools.xjc.outline.Aspect;
import com.sun.tools.xjc.generator.annotation.spec.XmlSchemaWriter;
import com.sun.tools.xjc.model.CPropertyInfo;
import com.sun.tools.xjc.model.CValuePropertyInfo;
import com.sun.tools.xjc.model.CAttributePropertyInfo;
import com.sun.tools.xjc.model.CElement;
import com.sun.tools.xjc.model.CReferencePropertyInfo;
import java.util.Iterator;
import com.sun.tools.xjc.model.CTypeRef;
import com.sun.tools.xjc.model.CElementPropertyInfo;
import com.sun.tools.xjc.model.CPropertyVisitor;
import java.util.Collections;
import java.util.HashSet;
import com.sun.codemodel.JDefinedClass;
import java.util.HashMap;
import javax.xml.bind.annotation.XmlNsForm;
import java.util.Set;
import com.sun.codemodel.JPackage;
import com.sun.tools.xjc.model.Model;
import com.sun.tools.xjc.outline.PackageOutline;

final class PackageOutlineImpl implements PackageOutline
{
    private final Model _model;
    private final JPackage _package;
    private final ObjectFactoryGenerator objectFactoryGenerator;
    final Set<ClassOutlineImpl> classes;
    private final Set<ClassOutlineImpl> classesView;
    private String mostUsedNamespaceURI;
    private XmlNsForm elementFormDefault;
    private HashMap<String, Integer> uriCountMap;
    private HashMap<String, Integer> propUriCountMap;
    
    public String getMostUsedNamespaceURI() {
        return this.mostUsedNamespaceURI;
    }
    
    public XmlNsForm getElementFormDefault() {
        assert this.elementFormDefault != null;
        return this.elementFormDefault;
    }
    
    public JPackage _package() {
        return this._package;
    }
    
    public ObjectFactoryGenerator objectFactoryGenerator() {
        return this.objectFactoryGenerator;
    }
    
    public Set<ClassOutlineImpl> getClasses() {
        return this.classesView;
    }
    
    public JDefinedClass objectFactory() {
        return this.objectFactoryGenerator.getObjectFactory();
    }
    
    protected PackageOutlineImpl(final BeanGenerator outline, final Model model, final JPackage _pkg) {
        this.classes = new HashSet<ClassOutlineImpl>();
        this.classesView = Collections.unmodifiableSet((Set<? extends ClassOutlineImpl>)this.classes);
        this.uriCountMap = new HashMap<String, Integer>();
        this.propUriCountMap = new HashMap<String, Integer>();
        this._model = model;
        this._package = _pkg;
        switch (model.strategy) {
            case BEAN_ONLY: {
                this.objectFactoryGenerator = new PublicObjectFactoryGenerator(outline, model, _pkg);
                break;
            }
            case INTF_AND_IMPL: {
                this.objectFactoryGenerator = new DualObjectFactoryGenerator(outline, model, _pkg);
                break;
            }
            default: {
                throw new IllegalStateException();
            }
        }
    }
    
    public void calcDefaultValues() {
        if (!this._model.isPackageLevelAnnotations()) {
            this.mostUsedNamespaceURI = "";
            this.elementFormDefault = XmlNsForm.UNQUALIFIED;
            return;
        }
        final CPropertyVisitor<Void> propVisitor = new CPropertyVisitor<Void>() {
            public Void onElement(final CElementPropertyInfo p) {
                for (final CTypeRef tr : p.getTypes()) {
                    PackageOutlineImpl.this.countURI(PackageOutlineImpl.this.propUriCountMap, tr.getTagName());
                }
                return null;
            }
            
            public Void onReference(final CReferencePropertyInfo p) {
                for (final CElement e : p.getElements()) {
                    PackageOutlineImpl.this.countURI(PackageOutlineImpl.this.propUriCountMap, e.getElementName());
                }
                return null;
            }
            
            public Void onAttribute(final CAttributePropertyInfo p) {
                return null;
            }
            
            public Void onValue(final CValuePropertyInfo p) {
                return null;
            }
        };
        for (final ClassOutlineImpl co : this.classes) {
            final CClassInfo ci = co.target;
            this.countURI(this.uriCountMap, ci.getTypeName());
            this.countURI(this.uriCountMap, ci.getElementName());
            for (final CPropertyInfo p : ci.getProperties()) {
                p.accept(propVisitor);
            }
        }
        this.mostUsedNamespaceURI = this.getMostUsedURI(this.uriCountMap);
        this.elementFormDefault = this.getFormDefault();
        if (!this.mostUsedNamespaceURI.equals("") || this.elementFormDefault == XmlNsForm.QUALIFIED) {
            final XmlSchemaWriter w = this._model.strategy.getPackage(this._package, Aspect.IMPLEMENTATION).annotate2(XmlSchemaWriter.class);
            if (!this.mostUsedNamespaceURI.equals("")) {
                w.namespace(this.mostUsedNamespaceURI);
            }
            if (this.elementFormDefault == XmlNsForm.QUALIFIED) {
                w.elementFormDefault(this.elementFormDefault);
            }
        }
    }
    
    private void countURI(final HashMap<String, Integer> map, final QName qname) {
        if (qname == null) {
            return;
        }
        final String uri = qname.getNamespaceURI();
        if (map.containsKey(uri)) {
            map.put(uri, map.get(uri) + 1);
        }
        else {
            map.put(uri, 1);
        }
    }
    
    private String getMostUsedURI(final HashMap<String, Integer> map) {
        String mostPopular = null;
        int count = 0;
        for (final Map.Entry<String, Integer> e : map.entrySet()) {
            final String uri = e.getKey();
            final int uriCount = e.getValue();
            if (mostPopular == null) {
                mostPopular = uri;
                count = uriCount;
            }
            else {
                if (uriCount <= count && (uriCount != count || !mostPopular.equals(""))) {
                    continue;
                }
                mostPopular = uri;
                count = uriCount;
            }
        }
        if (mostPopular == null) {
            return "";
        }
        return mostPopular;
    }
    
    private XmlNsForm getFormDefault() {
        if (this.getMostUsedURI(this.propUriCountMap).equals("")) {
            return XmlNsForm.UNQUALIFIED;
        }
        return XmlNsForm.QUALIFIED;
    }
}
