// 
// Decompiled by Procyon v0.5.30
// 

package org.fourthline.cling.support.model;

import java.util.List;
import java.util.ArrayList;
import org.fourthline.cling.model.ModelUtil;

public enum TransportAction
{
    Play, 
    Stop, 
    Pause, 
    Seek, 
    Next, 
    Previous, 
    Record;
    
    public static TransportAction[] valueOfCommaSeparatedList(final String s) {
        final String[] strings = ModelUtil.fromCommaSeparatedList(s);
        if (strings == null) {
            return new TransportAction[0];
        }
        final List<TransportAction> result = new ArrayList<TransportAction>();
        for (final String taString : strings) {
            for (final TransportAction ta : values()) {
                if (ta.name().equals(taString)) {
                    result.add(ta);
                }
            }
        }
        return result.toArray(new TransportAction[result.size()]);
    }
}
