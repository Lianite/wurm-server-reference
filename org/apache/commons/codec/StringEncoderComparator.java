// 
// Decompiled by Procyon v0.5.30
// 

package org.apache.commons.codec;

import java.util.Comparator;

public class StringEncoderComparator implements Comparator
{
    private final StringEncoder stringEncoder;
    
    public StringEncoderComparator() {
        this.stringEncoder = null;
    }
    
    public StringEncoderComparator(final StringEncoder stringEncoder) {
        this.stringEncoder = stringEncoder;
    }
    
    public int compare(final Object o1, final Object o2) {
        int compareCode = 0;
        try {
            final Comparable s1 = (Comparable)this.stringEncoder.encode(o1);
            final Comparable s2 = (Comparable)this.stringEncoder.encode(o2);
            compareCode = s1.compareTo(s2);
        }
        catch (EncoderException ee) {
            compareCode = 0;
        }
        return compareCode;
    }
}
