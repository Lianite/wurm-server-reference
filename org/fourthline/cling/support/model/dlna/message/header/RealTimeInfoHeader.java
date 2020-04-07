// 
// Decompiled by Procyon v0.5.30
// 

package org.fourthline.cling.support.model.dlna.message.header;

import org.fourthline.cling.model.message.header.InvalidHeaderException;
import org.fourthline.cling.support.model.dlna.types.NormalPlayTime;

public class RealTimeInfoHeader extends DLNAHeader<NormalPlayTime>
{
    public static final String PREFIX = "DLNA.ORG_TLAG=";
    
    @Override
    public void setString(String s) throws InvalidHeaderException {
        if (s.length() != 0 && s.startsWith("DLNA.ORG_TLAG=")) {
            try {
                s = s.substring("DLNA.ORG_TLAG=".length());
                this.setValue(s.equals("*") ? null : NormalPlayTime.valueOf(s));
                return;
            }
            catch (Exception ex) {}
        }
        throw new InvalidHeaderException("Invalid RealTimeInfo header value: " + s);
    }
    
    @Override
    public String getString() {
        final NormalPlayTime v = this.getValue();
        if (v == null) {
            return "DLNA.ORG_TLAG=*";
        }
        return "DLNA.ORG_TLAG=" + v.getString();
    }
}
