// 
// Decompiled by Procyon v0.5.30
// 

package org.fourthline.cling.support.model;

public enum TransportStatus
{
    OK, 
    ERROR_OCCURRED, 
    CUSTOM;
    
    String value;
    
    private TransportStatus() {
        this.value = this.name();
    }
    
    public String getValue() {
        return this.value;
    }
    
    public TransportStatus setValue(final String value) {
        this.value = value;
        return this;
    }
    
    public static TransportStatus valueOrCustomOf(final String s) {
        try {
            return valueOf(s);
        }
        catch (IllegalArgumentException ex) {
            return TransportStatus.CUSTOM.setValue(s);
        }
    }
}
