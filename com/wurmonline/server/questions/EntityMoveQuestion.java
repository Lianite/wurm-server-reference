// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.questions;

import com.wurmonline.server.LoginServerWebConnection;
import com.wurmonline.server.Server;
import com.wurmonline.server.deities.Deity;
import com.wurmonline.server.epic.EpicServerStatus;
import java.util.logging.Level;
import com.wurmonline.server.deities.Deities;
import java.util.Properties;
import com.wurmonline.server.creatures.Creature;
import java.util.logging.Logger;
import com.wurmonline.server.epic.MapHex;

public final class EntityMoveQuestion extends Question
{
    private Integer[] neighbours;
    private MapHex currentHex;
    private int deityToGuide;
    private boolean secondStep;
    private static final Logger logger;
    
    public EntityMoveQuestion(final Creature aResponder) {
        super(aResponder, "Guide the deities", "Whereto will you guide your deity?", 113, -10L);
        this.deityToGuide = -1;
        this.secondStep = false;
    }
    
    @Override
    public void answer(final Properties answers) {
        if (this.getResponder().getKarma() < 5000) {
            this.getResponder().getCommunicator().sendNormalServerMessage("You do not have enough karma to commune with " + this.getResponder().getDeity().getName() + ".");
            return;
        }
        final String deityString = answers.getProperty("deityId");
        if (!this.secondStep) {
            if (deityString != null && deityString.length() > 0) {
                try {
                    final int deityId = Integer.parseInt(deityString);
                    if (deityId < 0) {
                        this.getResponder().getCommunicator().sendNormalServerMessage("You refrain from disturbing the gods at this time.");
                        return;
                    }
                    final Deity deity = Deities.getDeity(deityId);
                    if (this.getResponder().getDeity() != null && deity != null) {
                        final EntityMoveQuestion nem = new EntityMoveQuestion(this.getResponder());
                        nem.secondStep = true;
                        nem.deityToGuide = deityId;
                        nem.sendHexQuestion();
                        return;
                    }
                    this.getResponder().getCommunicator().sendAlertServerMessage("You fail to commune with the gods...");
                }
                catch (NumberFormatException nfre) {
                    this.getResponder().getCommunicator().sendNormalServerMessage("Not a number for the desired deity...");
                    EntityMoveQuestion.logger.log(Level.INFO, "Not a number " + deityString);
                }
            }
            else {
                this.getResponder().getCommunicator().sendNormalServerMessage("You refrain from disturbing the gods at this time.");
            }
        }
        else if (this.getResponder().getDeity() != null) {
            final Deity deity2 = Deities.getDeity(this.deityToGuide);
            if (deity2 == null) {
                this.getResponder().getCommunicator().sendNormalServerMessage("Not a number for the desired deity...");
                return;
            }
            final String val = answers.getProperty("sethex");
            if (val != null && val.length() > 0) {
                try {
                    final int hexnum = Integer.parseInt(val);
                    if (hexnum < 0) {
                        this.getResponder().getCommunicator().sendNormalServerMessage("You refrain from disturbing the gods at this time.");
                        return;
                    }
                    boolean ok = false;
                    for (final Integer hexes : this.neighbours) {
                        if (hexes == hexnum) {
                            ok = true;
                            break;
                        }
                    }
                    if (ok) {
                        final MapHex hex = EpicServerStatus.getValrei().getMapHex(hexnum);
                        if (hex != null) {
                            this.getResponder().getCommunicator().sendNormalServerMessage("You attempt to guide your deity..");
                            new Thread(this.getResponder().getName() + "-guides-" + deity2.getName() + "-Thread") {
                                @Override
                                public final void run() {
                                    boolean success = Server.rand.nextFloat() < 0.7f;
                                    if (success) {
                                        final LoginServerWebConnection lsw = new LoginServerWebConnection();
                                        success = lsw.requestDeityMove(EntityMoveQuestion.this.deityToGuide, hexnum, EntityMoveQuestion.this.getResponder().getName());
                                        try {
                                            Thread.sleep(2000L);
                                        }
                                        catch (InterruptedException ex) {}
                                        if (success) {
                                            EntityMoveQuestion.logger.log(Level.INFO, EntityMoveQuestion.this.getResponder().getName() + " guides " + deity2.getName());
                                            EntityMoveQuestion.this.getResponder().getCommunicator().sendSafeServerMessage("... and " + deity2.getName() + " heeds your advice!");
                                            EntityMoveQuestion.this.getResponder().modifyKarma(-5000);
                                        }
                                        else {
                                            EntityMoveQuestion.this.getResponder().getCommunicator().sendNormalServerMessage("... but fail to penetrate the ether to Valrei.");
                                            EntityMoveQuestion.logger.log(Level.INFO, EntityMoveQuestion.this.getResponder().getName() + " guiding but connection to " + deity2.getName() + " broken.");
                                        }
                                    }
                                    else {
                                        try {
                                            Thread.sleep(3000L);
                                        }
                                        catch (InterruptedException ex2) {}
                                        EntityMoveQuestion.this.getResponder().getCommunicator().sendNormalServerMessage("... but you are ignored.");
                                        EntityMoveQuestion.this.getResponder().modifyKarma(-2500);
                                        EntityMoveQuestion.logger.log(Level.INFO, EntityMoveQuestion.this.getResponder().getName() + " guiding ignored by " + deity2.getName() + ".");
                                    }
                                }
                            }.start();
                        }
                    }
                }
                catch (NumberFormatException nfre2) {
                    this.getResponder().getCommunicator().sendNormalServerMessage("Not a number for the desired position...");
                    EntityMoveQuestion.logger.log(Level.INFO, "Not a number " + val);
                }
            }
            else {
                this.getResponder().getCommunicator().sendNormalServerMessage("You refrain from disturbing the gods at this time.");
            }
        }
        else {
            this.getResponder().getCommunicator().sendNormalServerMessage("You no longer pray to a deity.");
        }
    }
    
    @Override
    public void sendQuestion() {
        final StringBuilder buf = new StringBuilder();
        buf.append(this.getBmlHeader());
        if (this.getResponder().getDeity() != null) {
            buf.append("text{text=\"You may spend karma in order to envision Valrei and attempt to guide your deity.\"}text{text=\"\"}");
            buf.append("text{text=\"There is 70% chance that you succed in getting your deities attention, and the cost will be 5000 karma if you do.\"}text{text=\"\"}");
            buf.append("text{text=\"If the request fails, you will only lose 2500 karma.\"}text{text=\"\"}");
            buf.append("radio{ group='deityId'; id='0';text='Do not Guide';selected='true'}");
            if (this.getResponder().getKingdomTemplateId() == 3) {
                buf.append("radio{ group='deityId'; id='4';text='Guide Libila'}");
            }
            else if (this.getResponder().getKingdomTemplateId() == 2) {
                buf.append("radio{ group='deityId'; id='2';text='Guide Magranon'}");
            }
            else if (this.getResponder().getKingdomTemplateId() == 1) {
                if (this.getResponder().getDeity().number == 3) {
                    buf.append("radio{ group='deityId'; id='1';text='Guide Fo'}");
                }
                else {
                    buf.append("radio{ group='deityId'; id='1';text='Guide Fo'}");
                }
            }
        }
        else {
            buf.append("text{text=\"You no longer pray to a deity.\"}text{text=\"\"}");
        }
        buf.append(this.createAnswerButton2());
        this.getResponder().getCommunicator().sendBml(300, 300, true, true, buf.toString(), 200, 200, 200, this.title);
    }
    
    public final void sendHexQuestion() {
        final StringBuilder buf = new StringBuilder();
        buf.append(this.getBmlHeader());
        final Integer currentInt = Deities.getPosition(this.deityToGuide);
        final Deity deity = Deities.getDeity(this.deityToGuide);
        buf.append("text{text=\"Where do you want " + deity.getName() + " to go?\"}text{text=\"\"}");
        buf.append("radio{ group='sethex'; id=\"-1\";text=\"Never mind...\";selected=\"true\"};");
        if (currentInt != null) {
            this.currentHex = EpicServerStatus.getValrei().getMapHex((int)currentInt);
            if (this.currentHex != null) {
                this.neighbours = this.currentHex.getNearMapHexes();
                for (final Integer i : this.neighbours) {
                    final MapHex maphex = EpicServerStatus.getValrei().getMapHex((int)i);
                    if (maphex != null) {
                        final String trap = maphex.isTrap() ? " (trap)" : "";
                        final String slow = maphex.isSlow() ? " (slow)" : "";
                        final String teleport = maphex.isTeleport() ? " (shift)" : "";
                        final String strength = maphex.isStrength() ? " (strength)" : "";
                        final String vitality = maphex.isVitality() ? " (vitality)" : "";
                        buf.append("radio{ group='sethex'; id=\"" + (int)i + "\";text=\"" + maphex.getName() + trap + slow + teleport + strength + vitality + "\"};");
                    }
                    else {
                        EntityMoveQuestion.logger.log(Level.WARNING, "NO HEX ON VALREI FOR " + (int)i);
                    }
                }
                if (this.neighbours == null || this.neighbours.length == 0) {
                    buf.append("text{text=\"" + deity.getName() + " is not available for guidance now.\"}text{text=\"\"}");
                }
            }
            else {
                buf.append("text{text=\"" + deity.getName() + " is not available for guidance now.\"}text{text=\"\"}");
            }
        }
        else {
            buf.append("text{text=\"" + deity.getName() + " is not available for guidance now.\"}text{text=\"\"}");
        }
        buf.append(this.createAnswerButton2());
        this.getResponder().getCommunicator().sendBml(300, 300, true, true, buf.toString(), 200, 200, 200, this.title);
    }
    
    static {
        logger = Logger.getLogger(EntityMoveQuestion.class.getName());
    }
}
