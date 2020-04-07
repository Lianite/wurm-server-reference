// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.org.apache.xml.internal.resolver.tools;

import com.sun.org.apache.xml.internal.resolver.CatalogManager;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

public class ResolvingXMLReader extends ResolvingXMLFilter
{
    public static boolean namespaceAware;
    public static boolean validating;
    
    public ResolvingXMLReader() {
        final SAXParserFactory spf = SAXParserFactory.newInstance();
        spf.setNamespaceAware(ResolvingXMLReader.namespaceAware);
        spf.setValidating(ResolvingXMLReader.validating);
        try {
            final SAXParser parser = spf.newSAXParser();
            this.setParent(parser.getXMLReader());
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    
    public ResolvingXMLReader(final CatalogManager manager) {
        super(manager);
        final SAXParserFactory spf = SAXParserFactory.newInstance();
        spf.setNamespaceAware(ResolvingXMLReader.namespaceAware);
        spf.setValidating(ResolvingXMLReader.validating);
        try {
            final SAXParser parser = spf.newSAXParser();
            this.setParent(parser.getXMLReader());
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    
    static {
        ResolvingXMLReader.namespaceAware = true;
        ResolvingXMLReader.validating = false;
    }
}
