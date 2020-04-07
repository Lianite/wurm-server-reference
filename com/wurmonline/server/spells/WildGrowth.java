// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.spells;

import com.wurmonline.server.structures.Fence;
import com.wurmonline.server.zones.VolaTile;
import com.wurmonline.server.Players;
import com.wurmonline.server.behaviours.Crops;
import java.io.IOException;
import java.util.logging.Level;
import com.wurmonline.shared.constants.StructureConstantsEnum;
import com.wurmonline.mesh.Tiles;
import com.wurmonline.server.Server;
import com.wurmonline.server.zones.Zones;
import com.wurmonline.server.creatures.Creature;
import com.wurmonline.server.skills.Skill;
import java.util.logging.Logger;

public class WildGrowth extends ReligiousSpell
{
    private static final Logger logger;
    public static final int RANGE = 40;
    
    public WildGrowth() {
        super("Wild Growth", 436, 30, 40, 40, 41, 0L);
        this.targetTile = true;
        this.description = "fields and trees are nurtured";
        this.type = 1;
    }
    
    @Override
    boolean precondition(final Skill castSkill, final Creature performer, final int tilex, final int tiley, final int layer) {
        if (performer.getLayer() < 0) {
            performer.getCommunicator().sendNormalServerMessage("This spell does not work below ground.", (byte)3);
            return false;
        }
        return true;
    }
    
    @Override
    void doEffect(final Skill castSkill, final double power, final Creature performer, final int tilex, final int tiley, final int layer, final int heightOffset) {
        performer.getCommunicator().sendNormalServerMessage("An invigorating energy flows through you into the ground and reaches the roots of plants and trees.");
        final int sx = Zones.safeTileX(tilex - (int)Math.max(1.0, power / 20.0) - performer.getNumLinks());
        final int sy = Zones.safeTileY(tiley - (int)Math.max(1.0, power / 20.0) - performer.getNumLinks());
        final int ex = Zones.safeTileX(tilex + (int)Math.max(1.0, power / 20.0) + performer.getNumLinks());
        final int ey = Zones.safeTileY(tiley + (int)Math.max(1.0, power / 20.0) + performer.getNumLinks());
        boolean sentMessage = false;
        for (int x = sx; x <= ex; ++x) {
            for (int y = sy; y <= ey; ++y) {
                final int tile = Server.surfaceMesh.getTile(x, y);
                final byte type = Tiles.decodeType(tile);
                final Tiles.Tile theTile = Tiles.getTile(type);
                if (performer.isOnSurface()) {
                    final VolaTile t = Zones.getTileOrNull(x, y, true);
                    if (t != null) {
                        if (t.getVillage() == null || t.getVillage().isActionAllowed((short)468, performer, false, 0, 0)) {
                            for (final Fence fence : t.getFences()) {
                                if (fence.isHedge() && !fence.isHighHedge() && fence.getType() != StructureConstantsEnum.HEDGE_FLOWER1_LOW && fence.getType() != StructureConstantsEnum.HEDGE_FLOWER3_MEDIUM) {
                                    fence.setDamage(0.0f);
                                    fence.setType(StructureConstantsEnum.getEnumByValue((short)(fence.getType().value + 1)));
                                    try {
                                        fence.save();
                                        t.updateFence(fence);
                                    }
                                    catch (IOException iox) {
                                        WildGrowth.logger.log(Level.WARNING, x + "," + y + " " + iox.getMessage(), iox);
                                    }
                                }
                            }
                        }
                        else if (!sentMessage) {
                            performer.getCommunicator().sendNormalServerMessage("You are not allowed to affect the hedges in " + t.getVillage().getName() + ".", (byte)3);
                            sentMessage = true;
                        }
                    }
                }
                if (type == Tiles.Tile.TILE_FIELD.id || type == Tiles.Tile.TILE_FIELD2.id) {
                    final int worldResource = Server.getWorldResource(x, y);
                    final int farmedCount = worldResource >>> 11;
                    int farmedChance = worldResource & 0x7FF;
                    farmedChance = (int)Math.min(farmedChance + power * 2.0 + 75.0, 2047.0);
                    Server.setWorldResource(x, y, (farmedCount << 11) + farmedChance);
                    final byte data = Tiles.decodeData(tile);
                    final int tileAge = Crops.decodeFieldAge(data);
                    final int crop = Crops.getCropNumber(type, data);
                    if (tileAge < 7) {
                        Server.setSurfaceTile(x, y, Tiles.decodeHeight(tile), type, Crops.encodeFieldData(true, tileAge, crop));
                        Players.getInstance().sendChangedTile(x, y, true, false);
                    }
                }
                else if (theTile.isNormalTree() || theTile.isNormalBush()) {
                    final int age = Tiles.decodeData(tile) >> 4 & 0xF;
                    final int halfdata = Tiles.decodeData(tile) & 0xF;
                    Server.setWorldResource(x, y, 0);
                    if (age < 15) {
                        final int newData = (age + 1 << 4) + halfdata & 0xFF;
                        Server.setSurfaceTile(x, y, Tiles.decodeHeight(tile), type, (byte)newData);
                    }
                    else {
                        Server.setSurfaceTile(x, y, Tiles.decodeHeight(tile), Tiles.Tile.TILE_GRASS.id, (byte)0);
                    }
                    Players.getInstance().sendChangedTile(x, y, true, false);
                }
            }
        }
    }
    
    static {
        logger = Logger.getLogger(WildGrowth.class.getName());
    }
}
