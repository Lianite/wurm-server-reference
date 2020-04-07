// 
// Decompiled by Procyon v0.5.30
// 

package org.fourthline.cling.support.model;

public enum SeekMode
{
    TRACK_NR("TRACK_NR"), 
    ABS_TIME("ABS_TIME"), 
    REL_TIME("REL_TIME"), 
    ABS_COUNT("ABS_COUNT"), 
    REL_COUNT("REL_COUNT"), 
    CHANNEL_FREQ("CHANNEL_FREQ"), 
    TAPE_INDEX("TAPE-INDEX"), 
    FRAME("FRAME");
    
    private String protocolString;
    
    private SeekMode(final String protocolString) {
        this.protocolString = protocolString;
    }
    
    @Override
    public String toString() {
        return this.protocolString;
    }
    
    public static SeekMode valueOrExceptionOf(final String s) throws IllegalArgumentException {
        for (final SeekMode seekMode : values()) {
            if (seekMode.protocolString.equals(s)) {
                return seekMode;
            }
        }
        throw new IllegalArgumentException("Invalid seek mode string: " + s);
    }
}
