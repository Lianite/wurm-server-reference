// 
// Decompiled by Procyon v0.5.30
// 

package javax.xml.stream;

import javax.xml.stream.events.XMLEvent;
import java.util.Iterator;

public interface XMLEventReader extends Iterator
{
    XMLEvent nextEvent() throws XMLStreamException;
    
    boolean hasNext();
    
    XMLEvent peek() throws XMLStreamException;
    
    String getElementText() throws XMLStreamException;
    
    XMLEvent nextTag() throws XMLStreamException;
    
    Object getProperty(final String p0) throws IllegalArgumentException;
    
    void close() throws XMLStreamException;
}
