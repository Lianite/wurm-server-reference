// 
// Decompiled by Procyon v0.5.30
// 

package org.fourthline.cling.model.types.csv;

import org.fourthline.cling.model.types.InvalidValueException;

public class CSVBoolean extends CSV<Boolean>
{
    public CSVBoolean() {
    }
    
    public CSVBoolean(final String s) throws InvalidValueException {
        super(s);
    }
}
