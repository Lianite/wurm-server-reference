// 
// Decompiled by Procyon v0.5.30
// 

package oauth.signpost.signature;

import java.util.Iterator;
import oauth.signpost.OAuth;
import oauth.signpost.http.HttpParameters;
import oauth.signpost.http.HttpRequest;

public class QueryStringSigningStrategy implements SigningStrategy
{
    private static final long serialVersionUID = 1L;
    
    public String writeSignature(final String signature, final HttpRequest request, final HttpParameters requestParameters) {
        final HttpParameters oauthParams = requestParameters.getOAuthParameters();
        oauthParams.put("oauth_signature", signature, true);
        final Iterator<String> iter = oauthParams.keySet().iterator();
        final String firstKey = iter.next();
        final StringBuilder sb = new StringBuilder(OAuth.addQueryString(request.getRequestUrl(), oauthParams.getAsQueryString(firstKey)));
        while (iter.hasNext()) {
            sb.append("&");
            final String key = iter.next();
            sb.append(oauthParams.getAsQueryString(key));
        }
        final String signedUrl = sb.toString();
        request.setRequestUrl(signedUrl);
        return signedUrl;
    }
}
