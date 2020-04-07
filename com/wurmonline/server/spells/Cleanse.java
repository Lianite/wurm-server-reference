// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.spells;

import com.wurmonline.server.zones.VolaTile;
import com.wurmonline.server.zones.FaithZone;
import com.wurmonline.server.zones.NoSuchZoneException;
import com.wurmonline.server.Players;
import com.wurmonline.server.zones.Zones;
import com.wurmonline.mesh.Tiles;
import com.wurmonline.server.Server;
import com.wurmonline.server.creatures.Creature;
import com.wurmonline.server.skills.Skill;

public class Cleanse extends ReligiousSpell
{
    public static final int RANGE = 4;
    
    public Cleanse() {
        super("Cleanse", 930, 30, 26, 30, 33, 0L);
        this.targetTile = true;
        this.description = "cleanses a small area of mycelium infected land";
        this.type = 1;
    }
    
    @Override
    boolean precondition(final Skill castSkill, final Creature performer, final int tilex, final int tiley, final int layer) {
        if (performer.getLayer() < 0) {
            performer.getCommunicator().sendNormalServerMessage("This spell does not work below ground.", (byte)3);
            return false;
        }
        if (Tiles.decodeHeight(Server.surfaceMesh.getTile(tilex, tiley)) < 0) {
            performer.getCommunicator().sendNormalServerMessage("This spell does not work below water.", (byte)3);
            return false;
        }
        return true;
    }
    
    @Override
    void doEffect(final Skill castSkill, final double power, final Creature performer, final int tilex, final int tiley, final int layer, final int heightOffset) {
        performer.getCommunicator().sendNormalServerMessage("An invigorating energy flows through you into the ground and reaches the roots of plants and trees.");
        final int sx = Zones.safeTileX(tilex - 1 - performer.getNumLinks());
        final int sy = Zones.safeTileY(tiley - 1 - performer.getNumLinks());
        final int ex = Zones.safeTileX(tilex + 1 + performer.getNumLinks());
        final int ey = Zones.safeTileY(tiley + 1 + performer.getNumLinks());
        boolean blocked = false;
        for (int x = sx; x <= ex; ++x) {
            for (int y = sy; y <= ey; ++y) {
                try {
                    final FaithZone fz = Zones.getFaithZone(x, y, true);
                    boolean ok = false;
                    if (fz != null) {
                        if (fz.getCurrentRuler() == null || fz.getCurrentRuler() == performer.getDeity() || !fz.getCurrentRuler().isHateGod()) {
                            ok = true;
                        }
                    }
                    else {
                        ok = true;
                    }
                    if (ok) {
                        final VolaTile t = Zones.getOrCreateTile(x, y, true);
                        if (t == null || t.getVillage() == null || t.getVillage().kingdom == performer.getKingdomId()) {
                            final int tile = Server.surfaceMesh.getTile(x, y);
                            final byte type = Tiles.decodeType(tile);
                            final Tiles.Tile theTile = Tiles.getTile(type);
                            final byte data = Tiles.decodeData(tile);
                            if (type == Tiles.Tile.TILE_DIRT.id || type == Tiles.Tile.TILE_MYCELIUM_LAWN.id || type == Tiles.Tile.TILE_MYCELIUM.id || theTile.isMyceliumTree() || theTile.isMyceliumBush()) {
                                if (theTile.isMyceliumTree()) {
                                    Server.setSurfaceTile(x, y, Tiles.decodeHeight(tile), theTile.getTreeType(data).asNormalTree(), data);
                                }
                                else if (theTile.isMyceliumBush()) {
                                    Server.setSurfaceTile(x, y, Tiles.decodeHeight(tile), theTile.getBushType(data).asNormalBush(), data);
                                }
                                else if (type == Tiles.Tile.TILE_MYCELIUM_LAWN.id) {
                                    Server.setSurfaceTile(x, y, Tiles.decodeHeight(tile), Tiles.Tile.TILE_LAWN.id, (byte)0);
                                }
                                else {
                                    Server.setSurfaceTile(x, y, Tiles.decodeHeight(tile), Tiles.Tile.TILE_GRASS.id, (byte)0);
                                }
                                Players.getInstance().sendChangedTile(x, y, true, false);
                            }
                        }
                        else {
                            blocked = true;
                        }
                    }
                    else {
                        blocked = true;
                    }
                }
                catch (NoSuchZoneException ex2) {}
            }
        }
        if (blocked) {
            performer.getCommunicator().sendNormalServerMessage("The domain of another deity or settlement protects this area.", (byte)3);
        }
    }
}
