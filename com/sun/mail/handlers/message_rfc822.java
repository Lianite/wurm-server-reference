// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.mail.handlers;

import javax.mail.Message;
import java.io.OutputStream;
import javax.mail.MessageContext;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.mail.Authenticator;
import javax.mail.Session;
import java.util.Properties;
import javax.mail.MessageAware;
import java.io.IOException;
import javax.activation.DataSource;
import java.awt.datatransfer.DataFlavor;
import javax.activation.ActivationDataFlavor;
import javax.activation.DataContentHandler;

public class message_rfc822 implements DataContentHandler
{
    ActivationDataFlavor ourDataFlavor;
    static /* synthetic */ Class class$javax$mail$Message;
    
    public message_rfc822() {
        this.ourDataFlavor = new ActivationDataFlavor((message_rfc822.class$javax$mail$Message == null) ? (message_rfc822.class$javax$mail$Message = class$("javax.mail.Message")) : message_rfc822.class$javax$mail$Message, "message/rfc822", "Message");
    }
    
    public DataFlavor[] getTransferDataFlavors() {
        return new DataFlavor[] { this.ourDataFlavor };
    }
    
    public Object getTransferData(final DataFlavor df, final DataSource ds) throws IOException {
        if (this.ourDataFlavor.equals(df)) {
            return this.getContent(ds);
        }
        return null;
    }
    
    public Object getContent(final DataSource ds) throws IOException {
        try {
            Session session;
            if (ds instanceof MessageAware) {
                final MessageContext mc = ((MessageAware)ds).getMessageContext();
                session = mc.getSession();
            }
            else {
                session = Session.getDefaultInstance(new Properties(), null);
            }
            return new MimeMessage(session, ds.getInputStream());
        }
        catch (MessagingException me) {
            throw new IOException("Exception creating MimeMessage in message/rfc822 DataContentHandler: " + me.toString());
        }
    }
    
    public void writeTo(final Object obj, final String mimeType, final OutputStream os) throws IOException {
        if (obj instanceof Message) {
            final Message m = (Message)obj;
            try {
                m.writeTo(os);
            }
            catch (MessagingException me) {
                throw new IOException(me.toString());
            }
            return;
        }
        throw new IOException("unsupported object");
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
