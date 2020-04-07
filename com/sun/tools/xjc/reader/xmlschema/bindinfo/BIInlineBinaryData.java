// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.tools.xjc.reader.xmlschema.bindinfo;

import com.sun.tools.xjc.reader.Ring;
import com.sun.tools.xjc.reader.xmlschema.BGMBuilder;
import com.sun.tools.xjc.model.CPropertyInfo;
import com.sun.xml.xsom.XSComponent;
import javax.xml.namespace.QName;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "inlineBinaryData")
public class BIInlineBinaryData extends AbstractDeclarationImpl
{
    public static final QName NAME;
    
    public static void handle(final XSComponent source, final CPropertyInfo prop) {
        final BIInlineBinaryData inline = Ring.get(BGMBuilder.class).getBindInfo(source).get(BIInlineBinaryData.class);
        if (inline != null) {
            prop.inlineBinaryData = true;
            inline.markAsAcknowledged();
        }
    }
    
    public final QName getName() {
        return BIInlineBinaryData.NAME;
    }
    
    static {
        NAME = new QName("http://java.sun.com/xml/ns/jaxb", "inlineBinaryData");
    }
}
