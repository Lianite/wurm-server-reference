// 
// Decompiled by Procyon v0.5.30
// 

package javax.xml.stream;

import javax.xml.namespace.NamespaceContext;
import javax.xml.namespace.QName;

public interface XMLStreamReader extends XMLStreamConstants
{
    Object getProperty(final String p0) throws IllegalArgumentException;
    
    int next() throws XMLStreamException;
    
    void require(final int p0, final String p1, final String p2) throws XMLStreamException;
    
    String getElementText() throws XMLStreamException;
    
    int nextTag() throws XMLStreamException;
    
    boolean hasNext() throws XMLStreamException;
    
    void close() throws XMLStreamException;
    
    String getNamespaceURI(final String p0);
    
    boolean isStartElement();
    
    boolean isEndElement();
    
    boolean isCharacters();
    
    boolean isWhiteSpace();
    
    String getAttributeValue(final String p0, final String p1);
    
    int getAttributeCount();
    
    QName getAttributeName(final int p0);
    
    String getAttributeNamespace(final int p0);
    
    String getAttributeLocalName(final int p0);
    
    String getAttributePrefix(final int p0);
    
    String getAttributeType(final int p0);
    
    String getAttributeValue(final int p0);
    
    boolean isAttributeSpecified(final int p0);
    
    int getNamespaceCount();
    
    String getNamespacePrefix(final int p0);
    
    String getNamespaceURI(final int p0);
    
    NamespaceContext getNamespaceContext();
    
    int getEventType();
    
    String getText();
    
    char[] getTextCharacters();
    
    int getTextCharacters(final int p0, final char[] p1, final int p2, final int p3) throws XMLStreamException;
    
    int getTextStart();
    
    int getTextLength();
    
    String getEncoding();
    
    boolean hasText();
    
    Location getLocation();
    
    QName getName();
    
    String getLocalName();
    
    boolean hasName();
    
    String getNamespaceURI();
    
    String getPrefix();
    
    String getVersion();
    
    boolean isStandalone();
    
    boolean standaloneSet();
    
    String getCharacterEncodingScheme();
    
    String getPITarget();
    
    String getPIData();
}
