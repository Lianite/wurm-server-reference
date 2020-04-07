// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.tools.xjc.reader.xmlschema.bindinfo.parser;

import org.xml.sax.SAXException;
import org.xml.sax.Attributes;

public interface NGCCEventReceiver
{
    void enterElement(final String p0, final String p1, final String p2, final Attributes p3) throws SAXException;
    
    void leaveElement(final String p0, final String p1, final String p2) throws SAXException;
    
    void text(final String p0) throws SAXException;
    
    void enterAttribute(final String p0, final String p1, final String p2) throws SAXException;
    
    void leaveAttribute(final String p0, final String p1, final String p2) throws SAXException;
}
