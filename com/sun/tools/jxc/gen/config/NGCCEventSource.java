// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.tools.jxc.gen.config;

import org.xml.sax.SAXException;
import org.xml.sax.Attributes;

public interface NGCCEventSource
{
    int replace(final NGCCEventReceiver p0, final NGCCEventReceiver p1);
    
    void sendEnterElement(final int p0, final String p1, final String p2, final String p3, final Attributes p4) throws SAXException;
    
    void sendLeaveElement(final int p0, final String p1, final String p2, final String p3) throws SAXException;
    
    void sendEnterAttribute(final int p0, final String p1, final String p2, final String p3) throws SAXException;
    
    void sendLeaveAttribute(final int p0, final String p1, final String p2, final String p3) throws SAXException;
    
    void sendText(final int p0, final String p1) throws SAXException;
}
