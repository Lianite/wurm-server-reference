// 
// Decompiled by Procyon v0.5.30
// 

package javax.xml.stream;

import javax.xml.transform.Result;
import java.io.OutputStream;
import java.io.Writer;

public abstract class XMLOutputFactory
{
    public static final String IS_REPAIRING_NAMESPACES = "javax.xml.stream.isRepairingNamespaces";
    
    public static XMLOutputFactory newInstance() throws FactoryConfigurationError {
        return (XMLOutputFactory)FactoryFinder.find("javax.xml.stream.XMLOutputFactory", "com.bea.xml.stream.XMLOutputFactoryBase");
    }
    
    public static XMLInputFactory newInstance(final String factoryId, final ClassLoader classLoader) throws FactoryConfigurationError {
        return (XMLInputFactory)FactoryFinder.find(factoryId, "com.bea.xml.stream.XMLInputFactoryBase", classLoader);
    }
    
    public abstract XMLStreamWriter createXMLStreamWriter(final Writer p0) throws XMLStreamException;
    
    public abstract XMLStreamWriter createXMLStreamWriter(final OutputStream p0) throws XMLStreamException;
    
    public abstract XMLStreamWriter createXMLStreamWriter(final OutputStream p0, final String p1) throws XMLStreamException;
    
    public abstract XMLStreamWriter createXMLStreamWriter(final Result p0) throws XMLStreamException;
    
    public abstract XMLEventWriter createXMLEventWriter(final Result p0) throws XMLStreamException;
    
    public abstract XMLEventWriter createXMLEventWriter(final OutputStream p0) throws XMLStreamException;
    
    public abstract XMLEventWriter createXMLEventWriter(final OutputStream p0, final String p1) throws XMLStreamException;
    
    public abstract XMLEventWriter createXMLEventWriter(final Writer p0) throws XMLStreamException;
    
    public abstract void setProperty(final String p0, final Object p1) throws IllegalArgumentException;
    
    public abstract Object getProperty(final String p0) throws IllegalArgumentException;
    
    public abstract boolean isPropertySupported(final String p0);
}
