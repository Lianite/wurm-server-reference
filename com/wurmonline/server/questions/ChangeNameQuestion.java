// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.questions;

import com.wurmonline.server.Server;
import com.wurmonline.server.Items;
import com.wurmonline.server.LoginServerWebConnection;
import com.wurmonline.server.Players;
import com.wurmonline.server.deities.Deities;
import com.wurmonline.server.players.Player;
import java.util.logging.Level;
import com.wurmonline.server.LoginHandler;
import java.util.Properties;
import com.wurmonline.server.creatures.Creature;
import com.wurmonline.server.items.Item;
import java.util.logging.Logger;

public final class ChangeNameQuestion extends Question
{
    private static final Logger logger;
    private final int maxSize = 40;
    private final int minSize = 3;
    private Item certificate;
    
    public ChangeNameQuestion(final Creature aResponder, final Item cert) {
        super(aResponder, "Name change", "Do you wish to change your name?", 109, cert.getWurmId());
        this.certificate = cert;
    }
    
    @Override
    public void answer(final Properties answers) {
        this.setAnswer(answers);
        final Creature responder = this.getResponder();
        final String oldname = responder.getName();
        String newname = answers.getProperty("answer");
        final String oldpw = answers.getProperty("oldpw");
        final String newpw = answers.getProperty("newpw");
        if (this.certificate == null || this.certificate.deleted || this.certificate.getOwnerId() != this.getResponder().getWurmId()) {
            responder.getCommunicator().sendNormalServerMessage("You are no longer in possession of the certificate it seems.");
            return;
        }
        if (oldpw == null || oldpw.length() < 6) {
            responder.getCommunicator().sendNormalServerMessage("The old password contains at least 6 characters.");
            return;
        }
        if (newpw == null || newpw.length() < 6) {
            responder.getCommunicator().sendNormalServerMessage("The new password needs at least 6 characters.");
            return;
        }
        if (newpw.length() > 40) {
            responder.getCommunicator().sendNormalServerMessage("The new password is over 40 characters long.");
            return;
        }
        String hashedpw = "";
        try {
            hashedpw = LoginHandler.hashPassword(oldpw, LoginHandler.encrypt(LoginHandler.raiseFirstLetter(this.getResponder().getName())));
        }
        catch (Exception e) {
            ChangeNameQuestion.logger.log(Level.WARNING, "Failed to encrypt pw for " + this.getResponder().getName() + " with " + oldpw);
        }
        if (!hashedpw.equals(((Player)this.getResponder()).getSaveFile().getPassword())) {
            responder.getCommunicator().sendNormalServerMessage("You provided the wrong password.");
            return;
        }
        if (newname == null || newname.length() < 3) {
            responder.getCommunicator().sendNormalServerMessage("Your name remains the same since it would be too short.");
            return;
        }
        if (QuestionParser.containsIllegalCharacters(newname)) {
            responder.getCommunicator().sendNormalServerMessage("The name contains illegal characters.");
            return;
        }
        if (newname.equalsIgnoreCase(this.getResponder().getName())) {
            responder.getCommunicator().sendNormalServerMessage("Your name remains the same.");
            return;
        }
        if (newname.length() > 40) {
            responder.getCommunicator().sendNormalServerMessage("Too long. Your name remains the same.");
            return;
        }
        if (Deities.isNameOkay(newname)) {
            if (Players.getInstance().doesPlayerNameExist(newname)) {
                responder.getCommunicator().sendNormalServerMessage("The name " + newname + " is already in use.");
            }
            else {
                newname = LoginHandler.raiseFirstLetter(newname);
                final LoginServerWebConnection lsw = new LoginServerWebConnection();
                final String toReturn = lsw.renamePlayer(oldname, newname, newpw, this.getResponder().getPower());
                responder.getCommunicator().sendNormalServerMessage("You try to change the name from " + oldname + " to " + newname + " and set the password to '" + newpw + "'.");
                responder.getCommunicator().sendNormalServerMessage("The result is:");
                responder.getCommunicator().sendNormalServerMessage(toReturn);
                if (!toReturn.contains("Error.")) {
                    Items.destroyItem(this.certificate.getWurmId());
                    ChangeNameQuestion.logger.info(oldname + " (" + this.getResponder().getWurmId() + ") changed " + this.getResponder().getHisHerItsString() + " name to " + newname + '.');
                    Server.getInstance().broadCastSafe(oldname + " changed " + this.getResponder().getHisHerItsString() + " name to " + newname + '.');
                    this.getResponder().refreshVisible();
                }
            }
        }
        else {
            responder.getCommunicator().sendNormalServerMessage("The name  " + newname + " is illegal.");
        }
    }
    
    @Override
    public void sendQuestion() {
        final StringBuilder buf = new StringBuilder(this.getBmlHeader());
        buf.append("text{text=\"The name change system is spread across several servers and the name is used in a lot of complex situations.\"}");
        buf.append("text{text=\"It will not work perfectly and there will be certain data loss, especially regarding signatures and statistics.\"}");
        buf.append("text{text=\"In case you are not prepared to risk this you should close this window and sell the certificate back.\"}");
        buf.append("text{text=\"What would you like your name to be?\"}");
        buf.append("input{id=\"answer\";maxchars=\"40\";text=\"" + this.getResponder().getName() + "\"}");
        buf.append("text{text=\"Your password is required for security reasons. You can keep your old password.\"}");
        buf.append("harray{label{text=\"Old password\"};input{id=\"oldpw\";maxchars=\"40\";text=\"\"};}");
        buf.append("harray{label{text=\"New password\"};input{id=\"newpw\";maxchars=\"40\";text=\"\"};}");
        buf.append("text{text=\"\"}");
        buf.append(this.createAnswerButton2());
        this.getResponder().getCommunicator().sendBml(300, 300, true, true, buf.toString(), 200, 200, 200, this.title);
    }
    
    static {
        logger = Logger.getLogger(ChangeNameQuestion.class.getName());
    }
}
