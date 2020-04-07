// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server;

import java.util.Hashtable;
import javax.mail.PasswordAuthentication;
import java.io.Reader;
import java.io.BufferedReader;
import java.io.FileReader;
import java.net.InetAddress;
import javax.mail.MessagingException;
import javax.mail.internet.AddressException;
import javax.mail.Transport;
import java.util.logging.Level;
import javax.mail.Message;
import javax.mail.Address;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.Authenticator;
import javax.mail.Session;
import java.util.Properties;
import java.util.logging.Logger;

public final class Mailer
{
    private static final String pwfileName = "passwordmail.html";
    private static final String regmailfileName1 = "registrationphase1.html";
    private static final String regmailfileName2 = "registrationphase2.html";
    private static final String premexpiryw = "premiumexpirywarning.html";
    private static final String accountdelw = "accountdeletionwarning.html";
    private static final String accountdels = "accountdeletionsilvers.html";
    private static String phaseOneMail;
    private static String phaseTwoMail;
    private static String passwordMail;
    private static String accountDelMail;
    private static String accountDelPreventionMail;
    private static String premExpiryMail;
    private static final Logger logger;
    public static String smtpserver;
    private static String smtpuser;
    private static String smtppw;
    private static final String amaserver = "";
    
    public static void sendMail(final String sender, final String receiver, final String subject, final String text) throws AddressException, MessagingException {
        new Thread() {
            @Override
            public void run() {
                try {
                    final Properties props = new Properties();
                    props.setProperty("mail.transport.protocol", "smtp");
                    SMTPAuthenticator pwa = null;
                    if (Servers.localServer.LOGINSERVER) {
                        ((Hashtable<String, String>)props).put("mail.host", "");
                        ((Hashtable<String, String>)props).put("mail.smtp.auth", "true");
                        pwa = new SMTPAuthenticator();
                    }
                    else {
                        ((Hashtable<String, String>)props).put("mail.host", Mailer.smtpserver);
                    }
                    ((Hashtable<String, String>)props).put("mail.user", sender);
                    if (Servers.localServer.LOGINSERVER) {
                        ((Hashtable<String, String>)props).put("mail.smtp.host", "");
                    }
                    else {
                        ((Hashtable<String, String>)props).put("mail.smtp.host", Mailer.smtpserver);
                    }
                    ((Hashtable<String, String>)props).put("mail.smtp.port", "25");
                    final Session session = Session.getDefaultInstance(props, pwa);
                    final Properties properties = session.getProperties();
                    final String key = "mail.smtp.localhost";
                    String prop = properties.getProperty("mail.smtp.localhost");
                    if (prop == null) {
                        prop = getLocalHost(session);
                        ((Hashtable<String, String>)properties).put("mail.smtp.localhost", prop);
                    }
                    final MimeMessage msg = new MimeMessage(session);
                    msg.setContent(text, "text/html");
                    msg.setSubject(subject);
                    msg.setFrom(new InternetAddress(sender));
                    msg.addRecipient(MimeMessage.RecipientType.TO, new InternetAddress(receiver));
                    msg.saveChanges();
                    final Transport transport = session.getTransport("smtp");
                    transport.connect();
                    transport.sendMessage(msg, msg.getAllRecipients());
                    transport.close();
                }
                catch (Exception ex) {
                    Mailer.logger.log(Level.WARNING, ex.getMessage(), ex);
                }
            }
        }.start();
    }
    
    private static final String getLocalHost(final Session session) {
        String localHostName = null;
        final String name = "smtp";
        try {
            if (localHostName == null || localHostName.length() <= 0) {
                localHostName = InetAddress.getLocalHost().getHostName();
            }
            if (localHostName == null || localHostName.length() <= 0) {
                localHostName = session.getProperty("mail.smtp.localhost");
            }
        }
        catch (Exception uhex) {
            return "localhost";
        }
        return localHostName;
    }
    
    public static final String getPhaseOneMail() {
        if (Mailer.phaseOneMail == null) {
            Mailer.phaseOneMail = loadConfirmationMail1();
        }
        return Mailer.phaseOneMail;
    }
    
    public static final String getPhaseTwoMail() {
        if (Mailer.phaseTwoMail == null) {
            Mailer.phaseTwoMail = loadConfirmationMail2();
        }
        return Mailer.phaseTwoMail;
    }
    
    public static final String getPasswordMail() {
        if (Mailer.passwordMail == null) {
            Mailer.passwordMail = loadPasswordMail();
        }
        return Mailer.passwordMail;
    }
    
    public static final String getAccountDelPreventionMail() {
        if (Mailer.accountDelPreventionMail == null) {
            Mailer.accountDelPreventionMail = loadAccountDelPreventionMail();
        }
        return Mailer.accountDelPreventionMail;
    }
    
    public static final String getAccountDelMail() {
        if (Mailer.accountDelMail == null) {
            Mailer.accountDelMail = loadAccountDelMail();
        }
        return Mailer.accountDelMail;
    }
    
    public static final String getPremExpiryMail() {
        if (Mailer.premExpiryMail == null) {
            Mailer.premExpiryMail = loadPremExpiryMail();
        }
        return Mailer.premExpiryMail;
    }
    
    private static final String loadConfirmationMail1() {
        try (final BufferedReader in = new BufferedReader(new FileReader("registrationphase1.html"))) {
            final StringBuilder buf = new StringBuilder();
            String str;
            while ((str = in.readLine()) != null) {
                buf.append(str);
            }
            in.close();
            return buf.toString();
        }
        catch (Exception ex) {
            return "";
        }
    }
    
    private static final String loadConfirmationMail2() {
        try (final BufferedReader in = new BufferedReader(new FileReader("registrationphase2.html"))) {
            final StringBuilder buf = new StringBuilder();
            String str;
            while ((str = in.readLine()) != null) {
                buf.append(str);
            }
            in.close();
            return buf.toString();
        }
        catch (Exception ex) {
            return "";
        }
    }
    
    private static final String loadPasswordMail() {
        try (final BufferedReader in = new BufferedReader(new FileReader("passwordmail.html"))) {
            final StringBuilder buf = new StringBuilder();
            String str;
            while ((str = in.readLine()) != null) {
                buf.append(str);
            }
            in.close();
            return buf.toString();
        }
        catch (Exception ex) {
            return "";
        }
    }
    
    private static final String loadAccountDelMail() {
        try (final BufferedReader in = new BufferedReader(new FileReader("accountdeletionwarning.html"))) {
            final StringBuilder buf = new StringBuilder();
            String str;
            while ((str = in.readLine()) != null) {
                buf.append(str);
            }
            in.close();
            return buf.toString();
        }
        catch (Exception ex) {
            return "";
        }
    }
    
    private static final String loadAccountDelPreventionMail() {
        try (final BufferedReader in = new BufferedReader(new FileReader("accountdeletionsilvers.html"))) {
            final StringBuilder buf = new StringBuilder();
            String str;
            while ((str = in.readLine()) != null) {
                buf.append(str);
            }
            in.close();
            return buf.toString();
        }
        catch (Exception ex) {
            return "";
        }
    }
    
    private static final String loadPremExpiryMail() {
        try (final BufferedReader in = new BufferedReader(new FileReader("premiumexpirywarning.html"))) {
            final StringBuilder buf = new StringBuilder();
            String str;
            while ((str = in.readLine()) != null) {
                buf.append(str);
            }
            in.close();
            return buf.toString();
        }
        catch (Exception ex) {
            return "";
        }
    }
    
    public static void main(final String[] args) {
    }
    
    static {
        Mailer.phaseOneMail = loadConfirmationMail1();
        Mailer.phaseTwoMail = loadConfirmationMail2();
        Mailer.passwordMail = loadPasswordMail();
        Mailer.accountDelMail = loadAccountDelMail();
        Mailer.accountDelPreventionMail = loadAccountDelPreventionMail();
        Mailer.premExpiryMail = loadPremExpiryMail();
        logger = Logger.getLogger(Mailer.class.getName());
        Mailer.smtpserver = "localhost";
        Mailer.smtpuser = "";
        Mailer.smtppw = "";
    }
    
    private static final class SMTPAuthenticator extends Authenticator
    {
        public PasswordAuthentication getPasswordAuthentication() {
            final String username = Mailer.smtpuser;
            final String password = Mailer.smtppw;
            return new PasswordAuthentication(username, password);
        }
    }
}
