// 
// Decompiled by Procyon v0.5.30
// 

package org.fourthline.cling.model.types.csv;

import org.fourthline.cling.model.types.InvalidValueException;
import org.fourthline.cling.model.types.UnsignedIntegerTwoBytes;

public class CSVUnsignedIntegerTwoBytes extends CSV<UnsignedIntegerTwoBytes>
{
    public CSVUnsignedIntegerTwoBytes() {
    }
    
    public CSVUnsignedIntegerTwoBytes(final String s) throws InvalidValueException {
        super(s);
    }
}
