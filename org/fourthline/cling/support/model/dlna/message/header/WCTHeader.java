// 
// Decompiled by Procyon v0.5.30
// 

package org.fourthline.cling.support.model.dlna.message.header;

import org.fourthline.cling.model.message.header.InvalidHeaderException;
import java.util.regex.Pattern;

public class WCTHeader extends DLNAHeader<Boolean>
{
    static final Pattern pattern;
    
    public WCTHeader() {
        this.setValue(false);
    }
    
    @Override
    public void setString(final String s) throws InvalidHeaderException {
        if (WCTHeader.pattern.matcher(s).matches()) {
            this.setValue(s.equals("1"));
            return;
        }
        throw new InvalidHeaderException("Invalid SCID header value: " + s);
    }
    
    @Override
    public String getString() {
        return this.getValue() ? "1" : "0";
    }
    
    static {
        pattern = Pattern.compile("^[01]{1}$", 2);
    }
}
