// 
// Decompiled by Procyon v0.5.30
// 

package javax.mail.internet;

import java.io.ObjectStreamException;
import java.util.Enumeration;
import com.sun.mail.util.LineOutputStream;
import java.util.Vector;
import javax.mail.Part;
import javax.mail.Multipart;
import com.sun.mail.util.MessageRemovedIOException;
import javax.mail.MessageRemovedException;
import com.sun.mail.util.FolderClosedIOException;
import javax.mail.FolderClosedException;
import javax.activation.DataSource;
import com.sun.mail.util.MimeUtil;
import java.text.ParseException;
import java.util.Date;
import java.io.UnsupportedEncodingException;
import javax.mail.Address;
import com.sun.mail.util.ASCIIUtility;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import com.sun.mail.util.PropUtil;
import javax.mail.Folder;
import java.io.IOException;
import javax.mail.util.SharedByteArrayInputStream;
import java.io.OutputStream;
import java.io.ByteArrayOutputStream;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Flags;
import java.io.InputStream;
import javax.activation.DataHandler;
import javax.mail.Message;

public class MimeMessage extends Message implements MimePart
{
    protected DataHandler dh;
    protected byte[] content;
    protected InputStream contentStream;
    protected InternetHeaders headers;
    protected Flags flags;
    protected boolean modified;
    protected boolean saved;
    Object cachedContent;
    private static final MailDateFormat mailDateFormat;
    private boolean strict;
    private static final Flags answeredFlag;
    
    public MimeMessage(final Session session) {
        super(session);
        this.modified = false;
        this.saved = false;
        this.strict = true;
        this.modified = true;
        this.headers = new InternetHeaders();
        this.flags = new Flags();
        this.initStrict();
    }
    
    public MimeMessage(final Session session, final InputStream is) throws MessagingException {
        super(session);
        this.modified = false;
        this.saved = false;
        this.strict = true;
        this.flags = new Flags();
        this.initStrict();
        this.parse(is);
        this.saved = true;
    }
    
    public MimeMessage(final MimeMessage source) throws MessagingException {
        super(source.session);
        this.modified = false;
        this.saved = false;
        this.strict = true;
        this.flags = source.getFlags();
        if (this.flags == null) {
            this.flags = new Flags();
        }
        final int size = source.getSize();
        ByteArrayOutputStream bos;
        if (size > 0) {
            bos = new ByteArrayOutputStream(size);
        }
        else {
            bos = new ByteArrayOutputStream();
        }
        try {
            this.strict = source.strict;
            source.writeTo(bos);
            bos.close();
            final SharedByteArrayInputStream bis = new SharedByteArrayInputStream(bos.toByteArray());
            this.parse(bis);
            bis.close();
            this.saved = true;
        }
        catch (IOException ex) {
            throw new MessagingException("IOException while copying message", ex);
        }
    }
    
    protected MimeMessage(final Folder folder, final int msgnum) {
        super(folder, msgnum);
        this.modified = false;
        this.saved = false;
        this.strict = true;
        this.flags = new Flags();
        this.saved = true;
        this.initStrict();
    }
    
    protected MimeMessage(final Folder folder, final InputStream is, final int msgnum) throws MessagingException {
        this(folder, msgnum);
        this.initStrict();
        this.parse(is);
    }
    
    protected MimeMessage(final Folder folder, final InternetHeaders headers, final byte[] content, final int msgnum) throws MessagingException {
        this(folder, msgnum);
        this.headers = headers;
        this.content = content;
        this.initStrict();
    }
    
    private void initStrict() {
        if (this.session != null) {
            this.strict = PropUtil.getBooleanSessionProperty(this.session, "mail.mime.address.strict", true);
        }
    }
    
    protected void parse(InputStream is) throws MessagingException {
        if (!(is instanceof ByteArrayInputStream) && !(is instanceof BufferedInputStream) && !(is instanceof SharedInputStream)) {
            is = new BufferedInputStream(is);
        }
        this.headers = this.createInternetHeaders(is);
        if (is instanceof SharedInputStream) {
            final SharedInputStream sis = (SharedInputStream)is;
            this.contentStream = sis.newStream(sis.getPosition(), -1L);
        }
        else {
            try {
                this.content = ASCIIUtility.getBytes(is);
            }
            catch (IOException ioex) {
                throw new MessagingException("IOException", ioex);
            }
        }
        this.modified = false;
    }
    
    public Address[] getFrom() throws MessagingException {
        Address[] a = this.getAddressHeader("From");
        if (a == null) {
            a = this.getAddressHeader("Sender");
        }
        return a;
    }
    
    public void setFrom(final Address address) throws MessagingException {
        if (address == null) {
            this.removeHeader("From");
        }
        else {
            this.setHeader("From", address.toString());
        }
    }
    
    public void setFrom() throws MessagingException {
        InternetAddress me = null;
        try {
            me = InternetAddress._getLocalAddress(this.session);
        }
        catch (Exception ex) {
            throw new MessagingException("No From address", ex);
        }
        if (me != null) {
            this.setFrom(me);
            return;
        }
        throw new MessagingException("No From address");
    }
    
    public void addFrom(final Address[] addresses) throws MessagingException {
        this.addAddressHeader("From", addresses);
    }
    
    public Address getSender() throws MessagingException {
        final Address[] a = this.getAddressHeader("Sender");
        if (a == null || a.length == 0) {
            return null;
        }
        return a[0];
    }
    
    public void setSender(final Address address) throws MessagingException {
        if (address == null) {
            this.removeHeader("Sender");
        }
        else {
            this.setHeader("Sender", address.toString());
        }
    }
    
    public Address[] getRecipients(final Message.RecipientType type) throws MessagingException {
        if (type == RecipientType.NEWSGROUPS) {
            final String s = this.getHeader("Newsgroups", ",");
            return (Address[])((s == null) ? null : NewsAddress.parse(s));
        }
        return this.getAddressHeader(this.getHeaderName(type));
    }
    
    public Address[] getAllRecipients() throws MessagingException {
        final Address[] all = super.getAllRecipients();
        final Address[] ng = this.getRecipients(RecipientType.NEWSGROUPS);
        if (ng == null) {
            return all;
        }
        if (all == null) {
            return ng;
        }
        final Address[] addresses = new Address[all.length + ng.length];
        System.arraycopy(all, 0, addresses, 0, all.length);
        System.arraycopy(ng, 0, addresses, all.length, ng.length);
        return addresses;
    }
    
    public void setRecipients(final Message.RecipientType type, final Address[] addresses) throws MessagingException {
        if (type == RecipientType.NEWSGROUPS) {
            if (addresses == null || addresses.length == 0) {
                this.removeHeader("Newsgroups");
            }
            else {
                this.setHeader("Newsgroups", NewsAddress.toString(addresses));
            }
        }
        else {
            this.setAddressHeader(this.getHeaderName(type), addresses);
        }
    }
    
    public void setRecipients(final Message.RecipientType type, final String addresses) throws MessagingException {
        if (type == RecipientType.NEWSGROUPS) {
            if (addresses == null || addresses.length() == 0) {
                this.removeHeader("Newsgroups");
            }
            else {
                this.setHeader("Newsgroups", addresses);
            }
        }
        else {
            this.setAddressHeader(this.getHeaderName(type), (Address[])((addresses == null) ? null : InternetAddress.parse(addresses)));
        }
    }
    
    public void addRecipients(final Message.RecipientType type, final Address[] addresses) throws MessagingException {
        if (type == RecipientType.NEWSGROUPS) {
            final String s = NewsAddress.toString(addresses);
            if (s != null) {
                this.addHeader("Newsgroups", s);
            }
        }
        else {
            this.addAddressHeader(this.getHeaderName(type), addresses);
        }
    }
    
    public void addRecipients(final Message.RecipientType type, final String addresses) throws MessagingException {
        if (type == RecipientType.NEWSGROUPS) {
            if (addresses != null && addresses.length() != 0) {
                this.addHeader("Newsgroups", addresses);
            }
        }
        else {
            this.addAddressHeader(this.getHeaderName(type), InternetAddress.parse(addresses));
        }
    }
    
    public Address[] getReplyTo() throws MessagingException {
        Address[] a = this.getAddressHeader("Reply-To");
        if (a == null || a.length == 0) {
            a = this.getFrom();
        }
        return a;
    }
    
    public void setReplyTo(final Address[] addresses) throws MessagingException {
        this.setAddressHeader("Reply-To", addresses);
    }
    
    private Address[] getAddressHeader(final String name) throws MessagingException {
        final String s = this.getHeader(name, ",");
        return (Address[])((s == null) ? null : InternetAddress.parseHeader(s, this.strict));
    }
    
    private void setAddressHeader(final String name, final Address[] addresses) throws MessagingException {
        final String s = InternetAddress.toString(addresses);
        if (s == null) {
            this.removeHeader(name);
        }
        else {
            this.setHeader(name, s);
        }
    }
    
    private void addAddressHeader(final String name, final Address[] addresses) throws MessagingException {
        if (addresses == null || addresses.length == 0) {
            return;
        }
        final Address[] a = this.getAddressHeader(name);
        Address[] anew;
        if (a == null || a.length == 0) {
            anew = addresses;
        }
        else {
            anew = new Address[a.length + addresses.length];
            System.arraycopy(a, 0, anew, 0, a.length);
            System.arraycopy(addresses, 0, anew, a.length, addresses.length);
        }
        final String s = InternetAddress.toString(anew);
        if (s == null) {
            return;
        }
        this.setHeader(name, s);
    }
    
    public String getSubject() throws MessagingException {
        final String rawvalue = this.getHeader("Subject", null);
        if (rawvalue == null) {
            return null;
        }
        try {
            return MimeUtility.decodeText(MimeUtility.unfold(rawvalue));
        }
        catch (UnsupportedEncodingException ex) {
            return rawvalue;
        }
    }
    
    public void setSubject(final String subject) throws MessagingException {
        this.setSubject(subject, null);
    }
    
    public void setSubject(final String subject, final String charset) throws MessagingException {
        if (subject == null) {
            this.removeHeader("Subject");
        }
        else {
            try {
                this.setHeader("Subject", MimeUtility.fold(9, MimeUtility.encodeText(subject, charset, null)));
            }
            catch (UnsupportedEncodingException uex) {
                throw new MessagingException("Encoding error", uex);
            }
        }
    }
    
    public Date getSentDate() throws MessagingException {
        final String s = this.getHeader("Date", null);
        if (s != null) {
            try {
                synchronized (MimeMessage.mailDateFormat) {
                    return MimeMessage.mailDateFormat.parse(s);
                }
            }
            catch (ParseException pex) {
                return null;
            }
        }
        return null;
    }
    
    public void setSentDate(final Date d) throws MessagingException {
        if (d == null) {
            this.removeHeader("Date");
        }
        else {
            synchronized (MimeMessage.mailDateFormat) {
                this.setHeader("Date", MimeMessage.mailDateFormat.format(d));
            }
        }
    }
    
    public Date getReceivedDate() throws MessagingException {
        return null;
    }
    
    public int getSize() throws MessagingException {
        if (this.content != null) {
            return this.content.length;
        }
        if (this.contentStream != null) {
            try {
                final int size = this.contentStream.available();
                if (size > 0) {
                    return size;
                }
            }
            catch (IOException ex) {}
        }
        return -1;
    }
    
    public int getLineCount() throws MessagingException {
        return -1;
    }
    
    public String getContentType() throws MessagingException {
        String s = this.getHeader("Content-Type", null);
        s = MimeUtil.cleanContentType(this, s);
        if (s == null) {
            return "text/plain";
        }
        return s;
    }
    
    public boolean isMimeType(final String mimeType) throws MessagingException {
        return MimeBodyPart.isMimeType(this, mimeType);
    }
    
    public String getDisposition() throws MessagingException {
        return MimeBodyPart.getDisposition(this);
    }
    
    public void setDisposition(final String disposition) throws MessagingException {
        MimeBodyPart.setDisposition(this, disposition);
    }
    
    public String getEncoding() throws MessagingException {
        return MimeBodyPart.getEncoding(this);
    }
    
    public String getContentID() throws MessagingException {
        return this.getHeader("Content-Id", null);
    }
    
    public void setContentID(final String cid) throws MessagingException {
        if (cid == null) {
            this.removeHeader("Content-ID");
        }
        else {
            this.setHeader("Content-ID", cid);
        }
    }
    
    public String getContentMD5() throws MessagingException {
        return this.getHeader("Content-MD5", null);
    }
    
    public void setContentMD5(final String md5) throws MessagingException {
        this.setHeader("Content-MD5", md5);
    }
    
    public String getDescription() throws MessagingException {
        return MimeBodyPart.getDescription(this);
    }
    
    public void setDescription(final String description) throws MessagingException {
        this.setDescription(description, null);
    }
    
    public void setDescription(final String description, final String charset) throws MessagingException {
        MimeBodyPart.setDescription(this, description, charset);
    }
    
    public String[] getContentLanguage() throws MessagingException {
        return MimeBodyPart.getContentLanguage(this);
    }
    
    public void setContentLanguage(final String[] languages) throws MessagingException {
        MimeBodyPart.setContentLanguage(this, languages);
    }
    
    public String getMessageID() throws MessagingException {
        return this.getHeader("Message-ID", null);
    }
    
    public String getFileName() throws MessagingException {
        return MimeBodyPart.getFileName(this);
    }
    
    public void setFileName(final String filename) throws MessagingException {
        MimeBodyPart.setFileName(this, filename);
    }
    
    private String getHeaderName(final Message.RecipientType type) throws MessagingException {
        String headerName;
        if (type == Message.RecipientType.TO) {
            headerName = "To";
        }
        else if (type == Message.RecipientType.CC) {
            headerName = "Cc";
        }
        else if (type == Message.RecipientType.BCC) {
            headerName = "Bcc";
        }
        else {
            if (type != RecipientType.NEWSGROUPS) {
                throw new MessagingException("Invalid Recipient Type");
            }
            headerName = "Newsgroups";
        }
        return headerName;
    }
    
    public InputStream getInputStream() throws IOException, MessagingException {
        return this.getDataHandler().getInputStream();
    }
    
    protected InputStream getContentStream() throws MessagingException {
        if (this.contentStream != null) {
            return ((SharedInputStream)this.contentStream).newStream(0L, -1L);
        }
        if (this.content != null) {
            return new SharedByteArrayInputStream(this.content);
        }
        throw new MessagingException("No MimeMessage content");
    }
    
    public InputStream getRawInputStream() throws MessagingException {
        return this.getContentStream();
    }
    
    public synchronized DataHandler getDataHandler() throws MessagingException {
        if (this.dh == null) {
            this.dh = new MimeBodyPart.MimePartDataHandler(new MimePartDataSource(this));
        }
        return this.dh;
    }
    
    public Object getContent() throws IOException, MessagingException {
        if (this.cachedContent != null) {
            return this.cachedContent;
        }
        Object c;
        try {
            c = this.getDataHandler().getContent();
        }
        catch (FolderClosedIOException fex) {
            throw new FolderClosedException(fex.getFolder(), fex.getMessage());
        }
        catch (MessageRemovedIOException mex) {
            throw new MessageRemovedException(mex.getMessage());
        }
        if (MimeBodyPart.cacheMultipart && (c instanceof Multipart || c instanceof Message) && (this.content != null || this.contentStream != null)) {
            this.cachedContent = c;
            if (c instanceof MimeMultipart) {
                ((MimeMultipart)c).parse();
            }
        }
        return c;
    }
    
    public synchronized void setDataHandler(final DataHandler dh) throws MessagingException {
        this.dh = dh;
        this.cachedContent = null;
        MimeBodyPart.invalidateContentHeaders(this);
    }
    
    public void setContent(final Object o, final String type) throws MessagingException {
        if (o instanceof Multipart) {
            this.setContent((Multipart)o);
        }
        else {
            this.setDataHandler(new DataHandler(o, type));
        }
    }
    
    public void setText(final String text) throws MessagingException {
        this.setText(text, null);
    }
    
    public void setText(final String text, final String charset) throws MessagingException {
        MimeBodyPart.setText(this, text, charset, "plain");
    }
    
    public void setText(final String text, final String charset, final String subtype) throws MessagingException {
        MimeBodyPart.setText(this, text, charset, subtype);
    }
    
    public void setContent(final Multipart mp) throws MessagingException {
        this.setDataHandler(new DataHandler(mp, mp.getContentType()));
        mp.setParent(this);
    }
    
    public Message reply(final boolean replyToAll) throws MessagingException {
        final MimeMessage reply = this.createMimeMessage(this.session);
        String subject = this.getHeader("Subject", null);
        if (subject != null) {
            if (!subject.regionMatches(true, 0, "Re: ", 0, 4)) {
                subject = "Re: " + subject;
            }
            reply.setHeader("Subject", subject);
        }
        Address[] a = this.getReplyTo();
        reply.setRecipients(Message.RecipientType.TO, a);
        if (replyToAll) {
            final Vector v = new Vector();
            final InternetAddress me = InternetAddress.getLocalAddress(this.session);
            if (me != null) {
                v.addElement(me);
            }
            String alternates = null;
            if (this.session != null) {
                alternates = this.session.getProperty("mail.alternates");
            }
            if (alternates != null) {
                this.eliminateDuplicates(v, InternetAddress.parse(alternates, false));
            }
            final String replyallccStr = null;
            boolean replyallcc = false;
            if (this.session != null) {
                replyallcc = PropUtil.getBooleanSessionProperty(this.session, "mail.replyallcc", false);
            }
            this.eliminateDuplicates(v, a);
            a = this.getRecipients(Message.RecipientType.TO);
            a = this.eliminateDuplicates(v, a);
            if (a != null && a.length > 0) {
                if (replyallcc) {
                    reply.addRecipients(Message.RecipientType.CC, a);
                }
                else {
                    reply.addRecipients(Message.RecipientType.TO, a);
                }
            }
            a = this.getRecipients(Message.RecipientType.CC);
            a = this.eliminateDuplicates(v, a);
            if (a != null && a.length > 0) {
                reply.addRecipients(Message.RecipientType.CC, a);
            }
            a = this.getRecipients(RecipientType.NEWSGROUPS);
            if (a != null && a.length > 0) {
                reply.setRecipients(RecipientType.NEWSGROUPS, a);
            }
        }
        final String msgId = this.getHeader("Message-Id", null);
        if (msgId != null) {
            reply.setHeader("In-Reply-To", msgId);
        }
        String refs = this.getHeader("References", " ");
        if (refs == null) {
            refs = this.getHeader("In-Reply-To", " ");
        }
        if (msgId != null) {
            if (refs != null) {
                refs = MimeUtility.unfold(refs) + " " + msgId;
            }
            else {
                refs = msgId;
            }
        }
        if (refs != null) {
            reply.setHeader("References", MimeUtility.fold(12, refs));
        }
        try {
            this.setFlags(MimeMessage.answeredFlag, true);
        }
        catch (MessagingException ex) {}
        return reply;
    }
    
    private Address[] eliminateDuplicates(final Vector v, Address[] addrs) {
        if (addrs == null) {
            return null;
        }
        int gone = 0;
        for (int i = 0; i < addrs.length; ++i) {
            boolean found = false;
            for (int j = 0; j < v.size(); ++j) {
                if (v.elementAt(j).equals(addrs[i])) {
                    found = true;
                    ++gone;
                    addrs[i] = null;
                    break;
                }
            }
            if (!found) {
                v.addElement(addrs[i]);
            }
        }
        if (gone != 0) {
            Address[] a;
            if (addrs instanceof InternetAddress[]) {
                a = new InternetAddress[addrs.length - gone];
            }
            else {
                a = new Address[addrs.length - gone];
            }
            int k = 0;
            int j = 0;
            while (k < addrs.length) {
                if (addrs[k] != null) {
                    a[j++] = addrs[k];
                }
                ++k;
            }
            addrs = a;
        }
        return addrs;
    }
    
    public void writeTo(final OutputStream os) throws IOException, MessagingException {
        this.writeTo(os, null);
    }
    
    public void writeTo(final OutputStream os, final String[] ignoreList) throws IOException, MessagingException {
        if (!this.saved) {
            this.saveChanges();
        }
        if (this.modified) {
            MimeBodyPart.writeTo(this, os, ignoreList);
            return;
        }
        final Enumeration hdrLines = this.getNonMatchingHeaderLines(ignoreList);
        final LineOutputStream los = new LineOutputStream(os);
        while (hdrLines.hasMoreElements()) {
            los.writeln(hdrLines.nextElement());
        }
        los.writeln();
        if (this.content == null) {
            InputStream is = null;
            byte[] buf = new byte[8192];
            try {
                is = this.getContentStream();
                int len;
                while ((len = is.read(buf)) > 0) {
                    os.write(buf, 0, len);
                }
            }
            finally {
                if (is != null) {
                    is.close();
                }
                buf = null;
            }
        }
        else {
            os.write(this.content);
        }
        os.flush();
    }
    
    public String[] getHeader(final String name) throws MessagingException {
        return this.headers.getHeader(name);
    }
    
    public String getHeader(final String name, final String delimiter) throws MessagingException {
        return this.headers.getHeader(name, delimiter);
    }
    
    public void setHeader(final String name, final String value) throws MessagingException {
        this.headers.setHeader(name, value);
    }
    
    public void addHeader(final String name, final String value) throws MessagingException {
        this.headers.addHeader(name, value);
    }
    
    public void removeHeader(final String name) throws MessagingException {
        this.headers.removeHeader(name);
    }
    
    public Enumeration getAllHeaders() throws MessagingException {
        return this.headers.getAllHeaders();
    }
    
    public Enumeration getMatchingHeaders(final String[] names) throws MessagingException {
        return this.headers.getMatchingHeaders(names);
    }
    
    public Enumeration getNonMatchingHeaders(final String[] names) throws MessagingException {
        return this.headers.getNonMatchingHeaders(names);
    }
    
    public void addHeaderLine(final String line) throws MessagingException {
        this.headers.addHeaderLine(line);
    }
    
    public Enumeration getAllHeaderLines() throws MessagingException {
        return this.headers.getAllHeaderLines();
    }
    
    public Enumeration getMatchingHeaderLines(final String[] names) throws MessagingException {
        return this.headers.getMatchingHeaderLines(names);
    }
    
    public Enumeration getNonMatchingHeaderLines(final String[] names) throws MessagingException {
        return this.headers.getNonMatchingHeaderLines(names);
    }
    
    public synchronized Flags getFlags() throws MessagingException {
        return (Flags)this.flags.clone();
    }
    
    public synchronized boolean isSet(final Flags.Flag flag) throws MessagingException {
        return this.flags.contains(flag);
    }
    
    public synchronized void setFlags(final Flags flag, final boolean set) throws MessagingException {
        if (set) {
            this.flags.add(flag);
        }
        else {
            this.flags.remove(flag);
        }
    }
    
    public void saveChanges() throws MessagingException {
        this.modified = true;
        this.saved = true;
        this.updateHeaders();
    }
    
    protected void updateMessageID() throws MessagingException {
        this.setHeader("Message-ID", "<" + UniqueValue.getUniqueMessageIDValue(this.session) + ">");
    }
    
    protected synchronized void updateHeaders() throws MessagingException {
        MimeBodyPart.updateHeaders(this);
        this.setHeader("MIME-Version", "1.0");
        this.updateMessageID();
        if (this.cachedContent != null) {
            this.dh = new DataHandler(this.cachedContent, this.getContentType());
            this.cachedContent = null;
            this.content = null;
            if (this.contentStream != null) {
                try {
                    this.contentStream.close();
                }
                catch (IOException ex) {}
            }
            this.contentStream = null;
        }
    }
    
    protected InternetHeaders createInternetHeaders(final InputStream is) throws MessagingException {
        return new InternetHeaders(is);
    }
    
    protected MimeMessage createMimeMessage(final Session session) throws MessagingException {
        return new MimeMessage(session);
    }
    
    static {
        mailDateFormat = new MailDateFormat();
        answeredFlag = new Flags(Flags.Flag.ANSWERED);
    }
    
    public static class RecipientType extends Message.RecipientType
    {
        private static final long serialVersionUID = -5468290701714395543L;
        public static final RecipientType NEWSGROUPS;
        
        protected RecipientType(final String type) {
            super(type);
        }
        
        protected Object readResolve() throws ObjectStreamException {
            if (this.type.equals("Newsgroups")) {
                return RecipientType.NEWSGROUPS;
            }
            return super.readResolve();
        }
        
        static {
            NEWSGROUPS = new RecipientType("Newsgroups");
        }
    }
}
