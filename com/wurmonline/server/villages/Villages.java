// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.villages;

import com.wurmonline.server.economy.Change;
import com.wurmonline.server.kingdom.Kingdom;
import com.wurmonline.server.kingdom.Kingdoms;
import java.util.ArrayList;
import java.sql.ResultSet;
import java.sql.PreparedStatement;
import java.sql.Connection;
import java.sql.Statement;
import com.wurmonline.server.utils.DbUtilities;
import java.sql.SQLException;
import com.wurmonline.server.DbConnector;
import com.wurmonline.server.WurmId;
import com.wurmonline.server.creatures.Offspring;
import com.wurmonline.shared.util.StringUtilities;
import com.wurmonline.mesh.Tiles;
import com.wurmonline.server.Server;
import com.wurmonline.server.players.Player;
import com.wurmonline.server.creatures.CreatureTemplate;
import com.wurmonline.server.zones.Den;
import com.wurmonline.server.creatures.NoSuchCreatureTemplateException;
import com.wurmonline.server.creatures.Creatures;
import com.wurmonline.server.creatures.CreatureTemplateFactory;
import com.wurmonline.server.zones.Dens;
import com.wurmonline.server.Constants;
import com.wurmonline.server.Servers;
import java.util.Hashtable;
import java.util.Map;
import javax.annotation.Nullable;
import java.util.HashSet;
import com.wurmonline.server.zones.FocusZone;
import java.awt.Rectangle;
import com.wurmonline.server.items.ItemFactory;
import com.wurmonline.server.zones.VolaTile;
import com.wurmonline.server.zones.Zones;
import com.wurmonline.server.NoSuchPlayerException;
import com.wurmonline.server.creatures.NoSuchCreatureException;
import java.io.IOException;
import com.wurmonline.server.NoSuchItemException;
import com.wurmonline.server.items.Item;
import com.wurmonline.server.kingdom.InfluenceChain;
import com.wurmonline.server.Features;
import com.wurmonline.server.HistoryManager;
import com.wurmonline.server.items.NoSuchTemplateException;
import java.util.logging.Level;
import com.wurmonline.server.Items;
import com.wurmonline.server.FailedException;
import com.wurmonline.server.creatures.Creature;
import javax.annotation.Nonnull;
import com.wurmonline.math.TilePos;
import java.util.Iterator;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import javax.annotation.concurrent.GuardedBy;
import java.util.Set;
import java.util.logging.Logger;
import java.util.concurrent.ConcurrentHashMap;
import com.wurmonline.server.TimeConstants;
import com.wurmonline.server.economy.MonetaryConstants;
import com.wurmonline.server.MiscConstants;

public final class Villages implements VillageStatus, MiscConstants, MonetaryConstants, TimeConstants
{
    private static final ConcurrentHashMap<Integer, Village> villages;
    private static final ConcurrentHashMap<Long, DeadVillage> deadVillages;
    private static Logger logger;
    private static final String LOAD_VILLAGES = "SELECT * FROM VILLAGES WHERE DISBANDED=0";
    private static final String LOAD_DEAD_VILLAGES = "SELECT * FROM VILLAGES WHERE DISBANDED=1";
    private static final String CREATE_DEAD_VILLAGE = "INSERT INTO VILLAGES (NAME,FOUNDER,MAYOR,CREATIONDATE,STARTX,ENDX,STARTY,ENDY,DEEDID,LASTLOGIN,KINGDOM,DISBAND,DISBANDED,DEVISE) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
    private static final String LOAD_WARS = "SELECT * FROM VILLAGEWARS";
    private static final String LOAD_WAR_DECLARATIONS = "SELECT * FROM VILLAGEWARDECLARATIONS";
    @GuardedBy("ALLIANCES_RW_LOCK")
    private static final Set<Alliance> alliances;
    private static final ReentrantReadWriteLock ALLIANCES_RW_LOCK;
    @GuardedBy("WARS_RW_LOCK")
    private static final Set<Object> wars;
    private static final ReentrantReadWriteLock WARS_RW_LOCK;
    public static long TILE_UPKEEP;
    public static String TILE_UPKEEP_STRING;
    public static long TILE_COST;
    public static String TILE_COST_STRING;
    public static long GUARD_COST;
    public static String GUARD_COST_STRING;
    public static long GUARD_UPKEEP;
    public static String GUARD_UPKEEP_STRING;
    public static long PERIMETER_COST;
    public static String PERIMETER_COST_STRING;
    public static long PERIMETER_UPKEEP;
    public static String PERIMETER_UPKEEP_STRING;
    public static long MINIMUM_UPKEEP;
    public static String MINIMUM_UPKEEP_STRING;
    private static long lastPolledVillageFaith;
    
    public static Village getVillage(final int id) throws NoSuchVillageException {
        final Village toReturn = Villages.villages.get(id);
        if (toReturn == null) {
            throw new NoSuchVillageException("No village with id " + id);
        }
        return toReturn;
    }
    
    public static Village getVillage(final String name) throws NoSuchVillageException {
        for (final Village v : Villages.villages.values()) {
            if (v.getName().equalsIgnoreCase(name)) {
                return v;
            }
        }
        throw new NoSuchVillageException("No village with name " + name);
    }
    
    public static Village getVillage(@Nonnull final TilePos tilePos, final boolean surfaced) {
        return getVillage(tilePos.x, tilePos.y, surfaced);
    }
    
    public static Village getVillage(final int tilex, final int tiley, final boolean surfaced) {
        for (final Village village : Villages.villages.values()) {
            if (village.covers(tilex, tiley)) {
                return village;
            }
        }
        return null;
    }
    
    public static Village getVillagePlus(final int tilex, final int tiley, final boolean surfaced, final int extra) {
        for (final Village village : Villages.villages.values()) {
            if (village.coversPlus(tilex, tiley, extra)) {
                return village;
            }
        }
        return null;
    }
    
    public static final boolean isNameOk(final String villageName, final int ignoreVillageId) {
        for (final Village village : Villages.villages.values()) {
            if (village.id != ignoreVillageId && village.getName().equals(villageName)) {
                return false;
            }
        }
        return true;
    }
    
    public static final boolean isNameOk(final String villageName) {
        return isNameOk(villageName, -1);
    }
    
    public static Village createVillage(final int startx, final int endx, final int starty, final int endy, final int tokenx, final int tokeny, final String villageName, final Creature founder, final long deedid, final boolean surfaced, final boolean democracy, final String devise, final boolean permanent, final byte spawnKingdom, final int initialPerimeter) throws NoSuchItemException, IOException, NoSuchCreatureException, NoSuchPlayerException, NoSuchRoleException, FailedException {
        if (!isNameOk(villageName)) {
            throw new FailedException("The name " + villageName + " already exists. Please select another.");
        }
        Village toReturn = null;
        final Item deed = Items.getItem(deedid);
        if (deed.getTemplateId() == 862) {
            deed.setDamage(0.0f);
            deed.setTemplateId(663);
            deed.setData1(100);
        }
        toReturn = new DbVillage(startx, endx, starty, endy, villageName, founder, deedid, surfaced, democracy, devise, permanent, spawnKingdom, initialPerimeter);
        toReturn.addCitizen(founder, toReturn.getRoleForStatus((byte)2));
        toReturn.initialize();
        try {
            final Item token = createVillageToken(toReturn, tokenx, tokeny);
            toReturn.setTokenId(token.getWurmId());
        }
        catch (NoSuchTemplateException nst) {
            Villages.logger.log(Level.WARNING, nst.getMessage(), nst);
        }
        catch (FailedException fe) {
            Villages.logger.log(Level.WARNING, fe.getMessage(), fe);
        }
        deed.setData2(toReturn.getId());
        Villages.villages.put(toReturn.getId(), toReturn);
        toReturn.createInitialUpkeepPlan();
        toReturn.addHistory(founder.getName(), "founded");
        HistoryManager.addHistory(founder.getName(), "founded " + villageName, false);
        founder.achievement(170);
        if (Features.Feature.TOWER_CHAINING.isEnabled()) {
            final InfluenceChain chain = InfluenceChain.getInfluenceChain(toReturn.kingdom);
            InfluenceChain.addTokenToChain(toReturn.kingdom, toReturn.getToken());
        }
        return toReturn;
    }
    
    static void removeVillage(final int id) {
        final Village v = Villages.villages.remove(id);
        if (v != null) {
            final DeadVillage dv = new DeadVillage(v.getDeedId(), v.getStartX(), v.getStartY(), v.getEndX(), v.getEndY(), v.getName(), v.getFounderName(), (v.getMayor() != null) ? v.getMayor().getName() : "Unknown", v.getCreationDate(), System.currentTimeMillis(), System.currentTimeMillis(), v.kingdom);
            Villages.deadVillages.put(v.getDeedId(), dv);
        }
    }
    
    public static boolean mayCreateTokenOnTile(final boolean surfaced, final int tilex, final int tiley) {
        final VolaTile tile = Zones.getTileOrNull(tilex, tiley, surfaced);
        return tile == null || tile.getStructure() == null;
    }
    
    static Item createTokenOnTile(final Village village, final int tilex, final int tiley) throws NoSuchTemplateException, FailedException {
        final VolaTile tile = Zones.getTileOrNull(tilex, tiley, village.isOnSurface());
        if (tile == null) {
            final Item token = ItemFactory.createItem(236, 99.0f, (tilex << 2) + 2, (tiley << 2) + 2, 180.0f, village.isOnSurface(), (byte)0, -10L, null);
            token.setData2(village.getId());
            return token;
        }
        if (tile.getStructure() == null) {
            final Item token = ItemFactory.createItem(236, 99.0f, (tilex << 2) + 2, (tiley << 2) + 2, 180.0f, village.isOnSurface(), (byte)0, -10L, null);
            token.setData2(village.getId());
            return token;
        }
        return null;
    }
    
    static Item createVillageToken(final Village village, final int tokenx, final int tokeny) throws NoSuchTemplateException, FailedException {
        final int size = village.endx - village.startx;
        Item token = createTokenOnTile(village, tokenx, tokeny);
        if (token != null) {
            return token;
        }
        for (int x = -1; x <= 1; ++x) {
            for (int y = -1; y <= 1; ++y) {
                token = createTokenOnTile(village, tokenx + x, tokeny + y);
                if (token != null) {
                    return token;
                }
            }
        }
        for (int x = -size / 2; x <= size / 2; ++x) {
            for (int y = -size / 2; y <= size / 2; ++y) {
                token = createTokenOnTile(village, tokenx + x, tokeny + y);
                if (token != null) {
                    return token;
                }
            }
        }
        throw new FailedException("Failed to locate a good spot for the token item.");
    }
    
    public static final String isFocusZoneBlocking(final int sizeW, final int sizeE, final int sizeN, final int sizeS, final int tokenx, final int tokeny, final int desiredPerimeter, final boolean surfaced) {
        final int startpx = Zones.safeTileX(tokenx - sizeW - 5 - desiredPerimeter);
        final int startpy = Zones.safeTileY(tokeny - sizeN - 5 - desiredPerimeter);
        final int endpy = Zones.safeTileX(tokeny + sizeS + 1 + 5 + desiredPerimeter);
        final int endpx = Zones.safeTileY(tokenx + sizeE + 1 + 5 + desiredPerimeter);
        final Rectangle bounds = new Rectangle(startpx, startpy, endpx - startpx, endpy - startpy);
        final StringBuilder toReturn = new StringBuilder();
        final FocusZone[] allZones;
        final FocusZone[] fzs = allZones = FocusZone.getAllZones();
        for (final FocusZone focusz : allZones) {
            if (focusz.isNonPvP() || focusz.isPvP()) {
                final Rectangle focusRect = new Rectangle(focusz.getStartX(), focusz.getStartY(), focusz.getEndX() - focusz.getStartX(), focusz.getEndY() - focusz.getStartY());
                if (focusRect.intersects(bounds)) {
                    toReturn.append(focusz.getName() + " is within the planned area. ");
                }
            }
        }
        if (toReturn.toString().length() > 0) {
            toReturn.append("Settling there is no longer allowed.");
        }
        return toReturn.toString();
    }
    
    public static final Set<Village> getVillagesWithin(final int startX, final int startY, final int endX, final int endY) {
        Rectangle perimRect;
        final Rectangle bounds = perimRect = new Rectangle(startX, startY, endX - startX, endY - startY);
        final Set<Village> toReturn = new HashSet<Village>();
        for (final Village village : Villages.villages.values()) {
            perimRect = new Rectangle(village.startx, village.starty, village.getDiameterX(), village.getDiameterY());
            if (perimRect.intersects(bounds)) {
                toReturn.add(village);
            }
        }
        return toReturn;
    }
    
    public static Map<Village, String> canFoundVillage(final int sizeW, final int sizeE, final int sizeN, final int sizeS, final int tokenx, final int tokeny, final int desiredPerimeter, final boolean surfaced, @Nullable final Village original, final Creature founder) {
        final int startpx = Zones.safeTileX(tokenx - sizeW - 5 - desiredPerimeter);
        final int startpy = Zones.safeTileY(tokeny - sizeN - 5 - desiredPerimeter);
        final int endpy = Zones.safeTileX(tokeny + sizeS + 1 + 5 + desiredPerimeter);
        final int endpx = Zones.safeTileY(tokenx + sizeE + 1 + 5 + desiredPerimeter);
        Rectangle perimRect;
        final Rectangle bounds = perimRect = new Rectangle(startpx, startpy, endpx - startpx, endpy - startpy);
        final Map<Village, String> decliners = new Hashtable<Village, String>();
        final boolean allianceOnly = Servers.localServer.PVPSERVER && !Servers.localServer.isChallengeOrEpicServer();
        final Rectangle allianceBounds = allianceOnly ? new Rectangle(Zones.safeTileX(startpx - 100), Zones.safeTileY(startpy - 100), endpx - startpx + 200, endpy - startpy + 200) : bounds;
        boolean accept = false;
        boolean prohibited = false;
        for (final Village village : Villages.villages.values()) {
            if (village != original) {
                final int mindist = 5 + village.getPerimeterSize();
                perimRect = new Rectangle(village.startx - mindist, village.starty - mindist, village.getDiameterX() + mindist * 2, village.getDiameterY() + mindist * 2);
                if (perimRect.intersects(bounds)) {
                    prohibited = true;
                    decliners.put(village, "has perimeter within the planned settlement or its perimeter.");
                }
                else {
                    if (!allianceOnly || original != null || !perimRect.intersects(allianceBounds) || founder == null) {
                        continue;
                    }
                    if (founder.getCitizenVillage() != null && (founder.getCitizenVillage() == village || village.isAlly(founder))) {
                        accept = true;
                    }
                    else {
                        if (founder.getCitizenVillage() != null && founder.getCitizenVillage() == village && village.isAlly(founder)) {
                            continue;
                        }
                        decliners.put(village, "requires " + founder.getName() + " to be a citizen or ally.");
                    }
                }
            }
        }
        if (prohibited || !accept) {
            return decliners;
        }
        return new Hashtable<Village, String>();
    }
    
    public static Village getVillageWithPerimeterAt(final int tilex, final int tiley, final boolean surfaced) {
        for (final Village village : Villages.villages.values()) {
            final int mindist = 5 + village.getPerimeterSize();
            final Rectangle perimRect = new Rectangle(village.startx - mindist, village.starty - mindist, village.endx - village.startx + (1 + mindist * 2), village.endy - village.starty + (1 + mindist * 2));
            if (perimRect.contains(tilex, tiley)) {
                return village;
            }
        }
        return null;
    }
    
    public static Village doesNotAllowAction(final Creature performer, final int action, final int tilex, final int tiley, final boolean surfaced) {
        if (!Servers.localServer.HOMESERVER) {
            return null;
        }
        if (performer.getKingdomId() != Servers.localServer.KINGDOM) {
            return null;
        }
        if (performer.getPower() > 1) {
            return null;
        }
        if (performer.getKingdomTemplateId() == 3) {
            return null;
        }
        final VolaTile t = Zones.getTileOrNull(tilex, tiley, surfaced);
        if (t != null && t.getVillage() != null) {
            return null;
        }
        final Village v = getVillageWithPerimeterAt(tilex, tiley, surfaced);
        if (v != null && !v.isCitizen(performer) && !v.isAlly(performer)) {
            return v;
        }
        return null;
    }
    
    public static final Village doesNotAllowBuildAction(final Creature performer, final int action, final int tilex, final int tiley, final boolean surfaced) {
        if (performer.getPower() > 1) {
            return null;
        }
        final VolaTile t = Zones.getTileOrNull(tilex, tiley, surfaced);
        if (t != null) {
            final Village village = t.getVillage();
            if (village != null) {
                final VillageRole role = village.getRoleFor(performer);
                if (role != null) {
                    if (role.mayBuild()) {
                        return null;
                    }
                    return village;
                }
            }
        }
        final Village v = getVillageWithPerimeterAt(tilex, tiley, surfaced);
        if (v != null && !v.isCitizen(performer) && !v.isAlly(performer)) {
            return v;
        }
        return null;
    }
    
    public static Item isAltarOnDeed(final int sizeW, final int sizeE, final int sizeN, final int sizeS, final int tokenx, final int tokeny, final boolean surfaced) {
        final int startx = Math.max(0, tokenx - sizeW);
        final int starty = Math.max(0, tokeny - sizeN);
        final int endy = Math.min((1 << Constants.meshSize) - 1, tokeny + sizeS);
        for (int endx = Math.min((1 << Constants.meshSize) - 1, tokenx + sizeE), x = startx; x <= endx; ++x) {
            for (int y = starty; y <= endy; ++y) {
                final VolaTile t = Zones.getTileOrNull(x, y, surfaced);
                if (t != null) {
                    final Item[] items = t.getItems();
                    for (int i = 0; i < items.length; ++i) {
                        if (!items[i].isUnfinished() && (items[i].isNonDeedable() || (items[i].isRoyal() && items[i].isNoTake()) || (items[i].isEpicTargetItem() && Servers.localServer.PVPSERVER))) {
                            return items[i];
                        }
                    }
                }
            }
        }
        return null;
    }
    
    public static Object isAggOnDeed(@Nullable final Village currVill, final Creature responder, final int sizeW, final int sizeE, final int sizeN, final int sizeS, final int tokenx, final int tokeny, final boolean surfaced) {
        final int startx = Math.max(0, tokenx - sizeW);
        final int starty = Math.max(0, tokeny - sizeN);
        final int endy = Zones.safeTileY(tokeny + sizeS);
        for (int endx = Zones.safeTileX(tokenx + sizeE), x = startx; x <= endx; ++x) {
            for (int y = starty; y <= endy; ++y) {
                final Den den = Dens.getDen(x, y);
                if (den != null) {
                    try {
                        final CreatureTemplate template = CreatureTemplateFactory.getInstance().getTemplate(den.getTemplateId());
                        if (responder.getPower() >= 2) {
                            responder.getCommunicator().sendSafeServerMessage(template.getName() + " Den found at " + x + "," + y + ".");
                        }
                        if (!template.isUnique() || Creatures.getInstance().creatureWithTemplateExists(den.getTemplateId())) {
                            return den;
                        }
                    }
                    catch (NoSuchCreatureTemplateException nst) {
                        Villages.logger.log(Level.WARNING, den.getTemplateId() + ":" + nst.getMessage(), nst);
                        if (responder.getPower() >= 2) {
                            responder.getCommunicator().sendSafeServerMessage("Den with unknown template ID: " + den.getTemplateId() + " found at " + x + ", " + y + ".");
                        }
                        else {
                            responder.getCommunicator().sendSafeServerMessage("An invalid creature den was found. Please use /support to ask a GM for help to deal with this issue.");
                        }
                        return den;
                    }
                }
                final VolaTile t = Zones.getTileOrNull(x, y, surfaced);
                if (t != null && (currVill == null || t.getVillage() != currVill)) {
                    final Creature[] crets = t.getCreatures();
                    for (int i = 0; i < crets.length; ++i) {
                        if ((crets[i].getAttitude(responder) == 2 && (crets[i].getBaseCombatRating() > 5.0f || crets[i].isPlayer())) || crets[i].isUnique()) {
                            if (responder.getPower() >= 2) {
                                responder.getCommunicator().sendSafeServerMessage(crets[i].getName() + " agro Creature found at " + x + "," + y + ".");
                            }
                            return crets[i];
                        }
                    }
                }
            }
        }
        return null;
    }
    
    public static boolean canExpandVillage(final int size, final Item token) throws NoSuchVillageException {
        final Village vill = getVillage(token.getData2());
        final int tilex = vill.getStartX();
        final int tiley = vill.getStartY();
        final boolean surfaced = vill.isOnSurface();
        final int startx = Math.max(0, tilex - size);
        final int starty = Math.max(0, tiley - size);
        final int endx = Math.min((1 << Constants.meshSize) - 1, tilex + size);
        final int endy = Math.min((1 << Constants.meshSize) - 1, tiley + size);
        for (int x = startx; x <= endx; x += 5) {
            for (int y = starty; y <= endy; y += 5) {
                final Village check = Zones.getVillage(x, y, surfaced);
                if (check != null && !check.equals(vill)) {
                    return false;
                }
            }
        }
        return true;
    }
    
    public static void generateDeadVillage(final Player performer, final boolean sendFeedback) throws IOException {
        int centerX = -1;
        int centerY = -1;
        boolean gotLocation = false;
        while (!gotLocation) {
            final int testX = Server.rand.nextInt((int)(Zones.worldTileSizeX * 0.8f)) + (int)(Zones.worldTileSizeX * 0.1f);
            final int testY = Server.rand.nextInt((int)(Zones.worldTileSizeY * 0.8f)) + (int)(Zones.worldTileSizeY * 0.1f);
            if (Tiles.decodeHeight(Server.surfaceMesh.getTile(testX, testY)) <= 0) {
                continue;
            }
            centerX = testX;
            centerY = testY;
            gotLocation = true;
        }
        final int sizeX = Server.rand.nextInt(30) * ((Server.rand.nextInt(4) == 0) ? 3 : 1) + 5;
        int sizeY = Math.max(sizeX / 4, Math.min(sizeX * 4, Server.rand.nextInt(30) * ((Server.rand.nextInt(4) == 0) ? 3 : 1) + 5));
        sizeY = Math.max(5, sizeY);
        final int startx = centerX - sizeX;
        final int starty = centerY - sizeY;
        final int endx = centerX + sizeX;
        final int endy = centerY + sizeY;
        final String name = StringUtilities.raiseFirstLetterOnly(generateGenericVillageName());
        final String founderName = StringUtilities.raiseFirstLetterOnly(Server.rand.nextBoolean() ? Offspring.getRandomFemaleName() : Offspring.getRandomMaleName());
        final String mayorName = StringUtilities.raiseFirstLetterOnly(Server.rand.nextBoolean() ? founderName : (Server.rand.nextBoolean() ? Offspring.getRandomFemaleName() : Offspring.getRandomMaleName()));
        final long creationDate = System.currentTimeMillis() - 2419200000L * Server.rand.nextInt(60);
        final long deedid = WurmId.getNextItemId();
        final long disbandDate = (long)Math.min(System.currentTimeMillis() - 2419200000L, Math.max(creationDate + 2419200000L, creationDate + (System.currentTimeMillis() - creationDate) * Server.rand.nextFloat()));
        final long lastLogin = Math.max(creationDate + 2419200000L, disbandDate - 2419200000L * Server.rand.nextInt(6));
        final byte kingdom = Servers.localServer.HOMESERVER ? Servers.localServer.KINGDOM : ((byte)(Server.rand.nextInt(4) + 1));
        Connection dbcon = null;
        PreparedStatement ps = null;
        final ResultSet rs = null;
        try {
            dbcon = DbConnector.getZonesDbCon();
            ps = dbcon.prepareStatement("INSERT INTO VILLAGES (NAME,FOUNDER,MAYOR,CREATIONDATE,STARTX,ENDX,STARTY,ENDY,DEEDID,LASTLOGIN,KINGDOM,DISBAND,DISBANDED,DEVISE) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?)", 2);
            ps.setString(1, name);
            ps.setString(2, founderName);
            ps.setString(3, mayorName);
            ps.setLong(4, creationDate);
            ps.setInt(5, startx);
            ps.setInt(6, endx);
            ps.setInt(7, starty);
            ps.setInt(8, endy);
            ps.setLong(9, deedid);
            ps.setLong(10, lastLogin);
            ps.setByte(11, kingdom);
            ps.setLong(12, disbandDate);
            ps.setBoolean(13, true);
            ps.setString(14, "A settlement like no other.");
            ps.executeUpdate();
        }
        catch (SQLException sqx) {
            throw new IOException(sqx);
        }
        finally {
            DbUtilities.closeDatabaseObjects(ps, rs);
            DbConnector.returnConnection(dbcon);
        }
        final DeadVillage dv = new DeadVillage(deedid, startx, starty, endx, endy, name, founderName, mayorName, creationDate, disbandDate, lastLogin, kingdom);
        Villages.deadVillages.put(deedid, dv);
        performer.sendToLoggers("Generated a dead village at " + centerX + "," + centerY + ".");
        if (sendFeedback) {
            performer.getCommunicator().sendNormalServerMessage("Dead Village \"" + name + "\" created at " + centerX + "," + centerY + ".");
        }
    }
    
    private static String generateGenericVillageName() {
        final ArrayList<String> genericEndings = new ArrayList<String>();
        addAllStrings(genericEndings, " Village", " Isle", " Island", " Mountain", " Plains", " Estate", " Beach", " Homestead", " Valley", " Forest", " Farm", " Castle");
        final ArrayList<String> genericSuffix = new ArrayList<String>();
        addAllStrings(genericSuffix, "ford", "borough", "ington", "ton", "stead", "chester", "dale", "ham", "ing", "mouth", "port");
        String toReturn = "";
        switch (Server.rand.nextInt(3)) {
            case 0: {
                toReturn += Offspring.getRandomMaleName();
                break;
            }
            case 1: {
                toReturn += Offspring.getRandomFemaleName();
                break;
            }
            case 2: {
                toReturn += Offspring.getRandomGenericName();
                break;
            }
        }
        if (Server.rand.nextInt(3) == 0) {
            toReturn += genericSuffix.get(Server.rand.nextInt(genericSuffix.size()));
            if (Server.rand.nextBoolean()) {
                toReturn += genericEndings.get(Server.rand.nextInt(genericEndings.size()));
            }
        }
        else {
            toReturn += genericEndings.get(Server.rand.nextInt(genericEndings.size()));
        }
        return toReturn;
    }
    
    private static void addAllStrings(final ArrayList<String> toAddTo, final String... names) {
        for (final String s : names) {
            toAddTo.add(s);
        }
    }
    
    public static void loadDeadVillages() throws IOException {
        Villages.logger.info("Loading dead villages.");
        final long start = System.nanoTime();
        Connection dbcon = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            dbcon = DbConnector.getZonesDbCon();
            ps = dbcon.prepareStatement("SELECT * FROM VILLAGES WHERE DISBANDED=1");
            rs = ps.executeQuery();
            while (rs.next()) {
                final int startx = rs.getInt("STARTX");
                final int starty = rs.getInt("STARTY");
                final int endx = rs.getInt("ENDX");
                final int endy = rs.getInt("ENDY");
                final String name = rs.getString("NAME");
                final String founderName = rs.getString("FOUNDER");
                final String mayorName = rs.getString("MAYOR");
                final long creationDate = rs.getLong("CREATIONDATE");
                final long deedid = rs.getLong("DEEDID");
                final long disband = rs.getLong("DISBAND");
                final long lastLogin = rs.getLong("LASTLOGIN");
                final byte kingdom = rs.getByte("KINGDOM");
                final DeadVillage dv = new DeadVillage(deedid, startx, starty, endx, endy, name, founderName, mayorName, creationDate, disband, lastLogin, kingdom);
                Villages.deadVillages.put(deedid, dv);
            }
        }
        catch (SQLException sqx) {
            throw new IOException(sqx);
        }
        finally {
            DbUtilities.closeDatabaseObjects(ps, rs);
            DbConnector.returnConnection(dbcon);
            final long end = System.nanoTime();
            Villages.logger.info("Loaded " + Villages.deadVillages.size() + " dead villages from the database took " + (end - start) / 1000000.0f + " ms");
        }
    }
    
    public static final void loadVillages() throws IOException {
        Villages.logger.info("Loading villages.");
        final long start = System.nanoTime();
        Connection dbcon = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            dbcon = DbConnector.getZonesDbCon();
            ps = dbcon.prepareStatement("SELECT * FROM VILLAGES WHERE DISBANDED=0");
            rs = ps.executeQuery();
            while (rs.next()) {
                final int id = rs.getInt("ID");
                final int startx = rs.getInt("STARTX");
                final int starty = rs.getInt("STARTY");
                final int endx = rs.getInt("ENDX");
                final int endy = rs.getInt("ENDY");
                final String name = rs.getString("NAME");
                final String founderName = rs.getString("FOUNDER");
                final String mayorName = rs.getString("MAYOR");
                final long creationDate = rs.getLong("CREATIONDATE");
                final long deedid = rs.getLong("DEEDID");
                final boolean surfaced = rs.getBoolean("SURFACED");
                final String devise = rs.getString("DEVISE");
                final boolean democracy = rs.getBoolean("DEMOCRACY");
                final boolean homestead = rs.getBoolean("HOMESTEAD");
                final long tokenid = rs.getLong("TOKEN");
                final long disband = rs.getLong("DISBAND");
                final long disbander = rs.getLong("DISBANDER");
                final long lastLogin = rs.getLong("LASTLOGIN");
                final byte kingdom = rs.getByte("KINGDOM");
                final long upkeep = rs.getLong("UPKEEP");
                final byte settings = rs.getByte("MAYPICKUP");
                final boolean acceptsHomesteads = rs.getBoolean("ACCEPTSHOMESTEADS");
                final int maxcitizens = rs.getInt("MAXCITIZENS");
                final boolean perma = rs.getBoolean("PERMANENT");
                final byte spawnKingdom = rs.getByte("SPAWNKINGDOM");
                final boolean merchants = rs.getBoolean("MERCHANTS");
                final int perimeterTiles = rs.getInt("PERIMETER");
                final boolean aggros = rs.getBoolean("AGGROS");
                final String consumerKeyToUse = rs.getString("TWITKEY");
                final String consumerSecretToUse = rs.getString("TWITSECRET");
                final String applicationToken = rs.getString("TWITAPP");
                final String applicationSecret = rs.getString("TWITAPPSECRET");
                final boolean twitChat = rs.getBoolean("TWITCHAT");
                final boolean twitEnabled = rs.getBoolean("TWITENABLE");
                final float faithWar = rs.getFloat("FAITHWAR");
                final float faithHeal = rs.getFloat("FAITHHEAL");
                final float faithCreate = rs.getFloat("FAITHCREATE");
                final byte spawnSituation = rs.getByte("SPAWNSITUATION");
                final int allianceNumber = rs.getInt("ALLIANCENUMBER");
                final short wins = rs.getShort("HOTAWINS");
                final long lastChangedName = rs.getLong("NAMECHANGED");
                final int villageRep = rs.getInt("VILLAGEREP");
                final String motd = rs.getString("MOTD");
                final Village toAdd = new DbVillage(id, startx, endx, starty, endy, name, founderName, mayorName, deedid, surfaced, democracy, devise, creationDate, homestead, tokenid, disband, disbander, lastLogin, kingdom, upkeep, settings, acceptsHomesteads, merchants, maxcitizens, perma, spawnKingdom, perimeterTiles, aggros, consumerKeyToUse, consumerSecretToUse, applicationToken, applicationSecret, twitChat, twitEnabled, faithWar, faithHeal, faithCreate, spawnSituation, allianceNumber, wins, lastChangedName, motd);
                toAdd.villageReputation = villageRep;
                Villages.villages.put(id, toAdd);
                Kingdoms.getKingdom(kingdom).setExistsHere(true);
                toAdd.loadRoles();
                toAdd.loadVillageMapAnnotations();
                toAdd.loadVillageRecruitees();
                toAdd.plan = new DbGuardPlan(id);
                if (Villages.logger.isLoggable(Level.FINE)) {
                    Villages.logger.fine("Loaded Village ID: " + id + ": " + toAdd);
                }
            }
            for (final Village toAdd2 : Villages.villages.values()) {
                toAdd2.initialize();
                toAdd2.addGates();
                toAdd2.addMineDoors();
                toAdd2.loadReputations();
                toAdd2.plan.fixGuards();
                toAdd2.checkForEnemies();
                toAdd2.loadHistory();
            }
        }
        catch (SQLException sqx) {
            throw new IOException(sqx);
        }
        finally {
            DbUtilities.closeDatabaseObjects(ps, rs);
            DbConnector.returnConnection(dbcon);
            final long end = System.nanoTime();
            Villages.logger.info("Loaded " + Villages.villages.size() + " villages from the database took " + (end - start) / 1000000.0f + " ms");
        }
    }
    
    public static final void loadCitizens() {
        Villages.logger.info("Loading villages citizens.");
        for (final Village toAdd : Villages.villages.values()) {
            toAdd.loadCitizens();
        }
    }
    
    public static final void loadGuards() {
        Villages.logger.info("Loading villages guards.");
        for (final Village toAdd : Villages.villages.values()) {
            toAdd.loadGuards();
        }
    }
    
    static final void createWar(final Village villone, final Village villtwo) {
        final VillageWar newWar = new DbVillageWar(villone, villtwo);
        newWar.save();
        villone.startWar(newWar, true);
        villtwo.startWar(newWar, false);
        HistoryManager.addHistory("", villone.getName() + " and " + villtwo.getName() + " goes to war.");
    }
    
    public static final void declareWar(final Village villone, final Village villtwo) {
        final WarDeclaration newWar = new WarDeclaration(villone, villtwo);
        villone.addWarDeclaration(newWar);
        villtwo.addWarDeclaration(newWar);
    }
    
    public static final void declarePeace(final Creature performer, final Creature accepter, final Village villone, final Village villtwo) {
        villone.declarePeace(performer, accepter, villtwo, true);
        villtwo.declarePeace(performer, accepter, villone, false);
        final VillageWar[] wararr = getWars();
        for (int x = 0; x < wararr.length; ++x) {
            if ((wararr[x].getVillone() == villone && wararr[x].getVilltwo() == villtwo) || (wararr[x].getVilltwo() == villone && wararr[x].getVillone() == villtwo)) {
                removeAndDeleteVillageWar(wararr[x]);
            }
        }
        HistoryManager.addHistory("", villone.getName() + " and " + villtwo.getName() + " make peace.");
    }
    
    private static boolean removeAndDeleteVillageWar(final VillageWar aVillageWar) {
        boolean lVillageWarExisted = false;
        if (aVillageWar != null) {
            Villages.WARS_RW_LOCK.writeLock().lock();
            try {
                lVillageWarExisted = Villages.wars.remove(aVillageWar);
                aVillageWar.delete();
            }
            finally {
                Villages.WARS_RW_LOCK.writeLock().unlock();
            }
        }
        return lVillageWarExisted;
    }
    
    public static final VillageWar[] getWars() {
        Villages.WARS_RW_LOCK.readLock().lock();
        try {
            return Villages.wars.toArray(new VillageWar[Villages.wars.size()]);
        }
        finally {
            Villages.WARS_RW_LOCK.readLock().unlock();
        }
    }
    
    public static final Alliance[] getAlliances() {
        Villages.ALLIANCES_RW_LOCK.readLock().lock();
        try {
            return Villages.alliances.toArray(new Alliance[Villages.alliances.size()]);
        }
        finally {
            Villages.ALLIANCES_RW_LOCK.readLock().unlock();
        }
    }
    
    public static final void loadWars() throws IOException {
        Villages.logger.log(Level.INFO, "Loading all wars.");
        final long start = System.nanoTime();
        Connection dbcon = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        Villages.WARS_RW_LOCK.writeLock().lock();
        try {
            dbcon = DbConnector.getZonesDbCon();
            ps = dbcon.prepareStatement("SELECT * FROM VILLAGEWARS");
            rs = ps.executeQuery();
            int aid = -10;
            while (rs.next()) {
                try {
                    aid = rs.getInt("ID");
                    final Village villone = getVillage(rs.getInt("VILLONE"));
                    final Village villtwo = getVillage(rs.getInt("VILLTWO"));
                    final VillageWar war = new DbVillageWar(villone, villtwo);
                    villone.addWar(war);
                    villtwo.addWar(war);
                    Villages.wars.add(war);
                    if (!Villages.logger.isLoggable(Level.FINE)) {
                        continue;
                    }
                    Villages.logger.fine("Loaded War ID: " + aid + ": " + war);
                }
                catch (NoSuchVillageException nsv) {
                    Villages.logger.log(Level.WARNING, "Failed to load war with id " + aid + "!");
                }
            }
        }
        catch (SQLException sqx) {
            throw new IOException(sqx);
        }
        finally {
            Villages.WARS_RW_LOCK.writeLock().unlock();
            DbUtilities.closeDatabaseObjects(ps, rs);
            DbConnector.returnConnection(dbcon);
            final long end = System.nanoTime();
            Villages.logger.info("Loaded " + Villages.wars.size() + " wars from the database took " + (end - start) / 1000000.0f + " ms");
        }
    }
    
    public static final void loadWarDeclarations() throws IOException {
        Connection dbcon = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        Villages.WARS_RW_LOCK.writeLock().lock();
        try {
            dbcon = DbConnector.getZonesDbCon();
            ps = dbcon.prepareStatement("SELECT * FROM VILLAGEWARDECLARATIONS");
            rs = ps.executeQuery();
            int aid = -10;
            while (rs.next()) {
                try {
                    aid = rs.getInt("ID");
                    final Village villone = getVillage(rs.getInt("VILLONE"));
                    final Village villtwo = getVillage(rs.getInt("VILLTWO"));
                    final long time = rs.getLong("DECLARETIME");
                    final WarDeclaration war = new WarDeclaration(villone, villtwo, time);
                    villone.addWarDeclaration(war);
                    villtwo.addWarDeclaration(war);
                    Villages.wars.add(war);
                }
                catch (NoSuchVillageException nsv) {
                    Villages.logger.log(Level.WARNING, "Failed to load war with id " + aid + "!");
                }
            }
        }
        catch (SQLException sqx) {
            throw new IOException(sqx);
        }
        finally {
            Villages.WARS_RW_LOCK.writeLock().unlock();
            DbUtilities.closeDatabaseObjects(ps, rs);
            DbConnector.returnConnection(dbcon);
        }
    }
    
    public static Village getVillageForCreature(final Creature creature) {
        if (creature == null) {
            return null;
        }
        for (final Village village : Villages.villages.values()) {
            if (village.isCitizen(creature)) {
                return village;
            }
        }
        return null;
    }
    
    public static Village getVillageForCreature(final long wid) {
        if (wid == -10L) {
            return null;
        }
        for (final Village village : Villages.villages.values()) {
            if (village.isCitizen(wid)) {
                return village;
            }
        }
        return null;
    }
    
    public static long getVillageMoney() {
        long toReturn = 0L;
        for (final Village village : Villages.villages.values()) {
            if (village.plan != null) {
                toReturn += village.plan.moneyLeft;
            }
        }
        return toReturn;
    }
    
    public static final int getSizeForDeed(final int templateId) {
        if (templateId == 237 || templateId == 234) {
            return 5;
        }
        if (templateId == 211 || templateId == 253) {
            return 10;
        }
        if (templateId == 238) {
            return 15;
        }
        if (templateId == 239 || templateId == 254) {
            return 20;
        }
        if (templateId == 242) {
            return 50;
        }
        if (templateId == 244) {
            return 100;
        }
        if (templateId == 245) {
            return 200;
        }
        return 5;
    }
    
    public static final Village[] getVillages() {
        Village[] toReturn = new Village[0];
        if (Villages.villages != null) {
            toReturn = Villages.villages.values().toArray(new Village[Villages.villages.size()]);
        }
        return toReturn;
    }
    
    public static int getNumberOfVillages() {
        return Villages.villages.size();
    }
    
    public static final void poll() {
        final long now = System.currentTimeMillis();
        final Village[] aVillages = getVillages();
        final boolean lowerFaith = System.currentTimeMillis() - Villages.lastPolledVillageFaith > 86400000L;
        for (int x = 0; x < aVillages.length; ++x) {
            aVillages[x].poll(now, lowerFaith);
        }
        if (lowerFaith) {
            Villages.lastPolledVillageFaith = System.currentTimeMillis();
        }
    }
    
    public static final Village getCapital(final byte kingdom) {
        final Village[] vills = getVillages();
        for (int x = 0; x < vills.length; ++x) {
            if (vills[x].kingdom == kingdom && vills[x].isCapital()) {
                return vills[x];
            }
        }
        return null;
    }
    
    public static final Village getFirstVillageForKingdom(final byte kingdom) {
        final Village[] vills = getVillages();
        for (int x = 0; x < vills.length; ++x) {
            if (vills[x].kingdom == kingdom) {
                return vills[x];
            }
        }
        return null;
    }
    
    public static final Village getFirstPermanentVillageForKingdom(final byte kingdom) {
        final Village[] vills = getVillages();
        for (int x = 0; x < vills.length; ++x) {
            if (vills[x].kingdom == kingdom && vills[x].isPermanent) {
                return vills[x];
            }
        }
        return null;
    }
    
    public static final Village[] getPermanentVillagesForKingdom(final byte kingdom) {
        final ConcurrentHashMap<Integer, Village> permVills = new ConcurrentHashMap<Integer, Village>();
        for (final Village village : Villages.villages.values()) {
            if (village.isPermanent && village.kingdom == kingdom) {
                permVills.put(village.getId(), village);
            }
        }
        return permVills.values().toArray(new Village[permVills.size()]);
    }
    
    public static final boolean wasLastVillage(final Village village) {
        final Village[] vills = getVillages();
        for (int x = 0; x < vills.length; ++x) {
            if (village.getId() != vills[x].getId() && vills[x].kingdom == village.kingdom) {
                return false;
            }
        }
        return true;
    }
    
    public static final void convertTowers() {
        final Village[] vills = getVillages();
        for (int x = 0; x < vills.length; ++x) {
            vills[x].convertTowersWithinDistance(150);
        }
        for (int x = 0; x < vills.length; ++x) {
            vills[x].convertTowersWithinPerimeter();
        }
    }
    
    public static final Village[] getPermanentVillages(final byte kingdomChecked) {
        final Set<Village> toReturn = new HashSet<Village>();
        final Kingdom kingd = Kingdoms.getKingdom(kingdomChecked);
        if (kingd != null) {
            for (final Village v : Villages.villages.values()) {
                if (v.kingdom == kingdomChecked && (v.isPermanent || (v.isCapital() && kingd.isCustomKingdom()))) {
                    toReturn.add(v);
                }
            }
        }
        return toReturn.toArray(new Village[toReturn.size()]);
    }
    
    public static final Village[] getKosVillagesFor(final long playerId) {
        final Set<Village> toReturn = new HashSet<Village>();
        for (final Village v : Villages.villages.values()) {
            final Reputation rep = v.getReputationObject(playerId);
            if (rep != null) {
                toReturn.add(v);
            }
        }
        return toReturn.toArray(new Village[toReturn.size()]);
    }
    
    @Nullable
    public static final Village getVillageFor(final Item waystone) {
        for (final Village village : Villages.villages.values()) {
            if (village.coversPlus(waystone.getTileX(), waystone.getTileY(), 2)) {
                return village;
            }
        }
        return null;
    }
    
    public static final ArrayList<DeadVillage> getDeadVillagesFor(final int tilex, final int tiley) {
        return getDeadVillagesNear(tilex, tiley, 0);
    }
    
    public static final ArrayList<DeadVillage> getDeadVillagesNear(final int tilex, final int tiley, final int range) {
        final ArrayList<DeadVillage> toReturn = new ArrayList<DeadVillage>();
        for (final DeadVillage dv : Villages.deadVillages.values()) {
            if (dv.getStartX() - range <= tilex && dv.getEndX() + range >= tilex && dv.getStartY() - range <= tiley) {
                if (dv.getEndY() + range < tiley) {
                    continue;
                }
                toReturn.add(dv);
            }
        }
        return toReturn;
    }
    
    public static final DeadVillage getDeadVillage(final long deadVillageId) {
        return Villages.deadVillages.get(deadVillageId);
    }
    
    static {
        villages = new ConcurrentHashMap<Integer, Village>();
        deadVillages = new ConcurrentHashMap<Long, DeadVillage>();
        Villages.logger = Logger.getLogger(Villages.class.getName());
        alliances = new HashSet<Alliance>();
        ALLIANCES_RW_LOCK = new ReentrantReadWriteLock();
        wars = new HashSet<Object>();
        WARS_RW_LOCK = new ReentrantReadWriteLock();
        Villages.TILE_UPKEEP = 20L;
        Villages.TILE_UPKEEP_STRING = new Change(Villages.TILE_UPKEEP).getChangeString();
        Villages.TILE_COST = 100L;
        Villages.TILE_COST_STRING = new Change(Villages.TILE_COST).getChangeString();
        Villages.GUARD_COST = (Servers.localServer.isChallengeOrEpicServer() ? 3 : 2) * 10000;
        Villages.GUARD_COST_STRING = new Change(Villages.GUARD_COST).getChangeString();
        Villages.GUARD_UPKEEP = (Servers.localServer.isChallengeOrEpicServer() ? 3 : 1) * 10000;
        Villages.GUARD_UPKEEP_STRING = new Change(Villages.GUARD_UPKEEP).getChangeString();
        Villages.PERIMETER_COST = 50L;
        Villages.PERIMETER_COST_STRING = new Change(Villages.PERIMETER_COST).getChangeString();
        Villages.PERIMETER_UPKEEP = 5L;
        Villages.PERIMETER_UPKEEP_STRING = new Change(Villages.PERIMETER_UPKEEP).getChangeString();
        Villages.MINIMUM_UPKEEP = 10000L;
        Villages.MINIMUM_UPKEEP_STRING = new Change(Villages.MINIMUM_UPKEEP).getChangeString();
        Villages.lastPolledVillageFaith = System.currentTimeMillis();
    }
}
