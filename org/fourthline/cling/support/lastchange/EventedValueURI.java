// 
// Decompiled by Procyon v0.5.30
// 

package org.fourthline.cling.support.lastchange;

import org.fourthline.cling.model.types.Datatype;
import org.fourthline.cling.model.types.InvalidValueException;
import org.seamless.util.Exceptions;
import java.util.Map;
import java.util.logging.Logger;
import java.net.URI;

public class EventedValueURI extends EventedValue<URI>
{
    private static final Logger log;
    
    public EventedValueURI(final URI value) {
        super(value);
    }
    
    public EventedValueURI(final Map.Entry<String, String>[] attributes) {
        super(attributes);
    }
    
    @Override
    protected URI valueOf(final String s) throws InvalidValueException {
        try {
            return super.valueOf(s);
        }
        catch (InvalidValueException ex) {
            EventedValueURI.log.info("Ignoring invalid URI in evented value '" + s + "': " + Exceptions.unwrap(ex));
            return null;
        }
    }
    
    @Override
    protected Datatype getDatatype() {
        return Datatype.Builtin.URI.getDatatype();
    }
    
    static {
        log = Logger.getLogger(EventedValueURI.class.getName());
    }
}
