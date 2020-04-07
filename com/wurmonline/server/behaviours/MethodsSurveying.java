// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.behaviours;

import com.wurmonline.server.zones.Zones;
import com.wurmonline.server.Items;
import com.wurmonline.server.NoSuchItemException;
import com.wurmonline.server.players.Player;
import com.wurmonline.server.structures.PlanBridgeCheckResult;
import com.wurmonline.server.structures.Structure;
import com.wurmonline.server.structures.PlanBridgeChecks;
import com.wurmonline.server.Point;
import com.wurmonline.server.Server;
import com.wurmonline.server.Servers;
import com.wurmonline.server.structures.NoSuchStructureException;
import com.wurmonline.server.Features;
import javax.annotation.Nullable;
import com.wurmonline.server.items.Item;
import com.wurmonline.server.creatures.Creature;
import com.wurmonline.server.MiscConstants;

public final class MethodsSurveying implements MiscConstants
{
    public static boolean planBridge(final Action act, final Creature performer, final Item source, final Creature helper, @Nullable final Item target, final short action, final float counter) {
        if (!Features.Feature.CAVE_BRIDGES.isEnabled()) {
            if (!performer.isOnSurface()) {
                performer.getCommunicator().sendNormalServerMessage("You must be on the surface to plan a bridge.");
                return true;
            }
            if (target != null && !target.isOnSurface()) {
                performer.getCommunicator().sendNormalServerMessage("The range pole must be on the surface, so you can use it as a target, to plan a bridge.");
                return true;
            }
            if (helper != null && !helper.isOnSurface()) {
                performer.getCommunicator().sendNormalServerMessage(helper.getName() + " must be on the surface to help you plan a bridge.");
                return true;
            }
        }
        if (target != null && !target.isPlanted()) {
            performer.getCommunicator().sendNormalServerMessage("The range pole must be planted to stop it swaying, to help plan a bridge.");
            return true;
        }
        if (performer.getBuildingId() != -10L) {
            Structure plannedStructure = null;
            try {
                plannedStructure = performer.getStructure();
                if (plannedStructure.isTypeBridge()) {
                    performer.getCommunicator().sendNormalServerMessage("You cannot design a bridge as your mind keeps reverting back to the bridge \"" + plannedStructure.getName() + "\" that you are currently constructing.");
                }
                else if (plannedStructure.isFinalized()) {
                    performer.getCommunicator().sendNormalServerMessage("You cannot design a bridge as your mind keeps reverting back to the house \"" + plannedStructure.getName() + "\" that you are currently constructing.");
                }
                else {
                    performer.getCommunicator().sendNormalServerMessage("You cannot design a bridge as your mind keeps reverting back to the house that you are currently in the process of planning.");
                }
            }
            catch (NoSuchStructureException nse) {
                performer.getCommunicator().sendNormalServerMessage("You are already planning a building or a bridge, finish it before planning this bridge.");
            }
            return true;
        }
        if (performer.getVehicle() != -10L) {
            performer.getCommunicator().sendNormalServerMessage("A vehicle is not a steady enough platform to plan a bridge from.");
            return true;
        }
        boolean done = false;
        final boolean insta = Servers.localServer.testServer && performer.getPower() > 1;
        final int rangeMeters = (helper != null) ? Creature.rangeTo(performer, helper) : Creature.rangeTo(performer, target);
        final int timePerStage = insta ? 2 : (rangeMeters / 8 + 2);
        int time = timePerStage * 170;
        if (counter == 1.0f) {
            act.setTimeLeft(time);
            performer.sendActionControl(Actions.actionEntrys[637].getVerbString(), true, time);
            performer.getCommunicator().sendNormalServerMessage("You carefully place the dioptra on its tripod in front of you.");
            Server.getInstance().broadCastAction(performer.getName() + " starts to plan a bridge.", performer, 5);
            act.setTickCount(0);
            if (insta) {
                performer.getCommunicator().sendNormalServerMessage("(Note: Anything shown in brackets will be shown on test server for GM+ only.)");
            }
        }
        else {
            time = act.getTimeLeft();
        }
        if (helper != null && !insta && act.getTickCount() > 5 && act.getTickCount() < 12 && !isHoldingRangePole(helper)) {
            performer.getCommunicator().sendNormalServerMessage(helper.getName() + " is not holding a range pole.");
            done = true;
        }
        if (!done && act.currentSecond() % timePerStage == 0) {
            act.incTickCount();
            final float hX = (helper != null) ? helper.getStatus().getPositionX() : target.getPosX();
            final float hY = (helper != null) ? helper.getStatus().getPositionY() : target.getPosY();
            final double rotRads = Math.atan2(performer.getStatus().getPositionY() - hY, performer.getStatus().getPositionX() - hX);
            final float rot = (float)(rotRads * 57.29577951308232) + 90.0f;
            final float rAngle = Creature.normalizeAngle(rot);
            final String rs = (Servers.isThisATestServer() && insta) ? (" (LOS angle:" + rAngle + ")") : "";
            final float pRot = performer.getStatus().getRotation();
            final float pAngle = Creature.normalizeAngle(pRot - rot);
            final String ps = (Servers.isThisATestServer() && insta) ? (" (performerAngle:" + pAngle + ")") : "";
            final float tRot = (helper != null) ? helper.getStatus().getRotation() : 0.0f;
            final float tAngle = Creature.normalizeAngle(tRot - rot);
            final String ts = (Servers.isThisATestServer() && insta && helper != null) ? (" (targetAngle:" + tAngle + ")") : "";
            final Point near = (helper != null) ? getBridgeEnd(performer) : getBridgeEnd(performer, null);
            final Point far = (helper != null) ? getBridgeEnd(helper) : getBridgeEnd(performer, target);
            final Point diff = new Point(Math.abs(near.getX() - far.getX()), Math.abs(near.getY() - far.getY()), Math.abs(near.getH() - far.getH()));
            final Point start = new Point(Math.min(near.getX(), far.getX()), Math.min(near.getY(), far.getY()));
            final Point end = new Point(Math.max(near.getX(), far.getX()), Math.max(near.getY(), far.getY()));
            byte dir = 0;
            int len = 0;
            int wid = 0;
            String lDir = "";
            if (performer.getStatus().getDir() == 0 || performer.getStatus().getDir() == 4) {
                dir = 0;
                len = diff.getY();
                wid = diff.getX() + 1;
                lDir = "North-South";
            }
            else {
                dir = 6;
                len = diff.getX();
                wid = diff.getY() + 1;
                lDir = "East-West";
            }
            if (dir == 0) {
                if (near.getY() < far.getY()) {
                    start.setH(near.getH());
                    end.setH(far.getH());
                }
                else {
                    start.setH(far.getH());
                    end.setH(near.getH());
                }
            }
            else if (near.getX() < far.getX()) {
                start.setH(near.getH());
                end.setH(far.getH());
            }
            else {
                start.setH(far.getH());
                end.setH(near.getH());
            }
            String rp = "the range pole";
            if (helper != null) {
                rp = helper.getName();
            }
            String pMsg = "";
            String hMsg = "";
            String bMsg = "";
            String qMsg = "";
            String rMsg = "";
            switch (act.getTickCount()) {
                case 1: {
                    if (insta) {
                        performer.getCommunicator().sendNormalServerMessage("Start(x,y,h):(" + start.getX() + "," + start.getY() + "," + start.getH() + ") End(x,y,h):(" + end.getX() + "," + end.getY() + "," + end.getH() + ")");
                    }
                    performer.getCommunicator().sendNormalServerMessage("looks like bridge would be " + wid + "x" + len + " (" + lDir + ")");
                    if (!isBorderLevel(performer)) {
                        pMsg = "The (" + dirAsString(performer.getStatus().getDir()) + ") tile border in front of you must be level.";
                        done = true;
                        break;
                    }
                    performer.getCommunicator().sendNormalServerMessage("The tile border to your " + dirAsString(performer.getStatus().getDir()) + " is level");
                    pMsg = "You make sure the dioptra is level.";
                    bMsg = performer.getName() + " levels a dioptra.";
                    break;
                }
                case 2: {
                    if (pAngle < 170.0f || pAngle > 190.0f) {
                        pMsg = "You are not facing " + rp + ps + rs + ".";
                        done = true;
                        break;
                    }
                    pMsg = "You point the dioptra at " + rp + ".";
                    bMsg = performer.getName() + " points the dioptra.";
                    break;
                }
                case 3: {
                    final float maxDioptraTiles = maxTilesFor(source);
                    if (insta) {
                        qMsg = " (QL:" + source.getQualityLevel() + " [max tiles=" + maxDioptraTiles + "])";
                        rMsg = " (Range:" + rangeMeters + ")";
                    }
                    else if (maxDioptraTiles < rangeMeters / 4) {
                        pMsg = "The quality of the dioptra makes it too hard to work out the distance.";
                        done = true;
                        break;
                    }
                    if (helper != null) {
                        Emotes.emoteAt((short)2013, performer, helper);
                    }
                    pMsg = "You line up the dioptra with " + rp + ".";
                    hMsg = performer.getName() + " just signaled to you, so make sure you are holding your range pole and you are facing them!";
                    bMsg = performer.getName() + " points the dioptra.";
                    break;
                }
                case 4: {
                    if (helper != null) {
                        if (tAngle > 10.0f && tAngle < 350.0f) {
                            pMsg = helper.getName() + " is not facing you" + ts + rs + ".";
                            hMsg = "You need to face " + performer.getName() + " to help plan the bridge.";
                            Emotes.emoteAt((short)2017, performer, helper);
                            done = true;
                            break;
                        }
                        final int odir = (helper.getStatus().getDir() + 4) % 8;
                        if (odir != performer.getStatus().getDir()) {
                            pMsg = "You (facing " + dirAsString(performer.getStatus().getDir()) + ") and " + helper.getName() + " (facing " + dirAsString(helper.getStatus().getDir()) + ") are not facing in opposite directions.";
                            hMsg = "You need to face in opposite directions to plan the bridge.";
                            done = true;
                            break;
                        }
                        if (!insta && !isHoldingRangePole(helper)) {
                            pMsg = helper.getName() + " is not holding a range pole.";
                            done = true;
                            break;
                        }
                        if (insta) {
                            rMsg = " (Range:" + rangeMeters + ")";
                        }
                    }
                    final Item item = (helper != null) ? subjectItemOrNull(helper) : target;
                    float poleQL = 0.0f;
                    float maxPoleTiles = 0.0f;
                    if (item != null) {
                        poleQL = item.getQualityLevel();
                        maxPoleTiles = maxTilesFor(item);
                    }
                    if (insta) {
                        qMsg = " (QL:" + poleQL + " [max tiles:" + maxPoleTiles + "])";
                        rMsg = " (Range:" + rangeMeters + ")";
                    }
                    else if (maxPoleTiles < rangeMeters / 4) {
                        pMsg = "You can't read the graduations on the range pole at this distance.";
                        done = true;
                        break;
                    }
                    pMsg = "You read the graduations on the range pole" + qMsg + rMsg + " and work out the height difference.";
                    break;
                }
                case 5: {
                    if (helper != null && !isBorderLevel(helper)) {
                        pMsg = "The (" + dirAsString(helper.getStatus().getDir()) + ") tile border in front of " + helper.getName() + " is not level.";
                        hMsg = "The (" + dirAsString(helper.getStatus().getDir()) + ") tile border in front of you must be level.";
                        done = true;
                        break;
                    }
                    if (target != null && !isBorderLevel(performer, target)) {
                        pMsg = "The (" + dirAsString(getDir(performer, target)) + ") tile border in front of the range pole is not level.";
                        done = true;
                        break;
                    }
                    if (helper != null) {
                        performer.getCommunicator().sendNormalServerMessage("The tile border to your helpers " + dirAsString(helper.getStatus().getDir()) + " is level");
                    }
                    else {
                        performer.getCommunicator().sendNormalServerMessage("The tile border to your rangepole's " + dirAsString(getDir(performer, target)) + " is level");
                    }
                    pMsg = "You check the heights of the plan ends" + qMsg + ".";
                    break;
                }
                case 6: {
                    final PlanBridgeCheckResult ans = PlanBridgeChecks.calcHeights(performer, (helper != null) ? helper.getName() : "rangepole", near, far, diff, len, insta);
                    done = ans.failed();
                    qMsg = ans.qMsg();
                    pMsg = ans.pMsg();
                    if (!done) {
                        pMsg = "You work out the orientation and width for the bridge" + qMsg + ".";
                        break;
                    }
                    break;
                }
                case 7: {
                    if (wid > 1) {
                        final PlanBridgeCheckResult ans = PlanBridgeChecks.checkPlanWidth(start, end, dir, performer.getLayer());
                        done = ans.failed();
                        qMsg = ans.qMsg();
                        pMsg = ans.pMsg();
                    }
                    if (!done && Features.Feature.HIGHWAYS.isEnabled()) {
                        final PlanBridgeCheckResult ans = PlanBridgeChecks.checkForHighways(start, end, dir, performer.getLayer());
                        done = ans.failed();
                        qMsg = ans.qMsg();
                        pMsg = ans.pMsg();
                    }
                    if (!done) {
                        pMsg = "You check for clearance between the bridge ends.";
                        break;
                    }
                    break;
                }
                case 8: {
                    final PlanBridgeCheckResult ans = PlanBridgeChecks.checkClearance(start, end, dir, insta, performer.getLayer());
                    done = ans.failed();
                    qMsg = ans.qMsg();
                    pMsg = ans.pMsg();
                    if (done) {
                        if (pMsg.length() > 0) {
                            performer.getCommunicator().sendNormalServerMessage(pMsg);
                        }
                        if (qMsg.length() > 0) {
                            performer.getCommunicator().sendNormalServerMessage(qMsg);
                        }
                        pMsg = "You notice that there is not enough clearance for a bridge to be built here.";
                        break;
                    }
                    if (performer.isOnSurface()) {
                        pMsg = "You check for protruding foliage.";
                        break;
                    }
                    pMsg = "You check for ceiling clearance.";
                    break;
                }
                case 9: {
                    PlanBridgeCheckResult ans;
                    if (performer.isOnSurface()) {
                        ans = PlanBridgeChecks.checkForFolliage(start, end, dir, insta);
                    }
                    else {
                        ans = PlanBridgeChecks.checkCeilingClearance(start, end, dir, insta, performer.getLayer());
                    }
                    done = ans.failed();
                    qMsg = ans.qMsg();
                    pMsg = ans.pMsg();
                    if (!done) {
                        pMsg = "You check for nearby cave entrances and any lava.";
                        break;
                    }
                    break;
                }
                case 10: {
                    final PlanBridgeCheckResult ans = PlanBridgeChecks.checkForCaveEntrances(start, end, dir, insta);
                    done = ans.failed();
                    qMsg = ans.qMsg();
                    pMsg = ans.pMsg();
                    if (!done) {
                        pMsg = "You check for any items in the way.";
                        break;
                    }
                    break;
                }
                case 11: {
                    final PlanBridgeCheckResult ans = PlanBridgeChecks.checkForSpecialItems(start, end, dir, insta, performer.isOnSurface());
                    done = ans.failed();
                    qMsg = ans.qMsg();
                    pMsg = ans.pMsg();
                    if (!done) {
                        pMsg = "You check that you have permissions to build the bridge.";
                        break;
                    }
                    break;
                }
                case 12: {
                    PlanBridgeCheckResult ans = PlanBridgeChecks.checkForSettlements(performer, start, end, dir, insta, performer.isOnSurface());
                    done = ans.failed();
                    qMsg = ans.qMsg();
                    pMsg = ans.pMsg();
                    if (done) {
                        break;
                    }
                    ans = PlanBridgeChecks.checkForPerimeters(performer, start, end, dir, insta, performer.isOnSurface());
                    done = ans.failed();
                    qMsg = ans.qMsg();
                    pMsg = ans.pMsg();
                    if (pMsg.length() == 0) {
                        pMsg = "You check for any bridges that may interfere with this bridge.";
                        break;
                    }
                    break;
                }
                case 13: {
                    final PlanBridgeCheckResult ans = PlanBridgeChecks.checkForBridges(start, end, dir, insta, performer.isOnSurface());
                    done = ans.failed();
                    qMsg = ans.qMsg();
                    pMsg = ans.pMsg();
                    if (pMsg.length() == 0) {
                        pMsg = "You check there would be no buildings under this bridge.";
                        break;
                    }
                    break;
                }
                case 14: {
                    PlanBridgeCheckResult ans = PlanBridgeChecks.checkForBuildings(start, end, dir, insta, performer.isOnSurface());
                    done = ans.failed();
                    qMsg = ans.qMsg();
                    pMsg = ans.pMsg();
                    if (pMsg.length() == 0) {
                        ans = PlanBridgeChecks.checkForBuildingPermissions(performer, performer.getTileX(), performer.getTileY(), insta, performer.isOnSurface());
                        done = ans.failed();
                        qMsg = ans.qMsg();
                        pMsg = ans.pMsg();
                        if (pMsg.length() == 0) {
                            if (helper != null) {
                                ans = PlanBridgeChecks.checkForBuildingPermissions(performer, helper.getTileX(), helper.getTileY(), insta, performer.isOnSurface());
                            }
                            else {
                                ans = PlanBridgeChecks.checkForBuildingPermissions(performer, target.getTileX(), target.getTileY(), insta, performer.isOnSurface());
                            }
                            done = ans.failed();
                            qMsg = ans.qMsg();
                            pMsg = ans.pMsg();
                        }
                    }
                    if (pMsg.length() == 0) {
                        pMsg = "You work out the styles of bridges that would fit.";
                        break;
                    }
                    break;
                }
                case 15: {
                    pMsg = "You start working out the bridge components required.";
                    bMsg = performer.getName() + " starts working out the bridge components required.";
                    break;
                }
            }
            rMsg = "";
            if (insta) {
                rMsg = "(" + act.getTickCount() + ") ";
            }
            if (pMsg.length() > 0) {
                performer.getCommunicator().sendNormalServerMessage(rMsg + pMsg);
            }
            if (helper != null && hMsg.length() > 0) {
                helper.getCommunicator().sendNormalServerMessage(hMsg);
            }
            if (bMsg.length() > 0) {
                Server.getInstance().broadCastAction(bMsg, performer, 5);
            }
            if (act.getTickCount() == 15) {
                final int floorLevel = (helper != null) ? helper.getFloorLevel() : target.getFloorLevel();
                Methods.sendPlanBridgeQuestion(performer, floorLevel, start, end, dir, wid, len);
            }
        }
        if (act.getTickCount() >= 16 || done) {
            performer.getCommunicator().sendNormalServerMessage("You stop surveying.");
            performer.getCommunicator().sendNormalServerMessage("You pack up the dioptra.");
            done = true;
        }
        return done;
    }
    
    public static boolean survey(final Action act, final Creature performer, final Item source, final Creature helper, @Nullable final Item target, final short action, final float counter) {
        boolean done = false;
        if (helper != null && performer.isOnSurface() != helper.isOnSurface()) {
            performer.getCommunicator().sendNormalServerMessage("You must both be on surface or in a cave.");
            done = true;
        }
        else if (target != null && performer.isOnSurface() != target.isOnSurface()) {
            performer.getCommunicator().sendNormalServerMessage("You must both be on surface or in a cave.");
            done = true;
        }
        else if (target != null && !target.isPlanted()) {
            performer.getCommunicator().sendNormalServerMessage("The range pole must be planted to stop it swaying.");
            done = true;
        }
        else {
            final boolean insta = Servers.localServer.testServer && performer.getPower() > 1;
            final int rangeMeters = (helper != null) ? Creature.rangeTo(performer, helper) : Creature.rangeTo(performer, target);
            final int timePerStage = insta ? 2 : (rangeMeters / 8 + 2);
            int time = timePerStage * 70;
            if (counter == 1.0f) {
                act.setTimeLeft(time);
                performer.sendActionControl(Actions.actionEntrys[640].getVerbString(), true, time);
                performer.getCommunicator().sendNormalServerMessage("You carefully place the dioptra on its tripod in front of you.");
                Server.getInstance().broadCastAction(performer.getName() + " starts to survey.", performer, 5);
                act.setTickCount(0);
                if (insta) {
                    performer.getCommunicator().sendNormalServerMessage("(Note: Anything shown in brackets will be shown on test server for GM+ only.)");
                }
            }
            else {
                time = act.getTimeLeft();
            }
            if (!done && act.currentSecond() % timePerStage == 0) {
                act.incTickCount();
                final float hX = (helper != null) ? helper.getStatus().getPositionX() : target.getPosX();
                final float hY = (helper != null) ? helper.getStatus().getPositionY() : target.getPosY();
                final double rotRads = Math.atan2(performer.getStatus().getPositionY() - hY, performer.getStatus().getPositionX() - hX);
                final float rot = (float)(rotRads * 57.29577951308232) + 90.0f;
                final float rAngle = Creature.normalizeAngle(rot);
                final String rs = (Servers.isThisATestServer() && insta) ? (" (LOS angle:" + rAngle + ")") : "";
                final float pRot = performer.getStatus().getRotation();
                final float pAngle = Creature.normalizeAngle(pRot - rot);
                final String ps = (Servers.isThisATestServer() && insta) ? (" (performerAngle:" + pAngle + ")") : "";
                final float tRot = (helper != null) ? helper.getStatus().getRotation() : 0.0f;
                final float tAngle = Creature.normalizeAngle(tRot - rot);
                final String ts = (Servers.isThisATestServer() && insta && helper != null) ? (" (targetAngle:" + tAngle + ")") : "";
                final Point near = new Point(performer.getTileX(), performer.getTileY(), performer.getPosZDirts());
                final Point far = (helper != null) ? new Point(helper.getTileX(), helper.getTileY(), helper.getPosZDirts()) : new Point(target.getTileX(), target.getTileY(), (int)(target.getPosZ() * 10.0f));
                String rp = "the range pole";
                if (helper != null) {
                    rp = helper.getName();
                }
                String pMsg = "";
                String hMsg = "";
                String bMsg = "";
                String qMsg = "";
                String rMsg = "";
                switch (act.getTickCount()) {
                    case 1: {
                        pMsg = "You make sure the dioptra is level.";
                        bMsg = performer.getName() + " levels a dioptra.";
                        break;
                    }
                    case 2: {
                        if (pAngle < 170.0f || pAngle > 190.0f) {
                            pMsg = "You are not facing " + rp + ps + rs + ".";
                            done = true;
                            break;
                        }
                        pMsg = "You point the dioptra at " + rp + ".";
                        bMsg = performer.getName() + " points the dioptra.";
                        break;
                    }
                    case 3: {
                        final float maxDioptraTiles = maxTilesFor(source);
                        if (insta) {
                            qMsg = " (QL:" + source.getQualityLevel() + " [max tiles=" + maxDioptraTiles + "])";
                            rMsg = " (Range:" + rangeMeters + ")";
                        }
                        else if (maxDioptraTiles < rangeMeters / 4) {
                            pMsg = "The quality of the dioptra makes it too hard to work out the distance.";
                            done = true;
                            break;
                        }
                        pMsg = "You line up the dioptra with the range pole.";
                        bMsg = performer.getName() + " points the dioptra.";
                        break;
                    }
                    case 4: {
                        if (helper != null) {
                            if (tAngle > 10.0f && tAngle < 350.0f) {
                                pMsg = helper.getName() + " is not facing you" + ts + rs + ".";
                                hMsg = "You need to face " + performer.getName() + " to help survey.";
                                Emotes.emoteAt((short)2017, performer, helper);
                                done = true;
                                break;
                            }
                            if (!insta && !isHoldingRangePole(helper)) {
                                pMsg = helper.getName() + " is not holding a range pole.";
                                done = true;
                                break;
                            }
                            if (insta) {
                                rMsg = " (Range:" + rangeMeters + ")";
                            }
                        }
                        final Item item = (helper != null) ? subjectItemOrNull(helper) : target;
                        float maxPoleTiles = 0.0f;
                        float poleQL = 0.0f;
                        if (item != null) {
                            poleQL = item.getQualityLevel();
                            maxPoleTiles = maxTilesFor(item);
                        }
                        if (insta) {
                            qMsg = " (QL:" + poleQL + " [max tiles:" + maxPoleTiles + "])";
                            rMsg = " (Range:" + rangeMeters + ")";
                        }
                        else if (maxPoleTiles < rangeMeters / 4) {
                            pMsg = "You can't read the graduations on the range pole at this distance.";
                            done = true;
                            break;
                        }
                        pMsg = "You read the graduations on the range pole" + qMsg + rMsg + " and work out the height difference.";
                        break;
                    }
                    case 5: {
                        final Point diff = new Point(near.getX() - far.getX(), near.getY() - far.getY(), Math.abs(near.getH() - far.getH()));
                        if (diff.getY() != 0) {
                            if (diff.getX() != 0) {
                                pMsg = "The range pole is " + Math.abs(diff.getY()) + " tiles " + ((diff.getY() > 0) ? "North" : "South") + " and " + Math.abs(diff.getX()) + " tiles " + ((diff.getX() > 0) ? "West" : "East") + " of you.";
                            }
                            else {
                                pMsg = "The range pole is " + Math.abs(diff.getY()) + " tiles straight " + ((diff.getY() > 0) ? "North" : "South") + " of you.";
                            }
                        }
                        else if (diff.getX() != 0) {
                            pMsg = "The range pole is " + Math.abs(diff.getX()) + " tiles straight " + ((diff.getX() > 0) ? "West" : "East") + " of you.";
                        }
                        if (diff.getH() != 0) {
                            if (near.getH() > far.getH()) {
                                pMsg = pMsg + " Also you appear to be " + diff.getH() + " dirt higher than the range pole.";
                            }
                            else {
                                pMsg = pMsg + " Also you appear to be " + diff.getH() + " dirt lower than the range pole.";
                            }
                        }
                        else {
                            pMsg += " Also both ends are the same height, Nice!";
                        }
                        if (far.getH() <= 0 && near.getH() >= 0) {
                            if (insta) {
                                rMsg = "(" + act.getTickCount() + ") ";
                            }
                            if (pMsg.length() > 0) {
                                performer.getCommunicator().sendNormalServerMessage(rMsg + pMsg);
                            }
                            final String h = (helper != null) ? "helper is stood" : "rangepole is planted";
                            pMsg = "As your " + h + " in water, you work out how high you are above the water, and it is " + near.getH() + ".";
                            break;
                        }
                        break;
                    }
                }
                rMsg = "";
                if (insta) {
                    rMsg = "(" + act.getTickCount() + ") ";
                }
                if (pMsg.length() > 0) {
                    performer.getCommunicator().sendNormalServerMessage(rMsg + pMsg);
                }
                if (helper != null && hMsg.length() > 0) {
                    helper.getCommunicator().sendNormalServerMessage(hMsg);
                }
                if (bMsg.length() > 0) {
                    Server.getInstance().broadCastAction(bMsg, performer, 5);
                }
            }
            if (act.getTickCount() >= 6 || done) {
                performer.getCommunicator().sendNormalServerMessage("You stop surveying.");
                performer.getCommunicator().sendNormalServerMessage("You pack up the dioptra.");
                done = true;
            }
        }
        return done;
    }
    
    private static boolean isHoldingRangePole(final Creature target) {
        try {
            return ((Player)target).getCurrentAction().getNumber() == 636 && subjectItem(target).getTemplateId() == 901;
        }
        catch (NoSuchActionException e) {
            return false;
        }
        catch (NoSuchItemException e2) {
            return false;
        }
    }
    
    private static Item subjectItemOrNull(final Creature target) {
        try {
            final long subject = ((Player)target).getCurrentAction().getSubjectId();
            final Item item = Items.getItem(subject);
            return item;
        }
        catch (NoSuchActionException e) {
            return null;
        }
        catch (NoSuchItemException e2) {
            return null;
        }
    }
    
    private static Item subjectItem(final Creature target) throws NoSuchActionException, NoSuchItemException {
        final long subject = ((Player)target).getCurrentAction().getSubjectId();
        final Item item = Items.getItem(subject);
        return item;
    }
    
    private static Point getBridgeEnd(final Creature creature) {
        final byte dir = creature.getStatus().getDir();
        final Point point = new Point(creature.getTileX(), creature.getTileY());
        if (dir == 4) {
            point.setY(point.getY() + 1);
        }
        else if (dir == 2) {
            point.setX(point.getX() + 1);
        }
        final int tileH = (int)(Zones.getHeightForNode(point.getX(), point.getY(), creature.getLayer()) * 10.0f);
        final int hoff = creature.getFloorLevel() * 30 + ((creature.getFloorLevel() != 0) ? 3 : 0);
        point.setH(tileH + hoff);
        return point;
    }
    
    private static Point getBridgeEnd(final Creature creature, final Item item) {
        final byte dir = getDir(creature, item);
        final Point point = new Point(0, 0);
        int floorlevel = 0;
        if (item != null) {
            point.setX(item.getTileX());
            point.setY(item.getTileY());
            floorlevel = item.getFloorLevel();
        }
        else {
            point.setX(creature.getTileX());
            point.setY(creature.getTileY());
            floorlevel = creature.getFloorLevel();
        }
        if (dir == 4) {
            point.setY(point.getY() + 1);
        }
        else if (dir == 2) {
            point.setX(point.getX() + 1);
        }
        final int tileH = (int)(Zones.getHeightForNode(point.getX(), point.getY(), creature.getLayer()) * 10.0f);
        final int hoff = floorlevel * 30 + ((floorlevel != 0) ? 3 : 0);
        point.setH(tileH + hoff);
        return point;
    }
    
    private static byte getDir(final Creature creature, final Item item) {
        return (item != null) ? ((byte)((creature.getStatus().getDir() + 4) % 8)) : creature.getStatus().getDir();
    }
    
    private static boolean isBorderLevel(final Creature creature) {
        final Point pt = getBridgeEnd(creature);
        final byte dir = creature.getStatus().getDir();
        if (creature.getFloorLevel() == 0) {
            int tileX2 = pt.getX();
            int tileY2 = pt.getY();
            if (dir == 0 || dir == 4) {
                ++tileX2;
            }
            else if (dir == 2 || dir == 6) {
                ++tileY2;
            }
            final int ht2 = (int)(Zones.getHeightForNode(tileX2, tileY2, creature.getLayer()) * 10.0f);
            return pt.getH() == ht2;
        }
        return true;
    }
    
    private static boolean isBorderLevel(final Creature creature, final Item item) {
        final Point pt = getBridgeEnd(creature, item);
        final byte dir = creature.getStatus().getDir();
        if (creature.getFloorLevel() == 0) {
            int tileX2 = pt.getX();
            int tileY2 = pt.getY();
            if (dir == 0 || dir == 4) {
                ++tileX2;
            }
            else if (dir == 6 || dir == 2) {
                ++tileY2;
            }
            final int floorlevel = item.getFloorLevel();
            final int hoff = floorlevel * 30 + ((floorlevel != 0) ? 3 : 0);
            final int ht2 = (int)(Zones.getHeightForNode(tileX2, tileY2, creature.getLayer()) * 10.0f);
            return pt.getH() == ht2 + hoff;
        }
        return true;
    }
    
    private static float maxTilesFor(final Item item) {
        return item.getQualityLevel() * 0.4f + item.getRarity();
    }
    
    private static String dirAsString(final byte dir) {
        switch (dir) {
            case 0: {
                return "North";
            }
            case 2: {
                return "East";
            }
            case 4: {
                return "South";
            }
            case 6: {
                return "West";
            }
            default: {
                return "Unknown " + dir;
            }
        }
    }
}