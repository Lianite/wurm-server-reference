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

public class InformationDesc implements XMLable
{
    private String _title;
    private String _vendor;
    private URL _home;
    private String[] _descriptions;
    private IconDesc[] _icons;
    private ShortcutDesc _shortcutHints;
    private AssociationDesc[] _associations;
    private RContentDesc[] _relatedContent;
    private boolean _supportOfflineOperation;
    public static final int DESC_DEFAULT = 0;
    public static final int DESC_SHORT = 1;
    public static final int DESC_ONELINE = 2;
    public static final int DESC_TOOLTIP = 3;
    public static final int NOF_DESC = 4;
    public static final int ICON_SIZE_SMALL = 0;
    public static final int ICON_SIZE_MEDIUM = 1;
    public static final int ICON_SIZE_LARGE = 2;
    
    public InformationDesc(final String title, final String vendor, final URL home, String[] descriptions, final IconDesc[] icons, final ShortcutDesc shortcutHints, final RContentDesc[] relatedContent, final AssociationDesc[] associations, final boolean supportOfflineOperation) {
        this._title = title;
        this._vendor = vendor;
        this._home = home;
        if (descriptions == null) {
            descriptions = new String[4];
        }
        this._descriptions = descriptions;
        this._icons = icons;
        this._shortcutHints = shortcutHints;
        this._associations = associations;
        this._relatedContent = relatedContent;
        this._supportOfflineOperation = supportOfflineOperation;
    }
    
    public String getTitle() {
        return this._title;
    }
    
    public String getVendor() {
        return this._vendor;
    }
    
    public URL getHome() {
        return this._home;
    }
    
    public boolean supportsOfflineOperation() {
        return this._supportOfflineOperation;
    }
    
    public IconDesc[] getIcons() {
        return this._icons;
    }
    
    public ShortcutDesc getShortcut() {
        return this._shortcutHints;
    }
    
    public AssociationDesc[] getAssociations() {
        return this._associations;
    }
    
    public RContentDesc[] getRelatedContent() {
        return this._relatedContent;
    }
    
    public String getDescription(final int n) {
        return this._descriptions[n];
    }
    
    public IconDesc getIconLocation(final int n, final int n2) {
        int n3 = 0;
        int n4 = 0;
        switch (n) {
            case 0: {
                n4 = (n3 = 16);
                break;
            }
            case 1: {
                n4 = (n3 = 32);
                break;
            }
            case 2: {
                n4 = (n3 = 64);
                break;
            }
        }
        IconDesc iconDesc = null;
        long n5 = 0L;
        for (int i = 0; i < this._icons.length; ++i) {
            final IconDesc iconDesc2 = this._icons[i];
            if (iconDesc2.getKind() == n2) {
                if (iconDesc2.getHeight() == n3 && iconDesc2.getWidth() == n4) {
                    return iconDesc2;
                }
                if (iconDesc2.getHeight() == 0 && iconDesc2.getWidth() == 0) {
                    if (iconDesc == null) {
                        iconDesc = iconDesc2;
                    }
                }
                else {
                    final long n6 = Math.abs(iconDesc2.getHeight() * iconDesc2.getWidth() - n3 * n4);
                    if (n5 == 0L || n6 < n5) {
                        n5 = n6;
                        iconDesc = iconDesc2;
                    }
                }
            }
        }
        return iconDesc;
    }
    
    public XMLNode asXML() {
        final XMLNodeBuilder xmlNodeBuilder = new XMLNodeBuilder("information", new XMLAttributeBuilder().getAttributeList());
        xmlNodeBuilder.add("title", this._title);
        xmlNodeBuilder.add("vendor", this._vendor);
        xmlNodeBuilder.add(new XMLNode("homepage", new XMLAttribute("href", (this._home != null) ? this._home.toString() : null), (XMLNode)null, (XMLNode)null));
        xmlNodeBuilder.add(this.getDescriptionNode(0, ""));
        xmlNodeBuilder.add(this.getDescriptionNode(1, "short"));
        xmlNodeBuilder.add(this.getDescriptionNode(2, "one-line"));
        xmlNodeBuilder.add(this.getDescriptionNode(3, "tooltip"));
        if (this._icons != null) {
            for (int i = 0; i < this._icons.length; ++i) {
                xmlNodeBuilder.add((XMLable)this._icons[i]);
            }
        }
        if (this._shortcutHints != null) {
            xmlNodeBuilder.add((XMLable)this._shortcutHints);
        }
        if (this._associations != null) {
            for (int j = 0; j < this._associations.length; ++j) {
                xmlNodeBuilder.add((XMLable)this._associations[j]);
            }
        }
        if (this._relatedContent != null) {
            for (int k = 0; k < this._relatedContent.length; ++k) {
                xmlNodeBuilder.add((XMLable)this._relatedContent[k]);
            }
        }
        if (this._supportOfflineOperation) {
            xmlNodeBuilder.add(new XMLNode("offline-allowed", (XMLAttribute)null));
        }
        return xmlNodeBuilder.getNode();
    }
    
    private XMLNode getDescriptionNode(final int n, final String s) {
        final String s2 = this._descriptions[n];
        if (s2 == null) {
            return null;
        }
        final XMLAttributeBuilder xmlAttributeBuilder = new XMLAttributeBuilder();
        xmlAttributeBuilder.add("kind", s);
        return new XMLNode("description", xmlAttributeBuilder.getAttributeList(), new XMLNode(s2), (XMLNode)null);
    }
}
