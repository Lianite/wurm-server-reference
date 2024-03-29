// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.spells;

import com.wurmonline.server.zones.VolaTile;
import com.wurmonline.mesh.Tiles;
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

public class HolyCrop extends ReligiousSpell
{
    public static final int RANGE = 4;
    
    HolyCrop() {
        super("Holy Crop", 400, 100, 300, 60, 50, 7200000L);
        this.isRitual = true;
        this.targetItem = true;
        this.description = "crop and animal blessings";
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
        performer.getCommunicator().sendNormalServerMessage(performer.getDeity().getName() + " graces the lands with abundant crop yield and happy animals!", (byte)2);
        Server.getInstance().broadCastSafe("As the Holy Crop ritual is completed, followers of " + deity.getName() + " may now receive a blessing!");
        HistoryManager.addHistory(performer.getName(), "casts " + this.name + ". " + performer.getDeity().getName() + " graces the lands with abundant crop yield and happy animals.");
        templateDeity.setFavor(templateDeity.getFavor() - 100000);
        performer.achievement(635);
        for (final Creature c : performer.getLinks()) {
            c.achievement(635);
        }
        new RiteEvent.RiteOfCropEvent(-10, performer.getWurmId(), this.getNumber(), deity.getNumber(), System.currentTimeMillis(), 86400000L);
        final int pow = 100 + Math.max(20, (int)power * 3);
        if (Features.Feature.NEWDOMAINS.isEnabled()) {
            for (final FaithZone f : Zones.getFaithZones()) {
                Label_0391: {
                    if (f != null) {
                        if (f.getCurrentRuler().getTemplateDeity() == deity.getTemplateDeity()) {
                            try {
                                if (Zones.getFaithZone(f.getCenterX(), f.getCenterY(), true) != f) {
                                    break Label_0391;
                                }
                            }
                            catch (NoSuchZoneException e) {
                                break Label_0391;
                            }
                            for (int tx = f.getStartX(); tx < f.getEndX(); ++tx) {
                                for (int ty = f.getStartY(); ty < f.getEndY(); ++ty) {
                                    this.effectTile(tx, ty, pow);
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
                        for (int tx2 = surfaceZones[x][y].getStartX(); tx2 < surfaceZones[x][y].getEndX(); ++tx2) {
                            for (int ty2 = surfaceZones[x][y].getStartY(); ty2 < surfaceZones[x][y].getEndY(); ++ty2) {
                                this.effectTile(tx2, ty2, pow);
                            }
                        }
                    }
                }
            }
        }
    }
    
    private void effectTile(final int tx, final int ty, final int pow) {
        final int tile = Server.surfaceMesh.getTile(tx, ty);
        if (Tiles.decodeType(tile) == Tiles.Tile.TILE_FIELD.id) {
            final int worldResource = Server.getWorldResource(tx, ty);
            int farmedCount = worldResource >>> 11;
            int farmedChance = worldResource & 0x7FF;
            if (farmedCount < 5) {
                ++farmedCount;
            }
            farmedChance = Math.min(farmedChance + pow, 2047);
            Server.setWorldResource(tx, ty, (farmedCount << 11) + farmedChance);
        }
        final VolaTile t = Zones.getTileOrNull(tx, ty, true);
        if (t != null) {
            final Creature[] creatures;
            final Creature[] crets = creatures = t.getCreatures();
            for (final Creature lCret : creatures) {
                if (lCret.getLoyalty() > 0.0f) {
                    lCret.setLoyalty(99.0f);
                    lCret.getStatus().modifyHunger(0, 80.0f);
                }
                else if (lCret.isDomestic()) {
                    lCret.getStatus().modifyHunger(0, 80.0f);
                }
                lCret.removeRandomNegativeTrait();
            }
        }
    }
}
