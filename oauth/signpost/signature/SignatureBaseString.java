// 
// Decompiled by Procyon v0.5.30
// 

package oauth.signpost.signature;

import java.io.IOException;
import java.util.Iterator;
import java.net.URISyntaxException;
import java.net.URI;
import oauth.signpost.exception.OAuthMessageSignerException;
import oauth.signpost.OAuth;
import oauth.signpost.http.HttpParameters;
import oauth.signpost.http.HttpRequest;

public class SignatureBaseString
{
    private HttpRequest request;
    private HttpParameters requestParameters;
    
    public SignatureBaseString(final HttpRequest request, final HttpParameters requestParameters) {
        this.request = request;
        this.requestParameters = requestParameters;
    }
    
    public String generate() throws OAuthMessageSignerException {
        try {
            final String normalizedUrl = this.normalizeRequestUrl();
            final String normalizedParams = this.normalizeRequestParameters();
            return this.request.getMethod() + '&' + OAuth.percentEncode(normalizedUrl) + '&' + OAuth.percentEncode(normalizedParams);
        }
        catch (Exception e) {
            throw new OAuthMessageSignerException(e);
        }
    }
    
    public String normalizeRequestUrl() throws URISyntaxException {
        final URI uri = new URI(this.request.getRequestUrl());
        final String scheme = uri.getScheme().toLowerCase();
        String authority = uri.getAuthority().toLowerCase();
        final boolean dropPort = (scheme.equals("http") && uri.getPort() == 80) || (scheme.equals("https") && uri.getPort() == 443);
        if (dropPort) {
            final int index = authority.lastIndexOf(":");
            if (index >= 0) {
                authority = authority.substring(0, index);
            }
        }
        String path = uri.getRawPath();
        if (path == null || path.length() <= 0) {
            path = "/";
        }
        return scheme + "://" + authority + path;
    }
    
    public String normalizeRequestParameters() throws IOException {
        if (this.requestParameters == null) {
            return "";
        }
        final StringBuilder sb = new StringBuilder();
        final Iterator<String> iter = this.requestParameters.keySet().iterator();
        int i = 0;
        while (iter.hasNext()) {
            final String param = iter.next();
            if (!"oauth_signature".equals(param)) {
                if (!"realm".equals(param)) {
                    if (i > 0) {
                        sb.append("&");
                    }
                    sb.append(this.requestParameters.getAsQueryString(param, false));
                }
            }
            ++i;
        }
        return sb.toString();
    }
}
