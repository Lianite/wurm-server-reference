// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.shared.xml;

import java.util.Iterator;
import java.util.ArrayList;
import java.util.List;
import org.xml.sax.Attributes;

public class XmlNode
{
    private final String name;
    private final Attributes attributes;
    private final List<XmlNode> children;
    private String text;
    
    public XmlNode(final String localName, final Attributes attributes) {
        this.children = new ArrayList<XmlNode>();
        this.name = localName;
        this.attributes = attributes;
    }
    
    public void addChild(final XmlNode child) {
        this.children.add(child);
    }
    
    public void setText(final String text) {
        this.text = text;
    }
    
    public List<XmlNode> getAll(final String aName) {
        final List<XmlNode> list = new ArrayList<XmlNode>();
        for (final XmlNode xmlNode : this.children) {
            if (xmlNode.name.equals(aName)) {
                list.add(xmlNode);
            }
        }
        return list;
    }
    
    public XmlNode getFirst(final String aName) {
        for (final XmlNode xmlNode : this.children) {
            if (xmlNode.name.equals(aName)) {
                return xmlNode;
            }
        }
        return null;
    }
    
    public String getAttribute(final String aName) {
        return this.attributes.getValue(aName);
    }
    
    public Attributes getAttributes() {
        return this.attributes;
    }
    
    public List<XmlNode> getChildren() {
        return this.children;
    }
    
    public String getName() {
        return this.name;
    }
    
    public String getText() {
        return this.text;
    }
    
    public String getValue(final String string) {
        final XmlNode node = this.getFirst(string);
        return (node == null) ? null : node.getText();
    }
}
