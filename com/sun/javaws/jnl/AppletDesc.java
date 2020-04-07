// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.javaws.jnl;

import java.util.Hashtable;
import java.util.Enumeration;
import com.sun.deploy.xml.XMLAttribute;
import com.sun.deploy.xml.XMLNodeBuilder;
import com.sun.deploy.xml.XMLAttributeBuilder;
import com.sun.deploy.xml.XMLNode;
import java.util.Properties;
import java.net.URL;
import com.sun.deploy.xml.XMLable;

public class AppletDesc implements XMLable
{
    private String _name;
    private String _appletClass;
    private URL _documentBase;
    private int _width;
    private int _height;
    private Properties _params;
    
    public AppletDesc(final String name, final String appletClass, final URL documentBase, final int width, final int height, final Properties params) {
        this._name = name;
        this._appletClass = appletClass;
        this._documentBase = documentBase;
        this._width = width;
        this._height = height;
        this._params = params;
    }
    
    public String getName() {
        return this._name;
    }
    
    public String getAppletClass() {
        return this._appletClass;
    }
    
    public URL getDocumentBase() {
        return this._documentBase;
    }
    
    public int getWidth() {
        return this._width;
    }
    
    public int getHeight() {
        return this._height;
    }
    
    public Properties getParameters() {
        return this._params;
    }
    
    public XMLNode asXML() {
        final XMLAttributeBuilder xmlAttributeBuilder = new XMLAttributeBuilder();
        xmlAttributeBuilder.add("name", this._name);
        xmlAttributeBuilder.add("code", this._appletClass);
        xmlAttributeBuilder.add("documentbase", this._documentBase);
        xmlAttributeBuilder.add("width", (long)this._width);
        xmlAttributeBuilder.add("height", (long)this._height);
        final XMLNodeBuilder xmlNodeBuilder = new XMLNodeBuilder("applet-desc", xmlAttributeBuilder.getAttributeList());
        if (this._params != null) {
            final Enumeration<String> keys = ((Hashtable<String, V>)this._params).keys();
            while (keys.hasMoreElements()) {
                final String s = keys.nextElement();
                xmlNodeBuilder.add(new XMLNode("param", new XMLAttribute("name", s, new XMLAttribute("value", this._params.getProperty(s))), (XMLNode)null, (XMLNode)null));
            }
        }
        return xmlNodeBuilder.getNode();
    }
}
