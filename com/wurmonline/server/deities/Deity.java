// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.deities;

import com.wurmonline.server.epic.EpicMission;
import com.wurmonline.server.epic.EpicServerStatus;
import com.wurmonline.server.kingdom.Kingdom;
import com.wurmonline.server.players.Player;
import com.wurmonline.server.kingdom.Kingdoms;
import java.util.HashMap;
import com.wurmonline.server.Players;
import java.sql.ResultSet;
import java.sql.PreparedStatement;
import java.sql.Connection;
import java.sql.Statement;
import com.wurmonline.server.utils.DbUtilities;
import java.sql.SQLException;
import com.wurmonline.server.DbConnector;
import java.util.Map;
import com.wurmonline.server.Servers;
import java.util.Iterator;
import java.util.Arrays;
import com.wurmonline.server.Server;
import java.io.IOException;
import java.util.logging.Level;
import com.wurmonline.server.creatures.Creature;
import com.wurmonline.server.behaviours.Action;
import java.util.HashSet;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import com.wurmonline.server.spells.Spell;
import java.util.Set;
import java.util.logging.Logger;
import com.wurmonline.server.MiscConstants;

public abstract class Deity implements MiscConstants
{
    public static final byte TYPE_MISSIONAIRY = 0;
    public static final byte TYPE_CHAPLAIN = 1;
    public final int number;
    public final String name;
    public int alignment;
    public final byte sex;
    byte power;
    double faith;
    int favor;
    float attack;
    float vitality;
    private static Logger logger;
    public final int holyItem;
    public String[] convertText1;
    public String[] altarConvertText1;
    private final Set<Spell> spells;
    private final Set<Spell> creatureSpells;
    private final Set<Spell> itemSpells;
    private final Set<Spell> woundSpells;
    private final Set<Spell> tileSpells;
    private static final String insertKarma = "INSERT INTO HELPERS (WURMID,KARMA,DEITY) VALUES (?,?,?)";
    private static final String updateKarma = "UPDATE HELPERS SET KARMA=?,DEITY=? WHERE WURMID=?";
    private static final String loadKarma = "SELECT * FROM HELPERS WHERE DEITY=?";
    private final ConcurrentHashMap<Long, Float> karmavals;
    private int templateDeity;
    private boolean roadProtector;
    private float buildWallBonus;
    private boolean warrior;
    private boolean befriendCreature;
    private boolean befriendMonster;
    private boolean staminaBonus;
    private boolean foodBonus;
    private boolean healer;
    private boolean deathProtector;
    private boolean deathItemProtector;
    private boolean favorRegenerator;
    private boolean allowsButchering;
    private boolean woodAffinity;
    private boolean metalAffinity;
    private boolean clothAffinity;
    private boolean clayAffinity;
    private boolean meatAffinity;
    private boolean foodAffinity;
    private boolean learner;
    private boolean itemProtector;
    private boolean repairer;
    private boolean waterGod;
    private boolean mountainGod;
    private boolean forestGod;
    private boolean hateGod;
    public int lastConfrontationTileX;
    public int lastConfrontationTileY;
    private int activeFollowers;
    private final Random rand;
    private byte favoredKingdom;
    
    Deity(final int num, final String nam, final byte align, final byte aSex, final byte pow, final double aFaith, final int holyitem, final int _favor, final float _attack, final float _vitality, final boolean create) {
        this.convertText1 = new String[10];
        this.altarConvertText1 = new String[10];
        this.spells = new HashSet<Spell>();
        this.creatureSpells = new HashSet<Spell>();
        this.itemSpells = new HashSet<Spell>();
        this.woundSpells = new HashSet<Spell>();
        this.tileSpells = new HashSet<Spell>();
        this.karmavals = new ConcurrentHashMap<Long, Float>();
        this.templateDeity = 0;
        this.roadProtector = false;
        this.buildWallBonus = 0.0f;
        this.warrior = false;
        this.befriendCreature = false;
        this.befriendMonster = false;
        this.staminaBonus = false;
        this.foodBonus = false;
        this.healer = false;
        this.deathProtector = false;
        this.deathItemProtector = false;
        this.favorRegenerator = false;
        this.allowsButchering = false;
        this.woodAffinity = false;
        this.metalAffinity = false;
        this.clothAffinity = false;
        this.clayAffinity = false;
        this.meatAffinity = false;
        this.foodAffinity = false;
        this.learner = false;
        this.itemProtector = false;
        this.repairer = false;
        this.waterGod = false;
        this.mountainGod = false;
        this.forestGod = false;
        this.hateGod = false;
        this.activeFollowers = 0;
        this.favoredKingdom = 0;
        this.number = num;
        this.name = nam;
        this.alignment = align;
        this.sex = aSex;
        this.power = pow;
        this.faith = aFaith;
        this.holyItem = holyitem;
        this.favor = _favor;
        this.attack = _attack;
        this.vitality = _vitality;
        this.rand = new Random(this.number * 1001);
    }
    
    public final String getHeSheItString() {
        if (this.sex == 0) {
            return "he";
        }
        if (this.sex == 1) {
            return "she";
        }
        return "it";
    }
    
    public final String getCapHeSheItString() {
        if (this.sex == 0) {
            return "He";
        }
        if (this.sex == 1) {
            return "She";
        }
        return "It";
    }
    
    public final String getHisHerItsString() {
        if (this.sex == 0) {
            return "his";
        }
        if (this.sex == 1) {
            return "her";
        }
        return "its";
    }
    
    public final String getHimHerItString() {
        if (this.sex == 0) {
            return "him";
        }
        if (this.sex == 1) {
            return "her";
        }
        return "it";
    }
    
    public final int getTemplateDeity() {
        return this.templateDeity;
    }
    
    public final void setTemplateDeity(final int templateDeity) {
        this.templateDeity = templateDeity;
    }
    
    public final boolean accepts(final float align) {
        return align >= this.alignment - 100 && align <= this.alignment + 100;
    }
    
    public final boolean isActionFaithful(final Action action) {
        final int num = action.getNumber();
        return this.isActionFaithful(num);
    }
    
    final boolean isActionFaithful(final int num) {
        if (num == 191) {
            return !this.roadProtector;
        }
        return (num != 174 && num != 172 && num != 524) || this.buildWallBonus <= 0.0f;
    }
    
    public final void punishCreature(final Creature performer, final int actionNumber) {
        float lPower = 0.0f;
        if (actionNumber == 191) {
            lPower = 0.05f;
        }
        else if (actionNumber == 174 || actionNumber == 172) {
            lPower = 0.05f;
        }
        else if (actionNumber == 221) {
            lPower = 0.5f;
        }
        if (lPower > 0.0f) {
            performer.modifyFaith(-lPower);
            try {
                performer.setFavor(performer.getFavor() - lPower);
            }
            catch (IOException iox) {
                Deity.logger.log(Level.WARNING, performer.getName() + " " + this.name, iox);
            }
        }
    }
    
    public final boolean performActionOkey(final Creature performer, final Action action) {
        if (!this.isActionFaithful(action) && Server.rand.nextInt(100) <= 10) {
            this.punishCreature(performer, action.getNumber());
            return false;
        }
        return true;
    }
    
    public final void removeSpell(final Spell spell) {
        this.spells.remove(spell);
        if (spell.isTargetCreature()) {
            this.creatureSpells.remove(spell);
        }
        if (spell.isTargetAnyItem()) {
            this.itemSpells.remove(spell);
        }
        if (spell.isTargetWound()) {
            this.woundSpells.remove(spell);
        }
        if (spell.isTargetTile()) {
            this.tileSpells.remove(spell);
        }
    }
    
    public final void addSpell(final Spell spell) {
        this.spells.add(spell);
        if (spell.isTargetCreature()) {
            this.creatureSpells.add(spell);
        }
        if (spell.isTargetAnyItem()) {
            this.itemSpells.add(spell);
        }
        if (spell.isTargetWound()) {
            this.woundSpells.add(spell);
        }
        if (spell.isTargetTile()) {
            this.tileSpells.add(spell);
        }
    }
    
    public final boolean hasSpell(final Spell spell) {
        return this.spells.contains(spell);
    }
    
    public final Set<Spell> getSpells() {
        return this.spells;
    }
    
    public final Spell[] getSpellsTargettingCreatures(final int level) {
        final Set<Spell> toReturn = new HashSet<Spell>();
        for (final Spell s : this.creatureSpells) {
            if (s.level <= level && (!s.isRitual || this.getFavor() > 100000)) {
                toReturn.add(s);
            }
        }
        final Spell[] spells = toReturn.toArray(new Spell[toReturn.size()]);
        Arrays.sort(spells);
        return spells;
    }
    
    public final Spell[] getSpellsTargettingWounds(final int level) {
        final Set<Spell> toReturn = new HashSet<Spell>();
        for (final Spell s : this.woundSpells) {
            if (s.level <= level && (!s.isRitual || this.getFavor() > 100000)) {
                toReturn.add(s);
            }
        }
        final Spell[] spells = toReturn.toArray(new Spell[toReturn.size()]);
        Arrays.sort(spells);
        return spells;
    }
    
    public final Spell[] getSpellsTargettingItems(final int level) {
        final Set<Spell> toReturn = new HashSet<Spell>();
        for (final Spell s : this.itemSpells) {
            if (s.level <= level && (!s.isRitual || this.getFavor() > 100000 || Servers.isThisATestServer())) {
                toReturn.add(s);
            }
        }
        final Spell[] spells = toReturn.toArray(new Spell[toReturn.size()]);
        Arrays.sort(spells);
        return spells;
    }
    
    public final Spell[] getSpellsTargettingTiles(final int level) {
        final Set<Spell> toReturn = new HashSet<Spell>();
        for (final Spell s : this.tileSpells) {
            if (s.level <= level && (!s.isRitual || this.getFavor() > 100000)) {
                toReturn.add(s);
            }
        }
        final Spell[] spells = toReturn.toArray(new Spell[toReturn.size()]);
        Arrays.sort(spells);
        return spells;
    }
    
    public int getFavor() {
        return this.favor;
    }
    
    public void increaseFavor() {
        this.setFavor(this.favor + 1);
    }
    
    abstract void save() throws IOException;
    
    abstract void setFaith(final double p0) throws IOException;
    
    public abstract void setFavor(final int p0);
    
    @Override
    public final String toString() {
        return "Deity [Name: " + this.name + ", Number: " + this.number + ']';
    }
    
    public boolean isLibila() {
        return this.number == 4;
    }
    
    public boolean isMagranon() {
        return this.number == 2;
    }
    
    public boolean isCustomDeity() {
        return this.number > 4;
    }
    
    public boolean isFo() {
        return this.number == 1;
    }
    
    public boolean isVynora() {
        return this.number == 3;
    }
    
    public void setActiveFollowers(final int followers) {
        this.activeFollowers = followers;
    }
    
    public int getActiveFollowers() {
        return this.activeFollowers;
    }
    
    public double getFaithPerFollower() {
        return this.faith / Math.max(1.0f, this.activeFollowers);
    }
    
    public int getNumber() {
        return this.number;
    }
    
    public String getName() {
        return this.name;
    }
    
    public int getAlignment() {
        return this.alignment;
    }
    
    public byte getSex() {
        return this.sex;
    }
    
    public abstract void setPower(final byte p0);
    
    byte getPower() {
        return this.power;
    }
    
    double getFaith() {
        return this.faith;
    }
    
    float getAttack() {
        return this.attack;
    }
    
    float getVitality() {
        return this.vitality;
    }
    
    public int getHolyItem() {
        return this.holyItem;
    }
    
    public String[] getConvertText1() {
        return this.convertText1;
    }
    
    public String[] getAltarConvertText1() {
        return this.altarConvertText1;
    }
    
    public boolean isRoadProtector() {
        return this.roadProtector;
    }
    
    public void setRoadProtector(final boolean roadProtector) {
        this.roadProtector = roadProtector;
    }
    
    public float getBuildWallBonus() {
        return this.buildWallBonus;
    }
    
    public void setBuildWallBonus(final float buildWallBonus) {
        this.buildWallBonus = buildWallBonus;
    }
    
    public boolean isWarrior() {
        return this.warrior;
    }
    
    public void setWarrior(final boolean warrior) {
        this.warrior = warrior;
    }
    
    public boolean isFavorRegenerator() {
        return this.favorRegenerator;
    }
    
    public void setFavorRegenerator(final boolean favorRegenerator) {
        this.favorRegenerator = favorRegenerator;
    }
    
    public boolean isBefriendCreature() {
        return this.befriendCreature;
    }
    
    public void setBefriendCreature(final boolean befriendCreature) {
        this.befriendCreature = befriendCreature;
    }
    
    public boolean isBefriendMonster() {
        return this.befriendMonster;
    }
    
    public void setBefriendMonster(final boolean befriendMonster) {
        this.befriendMonster = befriendMonster;
    }
    
    public boolean isStaminaBonus() {
        return this.staminaBonus;
    }
    
    public void setStaminaBonus(final boolean staminaBonus) {
        this.staminaBonus = staminaBonus;
    }
    
    public boolean isFoodBonus() {
        return this.foodBonus;
    }
    
    public void setFoodBonus(final boolean foodBonus) {
        this.foodBonus = foodBonus;
    }
    
    public boolean isHealer() {
        return this.healer;
    }
    
    public void setHealer(final boolean healer) {
        this.healer = healer;
    }
    
    public boolean isDeathProtector() {
        return this.deathProtector;
    }
    
    public void setDeathProtector(final boolean deathProtector) {
        this.deathProtector = deathProtector;
    }
    
    public boolean isDeathItemProtector() {
        return this.deathItemProtector;
    }
    
    public void setDeathItemProtector(final boolean deathItemProtector) {
        this.deathItemProtector = deathItemProtector;
    }
    
    public boolean isAllowsButchering() {
        return this.allowsButchering;
    }
    
    public void setAllowsButchering(final boolean allowsButchering) {
        this.allowsButchering = allowsButchering;
    }
    
    public boolean isWoodAffinity() {
        return this.woodAffinity;
    }
    
    public void setWoodAffinity(final boolean woodAffinity) {
        this.woodAffinity = woodAffinity;
    }
    
    public boolean isMetalAffinity() {
        return this.metalAffinity;
    }
    
    public void setMetalAffinity(final boolean metalAffinity) {
        this.metalAffinity = metalAffinity;
    }
    
    public boolean isClothAffinity() {
        return this.clothAffinity;
    }
    
    public void setClothAffinity(final boolean clothAffinity) {
        this.clothAffinity = clothAffinity;
    }
    
    public boolean isClayAffinity() {
        return this.clayAffinity;
    }
    
    public void setClayAffinity(final boolean clayAffinity) {
        this.clayAffinity = clayAffinity;
    }
    
    public boolean isMeatAffinity() {
        return this.meatAffinity;
    }
    
    public void setMeatAffinity(final boolean meatAffinity) {
        this.meatAffinity = meatAffinity;
    }
    
    public boolean isFoodAffinity() {
        return this.foodAffinity;
    }
    
    public void setFoodAffinity(final boolean foodAffinity) {
        this.foodAffinity = foodAffinity;
    }
    
    public boolean isLearner() {
        return this.learner;
    }
    
    public void setLearner(final boolean learner) {
        this.learner = learner;
    }
    
    public boolean isItemProtector() {
        return this.itemProtector;
    }
    
    public void setItemProtector(final boolean itemProtector) {
        this.itemProtector = itemProtector;
    }
    
    public boolean isRepairer() {
        return this.repairer;
    }
    
    public void setRepairer(final boolean repairer) {
        this.repairer = repairer;
    }
    
    public boolean isWaterGod() {
        return this.waterGod;
    }
    
    public void setWaterGod(final boolean waterGod) {
        this.waterGod = waterGod;
    }
    
    public boolean isMountainGod() {
        return this.mountainGod;
    }
    
    public void setMountainGod(final boolean mountainGod) {
        this.mountainGod = mountainGod;
    }
    
    public boolean isForestGod() {
        return this.forestGod;
    }
    
    public void setForestGod(final boolean forestGod) {
        this.forestGod = forestGod;
    }
    
    public boolean isHateGod() {
        return this.hateGod;
    }
    
    public void setHateGod(final boolean hateGod) {
        this.hateGod = hateGod;
    }
    
    public int getLastConfrontationTileX() {
        return this.lastConfrontationTileX;
    }
    
    public int getLastConfrontationTileY() {
        return this.lastConfrontationTileY;
    }
    
    public final void clearKarma() {
        this.karmavals.clear();
    }
    
    public final ConcurrentHashMap<Long, Float> getHelpers() {
        return this.karmavals;
    }
    
    public final long getBestHelper(final boolean wipeKarma) {
        int totalTickets = 0;
        for (final float i : this.karmavals.values()) {
            if (i >= 300.0f) {
                totalTickets += (int)(i / 300.0f);
            }
        }
        int currentTicket = 0;
        final long[] tickets = new long[totalTickets];
        for (final Map.Entry<Long, Float> entry : this.karmavals.entrySet()) {
            if (entry.getValue() >= 300.0f) {
                for (int totalNum = (int)(entry.getValue() / 300.0f), j = 0; j < totalNum; ++j) {
                    tickets[currentTicket++] = entry.getKey();
                }
            }
        }
        final int winningTicket = Server.rand.nextInt((totalTickets < 1) ? 1 : totalTickets);
        if (winningTicket < tickets.length) {
            if (wipeKarma) {
                this.karmavals.replace(tickets[winningTicket], 0.0f);
            }
            return tickets[winningTicket];
        }
        return -10L;
    }
    
    public final void loadAllKarmaHelpers() {
        Connection dbcon = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            dbcon = DbConnector.getDeityDbCon();
            ps = dbcon.prepareStatement("SELECT * FROM HELPERS WHERE DEITY=?");
            ps.setInt(1, this.number);
            rs = ps.executeQuery();
            while (rs.next()) {
                this.karmavals.put(rs.getLong("WURMID"), (float)rs.getInt("KARMA"));
            }
            ps.close();
        }
        catch (SQLException sqx) {
            Deity.logger.log(Level.WARNING, sqx.getMessage(), sqx);
        }
        finally {
            DbUtilities.closeDatabaseObjects(ps, rs);
            DbConnector.returnConnection(dbcon);
        }
    }
    
    public final void setPlayerKarma(final long pid, final int value) {
        final Long playerId = pid;
        final Float karmaValue = (Float)value;
        Connection dbcon = null;
        PreparedStatement ps = null;
        if (this.karmavals.keySet().contains(playerId)) {
            this.karmavals.put(playerId, karmaValue);
            try {
                dbcon = DbConnector.getDeityDbCon();
                ps = dbcon.prepareStatement("UPDATE HELPERS SET KARMA=?,DEITY=? WHERE WURMID=?");
                ps.setInt(1, value);
                ps.setInt(2, this.number);
                ps.setLong(3, pid);
                ps.executeUpdate();
                ps.close();
            }
            catch (SQLException sqx) {
                Deity.logger.log(Level.WARNING, sqx.getMessage(), sqx);
            }
            finally {
                DbUtilities.closeDatabaseObjects(ps, null);
                DbConnector.returnConnection(dbcon);
            }
        }
        else {
            this.karmavals.put(playerId, karmaValue);
            try {
                dbcon = DbConnector.getDeityDbCon();
                ps = dbcon.prepareStatement("INSERT INTO HELPERS (WURMID,KARMA,DEITY) VALUES (?,?,?)");
                ps.setLong(1, pid);
                ps.setInt(2, value);
                ps.setInt(3, this.number);
                ps.executeUpdate();
                ps.close();
            }
            catch (SQLException sqx) {
                Deity.logger.log(Level.WARNING, sqx.getMessage(), sqx);
            }
            finally {
                DbUtilities.closeDatabaseObjects(ps, null);
                DbConnector.returnConnection(dbcon);
            }
        }
    }
    
    public Random initializeAndGetRand() {
        this.rand.setSeed(this.number * 1001);
        return this.rand;
    }
    
    public Random getRand() {
        return this.rand;
    }
    
    public final void setMaxKingdom() {
        final Player[] players = Players.getInstance().getPlayers();
        if (players.length > 10 || Servers.localServer.testServer) {
            final Map<Byte, Integer> maxWorshippers = new HashMap<Byte, Integer>();
            for (final Player lPlayer : players) {
                if (lPlayer.isPaying() && lPlayer.getDeity() != null && lPlayer.getDeity().number == this.getNumber()) {
                    Integer curr = maxWorshippers.get(lPlayer.getKingdomId());
                    if (curr == null) {
                        curr = 1;
                    }
                    else {
                        ++curr;
                    }
                    maxWorshippers.put(lPlayer.getKingdomId(), curr);
                }
            }
            byte maxKingdom = 0;
            int maxNums = 0;
            for (final Map.Entry<Byte, Integer> me : maxWorshippers.entrySet()) {
                final int nums = me.getValue();
                if (nums > maxNums) {
                    maxNums = nums;
                    maxKingdom = me.getKey();
                }
            }
            final Kingdom k = Kingdoms.getKingdom(maxKingdom);
            if (k != null) {
                this.setFavoredKingdom(k.getTemplate());
            }
            this.setFavoredKingdom(maxKingdom);
        }
    }
    
    public byte getFavoredKingdom() {
        return this.favoredKingdom;
    }
    
    public void setFavoredKingdom(final byte fKingdom) {
        if (fKingdom != this.favoredKingdom) {
            final EpicMission mission = EpicServerStatus.getEpicMissionForEntity(this.getNumber());
            if (mission != null) {
                Players.getInstance().sendUpdateEpicMission(mission);
            }
        }
        this.favoredKingdom = fKingdom;
    }
    
    static {
        Deity.logger = Logger.getLogger(Deity.class.getName());
    }
}
