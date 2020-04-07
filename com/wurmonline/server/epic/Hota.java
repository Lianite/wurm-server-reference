// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.epic;

import java.util.HashSet;
import com.wurmonline.server.FailedException;
import com.wurmonline.server.items.NoSuchTemplateException;
import com.wurmonline.server.items.WurmColor;
import com.wurmonline.server.items.ItemFactory;
import com.wurmonline.server.zones.Zone;
import com.wurmonline.server.zones.NoSuchZoneException;
import com.wurmonline.server.zones.VolaTile;
import com.wurmonline.server.zones.Zones;
import com.wurmonline.server.villages.Village;
import com.wurmonline.server.villages.NoSuchVillageException;
import com.wurmonline.server.villages.Villages;
import com.wurmonline.server.villages.PvPAlliance;
import java.util.Iterator;
import java.util.Map;
import java.util.HashMap;
import com.wurmonline.server.statistics.ChallengeSummary;
import com.wurmonline.server.statistics.ChallengePointEnum;
import com.wurmonline.server.players.Player;
import com.wurmonline.server.creatures.Creature;
import com.wurmonline.server.WurmCalendar;
import com.wurmonline.server.Server;
import com.wurmonline.server.zones.FocusZone;
import com.wurmonline.server.Servers;
import java.sql.ResultSet;
import java.sql.PreparedStatement;
import java.sql.Connection;
import java.sql.Statement;
import com.wurmonline.server.utils.DbUtilities;
import java.sql.SQLException;
import com.wurmonline.server.NoSuchItemException;
import java.util.logging.Level;
import com.wurmonline.server.Items;
import com.wurmonline.server.DbConnector;
import java.util.Set;
import java.util.LinkedList;
import com.wurmonline.server.items.Item;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;
import com.wurmonline.server.MiscConstants;
import com.wurmonline.server.TimeConstants;

public final class Hota implements TimeConstants, MiscConstants
{
    private static final Logger logger;
    private static final String LOAD_ALL_HOTA_ITEMS = "SELECT * FROM HOTA_ITEMS";
    private static final String CREATE_HOTA_ITEM = "INSERT INTO HOTA_ITEMS (ITEMID,ITEMTYPE) VALUES (?,?)";
    private static final String DELETE_HOTA_ITEMS = "DELETE FROM HOTA_ITEMS";
    private static final String INSERT_HOTA_HELPER = "INSERT INTO HOTA_HELPERS (CONQUERS,WURMID) VALUES (?,?)";
    private static final String UPDATE_HOTA_HELPER = "UPDATE HOTA_HELPERS SET CONQUERS=? WHERE WURMID=?";
    private static final String LOAD_ALL_HOTA_HELPER = "SELECT * FROM HOTA_HELPERS";
    private static final String DELETE_HOTA_HELPERS = "DELETE FROM HOTA_HELPERS";
    private static final ConcurrentHashMap<Item, Byte> hotaItems;
    private static final ConcurrentHashMap<Long, Integer> hotaHelpers;
    public static final byte TYPE_NONE = 0;
    public static final byte TYPE_PILLAR = 1;
    public static final byte TYPE_SPEEDSHRINE = 2;
    private static long nextRoundMessage;
    public static final int VILLAGE_ID_MOD = 2000000;
    public static final LinkedList<Item> pillarsLeft;
    public static final LinkedList<Item> pillarsTouched;
    private static final Set<Long> hotaConquerers;
    
    public static void loadAllHotaItems() {
        final long now = System.nanoTime();
        int numberOfItemsLoaded = 0;
        Connection dbcon = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            dbcon = DbConnector.getZonesDbCon();
            ps = dbcon.prepareStatement("SELECT * FROM HOTA_ITEMS");
            rs = ps.executeQuery();
            while (rs.next()) {
                final byte hotatype = rs.getByte("ITEMTYPE");
                final long itemId = rs.getLong("ITEMID");
                try {
                    final Item item = Items.getItem(itemId);
                    Hota.hotaItems.put(item, hotatype);
                    ++numberOfItemsLoaded;
                }
                catch (NoSuchItemException nsi) {
                    Hota.logger.log(Level.WARNING, nsi.getMessage(), nsi);
                }
            }
        }
        catch (SQLException sqx) {
            Hota.logger.log(Level.WARNING, sqx.getMessage(), sqx);
        }
        finally {
            DbUtilities.closeDatabaseObjects(ps, rs);
            DbConnector.returnConnection(dbcon);
            final float lElapsedTime = (System.nanoTime() - now) / 1000000.0f;
            Hota.logger.log(Level.INFO, "Loaded " + numberOfItemsLoaded + " HOTA items. It took " + lElapsedTime + " millis.");
        }
    }
    
    public static void poll() {
        if (Servers.localServer.getNextHota() > 0L) {
            if (System.currentTimeMillis() > Servers.localServer.getNextHota()) {
                final FocusZone hotaZone = FocusZone.getHotaZone();
                if (hotaZone != null) {
                    if (Hota.hotaItems.isEmpty()) {
                        createHotaItems();
                    }
                    putHotaItemsInWorld();
                    Servers.localServer.setNextHota(Long.MAX_VALUE);
                    String toBroadCast = "The Hunt of the Ancients has begun!";
                    switch (Server.rand.nextInt(4)) {
                        case 0: {
                            toBroadCast = "The Hunt of the Ancients has begun!";
                            break;
                        }
                        case 1: {
                            toBroadCast = "Let The Hunt Begin!";
                            break;
                        }
                        case 2: {
                            toBroadCast = "Hunt! Hunt! Hunt!";
                            break;
                        }
                        case 3: {
                            toBroadCast = "The Hunt of the Ancients is on!";
                            break;
                        }
                        case 4: {
                            if (WurmCalendar.isNight()) {
                                toBroadCast = "It's the night of the Hunter!";
                                break;
                            }
                            toBroadCast = "It's a glorious day for the Hunt!";
                            break;
                        }
                        case 5: {
                            if (Server.rand.nextInt(100) == 0) {
                                toBroadCast = "Run, Forrest! Run!";
                                break;
                            }
                            toBroadCast = "Go conquer those pillars!";
                            break;
                        }
                        default: {
                            toBroadCast = "The Hunt of the Ancients has begun!";
                            break;
                        }
                    }
                    Server.getInstance().broadCastSafe(toBroadCast);
                }
                else {
                    putHotaItemsInVoid();
                    Servers.localServer.setNextHota(0L);
                    Hota.nextRoundMessage = Long.MAX_VALUE;
                }
            }
            else if (Servers.localServer.getNextHota() < Long.MAX_VALUE) {
                if (Hota.nextRoundMessage == Long.MAX_VALUE) {
                    Hota.nextRoundMessage = System.currentTimeMillis();
                }
                final long timeLeft = Servers.localServer.getNextHota() - System.currentTimeMillis();
                if (System.currentTimeMillis() >= Hota.nextRoundMessage) {
                    String one = "The ";
                    String two = "Hunt of the Ancients ";
                    String three = "begins ";
                    if (Server.rand.nextBoolean()) {
                        one = "The next ";
                        if (Server.rand.nextBoolean()) {
                            one = "A new ";
                        }
                    }
                    if (Server.rand.nextBoolean()) {
                        two = "Hunt ";
                        if (Server.rand.nextBoolean()) {
                            two = "HotA ";
                        }
                    }
                    if (Server.rand.nextBoolean()) {
                        three = "starts ";
                        if (Server.rand.nextBoolean()) {
                            three = "will begin ";
                        }
                    }
                    Server.getInstance().broadCastSafe(one + two + three + "in " + Server.getTimeFor(timeLeft) + ".");
                    Hota.nextRoundMessage = System.currentTimeMillis() + (Servers.localServer.getNextHota() - System.currentTimeMillis()) / 2L;
                }
            }
        }
    }
    
    public static void addPillarConquered(final Creature creature, final Item pillar) {
        int numsToAdd = 5;
        final Integer points = Hota.hotaHelpers.get(creature.getWurmId());
        if (points != null) {
            numsToAdd = points + 5;
        }
        addHotaHelper(creature, numsToAdd);
        if (!Hota.pillarsTouched.contains(pillar)) {
            if (creature.isPlayer() && !Hota.hotaConquerers.contains(creature.getWurmId())) {
                Hota.hotaConquerers.add(creature.getWurmId());
            }
            if (Servers.localServer.isChallengeServer() && creature.isPlayer()) {
                ChallengeSummary.addToScore(((Player)creature).getSaveFile(), ChallengePointEnum.ChallengePoint.HOTAPILLARS.getEnumtype(), 1.0f);
                ChallengeSummary.addToScore(((Player)creature).getSaveFile(), ChallengePointEnum.ChallengePoint.OVERALL.getEnumtype(), 5.0f);
            }
            Hota.pillarsTouched.add(pillar);
            if (!Hota.pillarsLeft.isEmpty()) {
                final Item next = Hota.pillarsLeft.remove(Server.rand.nextInt(Hota.pillarsLeft.size()));
                putPillarInWorld(next);
            }
        }
        creature.achievement(1);
        final Map<Integer, Integer> alliances = new HashMap<Integer, Integer>();
        for (final Item item : Hota.hotaItems.keySet()) {
            if (item.getData1() > 0) {
                Integer nums = alliances.get(item.getData1());
                if (nums != null) {
                    ++nums;
                }
                else {
                    nums = 1;
                }
                alliances.put(item.getData1(), nums);
                if (nums < 4) {
                    continue;
                }
                win(item.getData1(), creature);
            }
        }
    }
    
    public final LinkedList<Item> getPillarsInWorld() {
        return Hota.pillarsTouched;
    }
    
    static void win(final int allianceNumber, final Creature winner) {
        final PvPAlliance winAlliance = PvPAlliance.getPvPAlliance(allianceNumber);
        if (winAlliance != null) {
            Server.getInstance().broadCastSafe(winner.getName() + " has secured victory for " + winAlliance.getName() + "!");
            winAlliance.addHotaWin();
        }
        else {
            try {
                final Village v = Villages.getVillage(allianceNumber - 2000000);
                Server.getInstance().broadCastSafe(winner.getName() + " has secured victory for " + v.getName() + "!");
                v.addHotaWin();
                v.createHotaPrize(v.getHotaWins());
            }
            catch (NoSuchVillageException e) {
                Hota.logger.log(Level.WARNING, e.getMessage(), e);
                Server.getInstance().broadCastSafe(winner.getName() + " has secured victory for " + winner.getHimHerItString() + "self!");
                winner.setHotaWins((short)(winner.getHotaWins() + 1));
            }
        }
        clearHotaHelpers();
        putHotaItemsInVoid();
        Servers.localServer.setNextHota(System.currentTimeMillis() + 129600000L);
        Hota.nextRoundMessage = (Servers.isThisATestServer() ? (System.currentTimeMillis() + 60000L) : (System.currentTimeMillis() + 3600000L));
    }
    
    public static void addPillarTouched(final Creature creature, final Item pillar) {
        final int sx = Zones.safeTileX(pillar.getTileX() - 30);
        final int sy = Zones.safeTileY(pillar.getTileY() - 30);
        final int ex = Zones.safeTileX(pillar.getTileX() + 30);
        final int ey = Zones.safeTileY(pillar.getTileY() + 30);
        for (int x = sx; x < ex; ++x) {
            for (int y = sy; y < ey; ++y) {
                final VolaTile t = Zones.getTileOrNull(x, y, creature.isOnSurface());
                if (t != null) {
                    for (final Creature c : t.getCreatures()) {
                        final Integer points = Hota.hotaHelpers.get(c.getWurmId());
                        if (points == null) {
                            addHotaHelper(c, 1);
                        }
                    }
                }
            }
        }
    }
    
    private static void putHotaItemsInVoid() {
        Hota.pillarsTouched.clear();
        Hota.pillarsLeft.clear();
        for (final Item item : Hota.hotaItems.keySet()) {
            item.deleteAllEffects();
            item.setData1(0);
            if (item.getZoneId() > 0) {
                item.putInVoid();
            }
        }
    }
    
    public static final Set<Item> getHotaItems() {
        return Hota.hotaItems.keySet();
    }
    
    private static void putHotaItemsInWorld() {
        final boolean SaromansEdition = Server.rand.nextInt(10) > 0;
        final FocusZone hotaZone = FocusZone.getHotaZone();
        if (hotaZone == null) {
            return;
        }
        int num = 0;
        int numShrines = 0;
        for (final Item item : Hota.hotaItems.keySet()) {
            item.setData1(0);
            if (item.getZoneId() > 0) {
                item.deleteAllEffects();
                item.putInVoid();
            }
            final int sizeX = hotaZone.getEndX() - hotaZone.getStartX();
            final int xborder = sizeX / 10;
            final int sizeXSlot = (sizeX - xborder - xborder) / 3;
            Hota.logger.log(Level.INFO, "Hota size x " + sizeX + " border=" + xborder + " sizeXSlot=" + sizeXSlot);
            final int sizeY = hotaZone.getEndY() - hotaZone.getStartY();
            final int yborder = sizeY / 10;
            final int sizeYSlot = (sizeY - yborder - yborder) / 3;
            Hota.logger.log(Level.INFO, "Hota size y  " + sizeY + " border=" + yborder + " sizeYSlot=" + sizeYSlot);
            int tx = Zones.safeTileX(hotaZone.getStartX() + Server.rand.nextInt(Math.max(10, sizeX)));
            int ty = Zones.safeTileY(hotaZone.getStartY() + Server.rand.nextInt(Math.max(10, sizeY)));
            boolean pillar = false;
            if (item.getTemplateId() == 739) {
                pillar = true;
                tx = Zones.safeTileX(hotaZone.getStartX() + xborder + num % 3 * sizeXSlot + Server.rand.nextInt(sizeXSlot));
                if (num < 3) {
                    ty = Zones.safeTileY(hotaZone.getStartY() + yborder + Server.rand.nextInt(sizeYSlot));
                }
                else if (num < 6) {
                    ty = Zones.safeTileY(hotaZone.getStartY() + yborder + sizeYSlot + Server.rand.nextInt(sizeYSlot));
                }
                else if (num < 9) {
                    ty = Zones.safeTileY(hotaZone.getStartY() + yborder + 2 * sizeYSlot + Server.rand.nextInt(sizeYSlot));
                }
                else if (num >= 9) {
                    tx = Zones.safeTileX(hotaZone.getStartX() + xborder + Server.rand.nextInt(sizeXSlot * 3));
                    ty = Zones.safeTileY(hotaZone.getStartY() + yborder + Server.rand.nextInt(sizeYSlot * 3));
                }
                ++num;
            }
            else if (item.getTemplateId() == 741) {
                tx = Math.max(0, Zones.safeTileX(hotaZone.getStartX() + xborder + sizeXSlot / 2 + numShrines % 2 * sizeXSlot + Server.rand.nextInt(sizeXSlot)));
                if (numShrines < 2) {
                    ty = Zones.safeTileY(hotaZone.getStartY() + yborder + sizeYSlot / 2 + Server.rand.nextInt(sizeXSlot));
                }
                else {
                    ty = Zones.safeTileY(hotaZone.getStartY() + yborder + sizeYSlot / 2 + sizeYSlot + Server.rand.nextInt(sizeXSlot));
                }
                ++numShrines;
            }
            final float posx = (tx << 2) + 2;
            final float posy = (ty << 2) + 2;
            try {
                item.setPosXYZ(posx, posy, Zones.calculateHeight(posx, posy, true));
                if (!pillar || !SaromansEdition) {
                    putPillarInWorld(item);
                }
                else {
                    Hota.pillarsLeft.add(item);
                }
                Hota.logger.log(Level.INFO, item.getName() + " " + num + "(" + numShrines + ") put at " + tx + "," + ty + " num % 3 =" + num % 3);
            }
            catch (NoSuchZoneException nsz) {
                Hota.logger.log(Level.INFO, "Item " + item.getWurmId() + " outside range " + item.getPosX() + " " + item.getPosY());
            }
        }
        if (!Hota.pillarsLeft.isEmpty()) {
            final Item next = Hota.pillarsLeft.remove(Server.rand.nextInt(Hota.pillarsLeft.size()));
            putPillarInWorld(next);
            final Item next2 = Hota.pillarsLeft.remove(Server.rand.nextInt(Hota.pillarsLeft.size()));
            putPillarInWorld(next2);
            final Item next3 = Hota.pillarsLeft.remove(Server.rand.nextInt(Hota.pillarsLeft.size()));
            putPillarInWorld(next3);
        }
    }
    
    public static final void putPillarInWorld(final Item pillar) {
        try {
            final Zone z = Zones.getZone((int)pillar.getPosX() >> 2, (int)pillar.getPosY() >> 2, true);
            z.addItem(pillar);
            Hota.logger.log(Level.INFO, pillar.getName() + " spawned at " + pillar.getTileX() + "," + pillar.getTileY());
        }
        catch (NoSuchZoneException nsz) {
            Hota.logger.log(Level.INFO, "Pillar " + pillar.getWurmId() + " outside range " + pillar.getPosX() + " " + pillar.getPosY());
        }
    }
    
    public static final void forcePillarsToWorld() {
        if (Servers.localServer.isShuttingDownIn < 30 && Servers.localServer.getNextHota() == Long.MAX_VALUE) {
            Hota.logger.warning("Forcing all remaining pillars to spawn into the world");
            while (!Hota.pillarsLeft.isEmpty()) {
                final Item next = Hota.pillarsLeft.removeLast();
                Hota.logger.fine("Putting " + next.getName() + " in the world!");
                putPillarInWorld(next);
            }
        }
        else if (!Servers.localServer.maintaining) {
            Hota.logger.warning("Something just tried to force all HoTA pillars to spawn when not appropriate");
        }
    }
    
    static void createHotaItems() {
        Hota.logger.info("Creating Hunt of the Ancients items.");
        try {
            final Item pillarOne = ItemFactory.createItem(739, 90.0f, null);
            pillarOne.setName("Green pillar of the hunt");
            pillarOne.setColor(WurmColor.createColor(0, 255, 0));
            Hota.hotaItems.put(pillarOne, (byte)1);
            insertHotaItem(pillarOne, (byte)1);
            final Item pillarTwo = ItemFactory.createItem(739, 90.0f, null);
            pillarTwo.setName("Blue pillar of the hunt");
            pillarTwo.setColor(WurmColor.createColor(0, 0, 255));
            Hota.hotaItems.put(pillarTwo, (byte)1);
            insertHotaItem(pillarTwo, (byte)1);
            final Item pillarThree = ItemFactory.createItem(739, 90.0f, null);
            pillarThree.setName("Red pillar of the hunt");
            pillarThree.setColor(WurmColor.createColor(255, 0, 0));
            Hota.hotaItems.put(pillarThree, (byte)1);
            insertHotaItem(pillarThree, (byte)1);
            final Item pillarFour = ItemFactory.createItem(739, 90.0f, null);
            pillarFour.setName("Yellow pillar of the hunt");
            pillarFour.setColor(WurmColor.createColor(238, 244, 6));
            Hota.hotaItems.put(pillarFour, (byte)1);
            insertHotaItem(pillarFour, (byte)1);
            final Item pillarFive = ItemFactory.createItem(739, 90.0f, null);
            pillarFive.setName("Sky pillar of the hunt");
            pillarFive.setColor(WurmColor.createColor(33, 208, 218));
            Hota.hotaItems.put(pillarFive, (byte)1);
            insertHotaItem(pillarFive, (byte)1);
            final Item pillarSix = ItemFactory.createItem(739, 90.0f, null);
            pillarSix.setName("Black pillar of the hunt");
            pillarSix.setColor(WurmColor.createColor(0, 0, 0));
            Hota.hotaItems.put(pillarSix, (byte)1);
            insertHotaItem(pillarSix, (byte)1);
            final Item pillarSeven = ItemFactory.createItem(739, 90.0f, null);
            pillarSeven.setName("Clear pillar of the hunt");
            pillarSix.setColor(WurmColor.createColor(255, 255, 255));
            Hota.hotaItems.put(pillarSeven, (byte)1);
            insertHotaItem(pillarSeven, (byte)1);
            final Item pillarEight = ItemFactory.createItem(739, 90.0f, null);
            pillarEight.setName("Brown pillar of the hunt");
            pillarEight.setColor(WurmColor.createColor(154, 88, 22));
            Hota.hotaItems.put(pillarEight, (byte)1);
            insertHotaItem(pillarEight, (byte)1);
            final Item pillarNine = ItemFactory.createItem(739, 90.0f, null);
            pillarNine.setName("Lilac pillar of the hunt");
            pillarNine.setColor(WurmColor.createColor(134, 69, 186));
            Hota.hotaItems.put(pillarNine, (byte)1);
            insertHotaItem(pillarNine, (byte)1);
            final Item pillarTen = ItemFactory.createItem(739, 90.0f, null);
            pillarTen.setName("Orange pillar of the hunt");
            pillarTen.setColor(WurmColor.createColor(255, 128, 186));
            Hota.hotaItems.put(pillarTen, (byte)1);
            insertHotaItem(pillarTen, (byte)1);
            final Item shrineOne = ItemFactory.createItem(741, 90.0f, null);
            Hota.hotaItems.put(shrineOne, (byte)2);
            insertHotaItem(shrineOne, (byte)2);
            final Item shrineTwo = ItemFactory.createItem(741, 90.0f, null);
            Hota.hotaItems.put(shrineTwo, (byte)2);
            insertHotaItem(shrineTwo, (byte)2);
            final Item shrineThree = ItemFactory.createItem(741, 90.0f, null);
            Hota.hotaItems.put(shrineThree, (byte)2);
            insertHotaItem(shrineThree, (byte)2);
            final Item shrineFour = ItemFactory.createItem(741, 90.0f, null);
            Hota.hotaItems.put(shrineFour, (byte)2);
            insertHotaItem(shrineFour, (byte)2);
        }
        catch (NoSuchTemplateException nst) {
            Hota.logger.log(Level.WARNING, nst.getMessage(), nst);
        }
        catch (FailedException fe) {
            Hota.logger.log(Level.WARNING, fe.getMessage(), fe);
        }
        finally {
            Hota.logger.info("Finished creating Hunt of the Ancients items.");
        }
    }
    
    public static void loadAllHelpers() {
        final long now = System.nanoTime();
        int numberOfHelpersLoaded = 0;
        Connection dbcon = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            dbcon = DbConnector.getZonesDbCon();
            ps = dbcon.prepareStatement("SELECT * FROM HOTA_HELPERS");
            rs = ps.executeQuery();
            while (rs.next()) {
                final int conquerValue = rs.getInt("CONQUERS");
                final long wid = rs.getLong("WURMID");
                Hota.hotaHelpers.put(wid, conquerValue);
                ++numberOfHelpersLoaded;
            }
        }
        catch (SQLException sqx) {
            Hota.logger.log(Level.WARNING, sqx.getMessage(), sqx);
        }
        finally {
            DbUtilities.closeDatabaseObjects(ps, rs);
            DbConnector.returnConnection(dbcon);
            final float lElapsedTime = (System.nanoTime() - now) / 1000000.0f;
            Hota.logger.log(Level.INFO, "Loaded " + numberOfHelpersLoaded + " HOTA helpers. It took " + lElapsedTime + " millis.");
        }
    }
    
    public static int getHelpValue(final long creatureId) {
        final Integer helped = Hota.hotaHelpers.get(creatureId);
        if (helped != null) {
            return helped;
        }
        return 0;
    }
    
    static void addHotaHelper(final Creature creature, final int helpValue) {
        Connection dbcon = null;
        PreparedStatement ps = null;
        try {
            dbcon = DbConnector.getZonesDbCon();
            String updateOrInsert = "INSERT INTO HOTA_HELPERS (CONQUERS,WURMID) VALUES (?,?)";
            final Integer helped = Hota.hotaHelpers.get(creature.getWurmId());
            if (helped != null) {
                updateOrInsert = "UPDATE HOTA_HELPERS SET CONQUERS=? WHERE WURMID=?";
            }
            Hota.hotaHelpers.put(creature.getWurmId(), helpValue);
            ps = dbcon.prepareStatement(updateOrInsert);
            ps.setInt(1, helpValue);
            ps.setLong(2, creature.getWurmId());
            ps.executeUpdate();
        }
        catch (SQLException sqx) {
            Hota.logger.log(Level.WARNING, "Failed to update Hota helper: " + creature.getName() + " with value " + helpValue, sqx);
        }
        finally {
            DbUtilities.closeDatabaseObjects(ps, null);
            DbConnector.returnConnection(dbcon);
        }
    }
    
    public static void destroyHota() {
        for (final Item i : Hota.hotaItems.keySet()) {
            Items.destroyItem(i.getWurmId());
        }
        Hota.hotaItems.clear();
        clearHotaHelpers();
        Connection dbcon = null;
        PreparedStatement ps = null;
        try {
            dbcon = DbConnector.getZonesDbCon();
            ps = dbcon.prepareStatement("DELETE FROM HOTA_ITEMS");
            ps.executeUpdate();
        }
        catch (SQLException sqx) {
            Hota.logger.log(Level.WARNING, "Failed to delete hota items", sqx);
        }
        finally {
            DbUtilities.closeDatabaseObjects(ps, null);
            DbConnector.returnConnection(dbcon);
        }
        Servers.localServer.setNextHota(0L);
        Hota.nextRoundMessage = Long.MAX_VALUE;
    }
    
    static void clearHotaHelpers() {
        Hota.hotaHelpers.clear();
        Hota.hotaConquerers.clear();
        Connection dbcon = null;
        PreparedStatement ps = null;
        try {
            dbcon = DbConnector.getZonesDbCon();
            ps = dbcon.prepareStatement("DELETE FROM HOTA_HELPERS");
            ps.executeUpdate();
        }
        catch (SQLException sqx) {
            Hota.logger.log(Level.WARNING, "Failed to delete all Hota helpers: ", sqx);
        }
        finally {
            DbUtilities.closeDatabaseObjects(ps, null);
            DbConnector.returnConnection(dbcon);
        }
    }
    
    private static void insertHotaItem(final Item item, final byte type) {
        Connection dbcon = null;
        PreparedStatement ps = null;
        try {
            dbcon = DbConnector.getZonesDbCon();
            ps = dbcon.prepareStatement("INSERT INTO HOTA_ITEMS (ITEMID,ITEMTYPE) VALUES (?,?)");
            ps.setLong(1, item.getWurmId());
            ps.setByte(2, type);
            ps.executeUpdate();
        }
        catch (SQLException sqx) {
            Hota.logger.log(Level.WARNING, "Failed to insert hota item id " + item.getWurmId() + " - " + item.getName() + " and type " + type, sqx);
        }
        finally {
            DbUtilities.closeDatabaseObjects(ps, null);
            DbConnector.returnConnection(dbcon);
        }
    }
    
    static {
        logger = Logger.getLogger(Hota.class.getName());
        hotaItems = new ConcurrentHashMap<Item, Byte>();
        hotaHelpers = new ConcurrentHashMap<Long, Integer>();
        Hota.nextRoundMessage = Long.MAX_VALUE;
        pillarsLeft = new LinkedList<Item>();
        pillarsTouched = new LinkedList<Item>();
        hotaConquerers = new HashSet<Long>();
    }
}
