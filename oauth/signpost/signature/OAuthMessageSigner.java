// 
// Decompiled by Procyon v0.5.30
// 

package oauth.signpost.signature;

import java.io.IOException;
import java.io.ObjectInputStream;
import oauth.signpost.exception.OAuthMessageSignerException;
import oauth.signpost.http.HttpParameters;
import oauth.signpost.http.HttpRequest;
import org.apache.commons.codec.binary.Base64;
import java.io.Serializable;

public abstract class OAuthMessageSigner implements Serializable
{
    private static final long serialVersionUID = 4445779788786131202L;
    private transient Base64 base64;
    private String consumerSecret;
    private String tokenSecret;
    
    public OAuthMessageSigner() {
        this.base64 = new Base64();
    }
    
    public abstract String sign(final HttpRequest p0, final HttpParameters p1) throws OAuthMessageSignerException;
    
    public abstract String getSignatureMethod();
    
    public String getConsumerSecret() {
        return this.consumerSecret;
    }
    
    public String getTokenSecret() {
        return this.tokenSecret;
    }
    
    public void setConsumerSecret(final String consumerSecret) {
        this.consumerSecret = consumerSecret;
    }
    
    public void setTokenSecret(final String tokenSecret) {
        this.tokenSecret = tokenSecret;
    }
    
    protected byte[] decodeBase64(final String s) {
        return this.base64.decode(s.getBytes());
    }
    
    protected String base64Encode(final byte[] b) {
        return new String(this.base64.encode(b));
    }
    
    private void readObject(final ObjectInputStream stream) throws IOException, ClassNotFoundException {
        stream.defaultReadObject();
        this.base64 = new Base64();
    }
}
