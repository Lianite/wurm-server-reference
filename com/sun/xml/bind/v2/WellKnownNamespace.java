// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.xml.bind.v2;

public abstract class WellKnownNamespace
{
    public static final String XML_SCHEMA = "http://www.w3.org/2001/XMLSchema";
    public static final String XML_SCHEMA_INSTANCE = "http://www.w3.org/2001/XMLSchema-instance";
    public static final Object XML_SCHEMA_DATATYPES;
    public static final String XML_NAMESPACE_URI = "http://www.w3.org/XML/1998/namespace";
    public static final String XOP = "http://www.w3.org/2004/08/xop/include";
    public static final String SWA_URI = "http://ws-i.org/profiles/basic/1.1/xsd";
    public static final String XML_MIME_URI = "http://www.w3.org/2005/05/xmlmime";
    public static final String JAXB = "http://java.sun.com/xml/ns/jaxb";
    
    static {
        XML_SCHEMA_DATATYPES = "http://www.w3.org/2001/XMLSchema-datatypes";
    }
}
