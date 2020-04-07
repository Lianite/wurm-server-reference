// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.javaws.jnl;

import com.sun.deploy.xml.XMLAttribute;
import com.sun.deploy.xml.XMLNodeBuilder;
import com.sun.deploy.xml.XMLAttributeBuilder;
import com.sun.deploy.xml.XMLNode;
import java.net.URL;
import com.sun.deploy.xml.XMLable;

public class RContentDesc implements XMLable
{
    private URL _href;
    private String _title;
    private String _description;
    private URL _icon;
    private boolean _isApplication;
    
    public RContentDesc(final URL href, final String title, final String description, final URL icon) {
        this._href = href;
        this._title = title;
        this._description = description;
        this._icon = icon;
        this._isApplication = (href != null && href.toString().endsWith(".jnlp"));
    }
    
    public URL getHref() {
        return this._href;
    }
    
    public URL getIcon() {
        return this._icon;
    }
    
    public String getTitle() {
        if (this._title == null) {
            final String path = this._href.getPath();
            return path.substring(path.lastIndexOf(47) + 1);
        }
        return this._title;
    }
    
    public String getDescription() {
        return this._description;
    }
    
    public boolean isApplication() {
        return this._isApplication;
    }
    
    public XMLNode asXML() {
        final XMLAttributeBuilder xmlAttributeBuilder = new XMLAttributeBuilder();
        xmlAttributeBuilder.add("href", this._href);
        final XMLNodeBuilder xmlNodeBuilder = new XMLNodeBuilder("related-content", xmlAttributeBuilder.getAttributeList());
        if (this._title != null) {
            xmlNodeBuilder.add("title", this._title);
        }
        if (this._description != null) {
            xmlNodeBuilder.add("description", this._description);
        }
        if (this._icon != null) {
            xmlNodeBuilder.add(new XMLNode("icon", new XMLAttribute("href", this._icon.toString())));
        }
        return xmlNodeBuilder.getNode();
    }
}
