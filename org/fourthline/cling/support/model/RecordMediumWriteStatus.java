// 
// Decompiled by Procyon v0.5.30
// 

package org.fourthline.cling.support.model;

public enum RecordMediumWriteStatus
{
    WRITABLE, 
    PROTECTED, 
    NOT_WRITABLE, 
    UNKNOWN, 
    NOT_IMPLEMENTED;
    
    public static RecordMediumWriteStatus valueOrUnknownOf(final String s) {
        if (s == null) {
            return RecordMediumWriteStatus.UNKNOWN;
        }
        try {
            return valueOf(s);
        }
        catch (IllegalArgumentException ex) {
            return RecordMediumWriteStatus.UNKNOWN;
        }
    }
}
