// 
// Decompiled by Procyon v0.5.30
// 

package javax.xml.stream.util;

import javax.xml.stream.Location;
import javax.xml.namespace.QName;
import javax.xml.namespace.NamespaceContext;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

public class StreamReaderDelegate implements XMLStreamReader
{
    private XMLStreamReader reader;
    
    public StreamReaderDelegate() {
    }
    
    public StreamReaderDelegate(final XMLStreamReader reader) {
        this.reader = reader;
    }
    
    public void setParent(final XMLStreamReader reader) {
        this.reader = reader;
    }
    
    public XMLStreamReader getParent() {
        return this.reader;
    }
    
    public int next() throws XMLStreamException {
        return this.reader.next();
    }
    
    public int nextTag() throws XMLStreamException {
        return this.reader.nextTag();
    }
    
    public String getElementText() throws XMLStreamException {
        return this.reader.getElementText();
    }
    
    public void require(final int type, final String namespaceURI, final String localName) throws XMLStreamException {
        this.reader.require(type, namespaceURI, localName);
    }
    
    public boolean hasNext() throws XMLStreamException {
        return this.reader.hasNext();
    }
    
    public void close() throws XMLStreamException {
        this.reader.close();
    }
    
    public String getNamespaceURI(final String prefix) {
        return this.reader.getNamespaceURI(prefix);
    }
    
    public NamespaceContext getNamespaceContext() {
        return this.reader.getNamespaceContext();
    }
    
    public boolean isStartElement() {
        return this.reader.isStartElement();
    }
    
    public boolean isEndElement() {
        return this.reader.isEndElement();
    }
    
    public boolean isCharacters() {
        return this.reader.isCharacters();
    }
    
    public boolean isWhiteSpace() {
        return this.reader.isWhiteSpace();
    }
    
    public String getAttributeValue(final String namespaceUri, final String localName) {
        return this.reader.getAttributeValue(namespaceUri, localName);
    }
    
    public int getAttributeCount() {
        return this.reader.getAttributeCount();
    }
    
    public QName getAttributeName(final int index) {
        return this.reader.getAttributeName(index);
    }
    
    public String getAttributePrefix(final int index) {
        return this.reader.getAttributePrefix(index);
    }
    
    public String getAttributeNamespace(final int index) {
        return this.reader.getAttributeNamespace(index);
    }
    
    public String getAttributeLocalName(final int index) {
        return this.reader.getAttributeLocalName(index);
    }
    
    public String getAttributeType(final int index) {
        return this.reader.getAttributeType(index);
    }
    
    public String getAttributeValue(final int index) {
        return this.reader.getAttributeValue(index);
    }
    
    public boolean isAttributeSpecified(final int index) {
        return this.reader.isAttributeSpecified(index);
    }
    
    public int getNamespaceCount() {
        return this.reader.getNamespaceCount();
    }
    
    public String getNamespacePrefix(final int index) {
        return this.reader.getNamespacePrefix(index);
    }
    
    public String getNamespaceURI(final int index) {
        return this.reader.getNamespaceURI(index);
    }
    
    public int getEventType() {
        return this.reader.getEventType();
    }
    
    public String getText() {
        return this.reader.getText();
    }
    
    public int getTextCharacters(final int sourceStart, final char[] target, final int targetStart, final int length) throws XMLStreamException {
        return this.reader.getTextCharacters(sourceStart, target, targetStart, length);
    }
    
    public char[] getTextCharacters() {
        return this.reader.getTextCharacters();
    }
    
    public int getTextStart() {
        return this.reader.getTextStart();
    }
    
    public int getTextLength() {
        return this.reader.getTextLength();
    }
    
    public String getEncoding() {
        return this.reader.getEncoding();
    }
    
    public boolean hasText() {
        return this.reader.hasText();
    }
    
    public Location getLocation() {
        return this.reader.getLocation();
    }
    
    public QName getName() {
        return this.reader.getName();
    }
    
    public String getLocalName() {
        return this.reader.getLocalName();
    }
    
    public boolean hasName() {
        return this.reader.hasName();
    }
    
    public String getNamespaceURI() {
        return this.reader.getNamespaceURI();
    }
    
    public String getPrefix() {
        return this.reader.getPrefix();
    }
    
    public String getVersion() {
        return this.reader.getVersion();
    }
    
    public boolean isStandalone() {
        return this.reader.isStandalone();
    }
    
    public boolean standaloneSet() {
        return this.reader.standaloneSet();
    }
    
    public String getCharacterEncodingScheme() {
        return this.reader.getCharacterEncodingScheme();
    }
    
    public String getPITarget() {
        return this.reader.getPITarget();
    }
    
    public String getPIData() {
        return this.reader.getPIData();
    }
    
    public Object getProperty(final String name) {
        return this.reader.getProperty(name);
    }
}
