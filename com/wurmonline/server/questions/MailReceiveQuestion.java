// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.questions;

import com.wurmonline.shared.util.MaterialUtilities;
import com.wurmonline.server.spells.SpellEffect;
import com.wurmonline.server.items.ItemSpellEffects;
import com.wurmonline.server.players.PlayerState;
import com.wurmonline.server.creatures.NoSuchCreatureTemplateException;
import com.wurmonline.server.creatures.CreatureTemplateFactory;
import com.wurmonline.server.items.WurmColor;
import com.wurmonline.server.Server;
import com.wurmonline.server.players.PlayerInfo;
import com.wurmonline.server.items.InscriptionData;
import java.util.Iterator;
import java.io.IOException;
import java.util.Date;
import java.text.DateFormat;
import com.wurmonline.server.players.PlayerInfoFactory;
import com.wurmonline.server.LoginServerWebConnection;
import com.wurmonline.server.economy.Change;
import java.util.Map;
import com.wurmonline.server.NoSuchItemException;
import java.util.logging.Level;
import com.wurmonline.server.Items;
import java.util.HashSet;
import java.util.HashMap;
import com.wurmonline.server.Servers;
import java.util.Properties;
import com.wurmonline.server.creatures.Creature;
import com.wurmonline.server.items.WurmMail;
import java.util.Set;
import com.wurmonline.server.items.Item;
import java.util.logging.Logger;
import com.wurmonline.server.TimeConstants;
import com.wurmonline.server.economy.MonetaryConstants;

public final class MailReceiveQuestion extends Question implements MonetaryConstants, TimeConstants
{
    private static final Logger logger;
    private final Item mbox;
    private Set<WurmMail> mailset;
    
    public MailReceiveQuestion(final Creature aResponder, final String aTitle, final String aQuestion, final Item aMailbox) {
        super(aResponder, aTitle, aQuestion, 53, aMailbox.getWurmId());
        this.mailset = null;
        this.mbox = aMailbox;
    }
    
    @Override
    public void answer(final Properties answers) {
        if (!this.mbox.isEmpty(false)) {
            this.getResponder().getCommunicator().sendNormalServerMessage("Empty the mailbox first.");
        }
        else {
            if (this.mailset.isEmpty()) {
                return;
            }
            if (!Servers.loginServer.isAvailable(5, true)) {
                this.getResponder().getCommunicator().sendNormalServerMessage("You may not receive mail right now. Please try later.");
                return;
            }
            int x = 0;
            float priceMod = 1.0f;
            int pcost = 0;
            long fullcost = 0L;
            final Map<Long, Long> moneyToSend = new HashMap<Long, Long>();
            WurmMail m = null;
            String val = "";
            final Set<Item> itemset = new HashSet<Item>();
            Map<Long, WurmMail> toReturn = null;
            final Iterator<WurmMail> it = this.mailset.iterator();
            while (it.hasNext()) {
                ++x;
                m = it.next();
                priceMod = 1.0f;
                final long receiver = WurmMail.getReceiverForItem(m.itemId);
                if (receiver == this.getResponder().getWurmId()) {
                    val = answers.getProperty(x + "receive");
                    if (val != null && val.equals("true")) {
                        try {
                            final Item item = Items.getItem(m.itemId);
                            if (m.rejected) {
                                pcost = 100;
                                if (item.getTemplateId() == 748 || item.getTemplateId() == 1272) {
                                    final InscriptionData insData = item.getInscription();
                                    if (insData != null && insData.hasBeenInscribed()) {
                                        pcost = 1;
                                    }
                                }
                                fullcost += pcost;
                            }
                            else if (m.type == 1) {
                                pcost = MailSendConfirmQuestion.getCostForItem(item, priceMod);
                                fullcost += pcost;
                                fullcost += m.price;
                                if (m.price > 0L) {
                                    Long msend = moneyToSend.get(m.sender);
                                    if (msend == null) {
                                        msend = m.price;
                                    }
                                    else {
                                        msend += m.price;
                                    }
                                    moneyToSend.put(m.sender, msend);
                                }
                                final Item[] contained = item.getAllItems(true);
                                for (int c = 0; c < contained.length; ++c) {
                                    pcost = MailSendConfirmQuestion.getCostForItem(contained[c], priceMod);
                                    fullcost += pcost;
                                }
                            }
                            itemset.add(item);
                        }
                        catch (NoSuchItemException nsi) {
                            MailReceiveQuestion.logger.log(Level.INFO, " NO SUCH ITEM");
                            WurmMail.deleteMail(m.itemId);
                        }
                    }
                    else {
                        val = answers.getProperty(x + "return");
                        if (val != null && val.equals("true")) {
                            if (toReturn == null) {
                                toReturn = new HashMap<Long, WurmMail>();
                            }
                            toReturn.put(m.itemId, m);
                        }
                        else {
                            if (!m.isExpired()) {
                                continue;
                            }
                            if (toReturn == null) {
                                toReturn = new HashMap<Long, WurmMail>();
                            }
                            toReturn.put(m.itemId, m);
                        }
                    }
                }
            }
            if (toReturn != null) {
                final Map<Integer, Set<WurmMail>> serverReturns = new HashMap<Integer, Set<WurmMail>>();
                for (final WurmMail retm : toReturn.values()) {
                    long timeavail = System.currentTimeMillis() + (101 - (int)this.mbox.getSpellCourierBonus()) * 60000L;
                    if (this.getResponder().getPower() > 0) {
                        timeavail = System.currentTimeMillis() + 60000L;
                    }
                    final WurmMail mail = new WurmMail((byte)1, retm.itemId, this.getResponder().getWurmId(), retm.sender, 0L, timeavail, System.currentTimeMillis() + (Servers.localServer.testServer ? 3600000L : 604800000L), Servers.localServer.id, true, false);
                    if (retm.sourceserver == Servers.localServer.id) {
                        WurmMail.removeMail(retm.itemId);
                        WurmMail.addWurmMail(mail);
                        mail.createInDatabase();
                    }
                    else {
                        Set<WurmMail> returnSet = serverReturns.get(retm.sourceserver);
                        if (returnSet == null) {
                            returnSet = new HashSet<WurmMail>();
                        }
                        returnSet.add(mail);
                        serverReturns.put(retm.sourceserver, returnSet);
                    }
                }
                if (!serverReturns.isEmpty()) {
                    final Map<Long, ReiceverReturnMails> returnsPerReceiver = new HashMap<Long, ReiceverReturnMails>();
                    for (final Map.Entry<Integer, Set<WurmMail>> entry : serverReturns.entrySet()) {
                        final Integer sid = entry.getKey();
                        final Set<WurmMail> mails = entry.getValue();
                        for (final WurmMail newmail : mails) {
                            try {
                                final Item i = Items.getItem(newmail.itemId);
                                ReiceverReturnMails returnSetReceiver = returnsPerReceiver.get(newmail.receiver);
                                if (returnSetReceiver == null) {
                                    returnSetReceiver = new ReiceverReturnMails();
                                    returnSetReceiver.setReceiverId(newmail.receiver);
                                    returnSetReceiver.setServerId(sid);
                                }
                                returnSetReceiver.addMail(newmail, i);
                                final Item[] contained2 = i.getAllItems(true);
                                for (int c2 = 0; c2 < contained2.length; ++c2) {
                                    returnSetReceiver.addMail(newmail, contained2[c2]);
                                }
                                returnsPerReceiver.put(newmail.receiver, returnSetReceiver);
                            }
                            catch (NoSuchItemException nsi2) {
                                MailReceiveQuestion.logger.log(Level.WARNING, "The item that should be returned is gone!");
                            }
                        }
                    }
                    if (!returnsPerReceiver.isEmpty()) {
                        boolean problem = false;
                        for (final Map.Entry<Long, ReiceverReturnMails> entry2 : returnsPerReceiver.entrySet()) {
                            final Long rid = entry2.getKey();
                            final ReiceverReturnMails returnSetReceiver2 = entry2.getValue();
                            final Item[] items = returnSetReceiver2.getReturnItemSetAsArray();
                            problem = MailSendConfirmQuestion.sendMailSetToServer(this.getResponder().getWurmId(), this.getResponder(), returnSetReceiver2.getServerId(), returnSetReceiver2.getReturnWurmMailSet(), rid, items);
                            if (!problem) {
                                for (int a = 0; a < items.length; ++a) {
                                    final Item[] contained3 = items[a].getAllItems(true);
                                    for (int c3 = 0; c3 < contained3.length; ++c3) {
                                        Items.destroyItem(contained3[c3].getWurmId());
                                    }
                                    Items.destroyItem(items[a].getWurmId());
                                    WurmMail.removeMail(items[a].getWurmId());
                                }
                            }
                        }
                    }
                }
            }
            final long money = this.getResponder().getMoney();
            if (fullcost > money) {
                final Change change = new Change(fullcost - money);
                this.getResponder().getCommunicator().sendNormalServerMessage("You need " + change.getChangeString() + " in order to receive the selected items.");
            }
            else if (fullcost > 0L) {
                final LoginServerWebConnection lsw = new LoginServerWebConnection();
                try {
                    if (this.getResponder().chargeMoney(fullcost)) {
                        for (final Item item2 : itemset) {
                            final Item[] contained4 = item2.getAllItems(true);
                            for (int c4 = 0; c4 < contained4.length; ++c4) {
                                contained4[c4].setMailed(false);
                                contained4[c4].setLastOwnerId(this.getResponder().getWurmId());
                            }
                            WurmMail.removeMail(item2.getWurmId());
                            this.mbox.insertItem(item2, true);
                            item2.setLastOwnerId(this.getResponder().getWurmId());
                            item2.setMailed(false);
                            MailReceiveQuestion.logger.log(Level.INFO, this.getResponder().getName() + " received " + item2.getName() + " " + item2.getWurmId());
                        }
                        final Change change2 = new Change(fullcost);
                        this.getResponder().getCommunicator().sendNormalServerMessage("The items are now available and you have been charged " + change2.getChangeString() + ".");
                        int xx = 0;
                        final Iterator<Map.Entry<Long, Long>> it6 = moneyToSend.entrySet().iterator();
                        while (it6.hasNext()) {
                            ++xx;
                            final Map.Entry<Long, Long> entry3 = it6.next();
                            final PlayerInfo pinf = PlayerInfoFactory.getPlayerInfoWithWurmId(entry3.getKey());
                            if (pinf != null) {
                                if (entry3.getValue() <= 0L) {
                                    continue;
                                }
                                MailReceiveQuestion.logger.log(Level.INFO, this.getResponder().getName() + " adding COD " + (long)entry3.getValue() + " to " + pinf.getName() + " via server " + lsw.getServerId());
                                lsw.addMoney(pinf.wurmId, pinf.getName(), entry3.getValue(), "Mail " + this.getResponder().getName() + DateFormat.getInstance().format(new Date()).replace(" ", "") + xx);
                            }
                            else if (entry3.getValue() > 0L) {
                                MailReceiveQuestion.logger.log(Level.INFO, "Adding COD " + (long)entry3.getValue() + " to " + (long)entry3.getKey() + " (no name) via server " + lsw.getServerId());
                                lsw.addMoney(entry3.getKey(), null, entry3.getValue(), "Mail " + this.getResponder().getName() + DateFormat.getInstance().format(new Date()).replace(" ", "") + xx);
                            }
                            else {
                                this.getResponder().getCommunicator().sendNormalServerMessage("Failed to locate the receiver of some money.");
                                MailReceiveQuestion.logger.log(Level.WARNING, "failed to locate receiver " + (long)entry3.getKey() + " of amount " + (long)entry3.getValue() + " from " + this.getResponder().getName() + ".");
                            }
                        }
                    }
                    else {
                        this.getResponder().getCommunicator().sendNormalServerMessage("Failed to charge you the money. The bank may not be available. No mail received.");
                    }
                }
                catch (IOException iox) {
                    this.getResponder().getCommunicator().sendNormalServerMessage("Failed to charge you the money. The bank may not be available. No mail received.");
                }
            }
            else {
                for (final Item item : itemset) {
                    final Item[] contained = item.getAllItems(true);
                    for (int c = 0; c < contained.length; ++c) {
                        contained[c].setMailed(false);
                        contained[c].setLastOwnerId(this.getResponder().getWurmId());
                    }
                    WurmMail.removeMail(item.getWurmId());
                    this.mbox.insertItem(item, true);
                    item.setLastOwnerId(this.getResponder().getWurmId());
                    item.setMailed(false);
                }
                if (itemset.size() > 0) {
                    this.getResponder().getCommunicator().sendNormalServerMessage("The items are now available in the " + this.mbox.getName() + ".");
                }
                if (toReturn != null && toReturn.size() > 0) {
                    this.getResponder().getCommunicator().sendNormalServerMessage("The spirits will return the unwanted items.");
                }
            }
        }
    }
    
    @Override
    public void sendQuestion() {
        final String lHtml = "border{scroll{vertical='true';horizontal='false';varray{rescale='true';passthrough{id='id';text='" + this.getId() + "'}";
        final StringBuilder buf = new StringBuilder(lHtml);
        if (!this.mbox.isEmpty(false)) {
            buf.append("label{text=\"Empty the mailbox first.\"}");
            buf.append("text{text=\"\"};}null;varray{");
        }
        else {
            this.mailset = WurmMail.getSentMailsFor(this.getResponder().getWurmId(), 100);
            if (this.mailset.isEmpty()) {
                buf.append("text{text='You have no pending mail.'}");
                buf.append("text{text=\"\"};}null;");
            }
            else {
                buf.append("text{text='Use the checkboxes to select which items you wish to receive in your mailbox, and which to return to the sender.'}");
                buf.append("text{text='If an item has a Cash On Delivery (C.O.D) cost, you have to have that money in the bank.'}");
                final long money = this.getResponder().getMoney();
                if (money <= 0L) {
                    buf.append("text{text='You have no money in the bank.'}");
                }
                else {
                    buf.append("text{text='You have " + new Change(money).getChangeString() + " in the bank.'}");
                }
                buf.append("}};null;");
                int rowNumb = 0;
                buf.append("tree{id=\"t1\";cols=\"10\";showheader=\"true\";height=\"300\"col{text=\"QL\";width=\"45\"};col{text=\"DAM\";width=\"45\"};col{text=\"Receive\";width=\"50\"};col{text=\"Return\";width=\"50\"};col{text=\"G\";width=\"25\"};col{text=\"S\";width=\"25\"};col{text=\"C\";width=\"25\"};col{text=\"I\";width=\"25\"};col{text=\"Sender\";width=\"75\"};col{text=\"Expiry\";width=\"220\"};");
                for (final WurmMail m : this.mailset) {
                    try {
                        ++rowNumb;
                        final Item item = Items.getItem(m.itemId);
                        buf.append(this.addItem("" + rowNumb, item, m, true));
                    }
                    catch (NoSuchItemException e) {
                        buf.append("row{id=\"e" + rowNumb + "\";hover=\"Item gone.\";name=\"Item gone.\";rarity=\"0\";children=\"0\";col{text=\"n/a\"};col{text=\"n/a\"};col{text=\"n/a\"};col{text=\"n/a\"};col{text=\"\"};col{text=\"\"};col{text=\"\"};col{text=\"\"};col{text=\"n/a\"};col{text=\"n/a\"}}");
                    }
                }
                buf.append("}");
                buf.append("null;varray{");
                if (this.mailset.size() < 100) {
                    buf.append("label{text='You have no more mail.'}");
                }
                else {
                    buf.append("text{text='You may have more mail than these. Manage these then check again.'}");
                }
            }
        }
        buf.append("harray{button{text=\"Send\";id=\"submit\"}}text=\"\"}}");
        this.getResponder().getCommunicator().sendBml(700, 300, true, true, buf.toString(), 200, 200, 200, this.title);
    }
    
    private String addItem(final String id, final Item item, final WurmMail m, final boolean isTopLevel) {
        final StringBuilder buf = new StringBuilder();
        Change change = null;
        final float priceMod = 1.0f;
        int pcost = 0;
        long fullcost = 0L;
        final Item[] contained = item.getItemsAsArray();
        final int children = contained.length;
        if (m.rejected && isTopLevel) {
            pcost = 100;
            if (item.getTemplateId() == 748 || item.getTemplateId() == 1272) {
                final InscriptionData insData = item.getInscription();
                if (insData != null && insData.hasBeenInscribed()) {
                    pcost = 1;
                }
            }
            change = new Change(pcost);
        }
        else if (m.price > 0L && isTopLevel) {
            fullcost = MailSendConfirmQuestion.getCostForItem(item, priceMod);
            fullcost += m.price;
            for (int c = 0; c < contained.length; ++c) {
                pcost = MailSendConfirmQuestion.getCostForItem(contained[c], priceMod);
                fullcost += pcost;
            }
            change = new Change(fullcost);
        }
        final String itemName = longItemName(item);
        final String sQL = String.format("%.2f", item.getQualityLevel());
        final String sDMG = String.format("%.2f", item.getDamage());
        String receive = "text=\"\"";
        String ret = "text=\"\"";
        String gold = "";
        String silver = "";
        String copper = "";
        String iron = "";
        String sender = "";
        String expire = "";
        if (isTopLevel) {
            receive = "checkbox=\"true\";id=\"" + id + "receive\"";
            if (m.rejected) {
                ret = "text=\"n/a\"";
            }
            else {
                ret = "checkbox=\"true\";id=\"" + id + "return\"";
            }
            if (change != null) {
                gold = "" + change.getGoldCoins();
                silver = "" + change.getSilverCoins();
                copper = "" + change.getCopperCoins();
                iron = "" + change.getIronCoins();
            }
            final PlayerState ps = PlayerInfoFactory.getPlayerState(m.getSender());
            sender = ((ps != null) ? ps.getPlayerName() : "Unknown");
            expire = "" + Server.getTimeFor(Math.max(0L, m.expiration - System.currentTimeMillis()));
        }
        String spells = "";
        final ItemSpellEffects eff = item.getSpellEffects();
        if (eff != null) {
            final SpellEffect[] speffs = eff.getEffects();
            for (int z = 0; z < speffs.length; ++z) {
                if (spells.length() > 0) {
                    spells += ",";
                }
                spells = spells + speffs[z].getName() + " [" + (int)speffs[z].power + "]";
            }
        }
        String extra = "";
        if (item.getColor() != -1) {
            extra = " [" + WurmColor.getRGBDescription(item.getColor()) + "]";
        }
        if (item.getTemplateId() == 866) {
            try {
                extra = extra + " [" + CreatureTemplateFactory.getInstance().getTemplate(item.getData2()).getName() + "]";
            }
            catch (NoSuchCreatureTemplateException e) {
                MailReceiveQuestion.logger.log(Level.WARNING, e.getMessage(), e);
            }
        }
        if (spells.length() == 0) {
            spells = "no enchants";
        }
        final String hover = itemName + " - " + spells + extra;
        buf.append("row{id=\"" + id + "\";hover=\"" + hover + "\";name=\"" + itemName + "\";rarity=\"" + item.getRarity() + "\";children=\"" + children + "\";col{text=\"" + sQL + "\"};col{text=\"" + sDMG + "\"};col{" + receive + "};col{" + ret + "};col{text=\"" + gold + "\"};col{text=\"" + silver + "\"};col{text=\"" + copper + "\"};col{text=\"" + iron + "\"};col{text=\"" + sender + "\"};col{text=\"" + expire.replace(",", " ") + "\"}}");
        for (int c2 = 0; c2 < contained.length; ++c2) {
            buf.append(this.addItem(id + "c" + c2, contained[c2], m, false));
        }
        return buf.toString();
    }
    
    public static String longItemName(final Item litem) {
        final StringBuilder sb = new StringBuilder();
        if (litem.getRarity() == 1) {
            sb.append("rare ");
        }
        else if (litem.getRarity() == 2) {
            sb.append("supreme ");
        }
        else if (litem.getRarity() == 3) {
            sb.append("fantastic ");
        }
        final String name = (litem.getName().length() == 0) ? litem.getTemplate().getName() : litem.getName();
        MaterialUtilities.appendNameWithMaterialSuffix(sb, name.replace("\"", "''"), litem.getMaterial());
        if (litem.getDescription().length() > 0) {
            sb.append(" (" + litem.getDescription() + ")");
        }
        return sb.toString();
    }
    
    static {
        logger = Logger.getLogger(MailReceiveQuestion.class.getName());
    }
}
