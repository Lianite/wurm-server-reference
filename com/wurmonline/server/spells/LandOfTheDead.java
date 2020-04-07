// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.spells;

import com.wurmonline.server.villages.Village;
import com.wurmonline.server.Items;
import com.wurmonline.server.zones.Zones;
import com.wurmonline.server.HistoryManager;
import com.wurmonline.server.villages.Villages;
import com.wurmonline.server.Server;
import com.wurmonline.server.items.Item;
import com.wurmonline.server.Servers;
import com.wurmonline.server.creatures.Creature;
import com.wurmonline.server.skills.Skill;

public class LandOfTheDead extends ReligiousSpell
{
    public static final int RANGE = 50;
    
    public LandOfTheDead() {
        super("Land of the Dead", 435, 60, 300, 70, 70, 259200000L);
        this.targetItem = true;
        this.targetTile = true;
        this.description = "summons the souls of the deceased";
        this.type = 0;
    }
    
    @Override
    boolean precondition(final Skill castSkill, final Creature performer, final int tilex, final int tiley, final int layer) {
        if (!Servers.isThisAPvpServer()) {
            performer.getCommunicator().sendNormalServerMessage("Libila cannot grant that power right now.");
            return false;
        }
        return true;
    }
    
    @Override
    boolean precondition(final Skill castSkill, final Creature performer, final Item target) {
        if (!Servers.isThisAPvpServer()) {
            performer.getCommunicator().sendNormalServerMessage("Libila cannot grant that power right now.");
            return false;
        }
        return true;
    }
    
    @Override
    void doEffect(final Skill castSkill, final double power, final Creature performer, final int tilex, final int tiley, final int layer, final int heightOffset) {
        this.castLandOfTheDead(performer, performer.getTileX(), performer.getTileY(), Math.max(10.0, power));
    }
    
    @Override
    void doEffect(final Skill castSkill, final double power, final Creature performer, final Item target) {
        this.castLandOfTheDead(performer, performer.getTileX(), performer.getTileY(), Math.max(10.0, power));
    }
    
    private void castLandOfTheDead(final Creature performer, final int startx, final int starty, final double power) {
        performer.getCommunicator().sendNormalServerMessage("You call back souls of the dead! Possess the shells! RISE! RISE!", (byte)2);
        Server.getInstance().broadCastAction(performer.getName() + " commands the souls of the dead to return and possess the shells of the deceased!", performer, 10);
        Village v = Villages.getVillage(startx, starty, true);
        if (v == null) {
            for (int x = -50; x < 50; x += 5) {
                for (int y = -50; y < 50; y += 5) {
                    v = Villages.getVillage(startx + x, starty + y, true);
                    if (v != null) {
                        break;
                    }
                }
            }
        }
        if (v != null) {
            HistoryManager.addHistory(performer.getName(), "Casts land of the dead near " + v.getName());
        }
        else {
            HistoryManager.addHistory(performer.getName(), "Casts land of the dead");
        }
        final int minx = Zones.safeTileX(startx - (int)(200.0 * power / 100.0));
        final int miny = Zones.safeTileY(starty - (int)(200.0 * power / 100.0));
        final int endx = Zones.safeTileX(startx + (int)(200.0 * power / 100.0));
        final int endy = Zones.safeTileY(starty + (int)(200.0 * power / 100.0));
        final Item[] its = Items.getAllItems();
        int maxCorpses = (int)power;
        for (int itx = 0; itx < its.length; ++itx) {
            if (its[itx].getZoneId() > -1 && its[itx].getTemplateId() == 272) {
                final int centerx = its[itx].getTileX();
                final int centery = its[itx].getTileY();
                if (centerx < endx && centerx > minx && centery < endy && centery > miny && Rebirth.mayRaise(performer, its[itx], false) && maxCorpses > 0) {
                    Rebirth.raise(power, performer, its[itx], true);
                    --maxCorpses;
                }
            }
        }
    }
}
