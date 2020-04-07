// 
// Decompiled by Procyon v0.5.30
// 

package org.fourthline.cling.support.model.dlna.message.header;

import org.fourthline.cling.model.message.header.InvalidHeaderException;

public class SupportedHeader extends DLNAHeader<String[]>
{
    public SupportedHeader() {
        this.setValue(new String[0]);
    }
    
    @Override
    public void setString(String s) throws InvalidHeaderException {
        if (s.length() != 0) {
            if (s.endsWith(";")) {
                s = s.substring(0, s.length() - 1);
            }
            this.setValue(s.split("\\s*,\\s*"));
            return;
        }
        throw new InvalidHeaderException("Invalid Supported header value: " + s);
    }
    
    @Override
    public String getString() {
        final String[] v = this.getValue();
        String r = (v.length > 0) ? v[0] : "";
        for (int i = 1; i < v.length; ++i) {
            r = r + "," + v[i];
        }
        return r;
    }
}
