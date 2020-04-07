// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.combat;

import com.wurmonline.server.spells.SpellEffect;
import com.wurmonline.server.bodys.Wound;
import com.wurmonline.server.Players;
import java.util.List;
import com.wurmonline.server.utils.CreatureLineSegment;
import com.wurmonline.shared.util.MulticolorLineSegment;
import java.util.ArrayList;
import javax.annotation.Nullable;
import com.wurmonline.server.zones.VolaTile;
import com.wurmonline.server.zones.Zone;
import java.util.Iterator;
import com.wurmonline.server.skills.Skills;
import com.wurmonline.server.zones.NoSuchZoneException;
import com.wurmonline.server.LoginHandler;
import com.wurmonline.server.players.Player;
import com.wurmonline.server.items.NoSpaceException;
import com.wurmonline.server.creatures.NoArmourException;
import com.wurmonline.server.NoSuchPlayerException;
import com.wurmonline.server.creatures.NoSuchCreatureException;
import java.util.logging.Level;
import com.wurmonline.server.creatures.ai.PathTile;
import com.wurmonline.server.creatures.ai.Path;
import com.wurmonline.server.structures.Blocker;
import com.wurmonline.server.structures.BlockingResult;
import com.wurmonline.server.structures.Fence;
import com.wurmonline.server.skills.Skill;
import com.wurmonline.server.NoSuchItemException;
import com.wurmonline.server.Items;
import com.wurmonline.server.creatures.ai.NoPathException;
import com.wurmonline.mesh.Tiles;
import com.wurmonline.server.creatures.ai.PathFinder;
import com.wurmonline.server.zones.VirtualZone;
import com.wurmonline.server.structures.Blocking;
import com.wurmonline.server.zones.Zones;
import com.wurmonline.server.players.ItemBonus;
import com.wurmonline.server.sounds.SoundPlayer;
import com.wurmonline.server.Server;
import com.wurmonline.server.WurmCalendar;
import com.wurmonline.server.behaviours.Actions;
import com.wurmonline.server.Servers;
import com.wurmonline.server.skills.NoSuchSkillException;
import com.wurmonline.server.behaviours.Action;
import com.wurmonline.server.items.Item;
import com.wurmonline.server.creatures.Creature;
import java.util.logging.Logger;
import com.wurmonline.server.TimeConstants;
import com.wurmonline.shared.constants.SoundNames;
import com.wurmonline.shared.constants.ItemMaterials;
import com.wurmonline.server.MiscConstants;

public final class Archery implements MiscConstants, ItemMaterials, SoundNames, TimeConstants
{
    public static final int BREAKNUM = 2;
    private static final Logger logger;
    private static boolean fnd;
    private static final float swimDepth = 1.4f;
    
    public static boolean attack(final Creature performer, final Creature defender, final Item bow, final float counter, final Action act) {
        boolean done = false;
        if (defender.isInvulnerable()) {
            performer.getCommunicator().sendCombatNormalMessage("You can't attack " + defender.getNameWithGenus() + " right now.");
            return true;
        }
        if (defender.equals(performer)) {
            return true;
        }
        if (performer.getPositionZ() < -0.5 && performer.getVehicle() == -10L) {
            performer.getCommunicator().sendCombatNormalMessage("You are too deep in the water to fire a bow.");
            return true;
        }
        if (performer.getPrimWeapon() != bow) {
            final Item[] weps = performer.getSecondaryWeapons();
            Archery.fnd = false;
            for (int x = 0; x < weps.length; ++x) {
                if (weps[x] == bow) {
                    Archery.fnd = true;
                }
            }
            if (!Archery.fnd) {
                performer.getCommunicator().sendCombatNormalMessage("You need to wield the bow in order to use it.");
                return true;
            }
        }
        if (performer.getShield() != null) {
            performer.getCommunicator().sendCombatNormalMessage("You can not use the bow while wearing a shield.");
            return true;
        }
        final int minRange = getMinimumRangeForBow(bow);
        final double maxRange = 180.0;
        if (Creature.getRange(performer, defender.getPosX(), defender.getPosY()) < minRange) {
            performer.getCommunicator().sendCombatNormalMessage(defender.getNameWithGenus() + " is too close.");
            return true;
        }
        if (Creature.getRange(performer, defender.getPosX(), defender.getPosY()) > 180.0) {
            performer.getCommunicator().sendCombatNormalMessage(defender.getNameWithGenus() + " is too far away.");
            return true;
        }
        if (defender.isDead()) {
            return true;
        }
        if (bow.isWeaponBow()) {
            Skill archery = null;
            Skill bowskill = null;
            try {
                final int skillnum = bow.getPrimarySkill();
                if (skillnum != -10L) {
                    try {
                        bowskill = performer.getSkills().getSkill(skillnum);
                    }
                    catch (NoSuchSkillException nss) {
                        bowskill = performer.getSkills().learn(skillnum, 1.0f);
                    }
                }
            }
            catch (NoSuchSkillException nss2) {
                performer.getCommunicator().sendCombatNormalMessage("This weapon has no skill attached. Please report this bug using the forums or the /dev channel.");
                return true;
            }
            try {
                archery = performer.getSkills().getSkill(1030);
            }
            catch (NoSuchSkillException nss2) {
                archery = performer.getSkills().learn(1030, 1.0f);
            }
            if (bowskill == null || archery == null) {
                performer.getCommunicator().sendCombatNormalMessage("There was a bug with the skill for this item. Please report this bug using the forums or the /dev channel.");
                return true;
            }
            if (!Servers.isThisAPvpServer() && defender.getBrandVillage() != null && !defender.getBrandVillage().mayAttack(performer, defender)) {
                performer.getCommunicator().sendCombatNormalMessage(defender.getNameWithGenus() + " is protected by its branding.");
                return true;
            }
            if (performer.currentVillage != null && !performer.currentVillage.mayAttack(performer, defender) && performer.isLegal()) {
                performer.getCommunicator().sendCombatNormalMessage("The permissions for the settlement you are on prevents this.");
                return true;
            }
            if (defender.currentVillage != null && !defender.currentVillage.mayAttack(performer, defender) && performer.isLegal()) {
                performer.getCommunicator().sendCombatNormalMessage("The permissions for the settlement that the target is on prevents this.");
                return true;
            }
            int time = 200;
            if (counter == 1.0f) {
                if (performer.isOnSurface() != defender.isOnSurface()) {
                    performer.getCommunicator().sendCombatNormalMessage("You fail to get a clear shot of " + defender.getNameWithGenus() + ".");
                    return true;
                }
                final Item arrow = getArrow(performer);
                if (arrow == null) {
                    performer.getCommunicator().sendCombatNormalMessage("You have no arrows left to shoot!");
                    return true;
                }
                if (defender.isGhost() && (arrow.getSpellEffects() == null || arrow.getSpellEffects().getEffects().length == 0)) {
                    performer.getCommunicator().sendCombatNormalMessage("An unenchanted arrow would not harm the " + defender.getNameWithGenus() + ".");
                    return true;
                }
                time = Actions.getQuickActionTime(performer, archery, bow, 0.0);
                time = Math.max(50, time);
                if (act.getNumber() == 125 && time > 50) {
                    time = (int)Math.max(20.0f, time * 0.8f);
                }
                performer.setStealth(false);
                performer.getCommunicator().sendCombatNormalMessage("You start aiming at " + defender.getNameWithGenus() + ".");
                if (WurmCalendar.getHour() > 20 || WurmCalendar.getHour() < 5) {
                    performer.getCommunicator().sendCombatNormalMessage("The dusk makes it harder to get a clear view of " + defender.getNameWithGenus() + ".");
                }
                performer.sendActionControl(Actions.actionEntrys[act.getNumber()].getVerbString(), true, time);
                Server.getInstance().broadCastAction(performer.getName() + " draws an arrow and raises " + performer.getHisHerItsString() + " bow.", performer, 5);
                SoundPlayer.playSound("sound.arrow.aim", performer, 1.6f);
                act.setTimeLeft(time);
            }
            else {
                time = act.getTimeLeft();
            }
            if (counter * 10.0f > time || performer.getPower() > 0) {
                done = true;
                performer.getStatus().modifyStamina(-1000.0f);
                if (performer.isOnSurface() != defender.isOnSurface()) {
                    performer.getCommunicator().sendCombatNormalMessage("You fail to get a clear shot of " + defender.getNameWithGenus() + ".");
                    return true;
                }
                boolean limitFail = false;
                if (performer.getArmourLimitingFactor() < 0.0f && Server.rand.nextFloat() < Math.abs(performer.getArmourLimitingFactor() * ItemBonus.getArcheryPenaltyReduction(performer))) {
                    limitFail = true;
                }
                final Fence fence = null;
                Zones.resetCoverHolder();
                boolean isAttackingPenned = false;
                if (performer.isOnSurface() && !performer.isWithinDistanceTo(defender, 110.0f) && defender.getCurrentTile() != null && defender.getCurrentTile().getStructure() != null && defender.getCurrentTile().getStructure().isFinalFinished()) {
                    performer.getCommunicator().sendCombatNormalMessage("You fail to get a clear shot of " + defender.getNameWithGenus() + " inside the structure.");
                    return true;
                }
                final BlockingResult result = Blocking.getRangedBlockerBetween(performer, defender);
                if (result != null) {
                    if (!performer.isOnPvPServer() || !defender.isOnPvPServer()) {
                        performer.getCommunicator().sendCombatNormalMessage("You fail to get a clear shot of " + defender.getNameWithGenus() + ".");
                        return true;
                    }
                    for (final Blocker b : result.getBlockerArray()) {
                        if (b.getBlockPercent(performer) >= 100.0f) {
                            performer.getCommunicator().sendCombatNormalMessage("You fail to get a clear shot of " + defender.getNameWithGenus() + ".");
                            return true;
                        }
                    }
                    if (!defender.isPlayer() && result.getTotalCover() > 0.0f) {
                        isAttackingPenned = true;
                    }
                }
                if (!defender.isPlayer() && defender.getFloorLevel() != performer.getFloorLevel()) {
                    isAttackingPenned = true;
                }
                if (!VirtualZone.isCreatureTurnedTowardsTarget(defender, performer, 60.0f, false)) {
                    performer.getCommunicator().sendCombatNormalMessage("You must turn towards " + defender.getNameWithGenus() + " in order to shoot it.");
                    return true;
                }
                final int minRange2 = getMinimumRangeForBow(bow);
                if (Creature.getRange(performer, defender.getPosX(), defender.getPosY()) < minRange2) {
                    performer.getCommunicator().sendCombatNormalMessage(defender.getNameWithGenus() + " is too close.");
                    return true;
                }
                int trees = 0;
                int treetilex = -1;
                int treetiley = -1;
                int tileArrowDownX = -1;
                int tileArrowDownY = -1;
                final PathFinder pf = new PathFinder(true);
                try {
                    final Path path = pf.rayCast(performer.getCurrentTile().tilex, performer.getCurrentTile().tiley, defender.getCurrentTile().tilex, defender.getCurrentTile().tiley, performer.isOnSurface(), ((int)Creature.getRange(performer, defender.getPosX(), defender.getPosY()) >> 2) + 5);
                    final float initialHeight = Math.max(-1.4f, performer.getPositionZ() + performer.getAltOffZ() + 1.4f);
                    final float targetHeight = Math.max(-1.4f, defender.getPositionZ() + defender.getAltOffZ() + 1.4f);
                    double distx = Math.pow(performer.getCurrentTile().tilex - defender.getCurrentTile().tilex, 2.0);
                    double disty = Math.pow(performer.getCurrentTile().tiley - defender.getCurrentTile().tiley, 2.0);
                    final double dist = Math.sqrt(distx + disty);
                    final double dx = (targetHeight - initialHeight) / dist;
                    while (!path.isEmpty()) {
                        final PathTile p = path.getFirst();
                        if (Tiles.getTile(Tiles.decodeType(p.getTile())).isTree()) {
                            ++trees;
                            if (treetilex == -1 && Server.rand.nextInt(10) < trees) {
                                treetilex = p.getTileX();
                                treetiley = p.getTileY();
                            }
                        }
                        distx = Math.pow(p.getTileX() - defender.getCurrentTile().tilex, 2.0);
                        disty = Math.pow(p.getTileY() - defender.getCurrentTile().tiley, 2.0);
                        final double currdist = Math.sqrt(distx + disty);
                        final float currHeight = Math.max(-1.4f, Zones.getLowestCorner(p.getTileX(), p.getTileY(), performer.getLayer()));
                        final double distmod = currdist * dx;
                        if (dx < 0.0) {
                            if (currHeight > targetHeight - distmod) {
                                performer.getCommunicator().sendCombatNormalMessage("You fail to get a clear shot.");
                                return true;
                            }
                        }
                        else if (currHeight > targetHeight - distmod) {
                            performer.getCommunicator().sendCombatNormalMessage("You fail to get a clear shot.");
                            return true;
                        }
                        if (tileArrowDownX == -1 && Server.rand.nextInt(15) == 0) {
                            tileArrowDownX = p.getTileX();
                            tileArrowDownY = p.getTileY();
                        }
                        path.removeFirst();
                    }
                }
                catch (NoPathException np) {
                    performer.getCommunicator().sendCombatNormalMessage("You fail to get a clear shot.");
                    return true;
                }
                if (tileArrowDownX == -1 && result != null && result.getTotalCover() > 0.0f) {
                    for (final Blocker b2 : result.getBlockerArray()) {
                        if (Server.rand.nextInt(100) < b2.getBlockPercent(performer)) {
                            tileArrowDownX = b2.getTileX();
                            tileArrowDownY = b2.getTileY();
                            if (!b2.isHorizontal() && performer.getCurrentTile().tilex < tileArrowDownX) {
                                --tileArrowDownX;
                            }
                            else if (b2.isHorizontal() && performer.getCurrentTile().tiley < tileArrowDownY) {
                                --tileArrowDownY;
                            }
                        }
                    }
                }
                performer.getStatus().modifyStamina(-2000.0f);
                final byte stringql = bow.getAuxData();
                if (Server.rand.nextInt(Math.max(2, stringql) * 10) == 0) {
                    int realTemplate = 459;
                    if (bow.getTemplateId() == 449) {
                        realTemplate = 461;
                    }
                    else if (bow.getTemplateId() == 448) {
                        realTemplate = 460;
                    }
                    bow.setTemplateId(realTemplate);
                    bow.setAuxData((byte)0);
                    performer.getCommunicator().sendCombatNormalMessage("The string breaks!");
                    return true;
                }
                final Item arrow2 = getArrow(performer);
                if (arrow2 == null) {
                    performer.getCommunicator().sendCombatNormalMessage("You have no arrows left to shoot!");
                    return true;
                }
                try {
                    arrow2.getParent().dropItem(arrow2.getWurmId(), false);
                }
                catch (NoSuchItemException nsi) {
                    Items.destroyItem(arrow2.getWurmId());
                    performer.getCommunicator().sendCombatNormalMessage("You have no arrows left to shoot!");
                    return true;
                }
                performer.setSecondsToLogout(300);
                if (!defender.isPlayer()) {
                    performer.setSecondsToLogout(180);
                }
                Arrows.addToHitCreature(arrow2, performer, defender, counter, act, trees, bow, bowskill, archery, isAttackingPenned, tileArrowDownX, tileArrowDownY, treetilex, treetiley, fence, limitFail);
            }
        }
        else {
            performer.getCommunicator().sendCombatNormalMessage("You can't shoot with that.");
            done = true;
        }
        return done;
    }
    
    static int getMinimumRangeForBow(final int bowTemplateId) {
        int minRange = 4;
        if (bowTemplateId == 449) {
            minRange = 40;
        }
        else if (bowTemplateId == 448) {
            minRange = 20;
        }
        else if (bowTemplateId == 447) {
            minRange = 4;
        }
        else if (bowTemplateId == 450) {
            minRange = 20;
        }
        return minRange;
    }
    
    static int getMinimumRangeForBow(final Item bow) {
        assert bow != null : "Bow was null when trying to get minimum range";
        return getMinimumRangeForBow(bow.getTemplateId());
    }
    
    public static final float getRarityArrowModifier(final Item arrow) {
        return 1.0f + arrow.getRarity() * 0.3f;
    }
    
    public static final float getRarityBowModifier(final Item bow) {
        return 1.0f + bow.getRarity() * 0.05f;
    }
    
    public static final double getDamage(final Creature performer, final Creature defender, final Item bow, final Item arrow, final Skill archery) {
        double damage = (bow.getDamagePercent() * arrow.getCurrentQualityLevel() + bow.getDamagePercent() * bow.getCurrentQualityLevel()) / 2.0f + archery.getKnowledge(0.0) * bow.getDamagePercent();
        if (!Servers.isThisAnEpicOrChallengeServer()) {
            damage += bow.getSpellExtraDamageBonus() / 5.0f;
            damage += arrow.getSpellExtraDamageBonus();
        }
        damage += getRarityArrowModifier(arrow);
        damage += getRarityBowModifier(bow);
        damage *= 1.0 + (performer.getStrengthSkill() - 20.0) / 100.0;
        if (Servers.isThisAnEpicOrChallengeServer()) {
            damage *= 1.0f + bow.getSpellExtraDamageBonus() / 30000.0f;
            damage *= 1.0f + arrow.getSpellExtraDamageBonus() / 30000.0f;
        }
        if (defender != null) {
            final float heightDiff = Math.max(-1.4f, performer.getPositionZ() + performer.getAltOffZ()) - Math.max(-1.4f, defender.getPositionZ() + defender.getAltOffZ());
            final float heightDamEffect = Math.max(-0.2f, Math.min(0.2f, heightDiff / 100.0f));
            damage *= 1.0f + heightDamEffect;
            if (Math.max(-1.4f, defender.getPositionZ()) + defender.getCentimetersHigh() / 100.0f < -1.0f) {
                damage *= 0.5;
            }
        }
        if (performer.getFightlevel() >= 1) {
            damage *= 1.2000000476837158;
        }
        if (performer.getFightlevel() >= 4) {
            damage *= 1.2000000476837158;
        }
        if (arrow.getTemplateId() == 456) {
            damage *= 1.2000000476837158;
        }
        else if (arrow.getTemplateId() == 454) {
            damage *= 0.20000000298023224;
        }
        return damage;
    }
    
    public static boolean throwItem(final Creature performer, final Creature defender, final Item thrown, final Action act) {
        if (defender.isInvulnerable()) {
            performer.getCommunicator().sendCombatNormalMessage("You can't attack " + defender.getNameWithGenus() + " right now.");
            return true;
        }
        if (performer.getPositionZ() < -1.0f && performer.getVehicle() == -10L) {
            performer.getCommunicator().sendCombatNormalMessage("You are too deep in the water to throw anything effectively.");
            return true;
        }
        if (thrown.isBodyPartAttached()) {
            performer.getCommunicator().sendCombatNormalMessage("You need to wield a weapon to throw.");
            return true;
        }
        if (thrown.isNoDrop()) {
            performer.getCommunicator().sendCombatNormalMessage("You are not allowed to drop that.");
            return true;
        }
        if (thrown.isArtifact()) {
            performer.getCommunicator().sendCombatNormalMessage("You can't bring yourself to let go.");
            return true;
        }
        if (defender.isDead()) {
            return true;
        }
        Skill weaponSkill = null;
        try {
            final int skillnum = thrown.getPrimarySkill();
            if (skillnum != -10L) {
                try {
                    weaponSkill = performer.getSkills().getSkill(skillnum);
                }
                catch (NoSuchSkillException nss) {
                    weaponSkill = performer.getSkills().learn(skillnum, 1.0f);
                }
            }
        }
        catch (NoSuchSkillException ex) {}
        if (performer.isOnSurface() != defender.isOnSurface()) {
            performer.getCommunicator().sendCombatNormalMessage("You fail to get a clear view of " + defender.getNameWithGenus() + ".");
            return true;
        }
        if (WurmCalendar.getHour() > 20 || WurmCalendar.getHour() < 5) {
            performer.getCommunicator().sendCombatNormalMessage("The dusk makes it harder to get a clear view of " + defender.getNameWithGenus() + ".");
        }
        Zones.resetCoverHolder();
        if (performer.isOnSurface() && !performer.isWithinDistanceTo(defender, 110.0f) && defender.getCurrentTile() != null && defender.getCurrentTile().getStructure() != null && defender.getCurrentTile().getStructure().isFinalFinished()) {
            performer.getCommunicator().sendCombatNormalMessage("You fail to get a clear view of " + defender.getNameWithGenus() + " inside the structure.");
            return true;
        }
        final BlockingResult result = Blocking.getRangedBlockerBetween(performer, defender);
        if (result != null) {
            if (!performer.isOnPvPServer() || !defender.isOnPvPServer()) {
                performer.getCommunicator().sendCombatNormalMessage("You fail to get a clear shot of " + defender.getNameWithGenus() + ".");
                return true;
            }
            for (final Blocker b : result.getBlockerArray()) {
                if (b.getBlockPercent(performer) >= 100.0f) {
                    performer.getCommunicator().sendCombatNormalMessage("You fail to get a clear shot of " + defender.getNameWithGenus() + ".");
                    return true;
                }
            }
        }
        if (!VirtualZone.isCreatureTurnedTowardsTarget(defender, performer, 60.0f, false)) {
            performer.getCommunicator().sendCombatNormalMessage("You must turn towards " + defender.getNameWithGenus() + " in order to throw at it.");
            return true;
        }
        double diff = Creature.getRange(performer, defender.getPosX(), defender.getPosY());
        if (diff < 2.0) {
            performer.getCommunicator().sendCombatNormalMessage(defender.getNameWithGenus() + " is too close.");
            return true;
        }
        if (diff * Math.max(1, thrown.getWeightGrams() / 5000) > performer.getStrengthSkill()) {
            performer.getCommunicator().sendCombatNormalMessage(defender.getNameWithGenus() + " is too far away.");
            return true;
        }
        if (thrown.getWeightGrams() / 1000.0f > performer.getStrengthSkill()) {
            performer.getCommunicator().sendCombatNormalMessage(defender.getNameWithGenus() + " is too heavy.");
            return true;
        }
        final float bon = thrown.getSpellNimbleness();
        if (bon > 0.0f) {
            diff -= bon / 10.0f;
        }
        int trees = 0;
        int treetilex = -1;
        int treetiley = -1;
        int tileArrowDownX = -1;
        int tileArrowDownY = -1;
        if (performer.getCurrentTile() != defender.getCurrentTile()) {
            final PathFinder pf = new PathFinder(true);
            try {
                final Path path = pf.rayCast(performer.getCurrentTile().tilex, performer.getCurrentTile().tiley, defender.getCurrentTile().tilex, defender.getCurrentTile().tiley, performer.isOnSurface(), ((int)Creature.getRange(performer, defender.getPosX(), defender.getPosY()) >> 2) + 5);
                while (!path.isEmpty()) {
                    final PathTile p = path.getFirst();
                    if (Tiles.getTile(Tiles.decodeType(p.getTile())).isTree()) {
                        ++trees;
                        if (treetilex == -1 && Server.rand.nextInt(10) > trees) {
                            treetilex = p.getTileX();
                            treetiley = p.getTileY();
                        }
                    }
                    if (tileArrowDownX == -1 && Server.rand.nextInt(15) == 0) {
                        tileArrowDownX = p.getTileX();
                        tileArrowDownY = p.getTileY();
                    }
                    path.removeFirst();
                }
            }
            catch (NoPathException np) {
                performer.getCommunicator().sendCombatNormalMessage("You fail to get a clear shot.");
                return true;
            }
            if (tileArrowDownX == -1 && result != null && result.getTotalCover() > 0.0f) {
                for (final Blocker b2 : result.getBlockerArray()) {
                    if (Server.rand.nextInt(100) < b2.getBlockPercent(performer)) {
                        tileArrowDownX = b2.getTileX();
                        tileArrowDownY = b2.getTileY();
                        if (!b2.isHorizontal() && performer.getCurrentTile().tilex < tileArrowDownX) {
                            --tileArrowDownX;
                        }
                        else if (b2.isHorizontal() && performer.getCurrentTile().tiley < tileArrowDownY) {
                            --tileArrowDownY;
                        }
                    }
                }
            }
        }
        try {
            thrown.getParent().dropItem(thrown.getWurmId(), false);
        }
        catch (NoSuchItemException nsi) {
            return true;
        }
        performer.setStealth(false);
        if (WurmCalendar.getHour() > 19) {
            diff += WurmCalendar.getHour() - 19;
        }
        else if (WurmCalendar.getHour() < 6) {
            diff += 6 - WurmCalendar.getHour();
        }
        diff += trees;
        diff += Zones.getCoverHolder();
        if (defender.isMoving()) {
            diff += 10.0;
        }
        if (Math.max(0.0f, defender.getPositionZ()) + defender.getCentimetersHigh() / 100.0f < -1.0f) {
            diff += 40.0;
        }
        performer.setSecondsToLogout(300);
        Server.getInstance().broadCastAction(performer.getName() + " throws " + performer.getHisHerItsString() + " " + thrown.getName() + ".", performer, 5);
        performer.getCommunicator().sendCombatNormalMessage("You throw the " + thrown.getName() + ".");
        performer.getStatus().modifyStamina(-5000.0f);
        try {
            final Skill bcontrol = defender.getSkills().getSkill(104);
            diff += bcontrol.getKnowledge(0.0) / 5.0;
        }
        catch (NoSuchSkillException nss2) {
            Archery.logger.log(Level.WARNING, defender.getWurmId() + ", " + defender.getName() + " no body control.");
        }
        diff *= 1.0 - performer.getMovementScheme().armourMod.getModifier();
        double power = 0.0;
        if (weaponSkill != null) {
            power = weaponSkill.skillCheck(diff, thrown, thrown.getCurrentQualityLevel(), true, 1.0f);
        }
        else {
            power = performer.getBodyControl() - Server.rand.nextInt(100);
        }
        double defCheck = 0.0;
        boolean parriedShield = false;
        if (power > 0.0) {
            final Item defShield = defender.getShield();
            if (defShield != null && defender.getStatus().getStamina() >= 300 && willParryWithShield(performer, defender)) {
                Skill defShieldSkill = null;
                final Skills defenderSkills = defender.getSkills();
                int skillnum2 = -10;
                try {
                    skillnum2 = defShield.getPrimarySkill();
                    defShieldSkill = defenderSkills.getSkill(skillnum2);
                }
                catch (NoSuchSkillException nss3) {
                    if (skillnum2 != -10) {
                        defShieldSkill = defenderSkills.learn(skillnum2, 1.0f);
                    }
                }
                if (defShieldSkill != null) {
                    defShieldSkill.skillCheck(20.0 + (defender.isMoving() ? power : Math.max(1.0, power - 20.0)), defShield, 0.0, false, 1.0f);
                    defCheck = defShieldSkill.getKnowledge(defShield, defender.isMoving() ? -20.0 : 0.0) - Server.rand.nextFloat() * 115.0f;
                }
                defCheck += (defShield.getSizeY() + defShield.getSizeZ()) / 3.0f;
                if (defCheck > 0.0) {
                    parriedShield = true;
                }
            }
            defender.addAttacker(performer);
            if (defender.isFriendlyKingdom(performer.getKingdomId())) {
                if (defender.isDominated()) {
                    if (!performer.hasBeenAttackedBy(defender.getWurmId()) && !performer.hasBeenAttackedBy(defender.dominator)) {
                        performer.setUnmotivatedAttacker();
                    }
                }
                else if (defender.isRidden()) {
                    if (performer.getCitizenVillage() == null || defender.getCurrentVillage() != performer.getCitizenVillage()) {
                        for (final Long riderLong : defender.getRiders()) {
                            try {
                                final Creature rider = Server.getInstance().getCreature(riderLong);
                                rider.addAttacker(performer);
                            }
                            catch (NoSuchCreatureException ex2) {}
                            catch (NoSuchPlayerException ex3) {}
                        }
                    }
                }
                else if (!performer.hasBeenAttackedBy(defender.getWurmId()) && (performer.getCitizenVillage() == null || !performer.getCitizenVillage().isEnemy(defender))) {
                    performer.setUnmotivatedAttacker();
                }
            }
            power = Math.max(power, 5.0);
            byte pos = getWoundPos(defender, act.getNumber());
            pos = (byte)CombatEngine.getRealPosition(pos);
            float armourMod = defender.getArmourMod();
            float evasionChance = 0.0f;
            double damage = Weapon.getBaseDamageForWeapon(thrown) * thrown.getCurrentQualityLevel() * 10.0f;
            damage *= 1.0 + (performer.getStrengthSkill() - 20.0) / 100.0;
            if (performer.getFightlevel() >= 1) {
                damage *= 1.5;
            }
            if (performer.getFightlevel() >= 4) {
                damage *= 1.5;
            }
            if (thrown.isLiquid()) {
                damage = Math.max(0, (Math.min(1500, thrown.getTemperature()) - 800) / 200) * thrown.getWeightGrams() / 30.0f;
            }
            Label_2354: {
                if (defCheck > 0.0) {
                    performer.getCommunicator().sendCombatNormalMessage("Your " + thrown.getName() + " glances off " + defender.getNameWithGenus() + "'s shield.");
                    defender.getCommunicator().sendCombatSafeMessage("You instinctively block the " + thrown.getName() + " with your shield.");
                    if (damage > 500.0) {
                        defender.getStatus().modifyStamina(-300.0f);
                    }
                }
                else {
                    if (armourMod != 1.0f && !defender.isVehicle()) {
                        if (!defender.isKingdomGuard()) {
                            break Label_2354;
                        }
                    }
                    try {
                        final byte bodyPosition = ArmourTemplate.getArmourPosition(pos);
                        final Item armour = defender.getArmour(bodyPosition);
                        if (!defender.isKingdomGuard()) {
                            armourMod = ArmourTemplate.calculateDR(armour, (byte)2);
                        }
                        else {
                            armourMod *= ArmourTemplate.calculateDR(armour, (byte)2);
                        }
                        armour.setDamage(armour.getDamage() + (float)(damage * armourMod / 30000.0) * armour.getDamageModifier() * ArmourTemplate.getArmourDamageModFor(armour, (byte)2));
                        CombatEngine.checkEnchantDestruction(thrown, armour, defender);
                        if (defender.getBonusForSpellEffect((byte)22) > 0.0f) {
                            if (armourMod >= 1.0f) {
                                armourMod = 0.2f + (1.0f - defender.getBonusForSpellEffect((byte)22) / 100.0f) * 0.6f;
                            }
                            else {
                                armourMod = Math.min(armourMod, 0.2f + (1.0f - defender.getBonusForSpellEffect((byte)22) / 100.0f) * 0.6f);
                            }
                        }
                    }
                    catch (NoArmourException nsi2) {
                        evasionChance = 1.0f - defender.getArmourMod();
                    }
                    catch (NoSpaceException nsp) {
                        Archery.logger.log(Level.WARNING, defender.getName() + " no armour space on loc " + pos);
                    }
                }
            }
            if (!(defender instanceof Player) && !performer.isInvulnerable()) {
                defender.setTarget(performer.getWurmId(), false);
            }
            if (defender.isUnique()) {
                evasionChance = 0.5f;
                damage *= armourMod;
            }
            boolean dropattile = false;
            if (thrown.getTemplateId() == 105 || thrown.getTemplateId() == 116) {
                performer.achievement(138);
            }
            if (defCheck > 0.0) {
                dropattile = true;
            }
            else if (Server.rand.nextFloat() < evasionChance) {
                dropattile = true;
                performer.getCommunicator().sendCombatNormalMessage("Your " + thrown.getName() + " glances off " + defender.getNameWithGenus() + "'s armour.");
                defender.getCommunicator().sendCombatSafeMessage(LoginHandler.raiseFirstLetter(thrown.getNameWithGenus()) + " hits you on the " + defender.getBody().getWoundLocationString(pos) + " but glances off your armour.");
            }
            else if (damage > 500.0) {
                hit(defender, performer, thrown, null, damage, 1.0f, armourMod, pos, true, false, 0.0, 0.0);
                performer.achievement(41);
            }
            else {
                dropattile = true;
                performer.getCommunicator().sendCombatNormalMessage("Your " + thrown.getName() + " glances off " + defender.getNameWithGenus() + " and does no damage.");
                defender.getCommunicator().sendCombatSafeMessage(LoginHandler.raiseFirstLetter(thrown.getNameWithGenus()) + " hits you on the " + defender.getBody().getWoundLocationString(pos) + " but does no damage.");
            }
            final float fullDamage = thrown.getDamage() + 5.0f * Server.rand.nextFloat();
            if (dropattile) {
                if (thrown.isLiquid()) {
                    Items.destroyItem(thrown.getWurmId());
                    return true;
                }
                if (defCheck > 0.0 && parriedShield) {
                    tileArrowDownX = defender.getCurrentTile().tilex;
                    tileArrowDownY = defender.getCurrentTile().tiley;
                    if (defShield.isWood()) {
                        SoundPlayer.playSound("sound.arrow.hit.wood", defender, 1.6f);
                    }
                    else if (defShield.isMetal()) {
                        SoundPlayer.playSound("sound.arrow.hit.metal", defender, 1.6f);
                    }
                }
                if (!thrown.isIndestructible() && Server.rand.nextInt(Math.max(1, (int)thrown.getCurrentQualityLevel())) < 2) {
                    for (final Item item : thrown.getAllItems(false)) {
                        try {
                            final Zone z = Zones.getZone(defender.getCurrentTile().tilex, defender.getCurrentTile().tiley, performer.isOnSurface());
                            final VolaTile t = z.getOrCreateTile(defender.getCurrentTile().tilex, defender.getCurrentTile().tiley);
                            t.addItem(item, false, false);
                        }
                        catch (NoSuchZoneException nsz) {
                            performer.getCommunicator().sendCombatNormalMessage("The " + item.getName() + " disappears from your view.");
                            Items.destroyItem(item.getWurmId());
                        }
                    }
                    performer.getCommunicator().sendCombatNormalMessage("The " + thrown.getName() + " breaks.");
                    Items.destroyItem(thrown.getWurmId());
                }
                else if (fullDamage < 100.0f || thrown.isIndestructible() || thrown.isLocked()) {
                    tileArrowDownX = defender.getCurrentTile().tilex;
                    tileArrowDownY = defender.getCurrentTile().tiley;
                    try {
                        final Zone z2 = Zones.getZone(tileArrowDownX, tileArrowDownY, performer.isOnSurface());
                        final VolaTile t2 = z2.getOrCreateTile(tileArrowDownX, tileArrowDownY);
                        t2.addItem(thrown, false, false);
                    }
                    catch (NoSuchZoneException nsz2) {
                        performer.getCommunicator().sendCombatNormalMessage("The " + thrown.getName() + " disappears from your view.");
                        Items.destroyItem(thrown.getWurmId());
                    }
                }
                else {
                    Items.destroyItem(thrown.getWurmId());
                    performer.getCommunicator().sendCombatNormalMessage("The " + thrown.getName() + " breaks.");
                    for (final Item item : thrown.getAllItems(false)) {
                        try {
                            final Zone z = Zones.getZone(defender.getCurrentTile().tilex, defender.getCurrentTile().tiley, performer.isOnSurface());
                            final VolaTile t = z.getOrCreateTile(defender.getCurrentTile().tilex, defender.getCurrentTile().tiley);
                            t.addItem(item, false, false);
                        }
                        catch (NoSuchZoneException nsz) {
                            performer.getCommunicator().sendCombatNormalMessage("The " + item.getName() + " disappears from your view.");
                            Items.destroyItem(item.getWurmId());
                        }
                    }
                }
            }
        }
        else {
            if (thrown.isLiquid()) {
                Items.destroyItem(thrown.getWurmId());
                return true;
            }
            final float fullDamage2 = thrown.getDamage() + 5.0f * Server.rand.nextFloat();
            if (!thrown.isIndestructible() && !thrown.isLocked() && Server.rand.nextInt(Math.max(1, (int)thrown.getCurrentQualityLevel())) < 2) {
                performer.getCommunicator().sendCombatNormalMessage("The " + thrown.getName() + " breaks.");
                for (final Item item2 : thrown.getAllItems(false)) {
                    try {
                        final Zone z3 = Zones.getZone(defender.getCurrentTile().tilex, defender.getCurrentTile().tiley, performer.isOnSurface());
                        final VolaTile t3 = z3.getOrCreateTile(defender.getCurrentTile().tilex, defender.getCurrentTile().tiley);
                        t3.addItem(item2, false, false);
                    }
                    catch (NoSuchZoneException nsz3) {
                        performer.getCommunicator().sendCombatNormalMessage("The " + item2.getName() + " disappears from your view.");
                        Items.destroyItem(item2.getWurmId());
                    }
                }
                Items.destroyItem(thrown.getWurmId());
            }
            else if (fullDamage2 >= 100.0f && !thrown.isIndestructible() && !thrown.isLocked()) {
                for (final Item item2 : thrown.getAllItems(false)) {
                    try {
                        final Zone z3 = Zones.getZone(defender.getCurrentTile().tilex, defender.getCurrentTile().tiley, performer.isOnSurface());
                        final VolaTile t3 = z3.getOrCreateTile(defender.getCurrentTile().tilex, defender.getCurrentTile().tiley);
                        t3.addItem(item2, false, false);
                    }
                    catch (NoSuchZoneException nsz3) {
                        performer.getCommunicator().sendCombatNormalMessage("The " + item2.getName() + " disappears from your view.");
                        Items.destroyItem(item2.getWurmId());
                    }
                }
                thrown.setDamage(fullDamage2);
                performer.getCommunicator().sendCombatNormalMessage("The " + thrown.getName() + " breaks.");
            }
            else {
                if (trees > 0 && treetilex > 0) {
                    tileArrowDownX = treetilex;
                    tileArrowDownY = treetiley;
                }
                boolean hitdef = false;
                if (tileArrowDownX == -1) {
                    if (defender.opponent != null && performer.getKingdomId() == defender.opponent.getKingdomId() && power < -20.0 && Server.rand.nextInt(100) < Math.abs(power) && defender.opponent.isPlayer() && defender.opponent != performer && performer.getFightlevel() < 1) {
                        byte pos2 = getWoundPos(defender.opponent, act.getNumber());
                        pos2 = (byte)CombatEngine.getRealPosition(pos2);
                        float armourMod2 = defender.opponent.getArmourMod();
                        double damage = Weapon.getBaseDamageForWeapon(thrown) * thrown.getCurrentQualityLevel() * 10.0f;
                        damage *= 1.0 + (performer.getStrengthSkill() - 20.0) / 100.0;
                        damage *= 1.0f + thrown.getCurrentQualityLevel() / 100.0f;
                        if (armourMod2 == 1.0f) {
                            try {
                                final byte bodyPosition = ArmourTemplate.getArmourPosition(pos2);
                                final Item armour = defender.opponent.getArmour(bodyPosition);
                                armourMod2 = ArmourTemplate.calculateDR(armour, (byte)2);
                                armour.setDamage(armour.getDamage() + (float)(damage * armourMod2 / 50000.0) * armour.getDamageModifier() * ArmourTemplate.getArmourDamageModFor(armour, (byte)2));
                                CombatEngine.checkEnchantDestruction(thrown, armour, defender.opponent);
                            }
                            catch (NoArmourException ex4) {}
                            catch (NoSpaceException nsp) {
                                Archery.logger.log(Level.WARNING, defender.getName() + " no armour space on loc " + pos2);
                            }
                        }
                        hit(defender.opponent, performer, thrown, null, damage, 1.0f, armourMod2, pos2, false, false, 0.0, 0.0);
                        hitdef = true;
                    }
                    if (!hitdef) {
                        tileArrowDownX = defender.getCurrentTile().tilex;
                        tileArrowDownY = defender.getCurrentTile().tiley;
                    }
                }
                if (!hitdef) {
                    try {
                        final Zone z4 = Zones.getZone(tileArrowDownX, tileArrowDownY, performer.isOnSurface());
                        final VolaTile t4 = z4.getOrCreateTile(tileArrowDownX, tileArrowDownY);
                        t4.addItem(thrown, false, false);
                        if (treetilex > 0) {
                            SoundPlayer.playSound("sound.arrow.stuck.wood", tileArrowDownX, tileArrowDownY, performer.isOnSurface(), 0.0f);
                        }
                        else if (result != null) {
                            if (result.getFirstBlocker() != null) {
                                if (result.getFirstBlocker().isFence()) {
                                    SoundPlayer.playSound("sound.work.masonry", tileArrowDownX, tileArrowDownY, performer.isOnSurface(), 0.0f);
                                }
                                else {
                                    SoundPlayer.playSound("sound.arrow.stuck.wood", tileArrowDownX, tileArrowDownY, performer.isOnSurface(), 0.0f);
                                }
                            }
                        }
                        else {
                            SoundPlayer.playSound("sound.arrow.stuck.ground", tileArrowDownX, tileArrowDownY, performer.isOnSurface(), 0.0f);
                        }
                    }
                    catch (NoSuchZoneException nsz4) {
                        Items.destroyItem(thrown.getWurmId());
                        performer.getCommunicator().sendCombatNormalMessage("The " + thrown.getName() + " disappears from your view");
                    }
                }
            }
        }
        return true;
    }
    
    public static boolean hit(final Creature defender, final Creature performer, final Item arrow, @Nullable final Item bow, double damage, final float damMod, final float armourMod, final byte pos, final boolean intentional, final boolean dryRun, final double diff, final double bonus) {
        if (defender.isDead()) {
            return true;
        }
        boolean done = false;
        final ArrayList<MulticolorLineSegment> segments = new ArrayList<MulticolorLineSegment>();
        segments.add(new CreatureLineSegment(performer));
        segments.add(new MulticolorLineSegment("'s ", (byte)7));
        segments.add(new MulticolorLineSegment(" " + arrow.getName() + " hits ", (byte)7));
        segments.add(new CreatureLineSegment(defender));
        segments.add(new MulticolorLineSegment(" in the " + defender.getBody().getWoundLocationString(pos) + ".", (byte)7));
        defender.getCommunicator().sendColoredMessageCombat(segments);
        segments.get(1).setText("r");
        for (final MulticolorLineSegment s : segments) {
            s.setColor((byte)3);
        }
        performer.getCommunicator().sendColoredMessageCombat(segments);
        final Battle battle = performer.getBattle();
        final float spdamb = arrow.getSpellDamageBonus();
        final float rotdam = arrow.getWeaponSpellDamageBonus();
        if (!arrow.isIndestructible() && (arrow.isLiquid() || Server.rand.nextInt(Math.max(1, (int)arrow.getCurrentQualityLevel())) < 2)) {
            Items.destroyItem(arrow.getWurmId());
            defender.getCommunicator().sendNormalServerMessage("The " + arrow.getName() + " breaks!");
            performer.getCommunicator().sendNormalServerMessage("The " + arrow.getName() + " breaks!");
        }
        else if (!arrow.setDamage(arrow.getDamage() + 5.0f * damMod)) {
            defender.getInventory().insertItem(arrow, true);
            arrow.setBusy(false);
        }
        damage = Math.min(1.2 * damage, Math.max(0.800000011920929 * damage, (Server.rand.nextFloat() + Server.rand.nextFloat()) * damage));
        final float ms = arrow.getSpellMindStealModifier();
        if (ms > 0.0f && !defender.isPlayer() && defender.getKingdomId() != performer.getKingdomId()) {
            final Skills s2 = defender.getSkills();
            final int r = Server.rand.nextInt(s2.getSkills().length);
            final Skill toSteal = s2.getSkills()[r];
            double mod = 1.0;
            if (damage < 5000.0) {
                mod = damage / 5000.0;
            }
            final double skillStolen = ms / 100.0f * 0.1f * mod;
            try {
                final Skill owned = defender.getSkills().getSkill(toSteal.getNumber());
                if (owned.getKnowledge() < toSteal.getKnowledge()) {
                    toSteal.setKnowledge(toSteal.getKnowledge() - skillStolen / 10.0, false);
                    owned.setKnowledge(owned.getKnowledge() + skillStolen / 10.0, false);
                    performer.getCommunicator().sendSafeServerMessage("The " + arrow.getName() + " steals some " + toSteal.getName() + ".");
                }
            }
            catch (NoSuchSkillException ex) {}
        }
        byte wtype = 2;
        if (arrow.isLiquid()) {
            wtype = 4;
        }
        boolean dead = false;
        float poisdam = arrow.getSpellVenomBonus();
        if (poisdam > 0.0f) {
            final float half = Math.max(1.0f, poisdam / 2.0f);
            poisdam = half + Server.rand.nextInt((int)half);
            wtype = 5;
            damage *= 0.800000011920929;
        }
        if (bow != null && damage * armourMod > 500.0 && bow.isWeaponBow()) {
            Skill archery = null;
            Skill bowskill = null;
            try {
                final int skillnum = bow.getPrimarySkill();
                if (skillnum != -10L) {
                    try {
                        bowskill = performer.getSkills().getSkill(skillnum);
                    }
                    catch (NoSuchSkillException nss) {
                        bowskill = performer.getSkills().learn(skillnum, 1.0f);
                    }
                }
            }
            catch (NoSuchSkillException nss2) {
                performer.getCommunicator().sendCombatNormalMessage("This weapon has no skill attached. Please report this bug using the forums or the /dev channel.");
                return true;
            }
            try {
                archery = performer.getSkills().getSkill(1030);
            }
            catch (NoSuchSkillException nss2) {
                archery = performer.getSkills().learn(1030, 1.0f);
            }
            if (bowskill == null || archery == null) {
                performer.getCommunicator().sendCombatNormalMessage("There was a bug with the skill for this item. Please report this bug using the forums or the /dev channel.");
                return true;
            }
            bowskill.skillCheck(diff, bow, arrow.getCurrentQualityLevel(), dryRun || arrow.getTemplateId() == 454, (float)damage / 500.0f);
        }
        if (rotdam > 0.0f) {
            dead = CombatEngine.addWound(performer, defender, wtype, pos, damage + damage * rotdam / 500.0, armourMod, "hit", battle, Server.rand.nextInt((int)Math.max(1.0f, rotdam)), poisdam, true, false, false, false);
        }
        else {
            dead = CombatEngine.addWound(performer, defender, wtype, pos, damage, armourMod, "hit", battle, 0.0f, poisdam, true, false, false, false);
            if (arrow.getSpellLifeTransferModifier() > 0.0f && damage * arrow.getSpellLifeTransferModifier() / 100.0 > 500.0 && performer.getBody() != null && performer.getBody().getWounds() != null) {
                final Wound[] w = performer.getBody().getWounds().getWounds();
                if (w.length > 0) {
                    w[0].modifySeverity(-(int)(damage * arrow.getSpellLifeTransferModifier() / ((performer.getCultist() != null && performer.getCultist().healsFaster()) ? 250.0f : 500.0f)));
                }
            }
        }
        if (!dead && spdamb > 0.0f && spdamb / 300.0f * damage > 500.0) {
            dead = defender.addWoundOfType(performer, (byte)4, pos, false, armourMod, false, spdamb / 300.0f * damage, 0.0f, 0.0f, false, true);
        }
        if (!dead && arrow.getSpellFrostDamageBonus() > 0.0f && arrow.getSpellFrostDamageBonus() / 300.0f * damage > 500.0) {
            dead = defender.addWoundOfType(performer, (byte)8, pos, false, armourMod, false, arrow.getSpellFrostDamageBonus() / 300.0f * damage, 0.0f, 0.0f, false, true);
        }
        if (!Players.getInstance().isOverKilling(performer.getWurmId(), defender.getWurmId())) {
            if (arrow.getSpellExtraDamageBonus() > 0.0f) {
                if (defender.isPlayer() && !defender.isNewbie()) {
                    final SpellEffect speff = arrow.getSpellEffect((byte)45);
                    double gainMod = 1.0;
                    if (damage < 5000.0) {
                        gainMod = damage / 5000.0;
                    }
                    if (speff != null) {
                        speff.setPower(Math.min(10000.0f, speff.power + (float)(dead ? 50.0 : (10.0 * gainMod))));
                    }
                }
                else if (!defender.isPlayer() && !defender.isGuard() && dead) {
                    final SpellEffect speff = arrow.getSpellEffect((byte)45);
                    float gainMod2 = 1.0f;
                    if (speff.getPower() > 5000.0f && !Servers.isThisAnEpicOrChallengeServer()) {
                        gainMod2 = Math.max(0.5f, 1.0f - (speff.getPower() - 5000.0f) / 5000.0f);
                    }
                    if (speff != null) {
                        speff.setPower(Math.min(10000.0f, speff.power + defender.getBaseCombatRating() * gainMod2));
                    }
                }
            }
            if (bow != null && bow.getSpellExtraDamageBonus() > 0.0f) {
                if (defender.isPlayer() && !defender.isNewbie()) {
                    final SpellEffect speff = bow.getSpellEffect((byte)45);
                    double gainMod = 1.0;
                    if (damage * armourMod < 5000.0) {
                        gainMod = damage * armourMod / 5000.0;
                    }
                    if (speff != null) {
                        speff.setPower(Math.min(10000.0f, speff.power + (float)(dead ? 30.0 : (2.0 * gainMod))));
                    }
                }
                else if (!defender.isPlayer() && !defender.isGuard() && dead) {
                    final SpellEffect speff = bow.getSpellEffect((byte)45);
                    float gainMod2 = 1.0f;
                    if (speff.getPower() > 5000.0f && !Servers.isThisAnEpicOrChallengeServer()) {
                        gainMod2 = Math.max(0.5f, 1.0f - (speff.getPower() - 5000.0f) / 5000.0f);
                    }
                    if (speff != null) {
                        speff.setPower(Math.min(10000.0f, speff.power + defender.getBaseCombatRating() * gainMod2));
                    }
                }
            }
        }
        if (dead) {
            performer.getCommunicator().sendCombatSafeMessage(defender.getNameWithGenus() + " is dead!");
            if (battle != null) {
                battle.addCasualty(performer, defender);
            }
            performer.getCombatHandler().setKillEffects(performer, defender);
            done = true;
        }
        else {
            SoundPlayer.playSound(defender.getHitSound(), defender, 1.6f);
            if (!defender.isPlayer() && defender.isTypeFleeing()) {
                defender.setFleeCounter(20, true);
                Server.getInstance().broadCastAction(defender.getNameWithGenus() + " panics.", defender, 10);
            }
        }
        return done;
    }
    
    public static boolean isTargetTurnedTowardsCreature(final Creature performer, final Item target) {
        return VirtualZone.isItemTurnedTowardsCreature(performer, target, 60.0f);
    }
    
    public static boolean attack(final Creature performer, final Item target, final Item bow, final float counter, final Action act) {
        boolean done = false;
        final boolean boat = target.isBoat();
        if (!target.isDecoration() && !boat) {
            performer.getCommunicator().sendNormalServerMessage("You can't attack " + target.getNameWithGenus() + ".");
            return true;
        }
        if (performer.getPositionZ() < -0.5 && performer.getVehicle() == -10L) {
            performer.getCommunicator().sendNormalServerMessage("You are too deep in the water to fire a bow.");
            return true;
        }
        if (bow.isWeaponBow()) {
            final int minRange = getMinimumRangeForBow(bow);
            if (Creature.getRange(performer, target.getPosX(), target.getPosY()) < minRange) {
                performer.getCommunicator().sendNormalServerMessage(LoginHandler.raiseFirstLetter(target.getName()) + " is too close.");
                return true;
            }
            Skill bowskill = null;
            try {
                final int skillnum = bow.getPrimarySkill();
                if (skillnum != -10L) {
                    try {
                        bowskill = performer.getSkills().getSkill(skillnum);
                    }
                    catch (NoSuchSkillException nss) {
                        bowskill = performer.getSkills().learn(skillnum, 1.0f);
                    }
                }
            }
            catch (NoSuchSkillException nss2) {
                performer.getCommunicator().sendNormalServerMessage("This weapon has no skill attached. Please report this bug using the forums or the /dev channel.");
                return true;
            }
            if (bowskill == null) {
                performer.getCommunicator().sendNormalServerMessage("There was a bug with the skill for this item. Please report this bug using the forums or the /dev channel.");
                return true;
            }
            if (!isTargetTurnedTowardsCreature(performer, target) && !boat) {
                performer.getCommunicator().sendNormalServerMessage("You must position yourself in front of the " + target.getName() + " in order to shoot at it.");
                return true;
            }
            if (!VirtualZone.isCreatureTurnedTowardsItem(target, performer, 60.0f)) {
                performer.getCommunicator().sendCombatNormalMessage("You must turn towards the " + target.getName() + " in order to shoot at it.");
                return true;
            }
            if (performer.attackingIntoIllegalDuellingRing(target.getTileX(), target.getTileY(), target.isOnSurface())) {
                performer.getCommunicator().sendNormalServerMessage("The duelling ring is holy ground and you may not attack across the border.");
                return true;
            }
            if (act.justTickedSecond()) {
                performer.decreaseFatigue();
            }
            int time = 200;
            if (counter == 1.0f) {
                if (performer.isOnSurface() != target.isOnSurface()) {
                    performer.getCommunicator().sendNormalServerMessage("You fail to get a clear shot of the " + target.getName() + ".");
                    return true;
                }
                final Item arrow = getArrow(performer);
                if (arrow == null) {
                    performer.getCommunicator().sendNormalServerMessage("You have no arrows left to shoot!");
                    return true;
                }
                time = Actions.getQuickActionTime(performer, bowskill, bow, 0.0);
                if (act.getNumber() == 125 && time > 30) {
                    time = (int)Math.max(30.0f, time * 0.8f);
                }
                performer.getCommunicator().sendNormalServerMessage("You start aiming at " + target.getNameWithGenus() + ".");
                if (WurmCalendar.getHour() > 20 || WurmCalendar.getHour() < 5) {
                    performer.getCommunicator().sendNormalServerMessage("The dusk makes it harder to get a clear view of the " + target.getName() + ".");
                }
                performer.sendActionControl(Actions.actionEntrys[act.getNumber()].getVerbString(), true, time);
                Server.getInstance().broadCastAction(performer.getName() + " draws an arrow and raises " + performer.getHisHerItsString() + " bow.", performer, 5);
                act.setTimeLeft(time);
                SoundPlayer.playSound("sound.arrow.aim", performer, 1.6f);
            }
            else {
                time = act.getTimeLeft();
            }
            if (counter * 10.0f > time) {
                done = true;
                performer.getStatus().modifyStamina(-1000.0f);
                if (performer.isOnSurface() != target.isOnSurface()) {
                    performer.getCommunicator().sendNormalServerMessage("You fail to get a clear shot of the " + target.getName() + ".");
                    return true;
                }
                Zones.resetCoverHolder();
                final int minRange2 = getMinimumRangeForBow(bow);
                if (Creature.getRange(performer, target.getPosX(), target.getPosY()) < minRange2) {
                    performer.getCommunicator().sendNormalServerMessage(target.getName() + " is too close.");
                    return true;
                }
                int trees = 0;
                int treetilex = -1;
                int treetiley = -1;
                int tileArrowDownX = -1;
                int tileArrowDownY = -1;
                final PathFinder pf = new PathFinder(true);
                try {
                    final Path path = pf.rayCast(performer.getCurrentTile().tilex, performer.getCurrentTile().tiley, target.getTileX(), target.getTileY(), performer.isOnSurface(), ((int)Creature.getRange(performer, target.getPosX(), target.getPosY()) >> 2) + 5);
                    while (!path.isEmpty()) {
                        final PathTile p = path.getFirst();
                        if (Tiles.getTile(Tiles.decodeType(p.getTile())).isTree()) {
                            ++trees;
                            if (treetilex == -1 && Server.rand.nextInt(10) > trees) {
                                treetilex = p.getTileX();
                                treetiley = p.getTileY();
                            }
                        }
                        if (tileArrowDownX == -1 && Server.rand.nextInt(15) == 0) {
                            tileArrowDownX = p.getTileX();
                            tileArrowDownY = p.getTileY();
                        }
                        path.removeFirst();
                    }
                }
                catch (NoPathException np) {
                    performer.getCommunicator().sendNormalServerMessage("You fail to get a clear shot.");
                    return true;
                }
                final byte stringql = bow.getAuxData();
                if (Server.rand.nextInt(Math.max(2, stringql) * 10) == 0) {
                    int realTemplate = 459;
                    if (bow.getTemplateId() == 449) {
                        realTemplate = 461;
                    }
                    else if (bow.getTemplateId() == 448) {
                        realTemplate = 460;
                    }
                    bow.setTemplateId(realTemplate);
                    bow.setAuxData((byte)0);
                    performer.getCommunicator().sendNormalServerMessage("The string breaks!");
                    return true;
                }
                double diff = getBaseDifficulty(act.getNumber());
                if (WurmCalendar.getHour() > 19) {
                    diff += WurmCalendar.getHour() - 19;
                }
                else if (WurmCalendar.getHour() < 6) {
                    diff += 6 - WurmCalendar.getHour();
                }
                diff += trees;
                final float bon = bow.getSpellNimbleness();
                if (bon > 0.0f) {
                    diff -= bon / 10.0f;
                }
                final double deviation = getRangeDifficulty(performer, bow.getTemplateId(), target.getPosX(), target.getPosY());
                diff += deviation;
                if (target.isBoat()) {
                    diff += 30.0;
                }
                final Item arrow2 = getArrow(performer);
                if (arrow2 == null) {
                    performer.getCommunicator().sendNormalServerMessage("You have no arrows left to shoot!");
                    return true;
                }
                try {
                    diff -= bow.getRarity();
                    diff -= arrow2.getRarity();
                    arrow2.getParent().dropItem(arrow2.getWurmId(), false);
                }
                catch (NoSuchItemException nsi) {
                    Items.destroyItem(arrow2.getWurmId());
                    performer.getCommunicator().sendNormalServerMessage("You have no arrows left to shoot!");
                    return true;
                }
                Arrows.addToHitItem(arrow2, performer, target, counter, bowskill, bow, tileArrowDownX, tileArrowDownY, deviation, diff, trees, treetilex, treetiley);
            }
        }
        else {
            done = true;
            performer.getCommunicator().sendNormalServerMessage("You can't shoot with that.");
        }
        return done;
    }
    
    public static final boolean mayTakeDamage(final Item target) {
        return !target.isStone() && !target.isMetal() && !target.isBodyPart() && !target.isTemporary() && target.getTemplateId() != 272 && (!target.isLockable() || target.getLockId() == -10L) && !target.isIndestructible() && !target.isHugeAltar() && !target.isDomainItem() && !target.isKingdomMarker() && !target.isTraded() && !target.isBanked() && !target.isArtifact();
    }
    
    public static Item getArrow(final Creature performer) {
        Item quiver = performer.getBody().getBodyItem().findFirstContainedItem(462);
        if (quiver == null || quiver.isEmpty(false)) {
            quiver = performer.getInventory().findItem(462, true);
        }
        Item arrow = null;
        if (quiver != null) {
            arrow = quiver.findFirstContainedItem(456);
            if (arrow == null) {
                arrow = quiver.findFirstContainedItem(455);
            }
            if (arrow == null) {
                arrow = quiver.findFirstContainedItem(454);
            }
        }
        else {
            arrow = performer.getBody().getBodyItem().findFirstContainedItem(456);
            if (arrow == null) {
                arrow = performer.getBody().getBodyItem().findFirstContainedItem(455);
            }
            if (arrow == null) {
                arrow = performer.getBody().getBodyItem().findFirstContainedItem(454);
            }
            if (arrow == null) {
                arrow = performer.getInventory().findItem(456, true);
            }
            if (arrow == null) {
                arrow = performer.getInventory().findItem(455, true);
            }
            if (arrow == null) {
                arrow = performer.getInventory().findItem(454, true);
            }
        }
        return arrow;
    }
    
    public static boolean isArchery(final int action) {
        return action == 124 || action == 125 || action == 128 || action == 131 || action == 129 || action == 130 || action == 126 || action == 127;
    }
    
    public static final double getBaseDifficulty(final int action) {
        if (action == 124) {
            return 20.0;
        }
        if (action == 125) {
            return 30.0;
        }
        if (action == 128) {
            return 35.0;
        }
        if (action == 129) {
            return 40.0;
        }
        if (action == 130) {
            return 40.0;
        }
        if (action == 126) {
            return 70.0;
        }
        if (action == 127) {
            return 70.0;
        }
        if (action == 131) {
            return 70.0;
        }
        if (action == 134) {
            return 1.0;
        }
        return 99.0;
    }
    
    public static final byte getWoundPos(final Creature defender, final int action) {
        Label_0060: {
            if (action != 124) {
                if (action != 125) {
                    break Label_0060;
                }
            }
            try {
                return defender.getBody().getRandomWoundPos();
            }
            catch (Exception ex) {
                Archery.logger.log(Level.WARNING, defender + ", " + ex.getMessage(), ex);
                return 2;
            }
        }
        if (action == 128) {
            return 2;
        }
        if (action == 131) {
            return 34;
        }
        if (action == 129) {
            return 3;
        }
        if (action == 130) {
            return 4;
        }
        if (action == 126) {
            return 1;
        }
        if (action == 127) {
            return 29;
        }
        return 2;
    }
    
    public static final double getRangeDifficulty(final Creature performer, final int itemTemplateId, final float targetX, final float targetY) {
        double perfectDist = 30.0;
        if (itemTemplateId == 447) {
            perfectDist = 20.0;
        }
        else if (itemTemplateId == 448) {
            perfectDist = 40.0;
        }
        else if (itemTemplateId == 449) {
            perfectDist = 80.0;
        }
        final double range = Creature.getRange(performer, targetX, targetY);
        double deviation = Math.abs(perfectDist - range);
        deviation += range / 5.0;
        if (deviation < 1.0) {
            deviation = 1.0;
        }
        return deviation;
    }
    
    public static final boolean willParryWithShield(final Creature archer, final Creature shieldbearer) {
        return VirtualZone.isCreatureTurnedTowardsTarget(archer, shieldbearer, 90.0f, true);
    }
    
    static {
        logger = Logger.getLogger(Archery.class.getName());
        Archery.fnd = false;
    }
}
