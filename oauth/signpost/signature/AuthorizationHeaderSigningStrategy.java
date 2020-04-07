// 
// Decompiled by Procyon v0.5.30
// 

package oauth.signpost.signature;

import java.util.Iterator;
import oauth.signpost.OAuth;
import oauth.signpost.http.HttpParameters;
import oauth.signpost.http.HttpRequest;

public class AuthorizationHeaderSigningStrategy implements SigningStrategy
{
    private static final long serialVersionUID = 1L;
    
    public String writeSignature(final String signature, final HttpRequest request, final HttpParameters requestParameters) {
        final StringBuilder sb = new StringBuilder();
        sb.append("OAuth ");
        if (requestParameters.containsKey("realm")) {
            sb.append(requestParameters.getAsHeaderElement("realm"));
            sb.append(", ");
        }
        final HttpParameters oauthParams = requestParameters.getOAuthParameters();
        oauthParams.put("oauth_signature", signature, true);
        final Iterator<String> iter = oauthParams.keySet().iterator();
        while (iter.hasNext()) {
            final String key = iter.next();
            sb.append(oauthParams.getAsHeaderElement(key));
            if (iter.hasNext()) {
                sb.append(", ");
            }
        }
        final String header = sb.toString();
        OAuth.debugOut("Auth Header", header);
        request.setHeader("Authorization", header);
        return header;
    }
}
