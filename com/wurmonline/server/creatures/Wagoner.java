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
import java.util.Iterator;
import com.wurmonline.server.Servers;
import com.wurmonline.server.WurmCalendar;
import javax.annotation.Nullable;
import java.util.Collection;
import com.wurmonline.server.highways.PathToCalculate;
import com.wurmonline.server.NoSuchItemException;
import java.util.logging.Level;
import com.wurmonline.server.Items;
import java.util.Random;
import com.wurmonline.server.items.Item;
import java.util.LinkedList;
import com.wurmonline.server.highways.Route;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import com.wurmonline.server.TimeConstants;
import com.wurmonline.server.MiscConstants;

public class Wagoner implements MiscConstants, TimeConstants
{
    private static final Logger logger;
    public static final byte STATE_IDLE = 0;
    public static final byte STATE_GOING_TO_SLEEP = 1;
    public static final byte STATE_SLEEPING = 2;
    public static final byte STATE_WAKING_UP = 3;
    public static final byte STATE_GETTING_READY = 4;
    public static final byte STATE_DRIVING_TO_COLLECTION_POINT = 5;
    public static final byte STATE_LOADING = 6;
    public static final byte STATE_DELIVERING = 7;
    public static final byte STATE_UNLOADING = 8;
    public static final byte STATE_GOING_HOME = 9;
    public static final byte STATE_PARKING = 10;
    public static final byte STUCK_COLLECTING = 11;
    public static final byte STUCK_DELIVERING = 12;
    public static final byte STUCK_GOING_HOME = 13;
    public static final byte TEST_WAITING = 14;
    public static final byte TEST_DRIVING = 15;
    public static final int MAX_NOT_MOVING = 30;
    public static final byte SPEECH_CHATTYNESS_SELDOM = 0;
    public static final byte SPEECH_CHATTYNESS_SILENT = 1;
    public static final byte SPEECH_CHATTYNESS_NOISY = 2;
    public static final byte SPEECH_CHATTYNESS_RANDOM = 3;
    public static final byte SPEECH_CONTEXT_TEST = 1;
    public static final byte SPEECH_CONTEXT_ERROR = 2;
    public static final byte SPEECH_CONTEXT_FOOD = 4;
    public static final byte SPEECH_CONTEXT_WORK = 8;
    public static final byte SPEECH_CONTEXT_SLEEP = 16;
    public static final byte SPEECH_CONTEXT_RANDOM = 32;
    public static final byte SPEECH_STYLE_NORMAL = 0;
    public static final byte SPEECH_STYLE_WHITTY = 1;
    public static final byte SPEECH_STYLE_GRUMPY = 2;
    public static final byte SPEECH_STYLE_CHEERY = 3;
    private static final String CREATE_WAGONER = "INSERT INTO WAGONER (WURMID,STATE,OWNERID,CONTRACT_ID,HOME_WAYSTONE_ID,HOME_VILLAGE_ID,WAGON_ID,RESTING_PLACE_ID,CHAIR_ID,TENT_ID,BED_ID,DELIVERY_ID,WAGON_POSX,WAGON_POSY,WAGON_ON_SURFACE,CAMP_ROT,LAST_WAYSTONE_ID,GOAL_WAYSTONE_ID) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
    private static final String UPDATE_WAGONER_STATE = "UPDATE WAGONER SET STATE=? WHERE WURMID=?";
    private static final String UPDATE_WAGONER_DELIVERY = "UPDATE WAGONER SET DELIVERY_ID=?, STATE=? WHERE WURMID=?";
    private static final String UPDATE_WAGONER_LAST_ID = "UPDATE WAGONER SET LAST_WAYSTONE_ID=? WHERE WURMID=?";
    private static final String UPDATE_WAGONER_GOAL_ID = "UPDATE WAGONER SET GOAL_WAYSTONE_ID=? WHERE WURMID=?";
    private static final String UPDATE_WAGONER_CHAT_OPTIONS = "UPDATE WAGONER SET CHAT=? WHERE WURMID=?";
    private static final String DELETE_WAGONER = "DELETE FROM WAGONER WHERE WURMID=?";
    private static final String GET_ALL_WAGONERS = "SELECT * FROM WAGONER";
    private static final Map<Long, Wagoner> wagoners;
    private long wagonerId;
    private byte state;
    private long ownerId;
    private long contractId;
    private long homeWaystoneId;
    private int homeVillageId;
    private long wagonId;
    private long restingPlaceId;
    private long chairId;
    private long tentId;
    private long bedId;
    private long deliveryId;
    private float wagonPosX;
    private float wagonPosY;
    private boolean wagonOnSurface;
    private float campRot;
    private long lastWaystoneId;
    private long goalWaystoneId;
    private List<Route> path;
    private LinkedList<Item> currentCatseyes;
    private boolean updateCatseyes;
    private LinkedList<Item> catseyesCollecting;
    private LinkedList<Item> catseyesDelivering;
    private LinkedList<Item> catseyesReturning;
    private Item wagon;
    private Creature creature;
    private boolean forceStateChange;
    private byte forcedNewState;
    private final Random rnd;
    private long lazy;
    private long lastCheck;
    private byte subState;
    private int notMoving;
    private int lastDir;
    private int tilex;
    private int tiley;
    private int tileCount;
    private byte speechChattyness;
    private boolean speechContextFood;
    private boolean speechContextWork;
    private boolean speechContextSleep;
    private boolean speechContextRandom;
    private byte speechStyle;
    private int randomness;
    private long chatDelay;
    private long lastChat;
    
    public Wagoner(final long wagonerId, final byte state, final long ownerId, final long contractId, final long homeWaystoneId, final int homeVillageId, final long wagonId, final long restingPlaceId, final long chairId, final long tentId, final long bedId, final long deliveryId, final float wagonPosX, final float wagonPosY, final boolean wagonOnSurface, final float campRot, final long lastWaystoneId, final long goalWaystoneId, final byte speachChatty, final boolean speachFood, final boolean speachWork, final boolean speachSleep, final boolean speachRandom, final byte speachType) {
        this.lastWaystoneId = -10L;
        this.goalWaystoneId = -10L;
        this.path = null;
        this.currentCatseyes = null;
        this.updateCatseyes = false;
        this.catseyesCollecting = null;
        this.catseyesDelivering = null;
        this.catseyesReturning = null;
        this.wagon = null;
        this.creature = null;
        this.forceStateChange = false;
        this.forcedNewState = 0;
        this.lazy = 0L;
        this.lastCheck = 0L;
        this.subState = 0;
        this.notMoving = 0;
        this.lastDir = -1;
        this.tilex = 0;
        this.tiley = 0;
        this.tileCount = 0;
        this.speechChattyness = 0;
        this.speechContextFood = false;
        this.speechContextWork = false;
        this.speechContextSleep = false;
        this.speechContextRandom = false;
        this.speechStyle = 0;
        this.randomness = 1;
        this.chatDelay = 0L;
        this.lastChat = 0L;
        this.wagonerId = wagonerId;
        this.state = state;
        this.ownerId = ownerId;
        this.contractId = contractId;
        this.homeWaystoneId = homeWaystoneId;
        this.homeVillageId = homeVillageId;
        this.wagonId = wagonId;
        this.restingPlaceId = restingPlaceId;
        this.chairId = chairId;
        this.tentId = tentId;
        this.bedId = bedId;
        this.deliveryId = deliveryId;
        this.wagonPosX = wagonPosX;
        this.wagonPosY = wagonPosY;
        this.wagonOnSurface = wagonOnSurface;
        this.campRot = campRot;
        this.lastWaystoneId = lastWaystoneId;
        this.goalWaystoneId = goalWaystoneId;
        this.rnd = new Random(wagonerId);
        this.speechChattyness = speachChatty;
        this.speechContextFood = speachFood;
        this.speechContextWork = speachWork;
        this.speechContextSleep = speachSleep;
        this.speechContextRandom = speachRandom;
        this.speechStyle = speachType;
        this.randomness = this.rnd.nextInt(5) + 5;
        try {
            (this.wagon = Items.getItem(this.wagonId)).setWagonerWagon(true);
            this.wagon.setDamage(0.0f);
            addWagoner(this);
        }
        catch (NoSuchItemException e) {
            Wagoner.logger.log(Level.WARNING, "Wagoner wagon (" + this.wagonId + ") missing! " + e.getMessage(), e);
        }
        if (this.deliveryId != -10L) {
            this.grabDeliveryCatseyes(this.deliveryId);
        }
        this.lazy = 60000L + 3000L * this.rnd.nextInt(60);
        this.setChatGap();
    }
    
    public long getWurmId() {
        return this.wagonerId;
    }
    
    public byte getState() {
        return this.state;
    }
    
    public String getStateName() {
        return getStateName(this.state);
    }
    
    public boolean isIdle() {
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
    
    public long getOwnerId() {
        return this.ownerId;
    }
    
    public void setOwnerId(final long newOwnerId) {
        this.ownerId = newOwnerId;
    }
    
    public long getContractId() {
        return this.contractId;
    }
    
    public long getHomeWaystoneId() {
        return this.homeWaystoneId;
    }
    
    public int getVillageId() {
        return this.homeVillageId;
    }
    
    public long getWagonId() {
        return this.wagonId;
    }
    
    public Item getWagon() {
        return this.wagon;
    }
    
    public long getRestingPlaceId() {
        return this.restingPlaceId;
    }
    
    public long getChairId() {
        return this.chairId;
    }
    
    public long getTentId() {
        return this.tentId;
    }
    
    public long getBedId() {
        return this.bedId;
    }
    
    public long getDeliveryId() {
        return this.deliveryId;
    }
    
    public float getWagonPosX() {
        return this.wagonPosX;
    }
    
    public float getWagonPosY() {
        return this.wagonPosY;
    }
    
    public boolean getWagonOnSurface() {
        return this.wagonOnSurface;
    }
    
    public float getCampRot() {
        return this.campRot;
    }
    
    public long getLastWaystoneId() {
        return this.lastWaystoneId;
    }
    
    public void setLastWaystoneId(final long newLastWaystoneId) {
        this.lastWaystoneId = newLastWaystoneId;
        dbUpdateLastWaystoneId(this.wagonerId, this.lastWaystoneId);
    }
    
    public long getGoalWaystoneId() {
        return this.goalWaystoneId;
    }
    
    public void setGoalWaystoneId(final long newGoalWaystoneId) {
        this.goalWaystoneId = newGoalWaystoneId;
        dbUpdateGoalWaystoneId(this.wagonerId, this.goalWaystoneId);
    }
    
    public void calculateRoute(final long newLastWaystoneId, final long newGoalWaystoneId) {
        this.setGoalWaystoneId(newGoalWaystoneId);
        this.calculateRoute(newLastWaystoneId);
    }
    
    public void calculateRoute(final long newLastWaystoneId) {
        this.setLastWaystoneId(newLastWaystoneId);
        this.calculateRoute();
    }
    
    public void calculateRoute() {
        if (this.lastWaystoneId == this.goalWaystoneId) {
            this.path = null;
        }
        else {
            this.path = PathToCalculate.getRoute(this.lastWaystoneId, this.goalWaystoneId);
            this.updateCatseyes = this.hasPath();
        }
    }
    
    public LinkedList<Item> getCurrentCatseyes() {
        return this.currentCatseyes;
    }
    
    public boolean maybeUpdateCatseyes() {
        if (this.path == null) {
            return this.updateCatseyes = false;
        }
        if (this.updateCatseyes) {
            final LinkedList<Item> catseyes = new LinkedList<Item>();
            while (!this.path.isEmpty()) {
                final Route actualRoute = this.path.remove(0);
                catseyes.addAll(actualRoute.getCatseyesListCopy());
                catseyes.add(actualRoute.getEndNode().getWaystone());
            }
            this.currentCatseyes = catseyes;
            this.updateCatseyes = false;
            return true;
        }
        return false;
    }
    
    public boolean updateCatseyes(final Item marker) {
        this.path = PathToCalculate.getRoute(this.lastWaystoneId, this.goalWaystoneId);
        this.currentCatseyes = new LinkedList<Item>();
        while (!this.path.isEmpty()) {
            final Route actualRoute = this.path.remove(0);
            this.currentCatseyes.addAll(actualRoute.getCatseyesListCopy());
            this.currentCatseyes.add(actualRoute.getEndNode().getWaystone());
        }
        if (this.currentCatseyes.contains(marker)) {
            while (this.currentCatseyes.getFirst() != marker) {
                this.currentCatseyes.removeFirst();
            }
            this.updateCatseyes = false;
            return true;
        }
        return false;
    }
    
    @Nullable
    public List<Route> getPath() {
        return this.path;
    }
    
    public boolean hasPath() {
        return this.path != null && !this.path.isEmpty();
    }
    
    public void remove() {
        removeWagoner(this);
    }
    
    public Creature getCreature() {
        return this.creature;
    }
    
    public void setCreature(final Creature creature) {
        this.creature = creature;
    }
    
    public String getName() {
        if (this.creature == null) {
            return "Unknown";
        }
        return this.creature.getName();
    }
    
    public void updateState(final byte newState) {
        if (this.state != newState) {
            this.state = newState;
            dbUpdateWagonerState(this.wagonerId, this.state);
            this.forceStateChange = false;
        }
        if (newState == 6) {
            this.catseyesCollecting = new LinkedList<Item>();
        }
        if (newState == 8) {
            this.catseyesDelivering = new LinkedList<Item>();
        }
        if (this.isIdle() && this.homeVillageId == -1) {
            this.removeWagonerCamp();
            Delivery.rejectWaitingForAccept(this.wagonerId);
            Delivery.clrWagonerQueue(this.wagonerId);
            this.remove();
        }
    }
    
    public boolean updateDeliveryId(final long newDeliveryId) {
        if (newDeliveryId != -10L && !this.grabDeliveryCatseyes(newDeliveryId)) {
            this.catseyesCollecting = null;
            this.catseyesDelivering = null;
            this.catseyesReturning = null;
            return false;
        }
        this.deliveryId = newDeliveryId;
        if (newDeliveryId == -10L) {
            this.state = 0;
            this.catseyesCollecting = null;
            this.catseyesDelivering = null;
            this.catseyesReturning = null;
        }
        else {
            this.state = 4;
        }
        dbUpdateWagonerDelivery(this.wagonerId, this.deliveryId, this.state);
        this.forceStateChange = false;
        return true;
    }
    
    public boolean grabDeliveryCatseyes(final long deliveryId) {
        final Delivery delivery = Delivery.getDelivery(deliveryId);
        if (delivery == null) {
            return false;
        }
        this.catseyesCollecting = new LinkedList<Item>();
        this.catseyesDelivering = new LinkedList<Item>();
        this.catseyesReturning = new LinkedList<Item>();
        switch (this.state) {
            case 0:
            case 4:
            case 5:
            case 11: {
                final List<Route> pathCollecting = PathToCalculate.getRoute(this.homeWaystoneId, delivery.getCollectionWaystoneId());
                if (pathCollecting == null || pathCollecting.isEmpty()) {
                    return false;
                }
                while (!pathCollecting.isEmpty()) {
                    final Route actualRoute = pathCollecting.remove(0);
                    this.catseyesCollecting.addAll(actualRoute.getCatseyesListCopy());
                    this.catseyesCollecting.add(actualRoute.getEndNode().getWaystone());
                }
            }
            case 6:
            case 7:
            case 12: {
                final List<Route> pathDelivering = PathToCalculate.getRoute(delivery.getCollectionWaystoneId(), delivery.getDeliveryWaystoneId());
                if (pathDelivering == null || pathDelivering.isEmpty()) {
                    return false;
                }
                while (!pathDelivering.isEmpty()) {
                    final Route actualRoute = pathDelivering.remove(0);
                    this.catseyesDelivering.addAll(actualRoute.getCatseyesListCopy());
                    this.catseyesDelivering.add(actualRoute.getEndNode().getWaystone());
                }
            }
            case 8:
            case 9:
            case 13: {
                final List<Route> pathReturning = PathToCalculate.getRoute(delivery.getDeliveryWaystoneId(), this.homeWaystoneId);
                if (pathReturning == null || pathReturning.isEmpty()) {
                    return false;
                }
                while (!pathReturning.isEmpty()) {
                    final Route actualRoute = pathReturning.remove(0);
                    this.catseyesReturning.addAll(actualRoute.getCatseyesListCopy());
                    this.catseyesReturning.add(actualRoute.getEndNode().getWaystone());
                }
                return true;
            }
            default: {
                return false;
            }
        }
    }
    
    public boolean markerOnDeliveryRoute(final Item marker) {
        return this.deliveryId != -10L && ((this.catseyesReturning != null && !this.catseyesReturning.isEmpty()) || this.grabDeliveryCatseyes(this.deliveryId)) && (this.catseyesCollecting.contains(marker) || this.catseyesDelivering.contains(marker) || this.catseyesReturning.contains(marker));
    }
    
    public void forceStateChange(final byte newState) {
        this.forcedNewState = newState;
        this.forceStateChange = true;
    }
    
    public boolean isForcedState() {
        return this.forceStateChange;
    }
    
    public byte getForcedState() {
        return this.forcedNewState;
    }
    
    public int getQueueLength() {
        return Delivery.getQueueLength(this.wagonerId);
    }
    
    public void clrTileCount() {
        this.tileCount = 0;
    }
    
    public void setTile(final int tilex, final int tiley) {
        this.tilex = tilex;
        this.tiley = tiley;
    }
    
    public boolean moved(final int tilex, final int tiley) {
        if (tilex != this.tilex || tiley != this.tiley) {
            this.setTile(tilex, tiley);
            ++this.tileCount;
            return true;
        }
        return false;
    }
    
    public void clrVillage() {
        Wagoner.logger.log(Level.WARNING, this.getName() + " (" + this.wagonerId + ") removed from village id: " + this.homeVillageId + ".", new Exception());
        if (this.homeVillageId != -1) {
            this.homeVillageId = -1;
            Delivery.rejectWaitingForAccept(this.wagonerId);
            if (this.isIdle()) {
                this.removeWagonerCamp();
                Delivery.clrWagonerQueue(this.wagonerId);
                this.remove();
            }
        }
    }
    
    private void removeWagonerCamp() {
        try {
            Creatures.getInstance().getCreature(this.wagonerId).destroy();
            this.creature = null;
        }
        catch (NoSuchCreatureException ex) {}
        Items.destroyItem(this.wagonId, true);
        Items.destroyItem(this.chairId, true);
        Items.destroyItem(this.bedId, true);
        Items.destroyItem(this.tentId, true);
        Items.destroyItem(this.restingPlaceId, true);
        try {
            final Item homeWaystone = Items.getItem(this.homeWaystoneId);
            homeWaystone.setData(-1L);
        }
        catch (NoSuchItemException e) {
            Wagoner.logger.log(Level.WARNING, "Home Waystone is missing " + e.getMessage(), e);
        }
        try {
            final Item contract = Items.getItem(this.contractId);
            contract.setData(-1L);
            contract.setDescription("");
        }
        catch (NoSuchItemException e) {
            Wagoner.logger.log(Level.WARNING, "Wagoner Contract is missing " + e.getMessage(), e);
        }
    }
    
    public void say(final Speech speech) {
        this.say(speech, null);
    }
    
    public void say(final Speech speech, @Nullable final Delivery delivery) {
        this.lastChat = WurmCalendar.getCurrentTime();
        boolean inContext = true;
        switch (speech.getContext()) {
            case 4: {
                inContext = this.speechContextFood;
                break;
            }
            case 8: {
                inContext = this.speechContextWork;
                break;
            }
            case 16: {
                inContext = this.speechContextSleep;
                break;
            }
            case 1: {
                inContext = Servers.isThisATestServer();
                break;
            }
            case 2: {
                inContext = true;
                break;
            }
            default: {
                inContext = false;
                break;
            }
        }
        if (inContext && this.sayIt()) {
            switch (speech.getParams()) {
                case 0: {
                    this.creature.say(speech.getMsg(this.speechStyle), speech.isEmote(this.speechStyle));
                    break;
                }
                case 1: {
                    final String sub1 = (delivery.getCrates() == 1) ? "1 crate" : (delivery.getCrates() + " crates");
                    final String msg = String.format(speech.getMsg(this.speechStyle), sub1);
                    this.creature.say(msg, speech.isEmote(this.speechStyle));
                    break;
                }
                case 2: {
                    final String sub1 = (delivery.getCrates() == 1) ? "1 crate" : (delivery.getCrates() + " crates");
                    final String sub2 = delivery.getReceiverName();
                    final String msg2 = String.format(speech.getMsg(this.speechStyle), sub1, sub2);
                    this.creature.say(msg2, speech.isEmote(this.speechStyle));
                    break;
                }
            }
        }
    }
    
    public void sayRandom() {
        final long now = WurmCalendar.getCurrentTime();
        if (this.lastChat + this.chatDelay > now) {
            return;
        }
        if (this.speechContextRandom && this.tileCount % 10 == 5 && this.sayIt() && this.rnd.nextInt(4) == 0) {
            this.lastChat = now;
            final long time = now + 300L;
            final int mins = (int)(time % 3600L / 60L);
            if (mins <= 10) {
                final int hour = (int)(time % 86400L / 3600L);
                for (final TimedSpeech ts : TimedSpeech.values()) {
                    if (ts.getHour() == hour) {
                        this.creature.say(ts.getMsg(this.speechStyle, this.creature.isOnSurface()), ts.isEmote(this.speechStyle, this.creature.isOnSurface()));
                        return;
                    }
                }
            }
            final RandomSpeech rs = RandomSpeech.values()[this.rnd.nextInt(RandomSpeech.values().length)];
            this.creature.say(rs.getMsg(this.speechStyle, this.creature.isOnSurface()), rs.isEmote(this.speechStyle, this.creature.isOnSurface()));
        }
    }
    
    public void sayStuck() {
        final long now = WurmCalendar.getCurrentTime();
        if (this.lastChat + this.chatDelay > now) {
            return;
        }
        this.lastChat = now;
        final RandomSpeech rs = RandomSpeech.values()[this.rnd.nextInt(RandomSpeech.values().length)];
        this.creature.say(rs.getMsg(this.speechStyle, this.creature.isOnSurface()), rs.isEmote(this.speechStyle, this.creature.isOnSurface()));
    }
    
    private boolean sayIt() {
        switch (this.speechChattyness) {
            case 0: {
                return this.rnd.nextInt(3) == 0;
            }
            case 1: {
                return false;
            }
            case 2: {
                return true;
            }
            case 3: {
                return this.rnd.nextInt(this.randomness) < 4;
            }
            default: {
                return true;
            }
        }
    }
    
    public long getLazy() {
        return this.lazy;
    }
    
    public long getLastCheck() {
        return this.lastCheck;
    }
    
    public void setLastCheck(final long now) {
        this.lastCheck = now;
    }
    
    public byte getSubState() {
        return this.subState;
    }
    
    public void incSubState() {
        ++this.subState;
    }
    
    public void clrSubState() {
        this.subState = 0;
    }
    
    public void setSubState(final byte newSubState) {
        this.subState = newSubState;
    }
    
    public int getNotMoving() {
        return this.notMoving;
    }
    
    public void incNotMoving() {
        ++this.notMoving;
    }
    
    public void clrNotMoving() {
        this.notMoving = 0;
    }
    
    public int getLastDir() {
        return this.lastDir;
    }
    
    public void setLastDir(final int newDir) {
        this.lastDir = newDir;
    }
    
    public byte getSpeechChattyness() {
        return this.speechChattyness;
    }
    
    public boolean isSpeachContextFood() {
        return this.speechContextFood;
    }
    
    public boolean isSpeachContextWork() {
        return this.speechContextWork;
    }
    
    public boolean isSpeachContextSleep() {
        return this.speechContextSleep;
    }
    
    public boolean isSpeachContextRandom() {
        return this.speechContextRandom;
    }
    
    public byte getSpeechStyle() {
        return this.speechStyle;
    }
    
    public void setSpeechOptions(final byte newChattyness, final boolean newContextFood, final boolean newContextWork, final boolean newContextSleep, final boolean newContextRandom, final byte newChatType) {
        this.speechChattyness = newChattyness;
        this.speechContextFood = newContextFood;
        this.speechContextWork = newContextWork;
        this.speechContextSleep = newContextSleep;
        this.speechContextRandom = newContextRandom;
        this.speechStyle = newChatType;
        this.setChatGap();
        final byte newSpeachOptions = (byte)(newChattyness + (newContextFood ? 0 : 4) + (newContextWork ? 0 : 8) + (newContextSleep ? 0 : 16) + (newContextRandom ? 32 : 0) + (newChatType << 6));
        dbUpdateWagonerChatOptions(this.wagonerId, newSpeachOptions);
    }
    
    private void setChatGap() {
        switch (this.speechChattyness) {
            case 0: {
                this.chatDelay = 300L;
                break;
            }
            case 1: {
                this.chatDelay = 3600L;
                break;
            }
            case 2: {
                this.chatDelay = 60L;
                break;
            }
            case 3: {
                this.chatDelay = 60L + this.rnd.nextInt(300);
                break;
            }
        }
    }
    
    public static String getStateName(final byte state) {
        switch (state) {
            case 0: {
                return "Idle";
            }
            case 1:
            case 2:
            case 3: {
                return "Sleeping";
            }
            case 4: {
                return "Getting ready";
            }
            case 5:
            case 11: {
                return "Driving to collection point";
            }
            case 6: {
                return "Loading";
            }
            case 7:
            case 12: {
                return "Delivering";
            }
            case 8: {
                return "Unloading";
            }
            case 9:
            case 13: {
                return "Going home";
            }
            case 10: {
                return "Parking";
            }
            case 14: {
                return "waiting";
            }
            case 15: {
                return "driving";
            }
            default: {
                return "";
            }
        }
    }
    
    public static final Wagoner getWagoner(final long wurmId) {
        return Wagoner.wagoners.get(wurmId);
    }
    
    public static final Map<Long, Wagoner> getWagoners() {
        return Wagoner.wagoners;
    }
    
    public static final Wagoner[] getAllWagoners() {
        return Wagoner.wagoners.values().toArray(new Wagoner[Wagoner.wagoners.size()]);
    }
    
    private static final void removeWagoner(final Wagoner wagoner) {
        Wagoner.wagoners.remove(wagoner.getWurmId());
        dbRemoveWagoner(wagoner.getWurmId());
    }
    
    private static final void addWagoner(final Wagoner wagoner) {
        Wagoner.wagoners.put(wagoner.getWurmId(), wagoner);
    }
    
    public static final void addWagoner(final long wurmId, final long ownerId, final long contractId, final long homeWaystoneId, final int homeVillageId, final Item wagon, final long restingPlaceId, final long chairId, final long tentId, final long bedId, final byte speachChatty, final boolean speachFood, final boolean speachWork, final boolean speachSleep, final boolean speachRandom, final byte speachType) {
        final byte state = 0;
        final long deliveryId = -10L;
        final long lastWaystoneId = -10L;
        final long goalWaystoneId = -10L;
        final Wagoner wagoner = new Wagoner(wurmId, (byte)0, ownerId, contractId, homeWaystoneId, homeVillageId, wagon.getWurmId(), restingPlaceId, chairId, tentId, bedId, -10L, wagon.getPosX(), wagon.getPosY(), wagon.isOnSurface(), wagon.getRotation(), -10L, -10L, speachChatty, speachFood, speachWork, speachSleep, speachRandom, speachType);
        dbCreateWagoner(wagoner);
    }
    
    public static final String getWagonerNameFrom(final long waystoneId) {
        for (final Wagoner wagoner : Wagoner.wagoners.values()) {
            if (wagoner.getHomeWaystoneId() == waystoneId) {
                return wagoner.getName() + " camp";
            }
        }
        return "";
    }
    
    public static boolean isOnActiveDeliveryRoute(final Item marker) {
        for (final Wagoner wagoner : Wagoner.wagoners.values()) {
            if (wagoner.markerOnDeliveryRoute(marker)) {
                return true;
            }
        }
        return false;
    }
    
    public static final void dbLoadAllWagoners() {
        Wagoner.logger.log(Level.INFO, "Loading all wagoners.");
        final long start = System.nanoTime();
        Connection dbcon = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            dbcon = DbConnector.getCreatureDbCon();
            ps = dbcon.prepareStatement("SELECT * FROM WAGONER");
            rs = ps.executeQuery();
            while (rs.next()) {
                final long wurmId = rs.getLong("WURMID");
                final byte state = rs.getByte("STATE");
                final long ownerId = rs.getLong("OWNERID");
                final long contractId = rs.getLong("CONTRACT_ID");
                final long homeWaystoneId = rs.getLong("HOME_WAYSTONE_ID");
                final int homeVillageId = rs.getInt("HOME_VILLAGE_ID");
                final long wagonId = rs.getLong("WAGON_ID");
                final long restingPlaceId = rs.getLong("RESTING_PLACE_ID");
                final long chairId = rs.getLong("CHAIR_ID");
                final long tentId = rs.getLong("TENT_ID");
                final long bedId = rs.getLong("BED_ID");
                final long deliveryId = rs.getLong("DELIVERY_ID");
                final float wagonPosX = rs.getFloat("WAGON_POSX");
                final float wagonPosY = rs.getFloat("WAGON_POSY");
                final boolean wagonOnSurface = rs.getBoolean("WAGON_ON_SURFACE");
                final float campRot = rs.getFloat("CAMP_ROT");
                final long lastWaystoneId = rs.getLong("LAST_WAYSTONE_ID");
                final long goalWaystoneId = rs.getLong("GOAL_WAYSTONE_ID");
                final byte chat = rs.getByte("CHAT");
                final byte speachChatty = (byte)(chat & 0x3);
                final boolean speachFood = (chat & 0x4) != 0x4;
                final boolean speachWork = (chat & 0x8) != 0x8;
                final boolean speachSleep = (chat & 0x10) != 0x10;
                final boolean speachRandom = (chat & 0x20) == 0x20;
                final byte speachType = (byte)(chat >> 6 & 0x3);
                new Wagoner(wurmId, state, ownerId, contractId, homeWaystoneId, homeVillageId, wagonId, restingPlaceId, chairId, tentId, bedId, deliveryId, wagonPosX, wagonPosY, wagonOnSurface, campRot, lastWaystoneId, goalWaystoneId, speachChatty, speachFood, speachWork, speachSleep, speachRandom, speachType);
            }
        }
        catch (SQLException sqex) {
            Wagoner.logger.log(Level.WARNING, "Failed to load all wagoners: " + sqex.getMessage(), sqex);
        }
        finally {
            DbUtilities.closeDatabaseObjects(ps, rs);
            DbConnector.returnConnection(dbcon);
            final long end = System.nanoTime();
            Wagoner.logger.log(Level.INFO, "Loaded " + Wagoner.wagoners.size() + " wagoners. That took " + (end - start) / 1000000.0f + " ms.");
        }
    }
    
    private static void dbCreateWagoner(final Wagoner wag) {
        Connection dbcon = null;
        PreparedStatement ps = null;
        final ResultSet rs = null;
        try {
            dbcon = DbConnector.getCreatureDbCon();
            ps = dbcon.prepareStatement("INSERT INTO WAGONER (WURMID,STATE,OWNERID,CONTRACT_ID,HOME_WAYSTONE_ID,HOME_VILLAGE_ID,WAGON_ID,RESTING_PLACE_ID,CHAIR_ID,TENT_ID,BED_ID,DELIVERY_ID,WAGON_POSX,WAGON_POSY,WAGON_ON_SURFACE,CAMP_ROT,LAST_WAYSTONE_ID,GOAL_WAYSTONE_ID) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
            ps.setLong(1, wag.getWurmId());
            ps.setByte(2, wag.getState());
            ps.setLong(3, wag.getOwnerId());
            ps.setLong(4, wag.getContractId());
            ps.setLong(5, wag.getHomeWaystoneId());
            ps.setInt(6, wag.getVillageId());
            ps.setLong(7, wag.getWagonId());
            ps.setLong(8, wag.getRestingPlaceId());
            ps.setLong(9, wag.getChairId());
            ps.setLong(10, wag.getTentId());
            ps.setLong(11, wag.getBedId());
            ps.setLong(12, wag.getDeliveryId());
            ps.setFloat(13, wag.getWagonPosX());
            ps.setFloat(14, wag.getWagonPosY());
            ps.setBoolean(15, wag.getWagonOnSurface());
            ps.setFloat(16, wag.getCampRot());
            ps.setLong(17, wag.getLastWaystoneId());
            ps.setLong(18, wag.getGoalWaystoneId());
            ps.executeUpdate();
        }
        catch (SQLException ex) {
            Wagoner.logger.log(Level.WARNING, "Failed to create wagoner " + wag.getWurmId() + " in wagoner table.", ex);
        }
        finally {
            DbUtilities.closeDatabaseObjects(ps, rs);
            DbConnector.returnConnection(dbcon);
        }
    }
    
    private static void dbRemoveWagoner(final long wagonerId) {
        Connection dbcon = null;
        PreparedStatement ps = null;
        final ResultSet rs = null;
        try {
            dbcon = DbConnector.getCreatureDbCon();
            ps = dbcon.prepareStatement("DELETE FROM WAGONER WHERE WURMID=?");
            ps.setLong(1, wagonerId);
            ps.executeUpdate();
        }
        catch (SQLException ex) {
            Wagoner.logger.log(Level.WARNING, "Failed to remove wagoner " + wagonerId + " from wagoner table.", ex);
        }
        finally {
            DbUtilities.closeDatabaseObjects(ps, rs);
            DbConnector.returnConnection(dbcon);
        }
    }
    
    private static void dbUpdateWagonerState(final long wagonerId, final byte state) {
        Connection dbcon = null;
        PreparedStatement ps = null;
        final ResultSet rs = null;
        try {
            dbcon = DbConnector.getCreatureDbCon();
            ps = dbcon.prepareStatement("UPDATE WAGONER SET STATE=? WHERE WURMID=?");
            ps.setByte(1, state);
            ps.setLong(2, wagonerId);
            ps.executeUpdate();
        }
        catch (SQLException ex) {
            Wagoner.logger.log(Level.WARNING, "Failed to update wagoner " + wagonerId + " to state " + state + " in wagoner table.", ex);
        }
        finally {
            DbUtilities.closeDatabaseObjects(ps, rs);
            DbConnector.returnConnection(dbcon);
        }
    }
    
    private static void dbUpdateWagonerDelivery(final long wagonerId, final long deliveryId, final byte state) {
        Connection dbcon = null;
        PreparedStatement ps = null;
        final ResultSet rs = null;
        try {
            dbcon = DbConnector.getCreatureDbCon();
            ps = dbcon.prepareStatement("UPDATE WAGONER SET DELIVERY_ID=?, STATE=? WHERE WURMID=?");
            ps.setLong(1, deliveryId);
            ps.setByte(2, state);
            ps.setLong(3, wagonerId);
            ps.executeUpdate();
        }
        catch (SQLException ex) {
            Wagoner.logger.log(Level.WARNING, "Failed to update wagoner " + wagonerId + " delivery " + deliveryId + " and state " + state + " in wagoner table.", ex);
        }
        finally {
            DbUtilities.closeDatabaseObjects(ps, rs);
            DbConnector.returnConnection(dbcon);
        }
    }
    
    private static void dbUpdateLastWaystoneId(final long wagonerId, final long lastWaystoneId) {
        Connection dbcon = null;
        PreparedStatement ps = null;
        final ResultSet rs = null;
        try {
            dbcon = DbConnector.getCreatureDbCon();
            ps = dbcon.prepareStatement("UPDATE WAGONER SET LAST_WAYSTONE_ID=? WHERE WURMID=?");
            ps.setLong(1, lastWaystoneId);
            ps.setLong(2, wagonerId);
            ps.executeUpdate();
        }
        catch (SQLException ex) {
            Wagoner.logger.log(Level.WARNING, "Failed to update wagoner " + wagonerId + " last waystone to " + lastWaystoneId + ".", ex);
        }
        finally {
            DbUtilities.closeDatabaseObjects(ps, rs);
            DbConnector.returnConnection(dbcon);
        }
    }
    
    private static void dbUpdateGoalWaystoneId(final long wagonerId, final long goalWaystoneId) {
        Connection dbcon = null;
        PreparedStatement ps = null;
        final ResultSet rs = null;
        try {
            dbcon = DbConnector.getCreatureDbCon();
            ps = dbcon.prepareStatement("UPDATE WAGONER SET GOAL_WAYSTONE_ID=? WHERE WURMID=?");
            ps.setLong(1, goalWaystoneId);
            ps.setLong(2, wagonerId);
            ps.executeUpdate();
        }
        catch (SQLException ex) {
            Wagoner.logger.log(Level.WARNING, "Failed to update wagoner " + wagonerId + " goal waystone to " + goalWaystoneId + ".", ex);
        }
        finally {
            DbUtilities.closeDatabaseObjects(ps, rs);
            DbConnector.returnConnection(dbcon);
        }
    }
    
    private static void dbUpdateWagonerChatOptions(final long wagonerId, final byte chatOptions) {
        Connection dbcon = null;
        PreparedStatement ps = null;
        final ResultSet rs = null;
        try {
            dbcon = DbConnector.getCreatureDbCon();
            ps = dbcon.prepareStatement("UPDATE WAGONER SET CHAT=? WHERE WURMID=?");
            ps.setByte(1, chatOptions);
            ps.setLong(2, wagonerId);
            ps.executeUpdate();
        }
        catch (SQLException ex) {
            Wagoner.logger.log(Level.WARNING, "Failed to update wagoner " + wagonerId + " chat options to " + chatOptions + ".", ex);
        }
        finally {
            DbUtilities.closeDatabaseObjects(ps, rs);
            DbConnector.returnConnection(dbcon);
        }
    }
    
    static {
        logger = Logger.getLogger(Wagoner.class.getName());
        wagoners = new ConcurrentHashMap<Long, Wagoner>();
    }
    
    public enum Speech
    {
        YAWNS((byte)16, 0, true, "Yawns!", true, "Yawns!", true, "Yawns!", true, "Yawns!"), 
        BED((byte)16, 0, false, "Going to bed before I fall asleep in my chair.", false, "Falling asleep in a chair, I'm getting old. Time for bed!", false, "Time to sleep so I can do it all over again tomorrow, lucky me.", false, "It's been a great day, but time for sleep!"), 
        ZZZZ((byte)16, 0, true, "zzzZzzzZzz.", true, "zzzZzzZzzz.", true, "zzzzzZzzZz.", true, "zzZzzzzzZz."), 
        GETUP((byte)16, 0, false, "Time to get up already?", false, "I don't wanna go to school Muuummm... Never mind, I'm up, I'm up, ignore that.", false, "It's too early, why am I up!", false, "Early bird gets the wurm!"), 
        SLEPT_IN((byte)16, 0, false, "Looks like I slept in!", false, "I swear I just closed my eyes for a few seconds.", false, "Yeesh, can't I sleep in some days without the third degree?", false, "Oops, looks like I was a bit of a sleepy head!"), 
        MORNING((byte)16, 0, false, "Morning all.", false, "Don't talk to me until I've had my... I don't know.", false, "Yeah yeah, morning, nothing good about it.", false, "Good morning everyone, I hope you have a great day - I know I will!"), 
        BREAKFAST((byte)4, 0, false, "Ah breakfast!", false, "Is that growling a bear or am I just hungry?", false, "Finally breakfast, let's hope this is better than the last meal.", false, "Breakfast time! What did I get today, I hope it's my favourite."), 
        LUNCH((byte)4, 0, false, "I hope it's chips.", false, "One can never have too many sandwiches for lunch, unless you have more than two.", false, "Lunch, the one good thing about daytime.", false, "Lunch time! The day goes by so fast when you're having fun."), 
        DINNER((byte)4, 0, false, "Hog roast, that's what to do with roadkill!", false, "Why can't I eat dinner at a table?", false, "I'm so over spider meat soup.", false, "Dinnertime! Time to use those mushrooms I foraged!"), 
        DELIVERY((byte)8, 0, false, "Woo hoo, a delivery to do!", false, "Another delivery? Why didn't I listen to my mother and become a carpenter.", false, "Another one? Really? Really?!?", false, "Woo hoo, a delivery to do!"), 
        SUMMON((byte)8, 0, true, "Cracks a whip, and four wagon creatures appear.", true, "Cracks a whip, and four wagon creatures appear.", true, "Cracks a whip, and four wagon creatures appear.", true, "Cracks a whip, and four wagon creatures appear."), 
        START_DELIVERY((byte)8, 0, false, "And off I go.", false, "And I'm off like a sausage in the sun.", false, "The sooner I do this the sooner it's over.", false, "Another adventure, I'm excited!"), 
        LOADING((byte)8, 1, false, "Loading %s.", false, "%s to go in the load, some crates to go. You take one out and load it about, one less crate to go in the load.", false, "%s? Couldn't have made it fewer?", false, "Wow, %s, that's a lot! Someone is going to be happy to receive them all."), 
        UNLOADING((byte)8, 2, false, "Delivery of %s for %s.", false, "I have %s for %s here, and I only peeked in one of them!", false, "I've got %s for %s here, and they're not going to unload themselves.", false, "HI! I have %s here for %s!"), 
        DELIVERY_DONE((byte)8, 0, false, "Delivery done, off back home now.", false, "Well, I'm not paid by the hour, time to get going... We should form a union!", false, "Right, that's all of them.  Not here to chat, I'm off.", false, "And that's all of them, I'm off back home.  Have a great day!"), 
        SEND_TO_VOID((byte)8, 0, true, "Cracks a whip and the wagoner creatures vanish.", true, "Cracks a whip and the wagoner creatures vanish.", true, "Cracks a whip and the wagoner creatures vanish.", true, "Cracks a whip and the wagoner creatures vanish."), 
        ARRIVED((byte)8, 0, false, "Yay, I've arrived!", false, "What has two thumbs and crates for you?  This wagoner!", false, "All right, all right, I'm here, quit nagging.", false, "I've arrived, and what a beautiful place you have!"), 
        REPAIR((byte)8, 0, true, "Cleaning the wagon.", false, "Someone scratched my paint job. But it looks better now!.", false, "Another scratch! More work as i'll just have to polish it out.", false, "A scratch, it looks just like a flame!"), 
        ERROR_VANISHED((byte)2, 0, false, "Delivery vanished, so parking up!", false, "Delivery vanished, so parking up!", false, "Delivery vanished, so parking up!", false, "Delivery vanished, so parking up!"), 
        ERROR_NO_ROUTE((byte)2, 0, false, "no eye dear!", false, "no eye dear!", false, "no eye dear!", false, "no eye dear!"), 
        ERROR_STUCK((byte)2, 0, false, "I seem to have stopped moving; maybe I'm stuck?", false, "Is it just me or did the scenery stop moving by?", false, "Ugh, it better not be a broken wheel!", false, "Oh no, it looks like I'm stuck!"), 
        TEST_STOP((byte)1, 0, false, "Ok, ok, stopping here!", false, "Right here, like this?", false, "Not going to go any further.", false, "Looks like it's time to stop!"), 
        TEST_GET_READY((byte)1, 0, false, "Ok, ok, i'll get ready for delivery.", false, "Let me go put on my makeup and get ready.", false, "Ugh, guess I better get ready to deliver.", false, "Yay another delivery, I'll get ready as quick as can be!"), 
        TEST_SLEEP((byte)1, 0, false, "Ok, ok, i'll go to sleep.", false, "Bedtime? Now? But it's not even dark!", false, "Right, nothing worth staying up for, bedtime.", false, "Bedtime? I agree, goodnight!"), 
        TEST_GETUP((byte)1, 0, false, "Stop poking me, I'll get up.", false, "Didn't your mother tell you not to spoke a sleeping person? You must have been an annoying child.", false, "Poke me with that again and you'll lose it!", false, "Stop poking me, it tickles! I'll get up and ready!"), 
        TEST_NOWHERE_TO_GO((byte)1, 0, false, "Nowhere to go... Must have been a test of getting ready, waiting for next command.", false, "Wait a minute, there's nowhere to go! You're pranking me arent you!", false, "Are you just pulling my leg with some sort of test?", false, "Oh no, it looks like that was just a test, I'll keep waiting for sure!"), 
        TEST_DRIVE((byte)1, 0, false, "Ok, ok, I'll drive there.", false, "Over there? You're sorely under-utilising my skills you know.", false, "Alright alright I'm going, quit nagging.", false, "You want me to drive there? Sure thing!"), 
        TEST_PARK((byte)1, 0, false, "Ok, ok, I'll park up.", false, "Careful, I never learned to parallel park.", false, "All right, all right, I'll park. Jeesh, the nerve of some people.", false, "Park here? Can do!"), 
        TEST_HOME((byte)1, 0, false, "Guess I'm not wanted anymore, so I'll drive home.", false, "I feel about as welcome as a fart in a plate armour suit, so I'll be going.", false, "Guess no one wants me here, me included. I'm going home.", false, "Don't need me here? That's okay, I'll go home, hope I helped!"), 
        STUCK_COLLECTING((byte)2, 0, true, "Wonder if I have a puncture.", false, "Beam me up scotty!", false, "Oh no, not stuck again!", false, "Oh I left the handbreak on..."), 
        STUCK_DELIVERING((byte)2, 0, false, "I have a flat!.", false, "Lucky the wheel is only flat at the bottom!", false, "Oh no, not a flat again!", false, "i get to try my powers out!"), 
        STUCK_GOING_HOME((byte)2, 0, false, "Looks like I may be home early.", true, "thinks of home!", true, "If only I still had enough karma...", false, "Yay, a use for my karma!");
        
        private final byte context;
        private final int params;
        private final boolean normalEmote;
        private final String normalMsg;
        private final boolean whittyEmote;
        private final String whittyMsg;
        private final boolean grumpyEmote;
        private final String grumpyMsg;
        private final boolean cheeryEmote;
        private final String cheeryMsg;
        
        private Speech(final byte context, final int params, final boolean normalEmote, final String normalMsg, final boolean whittyEmote, final String whittyMsg, final boolean grumpyEmote, final String grumpyMsg, final boolean cheeryEmote, final String cheeryMsg) {
            this.context = context;
            this.params = params;
            this.normalEmote = normalEmote;
            this.normalMsg = normalMsg;
            this.whittyEmote = whittyEmote;
            this.whittyMsg = whittyMsg;
            this.grumpyEmote = grumpyEmote;
            this.grumpyMsg = grumpyMsg;
            this.cheeryEmote = cheeryEmote;
            this.cheeryMsg = cheeryMsg;
        }
        
        public byte getContext() {
            return this.context;
        }
        
        public int getParams() {
            return this.params;
        }
        
        public boolean isEmote(final byte speechType) {
            switch (speechType) {
                case 1: {
                    return this.whittyEmote;
                }
                case 2: {
                    return this.grumpyEmote;
                }
                case 3: {
                    return this.cheeryEmote;
                }
                default: {
                    return this.normalEmote;
                }
            }
        }
        
        public String getMsg(final byte speechType) {
            switch (speechType) {
                case 1: {
                    return this.whittyMsg;
                }
                case 2: {
                    return this.grumpyMsg;
                }
                case 3: {
                    return this.cheeryMsg;
                }
                default: {
                    return this.normalMsg;
                }
            }
        }
    }
    
    public enum RandomSpeech
    {
        TILES(false, "What a view!", false, "Not much of a view in this tunnel.", false, "I can see for tiles and tiles!", false, "I can almost see to the next tile in the dark here.", false, "oh, not that mountain again!", false, "Those wagoner creatures better not get lose down here, I'll never get them back on the wagon.", false, "Oh look at that, a nice mountain!", false, "What a finely built tunnel, the miners guild would be proud!"), 
        TREES(false, "That reminds me, must sharpen my axe.", false, "I bet there's a petrified forest underground here.", false, "I can't see the forest for the trees.", false, "I wonder if you use an axe or a pick to harvest petrified forest.", false, "Trees, trees always trees.  I wish they'd just leaf me alone!", false, "So glad there's no trees down here, cut 'em all down I say.", false, "I like trees, the way they branch out.", false, "I'd like to come back and look for fossil trees someday near here."), 
        SAND(true, "Wonder where my shovel is.", false, "I cant see the desert for the sand.", false, "Sand everywhere!", false, "Oh can make sandcastles."), 
        BACKGROUND(false, "oh a butterfly!", false, "duck!", false, "Always Ducks!", false, "Oh, a hen, anyone for an omlette."), 
        VILLAGE(false, "I'm sure there is a village near here.", false, "I'm sure there's buried treasure near by.", false, "Some days you're the wagon, some days you're the squished wildlife.", false, "Some days you're the pick, some days you're the rockshard.", false, "I'm always on the road, some day soon I'm going to quit!", false, "Tunnels tunnels tunnels always going on, when do they ever end.", false, "Wonder if anyone will hear me!", false, "The echos in this tunnel are amazing!"), 
        TROLLS(false, "Oh, a troll!", false, "Who's your mama, troll!", false, "Not another troll!", false, "Oh a chance to get a recipe."), 
        DAY(false, "I hope I remembered a lantern this trip.", false, "Hope the tar for the lantern holds out while I'm down in this tunnel.", false, "This scenery doesn't look right, where's my map?", false, "Is it left or right at the next turning...", false, "I miss my campfire, this delivery better not take too long.", false, "What a damp and miserable tunnel, nothing like my cozy campfire.", false, "It's a fine day/night for a delivery.", false, "What fun to see a tunnel like this on the way to my delivery.");
        
        private final boolean surfaceNormalEmote;
        private final String surfaceNormalMsg;
        private final boolean caveNormalEmote;
        private final String caveNormalMsg;
        private final boolean surfaceWhittyEmote;
        private final String surfaceWhittyMsg;
        private final boolean caveWhittyEmote;
        private final String caveWhittyMsg;
        private final boolean surfaceGrumpyEmote;
        private final String surfaceGrumpyMsg;
        private final boolean caveGrumpyEmote;
        private final String caveGrumpyMsg;
        private final boolean surfaceCheeryEmote;
        private final String surfaceCheeryMsg;
        private final boolean caveCheeryEmote;
        private final String caveCheeryMsg;
        
        private RandomSpeech(final boolean surfaceNormalEmote, final String surfaceNormalMsg, final boolean caveNormalEmote, final String caveNormalMsg, final boolean surfaceWhittyEmote, final String surfaceWhittyMsg, final boolean caveWhittyEmote, final String caveWhittyMsg, final boolean surfaceGrumpyEmote, final String surfaceGrumpyMsg, final boolean caveGrumpyEmote, final String caveGrumpyMsg, final boolean surfaceCheeryEmote, final String surfaceCheeryMsg, final boolean caveCheeryEmote, final String caveCheeryMsg) {
            this.surfaceNormalEmote = surfaceNormalEmote;
            this.surfaceNormalMsg = surfaceNormalMsg;
            this.caveNormalEmote = caveNormalEmote;
            this.caveNormalMsg = caveNormalMsg;
            this.surfaceWhittyEmote = surfaceWhittyEmote;
            this.surfaceWhittyMsg = surfaceWhittyMsg;
            this.caveWhittyEmote = caveWhittyEmote;
            this.caveWhittyMsg = caveWhittyMsg;
            this.surfaceGrumpyEmote = surfaceGrumpyEmote;
            this.surfaceGrumpyMsg = surfaceGrumpyMsg;
            this.caveGrumpyEmote = caveGrumpyEmote;
            this.caveGrumpyMsg = caveGrumpyMsg;
            this.surfaceCheeryEmote = surfaceCheeryEmote;
            this.surfaceCheeryMsg = surfaceCheeryMsg;
            this.caveCheeryEmote = caveCheeryEmote;
            this.caveCheeryMsg = caveCheeryMsg;
        }
        
        private RandomSpeech(final boolean surfaceNormalEmote, final String surfaceNormalMsg, final boolean surfaceWhittyEmote, final String surfaceWhittyMsg, final boolean surfaceGrumpyEmote, final String surfaceGrumpyMsg, final boolean surfaceCheeryEmote, final String surfaceCheeryMsg) {
            this(surfaceNormalEmote, surfaceNormalMsg, surfaceNormalEmote, surfaceNormalMsg, surfaceWhittyEmote, surfaceWhittyMsg, surfaceWhittyEmote, surfaceWhittyMsg, surfaceGrumpyEmote, surfaceGrumpyMsg, surfaceGrumpyEmote, surfaceGrumpyMsg, surfaceCheeryEmote, surfaceCheeryMsg, surfaceCheeryEmote, surfaceCheeryMsg);
        }
        
        public boolean isEmote(final byte speechType, final boolean onSurface) {
            switch (speechType) {
                case 1: {
                    return onSurface ? this.surfaceWhittyEmote : this.caveWhittyEmote;
                }
                case 2: {
                    return onSurface ? this.surfaceGrumpyEmote : this.caveGrumpyEmote;
                }
                case 3: {
                    return onSurface ? this.surfaceCheeryEmote : this.caveCheeryEmote;
                }
                default: {
                    return onSurface ? this.surfaceNormalEmote : this.caveNormalEmote;
                }
            }
        }
        
        public String getMsg(final byte speechType, final boolean onSurface) {
            switch (speechType) {
                case 1: {
                    return onSurface ? this.surfaceWhittyMsg : this.caveWhittyMsg;
                }
                case 2: {
                    return onSurface ? this.surfaceGrumpyMsg : this.caveGrumpyMsg;
                }
                case 3: {
                    return onSurface ? this.surfaceCheeryMsg : this.caveCheeryMsg;
                }
                default: {
                    return onSurface ? this.surfaceNormalMsg : this.caveNormalMsg;
                }
            }
        }
    }
    
    public enum TimedSpeech
    {
        EARLY(4, false, "Getting nice and light.", false, "Was that thunder or my stomach?", false, "I'm so hungry, I could eat a horse!  Even a hell horse!", false, "Breakfast soon!"), 
        MORNING(5, false, "Morning!", false, "Morning!", false, "Its 5am and all's well!", false, "Its 5am and all's well!", false, "I should be back at camp having breakfast.", false, "I should be back at camp having breakfast.", false, "What a picture when dawn strikes!", false, "If only I was outside, I could see the dawn!"), 
        LUNCH(11, false, "Almost time for lunch!", false, "Was that thunder or my stomach?", false, "So hungry, I could eat a troll.  Well, most of one, anyway.", false, "Lunch soon!"), 
        NOON(12, false, "Sun in the sky says it's high noon.", false, "Hard to tell time in the dark, but I think it's noon.", false, "Guess I'll have to eat this packaged spider meal, I wonder what's actually in it?", false, "It's too dark to see what's in this packaged meal, and I think that's for the best.", false, "I hate having to work through lunch.  And when is tea time?", false, "The way this tunnel winds on and on, I'll never get back in time for lunch.", false, "Looks like I'm on the road for lunch today.", false, "Its a good day to have lunch in a tunnel."), 
        DINNER(17, false, "Almost time for Dinner!", false, "Was that thunder or my stomach?", false, "So hungry, I could eat a bison, horns, tail and all!", false, "Dinner soon!"), 
        EVENING(20, false, "Getting dark, hope someone has added lamps to the highway?", false, "Getting dark, hope someone has added lamps to the highway!", false, "If it gets much darker I'll be doing a slalom on the highway.", false, "If it gets much darker I'll be doing a slalom on the highway.", false, "Oh, getting dark again, I hate the dark.", false, "Oh, getting dark again, I hate the dark.", false, "I do like seeing the stars!", false, "At least being underground, I dont get distracted by the stars!"), 
        MIDNIGHT(0, false, "Sure is dark at midnight.", false, "Dark at midnight isn't dark until you're driving in a tunnel.", false, "I'm glad I'm not scared of traveling at night...much...", false, "This tunnel seems a bit spooky at midnight.", false, "Why don't we have bedrolls for those of us who have to travel at night?", false, "I'd like to stretch out in the wagon for a rest along with those crates!", false, "Racing through the night on a delivery run, nothing stops Wurm wagoners!", false, "I don't even notice the late hour when I'm driving through a tunnel like this.");
        
        private final int hour;
        private final boolean surfaceNormalEmote;
        private final String surfaceNormalMsg;
        private final boolean caveNormalEmote;
        private final String caveNormalMsg;
        private final boolean surfaceWhittyEmote;
        private final String surfaceWhittyMsg;
        private final boolean caveWhittyEmote;
        private final String caveWhittyMsg;
        private final boolean surfaceGrumpyEmote;
        private final String surfaceGrumpyMsg;
        private final boolean caveGrumpyEmote;
        private final String caveGrumpyMsg;
        private final boolean surfaceCheeryEmote;
        private final String surfaceCheeryMsg;
        private final boolean caveCheeryEmote;
        private final String caveCheeryMsg;
        
        private TimedSpeech(final int hour, final boolean surfaceNormalEmote, final String surfaceNormalMsg, final boolean caveNormalEmote, final String caveNormalMsg, final boolean surfaceWhittyEmote, final String surfaceWhittyMsg, final boolean caveWhittyEmote, final String caveWhittyMsg, final boolean surfaceGrumpyEmote, final String surfaceGrumpyMsg, final boolean caveGrumpyEmote, final String caveGrumpyMsg, final boolean surfaceCheeryEmote, final String surfaceCheeryMsg, final boolean caveCheeryEmote, final String caveCheeryMsg) {
            this.hour = hour;
            this.surfaceNormalEmote = surfaceNormalEmote;
            this.surfaceNormalMsg = surfaceNormalMsg;
            this.caveNormalEmote = caveNormalEmote;
            this.caveNormalMsg = caveNormalMsg;
            this.surfaceWhittyEmote = surfaceWhittyEmote;
            this.surfaceWhittyMsg = surfaceWhittyMsg;
            this.caveWhittyEmote = caveWhittyEmote;
            this.caveWhittyMsg = caveWhittyMsg;
            this.surfaceGrumpyEmote = surfaceGrumpyEmote;
            this.surfaceGrumpyMsg = surfaceGrumpyMsg;
            this.caveGrumpyEmote = caveGrumpyEmote;
            this.caveGrumpyMsg = caveGrumpyMsg;
            this.surfaceCheeryEmote = surfaceCheeryEmote;
            this.surfaceCheeryMsg = surfaceCheeryMsg;
            this.caveCheeryEmote = caveCheeryEmote;
            this.caveCheeryMsg = caveCheeryMsg;
        }
        
        private TimedSpeech(final int hour, final boolean surfaceNormalEmote, final String surfaceNormalMsg, final boolean surfaceWhittyEmote, final String surfaceWhittyMsg, final boolean surfaceGrumpyEmote, final String surfaceGrumpyMsg, final boolean surfaceCheeryEmote, final String surfaceCheeryMsg) {
            this(hour, surfaceNormalEmote, surfaceNormalMsg, surfaceNormalEmote, surfaceNormalMsg, surfaceWhittyEmote, surfaceWhittyMsg, surfaceWhittyEmote, surfaceWhittyMsg, surfaceGrumpyEmote, surfaceGrumpyMsg, surfaceGrumpyEmote, surfaceGrumpyMsg, surfaceCheeryEmote, surfaceCheeryMsg, surfaceCheeryEmote, surfaceCheeryMsg);
        }
        
        public int getHour() {
            return this.hour;
        }
        
        public boolean isEmote(final byte speechType, final boolean onSurface) {
            switch (speechType) {
                case 1: {
                    return onSurface ? this.surfaceWhittyEmote : this.caveWhittyEmote;
                }
                case 2: {
                    return onSurface ? this.surfaceGrumpyEmote : this.caveGrumpyEmote;
                }
                case 3: {
                    return onSurface ? this.surfaceCheeryEmote : this.caveCheeryEmote;
                }
                default: {
                    return onSurface ? this.surfaceNormalEmote : this.caveNormalEmote;
                }
            }
        }
        
        public String getMsg(final byte speechType, final boolean onSurface) {
            switch (speechType) {
                case 1: {
                    return onSurface ? this.surfaceWhittyMsg : this.caveWhittyMsg;
                }
                case 2: {
                    return onSurface ? this.surfaceGrumpyMsg : this.caveGrumpyMsg;
                }
                case 3: {
                    return onSurface ? this.surfaceCheeryMsg : this.caveCheeryMsg;
                }
                default: {
                    return onSurface ? this.surfaceNormalMsg : this.caveNormalMsg;
                }
            }
        }
    }
}
