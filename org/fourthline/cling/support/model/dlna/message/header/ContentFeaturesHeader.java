// 
// Decompiled by Procyon v0.5.30
// 

package org.fourthline.cling.support.model.dlna.message.header;

import org.fourthline.cling.model.message.header.UpnpHeader;
import org.fourthline.cling.model.message.header.InvalidHeaderException;
import org.fourthline.cling.support.model.dlna.DLNAAttribute;
import java.util.EnumMap;

public class ContentFeaturesHeader extends DLNAHeader<EnumMap<DLNAAttribute.Type, DLNAAttribute>>
{
    public ContentFeaturesHeader() {
        this.setValue(new EnumMap<DLNAAttribute.Type, DLNAAttribute>(DLNAAttribute.Type.class));
    }
    
    @Override
    public void setString(final String s) throws InvalidHeaderException {
        if (s.length() != 0) {
            final String[] split;
            final String[] atts = split = s.split(";");
            for (final String att : split) {
                final String[] attNameValue = att.split("=");
                if (attNameValue.length == 2) {
                    final DLNAAttribute.Type type = DLNAAttribute.Type.valueOfAttributeName(attNameValue[0]);
                    if (type != null) {
                        final DLNAAttribute dlnaAttrinute = DLNAAttribute.newInstance(type, attNameValue[1], "");
                        ((UpnpHeader<EnumMap<DLNAAttribute.Type, DLNAAttribute>>)this).getValue().put(type, dlnaAttrinute);
                    }
                }
            }
        }
    }
    
    @Override
    public String getString() {
        String s = "";
        for (final DLNAAttribute.Type type : DLNAAttribute.Type.values()) {
            final String value = ((UpnpHeader<EnumMap>)this).getValue().containsKey(type) ? ((UpnpHeader<EnumMap<DLNAAttribute.Type, DLNAAttribute>>)this).getValue().get(type).getString() : null;
            if (value != null && value.length() != 0) {
                s = s + ((s.length() == 0) ? "" : ";") + type.getAttributeName() + "=" + value;
            }
        }
        return s;
    }
}
