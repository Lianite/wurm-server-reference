// 
// Decompiled by Procyon v0.5.30
// 

package org.apache.http.conn.ssl;

import org.apache.http.HttpHost;
import java.net.InetAddress;
import java.net.UnknownHostException;
import org.apache.http.conn.HttpInetSocketAddress;
import java.net.SocketTimeoutException;
import org.apache.http.conn.ConnectTimeoutException;
import java.net.SocketAddress;
import org.apache.http.params.HttpConnectionParams;
import java.net.InetSocketAddress;
import java.io.IOException;
import javax.net.ssl.SSLSocket;
import java.net.Socket;
import org.apache.http.params.HttpParams;
import java.security.UnrecoverableKeyException;
import java.security.KeyStoreException;
import javax.net.ssl.X509TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.KeyManagerFactory;
import java.security.KeyStore;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import javax.net.ssl.TrustManager;
import javax.net.ssl.KeyManager;
import javax.net.ssl.SSLContext;
import org.apache.http.conn.scheme.HostNameResolver;
import org.apache.http.annotation.ThreadSafe;
import org.apache.http.conn.scheme.LayeredSocketFactory;
import org.apache.http.conn.scheme.LayeredSchemeSocketFactory;
import org.apache.http.conn.scheme.SchemeLayeredSocketFactory;

@ThreadSafe
public class SSLSocketFactory implements SchemeLayeredSocketFactory, LayeredSchemeSocketFactory, LayeredSocketFactory
{
    public static final String TLS = "TLS";
    public static final String SSL = "SSL";
    public static final String SSLV2 = "SSLv2";
    public static final X509HostnameVerifier ALLOW_ALL_HOSTNAME_VERIFIER;
    public static final X509HostnameVerifier BROWSER_COMPATIBLE_HOSTNAME_VERIFIER;
    public static final X509HostnameVerifier STRICT_HOSTNAME_VERIFIER;
    private final javax.net.ssl.SSLSocketFactory socketfactory;
    private final HostNameResolver nameResolver;
    private volatile X509HostnameVerifier hostnameVerifier;
    
    public static SSLSocketFactory getSocketFactory() throws SSLInitializationException {
        try {
            final SSLContext sslcontext = SSLContext.getInstance("TLS");
            sslcontext.init(null, null, null);
            return new SSLSocketFactory(sslcontext, SSLSocketFactory.BROWSER_COMPATIBLE_HOSTNAME_VERIFIER);
        }
        catch (NoSuchAlgorithmException ex) {
            throw new SSLInitializationException(ex.getMessage(), ex);
        }
        catch (KeyManagementException ex2) {
            throw new SSLInitializationException(ex2.getMessage(), ex2);
        }
    }
    
    public static SSLSocketFactory getSystemSocketFactory() throws SSLInitializationException {
        return new SSLSocketFactory((javax.net.ssl.SSLSocketFactory)javax.net.ssl.SSLSocketFactory.getDefault(), SSLSocketFactory.BROWSER_COMPATIBLE_HOSTNAME_VERIFIER);
    }
    
    private static SSLContext createSSLContext(String algorithm, final KeyStore keystore, final String keyPassword, final KeyStore truststore, final SecureRandom random, final TrustStrategy trustStrategy) throws NoSuchAlgorithmException, KeyStoreException, UnrecoverableKeyException, KeyManagementException {
        if (algorithm == null) {
            algorithm = "TLS";
        }
        final KeyManagerFactory kmfactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
        kmfactory.init(keystore, (char[])((keyPassword != null) ? keyPassword.toCharArray() : null));
        final KeyManager[] keymanagers = kmfactory.getKeyManagers();
        final TrustManagerFactory tmfactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
        tmfactory.init(truststore);
        final TrustManager[] trustmanagers = tmfactory.getTrustManagers();
        if (trustmanagers != null && trustStrategy != null) {
            for (int i = 0; i < trustmanagers.length; ++i) {
                final TrustManager tm = trustmanagers[i];
                if (tm instanceof X509TrustManager) {
                    trustmanagers[i] = new TrustManagerDecorator((X509TrustManager)tm, trustStrategy);
                }
            }
        }
        final SSLContext sslcontext = SSLContext.getInstance(algorithm);
        sslcontext.init(keymanagers, trustmanagers, random);
        return sslcontext;
    }
    
    public SSLSocketFactory(final String algorithm, final KeyStore keystore, final String keyPassword, final KeyStore truststore, final SecureRandom random, final HostNameResolver nameResolver) throws NoSuchAlgorithmException, KeyManagementException, KeyStoreException, UnrecoverableKeyException {
        this(createSSLContext(algorithm, keystore, keyPassword, truststore, random, null), nameResolver);
    }
    
    public SSLSocketFactory(final String algorithm, final KeyStore keystore, final String keyPassword, final KeyStore truststore, final SecureRandom random, final X509HostnameVerifier hostnameVerifier) throws NoSuchAlgorithmException, KeyManagementException, KeyStoreException, UnrecoverableKeyException {
        this(createSSLContext(algorithm, keystore, keyPassword, truststore, random, null), hostnameVerifier);
    }
    
    public SSLSocketFactory(final String algorithm, final KeyStore keystore, final String keyPassword, final KeyStore truststore, final SecureRandom random, final TrustStrategy trustStrategy, final X509HostnameVerifier hostnameVerifier) throws NoSuchAlgorithmException, KeyManagementException, KeyStoreException, UnrecoverableKeyException {
        this(createSSLContext(algorithm, keystore, keyPassword, truststore, random, trustStrategy), hostnameVerifier);
    }
    
    public SSLSocketFactory(final KeyStore keystore, final String keystorePassword, final KeyStore truststore) throws NoSuchAlgorithmException, KeyManagementException, KeyStoreException, UnrecoverableKeyException {
        this("TLS", keystore, keystorePassword, truststore, null, null, SSLSocketFactory.BROWSER_COMPATIBLE_HOSTNAME_VERIFIER);
    }
    
    public SSLSocketFactory(final KeyStore keystore, final String keystorePassword) throws NoSuchAlgorithmException, KeyManagementException, KeyStoreException, UnrecoverableKeyException {
        this("TLS", keystore, keystorePassword, null, null, null, SSLSocketFactory.BROWSER_COMPATIBLE_HOSTNAME_VERIFIER);
    }
    
    public SSLSocketFactory(final KeyStore truststore) throws NoSuchAlgorithmException, KeyManagementException, KeyStoreException, UnrecoverableKeyException {
        this("TLS", null, null, truststore, null, null, SSLSocketFactory.BROWSER_COMPATIBLE_HOSTNAME_VERIFIER);
    }
    
    public SSLSocketFactory(final TrustStrategy trustStrategy, final X509HostnameVerifier hostnameVerifier) throws NoSuchAlgorithmException, KeyManagementException, KeyStoreException, UnrecoverableKeyException {
        this("TLS", null, null, null, null, trustStrategy, hostnameVerifier);
    }
    
    public SSLSocketFactory(final TrustStrategy trustStrategy) throws NoSuchAlgorithmException, KeyManagementException, KeyStoreException, UnrecoverableKeyException {
        this("TLS", null, null, null, null, trustStrategy, SSLSocketFactory.BROWSER_COMPATIBLE_HOSTNAME_VERIFIER);
    }
    
    public SSLSocketFactory(final SSLContext sslContext) {
        this(sslContext, SSLSocketFactory.BROWSER_COMPATIBLE_HOSTNAME_VERIFIER);
    }
    
    public SSLSocketFactory(final SSLContext sslContext, final HostNameResolver nameResolver) {
        this.socketfactory = sslContext.getSocketFactory();
        this.hostnameVerifier = SSLSocketFactory.BROWSER_COMPATIBLE_HOSTNAME_VERIFIER;
        this.nameResolver = nameResolver;
    }
    
    public SSLSocketFactory(final SSLContext sslContext, final X509HostnameVerifier hostnameVerifier) {
        if (sslContext == null) {
            throw new IllegalArgumentException("SSL context may not be null");
        }
        this.socketfactory = sslContext.getSocketFactory();
        this.hostnameVerifier = hostnameVerifier;
        this.nameResolver = null;
    }
    
    public SSLSocketFactory(final javax.net.ssl.SSLSocketFactory socketfactory, final X509HostnameVerifier hostnameVerifier) {
        if (socketfactory == null) {
            throw new IllegalArgumentException("SSL socket factory may not be null");
        }
        this.socketfactory = socketfactory;
        this.hostnameVerifier = hostnameVerifier;
        this.nameResolver = null;
    }
    
    public Socket createSocket(final HttpParams params) throws IOException {
        final SSLSocket sock = (SSLSocket)this.socketfactory.createSocket();
        this.prepareSocket(sock);
        return sock;
    }
    
    @Deprecated
    public Socket createSocket() throws IOException {
        final SSLSocket sock = (SSLSocket)this.socketfactory.createSocket();
        this.prepareSocket(sock);
        return sock;
    }
    
    public Socket connectSocket(final Socket socket, final InetSocketAddress remoteAddress, final InetSocketAddress localAddress, final HttpParams params) throws IOException, UnknownHostException, ConnectTimeoutException {
        if (remoteAddress == null) {
            throw new IllegalArgumentException("Remote address may not be null");
        }
        if (params == null) {
            throw new IllegalArgumentException("HTTP parameters may not be null");
        }
        final Socket sock = (socket != null) ? socket : this.socketfactory.createSocket();
        if (localAddress != null) {
            sock.setReuseAddress(HttpConnectionParams.getSoReuseaddr(params));
            sock.bind(localAddress);
        }
        final int connTimeout = HttpConnectionParams.getConnectionTimeout(params);
        final int soTimeout = HttpConnectionParams.getSoTimeout(params);
        try {
            sock.setSoTimeout(soTimeout);
            sock.connect(remoteAddress, connTimeout);
        }
        catch (SocketTimeoutException ex) {
            throw new ConnectTimeoutException("Connect to " + remoteAddress + " timed out", ex);
        }
        String hostname;
        if (remoteAddress instanceof HttpInetSocketAddress) {
            hostname = ((HttpInetSocketAddress)remoteAddress).getHttpHost().getHostName();
        }
        else {
            hostname = remoteAddress.getHostName();
        }
        SSLSocket sslsock;
        if (sock instanceof SSLSocket) {
            sslsock = (SSLSocket)sock;
        }
        else {
            final int port = remoteAddress.getPort();
            sslsock = (SSLSocket)this.socketfactory.createSocket(sock, hostname, port, true);
            this.prepareSocket(sslsock);
        }
        sslsock.startHandshake();
        if (this.hostnameVerifier != null) {
            try {
                this.hostnameVerifier.verify(hostname, sslsock);
            }
            catch (IOException iox) {
                try {
                    sslsock.close();
                }
                catch (Exception ex2) {}
                throw iox;
            }
        }
        return sslsock;
    }
    
    public boolean isSecure(final Socket sock) throws IllegalArgumentException {
        if (sock == null) {
            throw new IllegalArgumentException("Socket may not be null");
        }
        if (!(sock instanceof SSLSocket)) {
            throw new IllegalArgumentException("Socket not created by this factory");
        }
        if (sock.isClosed()) {
            throw new IllegalArgumentException("Socket is closed");
        }
        return true;
    }
    
    public Socket createLayeredSocket(final Socket socket, final String host, final int port, final HttpParams params) throws IOException, UnknownHostException {
        final SSLSocket sslSocket = (SSLSocket)this.socketfactory.createSocket(socket, host, port, true);
        this.prepareSocket(sslSocket);
        sslSocket.startHandshake();
        if (this.hostnameVerifier != null) {
            this.hostnameVerifier.verify(host, sslSocket);
        }
        return sslSocket;
    }
    
    public Socket createLayeredSocket(final Socket socket, final String host, final int port, final boolean autoClose) throws IOException, UnknownHostException {
        final SSLSocket sslSocket = (SSLSocket)this.socketfactory.createSocket(socket, host, port, autoClose);
        this.prepareSocket(sslSocket);
        sslSocket.startHandshake();
        if (this.hostnameVerifier != null) {
            this.hostnameVerifier.verify(host, sslSocket);
        }
        return sslSocket;
    }
    
    @Deprecated
    public void setHostnameVerifier(final X509HostnameVerifier hostnameVerifier) {
        if (hostnameVerifier == null) {
            throw new IllegalArgumentException("Hostname verifier may not be null");
        }
        this.hostnameVerifier = hostnameVerifier;
    }
    
    public X509HostnameVerifier getHostnameVerifier() {
        return this.hostnameVerifier;
    }
    
    @Deprecated
    public Socket connectSocket(final Socket socket, final String host, final int port, final InetAddress localAddress, int localPort, final HttpParams params) throws IOException, UnknownHostException, ConnectTimeoutException {
        InetSocketAddress local = null;
        if (localAddress != null || localPort > 0) {
            if (localPort < 0) {
                localPort = 0;
            }
            local = new InetSocketAddress(localAddress, localPort);
        }
        InetAddress remoteAddress;
        if (this.nameResolver != null) {
            remoteAddress = this.nameResolver.resolve(host);
        }
        else {
            remoteAddress = InetAddress.getByName(host);
        }
        final InetSocketAddress remote = new HttpInetSocketAddress(new HttpHost(host, port), remoteAddress, port);
        return this.connectSocket(socket, remote, local, params);
    }
    
    @Deprecated
    public Socket createSocket(final Socket socket, final String host, final int port, final boolean autoClose) throws IOException, UnknownHostException {
        return this.createLayeredSocket(socket, host, port, autoClose);
    }
    
    protected void prepareSocket(final SSLSocket socket) throws IOException {
    }
    
    static {
        ALLOW_ALL_HOSTNAME_VERIFIER = new AllowAllHostnameVerifier();
        BROWSER_COMPATIBLE_HOSTNAME_VERIFIER = new BrowserCompatHostnameVerifier();
        STRICT_HOSTNAME_VERIFIER = new StrictHostnameVerifier();
    }
}
