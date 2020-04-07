// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.spells;

import com.wurmonline.server.zones.VolaTile;
import com.wurmonline.server.zones.Zones;
import com.wurmonline.server.creatures.Creature;
import com.wurmonline.server.skills.Skill;
import com.wurmonline.shared.constants.AttitudeConstants;

public class MassStamina extends ReligiousSpell implements AttitudeConstants
{
    public static final int RANGE = 12;
    
    public MassStamina() {
        super("Mass Stamina", 425, 15, 50, 20, 40, 900000L);
        this.targetTile = true;
        this.description = "covers an area with revitalising energy, refreshing stamina for allies";
        this.type = 2;
    }
    
    @Override
    void doEffect(final Skill castSkill, final double power, final Creature performer, final int tilex, final int tiley, final int layer, final int heightOffset) {
        final int sx = Zones.safeTileX(tilex - 5 - performer.getNumLinks());
        final int sy = Zones.safeTileY(tiley - 5 - performer.getNumLinks());
        final int ex = Zones.safeTileX(tilex + 5 + performer.getNumLinks());
        final int ey = Zones.safeTileY(tiley + 5 + performer.getNumLinks());
        for (int x = sx; x < ex; ++x) {
            for (int y = sy; y < ey; ++y) {
                final VolaTile t = Zones.getTileOrNull(x, y, performer.isOnSurface());
                if (t != null) {
                    final Creature[] creatures;
                    final Creature[] crets = creatures = t.getCreatures();
                    for (final Creature lCret : creatures) {
                        if (lCret.getAttitude(performer) != 2) {
                            lCret.getStatus().modifyStamina2(100.0f);
                        }
                    }
                }
            }
        }
    }
}
