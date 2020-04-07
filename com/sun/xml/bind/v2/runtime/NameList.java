// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.xml.bind.v2.runtime;

public final class NameList
{
    public final String[] namespaceURIs;
    public final boolean[] nsUriCannotBeDefaulted;
    public final String[] localNames;
    public final int numberOfElementNames;
    public final int numberOfAttributeNames;
    
    public NameList(final String[] namespaceURIs, final boolean[] nsUriCannotBeDefaulted, final String[] localNames, final int numberElementNames, final int numberAttributeNames) {
        this.namespaceURIs = namespaceURIs;
        this.nsUriCannotBeDefaulted = nsUriCannotBeDefaulted;
        this.localNames = localNames;
        this.numberOfElementNames = numberElementNames;
        this.numberOfAttributeNames = numberAttributeNames;
    }
}
