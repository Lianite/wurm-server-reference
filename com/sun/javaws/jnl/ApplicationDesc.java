// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.javaws.jnl;

import com.sun.deploy.xml.XMLAttribute;
import com.sun.deploy.xml.XMLNodeBuilder;
import com.sun.deploy.xml.XMLAttributeBuilder;
import com.sun.deploy.xml.XMLNode;
import com.sun.deploy.xml.XMLable;

public class ApplicationDesc implements XMLable
{
    private String _mainClass;
    private String[] _arguments;
    
    public ApplicationDesc(final String mainClass, final String[] arguments) {
        this._mainClass = mainClass;
        this._arguments = arguments;
    }
    
    public String getMainClass() {
        return this._mainClass;
    }
    
    public String[] getArguments() {
        return this._arguments;
    }
    
    public void setArguments(final String[] arguments) {
        this._arguments = arguments;
    }
    
    public XMLNode asXML() {
        final XMLAttributeBuilder xmlAttributeBuilder = new XMLAttributeBuilder();
        xmlAttributeBuilder.add("main-class", this._mainClass);
        final XMLNodeBuilder xmlNodeBuilder = new XMLNodeBuilder("application-desc", xmlAttributeBuilder.getAttributeList());
        if (this._arguments != null) {
            for (int i = 0; i < this._arguments.length; ++i) {
                xmlNodeBuilder.add(new XMLNode("argument", (XMLAttribute)null, new XMLNode(this._arguments[i]), (XMLNode)null));
            }
        }
        return xmlNodeBuilder.getNode();
    }
}
