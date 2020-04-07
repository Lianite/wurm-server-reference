// 
// Decompiled by Procyon v0.5.30
// 

package org.fourthline.cling.support.model.dlna;

public enum DLNAConversionIndicator
{
    NONE(0), 
    TRANSCODED(1);
    
    private int code;
    
    private DLNAConversionIndicator(final int code) {
        this.code = code;
    }
    
    public int getCode() {
        return this.code;
    }
    
    public static DLNAConversionIndicator valueOf(final int code) {
        for (final DLNAConversionIndicator errorCode : values()) {
            if (errorCode.getCode() == code) {
                return errorCode;
            }
        }
        return null;
    }
}
