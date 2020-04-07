// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.questions;

import com.wurmonline.server.Servers;
import java.util.Iterator;
import java.util.Set;
import com.wurmonline.server.players.PlayerInfo;
import java.util.logging.Level;
import com.wurmonline.server.items.Item;
import com.wurmonline.server.items.ItemFactory;
import com.wurmonline.server.Server;
import com.wurmonline.server.Items;
import com.wurmonline.server.players.PlayerInfoFactory;
import java.util.Properties;
import com.wurmonline.server.creatures.Creature;
import java.util.logging.Logger;
import com.wurmonline.server.TimeConstants;

public class RedeemQuestion extends Question implements TimeConstants
{
    private static final Logger logger;
    
    public RedeemQuestion(final Creature aResponder, final String aTitle, final String aQuestion) {
        super(aResponder, aTitle, aQuestion, 87, aResponder.getWurmId());
    }
    
    @Override
    public void answer(final Properties aAnswers) {
        final String key = "plays";
        final String val = aAnswers.getProperty("plays");
        try {
            final long wid = Long.parseLong(val);
            if (wid > 0L) {
                final PlayerInfo pinf = PlayerInfoFactory.getPlayerInfoWithWurmId(wid);
                if (pinf != null) {
                    if (pinf.hasMovedInventory()) {
                        Items.returnItemsFromFreezerFor(wid);
                    }
                    final Set<Item> items = Items.loadAllItemsForCreatureWithId(wid, false);
                    if (items.size() > 0) {
                        try {
                            final Item chest = ItemFactory.createItem(192, 50 + Server.rand.nextInt(30), pinf.getName());
                            for (final Item i : items) {
                                if (!i.isInventory() && !i.isBodyPart() && !i.isHomesteadDeed() && !i.isNewDeed() && !i.isVillageDeed() && i.getTemplateId() != 166) {
                                    chest.insertItem(i, true);
                                }
                            }
                            this.getResponder().getInventory().insertItem(chest, true);
                            this.getResponder().getCommunicator().sendNormalServerMessage("You redeem the items for " + pinf.getName() + " and put them in a nice chest.");
                            this.getResponder().getLogger().info(this.getResponder().getName() + " redeems the items of " + pinf.getName() + ".");
                            RedeemQuestion.logger.log(Level.INFO, this.getResponder().getName() + " redeems the items of " + pinf.getName() + ".");
                        }
                        catch (Exception ex) {
                            RedeemQuestion.logger.log(Level.INFO, ex.getMessage(), ex);
                        }
                    }
                    else {
                        this.getResponder().getCommunicator().sendNormalServerMessage("There were no items for " + pinf.getName() + ".");
                    }
                }
            }
        }
        catch (NumberFormatException nfn) {
            this.getResponder().getCommunicator().sendNormalServerMessage("Unknown player " + val);
        }
    }
    
    @Override
    public void sendQuestion() {
        final PlayerInfo[] plays = PlayerInfoFactory.getPlayerInfos();
        final String lHtml = this.getBmlHeader();
        final StringBuilder buf = new StringBuilder(lHtml);
        buf.append("text{text=\"This functionality will retrieve all non-deed and non-writ items from a banned player and put them in a chest in your inventory.\"}");
        buf.append("text{text=\"The suggestion is to use the hide functionality to get a pair of random coordinates to put it at or simply hide it at the suggested location.\"}");
        buf.append("text{text=\"Only players with at least a year of bannination left will be listed here.\"}");
        buf.append("text{text=\"Select a banned player from which to redeem items:\"}");
        buf.append("radio{group=\"plays\";id=\"-1\";text=\"None\";selected=\"true\"};");
        for (int x = 0; x < plays.length; ++x) {
            if (plays[x].isBanned() && plays[x].getCurrentServer() == Servers.localServer.id && plays[x].banexpiry - System.currentTimeMillis() > 29030400000L) {
                buf.append("radio{group=\"plays\";id=\"" + plays[x].wurmId + "\";text=\"" + plays[x].getName() + "\"};");
            }
        }
        buf.append(this.createAnswerButton3());
        this.getResponder().getCommunicator().sendBml(500, 300, true, true, buf.toString(), 200, 200, 200, this.title);
    }
    
    static {
        logger = Logger.getLogger(RedeemQuestion.class.getName());
    }
}
