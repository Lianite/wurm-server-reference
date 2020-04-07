// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.javaws.jnl;

import com.sun.deploy.xml.XMLAttributeBuilder;
import com.sun.deploy.xml.XMLNode;
import com.sun.javaws.Globals;
import com.sun.deploy.xml.XMLable;

public class ExtDownloadDesc implements XMLable
{
    private String _extensionPart;
    private String _part;
    private boolean _isLazy;
    
    public ExtDownloadDesc(final String extensionPart, final String part, final boolean b) {
        this._extensionPart = extensionPart;
        this._part = part;
        this._isLazy = (b && !Globals.isImportMode());
    }
    
    public String getExtensionPart() {
        return this._extensionPart;
    }
    
    public String getPart() {
        return this._part;
    }
    
    public boolean isLazy() {
        return this._isLazy;
    }
    
    public XMLNode asXML() {
        final XMLAttributeBuilder xmlAttributeBuilder = new XMLAttributeBuilder();
        xmlAttributeBuilder.add("ext-part", this._extensionPart);
        xmlAttributeBuilder.add("part", this._part);
        xmlAttributeBuilder.add("download", this._isLazy ? "lazy" : "eager");
        return new XMLNode("ext-download", xmlAttributeBuilder.getAttributeList());
    }
}
