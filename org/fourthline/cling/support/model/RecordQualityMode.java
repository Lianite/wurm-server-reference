// 
// Decompiled by Procyon v0.5.30
// 

package org.fourthline.cling.support.model;

import java.util.List;
import java.util.ArrayList;
import org.fourthline.cling.model.ModelUtil;

public enum RecordQualityMode
{
    EP("0:EP"), 
    LP("1:LP"), 
    SP("2:SP"), 
    BASIC("0:BASIC"), 
    MEDIUM("1:MEDIUM"), 
    HIGH("2:HIGH"), 
    NOT_IMPLEMENTED("NOT_IMPLEMENTED");
    
    private String protocolString;
    
    private RecordQualityMode(final String protocolString) {
        this.protocolString = protocolString;
    }
    
    @Override
    public String toString() {
        return this.protocolString;
    }
    
    public static RecordQualityMode valueOrExceptionOf(final String s) throws IllegalArgumentException {
        for (final RecordQualityMode recordQualityMode : values()) {
            if (recordQualityMode.protocolString.equals(s)) {
                return recordQualityMode;
            }
        }
        throw new IllegalArgumentException("Invalid record quality mode string: " + s);
    }
    
    public static RecordQualityMode[] valueOfCommaSeparatedList(final String s) {
        final String[] strings = ModelUtil.fromCommaSeparatedList(s);
        if (strings == null) {
            return new RecordQualityMode[0];
        }
        final List<RecordQualityMode> result = new ArrayList<RecordQualityMode>();
        for (final String rqm : strings) {
            for (final RecordQualityMode recordQualityMode : values()) {
                if (recordQualityMode.protocolString.equals(rqm)) {
                    result.add(recordQualityMode);
                }
            }
        }
        return result.toArray(new RecordQualityMode[result.size()]);
    }
}
