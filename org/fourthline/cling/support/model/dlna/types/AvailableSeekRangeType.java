// 
// Decompiled by Procyon v0.5.30
// 

package org.fourthline.cling.support.model.dlna.types;

import org.fourthline.cling.model.types.BytesRange;

public class AvailableSeekRangeType
{
    private Mode modeFlag;
    private NormalPlayTimeRange normalPlayTimeRange;
    private BytesRange bytesRange;
    
    public AvailableSeekRangeType(final Mode modeFlag, final NormalPlayTimeRange nptRange) {
        this.modeFlag = modeFlag;
        this.normalPlayTimeRange = nptRange;
    }
    
    public AvailableSeekRangeType(final Mode modeFlag, final BytesRange byteRange) {
        this.modeFlag = modeFlag;
        this.bytesRange = byteRange;
    }
    
    public AvailableSeekRangeType(final Mode modeFlag, final NormalPlayTimeRange nptRange, final BytesRange byteRange) {
        this.modeFlag = modeFlag;
        this.normalPlayTimeRange = nptRange;
        this.bytesRange = byteRange;
    }
    
    public NormalPlayTimeRange getNormalPlayTimeRange() {
        return this.normalPlayTimeRange;
    }
    
    public BytesRange getBytesRange() {
        return this.bytesRange;
    }
    
    public Mode getModeFlag() {
        return this.modeFlag;
    }
    
    public enum Mode
    {
        MODE_0, 
        MODE_1;
    }
}
