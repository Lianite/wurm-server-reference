// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.xml.bind.v2.runtime;

import javax.xml.namespace.QName;

public final class Name implements Comparable<Name>
{
    public final String nsUri;
    public final String localName;
    public final short nsUriIndex;
    public final short localNameIndex;
    public final short qNameIndex;
    public final boolean isAttribute;
    
    Name(final int qNameIndex, final int nsUriIndex, final String nsUri, final int localIndex, final String localName, final boolean isAttribute) {
        this.qNameIndex = (short)qNameIndex;
        this.nsUri = nsUri;
        this.localName = localName;
        this.nsUriIndex = (short)nsUriIndex;
        this.localNameIndex = (short)localIndex;
        this.isAttribute = isAttribute;
    }
    
    public String toString() {
        return '{' + this.nsUri + '}' + this.localName;
    }
    
    public QName toQName() {
        return new QName(this.nsUri, this.localName);
    }
    
    public boolean equals(final String nsUri, final String localName) {
        return localName.equals(this.localName) && nsUri.equals(this.nsUri);
    }
    
    public int compareTo(final Name that) {
        final int r = this.nsUri.compareTo(that.nsUri);
        if (r != 0) {
            return r;
        }
        return this.localName.compareTo(that.localName);
    }
}
