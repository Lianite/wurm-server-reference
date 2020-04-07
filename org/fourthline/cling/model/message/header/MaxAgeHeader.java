// 
// Decompiled by Procyon v0.5.30
// 

package org.fourthline.cling.model.message.header;

import java.util.regex.Matcher;
import java.util.Locale;
import java.util.regex.Pattern;

public class MaxAgeHeader extends UpnpHeader<Integer>
{
    public static final Pattern MAX_AGE_REGEX;
    
    public MaxAgeHeader(final Integer maxAge) {
        this.setValue(maxAge);
    }
    
    public MaxAgeHeader() {
        this.setValue(1800);
    }
    
    @Override
    public void setString(final String s) throws InvalidHeaderException {
        final Matcher matcher = MaxAgeHeader.MAX_AGE_REGEX.matcher(s.toLowerCase(Locale.ROOT));
        if (!matcher.matches()) {
            throw new InvalidHeaderException("Invalid cache-control value, can't parse max-age seconds: " + s);
        }
        final Integer maxAge = Integer.parseInt(matcher.group(1));
        this.setValue(maxAge);
    }
    
    @Override
    public String getString() {
        return "max-age=" + this.getValue().toString();
    }
    
    static {
        MAX_AGE_REGEX = Pattern.compile(".*max-age\\s*=\\s*([0-9]+).*");
    }
}
