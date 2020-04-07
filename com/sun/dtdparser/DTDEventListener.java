// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.dtdparser;

import org.xml.sax.SAXParseException;
import org.xml.sax.SAXException;
import org.xml.sax.Locator;
import java.util.EventListener;

public interface DTDEventListener extends EventListener
{
    public static final short CONTENT_MODEL_EMPTY = 0;
    public static final short CONTENT_MODEL_ANY = 1;
    public static final short CONTENT_MODEL_MIXED = 2;
    public static final short CONTENT_MODEL_CHILDREN = 3;
    public static final short USE_NORMAL = 0;
    public static final short USE_IMPLIED = 1;
    public static final short USE_FIXED = 2;
    public static final short USE_REQUIRED = 3;
    public static final short CHOICE = 0;
    public static final short SEQUENCE = 1;
    public static final short OCCURENCE_ZERO_OR_MORE = 0;
    public static final short OCCURENCE_ONE_OR_MORE = 1;
    public static final short OCCURENCE_ZERO_OR_ONE = 2;
    public static final short OCCURENCE_ONCE = 3;
    
    void setDocumentLocator(final Locator p0);
    
    void processingInstruction(final String p0, final String p1) throws SAXException;
    
    void notationDecl(final String p0, final String p1, final String p2) throws SAXException;
    
    void unparsedEntityDecl(final String p0, final String p1, final String p2, final String p3) throws SAXException;
    
    void internalGeneralEntityDecl(final String p0, final String p1) throws SAXException;
    
    void externalGeneralEntityDecl(final String p0, final String p1, final String p2) throws SAXException;
    
    void internalParameterEntityDecl(final String p0, final String p1) throws SAXException;
    
    void externalParameterEntityDecl(final String p0, final String p1, final String p2) throws SAXException;
    
    void startDTD(final InputEntity p0) throws SAXException;
    
    void endDTD() throws SAXException;
    
    void comment(final String p0) throws SAXException;
    
    void characters(final char[] p0, final int p1, final int p2) throws SAXException;
    
    void ignorableWhitespace(final char[] p0, final int p1, final int p2) throws SAXException;
    
    void startCDATA() throws SAXException;
    
    void endCDATA() throws SAXException;
    
    void fatalError(final SAXParseException p0) throws SAXException;
    
    void error(final SAXParseException p0) throws SAXException;
    
    void warning(final SAXParseException p0) throws SAXException;
    
    void startContentModel(final String p0, final short p1) throws SAXException;
    
    void endContentModel(final String p0, final short p1) throws SAXException;
    
    void attributeDecl(final String p0, final String p1, final String p2, final String[] p3, final short p4, final String p5) throws SAXException;
    
    void childElement(final String p0, final short p1) throws SAXException;
    
    void mixedElement(final String p0) throws SAXException;
    
    void startModelGroup() throws SAXException;
    
    void endModelGroup(final short p0) throws SAXException;
    
    void connector(final short p0) throws SAXException;
}
