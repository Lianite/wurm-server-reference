// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.spells;

import com.wurmonline.server.items.Item;
import com.wurmonline.server.zones.VolaTile;
import com.wurmonline.server.structures.Structure;
import com.wurmonline.server.zones.Zones;
import com.wurmonline.server.creatures.Creature;
import com.wurmonline.server.skills.Skill;
import com.wurmonline.shared.constants.AttitudeConstants;

public class PainRain extends DamageSpell implements AttitudeConstants
{
    public static final int RANGE = 24;
    public static final double BASE_DAMAGE = 6000.0;
    public static final double DAMAGE_PER_POWER = 40.0;
    public static final int RADIUS = 2;
    
    public PainRain() {
        super("Pain Rain", 432, 10, 40, 20, 40, 120000L);
        this.targetTile = true;
        this.offensive = true;
        this.description = "covers an area with damaging energy causing infection wounds on enemies";
        this.type = 2;
    }
    
    @Override
    void doEffect(final Skill castSkill, final double power, final Creature performer, final int tilex, final int tiley, final int layer, final int heightOffset) {
        final Structure currstr = performer.getCurrentTile().getStructure();
        final int radiusBonus = (int)(power / 40.0);
        final int sx = Zones.safeTileX(tilex - 2 - radiusBonus - performer.getNumLinks());
        final int sy = Zones.safeTileY(tiley - 2 - radiusBonus - performer.getNumLinks());
        final int ex = Zones.safeTileX(tilex + 2 + radiusBonus + performer.getNumLinks());
        final int ey = Zones.safeTileY(tiley + 2 + radiusBonus + performer.getNumLinks());
        for (int x = sx; x < ex; ++x) {
            for (int y = sy; y < ey; ++y) {
                final VolaTile t = Zones.getTileOrNull(x, y, layer == 0);
                if (t != null) {
                    final Structure toCheck = t.getStructure();
                    if (currstr == toCheck) {
                        final Item ring = Zones.isWithinDuelRing(x, y, layer >= 0);
                        if (ring == null) {
                            final Creature[] crets = t.getCreatures();
                            int affected = 0;
                            for (final Creature lCret : crets) {
                                if (!lCret.isInvulnerable() && lCret.getAttitude(performer) == 2) {
                                    lCret.addAttacker(performer);
                                    final double damage = this.calculateDamage(lCret, power, 6000.0, 40.0);
                                    lCret.addWoundOfType(performer, (byte)6, 1, true, 1.0f, false, damage, (float)power / 5.0f, 0.0f, false, true);
                                    ++affected;
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
    }
}
