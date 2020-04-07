// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.spells;

import com.wurmonline.server.items.Item;
import com.wurmonline.mesh.TreeData;
import java.awt.Shape;
import java.util.logging.Level;
import com.wurmonline.server.combat.CombatEngine;
import com.wurmonline.server.Players;
import com.wurmonline.server.items.ItemFactory;
import com.wurmonline.mesh.FoliageAge;
import com.wurmonline.mesh.Tiles;
import com.wurmonline.server.Server;
import java.awt.geom.Ellipse2D;
import com.wurmonline.server.zones.VolaTile;
import com.wurmonline.server.zones.Zones;
import com.wurmonline.server.creatures.Creature;
import com.wurmonline.server.skills.Skill;
import java.util.logging.Logger;
import com.wurmonline.shared.constants.AttitudeConstants;

public class Tornado extends DamageSpell implements AttitudeConstants
{
    private static Logger logger;
    public static final int RANGE = 24;
    public static final double BASE_DAMAGE = 4000.0;
    public static final double DAMAGE_PER_POWER = 80.0;
    public static final int RADIUS = 2;
    
    public Tornado() {
        super("Tornado", 413, 15, 50, 30, 40, 120000L);
        this.targetTile = true;
        this.offensive = true;
        this.description = "covers an area with extreme winds that can damage enemies and trees";
        this.type = 2;
    }
    
    @Override
    boolean precondition(final Skill castSkill, final Creature performer, final int tilex, final int tiley, final int layer) {
        if (performer.getLayer() < 0) {
            performer.getCommunicator().sendNormalServerMessage("You must be above ground to cast this spell.", (byte)3);
            return false;
        }
        final VolaTile t = Zones.getTileOrNull(tilex, tiley, performer.isOnSurface());
        if (t != null && t.getStructure() != null) {
            performer.getCommunicator().sendNormalServerMessage("You can't cast this inside.", (byte)3);
            return false;
        }
        return true;
    }
    
    @Override
    void doEffect(final Skill castSkill, final double power, final Creature performer, final int tilex, final int tiley, final int layer, final int heightOffset) {
        performer.getCommunicator().sendNormalServerMessage("You call upon the wind of Vynora.");
        final int radiusBonus = (int)(power / 40.0);
        final int sx = Zones.safeTileX(tilex - 2 - radiusBonus - performer.getNumLinks());
        final int sy = Zones.safeTileY(tiley - 2 - radiusBonus - performer.getNumLinks());
        final int ex = Zones.safeTileX(tilex + 2 + radiusBonus + performer.getNumLinks());
        final int ey = Zones.safeTileY(tiley + 2 + radiusBonus + performer.getNumLinks());
        final Shape circle = new Ellipse2D.Float(sx, sy, ex - sx, ey - sy);
        for (int x = sx; x < ex; ++x) {
            for (int y = sy; y < ey; ++y) {
                if (circle.contains(x, y)) {
                    final int tile = Server.surfaceMesh.getTile(x, y);
                    final byte type = Tiles.decodeType(tile);
                    final Tiles.Tile theTile = Tiles.getTile(type);
                    final byte data = Tiles.decodeData(tile);
                    if (type == Tiles.Tile.TILE_FIELD.id || type == Tiles.Tile.TILE_FIELD2.id) {
                        final int worldResource = Server.getWorldResource(x, y);
                        final int farmedCount = worldResource >>> 11;
                        int farmedChance = worldResource & 0x7FF;
                        farmedChance = (int)Math.min(farmedChance - power / 7.0, 2047.0);
                        Server.setWorldResource(x, y, (farmedCount << 11) + farmedChance);
                    }
                    else if (theTile.isNormalTree() || theTile.isMyceliumTree()) {
                        final byte treeAge = FoliageAge.getAgeAsByte(data);
                        final TreeData.TreeType treeType = theTile.getTreeType(data);
                        if (treeAge == 15 || Server.rand.nextInt(16 - treeAge) != 0) {
                            byte newt = Tiles.Tile.TILE_GRASS.id;
                            if (theTile.isMyceliumTree()) {
                                newt = Tiles.Tile.TILE_MYCELIUM.id;
                            }
                            Server.setSurfaceTile(x, y, Tiles.decodeHeight(tile), newt, (byte)0);
                            Server.setWorldResource(x, y, 0);
                            int templateId = 9;
                            if (treeAge >= FoliageAge.OLD_ONE.getAgeId() && treeAge < FoliageAge.SHRIVELLED.getAgeId() && !treeType.isFruitTree()) {
                                templateId = 385;
                            }
                            double sizeMod = treeAge / 15.0;
                            if (!treeType.isFruitTree()) {
                                sizeMod *= 0.25;
                            }
                            final double lNewRotation = Math.atan2((y << 2) + 2 - ((y << 2) + 2), (x << 2) + 2 - ((x << 2) + 2));
                            final float rot = (float)(lNewRotation * 57.29577951308232);
                            try {
                                final Item newItem = ItemFactory.createItem(templateId, (float)power / 5.0f, x * 4 + Server.rand.nextInt(4), y * 4 + Server.rand.nextInt(4), rot, performer.isOnSurface(), treeType.getMaterial(), (byte)0, -10L, null, treeAge);
                                newItem.setWeight((int)Math.max(1000.0, sizeMod * newItem.getWeightGrams()), true);
                                newItem.setLastOwnerId(performer.getWurmId());
                            }
                            catch (Exception ex2) {}
                            Players.getInstance().sendChangedTile(x, y, true, false);
                        }
                    }
                    final VolaTile t = Zones.getTileOrNull(x, y, performer.isOnSurface());
                    if (t != null && t.getStructure() == null && t.getFences().length <= 0) {
                        final Creature[] crets = t.getCreatures();
                        int affected = 0;
                        for (int c = 0; c < crets.length; ++c) {
                            if (!crets[c].isGhost() && !crets[c].isDead() && crets[c].getAttitude(performer) == 2) {
                                try {
                                    final byte pos = crets[c].getBody().getRandomWoundPos();
                                    final double damage = this.calculateDamage(crets[c], power, 4000.0, 80.0);
                                    CombatEngine.addWound(performer, crets[c], (byte)0, pos, damage, 1.0f, "assault", performer.getBattle(), 0.0f, 0.0f, false, false, false, true);
                                    performer.getStatus().setStunned(5.0f);
                                    ++affected;
                                }
                                catch (Exception exe) {
                                    Tornado.logger.log(Level.WARNING, exe.getMessage(), exe);
                                }
                            }
                            if (affected > power / 10.0 + performer.getNumLinks()) {
                                break;
                            }
                        }
                    }
                }
            }
        }
    }
    
    static {
        Tornado.logger = Logger.getLogger(Tornado.class.getName());
    }
}
