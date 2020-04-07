// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.javaws.jnl;

import com.sun.deploy.xml.XMLAttributeBuilder;
import com.sun.deploy.xml.XMLNode;
import java.net.URL;
import com.sun.deploy.xml.XMLable;

public class IconDesc implements XMLable
{
    private URL _location;
    private String _version;
    private String _locationString;
    private int _height;
    private int _width;
    private int _depth;
    private int _kind;
    public static final int ICON_KIND_DEFAULT = 0;
    public static final int ICON_KIND_SELECTED = 1;
    public static final int ICON_KIND_DISABLED = 2;
    public static final int ICON_KIND_ROLLOVER = 3;
    public static final int ICON_KIND_SPLASH = 4;
    private static final String[] kinds;
    
    public IconDesc(final URL location, final String version, final int n, final int width, final int depth, final int kind) {
        this._location = location;
        if (this._location != null) {
            this._locationString = this._location.toExternalForm();
        }
        this._width = width;
        this._depth = depth;
        this._kind = kind;
        this._version = version;
    }
    
    public URL getLocation() {
        return this._location;
    }
    
    public String getVersion() {
        return this._version;
    }
    
    public int getHeight() {
        return this._height;
    }
    
    public int getWidth() {
        return this._width;
    }
    
    public int getDepth() {
        return this._depth;
    }
    
    public int getKind() {
        return this._kind;
    }
    
    public final boolean equals(final Object o) {
        if (o == this) {
            return true;
        }
        if (o instanceof IconDesc) {
            final IconDesc iconDesc = (IconDesc)o;
            if (((this._version == null && iconDesc._version == null) || (this._version != null && this._version.equals(iconDesc._version))) && ((this._locationString == null && iconDesc._locationString == null) || (this._locationString != null && this._locationString.equals(iconDesc._locationString)))) {
                return true;
            }
        }
        return false;
    }
    
    public int hashCode() {
        int n = 0;
        if (this._locationString != null) {
            n |= this._locationString.hashCode();
        }
        if (this._version != null) {
            n |= this._version.hashCode();
        }
        return n;
    }
    
    public XMLNode asXML() {
        final XMLAttributeBuilder xmlAttributeBuilder = new XMLAttributeBuilder();
        xmlAttributeBuilder.add("href", this._location);
        xmlAttributeBuilder.add("height", (long)this._height);
        xmlAttributeBuilder.add("width", (long)this._width);
        xmlAttributeBuilder.add("depth", (long)this._depth);
        xmlAttributeBuilder.add("kind", IconDesc.kinds[this._kind]);
        return new XMLNode("icon", xmlAttributeBuilder.getAttributeList());
    }
    
    static {
        kinds = new String[] { "default", "selected", "disabled", "rollover", "splash" };
    }
}
