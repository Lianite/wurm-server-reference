// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.javaws.jnl;

import com.sun.deploy.xml.XMLAttribute;
import com.sun.deploy.xml.XMLNodeBuilder;
import com.sun.deploy.xml.XMLAttributeBuilder;
import com.sun.deploy.xml.XMLNode;
import com.sun.deploy.xml.XMLable;

public class ShortcutDesc implements XMLable
{
    private boolean _online;
    private boolean _desktop;
    private boolean _menu;
    private String _submenu;
    
    public ShortcutDesc(final boolean online, final boolean desktop, final boolean menu, final String submenu) {
        this._online = online;
        this._desktop = desktop;
        this._menu = menu;
        this._submenu = submenu;
    }
    
    public boolean getOnline() {
        return this._online;
    }
    
    public boolean getDesktop() {
        return this._desktop;
    }
    
    public boolean getMenu() {
        return this._menu;
    }
    
    public String getSubmenu() {
        return this._submenu;
    }
    
    public XMLNode asXML() {
        final XMLAttributeBuilder xmlAttributeBuilder = new XMLAttributeBuilder();
        xmlAttributeBuilder.add("online", this._online);
        final XMLNodeBuilder xmlNodeBuilder = new XMLNodeBuilder("shortcut", xmlAttributeBuilder.getAttributeList());
        if (this._desktop) {
            xmlNodeBuilder.add("desktop", (String)null);
        }
        if (this._menu) {
            if (this._submenu == null) {
                xmlNodeBuilder.add("menu", (String)null);
            }
            else {
                xmlNodeBuilder.add(new XMLNode("menu", new XMLAttribute("submenu", this._submenu)));
            }
        }
        return xmlNodeBuilder.getNode();
    }
}
