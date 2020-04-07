// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.spells;

import com.wurmonline.server.villages.Village;
import com.wurmonline.server.endgames.EndGameItems;
import com.wurmonline.server.behaviours.MethodsCreatures;
import com.wurmonline.server.villages.Villages;
import java.awt.Rectangle;
import com.wurmonline.server.zones.Zones;
import com.wurmonline.server.creatures.Creature;
import com.wurmonline.server.skills.Skill;

public class RevealSettlements extends ReligiousSpell
{
    public static final int RANGE = 4;
    
    public RevealSettlements() {
        super("Reveal Settlements", 443, 20, 30, 25, 30, 0L);
        this.targetTile = true;
        this.description = "locates nearby settlements";
        this.type = 2;
    }
    
    @Override
    void doEffect(final Skill castSkill, final double power, final Creature performer, final int tilex, final int tiley, final int layer, final int heightOffset) {
        performer.getCommunicator().sendNormalServerMessage("You receive insights about the area.");
        final int sx = Zones.safeTileX(performer.getTileX() - 100 - performer.getNumLinks() * 20);
        final int sy = Zones.safeTileY(performer.getTileY() - 100 - performer.getNumLinks() * 20);
        final int ex = Zones.safeTileX(performer.getTileX() + 100 + performer.getNumLinks() * 20);
        final int ey = Zones.safeTileY(performer.getTileY() + 100 + performer.getNumLinks() * 20);
        final Rectangle zoneRect = new Rectangle(sx, sy, ex - sx, ey - sy);
        final Village[] villages;
        final Village[] vills = villages = Villages.getVillages();
        for (final Village vill : villages) {
            if (vill != performer.getCurrentVillage()) {
                final Rectangle villageRect = new Rectangle(vill.startx, vill.starty, vill.endx - vill.startx + 1, vill.endy - vill.starty + 1);
                if (villageRect.intersects(zoneRect)) {
                    final int centerx = (int)villageRect.getCenterX();
                    final int centery = (int)villageRect.getCenterY();
                    final int mindist = Math.max(Math.abs(centerx - performer.getTileX()), Math.abs(centery - performer.getTileY()));
                    final int dir = MethodsCreatures.getDir(performer, centerx, centery);
                    final String direction = MethodsCreatures.getLocationStringFor(performer.getStatus().getRotation(), dir, "you");
                    final String toReturn = EndGameItems.getDistanceString(mindist, vill.getName(), direction, true);
                    performer.getCommunicator().sendNormalServerMessage(toReturn);
                }
            }
        }
    }
}
