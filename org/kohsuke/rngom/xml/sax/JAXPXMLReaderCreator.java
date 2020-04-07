// 
// Decompiled by Procyon v0.5.30
// 

package org.kohsuke.rngom.xml.sax;

import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import javax.xml.parsers.SAXParserFactory;

public class JAXPXMLReaderCreator implements XMLReaderCreator
{
    private final SAXParserFactory spf;
    
    public JAXPXMLReaderCreator(final SAXParserFactory spf) {
        this.spf = spf;
    }
    
    public JAXPXMLReaderCreator() {
        (this.spf = SAXParserFactory.newInstance()).setNamespaceAware(true);
    }
    
    public XMLReader createXMLReader() throws SAXException {
        try {
            return this.spf.newSAXParser().getXMLReader();
        }
        catch (ParserConfigurationException e) {
            throw new SAXException(e);
        }
    }
}
