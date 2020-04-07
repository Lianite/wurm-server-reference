// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.javaws.jnl;

import com.sun.deploy.xml.XMLAttributeBuilder;
import com.sun.deploy.xml.XMLNode;
import com.sun.javaws.Globals;
import java.net.URL;

public class JARDesc implements ResourceType
{
    private URL _location;
    private String _version;
    private int _size;
    private boolean _isNativeLib;
    private boolean _isLazyDownload;
    private boolean _isMainFile;
    private String _part;
    private ResourcesDesc _parent;
    
    public JARDesc(final URL location, final String version, final boolean b, final boolean isMainFile, final boolean isNativeLib, final String part, final int size, final ResourcesDesc parent) {
        this._location = location;
        this._version = version;
        this._isLazyDownload = (b && !this._isMainFile && !Globals.isImportMode());
        this._isNativeLib = isNativeLib;
        this._isMainFile = isMainFile;
        this._part = part;
        this._size = size;
        this._parent = parent;
    }
    
    public boolean isNativeLib() {
        return this._isNativeLib;
    }
    
    public boolean isJavaFile() {
        return !this._isNativeLib;
    }
    
    public URL getLocation() {
        return this._location;
    }
    
    public String getVersion() {
        return this._version;
    }
    
    public boolean isLazyDownload() {
        return this._isLazyDownload;
    }
    
    public boolean isMainJarFile() {
        return this._isMainFile;
    }
    
    public String getPartName() {
        return this._part;
    }
    
    public int getSize() {
        return this._size;
    }
    
    public ResourcesDesc getParent() {
        return this._parent;
    }
    
    public void visit(final ResourceVisitor resourceVisitor) {
        resourceVisitor.visitJARDesc(this);
    }
    
    public XMLNode asXML() {
        final XMLAttributeBuilder xmlAttributeBuilder = new XMLAttributeBuilder();
        xmlAttributeBuilder.add("href", this._location);
        xmlAttributeBuilder.add("version", this._version);
        xmlAttributeBuilder.add("part", this._part);
        xmlAttributeBuilder.add("download", this.isLazyDownload() ? "lazy" : "eager");
        xmlAttributeBuilder.add("main", this.isMainJarFile() ? "true" : "false");
        final String s = this._isNativeLib ? "nativelib" : "jar";
        return new XMLNode("jar", xmlAttributeBuilder.getAttributeList());
    }
    
    public String toString() {
        return "JARDesc[" + this._location + ":" + this._version;
    }
}
