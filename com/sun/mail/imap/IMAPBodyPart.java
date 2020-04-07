// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.mail.imap;

import com.sun.mail.util.PropUtil;
import javax.mail.internet.InternetHeaders;
import javax.mail.Multipart;
import javax.activation.DataSource;
import javax.mail.internet.MimePart;
import javax.activation.DataHandler;
import java.io.SequenceInputStream;
import java.io.ByteArrayInputStream;
import java.util.Enumeration;
import java.io.IOException;
import java.io.OutputStream;
import com.sun.mail.util.LineOutputStream;
import com.sun.mail.util.SharedByteArrayOutputStream;
import com.sun.mail.imap.protocol.BODY;
import com.sun.mail.imap.protocol.IMAPProtocol;
import com.sun.mail.iap.ProtocolException;
import com.sun.mail.iap.ConnectionException;
import javax.mail.FolderClosedException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import javax.mail.internet.MimeUtility;
import javax.mail.IllegalWriteException;
import javax.mail.MessagingException;
import javax.mail.internet.ContentType;
import com.sun.mail.imap.protocol.BODYSTRUCTURE;
import com.sun.mail.util.ReadableMime;
import javax.mail.internet.MimeBodyPart;

public class IMAPBodyPart extends MimeBodyPart implements ReadableMime
{
    private IMAPMessage message;
    private BODYSTRUCTURE bs;
    private String sectionId;
    private String type;
    private String description;
    private boolean headersLoaded;
    private static final boolean decodeFileName;
    
    protected IMAPBodyPart(final BODYSTRUCTURE bs, final String sid, final IMAPMessage message) {
        this.headersLoaded = false;
        this.bs = bs;
        this.sectionId = sid;
        this.message = message;
        final ContentType ct = new ContentType(bs.type, bs.subtype, bs.cParams);
        this.type = ct.toString();
    }
    
    protected void updateHeaders() {
    }
    
    public int getSize() throws MessagingException {
        return this.bs.size;
    }
    
    public int getLineCount() throws MessagingException {
        return this.bs.lines;
    }
    
    public String getContentType() throws MessagingException {
        return this.type;
    }
    
    public String getDisposition() throws MessagingException {
        return this.bs.disposition;
    }
    
    public void setDisposition(final String disposition) throws MessagingException {
        throw new IllegalWriteException("IMAPBodyPart is read-only");
    }
    
    public String getEncoding() throws MessagingException {
        return this.bs.encoding;
    }
    
    public String getContentID() throws MessagingException {
        return this.bs.id;
    }
    
    public String getContentMD5() throws MessagingException {
        return this.bs.md5;
    }
    
    public void setContentMD5(final String md5) throws MessagingException {
        throw new IllegalWriteException("IMAPBodyPart is read-only");
    }
    
    public String getDescription() throws MessagingException {
        if (this.description != null) {
            return this.description;
        }
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
        throw new IllegalWriteException("IMAPBodyPart is read-only");
    }
    
    public String getFileName() throws MessagingException {
        String filename = null;
        if (this.bs.dParams != null) {
            filename = this.bs.dParams.get("filename");
        }
        if (filename == null && this.bs.cParams != null) {
            filename = this.bs.cParams.get("name");
        }
        if (IMAPBodyPart.decodeFileName && filename != null) {
            try {
                filename = MimeUtility.decodeText(filename);
            }
            catch (UnsupportedEncodingException ex) {
                throw new MessagingException("Can't decode filename", ex);
            }
        }
        return filename;
    }
    
    public void setFileName(final String filename) throws MessagingException {
        throw new IllegalWriteException("IMAPBodyPart is read-only");
    }
    
    protected InputStream getContentStream() throws MessagingException {
        InputStream is = null;
        final boolean pk = this.message.getPeek();
        synchronized (this.message.getMessageCacheLock()) {
            try {
                final IMAPProtocol p = this.message.getProtocol();
                this.message.checkExpunged();
                if (p.isREV1() && this.message.getFetchBlockSize() != -1) {
                    return new IMAPInputStream(this.message, this.sectionId, this.message.ignoreBodyStructureSize() ? -1 : this.bs.size, pk);
                }
                final int seqnum = this.message.getSequenceNumber();
                BODY b;
                if (pk) {
                    b = p.peekBody(seqnum, this.sectionId);
                }
                else {
                    b = p.fetchBody(seqnum, this.sectionId);
                }
                if (b != null) {
                    is = b.getByteArrayInputStream();
                }
            }
            catch (ConnectionException cex) {
                throw new FolderClosedException(this.message.getFolder(), cex.getMessage());
            }
            catch (ProtocolException pex) {
                throw new MessagingException(pex.getMessage(), pex);
            }
        }
        if (is == null) {
            throw new MessagingException("No content");
        }
        return is;
    }
    
    private InputStream getHeaderStream() throws MessagingException {
        if (!this.message.isREV1()) {
            this.loadHeaders();
        }
        synchronized (this.message.getMessageCacheLock()) {
            try {
                final IMAPProtocol p = this.message.getProtocol();
                this.message.checkExpunged();
                if (!p.isREV1()) {
                    final SharedByteArrayOutputStream bos = new SharedByteArrayOutputStream(0);
                    final LineOutputStream los = new LineOutputStream(bos);
                    try {
                        final Enumeration hdrLines = super.getAllHeaderLines();
                        while (hdrLines.hasMoreElements()) {
                            los.writeln(hdrLines.nextElement());
                        }
                        los.writeln();
                    }
                    catch (IOException ioex) {}
                    finally {
                        try {
                            los.close();
                        }
                        catch (IOException ex) {}
                    }
                    return bos.toStream();
                }
                final int seqnum = this.message.getSequenceNumber();
                final BODY b = p.peekBody(seqnum, this.sectionId + ".MIME");
                if (b == null) {
                    throw new MessagingException("Failed to fetch headers");
                }
                final ByteArrayInputStream bis = b.getByteArrayInputStream();
                if (bis == null) {
                    throw new MessagingException("Failed to fetch headers");
                }
                return bis;
            }
            catch (ConnectionException cex) {
                throw new FolderClosedException(this.message.getFolder(), cex.getMessage());
            }
            catch (ProtocolException pex) {
                throw new MessagingException(pex.getMessage(), pex);
            }
        }
    }
    
    public InputStream getMimeStream() throws MessagingException {
        return new SequenceInputStream(this.getHeaderStream(), this.getContentStream());
    }
    
    public synchronized DataHandler getDataHandler() throws MessagingException {
        if (this.dh == null) {
            if (this.bs.isMulti()) {
                this.dh = new DataHandler(new IMAPMultipartDataSource(this, this.bs.bodies, this.sectionId, this.message));
            }
            else if (this.bs.isNested() && this.message.isREV1() && this.bs.envelope != null) {
                this.dh = new DataHandler(new IMAPNestedMessage(this.message, this.bs.bodies[0], this.bs.envelope, this.sectionId), this.type);
            }
        }
        return super.getDataHandler();
    }
    
    public void setDataHandler(final DataHandler content) throws MessagingException {
        throw new IllegalWriteException("IMAPBodyPart is read-only");
    }
    
    public void setContent(final Object o, final String type) throws MessagingException {
        throw new IllegalWriteException("IMAPBodyPart is read-only");
    }
    
    public void setContent(final Multipart mp) throws MessagingException {
        throw new IllegalWriteException("IMAPBodyPart is read-only");
    }
    
    public String[] getHeader(final String name) throws MessagingException {
        this.loadHeaders();
        return super.getHeader(name);
    }
    
    public void setHeader(final String name, final String value) throws MessagingException {
        throw new IllegalWriteException("IMAPBodyPart is read-only");
    }
    
    public void addHeader(final String name, final String value) throws MessagingException {
        throw new IllegalWriteException("IMAPBodyPart is read-only");
    }
    
    public void removeHeader(final String name) throws MessagingException {
        throw new IllegalWriteException("IMAPBodyPart is read-only");
    }
    
    public Enumeration getAllHeaders() throws MessagingException {
        this.loadHeaders();
        return super.getAllHeaders();
    }
    
    public Enumeration getMatchingHeaders(final String[] names) throws MessagingException {
        this.loadHeaders();
        return super.getMatchingHeaders(names);
    }
    
    public Enumeration getNonMatchingHeaders(final String[] names) throws MessagingException {
        this.loadHeaders();
        return super.getNonMatchingHeaders(names);
    }
    
    public void addHeaderLine(final String line) throws MessagingException {
        throw new IllegalWriteException("IMAPBodyPart is read-only");
    }
    
    public Enumeration getAllHeaderLines() throws MessagingException {
        this.loadHeaders();
        return super.getAllHeaderLines();
    }
    
    public Enumeration getMatchingHeaderLines(final String[] names) throws MessagingException {
        this.loadHeaders();
        return super.getMatchingHeaderLines(names);
    }
    
    public Enumeration getNonMatchingHeaderLines(final String[] names) throws MessagingException {
        this.loadHeaders();
        return super.getNonMatchingHeaderLines(names);
    }
    
    private synchronized void loadHeaders() throws MessagingException {
        if (this.headersLoaded) {
            return;
        }
        if (this.headers == null) {
            this.headers = new InternetHeaders();
        }
        synchronized (this.message.getMessageCacheLock()) {
            try {
                final IMAPProtocol p = this.message.getProtocol();
                this.message.checkExpunged();
                if (p.isREV1()) {
                    final int seqnum = this.message.getSequenceNumber();
                    final BODY b = p.peekBody(seqnum, this.sectionId + ".MIME");
                    if (b == null) {
                        throw new MessagingException("Failed to fetch headers");
                    }
                    final ByteArrayInputStream bis = b.getByteArrayInputStream();
                    if (bis == null) {
                        throw new MessagingException("Failed to fetch headers");
                    }
                    this.headers.load(bis);
                }
                else {
                    this.headers.addHeader("Content-Type", this.type);
                    this.headers.addHeader("Content-Transfer-Encoding", this.bs.encoding);
                    if (this.bs.description != null) {
                        this.headers.addHeader("Content-Description", this.bs.description);
                    }
                    if (this.bs.id != null) {
                        this.headers.addHeader("Content-ID", this.bs.id);
                    }
                    if (this.bs.md5 != null) {
                        this.headers.addHeader("Content-MD5", this.bs.md5);
                    }
                }
            }
            catch (ConnectionException cex) {
                throw new FolderClosedException(this.message.getFolder(), cex.getMessage());
            }
            catch (ProtocolException pex) {
                throw new MessagingException(pex.getMessage(), pex);
            }
        }
        this.headersLoaded = true;
    }
    
    static {
        decodeFileName = PropUtil.getBooleanSystemProperty("mail.mime.decodefilename", false);
    }
}
