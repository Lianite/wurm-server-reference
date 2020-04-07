// 
// Decompiled by Procyon v0.5.30
// 

package org.fourthline.cling.model.types.csv;

import org.fourthline.cling.model.types.InvalidValueException;

public class CSVInteger extends CSV<Integer>
{
    public CSVInteger() {
    }
    
    public CSVInteger(final String s) throws InvalidValueException {
        super(s);
    }
}
