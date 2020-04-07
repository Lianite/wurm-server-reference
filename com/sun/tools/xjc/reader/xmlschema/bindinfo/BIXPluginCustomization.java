// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.tools.xjc.reader.xmlschema.bindinfo;

import com.sun.tools.xjc.reader.Ring;
import com.sun.tools.xjc.model.Model;
import org.xml.sax.Locator;
import javax.xml.namespace.QName;
import org.w3c.dom.Element;

public final class BIXPluginCustomization extends AbstractDeclarationImpl
{
    public final Element element;
    private QName name;
    
    public BIXPluginCustomization(final Element e, final Locator _loc) {
        super(_loc);
        this.element = e;
    }
    
    public void onSetOwner() {
        super.onSetOwner();
        if (!Ring.get(Model.class).options.pluginURIs.contains(this.element.getNamespaceURI())) {
            this.markAsAcknowledged();
        }
    }
    
    public final QName getName() {
        if (this.name == null) {
            this.name = new QName(this.element.getNamespaceURI(), this.element.getLocalName());
        }
        return this.name;
    }
}
