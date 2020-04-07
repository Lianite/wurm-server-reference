// 
// Decompiled by Procyon v0.5.30
// 

package org.fourthline.cling.model.types.csv;

import org.fourthline.cling.model.types.InvalidValueException;

public class CSVBytes extends CSV<byte[]>
{
    public CSVBytes() {
    }
    
    public CSVBytes(final String s) throws InvalidValueException {
        super(s);
    }
}
