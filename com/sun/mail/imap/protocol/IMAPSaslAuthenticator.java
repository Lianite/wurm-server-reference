// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.mail.imap.protocol;

import com.sun.mail.iap.ProtocolException;
import java.io.OutputStream;
import javax.security.sasl.SaslClient;
import com.sun.mail.iap.Response;
import com.sun.mail.util.BASE64EncoderStream;
import com.sun.mail.util.ASCIIUtility;
import com.sun.mail.util.BASE64DecoderStream;
import com.sun.mail.util.PropUtil;
import java.io.ByteArrayOutputStream;
import com.sun.mail.iap.Argument;
import javax.security.sasl.SaslException;
import java.util.Map;
import javax.security.sasl.Sasl;
import javax.security.sasl.RealmChoiceCallback;
import javax.security.sasl.RealmCallback;
import javax.security.auth.callback.PasswordCallback;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import java.util.logging.Level;
import java.util.Vector;
import com.sun.mail.util.MailLogger;
import java.util.Properties;

public class IMAPSaslAuthenticator implements SaslAuthenticator
{
    private IMAPProtocol pr;
    private String name;
    private Properties props;
    private MailLogger logger;
    private String host;
    
    public IMAPSaslAuthenticator(final IMAPProtocol pr, final String name, final Properties props, final MailLogger logger, final String host) {
        this.pr = pr;
        this.name = name;
        this.props = props;
        this.logger = logger;
        this.host = host;
    }
    
    public boolean authenticate(final String[] mechs, final String realm, final String authzid, final String u, final String p) throws ProtocolException {
        synchronized (this.pr) {
            final Vector v = new Vector();
            String tag = null;
            Response r = null;
            boolean done = false;
            if (this.logger.isLoggable(Level.FINE)) {
                this.logger.fine("SASL Mechanisms:");
                for (int i = 0; i < mechs.length; ++i) {
                    this.logger.fine(" " + mechs[i]);
                }
                this.logger.fine("");
            }
            final CallbackHandler cbh = new CallbackHandler() {
                public void handle(final Callback[] callbacks) {
                    if (IMAPSaslAuthenticator.this.logger.isLoggable(Level.FINE)) {
                        IMAPSaslAuthenticator.this.logger.fine("SASL callback length: " + callbacks.length);
                    }
                    for (int i = 0; i < callbacks.length; ++i) {
                        if (IMAPSaslAuthenticator.this.logger.isLoggable(Level.FINE)) {
                            IMAPSaslAuthenticator.this.logger.fine("SASL callback " + i + ": " + callbacks[i]);
                        }
                        if (callbacks[i] instanceof NameCallback) {
                            final NameCallback ncb = (NameCallback)callbacks[i];
                            ncb.setName(u);
                        }
                        else if (callbacks[i] instanceof PasswordCallback) {
                            final PasswordCallback pcb = (PasswordCallback)callbacks[i];
                            pcb.setPassword(p.toCharArray());
                        }
                        else if (callbacks[i] instanceof RealmCallback) {
                            final RealmCallback rcb = (RealmCallback)callbacks[i];
                            rcb.setText((realm != null) ? realm : rcb.getDefaultText());
                        }
                        else if (callbacks[i] instanceof RealmChoiceCallback) {
                            final RealmChoiceCallback rcb2 = (RealmChoiceCallback)callbacks[i];
                            if (realm == null) {
                                rcb2.setSelectedIndex(rcb2.getDefaultChoice());
                            }
                            else {
                                final String[] choices = rcb2.getChoices();
                                for (int k = 0; k < choices.length; ++k) {
                                    if (choices[k].equals(realm)) {
                                        rcb2.setSelectedIndex(k);
                                        break;
                                    }
                                }
                            }
                        }
                    }
                }
            };
            SaslClient sc;
            try {
                sc = Sasl.createSaslClient(mechs, authzid, this.name, this.host, (Map<String, ?>)this.props, cbh);
            }
            catch (SaslException sex) {
                this.logger.log(Level.FINE, "Failed to create SASL client", sex);
                return false;
            }
            if (sc == null) {
                this.logger.fine("No SASL support");
                return false;
            }
            if (this.logger.isLoggable(Level.FINE)) {
                this.logger.fine("SASL client " + sc.getMechanismName());
            }
            try {
                tag = this.pr.writeCommand("AUTHENTICATE " + sc.getMechanismName(), null);
            }
            catch (Exception ex) {
                this.logger.log(Level.FINE, "SASL AUTHENTICATE Exception", ex);
                return false;
            }
            final OutputStream os = this.pr.getIMAPOutputStream();
            final ByteArrayOutputStream bos = new ByteArrayOutputStream();
            final byte[] CRLF = { 13, 10 };
            final boolean isXGWTRUSTEDAPP = sc.getMechanismName().equals("XGWTRUSTEDAPP") && PropUtil.getBooleanProperty(this.props, "mail." + this.name + ".sasl.xgwtrustedapphack.enable", true);
            while (!done) {
                try {
                    r = this.pr.readResponse();
                    if (r.isContinuation()) {
                        byte[] ba = null;
                        if (!sc.isComplete()) {
                            ba = r.readByteArray().getNewBytes();
                            if (ba.length > 0) {
                                ba = BASE64DecoderStream.decode(ba);
                            }
                            if (this.logger.isLoggable(Level.FINE)) {
                                this.logger.fine("SASL challenge: " + ASCIIUtility.toString(ba, 0, ba.length) + " :");
                            }
                            ba = sc.evaluateChallenge(ba);
                        }
                        if (ba == null) {
                            this.logger.fine("SASL no response");
                            os.write(CRLF);
                            os.flush();
                            bos.reset();
                        }
                        else {
                            if (this.logger.isLoggable(Level.FINE)) {
                                this.logger.fine("SASL response: " + ASCIIUtility.toString(ba, 0, ba.length) + " :");
                            }
                            ba = BASE64EncoderStream.encode(ba);
                            if (isXGWTRUSTEDAPP) {
                                bos.write(ASCIIUtility.getBytes("XGWTRUSTEDAPP "));
                            }
                            bos.write(ba);
                            bos.write(CRLF);
                            os.write(bos.toByteArray());
                            os.flush();
                            bos.reset();
                        }
                    }
                    else if (r.isTagged() && r.getTag().equals(tag)) {
                        done = true;
                    }
                    else if (r.isBYE()) {
                        done = true;
                    }
                    else {
                        v.addElement(r);
                    }
                }
                catch (Exception ioex) {
                    this.logger.log(Level.FINE, "SASL Exception", ioex);
                    r = Response.byeResponse(ioex);
                    done = true;
                }
            }
            if (sc.isComplete()) {
                final String qop = (String)sc.getNegotiatedProperty("javax.security.sasl.qop");
                if (qop != null && (qop.equalsIgnoreCase("auth-int") || qop.equalsIgnoreCase("auth-conf"))) {
                    this.logger.fine("SASL Mechanism requires integrity or confidentiality");
                    return false;
                }
            }
            Response[] responses = new Response[v.size()];
            v.copyInto(responses);
            this.pr.notifyResponseHandlers(responses);
            this.pr.handleResult(r);
            this.pr.setCapabilities(r);
            if (isXGWTRUSTEDAPP) {
                final Argument args = new Argument();
                args.writeString((authzid != null) ? authzid : u);
                responses = this.pr.command("LOGIN", args);
                this.pr.notifyResponseHandlers(responses);
                this.pr.handleResult(responses[responses.length - 1]);
                this.pr.setCapabilities(responses[responses.length - 1]);
            }
            return true;
        }
    }
}
