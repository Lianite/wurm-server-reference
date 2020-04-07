// 
// Decompiled by Procyon v0.5.30
// 

package javax.mail.internet;

import java.io.IOException;
import java.util.Enumeration;
import com.sun.mail.util.LineOutputStream;
import java.io.OutputStream;
import javax.mail.MessagingException;

public class PreencodedMimeBodyPart extends MimeBodyPart
{
    private String encoding;
    
    public PreencodedMimeBodyPart(final String encoding) {
        this.encoding = encoding;
    }
    
    public String getEncoding() throws MessagingException {
        return this.encoding;
    }
    
    public void writeTo(final OutputStream os) throws IOException, MessagingException {
        LineOutputStream los = null;
        if (os instanceof LineOutputStream) {
            los = (LineOutputStream)os;
        }
        else {
            los = new LineOutputStream(os);
        }
        final Enumeration hdrLines = this.getAllHeaderLines();
        while (hdrLines.hasMoreElements()) {
            los.writeln(hdrLines.nextElement());
        }
        los.writeln();
        this.getDataHandler().writeTo(os);
        os.flush();
    }
    
    protected void updateHeaders() throws MessagingException {
        super.updateHeaders();
        MimeBodyPart.setEncoding(this, this.encoding);
    }
}
