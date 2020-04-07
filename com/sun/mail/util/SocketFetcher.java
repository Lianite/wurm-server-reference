// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.mail.util;

import java.util.Hashtable;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.Iterator;
import java.util.Collection;
import java.util.regex.Pattern;
import java.security.cert.CertificateParsingException;
import java.util.List;
import java.security.cert.Certificate;
import javax.net.ssl.SSLPeerUnverifiedException;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import java.lang.reflect.Method;
import javax.net.ssl.SSLSocketFactory;
import java.security.GeneralSecurityException;
import javax.net.ssl.SSLSocket;
import java.net.SocketAddress;
import java.net.InetSocketAddress;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.SocketTimeoutException;
import javax.net.SocketFactory;
import java.net.InetAddress;
import java.util.logging.Level;
import java.net.Socket;
import java.util.Properties;

public class SocketFetcher
{
    private static MailLogger logger;
    private static final String SOCKS_SUPPORT = "com.sun.mail.util.SocksSupport";
    static /* synthetic */ Class class$com$sun$mail$util$SocketFetcher;
    static /* synthetic */ Class class$java$lang$String;
    static /* synthetic */ Class class$java$security$cert$X509Certificate;
    
    public static Socket getSocket(final String host, final int port, Properties props, String prefix, final boolean useSSL) throws IOException {
        if (SocketFetcher.logger.isLoggable(Level.FINER)) {
            SocketFetcher.logger.finer("getSocket, host " + host + ", port " + port + ", prefix " + prefix + ", useSSL " + useSSL);
        }
        if (prefix == null) {
            prefix = "socket";
        }
        if (props == null) {
            props = new Properties();
        }
        final int cto = PropUtil.getIntProperty(props, prefix + ".connectiontimeout", -1);
        Socket socket = null;
        final String localaddrstr = props.getProperty(prefix + ".localaddress", null);
        InetAddress localaddr = null;
        if (localaddrstr != null) {
            localaddr = InetAddress.getByName(localaddrstr);
        }
        final int localport = PropUtil.getIntProperty(props, prefix + ".localport", 0);
        final boolean fb = PropUtil.getBooleanProperty(props, prefix + ".socketFactory.fallback", true);
        int sfPort = -1;
        String sfErr = "unknown socket factory";
        final int to = PropUtil.getIntProperty(props, prefix + ".timeout", -1);
        try {
            SocketFactory sf = null;
            String sfPortName = null;
            if (useSSL) {
                final Object sfo = ((Hashtable<K, Object>)props).get(prefix + ".ssl.socketFactory");
                if (sfo instanceof SocketFactory) {
                    sf = (SocketFactory)sfo;
                    sfErr = "SSL socket factory instance " + sf;
                }
                if (sf == null) {
                    final String sfClass = props.getProperty(prefix + ".ssl.socketFactory.class");
                    sf = getSocketFactory(sfClass);
                    sfErr = "SSL socket factory class " + sfClass;
                }
                sfPortName = ".ssl.socketFactory.port";
            }
            if (sf == null) {
                final Object sfo = ((Hashtable<K, Object>)props).get(prefix + ".socketFactory");
                if (sfo instanceof SocketFactory) {
                    sf = (SocketFactory)sfo;
                    sfErr = "socket factory instance " + sf;
                }
                if (sf == null) {
                    final String sfClass = props.getProperty(prefix + ".socketFactory.class");
                    sf = getSocketFactory(sfClass);
                    sfErr = "socket factory class " + sfClass;
                }
                sfPortName = ".socketFactory.port";
            }
            if (sf != null) {
                sfPort = PropUtil.getIntProperty(props, prefix + sfPortName, -1);
                if (sfPort == -1) {
                    sfPort = port;
                }
                socket = createSocket(localaddr, localport, host, sfPort, cto, to, props, prefix, sf, useSSL);
            }
        }
        catch (SocketTimeoutException sex) {
            throw sex;
        }
        catch (Exception ex) {
            if (!fb) {
                if (ex instanceof InvocationTargetException) {
                    final Throwable t = ((InvocationTargetException)ex).getTargetException();
                    if (t instanceof Exception) {
                        ex = (Exception)t;
                    }
                }
                if (ex instanceof IOException) {
                    throw (IOException)ex;
                }
                final IOException ioex = new IOException("Couldn't connect using " + sfErr + " to host, port: " + host + ", " + sfPort + "; Exception: " + ex);
                ioex.initCause(ex);
                throw ioex;
            }
        }
        if (socket == null) {
            socket = createSocket(localaddr, localport, host, port, cto, to, props, prefix, null, useSSL);
        }
        else if (to >= 0) {
            socket.setSoTimeout(to);
        }
        return socket;
    }
    
    public static Socket getSocket(final String host, final int port, final Properties props, final String prefix) throws IOException {
        return getSocket(host, port, props, prefix, false);
    }
    
    private static Socket createSocket(final InetAddress localaddr, final int localport, final String host, final int port, final int cto, final int to, final Properties props, final String prefix, SocketFactory sf, final boolean useSSL) throws IOException {
        Socket socket = null;
        String socksHost = props.getProperty(prefix + ".socks.host", null);
        int socksPort = 1080;
        if (socksHost != null) {
            final int i = socksHost.indexOf(58);
            if (i >= 0) {
                socksHost = socksHost.substring(0, i);
                try {
                    socksPort = Integer.parseInt(socksHost.substring(i + 1));
                }
                catch (NumberFormatException ex2) {}
            }
            socksPort = PropUtil.getIntProperty(props, prefix + ".socks.port", socksPort);
            if (SocketFetcher.logger.isLoggable(Level.FINER)) {
                SocketFetcher.logger.finer("socks host " + socksHost + ", port " + socksPort);
            }
        }
        if (sf != null) {
            socket = sf.createSocket();
        }
        if (socket == null) {
            if (socksHost != null) {
                try {
                    final ClassLoader cl = getContextClassLoader();
                    Class proxySupport = null;
                    if (cl != null) {
                        try {
                            proxySupport = Class.forName("com.sun.mail.util.SocksSupport", false, cl);
                        }
                        catch (Exception ex3) {}
                    }
                    if (proxySupport == null) {
                        proxySupport = Class.forName("com.sun.mail.util.SocksSupport");
                    }
                    final Method mthGetSocket = proxySupport.getMethod("getSocket", (SocketFetcher.class$java$lang$String == null) ? (SocketFetcher.class$java$lang$String = class$("java.lang.String")) : SocketFetcher.class$java$lang$String, Integer.TYPE);
                    socket = (Socket)mthGetSocket.invoke(new Object(), socksHost, new Integer(socksPort));
                }
                catch (Exception ex) {
                    SocketFetcher.logger.log(Level.FINER, "failed to load ProxySupport class", ex);
                }
            }
            if (socket == null) {
                socket = new Socket();
            }
        }
        if (to >= 0) {
            socket.setSoTimeout(to);
        }
        if (localaddr != null) {
            socket.bind(new InetSocketAddress(localaddr, localport));
        }
        if (cto >= 0) {
            socket.connect(new InetSocketAddress(host, port), cto);
        }
        else {
            socket.connect(new InetSocketAddress(host, port));
        }
        if (useSSL && !(socket instanceof SSLSocket)) {
            SSLSocketFactory ssf = null;
            Label_0527: {
                final String trusted;
                if ((trusted = props.getProperty(prefix + ".ssl.trust")) != null) {
                    try {
                        final MailSSLSocketFactory msf = new MailSSLSocketFactory();
                        if (trusted.equals("*")) {
                            msf.setTrustAllHosts(true);
                        }
                        else {
                            msf.setTrustedHosts(trusted.split("\\s+"));
                        }
                        ssf = msf;
                        break Label_0527;
                    }
                    catch (GeneralSecurityException gex) {
                        final IOException ioex = new IOException("Can't create MailSSLSocketFactory");
                        ioex.initCause(gex);
                        throw ioex;
                    }
                }
                ssf = (SSLSocketFactory)SSLSocketFactory.getDefault();
            }
            socket = ssf.createSocket(socket, host, port, true);
            sf = ssf;
        }
        configureSSLSocket(socket, host, props, prefix, sf);
        return socket;
    }
    
    private static SocketFactory getSocketFactory(final String sfClass) throws ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        if (sfClass == null || sfClass.length() == 0) {
            return null;
        }
        final ClassLoader cl = getContextClassLoader();
        Class clsSockFact = null;
        if (cl != null) {
            try {
                clsSockFact = Class.forName(sfClass, false, cl);
            }
            catch (ClassNotFoundException ex) {}
        }
        if (clsSockFact == null) {
            clsSockFact = Class.forName(sfClass);
        }
        final Method mthGetDefault = clsSockFact.getMethod("getDefault", (Class[])new Class[0]);
        final SocketFactory sf = (SocketFactory)mthGetDefault.invoke(new Object(), new Object[0]);
        return sf;
    }
    
    public static Socket startTLS(final Socket socket) throws IOException {
        return startTLS(socket, new Properties(), "socket");
    }
    
    public static Socket startTLS(final Socket socket, final Properties props, final String prefix) throws IOException {
        final InetAddress a = socket.getInetAddress();
        final String host = a.getHostName();
        return startTLS(socket, host, props, prefix);
    }
    
    public static Socket startTLS(Socket socket, final String host, final Properties props, final String prefix) throws IOException {
        final int port = socket.getPort();
        if (SocketFetcher.logger.isLoggable(Level.FINER)) {
            SocketFetcher.logger.finer("startTLS host " + host + ", port " + port);
        }
        String sfErr = "unknown socket factory";
        try {
            SSLSocketFactory ssf = null;
            SocketFactory sf = null;
            Object sfo = ((Hashtable<K, Object>)props).get(prefix + ".ssl.socketFactory");
            if (sfo instanceof SocketFactory) {
                sf = (SocketFactory)sfo;
                sfErr = "SSL socket factory instance " + sf;
            }
            if (sf == null) {
                final String sfClass = props.getProperty(prefix + ".ssl.socketFactory.class");
                sf = getSocketFactory(sfClass);
                sfErr = "SSL socket factory class " + sfClass;
            }
            if (sf != null && sf instanceof SSLSocketFactory) {
                ssf = (SSLSocketFactory)sf;
            }
            if (ssf == null) {
                sfo = ((Hashtable<K, Object>)props).get(prefix + ".socketFactory");
                if (sfo instanceof SocketFactory) {
                    sf = (SocketFactory)sfo;
                    sfErr = "socket factory instance " + sf;
                }
                if (sf == null) {
                    final String sfClass = props.getProperty(prefix + ".socketFactory.class");
                    sf = getSocketFactory(sfClass);
                    sfErr = "socket factory class " + sfClass;
                }
                if (sf != null && sf instanceof SSLSocketFactory) {
                    ssf = (SSLSocketFactory)sf;
                }
            }
            Label_0471: {
                if (ssf == null) {
                    final String trusted;
                    if ((trusted = props.getProperty(prefix + ".ssl.trust")) != null) {
                        try {
                            final MailSSLSocketFactory msf = new MailSSLSocketFactory();
                            if (trusted.equals("*")) {
                                msf.setTrustAllHosts(true);
                            }
                            else {
                                msf.setTrustedHosts(trusted.split("\\s+"));
                            }
                            ssf = msf;
                            sfErr = "mail SSL socket factory";
                            break Label_0471;
                        }
                        catch (GeneralSecurityException gex) {
                            final IOException ioex = new IOException("Can't create MailSSLSocketFactory");
                            ioex.initCause(gex);
                            throw ioex;
                        }
                    }
                    ssf = (SSLSocketFactory)SSLSocketFactory.getDefault();
                    sfErr = "default SSL socket factory";
                }
            }
            socket = ssf.createSocket(socket, host, port, true);
            configureSSLSocket(socket, host, props, prefix, ssf);
        }
        catch (Exception ex) {
            if (ex instanceof InvocationTargetException) {
                final Throwable t = ((InvocationTargetException)ex).getTargetException();
                if (t instanceof Exception) {
                    ex = (Exception)t;
                }
            }
            if (ex instanceof IOException) {
                throw (IOException)ex;
            }
            final IOException ioex2 = new IOException("Exception in startTLS using " + sfErr + ": host, port: " + host + ", " + port + "; Exception: " + ex);
            ioex2.initCause(ex);
            throw ioex2;
        }
        return socket;
    }
    
    private static void configureSSLSocket(final Socket socket, final String host, final Properties props, final String prefix, final SocketFactory sf) throws IOException {
        if (!(socket instanceof SSLSocket)) {
            return;
        }
        final SSLSocket sslsocket = (SSLSocket)socket;
        final String protocols = props.getProperty(prefix + ".ssl.protocols", null);
        if (protocols != null) {
            sslsocket.setEnabledProtocols(stringArray(protocols));
        }
        else {
            sslsocket.setEnabledProtocols(new String[] { "TLSv1" });
        }
        final String ciphers = props.getProperty(prefix + ".ssl.ciphersuites", null);
        if (ciphers != null) {
            sslsocket.setEnabledCipherSuites(stringArray(ciphers));
        }
        if (SocketFetcher.logger.isLoggable(Level.FINER)) {
            SocketFetcher.logger.finer("SSL protocols after " + Arrays.asList(sslsocket.getEnabledProtocols()));
            SocketFetcher.logger.finer("SSL ciphers after " + Arrays.asList(sslsocket.getEnabledCipherSuites()));
        }
        sslsocket.startHandshake();
        final boolean idCheck = PropUtil.getBooleanProperty(props, prefix + ".ssl.checkserveridentity", false);
        if (idCheck) {
            checkServerIdentity(host, sslsocket);
        }
        if (sf instanceof MailSSLSocketFactory) {
            final MailSSLSocketFactory msf = (MailSSLSocketFactory)sf;
            if (!msf.isServerTrusted(host, sslsocket)) {
                try {
                    sslsocket.close();
                }
                finally {
                    throw new IOException("Server is not trusted: " + host);
                }
            }
        }
    }
    
    private static void checkServerIdentity(final String server, final SSLSocket sslSocket) throws IOException {
        try {
            final Certificate[] certChain = sslSocket.getSession().getPeerCertificates();
            if (certChain != null && certChain.length > 0 && certChain[0] instanceof X509Certificate && matchCert(server, (X509Certificate)certChain[0])) {
                return;
            }
        }
        catch (SSLPeerUnverifiedException e) {
            sslSocket.close();
            final IOException ioex = new IOException("Can't verify identity of server: " + server);
            ioex.initCause(e);
            throw ioex;
        }
        sslSocket.close();
        throw new IOException("Can't verify identity of server: " + server);
    }
    
    private static boolean matchCert(final String server, final X509Certificate cert) {
        if (SocketFetcher.logger.isLoggable(Level.FINER)) {
            SocketFetcher.logger.finer("matchCert server " + server + ", cert " + cert);
        }
        try {
            final Class hnc = Class.forName("sun.security.util.HostnameChecker");
            final Method getInstance = hnc.getMethod("getInstance", Byte.TYPE);
            final Object hostnameChecker = getInstance.invoke(new Object(), new Byte((byte)2));
            if (SocketFetcher.logger.isLoggable(Level.FINER)) {
                SocketFetcher.logger.finer("using sun.security.util.HostnameChecker");
            }
            final Method match = hnc.getMethod("match", (SocketFetcher.class$java$lang$String == null) ? (SocketFetcher.class$java$lang$String = class$("java.lang.String")) : SocketFetcher.class$java$lang$String, (SocketFetcher.class$java$security$cert$X509Certificate == null) ? (SocketFetcher.class$java$security$cert$X509Certificate = class$("java.security.cert.X509Certificate")) : SocketFetcher.class$java$security$cert$X509Certificate);
            try {
                match.invoke(hostnameChecker, server, cert);
                return true;
            }
            catch (InvocationTargetException cex) {
                SocketFetcher.logger.log(Level.FINER, "FAIL", cex);
                return false;
            }
        }
        catch (Exception ex) {
            SocketFetcher.logger.log(Level.FINER, "NO sun.security.util.HostnameChecker", ex);
            try {
                final Collection names = cert.getSubjectAlternativeNames();
                if (names != null) {
                    boolean foundName = false;
                    for (final List nameEnt : names) {
                        final Integer type = nameEnt.get(0);
                        if (type == 2) {
                            foundName = true;
                            final String name = nameEnt.get(1);
                            if (SocketFetcher.logger.isLoggable(Level.FINER)) {
                                SocketFetcher.logger.finer("found name: " + name);
                            }
                            if (matchServer(server, name)) {
                                return true;
                            }
                            continue;
                        }
                    }
                    if (foundName) {
                        return false;
                    }
                }
            }
            catch (CertificateParsingException ex2) {}
            final Pattern p = Pattern.compile("CN=([^,]*)");
            final Matcher m = p.matcher(cert.getSubjectX500Principal().getName());
            return m.find() && matchServer(server, m.group(1).trim());
        }
    }
    
    private static boolean matchServer(final String server, final String name) {
        if (SocketFetcher.logger.isLoggable(Level.FINER)) {
            SocketFetcher.logger.finer("match server " + server + " with " + name);
        }
        if (!name.startsWith("*.")) {
            return server.equalsIgnoreCase(name);
        }
        final String tail = name.substring(2);
        if (tail.length() == 0) {
            return false;
        }
        final int off = server.length() - tail.length();
        return off >= 1 && server.charAt(off - 1) == '.' && server.regionMatches(true, off, tail, 0, tail.length());
    }
    
    private static String[] stringArray(final String s) {
        final StringTokenizer st = new StringTokenizer(s);
        final List tokens = new ArrayList();
        while (st.hasMoreTokens()) {
            tokens.add(st.nextToken());
        }
        return tokens.toArray(new String[tokens.size()]);
    }
    
    private static ClassLoader getContextClassLoader() {
        return AccessController.doPrivileged((PrivilegedAction<ClassLoader>)new PrivilegedAction() {
            public Object run() {
                ClassLoader cl = null;
                try {
                    cl = Thread.currentThread().getContextClassLoader();
                }
                catch (SecurityException ex) {}
                return cl;
            }
        });
    }
    
    static /* synthetic */ Class class$(final String x0) {
        try {
            return Class.forName(x0);
        }
        catch (ClassNotFoundException x) {
            throw new NoClassDefFoundError().initCause(x);
        }
    }
    
    static {
        SocketFetcher.logger = new MailLogger((SocketFetcher.class$com$sun$mail$util$SocketFetcher == null) ? (SocketFetcher.class$com$sun$mail$util$SocketFetcher = class$("com.sun.mail.util.SocketFetcher")) : SocketFetcher.class$com$sun$mail$util$SocketFetcher, "socket", "DEBUG SocketFetcher", PropUtil.getBooleanSystemProperty("mail.socket.debug", false), System.out);
    }
}
