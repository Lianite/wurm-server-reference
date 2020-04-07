// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.zones;

import com.wurmonline.server.players.Cults;
import com.wurmonline.server.Servers;
import java.util.logging.Level;
import com.wurmonline.server.NoSuchPlayerException;
import com.wurmonline.server.creatures.NoSuchCreatureException;
import com.wurmonline.server.Server;
import com.wurmonline.server.items.Item;
import com.wurmonline.server.WurmId;
import com.wurmonline.server.Items;
import com.wurmonline.server.kingdom.GuardTower;
import com.wurmonline.server.creatures.DbCreatureStatus;
import java.io.IOException;
import com.wurmonline.server.kingdom.Kingdoms;
import com.wurmonline.server.creatures.Creatures;
import com.wurmonline.server.creatures.Creature;
import java.util.logging.Logger;
import com.wurmonline.shared.constants.CounterTypes;
import com.wurmonline.server.MiscConstants;

public final class ErrorChecks implements MiscConstants, CounterTypes
{
    private static final Logger logger;
    
    public static void checkCreatures(final Creature performer, final String searchString) {
        final long lStart = System.nanoTime();
        int nums = 0;
        final Creature[] crets = Creatures.getInstance().getCreatures();
        final boolean empty = searchString == null || searchString.length() == 0;
        performer.getCommunicator().sendSafeServerMessage("Starting creature check...");
        for (int x = 0; x < crets.length; ++x) {
            if (empty || crets[x].getName().contains(searchString)) {
                final VolaTile t = crets[x].getCurrentTile();
                if (t != null) {
                    try {
                        final Zone z = Zones.getZone(crets[x].getTileX(), crets[x].getTileY(), crets[x].isOnSurface());
                        final VolaTile rt = z.getTileOrNull(crets[x].getTileX(), crets[x].getTileY());
                        if (rt != null) {
                            if (rt.getTileX() != t.getTileX() || rt.getTileY() != t.getTileY()) {
                                performer.getCommunicator().sendNormalServerMessage(crets[x].getName() + " [" + crets[x].getWurmId() + "] at " + crets[x].getTileX() + "," + crets[x].getTileY() + " currenttile at " + t.getTileX() + " " + t.getTileY());
                                ++nums;
                            }
                            boolean found = false;
                            Creature[] cc = rt.getCreatures();
                            for (int xx = 0; xx < cc.length; ++xx) {
                                if (cc[xx].getWurmId() == crets[x].getWurmId()) {
                                    found = true;
                                }
                            }
                            if (!found) {
                                if (!crets[x].isDead()) {
                                    performer.getCommunicator().sendNormalServerMessage(crets[x].getName() + " [" + crets[x].getWurmId() + "] not in list on tile " + rt.getTileX() + " " + rt.getTileY() + " #" + rt.hashCode() + " xy=" + crets[x].getTileX() + ", " + crets[x].getTileY() + " surf=" + crets[x].isOnSurface() + " inactive=" + rt.isInactive());
                                    ++nums;
                                }
                                found = false;
                                cc = t.getCreatures();
                                for (int xx = 0; xx < cc.length; ++xx) {
                                    if (cc[xx].getWurmId() == crets[x].getWurmId()) {
                                        found = true;
                                    }
                                }
                                if (!found) {
                                    if (!crets[x].isDead()) {
                                        performer.getCommunicator().sendNormalServerMessage(crets[x].getName() + " [" + crets[x].getWurmId() + "] not in list on CURRENT tile " + t.getTileX() + " " + t.getTileY() + " #" + t.hashCode() + " xy=" + crets[x].getTileX() + ", " + crets[x].getTileY() + " surf=" + crets[x].isOnSurface() + " inactive=" + t.isInactive());
                                    }
                                    if (crets[x].isDead()) {
                                        boolean delete = true;
                                        if (crets[x].isKingdomGuard()) {
                                            final GuardTower tower = Kingdoms.getTower(crets[x]);
                                            if (tower != null) {
                                                try {
                                                    delete = false;
                                                    tower.returnGuard(crets[x]);
                                                    performer.getCommunicator().sendNormalServerMessage(crets[x].getName() + " [" + crets[x].getWurmId() + "] returned to tower.");
                                                }
                                                catch (IOException ex) {}
                                            }
                                        }
                                        if (delete) {
                                            if (DbCreatureStatus.getIsLoaded(crets[x].getWurmId()) == 0) {
                                                crets[x].destroy();
                                            }
                                            performer.getCommunicator().sendNormalServerMessage(crets[x].getName() + " [" + crets[x].getWurmId() + "] destroyed.");
                                        }
                                    }
                                }
                                else {
                                    performer.getCommunicator().sendNormalServerMessage(crets[x].getName() + " [" + crets[x].getWurmId() + "] IS in list on CURRENT tile " + t.getTileX() + " " + t.getTileY() + " #" + t.hashCode() + " xy=" + crets[x].getTileX() + ", " + crets[x].getTileY() + " surf=" + crets[x].isOnSurface() + " inactive=" + t.isInactive());
                                }
                            }
                        }
                        else {
                            performer.getCommunicator().sendNormalServerMessage(crets[x].getName() + " [" + crets[x].getWurmId() + "] null tile but current at " + t.getTileX() + ", " + t.getTileY());
                        }
                    }
                    catch (NoSuchZoneException nsz) {
                        performer.getCommunicator().sendNormalServerMessage(crets[x].getName() + " [" + crets[x].getWurmId() + "] no zone at " + t.getTileX() + ", " + t.getTileY());
                    }
                }
                else {
                    performer.getCommunicator().sendNormalServerMessage(crets[x].getName() + " [" + crets[x].getWurmId() + "] null current tile.");
                }
            }
        }
        performer.getCommunicator().sendSafeServerMessage("...done. " + nums + " errors.");
        ErrorChecks.logger.info("#checkCreatures took " + (System.nanoTime() - lStart) / 1000000.0f + "ms.");
    }
    
    public static void checkItems(final Creature performer, final String searchString) {
        final long lStart = System.nanoTime();
        ErrorChecks.logger.info(performer + " is checking Items using search string: " + searchString);
        final Item[] items = Items.getAllItems();
        final boolean empty = searchString == null || searchString.length() == 0;
        performer.getCommunicator().sendSafeServerMessage("Starting items check...");
        int nums = 0;
        for (int x = 0; x < items.length; ++x) {
            if ((empty || items[x].getName().contains(searchString)) && items[x].getZoneId() >= 0 && items[x].getTemplateId() != 177) {
                try {
                    final Zone z = Zones.getZone(items[x].getTileX(), items[x].getTileY(), items[x].isOnSurface());
                    final VolaTile rt = z.getTileOrNull(items[x].getTileX(), items[x].getTileY());
                    if (rt != null) {
                        if (rt.getTileX() != items[x].getTileX() || rt.getTileY() != items[x].getTileY()) {
                            performer.getCommunicator().sendNormalServerMessage(items[x].getName() + " [" + items[x].getWurmId() + "] at " + items[x].getTileX() + "," + items[x].getTileY() + " currenttile at " + rt.getTileX() + " " + rt.getTileY());
                            ++nums;
                        }
                        final Item[] cc = rt.getItems();
                        boolean found = false;
                        for (int xx = 0; xx < cc.length; ++xx) {
                            if (cc[xx].getWurmId() == items[x].getWurmId()) {
                                found = true;
                            }
                        }
                        if (!found) {
                            performer.getCommunicator().sendNormalServerMessage(items[x].getName() + " [" + items[x].getWurmId() + "] not in list on tile " + rt.getTileX() + " " + rt.getTileY() + " inactive=" + rt.isInactive());
                            ++nums;
                        }
                    }
                    else {
                        performer.getCommunicator().sendNormalServerMessage(items[x].getName() + " [" + items[x].getWurmId() + "] last:" + items[x].getLastParentId() + " pile=" + (WurmId.getType(items[x].lastParentId) == 6) + ", null tile but current at " + items[x].getTileX() + ", " + items[x].getTileY());
                        ++nums;
                    }
                }
                catch (NoSuchZoneException nsz) {
                    performer.getCommunicator().sendNormalServerMessage(items[x].getName() + " [" + items[x].getWurmId() + "] no zone at " + items[x].getTileX() + ", " + items[x].getTileY());
                }
            }
        }
        performer.getCommunicator().sendSafeServerMessage("...done. " + nums + " errors.");
        ErrorChecks.logger.info("#checkItems took " + (System.nanoTime() - lStart) / 1000000.0f + "ms.");
    }
    
    public static void getInfo(final Creature performer, final int tilex, final int tiley, final int layer) {
        try {
            final Zone z = Zones.getZone(tilex, tiley, layer >= 0);
            final VolaTile rt = z.getOrCreateTile(tilex, tiley);
            final Creature[] cc = rt.getCreatures();
            final VirtualZone[] watchers = rt.getWatchers();
            for (int xx = 0; xx < cc.length; ++xx) {
                performer.getCommunicator().sendNormalServerMessage(tilex + ", " + tiley + " contains " + cc[xx].getName());
                try {
                    Server.getInstance().getCreature(cc[xx].getWurmId());
                }
                catch (NoSuchCreatureException nsc) {
                    performer.getCommunicator().sendNormalServerMessage("The Creatures list does NOT contain " + cc[xx].getWurmId());
                }
                catch (NoSuchPlayerException nsp) {
                    performer.getCommunicator().sendNormalServerMessage("The Players list does NOT contain " + cc[xx].getWurmId());
                }
                for (int v = 0; v < watchers.length; ++v) {
                    try {
                        if (!watchers[v].containsCreature(cc[xx])) {
                            if (watchers[v].getWatcher() != null && watchers[v].getWatcher().getWurmId() != cc[xx].getWurmId()) {
                                performer.getCommunicator().sendNormalServerMessage(cc[xx].getName() + " (" + cc[xx].getWurmId() + ") is not visible to " + watchers[v].getWatcher().getName());
                            }
                            else if (watchers[v].getWatcher() == null) {
                                performer.getCommunicator().sendNormalServerMessage("The tile is monitored by an unknown creature or player who will not see the creature.");
                            }
                        }
                    }
                    catch (Exception e) {
                        ErrorChecks.logger.log(Level.WARNING, e.getMessage(), e);
                    }
                }
            }
            final Item[] items = rt.getItems();
            if (Servers.localServer.testServer) {
                for (final Item i : items) {
                    final String itemMessage = String.format("It contains %s, at floor level %d, at Z position %.2f", i.getName(), i.getFloorLevel(), i.getPosZ());
                    performer.getCommunicator().sendNormalServerMessage(itemMessage);
                }
                if (performer.getPower() >= 5) {
                    final String zoneMessage = String.format("Tile belongs to zone %d, which covers %d, %d to %d, %d.", z.getId(), z.getStartX(), z.getStartY(), z.getEndX(), z.getEndY());
                    performer.getCommunicator().sendNormalServerMessage(zoneMessage);
                    final VolaTile caveTile = Zones.getOrCreateTile(rt.tilex, rt.tiley, false);
                    final String caveVTMessage = String.format("Cave VolaTile instance transition is %s, layer is %d. It contains %d items.", caveTile.isTransition(), caveTile.getLayer(), caveTile.getItems().length);
                    performer.getCommunicator().sendNormalServerMessage(caveVTMessage);
                    final VolaTile surfTile = Zones.getOrCreateTile(rt.tilex, rt.tiley, true);
                    final String surfVTMessage = String.format("Surface VolaTile instance transition is %s, layer is %d. It contains %d items.", surfTile.isTransition(), surfTile.getLayer(), surfTile.getItems().length);
                    performer.getCommunicator().sendNormalServerMessage(surfVTMessage);
                }
            }
            else {
                performer.getCommunicator().sendNormalServerMessage("It contains " + items.length + " items.");
            }
        }
        catch (NoSuchZoneException nsz) {
            performer.getCommunicator().sendNormalServerMessage(tilex + "," + tiley + " no zone.");
        }
        try {
            final float height = Zones.calculateHeight((tilex << 2) + 2, (tiley << 2) + 2, performer.isOnSurface()) * 10.0f;
            final byte path = Cults.getPathFor(tilex, tiley, 0, (int)height);
            performer.getCommunicator().sendNormalServerMessage("Meditation path is " + Cults.getPathNameFor(path) + ".");
        }
        catch (NoSuchZoneException nsz) {
            ErrorChecks.logger.log(Level.WARNING, nsz.getMessage(), nsz);
        }
    }
    
    public static void checkZones(final Creature checker) {
        ErrorChecks.logger.info(checker.getName() + " checking zones");
        checker.getCommunicator().sendNormalServerMessage("Checking cave zone tiles:");
        Zones.checkAllCaveZones(checker);
        checker.getCommunicator().sendNormalServerMessage("Checking surface zone tiles:");
        Zones.checkAllSurfaceZones(checker);
        checker.getCommunicator().sendNormalServerMessage("Done.");
        ErrorChecks.logger.info(checker.getName() + " finished checking zones");
    }
    
    public static void checkItemWatchers() {
    }
    
    static {
        logger = Logger.getLogger(ErrorChecks.class.getName());
    }
}
