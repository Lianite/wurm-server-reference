// 
// Decompiled by Procyon v0.5.30
// 

package org.fourthline.cling.model.message;

public abstract class UpnpOperation
{
    private int httpMinorVersion;
    
    public UpnpOperation() {
        this.httpMinorVersion = 1;
    }
    
    public int getHttpMinorVersion() {
        return this.httpMinorVersion;
    }
    
    public void setHttpMinorVersion(final int httpMinorVersion) {
        this.httpMinorVersion = httpMinorVersion;
    }
}
