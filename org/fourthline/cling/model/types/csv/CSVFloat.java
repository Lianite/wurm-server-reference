// 
// Decompiled by Procyon v0.5.30
// 

package org.fourthline.cling.model.types.csv;

import org.fourthline.cling.model.types.InvalidValueException;

public class CSVFloat extends CSV<Float>
{
    public CSVFloat() {
    }
    
    public CSVFloat(final String s) throws InvalidValueException {
        super(s);
    }
}
