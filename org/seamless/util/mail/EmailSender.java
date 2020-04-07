// 
// Decompiled by Procyon v0.5.30
// 

package org.seamless.util.mail;

import java.util.Hashtable;
import javax.mail.Authenticator;
import javax.mail.BodyPart;
import javax.mail.internet.MimeMultipart;
import javax.mail.internet.MimeBodyPart;
import javax.mail.Multipart;
import javax.mail.MessagingException;
import javax.mail.Transport;
import javax.mail.Session;
import java.util.Date;
import javax.mail.Message;
import javax.mail.Address;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

public class EmailSender
{
    protected final Properties properties;
    protected final String host;
    protected final String user;
    protected final String password;
    
    public EmailSender(final String host, final String user, final String password) {
        this.properties = new Properties();
        if (host == null || host.length() == 0) {
            throw new IllegalArgumentException("Host is required");
        }
        this.host = host;
        this.user = user;
        this.password = password;
        ((Hashtable<String, String>)this.properties).put("mail.smtp.port", "25");
        ((Hashtable<String, String>)this.properties).put("mail.smtp.socketFactory.fallback", "false");
        ((Hashtable<String, String>)this.properties).put("mail.smtp.quitwait", "false");
        ((Hashtable<String, String>)this.properties).put("mail.smtp.host", host);
        ((Hashtable<String, String>)this.properties).put("mail.smtp.starttls.enable", "true");
        if (user != null && password != null) {
            ((Hashtable<String, String>)this.properties).put("mail.smtp.auth", "true");
        }
    }
    
    public Properties getProperties() {
        return this.properties;
    }
    
    public String getHost() {
        return this.host;
    }
    
    public String getUser() {
        return this.user;
    }
    
    public String getPassword() {
        return this.password;
    }
    
    public void send(final Email email) throws MessagingException {
        final Session session = this.createSession();
        final MimeMessage msg = new MimeMessage(session);
        msg.setFrom(new InternetAddress(email.getSender()));
        final InternetAddress[] receipients = { new InternetAddress(email.getRecipient()) };
        msg.setRecipients(Message.RecipientType.TO, receipients);
        msg.setSubject(email.getSubject());
        msg.setSentDate(new Date());
        msg.setContent(this.createContent(email));
        final Transport transport = this.createConnectedTransport(session);
        transport.sendMessage(msg, msg.getAllRecipients());
        transport.close();
    }
    
    protected Multipart createContent(final Email email) throws MessagingException {
        final MimeBodyPart partOne = new MimeBodyPart();
        partOne.setText(email.getPlaintext());
        final Multipart mp = new MimeMultipart("alternative");
        mp.addBodyPart(partOne);
        if (email.getHtml() != null) {
            final MimeBodyPart partTwo = new MimeBodyPart();
            partTwo.setContent(email.getHtml(), "text/html");
            mp.addBodyPart(partTwo);
        }
        return mp;
    }
    
    protected Session createSession() {
        return Session.getInstance(this.properties, null);
    }
    
    protected Transport createConnectedTransport(final Session session) throws MessagingException {
        final Transport transport = session.getTransport("smtp");
        if (this.user != null && this.password != null) {
            transport.connect(this.user, this.password);
        }
        else {
            transport.connect();
        }
        return transport;
    }
}
