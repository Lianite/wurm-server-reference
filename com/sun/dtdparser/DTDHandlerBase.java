// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.dtdparser;

import org.xml.sax.SAXParseException;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;

public class DTDHandlerBase implements DTDEventListener
{
    public void processingInstruction(final String target, final String data) throws SAXException {
    }
    
    public void setDocumentLocator(final Locator loc) {
    }
    
    public void fatalError(final SAXParseException e) throws SAXException {
        throw e;
    }
    
    public void error(final SAXParseException e) throws SAXException {
        throw e;
    }
    
    public void warning(final SAXParseException err) throws SAXException {
    }
    
    public void notationDecl(final String name, final String publicId, final String systemId) throws SAXException {
    }
    
    public void unparsedEntityDecl(final String name, final String publicId, final String systemId, final String notationName) throws SAXException {
    }
    
    public void endDTD() throws SAXException {
    }
    
    public void externalGeneralEntityDecl(final String n, final String p, final String s) throws SAXException {
    }
    
    public void internalGeneralEntityDecl(final String n, final String v) throws SAXException {
    }
    
    public void externalParameterEntityDecl(final String n, final String p, final String s) throws SAXException {
    }
    
    public void internalParameterEntityDecl(final String n, final String v) throws SAXException {
    }
    
    public void startDTD(final InputEntity in) throws SAXException {
    }
    
    public void comment(final String n) throws SAXException {
    }
    
    public void characters(final char[] ch, final int start, final int length) throws SAXException {
    }
    
    public void ignorableWhitespace(final char[] ch, final int start, final int length) throws SAXException {
    }
    
    public void startCDATA() throws SAXException {
    }
    
    public void endCDATA() throws SAXException {
    }
    
    public void startContentModel(final String elementName, final short contentModelType) throws SAXException {
    }
    
    public void endContentModel(final String elementName, final short contentModelType) throws SAXException {
    }
    
    public void attributeDecl(final String elementName, final String attributeName, final String attributeType, final String[] enumeration, final short attributeUse, final String defaultValue) throws SAXException {
    }
    
    public void childElement(final String elementName, final short occurence) throws SAXException {
    }
    
    public void mixedElement(final String elementName) throws SAXException {
    }
    
    public void startModelGroup() throws SAXException {
    }
    
    public void endModelGroup(final short occurence) throws SAXException {
    }
    
    public void connector(final short connectorType) throws SAXException {
    }
}
