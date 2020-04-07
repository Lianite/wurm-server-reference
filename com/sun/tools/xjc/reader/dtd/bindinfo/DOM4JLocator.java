// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.tools.xjc.reader.dtd.bindinfo;

import org.dom4j.QName;
import org.xml.sax.Locator;
import org.dom4j.Element;

class DOM4JLocator
{
    private static final String locationNamespace = "http://www.sun.com/xmlns/jaxb/dom4j-location";
    private static final String systemId = "systemid";
    private static final String column = "column";
    private static final String line = "line";
    
    public static void setLocationInfo(final Element e, final Locator loc) {
        e.addAttribute(QName.get("systemid", "http://www.sun.com/xmlns/jaxb/dom4j-location"), loc.getSystemId());
        e.addAttribute(QName.get("column", "http://www.sun.com/xmlns/jaxb/dom4j-location"), Integer.toString(loc.getLineNumber()));
        e.addAttribute(QName.get("line", "http://www.sun.com/xmlns/jaxb/dom4j-location"), Integer.toString(loc.getColumnNumber()));
    }
    
    public static Locator getLocationInfo(final Element e) {
        if (e.attribute(QName.get("systemid", "http://www.sun.com/xmlns/jaxb/dom4j-location")) == null) {
            return null;
        }
        return (Locator)new DOM4JLocator$1(e);
    }
}
