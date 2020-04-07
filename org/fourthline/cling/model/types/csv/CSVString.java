// 
// Decompiled by Procyon v0.5.30
// 

package org.fourthline.cling.model.types.csv;

import org.fourthline.cling.model.types.InvalidValueException;

public class CSVString extends CSV<String>
{
    public CSVString() {
    }
    
    public CSVString(final String s) throws InvalidValueException {
        super(s);
    }
}
