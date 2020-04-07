// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.xml.bind.v2.runtime;

import com.sun.xml.bind.v2.model.core.ClassInfo;
import com.sun.xml.bind.Util;
import com.sun.xml.bind.v2.runtime.unmarshaller.XsiTypeLoader;
import com.sun.xml.bind.v2.runtime.unmarshaller.StructureLoader;
import javax.xml.stream.XMLStreamException;
import java.io.IOException;
import javax.xml.bind.ValidationEvent;
import javax.xml.bind.ValidationEventLocator;
import javax.xml.bind.helpers.ValidationEventImpl;
import org.xml.sax.SAXException;
import java.lang.reflect.InvocationTargetException;
import com.sun.xml.bind.api.AccessorException;
import org.xml.sax.helpers.LocatorImpl;
import com.sun.xml.bind.v2.ClassFactory;
import com.sun.xml.bind.v2.runtime.unmarshaller.UnmarshallingContext;
import java.util.List;
import java.util.Collections;
import com.sun.istack.FinalArrayList;
import java.util.Iterator;
import java.util.Collection;
import com.sun.xml.bind.v2.model.core.ID;
import com.sun.xml.bind.v2.runtime.property.PropertyFactory;
import com.sun.xml.bind.v2.model.runtime.RuntimePropertyInfo;
import java.util.logging.Level;
import java.lang.reflect.Modifier;
import com.sun.xml.bind.v2.model.runtime.RuntimeTypeInfo;
import java.util.logging.Logger;
import java.lang.reflect.Method;
import com.sun.xml.bind.v2.runtime.property.AttributeProperty;
import org.xml.sax.Locator;
import javax.xml.namespace.QName;
import java.util.Map;
import com.sun.xml.bind.v2.runtime.reflect.Accessor;
import com.sun.xml.bind.v2.model.runtime.RuntimeClassInfo;
import com.sun.xml.bind.v2.runtime.unmarshaller.Loader;
import com.sun.xml.bind.v2.runtime.property.Property;

public final class ClassBeanInfoImpl<BeanT> extends JaxBeanInfo<BeanT>
{
    public final Property<BeanT>[] properties;
    private Property<? super BeanT> idProperty;
    private Loader loader;
    private Loader loaderWithTypeSubst;
    private RuntimeClassInfo ci;
    private final Accessor<? super BeanT, Map<QName, String>> inheritedAttWildcard;
    private final Transducer<BeanT> xducer;
    public final ClassBeanInfoImpl<? super BeanT> superClazz;
    private final Accessor<? super BeanT, Locator> xmlLocatorField;
    private final Name tagName;
    private AttributeProperty<BeanT>[] attributeProperties;
    private Property<BeanT>[] uriProperties;
    private final Method factoryMethod;
    private static final AttributeProperty[] EMPTY_PROPERTIES;
    private static final Logger logger;
    
    ClassBeanInfoImpl(final JAXBContextImpl owner, final RuntimeClassInfo ci) {
        super(owner, ci, ((ClassInfo<T, Class>)ci).getClazz(), ci.getTypeName(), ci.isElement(), false, true);
        this.ci = ci;
        this.inheritedAttWildcard = ci.getAttributeWildcard();
        this.xducer = ci.getTransducer();
        this.factoryMethod = ci.getFactoryMethod();
        Label_0156: {
            if (this.factoryMethod != null) {
                final int classMod = this.factoryMethod.getDeclaringClass().getModifiers();
                if (Modifier.isPublic(classMod)) {
                    if (Modifier.isPublic(this.factoryMethod.getModifiers())) {
                        break Label_0156;
                    }
                }
                try {
                    this.factoryMethod.setAccessible(true);
                }
                catch (SecurityException e) {
                    ClassBeanInfoImpl.logger.log(Level.FINE, "Unable to make the method of " + this.factoryMethod + " accessible", e);
                    throw e;
                }
            }
        }
        if (ci.getBaseClass() == null) {
            this.superClazz = null;
        }
        else {
            this.superClazz = (ClassBeanInfoImpl<? super BeanT>)owner.getOrCreate(ci.getBaseClass());
        }
        if (this.superClazz != null && this.superClazz.xmlLocatorField != null) {
            this.xmlLocatorField = this.superClazz.xmlLocatorField;
        }
        else {
            this.xmlLocatorField = ci.getLocatorField();
        }
        final Collection<? extends RuntimePropertyInfo> ps = ci.getProperties();
        this.properties = (Property<BeanT>[])new Property[ps.size()];
        int idx = 0;
        boolean elementOnly = true;
        for (final RuntimePropertyInfo info : ps) {
            final Property p = PropertyFactory.create(owner, info);
            if (info.id() == ID.ID) {
                this.idProperty = (Property<? super BeanT>)p;
            }
            this.properties[idx++] = (Property<BeanT>)p;
            elementOnly &= info.elementOnlyContent();
        }
        this.hasElementOnlyContentModel(elementOnly);
        if (ci.isElement()) {
            this.tagName = owner.nameBuilder.createElementName(ci.getElementName());
        }
        else {
            this.tagName = null;
        }
        this.setLifecycleFlags();
    }
    
    protected void link(final JAXBContextImpl grammar) {
        if (this.uriProperties != null) {
            return;
        }
        super.link(grammar);
        if (this.superClazz != null) {
            this.superClazz.link(grammar);
        }
        this.getLoader(grammar, true);
        if (this.superClazz != null) {
            if (this.idProperty == null) {
                this.idProperty = this.superClazz.idProperty;
            }
            if (!this.superClazz.hasElementOnlyContentModel()) {
                this.hasElementOnlyContentModel(false);
            }
        }
        final List<AttributeProperty> attProps = new FinalArrayList<AttributeProperty>();
        final List<Property> uriProps = new FinalArrayList<Property>();
        for (ClassBeanInfoImpl bi = this; bi != null; bi = bi.superClazz) {
            for (int i = bi.properties.length - 1; i >= 0; --i) {
                final Property p = bi.properties[i];
                if (p instanceof AttributeProperty) {
                    attProps.add((AttributeProperty)p);
                }
                if (p.hasSerializeURIAction()) {
                    uriProps.add(p);
                }
            }
        }
        if (grammar.c14nSupport) {
            Collections.sort(attProps);
        }
        if (attProps.isEmpty()) {
            this.attributeProperties = (AttributeProperty<BeanT>[])ClassBeanInfoImpl.EMPTY_PROPERTIES;
        }
        else {
            this.attributeProperties = attProps.toArray(new AttributeProperty[attProps.size()]);
        }
        if (uriProps.isEmpty()) {
            this.uriProperties = (Property<BeanT>[])ClassBeanInfoImpl.EMPTY_PROPERTIES;
        }
        else {
            this.uriProperties = uriProps.toArray(new Property[uriProps.size()]);
        }
    }
    
    public void wrapUp() {
        for (final Property p : this.properties) {
            p.wrapUp();
        }
        this.ci = null;
        super.wrapUp();
    }
    
    public String getElementNamespaceURI(final BeanT bean) {
        return this.tagName.nsUri;
    }
    
    public String getElementLocalName(final BeanT bean) {
        return this.tagName.localName;
    }
    
    public BeanT createInstance(final UnmarshallingContext context) throws IllegalAccessException, InvocationTargetException, InstantiationException, SAXException {
        BeanT bean = null;
        if (this.factoryMethod == null) {
            bean = ClassFactory.create0(this.jaxbType);
        }
        else {
            final Object o = ClassFactory.create(this.factoryMethod);
            if (!this.jaxbType.isInstance(o)) {
                throw new InstantiationException("The factory method didn't return a correct object");
            }
            bean = (BeanT)o;
        }
        if (this.xmlLocatorField != null) {
            try {
                this.xmlLocatorField.set((Object)bean, new LocatorImpl(context.getLocator()));
            }
            catch (AccessorException e) {
                context.handleError(e);
            }
        }
        return bean;
    }
    
    public boolean reset(final BeanT bean, final UnmarshallingContext context) throws SAXException {
        try {
            if (this.superClazz != null) {
                this.superClazz.reset((Object)bean, context);
            }
            for (final Property<BeanT> p : this.properties) {
                p.reset(bean);
            }
            return true;
        }
        catch (AccessorException e) {
            context.handleError(e);
            return false;
        }
    }
    
    public String getId(final BeanT bean, final XMLSerializer target) throws SAXException {
        if (this.idProperty != null) {
            try {
                return this.idProperty.getIdValue((Object)bean);
            }
            catch (AccessorException e) {
                target.reportError(null, e);
            }
        }
        return null;
    }
    
    public void serializeRoot(final BeanT bean, final XMLSerializer target) throws SAXException, IOException, XMLStreamException {
        if (this.tagName == null) {
            target.reportError(new ValidationEventImpl(1, Messages.UNABLE_TO_MARSHAL_NON_ELEMENT.format(bean.getClass().getName()), null, null));
        }
        else {
            target.startElement(this.tagName, bean);
            target.childAsSoleContent(bean, null);
            target.endElement();
        }
    }
    
    public void serializeBody(final BeanT bean, final XMLSerializer target) throws SAXException, IOException, XMLStreamException {
        if (this.superClazz != null) {
            this.superClazz.serializeBody((Object)bean, target);
        }
        try {
            for (final Property<BeanT> p : this.properties) {
                p.serializeBody(bean, target, null);
            }
        }
        catch (AccessorException e) {
            target.reportError(null, e);
        }
    }
    
    public void serializeAttributes(final BeanT bean, final XMLSerializer target) throws SAXException, IOException, XMLStreamException {
        for (final AttributeProperty<BeanT> p : this.attributeProperties) {
            try {
                p.serializeAttributes(bean, target);
            }
            catch (AccessorException e) {
                target.reportError(null, e);
            }
        }
        try {
            if (this.inheritedAttWildcard != null) {
                final Map<QName, String> map = this.inheritedAttWildcard.get((Object)bean);
                target.attWildcardAsAttributes(map, null);
            }
        }
        catch (AccessorException e2) {
            target.reportError(null, e2);
        }
    }
    
    public void serializeURIs(final BeanT bean, final XMLSerializer target) throws SAXException {
        try {
            for (final Property<BeanT> p : this.uriProperties) {
                p.serializeURIs(bean, target);
            }
            if (this.inheritedAttWildcard != null) {
                final Map<QName, String> map = this.inheritedAttWildcard.get((Object)bean);
                target.attWildcardAsURIs(map, null);
            }
        }
        catch (AccessorException e) {
            target.reportError(null, e);
        }
    }
    
    public Loader getLoader(final JAXBContextImpl context, final boolean typeSubstitutionCapable) {
        if (this.loader == null) {
            final StructureLoader sl = new StructureLoader(this);
            this.loader = sl;
            if (this.ci.hasSubClasses()) {
                this.loaderWithTypeSubst = new XsiTypeLoader(this);
            }
            else {
                this.loaderWithTypeSubst = this.loader;
            }
            sl.init(context, this, this.ci.getAttributeWildcard());
        }
        if (typeSubstitutionCapable) {
            return this.loaderWithTypeSubst;
        }
        return this.loader;
    }
    
    public Transducer<BeanT> getTransducer() {
        return this.xducer;
    }
    
    static {
        EMPTY_PROPERTIES = new AttributeProperty[0];
        logger = Util.getClassLogger();
    }
}
