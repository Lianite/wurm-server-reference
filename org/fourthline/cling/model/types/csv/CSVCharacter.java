// 
// Decompiled by Procyon v0.5.30
// 

package org.fourthline.cling.model.types.csv;

import org.fourthline.cling.model.types.InvalidValueException;

public class CSVCharacter extends CSV<Character>
{
    public CSVCharacter() {
    }
    
    public CSVCharacter(final String s) throws InvalidValueException {
        super(s);
    }
}
