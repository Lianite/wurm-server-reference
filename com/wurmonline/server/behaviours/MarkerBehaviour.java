// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.behaviours;

import com.wurmonline.server.highways.Routes;
import com.wurmonline.server.zones.Zone;
import com.wurmonline.server.skills.Skill;
import com.wurmonline.server.skills.Skills;
import com.wurmonline.server.zones.NoSuchZoneException;
import com.wurmonline.server.NoSuchItemException;
import java.util.logging.Level;
import com.wurmonline.server.zones.Zones;
import com.wurmonline.server.sounds.SoundPlayer;
import com.wurmonline.server.Server;
import com.wurmonline.server.questions.FindRouteQuestion;
import com.wurmonline.server.highways.HighwayPos;
import com.wurmonline.server.villages.Village;
import com.wurmonline.server.Items;
import com.wurmonline.server.Servers;
import com.wurmonline.server.creatures.Wagoner;
import com.wurmonline.server.creatures.Delivery;
import com.wurmonline.server.villages.Villages;
import com.wurmonline.server.highways.MethodsHighways;
import com.wurmonline.server.Features;
import java.util.LinkedList;
import javax.annotation.Nullable;
import java.util.Collection;
import java.util.List;
import com.wurmonline.server.items.Item;
import com.wurmonline.server.creatures.Creature;
import java.util.logging.Logger;
import com.wurmonline.shared.constants.HighwayConstants;

public class MarkerBehaviour extends ItemBehaviour implements HighwayConstants
{
    private static final Logger logger;
    
    public MarkerBehaviour() {
        super((short)56);
    }
    
    @Override
    public List<ActionEntry> getBehavioursFor(final Creature performer, final Item target) {
        final List<ActionEntry> toReturn = super.getBehavioursFor(performer, target);
        toReturn.addAll(this.getBehavioursForMarker(performer, null, target));
        return toReturn;
    }
    
    @Override
    public List<ActionEntry> getBehavioursFor(final Creature performer, final Item source, final Item target) {
        final List<ActionEntry> toReturn = super.getBehavioursFor(performer, source, target);
        toReturn.addAll(this.getBehavioursForMarker(performer, source, target));
        return toReturn;
    }
    
    @Override
    public boolean action(final Action act, final Creature performer, final Item target, final short action, final float counter) {
        final boolean[] ans = this.markerAction(act, performer, null, target, action, counter);
        if (ans[0]) {
            return ans[1];
        }
        return super.action(act, performer, target, action, counter);
    }
    
    @Override
    public boolean action(final Action act, final Creature performer, final Item source, final Item target, final short action, final float counter) {
        final boolean[] ans = this.markerAction(act, performer, source, target, action, counter);
        if (ans[0]) {
            return ans[1];
        }
        return super.action(act, performer, source, target, action, counter);
    }
    
    private List<ActionEntry> getBehavioursForMarker(final Creature performer, @Nullable final Item source, final Item target) {
        final List<ActionEntry> toReturn = new LinkedList<ActionEntry>();
        if (target.isRoadMarker() && target.isPlanted() && Features.Feature.HIGHWAYS.isEnabled()) {
            toReturn.add(Actions.actionEntrys[759]);
            final int linkCount = MethodsHighways.numberOfSetBits(target.getAuxData());
            if (linkCount > 0) {
                toReturn.add(Actions.actionEntrys[748]);
            }
            if (target.getTemplateId() == 1112 || linkCount < 2) {
                final List<ActionEntry> dirs = new LinkedList<ActionEntry>();
                final byte possibles = MethodsHighways.getPossibleLinksFrom(target);
                if (MethodsHighways.hasLink(possibles, (byte)1)) {
                    dirs.add(Actions.actionEntrys[749]);
                }
                if (MethodsHighways.hasLink(possibles, (byte)2)) {
                    dirs.add(Actions.actionEntrys[750]);
                }
                if (MethodsHighways.hasLink(possibles, (byte)4)) {
                    dirs.add(Actions.actionEntrys[751]);
                }
                if (MethodsHighways.hasLink(possibles, (byte)8)) {
                    dirs.add(Actions.actionEntrys[752]);
                }
                if (MethodsHighways.hasLink(possibles, (byte)16)) {
                    dirs.add(Actions.actionEntrys[753]);
                }
                if (MethodsHighways.hasLink(possibles, (byte)32)) {
                    dirs.add(Actions.actionEntrys[754]);
                }
                if (MethodsHighways.hasLink(possibles, (byte)64)) {
                    dirs.add(Actions.actionEntrys[755]);
                }
                if (MethodsHighways.hasLink(possibles, (byte)(-128))) {
                    dirs.add(Actions.actionEntrys[756]);
                }
                if (dirs.size() > 0) {
                    toReturn.add(new ActionEntry((short)(-dirs.size()), "Add Link to", "linking"));
                    toReturn.addAll(dirs);
                }
            }
            if (target.getTemplateId() == 1112) {
                toReturn.add(Actions.actionEntrys[758]);
                if (Features.Feature.WAGONER.isEnabled()) {
                    final Village waystoneVillage = Villages.getVillage(target.getTilePos(), target.isOnSurface());
                    if (waystoneVillage != null && (waystoneVillage.isActionAllowed((short)85, performer) || (performer.getPower() >= 4 && waystoneVillage.isPermanent)) && source != null && source.getTemplateId() == 1129 && source.getData() == -1L && !target.isWagonerCamp() && MethodsHighways.numberOfSetBits(target.getAuxData()) == 1) {
                        toReturn.add(new ActionEntry((short)863, "Set up wagoner camp", "setting up wagoner camp"));
                    }
                    if (waystoneVillage != null && (waystoneVillage.isActionAllowed((short)176, performer) || (performer.getPower() >= 4 && waystoneVillage.isPermanent))) {
                        if (source != null && source.getTemplateId() == 1309 && !target.isWagonerCamp()) {
                            toReturn.add(new ActionEntry((short)176, "Plant", "planting"));
                        }
                    }
                    else if (waystoneVillage == null && source != null && source.getTemplateId() == 1309 && !target.isWagonerCamp()) {
                        final Village per = Villages.getVillageWithPerimeterAt(target.getTileX(), target.getTileY(), target.isOnSurface());
                        if (per == null || per.isActionAllowed((short)176, performer)) {
                            toReturn.add(new ActionEntry((short)176, "Plant", "planting"));
                        }
                    }
                    if (Delivery.getWaitingDeliveries(performer.getWurmId()).length > 0) {
                        toReturn.add(new ActionEntry((short)915, "Accept delivery", "accepting"));
                    }
                    if (target.isWagonerCamp()) {
                        final Wagoner wagoner = Wagoner.getWagoner(target.getData());
                        if (wagoner != null && wagoner.getVillageId() != -1 && (wagoner.getOwnerId() == performer.getWurmId() || performer.getPower() > 1)) {
                            final List<ActionEntry> waglist = new LinkedList<ActionEntry>();
                            waglist.add(new ActionEntry((short)(-2), "Permissions", "viewing"));
                            waglist.add(Actions.actionEntrys[863]);
                            waglist.add(new ActionEntry((short)691, "History Of Wagoner", "viewing"));
                            waglist.add(Actions.actionEntrys[919]);
                            waglist.add(new ActionEntry((short)566, "Manage chat options", "managing"));
                            toReturn.add(new ActionEntry((short)(-(waglist.size() - 2)), wagoner.getName(), "wagoner"));
                            toReturn.addAll(waglist);
                        }
                    }
                    if (source != null && source.getTemplateId() == 1129 && source.getData() != -1L && Servers.isThisATestServer()) {
                        final Wagoner wagoner = Wagoner.getWagoner(source.getData());
                        if (wagoner != null && wagoner.getVillageId() != -1) {
                            final List<ActionEntry> testlist = new LinkedList<ActionEntry>();
                            if (wagoner.getState() == 0 && target.getData() == source.getData()) {
                                testlist.add(new ActionEntry((short)140, "Send to bed", "testing"));
                                testlist.add(new ActionEntry((short)111, "Test delivery", "testing"));
                            }
                            if (wagoner.getState() == 2 && target.getData() == source.getData()) {
                                testlist.add(new ActionEntry((short)30, "Wake up", "testing"));
                            }
                            if (wagoner.getState() == 14) {
                                testlist.add(new ActionEntry((short)682, "Drive to here", "commanding"));
                                if (target.getData() == source.getData()) {
                                    testlist.add(new ActionEntry((short)644, "Force Park", "parking"));
                                }
                                testlist.add(new ActionEntry((short)636, "Go Home", "parking"));
                            }
                            else if (wagoner.getState() == 15) {
                                testlist.add(new ActionEntry((short)917, "Cancel driving", "cancelling"));
                            }
                            testlist.add(new ActionEntry((short)185, "Show state", "checking"));
                            if (!testlist.isEmpty()) {
                                toReturn.add(new ActionEntry((short)(-testlist.size()), "Test only", "testing"));
                                toReturn.addAll(testlist);
                            }
                        }
                    }
                }
            }
            if (source != null) {
                if (source.isRoadMarker()) {
                    if (target.getTemplateId() == 1114 && source.getTemplateId() == 1112) {
                        toReturn.add(Actions.actionEntrys[78]);
                    }
                    else if (target.getTemplateId() == 1112 && MethodsHighways.numberOfSetBits(target.getAuxData()) <= 2 && source.getTemplateId() == 1114 && target.getLastOwnerId() == performer.getWurmId() && !target.isWagonerCamp() && !Items.isWaystoneInUse(target.getWurmId())) {
                        toReturn.add(Actions.actionEntrys[78]);
                    }
                }
                else if (!target.isWagonerCamp() && performer.mayDestroy(target) && !target.isIndestructible() && !Items.isWaystoneInUse(target.getWurmId()) && !MethodsHighways.isNextToACamp(MethodsHighways.getHighwayPos(target))) {
                    toReturn.add(new ActionEntry((short)(-1), "Bash", "Bash"));
                    if (source.getTemplateId() == 1115) {
                        toReturn.add(Actions.actionEntrys[757]);
                    }
                    else {
                        toReturn.add(new ActionEntry((short)83, "Destroy", "Destroying", new int[] { 5, 4, 43 }));
                    }
                }
            }
        }
        return toReturn;
    }
    
    public boolean[] markerAction(final Action act, final Creature performer, @Nullable final Item source, final Item target, final short action, final float counter) {
        if (target.isRoadMarker() && target.isPlanted() && Features.Feature.HIGHWAYS.isEnabled()) {
            switch (action) {
                case 759: {
                    return new boolean[] { true, showProtection(performer, target, act, counter, null) };
                }
                case 748: {
                    return new boolean[] { true, showLinks(performer, target, act, counter, null) };
                }
                case 749: {
                    return new boolean[] { true, this.setLink(performer, target, (byte)1, (byte)16) };
                }
                case 750: {
                    return new boolean[] { true, this.setLink(performer, target, (byte)2, (byte)32) };
                }
                case 751: {
                    return new boolean[] { true, this.setLink(performer, target, (byte)4, (byte)64) };
                }
                case 752: {
                    return new boolean[] { true, this.setLink(performer, target, (byte)8, (byte)(-128)) };
                }
                case 753: {
                    return new boolean[] { true, this.setLink(performer, target, (byte)16, (byte)1) };
                }
                case 754: {
                    return new boolean[] { true, this.setLink(performer, target, (byte)32, (byte)2) };
                }
                case 755: {
                    return new boolean[] { true, this.setLink(performer, target, (byte)64, (byte)4) };
                }
                case 756: {
                    return new boolean[] { true, this.setLink(performer, target, (byte)(-128), (byte)8) };
                }
                case 758: {
                    final FindRouteQuestion frq = new FindRouteQuestion(performer, target);
                    frq.sendQuestion();
                    return new boolean[] { true, true };
                }
                case 78: {
                    if (source != null && source.isRoadMarker()) {
                        if (target.getTemplateId() == 1114 && source.getTemplateId() == 1112) {
                            return new boolean[] { true, replace(act, performer, source, target, action, counter) };
                        }
                        if (target.getTemplateId() == 1112 && MethodsHighways.numberOfSetBits(target.getAuxData()) <= 2 && source.getTemplateId() == 1114 && target.getLastOwnerId() == performer.getWurmId()) {
                            return new boolean[] { true, replace(act, performer, source, target, action, counter) };
                        }
                    }
                    return new boolean[] { true, true };
                }
                case 757: {
                    if (source == null || source.getTemplateId() != 1115) {
                        return new boolean[] { true, true };
                    }
                    if (performer.mayDestroy(target) && !target.isIndestructible()) {
                        return new boolean[] { true, MethodsItems.destroyItem(action, performer, source, target, false, counter) };
                    }
                    return new boolean[] { true, true };
                }
            }
        }
        return new boolean[] { false, false };
    }
    
    private static boolean replace(final Action act, final Creature performer, final Item source, final Item target, final short action, final float counter) {
        if (MethodsItems.cannotPlant(performer, source)) {
            return true;
        }
        int time = 200;
        final Skills skills = performer.getSkills();
        final Skill primSkill = skills.getSkillOrLearn(10031);
        if (counter == 1.0f) {
            if (primSkill.getRealKnowledge() < 21.0) {
                performer.getCommunicator().sendNormalServerMessage("Not enough skill to replace the " + target.getName() + ".", (byte)3);
                return true;
            }
            if (!Methods.isActionAllowed(performer, (short)176, target.getTileX(), target.getTileY())) {
                return true;
            }
            if (!performer.canCarry(target.getFullWeight())) {
                performer.getCommunicator().sendNormalServerMessage("You would not be able to carry the " + target.getName() + " as well.", (byte)3);
                return true;
            }
            time = Actions.getStandardActionTime(performer, primSkill, source, 0.0);
            act.setTimeLeft(time);
            performer.getCommunicator().sendNormalServerMessage("You start to replace the " + target.getName() + ".");
            Server.getInstance().broadCastAction(performer.getName() + " starts to replace the " + target.getName() + ".", performer, 5);
            performer.sendActionControl("Replacing " + target.getName(), true, time);
            performer.getStatus().modifyStamina(-1000.0f);
        }
        else {
            time = act.getTimeLeft();
        }
        if (act.mayPlaySound()) {
            SoundPlayer.playSound("sound.work.stonecutting", performer, 1.0f);
        }
        if (act.currentSecond() == 5) {
            performer.getStatus().modifyStamina(-1000.0f);
        }
        if (counter * 10.0f > time) {
            final byte oldlinks = target.getAuxData();
            try {
                target.setReplacing(true);
                target.setWhatHappened("replaced");
                target.setIsPlanted(false);
                source.setIsPlanted(true);
                source.putItemInCorner(performer, target.getTileX(), target.getTileY(), target.isOnSurface(), target.getBridgeId(), false);
                MethodsHighways.autoLink(source, oldlinks);
                final Zone zone = Zones.getZone(target.getTileX(), target.getTileY(), target.isOnSurface());
                zone.removeItem(target);
                performer.getInventory().insertItem(target, false);
            }
            catch (NoSuchItemException nsie) {
                source.setIsPlanted(false);
                performer.getCommunicator().sendNormalServerMessage("You fail to replace the " + target.getName() + ". Something is weird.");
                MarkerBehaviour.logger.log(Level.WARNING, performer.getName() + ": " + nsie.getMessage(), nsie);
            }
            catch (NoSuchZoneException nsze) {
                source.setIsPlanted(false);
                performer.getCommunicator().sendNormalServerMessage("You fail to replace the " + target.getName() + ". Something is weird.");
                MarkerBehaviour.logger.log(Level.WARNING, performer.getName() + ": " + nsze.getMessage(), nsze);
            }
            performer.getCommunicator().sendNormalServerMessage("You replaced the " + target.getName() + " with a " + source.getName() + ".");
            Server.getInstance().broadCastAction(performer.getName() + " replaced the " + target.getName() + " with a " + source.getName() + ".", performer, 5);
            return true;
        }
        return false;
    }
    
    static final boolean showLinks(final Creature performer, final Item marker, final Action act, final float counter, @Nullable final HighwayPos highwayPos) {
        boolean done = false;
        if (act.currentSecond() == 1) {
            final String linktypeString = (highwayPos == null) ? "link" : "possible links";
            performer.getCommunicator().sendNormalServerMessage("You start viewing the " + linktypeString + ".");
            performer.sendActionControl(Actions.actionEntrys[748].getVerbString(), true, 300);
            if (highwayPos == null) {
                done = !MethodsHighways.viewLinks(performer, marker);
            }
            else {
                done = !MethodsHighways.viewLinks(performer, highwayPos, marker);
            }
            if (done) {
                performer.getCommunicator().sendNormalServerMessage("Problem viewing the " + linktypeString + ".");
            }
        }
        else if (act.currentSecond() >= 30) {
            done = true;
            final String linktypeString = (highwayPos == null) ? "link" : "possible links";
            performer.getCommunicator().sendNormalServerMessage("You stop viewing the " + linktypeString + ".");
        }
        return done;
    }
    
    static final boolean showProtection(final Creature performer, final Item marker, final Action act, final float counter, @Nullable final HighwayPos highwayPos) {
        boolean done = false;
        if (act.currentSecond() == 1) {
            final String linktypeString = (highwayPos == null) ? "protection" : "possible protection";
            performer.getCommunicator().sendNormalServerMessage("You start viewing the " + linktypeString + ".");
            performer.sendActionControl(Actions.actionEntrys[759].getVerbString(), true, 300);
            if (highwayPos == null) {
                done = !MethodsHighways.viewProtection(performer, marker);
            }
            else {
                done = !MethodsHighways.viewProtection(performer, highwayPos, marker);
            }
            if (done) {
                performer.getCommunicator().sendNormalServerMessage("Problem viewing the " + linktypeString + ".");
            }
        }
        else if (act.currentSecond() >= 30) {
            done = true;
            final String linktypeString = (highwayPos == null) ? "protection" : "possible protection";
            performer.getCommunicator().sendNormalServerMessage("You stop viewing the " + linktypeString + ".");
        }
        return done;
    }
    
    private boolean setLink(final Creature performer, final Item target, final byte linkDir, final byte oppositeDir) {
        final Item marker = MethodsHighways.getMarker(target, linkDir);
        if (marker != null) {
            target.setAuxData((byte)(target.getAuxData() | linkDir));
            marker.setAuxData((byte)(marker.getAuxData() | oppositeDir));
            Routes.checkForNewRoutes(marker);
            target.updateModelNameOnGroundItem();
            marker.updateModelNameOnGroundItem();
        }
        else {
            performer.getCommunicator().sendNormalServerMessage("No marker found in " + MethodsHighways.getLinkDirString(linkDir) + " direction.");
        }
        return true;
    }
    
    static {
        logger = Logger.getLogger(MarkerBehaviour.class.getName());
    }
}
