// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.javaws.jnl;

import com.sun.deploy.xml.XMLAttributeBuilder;
import com.sun.deploy.xml.XMLNode;

public class PackageDesc implements ResourceType
{
    private String _packageName;
    private String _part;
    private boolean _isRecursive;
    private boolean _isExact;
    
    public PackageDesc(final String packageName, final String part, final boolean isRecursive) {
        if (packageName.endsWith(".*")) {
            this._packageName = packageName.substring(0, packageName.length() - 1);
            this._isExact = false;
        }
        else {
            this._isExact = true;
            this._packageName = packageName;
        }
        this._part = part;
        this._isRecursive = isRecursive;
    }
    
    String getPackageName() {
        return this._packageName;
    }
    
    String getPart() {
        return this._part;
    }
    
    boolean isRecursive() {
        return this._isRecursive;
    }
    
    boolean match(String substring) {
        if (this._isExact) {
            return this._packageName.equals(substring);
        }
        if (this._isRecursive) {
            return substring.startsWith(this._packageName);
        }
        final int lastIndex = substring.lastIndexOf(46);
        if (lastIndex != -1) {
            substring = substring.substring(0, lastIndex + 1);
        }
        return substring.equals(this._packageName);
    }
    
    public void visit(final ResourceVisitor resourceVisitor) {
        resourceVisitor.visitPackageDesc(this);
    }
    
    public XMLNode asXML() {
        final XMLAttributeBuilder xmlAttributeBuilder = new XMLAttributeBuilder();
        xmlAttributeBuilder.add("name", this.getPackageName());
        xmlAttributeBuilder.add("part", this.getPart());
        xmlAttributeBuilder.add("recursive", this.isRecursive());
        return new XMLNode("package", xmlAttributeBuilder.getAttributeList());
    }
}
