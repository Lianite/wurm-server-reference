// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.spells;

import com.wurmonline.mesh.Tiles;
import com.wurmonline.server.players.Player;
import com.wurmonline.server.zones.FaithZone;
import com.wurmonline.server.Players;
import com.wurmonline.server.zones.NoSuchZoneException;
import com.wurmonline.server.zones.Zones;
import com.wurmonline.server.Features;
import com.wurmonline.server.HistoryManager;
import com.wurmonline.server.Server;
import com.wurmonline.server.deities.Deity;
import com.wurmonline.server.deities.Deities;
import com.wurmonline.server.items.Item;
import com.wurmonline.server.creatures.Creature;
import com.wurmonline.server.skills.Skill;
import com.wurmonline.server.Servers;

public class RiteDeath extends ReligiousSpell
{
    public static final int RANGE = 4;
    
    public RiteDeath() {
        super("Rite of Death", 402, 100, 300, 60, 50, 43200000L);
        this.isRitual = true;
        this.targetItem = true;
        this.description = (Servers.localServer.PVPSERVER ? "spawns mycelium in your gods domain" : "awards followers with some skill and sleep bonus");
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
        if (Servers.localServer.PVPSERVER) {
            performer.getCommunicator().sendNormalServerMessage("The domain of " + performer.getDeity().getName() + " is covered in mycelium.", (byte)2);
            Server.getInstance().broadCastSafe("As the Rite of Death is completed, followers of " + deity.getName() + " may now receive a blessing!");
            HistoryManager.addHistory(performer.getName(), "casts " + this.name + ". The domain of " + performer.getDeity().getName() + " is covered in mycelium.");
            templateDeity.setFavor(templateDeity.getFavor() - 100000);
            performer.achievement(635);
            for (final Creature c : performer.getLinks()) {
                c.achievement(635);
            }
            new RiteEvent.RiteOfDeathEvent(-10, performer.getWurmId(), this.getNumber(), deity.getNumber(), System.currentTimeMillis(), 86400000L);
            if (Features.Feature.NEWDOMAINS.isEnabled()) {
                final byte type = 0;
                for (final FaithZone f : Zones.getFaithZones()) {
                    Label_0394: {
                        if (f != null) {
                            if (f.getCurrentRuler().getTemplateDeity() == deity.getTemplateDeity()) {
                                try {
                                    if (Zones.getFaithZone(f.getCenterX(), f.getCenterY(), true) != f) {
                                        break Label_0394;
                                    }
                                }
                                catch (NoSuchZoneException e) {
                                    break Label_0394;
                                }
                                for (int tx = f.getStartX(); tx < f.getEndX(); ++tx) {
                                    for (int ty = f.getStartY(); ty < f.getEndY(); ++ty) {
                                        this.effectTile(tx, ty, type);
                                    }
                                }
                            }
                        }
                    }
                }
            }
            else {
                final FaithZone[][] surfaceZones = Zones.getFaithZones(true);
                final byte type2 = 0;
                for (int x = 0; x < Zones.faithSizeX; ++x) {
                    for (int y = 0; y < Zones.faithSizeY; ++y) {
                        if (surfaceZones[x][y].getCurrentRuler().getTemplateDeity() == deity.getTemplateDeity()) {
                            for (int tx2 = surfaceZones[x][y].getStartX(); tx2 <= surfaceZones[x][y].getEndX(); ++tx2) {
                                for (int ty2 = surfaceZones[x][y].getStartY(); ty2 <= surfaceZones[x][y].getEndY(); ++ty2) {
                                    this.effectTile(tx2, ty2, type2);
                                }
                            }
                        }
                    }
                }
            }
            final Player[] players2;
            final Player[] players = players2 = Players.getInstance().getPlayers();
            for (final Player lPlayer : players2) {
                if (lPlayer.getDeity() == null || lPlayer.getDeity().getTemplateDeity() != deity.getTemplateDeity()) {
                    lPlayer.getCommunicator().sendAlertServerMessage("You get a sudden headache.", (byte)3);
                    lPlayer.addWoundOfType(performer, (byte)9, 1, false, 1.0f, false, 1000.0, 0.0f, 0.0f, false, true);
                }
            }
        }
        else {
            performer.getCommunicator().sendNormalServerMessage("The followers of " + performer.getDeity().getName() + " may now receive a blessing.", (byte)2);
            Server.getInstance().broadCastSafe("As the Rite of Death is completed, followers of " + deity.getName() + " may now receive a blessing!");
            HistoryManager.addHistory(performer.getName(), "casts " + this.name + ". The followers of " + performer.getDeity().getName() + " may now receive a blessing.");
            templateDeity.setFavor(templateDeity.getFavor() - 100000);
            performer.achievement(635);
            for (final Creature c : performer.getLinks()) {
                c.achievement(635);
            }
            new RiteEvent.RiteOfDeathEvent(-10, performer.getWurmId(), this.getNumber(), deity.getNumber(), System.currentTimeMillis(), 86400000L);
        }
    }
    
    private void effectTile(final int tx, final int ty, byte type) {
        final int tile = Server.surfaceMesh.getTile(tx, ty);
        type = Tiles.decodeType(tile);
        final Tiles.Tile theTile = Tiles.getTile(type);
        final byte data = Tiles.decodeData(tile);
        if (type == Tiles.Tile.TILE_GRASS.id || type == Tiles.Tile.TILE_DIRT.id) {
            Server.setSurfaceTile(tx, ty, Tiles.decodeHeight(tile), Tiles.Tile.TILE_MYCELIUM.id, (byte)0);
            Players.getInstance().sendChangedTile(tx, ty, true, false);
        }
        else if (theTile.isNormalTree()) {
            Server.setSurfaceTile(tx, ty, Tiles.decodeHeight(tile), theTile.getTreeType(data).asMyceliumTree(), data);
            Players.getInstance().sendChangedTile(tx, ty, true, false);
        }
        else if (theTile.isNormalBush()) {
            Server.setSurfaceTile(tx, ty, Tiles.decodeHeight(tile), theTile.getBushType(data).asMyceliumBush(), data);
            Players.getInstance().sendChangedTile(tx, ty, true, false);
        }
    }
}
