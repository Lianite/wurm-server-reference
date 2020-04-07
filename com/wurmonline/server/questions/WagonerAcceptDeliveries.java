// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.questions;

import com.wurmonline.server.villages.Village;
import com.wurmonline.server.zones.VolaTile;
import com.wurmonline.server.zones.Zones;
import com.wurmonline.server.creatures.Wagoner;
import com.wurmonline.server.highways.PathToCalculate;
import java.util.Arrays;
import java.util.Comparator;
import com.wurmonline.server.players.Player;
import com.wurmonline.server.economy.Change;
import java.util.Iterator;
import com.wurmonline.server.NoSuchPlayerException;
import com.wurmonline.server.Players;
import java.io.IOException;
import java.util.logging.Level;
import com.wurmonline.server.economy.Economy;
import com.wurmonline.server.creatures.Delivery;
import java.util.Properties;
import com.wurmonline.server.creatures.Creature;
import com.wurmonline.server.items.Item;
import java.util.logging.Logger;
import com.wurmonline.server.economy.MonetaryConstants;

public class WagonerAcceptDeliveries extends Question implements MonetaryConstants
{
    private static final Logger logger;
    private static final String red = "color=\"255,127,127\";";
    private final Item waystone;
    private long deliveryId;
    private int sortBy;
    private int pageNo;
    
    public WagonerAcceptDeliveries(final Creature aResponder, final Item waystone) {
        super(aResponder, "Wagoner Accept Delivery Management", "Wagoner Accept Delivery Management", 146, waystone.getWurmId());
        this.deliveryId = -10L;
        this.sortBy = 1;
        this.pageNo = 1;
        this.waystone = waystone;
    }
    
    public WagonerAcceptDeliveries(final Creature aResponder, final Item waystone, final long deliveryId, final int sortBy, final int pageNo) {
        super(aResponder, "Wagoner Accept Delivery Management", "Wagoner Accept Delivery Management", 146, waystone.getWurmId());
        this.deliveryId = -10L;
        this.sortBy = 1;
        this.pageNo = 1;
        this.waystone = waystone;
        this.deliveryId = deliveryId;
        this.sortBy = sortBy;
        this.pageNo = pageNo;
    }
    
    @Override
    public void answer(final Properties aAnswer) {
        this.setAnswer(aAnswer);
        switch (this.pageNo) {
            case 1: {
                final boolean close = this.getBooleanProp("close");
                if (close) {
                    return;
                }
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
                final WagonerAcceptDeliveries wad = new WagonerAcceptDeliveries(this.getResponder(), this.waystone, this.deliveryId, this.sortBy, this.pageNo);
                switch (this.pageNo) {
                    case 1: {
                        wad.sendQuestion();
                        break;
                    }
                    case 2: {
                        wad.sendQuestion2();
                        break;
                    }
                }
            }
            case 2: {
                final boolean back = this.getBooleanProp("back");
                if (back) {
                    final WagonerAcceptDeliveries wad2 = new WagonerAcceptDeliveries(this.getResponder(), this.waystone, this.deliveryId, this.sortBy, 1);
                    wad2.sendQuestion();
                    return;
                }
                final boolean accept = this.getBooleanProp("accept");
                if (accept) {
                    final Delivery delivery = Delivery.getDelivery(this.deliveryId);
                    final long rmoney = this.getResponder().getMoney();
                    if (rmoney < delivery.getReceiverCost()) {
                        this.getResponder().getCommunicator().sendServerMessage("You cannot afford that delivery.", 255, 127, 127);
                        return;
                    }
                    boolean passed = true;
                    if (delivery.getReceiverCost() > 0L) {
                        try {
                            passed = this.getResponder().chargeMoney(delivery.getReceiverCost());
                            if (passed) {
                                final Change change = Economy.getEconomy().getChangeFor(this.getResponder().getMoney());
                                this.getResponder().getCommunicator().sendNormalServerMessage("You now have " + change.getChangeString() + " in the bank.");
                                this.getResponder().getCommunicator().sendNormalServerMessage("If this amount is incorrect, please wait a while since the information may not immediately be updated.");
                            }
                        }
                        catch (IOException e) {
                            passed = false;
                            this.getResponder().getCommunicator().sendServerMessage("Something went wrong!", 255, 127, 127);
                            WagonerAcceptDeliveries.logger.log(Level.WARNING, e.getMessage(), e);
                        }
                    }
                    if (passed) {
                        delivery.setAccepted(this.waystone.getWurmId());
                        this.getResponder().getCommunicator().sendServerMessage("Delivery accepted from " + delivery.getSenderName() + ".", 127, 255, 127);
                        try {
                            final Player player = Players.getInstance().getPlayer(delivery.getSenderId());
                            player.getCommunicator().sendServerMessage("Delivery accepted by " + delivery.getReceiverName() + ".", 127, 255, 127);
                        }
                        catch (NoSuchPlayerException ex) {}
                    }
                    return;
                }
                else {
                    final boolean reject = this.getBooleanProp("reject");
                    if (reject) {
                        final Delivery delivery2 = Delivery.getDelivery(this.deliveryId);
                        delivery2.setRejected();
                        this.getResponder().getCommunicator().sendNormalServerMessage("Delivery rejected from " + delivery2.getSenderName());
                        try {
                            final Player player2 = Players.getInstance().getPlayer(delivery2.getSenderId());
                            player2.getCommunicator().sendNormalServerMessage("Delivery rejected by " + delivery2.getReceiverName());
                        }
                        catch (NoSuchPlayerException ex2) {}
                        break;
                    }
                    break;
                }
                break;
            }
        }
    }
    
    @Override
    public void sendQuestion() {
        final StringBuilder buf = new StringBuilder();
        buf.append(this.getBmlHeader());
        buf.append("text{text=\"\"}");
        buf.append("text{text=\"The following bulk deliveries are waiting, but you need to accept them first before their delivery can start.\"}");
        buf.append("text{text=\"If an delivery has a Cash On Delivery (C.O.D) cost associated with it, you have to pay upfront before the delivery will start, the monies will be held by the wagoner and will only be paid to the supplier when the delivery is complete.\"}");
        final long money = this.getResponder().getMoney();
        if (money <= 0L) {
            buf.append("text{text='You have no money in the bank.'}");
        }
        else {
            buf.append("text{text='You have " + new Change(money).getChangeString() + " in the bank.'}");
        }
        final Delivery[] deliveries = Delivery.getWaitingDeliveries(this.getResponder().getWurmId());
        final int absSortBy = Math.abs(this.sortBy);
        final int upDown = Integer.signum(this.sortBy);
        switch (absSortBy) {
            case 1: {
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
            case 7: {
                Arrays.sort(deliveries, new Comparator<Delivery>() {
                    @Override
                    public int compare(final Delivery param1, final Delivery param2) {
                        if (param1.getReceiverCost() < param2.getReceiverCost()) {
                            return 1 * upDown;
                        }
                        return -1 * upDown;
                    }
                });
                break;
            }
        }
        buf.append("label{text=\"Select which delivery to view \"};");
        buf.append("table{rows=\"1\";cols=\"9\";label{text=\"\"};" + this.colHeader("Sender", 1, this.sortBy) + this.colHeader("Receiver", 2, this.sortBy) + this.colHeader("Delivery State", 3, this.sortBy) + this.colHeader("# Crates", 4, this.sortBy) + this.colHeader("Wagoner", 5, this.sortBy) + this.colHeader("Wagoner State", 6, this.sortBy) + this.colHeader("Cost", 7, this.sortBy) + "label{type=\"bold\";text=\"\"};");
        String noneSelected = "selected=\"true\";";
        for (final Delivery delivery : deliveries) {
            final boolean sameWaystone = delivery.getCollectionWaystoneId() == this.waystone.getWurmId();
            final boolean connected = !sameWaystone && PathToCalculate.isWaystoneConnected(delivery.getCollectionWaystoneId(), this.waystone.getWurmId());
            String selected = "";
            if (this.deliveryId == delivery.getDeliveryId()) {
                selected = "selected=\"true\";";
                noneSelected = "";
            }
            buf.append((connected ? ("radio{group=\"sel\";id=\"" + delivery.getDeliveryId() + "\";" + selected + "text=\"\"};") : "label{text=\"  \"};") + "label{text=\"" + delivery.getSenderName() + "\"};label{text=\"" + delivery.getReceiverName() + "\"};label{text=\"" + delivery.getStateName() + "\"};label{text=\"" + delivery.getCrates() + "\"};label{text=\"" + delivery.getWagonerName() + "\"};label{text=\"" + delivery.getWagonerState() + "\"};label{text=\"" + new Change(delivery.getReceiverCost()).getChangeShortString() + "\"};");
            if (sameWaystone) {
                buf.append("label{color=\"255,127,127\";text=\"same waystone\";hover=\"Waystone is the collection one, no need for a wagoner.\"}");
            }
            else if (!connected) {
                buf.append("label{color=\"255,127,127\";text=\"no route!\";hover=\"No route found from collection waystone to this waystone.\"}");
            }
            else {
                buf.append("label{text=\"\"}");
            }
        }
        buf.append("}");
        buf.append("radio{group=\"sel\";id=\"-10\";" + noneSelected + "text=\" None\"}");
        buf.append("text{text=\"\"}");
        if (this.waystone.getData() != -1L) {
            final Wagoner wagoner = Wagoner.getWagoner(this.waystone.getData());
            if (wagoner == null) {
                WagonerAcceptDeliveries.logger.log(Level.WARNING, "wagoner (" + this.waystone.getData() + ") not found that was associated with waystone " + this.waystone.getWurmId() + " @" + this.waystone.getTileX() + "," + this.waystone.getTileY() + "," + this.waystone.isOnSurface());
                this.waystone.setData(-1L);
            }
            else {
                buf.append("label{color=\"255,127,127\";text=\"This waystone is the home of " + wagoner.getName() + " and they wont allow deliveries here.\"};");
                buf.append("harray{button{text=\"Close\";id=\"close\"}}");
            }
        }
        if (this.waystone.getData() == -1L) {
            final VolaTile vt = Zones.getTileOrNull(this.waystone.getTileX(), this.waystone.getTileY(), this.waystone.isOnSurface());
            final Village village = (vt != null) ? vt.getVillage() : null;
            if (village != null && !village.isActionAllowed((short)605, this.getResponder())) {
                buf.append("label{color=\"255,127,127\";text=\"You need Load permissions to be able to accept a delivery here!\"};");
                buf.append("harray{button{text=\"Close\";id=\"close\"}}");
            }
            else {
                buf.append("harray{label{text=\"Continue to \"};button{text=\"Next\";id=\"next\"}label{text=\" screen to view selected delivery.\"};}");
            }
        }
        buf.append("}};null;null;}");
        this.getResponder().getCommunicator().sendBml(600, 400, true, true, buf.toString(), 200, 200, 200, this.title);
    }
    
    public void sendQuestion2() {
        final Delivery delivery = Delivery.getDelivery(this.deliveryId);
        if (delivery == null) {
            this.getResponder().getCommunicator().sendNormalServerMessage("Delivery not found!");
            this.pageNo = 1;
            this.sendQuestion();
            return;
        }
        this.pageNo = 2;
        final long money = this.getResponder().getMoney();
        final boolean connected = PathToCalculate.isWaystoneConnected(delivery.getCollectionWaystoneId(), this.waystone.getWurmId());
        final boolean hasAccept = money >= delivery.getReceiverCost() && connected;
        final String buffer = WagonerDeliveriesQuestion.showDelivery(delivery, this.getId(), this.getResponder(), true, !connected, hasAccept, true, false);
        this.getResponder().getCommunicator().sendBml(400, 400, true, true, buffer, 200, 200, 200, this.title);
    }
    
    static {
        logger = Logger.getLogger(WagonerAcceptDeliveries.class.getName());
    }
}
