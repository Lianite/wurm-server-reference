// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.questions;

import com.wurmonline.shared.util.MaterialUtilities;
import com.wurmonline.server.villages.Village;
import java.util.Set;
import java.util.Arrays;
import java.util.Comparator;
import com.wurmonline.server.highways.PathToCalculate;
import java.util.HashSet;
import com.wurmonline.server.NoSuchItemException;
import com.wurmonline.server.Items;
import com.wurmonline.server.creatures.Creatures;
import java.util.Iterator;
import com.wurmonline.server.players.PlayerState;
import com.wurmonline.server.creatures.Delivery;
import java.io.IOException;
import java.util.logging.Level;
import com.wurmonline.server.economy.Economy;
import com.wurmonline.server.economy.Change;
import com.wurmonline.server.Servers;
import com.wurmonline.server.players.PlayerInfoFactory;
import com.wurmonline.server.LoginHandler;
import com.wurmonline.server.creatures.Wagoner;
import java.util.Properties;
import com.wurmonline.server.creatures.Creature;
import com.wurmonline.server.items.Item;
import java.text.DecimalFormat;
import java.util.logging.Logger;
import com.wurmonline.server.economy.MonetaryConstants;

public class WagonerSetupDeliveryQuestion extends Question implements MonetaryConstants
{
    private static final Logger logger;
    private static final String red = "color=\"255,127,127\"";
    private static final DecimalFormat df;
    private final Item container;
    private int sortBy;
    private int pageNo;
    private long wagonerId;
    private long receiverId;
    private String receiverName;
    private String error;
    private int crates;
    
    public WagonerSetupDeliveryQuestion(final Creature aResponder, final Item container) {
        super(aResponder, getTitle(1), getTitle(1), 145, -10L);
        this.sortBy = 1;
        this.pageNo = 1;
        this.wagonerId = -10L;
        this.receiverId = -10L;
        this.receiverName = "";
        this.error = "";
        this.crates = 0;
        (this.container = container).setIsSealedOverride(true);
    }
    
    public WagonerSetupDeliveryQuestion(final Creature aResponder, final Item container, final int sortBy, final int pageNo, final long wagonerId, final long receiverId, final String receiverName, final String error) {
        super(aResponder, getTitle(pageNo), getTitle(pageNo), 145, -10L);
        this.sortBy = 1;
        this.pageNo = 1;
        this.wagonerId = -10L;
        this.receiverId = -10L;
        this.receiverName = "";
        this.error = "";
        this.crates = 0;
        this.container = container;
        this.sortBy = sortBy;
        this.pageNo = pageNo;
        this.wagonerId = wagonerId;
        this.receiverId = receiverId;
        this.receiverName = receiverName;
        this.error = error;
    }
    
    private static final String getTitle(final int pageNo) {
        return "Set up Delivery page " + pageNo + " of 2";
    }
    
    @Override
    public void answer(final Properties aAnswer) {
        this.setAnswer(aAnswer);
        final boolean cancel = this.getBooleanProp("cancel");
        if (cancel) {
            this.getResponder().getCommunicator().sendNormalServerMessage("Set up Delivery Cancelled!");
            this.container.setIsSealedOverride(false);
            return;
        }
        switch (this.pageNo) {
            case 1: {
                final boolean next = this.getBooleanProp("next");
                if (next) {
                    this.error = "";
                    final String sel = aAnswer.getProperty("sel");
                    final long selId = Long.parseLong(sel);
                    if (selId == -10L) {
                        this.getResponder().getCommunicator().sendNormalServerMessage("You decide to do nothing.");
                        this.container.setIsSealedOverride(false);
                        return;
                    }
                    final Wagoner wagoner = Wagoner.getWagoner(selId);
                    if (wagoner == null) {
                        this.wagonerId = -10L;
                        this.error = "Wagoner has vanished!";
                    }
                    else {
                        this.wagonerId = wagoner.getWurmId();
                    }
                    final String who = aAnswer.getProperty("playername");
                    if (who != null && who.length() > 2) {
                        if (LoginHandler.containsIllegalCharacters(who)) {
                            this.getResponder().getCommunicator().sendNormalServerMessage("The name of the receiver contains illegal characters. Please check the name.");
                            this.error = "Player " + who + " contains illegal characters!";
                        }
                    }
                    else {
                        this.error = "Player name was too short!";
                    }
                    if (this.error.length() == 0) {
                        this.receiverName = LoginHandler.raiseFirstLetter(who);
                        final PlayerState ps = PlayerInfoFactory.getPlayerState(this.receiverName);
                        if (ps == null) {
                            this.error = "Player " + this.receiverName + " not found!";
                        }
                        else if (ps.getServerId() != Servers.getLocalServerId()) {
                            this.error = "Player " + this.receiverName + " is not on this server!";
                        }
                        else {
                            this.receiverId = ps.getPlayerId();
                        }
                    }
                    if (this.receiverId == -10L && this.error.length() == 0) {
                        this.error = "Player " + who + " not found!";
                    }
                    if (this.error.length() == 0) {
                        this.pageNo = 2;
                    }
                }
                else {
                    for (final String key : this.getAnswer().stringPropertyNames()) {
                        if (key.startsWith("sort")) {
                            final String sid = key.substring(4);
                            this.sortBy = Integer.parseInt(sid);
                            break;
                        }
                    }
                }
                final WagonerSetupDeliveryQuestion wdq = new WagonerSetupDeliveryQuestion(this.getResponder(), this.container, this.sortBy, this.pageNo, this.wagonerId, this.receiverId, this.receiverName, this.error);
                switch (this.pageNo) {
                    case 1: {
                        wdq.sendQuestion();
                        break;
                    }
                    case 2: {
                        wdq.sendQuestion2();
                        break;
                    }
                }
            }
            case 2: {
                int codprice = 0;
                String val = aAnswer.getProperty("g");
                if (val != null && val.length() > 0) {
                    try {
                        codprice = Integer.parseInt(val) * 1000000;
                    }
                    catch (NumberFormatException nfe) {
                        this.getResponder().getCommunicator().sendNormalServerMessage("Failed to set the gold price for delivery. Note that a coin value is in whole numbers, no decimals.");
                    }
                }
                val = aAnswer.getProperty("s");
                if (val != null && val.length() > 0) {
                    try {
                        codprice += Integer.parseInt(val) * 10000;
                    }
                    catch (NumberFormatException nfe) {
                        this.getResponder().getCommunicator().sendNormalServerMessage("Failed to set a silver price for delivery. Note that a coin value is in whole numbers, no decimals.");
                    }
                }
                val = aAnswer.getProperty("c");
                if (val != null && val.length() > 0) {
                    try {
                        codprice += Integer.parseInt(val) * 100;
                    }
                    catch (NumberFormatException nfe) {
                        this.getResponder().getCommunicator().sendNormalServerMessage("Failed to set a copper price for delivery. Note that a coin value is in whole numbers, no decimals.");
                    }
                }
                val = aAnswer.getProperty("i");
                if (val != null && val.length() > 0) {
                    try {
                        codprice += Integer.parseInt(val);
                    }
                    catch (NumberFormatException nfe) {
                        this.getResponder().getCommunicator().sendNormalServerMessage("Failed to set an iron price for delivery. Note that a coin value is in whole numbers, no decimals.");
                    }
                }
                int senderCost = 0;
                final String sel2 = aAnswer.getProperty("fee");
                final int selId2 = Integer.parseInt(sel2);
                if (selId2 == 0) {
                    senderCost += this.crates * 100;
                    final long money = this.getResponder().getMoney();
                    if (money < senderCost) {
                        this.getResponder().getCommunicator().sendNormalServerMessage("You cannot afford to pay for the delivery fee, so its been cancelled.");
                        this.container.setIsSealedOverride(false);
                        return;
                    }
                }
                else {
                    codprice += this.crates * 100;
                }
                boolean failed = false;
                if (senderCost > 0) {
                    try {
                        if (this.getResponder().chargeMoney(senderCost)) {
                            this.getResponder().getCommunicator().sendNormalServerMessage("You have been charged " + new Change(senderCost).getChangeString() + ".");
                            final Change change = Economy.getEconomy().getChangeFor(this.getResponder().getMoney());
                            this.getResponder().getCommunicator().sendNormalServerMessage("You now have " + change.getChangeString() + " in the bank.");
                            this.getResponder().getCommunicator().sendNormalServerMessage("If this amount is incorrect, please wait a while since the information may not immediately be updated.");
                        }
                        else {
                            failed = true;
                        }
                    }
                    catch (IOException e) {
                        failed = true;
                        WagonerSetupDeliveryQuestion.logger.log(Level.WARNING, e.getMessage(), e);
                    }
                }
                if (failed) {
                    this.getResponder().getCommunicator().sendNormalServerMessage("Something went wrong, delivery set up cancelled.");
                    this.container.setIsSealedOverride(false);
                    return;
                }
                this.container.setIsSealedByPlayer(true);
                Delivery.addDelivery(this.container.getData(), this.container.getWurmId(), this.crates, this.getResponder().getWurmId(), senderCost, this.receiverId, codprice, this.wagonerId);
                this.getResponder().getCommunicator().sendNormalServerMessage("You have set up a delivery to " + this.receiverName + " for " + new Change(codprice).getChangeString() + ".");
                break;
            }
        }
    }
    
    @Override
    public void sendQuestion() {
        int height = 300;
        final StringBuilder buf = new StringBuilder();
        buf.append(this.getBmlHeader());
        buf.append("closebutton{id=\"cancel\"};");
        buf.append("text{type=\"bold\";color=\"255,127,127\"text=\"" + this.error + "\"}");
        final Set<Creature> creatureSet = Creatures.getMayUseWagonersFor(this.getResponder());
        final long endWaystoneId = this.container.getData();
        Item endWaystone = null;
        try {
            endWaystone = Items.getItem(endWaystoneId);
        }
        catch (NoSuchItemException ex) {}
        final Set<Distanced> wagonerSet = new HashSet<Distanced>();
        for (final Creature creature : creatureSet) {
            final Wagoner wagoner = Wagoner.getWagoner(creature.getWurmId());
            if (wagoner != null) {
                final float dist = PathToCalculate.getRouteDistance(wagoner.getHomeWaystoneId(), endWaystoneId);
                if (dist == 99999.0f) {
                    continue;
                }
                final boolean isPublic = creature.publicMayUse(this.getResponder());
                String villName = "";
                final Village vill = creature.getCitizenVillage();
                if (vill != null) {
                    villName = vill.getName();
                }
                wagonerSet.add(new Distanced(wagoner, isPublic, villName, dist));
            }
        }
        final Distanced[] wagonerArr = wagonerSet.toArray(new Distanced[wagonerSet.size()]);
        final int absSortBy = Math.abs(this.sortBy);
        final int upDown = Integer.signum(this.sortBy);
        switch (absSortBy) {
            case 1: {
                Arrays.sort(wagonerArr, new Comparator<Distanced>() {
                    @Override
                    public int compare(final Distanced param1, final Distanced param2) {
                        return param1.getWagoner().getName().compareTo(param2.getWagoner().getName()) * upDown;
                    }
                });
                break;
            }
            case 2: {
                Arrays.sort(wagonerArr, new Comparator<Distanced>() {
                    @Override
                    public int compare(final Distanced param1, final Distanced param2) {
                        return param1.getType().compareTo(param2.getType()) * upDown;
                    }
                });
                break;
            }
            case 3: {
                Arrays.sort(wagonerArr, new Comparator<Distanced>() {
                    @Override
                    public int compare(final Distanced param1, final Distanced param2) {
                        return param1.getVillageName().compareTo(param2.getVillageName()) * upDown;
                    }
                });
                break;
            }
            case 4: {
                Arrays.sort(wagonerArr, new Comparator<Distanced>() {
                    @Override
                    public int compare(final Distanced param1, final Distanced param2) {
                        if (param1.getDistance() < param2.getDistance()) {
                            return 1 * upDown;
                        }
                        return -1 * upDown;
                    }
                });
                break;
            }
            case 5: {
                Arrays.sort(wagonerArr, new Comparator<Distanced>() {
                    @Override
                    public int compare(final Distanced param1, final Distanced param2) {
                        return param1.getWagoner().getStateName().compareTo(param2.getWagoner().getStateName()) * upDown;
                    }
                });
                break;
            }
            case 6: {
                Arrays.sort(wagonerArr, new Comparator<Distanced>() {
                    @Override
                    public int compare(final Distanced param1, final Distanced param2) {
                        if (param1.getQueueLength() < param2.getQueueLength()) {
                            return 1 * upDown;
                        }
                        return -1 * upDown;
                    }
                });
                break;
            }
        }
        buf.append("label{text=\"Select which wagoner to use \"};");
        buf.append("table{rows=\"1\";cols=\"8\";label{text=\"\"};" + this.colHeader("Name", 1, this.sortBy) + this.colHeader("Type", 2, this.sortBy) + this.colHeader("Village", 3, this.sortBy) + this.colHeader("Distance", 4, this.sortBy) + this.colHeader("State", 5, this.sortBy) + this.colHeader("Queue", 6, this.sortBy) + "label{type=\"bold\";text=\"\"};");
        String noneSelected = "selected=\"true\";";
        for (final Distanced distanced : wagonerArr) {
            String selected = "";
            if (this.wagonerId == distanced.getWagoner().getWurmId()) {
                selected = "selected=\"true\";";
                noneSelected = "";
            }
            buf.append((distanced.getVillageName().length() == 0) ? "label{text=\"\"}" : ("radio{group=\"sel\";id=\"" + distanced.getWagoner().getWurmId() + "\";" + selected + "text=\"\"}label{text=\"" + distanced.getWagoner().getName() + "\"};label{text=\"" + distanced.getType() + "\"};label{text=\"" + distanced.getVillageName() + "\"};label{text=\"" + (int)distanced.getDistance() + "\"};label{text=\"" + distanced.getWagoner().getStateName() + "\"};label{text=\"" + distanced.getQueueLength() + "\"};label{text=\"\"}"));
        }
        buf.append("}");
        buf.append("radio{group=\"sel\";id=\"-10\";" + noneSelected + "text=\" None\"}");
        if (endWaystone == null) {
            buf.append("text{text=\"Could not find the associated waystone for this wagoner container.\"}");
        }
        buf.append("text{text=\"\"}");
        boolean ownsAll = true;
        final Item[] itemsAsArray;
        final Item[] crates = itemsAsArray = this.container.getItemsAsArray();
        for (final Item crate : itemsAsArray) {
            if (crate.getLastOwnerId() != this.getResponder().getWurmId()) {
                ownsAll = false;
            }
        }
        if (!ownsAll) {
            buf.append("text{text=\"You cannot set up a delivery on this container as you did not load all the crates.\"}");
            buf.append("harray{button{text=\"Close\";id=\"cancel\"}}");
        }
        else {
            buf.append("text{text=\"Specify who to deliver the contents of this container to.\"}");
            buf.append("harray{label{text=\"Deliver to \"}input{maxchars=\"40\";id=\"playername\";text=\"" + this.receiverName + "\"}}");
            buf.append("text{text=\"\"}");
            buf.append("text{text=\"They will be informed and will have to accept it before the delivery can start.\"}");
            buf.append("text{text=\"\"}");
            buf.append("harray{label{text=\"Continue to \"};button{text=\"Next\";id=\"next\"}label{text=\" screen to add costs.\"};}");
        }
        buf.append("}};null;null;}");
        height += wagonerArr.length * 20;
        this.getResponder().getCommunicator().sendBml(420, height, true, true, buf.toString(), 200, 200, 200, this.title);
    }
    
    public void sendQuestion2() {
        final StringBuilder buf = new StringBuilder();
        final String header = "border{scroll{vertical='true';horizontal='false';varray{rescale='true';passthrough{id='id';text='" + this.getId() + "'}";
        buf.append(header);
        buf.append("closebutton{id=\"cancel\"};");
        final Wagoner wagoner = Wagoner.getWagoner(this.wagonerId);
        buf.append("text{text=\"You have selected " + wagoner.getName() + " to perform the delivery.\"}");
        buf.append("text{text=\"They will be delivering to " + this.receiverName + ".\"}");
        final long money = this.getResponder().getMoney();
        if (money <= 0L) {
            buf.append("text{text='You have no money in the bank.'}");
        }
        else {
            buf.append("text{text='You have " + new Change(money).getChangeString() + " in the bank.'}");
        }
        buf.append("}};null;");
        buf.append("tree{id=\"t1\";cols=\"3\";showheader=\"true\";height=\"300\"col{text=\"QL\";width=\"50\"};col{text=\"DMG\";width=\"50\"};col{text=\"Weight\";width=\"50\"};");
        final Item[] itemsAsArray;
        final Item[] crates = itemsAsArray = this.container.getItemsAsArray();
        for (final Item crate : itemsAsArray) {
            buf.append(this.addCrate(crate));
        }
        buf.append("}");
        buf.append("null;varray{");
        this.crates = crates.length;
        buf.append("label{text=\"" + wagoner.getName() + " charges a delivery fee of 1C per crate. So a total of " + this.crates + "C.\"}");
        buf.append("harray{label{text=\"These fees will be paid for by:\"}radio{group=\"fee\";id=\"0\";text=\"You \"}radio{group=\"fee\";id=\"1\";selected=\"true\";text=\"" + this.receiverName + ".\"}}");
        buf.append("harray{label{text=\"You are charging \"};");
        buf.append("table{rows=\"1\"; cols=\"8\";label{text=\"G:\"};input{maxchars=\"2\"; id=\"g\";text=\"0\"};label{text=\"S:\"};input{maxchars=\"2\"; id=\"s\";text=\"0\"};label{text=\"C:\"};input{maxchars=\"2\"; id=\"c\";text=\"0\"};label{text=\"I:\"};input{maxchars=\"2\"; id=\"i\";text=\"0\"};}");
        buf.append("label{text=\" for the goods:\"}}");
        buf.append("harray{button{text=\"Add to " + wagoner.getName() + "'s queue\";id=\"queue\"};}");
        buf.append("text=\"\"}}");
        this.getResponder().getCommunicator().sendBml(450, 400, true, true, buf.toString(), 200, 200, 200, this.title);
    }
    
    private String addCrate(final Item crate) {
        final StringBuilder buf = new StringBuilder();
        final String sQL = "" + WagonerSetupDeliveryQuestion.df.format(crate.getQualityLevel());
        final String sDMG = "" + WagonerSetupDeliveryQuestion.df.format(crate.getDamage());
        final String sWeight = "" + WagonerSetupDeliveryQuestion.df.format(crate.getFullWeight(true) / 1000.0f);
        final String hover;
        final String itemName = hover = longItemName(crate);
        final Item[] contained = crate.getItemsAsArray();
        final int children = contained.length;
        buf.append("row{id=\"" + this.id + "\";hover=\"" + hover + "\";name=\"" + itemName + "\";rarity=\"" + crate.getRarity() + "\";children=\"" + children + "\";col{text=\"" + sQL + "\"};col{text=\"" + sDMG + "\"};col{text=\"" + sWeight + "\"}}");
        for (final Item bulkItem : contained) {
            buf.append(this.addBulkItem(bulkItem));
        }
        return buf.toString();
    }
    
    private String addBulkItem(final Item bulkItem) {
        final StringBuilder buf = new StringBuilder();
        final String sQL = "" + WagonerSetupDeliveryQuestion.df.format(bulkItem.getQualityLevel());
        final String sWeight = "" + WagonerSetupDeliveryQuestion.df.format(bulkItem.getFullWeight(true) / 1000.0f);
        final String hover;
        final String itemName = hover = longItemName(bulkItem);
        buf.append("row{id=\"" + this.id + "\";hover=\"" + hover + "\";name=\"" + itemName + "\";rarity=\"0\";children=\"0\";col{text=\"" + sQL + "\"};col{text=\"0.00\"};col{text=\"" + sWeight + "\"}}");
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
    
    @Override
    public void timedOut() {
        this.container.setIsSealedOverride(false);
    }
    
    static {
        logger = Logger.getLogger(WagonerSetupDeliveryQuestion.class.getName());
        df = new DecimalFormat("#0.00");
    }
    
    class Distanced
    {
        private final Wagoner wagoner;
        private final boolean isPublic;
        private final String villageName;
        private final float distance;
        private final int queueLength;
        
        Distanced(final Wagoner wagoner, final boolean isPublic, final String villageName, final float distance) {
            this.wagoner = wagoner;
            this.isPublic = isPublic;
            this.villageName = villageName;
            this.distance = distance;
            this.queueLength = Delivery.getQueueLength(wagoner.getWurmId());
        }
        
        Wagoner getWagoner() {
            return this.wagoner;
        }
        
        float getDistance() {
            return this.distance;
        }
        
        boolean isPublic() {
            return this.isPublic;
        }
        
        String getType() {
            if (this.isPublic) {
                return "Public";
            }
            return "Private";
        }
        
        String getVillageName() {
            return this.villageName;
        }
        
        int getQueueLength() {
            return this.queueLength;
        }
    }
}
