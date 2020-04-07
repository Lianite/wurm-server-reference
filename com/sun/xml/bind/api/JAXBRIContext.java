// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.xml.bind.api;

import com.sun.xml.bind.v2.model.nav.Navigator;
import java.lang.reflect.Type;
import com.sun.xml.bind.api.impl.NameConverter;
import javax.xml.transform.Result;
import java.io.IOException;
import javax.xml.bind.SchemaOutputResolver;
import java.util.List;
import javax.xml.namespace.QName;
import java.util.Collections;
import javax.xml.bind.JAXBException;
import com.sun.xml.bind.v2.ContextFactory;
import com.sun.xml.bind.v2.model.annotation.RuntimeAnnotationReader;
import java.util.Map;
import com.sun.istack.Nullable;
import java.util.Collection;
import com.sun.istack.NotNull;
import javax.xml.bind.JAXBContext;

public abstract class JAXBRIContext extends JAXBContext
{
    public static final String DEFAULT_NAMESPACE_REMAP = "com.sun.xml.bind.defaultNamespaceRemap";
    public static final String TYPE_REFERENCES = "com.sun.xml.bind.typeReferences";
    public static final String CANONICALIZATION_SUPPORT = "com.sun.xml.bind.c14n";
    public static final String TREAT_EVERYTHING_NILLABLE = "com.sun.xml.bind.treatEverythingNillable";
    public static final String ANNOTATION_READER;
    public static final String ENABLE_XOP = "com.sun.xml.bind.XOP";
    public static final String SUBCLASS_REPLACEMENTS = "com.sun.xml.bind.subclassReplacements";
    public static final String XMLACCESSORFACTORY_SUPPORT = "com.sun.xml.bind.XmlAccessorFactory";
    
    public static JAXBRIContext newInstance(@NotNull final Class[] classes, @Nullable final Collection<TypeReference> typeRefs, @Nullable final Map<Class, Class> subclassReplacements, @Nullable final String defaultNamespaceRemap, final boolean c14nSupport, @Nullable final RuntimeAnnotationReader ar) throws JAXBException {
        return ContextFactory.createContext(classes, typeRefs, subclassReplacements, defaultNamespaceRemap, c14nSupport, ar, false, false);
    }
    
    public static JAXBRIContext newInstance(@NotNull final Class[] classes, @Nullable final Collection<TypeReference> typeRefs, @Nullable final String defaultNamespaceRemap, final boolean c14nSupport) throws JAXBException {
        return newInstance(classes, typeRefs, (Map<Class, Class>)Collections.emptyMap(), defaultNamespaceRemap, c14nSupport, null);
    }
    
    public abstract boolean hasSwaRef();
    
    @Nullable
    public abstract QName getElementName(@NotNull final Object p0) throws JAXBException;
    
    public abstract Bridge createBridge(@NotNull final TypeReference p0);
    
    @NotNull
    public abstract BridgeContext createBridgeContext();
    
    public abstract <B, V> RawAccessor<B, V> getElementPropertyAccessor(final Class<B> p0, final String p1, final String p2) throws JAXBException;
    
    @NotNull
    public abstract List<String> getKnownNamespaceURIs();
    
    public abstract void generateSchema(@NotNull final SchemaOutputResolver p0) throws IOException;
    
    public abstract QName getTypeName(@NotNull final TypeReference p0);
    
    @NotNull
    public abstract String getBuildId();
    
    public abstract void generateEpisode(final Result p0);
    
    @NotNull
    public static String mangleNameToVariableName(@NotNull final String localName) {
        return NameConverter.standard.toVariableName(localName);
    }
    
    @NotNull
    public static String mangleNameToClassName(@NotNull final String localName) {
        return NameConverter.standard.toClassName(localName);
    }
    
    @NotNull
    public static String mangleNameToPropertyName(@NotNull final String localName) {
        return NameConverter.standard.toPropertyName(localName);
    }
    
    @Nullable
    public static Type getBaseType(@NotNull final Type type, @NotNull final Class baseType) {
        return Navigator.REFLECTION.getBaseClass(type, baseType);
    }
    
    static {
        ANNOTATION_READER = RuntimeAnnotationReader.class.getName();
    }
}
