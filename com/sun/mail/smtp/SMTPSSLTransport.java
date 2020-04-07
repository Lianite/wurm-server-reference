// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.mail.smtp;

import javax.mail.URLName;
import javax.mail.Session;

public class SMTPSSLTransport extends SMTPTransport
{
    public SMTPSSLTransport(final Session session, final URLName urlname) {
        super(session, urlname, "smtps", true);
    }
}
