// 
// Decompiled by Procyon v0.5.30
// 

package javax.xml.stream.events;

import javax.xml.stream.XMLStreamException;
import java.io.Writer;
import javax.xml.namespace.QName;
import javax.xml.stream.Location;
import javax.xml.stream.XMLStreamConstants;

public interface XMLEvent extends XMLStreamConstants
{
    int getEventType();
    
    Location getLocation();
    
    boolean isStartElement();
    
    boolean isAttribute();
    
    boolean isNamespace();
    
    boolean isEndElement();
    
    boolean isEntityReference();
    
    boolean isProcessingInstruction();
    
    boolean isCharacters();
    
    boolean isStartDocument();
    
    boolean isEndDocument();
    
    StartElement asStartElement();
    
    EndElement asEndElement();
    
    Characters asCharacters();
    
    QName getSchemaType();
    
    void writeAsEncodedUnicode(final Writer p0) throws XMLStreamException;
}
