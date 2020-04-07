// 
// Decompiled by Procyon v0.5.30
// 

package org.fourthline.cling.model.types.csv;

import org.fourthline.cling.model.types.InvalidValueException;
import org.fourthline.cling.model.types.UnsignedIntegerOneByte;

public class CSVUnsignedIntegerOneByte extends CSV<UnsignedIntegerOneByte>
{
    public CSVUnsignedIntegerOneByte() {
    }
    
    public CSVUnsignedIntegerOneByte(final String s) throws InvalidValueException {
        super(s);
    }
}
