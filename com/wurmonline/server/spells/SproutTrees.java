// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.spells;

import java.util.List;
import com.wurmonline.server.Players;
import com.wurmonline.mesh.GrassData;
import com.wurmonline.mesh.TreeData;
import com.wurmonline.mesh.Tiles;
import com.wurmonline.server.Server;
import java.util.logging.Level;
import java.util.ArrayList;
import com.wurmonline.server.zones.VolaTile;
import com.wurmonline.server.zones.Zone;
import com.wurmonline.server.zones.NoSuchZoneException;
import com.wurmonline.server.zones.Zones;
import com.wurmonline.server.creatures.Creature;
import com.wurmonline.server.skills.Skill;
import java.util.logging.Logger;

public class SproutTrees extends KarmaSpell
{
    private static final Logger logger;
    public static final int RANGE = 24;
    
    public SproutTrees() {
        super("Sprout trees", 634, 30, 400, 32, 1, 300000L);
        this.description = "sprouts trees on grass or dirt tiles in the area.";
        this.offensive = false;
        this.targetTile = true;
    }
    
    @Override
    boolean precondition(final Skill castSkill, final Creature performer, final int tilex, final int tiley, final int layer) {
        if (layer < 0) {
            performer.getCommunicator().sendNormalServerMessage("You need to be on the surface to cast this spell");
            return false;
        }
        try {
            final Zone zone = Zones.getZone(tilex, tiley, true);
            final VolaTile tile = zone.getOrCreateTile(tilex, tiley);
            if (tile.getVillage() != null) {
                if (performer.getCitizenVillage() == null) {
                    performer.getCommunicator().sendNormalServerMessage("You may not cast that spell on someone elses deed.");
                    return false;
                }
                if (performer.getCitizenVillage().getId() != tile.getVillage().getId()) {
                    performer.getCommunicator().sendNormalServerMessage("You may not cast that spell on someone elses deed.");
                    return false;
                }
            }
        }
        catch (NoSuchZoneException nsz) {
            performer.getCommunicator().sendNormalServerMessage("You fail to focus the spell on that area.");
            return false;
        }
        return true;
    }
    
    @Override
    void doEffect(final Skill castSkill, final double power, final Creature performer, final int tilex, final int tiley, final int layer, final int heightOffset) {
        List<VolaTile> tiles = null;
        try {
            final Zone zone = Zones.getZone(tilex, tiley, true);
            tiles = new ArrayList<VolaTile>();
            for (int x = tilex - 2; x < tilex + 2; ++x) {
                for (int y = tiley - 2; y < tiley + 2; ++y) {
                    final VolaTile tile = zone.getOrCreateTile(x, y);
                    if (tile.getVillage() != null) {
                        if (performer.getCitizenVillage() == null) {
                            continue;
                        }
                        if (performer.getCitizenVillage().getId() != tile.getVillage().getId()) {
                            continue;
                        }
                    }
                    tiles.add(tile);
                }
            }
        }
        catch (NoSuchZoneException nz) {
            SproutTrees.logger.log(Level.WARNING, "Unable to find zone for sprout trees.", nz);
        }
        if (tiles == null || tiles.size() == 0) {
            performer.getCommunicator().sendNormalServerMessage("You fail to focus the spell on that area.");
            return;
        }
        final int treeType = Math.min(13, (int)(power / 7.5));
        for (int i = 0; i < tiles.size(); ++i) {
            final VolaTile currTile = tiles.get(i);
            if (!this.needsToCheckSurrounding(treeType) || !this.isTreeNearby(currTile.tilex, currTile.tiley)) {
                final int tileId = Server.surfaceMesh.getTile(currTile.tilex, currTile.tiley);
                final byte type = Tiles.decodeType(tileId);
                if (type == 5 || type == 2 || type == 10) {
                    final byte age = (byte)(7 + Server.rand.nextInt(8));
                    byte ttype;
                    if (type == 10) {
                        ttype = TreeData.TreeType.fromInt(type).asMyceliumTree();
                    }
                    else {
                        ttype = TreeData.TreeType.fromInt(type).asNormalTree();
                    }
                    final byte newData = Tiles.encodeTreeData(age, false, false, GrassData.GrowthTreeStage.SHORT);
                    Server.surfaceMesh.setTile(currTile.tilex, currTile.tiley, Tiles.encode(Tiles.decodeHeight(tileId), ttype, newData));
                    Players.getInstance().sendChangedTile(currTile.tilex, currTile.tiley, true, false);
                }
            }
        }
    }
    
    private boolean needsToCheckSurrounding(final int type) {
        return type == TreeData.TreeType.OAK.getTypeId() || type == TreeData.TreeType.WILLOW.getTypeId();
    }
    
    private boolean isTreeNearby(final int tx, final int ty) {
        for (int x = tx - 1; x < tx + 1; ++x) {
            for (int y = ty - 1; y < ty + 1; ++y) {
                if (x != tx || y != ty) {
                    try {
                        final int tileId = Server.surfaceMesh.getTile(x, y);
                        final byte type = Tiles.decodeType(tileId);
                        final Tiles.Tile tile = Tiles.getTile(type);
                        if (tile.isTree()) {
                            return true;
                        }
                    }
                    catch (Exception ex) {
                        SproutTrees.logger.log(Level.FINEST, ex.getMessage(), ex);
                    }
                }
            }
        }
        return false;
    }
    
    static {
        logger = Logger.getLogger(SproutTrees.class.getName());
    }
}
