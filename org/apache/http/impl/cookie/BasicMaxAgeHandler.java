// 
// Decompiled by Procyon v0.5.30
// 

package org.apache.http.impl.cookie;

import java.util.Date;
import org.apache.http.cookie.MalformedCookieException;
import org.apache.http.cookie.SetCookie;
import org.apache.http.annotation.Immutable;

@Immutable
public class BasicMaxAgeHandler extends AbstractCookieAttributeHandler
{
    public void parse(final SetCookie cookie, final String value) throws MalformedCookieException {
        if (cookie == null) {
            throw new IllegalArgumentException("Cookie may not be null");
        }
        if (value == null) {
            throw new MalformedCookieException("Missing value for max-age attribute");
        }
        int age;
        try {
            age = Integer.parseInt(value);
        }
        catch (NumberFormatException e) {
            throw new MalformedCookieException("Invalid max-age attribute: " + value);
        }
        if (age < 0) {
            throw new MalformedCookieException("Negative max-age attribute: " + value);
        }
        cookie.setExpiryDate(new Date(System.currentTimeMillis() + age * 1000L));
    }
}
