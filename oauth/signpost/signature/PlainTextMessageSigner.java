// 
// Decompiled by Procyon v0.5.30
// 

package oauth.signpost.signature;

import oauth.signpost.exception.OAuthMessageSignerException;
import oauth.signpost.OAuth;
import oauth.signpost.http.HttpParameters;
import oauth.signpost.http.HttpRequest;

public class PlainTextMessageSigner extends OAuthMessageSigner
{
    public String getSignatureMethod() {
        return "PLAINTEXT";
    }
    
    public String sign(final HttpRequest request, final HttpParameters requestParams) throws OAuthMessageSignerException {
        return OAuth.percentEncode(this.getConsumerSecret()) + '&' + OAuth.percentEncode(this.getTokenSecret());
    }
}
