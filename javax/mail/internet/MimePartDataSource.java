// 
// Decompiled by Procyon v0.5.30
// 

package javax.mail.internet;

import javax.mail.Part;
import java.net.UnknownServiceException;
import java.io.OutputStream;
import java.io.IOException;
import javax.mail.FolderClosedException;
import com.sun.mail.util.FolderClosedIOException;
import javax.mail.MessagingException;
import java.io.InputStream;
import javax.mail.MessageContext;
import javax.mail.MessageAware;
import javax.activation.DataSource;

public class MimePartDataSource implements DataSource, MessageAware
{
    protected MimePart part;
    private MessageContext context;
    
    public MimePartDataSource(final MimePart part) {
        this.part = part;
    }
    
    public InputStream getInputStream() throws IOException {
        try {
            InputStream is;
            if (this.part instanceof MimeBodyPart) {
                is = ((MimeBodyPart)this.part).getContentStream();
            }
            else {
                if (!(this.part instanceof MimeMessage)) {
                    throw new MessagingException("Unknown part");
                }
                is = ((MimeMessage)this.part).getContentStream();
            }
            final String encoding = MimeBodyPart.restrictEncoding(this.part, this.part.getEncoding());
            if (encoding != null) {
                return MimeUtility.decode(is, encoding);
            }
            return is;
        }
        catch (FolderClosedException fex) {
            throw new FolderClosedIOException(fex.getFolder(), fex.getMessage());
        }
        catch (MessagingException mex) {
            throw new IOException(mex.getMessage());
        }
    }
    
    public OutputStream getOutputStream() throws IOException {
        throw new UnknownServiceException("Writing not supported");
    }
    
    public String getContentType() {
        try {
            return this.part.getContentType();
        }
        catch (MessagingException mex) {
            return "application/octet-stream";
        }
    }
    
    public String getName() {
        try {
            if (this.part instanceof MimeBodyPart) {
                return ((MimeBodyPart)this.part).getFileName();
            }
        }
        catch (MessagingException ex) {}
        return "";
    }
    
    public synchronized MessageContext getMessageContext() {
        if (this.context == null) {
            this.context = new MessageContext(this.part);
        }
        return this.context;
    }
}
