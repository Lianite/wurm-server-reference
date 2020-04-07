// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.questions;

import com.wurmonline.server.items.InscriptionData;
import com.wurmonline.server.intra.PlayerTransfer;
import java.io.OutputStream;
import java.io.DataOutputStream;
import java.io.ByteArrayOutputStream;
import javax.annotation.Nullable;
import com.wurmonline.server.ServerEntry;
import java.util.Set;
import com.wurmonline.server.economy.Change;
import java.io.IOException;
import java.util.logging.Level;
import com.wurmonline.server.LoginServerWebConnection;
import com.wurmonline.server.NoSuchItemException;
import com.wurmonline.server.Items;
import com.wurmonline.server.items.WurmMail;
import java.util.HashSet;
import com.wurmonline.server.Servers;
import java.util.Properties;
import com.wurmonline.server.LoginHandler;
import com.wurmonline.server.creatures.Creature;
import com.wurmonline.server.items.Item;
import java.util.logging.Logger;
import com.wurmonline.server.TimeConstants;
import com.wurmonline.server.MiscConstants;
import com.wurmonline.server.economy.MonetaryConstants;

public final class MailSendConfirmQuestion extends Question implements MonetaryConstants, MiscConstants, TimeConstants
{
    private static final Logger logger;
    private final Item mailbox;
    private final Item[] items;
    private final String receiver;
    private final boolean[] cods;
    private long fullprice;
    private final long receiverId;
    private final int targetServer;
    
    MailSendConfirmQuestion(final Creature aResponder, final String aTitle, final String aQuestion, final Item aMailbox, final Item[] aItems, final boolean[] aCods, final String aReceiver, final long[] aReceiverInfo) {
        super(aResponder, aTitle, aQuestion, 55, aMailbox.getWurmId());
        this.fullprice = 0L;
        this.mailbox = aMailbox;
        this.items = aItems;
        this.receiver = LoginHandler.raiseFirstLetter(aReceiver);
        this.cods = aCods;
        this.receiverId = aReceiverInfo[1];
        this.targetServer = (int)aReceiverInfo[0];
    }
    
    @Override
    public void answer(final Properties answers) {
        if (!MailSendQuestion.validateMailboxContents(this.items, this.mailbox)) {
            this.getResponder().getCommunicator().sendNormalServerMessage("The items in the mailbox have changed. Please try sending again.");
            return;
        }
        if (this.getResponder().getMoney() < this.fullprice) {
            this.getResponder().getCommunicator().sendNormalServerMessage("You can not afford sending the packages.");
        }
        else if (!Servers.loginServer.isAvailable(5, true)) {
            this.getResponder().getCommunicator().sendNormalServerMessage("You may not send mail right now. The service is unavailable.");
        }
        else {
            boolean charge = false;
            boolean revert = false;
            int codprice = 0;
            final boolean local = Servers.localServer.id == this.targetServer;
            WurmMail mail = null;
            int revertx = 0;
            Set<WurmMail> mails = null;
            Set<Item> mailitems = null;
            if (!local) {
                final boolean changingCluster = false;
                final ServerEntry entry = Servers.getServerWithId(this.targetServer);
                if (entry == null) {
                    this.getResponder().getCommunicator().sendNormalServerMessage("You can not mail that far.");
                    return;
                }
                if (changingCluster) {
                    this.getResponder().getCommunicator().sendNormalServerMessage("You can not mail that far.");
                    return;
                }
                if (!entry.isConnected()) {
                    this.getResponder().getCommunicator().sendNormalServerMessage("That island is not available currently. Please try again later.");
                    return;
                }
                mails = new HashSet<WurmMail>();
                mailitems = new HashSet<Item>();
            }
            long timeavail = System.currentTimeMillis() + (101 - (int)this.mailbox.getSpellCourierBonus()) * 60000L;
            if (this.getResponder().getPower() > 0) {
                timeavail = System.currentTimeMillis() + 60000L;
            }
            Item realItem = null;
            for (int x = 0; x < this.items.length; ++x) {
                try {
                    realItem = Items.getItem(this.items[x].getWurmId());
                    if (this.items[x].getTemplateId() == 651) {
                        this.getResponder().getCommunicator().sendNormalServerMessage("The gift boxes are not handled by the mail service.");
                        return;
                    }
                    if (this.items[x].isCoin()) {
                        this.getResponder().getCommunicator().sendNormalServerMessage("Coins are currently not handled by the mail service.");
                        return;
                    }
                    if (this.items[x].isBanked()) {
                        this.getResponder().getCommunicator().sendNormalServerMessage("The " + this.items[x].getName() + " is currently unavailable.");
                        return;
                    }
                    if (this.items[x].isUnfinished()) {
                        this.getResponder().getCommunicator().sendNormalServerMessage("Unfinished items would be broken by the mail service.");
                        return;
                    }
                    if ((this.items[x].getTemplateId() == 665 && this.items[x].isLocked()) || (this.items[x].getTemplateId() == 192 && this.items[x].isLocked())) {
                        this.getResponder().getCommunicator().sendNormalServerMessage(this.items[x].getNameWithGenus() + " cannot be mailed while locked.");
                        return;
                    }
                    codprice = 0;
                    String key = "";
                    String val = "";
                    if (this.cods[x]) {
                        key = x + "g";
                        val = answers.getProperty(key);
                        if (val != null && val.length() > 0) {
                            try {
                                codprice = Integer.parseInt(val) * 1000000;
                            }
                            catch (NumberFormatException nfe) {
                                this.getResponder().getCommunicator().sendNormalServerMessage("Failed to set the gold price for " + realItem.getName() + ". Note that a coin value is in whole numbers, no decimals.");
                            }
                        }
                        key = x + "s";
                        val = answers.getProperty(key);
                        if (val != null && val.length() > 0) {
                            try {
                                codprice += Integer.parseInt(val) * 10000;
                            }
                            catch (NumberFormatException nfe) {
                                this.getResponder().getCommunicator().sendNormalServerMessage("Failed to set a silver price for " + realItem.getName() + ". Note that a coin value is in whole numbers, no decimals.");
                            }
                        }
                        key = x + "c";
                        val = answers.getProperty(key);
                        if (val != null && val.length() > 0) {
                            try {
                                codprice += Integer.parseInt(val) * 100;
                            }
                            catch (NumberFormatException nfe) {
                                this.getResponder().getCommunicator().sendNormalServerMessage("Failed to set a copper price for " + realItem.getName() + ". Note that a coin value is in whole numbers, no decimals.");
                            }
                        }
                        key = x + "i";
                        val = answers.getProperty(key);
                        if (val != null && val.length() > 0) {
                            try {
                                codprice += Integer.parseInt(val);
                            }
                            catch (NumberFormatException nfe) {
                                this.getResponder().getCommunicator().sendNormalServerMessage("Failed to set an iron price for " + realItem.getName() + ". Note that a coin value is in whole numbers, no decimals.");
                            }
                        }
                        if (codprice <= 0) {
                            codprice = 1;
                            this.getResponder().getCommunicator().sendNormalServerMessage("Cod price set to 1 iron, since it was negative or zero.");
                        }
                        mail = new WurmMail((byte)1, realItem.getWurmId(), this.getResponder().getWurmId(), this.receiverId, codprice, timeavail, System.currentTimeMillis() + (Servers.localServer.testServer ? 3600000L : 604800000L) * 2L, Servers.localServer.id, false, false);
                    }
                    else {
                        charge = true;
                        mail = new WurmMail((byte)0, realItem.getWurmId(), this.getResponder().getWurmId(), this.receiverId, codprice, timeavail, System.currentTimeMillis() + (Servers.localServer.testServer ? 3600000L : 604800000L) * 2L, Servers.localServer.id, false, false);
                    }
                    if (local) {
                        WurmMail.addWurmMail(mail);
                        mail.createInDatabase();
                    }
                    else {
                        mails.add(mail);
                    }
                    if (realItem.getParentId() != this.mailbox.getWurmId()) {
                        revert = true;
                        this.getResponder().getCommunicator().sendAlertServerMessage("The " + realItem.getName() + " is no longer in the mailbox!");
                        break;
                    }
                    realItem.putInVoid();
                    realItem.setMailed(true);
                    realItem.setMailTimes((byte)(realItem.getMailTimes() + 1));
                    final Item[] contained = realItem.getAllItems(true);
                    for (int c = 0; c < contained.length; ++c) {
                        contained[c].setMailed(true);
                        contained[c].setMailTimes((byte)(contained[c].getMailTimes() + 1));
                        if (!local) {
                            mailitems.add(contained[c]);
                        }
                    }
                    if (!local) {
                        mailitems.add(realItem);
                    }
                    revertx = x;
                }
                catch (NoSuchItemException nsi) {
                    revert = true;
                    this.getResponder().getCommunicator().sendAlertServerMessage("The " + this.items[x].getName() + " is no longer in the mailbox!");
                    break;
                }
            }
            if (!local) {
                revert = sendMailSetToServer(this.getResponder().getWurmId(), this.getResponder(), this.targetServer, mails, this.receiverId, mailitems.toArray(new Item[mailitems.size()]));
            }
            if (revert) {
                charge = false;
            }
            if (charge) {
                final LoginServerWebConnection lsw = new LoginServerWebConnection();
                final long newBalance = lsw.chargeMoney(this.getResponder().getName(), this.fullprice);
                if (newBalance < 0L) {
                    this.getResponder().getCommunicator().sendAlertServerMessage("The spirits seem to deliver for free this time.");
                    MailSendConfirmQuestion.logger.log(Level.WARNING, "Failed to withdraw " + this.fullprice + " iron from " + this.getResponder().getName() + ". Mail was free.");
                }
                else {
                    try {
                        this.getResponder().setMoney(newBalance);
                    }
                    catch (IOException iox) {
                        MailSendConfirmQuestion.logger.log(Level.WARNING, this.getResponder().getName() + " " + iox.getMessage(), iox);
                    }
                    this.getResponder().getCommunicator().sendNormalServerMessage("You have been charged " + new Change(this.fullprice).getChangeString() + ".");
                }
            }
            if (revert) {
                for (int x = 0; x < revertx + 1; ++x) {
                    try {
                        realItem = Items.getItem(this.items[x].getWurmId());
                        if (realItem.getParentId() != this.mailbox.getWurmId()) {
                            WurmMail.removeMail(this.items[x].getWurmId());
                            realItem.setMailed(false);
                            realItem.setMailTimes((byte)(realItem.getMailTimes() - 1));
                            final Item[] contained2 = realItem.getAllItems(true);
                            for (int c2 = 0; c2 < contained2.length; ++c2) {
                                contained2[c2].setMailed(false);
                                contained2[c2].setMailTimes((byte)(contained2[c2].getMailTimes() - 1));
                                contained2[c2].setLastOwnerId(this.getResponder().getWurmId());
                            }
                        }
                    }
                    catch (NoSuchItemException ex) {}
                }
            }
            else {
                String time = "just under two hours.";
                final float bon = this.mailbox.getSpellCourierBonus();
                if (bon > 90.0f) {
                    time = "less than ten minutes.";
                }
                else if (bon > 70.0f) {
                    time = "less than thirty minutes.";
                }
                else if (bon > 40.0f) {
                    time = "less than an hour.";
                }
                else if (bon > 10.0f) {
                    time = "a bit more than an hour.";
                }
                this.getResponder().getCommunicator().sendNormalServerMessage("The items silently disappear from the " + this.mailbox.getName() + ". You expect them to arrive in " + time);
                if (!local) {
                    for (int x2 = 0; x2 < this.items.length; ++x2) {
                        final Item[] contained = this.items[x2].getAllItems(true);
                        for (int c = 0; c < contained.length; ++c) {
                            MailSendConfirmQuestion.logger.log(Level.INFO, this.getResponder().getName() + " destroying contained " + contained[c].getName() + ", ql " + contained[c].getQualityLevel() + " wid=" + contained[c].getWurmId());
                            Items.destroyItem(contained[c].getWurmId(), true, true);
                        }
                        MailSendConfirmQuestion.logger.log(Level.INFO, this.getResponder().getName() + " destroying " + this.items[x2].getName() + ", ql " + this.items[x2].getQualityLevel() + " wid=" + this.items[x2].getWurmId());
                        Items.destroyItem(this.items[x2].getWurmId(), true, true);
                    }
                }
            }
        }
    }
    
    public static final boolean sendMailSetToServer(final long senderId, @Nullable final Creature responder, final int targetServer, final Set<WurmMail> mails, final long receiverId, final Item[] items) {
        boolean revert = false;
        LoginServerWebConnection lsw = null;
        final ServerEntry entry = Servers.getServerWithId(targetServer);
        if (entry != null) {
            if (entry.isAvailable(5, true)) {
                lsw = new LoginServerWebConnection(targetServer);
            }
            else {
                if (responder != null) {
                    responder.getCommunicator().sendNormalServerMessage("The inter-island mail service is on strike right now. Please try later.");
                }
                revert = true;
            }
        }
        else {
            lsw = new LoginServerWebConnection();
        }
        if (!revert) {
            final WurmMail[] mailarr = mails.toArray(new WurmMail[mails.size()]);
            final ByteArrayOutputStream bos = new ByteArrayOutputStream();
            final ByteArrayOutputStream bos2 = new ByteArrayOutputStream();
            try {
                final DataOutputStream dos = new DataOutputStream(bos);
                final DataOutputStream dos2 = new DataOutputStream(bos2);
                dos.writeInt(items.length);
                for (int x = 0; x < items.length; ++x) {
                    if (responder != null) {
                        MailSendConfirmQuestion.logger.log(Level.INFO, responder.getName() + " sending " + items[x].getName() + ", ql " + items[x].getQualityLevel() + " wid=" + items[x].getWurmId() + " to " + ((entry != null) ? entry.getName() : targetServer));
                    }
                    else {
                        MailSendConfirmQuestion.logger.log(Level.INFO, senderId + " sending " + items[x].getName() + ", ql " + items[x].getQualityLevel() + " wid=" + items[x].getWurmId() + " to " + ((entry != null) ? entry.getName() : targetServer));
                    }
                    PlayerTransfer.sendItem(items[x], dos, false);
                }
                dos.flush();
                dos.close();
                dos2.writeInt(mailarr.length);
                for (int x = 0; x < mailarr.length; ++x) {
                    dos2.writeByte(mailarr[x].type);
                    dos2.writeLong(mailarr[x].itemId);
                    dos2.writeLong(mailarr[x].sender);
                    dos2.writeLong(mailarr[x].receiver);
                    dos2.writeLong(mailarr[x].price);
                    dos2.writeLong(mailarr[x].sent);
                    dos2.writeLong(mailarr[x].expiration);
                    dos2.writeInt(mailarr[x].sourceserver);
                    dos2.writeBoolean(mailarr[x].rejected);
                }
                dos2.flush();
                dos2.close();
            }
            catch (Exception ex) {
                MailSendConfirmQuestion.logger.log(Level.WARNING, ex.getMessage(), ex);
            }
            final byte[] itemdata = bos.toByteArray();
            final byte[] maildata = bos2.toByteArray();
            final String result = lsw.sendMail(maildata, itemdata, senderId, receiverId, targetServer);
            if (result.length() != 0) {
                revert = true;
                if (responder != null) {
                    responder.getCommunicator().sendAlertServerMessage("The spirits in the mailbox reported a problem when sending the contents of the mailbox. Reverting. The message was: " + result);
                    MailSendConfirmQuestion.logger.log(Level.WARNING, responder.getName() + ", " + result);
                }
            }
        }
        return revert;
    }
    
    public static final int getCostForItem(final Item i, final float priceMod) {
        if (i.isComponentItem()) {
            return 0;
        }
        if (i.getTemplateId() == 1392) {
            return 0;
        }
        int pcost = 10;
        int combinePriceMod = 1;
        if (i.getTemplateId() == 748 || i.getTemplateId() == 1272 || i.getTemplateId() == 1403) {
            final InscriptionData insData = i.getInscription();
            if (insData != null) {
                if (insData.hasBeenInscribed()) {
                    return 1;
                }
            }
            else {
                final Item parent = i.getParentOrNull();
                if (parent != null && (parent.getTemplateId() == 1409 || parent.getTemplateId() == 1404 || parent.getTemplateId() == 1127 || parent.getTemplateId() == 1128)) {
                    return 1;
                }
            }
        }
        if (i.isCombine() || i.isLiquid()) {
            combinePriceMod = Math.max(1, i.getWeightGrams() / 5000);
        }
        pcost *= combinePriceMod;
        pcost *= (int)priceMod;
        return pcost * 1 * 10;
    }
    
    @Override
    public void sendQuestion() {
        final float priceMod = 1.0f;
        final String lHtml = this.getBmlHeader();
        final StringBuilder buf = new StringBuilder(lHtml);
        buf.append("text{text='This should give you an overview of how much the cost will be for sending the contents of the mailbox, visible on the bottom of this dialogue.'}");
        buf.append("text{text='C.O.D means Cash On Delivery, which is good for selling items to other players. A C.O.D item costs nothing for you to send.'}");
        buf.append("text{text='The Mail cost is what the spirits charge to deliver the item. The C.O.D cost payed by the receiver will be what you enter plus the Mail cost.'}");
        buf.append("text{text='Example: You check the checkbox for a Mallet which say has a 1 copper coins Mail cost.'}");
        buf.append("text{text='You enter 20 in the C.O.D copper coins textbox. This means the receiver will have to pay 21 copper in all in order to receive the mallet of which you receive 20.'}");
        buf.append("text{type='italic';text='Note that if a C.O.D receiver returns the item you have to pay a 1 copper (or 1 iron for paper/papryus) fee to retrieve it.'}");
        buf.append("text{text='If the item is rejected you have two weeks to pick it up or it will be destroyed by the spirits since it conflicts with their banking policy.'}");
        buf.append("text{text=''};");
        this.fullprice = 0L;
        for (int x = 0; x < this.items.length; ++x) {
            if (!this.cods[x]) {
                int pcost = getCostForItem(this.items[x], priceMod);
                this.fullprice += pcost;
                final Item[] contained = this.items[x].getAllItems(true);
                for (int c = 0; c < contained.length; ++c) {
                    pcost = getCostForItem(contained[c], priceMod);
                    this.fullprice += pcost;
                }
            }
        }
        Change change = new Change(this.fullprice);
        buf.append("text{type='bold';text=\"The cost for sending these items will be " + change.getChangeString() + ".\"};");
        if (this.getResponder().getMoney() < this.fullprice) {
            buf.append("text{type='bold';text=\"You can not afford that. You need to add some money to your bank account.\"};");
        }
        else {
            int pcost = 0;
            buf.append("table{rows='" + (this.items.length + 1) + "'; cols='9';label{text='Item name'};label{text='QL'};label{text='DAM'};label{text='C.O.D'};label{text='Mail cost'};label{text='Your price in G'};label{text=',S'};label{text=',C'};label{text=',I'};");
            for (int x2 = 0; x2 < this.items.length; ++x2) {
                buf.append(Question.itemNameWithColorByRarity(this.items[x2]));
                buf.append("label{text=\"" + String.format("%.2f", this.items[x2].getQualityLevel()) + "\"};");
                buf.append("label{text=\"" + String.format("%.2f", this.items[x2].getDamage()) + "\"};");
                pcost = getCostForItem(this.items[x2], priceMod);
                final Item[] contained2 = this.items[x2].getAllItems(true);
                for (int c2 = 0; c2 < contained2.length; ++c2) {
                    pcost += getCostForItem(contained2[c2], priceMod);
                }
                change = new Change(pcost);
                if (this.cods[x2]) {
                    buf.append("label{text=\"yes\"};");
                    buf.append("label{text=\"" + change.getChangeShortString() + "\"};");
                    buf.append("input{maxchars='2'; id='" + x2 + "g';text='0'};");
                    buf.append("input{maxchars='2'; id='" + x2 + "s';text='0'};");
                    buf.append("input{maxchars='2'; id='" + x2 + "c';text='0'};");
                    buf.append("input{maxchars='2'; id='" + x2 + "i';text='0'}");
                }
                else {
                    buf.append("label{text=\"no\"};");
                    buf.append("label{text=\"" + change.getChangeShortString() + "\"};");
                    buf.append("label{text='0'};");
                    buf.append("label{text='0'};");
                    buf.append("label{text='0'};");
                    buf.append("label{text='0'};");
                }
            }
            buf.append("}");
        }
        buf.append(this.createAnswerButton3());
        this.getResponder().getCommunicator().sendBml(500, 400, true, true, buf.toString(), 200, 200, 200, this.title);
    }
    
    static {
        logger = Logger.getLogger(MailSendConfirmQuestion.class.getName());
    }
}
