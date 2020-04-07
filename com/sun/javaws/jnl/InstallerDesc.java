// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.javaws.jnl;

import com.sun.deploy.xml.XMLNodeBuilder;
import com.sun.deploy.xml.XMLAttributeBuilder;
import com.sun.deploy.xml.XMLNode;
import com.sun.deploy.xml.XMLable;

public class InstallerDesc implements XMLable
{
    private String _mainClass;
    
    public InstallerDesc(final String mainClass) {
        this._mainClass = mainClass;
    }
    
    public String getMainClass() {
        return this._mainClass;
    }
    
    public XMLNode asXML() {
        final XMLAttributeBuilder xmlAttributeBuilder = new XMLAttributeBuilder();
        xmlAttributeBuilder.add("main-class", this._mainClass);
        return new XMLNodeBuilder("installer-desc", xmlAttributeBuilder.getAttributeList()).getNode();
    }
}
