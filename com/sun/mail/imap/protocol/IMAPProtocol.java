// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.mail.imap.protocol;

import java.io.InterruptedIOException;
import com.sun.mail.iap.LiteralException;
import com.sun.mail.imap.Rights;
import com.sun.mail.imap.ACL;
import com.sun.mail.iap.ParsingException;
import java.util.Enumeration;
import java.util.Hashtable;
import javax.mail.Quota;
import com.sun.mail.imap.SortTerm;
import com.sun.mail.iap.CommandFailedException;
import javax.mail.search.SearchException;
import javax.mail.search.SearchTerm;
import com.sun.mail.imap.AppendUID;
import com.sun.mail.iap.Literal;
import java.util.Date;
import javax.mail.Flags;
import com.sun.mail.iap.BadCommandException;
import java.lang.reflect.Constructor;
import com.sun.mail.auth.Ntlm;
import com.sun.mail.util.ASCIIUtility;
import java.io.OutputStream;
import com.sun.mail.util.BASE64EncoderStream;
import java.io.ByteArrayOutputStream;
import java.util.Vector;
import java.util.Iterator;
import com.sun.mail.iap.ConnectionException;
import java.util.logging.Level;
import java.util.Locale;
import com.sun.mail.iap.Response;
import java.util.ArrayList;
import java.util.HashMap;
import com.sun.mail.iap.Argument;
import com.sun.mail.iap.ProtocolException;
import java.io.IOException;
import javax.mail.internet.MimeUtility;
import com.sun.mail.util.PropUtil;
import com.sun.mail.util.MailLogger;
import java.util.Properties;
import com.sun.mail.iap.ByteArray;
import java.util.List;
import java.util.Map;
import com.sun.mail.iap.Protocol;

public class IMAPProtocol extends Protocol
{
    private boolean connected;
    private boolean rev1;
    private boolean noauthdebug;
    private boolean authenticated;
    private Map capabilities;
    private List authmechs;
    protected SearchSequence searchSequence;
    protected String[] searchCharsets;
    private String name;
    private SaslAuthenticator saslAuthenticator;
    private ByteArray ba;
    private static final byte[] CRLF;
    private static final FetchItem[] fetchItems;
    private volatile String idleTag;
    private static final byte[] DONE;
    static /* synthetic */ Class class$com$sun$mail$imap$protocol$IMAPProtocol;
    static /* synthetic */ Class class$java$lang$String;
    static /* synthetic */ Class class$java$util$Properties;
    static /* synthetic */ Class class$com$sun$mail$util$MailLogger;
    static /* synthetic */ Class class$com$sun$mail$imap$protocol$BODYSTRUCTURE;
    static /* synthetic */ Class class$com$sun$mail$imap$protocol$BODY;
    static /* synthetic */ Class class$com$sun$mail$imap$protocol$RFC822DATA;
    static /* synthetic */ Class class$javax$mail$Flags;
    static /* synthetic */ Class class$com$sun$mail$imap$protocol$UID;
    
    public IMAPProtocol(final String name, final String host, final int port, final Properties props, final boolean isSSL, final MailLogger logger) throws IOException, ProtocolException {
        super(host, port, props, "mail." + name, isSSL, logger);
        this.connected = false;
        this.rev1 = false;
        this.noauthdebug = true;
        try {
            this.name = name;
            this.noauthdebug = !PropUtil.getBooleanProperty(props, "mail.debug.auth", false);
            if (this.capabilities == null) {
                this.capability();
            }
            if (this.hasCapability("IMAP4rev1")) {
                this.rev1 = true;
            }
            (this.searchCharsets = new String[2])[0] = "UTF-8";
            this.searchCharsets[1] = MimeUtility.mimeCharset(MimeUtility.getDefaultJavaCharset());
            this.connected = true;
        }
        finally {
            if (!this.connected) {
                this.disconnect();
            }
        }
    }
    
    public FetchItem[] getFetchItems() {
        return IMAPProtocol.fetchItems;
    }
    
    public void capability() throws ProtocolException {
        final Response[] r = this.command("CAPABILITY", null);
        if (!r[r.length - 1].isOK()) {
            throw new ProtocolException(r[r.length - 1].toString());
        }
        this.capabilities = new HashMap(10);
        this.authmechs = new ArrayList(5);
        for (int i = 0, len = r.length; i < len; ++i) {
            if (r[i] instanceof IMAPResponse) {
                final IMAPResponse ir = (IMAPResponse)r[i];
                if (ir.keyEquals("CAPABILITY")) {
                    this.parseCapabilities(ir);
                }
            }
        }
    }
    
    protected void setCapabilities(final Response r) {
        byte b;
        while ((b = r.readByte()) > 0 && b != 91) {}
        if (b == 0) {
            return;
        }
        final String s = r.readAtom();
        if (!s.equalsIgnoreCase("CAPABILITY")) {
            return;
        }
        this.capabilities = new HashMap(10);
        this.authmechs = new ArrayList(5);
        this.parseCapabilities(r);
    }
    
    protected void parseCapabilities(final Response r) {
        String s;
        while ((s = r.readAtom(']')) != null) {
            if (s.length() == 0) {
                if (r.peekByte() == 93) {
                    break;
                }
                r.skipToken();
            }
            else {
                this.capabilities.put(s.toUpperCase(Locale.ENGLISH), s);
                if (!s.regionMatches(true, 0, "AUTH=", 0, 5)) {
                    continue;
                }
                this.authmechs.add(s.substring(5));
                if (!this.logger.isLoggable(Level.FINE)) {
                    continue;
                }
                this.logger.fine("AUTH: " + s.substring(5));
            }
        }
    }
    
    protected void processGreeting(final Response r) throws ProtocolException {
        super.processGreeting(r);
        if (r.isOK()) {
            this.setCapabilities(r);
            return;
        }
        final IMAPResponse ir = (IMAPResponse)r;
        if (ir.keyEquals("PREAUTH")) {
            this.authenticated = true;
            this.setCapabilities(r);
            return;
        }
        throw new ConnectionException(this, r);
    }
    
    public boolean isAuthenticated() {
        return this.authenticated;
    }
    
    public boolean isREV1() {
        return this.rev1;
    }
    
    protected boolean supportsNonSyncLiterals() {
        return this.hasCapability("LITERAL+");
    }
    
    public Response readResponse() throws IOException, ProtocolException {
        IMAPResponse r = new IMAPResponse(this);
        if (r.keyEquals("FETCH")) {
            r = new FetchResponse(r, this.getFetchItems());
        }
        return r;
    }
    
    public boolean hasCapability(String c) {
        if (c.endsWith("*")) {
            c = c.substring(0, c.length() - 1).toUpperCase(Locale.ENGLISH);
            final Iterator it = this.capabilities.keySet().iterator();
            while (it.hasNext()) {
                if (it.next().startsWith(c)) {
                    return true;
                }
            }
            return false;
        }
        return this.capabilities.containsKey(c.toUpperCase(Locale.ENGLISH));
    }
    
    public Map getCapabilities() {
        return this.capabilities;
    }
    
    public void disconnect() {
        super.disconnect();
        this.authenticated = false;
    }
    
    public void noop() throws ProtocolException {
        this.logger.fine("IMAPProtocol noop");
        this.simpleCommand("NOOP", null);
    }
    
    public void logout() throws ProtocolException {
        try {
            final Response[] r = this.command("LOGOUT", null);
            this.authenticated = false;
            this.notifyResponseHandlers(r);
        }
        finally {
            this.disconnect();
        }
    }
    
    public void login(final String u, final String p) throws ProtocolException {
        final Argument args = new Argument();
        args.writeString(u);
        args.writeString(p);
        Response[] r = null;
        try {
            if (this.noauthdebug && this.isTracing()) {
                this.logger.fine("LOGIN command trace suppressed");
                this.suspendTracing();
            }
            r = this.command("LOGIN", args);
        }
        finally {
            this.resumeTracing();
        }
        this.notifyResponseHandlers(r);
        if (this.noauthdebug && this.isTracing()) {
            this.logger.fine("LOGIN command result: " + r[r.length - 1]);
        }
        this.handleResult(r[r.length - 1]);
        this.setCapabilities(r[r.length - 1]);
        this.authenticated = true;
    }
    
    public synchronized void authlogin(final String u, final String p) throws ProtocolException {
        final Vector v = new Vector();
        String tag = null;
        Response r = null;
        boolean done = false;
        try {
            if (this.noauthdebug && this.isTracing()) {
                this.logger.fine("AUTHENTICATE LOGIN command trace suppressed");
                this.suspendTracing();
            }
            try {
                tag = this.writeCommand("AUTHENTICATE LOGIN", null);
            }
            catch (Exception ex) {
                r = Response.byeResponse(ex);
                done = true;
            }
            final OutputStream os = this.getOutputStream();
            final ByteArrayOutputStream bos = new ByteArrayOutputStream();
            final OutputStream b64os = new BASE64EncoderStream(bos, Integer.MAX_VALUE);
            boolean first = true;
            while (!done) {
                try {
                    r = this.readResponse();
                    if (r.isContinuation()) {
                        String s;
                        if (first) {
                            s = u;
                            first = false;
                        }
                        else {
                            s = p;
                        }
                        b64os.write(ASCIIUtility.getBytes(s));
                        b64os.flush();
                        bos.write(IMAPProtocol.CRLF);
                        os.write(bos.toByteArray());
                        os.flush();
                        bos.reset();
                    }
                    else if (r.isTagged() && r.getTag().equals(tag)) {
                        done = true;
                    }
                    else if (r.isBYE()) {
                        done = true;
                    }
                    else {
                        v.addElement(r);
                    }
                }
                catch (Exception ioex) {
                    r = Response.byeResponse(ioex);
                    done = true;
                }
            }
        }
        finally {
            this.resumeTracing();
        }
        final Response[] responses = new Response[v.size()];
        v.copyInto(responses);
        this.notifyResponseHandlers(responses);
        if (this.noauthdebug && this.isTracing()) {
            this.logger.fine("AUTHENTICATE LOGIN command result: " + r);
        }
        this.handleResult(r);
        this.setCapabilities(r);
        this.authenticated = true;
    }
    
    public synchronized void authplain(final String authzid, final String u, final String p) throws ProtocolException {
        final Vector v = new Vector();
        String tag = null;
        Response r = null;
        boolean done = false;
        try {
            if (this.noauthdebug && this.isTracing()) {
                this.logger.fine("AUTHENTICATE PLAIN command trace suppressed");
                this.suspendTracing();
            }
            try {
                tag = this.writeCommand("AUTHENTICATE PLAIN", null);
            }
            catch (Exception ex) {
                r = Response.byeResponse(ex);
                done = true;
            }
            final OutputStream os = this.getOutputStream();
            final ByteArrayOutputStream bos = new ByteArrayOutputStream();
            final OutputStream b64os = new BASE64EncoderStream(bos, Integer.MAX_VALUE);
            while (!done) {
                try {
                    r = this.readResponse();
                    if (r.isContinuation()) {
                        final String nullByte = "\u0000";
                        final String s = ((authzid == null) ? "" : authzid) + "\u0000" + u + "\u0000" + p;
                        b64os.write(ASCIIUtility.getBytes(s));
                        b64os.flush();
                        bos.write(IMAPProtocol.CRLF);
                        os.write(bos.toByteArray());
                        os.flush();
                        bos.reset();
                    }
                    else if (r.isTagged() && r.getTag().equals(tag)) {
                        done = true;
                    }
                    else if (r.isBYE()) {
                        done = true;
                    }
                    else {
                        v.addElement(r);
                    }
                }
                catch (Exception ioex) {
                    r = Response.byeResponse(ioex);
                    done = true;
                }
            }
        }
        finally {
            this.resumeTracing();
        }
        final Response[] responses = new Response[v.size()];
        v.copyInto(responses);
        this.notifyResponseHandlers(responses);
        if (this.noauthdebug && this.isTracing()) {
            this.logger.fine("AUTHENTICATE PLAIN command result: " + r);
        }
        this.handleResult(r);
        this.setCapabilities(r);
        this.authenticated = true;
    }
    
    public synchronized void authntlm(final String authzid, final String u, final String p) throws ProtocolException {
        final Vector v = new Vector();
        String tag = null;
        Response r = null;
        boolean done = false;
        final String type1Msg = null;
        final int flags = PropUtil.getIntProperty(this.props, "mail." + this.name + ".auth.ntlm.flags", 0);
        final String domain = this.props.getProperty("mail." + this.name + ".auth.ntlm.domain", "");
        final Ntlm ntlm = new Ntlm(domain, this.getLocalHost(), u, p, this.logger);
        try {
            if (this.noauthdebug && this.isTracing()) {
                this.logger.fine("AUTHENTICATE NTLM command trace suppressed");
                this.suspendTracing();
            }
            try {
                tag = this.writeCommand("AUTHENTICATE NTLM", null);
            }
            catch (Exception ex) {
                r = Response.byeResponse(ex);
                done = true;
            }
            final OutputStream os = this.getOutputStream();
            boolean first = true;
            while (!done) {
                try {
                    r = this.readResponse();
                    if (r.isContinuation()) {
                        String s;
                        if (first) {
                            s = ntlm.generateType1Msg(flags);
                            first = false;
                        }
                        else {
                            s = ntlm.generateType3Msg(r.getRest());
                        }
                        os.write(ASCIIUtility.getBytes(s));
                        os.write(IMAPProtocol.CRLF);
                        os.flush();
                    }
                    else if (r.isTagged() && r.getTag().equals(tag)) {
                        done = true;
                    }
                    else if (r.isBYE()) {
                        done = true;
                    }
                    else {
                        v.addElement(r);
                    }
                }
                catch (Exception ioex) {
                    r = Response.byeResponse(ioex);
                    done = true;
                }
            }
        }
        finally {
            this.resumeTracing();
        }
        final Response[] responses = new Response[v.size()];
        v.copyInto(responses);
        this.notifyResponseHandlers(responses);
        if (this.noauthdebug && this.isTracing()) {
            this.logger.fine("AUTHENTICATE NTLM command result: " + r);
        }
        this.handleResult(r);
        this.setCapabilities(r);
        this.authenticated = true;
    }
    
    public void sasllogin(final String[] allowed, final String realm, final String authzid, final String u, final String p) throws ProtocolException {
        if (this.saslAuthenticator == null) {
            try {
                final Class sac = Class.forName("com.sun.mail.imap.protocol.IMAPSaslAuthenticator");
                final Constructor c = sac.getConstructor((IMAPProtocol.class$com$sun$mail$imap$protocol$IMAPProtocol == null) ? (IMAPProtocol.class$com$sun$mail$imap$protocol$IMAPProtocol = class$("com.sun.mail.imap.protocol.IMAPProtocol")) : IMAPProtocol.class$com$sun$mail$imap$protocol$IMAPProtocol, (IMAPProtocol.class$java$lang$String == null) ? (IMAPProtocol.class$java$lang$String = class$("java.lang.String")) : IMAPProtocol.class$java$lang$String, (IMAPProtocol.class$java$util$Properties == null) ? (IMAPProtocol.class$java$util$Properties = class$("java.util.Properties")) : IMAPProtocol.class$java$util$Properties, (IMAPProtocol.class$com$sun$mail$util$MailLogger == null) ? (IMAPProtocol.class$com$sun$mail$util$MailLogger = class$("com.sun.mail.util.MailLogger")) : IMAPProtocol.class$com$sun$mail$util$MailLogger, (IMAPProtocol.class$java$lang$String == null) ? (IMAPProtocol.class$java$lang$String = class$("java.lang.String")) : IMAPProtocol.class$java$lang$String);
                this.saslAuthenticator = c.newInstance(this, this.name, this.props, this.logger, this.host);
            }
            catch (Exception ex) {
                this.logger.log(Level.FINE, "Can't load SASL authenticator", ex);
                return;
            }
        }
        List v;
        if (allowed != null && allowed.length > 0) {
            v = new ArrayList(allowed.length);
            for (int i = 0; i < allowed.length; ++i) {
                if (this.authmechs.contains(allowed[i])) {
                    v.add(allowed[i]);
                }
            }
        }
        else {
            v = this.authmechs;
        }
        final String[] mechs = v.toArray(new String[v.size()]);
        try {
            if (this.noauthdebug && this.isTracing()) {
                this.logger.fine("SASL authentication command trace suppressed");
                this.suspendTracing();
            }
            if (this.saslAuthenticator.authenticate(mechs, realm, authzid, u, p)) {
                if (this.noauthdebug && this.isTracing()) {
                    this.logger.fine("SASL authentication succeeded");
                }
                this.authenticated = true;
            }
            else if (this.noauthdebug && this.isTracing()) {
                this.logger.fine("SASL authentication failed");
            }
        }
        finally {
            this.resumeTracing();
        }
    }
    
    OutputStream getIMAPOutputStream() {
        return this.getOutputStream();
    }
    
    public void proxyauth(final String u) throws ProtocolException {
        final Argument args = new Argument();
        args.writeString(u);
        this.simpleCommand("PROXYAUTH", args);
    }
    
    public void id(final String guid) throws ProtocolException {
        this.simpleCommand("ID (\"GUID\" \"" + guid + "\")", null);
    }
    
    public void startTLS() throws ProtocolException {
        try {
            super.startTLS("STARTTLS");
        }
        catch (ProtocolException pex) {
            this.logger.log(Level.FINE, "STARTTLS ProtocolException", pex);
            throw pex;
        }
        catch (Exception ex) {
            this.logger.log(Level.FINE, "STARTTLS Exception", ex);
            final Response[] r = { Response.byeResponse(ex) };
            this.notifyResponseHandlers(r);
            this.disconnect();
            throw new ProtocolException("STARTTLS failure", ex);
        }
    }
    
    public MailboxInfo select(String mbox) throws ProtocolException {
        mbox = BASE64MailboxEncoder.encode(mbox);
        final Argument args = new Argument();
        args.writeString(mbox);
        final Response[] r = this.command("SELECT", args);
        final MailboxInfo minfo = new MailboxInfo(r);
        this.notifyResponseHandlers(r);
        final Response response = r[r.length - 1];
        if (response.isOK()) {
            if (response.toString().indexOf("READ-ONLY") != -1) {
                minfo.mode = 1;
            }
            else {
                minfo.mode = 2;
            }
        }
        this.handleResult(response);
        return minfo;
    }
    
    public MailboxInfo examine(String mbox) throws ProtocolException {
        mbox = BASE64MailboxEncoder.encode(mbox);
        final Argument args = new Argument();
        args.writeString(mbox);
        final Response[] r = this.command("EXAMINE", args);
        final MailboxInfo minfo = new MailboxInfo(r);
        minfo.mode = 1;
        this.notifyResponseHandlers(r);
        this.handleResult(r[r.length - 1]);
        return minfo;
    }
    
    public void unselect() throws ProtocolException {
        if (!this.hasCapability("UNSELECT")) {
            throw new BadCommandException("UNSELECT not supported");
        }
        this.simpleCommand("UNSELECT", null);
    }
    
    public Status status(String mbox, String[] items) throws ProtocolException {
        if (!this.isREV1() && !this.hasCapability("IMAP4SUNVERSION")) {
            throw new BadCommandException("STATUS not supported");
        }
        mbox = BASE64MailboxEncoder.encode(mbox);
        final Argument args = new Argument();
        args.writeString(mbox);
        final Argument itemArgs = new Argument();
        if (items == null) {
            items = Status.standardItems;
        }
        for (int i = 0, len = items.length; i < len; ++i) {
            itemArgs.writeAtom(items[i]);
        }
        args.writeArgument(itemArgs);
        final Response[] r = this.command("STATUS", args);
        Status status = null;
        final Response response = r[r.length - 1];
        if (response.isOK()) {
            for (int j = 0, len2 = r.length; j < len2; ++j) {
                if (r[j] instanceof IMAPResponse) {
                    final IMAPResponse ir = (IMAPResponse)r[j];
                    if (ir.keyEquals("STATUS")) {
                        if (status == null) {
                            status = new Status(ir);
                        }
                        else {
                            Status.add(status, new Status(ir));
                        }
                        r[j] = null;
                    }
                }
            }
        }
        this.notifyResponseHandlers(r);
        this.handleResult(response);
        return status;
    }
    
    public void create(String mbox) throws ProtocolException {
        mbox = BASE64MailboxEncoder.encode(mbox);
        final Argument args = new Argument();
        args.writeString(mbox);
        this.simpleCommand("CREATE", args);
    }
    
    public void delete(String mbox) throws ProtocolException {
        mbox = BASE64MailboxEncoder.encode(mbox);
        final Argument args = new Argument();
        args.writeString(mbox);
        this.simpleCommand("DELETE", args);
    }
    
    public void rename(String o, String n) throws ProtocolException {
        o = BASE64MailboxEncoder.encode(o);
        n = BASE64MailboxEncoder.encode(n);
        final Argument args = new Argument();
        args.writeString(o);
        args.writeString(n);
        this.simpleCommand("RENAME", args);
    }
    
    public void subscribe(String mbox) throws ProtocolException {
        final Argument args = new Argument();
        mbox = BASE64MailboxEncoder.encode(mbox);
        args.writeString(mbox);
        this.simpleCommand("SUBSCRIBE", args);
    }
    
    public void unsubscribe(String mbox) throws ProtocolException {
        final Argument args = new Argument();
        mbox = BASE64MailboxEncoder.encode(mbox);
        args.writeString(mbox);
        this.simpleCommand("UNSUBSCRIBE", args);
    }
    
    public ListInfo[] list(final String ref, final String pattern) throws ProtocolException {
        return this.doList("LIST", ref, pattern);
    }
    
    public ListInfo[] lsub(final String ref, final String pattern) throws ProtocolException {
        return this.doList("LSUB", ref, pattern);
    }
    
    protected ListInfo[] doList(final String cmd, String ref, String pat) throws ProtocolException {
        ref = BASE64MailboxEncoder.encode(ref);
        pat = BASE64MailboxEncoder.encode(pat);
        final Argument args = new Argument();
        args.writeString(ref);
        args.writeString(pat);
        final Response[] r = this.command(cmd, args);
        ListInfo[] linfo = null;
        final Response response = r[r.length - 1];
        if (response.isOK()) {
            final Vector v = new Vector(1);
            for (int i = 0, len = r.length; i < len; ++i) {
                if (r[i] instanceof IMAPResponse) {
                    final IMAPResponse ir = (IMAPResponse)r[i];
                    if (ir.keyEquals(cmd)) {
                        v.addElement(new ListInfo(ir));
                        r[i] = null;
                    }
                }
            }
            if (v.size() > 0) {
                linfo = new ListInfo[v.size()];
                v.copyInto(linfo);
            }
        }
        this.notifyResponseHandlers(r);
        this.handleResult(response);
        return linfo;
    }
    
    public void append(final String mbox, final Flags f, final Date d, final Literal data) throws ProtocolException {
        this.appenduid(mbox, f, d, data, false);
    }
    
    public AppendUID appenduid(final String mbox, final Flags f, final Date d, final Literal data) throws ProtocolException {
        return this.appenduid(mbox, f, d, data, true);
    }
    
    public AppendUID appenduid(String mbox, Flags f, final Date d, final Literal data, final boolean uid) throws ProtocolException {
        mbox = BASE64MailboxEncoder.encode(mbox);
        final Argument args = new Argument();
        args.writeString(mbox);
        if (f != null) {
            if (f.contains(Flags.Flag.RECENT)) {
                f = new Flags(f);
                f.remove(Flags.Flag.RECENT);
            }
            args.writeAtom(this.createFlagList(f));
        }
        if (d != null) {
            args.writeString(INTERNALDATE.format(d));
        }
        args.writeBytes(data);
        final Response[] r = this.command("APPEND", args);
        this.notifyResponseHandlers(r);
        this.handleResult(r[r.length - 1]);
        if (uid) {
            return this.getAppendUID(r[r.length - 1]);
        }
        return null;
    }
    
    private AppendUID getAppendUID(final Response r) {
        if (!r.isOK()) {
            return null;
        }
        byte b;
        while ((b = r.readByte()) > 0 && b != 91) {}
        if (b == 0) {
            return null;
        }
        final String s = r.readAtom();
        if (!s.equalsIgnoreCase("APPENDUID")) {
            return null;
        }
        final long uidvalidity = r.readLong();
        final long uid = r.readLong();
        return new AppendUID(uidvalidity, uid);
    }
    
    public void check() throws ProtocolException {
        this.simpleCommand("CHECK", null);
    }
    
    public void close() throws ProtocolException {
        this.simpleCommand("CLOSE", null);
    }
    
    public void expunge() throws ProtocolException {
        this.simpleCommand("EXPUNGE", null);
    }
    
    public void uidexpunge(final UIDSet[] set) throws ProtocolException {
        if (!this.hasCapability("UIDPLUS")) {
            throw new BadCommandException("UID EXPUNGE not supported");
        }
        this.simpleCommand("UID EXPUNGE " + UIDSet.toString(set), null);
    }
    
    public BODYSTRUCTURE fetchBodyStructure(final int msgno) throws ProtocolException {
        final Response[] r = this.fetch(msgno, "BODYSTRUCTURE");
        this.notifyResponseHandlers(r);
        final Response response = r[r.length - 1];
        if (response.isOK()) {
            return (BODYSTRUCTURE)FetchResponse.getItem(r, msgno, (IMAPProtocol.class$com$sun$mail$imap$protocol$BODYSTRUCTURE == null) ? (IMAPProtocol.class$com$sun$mail$imap$protocol$BODYSTRUCTURE = class$("com.sun.mail.imap.protocol.BODYSTRUCTURE")) : IMAPProtocol.class$com$sun$mail$imap$protocol$BODYSTRUCTURE);
        }
        if (response.isNO()) {
            return null;
        }
        this.handleResult(response);
        return null;
    }
    
    public BODY peekBody(final int msgno, final String section) throws ProtocolException {
        return this.fetchBody(msgno, section, true);
    }
    
    public BODY fetchBody(final int msgno, final String section) throws ProtocolException {
        return this.fetchBody(msgno, section, false);
    }
    
    protected BODY fetchBody(final int msgno, final String section, final boolean peek) throws ProtocolException {
        Response[] r;
        if (peek) {
            r = this.fetch(msgno, "BODY.PEEK[" + ((section == null) ? "]" : (section + "]")));
        }
        else {
            r = this.fetch(msgno, "BODY[" + ((section == null) ? "]" : (section + "]")));
        }
        this.notifyResponseHandlers(r);
        final Response response = r[r.length - 1];
        if (response.isOK()) {
            return (BODY)FetchResponse.getItem(r, msgno, (IMAPProtocol.class$com$sun$mail$imap$protocol$BODY == null) ? (IMAPProtocol.class$com$sun$mail$imap$protocol$BODY = class$("com.sun.mail.imap.protocol.BODY")) : IMAPProtocol.class$com$sun$mail$imap$protocol$BODY);
        }
        if (response.isNO()) {
            return null;
        }
        this.handleResult(response);
        return null;
    }
    
    public BODY peekBody(final int msgno, final String section, final int start, final int size) throws ProtocolException {
        return this.fetchBody(msgno, section, start, size, true, null);
    }
    
    public BODY fetchBody(final int msgno, final String section, final int start, final int size) throws ProtocolException {
        return this.fetchBody(msgno, section, start, size, false, null);
    }
    
    public BODY peekBody(final int msgno, final String section, final int start, final int size, final ByteArray ba) throws ProtocolException {
        return this.fetchBody(msgno, section, start, size, true, ba);
    }
    
    public BODY fetchBody(final int msgno, final String section, final int start, final int size, final ByteArray ba) throws ProtocolException {
        return this.fetchBody(msgno, section, start, size, false, ba);
    }
    
    protected BODY fetchBody(final int msgno, final String section, final int start, final int size, final boolean peek, final ByteArray ba) throws ProtocolException {
        this.ba = ba;
        final Response[] r = this.fetch(msgno, (peek ? "BODY.PEEK[" : "BODY[") + ((section == null) ? "]<" : (section + "]<")) + String.valueOf(start) + "." + String.valueOf(size) + ">");
        this.notifyResponseHandlers(r);
        final Response response = r[r.length - 1];
        if (response.isOK()) {
            return (BODY)FetchResponse.getItem(r, msgno, (IMAPProtocol.class$com$sun$mail$imap$protocol$BODY == null) ? (IMAPProtocol.class$com$sun$mail$imap$protocol$BODY = class$("com.sun.mail.imap.protocol.BODY")) : IMAPProtocol.class$com$sun$mail$imap$protocol$BODY);
        }
        if (response.isNO()) {
            return null;
        }
        this.handleResult(response);
        return null;
    }
    
    protected ByteArray getResponseBuffer() {
        final ByteArray ret = this.ba;
        this.ba = null;
        return ret;
    }
    
    public RFC822DATA fetchRFC822(final int msgno, final String what) throws ProtocolException {
        final Response[] r = this.fetch(msgno, (what == null) ? "RFC822" : ("RFC822." + what));
        this.notifyResponseHandlers(r);
        final Response response = r[r.length - 1];
        if (response.isOK()) {
            return (RFC822DATA)FetchResponse.getItem(r, msgno, (IMAPProtocol.class$com$sun$mail$imap$protocol$RFC822DATA == null) ? (IMAPProtocol.class$com$sun$mail$imap$protocol$RFC822DATA = class$("com.sun.mail.imap.protocol.RFC822DATA")) : IMAPProtocol.class$com$sun$mail$imap$protocol$RFC822DATA);
        }
        if (response.isNO()) {
            return null;
        }
        this.handleResult(response);
        return null;
    }
    
    public Flags fetchFlags(final int msgno) throws ProtocolException {
        Flags flags = null;
        final Response[] r = this.fetch(msgno, "FLAGS");
        for (int i = 0, len = r.length; i < len; ++i) {
            if (r[i] != null && r[i] instanceof FetchResponse) {
                if (((FetchResponse)r[i]).getNumber() == msgno) {
                    final FetchResponse fr = (FetchResponse)r[i];
                    if ((flags = (Flags)fr.getItem((IMAPProtocol.class$javax$mail$Flags == null) ? (IMAPProtocol.class$javax$mail$Flags = class$("javax.mail.Flags")) : IMAPProtocol.class$javax$mail$Flags)) != null) {
                        r[i] = null;
                        break;
                    }
                }
            }
        }
        this.notifyResponseHandlers(r);
        this.handleResult(r[r.length - 1]);
        return flags;
    }
    
    public UID fetchUID(final int msgno) throws ProtocolException {
        final Response[] r = this.fetch(msgno, "UID");
        this.notifyResponseHandlers(r);
        final Response response = r[r.length - 1];
        if (response.isOK()) {
            return (UID)FetchResponse.getItem(r, msgno, (IMAPProtocol.class$com$sun$mail$imap$protocol$UID == null) ? (IMAPProtocol.class$com$sun$mail$imap$protocol$UID = class$("com.sun.mail.imap.protocol.UID")) : IMAPProtocol.class$com$sun$mail$imap$protocol$UID);
        }
        if (response.isNO()) {
            return null;
        }
        this.handleResult(response);
        return null;
    }
    
    public UID fetchSequenceNumber(final long uid) throws ProtocolException {
        UID u = null;
        final Response[] r = this.fetch(String.valueOf(uid), "UID", true);
        for (int i = 0, len = r.length; i < len; ++i) {
            if (r[i] != null) {
                if (r[i] instanceof FetchResponse) {
                    final FetchResponse fr = (FetchResponse)r[i];
                    if ((u = (UID)fr.getItem((IMAPProtocol.class$com$sun$mail$imap$protocol$UID == null) ? (IMAPProtocol.class$com$sun$mail$imap$protocol$UID = class$("com.sun.mail.imap.protocol.UID")) : IMAPProtocol.class$com$sun$mail$imap$protocol$UID)) != null) {
                        if (u.uid == uid) {
                            break;
                        }
                        u = null;
                    }
                }
            }
        }
        this.notifyResponseHandlers(r);
        this.handleResult(r[r.length - 1]);
        return u;
    }
    
    public UID[] fetchSequenceNumbers(final long start, final long end) throws ProtocolException {
        final Response[] r = this.fetch(String.valueOf(start) + ":" + ((end == -1L) ? "*" : String.valueOf(end)), "UID", true);
        final Vector v = new Vector();
        for (int i = 0, len = r.length; i < len; ++i) {
            if (r[i] != null) {
                if (r[i] instanceof FetchResponse) {
                    final FetchResponse fr = (FetchResponse)r[i];
                    final UID u;
                    if ((u = (UID)fr.getItem((IMAPProtocol.class$com$sun$mail$imap$protocol$UID == null) ? (IMAPProtocol.class$com$sun$mail$imap$protocol$UID = class$("com.sun.mail.imap.protocol.UID")) : IMAPProtocol.class$com$sun$mail$imap$protocol$UID)) != null) {
                        v.addElement(u);
                    }
                }
            }
        }
        this.notifyResponseHandlers(r);
        this.handleResult(r[r.length - 1]);
        final UID[] ua = new UID[v.size()];
        v.copyInto(ua);
        return ua;
    }
    
    public UID[] fetchSequenceNumbers(final long[] uids) throws ProtocolException {
        final StringBuffer sb = new StringBuffer();
        for (int i = 0; i < uids.length; ++i) {
            if (i > 0) {
                sb.append(",");
            }
            sb.append(String.valueOf(uids[i]));
        }
        final Response[] r = this.fetch(sb.toString(), "UID", true);
        final Vector v = new Vector();
        for (int j = 0, len = r.length; j < len; ++j) {
            if (r[j] != null) {
                if (r[j] instanceof FetchResponse) {
                    final FetchResponse fr = (FetchResponse)r[j];
                    final UID u;
                    if ((u = (UID)fr.getItem((IMAPProtocol.class$com$sun$mail$imap$protocol$UID == null) ? (IMAPProtocol.class$com$sun$mail$imap$protocol$UID = class$("com.sun.mail.imap.protocol.UID")) : IMAPProtocol.class$com$sun$mail$imap$protocol$UID)) != null) {
                        v.addElement(u);
                    }
                }
            }
        }
        this.notifyResponseHandlers(r);
        this.handleResult(r[r.length - 1]);
        final UID[] ua = new UID[v.size()];
        v.copyInto(ua);
        return ua;
    }
    
    public Response[] fetch(final MessageSet[] msgsets, final String what) throws ProtocolException {
        return this.fetch(MessageSet.toString(msgsets), what, false);
    }
    
    public Response[] fetch(final int start, final int end, final String what) throws ProtocolException {
        return this.fetch(String.valueOf(start) + ":" + String.valueOf(end), what, false);
    }
    
    public Response[] fetch(final int msg, final String what) throws ProtocolException {
        return this.fetch(String.valueOf(msg), what, false);
    }
    
    private Response[] fetch(final String msgSequence, final String what, final boolean uid) throws ProtocolException {
        if (uid) {
            return this.command("UID FETCH " + msgSequence + " (" + what + ")", null);
        }
        return this.command("FETCH " + msgSequence + " (" + what + ")", null);
    }
    
    public void copy(final MessageSet[] msgsets, final String mbox) throws ProtocolException {
        this.copy(MessageSet.toString(msgsets), mbox);
    }
    
    public void copy(final int start, final int end, final String mbox) throws ProtocolException {
        this.copy(String.valueOf(start) + ":" + String.valueOf(end), mbox);
    }
    
    private void copy(final String msgSequence, String mbox) throws ProtocolException {
        mbox = BASE64MailboxEncoder.encode(mbox);
        final Argument args = new Argument();
        args.writeAtom(msgSequence);
        args.writeString(mbox);
        this.simpleCommand("COPY", args);
    }
    
    public void storeFlags(final MessageSet[] msgsets, final Flags flags, final boolean set) throws ProtocolException {
        this.storeFlags(MessageSet.toString(msgsets), flags, set);
    }
    
    public void storeFlags(final int start, final int end, final Flags flags, final boolean set) throws ProtocolException {
        this.storeFlags(String.valueOf(start) + ":" + String.valueOf(end), flags, set);
    }
    
    public void storeFlags(final int msg, final Flags flags, final boolean set) throws ProtocolException {
        this.storeFlags(String.valueOf(msg), flags, set);
    }
    
    private void storeFlags(final String msgset, final Flags flags, final boolean set) throws ProtocolException {
        Response[] r;
        if (set) {
            r = this.command("STORE " + msgset + " +FLAGS " + this.createFlagList(flags), null);
        }
        else {
            r = this.command("STORE " + msgset + " -FLAGS " + this.createFlagList(flags), null);
        }
        this.notifyResponseHandlers(r);
        this.handleResult(r[r.length - 1]);
    }
    
    private String createFlagList(final Flags flags) {
        final StringBuffer sb = new StringBuffer();
        sb.append("(");
        final Flags.Flag[] sf = flags.getSystemFlags();
        boolean first = true;
        for (int i = 0; i < sf.length; ++i) {
            final Flags.Flag f = sf[i];
            String s;
            if (f == Flags.Flag.ANSWERED) {
                s = "\\Answered";
            }
            else if (f == Flags.Flag.DELETED) {
                s = "\\Deleted";
            }
            else if (f == Flags.Flag.DRAFT) {
                s = "\\Draft";
            }
            else if (f == Flags.Flag.FLAGGED) {
                s = "\\Flagged";
            }
            else if (f == Flags.Flag.RECENT) {
                s = "\\Recent";
            }
            else {
                if (f != Flags.Flag.SEEN) {
                    continue;
                }
                s = "\\Seen";
            }
            if (first) {
                first = false;
            }
            else {
                sb.append(' ');
            }
            sb.append(s);
        }
        final String[] uf = flags.getUserFlags();
        for (int j = 0; j < uf.length; ++j) {
            if (first) {
                first = false;
            }
            else {
                sb.append(' ');
            }
            sb.append(uf[j]);
        }
        sb.append(")");
        return sb.toString();
    }
    
    public int[] search(final MessageSet[] msgsets, final SearchTerm term) throws ProtocolException, SearchException {
        return this.search(MessageSet.toString(msgsets), term);
    }
    
    public int[] search(final SearchTerm term) throws ProtocolException, SearchException {
        return this.search("ALL", term);
    }
    
    private int[] search(final String msgSequence, final SearchTerm term) throws ProtocolException, SearchException {
        this.getSearchSequence();
        if (SearchSequence.isAscii(term)) {
            try {
                return this.issueSearch(msgSequence, term, null);
            }
            catch (IOException ex) {}
        }
        for (int i = 0; i < this.searchCharsets.length; ++i) {
            if (this.searchCharsets[i] != null) {
                try {
                    return this.issueSearch(msgSequence, term, this.searchCharsets[i]);
                }
                catch (CommandFailedException cfx) {
                    this.searchCharsets[i] = null;
                }
                catch (IOException ioex) {}
                catch (ProtocolException pex) {
                    throw pex;
                }
                catch (SearchException sex) {
                    throw sex;
                }
            }
        }
        throw new SearchException("Search failed");
    }
    
    private int[] issueSearch(final String msgSequence, final SearchTerm term, final String charset) throws ProtocolException, SearchException, IOException {
        final Argument args = this.getSearchSequence().generateSequence(term, (charset == null) ? null : MimeUtility.javaCharset(charset));
        args.writeAtom(msgSequence);
        Response[] r;
        if (charset == null) {
            r = this.command("SEARCH", args);
        }
        else {
            r = this.command("SEARCH CHARSET " + charset, args);
        }
        final Response response = r[r.length - 1];
        int[] matches = null;
        if (response.isOK()) {
            final Vector v = new Vector();
            for (int i = 0, len = r.length; i < len; ++i) {
                if (r[i] instanceof IMAPResponse) {
                    final IMAPResponse ir = (IMAPResponse)r[i];
                    if (ir.keyEquals("SEARCH")) {
                        int num;
                        while ((num = ir.readNumber()) != -1) {
                            v.addElement(new Integer(num));
                        }
                        r[i] = null;
                    }
                }
            }
            final int vsize = v.size();
            matches = new int[vsize];
            for (int j = 0; j < vsize; ++j) {
                matches[j] = v.elementAt(j);
            }
        }
        this.notifyResponseHandlers(r);
        this.handleResult(response);
        return matches;
    }
    
    protected SearchSequence getSearchSequence() {
        if (this.searchSequence == null) {
            this.searchSequence = new SearchSequence();
        }
        return this.searchSequence;
    }
    
    public int[] sort(final SortTerm[] term, final SearchTerm sterm) throws ProtocolException, SearchException {
        if (!this.hasCapability("SORT*")) {
            throw new BadCommandException("SORT not supported");
        }
        if (term == null || term.length == 0) {
            throw new BadCommandException("Must have at least one sort term");
        }
        final Argument args = new Argument();
        final Argument sargs = new Argument();
        for (int i = 0; i < term.length; ++i) {
            sargs.writeAtom(term[i].toString());
        }
        args.writeArgument(sargs);
        args.writeAtom("UTF-8");
        Label_0141: {
            if (sterm != null) {
                try {
                    args.append(this.getSearchSequence().generateSequence(sterm, "UTF-8"));
                    break Label_0141;
                }
                catch (IOException ioex) {
                    throw new SearchException(ioex.toString());
                }
            }
            args.writeAtom("ALL");
        }
        final Response[] r = this.command("SORT", args);
        final Response response = r[r.length - 1];
        int[] matches = null;
        if (response.isOK()) {
            final Vector v = new Vector();
            for (int j = 0, len = r.length; j < len; ++j) {
                if (r[j] instanceof IMAPResponse) {
                    final IMAPResponse ir = (IMAPResponse)r[j];
                    if (ir.keyEquals("SORT")) {
                        int num;
                        while ((num = ir.readNumber()) != -1) {
                            v.addElement(new Integer(num));
                        }
                        r[j] = null;
                    }
                }
            }
            final int vsize = v.size();
            matches = new int[vsize];
            for (int k = 0; k < vsize; ++k) {
                matches[k] = v.elementAt(k);
            }
        }
        this.notifyResponseHandlers(r);
        this.handleResult(response);
        return matches;
    }
    
    public Namespaces namespace() throws ProtocolException {
        if (!this.hasCapability("NAMESPACE")) {
            throw new BadCommandException("NAMESPACE not supported");
        }
        final Response[] r = this.command("NAMESPACE", null);
        Namespaces namespace = null;
        final Response response = r[r.length - 1];
        if (response.isOK()) {
            for (int i = 0, len = r.length; i < len; ++i) {
                if (r[i] instanceof IMAPResponse) {
                    final IMAPResponse ir = (IMAPResponse)r[i];
                    if (ir.keyEquals("NAMESPACE")) {
                        if (namespace == null) {
                            namespace = new Namespaces(ir);
                        }
                        r[i] = null;
                    }
                }
            }
        }
        this.notifyResponseHandlers(r);
        this.handleResult(response);
        return namespace;
    }
    
    public Quota[] getQuotaRoot(String mbox) throws ProtocolException {
        if (!this.hasCapability("QUOTA")) {
            throw new BadCommandException("GETQUOTAROOT not supported");
        }
        mbox = BASE64MailboxEncoder.encode(mbox);
        final Argument args = new Argument();
        args.writeString(mbox);
        final Response[] r = this.command("GETQUOTAROOT", args);
        final Response response = r[r.length - 1];
        final Hashtable tab = new Hashtable();
        if (response.isOK()) {
            for (int i = 0, len = r.length; i < len; ++i) {
                if (r[i] instanceof IMAPResponse) {
                    final IMAPResponse ir = (IMAPResponse)r[i];
                    if (ir.keyEquals("QUOTAROOT")) {
                        ir.readAtomString();
                        String root = null;
                        while ((root = ir.readAtomString()) != null && root.length() > 0) {
                            tab.put(root, new Quota(root));
                        }
                        r[i] = null;
                    }
                    else if (ir.keyEquals("QUOTA")) {
                        final Quota quota = this.parseQuota(ir);
                        final Quota q = tab.get(quota.quotaRoot);
                        if (q == null || q.resources != null) {}
                        tab.put(quota.quotaRoot, quota);
                        r[i] = null;
                    }
                }
            }
        }
        this.notifyResponseHandlers(r);
        this.handleResult(response);
        final Quota[] qa = new Quota[tab.size()];
        final Enumeration e = tab.elements();
        int j = 0;
        while (e.hasMoreElements()) {
            qa[j] = e.nextElement();
            ++j;
        }
        return qa;
    }
    
    public Quota[] getQuota(final String root) throws ProtocolException {
        if (!this.hasCapability("QUOTA")) {
            throw new BadCommandException("QUOTA not supported");
        }
        final Argument args = new Argument();
        args.writeString(root);
        final Response[] r = this.command("GETQUOTA", args);
        Quota quota = null;
        final Vector v = new Vector();
        final Response response = r[r.length - 1];
        if (response.isOK()) {
            for (int i = 0, len = r.length; i < len; ++i) {
                if (r[i] instanceof IMAPResponse) {
                    final IMAPResponse ir = (IMAPResponse)r[i];
                    if (ir.keyEquals("QUOTA")) {
                        quota = this.parseQuota(ir);
                        v.addElement(quota);
                        r[i] = null;
                    }
                }
            }
        }
        this.notifyResponseHandlers(r);
        this.handleResult(response);
        final Quota[] qa = new Quota[v.size()];
        v.copyInto(qa);
        return qa;
    }
    
    public void setQuota(final Quota quota) throws ProtocolException {
        if (!this.hasCapability("QUOTA")) {
            throw new BadCommandException("QUOTA not supported");
        }
        final Argument args = new Argument();
        args.writeString(quota.quotaRoot);
        final Argument qargs = new Argument();
        if (quota.resources != null) {
            for (int i = 0; i < quota.resources.length; ++i) {
                qargs.writeAtom(quota.resources[i].name);
                qargs.writeNumber(quota.resources[i].limit);
            }
        }
        args.writeArgument(qargs);
        final Response[] r = this.command("SETQUOTA", args);
        final Response response = r[r.length - 1];
        this.notifyResponseHandlers(r);
        this.handleResult(response);
    }
    
    private Quota parseQuota(final Response r) throws ParsingException {
        final String quotaRoot = r.readAtomString();
        final Quota q = new Quota(quotaRoot);
        r.skipSpaces();
        if (r.readByte() != 40) {
            throw new ParsingException("parse error in QUOTA");
        }
        final Vector v = new Vector();
        while (r.peekByte() != 41) {
            final String name = r.readAtom();
            if (name != null) {
                final long usage = r.readLong();
                final long limit = r.readLong();
                final Quota.Resource res = new Quota.Resource(name, usage, limit);
                v.addElement(res);
            }
        }
        r.readByte();
        v.copyInto(q.resources = new Quota.Resource[v.size()]);
        return q;
    }
    
    public void setACL(String mbox, final char modifier, final ACL acl) throws ProtocolException {
        if (!this.hasCapability("ACL")) {
            throw new BadCommandException("ACL not supported");
        }
        mbox = BASE64MailboxEncoder.encode(mbox);
        final Argument args = new Argument();
        args.writeString(mbox);
        args.writeString(acl.getName());
        String rights = acl.getRights().toString();
        if (modifier == '+' || modifier == '-') {
            rights = modifier + rights;
        }
        args.writeString(rights);
        final Response[] r = this.command("SETACL", args);
        final Response response = r[r.length - 1];
        this.notifyResponseHandlers(r);
        this.handleResult(response);
    }
    
    public void deleteACL(String mbox, final String user) throws ProtocolException {
        if (!this.hasCapability("ACL")) {
            throw new BadCommandException("ACL not supported");
        }
        mbox = BASE64MailboxEncoder.encode(mbox);
        final Argument args = new Argument();
        args.writeString(mbox);
        args.writeString(user);
        final Response[] r = this.command("DELETEACL", args);
        final Response response = r[r.length - 1];
        this.notifyResponseHandlers(r);
        this.handleResult(response);
    }
    
    public ACL[] getACL(String mbox) throws ProtocolException {
        if (!this.hasCapability("ACL")) {
            throw new BadCommandException("ACL not supported");
        }
        mbox = BASE64MailboxEncoder.encode(mbox);
        final Argument args = new Argument();
        args.writeString(mbox);
        final Response[] r = this.command("GETACL", args);
        final Response response = r[r.length - 1];
        final Vector v = new Vector();
        if (response.isOK()) {
            for (int i = 0, len = r.length; i < len; ++i) {
                if (r[i] instanceof IMAPResponse) {
                    final IMAPResponse ir = (IMAPResponse)r[i];
                    if (ir.keyEquals("ACL")) {
                        ir.readAtomString();
                        String name = null;
                        while ((name = ir.readAtomString()) != null) {
                            final String rights = ir.readAtomString();
                            if (rights == null) {
                                break;
                            }
                            final ACL acl = new ACL(name, new Rights(rights));
                            v.addElement(acl);
                        }
                        r[i] = null;
                    }
                }
            }
        }
        this.notifyResponseHandlers(r);
        this.handleResult(response);
        final ACL[] aa = new ACL[v.size()];
        v.copyInto(aa);
        return aa;
    }
    
    public Rights[] listRights(String mbox, final String user) throws ProtocolException {
        if (!this.hasCapability("ACL")) {
            throw new BadCommandException("ACL not supported");
        }
        mbox = BASE64MailboxEncoder.encode(mbox);
        final Argument args = new Argument();
        args.writeString(mbox);
        args.writeString(user);
        final Response[] r = this.command("LISTRIGHTS", args);
        final Response response = r[r.length - 1];
        final Vector v = new Vector();
        if (response.isOK()) {
            for (int i = 0, len = r.length; i < len; ++i) {
                if (r[i] instanceof IMAPResponse) {
                    final IMAPResponse ir = (IMAPResponse)r[i];
                    if (ir.keyEquals("LISTRIGHTS")) {
                        ir.readAtomString();
                        ir.readAtomString();
                        String rights;
                        while ((rights = ir.readAtomString()) != null) {
                            v.addElement(new Rights(rights));
                        }
                        r[i] = null;
                    }
                }
            }
        }
        this.notifyResponseHandlers(r);
        this.handleResult(response);
        final Rights[] ra = new Rights[v.size()];
        v.copyInto(ra);
        return ra;
    }
    
    public Rights myRights(String mbox) throws ProtocolException {
        if (!this.hasCapability("ACL")) {
            throw new BadCommandException("ACL not supported");
        }
        mbox = BASE64MailboxEncoder.encode(mbox);
        final Argument args = new Argument();
        args.writeString(mbox);
        final Response[] r = this.command("MYRIGHTS", args);
        final Response response = r[r.length - 1];
        Rights rights = null;
        if (response.isOK()) {
            for (int i = 0, len = r.length; i < len; ++i) {
                if (r[i] instanceof IMAPResponse) {
                    final IMAPResponse ir = (IMAPResponse)r[i];
                    if (ir.keyEquals("MYRIGHTS")) {
                        ir.readAtomString();
                        final String rs = ir.readAtomString();
                        if (rights == null) {
                            rights = new Rights(rs);
                        }
                        r[i] = null;
                    }
                }
            }
        }
        this.notifyResponseHandlers(r);
        this.handleResult(response);
        return rights;
    }
    
    public synchronized void idleStart() throws ProtocolException {
        if (!this.hasCapability("IDLE")) {
            throw new BadCommandException("IDLE not supported");
        }
        final Vector v = new Vector();
        boolean done = false;
        Response r = null;
        try {
            this.idleTag = this.writeCommand("IDLE", null);
        }
        catch (LiteralException lex) {
            v.addElement(lex.getResponse());
            done = true;
        }
        catch (Exception ex) {
            v.addElement(Response.byeResponse(ex));
            done = true;
        }
        while (!done) {
            try {
                r = this.readResponse();
            }
            catch (IOException ioex) {
                r = Response.byeResponse(ioex);
            }
            catch (ProtocolException pex) {
                continue;
            }
            v.addElement(r);
            if (r.isContinuation() || r.isBYE()) {
                done = true;
            }
        }
        final Response[] responses = new Response[v.size()];
        v.copyInto(responses);
        r = responses[responses.length - 1];
        this.notifyResponseHandlers(responses);
        if (!r.isContinuation()) {
            this.handleResult(r);
        }
    }
    
    public synchronized Response readIdleResponse() {
        if (this.idleTag == null) {
            return null;
        }
        Response r = null;
        while (r == null) {
            try {
                r = this.readResponse();
            }
            catch (InterruptedIOException iioex) {
                if (iioex.bytesTransferred == 0) {
                    r = null;
                }
                else {
                    r = Response.byeResponse(iioex);
                }
            }
            catch (IOException ioex) {
                r = Response.byeResponse(ioex);
            }
            catch (ProtocolException pex) {
                r = Response.byeResponse(pex);
            }
        }
        return r;
    }
    
    public boolean processIdleResponse(final Response r) throws ProtocolException {
        final Response[] responses = { r };
        boolean done = false;
        this.notifyResponseHandlers(responses);
        if (r.isBYE()) {
            done = true;
        }
        if (r.isTagged() && r.getTag().equals(this.idleTag)) {
            done = true;
        }
        if (done) {
            this.idleTag = null;
        }
        this.handleResult(r);
        return !done;
    }
    
    public void idleAbort() throws ProtocolException {
        final OutputStream os = this.getOutputStream();
        try {
            os.write(IMAPProtocol.DONE);
            os.flush();
        }
        catch (IOException ex) {}
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
        CRLF = new byte[] { 13, 10 };
        fetchItems = new FetchItem[0];
        DONE = new byte[] { 68, 79, 78, 69, 13, 10 };
    }
}
