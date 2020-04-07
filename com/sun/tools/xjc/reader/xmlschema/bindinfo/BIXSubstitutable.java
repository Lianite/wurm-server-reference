// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.tools.xjc.reader.xmlschema.bindinfo;

import javax.xml.namespace.QName;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "substitutable", namespace = "http://java.sun.com/xml/ns/jaxb/xjc")
public final class BIXSubstitutable extends AbstractDeclarationImpl
{
    public static final QName NAME;
    
    public final QName getName() {
        return BIXSubstitutable.NAME;
    }
    
    static {
        NAME = new QName("http://java.sun.com/xml/ns/jaxb/xjc", "substitutable");
    }
}
