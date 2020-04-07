// 
// Decompiled by Procyon v0.5.30
// 

package org.fourthline.cling.support.model.dlna;

public enum DLNAOperations
{
    NONE(0), 
    RANGE(1), 
    TIMESEEK(16);
    
    private int code;
    
    private DLNAOperations(final int code) {
        this.code = code;
    }
    
    public int getCode() {
        return this.code;
    }
    
    public static DLNAOperations valueOf(final int code) {
        for (final DLNAOperations errorCode : values()) {
            if (errorCode.getCode() == code) {
                return errorCode;
            }
        }
        return null;
    }
}
