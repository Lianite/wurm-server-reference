// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.javaws.jnl;

import com.sun.deploy.xml.XMLable;
import com.sun.deploy.xml.XMLNodeBuilder;
import com.sun.deploy.xml.XMLAttributeBuilder;
import com.sun.deploy.xml.XMLNode;
import java.util.HashSet;
import java.net.URL;

public class ExtensionDesc implements ResourceType
{
    private String _name;
    private URL _location;
    private String _version;
    private boolean _isInstaller;
    private ExtDownloadDesc[] _extDownloadDescs;
    private LaunchDesc _extensionDefDesc;
    
    public ExtensionDesc(final String name, final URL location, final String version, ExtDownloadDesc[] extDownloadDescs) {
        this._location = location;
        this._version = version;
        this._name = name;
        if (extDownloadDescs == null) {
            extDownloadDescs = new ExtDownloadDesc[0];
        }
        this._extDownloadDescs = extDownloadDescs;
        this._extensionDefDesc = null;
        this._isInstaller = false;
    }
    
    public void setInstaller(final boolean isInstaller) {
        this._isInstaller = isInstaller;
    }
    
    public boolean isInstaller() {
        return this._isInstaller;
    }
    
    public String getVersion() {
        return this._version;
    }
    
    public URL getLocation() {
        return this._location;
    }
    
    public String getName() {
        return this._name;
    }
    
    ExtDownloadDesc[] getExtDownloadDescs() {
        return this._extDownloadDescs;
    }
    
    public LaunchDesc getExtensionDesc() {
        return this._extensionDefDesc;
    }
    
    public void setExtensionDesc(final LaunchDesc extensionDefDesc) {
        this._extensionDefDesc = extensionDefDesc;
    }
    
    ResourcesDesc getExtensionResources() {
        return this._extensionDefDesc.getResources();
    }
    
    HashSet getExtensionPackages(final HashSet set, final boolean b) {
        final HashSet<String> set2 = new HashSet<String>();
        for (int i = 0; i < this._extDownloadDescs.length; ++i) {
            final ExtDownloadDesc extDownloadDesc = this._extDownloadDescs[i];
            if ((b && !extDownloadDesc.isLazy()) || (set != null && set.contains(extDownloadDesc.getPart()))) {
                set2.add(extDownloadDesc.getExtensionPart());
            }
        }
        return set2;
    }
    
    public void visit(final ResourceVisitor resourceVisitor) {
        resourceVisitor.visitExtensionDesc(this);
    }
    
    public XMLNode asXML() {
        final XMLAttributeBuilder xmlAttributeBuilder = new XMLAttributeBuilder();
        xmlAttributeBuilder.add("href", this._location);
        xmlAttributeBuilder.add("version", this._version);
        xmlAttributeBuilder.add("name", this._name);
        final XMLNodeBuilder xmlNodeBuilder = new XMLNodeBuilder("extension", xmlAttributeBuilder.getAttributeList());
        for (int i = 0; i < this._extDownloadDescs.length; ++i) {
            xmlNodeBuilder.add((XMLable)this._extDownloadDescs[i]);
        }
        return xmlNodeBuilder.getNode();
    }
}
