// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.spells;

import com.wurmonline.server.zones.Zone;
import com.wurmonline.server.endgames.EndGameItems;
import com.wurmonline.server.creatures.DbCreatureStatus;
import com.wurmonline.server.behaviours.MethodsCreatures;
import com.wurmonline.server.zones.Zones;
import com.wurmonline.server.creatures.Creature;
import com.wurmonline.server.skills.Skill;

public class RevealCreatures extends ReligiousSpell
{
    public static final int RANGE = 4;
    
    public RevealCreatures() {
        super("Reveal Creatures", 444, 40, 30, 25, 30, 0L);
        this.targetTile = true;
        this.description = "locates creatures nearby";
        this.type = 2;
    }
    
    @Override
    void doEffect(final Skill castSkill, final double power, final Creature performer, final int tilex, final int tiley, final int layer, final int heightOffset) {
        performer.getCommunicator().sendNormalServerMessage("You receive insights about the area.");
        final int sx = Zones.safeTileX(performer.getTileX() - 40 - performer.getNumLinks() * 5);
        final int sy = Zones.safeTileY(performer.getTileY() - 40 - performer.getNumLinks() * 5);
        final int ex = Zones.safeTileX(performer.getTileX() + 40 + performer.getNumLinks() * 5);
        final int ey = Zones.safeTileY(performer.getTileY() + 40 + performer.getNumLinks() * 5);
        final Zone[] zonesCoveredBy;
        final Zone[] zones = zonesCoveredBy = Zones.getZonesCoveredBy(sx, sy, ex, ey, performer.isOnSurface());
        for (final Zone lZone : zonesCoveredBy) {
            final Creature[] allCreatures;
            final Creature[] crets = allCreatures = lZone.getAllCreatures();
            for (final Creature cret : allCreatures) {
                if (cret.getPower() <= performer.getPower() && cret != performer && cret.getBonusForSpellEffect((byte)29) <= 0.0f) {
                    final int mindist = Math.max(Math.abs(cret.getTileX() - performer.getTileX()), Math.abs(cret.getTileY() - performer.getTileY()));
                    final int dir = MethodsCreatures.getDir(performer, cret.getTileX(), cret.getTileY());
                    final String direction = MethodsCreatures.getLocationStringFor(performer.getStatus().getRotation(), dir, "you");
                    String toReturn;
                    if (DbCreatureStatus.getIsLoaded(cret.getWurmId()) == 0) {
                        toReturn = EndGameItems.getDistanceString(mindist, cret.getName(), direction, false);
                    }
                    else {
                        toReturn = "";
                    }
                    performer.getCommunicator().sendNormalServerMessage(toReturn);
                }
            }
        }
    }
}
