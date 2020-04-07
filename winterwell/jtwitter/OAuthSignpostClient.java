// 
// Decompiled by Procyon v0.5.30
// 

package winterwell.jtwitter;

import oauth.signpost.exception.OAuthException;
import java.io.OutputStream;
import java.net.URI;
import java.lang.reflect.Method;
import oauth.signpost.signature.SigningStrategy;
import java.net.URLConnection;
import winterwell.jtwitter.guts.ClientHttpRequest;
import oauth.signpost.http.HttpRequest;
import oauth.signpost.signature.AuthorizationHeaderSigningStrategy;
import java.io.IOException;
import java.io.StringBufferInputStream;
import java.io.InputStream;
import oauth.signpost.basic.HttpURLConnectionRequestAdapter;
import java.util.HashMap;
import java.net.URL;
import java.net.HttpURLConnection;
import java.util.Map;
import oauth.signpost.OAuthConsumer;
import oauth.signpost.basic.DefaultOAuthProvider;
import java.io.Serializable;

public class OAuthSignpostClient extends URLConnectionHttpClient implements Twitter.IHttpClient, Serializable
{
    private static final DefaultOAuthProvider FOURSQUARE_PROVIDER;
    private static final DefaultOAuthProvider LINKEDIN_PROVIDER;
    public static final String JTWITTER_OAUTH_KEY = "Cz8ZLgitPR2jrQVaD6ncw";
    public static final String JTWITTER_OAUTH_SECRET = "9FFYaWJSvQ6Yi5tctN30eN6DnXWmdw0QgJMl7V6KGI";
    private static final long serialVersionUID = 1L;
    private String accessToken;
    private String accessTokenSecret;
    private String callbackUrl;
    private OAuthConsumer consumer;
    private String consumerKey;
    private String consumerSecret;
    private DefaultOAuthProvider provider;
    
    static {
        FOURSQUARE_PROVIDER = new DefaultOAuthProvider("http://foursquare.com/oauth/request_token", "http://foursquare.com/oauth/access_token", "http://foursquare.com/oauth/authorize");
        LINKEDIN_PROVIDER = new DefaultOAuthProvider("https://api.linkedin.com/uas/oauth/requestToken", "https://api.linkedin.com/uas/oauth/accessToken", "https://www.linkedin.com/uas/oauth/authorize");
    }
    
    public final String postMultipartForm(final String url, final Map<String, ?> vars) throws TwitterException {
        final String resource = this.checkRateLimit(url);
        try {
            final HttpURLConnection connection = (HttpURLConnection)new URL(url).openConnection();
            connection.setRequestMethod("POST");
            connection.setReadTimeout(this.timeout);
            connection.setConnectTimeout(this.timeout);
            final Map<String, String> vars2 = new HashMap<String, String>();
            final String payload = this.post2_getPayload(vars2);
            final HttpURLConnectionRequestAdapter wrapped = new HttpURLConnectionRequestAdapter(connection) {
                @Override
                public InputStream getMessagePayload() throws IOException {
                    return new StringBufferInputStream(payload);
                }
            };
            final SimpleOAuthConsumer _consumer = new SimpleOAuthConsumer(this.consumerKey, this.consumerSecret);
            _consumer.setTokenWithSecret(this.accessToken, this.accessTokenSecret);
            final SigningStrategy ss = new AuthorizationHeaderSigningStrategy();
            _consumer.setSigningStrategy(ss);
            _consumer.sign(wrapped);
            final ClientHttpRequest req = new ClientHttpRequest(connection);
            final InputStream page = req.post(vars);
            this.processError(connection, resource);
            this.processHeaders(connection, resource);
            return InternalUtils.read(page);
        }
        catch (TwitterException e) {
            throw e;
        }
        catch (Exception e2) {
            throw new TwitterException(e2);
        }
    }
    
    public static String askUser(final String question) {
        try {
            final Class<?> JOptionPaneClass = Class.forName("javax.swing.JOptionPane");
            final Method showInputDialog = JOptionPaneClass.getMethod("showInputDialog", Object.class);
            return (String)showInputDialog.invoke(null, question);
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    
    public OAuthSignpostClient(final String consumerKey, final String consumerSecret, final String callbackUrl) {
        assert consumerKey != null && consumerSecret != null && callbackUrl != null;
        this.consumerKey = consumerKey;
        this.consumerSecret = consumerSecret;
        this.callbackUrl = callbackUrl;
        this.init();
    }
    
    public OAuthSignpostClient(final String consumerKey, final String consumerSecret, final String accessToken, final String accessTokenSecret) {
        this.consumerKey = consumerKey.toString();
        this.consumerSecret = consumerSecret.toString();
        this.accessToken = accessToken.toString();
        this.accessTokenSecret = accessTokenSecret.toString();
        this.init();
    }
    
    @Deprecated
    public void authorizeDesktop() {
        final URI uri = this.authorizeUrl();
        try {
            final Class<?> desktopClass = Class.forName("java.awt.Desktop");
            final Method getDesktop = desktopClass.getMethod("getDesktop", (Class<?>[])null);
            final Object d = getDesktop.invoke(null, (Object[])null);
            final Method browse = desktopClass.getMethod("browse", URI.class);
            browse.invoke(d, uri);
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    
    public URI authorizeUrl() {
        try {
            final String url = this.provider.retrieveRequestToken(this.consumer, this.callbackUrl);
            return new URI(url);
        }
        catch (Exception e) {
            throw new TwitterException(e);
        }
    }
    
    @Override
    public boolean canAuthenticate() {
        return this.consumer.getToken() != null;
    }
    
    @Override
    public Twitter.IHttpClient copy() {
        return this.clone();
    }
    
    @Override
    public URLConnectionHttpClient clone() {
        final OAuthSignpostClient c = (OAuthSignpostClient)super.clone();
        c.consumerKey = this.consumerKey;
        c.consumerSecret = this.consumerSecret;
        c.accessToken = this.accessToken;
        c.accessTokenSecret = this.accessTokenSecret;
        c.callbackUrl = this.callbackUrl;
        c.init();
        return c;
    }
    
    public String[] getAccessToken() {
        if (this.accessToken == null) {
            return null;
        }
        return new String[] { this.accessToken, this.accessTokenSecret };
    }
    
    @Override
    String getName() {
        return (this.name == null) ? "?user" : this.name;
    }
    
    private void init() {
        this.consumer = new SimpleOAuthConsumer(this.consumerKey, this.consumerSecret);
        if (this.accessToken != null) {
            this.consumer.setTokenWithSecret(this.accessToken, this.accessTokenSecret);
        }
        this.provider = new DefaultOAuthProvider("https://api.twitter.com/oauth/request_token", "https://api.twitter.com/oauth/access_token", "https://api.twitter.com/oauth/authorize");
    }
    
    @Override
    public HttpURLConnection post2_connect(final String uri, final Map<String, String> vars) throws IOException, OAuthException {
        final String resource = this.checkRateLimit(uri);
        final HttpURLConnection connection = (HttpURLConnection)new URL(uri).openConnection();
        connection.setRequestMethod("POST");
        connection.setDoOutput(true);
        connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        connection.setReadTimeout(this.timeout);
        connection.setConnectTimeout(this.timeout);
        final String payload = this.post2_getPayload(vars);
        final HttpURLConnectionRequestAdapter wrapped = new HttpURLConnectionRequestAdapter(connection) {
            @Override
            public InputStream getMessagePayload() throws IOException {
                return new StringBufferInputStream(payload);
            }
        };
        this.consumer.sign(wrapped);
        final OutputStream os = connection.getOutputStream();
        os.write(payload.getBytes());
        InternalUtils.close(os);
        this.processError(connection, resource);
        this.processHeaders(connection, resource);
        return connection;
    }
    
    @Override
    protected void setAuthentication(final URLConnection connection, final String name, final String password) {
        try {
            this.consumer.sign(connection);
        }
        catch (OAuthException e) {
            throw new TwitterException(e);
        }
    }
    
    public void setAuthorizationCode(final String verifier) throws TwitterException {
        if (this.accessToken != null) {
            this.accessToken = null;
            this.init();
        }
        try {
            this.provider.retrieveAccessToken(this.consumer, verifier);
            this.accessToken = this.consumer.getToken();
            this.accessTokenSecret = this.consumer.getTokenSecret();
        }
        catch (Exception e) {
            if (e.getMessage().contains("401")) {
                throw new TwitterException.E401(e.getMessage());
            }
            throw new TwitterException(e);
        }
    }
    
    public void setFoursquareProvider() {
        this.setProvider(OAuthSignpostClient.FOURSQUARE_PROVIDER);
    }
    
    public void setLinkedInProvider() {
        this.setProvider(OAuthSignpostClient.LINKEDIN_PROVIDER);
    }
    
    public void setName(final String name) {
        this.name = name;
    }
    
    public void setProvider(final DefaultOAuthProvider provider) {
        this.provider = provider;
    }
}
