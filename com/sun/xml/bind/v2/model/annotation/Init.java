// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.xml.bind.v2.model.annotation;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlValue;
import javax.xml.bind.annotation.XmlElementRefs;
import javax.xml.bind.annotation.XmlSchema;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlRootElement;

class Init
{
    static Quick[] getAll() {
        return new Quick[] { new XmlRootElementQuick(null, null), new XmlTypeQuick(null, null), new XmlSchemaTypeQuick(null, null), new XmlElementRefQuick(null, null), new XmlElementDeclQuick(null, null), new XmlAttributeQuick(null, null), new XmlSchemaQuick(null, null), new XmlElementRefsQuick(null, null), new XmlValueQuick(null, null), new XmlTransientQuick(null, null), new XmlElementQuick(null, null), new XmlEnumQuick(null, null) };
    }
}
