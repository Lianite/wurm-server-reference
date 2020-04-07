// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.mail.imap;

import java.util.Iterator;
import javax.mail.UIDFolder;
import java.util.HashSet;
import javax.mail.FetchProfile;
import java.util.Set;
import java.util.Locale;
import com.sun.mail.iap.Response;
import com.sun.mail.imap.protocol.FetchResponse;
import com.sun.mail.imap.protocol.FetchItem;
import javax.mail.Header;
import com.sun.mail.imap.protocol.UID;
import com.sun.mail.imap.protocol.RFC822SIZE;
import com.sun.mail.imap.protocol.INTERNALDATE;
import com.sun.mail.imap.protocol.Item;
import javax.mail.Flags;
import java.util.Enumeration;
import javax.mail.internet.InternetHeaders;
import java.io.IOException;
import java.io.OutputStream;
import javax.activation.DataSource;
import javax.mail.internet.MimePart;
import javax.activation.DataHandler;
import com.sun.mail.imap.protocol.RFC822DATA;
import com.sun.mail.imap.protocol.BODY;
import java.io.InputStream;
import javax.mail.internet.ContentType;
import java.io.UnsupportedEncodingException;
import javax.mail.internet.MimeUtility;
import javax.mail.Message;
import javax.mail.IllegalWriteException;
import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import javax.mail.Address;
import com.sun.mail.iap.ConnectionException;
import javax.mail.MessageRemovedException;
import com.sun.mail.iap.ProtocolException;
import javax.mail.FolderClosedException;
import com.sun.mail.imap.protocol.IMAPProtocol;
import javax.mail.Session;
import javax.mail.Folder;
import java.util.Hashtable;
import java.util.Date;
import java.util.Map;
import com.sun.mail.imap.protocol.ENVELOPE;
import com.sun.mail.imap.protocol.BODYSTRUCTURE;
import com.sun.mail.util.ReadableMime;
import javax.mail.internet.MimeMessage;

public class IMAPMessage extends MimeMessage implements ReadableMime
{
    protected BODYSTRUCTURE bs;
    protected ENVELOPE envelope;
    protected Map items;
    private Date receivedDate;
    private int size;
    private boolean peek;
    private long uid;
    protected String sectionId;
    private String type;
    private String subject;
    private String description;
    private volatile boolean headersLoaded;
    private Hashtable loadedHeaders;
    static final String EnvelopeCmd = "ENVELOPE INTERNALDATE RFC822.SIZE";
    
    protected IMAPMessage(final IMAPFolder folder, final int msgnum) {
        super(folder, msgnum);
        this.size = -1;
        this.uid = -1L;
        this.headersLoaded = false;
        this.loadedHeaders = new Hashtable(1);
        this.flags = null;
    }
    
    protected IMAPMessage(final Session session) {
        super(session);
        this.size = -1;
        this.uid = -1L;
        this.headersLoaded = false;
        this.loadedHeaders = new Hashtable(1);
    }
    
    protected IMAPProtocol getProtocol() throws ProtocolException, FolderClosedException {
        ((IMAPFolder)this.folder).waitIfIdle();
        final IMAPProtocol p = ((IMAPFolder)this.folder).protocol;
        if (p == null) {
            throw new FolderClosedException(this.folder);
        }
        return p;
    }
    
    protected boolean isREV1() throws FolderClosedException {
        final IMAPProtocol p = ((IMAPFolder)this.folder).protocol;
        if (p == null) {
            throw new FolderClosedException(this.folder);
        }
        return p.isREV1();
    }
    
    protected Object getMessageCacheLock() {
        return ((IMAPFolder)this.folder).messageCacheLock;
    }
    
    protected int getSequenceNumber() {
        return ((IMAPFolder)this.folder).messageCache.seqnumOf(this.getMessageNumber());
    }
    
    protected void setMessageNumber(final int msgnum) {
        super.setMessageNumber(msgnum);
    }
    
    protected long getUID() {
        return this.uid;
    }
    
    protected void setUID(final long uid) {
        this.uid = uid;
    }
    
    protected void setExpunged(final boolean set) {
        super.setExpunged(set);
    }
    
    protected void checkExpunged() throws MessageRemovedException {
        if (this.expunged) {
            throw new MessageRemovedException();
        }
    }
    
    protected void forceCheckExpunged() throws MessageRemovedException, FolderClosedException {
        synchronized (this.getMessageCacheLock()) {
            try {
                this.getProtocol().noop();
            }
            catch (ConnectionException cex) {
                throw new FolderClosedException(this.folder, cex.getMessage());
            }
            catch (ProtocolException ex) {}
        }
        if (this.expunged) {
            throw new MessageRemovedException();
        }
    }
    
    protected int getFetchBlockSize() {
        return ((IMAPStore)this.folder.getStore()).getFetchBlockSize();
    }
    
    protected boolean ignoreBodyStructureSize() {
        return ((IMAPStore)this.folder.getStore()).ignoreBodyStructureSize();
    }
    
    public Address[] getFrom() throws MessagingException {
        this.checkExpunged();
        this.loadEnvelope();
        InternetAddress[] a = this.envelope.from;
        if (a == null || a.length == 0) {
            a = this.envelope.sender;
        }
        return this.aaclone(a);
    }
    
    public void setFrom(final Address address) throws MessagingException {
        throw new IllegalWriteException("IMAPMessage is read-only");
    }
    
    public void addFrom(final Address[] addresses) throws MessagingException {
        throw new IllegalWriteException("IMAPMessage is read-only");
    }
    
    public Address getSender() throws MessagingException {
        this.checkExpunged();
        this.loadEnvelope();
        if (this.envelope.sender != null) {
            return this.envelope.sender[0];
        }
        return null;
    }
    
    public void setSender(final Address address) throws MessagingException {
        throw new IllegalWriteException("IMAPMessage is read-only");
    }
    
    public Address[] getRecipients(final Message.RecipientType type) throws MessagingException {
        this.checkExpunged();
        this.loadEnvelope();
        if (type == Message.RecipientType.TO) {
            return this.aaclone(this.envelope.to);
        }
        if (type == Message.RecipientType.CC) {
            return this.aaclone(this.envelope.cc);
        }
        if (type == Message.RecipientType.BCC) {
            return this.aaclone(this.envelope.bcc);
        }
        return super.getRecipients(type);
    }
    
    public void setRecipients(final Message.RecipientType type, final Address[] addresses) throws MessagingException {
        throw new IllegalWriteException("IMAPMessage is read-only");
    }
    
    public void addRecipients(final Message.RecipientType type, final Address[] addresses) throws MessagingException {
        throw new IllegalWriteException("IMAPMessage is read-only");
    }
    
    public Address[] getReplyTo() throws MessagingException {
        this.checkExpunged();
        this.loadEnvelope();
        if (this.envelope.replyTo == null || this.envelope.replyTo.length == 0) {
            return this.getFrom();
        }
        return this.aaclone(this.envelope.replyTo);
    }
    
    public void setReplyTo(final Address[] addresses) throws MessagingException {
        throw new IllegalWriteException("IMAPMessage is read-only");
    }
    
    public String getSubject() throws MessagingException {
        this.checkExpunged();
        if (this.subject != null) {
            return this.subject;
        }
        this.loadEnvelope();
        if (this.envelope.subject == null) {
            return null;
        }
        try {
            this.subject = MimeUtility.decodeText(MimeUtility.unfold(this.envelope.subject));
        }
        catch (UnsupportedEncodingException ex) {
            this.subject = this.envelope.subject;
        }
        return this.subject;
    }
    
    public void setSubject(final String subject, final String charset) throws MessagingException {
        throw new IllegalWriteException("IMAPMessage is read-only");
    }
    
    public Date getSentDate() throws MessagingException {
        this.checkExpunged();
        this.loadEnvelope();
        if (this.envelope.date == null) {
            return null;
        }
        return new Date(this.envelope.date.getTime());
    }
    
    public void setSentDate(final Date d) throws MessagingException {
        throw new IllegalWriteException("IMAPMessage is read-only");
    }
    
    public Date getReceivedDate() throws MessagingException {
        this.checkExpunged();
        this.loadEnvelope();
        if (this.receivedDate == null) {
            return null;
        }
        return new Date(this.receivedDate.getTime());
    }
    
    public int getSize() throws MessagingException {
        this.checkExpunged();
        if (this.size == -1) {
            this.loadEnvelope();
        }
        return this.size;
    }
    
    public int getLineCount() throws MessagingException {
        this.checkExpunged();
        this.loadBODYSTRUCTURE();
        return this.bs.lines;
    }
    
    public String[] getContentLanguage() throws MessagingException {
        this.checkExpunged();
        this.loadBODYSTRUCTURE();
        if (this.bs.language != null) {
            return this.bs.language.clone();
        }
        return null;
    }
    
    public void setContentLanguage(final String[] languages) throws MessagingException {
        throw new IllegalWriteException("IMAPMessage is read-only");
    }
    
    public String getInReplyTo() throws MessagingException {
        this.checkExpunged();
        this.loadEnvelope();
        return this.envelope.inReplyTo;
    }
    
    public synchronized String getContentType() throws MessagingException {
        this.checkExpunged();
        if (this.type == null) {
            this.loadBODYSTRUCTURE();
            final ContentType ct = new ContentType(this.bs.type, this.bs.subtype, this.bs.cParams);
            this.type = ct.toString();
        }
        return this.type;
    }
    
    public String getDisposition() throws MessagingException {
        this.checkExpunged();
        this.loadBODYSTRUCTURE();
        return this.bs.disposition;
    }
    
    public void setDisposition(final String disposition) throws MessagingException {
        throw new IllegalWriteException("IMAPMessage is read-only");
    }
    
    public String getEncoding() throws MessagingException {
        this.checkExpunged();
        this.loadBODYSTRUCTURE();
        return this.bs.encoding;
    }
    
    public String getContentID() throws MessagingException {
        this.checkExpunged();
        this.loadBODYSTRUCTURE();
        return this.bs.id;
    }
    
    public void setContentID(final String cid) throws MessagingException {
        throw new IllegalWriteException("IMAPMessage is read-only");
    }
    
    public String getContentMD5() throws MessagingException {
        this.checkExpunged();
        this.loadBODYSTRUCTURE();
        return this.bs.md5;
    }
    
    public void setContentMD5(final String md5) throws MessagingException {
        throw new IllegalWriteException("IMAPMessage is read-only");
    }
    
    public String getDescription() throws MessagingException {
        this.checkExpunged();
        if (this.description != null) {
            return this.description;
        }
        this.loadBODYSTRUCTURE();
        if (this.bs.description == null) {
            return null;
        }
        try {
            this.description = MimeUtility.decodeText(this.bs.description);
        }
        catch (UnsupportedEncodingException ex) {
            this.description = this.bs.description;
        }
        return this.description;
    }
    
    public void setDescription(final String description, final String charset) throws MessagingException {
        throw new IllegalWriteException("IMAPMessage is read-only");
    }
    
    public String getMessageID() throws MessagingException {
        this.checkExpunged();
        this.loadEnvelope();
        return this.envelope.messageId;
    }
    
    public String getFileName() throws MessagingException {
        this.checkExpunged();
        String filename = null;
        this.loadBODYSTRUCTURE();
        if (this.bs.dParams != null) {
            filename = this.bs.dParams.get("filename");
        }
        if (filename == null && this.bs.cParams != null) {
            filename = this.bs.cParams.get("name");
        }
        return filename;
    }
    
    public void setFileName(final String filename) throws MessagingException {
        throw new IllegalWriteException("IMAPMessage is read-only");
    }
    
    protected InputStream getContentStream() throws MessagingException {
        InputStream is = null;
        final boolean pk = this.getPeek();
        synchronized (this.getMessageCacheLock()) {
            try {
                final IMAPProtocol p = this.getProtocol();
                this.checkExpunged();
                if (p.isREV1() && this.getFetchBlockSize() != -1) {
                    return new IMAPInputStream(this, this.toSection("TEXT"), (this.bs != null && !this.ignoreBodyStructureSize()) ? this.bs.size : -1, pk);
                }
                if (p.isREV1()) {
                    BODY b;
                    if (pk) {
                        b = p.peekBody(this.getSequenceNumber(), this.toSection("TEXT"));
                    }
                    else {
                        b = p.fetchBody(this.getSequenceNumber(), this.toSection("TEXT"));
                    }
                    if (b != null) {
                        is = b.getByteArrayInputStream();
                    }
                }
                else {
                    final RFC822DATA rd = p.fetchRFC822(this.getSequenceNumber(), "TEXT");
                    if (rd != null) {
                        is = rd.getByteArrayInputStream();
                    }
                }
            }
            catch (ConnectionException cex) {
                throw new FolderClosedException(this.folder, cex.getMessage());
            }
            catch (ProtocolException pex) {
                this.forceCheckExpunged();
                throw new MessagingException(pex.getMessage(), pex);
            }
        }
        if (is == null) {
            throw new MessagingException("No content");
        }
        return is;
    }
    
    public synchronized DataHandler getDataHandler() throws MessagingException {
        this.checkExpunged();
        if (this.dh == null) {
            this.loadBODYSTRUCTURE();
            if (this.type == null) {
                final ContentType ct = new ContentType(this.bs.type, this.bs.subtype, this.bs.cParams);
                this.type = ct.toString();
            }
            if (this.bs.isMulti()) {
                this.dh = new DataHandler(new IMAPMultipartDataSource(this, this.bs.bodies, this.sectionId, this));
            }
            else if (this.bs.isNested() && this.isREV1() && this.bs.envelope != null) {
                this.dh = new DataHandler(new IMAPNestedMessage(this, this.bs.bodies[0], this.bs.envelope, (this.sectionId == null) ? "1" : (this.sectionId + ".1")), this.type);
            }
        }
        return super.getDataHandler();
    }
    
    public void setDataHandler(final DataHandler content) throws MessagingException {
        throw new IllegalWriteException("IMAPMessage is read-only");
    }
    
    public InputStream getMimeStream() throws MessagingException {
        InputStream is = null;
        final boolean pk = this.getPeek();
        synchronized (this.getMessageCacheLock()) {
            try {
                final IMAPProtocol p = this.getProtocol();
                this.checkExpunged();
                if (p.isREV1() && this.getFetchBlockSize() != -1) {
                    return new IMAPInputStream(this, this.sectionId, -1, pk);
                }
                if (p.isREV1()) {
                    BODY b;
                    if (pk) {
                        b = p.peekBody(this.getSequenceNumber(), this.sectionId);
                    }
                    else {
                        b = p.fetchBody(this.getSequenceNumber(), this.sectionId);
                    }
                    if (b != null) {
                        is = b.getByteArrayInputStream();
                    }
                }
                else {
                    final RFC822DATA rd = p.fetchRFC822(this.getSequenceNumber(), null);
                    if (rd != null) {
                        is = rd.getByteArrayInputStream();
                    }
                }
            }
            catch (ConnectionException cex) {
                throw new FolderClosedException(this.folder, cex.getMessage());
            }
            catch (ProtocolException pex) {
                this.forceCheckExpunged();
                throw new MessagingException(pex.getMessage(), pex);
            }
        }
        if (is == null) {
            this.forceCheckExpunged();
            throw new MessagingException("No content");
        }
        return is;
    }
    
    public void writeTo(final OutputStream os) throws IOException, MessagingException {
        final InputStream is = this.getMimeStream();
        try {
            final byte[] bytes = new byte[16384];
            int count;
            while ((count = is.read(bytes)) != -1) {
                os.write(bytes, 0, count);
            }
        }
        finally {
            is.close();
        }
    }
    
    public String[] getHeader(final String name) throws MessagingException {
        this.checkExpunged();
        if (this.isHeaderLoaded(name)) {
            return this.headers.getHeader(name);
        }
        InputStream is = null;
        synchronized (this.getMessageCacheLock()) {
            try {
                final IMAPProtocol p = this.getProtocol();
                this.checkExpunged();
                if (p.isREV1()) {
                    final BODY b = p.peekBody(this.getSequenceNumber(), this.toSection("HEADER.FIELDS (" + name + ")"));
                    if (b != null) {
                        is = b.getByteArrayInputStream();
                    }
                }
                else {
                    final RFC822DATA rd = p.fetchRFC822(this.getSequenceNumber(), "HEADER.LINES (" + name + ")");
                    if (rd != null) {
                        is = rd.getByteArrayInputStream();
                    }
                }
            }
            catch (ConnectionException cex) {
                throw new FolderClosedException(this.folder, cex.getMessage());
            }
            catch (ProtocolException pex) {
                this.forceCheckExpunged();
                throw new MessagingException(pex.getMessage(), pex);
            }
        }
        if (is == null) {
            return null;
        }
        if (this.headers == null) {
            this.headers = new InternetHeaders();
        }
        this.headers.load(is);
        this.setHeaderLoaded(name);
        return this.headers.getHeader(name);
    }
    
    public String getHeader(final String name, final String delimiter) throws MessagingException {
        this.checkExpunged();
        if (this.getHeader(name) == null) {
            return null;
        }
        return this.headers.getHeader(name, delimiter);
    }
    
    public void setHeader(final String name, final String value) throws MessagingException {
        throw new IllegalWriteException("IMAPMessage is read-only");
    }
    
    public void addHeader(final String name, final String value) throws MessagingException {
        throw new IllegalWriteException("IMAPMessage is read-only");
    }
    
    public void removeHeader(final String name) throws MessagingException {
        throw new IllegalWriteException("IMAPMessage is read-only");
    }
    
    public Enumeration getAllHeaders() throws MessagingException {
        this.checkExpunged();
        this.loadHeaders();
        return super.getAllHeaders();
    }
    
    public Enumeration getMatchingHeaders(final String[] names) throws MessagingException {
        this.checkExpunged();
        this.loadHeaders();
        return super.getMatchingHeaders(names);
    }
    
    public Enumeration getNonMatchingHeaders(final String[] names) throws MessagingException {
        this.checkExpunged();
        this.loadHeaders();
        return super.getNonMatchingHeaders(names);
    }
    
    public void addHeaderLine(final String line) throws MessagingException {
        throw new IllegalWriteException("IMAPMessage is read-only");
    }
    
    public Enumeration getAllHeaderLines() throws MessagingException {
        this.checkExpunged();
        this.loadHeaders();
        return super.getAllHeaderLines();
    }
    
    public Enumeration getMatchingHeaderLines(final String[] names) throws MessagingException {
        this.checkExpunged();
        this.loadHeaders();
        return super.getMatchingHeaderLines(names);
    }
    
    public Enumeration getNonMatchingHeaderLines(final String[] names) throws MessagingException {
        this.checkExpunged();
        this.loadHeaders();
        return super.getNonMatchingHeaderLines(names);
    }
    
    public synchronized Flags getFlags() throws MessagingException {
        this.checkExpunged();
        this.loadFlags();
        return super.getFlags();
    }
    
    public synchronized boolean isSet(final Flags.Flag flag) throws MessagingException {
        this.checkExpunged();
        this.loadFlags();
        return super.isSet(flag);
    }
    
    public synchronized void setFlags(final Flags flag, final boolean set) throws MessagingException {
        synchronized (this.getMessageCacheLock()) {
            try {
                final IMAPProtocol p = this.getProtocol();
                this.checkExpunged();
                p.storeFlags(this.getSequenceNumber(), flag, set);
            }
            catch (ConnectionException cex) {
                throw new FolderClosedException(this.folder, cex.getMessage());
            }
            catch (ProtocolException pex) {
                throw new MessagingException(pex.getMessage(), pex);
            }
        }
    }
    
    public synchronized void setPeek(final boolean peek) {
        this.peek = peek;
    }
    
    public synchronized boolean getPeek() {
        return this.peek;
    }
    
    public synchronized void invalidateHeaders() {
        this.headersLoaded = false;
        this.loadedHeaders.clear();
        this.headers = null;
        this.envelope = null;
        this.bs = null;
        this.receivedDate = null;
        this.size = -1;
        this.type = null;
        this.subject = null;
        this.description = null;
        this.flags = null;
    }
    
    protected boolean handleFetchItem(final Item item, final String[] hdrs, final boolean allHeaders) throws MessagingException {
        if (item instanceof Flags) {
            this.flags = (Flags)item;
        }
        else if (item instanceof ENVELOPE) {
            this.envelope = (ENVELOPE)item;
        }
        else if (item instanceof INTERNALDATE) {
            this.receivedDate = ((INTERNALDATE)item).getDate();
        }
        else if (item instanceof RFC822SIZE) {
            this.size = ((RFC822SIZE)item).size;
        }
        else if (item instanceof BODYSTRUCTURE) {
            this.bs = (BODYSTRUCTURE)item;
        }
        else if (item instanceof UID) {
            final UID u = (UID)item;
            this.uid = u.uid;
            if (((IMAPFolder)this.folder).uidTable == null) {
                ((IMAPFolder)this.folder).uidTable = new Hashtable();
            }
            ((IMAPFolder)this.folder).uidTable.put(new Long(u.uid), this);
        }
        else {
            if (!(item instanceof RFC822DATA) && !(item instanceof BODY)) {
                return false;
            }
            InputStream headerStream;
            if (item instanceof RFC822DATA) {
                headerStream = ((RFC822DATA)item).getByteArrayInputStream();
            }
            else {
                headerStream = ((BODY)item).getByteArrayInputStream();
            }
            final InternetHeaders h = new InternetHeaders();
            if (headerStream != null) {
                h.load(headerStream);
            }
            if (this.headers == null || allHeaders) {
                this.headers = h;
            }
            else {
                final Enumeration e = h.getAllHeaders();
                while (e.hasMoreElements()) {
                    final Header he = e.nextElement();
                    if (!this.isHeaderLoaded(he.getName())) {
                        this.headers.addHeader(he.getName(), he.getValue());
                    }
                }
            }
            if (allHeaders) {
                this.setHeadersLoaded(true);
            }
            else {
                for (int k = 0; k < hdrs.length; ++k) {
                    this.setHeaderLoaded(hdrs[k]);
                }
            }
        }
        return true;
    }
    
    protected void handleExtensionFetchItems(final Map extensionItems) throws MessagingException {
        if (this.items == null) {
            this.items = extensionItems;
        }
        else {
            this.items.putAll(extensionItems);
        }
    }
    
    protected Object fetchItem(final FetchItem fitem) throws MessagingException {
        synchronized (this.getMessageCacheLock()) {
            Object robj = null;
            try {
                final IMAPProtocol p = this.getProtocol();
                this.checkExpunged();
                final int seqnum = this.getSequenceNumber();
                final Response[] r = p.fetch(seqnum, fitem.getName());
                for (int i = 0; i < r.length; ++i) {
                    if (r[i] != null && r[i] instanceof FetchResponse) {
                        if (((FetchResponse)r[i]).getNumber() == seqnum) {
                            final FetchResponse f = (FetchResponse)r[i];
                            final Object o = f.getExtensionItems().get(fitem.getName());
                            if (o != null) {
                                robj = o;
                            }
                        }
                    }
                }
                p.notifyResponseHandlers(r);
                p.handleResult(r[r.length - 1]);
            }
            catch (ConnectionException cex) {
                throw new FolderClosedException(this.folder, cex.getMessage());
            }
            catch (ProtocolException pex) {
                this.forceCheckExpunged();
                throw new MessagingException(pex.getMessage(), pex);
            }
            return robj;
        }
    }
    
    public synchronized Object getItem(final FetchItem fitem) throws MessagingException {
        Object item = (this.items == null) ? null : this.items.get(fitem.getName());
        if (item == null) {
            item = this.fetchItem(fitem);
        }
        return item;
    }
    
    private synchronized void loadEnvelope() throws MessagingException {
        if (this.envelope != null) {
            return;
        }
        Response[] r = null;
        synchronized (this.getMessageCacheLock()) {
            try {
                final IMAPProtocol p = this.getProtocol();
                this.checkExpunged();
                final int seqnum = this.getSequenceNumber();
                r = p.fetch(seqnum, "ENVELOPE INTERNALDATE RFC822.SIZE");
                for (int i = 0; i < r.length; ++i) {
                    if (r[i] != null && r[i] instanceof FetchResponse) {
                        if (((FetchResponse)r[i]).getNumber() == seqnum) {
                            final FetchResponse f = (FetchResponse)r[i];
                            for (int count = f.getItemCount(), j = 0; j < count; ++j) {
                                final Item item = f.getItem(j);
                                if (item instanceof ENVELOPE) {
                                    this.envelope = (ENVELOPE)item;
                                }
                                else if (item instanceof INTERNALDATE) {
                                    this.receivedDate = ((INTERNALDATE)item).getDate();
                                }
                                else if (item instanceof RFC822SIZE) {
                                    this.size = ((RFC822SIZE)item).size;
                                }
                            }
                        }
                    }
                }
                p.notifyResponseHandlers(r);
                p.handleResult(r[r.length - 1]);
            }
            catch (ConnectionException cex) {
                throw new FolderClosedException(this.folder, cex.getMessage());
            }
            catch (ProtocolException pex) {
                this.forceCheckExpunged();
                throw new MessagingException(pex.getMessage(), pex);
            }
        }
        if (this.envelope == null) {
            throw new MessagingException("Failed to load IMAP envelope");
        }
    }
    
    private synchronized void loadBODYSTRUCTURE() throws MessagingException {
        if (this.bs != null) {
            return;
        }
        synchronized (this.getMessageCacheLock()) {
            try {
                final IMAPProtocol p = this.getProtocol();
                this.checkExpunged();
                this.bs = p.fetchBodyStructure(this.getSequenceNumber());
            }
            catch (ConnectionException cex) {
                throw new FolderClosedException(this.folder, cex.getMessage());
            }
            catch (ProtocolException pex) {
                this.forceCheckExpunged();
                throw new MessagingException(pex.getMessage(), pex);
            }
            if (this.bs == null) {
                this.forceCheckExpunged();
                throw new MessagingException("Unable to load BODYSTRUCTURE");
            }
        }
    }
    
    private synchronized void loadHeaders() throws MessagingException {
        if (this.headersLoaded) {
            return;
        }
        InputStream is = null;
        synchronized (this.getMessageCacheLock()) {
            try {
                final IMAPProtocol p = this.getProtocol();
                this.checkExpunged();
                if (p.isREV1()) {
                    final BODY b = p.peekBody(this.getSequenceNumber(), this.toSection("HEADER"));
                    if (b != null) {
                        is = b.getByteArrayInputStream();
                    }
                }
                else {
                    final RFC822DATA rd = p.fetchRFC822(this.getSequenceNumber(), "HEADER");
                    if (rd != null) {
                        is = rd.getByteArrayInputStream();
                    }
                }
            }
            catch (ConnectionException cex) {
                throw new FolderClosedException(this.folder, cex.getMessage());
            }
            catch (ProtocolException pex) {
                this.forceCheckExpunged();
                throw new MessagingException(pex.getMessage(), pex);
            }
        }
        if (is == null) {
            throw new MessagingException("Cannot load header");
        }
        this.headers = new InternetHeaders(is);
        this.headersLoaded = true;
    }
    
    private synchronized void loadFlags() throws MessagingException {
        if (this.flags != null) {
            return;
        }
        synchronized (this.getMessageCacheLock()) {
            try {
                final IMAPProtocol p = this.getProtocol();
                this.checkExpunged();
                this.flags = p.fetchFlags(this.getSequenceNumber());
                if (this.flags == null) {
                    this.flags = new Flags();
                }
            }
            catch (ConnectionException cex) {
                throw new FolderClosedException(this.folder, cex.getMessage());
            }
            catch (ProtocolException pex) {
                this.forceCheckExpunged();
                throw new MessagingException(pex.getMessage(), pex);
            }
        }
    }
    
    private boolean areHeadersLoaded() {
        return this.headersLoaded;
    }
    
    private void setHeadersLoaded(final boolean loaded) {
        this.headersLoaded = loaded;
    }
    
    private boolean isHeaderLoaded(final String name) {
        return this.headersLoaded || this.loadedHeaders.containsKey(name.toUpperCase(Locale.ENGLISH));
    }
    
    private void setHeaderLoaded(final String name) {
        this.loadedHeaders.put(name.toUpperCase(Locale.ENGLISH), name);
    }
    
    private String toSection(final String what) {
        if (this.sectionId == null) {
            return what;
        }
        return this.sectionId + "." + what;
    }
    
    private InternetAddress[] aaclone(final InternetAddress[] aa) {
        if (aa == null) {
            return null;
        }
        return aa.clone();
    }
    
    private Flags _getFlags() {
        return this.flags;
    }
    
    private ENVELOPE _getEnvelope() {
        return this.envelope;
    }
    
    private BODYSTRUCTURE _getBodyStructure() {
        return this.bs;
    }
    
    void _setFlags(final Flags flags) {
        this.flags = flags;
    }
    
    Session _getSession() {
        return this.session;
    }
    
    public static class FetchProfileCondition implements Utility.Condition
    {
        private boolean needEnvelope;
        private boolean needFlags;
        private boolean needBodyStructure;
        private boolean needUID;
        private boolean needHeaders;
        private boolean needSize;
        private String[] hdrs;
        private Set need;
        
        public FetchProfileCondition(final FetchProfile fp, final FetchItem[] fitems) {
            this.needEnvelope = false;
            this.needFlags = false;
            this.needBodyStructure = false;
            this.needUID = false;
            this.needHeaders = false;
            this.needSize = false;
            this.hdrs = null;
            this.need = new HashSet();
            if (fp.contains(FetchProfile.Item.ENVELOPE)) {
                this.needEnvelope = true;
            }
            if (fp.contains(FetchProfile.Item.FLAGS)) {
                this.needFlags = true;
            }
            if (fp.contains(FetchProfile.Item.CONTENT_INFO)) {
                this.needBodyStructure = true;
            }
            if (fp.contains(UIDFolder.FetchProfileItem.UID)) {
                this.needUID = true;
            }
            if (fp.contains(IMAPFolder.FetchProfileItem.HEADERS)) {
                this.needHeaders = true;
            }
            if (fp.contains(IMAPFolder.FetchProfileItem.SIZE)) {
                this.needSize = true;
            }
            this.hdrs = fp.getHeaderNames();
            for (int i = 0; i < fitems.length; ++i) {
                if (fp.contains(fitems[i].getFetchProfileItem())) {
                    this.need.add(fitems[i]);
                }
            }
        }
        
        public boolean test(final IMAPMessage m) {
            if (this.needEnvelope && m._getEnvelope() == null) {
                return true;
            }
            if (this.needFlags && m._getFlags() == null) {
                return true;
            }
            if (this.needBodyStructure && m._getBodyStructure() == null) {
                return true;
            }
            if (this.needUID && m.getUID() == -1L) {
                return true;
            }
            if (this.needHeaders && !m.areHeadersLoaded()) {
                return true;
            }
            if (this.needSize && m.size == -1) {
                return true;
            }
            for (int i = 0; i < this.hdrs.length; ++i) {
                if (!m.isHeaderLoaded(this.hdrs[i])) {
                    return true;
                }
            }
            for (final FetchItem fitem : this.need) {
                if (m.items == null || m.items.get(fitem.getName()) == null) {
                    return true;
                }
            }
            return false;
        }
    }
}
