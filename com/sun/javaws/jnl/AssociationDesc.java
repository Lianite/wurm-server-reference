// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.javaws.jnl;

import com.sun.deploy.xml.XMLNodeBuilder;
import com.sun.deploy.xml.XMLAttributeBuilder;
import com.sun.deploy.xml.XMLNode;
import com.sun.deploy.xml.XMLable;

public class AssociationDesc implements XMLable
{
    private String _extensions;
    private String _mimeType;
    
    public AssociationDesc(final String extensions, final String mimeType) {
        this._extensions = extensions;
        this._mimeType = mimeType;
    }
    
    public String getExtensions() {
        return this._extensions;
    }
    
    public String getMimeType() {
        return this._mimeType;
    }
    
    public XMLNode asXML() {
        final XMLAttributeBuilder xmlAttributeBuilder = new XMLAttributeBuilder();
        xmlAttributeBuilder.add("extensions", this._extensions);
        xmlAttributeBuilder.add("mime-type", this._mimeType);
        return new XMLNodeBuilder("association", xmlAttributeBuilder.getAttributeList()).getNode();
    }
}
