// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.spells;

import com.wurmonline.server.structures.BridgePart;
import com.wurmonline.server.structures.Fence;
import com.wurmonline.server.structures.Floor;
import com.wurmonline.server.structures.Wall;
import com.wurmonline.server.zones.VolaTile;
import com.wurmonline.server.zones.FaithZone;
import com.wurmonline.server.zones.NoSuchZoneException;
import com.wurmonline.server.zones.Zones;
import com.wurmonline.server.Features;
import com.wurmonline.server.HistoryManager;
import com.wurmonline.server.Server;
import com.wurmonline.server.deities.Deity;
import com.wurmonline.server.Servers;
import com.wurmonline.server.deities.Deities;
import com.wurmonline.server.items.Item;
import com.wurmonline.server.creatures.Creature;
import com.wurmonline.server.skills.Skill;

public class RitualSun extends ReligiousSpell
{
    public static final int RANGE = 4;
    
    public RitualSun() {
        super("Ritual of the Sun", 401, 100, 300, 60, 50, 43200000L);
        this.isRitual = true;
        this.targetItem = true;
        this.description = "damage in your gods domains is removed";
        this.type = 0;
    }
    
    @Override
    boolean precondition(final Skill castSkill, final Creature performer, final Item target) {
        if (performer.getDeity() != null) {
            final Deity deity = performer.getDeity();
            final Deity templateDeity = Deities.getDeity(deity.getTemplateDeity());
            if (templateDeity.getFavor() < 100000 && !Servers.isThisATestServer()) {
                performer.getCommunicator().sendNormalServerMessage(deity.getName() + " can not grant that power right now.", (byte)3);
                return false;
            }
            if (target.getBless() == deity && target.isDomainItem()) {
                return true;
            }
            performer.getCommunicator().sendNormalServerMessage(String.format("You need to cast this spell at an altar of %s.", deity.getName()), (byte)3);
        }
        return false;
    }
    
    @Override
    void doEffect(final Skill castSkill, final double power, final Creature performer, final Item target) {
        final Deity deity = performer.getDeity();
        final Deity templateDeity = Deities.getDeity(deity.getTemplateDeity());
        performer.getCommunicator().sendNormalServerMessage(performer.getDeity().getName() + " increases protection in the lands by mending the broken!");
        Server.getInstance().broadCastSafe("As the Ritual of the Sun is completed, followers of " + deity.getName() + " may now receive a blessing!");
        HistoryManager.addHistory(performer.getName(), "casts " + this.name + ". " + performer.getDeity().getName() + " mends protections in the lands.");
        templateDeity.setFavor(templateDeity.getFavor() - 100000);
        performer.achievement(635);
        for (final Creature c : performer.getLinks()) {
            c.achievement(635);
        }
        new RiteEvent.RiteOfTheSunEvent(-10, performer.getWurmId(), this.getNumber(), deity.getNumber(), System.currentTimeMillis(), 86400000L);
        if (Features.Feature.NEWDOMAINS.isEnabled()) {
            for (final FaithZone f : Zones.getFaithZones()) {
                Label_0382: {
                    if (f != null && f.getCurrentRuler() != null) {
                        if (f.getCurrentRuler().getTemplateDeity() == deity.getTemplateDeity()) {
                            try {
                                if (Zones.getFaithZone(f.getCenterX(), f.getCenterY(), true) != f) {
                                    break Label_0382;
                                }
                            }
                            catch (NoSuchZoneException e) {
                                break Label_0382;
                            }
                            for (int tx = f.getStartX(); tx < f.getEndX(); ++tx) {
                                for (int ty = f.getStartY(); ty < f.getEndY(); ++ty) {
                                    this.effectTile(tx, ty);
                                }
                            }
                        }
                    }
                }
            }
        }
        else {
            final FaithZone[][] surfaceZones = Zones.getFaithZones(true);
            for (int x = 0; x < Zones.faithSizeX; ++x) {
                for (int y = 0; y < Zones.faithSizeY; ++y) {
                    if (surfaceZones[x][y].getCurrentRuler().getTemplateDeity() == deity.getTemplateDeity()) {
                        for (int tx2 = surfaceZones[x][y].getStartX(); tx2 <= surfaceZones[x][y].getEndX(); ++tx2) {
                            for (int ty2 = surfaceZones[x][y].getStartY(); ty2 <= surfaceZones[x][y].getEndY(); ++ty2) {
                                this.effectTile(tx2, ty2);
                            }
                        }
                    }
                }
            }
        }
    }
    
    private void effectTile(final int tx, final int ty) {
        final VolaTile t = Zones.getTileOrNull(tx, ty, true);
        if (t != null) {
            final Wall[] walls2;
            final Wall[] walls = walls2 = t.getWalls();
            for (final Wall lWall : walls2) {
                lWall.setDamage(0.0f);
            }
            for (final Floor floor : t.getFloors()) {
                floor.setDamage(0.0f);
            }
            for (final Fence fence : t.getFences()) {
                fence.setDamage(0.0f);
            }
            for (final BridgePart bp : t.getBridgeParts()) {
                bp.setDamage(0.0f);
            }
        }
    }
}
