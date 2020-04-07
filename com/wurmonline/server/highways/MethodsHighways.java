// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.highways;

import com.wurmonline.server.MeshTile;
import com.wurmonline.server.skills.Skill;
import com.wurmonline.server.villages.Village;
import com.wurmonline.server.villages.Villages;
import com.wurmonline.math.Vector2f;
import com.wurmonline.server.utils.CoordUtils;
import java.util.HashSet;
import com.wurmonline.server.Servers;
import com.wurmonline.server.creatures.Creature;
import com.wurmonline.server.Items;
import com.wurmonline.shared.constants.StructureConstants;
import com.wurmonline.shared.constants.BridgeConstants;
import com.wurmonline.mesh.MeshIO;
import com.wurmonline.server.zones.Zones;
import com.wurmonline.mesh.Tiles;
import com.wurmonline.server.Server;
import javax.annotation.Nullable;
import com.wurmonline.server.Features;
import com.wurmonline.server.items.Item;
import com.wurmonline.server.structures.Floor;
import com.wurmonline.server.structures.BridgePart;
import com.wurmonline.shared.constants.HighwayConstants;
import com.wurmonline.server.MiscConstants;

public class MethodsHighways implements MiscConstants, HighwayConstants
{
    public static final boolean middleOfHighway(final HighwayPos highwayPos) {
        final int tilex = highwayPos.getTilex();
        final int tiley = highwayPos.getTiley();
        final boolean onSurface = highwayPos.isOnSurface();
        final BridgePart currentBridgePart = highwayPos.getBridgePart();
        final Floor currentFloor = highwayPos.getFloor();
        if (currentBridgePart != null) {
            return bridgeChecks(tilex, tiley, onSurface, currentBridgePart);
        }
        if (currentFloor != null) {
            return floorChecks(tilex, tiley, onSurface, currentFloor);
        }
        if (!onSurface) {
            return caveChecks(tilex, tiley);
        }
        return surfaceChecks(tilex, tiley);
    }
    
    public static final boolean onHighway(final Item item) {
        if (!Features.Feature.HIGHWAYS.isEnabled()) {
            return false;
        }
        final HighwayPos highwayPos = getHighwayPos(item);
        return onHighway(highwayPos);
    }
    
    public static final boolean onHighway(final int cornerx, final int cornery, final boolean onSurface) {
        if (!Features.Feature.HIGHWAYS.isEnabled()) {
            return false;
        }
        final HighwayPos highwayPos = getHighwayPos(cornerx, cornery, onSurface);
        return onHighway(highwayPos);
    }
    
    public static final boolean onWagonerCamp(final int cornerx, final int cornery, final boolean onSurface) {
        if (!Features.Feature.HIGHWAYS.isEnabled()) {
            return false;
        }
        final HighwayPos highwayPos = getHighwayPos(cornerx, cornery, onSurface);
        return onWagonerCamp(highwayPos);
    }
    
    public static final boolean onHighway(final BridgePart bridgePart) {
        if (!Features.Feature.HIGHWAYS.isEnabled()) {
            return false;
        }
        final HighwayPos highwayPos = getHighwayPos(bridgePart);
        return onHighway(highwayPos);
    }
    
    public static final boolean onHighway(final Floor floor) {
        if (!Features.Feature.HIGHWAYS.isEnabled()) {
            return false;
        }
        final HighwayPos highwayPos = getHighwayPos(floor);
        return onHighway(highwayPos);
    }
    
    public static final boolean onHighway(@Nullable final HighwayPos highwaypos) {
        return highwaypos != null && (containsMarker(highwaypos, (byte)0) || containsMarker(highwaypos, (byte)1) || containsMarker(highwaypos, (byte)2) || containsMarker(highwaypos, (byte)4) || containsMarker(highwaypos, (byte)8) || containsMarker(highwaypos, (byte)16) || containsMarker(highwaypos, (byte)32) || containsMarker(highwaypos, (byte)64) || containsMarker(highwaypos, (byte)(-128)));
    }
    
    public static final boolean onWagonerCamp(@Nullable final HighwayPos highwaypos) {
        return highwaypos != null && (containsWagonerWaystone(highwaypos, (byte)0) || containsWagonerWaystone(highwaypos, (byte)1) || containsWagonerWaystone(highwaypos, (byte)2) || containsWagonerWaystone(highwaypos, (byte)4) || containsWagonerWaystone(highwaypos, (byte)8) || containsWagonerWaystone(highwaypos, (byte)16) || containsWagonerWaystone(highwaypos, (byte)32) || containsWagonerWaystone(highwaypos, (byte)64) || containsWagonerWaystone(highwaypos, (byte)(-128)));
    }
    
    private static final boolean caveChecks(final int tilex, final int tiley) {
        final MeshIO caveMesh = Server.caveMesh;
        final int currentEncodedTile = caveMesh.getTile(tilex, tiley);
        final byte currentType = Tiles.decodeType(currentEncodedTile);
        final boolean onSurface = false;
        if (currentType == Tiles.Tile.TILE_CAVE_EXIT.id) {
            for (int x = -1; x <= 0; ++x) {
                for (int y = -1; y <= 0; ++y) {
                    final int encodedTile = caveMesh.getTile(tilex + x, tiley + y);
                    final byte type = Tiles.decodeType(encodedTile);
                    if (!Tiles.isReinforcedFloor(type) && !Tiles.isRoadType(type) && type != Tiles.Tile.TILE_CAVE_EXIT.id) {
                        if (!Tiles.isSolidCave(type)) {
                            return false;
                        }
                        final int surfaceTile = Server.surfaceMesh.getTile(tilex + x, tiley + y);
                        final byte surfaceType = Tiles.decodeType(surfaceTile);
                        if (!Tiles.isRoadType(surfaceType)) {
                            return false;
                        }
                    }
                }
            }
            return true;
        }
        boolean foundBridge = false;
        if (!Tiles.isReinforcedFloor(currentType) && currentType != Tiles.Tile.TILE_CAVE_EXIT.id) {
            return false;
        }
        final int northEncodedTile = caveMesh.getTile(tilex, tiley - 1);
        final byte northType = Tiles.decodeType(northEncodedTile);
        final BridgePart bridgePartNorth = Zones.getBridgePartFor(tilex, tiley - 1, false);
        if (bridgePartNorth != null) {
            if (bridgePartNorth.getSouthExit() == 0) {
                foundBridge = true;
            }
        }
        else if (!Tiles.isReinforcedFloor(northType) && northType != Tiles.Tile.TILE_CAVE_EXIT.id) {
            return false;
        }
        final int westEncodedTile = caveMesh.getTile(tilex - 1, tiley);
        final byte westType = Tiles.decodeType(westEncodedTile);
        final BridgePart bridgePartWest = Zones.getBridgePartFor(tilex, tiley - 1, false);
        if (bridgePartWest != null) {
            if (bridgePartWest.getEastExit() == 0) {
                foundBridge = true;
            }
        }
        else if (!Tiles.isReinforcedFloor(westType) && westType != Tiles.Tile.TILE_CAVE_EXIT.id) {
            return false;
        }
        if (foundBridge) {
            final BridgePart bridgePart = Zones.getBridgePartFor(tilex - 1, tiley - 1, false);
            if (bridgePart == null) {
                return false;
            }
        }
        else {
            final int northWestEncodedTile = caveMesh.getTile(tilex - 1, tiley - 1);
            final byte northWestType = Tiles.decodeType(northWestEncodedTile);
            if (!Tiles.isReinforcedFloor(northWestType) && northWestType != Tiles.Tile.TILE_CAVE_EXIT.id) {
                return false;
            }
        }
        return true;
    }
    
    private static final boolean surfaceChecks(final int tilex, final int tiley) {
        boolean foundBridge = false;
        final boolean onSurface = true;
        final int currentEncodedTile = Server.surfaceMesh.getTile(tilex, tiley);
        final byte currentType = Tiles.decodeType(currentEncodedTile);
        if (!Tiles.isRoadType(currentType) && currentType != Tiles.Tile.TILE_HOLE.id) {
            return false;
        }
        final int northEncodedTile = Server.surfaceMesh.getTile(tilex, tiley - 1);
        final byte northType = Tiles.decodeType(northEncodedTile);
        if (!Tiles.isRoadType(northType) && northType != Tiles.Tile.TILE_HOLE.id) {
            final BridgePart bridgePart = Zones.getBridgePartFor(tilex, tiley - 1, true);
            if (bridgePart == null) {
                return false;
            }
            if (bridgePart.getSouthExit() == 0) {
                foundBridge = true;
            }
        }
        final int westEncodedTile = Server.surfaceMesh.getTile(tilex - 1, tiley);
        final byte westType = Tiles.decodeType(westEncodedTile);
        if (!Tiles.isRoadType(westType) && westType != Tiles.Tile.TILE_HOLE.id) {
            final BridgePart bridgePart2 = Zones.getBridgePartFor(tilex - 1, tiley, true);
            if (bridgePart2 == null) {
                return false;
            }
            if (bridgePart2.getEastExit() == 0) {
                foundBridge = true;
            }
        }
        if (foundBridge) {
            final BridgePart bridgePart2 = Zones.getBridgePartFor(tilex - 1, tiley - 1, true);
            if (bridgePart2 == null) {
                return false;
            }
        }
        else {
            final int northWestEncodedTile = Server.surfaceMesh.getTile(tilex - 1, tiley - 1);
            final byte northWestType = Tiles.decodeType(northWestEncodedTile);
            if (!Tiles.isRoadType(northWestType) && northWestType != Tiles.Tile.TILE_HOLE.id) {
                return false;
            }
        }
        return true;
    }
    
    private static final boolean bridgeChecks(final int tilex, final int tiley, final boolean onSurface, final BridgePart currentBridgePart) {
        if (currentBridgePart.hasNorthExit()) {
            if (currentBridgePart.getNorthExit() == 0) {
                final MeshIO mesh = onSurface ? Server.surfaceMesh : Server.caveMesh;
                if (!Tiles.isRoadType(mesh.getTile(tilex, tiley - 1))) {
                    return false;
                }
                if (!Tiles.isRoadType(mesh.getTile(tilex - 1, tiley - 1))) {
                    return false;
                }
                final BridgePart bridgePartWest = Zones.getBridgePartFor(tilex - 1, tiley, onSurface);
                return bridgePartWest != null && bridgePartWest.getBridgePartState() == BridgeConstants.BridgeState.COMPLETED;
            }
            else {
                final Floor floorNorth = Zones.getFloor(tilex, tiley - 1, onSurface, currentBridgePart.getNorthExitFloorLevel());
                if (floorNorth == null || floorNorth.getFloorState() != StructureConstants.FloorState.COMPLETED) {
                    return false;
                }
                final Floor floorNorthWest = Zones.getFloor(tilex - 1, tiley - 1, onSurface, currentBridgePart.getNorthExitFloorLevel());
                if (floorNorthWest == null || floorNorthWest.getFloorState() != StructureConstants.FloorState.COMPLETED) {
                    return false;
                }
                final BridgePart bridgePartWest2 = Zones.getBridgePartFor(tilex - 1, tiley, onSurface);
                return bridgePartWest2 != null && bridgePartWest2.getBridgePartState() == BridgeConstants.BridgeState.COMPLETED;
            }
        }
        else if (currentBridgePart.hasWestExit()) {
            if (currentBridgePart.getWestExit() == 0) {
                final MeshIO mesh = onSurface ? Server.surfaceMesh : Server.caveMesh;
                final BridgePart bridgePartNorth = Zones.getBridgePartFor(tilex, tiley - 1, onSurface);
                return bridgePartNorth != null && bridgePartNorth.getBridgePartState() == BridgeConstants.BridgeState.COMPLETED && Tiles.isRoadType(mesh.getTile(tilex - 1, tiley - 1)) && Tiles.isRoadType(mesh.getTile(tilex - 1, tiley));
            }
            final BridgePart bridgePartNorth2 = Zones.getBridgePartFor(tilex, tiley - 1, onSurface);
            if (bridgePartNorth2 == null || bridgePartNorth2.getBridgePartState() != BridgeConstants.BridgeState.COMPLETED) {
                return false;
            }
            final Floor floorNorthWest = Zones.getFloor(tilex - 1, tiley - 1, onSurface, currentBridgePart.getWestExitFloorLevel());
            if (floorNorthWest == null || floorNorthWest.getFloorState() != StructureConstants.FloorState.COMPLETED) {
                return false;
            }
            final Floor floorWest = Zones.getFloor(tilex - 1, tiley, onSurface, currentBridgePart.getWestExitFloorLevel());
            return floorWest != null && floorWest.getFloorState() == StructureConstants.FloorState.COMPLETED;
        }
        else {
            final BridgePart bridgePartNorth2 = Zones.getBridgePartFor(tilex, tiley - 1, onSurface);
            if (bridgePartNorth2 == null || bridgePartNorth2.getBridgePartState() != BridgeConstants.BridgeState.COMPLETED) {
                return false;
            }
            final BridgePart bridgePartNorthWest = Zones.getBridgePartFor(tilex - 1, tiley - 1, onSurface);
            if (bridgePartNorthWest == null || bridgePartNorthWest.getBridgePartState() != BridgeConstants.BridgeState.COMPLETED) {
                return false;
            }
            final BridgePart bridgePartWest2 = Zones.getBridgePartFor(tilex - 1, tiley, onSurface);
            return bridgePartWest2 != null && bridgePartWest2.getBridgePartState() == BridgeConstants.BridgeState.COMPLETED;
        }
    }
    
    private static final boolean floorChecks(final int tilex, final int tiley, final boolean onSurface, final Floor currentFloor) {
        final Floor floorNorth = Zones.getFloor(tilex, tiley - 1, onSurface, currentFloor.getFloorLevel());
        if (floorNorth == null) {
            final BridgePart bridgePartNorth = Zones.getBridgePartFor(tilex, tiley - 1, onSurface);
            if (bridgePartNorth == null || bridgePartNorth.getSouthExitFloorLevel() != currentFloor.getFloorLevel()) {
                return false;
            }
            final BridgePart bridgePartNorthWest = Zones.getBridgePartFor(tilex - 1, tiley - 1, onSurface);
            if (bridgePartNorthWest == null || bridgePartNorthWest.getSouthExitFloorLevel() != currentFloor.getFloorLevel()) {
                return false;
            }
        }
        final Floor floorWest = Zones.getFloor(tilex - 1, tiley, onSurface, currentFloor.getFloorLevel());
        if (floorWest == null) {
            final BridgePart bridgePartWest = Zones.getBridgePartFor(tilex - 1, tiley, onSurface);
            if (bridgePartWest == null || bridgePartWest.getEastExitFloorLevel() != currentFloor.getFloorLevel()) {
                return false;
            }
            final BridgePart bridgePartNorthWest2 = Zones.getBridgePartFor(tilex - 1, tiley - 1, onSurface);
            if (bridgePartNorthWest2 == null || bridgePartNorthWest2.getEastExitFloorLevel() != currentFloor.getFloorLevel()) {
                return false;
            }
        }
        if (floorNorth != null && floorWest != null) {
            final Floor floorNorthWest = Zones.getFloor(tilex - 1, tiley - 1, onSurface, currentFloor.getFloorLevel());
            if (floorNorthWest == null) {
                return false;
            }
        }
        return true;
    }
    
    public static final boolean hasLink(final byte dirs, final byte linkdir) {
        return (dirs & linkdir) != 0x0;
    }
    
    public static final byte getPossibleLinksFrom(final Item marker) {
        final HighwayPos highwayPos = getHighwayPosFromMarker(marker);
        return getPossibleLinksFrom(highwayPos, marker, marker.getAuxData());
    }
    
    public static final byte getPossibleLinksFrom(final HighwayPos highwayPos, final Item marker) {
        return getPossibleLinksFrom(highwayPos, marker, (byte)0);
    }
    
    private static final byte getPossibleLinksFrom(final HighwayPos highwayPos, final Item marker, final byte currentLinks) {
        byte possibles = (byte)(~currentLinks & 0xFF);
        possibles = checkLink(possibles, highwayPos, (byte)1);
        possibles = checkLink(possibles, highwayPos, (byte)2);
        possibles = checkLink(possibles, highwayPos, (byte)4);
        possibles = checkLink(possibles, highwayPos, (byte)8);
        possibles = checkLink(possibles, highwayPos, (byte)16);
        possibles = checkLink(possibles, highwayPos, (byte)32);
        possibles = checkLink(possibles, highwayPos, (byte)64);
        possibles = checkLink(possibles, highwayPos, (byte)(-128));
        if (marker.getTemplateId() == 1114 && numberOfSetBits(possibles) > 2) {
            final int lower = possibles & 0xF;
            final int upper = possibles & 0xF0;
            final int loup = lower << 4;
            final int uplo = upper >>> 4;
            final int upnew = upper & loup;
            final int lonew = lower & uplo;
            final byte poss = (byte)(upnew | lonew);
            if (numberOfSetBits(poss) == 2) {
                possibles = poss;
            }
        }
        return possibles;
    }
    
    private static final byte checkLink(final byte possibles, final HighwayPos currentHighwayPos, final byte checkdir) {
        if (hasLink(possibles, checkdir)) {
            final HighwayPos highwayPos = getNewHighwayPosLinked(currentHighwayPos, checkdir);
            if (highwayPos == null) {
                return (byte)(possibles & ~checkdir);
            }
            final Item marker = getMarker(highwayPos);
            if (marker == null) {
                return (byte)(possibles & ~checkdir);
            }
            if (hasLink(getOppositedir(checkdir), marker.getAuxData())) {
                return (byte)(possibles & ~checkdir);
            }
            if (marker.getTemplateId() == 1114 && numberOfSetBits(marker.getAuxData()) > 1) {
                return (byte)(possibles & ~checkdir);
            }
        }
        return possibles;
    }
    
    public static final void autoLink(final Item newMarker, final byte possibleLinks) {
        final HighwayPos currentHighwayPos = getHighwayPosFromMarker(newMarker);
        addLink(newMarker, currentHighwayPos, possibleLinks, (byte)1, (byte)16);
        addLink(newMarker, currentHighwayPos, possibleLinks, (byte)2, (byte)32);
        addLink(newMarker, currentHighwayPos, possibleLinks, (byte)4, (byte)64);
        addLink(newMarker, currentHighwayPos, possibleLinks, (byte)8, (byte)(-128));
        addLink(newMarker, currentHighwayPos, possibleLinks, (byte)16, (byte)1);
        addLink(newMarker, currentHighwayPos, possibleLinks, (byte)32, (byte)2);
        addLink(newMarker, currentHighwayPos, possibleLinks, (byte)64, (byte)4);
        addLink(newMarker, currentHighwayPos, possibleLinks, (byte)(-128), (byte)8);
        Routes.checkForNewRoutes(newMarker);
    }
    
    private static final void addLink(final Item newMarker, final HighwayPos currentHighwayPos, final byte possibles, final byte linkdir, final byte reversedir) {
        if (hasLink(possibles, linkdir)) {
            final Item linkMarker = getMarker(currentHighwayPos, linkdir);
            if (linkMarker != null) {
                newMarker.setAuxData((byte)(newMarker.getAuxData() | linkdir));
                linkMarker.setAuxData((byte)(linkMarker.getAuxData() | reversedir));
                newMarker.updateModelNameOnGroundItem();
                linkMarker.updateModelNameOnGroundItem();
            }
        }
    }
    
    public static final void removeLinksTo(final Item fromMarker) {
        final Item[] markers = Routes.getRouteMarkers(fromMarker);
        final HighwayPos currentHighwayPos = getHighwayPosFromMarker(fromMarker);
        removeLink(currentHighwayPos, (byte)1, (byte)16);
        removeLink(currentHighwayPos, (byte)2, (byte)32);
        removeLink(currentHighwayPos, (byte)4, (byte)64);
        removeLink(currentHighwayPos, (byte)8, (byte)(-128));
        removeLink(currentHighwayPos, (byte)16, (byte)1);
        removeLink(currentHighwayPos, (byte)32, (byte)2);
        removeLink(currentHighwayPos, (byte)64, (byte)4);
        removeLink(currentHighwayPos, (byte)(-128), (byte)8);
        fromMarker.setAuxData((byte)0);
        Items.removeMarker(fromMarker);
        fromMarker.updateModelNameOnGroundItem();
        for (final Item marker : markers) {
            marker.updateModelNameOnGroundItem();
        }
    }
    
    private static final void removeLink(final HighwayPos currentHighwayPos, final byte fromdir, final byte linkdir) {
        final Item marker = getMarker(currentHighwayPos, fromdir);
        if (marker != null && hasLink(marker.getAuxData(), linkdir)) {
            marker.setAuxData((byte)(marker.getAuxData() & ~linkdir));
            marker.updateModelNameOnGroundItem();
        }
    }
    
    @Nullable
    public static final Item getMarker(final Item marker, final byte dir) {
        final HighwayPos currentHighwayPos = getHighwayPosFromMarker(marker);
        switch (dir) {
            case 1: {
                return getMarker(currentHighwayPos, (byte)1);
            }
            case 2: {
                return getMarker(currentHighwayPos, (byte)2);
            }
            case 4: {
                return getMarker(currentHighwayPos, (byte)4);
            }
            case 8: {
                return getMarker(currentHighwayPos, (byte)8);
            }
            case 16: {
                return getMarker(currentHighwayPos, (byte)16);
            }
            case 32: {
                return getMarker(currentHighwayPos, (byte)32);
            }
            case 64: {
                return getMarker(currentHighwayPos, (byte)64);
            }
            case Byte.MIN_VALUE: {
                return getMarker(currentHighwayPos, (byte)(-128));
            }
            default: {
                return null;
            }
        }
    }
    
    public static final boolean viewProtection(final Creature performer, final Item marker) {
        final HighwayPos highwayPos = getHighwayPosFromMarker(marker);
        return sendShowProtection(performer, marker, highwayPos);
    }
    
    public static final boolean viewProtection(final Creature performer, final HighwayPos highwayPos, final Item marker) {
        return sendShowProtection(performer, marker, highwayPos);
    }
    
    public static final boolean viewLinks(final Creature performer, final Item marker) {
        final HighwayPos highwayPos = getHighwayPosFromMarker(marker);
        return viewLinks(performer, highwayPos, marker, (byte)1, marker.getAuxData());
    }
    
    public static final boolean viewLinks(final Creature performer, final HighwayPos highwayPos, final Item marker) {
        final byte links = getPossibleLinksFrom(highwayPos, marker);
        return viewLinks(performer, highwayPos, marker, (byte)0, links);
    }
    
    public static final boolean viewLinks(final Creature performer, final HighwayPos currentHighwayPos, final Item marker, final byte linktype, final byte links) {
        final String linktypeString = (linktype == 1) ? "Links" : "Possible links";
        boolean showing = false;
        if (links == 0) {
            performer.getCommunicator().sendNormalServerMessage("There are no " + linktypeString.toLowerCase() + " from there!");
        }
        else {
            showing = sendShowLinks(performer, currentHighwayPos, marker, linktype, links);
            if (Servers.isThisATestServer()) {
                int count = 0;
                final int todo = numberOfSetBits(links);
                final StringBuilder buf = new StringBuilder();
                buf.append(linktypeString + " are: ");
                if (hasLink(links, (byte)1) && containsMarker(currentHighwayPos, (byte)1)) {
                    if (count++ > 0) {
                        if (count == todo) {
                            buf.append(" and ");
                        }
                        else {
                            buf.append(", ");
                        }
                    }
                    buf.append(getLinkDirString((byte)1));
                }
                if (hasLink(links, (byte)2) && containsMarker(currentHighwayPos, (byte)2)) {
                    if (count++ > 0) {
                        if (count == todo) {
                            buf.append(" and ");
                        }
                        else {
                            buf.append(", ");
                        }
                    }
                    buf.append(getLinkDirString((byte)2));
                }
                if (hasLink(links, (byte)4) && containsMarker(currentHighwayPos, (byte)4)) {
                    if (count++ > 0) {
                        if (count == todo) {
                            buf.append(" and ");
                        }
                        else {
                            buf.append(", ");
                        }
                    }
                    buf.append(getLinkDirString((byte)4));
                }
                if (hasLink(links, (byte)8) && containsMarker(currentHighwayPos, (byte)8)) {
                    if (count++ > 0) {
                        if (count == todo) {
                            buf.append(" and ");
                        }
                        else {
                            buf.append(", ");
                        }
                    }
                    buf.append(getLinkDirString((byte)8));
                }
                if (hasLink(links, (byte)16) && containsMarker(currentHighwayPos, (byte)16)) {
                    if (count++ > 0) {
                        if (count == todo) {
                            buf.append(" and ");
                        }
                        else {
                            buf.append(", ");
                        }
                    }
                    buf.append(getLinkDirString((byte)16));
                }
                if (hasLink(links, (byte)32) && containsMarker(currentHighwayPos, (byte)32)) {
                    if (count++ > 0) {
                        if (count == todo) {
                            buf.append(" and ");
                        }
                        else {
                            buf.append(", ");
                        }
                    }
                    buf.append(getLinkDirString((byte)32));
                }
                if (hasLink(links, (byte)64) && containsMarker(currentHighwayPos, (byte)64)) {
                    if (count++ > 0) {
                        if (count == todo) {
                            buf.append(" and ");
                        }
                        else {
                            buf.append(", ");
                        }
                    }
                    buf.append(getLinkDirString((byte)64));
                }
                if (hasLink(links, (byte)(-128)) && containsMarker(currentHighwayPos, (byte)(-128))) {
                    if (count++ > 0) {
                        if (count == todo) {
                            buf.append(" and ");
                        }
                        else {
                            buf.append(", ");
                        }
                    }
                    buf.append(getLinkDirString((byte)(-128)));
                }
                performer.getCommunicator().sendNormalServerMessage("test only:" + buf.toString());
            }
        }
        return showing;
    }
    
    private static final boolean sendShowLinks(final Creature performer, final HighwayPos currentHighwayPos, final Item marker, final byte linktype, final byte links) {
        final boolean markerType = marker.getTemplateId() == 1112;
        final byte[] glows = { getLinkGlow(linktype, marker, links, (byte)1), getLinkGlow(linktype, marker, links, (byte)2), getLinkGlow(linktype, marker, links, (byte)4), getLinkGlow(linktype, marker, links, (byte)8), getLinkGlow(linktype, marker, links, (byte)16), getLinkGlow(linktype, marker, links, (byte)32), getLinkGlow(linktype, marker, links, (byte)64), getLinkGlow(linktype, marker, links, (byte)(-128)) };
        return performer.getCommunicator().sendShowLinks(markerType, currentHighwayPos, glows);
    }
    
    private static final byte getLinkGlow(final byte linktype, final Item marker, final byte links, final byte link) {
        if (!hasLink(links, link)) {
            return -1;
        }
        if (linktype != 1) {
            if (marker.getTemplateId() == 1112) {}
            return 2;
        }
        if (marker.getTemplateId() == 1112) {
            final Node node = Routes.getNode(marker.getWurmId());
            if (node != null) {
                final Route route = node.getRoute(link);
                if (route != null) {
                    return 3;
                }
            }
            return 1;
        }
        final int count = numberOfSetBits(marker.getAuxData());
        if (count == 2) {
            return 3;
        }
        if (count == 1) {
            return 2;
        }
        return 1;
    }
    
    public static final boolean sendShowProtection(final Creature performer, final Item marker, final HighwayPos currentHighwayPos) {
        final StringBuilder buf = new StringBuilder();
        buf.append("Protected: center");
        final boolean markerType = marker.getTemplateId() == 1112;
        final HashSet<HighwayPos> protectedTiles = new HashSet<HighwayPos>();
        HighwayPos highwayPos = getNewHighwayPosLinked(currentHighwayPos, (byte)1);
        if (highwayPos != null) {
            protectedTiles.add(highwayPos);
            buf.append(", north");
        }
        highwayPos = getNewHighwayPosLinked(currentHighwayPos, (byte)2);
        if (highwayPos != null && isPaved(highwayPos)) {
            protectedTiles.add(highwayPos);
            buf.append(", northeast");
        }
        highwayPos = getNewHighwayPosLinked(currentHighwayPos, (byte)4);
        if (highwayPos != null && isPaved(highwayPos)) {
            protectedTiles.add(highwayPos);
            buf.append(", east");
        }
        highwayPos = getNewHighwayPosLinked(currentHighwayPos, (byte)8);
        if (highwayPos != null && isPaved(highwayPos)) {
            protectedTiles.add(highwayPos);
            buf.append(", southeast");
        }
        highwayPos = getNewHighwayPosLinked(currentHighwayPos, (byte)16);
        if (highwayPos != null && isPaved(highwayPos)) {
            protectedTiles.add(highwayPos);
            buf.append(", south");
        }
        highwayPos = getNewHighwayPosLinked(currentHighwayPos, (byte)32);
        if (highwayPos != null && isPaved(highwayPos)) {
            protectedTiles.add(highwayPos);
            buf.append(", southwest");
        }
        highwayPos = getNewHighwayPosLinked(currentHighwayPos, (byte)64);
        if (highwayPos != null) {
            protectedTiles.add(highwayPos);
            buf.append(", west");
        }
        highwayPos = getNewHighwayPosLinked(currentHighwayPos, (byte)(-128));
        if (highwayPos != null) {
            protectedTiles.add(highwayPos);
            buf.append(", northwest");
        }
        final HighwayPos[] protectedHPs = protectedTiles.toArray(new HighwayPos[protectedTiles.size()]);
        if (Servers.isThisATestServer()) {
            final int pos = buf.lastIndexOf(",");
            if (pos > 0) {
                buf.replace(pos, pos + 1, " and");
            }
            performer.getCommunicator().sendNormalServerMessage("test only:" + buf.toString());
        }
        return performer.getCommunicator().sendShowProtection(markerType, currentHighwayPos, protectedHPs);
    }
    
    private static boolean isPaved(final HighwayPos highwayPos) {
        if (highwayPos.getBridgeId() != -10L) {
            return true;
        }
        if (highwayPos.getFloorLevel() > 0) {
            return true;
        }
        if (highwayPos.isOnSurface()) {
            final int surfaceTile = Server.surfaceMesh.getTile(highwayPos.getTilex(), highwayPos.getTiley());
            final byte surfaceType = Tiles.decodeType(surfaceTile);
            if (!Tiles.isRoadType(surfaceType)) {
                return false;
            }
        }
        else {
            final int caveTile = Server.caveMesh.getTile(highwayPos.getTilex(), highwayPos.getTiley());
            final byte caveType = Tiles.decodeType(caveTile);
            if (!Tiles.isReinforcedFloor(caveType) && !Tiles.isRoadType(caveType) && caveType != Tiles.Tile.TILE_CAVE_EXIT.id) {
                return false;
            }
        }
        return true;
    }
    
    public static final String getLinkAsString(final byte links) {
        int count = 0;
        final int todo = numberOfSetBits(links);
        final StringBuilder buf = new StringBuilder();
        if (hasLink(links, (byte)1)) {
            if (count++ > 0) {
                if (count == todo) {
                    buf.append(" and ");
                }
                else {
                    buf.append(", ");
                }
            }
            buf.append(getLinkDirString((byte)1));
        }
        if (hasLink(links, (byte)2)) {
            if (count++ > 0) {
                if (count == todo) {
                    buf.append(" and ");
                }
                else {
                    buf.append(", ");
                }
            }
            buf.append(getLinkDirString((byte)2));
        }
        if (hasLink(links, (byte)4)) {
            if (count++ > 0) {
                if (count == todo) {
                    buf.append(" and ");
                }
                else {
                    buf.append(", ");
                }
            }
            buf.append(getLinkDirString((byte)4));
        }
        if (hasLink(links, (byte)8)) {
            if (count++ > 0) {
                if (count == todo) {
                    buf.append(" and ");
                }
                else {
                    buf.append(", ");
                }
            }
            buf.append(getLinkDirString((byte)8));
        }
        if (hasLink(links, (byte)16)) {
            if (count++ > 0) {
                if (count == todo) {
                    buf.append(" and ");
                }
                else {
                    buf.append(", ");
                }
            }
            buf.append(getLinkDirString((byte)16));
        }
        if (hasLink(links, (byte)32)) {
            if (count++ > 0) {
                if (count == todo) {
                    buf.append(" and ");
                }
                else {
                    buf.append(", ");
                }
            }
            buf.append(getLinkDirString((byte)32));
        }
        if (hasLink(links, (byte)64)) {
            if (count++ > 0) {
                if (count == todo) {
                    buf.append(" and ");
                }
                else {
                    buf.append(", ");
                }
            }
            buf.append(getLinkDirString((byte)64));
        }
        if (hasLink(links, (byte)(-128))) {
            if (count++ > 0) {
                if (count == todo) {
                    buf.append(" and ");
                }
                else {
                    buf.append(", ");
                }
            }
            buf.append(getLinkDirString((byte)(-128)));
        }
        if (count == 0) {
            buf.append("none");
        }
        return buf.toString();
    }
    
    public static final boolean containsWagonerWaystone(final HighwayPos highwayPos, final byte fromdir) {
        final Item marker = getMarker(highwayPos, fromdir);
        return marker != null && marker.getTemplateId() != 1114 && marker.getData() != -1L;
    }
    
    public static final boolean containsMarker(final HighwayPos highwayPos, final byte fromdir) {
        return getMarker(highwayPos, fromdir) != null;
    }
    
    @Nullable
    public static final Item getMarker(@Nullable final HighwayPos currentHighwayPos, final byte fromdir) {
        if (currentHighwayPos == null) {
            return null;
        }
        if (fromdir == 0) {
            return getMarker(currentHighwayPos);
        }
        final HighwayPos highwayPos = getNewHighwayPosLinked(currentHighwayPos, fromdir);
        if (highwayPos != null) {
            return getMarker(highwayPos);
        }
        return null;
    }
    
    @Nullable
    public static final Item getMarker(final HighwayPos highwaypos) {
        if (highwaypos == null) {
            return null;
        }
        return Items.getMarker(highwaypos.getTilex(), highwaypos.getTiley(), highwaypos.isOnSurface(), highwaypos.getFloorLevel(), highwaypos.getBridgeId());
    }
    
    @Nullable
    public static final Item getMarker(final Creature creature) {
        return Items.getMarker(creature.getTileX(), creature.getTileY(), creature.isOnSurface(), creature.getFloorLevel(), creature.getBridgeId());
    }
    
    @Nullable
    public static final HighwayPos getHighwayPos(final Item marker) {
        final int tilex = marker.getTileX();
        final int tiley = marker.getTileY();
        final boolean onSurface = marker.isOnSurface();
        if (marker.getBridgeId() != -10L) {
            return new HighwayPos(tilex, tiley, onSurface, Zones.getBridgePartFor(tilex, tiley, onSurface), null);
        }
        if (marker.getFloorLevel() > 0) {
            return new HighwayPos(tilex, tiley, onSurface, null, Zones.getFloor(tilex, tiley, onSurface, marker.getFloorLevel()));
        }
        return new HighwayPos(tilex, tiley, onSurface, null, null);
    }
    
    @Nullable
    public static final HighwayPos getHighwayPos(final BridgePart bridgePart) {
        final int tilex = bridgePart.getTileX();
        final int tiley = bridgePart.getTileY();
        final boolean onSurface = bridgePart.isOnSurface();
        return new HighwayPos(tilex, tiley, onSurface, bridgePart, null);
    }
    
    @Nullable
    public static final HighwayPos getHighwayPos(final Floor floor) {
        final int tilex = floor.getTileX();
        final int tiley = floor.getTileY();
        final boolean onSurface = floor.isOnSurface();
        return new HighwayPos(tilex, tiley, onSurface, null, floor);
    }
    
    @Nullable
    public static final HighwayPos getNewHighwayPosLinked(@Nullable final HighwayPos currentHighwayPos, final byte todir) {
        if (currentHighwayPos == null) {
            return null;
        }
        int tilex = currentHighwayPos.getTilex();
        int tiley = currentHighwayPos.getTiley();
        switch (todir) {
            case 1: {
                --tiley;
                break;
            }
            case 2: {
                --tiley;
                ++tilex;
                break;
            }
            case 4: {
                ++tilex;
                break;
            }
            case 8: {
                ++tiley;
                ++tilex;
                break;
            }
            case 16: {
                ++tiley;
                break;
            }
            case 32: {
                ++tiley;
                --tilex;
                break;
            }
            case 64: {
                --tilex;
                break;
            }
            case Byte.MIN_VALUE: {
                --tiley;
                --tilex;
                break;
            }
        }
        final boolean onSurface = currentHighwayPos.isOnSurface();
        if (currentHighwayPos.getBridgePart() != null) {
            return getNewHighwayPosFromBridge(tilex, tiley, onSurface, currentHighwayPos.getBridgePart(), todir);
        }
        if (currentHighwayPos.getFloor() != null) {
            return getNewHighwayPosFromFloor(tilex, tiley, onSurface, currentHighwayPos.getFloor(), todir);
        }
        if (onSurface) {
            final int encodedtile = Server.surfaceMesh.getTile(tilex, tiley);
            final byte type = Tiles.decodeType(encodedtile);
            if (type == Tiles.Tile.TILE_HOLE.id) {
                return new HighwayPos(tilex, tiley, false, null, null);
            }
            final BridgePart bridgePart = Zones.getBridgePartFor(tilex, tiley, onSurface);
            if (bridgePart != null) {
                if (bridgePart.getSouthExit() == 0 && (todir == -128 || todir == 1 || todir == 2)) {
                    return new HighwayPos(tilex, tiley, onSurface, bridgePart, null);
                }
                if (bridgePart.getWestExit() == 0 && (todir == 2 || todir == 4 || todir == 8)) {
                    return new HighwayPos(tilex, tiley, onSurface, bridgePart, null);
                }
                if (bridgePart.getNorthExit() == 0 && (todir == 8 || todir == 16 || todir == 32)) {
                    return new HighwayPos(tilex, tiley, onSurface, bridgePart, null);
                }
                if (bridgePart.getEastExit() == 0 && (todir == 32 || todir == 64 || todir == -128)) {
                    return new HighwayPos(tilex, tiley, onSurface, bridgePart, null);
                }
            }
        }
        else {
            final int encodedCurrentTile = Server.caveMesh.getTile(currentHighwayPos.getTilex(), currentHighwayPos.getTiley());
            final byte currentType = Tiles.decodeType(encodedCurrentTile);
            final int encodedtile2 = Server.caveMesh.getTile(tilex, tiley);
            final byte type2 = Tiles.decodeType(encodedtile2);
            if (currentType == Tiles.Tile.TILE_CAVE_EXIT.id) {
                if (Tiles.isSolidCave(type2)) {
                    return new HighwayPos(tilex, tiley, true, null, null);
                }
            }
            else {
                if (Tiles.isSolidCave(type2)) {
                    return null;
                }
                final BridgePart bridgePart2 = Zones.getBridgePartFor(tilex, tiley, onSurface);
                if (bridgePart2 != null) {
                    if (bridgePart2.getSouthExit() == 0 && (todir == -128 || todir == 1 || todir == 2)) {
                        return new HighwayPos(tilex, tiley, onSurface, bridgePart2, null);
                    }
                    if (bridgePart2.getWestExit() == 0 && (todir == 2 || todir == 4 || todir == 8)) {
                        return new HighwayPos(tilex, tiley, onSurface, bridgePart2, null);
                    }
                    if (bridgePart2.getNorthExit() == 0 && (todir == 8 || todir == 16 || todir == 32)) {
                        return new HighwayPos(tilex, tiley, onSurface, bridgePart2, null);
                    }
                    if (bridgePart2.getEastExit() == 0 && (todir == 32 || todir == 64 || todir == -128)) {
                        return new HighwayPos(tilex, tiley, onSurface, bridgePart2, null);
                    }
                }
            }
        }
        return new HighwayPos(tilex, tiley, onSurface, null, null);
    }
    
    @Nullable
    public static final HighwayPos getHighwayPos(final int cornerx, final int cornery, final boolean onSurface) {
        if (onSurface) {
            final int encodedTile = Server.surfaceMesh.getTile(cornerx, cornery);
            final byte type = Tiles.decodeType(encodedTile);
            if (type == Tiles.Tile.TILE_HOLE.id) {
                return new HighwayPos(cornerx, cornery, false, null, null);
            }
        }
        final BridgePart bridgePart = Zones.getBridgePartFor(cornerx, cornery, onSurface);
        if (bridgePart != null && (bridgePart.getNorthExit() == 0 || bridgePart.getEastExit() == 0 || bridgePart.getSouthExit() == 0 || bridgePart.getWestExit() == 0)) {
            return new HighwayPos(cornerx, cornery, onSurface, bridgePart, null);
        }
        return new HighwayPos(cornerx, cornery, onSurface, null, null);
    }
    
    @Nullable
    public static final HighwayPos getHighwayPos(final int cornerx, final int cornery, final boolean onSurface, final int heightOffset) {
        if (heightOffset == 0) {
            return getHighwayPos(cornerx, cornery, onSurface);
        }
        final Floor[] floors = Zones.getFloorsAtTile(cornerx, cornery, heightOffset, heightOffset, onSurface);
        if (floors != null && floors.length == 1) {
            return getHighwayPos(floors[0]);
        }
        if (heightOffset > 0) {
            final BridgePart bridgePart = Zones.getBridgePartFor(cornerx, cornery, onSurface);
            if (bridgePart != null) {
                return getHighwayPos(bridgePart);
            }
        }
        return null;
    }
    
    @Nullable
    public static final HighwayPos getHighwayPos(final Creature creature) {
        final int tilex = creature.getTileX();
        final int tiley = creature.getTileY();
        final boolean onSurface = creature.isOnSurface();
        if (creature.getBridgeId() != -10L) {
            final BridgePart bridgePart = Zones.getBridgePartFor(tilex, tiley, onSurface);
            if (bridgePart != null) {
                return new HighwayPos(tilex, tiley, onSurface, bridgePart, null);
            }
        }
        if (creature.getFloorLevel() > 0) {
            final Floor floor = Zones.getFloor(tilex, tiley, onSurface, creature.getFloorLevel());
            if (floor != null) {
                return new HighwayPos(tilex, tiley, onSurface, null, floor);
            }
        }
        return new HighwayPos(tilex, tiley, onSurface, null, null);
    }
    
    @Nullable
    public static final HighwayPos getNewHighwayPosCorner(final Creature performer, final int currentTilex, final int currentTiley, final boolean onSurface, @Nullable final BridgePart currentBridgePart, @Nullable final Floor currentFloor) {
        final Vector2f pos = performer.getPos2f();
        final int posTilex = CoordUtils.WorldToTile(pos.x + 2.0f);
        final int posTiley = CoordUtils.WorldToTile(pos.y + 2.0f);
        if (posTilex == currentTilex && posTiley == currentTiley) {
            return new HighwayPos(currentTilex, currentTiley, onSurface, currentBridgePart, currentFloor);
        }
        byte fromdir = 0;
        if (posTilex == currentTilex && posTiley < currentTiley) {
            fromdir = 1;
        }
        else if (posTilex > currentTilex && posTiley < currentTiley) {
            fromdir = 2;
        }
        else if (posTilex > currentTilex && posTiley == currentTiley) {
            fromdir = 4;
        }
        else if (posTilex > currentTilex && posTiley > currentTiley) {
            fromdir = 8;
        }
        else if (posTilex == currentTilex && posTiley > currentTiley) {
            fromdir = 16;
        }
        else if (posTilex < currentTilex && posTiley > currentTiley) {
            fromdir = 32;
        }
        else if (posTilex < currentTilex && posTiley == currentTiley) {
            fromdir = 64;
        }
        else if (posTilex < currentTilex && posTiley < currentTiley) {
            fromdir = -128;
        }
        if (currentBridgePart != null) {
            return getNewHighwayPosFromBridge(posTilex, posTiley, onSurface, currentBridgePart, fromdir);
        }
        if (currentFloor != null) {
            return getNewHighwayPosFromFloor(posTilex, posTiley, onSurface, currentFloor, fromdir);
        }
        if (onSurface) {
            final int encodedtile = Server.surfaceMesh.getTile(posTilex, posTiley);
            final byte type = Tiles.decodeType(encodedtile);
            if (type == Tiles.Tile.TILE_HOLE.id) {
                return new HighwayPos(posTilex, posTiley, false, null, null);
            }
            final BridgePart bridgePart = Zones.getBridgePartFor(posTilex, posTiley, onSurface);
            if (bridgePart != null) {
                if (bridgePart.getSouthExit() == 0 && (fromdir == -128 || fromdir == 1 || fromdir == 2)) {
                    return new HighwayPos(posTilex, posTiley, onSurface, bridgePart, null);
                }
                if (bridgePart.getWestExit() == 0 && (fromdir == 2 || fromdir == 4 || fromdir == 8)) {
                    return new HighwayPos(posTilex, posTiley, onSurface, bridgePart, null);
                }
                if (bridgePart.getNorthExit() == 0 && (fromdir == 8 || fromdir == 16 || fromdir == 32)) {
                    return new HighwayPos(posTilex, posTiley, onSurface, bridgePart, null);
                }
                if (bridgePart.getEastExit() == 0 && (fromdir == 32 || fromdir == 64 || fromdir == -128)) {
                    return new HighwayPos(posTilex, posTiley, onSurface, bridgePart, null);
                }
            }
        }
        else {
            final int encodedtile = Server.caveMesh.getTile(posTilex, posTiley);
            final byte type = Tiles.decodeType(encodedtile);
            if (Tiles.isSolidCave(type)) {
                return new HighwayPos(posTilex, posTiley, true, null, null);
            }
        }
        return new HighwayPos(posTilex, posTiley, onSurface, null, null);
    }
    
    @Nullable
    private static final HighwayPos getNewHighwayPosFromBridge(final int tilex, final int tiley, final boolean onSurface, final BridgePart currentBridgePart, final byte fromdir) {
        if (currentBridgePart.hasNorthExit() && (fromdir == -128 || fromdir == 1 || fromdir == 2)) {
            if (!currentBridgePart.hasHouseNorthExit()) {
                return new HighwayPos(tilex, tiley, onSurface, null, null);
            }
            final Floor floor = Zones.getFloor(tilex, tiley, onSurface, currentBridgePart.getNorthExitFloorLevel());
            if (floor == null) {
                return null;
            }
            return new HighwayPos(tilex, tiley, onSurface, null, floor);
        }
        else if (currentBridgePart.hasEastExit() && (fromdir == 2 || fromdir == 4 || fromdir == 32 || fromdir == 2)) {
            if (!currentBridgePart.hasHouseEastExit()) {
                return new HighwayPos(tilex, tiley, onSurface, null, null);
            }
            final Floor floor = Zones.getFloor(tilex, tiley, onSurface, currentBridgePart.getEastExitFloorLevel());
            if (floor == null) {
                return null;
            }
            return new HighwayPos(tilex, tiley, onSurface, null, floor);
        }
        else if (currentBridgePart.hasSouthExit() && (fromdir == 8 || fromdir == 16 || fromdir == 32)) {
            if (!currentBridgePart.hasHouseSouthExit()) {
                return new HighwayPos(tilex, tiley, onSurface, null, null);
            }
            final Floor floor = Zones.getFloor(tilex, tiley, onSurface, currentBridgePart.getSouthExitFloorLevel());
            if (floor == null) {
                return null;
            }
            return new HighwayPos(tilex, tiley, onSurface, null, floor);
        }
        else if (currentBridgePart.hasWestExit() && (fromdir == 32 || fromdir == 64 || fromdir == -128 || fromdir == 2)) {
            if (!currentBridgePart.hasHouseWestExit()) {
                return new HighwayPos(tilex, tiley, onSurface, null, null);
            }
            final Floor floor = Zones.getFloor(tilex, tiley, onSurface, currentBridgePart.getWestExitFloorLevel());
            if (floor == null) {
                return null;
            }
            return new HighwayPos(tilex, tiley, onSurface, null, floor);
        }
        else {
            final BridgePart bridgePart = Zones.getBridgePartFor(tilex, tiley, onSurface);
            if (bridgePart != null) {
                return new HighwayPos(tilex, tiley, onSurface, bridgePart, null);
            }
            return null;
        }
    }
    
    @Nullable
    private static final HighwayPos getNewHighwayPosFromFloor(final int tilex, final int tiley, final boolean onSurface, final Floor currentFloor, final byte fromdir) {
        final Floor floor = Zones.getFloor(tilex, tiley, onSurface, currentFloor.getFloorLevel());
        if (floor != null) {
            return new HighwayPos(tilex, tiley, onSurface, null, floor);
        }
        final BridgePart bridgePart = Zones.getBridgePartFor(tilex, tiley, onSurface);
        if (bridgePart != null) {
            if (bridgePart.getSouthExitFloorLevel() == currentFloor.getFloorLevel() && (fromdir == -128 || fromdir == 1 || fromdir == 2)) {
                return new HighwayPos(tilex, tiley, onSurface, bridgePart, null);
            }
            if (bridgePart.getWestExitFloorLevel() == currentFloor.getFloorLevel() && (fromdir == 2 || fromdir == 4 || fromdir == 8)) {
                return new HighwayPos(tilex, tiley, onSurface, bridgePart, null);
            }
            if (bridgePart.getNorthExitFloorLevel() == currentFloor.getFloorLevel() && (fromdir == 8 || fromdir == 16 || fromdir == 32)) {
                return new HighwayPos(tilex, tiley, onSurface, bridgePart, null);
            }
            if (bridgePart.getEastExitFloorLevel() == currentFloor.getFloorLevel() && (fromdir == 32 || fromdir == 64 || fromdir == -128)) {
                return new HighwayPos(tilex, tiley, onSurface, bridgePart, null);
            }
        }
        return null;
    }
    
    @Nullable
    private static final HighwayPos getHighwayPosFromMarker(final Item marker) {
        final int tilex = marker.getTileX();
        final int tiley = marker.getTileY();
        final boolean onSurface = marker.isOnSurface();
        if (marker.getBridgeId() != -10L) {
            final BridgePart bridgePart = Zones.getBridgePartFor(tilex, tiley, onSurface);
            return new HighwayPos(tilex, tiley, onSurface, bridgePart, null);
        }
        if (marker.getFloorLevel() > 0) {
            final Floor floor = Zones.getFloor(tilex, tiley, marker.isOnSurface(), marker.getFloorLevel());
            return new HighwayPos(marker.getTileX(), marker.getTileY(), marker.isOnSurface(), null, floor);
        }
        return new HighwayPos(marker.getTileX(), marker.getTileY(), marker.isOnSurface(), null, null);
    }
    
    public static final String getLinkDirString(final byte linkdir) {
        switch (linkdir) {
            case 1: {
                return "north";
            }
            case 2: {
                return "northeast";
            }
            case 4: {
                return "east";
            }
            case 8: {
                return "southeast";
            }
            case 16: {
                return "south";
            }
            case 32: {
                return "southwest";
            }
            case 64: {
                return "west";
            }
            case Byte.MIN_VALUE: {
                return "northwest";
            }
            default: {
                return "unknown(" + linkdir + ")";
            }
        }
    }
    
    public static final boolean canPlantMarker(@Nullable final Creature performer, final HighwayPos currentHighwayPos, final Item marker, final byte possibleLinks) {
        final int cornerX = currentHighwayPos.getTilex();
        final int cornerY = currentHighwayPos.getTiley();
        final Village village = Villages.getVillagePlus(cornerX, cornerY, true, 2);
        final int pcount = numberOfSetBits(possibleLinks);
        if (marker.getTemplateId() == 1112) {
            if (pcount == 0 && village == null) {
                if (performer != null) {
                    performer.getCommunicator().sendNormalServerMessage("Can only plant if there is an adjacent marker.");
                }
                return false;
            }
        }
        else {
            if (pcount == 0) {
                if (performer != null) {
                    performer.getCommunicator().sendNormalServerMessage("Can only plant if there is an adjacent marker.");
                }
                return false;
            }
            if (pcount > 2) {
                if (performer != null) {
                    performer.getCommunicator().sendNormalServerMessage("Catseyes can only be planted if there is a maximum of two possible links.");
                }
                return false;
            }
        }
        if (performer != null) {
            if (village != null) {
                if (!village.isActionAllowed((short)176, performer)) {
                    performer.getCommunicator().sendNormalServerMessage("You do not have permission to plant a " + marker.getName() + " on (or next to) \"" + village.getName() + "\".");
                    return false;
                }
                if (!village.isHighwayAllowed()) {
                    performer.getCommunicator().sendNormalServerMessage("\"" + village.getName() + "\" does not allow highways.");
                    return false;
                }
                if (village.getReputations().length > 0) {
                    performer.getCommunicator().sendNormalServerMessage("You cannot plant a " + marker.getName() + " on (or next to) \"" + village.getName() + "\" as it has an active kos list.");
                    return false;
                }
            }
            final Skill skill = performer.getSkills().getSkillOrLearn(10031);
            if (skill.getRealKnowledge() < 20.1) {
                performer.getCommunicator().sendNormalServerMessage("You do not have enough skill to plant that.");
                return false;
            }
            if (!performer.isPaying()) {
                performer.getCommunicator().sendNormalServerMessage("You need to be premium to plant that.");
                return false;
            }
            if (checkSlopes(currentHighwayPos)) {
                if (performer != null) {
                    performer.getCommunicator().sendNormalServerMessage("This area is too sloped to allow highway markers.");
                }
                return false;
            }
            HighwayPos highwayPos = getNewHighwayPosLinked(currentHighwayPos, (byte)1);
            if (highwayPos != null && checkSlopes(highwayPos)) {
                if (performer != null) {
                    performer.getCommunicator().sendNormalServerMessage("North tile is too sloped to allow highway markers.");
                }
                return false;
            }
            highwayPos = getNewHighwayPosLinked(currentHighwayPos, (byte)2);
            if (highwayPos != null && checkSlopes(currentHighwayPos)) {
                if (performer != null) {
                    performer.getCommunicator().sendNormalServerMessage("North East tile is too sloped to allow highway markers.");
                }
                return false;
            }
            highwayPos = getNewHighwayPosLinked(currentHighwayPos, (byte)4);
            if (highwayPos != null && checkSlopes(currentHighwayPos)) {
                if (performer != null) {
                    performer.getCommunicator().sendNormalServerMessage("East tile is too sloped to allow highway markers.");
                }
                return false;
            }
            highwayPos = getNewHighwayPosLinked(currentHighwayPos, (byte)8);
            if (highwayPos != null && checkSlopes(currentHighwayPos)) {
                if (performer != null) {
                    performer.getCommunicator().sendNormalServerMessage("South East tile is too sloped to allow highway markers.");
                }
                return false;
            }
            highwayPos = getNewHighwayPosLinked(currentHighwayPos, (byte)16);
            if (highwayPos != null && checkSlopes(currentHighwayPos)) {
                if (performer != null) {
                    performer.getCommunicator().sendNormalServerMessage("South tile is too sloped to allow highway markers.");
                }
                return false;
            }
            highwayPos = getNewHighwayPosLinked(currentHighwayPos, (byte)32);
            if (highwayPos != null && checkSlopes(currentHighwayPos)) {
                if (performer != null) {
                    performer.getCommunicator().sendNormalServerMessage("South West tile is too sloped to allow highway markers.");
                }
                return false;
            }
            highwayPos = getNewHighwayPosLinked(currentHighwayPos, (byte)64);
            if (highwayPos != null && checkSlopes(currentHighwayPos)) {
                if (performer != null) {
                    performer.getCommunicator().sendNormalServerMessage("West tile is too sloped to allow highway markers.");
                }
                return false;
            }
            highwayPos = getNewHighwayPosLinked(currentHighwayPos, (byte)(-128));
            if (highwayPos != null && checkSlopes(currentHighwayPos)) {
                if (performer != null) {
                    performer.getCommunicator().sendNormalServerMessage("North West tile is too sloped to allow highway markers.");
                }
                return false;
            }
        }
        return true;
    }
    
    static boolean checkSlopes(final HighwayPos highwayPos) {
        if (highwayPos.isSurfaceTile()) {
            final MeshTile meshTile = new MeshTile(Server.surfaceMesh, highwayPos.getTilex(), highwayPos.getTiley());
            if (Tiles.isRoadType(meshTile.getTileType()) && meshTile.checkSlopes(20, 28)) {
                return true;
            }
        }
        if (highwayPos.isCaveTile()) {
            final MeshTile meshTile = new MeshTile(Server.caveMesh, highwayPos.getTilex(), highwayPos.getTiley());
            if (Tiles.isRoadType(meshTile.getTileType()) && meshTile.checkSlopes(20, 28)) {
                return true;
            }
        }
        return false;
    }
    
    public static final void removeNearbyMarkers(final Floor floor) {
        final HighwayPos highwayPos = new HighwayPos(floor.getTileX(), floor.getTileY(), floor.isOnSurface(), null, floor);
        removeNearbyMarkers(highwayPos);
    }
    
    public static final void removeNearbyMarkers(final BridgePart bridgePart) {
        final HighwayPos highwayPos = new HighwayPos(bridgePart.getTileX(), bridgePart.getTileY(), bridgePart.isOnSurface(), bridgePart, null);
        removeNearbyMarkers(highwayPos);
    }
    
    public static final void removeNearbyMarkers(final int tilex, final int tiley, final boolean onSurface) {
        final HighwayPos highwayPos = new HighwayPos(tilex, tiley, onSurface, null, null);
        removeNearbyMarkers(highwayPos);
    }
    
    private static final void removeNearbyMarkers(final HighwayPos highwayPos) {
        final Item marker = getMarker(highwayPos);
        if (marker != null) {
            marker.setDamage(100.0f);
        }
        removeNearbyMarker(highwayPos, (byte)1);
        removeNearbyMarker(highwayPos, (byte)2);
        removeNearbyMarker(highwayPos, (byte)4);
        removeNearbyMarker(highwayPos, (byte)8);
        removeNearbyMarker(highwayPos, (byte)16);
        removeNearbyMarker(highwayPos, (byte)32);
        removeNearbyMarker(highwayPos, (byte)64);
        removeNearbyMarker(highwayPos, (byte)(-128));
    }
    
    private static final void removeNearbyMarker(final HighwayPos currentHighwayPos, final byte linkdir) {
        final HighwayPos highwayPos = getNewHighwayPosLinked(currentHighwayPos, (byte)1);
        if (highwayPos != null) {
            final Item marker = getMarker(highwayPos);
            if (marker != null) {
                if (currentHighwayPos.getBridgeId() != -10L || currentHighwayPos.getFloorLevel() != 0) {
                    if (marker.getBridgeId() != -10L || marker.getFloorLevel() != 0) {
                        marker.setDamage(100.0f);
                    }
                }
                else if (marker.getBridgeId() == -10L || marker.getFloorLevel() == 0) {
                    marker.setDamage(100.0f);
                }
            }
        }
    }
    
    public static final byte convertLink(final byte link) {
        switch (link) {
            case 1: {
                return 0;
            }
            case 2: {
                return 1;
            }
            case 4: {
                return 2;
            }
            case 8: {
                return 3;
            }
            case 16: {
                return 4;
            }
            case 32: {
                return 5;
            }
            case 64: {
                return 6;
            }
            case Byte.MIN_VALUE: {
                return 7;
            }
            default: {
                return -1;
            }
        }
    }
    
    public static final byte getOppositedir(final byte fromdir) {
        final int lr4 = (fromdir & 0xFF) >>> 4;
        final int ll4 = (fromdir & 0xFF) << 4;
        final int lc4 = lr4 | ll4;
        final byte oppositedir = (byte)(lc4 & 0xFF);
        return oppositedir;
    }
    
    public static final byte getOtherdir(final byte dirs, final byte fromdir) {
        final byte otherdir = (byte)(dirs & ~fromdir);
        return otherdir;
    }
    
    public static final boolean isNextToACamp(final HighwayPos currentHighwayPos) {
        Item marker = getMarker(currentHighwayPos, (byte)1);
        if (marker != null && marker.getTemplateId() == 1112 && marker.getData() != -1L) {
            return true;
        }
        marker = getMarker(currentHighwayPos, (byte)2);
        if (marker != null && marker.getTemplateId() == 1112 && marker.getData() != -1L) {
            return true;
        }
        marker = getMarker(currentHighwayPos, (byte)4);
        if (marker != null && marker.getTemplateId() == 1112 && marker.getData() != -1L) {
            return true;
        }
        marker = getMarker(currentHighwayPos, (byte)8);
        if (marker != null && marker.getTemplateId() == 1112 && marker.getData() != -1L) {
            return true;
        }
        marker = getMarker(currentHighwayPos, (byte)16);
        if (marker != null && marker.getTemplateId() == 1112 && marker.getData() != -1L) {
            return true;
        }
        marker = getMarker(currentHighwayPos, (byte)32);
        if (marker != null && marker.getTemplateId() == 1112 && marker.getData() != -1L) {
            return true;
        }
        marker = getMarker(currentHighwayPos, (byte)64);
        if (marker != null && marker.getTemplateId() == 1112 && marker.getData() != -1L) {
            return true;
        }
        marker = getMarker(currentHighwayPos, (byte)(-128));
        return marker != null && marker.getTemplateId() == 1112 && marker.getData() != -1L;
    }
    
    public static final int numberOfSetBits(final byte b) {
        return Integer.bitCount(b & 0xFF);
    }
}
