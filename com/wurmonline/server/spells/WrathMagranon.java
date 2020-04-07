// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.spells;

import com.wurmonline.server.villages.Village;
import com.wurmonline.server.structures.Structure;
import com.wurmonline.server.structures.Wall;
import com.wurmonline.server.items.Item;
import com.wurmonline.server.zones.VolaTile;
import com.wurmonline.server.behaviours.MethodsStructure;
import com.wurmonline.server.structures.NoSuchStructureException;
import com.wurmonline.server.structures.Structures;
import com.wurmonline.shared.constants.StructureTypeEnum;
import com.wurmonline.server.Servers;
import com.wurmonline.server.structures.Fence;
import java.util.ArrayList;
import com.wurmonline.server.zones.Zones;
import com.wurmonline.server.creatures.Creature;
import com.wurmonline.server.skills.Skill;
import com.wurmonline.shared.constants.AttitudeConstants;

public class WrathMagranon extends DamageSpell implements AttitudeConstants
{
    public static final int RANGE = 4;
    public static final double BASE_DAMAGE = 3000.0;
    public static final double DAMAGE_PER_POWER = 60.0;
    public static final float BASE_STRUCTURE_DAMAGE = 7.5f;
    public static final float STRUCTURE_DAMAGE_PER_POWER = 0.15f;
    public static final int RADIUS = 1;
    
    public WrathMagranon() {
        super("Wrath of Magranon", 441, 10, 50, 50, 50, 300000L);
        this.targetTile = true;
        this.offensive = true;
        this.description = "covers an area with exploding power, damaging enemies and walls";
        this.type = 2;
    }
    
    @Override
    void doEffect(final Skill castSkill, final double power, final Creature performer, final int tilex, final int tiley, final int layer, final int heightOffset) {
        performer.getCommunicator().sendNormalServerMessage("You slam down the fist of Magranon, which crushes enemy structures in the area!");
        final int radiusBonus = (int)(power / 80.0);
        final int sx = Zones.safeTileX(tilex - 1 - radiusBonus - performer.getNumLinks());
        final int sy = Zones.safeTileY(tiley - 1 - radiusBonus - performer.getNumLinks());
        final int ex = Zones.safeTileX(tilex + 1 + radiusBonus + performer.getNumLinks());
        final int ey = Zones.safeTileY(tiley + 1 + radiusBonus + performer.getNumLinks());
        final float structureDamage = 7.5f + (float)power * 0.15f;
        final ArrayList<Fence> damagedFences = new ArrayList<Fence>();
        for (int x = sx; x <= ex; ++x) {
            for (int y = sy; y <= ey; ++y) {
                final VolaTile t = Zones.getTileOrNull(x, y, layer == 0);
                if (t != null) {
                    final Item ring = Zones.isWithinDuelRing(x, y, layer >= 0);
                    if (ring == null) {
                        for (final Creature lCret : t.getCreatures()) {
                            if (!lCret.isInvulnerable() && lCret.getAttitude(performer) == 2) {
                                lCret.addAttacker(performer);
                                final double damage = this.calculateDamage(lCret, power, 3000.0, 60.0);
                                lCret.addWoundOfType(performer, (byte)0, 1, true, 1.0f, false, damage, (float)power / 5.0f, 0.0f, false, true);
                            }
                        }
                        if (Servers.isThisAPvpServer()) {
                            for (final Wall wall : t.getWalls()) {
                                Label_0456: {
                                    if (wall.getType() != StructureTypeEnum.PLAN) {
                                        boolean dealDam = true;
                                        Structure structure;
                                        try {
                                            structure = Structures.getStructure(wall.getStructureId());
                                        }
                                        catch (NoSuchStructureException nss) {
                                            break Label_0456;
                                        }
                                        final int tx = wall.getTileX();
                                        final int ty = wall.getTileY();
                                        final Village v = Zones.getVillage(tx, ty, performer.isOnSurface());
                                        if (v != null && !v.isEnemy(performer) && !MethodsStructure.mayModifyStructure(performer, structure, wall.getTile(), (short)82)) {
                                            dealDam = false;
                                        }
                                        if (dealDam) {
                                            final float wallql = wall.getCurrentQualityLevel();
                                            final float damageToDeal = structureDamage * ((150.0f - wallql) / 100.0f);
                                            wall.setDamage(wall.getDamage() + damageToDeal);
                                        }
                                    }
                                }
                            }
                        }
                        for (final Fence fence : t.getAllFences()) {
                            if (fence.isFinished()) {
                                if (!damagedFences.contains(fence)) {
                                    boolean dealDam = true;
                                    final Village vill = MethodsStructure.getVillageForFence(fence);
                                    if (vill != null && !vill.isEnemy(performer)) {
                                        dealDam = false;
                                    }
                                    float mult = 1.0f;
                                    if (performer.getCultist() != null && performer.getCultist().doubleStructDamage()) {
                                        mult *= 2.0f;
                                    }
                                    if (dealDam) {
                                        final float fenceql = fence.getCurrentQualityLevel();
                                        final float damageToDeal2 = structureDamage * ((150.0f - fenceql) / 100.0f);
                                        fence.setDamage(fence.getDamage() + damageToDeal2 * mult);
                                        damagedFences.add(fence);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        final VolaTile t2 = Zones.getTileOrNull(tilex, tiley, performer.isOnSurface());
        if (t2 != null && layer == 0) {
            Zones.flash(tilex, tiley, false);
        }
    }
}
