// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.behaviours;

import javax.annotation.Nonnull;
import com.wurmonline.server.villages.Village;
import com.wurmonline.server.villages.Villages;
import com.wurmonline.server.items.ItemTemplate;
import com.wurmonline.server.items.NoSuchTemplateException;
import com.wurmonline.server.items.ItemTemplateFactory;
import com.wurmonline.mesh.CaveTile;
import com.wurmonline.server.zones.Zone;
import com.wurmonline.server.structures.Fence;
import com.wurmonline.server.items.ItemFactory;
import com.wurmonline.server.items.RuneUtilities;
import com.wurmonline.server.zones.NoSuchZoneException;
import com.wurmonline.server.utils.logging.TileEvent;
import com.wurmonline.server.skills.NoSuchSkillException;
import com.wurmonline.server.Servers;
import com.wurmonline.math.TilePos;
import com.wurmonline.math.Vector2f;
import com.wurmonline.server.utils.CoordUtils;
import com.wurmonline.server.skills.Skill;
import com.wurmonline.server.skills.Skills;
import com.wurmonline.server.Players;
import java.util.logging.Level;
import com.wurmonline.server.sounds.SoundPlayer;
import com.wurmonline.server.GeneralUtilities;
import com.wurmonline.server.creatures.Communicator;
import com.wurmonline.server.Items;
import com.wurmonline.server.structures.Floor;
import com.wurmonline.server.structures.BridgePart;
import com.wurmonline.server.zones.VolaTile;
import com.wurmonline.server.zones.Zones;
import com.wurmonline.server.deities.Deity;
import com.wurmonline.server.highways.HighwayPos;
import com.wurmonline.server.Server;
import com.wurmonline.server.players.Player;
import com.wurmonline.server.highways.MethodsHighways;
import java.util.Collection;
import com.wurmonline.server.Features;
import com.wurmonline.mesh.Tiles;
import com.wurmonline.server.items.Item;
import java.util.List;
import com.wurmonline.server.creatures.Creature;
import java.util.logging.Logger;

public final class CaveTileBehaviour extends TileBehaviour
{
    private static final Logger logger;
    private static final float FLATTENING_MAX_DEPTH = -7.0f;
    
    CaveTileBehaviour() {
        super((short)39);
    }
    
    @Override
    public final boolean isCave() {
        return true;
    }
    
    @Override
    public List<ActionEntry> getBehavioursFor(final Creature performer, final int tilex, final int tiley, final boolean onSurface, final int tile, final int dir) {
        final List<ActionEntry> toReturn = super.getBehavioursFor(performer, tilex, tiley, onSurface, tile);
        if (performer.getDeity() != null && performer.getDeity().isMountainGod()) {
            Methods.addActionIfAbsent(toReturn, Actions.actionEntrys[141]);
        }
        return toReturn;
    }
    
    @Override
    public List<ActionEntry> getBehavioursFor(final Creature performer, final Item source, final int tilex, final int tiley, final boolean onSurface, final int tile, final int dir) {
        final List<ActionEntry> toReturn = super.getBehavioursFor(performer, source, tilex, tiley, onSurface, tile);
        final byte type = Tiles.decodeType(tile);
        final int templateId = source.getTemplateId();
        if (Features.Feature.CAVE_DWELLINGS.isEnabled() && (Tiles.isReinforcedFloor(type) || Tiles.isRoadType(type)) && dir == 0) {
            toReturn.addAll(TileBehaviour.getBuildableTileBehaviours(tilex, tiley, performer, templateId));
        }
        if (templateId == 492 && type == Tiles.Tile.TILE_CAVE_FLOOR_REINFORCED.id && dir == 0) {
            toReturn.add(new ActionEntry((short)155, "Prepare", "preparing the floor"));
        }
        else if (templateId == 97 && type == Tiles.Tile.TILE_CAVE_PREPATED_FLOOR_REINFORCED.id && dir == 0) {
            toReturn.add(new ActionEntry((short)191, "Remove mortar", "removing mortar"));
        }
        else if (source.isCavePaveable() && type == Tiles.Tile.TILE_CAVE_PREPATED_FLOOR_REINFORCED.id && dir == 0) {
            toReturn.add(Actions.actionEntrys[155]);
        }
        else if (Tiles.isRoadType(type) && dir == 0) {
            if (source.isPaveable() && source.getTemplateId() != 495) {
                final HighwayPos highwaypos = MethodsHighways.getHighwayPos(tilex, tiley, onSurface);
                if (MethodsHighways.onHighway(highwaypos)) {
                    toReturn.add(new ActionEntry((short)155, "Replace paving", "re-paving"));
                }
            }
            else if (templateId == 1115) {
                final HighwayPos highwaypos = MethodsHighways.getHighwayPos(tilex, tiley, onSurface);
                if (!MethodsHighways.onHighway(highwaypos)) {
                    toReturn.add(Actions.actionEntrys[191]);
                }
            }
        }
        else if (templateId == 153 && type == Tiles.Tile.TILE_PLANKS.id && dir == 0) {
            toReturn.add(new ActionEntry((short)231, "Tar", "tarring"));
        }
        if (source.isMiningtool() && (dir == 1 || type == Tiles.Tile.TILE_CAVE.id || type == Tiles.Tile.TILE_CAVE_FLOOR_REINFORCED.id)) {
            int cnt = -2;
            boolean justrock = false;
            if ((dir == 0 && type == Tiles.Tile.TILE_CAVE.id) || (dir == 1 && (type == Tiles.Tile.TILE_CAVE.id || Tiles.isReinforcedFloor(type)))) {
                justrock = true;
                --cnt;
            }
            toReturn.add(new ActionEntry((short)cnt, "Mining", "Mining options"));
            if (type == Tiles.Tile.TILE_CAVE_FLOOR_REINFORCED.id && dir == 0) {
                toReturn.add(new ActionEntry((short)145, "Remove reinforcement", "removing reinforcement"));
            }
            else {
                toReturn.add(Actions.actionEntrys[145]);
            }
            toReturn.add(Actions.actionEntrys[156]);
            if (justrock) {
                if (tilex == performer.getTileX() && tiley == performer.getTileY()) {
                    toReturn.add(Actions.actionEntrys[150]);
                }
                else {
                    toReturn.add(Actions.actionEntrys[532]);
                }
            }
        }
        else if (source.getTemplateId() == 429 && dir == 0 && Tiles.decodeType(tile) == Tiles.Tile.TILE_CAVE.id) {
            toReturn.add(Actions.actionEntrys[229]);
        }
        else if (source.getTemplateId() == 782) {
            toReturn.add(Actions.actionEntrys[518]);
        }
        if (performer.getDeity() != null && performer.getDeity().isMountainGod()) {
            Methods.addActionIfAbsent(toReturn, Actions.actionEntrys[141]);
        }
        if (performer.getPower() > 1) {
            if ((source.getTemplateId() == 176 || source.getTemplateId() == 315) && !Tiles.isSolidCave(Tiles.decodeType(tile))) {
                final HighwayPos highwayPos = MethodsHighways.getHighwayPos(tilex, tiley, false);
                if (highwayPos == null || !MethodsHighways.onHighway(highwayPos)) {
                    toReturn.add(Actions.actionEntrys[193]);
                }
            }
            toReturn.add(Actions.actionEntrys[476]);
            if (performer.getPower() >= 4 && source.getTemplateId() == 176) {
                toReturn.add(Actions.actionEntrys[518]);
            }
        }
        if ((source.getTemplateId() == 601 || (WurmPermissions.mayUseDeityWand(performer) && source.getTemplateId() == 176) || (WurmPermissions.mayUseGMWand(performer) && source.getTemplateId() == 315)) && (Tiles.decodeType(tile) == Tiles.Tile.TILE_CAVE_EXIT.id || Tiles.decodeType(tile) == Tiles.Tile.TILE_CAVE.id || Tiles.isReinforcedFloor(Tiles.decodeType(tile)))) {
            final HighwayPos highwayPos = MethodsHighways.getHighwayPos(tilex, tiley, false);
            if (highwayPos == null || !MethodsHighways.onHighway(highwayPos)) {
                toReturn.add(new ActionEntry((short)193, "Collapse", "collapsing"));
            }
        }
        if (((Player)performer).isSendExtraBytes() && type == Tiles.Tile.TILE_CAVE_EXIT.id && dir == 0) {
            final byte fType = Server.getClientCaveFlags(tilex, tiley);
            if (source.isMiningtool() && type == Tiles.Tile.TILE_CAVE_EXIT.id && fType == Tiles.Tile.TILE_CAVE_FLOOR_REINFORCED.id) {
                toReturn.add(new ActionEntry((short)145, "Remove reinforcement", "removing reinforcement"));
            }
            if (templateId == 492 && fType == Tiles.Tile.TILE_CAVE_FLOOR_REINFORCED.id) {
                toReturn.add(new ActionEntry((short)155, "Prepare", "preparing the floor"));
            }
            else if (templateId == 97 && fType == Tiles.Tile.TILE_CAVE_PREPATED_FLOOR_REINFORCED.id) {
                toReturn.add(new ActionEntry((short)191, "Remove mortar", "removing mortar"));
            }
            else if (source.isCavePaveable() && fType == Tiles.Tile.TILE_CAVE_PREPATED_FLOOR_REINFORCED.id) {
                toReturn.add(Actions.actionEntrys[155]);
            }
            else if (templateId == 1115 && Tiles.isRoadType(fType)) {
                final HighwayPos highwaypos2 = MethodsHighways.getHighwayPos(tilex, tiley, onSurface);
                if (!MethodsHighways.onHighway(highwaypos2)) {
                    toReturn.add(Actions.actionEntrys[191]);
                }
            }
            else if (Tiles.isRoadType(fType)) {
                final HighwayPos highwaypos2 = MethodsHighways.getHighwayPos(tilex, tiley, onSurface);
                if (MethodsHighways.onHighway(highwaypos2)) {
                    toReturn.add(Actions.actionEntrys[155]);
                }
            }
            else if (templateId == 153 && fType == Tiles.Tile.TILE_PLANKS.id) {
                toReturn.add(new ActionEntry((short)231, "Tar", "tarring"));
            }
            else if (source.getTemplateId() == 429 && fType == 0) {
                toReturn.add(Actions.actionEntrys[229]);
            }
        }
        return toReturn;
    }
    
    @Override
    public boolean action(final Action act, final Creature performer, final int tilex, final int tiley, final boolean onSurface, final int tile, final int dir, final short action, final float counter) {
        boolean handled = false;
        boolean done = true;
        switch (action) {
            case 1: {
                if (dir == 0) {
                    final byte actualType = Tiles.decodeType(tile);
                    final byte type = (actualType == Tiles.Tile.TILE_CAVE_EXIT.id) ? Server.getClientCaveFlags(tilex, tiley) : actualType;
                    final String floorexit = (actualType == Tiles.Tile.TILE_CAVE_EXIT.id) ? "exit" : "floor";
                    if (type == Tiles.Tile.TILE_CAVE_FLOOR_REINFORCED.id) {
                        performer.getCommunicator().sendNormalServerMessage("You see a cave " + floorexit + " has been reinforced with thick wooden beams and metal bands.");
                    }
                    else if (type == Tiles.Tile.TILE_CAVE_PREPATED_FLOOR_REINFORCED.id) {
                        performer.getCommunicator().sendNormalServerMessage("You see a reinforced cave " + floorexit + " which has been prepared ready for paving.");
                    }
                    else if (Tiles.isRoadType(type)) {
                        performer.getCommunicator().sendNormalServerMessage("You see a paved cave " + floorexit + ".");
                    }
                    else if (actualType == Tiles.Tile.TILE_CAVE_EXIT.id) {
                        performer.getCommunicator().sendNormalServerMessage("You see cave exit.");
                    }
                    else {
                        performer.getCommunicator().sendNormalServerMessage("You see dark dungeons.");
                    }
                }
                else {
                    performer.getCommunicator().sendNormalServerMessage("You see a ceiling.");
                }
                TileBehaviour.sendVillageString(performer, tilex, tiley, false);
                handled = true;
                break;
            }
            case 141: {
                final Deity deity = performer.getDeity();
                if (deity != null && deity.isMountainGod()) {
                    done = MethodsReligion.pray(act, performer, counter);
                    handled = true;
                    break;
                }
                break;
            }
            default: {
                handled = false;
                break;
            }
        }
        if (!handled) {
            done = super.action(act, performer, tilex, tiley, onSurface, tile, action, counter);
        }
        return done;
    }
    
    private static final boolean isBlocked(final int tilex, final int tiley, final int digTilex, final int digTiley, final Creature performer, final short action, final float counter, final int dir, final int h, final int cceil, final int encodedTile) {
        if (!Methods.isActionAllowed(performer, action, false, tilex, tiley, encodedTile, dir)) {
            return true;
        }
        final VolaTile dropTile = Zones.getTileOrNull(tilex, tiley, performer.isOnSurface());
        if (dropTile != null && dropTile.getNumberOfItems(performer.getFloorLevel()) > 99) {
            performer.getCommunicator().sendNormalServerMessage("There is no space to mine here. Clear the area first.");
            return true;
        }
        boolean out = false;
        if (dir == 1) {
            if (TileRockBehaviour.allCornersAtRockHeight(digTilex, digTiley)) {
                out = true;
            }
        }
        else if (dir != 0) {
            short maxHeight = -25;
            final short currHeight = Tiles.decodeHeight(Server.caveMesh.getTile(digTilex, digTiley));
            for (int x = -1; x <= 1; ++x) {
                for (int y = -1; y <= 1; ++y) {
                    if ((y == 0 || x == 0) && (x != 0 || y != 0)) {
                        boolean check = true;
                        final int tt = Server.caveMesh.getTile(digTilex + x, digTiley + y);
                        if (y == 0) {
                            final int tt2 = Server.caveMesh.getTile(digTilex + x, digTiley - 1);
                            if (Tiles.isSolidCave(Tiles.decodeType(tt2)) && Tiles.isSolidCave(Tiles.decodeType(tt))) {
                                check = false;
                            }
                        }
                        if (x == 0) {
                            final int tt2 = Server.caveMesh.getTile(digTilex - 1, digTiley + y);
                            if (Tiles.isSolidCave(Tiles.decodeType(tt2)) && Tiles.isSolidCave(Tiles.decodeType(tt))) {
                                check = false;
                            }
                        }
                        if (check) {
                            final short height = Tiles.decodeHeight(tt);
                            if (height > maxHeight) {
                                maxHeight = height;
                            }
                        }
                    }
                }
            }
            if (maxHeight - currHeight > 200) {
                performer.getCommunicator().sendNormalServerMessage("The ground is too steep to mine at here. You need to make it more flat.");
                return true;
            }
        }
        else {
            final byte type = Tiles.decodeType(encodedTile);
            if (!Tiles.isReinforcedFloor(type)) {
                if (anyAdjacentReinforcedFloors(tilex, tiley, false)) {
                    performer.getCommunicator().sendNormalServerMessage("You can not mine next to reinforced floors.");
                    return true;
                }
            }
        }
        if (out) {
            if (!Terraforming.allCornersAtRockLevel(tilex, tiley, Server.surfaceMesh)) {
                performer.getCommunicator().sendNormalServerMessage("The roof sounds strangely hollow and you notice dirt flowing in, so you stop mining.");
                return true;
            }
            if (counter == 1.0f) {
                Server.getInstance().broadCastAction(performer.getName() + " starts mining a hole to the outside.", performer, 5);
                performer.getCommunicator().sendNormalServerMessage("You begin to mine your way to the outside.");
            }
        }
        else if (dir == 1 && isAtRockLevelAndNotRock(digTilex, digTiley, h + cceil)) {
            performer.getCommunicator().sendNormalServerMessage("The roof sounds strangely hollow and you notice dirt flowing in, so you stop mining.");
            return true;
        }
        return false;
    }
    
    @Override
    public boolean action(final Action act, final Creature performer, final Item source, final int tilex, final int tiley, final boolean onSurface, final int heightOffset, final int tile, final int dir, final short action, final float counter) {
        boolean done = true;
        boolean handled = false;
        final byte type = Tiles.decodeType(tile);
        final byte fType = Server.getClientCaveFlags(tilex, tiley);
        switch (action) {
            case 518: {
                if (performer.getPower() < 4 || source.getTemplateId() != 176) {
                    if (source.getTemplateId() != 782) {
                        handled = false;
                        break;
                    }
                }
                handled = true;
                if (anyReinforcedFloors(performer)) {
                    performer.getCommunicator().sendNormalServerMessage("You cannot raise reinforced floors.");
                    done = true;
                    break;
                }
                final int digTilex = (int)performer.getStatus().getPositionX() + 2 >> 2;
                final int digTiley = (int)performer.getStatus().getPositionY() + 2 >> 2;
                done = raiseRockLevel(performer, source, digTilex, digTiley, counter, act);
                break;
            }
            case 145: {
                if (source.isMiningtool() && (dir == 1 || type == Tiles.Tile.TILE_CAVE.id || type == Tiles.Tile.TILE_CAVE_FLOOR_REINFORCED.id)) {
                    handled = true;
                    done = this.handle_MINE(act, performer, source, tilex, tiley, action, counter, dir);
                }
                if (source.isMiningtool() && type == Tiles.Tile.TILE_CAVE_EXIT.id && fType == Tiles.Tile.TILE_CAVE_FLOOR_REINFORCED.id) {
                    handled = true;
                    done = this.handle_MINE(act, performer, source, tilex, tiley, action, counter, dir);
                    break;
                }
                break;
            }
            case 156: {
                if (source.isMiningtool() && (dir == 1 || type == Tiles.Tile.TILE_CAVE.id || type == Tiles.Tile.TILE_CAVE_FLOOR_REINFORCED.id)) {
                    handled = true;
                    done = this.handle_PROSPECT(performer, source, tilex, tiley, tile, counter, dir);
                    break;
                }
                break;
            }
            case 150: {
                if (source.isMiningtool() && (dir == 1 || type == Tiles.Tile.TILE_CAVE.id || type == Tiles.Tile.TILE_CAVE_FLOOR_REINFORCED.id)) {
                    handled = true;
                    done = this.handle_FLATTEN(act, performer, source, tilex, tiley, tile, counter, dir);
                    break;
                }
                break;
            }
            case 532: {
                if (source.isMiningtool() && (dir == 1 || type == Tiles.Tile.TILE_CAVE.id || type == Tiles.Tile.TILE_CAVE_FLOOR_REINFORCED.id)) {
                    handled = true;
                    done = this.handle_LEVEL(act, performer, source, tilex, tiley, tile, counter, dir);
                    break;
                }
                break;
            }
            case 229: {
                if (source.getTemplateId() == 429) {
                    handled = true;
                    done = this.handle_REINFORCE(performer, source, tilex, tiley, tile, counter, dir);
                    break;
                }
                break;
            }
            case 141: {
                if (performer.getDeity() != null && performer.getDeity().isMountainGod()) {
                    done = MethodsReligion.pray(act, performer, counter);
                    handled = true;
                    break;
                }
                break;
            }
            case 193: {
                done = true;
                handled = true;
                this.handle_REPAIR_STRUCT(performer, source, tilex, tiley, tile);
                break;
            }
            case 176: {
                handled = true;
                if (source.isRoadMarker() && Features.Feature.HIGHWAYS.isEnabled() && !source.isTraded()) {
                    final HighwayPos highwayPos = MethodsHighways.getNewHighwayPosCorner(performer, tilex, tiley, onSurface, null, null);
                    if (highwayPos != null && MethodsHighways.middleOfHighway(highwayPos) && !MethodsHighways.containsMarker(highwayPos, (byte)0)) {
                        final byte pLinks = MethodsHighways.getPossibleLinksFrom(highwayPos, source);
                        if (!MethodsHighways.canPlantMarker(performer, highwayPos, source, pLinks)) {
                            done = true;
                        }
                        else if (performer.getPower() > 0) {
                            done = MethodsItems.plantSignFinish(performer, source, true, highwayPos.getTilex(), highwayPos.getTiley(), highwayPos.isOnSurface(), highwayPos.getBridgeId(), false, -1L);
                        }
                        else {
                            done = MethodsItems.plantSign(performer, source, counter, true, highwayPos.getTilex(), highwayPos.getTiley(), highwayPos.isOnSurface(), highwayPos.getBridgeId(), false, -1L);
                        }
                        if (done && source.isPlanted()) {
                            MethodsHighways.autoLink(source, pLinks);
                        }
                    }
                    break;
                }
                break;
            }
            case 155: {
                if (source.getTemplateId() == 492 && dir == 0 && (type == Tiles.Tile.TILE_CAVE_FLOOR_REINFORCED.id || (type == Tiles.Tile.TILE_CAVE_EXIT.id && fType == Tiles.Tile.TILE_CAVE_FLOOR_REINFORCED.id))) {
                    handled = true;
                    done = this.changeFloor(act, performer, source, tilex, tiley, tile, action, counter);
                }
                if (source.isCavePaveable() && dir == 0 && (type == Tiles.Tile.TILE_CAVE_PREPATED_FLOOR_REINFORCED.id || (type == Tiles.Tile.TILE_CAVE_EXIT.id && fType == Tiles.Tile.TILE_CAVE_PREPATED_FLOOR_REINFORCED.id))) {
                    handled = true;
                    done = this.changeFloor(act, performer, source, tilex, tiley, tile, action, counter);
                }
                if (source.isCavePaveable() && dir == 0 && (Tiles.isRoadType(type) || (type == Tiles.Tile.TILE_CAVE_EXIT.id && Tiles.isRoadType(fType)))) {
                    handled = true;
                    done = this.changeFloor(act, performer, source, tilex, tiley, tile, action, counter);
                    break;
                }
                break;
            }
            case 191: {
                if (source.getTemplateId() == 1115 && dir == 0 && (Tiles.isRoadType(type) || (type == Tiles.Tile.TILE_CAVE_EXIT.id && Tiles.isRoadType(fType)))) {
                    handled = true;
                    done = Terraforming.destroyPave(performer, source, tilex, tiley, onSurface, tile, counter);
                }
                if (source.getTemplateId() == 97 && dir == 0 && (type == Tiles.Tile.TILE_CAVE_PREPATED_FLOOR_REINFORCED.id || (type == Tiles.Tile.TILE_CAVE_EXIT.id && fType == Tiles.Tile.TILE_CAVE_PREPATED_FLOOR_REINFORCED.id))) {
                    handled = true;
                    done = this.changeFloor(act, performer, source, tilex, tiley, tile, action, counter);
                    break;
                }
                break;
            }
            case 231: {
                if (source.getTemplateId() == 153 && dir == 0 && (type == Tiles.Tile.TILE_PLANKS.id || (type == Tiles.Tile.TILE_CAVE_EXIT.id && fType == Tiles.Tile.TILE_PLANKS.id))) {
                    handled = true;
                    done = Terraforming.tarFloor(performer, source, tilex, tiley, onSurface, tile, counter);
                    break;
                }
                break;
            }
            case 1: {
                handled = true;
                done = this.action(act, performer, source, tilex, tiley, onSurface, heightOffset, tile, dir, action, counter);
                break;
            }
            default: {
                handled = false;
                break;
            }
        }
        if (!handled) {
            done = super.action(act, performer, source, tilex, tiley, onSurface, heightOffset, tile, action, counter);
        }
        return done;
    }
    
    private void handle_REPAIR_STRUCT(final Creature performer, final Item source, final int tilex, final int tiley, final int tile) {
        final int templateId = source.getTemplateId();
        final boolean shakerOrb = templateId == 601;
        final boolean hasSpecialPermission = (WurmPermissions.mayUseDeityWand(performer) && source.getTemplateId() == 176) || (WurmPermissions.mayUseGMWand(performer) && source.getTemplateId() == 315);
        if (!shakerOrb && !hasSpecialPermission) {
            return;
        }
        final byte decodedType = Tiles.decodeType(tile);
        if (decodedType != Tiles.Tile.TILE_CAVE_EXIT.id && decodedType != Tiles.Tile.TILE_CAVE.id && !Tiles.isReinforcedFloor(decodedType)) {
            return;
        }
        final Communicator comm = performer.getCommunicator();
        final HighwayPos highwayPos = MethodsHighways.getHighwayPos(tilex, tiley, false);
        if (highwayPos != null && MethodsHighways.onHighway(highwayPos)) {
            return;
        }
        final VolaTile t = Zones.getOrCreateTile(tilex, tiley, false);
        if (t.getStructure() != null) {
            comm.sendNormalServerMessage("You cannot do that inside a cave dwelling.");
            return;
        }
        if (t.getVillage() == null) {
            for (int x = -1; x <= 1; ++x) {
                for (int y = -1; y <= 1; ++y) {
                    if (x != 0 || y != 0) {
                        final VolaTile vt = Zones.getTileOrNull(tilex + x, tiley + y, false);
                        if (vt != null && vt.getStructure() != null) {
                            comm.sendNormalServerMessage("The nearby cave dwelling interfears with that.");
                            return;
                        }
                    }
                }
            }
        }
        if (t != null) {
            if (t.getCreatures().length > 0) {
                comm.sendNormalServerMessage("That tile is occupied by creatures.");
                return;
            }
            if (!shakerOrb) {
                t.destroyEverything();
            }
            else if (t.getItems().length > 0) {
                comm.sendNormalServerMessage("You should remove the items first.");
                return;
            }
        }
        final int ts = Server.surfaceMesh.getTile(tilex, tiley);
        final byte type = Tiles.decodeType(ts);
        if (type == Tiles.Tile.TILE_CAVE_EXIT.id) {
            final VolaTile surfaceTile = Zones.getTileOrNull(tilex, tiley, true);
            if (surfaceTile != null && surfaceTile.getCreatures().length > 0) {
                comm.sendNormalServerMessage("There are creatures in the way of the cave entrance.");
                return;
            }
        }
        if (Tiles.isMineDoor(type) && shakerOrb) {
            comm.sendNormalServerMessage("You need to destroy the mine door first.");
            return;
        }
        Terraforming.setAsRock(tilex, tiley, false);
        if (!shakerOrb) {
            if (performer.getPower() >= 5) {
                comm.sendNormalServerMessage("Tried to set " + tilex + "," + tiley + " to rock.");
            }
            else {
                comm.sendNormalServerMessage("Tried to set the tile to rock.");
            }
            return;
        }
        comm.sendNormalServerMessage("You throw the " + source.getName() + " on the ground. The earth suddenly shakes and the rock falls in!");
        Server.getInstance().broadCastAction(performer.getName() + " throws " + source.getNameWithGenus() + " on the ground. The earth suddenly shakes and the rock falls in!", performer, 5);
        Items.destroyItem(source.getWurmId());
        performer.achievement(151);
    }
    
    private boolean handle_REINFORCE(final Creature performer, final Item source, final int tilex, final int tiley, final int tile, final float counter, final int dir) {
        final Communicator comm = performer.getCommunicator();
        if (dir == 1) {
            comm.sendNormalServerMessage("You can not reinforce ceilings.");
            return true;
        }
        if (!Methods.isActionAllowed(performer, (short)229, false, tilex, tiley, tile, dir) || !Methods.isActionAllowed(performer, (short)145, false, tilex, tiley, tile, dir)) {
            return true;
        }
        if (!GeneralUtilities.isValidTileLocation(tilex, tiley)) {
            comm.sendNormalServerMessage("The water is too deep here.");
            return true;
        }
        if (performer.getFloorLevel() != 0) {
            comm.sendNormalServerMessage("You need to be stood on the ground to be able to do this.");
            return true;
        }
        boolean done = false;
        final Skills skills = performer.getSkills();
        final boolean insta = performer.getPower() > 3;
        final Skill mining = skills.getSkillOrLearn(1008);
        int time = 0;
        if (counter == 1.0f) {
            SoundPlayer.playSound("sound.work.masonry", tilex, tiley, performer.isOnSurface(), 1.0f);
            time = Math.min(250, Actions.getStandardActionTime(performer, mining, source, 0.0));
            try {
                performer.getCurrentAction().setTimeLeft(time);
            }
            catch (NoSuchActionException nsa) {
                CaveTileBehaviour.logger.log(Level.INFO, "This action does not exist?", nsa);
            }
            final String floorexit = (Tiles.decodeType(tile) == Tiles.Tile.TILE_CAVE_EXIT.id) ? "exit" : "floor";
            comm.sendNormalServerMessage("You start to reinforce the cave " + floorexit + ".");
            Server.getInstance().broadCastAction(performer.getName() + " starts to reinforce the cave " + floorexit + ".", performer, 5);
            performer.sendActionControl(Actions.actionEntrys[229].getVerbString(), true, time);
        }
        else {
            try {
                time = performer.getCurrentAction().getTimeLeft();
            }
            catch (NoSuchActionException nsa) {
                CaveTileBehaviour.logger.log(Level.INFO, "This action does not exist?", nsa);
            }
        }
        if (counter * 10.0f > time || insta) {
            mining.skillCheck(20.0, source, 0.0, false, counter);
            done = true;
            final String floorexit = (Tiles.decodeType(tile) == Tiles.Tile.TILE_CAVE_EXIT.id) ? "exit" : "floor";
            comm.sendNormalServerMessage("You reinforce the cave " + floorexit + ".");
            Server.getInstance().broadCastAction(performer.getName() + " reinforces the cave " + floorexit + ".", performer, 5);
            Items.destroyItem(source.getWurmId());
            if (Tiles.decodeType(tile) == Tiles.Tile.TILE_CAVE_EXIT.id) {
                Server.setClientCaveFlags(tilex, tiley, Tiles.Tile.TILE_CAVE_FLOOR_REINFORCED.id);
            }
            else {
                final int encodedValue = Tiles.encode(Tiles.decodeHeight(tile), Tiles.Tile.TILE_CAVE_FLOOR_REINFORCED.id, Tiles.decodeData(tile));
                Server.caveMesh.setTile(tilex, tiley, encodedValue);
            }
            Players.getInstance().sendChangedTile(tilex, tiley, false, true);
        }
        return done;
    }
    
    private boolean handle_LEVEL(final Action act, final Creature performer, final Item source, final int tilex, final int tiley, final int tile, final float counter, final int dir) {
        final int tx = performer.getTileX();
        final int ty = performer.getTileY();
        final int dx = Math.abs(tx - tilex);
        final int dy = Math.abs(ty - tiley);
        if (dx > 1 || dy > 1 || dx + dy < 1) {
            performer.getCommunicator().sendNormalServerMessage("You can only level tiles that you are adjacent to.");
            return true;
        }
        if (Tiles.decodeType(tile) == Tiles.Tile.TILE_CAVE_FLOOR_REINFORCED.id && dir == 0) {
            performer.getCommunicator().sendNormalServerMessage("You cannot level a reinforced floor.");
            return true;
        }
        if (Zones.isTileCornerProtected(tilex, tiley)) {
            performer.getCommunicator().sendNormalServerMessage("That tile is protected by the gods. You can not level there.");
            return true;
        }
        if (performer.getFloorLevel() != 0 && dir == 0) {
            performer.getCommunicator().sendNormalServerMessage("You need to be stood on the ground to be able to do this.");
            return true;
        }
        final boolean done = this.flatten(performer, source, tile, tilex, tiley, counter, act, dir);
        return done;
    }
    
    private boolean handle_FLATTEN(final Action act, final Creature performer, final Item source, final int tilex, final int tiley, final int tile, final float counter, final int dir) {
        if (tilex != performer.getTileX() && tiley != performer.getTileY()) {
            performer.getCommunicator().sendNormalServerMessage("You must stand on the tile you are trying to flatten.");
            return true;
        }
        if (Tiles.decodeType(tile) == Tiles.Tile.TILE_CAVE_FLOOR_REINFORCED.id && dir == 0) {
            performer.getCommunicator().sendNormalServerMessage("You cannot flatten a reinforced floor.");
            return true;
        }
        if (Zones.isTileCornerProtected(tilex, tiley)) {
            performer.getCommunicator().sendNormalServerMessage("This tile is protected by the gods. You can not flatten here.");
            return true;
        }
        if (performer.getFloorLevel() != 0 && dir == 0) {
            performer.getCommunicator().sendNormalServerMessage("You need to be stood on the ground to be able to do this.");
            return true;
        }
        final boolean done = this.flatten(performer, source, tile, tilex, tiley, counter, act, dir);
        return done;
    }
    
    private boolean handle_PROSPECT(final Creature performer, final Item source, final int tilex, final int tiley, final int tile, final float counter, final int dir) {
        final Communicator comm = performer.getCommunicator();
        if (!GeneralUtilities.isValidTileLocation(tilex, tiley)) {
            comm.sendNormalServerMessage("The water is too deep to prospect.");
            return true;
        }
        if (performer.getFloorLevel() != 0) {
            comm.sendNormalServerMessage("You need to be stood on the ground to be able to do this.");
            return true;
        }
        final short h = Tiles.decodeHeight(tile);
        if (h <= -25 && dir != 1) {
            comm.sendNormalServerMessage("The water is too deep to prospect.");
            return true;
        }
        if (dir == 0 && Tiles.isReinforcedFloor(Tiles.decodeType(tile))) {
            comm.sendNormalServerMessage("This floor is reinforced and may not be prospected.");
            return true;
        }
        final Skills skills = performer.getSkills();
        final Skill prospecting = skills.getSkillOrLearn(10032);
        int time = 0;
        if (counter == 1.0f) {
            String sstring = "sound.work.prospecting1";
            final int x = Server.rand.nextInt(3);
            if (x == 0) {
                sstring = "sound.work.prospecting2";
            }
            else if (x == 1) {
                sstring = "sound.work.prospecting3";
            }
            SoundPlayer.playSound(sstring, tilex, tiley, performer.isOnSurface(), 1.0f);
            time = (int)Math.max(30.0, 100.0 - prospecting.getKnowledge(source, 0.0));
            try {
                performer.getCurrentAction().setTimeLeft(time);
            }
            catch (NoSuchActionException nsa) {
                CaveTileBehaviour.logger.log(Level.INFO, "This action does not exist?", nsa);
            }
            comm.sendNormalServerMessage("You start to gather fragments of the rock.");
            Server.getInstance().broadCastAction(performer.getName() + " starts gathering fragments of the rock.", performer, 5);
            performer.sendActionControl(Actions.actionEntrys[156].getVerbString(), true, time);
        }
        else {
            try {
                time = performer.getCurrentAction().getTimeLeft();
            }
            catch (NoSuchActionException nsa2) {
                CaveTileBehaviour.logger.log(Level.INFO, "This action does not exist?", nsa2);
            }
        }
        if (counter * 10.0f <= time) {
            return false;
        }
        performer.getStatus().modifyStamina(-3000.0f);
        prospecting.skillCheck(1.0, source, 0.0, false, counter);
        source.setDamage(source.getDamage() + 5.0E-4f * source.getDamageModifier());
        final String findString = "only rock.";
        comm.sendNormalServerMessage("You find only rock.");
        if (prospecting.getKnowledge(0.0) > 20.0) {
            CaveTileBehaviour.r.setSeed((tilex + tiley * Zones.worldTileSizeY) * 789221L);
            final int m = 100;
            final int max = Math.min(100, 20 + CaveTileBehaviour.r.nextInt(80));
            comm.sendNormalServerMessage("It is of " + TileBehaviour.getShardQlDescription(max) + ".");
        }
        if (prospecting.getKnowledge(0.0) > 40.0) {
            CaveTileBehaviour.r.setSeed((tilex + tiley * Zones.worldTileSizeY) * 102533L);
            if (CaveTileBehaviour.r.nextInt(100) == 0) {
                comm.sendNormalServerMessage("You will find salt here!");
            }
        }
        if (prospecting.getKnowledge(0.0) > 20.0) {
            CaveTileBehaviour.r.setSeed((tilex + tiley * Zones.worldTileSizeY) * 6883L);
            if (CaveTileBehaviour.r.nextInt(200) == 0) {
                comm.sendNormalServerMessage("You will find flint here!");
            }
        }
        return true;
    }
    
    private boolean handle_MINE(final Action act, final Creature performer, final Item source, final int tilex, final int tiley, final short action, final float counter, final int dir) {
        final Vector2f pos2f = performer.getPos2f();
        final TilePos digTilePos = CoordUtils.WorldToTile(pos2f.add(2.0f, 2.0f));
        final int digTilex = digTilePos.x;
        final int digTiley = digTilePos.y;
        if (digTilex > tilex + 1 || digTilex < tilex || digTiley > tiley + 1 || digTiley < tiley) {
            if (performer.getPower() > 1) {
                performer.getCommunicator().sendNormalServerMessage("You are too far away to mine at " + tilex + "," + tiley + ".");
            }
            else {
                performer.getCommunicator().sendNormalServerMessage("You are too far away to mine there.");
            }
            return true;
        }
        return mine(act, performer, source, tilex, tiley, action, counter, dir, digTilePos);
    }
    
    public static boolean mine(final Action act, final Creature performer, final Item source, final int tilex, final int tiley, final short action, final float counter, final int dir, final TilePos digTilePos) {
        final Communicator comm = performer.getCommunicator();
        final int digCorner = Server.caveMesh.getTile(digTilePos);
        final int digTilex = digTilePos.x;
        final int digTiley = digTilePos.y;
        if (!GeneralUtilities.isValidTileLocation(tilex, tiley)) {
            comm.sendNormalServerMessage("The water is too deep to mine.");
            return true;
        }
        if (performer.isOnSurface()) {
            comm.sendNormalServerMessage("You are too far away to mine there.");
            return true;
        }
        if (performer.getFloorLevel() != 0 && dir == 0) {
            comm.sendNormalServerMessage("You need to be stood on the ground to be able to do this.");
            return true;
        }
        final int meshTile = Server.caveMesh.getTile(tilex, tiley);
        final byte actualType = Tiles.decodeType(meshTile);
        final byte tileType = (actualType == Tiles.Tile.TILE_CAVE_EXIT.id) ? Server.getClientCaveFlags(tilex, tiley) : actualType;
        if (Zones.isTileCornerProtected(digTilex, digTiley)) {
            comm.sendNormalServerMessage("This tile is protected by the gods. You can not mine here.");
            return true;
        }
        final int h = Tiles.decodeHeight(digCorner);
        if (h <= -25 && dir != 1 && tileType != Tiles.Tile.TILE_CAVE_FLOOR_REINFORCED.id) {
            comm.sendNormalServerMessage("The water is too deep to mine.");
            return true;
        }
        final short cceil = (short)(Tiles.decodeData(digCorner) & 0xFF);
        if (dir == 1) {
            if (cceil > 60 + performer.getFloorLevel() * 30) {
                comm.sendNormalServerMessage("You cannot reach the ceiling.");
                return true;
            }
        }
        else {
            if (cceil >= 254) {
                comm.sendNormalServerMessage("Lowering the floor further would make the cavern unstable.");
                return true;
            }
            if (dir == 0 && tileType == Tiles.Tile.TILE_CAVE_FLOOR_REINFORCED.id && performer.getPower() < 2) {
                if (performer.getStrengthSkill() < 21.0) {
                    comm.sendNormalServerMessage("You need to be stronger in order to remove the reinforcement.");
                    return true;
                }
                if (!Methods.isActionAllowed(performer, (short)229, false, tilex, tiley, meshTile, dir) || !Methods.isActionAllowed(performer, (short)145, false, tilex, tiley, meshTile, dir)) {
                    return true;
                }
                final VolaTile t = Zones.getTileOrNull(tilex, tiley, false);
                if (t != null && t.getStructure() != null) {
                    comm.sendNormalServerMessage("You cannot remove the reinforcement inside cave dwellings.");
                    return true;
                }
            }
        }
        if (Tiles.decodeData(digCorner) == 0 && Tiles.decodeHeight(digCorner) == Tiles.decodeHeight(Server.surfaceMesh.getTile(digTilePos))) {
            comm.sendNormalServerMessage("You fail to produce anything here. The rock is stone hard.");
            return true;
        }
        if (dir == 0 && tileType != Tiles.Tile.TILE_CAVE_FLOOR_REINFORCED.id && anyReinforcedFloors(performer)) {
            comm.sendNormalServerMessage("You cannot mine a floor next to reinforced floors.");
            return true;
        }
        final Skills skills = performer.getSkills();
        final Skill mining = skills.getSkillOrLearn(1008);
        Skill tool = null;
        final boolean insta = performer.getPower() >= 5 || (performer.getPower() >= 4 && Servers.isThisATestServer());
        try {
            tool = skills.getSkill(source.getPrimarySkill());
        }
        catch (Exception ex2) {
            try {
                tool = skills.learn(source.getPrimarySkill(), 1.0f);
            }
            catch (NoSuchSkillException nse) {
                CaveTileBehaviour.logger.log(Level.WARNING, performer.getName() + " trying to mine with an item with no primary skill: " + source.getName());
            }
        }
        if (dir == 0) {
            for (int x = 0; x >= -1; --x) {
                for (int y = 0; y >= -1; --y) {
                    final VolaTile vt = Zones.getTileOrNull(digTilex + x, digTiley + y, false);
                    if (vt != null && vt.getStructure() != null) {
                        if (vt.getStructure().isTypeHouse()) {
                            if (x == 0 && y == 0) {
                                performer.getCommunicator().sendNormalServerMessage("You cannot mine in a building.", (byte)3);
                            }
                            else {
                                performer.getCommunicator().sendNormalServerMessage("You cannot mine next to a building.", (byte)3);
                            }
                            return true;
                        }
                        for (final BridgePart bp : vt.getBridgeParts()) {
                            if (bp.getType().isSupportType()) {
                                performer.getCommunicator().sendNormalServerMessage("The bridge support nearby prevents mining.");
                                return true;
                            }
                            if ((x == -1 && bp.hasEastExit()) || (x == 0 && bp.hasWestExit()) || (y == -1 && bp.hasSouthExit()) || (y == 0 && bp.hasNorthExit())) {
                                performer.getCommunicator().sendNormalServerMessage("The end of the bridge nearby prevents mining.");
                                return true;
                            }
                        }
                    }
                }
            }
            VolaTile vt2 = Zones.getTileOrNull(digTilex, digTiley, false);
            if (vt2 != null && vt2.getFencesForLevel(0).length > 0) {
                performer.getCommunicator().sendNormalServerMessage("You cannot mine next to a fence.", (byte)3);
                return true;
            }
            vt2 = Zones.getTileOrNull(digTilex, digTiley - 1, false);
            if (vt2 != null && vt2.getFencesForLevel(0).length > 0) {
                for (final Fence f : vt2.getFencesForLevel(0)) {
                    if (!f.isHorizontal()) {
                        performer.getCommunicator().sendNormalServerMessage("You cannot mine next to a fence.", (byte)3);
                        return true;
                    }
                }
            }
            vt2 = Zones.getTileOrNull(digTilex - 1, digTiley, false);
            if (vt2 != null && vt2.getFencesForLevel(0).length > 0) {
                for (final Fence f : vt2.getFencesForLevel(0)) {
                    if (f.isHorizontal()) {
                        performer.getCommunicator().sendNormalServerMessage("You cannot mine next to a fence.", (byte)3);
                        return true;
                    }
                }
            }
        }
        int time = 0;
        if (counter == 1.0f) {
            final boolean isBlocked = isBlocked(tilex, tiley, digTilex, digTiley, performer, action, counter, dir, h, cceil, meshTile);
            if (isBlocked) {
                return true;
            }
            Server.getInstance().broadCastAction(performer.getName() + " starts mining.", performer, 5);
            comm.sendNormalServerMessage("You start to mine.");
            time = Actions.getStandardActionTime(performer, mining, source, 0.0);
            try {
                performer.getCurrentAction().setTimeLeft(time);
            }
            catch (NoSuchActionException nsa) {
                CaveTileBehaviour.logger.log(Level.INFO, "This action does not exist?", nsa);
            }
            performer.sendActionControl(Actions.actionEntrys[145].getVerbString(), true, time);
            source.setDamage(source.getDamage() + 0.0015f * source.getDamageModifier());
            performer.getStatus().modifyStamina(-1000.0f);
            return false;
        }
        else {
            try {
                time = performer.getCurrentAction().getTimeLeft();
            }
            catch (NoSuchActionException nsa2) {
                CaveTileBehaviour.logger.log(Level.INFO, "This action does not exist?", nsa2);
            }
            if (act.justTickedSecond()) {
                final boolean isBlocked = isBlocked(tilex, tiley, digTilex, digTiley, performer, action, counter, dir, h, cceil, meshTile);
                if (isBlocked) {
                    return true;
                }
            }
            if (counter * 10.0f <= time && !insta) {
                if (act.currentSecond() % 5 == 0 || (act.currentSecond() == 3 && time < 50)) {
                    String sstring = "sound.work.mining1";
                    final int x2 = Server.rand.nextInt(3);
                    if (x2 == 0) {
                        sstring = "sound.work.mining2";
                    }
                    else if (x2 == 1) {
                        sstring = "sound.work.mining3";
                    }
                    SoundPlayer.playSound(sstring, tilex, tiley, performer.isOnSurface(), 1.0f);
                    source.setDamage(source.getDamage() + 0.0015f * source.getDamageModifier());
                    performer.getStatus().modifyStamina(-7000.0f);
                }
                return false;
            }
            if (act.getRarity() != 0) {
                performer.playPersonalSound("sound.fx.drumroll");
            }
            if (dir == 0 && tileType == Tiles.Tile.TILE_CAVE_FLOOR_REINFORCED.id) {
                TileEvent.log(tilex, tiley, -1, performer.getWurmId(), action);
                comm.sendNormalServerMessage("You manage to remove the reinforcement.");
                if (actualType == Tiles.Tile.TILE_CAVE_EXIT.id) {
                    Server.setClientCaveFlags(tilex, tiley, (byte)0);
                }
                else {
                    Server.caveMesh.setTile(tilex, tiley, Tiles.encode(Tiles.decodeHeight(meshTile), Tiles.Tile.TILE_CAVE.id, Tiles.decodeData(meshTile)));
                }
                TileRockBehaviour.sendCaveTile(tilex, tiley, 0, 0);
                return true;
            }
            final VolaTile dropTile = Zones.getTileOrNull(performer.getTilePos(), performer.isOnSurface());
            if (dropTile != null && dropTile.getNumberOfItems(performer.getFloorLevel()) > 99) {
                comm.sendNormalServerMessage("There is no space to mine here. Clear the area first.");
                return true;
            }
            if (cceil >= 254) {
                comm.sendNormalServerMessage("Lowering the floor further would make the cavern unstable.");
                return true;
            }
            double bonus = 0.0;
            double power = 0.0;
            final boolean done = true;
            if (dir == 1) {
                boolean createOutside = false;
                if (TileRockBehaviour.allCornersAtRockHeight(tilex, tiley)) {
                    createOutside = true;
                }
                if (!createOutside) {
                    if (isAtRockLevelAndNotRock(digTilex, digTiley, h + cceil)) {
                        comm.sendNormalServerMessage("The roof sounds strangely hollow and you notice dirt flowing in.");
                        return true;
                    }
                    if (Tiles.decodeHeight(digCorner) + cceil + 2 >= Tiles.decodeHeight(Server.surfaceMesh.getTile(digTilePos))) {
                        comm.sendNormalServerMessage("The roof sounds dangerously weak and you must abandon this attempt.");
                        return true;
                    }
                    Server.caveMesh.setTile(digTilex, digTiley, Tiles.encode(Tiles.decodeHeight(digCorner), Tiles.decodeType(digCorner), (byte)(cceil + 1)));
                    Players.getInstance().sendChangedTile(digTilePos, false, true);
                    if (TileRockBehaviour.allCornersAtRockHeight(tilex, tiley)) {
                        comm.sendNormalServerMessage("The ceiling makes a hollow sound.");
                    }
                }
                else {
                    if (!Terraforming.allCornersAtRockLevel(tilex, tiley, Server.surfaceMesh)) {
                        comm.sendNormalServerMessage("The roof sounds strangely hollow and you notice dirt flowing in, so you stop mining.");
                        return true;
                    }
                    if (!TileRockBehaviour.createOutInTunnel(tilex, tiley, meshTile, performer, 0)) {
                        comm.sendNormalServerMessage("You decide to stop mining in the last second. The ceiling would cave in on you!");
                        return true;
                    }
                }
            }
            else {
                final short ceilMod = 1;
                if (Tiles.decodeData(digCorner) == 0 && Tiles.decodeHeight(digCorner) == Tiles.decodeHeight(Server.surfaceMesh.getTile(digTilePos))) {
                    comm.sendNormalServerMessage("You decide to stop mining in the last second. The ceiling would cave in on you!");
                    return true;
                }
                Server.caveMesh.setTile(digTilex, digTiley, Tiles.encode((short)(Tiles.decodeHeight(digCorner) - 1), Tiles.decodeType(digCorner), (byte)(cceil + 1)));
                Players.getInstance().sendChangedTile(digTilePos, false, true);
                for (int x3 = -1; x3 <= 0; ++x3) {
                    for (int y2 = -1; y2 <= 0; ++y2) {
                        try {
                            final Zone toCheckForChange = Zones.getZone(digTilex + x3, digTiley + y2, false);
                            toCheckForChange.changeTile(digTilex + x3, digTiley + y2);
                        }
                        catch (NoSuchZoneException nsz) {
                            CaveTileBehaviour.logger.log(Level.INFO, "no such zone?: " + (digTilex + x3) + "," + (digTiley + y2), nsz);
                            comm.sendNormalServerMessage("You can't mine there.");
                            return true;
                        }
                    }
                }
            }
            final int itemTemplateCreated = TileRockBehaviour.getItemTemplateForTile(Tiles.decodeType(meshTile));
            final float diff = TileRockBehaviour.getDifficultyForTile(Tiles.decodeType(meshTile));
            if (tool != null) {
                bonus = tool.skillCheck(diff, source, 0.0, false, counter) / 5.0;
            }
            power = Math.max(1.0, mining.skillCheck(diff, source, bonus, false, counter));
            try {
                final double imbueEnhancement = 1.0 + 0.23047 * source.getSkillSpellImprovement(1008) / 100.0;
                if (mining.getKnowledge(0.0) * imbueEnhancement < power) {
                    power = mining.getKnowledge(0.0) * imbueEnhancement;
                }
                CaveTileBehaviour.r.setSeed((tilex + tiley * Zones.worldTileSizeY) * 789221L);
                int m = TileRockBehaviour.MAX_QL;
                if (itemTemplateCreated == 146 || itemTemplateCreated == 38) {
                    m = 100;
                }
                if (itemTemplateCreated == 39) {
                    performer.achievement(372);
                }
                float modifier = 1.0f;
                if (source.getSpellEffects() != null) {
                    modifier = source.getSpellEffects().getRuneEffect(RuneUtilities.ModifierEffect.ENCH_RESGATHERED);
                }
                final int max = Math.min(m, (int)(20.0 + CaveTileBehaviour.r.nextInt(80) * imbueEnhancement));
                power = Math.min(power, max);
                if (source.isCrude()) {
                    power = 1.0;
                }
                final float orePower = GeneralUtilities.calcOreRareQuality(power * modifier, act.getRarity(), source.getRarity());
                final Item newItem = ItemFactory.createItem(itemTemplateCreated, orePower, act.getRarity(), null);
                newItem.setLastOwnerId(performer.getWurmId());
                newItem.setDataXY(tilex, tiley);
                newItem.putItemInfrontof(performer, 0.0f);
                comm.sendNormalServerMessage("You mine some " + newItem.getName() + ".");
                Server.getInstance().broadCastAction(performer.getName() + " mines some " + newItem.getName() + ".", performer, 5);
                TileRockBehaviour.createGem(tilex, tiley, performer, power, false, act);
            }
            catch (Exception ex) {
                CaveTileBehaviour.logger.log(Level.WARNING, "Factory failed to produce item", ex);
            }
            return true;
        }
    }
    
    public static final boolean raiseRockLevel(final Creature performer, final Item source, final int tilex, final int tiley, final float counter, final Action act) {
        if (!GeneralUtilities.isValidTileLocation(tilex, tiley)) {
            performer.getCommunicator().sendNormalServerMessage("The ground can not be raised here.");
            return true;
        }
        int tile = 0;
        if (performer.isOnSurface()) {
            tile = Server.surfaceMesh.getTile(tilex, tiley);
        }
        else {
            tile = Server.caveMesh.getTile(tilex, tiley);
            if (!Tiles.isReinforcedFloor(Tiles.decodeType(tile)) && anyReinforcedFloors(performer)) {
                performer.getCommunicator().sendNormalServerMessage("You cannot raise the corner next to reinforced floors.");
                return true;
            }
        }
        if (counter == 1.0f || counter == 0.0f || act.justTickedSecond()) {
            if (performer.getCurrentTile().getStructure() != null) {
                performer.getCommunicator().sendNormalServerMessage("This cannot be done in buildings.");
                return true;
            }
            if (Zones.protectedTiles[tilex][tiley]) {
                performer.getCommunicator().sendNormalServerMessage("For some strange reason you can't bring yourself to change this place.");
                return true;
            }
            if (Terraforming.isAltarBlocking(performer, tilex, tiley)) {
                performer.getCommunicator().sendNormalServerMessage("You cannot build here, since this is holy ground.");
                return true;
            }
            if (performer.getLayer() < 0) {
                if (CaveTile.decodeCeilingHeight(tile) <= 20) {
                    performer.getCommunicator().sendNormalServerMessage("The ceiling is too close.");
                    return true;
                }
                if (performer.getFloorLevel() > 0) {
                    performer.getCommunicator().sendNormalServerMessage("You must be standing on the ground in order to do this!");
                    return true;
                }
                if (Zones.isTileCornerProtected(tilex, tiley)) {
                    performer.getCommunicator().sendNormalServerMessage("This tile is protected by the gods. You can not raise the corner here.");
                    return true;
                }
            }
            else {
                if (performer.getFloorLevel() != 0) {
                    performer.getCommunicator().sendNormalServerMessage("You must be standing on the ground in order to do this!");
                    return true;
                }
                for (int x = 0; x >= -1; --x) {
                    for (int y = 0; y >= -1; --y) {
                        final int tx = Zones.safeTileX(tilex + x);
                        final int ty = Zones.safeTileY(tiley + y);
                        if (Tiles.decodeType(Server.caveMesh.getTile(tx, ty)) == Tiles.Tile.TILE_CAVE_EXIT.id) {
                            performer.getCommunicator().sendNormalServerMessage("The opening is too close.");
                            return true;
                        }
                        final int ttile = Server.surfaceMesh.getTile(tx, ty);
                        if (Tiles.decodeType(ttile) != Tiles.Tile.TILE_ROCK.id) {
                            performer.getCommunicator().sendNormalServerMessage("The concrete won't stick to that.");
                            return true;
                        }
                        final VolaTile vtile = Zones.getTileOrNull(tx, ty, performer.isOnSurface());
                        if (vtile != null) {
                            if (vtile.getStructure() != null) {
                                performer.getCommunicator().sendNormalServerMessage("The structure is in the way.");
                                return true;
                            }
                            if (x == 0 && y == 0) {
                                final Fence[] fences = vtile.getFences();
                                final int length = fences.length;
                                final int n = 0;
                                if (n < length) {
                                    final Fence fence = fences[n];
                                    performer.getCommunicator().sendNormalServerMessage("The " + fence.getName() + " is in the way.");
                                    return true;
                                }
                            }
                            else if (x == -1 && y == 0) {
                                for (final Fence fence : vtile.getFences()) {
                                    if (fence.isHorizontal()) {
                                        performer.getCommunicator().sendNormalServerMessage("The " + fence.getName() + " is in the way.");
                                        return true;
                                    }
                                }
                            }
                            else if (y == -1 && x == 0) {
                                for (final Fence fence : vtile.getFences()) {
                                    if (!fence.isHorizontal()) {
                                        performer.getCommunicator().sendNormalServerMessage("The " + fence.getName() + " is in the way.");
                                        return true;
                                    }
                                }
                            }
                        }
                    }
                }
                final int slopeDown = Terraforming.getMaxSurfaceDownSlope(tilex, tiley);
                final int maxSlope = Servers.localServer.PVPSERVER ? -25 : -40;
                if (performer.getPower() > 4 && source.getTemplateId() == 176) {
                    if (slopeDown < -300) {
                        performer.getCommunicator().sendNormalServerMessage("Maximum slope would be exceeded.");
                        return true;
                    }
                }
                else if (slopeDown < maxSlope) {
                    if (performer.getPower() == 4 && source.getTemplateId() == 176) {
                        performer.getCommunicator().sendNormalServerMessage("Maximum slope would be exceeded.");
                    }
                    else {
                        performer.getCommunicator().sendNormalServerMessage("The " + source.getName() + " would only flow away.");
                    }
                    return true;
                }
            }
            if (source.getTemplateId() != 176 && source.getWeightGrams() < source.getTemplate().getWeightGrams()) {
                performer.getCommunicator().sendNormalServerMessage("The " + source.getName() + " contains too little material to be usable.");
                return true;
            }
        }
        boolean done = true;
        final short h = Tiles.decodeHeight(tile);
        if (h >= -25 || source.getTemplateId() == 176) {
            final Skills skills = performer.getSkills();
            Skill masonry = null;
            done = false;
            try {
                masonry = skills.getSkill(1013);
            }
            catch (Exception ex) {
                masonry = skills.learn(1013, 1.0f);
            }
            int time = 0;
            if (counter == 1.0f) {
                time = (int)Math.max(30.0, 100.0 - masonry.getKnowledge(source, 0.0));
                try {
                    performer.getCurrentAction().setTimeLeft(time);
                }
                catch (NoSuchActionException nsa) {
                    CaveTileBehaviour.logger.log(Level.INFO, "This action does not exist?", nsa);
                }
                if (source.getTemplateId() == 176) {
                    performer.getCommunicator().sendNormalServerMessage("You will the rock to raise up.");
                }
                else {
                    performer.getCommunicator().sendNormalServerMessage("You start to spread out the " + source.getName() + ".");
                    Server.getInstance().broadCastAction(performer.getName() + " starts spreading the " + source.getName() + ".", performer, 5);
                }
                performer.sendActionControl(Actions.actionEntrys[518].getVerbString(), true, time);
            }
            else {
                try {
                    time = performer.getCurrentAction().getTimeLeft();
                }
                catch (NoSuchActionException nsa) {
                    CaveTileBehaviour.logger.log(Level.INFO, "This action does not exist?", nsa);
                }
            }
            if (counter * 10.0f > time || source.getTemplateId() == 176) {
                if (source.getTemplateId() != 176) {
                    performer.getStatus().modifyStamina(-3000.0f);
                    source.setWeight(source.getWeightGrams() - source.getTemplate().getWeightGrams(), true);
                    masonry.skillCheck(1.0, source, 0.0, false, counter);
                    source.setDamage(source.getDamage() + 5.0E-4f * source.getDamageModifier());
                }
                done = true;
                if (performer.getLayer() < 0) {
                    Server.caveMesh.setTile(tilex, tiley, Tiles.encode((short)(Tiles.decodeHeight(tile) + 1), Tiles.decodeType(tile), (byte)(CaveTile.decodeCeilingHeight(tile) - 1)));
                    tile = Server.caveMesh.getTile(tilex, tiley);
                }
                else {
                    tile = Server.rockMesh.getTile(tilex, tiley);
                    Server.rockMesh.setTile(tilex, tiley, Tiles.encode((short)(Tiles.decodeHeight(tile) + 1), Tiles.decodeType(tile), Tiles.decodeData(tile)));
                    tile = Server.surfaceMesh.getTile(tilex, tiley);
                    Server.surfaceMesh.setTile(tilex, tiley, Tiles.encode((short)(Tiles.decodeHeight(tile) + 1), Tiles.decodeType(tile), Tiles.decodeData(tile)));
                }
                if (source.getTemplateId() != 176 && source.getWeightGrams() < source.getTemplate().getWeightGrams()) {
                    performer.getCommunicator().sendNormalServerMessage("The " + source.getName() + " contains too little material to be usable.");
                    return true;
                }
                Players.getInstance().sendChangedTile(tilex, tiley, performer.getLayer() >= 0, false);
                performer.getCommunicator().sendNormalServerMessage("You raise the ground a bit.");
                if (source.getTemplateId() != 176) {
                    Server.getInstance().broadCastAction(performer.getName() + " raises the ground a bit.", performer, 5);
                }
            }
        }
        else {
            performer.getCommunicator().sendNormalServerMessage("The water is too deep and would only dissolve the " + source.getName() + ".");
        }
        return done;
    }
    
    private static boolean isAtRockLevelAndNotRock(final int tilex, final int tiley, final int height) {
        final int rtile = Server.rockMesh.getTile(tilex, tiley);
        final int rheight = Tiles.decodeHeight(rtile);
        if (rheight <= height) {
            final int stile = Server.surfaceMesh.getTile(tilex, tiley);
            if (Tiles.decodeType(stile) != Tiles.Tile.TILE_ROCK.id && Tiles.decodeType(stile) != Tiles.Tile.TILE_CLIFF.id) {
                return true;
            }
        }
        return false;
    }
    
    private boolean flatten(final Creature performer, final Item source, final int tile, final int tilex, final int tiley, final float counter, final Action act, final int dir) {
        boolean done = false;
        final String action = act.getActionEntry().getActionString().toLowerCase();
        final short[][] cornerHeights = new short[2][2];
        if (!GeneralUtilities.isValidTileLocation(tilex, tiley)) {
            performer.getCommunicator().sendNormalServerMessage("The water is too deep to " + action + ".");
            return true;
        }
        final byte type = Tiles.decodeType(tile);
        if (type != Tiles.Tile.TILE_CAVE.id && ((!Tiles.isReinforcedFloor(type) && !Tiles.isRoadType(type)) || dir == 0)) {
            performer.getCommunicator().sendNormalServerMessage("You can not " + action + " this type of tile.");
            return true;
        }
        if (performer.getFloorLevel() != 0 && dir == 0) {
            performer.getCommunicator().sendNormalServerMessage("You need to be stood on the ground to be able to do this.");
            return true;
        }
        if (act.getNumber() == 532 && dir == 0 && !Terraforming.isFlat(performer.getTileX(), performer.getTileY(), performer.isOnSurface(), 0)) {
            performer.getCommunicator().sendNormalServerMessage("You need to be standing on flat ground to be able to level.");
            return true;
        }
        if (act.getNumber() == 532 && dir == 1 && !this.isCeilingFlat(performer.getTileX(), performer.getTileY())) {
            performer.getCommunicator().sendNormalServerMessage("You need to be standing under a flat ceiling to be able to level an adjacent tile.");
            return true;
        }
        if (act.getNumber() == 150 && tilex != performer.getTileX() && tiley != performer.getTileY()) {
            if (dir == 0) {
                performer.getCommunicator().sendNormalServerMessage("You need to be standing on the tile you are flattening.");
            }
            else {
                performer.getCommunicator().sendNormalServerMessage("You need to be standing under the tile you are flattening.");
            }
            return true;
        }
        if (dir == 0 && (Tiles.isReinforcedFloor(Tiles.decodeType(Server.caveMesh.getTile(tilex - 1, tiley - 1))) || Tiles.isReinforcedFloor(Tiles.decodeType(Server.caveMesh.getTile(tilex - 1, tiley))) || Tiles.isReinforcedFloor(Tiles.decodeType(Server.caveMesh.getTile(tilex - 1, tiley + 1))) || Tiles.isReinforcedFloor(Tiles.decodeType(Server.caveMesh.getTile(tilex, tiley - 1))) || Tiles.isReinforcedFloor(Tiles.decodeType(Server.caveMesh.getTile(tilex, tiley + 1))) || Tiles.isReinforcedFloor(Tiles.decodeType(Server.caveMesh.getTile(tilex + 1, tiley - 1))) || Tiles.isReinforcedFloor(Tiles.decodeType(Server.caveMesh.getTile(tilex + 1, tiley))) || Tiles.isReinforcedFloor(Tiles.decodeType(Server.caveMesh.getTile(tilex + 1, tiley + 1))))) {
            performer.getCommunicator().sendNormalServerMessage("You can not " + action + " next to reinforced floors.");
            return true;
        }
        final boolean insta = source.getTemplateId() == 176 && performer.getPower() > 2;
        if (counter == 1.0f) {
            int belowWater = 0;
            short maxHeight = 0;
            short minHeight = 32767;
            for (int x = 0; x <= 1; ++x) {
                for (int y = 0; y <= 1; ++y) {
                    final short ht = this.getHeight(tilex + x, tiley + y, dir);
                    cornerHeights[x][y] = ht;
                    if (ht < -7.0f) {
                        ++belowWater;
                    }
                    if (ht > maxHeight) {
                        maxHeight = ht;
                    }
                    if (ht < minHeight) {
                        minHeight = ht;
                    }
                    if (Zones.isTileCornerProtected(tilex + x, tiley + y)) {
                        performer.getCommunicator().sendNormalServerMessage("This tile is protected by the gods. You can not " + action + " here.");
                        return true;
                    }
                }
            }
            if (belowWater == 4) {
                performer.getCommunicator().sendNormalServerMessage("You can't " + action + " at that depth.");
                return true;
            }
            int requiredHeight = minHeight;
            if (dir == 1) {
                requiredHeight = maxHeight;
            }
            else if (act.getNumber() == 532) {
                requiredHeight = this.getHeight(performer.getTileX(), performer.getTileY(), dir);
            }
            int totalup = 0;
            int totaldown = 0;
            for (int x2 = 0; x2 <= 1; ++x2) {
                for (int y2 = 0; y2 <= 1; ++y2) {
                    final int diff = cornerHeights[x2][y2] - requiredHeight;
                    if (diff > 0) {
                        totalup += diff;
                    }
                    else {
                        totaldown += Math.abs(diff);
                    }
                }
            }
            if (totalup + totaldown == 0) {
                performer.getCommunicator().sendNormalServerMessage("The tile is already flat.");
                return true;
            }
            float totaltime = 0.0f;
            if ((totalup > 0 && dir == 0) || (totaldown > 0 && dir == 1)) {
                final Skill mining = performer.getSkills().getSkillOrLearn(1008);
                final int tickMining = Actions.getStandardActionTime(performer, mining, source, 0.0) + 5;
                if (dir == 0) {
                    totaltime = tickMining * totalup * 1.25f;
                }
                else {
                    totaltime = tickMining * totaldown * 1.25f;
                }
                act.setNextTick(tickMining);
            }
            if (totaldown > 0 && dir == 0) {
                final Skill masonry = performer.getSkills().getSkillOrLearn(1013);
                final int tickMasonry = Actions.getStandardActionTime(performer, masonry, null, 0.0) + 5;
                totaltime += tickMasonry * totaldown * 1.125f;
                if (totalup == 0) {
                    act.setNextTick(tickMasonry);
                }
            }
            act.setTickCount(1);
            act.setTimeLeft((int)totaltime);
            act.setData(requiredHeight);
            if (!insta) {
                final int digTile = Server.caveMesh.getTile(tilex, tiley);
                final int h = Tiles.decodeHeight(digTile);
                final short cceil = (short)CaveTile.decodeCeilingHeight(digTile);
                if (isBlocked(tilex, tiley, tilex, tiley, performer, act.getNumber(), counter, dir, h, cceil, digTile)) {
                    return true;
                }
                final String gc = (dir == 0) ? "ground" : "ceiling";
                performer.getCommunicator().sendNormalServerMessage("You start to " + action + " the " + gc + ".");
                Server.getInstance().broadCastAction(performer.getName() + " starts to " + action + " the " + gc + ".", performer, 5);
                act.setTimeLeft((int)totaltime);
                performer.sendActionControl(action, true, (int)totaltime);
                source.setDamage(source.getDamage() + 0.0015f * source.getDamageModifier());
                performer.getStatus().modifyStamina(-1800.0f);
            }
        }
        if (counter * 10.0f >= act.getNextTick() || insta) {
            if (Zones.protectedTiles[tilex][tiley]) {
                performer.getCommunicator().sendNormalServerMessage("Your body goes limp and you find no strength to continue here. Weird.");
                return true;
            }
            if (performer.getStatus().getStamina() < 6000) {
                performer.getCommunicator().sendNormalServerMessage("You must rest.");
                return true;
            }
            if (!insta) {
                source.setDamage(source.getDamage() + 0.0015f * source.getDamageModifier());
                performer.getStatus().modifyStamina(-5000.0f);
            }
            final int requiredHeight2 = (int)act.getData();
            int highx = 0;
            int highy = 0;
            int highCorner = 0;
            for (int x3 = 0; x3 <= 1; ++x3) {
                for (int y3 = 0; y3 <= 1; ++y3) {
                    final int diff2 = this.getHeight(tilex + x3, tiley + y3, dir) - requiredHeight2;
                    if (dir == 0) {
                        if (diff2 > highCorner) {
                            highx = tilex + x3;
                            highy = tiley + y3;
                            highCorner = diff2;
                        }
                    }
                    else if (diff2 < highCorner) {
                        highx = tilex + x3;
                        highy = tiley + y3;
                        highCorner = diff2;
                    }
                }
            }
            if (highCorner != 0) {
                if (dir == 0 && this.anyReinforcedFloorTiles(highx, highy)) {
                    performer.getCommunicator().sendNormalServerMessage("You cannot modify one of the corners due to reinforced floors.");
                    return true;
                }
                final int highTile = Server.caveMesh.getTile(highx, highy);
                final short cceil2 = (short)(Tiles.decodeData(highTile) & 0xFF);
                if (dir == 1 && act.getNumber() == 150 && cceil2 > 60 + performer.getFloorLevel() * 30) {
                    performer.getCommunicator().sendNormalServerMessage("You cannot reach the ceiling.");
                    return true;
                }
                if (cceil2 >= 254) {
                    if (dir == 0) {
                        performer.getCommunicator().sendNormalServerMessage("Lowering the floor further would make the cavern unstable.");
                    }
                    else {
                        performer.getCommunicator().sendNormalServerMessage("Raising the ceiling further would make the cavern unstable.");
                    }
                    return true;
                }
                if (dir == 1 && Tiles.decodeHeight(highTile) + cceil2 + 2 >= Tiles.decodeHeight(Server.rockMesh.getTile(highx, highy))) {
                    performer.getCommunicator().sendNormalServerMessage("The roof sounds dangerously weak and you must abandon this attempt.");
                    return true;
                }
                final Skill mining2 = performer.getSkills().getSkillOrLearn(1008);
                Skill tool = null;
                try {
                    tool = performer.getSkills().getSkillOrLearn(source.getPrimarySkill());
                }
                catch (NoSuchSkillException nse) {
                    CaveTileBehaviour.logger.log(Level.WARNING, performer.getName() + " trying to mine with an item with no primary skill: " + source.getName());
                }
                final int tickTime = Actions.getStandardActionTime(performer, mining2, source, 0.0);
                double bonus = 0.0;
                double power = 0.0;
                final int itemTemplateCreated = TileRockBehaviour.getItemTemplateForTile(Tiles.decodeType(tile));
                final float diff3 = TileRockBehaviour.getDifficultyForTile(Tiles.decodeType(tile));
                if (!insta) {
                    String sstring = "sound.work.mining1";
                    final int rndsound = Server.rand.nextInt(3);
                    if (rndsound == 0) {
                        sstring = "sound.work.mining2";
                    }
                    else if (rndsound == 1) {
                        sstring = "sound.work.mining3";
                    }
                    SoundPlayer.playSound(sstring, tilex, tiley, performer.isOnSurface(), 1.0f);
                    final VolaTile dropTile = Zones.getTileOrNull(performer.getTileX(), performer.getTileY(), performer.isOnSurface());
                    if (dropTile != null && dropTile.getNumberOfItems(performer.getFloorLevel()) > 99) {
                        performer.getCommunicator().sendNormalServerMessage("There is no space to mine here. Clear the area first.");
                        return true;
                    }
                    if (tool != null) {
                        bonus = tool.skillCheck(diff3, source, 0.0, false, tickTime / 10) / 5.0;
                    }
                    power = Math.max(1.0, mining2.skillCheck(diff3, source, bonus, false, tickTime / 10));
                }
                if (dir == 0) {
                    Server.caveMesh.setTile(highx, highy, Tiles.encode((short)(Tiles.decodeHeight(highTile) - 1), Tiles.decodeType(highTile), (byte)(cceil2 + 1)));
                }
                else {
                    Server.caveMesh.setTile(highx, highy, Tiles.encode(Tiles.decodeHeight(highTile), Tiles.decodeType(highTile), (byte)(cceil2 + 1)));
                }
                Players.getInstance().sendChangedTile(highx, highy, false, true);
                if (dir == 0) {
                    for (int x4 = -1; x4 <= 0; ++x4) {
                        for (int y4 = -1; y4 <= 0; ++y4) {
                            try {
                                final Zone toCheckForChange = Zones.getZone(highx + x4, highy + y4, false);
                                toCheckForChange.changeTile(highx + x4, highy + y4);
                            }
                            catch (NoSuchZoneException nsz) {
                                CaveTileBehaviour.logger.log(Level.INFO, "no such zone?: " + (highx + x4) + "," + (highy + y4), nsz);
                                performer.getCommunicator().sendNormalServerMessage("You can't mine there.");
                                return true;
                            }
                        }
                    }
                }
                if (!insta) {
                    try {
                        if (act.getRarity() != 0) {
                            performer.playPersonalSound("sound.fx.drumroll");
                        }
                        if (mining2.getKnowledge(0.0) < power) {
                            power = mining2.getKnowledge(0.0);
                        }
                        CaveTileBehaviour.r.setSeed((tilex + tiley * Zones.worldTileSizeY) * 789221L);
                        int m = TileRockBehaviour.MAX_QL;
                        if (itemTemplateCreated == 146 || itemTemplateCreated == 38) {
                            m = 100;
                        }
                        if (itemTemplateCreated == 39) {
                            performer.achievement(372);
                        }
                        float modifier = 1.0f;
                        if (source.getSpellEffects() != null) {
                            modifier = source.getSpellEffects().getRuneEffect(RuneUtilities.ModifierEffect.ENCH_RESGATHERED);
                        }
                        final int max = Math.min(m, 20 + CaveTileBehaviour.r.nextInt(80));
                        power = Math.min(power, max);
                        if (source.isCrude()) {
                            power = 1.0;
                        }
                        final float orePower = GeneralUtilities.calcOreRareQuality(power * modifier, act.getRarity(), source.getRarity());
                        final Item newItem = ItemFactory.createItem(itemTemplateCreated, orePower, act.getRarity(), null);
                        newItem.setLastOwnerId(performer.getWurmId());
                        newItem.setDataXY(tilex, tiley);
                        newItem.putItemInfrontof(performer, 0.0f);
                        performer.getCommunicator().sendNormalServerMessage("You mine some " + newItem.getName() + ".");
                        Server.getInstance().broadCastAction(performer.getName() + " mines some " + newItem.getName() + ".", performer, 5);
                        TileRockBehaviour.createGem(tilex, tiley, performer, power, false, act);
                    }
                    catch (Exception ex) {
                        CaveTileBehaviour.logger.log(Level.WARNING, "Factory failed to produce item", ex);
                    }
                }
            }
            else if (dir == 0) {
                int lowx = 0;
                int lowy = 0;
                int lowCorner = 0;
                for (int x5 = 0; x5 <= 1; ++x5) {
                    for (int y5 = 0; y5 <= 1; ++y5) {
                        final int diff4 = requiredHeight2 - Tiles.decodeHeight(Server.caveMesh.getTile(tilex + x5, tiley + y5));
                        if (diff4 > lowCorner) {
                            lowx = tilex + x5;
                            lowy = tiley + y5;
                            lowCorner = diff4;
                        }
                    }
                }
                if (lowCorner <= 0) {
                    performer.getCommunicator().sendNormalServerMessage("Done.");
                    return true;
                }
                if (this.anyReinforcedFloorTiles(lowx, lowy)) {
                    performer.getCommunicator().sendNormalServerMessage("You cannot modify one of the corners due to reinforced floors.");
                    return true;
                }
                final int lowTile = Server.caveMesh.getTile(lowx, lowy);
                if (CaveTile.decodeCeilingHeight(lowTile) <= 21) {
                    performer.getCommunicator().sendNormalServerMessage("The ceiling is too close.");
                    return true;
                }
                if (!insta) {
                    final Skill masonry2 = performer.getSkills().getSkillOrLearn(1013);
                    final int tickMasonry2 = Actions.getStandardActionTime(performer, masonry2, null, 0.0);
                    try {
                        final ItemTemplate template = ItemTemplateFactory.getInstance().getTemplate(782);
                        final Item concrete = MethodsStructure.creatureHasItem(template, performer, true);
                        if (concrete == null) {
                            performer.getCommunicator().sendNormalServerMessage("One or more corners need to be filled in with concrete.");
                            return true;
                        }
                        performer.getStatus().modifyStamina(-3500.0f);
                        concrete.setWeight(concrete.getWeightGrams() - concrete.getTemplate().getWeightGrams(), true);
                        masonry2.skillCheck(1.0, source, 0.0, false, tickMasonry2 / 10);
                        concrete.setDamage(concrete.getDamage() + 5.0E-4f * concrete.getDamageModifier());
                    }
                    catch (NoSuchTemplateException e) {
                        CaveTileBehaviour.logger.log(Level.WARNING, e.getMessage(), e);
                        return true;
                    }
                }
                Server.caveMesh.setTile(lowx, lowy, Tiles.encode((short)(Tiles.decodeHeight(lowTile) + 1), Tiles.decodeType(lowTile), (byte)(CaveTile.decodeCeilingHeight(lowTile) - 1)));
                Players.getInstance().sendChangedTile(lowx, lowy, false, false);
                performer.getCommunicator().sendNormalServerMessage("You raise the ground a bit.");
                Server.getInstance().broadCastAction(performer.getName() + " raises the ground a bit.", performer, 5);
            }
            done = true;
            boolean above = false;
            for (int x6 = 0; x6 <= 1; ++x6) {
                for (int y6 = 0; y6 <= 1; ++y6) {
                    final short ht2 = this.getHeight(tilex + x6, tiley + y6, dir);
                    if (dir == 0) {
                        if (ht2 > requiredHeight2) {
                            above = true;
                            done = false;
                        }
                        if (ht2 != requiredHeight2) {
                            done = false;
                        }
                    }
                    else if (ht2 < requiredHeight2) {
                        above = true;
                        done = false;
                    }
                }
            }
            if (!done) {
                int tickNextTime = 0;
                if (above) {
                    final Skill mining2 = performer.getSkills().getSkillOrLearn(1008);
                    tickNextTime = Actions.getStandardActionTime(performer, mining2, source, 0.0);
                }
                else {
                    final Skill masonry3 = performer.getSkills().getSkillOrLearn(1013);
                    tickNextTime = Actions.getStandardActionTime(performer, masonry3, null, 0.0);
                }
                act.incTickCount();
                act.incNextTick(tickNextTime + 5);
                act.setRarity(performer.getRarity());
            }
            else if (dir == 1) {
                if (this.isCeilingFlat(tilex, tiley)) {
                    performer.getCommunicator().sendNormalServerMessage("The ceiling is now flat.");
                }
                else {
                    performer.getCommunicator().sendNormalServerMessage("You do not seem to be able to make the ceiling flat.");
                }
            }
            else if (Terraforming.isFlat(tilex, tiley, false, 0)) {
                performer.getCommunicator().sendNormalServerMessage("The floor is now flat.");
            }
            else {
                performer.getCommunicator().sendNormalServerMessage("You do not seem to be able to make the floor flat.");
            }
        }
        return done;
    }
    
    private boolean anyReinforcedFloorTiles(final int tilex, final int tiley) {
        for (int x = tilex - 1; x <= tilex; ++x) {
            for (int y = tiley - 1; y <= tiley; ++y) {
                final int digTile = Server.caveMesh.getTile(x, y);
                if (Tiles.decodeType(digTile) == Tiles.Tile.TILE_CAVE_FLOOR_REINFORCED.id) {
                    return true;
                }
            }
        }
        return false;
    }
    
    private static boolean anyReinforcedFloors(final Creature performer) {
        final int digTilex = (int)(performer.getStatus().getPositionX() + 2.0f) >> 2;
        final int digTiley = (int)(performer.getStatus().getPositionY() + 2.0f) >> 2;
        final int digTile = Server.caveMesh.getTile(digTilex, digTiley);
        final byte digType = Tiles.decodeType(digTile);
        return Tiles.isReinforcedFloor(digType) || Tiles.isRoadType(digType) || anyAdjacentReinforcedFloors(digTilex, digTiley, false);
    }
    
    private static boolean anyAdjacentReinforcedFloors(final int digTilex, final int digTiley, final boolean all) {
        int digTile = Server.caveMesh.getTile(digTilex - 1, digTiley - 1);
        byte digType = Tiles.decodeType(digTile);
        if (Tiles.isReinforcedFloor(digType) || Tiles.isRoadType(digType)) {
            return true;
        }
        digTile = Server.caveMesh.getTile(digTilex - 1, digTiley);
        digType = Tiles.decodeType(digTile);
        if (Tiles.isReinforcedFloor(digType) || Tiles.isRoadType(digType)) {
            return true;
        }
        digTile = Server.caveMesh.getTile(digTilex, digTiley - 1);
        digType = Tiles.decodeType(digTile);
        if (Tiles.isReinforcedFloor(digType) || Tiles.isRoadType(digType)) {
            return true;
        }
        if (all) {
            digTile = Server.caveMesh.getTile(digTilex + 1, digTiley - 1);
            digType = Tiles.decodeType(digTile);
            if (Tiles.isReinforcedFloor(digType) || Tiles.isRoadType(digType)) {
                return true;
            }
            digTile = Server.caveMesh.getTile(digTilex + 1, digTiley);
            digType = Tiles.decodeType(digTile);
            if (Tiles.isReinforcedFloor(digType) || Tiles.isRoadType(digType)) {
                return true;
            }
            digTile = Server.caveMesh.getTile(digTilex + 1, digTiley + 1);
            digType = Tiles.decodeType(digTile);
            if (Tiles.isReinforcedFloor(digType) || Tiles.isRoadType(digType)) {
                return true;
            }
            digTile = Server.caveMesh.getTile(digTilex, digTiley + 1);
            digType = Tiles.decodeType(digTile);
            if (Tiles.isReinforcedFloor(digType) || Tiles.isRoadType(digType)) {
                return true;
            }
            digTile = Server.caveMesh.getTile(digTilex - 1, digTiley + 1);
            digType = Tiles.decodeType(digTile);
            if (Tiles.isReinforcedFloor(digType) || Tiles.isRoadType(digType)) {
                return true;
            }
        }
        return false;
    }
    
    private boolean isCeilingFlat(final int tilex, final int tiley) {
        final short cht = this.getRealCeilingHeight(tilex, tiley);
        return cht == this.getRealCeilingHeight(tilex + 1, tiley) && cht == this.getRealCeilingHeight(tilex + 1, tiley + 1) && cht == this.getRealCeilingHeight(tilex, tiley + 1);
    }
    
    private short getRealCeilingHeight(final int tilex, final int tiley) {
        final int meshTile = Server.caveMesh.getTile(tilex, tiley);
        final int ht = Tiles.decodeHeight(meshTile);
        final int cceil = CaveTile.decodeCeilingHeight(meshTile);
        return (short)(ht + cceil);
    }
    
    private short getHeight(final int tilex, final int tiley, final int dir) {
        if (dir == 0) {
            return Tiles.decodeHeight(Server.caveMesh.getTile(tilex, tiley));
        }
        return this.getRealCeilingHeight(tilex, tiley);
    }
    
    private boolean changeFloor(final Action act, final Creature performer, final Item source, final int tilex, final int tiley, final int tile, final short action, final float counter) {
        final int pavingItem = source.getTemplateId();
        if (!Methods.isActionAllowed(performer, (short)155, tilex, tiley)) {
            return true;
        }
        final byte type = Tiles.decodeType(tile);
        final byte fType = Server.getClientCaveFlags(tilex, tiley);
        boolean repaving = false;
        if (Tiles.isRoadType(type) || (type == Tiles.Tile.TILE_CAVE_EXIT.id && Tiles.isRoadType(fType))) {
            repaving = true;
            if (performer.getStrengthSkill() < 20.0) {
                performer.getCommunicator().sendNormalServerMessage("You need to be stronger to replace pavement.");
                return true;
            }
            final Village village = Villages.getVillageWithPerimeterAt(tilex, tiley, true);
            if (village != null && !village.isActionAllowed(act.getNumber(), performer)) {
                performer.getCommunicator().sendNormalServerMessage("You do not have permissions to do that.");
                return true;
            }
        }
        if (Tiles.decodeHeight(tile) < -100) {
            performer.getCommunicator().sendNormalServerMessage("The water is too deep to pave here.");
            return true;
        }
        final String floorexit = (Tiles.decodeType(tile) == Tiles.Tile.TILE_CAVE_EXIT.id) ? "exit" : "floor";
        if (pavingItem == 492) {
            if (source.getWeightGrams() < 2000) {
                performer.getCommunicator().sendNormalServerMessage("It takes 2kg of " + source.getName() + " to prepare the " + floorexit + ".");
                return true;
            }
        }
        else if (pavingItem != 97) {
            if (source.getWeightGrams() < source.getTemplate().getWeightGrams()) {
                performer.getCommunicator().sendNormalServerMessage("The amount of " + source.getName() + " is too little to pave. You may need to combine them with other " + source.getTemplate().getPlural() + ".");
                return true;
            }
        }
        if (counter == 1.0f) {
            final Skill paving = performer.getSkills().getSkillOrLearn(10031);
            final int time = Actions.getStandardActionTime(performer, paving, source, 0.0);
            act.setTimeLeft(time);
            final String prepared = (pavingItem == 492) ? ("prepare the reinforced " + floorexit) : ((pavingItem == 97) ? "remove the mortar" : ((repaving ? "repave the " : "pave the prepared ") + floorexit));
            if (pavingItem == 519) {
                performer.getCommunicator().sendNormalServerMessage("You break up the collosus brick and start to " + prepared + ".");
            }
            else {
                performer.getCommunicator().sendNormalServerMessage("You start to " + prepared + " with the " + source.getName() + ".");
            }
            Server.getInstance().broadCastAction(performer.getName() + " starts to " + prepared + ".", performer, 5);
            performer.sendActionControl(act.getActionEntry().getVerbString(), true, time);
            performer.getStatus().modifyStamina(-1000.0f);
            return false;
        }
        final int time2 = act.getTimeLeft();
        if (act.currentSecond() % 5 == 0) {
            performer.getStatus().modifyStamina(-10000.0f);
        }
        if (act.mayPlaySound()) {
            Methods.sendSound(performer, "sound.work.paving");
        }
        if (counter * 10.0f <= time2) {
            return false;
        }
        final Skill paving2 = performer.getSkills().getSkillOrLearn(10031);
        paving2.skillCheck((pavingItem == 146) ? 5.0 : 30.0, source, 0.0, false, counter);
        TileEvent.log(tilex, tiley, -1, performer.getWurmId(), action);
        byte newTileType = 0;
        switch (pavingItem) {
            case 492: {
                newTileType = Tiles.Tile.TILE_CAVE_PREPATED_FLOOR_REINFORCED.id;
                break;
            }
            case 97: {
                newTileType = Tiles.Tile.TILE_CAVE_FLOOR_REINFORCED.id;
                break;
            }
            case 132: {
                newTileType = Tiles.Tile.TILE_COBBLESTONE.id;
                break;
            }
            case 1122: {
                newTileType = Tiles.Tile.TILE_COBBLESTONE_ROUND.id;
                break;
            }
            case 519: {
                newTileType = Tiles.Tile.TILE_COBBLESTONE_ROUGH.id;
                break;
            }
            case 406: {
                newTileType = Tiles.Tile.TILE_STONE_SLABS.id;
                break;
            }
            case 1123: {
                newTileType = Tiles.Tile.TILE_SLATE_BRICKS.id;
                break;
            }
            case 771: {
                newTileType = Tiles.Tile.TILE_SLATE_SLABS.id;
                break;
            }
            case 1121: {
                newTileType = Tiles.Tile.TILE_SANDSTONE_BRICKS.id;
                break;
            }
            case 1124: {
                newTileType = Tiles.Tile.TILE_SANDSTONE_SLABS.id;
                break;
            }
            case 787: {
                newTileType = Tiles.Tile.TILE_MARBLE_SLABS.id;
                break;
            }
            case 786: {
                newTileType = Tiles.Tile.TILE_MARBLE_BRICKS.id;
                break;
            }
            case 776: {
                newTileType = Tiles.Tile.TILE_POTTERY_BRICKS.id;
                break;
            }
            case 495: {
                newTileType = Tiles.Tile.TILE_PLANKS.id;
                break;
            }
            default: {
                newTileType = Tiles.Tile.TILE_GRAVEL.id;
                break;
            }
        }
        if (type == Tiles.Tile.TILE_CAVE_EXIT.id) {
            Server.setClientCaveFlags(tilex, tiley, newTileType);
        }
        else {
            Server.caveMesh.setTile(tilex, tiley, Tiles.encode(Tiles.decodeHeight(tile), newTileType, Tiles.decodeData(tile)));
        }
        TileRockBehaviour.sendCaveTile(tilex, tiley, 0, 0);
        if (pavingItem == 492) {
            source.setWeight(source.getWeightGrams() - 2000, true);
        }
        else if (pavingItem != 97) {
            Items.destroyItem(source.getWurmId());
        }
        final String prepared2 = (pavingItem == 492) ? "prepared" : ((pavingItem == 97) ? "back to plain reinforcement" : "paved");
        performer.getCommunicator().sendNormalServerMessage("The cave " + floorexit + " is " + prepared2 + " now.");
        return true;
    }
    
    static {
        logger = Logger.getLogger(CaveTileBehaviour.class.getName());
    }
}
