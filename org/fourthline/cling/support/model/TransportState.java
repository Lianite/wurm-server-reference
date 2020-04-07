// 
// Decompiled by Procyon v0.5.30
// 

package org.fourthline.cling.support.model;

public enum TransportState
{
    STOPPED, 
    PLAYING, 
    TRANSITIONING, 
    PAUSED_PLAYBACK, 
    PAUSED_RECORDING, 
    RECORDING, 
    NO_MEDIA_PRESENT, 
    CUSTOM;
    
    String value;
    
    private TransportState() {
        this.value = this.name();
    }
    
    public String getValue() {
        return this.value;
    }
    
    public TransportState setValue(final String value) {
        this.value = value;
        return this;
    }
    
    public static TransportState valueOrCustomOf(final String s) {
        try {
            return valueOf(s);
        }
        catch (IllegalArgumentException ex) {
            return TransportState.CUSTOM.setValue(s);
        }
    }
}
