// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.zones;

import com.wurmonline.server.players.Player;
import com.wurmonline.mesh.TreeData;
import com.wurmonline.mesh.GrassData;
import com.wurmonline.mesh.FoliageAge;
import com.wurmonline.server.items.NoSuchTemplateException;
import com.wurmonline.server.FailedException;
import com.wurmonline.server.Players;
import com.wurmonline.server.items.ItemFactory;
import com.wurmonline.server.behaviours.Terraforming;
import com.wurmonline.mesh.Tiles;
import com.wurmonline.server.Server;
import com.wurmonline.server.epic.Hota;
import java.sql.ResultSet;
import java.sql.PreparedStatement;
import java.sql.Connection;
import java.sql.Statement;
import com.wurmonline.server.utils.DbUtilities;
import java.sql.SQLException;
import com.wurmonline.server.DbConnector;
import java.util.HashSet;
import java.util.Iterator;
import java.io.IOException;
import java.util.logging.Level;
import com.wurmonline.server.items.Item;
import java.util.logging.Logger;
import java.util.Set;
import com.wurmonline.server.TimeConstants;

public final class FocusZone extends Zone implements TimeConstants
{
    private static final String loadAll = "SELECT * FROM FOCUSZONES";
    private static final String addZone = "INSERT INTO FOCUSZONES (STARTX,STARTY,ENDX,ENDY,TYPE,NAME,DESCRIPTION) VALUES (?,?,?,?,?,?,?)";
    private static final String deleteZone = "DELETE FROM FOCUSZONES WHERE STARTX=? AND STARTY=? AND ENDX=? AND ENDY=? AND TYPE=? AND NAME=?";
    private static final Set<FocusZone> focusZones;
    private static final Logger logger;
    private final byte type;
    public static final byte TYPE_NONE = 0;
    public static final byte TYPE_VOLCANO = 1;
    public static final byte TYPE_PVP = 2;
    public static final byte TYPE_NAME = 3;
    public static final byte TYPE_NAME_POPUP = 4;
    public static final byte TYPE_NON_PVP = 5;
    public static final byte TYPE_PVP_HOTA = 6;
    public static final byte TYPE_PVP_BATTLECAMP = 7;
    public static final byte TYPE_FLATTEN_DIRT = 8;
    public static final byte TYPE_HOUSE_WOOD = 9;
    public static final byte TYPE_HOUSE_STONE = 10;
    public static final byte TYPE_PREM_SPAWN = 11;
    public static final byte TYPE_NO_BUILD = 12;
    public static final byte TYPE_TALLWALLS = 13;
    public static final byte TYPE_FOG = 14;
    public static final byte TYPE_FLATTEN_ROCK = 15;
    public static final byte TYPE_REPLENISH_DIRT = 16;
    public static final byte TYPE_REPLENISH_TREES = 17;
    public static final byte TYPE_REPLENISH_ORES = 18;
    private int polls;
    private Item projectile;
    private int pollSecondLanded;
    private final String name;
    private final String description;
    
    public FocusZone(final int aStartX, final int aEndX, final int aStartY, final int aEndY, final byte zoneType, final String aName, final String aDescription, final boolean save) {
        super(aStartX, aEndX, aStartY, aEndY, true);
        this.polls = 0;
        this.projectile = null;
        this.pollSecondLanded = 0;
        this.name = aName;
        this.description = aDescription;
        this.type = zoneType;
        if (save) {
            try {
                this.save();
                FocusZone.focusZones.add(this);
            }
            catch (IOException iox) {
                FocusZone.logger.log(Level.INFO, iox.getMessage(), iox);
            }
        }
    }
    
    public final String getName() {
        return this.name;
    }
    
    public final String getDescription() {
        return this.description;
    }
    
    public final boolean isPvP() {
        return this.type == 2 || this.type == 6;
    }
    
    public final boolean isNonPvP() {
        return this.type == 5;
    }
    
    public final boolean isNamePopup() {
        return this.type == 4;
    }
    
    public final boolean isName() {
        return this.type == 3 || this.type == 7;
    }
    
    public final boolean isBattleCamp() {
        return this.type == 7;
    }
    
    public final boolean isPvPHota() {
        return this.type == 6;
    }
    
    public final boolean isPremSpawnOnly() {
        return this.type == 11;
    }
    
    public final boolean isNoBuild() {
        return this.type == 12;
    }
    
    public final boolean isFog() {
        return this.type == 14;
    }
    
    public final boolean isType(final byte wantedType) {
        return this.type == wantedType;
    }
    
    @Override
    void load() throws IOException {
    }
    
    public static void pollAll() {
        for (final FocusZone fz : FocusZone.focusZones) {
            fz.poll();
        }
    }
    
    public static final Set<FocusZone> getZonesAt(final int tilex, final int tiley) {
        if (FocusZone.focusZones.size() > 0) {
            final Set<FocusZone> toReturn = new HashSet<FocusZone>();
            for (final FocusZone fz : FocusZone.focusZones) {
                if (fz.covers(tilex, tiley)) {
                    toReturn.add(fz);
                }
            }
            return toReturn;
        }
        return FocusZone.focusZones;
    }
    
    public static final boolean isPvPZoneAt(final int tilex, final int tiley) {
        if (FocusZone.focusZones.size() > 0) {
            for (final FocusZone fz : FocusZone.focusZones) {
                if (fz.covers(tilex, tiley) && fz.isPvP()) {
                    return true;
                }
            }
            return false;
        }
        return false;
    }
    
    public static final boolean isNonPvPZoneAt(final int tilex, final int tiley) {
        if (FocusZone.focusZones.size() > 0) {
            for (final FocusZone fz : FocusZone.focusZones) {
                if (fz.covers(tilex, tiley) && fz.isNonPvP()) {
                    return true;
                }
            }
            return false;
        }
        return false;
    }
    
    public static final boolean isPremSpawnOnlyZoneAt(final int tilex, final int tiley) {
        if (FocusZone.focusZones.size() > 0) {
            for (final FocusZone fz : FocusZone.focusZones) {
                if (fz.covers(tilex, tiley) && fz.isPremSpawnOnly()) {
                    return true;
                }
            }
            return false;
        }
        return false;
    }
    
    public static final boolean isNoBuildZoneAt(final int tilex, final int tiley) {
        if (FocusZone.focusZones.size() > 0) {
            for (final FocusZone fz : FocusZone.focusZones) {
                if (fz.covers(tilex, tiley) && fz.isNoBuild()) {
                    return true;
                }
            }
            return false;
        }
        return false;
    }
    
    public static final boolean isFogZoneAt(final int tilex, final int tiley) {
        if (FocusZone.focusZones.size() > 0) {
            for (final FocusZone fz : FocusZone.focusZones) {
                if (fz.covers(tilex, tiley) && fz.isFog()) {
                    return true;
                }
            }
            return false;
        }
        return false;
    }
    
    public static final boolean isZoneAt(final int tilex, final int tiley, final byte wantedType) {
        if (FocusZone.focusZones.size() > 0) {
            for (final FocusZone fz : FocusZone.focusZones) {
                if (fz.covers(tilex, tiley) && fz.isType(wantedType)) {
                    return true;
                }
            }
            return false;
        }
        return false;
    }
    
    public static final FocusZone[] getAllZones() {
        return FocusZone.focusZones.toArray(new FocusZone[FocusZone.focusZones.size()]);
    }
    
    public static final FocusZone getHotaZone() {
        for (final FocusZone fz : getAllZones()) {
            if (fz.isPvPHota()) {
                return fz;
            }
        }
        return null;
    }
    
    public static void loadAll() {
        final long now = System.nanoTime();
        int numberOfZonesLoaded = 0;
        Connection dbcon = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            dbcon = DbConnector.getZonesDbCon();
            ps = dbcon.prepareStatement("SELECT * FROM FOCUSZONES");
            rs = ps.executeQuery();
            while (rs.next()) {
                final FocusZone fz = new FocusZone(rs.getInt("STARTX"), rs.getInt("ENDX"), rs.getInt("STARTY"), rs.getInt("ENDY"), rs.getByte("TYPE"), rs.getString("NAME"), rs.getString("DESCRIPTION"), false);
                FocusZone.focusZones.add(fz);
                ++numberOfZonesLoaded;
            }
        }
        catch (SQLException sqex) {
            FocusZone.logger.log(Level.WARNING, "Problem loading focus zone, count is " + numberOfZonesLoaded + " due to " + sqex.getMessage(), sqex);
        }
        finally {
            DbUtilities.closeDatabaseObjects(ps, rs);
            DbConnector.returnConnection(dbcon);
            final float lElapsedTime = (System.nanoTime() - now) / 1000000.0f;
            FocusZone.logger.log(Level.INFO, "Loaded " + numberOfZonesLoaded + " focus zones. It took " + lElapsedTime + " millis.");
        }
    }
    
    @Override
    void loadFences() throws IOException {
    }
    
    public void delete() throws IOException {
        if (getHotaZone() == this) {
            Hota.destroyHota();
        }
        FocusZone.focusZones.remove(this);
        Connection dbcon = null;
        PreparedStatement ps = null;
        try {
            dbcon = DbConnector.getZonesDbCon();
            ps = dbcon.prepareStatement("DELETE FROM FOCUSZONES WHERE STARTX=? AND STARTY=? AND ENDX=? AND ENDY=? AND TYPE=? AND NAME=?");
            ps.setInt(1, this.startX);
            ps.setInt(2, this.startY);
            ps.setInt(3, this.endX);
            ps.setInt(4, this.endY);
            ps.setByte(5, this.type);
            ps.setString(6, this.name);
            ps.executeUpdate();
        }
        catch (SQLException sqex) {
            FocusZone.logger.log(Level.WARNING, sqex.getMessage(), sqex);
        }
        finally {
            DbUtilities.closeDatabaseObjects(ps, null);
            DbConnector.returnConnection(dbcon);
        }
    }
    
    @Override
    void save() throws IOException {
        Connection dbcon = null;
        PreparedStatement ps = null;
        try {
            dbcon = DbConnector.getZonesDbCon();
            ps = dbcon.prepareStatement("INSERT INTO FOCUSZONES (STARTX,STARTY,ENDX,ENDY,TYPE,NAME,DESCRIPTION) VALUES (?,?,?,?,?,?,?)");
            ps.setInt(1, this.startX);
            ps.setInt(2, this.startY);
            ps.setInt(3, this.endX);
            ps.setInt(4, this.endY);
            ps.setByte(5, this.type);
            ps.setString(6, this.name);
            ps.setString(7, this.description);
            ps.executeUpdate();
        }
        catch (SQLException sqex) {
            FocusZone.logger.log(Level.WARNING, sqex.getMessage(), sqex);
        }
        finally {
            DbUtilities.closeDatabaseObjects(ps, null);
            DbConnector.returnConnection(dbcon);
        }
    }
    
    public void poll() {
        ++this.polls;
        if (this.type == 1 && this.polls % 5 == 0) {
            boolean foundLava = false;
            for (int x = this.startX; x < this.endX; ++x) {
                for (int y = this.startY; y < this.endY; ++y) {
                    if (Tiles.decodeType(Server.caveMesh.getTile(x, y)) == Tiles.Tile.TILE_CAVE_WALL_LAVA.id) {
                        for (int xx = -1; xx <= 1; ++xx) {
                            for (int yy = -1; yy <= 1; ++yy) {
                                if ((xx != 0 || yy != 0) && (xx == 0 || yy == 0) && !Tiles.isSolidCave(Tiles.decodeType(Server.caveMesh.getTile(x + xx, y + yy)))) {
                                    FocusZone.logger.log(Level.INFO, "Lava flow at " + (x + xx) + "," + (y + yy));
                                    Terraforming.setAsRock(x + xx, y + yy, true, true);
                                    foundLava = true;
                                    break;
                                }
                            }
                        }
                    }
                    if (foundLava) {
                        break;
                    }
                }
            }
            if (this.pollSecondLanded > 0 && this.polls >= this.pollSecondLanded) {
                if (this.projectile != null) {
                    try {
                        final Zone z = Zones.getZone(this.projectile.getTileX(), this.projectile.getTileY(), true);
                        z.addItem(this.projectile);
                        FocusZone.logger.log(Level.INFO, "Added projectile to " + this.projectile.getTileX() + "," + this.projectile.getTileY());
                    }
                    catch (NoSuchZoneException nsz) {
                        FocusZone.logger.log(Level.WARNING, nsz.getMessage(), nsz);
                    }
                    this.projectile = null;
                }
                this.pollSecondLanded = 0;
                this.polls = 0;
            }
            else if (this.polls == 42600L) {
                Server.getInstance().broadCastNormal(this.name + " rumbles.");
            }
            else if (this.polls == 43200L) {
                Server.getInstance().broadCastNormal(this.name + " rumbles intensely.");
            }
            else if (this.polls >= 43200L && Server.rand.nextInt(3600) == 0) {
                try {
                    this.projectile = ItemFactory.createItem(692, 80.0f + Server.rand.nextFloat() * 20.0f, null);
                    final int centerX = this.getStartX() + this.getSize() / 2;
                    final int centerY = this.getStartY() + this.getSize() / 2;
                    final int randX = Zones.safeTileX(centerX - 10 + Server.rand.nextInt(21));
                    final int randY = Zones.safeTileY(centerY - 10 + Server.rand.nextInt(21));
                    final int landX = Zones.safeTileX(randX - 100 + Server.rand.nextInt(200));
                    final int landY = Zones.safeTileY(randY - 100 + Server.rand.nextInt(200));
                    final int secondsInAir = Math.max(5, Math.max(Math.abs(randX - landX), Math.abs(randY - landY)) / 10);
                    this.pollSecondLanded = this.polls + secondsInAir;
                    final float sx = randX * 4 + 2;
                    final float sy = randY * 4 + 2;
                    final float ex = landX * 4 + 2;
                    final float ey = landY * 4 + 2;
                    final float rot = Server.rand.nextFloat() * 360.0f;
                    FocusZone.logger.log(Level.INFO, "Creating projectile from " + randX + "," + randY + " to " + landX + "," + landY);
                    try {
                        final float sh = Zones.calculateHeight(sx, sy, true) - 10.0f;
                        final float eh = Zones.calculateHeight(ex, ey, true);
                        this.projectile.setPosXYZRotation(ex, ey, eh, rot);
                        final Player[] players = Players.getInstance().getPlayers();
                        for (int x2 = 0; x2 < players.length; ++x2) {
                            if (players[x2].isWithinDistanceTo(sx, sy, sh, 500.0f) || players[x2].isWithinDistanceTo(ex, ey, eh, 500.0f)) {
                                players[x2].getCommunicator().sendProjectile(this.projectile.getWurmId(), (byte)3, this.projectile.getModelName(), this.projectile.getName(), this.projectile.getMaterial(), sx, sy, sh, rot, (byte)0, landX, landY, eh, -10L, -10L, secondsInAir, secondsInAir);
                            }
                        }
                    }
                    catch (NoSuchZoneException nsz2) {
                        FocusZone.logger.log(Level.WARNING, nsz2.getMessage(), nsz2);
                        this.projectile = null;
                        this.pollSecondLanded = 0;
                        this.polls = 0;
                    }
                }
                catch (FailedException fe) {
                    FocusZone.logger.log(Level.WARNING, fe.getMessage(), fe);
                    this.projectile = null;
                    this.pollSecondLanded = 0;
                    this.polls = 0;
                }
                catch (NoSuchTemplateException nst) {
                    FocusZone.logger.log(Level.WARNING, nst.getMessage(), nst);
                    this.projectile = null;
                    this.pollSecondLanded = 0;
                    this.polls = 0;
                }
            }
        }
        else if (this.type == 16) {
            if (this.polls % 900L == 0L) {
                final float avgHeight = (Zones.getHeightForNode(this.getStartX(), this.getStartY(), 1) + Zones.getHeightForNode(this.getStartX(), this.getEndY() + 1, 1) + Zones.getHeightForNode(this.getEndX() + 1, this.getStartY(), 1) + Zones.getHeightForNode(this.getEndX() + 1, this.getEndY() + 1, 1)) / 4.0f;
                for (int tileX = this.getStartX() + 1; tileX < this.getEndX(); ++tileX) {
                    for (int tileY = this.getStartY() + 1; tileY < this.getEndY(); ++tileY) {
                        final int tile = Server.surfaceMesh.getTile(tileX, tileY);
                        final byte type = Tiles.decodeType(tile);
                        if (type == Tiles.Tile.TILE_DIRT.id || type == Tiles.Tile.TILE_DIRT_PACKED.id || type == Tiles.Tile.TILE_SAND.id || Tiles.isGrassType(type)) {
                            final short actualHeight = Tiles.decodeHeight(tile);
                            if (actualHeight > avgHeight * 10.0f + 5.0f) {
                                Server.surfaceMesh.setTile(tileX, tileY, Tiles.encode((short)(actualHeight - 1), type, Tiles.decodeData(tile)));
                            }
                            else {
                                if (actualHeight >= avgHeight * 10.0f - 5.0f) {
                                    continue;
                                }
                                Server.surfaceMesh.setTile(tileX, tileY, Tiles.encode((short)(actualHeight + 1), type, Tiles.decodeData(tile)));
                            }
                            Players.getInstance().sendChangedTile(tileX, tileY, true, true);
                            try {
                                final Zone toCheckForChange = Zones.getZone(tileX, tileY, true);
                                toCheckForChange.changeTile(tileX, tileY);
                            }
                            catch (NoSuchZoneException nsz3) {
                                FocusZone.logger.log(Level.INFO, "no such zone?: " + tileX + ", " + tileY, nsz3);
                            }
                        }
                    }
                }
            }
        }
        else if (this.type == 17) {
            if (this.polls % 300L == 0L) {
                for (int tileX2 = this.getStartX() + 1; tileX2 < this.getEndX(); ++tileX2) {
                    for (int tileY2 = this.getStartY() + 1; tileY2 < this.getEndY(); ++tileY2) {
                        final int tile2 = Server.surfaceMesh.getTile(tileX2, tileY2);
                        final byte type2 = Tiles.decodeType(tile2);
                        if (Tiles.isTree(type2)) {
                            final byte age = FoliageAge.getAgeAsByte(Tiles.decodeData(tile2));
                            if (age <= FoliageAge.MATURE_THREE.getAgeId()) {
                                final int newData = Tiles.encodeTreeData((byte)(age + 1), false, false, GrassData.GrowthTreeStage.decodeTileData(Tiles.decodeData(tile2)));
                                Server.surfaceMesh.setTile(tileX2, tileY2, Tiles.encode(Tiles.decodeHeight(tile2), Tiles.decodeType(tile2), (byte)newData));
                                Players.getInstance().sendChangedTile(tileX2, tileY2, true, false);
                            }
                        }
                        else {
                            boolean skip = false;
                            for (int x3 = tileX2 - 1; x3 < tileX2 + 1; ++x3) {
                                for (int y2 = tileY2 - 1; y2 < tileY2 + 1; ++y2) {
                                    if (Tiles.isTree(Tiles.decodeType(Server.surfaceMesh.getTile(x3, y2))) && (tileX2 == 0 || tileY2 == 0)) {
                                        skip = true;
                                    }
                                }
                            }
                            if (!skip) {
                                TreeData.TreeType treeType = TreeData.TreeType.BIRCH;
                                switch (Server.rand.nextInt(5)) {
                                    case 0: {
                                        treeType = TreeData.TreeType.LINDEN;
                                        break;
                                    }
                                    case 1: {
                                        treeType = TreeData.TreeType.PINE;
                                        break;
                                    }
                                    case 2: {
                                        treeType = TreeData.TreeType.WALNUT;
                                        break;
                                    }
                                    case 3: {
                                        treeType = TreeData.TreeType.CEDAR;
                                        break;
                                    }
                                }
                                final int newData2 = Tiles.encodeTreeData(FoliageAge.YOUNG_ONE, false, false, GrassData.GrowthTreeStage.SHORT);
                                Server.setSurfaceTile(tileX2, tileY2, Tiles.decodeHeight(tile2), treeType.asNormalTree(), (byte)newData2);
                                Server.setWorldResource(tileX2, tileY2, 0);
                                Players.getInstance().sendChangedTile(tileX2, tileY2, true, true);
                            }
                        }
                    }
                }
            }
        }
        else if (this.type == 18 && this.polls % 900L == 0L) {
            for (int tileX2 = this.getStartX() + 1; tileX2 < this.getEndX(); ++tileX2) {
                for (int tileY2 = this.getStartY() + 1; tileY2 < this.getEndY(); ++tileY2) {
                    final int tile2 = Server.caveMesh.getTile(tileX2, tileY2);
                    final byte type2 = Tiles.decodeType(tile2);
                    if (Tiles.isOreCave(type2)) {
                        int resource = Server.getCaveResource(tileX2, tileY2);
                        if (resource < 1000) {
                            resource = Server.rand.nextInt(10000) + 10000;
                            Server.setCaveResource(tileX2, tileY2, resource);
                        }
                    }
                }
            }
        }
    }
    
    static {
        focusZones = new HashSet<FocusZone>();
        logger = Logger.getLogger(FocusZone.class.getName());
    }
}
