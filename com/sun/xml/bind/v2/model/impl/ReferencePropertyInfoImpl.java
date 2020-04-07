// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.xml.bind.v2.model.impl;

import java.util.Collection;
import com.sun.xml.bind.v2.model.core.NonElement;
import com.sun.xml.bind.v2.model.core.ClassInfo;
import java.util.Iterator;
import javax.xml.bind.annotation.XmlNsForm;
import javax.xml.bind.annotation.XmlSchema;
import com.sun.xml.bind.v2.model.core.ElementInfo;
import javax.xml.namespace.QName;
import com.sun.xml.bind.v2.model.annotation.AnnotationReader;
import com.sun.xml.bind.v2.model.nav.Navigator;
import java.util.Collections;
import com.sun.xml.bind.v2.model.annotation.Locatable;
import javax.xml.bind.JAXBElement;
import com.sun.xml.bind.v2.runtime.IllegalAnnotationException;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlElementRefs;
import java.util.LinkedHashSet;
import com.sun.xml.bind.v2.model.core.PropertyKind;
import java.lang.annotation.Annotation;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlMixed;
import com.sun.xml.bind.v2.model.core.WildcardMode;
import com.sun.xml.bind.v2.model.core.Element;
import java.util.Set;
import com.sun.xml.bind.v2.model.core.ReferencePropertyInfo;

class ReferencePropertyInfoImpl<T, C, F, M> extends ERPropertyInfoImpl<T, C, F, M> implements ReferencePropertyInfo<T, C>
{
    private Set<Element<T, C>> types;
    private final boolean isMixed;
    private final WildcardMode wildcard;
    private final C domHandler;
    
    public ReferencePropertyInfoImpl(final ClassInfoImpl<T, C, F, M> classInfo, final PropertySeed<T, C, F, M> seed) {
        super(classInfo, seed);
        this.isMixed = (seed.readAnnotation(XmlMixed.class) != null);
        final XmlAnyElement xae = seed.readAnnotation(XmlAnyElement.class);
        if (xae == null) {
            this.wildcard = null;
            this.domHandler = null;
        }
        else {
            this.wildcard = (xae.lax() ? WildcardMode.LAX : WildcardMode.SKIP);
            this.domHandler = this.nav().asDecl(this.reader().getClassValue(xae, "value"));
        }
    }
    
    public Set<? extends Element<T, C>> ref() {
        return this.getElements();
    }
    
    public PropertyKind kind() {
        return PropertyKind.REFERENCE;
    }
    
    public Set<? extends Element<T, C>> getElements() {
        if (this.types == null) {
            this.calcTypes(false);
        }
        assert this.types != null;
        return this.types;
    }
    
    private void calcTypes(final boolean last) {
        this.types = new LinkedHashSet<Element<T, C>>();
        final XmlElementRefs refs = this.seed.readAnnotation(XmlElementRefs.class);
        final XmlElementRef ref = this.seed.readAnnotation(XmlElementRef.class);
        if (refs != null && ref != null) {
            this.parent.builder.reportError(new IllegalAnnotationException(Messages.MUTUALLY_EXCLUSIVE_ANNOTATIONS.format(this.nav().getClassName(this.parent.getClazz()) + '#' + this.seed.getName(), ref.annotationType().getName(), refs.annotationType().getName()), ref, refs));
        }
        XmlElementRef[] ann;
        if (refs != null) {
            ann = refs.value();
        }
        else if (ref != null) {
            ann = new XmlElementRef[] { ref };
        }
        else {
            ann = null;
        }
        if (ann != null) {
            final Navigator<T, C, F, M> nav = this.nav();
            final AnnotationReader<T, C, F, M> reader = this.reader();
            final T defaultType = nav.ref(XmlElementRef.DEFAULT.class);
            final C je = nav.asDecl(JAXBElement.class);
            for (final XmlElementRef r : ann) {
                T type = reader.getClassValue(r, "type");
                if (type.equals(defaultType)) {
                    type = nav.erasure(this.getIndividualType());
                }
                boolean yield;
                if (nav.getBaseClass(type, je) != null) {
                    yield = this.addGenericElement(r);
                }
                else {
                    yield = this.addAllSubtypes(type);
                }
                if (last && !yield) {
                    if (type.equals(nav.ref(JAXBElement.class))) {
                        this.parent.builder.reportError(new IllegalAnnotationException(Messages.NO_XML_ELEMENT_DECL.format(this.getEffectiveNamespaceFor(r), r.name()), this));
                    }
                    else {
                        this.parent.builder.reportError(new IllegalAnnotationException(Messages.INVALID_XML_ELEMENT_REF.format(new Object[0]), this));
                    }
                    return;
                }
            }
        }
        this.types = Collections.unmodifiableSet((Set<? extends Element<T, C>>)this.types);
    }
    
    private boolean addGenericElement(final XmlElementRef r) {
        final String nsUri = this.getEffectiveNamespaceFor(r);
        return this.addGenericElement(this.parent.owner.getElementInfo(this.parent.getClazz(), new QName(nsUri, r.name())));
    }
    
    private String getEffectiveNamespaceFor(final XmlElementRef r) {
        String nsUri = r.namespace();
        final XmlSchema xs = this.reader().getPackageAnnotation(XmlSchema.class, this.parent.getClazz(), this);
        if (xs != null && xs.attributeFormDefault() == XmlNsForm.QUALIFIED && nsUri.length() == 0) {
            nsUri = this.parent.builder.defaultNsUri;
        }
        return nsUri;
    }
    
    private boolean addGenericElement(final ElementInfo<T, C> ei) {
        if (ei == null) {
            return false;
        }
        this.types.add(ei);
        for (final ElementInfo<T, C> subst : ei.getSubstitutionMembers()) {
            this.addGenericElement(subst);
        }
        return true;
    }
    
    private boolean addAllSubtypes(final T type) {
        final Navigator<T, C, F, M> nav = this.nav();
        final NonElement<T, C> t = this.parent.builder.getClassInfo(nav.asDecl(type), this);
        if (!(t instanceof ClassInfo)) {
            return false;
        }
        boolean result = false;
        final ClassInfo<T, C> c = (ClassInfo<T, C>)(ClassInfo)t;
        if (c.isElement()) {
            this.types.add(c.asElement());
            result = true;
        }
        for (final ClassInfo<T, C> ci : this.parent.owner.beans().values()) {
            if (ci.isElement() && nav.isSubClassOf(ci.getType(), type)) {
                this.types.add(ci.asElement());
                result = true;
            }
        }
        for (final ElementInfo<T, C> ei : this.parent.owner.getElementMappings(null).values()) {
            if (nav.isSubClassOf(ei.getType(), type)) {
                this.types.add(ei);
                result = true;
            }
        }
        return result;
    }
    
    protected void link() {
        super.link();
        this.calcTypes(true);
    }
    
    public final boolean isMixed() {
        return this.isMixed;
    }
    
    public final WildcardMode getWildcard() {
        return this.wildcard;
    }
    
    public final C getDOMHandler() {
        return this.domHandler;
    }
}
