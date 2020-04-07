// 
// Decompiled by Procyon v0.5.30
// 

package org.fourthline.cling.support.model.dlna.types;

import org.fourthline.cling.model.types.BytesRange;

public class TimeSeekRangeType
{
    private NormalPlayTimeRange normalPlayTimeRange;
    private BytesRange bytesRange;
    
    public TimeSeekRangeType(final NormalPlayTimeRange nptRange) {
        this.normalPlayTimeRange = nptRange;
    }
    
    public TimeSeekRangeType(final NormalPlayTimeRange nptRange, final BytesRange byteRange) {
        this.normalPlayTimeRange = nptRange;
        this.bytesRange = byteRange;
    }
    
    public NormalPlayTimeRange getNormalPlayTimeRange() {
        return this.normalPlayTimeRange;
    }
    
    public BytesRange getBytesRange() {
        return this.bytesRange;
    }
    
    public void setBytesRange(final BytesRange bytesRange) {
        this.bytesRange = bytesRange;
    }
}
