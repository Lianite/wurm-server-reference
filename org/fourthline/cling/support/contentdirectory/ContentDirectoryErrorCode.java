// 
// Decompiled by Procyon v0.5.30
// 

package org.fourthline.cling.support.contentdirectory;

public enum ContentDirectoryErrorCode
{
    NO_SUCH_OBJECT(701, "The specified ObjectID is invalid"), 
    UNSUPPORTED_SORT_CRITERIA(709, "Unsupported or invalid sort criteria"), 
    CANNOT_PROCESS(720, "Cannot process the request");
    
    private int code;
    private String description;
    
    private ContentDirectoryErrorCode(final int code, final String description) {
        this.code = code;
        this.description = description;
    }
    
    public int getCode() {
        return this.code;
    }
    
    public String getDescription() {
        return this.description;
    }
    
    public static ContentDirectoryErrorCode getByCode(final int code) {
        for (final ContentDirectoryErrorCode errorCode : values()) {
            if (errorCode.getCode() == code) {
                return errorCode;
            }
        }
        return null;
    }
}
