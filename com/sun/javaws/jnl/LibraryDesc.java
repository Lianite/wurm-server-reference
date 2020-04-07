// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.javaws.jnl;

import com.sun.deploy.xml.XMLAttribute;
import com.sun.deploy.xml.XMLNode;
import com.sun.deploy.xml.XMLable;

public class LibraryDesc implements XMLable
{
    private String _uniqueId;
    
    public LibraryDesc(final String uniqueId) {
        this._uniqueId = uniqueId;
    }
    
    public String getUniqueId() {
        return this._uniqueId;
    }
    
    public XMLNode asXML() {
        return new XMLNode("library-desc", new XMLAttribute("unique-id", this._uniqueId));
    }
}
