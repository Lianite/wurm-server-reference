// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.spells;

import com.wurmonline.server.Players;
import com.wurmonline.server.structures.Fence;
import com.wurmonline.server.zones.VolaTile;
import com.wurmonline.server.zones.Zone;
import com.wurmonline.server.zones.NoSuchZoneException;
import com.wurmonline.server.zones.Zones;
import com.wurmonline.mesh.Tiles;
import com.wurmonline.server.behaviours.Terraforming;
import com.wurmonline.server.Server;
import com.wurmonline.server.FailedException;
import com.wurmonline.server.items.ItemFactory;
import com.wurmonline.server.items.ItemTemplate;
import com.wurmonline.server.items.NoSuchTemplateException;
import com.wurmonline.server.items.ItemTemplateFactory;
import com.wurmonline.server.behaviours.MethodsItems;
import com.wurmonline.server.items.Item;
import com.wurmonline.server.creatures.Creature;
import com.wurmonline.server.skills.Skill;

public class Dirt extends ReligiousSpell
{
    public static final int RANGE = 8;
    
    public Dirt() {
        super("Dirt", 453, 10, 20, 50, 40, 0L);
        this.targetTile = true;
        this.targetItem = true;
        this.description = "creates and destroys dirt";
    }
    
    @Override
    boolean precondition(final Skill castSkill, final Creature performer, final int tilex, final int tiley, final int layer) {
        if (layer >= 0) {
            for (int x = -2; x <= 1; ++x) {
                for (int y = -2; y <= 1; ++y) {
                    if (isBlocked(tilex + x, tiley + y, performer)) {
                        return false;
                    }
                }
            }
            return true;
        }
        performer.getCommunicator().sendNormalServerMessage("This spell does not work on rock.", (byte)3);
        return false;
    }
    
    @Override
    boolean precondition(final Skill castSkill, final Creature performer, final Item target) {
        if (!target.isHollow() || (target.getTemplate().hasViewableSubItems() && !target.getTemplate().isContainerWithSubItems())) {
            performer.getCommunicator().sendNormalServerMessage("The " + target.getName() + " is not a container.", (byte)3);
            return false;
        }
        if (target.isBulkContainer()) {
            if (target.getTemplateId() == 661) {
                performer.getCommunicator().sendNormalServerMessage("The spell wont work on the " + target.getName() + ".", (byte)3);
                return false;
            }
            final long topParent = target.getTopParent();
            if (topParent != -10L && topParent == performer.getInventory().getWurmId()) {
                performer.getCommunicator().sendNormalServerMessage("The " + target.getName() + " must not be in your inventory.", (byte)3);
                return false;
            }
        }
        if (target.isLockable() && target.getLockId() != -10L && !MethodsItems.mayUseInventoryOfVehicle(performer, target) && !MethodsItems.hasKeyForContainer(performer, target)) {
            performer.getCommunicator().sendNormalServerMessage("You must be able to open the " + target.getName() + " to create dirt inside of it.", (byte)3);
            return false;
        }
        if (target.getTemplateId() == 1028) {
            performer.getCommunicator().sendNormalServerMessage("The " + target.getName() + " can only hold ore.", (byte)3);
            return false;
        }
        if (target.isTent() && target.getParentOrNull() != null) {
            performer.getCommunicator().sendNormalServerMessage("You cannot create dirt inside of that.", (byte)3);
            return false;
        }
        if (target.isCrate()) {
            final int nums = target.getRemainingCrateSpace();
            if (nums <= 0) {
                performer.getCommunicator().sendNormalServerMessage("The " + target.getName() + " will not be able to contain all that dirt.", (byte)3);
                return false;
            }
        }
        else {
            final int sizeLeft = target.getFreeVolume();
            try {
                final ItemTemplate template = ItemTemplateFactory.getInstance().getTemplate(26);
                final int nums2 = sizeLeft / template.getVolume();
                if (nums2 <= 0) {
                    performer.getCommunicator().sendNormalServerMessage("The " + target.getName() + " will not be able to contain all that dirt.", (byte)3);
                    return false;
                }
                if (target.getOwnerId() == performer.getWurmId() && !performer.canCarry(template.getWeightGrams())) {
                    performer.getCommunicator().sendNormalServerMessage("You would not be able to carry all that dirt.", (byte)3);
                    return false;
                }
                if (target.isContainerLiquid()) {
                    for (final Item i : target.getAllItems(false)) {
                        if (i.isLiquid()) {
                            performer.getCommunicator().sendNormalServerMessage("That would destroy the liquid.", (byte)3);
                            return false;
                        }
                    }
                }
            }
            catch (NoSuchTemplateException nst) {
                return false;
            }
        }
        return true;
    }
    
    @Override
    void doEffect(final Skill castSkill, final double power, final Creature performer, final Item target) {
        final int sizeLeft = target.getFreeVolume();
        try {
            final ItemTemplate template = ItemTemplateFactory.getInstance().getTemplate(26);
            boolean created = false;
            int nums = Math.min(6, sizeLeft / template.getVolume());
            if (target.isCrate()) {
                nums = Math.min(6, target.getRemainingCrateSpace());
            }
            if (nums > 0) {
                if (target.isBulkContainer()) {
                    final Item dirt = ItemFactory.createItem(26, (float)power, performer.getName());
                    dirt.setWeight(template.getWeightGrams() * nums, true);
                    dirt.setMaterial(template.getMaterial());
                    if (target.isCrate()) {
                        dirt.AddBulkItemToCrate(performer, target);
                    }
                    else {
                        dirt.AddBulkItem(performer, target);
                    }
                    created = true;
                }
                else {
                    for (int x = 0; x < nums; ++x) {
                        if (target.getOwnerId() == performer.getWurmId()) {
                            if (!performer.canCarry(template.getWeightGrams())) {
                                if (created) {
                                    performer.getCommunicator().sendNormalServerMessage("You create some dirt.", (byte)2);
                                }
                                return;
                            }
                        }
                        else if (!target.mayCreatureInsertItem()) {
                            if (created) {
                                performer.getCommunicator().sendNormalServerMessage("You create some dirt.", (byte)2);
                            }
                            return;
                        }
                        final Item dirt2 = ItemFactory.createItem(26, (float)power, performer.getName());
                        target.insertItem(dirt2);
                        created = true;
                    }
                }
            }
            if (created) {
                performer.getCommunicator().sendNormalServerMessage("You create some dirt.", (byte)2);
            }
        }
        catch (NoSuchTemplateException ex) {}
        catch (FailedException ex2) {}
    }
    
    private static final boolean isBlocked(final int tx, final int ty, final Creature performer) {
        final int otile = Server.surfaceMesh.getTile(tx, ty);
        final int diff = Math.abs(Terraforming.getMaxSurfaceDifference(otile, tx, ty));
        if (diff > 270) {
            performer.getCommunicator().sendNormalServerMessage("The slope would crumble.", (byte)3);
            return true;
        }
        for (int x = 0; x >= -1; --x) {
            for (int y = 0; y >= -1; --y) {
                try {
                    final int tile = Server.surfaceMesh.getTile(tx + x, ty + y);
                    final byte type = Tiles.decodeType(tile);
                    if (Terraforming.isNonDiggableTile(type)) {
                        performer.getCommunicator().sendNormalServerMessage("You need to clear the area first.", (byte)3);
                        return true;
                    }
                    if (Terraforming.isRoad(type)) {
                        performer.getCommunicator().sendNormalServerMessage("The road is too hard.", (byte)3);
                        return true;
                    }
                    if (type == Tiles.Tile.TILE_CLAY.id || type == Tiles.Tile.TILE_TAR.id || type == Tiles.Tile.TILE_PEAT.id) {
                        return true;
                    }
                    if (Tiles.decodeHeight(tile) < -3000) {
                        performer.getCommunicator().sendNormalServerMessage("Nothing happens at this depth.", (byte)3);
                        return true;
                    }
                    final Zone zone = Zones.getZone(tx + x, ty + y, performer.isOnSurface());
                    final VolaTile vtile = zone.getTileOrNull(tx + x, ty + y);
                    if (vtile != null) {
                        if (vtile.getStructure() != null) {
                            performer.getCommunicator().sendNormalServerMessage("The structure is in the way.", (byte)3);
                            return true;
                        }
                        final Fence[] fences = vtile.getFencesForLevel(0);
                        if (fences.length > 0) {
                            if (x == 0 && y == 0) {
                                performer.getCommunicator().sendNormalServerMessage("The " + fences[0].getName() + " is in the way.", (byte)3);
                                return true;
                            }
                            if (x == -1 && y == 0) {
                                for (final Fence f : fences) {
                                    if (f.isHorizontal()) {
                                        final String wname = f.getName();
                                        performer.getCommunicator().sendNormalServerMessage("The " + wname + " is in the way.", (byte)3);
                                        return true;
                                    }
                                }
                            }
                            else if (y == -1 && x == 0) {
                                for (final Fence f : fences) {
                                    if (!f.isHorizontal()) {
                                        final String wname = f.getName();
                                        performer.getCommunicator().sendNormalServerMessage("The " + wname + " is in the way.", (byte)3);
                                        return true;
                                    }
                                }
                            }
                        }
                    }
                }
                catch (NoSuchZoneException nsz) {
                    performer.getCommunicator().sendNormalServerMessage("The water is too deep to dig in.", (byte)3);
                    return true;
                }
            }
        }
        return false;
    }
    
    @Override
    void doEffect(final Skill castSkill, final double power, final Creature performer, final int tilex, final int tiley, final int layer, final int heightOffset) {
        for (int x = -2; x <= 1; ++x) {
            for (int y = -2; y <= 1; ++y) {
                if (!isBlocked(tilex + x, tiley + y, performer)) {
                    final int tile = Server.surfaceMesh.getTile(tilex + x, tiley + y);
                    final byte oldType = Tiles.decodeType(tile);
                    final int rocktile = Server.rockMesh.getTile(tilex + x, tiley + y);
                    final short rockheight = Tiles.decodeHeight(rocktile);
                    short mod = 0;
                    if (x > -2 && y > -2) {
                        mod = 3;
                    }
                    if (x == 0 && y == 0) {
                        mod = 5;
                    }
                    final short newHeight = (short)Math.max(rockheight, Tiles.decodeHeight(tile) - mod);
                    byte type = Tiles.Tile.TILE_DIRT.id;
                    if (oldType == Tiles.Tile.TILE_SAND.id) {
                        type = oldType;
                    }
                    else if (oldType == Tiles.Tile.TILE_CLAY.id || oldType == Tiles.Tile.TILE_TAR.id || oldType == Tiles.Tile.TILE_PEAT.id) {
                        type = oldType;
                    }
                    else if (oldType == Tiles.Tile.TILE_MOSS.id) {
                        type = oldType;
                    }
                    else if (oldType == Tiles.Tile.TILE_MARSH.id) {
                        type = oldType;
                    }
                    if (Terraforming.allCornersAtRockLevel(tilex + x, tiley + y, Server.surfaceMesh)) {
                        type = Tiles.Tile.TILE_ROCK.id;
                    }
                    Server.setSurfaceTile(tilex + x, tiley + y, newHeight, type, (byte)0);
                    Players.getInstance().sendChangedTile(tilex + x, tiley + y, true, true);
                }
            }
        }
    }
}
