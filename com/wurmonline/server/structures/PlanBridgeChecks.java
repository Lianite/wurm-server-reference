// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.structures;

import com.wurmonline.server.behaviours.Methods;
import com.wurmonline.server.villages.Village;
import com.wurmonline.server.villages.Villages;
import com.wurmonline.server.items.Item;
import com.wurmonline.server.zones.VolaTile;
import com.wurmonline.server.behaviours.Terraforming;
import com.wurmonline.mesh.CaveTile;
import com.wurmonline.mesh.MeshIO;
import com.wurmonline.server.highways.MethodsHighways;
import com.wurmonline.mesh.Tiles;
import com.wurmonline.server.Server;
import com.wurmonline.server.zones.Zones;
import com.wurmonline.server.Point;
import com.wurmonline.server.creatures.Creature;
import com.wurmonline.server.MiscConstants;

public final class PlanBridgeChecks implements MiscConstants
{
    public static boolean passChecks(final Creature performer, final Point start, final Point trueEnd, final byte dir, final int[] hts, final boolean insta) {
        final Point end = new Point(trueEnd);
        if (dir == 0) {
            end.setY(end.getY() + 1);
        }
        else {
            end.setX(end.getX() + 1);
        }
        PlanBridgeCheckResult ans = checkPlanWidth(start, end, dir, performer.getLayer());
        if (!ans.failed()) {
            ans = checkForHighways(start, end, dir, performer.getLayer());
        }
        if (!ans.failed()) {
            ans = checkClearance(start, end, dir, insta, performer.getLayer());
        }
        if (!ans.failed() && performer.isOnSurface()) {
            ans = checkForFolliage(start, end, dir, insta);
        }
        if (!ans.failed() && !performer.isOnSurface()) {
            ans = checkCeilingClearance(start, end, dir, insta, performer.getLayer());
        }
        if (!ans.failed()) {
            ans = checkForCaveEntrances(start, end, dir, insta);
        }
        if (!ans.failed()) {
            ans = checkForSpecialItems(start, end, dir, insta, performer.isOnSurface());
        }
        if (!ans.failed()) {
            ans = checkForSettlements(performer, start, end, dir, insta, performer.isOnSurface());
        }
        if (!ans.failed()) {
            ans = checkForBridges(start, end, dir, insta, performer.isOnSurface());
        }
        if (!ans.failed()) {
            ans = checkForBuildings(start, end, dir, insta, performer.isOnSurface());
        }
        if (ans.failed()) {
            performer.getCommunicator().sendNormalServerMessage("Failed to plan bridge because of the following:");
            performer.getCommunicator().sendNormalServerMessage(ans.pMsg());
            return false;
        }
        return true;
    }
    
    public static PlanBridgeCheckResult calcHeights(final Creature performer, final String targetName, final Point near, final Point far, final Point diff, final int len, final boolean insta) {
        String qMsg = "";
        String rMsg = "";
        if (near.getH() < 1) {
            final String pMsg = "Your end of the bridge is too close to the water level!";
            return new PlanBridgeCheckResult(true, "Your end of the bridge is too close to the water level!", qMsg);
        }
        if (far.getH() < 1) {
            final String pMsg = "Your helpers end of the bridge is too close to the water level!";
            return new PlanBridgeCheckResult(true, "Your helpers end of the bridge is too close to the water level!", qMsg);
        }
        if (diff.getH() != 0) {
            if (near.getH() > far.getH()) {
                performer.getCommunicator().sendNormalServerMessage("You appear to be " + diff.getH() + " dirt higher than " + targetName + ".");
            }
            else {
                performer.getCommunicator().sendNormalServerMessage("You appear to be " + diff.getH() + " dirt lower than " + targetName + ".");
            }
        }
        else {
            performer.getCommunicator().sendNormalServerMessage("Both ends are the same height, Nice!");
        }
        if (insta) {
            qMsg = " (Slope:" + diff.getH() / len + ")";
            rMsg = " (20)";
        }
        if (diff.getH() > 20 * len) {
            final String pMsg = "You calculate the bridge slope" + qMsg + " and realise the slope would exceed the maximum" + rMsg + ".";
            return new PlanBridgeCheckResult(true, pMsg, qMsg);
        }
        return new PlanBridgeCheckResult(false);
    }
    
    public static PlanBridgeCheckResult checkPlanWidth(final Point start, final Point end, final byte dir, final int layer) {
        final String qMsg = "";
        Structure startBuilding;
        Structure endBuilding;
        if (dir == 0) {
            startBuilding = Structures.getStructureForTile(start.getX(), start.getY() - 1, layer == 0);
            endBuilding = Structures.getStructureForTile(end.getX(), end.getY(), layer == 0);
        }
        else {
            startBuilding = Structures.getStructureForTile(start.getX() - 1, start.getY(), layer == 0);
            endBuilding = Structures.getStructureForTile(end.getX(), end.getY(), layer == 0);
        }
        if (startBuilding != null) {
            if (dir == 0) {
                for (int x = start.getX() + 1; x <= end.getX(); ++x) {
                    final Structure building = Structures.getStructureForTile(x, start.getY() - 1, layer == 0);
                    if (building != null && startBuilding.getWurmId() != building.getWurmId()) {
                        final String pMsg = "You notice that the north bridge end does not butt all to same building all way along.";
                        return new PlanBridgeCheckResult(true, "You notice that the north bridge end does not butt all to same building all way along.", "");
                    }
                }
            }
            else {
                for (int y = start.getY() + 1; y <= end.getY(); ++y) {
                    final Structure building = Structures.getStructureForTile(start.getX() - 1, y, layer == 0);
                    if (building != null && startBuilding.getWurmId() != building.getWurmId()) {
                        final String pMsg = "You notice that the west bridge end does not butt all to same building all way along.";
                        return new PlanBridgeCheckResult(true, "You notice that the west bridge end does not butt all to same building all way along.", "");
                    }
                }
            }
        }
        else if (dir == 0) {
            int x = start.getX();
            while (x <= end.getX() + 1) {
                final int ht = (int)(Zones.getHeightForNode(x, start.getY(), layer) * 10.0f);
                if (ht != start.getH()) {
                    if (x == start.getX()) {
                        final String pMsg = "You notice that the north west corner of the plan has changed height since the start of the planning phase.";
                        return new PlanBridgeCheckResult(true, "You notice that the north west corner of the plan has changed height since the start of the planning phase.", "");
                    }
                    final String pMsg = "You notice that the north bridge end is not level for the width of the bridge.";
                    return new PlanBridgeCheckResult(true, "You notice that the north bridge end is not level for the width of the bridge.", "");
                }
                else {
                    ++x;
                }
            }
        }
        else {
            int y = start.getY();
            while (y <= end.getY() + 1) {
                final int ht = (int)(Zones.getHeightForNode(start.getX(), y, layer) * 10.0f);
                if (ht != start.getH()) {
                    if (y == start.getY()) {
                        final String pMsg = "You notice that the north west corner of the plan has changed height since the start of the planning phase.";
                        return new PlanBridgeCheckResult(true, "You notice that the north west corner of the plan has changed height since the start of the planning phase.", "");
                    }
                    final String pMsg = "You notice that the west bridge end is not level for the width of the bridge.";
                    return new PlanBridgeCheckResult(true, "You notice that the west bridge end is not level for the width of the bridge.", "");
                }
                else {
                    ++y;
                }
            }
        }
        if (endBuilding != null) {
            if (dir == 0) {
                for (int x = start.getX() + 1; x <= end.getX(); ++x) {
                    final Structure building = Structures.getStructureForTile(x, end.getY(), layer == 0);
                    if (building != null && endBuilding.getWurmId() != building.getWurmId()) {
                        final String pMsg = "You notice that the south bridge end does not butt all to same building all way along.";
                        return new PlanBridgeCheckResult(true, "You notice that the south bridge end does not butt all to same building all way along.", "");
                    }
                }
            }
            else {
                for (int y = start.getY() + 1; y <= end.getY(); ++y) {
                    final Structure building = Structures.getStructureForTile(end.getX(), y, layer == 0);
                    if (building != null && endBuilding.getWurmId() != building.getWurmId()) {
                        final String pMsg = "You notice that the east bridge end does not butt all to same building all way along.";
                        return new PlanBridgeCheckResult(true, "You notice that the east bridge end does not butt all to same building all way along.", "");
                    }
                }
            }
        }
        else if (dir == 0) {
            int x = start.getX();
            while (x <= end.getX() + 1) {
                final int ht = (int)(Zones.getHeightForNode(x, end.getY(), layer) * 10.0f);
                if (ht != end.getH()) {
                    if (x == start.getX()) {
                        final String pMsg = "You notice that the south west corner of the plan has changed height since the start of the planning phase.";
                        return new PlanBridgeCheckResult(true, "You notice that the south west corner of the plan has changed height since the start of the planning phase.", "");
                    }
                    final String pMsg = "You notice that the south bridge end is not level for the width of the bridge.";
                    return new PlanBridgeCheckResult(true, "You notice that the south bridge end is not level for the width of the bridge.", "");
                }
                else {
                    ++x;
                }
            }
        }
        else {
            int y = start.getY();
            while (y <= end.getY() + 1) {
                final int ht = (int)(Zones.getHeightForNode(end.getX(), y, layer) * 10.0f);
                if (ht != end.getH()) {
                    if (y == start.getY()) {
                        final String pMsg = "You notice that the south west corner of the plan has changed height since the start of the planning phase.";
                        return new PlanBridgeCheckResult(true, "You notice that the south west corner of the plan has changed height since the start of the planning phase.", "");
                    }
                    final String pMsg = "You notice that the east bridge end is not level for the width of the bridge.";
                    return new PlanBridgeCheckResult(true, "You notice that the east bridge end is not level for the width of the bridge.", "");
                }
                else {
                    ++y;
                }
            }
        }
        return new PlanBridgeCheckResult(false);
    }
    
    public static PlanBridgeCheckResult checkForHighways(final Point start, final Point end, final byte dir, final int layer) {
        final MeshIO mesh = (layer == 0) ? Server.surfaceMesh : Server.caveMesh;
        if (dir == 0) {
            for (int x = start.getX(); x <= end.getX(); ++x) {
                final int encodedStartTile = mesh.getTile(x, start.getY());
                final byte startType = Tiles.decodeType(encodedStartTile);
                final int startHeight = Tiles.decodeHeight(encodedStartTile);
                final int startOffset = start.getH() - startHeight;
                if (startOffset == 0 && isRoadTile(startType) && MethodsHighways.onHighway(x, start.getY(), layer == 0)) {
                    final String pMsg = "Cannot build a bridge on a highway.";
                    return new PlanBridgeCheckResult(true, "Cannot build a bridge on a highway.", "");
                }
                final int y = end.getY() - 1;
                final int encodedEndTile = mesh.getTile(x, y);
                final byte endType = Tiles.decodeType(encodedEndTile);
                final int endHeight = Tiles.decodeHeight(encodedEndTile);
                final int endOffset = end.getH() - endHeight;
                if (start.getY() != end.getY() && endOffset == 0 && isRoadTile(endType) && MethodsHighways.onHighway(x, y, layer == 0)) {
                    final String pMsg2 = "Cannot build a bridge on a highway.";
                    return new PlanBridgeCheckResult(true, "Cannot build a bridge on a highway.", "");
                }
            }
        }
        else {
            for (int y2 = start.getY(); y2 <= end.getY(); ++y2) {
                final int encodedStartTile = mesh.getTile(start.getX(), y2);
                final byte startType = Tiles.decodeType(encodedStartTile);
                final int startHeight = Tiles.decodeHeight(encodedStartTile);
                final int startOffset = start.getH() - startHeight;
                if (startOffset == 0 && isRoadTile(startType) && MethodsHighways.onHighway(start.getX(), y2, layer == 0)) {
                    final String pMsg = "Cannot build a bridge on a highway.";
                    return new PlanBridgeCheckResult(true, "Cannot build a bridge on a highway.", "");
                }
                final int x2 = end.getX() - 1;
                final int encodedEndTile = mesh.getTile(x2, y2);
                final byte endType = Tiles.decodeType(encodedEndTile);
                final int endHeight = Tiles.decodeHeight(encodedEndTile);
                final int endOffset = end.getH() - endHeight;
                if (start.getX() != end.getX() && endOffset == 0 && isRoadTile(endType) && MethodsHighways.onHighway(x2, y2, layer == 0)) {
                    final String pMsg2 = "Cannot build a bridge on a highway.";
                    return new PlanBridgeCheckResult(true, "Cannot build a bridge on a highway.", "");
                }
            }
        }
        return new PlanBridgeCheckResult(false);
    }
    
    private static boolean isRoadTile(final byte type) {
        return Tiles.isReinforcedFloor(type) || Tiles.isRoadType(type);
    }
    
    public static PlanBridgeCheckResult checkClearance(final Point start, final Point end, final byte dir, final boolean insta, final int layer) {
        final int len = Math.max(end.getX() - start.getX(), end.getY() - start.getY());
        String qMsg = "";
        float ns = 0.0f;
        if (len == 1) {
            Structure startBuilding;
            Structure endBuilding;
            if (dir == 0) {
                startBuilding = Structures.getStructureForTile(start.getX(), start.getY() - 1, layer == 0);
                endBuilding = Structures.getStructureForTile(end.getX(), end.getY(), layer == 0);
            }
            else {
                startBuilding = Structures.getStructureForTile(start.getX() - 1, start.getY(), layer == 0);
                endBuilding = Structures.getStructureForTile(end.getX(), end.getY(), layer == 0);
            }
            if (startBuilding == null && endBuilding == null) {
                final String pMsg = "One end of both ends must connect to a building!";
                return new PlanBridgeCheckResult(true, "One end of both ends must connect to a building!", qMsg);
            }
        }
        else if (dir == 0) {
            final float slope = (end.getH() - start.getH()) / len;
            float pz = start.getH();
            for (int y = start.getY() + 1; y < end.getY(); ++y) {
                ns += slope;
                pz = start.getH() + ns;
                for (int x = start.getX(); x <= end.getX() + 1; ++x) {
                    final int ht = (int)(Zones.getHeightForNode(x, y, layer) * 10.0f);
                    if (ht + 10 > pz) {
                        if (insta) {
                            qMsg = "(N-S) S:" + start.getX() + "," + start.getY() + "," + start.getH() + " U:" + end.getX() + "," + end.getY() + "," + end.getH() + " Failed at x:" + x + ", y:" + y + ", z:" + pz + ", h:" + ht + ", s:" + slope;
                        }
                        final String pMsg2 = "Some terrain is in the way!";
                        return new PlanBridgeCheckResult(true, "Some terrain is in the way!", qMsg);
                    }
                }
            }
        }
        else {
            final float slope = (end.getH() - start.getH()) / len;
            float pz = end.getH();
            for (int x2 = start.getX() + 1; x2 < end.getX(); ++x2) {
                ns += slope;
                pz = start.getH() + ns;
                for (int y2 = start.getY(); y2 <= end.getY() + 1; ++y2) {
                    final int ht = (int)(Zones.getHeightForNode(x2, y2, layer) * 10.0f);
                    if (ht + 10 > pz) {
                        if (insta) {
                            qMsg = "(W-E) S:" + start.getX() + "," + start.getY() + "," + start.getH() + " U:" + end.getX() + "," + end.getY() + "," + end.getH() + " Failed at x:" + x2 + ", y:" + y2 + ", z:" + pz + ", h:" + ht + ", s:" + slope;
                        }
                        final String pMsg2 = "Some terrain is in the way!";
                        return new PlanBridgeCheckResult(true, "Some terrain is in the way!", qMsg);
                    }
                }
            }
        }
        return new PlanBridgeCheckResult(false);
    }
    
    public static PlanBridgeCheckResult checkCeilingClearance(final Point start, final Point end, final byte dir, final boolean insta, final int layer) {
        final int len = Math.max(end.getX() - start.getX(), end.getY() - start.getY());
        String qMsg = "";
        float ns = 0.0f;
        if (len == 1) {
            return new PlanBridgeCheckResult(false);
        }
        if (dir == 0) {
            final float slope = (end.getH() - start.getH()) / len;
            float pz = start.getH();
            for (int y = start.getY() + 1; y < end.getY(); ++y) {
                ns += slope;
                pz = start.getH() + ns;
                for (int x = start.getX(); x <= end.getX() + 1; ++x) {
                    final int ht = (int)(Zones.getHeightForNode(x, y, layer) * 10.0f);
                    final int encodedTile = Server.caveMesh.getTile(x, y);
                    final int cht = CaveTile.decodeCeilingHeight(encodedTile);
                    final int tht = ht + cht;
                    if (tht < pz + 30.0f) {
                        if (insta) {
                            qMsg = "(N-S) S:" + start.getX() + "," + start.getY() + "," + start.getH() + " U:" + end.getX() + "," + end.getY() + "," + end.getH() + " Failed at x:" + x + ", y:" + y + ", z:" + pz + ", h:" + ht + ", s:" + slope + " Ceiling:" + cht + " Total:" + tht;
                        }
                        final String pMsg = "Ceiling is in the way!";
                        return new PlanBridgeCheckResult(true, "Ceiling is in the way!", qMsg);
                    }
                }
            }
        }
        else {
            final float slope = (end.getH() - start.getH()) / len;
            float pz = end.getH();
            for (int x2 = start.getX() + 1; x2 < end.getX(); ++x2) {
                ns += slope;
                pz = start.getH() + ns;
                for (int y2 = start.getY(); y2 <= end.getY() + 1; ++y2) {
                    final int ht = (int)(Zones.getHeightForNode(x2, y2, layer) * 10.0f);
                    final int encodedTile = Server.caveMesh.getTile(x2, y2);
                    final int cht = CaveTile.decodeCeilingHeight(encodedTile);
                    final int tht = ht + cht;
                    if (tht < pz + 30.0f) {
                        if (insta) {
                            qMsg = "(W-E) S:" + start.getX() + "," + start.getY() + "," + start.getH() + " U:" + end.getX() + "," + end.getY() + "," + end.getH() + " Failed at x:" + x2 + ", y:" + y2 + ", z:" + pz + ", h:" + ht + ", s:" + slope + " Ceiling:" + cht + " Total:" + tht;
                        }
                        final String pMsg = "Ceiling is in the way!";
                        return new PlanBridgeCheckResult(true, "Ceiling is in the way!", qMsg);
                    }
                }
            }
        }
        return new PlanBridgeCheckResult(false);
    }
    
    public static PlanBridgeCheckResult checkForFolliage(final Point start, final Point end, final byte dir, final boolean insta) {
        String qMsg = "";
        if (dir == 0) {
            for (int x = start.getX(); x <= end.getX(); ++x) {
                for (int y = start.getY(); y < end.getY(); ++y) {
                    final byte ttype = Tiles.decodeType(Server.surfaceMesh.getTile(x, y));
                    if (!Terraforming.isBridgeableTile(ttype)) {
                        if (insta) {
                            qMsg = " (@ " + x + "," + y + ")";
                        }
                        final String pMsg = Tiles.getTile(ttype).tiledesc + " is blocking the planning" + qMsg + ".";
                        return new PlanBridgeCheckResult(true, pMsg, qMsg);
                    }
                }
            }
        }
        else {
            for (int y2 = start.getY(); y2 <= end.getY(); ++y2) {
                for (int x2 = start.getX(); x2 < end.getX(); ++x2) {
                    final byte ttype = Tiles.decodeType(Server.surfaceMesh.getTile(x2, y2));
                    if (!Terraforming.isBridgeableTile(ttype)) {
                        if (insta) {
                            qMsg = " (@ " + x2 + "," + y2 + ")";
                        }
                        final String pMsg = Tiles.getTile(ttype).tiledesc + " is blocking the planning" + qMsg + ".";
                        return new PlanBridgeCheckResult(true, pMsg, qMsg);
                    }
                }
            }
        }
        return new PlanBridgeCheckResult(false);
    }
    
    public static PlanBridgeCheckResult checkForCaveEntrances(final Point start, final Point end, final byte dir, final boolean insta) {
        if (dir == 0) {
            for (int x = start.getX() - 1; x <= end.getX() + 1; ++x) {
                for (int y = start.getY() - 1; y <= end.getY(); ++y) {
                    final PlanBridgeCheckResult pbcr = checkForCaveEntrances(x, y, insta);
                    if (pbcr != null) {
                        return pbcr;
                    }
                }
            }
        }
        else {
            for (int y2 = start.getY() - 1; y2 <= end.getY() + 1; ++y2) {
                for (int x2 = start.getX() - 1; x2 <= end.getX(); ++x2) {
                    final PlanBridgeCheckResult pbcr = checkForCaveEntrances(x2, y2, insta);
                    if (pbcr != null) {
                        return pbcr;
                    }
                }
            }
        }
        return new PlanBridgeCheckResult(false);
    }
    
    private static PlanBridgeCheckResult checkForCaveEntrances(final int x, final int y, final boolean insta) {
        final byte ttype = Tiles.decodeType(Server.surfaceMesh.getTile(x, y));
        if (Terraforming.isCaveEntrance(ttype)) {
            String qMsg = "";
            if (insta) {
                qMsg = " (" + Tiles.getTile(ttype).tiledesc + " @ " + x + "," + y + ")";
            }
            final String pMsg = "There is a cave entrance too close to where the bridge would go" + qMsg + ".";
            return new PlanBridgeCheckResult(true, pMsg, qMsg);
        }
        if (ttype == Tiles.Tile.TILE_LAVA.id) {
            String qMsg = "";
            if (insta) {
                qMsg = " (" + Tiles.getTile(ttype).tiledesc + " @ " + x + "," + y + ")";
            }
            final String pMsg = "You cannot plan a bridge over lava" + qMsg + ".";
            return new PlanBridgeCheckResult(true, pMsg, qMsg);
        }
        return null;
    }
    
    public static PlanBridgeCheckResult checkForSpecialItems(final Point start, final Point end, final byte dir, final boolean insta, final boolean onSurface) {
        if (dir == 0) {
            for (int x = start.getX(); x <= end.getX(); ++x) {
                for (int y = start.getY(); y < end.getY(); ++y) {
                    final PlanBridgeCheckResult pbcr = checkForSpecialItems(x, y, insta, onSurface);
                    if (pbcr != null) {
                        return pbcr;
                    }
                }
            }
        }
        else {
            for (int y2 = start.getY(); y2 <= end.getY(); ++y2) {
                for (int x2 = start.getX(); x2 < end.getX(); ++x2) {
                    final PlanBridgeCheckResult pbcr = checkForSpecialItems(x2, y2, insta, onSurface);
                    if (pbcr != null) {
                        return pbcr;
                    }
                }
            }
        }
        return new PlanBridgeCheckResult(false);
    }
    
    private static PlanBridgeCheckResult checkForSpecialItems(final int x, final int y, final boolean insta, final boolean onSurface) {
        String qMsg = "";
        final VolaTile vt = Zones.getTileOrNull(x, y, onSurface);
        if (vt != null) {
            for (final Item i : vt.getItems()) {
                if (i.getTemplateId() == 236) {
                    if (insta) {
                        qMsg = " (" + i.getName() + " @ " + x + "," + y + ")";
                    }
                    final String pMsg = "There would put a settlement token under the bridge which is not allowed" + qMsg + ".";
                    return new PlanBridgeCheckResult(true, pMsg, qMsg);
                }
                if (i.isGuardTower()) {
                    if (insta) {
                        qMsg = " (" + i.getName() + " @ " + x + "," + y + ")";
                    }
                    final String pMsg = "There would put a guard tower under the bridge which is not allowed" + qMsg + ".";
                    return new PlanBridgeCheckResult(true, pMsg, qMsg);
                }
                if (i.isCorpse()) {
                    if (insta) {
                        qMsg = " (" + i.getName() + " @ " + x + "," + y + ")";
                    }
                    final String pMsg = "There is a corpse under the plan, please remove it before trying again" + qMsg + ".";
                    return new PlanBridgeCheckResult(true, pMsg, qMsg);
                }
            }
        }
        return null;
    }
    
    public static PlanBridgeCheckResult checkForSettlements(final Creature performer, final Point start, final Point end, final byte dir, final boolean insta, final boolean onSurface) {
        if (dir == 0) {
            for (int x = start.getX() - 1; x <= end.getX() + 1; ++x) {
                for (int y = start.getY() - 1; y <= end.getY(); ++y) {
                    final PlanBridgeCheckResult pbcr = checkForSettlements(performer, x, y, insta, onSurface);
                    if (pbcr != null) {
                        return pbcr;
                    }
                }
            }
        }
        else {
            for (int y2 = start.getY() - 1; y2 <= end.getY() + 1; ++y2) {
                for (int x2 = start.getX() - 1; x2 <= end.getX(); ++x2) {
                    final PlanBridgeCheckResult pbcr = checkForSettlements(performer, x2, y2, insta, onSurface);
                    if (pbcr != null) {
                        return pbcr;
                    }
                }
            }
        }
        return new PlanBridgeCheckResult(false);
    }
    
    private static PlanBridgeCheckResult checkForSettlements(final Creature performer, final int x, final int y, final boolean insta, final boolean onSurface) {
        String pMsg = "";
        String qMsg = "";
        final Village vill = Villages.getVillage(x, y, onSurface);
        if (vill != null && !vill.isActionAllowed((short)116, performer, false, 0, 0)) {
            if (insta) {
                qMsg = " (@ " + x + "," + y + ")";
            }
            if (vill.isEnemy(performer)) {
                pMsg = vill.getName() + " does not allow that" + qMsg + ".";
            }
            else {
                pMsg = "That would be illegal here. You can check the settlement token for the local laws" + qMsg + ".";
            }
            return new PlanBridgeCheckResult(true, pMsg, qMsg);
        }
        return null;
    }
    
    public static PlanBridgeCheckResult checkForPerimeters(final Creature performer, final Point start, final Point end, final byte dir, final boolean insta, final boolean onSurface) {
        if (dir == 0) {
            for (int x = start.getX(); x <= end.getX(); ++x) {
                for (int y = start.getY(); y < end.getY(); ++y) {
                    final Village vill = Villages.getVillage(x, y, onSurface);
                    if (vill == null) {
                        final Village perim = Villages.getVillageWithPerimeterAt(x, y, onSurface);
                        if (perim != null && !perim.isCitizen(performer) && !Methods.isActionAllowed(performer, (short)116, x, y)) {
                            String qMsg = "";
                            if (insta) {
                                qMsg = " (@ " + x + "," + y + ")";
                            }
                            final String pMsg = "The bridge will pass through " + perim.getName() + "'s perimeter where you do not have permission to build" + qMsg + ".";
                            return new PlanBridgeCheckResult(true, pMsg, qMsg);
                        }
                    }
                }
            }
        }
        else {
            for (int y2 = start.getY(); y2 <= end.getY(); ++y2) {
                for (int x2 = start.getX(); x2 < end.getX(); ++x2) {
                    final Village vill = Villages.getVillage(x2, y2, onSurface);
                    if (vill == null) {
                        final Village perim = Villages.getVillageWithPerimeterAt(x2, y2, onSurface);
                        if (perim != null && !perim.isCitizen(performer) && !Methods.isActionAllowed(performer, (short)116, x2, y2)) {
                            String qMsg = "";
                            if (insta) {
                                qMsg = " (@ " + x2 + "," + y2 + ")";
                            }
                            final String pMsg = "The bridge will pass through " + perim.getName() + "'s perimeter where you do not have permission to build" + qMsg + ".";
                            return new PlanBridgeCheckResult(true, pMsg, qMsg);
                        }
                    }
                }
            }
        }
        return new PlanBridgeCheckResult(false);
    }
    
    public static PlanBridgeCheckResult checkForBridges(final Point start, final Point end, final byte dir, final boolean insta, final boolean onSurface) {
        if (dir == 0) {
            for (int x = start.getX(); x <= end.getX(); ++x) {
                final PlanBridgeCheckResult pbcrN = checkForBridges(x, end.getY() - 1, insta, onSurface);
                if (pbcrN != null) {
                    return pbcrN;
                }
                final PlanBridgeCheckResult pbcrS = checkForBridges(x, end.getY(), insta, onSurface);
                if (pbcrS != null) {
                    return pbcrS;
                }
            }
        }
        else {
            for (int y = start.getY(); y <= end.getY(); ++y) {
                final PlanBridgeCheckResult pbcrW = checkForBridges(start.getX() - 1, y, insta, onSurface);
                if (pbcrW != null) {
                    return pbcrW;
                }
                final PlanBridgeCheckResult pbcrE = checkForBridges(end.getX(), y, insta, onSurface);
                if (pbcrE != null) {
                    return pbcrE;
                }
            }
        }
        return new PlanBridgeCheckResult(false);
    }
    
    private static PlanBridgeCheckResult checkForBridges(final int x, final int y, final boolean insta, final boolean onSurface) {
        final Structure building = Structures.getStructureForTile(x, y, onSurface);
        if (building != null && building.isTypeBridge()) {
            String qMsg = "";
            if (insta) {
                qMsg = " (@ " + x + "," + y + ")";
            }
            final String pMsg = "There is an existing bridge '" + building.getName() + "'too close" + qMsg + " to where you are planning this bridge to go.";
            return new PlanBridgeCheckResult(true, pMsg, qMsg);
        }
        return null;
    }
    
    public static PlanBridgeCheckResult checkForBuildings(final Point start, final Point end, final byte dir, final boolean insta, final boolean onSurfrace) {
        if (dir == 0) {
            for (int x = start.getX(); x <= end.getX(); ++x) {
                for (int y = start.getY(); y < end.getY(); ++y) {
                    final PlanBridgeCheckResult pbcr = checkForBuildings(x, y, insta, onSurfrace);
                    if (pbcr != null) {
                        return pbcr;
                    }
                }
            }
        }
        else {
            for (int y2 = start.getY(); y2 <= end.getY(); ++y2) {
                for (int x2 = start.getX(); x2 < end.getX(); ++x2) {
                    final PlanBridgeCheckResult pbcr = checkForBuildings(x2, y2, insta, onSurfrace);
                    if (pbcr != null) {
                        return pbcr;
                    }
                }
            }
        }
        return new PlanBridgeCheckResult(false);
    }
    
    private static PlanBridgeCheckResult checkForBuildings(final int x, final int y, final boolean insta, final boolean onSurfrace) {
        final Structure building = Structures.getStructureForTile(x, y, onSurfrace);
        if (building != null) {
            String qMsg = "";
            if (insta) {
                qMsg = " (@ " + x + "," + y + ")";
            }
            final String stype = building.isTypeHouse() ? "house" : "bridge";
            final String pMsg = "There is a " + stype + " called '" + building.getName() + "'" + qMsg + " where you are trying to plan this bridge.";
            return new PlanBridgeCheckResult(true, pMsg, qMsg);
        }
        return null;
    }
    
    public static PlanBridgeCheckResult checkForBuildingPermissions(final Creature performer, final int x, final int y, final boolean insta, final boolean onSurface) {
        final Structure building = Structures.getStructureForTile(x, y, onSurface);
        if (building != null && building.isTypeHouse() && !building.isGuest(performer)) {
            String qMsg = "";
            if (insta) {
                qMsg = " (@ " + x + "," + y + ")";
            }
            final String pMsg = "You need to be a guest of the house called '" + building.getName() + "'" + qMsg + " to be able to plan this bridge.";
            return new PlanBridgeCheckResult(true, pMsg, qMsg);
        }
        return new PlanBridgeCheckResult(false);
    }
    
    public static PlanBridgeCheckResult checkCeilingClearance(final Creature performer, final int len, final Point start, final Point end, final byte dir, final boolean insta) {
        if (!performer.isOnSurface()) {
            int[] hts = PlanBridgeMethods.calcArch(performer, 5, len, start, end);
            PlanBridgeCheckResult res = checkCeilingClearance(start, end, dir, hts, insta);
            if (res.failed()) {
                hts = PlanBridgeMethods.calcArch(performer, 10, len, start, end);
                res = checkCeilingClearance(start, end, dir, hts, insta);
            }
            if (res.failed()) {
                hts = PlanBridgeMethods.calcArch(performer, 15, len, start, end);
                res = checkCeilingClearance(start, end, dir, hts, insta);
            }
            if (res.failed()) {
                hts = PlanBridgeMethods.calcArch(performer, 20, len, start, end);
                res = checkCeilingClearance(start, end, dir, hts, insta);
            }
            return res;
        }
        return new PlanBridgeCheckResult(false);
    }
    
    public static PlanBridgeCheckResult checkCeilingClearance(final Point start, final Point end, final byte dir, final int[] hts, final boolean insta) {
        if (dir == 0) {
            for (int x = start.getX(); x <= end.getX(); ++x) {
                for (int y = start.getY(); y < end.getY(); ++y) {
                    final int encodedTile = Server.caveMesh.getTile(x, y);
                    final int ht = Tiles.decodeHeight(encodedTile);
                    final int cht = CaveTile.decodeCeilingHeight(encodedTile);
                    final int tht = ht + cht;
                    if (hts[y - start.getY()] + 30 > tht) {
                        final String qMsg = insta ? (" (@ " + x + "," + y + ")") : "";
                        final String pMsg = "Ceiling too close" + qMsg + ".";
                        return new PlanBridgeCheckResult(true, pMsg, qMsg);
                    }
                }
            }
        }
        else {
            for (int y2 = start.getY(); y2 <= end.getY(); ++y2) {
                for (int x2 = start.getX(); x2 < end.getX(); ++x2) {
                    final int encodedTile = Server.caveMesh.getTile(x2, y2);
                    final int ht = Tiles.decodeHeight(encodedTile);
                    final int cht = CaveTile.decodeCeilingHeight(encodedTile);
                    final int tht = ht + cht;
                    if (hts[x2 - start.getX()] + 30 > tht) {
                        final String qMsg = insta ? (" (@ " + x2 + "," + y2 + ")") : "";
                        final String pMsg = "Ceiling too close" + qMsg + ".";
                        return new PlanBridgeCheckResult(true, pMsg, qMsg);
                    }
                }
            }
        }
        return new PlanBridgeCheckResult(false);
    }
}
