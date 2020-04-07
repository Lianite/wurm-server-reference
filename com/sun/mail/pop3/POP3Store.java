// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.mail.pop3;

import java.util.Collections;
import javax.mail.Folder;
import java.io.IOException;
import javax.mail.MessagingException;
import java.io.EOFException;
import javax.mail.AuthenticationFailedException;
import java.util.logging.Level;
import com.sun.mail.util.PropUtil;
import javax.mail.URLName;
import javax.mail.Session;
import java.io.File;
import java.lang.reflect.Constructor;
import com.sun.mail.util.MailLogger;
import java.util.Map;
import javax.mail.Store;

public class POP3Store extends Store
{
    private String name;
    private int defaultPort;
    private boolean isSSL;
    private Protocol port;
    private POP3Folder portOwner;
    private String host;
    private int portNum;
    private String user;
    private String passwd;
    private boolean useStartTLS;
    private boolean requireStartTLS;
    private boolean usingSSL;
    private Map capabilities;
    private MailLogger logger;
    volatile Constructor messageConstructor;
    volatile boolean rsetBeforeQuit;
    volatile boolean disableTop;
    volatile boolean forgetTopHeaders;
    volatile boolean supportsUidl;
    volatile boolean cacheWriteTo;
    volatile boolean useFileCache;
    volatile File fileCacheDir;
    volatile boolean keepMessageContent;
    static /* synthetic */ Class class$javax$mail$Folder;
    
    public POP3Store(final Session session, final URLName url) {
        this(session, url, "pop3", false);
    }
    
    public POP3Store(final Session session, final URLName url, String name, boolean isSSL) {
        super(session, url);
        this.name = "pop3";
        this.defaultPort = 110;
        this.isSSL = false;
        this.port = null;
        this.portOwner = null;
        this.host = null;
        this.portNum = -1;
        this.user = null;
        this.passwd = null;
        this.useStartTLS = false;
        this.requireStartTLS = false;
        this.usingSSL = false;
        this.messageConstructor = null;
        this.rsetBeforeQuit = false;
        this.disableTop = false;
        this.forgetTopHeaders = false;
        this.supportsUidl = true;
        this.cacheWriteTo = false;
        this.useFileCache = false;
        this.fileCacheDir = null;
        this.keepMessageContent = false;
        if (url != null) {
            name = url.getProtocol();
        }
        this.name = name;
        this.logger = new MailLogger(this.getClass(), "DEBUG POP3", session);
        if (!isSSL) {
            isSSL = PropUtil.getBooleanSessionProperty(session, "mail." + name + ".ssl.enable", false);
        }
        if (isSSL) {
            this.defaultPort = 995;
        }
        else {
            this.defaultPort = 110;
        }
        this.isSSL = isSSL;
        this.rsetBeforeQuit = this.getBoolProp("rsetbeforequit");
        this.disableTop = this.getBoolProp("disabletop");
        this.forgetTopHeaders = this.getBoolProp("forgettopheaders");
        this.cacheWriteTo = this.getBoolProp("cachewriteto");
        this.useFileCache = this.getBoolProp("filecache.enable");
        final String dir = session.getProperty("mail." + name + ".filecache.dir");
        if (dir != null && this.logger.isLoggable(Level.CONFIG)) {
            this.logger.config("mail." + name + ".filecache.dir: " + dir);
        }
        if (dir != null) {
            this.fileCacheDir = new File(dir);
        }
        this.keepMessageContent = this.getBoolProp("keepmessagecontent");
        this.useStartTLS = this.getBoolProp("starttls.enable");
        this.requireStartTLS = this.getBoolProp("starttls.required");
        final String s = session.getProperty("mail." + name + ".message.class");
        if (s != null) {
            this.logger.log(Level.CONFIG, "message class: {0}", s);
            try {
                final ClassLoader cl = this.getClass().getClassLoader();
                Class messageClass = null;
                try {
                    messageClass = Class.forName(s, false, cl);
                }
                catch (ClassNotFoundException ex2) {
                    messageClass = Class.forName(s);
                }
                final Class[] c = { (POP3Store.class$javax$mail$Folder == null) ? (POP3Store.class$javax$mail$Folder = class$("javax.mail.Folder")) : POP3Store.class$javax$mail$Folder, Integer.TYPE };
                this.messageConstructor = messageClass.getConstructor((Class[])c);
            }
            catch (Exception ex) {
                this.logger.log(Level.CONFIG, "failed to load message class", ex);
            }
        }
    }
    
    private final synchronized boolean getBoolProp(String prop) {
        prop = "mail." + this.name + "." + prop;
        final boolean val = PropUtil.getBooleanSessionProperty(this.session, prop, false);
        if (this.logger.isLoggable(Level.CONFIG)) {
            this.logger.config(prop + ": " + val);
        }
        return val;
    }
    
    synchronized Session getSession() {
        return this.session;
    }
    
    protected synchronized boolean protocolConnect(final String host, int portNum, final String user, final String passwd) throws MessagingException {
        if (host == null || passwd == null || user == null) {
            return false;
        }
        if (portNum == -1) {
            portNum = PropUtil.getIntSessionProperty(this.session, "mail." + this.name + ".port", -1);
        }
        if (portNum == -1) {
            portNum = this.defaultPort;
        }
        this.host = host;
        this.portNum = portNum;
        this.user = user;
        this.passwd = passwd;
        try {
            this.port = this.getPort(null);
        }
        catch (EOFException eex) {
            throw new AuthenticationFailedException(eex.getMessage());
        }
        catch (IOException ioex) {
            throw new MessagingException("Connect failed", ioex);
        }
        return true;
    }
    
    public synchronized boolean isConnected() {
        if (!super.isConnected()) {
            return false;
        }
        try {
            if (this.port == null) {
                this.port = this.getPort(null);
            }
            else if (!this.port.noop()) {
                throw new IOException("NOOP failed");
            }
            return true;
        }
        catch (IOException ioex) {
            try {
                super.close();
            }
            catch (MessagingException mex) {}
            finally {
                return false;
            }
        }
    }
    
    synchronized Protocol getPort(final POP3Folder owner) throws IOException {
        if (this.port != null && this.portOwner == null) {
            this.portOwner = owner;
            return this.port;
        }
        final Protocol p = new Protocol(this.host, this.portNum, this.logger, this.session.getProperties(), "mail." + this.name, this.isSSL);
        Label_0277: {
            if (this.useStartTLS || this.requireStartTLS) {
                if (p.hasCapability("STLS")) {
                    if (p.stls()) {
                        p.setCapabilities(p.capa());
                        break Label_0277;
                    }
                    if (!this.requireStartTLS) {
                        break Label_0277;
                    }
                    this.logger.fine("STLS required but failed");
                    try {
                        p.quit();
                    }
                    catch (IOException ioex) {}
                    finally {
                        throw new EOFException("STLS required but failed");
                    }
                }
                if (this.requireStartTLS) {
                    this.logger.fine("STLS required but not supported");
                    try {
                        p.quit();
                    }
                    catch (IOException ioex) {}
                    finally {
                        throw new EOFException("STLS required but not supported");
                    }
                }
            }
        }
        this.capabilities = p.getCapabilities();
        this.usingSSL = p.isSSL();
        if (!this.disableTop && this.capabilities != null && !this.capabilities.containsKey("TOP")) {
            this.disableTop = true;
            this.logger.fine("server doesn't support TOP, disabling it");
        }
        this.supportsUidl = (this.capabilities == null || this.capabilities.containsKey("UIDL"));
        String msg = null;
        if ((msg = p.login(this.user, this.passwd)) != null) {
            try {
                p.quit();
            }
            catch (IOException ioex2) {}
            finally {
                throw new EOFException(msg);
            }
        }
        if (this.port == null && owner != null) {
            this.port = p;
            this.portOwner = owner;
        }
        if (this.portOwner == null) {
            this.portOwner = owner;
        }
        return p;
    }
    
    synchronized void closePort(final POP3Folder owner) {
        if (this.portOwner == owner) {
            this.port = null;
            this.portOwner = null;
        }
    }
    
    public synchronized void close() throws MessagingException {
        try {
            if (this.port != null) {
                this.port.quit();
            }
        }
        catch (IOException ioex) {}
        finally {
            this.port = null;
            super.close();
        }
    }
    
    public Folder getDefaultFolder() throws MessagingException {
        this.checkConnected();
        return new DefaultFolder(this);
    }
    
    public Folder getFolder(final String name) throws MessagingException {
        this.checkConnected();
        return new POP3Folder(this, name);
    }
    
    public Folder getFolder(final URLName url) throws MessagingException {
        this.checkConnected();
        return new POP3Folder(this, url.getFile());
    }
    
    public Map capabilities() throws MessagingException {
        final Map c;
        synchronized (this) {
            c = this.capabilities;
        }
        if (c != null) {
            return Collections.unmodifiableMap((Map<?, ?>)c);
        }
        return Collections.EMPTY_MAP;
    }
    
    public boolean isSSL() {
        return this.usingSSL;
    }
    
    protected void finalize() throws Throwable {
        super.finalize();
        if (this.port != null) {
            this.close();
        }
    }
    
    private void checkConnected() throws MessagingException {
        if (!super.isConnected()) {
            throw new MessagingException("Not connected");
        }
    }
    
    static /* synthetic */ Class class$(final String x0) {
        try {
            return Class.forName(x0);
        }
        catch (ClassNotFoundException x) {
            throw new NoClassDefFoundError().initCause(x);
        }
    }
}
