// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.spells;

import com.wurmonline.server.zones.VolaTile;
import com.wurmonline.server.zones.Zones;
import com.wurmonline.server.creatures.Creature;
import com.wurmonline.server.skills.Skill;

public class HumidDrizzle extends ReligiousSpell
{
    public static final int RANGE = 4;
    
    public HumidDrizzle() {
        super("Humid Drizzle", 407, 30, 30, 20, 21, 30000L);
        this.targetTile = true;
        this.description = "tends to animals in area";
        this.type = 1;
    }
    
    @Override
    void doEffect(final Skill castSkill, final double power, final Creature performer, final int tilex, final int tiley, final int layer, final int heightOffset) {
        performer.getCommunicator().sendNormalServerMessage("You tend to the animals here.", (byte)2);
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
                        if (!lCret.isMonster() && !lCret.isPlayer() && SpellResist.getSpellResistance(lCret, this.getNumber()) >= 1.0) {
                            lCret.setMilked(false);
                            lCret.setLastGroomed(System.currentTimeMillis());
                            lCret.getBody().healFully();
                            performer.getCommunicator().sendNormalServerMessage(lCret.getNameWithGenus() + " now shines with health.");
                            SpellResist.addSpellResistance(lCret, this.getNumber(), power);
                        }
                    }
                }
            }
        }
    }
}
