// 
// Decompiled by Procyon v0.5.30
// 

package org.fourthline.cling.model.types.csv;

import org.fourthline.cling.model.types.InvalidValueException;
import java.util.Date;

public class CSVDate extends CSV<Date>
{
    public CSVDate() {
    }
    
    public CSVDate(final String s) throws InvalidValueException {
        super(s);
    }
}
