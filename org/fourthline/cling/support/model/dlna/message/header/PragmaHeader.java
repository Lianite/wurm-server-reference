// 
// Decompiled by Procyon v0.5.30
// 

package org.fourthline.cling.support.model.dlna.message.header;

import org.fourthline.cling.model.message.header.UpnpHeader;
import java.util.Iterator;
import org.fourthline.cling.model.message.header.InvalidHeaderException;
import java.util.ArrayList;
import org.fourthline.cling.model.types.PragmaType;
import java.util.List;

public class PragmaHeader extends DLNAHeader<List<PragmaType>>
{
    public PragmaHeader() {
        ((UpnpHeader<ArrayList<PragmaType>>)this).setValue(new ArrayList<PragmaType>());
    }
    
    @Override
    public void setString(String s) throws InvalidHeaderException {
        if (s.length() != 0) {
            if (s.endsWith(";")) {
                s = s.substring(0, s.length() - 1);
            }
            final String[] list = s.split("\\s*;\\s*");
            final List<PragmaType> value = new ArrayList<PragmaType>();
            for (final String pragma : list) {
                value.add(PragmaType.valueOf(pragma));
            }
            return;
        }
        throw new InvalidHeaderException("Invalid Pragma header value: " + s);
    }
    
    @Override
    public String getString() {
        final List<PragmaType> v = this.getValue();
        String r = "";
        for (final PragmaType pragma : v) {
            r = r + ((r.length() == 0) ? "" : ",") + pragma.getString();
        }
        return r;
    }
}
