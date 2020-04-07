// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.xml.bind.v2.model.impl;

import com.sun.xml.bind.v2.runtime.Name;
import javax.xml.stream.XMLStreamException;
import java.io.IOException;
import com.sun.xml.bind.v2.ClassFactory;
import com.sun.xml.bind.v2.runtime.unmarshaller.UnmarshallingContext;
import com.sun.istack.NotNull;
import org.xml.sax.SAXException;
import com.sun.xml.bind.api.AccessorException;
import com.sun.xml.bind.v2.runtime.XMLSerializer;
import com.sun.xml.bind.v2.runtime.Location;
import com.sun.xml.bind.v2.model.core.ClassInfo;
import com.sun.xml.bind.v2.model.core.PropertyInfo;
import java.lang.annotation.Annotation;
import com.sun.xml.bind.annotation.XmlLocation;
import javax.xml.bind.JAXBException;
import java.lang.reflect.Modifier;
import java.util.Iterator;
import com.sun.xml.bind.v2.model.runtime.RuntimeNonElementRef;
import com.sun.xml.bind.v2.runtime.reflect.TransducedAccessor;
import com.sun.xml.bind.v2.model.runtime.RuntimeValuePropertyInfo;
import com.sun.xml.bind.v2.model.core.PropertyKind;
import com.sun.xml.bind.v2.model.runtime.RuntimePropertyInfo;
import java.util.List;
import com.sun.xml.bind.XmlAccessorFactory;
import com.sun.xml.bind.AccessorFactoryImpl;
import com.sun.xml.bind.v2.runtime.IllegalAnnotationException;
import com.sun.xml.bind.v2.model.annotation.Locatable;
import com.sun.xml.bind.v2.runtime.Transducer;
import javax.xml.namespace.QName;
import java.util.Map;
import com.sun.xml.bind.AccessorFactory;
import org.xml.sax.Locator;
import com.sun.xml.bind.v2.runtime.reflect.Accessor;
import com.sun.xml.bind.v2.model.runtime.RuntimeElement;
import com.sun.xml.bind.v2.model.runtime.RuntimeClassInfo;
import java.lang.reflect.Method;
import java.lang.reflect.Field;
import java.lang.reflect.Type;

class RuntimeClassInfoImpl extends ClassInfoImpl<Type, Class, Field, Method> implements RuntimeClassInfo, RuntimeElement
{
    private Accessor<?, Locator> xmlLocationAccessor;
    private AccessorFactory accessorFactory;
    private Accessor<?, Map<QName, String>> attributeWildcardAccessor;
    private boolean computedTransducer;
    private Transducer xducer;
    
    public RuntimeClassInfoImpl(final RuntimeModelBuilder modelBuilder, final Locatable upstream, final Class clazz) {
        super((ModelBuilder<Object, Class, Object, Object>)modelBuilder, upstream, clazz);
        this.computedTransducer = false;
        this.xducer = null;
        this.accessorFactory = this.createAccessorFactory(clazz);
    }
    
    protected AccessorFactory createAccessorFactory(final Class clazz) {
        AccessorFactory accFactory = null;
        if (((RuntimeModelBuilder)this.builder).context.xmlAccessorFactorySupport) {
            final XmlAccessorFactory factoryAnn = this.findXmlAccessorFactoryAnnotation(clazz);
            if (factoryAnn != null) {
                try {
                    accFactory = (AccessorFactory)factoryAnn.value().newInstance();
                }
                catch (InstantiationException e) {
                    this.builder.reportError(new IllegalAnnotationException(Messages.ACCESSORFACTORY_INSTANTIATION_EXCEPTION.format(factoryAnn.getClass().getName(), this.nav().getClassName(clazz)), this));
                }
                catch (IllegalAccessException e2) {
                    this.builder.reportError(new IllegalAnnotationException(Messages.ACCESSORFACTORY_ACCESS_EXCEPTION.format(factoryAnn.getClass().getName(), this.nav().getClassName(clazz)), this));
                }
            }
        }
        if (accFactory == null) {
            accFactory = AccessorFactoryImpl.getInstance();
        }
        return accFactory;
    }
    
    protected XmlAccessorFactory findXmlAccessorFactoryAnnotation(final Class clazz) {
        XmlAccessorFactory factoryAnn = this.reader().getClassAnnotation(XmlAccessorFactory.class, clazz, this);
        if (factoryAnn == null) {
            factoryAnn = this.reader().getPackageAnnotation(XmlAccessorFactory.class, clazz, this);
        }
        return factoryAnn;
    }
    
    public Method getFactoryMethod() {
        return super.getFactoryMethod();
    }
    
    public final RuntimeClassInfoImpl getBaseClass() {
        return (RuntimeClassInfoImpl)super.getBaseClass();
    }
    
    protected ReferencePropertyInfoImpl createReferenceProperty(final PropertySeed<Type, Class, Field, Method> seed) {
        return new RuntimeReferencePropertyInfoImpl(this, seed);
    }
    
    protected AttributePropertyInfoImpl createAttributeProperty(final PropertySeed<Type, Class, Field, Method> seed) {
        return new RuntimeAttributePropertyInfoImpl(this, seed);
    }
    
    protected ValuePropertyInfoImpl createValueProperty(final PropertySeed<Type, Class, Field, Method> seed) {
        return new RuntimeValuePropertyInfoImpl(this, seed);
    }
    
    protected ElementPropertyInfoImpl createElementProperty(final PropertySeed<Type, Class, Field, Method> seed) {
        return new RuntimeElementPropertyInfoImpl(this, seed);
    }
    
    protected MapPropertyInfoImpl createMapProperty(final PropertySeed<Type, Class, Field, Method> seed) {
        return new RuntimeMapPropertyInfoImpl(this, seed);
    }
    
    public List<? extends RuntimePropertyInfo> getProperties() {
        return (List<? extends RuntimePropertyInfo>)super.getProperties();
    }
    
    public RuntimePropertyInfo getProperty(final String name) {
        return (RuntimePropertyInfo)super.getProperty(name);
    }
    
    public void link() {
        this.getTransducer();
        super.link();
    }
    
    public <B> Accessor<B, Map<QName, String>> getAttributeWildcard() {
        for (RuntimeClassInfoImpl c = this; c != null; c = c.getBaseClass()) {
            if (c.attributeWildcard != null) {
                if (c.attributeWildcardAccessor == null) {
                    c.attributeWildcardAccessor = c.createAttributeWildcardAccessor();
                }
                return (Accessor<B, Map<QName, String>>)c.attributeWildcardAccessor;
            }
        }
        return null;
    }
    
    public Transducer getTransducer() {
        if (!this.computedTransducer) {
            this.computedTransducer = true;
            this.xducer = this.calcTransducer();
        }
        return this.xducer;
    }
    
    private Transducer calcTransducer() {
        RuntimeValuePropertyInfo valuep = null;
        if (this.hasAttributeWildcard()) {
            return null;
        }
        for (RuntimeClassInfoImpl ci = this; ci != null; ci = ci.getBaseClass()) {
            for (final RuntimePropertyInfo pi : ci.getProperties()) {
                if (pi.kind() != PropertyKind.VALUE) {
                    return null;
                }
                valuep = (RuntimeValuePropertyInfo)pi;
            }
        }
        if (valuep == null) {
            return null;
        }
        if (!valuep.getTarget().isSimpleType()) {
            return null;
        }
        return new TransducerImpl(((ClassInfoImpl<T, Class<Object>, F, M>)this).getClazz(), TransducedAccessor.get(((RuntimeModelBuilder)this.builder).context, valuep));
    }
    
    private Accessor<?, Map<QName, String>> createAttributeWildcardAccessor() {
        assert this.attributeWildcard != null;
        return (Accessor<?, Map<QName, String>>)((RuntimePropertySeed)this.attributeWildcard).getAccessor();
    }
    
    protected RuntimePropertySeed createFieldSeed(final Field field) {
        final boolean readOnly = Modifier.isStatic(field.getModifiers());
        Accessor acc;
        try {
            acc = this.accessorFactory.createFieldAccessor((Class)this.clazz, field, readOnly);
        }
        catch (JAXBException e) {
            this.builder.reportError(new IllegalAnnotationException(Messages.CUSTOM_ACCESSORFACTORY_FIELD_ERROR.format(this.nav().getClassName((Class)this.clazz), e.toString()), this));
            acc = Accessor.getErrorInstance();
        }
        return new RuntimePropertySeed((PropertySeed<Type, Class, Field, Method>)super.createFieldSeed(field), acc);
    }
    
    public RuntimePropertySeed createAccessorSeed(final Method getter, final Method setter) {
        Accessor acc;
        try {
            acc = this.accessorFactory.createPropertyAccessor((Class)this.clazz, getter, setter);
        }
        catch (JAXBException e) {
            this.builder.reportError(new IllegalAnnotationException(Messages.CUSTOM_ACCESSORFACTORY_PROPERTY_ERROR.format(this.nav().getClassName((Class)this.clazz), e.toString()), this));
            acc = Accessor.getErrorInstance();
        }
        return new RuntimePropertySeed((PropertySeed<Type, Class, Field, Method>)super.createAccessorSeed(getter, setter), acc);
    }
    
    protected void checkFieldXmlLocation(final Field f) {
        if (this.reader().hasFieldAnnotation(XmlLocation.class, f)) {
            this.xmlLocationAccessor = new Accessor.FieldReflection<Object, Locator>(f);
        }
    }
    
    public Accessor<?, Locator> getLocatorField() {
        return this.xmlLocationAccessor;
    }
    
    static final class RuntimePropertySeed implements PropertySeed<Type, Class, Field, Method>
    {
        private final Accessor acc;
        private final PropertySeed<Type, Class, Field, Method> core;
        
        public RuntimePropertySeed(final PropertySeed<Type, Class, Field, Method> core, final Accessor acc) {
            this.core = core;
            this.acc = acc;
        }
        
        public String getName() {
            return this.core.getName();
        }
        
        public <A extends Annotation> A readAnnotation(final Class<A> annotationType) {
            return this.core.readAnnotation(annotationType);
        }
        
        public boolean hasAnnotation(final Class<? extends Annotation> annotationType) {
            return this.core.hasAnnotation(annotationType);
        }
        
        public Type getRawType() {
            return this.core.getRawType();
        }
        
        public Location getLocation() {
            return this.core.getLocation();
        }
        
        public Locatable getUpstream() {
            return this.core.getUpstream();
        }
        
        public Accessor getAccessor() {
            return this.acc;
        }
    }
    
    private static final class TransducerImpl<BeanT> implements Transducer<BeanT>
    {
        private final TransducedAccessor<BeanT> xacc;
        private final Class<BeanT> ownerClass;
        
        public TransducerImpl(final Class<BeanT> ownerClass, final TransducedAccessor<BeanT> xacc) {
            this.xacc = xacc;
            this.ownerClass = ownerClass;
        }
        
        public boolean useNamespace() {
            return this.xacc.useNamespace();
        }
        
        public boolean isDefault() {
            return false;
        }
        
        public void declareNamespace(final BeanT bean, final XMLSerializer w) throws AccessorException {
            try {
                this.xacc.declareNamespace(bean, w);
            }
            catch (SAXException e) {
                throw new AccessorException(e);
            }
        }
        
        @NotNull
        public CharSequence print(final BeanT o) throws AccessorException {
            try {
                final CharSequence value = this.xacc.print(o);
                if (value == null) {
                    throw new AccessorException(Messages.THERE_MUST_BE_VALUE_IN_XMLVALUE.format(o));
                }
                return value;
            }
            catch (SAXException e) {
                throw new AccessorException(e);
            }
        }
        
        public BeanT parse(final CharSequence lexical) throws AccessorException, SAXException {
            final UnmarshallingContext ctxt = UnmarshallingContext.getInstance();
            BeanT inst;
            if (ctxt != null) {
                inst = (BeanT)ctxt.createInstance(this.ownerClass);
            }
            else {
                inst = ClassFactory.create(this.ownerClass);
            }
            this.xacc.parse(inst, lexical);
            return inst;
        }
        
        public void writeText(final XMLSerializer w, final BeanT o, final String fieldName) throws IOException, SAXException, XMLStreamException, AccessorException {
            if (!this.xacc.hasValue(o)) {
                throw new AccessorException(Messages.THERE_MUST_BE_VALUE_IN_XMLVALUE.format(o));
            }
            this.xacc.writeText(w, o, fieldName);
        }
        
        public void writeLeafElement(final XMLSerializer w, final Name tagName, final BeanT o, final String fieldName) throws IOException, SAXException, XMLStreamException, AccessorException {
            if (!this.xacc.hasValue(o)) {
                throw new AccessorException(Messages.THERE_MUST_BE_VALUE_IN_XMLVALUE.format(o));
            }
            this.xacc.writeLeafElement(w, tagName, o, fieldName);
        }
        
        public QName getTypeName(final BeanT instance) {
            return null;
        }
    }
}
