// 
// Decompiled by Procyon v0.5.30
// 

package org.fourthline.cling.support.model.dlna.message.header;

import org.fourthline.cling.model.message.header.InvalidHeaderException;
import java.util.regex.Pattern;

public class EventTypeHeader extends DLNAHeader<String>
{
    static final Pattern pattern;
    
    public EventTypeHeader() {
        this.setValue("0000");
    }
    
    @Override
    public void setString(final String s) throws InvalidHeaderException {
        if (EventTypeHeader.pattern.matcher(s).matches()) {
            this.setValue(s);
            return;
        }
        throw new InvalidHeaderException("Invalid EventType header value: " + s);
    }
    
    @Override
    public String getString() {
        return this.getValue().toString();
    }
    
    static {
        pattern = Pattern.compile("^[0-9]{4}$", 2);
    }
}
