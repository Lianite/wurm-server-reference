// 
// Decompiled by Procyon v0.5.30
// 

package org.fourthline.cling.support.model.dlna.message.header;

import org.fourthline.cling.model.message.header.InvalidHeaderException;
import org.fourthline.cling.model.types.BytesRange;
import org.fourthline.cling.support.model.dlna.types.NormalPlayTimeRange;
import org.fourthline.cling.model.types.InvalidValueException;
import org.fourthline.cling.support.model.dlna.types.AvailableSeekRangeType;

public class AvailableSeekRangeHeader extends DLNAHeader<AvailableSeekRangeType>
{
    public AvailableSeekRangeHeader() {
    }
    
    public AvailableSeekRangeHeader(final AvailableSeekRangeType timeSeekRange) {
        this.setValue(timeSeekRange);
    }
    
    @Override
    public void setString(final String s) throws InvalidHeaderException {
        if (s.length() != 0) {
            final String[] params = s.split(" ");
            if (params.length > 1) {
                try {
                    AvailableSeekRangeType.Mode mode = null;
                    NormalPlayTimeRange timeRange = null;
                    BytesRange byteRange = null;
                    try {
                        mode = AvailableSeekRangeType.Mode.valueOf("MODE_" + params[0]);
                    }
                    catch (IllegalArgumentException e) {
                        throw new InvalidValueException("Invalid AvailableSeekRange Mode");
                    }
                    boolean useTime = true;
                    try {
                        timeRange = NormalPlayTimeRange.valueOf(params[1], true);
                    }
                    catch (InvalidValueException timeInvalidValueException) {
                        try {
                            byteRange = BytesRange.valueOf(params[1]);
                            useTime = false;
                        }
                        catch (InvalidValueException bytesInvalidValueException) {
                            throw new InvalidValueException("Invalid AvailableSeekRange Range");
                        }
                    }
                    if (useTime) {
                        if (params.length > 2) {
                            byteRange = BytesRange.valueOf(params[2]);
                            this.setValue(new AvailableSeekRangeType(mode, timeRange, byteRange));
                        }
                        else {
                            this.setValue(new AvailableSeekRangeType(mode, timeRange));
                        }
                    }
                    else {
                        this.setValue(new AvailableSeekRangeType(mode, byteRange));
                    }
                    return;
                }
                catch (InvalidValueException invalidValueException) {
                    throw new InvalidHeaderException("Invalid AvailableSeekRange header value: " + s + "; " + invalidValueException.getMessage());
                }
            }
        }
        throw new InvalidHeaderException("Invalid AvailableSeekRange header value: " + s);
    }
    
    @Override
    public String getString() {
        final AvailableSeekRangeType t = this.getValue();
        String s = Integer.toString(t.getModeFlag().ordinal());
        if (t.getNormalPlayTimeRange() != null) {
            s = s + " " + t.getNormalPlayTimeRange().getString(false);
        }
        if (t.getBytesRange() != null) {
            s = s + " " + t.getBytesRange().getString(false);
        }
        return s;
    }
}
