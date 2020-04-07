// 
// Decompiled by Procyon v0.5.30
// 

package javax.xml.stream.util;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.XMLEvent;
import javax.xml.stream.XMLStreamReader;

public interface XMLEventAllocator
{
    XMLEventAllocator newInstance();
    
    XMLEvent allocate(final XMLStreamReader p0) throws XMLStreamException;
    
    void allocate(final XMLStreamReader p0, final XMLEventConsumer p1) throws XMLStreamException;
}
