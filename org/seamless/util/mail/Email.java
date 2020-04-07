// 
// Decompiled by Procyon v0.5.30
// 

package org.seamless.util.mail;

public class Email
{
    protected String sender;
    protected String recipient;
    protected String subject;
    protected String plaintext;
    protected String html;
    
    public Email(final String sender, final String recipient, final String subject, final String plaintext) {
        this(sender, recipient, subject, plaintext, null);
    }
    
    public Email(final String sender, final String recipient, final String subject, final String plaintext, final String html) {
        this.sender = sender;
        this.recipient = recipient;
        this.subject = subject;
        this.plaintext = plaintext;
        this.html = html;
    }
    
    public String getSender() {
        return this.sender;
    }
    
    public void setSender(final String sender) {
        this.sender = sender;
    }
    
    public String getRecipient() {
        return this.recipient;
    }
    
    public void setRecipient(final String recipient) {
        this.recipient = recipient;
    }
    
    public String getSubject() {
        return this.subject;
    }
    
    public void setSubject(final String subject) {
        this.subject = subject;
    }
    
    public String getPlaintext() {
        return this.plaintext;
    }
    
    public void setPlaintext(final String plaintext) {
        this.plaintext = plaintext;
    }
    
    public String getHtml() {
        return this.html;
    }
    
    public void setHtml(final String html) {
        this.html = html;
    }
}
