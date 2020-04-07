// 
// Decompiled by Procyon v0.5.30
// 

package org.apache.xml.resolver.tools;

import javax.xml.parsers.SAXParserFactory;

public class ResolvingXMLReader extends ResolvingXMLFilter
{
    public ResolvingXMLReader() {
        final SAXParserFactory instance = SAXParserFactory.newInstance();
        try {
            this.setParent(instance.newSAXParser().getXMLReader());
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
