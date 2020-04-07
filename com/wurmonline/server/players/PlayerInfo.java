// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.players;

import com.wurmonline.server.creatures.SpellEffectsEnum;
import com.wurmonline.server.creatures.Communicator;
import java.util.Optional;
import com.wurmonline.server.items.Item;
import com.wurmonline.server.Items;
import com.wurmonline.server.Servers;
import com.wurmonline.server.NoSuchPlayerException;
import com.wurmonline.server.Players;
import java.util.Iterator;
import javax.annotation.Nullable;
import java.util.logging.Level;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import com.wurmonline.server.WurmCalendar;
import com.wurmonline.server.Server;
import com.wurmonline.server.highways.Route;
import java.util.List;
import java.util.BitSet;
import java.util.Map;
import com.wurmonline.server.deities.Deity;
import com.wurmonline.server.steam.SteamId;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;
import com.wurmonline.server.combat.CombatConstants;
import com.wurmonline.server.TimeConstants;
import com.wurmonline.server.MiscConstants;

public abstract class PlayerInfo implements MiscConstants, TimeConstants, CombatConstants, Comparable<PlayerInfo>
{
    public long lastCreatedHistoryEvent;
    public int timeToCheckPrem;
    String name;
    String password;
    public long wurmId;
    public long lastToggledFSleep;
    public long lastLogin;
    private long lastDeath;
    public long playingTime;
    private static Logger logger;
    private ConcurrentHashMap<Long, Friend> friends;
    private boolean hasLoadedFriends;
    private Set<Long> enemies;
    boolean reimbursed;
    public long plantedSign;
    boolean banned;
    String ipaddress;
    SteamId steamId;
    private Set<Long> ignored;
    boolean muted;
    byte power;
    long paymentExpireDate;
    public long version;
    int rank;
    int maxRank;
    public boolean mayHearDevTalk;
    public long lastWarned;
    public int warnings;
    public long lastChangedDeity;
    public byte realdeath;
    float alignment;
    Deity deity;
    float faith;
    float favor;
    Deity god;
    public static final String LOADED_CLASSES_DISCONNECT = "CLASS_CHECK_DISCONNECT";
    public long lastCheated;
    public int fatigueSecsLeft;
    public int fatigueSecsToday;
    public int fatigueSecsYesterday;
    public long lastFatigue;
    public static final int MAX_FATIGUE_SECONDS = 43200;
    private static final int FATIGUE_INCREASE_TIME = 3600;
    private static final long FATIGUE_INCREASE_DELAY_PREMIUM = 10800000L;
    public static final long MINTIME_BETWEEN_CHAMPION = 14515200000L;
    public boolean dead;
    String sessionKey;
    public long sessionExpiration;
    public byte numFaith;
    public long lastFaith;
    protected byte sex;
    public long money;
    public boolean climbing;
    protected boolean spamMode;
    private long lastGasp;
    byte changedKingdom;
    public long MIN_KINGDOM_CHANGE_TIME;
    public long lastChangedKindom;
    public long championTimeStamp;
    public short championPoints;
    public long creationDate;
    public String banreason;
    public long banexpiry;
    public long face;
    protected byte blood;
    public long lastChangedCluster;
    public short muteTimes;
    public long nextAvailableMute;
    public long startedReceivingMutes;
    public short mutesReceived;
    public int reputation;
    public long lastPolledReputation;
    Set<Titles.Title> titles;
    public Titles.Title title;
    public Titles.Title secondTitle;
    public String kingdomtitle;
    public long pet;
    public float alcohol;
    public float nicotine;
    public long alcoholAddiction;
    public long nicotineAddiction;
    public boolean mayMute;
    public String mutereason;
    public long muteexpiry;
    public boolean logging;
    public int lastServer;
    public int currentServer;
    public boolean loaded;
    public long referrer;
    public String emailAddress;
    public long lastLogout;
    public String pwQuestion;
    public String pwAnswer;
    public long lastRequestedPassword;
    long lastChangedVillage;
    public boolean isPriest;
    public long bed;
    public int sleep;
    public boolean frozenSleep;
    public boolean overRideShop;
    public boolean isTheftWarned;
    public boolean noReimbursementLeft;
    public boolean deathProtected;
    public byte fightmode;
    public long nextAffinity;
    public int tutorialLevel;
    public boolean autoFighting;
    public long appointments;
    public long lastvehicle;
    boolean playerAssistant;
    boolean mayAppointPlayerAssistant;
    boolean seesPlayerAssistantWindow;
    protected boolean hasMovedInventory;
    public byte priestType;
    public long lastChangedPriestType;
    byte lastTaggedKindom;
    private final Map<Long, Integer> macroAttackers;
    private final Map<Long, Integer> macroArchers;
    private static final int MAX_MACRO_ATTACKS = 100;
    long lastMovedBetweenKingdom;
    public long lastModifiedRank;
    public long lastChangedJoat;
    public boolean hasFreeTransfer;
    public int lastTriggerEffect;
    public boolean hasSkillGain;
    public float champChanneling;
    public boolean votedKing;
    public int epicServerId;
    public byte epicKingdom;
    public long lastUsedEpicPortal;
    byte chaosKingdom;
    short hotaWins;
    protected int karma;
    protected int maxKarma;
    protected int totalKarma;
    public long abilities;
    public int abilityTitle;
    public Awards awards;
    protected BitSet abilityBits;
    public long flags;
    public long flags2;
    protected BitSet flagBits;
    protected BitSet flag2Bits;
    public int scenarioKarma;
    public byte undeadType;
    public int undeadKills;
    public int undeadPlayerKills;
    public int undeadPlayerSeconds;
    private long moneyToSend;
    private final ConcurrentHashMap<String, Long> targetPMIds;
    private long sessionFlags;
    protected BitSet sessionFlagBits;
    protected String modelName;
    private long moneyEarnedBySellingLastHour;
    protected long moneyEarnedBySellingEver;
    private long lastResetEarningsCounter;
    public final ConcurrentHashMap<String, Long> historyIPStart;
    public final ConcurrentHashMap<String, Long> historyIPLast;
    public final ConcurrentHashMap<String, Long> historyEmail;
    public final ConcurrentHashMap<SteamId, SteamIdHistory> historySteamId;
    private Map<Short, SpellResistance> spellResistances;
    private float limitingArmourFactor;
    private long lastChangedPath;
    private List<Route> highwayPath;
    private List<Float> highwayDistances;
    private String highwayPathDestination;
    
    PlayerInfo(final String aname) {
        this.lastCreatedHistoryEvent = 0L;
        this.timeToCheckPrem = 61 + Server.rand.nextInt(28000);
        this.name = null;
        this.password = "EmptyPassword";
        this.wurmId = -10L;
        this.lastToggledFSleep = 0L;
        this.lastLogin = 0L;
        this.lastDeath = 0L;
        this.playingTime = 0L;
        this.hasLoadedFriends = false;
        this.reimbursed = (!WurmCalendar.isChristmas() && !WurmCalendar.isEaster());
        this.plantedSign = System.currentTimeMillis() - 604800000L;
        this.banned = false;
        this.ipaddress = "";
        this.ignored = null;
        this.muted = false;
        this.power = 0;
        this.version = 0L;
        this.rank = 1000;
        this.maxRank = 1000;
        this.mayHearDevTalk = false;
        this.lastWarned = 0L;
        this.warnings = 0;
        this.lastChangedDeity = System.currentTimeMillis() - 604800000L;
        this.realdeath = 0;
        this.alignment = 1.0f;
        this.deity = null;
        this.faith = 0.0f;
        this.favor = 0.0f;
        this.god = null;
        this.lastCheated = 0L;
        this.fatigueSecsLeft = 28800;
        this.fatigueSecsToday = 0;
        this.fatigueSecsYesterday = 0;
        this.lastFatigue = System.currentTimeMillis();
        this.dead = false;
        this.sessionKey = "";
        this.sessionExpiration = 0L;
        this.money = 0L;
        this.climbing = false;
        this.spamMode = true;
        this.lastGasp = 0L;
        this.changedKingdom = 0;
        this.MIN_KINGDOM_CHANGE_TIME = 1209600000L;
        this.lastChangedKindom = System.currentTimeMillis() - 1209600000L;
        this.championTimeStamp = 0L;
        this.championPoints = 0;
        this.creationDate = System.currentTimeMillis();
        this.banreason = "";
        this.banexpiry = 0L;
        this.face = 0L;
        this.blood = 0;
        this.lastChangedCluster = 0L;
        this.muteTimes = 0;
        this.nextAvailableMute = 0L;
        this.startedReceivingMutes = 0L;
        this.mutesReceived = 0;
        this.reputation = 100;
        this.lastPolledReputation = System.currentTimeMillis();
        this.titles = new HashSet<Titles.Title>();
        this.title = null;
        this.secondTitle = null;
        this.kingdomtitle = "";
        this.pet = -10L;
        this.alcohol = 0.0f;
        this.nicotine = 0.0f;
        this.alcoholAddiction = 0L;
        this.nicotineAddiction = 0L;
        this.mayMute = false;
        this.mutereason = "";
        this.muteexpiry = 0L;
        this.logging = false;
        this.loaded = false;
        this.referrer = 0L;
        this.emailAddress = "";
        this.lastLogout = 0L;
        this.pwQuestion = "";
        this.pwAnswer = "";
        this.lastRequestedPassword = 0L;
        this.lastChangedVillage = 0L;
        this.isPriest = false;
        this.bed = -10L;
        this.sleep = 0;
        this.frozenSleep = true;
        this.overRideShop = false;
        this.isTheftWarned = false;
        this.noReimbursementLeft = false;
        this.deathProtected = false;
        this.fightmode = 2;
        this.nextAffinity = 0L;
        this.tutorialLevel = 0;
        this.autoFighting = false;
        this.appointments = 0L;
        this.lastvehicle = -10L;
        this.playerAssistant = false;
        this.mayAppointPlayerAssistant = false;
        this.seesPlayerAssistantWindow = false;
        this.hasMovedInventory = false;
        this.priestType = 0;
        this.lastChangedPriestType = 0L;
        this.lastTaggedKindom = 0;
        this.macroAttackers = new HashMap<Long, Integer>();
        this.macroArchers = new HashMap<Long, Integer>();
        this.lastMovedBetweenKingdom = System.currentTimeMillis();
        this.lastModifiedRank = System.currentTimeMillis();
        this.lastChangedJoat = System.currentTimeMillis();
        this.hasFreeTransfer = false;
        this.lastTriggerEffect = 0;
        this.hasSkillGain = true;
        this.champChanneling = 0.0f;
        this.votedKing = false;
        this.epicServerId = -1;
        this.epicKingdom = 0;
        this.lastUsedEpicPortal = 0L;
        this.chaosKingdom = 0;
        this.hotaWins = 0;
        this.karma = 0;
        this.maxKarma = 0;
        this.totalKarma = 0;
        this.abilityTitle = -1;
        this.awards = null;
        this.abilityBits = new BitSet(64);
        this.flagBits = new BitSet(64);
        this.flag2Bits = new BitSet(64);
        this.scenarioKarma = 0;
        this.undeadType = 0;
        this.undeadKills = 0;
        this.undeadPlayerKills = 0;
        this.undeadPlayerSeconds = 0;
        this.moneyToSend = 0L;
        this.targetPMIds = new ConcurrentHashMap<String, Long>();
        this.sessionFlags = 0L;
        this.sessionFlagBits = new BitSet(64);
        this.modelName = "Human";
        this.moneyEarnedBySellingLastHour = 0L;
        this.moneyEarnedBySellingEver = 0L;
        this.lastResetEarningsCounter = 0L;
        this.historyIPStart = new ConcurrentHashMap<String, Long>();
        this.historyIPLast = new ConcurrentHashMap<String, Long>();
        this.historyEmail = new ConcurrentHashMap<String, Long>();
        this.historySteamId = new ConcurrentHashMap<SteamId, SteamIdHistory>();
        this.limitingArmourFactor = 0.3f;
        this.lastChangedPath = 0L;
        this.highwayPath = null;
        this.highwayDistances = null;
        this.highwayPathDestination = "";
        this.name = aname;
    }
    
    @Override
    public int compareTo(final PlayerInfo otherPlayerInfo) {
        return this.getName().compareTo(otherPlayerInfo.getName());
    }
    
    public final int getPower() {
        return this.power;
    }
    
    public final long getPaymentExpire() {
        return System.currentTimeMillis() + 29030400000L;
    }
    
    public abstract void setPower(final byte p0) throws IOException;
    
    public abstract void setPaymentExpire(final long p0) throws IOException;
    
    public abstract void setPaymentExpire(final long p0, final boolean p1) throws IOException;
    
    public final boolean isPaying() {
        return true;
    }
    
    public final boolean isQAAccount() {
        return this.isFlagSet(26);
    }
    
    public final boolean isBanned() {
        return this.banned;
    }
    
    public final int getChangedKingdom() {
        return this.changedKingdom;
    }
    
    public abstract void setBanned(final boolean p0, final String p1, final long p2) throws IOException;
    
    public final void setLogin() {
        this.calculateSleep();
        this.lastLogin = System.currentTimeMillis();
    }
    
    public final long getLastLogin() {
        return this.lastLogin;
    }
    
    public final long getLastLogout() {
        return this.lastLogout;
    }
    
    public final boolean mayBecomeChampion() {
        return System.currentTimeMillis() - this.championTimeStamp > 14515200000L;
    }
    
    public final short getChampionPoints() {
        return this.championPoints;
    }
    
    public final boolean isReimbursed() {
        return this.reimbursed;
    }
    
    public final boolean hasLoadedFriends() {
        return this.hasLoadedFriends;
    }
    
    protected final void setLoadedFriends(final boolean hasLoaded) {
        this.hasLoadedFriends = hasLoaded;
    }
    
    public final long getPlayerId() {
        return this.wurmId;
    }
    
    public final String getPassword() {
        return this.password;
    }
    
    final boolean hasPlantedSign() {
        return System.currentTimeMillis() - this.plantedSign < 86400000L;
    }
    
    public final Titles.Title[] getTitles() {
        return this.titles.toArray(new Titles.Title[this.titles.size()]);
    }
    
    final boolean mayChangeDeity(final int targetDeity) {
        return targetDeity == 4 || System.currentTimeMillis() - this.lastChangedDeity > 604800000L;
    }
    
    public final void setPassword(final String pw) {
        this.password = pw;
        try {
            this.save();
        }
        catch (IOException iox) {
            PlayerInfo.logger.log(Level.WARNING, "Failed to change password for " + this.name, iox);
        }
    }
    
    public void initialize(final String aName, final long aWurmId, final String aPassword, final String aPwQuestion, final String aPwAnswer, final long aFace, final boolean aGuest) throws IOException {
        this.name = aName;
        this.wurmId = aWurmId;
        this.password = aPassword;
        this.face = aFace;
        this.pwQuestion = aPwQuestion;
        this.pwAnswer = aPwAnswer;
        this.lastLogout = System.currentTimeMillis();
        this.flagBits.set(3, true);
        this.flags = this.getFlagLong();
        if (!aGuest) {
            this.save();
        }
        PlayerInfoFactory.addPlayerInfo(this);
    }
    
    public final String getName() {
        return this.name;
    }
    
    public final Friend[] getFriends() {
        if (!this.hasLoadedFriends()) {
            this.loadFriends(this.wurmId);
        }
        if (this.friends != null) {
            return this.friends.values().toArray(new Friend[this.friends.size()]);
        }
        return new Friend[0];
    }
    
    @Nullable
    public final Friend getFriend(final long friendId) {
        if (!this.hasLoadedFriends()) {
            this.loadFriends(friendId);
        }
        return this.friends.get(friendId);
    }
    
    final void addFriend(final long friendId, final byte catId, final String note, final boolean loading) {
        if (this.friends == null) {
            this.friends = new ConcurrentHashMap<Long, Friend>();
        }
        final Long fid = new Long(friendId);
        if (!this.friends.containsKey(fid)) {
            this.friends.put(fid, new Friend(friendId, catId, note));
            if (!loading) {
                try {
                    this.saveFriend(this.wurmId, friendId, catId, note);
                }
                catch (IOException iox) {
                    if (this.name != null) {
                        PlayerInfo.logger.log(Level.WARNING, "Failed to save friends for " + this.name, iox);
                    }
                    else {
                        PlayerInfo.logger.log(Level.WARNING, "Failed to save friends for unknown player.", iox);
                    }
                }
            }
        }
    }
    
    final void updateFriendData(final long friendId, final byte catId, final String note) {
        if (this.friends == null) {
            this.friends = new ConcurrentHashMap<Long, Friend>();
        }
        final Long fid = new Long(friendId);
        if (this.friends.containsKey(fid)) {
            final Friend friend = this.friends.put(fid, new Friend(friendId, catId, note));
            if (friend.getCatId() == catId) {
                if (friend.getNote().equals(note)) {
                    return;
                }
            }
            try {
                this.updateFriend(this.wurmId, friendId, catId, note);
            }
            catch (IOException iox) {
                if (this.name != null) {
                    PlayerInfo.logger.log(Level.WARNING, "Failed to update friend (" + friend.getName() + ") for " + this.name, iox);
                }
                else {
                    PlayerInfo.logger.log(Level.WARNING, "Failed to update friend (" + friend.getName() + ") for unknown player.", iox);
                }
            }
        }
    }
    
    public final boolean isFriendsWith(final long friendId) {
        if (this.friends == null) {
            this.loadFriends(this.wurmId);
        }
        return this.friends != null && this.friends.containsKey(friendId);
    }
    
    public final boolean removeFriend(final long friendId) {
        if (this.friends == null) {
            this.loadFriends(this.wurmId);
        }
        if (this.friends != null) {
            final Long fid = new Long(friendId);
            if (this.friends.containsKey(fid)) {
                this.friends.remove(fid);
                try {
                    this.deleteFriend(this.wurmId, friendId);
                }
                catch (IOException iox) {
                    if (this.name != null) {
                        PlayerInfo.logger.log(Level.WARNING, "Failed to save friends for " + this.name, iox);
                    }
                    else {
                        PlayerInfo.logger.log(Level.WARNING, "Failed to save friends for unknown player.", iox);
                    }
                }
                return true;
            }
        }
        return false;
    }
    
    final void addEnemy(final long enemyId, final boolean loading) {
        if (this.enemies == null) {
            this.enemies = new HashSet<Long>();
        }
        final Long fid = new Long(enemyId);
        if (!this.enemies.contains(fid)) {
            this.enemies.add(fid);
            if (!loading) {
                try {
                    this.saveEnemy(this.wurmId, enemyId);
                }
                catch (IOException iox) {
                    if (this.name != null) {
                        PlayerInfo.logger.log(Level.WARNING, "Failed to save friends for " + this.name, iox);
                    }
                    else {
                        PlayerInfo.logger.log(Level.WARNING, "Failed to save friends for unknown player.", iox);
                    }
                }
            }
        }
    }
    
    public final boolean removeIgnored(final long ignoredId) {
        if (this.ignored != null) {
            final Long fid = new Long(ignoredId);
            if (this.ignored.contains(fid)) {
                this.ignored.remove(fid);
                try {
                    this.deleteIgnored(this.wurmId, ignoredId);
                    return true;
                }
                catch (IOException iox) {
                    if (this.name != null) {
                        PlayerInfo.logger.log(Level.WARNING, "Failed to delete ignored for " + this.name, iox);
                    }
                    else {
                        PlayerInfo.logger.log(Level.WARNING, "Failed to delete ignored for unknown player.", iox);
                    }
                }
            }
        }
        return false;
    }
    
    final boolean isIgnored(final long playerId) {
        if (this.ignored != null) {
            for (final Long id : this.ignored) {
                if (id == playerId) {
                    return true;
                }
            }
            return false;
        }
        return false;
    }
    
    public final long[] getIgnored() {
        long[] toReturn = PlayerInfo.EMPTY_LONG_PRIMITIVE_ARRAY;
        if (this.ignored != null && this.ignored.size() > 0) {
            toReturn = new long[this.ignored.size()];
            int x = 0;
            for (final Long ig : this.ignored) {
                toReturn[x] = ig;
                ++x;
            }
        }
        return toReturn;
    }
    
    public final int getWarnings() {
        return this.warnings;
    }
    
    public final long getLastWarned() {
        return this.lastWarned;
    }
    
    public final String getWarningStats(final long getLastWarned) {
        String warnString = this.name + " has never been warned before.";
        if (getLastWarned > 0L) {
            warnString = "Last warning received was " + Server.getTimeFor(System.currentTimeMillis() - getLastWarned) + " ago.";
        }
        return this.name + " has played " + Server.getTimeFor(this.playingTime) + " and received " + this.warnings + " warnings. " + warnString;
    }
    
    public abstract void resetWarnings() throws IOException;
    
    public final int getRank() {
        return this.rank;
    }
    
    public final int getMaxRank() {
        return this.maxRank;
    }
    
    public final float getAlignment() {
        return this.alignment;
    }
    
    public final float getFaith() {
        return this.faith;
    }
    
    public final Deity getDeity() {
        return this.deity;
    }
    
    public final Deity getGod() {
        return this.god;
    }
    
    public boolean spamMode() {
        return this.spamMode;
    }
    
    public final float getFavor() {
        return this.favor;
    }
    
    final void sendReligionStatus(final int itemNum, final float value) {
        try {
            final Player p = Players.getInstance().getPlayer(this.wurmId);
            p.getCommunicator().sendUpdateSkill(itemNum, value, 0);
            p.checkFaithTitles();
        }
        catch (NoSuchPlayerException ex) {}
    }
    
    final void sendAttitudeChange() {
        try {
            Players.getInstance().getPlayer(this.wurmId).sendAttitudeChange();
        }
        catch (NoSuchPlayerException ex) {}
    }
    
    final boolean checkPrayerFaith() {
        if (this.deity == null) {
            return false;
        }
        if (this.numFaith >= (this.isFlagSet(81) ? 6 : 5)) {
            return false;
        }
        if (System.currentTimeMillis() - this.lastFaith > 1200000L) {
            this.lastFaith = System.currentTimeMillis();
            if (this.getFaith() < 30.0f || this.isPaying()) {
                if (!Servers.localServer.isChallengeServer()) {
                    this.modifyFaith(Math.min(1.0f, (100.0f - this.getFaith()) / (10.0f * Math.max(1.0f, this.getFaith()))));
                }
                else {
                    this.modifyFaith(1.0f);
                }
                ++this.numFaith;
            }
            try {
                this.setNumFaith(this.numFaith, this.lastFaith);
            }
            catch (IOException iox) {
                PlayerInfo.logger.log(Level.WARNING, this.name + " " + iox.getMessage(), iox);
            }
            return true;
        }
        return false;
    }
    
    final void modifyFaith(final float mod) {
        if (this.deity != null && mod != 0.0f) {
            if (this.getFaith() >= 30.0f && mod >= 0.0f) {
                if (!this.isPriest || !this.isPaying()) {
                    return;
                }
            }
            try {
                this.setFaith(Math.max(1.0f, this.getFaith() + Math.min(1.0f, mod)));
            }
            catch (IOException iox) {
                PlayerInfo.logger.log(Level.WARNING, this.name, iox);
            }
        }
    }
    
    final void decreaseFatigue() {
        --this.fatigueSecsLeft;
        ++this.fatigueSecsToday;
        if (this.fatigueSecsLeft % 100 == 0) {
            this.setFatigueSecs(this.fatigueSecsLeft, this.lastFatigue);
        }
    }
    
    final boolean checkFatigue() {
        long times = 0L;
        times = (System.currentTimeMillis() - this.lastFatigue) / 10800000L;
        if (times > 0L) {
            for (int x = 0; x < Math.min(times, 8L); ++x) {
                this.fatigueSecsLeft = Math.min(this.fatigueSecsLeft + 3600, 43200);
            }
            this.lastFatigue += times * 10800000L;
            this.setFatigueSecs(this.fatigueSecsLeft, this.lastFatigue);
            return true;
        }
        return false;
    }
    
    final int hardSetFatigueSecs(final int fatsecsleft) {
        return this.fatigueSecsLeft = Math.max(0, Math.min(this.fatigueSecsLeft + fatsecsleft, 43200));
    }
    
    public final boolean isMute() {
        return this.muted;
    }
    
    public final boolean addIgnored(final long id, final boolean load) throws IOException {
        if (this.ignored == null) {
            this.ignored = new HashSet<Long>();
        }
        if (!this.ignored.contains(new Long(id))) {
            this.ignored.add(new Long(id));
            if (!load) {
                this.saveIgnored(this.wurmId, id);
            }
            return true;
        }
        return false;
    }
    
    public final int getReputation() {
        return this.reputation;
    }
    
    final void pollReputation(final long now) {
        if (now > this.lastPolledReputation + 3600000L) {
            final long nums = (now - this.lastPolledReputation) / 3600000L;
            this.setReputation(this.reputation + (int)nums);
            this.lastPolledReputation = System.currentTimeMillis();
        }
    }
    
    public final void logout() {
        if (this.lastLogin > 0L) {
            this.playingTime = this.playingTime + System.currentTimeMillis() - this.lastLogin;
        }
        if (this.lastLogin > 0L) {
            this.lastLogout = System.currentTimeMillis();
        }
        this.setSessionFlags(this.lastLogin = 0L);
    }
    
    public final int getSleepLeft() {
        if (this.sleep <= 0) {
            this.frozenSleep = true;
        }
        return this.sleep;
    }
    
    public boolean isSleepFrozen() {
        return this.frozenSleep;
    }
    
    final boolean hasSleepBonus() {
        return !this.frozenSleep && this.sleep > 0;
    }
    
    private void calculateSleep() {
        if (this.bed > 0L) {
            long sleepTime = System.currentTimeMillis() - this.lastLogout;
            if (sleepTime > 10800000L) {
                final Optional<Item> beds = Items.getItemOptional(this.bed);
                if (beds.isPresent()) {
                    final Item bed = beds.get();
                    if (bed.isBed()) {
                        bed.setData(0L);
                    }
                }
            }
            if (sleepTime > 3600000L) {
                sleepTime /= 1000L;
                final long secs = sleepTime / 24L;
                this.setSleep((int)(this.sleep + secs));
            }
            this.setBed(0L);
        }
    }
    
    public final void addToSleep(final int secs) {
        this.setSleep(this.sleep + secs);
        try {
            final Player p = Players.getInstance().getPlayer(this.wurmId);
            p.getCommunicator().sendSleepInfo();
        }
        catch (NoSuchPlayerException ex) {}
    }
    
    final boolean eligibleForAffinity() {
        if (System.currentTimeMillis() > this.nextAffinity || this.nextAffinity == 0L) {
            this.setNextAffinity(System.currentTimeMillis() + 2419200000L + Server.rand.nextInt(50000));
            return true;
        }
        return false;
    }
    
    abstract void setReputation(final int p0);
    
    public abstract void setMuted(final boolean p0, final String p1, final long p2);
    
    abstract void setFatigueSecs(final int p0, final long p1);
    
    abstract void setCheated(final String p0);
    
    public abstract void updatePassword(final String p0) throws IOException;
    
    public abstract void setRealDeath(final byte p0) throws IOException;
    
    public abstract void setFavor(final float p0) throws IOException;
    
    public abstract void setFaith(final float p0) throws IOException;
    
    abstract void setDeity(final Deity p0) throws IOException;
    
    abstract void setAlignment(final float p0) throws IOException;
    
    abstract void setGod(final Deity p0) throws IOException;
    
    public abstract void load() throws IOException;
    
    public abstract void warn() throws IOException;
    
    public abstract void save() throws IOException;
    
    public abstract void setLastTrigger(final int p0);
    
    public int getLastTrigger() {
        return this.lastTriggerEffect;
    }
    
    abstract void setIpaddress(final String p0) throws IOException;
    
    abstract void setSteamId(final SteamId p0) throws IOException;
    
    public abstract void setRank(final int p0) throws IOException;
    
    public abstract void setReimbursed(final boolean p0) throws IOException;
    
    abstract void setPlantedSign() throws IOException;
    
    abstract void setChangedDeity() throws IOException;
    
    public abstract String getIpaddress();
    
    abstract void setDead(final boolean p0);
    
    public abstract void setSessionKey(final String p0, final long p1) throws IOException;
    
    abstract void setName(final String p0) throws IOException;
    
    public abstract void setVersion(final long p0) throws IOException;
    
    abstract void saveFriend(final long p0, final long p1, final byte p2, final String p3) throws IOException;
    
    abstract void updateFriend(final long p0, final long p1, final byte p2, final String p3) throws IOException;
    
    abstract void deleteFriend(final long p0, final long p1) throws IOException;
    
    abstract void saveEnemy(final long p0, final long p1) throws IOException;
    
    abstract void deleteEnemy(final long p0, final long p1) throws IOException;
    
    abstract void saveIgnored(final long p0, final long p1) throws IOException;
    
    abstract void deleteIgnored(final long p0, final long p1) throws IOException;
    
    public abstract void setNumFaith(final byte p0, final long p1) throws IOException;
    
    abstract long getFlagLong();
    
    abstract long getFlag2Long();
    
    public abstract void setMoney(final long p0) throws IOException;
    
    abstract void setSex(final byte p0) throws IOException;
    
    abstract void setClimbing(final boolean p0) throws IOException;
    
    abstract void setChangedKingdom(final byte p0, final boolean p1) throws IOException;
    
    public abstract void setFace(final long p0) throws IOException;
    
    abstract boolean addTitle(final Titles.Title p0);
    
    abstract boolean removeTitle(final Titles.Title p0);
    
    abstract void setAlcohol(final float p0);
    
    abstract void setPet(final long p0);
    
    public abstract void setNicotineTime(final long p0);
    
    public abstract boolean setAlcoholTime(final long p0);
    
    abstract void setNicotine(final float p0);
    
    public abstract void setMayMute(final boolean p0);
    
    public abstract void setEmailAddress(final String p0);
    
    abstract void setPriest(final boolean p0);
    
    public abstract void setOverRideShop(final boolean p0);
    
    public abstract void setReferedby(final long p0);
    
    public abstract void setBed(final long p0);
    
    abstract void setLastChangedVillage(final long p0);
    
    abstract void setSleep(final int p0);
    
    abstract void setTheftwarned(final boolean p0);
    
    public abstract void setHasNoReimbursementLeft(final boolean p0);
    
    abstract void setDeathProtected(final boolean p0);
    
    public int getCurrentServer() {
        return this.currentServer;
    }
    
    final void addAppointment(final int aid) {
        if (!this.hasAppointment(aid)) {
            this.appointments += 1L << aid;
            this.saveAppointments();
        }
    }
    
    final void removeAppointment(final int aid) {
        if (this.hasAppointment(aid)) {
            this.appointments -= 1L << aid;
            this.saveAppointments();
        }
    }
    
    final void clearAppointments() {
        this.appointments = 0L;
        this.saveAppointments();
    }
    
    final boolean hasAppointment(final int aid) {
        return (this.appointments >> aid & 0x1L) == 0x1L;
    }
    
    public final boolean isPlayerAssistant() {
        return this.playerAssistant;
    }
    
    public final boolean mayAppointPlayerAssistant() {
        return this.mayAppointPlayerAssistant;
    }
    
    public final boolean seesPlayerAssistantWindow() {
        return this.seesPlayerAssistantWindow;
    }
    
    public final boolean hasMovedInventory() {
        return this.hasMovedInventory;
    }
    
    public final boolean mayUseLastGasp() {
        return System.currentTimeMillis() - this.lastGasp > 21600000L;
    }
    
    public final void useLastGasp() {
        this.lastGasp = System.currentTimeMillis();
    }
    
    public final boolean isUsingLastGasp() {
        return System.currentTimeMillis() - this.lastGasp < 120000L;
    }
    
    public final byte getChaosKingdom() {
        return this.chaosKingdom;
    }
    
    public final short getHotaWins() {
        return this.hotaWins;
    }
    
    public final long getLastDeath() {
        return this.lastDeath;
    }
    
    public final void died() {
        this.lastDeath = System.currentTimeMillis();
    }
    
    protected final void checkHotaTitles() {
        if (this.hotaWins == 1) {
            this.addTitle(Titles.Title.Hota_One);
        }
        if (this.hotaWins == 3) {
            this.addTitle(Titles.Title.Hota_Two);
        }
        if (this.hotaWins == 7) {
            this.addTitle(Titles.Title.Hota_Three);
        }
        if (this.hotaWins == 15) {
            this.addTitle(Titles.Title.Hota_Four);
        }
        if (this.hotaWins == 30) {
            this.addTitle(Titles.Title.Hota_Five);
        }
    }
    
    public abstract void setCurrentServer(final int p0);
    
    public abstract void setDevTalk(final boolean p0);
    
    public abstract void transferDeity(@Nullable final Deity p0) throws IOException;
    
    abstract void saveSwitchFatigue();
    
    abstract void saveFightMode(final byte p0);
    
    abstract void setNextAffinity(final long p0);
    
    public abstract void saveAppointments();
    
    abstract void setTutorialLevel(final int p0);
    
    abstract void setAutofight(final boolean p0);
    
    abstract void setLastVehicle(final long p0);
    
    public abstract void setIsPlayerAssistant(final boolean p0);
    
    public abstract void setMayAppointPlayerAssistant(final boolean p0);
    
    public abstract boolean togglePlayerAssistantWindow(final boolean p0);
    
    public abstract void setLastTaggedTerr(final byte p0);
    
    public abstract void setNewPriestType(final byte p0, final long p1);
    
    public abstract void setChangedJoat();
    
    public abstract void setMovedInventory(final boolean p0);
    
    public abstract void setFreeTransfer(final boolean p0);
    
    public abstract boolean setHasSkillGain(final boolean p0);
    
    public abstract void loadIgnored(final long p0);
    
    public abstract void loadTitles(final long p0);
    
    public abstract void loadFriends(final long p0);
    
    public abstract void loadHistorySteamIds(final long p0);
    
    public abstract void loadHistoryIPs(final long p0);
    
    public abstract void loadHistoryEmails(final long p0);
    
    public abstract boolean setChampionPoints(final short p0);
    
    public abstract void setChangedKingdom();
    
    public abstract void setChampionTimeStamp();
    
    public abstract void setChampChanneling(final float p0);
    
    public abstract void setMuteTimes(final short p0);
    
    public abstract void setVotedKing(final boolean p0);
    
    public abstract void setEpicLocation(final byte p0, final int p1);
    
    public abstract void setChaosKingdom(final byte p0);
    
    public abstract void setHotaWins(final short p0);
    
    public abstract void setSpamMode(final boolean p0);
    
    public abstract void setKarma(final int p0);
    
    public abstract void setScenarioKarma(final int p0);
    
    public int getKarma() {
        return this.karma;
    }
    
    public int getMaxKarma() {
        return this.maxKarma;
    }
    
    public int getTotalKarma() {
        return this.totalKarma;
    }
    
    public int getScenarioKarma() {
        return this.scenarioKarma;
    }
    
    public final boolean isAbilityBitSet(final int abilityBit) {
        return this.abilities != 0L && this.abilityBits.get(abilityBit);
    }
    
    public final boolean isFlagSet(final int flagBit) {
        if (flagBit < 64) {
            if (this.flags != 0L) {
                return this.flagBits.get(flagBit);
            }
        }
        else if (this.flags2 != 0L) {
            return this.flag2Bits.get(flagBit - 64);
        }
        return false;
    }
    
    public byte getBlood() {
        return this.blood;
    }
    
    public abstract void setBlood(final byte p0);
    
    public abstract void setFlag(final int p0, final boolean p1);
    
    public abstract void setFlagBits(final long p0);
    
    public abstract void setFlag2Bits(final long p0);
    
    public abstract void forceFlagsUpdate();
    
    public abstract void setAbility(final int p0, final boolean p1);
    
    public abstract void setCurrentAbilityTitle(final int p0);
    
    public abstract void setUndeadData();
    
    public ConcurrentHashMap<String, Long> getAllTargetPMIds() {
        return this.targetPMIds;
    }
    
    public boolean hasPMTarget(final String targetName) {
        return this.targetPMIds.containsKey(targetName);
    }
    
    public long getPMTargetId(final String targetName) {
        if (this.targetPMIds.containsKey(targetName)) {
            return this.targetPMIds.get(targetName);
        }
        return -10L;
    }
    
    public void addPMTarget(final String targetName, final long targetId) {
        if (!this.targetPMIds.containsKey(targetName)) {
            this.targetPMIds.put(targetName, targetId);
        }
    }
    
    public void removePMTarget(final String targetName) {
        if (this.targetPMIds.containsKey(targetName)) {
            this.targetPMIds.remove(targetName);
        }
    }
    
    public long getSessionFlags() {
        return this.sessionFlags;
    }
    
    public void setSessionFlags(final long aFlags) {
        this.sessionFlags = aFlags;
        this.sessionFlagBits.clear();
        for (int x = 0; x < 64; ++x) {
            if ((aFlags >>> x & 0x1L) == 0x1L) {
                this.sessionFlagBits.set(x);
            }
        }
    }
    
    public final boolean isSessionFlagSet(final int flagBit) {
        return this.sessionFlags != 0L && this.sessionFlagBits.get(flagBit);
    }
    
    public final void setSessionFlag(final int number, final boolean value) {
        this.sessionFlagBits.set(number, value);
        this.sessionFlags = this.getSessionFlagLong();
    }
    
    public final String getModelName() {
        return this.modelName;
    }
    
    public abstract void setModelName(final String p0);
    
    private final long getSessionFlagLong() {
        long ret = 0L;
        for (int x = 0; x < 64; ++x) {
            if (this.sessionFlagBits.get(x)) {
                ret += 1L << x;
            }
        }
        return ret;
    }
    
    @Override
    public final String toString() {
        return "PlayerInfo [wurmId: " + this.wurmId + ", name: " + this.name + ", currentServer: " + this.currentServer + ", lastLogin: " + this.lastLogin + ", lastLogout: " + this.lastLogout + ", banned: " + this.banned + ", ipaddress: " + this.ipaddress + ", power: " + this.power + ", creationDate: " + this.creationDate + ", paymentExpireDate: " + this.paymentExpireDate + ", playingTime: " + this.playingTime + ", money: " + this.money + ']';
    }
    
    public long getMoneyEarnedBySellingLastHour() {
        return this.moneyEarnedBySellingLastHour;
    }
    
    public final long getMoneyToSend() {
        return this.moneyToSend;
    }
    
    public final void resetMoneyToSend() {
        this.moneyToSend = 0L;
    }
    
    public void addMoneyEarnedBySellingLastHour(final long aMoney) {
        this.moneyToSend += aMoney;
        if (this.getMoneyEarnedBySellingLastHour() == 0L) {
            this.setLastResetEarningsCounter(System.currentTimeMillis());
        }
        this.setMoneyEarnedBySellingLastHour(this.getMoneyEarnedBySellingLastHour() + aMoney);
        this.addMoneyEarnedBySellingEver(aMoney);
    }
    
    public void checkIfResetSellEarning() {
        if (System.currentTimeMillis() - this.getLastResetEarningsCounter() > (Servers.isThisATestServer() ? 20000L : 3600000L)) {
            this.setLastResetEarningsCounter(System.currentTimeMillis());
            this.setMoneyEarnedBySellingLastHour(0L);
        }
    }
    
    public long getLastResetEarningsCounter() {
        return this.lastResetEarningsCounter;
    }
    
    public long getMoneyEarnedBySellingEver() {
        return this.moneyEarnedBySellingEver;
    }
    
    public abstract void addMoneyEarnedBySellingEver(final long p0);
    
    public abstract void setPointsForChamp();
    
    public abstract void switchChamp();
    
    public void setMoneyEarnedBySellingLastHour(final long aMoneyEarnedBySellingLastHour) {
        this.moneyEarnedBySellingLastHour = aMoneyEarnedBySellingLastHour;
    }
    
    public void setLastResetEarningsCounter(final long aLastResetEarningsCounter) {
        this.lastResetEarningsCounter = aLastResetEarningsCounter;
    }
    
    public abstract void setPassRetrieval(final String p0, final String p1) throws IOException;
    
    public final float addSpellResistance(final short spellId) {
        if (this.spellResistances == null) {
            this.spellResistances = new ConcurrentHashMap<Short, SpellResistance>();
        }
        SpellResistance existing = this.spellResistances.get(spellId);
        if (existing == null) {
            existing = new SpellResistance(spellId);
            this.spellResistances.put(spellId, existing);
        }
        final float toReturn = existing.getResistance();
        existing.setResistance();
        return 1.0f - toReturn;
    }
    
    public final SpellResistance getSpellResistance(final short spellId) {
        if (this.spellResistances == null) {
            this.spellResistances = new ConcurrentHashMap<Short, SpellResistance>();
        }
        return this.spellResistances.get(spellId);
    }
    
    public final void pollResistances(final Communicator comm) {
        if (this.spellResistances != null) {
            final SpellResistance[] array;
            final SpellResistance[] resisArr = array = this.spellResistances.values().toArray(new SpellResistance[this.spellResistances.size()]);
            for (final SpellResistance resist : array) {
                if (resist.tickSecond(comm)) {
                    this.spellResistances.remove(resist.getSpellType());
                }
            }
            if (this.spellResistances.isEmpty()) {
                this.spellResistances = null;
            }
        }
    }
    
    public final void clearSpellResistances(final Communicator communicator) {
        if (this.spellResistances != null) {
            for (final SpellResistance resist : this.spellResistances.values()) {
                resist.sendUpdateToClient(communicator, (byte)0);
            }
            this.spellResistances.clear();
        }
    }
    
    public final void sendSpellResistances(final Communicator communicator) {
        if (this.spellResistances != null) {
            for (final SpellResistance resist : this.spellResistances.values()) {
                resist.sendUpdateToClient(communicator, (byte)2);
            }
        }
    }
    
    public final void setArmourLimitingFactor(final float factor, final Communicator communicator, final boolean initializing) {
        float factorToUse = factor;
        if (this.favor >= 35.0f && this.faith >= 70.0f && this.deity != null && this.deity.number == 2) {
            float tempfactor = this.limitingArmourFactor;
            if (factorToUse == -0.15f) {
                tempfactor = 0.0f;
                if (tempfactor == this.limitingArmourFactor) {
                    return;
                }
                factorToUse = tempfactor;
            }
        }
        if (this.limitingArmourFactor == factorToUse && !initializing) {
            return;
        }
        this.limitingArmourFactor = factorToUse;
        communicator.sendRemoveSpellEffect(SpellEffectsEnum.ARMOUR_LIMIT_NONE);
        communicator.sendRemoveSpellEffect(SpellEffectsEnum.ARMOUR_LIMIT_LIGHT);
        communicator.sendRemoveSpellEffect(SpellEffectsEnum.ARMOUR_LIMIT_MEDIUM);
        communicator.sendRemoveSpellEffect(SpellEffectsEnum.ARMOUR_LIMIT_HEAVY);
        SpellEffectsEnum toSend = SpellEffectsEnum.ARMOUR_LIMIT_NONE;
        if (this.limitingArmourFactor == -0.3f) {
            toSend = SpellEffectsEnum.ARMOUR_LIMIT_HEAVY;
        }
        else if (this.limitingArmourFactor == -0.15f) {
            toSend = SpellEffectsEnum.ARMOUR_LIMIT_MEDIUM;
        }
        else if (this.limitingArmourFactor == 0.0f) {
            toSend = SpellEffectsEnum.ARMOUR_LIMIT_LIGHT;
        }
        if (toSend != null) {
            communicator.sendAddStatusEffect(toSend, 100000);
        }
    }
    
    public final float getArmourLimitingFactor() {
        return this.limitingArmourFactor;
    }
    
    public long getLastChangedPath() {
        return this.lastChangedPath;
    }
    
    public void setLastChangedPath(final long lastChangedPath) {
        this.lastChangedPath = lastChangedPath;
    }
    
    public final byte getSex() {
        return this.sex;
    }
    
    public final boolean isMale() {
        return this.sex == 0;
    }
    
    public final boolean isFemale() {
        return this.sex == 1;
    }
    
    public final boolean isOnlineHere() {
        return this.currentServer == Servers.localServer.id && Players.getInstance().getPlayerOrNull(this.wurmId) != null;
    }
    
    List<Route> getHighwayPath() {
        return this.highwayPath;
    }
    
    void setHighwayPath(final String newDestination, final List<Route> newPath) {
        this.highwayPath = newPath;
        if (newPath == null) {
            this.highwayPathDestination = "";
        }
        else {
            this.highwayPathDestination = newDestination;
        }
    }
    
    String getHighwayPathDestination() {
        return this.highwayPathDestination;
    }
    
    public SteamId getSteamId() {
        return this.steamId;
    }
    
    static {
        PlayerInfo.logger = Logger.getLogger(PlayerInfo.class.getName());
    }
}
