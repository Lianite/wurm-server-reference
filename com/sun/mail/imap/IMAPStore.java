// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.mail.imap;

import javax.mail.Quota;
import javax.mail.StoreClosedException;
import com.sun.mail.iap.BadCommandException;
import com.sun.mail.imap.protocol.ListInfo;
import javax.mail.Folder;
import javax.mail.PasswordAuthentication;
import java.net.UnknownHostException;
import java.net.InetAddress;
import com.sun.mail.iap.ConnectionException;
import com.sun.mail.imap.protocol.IMAPProtocol;
import java.io.IOException;
import com.sun.mail.iap.ProtocolException;
import javax.mail.MessagingException;
import com.sun.mail.iap.CommandFailedException;
import javax.mail.AuthenticationFailedException;
import java.util.StringTokenizer;
import java.util.Vector;
import java.util.logging.Level;
import com.sun.mail.util.PropUtil;
import com.sun.mail.iap.Response;
import javax.mail.URLName;
import javax.mail.Session;
import java.lang.reflect.Constructor;
import com.sun.mail.util.MailLogger;
import com.sun.mail.imap.protocol.Namespaces;
import com.sun.mail.iap.ResponseHandler;
import javax.mail.QuotaAwareStore;
import javax.mail.Store;

public class IMAPStore extends Store implements QuotaAwareStore, ResponseHandler
{
    public static final int RESPONSE = 1000;
    protected final String name;
    protected final int defaultPort;
    protected final boolean isSSL;
    private final int blksize;
    private boolean ignoreSize;
    private final int statusCacheTimeout;
    private final int appendBufferSize;
    private final int minIdleTime;
    private volatile int port;
    protected String host;
    protected String user;
    protected String password;
    protected String proxyAuthUser;
    protected String authorizationID;
    protected String saslRealm;
    private Namespaces namespaces;
    private boolean disableAuthLogin;
    private boolean disableAuthPlain;
    private boolean disableAuthNtlm;
    private boolean enableStartTLS;
    private boolean requireStartTLS;
    private boolean usingSSL;
    private boolean enableSASL;
    private String[] saslMechanisms;
    private boolean forcePasswordRefresh;
    private boolean enableImapEvents;
    private String guid;
    private volatile boolean connectionFailed;
    private volatile boolean forceClose;
    private final Object connectionFailedLock;
    private boolean debugusername;
    private boolean debugpassword;
    protected MailLogger logger;
    private boolean messageCacheDebug;
    private volatile Constructor folderConstructor;
    private volatile Constructor folderConstructorLI;
    private final ConnectionPool pool;
    private ResponseHandler nonStoreResponseHandler;
    static /* synthetic */ Class class$java$lang$String;
    static /* synthetic */ Class class$com$sun$mail$imap$IMAPStore;
    static /* synthetic */ Class class$java$lang$Boolean;
    static /* synthetic */ Class class$com$sun$mail$imap$protocol$ListInfo;
    
    public IMAPStore(final Session session, final URLName url) {
        this(session, url, "imap", false);
    }
    
    protected IMAPStore(final Session session, final URLName url, String name, boolean isSSL) {
        super(session, url);
        this.port = -1;
        this.disableAuthLogin = false;
        this.disableAuthPlain = false;
        this.disableAuthNtlm = false;
        this.enableStartTLS = false;
        this.requireStartTLS = false;
        this.usingSSL = false;
        this.enableSASL = false;
        this.forcePasswordRefresh = false;
        this.enableImapEvents = false;
        this.connectionFailed = false;
        this.forceClose = false;
        this.connectionFailedLock = new Object();
        this.folderConstructor = null;
        this.folderConstructorLI = null;
        this.nonStoreResponseHandler = new ResponseHandler() {
            public void handleResponse(final Response r) {
                if (r.isOK() || r.isNO() || r.isBAD() || r.isBYE()) {
                    IMAPStore.this.handleResponseCode(r);
                }
                if (r.isBYE()) {
                    IMAPStore.this.logger.fine("IMAPStore non-store connection dead");
                }
            }
        };
        if (url != null) {
            name = url.getProtocol();
        }
        this.name = name;
        if (!isSSL) {
            isSSL = PropUtil.getBooleanSessionProperty(session, "mail." + name + ".ssl.enable", false);
        }
        if (isSSL) {
            this.defaultPort = 993;
        }
        else {
            this.defaultPort = 143;
        }
        this.isSSL = isSSL;
        this.debug = session.getDebug();
        this.debugusername = PropUtil.getBooleanSessionProperty(session, "mail.debug.auth.username", true);
        this.debugpassword = PropUtil.getBooleanSessionProperty(session, "mail.debug.auth.password", false);
        this.logger = new MailLogger(this.getClass(), "DEBUG " + name.toUpperCase(), session);
        final boolean partialFetch = PropUtil.getBooleanSessionProperty(session, "mail." + name + ".partialfetch", true);
        if (!partialFetch) {
            this.blksize = -1;
            this.logger.config("mail.imap.partialfetch: false");
        }
        else {
            this.blksize = PropUtil.getIntSessionProperty(session, "mail." + name + ".fetchsize", 16384);
            if (this.logger.isLoggable(Level.CONFIG)) {
                this.logger.config("mail.imap.fetchsize: " + this.blksize);
            }
        }
        this.ignoreSize = PropUtil.getBooleanSessionProperty(session, "mail." + name + ".ignorebodystructuresize", false);
        if (this.logger.isLoggable(Level.CONFIG)) {
            this.logger.config("mail.imap.ignorebodystructuresize: " + this.ignoreSize);
        }
        this.statusCacheTimeout = PropUtil.getIntSessionProperty(session, "mail." + name + ".statuscachetimeout", 1000);
        if (this.logger.isLoggable(Level.CONFIG)) {
            this.logger.config("mail.imap.statuscachetimeout: " + this.statusCacheTimeout);
        }
        this.appendBufferSize = PropUtil.getIntSessionProperty(session, "mail." + name + ".appendbuffersize", -1);
        if (this.logger.isLoggable(Level.CONFIG)) {
            this.logger.config("mail.imap.appendbuffersize: " + this.appendBufferSize);
        }
        this.minIdleTime = PropUtil.getIntSessionProperty(session, "mail." + name + ".minidletime", 10);
        if (this.logger.isLoggable(Level.CONFIG)) {
            this.logger.config("mail.imap.minidletime: " + this.minIdleTime);
        }
        String s = session.getProperty("mail." + name + ".proxyauth.user");
        if (s != null) {
            this.proxyAuthUser = s;
            if (this.logger.isLoggable(Level.CONFIG)) {
                this.logger.config("mail.imap.proxyauth.user: " + this.proxyAuthUser);
            }
        }
        this.disableAuthLogin = PropUtil.getBooleanSessionProperty(session, "mail." + name + ".auth.login.disable", false);
        if (this.disableAuthLogin) {
            this.logger.config("disable AUTH=LOGIN");
        }
        this.disableAuthPlain = PropUtil.getBooleanSessionProperty(session, "mail." + name + ".auth.plain.disable", false);
        if (this.disableAuthPlain) {
            this.logger.config("disable AUTH=PLAIN");
        }
        this.disableAuthNtlm = PropUtil.getBooleanSessionProperty(session, "mail." + name + ".auth.ntlm.disable", false);
        if (this.disableAuthNtlm) {
            this.logger.config("disable AUTH=NTLM");
        }
        this.enableStartTLS = PropUtil.getBooleanSessionProperty(session, "mail." + name + ".starttls.enable", false);
        if (this.enableStartTLS) {
            this.logger.config("enable STARTTLS");
        }
        this.requireStartTLS = PropUtil.getBooleanSessionProperty(session, "mail." + name + ".starttls.required", false);
        if (this.requireStartTLS) {
            this.logger.config("require STARTTLS");
        }
        this.enableSASL = PropUtil.getBooleanSessionProperty(session, "mail." + name + ".sasl.enable", false);
        if (this.enableSASL) {
            this.logger.config("enable SASL");
        }
        if (this.enableSASL) {
            s = session.getProperty("mail." + name + ".sasl.mechanisms");
            if (s != null && s.length() > 0) {
                if (this.logger.isLoggable(Level.CONFIG)) {
                    this.logger.config("SASL mechanisms allowed: " + s);
                }
                final Vector v = new Vector(5);
                final StringTokenizer st = new StringTokenizer(s, " ,");
                while (st.hasMoreTokens()) {
                    final String m = st.nextToken();
                    if (m.length() > 0) {
                        v.addElement(m);
                    }
                }
                v.copyInto(this.saslMechanisms = new String[v.size()]);
            }
        }
        s = session.getProperty("mail." + name + ".sasl.authorizationid");
        if (s != null) {
            this.authorizationID = s;
            this.logger.log(Level.CONFIG, "mail.imap.sasl.authorizationid: {0}", this.authorizationID);
        }
        s = session.getProperty("mail." + name + ".sasl.realm");
        if (s != null) {
            this.saslRealm = s;
            this.logger.log(Level.CONFIG, "mail.imap.sasl.realm: {0}", this.saslRealm);
        }
        this.forcePasswordRefresh = PropUtil.getBooleanSessionProperty(session, "mail." + name + ".forcepasswordrefresh", false);
        if (this.forcePasswordRefresh) {
            this.logger.config("enable forcePasswordRefresh");
        }
        this.enableImapEvents = PropUtil.getBooleanSessionProperty(session, "mail." + name + ".enableimapevents", false);
        if (this.enableImapEvents) {
            this.logger.config("enable IMAP events");
        }
        this.messageCacheDebug = PropUtil.getBooleanSessionProperty(session, "mail." + name + ".messagecache.debug", false);
        this.guid = session.getProperty("mail." + name + ".yahoo.guid");
        if (this.guid != null) {
            this.logger.log(Level.CONFIG, "mail.imap.yahoo.guid: {0}", this.guid);
        }
        s = session.getProperty("mail." + name + ".folder.class");
        if (s != null) {
            this.logger.log(Level.CONFIG, "IMAP: folder class: {0}", s);
            try {
                final ClassLoader cl = this.getClass().getClassLoader();
                Class folderClass = null;
                try {
                    folderClass = Class.forName(s, false, cl);
                }
                catch (ClassNotFoundException ex2) {
                    folderClass = Class.forName(s);
                }
                final Class[] c = { (IMAPStore.class$java$lang$String == null) ? (IMAPStore.class$java$lang$String = class$("java.lang.String")) : IMAPStore.class$java$lang$String, Character.TYPE, (IMAPStore.class$com$sun$mail$imap$IMAPStore == null) ? (IMAPStore.class$com$sun$mail$imap$IMAPStore = class$("com.sun.mail.imap.IMAPStore")) : IMAPStore.class$com$sun$mail$imap$IMAPStore, (IMAPStore.class$java$lang$Boolean == null) ? (IMAPStore.class$java$lang$Boolean = class$("java.lang.Boolean")) : IMAPStore.class$java$lang$Boolean };
                this.folderConstructor = folderClass.getConstructor((Class[])c);
                final Class[] c2 = { (IMAPStore.class$com$sun$mail$imap$protocol$ListInfo == null) ? (IMAPStore.class$com$sun$mail$imap$protocol$ListInfo = class$("com.sun.mail.imap.protocol.ListInfo")) : IMAPStore.class$com$sun$mail$imap$protocol$ListInfo, (IMAPStore.class$com$sun$mail$imap$IMAPStore == null) ? (IMAPStore.class$com$sun$mail$imap$IMAPStore = class$("com.sun.mail.imap.IMAPStore")) : IMAPStore.class$com$sun$mail$imap$IMAPStore };
                this.folderConstructorLI = folderClass.getConstructor((Class[])c2);
            }
            catch (Exception ex) {
                this.logger.log(Level.CONFIG, "IMAP: failed to load folder class", ex);
            }
        }
        this.pool = new ConnectionPool(name, this.logger, session);
    }
    
    protected synchronized boolean protocolConnect(final String host, final int pport, final String user, final String password) throws MessagingException {
        IMAPProtocol protocol = null;
        if (host == null || password == null || user == null) {
            if (this.logger.isLoggable(Level.FINE)) {
                this.logger.fine("protocolConnect returning false, host=" + host + ", user=" + this.traceUser(user) + ", password=" + this.tracePassword(password));
            }
            return false;
        }
        if (pport != -1) {
            this.port = pport;
        }
        else {
            this.port = PropUtil.getIntSessionProperty(this.session, "mail." + this.name + ".port", this.port);
        }
        if (this.port == -1) {
            this.port = this.defaultPort;
        }
        try {
            final boolean poolEmpty;
            synchronized (this.pool) {
                poolEmpty = this.pool.authenticatedConnections.isEmpty();
            }
            if (poolEmpty) {
                if (this.logger.isLoggable(Level.FINE)) {
                    this.logger.fine("trying to connect to host \"" + host + "\", port " + this.port + ", isSSL " + this.isSSL);
                }
                protocol = this.newIMAPProtocol(host, this.port);
                if (this.logger.isLoggable(Level.FINE)) {
                    this.logger.fine("protocolConnect login, host=" + host + ", user=" + this.traceUser(user) + ", password=" + this.tracePassword(password));
                }
                this.login(protocol, user, password);
                protocol.addResponseHandler(this);
                this.usingSSL = protocol.isSSL();
                this.host = host;
                this.user = user;
                this.password = password;
                synchronized (this.pool) {
                    this.pool.authenticatedConnections.addElement(protocol);
                }
            }
        }
        catch (CommandFailedException cex) {
            if (protocol != null) {
                protocol.disconnect();
            }
            protocol = null;
            throw new AuthenticationFailedException(cex.getResponse().getRest());
        }
        catch (ProtocolException pex) {
            if (protocol != null) {
                protocol.disconnect();
            }
            protocol = null;
            throw new MessagingException(pex.getMessage(), pex);
        }
        catch (IOException ioex) {
            throw new MessagingException(ioex.getMessage(), ioex);
        }
        return true;
    }
    
    protected IMAPProtocol newIMAPProtocol(final String host, final int port) throws IOException, ProtocolException {
        return new IMAPProtocol(this.name, host, port, this.session.getProperties(), this.isSSL, this.logger);
    }
    
    private void login(final IMAPProtocol p, final String u, final String pw) throws ProtocolException {
        if (this.enableStartTLS || this.requireStartTLS) {
            if (p.hasCapability("STARTTLS")) {
                p.startTLS();
                p.capability();
            }
            else if (this.requireStartTLS) {
                this.logger.fine("STARTTLS required but not supported by server");
                throw new ProtocolException("STARTTLS required but not supported by server");
            }
        }
        if (p.isAuthenticated()) {
            return;
        }
        this.preLogin(p);
        if (this.guid != null) {
            p.id(this.guid);
        }
        p.getCapabilities().put("__PRELOGIN__", "");
        String authzid;
        if (this.authorizationID != null) {
            authzid = this.authorizationID;
        }
        else if (this.proxyAuthUser != null) {
            authzid = this.proxyAuthUser;
        }
        else {
            authzid = null;
        }
        if (this.enableSASL) {
            p.sasllogin(this.saslMechanisms, this.saslRealm, authzid, u, pw);
        }
        if (!p.isAuthenticated()) {
            if (p.hasCapability("AUTH=PLAIN") && !this.disableAuthPlain) {
                p.authplain(authzid, u, pw);
            }
            else if ((p.hasCapability("AUTH-LOGIN") || p.hasCapability("AUTH=LOGIN")) && !this.disableAuthLogin) {
                p.authlogin(u, pw);
            }
            else if (p.hasCapability("AUTH=NTLM") && !this.disableAuthNtlm) {
                p.authntlm(authzid, u, pw);
            }
            else {
                if (p.hasCapability("LOGINDISABLED")) {
                    throw new ProtocolException("No login methods supported!");
                }
                p.login(u, pw);
            }
        }
        if (this.proxyAuthUser != null) {
            p.proxyauth(this.proxyAuthUser);
        }
        if (p.hasCapability("__PRELOGIN__")) {
            try {
                p.capability();
            }
            catch (ConnectionException cex) {
                throw cex;
            }
            catch (ProtocolException ex) {}
        }
    }
    
    protected void preLogin(final IMAPProtocol p) throws ProtocolException {
    }
    
    public boolean isSSL() {
        return this.usingSSL;
    }
    
    public synchronized void setUsername(final String user) {
        this.user = user;
    }
    
    public synchronized void setPassword(final String password) {
        this.password = password;
    }
    
    IMAPProtocol getProtocol(final IMAPFolder folder) throws MessagingException {
        IMAPProtocol p = null;
        while (p == null) {
            synchronized (this.pool) {
                if (this.pool.authenticatedConnections.isEmpty() || (this.pool.authenticatedConnections.size() == 1 && (this.pool.separateStoreConnection || this.pool.storeConnectionInUse))) {
                    this.logger.fine("no connections in the pool, creating a new one");
                    try {
                        if (this.forcePasswordRefresh) {
                            this.refreshPassword();
                        }
                        p = this.newIMAPProtocol(this.host, this.port);
                        this.login(p, this.user, this.password);
                    }
                    catch (Exception ex1) {
                        if (p != null) {
                            try {
                                p.disconnect();
                            }
                            catch (Exception ex2) {}
                        }
                        p = null;
                    }
                    if (p == null) {
                        throw new MessagingException("connection failure");
                    }
                }
                else {
                    if (this.logger.isLoggable(Level.FINE)) {
                        this.logger.fine("connection available -- size: " + this.pool.authenticatedConnections.size());
                    }
                    p = this.pool.authenticatedConnections.lastElement();
                    this.pool.authenticatedConnections.removeElement(p);
                    final long lastUsed = System.currentTimeMillis() - p.getTimestamp();
                    if (lastUsed > this.pool.serverTimeoutInterval) {
                        try {
                            p.removeResponseHandler(this);
                            p.addResponseHandler(this.nonStoreResponseHandler);
                            p.noop();
                            p.removeResponseHandler(this.nonStoreResponseHandler);
                            p.addResponseHandler(this);
                        }
                        catch (ProtocolException pex) {
                            try {
                                p.removeResponseHandler(this.nonStoreResponseHandler);
                                p.disconnect();
                            }
                            finally {
                                p = null;
                            }
                        }
                    }
                    p.removeResponseHandler(this);
                }
                this.timeoutConnections();
                if (folder == null) {
                    continue;
                }
                if (this.pool.folders == null) {
                    this.pool.folders = new Vector();
                }
                this.pool.folders.addElement(folder);
            }
        }
        return p;
    }
    
    private IMAPProtocol getStoreProtocol() throws ProtocolException {
        IMAPProtocol p = null;
        while (p == null) {
            synchronized (this.pool) {
                this.waitIfIdle();
                if (this.pool.authenticatedConnections.isEmpty()) {
                    this.pool.logger.fine("getStoreProtocol() - no connections in the pool, creating a new one");
                    try {
                        if (this.forcePasswordRefresh) {
                            this.refreshPassword();
                        }
                        p = this.newIMAPProtocol(this.host, this.port);
                        this.login(p, this.user, this.password);
                    }
                    catch (Exception ex1) {
                        if (p != null) {
                            try {
                                p.logout();
                            }
                            catch (Exception ex3) {}
                        }
                        p = null;
                    }
                    if (p == null) {
                        throw new ConnectionException("failed to create new store connection");
                    }
                    p.addResponseHandler(this);
                    this.pool.authenticatedConnections.addElement(p);
                }
                else {
                    if (this.pool.logger.isLoggable(Level.FINE)) {
                        this.pool.logger.fine("getStoreProtocol() - connection available -- size: " + this.pool.authenticatedConnections.size());
                    }
                    p = this.pool.authenticatedConnections.firstElement();
                }
                if (this.pool.storeConnectionInUse) {
                    try {
                        p = null;
                        this.pool.wait();
                    }
                    catch (InterruptedException ex2) {}
                }
                else {
                    this.pool.storeConnectionInUse = true;
                    this.pool.logger.fine("getStoreProtocol() -- storeConnectionInUse");
                }
                this.timeoutConnections();
            }
        }
        return p;
    }
    
    IMAPProtocol getFolderStoreProtocol() throws ProtocolException {
        final IMAPProtocol p = this.getStoreProtocol();
        p.removeResponseHandler(this);
        p.addResponseHandler(this.nonStoreResponseHandler);
        return p;
    }
    
    private void refreshPassword() {
        if (this.logger.isLoggable(Level.FINE)) {
            this.logger.fine("refresh password, user: " + this.traceUser(this.user));
        }
        InetAddress addr;
        try {
            addr = InetAddress.getByName(this.host);
        }
        catch (UnknownHostException e) {
            addr = null;
        }
        final PasswordAuthentication pa = this.session.requestPasswordAuthentication(addr, this.port, this.name, null, this.user);
        if (pa != null) {
            this.user = pa.getUserName();
            this.password = pa.getPassword();
        }
    }
    
    boolean allowReadOnlySelect() {
        return PropUtil.getBooleanSessionProperty(this.session, "mail." + this.name + ".allowreadonlyselect", false);
    }
    
    boolean hasSeparateStoreConnection() {
        return this.pool.separateStoreConnection;
    }
    
    MailLogger getConnectionPoolLogger() {
        return this.pool.logger;
    }
    
    boolean getMessageCacheDebug() {
        return this.messageCacheDebug;
    }
    
    boolean isConnectionPoolFull() {
        synchronized (this.pool) {
            if (this.pool.logger.isLoggable(Level.FINE)) {
                this.pool.logger.fine("connection pool current size: " + this.pool.authenticatedConnections.size() + "   pool size: " + this.pool.poolSize);
            }
            return this.pool.authenticatedConnections.size() >= this.pool.poolSize;
        }
    }
    
    void releaseProtocol(final IMAPFolder folder, final IMAPProtocol protocol) {
        synchronized (this.pool) {
            if (protocol != null) {
                if (!this.isConnectionPoolFull()) {
                    protocol.addResponseHandler(this);
                    this.pool.authenticatedConnections.addElement(protocol);
                    if (this.logger.isLoggable(Level.FINE)) {
                        this.logger.fine("added an Authenticated connection -- size: " + this.pool.authenticatedConnections.size());
                    }
                }
                else {
                    this.logger.fine("pool is full, not adding an Authenticated connection");
                    try {
                        protocol.logout();
                    }
                    catch (ProtocolException ex) {}
                }
            }
            if (this.pool.folders != null) {
                this.pool.folders.removeElement(folder);
            }
            this.timeoutConnections();
        }
    }
    
    private void releaseStoreProtocol(final IMAPProtocol protocol) {
        if (protocol == null) {
            this.cleanup();
            return;
        }
        final boolean failed;
        synchronized (this.connectionFailedLock) {
            failed = this.connectionFailed;
            this.connectionFailed = false;
        }
        synchronized (this.pool) {
            this.pool.storeConnectionInUse = false;
            this.pool.notifyAll();
            this.pool.logger.fine("releaseStoreProtocol()");
            this.timeoutConnections();
        }
        assert !Thread.holdsLock(this.pool);
        if (failed) {
            this.cleanup();
        }
    }
    
    void releaseFolderStoreProtocol(final IMAPProtocol protocol) {
        if (protocol == null) {
            return;
        }
        protocol.removeResponseHandler(this.nonStoreResponseHandler);
        protocol.addResponseHandler(this);
        synchronized (this.pool) {
            this.pool.storeConnectionInUse = false;
            this.pool.notifyAll();
            this.pool.logger.fine("releaseFolderStoreProtocol()");
            this.timeoutConnections();
        }
    }
    
    private void emptyConnectionPool(final boolean force) {
        synchronized (this.pool) {
            for (int index = this.pool.authenticatedConnections.size() - 1; index >= 0; --index) {
                try {
                    final IMAPProtocol p = this.pool.authenticatedConnections.elementAt(index);
                    p.removeResponseHandler(this);
                    if (force) {
                        p.disconnect();
                    }
                    else {
                        p.logout();
                    }
                }
                catch (ProtocolException ex) {}
            }
            this.pool.authenticatedConnections.removeAllElements();
        }
        this.pool.logger.fine("removed all authenticated connections from pool");
    }
    
    private void timeoutConnections() {
        synchronized (this.pool) {
            if (System.currentTimeMillis() - this.pool.lastTimePruned > this.pool.pruningInterval && this.pool.authenticatedConnections.size() > 1) {
                if (this.pool.logger.isLoggable(Level.FINE)) {
                    this.pool.logger.fine("checking for connections to prune: " + (System.currentTimeMillis() - this.pool.lastTimePruned));
                    this.pool.logger.fine("clientTimeoutInterval: " + this.pool.clientTimeoutInterval);
                }
                for (int index = this.pool.authenticatedConnections.size() - 1; index > 0; --index) {
                    final IMAPProtocol p = this.pool.authenticatedConnections.elementAt(index);
                    if (this.pool.logger.isLoggable(Level.FINE)) {
                        this.pool.logger.fine("protocol last used: " + (System.currentTimeMillis() - p.getTimestamp()));
                    }
                    if (System.currentTimeMillis() - p.getTimestamp() > this.pool.clientTimeoutInterval) {
                        this.pool.logger.fine("authenticated connection timed out, logging out the connection");
                        p.removeResponseHandler(this);
                        this.pool.authenticatedConnections.removeElementAt(index);
                        try {
                            p.logout();
                        }
                        catch (ProtocolException ex) {}
                    }
                }
                this.pool.lastTimePruned = System.currentTimeMillis();
            }
        }
    }
    
    int getFetchBlockSize() {
        return this.blksize;
    }
    
    boolean ignoreBodyStructureSize() {
        return this.ignoreSize;
    }
    
    Session getSession() {
        return this.session;
    }
    
    int getStatusCacheTimeout() {
        return this.statusCacheTimeout;
    }
    
    int getAppendBufferSize() {
        return this.appendBufferSize;
    }
    
    int getMinIdleTime() {
        return this.minIdleTime;
    }
    
    public synchronized boolean hasCapability(final String capability) throws MessagingException {
        IMAPProtocol p = null;
        try {
            p = this.getStoreProtocol();
            return p.hasCapability(capability);
        }
        catch (ProtocolException pex) {
            throw new MessagingException(pex.getMessage(), pex);
        }
        finally {
            this.releaseStoreProtocol(p);
        }
    }
    
    public synchronized boolean isConnected() {
        if (!super.isConnected()) {
            return false;
        }
        IMAPProtocol p = null;
        try {
            p = this.getStoreProtocol();
            p.noop();
        }
        catch (ProtocolException pex) {}
        finally {
            this.releaseStoreProtocol(p);
        }
        return super.isConnected();
    }
    
    public synchronized void close() throws MessagingException {
        if (!super.isConnected()) {
            return;
        }
        IMAPProtocol protocol = null;
        try {
            final boolean isEmpty;
            synchronized (this.pool) {
                isEmpty = this.pool.authenticatedConnections.isEmpty();
            }
            if (isEmpty) {
                this.pool.logger.fine("close() - no connections ");
                this.cleanup();
                return;
            }
            protocol = this.getStoreProtocol();
            synchronized (this.pool) {
                this.pool.authenticatedConnections.removeElement(protocol);
            }
            protocol.logout();
        }
        catch (ProtocolException pex) {
            throw new MessagingException(pex.getMessage(), pex);
        }
        finally {
            this.releaseStoreProtocol(protocol);
        }
    }
    
    protected void finalize() throws Throwable {
        super.finalize();
        this.close();
    }
    
    private synchronized void cleanup() {
        if (!super.isConnected()) {
            this.logger.fine("IMAPStore cleanup, not connected");
            return;
        }
        final boolean force;
        synchronized (this.connectionFailedLock) {
            force = this.forceClose;
            this.forceClose = false;
            this.connectionFailed = false;
        }
        if (this.logger.isLoggable(Level.FINE)) {
            this.logger.fine("IMAPStore cleanup, force " + force);
        }
        Vector foldersCopy = null;
        boolean done = true;
        while (true) {
            synchronized (this.pool) {
                if (this.pool.folders != null) {
                    done = false;
                    foldersCopy = this.pool.folders;
                    this.pool.folders = null;
                }
                else {
                    done = true;
                }
            }
            if (done) {
                break;
            }
            for (int i = 0, fsize = foldersCopy.size(); i < fsize; ++i) {
                final IMAPFolder f = foldersCopy.elementAt(i);
                try {
                    if (force) {
                        this.logger.fine("force folder to close");
                        f.forceClose();
                    }
                    else {
                        this.logger.fine("close folder");
                        f.close(false);
                    }
                }
                catch (MessagingException mex) {}
                catch (IllegalStateException ex) {}
            }
        }
        synchronized (this.pool) {
            this.emptyConnectionPool(force);
        }
        try {
            super.close();
        }
        catch (MessagingException ex2) {}
        this.logger.fine("IMAPStore cleanup done");
    }
    
    public synchronized Folder getDefaultFolder() throws MessagingException {
        this.checkConnected();
        return new DefaultFolder(this);
    }
    
    public synchronized Folder getFolder(final String name) throws MessagingException {
        this.checkConnected();
        return this.newIMAPFolder(name, '\uffff');
    }
    
    public synchronized Folder getFolder(final URLName url) throws MessagingException {
        this.checkConnected();
        return this.newIMAPFolder(url.getFile(), '\uffff');
    }
    
    protected IMAPFolder newIMAPFolder(final String fullName, final char separator, final Boolean isNamespace) {
        IMAPFolder f = null;
        if (this.folderConstructor != null) {
            try {
                final Object[] o = { fullName, new Character(separator), this, isNamespace };
                f = this.folderConstructor.newInstance(o);
            }
            catch (Exception ex) {
                this.logger.log(Level.FINE, "exception creating IMAPFolder class", ex);
            }
        }
        if (f == null) {
            f = new IMAPFolder(fullName, separator, this, isNamespace);
        }
        return f;
    }
    
    protected IMAPFolder newIMAPFolder(final String fullName, final char separator) {
        return this.newIMAPFolder(fullName, separator, null);
    }
    
    protected IMAPFolder newIMAPFolder(final ListInfo li) {
        IMAPFolder f = null;
        if (this.folderConstructorLI != null) {
            try {
                final Object[] o = { li, this };
                f = this.folderConstructorLI.newInstance(o);
            }
            catch (Exception ex) {
                this.logger.log(Level.FINE, "exception creating IMAPFolder class LI", ex);
            }
        }
        if (f == null) {
            f = new IMAPFolder(li, this);
        }
        return f;
    }
    
    public Folder[] getPersonalNamespaces() throws MessagingException {
        final Namespaces ns = this.getNamespaces();
        if (ns == null || ns.personal == null) {
            return super.getPersonalNamespaces();
        }
        return this.namespaceToFolders(ns.personal, null);
    }
    
    public Folder[] getUserNamespaces(final String user) throws MessagingException {
        final Namespaces ns = this.getNamespaces();
        if (ns == null || ns.otherUsers == null) {
            return super.getUserNamespaces(user);
        }
        return this.namespaceToFolders(ns.otherUsers, user);
    }
    
    public Folder[] getSharedNamespaces() throws MessagingException {
        final Namespaces ns = this.getNamespaces();
        if (ns == null || ns.shared == null) {
            return super.getSharedNamespaces();
        }
        return this.namespaceToFolders(ns.shared, null);
    }
    
    private synchronized Namespaces getNamespaces() throws MessagingException {
        this.checkConnected();
        IMAPProtocol p = null;
        if (this.namespaces == null) {
            try {
                p = this.getStoreProtocol();
                this.namespaces = p.namespace();
            }
            catch (BadCommandException bex) {}
            catch (ConnectionException cex) {
                throw new StoreClosedException(this, cex.getMessage());
            }
            catch (ProtocolException pex) {
                throw new MessagingException(pex.getMessage(), pex);
            }
            finally {
                this.releaseStoreProtocol(p);
            }
        }
        return this.namespaces;
    }
    
    private Folder[] namespaceToFolders(final Namespaces.Namespace[] ns, final String user) {
        final Folder[] fa = new Folder[ns.length];
        for (int i = 0; i < fa.length; ++i) {
            String name = ns[i].prefix;
            if (user == null) {
                final int len = name.length();
                if (len > 0 && name.charAt(len - 1) == ns[i].delimiter) {
                    name = name.substring(0, len - 1);
                }
            }
            else {
                name += user;
            }
            fa[i] = this.newIMAPFolder(name, ns[i].delimiter, user == null);
        }
        return fa;
    }
    
    public synchronized Quota[] getQuota(final String root) throws MessagingException {
        this.checkConnected();
        Quota[] qa = null;
        IMAPProtocol p = null;
        try {
            p = this.getStoreProtocol();
            qa = p.getQuotaRoot(root);
        }
        catch (BadCommandException bex) {
            throw new MessagingException("QUOTA not supported", bex);
        }
        catch (ConnectionException cex) {
            throw new StoreClosedException(this, cex.getMessage());
        }
        catch (ProtocolException pex) {
            throw new MessagingException(pex.getMessage(), pex);
        }
        finally {
            this.releaseStoreProtocol(p);
        }
        return qa;
    }
    
    public synchronized void setQuota(final Quota quota) throws MessagingException {
        this.checkConnected();
        IMAPProtocol p = null;
        try {
            p = this.getStoreProtocol();
            p.setQuota(quota);
        }
        catch (BadCommandException bex) {
            throw new MessagingException("QUOTA not supported", bex);
        }
        catch (ConnectionException cex) {
            throw new StoreClosedException(this, cex.getMessage());
        }
        catch (ProtocolException pex) {
            throw new MessagingException(pex.getMessage(), pex);
        }
        finally {
            this.releaseStoreProtocol(p);
        }
    }
    
    private void checkConnected() {
        assert Thread.holdsLock(this);
        if (!super.isConnected()) {
            throw new IllegalStateException("Not connected");
        }
    }
    
    public void handleResponse(final Response r) {
        if (r.isOK() || r.isNO() || r.isBAD() || r.isBYE()) {
            this.handleResponseCode(r);
        }
        if (r.isBYE()) {
            this.logger.fine("IMAPStore connection dead");
            synchronized (this.connectionFailedLock) {
                this.connectionFailed = true;
                if (r.isSynthetic()) {
                    this.forceClose = true;
                }
            }
        }
    }
    
    public void idle() throws MessagingException {
        IMAPProtocol p = null;
        assert !Thread.holdsLock(this.pool);
        synchronized (this) {
            this.checkConnected();
        }
        try {
            synchronized (this.pool) {
                p = this.getStoreProtocol();
                if (this.pool.idleState != 0) {
                    try {
                        this.pool.wait();
                    }
                    catch (InterruptedException ex) {}
                    return;
                }
                p.idleStart();
                this.pool.idleState = 1;
                this.pool.idleProtocol = p;
            }
            while (true) {
                final Response r = p.readIdleResponse();
                synchronized (this.pool) {
                    if (r == null || !p.processIdleResponse(r)) {
                        this.pool.idleState = 0;
                        this.pool.notifyAll();
                        break;
                    }
                }
                if (this.enableImapEvents && r.isUnTagged()) {
                    this.notifyStoreListeners(1000, r.toString());
                }
            }
            final int minidle = this.getMinIdleTime();
            if (minidle > 0) {
                try {
                    Thread.sleep(minidle);
                }
                catch (InterruptedException ex2) {}
            }
        }
        catch (BadCommandException bex) {
            throw new MessagingException("IDLE not supported", bex);
        }
        catch (ConnectionException cex) {
            throw new StoreClosedException(this, cex.getMessage());
        }
        catch (ProtocolException pex) {
            throw new MessagingException(pex.getMessage(), pex);
        }
        finally {
            synchronized (this.pool) {
                this.pool.idleProtocol = null;
            }
            this.releaseStoreProtocol(p);
        }
    }
    
    private void waitIfIdle() throws ProtocolException {
        assert Thread.holdsLock(this.pool);
        while (this.pool.idleState != 0) {
            if (this.pool.idleState == 1) {
                this.pool.idleProtocol.idleAbort();
                this.pool.idleState = 2;
            }
            try {
                this.pool.wait();
            }
            catch (InterruptedException ex) {}
        }
    }
    
    void handleResponseCode(final Response r) {
        String s = r.getRest();
        boolean isAlert = false;
        if (s.startsWith("[")) {
            final int i = s.indexOf(93);
            if (i > 0 && s.substring(0, i + 1).equalsIgnoreCase("[ALERT]")) {
                isAlert = true;
            }
            s = s.substring(i + 1).trim();
        }
        if (isAlert) {
            this.notifyStoreListeners(1, s);
        }
        else if (r.isUnTagged() && s.length() > 0) {
            this.notifyStoreListeners(2, s);
        }
    }
    
    private String traceUser(final String user) {
        return this.debugusername ? user : "<user name suppressed>";
    }
    
    private String tracePassword(final String password) {
        return this.debugpassword ? password : ((password == null) ? "<null>" : "<non-null>");
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
        $assertionsDisabled = !((IMAPStore.class$com$sun$mail$imap$IMAPStore == null) ? (IMAPStore.class$com$sun$mail$imap$IMAPStore = class$("com.sun.mail.imap.IMAPStore")) : IMAPStore.class$com$sun$mail$imap$IMAPStore).desiredAssertionStatus();
    }
    
    static class ConnectionPool
    {
        private Vector authenticatedConnections;
        private Vector folders;
        private boolean storeConnectionInUse;
        private long lastTimePruned;
        private final boolean separateStoreConnection;
        private final long clientTimeoutInterval;
        private final long serverTimeoutInterval;
        private final int poolSize;
        private final long pruningInterval;
        private final MailLogger logger;
        private static final int RUNNING = 0;
        private static final int IDLE = 1;
        private static final int ABORTING = 2;
        private int idleState;
        private IMAPProtocol idleProtocol;
        
        ConnectionPool(final String name, final MailLogger plogger, final Session session) {
            this.authenticatedConnections = new Vector();
            this.storeConnectionInUse = false;
            this.idleState = 0;
            this.lastTimePruned = System.currentTimeMillis();
            final boolean debug = PropUtil.getBooleanSessionProperty(session, "mail." + name + ".connectionpool.debug", false);
            this.logger = plogger.getSubLogger("connectionpool", "DEBUG IMAP CP", debug);
            final int size = PropUtil.getIntSessionProperty(session, "mail." + name + ".connectionpoolsize", -1);
            if (size > 0) {
                this.poolSize = size;
                if (this.logger.isLoggable(Level.CONFIG)) {
                    this.logger.config("mail.imap.connectionpoolsize: " + this.poolSize);
                }
            }
            else {
                this.poolSize = 1;
            }
            final int connectionPoolTimeout = PropUtil.getIntSessionProperty(session, "mail." + name + ".connectionpooltimeout", -1);
            if (connectionPoolTimeout > 0) {
                this.clientTimeoutInterval = connectionPoolTimeout;
                if (this.logger.isLoggable(Level.CONFIG)) {
                    this.logger.config("mail.imap.connectionpooltimeout: " + this.clientTimeoutInterval);
                }
            }
            else {
                this.clientTimeoutInterval = 45000L;
            }
            final int serverTimeout = PropUtil.getIntSessionProperty(session, "mail." + name + ".servertimeout", -1);
            if (serverTimeout > 0) {
                this.serverTimeoutInterval = serverTimeout;
                if (this.logger.isLoggable(Level.CONFIG)) {
                    this.logger.config("mail.imap.servertimeout: " + this.serverTimeoutInterval);
                }
            }
            else {
                this.serverTimeoutInterval = 1800000L;
            }
            final int pruning = PropUtil.getIntSessionProperty(session, "mail." + name + ".pruninginterval", -1);
            if (pruning > 0) {
                this.pruningInterval = pruning;
                if (this.logger.isLoggable(Level.CONFIG)) {
                    this.logger.config("mail.imap.pruninginterval: " + this.pruningInterval);
                }
            }
            else {
                this.pruningInterval = 60000L;
            }
            this.separateStoreConnection = PropUtil.getBooleanSessionProperty(session, "mail." + name + ".separatestoreconnection", false);
            if (this.separateStoreConnection) {
                this.logger.config("dedicate a store connection");
            }
        }
    }
}
