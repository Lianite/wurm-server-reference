// 
// Decompiled by Procyon v0.5.30
// 

package org.fourthline.cling.model.types.csv;

import org.fourthline.cling.model.types.InvalidValueException;
import java.net.URI;

public class CSVURI extends CSV<URI>
{
    public CSVURI() {
    }
    
    public CSVURI(final String s) throws InvalidValueException {
        super(s);
    }
}
