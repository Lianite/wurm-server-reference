// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.shared.xml;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import java.io.IOException;
import org.xml.sax.XMLReader;
import org.xml.sax.InputSource;
import org.xml.sax.ContentHandler;
import org.xml.sax.helpers.XMLReaderFactory;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import org.xml.sax.helpers.DefaultHandler;

public final class XmlParser extends DefaultHandler
{
    private final List<XmlNode> nodeStack;
    private XmlNode rootNode;
    
    private XmlParser() {
        this.nodeStack = new ArrayList<XmlNode>();
        this.rootNode = null;
    }
    
    public static XmlNode parse(final InputStream in) throws IOException, SAXException {
        final XMLReader xmlReader = XMLReaderFactory.createXMLReader();
        final XmlParser xmlParser = new XmlParser();
        xmlReader.setContentHandler(xmlParser);
        xmlReader.parse(new InputSource(in));
        return xmlParser.rootNode;
    }
    
    @Override
    public void characters(final char[] ch, final int start, final int length) throws SAXException {
        if (this.nodeStack.size() > 0) {
            this.nodeStack.get(this.nodeStack.size() - 1).setText(new String(ch, start, length));
        }
    }
    
    @Override
    public void endElement(final String uri, final String localName, final String qName) throws SAXException {
        if (this.nodeStack.size() > 0) {
            this.nodeStack.remove(this.nodeStack.size() - 1);
        }
    }
    
    @Override
    public void startElement(final String uri, final String localName, final String qName, final Attributes attributes) throws SAXException {
        final XmlNode xmlNode = new XmlNode(localName, attributes);
        if (this.rootNode == null) {
            this.rootNode = xmlNode;
        }
        if (this.nodeStack.size() > 0) {
            this.nodeStack.get(this.nodeStack.size() - 1).addChild(xmlNode);
        }
        this.nodeStack.add(xmlNode);
    }
    
    static {
        try {
            XMLReaderFactory.createXMLReader();
        }
        catch (Exception e2) {
            System.out.println("Failed to load default xml reader.. attempting org.apache.crimson.parser.XMLReaderImpl");
            System.setProperty("org.xml.sax.driver", "org.apache.crimson.parser.XMLReaderImpl");
            try {
                XMLReaderFactory.createXMLReader();
            }
            catch (SAXException e1) {
                System.out.println("Failed to create XMLReader!!");
                e1.printStackTrace();
            }
        }
    }
}
