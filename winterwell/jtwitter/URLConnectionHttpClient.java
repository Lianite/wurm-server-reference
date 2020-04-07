// 
// Decompiled by Procyon v0.5.30
// 

package winterwell.jtwitter;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import winterwell.jtwitter.guts.Base64Encoder;
import java.io.Reader;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import winterwell.json.JSONArray;
import java.net.SocketException;
import java.net.ConnectException;
import java.io.OutputStream;
import java.io.InputStream;
import java.nio.charset.MalformedInputException;
import java.util.zip.GZIPInputStream;
import java.net.SocketTimeoutException;
import java.util.Collection;
import winterwell.json.JSONObject;
import java.io.IOException;
import java.util.Iterator;
import java.net.URLConnection;
import java.net.URL;
import java.net.HttpURLConnection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.io.Serializable;

public class URLConnectionHttpClient implements Twitter.IHttpClient, Serializable, Cloneable
{
    private static final int dfltTimeOutMilliSecs = 10000;
    private static final long serialVersionUID = 1L;
    private Map<String, List<String>> headers;
    int minRateLimit;
    protected String name;
    private String password;
    private Map<String, RateLimit> rateLimits;
    boolean retryOnError;
    protected int timeout;
    private boolean htmlImpliesError;
    private boolean gzip;
    
    @Override
    public boolean isRetryOnError() {
        return this.retryOnError;
    }
    
    public void setGzip(final boolean gzip) {
        this.gzip = gzip;
    }
    
    public void setHtmlImpliesError(final boolean htmlImpliesError) {
        this.htmlImpliesError = htmlImpliesError;
    }
    
    public URLConnectionHttpClient() {
        this(null, null);
    }
    
    public URLConnectionHttpClient(final String name, final String password) {
        this.rateLimits = Collections.synchronizedMap(new HashMap<String, RateLimit>());
        this.timeout = 10000;
        this.htmlImpliesError = true;
        this.gzip = false;
        this.name = name;
        this.password = password;
        assert name == null && password == null;
    }
    
    @Override
    public boolean canAuthenticate() {
        return this.name != null && this.password != null;
    }
    
    @Override
    public HttpURLConnection connect(String url, final Map<String, String> vars, final boolean authenticate) throws IOException {
        final String resource = this.checkRateLimit(url);
        if (vars != null && vars.size() != 0) {
            final StringBuilder uri = new StringBuilder(url);
            if (url.indexOf(63) == -1) {
                uri.append("?");
            }
            else if (!url.endsWith("&")) {
                uri.append("&");
            }
            for (final Map.Entry e : vars.entrySet()) {
                if (e.getValue() == null) {
                    continue;
                }
                final String ek = InternalUtils.encode(e.getKey());
                assert !url.contains(String.valueOf(ek) + "=") : String.valueOf(url) + " " + vars;
                uri.append(String.valueOf(ek) + "=" + InternalUtils.encode(e.getValue()) + "&");
            }
            url = uri.toString();
        }
        final HttpURLConnection connection = (HttpURLConnection)new URL(url).openConnection();
        if (authenticate) {
            this.setAuthentication(connection, this.name, this.password);
        }
        connection.setRequestProperty("User-Agent", "JTwitter/2.8.7");
        connection.setRequestProperty("Host", "api.twitter.com");
        if (this.gzip) {
            connection.setRequestProperty("Accept-Encoding", "gzip");
        }
        connection.setDoInput(true);
        connection.setConnectTimeout(this.timeout);
        connection.setReadTimeout(this.timeout);
        connection.setConnectTimeout(this.timeout);
        this.processError(connection, resource);
        this.processHeaders(connection, resource);
        return connection;
    }
    
    @Override
    public Twitter.IHttpClient copy() {
        return this.clone();
    }
    
    public URLConnectionHttpClient clone() {
        try {
            final URLConnectionHttpClient c = (URLConnectionHttpClient)super.clone();
            c.name = this.name;
            c.password = this.password;
            c.gzip = this.gzip;
            c.htmlImpliesError = this.htmlImpliesError;
            c.setRetryOnError(this.retryOnError);
            c.setTimeout(this.timeout);
            c.setMinRateLimit(this.minRateLimit);
            c.rateLimits = this.rateLimits;
            return c;
        }
        catch (CloneNotSupportedException ex) {
            throw new RuntimeException(ex);
        }
    }
    
    protected final void disconnect(final HttpURLConnection connection) {
        if (connection == null) {
            return;
        }
        try {
            connection.disconnect();
        }
        catch (Throwable t) {}
    }
    
    private String getErrorStream(final HttpURLConnection connection) {
        try {
            return InternalUtils.read(connection.getErrorStream());
        }
        catch (NullPointerException e) {
            return null;
        }
    }
    
    @Override
    public String getHeader(final String headerName) {
        if (this.headers == null) {
            return null;
        }
        final List<String> vals = this.headers.get(headerName);
        return (vals == null || vals.isEmpty()) ? null : vals.get(0);
    }
    
    String getName() {
        return this.name;
    }
    
    @Override
    public Map<String, RateLimit> getRateLimits() {
        return this.rateLimits;
    }
    
    public Map<String, RateLimit> updateRateLimits() {
        final Map<String, String> vars = null;
        final String json = this.getPage("https://api.twitter.com/1.1/application/rate_limit_status.json", vars, true);
        final JSONObject jo = new JSONObject(json).getJSONObject("resources");
        final Collection<JSONObject> families = (Collection<JSONObject>)jo.getMap().values();
        for (final JSONObject family : families) {
            for (final String res : family.getMap().keySet()) {
                final JSONObject jrl = (JSONObject)family.getMap().get(res);
                final RateLimit rl = new RateLimit(jrl);
                this.rateLimits.put(res, rl);
            }
        }
        return this.getRateLimits();
    }
    
    @Override
    public final String getPage(final String url, final Map<String, String> vars, final boolean authenticate) throws TwitterException {
        assert url != null;
        InternalUtils.count(url);
        try {
            final String json = this.getPage2(url, vars, authenticate);
            if (this.htmlImpliesError && (json.startsWith("<!DOCTYPE html") || json.startsWith("<html")) && !url.startsWith("https://twitter.com")) {
                final String meat = InternalUtils.stripTags(json);
                throw new TwitterException.E50X(meat);
            }
            return json;
        }
        catch (SocketTimeoutException e) {
            if (!this.retryOnError) {
                throw this.getPage2_ex(e, url);
            }
            try {
                Thread.sleep(500L);
                return this.getPage2(url, vars, authenticate);
            }
            catch (Exception e4) {
                throw this.getPage2_ex(e, url);
            }
        }
        catch (TwitterException.E50X e2) {
            if (!this.retryOnError) {
                throw this.getPage2_ex(e2, url);
            }
            try {
                Thread.sleep(500L);
                return this.getPage2(url, vars, authenticate);
            }
            catch (Exception e4) {
                throw this.getPage2_ex(e2, url);
            }
        }
        catch (IOException e3) {
            throw new TwitterException.IO(e3);
        }
    }
    
    private TwitterException getPage2_ex(final Exception ex, final String url) {
        if (ex instanceof TwitterException) {
            return (TwitterException)ex;
        }
        if (ex instanceof SocketTimeoutException) {
            return new TwitterException.Timeout(url);
        }
        if (ex instanceof IOException) {
            return new TwitterException.IO((IOException)ex);
        }
        return new TwitterException(ex);
    }
    
    private String getPage2(final String url, final Map<String, String> vars, final boolean authenticate) throws IOException {
        HttpURLConnection connection = null;
        try {
            connection = this.connect(url, vars, authenticate);
            InputStream inStream = connection.getInputStream();
            final String contentEncoding = connection.getContentEncoding();
            if ("gzip".equals(contentEncoding)) {
                inStream = new GZIPInputStream(inStream);
            }
            final String page = InternalUtils.read(inStream);
            return page;
        }
        catch (MalformedInputException ex) {
            throw new IOException(ex + " enc:" + connection.getContentEncoding());
        }
        finally {
            this.disconnect(connection);
        }
    }
    
    @Override
    public RateLimit getRateLimit(final Twitter.KRequestType reqType) {
        return this.rateLimits.get(reqType.rateLimit);
    }
    
    @Override
    public final String post(final String uri, final Map<String, String> vars, final boolean authenticate) throws TwitterException {
        InternalUtils.count(uri);
        try {
            final String json = this.post2(uri, vars, authenticate);
            return json;
        }
        catch (TwitterException.E50X e) {
            if (!this.retryOnError) {
                throw this.getPage2_ex(e, uri);
            }
            try {
                Thread.sleep(500L);
                return this.post2(uri, vars, authenticate);
            }
            catch (Exception e4) {
                throw this.getPage2_ex(e, uri);
            }
        }
        catch (SocketTimeoutException e2) {
            if (!this.retryOnError) {
                throw this.getPage2_ex(e2, uri);
            }
            try {
                Thread.sleep(500L);
                return this.post2(uri, vars, authenticate);
            }
            catch (Exception e4) {
                throw this.getPage2_ex(e2, uri);
            }
        }
        catch (Exception e3) {
            throw this.getPage2_ex(e3, uri);
        }
    }
    
    private String post2(final String uri, final Map<String, String> vars, final boolean authenticate) throws Exception {
        HttpURLConnection connection = null;
        try {
            connection = this.post2_connect(uri, vars);
            final String response = InternalUtils.read(connection.getInputStream());
            return response;
        }
        finally {
            this.disconnect(connection);
        }
    }
    
    @Override
    public HttpURLConnection post2_connect(final String uri, final Map<String, String> vars) throws Exception {
        final String resource = this.checkRateLimit(uri);
        InternalUtils.count(uri);
        final HttpURLConnection connection = (HttpURLConnection)new URL(uri).openConnection();
        connection.setRequestMethod("POST");
        connection.setDoOutput(true);
        this.setAuthentication(connection, this.name, this.password);
        connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        connection.setReadTimeout(this.timeout);
        connection.setConnectTimeout(this.timeout);
        final String payload = this.post2_getPayload(vars);
        connection.setRequestProperty("Content-Length", new StringBuilder().append(payload.length()).toString());
        final OutputStream os = connection.getOutputStream();
        os.write(payload.getBytes());
        InternalUtils.close(os);
        this.processError(connection, resource);
        this.processHeaders(connection, resource);
        return connection;
    }
    
    protected String checkRateLimit(final String url) {
        final String resource = RateLimit.getResource(url);
        final RateLimit limit = this.rateLimits.get(resource);
        if (limit != null && limit.getRemaining() <= this.minRateLimit && !limit.isOutOfDate()) {
            throw new TwitterException.RateLimit("Pre-emptive rate-limit block for " + limit + " for " + url);
        }
        return resource;
    }
    
    protected String post2_getPayload(final Map<String, String> vars) {
        if (vars == null || vars.isEmpty()) {
            return "";
        }
        final StringBuilder encodedData = new StringBuilder();
        if (vars.size() == 1) {
            final String key = vars.keySet().iterator().next();
            if ("".equals(key)) {
                final String val = InternalUtils.encode(vars.get(key));
                return val;
            }
        }
        final Iterator<String> iterator = vars.keySet().iterator();
        while (iterator.hasNext()) {
            final String key = iterator.next();
            final String val2 = InternalUtils.encode(vars.get(key));
            encodedData.append(InternalUtils.encode(key));
            encodedData.append('=');
            encodedData.append(val2);
            encodedData.append('&');
        }
        encodedData.deleteCharAt(encodedData.length() - 1);
        return encodedData.toString();
    }
    
    final void processError(final HttpURLConnection connection, final String resource) {
        try {
            final int code = connection.getResponseCode();
            if (code == 200) {
                return;
            }
            final URL url = connection.getURL();
            final String error = this.processError2_reason(connection);
            if (code == 401) {
                if (error.contains("Basic authentication is not supported")) {
                    throw new TwitterException.UpdateToOAuth();
                }
                throw new TwitterException.E401(String.valueOf(error) + "\n" + url + " (" + ((this.name == null) ? "anonymous" : this.name) + ")");
            }
            else {
                if (code == 400 && error.startsWith("code 215")) {
                    throw new TwitterException.E401(error);
                }
                if (code == 403) {
                    this.processError2_403(connection, resource, url, error);
                }
                if (code == 404) {
                    if (error != null && error.contains("deleted")) {
                        throw new TwitterException.SuspendedUser(String.valueOf(error) + "\n" + url);
                    }
                    throw new TwitterException.E404(String.valueOf(error) + "\n" + url);
                }
                else {
                    if (code == 406) {
                        throw new TwitterException.E406(String.valueOf(error) + "\n" + url);
                    }
                    if (code == 413) {
                        throw new TwitterException.E413(String.valueOf(error) + "\n" + url);
                    }
                    if (code == 416) {
                        throw new TwitterException.E416(String.valueOf(error) + "\n" + url);
                    }
                    if (code == 420) {
                        throw new TwitterException.TooManyLogins(String.valueOf(error) + "\n" + url);
                    }
                    if (code >= 500 && code < 600) {
                        throw new TwitterException.E50X(String.valueOf(error) + "\n" + url);
                    }
                    this.processError2_rateLimit(connection, resource, code, error);
                    if (code > 299 && code < 400) {
                        final String locn = connection.getHeaderField("Location");
                        throw new TwitterException(String.valueOf(code) + " " + error + " " + url + " -> " + locn);
                    }
                    throw new TwitterException(String.valueOf(code) + " " + error + " " + url);
                }
            }
        }
        catch (SocketTimeoutException e3) {
            final URL url = connection.getURL();
            throw new TwitterException.Timeout(String.valueOf(this.timeout) + "milli-secs for " + url);
        }
        catch (ConnectException e4) {
            final URL url = connection.getURL();
            throw new TwitterException.Timeout(url.toString());
        }
        catch (SocketException e) {
            throw new TwitterException.E50X(e.toString());
        }
        catch (IOException e2) {
            throw new TwitterException(e2);
        }
    }
    
    private String processError2_reason(final HttpURLConnection connection) throws IOException {
        final String errorPage = readErrorPage(connection);
        if (errorPage != null) {
            try {
                final JSONObject je = new JSONObject(errorPage);
                final Object error = je.get("errors");
                if (error instanceof JSONArray) {
                    final JSONObject err = ((JSONArray)error).getJSONObject(0);
                    return "code " + err.get("code") + ": " + err.getString("message");
                }
                if (error instanceof String) {
                    return (String)error;
                }
            }
            catch (Exception ex) {}
        }
        String error2 = connection.getResponseMessage();
        final Map<String, List<String>> connHeaders = connection.getHeaderFields();
        final List<String> errorMessage = connHeaders.get(null);
        if (errorMessage != null && !errorMessage.isEmpty()) {
            error2 = String.valueOf(error2) + "\n" + errorMessage.get(0);
        }
        if (errorPage != null && !errorPage.isEmpty()) {
            error2 = String.valueOf(error2) + "\n" + errorPage;
        }
        return error2;
    }
    
    private void processError2_403(final HttpURLConnection connection, final String resource, final URL url, final String errorPage) {
        final String _name = (this.name == null) ? "anon" : this.name;
        if (errorPage == null) {
            throw new TwitterException.E403(url + " (" + _name + ")");
        }
        if (errorPage.startsWith("code 185") || errorPage.contains("Wow, that's a lot of Twittering!")) {
            this.processHeaders(connection, resource);
            throw new TwitterException.RateLimit(errorPage);
        }
        if (errorPage.contains("too old")) {
            throw new TwitterException.BadParameter(String.valueOf(errorPage) + "\n" + url);
        }
        if (errorPage.contains("suspended")) {
            throw new TwitterException.SuspendedUser(String.valueOf(errorPage) + "\n" + url);
        }
        if (errorPage.contains("Could not find")) {
            throw new TwitterException.SuspendedUser(String.valueOf(errorPage) + "\n" + url);
        }
        if (errorPage.contains("too recent")) {
            throw new TwitterException.TooRecent(String.valueOf(errorPage) + "\n" + url);
        }
        if (errorPage.contains("already requested to follow")) {
            throw new TwitterException.Repetition(String.valueOf(errorPage) + "\n" + url);
        }
        if (errorPage.contains("duplicate")) {
            throw new TwitterException.Repetition(errorPage);
        }
        if (errorPage.contains("unable to follow more people")) {
            throw new TwitterException.FollowerLimit(String.valueOf(this.name) + " " + errorPage);
        }
        if (errorPage.contains("application is not allowed to access")) {
            throw new TwitterException.AccessLevel(String.valueOf(this.name) + " " + errorPage);
        }
        throw new TwitterException.E403(String.valueOf(errorPage) + "\n" + url + " (" + _name + ")");
    }
    
    private void processError2_rateLimit(final HttpURLConnection connection, final String resource, final int code, final String error) {
        final boolean rateLimitExceeded = error.contains("Rate limit exceeded");
        if (rateLimitExceeded) {
            this.processHeaders(connection, resource);
            throw new TwitterException.RateLimit(String.valueOf(this.getName()) + ": " + error);
        }
        if (code == 400) {
            try {
                final String json = this.getPage("http://twitter.com/account/rate_limit_status.json", null, this.password != null);
                final JSONObject obj = new JSONObject(json);
                final int hits = obj.getInt("remaining_hits");
                if (hits < 1) {
                    throw new TwitterException.RateLimit(error);
                }
            }
            catch (Exception ex) {}
        }
    }
    
    protected final void processHeaders(final HttpURLConnection connection, final String resource) {
        this.headers = connection.getHeaderFields();
        this.updateRateLimits(resource);
    }
    
    static String readErrorPage(final HttpURLConnection connection) {
        InputStream stream = connection.getErrorStream();
        if (stream == null) {
            return null;
        }
        try {
            if ("gzip".equals(connection.getHeaderField("Content-Encoding"))) {
                stream = new GZIPInputStream(stream);
            }
            final BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
            final int bufSize = 8192;
            final StringBuilder sb = new StringBuilder(8192);
            final char[] cbuf = new char[8192];
            try {
                while (true) {
                    final int chars = reader.read(cbuf);
                    if (chars == -1) {
                        break;
                    }
                    sb.append(cbuf, 0, chars);
                }
            }
            catch (IOException e) {
                if (sb.length() == 0) {
                    return null;
                }
                return sb.toString();
            }
            return sb.toString();
        }
        catch (IOException e2) {
            return null;
        }
        finally {
            InternalUtils.close(stream);
        }
    }
    
    protected void setAuthentication(final URLConnection connection, final String name, final String password) {
        if (name == null || password == null) {
            throw new TwitterException.E401("Authentication requested but no authorisation details are set!");
        }
        final String token = String.valueOf(name) + ":" + password;
        String encoding = Base64Encoder.encode(token);
        encoding = encoding.replace("\r\n", "");
        connection.setRequestProperty("Authorization", "Basic " + encoding);
    }
    
    public void setMinRateLimit(final int minRateLimit) {
        this.minRateLimit = minRateLimit;
    }
    
    @Override
    public void setRetryOnError(final boolean retryOnError) {
        this.retryOnError = retryOnError;
    }
    
    @Override
    public void setTimeout(final int millisecs) {
        this.timeout = millisecs;
    }
    
    @Override
    public String toString() {
        return String.valueOf(this.getClass().getName()) + "[name=" + this.name + ", password=" + ((this.password == null) ? "null" : "XXX") + "]";
    }
    
    void updateRateLimits(final String resource) {
        if (resource == null) {
            return;
        }
        final String limit = this.getHeader("X-Rate-Limit-Limit");
        if (limit == null) {
            return;
        }
        final String remaining = this.getHeader("X-Rate-Limit-Remaining");
        final String reset = this.getHeader("X-Rate-Limit-Reset");
        this.rateLimits.put(resource, new RateLimit(limit, remaining, reset));
    }
}
