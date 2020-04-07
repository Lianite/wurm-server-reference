// 
// Decompiled by Procyon v0.5.30
// 

package org.fourthline.cling.model.message.header;

import java.io.Serializable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TimeoutHeader extends UpnpHeader<Integer>
{
    public static final Integer INFINITE_VALUE;
    public static final Pattern PATTERN;
    
    public TimeoutHeader() {
        this.setValue(1800);
    }
    
    public TimeoutHeader(final int timeoutSeconds) {
        this.setValue(timeoutSeconds);
    }
    
    public TimeoutHeader(final Integer timeoutSeconds) {
        this.setValue(timeoutSeconds);
    }
    
    @Override
    public void setString(final String s) throws InvalidHeaderException {
        final Matcher matcher = TimeoutHeader.PATTERN.matcher(s);
        if (!matcher.matches()) {
            throw new InvalidHeaderException("Can't parse timeout seconds integer from: " + s);
        }
        if (matcher.group(1) != null) {
            this.setValue(Integer.parseInt(matcher.group(1)));
        }
        else {
            this.setValue(TimeoutHeader.INFINITE_VALUE);
        }
    }
    
    @Override
    public String getString() {
        return "Second-" + (this.getValue().equals(TimeoutHeader.INFINITE_VALUE) ? "infinite" : ((UpnpHeader<Serializable>)this).getValue());
    }
    
    static {
        INFINITE_VALUE = Integer.MAX_VALUE;
        PATTERN = Pattern.compile("Second-(?:([0-9]+)|infinite)");
    }
}
