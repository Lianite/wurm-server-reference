// 
// Decompiled by Procyon v0.5.30
// 

package javax.xml.stream;

import javax.xml.namespace.NamespaceContext;
import javax.xml.stream.events.XMLEvent;
import javax.xml.stream.util.XMLEventConsumer;

public interface XMLEventWriter extends XMLEventConsumer
{
    void flush() throws XMLStreamException;
    
    void close() throws XMLStreamException;
    
    void add(final XMLEvent p0) throws XMLStreamException;
    
    void add(final XMLEventReader p0) throws XMLStreamException;
    
    String getPrefix(final String p0) throws XMLStreamException;
    
    void setPrefix(final String p0, final String p1) throws XMLStreamException;
    
    void setDefaultNamespace(final String p0) throws XMLStreamException;
    
    void setNamespaceContext(final NamespaceContext p0) throws XMLStreamException;
    
    NamespaceContext getNamespaceContext();
}
