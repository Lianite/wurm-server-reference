// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.tools.xjc.reader.dtd.bindinfo;

import org.xml.sax.Locator;
import org.w3c.dom.Element;

class DOMLocator
{
    private static final String locationNamespace = "http://www.sun.com/xmlns/jaxb/dom-location";
    private static final String systemId = "systemid";
    private static final String column = "column";
    private static final String line = "line";
    
    public static void setLocationInfo(final Element e, final Locator loc) {
        e.setAttributeNS("http://www.sun.com/xmlns/jaxb/dom-location", "loc:systemid", loc.getSystemId());
        e.setAttributeNS("http://www.sun.com/xmlns/jaxb/dom-location", "loc:column", Integer.toString(loc.getLineNumber()));
        e.setAttributeNS("http://www.sun.com/xmlns/jaxb/dom-location", "loc:line", Integer.toString(loc.getColumnNumber()));
    }
    
    public static Locator getLocationInfo(final Element e) {
        if (DOMUtil.getAttribute(e, "http://www.sun.com/xmlns/jaxb/dom-location", "systemid") == null) {
            return null;
        }
        return new Locator() {
            public int getLineNumber() {
                return Integer.parseInt(DOMUtil.getAttribute(e, "http://www.sun.com/xmlns/jaxb/dom-location", "line"));
            }
            
            public int getColumnNumber() {
                return Integer.parseInt(DOMUtil.getAttribute(e, "http://www.sun.com/xmlns/jaxb/dom-location", "column"));
            }
            
            public String getSystemId() {
                return DOMUtil.getAttribute(e, "http://www.sun.com/xmlns/jaxb/dom-location", "systemid");
            }
            
            public String getPublicId() {
                return null;
            }
        };
    }
}
