// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.endgames;

import java.util.HashMap;
import com.wurmonline.server.structures.Structure;
import com.wurmonline.server.villages.Village;
import com.wurmonline.server.behaviours.MethodsCreatures;
import com.wurmonline.server.kingdom.King;
import com.wurmonline.server.kingdom.Kingdoms;
import com.wurmonline.server.items.NotOwnedException;
import com.wurmonline.server.NoSuchPlayerException;
import com.wurmonline.server.creatures.NoSuchCreatureException;
import com.wurmonline.server.creatures.CreaturePos;
import java.util.List;
import java.util.ArrayList;
import com.wurmonline.server.zones.Zone;
import com.wurmonline.server.zones.NoSuchZoneException;
import com.wurmonline.server.villages.Villages;
import com.wurmonline.server.players.Player;
import com.wurmonline.server.players.Titles;
import com.wurmonline.server.Players;
import com.wurmonline.server.HistoryManager;
import com.wurmonline.server.creatures.Creature;
import java.sql.ResultSet;
import java.sql.PreparedStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import com.wurmonline.server.utils.DbUtilities;
import com.wurmonline.server.NoSuchItemException;
import com.wurmonline.server.Items;
import com.wurmonline.server.DbConnector;
import com.wurmonline.server.Constants;
import com.wurmonline.server.Servers;
import java.util.Set;
import java.util.HashSet;
import com.wurmonline.server.zones.VolaTile;
import com.wurmonline.server.zones.FocusZone;
import com.wurmonline.mesh.Tiles;
import com.wurmonline.server.Server;
import javax.annotation.Nullable;
import java.util.Iterator;
import com.wurmonline.server.items.Item;
import com.wurmonline.server.FailedException;
import com.wurmonline.server.items.NoSuchTemplateException;
import com.wurmonline.server.items.ItemFactory;
import com.wurmonline.server.zones.Zones;
import java.util.logging.Level;
import com.wurmonline.server.kingdom.Kingdom;
import java.util.LinkedList;
import java.util.logging.Logger;
import java.util.Map;
import com.wurmonline.server.TimeConstants;
import com.wurmonline.shared.constants.Enchants;
import com.wurmonline.server.items.ItemTypes;
import com.wurmonline.server.MiscConstants;

public final class EndGameItems implements MiscConstants, ItemTypes, Enchants, TimeConstants
{
    public static final Map<Long, EndGameItem> altars;
    private static final Map<Long, EndGameItem> artifacts;
    private static final Logger logger;
    private static final String LOAD_ENDGAMEITEMS = "SELECT * FROM ENDGAMEITEMS";
    private static float posx;
    private static float posy;
    private static int tileX;
    private static int tileY;
    private static final LinkedList<Kingdom> missingCrowns;
    public static final byte chargeDecay = 10;
    private static long lastRechargedItem;
    
    public static final void createAltars() {
        EndGameItems.logger.log(Level.INFO, "Creating altars.");
        boolean found = false;
        int startX = (Zones.worldTileSizeX - 10) / 2;
        int startY = Math.min(Zones.worldTileSizeY / 20, 300);
        int tries = 0;
        while (!found && tries < 1000) {
            ++tries;
            final float posz = findPlacementTile(startX, startY);
            if (posz <= 0.0f) {
                startX += Math.min(Zones.worldTileSizeX / 20, 300);
                if (startX >= Zones.worldTileSizeX - Math.min(Zones.worldTileSizeX / 20, 100)) {
                    startX = (Zones.worldTileSizeX - 10) / 2;
                    startY += Math.min(Zones.worldTileSizeY / 20, 100);
                }
                if (startY >= Zones.worldTileSizeY - Math.min(Zones.worldTileSizeY / 20, 100)) {
                    break;
                }
                continue;
            }
            else {
                found = true;
            }
        }
        if (!found) {
            EndGameItems.logger.log(Level.WARNING, "Failed to locate a good spot to create holy altar. Exiting.");
            return;
        }
        EndGameItems.posx = EndGameItems.tileX << 2;
        EndGameItems.posy = EndGameItems.tileY << 2;
        try {
            final Item holy = ItemFactory.createItem(327, 90.0f, EndGameItems.posx, EndGameItems.posy, 180.0f, true, (byte)0, -10L, null);
            holy.bless(1);
            holy.enchant((byte)5);
            final EndGameItem eg = new EndGameItem(holy, true, (short)68, true);
            EndGameItems.altars.put(new Long(eg.getWurmid()), eg);
            EndGameItems.logger.log(Level.INFO, "Created holy altar at " + EndGameItems.posx + ", " + EndGameItems.posy + ".");
        }
        catch (NoSuchTemplateException nst) {
            EndGameItems.logger.log(Level.WARNING, nst.getMessage(), nst);
        }
        catch (FailedException fe) {
            EndGameItems.logger.log(Level.WARNING, fe.getMessage(), fe);
        }
        EndGameItems.tileX = 0;
        EndGameItems.tileY = 0;
        found = false;
        startX = (Zones.worldTileSizeX - 10) / 2;
        startY = Math.max(Zones.worldTileSizeY - 300, Zones.worldTileSizeY - Zones.worldTileSizeY / 20);
        tries = 0;
        while (!found && tries < 1000) {
            ++tries;
            final float posz = findPlacementTile(startX, startY);
            if (posz <= 0.0f) {
                startX += Math.min(Zones.worldTileSizeX / 20, 300);
                if (startX >= Zones.worldTileSizeX - Math.min(Zones.worldTileSizeX / 20, 100)) {
                    startX = (Zones.worldTileSizeX - 10) / 2;
                    startY -= Math.min(Zones.worldTileSizeY / 20, 100);
                }
                if (startY <= 0) {
                    break;
                }
                continue;
            }
            else {
                found = true;
            }
        }
        if (!found) {
            EndGameItems.logger.log(Level.WARNING, "Failed to locate a good spot to create unholy altar. Exiting.");
            return;
        }
        EndGameItems.posx = EndGameItems.tileX << 2;
        EndGameItems.posy = EndGameItems.tileY << 2;
        try {
            final Item unholy = ItemFactory.createItem(328, 90.0f, EndGameItems.posx, EndGameItems.posy, 180.0f, true, (byte)0, -10L, null);
            unholy.bless(4);
            unholy.enchant((byte)8);
            final EndGameItem eg = new EndGameItem(unholy, false, (short)68, true);
            EndGameItems.altars.put(new Long(eg.getWurmid()), eg);
            EndGameItems.logger.log(Level.INFO, "Created unholy altar at " + EndGameItems.posx + ", " + EndGameItems.posy + ".");
        }
        catch (NoSuchTemplateException nst) {
            EndGameItems.logger.log(Level.WARNING, nst.getMessage(), nst);
        }
        catch (FailedException fe) {
            EndGameItems.logger.log(Level.WARNING, fe.getMessage(), fe);
        }
    }
    
    @Nullable
    public static final EndGameItem getEvilAltar() {
        if (EndGameItems.altars != null) {
            for (final EndGameItem eg : EndGameItems.altars.values()) {
                if (eg.getItem().getTemplateId() == 328) {
                    return eg;
                }
            }
        }
        return null;
    }
    
    @Nullable
    public static final EndGameItem getGoodAltar() {
        if (EndGameItems.altars != null) {
            for (final EndGameItem eg : EndGameItems.altars.values()) {
                if (eg.getItem().getTemplateId() == 327) {
                    return eg;
                }
            }
        }
        return null;
    }
    
    public static final float findPlacementTile(final int tx, final int ty) {
        float maxZ = 0.0f;
        if (Zones.isWithinDuelRing(tx, ty, tx + 20, ty + 20)) {
            return maxZ;
        }
        for (int x = 0; x < 20; ++x) {
            for (int y = 0; y < 20; ++y) {
                final int tile = Server.surfaceMesh.getTile(tx + x, ty + y);
                final float z = Tiles.decodeHeight(tile);
                final byte ttype = Tiles.decodeType(tile);
                if (ttype != Tiles.Tile.TILE_ROCK.id && ttype != Tiles.Tile.TILE_CLIFF.id && ttype != Tiles.Tile.TILE_HOLE.id && z > 0.0f && z > maxZ && z < 700.0f) {
                    EndGameItems.tileX = tx + x;
                    EndGameItems.tileY = ty + y;
                    maxZ = z;
                }
            }
        }
        return maxZ;
    }
    
    public static final void createArtifacts() {
        try {
            final Item rod = ItemFactory.createItem(329, 90.0f, (byte)3, null);
            rod.bless(1);
            rod.enchant((byte)5);
            placeArtifact(rod);
            EndGameItem eg = new EndGameItem(rod, true, (short)69, true);
            EndGameItems.artifacts.put(new Long(eg.getWurmid()), eg);
            final Item crownmight = ItemFactory.createItem(330, 90.0f, (byte)3, null);
            crownmight.bless(2);
            crownmight.enchant((byte)6);
            placeArtifact(crownmight);
            eg = new EndGameItem(crownmight, true, (short)69, true);
            EndGameItems.artifacts.put(new Long(eg.getWurmid()), eg);
            final Item charmOfFo = ItemFactory.createItem(331, 90.0f, (byte)3, null);
            charmOfFo.bless(1);
            charmOfFo.enchant((byte)5);
            placeArtifact(charmOfFo);
            eg = new EndGameItem(charmOfFo, true, (short)69, true);
            EndGameItems.artifacts.put(new Long(eg.getWurmid()), eg);
            final Item vynorasEye = ItemFactory.createItem(332, 90.0f, (byte)3, null);
            vynorasEye.bless(3);
            vynorasEye.enchant((byte)7);
            placeArtifact(vynorasEye);
            eg = new EndGameItem(vynorasEye, true, (short)69, true);
            EndGameItems.artifacts.put(new Long(eg.getWurmid()), eg);
            final Item vynorasEar = ItemFactory.createItem(333, 90.0f, (byte)3, null);
            vynorasEar.bless(3);
            vynorasEar.enchant((byte)7);
            placeArtifact(vynorasEar);
            eg = new EndGameItem(vynorasEar, true, (short)69, true);
            EndGameItems.artifacts.put(new Long(eg.getWurmid()), eg);
            final Item vynorasMouth = ItemFactory.createItem(334, 90.0f, (byte)3, null);
            vynorasMouth.bless(3);
            vynorasEar.enchant((byte)7);
            placeArtifact(vynorasMouth);
            eg = new EndGameItem(vynorasMouth, true, (short)69, true);
            EndGameItems.artifacts.put(new Long(eg.getWurmid()), eg);
            final Item fingerOfFo = ItemFactory.createItem(335, 90.0f, (byte)3, null);
            fingerOfFo.bless(1);
            fingerOfFo.enchant((byte)5);
            placeArtifact(fingerOfFo);
            eg = new EndGameItem(fingerOfFo, true, (short)69, true);
            EndGameItems.artifacts.put(new Long(eg.getWurmid()), eg);
            final Item swordOfMagranon = ItemFactory.createItem(336, 90.0f, (byte)3, null);
            swordOfMagranon.bless(2);
            swordOfMagranon.enchant((byte)4);
            placeArtifact(swordOfMagranon);
            eg = new EndGameItem(swordOfMagranon, true, (short)69, true);
            EndGameItems.artifacts.put(new Long(eg.getWurmid()), eg);
            final Item hammerOfMagranon = ItemFactory.createItem(337, 90.0f, (byte)3, null);
            hammerOfMagranon.bless(2);
            hammerOfMagranon.enchant((byte)4);
            placeArtifact(hammerOfMagranon);
            eg = new EndGameItem(hammerOfMagranon, true, (short)69, true);
            EndGameItems.artifacts.put(new Long(eg.getWurmid()), eg);
            final Item libilasScale = ItemFactory.createItem(338, 90.0f, (byte)3, null);
            libilasScale.bless(4);
            libilasScale.enchant((byte)8);
            placeArtifact(libilasScale);
            eg = new EndGameItem(libilasScale, false, (short)69, true);
            EndGameItems.artifacts.put(new Long(eg.getWurmid()), eg);
            final Item orbOfDoom = ItemFactory.createItem(339, 90.0f, (byte)3, null);
            orbOfDoom.bless(4);
            orbOfDoom.enchant((byte)8);
            placeArtifact(orbOfDoom);
            eg = new EndGameItem(orbOfDoom, false, (short)69, true);
            EndGameItems.artifacts.put(new Long(eg.getWurmid()), eg);
            final Item sceptreOfAscension = ItemFactory.createItem(340, 90.0f, (byte)3, null);
            sceptreOfAscension.bless(4);
            sceptreOfAscension.enchant((byte)1);
            placeArtifact(sceptreOfAscension);
            eg = new EndGameItem(sceptreOfAscension, false, (short)69, true);
            EndGameItems.artifacts.put(new Long(eg.getWurmid()), eg);
        }
        catch (NoSuchTemplateException nst) {
            EndGameItems.logger.log(Level.WARNING, "Failed to create item: " + nst.getMessage(), nst);
        }
        catch (FailedException fe) {
            EndGameItems.logger.log(Level.WARNING, "Failed to create item: " + fe.getMessage(), fe);
        }
    }
    
    public static final void placeArtifact(final Item artifact) {
        boolean found = false;
        while (!found) {
            final int x = Server.rand.nextInt(Zones.worldTileSizeX);
            final int y = Server.rand.nextInt(Zones.worldTileSizeX);
            final int tile = Server.surfaceMesh.getTile(x, y);
            final int rocktile = Server.rockMesh.getTile(x, y);
            final float th = Tiles.decodeHeightAsFloat(tile);
            final float rh = Tiles.decodeHeightAsFloat(rocktile);
            final FocusZone hoderZone = FocusZone.getHotaZone();
            assert hoderZone != null;
            float seth = 0.0f;
            if (th <= 4.0f || rh <= 4.0f) {
                continue;
            }
            if (th - rh >= 1.0f) {
                seth = Math.max(1, Server.rand.nextInt((int)(th * 10.0f - 5.0f - rh * 10.0f)));
            }
            if (seth <= 0.0f) {
                continue;
            }
            final VolaTile t = Zones.getTileOrNull(x, y, true);
            if (t != null && (t.getStructure() != null || t.getVillage() != null || t.getZone() == hoderZone)) {
                continue;
            }
            seth /= 10.0f;
            found = true;
            artifact.setPosXYZ((x << 2) + 2, (y << 2) + 2, rh + seth);
            artifact.setAuxData((byte)30);
            EndGameItems.logger.log(Level.INFO, "Placed " + artifact.getName() + " at " + x + "," + y + " at height " + (rh + seth) + " rockheight=" + rh + " tileheight=" + th);
        }
    }
    
    public static final Item[] getArtifactDugUp(final int x, final int y, final float height, final boolean allCornersRock) {
        final Set<Item> found = new HashSet<Item>();
        for (final EndGameItem artifact : EndGameItems.artifacts.values()) {
            if (artifact.getItem().getZoneId() == -10L && artifact.getItem().getOwnerId() == -10L && (int)artifact.getItem().getPosX() >> 2 == x && (int)artifact.getItem().getPosY() >> 2 == y && (height <= artifact.getItem().getPosZ() || allCornersRock)) {
                found.add(artifact.getItem());
                artifact.setLastMoved(System.currentTimeMillis());
            }
        }
        return found.toArray(new Item[found.size()]);
    }
    
    public static final EndGameItem getArtifactAtTile(final int x, final int y) {
        for (final EndGameItem artifact : EndGameItems.artifacts.values()) {
            if (artifact.getItem().getZoneId() == -10L && artifact.getItem().getOwnerId() == -10L && (int)artifact.getItem().getPosX() >> 2 == x && (int)artifact.getItem().getPosY() >> 2 == y) {
                return artifact;
            }
        }
        return null;
    }
    
    public static final void deleteEndGameItem(final EndGameItem eg) {
        if (eg != null) {
            if (eg.getItem().isHugeAltar()) {
                EndGameItems.altars.remove(new Long(eg.getWurmid()));
            }
            else if (eg.getItem().isArtifact()) {
                EndGameItems.artifacts.remove(new Long(eg.getWurmid()));
            }
            eg.delete();
        }
    }
    
    public static final void loadEndGameItems() {
        EndGameItems.logger.info("Loading End Game Items.");
        final long now = System.nanoTime();
        if (Servers.localServer.id == 3 || Servers.localServer.id == 12 || Servers.localServer.isChallengeServer() || (Server.getInstance().isPS() && Constants.loadEndGameItems)) {
            Connection dbcon = null;
            PreparedStatement ps = null;
            ResultSet rs = null;
            try {
                dbcon = DbConnector.getItemDbCon();
                ps = dbcon.prepareStatement("SELECT * FROM ENDGAMEITEMS");
                rs = ps.executeQuery();
                long iid = -10L;
                boolean holy = true;
                short type = 0;
                boolean found = false;
                boolean foundAltar = false;
                long lastMoved = 0L;
                while (rs.next()) {
                    iid = rs.getLong("WURMID");
                    holy = rs.getBoolean("HOLY");
                    type = rs.getShort("TYPE");
                    lastMoved = rs.getLong("LASTMOVED");
                    try {
                        final Item item = Items.getItem(iid);
                        final EndGameItem eg = new EndGameItem(item, holy, type, false);
                        eg.lastMoved = lastMoved;
                        if (type == 68) {
                            eg.setLastMoved(System.currentTimeMillis());
                            foundAltar = true;
                            EndGameItems.altars.put(new Long(iid), eg);
                        }
                        else if (type == 69) {
                            found = true;
                            EndGameItems.artifacts.put(new Long(iid), eg);
                            if (!EndGameItems.logger.isLoggable(Level.FINE)) {
                                continue;
                            }
                            EndGameItems.logger.fine("Loaded Artifact, ID: " + iid + ", " + eg);
                        }
                        else {
                            EndGameItems.logger.warning("End Game Items should only be Huge Altars or Artifiacts not type " + type + ", ID: " + iid + ", " + eg);
                        }
                    }
                    catch (NoSuchItemException nsi) {
                        if (Server.getInstance().isPS()) {
                            EndGameItems.logger.log(Level.INFO, "Endgame item missing: " + iid + ". Deleting entry.");
                            EndGameItem.delete(iid);
                            if (type != 68) {
                                continue;
                            }
                            EndGameItems.logger.log(Level.INFO, (holy ? "White Light" : "Black Light") + " altar is missing. Destroy the " + (holy ? "Black Light" : "White Light") + " altar to respawn both.");
                        }
                        else {
                            EndGameItems.logger.log(Level.WARNING, "Endgame item missing: " + iid, nsi);
                        }
                    }
                }
                DbUtilities.closeDatabaseObjects(ps, rs);
                if (!found) {
                    createArtifacts();
                }
                else {
                    setArtifactsInWorld();
                }
                if (!foundAltar) {
                    createAltars();
                }
            }
            catch (SQLException sqx) {
                EndGameItems.logger.log(Level.WARNING, "Failed to load item datas: " + sqx.getMessage(), sqx);
            }
            finally {
                DbUtilities.closeDatabaseObjects(ps, rs);
                DbConnector.returnConnection(dbcon);
            }
        }
        final int numberOfAltars = (EndGameItems.altars != null) ? EndGameItems.altars.size() : 0;
        final int numberOfArtifacts = (EndGameItems.artifacts != null) ? EndGameItems.artifacts.size() : 0;
        EndGameItems.logger.log(Level.INFO, "Loaded " + numberOfAltars + " altars and " + numberOfArtifacts + " artifacts. That took " + (System.nanoTime() - now) / 1000000.0f + " ms.");
    }
    
    public static EndGameItem getEndGameItem(final Item item) {
        if (item.isHugeAltar()) {
            return EndGameItems.altars.get(new Long(item.getWurmId()));
        }
        if (item.isArtifact()) {
            return EndGameItems.artifacts.get(new Long(item.getWurmId()));
        }
        return null;
    }
    
    public static final boolean mayRechargeItem() {
        return System.currentTimeMillis() - EndGameItems.lastRechargedItem > 60000L;
    }
    
    public static final void touchRecharge() {
        EndGameItems.lastRechargedItem = System.currentTimeMillis();
    }
    
    public static final void destroyHugeAltar(final Item altar, final Creature destroyer) {
        final EndGameItem eg = EndGameItems.altars.get(new Long(altar.getWurmId()));
        if (eg != null) {
            Server.getInstance().broadCastAlert("The " + altar.getName() + " has fallen to the hands of " + destroyer.getName() + "!", true, (byte)4);
            HistoryManager.addHistory(destroyer.getName(), "Destroyed the " + altar.getName() + ".");
            if (destroyer.isPlayer()) {
                final float sx = altar.getPosX() - 100.0f;
                final float ex = altar.getPosX() + 100.0f;
                final float sy = altar.getPosY() - 100.0f;
                final float ey = altar.getPosY() + 100.0f;
                for (final Player p : Players.getInstance().getPlayers()) {
                    if (p.getPosX() > sx && p.getPosX() < ex && p.getPosY() > sy && p.getPosY() < ey && p.getKingdomId() == destroyer.getKingdomId()) {
                        p.addTitle(Titles.Title.Altar_Destroyer);
                        if (eg.isHoly()) {
                            p.achievement(356);
                        }
                        else {
                            p.achievement(357);
                        }
                    }
                }
            }
            final Player[] players3;
            final Player[] players = players3 = Players.getInstance().getPlayers();
            for (final Player lPlayer : players3) {
                if (eg.isHoly()) {
                    if (lPlayer.getDeity() != null && lPlayer.getDeity().isHateGod()) {
                        lPlayer.setFarwalkerSeconds((byte)100);
                        lPlayer.healRandomWound(100);
                    }
                    else if (lPlayer.getDeity() != null && !lPlayer.getDeity().isHateGod()) {
                        lPlayer.getCommunicator().sendCombatAlertMessage("Your life force is drained, as it is used to heal the " + altar.getName() + "!");
                        lPlayer.addWoundOfType(null, (byte)9, 1, false, 1.0f, false, 5000.0, 0.0f, 0.0f, false, false);
                    }
                }
                else if (lPlayer.getDeity() != null && !lPlayer.getDeity().isHateGod()) {
                    lPlayer.setFarwalkerSeconds((byte)100);
                    lPlayer.healRandomWound(100);
                }
                else if (lPlayer.getDeity() != null && lPlayer.getDeity().isHateGod()) {
                    lPlayer.getCommunicator().sendCombatAlertMessage("Your life force is drained, as it is used to heal the " + altar.getName() + "!");
                    lPlayer.addWoundOfType(null, (byte)9, 1, false, 1.0f, false, 5000.0, 0.0f, 0.0f, false, false);
                }
            }
            healAndTeleportAltar(eg);
            hideRandomArtifact(eg.isHoly());
        }
    }
    
    private static final void healAndTeleportAltar(final EndGameItem altar) {
        final Item altarItem = altar.getItem();
        altarItem.putInVoid();
        altarItem.setDamage(0.0f);
        final Player[] players;
        final Player[] p = players = Players.getInstance().getPlayers();
        for (final Player lPlayer : players) {
            lPlayer.getCommunicator().sendRemoveEffect(altar.getWurmid());
        }
        boolean found = false;
        final int randX = Zones.worldTileSizeX - 200;
        final int randY = Zones.worldTileSizeY / 2;
        int startX = Zones.safeTileX(100 + Server.rand.nextInt(randX));
        int startY = Zones.safeTileY(100 + Server.rand.nextInt(randY));
        if (!altar.isHoly()) {
            startY = Zones.safeTileY(Zones.worldTileSizeY / 2 + startY);
        }
        int tries = 0;
        float posz = 0.0f;
        while (!found && tries < 1000) {
            ++tries;
            posz = findPlacementTile(startX, startY);
            if (Villages.getVillageWithPerimeterAt(EndGameItems.tileX, EndGameItems.tileY, true) != null) {
                posz = -1.0f;
            }
            if (posz <= 0.0f) {
                startX = Zones.safeTileX(100 + Server.rand.nextInt(randX));
                startY = Zones.safeTileY(100 + Server.rand.nextInt(randY));
                if (altar.isHoly()) {
                    continue;
                }
                startY = Zones.safeTileY(Zones.worldTileSizeY / 2 + startY);
            }
            else {
                found = true;
            }
        }
        if (!found) {
            EndGameItems.logger.log(Level.WARNING, "Failed to locate a good spot to create holy altar. Exiting.");
            return;
        }
        EndGameItems.posx = (EndGameItems.tileX << 2) + 2;
        EndGameItems.posy = (EndGameItems.tileY << 2) + 2;
        altarItem.setPosXYZ(EndGameItems.posx, EndGameItems.posy, posz);
        try {
            final Zone z = Zones.getZone(EndGameItems.tileX, EndGameItems.tileY, true);
            z.addItem(altarItem);
            if (altar.isHoly()) {
                for (final Player lPlayer2 : p) {
                    lPlayer2.getCommunicator().sendAddEffect(altar.getWurmid(), (short)2, altar.getItem().getPosX(), altar.getItem().getPosY(), altar.getItem().getPosZ(), (byte)0);
                }
            }
            else {
                for (final Player lPlayer2 : p) {
                    lPlayer2.getCommunicator().sendAddEffect(altar.getWurmid(), (short)3, altar.getItem().getPosX(), altar.getItem().getPosY(), altar.getItem().getPosZ(), (byte)0);
                }
            }
        }
        catch (NoSuchZoneException nsz) {
            EndGameItems.logger.log(Level.WARNING, nsz.getMessage(), nsz);
        }
    }
    
    private static final void hideRandomArtifact(final boolean holy) {
        final EndGameItem[] arts = EndGameItems.artifacts.values().toArray(new EndGameItem[EndGameItems.artifacts.size()]);
        Item artifactToPlace = null;
        final List<Item> candidates = new ArrayList<Item>();
        for (final EndGameItem lArt : arts) {
            final Item artifact = lArt.getItem();
            if (lArt.isInWorld() && lArt.isHoly() == holy) {
                candidates.add(artifact);
            }
        }
        if (candidates.size() > 0) {
            artifactToPlace = candidates.get(Server.rand.nextInt(candidates.size()));
            try {
                final Item parent = artifactToPlace.getParent();
                parent.dropItem(artifactToPlace.getWurmId(), false);
                placeArtifact(artifactToPlace);
            }
            catch (NoSuchItemException ex) {}
        }
    }
    
    public static final void setArtifactsInWorld() {
        final EndGameItem[] array;
        final EndGameItem[] arts = array = EndGameItems.artifacts.values().toArray(new EndGameItem[EndGameItems.artifacts.size()]);
        for (final EndGameItem lArt : array) {
            final Item artifact = lArt.getItem();
            if (artifact.getOwnerId() != -10L) {
                final CreaturePos stat = CreaturePos.getPosition(artifact.getOwnerId());
                if (stat != null && stat.getPosX() > 0.0f) {
                    try {
                        final Item parent = artifact.getParent();
                        parent.dropItem(artifact.getWurmId(), false);
                        final Zone z = Zones.getZone((int)stat.getPosX() >> 2, (int)stat.getPosY() >> 2, stat.getLayer() >= 0);
                        artifact.setPosXY(stat.getPosX(), stat.getPosY());
                        z.addItem(artifact);
                        EndGameItems.logger.log(Level.INFO, "Zone " + z.getId() + " added " + artifact.getName() + " at " + ((int)stat.getPosX() >> 2) + "," + ((int)stat.getPosY() >> 2));
                    }
                    catch (NoSuchItemException nsi) {
                        EndGameItems.logger.log(Level.WARNING, artifact.getName() + ": " + nsi.getMessage(), nsi);
                    }
                    catch (NoSuchZoneException nsz) {
                        EndGameItems.logger.log(Level.WARNING, artifact.getName() + ": " + nsz.getMessage(), nsz);
                    }
                }
            }
        }
    }
    
    public static final void pollAll() {
        final EndGameItem[] array;
        final EndGameItem[] arts = array = EndGameItems.artifacts.values().toArray(new EndGameItem[EndGameItems.artifacts.size()]);
        for (final EndGameItem lArt : array) {
            if (lArt.isInWorld() && System.currentTimeMillis() - lArt.getLastMoved() > (Servers.isThisATestServer() ? 60000L : 604800000L)) {
                lArt.setLastMoved(System.currentTimeMillis());
                final Item artifact = lArt.getItem();
                if (artifact.getAuxData() <= 0) {
                    moveArtifact(artifact);
                }
                else {
                    artifact.setAuxData((byte)Math.max(0, artifact.getAuxData() - 10));
                    try {
                        if (artifact.getOwner() != -10L) {
                            final Creature owner = Server.getInstance().getCreature(artifact.getOwner());
                            owner.getCommunicator().sendNormalServerMessage(artifact.getName() + " vibrates faintly.");
                        }
                    }
                    catch (NoSuchCreatureException ex) {}
                    catch (NoSuchPlayerException ex2) {}
                    catch (NotOwnedException ex3) {}
                }
            }
        }
    }
    
    private static final void moveArtifact(final Item artifact) {
        try {
            if (artifact.getOwner() != -10L) {
                final Creature owner = Server.getInstance().getCreature(artifact.getOwner());
                owner.getCommunicator().sendNormalServerMessage(artifact.getName() + " disappears. It has fulfilled its mission.");
            }
        }
        catch (NoSuchCreatureException ex) {}
        catch (NoSuchPlayerException ex2) {}
        catch (NotOwnedException ex3) {}
        String act = null;
        switch (Server.rand.nextInt(6)) {
            case 0: {
                act = "is reported to have disappeared.";
                break;
            }
            case 1: {
                act = "is gone missing.";
                break;
            }
            case 2: {
                act = "returned to the depths.";
                break;
            }
            case 3: {
                act = "seems to have decided to leave.";
                break;
            }
            case 4: {
                act = "has found a new location.";
                break;
            }
            default: {
                act = "has vanished.";
                break;
            }
        }
        HistoryManager.addHistory("The " + artifact.getName(), act);
        artifact.putInVoid();
        placeArtifact(artifact);
    }
    
    public static final void destroyArtifacts() {
        final EndGameItem[] array;
        final EndGameItem[] arts = array = EndGameItems.artifacts.values().toArray(new EndGameItem[EndGameItems.artifacts.size()]);
        for (final EndGameItem lArt : array) {
            final Item artifact = lArt.getItem();
            try {
                if (artifact.getOwner() != -10L) {
                    final Creature owner = Server.getInstance().getCreature(artifact.getOwner());
                    owner.getCommunicator().sendNormalServerMessage(artifact.getName() + " disappears. It has fulfilled its mission.");
                }
            }
            catch (NoSuchCreatureException ex) {}
            catch (NoSuchPlayerException ex2) {}
            catch (NotOwnedException ex3) {}
            Items.destroyItem(artifact.getWurmId());
            lArt.destroy();
        }
    }
    
    public static final String locateEndGameItem(final int templateId, final Creature performer) {
        String toReturn = "The artifact was hidden from view by the gods.";
        if (!Servers.localServer.HOMESERVER) {
            EndGameItem itemsearched = null;
            if (templateId == -1) {
                if (Server.rand.nextBoolean()) {
                    EndGameItems.missingCrowns.clear();
                    final Kingdom[] kingdoms = Kingdoms.getAllKingdoms();
                    for (int x = 0; x < kingdoms.length; ++x) {
                        if (kingdoms[x].isCustomKingdom() && kingdoms[x].existsHere()) {
                            final King k = King.getKing(kingdoms[x].getId());
                            if (k == null) {
                                EndGameItems.missingCrowns.add(kingdoms[x]);
                            }
                        }
                    }
                    if (EndGameItems.missingCrowns.size() > 0) {
                        final int crownToLookFor = Server.rand.nextInt(EndGameItems.missingCrowns.size());
                        final Kingdom toLookFor = EndGameItems.missingCrowns.get(crownToLookFor);
                        final Item[] allItems;
                        final Item[] _items = allItems = Items.getAllItems();
                        for (final Item lItem : allItems) {
                            if (lItem.isRoyal() && lItem.getKingdom() == toLookFor.getId()) {
                                itemsearched = new EndGameItem(lItem, false, (short)122, false);
                            }
                        }
                    }
                }
                if (itemsearched == null) {
                    final int s = EndGameItems.artifacts.size();
                    if (s > 0) {
                        final int num = Server.rand.nextInt(s);
                        int x2 = 0;
                        final Iterator<EndGameItem> it = EndGameItems.artifacts.values().iterator();
                        while (it.hasNext()) {
                            itemsearched = it.next();
                            if (x2 == num) {
                                break;
                            }
                            ++x2;
                        }
                    }
                }
            }
            else {
                final Iterator<EndGameItem> it2 = EndGameItems.artifacts.values().iterator();
                while (it2.hasNext()) {
                    itemsearched = it2.next();
                    if (itemsearched.getItem().getTemplateId() == templateId) {
                        break;
                    }
                }
            }
            String name = "artifact";
            if (itemsearched != null && itemsearched.getItem() != null) {
                toReturn = "";
                name = itemsearched.getItem().getName();
                if (itemsearched.getType() == 122) {
                    final Kingdom i = Kingdoms.getKingdom(itemsearched.getItem().getKingdom());
                    if (i != null) {
                        name = itemsearched.getItem().getName() + " of " + i.getName();
                    }
                }
                final int tilex = (int)itemsearched.getItem().getPosX() >> 2;
                final int tiley = (int)itemsearched.getItem().getPosY() >> 2;
                if (itemsearched.getItem().getOwnerId() != -10L) {
                    try {
                        final Creature c = Server.getInstance().getCreature(itemsearched.getItem().getOwnerId());
                        toReturn = toReturn + "The " + name + " is carried by " + c.getName() + ". ";
                        final VolaTile t = c.getCurrentTile();
                        if (t != null) {
                            if (t.getVillage() != null) {
                                toReturn = toReturn + c.getName() + " is in the settlement of " + t.getVillage().getName() + ". ";
                            }
                            if (t.getStructure() != null) {
                                toReturn = toReturn + c.getName() + " is in the house of " + t.getStructure().getName() + ". ";
                            }
                        }
                    }
                    catch (NoSuchCreatureException nsc) {
                        toReturn = toReturn + "In your vision, you can only discern a shadow that carries the " + name + ". ";
                    }
                    catch (NoSuchPlayerException nsp) {
                        toReturn = toReturn + "In your vision, you can only discern a shadow that carries the " + name + ". ";
                    }
                }
                else if (itemsearched.isInWorld()) {
                    final VolaTile t2 = Zones.getTileOrNull(tilex, tiley, itemsearched.getItem().isOnSurface());
                    if (t2 != null) {
                        if (t2.getVillage() != null) {
                            toReturn = toReturn + "The " + name + " is in the settlement of " + t2.getVillage().getName() + ". ";
                        }
                        if (t2.getStructure() != null) {
                            toReturn = toReturn + "The " + name + " is in the house of " + t2.getStructure().getName() + ". ";
                        }
                        try {
                            if (itemsearched.getItem() != null) {
                                final long parentId = itemsearched.getItem().getTopParent();
                                final Item parent = Items.getItem(parentId);
                                if (parent != itemsearched.getItem()) {
                                    toReturn = toReturn + "It is within a " + parent.getName() + ".";
                                }
                            }
                        }
                        catch (NoSuchItemException ex) {}
                        toReturn = toReturn + "The " + name + " is in the wild. ";
                        final VolaTile ct = performer.getCurrentTile();
                        if (ct != null) {
                            final int ctx = ct.tilex;
                            final int cty = ct.tiley;
                            final int mindist = Math.max(Math.abs(tilex - ctx), Math.abs(tiley - cty));
                            final int dir = MethodsCreatures.getDir(performer, tilex, tiley);
                            final String direction = MethodsCreatures.getLocationStringFor(performer.getStatus().getRotation(), dir, "you");
                            toReturn += getDistanceString(mindist, name, direction, true);
                        }
                    }
                    else {
                        try {
                            final Zone z = Zones.getZone(tilex, tiley, true);
                            final Village[] villages = z.getVillages();
                            if (villages.length > 0) {
                                for (final Village lVillage : villages) {
                                    toReturn = toReturn + "The " + name + " is near the settlement of " + lVillage.getName() + ". ";
                                }
                            }
                            else {
                                final Structure[] structs = z.getStructures();
                                if (structs.length > 0) {
                                    for (final Structure lStruct : structs) {
                                        toReturn = toReturn + "The " + name + " is near " + lStruct.getName() + ". ";
                                    }
                                }
                                else {
                                    final VolaTile ct2 = performer.getCurrentTile();
                                    if (ct2 != null) {
                                        final int ctx2 = ct2.tilex;
                                        final int cty2 = ct2.tiley;
                                        final int mindist2 = Math.max(Math.abs(tilex - ctx2), Math.abs(tiley - cty2));
                                        final int dir2 = MethodsCreatures.getDir(performer, tilex, tiley);
                                        final String direction2 = MethodsCreatures.getLocationStringFor(performer.getStatus().getRotation(), dir2, "you");
                                        toReturn += getDistanceString(mindist2, name, direction2, true);
                                    }
                                }
                            }
                        }
                        catch (NoSuchZoneException nsz) {
                            EndGameItems.logger.log(Level.WARNING, "No Zone At " + tilex + ", " + tiley + " surf=true for item " + itemsearched.getItem().getName() + ".", nsz);
                        }
                    }
                }
                else {
                    toReturn = toReturn + "The " + name + " has not yet been revealed. ";
                    final VolaTile ct3 = performer.getCurrentTile();
                    if (ct3 != null) {
                        final int ctx3 = ct3.tilex;
                        final int cty3 = ct3.tiley;
                        final int mindist3 = Math.max(Math.abs(tilex - ctx3), Math.abs(tiley - cty3));
                        final int dir3 = MethodsCreatures.getDir(performer, tilex, tiley);
                        final String direction3 = MethodsCreatures.getLocationStringFor(performer.getStatus().getRotation(), dir3, "you");
                        toReturn += getDistanceString(mindist3, name, direction3, true);
                    }
                }
            }
            return toReturn;
        }
        if (Servers.localServer.serverEast != null && !Servers.localServer.serverEast.HOMESERVER) {
            return "You feel a faint indication far to the east.";
        }
        if (Servers.localServer.serverSouth != null && !Servers.localServer.serverSouth.HOMESERVER) {
            return "You feel a faint indication far to the south.";
        }
        if (Servers.localServer.serverWest != null && !Servers.localServer.serverWest.HOMESERVER) {
            return "You feel a faint indication far to the west.";
        }
        if (Servers.localServer.serverNorth != null && !Servers.localServer.serverNorth.HOMESERVER) {
            return "You feel a faint indication far to the north.";
        }
        return toReturn;
    }
    
    public static final EndGameItem[] getArtifacts() {
        return EndGameItems.artifacts.values().toArray(new EndGameItem[EndGameItems.artifacts.values().size()]);
    }
    
    public static String getEpicPlayerLocateString(final int mindist, final String name, final String direction) {
        String toReturn = "";
        if (mindist == 0) {
            toReturn = toReturn + "You are practically standing on the " + name + "! ";
        }
        else if (mindist < 1) {
            toReturn = toReturn + "The " + name + " is " + direction + " a few steps away! ";
        }
        else if (mindist < 4) {
            toReturn = toReturn + "The " + name + " is " + direction + " a stone's throw away! ";
        }
        else if (mindist < 6) {
            toReturn = toReturn + "The " + name + " is " + direction + " very close. ";
        }
        else if (mindist < 10) {
            toReturn = toReturn + "The " + name + " is " + direction + " pretty close by. ";
        }
        else if (mindist < 20) {
            toReturn = toReturn + "The " + name + " is " + direction + " fairly close by. ";
        }
        else if (mindist < 50) {
            toReturn = toReturn + "The " + name + " is some distance away " + direction + ". ";
        }
        else if (mindist < 200) {
            toReturn = toReturn + "The " + name + " is quite some distance away " + direction + ". ";
        }
        else {
            toReturn += "No such soul found.";
        }
        return toReturn;
    }
    
    public static final String getDistanceString(final int mindist, final String name, final String direction, final boolean includeThe) {
        String toReturn = "";
        if (mindist == 0) {
            toReturn = toReturn + "You are practically standing on the " + name + "! ";
        }
        else if (mindist < 1) {
            toReturn = toReturn + "The " + name + " is " + direction + " a few steps away! ";
        }
        else if (mindist < 4) {
            toReturn = toReturn + "The " + name + " is " + direction + " a stone's throw away! ";
        }
        else if (mindist < 6) {
            toReturn = toReturn + "The " + name + " is " + direction + " very close. ";
        }
        else if (mindist < 10) {
            toReturn = toReturn + "The " + name + " is " + direction + " pretty close by. ";
        }
        else if (mindist < 20) {
            toReturn = toReturn + "The " + name + " is " + direction + " fairly close by. ";
        }
        else if (mindist < 50) {
            toReturn = toReturn + "The " + name + " is some distance away " + direction + ". ";
        }
        else if (mindist < 200) {
            toReturn = toReturn + "The " + name + " is quite some distance away " + direction + ". ";
        }
        else if (mindist < 500) {
            toReturn = toReturn + "The " + name + " is rather a long distance away " + direction + ". ";
        }
        else if (mindist < 1000) {
            toReturn = toReturn + "The " + name + " is pretty far away " + direction + ". ";
        }
        else if (mindist < 2000) {
            toReturn = toReturn + "The " + name + " is far away " + direction + ". ";
        }
        else {
            toReturn = toReturn + "The " + name + " is very far away " + direction + ". ";
        }
        return toReturn;
    }
    
    public static final String locateRandomEndGameItem(final Creature performer) {
        return locateEndGameItem(-1, performer);
    }
    
    public static final void relocateAllEndGameItems() {
        for (final EndGameItem eg : EndGameItems.artifacts.values()) {
            eg.setLastMoved(System.currentTimeMillis());
            moveArtifact(eg.getItem());
        }
        for (final EndGameItem altar : EndGameItems.altars.values()) {
            Items.destroyItem(altar.getItem().getWurmId());
            altar.delete();
        }
        EndGameItems.altars.clear();
    }
    
    static {
        altars = new HashMap<Long, EndGameItem>();
        artifacts = new HashMap<Long, EndGameItem>();
        logger = Logger.getLogger(EndGameItems.class.getName());
        EndGameItems.posx = 0.0f;
        EndGameItems.posy = 0.0f;
        EndGameItems.tileX = 0;
        EndGameItems.tileY = 0;
        missingCrowns = new LinkedList<Kingdom>();
        EndGameItems.lastRechargedItem = 0L;
    }
}
