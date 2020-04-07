// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.tools.xjc.reader.xmlschema.bindinfo;

import javax.xml.namespace.QName;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "dom")
public class BIDom extends AbstractDeclarationImpl
{
    @XmlAttribute
    String type;
    public static final QName NAME;
    
    public final QName getName() {
        return BIDom.NAME;
    }
    
    static {
        NAME = new QName("http://java.sun.com/xml/ns/jaxb", "dom");
    }
}
