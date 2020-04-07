// 
// Decompiled by Procyon v0.5.30
// 

package org.fourthline.cling.support.model.dlna.types;

public class CodedDataBuffer
{
    private Long size;
    private TransferMechanism tranfer;
    
    public CodedDataBuffer(final Long size, final TransferMechanism transfer) {
        this.size = size;
        this.tranfer = transfer;
    }
    
    public Long getSize() {
        return this.size;
    }
    
    public TransferMechanism getTranfer() {
        return this.tranfer;
    }
    
    public enum TransferMechanism
    {
        IMMEDIATELY, 
        TIMESTAMP, 
        OTHER;
    }
}
