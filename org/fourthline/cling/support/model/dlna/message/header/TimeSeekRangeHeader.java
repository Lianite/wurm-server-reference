// 
// Decompiled by Procyon v0.5.30
// 

package org.fourthline.cling.support.model.dlna.message.header;

import org.fourthline.cling.model.types.InvalidValueException;
import org.fourthline.cling.model.message.header.InvalidHeaderException;
import org.fourthline.cling.model.types.BytesRange;
import org.fourthline.cling.support.model.dlna.types.NormalPlayTimeRange;
import org.fourthline.cling.support.model.dlna.types.TimeSeekRangeType;

public class TimeSeekRangeHeader extends DLNAHeader<TimeSeekRangeType>
{
    public TimeSeekRangeHeader() {
    }
    
    public TimeSeekRangeHeader(final TimeSeekRangeType timeSeekRange) {
        this.setValue(timeSeekRange);
    }
    
    @Override
    public void setString(final String s) throws InvalidHeaderException {
        if (s.length() != 0) {
            final String[] params = s.split(" ");
            if (params.length > 0) {
                try {
                    final TimeSeekRangeType t = new TimeSeekRangeType(NormalPlayTimeRange.valueOf(params[0]));
                    if (params.length > 1) {
                        t.setBytesRange(BytesRange.valueOf(params[1]));
                    }
                    this.setValue(t);
                    return;
                }
                catch (InvalidValueException invalidValueException) {
                    throw new InvalidHeaderException("Invalid TimeSeekRange header value: " + s + "; " + invalidValueException.getMessage());
                }
            }
        }
        throw new InvalidHeaderException("Invalid TimeSeekRange header value: " + s);
    }
    
    @Override
    public String getString() {
        final TimeSeekRangeType t = this.getValue();
        String s = t.getNormalPlayTimeRange().getString();
        if (t.getBytesRange() != null) {
            s = s + " " + t.getBytesRange().getString(true);
        }
        return s;
    }
}
