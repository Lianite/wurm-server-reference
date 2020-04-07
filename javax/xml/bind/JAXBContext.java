// 
// Decompiled by Procyon v0.5.30
// 

package javax.xml.bind;

import java.io.IOException;
import org.w3c.dom.Node;
import java.util.Map;
import java.util.Collections;

public abstract class JAXBContext
{
    public static final String JAXB_CONTEXT_FACTORY = "javax.xml.bind.context.factory";
    
    public static JAXBContext newInstance(final String contextPath) throws JAXBException {
        return newInstance(contextPath, Thread.currentThread().getContextClassLoader());
    }
    
    public static JAXBContext newInstance(final String contextPath, final ClassLoader classLoader) throws JAXBException {
        return newInstance(contextPath, classLoader, Collections.emptyMap());
    }
    
    public static JAXBContext newInstance(final String contextPath, final ClassLoader classLoader, final Map<String, ?> properties) throws JAXBException {
        return ContextFinder.find("javax.xml.bind.context.factory", contextPath, classLoader, properties);
    }
    
    public static JAXBContext newInstance(final Class... classesToBeBound) throws JAXBException {
        return newInstance(classesToBeBound, Collections.emptyMap());
    }
    
    public static JAXBContext newInstance(final Class[] classesToBeBound, final Map<String, ?> properties) throws JAXBException {
        for (int i = classesToBeBound.length - 1; i >= 0; --i) {
            if (classesToBeBound[i] == null) {
                throw new IllegalArgumentException();
            }
        }
        return ContextFinder.find(classesToBeBound, properties);
    }
    
    public abstract Unmarshaller createUnmarshaller() throws JAXBException;
    
    public abstract Marshaller createMarshaller() throws JAXBException;
    
    public abstract Validator createValidator() throws JAXBException;
    
    public <T> Binder<T> createBinder(final Class<T> domType) {
        throw new UnsupportedOperationException();
    }
    
    public Binder<Node> createBinder() {
        return this.createBinder(Node.class);
    }
    
    public JAXBIntrospector createJAXBIntrospector() {
        throw new UnsupportedOperationException();
    }
    
    public void generateSchema(final SchemaOutputResolver outputResolver) throws IOException {
        throw new UnsupportedOperationException();
    }
}
