// 
// Decompiled by Procyon v0.5.30
// 

package org.apache.http.impl.client;

import javax.net.ssl.SSLSession;
import org.apache.http.conn.HttpRoutedConnection;
import org.apache.http.impl.DefaultHttpClientConnection;
import java.io.IOException;
import org.apache.http.HttpRequest;
import org.apache.http.ProtocolVersion;
import org.apache.http.HttpResponse;
import org.apache.http.protocol.HttpContext;
import org.apache.http.HttpEntity;
import org.apache.http.entity.BufferedHttpEntity;
import org.apache.http.util.EntityUtils;
import org.apache.http.client.AuthenticationStrategy;
import org.apache.http.client.params.HttpClientParams;
import org.apache.http.HttpException;
import org.apache.http.HttpClientConnection;
import org.apache.http.auth.AuthScope;
import org.apache.http.message.BasicHttpRequest;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.conn.routing.HttpRoute;
import java.net.Socket;
import org.apache.http.auth.Credentials;
import org.apache.http.HttpHost;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.impl.DefaultConnectionReuseStrategy;
import org.apache.http.impl.auth.KerberosSchemeFactory;
import org.apache.http.impl.auth.SPNegoSchemeFactory;
import org.apache.http.impl.auth.NTLMSchemeFactory;
import org.apache.http.impl.auth.DigestSchemeFactory;
import org.apache.http.auth.AuthSchemeFactory;
import org.apache.http.impl.auth.BasicSchemeFactory;
import org.apache.http.protocol.ImmutableHttpProcessor;
import org.apache.http.client.protocol.RequestProxyAuthentication;
import org.apache.http.protocol.RequestUserAgent;
import org.apache.http.client.protocol.RequestClientConnControl;
import org.apache.http.protocol.RequestTargetHost;
import org.apache.http.protocol.RequestContent;
import org.apache.http.HttpRequestInterceptor;
import org.apache.http.params.HttpParams;
import org.apache.http.ConnectionReuseStrategy;
import org.apache.http.auth.AuthSchemeRegistry;
import org.apache.http.auth.AuthState;
import org.apache.http.protocol.HttpRequestExecutor;
import org.apache.http.protocol.HttpProcessor;

public class ProxyClient
{
    private final HttpProcessor httpProcessor;
    private final HttpRequestExecutor requestExec;
    private final ProxyAuthenticationStrategy proxyAuthStrategy;
    private final HttpAuthenticator authenticator;
    private final AuthState proxyAuthState;
    private final AuthSchemeRegistry authSchemeRegistry;
    private final ConnectionReuseStrategy reuseStrategy;
    private final HttpParams params;
    
    public ProxyClient(final HttpParams params) {
        if (params == null) {
            throw new IllegalArgumentException("HTTP parameters may not be null");
        }
        this.httpProcessor = new ImmutableHttpProcessor(new HttpRequestInterceptor[] { new RequestContent(), new RequestTargetHost(), new RequestClientConnControl(), new RequestUserAgent(), new RequestProxyAuthentication() });
        this.requestExec = new HttpRequestExecutor();
        this.proxyAuthStrategy = new ProxyAuthenticationStrategy();
        this.authenticator = new HttpAuthenticator();
        this.proxyAuthState = new AuthState();
        (this.authSchemeRegistry = new AuthSchemeRegistry()).register("Basic", new BasicSchemeFactory());
        this.authSchemeRegistry.register("Digest", new DigestSchemeFactory());
        this.authSchemeRegistry.register("NTLM", new NTLMSchemeFactory());
        this.authSchemeRegistry.register("negotiate", new SPNegoSchemeFactory());
        this.authSchemeRegistry.register("Kerberos", new KerberosSchemeFactory());
        this.reuseStrategy = new DefaultConnectionReuseStrategy();
        this.params = params;
    }
    
    public ProxyClient() {
        this(new BasicHttpParams());
    }
    
    public HttpParams getParams() {
        return this.params;
    }
    
    public AuthSchemeRegistry getAuthSchemeRegistry() {
        return this.authSchemeRegistry;
    }
    
    public Socket tunnel(final HttpHost proxy, final HttpHost target, final Credentials credentials) throws IOException, HttpException {
        final ProxyConnection conn = new ProxyConnection(new HttpRoute(proxy));
        final HttpContext context = new BasicHttpContext();
        HttpResponse response = null;
        while (true) {
            if (!conn.isOpen()) {
                final Socket socket = new Socket(proxy.getHostName(), proxy.getPort());
                conn.bind(socket, this.params);
            }
            final String host = target.getHostName();
            int port = target.getPort();
            if (port < 0) {
                port = 80;
            }
            final StringBuilder buffer = new StringBuilder(host.length() + 6);
            buffer.append(host);
            buffer.append(':');
            buffer.append(Integer.toString(port));
            final String authority = buffer.toString();
            final ProtocolVersion ver = HttpProtocolParams.getVersion(this.params);
            final HttpRequest connect = new BasicHttpRequest("CONNECT", authority, ver);
            connect.setParams(this.params);
            final BasicCredentialsProvider credsProvider = new BasicCredentialsProvider();
            credsProvider.setCredentials(new AuthScope(proxy), credentials);
            context.setAttribute("http.target_host", target);
            context.setAttribute("http.proxy_host", proxy);
            context.setAttribute("http.connection", conn);
            context.setAttribute("http.request", connect);
            context.setAttribute("http.auth.proxy-scope", this.proxyAuthState);
            context.setAttribute("http.auth.credentials-provider", credsProvider);
            context.setAttribute("http.authscheme-registry", this.authSchemeRegistry);
            this.requestExec.preProcess(connect, this.httpProcessor, context);
            response = this.requestExec.execute(connect, conn, context);
            response.setParams(this.params);
            this.requestExec.postProcess(response, this.httpProcessor, context);
            final int status = response.getStatusLine().getStatusCode();
            if (status < 200) {
                throw new HttpException("Unexpected response to CONNECT request: " + response.getStatusLine());
            }
            if (!HttpClientParams.isAuthenticating(this.params)) {
                continue;
            }
            if (this.authenticator.isAuthenticationRequested(proxy, response, this.proxyAuthStrategy, this.proxyAuthState, context) && this.authenticator.authenticate(proxy, response, this.proxyAuthStrategy, this.proxyAuthState, context)) {
                if (this.reuseStrategy.keepAlive(response, context)) {
                    final HttpEntity entity = response.getEntity();
                    EntityUtils.consume(entity);
                }
                else {
                    conn.close();
                }
            }
            else {
                final int status2 = response.getStatusLine().getStatusCode();
                if (status2 > 299) {
                    final HttpEntity entity2 = response.getEntity();
                    if (entity2 != null) {
                        response.setEntity(new BufferedHttpEntity(entity2));
                    }
                    conn.close();
                    throw new TunnelRefusedException("CONNECT refused by proxy: " + response.getStatusLine(), response);
                }
                return conn.getSocket();
            }
        }
    }
    
    static class ProxyConnection extends DefaultHttpClientConnection implements HttpRoutedConnection
    {
        private final HttpRoute route;
        
        ProxyConnection(final HttpRoute route) {
            this.route = route;
        }
        
        public HttpRoute getRoute() {
            return this.route;
        }
        
        public boolean isSecure() {
            return false;
        }
        
        public SSLSession getSSLSession() {
            return null;
        }
        
        public Socket getSocket() {
            return super.getSocket();
        }
    }
}
