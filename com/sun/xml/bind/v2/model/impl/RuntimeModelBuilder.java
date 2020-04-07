// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.xml.bind.v2.model.impl;

import org.xml.sax.SAXException;
import com.sun.xml.bind.api.AccessorException;
import com.sun.xml.bind.v2.runtime.unmarshaller.UnmarshallingContext;
import com.sun.xml.bind.WhiteSpaceProcessor;
import com.sun.xml.bind.v2.runtime.FilterTransducer;
import com.sun.xml.bind.v2.model.core.NonElement;
import com.sun.xml.bind.v2.model.core.TypeInfoSet;
import javax.activation.MimeType;
import com.sun.xml.bind.v2.model.runtime.RuntimePropertyInfo;
import com.sun.xml.bind.v2.runtime.SchemaTypeTransducer;
import com.sun.xml.bind.v2.runtime.InlineBinaryTransducer;
import com.sun.xml.bind.v2.runtime.MimeTypedTransducer;
import com.sun.xml.bind.v2.model.core.ID;
import com.sun.xml.bind.v2.runtime.Transducer;
import com.sun.xml.bind.v2.model.runtime.RuntimeNonElementRef;
import com.sun.xml.bind.v2.model.runtime.RuntimeTypeInfoSet;
import com.sun.xml.bind.v2.model.nav.ReflectionNavigator;
import com.sun.xml.bind.v2.runtime.IllegalAnnotationException;
import com.sun.xml.bind.v2.model.runtime.RuntimeNonElement;
import com.sun.xml.bind.v2.model.annotation.Locatable;
import com.sun.xml.bind.v2.model.annotation.AnnotationReader;
import com.sun.xml.bind.v2.model.nav.Navigator;
import java.util.Map;
import com.sun.xml.bind.v2.model.annotation.RuntimeAnnotationReader;
import com.sun.istack.Nullable;
import com.sun.xml.bind.v2.runtime.JAXBContextImpl;
import java.lang.reflect.Method;
import java.lang.reflect.Field;
import java.lang.reflect.Type;

public class RuntimeModelBuilder extends ModelBuilder<Type, Class, Field, Method>
{
    @Nullable
    public final JAXBContextImpl context;
    
    public RuntimeModelBuilder(final JAXBContextImpl context, final RuntimeAnnotationReader annotationReader, final Map<Class, Class> subclassReplacements, final String defaultNamespaceRemap) {
        super((AnnotationReader<Object, Class, Object, Object>)annotationReader, (Navigator<Object, Class, Object, Object>)Navigator.REFLECTION, subclassReplacements, defaultNamespaceRemap);
        this.context = context;
    }
    
    public RuntimeNonElement getClassInfo(final Class clazz, final Locatable upstream) {
        return (RuntimeNonElement)super.getClassInfo(clazz, upstream);
    }
    
    public RuntimeNonElement getClassInfo(final Class clazz, final boolean searchForSuperClass, final Locatable upstream) {
        return (RuntimeNonElement)super.getClassInfo(clazz, searchForSuperClass, upstream);
    }
    
    protected RuntimeEnumLeafInfoImpl createEnumLeafInfo(final Class clazz, final Locatable upstream) {
        return new RuntimeEnumLeafInfoImpl(this, upstream, clazz);
    }
    
    protected RuntimeClassInfoImpl createClassInfo(final Class clazz, final Locatable upstream) {
        return new RuntimeClassInfoImpl(this, upstream, clazz);
    }
    
    public RuntimeElementInfoImpl createElementInfo(final RegistryInfoImpl<Type, Class, Field, Method> registryInfo, final Method method) throws IllegalAnnotationException {
        return new RuntimeElementInfoImpl(this, (RegistryInfoImpl)registryInfo, method);
    }
    
    public RuntimeArrayInfoImpl createArrayInfo(final Locatable upstream, final Type arrayType) {
        return new RuntimeArrayInfoImpl(this, upstream, (Class)arrayType);
    }
    
    public ReflectionNavigator getNavigator() {
        return (ReflectionNavigator)this.nav;
    }
    
    protected RuntimeTypeInfoSetImpl createTypeInfoSet() {
        return new RuntimeTypeInfoSetImpl((AnnotationReader<Type, Class, Field, Method>)this.reader);
    }
    
    public RuntimeTypeInfoSet link() {
        return (RuntimeTypeInfoSet)super.link();
    }
    
    public static Transducer createTransducer(final RuntimeNonElementRef ref) {
        Transducer t = ref.getTarget().getTransducer();
        final RuntimePropertyInfo src = ref.getSource();
        final ID id = src.id();
        if (id == ID.IDREF) {
            return RuntimeBuiltinLeafInfoImpl.STRING;
        }
        if (id == ID.ID) {
            t = new IDTransducerImpl(t);
        }
        final MimeType emt = src.getExpectedMimeType();
        if (emt != null) {
            t = new MimeTypedTransducer(t, emt);
        }
        if (src.inlineBinaryData()) {
            t = new InlineBinaryTransducer(t);
        }
        if (src.getSchemaType() != null) {
            t = new SchemaTypeTransducer(t, src.getSchemaType());
        }
        return t;
    }
    
    private static final class IDTransducerImpl<ValueT> extends FilterTransducer<ValueT>
    {
        public IDTransducerImpl(final Transducer<ValueT> core) {
            super(core);
        }
        
        public ValueT parse(final CharSequence lexical) throws AccessorException, SAXException {
            final String value = WhiteSpaceProcessor.trim(lexical).toString();
            UnmarshallingContext.getInstance().addToIdTable(value);
            return this.core.parse(value);
        }
    }
}
