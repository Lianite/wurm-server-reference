// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.behaviours;

import com.wurmonline.server.structures.BridgePart;
import com.wurmonline.server.highways.HighwayPos;
import com.wurmonline.server.structures.Floor;
import com.wurmonline.server.highways.MethodsHighways;
import com.wurmonline.server.zones.Zones;
import com.wurmonline.server.structures.Structure;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import com.wurmonline.shared.constants.StructureTypeEnum;
import com.wurmonline.mesh.CaveTile;
import com.wurmonline.server.Server;
import com.wurmonline.server.items.Item;
import java.util.List;
import com.wurmonline.mesh.Tiles;
import com.wurmonline.server.creatures.Creature;
import java.util.logging.Logger;

public final class StructureBehaviour extends Behaviour
{
    private static final Logger logger;
    
    StructureBehaviour() {
        super((short)6);
    }
    
    @Override
    public List<ActionEntry> getBehavioursFor(final Creature performer, final int tilex, final int tiley, final boolean onSurface, final Tiles.TileBorderDirection dir, final boolean border, final int heightOffset) {
        final List<ActionEntry> toReturn = super.getBehavioursFor(performer, tilex, tiley, onSurface, dir, border, heightOffset);
        toReturn.add(Actions.actionEntrys[607]);
        return toReturn;
    }
    
    @Override
    public List<ActionEntry> getBehavioursFor(final Creature performer, final Item target, final int tilex, final int tiley, final boolean onSurface, final Tiles.TileBorderDirection dir, final boolean border, final int heightOffset) {
        final List<ActionEntry> toReturn = super.getBehavioursFor(performer, target, tilex, tiley, onSurface, dir, border, heightOffset);
        toReturn.add(Actions.actionEntrys[607]);
        final boolean hasMarker = hasMarker(tilex, tiley, onSurface, dir, heightOffset);
        if (!MethodsStructure.isCorrectToolForBuilding(performer, target.getTemplateId())) {
            return toReturn;
        }
        final Structure structure = MethodsStructure.getStructureOrNullAtTileBorder(tilex, tiley, dir, onSurface);
        if (structure != null && structure.isActionAllowed(performer, (short)116)) {
            if (!onSurface) {
                final int minHeight = (performer.getFloorLevel() + 1) * 30;
                final int nwCorner = Server.caveMesh.getTile(tilex, tiley);
                final short nwCeil = (short)CaveTile.decodeCeilingHeight(nwCorner);
                if (nwCeil < minHeight) {
                    return toReturn;
                }
                if (dir == Tiles.TileBorderDirection.DIR_HORIZ) {
                    final int neCorner = Server.caveMesh.getTile(tilex + 1, tiley);
                    final short neCeil = (short)CaveTile.decodeCeilingHeight(neCorner);
                    if (neCeil < minHeight) {
                        return toReturn;
                    }
                }
                else {
                    final int swCorner = Server.caveMesh.getTile(tilex, tiley + 1);
                    final short swCeil = (short)CaveTile.decodeCeilingHeight(swCorner);
                    if (swCeil < minHeight) {
                        return toReturn;
                    }
                }
            }
            boolean hasArch = false;
            if (!MethodsStructure.doesTileBorderContainWallOrFence(tilex, tiley, heightOffset, dir, onSurface, false)) {
                toReturn.add(new ActionEntry((short)(-1), "Plan", "planning"));
                toReturn.add(new ActionEntry((short)(20000 + StructureTypeEnum.SOLID.ordinal()), "Wall", "planning wall", StructureBehaviour.emptyIntArr));
            }
            else {
                hasArch = true;
            }
            if (!hasMarker) {
                toReturn.add(new ActionEntry((short)(-11), "Fence", "Fence options"));
                final List<ActionEntry> iron = new LinkedList<ActionEntry>();
                iron.add(Actions.actionEntrys[611]);
                iron.add(Actions.actionEntrys[477]);
                iron.add(Actions.actionEntrys[479]);
                iron.add(Actions.actionEntrys[521]);
                iron.add(Actions.actionEntrys[545]);
                if (!hasArch) {
                    iron.add(Actions.actionEntrys[546]);
                }
                toReturn.add(new ActionEntry((short)(-iron.size()), "Iron", "Fence options"));
                Collections.sort(iron);
                toReturn.addAll(iron);
                final List<ActionEntry> marble = new LinkedList<ActionEntry>();
                marble.add(Actions.actionEntrys[844]);
                marble.add(Actions.actionEntrys[845]);
                marble.add(Actions.actionEntrys[846]);
                marble.add(Actions.actionEntrys[904]);
                marble.add(Actions.actionEntrys[905]);
                marble.add(Actions.actionEntrys[902]);
                if (!hasArch) {
                    marble.add(Actions.actionEntrys[900]);
                    marble.add(Actions.actionEntrys[901]);
                    marble.add(Actions.actionEntrys[903]);
                }
                toReturn.add(new ActionEntry((short)(-marble.size()), "Marble", "Fence options"));
                Collections.sort(marble);
                toReturn.addAll(marble);
                final List<ActionEntry> plank = new LinkedList<ActionEntry>();
                plank.add(Actions.actionEntrys[520]);
                plank.add(Actions.actionEntrys[528]);
                plank.add(Actions.actionEntrys[166]);
                plank.add(Actions.actionEntrys[168]);
                if (!hasArch) {
                    plank.add(Actions.actionEntrys[516]);
                }
                toReturn.add(new ActionEntry((short)(-plank.size()), "Plank", "Fence options"));
                Collections.sort(plank);
                toReturn.addAll(plank);
                final List<ActionEntry> pottery = new LinkedList<ActionEntry>();
                pottery.add(Actions.actionEntrys[838]);
                pottery.add(Actions.actionEntrys[839]);
                pottery.add(Actions.actionEntrys[840]);
                pottery.add(Actions.actionEntrys[898]);
                pottery.add(Actions.actionEntrys[899]);
                pottery.add(Actions.actionEntrys[896]);
                if (!hasArch) {
                    pottery.add(Actions.actionEntrys[894]);
                    pottery.add(Actions.actionEntrys[895]);
                    pottery.add(Actions.actionEntrys[897]);
                }
                toReturn.add(new ActionEntry((short)(-pottery.size()), "Pottery", "Fence options"));
                Collections.sort(pottery);
                toReturn.addAll(pottery);
                final List<ActionEntry> rope = new LinkedList<ActionEntry>();
                rope.add(Actions.actionEntrys[544]);
                rope.add(Actions.actionEntrys[543]);
                toReturn.add(new ActionEntry((short)(-rope.size()), "Rope", "Rope options"));
                Collections.sort(rope);
                toReturn.addAll(rope);
                final List<ActionEntry> round = new LinkedList<ActionEntry>();
                round.add(Actions.actionEntrys[835]);
                round.add(Actions.actionEntrys[836]);
                round.add(Actions.actionEntrys[837]);
                round.add(Actions.actionEntrys[880]);
                round.add(Actions.actionEntrys[881]);
                round.add(Actions.actionEntrys[878]);
                if (!hasArch) {
                    round.add(Actions.actionEntrys[876]);
                    round.add(Actions.actionEntrys[877]);
                    round.add(Actions.actionEntrys[879]);
                }
                toReturn.add(new ActionEntry((short)(-round.size()), "Rounded stone", "Fence options"));
                Collections.sort(round);
                toReturn.addAll(round);
                final List<ActionEntry> sandstone = new LinkedList<ActionEntry>();
                sandstone.add(Actions.actionEntrys[841]);
                sandstone.add(Actions.actionEntrys[842]);
                sandstone.add(Actions.actionEntrys[843]);
                sandstone.add(Actions.actionEntrys[886]);
                sandstone.add(Actions.actionEntrys[887]);
                sandstone.add(Actions.actionEntrys[884]);
                if (!hasArch) {
                    sandstone.add(Actions.actionEntrys[882]);
                    sandstone.add(Actions.actionEntrys[883]);
                    sandstone.add(Actions.actionEntrys[885]);
                }
                toReturn.add(new ActionEntry((short)(-sandstone.size()), "Sandstone", "Fence options"));
                Collections.sort(sandstone);
                toReturn.addAll(sandstone);
                final List<ActionEntry> shaft = new LinkedList<ActionEntry>();
                shaft.add(Actions.actionEntrys[527]);
                shaft.add(Actions.actionEntrys[526]);
                shaft.add(Actions.actionEntrys[529]);
                toReturn.add(new ActionEntry((short)(-shaft.size()), "Shaft", "Fence options"));
                Collections.sort(shaft);
                toReturn.addAll(shaft);
                final List<ActionEntry> slate = new LinkedList<ActionEntry>();
                slate.add(Actions.actionEntrys[832]);
                slate.add(Actions.actionEntrys[833]);
                slate.add(Actions.actionEntrys[834]);
                slate.add(Actions.actionEntrys[874]);
                slate.add(Actions.actionEntrys[875]);
                slate.add(Actions.actionEntrys[872]);
                if (!hasArch) {
                    slate.add(Actions.actionEntrys[870]);
                    slate.add(Actions.actionEntrys[871]);
                    slate.add(Actions.actionEntrys[873]);
                }
                toReturn.add(new ActionEntry((short)(-slate.size()), "Slate", "Fence options"));
                Collections.sort(slate);
                toReturn.addAll(slate);
                final List<ActionEntry> stone = new LinkedList<ActionEntry>();
                stone.add(Actions.actionEntrys[542]);
                stone.add(Actions.actionEntrys[541]);
                stone.add(Actions.actionEntrys[517]);
                if (!hasArch) {
                    stone.add(Actions.actionEntrys[654]);
                    stone.add(Actions.actionEntrys[164]);
                }
                toReturn.add(new ActionEntry((short)(-stone.size()), "Stone", "Fence options"));
                Collections.sort(stone);
                toReturn.addAll(stone);
                final List<ActionEntry> woven = new LinkedList<ActionEntry>();
                woven.add(Actions.actionEntrys[478]);
                toReturn.add(new ActionEntry((short)(-woven.size()), "Woven", "Fence options"));
                Collections.sort(woven);
                toReturn.addAll(woven);
            }
        }
        return toReturn;
    }
    
    static boolean canBuildFenceOnFloor(final short action) {
        switch (action) {
            case 164:
            case 166:
            case 168:
            case 477:
            case 478:
            case 479:
            case 516:
            case 517:
            case 520:
            case 521:
            case 526:
            case 527:
            case 528:
            case 529:
            case 541:
            case 542:
            case 543:
            case 544:
            case 545:
            case 546:
            case 611:
            case 654:
            case 832:
            case 833:
            case 834:
            case 835:
            case 836:
            case 837:
            case 838:
            case 839:
            case 840:
            case 841:
            case 842:
            case 843:
            case 844:
            case 845:
            case 846:
            case 870:
            case 871:
            case 872:
            case 873:
            case 874:
            case 875:
            case 876:
            case 877:
            case 878:
            case 879:
            case 880:
            case 881:
            case 882:
            case 883:
            case 884:
            case 885:
            case 886:
            case 887:
            case 888:
            case 889:
            case 890:
            case 891:
            case 892:
            case 893:
            case 894:
            case 895:
            case 896:
            case 897:
            case 898:
            case 899:
            case 900:
            case 901:
            case 902:
            case 903:
            case 904:
            case 905: {
                return true;
            }
            default: {
                return false;
            }
        }
    }
    
    @Override
    public boolean action(final Action act, final Creature performer, final Item source, final int tilex, final int tiley, final boolean onSurface, final int heightOffset, final Tiles.TileBorderDirection dir, final long borderId, final short action, final float counter) {
        final boolean hasMarker = hasMarker(tilex, tiley, onSurface, dir, heightOffset);
        final Structure structure = MethodsStructure.getStructureOrNullAtTileBorder(tilex, tiley, dir, onSurface);
        if (action == 20000 + StructureTypeEnum.SOLID.ordinal() && structure != null && structure.isActionAllowed(performer, (short)116)) {
            return MethodsStructure.planWallAt(act, performer, source, tilex, tiley, onSurface, heightOffset, dir, action, counter);
        }
        if (!hasMarker && canBuildFenceOnFloor(action) && structure != null && structure.isActionAllowed(performer, (short)116)) {
            return MethodsStructure.buildFence(act, performer, source, tilex, tiley, onSurface, heightOffset, dir, borderId, action, counter);
        }
        if (action == 607) {
            performer.getCommunicator().sendAddTileBorderToCreationWindow(borderId);
            return true;
        }
        if (action == 1) {
            return this.action(act, performer, tilex, tiley, onSurface, dir, borderId, action, counter);
        }
        if (hasMarker) {
            performer.getCommunicator().sendNormalServerMessage("You cannot do that on a highway.");
        }
        return true;
    }
    
    @Override
    public boolean action(final Action act, final Creature performer, final int tilex, final int tiley, final boolean onSurface, final Tiles.TileBorderDirection dir, final long borderId, final short action, final float counter) {
        if (action == 1) {
            performer.getCommunicator().sendNormalServerMessage("This outlines where walls may be built.");
        }
        else if (action == 607) {
            performer.getCommunicator().sendAddTileBorderToCreationWindow(borderId);
        }
        return true;
    }
    
    static boolean hasMarker(final int tilex, final int tiley, final boolean onSurface, final Tiles.TileBorderDirection dir, final int heightOffset) {
        final Floor[] floors = Zones.getFloorsAtTile(tilex, tiley, heightOffset, heightOffset, onSurface);
        HighwayPos highwaypos;
        if (floors != null && floors.length == 1) {
            highwaypos = MethodsHighways.getHighwayPos(floors[0]);
        }
        else if (heightOffset > 0) {
            final BridgePart bridgePart = Zones.getBridgePartFor(tilex, tiley, onSurface);
            if (bridgePart == null) {
                return false;
            }
            highwaypos = MethodsHighways.getHighwayPos(bridgePart);
        }
        else {
            highwaypos = MethodsHighways.getHighwayPos(tilex, tiley, onSurface);
        }
        return MethodsHighways.containsMarker(highwaypos, (byte)(-1)) || (dir == Tiles.TileBorderDirection.DIR_HORIZ && MethodsHighways.containsMarker(highwaypos, (byte)4)) || (dir == Tiles.TileBorderDirection.DIR_DOWN && MethodsHighways.containsMarker(highwaypos, (byte)16));
    }
    
    static {
        logger = Logger.getLogger(WallBehaviour.class.getName());
    }
}
