// 
// Decompiled by Procyon v0.5.30
// 

package org.fourthline.cling.model.types;

import org.fourthline.cling.model.ModelUtil;
import java.util.Arrays;

public class DLNACaps
{
    final String[] caps;
    
    public DLNACaps(final String[] caps) {
        this.caps = caps;
    }
    
    public String[] getCaps() {
        return this.caps;
    }
    
    public static DLNACaps valueOf(final String s) throws InvalidValueException {
        if (s == null || s.length() == 0) {
            return new DLNACaps(new String[0]);
        }
        final String[] caps = s.split(",");
        final String[] trimmed = new String[caps.length];
        for (int i = 0; i < caps.length; ++i) {
            trimmed[i] = caps[i].trim();
        }
        return new DLNACaps(trimmed);
    }
    
    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        final DLNACaps dlnaCaps = (DLNACaps)o;
        return Arrays.equals(this.caps, dlnaCaps.caps);
    }
    
    @Override
    public int hashCode() {
        return Arrays.hashCode(this.caps);
    }
    
    @Override
    public String toString() {
        return ModelUtil.toCommaSeparatedList(this.getCaps());
    }
}
