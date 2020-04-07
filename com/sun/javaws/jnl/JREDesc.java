// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.javaws.jnl;

import com.sun.deploy.xml.XMLAttributeBuilder;
import com.sun.deploy.xml.XMLNode;
import java.net.URL;

public class JREDesc implements ResourceType
{
    private String _version;
    private long _maxHeap;
    private long _minHeap;
    private String _vmargs;
    private URL _href;
    private boolean _isSelected;
    private ResourcesDesc _resourceDesc;
    private LaunchDesc _extensioDesc;
    
    public JREDesc(final String version, final long minHeap, final long maxHeap, final String vmargs, final URL href, final ResourcesDesc resourceDesc) {
        this._version = version;
        this._maxHeap = maxHeap;
        this._minHeap = minHeap;
        this._vmargs = vmargs;
        this._href = href;
        this._isSelected = false;
        this._resourceDesc = resourceDesc;
        this._extensioDesc = null;
    }
    
    public String getVersion() {
        return this._version;
    }
    
    public URL getHref() {
        return this._href;
    }
    
    public long getMinHeap() {
        return this._minHeap;
    }
    
    public long getMaxHeap() {
        return this._maxHeap;
    }
    
    public String getVmArgs() {
        return this._vmargs;
    }
    
    public boolean isSelected() {
        return this._isSelected;
    }
    
    public void markAsSelected() {
        this._isSelected = true;
    }
    
    public ResourcesDesc getNestedResources() {
        return this._resourceDesc;
    }
    
    public LaunchDesc getExtensionDesc() {
        return this._extensioDesc;
    }
    
    public void setExtensionDesc(final LaunchDesc extensioDesc) {
        this._extensioDesc = extensioDesc;
    }
    
    public void visit(final ResourceVisitor resourceVisitor) {
        resourceVisitor.visitJREDesc(this);
    }
    
    public XMLNode asXML() {
        final XMLAttributeBuilder xmlAttributeBuilder = new XMLAttributeBuilder();
        if (this._minHeap > 0L) {
            xmlAttributeBuilder.add("initial-heap-size", this._minHeap);
        }
        if (this._maxHeap > 0L) {
            xmlAttributeBuilder.add("max-heap-size", this._maxHeap);
        }
        if (this._vmargs != null) {
            xmlAttributeBuilder.add("java-vm-args", this._vmargs);
        }
        xmlAttributeBuilder.add("href", this._href);
        if (this._version != null) {
            xmlAttributeBuilder.add("version", this._version);
        }
        XMLNode xml = (this._extensioDesc != null) ? this._extensioDesc.asXML() : null;
        if (this._resourceDesc != null) {
            xml = this._resourceDesc.asXML();
        }
        return new XMLNode("j2se", xmlAttributeBuilder.getAttributeList(), xml, (XMLNode)null);
    }
}
