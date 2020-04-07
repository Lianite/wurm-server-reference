// 
// Decompiled by Procyon v0.5.30
// 

package org.fourthline.cling.support.renderingcontrol;

public enum RenderingControlErrorCode
{
    INVALID_PRESET_NAME(701, "The specified name is not a valid preset name"), 
    INVALID_INSTANCE_ID(702, "The specified instanceID is invalid for this RenderingControl");
    
    private int code;
    private String description;
    
    private RenderingControlErrorCode(final int code, final String description) {
        this.code = code;
        this.description = description;
    }
    
    public int getCode() {
        return this.code;
    }
    
    public String getDescription() {
        return this.description;
    }
    
    public static RenderingControlErrorCode getByCode(final int code) {
        for (final RenderingControlErrorCode errorCode : values()) {
            if (errorCode.getCode() == code) {
                return errorCode;
            }
        }
        return null;
    }
}
