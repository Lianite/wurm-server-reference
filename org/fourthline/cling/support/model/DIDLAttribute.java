// 
// Decompiled by Procyon v0.5.30
// 

package org.fourthline.cling.support.model;

public class DIDLAttribute
{
    private String namespaceURI;
    private String prefix;
    private String value;
    
    public DIDLAttribute(final String namespaceURI, final String prefix, final String value) {
        this.namespaceURI = namespaceURI;
        this.prefix = prefix;
        this.value = value;
    }
    
    public String getNamespaceURI() {
        return this.namespaceURI;
    }
    
    public String getPrefix() {
        return this.prefix;
    }
    
    public String getValue() {
        return this.value;
    }
}
