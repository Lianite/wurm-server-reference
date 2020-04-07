// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.questions;

import com.wurmonline.server.ServerEntry;
import com.wurmonline.server.players.PlayerInfo;
import java.io.IOException;
import com.wurmonline.server.LoginServerWebConnection;
import com.wurmonline.server.kingdom.Kingdoms;
import com.wurmonline.server.Players;
import com.wurmonline.server.Servers;
import com.wurmonline.server.players.PlayerInfoFactory;
import com.wurmonline.server.LoginHandler;
import java.util.Properties;
import com.wurmonline.server.creatures.Creature;
import com.wurmonline.server.items.Item;
import com.wurmonline.server.economy.MonetaryConstants;

public final class MailSendQuestion extends Question implements MonetaryConstants
{
    private final Item mailbox;
    private final Item[] items;
    
    public MailSendQuestion(final Creature aResponder, final String aTitle, final String aQuestion, final Item aMailbox) {
        super(aResponder, aTitle, aQuestion, 54, aMailbox.getWurmId());
        this.mailbox = aMailbox;
        this.items = this.mailbox.getItemsAsArray();
    }
    
    public static boolean validateMailboxContents(final Item[] items, final Item mailbox) {
        if (items.length != mailbox.getItems().size()) {
            return false;
        }
        for (final Item i : items) {
            if (!mailbox.getItems().contains(i)) {
                return false;
            }
        }
        return true;
    }
    
    @Override
    public void answer(final Properties answers) {
        final String nosend = answers.getProperty("dontsend");
        if (!validateMailboxContents(this.items, this.mailbox)) {
            this.getResponder().getCommunicator().sendNormalServerMessage("The items in the mailbox have changed. Please try sending again.");
            return;
        }
        if (nosend != null && nosend.equals("true")) {
            this.getResponder().getCommunicator().sendNormalServerMessage("You decide to not send the shipment yet.");
        }
        else {
            String name = answers.getProperty("IDrecipient");
            if (name != null && name.length() > 2) {
                if (LoginHandler.containsIllegalCharacters(name)) {
                    this.getResponder().getCommunicator().sendNormalServerMessage("The name of the receiver contains illegal characters. Please check the name.");
                }
                else {
                    name = LoginHandler.raiseFirstLetter(name);
                    final PlayerInfo pinf = PlayerInfoFactory.createPlayerInfo(name);
                    long[] info = { Servers.localServer.id, pinf.wurmId };
                    try {
                        pinf.load();
                        info = new long[] { Servers.localServer.id, pinf.wurmId };
                        final byte kingdom = Players.getInstance().getKingdomForPlayer(pinf.wurmId);
                        if (kingdom != this.getResponder().getKingdomId()) {
                            final String kname = Kingdoms.getNameFor(kingdom);
                            this.getResponder().getCommunicator().sendNormalServerMessage(pinf.getName() + " is with the " + kname + ". You may not trade with the enemy.");
                            return;
                        }
                        if (this.getResponder().getEnemyPresense() > 0) {
                            this.getResponder().getCommunicator().sendNormalServerMessage("You cannot send mail while there is an enemy nearby.");
                            return;
                        }
                        final LoginServerWebConnection lsw = new LoginServerWebConnection();
                        try {
                            info = lsw.getCurrentServer(name, -1L);
                        }
                        catch (Exception e) {
                            this.getResponder().getCommunicator().sendNormalServerMessage("That island is not available currently. Please try again later.");
                            return;
                        }
                    }
                    catch (IOException iox) {
                        final LoginServerWebConnection lsw = new LoginServerWebConnection();
                        try {
                            info = lsw.getCurrentServer(name, -1L);
                        }
                        catch (Exception e) {
                            info = new long[] { -1L, -1L };
                        }
                    }
                    if (info[1] > 0L && (int)info[0] > 0) {
                        final boolean sameServer = info[0] == Servers.localServer.id;
                        final boolean[] cods = new boolean[this.items.length];
                        for (int x = 0; x < this.items.length; ++x) {
                            final String val = answers.getProperty(x + "cod");
                            if (val != null && val.equals("true")) {
                                cods[x] = true;
                            }
                            else {
                                cods[x] = false;
                            }
                            if (this.items[x].getTemplateId() == 651) {
                                this.getResponder().getCommunicator().sendNormalServerMessage("The spirits refuse to touch the " + this.items[x].getName() + " since they don't know what's in it.");
                                return;
                            }
                            if (this.items[x].isCoin()) {
                                this.getResponder().getCommunicator().sendNormalServerMessage("The spirits refuse to touch the coin.");
                                return;
                            }
                            if (this.items[x].isBanked()) {
                                this.getResponder().getCommunicator().sendNormalServerMessage("The " + this.items[x].getName() + " is currently unavailable.");
                                return;
                            }
                            if (this.items[x].isUnfinished()) {
                                this.getResponder().getCommunicator().sendNormalServerMessage("The spirits seem afraid to break the " + this.items[x].getName() + ".");
                                return;
                            }
                            if (!sameServer) {
                                final boolean changingCluster = false;
                                final ServerEntry entry = Servers.getServerWithId((int)info[0]);
                                if (entry == null) {
                                    this.getResponder().getCommunicator().sendNormalServerMessage("You can not mail the " + this.items[x].getName() + " that far.");
                                    return;
                                }
                                if (changingCluster) {
                                    this.getResponder().getCommunicator().sendNormalServerMessage("You can not mail that far.");
                                    return;
                                }
                                if (!this.items[x].willLeaveServer(false, changingCluster, false)) {
                                    this.getResponder().getCommunicator().sendNormalServerMessage("You can not mail the " + this.items[x].getName() + " that far.");
                                    return;
                                }
                                final Item[] contained = this.items[x].getAllItems(true);
                                for (int c = 0; c < contained.length; ++c) {
                                    if (!contained[c].willLeaveServer(false, changingCluster, false)) {
                                        this.getResponder().getCommunicator().sendNormalServerMessage("You can not mail the " + contained[c].getName() + " that far.");
                                        return;
                                    }
                                    if (!this.isContainerMailItemOk(contained[c], this.items[x])) {
                                        return;
                                    }
                                }
                            }
                            else {
                                final Item[] contained2 = this.items[x].getAllItems(true);
                                for (int c2 = 0; c2 < contained2.length; ++c2) {
                                    if (!this.isContainerMailItemOk(contained2[c2], this.items[x])) {
                                        return;
                                    }
                                }
                            }
                        }
                        final MailSendConfirmQuestion msc = new MailSendConfirmQuestion(this.getResponder(), "Confirm the price", "Check the price of the shipment and set C.O.D prices:", this.mailbox, this.items, cods, name, info);
                        msc.sendQuestion();
                    }
                    else {
                        this.getResponder().getCommunicator().sendNormalServerMessage("Unknown recipient '" + name + "'.");
                    }
                }
            }
            else {
                this.getResponder().getCommunicator().sendNormalServerMessage("Unknown recipient. Please try again.");
            }
        }
    }
    
    protected final boolean isContainerMailItemOk(final Item item, final Item container) {
        if (item.isArtifact() || item.isRoyal()) {
            this.getResponder().getCommunicator().sendAlertServerMessage("You can not mail the " + item.getName() + " (in the " + container.getName() + "). The spirits refuse to deal with it.");
            return false;
        }
        if (item.getTemplateId() == 651) {
            this.getResponder().getCommunicator().sendNormalServerMessage("The gift boxes are not handled by the mail service.");
            return false;
        }
        if (item.isCoin()) {
            this.getResponder().getCommunicator().sendNormalServerMessage("Coins are currently not handled by the mail service.");
            return false;
        }
        if (item.isBanked()) {
            this.getResponder().getCommunicator().sendNormalServerMessage("The " + item.getName() + " is currently unavailable.");
            return false;
        }
        if (item.isUnfinished()) {
            this.getResponder().getCommunicator().sendNormalServerMessage("Unfinished items would be broken by the mail service.");
            return false;
        }
        return true;
    }
    
    @Override
    public void sendQuestion() {
        final String lHtml = this.getBmlHeader();
        final StringBuilder buf = new StringBuilder(lHtml);
        buf.append("text{type='bold';text='Use mailboxes with caution and inexpensive items until you rely on the spirits. They are still learning and take no responsibility for lost items.'};");
        buf.append("text{text='Here you decide who you want to send the contains of the mailbox to, and whether they should be C.O.D.'};");
        buf.append("text{text='If an item should be C.O.D (Cash On Delivery, paid by the receiver), check the corresponding checkbox.'};");
        buf.append("text{text='Note that sending C.O.D with an intent to scam other players is a bannable offense.'};");
        buf.append("label{text=\"Select the recipient:\"};");
        buf.append("input{text='';maxchars='40'; id='IDrecipient'};");
        buf.append("text{text=''}");
        buf.append("text{text='The costs are shown on the next screen. It is safe to click send here, but if you click send on the next screen, the goods will be sent.'};");
        buf.append("checkbox{text='Check this if you do not want to go to the next screen';id='dontsend'};");
        buf.append("table{rows='" + (this.items.length + 1) + "'; cols='4';label{text='Item name'};label{text='QL'};label{text='DAM'};label{text='C.O.D'};");
        for (int x = 0; x < this.items.length; ++x) {
            buf.append(Question.itemNameWithColorByRarity(this.items[x]));
            buf.append("label{text=\"" + String.format("%.2f", this.items[x].getQualityLevel()) + "\"};");
            buf.append("label{text=\"" + String.format("%.2f", this.items[x].getDamage()) + "\"};");
            buf.append("checkbox{id='" + x + "cod'};");
        }
        buf.append("};");
        buf.append(this.createAnswerButton2());
        this.getResponder().getCommunicator().sendBml(500, 400, true, true, buf.toString(), 200, 200, 200, this.title);
    }
}
