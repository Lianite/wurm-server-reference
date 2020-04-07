// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.mail.imap;

import javax.mail.MessagingException;
import javax.mail.BodyPart;
import com.sun.mail.imap.protocol.BODYSTRUCTURE;
import javax.mail.internet.MimePart;
import java.util.Vector;
import javax.mail.MultipartDataSource;
import javax.mail.internet.MimePartDataSource;

public class IMAPMultipartDataSource extends MimePartDataSource implements MultipartDataSource
{
    private Vector parts;
    
    protected IMAPMultipartDataSource(final MimePart part, final BODYSTRUCTURE[] bs, final String sectionId, final IMAPMessage msg) {
        super(part);
        this.parts = new Vector(bs.length);
        for (int i = 0; i < bs.length; ++i) {
            this.parts.addElement(new IMAPBodyPart(bs[i], (sectionId == null) ? Integer.toString(i + 1) : (sectionId + "." + Integer.toString(i + 1)), msg));
        }
    }
    
    public int getCount() {
        return this.parts.size();
    }
    
    public BodyPart getBodyPart(final int index) throws MessagingException {
        return this.parts.elementAt(index);
    }
}
