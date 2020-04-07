// 
// Decompiled by Procyon v0.5.30
// 

package oauth.signpost;

import java.io.InputStream;
import oauth.signpost.signature.QueryStringSigningStrategy;
import oauth.signpost.basic.UrlStringRequestAdapter;
import oauth.signpost.exception.OAuthMessageSignerException;
import java.io.IOException;
import oauth.signpost.exception.OAuthCommunicationException;
import java.util.SortedSet;
import java.util.Map;
import oauth.signpost.exception.OAuthExpectationFailedException;
import oauth.signpost.http.HttpRequest;
import oauth.signpost.signature.AuthorizationHeaderSigningStrategy;
import oauth.signpost.signature.HmacSha1MessageSigner;
import java.util.Random;
import oauth.signpost.http.HttpParameters;
import oauth.signpost.signature.SigningStrategy;
import oauth.signpost.signature.OAuthMessageSigner;

public abstract class AbstractOAuthConsumer implements OAuthConsumer
{
    private static final long serialVersionUID = 1L;
    private String consumerKey;
    private String consumerSecret;
    private String token;
    private OAuthMessageSigner messageSigner;
    private SigningStrategy signingStrategy;
    private HttpParameters additionalParameters;
    private HttpParameters requestParameters;
    private boolean sendEmptyTokens;
    private final Random random;
    
    public AbstractOAuthConsumer(final String consumerKey, final String consumerSecret) {
        this.random = new Random(System.nanoTime());
        this.consumerKey = consumerKey;
        this.consumerSecret = consumerSecret;
        this.setMessageSigner(new HmacSha1MessageSigner());
        this.setSigningStrategy(new AuthorizationHeaderSigningStrategy());
    }
    
    public void setMessageSigner(final OAuthMessageSigner messageSigner) {
        (this.messageSigner = messageSigner).setConsumerSecret(this.consumerSecret);
    }
    
    public void setSigningStrategy(final SigningStrategy signingStrategy) {
        this.signingStrategy = signingStrategy;
    }
    
    public void setAdditionalParameters(final HttpParameters additionalParameters) {
        this.additionalParameters = additionalParameters;
    }
    
    public synchronized HttpRequest sign(final HttpRequest request) throws OAuthMessageSignerException, OAuthExpectationFailedException, OAuthCommunicationException {
        if (this.consumerKey == null) {
            throw new OAuthExpectationFailedException("consumer key not set");
        }
        if (this.consumerSecret == null) {
            throw new OAuthExpectationFailedException("consumer secret not set");
        }
        this.requestParameters = new HttpParameters();
        try {
            if (this.additionalParameters != null) {
                this.requestParameters.putAll(this.additionalParameters, false);
            }
            this.collectHeaderParameters(request, this.requestParameters);
            this.collectQueryParameters(request, this.requestParameters);
            this.collectBodyParameters(request, this.requestParameters);
            this.completeOAuthParameters(this.requestParameters);
            this.requestParameters.remove((Object)"oauth_signature");
        }
        catch (IOException e) {
            throw new OAuthCommunicationException(e);
        }
        final String signature = this.messageSigner.sign(request, this.requestParameters);
        OAuth.debugOut("signature", signature);
        this.signingStrategy.writeSignature(signature, request, this.requestParameters);
        OAuth.debugOut("Request URL", request.getRequestUrl());
        return request;
    }
    
    public synchronized HttpRequest sign(final Object request) throws OAuthMessageSignerException, OAuthExpectationFailedException, OAuthCommunicationException {
        return this.sign(this.wrap(request));
    }
    
    public synchronized String sign(final String url) throws OAuthMessageSignerException, OAuthExpectationFailedException, OAuthCommunicationException {
        final HttpRequest request = new UrlStringRequestAdapter(url);
        final SigningStrategy oldStrategy = this.signingStrategy;
        this.signingStrategy = new QueryStringSigningStrategy();
        this.sign(request);
        this.signingStrategy = oldStrategy;
        return request.getRequestUrl();
    }
    
    protected abstract HttpRequest wrap(final Object p0);
    
    public void setTokenWithSecret(final String token, final String tokenSecret) {
        this.token = token;
        this.messageSigner.setTokenSecret(tokenSecret);
    }
    
    public String getToken() {
        return this.token;
    }
    
    public String getTokenSecret() {
        return this.messageSigner.getTokenSecret();
    }
    
    public String getConsumerKey() {
        return this.consumerKey;
    }
    
    public String getConsumerSecret() {
        return this.consumerSecret;
    }
    
    protected void completeOAuthParameters(final HttpParameters out) {
        if (!out.containsKey("oauth_consumer_key")) {
            out.put("oauth_consumer_key", this.consumerKey, true);
        }
        if (!out.containsKey("oauth_signature_method")) {
            out.put("oauth_signature_method", this.messageSigner.getSignatureMethod(), true);
        }
        if (!out.containsKey("oauth_timestamp")) {
            out.put("oauth_timestamp", this.generateTimestamp(), true);
        }
        if (!out.containsKey("oauth_nonce")) {
            out.put("oauth_nonce", this.generateNonce(), true);
        }
        if (!out.containsKey("oauth_version")) {
            out.put("oauth_version", "1.0", true);
        }
        if (!out.containsKey("oauth_token") && ((this.token != null && !this.token.equals("")) || this.sendEmptyTokens)) {
            out.put("oauth_token", this.token, true);
        }
    }
    
    public HttpParameters getRequestParameters() {
        return this.requestParameters;
    }
    
    public void setSendEmptyTokens(final boolean enable) {
        this.sendEmptyTokens = enable;
    }
    
    protected void collectHeaderParameters(final HttpRequest request, final HttpParameters out) {
        final HttpParameters headerParams = OAuth.oauthHeaderToParamsMap(request.getHeader("Authorization"));
        out.putAll(headerParams, false);
    }
    
    protected void collectBodyParameters(final HttpRequest request, final HttpParameters out) throws IOException {
        final String contentType = request.getContentType();
        if (contentType != null && contentType.startsWith("application/x-www-form-urlencoded")) {
            final InputStream payload = request.getMessagePayload();
            out.putAll(OAuth.decodeForm(payload), true);
        }
    }
    
    protected void collectQueryParameters(final HttpRequest request, final HttpParameters out) {
        final String url = request.getRequestUrl();
        final int q = url.indexOf(63);
        if (q >= 0) {
            out.putAll(OAuth.decodeForm(url.substring(q + 1)), true);
        }
    }
    
    protected String generateTimestamp() {
        return Long.toString(System.currentTimeMillis() / 1000L);
    }
    
    protected String generateNonce() {
        return Long.toString(this.random.nextLong());
    }
}
