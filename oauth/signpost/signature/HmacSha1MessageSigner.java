// 
// Decompiled by Procyon v0.5.30
// 

package oauth.signpost.signature;

import javax.crypto.SecretKey;
import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;
import oauth.signpost.exception.OAuthMessageSignerException;
import java.security.Key;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import oauth.signpost.OAuth;
import oauth.signpost.http.HttpParameters;
import oauth.signpost.http.HttpRequest;

public class HmacSha1MessageSigner extends OAuthMessageSigner
{
    private static final String MAC_NAME = "HmacSHA1";
    
    public String getSignatureMethod() {
        return "HMAC-SHA1";
    }
    
    public String sign(final HttpRequest request, final HttpParameters requestParams) throws OAuthMessageSignerException {
        try {
            final String keyString = OAuth.percentEncode(this.getConsumerSecret()) + '&' + OAuth.percentEncode(this.getTokenSecret());
            final byte[] keyBytes = keyString.getBytes("UTF-8");
            final SecretKey key = new SecretKeySpec(keyBytes, "HmacSHA1");
            final Mac mac = Mac.getInstance("HmacSHA1");
            mac.init(key);
            final String sbs = new SignatureBaseString(request, requestParams).generate();
            OAuth.debugOut("SBS", sbs);
            final byte[] text = sbs.getBytes("UTF-8");
            return this.base64Encode(mac.doFinal(text)).trim();
        }
        catch (GeneralSecurityException e) {
            throw new OAuthMessageSignerException(e);
        }
        catch (UnsupportedEncodingException e2) {
            throw new OAuthMessageSignerException(e2);
        }
    }
}
