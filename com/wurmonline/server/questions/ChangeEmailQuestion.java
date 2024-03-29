// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.questions;

import java.io.IOException;
import com.wurmonline.server.players.PlayerInfoFactory;
import com.wurmonline.server.LoginServerWebConnection;
import com.wurmonline.server.webinterface.WebInterfaceImpl;
import com.wurmonline.server.players.Player;
import java.util.logging.Level;
import com.wurmonline.server.LoginHandler;
import java.util.Properties;
import com.wurmonline.server.creatures.Creature;
import java.util.logging.Logger;

public class ChangeEmailQuestion extends Question
{
    private static final Logger logger;
    boolean providedPassword;
    String passProvided;
    String passProvidedHashed;
    String alertMessage;
    
    public ChangeEmailQuestion(final Creature aResponder) {
        super(aResponder, "Email address for " + aResponder.getName(), "Changing email address", 112, -10L);
        this.providedPassword = false;
        this.passProvided = "unknown";
        this.passProvidedHashed = "unknown";
        this.alertMessage = "";
    }
    
    @Override
    public void answer(final Properties answers) {
        if (!this.providedPassword) {
            final String oldpw = answers.getProperty("pwinput");
            if (oldpw == null || oldpw.length() < 6) {
                this.getResponder().getCommunicator().sendNormalServerMessage("The old password contains at least 6 characters.");
                return;
            }
            String hashedpw = "";
            try {
                hashedpw = LoginHandler.hashPassword(oldpw, LoginHandler.encrypt(LoginHandler.raiseFirstLetter(this.getResponder().getName())));
            }
            catch (Exception e) {
                ChangeEmailQuestion.logger.log(Level.WARNING, "Failed to encrypt pw for " + this.getResponder().getName() + " with " + oldpw);
            }
            if (!hashedpw.equals(((Player)this.getResponder()).getSaveFile().getPassword())) {
                this.getResponder().getCommunicator().sendNormalServerMessage("You provided the wrong password.");
                return;
            }
            this.providedPassword = true;
            this.passProvided = oldpw;
            this.passProvidedHashed = hashedpw;
            final ChangeEmailQuestion ceq = new ChangeEmailQuestion(this.getResponder());
            ceq.providedPassword = true;
            ceq.passProvided = this.passProvided;
            ceq.passProvidedHashed = this.passProvidedHashed;
            ceq.sendQuestion();
        }
        else {
            boolean resend = false;
            this.alertMessage = "";
            final String newEmail = answers.getProperty("emailAddress");
            String ppassword = answers.getProperty("pwinput2");
            if (ppassword == null || ppassword.length() < 2) {
                ppassword = this.passProvided;
            }
            final String pwQuestion = answers.getProperty("pwQuestion");
            if (pwQuestion == null || pwQuestion.length() < 5) {
                this.getResponder().getCommunicator().sendAlertServerMessage("You need to provide a password retrieval question at least 5 characters long. This is used on the website.");
                this.alertMessage = "You need to provide a password retrieval question at least 5 characters long. This is used on the website.";
                resend = true;
            }
            final String pwAnswer = answers.getProperty("pwAnswer");
            if (pwAnswer == null || pwAnswer.length() < 3) {
                this.getResponder().getCommunicator().sendAlertServerMessage("You need to provide a password retrieval answer at least 3 characters long. This is used on the website.");
                this.alertMessage = "You need to provide a password retrieval answer at least 3 characters long. This is used on the website.";
                resend = true;
            }
            if (!resend) {
                if (newEmail != null && WebInterfaceImpl.isEmailValid(newEmail)) {
                    if (!newEmail.equalsIgnoreCase(((Player)this.getResponder()).getSaveFile().emailAddress)) {
                        resend = false;
                        this.getResponder().getCommunicator().sendNormalServerMessage("You try to change the email to '" + newEmail + "' - result:");
                        final LoginServerWebConnection lsw = new LoginServerWebConnection();
                        final String isok = lsw.changeEmail(this.getResponder().getName(), this.getResponder().getName(), newEmail, ppassword, this.getResponder().getPower(), pwQuestion, pwAnswer);
                        this.getResponder().getCommunicator().sendNormalServerMessage(isok);
                        if (isok.contains("- ok")) {
                            try {
                                PlayerInfoFactory.changeEmail(this.getResponder().getName(), this.getResponder().getName(), newEmail, ppassword, this.getResponder().getPower(), pwQuestion, pwAnswer);
                                ChangeEmailQuestion.logger.log(Level.INFO, this.getResponder().getName() + " changed the email to " + newEmail);
                                this.getResponder().getCommunicator().lastChangedEmail = System.currentTimeMillis();
                            }
                            catch (IOException iox) {
                                ChangeEmailQuestion.logger.log(Level.INFO, this.getResponder().getName() + " FAILED changed the email to " + newEmail, new Exception());
                                this.getResponder().getCommunicator().sendAlertServerMessage("The email was successfully changed on the login server, but not changed locally!");
                            }
                        }
                    }
                    else {
                        this.getResponder().getCommunicator().sendAlertServerMessage("No change was made.");
                    }
                }
                else {
                    this.getResponder().getCommunicator().sendAlertServerMessage("The email " + newEmail + " is not a valid email address.");
                    this.alertMessage = "The email " + newEmail + " is not a valid email address.";
                    resend = true;
                }
            }
            if (resend) {
                final ChangeEmailQuestion ceq2 = new ChangeEmailQuestion(this.getResponder());
                ceq2.providedPassword = true;
                ceq2.passProvided = this.passProvided;
                ceq2.passProvidedHashed = this.passProvidedHashed;
                ceq2.alertMessage = this.alertMessage;
                ceq2.sendQuestion();
            }
        }
    }
    
    @Override
    public void sendQuestion() {
        final StringBuilder buf = new StringBuilder(this.getBmlHeader());
        buf.append("text{text=\"Current email:\"}");
        buf.append("text{text=\"" + ((Player)this.getResponder()).getSaveFile().emailAddress + "\"}");
        if (!this.providedPassword) {
            buf.append("text{text=\"Provide the account password in order to update email information:\"}");
            buf.append("input{id=\"pwinput\";maxchars=\"32\"};");
        }
        else {
            if (this.alertMessage != null) {
                buf.append("label{color=\"255,40,40\";text=\"" + this.alertMessage + "\"}");
            }
            buf.append("text{text=\"Desired email:\"}");
            buf.append("input{id=\"emailAddress\";maxchars=\"127\";text=\"" + ((Player)this.getResponder()).getSaveFile().emailAddress + "\"};");
            buf.append("text{text=\"If you want to change the email to one already in use, you need to provide the password for an account using that email.\"}");
            buf.append("text{text=\"If you want to change the email to one that is not in use, you need to leave this empty instead:\"}");
            buf.append("input{id=\"pwinput2\";maxchars=\"30\"};");
            buf.append("text{text=\"Question for password retrieval via website:\"}");
            buf.append("input{id=\"pwQuestion\";maxchars=\"127\";text=\"" + ((Player)this.getResponder()).getSaveFile().pwQuestion + "\"};");
            buf.append("text{text=\"Answer to that question:\"}");
            buf.append("input{id=\"pwAnswer\";maxchars=\"20\";text=\"" + ((Player)this.getResponder()).getSaveFile().pwAnswer + "\"};");
        }
        buf.append(this.createAnswerButton2());
        this.getResponder().getCommunicator().sendBml(300, 300, true, true, buf.toString(), 200, 200, 200, this.title);
    }
    
    static {
        logger = Logger.getLogger(ChangeEmailQuestion.class.getName());
    }
}
