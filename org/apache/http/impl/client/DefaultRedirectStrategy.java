// 
// Decompiled by Procyon v0.5.30
// 

package org.apache.http.impl.client;

import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.client.methods.HttpPatch;
import org.apache.http.client.methods.HttpOptions;
import org.apache.http.client.methods.HttpTrace;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpHead;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.params.HttpParams;
import org.apache.http.client.CircularRedirectException;
import java.net.URISyntaxException;
import org.apache.http.HttpHost;
import org.apache.http.client.utils.URIUtils;
import java.net.URI;
import org.apache.http.ProtocolException;
import org.apache.http.Header;
import org.apache.http.protocol.HttpContext;
import org.apache.http.HttpResponse;
import org.apache.http.HttpRequest;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.logging.Log;
import org.apache.http.annotation.Immutable;
import org.apache.http.client.RedirectStrategy;

@Immutable
public class DefaultRedirectStrategy implements RedirectStrategy
{
    private final Log log;
    public static final String REDIRECT_LOCATIONS = "http.protocol.redirect-locations";
    private static final String[] REDIRECT_METHODS;
    
    public DefaultRedirectStrategy() {
        this.log = LogFactory.getLog(this.getClass());
    }
    
    public boolean isRedirected(final HttpRequest request, final HttpResponse response, final HttpContext context) throws ProtocolException {
        if (request == null) {
            throw new IllegalArgumentException("HTTP request may not be null");
        }
        if (response == null) {
            throw new IllegalArgumentException("HTTP response may not be null");
        }
        final int statusCode = response.getStatusLine().getStatusCode();
        final String method = request.getRequestLine().getMethod();
        final Header locationHeader = response.getFirstHeader("location");
        switch (statusCode) {
            case 302: {
                return this.isRedirectable(method) && locationHeader != null;
            }
            case 301:
            case 307: {
                return this.isRedirectable(method);
            }
            case 303: {
                return true;
            }
            default: {
                return false;
            }
        }
    }
    
    public URI getLocationURI(final HttpRequest request, final HttpResponse response, final HttpContext context) throws ProtocolException {
        if (request == null) {
            throw new IllegalArgumentException("HTTP request may not be null");
        }
        if (response == null) {
            throw new IllegalArgumentException("HTTP response may not be null");
        }
        if (context == null) {
            throw new IllegalArgumentException("HTTP context may not be null");
        }
        final Header locationHeader = response.getFirstHeader("location");
        if (locationHeader == null) {
            throw new ProtocolException("Received redirect response " + response.getStatusLine() + " but no location header");
        }
        final String location = locationHeader.getValue();
        if (this.log.isDebugEnabled()) {
            this.log.debug("Redirect requested to location '" + location + "'");
        }
        URI uri = this.createLocationURI(location);
        final HttpParams params = request.getParams();
        try {
            uri = URIUtils.rewriteURI(uri);
            if (!uri.isAbsolute()) {
                if (params.isParameterTrue("http.protocol.reject-relative-redirect")) {
                    throw new ProtocolException("Relative redirect location '" + uri + "' not allowed");
                }
                final HttpHost target = (HttpHost)context.getAttribute("http.target_host");
                if (target == null) {
                    throw new IllegalStateException("Target host not available in the HTTP context");
                }
                final URI requestURI = new URI(request.getRequestLine().getUri());
                final URI absoluteRequestURI = URIUtils.rewriteURI(requestURI, target, true);
                uri = URIUtils.resolve(absoluteRequestURI, uri);
            }
        }
        catch (URISyntaxException ex) {
            throw new ProtocolException(ex.getMessage(), ex);
        }
        RedirectLocations redirectLocations = (RedirectLocations)context.getAttribute("http.protocol.redirect-locations");
        if (redirectLocations == null) {
            redirectLocations = new RedirectLocations();
            context.setAttribute("http.protocol.redirect-locations", redirectLocations);
        }
        if (params.isParameterFalse("http.protocol.allow-circular-redirects") && redirectLocations.contains(uri)) {
            throw new CircularRedirectException("Circular redirect to '" + uri + "'");
        }
        redirectLocations.add(uri);
        return uri;
    }
    
    protected URI createLocationURI(final String location) throws ProtocolException {
        try {
            return new URI(location).normalize();
        }
        catch (URISyntaxException ex) {
            throw new ProtocolException("Invalid redirect URI: " + location, ex);
        }
    }
    
    protected boolean isRedirectable(final String method) {
        for (final String m : DefaultRedirectStrategy.REDIRECT_METHODS) {
            if (m.equalsIgnoreCase(method)) {
                return true;
            }
        }
        return false;
    }
    
    public HttpUriRequest getRedirect(final HttpRequest request, final HttpResponse response, final HttpContext context) throws ProtocolException {
        final URI uri = this.getLocationURI(request, response, context);
        final String method = request.getRequestLine().getMethod();
        if (method.equalsIgnoreCase("HEAD")) {
            return new HttpHead(uri);
        }
        if (method.equalsIgnoreCase("GET")) {
            return new HttpGet(uri);
        }
        final int status = response.getStatusLine().getStatusCode();
        if (status == 307) {
            if (method.equalsIgnoreCase("POST")) {
                return this.copyEntity(new HttpPost(uri), request);
            }
            if (method.equalsIgnoreCase("PUT")) {
                return this.copyEntity(new HttpPut(uri), request);
            }
            if (method.equalsIgnoreCase("DELETE")) {
                return new HttpDelete(uri);
            }
            if (method.equalsIgnoreCase("TRACE")) {
                return new HttpTrace(uri);
            }
            if (method.equalsIgnoreCase("OPTIONS")) {
                return new HttpOptions(uri);
            }
            if (method.equalsIgnoreCase("PATCH")) {
                return this.copyEntity(new HttpPatch(uri), request);
            }
        }
        return new HttpGet(uri);
    }
    
    private HttpUriRequest copyEntity(final HttpEntityEnclosingRequestBase redirect, final HttpRequest original) {
        if (original instanceof HttpEntityEnclosingRequest) {
            redirect.setEntity(((HttpEntityEnclosingRequest)original).getEntity());
        }
        return redirect;
    }
    
    static {
        REDIRECT_METHODS = new String[] { "GET", "HEAD" };
    }
}
