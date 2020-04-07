// 
// Decompiled by Procyon v0.5.30
// 

package org.kohsuke.rngom.xml.sax;

import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

public interface XMLReaderCreator
{
    XMLReader createXMLReader() throws SAXException;
}
