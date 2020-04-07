// 
// Decompiled by Procyon v0.5.30
// 

package org.fourthline.cling.support.model;

import org.fourthline.cling.model.types.InvalidValueException;
import org.fourthline.cling.model.ModelUtil;
import java.util.ArrayList;

public class ProtocolInfos extends ArrayList<ProtocolInfo>
{
    public ProtocolInfos(final ProtocolInfo... info) {
        for (final ProtocolInfo protocolInfo : info) {
            this.add(protocolInfo);
        }
    }
    
    public ProtocolInfos(final String s) throws InvalidValueException {
        final String[] infos = ModelUtil.fromCommaSeparatedList(s);
        if (infos != null) {
            for (final String info : infos) {
                this.add(new ProtocolInfo(info));
            }
        }
    }
    
    @Override
    public String toString() {
        return ModelUtil.toCommaSeparatedList(this.toArray(new ProtocolInfo[this.size()]));
    }
}
