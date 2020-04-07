// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.tools.xjc.reader.internalizer;

import org.xml.sax.SAXException;
import org.w3c.dom.Element;
import org.xml.sax.XMLFilter;

public interface InternalizationLogic
{
    XMLFilter createExternalReferenceFinder(final DOMForest p0);
    
    boolean checkIfValidTargetNode(final DOMForest p0, final Element p1, final Element p2) throws SAXException;
    
    Element refineTarget(final Element p0);
}
