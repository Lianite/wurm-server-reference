// 
// Decompiled by Procyon v0.5.30
// 

package org.fourthline.cling.support.model;

public enum BrowseFlag
{
    METADATA("BrowseMetadata"), 
    DIRECT_CHILDREN("BrowseDirectChildren");
    
    private String protocolString;
    
    private BrowseFlag(final String protocolString) {
        this.protocolString = protocolString;
    }
    
    @Override
    public String toString() {
        return this.protocolString;
    }
    
    public static BrowseFlag valueOrNullOf(final String s) {
        for (final BrowseFlag browseFlag : values()) {
            if (browseFlag.toString().equals(s)) {
                return browseFlag;
            }
        }
        return null;
    }
}
