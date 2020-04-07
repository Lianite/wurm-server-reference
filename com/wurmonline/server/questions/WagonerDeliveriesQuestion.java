// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.questions;

import com.wurmonline.shared.util.MaterialUtilities;
import com.wurmonline.server.items.Item;
import com.wurmonline.server.economy.Change;
import javax.annotation.Nullable;
import com.wurmonline.server.villages.Village;
import java.util.Set;
import com.wurmonline.server.creatures.Wagoner;
import com.wurmonline.server.highways.PathToCalculate;
import java.util.HashSet;
import com.wurmonline.server.creatures.Creatures;
import java.util.Arrays;
import java.util.Comparator;
import com.wurmonline.server.players.Player;
import java.util.Iterator;
import com.wurmonline.server.NoSuchPlayerException;
import com.wurmonline.server.Players;
import com.wurmonline.server.creatures.Delivery;
import java.util.Properties;
import com.wurmonline.server.creatures.Creature;
import java.text.DecimalFormat;
import java.util.logging.Logger;

public class WagonerDeliveriesQuestion extends Question
{
    private static final Logger logger;
    private static final DecimalFormat df;
    private long deliveryId;
    private int sortBy;
    private int pageNo;
    private boolean hasBack;
    private final boolean hasList;
    
    public WagonerDeliveriesQuestion(final Creature aResponder, final long deliveryId, final boolean hasList) {
        super(aResponder, getTitle(deliveryId), getTitle(deliveryId), 147, deliveryId);
        this.sortBy = 1;
        this.pageNo = 1;
        this.hasBack = false;
        this.deliveryId = deliveryId;
        this.hasList = hasList;
    }
    
    public WagonerDeliveriesQuestion(final Creature aResponder, final long deliveryId, final boolean hasList, final int sortBy, final int pageNo, final boolean hasBack) {
        super(aResponder, getTitle(deliveryId), getTitle(deliveryId), 147, deliveryId);
        this.sortBy = 1;
        this.pageNo = 1;
        this.hasBack = false;
        this.deliveryId = deliveryId;
        this.hasList = hasList;
        this.sortBy = sortBy;
        this.pageNo = pageNo;
        this.hasBack = hasBack;
    }
    
    private static String getTitle(final long deliveryId) {
        if (deliveryId == -10L) {
            return "Wagoner Deliveries Management";
        }
        return "Wagoner Delivery Management";
    }
    
    @Override
    public void answer(final Properties aAnswer) {
        this.setAnswer(aAnswer);
        switch (this.pageNo) {
            case 1: {
                final boolean next = this.getBooleanProp("next");
                if (next) {
                    final String sel = aAnswer.getProperty("sel");
                    this.deliveryId = Long.parseLong(sel);
                    if (this.deliveryId == -10L) {
                        this.getResponder().getCommunicator().sendNormalServerMessage("You decide to do nothing.");
                        return;
                    }
                    this.pageNo = 2;
                    this.sortBy = 1;
                    this.hasBack = true;
                }
                else {
                    for (final String key : this.getAnswer().stringPropertyNames()) {
                        if (key.startsWith("sort")) {
                            final String sid = key.substring(4);
                            this.sortBy = Integer.parseInt(sid);
                            break;
                        }
                        if (key.startsWith("assign")) {
                            final String sid = key.substring(6);
                            this.deliveryId = Integer.parseInt(sid);
                            final WagonerDeliveriesQuestion wdq = new WagonerDeliveriesQuestion(this.getResponder(), this.deliveryId, this.hasList, this.sortBy, this.pageNo, this.hasBack);
                            wdq.sendQuestion3();
                            return;
                        }
                    }
                }
                final WagonerDeliveriesQuestion wdq2 = new WagonerDeliveriesQuestion(this.getResponder(), this.deliveryId, this.hasList, this.sortBy, this.pageNo, this.hasBack);
                switch (this.pageNo) {
                    case 1: {
                        wdq2.sendQuestion();
                        break;
                    }
                    case 2: {
                        wdq2.sendQuestion2();
                        break;
                    }
                }
            }
            case 2: {
                final boolean cancel = this.getBooleanProp("cancel");
                if (cancel) {
                    final Delivery delivery = Delivery.getDelivery(this.deliveryId);
                    if (delivery != null && delivery.getState() == 0) {
                        delivery.setCancelled();
                        this.getResponder().getCommunicator().sendServerMessage("You have cancelled your delivery to " + delivery.getReceiverName() + ".", 255, 127, 127);
                    }
                    else if (delivery == null) {
                        this.getResponder().getCommunicator().sendNormalServerMessage("Delivery cannot be found!");
                    }
                    else {
                        this.getResponder().getCommunicator().sendNormalServerMessage("Delivery may not be cancelled as it has been accepted.");
                    }
                    return;
                }
                final boolean assign = this.getBooleanProp("assign");
                if (assign) {
                    final WagonerDeliveriesQuestion wdq3 = new WagonerDeliveriesQuestion(this.getResponder(), this.deliveryId, this.hasList, this.sortBy, this.pageNo, this.hasBack);
                    wdq3.sendQuestion3();
                    return;
                }
                final boolean back = this.getBooleanProp("back");
                if (back) {
                    final WagonerDeliveriesQuestion wdq4 = new WagonerDeliveriesQuestion(this.getResponder(), -10L, true, 1, 1, false);
                    wdq4.sendQuestion();
                    return;
                }
                final boolean reject = this.getBooleanProp("reject");
                if (reject) {
                    final Delivery delivery2 = Delivery.getDelivery(this.deliveryId);
                    delivery2.setRejected();
                    this.getResponder().getCommunicator().sendNormalServerMessage("Delivery rejected from " + delivery2.getSenderName());
                    try {
                        final Player player = Players.getInstance().getPlayer(delivery2.getSenderId());
                        player.getCommunicator().sendNormalServerMessage("Delivery rejected by " + delivery2.getReceiverName());
                    }
                    catch (NoSuchPlayerException ex) {}
                    break;
                }
                break;
            }
            case 3: {
                final boolean close = this.getBooleanProp("close");
                if (close) {
                    return;
                }
                final boolean cancel2 = this.getBooleanProp("cancel");
                if (cancel2) {
                    final Delivery delivery3 = Delivery.getDelivery(this.deliveryId);
                    delivery3.setCancelledNoWagoner();
                }
                else {
                    final String sel2 = aAnswer.getProperty("sel");
                    final long selId = Long.parseLong(sel2);
                    if (selId == -10L) {
                        this.getResponder().getCommunicator().sendNormalServerMessage("No wagoner selected.");
                    }
                    else {
                        final Delivery delivery4 = Delivery.getDelivery(this.deliveryId);
                        delivery4.setWagonerId(selId);
                    }
                }
                final WagonerDeliveriesQuestion wdq3 = new WagonerDeliveriesQuestion(this.getResponder(), this.deliveryId, this.hasList, this.sortBy, this.pageNo, this.hasBack);
                if (this.hasList) {
                    wdq3.sendQuestion();
                    break;
                }
                wdq3.sendQuestion2();
                break;
            }
        }
    }
    
    @Override
    public void sendQuestion() {
        this.pageNo = 1;
        final StringBuilder buf = new StringBuilder();
        buf.append(this.getBmlHeader());
        buf.append("text{text=\"\"}");
        final Delivery[] deliveries = Delivery.getPendingDeliveries(this.getResponder().getWurmId());
        final int absSortBy = Math.abs(this.sortBy);
        final int upDown = Integer.signum(this.sortBy);
        switch (absSortBy) {
            case 7: {
                Arrays.sort(deliveries, new Comparator<Delivery>() {
                    @Override
                    public int compare(final Delivery param1, final Delivery param2) {
                        return param1.getSenderName().compareTo(param2.getSenderName()) * upDown;
                    }
                });
                break;
            }
            case 2: {
                Arrays.sort(deliveries, new Comparator<Delivery>() {
                    @Override
                    public int compare(final Delivery param1, final Delivery param2) {
                        return param1.getReceiverName().compareTo(param2.getReceiverName()) * upDown;
                    }
                });
                break;
            }
            case 3: {
                Arrays.sort(deliveries, new Comparator<Delivery>() {
                    @Override
                    public int compare(final Delivery param1, final Delivery param2) {
                        return param1.getStateName().compareTo(param2.getStateName()) * upDown;
                    }
                });
                break;
            }
            case 4: {
                Arrays.sort(deliveries, new Comparator<Delivery>() {
                    @Override
                    public int compare(final Delivery param1, final Delivery param2) {
                        if (param1.getCrates() < param2.getCrates()) {
                            return 1 * upDown;
                        }
                        return -1 * upDown;
                    }
                });
                break;
            }
            case 5: {
                Arrays.sort(deliveries, new Comparator<Delivery>() {
                    @Override
                    public int compare(final Delivery param1, final Delivery param2) {
                        return param1.getWagonerName().compareTo(param2.getWagonerName()) * upDown;
                    }
                });
                break;
            }
            case 6: {
                Arrays.sort(deliveries, new Comparator<Delivery>() {
                    @Override
                    public int compare(final Delivery param1, final Delivery param2) {
                        return param1.getWagonerState().compareTo(param2.getWagonerState()) * upDown;
                    }
                });
                break;
            }
            case 1: {
                Arrays.sort(deliveries, new Comparator<Delivery>() {
                    @Override
                    public int compare(final Delivery param1, final Delivery param2) {
                        if (param1.getWhen() < param2.getWhen()) {
                            return 1 * upDown;
                        }
                        return -1 * upDown;
                    }
                });
                break;
            }
        }
        buf.append("label{text=\"Select which delivery to view \"};");
        buf.append("table{rows=\"1\";cols=\"8\";label{text=\"\"};" + this.colHeader("Sender", 7, this.sortBy) + this.colHeader("Receiver", 2, this.sortBy) + this.colHeader("Delivery State", 3, this.sortBy) + this.colHeader("# Crates", 4, this.sortBy) + this.colHeader("Wagoner", 5, this.sortBy) + this.colHeader("Wagoner State", 6, this.sortBy) + this.colHeader("Last state change", 1, this.sortBy));
        final String noneSelected = "selected=\"true\";";
        for (final Delivery delivery : deliveries) {
            String rad;
            if (delivery.canSeeCrates()) {
                rad = "radio{group=\"sel\";id=\"" + delivery.getDeliveryId() + "\"text=\"\"}";
            }
            else {
                rad = "label{text=\"  \"};";
            }
            String wagonerName;
            String wagonerState;
            if (delivery.getWagonerId() == -10L && delivery.isQueued()) {
                wagonerName = "button{text=\"Assign wagoner\";id=\"assign" + delivery.getDeliveryId() + "\"}";
                wagonerState = "label{text=\"\"};";
            }
            else {
                wagonerName = "label{text=\"" + delivery.getWagonerName() + "\"};";
                wagonerState = "label{text=\"" + delivery.getWagonerState() + "\"};";
            }
            buf.append(rad + "label{text=\"" + delivery.getSenderName() + "\"};label{text=\"" + delivery.getReceiverName() + "\"};label{text=\"" + delivery.getStateName() + "\"};label{text=\"" + delivery.getCrates() + "\"};" + wagonerName + wagonerState + "label{text=\"" + delivery.getStringWhen() + "\"}");
        }
        buf.append("}");
        buf.append("radio{group=\"sel\";id=\"-10\";" + noneSelected + "text=\" None\"}");
        buf.append("text{text=\"\"}");
        buf.append("harray{label{text=\"Continue to \"};button{text=\"Next\";id=\"next\"}label{text=\" screen to view selected delivery.\"};}");
        buf.append("}};null;null;}");
        this.getResponder().getCommunicator().sendBml(600, 400, true, true, buf.toString(), 200, 200, 200, this.title);
    }
    
    public void sendQuestion2() {
        final Delivery delivery = Delivery.getDelivery(this.deliveryId);
        if (delivery == null) {
            this.getResponder().getCommunicator().sendNormalServerMessage("Delivery not found!");
            if (this.hasBack) {
                this.pageNo = 1;
                this.sendQuestion();
            }
            return;
        }
        this.pageNo = 2;
        final boolean hasCancel = delivery.getSenderId() == this.getResponder().getWurmId() && delivery.getState() == 0;
        final boolean hasReject = delivery.getReceiverId() == this.getResponder().getWurmId() && delivery.getState() == 0;
        final Creature creature = (delivery.getReceiverId() == this.getResponder().getWurmId()) ? this.getResponder() : null;
        final String buffer = showDelivery(delivery, this.getId(), creature, this.hasBack, false, false, hasReject, hasCancel);
        this.getResponder().getCommunicator().sendBml(400, 400, true, true, buffer, 200, 200, 200, this.title);
    }
    
    public void sendQuestion3() {
        final Delivery delivery = Delivery.getDelivery(this.deliveryId);
        if (delivery == null) {
            this.getResponder().getCommunicator().sendNormalServerMessage("Delivery not found!");
            if (this.hasBack) {
                this.pageNo = 1;
                this.sendQuestion();
            }
            return;
        }
        this.pageNo = 3;
        final Set<Creature> creatureSet = Creatures.getMayUseWagonersFor(this.getResponder());
        final Set<Distanced> wagonerSet = new HashSet<Distanced>();
        float dist = PathToCalculate.getRouteDistance(delivery.getCollectionWaystoneId(), delivery.getDeliveryWaystoneId());
        if (dist != 99999.0f) {
            for (final Creature creature : creatureSet) {
                final Wagoner wagoner = Wagoner.getWagoner(creature.getWurmId());
                if (wagoner != null) {
                    dist = PathToCalculate.getRouteDistance(wagoner.getHomeWaystoneId(), delivery.getCollectionWaystoneId());
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
        int height = 300;
        final StringBuilder buf = new StringBuilder();
        buf.append(this.getBmlHeader());
        buf.append("closebutton{id=\"close\"};");
        buf.append("label{text=\"Select which wagoner to use \"};");
        buf.append("table{rows=\"1\";cols=\"8\";label{text=\"\"};" + this.colHeader("Name", 1, this.sortBy) + this.colHeader("Type", 2, this.sortBy) + this.colHeader("Village", 3, this.sortBy) + this.colHeader("Distance", 4, this.sortBy) + this.colHeader("State", 5, this.sortBy) + this.colHeader("Queue", 6, this.sortBy) + "label{type=\"bold\";text=\"\"};");
        final String noneSelected = "selected=\"true\";";
        for (final Distanced distanced : wagonerArr) {
            buf.append((distanced.getVillageName().length() == 0) ? "label{text=\"\"}" : ("radio{group=\"sel\";id=\"" + distanced.getWagoner().getWurmId() + "\";text=\"\"}label{text=\"" + distanced.getWagoner().getName() + "\"};label{text=\"" + distanced.getType() + "\"};label{text=\"" + distanced.getVillageName() + "\"};label{text=\"" + (int)distanced.getDistance() + "\"};label{text=\"" + distanced.getWagoner().getStateName() + "\"};label{text=\"" + distanced.getQueueLength() + "\"};label{text=\"\"}"));
        }
        buf.append("}");
        buf.append("radio{group=\"sel\";id=\"-10\";" + noneSelected + "text=\" None\"}");
        buf.append("text{text=\"\"}");
        String assignButton;
        if (wagonerArr.length > 0) {
            assignButton = "button{text=\"Assign\";id=\"assign\"};label{text=\" \"};";
        }
        else {
            assignButton = "";
        }
        buf.append("harray{" + assignButton + "button{text=\"Cancel Delivery\";id=\"cancel\";hover=\"This will remove this delivery from the wagoner queue\";confirm=\"You are about to cancel a delivery to " + delivery.getReceiverName() + ".\";question=\"Do you really want to do that?\"}label{text=\" \"};button{text=\"Back to Delivery\";id=\"back\"}label{text=\" \"};button{text=\"Close\";id=\"close\"}}");
        buf.append("}};null;null;}");
        height += wagonerArr.length * 20;
        this.getResponder().getCommunicator().sendBml(420, height, true, true, buf.toString(), 200, 200, 200, this.title);
    }
    
    static String showDelivery(final Delivery delivery, final int questionId, @Nullable final Creature creature, final boolean hasBack, final boolean isNotConnected, final boolean hasAccept, final boolean hasReject, final boolean hasCancel) {
        final StringBuilder buf = new StringBuilder();
        buf.append("border{scroll{vertical='true';horizontal='false';varray{rescale='true';passthrough{id='id';text='" + questionId + "'}");
        buf.append("text{text=\"Delivery of " + delivery.getCrates() + " crate" + ((delivery.getCrates() == 1) ? "" : "s") + " from " + delivery.getSenderName() + " to " + delivery.getReceiverName() + ".\"}");
        String wagonerName;
        if (delivery.getWagonerId() == -10L && delivery.isQueued()) {
            wagonerName = "harray{label{text=\"Using \"};button{text=\"Assign wagoner\";id=\"assign\"};label{text=\" \"};}";
        }
        else {
            wagonerName = "text{text=\"Using " + delivery.getWagonerName() + ".\"}";
        }
        buf.append(wagonerName);
        if (isNotConnected) {
            buf.append("label{text=\"This waystone is not connected to the collection waystone, so cannot accept it here.\"}");
        }
        else if (creature != null) {
            final long money = creature.getMoney();
            if (money <= 0L) {
                buf.append("text{text='You have no money in the bank.'}");
            }
            else {
                buf.append("text{text='You have " + new Change(money).getChangeString() + " in the bank.'}");
            }
        }
        buf.append("}};null;");
        buf.append("tree{id=\"t1\";cols=\"3\";showheader=\"true\";height=\"300\"col{text=\"QL\";width=\"50\"};col{text=\"DMG\";width=\"50\"};col{text=\"Weight\";width=\"50\"};");
        final Item crateContainer = delivery.getCrateContainer();
        if (crateContainer != null) {
            final Item[] itemsAsArray;
            final Item[] crates = itemsAsArray = crateContainer.getItemsAsArray();
            for (final Item crate : itemsAsArray) {
                buf.append(addCrate(crate));
            }
        }
        buf.append("}");
        buf.append("null;varray{");
        if (delivery.getSenderCost() > 0L) {
            buf.append("label{text=\"The delivery fees of " + new Change(delivery.getSenderCost()).getChangeString() + " have been paid for by " + delivery.getSenderName() + ".\"}");
        }
        else {
            final int deliveryFees = delivery.getCrates() * 100;
            buf.append("label{text=\"The delivery fees of " + new Change(deliveryFees).getChangeString() + " has been added into the goods cost.\"}");
        }
        if (delivery.getReceiverCost() == 0L) {
            buf.append("label{text=\"The goods have already been paid for by " + delivery.getSenderName() + ".\"}");
        }
        else if (delivery.getState() == 0) {
            buf.append("label{text=\"The goods cost of " + new Change(delivery.getReceiverCost()).getChangeString() + " have yet to be agreed by " + delivery.getReceiverName() + ".\"}");
        }
        else {
            buf.append("label{text=\"The goods cost of " + new Change(delivery.getReceiverCost()).getChangeString() + " has been paid for by " + delivery.getReceiverName() + ".\"}");
        }
        buf.append("label{text=\"All monies are held by the wagoner until the delivery is complete.\"}");
        buf.append("harray{" + (hasAccept ? "button{text=\"Accept\";id=\"accept\"};label{text=\" \"};" : "") + (hasReject ? "button{text=\"Reject\";id=\"reject\"};label{text=\" \"};" : "") + ((!hasAccept && !hasReject) ? "button{text=\"Close\";id=\"submit\"};label{text=\" \"};" : "") + (hasCancel ? ("button{text=\"Cancel Delivery\";id=\"cancel\";hover=\"This will remove this delivery from the wagoner queue\";confirm=\"You are about to cancel a delivery to " + delivery.getReceiverName() + ".\";question=\"Do you really want to do that?\"}label{text=\" \"};") : "") + (hasBack ? "button{text=\"Back to list\";id=\"back\"};" : "") + "}");
        buf.append("text=\"\"}}");
        return buf.toString();
    }
    
    private static String addCrate(final Item crate) {
        final StringBuilder buf = new StringBuilder();
        final String sQL = "" + WagonerDeliveriesQuestion.df.format(crate.getQualityLevel());
        final String sDMG = "" + WagonerDeliveriesQuestion.df.format(crate.getDamage());
        final String sWeight = "" + WagonerDeliveriesQuestion.df.format(crate.getFullWeight(true) / 1000.0f);
        final String hover;
        final String itemName = hover = longItemName(crate);
        final Item[] contained = crate.getItemsAsArray();
        final int children = contained.length;
        buf.append("row{id=\"" + crate.getWurmId() + "\";hover=\"" + hover + "\";name=\"" + itemName + "\";rarity=\"" + crate.getRarity() + "\";children=\"" + children + "\";col{text=\"" + sQL + "\"};col{text=\"" + sDMG + "\"};col{text=\"" + sWeight + "\"}}");
        for (final Item bulkItem : contained) {
            buf.append(addBulkItem(bulkItem));
        }
        return buf.toString();
    }
    
    private static String addBulkItem(final Item bulkItem) {
        final StringBuilder buf = new StringBuilder();
        final String sQL = "" + WagonerDeliveriesQuestion.df.format(bulkItem.getQualityLevel());
        final String sWeight = "" + WagonerDeliveriesQuestion.df.format(bulkItem.getFullWeight(true) / 1000.0f);
        final String hover;
        final String itemName = hover = longItemName(bulkItem);
        buf.append("row{id=\"" + bulkItem.getWurmId() + "\";hover=\"" + hover + "\";name=\"" + itemName + "\";rarity=\"0\";children=\"0\";col{text=\"" + sQL + "\"};col{text=\"0.00\"};col{text=\"" + sWeight + "\"}}");
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
        logger = Logger.getLogger(WagonerDeliveriesQuestion.class.getName());
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
