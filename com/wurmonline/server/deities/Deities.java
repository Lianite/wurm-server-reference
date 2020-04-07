// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.deities;

import java.util.List;
import java.util.ArrayList;
import com.wurmonline.server.players.PlayerInfoFactory;
import com.wurmonline.server.Server;
import java.util.LinkedList;
import com.wurmonline.server.WurmCalendar;
import com.wurmonline.server.kingdom.Kingdom;
import java.sql.ResultSet;
import java.sql.PreparedStatement;
import java.sql.Connection;
import java.sql.Statement;
import com.wurmonline.server.utils.DbUtilities;
import java.sql.SQLException;
import java.io.IOException;
import java.util.logging.Level;
import com.wurmonline.server.kingdom.Kingdoms;
import java.util.HashMap;
import com.wurmonline.server.DbConnector;
import java.util.Iterator;
import java.util.Random;
import java.util.logging.Logger;
import java.util.Map;
import com.wurmonline.server.TimeConstants;
import com.wurmonline.server.MiscConstants;

public final class Deities implements MiscConstants, TimeConstants
{
    private static final Map<Integer, Deity> deities;
    private static final String LOAD_DEITIES = "SELECT * FROM DEITIES";
    private static final String CALCULATE_FAITHS = "SELECT DEITY,FAITH, KINGDOM, PAYMENTEXPIRE FROM PLAYERS WHERE POWER=0 AND LASTLOGOUT>?";
    private static final Map<String, Integer> valreiStatuses;
    private static final Map<Integer, Integer> valreiPositions;
    private static final Map<Integer, String> valreiNames;
    private static Logger logger;
    public static final int DEITY_NONE = 0;
    public static final int DEITY_FO = 1;
    public static final int DEITY_MAGRANON = 2;
    public static final int DEITY_VYNORA = 3;
    public static final int DEITY_LIBILA = 4;
    public static final int DEITY_WURM = 5;
    public static final int DEITY_NOGUMP = 6;
    public static final int DEITY_WALNUT = 7;
    public static final int DEITY_PHARMAKOS = 8;
    public static final int DEITY_JACKAL = 9;
    public static final int DEITY_DEATHCRAWLER = 10;
    public static final int DEITY_SCAVENGER = 11;
    public static final int DEITY_GIANT = 12;
    public static final int DEITY_RESERVED = 100;
    public static final int DEITY_TOSIEK = 31;
    public static final int DEITY_NAHJO = 32;
    public static final int DEITY_NATHAN = 33;
    public static final int DEITY_PAAWEELR = 34;
    public static final int DEITY_SMEAGAIN = 35;
    public static final int DEITY_GARY = 36;
    public static int maxDeityNum;
    public static final float THEFTMOD = -0.25f;
    public static float faithPlayers;
    public static final int FAVORNEEDEDFORRITUALS = 100000;
    
    public static final boolean isOkOnFreedom(final int deityNum, final byte kingdomId) {
        return kingdomId == 4 && (deityNum == 1 || deityNum == 3 || deityNum == 2);
    }
    
    public static final byte getFavoredKingdom(final int deityNum) {
        if (deityNum == 1 || deityNum == 3) {
            return 1;
        }
        if (deityNum == 2) {
            return 2;
        }
        if (deityNum == 4) {
            return 3;
        }
        final Deity d = getDeity(deityNum);
        if (d != null) {
            return d.getFavoredKingdom();
        }
        return 0;
    }
    
    private static void rollBefriendPassive(final Deity deity, final Random rand) {
        if (deity.isHateGod()) {
            deity.setBefriendMonster(rand.nextInt(2) == 0);
        }
        else if (deity.isForestGod()) {
            deity.setBefriendCreature(rand.nextInt(2) == 0);
        }
        else {
            deity.setBefriendCreature(rand.nextInt(5) == 0);
            if (!deity.isBefriendCreature()) {
                deity.setBefriendMonster(rand.nextInt(10) == 0);
            }
        }
    }
    
    private static void rollWarriorPassive(final Deity deity, final Random rand) {
        if (deity.isMountainGod()) {
            deity.setWarrior(rand.nextInt(2) == 0);
        }
        else if (deity.isHateGod()) {
            deity.setDeathItemProtector(rand.nextInt(2) == 0);
        }
        if (deity.isWarrior() || deity.isDeathItemProtector()) {
            return;
        }
        deity.setWarrior(rand.nextInt(4) == 0);
        if (!deity.isWarrior()) {
            deity.setDeathItemProtector(rand.nextInt(4) == 0);
            if (!deity.isDeathItemProtector()) {
                deity.setDeathProtector(rand.nextInt(3) == 0);
            }
        }
        if (deity.isWarrior() || deity.isDeathItemProtector() || deity.isDeathProtector()) {
            return;
        }
        deity.setDeathProtector(true);
    }
    
    private static void rollHealingLearningPassive(final Deity deity, final Random rand) {
        if (deity.isWarrior() || deity.isDeathItemProtector()) {
            deity.setLearner(rand.nextInt(3) == 0);
            return;
        }
        if (!deity.isHateGod()) {
            if (deity.isForestGod()) {
                deity.setHealer(rand.nextInt(3) > 0);
            }
            else {
                deity.setHealer(rand.nextInt(3) == 0);
            }
            if (deity.isHealer()) {
                return;
            }
        }
        deity.setLearner(true);
    }
    
    private static void initializeDemigodPassives(final Deity deity) {
        final Random rand = deity.initializeAndGetRand();
        int template = 1 + rand.nextInt(4);
        if (deity.getNumber() == 31 || deity.getNumber() == 33 || deity.getNumber() == 36) {
            template = 4;
        }
        if (deity.getNumber() == 32) {
            template = 1;
        }
        if (deity.getNumber() == 34) {
            template = 3;
        }
        if (deity.getNumber() == 35) {
            template = 2;
        }
        deity.setTemplateDeity(template);
        if (template == 1) {
            deity.setForestGod(true);
            deity.setClothAffinity(true);
        }
        else if (template == 2) {
            deity.setMountainGod(true);
            deity.setMetalAffinity(true);
        }
        else if (template == 3) {
            deity.setWaterGod(true);
            deity.setClayAffinity(true);
        }
        else if (template == 4) {
            deity.setHateGod(true);
            deity.setMeatAffinity(true);
        }
        rollBefriendPassive(deity, rand);
        rollWarriorPassive(deity, rand);
        rollHealingLearningPassive(deity, rand);
        if (deity.isHateGod()) {
            deity.setAllowsButchering(true);
            deity.setFavorRegenerator(rand.nextInt(2) == 0);
        }
        else {
            deity.setAllowsButchering(rand.nextInt(5) == 0);
            deity.setFavorRegenerator(rand.nextInt(4) == 0);
        }
        if (deity.isForestGod()) {
            deity.setFoodAffinity(rand.nextInt(2) == 0);
            deity.setStaminaBonus(rand.nextInt(2) == 0);
            deity.setFoodBonus(rand.nextInt(2) == 0);
        }
        else {
            deity.setFoodAffinity(rand.nextInt(4) == 0);
            deity.setStaminaBonus(rand.nextInt(4) == 0);
            deity.setFoodBonus(rand.nextInt(4) == 0);
        }
        if (deity.isWaterGod()) {
            deity.setRoadProtector(rand.nextInt(2) == 0);
            deity.setItemProtector(rand.nextInt(2) == 0);
            deity.setRepairer(rand.nextInt(2) == 0);
            deity.setBuildWallBonus(rand.nextInt(10));
            deity.setWoodAffinity(rand.nextInt(2) == 0);
        }
        else {
            deity.setRoadProtector(rand.nextInt(4) == 0);
            deity.setItemProtector(rand.nextInt(4) == 0);
            deity.setRepairer(rand.nextInt(4) == 0);
            deity.setBuildWallBonus(5 - rand.nextInt(10));
            deity.setWoodAffinity(rand.nextInt(4) == 0);
        }
        if (deity.isHealer()) {
            deity.alignment = 100;
        }
        else if (deity.isHateGod()) {
            deity.alignment = -100;
        }
        if (deity.getNumber() == 35) {
            deity.setDeathItemProtector(true);
        }
        if (deity.getNumber() == 32) {
            deity.setMeatAffinity(true);
        }
        createHumanConvertStrings(deity);
    }
    
    private static void addDeity(final Deity deity) {
        if (deity.number == 1) {
            deity.setTemplateDeity(1);
            deity.setForestGod(true);
            deity.setClothAffinity(true);
            deity.setFoodAffinity(true);
            deity.setBefriendCreature(true);
            deity.setStaminaBonus(true);
            deity.setFoodBonus(true);
            deity.setHealer(true);
            createFoConvertStrings(deity);
        }
        else if (deity.number == 2) {
            deity.setTemplateDeity(2);
            deity.setMountainGod(true);
            deity.setMetalAffinity(true);
            deity.setWarrior(true);
            deity.setDeathProtector(true);
            deity.setDeathItemProtector(true);
            createMagranonConvertStrings(deity);
        }
        else if (deity.number == 3) {
            deity.setTemplateDeity(3);
            deity.setWaterGod(true);
            deity.setClayAffinity(true);
            deity.setWoodAffinity(true);
            deity.setRoadProtector(true);
            deity.setItemProtector(true);
            deity.setRepairer(true);
            deity.setLearner(true);
            deity.setBuildWallBonus(20.0f);
            createVynoraConvertStrings(deity);
        }
        else if (deity.number == 4) {
            deity.setTemplateDeity(4);
            deity.setHateGod(true);
            deity.setMeatAffinity(true);
            deity.setFavorRegenerator(true);
            deity.setBefriendMonster(true);
            deity.setDeathProtector(true);
            deity.setAllowsButchering(true);
            createLibilaConvertStrings(deity);
        }
        else if (deity.isCustomDeity()) {
            initializeDemigodPassives(deity);
            createHumanConvertStrings(deity);
        }
        else {
            final Random rand = deity.initializeAndGetRand();
            deity.setMeatAffinity(rand.nextInt(3) == 0);
            deity.setHateGod(rand.nextInt(3) == 0 || deity.alignment < 0);
            deity.setAllowsButchering(rand.nextInt(3) == 0 || deity.isHateGod());
            deity.setRoadProtector(rand.nextInt(3) == 0);
            deity.setItemProtector(rand.nextInt(3) == 0);
            deity.setWarrior(rand.nextInt(3) == 0);
            deity.setDeathProtector(rand.nextInt(3) == 0);
            deity.setDeathItemProtector(rand.nextInt(3) == 0);
            deity.setMetalAffinity(rand.nextInt(3) == 0);
            deity.setMountainGod(rand.nextInt(3) == 0 || deity.number == 32);
            deity.setRepairer(rand.nextInt(3) == 0);
            deity.setLearner(rand.nextInt(3) == 0);
            deity.setBuildWallBonus(rand.nextInt(10));
            deity.setWoodAffinity(rand.nextInt(3) == 0);
            deity.setWaterGod(rand.nextInt(3) == 0 || deity.number == 32);
            deity.setBefriendCreature(rand.nextInt(3) == 0);
            deity.setStaminaBonus(rand.nextInt(3) == 0);
            deity.setFoodBonus(rand.nextInt(3) == 0);
            if (!deity.isHateGod()) {
                deity.setHealer(rand.nextInt(3) == 0);
            }
            deity.setClayAffinity(rand.nextInt(3) == 0);
            deity.setClothAffinity(rand.nextInt(3) == 0);
            deity.setFoodAffinity(rand.nextInt(3) == 0);
            if (!deity.isHateGod() && !deity.isWaterGod() && !deity.isMountainGod()) {
                deity.setForestGod(rand.nextInt(8) < 2);
            }
            if (deity.isHealer()) {
                deity.alignment = 100;
            }
            else if (deity.isHateGod()) {
                deity.alignment = -100;
            }
            createHumanConvertStrings(deity);
        }
        Deities.deities.put(deity.number, deity);
    }
    
    static void removeDeity(final int number) {
        Deities.deities.remove(number);
    }
    
    public static Deity getDeity(final int number) {
        return Deities.deities.get(number);
    }
    
    private static void resetDeityFollowers() {
        for (final Deity d : Deities.deities.values()) {
            d.setActiveFollowers(0);
        }
    }
    
    public static Deity[] getDeities() {
        return Deities.deities.values().toArray(new Deity[Deities.deities.size()]);
    }
    
    public static Map<Integer, String> getEntities() {
        return Deities.valreiNames;
    }
    
    public static final int getEntityNumber(final String name) {
        for (final Map.Entry<Integer, String> entry : Deities.valreiNames.entrySet()) {
            if (entry.getValue().equalsIgnoreCase(name)) {
                return entry.getKey();
            }
        }
        return -1;
    }
    
    public static final Deity translateDeityForEntity(final int entityNumber) {
        final String entityName = Deities.valreiNames.get(entityNumber);
        if (entityName != null) {
            for (final Deity d : Deities.deities.values()) {
                if (d.getName().equalsIgnoreCase(entityName)) {
                    return d;
                }
            }
        }
        return null;
    }
    
    public static final int translateEntityForDeity(final int deityNumber) {
        final Deity d = getDeity(deityNumber);
        if (d != null) {
            for (final Map.Entry<Integer, String> entry : Deities.valreiNames.entrySet()) {
                if (entry.getValue().equalsIgnoreCase(d.getName())) {
                    return entry.getKey();
                }
            }
        }
        return -1;
    }
    
    public static final void addEntity(final int number, final String name) {
        Deities.valreiNames.put(number, name);
    }
    
    public static final boolean isNameOkay(final String aName) {
        final String lName = aName.toLowerCase();
        for (final Deity d : Deities.deities.values()) {
            if (d.getNumber() < 100 && lName.equals(d.name.toLowerCase())) {
                return false;
            }
        }
        return !lName.equals("jackal") && !lName.equals("valrej") && !lName.equals("valrei") && !lName.equals("seris") && !lName.equals("sol") && !lName.equals("upkeep") && !lName.equals("system") && !lName.equals("village") && !lName.equals("team") && !lName.equals("local") && !lName.equals("combat") && !lName.equals("friends") && !lName.equals("nogump") && !lName.equals("uttacha") && !lName.equals("pharmakos") && !lName.equals("walnut");
    }
    
    public static final void calculateFaiths() {
        Connection dbcon = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        resetDeityFollowers();
        try {
            dbcon = DbConnector.getPlayerDbCon();
            ps = dbcon.prepareStatement("SELECT DEITY,FAITH, KINGDOM, PAYMENTEXPIRE FROM PLAYERS WHERE POWER=0 AND LASTLOGOUT>?");
            ps.setLong(1, System.currentTimeMillis() - 604800000L);
            rs = ps.executeQuery();
            Deities.faithPlayers = 0.0f;
            final Map<Integer, Float> faithMap = new HashMap<Integer, Float>();
            Kingdoms.activePremiumHots = 0;
            Kingdoms.activePremiumJenn = 0;
            Kingdoms.activePremiumMolr = 0;
            while (rs.next()) {
                final byte d = rs.getByte("DEITY");
                if (d > 0) {
                    final float faith = rs.getFloat("FAITH");
                    Float f = faithMap.get((int)d);
                    if (f == null) {
                        f = new Float(faith);
                        faithMap.put((int)d, f);
                    }
                    else {
                        f = new Float(f + faith);
                    }
                    final Deity deity = Deities.deities.get((int)d);
                    if (deity != null) {
                        deity.setActiveFollowers(deity.getActiveFollowers() + 1);
                    }
                    ++Deities.faithPlayers;
                }
                final byte kdom = rs.getByte("KINGDOM");
                if (kdom == 1) {
                    ++Kingdoms.activePremiumJenn;
                }
                else if (kdom == 3) {
                    ++Kingdoms.activePremiumHots;
                }
                else if (kdom == 2) {
                    ++Kingdoms.activePremiumMolr;
                }
                final Kingdom kingdom = Kingdoms.getKingdom(kdom);
                ++kingdom.activePremiums;
                Kingdoms.getKingdom(kdom).countedAtleastOnce = true;
            }
            try {
                for (final Map.Entry<Integer, Float> me : faithMap.entrySet()) {
                    final Deity deity2 = getDeity(me.getKey());
                    if (deity2 != null && me.getValue() > 0.0f) {
                        deity2.setFaith(me.getValue() / Deities.faithPlayers);
                        if (deity2.getNumber() >= 0 && deity2.getNumber() <= 4) {
                            continue;
                        }
                        deity2.setMaxKingdom();
                    }
                    else {
                        if (deity2 == null) {
                            continue;
                        }
                        deity2.setFaith(0.0);
                    }
                }
            }
            catch (IOException iox) {
                Deities.logger.log(Level.WARNING, iox.getMessage(), iox);
            }
        }
        catch (SQLException sqx) {
            Deities.logger.log(Level.WARNING, sqx.getMessage(), sqx);
        }
        finally {
            DbConnector.returnConnection(dbcon);
            DbUtilities.closeDatabaseObjects(ps, rs);
        }
        Kingdoms.checkIfDisbandKingdom();
    }
    
    public static boolean mayDestroyAltars() {
        return WurmCalendar.mayDestroyHugeAltars();
    }
    
    private static final void loadDeities() throws IOException {
        Connection dbcon = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            dbcon = DbConnector.getDeityDbCon();
            ps = dbcon.prepareStatement("SELECT * FROM DEITIES");
            rs = ps.executeQuery();
            int found = 0;
            while (rs.next()) {
                final int number = rs.getByte("ID");
                if (number > Deities.maxDeityNum) {
                    Deities.maxDeityNum = number;
                }
                final String name = rs.getString("NAME");
                final byte sex = rs.getByte("SEX");
                final double faith = rs.getDouble("FAITH");
                final int favor = rs.getInt("FAVOR");
                final byte alignment = rs.getByte("ALIGNMENT");
                final byte power = rs.getByte("POWER");
                final int holyitem = rs.getInt("HOLYITEM");
                final float attack = rs.getFloat("ATTACK");
                final float vitality = rs.getFloat("VITALITY");
                final DbDeity deity = new DbDeity(number, name, alignment, sex, power, faith, holyitem, favor, attack, vitality, false);
                addDeity(deity);
                if (number == 1 || number == 3) {
                    deity.setFavoredKingdom((byte)1);
                }
                else if (number == 2) {
                    deity.setFavoredKingdom((byte)2);
                }
                else if (number == 4) {
                    deity.setFavoredKingdom((byte)3);
                }
                ++found;
            }
            if (found == 0) {
                createBasicDeities();
            }
        }
        catch (SQLException sqx) {
            Deities.logger.log(Level.WARNING, sqx.getMessage(), sqx);
            throw new IOException(sqx);
        }
        finally {
            DbUtilities.closeDatabaseObjects(ps, rs);
            DbConnector.returnConnection(dbcon);
        }
    }
    
    public static final int getNextDeityNum() {
        return ++Deities.maxDeityNum;
    }
    
    public static final Deity ascend(final int newid, final String name, final long wurmid, final byte sex, final byte power, final float attack, final float vitality) {
        if (newid > Deities.maxDeityNum) {
            Deities.maxDeityNum = newid;
        }
        final Random rand = new Random(wurmid);
        final DbDeity deity = new DbDeity(newid, name, (byte)0, sex, power, 0.0, 505 + rand.nextInt(4), 0, attack, vitality, true);
        addDeity(deity);
        try {
            deity.save();
        }
        catch (IOException iox) {
            Deities.logger.log(Level.WARNING, "Failed to save " + deity.name, iox);
            return null;
        }
        return deity;
    }
    
    public static final Deity getRandomHateDeity() {
        boolean exists = false;
        for (final Deity d : Deities.deities.values()) {
            if (d.isHateGod() && d.number != 4) {
                exists = true;
            }
        }
        if (exists) {
            final LinkedList<Deity> toRet = new LinkedList<Deity>();
            for (final Deity d2 : Deities.deities.values()) {
                if (d2.isHateGod() && d2.number != 4) {
                    toRet.add(d2);
                }
            }
            return toRet.get(Server.rand.nextInt(toRet.size()));
        }
        return null;
    }
    
    public static final Deity getRandomNonHateDeity() {
        boolean exists = false;
        for (final Deity d : Deities.deities.values()) {
            if (!d.isHateGod() && d.number > 100) {
                exists = true;
            }
        }
        if (exists) {
            final LinkedList<Deity> toRet = new LinkedList<Deity>();
            for (final Deity d2 : Deities.deities.values()) {
                if (!d2.isHateGod() && d2.number > 100) {
                    toRet.add(d2);
                }
            }
            return toRet.get(Server.rand.nextInt(toRet.size()));
        }
        return null;
    }
    
    private static final void createBasicDeities() {
        DbDeity deity = new DbDeity(1, "Fo", (byte)100, (byte)0, (byte)5, 0.0, 505, 0, 7.0f, 5.0f, true);
        addDeity(deity);
        deity.setFavoredKingdom((byte)1);
        try {
            deity.save();
        }
        catch (IOException iox) {
            Deities.logger.log(Level.WARNING, "Failed to save " + deity.name, iox);
        }
        deity = new DbDeity(2, "Magranon", (byte)70, (byte)0, (byte)4, 0.0, 507, 0, 6.0f, 6.0f, true);
        addDeity(deity);
        deity.setFavoredKingdom((byte)2);
        try {
            deity.save();
        }
        catch (IOException iox) {
            Deities.logger.log(Level.WARNING, "Failed to save " + deity.name, iox);
        }
        deity = new DbDeity(3, "Vynora", (byte)70, (byte)1, (byte)4, 0.0, 508, 0, 5.0f, 7.0f, true);
        addDeity(deity);
        deity.setFavoredKingdom((byte)1);
        try {
            deity.save();
        }
        catch (IOException iox) {
            Deities.logger.log(Level.WARNING, "Failed to save " + deity.name, iox);
        }
        deity = new DbDeity(4, "Libila", (byte)(-100), (byte)1, (byte)4, 0.0, 506, 0, 6.0f, 6.0f, true);
        addDeity(deity);
        deity.setFavoredKingdom((byte)3);
        try {
            deity.save();
        }
        catch (IOException iox) {
            Deities.logger.log(Level.WARNING, "Failed to save " + deity.name, iox);
        }
    }
    
    private static void createHumanConvertStrings(final Deity deity) {
        final String[] conv1 = { deity.name + " is here to grant strength and guidance.", "We are all seekers here in these foreign lands,", "We are all threatened by the ancient powers,", "And it is easy to stumble in this darkness never to rise again.", deity.getCapHeSheItString() + " will lead you through the marshes and caverns, and " + deity.getHeSheItString() + " will hold your hand when you falter.", deity.isHealer() ? ("Follow " + deity.getHimHerItString() + " and " + deity.getHeSheItString() + " will keep you safe and healthy.") : ("Trust " + deity.getHimHerItString() + " in the darkness and sorrow."), deity.isHateGod() ? "Together we will crush our enemies and rule here in eternity." : "There is a blessed land we want to show you.", deity.isHateGod() ? ("Listen carefully and you will hear the thunder of " + deity.getHisHerItsString() + " armies!") : "Join us in freedom and follow us in peaceful bliss!", "The path leads on to victory and a new dawn for humankind.", deity.isHateGod() ? "Will you join us or be crushed like a flea?" : "Will you join us?" };
        final String[] alt1 = { deity.isHateGod() ? "Mortal," : "Dear friend,", "I have transcended to a higher state of being.", "I am now among the Immortals.", "I have become the epitome of " + (deity.isHateGod() ? "darkness." : (deity.isHealer() ? "love." : "light.")), "Trust me when I promise you a path to strength and glory.", deity.isHateGod() ? "I will lead the way and you will follow." : "I will walk beside you in eternal friendship.", deity.isHateGod() ? "Together we will slay our enemies in the sleep and tear their children apart." : "I will support you when you stagger, and keep your children safe.", "Nothing will stop us and one day we will meet on the Western Spurs and drink " + (deity.isHateGod() ? "our enemies' blood." : "honey and wine!"), "Let us grow together and conquer the forbidden lands where our souls rule in eternity!", "Are you ready to join us?" };
        deity.convertText1 = conv1;
        deity.altarConvertText1 = alt1;
    }
    
    private static void createFoConvertStrings(final Deity fo) {
        final String[] conv1 = { "Fo!", "His creations surround you. His love is everywhere around you.", "He is the father of all things. He created the world out of love and passion.", "To embrace Fo is to embrace all living things around you.", "All and everyone is equal, but different.", "We are all dirt. We are all gems. We just come in different shapes and colors.", "To create more and love all that is already created is to love Fo.", "To passionately strike down at those who aim to destroy these creations is to love Fo.", "To strive after beauty and harmony with nature is to love Fo.", "If you love Fo, Fo loves you!" };
        final String[] alt1 = { "I am Fo.", "I am the Silence and the Trees. The sprout is my symbol.", "Silent and lonely I lingered in darkness.", "Look around you. I created this of love and loneliness.", "You are all like, but different.", "You are all dirt. You are all gems. You just come in different shapes and colors.", "Do my bidding. Let all things grow into the splendor they may possess.", "Strive after beauty and harmony with nature. Let your soul become a lustrous gem.", "With the same passion by which I once created all this, strike down at those who aim to destroy these creations.", "Love me. Let me love you." };
        fo.convertText1 = conv1;
        fo.altarConvertText1 = alt1;
    }
    
    private static void createMagranonConvertStrings(final Deity magranon) {
        final String[] conv1 = { "Is your goal in life to achieve riches? To achieve freedom?", "Who is stopping you? You are. Who will help you? Magranon will!", "We, the followers of Magranon will stand at the top of the world one day and sing!", "Together we will strive to rule the world. We will conquer all evil, build fantastic houses and live rich and glorious lives in them.", "What is knowledge for if you do not use it? What use is compassion if you are hungry?", "What are the alternatives?", "Say yes to yourself! Say 'I will!' Your world will change, and you will change the world!", "There are obstacles. People and forces will oppose us. Who will want to deny us all we strive for.", "That force must be utterly defeated! No victory will be possible unless we cleanse the world of that evil.", "Join our ranks. Help yourself reach the top!" };
        final String[] alt1 = { "Listen to the words of Magranon:", "What are you? Could you not be more?", "I am the Fire and the Mountain. A sword is my symbol.", "I will help you rule the world. You will conquer all evil, and live a rich and glorious life.", "One day you will stand at the top of the world and sing!", "What is knowledge for if you do not use it? What use is compassion if you are hungry?", "Paths leading endlessly into the mist! What matters is power!", "First, power over self. Then, power over others.", "Say yes to yourself! Say 'I will!' Your world will change, and you will change the world!", "Let me help. Together, nothing can stop us!" };
        magranon.convertText1 = conv1;
        magranon.altarConvertText1 = alt1;
    }
    
    private static void createVynoraConvertStrings(final Deity vynora) {
        final String[] conv1 = { "What is this?", "Have you ever asked yourself that question?", "Vynora, our godess and guide, will help you seek the answer to that ancient riddle, just as we help her in her quest to know everything.", "Many secrets has she gathered, and we who call ourselves Seekers will be the first ones to learn.", "What is a Man? What is a Woman? What lies in darkness of the Void? Questions need answers!", "Seekers strive after excellence. We seek the truth in all things. We will go anywhere in our attempts to find it.", "True knowledge also brings us power. Power over self, but also power over others.", "Our gathered experience tells us that the best for all is to use that knowledge with care.", "Therefore most of us are peaceful and strive after a calm and orderly way to gather knowledge.", "Welcome to join the followers of Vynora!" };
        final String[] alt1 = { "Seeker!", "I am the Water and the Wind. A bowl is my symbol.", "Have you ever asked yourself the Questions?", "I will help you find the answer to the Ancient Riddles, if you help me.", "My knowledge is vast, but I need to know the last parts! We all must know!", "What are you? What lingers in the darkness of the Void?", "Seek excellence. Seek the truth in all things. Go anywhere in your attempts to find it.", "True knowledge also brings you power.", "Exercise that power with care, or it will hurt you like the snake who bites its tail.", "Too many secrets are hidden by the other gods, and none will they reveal. This cannot be.", "Flow with me!" };
        vynora.convertText1 = conv1;
        vynora.altarConvertText1 = alt1;
    }
    
    private static void createLibilaConvertStrings(final Deity libila) {
        final String[] conv1 = { "Look at you. Pitiful creature.", "You seek the powers of the Whisperer? They may be available.", "Know that I personally will not help you much. But I am bound to tell you that she will.", "To tell the truth - She rewards me for recruiting you.", "People say much about us. They think we lie. And yes we do. We lie about a lot of things, but not about the truth.", "Truth is everything will end. Truth is some of us will be rewarded greatly by Her some day.", "Libila will not accept her having been betrayed by the other gods. Your contract with her is to help her, and she will help you.", "Her aim is to gain control here, and your goal is to stop the others from gaining it instead.", "You are expected and required to use all effective means available: Terror, deception, torture, death, sacrifices.", "Are you ready to join the Horde of the Summoned? Know that if you choose not to, you are against us!" };
        final String[] alt1 = { "Look at you. Pitiful creature.", "You seek the powers of the Whisperer? They may be available.", "I am the Hate and the Deceit, but know that I will help you much.", "Know that I want revenge for the betrayal by the others. This is why the scythe is my symbol.", "Become my tool and my weapon. Let me sharpen you, and let me run you through the heart of my enemies.", "For this I will reward you greatly. You will be given powers beyond normal mortal possibilities.", "Exact my revenge anywhere, anytime and anyhow. Make it painful and frightening.", "Together, let us enter the Forbidden Lands. We are all in our right to do so!", "Let us grow together, and throw our enemies into the void!", "Are you ready to join the Horde of the Summoned? Know that if you choose not to, you are against me!" };
        libila.convertText1 = conv1;
        libila.altarConvertText1 = alt1;
    }
    
    public static boolean acceptsNewChampions(final int deityNumber) {
        int nums = PlayerInfoFactory.getNumberOfChamps(deityNumber);
        if (deityNumber == 1) {
            nums += PlayerInfoFactory.getNumberOfChamps(3);
        }
        if (deityNumber == 3) {
            nums += PlayerInfoFactory.getNumberOfChamps(1);
        }
        if (deityNumber == 4) {
            return nums < 200;
        }
        return nums < 200;
    }
    
    public static final void clearValreiPositions() {
        Deities.valreiPositions.clear();
    }
    
    public static final boolean hasValreiPositions() {
        return Deities.valreiPositions.size() > 0;
    }
    
    public static final void addPosition(final int deityId, final int hexPosition) {
        Deities.valreiPositions.put(deityId, hexPosition);
    }
    
    public static final Integer getPosition(final int deityId) {
        return Deities.valreiPositions.get(deityId);
    }
    
    public static final void clearValreiStatuses() {
        Deities.valreiStatuses.clear();
    }
    
    public static final void addStatus(final String status, final int deityId) {
        Deities.valreiStatuses.put(status, deityId);
    }
    
    public static final String getEntityName(final int deityId) {
        switch (deityId) {
            case 7: {
                return "Walnut";
            }
            case 10: {
                return "The Deathcrawler";
            }
            case 8: {
                return "Pharmakos";
            }
            case 6: {
                return "Nogump";
            }
            case 11: {
                return "The Scavenger";
            }
            case 12: {
                return "The Dirtmaw Giant";
            }
            case 9: {
                return "Jackal";
            }
            default: {
                String n = Deities.valreiNames.get(deityId);
                if (n == null) {
                    n = "";
                }
                return n;
            }
        }
    }
    
    public static final String getDeityName(final int deityId) {
        final Deity d = getDeity(deityId);
        if (d != null) {
            return d.getName();
        }
        switch (deityId) {
            case 7: {
                return "Walnut";
            }
            case 10: {
                return "The Deathcrawler";
            }
            case 8: {
                return "Pharmakos";
            }
            case 6: {
                return "Nogump";
            }
            case 11: {
                return "The Scavenger";
            }
            case 12: {
                return "The Dirtmaw Giant";
            }
            case 9: {
                return "Jackal";
            }
            default: {
                String n = Deities.valreiNames.get(deityId);
                if (n == null) {
                    n = "";
                }
                return n;
            }
        }
    }
    
    public static final String getRandomStatusFor(final int deityId) {
        final List<String> availStatuses = new ArrayList<String>();
        for (final Map.Entry<String, Integer> status : Deities.valreiStatuses.entrySet()) {
            if (status.getValue() == deityId) {
                availStatuses.add(status.getKey());
            }
        }
        if (availStatuses.size() > 0) {
            final int num = Server.rand.nextInt(availStatuses.size());
            return availStatuses.get(num);
        }
        return "";
    }
    
    public static final boolean hasValreiStatuses() {
        return Deities.valreiStatuses.size() > 0;
    }
    
    public static final String getRandomStatus() {
        if (Deities.valreiStatuses.size() > 0) {
            final int num = Server.rand.nextInt(Deities.valreiStatuses.size());
            int x = 0;
            for (final Map.Entry<String, Integer> status : Deities.valreiStatuses.entrySet()) {
                if (x >= num) {
                    return status.getKey();
                }
                ++x;
            }
        }
        return "";
    }
    
    static {
        deities = new HashMap<Integer, Deity>();
        valreiStatuses = new HashMap<String, Integer>();
        valreiPositions = new HashMap<Integer, Integer>();
        valreiNames = new HashMap<Integer, String>();
        Deities.logger = Logger.getLogger(Deities.class.getName());
        Deities.maxDeityNum = 100;
        Deities.faithPlayers = 0.0f;
        try {
            Deities.logger.log(Level.INFO, "Loading deities ");
            loadDeities();
        }
        catch (IOException iox) {
            Deities.logger.log(Level.WARNING, "Failed to load deities!", iox);
        }
    }
}
