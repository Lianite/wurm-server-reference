// 
// Decompiled by Procyon v0.5.30
// 

package org.fourthline.cling.model.types.csv;

import org.fourthline.cling.model.types.InvalidValueException;

public class CSVShort extends CSV<Short>
{
    public CSVShort() {
    }
    
    public CSVShort(final String s) throws InvalidValueException {
        super(s);
    }
}
