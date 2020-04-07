// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.mail.util.logging;

import java.util.Hashtable;
import javax.mail.PasswordAuthentication;
import java.security.PrivilegedAction;
import java.security.AccessController;
import java.io.Writer;
import java.io.PrintWriter;
import java.io.OutputStreamWriter;
import java.util.ResourceBundle;
import java.util.Date;
import javax.mail.internet.AddressException;
import javax.mail.Address;
import java.net.UnknownHostException;
import java.net.InetAddress;
import javax.mail.SendFailedException;
import com.sun.mail.smtp.SMTPTransport;
import java.nio.charset.Charset;
import javax.mail.internet.InternetAddress;
import java.util.Locale;
import javax.mail.Multipart;
import javax.mail.BodyPart;
import javax.mail.internet.MimeMultipart;
import javax.mail.Part;
import javax.mail.internet.MimePart;
import javax.mail.internet.MimeMessage;
import javax.mail.Transport;
import java.util.logging.ErrorManager;
import java.io.UnsupportedEncodingException;
import java.util.logging.LogManager;
import java.util.Arrays;
import java.lang.reflect.Array;
import javax.mail.internet.ContentType;
import javax.activation.DataSource;
import javax.activation.DataHandler;
import javax.mail.util.ByteArrayDataSource;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeUtility;
import javax.mail.MessagingException;
import java.io.OutputStream;
import java.io.ByteArrayOutputStream;
import javax.mail.Message;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLConnection;
import java.io.ByteArrayInputStream;
import java.util.logging.SimpleFormatter;
import javax.mail.MessageContext;
import javax.activation.FileTypeMap;
import java.util.logging.Level;
import java.util.Comparator;
import java.util.logging.LogRecord;
import javax.mail.Session;
import javax.mail.Authenticator;
import java.util.Properties;
import java.lang.reflect.Method;
import java.util.logging.Formatter;
import java.util.logging.Filter;
import java.util.logging.Handler;

public class MailHandler extends Handler
{
    private static final Filter[] EMPTY_FILTERS;
    private static final Formatter[] EMPTY_FORMATTERS;
    private static final int MIN_HEADER_SIZE = 1024;
    private static final int offValue;
    private static final GetAndSetContext GET_AND_SET_CCL;
    private static final ThreadLocal MUTEX;
    private static final Object MUTEX_PUBLISH;
    private static final Object MUTEX_REPORT;
    private static final Method REMOVE;
    private volatile boolean sealed;
    private boolean isWriting;
    private Properties mailProps;
    private Authenticator auth;
    private Session session;
    private LogRecord[] data;
    private int size;
    private int capacity;
    private Comparator comparator;
    private Formatter subjectFormatter;
    private Level pushLevel;
    private Filter pushFilter;
    private Filter[] attachmentFilters;
    private Formatter[] attachmentFormatters;
    private Formatter[] attachmentNames;
    private FileTypeMap contentTypes;
    static /* synthetic */ Class class$com$sun$mail$util$logging$MailHandler;
    static /* synthetic */ Class class$java$lang$ThreadLocal;
    static /* synthetic */ Class array$Ljava$util$logging$Filter;
    static /* synthetic */ Class array$Ljava$util$logging$Formatter;
    static /* synthetic */ Class class$java$lang$Throwable;
    static /* synthetic */ Class class$java$lang$String;
    
    public MailHandler() {
        this.init(true);
        this.sealed = true;
    }
    
    public MailHandler(final int capacity) {
        this.init(true);
        this.sealed = true;
        this.setCapacity0(capacity);
    }
    
    public MailHandler(final Properties props) {
        this.init(false);
        this.sealed = true;
        this.setMailProperties0(props);
    }
    
    public boolean isLoggable(final LogRecord record) {
        final int levelValue = this.getLevel().intValue();
        if (record.getLevel().intValue() < levelValue || levelValue == MailHandler.offValue) {
            return false;
        }
        final Filter body = this.getFilter();
        return body == null || body.isLoggable(record) || this.isAttachmentLoggable(record);
    }
    
    public void publish(final LogRecord record) {
        if (this.tryMutex()) {
            try {
                if (this.isLoggable(record)) {
                    record.getSourceMethodName();
                    this.publish0(record);
                }
            }
            finally {
                this.releaseMutex();
            }
        }
        else {
            this.reportUnPublishedError(record);
        }
    }
    
    private void publish0(final LogRecord record) {
        boolean priority;
        MessageContext ctx;
        synchronized (this) {
            if (this.size == this.data.length && this.size < this.capacity) {
                this.grow();
            }
            if (this.size < this.data.length) {
                this.data[this.size] = record;
                ++this.size;
                priority = this.isPushable(record);
                if (priority || this.size >= this.capacity) {
                    ctx = this.writeLogRecords(1);
                }
                else {
                    ctx = null;
                }
            }
            else {
                priority = false;
                ctx = null;
            }
        }
        if (ctx != null) {
            this.send(ctx, priority, 1);
        }
    }
    
    private void reportUnPublishedError(final LogRecord record) {
        if (MailHandler.MUTEX_PUBLISH.equals(MailHandler.MUTEX.get())) {
            MailHandler.MUTEX.set(MailHandler.MUTEX_REPORT);
            try {
                String msg;
                if (record != null) {
                    final SimpleFormatter f = new SimpleFormatter();
                    msg = "Log record " + record.getSequenceNumber() + " was not published. " + this.head(f) + this.format(f, record) + this.tail(f, "");
                }
                else {
                    msg = null;
                }
                final Exception e = new IllegalStateException("Recursive publish detected by thread " + Thread.currentThread());
                this.reportError(msg, e, 1);
            }
            finally {
                MailHandler.MUTEX.set(MailHandler.MUTEX_PUBLISH);
            }
        }
    }
    
    private boolean tryMutex() {
        if (MailHandler.MUTEX.get() == null) {
            MailHandler.MUTEX.set(MailHandler.MUTEX_PUBLISH);
            return true;
        }
        return false;
    }
    
    private void releaseMutex() {
        if (MailHandler.REMOVE != null) {
            try {
                MailHandler.REMOVE.invoke(MailHandler.MUTEX, (Object[])null);
            }
            catch (RuntimeException ignore) {
                MailHandler.MUTEX.set(null);
            }
            catch (Exception ignore2) {
                MailHandler.MUTEX.set(null);
            }
        }
        else {
            MailHandler.MUTEX.set(null);
        }
    }
    
    public void push() {
        this.push(true, 2);
    }
    
    public void flush() {
        this.push(false, 2);
    }
    
    public void close() {
        final Object ccl = this.getAndSetContextClassLoader();
        try {
            MessageContext ctx = null;
            synchronized (this) {
                super.setLevel(Level.OFF);
                try {
                    ctx = this.writeLogRecords(3);
                }
                finally {
                    if (this.capacity > 0) {
                        this.capacity = -this.capacity;
                    }
                    if (this.size == 0 && this.data.length != 1) {
                        this.data = new LogRecord[1];
                    }
                }
            }
            if (ctx != null) {
                this.send(ctx, false, 3);
            }
        }
        finally {
            this.setContextClassLoader(ccl);
        }
    }
    
    public synchronized void setLevel(final Level newLevel) {
        if (this.capacity > 0) {
            super.setLevel(newLevel);
        }
        else {
            if (newLevel == null) {
                throw new NullPointerException();
            }
            this.checkAccess();
        }
    }
    
    public final synchronized Level getPushLevel() {
        return this.pushLevel;
    }
    
    public final synchronized void setPushLevel(final Level level) {
        this.checkAccess();
        if (level == null) {
            throw new NullPointerException();
        }
        if (this.isWriting) {
            throw new IllegalStateException();
        }
        this.pushLevel = level;
    }
    
    public final synchronized Filter getPushFilter() {
        return this.pushFilter;
    }
    
    public final synchronized void setPushFilter(final Filter filter) {
        this.checkAccess();
        if (this.isWriting) {
            throw new IllegalStateException();
        }
        this.pushFilter = filter;
    }
    
    public final synchronized Comparator getComparator() {
        return this.comparator;
    }
    
    public final synchronized void setComparator(final Comparator c) {
        this.checkAccess();
        if (this.isWriting) {
            throw new IllegalStateException();
        }
        this.comparator = c;
    }
    
    public final synchronized int getCapacity() {
        assert this.capacity != Integer.MIN_VALUE && this.capacity != 0 : this.capacity;
        return Math.abs(this.capacity);
    }
    
    public final synchronized Authenticator getAuthenticator() {
        this.checkAccess();
        return this.auth;
    }
    
    public final void setAuthenticator(final Authenticator auth) {
        this.setAuthenticator0(auth);
    }
    
    public final void setAuthenticator(final char[] password) {
        if (password == null) {
            this.setAuthenticator0(null);
        }
        else {
            this.setAuthenticator0(new DefaultAuthenticator(new String(password)));
        }
    }
    
    private void setAuthenticator0(final Authenticator auth) {
        this.checkAccess();
        final Session settings;
        synchronized (this) {
            if (this.isWriting) {
                throw new IllegalStateException();
            }
            this.auth = auth;
            settings = this.fixUpSession();
        }
        this.verifySettings(settings);
    }
    
    public final void setMailProperties(final Properties props) {
        this.setMailProperties0(props);
    }
    
    private void setMailProperties0(Properties props) {
        this.checkAccess();
        props = (Properties)props.clone();
        final Session settings;
        synchronized (this) {
            if (this.isWriting) {
                throw new IllegalStateException();
            }
            this.mailProps = props;
            settings = this.fixUpSession();
        }
        this.verifySettings(settings);
    }
    
    public final Properties getMailProperties() {
        this.checkAccess();
        final Properties props;
        synchronized (this) {
            props = this.mailProps;
        }
        return (Properties)props.clone();
    }
    
    public final Filter[] getAttachmentFilters() {
        return this.readOnlyAttachmentFilters().clone();
    }
    
    public final void setAttachmentFilters(Filter[] filters) {
        this.checkAccess();
        filters = (Filter[])copyOf(filters, filters.length, (MailHandler.array$Ljava$util$logging$Filter == null) ? (MailHandler.array$Ljava$util$logging$Filter = class$("[Ljava.util.logging.Filter;")) : MailHandler.array$Ljava$util$logging$Filter);
        synchronized (this) {
            if (this.attachmentFormatters.length != filters.length) {
                throw attachmentMismatch(this.attachmentFormatters.length, filters.length);
            }
            if (this.isWriting) {
                throw new IllegalStateException();
            }
            this.attachmentFilters = filters;
        }
    }
    
    public final Formatter[] getAttachmentFormatters() {
        final Formatter[] formatters;
        synchronized (this) {
            formatters = this.attachmentFormatters;
        }
        return formatters.clone();
    }
    
    public final void setAttachmentFormatters(Formatter[] formatters) {
        this.checkAccess();
        if (formatters.length == 0) {
            formatters = emptyFormatterArray();
        }
        else {
            formatters = (Formatter[])copyOf(formatters, formatters.length, (MailHandler.array$Ljava$util$logging$Formatter == null) ? (MailHandler.array$Ljava$util$logging$Formatter = class$("[Ljava.util.logging.Formatter;")) : MailHandler.array$Ljava$util$logging$Formatter);
            for (int i = 0; i < formatters.length; ++i) {
                if (formatters[i] == null) {
                    throw new NullPointerException(atIndexMsg(i));
                }
            }
        }
        synchronized (this) {
            if (this.isWriting) {
                throw new IllegalStateException();
            }
            this.attachmentFormatters = formatters;
            this.fixUpAttachmentFilters();
            this.fixUpAttachmentNames();
        }
    }
    
    public final Formatter[] getAttachmentNames() {
        final Formatter[] formatters;
        synchronized (this) {
            formatters = this.attachmentNames;
        }
        return formatters.clone();
    }
    
    public final void setAttachmentNames(final String[] names) {
        this.checkAccess();
        Formatter[] formatters;
        if (names.length == 0) {
            formatters = emptyFormatterArray();
        }
        else {
            formatters = new Formatter[names.length];
        }
        for (int i = 0; i < names.length; ++i) {
            final String name = names[i];
            if (name == null) {
                throw new NullPointerException(atIndexMsg(i));
            }
            if (name.length() <= 0) {
                throw new IllegalArgumentException(atIndexMsg(i));
            }
            formatters[i] = new TailNameFormatter(name);
        }
        synchronized (this) {
            if (this.attachmentFormatters.length != names.length) {
                throw attachmentMismatch(this.attachmentFormatters.length, names.length);
            }
            if (this.isWriting) {
                throw new IllegalStateException();
            }
            this.attachmentNames = formatters;
        }
    }
    
    public final void setAttachmentNames(Formatter[] formatters) {
        this.checkAccess();
        formatters = (Formatter[])copyOf(formatters, formatters.length, (MailHandler.array$Ljava$util$logging$Formatter == null) ? (MailHandler.array$Ljava$util$logging$Formatter = class$("[Ljava.util.logging.Formatter;")) : MailHandler.array$Ljava$util$logging$Formatter);
        for (int i = 0; i < formatters.length; ++i) {
            if (formatters[i] == null) {
                throw new NullPointerException(atIndexMsg(i));
            }
        }
        synchronized (this) {
            if (this.attachmentFormatters.length != formatters.length) {
                throw attachmentMismatch(this.attachmentFormatters.length, formatters.length);
            }
            if (this.isWriting) {
                throw new IllegalStateException();
            }
            this.attachmentNames = formatters;
        }
    }
    
    public final synchronized Formatter getSubject() {
        return this.subjectFormatter;
    }
    
    public final void setSubject(final String subject) {
        if (subject != null) {
            this.setSubject(new TailNameFormatter(subject));
            return;
        }
        this.checkAccess();
        throw new NullPointerException();
    }
    
    public final void setSubject(final Formatter format) {
        this.checkAccess();
        if (format == null) {
            throw new NullPointerException();
        }
        synchronized (this) {
            if (this.isWriting) {
                throw new IllegalStateException();
            }
            this.subjectFormatter = format;
        }
    }
    
    protected void reportError(final String msg, final Exception ex, final int code) {
        if (msg != null) {
            super.reportError(Level.SEVERE.getName() + ": " + msg, ex, code);
        }
        else {
            super.reportError(null, ex, code);
        }
    }
    
    final void checkAccess() {
        if (this.sealed) {
            LogManagerProperties.getLogManager().checkAccess();
        }
    }
    
    final String contentTypeOf(String head) {
        if (head != null && head.length() > 0) {
            final int MAX_CHARS = 25;
            if (head.length() > 25) {
                head = head.substring(0, 25);
            }
            try {
                final String encoding = this.getEncodingName();
                final ByteArrayInputStream in = new ByteArrayInputStream(head.getBytes(encoding));
                assert in.markSupported() : in.getClass().getName();
                return URLConnection.guessContentTypeFromStream(in);
            }
            catch (IOException IOE) {
                this.reportError(IOE.getMessage(), IOE, 5);
            }
        }
        return null;
    }
    
    final boolean isMissingContent(final Message msg, Throwable t) {
        for (Throwable cause = t.getCause(); cause != null; cause = cause.getCause()) {
            t = cause;
        }
        try {
            msg.writeTo(new ByteArrayOutputStream(1024));
        }
        catch (RuntimeException RE) {
            throw RE;
        }
        catch (Exception noContent) {
            final String txt = noContent.getMessage();
            if (!isEmpty(txt) && noContent.getClass() == t.getClass()) {
                return txt.equals(t.getMessage());
            }
        }
        return false;
    }
    
    private void reportError(final Message msg, final Exception ex, final int code) {
        try {
            super.reportError(this.toRawString(msg), ex, code);
        }
        catch (MessagingException rawMe) {
            this.reportError(this.toMsgString(rawMe), ex, code);
        }
        catch (IOException rawIo) {
            this.reportError(this.toMsgString(rawIo), ex, code);
        }
    }
    
    private String getContentType(final String name) {
        assert Thread.holdsLock(this);
        final String type = this.contentTypes.getContentType(name);
        if ("application/octet-stream".equalsIgnoreCase(type)) {
            return null;
        }
        return type;
    }
    
    private String getEncodingName() {
        String encoding = this.getEncoding();
        if (encoding == null) {
            encoding = MimeUtility.getDefaultJavaCharset();
        }
        return encoding;
    }
    
    private void setContent(final MimeBodyPart part, final CharSequence buf, String type) throws MessagingException {
        final String encoding = this.getEncodingName();
        if (type != null && !"text/plain".equalsIgnoreCase(type)) {
            type = this.contentWithEncoding(type, encoding);
            try {
                final DataSource source = new ByteArrayDataSource(buf.toString(), type);
                part.setDataHandler(new DataHandler(source));
            }
            catch (IOException IOE) {
                this.reportError(IOE.getMessage(), IOE, 5);
                part.setText(buf.toString(), encoding);
            }
        }
        else {
            part.setText(buf.toString(), MimeUtility.mimeCharset(encoding));
        }
    }
    
    private String contentWithEncoding(String type, String encoding) {
        assert encoding != null;
        try {
            final ContentType ct = new ContentType(type);
            ct.setParameter("charset", MimeUtility.mimeCharset(encoding));
            encoding = ct.toString();
            if (!isEmpty(encoding)) {
                type = encoding;
            }
        }
        catch (MessagingException ME) {
            this.reportError(type, ME, 5);
        }
        return type;
    }
    
    private synchronized void setCapacity0(final int newCapacity) {
        if (newCapacity <= 0) {
            throw new IllegalArgumentException("Capacity must be greater than zero.");
        }
        if (this.isWriting) {
            throw new IllegalStateException();
        }
        if (this.capacity < 0) {
            this.capacity = -newCapacity;
        }
        else {
            this.capacity = newCapacity;
        }
    }
    
    private synchronized Filter[] readOnlyAttachmentFilters() {
        return this.attachmentFilters;
    }
    
    private static Formatter[] emptyFormatterArray() {
        return MailHandler.EMPTY_FORMATTERS;
    }
    
    private static Filter[] emptyFilterArray() {
        return MailHandler.EMPTY_FILTERS;
    }
    
    private boolean fixUpAttachmentNames() {
        assert Thread.holdsLock(this);
        boolean fixed = false;
        final int expect = this.attachmentFormatters.length;
        final int current = this.attachmentNames.length;
        if (current != expect) {
            this.attachmentNames = (Formatter[])copyOf(this.attachmentNames, expect);
            fixed = (current != 0);
        }
        if (expect == 0) {
            this.attachmentNames = emptyFormatterArray();
            assert this.attachmentNames.length == 0;
        }
        else {
            for (int i = 0; i < expect; ++i) {
                if (this.attachmentNames[i] == null) {
                    this.attachmentNames[i] = new TailNameFormatter(this.toString(this.attachmentFormatters[i]));
                }
            }
        }
        return fixed;
    }
    
    private boolean fixUpAttachmentFilters() {
        assert Thread.holdsLock(this);
        boolean fixed = false;
        final int expect = this.attachmentFormatters.length;
        final int current = this.attachmentFilters.length;
        if (current != expect) {
            this.attachmentFilters = (Filter[])copyOf(this.attachmentFilters, expect);
            fixed = (current != 0);
        }
        if (expect == 0) {
            this.attachmentFilters = emptyFilterArray();
            assert this.attachmentFilters.length == 0;
        }
        return fixed;
    }
    
    private static Object[] copyOf(final Object[] a, final int size) {
        final Object[] copy = (Object[])Array.newInstance(a.getClass().getComponentType(), size);
        System.arraycopy(a, 0, copy, 0, Math.min(a.length, size));
        return copy;
    }
    
    private static Object[] copyOf(final Object[] a, final int len, final Class type) {
        if (type == a.getClass()) {
            return a.clone();
        }
        final Object[] copy = (Object[])Array.newInstance(type.getComponentType(), len);
        System.arraycopy(a, 0, copy, 0, Math.min(len, a.length));
        return copy;
    }
    
    private void reset() {
        assert Thread.holdsLock(this);
        if (this.size < this.data.length) {
            Arrays.fill(this.data, 0, this.size, null);
        }
        else {
            Arrays.fill(this.data, null);
        }
        this.size = 0;
    }
    
    private void grow() {
        assert Thread.holdsLock(this);
        final int len = this.data.length;
        int newCapacity = len + (len >> 1) + 1;
        if (newCapacity > this.capacity || newCapacity < len) {
            newCapacity = this.capacity;
        }
        assert len != this.capacity : len;
        this.data = (LogRecord[])copyOf(this.data, newCapacity);
    }
    
    private synchronized void init(final boolean inherit) {
        final LogManager manager = LogManagerProperties.getLogManager();
        final String p = this.getClass().getName();
        this.mailProps = new Properties();
        this.contentTypes = FileTypeMap.getDefaultFileTypeMap();
        this.initErrorManager(manager, p);
        this.initLevel(manager, p);
        this.initFilter(manager, p);
        this.initCapacity(manager, p);
        this.initAuthenticator(manager, p);
        this.initEncoding(manager, p);
        this.initFormatter(manager, p);
        this.initComparator(manager, p);
        this.initPushLevel(manager, p);
        this.initPushFilter(manager, p);
        this.initSubject(manager, p);
        this.initAttachmentFormaters(manager, p);
        this.initAttachmentFilters(manager, p);
        this.initAttachmentNames(manager, p);
        if (inherit && manager.getProperty(p.concat(".verify")) != null) {
            this.verifySettings(this.initSession());
        }
    }
    
    private static boolean isEmpty(final String s) {
        return s == null || s.length() == 0;
    }
    
    private static boolean hasValue(final String name) {
        return !isEmpty(name) && !"null".equalsIgnoreCase(name);
    }
    
    private void initAttachmentFilters(final LogManager manager, final String p) {
        assert Thread.holdsLock(this);
        assert this.attachmentFormatters != null;
        final String list = manager.getProperty(p.concat(".attachment.filters"));
        if (list != null && list.length() > 0) {
            final String[] names = list.split(",");
            final Filter[] a = new Filter[names.length];
            for (int i = 0; i < a.length; ++i) {
                names[i] = names[i].trim();
                if (!"null".equalsIgnoreCase(names[i])) {
                    try {
                        a[i] = LogManagerProperties.newFilter(names[i]);
                    }
                    catch (SecurityException SE) {
                        throw SE;
                    }
                    catch (Exception E) {
                        this.reportError(E.getMessage(), E, 4);
                    }
                }
            }
            this.attachmentFilters = a;
            if (this.fixUpAttachmentFilters()) {
                this.reportError("Attachment filters.", attachmentMismatch("Length mismatch."), 4);
            }
        }
        else {
            this.attachmentFilters = emptyFilterArray();
            this.fixUpAttachmentFilters();
        }
    }
    
    private void initAttachmentFormaters(final LogManager manager, final String p) {
        assert Thread.holdsLock(this);
        final String list = manager.getProperty(p.concat(".attachment.formatters"));
        if (list != null && list.length() > 0) {
            final String[] names = list.split(",");
            Formatter[] a;
            if (names.length == 0) {
                a = emptyFormatterArray();
            }
            else {
                a = new Formatter[names.length];
            }
            for (int i = 0; i < a.length; ++i) {
                names[i] = names[i].trim();
                if (!"null".equalsIgnoreCase(names[i])) {
                    try {
                        a[i] = LogManagerProperties.newFormatter(names[i]);
                        if (a[i] instanceof TailNameFormatter) {
                            a[i] = new SimpleFormatter();
                            final Exception CNFE = new ClassNotFoundException(a[i].toString());
                            this.reportError("Attachment formatter.", CNFE, 4);
                        }
                        continue;
                    }
                    catch (SecurityException SE) {
                        throw SE;
                    }
                    catch (Exception E) {
                        a[i] = new SimpleFormatter();
                        this.reportError(E.getMessage(), E, 4);
                        continue;
                    }
                }
                a[i] = new SimpleFormatter();
                final Exception NPE = new NullPointerException(atIndexMsg(i));
                this.reportError("Attachment formatter.", NPE, 4);
            }
            this.attachmentFormatters = a;
        }
        else {
            this.attachmentFormatters = emptyFormatterArray();
        }
    }
    
    private void initAttachmentNames(final LogManager manager, final String p) {
        assert Thread.holdsLock(this);
        assert this.attachmentFormatters != null;
        final String list = manager.getProperty(p.concat(".attachment.names"));
        if (list != null && list.length() > 0) {
            final String[] names = list.split(",");
            final Formatter[] a = new Formatter[names.length];
            for (int i = 0; i < a.length; ++i) {
                names[i] = names[i].trim();
                if (!"null".equalsIgnoreCase(names[i])) {
                    try {
                        try {
                            a[i] = LogManagerProperties.newFormatter(names[i]);
                        }
                        catch (ClassNotFoundException literal) {
                            a[i] = new TailNameFormatter(names[i]);
                        }
                        catch (ClassCastException literal2) {
                            a[i] = new TailNameFormatter(names[i]);
                        }
                        continue;
                    }
                    catch (SecurityException SE) {
                        throw SE;
                    }
                    catch (Exception E) {
                        this.reportError(E.getMessage(), E, 4);
                        continue;
                    }
                }
                final Exception NPE = new NullPointerException(atIndexMsg(i));
                this.reportError("Attachment names.", NPE, 4);
            }
            this.attachmentNames = a;
            if (this.fixUpAttachmentNames()) {
                this.reportError("Attachment names.", attachmentMismatch("Length mismatch."), 4);
            }
        }
        else {
            this.attachmentNames = emptyFormatterArray();
            this.fixUpAttachmentNames();
        }
    }
    
    private void initAuthenticator(final LogManager manager, final String p) {
        assert Thread.holdsLock(this);
        final String name = manager.getProperty(p.concat(".authenticator"));
        if (hasValue(name)) {
            try {
                this.auth = LogManagerProperties.newAuthenticator(name);
            }
            catch (SecurityException SE) {
                throw SE;
            }
            catch (ClassNotFoundException literalAuth) {
                this.auth = new DefaultAuthenticator(name);
            }
            catch (ClassCastException literalAuth2) {
                this.auth = new DefaultAuthenticator(name);
            }
            catch (Exception E) {
                this.reportError(E.getMessage(), E, 4);
            }
        }
    }
    
    private void initLevel(final LogManager manager, final String p) {
        assert Thread.holdsLock(this);
        try {
            final String val = manager.getProperty(p.concat(".level"));
            if (val != null) {
                super.setLevel(Level.parse(val));
            }
            else {
                super.setLevel(Level.WARNING);
            }
        }
        catch (SecurityException SE) {
            throw SE;
        }
        catch (RuntimeException RE) {
            this.reportError(RE.getMessage(), RE, 4);
            try {
                super.setLevel(Level.WARNING);
            }
            catch (RuntimeException fail) {
                this.reportError(fail.getMessage(), fail, 4);
            }
        }
    }
    
    private void initFilter(final LogManager manager, final String p) {
        assert Thread.holdsLock(this);
        try {
            final String name = manager.getProperty(p.concat(".filter"));
            if (hasValue(name)) {
                super.setFilter(LogManagerProperties.newFilter(name));
            }
        }
        catch (SecurityException SE) {
            throw SE;
        }
        catch (Exception E) {
            this.reportError(E.getMessage(), E, 4);
        }
    }
    
    private void initCapacity(final LogManager manager, final String p) {
        assert Thread.holdsLock(this);
        final int DEFAULT_CAPACITY = 1000;
        try {
            final String value = manager.getProperty(p.concat(".capacity"));
            if (value != null) {
                this.setCapacity0(Integer.parseInt(value));
            }
            else {
                this.setCapacity0(1000);
            }
        }
        catch (RuntimeException RE) {
            this.reportError(RE.getMessage(), RE, 4);
        }
        if (this.capacity <= 0) {
            this.capacity = 1000;
        }
        this.data = new LogRecord[1];
    }
    
    private void initEncoding(final LogManager manager, final String p) {
        assert Thread.holdsLock(this);
        try {
            super.setEncoding(manager.getProperty(p.concat(".encoding")));
        }
        catch (SecurityException SE) {
            throw SE;
        }
        catch (UnsupportedEncodingException UEE) {
            this.reportError(UEE.getMessage(), UEE, 4);
        }
        catch (RuntimeException RE) {
            this.reportError(RE.getMessage(), RE, 4);
        }
    }
    
    private void initErrorManager(final LogManager manager, final String p) {
        assert Thread.holdsLock(this);
        final String name = manager.getProperty(p.concat(".errorManager"));
        if (name != null) {
            try {
                final ErrorManager em = LogManagerProperties.newErrorManager(name);
                super.setErrorManager(em);
            }
            catch (SecurityException SE) {
                throw SE;
            }
            catch (Exception E) {
                this.reportError(E.getMessage(), E, 4);
            }
        }
    }
    
    private void initFormatter(final LogManager manager, final String p) {
        assert Thread.holdsLock(this);
        final String name = manager.getProperty(p.concat(".formatter"));
        if (hasValue(name)) {
            try {
                final Formatter formatter = LogManagerProperties.newFormatter(name);
                assert formatter != null;
                if (!(formatter instanceof TailNameFormatter)) {
                    super.setFormatter(formatter);
                }
                else {
                    super.setFormatter(new SimpleFormatter());
                }
                return;
            }
            catch (SecurityException SE) {
                throw SE;
            }
            catch (Exception E) {
                this.reportError(E.getMessage(), E, 4);
                try {
                    super.setFormatter(new SimpleFormatter());
                }
                catch (RuntimeException fail) {
                    this.reportError(fail.getMessage(), fail, 4);
                }
                return;
            }
        }
        super.setFormatter(new SimpleFormatter());
    }
    
    private void initComparator(final LogManager manager, final String p) {
        assert Thread.holdsLock(this);
        final String name = manager.getProperty(p.concat(".comparator"));
        if (hasValue(name)) {
            try {
                this.comparator = LogManagerProperties.newComparator(name);
            }
            catch (SecurityException SE) {
                throw SE;
            }
            catch (Exception E) {
                this.reportError(E.getMessage(), E, 4);
            }
        }
    }
    
    private void initPushLevel(final LogManager manager, final String p) {
        assert Thread.holdsLock(this);
        try {
            final String val = manager.getProperty(p.concat(".pushLevel"));
            if (val != null) {
                this.pushLevel = Level.parse(val);
            }
        }
        catch (RuntimeException RE) {
            this.reportError(RE.getMessage(), RE, 4);
        }
        if (this.pushLevel == null) {
            this.pushLevel = Level.OFF;
        }
    }
    
    private void initPushFilter(final LogManager manager, final String p) {
        assert Thread.holdsLock(this);
        final String name = manager.getProperty(p.concat(".pushFilter"));
        if (hasValue(name)) {
            try {
                this.pushFilter = LogManagerProperties.newFilter(name);
            }
            catch (SecurityException SE) {
                throw SE;
            }
            catch (Exception E) {
                this.reportError(E.getMessage(), E, 4);
            }
        }
    }
    
    private void initSubject(final LogManager manager, final String p) {
        assert Thread.holdsLock(this);
        final String name = manager.getProperty(p.concat(".subject"));
        if (hasValue(name)) {
            try {
                this.subjectFormatter = LogManagerProperties.newFormatter(name);
            }
            catch (SecurityException SE) {
                throw SE;
            }
            catch (ClassNotFoundException literalSubject) {
                this.subjectFormatter = new TailNameFormatter(name);
            }
            catch (ClassCastException literalSubject2) {
                this.subjectFormatter = new TailNameFormatter(name);
            }
            catch (Exception E) {
                this.subjectFormatter = new TailNameFormatter(name);
                this.reportError(E.getMessage(), E, 4);
            }
        }
        if (this.subjectFormatter == null) {
            this.subjectFormatter = new TailNameFormatter("");
        }
    }
    
    private boolean isAttachmentLoggable(final LogRecord record) {
        final Filter[] filters = this.readOnlyAttachmentFilters();
        for (int i = 0; i < filters.length; ++i) {
            final Filter f = filters[i];
            if (f == null || f.isLoggable(record)) {
                return true;
            }
        }
        return false;
    }
    
    private boolean isPushable(final LogRecord record) {
        assert Thread.holdsLock(this);
        final int value = this.getPushLevel().intValue();
        if (value == MailHandler.offValue || record.getLevel().intValue() < value) {
            return false;
        }
        final Filter filter = this.getPushFilter();
        return filter == null || filter.isLoggable(record);
    }
    
    private void push(final boolean priority, final int code) {
        if (this.tryMutex()) {
            try {
                final MessageContext ctx = this.writeLogRecords(code);
                if (ctx != null) {
                    this.send(ctx, priority, code);
                }
            }
            finally {
                this.releaseMutex();
            }
        }
        else {
            this.reportUnPublishedError(null);
        }
    }
    
    private void send(final MessageContext ctx, final boolean priority, final int code) {
        final Message msg = ctx.getMessage();
        try {
            this.envelopeFor(ctx, priority);
            Transport.send(msg);
        }
        catch (Exception E) {
            this.reportError(msg, E, code);
        }
    }
    
    private void sort() {
        assert Thread.holdsLock(this);
        if (this.comparator != null) {
            try {
                if (this.size != 1) {
                    Arrays.sort(this.data, 0, this.size, this.comparator);
                }
                else {
                    this.comparator.compare(this.data[0], this.data[0]);
                }
            }
            catch (RuntimeException RE) {
                this.reportError(RE.getMessage(), RE, 5);
            }
        }
    }
    
    private synchronized MessageContext writeLogRecords(final int code) {
        if (this.size == 0 || this.isWriting) {
            return null;
        }
        this.isWriting = true;
        try {
            this.sort();
            if (this.session == null) {
                this.initSession();
            }
            final MimeMessage msg = new MimeMessage(this.session);
            msg.setDescription(this.descriptionFrom(this.comparator, this.pushLevel, this.pushFilter));
            MimeBodyPart[] parts = new MimeBodyPart[this.attachmentFormatters.length];
            StringBuffer[] buffers = new StringBuffer[parts.length];
            String contentType = null;
            StringBuffer buf = null;
            this.appendSubject(msg, this.head(this.subjectFormatter));
            final MimeBodyPart body = this.createBodyPart();
            final Formatter bodyFormat = this.getFormatter();
            final Filter bodyFilter = this.getFilter();
            Locale lastLocale = null;
            for (int ix = 0; ix < this.size; ++ix) {
                boolean formatted = false;
                final LogRecord r = this.data[ix];
                this.data[ix] = null;
                final Locale locale = this.localeFor(r);
                this.appendSubject(msg, this.format(this.subjectFormatter, r));
                if (bodyFilter == null || bodyFilter.isLoggable(r)) {
                    if (buf == null) {
                        buf = new StringBuffer();
                        final String head = this.head(bodyFormat);
                        buf.append(head);
                        contentType = this.contentTypeOf(head);
                    }
                    formatted = true;
                    buf.append(this.format(bodyFormat, r));
                    if (locale != null && !locale.equals(lastLocale)) {
                        this.appendContentLang(body, locale);
                    }
                }
                for (int i = 0; i < parts.length; ++i) {
                    final Filter af = this.attachmentFilters[i];
                    if (af == null || af.isLoggable(r)) {
                        if (parts[i] == null) {
                            parts[i] = this.createBodyPart(i);
                            (buffers[i] = new StringBuffer()).append(this.head(this.attachmentFormatters[i]));
                            this.appendFileName(parts[i], this.head(this.attachmentNames[i]));
                        }
                        formatted = true;
                        this.appendFileName(parts[i], this.format(this.attachmentNames[i], r));
                        buffers[i].append(this.format(this.attachmentFormatters[i], r));
                        if (locale != null && !locale.equals(lastLocale)) {
                            this.appendContentLang(parts[i], locale);
                        }
                    }
                }
                if (formatted) {
                    if (locale != null && !locale.equals(lastLocale)) {
                        this.appendContentLang(msg, locale);
                    }
                }
                else {
                    this.reportFilterError(r);
                }
                lastLocale = locale;
            }
            this.size = 0;
            for (int j = parts.length - 1; j >= 0; --j) {
                if (parts[j] != null) {
                    this.appendFileName(parts[j], this.tail(this.attachmentNames[j], "err"));
                    buffers[j].append(this.tail(this.attachmentFormatters[j], ""));
                    if (buffers[j].length() > 0) {
                        String name = parts[j].getFileName();
                        if (isEmpty(name)) {
                            name = this.toString(this.attachmentFormatters[j]);
                            parts[j].setFileName(name);
                        }
                        this.setContent(parts[j], buffers[j], this.getContentType(name));
                    }
                    else {
                        this.setIncompleteCopy(msg);
                        parts[j] = null;
                    }
                    buffers[j] = null;
                }
            }
            buffers = null;
            if (buf != null) {
                buf.append(this.tail(bodyFormat, ""));
            }
            else {
                buf = new StringBuffer(0);
            }
            this.appendSubject(msg, this.tail(this.subjectFormatter, ""));
            final MimeMultipart multipart = new MimeMultipart();
            final String altType = this.getContentType(bodyFormat.getClass().getName());
            this.setContent(body, buf, (altType == null) ? contentType : altType);
            buf = null;
            multipart.addBodyPart(body);
            for (int k = 0; k < parts.length; ++k) {
                if (parts[k] != null) {
                    multipart.addBodyPart(parts[k]);
                }
            }
            parts = null;
            msg.setContent(multipart);
            return new MessageContext(msg);
        }
        catch (RuntimeException re) {
            this.reportError(re.getMessage(), re, code);
        }
        catch (Exception e) {
            this.reportError(e.getMessage(), e, code);
        }
        finally {
            this.isWriting = false;
            if (this.size > 0) {
                this.reset();
            }
        }
        return null;
    }
    
    private void verifySettings(final Session session) {
        if (session != null) {
            final Properties props = session.getProperties();
            final Object check = ((Hashtable<String, String>)props).put("verify", "");
            if (check instanceof String) {
                final String value = (String)check;
                if (hasValue(value)) {
                    this.verifySettings0(session, value);
                }
            }
            else if (check != null) {
                this.verifySettings0(session, check.getClass().toString());
            }
        }
    }
    
    private void verifySettings0(final Session session, final String verify) {
        assert verify != null : null;
        if (!"local".equals(verify) && !"remote".equals(verify)) {
            this.reportError("Verify must be 'local' or 'remote'.", new IllegalArgumentException(verify), 4);
            return;
        }
        final String msg = "Local address is " + InternetAddress.getLocalAddress(session) + '.';
        try {
            Charset.forName(this.getEncodingName());
        }
        catch (RuntimeException RE) {
            final IOException UEE = new UnsupportedEncodingException(RE.toString());
            UEE.initCause(RE);
            this.reportError(msg, UEE, 5);
        }
        final MimeMessage abort = new MimeMessage(session);
        synchronized (this) {
            this.appendSubject(abort, this.head(this.subjectFormatter));
            this.appendSubject(abort, this.tail(this.subjectFormatter, ""));
        }
        this.setIncompleteCopy(abort);
        this.envelopeFor(new MessageContext(abort), true);
        try {
            abort.saveChanges();
        }
        catch (MessagingException ME) {
            this.reportError(msg, ME, 5);
        }
        try {
            Address[] all = abort.getAllRecipients();
            if (all == null) {
                all = new InternetAddress[0];
            }
            Transport t;
            try {
                final Address[] any = (all.length != 0) ? all : abort.getFrom();
                if (any == null || any.length == 0) {
                    final MessagingException me = new MessagingException("No recipient or from address.");
                    this.reportError(msg, me, 4);
                    throw me;
                }
                t = session.getTransport(any[0]);
                session.getProperty("mail.transport.protocol");
            }
            catch (MessagingException protocol) {
                try {
                    t = session.getTransport();
                }
                catch (MessagingException fail) {
                    throw attach(protocol, fail);
                }
            }
            String host = null;
            if ("remote".equals(verify)) {
                MessagingException closed = null;
                t.connect();
                try {
                    try {
                        if (t instanceof SMTPTransport) {
                            host = ((SMTPTransport)t).getLocalHost();
                        }
                        t.sendMessage(abort, all);
                    }
                    finally {
                        try {
                            t.close();
                        }
                        catch (MessagingException ME2) {
                            closed = ME2;
                        }
                    }
                    this.reportUnexpectedSend(abort, verify, null);
                }
                catch (SendFailedException sfe) {
                    Address[] recip = sfe.getInvalidAddresses();
                    if (recip != null && recip.length != 0) {
                        this.fixUpContent(abort, verify, sfe);
                        this.reportError(abort, sfe, 4);
                    }
                    recip = sfe.getValidSentAddresses();
                    if (recip != null && recip.length != 0) {
                        this.reportUnexpectedSend(abort, verify, sfe);
                    }
                }
                catch (MessagingException ME3) {
                    if (!this.isMissingContent(abort, ME3)) {
                        this.fixUpContent(abort, verify, ME3);
                        this.reportError(abort, ME3, 4);
                    }
                }
                if (closed != null) {
                    this.fixUpContent(abort, verify, closed);
                    this.reportError(abort, closed, 3);
                }
            }
            else {
                final String protocol2 = t.getURLName().getProtocol();
                session.getProperty("mail.host");
                session.getProperty("mail.user");
                session.getProperty("mail." + protocol2 + ".host");
                session.getProperty("mail." + protocol2 + ".port");
                session.getProperty("mail." + protocol2 + ".user");
                if (t instanceof SMTPTransport) {
                    host = ((SMTPTransport)t).getLocalHost();
                }
                else {
                    host = session.getProperty("mail." + protocol2 + ".localhost");
                    if (isEmpty(host)) {
                        host = session.getProperty("mail." + protocol2 + ".localaddress");
                    }
                }
            }
            try {
                if (isEmpty(host)) {
                    if (InetAddress.getLocalHost().getCanonicalHostName().length() == 0) {
                        throw new UnknownHostException();
                    }
                }
                else if (InetAddress.getByName(host).getCanonicalHostName().length() == 0) {
                    throw new UnknownHostException(host);
                }
            }
            catch (IOException IOE) {
                final MessagingException ME3 = new MessagingException(msg, IOE);
                this.fixUpContent(abort, verify, ME3);
                this.reportError(abort, ME3, 4);
            }
            try {
                final MimeMultipart multipart = new MimeMultipart();
                final MimeBodyPart body = new MimeBodyPart();
                body.setDisposition("inline");
                body.setDescription(verify);
                this.setAcceptLang(body);
                this.setContent(body, "", "text/plain");
                multipart.addBodyPart(body);
                abort.setContent(multipart);
                abort.saveChanges();
                abort.writeTo(new ByteArrayOutputStream(1024));
            }
            catch (IOException IOE) {
                final MessagingException ME3 = new MessagingException(msg, IOE);
                this.fixUpContent(abort, verify, ME3);
                this.reportError(abort, ME3, 5);
            }
            if (all.length == 0) {
                throw new MessagingException("No recipient addresses.");
            }
            verifyAddresses(all);
            final Address[] from = abort.getFrom();
            final Address sender = abort.getSender();
            if (sender instanceof InternetAddress) {
                ((InternetAddress)sender).validate();
            }
            if (abort.getHeader("From", ",") != null && from.length != 0) {
                verifyAddresses(from);
                for (int i = 0; i < from.length; ++i) {
                    if (from[i].equals(sender)) {
                        final MessagingException ME2 = new MessagingException("Sender address '" + sender + "' equals from address.");
                        throw new MessagingException(msg, ME2);
                    }
                }
            }
            else if (sender == null) {
                final MessagingException ME4 = new MessagingException("No from or sender address.");
                throw new MessagingException(msg, ME4);
            }
            verifyAddresses(abort.getReplyTo());
        }
        catch (MessagingException ME) {
            this.fixUpContent(abort, verify, ME);
            this.reportError(abort, ME, 4);
        }
        catch (RuntimeException RE2) {
            this.fixUpContent(abort, verify, RE2);
            this.reportError(abort, RE2, 4);
        }
    }
    
    private static void verifyAddresses(final Address[] all) throws AddressException {
        if (all != null) {
            for (int i = 0; i < all.length; ++i) {
                final Address a = all[i];
                if (a instanceof InternetAddress) {
                    ((InternetAddress)a).validate();
                }
            }
        }
    }
    
    private void reportUnexpectedSend(final MimeMessage msg, final String verify, final Exception cause) {
        final MessagingException write = new MessagingException("An empty message was sent.", cause);
        this.fixUpContent(msg, verify, write);
        this.reportError(msg, write, 4);
    }
    
    private void fixUpContent(final MimeMessage msg, final String verify, final Throwable t) {
        try {
            final MimeBodyPart body;
            final String msgDesc;
            final String subjectType;
            synchronized (this) {
                body = this.createBodyPart();
                msgDesc = this.descriptionFrom(this.comparator, this.pushLevel, this.pushFilter);
                subjectType = this.getClassId(this.subjectFormatter);
            }
            body.setDescription("Formatted using " + ((t == null) ? ((MailHandler.class$java$lang$Throwable == null) ? (MailHandler.class$java$lang$Throwable = class$("java.lang.Throwable")) : MailHandler.class$java$lang$Throwable).getName() : t.getClass().getName()) + ", filtered with " + verify + ", and named by " + subjectType + '.');
            this.setContent(body, this.toMsgString(t), "text/plain");
            final MimeMultipart multipart = new MimeMultipart();
            multipart.addBodyPart(body);
            msg.setContent(multipart);
            msg.setDescription(msgDesc);
            this.setAcceptLang(msg);
            msg.saveChanges();
        }
        catch (MessagingException ME) {
            this.reportError("Unable to create body.", ME, 4);
        }
        catch (RuntimeException RE) {
            this.reportError("Unable to create body.", RE, 4);
        }
    }
    
    private Session fixUpSession() {
        assert Thread.holdsLock(this);
        Session settings;
        if (this.mailProps.getProperty("verify") != null) {
            settings = this.initSession();
            assert settings == this.session;
        }
        else {
            this.session = null;
            settings = null;
        }
        return settings;
    }
    
    private Session initSession() {
        assert Thread.holdsLock(this);
        final String p = this.getClass().getName();
        final LogManagerProperties proxy = new LogManagerProperties(this.mailProps, p);
        return this.session = Session.getInstance(proxy, this.auth);
    }
    
    private void envelopeFor(final MessageContext ctx, final boolean priority) {
        final Message msg = ctx.getMessage();
        final Properties proxyProps = ctx.getSession().getProperties();
        this.setAcceptLang(msg);
        this.setFrom(msg, proxyProps);
        this.setRecipient(msg, proxyProps, "mail.to", Message.RecipientType.TO);
        this.setRecipient(msg, proxyProps, "mail.cc", Message.RecipientType.CC);
        this.setRecipient(msg, proxyProps, "mail.bcc", Message.RecipientType.BCC);
        this.setReplyTo(msg, proxyProps);
        this.setSender(msg, proxyProps);
        this.setMailer(msg);
        this.setAutoSubmitted(msg);
        if (priority) {
            this.setPriority(msg);
        }
        try {
            msg.setSentDate(new Date());
        }
        catch (MessagingException ME) {
            this.reportError(ME.getMessage(), ME, 5);
        }
    }
    
    private MimeBodyPart createBodyPart() throws MessagingException {
        assert Thread.holdsLock(this);
        final MimeBodyPart part = new MimeBodyPart();
        part.setDisposition("inline");
        part.setDescription(this.descriptionFrom(this.getFormatter(), this.getFilter(), this.subjectFormatter));
        this.setAcceptLang(part);
        return part;
    }
    
    private MimeBodyPart createBodyPart(final int index) throws MessagingException {
        assert Thread.holdsLock(this);
        final MimeBodyPart part = new MimeBodyPart();
        part.setDisposition("attachment");
        part.setDescription(this.descriptionFrom(this.attachmentFormatters[index], this.attachmentFilters[index], this.attachmentNames[index]));
        this.setAcceptLang(part);
        return part;
    }
    
    private String descriptionFrom(final Comparator c, final Level l, final Filter f) {
        return "Sorted using " + ((c == null) ? "no comparator" : c.getClass().getName()) + ", pushed when " + l.getName() + ", and " + ((f == null) ? "no push filter" : f.getClass().getName()) + '.';
    }
    
    private String descriptionFrom(final Formatter f, final Filter filter, final Formatter name) {
        return "Formatted using " + this.getClassId(f) + ", filtered with " + ((filter == null) ? "no filter" : filter.getClass().getName()) + ", and named by " + this.getClassId(name) + '.';
    }
    
    private String getClassId(final Formatter f) {
        if (f instanceof TailNameFormatter) {
            return ((MailHandler.class$java$lang$String == null) ? (MailHandler.class$java$lang$String = class$("java.lang.String")) : MailHandler.class$java$lang$String).getName();
        }
        return f.getClass().getName();
    }
    
    private String toString(final Formatter f) {
        final String name = f.toString();
        if (!isEmpty(name)) {
            return name;
        }
        return this.getClassId(f);
    }
    
    private void appendFileName(final Part part, final String chunk) {
        if (chunk != null) {
            if (chunk.length() > 0) {
                this.appendFileName0(part, chunk);
            }
        }
        else {
            this.reportNullError(5);
        }
    }
    
    private void appendFileName0(final Part part, final String chunk) {
        try {
            final String old = part.getFileName();
            part.setFileName((old != null) ? old.concat(chunk) : chunk);
        }
        catch (MessagingException ME) {
            this.reportError(ME.getMessage(), ME, 5);
        }
    }
    
    private void appendSubject(final Message msg, final String chunk) {
        if (chunk != null) {
            if (chunk.length() > 0) {
                this.appendSubject0(msg, chunk);
            }
        }
        else {
            this.reportNullError(5);
        }
    }
    
    private void appendSubject0(final Message msg, final String chunk) {
        try {
            final String encoding = this.getEncodingName();
            final String old = msg.getSubject();
            assert msg instanceof MimeMessage;
            ((MimeMessage)msg).setSubject((old != null) ? old.concat(chunk) : chunk, MimeUtility.mimeCharset(encoding));
        }
        catch (MessagingException ME) {
            this.reportError(ME.getMessage(), ME, 5);
        }
    }
    
    private Locale localeFor(final LogRecord r) {
        final ResourceBundle rb = r.getResourceBundle();
        Locale l;
        if (rb != null) {
            l = rb.getLocale();
            if (l == null || isEmpty(l.getLanguage())) {
                l = Locale.getDefault();
            }
        }
        else {
            l = null;
        }
        return l;
    }
    
    private void appendContentLang(final MimePart p, final Locale l) {
        try {
            String lang = LogManagerProperties.toLanguageTag(l);
            if (lang.length() != 0) {
                String header = p.getHeader("Content-Language", null);
                if (isEmpty(header)) {
                    p.setHeader("Content-Language", lang);
                }
                else if (!header.equalsIgnoreCase(lang)) {
                    lang = ",".concat(lang);
                    int idx = 0;
                    while ((idx = header.indexOf(lang, idx)) > -1) {
                        idx += lang.length();
                        if (idx == header.length() || header.charAt(idx) == ',') {
                            break;
                        }
                    }
                    if (idx < 0) {
                        int len = header.lastIndexOf("\r\n\t");
                        if (len < 0) {
                            len = 20 + header.length();
                        }
                        else {
                            len = header.length() - len + 8;
                        }
                        if (len + lang.length() > 76) {
                            header = header.concat("\r\n\t".concat(lang));
                        }
                        else {
                            header = header.concat(lang);
                        }
                        p.setHeader("Content-Language", header);
                    }
                }
            }
        }
        catch (MessagingException ME) {
            this.reportError(ME.getMessage(), ME, 5);
        }
    }
    
    private void setAcceptLang(final Part p) {
        try {
            final String lang = LogManagerProperties.toLanguageTag(Locale.getDefault());
            if (lang.length() != 0) {
                p.setHeader("Accept-Language", lang);
            }
        }
        catch (MessagingException ME) {
            this.reportError(ME.getMessage(), ME, 5);
        }
    }
    
    private void reportFilterError(final LogRecord record) {
        assert Thread.holdsLock(this);
        final SimpleFormatter f = new SimpleFormatter();
        final String msg = "Log record " + record.getSequenceNumber() + " was filtered from all message parts.  " + this.head(f) + this.format(f, record) + this.tail(f, "");
        final String txt = this.getFilter() + ", " + Arrays.asList(this.readOnlyAttachmentFilters());
        this.reportError(msg, new IllegalArgumentException(txt), 5);
    }
    
    private void reportNullError(final int code) {
        this.reportError("null", new NullPointerException(), code);
    }
    
    private String head(final Formatter f) {
        try {
            return f.getHead(this);
        }
        catch (RuntimeException RE) {
            this.reportError(RE.getMessage(), RE, 5);
            return "";
        }
    }
    
    private String format(final Formatter f, final LogRecord r) {
        try {
            return f.format(r);
        }
        catch (RuntimeException RE) {
            this.reportError(RE.getMessage(), RE, 5);
            return "";
        }
    }
    
    private String tail(final Formatter f, final String def) {
        try {
            return f.getTail(this);
        }
        catch (RuntimeException RE) {
            this.reportError(RE.getMessage(), RE, 5);
            return def;
        }
    }
    
    private void setMailer(final Message msg) {
        try {
            final Class mail = (MailHandler.class$com$sun$mail$util$logging$MailHandler == null) ? (MailHandler.class$com$sun$mail$util$logging$MailHandler = class$("com.sun.mail.util.logging.MailHandler")) : MailHandler.class$com$sun$mail$util$logging$MailHandler;
            final Class k = this.getClass();
            String value;
            if (k == mail) {
                value = mail.getName();
            }
            else {
                try {
                    value = MimeUtility.encodeText(k.getName());
                }
                catch (UnsupportedEncodingException E) {
                    this.reportError(E.getMessage(), E, 5);
                    value = k.getName().replaceAll("[^\\x00-\\x7F]", "\u001a");
                }
                value = MimeUtility.fold(10, mail.getName() + " using the " + value + " extension.");
            }
            msg.setHeader("X-Mailer", value);
        }
        catch (MessagingException ME) {
            this.reportError(ME.getMessage(), ME, 5);
        }
    }
    
    private void setPriority(final Message msg) {
        try {
            msg.setHeader("Importance", "High");
            msg.setHeader("Priority", "urgent");
            msg.setHeader("X-Priority", "2");
        }
        catch (MessagingException ME) {
            this.reportError(ME.getMessage(), ME, 5);
        }
    }
    
    private void setIncompleteCopy(final Message msg) {
        try {
            msg.setHeader("Incomplete-Copy", "");
        }
        catch (MessagingException ME) {
            this.reportError(ME.getMessage(), ME, 5);
        }
    }
    
    private void setAutoSubmitted(final Message msg) {
        try {
            msg.setHeader("auto-submitted", "auto-generated");
        }
        catch (MessagingException ME) {
            this.reportError(ME.getMessage(), ME, 5);
        }
    }
    
    private void setFrom(final Message msg, final Properties props) {
        final String from = props.getProperty("mail.from");
        if (from != null && from.length() > 0) {
            try {
                final Address[] address = InternetAddress.parse(from, false);
                if (address == null || address.length == 0) {
                    this.setDefaultFrom(msg);
                }
                else if (address.length == 1) {
                    msg.setFrom(address[0]);
                }
                else {
                    msg.addFrom(address);
                }
            }
            catch (MessagingException ME) {
                this.reportError(ME.getMessage(), ME, 5);
                this.setDefaultFrom(msg);
            }
        }
        else {
            this.setDefaultFrom(msg);
        }
    }
    
    private void setDefaultFrom(final Message msg) {
        try {
            msg.setFrom();
        }
        catch (MessagingException ME) {
            this.reportError(ME.getMessage(), ME, 5);
        }
    }
    
    private void setReplyTo(final Message msg, final Properties props) {
        final String reply = props.getProperty("mail.reply.to");
        if (reply != null && reply.length() > 0) {
            try {
                final Address[] address = InternetAddress.parse(reply, false);
                if (address != null && address.length > 0) {
                    msg.setReplyTo(address);
                }
            }
            catch (MessagingException ME) {
                this.reportError(ME.getMessage(), ME, 5);
            }
        }
    }
    
    private void setSender(final Message msg, final Properties props) {
        assert msg instanceof MimeMessage : msg;
        final String sender = props.getProperty("mail.sender");
        if (sender != null && sender.length() > 0) {
            try {
                final InternetAddress[] address = InternetAddress.parse(sender, false);
                if (address != null && address.length > 0) {
                    ((MimeMessage)msg).setSender(address[0]);
                    if (address.length > 1) {
                        this.reportError("Ignoring other senders.", tooManyAddresses(address, 1), 5);
                    }
                }
            }
            catch (MessagingException ME) {
                this.reportError(ME.getMessage(), ME, 5);
            }
        }
    }
    
    private static AddressException tooManyAddresses(final Address[] address, final int offset) {
        final String msg = Arrays.asList(address).subList(offset, address.length).toString();
        return new AddressException(msg);
    }
    
    private void setRecipient(final Message msg, final Properties props, final String key, final Message.RecipientType type) {
        final String value = props.getProperty(key);
        if (value != null && value.length() > 0) {
            try {
                final Address[] address = InternetAddress.parse(value, false);
                if (address != null && address.length > 0) {
                    msg.setRecipients(type, address);
                }
            }
            catch (MessagingException ME) {
                this.reportError(ME.getMessage(), ME, 5);
            }
        }
    }
    
    private String toRawString(final Message msg) throws MessagingException, IOException {
        if (msg != null) {
            final int nbytes = Math.max(msg.getSize() + 1024, 1024);
            final ByteArrayOutputStream out = new ByteArrayOutputStream(nbytes);
            msg.writeTo(out);
            return out.toString("US-ASCII");
        }
        return null;
    }
    
    private String toMsgString(final Throwable t) {
        if (t == null) {
            return "null";
        }
        final String encoding = this.getEncodingName();
        try {
            final ByteArrayOutputStream out = new ByteArrayOutputStream(1024);
            final PrintWriter pw = new PrintWriter(new OutputStreamWriter(out, encoding));
            pw.println(t.getMessage());
            t.printStackTrace(pw);
            pw.flush();
            pw.close();
            return out.toString(encoding);
        }
        catch (IOException badMimeCharset) {
            return t.toString() + ' ' + badMimeCharset.toString();
        }
    }
    
    private Object getAndSetContextClassLoader() {
        try {
            return AccessController.doPrivileged((PrivilegedAction<Object>)MailHandler.GET_AND_SET_CCL);
        }
        catch (SecurityException ignore) {
            return MailHandler.GET_AND_SET_CCL;
        }
    }
    
    private void setContextClassLoader(final Object ccl) {
        if (ccl == null || ccl instanceof ClassLoader) {
            AccessController.doPrivileged((PrivilegedAction<Object>)new GetAndSetContext(ccl));
        }
    }
    
    private static RuntimeException attachmentMismatch(final String msg) {
        return new IndexOutOfBoundsException(msg);
    }
    
    private static RuntimeException attachmentMismatch(final int expected, final int found) {
        return attachmentMismatch("Attachments mismatched, expected " + expected + " but given " + found + '.');
    }
    
    private static MessagingException attach(final MessagingException required, final Exception optional) {
        if (optional != null && !required.setNextException(optional) && optional instanceof MessagingException) {
            final MessagingException head = (MessagingException)optional;
            if (head.setNextException(required)) {
                return head;
            }
        }
        return required;
    }
    
    private static String atIndexMsg(final int i) {
        return "At index: " + i + '.';
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
        $assertionsDisabled = !((MailHandler.class$com$sun$mail$util$logging$MailHandler == null) ? (MailHandler.class$com$sun$mail$util$logging$MailHandler = class$("com.sun.mail.util.logging.MailHandler")) : MailHandler.class$com$sun$mail$util$logging$MailHandler).desiredAssertionStatus();
        EMPTY_FILTERS = new Filter[0];
        EMPTY_FORMATTERS = new Formatter[0];
        offValue = Level.OFF.intValue();
        GET_AND_SET_CCL = new GetAndSetContext((MailHandler.class$com$sun$mail$util$logging$MailHandler == null) ? (MailHandler.class$com$sun$mail$util$logging$MailHandler = class$("com.sun.mail.util.logging.MailHandler")) : MailHandler.class$com$sun$mail$util$logging$MailHandler);
        MUTEX = new ThreadLocal();
        MUTEX_PUBLISH = Level.ALL;
        MUTEX_REPORT = Level.OFF;
        Method m;
        try {
            m = ((MailHandler.class$java$lang$ThreadLocal == null) ? (MailHandler.class$java$lang$ThreadLocal = class$("java.lang.ThreadLocal")) : MailHandler.class$java$lang$ThreadLocal).getMethod("remove", (Class[])null);
        }
        catch (RuntimeException noAccess) {
            m = null;
        }
        catch (Exception javaOnePointFour) {
            m = null;
        }
        REMOVE = m;
    }
    
    private static final class DefaultAuthenticator extends Authenticator
    {
        private final String pass;
        
        DefaultAuthenticator(final String pass) {
            assert pass != null;
            this.pass = pass;
        }
        
        protected final PasswordAuthentication getPasswordAuthentication() {
            return new PasswordAuthentication(this.getDefaultUserName(), this.pass);
        }
        
        static {
            $assertionsDisabled = !((MailHandler.class$com$sun$mail$util$logging$MailHandler == null) ? (MailHandler.class$com$sun$mail$util$logging$MailHandler = MailHandler.class$("com.sun.mail.util.logging.MailHandler")) : MailHandler.class$com$sun$mail$util$logging$MailHandler).desiredAssertionStatus();
        }
    }
    
    private static final class GetAndSetContext implements PrivilegedAction
    {
        private final Object source;
        
        GetAndSetContext(final Object source) {
            this.source = source;
        }
        
        public final Object run() {
            final Thread current = Thread.currentThread();
            final ClassLoader ccl = current.getContextClassLoader();
            ClassLoader loader;
            if (this.source == null) {
                loader = null;
            }
            else if (this.source instanceof ClassLoader) {
                loader = (ClassLoader)this.source;
            }
            else if (this.source instanceof Class) {
                loader = ((Class)this.source).getClassLoader();
            }
            else {
                loader = this.source.getClass().getClassLoader();
            }
            if (ccl != loader) {
                current.setContextClassLoader(loader);
                return ccl;
            }
            return this;
        }
    }
    
    private static final class TailNameFormatter extends Formatter
    {
        private final String name;
        
        TailNameFormatter(final String name) {
            assert name != null;
            this.name = name;
        }
        
        public final String format(final LogRecord record) {
            return "";
        }
        
        public final String getTail(final Handler h) {
            return this.name;
        }
        
        public final boolean equals(final Object o) {
            return o instanceof TailNameFormatter && this.name.equals(((TailNameFormatter)o).name);
        }
        
        public final int hashCode() {
            return this.getClass().hashCode() + this.name.hashCode();
        }
        
        public final String toString() {
            return this.name;
        }
        
        static {
            $assertionsDisabled = !((MailHandler.class$com$sun$mail$util$logging$MailHandler == null) ? (MailHandler.class$com$sun$mail$util$logging$MailHandler = MailHandler.class$("com.sun.mail.util.logging.MailHandler")) : MailHandler.class$com$sun$mail$util$logging$MailHandler).desiredAssertionStatus();
        }
    }
}
