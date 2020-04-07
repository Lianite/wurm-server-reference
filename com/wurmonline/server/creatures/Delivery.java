// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.creatures;

import java.util.concurrent.ConcurrentHashMap;
import java.sql.ResultSet;
import java.sql.PreparedStatement;
import java.sql.Connection;
import java.sql.Statement;
import com.wurmonline.server.utils.DbUtilities;
import java.sql.SQLException;
import com.wurmonline.server.DbConnector;
import java.util.List;
import java.util.Collections;
import java.util.LinkedList;
import java.util.Iterator;
import java.util.Set;
import java.util.HashSet;
import com.wurmonline.server.LoginServerWebConnection;
import com.wurmonline.server.villages.Village;
import com.wurmonline.server.villages.NoSuchVillageException;
import com.wurmonline.server.economy.Economy;
import com.wurmonline.server.villages.Villages;
import com.wurmonline.server.players.Player;
import com.wurmonline.server.NoSuchPlayerException;
import com.wurmonline.server.Players;
import java.util.Date;
import javax.annotation.Nullable;
import com.wurmonline.server.NoSuchItemException;
import java.util.logging.Level;
import com.wurmonline.server.Items;
import com.wurmonline.server.items.Item;
import com.wurmonline.server.economy.Change;
import com.wurmonline.server.players.PlayerInfoFactory;
import java.util.Map;
import java.text.SimpleDateFormat;
import java.util.logging.Logger;
import com.wurmonline.server.MiscConstants;

public class Delivery implements MiscConstants, Comparable<Delivery>
{
    private static final Logger logger;
    public static final byte STATE_WAITING_FOR_ACCEPT = 0;
    public static final byte STATE_QUEUED = 1;
    public static final byte STATE_WAITING_FOR_PICKUP = 2;
    public static final byte STATE_BEING_DELIVERED = 3;
    public static final byte STATE_DELIVERED = 4;
    public static final byte STATE_REJECTING = 5;
    public static final byte STATE_TIMEING_OUT = 6;
    public static final byte STATE_COMPLETED = 7;
    public static final byte STATE_REJECTED = 8;
    public static final byte STATE_CANCELLING = 9;
    public static final byte STATE_CANCELLED = 10;
    public static final byte STATE_TIMED_OUT = 11;
    public static final byte STATE_CANCELLING_NO_WAGONER = 12;
    private final SimpleDateFormat df;
    private static final String CREATE_DELIVERY = "INSERT INTO DELIVERYQUEUE (STATE,COLLECTION_WAYSTONE_ID,CONTAINER_ID,CRATES,SENDER_ID,SENDER_COST,RECEIVER_ID,RECEIVER_COST,DELIVERY_WAYSTONE_ID,WAGONER_ID,TS_EXPIRY,TS_WAITING_FOR_ACCEPT,TS_ACCEPTED_OR_REJECTED,TS_DELIVERY_STARTED,TS_PICKED_UP,TS_DELIVERED) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
    private static final String UPDATE_DELIVERY_ACCEPT = "UPDATE DELIVERYQUEUE SET STATE=?,DELIVERY_WAYSTONE_ID=?,TS_ACCEPTED_OR_REJECTED=? WHERE DELIVERY_ID=?";
    private static final String UPDATE_DELIVERY_REJECT = "UPDATE DELIVERYQUEUE SET STATE=?,TS_ACCEPTED_OR_REJECTED=?,WAGONER_ID=? WHERE DELIVERY_ID=?";
    private static final String UPDATE_DELIVERY_STARTED = "UPDATE DELIVERYQUEUE SET STATE=?,TS_DELIVERY_STARTED=? WHERE DELIVERY_ID=?";
    private static final String UPDATE_DELIVERY_PICKED_UP = "UPDATE DELIVERYQUEUE SET STATE=?,TS_PICKED_UP=? WHERE DELIVERY_ID=?";
    private static final String UPDATE_DELIVERY_DELIVERED = "UPDATE DELIVERYQUEUE SET STATE=?,TS_DELIVERED=? WHERE DELIVERY_ID=?";
    private static final String UPDATE_DELIVERY_STATE = "UPDATE DELIVERYQUEUE SET STATE=? WHERE DELIVERY_ID=?";
    private static final String UPDATE_DELIVERY_WAGONER = "UPDATE DELIVERYQUEUE SET WAGONER_ID=? WHERE DELIVERY_ID=?";
    private static final String UPDATE_DELIVERY_CONTAINER = "UPDATE DELIVERYQUEUE SET CONTAINER_ID=? WHERE DELIVERY_ID=?";
    private static final String DELETE_DELIVERY = "DELETE FROM DELIVERYQUEUE WHERE DELIVERY_ID=?";
    private static final String GET_ALL_DELIVERIES = "SELECT * FROM DELIVERYQUEUE ORDER BY DELIVERY_ID";
    private static final Map<Long, Delivery> deliveryQueue;
    private static final Map<Long, Delivery> containerDelivery;
    private final long deliveryId;
    private byte state;
    private final long collectionWaystoneId;
    private long containerId;
    private final int crates;
    private final long senderId;
    private final long senderCost;
    private final long receiverId;
    private final long receiverCost;
    private long deliveryWaystoneId;
    private long wagonerId;
    private final long tsExpiry;
    private long tsWaitingForAccept;
    private long tsAcceptedOrRejected;
    private long tsDeliveryStarted;
    private long tsPickedUp;
    private long tsDelivered;
    private long lastChecked;
    
    public Delivery(final long deliveryId, final byte state, final long collectionWaystoneId, final long containerId, final int crates, final long senderId, final long senderCost, final long receiverId, final long receiverCost, final long deliveryWaystoneId, final long wagonerId, final long tsExpiry, final long tsWaitingForAccept, final long tsAcceptedOrRejected, final long tsDeliveryStarted, final long tsPickedUp, final long tsDelivered) {
        this.df = new SimpleDateFormat("dd-MMM-yyyy HH:mm");
        this.tsWaitingForAccept = 0L;
        this.tsAcceptedOrRejected = 0L;
        this.tsDeliveryStarted = 0L;
        this.tsPickedUp = 0L;
        this.tsDelivered = 0L;
        this.lastChecked = 0L;
        this.deliveryId = deliveryId;
        this.state = state;
        this.collectionWaystoneId = collectionWaystoneId;
        this.containerId = containerId;
        this.crates = crates;
        this.senderId = senderId;
        this.senderCost = senderCost;
        this.receiverId = receiverId;
        this.receiverCost = receiverCost;
        this.deliveryWaystoneId = deliveryWaystoneId;
        this.wagonerId = wagonerId;
        this.tsExpiry = tsExpiry;
        this.tsWaitingForAccept = tsWaitingForAccept;
        this.tsAcceptedOrRejected = tsAcceptedOrRejected;
        this.tsDeliveryStarted = tsDeliveryStarted;
        this.tsPickedUp = tsPickedUp;
        this.tsDelivered = tsDelivered;
        addDelivery(this);
        if (containerId != -10L) {
            Delivery.containerDelivery.put(containerId, this);
        }
    }
    
    @Override
    public int compareTo(final Delivery otherDelivery) {
        if (this.getWhenAcceptedOrRejected() == otherDelivery.getWhenAcceptedOrRejected()) {
            return 0;
        }
        if (this.getWhenAcceptedOrRejected() < otherDelivery.getWhenAcceptedOrRejected()) {
            return -1;
        }
        return 1;
    }
    
    public long getDeliveryId() {
        return this.deliveryId;
    }
    
    public byte getState() {
        return this.state;
    }
    
    public long getCollectionWaystoneId() {
        return this.collectionWaystoneId;
    }
    
    public long getContainerId() {
        return this.containerId;
    }
    
    public void clrContainerId() {
        this.containerId = -10L;
        dbUpdateDeliveryContainer(this.deliveryId, this.containerId);
    }
    
    public int getCrates() {
        return this.crates;
    }
    
    public long getSenderId() {
        return this.senderId;
    }
    
    public String getSenderName() {
        return PlayerInfoFactory.getPlayerName(this.senderId);
    }
    
    public long getSenderCost() {
        return this.senderCost;
    }
    
    public String getSenderCostString() {
        if (this.senderCost <= 0L) {
            return "none";
        }
        return new Change(this.senderCost).getChangeShortString();
    }
    
    public long getReceiverId() {
        return this.receiverId;
    }
    
    public String getReceiverName() {
        return PlayerInfoFactory.getPlayerName(this.receiverId);
    }
    
    public long getReceiverCost() {
        return this.receiverCost;
    }
    
    public String getReceiverCostString() {
        if (this.receiverCost <= 0L) {
            return "none";
        }
        return new Change(this.receiverCost).getChangeShortString();
    }
    
    public long getDeliveryWaystoneId() {
        return this.deliveryWaystoneId;
    }
    
    public long getWagonerId() {
        return this.wagonerId;
    }
    
    public String getWagonerName() {
        final Wagoner wag = Wagoner.getWagoner(this.wagonerId);
        if (wag == null) {
            return "on vacation!";
        }
        return wag.getName();
    }
    
    public String getWagonerState() {
        final Wagoner wag = Wagoner.getWagoner(this.wagonerId);
        if (wag == null) {
            return "on strike!";
        }
        if (wag.getDeliveryId() != this.deliveryId && wag.getDeliveryId() != -10L) {
            return "busy";
        }
        return wag.getStateName();
    }
    
    @Nullable
    public Item getCrateContainer() {
        long crateContainerId = -10L;
        final Wagoner wag = Wagoner.getWagoner(this.wagonerId);
        if (wag == null) {
            crateContainerId = this.containerId;
        }
        else if (wag.getDeliveryId() != this.deliveryId) {
            crateContainerId = this.containerId;
        }
        else {
            switch (wag.getState()) {
                case 0:
                case 1:
                case 2:
                case 3:
                case 4:
                case 5: {
                    crateContainerId = this.containerId;
                    break;
                }
                case 7: {
                    crateContainerId = wag.getWagonId();
                    break;
                }
                default: {
                    crateContainerId = -10L;
                    break;
                }
            }
        }
        if (crateContainerId == -10L) {
            return null;
        }
        try {
            return Items.getItem(crateContainerId);
        }
        catch (NoSuchItemException e) {
            Delivery.logger.log(Level.WARNING, e.getMessage(), e);
            return null;
        }
    }
    
    public boolean canSeeCrates() {
        switch (this.state) {
            case 0:
            case 1:
            case 2:
            case 3: {
                return true;
            }
            default: {
                return false;
            }
        }
    }
    
    public boolean isPreDelivery() {
        switch (this.state) {
            case 0:
            case 1:
            case 2:
            case 3: {
                return true;
            }
            default: {
                return false;
            }
        }
    }
    
    public boolean isQueued() {
        switch (this.state) {
            case 1: {
                return true;
            }
            default: {
                return false;
            }
        }
    }
    
    public boolean isWaitingForAccept() {
        switch (this.state) {
            case 0: {
                return true;
            }
            default: {
                return false;
            }
        }
    }
    
    public boolean canViewDelivery() {
        switch (this.state) {
            case 0:
            case 1:
            case 2: {
                return true;
            }
            default: {
                return false;
            }
        }
    }
    
    public boolean inProgress() {
        switch (this.state) {
            case 2:
            case 3: {
                return true;
            }
            default: {
                return false;
            }
        }
    }
    
    public boolean isComplete() {
        switch (this.state) {
            case 4:
            case 7: {
                return true;
            }
            default: {
                return false;
            }
        }
    }
    
    public boolean wasRejected() {
        switch (this.state) {
            case 5:
            case 6:
            case 8:
            case 11: {
                return true;
            }
            default: {
                return false;
            }
        }
    }
    
    public long getWhen() {
        switch (this.state) {
            case 0: {
                return this.tsWaitingForAccept;
            }
            case 1: {
                return this.tsAcceptedOrRejected;
            }
            case 2: {
                return this.tsDeliveryStarted;
            }
            case 3: {
                return this.tsPickedUp;
            }
            case 4: {
                return this.tsDelivered;
            }
            case 5:
            case 6:
            case 7:
            case 8:
            case 9:
            case 10:
            case 11:
            case 12: {
                return this.tsAcceptedOrRejected;
            }
            default: {
                return 0L;
            }
        }
    }
    
    public String getStringWhen() {
        final long ts = this.getWhen();
        if (ts == 0L) {
            return "";
        }
        return this.df.format(new Date(ts));
    }
    
    public long getWhenWaitingForAccept() {
        return this.tsWaitingForAccept;
    }
    
    public String getStringWaitingForAccept() {
        if (this.tsWaitingForAccept == 0L) {
            return "";
        }
        return this.df.format(new Date(this.tsWaitingForAccept));
    }
    
    public long getWhenAcceptedOrRejected() {
        return this.tsAcceptedOrRejected;
    }
    
    public String getStringAcceptedOrRejected() {
        if (this.tsAcceptedOrRejected == 0L) {
            return "";
        }
        return this.df.format(new Date(this.tsAcceptedOrRejected));
    }
    
    public long getWhenDeliveryStarted() {
        return this.tsDeliveryStarted;
    }
    
    public String getStringDeliveryStarted() {
        if (this.tsDeliveryStarted == 0L) {
            return "";
        }
        return this.df.format(new Date(this.tsDeliveryStarted));
    }
    
    public long getWhenPickedUp() {
        return this.tsPickedUp;
    }
    
    public String getStringPickedUp() {
        if (this.tsPickedUp == 0L) {
            return "";
        }
        return this.df.format(new Date(this.tsPickedUp));
    }
    
    public long getWhenDelivered() {
        return this.tsDelivered;
    }
    
    public String getStringDelivered() {
        if (this.tsDelivered == 0L) {
            return "";
        }
        return this.df.format(new Date(this.tsDelivered));
    }
    
    public void remove() {
        removeDelivery(this);
    }
    
    public String getStateName() {
        switch (this.state) {
            case 0: {
                return "waiting for accept";
            }
            case 1: {
                return "queued";
            }
            case 2: {
                return "waiting for pickup";
            }
            case 3: {
                return "being delivered";
            }
            case 4: {
                return "delivered";
            }
            case 5: {
                return "rejecting";
            }
            case 6: {
                return "timing out";
            }
            case 11: {
                return "timed out";
            }
            case 7: {
                return "completed";
            }
            case 8: {
                return "rejected";
            }
            case 9:
            case 12: {
                return "cancelling";
            }
            case 10: {
                return "cancelled";
            }
            default: {
                return "";
            }
        }
    }
    
    public void setAccepted(final long waystoneId) {
        this.tsAcceptedOrRejected = System.currentTimeMillis();
        this.state = 1;
        this.deliveryWaystoneId = waystoneId;
        dbUpdateDeliveryAccept(this.deliveryId, this.state, this.deliveryWaystoneId, this.tsAcceptedOrRejected);
    }
    
    public void setRejected() {
        this.tsAcceptedOrRejected = System.currentTimeMillis();
        this.state = 5;
        this.wagonerId = -10L;
        dbUpdateDeliveryReject(this.deliveryId, this.state, this.tsAcceptedOrRejected);
        this.updateContainerVisuals();
        this.checkPayment(true);
    }
    
    public void setCancelled() {
        this.tsAcceptedOrRejected = System.currentTimeMillis();
        this.state = 9;
        this.wagonerId = -10L;
        dbUpdateDeliveryReject(this.deliveryId, this.state, this.tsAcceptedOrRejected);
        this.updateContainerVisuals();
        this.checkPayment(true);
    }
    
    public void setCancelledNoWagoner() {
        this.tsAcceptedOrRejected = System.currentTimeMillis();
        this.state = 12;
        this.wagonerId = -10L;
        dbUpdateDeliveryReject(this.deliveryId, this.state, this.tsAcceptedOrRejected);
        this.updateContainerVisuals();
        this.checkPayment(true);
    }
    
    public void setTimingOut() {
        this.tsAcceptedOrRejected = System.currentTimeMillis();
        this.state = 6;
        this.wagonerId = -10L;
        dbUpdateDeliveryReject(this.deliveryId, this.state, this.tsAcceptedOrRejected);
        this.updateContainerVisuals();
        try {
            final Player player = Players.getInstance().getPlayer(this.getSenderId());
            player.getCommunicator().sendServerMessage("Delivery to " + this.getReceiverName() + " timed out, was not accepted in time.", 255, 127, 127);
        }
        catch (NoSuchPlayerException ex) {}
        try {
            final Player player = Players.getInstance().getPlayer(this.getReceiverId());
            player.getCommunicator().sendServerMessage("Delivery from " + this.getSenderName() + " timed out, was not accepted in time.", 255, 127, 127);
        }
        catch (NoSuchPlayerException ex2) {}
        this.checkPayment(true);
    }
    
    public void setStarted() {
        this.tsDeliveryStarted = System.currentTimeMillis();
        this.state = 2;
        dbUpdateDeliveryStarted(this.deliveryId, this.state, this.tsDeliveryStarted);
    }
    
    public void setPickedUp() {
        this.tsPickedUp = System.currentTimeMillis();
        this.state = 3;
        dbUpdateDeliveryPickedUp(this.deliveryId, this.state, this.tsPickedUp);
        this.clrContainerId();
        this.updateContainerVisuals();
    }
    
    public void setDelivered() {
        this.tsDelivered = System.currentTimeMillis();
        this.state = 4;
        dbUpdateDeliveryDelivered(this.deliveryId, this.state, this.tsDelivered);
        final int wagonersCut = this.getCrates() * 100;
        final Wagoner wagoner = Wagoner.getWagoner(this.wagonerId);
        if (wagoner != null && wagoner.getVillageId() != -1) {
            try {
                final Village village = Villages.getVillage(wagoner.getVillageId());
                final int deedCut = (int)(wagonersCut * 0.2f);
                village.plan.addMoney(deedCut);
                village.plan.addPayment(wagoner.getName(), wagoner.getWurmId(), deedCut);
                final Change newch = Economy.getEconomy().getChangeFor(deedCut);
                village.addHistory(wagoner.getName(), "added " + newch.getChangeString() + " to upkeep");
                Delivery.logger.log(Level.INFO, wagoner.getName() + " added " + deedCut + " irons to " + village.getName() + " upkeep.");
            }
            catch (NoSuchVillageException ex) {}
        }
        this.checkPayment(true);
    }
    
    public void updateContainerVisuals() {
        try {
            final Item container = Items.getItem(this.containerId);
            container.updateName();
        }
        catch (NoSuchItemException e) {
            Delivery.logger.log(Level.WARNING, e.getMessage(), e);
        }
    }
    
    public String getContainerDescription() {
        final String desc = "goods from " + this.getSenderName() + " to " + this.getReceiverName();
        switch (this.state) {
            case 1: {
                if (this.wagonerId == -10L) {
                    return desc + " [NO WAGONER}";
                }
                return desc;
            }
            case 5:
            case 8: {
                return desc + " [REJECTED]";
            }
            case 9:
            case 10: {
                return desc + " [CANCELLED]";
            }
            case 6:
            case 11: {
                return desc + " [TIMED OUT]";
            }
            default: {
                return desc;
            }
        }
    }
    
    private boolean pay(final long payTo, final String payName, final long cost, final String detail, final String message, final byte newState) {
        boolean paid = cost <= 0L;
        if (!paid) {
            final LoginServerWebConnection lsw = new LoginServerWebConnection();
            if (lsw.addMoney(payTo, payName, cost, detail)) {
                paid = true;
                try {
                    final Player player = Players.getInstance().getPlayer(payTo);
                    player.getCommunicator().sendNormalServerMessage(message);
                    final Change change = Economy.getEconomy().getChangeFor(player.getMoney());
                    player.getCommunicator().sendNormalServerMessage("You now have " + change.getChangeString() + " in the bank.");
                    player.getCommunicator().sendNormalServerMessage("If this amount is incorrect, please wait a while since the information may not immediately be updated.");
                }
                catch (NoSuchPlayerException ex) {}
            }
        }
        if (paid) {
            this.state = newState;
            dbUpdateState(this.deliveryId, this.state);
        }
        return paid;
    }
    
    public void setWagonerId(final long wagonerId) {
        this.wagonerId = wagonerId;
        dbUpdateDeliveryDismiss(this.deliveryId, this.wagonerId);
        this.updateContainerVisuals();
    }
    
    void checkPayment(final boolean force) {
        final long now = System.currentTimeMillis();
        if (!force && now < this.lastChecked + 60000L) {
            return;
        }
        this.lastChecked = now;
        switch (this.state) {
            case 0: {
                if (now > this.tsExpiry) {
                    this.setTimingOut();
                    break;
                }
                break;
            }
            case 12: {
                final boolean paid = this.pay(this.receiverId, this.getReceiverName(), this.receiverCost, "Refund goods cost for delivery " + this.getDeliveryId() + " as cancelled.", this.getSenderName() + " has cancelled the delivery as no wagoner.", (byte)9);
                if (!paid) {
                    break;
                }
            }
            case 9: {
                this.pay(this.senderId, this.getSenderName(), this.senderCost, "Refund delivery " + this.getDeliveryId() + " as cancelled.", "You have cancelled the delivery to " + this.getReceiverName() + ".", (byte)10);
                break;
            }
            case 5: {
                this.pay(this.senderId, this.getSenderName(), this.senderCost, "Refund delivery " + this.getDeliveryId() + " as rejected.", "Your delivery to " + this.getReceiverName() + " has been rejected.", (byte)8);
                break;
            }
            case 6: {
                this.pay(this.senderId, this.getSenderName(), this.senderCost, "Refund delivery " + this.getDeliveryId() + " as timed out.", "Your delivery to " + this.getReceiverName() + " has timed out.", (byte)11);
                break;
            }
            case 4: {
                final int deliveryCost = (this.senderCost == 0L) ? (this.getCrates() * 100) : 0;
                this.pay(this.senderId, this.getSenderName(), this.receiverCost - deliveryCost, "Delivery " + this.getDeliveryId() + " paid.", "Your delivery to " + this.getReceiverName() + " has been paid.", (byte)7);
                break;
            }
        }
    }
    
    private static final void removeDelivery(final Delivery delivery) {
        Delivery.deliveryQueue.remove(delivery.getDeliveryId());
        dbRemoveDelivery(delivery.getDeliveryId());
    }
    
    @Nullable
    public static final Delivery getDelivery(final long deliveryId) {
        return Delivery.deliveryQueue.get(deliveryId);
    }
    
    private static final void addDelivery(final Delivery delivery) {
        Delivery.deliveryQueue.put(delivery.getDeliveryId(), delivery);
    }
    
    public static final void addDelivery(final long collectionWaystoneId, final long containerId, final int crates, final long senderId, final long senderCost, final long receiverId, final long receiverCost, final long wagonerId) {
        final byte state = 0;
        final long deliveryWaystoneId = -10L;
        final long tsExpiry = System.currentTimeMillis() + 604800000L;
        final long tsWaitingForAccept = System.currentTimeMillis();
        final long tsAcceptedOrRejected = 0L;
        final long tsDeliveryStarted = 0L;
        final long tsPickedUp = 0L;
        final long tsDelivered = 0L;
        final long deliveryId = dbCreateDelivery((byte)0, collectionWaystoneId, containerId, crates, senderId, senderCost, receiverId, receiverCost, -10L, wagonerId, tsExpiry, tsWaitingForAccept, 0L, 0L, 0L, 0L);
        new Delivery(deliveryId, (byte)0, collectionWaystoneId, containerId, crates, senderId, senderCost, receiverId, receiverCost, -10L, wagonerId, tsExpiry, tsWaitingForAccept, 0L, 0L, 0L, 0L);
    }
    
    public static final Delivery[] getWaitingDeliveries(final long playerId) {
        final Set<Delivery> deliverySet = new HashSet<Delivery>();
        for (final Delivery delivery : Delivery.deliveryQueue.values()) {
            if (delivery.isWaitingForAccept() && delivery.getReceiverId() == playerId) {
                deliverySet.add(delivery);
            }
        }
        return deliverySet.toArray(new Delivery[deliverySet.size()]);
    }
    
    public static final Delivery[] getLostDeliveries(final long playerId) {
        final Set<Delivery> deliverySet = new HashSet<Delivery>();
        for (final Delivery delivery : Delivery.deliveryQueue.values()) {
            if (delivery.isQueued() && delivery.getSenderId() == playerId && delivery.getWagonerId() == -10L) {
                deliverySet.add(delivery);
            }
        }
        return deliverySet.toArray(new Delivery[deliverySet.size()]);
    }
    
    public static final Delivery[] getPendingDeliveries(final long playerId) {
        final Set<Delivery> deliverySet = new HashSet<Delivery>();
        for (final Delivery delivery : Delivery.deliveryQueue.values()) {
            if (delivery.getReceiverId() == playerId || delivery.getSenderId() == playerId) {
                deliverySet.add(delivery);
            }
        }
        return deliverySet.toArray(new Delivery[deliverySet.size()]);
    }
    
    public static final int countWaitingForAccept(final long wagonerId) {
        int count = 0;
        for (final Delivery delivery : Delivery.deliveryQueue.values()) {
            if (delivery.isWaitingForAccept() && delivery.getWagonerId() == wagonerId) {
                ++count;
            }
        }
        return count;
    }
    
    public static final void rejectWaitingForAccept(final long wagonerId) {
        for (final Delivery delivery : Delivery.deliveryQueue.values()) {
            if (delivery.isWaitingForAccept() && delivery.getWagonerId() == wagonerId) {
                delivery.setRejected();
            }
        }
    }
    
    public static final void clrWagonerQueue(final long wagonerId) {
        for (final Delivery delivery : Delivery.deliveryQueue.values()) {
            if (delivery.isQueued() && delivery.getWagonerId() == wagonerId) {
                delivery.setWagonerId(-10L);
            }
        }
    }
    
    public static final LinkedList<Delivery> getNextDeliveriesFor(final long wagonerId) {
        final LinkedList<Delivery> deliveries = new LinkedList<Delivery>();
        for (final Delivery delivery : Delivery.deliveryQueue.values()) {
            if (delivery.isQueued() && delivery.getWagonerId() == wagonerId) {
                deliveries.add(delivery);
            }
        }
        Collections.sort(deliveries);
        return deliveries;
    }
    
    public static final boolean hasNextDeliveryFor(final long wagonerId) {
        for (final Delivery delivery : Delivery.deliveryQueue.values()) {
            if (delivery.isQueued() && delivery.getWagonerId() == wagonerId) {
                return true;
            }
        }
        return false;
    }
    
    public static final Delivery[] getKnownDeliveries(final long playerId) {
        final Set<Delivery> deliverySet = new HashSet<Delivery>();
        for (final Delivery delivery : Delivery.deliveryQueue.values()) {
            if (delivery.getSenderId() == playerId || delivery.getReceiverId() == playerId) {
                deliverySet.add(delivery);
            }
        }
        return deliverySet.toArray(new Delivery[deliverySet.size()]);
    }
    
    public static final int getQueueLength(final long wagonerId) {
        int queueLength = 0;
        for (final Delivery delivery : Delivery.deliveryQueue.values()) {
            if (delivery.isQueued() && delivery.getWagonerId() == wagonerId) {
                ++queueLength;
            }
        }
        return queueLength;
    }
    
    public static final Delivery[] getDeliveriesFor(final long wagonerId, final boolean inQueue, final boolean waitAccept, final boolean inProgress, final boolean rejected, final boolean delivered) {
        final Set<Delivery> deliverySet = new HashSet<Delivery>();
        for (final Delivery delivery : Delivery.deliveryQueue.values()) {
            if (delivery.getWagonerId() == wagonerId) {
                if (inQueue && delivery.isQueued()) {
                    deliverySet.add(delivery);
                }
                if (waitAccept && delivery.isWaitingForAccept()) {
                    deliverySet.add(delivery);
                }
                if (inProgress && delivery.inProgress()) {
                    deliverySet.add(delivery);
                }
                if (rejected && delivery.wasRejected()) {
                    deliverySet.add(delivery);
                }
                if (!delivered || !delivery.isComplete()) {
                    continue;
                }
                deliverySet.add(delivery);
            }
        }
        return deliverySet.toArray(new Delivery[deliverySet.size()]);
    }
    
    @Nullable
    public static final Delivery getDeliveryFrom(final long containerId) {
        return Delivery.containerDelivery.get(containerId);
    }
    
    @Nullable
    public static final Delivery canViewDelivery(final Item container, final Creature creature) {
        for (final Delivery delivery : Delivery.deliveryQueue.values()) {
            if (delivery.getContainerId() == container.getWurmId() && delivery.canViewDelivery() && (delivery.getSenderId() == creature.getWurmId() || creature.getPower() >= 2)) {
                return delivery;
            }
        }
        return null;
    }
    
    @Nullable
    public static final boolean canUnSealContainer(final Item container, final Creature creature) {
        final Delivery delivery = getDeliveryFrom(container.getWurmId());
        return delivery != null && (!delivery.canViewDelivery() || delivery.getWagonerId() == -10L) && (delivery.getSenderId() == creature.getWurmId() || creature.getPower() >= 2);
    }
    
    public static final boolean isDeliveryPoint(final long waystoneId) {
        for (final Delivery delivery : Delivery.deliveryQueue.values()) {
            if (delivery.getDeliveryWaystoneId() == waystoneId && delivery.isPreDelivery()) {
                return true;
            }
        }
        return false;
    }
    
    public static void freeContainer(final long containerId) {
        final Delivery delivery = getDeliveryFrom(containerId);
        if (delivery != null) {
            delivery.clrContainerId();
            Delivery.containerDelivery.remove(containerId);
        }
    }
    
    public static String getContainerDescription(final long containerId) {
        final Delivery delivery = getDeliveryFrom(containerId);
        if (delivery != null) {
            return delivery.getContainerDescription();
        }
        return "";
    }
    
    public static void poll() {
        for (final Delivery delivery : Delivery.deliveryQueue.values()) {
            delivery.checkPayment(false);
        }
    }
    
    public static final void dbLoadAllDeliveries() {
        Delivery.logger.log(Level.INFO, "Loading all deliveries from delivery queue.");
        final long start = System.nanoTime();
        final long count = 0L;
        Connection dbcon = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            dbcon = DbConnector.getCreatureDbCon();
            ps = dbcon.prepareStatement("SELECT * FROM DELIVERYQUEUE ORDER BY DELIVERY_ID");
            rs = ps.executeQuery();
            while (rs.next()) {
                final long deliveryId = rs.getLong("DELIVERY_ID");
                final byte state = rs.getByte("STATE");
                final long collectionWaystoneId = rs.getLong("COLLECTION_WAYSTONE_ID");
                final long containerId = rs.getLong("CONTAINER_ID");
                final byte crates = rs.getByte("CRATES");
                final long senderId = rs.getLong("SENDER_ID");
                final long senderCost = rs.getLong("SENDER_COST");
                final long receiverId = rs.getLong("RECEIVER_ID");
                final long receiverCost = rs.getLong("RECEIVER_COST");
                final long deliveryWaystoneId = rs.getLong("DELIVERY_WAYSTONE_ID");
                final long wagonerId = rs.getLong("WAGONER_ID");
                final long tsExpiry = rs.getLong("TS_EXPIRY");
                final long tsWaitingForAccept = rs.getLong("TS_WAITING_FOR_ACCEPT");
                final long tsAcceptedOrRejected = rs.getLong("TS_ACCEPTED_OR_REJECTED");
                final long tsDeliveryStarted = rs.getLong("TS_DELIVERY_STARTED");
                final long tsPickedUp = rs.getLong("TS_PICKED_UP");
                final long tsDelivered = rs.getLong("TS_DELIVERED");
                new Delivery(deliveryId, state, collectionWaystoneId, containerId, crates, senderId, senderCost, receiverId, receiverCost, deliveryWaystoneId, wagonerId, tsExpiry, tsWaitingForAccept, tsAcceptedOrRejected, tsDeliveryStarted, tsPickedUp, tsDelivered);
            }
        }
        catch (SQLException sqex) {
            Delivery.logger.log(Level.WARNING, "Failed to load all deliveries: " + sqex.getMessage(), sqex);
        }
        finally {
            DbUtilities.closeDatabaseObjects(ps, rs);
            DbConnector.returnConnection(dbcon);
            final long end = System.nanoTime();
            Delivery.logger.log(Level.INFO, "Loaded " + count + " deliveries from delivery queue. That took " + (end - start) / 1000000.0f + " ms.");
        }
    }
    
    private static long dbCreateDelivery(final byte state, final long collectionWaystoneId, final long containerId, final int crates, final long senderId, final long senderCost, final long receiverId, final long receiverCost, final long deliveryWaystoneId, final long wagonerId, final long tsExpiry, final long tsWaitingForAccept, final long tsAcceptedOrRejected, final long tsDeliveryStarted, final long tsPickedUp, final long tsDelivered) {
        long deliveryId = -10L;
        Connection dbcon = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            dbcon = DbConnector.getCreatureDbCon();
            ps = dbcon.prepareStatement("INSERT INTO DELIVERYQUEUE (STATE,COLLECTION_WAYSTONE_ID,CONTAINER_ID,CRATES,SENDER_ID,SENDER_COST,RECEIVER_ID,RECEIVER_COST,DELIVERY_WAYSTONE_ID,WAGONER_ID,TS_EXPIRY,TS_WAITING_FOR_ACCEPT,TS_ACCEPTED_OR_REJECTED,TS_DELIVERY_STARTED,TS_PICKED_UP,TS_DELIVERED) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)", 1);
            ps.setByte(1, state);
            ps.setLong(2, collectionWaystoneId);
            ps.setLong(3, containerId);
            ps.setByte(4, (byte)crates);
            ps.setLong(5, senderId);
            ps.setLong(6, senderCost);
            ps.setLong(7, receiverId);
            ps.setLong(8, receiverCost);
            ps.setLong(9, deliveryWaystoneId);
            ps.setLong(10, wagonerId);
            ps.setLong(11, tsExpiry);
            ps.setLong(12, tsWaitingForAccept);
            ps.setLong(13, tsAcceptedOrRejected);
            ps.setLong(14, tsDeliveryStarted);
            ps.setLong(15, tsPickedUp);
            ps.setLong(16, tsDelivered);
            ps.executeUpdate();
            rs = ps.getGeneratedKeys();
            if (rs.next()) {
                deliveryId = rs.getLong(1);
            }
        }
        catch (SQLException ex) {
            Delivery.logger.log(Level.WARNING, "Failed to create delivery in deliveryQueue table.", ex);
        }
        finally {
            DbUtilities.closeDatabaseObjects(ps, rs);
            DbConnector.returnConnection(dbcon);
        }
        return deliveryId;
    }
    
    private static void dbRemoveDelivery(final long deliveryId) {
        Connection dbcon = null;
        PreparedStatement ps = null;
        final ResultSet rs = null;
        try {
            dbcon = DbConnector.getCreatureDbCon();
            ps = dbcon.prepareStatement("DELETE FROM DELIVERYQUEUE WHERE DELIVERY_ID=?");
            ps.setLong(1, deliveryId);
            ps.executeUpdate();
        }
        catch (SQLException ex) {
            Delivery.logger.log(Level.WARNING, "Failed to remove delivery " + deliveryId + " from deliveryQueue table.", ex);
        }
        finally {
            DbUtilities.closeDatabaseObjects(ps, rs);
            DbConnector.returnConnection(dbcon);
        }
    }
    
    private static void dbUpdateDeliveryAccept(final long deliveryId, final byte state, final long waystoneId, final long tsAcceptOrReject) {
        Connection dbcon = null;
        PreparedStatement ps = null;
        final ResultSet rs = null;
        try {
            dbcon = DbConnector.getCreatureDbCon();
            ps = dbcon.prepareStatement("UPDATE DELIVERYQUEUE SET STATE=?,DELIVERY_WAYSTONE_ID=?,TS_ACCEPTED_OR_REJECTED=? WHERE DELIVERY_ID=?");
            ps.setByte(1, state);
            ps.setLong(2, waystoneId);
            ps.setLong(3, tsAcceptOrReject);
            ps.setLong(4, deliveryId);
            ps.executeUpdate();
        }
        catch (SQLException ex) {
            Delivery.logger.log(Level.WARNING, "Failed to update delivery " + deliveryId + " to state " + state + " in deliveryQueue table.", ex);
        }
        finally {
            DbUtilities.closeDatabaseObjects(ps, rs);
            DbConnector.returnConnection(dbcon);
        }
    }
    
    private static void dbUpdateDeliveryReject(final long deliveryId, final byte state, final long tsAcceptOrReject) {
        Connection dbcon = null;
        PreparedStatement ps = null;
        final ResultSet rs = null;
        try {
            dbcon = DbConnector.getCreatureDbCon();
            ps = dbcon.prepareStatement("UPDATE DELIVERYQUEUE SET STATE=?,TS_ACCEPTED_OR_REJECTED=?,WAGONER_ID=? WHERE DELIVERY_ID=?");
            ps.setByte(1, state);
            ps.setLong(2, tsAcceptOrReject);
            ps.setLong(3, -10L);
            ps.setLong(4, deliveryId);
            ps.executeUpdate();
        }
        catch (SQLException ex) {
            Delivery.logger.log(Level.WARNING, "Failed to update delivery " + deliveryId + " to state " + state + " in deliveryQueue table.", ex);
        }
        finally {
            DbUtilities.closeDatabaseObjects(ps, rs);
            DbConnector.returnConnection(dbcon);
        }
    }
    
    private static void dbUpdateDeliveryDismiss(final long deliveryId, final long wagonerId) {
        Connection dbcon = null;
        PreparedStatement ps = null;
        final ResultSet rs = null;
        try {
            dbcon = DbConnector.getCreatureDbCon();
            ps = dbcon.prepareStatement("UPDATE DELIVERYQUEUE SET WAGONER_ID=? WHERE DELIVERY_ID=?");
            ps.setLong(1, wagonerId);
            ps.setLong(2, deliveryId);
            ps.executeUpdate();
        }
        catch (SQLException ex) {
            Delivery.logger.log(Level.WARNING, "Failed to update delivery " + deliveryId + " to wagoner " + wagonerId + " in deliveryQueue table.", ex);
        }
        finally {
            DbUtilities.closeDatabaseObjects(ps, rs);
            DbConnector.returnConnection(dbcon);
        }
    }
    
    private static void dbUpdateDeliveryContainer(final long deliveryId, final long containerId) {
        Connection dbcon = null;
        PreparedStatement ps = null;
        final ResultSet rs = null;
        try {
            dbcon = DbConnector.getCreatureDbCon();
            ps = dbcon.prepareStatement("UPDATE DELIVERYQUEUE SET CONTAINER_ID=? WHERE DELIVERY_ID=?");
            ps.setLong(1, containerId);
            ps.setLong(2, deliveryId);
            ps.executeUpdate();
        }
        catch (SQLException ex) {
            Delivery.logger.log(Level.WARNING, "Failed to update delivery " + deliveryId + " to container " + containerId + " in deliveryQueue table.", ex);
        }
        finally {
            DbUtilities.closeDatabaseObjects(ps, rs);
            DbConnector.returnConnection(dbcon);
        }
    }
    
    private static void dbUpdateDeliveryStarted(final long deliveryId, final byte state, final long tsStarted) {
        Connection dbcon = null;
        PreparedStatement ps = null;
        final ResultSet rs = null;
        try {
            dbcon = DbConnector.getCreatureDbCon();
            ps = dbcon.prepareStatement("UPDATE DELIVERYQUEUE SET STATE=?,TS_DELIVERY_STARTED=? WHERE DELIVERY_ID=?");
            ps.setByte(1, state);
            ps.setLong(2, tsStarted);
            ps.setLong(3, deliveryId);
            ps.executeUpdate();
        }
        catch (SQLException ex) {
            Delivery.logger.log(Level.WARNING, "Failed to update delivery " + deliveryId + " to state " + state + " in deliveryQueue table.", ex);
        }
        finally {
            DbUtilities.closeDatabaseObjects(ps, rs);
            DbConnector.returnConnection(dbcon);
        }
    }
    
    private static void dbUpdateDeliveryPickedUp(final long deliveryId, final byte state, final long tsPickedUp) {
        Connection dbcon = null;
        PreparedStatement ps = null;
        final ResultSet rs = null;
        try {
            dbcon = DbConnector.getCreatureDbCon();
            ps = dbcon.prepareStatement("UPDATE DELIVERYQUEUE SET STATE=?,TS_PICKED_UP=? WHERE DELIVERY_ID=?");
            ps.setByte(1, state);
            ps.setLong(2, tsPickedUp);
            ps.setLong(3, deliveryId);
            ps.executeUpdate();
        }
        catch (SQLException ex) {
            Delivery.logger.log(Level.WARNING, "Failed to update delivery " + deliveryId + " to state " + state + " in deliveryQueue table.", ex);
        }
        finally {
            DbUtilities.closeDatabaseObjects(ps, rs);
            DbConnector.returnConnection(dbcon);
        }
    }
    
    private static void dbUpdateDeliveryDelivered(final long deliveryId, final byte state, final long tsDelivered) {
        Connection dbcon = null;
        PreparedStatement ps = null;
        final ResultSet rs = null;
        try {
            dbcon = DbConnector.getCreatureDbCon();
            ps = dbcon.prepareStatement("UPDATE DELIVERYQUEUE SET STATE=?,TS_DELIVERED=? WHERE DELIVERY_ID=?");
            ps.setByte(1, state);
            ps.setLong(2, tsDelivered);
            ps.setLong(3, deliveryId);
            ps.executeUpdate();
        }
        catch (SQLException ex) {
            Delivery.logger.log(Level.WARNING, "Failed to update delivery " + deliveryId + " to state " + state + " in deliveryQueue table.", ex);
        }
        finally {
            DbUtilities.closeDatabaseObjects(ps, rs);
            DbConnector.returnConnection(dbcon);
        }
    }
    
    private static void dbUpdateState(final long deliveryId, final byte state) {
        Connection dbcon = null;
        PreparedStatement ps = null;
        final ResultSet rs = null;
        try {
            dbcon = DbConnector.getCreatureDbCon();
            ps = dbcon.prepareStatement("UPDATE DELIVERYQUEUE SET STATE=? WHERE DELIVERY_ID=?");
            ps.setByte(1, state);
            ps.setLong(2, deliveryId);
            ps.executeUpdate();
        }
        catch (SQLException ex) {
            Delivery.logger.log(Level.WARNING, "Failed to update delivery " + deliveryId + " to state " + state + " in deliveryQueue table.", ex);
        }
        finally {
            DbUtilities.closeDatabaseObjects(ps, rs);
            DbConnector.returnConnection(dbcon);
        }
    }
    
    static {
        logger = Logger.getLogger(Wagoner.class.getName());
        deliveryQueue = new ConcurrentHashMap<Long, Delivery>();
        containerDelivery = new ConcurrentHashMap<Long, Delivery>();
    }
}
