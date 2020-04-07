// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.behaviours;

import com.wurmonline.shared.exceptions.WurmServerException;
import com.wurmonline.server.items.ItemTemplate;
import java.util.Iterator;
import com.wurmonline.server.structures.Floor;
import com.wurmonline.shared.constants.StructureConstants;
import java.util.LinkedList;
import java.util.List;
import com.wurmonline.server.skills.NoSuchSkillException;
import com.wurmonline.mesh.Tiles;
import com.wurmonline.server.structures.Structure;
import com.wurmonline.server.players.PlayerInfo;
import com.wurmonline.server.structures.BlockingResult;
import com.wurmonline.server.structures.Blocking;
import com.wurmonline.server.GeneralUtilities;
import com.wurmonline.server.items.NoSuchTemplateException;
import com.wurmonline.server.FailedException;
import java.io.IOException;
import com.wurmonline.server.creatures.DbCreatureStatus;
import com.wurmonline.server.items.ItemFactory;
import com.wurmonline.server.villages.Reputation;
import com.wurmonline.server.villages.Village;
import com.wurmonline.server.skills.Skill;
import com.wurmonline.server.zones.VolaTile;
import com.wurmonline.server.zones.Zone;
import com.wurmonline.server.WurmCalendar;
import com.wurmonline.server.Server;
import com.wurmonline.server.utils.StringUtil;
import com.wurmonline.server.zones.NoSuchZoneException;
import com.wurmonline.server.zones.Zones;
import com.wurmonline.server.players.PlayerInfoFactory;
import com.wurmonline.server.creatures.NoSuchCreatureException;
import com.wurmonline.server.creatures.Creatures;
import com.wurmonline.server.NoSuchItemException;
import com.wurmonline.server.Items;
import java.util.logging.Level;
import com.wurmonline.server.items.Item;
import com.wurmonline.server.creatures.Creature;
import java.util.logging.Logger;
import com.wurmonline.server.creatures.CreatureTemplateIds;
import com.wurmonline.server.MiscConstants;

public final class CargoTransportationMethods implements MiscConstants, CreatureTemplateIds
{
    private static final Logger logger;
    private static final double LOAD_STRENGTH_NEEDED = 23.0;
    
    public static final boolean loadCargo(final Creature performer, final Item target, final float counter) {
        Action act = null;
        try {
            act = performer.getCurrentAction();
        }
        catch (NoSuchActionException nsa) {
            CargoTransportationMethods.logger.log(Level.WARNING, "Unable to get current action in loadCargo().", nsa);
            return true;
        }
        for (final Item item : target.getAllItems(true)) {
            if (item.getTemplateId() == 1436 && !item.isEmpty(true)) {
                performer.getCommunicator().sendNormalServerMessage("The " + target.getName() + " has chickens in it, remove them first.");
                return true;
            }
        }
        if (target.getTemplateId() == 1311) {
            try {
                final Item vehicle = Items.getItem(performer.getVehicle());
                if (vehicle.getTemplateId() == 491) {
                    performer.getCommunicator().sendNormalServerMessage("The " + target.getName() + " will not fit in the " + vehicle.getName() + ".");
                    return true;
                }
                if (vehicle.getTemplateId() == 490) {
                    performer.getCommunicator().sendNormalServerMessage("The " + target.getName() + " will not fit in the " + vehicle.getName() + ".");
                    return true;
                }
            }
            catch (NoSuchItemException ex) {
                CargoTransportationMethods.logger.log(Level.WARNING, "NoSuchItemException: " + String.valueOf(ex));
                ex.printStackTrace();
            }
            if (target.getTemplateId() == 1311 && !target.isEmpty(true)) {
                for (final Item item : target.getAllItems(true)) {
                    try {
                        final Creature getCreature = Creatures.getInstance().getCreature(item.getData());
                        if (getCreature.getDominator() != null && getCreature.getDominator() != performer) {
                            performer.getCommunicator().sendNormalServerMessage("You cannot load this cage, the creature inside is not tamed by you.");
                            return true;
                        }
                    }
                    catch (NoSuchCreatureException ex2) {
                        CargoTransportationMethods.logger.log(Level.WARNING, ex2.getMessage(), ex2);
                    }
                }
            }
            try {
                final Item vehicle2 = Items.getItem(performer.getVehicle());
                final int theVessel = vehicle2.getTemplateId();
                int max = 0;
                switch (theVessel) {
                    case 541: {
                        max = 5;
                        break;
                    }
                    case 540: {
                        max = 6;
                        break;
                    }
                    case 542: {
                        max = 4;
                        break;
                    }
                    case 543: {
                        max = 8;
                        break;
                    }
                    case 850: {
                        max = 2;
                        break;
                    }
                    case 1410: {
                        max = 4;
                        break;
                    }
                    default: {
                        max = 0;
                        break;
                    }
                }
                if (vehicle2.getInsertItem() != null && vehicle2.getInsertItem().getNumberCages() >= max) {
                    performer.getCommunicator().sendNormalServerMessage("The " + target.getName() + " will not fit in the " + vehicle2.getName() + ".");
                    return true;
                }
            }
            catch (NoSuchItemException ex3) {
                CargoTransportationMethods.logger.log(Level.WARNING, ex3.getMessage(), ex3);
            }
        }
        if (isLargeMagicChest(target) && !performerIsLastOwner(performer, target)) {
            return true;
        }
        if (isAutoRefillWell(target)) {
            performer.getCommunicator().sendNormalServerMessage("This is a special version of the item that is designed to exist only on starter deeds, and is therefor not transportable.");
            return true;
        }
        if (targetIsNotTransportable(target, performer)) {
            return true;
        }
        if (targetIsNotOnTheGround(target, performer, false)) {
            return true;
        }
        if (targetIsDraggedCheck(target, performer)) {
            return true;
        }
        if (targetIsPlantedCheck(target, performer)) {
            return true;
        }
        if (targetIsOccupiedBed(target, performer, act.getNumber())) {
            return true;
        }
        if (performerIsNotOnAVehicle(performer)) {
            return true;
        }
        final Vehicle vehicle3 = Vehicles.getVehicleForId(performer.getVehicle());
        if (performerIsNotOnATransportVehicle(performer, vehicle3)) {
            return true;
        }
        if (targetIsSameAsCarrier(performer, vehicle3, target)) {
            return true;
        }
        if (targetIsHitchedOrCommanded(target, performer)) {
            return true;
        }
        final Seat seat = vehicle3.getSeatFor(performer.getWurmId());
        if (performerIsNotSeatedOnAVehicle(performer, seat)) {
            return true;
        }
        if (perfomerActionTargetIsBlocked(performer, target)) {
            return true;
        }
        final Item carrier = getCarrierItem(vehicle3, performer);
        if (carrier == null) {
            return true;
        }
        final int distance = getLoadActionDistance(carrier);
        if (!performerIsWithinDistanceToTarget(performer, target, distance)) {
            return true;
        }
        if (!target.isVehicle()) {
            if (targetIsLockedCheck(target, performer, vehicle3)) {
                return true;
            }
        }
        else {
            if (target.isOwnedByWagoner()) {
                performer.getCommunicator().sendNormalServerMessage("The " + target.getName() + " is owned by a wagoner, and will not allow that to be loaded.");
                return true;
            }
            if (targetVehicleIsLockedCheck(target, performer, vehicle3)) {
                return true;
            }
        }
        if (performerMayNotUseInventory(performer, carrier)) {
            return true;
        }
        if (targetIsLoadedWarmachine(target, performer, carrier)) {
            return true;
        }
        if (targetIsNotEmptyContainerCheck(target, performer, carrier, true)) {
            return true;
        }
        if (targetIsOnFireCheck(target, performer, carrier)) {
            return true;
        }
        if (targetHasActiveQueen(target, performer, carrier, true)) {
            return true;
        }
        if (target.getTemplateId() != 1311 && targetCanNotBeInsertedCheck(target, carrier, vehicle3, performer)) {
            return true;
        }
        if (!isOnSameLevelLoad(target, performer)) {
            return true;
        }
        if (!Methods.isActionAllowed(performer, (short)605)) {
            return true;
        }
        if (target.isPlanted() && !Methods.isActionAllowed(performer, (short)685)) {
            return true;
        }
        if (target.isCrate() && target.isSealedByPlayer() && target.getLastOwnerId() != performer.getWurmId()) {
            final String pname = PlayerInfoFactory.getPlayerName(target.getLastOwnerId());
            performer.getCommunicator().sendNormalServerMessage("The " + target.getName() + " has a security seal on it, and may only be loaded by " + pname + ".");
            return true;
        }
        if (target.isMarketStall()) {
            final int tilex = target.getTileX();
            final int tiley = target.getTileY();
            try {
                final Zone zone = Zones.getZone(tilex, tiley, target.isOnSurface());
                final VolaTile t = zone.getOrCreateTile(tilex, tiley);
                if (t != null && t.getCreatures().length > 0) {
                    for (final Creature cret : t.getCreatures()) {
                        if (cret.isNpcTrader()) {
                            performer.getCommunicator().sendSafeServerMessage("This stall is currently in use.");
                            return true;
                        }
                    }
                }
            }
            catch (NoSuchZoneException nsz2) {
                CargoTransportationMethods.logger.warning(String.format("Could not find zone at tile [%s, %s] for item %s.", tilex, tiley, target.getName()));
            }
        }
        int time = Actions.getLoadUnloadActionTime(performer);
        if (counter == 1.0f) {
            if (!strengthCheck(performer, 23.0)) {
                final String message = StringUtil.format("You are not strong enough to do this, you need at least %.1f body strength.", 23.0);
                performer.getCommunicator().sendNormalServerMessage(message);
                return true;
            }
            act.setTimeLeft(time);
            performer.getCommunicator().sendNormalServerMessage("You start to load the " + target.getName() + ".");
            Server.getInstance().broadCastAction(performer.getName() + " starts to load the " + target.getName() + ".", performer, 5);
            performer.sendActionControl(Actions.actionEntrys[605].getVerbString(), true, time);
            performer.getStatus().modifyStamina(-10000.0f);
            return false;
        }
        else {
            time = act.getTimeLeft();
            if (act.currentSecond() % 5 == 0) {
                performer.getStatus().modifyStamina(-10000.0f);
            }
            if (act.currentSecond() == 3 && target.isLocked() && (target.isGuest(-30L) || target.isGuest(-20L) || target.isGuest(-40L))) {
                performer.getCommunicator().sendServerMessage("WARNING - " + target.getName() + " has Group permissions, this WILL cause problems when crossing servers!", 255, 127, 63);
            }
            if (counter * 10.0f <= time) {
                return false;
            }
            if (target.getTemplateId() != 1311 && targetCanNotBeInsertedCheck(target, carrier, vehicle3, performer)) {
                return true;
            }
            final boolean isStealing = MethodsItems.checkIfStealing(target, performer, act);
            if (performerIsTryingToStealInLawfulMode(performer, isStealing)) {
                return true;
            }
            Creature[] watchers = null;
            try {
                watchers = target.getWatchers();
            }
            catch (Exception ex4) {}
            if (isStealing) {
                if (performerMayNotStealCheck(performer)) {
                    return true;
                }
                Skill stealing = null;
                boolean dryRun = false;
                final Village v = Zones.getVillage(target.getTileX(), target.getTileY(), true);
                if (v != null) {
                    final Reputation rep = v.getReputationObject(performer.getWurmId());
                    if (rep != null && rep.getValue() >= 0 && rep.isPermanent()) {
                        dryRun = true;
                    }
                }
                if (MethodsItems.setTheftEffects(performer, act, target)) {
                    stealing = performer.getStealSkill();
                    stealing.skillCheck(target.getQualityLevel(), 0.0, dryRun, 10.0f);
                    return true;
                }
                stealing = performer.getStealSkill();
                stealing.skillCheck(target.getQualityLevel(), 0.0, dryRun, 10.0f);
            }
            try {
                if (targetIsNotOnTheGround(target, performer, true)) {
                    return true;
                }
                final Zone zone2 = Zones.getZone(target.getTileX(), target.getTileY(), target.isOnSurface());
                zone2.removeItem(target);
                if (shouldRemovePlantedFlag(target)) {
                    target.setIsPlanted(false);
                }
                carrier.insertItem(target, true);
                if (carrier.getTemplateId() == 1410) {
                    updateItemModel(carrier);
                }
                if (watchers != null) {
                    for (final Creature c : watchers) {
                        c.getCommunicator().sendCloseInventoryWindow(target.getWurmId());
                    }
                }
                if (target.isCrate() && target.isSealedByPlayer() && target.getLastOwnerId() == performer.getWurmId()) {
                    performer.getCommunicator().sendNormalServerMessage("You accidently knock off the security seal.");
                    target.setIsSealedByPlayer(false);
                }
                performer.getCommunicator().sendNormalServerMessage("You finish loading the " + target.getName() + ".");
                Server.getInstance().broadCastAction(performer.getName() + " finishes loading the " + target.getName() + ".", performer, 5);
                target.setLastMaintained(WurmCalendar.currentTime);
            }
            catch (NoSuchZoneException nsz) {
                final String message2 = StringUtil.format("Unable to find zone for x: %d y: %d surface: %s in loadCargo().", target.getTileX(), target.getTileY(), Boolean.toString(target.isOnSurface()));
                CargoTransportationMethods.logger.log(Level.WARNING, message2, nsz);
            }
            return true;
        }
    }
    
    static boolean loadCreature(final Creature performer, final Item target, final float counter) {
        final Creature[] folls = performer.getFollowers();
        Action act;
        try {
            act = performer.getCurrentAction();
        }
        catch (NoSuchActionException nsa) {
            CargoTransportationMethods.logger.log(Level.WARNING, "Unable to get current action in loadCreature().", nsa);
            return true;
        }
        if (!target.isEmpty(true)) {
            performer.getCommunicator().sendNormalServerMessage("There is already a creature in this " + target.getName() + ".", (byte)3);
            return true;
        }
        for (final Creature foll : folls) {
            if (foll.getStatus().getBody().isWounded()) {
                performer.getCommunicator().sendNormalServerMessage("The creature whimpers in pain, and refuses to enter the cage.");
                return true;
            }
            if (foll.getBody().getAllItems().length > 0) {
                performer.getCommunicator().sendNormalServerMessage("You cannot load the creature with items on it, remove them first.");
                return true;
            }
        }
        if (folls.length > 1) {
            performer.getCommunicator().sendNormalServerMessage("You are currently leading too many creatures.");
            return true;
        }
        if (target.getParentId() != -10L) {
            performer.getCommunicator().sendNormalServerMessage("You must unload the cage to load creatures into it.");
            return true;
        }
        if (target.isLocked() && !target.mayAccessHold(performer)) {
            performer.getCommunicator().sendNormalServerMessage("You are not allowed to load creatures into this cage. The target is locked.");
            return true;
        }
        if (target.getCurrentQualityLevel() < 10.0f) {
            performer.getCommunicator().sendNormalServerMessage("The cage is in too poor of shape to be used.");
            return true;
        }
        final int time = Actions.getLoadUnloadActionTime(performer);
        if (counter == 1.0f) {
            for (final Creature foll2 : folls) {
                if (foll2.isPregnant()) {
                    performer.getCommunicator().sendNormalServerMessage("You feel terrible caging the unborn offspring of " + foll2.getNameWithGenus() + ", and decide not to.");
                    return true;
                }
                if (!performer.isWithinDistanceTo(foll2, 5.0f)) {
                    performer.getCommunicator().sendNormalServerMessage("You are too far away from the creature.");
                    return true;
                }
                if (!foll2.isWithinDistanceTo(target, 5.0f)) {
                    performer.getCommunicator().sendNormalServerMessage("The creature is too far away from the cage.");
                    return true;
                }
                if (!performer.isWithinDistanceTo(target, 5.0f)) {
                    performer.getCommunicator().sendNormalServerMessage("You are too far away from the cage.");
                    return true;
                }
                act.setTimeLeft(time);
                performer.getCommunicator().sendNormalServerMessage("You start to load the " + foll2.getName() + ".");
                Server.getInstance().broadCastAction(performer.getName() + " starts to load the " + foll2.getName() + ".", performer, 5);
                performer.sendActionControl(Actions.actionEntrys[907].getVerbString(), true, time);
                performer.getStatus().modifyStamina(-10000.0f);
            }
            return false;
        }
        if (counter * 10.0f > time) {
            try {
                for (final Creature foll2 : folls) {
                    if (!foll2.getInventory().isEmpty(true)) {
                        for (final Item item : foll2.getInventory().getAllItems(true)) {
                            Items.destroyItem(item.getWurmId());
                        }
                    }
                    final Item insertTarget = target.getInsertItem();
                    final Item i = ItemFactory.createItem(1310, foll2.getStatus().age, (byte)0, null);
                    if (insertTarget != null) {
                        insertTarget.insertItem(i, true);
                        i.setData(foll2.getWurmId());
                        i.setName(foll2.getName());
                        i.setWeight((int)foll2.getWeight(), false);
                    }
                    else {
                        CargoTransportationMethods.logger.log(Level.WARNING, "" + performer.getName() + " caused Null pointer.");
                    }
                    DbCreatureStatus.setLoaded(1, foll2.getWurmId());
                    target.setAuxData((byte)foll2.getTemplate().getTemplateId());
                    foll2.setLeader(null);
                    foll2.getCurrentTile().deleteCreature(foll2);
                    foll2.savePosition(-10);
                    foll2.destroyVisionArea();
                    foll2.getStatus().setDead(true);
                    target.setName("creature cage [" + foll2.getName() + "]");
                    updateItemModel(target);
                    target.setData(System.currentTimeMillis());
                }
            }
            catch (IOException | FailedException | NoSuchTemplateException ex3) {
                final Exception ex2;
                final Exception ex = ex2;
                CargoTransportationMethods.logger.log(Level.WARNING, ex.getMessage(), ex);
            }
            for (final Creature foll2 : folls) {
                if (foll2.getStatus().getAgeString().equals("venerable")) {
                    performer.getCommunicator().sendNormalServerMessage("The " + foll2.getName() + " is " + "venerable" + ", and may die if it crosses to another server.", (byte)3);
                }
                performer.getCommunicator().sendNormalServerMessage("You finish loading the " + foll2.getName() + ".");
                Server.getInstance().broadCastAction(performer.getName() + " finishes loading the " + foll2.getName() + ".", performer, 5);
            }
            return true;
        }
        return false;
    }
    
    static boolean unloadCreature(final Creature performer, final Item target, final float counter) {
        Action act;
        try {
            act = performer.getCurrentAction();
        }
        catch (NoSuchActionException nsa) {
            CargoTransportationMethods.logger.log(Level.WARNING, "Unable to get current action in unloadCreature().", nsa);
            return true;
        }
        if (!Methods.isActionAllowed(performer, (short)234)) {
            return true;
        }
        try {
            final Creature getCreature = Creatures.getInstance().getCreature(target.getData());
            if (getCreature.getDominator() != null && getCreature.getDominator() != performer) {
                performer.getCommunicator().sendNormalServerMessage("You cannot unload this creature, it is not your pet.");
                return true;
            }
        }
        catch (NoSuchCreatureException ex) {
            CargoTransportationMethods.logger.log(Level.WARNING, ex.getMessage(), ex);
        }
        try {
            if (target.getParent().getParentId() != -10L) {
                performer.getCommunicator().sendNormalServerMessage("You must unload the cage to unload the creature within.");
                return true;
            }
        }
        catch (NoSuchItemException ex2) {
            CargoTransportationMethods.logger.log(Level.WARNING, ex2.getMessage(), ex2);
        }
        if (target.getData() == -10L) {
            performer.getCommunicator().sendNormalServerMessage("The loaded creature has no data, return to the origin server and re-load it.");
            return true;
        }
        final int time = Actions.getLoadUnloadActionTime(performer);
        if (counter == 1.0f) {
            act.setTimeLeft(time);
            performer.getCommunicator().sendNormalServerMessage("You start to unload the " + target.getName() + ".");
            Server.getInstance().broadCastAction(performer.getName() + " starts to unload the " + target.getName() + ".", performer, 5);
            performer.sendActionControl(Actions.actionEntrys[908].getVerbString(), true, time);
            performer.getStatus().modifyStamina(-10000.0f);
            return false;
        }
        if (counter * 10.0f > time) {
            try {
                target.getParent().setName("creature cage [Empty]");
                final Creature getCreature2 = Creatures.getInstance().getCreature(target.getData());
                final Creatures cstat = Creatures.getInstance();
                getCreature2.getStatus().setDead(false);
                cstat.removeCreature(getCreature2);
                cstat.addCreature(getCreature2, false);
                getCreature2.putInWorld();
                CreatureBehaviour.blinkTo(getCreature2, performer.getPosX(), performer.getPosY(), performer.getLayer(), performer.getPositionZ(), performer.getBridgeId(), performer.getFloorLevel());
                target.getParent().setDamage((float)(target.getParent().getDamage() + getCreature2.getSkills().getSkill(102).getKnowledge(0.0) / target.getParent().getQualityLevel()));
                target.getParent().setAuxData((byte)0);
                updateItemModel(target.getParent());
                performer.getCommunicator().sendNormalServerMessage("Creature unloaded.");
                performer.getCommunicator().sendNormalServerMessage("The creature damages the cage from anger.");
                getCreature2.save();
                getCreature2.savePosition(target.getZoneId());
                Items.destroyItem(target.getWurmId());
                target.setLastOwnerId(performer.getWurmId());
                DbCreatureStatus.setLoaded(0, getCreature2.getWurmId());
            }
            catch (Exception ex3) {
                CargoTransportationMethods.logger.log(Level.WARNING, ex3.getMessage(), ex3);
            }
            performer.getCommunicator().sendNormalServerMessage("You finish unloading the " + target.getName() + ".");
            Server.getInstance().broadCastAction(performer.getName() + " finishes unloading the " + target.getName() + ".", performer, 5);
            return true;
        }
        return false;
    }
    
    private static final boolean isOnSameLevelLoad(final Item target, final Creature player) {
        if (!GeneralUtilities.isOnSameLevel(player, target)) {
            final String m = StringUtil.format("You must be on the same floor level as the %s in order to load it.", target.getName());
            player.getCommunicator().sendNormalServerMessage(m);
            return false;
        }
        return true;
    }
    
    private static final boolean isOnSameLevelUnload(final Item target, final Creature player) {
        if (!GeneralUtilities.isOnSameLevel(player, target)) {
            final String m = StringUtil.format("You must be on the same floor level as the %s in order to unload items from it.", target.getName());
            player.getCommunicator().sendNormalServerMessage(m);
            return false;
        }
        return true;
    }
    
    public static final boolean shouldRemovePlantedFlag(final Item target) {
        return target.isPlanted();
    }
    
    public static final boolean loadShip(final Creature performer, final Item target, final float counter) {
        Action act = null;
        try {
            act = performer.getCurrentAction();
        }
        catch (NoSuchActionException nsa) {
            CargoTransportationMethods.logger.log(Level.WARNING, "Unable to get current action in loadShip().", nsa);
            return true;
        }
        if (targetIsMoored(target, performer)) {
            return true;
        }
        if (targetIsDraggedCheck(target, performer)) {
            return true;
        }
        if (targetIsNotOnTheGround(target, performer, false)) {
            return true;
        }
        final Vehicle vehicle = Vehicles.getVehicleForId(performer.getVehicle());
        if (performerIsNotOnATransportVehicle(performer, vehicle)) {
            return true;
        }
        final Seat seat = vehicle.getSeatFor(performer.getWurmId());
        if (performerIsNotSeatedOnAVehicle(performer, seat)) {
            return true;
        }
        if (perfomerActionTargetIsBlocked(performer, target)) {
            return true;
        }
        if (targetVehicleIsLockedCheck(target, performer, vehicle)) {
            return true;
        }
        final Item carrier = getCarrierItem(vehicle, performer);
        if (carrier == null) {
            return true;
        }
        if (performerMayNotUseInventory(performer, carrier)) {
            return true;
        }
        if (carrier.getItems().size() > 0) {
            performer.getCommunicator().sendNormalServerMessage(StringUtil.format("The %s is full.", carrier.getName()));
            return true;
        }
        if (targetIsHitchedOrCommanded(target, performer)) {
            return true;
        }
        if (targetIsNotEmptyContainerCheck(target, performer, carrier, true)) {
            return true;
        }
        if (!Methods.isActionAllowed(performer, (short)605)) {
            return true;
        }
        int time = Actions.getLoadUnloadActionTime(performer);
        if (counter == 1.0f) {
            float modifier = target.getFullWeight(true) / target.getTemplate().getWeightGrams();
            if (target.isUnfinished()) {
                modifier = 1.0f;
            }
            if (!strengthCheck(performer, 23.0 + (3.0f * modifier - 3.0f))) {
                final String message = StringUtil.format("You are not strong enough to do this, you need at least %.1f body strength.", 23.0 + (3.0f * modifier - 3.0f));
                performer.getCommunicator().sendNormalServerMessage(message);
                return true;
            }
            act.setTimeLeft(time);
            performer.getCommunicator().sendNormalServerMessage("You start to load the " + target.getName() + ".");
            Server.getInstance().broadCastAction(performer.getName() + " starts to load the " + target.getName() + ".", performer, 5);
            performer.sendActionControl(Actions.actionEntrys[605].getVerbString(), true, time);
            performer.getStatus().modifyStamina(-10000.0f);
            return false;
        }
        else {
            time = act.getTimeLeft();
            if (act.currentSecond() % 5 == 0) {
                performer.getStatus().modifyStamina(-10000.0f);
            }
            if (counter * 10.0f <= time) {
                return false;
            }
            final boolean isStealing = MethodsItems.checkIfStealing(target, performer, act);
            if (performerIsTryingToStealInLawfulMode(performer, isStealing)) {
                return true;
            }
            if (isStealing) {
                if (performerMayNotStealCheck(performer)) {
                    return true;
                }
                Skill stealing = null;
                boolean dryRun = false;
                final Village v = Zones.getVillage(target.getTileX(), target.getTileY(), true);
                if (v != null) {
                    final Reputation rep = v.getReputationObject(performer.getWurmId());
                    if (rep != null && rep.getValue() >= 0 && rep.isPermanent()) {
                        dryRun = true;
                    }
                }
                if (MethodsItems.setTheftEffects(performer, act, target)) {
                    stealing = performer.getStealSkill();
                    stealing.skillCheck(target.getQualityLevel(), 0.0, dryRun, 10.0f);
                    return true;
                }
                stealing = performer.getStealSkill();
                stealing.skillCheck(target.getQualityLevel(), 0.0, dryRun, 10.0f);
            }
            Creature[] watchers = null;
            try {
                watchers = target.getWatchers();
            }
            catch (Exception ex) {}
            try {
                if (targetIsNotOnTheGround(target, performer, true)) {
                    return true;
                }
                final Zone zone = Zones.getZone(target.getTileX(), target.getTileY(), target.isOnSurface());
                zone.removeItem(target);
                carrier.insertItem(target, true);
                updateItemModel(carrier);
                performer.getCommunicator().sendNormalServerMessage("You finish loading the " + target.getName() + ".");
                Server.getInstance().broadCastAction(performer.getName() + " finishes loading the " + target.getName() + ".", performer, 5);
                if (watchers != null) {
                    for (final Creature c : watchers) {
                        c.getCommunicator().sendCloseInventoryWindow(target.getWurmId());
                    }
                }
            }
            catch (NoSuchZoneException nsz) {
                final String message2 = StringUtil.format("Unable to find zone for x: %d y: %d surface: %s in loadShip().", target.getTileX(), target.getTileY(), Boolean.toString(target.isOnSurface()));
                CargoTransportationMethods.logger.log(Level.WARNING, message2, nsz);
            }
            return true;
        }
    }
    
    public static final void updateItemModel(final Item toUpdate) {
        toUpdate.updateModelNameOnGroundItem();
        toUpdate.updateName();
    }
    
    private static final boolean targetIsDraggedCheck(final Item target, final Creature performer) {
        if (target.isDraggable() && Items.isItemDragged(target)) {
            final String message = StringUtil.format("You can not load the %s while it's being dragged.", target.getName());
            performer.getCommunicator().sendNormalServerMessage(message);
            return true;
        }
        return false;
    }
    
    private static final boolean targetIsSameAsCarrier(final Creature performer, final Vehicle vehicle, final Item target) {
        if (vehicle.getWurmid() == target.getWurmId()) {
            performer.getCommunicator().sendNormalServerMessage("You are unable to bend reality enough to accomplish that!");
            return true;
        }
        return false;
    }
    
    private static final boolean targetIsNotOnTheGround(final Item target, final Creature performer, final boolean finalCheck) {
        if (target.getTopParent() != target.getWurmId()) {
            String message;
            if (finalCheck) {
                message = "The %s is no longer on the ground, so you can't load it.";
            }
            else {
                message = "The %s needs to be on the ground.";
            }
            performer.getCommunicator().sendNormalServerMessage(StringUtil.format(message, target.getName()));
            return true;
        }
        return false;
    }
    
    private static final boolean targetIsMoored(final Item target, final Creature player) {
        if (target.getData() != -1L) {
            try {
                final Item anchor = Items.getItem(target.getData());
                if (anchor.isAnchor()) {
                    player.getCommunicator().sendNormalServerMessage("You are not allowed to load a moored ship.");
                    return true;
                }
            }
            catch (NoSuchItemException nsi) {
                CargoTransportationMethods.logger.log(Level.FINE, "Unable to find item with id: " + target.getData(), nsi);
            }
        }
        return false;
    }
    
    private static final boolean targetIsHitchedOrCommanded(final Item target, final Creature performer) {
        if (target.isVehicle()) {
            final Vehicle targetVehicle = Vehicles.getVehicle(target);
            if (targetVehicle == null) {
                return false;
            }
            for (int i = 0; i < targetVehicle.getHitched().length; ++i) {
                if (targetVehicle.getHitched()[i].isOccupied()) {
                    final String m = StringUtil.format("You can not load the %s while it's being dragged or has creatures hitched.", targetVehicle.getName());
                    performer.getCommunicator().sendNormalServerMessage(m);
                    return true;
                }
            }
            for (int i = 0; i < targetVehicle.getSeats().length; ++i) {
                if (targetVehicle.getSeats()[i].isOccupied()) {
                    final String m = StringUtil.format("You can not load the %s while someone is using it.", targetVehicle.getName());
                    performer.getCommunicator().sendNormalServerMessage(m);
                    return true;
                }
            }
        }
        return false;
    }
    
    private static final boolean targetIsPlantedCheck(final Item target, final Creature performer) {
        if (target.isEnchantedTurret()) {
            if (target.isPlanted() && (target.getKingdom() != performer.getKingdomId() || target.getLastOwnerId() != performer.getWurmId())) {
                final VolaTile t = Zones.getTileOrNull(target.getTileX(), target.getTileY(), target.isOnSurface());
                if (t != null && t.getVillage() != null && t.getVillage().isCitizen(performer) && target.getKingdom() == performer.getKingdomId()) {
                    return false;
                }
                final String message = StringUtil.format("The %s is firmly planted in the ground.", target.getName());
                performer.getCommunicator().sendNormalServerMessage(message, (byte)3);
                return true;
            }
        }
        else if (target.isPlanted() && !ItemBehaviour.isSignManipulationOk(target, performer, (short)6)) {
            final String message2 = StringUtil.format("The %s is firmly planted in the ground.", target.getName());
            performer.getCommunicator().sendNormalServerMessage(message2, (byte)3);
            return true;
        }
        return false;
    }
    
    private static final boolean targetIsNotTransportable(final Item target, final Creature performer) {
        if (!target.getTemplate().isTransportable()) {
            final String message = StringUtil.format("%s is not transportable in this way.", target.getName());
            performer.getCommunicator().sendNormalServerMessage(message);
            return true;
        }
        return false;
    }
    
    private static final boolean targetCanNotBeInsertedCheck(final Item target, final Item carrier, final Vehicle vehicle, final Creature performer) {
        boolean result = true;
        final int freevol = carrier.getFreeVolume();
        final int targetvol = target.getVolume();
        if (!carrier.isBoat()) {
            if (carrier.getContainerSizeX() >= target.getSizeX() && carrier.getContainerSizeY() >= target.getSizeY() && carrier.getContainerSizeZ() > target.getSizeZ() && freevol >= targetvol) {
                result = !carrier.mayCreatureInsertItem();
            }
        }
        else if (freevol >= targetvol) {
            result = !carrier.mayCreatureInsertItem();
        }
        if (result) {
            final String message = StringUtil.format("There is not enough room in the %s for the %s.", vehicle.getName(), target.getName());
            performer.getCommunicator().sendNormalServerMessage(message);
            return result;
        }
        return result;
    }
    
    private static final boolean targetIsOnFireCheck(final Item target, final Creature performer, final Item carrier) {
        if (target.isOnFire() && !target.isAlwaysLit()) {
            final String message = StringUtil.format("The %s is still burning and can not be loaded on to the %s.", target.getName(), carrier.getName());
            performer.getCommunicator().sendNormalServerMessage(message);
            return true;
        }
        return false;
    }
    
    private static final boolean targetHasActiveQueen(final Item target, final Creature performer, final Item carrier, final boolean load) {
        if (target.getTemplateId() == 1175 && target.hasQueen() && performer.getBestBeeSmoker() == null && performer.getPower() <= 1) {
            performer.getCommunicator().sendSafeServerMessage("The bees get angry and defend the " + target.getName() + " by stinging you.");
            performer.addWoundOfType(null, (byte)5, 2, true, 1.0f, false, 4000.0f + Server.rand.nextFloat() * 8000.0f, 0.0f, 25.0f, false, false);
            return true;
        }
        return false;
    }
    
    private static final boolean targetIsNotEmptyContainerCheck(final Item target, final Creature performer, final Item carrier, final boolean load) {
        return false;
    }
    
    private static final boolean targetIsLoadedWarmachine(final Item target, final Creature performer, final Item carrier) {
        if (WarmachineBehaviour.isLoaded(target)) {
            final String message = StringUtil.format("The %s must be unloaded before being loaded on to the %s.", target.getName(), carrier.getName());
            performer.getCommunicator().sendNormalServerMessage(message);
            return true;
        }
        return false;
    }
    
    private static final Item getCarrierItem(final Vehicle vehicle, final Creature performer) {
        try {
            return Items.getItem(vehicle.getWurmid());
        }
        catch (NoSuchItemException nsi) {
            final String message = StringUtil.format("Unable to get vehicle item for vehicle with id: %d.", vehicle.getWurmid());
            CargoTransportationMethods.logger.log(Level.WARNING, message, nsi);
            return null;
        }
    }
    
    private static final Item getCarrierItemForTarget(final Item target, final Creature performer) {
        try {
            return Items.getItem(target.getTopParent());
        }
        catch (NoSuchItemException nsi) {
            final String message = StringUtil.format("Unable to get parent vehicle for: %s with top parent: %d", target.getName(), target.getTopParent());
            CargoTransportationMethods.logger.log(Level.WARNING, message, nsi);
            return null;
        }
    }
    
    private static final int getLoadActionDistance(final Item carrier) {
        final int DEFAULT_MAX_RANGE = 4;
        if (!carrier.isVehicle()) {
            return 4;
        }
        final Vehicle vehicle = Vehicles.getVehicle(carrier);
        if (vehicle != null) {
            return vehicle.getMaxAllowedLoadDistance();
        }
        return 4;
    }
    
    private static final boolean targetIsLockedCheck(final Item target, final Creature performer, final Vehicle vehicle) {
        return targetIsLockedCheck(target, performer, vehicle, true);
    }
    
    private static final boolean targetIsLockedCheck(final Item target, final Creature performer, final Vehicle vehicle, final boolean sendMessage) {
        if (target.getLockId() != -10L) {
            boolean locked = false;
            boolean hasKey = false;
            try {
                final Item lock = Items.getItem(target.getLockId());
                locked = lock.isLocked();
                hasKey = performer.hasKeyForLock(lock);
                if (target.getTemplateId() == 1311 && target.isLocked()) {
                    if (!target.mayAccessHold(performer)) {
                        if (sendMessage) {
                            performer.getCommunicator().sendNormalServerMessage("You cannot open the " + target.getName() + " so therefore cannot load it.");
                        }
                        return locked;
                    }
                    hasKey = true;
                }
            }
            catch (NoSuchItemException nsi) {
                locked = true;
            }
            if (locked && !hasKey) {
                if (sendMessage) {
                    final String message = "The %s is locked. It needs to be unlocked before being loaded on to the %s.";
                    performer.getCommunicator().sendSafeServerMessage(StringUtil.format("The %s is locked. It needs to be unlocked before being loaded on to the %s.", target.getName(), vehicle.getName()));
                }
                return locked;
            }
        }
        return false;
    }
    
    private static final boolean targetVehicleIsLockedCheck(final Item target, final Creature performer, final Vehicle vehicle) {
        if (targetIsLockedCheck(target, performer, vehicle, false) && !VehicleBehaviour.mayDriveVehicle(performer, target, null)) {
            final String message = "The %s is locked. It needs to be unlocked before being loaded on to the %s, or you must be allowed to embark as a driver.";
            performer.getCommunicator().sendSafeServerMessage(StringUtil.format("The %s is locked. It needs to be unlocked before being loaded on to the %s, or you must be allowed to embark as a driver.", target.getName(), vehicle.getName()));
            return true;
        }
        return false;
    }
    
    private static final boolean performerIsNotOnAVehicle(final Creature performer) {
        if (performer.getVehicle() == -10L) {
            performer.getCommunicator().sendNormalServerMessage("You are not on a vehicle.");
            return true;
        }
        return false;
    }
    
    private static final boolean performerIsNotOnATransportVehicle(final Creature performer, final Vehicle vehicle) {
        if (vehicle == null || vehicle.creature) {
            performer.getCommunicator().sendNormalServerMessage("You are not the commander or passenger of a vehicle.");
            return true;
        }
        return false;
    }
    
    private static final boolean performerIsNotSeatedOnAVehicle(final Creature performer, final Seat seat) {
        if (seat == null) {
            performer.getCommunicator().sendNormalServerMessage("You need to be the commander or a passenger to do this.");
            return true;
        }
        return false;
    }
    
    private static final boolean performerIsWithinDistanceToTarget(final Creature performer, final Item target, final int maxDistance) {
        if (!performer.isWithinDistanceTo(target.getPosX(), target.getPosY(), target.getPosZ(), maxDistance)) {
            performer.getCommunicator().sendNormalServerMessage("You need to be closer to do this.");
            return false;
        }
        return true;
    }
    
    private static final boolean carrierIsNotVehicle(final Item carrier, final Item target, final Creature performer) {
        if (!carrier.isVehicle()) {
            final String message = StringUtil.format("%s must be loaded on a vehicle for this to work.", target.getName());
            performer.getCommunicator().sendNormalServerMessage(message, (byte)3);
            return true;
        }
        return false;
    }
    
    private static final boolean performerMayNotStealCheck(final Creature performer) {
        if (!performer.maySteal()) {
            performer.getCommunicator().sendNormalServerMessage("You need more body control to steal things.", (byte)3);
            return true;
        }
        return false;
    }
    
    private static final boolean performerIsTryingToStealInLawfulMode(final Creature performer, final boolean isStealing) {
        if (isStealing && Action.checkLegalMode(performer)) {
            performer.getCommunicator().sendNormalServerMessage("This would be stealing. You need to deactivate lafwful mode in order to steal.", (byte)3);
            return true;
        }
        return false;
    }
    
    private static final boolean perfomerActionTargetIsBlocked(final Creature performer, final Item target) {
        final BlockingResult result = Blocking.getBlockerBetween(performer, target, 4);
        if (result != null) {
            final String message = StringUtil.format("You can't reach the %s through the %s.", target.getName(), result.getFirstBlocker().getName());
            performer.getCommunicator().sendSafeServerMessage(message, (byte)3);
            return true;
        }
        return false;
    }
    
    private static final boolean performerIsLastOwner(final Creature performer, final Item target) {
        if (target.getLastOwnerId() != performer.getWurmId()) {
            performer.getCommunicator().sendNormalServerMessage(StringUtil.format("You are not the owner of the %s.", target.getName()), (byte)3);
            return false;
        }
        return true;
    }
    
    private static final boolean isLargeMagicChest(final Item target) {
        return target.getTemplateId() == 664;
    }
    
    private static final boolean isAutoRefillWell(final Item target) {
        if (target.getTemplateId() == 608) {
            final byte aux = target.getAuxData();
            if (aux == 4 || aux == 5 || aux == 6) {
                return true;
            }
        }
        return false;
    }
    
    private static final boolean performerMayNotUseInventory(final Creature performer, final Item carrier) {
        if (carrier.isLocked() && !MethodsItems.mayUseInventoryOfVehicle(performer, carrier)) {
            final String message = "You are not allowed to access the inventory of the %s.";
            performer.getCommunicator().sendNormalServerMessage(StringUtil.format("You are not allowed to access the inventory of the %s.", carrier.getName()), (byte)3);
            return true;
        }
        return false;
    }
    
    private static final boolean targetIsOccupiedBed(final Item target, final Creature performer, final short actionId) {
        if (target.isBed()) {
            final PlayerInfo info = PlayerInfoFactory.getPlayerSleepingInBed(target.getWurmId());
            if (info != null) {
                if (actionId == 672 || actionId == 671) {
                    final String message = StringUtil.format("You can not haul the %s because it's occupied by %s.", target.getName(), info.getName());
                    performer.getCommunicator().sendNormalServerMessage(message, (byte)3);
                }
                else {
                    final String message = StringUtil.format("You can not load the %s because it's occupied by %s.", target.getName(), info.getName());
                    performer.getCommunicator().sendNormalServerMessage(message, (byte)3);
                }
                return true;
            }
        }
        return false;
    }
    
    public static final boolean unloadCargo(final Creature performer, final Item target, final float counter) {
        if (target.getTopParent() == target.getWurmId()) {
            return true;
        }
        Action act = null;
        try {
            act = performer.getCurrentAction();
        }
        catch (NoSuchActionException nsa) {
            final String message = StringUtil.format("Unable to find current action for %s in unloadCargo.", performer.getName());
            CargoTransportationMethods.logger.log(Level.WARNING, message, nsa);
            return true;
        }
        if (target.getTemplateId() == 1311) {
            final VolaTile t = Zones.getTileOrNull(target.getTileX(), target.getTileY(), performer.isOnSurface());
            if (t.getFourPerTileCount(performer.getFloorLevel()) >= 4) {
                performer.getCommunicator().sendNormalServerMessage("You cannot unload this here, there isn't enough room.");
                return true;
            }
        }
        final Item carrier = getCarrierItemForTarget(target, performer);
        if (carrier == null) {
            return true;
        }
        if (carrierIsNotVehicle(carrier, target, performer)) {
            return true;
        }
        if (cantBeUnloaded(target, carrier, performer)) {
            return true;
        }
        if (!Methods.isActionAllowed(performer, (short)234)) {
            return true;
        }
        final int distance = getLoadActionDistance(carrier);
        if (!performerIsWithinDistanceToTarget(performer, target, distance)) {
            return true;
        }
        if (targetIsNotEmptyContainerCheck(target, performer, carrier, false)) {
            return true;
        }
        if (targetHasActiveQueen(target, performer, carrier, false)) {
            return true;
        }
        if (!mayUnloadHereCheck(target, performer)) {
            return true;
        }
        if (!isOnSameLevelUnload(carrier, performer)) {
            return true;
        }
        int time = Actions.getLoadUnloadActionTime(performer);
        if (counter != 1.0f) {
            time = act.getTimeLeft();
            if (act.currentSecond() == 3) {
                final Village village = Zones.getVillage(target.getTileX(), target.getTileY(), performer.isOnSurface());
                final VolaTile vt = Zones.getTileOrNull(target.getTileX(), target.getTileY(), performer.isOnSurface());
                final Structure structure = (vt != null) ? vt.getStructure() : null;
                if ((!performer.isOnSurface() || structure == null || !structure.isTypeHouse() || !structure.isFinished() || !structure.isActionAllowed(performer, (short)605)) && village != null) {
                    if (!village.isActionAllowed((short)605, performer)) {
                        performer.getCommunicator().sendServerMessage("WARNING: You currently do not have permissions to re-load this item.", 255, 127, 63);
                    }
                }
            }
            if (counter * 10.0f > time) {
                try {
                    carrier.dropItem(target.getWurmId(), false);
                }
                catch (NoSuchItemException nsi) {
                    final String message2 = StringUtil.format("Unable to find and drop item: %s with id:%d", target.getName(), target.getWurmId());
                    CargoTransportationMethods.logger.log(Level.WARNING, message2, nsi);
                    return true;
                }
                try {
                    final Zone zone = Zones.getZone(performer.getTileX(), performer.getTileY(), performer.isOnSurface());
                    target.setPos(performer.getPosX(), performer.getPosY(), performer.getPositionZ(), target.getRotation(), performer.getBridgeId());
                    zone.addItem(target);
                    if (target.getLockId() == -10L && !target.isVehicle() && !isLargeMagicChest(target)) {
                        target.setLastOwnerId(performer.getWurmId());
                    }
                    if (target.getTemplateId() == 891) {
                        target.setLastOwnerId(performer.getWurmId());
                    }
                    if (carrier.getTemplateId() == 853 || carrier.getTemplateId() == 1410) {
                        updateItemModel(carrier);
                    }
                    final String youMessage = StringUtil.format("You finish unloading the %s from the %s.", target.getName(), carrier.getName());
                    final String broadcastMessage = StringUtil.format("%s finishes unloading the %s from the %s.", performer.getName(), target.getName(), carrier.getName());
                    performer.getCommunicator().sendNormalServerMessage(youMessage);
                    Server.getInstance().broadCastAction(broadcastMessage, performer, 5);
                    return true;
                }
                catch (NoSuchZoneException nsz) {
                    final String message2 = StringUtil.format("Unable to find zone for x:%d y:%d surface:%s.", performer.getTileX(), performer.getTileY(), Boolean.toString(performer.isOnSurface()));
                    CargoTransportationMethods.logger.log(Level.WARNING, message2, nsz);
                    return true;
                }
            }
            return false;
        }
        if (!strengthCheck(performer, 23.0)) {
            performer.getCommunicator().sendNormalServerMessage("You are not strong enough to do this, you need at least 23 body strength.", (byte)3);
            return true;
        }
        act.setTimeLeft(time);
        final String youMessage2 = StringUtil.format("You start to unload the %s from the %s.", target.getName(), carrier.getName());
        final String broadcastMessage2 = StringUtil.format("%s starts to unload the %s from the %s.", performer.getName(), target.getName(), carrier.getName());
        performer.getCommunicator().sendNormalServerMessage(youMessage2);
        Server.getInstance().broadCastAction(broadcastMessage2, performer, 5);
        performer.sendActionControl(Actions.actionEntrys[606].getVerbString(), true, time);
        performer.getStatus().modifyStamina(-10000.0f);
        return false;
    }
    
    private static final boolean mayUnloadHereCheck(final Item target, final Creature player) {
        final VolaTile tile = Zones.getTileOrNull(player.getTileX(), player.getTileY(), player.isOnSurface());
        if (tile == null) {
            return false;
        }
        final int level = tile.getDropFloorLevel(player.getFloorLevel(true));
        if (tile.getNumberOfItems(level) >= 100) {
            final String message = "You cannot unload the %s here, since there are too many items here already.";
            player.getCommunicator().sendNormalServerMessage(StringUtil.format("You cannot unload the %s here, since there are too many items here already.", StringUtil.toLowerCase(target.getName())), (byte)3);
            return false;
        }
        if (tile.getNumberOfDecorations(level) >= 15 && !target.isCrate()) {
            final String message = "You cannot unload the %s here, since there are too many decorations here already.";
            player.getCommunicator().sendNormalServerMessage(StringUtil.format("You cannot unload the %s here, since there are too many decorations here already.", StringUtil.toLowerCase(target.getName())), (byte)3);
            return false;
        }
        if ((target.isOnePerTile() && tile.hasOnePerTileItem(level)) || (target.isFourPerTile() && tile.getFourPerTileCount(level) == 4)) {
            final String message = "You cannot unload the %s here, since there is not enough space in front of you.";
            player.getCommunicator().sendNormalServerMessage(StringUtil.format("You cannot unload the %s here, since there is not enough space in front of you.", StringUtil.toLowerCase(target.getName())), (byte)3);
            return false;
        }
        if (target.isOutsideOnly() && tile.getStructure() != null) {
            final String message = "You cannot unload the %s here, it must be unloaded outside.";
            player.getCommunicator().sendNormalServerMessage(StringUtil.format("You cannot unload the %s here, it must be unloaded outside.", StringUtil.toLowerCase(target.getName())), (byte)3);
            return false;
        }
        if (target.isSurfaceOnly() && !player.isOnSurface()) {
            final String message = "You cannot unload the %s here, it must be unloaded on the surface.";
            player.getCommunicator().sendNormalServerMessage(StringUtil.format("You cannot unload the %s here, it must be unloaded on the surface.", StringUtil.toLowerCase(target.getName())), (byte)3);
            return false;
        }
        if (player.isOnSurface() && target.isSurfaceOnly()) {
            final int encodedTile = Server.surfaceMesh.getTile(player.getTileX(), player.getTileY());
            final byte tileType = Tiles.decodeType(encodedTile);
            if (tileType == 0 || Tiles.isMineDoor(tileType)) {
                final String message2 = "You cannot unload the %s here, it cannot be unloaded on a mine door.";
                player.getCommunicator().sendNormalServerMessage(StringUtil.format("You cannot unload the %s here, it cannot be unloaded on a mine door.", StringUtil.toLowerCase(target.getName())), (byte)3);
                return false;
            }
        }
        return true;
    }
    
    public static final boolean strengthCheck(final Creature player, final double neededStrength) {
        try {
            final Skill strength = player.getSkills().getSkill(102);
            return strength.getRealKnowledge() >= neededStrength;
        }
        catch (NoSuchSkillException nss) {
            CargoTransportationMethods.logger.log(Level.WARNING, "Unable to find body strength of player: " + player.getName(), nss);
            return false;
        }
    }
    
    public static final List<ActionEntry> getHaulActions(final Creature player, final Item target) {
        final List<ActionEntry> toReturn = new LinkedList<ActionEntry>();
        if (target.getTemplate().isTransportable() && !target.isBoat()) {
            final int playerFloorLevel = player.getFloorLevel(true);
            final int targetFloorLevel = target.getFloorLevel();
            if (playerFloorLevel > targetFloorLevel) {
                final int floorDiff = playerFloorLevel - targetFloorLevel;
                if (playerFloorLevel > 0 && floorDiff == 1) {
                    final VolaTile tile = player.getCurrentTile();
                    if (tile == null) {
                        return toReturn;
                    }
                    final Structure structure = tile.getStructure();
                    if (structure == null) {
                        return toReturn;
                    }
                    final Floor[] floors = structure.getFloorsAtTile(tile.tilex, tile.tiley, playerFloorLevel * 30, playerFloorLevel * 30);
                    if (floors == null || floors.length == 0) {
                        return toReturn;
                    }
                    if (floors[0].getType() != StructureConstants.FloorType.OPENING) {
                        return toReturn;
                    }
                    toReturn.add(Actions.actionEntrys[671]);
                }
            }
            else if (playerFloorLevel == targetFloorLevel) {
                final VolaTile tile2 = player.getCurrentTile();
                if (tile2 == null) {
                    return toReturn;
                }
                final Structure structure2 = tile2.getStructure();
                if (structure2 == null) {
                    return toReturn;
                }
                final Floor[] floors2 = structure2.getFloorsAtTile(tile2.tilex, tile2.tiley, playerFloorLevel * 30, playerFloorLevel * 30);
                if (floors2 == null || floors2.length == 0) {
                    return toReturn;
                }
                if (floors2[0].getType() != StructureConstants.FloorType.OPENING) {
                    return toReturn;
                }
                toReturn.add(Actions.actionEntrys[672]);
            }
        }
        return toReturn;
    }
    
    public static final boolean haul(final Creature performer, final Item target, final float counter, final short actionId, final Action act) {
        final double strNeeded = 21.0;
        if (!strengthCheck(performer, 21.0)) {
            performer.getCommunicator().sendNormalServerMessage(StringUtil.format("You need at least %.2f Body Strength to haul.", 21.0), (byte)3);
            return true;
        }
        if (targetIsNotOnTheGround(target, performer, false)) {
            return true;
        }
        if (isAutoRefillWell(target)) {
            performer.getCommunicator().sendNormalServerMessage("This is a special version of the item that is designed to exist only on starter deeds, and is therefor not transportable.");
            return true;
        }
        if (!target.getTemplate().isTransportable()) {
            performer.getCommunicator().sendNormalServerMessage("You can not haul this item.", (byte)3);
            return true;
        }
        if (target.getTemplate().isContainerWithSubItems()) {
            for (final Item i : target.getItems()) {
                if (i.isPlacedOnParent()) {
                    performer.getCommunicator().sendNormalServerMessage("You can not haul this item while it has items placed on top of it.", (byte)3);
                    return true;
                }
            }
        }
        if (target.isBoat()) {
            performer.getCommunicator().sendNormalServerMessage("You may not haul boats up or down in houses.", (byte)3);
            return true;
        }
        if (targetIsPlantedCheck(target, performer)) {
            return true;
        }
        if (targetIsOccupiedBed(target, performer, actionId)) {
            return true;
        }
        if (target.isVehicle()) {
            if ((target.getTemplateId() == 853 || target.getTemplateId() == 1410) && target.getItemCount() != 0) {
                performer.getCommunicator().sendNormalServerMessage(StringUtil.format("The %s needs to be empty before you can haul it.", target.getName()), (byte)3);
                return true;
            }
            final Vehicle vehicle = Vehicles.getVehicle(target);
            if (vehicle.isAnySeatOccupied()) {
                performer.getCommunicator().sendNormalServerMessage(StringUtil.format("You may not haul the %s when it's in use by another player.", target.getName()), (byte)3);
                return true;
            }
            if (vehicle.isAnythingHitched()) {
                performer.getCommunicator().sendNormalServerMessage(StringUtil.format("You may not haul the %s when there are creatures hitched to it.", target.getName()), (byte)3);
                return true;
            }
        }
        if (target.isDraggable() && Items.isItemDragged(target)) {
            performer.getCommunicator().sendNormalServerMessage(StringUtil.format("You can not haul the %s while it's being dragged.", target.getName()), (byte)3);
            return true;
        }
        final int playerFloorLevel = performer.getFloorLevel(true);
        final int targetFloorLevel = target.getFloorLevel();
        final int diff = Math.abs(playerFloorLevel - targetFloorLevel);
        if (actionId == 671) {
            if (diff > 1) {
                performer.getCommunicator().sendNormalServerMessage("The difference in floor levels is to great, you need to be closer.", (byte)3);
                return true;
            }
        }
        else if (diff != 0) {
            performer.getCommunicator().sendNormalServerMessage("You must be on the same floor level.", (byte)3);
            return true;
        }
        if (!performerIsWithinDistanceToTarget(performer, target, 4)) {
            return true;
        }
        final boolean onDeed = Actions.canReceiveDeedSpeedBonus(performer);
        int time = onDeed ? 10 : 50;
        if (counter == 1.0f) {
            if (!strengthCheck(performer, 21.0)) {
                performer.getCommunicator().sendNormalServerMessage(StringUtil.format("You need at least %.2f Body Strength to haul.", 21.0), (byte)3);
                return true;
            }
            act.setTimeLeft(time);
            performer.getCommunicator().sendNormalServerMessage("You start to haul the " + target.getName() + ".");
            Server.getInstance().broadCastAction(performer.getName() + " starts to haul the " + target.getName() + ".", performer, 5);
            performer.sendActionControl(Actions.actionEntrys[actionId].getVerbString(), true, time);
            performer.getStatus().modifyStamina(-10000.0f);
            return false;
        }
        else {
            time = act.getTimeLeft();
            if (act.currentSecond() % 5 == 0) {
                performer.getStatus().modifyStamina(-10000.0f);
            }
            if (counter * 10.0f > time) {
                final int tileX = target.getTileX();
                final int tileY = target.getTileY();
                try {
                    final Zone zone = Zones.getZone(tileX, tileY, target.isOnSurface());
                    float z = 0.0f;
                    if (actionId == 671) {
                        z = Zones.calculatePosZ(performer.getPosX(), performer.getPosY(), performer.getCurrentTile(), target.isOnSurface(), performer.isOnSurface(), target.getPosZ(), performer, -10L);
                    }
                    else {
                        z = Zones.calculateHeight(target.getPosX(), target.getPosY(), target.isOnSurface()) + (performer.getFloorLevel() - 1) * 3.0f;
                    }
                    zone.removeItem(target);
                    target.setPosXYZ(performer.getPosX(), performer.getPosY(), z);
                    zone.addItem(target);
                }
                catch (NoSuchZoneException nsz) {
                    CargoTransportationMethods.logger.log(Level.WARNING, "Unable to find zone for item.", nsz);
                }
                return true;
            }
            return false;
        }
    }
    
    public static final List<ActionEntry> getLoadUnloadActions(final Creature player, final Item target) {
        final List<ActionEntry> toReturn = new LinkedList<ActionEntry>();
        if (!target.getTemplate().isTransportable() && !target.isBoat() && !target.isUnfinished()) {
            return toReturn;
        }
        if (target.getTopParent() == target.getWurmId()) {
            if (player.getVehicle() != -10L) {
                final Vehicle vehicle = Vehicles.getVehicleForId(player.getVehicle());
                if (vehicle != null && !vehicle.creature && !vehicle.isChair()) {
                    final Seat seat = vehicle.getSeatFor(player.getWurmId());
                    if (seat != null) {
                        try {
                            final Item vehicleItem = Items.getItem(vehicle.getWurmid());
                            if (target.isBoat() && vehicleItem.getTemplateId() != 853) {
                                return toReturn;
                            }
                            if (target.isUnfinished() && vehicleItem.getTemplateId() == 853) {
                                final ItemTemplate template = target.getRealTemplate();
                                if (template == null) {
                                    return toReturn;
                                }
                                if (!template.isBoat()) {
                                    return toReturn;
                                }
                            }
                            if (MethodsItems.mayUseInventoryOfVehicle(player, vehicleItem) || vehicleItem.getLockId() == -10L || Items.isItemDragged(vehicleItem)) {
                                toReturn.add(Actions.actionEntrys[605]);
                            }
                        }
                        catch (NoSuchItemException nsi) {
                            final String message = StringUtil.format("Unable to find vehicle item with id: %s.", vehicle.getWurmid());
                            CargoTransportationMethods.logger.log(Level.WARNING, message, nsi);
                        }
                    }
                }
            }
        }
        else {
            final Vehicle vehicle = Vehicles.getVehicleForId(target.getTopParent());
            try {
                if (vehicle != null) {
                    final Item vehicleItem2 = Items.getItem(vehicle.getWurmid());
                    if (vehicle != null && !vehicle.creature && (MethodsItems.mayUseInventoryOfVehicle(player, vehicleItem2) || vehicleItem2.getLockId() == -10L || Items.isItemDragged(vehicleItem2))) {
                        toReturn.add(Actions.actionEntrys[606]);
                    }
                }
            }
            catch (NoSuchItemException nsi2) {
                final String message2 = StringUtil.format("Unable to find vehicle item with id: %d.", vehicle.getWurmid());
                CargoTransportationMethods.logger.log(Level.WARNING, message2, nsi2);
            }
        }
        return toReturn;
    }
    
    private static boolean cantBeUnloaded(final Item target, final Item carrier, final Creature performer) {
        if (target.getTopParentOrNull() != null && target.getParentOrNull() != null && target.getParentOrNull().isVehicle() && target.getTopParentOrNull() != target.getParentOrNull()) {
            performer.getCommunicator().sendNormalServerMessage("You can't unload the " + target.getName() + " from there. Unload the " + target.getParentOrNull().getName() + " first.");
            return true;
        }
        return ((!target.isBoat() && !isUnfinishedBoat(target)) || carrier.getTemplateId() != 853) && (target.getTemplateId() != 1311 || carrier.getTemplateId() != 1410) && targetIsNotTransportable(target, performer);
    }
    
    private static boolean isUnfinishedBoat(final Item item) {
        return item.isUnfinished() && item.getRealTemplate() != null && item.getRealTemplate().isBoat();
    }
    
    static boolean loadChicken(final Creature performer, final Item target, final float counter) {
        final Creature[] folls = performer.getFollowers();
        Action act;
        try {
            act = performer.getCurrentAction();
        }
        catch (NoSuchActionException nsa) {
            CargoTransportationMethods.logger.log(Level.WARNING, "Unable to get current action in loadChicken().", nsa);
            return true;
        }
        if (folls.length > 1) {
            performer.getCommunicator().sendNormalServerMessage("You are currently leading too many creatures.");
            return true;
        }
        if (target.isLocked() && !target.mayAccessHold(performer)) {
            performer.getCommunicator().sendNormalServerMessage("You cant put a chicken in this coop, its locked.");
            return true;
        }
        if (target.getCurrentQualityLevel() < 10.0f) {
            performer.getCommunicator().sendNormalServerMessage("The coop is in too poor of shape to be used.");
            return true;
        }
        for (final Item item : target.getAllItems(true)) {
            if (item.getTemplateId() == 1434 && item.isEmpty(true)) {
                performer.getCommunicator().sendNormalServerMessage("You need to put food in the " + item.getName() + " first.");
                return true;
            }
            if (item.getTemplateId() == 1435 && item.isEmpty(true)) {
                performer.getCommunicator().sendNormalServerMessage("You need to put water in the " + item.getName() + " first.");
                return true;
            }
        }
        if (target.getParentId() != -10L) {
            performer.getCommunicator().sendNormalServerMessage("You must unload the coop to load creatures into it.");
            return true;
        }
        final int time = Actions.getLoadUnloadActionTime(performer);
        if (counter == 1.0f) {
            for (final Creature foll : folls) {
                for (final Item item2 : target.getAllItems(true)) {
                    if (item2.getTemplateId() == 1436) {
                        try {
                            if (item2.getAllItems(true).length >= (int)item2.getParent().getQualityLevel() / 10 + item2.getParent().getRarity()) {
                                performer.getCommunicator().sendNormalServerMessage("The " + foll.getName() + " refuses to enter the coop. There is no space.");
                                return true;
                            }
                        }
                        catch (NoSuchItemException ex) {
                            CargoTransportationMethods.logger.log(Level.WARNING, ex.getMessage(), ex);
                        }
                    }
                }
                if (foll.getTemplate().getTemplateId() != 45) {
                    performer.getCommunicator().sendNormalServerMessage("The " + foll.getName() + " refuses to enter the coop.");
                    return true;
                }
                if (!performer.isWithinDistanceTo(foll, 5.0f)) {
                    performer.getCommunicator().sendNormalServerMessage("You are too far away from the creature.");
                    return true;
                }
                if (!foll.isWithinDistanceTo(target, 5.0f)) {
                    performer.getCommunicator().sendNormalServerMessage("The creature is too far away from the coop.");
                    return true;
                }
                if (!performer.isWithinDistanceTo(target, 5.0f)) {
                    performer.getCommunicator().sendNormalServerMessage("You are too far away from the coop.");
                    return true;
                }
                act.setTimeLeft(time);
                performer.getCommunicator().sendNormalServerMessage("You start to load the " + foll.getName() + ".");
                Server.getInstance().broadCastAction(performer.getName() + " starts to load the " + foll.getName() + ".", performer, 5);
                performer.sendActionControl(Actions.actionEntrys[907].getVerbString(), true, time);
                performer.getStatus().modifyStamina(-10000.0f);
            }
            return false;
        }
        if (counter * 10.0f > time) {
            try {
                for (final Creature foll : folls) {
                    final Item i = ItemFactory.createItem(1310, foll.getStatus().age, (byte)0, null);
                    for (final Item item3 : target.getAllItems(true)) {
                        if (item3.getTemplateId() == 1436) {
                            i.setData(foll.getWurmId());
                            i.setName(foll.getName());
                            i.setWeight((int)foll.getWeight(), false);
                            item3.insertItem(i, true);
                        }
                    }
                    DbCreatureStatus.setLoaded(1, foll.getWurmId());
                    target.setAuxData((byte)foll.getTemplate().getTemplateId());
                    foll.setLeader(null);
                    foll.getCurrentTile().deleteCreature(foll);
                    try {
                        foll.savePosition(-10);
                        foll.getStatus().setDead(true);
                    }
                    catch (IOException ex2) {
                        CargoTransportationMethods.logger.log(Level.WARNING, ex2.getMessage(), ex2);
                    }
                    foll.destroyVisionArea();
                    updateItemModel(target);
                    target.setData(System.currentTimeMillis());
                    performer.getCommunicator().sendNormalServerMessage("You finish loading the " + foll.getName() + ".");
                    Server.getInstance().broadCastAction(performer.getName() + " finishes loading the " + foll.getName() + ".", performer, 5);
                }
            }
            catch (FailedException | NoSuchTemplateException ex5) {
                final WurmServerException ex4;
                final WurmServerException ex3 = ex4;
                CargoTransportationMethods.logger.log(Level.WARNING, ex3.getMessage(), ex3);
            }
            return true;
        }
        return false;
    }
    
    static boolean unloadChicken(final Creature performer, final Item target, final float counter) {
        Action act;
        try {
            act = performer.getCurrentAction();
        }
        catch (NoSuchActionException nsa) {
            CargoTransportationMethods.logger.log(Level.WARNING, "Unable to get current action in loadChicken().", nsa);
            return true;
        }
        if (!Methods.isActionAllowed(performer, (short)234)) {
            return true;
        }
        try {
            final Creature getCreature = Creatures.getInstance().getCreature(target.getData());
            if (getCreature.getDominator() != null && getCreature.getDominator() != performer) {
                performer.getCommunicator().sendNormalServerMessage("You cannot unload this creature, it is not your pet.");
                return true;
            }
        }
        catch (NoSuchCreatureException e) {
            CargoTransportationMethods.logger.log(Level.WARNING, e.getMessage(), e);
        }
        if (target.getData() == -10L) {
            CargoTransportationMethods.logger.log(Level.WARNING, target.getWurmId() + " has no data, this should not happen, destroying.");
            Items.destroyItem(target.getWurmId());
            return true;
        }
        final int time = Actions.getLoadUnloadActionTime(performer);
        if (counter == 1.0f) {
            act.setTimeLeft(time);
            performer.getCommunicator().sendNormalServerMessage("You start to unload the " + target.getName() + ".");
            Server.getInstance().broadCastAction(performer.getName() + " starts to unload the " + target.getName() + ".", performer, 5);
            performer.sendActionControl(Actions.actionEntrys[908].getVerbString(), true, time);
            performer.getStatus().modifyStamina(-10000.0f);
            return false;
        }
        if (counter * 10.0f > time) {
            try {
                final Creature getCreature2 = Creatures.getInstance().getCreature(target.getData());
                DbCreatureStatus.setLoaded(0, getCreature2.getWurmId());
                final Creatures cstat = Creatures.getInstance();
                getCreature2.getStatus().setDead(false);
                cstat.removeCreature(getCreature2);
                cstat.addCreature(getCreature2, false);
                getCreature2.putInWorld();
                CreatureBehaviour.blinkTo(getCreature2, performer.getPosX(), performer.getPosY(), performer.getLayer(), performer.getPositionZ(), performer.getBridgeId(), performer.getFloorLevel());
                performer.getCommunicator().sendNormalServerMessage("You finish unloading the " + target.getName() + ".");
                Server.getInstance().broadCastAction(performer.getName() + " finishes unloading the " + target.getName() + ".", performer, 5);
                getCreature2.save();
                getCreature2.savePosition(target.getZoneId());
                if (performer.getFollowers().length < 4) {
                    getCreature2.setLeader(performer);
                    performer.addFollower(getCreature2, performer.getLeadingItem(getCreature2));
                }
                final Item nestingBox = target.getParent();
                final Item coop = nestingBox.getParent();
                Items.destroyItem(target.getWurmId());
                updateItemModel(coop);
            }
            catch (NoSuchCreatureException | IOException | NoSuchItemException ex3) {
                final Exception ex2;
                final Exception ex = ex2;
                CargoTransportationMethods.logger.log(Level.WARNING, ex.getMessage(), ex);
            }
            return true;
        }
        return false;
    }
    
    static {
        logger = Logger.getLogger(CargoTransportationMethods.class.getName());
    }
}
