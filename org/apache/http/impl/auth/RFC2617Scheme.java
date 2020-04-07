// 
// Decompiled by Procyon v0.5.30
// 

package org.apache.http.impl.auth;

import java.util.Locale;
import org.apache.http.HeaderElement;
import org.apache.http.message.HeaderValueParser;
import org.apache.http.auth.MalformedChallengeException;
import org.apache.http.message.ParserCursor;
import org.apache.http.message.BasicHeaderValueParser;
import org.apache.http.util.CharArrayBuffer;
import java.util.HashMap;
import org.apache.http.auth.ChallengeState;
import java.util.Map;
import org.apache.http.annotation.NotThreadSafe;

@NotThreadSafe
public abstract class RFC2617Scheme extends AuthSchemeBase
{
    private final Map<String, String> params;
    
    public RFC2617Scheme(final ChallengeState challengeState) {
        super(challengeState);
        this.params = new HashMap<String, String>();
    }
    
    public RFC2617Scheme() {
        this(null);
    }
    
    protected void parseChallenge(final CharArrayBuffer buffer, final int pos, final int len) throws MalformedChallengeException {
        final HeaderValueParser parser = BasicHeaderValueParser.DEFAULT;
        final ParserCursor cursor = new ParserCursor(pos, buffer.length());
        final HeaderElement[] elements = parser.parseElements(buffer, cursor);
        if (elements.length == 0) {
            throw new MalformedChallengeException("Authentication challenge is empty");
        }
        this.params.clear();
        for (final HeaderElement element : elements) {
            this.params.put(element.getName(), element.getValue());
        }
    }
    
    protected Map<String, String> getParameters() {
        return this.params;
    }
    
    public String getParameter(final String name) {
        if (name == null) {
            return null;
        }
        return this.params.get(name.toLowerCase(Locale.ENGLISH));
    }
    
    public String getRealm() {
        return this.getParameter("realm");
    }
}
