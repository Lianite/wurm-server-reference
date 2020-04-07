// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.javaws.jnl;

import com.sun.deploy.xml.XMLAttribute;
import com.sun.deploy.xml.XMLNode;

public class PropertyDesc implements ResourceType
{
    private String _key;
    private String _value;
    
    public PropertyDesc(final String key, final String value) {
        this._key = key;
        this._value = value;
    }
    
    String getKey() {
        return this._key;
    }
    
    String getValue() {
        return this._value;
    }
    
    public void visit(final ResourceVisitor resourceVisitor) {
        resourceVisitor.visitPropertyDesc(this);
    }
    
    public XMLNode asXML() {
        return new XMLNode("property", new XMLAttribute("name", this.getKey(), new XMLAttribute("value", this.getValue())), (XMLNode)null, (XMLNode)null);
    }
}
