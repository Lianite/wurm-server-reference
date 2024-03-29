// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.behaviours;

import java.util.concurrent.ConcurrentHashMap;
import com.wurmonline.server.combat.Battle;
import com.wurmonline.server.combat.CombatEngine;
import com.wurmonline.server.items.FragmentUtilities;
import com.wurmonline.server.spells.SpellEffect;
import com.wurmonline.server.items.ItemSpellEffects;
import com.wurmonline.server.items.InscriptionData;
import com.wurmonline.server.questions.VillageMessagePopup;
import com.wurmonline.server.questions.VillageMessageBoard;
import com.wurmonline.server.skills.SkillTemplate;
import com.wurmonline.server.skills.AffinitiesTimed;
import java.util.ArrayList;
import com.wurmonline.server.items.Ingredient;
import com.wurmonline.server.skills.Skills;
import com.wurmonline.server.items.RecipesByPlayer;
import javax.annotation.Nullable;
import com.wurmonline.server.villages.DeadVillage;
import com.wurmonline.server.creatures.Delivery;
import java.util.HashMap;
import com.wurmonline.server.items.CreationRequirement;
import com.wurmonline.shared.exceptions.WurmServerException;
import com.wurmonline.server.items.ItemTemplate;
import com.wurmonline.server.effects.Effect;
import com.wurmonline.server.items.CreationEntry;
import com.wurmonline.server.spells.Spells;
import com.wurmonline.server.questions.WishQuestion;
import com.wurmonline.server.endgames.EndGameItem;
import com.wurmonline.server.endgames.EndGameItems;
import com.wurmonline.server.Constants;
import com.wurmonline.server.questions.AppointmentsQuestion;
import com.wurmonline.server.questions.AchievementCreation;
import com.wurmonline.server.questions.MissionManager;
import com.wurmonline.server.combat.Archery;
import com.wurmonline.server.items.ItemTemplateFactory;
import com.wurmonline.server.items.ItemMealData;
import com.wurmonline.server.skills.SkillSystem;
import com.wurmonline.server.items.Materials;
import com.wurmonline.server.creatures.Wagoner;
import com.wurmonline.server.highways.MethodsHighways;
import com.wurmonline.server.NoSuchEntryException;
import com.wurmonline.server.effects.EffectFactory;
import com.wurmonline.server.items.WurmColor;
import com.wurmonline.server.items.CreationMatrix;
import com.wurmonline.server.questions.SinglePriceManageQuestion;
import com.wurmonline.server.utils.NameCountList;
import com.wurmonline.server.creatures.Brand;
import com.wurmonline.server.deities.Deity;
import com.wurmonline.server.ServerEntry;
import java.util.Set;
import com.wurmonline.server.players.PlayerInfo;
import com.wurmonline.server.kingdom.GuardTower;
import com.wurmonline.server.items.Recipe;
import com.wurmonline.server.villages.RecruitmentAd;
import com.wurmonline.server.creatures.Communicator;
import com.wurmonline.server.questions.LCMManagementQuestion;
import com.wurmonline.server.questions.SwapDeityQuestion;
import com.wurmonline.server.players.Permissions;
import com.wurmonline.server.questions.AbdicationQuestion;
import com.wurmonline.server.questions.KingdomHistory;
import com.wurmonline.server.questions.KingdomStatusQuestion;
import com.wurmonline.server.WurmId;
import com.wurmonline.shared.util.MaterialUtilities;
import com.wurmonline.server.skills.NoSuchSkillException;
import com.wurmonline.server.creatures.Traits;
import com.wurmonline.shared.util.StringUtilities;
import com.wurmonline.server.villages.NoSuchVillageException;
import com.wurmonline.server.questions.PermissionsHistory;
import com.wurmonline.server.players.PermissionsPlayerList;
import com.wurmonline.server.questions.ManagePermissions;
import com.wurmonline.server.questions.ManageObjectList;
import com.wurmonline.server.questions.ConchQuestion;
import com.wurmonline.server.questions.KingdomFoundationQuestion;
import com.wurmonline.server.questions.GroupCAHelpQuestion;
import com.wurmonline.server.questions.InGameVoteSetupQuestion;
import com.wurmonline.server.questions.FeatureManagement;
import com.wurmonline.server.questions.GmInterface;
import com.wurmonline.server.questions.KingdomMembersQuestion;
import com.wurmonline.server.economy.Economy;
import com.wurmonline.server.GeneralUtilities;
import com.wurmonline.server.players.Achievements;
import com.wurmonline.server.questions.AffinityQuestion;
import com.wurmonline.server.players.Abilities;
import com.wurmonline.server.questions.ChangeNameQuestion;
import com.wurmonline.server.questions.PortalQuestion;
import com.wurmonline.server.questions.ChangeAppearanceQuestion;
import com.wurmonline.server.FailedException;
import com.wurmonline.server.items.NoSuchTemplateException;
import com.wurmonline.server.sounds.SoundPlayer;
import com.wurmonline.server.items.ItemFactory;
import com.wurmonline.server.questions.TextInputQuestion;
import com.wurmonline.server.questions.ChangeMedPathQuestion;
import com.wurmonline.server.questions.CultQuestion;
import com.wurmonline.server.players.Cults;
import com.wurmonline.server.questions.GmVillageAdInterface;
import com.wurmonline.server.utils.StringUtil;
import com.wurmonline.server.statistics.ChallengePointEnum;
import com.wurmonline.server.epic.MissionHelper;
import com.wurmonline.shared.util.MovementChecker;
import com.wurmonline.server.players.PlayerInfoFactory;
import com.wurmonline.server.NoSuchPlayerException;
import java.io.IOException;
import com.wurmonline.server.Players;
import com.wurmonline.server.tutorial.PlayerTutorial;
import com.wurmonline.server.items.Recipes;
import com.wurmonline.server.players.Player;
import com.wurmonline.server.skills.Skill;
import com.wurmonline.shared.constants.StructureConstantsEnum;
import com.wurmonline.server.structures.Fence;
import com.wurmonline.server.kingdom.Kingdoms;
import com.wurmonline.server.spells.Spell;
import com.wurmonline.server.items.RuneUtilities;
import com.wurmonline.server.Features;
import com.wurmonline.server.WurmCalendar;
import com.wurmonline.server.tutorial.MissionTrigger;
import com.wurmonline.server.structures.Structure;
import com.wurmonline.server.zones.VolaTile;
import com.wurmonline.server.zones.Zone;
import com.wurmonline.server.structures.BlockingResult;
import java.util.Iterator;
import com.wurmonline.server.villages.Citizen;
import com.wurmonline.server.villages.Village;
import com.wurmonline.server.creatures.Creatures;
import java.util.Collections;
import com.wurmonline.server.items.NotOwnedException;
import com.wurmonline.server.tutorial.MissionTriggers;
import com.wurmonline.server.creatures.NoSuchCreatureException;
import com.wurmonline.server.players.Cultist;
import com.wurmonline.server.kingdom.King;
import com.wurmonline.server.zones.NoSuchZoneException;
import com.wurmonline.server.zones.Zones;
import com.wurmonline.server.structures.Blocking;
import com.wurmonline.server.NoSuchItemException;
import java.util.logging.Level;
import com.wurmonline.server.LoginHandler;
import com.wurmonline.server.Items;
import com.wurmonline.server.villages.RecruitmentAds;
import com.wurmonline.server.villages.Villages;
import com.wurmonline.mesh.Tiles;
import com.wurmonline.server.Server;
import com.wurmonline.server.Servers;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import com.wurmonline.server.items.Item;
import com.wurmonline.server.creatures.Creature;
import java.util.Random;
import java.util.Map;
import java.util.logging.Logger;
import com.wurmonline.shared.constants.ItemMaterials;
import com.wurmonline.server.economy.MonetaryConstants;
import com.wurmonline.server.TimeConstants;
import com.wurmonline.server.questions.QuestionTypes;
import com.wurmonline.server.MiscConstants;
import com.wurmonline.server.items.ItemTypes;

public class ItemBehaviour extends Behaviour implements ItemTypes, MiscConstants, QuestionTypes, TimeConstants, MonetaryConstants, ItemMaterials
{
    private static final Logger logger;
    public static final Map<Long, Long> conquers;
    private static final Random recipeRandom;
    private final float gemChance = 1.0f;
    private final float barChance = 10.0f;
    private final float imbuePotionChance = 2.0f;
    private final float supremeBoneChance = 0.1f;
    private final float transRodChance = 0.05f;
    private final float staffLandChance = 0.01f;
    private final float paleMaskChance = 0.01f;
    private final float plumedHelmChance = 0.025f;
    private final float cavalierHelmChance = 0.1f;
    private final float challMaskChance = 0.1f;
    private final float challStatueChance = 0.5f;
    private final float shatterProtPotionChance = 1.0f;
    
    ItemBehaviour() {
        super((short)1);
    }
    
    ItemBehaviour(final short type) {
        super(type);
    }
    
    @Override
    public List<ActionEntry> getBehavioursFor(final Creature performer, final Item target) {
        final List<ActionEntry> toReturn = new LinkedList<ActionEntry>();
        toReturn.addAll(super.getBehavioursFor(performer, target));
        final int tid = target.getTemplateId();
        if (!target.isTraded()) {
            if (target.canBePlanted()) {
                final long ownerId = target.getOwnerId();
                if (ownerId == -10L) {
                    if (performer.isWithinDistanceTo(target.getPosX(), target.getPosY(), target.getPosZ(), 4.0f)) {
                        final boolean isPvpServer = Servers.isThisAPvpServer();
                        if (MethodsItems.checkIfStealing(target, performer, null)) {
                            if (isPvpServer) {
                                toReturn.add(Actions.actionEntrys[100]);
                            }
                        }
                        else if (target.isKingdomFlag() && target.getAuxData() != performer.getKingdomId()) {
                            if (isSignManipulationOk(target, performer, (short)6)) {
                                toReturn.add(Actions.actionEntrys[6]);
                            }
                            else if (isPvpServer) {
                                toReturn.add(Actions.actionEntrys[100]);
                            }
                        }
                        else if (((!target.isBulkContainer() && target.getTemplate().getInitialContainers() == null) || target.isEmpty(true)) && !target.isNoTake(performer)) {
                            if (isSignManipulationOk(target, performer, (short)6)) {
                                toReturn.add(Actions.actionEntrys[6]);
                                if ((target.getParentId() == -10L && tid != 26) || tid != 298) {
                                    toReturn.add(Actions.actionEntrys[925]);
                                }
                            }
                            else if (isPvpServer) {
                                toReturn.add(Actions.actionEntrys[100]);
                            }
                        }
                        if (!target.isPlanted() && target.getParentId() == -10L && !target.isRoadMarker()) {
                            toReturn.add(new ActionEntry((short)176, "Secure", "securing the " + target.getName()));
                        }
                    }
                }
                else if (ownerId == performer.getWurmId() && !target.isRoadMarker()) {
                    if (target.getTemplateId() == 1396) {
                        int placedTile;
                        if (performer.isOnSurface()) {
                            placedTile = Server.surfaceMesh.getTile(performer.getTileX(), performer.getTileY());
                        }
                        else {
                            placedTile = Server.caveMesh.getTile(performer.getTileX(), performer.getTileY());
                        }
                        if (Tiles.decodeHeight(placedTile) < 0) {
                            toReturn.add(Actions.actionEntrys[176]);
                        }
                    }
                    else if (!target.isAbility()) {
                        toReturn.add(Actions.actionEntrys[176]);
                    }
                }
                if (target.getTemplateId() == 835) {
                    final Village village = Villages.getVillageForCreature(performer);
                    if (village != null) {
                        if (!RecruitmentAds.containsAdForVillage(village.getId())) {
                            final Citizen cit = village.getCitizen(performer.getWurmId());
                            if (cit != null && cit.getRole().mayInviteCitizens()) {
                                toReturn.add(Actions.actionEntrys[598]);
                            }
                        }
                        else {
                            final Citizen cit = village.getCitizen(performer.getWurmId());
                            if (cit != null && cit.getRole().mayInviteCitizens()) {
                                toReturn.add(Actions.actionEntrys[602]);
                                toReturn.add(Actions.actionEntrys[603]);
                            }
                        }
                    }
                    else {
                        toReturn.add(Actions.actionEntrys[601]);
                    }
                }
                if (target.getTemplateId() == 1271 && target.mayAccessHold(performer)) {
                    toReturn.add(new ActionEntry((short)17, "Read messages", "reading messages"));
                }
            }
            try {
                if (target.getOwner() == performer.getWurmId()) {
                    if ((target.isLight() || target.isFire()) && target.isOnFire() && !target.isAlwaysLit()) {
                        if (target.getTemplateId() == 729) {
                            toReturn.add(new ActionEntry((short)53, "Blow out", "blowing out", new int[] { 0, 43 }));
                        }
                        else {
                            toReturn.add(Actions.actionEntrys[53]);
                        }
                    }
                    try {
                        final Item p = Items.getItem(target.getTopParent());
                        if (p != null) {
                            if (target.isHollow() && !target.isSealedByPlayer() && target.getTemplateId() != 1342 && (target.getTopParent() == performer.getInventory().getWurmId() || p.isBodyPart())) {
                                final long lockId = target.getLockId();
                                if (lockId == -10L) {
                                    toReturn.add(Actions.actionEntrys[568]);
                                }
                                else {
                                    try {
                                        final Item lock = Items.getItem(lockId);
                                        if (performer.hasKeyForLock(lock) || target.isOwner(performer)) {
                                            toReturn.add(Actions.actionEntrys[568]);
                                            if (target.isOwner(performer)) {
                                                toReturn.add(new ActionEntry((short)(-1), LoginHandler.raiseFirstLetter(target.getActualName()), target.getActualName()));
                                                toReturn.add(Actions.actionEntrys[102]);
                                            }
                                        }
                                        else if (target.mayAccessHold(performer)) {
                                            toReturn.add(Actions.actionEntrys[568]);
                                        }
                                    }
                                    catch (NoSuchItemException nse) {
                                        ItemBehaviour.logger.log(Level.WARNING, "No lock with id " + lockId + ", although the item has that.");
                                    }
                                }
                            }
                            else if (target.getTemplateId() == 94 || target.getTemplateId() == 152 || target.getTemplateId() == 780 || target.getTemplateId() == 95 || target.getTemplateId() == 150 || target.getTemplateId() == 96 || target.getTemplateId() == 151) {
                                toReturn.add(Actions.actionEntrys[939]);
                            }
                        }
                    }
                    catch (NoSuchItemException ex2) {}
                    toReturn.addAll(AutoEquipMethods.getBehaviours(target, performer));
                    addCreationWindowOption(performer, target, toReturn);
                    if (target.isRoyal() && (tid == 536 || tid == 530 || tid == 533)) {
                        toReturn.add(Actions.actionEntrys[355]);
                        toReturn.add(Actions.actionEntrys[356]);
                        toReturn.add(Actions.actionEntrys[358]);
                    }
                    if (target.isLiquid()) {
                        toReturn.add(new ActionEntry((short)(-1), "Pour", "pouring", new int[0]));
                        toReturn.add(new ActionEntry((short)7, "On ground", "pouring", new int[0]));
                    }
                    else {
                        final int templateId = target.getTemplateId();
                        if (templateId == 26 || templateId == 298) {
                            toReturn.add(new ActionEntry((short)(-2), "Drop", "dropping", new int[0]));
                            toReturn.add(new ActionEntry((short)7, "On ground", "dropping", new int[0]));
                            toReturn.add(Actions.actionEntrys[638]);
                        }
                        else if (!target.isComponentItem()) {
                            toReturn.add(new ActionEntry((short)(-2), "Drop", "dropping", new int[0]));
                            toReturn.add(new ActionEntry((short)7, "On ground", "dropping", new int[0]));
                            toReturn.add(Actions.actionEntrys[925]);
                        }
                    }
                    if (!target.isNoRename() && (!target.isVehicle() || target.isChair() || target.isTent())) {
                        toReturn.add(Actions.actionEntrys[59]);
                    }
                    if (tid == 175 || tid == 651 || tid == 1097 || tid == 1098 || (tid == 466 && target.getAuxData() == 1)) {
                        toReturn.add(Actions.actionEntrys[3]);
                    }
                    else if (tid == 782) {
                        toReturn.add(Actions.actionEntrys[518]);
                    }
                    else if (tid == 1172) {
                        toReturn.add(new ActionEntry((short)(-13), "Set volume to", "setting", new int[0]));
                        toReturn.add(Actions.actionEntrys[737]);
                        toReturn.add(Actions.actionEntrys[736]);
                        toReturn.add(Actions.actionEntrys[735]);
                        toReturn.add(Actions.actionEntrys[734]);
                        toReturn.add(Actions.actionEntrys[733]);
                        toReturn.add(Actions.actionEntrys[732]);
                        toReturn.add(Actions.actionEntrys[731]);
                        toReturn.add(Actions.actionEntrys[730]);
                        toReturn.add(Actions.actionEntrys[729]);
                        toReturn.add(Actions.actionEntrys[728]);
                        toReturn.add(Actions.actionEntrys[727]);
                        toReturn.add(Actions.actionEntrys[726]);
                        toReturn.add(Actions.actionEntrys[725]);
                    }
                    else if (tid == 200 || tid == 1192 || tid == 69 || tid == 66 || tid == 68 || tid == 29 || tid == 32) {
                        toReturn.add(Actions.actionEntrys[936]);
                    }
                    else if (tid == 479) {
                        toReturn.add(Actions.actionEntrys[937]);
                    }
                    if (tid == 176) {
                        if (performer.getPower() >= 2) {
                            toReturn.add(Actions.actionEntrys[244]);
                            toReturn.add(Actions.actionEntrys[503]);
                            toReturn.add(Actions.actionEntrys[719]);
                        }
                        if (performer.getPower() >= 3) {
                            short nos = -5;
                            if (Servers.isThisLoginServer()) {
                                --nos;
                            }
                            if (Servers.isThisLoginServer()) {
                                --nos;
                            }
                            toReturn.add(new ActionEntry(nos, "Server", "Server stuff", ItemBehaviour.emptyIntArr));
                            toReturn.add(Actions.actionEntrys[184]);
                            toReturn.add(Actions.actionEntrys[195]);
                            toReturn.add(Actions.actionEntrys[194]);
                            toReturn.add(Actions.actionEntrys[212]);
                            toReturn.add(Actions.actionEntrys[481]);
                            if (Servers.isThisLoginServer()) {
                                toReturn.add(Actions.actionEntrys[609]);
                            }
                            if (Servers.isThisLoginServer()) {
                                toReturn.add(Actions.actionEntrys[635]);
                            }
                        }
                        else if (WurmPermissions.maySetFaith(performer)) {
                            toReturn.add(Actions.actionEntrys[212]);
                        }
                    }
                    if (tid == 315) {
                        if (performer.getPower() >= 2) {
                            toReturn.add(Actions.actionEntrys[244]);
                            toReturn.add(Actions.actionEntrys[503]);
                        }
                    }
                    else if (tid == 682) {
                        if (Servers.localServer.PVPSERVER) {
                            toReturn.add(Actions.actionEntrys[480]);
                        }
                    }
                    else if (tid == 1024) {
                        toReturn.add(Actions.actionEntrys[115]);
                    }
                    if (!target.isFullprice()) {
                        toReturn.add(new ActionEntry((short)(-2), "Prices", "Prices", ItemBehaviour.emptyIntArr));
                        toReturn.add(Actions.actionEntrys[86]);
                        toReturn.add(Actions.actionEntrys[87]);
                    }
                    else {
                        toReturn.add(new ActionEntry((short)(-1), "Prices", "Prices", ItemBehaviour.emptyIntArr));
                        toReturn.add(Actions.actionEntrys[87]);
                    }
                    if (target.isEgg()) {
                        toReturn.add(Actions.actionEntrys[328]);
                        if (tid == 465) {
                            toReturn.add(Actions.actionEntrys[330]);
                        }
                    }
                    if (!target.isLiquid() && target.isWrapped()) {
                        toReturn.add(new ActionEntry((short)740, "Unwrap", "unwrapping", ItemBehaviour.emptyIntArr));
                    }
                    else if (target.isRaw() && target.canBeRawWrapped() && target.isPStateNone()) {
                        toReturn.add(new ActionEntry((short)739, "Wrap", "wrapping", ItemBehaviour.emptyIntArr));
                    }
                    if (tid == 1101) {
                        toReturn.add(Actions.actionEntrys[183]);
                    }
                    else if (target.isFood()) {
                        if (!target.isNoEatOrDrink()) {
                            toReturn.add(Actions.actionEntrys[19]);
                            toReturn.add(Actions.actionEntrys[182]);
                        }
                    }
                    else if (target.isLiquid()) {
                        if (!target.isNoEatOrDrink() && !target.isUndistilled() && target.isDrinkable()) {
                            toReturn.add(Actions.actionEntrys[19]);
                            toReturn.add(Actions.actionEntrys[183]);
                        }
                    }
                    else if ((target.isRepairable() || tid == 179 || tid == 386) && (!target.isKingdomMarker() || performer.isFriendlyKingdom(target.getAuxData()))) {
                        toReturn.add(Actions.actionEntrys[162]);
                    }
                    if (target.isContainerLiquid() && target.getItemCount() == 1) {
                        for (final Item i : target.getItems()) {
                            if (!i.isNoEatOrDrink() && !i.isUndistilled() && i.isDrinkable() && !i.isNoEatOrDrink() && !i.isUndistilled() && i.isDrinkable() && !target.isSealedByPlayer()) {
                                toReturn.add(Actions.actionEntrys[19]);
                                toReturn.add(Actions.actionEntrys[183]);
                                break;
                            }
                        }
                    }
                    if (target.isWeaponBow()) {
                        toReturn.add(Actions.actionEntrys[133]);
                    }
                    else if (target.isGem()) {
                        if (target.getData1() > 0) {
                            toReturn.add(Actions.actionEntrys[118]);
                        }
                    }
                    else if (tid == 233) {
                        toReturn.add(Actions.actionEntrys[682]);
                    }
                    else if (tid == 781 || tid == 843 || tid == 1300) {
                        if (target.getOwnerId() != -10L) {
                            toReturn.add(Actions.actionEntrys[118]);
                        }
                    }
                    else if (tid == 719) {
                        toReturn.add(Actions.actionEntrys[118]);
                    }
                    else if (target.isServerPortal()) {
                        toReturn.add(Actions.actionEntrys[118]);
                    }
                    else if (tid == 602) {
                        toReturn.add(Actions.actionEntrys[118]);
                    }
                    else if (target.isDeathProtection() || tid == 527 || tid == 5 || tid == 834 || tid == 836) {
                        toReturn.add(Actions.actionEntrys[118]);
                    }
                    else if (target.isAbility() && !target.canBePlanted()) {
                        toReturn.add(Actions.actionEntrys[118]);
                    }
                    else if (target.getTemplateId() == 1438) {
                        toReturn.add(new ActionEntry((short)118, "Claim affinity", "claiming"));
                    }
                    if (target.isInstaDiscard()) {
                        toReturn.add(Actions.actionEntrys[600]);
                    }
                }
                else if (!target.isNoRename() && performer.getPower() >= 2 && (!target.isVehicle() || target.isChair() || target.isTent())) {
                    toReturn.add(Actions.actionEntrys[59]);
                }
            }
            catch (NotOwnedException ex) {
                if (!target.isBanked()) {
                    float maxDist = 6.0f;
                    if (target.isVehicle()) {
                        final Vehicle vehicle = Vehicles.getVehicle(target);
                        if (vehicle != null) {
                            maxDist = Math.max(maxDist, vehicle.getMaxAllowedLoadDistance());
                        }
                    }
                    if (target.getTopParent() == performer.getVehicle() || performer.isWithinDistanceTo(target.getPosX(), target.getPosY(), target.getPosZ(), maxDist)) {
                        final BlockingResult result = (target.getWurmId() == performer.getVehicle()) ? null : Blocking.getBlockerBetween(performer, target, 4);
                        if (result == null) {
                            boolean sameStruct = true;
                            try {
                                final Zone tzone = Zones.getZone((int)target.getPosX() >> 2, (int)target.getPosY() >> 2, target.isOnSurface());
                                final VolaTile tile = tzone.getTileOrNull((int)target.getPosX() >> 2, (int)target.getPosY() >> 2);
                                if (tile != null) {
                                    final Structure struct = tile.getStructure();
                                    final VolaTile tile2 = performer.getCurrentTile();
                                    if (tile2 != null) {
                                        if (tile2.getStructure() != struct) {
                                            if (struct != null && struct.isTypeHouse()) {
                                                sameStruct = false;
                                            }
                                            if (tile2.getStructure() != null && tile2.getStructure().isTypeHouse()) {
                                                sameStruct = false;
                                            }
                                        }
                                    }
                                    else if (struct != null && struct.isTypeHouse()) {
                                        sameStruct = false;
                                    }
                                }
                            }
                            catch (NoSuchZoneException ex3) {}
                            if (sameStruct) {
                                if (target.isLight() || target.isFire() || target.getTemplateId() == 1243) {
                                    if (target.isOnFire()) {
                                        if (!target.isAlwaysLit()) {
                                            if (target.getTemplateId() == 729) {
                                                toReturn.add(new ActionEntry((short)53, "Blow out", "blowing out", new int[] { 0, 43 }));
                                            }
                                            else {
                                                toReturn.add(Actions.actionEntrys[53]);
                                            }
                                        }
                                    }
                                    else if (target.getTemplateId() == 1396) {
                                        if (target.getAuxData() > 0 && target.isPlanted()) {
                                            toReturn.add(Actions.actionEntrys[12]);
                                        }
                                    }
                                    else if ((target.isLight() || target.getTemplateId() == 742) && (target.getTemplateId() != 729 || target.getAuxData() > 0)) {
                                        toReturn.add(Actions.actionEntrys[12]);
                                    }
                                }
                                if (tid == 538 && !Servers.localServer.isChallengeServer()) {
                                    toReturn.add(Actions.actionEntrys[353]);
                                }
                                else if (tid == 726) {
                                    if (performer.getKingdomId() == target.getAuxData()) {
                                        final King k = King.getKing(target.getAuxData());
                                        if (k != null) {
                                            if (k.mayBeChallenged()) {
                                                toReturn.add(Actions.actionEntrys[488]);
                                            }
                                            else if (k.hasFailedAllChallenges()) {
                                                toReturn.add(Actions.actionEntrys[487]);
                                            }
                                        }
                                    }
                                    if (performer.getPower() >= 3) {
                                        toReturn.add(Actions.actionEntrys[118]);
                                    }
                                }
                                else if (target.isMeditation()) {
                                    short nums = -1;
                                    final Cultist c = Cultist.getCultist(performer.getWurmId());
                                    if (c != null) {
                                        --nums;
                                        if (c.getLevel() > 2) {
                                            --nums;
                                        }
                                        if (c.getPath() == 4) {
                                            --nums;
                                        }
                                    }
                                    toReturn.add(new ActionEntry(nums, "Nature", "meditation"));
                                    toReturn.add(Actions.actionEntrys[384]);
                                    if (nums < -1) {
                                        toReturn.add(Actions.actionEntrys[385]);
                                    }
                                    if (nums < -2 && c.getLevel() > 2) {
                                        toReturn.add(Actions.actionEntrys[386]);
                                    }
                                    if (nums < -2 && c.getPath() == 4) {
                                        toReturn.add(Actions.actionEntrys[722]);
                                    }
                                }
                                toReturn.addAll(AutoEquipMethods.getBehaviours(target, performer));
                                addCreationWindowOption(performer, target, toReturn);
                                if (tid == 652) {
                                    toReturn.add(Actions.actionEntrys[214]);
                                }
                                if (!target.isNoTake(performer) && !target.isOutsideOnly() && !target.canBePlanted() && !target.isLiquid() && !target.isBulkContainer()) {
                                    if (MethodsItems.checkIfStealing(target, performer, null)) {
                                        toReturn.add(Actions.actionEntrys[100]);
                                    }
                                    else {
                                        toReturn.add(Actions.actionEntrys[6]);
                                        if ((target.getParentId() == -10L && tid != 26) || tid != 298) {
                                            toReturn.add(Actions.actionEntrys[925]);
                                        }
                                    }
                                }
                                if (target.isHollow() && !target.isSealedByPlayer() && (target.getTemplateId() != 1342 || target.isPlanted()) && (!target.getTemplate().hasViewableSubItems() || target.getTemplate().isContainerWithSubItems() || performer.getPower() > 0)) {
                                    final boolean isTop = target.getWurmId() == target.getTopParent() || (target.getTopParentOrNull() != null && target.getTopParentOrNull().getTemplate().hasViewableSubItems() && (!target.getTopParentOrNull().getTemplate().isContainerWithSubItems() || target.isPlacedOnParent()));
                                    Label_4664: {
                                        if ((target.getLockId() != -10L || !isTop) && (!target.isDraggable() || !MethodsItems.mayUseInventoryOfVehicle(performer, target))) {
                                            if (target.getTemplateId() != 850 || !MethodsItems.mayUseInventoryOfVehicle(performer, target)) {
                                                if (target.getLockId() != -10L) {
                                                    try {
                                                        final Item lock2 = Items.getItem(target.getLockId());
                                                        boolean hasKey = performer.hasKeyForLock(lock2) || target.isOwner(performer);
                                                        if (target.mayAccessHold(performer)) {
                                                            hasKey = true;
                                                        }
                                                        if (hasKey) {
                                                            try {
                                                                final Creature[] watchers = target.getWatchers();
                                                                boolean watching = false;
                                                                for (final Creature lWatcher : watchers) {
                                                                    if (lWatcher == performer) {
                                                                        watching = true;
                                                                        break;
                                                                    }
                                                                }
                                                                if (watching) {
                                                                    toReturn.add(Actions.actionEntrys[4]);
                                                                }
                                                                else {
                                                                    toReturn.add(Actions.actionEntrys[3]);
                                                                }
                                                            }
                                                            catch (NoSuchCreatureException nsc) {
                                                                toReturn.add(Actions.actionEntrys[3]);
                                                            }
                                                            if (target.isOwner(performer) && target.isLocked()) {
                                                                toReturn.add(new ActionEntry((short)(-1), LoginHandler.raiseFirstLetter(target.getActualName()), target.getActualName()));
                                                                toReturn.add(Actions.actionEntrys[102]);
                                                            }
                                                        }
                                                    }
                                                    catch (NoSuchItemException ex4) {}
                                                }
                                                break Label_4664;
                                            }
                                        }
                                        try {
                                            final Creature[] watchers2 = target.getWatchers();
                                            boolean watching2 = false;
                                            for (final Creature lWatcher2 : watchers2) {
                                                if (lWatcher2 == performer) {
                                                    watching2 = true;
                                                    break;
                                                }
                                            }
                                            if (watching2) {
                                                toReturn.add(Actions.actionEntrys[4]);
                                            }
                                            else if (target.getTemplateId() == 272 && target.getWasBrandedTo() != -10L) {
                                                if (target.mayCommand(performer)) {
                                                    toReturn.add(Actions.actionEntrys[3]);
                                                }
                                            }
                                            else {
                                                toReturn.add(Actions.actionEntrys[3]);
                                            }
                                        }
                                        catch (NoSuchCreatureException nsc2) {
                                            if (target.getTemplateId() == 272 && target.getWasBrandedTo() != -10L) {
                                                if (target.mayCommand(performer)) {
                                                    toReturn.add(Actions.actionEntrys[3]);
                                                }
                                            }
                                            else {
                                                toReturn.add(Actions.actionEntrys[3]);
                                            }
                                        }
                                        if (target.isOwner(performer) && target.isLocked()) {
                                            toReturn.add(new ActionEntry((short)(-1), LoginHandler.raiseFirstLetter(target.getActualName()), target.getActualName()));
                                            toReturn.add(Actions.actionEntrys[102]);
                                        }
                                    }
                                    if (target.isMailBox()) {
                                        if (target.isEmpty(false)) {
                                            toReturn.add(Actions.actionEntrys[336]);
                                        }
                                        else {
                                            toReturn.add(Actions.actionEntrys[337]);
                                        }
                                    }
                                }
                                if (target.getTemplateId() != 272 || target.getWasBrandedTo() == -10L || target.mayCommand(performer)) {
                                    toReturn.addAll(this.makeMoveSubMenu(performer, target));
                                }
                                if (target.isServerPortal()) {
                                    toReturn.add(Actions.actionEntrys[118]);
                                }
                                else if (tid == 972) {
                                    toReturn.add(new ActionEntry((short)118, "Pat", "patting"));
                                }
                                else if (tid == 738 || tid == 741) {
                                    toReturn.add(Actions.actionEntrys[118]);
                                }
                                else if (tid == 739 || target.isWarTarget()) {
                                    toReturn.add(Actions.actionEntrys[504]);
                                }
                                else if (tid == 722) {
                                    toReturn.add(Actions.actionEntrys[118]);
                                }
                                if (target.isDraggable()) {
                                    boolean ok = true;
                                    if (target.isVehicle()) {
                                        final Vehicle vehic = Vehicles.getVehicle(target);
                                        if (vehic.pilotId != -10L) {
                                            ok = false;
                                        }
                                        if (vehic.draggers != null && !vehic.draggers.isEmpty()) {
                                            ok = false;
                                        }
                                    }
                                    if (performer.getVehicle() != -10L) {
                                        ok = false;
                                    }
                                    if (ok && !Items.isItemDragged(target) && target.getTopParent() == target.getWurmId()) {
                                        final boolean havePermission = VehicleBehaviour.hasPermission(performer, target);
                                        if (havePermission || target.mayDrag(performer)) {
                                            toReturn.add(Actions.actionEntrys[74]);
                                        }
                                    }
                                    else if (performer.getDraggedItem() == target) {
                                        toReturn.add(Actions.actionEntrys[75]);
                                    }
                                }
                                if (target.isBed()) {
                                    this.addBedOptions(performer, target, toReturn);
                                }
                                if (target.isFood()) {
                                    if (!target.isNoEatOrDrink()) {
                                        toReturn.add(Actions.actionEntrys[19]);
                                        toReturn.add(Actions.actionEntrys[182]);
                                    }
                                }
                                else if (target.isLiquid()) {
                                    if (!target.isNoEatOrDrink() && !target.isUndistilled() && target.isDrinkable()) {
                                        toReturn.add(Actions.actionEntrys[19]);
                                        toReturn.add(Actions.actionEntrys[183]);
                                    }
                                }
                                else if ((target.isRepairable() || tid == 179 || tid == 386) && (!target.isKingdomMarker() || performer.isFriendlyKingdom(target.getAuxData()))) {
                                    toReturn.add(Actions.actionEntrys[162]);
                                }
                                if (target.isContainerLiquid() && target.getItemCount() == 1) {
                                    for (final Item j : target.getItems()) {
                                        if (!j.isNoEatOrDrink() && !j.isUndistilled() && j.isDrinkable() && !j.isNoEatOrDrink() && !j.isUndistilled() && j.isDrinkable() && !target.isSealedByPlayer()) {
                                            toReturn.add(Actions.actionEntrys[19]);
                                            toReturn.add(Actions.actionEntrys[183]);
                                            break;
                                        }
                                    }
                                }
                                if (target.isWeaponBow()) {
                                    toReturn.add(Actions.actionEntrys[133]);
                                }
                                if (tid == 442) {
                                    toReturn.add(new ActionEntry((short)91, "Taste the julbord", "eating"));
                                }
                                if (target.isEgg() && tid == 465) {
                                    toReturn.add(Actions.actionEntrys[330]);
                                }
                                if ((target.lastOwner == performer.getWurmId() || performer.getPower() >= 2 || target.isShelf()) && !target.isNoRename() && (!target.isVehicle() || target.isChair() || target.isTent())) {
                                    if (target.isShelf() && performer.getPower() < 1 && target.getLastOwnerId() != performer.getWurmId()) {
                                        final Item outerParent = target.getOuterItemOrNull();
                                        if (outerParent != null && outerParent.mayManage(performer)) {
                                            toReturn.add(Actions.actionEntrys[59]);
                                        }
                                    }
                                    else if (performer.getPower() >= 2 || target.getLastOwnerId() == performer.getWurmId()) {
                                        toReturn.add(Actions.actionEntrys[59]);
                                    }
                                }
                                if (target.isEpicTargetItem() || target.isKingdomMarker()) {
                                    final MissionTrigger[] mr1 = MissionTriggers.getMissionTriggersWith(-1, 501, target.getWurmId());
                                    if (mr1.length > 0) {
                                        toReturn.add(Actions.actionEntrys[501]);
                                    }
                                    final MissionTrigger[] mr2 = MissionTriggers.getMissionTriggersWith(-1, 496, target.getWurmId());
                                    if (mr2.length > 0) {
                                        toReturn.add(Actions.actionEntrys[496]);
                                    }
                                    final MissionTrigger[] mr3 = MissionTriggers.getMissionTriggersWith(-1, 498, target.getWurmId());
                                    if (mr3.length > 0) {
                                        toReturn.add(Actions.actionEntrys[498]);
                                    }
                                    final MissionTrigger[] mr4 = MissionTriggers.getMissionTriggersWith(-1, 500, target.getWurmId());
                                    if (mr4.length > 0) {
                                        toReturn.add(Actions.actionEntrys[500]);
                                    }
                                    final MissionTrigger[] mr5 = MissionTriggers.getMissionTriggersWith(-1, 502, target.getWurmId());
                                    if (mr5.length > 0) {
                                        toReturn.add(Actions.actionEntrys[502]);
                                    }
                                    final MissionTrigger[] mr6 = MissionTriggers.getMissionTriggersWith(-1, 497, target.getWurmId());
                                    if (mr6.length > 0) {
                                        toReturn.add(Actions.actionEntrys[497]);
                                    }
                                    final MissionTrigger[] mr7 = MissionTriggers.getMissionTriggersWith(-1, 499, target.getWurmId());
                                    if (mr7.length > 0) {
                                        toReturn.add(Actions.actionEntrys[499]);
                                    }
                                }
                            }
                        }
                    }
                }
                toReturn.addAll(CargoTransportationMethods.getLoadUnloadActions(performer, target));
            }
            if (target.isCrate() && target.isSealedByPlayer() && target.getLastOwnerId() == performer.getWurmId()) {
                toReturn.add(Actions.actionEntrys[740]);
            }
            if (target.canHavePermissions()) {
                final List<ActionEntry> permissions = new LinkedList<ActionEntry>();
                if (target.isBed() && target.mayManage(performer)) {
                    permissions.add(new ActionEntry((short)688, "Manage Bed", "managing"));
                }
                else if (target.getTemplateId() == 1271 && target.mayManage(performer)) {
                    permissions.add(new ActionEntry((short)688, "Manage Message Board", "managing"));
                }
                else if (!target.isVehicle() && target.mayManage(performer)) {
                    permissions.add(Actions.actionEntrys[688]);
                }
                if (target.isBed() && (target.isOwner(performer) || performer.getPower() > 1)) {
                    permissions.add(new ActionEntry((short)691, "History Of Bed", "viewing"));
                }
                else if (target.getTemplateId() == 1271 && (target.isOwner(performer) || performer.getPower() > 1)) {
                    permissions.add(new ActionEntry((short)691, "History Of Message Board", "viewing"));
                }
                else if (!target.isVehicle() && (target.isOwner(performer) || performer.getPower() > 1)) {
                    permissions.add(new ActionEntry((short)691, "History Of Item", "viewing"));
                }
                if (!permissions.isEmpty()) {
                    Collections.sort(permissions);
                    toReturn.add(new ActionEntry((short)(-permissions.size()), "Permissions", "viewing"));
                    toReturn.addAll(permissions);
                }
            }
            if (target.isSealedByPlayer() && target.getTemplateId() != 1309 && !target.isCrate()) {
                Item liquid = null;
                for (final Item item : target.getItemsAsArray()) {
                    if (item.isLiquid()) {
                        liquid = item;
                        break;
                    }
                }
                if (liquid == null || !liquid.isFermenting()) {
                    toReturn.add(Actions.actionEntrys[740]);
                }
                toReturn.add(Actions.actionEntrys[19]);
            }
            if ((target.isFoodMaker() || target.getTemplate().isCooker()) && !target.isSealedByPlayer()) {
                toReturn.add(Actions.actionEntrys[285]);
            }
        }
        else {
            toReturn.add(new ActionEntry((short)(-1), "Prices", "Prices"));
            toReturn.add(Actions.actionEntrys[87]);
        }
        if (tid == 257) {
            toReturn.add(Actions.actionEntrys[79]);
        }
        if (target.getTemplateId() == 1310 && target.getData() != -10L) {
            try {
                final Creature cagedCreature = Creatures.getInstance().getCreature(target.getData());
                if (cagedCreature != null && !cagedCreature.isPlayer() && !cagedCreature.isHuman() && !cagedCreature.isGhost() && !cagedCreature.isReborn() && (!cagedCreature.isCaredFor() || cagedCreature.getCareTakerId() == performer.getWurmId())) {
                    toReturn.add(Actions.actionEntrys[493]);
                }
            }
            catch (NoSuchCreatureException ex5) {}
        }
        Behaviour.addEmotes(toReturn);
        return toReturn;
    }
    
    private static void addLockOptions(final Creature performer, final Item source, final Item target, final long lockId, final int stid, final boolean isTop, final List<ActionEntry> toReturn, final Creature[] watchers) {
        if (lockId != -10L) {
            boolean addedOpen = false;
            final boolean test1 = target.isDraggable() && MethodsItems.mayUseInventoryOfVehicle(performer, target);
            final boolean test2 = target.getTemplateId() == 850 && MethodsItems.mayUseInventoryOfVehicle(performer, target);
            if (isTop && (test1 || test2)) {
                toReturn.add(Actions.actionEntrys[3]);
                addedOpen = true;
            }
            try {
                final Item lock = Items.getItem(lockId);
                if (performer.hasKeyForLock(lock) || target.isOwner(performer)) {
                    if (isTop && !addedOpen) {
                        toReturn.add(Actions.actionEntrys[3]);
                    }
                    if (target.isOwner(performer)) {
                        int sz = -1;
                        if (source.isLock()) {
                            --sz;
                        }
                        toReturn.add(new ActionEntry((short)sz, LoginHandler.raiseFirstLetter(target.getActualName()), target.getActualName()));
                        if (source.isLock()) {
                            toReturn.add(new ActionEntry((short)78, "Replace lock", "replacing lock"));
                        }
                        toReturn.add(Actions.actionEntrys[102]);
                    }
                }
                else if (target.mayAccessHold(performer) && isTop && !addedOpen) {
                    toReturn.add(Actions.actionEntrys[3]);
                }
                if (stid == 463) {
                    addLockPickEntry(performer, source, target, lock, toReturn);
                }
            }
            catch (NoSuchItemException nsi) {
                ItemBehaviour.logger.log(Level.WARNING, "No lock with id " + lockId + ", although the item has that.");
                if (source.isLock() && target.isOwner(performer)) {
                    toReturn.add(new ActionEntry((short)(-1), LoginHandler.raiseFirstLetter(target.getActualName()), target.getActualName()));
                    toReturn.add(new ActionEntry((short)78, "Replace lock", "replacing lock"));
                }
            }
        }
        else {
            if (isTop) {
                toReturn.add(Actions.actionEntrys[3]);
            }
            if ((!target.isTent() || target.getLastOwnerId() == performer.getWurmId() || target.getOwnerId() == performer.getWurmId()) && lockId == -10L && (target.getParentId() != -10L || watchers == null || watchers.length == 0) && ((source.isBoatLock() && target.isBoat()) || source.mayLockItems()) && target.isOwner(performer)) {
                toReturn.add(Actions.actionEntrys[161]);
            }
        }
    }
    
    protected static void addLockPickEntry(final Creature performer, final Item source, final Item target, final Item lock, final List<ActionEntry> toReturn) {
        final boolean isLargeVehicle = target.isVehicle() && target.getSizeZ() > 5;
        float rarityMod = 1.0f;
        if (lock.getRarity() > 0) {
            rarityMod += lock.getRarity() * 0.2f;
        }
        if (target.getRarity() > 0) {
            rarityMod += target.getRarity() * 0.2f;
        }
        if (source.getRarity() > 0) {
            rarityMod -= source.getRarity() * 0.1f;
        }
        final float difficulty = MethodsItems.getPickChance(target.getCurrentQualityLevel(), source.getCurrentQualityLevel(), lock.getCurrentQualityLevel(), (float)performer.getLockPickingSkillVal(), (byte)(isLargeVehicle ? 2 : 0)) / rarityMod * (1.0f + Item.getMaterialLockpickBonus(source.getMaterial()));
        final String pick = "Pick lock: " + difficulty + "%";
        toReturn.add(new ActionEntry((short)101, pick, "picking lock"));
    }
    
    private static void addCreationWindowOption(final Creature performer, final Item target, final List<ActionEntry> toReturn) {
        if (target.isUseOnGroundOnly() && !target.isDomainItem() && !target.isKingdomMarker() && !target.hideAddToCreationWindow() && !target.isNoDrop() && !target.isMailBox()) {
            if (target.getTopParent() == target.getWurmId()) {
                toReturn.add(Actions.actionEntrys[607]);
            }
        }
        else if (target.isUnfinished()) {
            toReturn.add(Actions.actionEntrys[607]);
        }
    }
    
    private static void decay(final Item item, final Creature performer) {
        if (item.getTemplateId() == 176 && performer.getPower() > 1 && Servers.isThisATestServer()) {
            WurmCalendar.incrementHour();
            return;
        }
        final long decayTime = item.getDecayTime();
        if (decayTime == Long.MAX_VALUE) {
            return;
        }
        final long time = item.getLastMaintained();
        item.setLastMaintained(time - decayTime);
        if (WurmCalendar.currentTime <= item.creationDate + 1382400L) {
            item.creationDate -= decayTime;
        }
        if (item.isBulkContainer() && item.getItemCount() > 0) {
            final Item[] items = item.getItemsAsArray();
            for (int i = 0; i < item.getItemCount(); ++i) {
                final long decayTime2 = items[i].getDecayTime();
                final long time2 = items[i].getLastMaintained();
                items[i].setLastMaintained(time2 - decayTime2);
                if (WurmCalendar.currentTime <= items[i].creationDate + 1382400L) {
                    items[i].creationDate -= decayTime;
                }
            }
        }
        Item topParent = null;
        try {
            topParent = Items.getItem(item.getTopParent());
        }
        catch (NoSuchItemException ex) {}
        if (topParent != null && topParent.getTemplateId() == 0) {
            item.pollOwned(performer);
        }
        else {
            final int tX = item.getTileX();
            final int tY = item.getTileY();
            final VolaTile tile = Zones.getTileOrNull(tX, tY, item.isOnSurface());
            if (tile != null) {
                item.poll(tile.getStructure() != null, tile.getVillage() != null, 0L);
            }
            else {
                item.poll(false, false, 0L);
            }
        }
    }
    
    @Override
    public List<ActionEntry> getBehavioursFor(final Creature performer, final Item source, final Item target) {
        final List<ActionEntry> toReturn = new LinkedList<ActionEntry>();
        final int ttid = target.getTemplateId();
        final int stid = source.getTemplateId();
        boolean added = false;
        try {
            toReturn.addAll(super.getBehavioursFor(performer, source, target));
        }
        catch (Exception ex) {}
        long owner = -10L;
        if (!target.isTraded()) {
            if (target.canBePlanted()) {
                final long ownerId = target.getOwnerId();
                if (ownerId == -10L) {
                    if (performer.isWithinDistanceTo(target.getPosX(), target.getPosY(), target.getPosZ(), 4.0f)) {
                        final BlockingResult result = Blocking.getBlockerBetween(performer, target, 4);
                        if (result == null) {
                            final boolean isPvpServer = Servers.isThisAPvpServer();
                            if (MethodsItems.checkIfStealing(target, performer, null)) {
                                if (isPvpServer) {
                                    toReturn.add(Actions.actionEntrys[100]);
                                }
                            }
                            else if (target.isKingdomFlag() && target.getAuxData() != performer.getKingdomId()) {
                                if (isSignManipulationOk(target, performer, (short)6)) {
                                    toReturn.add(Actions.actionEntrys[6]);
                                }
                                else if (isPvpServer) {
                                    toReturn.add(Actions.actionEntrys[100]);
                                }
                            }
                            else if (((!target.isBulkContainer() && target.getTemplate().getInitialContainers() == null) || target.isEmpty(true)) && !target.isNoTake(performer)) {
                                if (isSignManipulationOk(target, performer, (short)6)) {
                                    toReturn.add(Actions.actionEntrys[6]);
                                    if ((target.getParentId() == -10L && ttid != 26) || ttid != 298) {
                                        toReturn.add(Actions.actionEntrys[925]);
                                    }
                                }
                                else if (isPvpServer) {
                                    toReturn.add(Actions.actionEntrys[100]);
                                }
                            }
                            if (!target.isPlanted() && target.getParentId() == -10L && !target.isRoadMarker()) {
                                toReturn.add(new ActionEntry((short)176, "Secure", "securing the " + target.getName()));
                            }
                        }
                    }
                    if (Features.Feature.SINGLE_PLAYER_BRIDGES.isEnabled() && source.getTemplateId() == 903 && target.getTemplateId() == 901) {
                        toReturn.add(Actions.actionEntrys[637]);
                        toReturn.add(Actions.actionEntrys[640]);
                    }
                }
                else if (ownerId == performer.getWurmId() && !target.isRoadMarker()) {
                    if (target.getTemplateId() == 1396) {
                        int placedTile;
                        if (performer.isOnSurface()) {
                            placedTile = Server.surfaceMesh.getTile(performer.getTileX(), performer.getTileY());
                        }
                        else {
                            placedTile = Server.caveMesh.getTile(performer.getTileX(), performer.getTileY());
                        }
                        if (Tiles.decodeHeight(placedTile) < 0) {
                            toReturn.add(Actions.actionEntrys[176]);
                        }
                    }
                    else if (source.isAbility() || target.isAbility()) {
                        if (source == target) {
                            toReturn.add(Actions.actionEntrys[640]);
                            toReturn.add(new ActionEntry((short)176, "Activate", "activating the " + target.getName()));
                        }
                    }
                    else {
                        toReturn.add(Actions.actionEntrys[176]);
                    }
                }
                if (target.getTemplateId() == 835) {
                    final Village village = Villages.getVillageForCreature(performer);
                    if (village != null) {
                        if (!RecruitmentAds.containsAdForVillage(village.getId())) {
                            final Citizen cit = village.getCitizen(performer.getWurmId());
                            if (cit != null && cit.getRole().mayInviteCitizens()) {
                                toReturn.add(Actions.actionEntrys[598]);
                            }
                        }
                        else {
                            final Citizen cit = village.getCitizen(performer.getWurmId());
                            if (cit != null && cit.getRole().mayInviteCitizens()) {
                                toReturn.add(Actions.actionEntrys[602]);
                                toReturn.add(Actions.actionEntrys[603]);
                            }
                        }
                    }
                    else {
                        toReturn.add(Actions.actionEntrys[601]);
                    }
                }
                if (target.getTemplateId() == 1271) {
                    if (target.mayAccessHold(performer)) {
                        toReturn.add(new ActionEntry((short)17, "Read messages", "reading messages"));
                    }
                    if ((target.mayPostNotices(performer) || target.mayAddPMs(performer)) && source.getAuxData() == 0 && (source.getTemplateId() == 748 || source.getTemplateId() == 1272)) {
                        toReturn.add(new ActionEntry((short)118, "Post message", "posting message"));
                    }
                }
            }
            try {
                owner = target.getOwner();
                if (owner == performer.getWurmId()) {
                    try {
                        final Item p = Items.getItem(target.getTopParent());
                        if (p != null) {
                            if (target.isHollow() && !target.isSealedByPlayer() && target.getTemplateId() != 1342 && (target.getTopParent() == performer.getInventory().getWurmId() || p.isBodyPart())) {
                                final long lockId = target.getLockId();
                                if (lockId == -10L) {
                                    toReturn.add(Actions.actionEntrys[568]);
                                    if (target.isLockable() && source.mayLockItems()) {
                                        target.setNewOwner(performer.getWurmId());
                                        toReturn.add(Actions.actionEntrys[161]);
                                    }
                                    else if (Features.Feature.TRANSFORM_RESOURCE_TILES.isEnabled() && ttid == 1020 && source.isLiquid() && getTileTransmutationLiquidAuxData(source, target) != 0) {
                                        toReturn.add(new ActionEntry((short)(-2), "Alchemy", "Alchemy"));
                                        toReturn.add(Actions.actionEntrys[283]);
                                        toReturn.add(Actions.actionEntrys[285]);
                                    }
                                }
                                else {
                                    try {
                                        final Item lock = Items.getItem(lockId);
                                        if (performer.hasKeyForLock(lock) || target.isOwner(performer)) {
                                            toReturn.add(Actions.actionEntrys[568]);
                                            if (target.isOwner(performer)) {
                                                int sz = -1;
                                                if (source.isLock()) {
                                                    --sz;
                                                }
                                                toReturn.add(new ActionEntry((short)sz, LoginHandler.raiseFirstLetter(target.getActualName()), target.getActualName()));
                                                if (source.isLock()) {
                                                    toReturn.add(new ActionEntry((short)78, "Replace lock", "replacing lock"));
                                                }
                                                toReturn.add(Actions.actionEntrys[102]);
                                            }
                                        }
                                        else if (target.mayAccessHold(performer)) {
                                            toReturn.add(Actions.actionEntrys[568]);
                                        }
                                        if (stid == 463) {
                                            addLockPickEntry(performer, source, target, lock, toReturn);
                                        }
                                    }
                                    catch (NoSuchItemException nse) {
                                        ItemBehaviour.logger.log(Level.WARNING, "No lock with id " + lockId + ", although the item has that.");
                                        if (source.isLock() && target.isOwner(performer)) {
                                            toReturn.add(new ActionEntry((short)78, "Replace lock", "replacing lock"));
                                        }
                                    }
                                }
                            }
                            else {
                                if (ttid == 94 || ttid == 152 || ttid == 780 || ttid == 95 || ttid == 150 || ttid == 96 || ttid == 151) {
                                    toReturn.add(Actions.actionEntrys[939]);
                                }
                                if (ttid == 1394 && (source.isWeaponKnife() || source.getTemplateId() == 258)) {
                                    toReturn.add(Actions.actionEntrys[942]);
                                }
                            }
                        }
                    }
                    catch (NoSuchItemException ex2) {}
                    if (!source.equals(target)) {
                        if (!added) {
                            addCreationEntrys(toReturn, performer, source, target);
                            added = true;
                        }
                        if (ttid == 1076 && source.isGem() && target.getData1() <= 0) {
                            toReturn.add(Actions.actionEntrys[463]);
                        }
                        if (source.isCombine() && stid == ttid && source.getRarity() == target.getRarity()) {
                            toReturn.add(Actions.actionEntrys[93]);
                        }
                        if ((stid == 1193 || stid == 417) && ttid == stid && source.getRealTemplateId() != target.getRealTemplateId() && source.getRarity() == target.getRarity()) {
                            toReturn.add(Actions.actionEntrys[93]);
                        }
                        if (ttid == 765 && (stid == 62 || source.isWeaponCrush())) {
                            toReturn.add(Actions.actionEntrys[54]);
                        }
                        if (ttid == 1307 && target.getData1() > 0 && stid == 1307 && source.getRealTemplateId() == target.getRealTemplateId()) {
                            toReturn.add(Actions.actionEntrys[912]);
                        }
                        if (target.isLight() || target.isFire() || ttid == 1243) {
                            if (target.isOnFire()) {
                                if (!target.isAlwaysLit()) {
                                    if (target.getTemplateId() == 729) {
                                        toReturn.add(new ActionEntry((short)53, "Blow out", "blowing out", new int[] { 0, 43 }));
                                    }
                                    else {
                                        toReturn.add(Actions.actionEntrys[53]);
                                    }
                                }
                            }
                            else if (target.getTemplateId() == 1396) {
                                if ((stid == 143 || (stid == 176 && performer.getPower() >= 2)) && target.getAuxData() > 0 && target.isPlanted()) {
                                    toReturn.add(Actions.actionEntrys[12]);
                                }
                            }
                            else if ((stid == 143 || (stid == 176 && performer.getPower() >= 2) || target.getTemplateId() == 742 || (source.isBurnable() && !source.isIndestructible() && source.getTemperature() > 1000)) && (target.getTemplateId() != 729 || target.getAuxData() > 0)) {
                                toReturn.add(Actions.actionEntrys[12]);
                            }
                        }
                        if (source.isWeaponKnife() && (ttid == 92 || ttid == 129 || (target.getTemplate().getFoodGroup() == 1201 && ttid != 369))) {
                            toReturn.add(Actions.actionEntrys[225]);
                        }
                        if (source.isWeaponKnife() || source.getTemplateId() == 258) {
                            if (ttid == 729) {
                                toReturn.add(new ActionEntry((short)225, "Cut up", "cutting"));
                            }
                            else if (ttid == 203) {
                                toReturn.add(new ActionEntry((short)225, "Slice up", "cutting"));
                            }
                            else if (!target.isBulk() && target.isFood() && !target.isDrinkable() && !target.isMeat() && !target.isFish() && target.getTemplateId() != 729 && (!target.getName().contains("portion of ") || !target.getName().contains("slice of ")) && stid == 258) {
                                toReturn.add(new ActionEntry((short)225, "Take Portion", "taking"));
                            }
                        }
                        if (stid == 1255 && target.canBeSealedByPlayer() && !target.isSealedByPlayer()) {
                            toReturn.add(Actions.actionEntrys[739]);
                        }
                        if (!target.isLiquid() && target.isWrapped()) {
                            toReturn.add(new ActionEntry((short)740, "Unwrap", "unwrapping", ItemBehaviour.emptyIntArr));
                        }
                        else if (target.isRaw() && target.canBeRawWrapped() && target.isPStateNone()) {
                            toReturn.add(new ActionEntry((short)739, "Wrap", "wrapping", ItemBehaviour.emptyIntArr));
                        }
                        else if ((stid == 748 || stid == 1272) && target.canBePapyrusWrapped() && !target.isWrapped() && source.getAuxData() == 2) {
                            toReturn.add(new ActionEntry((short)739, "Wrap", "wrapping", ItemBehaviour.emptyIntArr));
                        }
                        else if ((stid == 213 || stid == 926) && target.canBeClothWrapped() && !target.isWrapped()) {
                            toReturn.add(new ActionEntry((short)739, "Wrap", "wrapping", ItemBehaviour.emptyIntArr));
                        }
                        if (target.isSealedByPlayer() && target.getTemplateId() != 1309 && !target.isCrate()) {
                            Item liquid = null;
                            for (final Item item : target.getItemsAsArray()) {
                                if (item.isLiquid()) {
                                    liquid = item;
                                    break;
                                }
                            }
                            if (liquid == null || !liquid.isFermenting()) {
                                toReturn.add(Actions.actionEntrys[740]);
                            }
                            toReturn.add(Actions.actionEntrys[19]);
                        }
                        if (source.getTemplate().isRune()) {
                            final Skill soulDepth = performer.getSoulDepth();
                            final double diff = 20.0f + source.getDamage() - (source.getCurrentQualityLevel() + source.getRarity() - 45.0);
                            final double chance = soulDepth.getChance(diff, null, source.getCurrentQualityLevel());
                            if (RuneUtilities.isEnchantRune(source) && RuneUtilities.canApplyRuneTo(source, target)) {
                                if (RuneUtilities.getNumberOfRuneEffects(target) == 0) {
                                    toReturn.add(new ActionEntry((short)945, "Attach Rune: " + chance + "%", "attaching rune", ItemBehaviour.emptyIntArr));
                                }
                                else if (RuneUtilities.getNumberOfRuneEffects(target) == 1) {
                                    toReturn.add(new ActionEntry((short)945, "Replace Rune: " + chance + "%", "replacing rune", ItemBehaviour.emptyIntArr));
                                }
                            }
                            else if (RuneUtilities.isSingleUseRune(source) && RuneUtilities.isCorrectTarget(source, target)) {
                                toReturn.add(new ActionEntry((short)945, "Use Rune: " + chance + "%", "using rune", ItemBehaviour.emptyIntArr));
                            }
                        }
                    }
                    if (ttid == 682 && Servers.localServer.PVPSERVER) {
                        toReturn.add(Actions.actionEntrys[480]);
                    }
                    if (ttid == 1024) {
                        toReturn.add(Actions.actionEntrys[115]);
                    }
                }
                if (source.isPuppet() && target.isPuppet() && !source.equals(target)) {
                    toReturn.add(Actions.actionEntrys[397]);
                }
            }
            catch (NotOwnedException noe) {
                if ((source.getTemplateId() == 25 && target.getAuxData() == 30 && target.getTemplateId() == 180) || (source.getTemplateId() == 25 && target.getAuxData() == 30 && target.getTemplateId() == 178) || (source.getTemplateId() == 25 && target.getAuxData() == 30 && target.getTemplateId() == 1023) || (source.getTemplateId() == 25 && target.getAuxData() == 30 && target.getTemplateId() == 1028)) {
                    final BlockingResult result2 = Blocking.getBlockerBetween(performer, target, 4);
                    if (result2 == null) {
                        toReturn.add(Actions.actionEntrys[922]);
                    }
                }
                if (source.isWeaponBow() && ttid == 458) {
                    toReturn.add(Actions.actionEntrys[134]);
                }
                if (ttid == 652) {
                    toReturn.add(Actions.actionEntrys[214]);
                }
                if (target.isNoTake(performer) || target.isUseOnGroundOnly()) {
                    if (target.isRiftAltar() && (stid == 764 || stid == 1096)) {
                        toReturn.add(Actions.actionEntrys[117]);
                    }
                    if (target.getWurmId() == performer.getVehicle() || performer.isWithinDistanceTo(target.getPosX(), target.getPosY(), target.getPosZ(), (target.isVehicle() && !target.isTent() && !target.isChair()) ? ((float)Math.max(4, target.getSizeZ() / 100)) : 4.0f)) {
                        final BlockingResult result2 = (target.getWurmId() == performer.getVehicle()) ? null : Blocking.getBlockerBetween(performer, target, 4);
                        if (result2 == null) {
                            if (ttid == 538 && !Servers.localServer.isChallengeServer()) {
                                toReturn.add(Actions.actionEntrys[353]);
                            }
                            else if (ttid == 726) {
                                if (performer.getKingdomId() == target.getAuxData()) {
                                    final King k = King.getKing(target.getAuxData());
                                    if (k != null) {
                                        if (k.mayBeChallenged()) {
                                            toReturn.add(Actions.actionEntrys[488]);
                                        }
                                        else if (k.hasFailedAllChallenges()) {
                                            toReturn.add(Actions.actionEntrys[487]);
                                        }
                                    }
                                }
                                if (performer.getPower() >= 3) {
                                    toReturn.add(Actions.actionEntrys[118]);
                                }
                            }
                            if (!added) {
                                addCreationEntrys(toReturn, performer, source, target);
                                added = true;
                            }
                            if (target.isSealedByPlayer() && target.getTemplateId() != 1309 && !target.isCrate()) {
                                Item liquid2 = null;
                                for (final Item item2 : target.getItemsAsArray()) {
                                    if (item2.isLiquid()) {
                                        liquid2 = item2;
                                        break;
                                    }
                                }
                                if (liquid2 == null || !liquid2.isFermenting()) {
                                    toReturn.add(Actions.actionEntrys[740]);
                                }
                                toReturn.add(Actions.actionEntrys[19]);
                            }
                            if (stid == 561 && target.canBePeggedByPlayer() && !target.isSealedByPlayer()) {
                                toReturn.add(Actions.actionEntrys[739]);
                            }
                            if (source.getTemplate().isRune()) {
                                final Skill soulDepth2 = performer.getSoulDepth();
                                final double diff2 = 20.0f + source.getDamage() - (source.getCurrentQualityLevel() + source.getRarity() - 45.0);
                                final double chance2 = soulDepth2.getChance(diff2, null, source.getCurrentQualityLevel());
                                if (RuneUtilities.isEnchantRune(source) && RuneUtilities.canApplyRuneTo(source, target)) {
                                    if (RuneUtilities.getNumberOfRuneEffects(target) == 0) {
                                        toReturn.add(new ActionEntry((short)945, "Attach Rune: " + chance2 + "%", "attaching rune", ItemBehaviour.emptyIntArr));
                                    }
                                    else if (RuneUtilities.getNumberOfRuneEffects(target) == 1) {
                                        toReturn.add(new ActionEntry((short)945, "Replace Rune: " + chance2 + "%", "replacing rune", ItemBehaviour.emptyIntArr));
                                    }
                                }
                                else if (RuneUtilities.isSingleUseRune(source) && RuneUtilities.isCorrectTarget(source, target) && !target.isInventory() && !target.isTemporary() && !target.isNotSpellTarget()) {
                                    toReturn.add(new ActionEntry((short)945, "Use Rune: " + chance2 + "%", "using rune", ItemBehaviour.emptyIntArr));
                                }
                            }
                            if (((stid == 143 || (stid == 176 && performer.getPower() >= 2) || (source.isBurnable() && !source.isIndestructible() && source.getTemperature() > 1000)) && (ttid == 180 || ttid == 178 || ttid == 37 || ttid == 74 || ttid == 889 || ttid == 1023 || ttid == 1028 || target.isLight() || ttid == 1178)) || target.getTemplateId() == 742) {
                                if (!target.isOnFire()) {
                                    if (target.getTemplateId() == 729) {
                                        if (target.getAuxData() > 0) {
                                            toReturn.add(Actions.actionEntrys[12]);
                                        }
                                    }
                                    else if (target.getParentId() == -10L || target.getTemplateId() == 1243) {
                                        toReturn.add(Actions.actionEntrys[12]);
                                    }
                                }
                                else if (!target.isAlwaysLit()) {
                                    if (target.getTemplateId() == 729) {
                                        toReturn.add(new ActionEntry((short)53, "Blow out", "blowing out", new int[] { 0, 43 }));
                                    }
                                    else {
                                        toReturn.add(Actions.actionEntrys[53]);
                                    }
                                }
                            }
                            else if (source.isCoin() && target.isSpringFilled() && target.getSpellCourierBonus() > 0.0f) {
                                toReturn.add(Actions.actionEntrys[380]);
                            }
                        }
                        if (ttid == 442) {
                            toReturn.add(new ActionEntry((short)91, "Taste the julbord", "eating"));
                        }
                    }
                }
                else if (target.isLight() && !target.isAlwaysLit()) {
                    if ((stid == 143 || (stid == 176 && performer.getPower() >= 2)) && !target.isOnFire()) {
                        if (target.getTemplateId() != 729 || target.getAuxData() > 0) {
                            toReturn.add(Actions.actionEntrys[12]);
                        }
                    }
                    else if (target.isOnFire()) {
                        if (target.getTemplateId() == 729) {
                            toReturn.add(new ActionEntry((short)53, "Blow out", "blowing out", new int[] { 0, 43 }));
                        }
                        else {
                            toReturn.add(Actions.actionEntrys[53]);
                        }
                    }
                }
                else if (target.isMeditation()) {
                    short nums = -1;
                    final Cultist c = Cultist.getCultist(performer.getWurmId());
                    if (c != null) {
                        --nums;
                        if (c.getLevel() > 2) {
                            --nums;
                        }
                        if (c.getPath() == 4) {
                            --nums;
                        }
                    }
                    toReturn.add(new ActionEntry(nums, "Nature", "meditation"));
                    toReturn.add(Actions.actionEntrys[384]);
                    if (nums < -1) {
                        toReturn.add(Actions.actionEntrys[385]);
                    }
                    if (nums < -2 && c.getLevel() > 2) {
                        toReturn.add(Actions.actionEntrys[386]);
                    }
                    if (nums < -2 && c.getPath() == 4) {
                        toReturn.add(Actions.actionEntrys[722]);
                    }
                }
                else if (target.isSealedByPlayer() && target.getTemplateId() != 1309 && !target.isCrate()) {
                    Item liquid3 = null;
                    for (final Item item3 : target.getItemsAsArray()) {
                        if (item3.isLiquid()) {
                            liquid3 = item3;
                            break;
                        }
                    }
                    if (liquid3 == null || !liquid3.isFermenting()) {
                        toReturn.add(Actions.actionEntrys[740]);
                    }
                    toReturn.add(Actions.actionEntrys[19]);
                }
                else if (stid == 1255 && target.canBeSealedByPlayer() && !target.isSealedByPlayer()) {
                    toReturn.add(Actions.actionEntrys[739]);
                }
                else if (stid == 561 && target.canBePeggedByPlayer() && !target.isSealedByPlayer()) {
                    toReturn.add(Actions.actionEntrys[739]);
                }
            }
            if (target.isCrate() && target.isSealedByPlayer() && target.getLastOwnerId() == performer.getWurmId()) {
                toReturn.add(Actions.actionEntrys[740]);
            }
            if (source.isWand() && performer.getPower() >= 2 && target.isLock()) {
                if (target.isLocked()) {
                    toReturn.add(Actions.actionEntrys[102]);
                }
                else if (Servers.isThisATestServer()) {
                    toReturn.add(Actions.actionEntrys[28]);
                }
            }
            if (target.canHavePermissions()) {
                final List<ActionEntry> permissions = new LinkedList<ActionEntry>();
                if (target.isBed() && target.mayManage(performer)) {
                    permissions.add(new ActionEntry((short)688, "Manage Bed", "managing"));
                }
                else if (target.getTemplateId() == 1271 && target.mayManage(performer)) {
                    permissions.add(new ActionEntry((short)688, "Manage Message Board", "managing"));
                }
                else if (!target.isVehicle() && target.mayManage(performer)) {
                    permissions.add(Actions.actionEntrys[688]);
                }
                if (target.isBed() && target.maySeeHistory(performer)) {
                    permissions.add(new ActionEntry((short)691, "History Bed", "viewing"));
                }
                else if (target.getTemplateId() == 1271 && (target.isOwner(performer) || performer.getPower() > 1)) {
                    permissions.add(new ActionEntry((short)691, "History Of Message Board", "viewing"));
                }
                else if (!target.isVehicle() && target.maySeeHistory(performer)) {
                    permissions.add(new ActionEntry((short)691, "History Item", "viewing"));
                }
                if (!permissions.isEmpty()) {
                    Collections.sort(permissions);
                    toReturn.add(new ActionEntry((short)(-permissions.size()), "Permissions", "viewing"));
                    toReturn.addAll(permissions);
                }
            }
        }
        else if ((stid == 315 || stid == 176) && performer.getPower() >= 2) {
            final short itint = -3;
            toReturn.add(new ActionEntry((short)(-3), "Item", "Item stuff"));
            toReturn.add(Actions.actionEntrys[180]);
            toReturn.add(Actions.actionEntrys[185]);
            toReturn.add(Actions.actionEntrys[684]);
        }
        toReturn.addAll(AutoEquipMethods.getBehaviours(target, performer));
        toReturn.addAll(CargoTransportationMethods.getLoadUnloadActions(performer, target));
        if (source.getTemplateId() == 319 || source.getTemplateId() == 1029) {
            toReturn.addAll(CargoTransportationMethods.getHaulActions(performer, target));
        }
        if ((source.getTemplateId() == 315 && target.getTemplateId() == 1310) || (source.getTemplateId() == 176 && target.getTemplateId() == 1310)) {
            toReturn.add(Actions.actionEntrys[909]);
        }
        if (target.getTemplateId() == 1311 && Features.Feature.TRANSPORTABLE_CREATURES.isEnabled()) {
            final Creature[] folls = performer.getFollowers();
            if (folls.length > 0) {
                toReturn.add(new ActionEntry((short)907, "Load Creature", "loading "));
            }
        }
        if (target.getTemplateId() == 1310) {
            if (source.isLeadCreature()) {
                toReturn.add(new ActionEntry((short)908, "Unload Creature", "unloading "));
            }
            if (target.getData() != -10L) {
                try {
                    final Creature cagedCreature = Creatures.getInstance().getCreature(target.getData());
                    if (cagedCreature != null && !cagedCreature.isPlayer() && !cagedCreature.isHuman() && !cagedCreature.isGhost() && !cagedCreature.isReborn() && (!cagedCreature.isCaredFor() || cagedCreature.getCareTakerId() == performer.getWurmId())) {
                        toReturn.add(Actions.actionEntrys[493]);
                    }
                }
                catch (NoSuchCreatureException ex3) {}
            }
        }
        if (Features.Feature.CHICKEN_COOPS.isEnabled() && target.getTemplateId() == 1432) {
            final Creature[] folls = performer.getFollowers();
            if (folls.length == 1) {
                toReturn.add(new ActionEntry((short)907, "Load Creature", "loading "));
            }
        }
        addCreationWindowOption(performer, target, toReturn);
        boolean mayManipulate = false;
        if (!target.isTraded()) {
            if (owner == performer.getWurmId()) {
                mayManipulate = true;
                if (stid == 457 && target.isBowUnstringed()) {
                    toReturn.add(Actions.actionEntrys[132]);
                }
                if ((stid == 150 || stid == 151) && ttid == 780) {
                    toReturn.add(Actions.actionEntrys[132]);
                }
                if (target.isLiquid()) {
                    toReturn.add(new ActionEntry((short)(-1), "Pour", "pouring", new int[0]));
                    toReturn.add(new ActionEntry((short)7, "On ground", "pouring", new int[0]));
                }
                else {
                    final int templateId = target.getTemplateId();
                    if (templateId == 26 || templateId == 298) {
                        toReturn.add(new ActionEntry((short)(-2), "Drop", "dropping", new int[0]));
                        toReturn.add(new ActionEntry((short)7, "On ground", "dropping", new int[0]));
                        toReturn.add(Actions.actionEntrys[638]);
                    }
                    else if (!target.isComponentItem()) {
                        toReturn.add(new ActionEntry((short)(-2), "Drop", "dropping", new int[0]));
                        toReturn.add(new ActionEntry((short)7, "On ground", "dropping", new int[0]));
                        toReturn.add(Actions.actionEntrys[925]);
                    }
                }
                if (ttid == 175 || ttid == 651 || ttid == 1097 || ttid == 1098 || (ttid == 466 && target.getAuxData() == 1)) {
                    toReturn.add(Actions.actionEntrys[3]);
                }
                else if (ttid == 782) {
                    toReturn.add(Actions.actionEntrys[518]);
                }
                if (ttid == 1101) {
                    toReturn.add(Actions.actionEntrys[183]);
                }
                if (!target.isFullprice()) {
                    toReturn.add(new ActionEntry((short)(-2), "Prices", "Prices"));
                    toReturn.add(Actions.actionEntrys[86]);
                    toReturn.add(Actions.actionEntrys[87]);
                }
                else {
                    toReturn.add(new ActionEntry((short)(-1), "Prices", "Prices"));
                    toReturn.add(Actions.actionEntrys[87]);
                }
                if (target.isGem()) {
                    if (target.getData1() > 0) {
                        toReturn.add(Actions.actionEntrys[118]);
                    }
                }
                else if (ttid == 602) {
                    toReturn.add(Actions.actionEntrys[118]);
                }
                else if (target.isDeathProtection() || ttid == 527 || ttid == 5 || ttid == 834 || ttid == 836) {
                    toReturn.add(Actions.actionEntrys[118]);
                }
                else if (ttid == 233) {
                    toReturn.add(Actions.actionEntrys[682]);
                }
                else if (ttid == 781 || ttid == 843 || ttid == 1300) {
                    if (target.getOwnerId() != -10L) {
                        toReturn.add(Actions.actionEntrys[118]);
                    }
                }
                else if (ttid == 719) {
                    toReturn.add(Actions.actionEntrys[118]);
                }
                if ((source.isHealing() && target.isHealing()) || (source.getTemplateId() == 764 && target.getTemplateId() == 866) || (source.getTemplateId() == 866 && target.getTemplateId() == 764)) {
                    if (stid != ttid) {
                        toReturn.add(new ActionEntry((short)(-2), "Alchemy", "Alchemy"));
                        toReturn.add(Actions.actionEntrys[283]);
                        toReturn.add(Actions.actionEntrys[285]);
                    }
                }
                else if (source.isSmearable()) {
                    toReturn.add(new ActionEntry((short)(-1), "Alchemy", "Alchemy"));
                    toReturn.add(Actions.actionEntrys[633]);
                }
                else if (target.isAbility() && !target.canBePlanted()) {
                    toReturn.add(Actions.actionEntrys[118]);
                }
                else if (target.getTemplateId() == 1438) {
                    toReturn.add(new ActionEntry((short)118, "Claim affinity", "claiming"));
                }
                if (target.isInstaDiscard()) {
                    toReturn.add(Actions.actionEntrys[600]);
                }
            }
            else if (target.getZoneId() != -10L) {
                Item top = target.getTopParentOrNull();
                if (top == null) {
                    top = target;
                }
                if (top.getWurmId() == performer.getVehicle() || performer.isWithinDistanceTo(target.getPosX(), target.getPosY(), target.getPosZ(), (target.isVehicle() && !target.isTent() && !target.isChair()) ? ((float)Math.max(6, target.getSizeZ() / 100)) : 6.0f)) {
                    final BlockingResult result = (target.getWurmId() == performer.getVehicle()) ? null : Blocking.getBlockerBetween(performer, target, 4);
                    if (result == null) {
                        mayManipulate = true;
                        try {
                            final Zone tzone = Zones.getZone((int)target.getPosX() >> 2, (int)target.getPosY() >> 2, target.isOnSurface());
                            final VolaTile tile = tzone.getTileOrNull((int)target.getPosX() >> 2, (int)target.getPosY() >> 2);
                            if (tile != null) {
                                final Structure struct = tile.getStructure();
                                final VolaTile tile2 = performer.getCurrentTile();
                                if (tile2 != null) {
                                    if (tile.getStructure() != struct && (struct == null || !struct.isTypeBridge())) {
                                        mayManipulate = false;
                                    }
                                }
                                else if (struct != null && !struct.isTypeBridge()) {
                                    mayManipulate = false;
                                }
                            }
                        }
                        catch (NoSuchZoneException ex4) {}
                        if (mayManipulate) {
                            if (!target.isNoTake(performer) && !target.isOutsideOnly() && !target.canBePlanted() && !target.isLiquid() && !target.isBulkContainer()) {
                                if (MethodsItems.checkIfStealing(target, performer, null)) {
                                    toReturn.add(Actions.actionEntrys[100]);
                                }
                                else {
                                    toReturn.add(Actions.actionEntrys[6]);
                                    if ((target.getParentId() == -10L && ttid != 26) || ttid != 298) {
                                        toReturn.add(Actions.actionEntrys[925]);
                                    }
                                }
                            }
                            if ((target.isHollow() && (!target.isSealedByPlayer() && (target.getTemplateId() != 1342 || target.isPlanted())) && (!target.getTemplate().hasViewableSubItems() || target.getTemplate().isContainerWithSubItems() || performer.getPower() > 0)) || ttid == 865) {
                                final boolean isTop = target.getWurmId() == target.getTopParent() || (target.getTopParentOrNull() != null && target.getTopParentOrNull().getTemplate().hasViewableSubItems() && (!target.getTopParentOrNull().getTemplate().isContainerWithSubItems() || target.isPlacedOnParent()));
                                if (target.isLockable()) {
                                    final long lockId2 = target.getLockId();
                                    try {
                                        final Creature[] watchers = target.getWatchers();
                                        boolean watching = false;
                                        for (final Creature lWatcher : watchers) {
                                            if (lWatcher == performer) {
                                                watching = true;
                                            }
                                        }
                                        if (watching) {
                                            if (isTop) {
                                                toReturn.add(Actions.actionEntrys[4]);
                                            }
                                            addLockOptions(performer, source, target, lockId2, stid, isTop, toReturn, watchers);
                                        }
                                        else {
                                            addLockOptions(performer, source, target, lockId2, stid, isTop, toReturn, watchers);
                                        }
                                    }
                                    catch (NoSuchCreatureException nsc) {
                                        addLockOptions(performer, source, target, lockId2, stid, isTop, toReturn, null);
                                    }
                                }
                                else if (isTop) {
                                    try {
                                        final Creature[] watchers2 = target.getWatchers();
                                        boolean watching2 = false;
                                        for (final Creature lWatcher2 : watchers2) {
                                            if (lWatcher2 == performer) {
                                                watching2 = true;
                                            }
                                        }
                                        if (watching2) {
                                            toReturn.add(Actions.actionEntrys[4]);
                                        }
                                        else if (target.getTemplateId() == 272 && target.getWasBrandedTo() != -10L) {
                                            if (target.mayCommand(performer)) {
                                                toReturn.add(Actions.actionEntrys[3]);
                                            }
                                        }
                                        else {
                                            toReturn.add(Actions.actionEntrys[3]);
                                        }
                                    }
                                    catch (NoSuchCreatureException nsc2) {
                                        if (target.getTemplateId() == 272 && target.getWasBrandedTo() != -10L) {
                                            if (target.mayCommand(performer)) {
                                                toReturn.add(Actions.actionEntrys[3]);
                                            }
                                        }
                                        else {
                                            toReturn.add(Actions.actionEntrys[3]);
                                        }
                                    }
                                }
                            }
                            if (target.isMailBox()) {
                                if (target.isEmpty(false)) {
                                    toReturn.add(Actions.actionEntrys[336]);
                                }
                                else {
                                    toReturn.add(Actions.actionEntrys[337]);
                                }
                            }
                            if (target.getTemplateId() != 272 || target.getWasBrandedTo() == -10L || target.mayCommand(performer)) {
                                toReturn.addAll(this.makeMoveSubMenu(performer, target));
                            }
                            if (!added) {
                                addCreationEntrys(toReturn, performer, source, target);
                                added = true;
                            }
                            if (target.isDraggable()) {
                                boolean ok = true;
                                if (target.isVehicle()) {
                                    final Vehicle vehic = Vehicles.getVehicle(target);
                                    if (vehic.pilotId != -10L) {
                                        ok = false;
                                    }
                                    if (vehic.draggers != null && !vehic.draggers.isEmpty()) {
                                        ok = false;
                                    }
                                }
                                if (performer.getVehicle() != -10L) {
                                    ok = false;
                                }
                                if (ok && !Items.isItemDragged(target) && target.getTopParent() == target.getWurmId()) {
                                    final boolean havePermission = VehicleBehaviour.hasPermission(performer, target);
                                    if (havePermission || target.mayDrag(performer)) {
                                        toReturn.add(Actions.actionEntrys[74]);
                                    }
                                }
                                else if (performer.getDraggedItem() == target) {
                                    toReturn.add(Actions.actionEntrys[75]);
                                }
                            }
                            if (ttid == 741) {
                                toReturn.add(Actions.actionEntrys[118]);
                            }
                            else if (ttid == 739 || target.isWarTarget()) {
                                toReturn.add(Actions.actionEntrys[504]);
                            }
                            else if (ttid == 722) {
                                toReturn.add(Actions.actionEntrys[118]);
                            }
                            if (target.isBed()) {
                                this.addBedOptions(performer, target, toReturn);
                            }
                            if (stid == 20 && (ttid == 692 || ttid == 696 || target.getTemplate().isRiftStoneDeco())) {
                                toReturn.add(Actions.actionEntrys[145]);
                            }
                            if (source.isWeaponAxe() && target.getTemplate().isRiftPlantDeco()) {
                                toReturn.add(Actions.actionEntrys[96]);
                            }
                            if (stid == 20 && target.getTemplate().isRiftCrystalDeco()) {
                                toReturn.add(Actions.actionEntrys[156]);
                            }
                        }
                    }
                }
            }
            if (mayManipulate) {
                if (target.isFood()) {
                    if (!target.isNoEatOrDrink()) {
                        toReturn.add(Actions.actionEntrys[19]);
                        toReturn.add(Actions.actionEntrys[182]);
                    }
                    if (target.isEgg() && ttid == 465) {
                        toReturn.add(Actions.actionEntrys[330]);
                    }
                }
                else if (source.isColor()) {
                    if (target.isColorable()) {
                        if (target.getTemplateId() == 1396) {
                            toReturn.add(new ActionEntry((short)231, "Paint barrel", "painting"));
                        }
                        else if (target.isWood()) {
                            toReturn.add(new ActionEntry((short)231, "Paint wood", "painting"));
                        }
                        else {
                            toReturn.add(Actions.actionEntrys[231]);
                        }
                    }
                    if (target.supportsSecondryColor()) {
                        if (target.getTemplateId() == 1396) {
                            toReturn.add(new ActionEntry((short)923, "Paint lamp", "painting"));
                        }
                        else if (target.isColorable()) {
                            toReturn.add(new ActionEntry((short)923, "Dye " + target.getSecondryItemName(), "dyeing"));
                        }
                        else {
                            toReturn.add(new ActionEntry((short)923, "Paint", "painting"));
                        }
                    }
                }
                else if (stid == 676) {
                    if (source.getOwnerId() == performer.getWurmId()) {
                        toReturn.add(Actions.actionEntrys[472]);
                        toReturn.add(Actions.actionEntrys[510]);
                    }
                }
                else if (stid == 751 && ttid == 676) {
                    if (source.getOwnerId() == performer.getWurmId()) {
                        toReturn.add(Actions.actionEntrys[370]);
                    }
                }
                else if (stid == 867 && Spell.mayBeEnchanted(target) && target.getRarity() < source.getRarity()) {
                    toReturn.add(Actions.actionEntrys[632]);
                }
                if (target.isColorable() && target.color != -1 && !target.isLiquid()) {
                    if (target.getTemplateId() == 1396) {
                        if (stid == 441) {
                            toReturn.add(new ActionEntry((short)232, "Remove barrel colour", "removing"));
                        }
                    }
                    else if (stid == 441) {
                        toReturn.add(Actions.actionEntrys[232]);
                    }
                }
                if (target.color2 != -1 && target.supportsSecondryColor() && !target.isLiquid()) {
                    if (target.getTemplateId() == 1396) {
                        if (stid == 73) {
                            toReturn.add(new ActionEntry((short)924, "Remove lamp colour", "removing"));
                        }
                    }
                    else if (stid == 73) {
                        toReturn.add(Actions.actionEntrys[924]);
                    }
                    else if (!target.isColorable() && stid == 441) {
                        toReturn.add(new ActionEntry((short)924, "Remove paint", "removing"));
                    }
                }
                if (ttid == 1307 && target.getData1() <= 0 && (stid == 441 || stid == 97)) {
                    toReturn.add(Actions.actionEntrys[911]);
                }
                if (stid == 788 && target.isMetal() && !target.isLiquid() && !target.isIndestructible() && !target.getTemplate().isRune() && target.getTemplateId() != 1307 && !target.getTemplate().isStatue() && target.getTemplateId() != 1423 && target.getTemperature() > 6000 && target.getParentId() > -10L) {
                    toReturn.add(Actions.actionEntrys[519]);
                }
                if (target.isServerPortal()) {
                    toReturn.add(Actions.actionEntrys[118]);
                }
                else if (ttid == 700 && (((source.isLight() || source.isFire()) && source.isOnFire()) || stid == 315 || stid == 176 || stid == 143)) {
                    toReturn.add(Actions.actionEntrys[118]);
                }
                else if (ttid == 972) {
                    toReturn.add(new ActionEntry((short)118, "Pat", "patting"));
                }
                else if (ttid == 738) {
                    toReturn.add(Actions.actionEntrys[118]);
                }
                if ((source.isColor() && target.isColorComponent()) || (target.isColor() && source.isColorComponent())) {
                    toReturn.add(Actions.actionEntrys[283]);
                }
                if (stid == 489 || (performer.getPower() >= 2 && (stid == 176 || stid == 315))) {
                    toReturn.add(Actions.actionEntrys[329]);
                }
                if (source.isRoyal() && (stid == 535 || stid == 529 || stid == 532)) {
                    toReturn.add(Actions.actionEntrys[354]);
                    if (Kingdoms.isCustomKingdom(performer.getKingdomId())) {
                        toReturn.add(Actions.actionEntrys[719]);
                    }
                }
                if (target.isRoyal() && (ttid == 536 || ttid == 530 || ttid == 533)) {
                    toReturn.add(Actions.actionEntrys[355]);
                    toReturn.add(Actions.actionEntrys[356]);
                    toReturn.add(Actions.actionEntrys[358]);
                }
                boolean improvable = false;
                String actString = "";
                if (target.isContainerLiquid() && target.getItemCount() == 1) {
                    for (final Item i : target.getItems()) {
                        if (!i.isNoEatOrDrink() && !i.isUndistilled() && i.isDrinkable() && !target.isSealedByPlayer()) {
                            toReturn.add(Actions.actionEntrys[19]);
                            toReturn.add(Actions.actionEntrys[183]);
                            break;
                        }
                    }
                }
                else if (target.isLiquid()) {
                    if (!target.isFood() && !target.isNoEatOrDrink() && !target.isUndistilled() && target.isDrinkable()) {
                        toReturn.add(Actions.actionEntrys[19]);
                        toReturn.add(Actions.actionEntrys[183]);
                    }
                    if ((source.isContainerLiquid() || source.isOilConsuming()) && source.getWurmId() != target.getParentId() && !source.isSealedByPlayer()) {
                        toReturn.add(Actions.actionEntrys[189]);
                    }
                    if (stid == 386) {
                        final byte material = MethodsItems.getImproveMaterial(source);
                        final int tid = MethodsItems.getItemForImprovement(material, source.creationState);
                        if (tid == ttid) {
                            actString = MethodsItems.getImproveAction(material, source.creationState);
                            toReturn.add(new ActionEntry((short)228, actString, actString));
                        }
                    }
                    else if (source.isRepairable() && source.creationState != 0 && !source.isNewbieItem() && !source.isChallengeNewbieItem()) {
                        final byte material = MethodsItems.getImproveMaterial(source);
                        final int tid = MethodsItems.getItemForImprovement(material, source.creationState);
                        if (tid == target.getTemplateId()) {
                            improvable = true;
                            actString = MethodsItems.getImproveAction(material, source.creationState);
                        }
                    }
                }
                else if (target.isOilConsuming()) {
                    if (source.isLiquidInflammable()) {
                        toReturn.add(Actions.actionEntrys[189]);
                    }
                }
                else if (target.isCandleHolder() && stid == 133) {
                    if (target.getTemplateId() == 729) {
                        toReturn.add(new ActionEntry((short)189, "Add candles", "adding"));
                    }
                    else {
                        toReturn.add(Actions.actionEntrys[189]);
                    }
                }
                if (stid == 654 && source.getAuxData() == 0) {
                    toReturn.add(Actions.actionEntrys[462]);
                }
                if (target.isRepairable() || improvable || ttid == 179 || ttid == 386) {
                    final int skillNum = MethodsItems.getImproveSkill(target);
                    final byte material2 = MethodsItems.getImproveMaterial(target);
                    int templateId2 = MethodsItems.getImproveTemplateId(target);
                    if (target.creationState != 0) {
                        templateId2 = MethodsItems.getItemForImprovement(material2, target.creationState);
                    }
                    if (skillNum != -10 && (templateId2 == source.getTemplateId() || (source.getTemplateId() == 176 && performer.getPower() >= 2)) && !target.isNewbieItem()) {
                        improvable = true;
                    }
                    if (improvable) {
                        if (target.creationState != 0) {
                            if (!target.isGuardTower() || target.getDamage() == 0.0f) {
                                actString = MethodsItems.getImproveAction(material2, target.creationState);
                                toReturn.add(new ActionEntry((short)192, actString, actString, new int[] { 43 }));
                            }
                        }
                        else if (!actString.isEmpty()) {
                            toReturn.add(new ActionEntry((short)192, actString, actString, new int[] { 43 }));
                        }
                        else {
                            toReturn.add(Actions.actionEntrys[192]);
                        }
                    }
                    if (!target.isLiquid() && (!target.isKingdomMarker() || performer.isFriendlyKingdom(target.getAuxData()))) {
                        toReturn.add(Actions.actionEntrys[162]);
                    }
                }
                if (performer.mayDestroy(target) && !target.isIndestructible() && !target.isRoadMarker()) {
                    toReturn.add(new ActionEntry((short)(-1), "Bash", "Bash"));
                    if (!target.isWarmachine() && target.getTemplateId() != 938 && target.getTemplateId() != 931) {
                        toReturn.add(Actions.actionEntrys[83]);
                    }
                    else {
                        toReturn.add(new ActionEntry((short)83, "Destroy", "Destroying", new int[] { 5, 4, 43 }));
                    }
                }
                if (target.isWeaponBow()) {
                    toReturn.add(Actions.actionEntrys[133]);
                }
                if (source.isHolyItem() && !target.isInventory() && !target.isTemporary() && !target.isNotSpellTarget() && performer.getDeity() != null && (performer.isPriest() || performer.getPower() > 0) && source.isHolyItem(performer.getDeity())) {
                    final float faith = performer.getFaith();
                    final Spell[] spells = performer.getDeity().getSpellsTargettingItems((int)faith);
                    if (spells.length > 0) {
                        toReturn.add(new ActionEntry((short)(-spells.length), "Spells", "spells"));
                        for (final Spell lSpell : spells) {
                            toReturn.add(Actions.actionEntrys[lSpell.number]);
                        }
                    }
                }
                if ((target.getOwnerId() == performer.getWurmId() || target.lastOwner == performer.getWurmId() || performer.getPower() >= 2) && !target.isNoRename() && (!target.isVehicle() || target.isChair() || target.isTent())) {
                    toReturn.add(Actions.actionEntrys[59]);
                }
                if (target.isEpicTargetItem() || target.isKingdomMarker()) {
                    final MissionTrigger[] m2 = MissionTriggers.getMissionTriggersWith(stid, 473, target.getWurmId());
                    if (m2.length > 0) {
                        toReturn.add(Actions.actionEntrys[473]);
                    }
                    final MissionTrigger[] m3 = MissionTriggers.getMissionTriggersWith(stid, 474, target.getWurmId());
                    if (m3.length > 0) {
                        toReturn.add(Actions.actionEntrys[474]);
                    }
                    final MissionTrigger[] mr1 = MissionTriggers.getMissionTriggersWith(stid, 501, target.getWurmId());
                    if (mr1.length > 0) {
                        toReturn.add(Actions.actionEntrys[501]);
                    }
                    final MissionTrigger[] mr2 = MissionTriggers.getMissionTriggersWith(stid, 496, target.getWurmId());
                    if (mr2.length > 0) {
                        toReturn.add(Actions.actionEntrys[496]);
                    }
                    final MissionTrigger[] mr3 = MissionTriggers.getMissionTriggersWith(stid, 498, target.getWurmId());
                    if (mr3.length > 0) {
                        toReturn.add(Actions.actionEntrys[498]);
                    }
                    final MissionTrigger[] mr4 = MissionTriggers.getMissionTriggersWith(stid, 500, target.getWurmId());
                    if (mr4.length > 0) {
                        toReturn.add(Actions.actionEntrys[500]);
                    }
                    final MissionTrigger[] mr5 = MissionTriggers.getMissionTriggersWith(stid, 502, target.getWurmId());
                    if (mr5.length > 0) {
                        toReturn.add(Actions.actionEntrys[502]);
                    }
                    final MissionTrigger[] mr6 = MissionTriggers.getMissionTriggersWith(stid, 497, target.getWurmId());
                    if (mr6.length > 0) {
                        toReturn.add(Actions.actionEntrys[497]);
                    }
                    final MissionTrigger[] mr7 = MissionTriggers.getMissionTriggersWith(stid, 499, target.getWurmId());
                    if (mr7.length > 0) {
                        toReturn.add(Actions.actionEntrys[499]);
                    }
                }
                else if (ttid == 1172) {
                    toReturn.add(new ActionEntry((short)(-13), "Set volume to", "setting", new int[0]));
                    toReturn.add(Actions.actionEntrys[737]);
                    toReturn.add(Actions.actionEntrys[736]);
                    toReturn.add(Actions.actionEntrys[735]);
                    toReturn.add(Actions.actionEntrys[734]);
                    toReturn.add(Actions.actionEntrys[733]);
                    toReturn.add(Actions.actionEntrys[732]);
                    toReturn.add(Actions.actionEntrys[731]);
                    toReturn.add(Actions.actionEntrys[730]);
                    toReturn.add(Actions.actionEntrys[729]);
                    toReturn.add(Actions.actionEntrys[728]);
                    toReturn.add(Actions.actionEntrys[727]);
                    toReturn.add(Actions.actionEntrys[726]);
                    toReturn.add(Actions.actionEntrys[725]);
                }
                else if (ttid == 200 || ttid == 1192 || ttid == 69 || ttid == 66 || ttid == 68 || ttid == 29 || ttid == 32) {
                    toReturn.add(Actions.actionEntrys[936]);
                }
                else if (target.isFish() && target.getWeightGrams() < 300 && (stid == 258 || stid == 93 || stid == 126)) {
                    toReturn.add(Actions.actionEntrys[936]);
                }
                else if (ttid == 479) {
                    toReturn.add(Actions.actionEntrys[937]);
                }
            }
            else if (target.getTemplateId() == 931 && performer.isWithinDistanceTo(target.getPosX(), target.getPosY(), target.getPosZ(), 4.0f)) {
                final BlockingResult result2 = Blocking.getBlockerBetween(performer, target, 4);
                boolean blocked = true;
                if (result2 == null) {
                    blocked = false;
                }
                else if (result2.getFirstBlocker() == result2.getLastBlocker() && result2.getFirstBlocker().isFence() && result2.getFirstBlocker() instanceof Fence) {
                    final Fence fence = (Fence)result2.getFirstBlocker();
                    if (fence.getType() == StructureConstantsEnum.FENCE_SIEGEWALL) {
                        blocked = false;
                    }
                }
                if (!blocked && performer.mayDestroy(target) && !target.isIndestructible() && !target.isRoadMarker()) {
                    toReturn.add(new ActionEntry((short)(-1), "Bash", "Bash"));
                    toReturn.add(new ActionEntry((short)83, "Destroy", "Destroying", new int[] { 5, 4, 43 }));
                }
            }
            if (stid == 176 && WurmPermissions.mayUseDeityWand(performer)) {
                int itint2 = Servers.isThisATestServer() ? -8 : -7;
                if (performer.getPower() >= 4 && Spell.mayBeEnchanted(target)) {
                    --itint2;
                }
                --itint2;
                if (Servers.localServer.testServer && ttid == 1437) {
                    --itint2;
                }
                toReturn.add(new ActionEntry((short)itint2, "Item", "Item stuff"));
                toReturn.add(Actions.actionEntrys[503]);
                if (Servers.isThisATestServer()) {
                    toReturn.add(Actions.actionEntrys[581]);
                }
                toReturn.add(Actions.actionEntrys[180]);
                if (performer.getPower() >= 4 && Spell.mayBeEnchanted(target)) {
                    toReturn.add(Actions.actionEntrys[539]);
                }
                toReturn.add(Actions.actionEntrys[185]);
                toReturn.add(Actions.actionEntrys[684]);
                toReturn.add(new ActionEntry((short)91, "Refresh", "refreshing"));
                toReturn.add(Actions.actionEntrys[88]);
                toReturn.add(Actions.actionEntrys[179]);
                toReturn.add(Actions.actionEntrys[674]);
                if (Servers.localServer.testServer && ttid == 1437) {
                    toReturn.add(Actions.actionEntrys[486]);
                }
                if (performer.getPower() >= 3) {
                    toReturn.add(Actions.actionEntrys[135]);
                    short nos = -7;
                    if (Servers.isThisLoginServer()) {
                        --nos;
                    }
                    if (Servers.isThisLoginServer()) {
                        --nos;
                    }
                    toReturn.add(new ActionEntry(nos, "Server", "Server stuff"));
                    toReturn.add(Actions.actionEntrys[184]);
                    toReturn.add(Actions.actionEntrys[195]);
                    toReturn.add(Actions.actionEntrys[194]);
                    toReturn.add(Actions.actionEntrys[212]);
                    toReturn.add(Actions.actionEntrys[244]);
                    toReturn.add(Actions.actionEntrys[481]);
                    toReturn.add(Actions.actionEntrys[503]);
                    if (Servers.isThisLoginServer()) {
                        toReturn.add(Actions.actionEntrys[609]);
                    }
                    if (Servers.isThisLoginServer()) {
                        toReturn.add(Actions.actionEntrys[635]);
                    }
                }
                else if (WurmPermissions.maySetFaith(performer)) {
                    toReturn.add(Actions.actionEntrys[212]);
                }
                short nums2 = -3;
                if (performer.getPower() >= 3) {
                    nums2 = -4;
                }
                if (performer.getPower() >= 4) {
                    --nums2;
                }
                toReturn.add(new ActionEntry(nums2, "Creatures", "Creatures stuff"));
                toReturn.add(Actions.actionEntrys[89]);
                toReturn.add(Actions.actionEntrys[467]);
                toReturn.add(Actions.actionEntrys[719]);
                if (performer.getPower() >= 4) {
                    toReturn.add(Actions.actionEntrys[535]);
                }
                if (performer.getPower() >= 3) {
                    toReturn.add(Actions.actionEntrys[92]);
                }
                final int tx = source.getData1();
                final int ty = source.getData2();
                if (tx != -1 && ty != -1) {
                    toReturn.add(Actions.actionEntrys[95]);
                }
                toReturn.add(Actions.actionEntrys[94]);
                if (ttid == 176) {
                    toReturn.add(new ActionEntry((short)598, "Manage Recruitment Ads", "Manage Recruitment Ads"));
                }
            }
            else if (stid == 315 && performer.getPower() > 0) {
                if ((ttid != 315 && ttid != 176) || performer.getPower() >= 5) {
                    final short itint3 = -6;
                    toReturn.add(new ActionEntry((short)(-6), "Item", "Item stuff"));
                    toReturn.add(Actions.actionEntrys[180]);
                    toReturn.add(Actions.actionEntrys[185]);
                    toReturn.add(Actions.actionEntrys[684]);
                    toReturn.add(new ActionEntry((short)91, "Refresh", "refreshing"));
                    toReturn.add(Actions.actionEntrys[179]);
                    toReturn.add(Actions.actionEntrys[674]);
                }
                if (performer.getPower() >= 2) {
                    toReturn.add(Actions.actionEntrys[244]);
                    toReturn.add(Actions.actionEntrys[503]);
                    short nums = -2;
                    if (performer.getPower() >= 4) {
                        --nums;
                    }
                    toReturn.add(new ActionEntry(nums, "Creatures", "Creatures stuff"));
                    toReturn.add(Actions.actionEntrys[89]);
                    toReturn.add(Actions.actionEntrys[467]);
                    if (performer.getPower() >= 4) {
                        toReturn.add(Actions.actionEntrys[535]);
                    }
                }
                final int tx2 = source.getData1();
                final int ty2 = source.getData2();
                if (tx2 != -1 && ty2 != -1) {
                    toReturn.add(Actions.actionEntrys[95]);
                }
                toReturn.add(Actions.actionEntrys[94]);
            }
            else if (stid == 1027 && ttid == 1027 && performer.getPower() >= 1) {
                toReturn.add(new ActionEntry((short)(-3), "LCM", "checking", ItemBehaviour.emptyIntArr));
                toReturn.add(Actions.actionEntrys[698]);
                toReturn.add(Actions.actionEntrys[699]);
                toReturn.add(Actions.actionEntrys[700]);
                toReturn.add(Actions.actionEntrys[244]);
                toReturn.add(Actions.actionEntrys[94]);
                final int tx2 = source.getData1();
                final int ty2 = source.getData2();
                if (tx2 != -1 && ty2 != -1) {
                    toReturn.add(Actions.actionEntrys[95]);
                }
            }
            else if (stid == 174 && ttid == 174 && performer.getPower() >= 1) {
                toReturn.add(Actions.actionEntrys[244]);
            }
            if ((target.isFoodMaker() || target.getTemplate().isCooker()) && !target.isSealedByPlayer()) {
                toReturn.add(Actions.actionEntrys[285]);
            }
        }
        else {
            toReturn.add(new ActionEntry((short)(-1), "Prices", "Prices"));
            toReturn.add(Actions.actionEntrys[87]);
        }
        Behaviour.addEmotes(toReturn);
        return toReturn;
    }
    
    public static String getMachineInfo(final Item target) {
        String toReturn = "";
        boolean ready = true;
        if (target.getData() > 0L) {
            try {
                final Item item = Items.getItem(target.getData());
                toReturn = "It is loaded with " + item.getNameWithGenus() + ".";
            }
            catch (NoSuchItemException nsi) {
                toReturn = "It is not loaded.";
                ready = false;
            }
        }
        else {
            toReturn = "It is not loaded.";
            ready = false;
        }
        if (target.getAuxData() > 0) {
            toReturn = "It is winched " + target.getAuxData() + " laps.";
        }
        if (ready) {
            toReturn = "It is ready to fire.";
        }
        return toReturn;
    }
    
    @Override
    public boolean action(final Action act, final Creature performer, final Item target, final short action, final float counter) {
        final int ttid = target.getTemplateId();
        boolean toReturn = true;
        final Communicator comm = performer.getCommunicator();
        if (target.canBePlanted() || target.getTemplateId() == 1309) {
            if ((action == 177 || action == 178) && target.isTurnable(performer) && (!target.isRoadMarker() || !target.isPlanted())) {
                if (isSignManipulationOk(target, performer, (short)177)) {
                    return MethodsItems.moveItem(performer, target, counter, action, act);
                }
                comm.sendNormalServerMessage("You may not turn that " + target.getName() + ".");
                return true;
            }
            else if (action == 176 && !target.isRoadMarker()) {
                if (performer.getPower() > 0) {
                    return MethodsItems.plantSignFinish(performer, target, false, 0, 0, performer.isOnSurface(), performer.getBridgeId(), false, -1L);
                }
                return MethodsItems.plantSign(performer, target, counter, false, 0, 0, performer.isOnSurface(), performer.getBridgeId(), false, -1L);
            }
            else {
                if (action == 598) {
                    comm.sendOpenWindowByTypeID((byte)0);
                    return true;
                }
                if (action == 601) {
                    comm.sendOpenWindowByTypeID((byte)1);
                    comm.sendViableVillageRecruitmentAds();
                    return true;
                }
                if (action == 602) {
                    RecruitmentAds.deleteVillageAd((Player)performer);
                    return true;
                }
                if (action == 603) {
                    final Village village = Villages.getVillageForCreature(performer);
                    if (village == null) {
                        comm.sendNormalServerMessage("You are not a member of a village.");
                        return true;
                    }
                    final RecruitmentAd ad = RecruitmentAds.getVillageAd(village.getId(), performer.getKingdomId());
                    if (ad == null) {
                        comm.sendNormalServerMessage("Your village does not have a recruitment ad that can be edited.");
                        return true;
                    }
                    comm.sendOpenManageRecruitWindowWithData(ad.getDescription());
                    return true;
                }
                else if (Recipes.isRecipeAction(action)) {
                    final Recipe recipe = Recipes.getRecipeByActionId(action);
                    if (recipe == null) {
                        performer.getCommunicator().sendNormalServerMessage("Recipe" + ((performer.getPower() > 1) ? (" " + action) : "") + " not found, most odd!");
                        return true;
                    }
                    return handleRecipe(act, performer, null, target, action, counter, recipe);
                }
                else if (action == 17 && target.getTemplateId() == 1271) {
                    this.readVillageMessages(performer, target);
                }
                else if ((action == 6 || action == 100 || (action == 925 && target.getOwnerId() != performer.getWurmId() && counter == 1.0f)) && !target.isNoTake(performer)) {
                    if (target.getTemplateId() == 1178 && target.getTemperature() > 399 && target.getParentId() == -10L && !target.isEmpty(true)) {
                        comm.sendNormalServerMessage("The " + target.getName() + " is too hot to handle.");
                        return true;
                    }
                    if (target.getTemplateId() == 1175 && target.hasQueen()) {
                        comm.sendNormalServerMessage("The " + target.getName() + " can not be taken when it has a queen in it, try loading it.");
                        return true;
                    }
                    if (target.isChair()) {
                        final Vehicle chair = Vehicles.getVehicle(target);
                        if (chair.isAnySeatOccupied()) {
                            comm.sendNormalServerMessage("The " + Vehicle.getVehicleName(chair) + " is occupied and may not be taken.");
                            return true;
                        }
                    }
                    if ((target.isBulkContainer() || target.getTemplate().getInitialContainers() != null || target.getTemplateId() == 1342) && !target.isEmpty(true)) {
                        comm.sendNormalServerMessage("The " + target.getName() + " needs to be empty to pick it up.");
                        return true;
                    }
                    if (target.getTemplateId() == 1175 && target.hasQueen() && !WurmCalendar.isSeasonWinter()) {
                        performer.getCommunicator().sendSafeServerMessage("The bees get angry and defend the " + target.getName() + " by stinging you.");
                        performer.addWoundOfType(null, (byte)5, 2, true, 1.0f, false, 5000.0f + Server.rand.nextFloat() * 7000.0f, 0.0f, 30.0f, false, false);
                        return true;
                    }
                    if (target.isPlanted()) {
                        toReturn = true;
                        final boolean ok = isSignManipulationOk(target, performer, (short)6);
                        if (ok) {
                            final TakeResultEnum result = MethodsItems.take(act, performer, target);
                            if (result == TakeResultEnum.SUCCESS) {
                                target.setIsPlanted(false);
                                if (target.getTemplateId() == 1342) {
                                    performer.getCommunicator().sendRemoveFromInventory(target, -1L);
                                    performer.getCommunicator().sendAddToInventory(target, -1L, -1L, -1);
                                }
                                if (action == 925) {
                                    return MethodsItems.placeItem(performer, target, act, counter);
                                }
                                comm.sendNormalServerMessage("You get " + target.getNameWithGenus() + ".");
                                Server.getInstance().broadCastAction(performer.getName() + " gets " + target.getNameWithGenus() + ".", performer, Math.min(Math.max(3, target.getSizeZ() / 10), 10));
                                PlayerTutorial.firePlayerTrigger(performer.getWurmId(), PlayerTutorial.PlayerTrigger.TAKEN_ITEM);
                            }
                            else {
                                result.sendToPerformer(performer);
                            }
                        }
                        else {
                            comm.sendNormalServerMessage("The " + target.getName() + " is firmly planted in the ground.");
                        }
                        return toReturn;
                    }
                    final TakeResultEnum result2 = MethodsItems.take(act, performer, target);
                    if (result2 == TakeResultEnum.SUCCESS) {
                        target.setIsPlanted(false);
                        if (action == 925) {
                            return MethodsItems.placeItem(performer, target, act, counter);
                        }
                        comm.sendNormalServerMessage("You get " + target.getNameWithGenus() + ".");
                        Server.getInstance().broadCastAction(performer.getName() + " gets " + target.getNameWithGenus() + ".", performer, Math.min(Math.max(3, target.getSizeZ() / 10), 10));
                        PlayerTutorial.firePlayerTrigger(performer.getWurmId(), PlayerTutorial.PlayerTrigger.TAKEN_ITEM);
                    }
                    else {
                        result2.sendToPerformer(performer);
                    }
                    return true;
                }
                else if (action == 1) {
                    return examine(act, performer, target, action, counter);
                }
            }
        }
        if (target.isCrate() && target.isSealedByPlayer() && action == 1) {
            return examine(act, performer, target, action, counter);
        }
        Label_15319: {
            if (action == 1) {
                String descString = target.examine(performer);
                Label_1722: {
                    if (!target.isKingdomMarker()) {
                        if (target.getTemplateId() != 996) {
                            if (!target.isWarTarget()) {
                                break Label_1722;
                            }
                            final Long last = ItemBehaviour.conquers.get(target.getWurmId());
                            if (last != null && System.currentTimeMillis() - last < 3600000L) {
                                descString = descString + " You will have to wait " + Server.getTimeFor(last + 3600000L - System.currentTimeMillis()) + " if you want to receive battle rank for conquering the " + target.getName() + ".";
                            }
                            break Label_1722;
                        }
                    }
                    try {
                        final String name = Players.getInstance().getNameFor(target.lastOwner);
                        descString = descString + " The name of the founder, " + name + ", has been carved into the stone above the door.";
                    }
                    catch (IOException iox) {
                        ItemBehaviour.logger.log(Level.WARNING, iox.getMessage(), iox);
                    }
                    catch (NoSuchPlayerException ex3) {}
                    final GuardTower tower = Kingdoms.getTower(target);
                    if (tower != null) {
                        descString = descString + " '" + tower.getName() + "' is engraved in a metal plaque on the door.";
                        if (performer.getPower() > 1) {
                            descString = descString + " There are " + tower.getGuardCount() + " guards out of a max of " + tower.getMaxGuards() + " guards associated with this tower.";
                        }
                    }
                    final Long last2 = ItemBehaviour.conquers.get(target.getWurmId());
                    if (last2 != null && System.currentTimeMillis() - last2 < 3600000L) {
                        descString = descString + " You will have to wait " + Server.getTimeFor(last2 + 3600000L - System.currentTimeMillis()) + " if you want to receive battle rank for conquering the " + target.getName() + ".";
                    }
                }
                if (target.isBed()) {
                    PlayerInfo info = PlayerInfoFactory.getPlayerSleepingInBed(target.getWurmId());
                    if (info != null) {
                        comm.sendNormalServerMessage("Some kind of mysterious haze lingers over the " + target.getName() + ", and you notice that the " + target.getName() + " is occupied by the spirit of " + info.getName() + ".");
                    }
                    if (target.getData() > 0L) {
                        info = PlayerInfoFactory.getPlayerInfoWithWurmId(target.getData());
                        if (info != null && (info.lastLogin > 0L || info.lastLogout < System.currentTimeMillis() - 86400000L)) {
                            comm.sendNormalServerMessage(info.getName() + " has rented the " + target.getName() + ".");
                        }
                    }
                }
                Label_3042: {
                    if (!target.isHitchTarget()) {
                        if (!target.isVehicle() || Vehicles.getVehicle(target).isChair()) {
                            break Label_3042;
                        }
                    }
                    try {
                        String where = " in the stern.";
                        if (target.isTent()) {
                            where = " on a pole.";
                        }
                        if (target.isFence()) {
                            where = " on the bottom.";
                        }
                        final String name2 = Players.getInstance().getNameFor(target.lastOwner);
                        descString = descString + " The name of the owner, " + name2 + ", has been etched" + where;
                    }
                    catch (IOException iox) {
                        ItemBehaviour.logger.log(Level.WARNING, iox.getMessage(), iox);
                    }
                    catch (NoSuchPlayerException ex4) {}
                    if (target.isLockable()) {
                        final long lockId = target.getLockId();
                        if (lockId != -10L) {
                            try {
                                Items.getItem(lockId);
                            }
                            catch (NoSuchItemException e2) {
                                ItemBehaviour.logger.log(Level.WARNING, "No lock with id " + lockId + ", although the item has that.");
                                comm.sendNormalServerMessage("It looks like the lock has nearly rusted away, it should be replaced.");
                            }
                        }
                    }
                    if (performer.getPower() > 3) {
                        comm.sendNormalServerMessage("Windrot=" + Server.getWeather().getWindRotation() + " power=" + Server.getWeather().getWindPower() + ", impact=" + performer.getMovementScheme().getWindImpact() + " speedmod=" + performer.getMovementScheme().getSpeedMod());
                        comm.sendNormalServerMessage("Vrot=" + performer.getMovementScheme().getVehicleRotation() + ", speed=" + performer.getMovementScheme().getMountSpeed() + ", power=" + MovementChecker.getWindPower(Server.getWeather().getWindRotation() - 180.0f, performer.getMovementScheme().getVehicleRotation()));
                    }
                    final Vehicle vehic = Vehicles.getVehicle(target);
                    if (performer.getPower() > 0) {
                        if (vehic != null) {
                            final Set<Creature> draggers = vehic.draggers;
                            if (draggers != null) {
                                for (final Creature c : draggers) {
                                    comm.sendNormalServerMessage("Dragged  by " + c.getName());
                                }
                                final Seat[] hitched2;
                                final Seat[] hitched = hitched2 = vehic.hitched;
                                for (final Seat lElement : hitched2) {
                                    comm.sendNormalServerMessage("Hitch seat " + lElement.id + " occupied by " + lElement.getOccupant());
                                }
                            }
                            else {
                                comm.sendNormalServerMessage("No draggers registered.");
                            }
                            comm.sendNormalServerMessage("Pilot id=" + vehic.pilotId);
                        }
                        else {
                            comm.sendNormalServerMessage("Failed to locate vehicle data");
                        }
                    }
                    if (vehic != null) {
                        String passengers = "";
                        for (final Seat seat : vehic.getSeats()) {
                            if (seat.isOccupied()) {
                                Player occupant = null;
                                try {
                                    occupant = Players.getInstance().getPlayer(seat.getOccupant());
                                }
                                catch (NoSuchPlayerException e3) {
                                    ItemBehaviour.logger.log(Level.WARNING, "Occupant with ID: " + seat.getOccupant() + " was not found...");
                                }
                                if (occupant == null || occupant.isVisibleTo(performer)) {
                                    final String pName = PlayerInfoFactory.getPlayerName(seat.getOccupant());
                                    if (seat.type == 0) {
                                        comm.sendNormalServerMessage("Commander: " + pName + ".");
                                    }
                                    else if (seat.type == 1) {
                                        if (!passengers.isEmpty()) {
                                            passengers += ", ";
                                        }
                                        passengers += pName;
                                    }
                                }
                            }
                        }
                        final String pass = "Passenger" + ((passengers.length() == 1) ? "" : "s");
                        if (!passengers.isEmpty()) {
                            comm.sendNormalServerMessage(pass + ": " + passengers + ".");
                        }
                    }
                    assert vehic != null;
                    if (performer.getVehicle() == vehic.getWurmid()) {
                        String whereTo = "The " + vehic.getName();
                        boolean isPvP = false;
                        if (vehic.hasDestinationSet()) {
                            whereTo = whereTo + " has a course plotted to " + vehic.getDestinationServer().getName();
                            if (vehic.getDestinationServer().PVPSERVER) {
                                whereTo += ", which will take you in to hostile territory";
                                isPvP = true;
                            }
                        }
                        else {
                            whereTo += " does not have a course plotted";
                        }
                        if (isPvP) {
                            comm.sendAlertServerMessage(whereTo + ".");
                        }
                        else {
                            comm.sendNormalServerMessage(whereTo + ".");
                        }
                    }
                }
                comm.sendNormalServerMessage(descString);
                if (ttid == 1172) {
                    final int vol = target.getVolume();
                    final String vm = (vol >= 1000) ? (vol / 1000 + "kg") : (vol + "g");
                    comm.sendNormalServerMessage("You check the wheel on the bottom and it indicates the volume is set to " + vm + ".");
                }
                if (target.isFish() && target.isNamed() && target.getCreatorName() != null && target.getCreatorName().length() > 0) {
                    comm.sendNormalServerMessage("Caught by " + target.getCreatorName() + " on " + WurmCalendar.getDateFor(target.creationDate));
                }
                else {
                    final String s = target.getSignature();
                    if (s != null && s.length() > 2 && !target.isDish()) {
                        comm.sendNormalServerMessage("You can barely make out the signature of its maker,  '" + s + "'.");
                    }
                    else if (target.isNamed() && target.getOwnerId() == performer.getWurmId() && target.getCurrentQualityLevel() >= 20.0f && !target.isLiquid() && !target.isDish() && target.getTemplateId() != 1307) {
                        target.setCreator(performer.getName());
                        comm.sendNormalServerMessage("Since its creator tag has faded, you decide to keep history alive by scratching your name on it. Afterwards you proudly read '" + target.getCreatorName() + "'.");
                    }
                }
                target.sendEnchantmentStrings(comm);
                target.sendExtraStrings(comm);
                if (target.isNoTake(performer)) {
                    MissionHelper.printHelpForMission(target.getWurmId(), target.getName(), performer);
                }
            }
            else if (!target.isTraded()) {
                if (action == 6 || action == 100 || (action == 925 && target.getOwnerId() != performer.getWurmId() && counter == 1.0f)) {
                    if (target.isVehicle()) {
                        final Vehicle vehicle = Vehicles.getVehicle(target);
                        for (final Seat seat2 : vehicle.seats) {
                            if (seat2.isOccupied()) {
                                comm.sendNormalServerMessage("You cannot take this item.");
                                return true;
                            }
                        }
                    }
                    if (target.getTemplateId() == 1178 && target.getTemperature() > 200) {
                        comm.sendNormalServerMessage("The " + target.getName() + " is too hot to handle.");
                        return true;
                    }
                    if (target.getTemplateId() == 1175 && target.hasQueen() && !WurmCalendar.isSeasonWinter()) {
                        performer.getCommunicator().sendSafeServerMessage("The bees get angry and defend the " + target.getName() + " by stinging you.");
                        performer.addWoundOfType(null, (byte)5, 2, true, 1.0f, false, 5000.0f + Server.rand.nextFloat() * 7000.0f, 0.0f, 30.0f, false, false);
                        return true;
                    }
                    if ((!target.isNoTake(performer) || performer.getPower() > 0) && !target.isCrate()) {
                        final Item topp = target.getTopParentOrNull();
                        final TakeResultEnum result = MethodsItems.take(act, performer, target);
                        if (result == TakeResultEnum.SUCCESS) {
                            target.setIsPlanted(false);
                            if (action == 925) {
                                return MethodsItems.placeItem(performer, target, act, counter);
                            }
                            if (topp != null && topp.isItemSpawn()) {
                                performer.addChallengeScore(ChallengePointEnum.ChallengePoint.ITEMSLOOTED.getEnumtype(), 0.01f);
                            }
                            comm.sendNormalServerMessage("You get " + target.getNameWithGenus() + ".");
                            Server.getInstance().broadCastAction(performer.getName() + " gets " + target.getNameWithGenus() + ".", performer, Math.min(Math.max(3, target.getSizeZ() / 10), 10));
                            PlayerTutorial.firePlayerTrigger(performer.getWurmId(), PlayerTutorial.PlayerTrigger.TAKEN_ITEM);
                        }
                        else {
                            result.sendToPerformer(performer);
                        }
                    }
                    return true;
                }
                else {
                    if (action == 12) {
                        if (target.getTemplateId() == 1396 && target.isPlanted() && target.getAuxData() > 0) {
                            lightItem(target, null, performer);
                        }
                        else if (target.getTemplateId() != 729 || target.getAuxData() > 0) {
                            lightItem(target, null, performer);
                        }
                        return true;
                    }
                    if (action == 607) {
                        comm.sendAddToCreationWindow(target);
                        target.addCreationWindowWatcher((Player)performer);
                        return true;
                    }
                    if (action == 605) {
                        if (target.isChair()) {
                            final Vehicle chair = Vehicles.getVehicle(target);
                            if (chair.isAnySeatOccupied()) {
                                comm.sendNormalServerMessage("The " + Vehicle.getVehicleName(chair) + " is occupied and may not be taken.");
                                return true;
                            }
                        }
                        if (performer.getVehicle() == -10L) {
                            break Label_15319;
                        }
                        try {
                            final Item vehicle2 = Items.getItem(performer.getVehicle());
                            if (vehicle2.getTemplateId() == 853) {
                                if (target.isBoat() || (target.isUnfinished() && target.getRealTemplate() != null && target.getRealTemplate().isBoat())) {
                                    return CargoTransportationMethods.loadShip(performer, target, counter);
                                }
                                comm.sendNormalServerMessage(StringUtil.format("You can't load the %s on to the %s.", StringUtil.toLowerCase(target.getName()), StringUtil.toLowerCase(vehicle2.getName())));
                            }
                            else {
                                if (vehicle2.getTemplateId() != 1410) {
                                    return CargoTransportationMethods.loadCargo(performer, target, counter);
                                }
                                if (target.getTemplateId() == 1311) {
                                    return CargoTransportationMethods.loadCargo(performer, target, counter);
                                }
                                comm.sendNormalServerMessage(StringUtil.format("You can't load the %s on to the %s.", StringUtil.toLowerCase(target.getName()), StringUtil.toLowerCase(vehicle2.getName())));
                            }
                            break Label_15319;
                        }
                        catch (NoSuchItemException nsi) {
                            ItemBehaviour.logger.log(Level.FINE, "Unable to find vehicle item.", nsi);
                            return true;
                        }
                    }
                    if (action == 606) {
                        return CargoTransportationMethods.unloadCargo(performer, target, counter);
                    }
                    if (action == 907) {
                        if (target.getTemplateId() == 1432) {
                            if (Features.Feature.CHICKEN_COOPS.isEnabled()) {
                                final Creature[] folls = performer.getFollowers();
                                if (folls.length > 0) {
                                    return CargoTransportationMethods.loadChicken(performer, target, counter);
                                }
                            }
                        }
                        else if (target.getTemplateId() == 1311 && Features.Feature.TRANSPORTABLE_CREATURES.isEnabled()) {
                            final Creature[] folls = performer.getFollowers();
                            if (folls.length > 0) {
                                return CargoTransportationMethods.loadCreature(performer, target, counter);
                            }
                        }
                    }
                    else {
                        if (action == 908) {
                            try {
                                final long tpid = target.getTopParent();
                                final Item topParent = Items.getItem(tpid);
                                if (topParent.getTemplateId() == 1432) {
                                    return CargoTransportationMethods.unloadChicken(performer, target, counter);
                                }
                                return CargoTransportationMethods.unloadCreature(performer, target, counter);
                            }
                            catch (NoSuchItemException ex) {
                                ItemBehaviour.logger.log(Level.WARNING, ex.getMessage(), ex);
                                break Label_15319;
                            }
                        }
                        if (action == 493) {
                            if (target.getData() != -10L) {
                                try {
                                    final Creature cagedCreature = Creatures.getInstance().getCreature(target.getData());
                                    if (cagedCreature != null) {
                                        CreatureBehaviour.handle_CAGE_SET_PROTECTED(performer, cagedCreature);
                                    }
                                }
                                catch (NoSuchCreatureException ex5) {}
                            }
                        }
                        else if (action == 7 || action == 638 || action == 925) {
                            if (target.isSurfaceOnly() && !performer.isOnSurface()) {
                                comm.sendNormalServerMessage(target.getName() + " can only be dropped on the surface.");
                                return true;
                            }
                            if ((!target.isNoDrop() || performer.getPower() > 0) && !target.isComponentItem()) {
                                if (action == 925) {
                                    return MethodsItems.placeItem(performer, target, act, counter);
                                }
                                final String[] msg = MethodsItems.drop(performer, target, action == 7);
                                if (msg.length > 0) {
                                    comm.sendNormalServerMessage(msg[0] + msg[1] + msg[2]);
                                    Server.getInstance().broadCastAction(performer.getName() + " drops " + msg[1] + msg[3], performer, 5);
                                }
                                return true;
                            }
                        }
                        else {
                            if (action == 598) {
                                if (target.getTemplateId() == 176) {
                                    final GmVillageAdInterface vad = new GmVillageAdInterface(performer, target.getWurmId());
                                    vad.sendQuestion();
                                }
                                return true;
                            }
                            if (target.isLockable() && action == 102) {
                                if (target.getLastOwnerId() == performer.getWurmId() || target.getOwnerId() == performer.getWurmId()) {
                                    return MethodsItems.unlock(performer, null, target, counter);
                                }
                                comm.sendNormalServerMessage("Only the owner can unlock that.");
                                return true;
                            }
                            else if (action == 568) {
                                if (target.isLockable()) {
                                    if (target.getLockId() == -10L || (target.isDraggable() && MethodsItems.mayUseInventoryOfVehicle(performer, target))) {
                                        comm.sendOpenInventoryContainer(target.getWurmId());
                                    }
                                    else {
                                        try {
                                            final Item lock = Items.getItem(target.getLockId());
                                            if (!lock.getLocked() || target.isOwner(performer)) {
                                                comm.sendOpenInventoryContainer(target.getWurmId());
                                                return true;
                                            }
                                            final long[] keys = lock.getKeyIds();
                                            for (int i = 0; i < keys.length; ++i) {
                                                final Item key = Items.getItem(keys[i]);
                                                if (key.getTopParent() == performer.getInventory().getWurmId()) {
                                                    comm.sendOpenInventoryContainer(target.getWurmId());
                                                    return true;
                                                }
                                            }
                                            comm.sendSafeServerMessage("The " + target.getName() + " is locked. Please use the key to unlock and open it.");
                                        }
                                        catch (NoSuchItemException nsi) {
                                            comm.sendSafeServerMessage("The " + target.getName() + " is locked. Please use the key to unlock and open it.");
                                        }
                                    }
                                }
                                else if (target.isHollow() && !target.isSealedByPlayer() && target.getTemplateId() != 1342) {
                                    comm.sendOpenInventoryContainer(target.getWurmId());
                                }
                                toReturn = true;
                            }
                            else if (action == 162) {
                                if (target.isRepairable() || ttid == 179 || ttid == 386) {
                                    if (target.getTemplateId() == 1311 && !target.isEmpty(true)) {
                                        comm.sendNormalServerMessage("You must first remove the creature from the cage, in order to repair it.");
                                        return true;
                                    }
                                    toReturn = false;
                                    int time = 0;
                                    if (counter == 1.0f) {
                                        if (target.getDamage() == 0.0f) {
                                            if (performer.getPower() >= 5 && Servers.localServer.testServer) {
                                                target.setDamage(30.0f);
                                            }
                                            else {
                                                comm.sendNormalServerMessage("The " + target.getName() + " doesn't need repairing.");
                                                toReturn = true;
                                            }
                                        }
                                        else if (performer.isGuest()) {
                                            comm.sendNormalServerMessage("Guests are not allowed to repair items.");
                                            toReturn = true;
                                        }
                                        else if (target.getTemperature() > 1000 && (target.isWood() || target.isCloth() || target.isMelting() || target.isLiquidInflammable())) {
                                            comm.sendNormalServerMessage("The " + target.getName() + " is too hot to be repaired.");
                                            toReturn = true;
                                        }
                                        else if (target.isKingdomMarker() && (!performer.isFriendlyKingdom(target.getAuxData()) || performer.getEnemyPresense() > 0 || performer.getFightingSkill().getRealKnowledge() < 20.0 || !performer.isPaying())) {
                                            if (performer.getEnemyPresense() > 0) {
                                                comm.sendNormalServerMessage("You are not allowed to repair the " + target.getName() + " while enemies are about.");
                                            }
                                            else if (performer.getFightingSkill().getRealKnowledge() < 20.0) {
                                                comm.sendNormalServerMessage("You are not allowed to repair the " + target.getName() + " until you have 20 fighting skill.");
                                            }
                                            else if (!performer.isFriendlyKingdom(target.getAuxData())) {
                                                comm.sendNormalServerMessage("You are not allowed to repair the " + target.getName() + " it is not part of your kingdom.");
                                            }
                                            else if (!performer.isPaying()) {
                                                comm.sendNormalServerMessage("You are not allowed to repair the " + target.getName() + " without premium status.");
                                            }
                                            else {
                                                comm.sendNormalServerMessage("You are not allowed to repair the " + target.getName() + ".");
                                            }
                                            toReturn = true;
                                        }
                                        else {
                                            if (target.getOwnerId() == -10L && !Methods.isActionAllowed(performer, action, target)) {
                                                return true;
                                            }
                                            act.setPower(target.getDamage());
                                            time = target.getRepairTime(performer);
                                            comm.sendNormalServerMessage("You start repairing the " + target.getName() + ".");
                                            Server.getInstance().broadCastAction(performer.getName() + " starts repairing " + target.getNameWithGenus() + ".", performer, 5);
                                            performer.sendActionControl(Actions.actionEntrys[162].getVerbString(), true, time);
                                            act.setTimeLeft(time);
                                        }
                                    }
                                    else {
                                        time = act.getTimeLeft();
                                        if (target.getZoneId() == -10L && performer.isOnSurface() != target.isOnSurface()) {
                                            comm.sendNormalServerMessage("You can't reach the " + target.getName() + " now.");
                                            return true;
                                        }
                                        if (!target.isRepairable() && ttid != 179 && ttid != 386) {
                                            comm.sendNormalServerMessage(target.getName() + "is not repairable.");
                                            return true;
                                        }
                                        if (act.justTickedSecond()) {
                                            target.repair(performer, (short)time, act.getPower());
                                        }
                                        if (counter * 10.0f > time || target.getDamage() == 0.0f) {
                                            comm.sendNormalServerMessage("You repair the " + target.getName() + ".");
                                            if (target.getDamage() > 0.0f) {
                                                target.setDamage(0.0f);
                                            }
                                            Server.getInstance().broadCastAction(performer.getName() + " repairs " + target.getNameWithGenus() + ".", performer, 5);
                                            toReturn = true;
                                        }
                                    }
                                }
                            }
                            else if (action == 384) {
                                if (target.getOwnerId() == -10L && target.isMeditation()) {
                                    return Cults.meditate(performer, performer.getLayer(), act, counter, target);
                                }
                                toReturn = true;
                            }
                            else if (action == 385) {
                                if (target.getOwnerId() == -10L && target.isMeditation()) {
                                    final Cultist c2 = performer.getCultist();
                                    if (c2 != null) {
                                        final CultQuestion cq = new CultQuestion(performer, "Leaving the path", "Are you sure?", -1L, c2, c2.getPath(), true, false);
                                        cq.sendQuestion();
                                    }
                                }
                                toReturn = true;
                            }
                            else if (action == 386) {
                                if (target.getOwnerId() == -10L && target.isMeditation()) {
                                    final Cultist c2 = performer.getCultist();
                                    if (c2 != null && c2.getLevel() > 2) {
                                        final CultQuestion cq = new CultQuestion(performer, "Path leadership", "Leaders", -1L, c2, c2.getPath(), true, true);
                                        cq.sendQuestion();
                                    }
                                }
                                toReturn = true;
                            }
                            else if (action == 722) {
                                if (!Features.Feature.ALLOW_MEDPATHCHANGE.isEnabled()) {
                                    performer.getCommunicator().sendNormalServerMessage("That feature is not currently available.");
                                }
                                else if (target.getOwnerId() == -10L && target.isMeditation()) {
                                    final Cultist c2 = performer.getCultist();
                                    if (c2 != null && c2.getPath() == 4) {
                                        final ChangeMedPathQuestion cq2 = new ChangeMedPathQuestion(performer, c2, target);
                                        cq2.sendQuestion();
                                    }
                                }
                                toReturn = true;
                            }
                            else if (action == 59) {
                                toReturn = true;
                                if (target.getOwnerId() == performer.getWurmId() || target.lastOwner == performer.getWurmId() || performer.getPower() >= 2) {
                                    if (!target.isNoRename() && (!target.isVehicle() || target.isChair() || target.isTent())) {
                                        int maxSize = 20;
                                        if (target.isSign()) {
                                            int mod = 1;
                                            if (ttid == 209) {
                                                mod = 2;
                                            }
                                            maxSize = Math.max(5, (int)(target.getRarity() * 3 + target.getCurrentQualityLevel() * mod));
                                        }
                                        if (target.getTemplateId() == 651) {
                                            maxSize = 32;
                                        }
                                        final TextInputQuestion tiq = new TextInputQuestion(performer, "Setting description for " + target.getName() + ".", "Set the new description:", 1, target.getWurmId(), maxSize, target.getTemplateId() == 656);
                                        tiq.setOldtext(target.getDescription());
                                        tiq.sendQuestion();
                                    }
                                }
                                else if (!target.isNoRename() && target.isShelf()) {
                                    final Item shelfParent = target.getOuterItemOrNull();
                                    if (shelfParent != null && shelfParent.mayManage(performer)) {
                                        final int maxSize2 = 20;
                                        final TextInputQuestion tiq2 = new TextInputQuestion(performer, "Setting description for " + target.getName() + ".", "Set the new description:", 1, target.getWurmId(), maxSize2, false);
                                        tiq2.setOldtext(target.getDescription());
                                        tiq2.sendQuestion();
                                    }
                                }
                            }
                            else if (action == 518) {
                                if (ttid == 782) {
                                    final int digTilex = (int)performer.getStatus().getPositionX() + 2 >> 2;
                                    final int digTiley = (int)performer.getStatus().getPositionY() + 2 >> 2;
                                    toReturn = CaveTileBehaviour.raiseRockLevel(performer, target, digTilex, digTiley, counter, act);
                                }
                                else {
                                    comm.sendNormalServerMessage("You can not use this to raise ground.");
                                }
                            }
                            else if (action == 3 && (ttid == 175 || ttid == 651 || ttid == 1097 || ttid == 1098 || (ttid == 466 && target.getAuxData() == 1))) {
                                if (!performer.isWithinDistanceTo(target.getPosX(), target.getPosY(), target.getPosZ(), (target.isVehicle() && !target.isTent() && !target.isChair()) ? ((float)Math.max(4, target.getSizeZ() / 100)) : 4.0f)) {
                                    return true;
                                }
                                if (ttid == 175) {
                                    if (Servers.localServer.testServer) {
                                        if (performer.getPower() < 5) {
                                            comm.sendNormalServerMessage("Nothing happens here in these weird lands!");
                                            break Label_15319;
                                        }
                                    }
                                    try {
                                        final long parentId = target.getParentId();
                                        final Item parent = Items.getItem(parentId);
                                        parent.dropItem(target.getWurmId(), false);
                                        float qlCreated = 99.0f;
                                        int createdId = 527;
                                        if (target.getAuxData() == 1) {
                                            createdId = 602;
                                            qlCreated = 60.0f;
                                        }
                                        else if (target.getAuxData() == 2) {
                                            createdId = 653;
                                            qlCreated = 60.0f;
                                        }
                                        else if (target.getAuxData() == 3) {
                                            createdId = 700;
                                            qlCreated = 99.0f;
                                        }
                                        else if (target.getAuxData() == 4) {
                                            createdId = 738;
                                            qlCreated = 1.0f;
                                        }
                                        else if (target.getAuxData() == 5) {
                                            createdId = 791;
                                            qlCreated = 99.0f;
                                        }
                                        else if (target.getAuxData() == 6) {
                                            createdId = 844;
                                            qlCreated = 99.0f;
                                        }
                                        else if (target.getAuxData() == 7) {
                                            createdId = 972;
                                            qlCreated = 99.0f;
                                        }
                                        else if (target.getAuxData() == 8) {
                                            createdId = 1032;
                                            qlCreated = 99.0f;
                                        }
                                        else if (target.getAuxData() == 9) {
                                            createdId = 1297;
                                            qlCreated = 99.0f;
                                        }
                                        else if (target.getAuxData() == 10) {
                                            createdId = 1334;
                                            qlCreated = 99.0f;
                                        }
                                        else if (target.getAuxData() == 11) {
                                            createdId = 1437;
                                            qlCreated = 99.0f;
                                        }
                                        if (performer.getInventory() == null) {
                                            performer.getCommunicator().sendAlertServerMessage("Something went wrong while attempting to open your gift, please try again later. If this persists, please contact an administrator.");
                                            return true;
                                        }
                                        final Item gift = ItemFactory.createItem(createdId, qlCreated, performer.getName());
                                        performer.getInventory().insertItem(gift, true);
                                        comm.sendSafeServerMessage("There is something inside with your name on it!");
                                        if (target.getAuxData() == 1) {
                                            gift.setAuxData((byte)1);
                                            comm.sendSafeServerMessage("You hear a barely audible humming sound.");
                                        }
                                        else if (target.getAuxData() == 2) {
                                            createdId = 654;
                                            qlCreated = 99.0f;
                                            final Item liquid = ItemFactory.createItem(createdId, qlCreated, null);
                                            gift.insertItem(liquid, true);
                                        }
                                        else if (target.getAuxData() == 4) {
                                            gift.setAuxData((byte)99);
                                        }
                                        else if (target.getAuxData() == 6) {
                                            gift.setAuxData((byte)99);
                                        }
                                        Items.decay(target.getWurmId(), target.getDbStrings());
                                        SoundPlayer.playSong("sound.music.song.christmas", performer);
                                    }
                                    catch (NoSuchTemplateException nst) {
                                        ItemBehaviour.logger.log(Level.WARNING, performer.getName() + " Christmas present template gone? " + nst.getMessage(), nst);
                                    }
                                    catch (NoSuchItemException nsi) {
                                        ItemBehaviour.logger.log(Level.WARNING, performer.getName() + " Christmas present loss: " + nsi.getMessage(), nsi);
                                    }
                                    catch (FailedException fe) {
                                        ItemBehaviour.logger.log(Level.WARNING, performer.getName() + " receives no Christmas present: " + fe.getMessage(), fe);
                                    }
                                }
                                else {
                                    if (target.getOwnerId() != performer.getWurmId()) {
                                        comm.sendSafeServerMessage("You need to carry the " + target.getName() + " in order to open it.");
                                        return true;
                                    }
                                    try {
                                        if (performer.getPower() <= 0 && !target.getDescription().isEmpty() && !target.getDescription().equalsIgnoreCase(performer.getName()) && !target.getCreatorName().equalsIgnoreCase(performer.getName()) && (!Servers.localServer.PVPSERVER || Servers.localServer.testServer)) {
                                            comm.sendNormalServerMessage("This gift is not for you to open!");
                                            return false;
                                        }
                                        if (ttid == 651) {
                                            final long parentId = target.getParentId();
                                            final Item parent = Items.getItem(parentId);
                                            final long wurmid = target.getData();
                                            if (wurmid <= 0L) {
                                                comm.sendSafeServerMessage("The " + target.getName() + " is empty.");
                                                return true;
                                            }
                                            final Item toInsert = Items.getItem(wurmid);
                                            parent.dropItem(target.getWurmId(), false);
                                            comm.sendSafeServerMessage("There is " + toInsert.getNameWithGenus() + " inside with your name on it!");
                                            performer.getInventory().insertItem(toInsert, true);
                                        }
                                        else if (ttid == 1097 || ttid == 1098) {
                                            final int itemTemplate = this.getRandomItemFromPack(ttid);
                                            try {
                                                byte rarity = 0;
                                                if (itemTemplate == 867) {
                                                    if (Server.rand.nextInt(10) == 0) {
                                                        rarity = 2;
                                                    }
                                                    else {
                                                        rarity = 0;
                                                    }
                                                }
                                                final Item toInsert2 = ItemFactory.createItem(itemTemplate, 80.0f + Server.rand.nextFloat() * 20.0f, (byte)((ttid == 1098) ? 34 : 0), rarity, performer.getName());
                                                comm.sendSafeServerMessage("There is " + toInsert2.getNameWithGenus() + " inside with your name on it!");
                                                performer.getInventory().insertItem(toInsert2, true);
                                            }
                                            catch (Exception ex2) {
                                                ItemBehaviour.logger.log(Level.WARNING, performer.getName() + " opening gift pack:" + ex2.getMessage(), ex2);
                                            }
                                        }
                                        else if (ttid == 466 && target.getAuxData() == 1) {
                                            try {
                                                final Item chocolate = ItemFactory.createItem(1185, 50.0f + Server.rand.nextFloat() * 40.0f, "Easter Bunny");
                                                final Item sleepPowder = ItemFactory.createItem(666, 99.0f, "Easter Bunny");
                                                Item bonusItem = null;
                                                if (Server.rand.nextFloat() < 0.66f) {
                                                    bonusItem = ItemFactory.createItem(this.getRandomGem(true), 50.0f + Server.rand.nextFloat() * 49.0f, "Easter Bunny");
                                                }
                                                else {
                                                    bonusItem = ItemFactory.createItem(1307, 50.0f + Server.rand.nextFloat() * 40.0f, "Easter Bunny");
                                                    bonusItem.setRealTemplate(this.getRandomStatueFragment());
                                                    bonusItem.setAuxData((byte)1);
                                                    bonusItem.setData1(1);
                                                    bonusItem.setData2(30 + Server.rand.nextInt(50));
                                                    bonusItem.setWeight(bonusItem.getRealTemplate().getWeightGrams() / bonusItem.getRealTemplate().getFragmentAmount(), false);
                                                }
                                                comm.sendSafeServerMessage("You break the egg open and find some items!");
                                                performer.getInventory().insertItem(chocolate, true);
                                                performer.getInventory().insertItem(sleepPowder, true);
                                                performer.getInventory().insertItem(bonusItem, true);
                                            }
                                            catch (Exception e) {
                                                ItemBehaviour.logger.log(Level.WARNING, performer.getName() + " opening easter egg:" + e.getMessage(), e);
                                            }
                                        }
                                        Items.destroyItem(target.getWurmId());
                                    }
                                    catch (NoSuchItemException nsi) {
                                        comm.sendSafeServerMessage("Something was not in the package! It poofs gone. What a mess!");
                                        ItemBehaviour.logger.log(Level.WARNING, performer.getName() + " gift item loss: " + nsi.getMessage(), nsi);
                                    }
                                }
                            }
                            else if (action == 118) {
                                if (target.isGem()) {
                                    if (target.getData1() > 0 && performer.getWurmId() == target.getOwnerId()) {
                                        final int diff = (int)(performer.getFaith() - performer.getFavor());
                                        if (diff > 0) {
                                            final int avail = target.getData1();
                                            final int received = Math.min(diff, avail);
                                            try {
                                                final float qlMod = target.getRarity() + 1.0f;
                                                performer.setFavor(performer.getFavor() + received);
                                                target.setData1(target.getData1() - (int)(received / qlMod));
                                                target.setQualityLevel(target.getQualityLevel() - received / 2.0f / qlMod);
                                                if (target.getQualityLevel() < 1.0f) {
                                                    Items.destroyItem(target.getWurmId());
                                                }
                                                comm.sendNormalServerMessage("You feel a rush of blood to your head as the power from the gem enters your body.");
                                                Server.getInstance().broadCastAction(performer.getName() + " straightens up as " + performer.getHeSheItString() + " draws power from a gem.", performer, 5);
                                                if (received >= 50.0f) {
                                                    performer.achievement(625);
                                                }
                                            }
                                            catch (IOException iox2) {
                                                ItemBehaviour.logger.log(Level.WARNING, performer.getName() + ": " + iox2.getMessage(), iox2);
                                            }
                                        }
                                    }
                                }
                                else if (target.isDeathProtection()) {
                                    toReturn = true;
                                    if (performer.getWurmId() == target.getOwnerId()) {
                                        if (performer.isDeathProtected()) {
                                            comm.sendNormalServerMessage("Nothing happens.");
                                        }
                                        else {
                                            comm.sendNormalServerMessage("Your skin tickles all over for a few seconds, as if a thousand ants crawled upon it.");
                                            Server.getInstance().broadCastAction(performer.getName() + " shivers for a few seconds as " + performer.getHeSheItString() + " uses " + target.getNameWithGenus() + ".", performer, 5);
                                            performer.setDeathProtected(true);
                                            Items.destroyItem(target.getWurmId());
                                            performer.achievement(153);
                                        }
                                    }
                                }
                                else if (ttid == 527) {
                                    if (target.getOwnerId() == performer.getWurmId()) {
                                        if (performer.getEnemyPresense() <= 0) {
                                            performer.achievement(5);
                                            performer.activeFarwalkerAmulet(target);
                                        }
                                        else {
                                            comm.sendNormalServerMessage("You fiddle with the " + target.getName() + " but are too stressed to use it.");
                                        }
                                    }
                                    else {
                                        comm.sendNormalServerMessage("You need to carry the " + target.getName() + " in order to use it.");
                                    }
                                }
                                else if (ttid == 738) {
                                    if (target.getAuxData() > 0) {
                                        String sound = "sound.emote.chuckle";
                                        if (target.getAuxData() > 20) {
                                            if (target.getCurrentQualityLevel() > 80.0f) {
                                                sound = "sound.emote.laugh";
                                            }
                                            else if (target.getCurrentQualityLevel() > 60.0f) {
                                                sound = "sound.emote.applaud";
                                            }
                                            else if (target.getCurrentQualityLevel() > 30.0f) {
                                                sound = "sound.emote.call";
                                            }
                                        }
                                        else if (target.getCurrentQualityLevel() > 80.0f) {
                                            sound = "sound.emote.curse";
                                        }
                                        else if (target.getCurrentQualityLevel() > 60.0f) {
                                            sound = "sound.emote.insult";
                                        }
                                        else if (target.getCurrentQualityLevel() > 30.0f) {
                                            sound = "sound.emote.disagree";
                                        }
                                        else {
                                            sound = "sound.emote.worry";
                                        }
                                        SoundPlayer.playSound(sound, target, 1.5f);
                                        comm.sendAnimation(target.getWurmId(), "model.animation.use", false, false);
                                        target.setAuxData((byte)(target.getAuxData() - 1));
                                    }
                                }
                                else if (ttid == 972) {
                                    comm.sendAnimation(target.getWurmId(), "use", false, false);
                                    comm.sendNormalServerMessage("The Yule Goat likes that!");
                                }
                                else if (ttid == 741) {
                                    if (target.getOwnerId() == performer.getWurmId()) {
                                        comm.sendNormalServerMessage("You can't use the " + target.getName() + " while carrying it.");
                                    }
                                    else {
                                        performer.activeFarwalkerAmulet(target);
                                    }
                                }
                                else if (ttid == 5 || ttid == 834 || ttid == 836) {
                                    if (target.getOwnerId() == performer.getWurmId()) {
                                        performer.activePotion(target);
                                    }
                                    else {
                                        comm.sendNormalServerMessage("You need to carry the " + target.getName() + " in order to use it.");
                                    }
                                }
                                else if (ttid == 781 || ttid == 1300) {
                                    if (target.getOwnerId() == performer.getWurmId()) {
                                        if (ttid == 781 || target.getAuxData() == 1) {
                                            comm.sendCustomizeFace(performer.getFace(), target.getWurmId());
                                        }
                                        else {
                                            final ChangeAppearanceQuestion question = new ChangeAppearanceQuestion(performer, target);
                                            question.sendQuestion();
                                        }
                                        toReturn = true;
                                    }
                                }
                                else if (ttid == 722) {
                                    if (target.getOwnerId() != performer.getWurmId()) {
                                        toReturn = MethodsItems.useBellTower(performer, target, act, counter);
                                    }
                                }
                                else if (ttid == 719) {
                                    if (target.getOwnerId() == performer.getWurmId()) {
                                        toReturn = false;
                                        if (counter == 1.0f) {
                                            if (Server.rand.nextInt(100) == 0) {
                                                SoundPlayer.playSound("sound.bell.handbell.long", performer, 1.5f);
                                            }
                                            else {
                                                SoundPlayer.playSound("sound.bell.handbell", performer, 1.5f);
                                            }
                                            performer.sendActionControl(Actions.actionEntrys[118].getVerbString(), true, 50);
                                        }
                                        if (counter >= 5.0f) {
                                            toReturn = true;
                                        }
                                    }
                                }
                                else {
                                    if (target.isServerPortal()) {
                                        String title = "Using a portal";
                                        final int data1 = target.getData1();
                                        ServerEntry entry = Servers.getServerWithId(data1);
                                        if (entry != null) {
                                            if (entry.id == Servers.loginServer.id) {
                                                entry = Servers.loginServer;
                                            }
                                            if (performer.getPower() == 0 && entry.entryServer && !entry.testServer) {
                                                title = "Dormant portal";
                                            }
                                            else if (entry.HOMESERVER) {
                                                if (entry.PVPSERVER) {
                                                    title = Kingdoms.getNameFor(entry.KINGDOM) + " HOME";
                                                }
                                                else {
                                                    title = "Dormant portal";
                                                }
                                            }
                                        }
                                        final PortalQuestion port = new PortalQuestion(performer, target.getName(), title, target);
                                        port.sendQuestion();
                                        Server.getInstance().broadCastAction(performer.getName() + " approaches the " + target.getName() + ".", performer, 5);
                                        return true;
                                    }
                                    if (ttid == 602) {
                                        if (target.getOwnerId() == performer.getWurmId()) {
                                            final int current = target.getAuxData();
                                            if (current <= 1) {
                                                comm.sendNormalServerMessage("You rub the wand and it makes a humming sound.");
                                                target.setAuxData((byte)3);
                                                SoundPlayer.playSound("sound.fx.humm", performer, 1.0f);
                                            }
                                            else if (current == 3) {
                                                comm.sendNormalServerMessage("You rub the wand and it makes a loud humming sound.");
                                                target.setAuxData((byte)5);
                                                SoundPlayer.playSound("sound.fx.humm", performer, 1.0f);
                                            }
                                            else if (current == 5) {
                                                comm.sendNormalServerMessage("You rub the wand and it makes a barely audible humming sound.");
                                                target.setAuxData((byte)1);
                                                SoundPlayer.playSound("sound.fx.humm", performer, 1.0f);
                                            }
                                        }
                                    }
                                    else if (ttid == 843) {
                                        if (Features.Feature.NAMECHANGE.isEnabled()) {
                                            if (target.getOwnerId() == performer.getWurmId()) {
                                                final ChangeNameQuestion ncq = new ChangeNameQuestion(performer, target);
                                                ncq.sendQuestion();
                                                toReturn = true;
                                            }
                                        }
                                        else {
                                            comm.sendNormalServerMessage("This feature is disabled for now.");
                                        }
                                    }
                                    else if (target.isAbility()) {
                                        toReturn = Abilities.useItem(performer, target, act, counter);
                                    }
                                    else if (target.isDuelRing()) {
                                        toReturn = true;
                                        if (performer.getPower() >= 3) {
                                            if (doesRingMarkersExist(target.getTileX(), target.getTileY())) {
                                                return true;
                                            }
                                            createMarkers(target.getTileX(), target.getTileY());
                                        }
                                    }
                                    else if (target.getTemplateId() == 1438) {
                                        toReturn = true;
                                        final AffinityQuestion aq = new AffinityQuestion(performer, target);
                                        aq.sendQuestion();
                                    }
                                    else {
                                        toReturn = true;
                                    }
                                }
                            }
                            else if (action == 682) {
                                toReturn = true;
                                if (ttid == 233 && target.getOwnerId() == performer.getWurmId()) {
                                    toReturn = MethodsItems.usePendulum(performer, target, act, counter);
                                }
                            }
                            else if (action == 3 && target.isHollow() && !target.isSealedByPlayer() && (!target.getTemplate().hasViewableSubItems() || target.getTemplate().isContainerWithSubItems() || performer.getPower() > 0)) {
                                if (target.isSealedByPlayer()) {
                                    return true;
                                }
                                if (target.getTemplateId() == 1342 && !target.isPlanted()) {
                                    return true;
                                }
                                toReturn = true;
                                if (target.getWurmId() != performer.getVehicle() && !performer.isWithinDistanceTo(target.getPosX(), target.getPosY(), target.getPosZ(), (target.isVehicle() && !target.isTent()) ? ((float)Math.max(6, target.getSizeZ() / 100)) : 6.0f)) {
                                    return toReturn;
                                }
                                final boolean isTop = target.getWurmId() == target.getTopParent() || (target.getTopParentOrNull() != null && target.getTopParentOrNull().getTemplate().hasViewableSubItems() && (!target.getTopParentOrNull().getTemplate().isContainerWithSubItems() || target.isPlacedOnParent()));
                                if (!isTop) {
                                    if (target.isLockable()) {
                                        if (target.getLockId() == -10L || (target.isDraggable() && MethodsItems.mayUseInventoryOfVehicle(performer, target)) || (target.getTemplateId() == 850 && MethodsItems.mayUseInventoryOfVehicle(performer, target)) || (target.isLocked() && target.mayAccessHold(performer))) {
                                            comm.sendOpenInventoryContainer(target.getWurmId());
                                        }
                                        else {
                                            try {
                                                final Item lock2 = Items.getItem(target.getLockId());
                                                if (!lock2.getLocked() || target.isOwner(performer)) {
                                                    comm.sendOpenInventoryContainer(target.getWurmId());
                                                    return true;
                                                }
                                                final long[] keys2 = lock2.getKeyIds();
                                                for (int j = 0; j < keys2.length; ++j) {
                                                    final Item key2 = Items.getItem(keys2[j]);
                                                    if (key2.getTopParent() == performer.getInventory().getWurmId()) {
                                                        comm.sendOpenInventoryContainer(target.getWurmId());
                                                        return true;
                                                    }
                                                }
                                                comm.sendSafeServerMessage("The " + target.getName() + " is locked. Please use the key to unlock and open it.");
                                            }
                                            catch (NoSuchItemException nsi2) {
                                                comm.sendSafeServerMessage("The " + target.getName() + " is locked. Please use the key to unlock and open it.");
                                            }
                                        }
                                    }
                                    else if (target.getTemplateId() == 272 && target.getWasBrandedTo() != -10L) {
                                        if (!target.mayCommand(performer)) {
                                            performer.getCommunicator().sendNormalServerMessage("You do not have permissions.");
                                            return true;
                                        }
                                        comm.sendOpenInventoryContainer(target.getWurmId());
                                    }
                                    else {
                                        comm.sendOpenInventoryContainer(target.getWurmId());
                                    }
                                    return true;
                                }
                                if (target.getTemplateId() == 272 && target.getWasBrandedTo() != -10L && !target.mayCommand(performer)) {
                                    performer.getCommunicator().sendNormalServerMessage("You do not have permissions.");
                                    return true;
                                }
                                if ((target.getTemplateId() == 1239 || target.getTemplateId() == 1175) && target.hasQueen() && !WurmCalendar.isSeasonWinter() && performer.getBestBeeSmoker() == null && performer.getPower() < 2) {
                                    performer.getCommunicator().sendSafeServerMessage("The bees get angry and defend the " + target.getName() + " by stinging you.");
                                    performer.addWoundOfType(null, (byte)5, 2, true, 1.0f, false, 5000.0f + Server.rand.nextFloat() * 7000.0f, 0.0f, 30.0f, false, false);
                                    return true;
                                }
                                if (target.getTemplateId() == 1239) {
                                    Achievements.triggerAchievement(performer.getWurmId(), 552);
                                }
                                if (target.isLockable()) {
                                    if (target.getLockId() == -10L || (target.isDraggable() && MethodsItems.mayUseInventoryOfVehicle(performer, target)) || (target.getTemplateId() == 850 && MethodsItems.mayUseInventoryOfVehicle(performer, target))) {
                                        if (performer.addItemWatched(target)) {
                                            if (target.getTemplateId() == 995 && target.getAuxData() < 100) {
                                                performer.achievement(367);
                                                target.setAuxData((byte)100);
                                            }
                                            if (target.getDescription().isEmpty()) {
                                                comm.sendOpenInventoryWindow(target.getWurmId(), target.getName());
                                            }
                                            else {
                                                comm.sendOpenInventoryWindow(target.getWurmId(), target.getName() + " [" + target.getDescription() + "]");
                                            }
                                            target.addWatcher(target.getWurmId(), performer);
                                            target.sendContainedItems(target.getWurmId(), performer);
                                        }
                                    }
                                    else if (target.getLockId() != -10L) {
                                        try {
                                            final Item lock2 = Items.getItem(target.getLockId());
                                            final boolean hasKey = performer.hasKeyForLock(lock2) || target.isOwner(performer);
                                            if (hasKey || (target.isLocked() && target.mayAccessHold(performer))) {
                                                if (performer.addItemWatched(target)) {
                                                    if (target.getDescription().isEmpty()) {
                                                        comm.sendOpenInventoryWindow(target.getWurmId(), target.getName());
                                                    }
                                                    else {
                                                        comm.sendOpenInventoryWindow(target.getWurmId(), target.getName() + " [" + target.getDescription() + "]");
                                                    }
                                                    target.addWatcher(target.getWurmId(), performer);
                                                    target.sendContainedItems(target.getWurmId(), performer);
                                                }
                                            }
                                            else {
                                                comm.sendSafeServerMessage("The " + target.getName() + " is locked. Please use the key to unlock and open it.");
                                            }
                                        }
                                        catch (NoSuchItemException nsi2) {
                                            comm.sendSafeServerMessage("The " + target.getName() + " is locked. Please use the key to unlock and open it.");
                                        }
                                    }
                                    else {
                                        comm.sendSafeServerMessage("The " + target.getName() + " is locked. Please use the key to unlock and open it.");
                                    }
                                }
                                else if (performer.addItemWatched(target)) {
                                    if (target.getTemplateId() == 995 && target.getAuxData() < 100) {
                                        performer.achievement(367);
                                        target.setAuxData((byte)100);
                                    }
                                    if (target.getDescription().isEmpty()) {
                                        comm.sendOpenInventoryWindow(target.getWurmId(), target.getName());
                                    }
                                    else {
                                        comm.sendOpenInventoryWindow(target.getWurmId(), target.getName() + " [" + target.getDescription() + "]");
                                    }
                                    target.addWatcher(target.getWurmId(), performer);
                                    target.sendContainedItems(target.getWurmId(), performer);
                                }
                            }
                            else if (action == 740 && !target.isLiquid()) {
                                if (target.isSealedByPlayer()) {
                                    if (target.isCrate()) {
                                        if (performer.getWurmId() == target.getLastOwnerId() || performer.getPower() > 1) {
                                            toReturn = MethodsItems.removeSecuritySeal(performer, target, act);
                                        }
                                        else {
                                            comm.sendSafeServerMessage("Only the last owner can remove the security seal on the " + target.getName() + ".");
                                        }
                                    }
                                    else {
                                        toReturn = MethodsItems.breakSeal(performer, target, act);
                                    }
                                }
                                else if (target.isWrapped()) {
                                    toReturn = MethodsItems.unwrap(performer, target, act);
                                }
                            }
                            else if (action == 19) {
                                if (target.isSealedByPlayer()) {
                                    this.tasteLiquid(performer, target);
                                }
                                else if (target.isFood() || target.isLiquid()) {
                                    this.taste(performer, target);
                                }
                                else if (target.isContainerLiquid() && target.getItemCount() == 1) {
                                    for (final Item k : target.getItems()) {
                                        if (!k.isNoEatOrDrink() && !k.isUndistilled() && k.isDrinkable()) {
                                            this.taste(performer, k);
                                            break;
                                        }
                                    }
                                }
                            }
                            else if (action == 739) {
                                if (target.isRaw() && target.canBeRawWrapped() && !target.isWrapped()) {
                                    toReturn = MethodsItems.wrap(performer, null, target, act);
                                }
                            }
                            else if (action >= 496 && action <= 502) {
                                toReturn = MethodsReligion.performRitual(performer, null, target, counter, action, act);
                            }
                            else if (action == 504) {
                                toReturn = MethodsItems.conquerTarget(performer, target, comm, counter, act);
                            }
                            else if (action == 336) {
                                toReturn = true;
                                if (target.isMailBox()) {
                                    if (target.isEmpty(false)) {
                                        WurmMailSender.checkForWurmMail(performer, target);
                                    }
                                    else {
                                        comm.sendNormalServerMessage("The mailbox needs to be empty.");
                                    }
                                }
                            }
                            else if (action == 337) {
                                toReturn = true;
                                if (target.isMailBox()) {
                                    if (!target.isEmpty(false)) {
                                        WurmMailSender.sendWurmMail(performer, target);
                                    }
                                    else {
                                        comm.sendNormalServerMessage("The mailbox is empty.");
                                    }
                                }
                            }
                            else if (action == 4 && target.isHollow()) {
                                toReturn = true;
                                target.close(performer);
                            }
                            else if (action == 181 || action == 99 || action == 697 || action == 696 || action == 864) {
                                if (target.isMoveable(performer) && ((!target.isRoadMarker() && target.getTemplateId() != 1342) || !target.isPlanted())) {
                                    toReturn = MethodsItems.moveItem(performer, target, counter, action, act);
                                }
                                else {
                                    comm.sendNormalServerMessage("You may not move that item right now.");
                                }
                            }
                            else if (action == 177 || action == 178) {
                                if (target.isTurnable(performer) && (!target.isRoadMarker() || !target.isPlanted())) {
                                    toReturn = MethodsItems.moveItem(performer, target, counter, action, act);
                                }
                                else {
                                    comm.sendNormalServerMessage("You may not turn that item right now.");
                                }
                            }
                            else if (action == 926) {
                                toReturn = MethodsItems.placeLargeItem(performer, target, act, counter);
                            }
                            else if (action == 74) {
                                boolean ok = target.isDraggable();
                                boolean doneMsg = false;
                                if (performer.isTeleporting()) {
                                    ok = false;
                                }
                                if (target.isVehicle()) {
                                    final Vehicle vehic2 = Vehicles.getVehicle(target);
                                    if (vehic2.pilotId != -10L) {
                                        ok = false;
                                    }
                                    if (vehic2.draggers != null && !vehic2.draggers.isEmpty()) {
                                        ok = false;
                                    }
                                    if (target.getTemplateId() != 186 && target.getKingdom() != performer.getKingdomId()) {
                                        ok = false;
                                    }
                                    final boolean havePermission = VehicleBehaviour.hasPermission(performer, target);
                                    if (!havePermission && !target.mayDrag(performer)) {
                                        ok = false;
                                    }
                                }
                                if (performer.getVehicle() != -10L) {
                                    ok = false;
                                }
                                if (!GeneralUtilities.isOnSameLevel(performer, target)) {
                                    final VolaTile theTile = Zones.getTileOrNull(target.getTileX(), target.getTileY(), target.isOnSurface());
                                    ok = false;
                                    if (theTile != null) {
                                        if (theTile.getStructure() != null) {
                                            comm.sendNormalServerMessage("You must be on the same floor level to start dragging.");
                                        }
                                        else {
                                            comm.sendNormalServerMessage("You need to get closer to the " + target.getName() + ".");
                                        }
                                        doneMsg = true;
                                    }
                                }
                                if (target.getTemplateId() == 1125 && (target.isBusy() || (System.currentTimeMillis() - target.lastRammed < 30000L && performer.getWurmId() != target.lastRamUser))) {
                                    ok = false;
                                    comm.sendNormalServerMessage("You cannot drag the " + target.getName() + " while it is being used.");
                                    doneMsg = true;
                                }
                                if (performer.getDraggedItem() != null) {
                                    ok = false;
                                    comm.sendNormalServerMessage("You need to stop dragging the " + performer.getDraggedItem().getName() + " first.");
                                    doneMsg = true;
                                }
                                else if (ok && !Items.isItemDragged(target)) {
                                    if (!performer.isGuest()) {
                                        toReturn = MethodsItems.startDragging(act, performer, target);
                                    }
                                    else {
                                        comm.sendNormalServerMessage("You are not allowed to drag items as a guest.");
                                    }
                                }
                                else if (!doneMsg) {
                                    comm.sendNormalServerMessage("You are not allowed to drag that now.");
                                }
                            }
                            else {
                                if (action == 75) {
                                    if (performer.isWithinDistanceTo(target.getPosX(), target.getPosY(), target.getPosZ(), 4.0f)) {
                                        toReturn = MethodsItems.stopDragging(performer, target);
                                    }
                                    return true;
                                }
                                if (action == 182) {
                                    if (!target.isNoEatOrDrink()) {
                                        toReturn = false;
                                        if (act.justTickedSecond()) {
                                            toReturn = MethodsItems.eat(act, performer, target, counter);
                                        }
                                    }
                                }
                                else if (action == 328) {
                                    if (target.isEgg()) {
                                        comm.sendNormalServerMessage("You suck on the " + target.getName() + " a little. Weird feeling.");
                                        Server.getInstance().broadCastAction(performer.getName() + " sucks on " + target.getNameWithGenus() + ". A strange feeling runs through you.", performer, 3);
                                    }
                                }
                                else if (action == 330) {
                                    toReturn = true;
                                    if (target.isEgg() && target.getTemplateId() == 465) {
                                        if (target.hatching) {
                                            comm.sendNormalServerMessage("The " + target.getName() + " is already hatching.");
                                        }
                                        else if (target.getDamage() > 80.0f || performer.getPower() >= 5) {
                                            comm.sendNormalServerMessage("You make a small hole in the " + target.getName() + ".");
                                            Server.getInstance().broadCastAction(performer.getName() + " makes a small hole in " + target.getNameWithGenus() + "!", performer, 20);
                                            target.hatching = true;
                                        }
                                        else {
                                            comm.sendNormalServerMessage("The shell of the " + target.getName() + " is too hard still.");
                                        }
                                    }
                                }
                                else if (action == 325) {
                                    toReturn = MethodsItems.askSleep(act, performer, target, counter);
                                }
                                else if (action == 140) {
                                    toReturn = MethodsItems.sleep(act, performer, target, counter);
                                }
                                else if (action == 365 || action == 366 || action == 367 || action == 320 || action == 319 || action == 322 || action == 321 || action == 323) {
                                    toReturn = MethodsItems.setRent(act, performer, target);
                                }
                                else if (action == 324) {
                                    toReturn = false;
                                    if (target.getData() != performer.getWurmId()) {
                                        toReturn = MethodsItems.rent(act, performer, target);
                                    }
                                    if (!toReturn) {
                                        toReturn = MethodsItems.askSleep(act, performer, target, counter);
                                    }
                                }
                                else if (action == 183) {
                                    if (ttid == 1101) {
                                        return MethodsItems.drinkChampagne(performer, target);
                                    }
                                    if (target.isContainerLiquid() && target.getItemCount() == 1) {
                                        for (final Item k : target.getItems()) {
                                            if (!k.isNoEatOrDrink() && !k.isUndistilled() && k.isDrinkable()) {
                                                toReturn = false;
                                                if (act.justTickedSecond()) {
                                                    toReturn = MethodsItems.drink(act, performer, k, counter);
                                                    break;
                                                }
                                                break;
                                            }
                                        }
                                    }
                                    if (!target.isFood() && !target.isNoEatOrDrink() && !target.isUndistilled() && target.isDrinkable()) {
                                        toReturn = false;
                                        if (act.justTickedSecond()) {
                                            toReturn = MethodsItems.drink(act, performer, target, counter);
                                        }
                                    }
                                }
                                else if (action == 600) {
                                    if (target.isInstaDiscard()) {
                                        toReturn = false;
                                        if (act.justTickedSecond()) {
                                            toReturn = Methods.discardSellItem(performer, act, target, counter);
                                        }
                                    }
                                }
                                else if (action == 91) {
                                    if (target.getTemplateId() == 442) {
                                        if (performer.getStatus().canEat()) {
                                            String eatString = "You eat some Christmas ham and spare ribs in gravy with Jansson's frestelse topped with apple jam.";
                                            final int food = Server.rand.nextInt(4);
                                            if (food == 0) {
                                                eatString = "You can't keep off the candy. You swallow all sorts of chocolate, marshmallows, marzipan pigs, and top it off with a fruit salad with whipped cream.";
                                            }
                                            else if (food == 1) {
                                                eatString = "The fish is delicious. Red salmon with cooked potatoes and mayonnaise. The pickled herring is particularly good.";
                                            }
                                            else if (food == 2) {
                                                eatString = "You serve yourself from the cold dishes. Roast beef, potato and mimosa salad. Various kinds of salami and sausage. Yum!";
                                            }
                                            comm.sendNormalServerMessage(eatString);
                                            Server.getInstance().broadCastAction(performer.getName() + " returns from the " + target.getName() + " with a loud burp.", performer, 5);
                                            performer.getStatus().setMaxCCFP();
                                            performer.getStatus().refresh(0.99f, true);
                                        }
                                        else {
                                            comm.sendNormalServerMessage("You can't bring yourself to eat more right now.");
                                        }
                                    }
                                    toReturn = true;
                                }
                                else if (action == 86) {
                                    toReturn = true;
                                    if (target.isFullprice()) {
                                        comm.sendNormalServerMessage("You cannot set the price of that object.");
                                    }
                                    else {
                                        Methods.sendSinglePriceQuestion(performer, target);
                                    }
                                }
                                else if (action == 87) {
                                    toReturn = true;
                                    if (target.isFullprice()) {
                                        comm.sendNormalServerMessage("Price is set to " + Economy.getEconomy().getChangeFor(target.getValue()).getChangeString() + ".");
                                    }
                                    else if (target.getPrice() <= 0) {
                                        if (target.getValue() <= 1) {
                                            comm.sendNormalServerMessage("A trader would deem this pretty worthless.");
                                        }
                                        else {
                                            comm.sendNormalServerMessage("A trader would sell this for about " + Economy.getEconomy().getChangeFor(target.getValue() / 2).getChangeString() + ".");
                                        }
                                    }
                                    else {
                                        comm.sendNormalServerMessage("Price is set to " + Economy.getEconomy().getChangeFor(target.getPrice()).getChangeString() + ".");
                                    }
                                    if (MethodsReligion.canBeSacrificed(target)) {
                                        final Deity deity = performer.getDeity();
                                        final float favorValue = MethodsReligion.getFavorValue(deity, target);
                                        final float favorMod = MethodsReligion.getFavorModifier(deity, target);
                                        String deityName = "a deity";
                                        if (deity != null) {
                                            deityName = deity.getName();
                                        }
                                        final int favorGain = (int)(favorValue * favorMod / 1000.0f);
                                        if (favorGain > 1.0f) {
                                            comm.sendNormalServerMessage(String.format("You think you can sacrifice this to %s for about %d favor.", deityName, favorGain));
                                        }
                                        else {
                                            comm.sendNormalServerMessage(String.format("You think %s would not provide much favor for sacrificing this.", deityName));
                                        }
                                    }
                                }
                                else if (action == 133) {
                                    if (Methods.isActionAllowed(performer, action)) {
                                        toReturn = MethodsItems.unstringBow(performer, target, act, counter);
                                    }
                                }
                                else if (action == 53) {
                                    toReturn = true;
                                    if (target.isFire() || target.isLight() || target.isForgeOrOven() || target.getTemplateId() == 1243 || target.getTemplateId() == 889 || target.getTemplateId() == 1023 || target.getTemplateId() == 1028 || target.getTemplateId() == 1178) {
                                        if (target.isOnFire()) {
                                            if (target.getTemplateId() == 729) {
                                                comm.sendNormalServerMessage("You blow out the " + target.getName() + ".");
                                                Server.getInstance().broadCastAction(performer.getName() + " blows out " + target.getNameWithGenus() + ".", performer, 5);
                                                if (target.isAlwaysLit()) {
                                                    comm.sendNormalServerMessage("The candles quickly relight.");
                                                }
                                                else {
                                                    target.setTemperature((short)200);
                                                }
                                            }
                                            else {
                                                comm.sendNormalServerMessage("You snuff the " + target.getName() + ".");
                                                Server.getInstance().broadCastAction(performer.getName() + " snuffs " + target.getNameWithGenus() + ".", performer, 5);
                                                if (target.isAlwaysLit()) {
                                                    comm.sendNormalServerMessage("Magically the " + target.getName() + " quickly relights.");
                                                }
                                                else {
                                                    target.setTemperature((short)200);
                                                    if (target.getTemplateId() == 889) {
                                                        target.deleteFireEffect();
                                                    }
                                                }
                                            }
                                        }
                                        else {
                                            comm.sendNormalServerMessage("The " + target.getName() + " is not burning.");
                                        }
                                    }
                                }
                                else if (action == 719) {
                                    final byte kId = performer.getKingdomId();
                                    if (performer.getPower() >= 2 && (target.getTemplateId() == 176 || target.getTemplateId() == 315)) {
                                        final KingdomMembersQuestion kmq = new KingdomMembersQuestion(performer, target.getWurmId(), (byte)0);
                                        kmq.sendQuestion();
                                    }
                                    else if (Kingdoms.isCustomKingdom(kId) && King.isKing(performer.getWurmId(), kId)) {
                                        final KingdomMembersQuestion kmq = new KingdomMembersQuestion(performer, target.getWurmId(), Kingdoms.getNameFor(kId), kId);
                                        kmq.sendQuestion();
                                    }
                                }
                                else if (action == 89) {
                                    toReturn = true;
                                    if (performer.getPower() <= 0) {
                                        MethodsCreatures.sendSetKingdomQuestion(performer, target);
                                    }
                                    else if (WurmPermissions.mayUseDeityWand(performer)) {
                                        MethodsCreatures.sendSetKingdomQuestion(performer, target);
                                    }
                                }
                                else if (action == 467) {
                                    toReturn = true;
                                    if (performer.getPower() >= 2 && target.getOwnerId() == performer.getWurmId()) {
                                        final GmInterface gmi = new GmInterface(performer, target.getWurmId());
                                        gmi.sendQuestion();
                                    }
                                }
                                else if (action == 535) {
                                    toReturn = true;
                                    if (performer.getPower() >= 4 && target.getOwnerId() == performer.getWurmId()) {
                                        final FeatureManagement fm = new FeatureManagement(performer, target.getWurmId());
                                        fm.sendQuestion();
                                    }
                                }
                                else if (action == 609 && Servers.isThisLoginServer()) {
                                    toReturn = true;
                                    if (performer.getPower() >= 4 && target.getOwnerId() == performer.getWurmId()) {
                                        final InGameVoteSetupQuestion igvsq = new InGameVoteSetupQuestion(performer);
                                        igvsq.sendQuestion();
                                    }
                                }
                                else if (action == 635 && Servers.isThisLoginServer()) {
                                    toReturn = true;
                                    if (performer.getPower() >= 4 && target.getOwnerId() == performer.getWurmId()) {
                                        final GroupCAHelpQuestion gchq = new GroupCAHelpQuestion(performer);
                                        gchq.sendQuestion();
                                    }
                                }
                                else if (action == 353) {
                                    if (Servers.localServer.isChallengeServer()) {
                                        toReturn = true;
                                    }
                                    else if (ttid == 538) {
                                        if (King.getKing((byte)2) != null) {
                                            toReturn = true;
                                            comm.sendNormalServerMessage("The " + King.getRulerTitle(King.getKing((byte)2).gender == 0, (byte)2) + " is appointed already. The stone is empty.");
                                            Methods.resetMolrStone();
                                        }
                                        else {
                                            toReturn = Methods.aspireKing(performer, (byte)2, target, null, act, counter);
                                            if (toReturn) {
                                                Methods.resetMolrStone();
                                            }
                                        }
                                    }
                                }
                                else if (action == 480) {
                                    if (ttid == 682 && Servers.localServer.PVPSERVER) {
                                        final KingdomFoundationQuestion kfq = new KingdomFoundationQuestion(performer, target.getWurmId());
                                        kfq.sendQuestion();
                                    }
                                }
                                else if (action == 115 && target.getTemplateId() == 1024) {
                                    final ConchQuestion question2 = new ConchQuestion(performer, target.getWurmId());
                                    question2.sendQuestion();
                                    performer.playPersonalSound("sound.fx.conch");
                                }
                                else if (action == 214) {
                                    if (ttid == 652) {
                                        if (performer.isReallyPaying() || performer.getPower() >= 2) {
                                            if (!performer.hasFlag(63)) {
                                                if (WurmCalendar.isChristmas()) {
                                                    if (!performer.hasFlag(62)) {
                                                        if (performer.getKingdomTemplateId() == 3) {
                                                            comm.sendNormalServerMessage("Seems that you have been a bad person this year, " + performer.getName() + ". There is a gift for you beneath the tree!");
                                                        }
                                                        else if (performer.getReputation() < 0) {
                                                            comm.sendNormalServerMessage("You have been a bad person this year, " + performer.getName() + ", but there is a gift for you anyways.");
                                                        }
                                                        else {
                                                            comm.sendNormalServerMessage("You have been a good person this year, " + performer.getName() + ". Santa left a gift for you beneath the tree!");
                                                        }
                                                        awardChristmasPresent(performer);
                                                    }
                                                    else {
                                                        comm.sendNormalServerMessage("There are no more gifts for you from Santa beneath the tree.");
                                                    }
                                                }
                                                else {
                                                    comm.sendNormalServerMessage("It is not christmas so there are no gifts from Santa beneath the tree.");
                                                }
                                            }
                                            else {
                                                comm.sendNormalServerMessage("You need to have paid for premium time to receive this year's gift.");
                                            }
                                        }
                                        else {
                                            comm.sendNormalServerMessage("You need to be a premium player to receive this year's gift.");
                                        }
                                    }
                                }
                                else if (action == 285 && !target.isSealedByPlayer() && (target.isFoodMaker() || target.getTemplate().isCooker())) {
                                    this.showRecipeInfo(performer, null, target);
                                }
                                else if (action == 79) {
                                    comm.sendNormalServerMessage("Nerd.");
                                }
                                else if (action == 688) {
                                    if (target.canHavePermissions() && target.mayManage(performer)) {
                                        ManageObjectList.Type molt = ManageObjectList.Type.ITEM;
                                        if (target.isBed()) {
                                            molt = ManageObjectList.Type.BED;
                                        }
                                        if (target.getTemplateId() == 1271) {
                                            molt = ManageObjectList.Type.MESSAGE_BOARD;
                                        }
                                        if (target.getTemplateId() == 272) {
                                            molt = ManageObjectList.Type.CORPSE;
                                        }
                                        final ManagePermissions mp = new ManagePermissions(performer, molt, target, false, -10L, false, null, "");
                                        mp.sendQuestion();
                                    }
                                }
                                else if (action == 691) {
                                    if (target.canHavePermissions() && target.maySeeHistory(performer)) {
                                        final PermissionsHistory ph = new PermissionsHistory(performer, target.getWurmId());
                                        ph.sendQuestion();
                                    }
                                }
                                else if (action == 725) {
                                    this.setVolume(performer, target, 12);
                                }
                                else if (action == 726) {
                                    this.setVolume(performer, target, 11);
                                }
                                else if (action == 727) {
                                    this.setVolume(performer, target, 10);
                                }
                                else if (action == 728) {
                                    this.setVolume(performer, target, 9);
                                }
                                else if (action == 729) {
                                    this.setVolume(performer, target, 8);
                                }
                                else if (action == 730) {
                                    this.setVolume(performer, target, 7);
                                }
                                else if (action == 731) {
                                    this.setVolume(performer, target, 6);
                                }
                                else if (action == 732) {
                                    this.setVolume(performer, target, 5);
                                }
                                else if (action == 733) {
                                    this.setVolume(performer, target, 4);
                                }
                                else if (action == 734) {
                                    this.setVolume(performer, target, 3);
                                }
                                else if (action == 735) {
                                    this.setVolume(performer, target, 2);
                                }
                                else if (action == 736) {
                                    this.setVolume(performer, target, 1);
                                }
                                else if (action == 737) {
                                    this.setVolume(performer, target, 0);
                                }
                                else {
                                    if (action == 936) {
                                        return makeBait(act, performer, target, action, counter);
                                    }
                                    if (action == 937 && target.getTemplateId() == 479) {
                                        return makeFloat(act, performer, target, action, counter);
                                    }
                                }
                            }
                        }
                    }
                }
            }
            else if (action == 87) {
                toReturn = true;
                if (target.isFullprice()) {
                    comm.sendNormalServerMessage("Price is set to " + Economy.getEconomy().getChangeFor(target.getValue()).getChangeString() + ".");
                }
                else if (target.getPrice() <= 1) {
                    if (target.getValue() <= 1) {
                        comm.sendNormalServerMessage("A trader would deem this pretty worthless.");
                    }
                    else {
                        comm.sendNormalServerMessage("A trader would sell this for about " + Economy.getEconomy().getChangeFor(target.getValue() / 2).getChangeString() + ".");
                    }
                }
                else {
                    comm.sendNormalServerMessage("Price is set to " + Economy.getEconomy().getChangeFor(target.getPrice()).getChangeString() + ".");
                }
            }
        }
        if (action == 909) {
            final Creature[] creatures;
            final Creature[] storedanimal = creatures = Creatures.getInstance().getCreatures();
            for (final Creature cret : creatures) {
                if (cret.getWurmId() == target.getData()) {
                    final Brand brand = Creatures.getInstance().getBrand(target.getData());
                    if (brand != null) {
                        try {
                            final Village v = Villages.getVillage((int)brand.getBrandId());
                            performer.getCommunicator().sendNormalServerMessage("It has been branded by and belongs to the settlement of " + v.getName() + ".");
                        }
                        catch (NoSuchVillageException nsv) {
                            brand.deleteBrand();
                        }
                    }
                    if (cret.isCaredFor()) {
                        final long careTaker = cret.getCareTakerId();
                        final PlayerInfo info2 = PlayerInfoFactory.getPlayerInfoWithWurmId(careTaker);
                        if (info2 != null) {
                            performer.getCommunicator().sendNormalServerMessage("It is being taken care of by " + info2.getName() + ".");
                        }
                    }
                    performer.getCommunicator().sendNormalServerMessage(StringUtilities.raiseFirstLetter(cret.getStatus().getBodyType()));
                    if (cret.isDomestic() && System.currentTimeMillis() - cret.getLastGroomed() > 172800000L) {
                        performer.getCommunicator().sendNormalServerMessage("This creature could use some grooming.");
                    }
                    if (cret.hasTraits()) {
                        try {
                            final Skill breeding = performer.getSkills().getSkill(10085);
                            double knowl;
                            if (performer.getPower() > 0) {
                                knowl = 99.99;
                            }
                            else {
                                knowl = breeding.getKnowledge(0.0);
                            }
                            if (knowl > 20.0) {
                                final StringBuilder buf = new StringBuilder();
                                for (int x = 0; x < 64; ++x) {
                                    if (cret.hasTrait(x) && knowl - 20.0 > x) {
                                        final String l = Traits.getTraitString(x);
                                        if (l.length() > 0) {
                                            buf.append(l);
                                            buf.append(' ');
                                        }
                                    }
                                }
                                if (buf.toString().length() > 0) {
                                    performer.getCommunicator().sendNormalServerMessage(buf.toString());
                                }
                            }
                        }
                        catch (NoSuchSkillException ex6) {}
                    }
                }
            }
        }
        if (action == 185) {
            toReturn = true;
            if (performer.getPower() >= 0) {
                comm.sendNormalServerMessage("It is made from " + MaterialUtilities.getMaterialString(target.getMaterial()) + " (" + target.getMaterial() + ") " + ".");
                comm.sendNormalServerMessage("WurmId:" + target.getWurmId() + ", posx=" + target.getPosX() + "(" + ((int)target.getPosX() >> 2) + "), posy=" + target.getPosY() + "(" + ((int)target.getPosY() >> 2) + "), posz=" + target.getPosZ() + ", rot" + target.getRotation() + " layer=" + (target.isOnSurface() ? 0 : -1) + " bridgeid=" + target.getBridgeId());
                comm.sendNormalServerMessage("Ql:" + target.getQualityLevel() + ", damage=" + target.getDamage() + ", weight=" + target.getWeightGrams() + ", temp=" + target.getTemperature());
                comm.sendNormalServerMessage("parentid=" + target.getParentId() + " ownerid=" + target.getOwnerId() + " zoneid=" + target.getZoneId() + " sizex=" + target.getSizeX() + ", sizey=" + target.getSizeY() + " sizez=" + target.getSizeZ() + ".");
                final long timeSince = WurmCalendar.currentTime - target.getLastMaintained();
                final String timeString = Server.getTimeFor(timeSince * 1000L);
                comm.sendNormalServerMessage("Last maintained " + timeString + " ago.");
                String lastOwnerS = String.valueOf(target.lastOwner);
                final PlayerInfo p = PlayerInfoFactory.getPlayerInfoWithWurmId(target.getLastOwnerId());
                if (p != null) {
                    lastOwnerS = p.getName();
                }
                else {
                    try {
                        final Creature c3 = Creatures.getInstance().getCreature(target.lastOwner);
                        lastOwnerS = c3.getName();
                    }
                    catch (NoSuchCreatureException nsc) {
                        lastOwnerS = "dead " + lastOwnerS;
                    }
                }
                comm.sendNormalServerMessage("lastownerid=" + lastOwnerS + ", Model=" + target.getModelName());
                if (performer.getPower() >= 5) {
                    comm.sendNormalServerMessage("Zoneid=" + target.getZoneId() + " real zid=" + target.zoneId + " Counter=" + WurmId.getNumber(target.getWurmId()) + " origin=" + WurmId.getOrigin(target.getWurmId()) + " fl=" + target.getFloorLevel() + " your fl=" + performer.getFloorLevel() + " bridge=" + target.getBridgeId());
                    if (target.isVehicle()) {
                        final float diffposx = target.getPosX() - performer.getPosX();
                        final float diffposy = target.getPosY() - performer.getPosY();
                        comm.sendNormalServerMessage("Relative: offx=" + diffposx + ", offy=" + diffposy + ", offz=" + performer.getPositionZ() + " altOffZ=" + performer.getAltOffZ());
                    }
                }
                if (target.hasData()) {
                    comm.sendNormalServerMessage("data=" + target.getData() + ", data1=" + target.getData1() + " data2=" + target.getData2());
                }
                String creator = ", creator=" + target.creator;
                if (target.creator == null || target.creator.isEmpty()) {
                    creator = "";
                }
                comm.sendNormalServerMessage("auxdata=" + target.getAuxData() + creator);
                if (target.isKey()) {
                    comm.sendNormalServerMessage("lock id=" + target.getLockId());
                }
                if (target.isLock()) {
                    final long[] keys3 = target.getKeyIds();
                    comm.sendNormalServerMessage("Keys:");
                    for (final long lKey : keys3) {
                        comm.sendNormalServerMessage(String.valueOf(lKey));
                    }
                }
                if (target.getTemplateId() == 1311) {
                    comm.sendNormalServerMessage("Loader / Unloader WurmID = " + target.getLastOwnerId() + ", Name = " + lastOwnerS);
                }
            }
        }
        else if (action == 355) {
            if (target.isRoyal() && (ttid == 536 || ttid == 530 || ttid == 533)) {
                final KingdomStatusQuestion kq = new KingdomStatusQuestion(performer, "Kingdom status", "Kingdoms", performer.getWurmId());
                kq.sendQuestion();
            }
        }
        else if (action == 356) {
            if (target.isRoyal() && (ttid == 536 || ttid == 530 || ttid == 533)) {
                final KingdomHistory kq2 = new KingdomHistory(performer, "Kingdom history", "History of the kingdoms", performer.getWurmId());
                kq2.sendQuestion();
            }
        }
        else if (action == 358) {
            if (target.isRoyal() && (ttid == 536 || ttid == 530 || ttid == 533)) {
                final AbdicationQuestion kq3 = new AbdicationQuestion(performer, "Abdication", "Do you want to abdicate?", performer.getWurmId());
                kq3.sendQuestion();
            }
        }
        else if (action == 487) {
            if (ttid == 726 && (performer.getPower() == 0 || Servers.localServer.testServer) && (performer.getKingdomId() == target.getAuxData() || Servers.localServer.testServer)) {
                if (System.currentTimeMillis() - ((Player)performer).getSaveFile().lastChangedKindom > 7257600000L || Servers.localServer.testServer) {
                    final King m = King.getKing(target.getAuxData());
                    if (m != null && m.hasFailedAllChallenges()) {
                        if (!performer.hasVotedKing()) {
                            performer.setVotedKing(true);
                            comm.sendNormalServerMessage("You vote to remove " + m.getFullTitle() + ".");
                            if (m.getVotesNeeded() == 0) {
                                m.removeByVote();
                            }
                        }
                        else {
                            comm.sendNormalServerMessage("You have already voted to remove " + m.getFullTitle() + ".");
                        }
                    }
                }
                else {
                    comm.sendNormalServerMessage("You have have not been part of this kingdom long enough.");
                }
            }
        }
        else if (action == 488) {
            if (ttid == 726 && (performer.getKingdomId() == target.getAuxData() || Servers.localServer.testServer) && (performer.getPower() == 0 || Servers.localServer.testServer)) {
                if (System.currentTimeMillis() - ((Player)performer).getSaveFile().lastChangedKindom > 7257600000L || Servers.localServer.testServer) {
                    final King m = King.getKing(target.getAuxData());
                    if (m != null && m.mayBeChallenged()) {
                        final boolean alreadyChallenged = m.hasBeenChallenged();
                        m.addChallenge(performer);
                        comm.sendNormalServerMessage("You vote to challenge " + m.getFullTitle() + ".");
                        if (performer.getPower() > 0) {}
                        if (!alreadyChallenged && m.hasBeenChallenged()) {
                            try {
                                final Player ck = Players.getInstance().getPlayer(m.kingid);
                                MethodsCreatures.sendChallengeKingQuestion(ck);
                            }
                            catch (NoSuchPlayerException ex7) {}
                        }
                    }
                }
                else {
                    comm.sendNormalServerMessage("You have have not been part of this kingdom long enough.");
                }
            }
        }
        else if (action == 88) {
            toReturn = true;
            if (performer.getPower() >= 2) {
                Methods.sendSetDataQuestion(performer, target);
            }
            else {
                ItemBehaviour.logger.log(Level.WARNING, performer.getName() + " hacking the protocol by trying to set the data of " + target + ", counter: " + counter + '!');
            }
        }
        else if (action == 684) {
            toReturn = true;
            if (performer.getPower() >= 2) {
                Methods.sendItemRestrictionManagement(performer, target, target.getWurmId());
            }
            else {
                ItemBehaviour.logger.log(Level.WARNING, performer.getName() + " hacking the protocol by trying to set the restrictions of " + target + ", counter: " + counter + '!');
            }
        }
        else if (action == 608) {
            toReturn = true;
            if (performer.getDeity() != null && (performer.getDeity().getName().equals("Nahjo") || Servers.isThisATestServer()) && target.getTemplateId() == performer.getDeity().getHolyItem()) {
                final SwapDeityQuestion sdq = new SwapDeityQuestion(performer);
                sdq.sendQuestion();
            }
        }
        else if (action == 698 || action == 699 || action == 700) {
            toReturn = true;
            final LCMManagementQuestion question3 = new LCMManagementQuestion(performer, "Info", "Take an action", performer.getWurmId(), action);
            question3.sendQuestion();
        }
        else if (action == 939) {
            if (target.getTemplateId() == 94 || target.getTemplateId() == 152 || target.getTemplateId() == 780 || target.getTemplateId() == 95 || target.getTemplateId() == 150 || target.getTemplateId() == 96 || target.getTemplateId() == 151) {
                convertFishingEquipment(performer, target);
            }
            toReturn = true;
        }
        return toReturn;
    }
    
    @Override
    public boolean action(final Action act, final Creature performer, final Item[] targets, final short action, final float counter) {
        final boolean toReturn = true;
        if (action == 7 || action == 638) {
            final boolean dropOnGround = action == 7;
            String pre = "";
            String post = "";
            String broadcastPost = "";
            final NameCountList dropping = new NameCountList();
            for (int x = 0; x < targets.length; ++x) {
                if (targets[x].isSurfaceOnly() && !performer.isOnSurface()) {
                    performer.getCommunicator().sendNormalServerMessage(targets[x].getName() + " can only be dropped on the surface.");
                }
                else if ((!targets[x].isNoDrop() || performer.getPower() > 0) && !targets[x].isComponentItem()) {
                    final String[] msg = MethodsItems.drop(performer, targets[x], dropOnGround);
                    if (msg.length > 0) {
                        if (pre.isEmpty()) {
                            pre = msg[0];
                        }
                        if (post.isEmpty()) {
                            post = msg[2];
                        }
                        if (broadcastPost.isEmpty()) {
                            broadcastPost = msg[3];
                        }
                        dropping.add(targets[x].getName());
                    }
                }
            }
            final String line = dropping.toString();
            if (!line.isEmpty()) {
                performer.getCommunicator().sendNormalServerMessage(pre + line + post);
                Server.getInstance().broadCastAction(performer.getName() + " drops " + line + post, performer, 7);
            }
        }
        else if (action == 59) {
            final TextInputQuestion tiq = new TextInputQuestion(performer, "Setting description for multiple items", "Set the new descriptions:", targets);
            tiq.sendQuestion();
        }
        else if (action == 86) {
            final SinglePriceManageQuestion spm = new SinglePriceManageQuestion(performer, "Price management for multiple items", "Set the desired price:", targets);
            spm.sendQuestion();
        }
        return true;
    }
    
    @Override
    public boolean action(final Action act, final Creature performer, final Item source, final Item target, final short action, final float counter) {
        if (target.canBePlanted()) {
            if (action == 177 || action == 176 || action == 178) {
                return this.action(act, performer, target, action, counter);
            }
            if (action == 6 || action == 100) {
                return this.action(act, performer, target, action, counter);
            }
            if (action == 1) {
                return examine(act, performer, target, action, counter);
            }
            if (Features.Feature.SINGLE_PLAYER_BRIDGES.isEnabled() && action == 637 && source.getTemplateId() == 903 && target.getTemplateId() == 901) {
                return MethodsSurveying.planBridge(act, performer, source, null, target, action, counter);
            }
            if (Features.Feature.SINGLE_PLAYER_BRIDGES.isEnabled() && action == 640 && source.getTemplateId() == 903 && target.getTemplateId() == 901) {
                return MethodsSurveying.survey(act, performer, source, null, target, action, counter);
            }
            if (action == 640 && (source.getTemplateId() == 805 || source.getTemplateId() == 1009)) {
                if (MethodsItems.surveyAbilitySigns(source, performer)) {
                    performer.getCommunicator().sendNormalServerMessage("You may activate this item here.");
                }
                else {
                    performer.getCommunicator().sendNormalServerMessage("You cannot activate this item here.");
                }
                return true;
            }
        }
        boolean done = true;
        final int ttid = target.getTemplateId();
        final int stid = source.getTemplateId();
        if (!target.isTraded()) {
            if (action > 10000) {
                final int itemToCreate = action - 10000;
                boolean actionResult = false;
                try {
                    if ((target.isNoTake(performer) || target.isUseOnGroundOnly()) && target.getWurmId() != performer.getVehicle() && !performer.isWithinDistanceTo(target.getPosX(), target.getPosY(), target.getPosZ(), 4.0f)) {
                        performer.getCommunicator().sendNormalServerMessage("You are too far away to do that.");
                        return done;
                    }
                    final CreationEntry creation = CreationMatrix.getInstance().getCreationEntry(stid, ttid, itemToCreate);
                    if (source.getWurmId() == target.getWurmId()) {
                        performer.getCommunicator().sendNormalServerMessage("The same item can not be used twice during creation.");
                        return true;
                    }
                    done = false;
                    final Item created = creation.run(performer, source, target.getWurmId(), counter);
                    actionResult = true;
                    if (created.getTemplateId() == 1293 && created.getMaterial() == 56) {
                        final int c = WurmColor.createColor(1 + Server.rand.nextInt(255), 1 + Server.rand.nextInt(255), 1 + Server.rand.nextInt(255));
                        created.setColor(c);
                    }
                    if (created.isLock()) {
                        try {
                            if (created.getTemplateId() != 167) {
                                final Item key = ItemFactory.createItem(168, created.getQualityLevel(), performer.getName());
                                key.setMaterial(created.getMaterial());
                                performer.getInventory().insertItem(key, true);
                                created.addKey(key.getWurmId());
                                key.setLockId(created.getWurmId());
                            }
                        }
                        catch (NoSuchTemplateException nst) {
                            ItemBehaviour.logger.log(Level.WARNING, performer.getName() + " failed to create key: " + nst.getMessage(), nst);
                        }
                        catch (FailedException fe) {
                            ItemBehaviour.logger.log(Level.WARNING, performer.getName() + " failed to create key: " + fe.getMessage(), fe);
                        }
                    }
                    if ((created.isDrinkable() || created.isFood()) && performer.isRoyalChef()) {
                        created.setQualityLevel(Math.min(99.0f, created.getQualityLevel() + 10.0f));
                    }
                    if (!created.isLiquid() && created.getTemplateId() != 1269) {
                        if (!creation.isCreateOnGround() && performer.getInventory().insertItem(created)) {
                            performer.getCommunicator().sendNormalServerMessage("You create " + created.getNameWithGenus() + ".");
                            Server.getInstance().broadCastAction(performer.getName() + " creates " + created.getNameWithGenus() + ".", performer, 5);
                            performer.getCommunicator().sendActionResult(true);
                        }
                        else {
                            created.setLastOwnerId(performer.getWurmId());
                            try {
                                float posX = performer.getStatus().getPositionX();
                                float posY = performer.getStatus().getPositionY();
                                final float rot = performer.getStatus().getRotation();
                                final float xPosMod = (float)Math.sin(rot * 0.017453292f) * 2.0f;
                                final float yPosMod = -(float)Math.cos(rot * 0.017453292f) * 2.0f;
                                posX += xPosMod;
                                posY += yPosMod;
                                final int placedX = (int)posX >> 2;
                                final int placedY = (int)posY >> 2;
                                int placedTile;
                                if (performer.isOnSurface()) {
                                    placedTile = Server.surfaceMesh.getTile(placedX, placedY);
                                }
                                else {
                                    placedTile = Server.caveMesh.getTile(placedX, placedY);
                                }
                                if (Tiles.decodeHeight(placedTile) < 0) {
                                    if (created.getTemplateId() == 37) {
                                        performer.getCommunicator().sendNormalServerMessage("The fire fizzles in the water and goes out.");
                                        Items.decay(created.getWurmId(), created.getDbStrings());
                                    }
                                    else {
                                        created.putItemInfrontof(performer);
                                        performer.getCommunicator().sendNormalServerMessage("You create " + created.getNameWithGenus() + " in front of you on the ground.");
                                        Server.getInstance().broadCastAction(performer.getName() + " creates " + created.getNameWithGenus() + ".", performer, Math.min(Math.max(3, created.getSizeZ() / 10), 10));
                                    }
                                }
                                else {
                                    created.putItemInfrontof(performer);
                                    performer.getCommunicator().sendNormalServerMessage("You create " + created.getNameWithGenus() + " in front of you on the ground.");
                                    Server.getInstance().broadCastAction(performer.getName() + " creates " + created.getNameWithGenus() + ".", performer, Math.min(Math.max(3, created.getSizeZ() / 10), 10));
                                    if (created.getTemplateId() == 37 && !created.deleted) {
                                        created.setTemperature((short)10000);
                                        final Effect effect = EffectFactory.getInstance().createFire(created.getWurmId(), created.getPosX(), created.getPosY(), created.getPosZ(), performer.isOnSurface());
                                        created.addEffect(effect);
                                    }
                                }
                            }
                            catch (NoSuchZoneException nsz) {
                                ItemBehaviour.logger.log(Level.WARNING, performer.getName() + ": " + nsz.getMessage(), nsz);
                            }
                            catch (NoSuchPlayerException nsp) {
                                ItemBehaviour.logger.log(Level.INFO, nsp.getMessage(), nsp);
                            }
                            catch (NoSuchCreatureException nsc) {
                                ItemBehaviour.logger.log(Level.INFO, nsc.getMessage(), nsc);
                            }
                        }
                    }
                    done = true;
                }
                catch (NoSuchEntryException nse) {
                    ItemBehaviour.logger.log(Level.WARNING, performer.getName() + ":" + nse.getMessage(), nse);
                    done = true;
                }
                catch (NoSuchSkillException nss) {
                    ItemBehaviour.logger.log(Level.WARNING, performer.getName() + ":" + nss.getMessage(), nss);
                    done = true;
                }
                catch (NoSuchItemException nsi2) {
                    done = true;
                }
                catch (FailedException ex4) {}
                if (done) {
                    performer.getCommunicator().sendActionResult(actionResult);
                }
            }
            else if (Recipes.isRecipeAction(action)) {
                final Recipe recipe = Recipes.getRecipeByActionId(action);
                if (recipe == null) {
                    performer.getCommunicator().sendNormalServerMessage("Recipe" + ((performer.getPower() > 1) ? (" " + action) : "") + " not found, most odd!");
                    return true;
                }
                done = handleRecipe(act, performer, source, target, action, counter, recipe);
            }
            else if (action == 17 && target.getTemplateId() == 1271) {
                this.readVillageMessages(performer, target);
            }
            else if (action == 118) {
                if (target.getTemplateId() == 1271 && (source.getTemplateId() == 748 || source.getTemplateId() == 1272)) {
                    this.postVillageMessage(performer, source, target);
                }
                else if (ttid == 700) {
                    done = true;
                    if (((source.isLight() || source.isFire()) && source.isOnFire()) || stid == 315 || stid == 176 || stid == 143) {
                        if (target.getOwnerId() == -10L) {
                            SoundPlayer.playSound("sound.object.fzz", target, 1.0f);
                            final short num = (short)(5 + Server.rand.nextInt(5));
                            Players.getInstance().sendEffect(num, target.getPosX(), target.getPosY(), target.getPosZ() + 10.0f + Server.rand.nextInt(30), performer.isOnSurface(), 500.0f);
                            target.setQualityLevel(target.getQualityLevel() - 1.0f);
                            if (target.getQualityLevel() > 0.0f) {
                                performer.getCommunicator().sendNormalServerMessage("The fireworks now has " + Math.ceil(target.getQualityLevel()) + " charges left.");
                            }
                            Server.getInstance().broadCastAction(performer.getName() + " fires off some fireworks!", performer, 5);
                        }
                        else {
                            performer.getCommunicator().sendNormalServerMessage("You need to light the fireworks on the ground.");
                        }
                    }
                    else {
                        performer.getCommunicator().sendNormalServerMessage("You can't light fireworks with that.");
                    }
                }
                else {
                    done = this.action(act, performer, target, action, counter);
                }
            }
            else if (action == 942) {
                if (ttid == 1394 && (source.isWeaponKnife() || source.getTemplateId() == 258)) {
                    done = openClam(act, performer, source, target, action, counter);
                }
            }
            else if (action == 945) {
                if (source.getTemplate().isRune()) {
                    done = useRuneOnItem(act, performer, source, target, action, counter);
                }
            }
            else if (action == 162) {
                done = this.action(act, performer, target, action, counter);
            }
            else if (action == 671 || action == 672) {
                if (source.getTemplateId() == 319 || source.getTemplateId() == 1029) {
                    done = CargoTransportationMethods.haul(performer, target, counter, action, act);
                }
            }
            else {
                if (action == 581 && stid == 176 && Servers.isThisATestServer()) {
                    decay(target, performer);
                    return true;
                }
                if (action == 83) {
                    if (target.getTemplateId() == 1112 && Items.isWaystoneInUse(target.getWurmId())) {
                        performer.getCommunicator().sendNormalServerMessage("That waystone is the anchor point for a container or a delivery point, thus may not be bashed.");
                        done = true;
                    }
                    else if (target.isRoadMarker() && MethodsHighways.isNextToACamp(MethodsHighways.getHighwayPos(target))) {
                        performer.getCommunicator().sendNormalServerMessage("That " + target.getName() + " is in a wagoners camp, who does not allow such actions.");
                        done = true;
                    }
                    else if (target.isRoadMarker() && Wagoner.isOnActiveDeliveryRoute(target)) {
                        performer.getCommunicator().sendNormalServerMessage("That " + target.getName() + " is on a wagoners route, you will have to wait till they have done that delivery.");
                        done = true;
                    }
                    else if (target.getTemplateId() == 1311 && !target.isEmpty(true)) {
                        performer.getCommunicator().sendNormalServerMessage("You cannot destroy this cage with a creature inside.");
                        done = true;
                    }
                    else {
                        done = (!performer.mayDestroy(target) || target.isIndestructible() || MethodsItems.destroyItem(action, performer, source, target, false, counter));
                    }
                }
                else if (action == 185) {
                    if (source.getTemplateId() == 176 && performer.getPower() > 0 && source.getAuxData() == 1 && target.isVehicle()) {
                        final Vehicle v = Vehicles.getVehicle(target);
                        final String wPos = StringUtil.format("Vehicle TileX: %d TileY: %d FloorLevel: %d", target.getTileX(), target.getTileY(), target.getFloorLevel());
                        performer.getCommunicator().sendNormalServerMessage(wPos);
                        if (v.getDraggers() != null) {
                            for (final Creature c2 : v.getDraggers()) {
                                final String s = StringUtil.format("Dragger TileX: %d TileY: %d, id: %d FloorLevel: %d, PosZ: %d, StatusPosZ: %.2f", c2.getTileX(), c2.getTileY(), c2.getWurmId(), c2.getFloorLevel(), c2.getPosZDirts(), c2.getStatus().getPositionZ());
                                performer.getCommunicator().sendNormalServerMessage(s);
                            }
                        }
                        final Seat[] seats = v.getSeats();
                        if (seats != null) {
                            for (int i = 0; i < seats.length; ++i) {
                                final Player p = Players.getInstance().getPlayerOrNull(seats[i].getOccupant());
                                if (p != null) {
                                    final String s2 = StringUtil.format("Name: %s, TileX: %d TileY: %d, ZPos: %.2f, FloorLevel: %d", p.getName(), p.getTileX(), p.getTileX(), p.getStatus().getPositionZ(), p.getFloorLevel(true));
                                    performer.getCommunicator().sendNormalServerMessage(s2);
                                }
                            }
                        }
                        return true;
                    }
                    return this.action(act, performer, target, action, counter);
                }
                else if (action == 568) {
                    if (target.isLockable()) {
                        if (target.getLockId() == -10L || (target.isDraggable() && MethodsItems.mayUseInventoryOfVehicle(performer, target))) {
                            performer.getCommunicator().sendOpenInventoryContainer(target.getWurmId());
                        }
                        else {
                            try {
                                final Item lock = Items.getItem(target.getLockId());
                                if (!lock.getLocked() || target.isOwner(performer)) {
                                    performer.getCommunicator().sendOpenInventoryContainer(target.getWurmId());
                                    return true;
                                }
                                final long[] keys = lock.getKeyIds();
                                for (int j = 0; j < keys.length; ++j) {
                                    final Item key2 = Items.getItem(keys[j]);
                                    if (key2.getTopParent() == performer.getInventory().getWurmId()) {
                                        performer.getCommunicator().sendOpenInventoryContainer(target.getWurmId());
                                        return true;
                                    }
                                }
                                performer.getCommunicator().sendSafeServerMessage("The " + target.getName() + " is locked. Please use the key to unlock and open it.");
                            }
                            catch (NoSuchItemException nsi) {
                                performer.getCommunicator().sendSafeServerMessage("The " + target.getName() + " is locked. Please use the key to unlock and open it.");
                            }
                        }
                    }
                    else if (target.isHollow() && !target.isSealedByPlayer() && target.getTemplateId() != 1342) {
                        performer.getCommunicator().sendOpenInventoryContainer(target.getWurmId());
                    }
                    done = true;
                }
                else if (action == 192) {
                    if (target.isNoImprove()) {
                        performer.getCommunicator().sendNormalServerMessage("That item cannot be improved");
                        return true;
                    }
                    if (target.getOwnerId() == -10L && !Methods.isActionAllowed(performer, action, target)) {
                        return true;
                    }
                    if (target.creationState != 0 || source.creationState != 0) {
                        done = true;
                        final boolean targetWater = target.getTemplateId() == 128;
                        final Item toImprove = targetWater ? source : target;
                        final Item improveItem = targetWater ? target : source;
                        final byte material = MethodsItems.getImproveMaterial(toImprove);
                        int tid = MethodsItems.getItemForImprovement(material, toImprove.creationState);
                        if (tid == improveItem.getTemplateId()) {
                            if (tid == 128 && Materials.isMetal(material)) {
                                done = MethodsItems.temper(act, performer, toImprove, improveItem, counter);
                            }
                            else {
                                done = MethodsItems.polishItem(act, performer, improveItem, toImprove, counter);
                            }
                        }
                        else if (source.creationState != 0) {
                            tid = MethodsItems.getItemForImprovement(material, improveItem.creationState);
                            if (tid == toImprove.getTemplateId()) {
                                if (tid == 128 && Materials.isMetal(improveItem.getMaterial())) {
                                    done = MethodsItems.temper(act, performer, improveItem, toImprove, counter);
                                }
                                else {
                                    done = MethodsItems.polishItem(act, performer, toImprove, improveItem, counter);
                                }
                            }
                        }
                    }
                    else {
                        done = MethodsItems.improveItem(act, performer, source, target, counter);
                    }
                }
                else if (action == 228) {
                    if (source.getTemplateId() == 386) {
                        done = true;
                        final byte material2 = MethodsItems.getImproveMaterial(source);
                        final int tid2 = MethodsItems.getItemForImprovement(material2, source.creationState);
                        if (tid2 == target.getTemplateId()) {
                            if (tid2 == 128 && Materials.isMetal(material2)) {
                                done = MethodsItems.temper(act, performer, source, target, counter);
                            }
                            else {
                                done = MethodsItems.polishItem(act, performer, target, source, counter);
                            }
                        }
                    }
                }
                else if (action == 93 && source.getRarity() == target.getRarity()) {
                    done = true;
                    if ((stid == 1193 || stid == 417) && ttid == stid && source.getRealTemplateId() != target.getRealTemplateId()) {
                        int freeVol = source.getWeightGrams();
                        final Item p2 = source.getParentOrNull();
                        if (source.getParentId() != target.getParentId()) {
                            if (p2 == null) {
                                return true;
                            }
                            freeVol = Math.min(freeVol, p2.getFreeVolume());
                        }
                        if (freeVol > 0) {
                            final float newQl = (target.getCurrentQualityLevel() * target.getWeightGrams() + source.getCurrentQualityLevel() * freeVol) / (target.getWeightGrams() + freeVol);
                            final float caloriesTotal = target.getCaloriesByWeight() + source.getCaloriesByWeight(freeVol);
                            final float carbsTotal = target.getCarbsByWeight() + source.getCarbsByWeight(freeVol);
                            final float fatsTotal = target.getFatsByWeight() + source.getFatsByWeight(freeVol);
                            final float proteinsTotal = target.getProteinsByWeight() + source.getProteinsByWeight(freeVol);
                            final int weight = target.getWeightGrams() + freeVol;
                            final short calories = (short)(caloriesTotal * 1000.0f / weight);
                            final short carbs = (short)(carbsTotal * 1000.0f / weight);
                            final short fats = (short)(fatsTotal * 1000.0f / weight);
                            final short proteins = (short)(proteinsTotal * 1000.0f / weight);
                            final int ibonus = (target.getBonus() + source.getBonus()) / 2;
                            final byte bonus = (byte)(ibonus % SkillSystem.getNumberOfSkillTemplates());
                            final byte stages = target.getFoodStages();
                            final byte ingredients = target.getFoodIngredients();
                            target.setRealTemplate(-10);
                            target.setWeight(target.getWeightGrams() + freeVol, true);
                            target.setQualityLevel(newQl);
                            target.setOriginalQualityLevel(newQl);
                            target.setDamage(0.0f);
                            ItemMealData.update(target.getWurmId(), (short)(-1), calories, carbs, fats, proteins, bonus, stages, ingredients);
                            target.setName(target.getTemplate().getName(), true);
                            source.setWeight(source.getWeightGrams() - freeVol, true);
                        }
                        else {
                            performer.getCommunicator().sendNormalServerMessage("No room in " + p2.getName() + " to combine those.");
                        }
                    }
                    else {
                        try {
                            performer.getMovementScheme().stopSendingSpeedModifier();
                            if (performer.isPlayer()) {
                                ((Player)performer).justCombined = true;
                            }
                            if (source.combine(target, performer)) {
                                performer.getCommunicator().sendNormalServerMessage("You combine the items into one.");
                            }
                        }
                        catch (FailedException fe2) {
                            performer.getCommunicator().sendNormalServerMessage(fe2.getMessage());
                        }
                    }
                }
                else if (action == 283) {
                    done = true;
                    if (source.isHealing() && target.isHealing()) {
                        if (source.getTemplateId() != target.getTemplateId()) {
                            return MethodsItems.createSalve(performer, source, target, act, counter);
                        }
                        performer.getCommunicator().sendNormalServerMessage("Nothing would happen if you 'mixed' these.");
                    }
                    else if (source.isColorComponent() && target.isColor()) {
                        done = MethodsItems.improveColor(performer, source, target, act);
                    }
                    else if (target.isColorComponent() && source.isColor()) {
                        done = MethodsItems.improveColor(performer, target, source, act);
                    }
                    else if (source.getTemplateId() == 764 && target.getTemplateId() == 866) {
                        done = MethodsItems.createOil(performer, source, target, act, counter);
                    }
                    else if (source.getTemplateId() == 866 && target.getTemplateId() == 764) {
                        done = MethodsItems.createOil(performer, target, source, act, counter);
                    }
                    else if (Features.Feature.TRANSFORM_RESOURCE_TILES.isEnabled() && ttid == 1020 && source.isLiquid()) {
                        final byte potionAuxData = getTileTransmutationLiquidAuxData(source, target);
                        if (potionAuxData == 0) {
                            performer.getCommunicator().sendNormalServerMessage("You cannot mix these.");
                        }
                        else {
                            done = MethodsItems.createPotion(performer, source, target, act, counter, potionAuxData);
                        }
                    }
                    else {
                        performer.getCommunicator().sendNormalServerMessage("You cannot mix these.");
                    }
                }
                else if (action == 633) {
                    done = true;
                    if (source.isSmearable()) {
                        done = MethodsItems.smear(performer, source, target, act, counter);
                    }
                }
                else if (action == 285) {
                    done = true;
                    if (source.isHealing() && target.isHealing()) {
                        if (source.getTemplateId() != target.getTemplateId()) {
                            try {
                                final Skill alch = performer.getSkills().getSkill(10042);
                                final int knowl = (int)alch.getKnowledge(0.0);
                                final int pow = source.getAlchemyType() * target.getAlchemyType();
                                if (knowl < 10) {
                                    performer.getCommunicator().sendNormalServerMessage("You have no clue what would happen if you mixed these.");
                                }
                                else if (knowl < 30) {
                                    if (pow < 10) {
                                        performer.getCommunicator().sendNormalServerMessage("You would probably create a fairly weak healing cover.");
                                    }
                                    else {
                                        performer.getCommunicator().sendNormalServerMessage("You would probably create a fairly strong healing cover.");
                                    }
                                }
                                else if (pow < 10) {
                                    performer.getCommunicator().sendNormalServerMessage("You would probably create a fairly weak healing cover.");
                                }
                                else if (pow < 20) {
                                    performer.getCommunicator().sendNormalServerMessage("You would probably create a fairly strong healing cover.");
                                }
                                else if (pow < 25) {
                                    performer.getCommunicator().sendNormalServerMessage("You would probably create a very strong healing cover.");
                                }
                                else {
                                    performer.getCommunicator().sendNormalServerMessage("You would probably create an extremely strong healing cover.");
                                }
                            }
                            catch (NoSuchSkillException nss2) {
                                performer.getCommunicator().sendNormalServerMessage("You have no clue what would happen if you mixed these.");
                            }
                        }
                        else {
                            performer.getCommunicator().sendNormalServerMessage("Nothing would happen if you 'mixed' these.");
                        }
                    }
                    else if (source.getTemplateId() == 764 || target.getTemplateId() == 764) {
                        if (source.getTemplateId() != 866) {
                            if (target.getTemplateId() != 866) {
                                performer.getCommunicator().sendNormalServerMessage("You cannot mix these.");
                                return done;
                            }
                        }
                        try {
                            final Skill alch = performer.getSkills().getSkill(10042);
                            final int knowl = (int)alch.getKnowledge(0.0);
                            if (knowl < 10) {
                                performer.getCommunicator().sendNormalServerMessage("You have no clue what would happen if you mixed these.");
                            }
                            else if (knowl < 30) {
                                performer.getCommunicator().sendNormalServerMessage("You would create some sort of potion.");
                            }
                            else {
                                final Item blood = (target.getTemplateId() == 866) ? target : source;
                                try {
                                    final ItemTemplate template = ItemTemplateFactory.getInstance().getTemplate(blood.getPotionTemplateIdForBlood());
                                    performer.getCommunicator().sendNormalServerMessage("You would create " + template.getNameWithGenus() + ".");
                                }
                                catch (NoSuchTemplateException nsi3) {
                                    performer.getCommunicator().sendNormalServerMessage("You have no clue what would happen if you mixed these.");
                                }
                            }
                        }
                        catch (NoSuchSkillException nss2) {
                            performer.getCommunicator().sendNormalServerMessage("You have no clue what would happen if you mixed these.");
                        }
                    }
                    else if (Features.Feature.TRANSFORM_RESOURCE_TILES.isEnabled() && ttid == 1020 && source.isLiquid()) {
                        final byte potionAuxData = getTileTransmutationLiquidAuxData(source, target);
                        switch (potionAuxData) {
                            case 1: {
                                performer.getCommunicator().sendNormalServerMessage("That would make a liquid that could transform a sand tile into a clay tile, but will require blessing to activate it.");
                                break;
                            }
                            case 2: {
                                performer.getCommunicator().sendNormalServerMessage("That would make a liquid that could transform a grass or mycelium tile into a peat tile, but will require blessing to activate it.");
                                break;
                            }
                            case 3: {
                                performer.getCommunicator().sendNormalServerMessage("That would make a liquid that could transform a steppe tile into a tar tile, but will require blessing to activate it.");
                                break;
                            }
                            case 4: {
                                performer.getCommunicator().sendNormalServerMessage("That would make a liquid that could transform a clay tile into a dirt tile, but will require blessing to activate it.");
                                break;
                            }
                            case 5: {
                                performer.getCommunicator().sendNormalServerMessage("That would make a liquid that could transform a peat tile into a dirt tile, but will require blessing to activate it.");
                                break;
                            }
                            case 6: {
                                performer.getCommunicator().sendNormalServerMessage("That would make a liquid that could transform a tar tile into a dirt tile, but will require blessing to activate it.");
                                break;
                            }
                            case 7: {
                                performer.getCommunicator().sendNormalServerMessage("That would make a liquid that could transform a moss tile into a tundra tile, but will require blessing to activate it.");
                                break;
                            }
                            case 8: {
                                performer.getCommunicator().sendNormalServerMessage("That would make a liquid that could transform a tundra tile into a dirt tile, but will require blessing to activate it.");
                                break;
                            }
                            default: {
                                performer.getCommunicator().sendNormalServerMessage("You cannot mix these.");
                                break;
                            }
                        }
                    }
                    else if (!target.isSealedByPlayer() && (target.isFoodMaker() || target.getTemplate().isCooker())) {
                        this.showRecipeInfo(performer, source, target);
                    }
                    else {
                        performer.getCommunicator().sendNormalServerMessage("You cannot mix these.");
                    }
                }
                else {
                    if (action == 12) {
                        if (target.getTemplateId() == 1396 && target.isPlanted() && target.getAuxData() > 0) {
                            lightItem(target, source, performer);
                        }
                        else if (target.getTemplateId() != 729 || target.getAuxData() > 0) {
                            lightItem(target, source, performer);
                        }
                        return true;
                    }
                    if (action == 134) {
                        if (source.isWeaponBow() && ttid == 458) {
                            done = Archery.attack(performer, target, source, counter, act);
                        }
                    }
                    else if (target.isLockable() && (action == 161 || action == 78)) {
                        done = true;
                        if (source.isLock()) {
                            if (source.isLocked()) {
                                performer.getCommunicator().sendNormalServerMessage("The " + source.getName() + " is already in use.");
                                return true;
                            }
                            if (target.getLastOwnerId() == performer.getWurmId() || target.getOwnerId() == performer.getWurmId()) {
                                done = MethodsItems.lock(performer, source, target, counter, action == 78);
                            }
                            else {
                                performer.getCommunicator().sendNormalServerMessage("The owner will have to lock that.");
                            }
                        }
                    }
                    else if (target.isLockable() && action == 102) {
                        done = true;
                        if (target.getLastOwnerId() == performer.getWurmId() || target.getOwnerId() == performer.getWurmId()) {
                            done = MethodsItems.unlock(performer, source, target, counter);
                        }
                        else {
                            performer.getCommunicator().sendNormalServerMessage("Only the owner can unlock that.");
                        }
                    }
                    else if (source.isWand() && performer.getPower() >= 2 && target.isLock() && action == 28 && Servers.isThisATestServer()) {
                        target.setLocked(true);
                        performer.getCommunicator().sendNormalServerMessage("You set that lock to its locked (bugged) state.");
                    }
                    else if (source.isWand() && performer.getPower() >= 2 && target.isLock() && action == 102) {
                        target.setLocked(false);
                        performer.getCommunicator().sendNormalServerMessage("You set that lock to its unlocked state.");
                    }
                    else if (target.isLockable() && action == 101 && !target.isNotLockpickable()) {
                        final VolaTile vt = Zones.getTileOrNull(target.getTileX(), target.getTileY(), target.isOnSurface());
                        final Village vill = (vt == null) ? null : vt.getVillage();
                        boolean ok = false;
                        if (target.getLastOwnerId() == performer.getWurmId()) {
                            ok = true;
                        }
                        else if (target.isInPvPZone() && Methods.isActionAllowed(performer, action, true, target.getTileX(), target.getTileY(), 0, 0)) {
                            ok = true;
                        }
                        else if (!target.isInPvPZone() && !target.isVehicle() && vill != null && vill.isActionAllowed(action, performer)) {
                            ok = true;
                        }
                        if (ok) {
                            done = MethodsItems.picklock(performer, source, target, counter, act);
                        }
                        else {
                            done = true;
                            performer.getCommunicator().sendNormalServerMessage("Stealing " + target.getTemplate().getPlural() + " is punished with death penalty here. You decide not to.");
                        }
                    }
                    else if (action == 182) {
                        if (target.isWrapped()) {
                            performer.getCommunicator().sendNormalServerMessage("You need to unwrap it first before eating it.");
                            return true;
                        }
                        if (!target.isNoEatOrDrink()) {
                            done = false;
                            if (act.justTickedSecond()) {
                                done = MethodsItems.eat(act, performer, target, counter);
                            }
                        }
                    }
                    else if (action == 600) {
                        if (target.isInstaDiscard()) {
                            done = false;
                            if (act.justTickedSecond()) {
                                done = Methods.discardSellItem(performer, act, target, counter);
                            }
                        }
                    }
                    else if (action == 325) {
                        done = MethodsItems.askSleep(act, performer, target, counter);
                    }
                    else if (action == 140) {
                        done = MethodsItems.sleep(act, performer, target, counter);
                    }
                    else if (action == 365 || action == 366 || action == 367 || action == 320 || action == 319 || action == 322 || action == 321 || action == 323) {
                        done = MethodsItems.setRent(act, performer, target);
                    }
                    else if (action == 324) {
                        done = MethodsItems.rent(act, performer, target);
                    }
                    else if (action == 183) {
                        if (ttid == 1101) {
                            return MethodsItems.drinkChampagne(performer, target);
                        }
                        if (target.isContainerLiquid() && target.getItemCount() == 1) {
                            for (final Item k : target.getItems()) {
                                if (!k.isNoEatOrDrink() && !k.isUndistilled() && k.isDrinkable()) {
                                    done = false;
                                    if (act.justTickedSecond()) {
                                        done = MethodsItems.drink(act, performer, k, counter);
                                        break;
                                    }
                                    break;
                                }
                            }
                        }
                        else if (!target.isFood() && !target.isNoEatOrDrink() && !target.isUndistilled() && target.isDrinkable()) {
                            done = false;
                            if (act.justTickedSecond()) {
                                done = MethodsItems.drink(act, performer, target, counter);
                            }
                        }
                    }
                    else if (action == 225) {
                        if ((ttid == 729 || ttid == 203) && (source.isWeaponKnife() || stid == 258)) {
                            if (performer.getWurmId() == target.getOwnerId()) {
                                final int toMake = (ttid == 203) ? 1170 : 730;
                                try {
                                    final ItemTemplate it = ItemTemplateFactory.getInstance().getTemplate(toMake);
                                    int slices = target.getWeightGrams() / it.getWeightGrams();
                                    if (slices * it.getWeightGrams() < target.getWeightGrams()) {
                                        ++slices;
                                    }
                                    performer.getCommunicator().sendNormalServerMessage("You cut the " + target.getName() + " into " + slices + " elegant slices.");
                                    Server.getInstance().broadCastAction(performer.getName() + " cuts the " + target.getName() + " into " + slices + " elegant slices.", performer, 5);
                                    if (performer.getInventory().getNumItemsNotCoins() >= 100 - slices) {
                                        performer.getCommunicator().sendNormalServerMessage("You don't have space for the slices.");
                                        return true;
                                    }
                                    int slicesWeight = 0;
                                    for (int x = 0; x < slices; ++x) {
                                        try {
                                            final Item slice = ItemFactory.createItem(toMake, target.getCurrentQualityLevel(), performer.getName());
                                            slice.setName("slice of " + target.getName());
                                            slice.setMaterial(target.getMaterial());
                                            slice.setRealTemplate(target.getRealTemplateId());
                                            if (slicesWeight + it.getWeightGrams() > target.getWeightGrams()) {
                                                slice.setWeight(target.getWeightGrams() - slicesWeight, true);
                                            }
                                            slicesWeight += slice.getWeightGrams();
                                            performer.getInventory().insertItem(slice, true);
                                        }
                                        catch (NoSuchTemplateException nst2) {
                                            ItemBehaviour.logger.log(Level.WARNING, nst2.getMessage());
                                        }
                                        catch (FailedException fe3) {
                                            ItemBehaviour.logger.log(Level.WARNING, fe3.getMessage());
                                        }
                                    }
                                    Items.destroyItem(target.getWurmId());
                                }
                                catch (NoSuchTemplateException nste) {
                                    ItemBehaviour.logger.log(Level.WARNING, nste.getMessage(), nste);
                                    performer.getCommunicator().sendNormalServerMessage("Slice type does not exist, please report this bug.");
                                    return true;
                                }
                            }
                        }
                        else if (source.isWeaponKnife()) {
                            if (ttid == 92 || ttid == 129) {
                                done = MethodsItems.filet(act, performer, source, target, counter);
                            }
                            else if (target.getTemplate().getFoodGroup() == 1201 && ttid != 369) {
                                done = MethodsItems.filetFish(act, performer, source, target, counter);
                            }
                        }
                        else if (!target.isBulk() && target.isFood() && !target.isDrinkable() && !target.isMeat() && !target.isFish() && target.getTemplateId() != 729) {
                            if (target.getWeightGrams() < 2000) {
                                performer.getCommunicator().sendNormalServerMessage(target.getNameWithGenus() + " is too small.");
                                return true;
                            }
                            if (target.getName().contains("portion of ")) {
                                performer.getCommunicator().sendNormalServerMessage("It makes no sense to remove a portion of " + target.getNameWithGenus() + ".");
                                return true;
                            }
                            if (target.getName().contains("slice of ")) {
                                performer.getCommunicator().sendNormalServerMessage("It makes no sense to remove a slice of " + target.getNameWithGenus() + ".");
                                return true;
                            }
                            performer.getCommunicator().sendNormalServerMessage("You get a portion of " + target.getNameWithGenus() + ".");
                            Server.getInstance().broadCastAction(performer.getName() + " gets a portion of " + target.getNameWithGenus() + ".", performer, 5);
                            if (performer.getInventory().getNumItemsNotCoins() >= 99) {
                                performer.getCommunicator().sendNormalServerMessage("You don't have space for the slices.");
                                return true;
                            }
                            try {
                                final int w = 2000;
                                final Item slice2 = ItemFactory.createItem(target.getTemplateId(), target.getQualityLevel(), target.creator);
                                slice2.setWeight(w, true);
                                target.setWeight(target.getWeightGrams() - w, true);
                                slice2.setTemperature(target.getTemperature());
                                slice2.setAuxData(target.getAuxData());
                                slice2.setName("portion of " + target.getActualName());
                                slice2.setCreator(target.creator);
                                slice2.setDamage(target.getDamage());
                                slice2.setMaterial(target.getMaterial());
                                slice2.setSizes(10, 10, 20);
                                if (target.getRealTemplate() != null) {
                                    slice2.setRealTemplate(target.getRealTemplateId());
                                }
                                if (target.descIsExam()) {
                                    slice2.setDescription(target.examine(performer));
                                }
                                else {
                                    slice2.setDescription(target.getDescription());
                                }
                                if (target.color != -1) {
                                    slice2.setColor(target.color);
                                }
                                final ItemMealData imd = ItemMealData.getItemMealData(target.getWurmId());
                                if (imd != null) {
                                    ItemMealData.save(slice2.getWurmId(), imd.getRecipeId(), imd.getCalories(), imd.getCarbs(), imd.getFats(), imd.getProteins(), imd.getBonus(), imd.getStages(), imd.getIngredients());
                                }
                                performer.getInventory().insertItem(slice2);
                            }
                            catch (NoSuchTemplateException | FailedException ex5) {
                                final WurmServerException ex2;
                                final WurmServerException e = ex2;
                                ItemBehaviour.logger.log(Level.WARNING, e.getMessage());
                            }
                        }
                    }
                    else if (action == 739) {
                        if (stid == 1255 && target.canBeSealedByPlayer() && !target.isSealedByPlayer()) {
                            done = MethodsItems.seal(performer, source, target, act);
                        }
                        else if (stid == 561 && target.canBePeggedByPlayer() && !target.isSealedByPlayer()) {
                            done = MethodsItems.seal(performer, source, target, act);
                        }
                        else if ((stid == 748 || stid == 1272) && target.canBePapyrusWrapped() && !target.isWrapped() && source.getAuxData() == 2) {
                            done = MethodsItems.wrap(performer, source, target, act);
                        }
                        else if ((stid == 213 || stid == 926) && target.canBeClothWrapped() && !target.isWrapped()) {
                            done = MethodsItems.wrap(performer, source, target, act);
                        }
                        else if (target.isRaw() && target.canBeRawWrapped() && target.isPStateNone()) {
                            done = MethodsItems.wrap(performer, null, target, act);
                        }
                    }
                    else if (action == 740 && !target.isLiquid()) {
                        if (target.isSealedByPlayer()) {
                            if (target.isCrate()) {
                                if (performer.getWurmId() == target.getLastOwnerId() || performer.getPower() > 1) {
                                    done = MethodsItems.removeSecuritySeal(performer, target, act);
                                }
                                else {
                                    performer.getCommunicator().sendSafeServerMessage("Only the last owner can remove the security seal on the " + target.getName() + ".");
                                }
                            }
                            else {
                                done = MethodsItems.breakSeal(performer, target, act);
                            }
                        }
                        else if (target.isWrapped()) {
                            done = MethodsItems.unwrap(performer, target, act);
                        }
                    }
                    else if (action == 19) {
                        if (target.isSealedByPlayer()) {
                            this.tasteLiquid(performer, target);
                        }
                        else if (target.isFood() || target.isLiquid()) {
                            this.taste(performer, target);
                        }
                        else if (target.isContainerLiquid() && target.getItemCount() == 1) {
                            for (final Item k : target.getItems()) {
                                if (!k.isNoEatOrDrink() && !k.isUndistilled() && k.isDrinkable()) {
                                    this.taste(performer, k);
                                    break;
                                }
                            }
                        }
                    }
                    else if (action == 231) {
                        if (target.isColorable() && source.isColor()) {
                            done = MethodsItems.colorItem(performer, source, target, act, true);
                        }
                    }
                    else if (action == 232) {
                        if (stid == 441 && !target.isLiquid()) {
                            done = MethodsItems.removeColor(performer, source, target, act, true);
                        }
                    }
                    else if (action == 923) {
                        if (source.isColor() && target.supportsSecondryColor()) {
                            done = MethodsItems.colorItem(performer, source, target, act, false);
                        }
                    }
                    else if (action == 924) {
                        if (stid == 73 && !target.isLiquid() && target.supportsSecondryColor()) {
                            done = MethodsItems.removeColor(performer, source, target, act, false);
                        }
                    }
                    else if (action == 329) {
                        if (stid == 489 || (performer.getPower() >= 2 && (stid == 176 || stid == 315))) {
                            done = MethodsItems.watchSpyglass(performer, source, act, counter);
                        }
                    }
                    else if (action == 397) {
                        if (source.isPuppet() && target.isPuppet() && !source.equals(target)) {
                            done = MethodsItems.puppetSpeak(performer, source, target, act, counter);
                        }
                    }
                    else if (action == 472) {
                        if (source.getOwnerId() == performer.getWurmId() && stid == 676) {
                            done = true;
                            final MissionManager m = new MissionManager(performer, "Manage missions", "Select action", target.getWurmId(), target.getName(), source.getWurmId());
                            m.sendQuestion();
                        }
                    }
                    else if (action == 510) {
                        if (source.getOwnerId() == performer.getWurmId() && stid == 676) {
                            done = true;
                            if (source.getAuxData() >= 10 || performer.getPower() > 0) {
                                final AchievementCreation l = new AchievementCreation(performer, "Achievement Management", "Achievement properties", source.getWurmId());
                                l.sendQuestion();
                            }
                            else {
                                performer.getCommunicator().sendNormalServerMessage("The cost for creating an achievement is 10 charges. The " + source.getName() + " contains " + source.getAuxData() + ".");
                            }
                        }
                    }
                    else if (action == 370) {
                        done = true;
                        if (stid == 751 && ttid == 676) {
                            if (source.getOwnerId() != performer.getWurmId() || source.isTraded()) {
                                performer.getCommunicator().sendNormalServerMessage("You can not use the " + source.getName() + " right now.");
                            }
                            else if (target.getAuxData() > 90) {
                                performer.getCommunicator().sendNormalServerMessage("The charges would be lost. Use the " + target.getName() + " a bit more first.");
                            }
                            else {
                                Items.destroyItem(source.getWurmId());
                                target.setAuxData((byte)(target.getAuxData() + 10));
                                performer.getCommunicator().sendNormalServerMessage("You recharge the " + target.getName() + ". The " + source.getName() + " crumbles in the process.");
                            }
                        }
                    }
                    else if (action == 632) {
                        done = true;
                        if (stid == 867) {
                            if (Spell.mayBeEnchanted(target)) {
                                if (target.getRarity() < source.getRarity()) {
                                    if (!target.isFood() || source.getRarity() < 2) {
                                        if ((target.isCombine() && target.getWeightGrams() <= target.getTemplate().getWeightGrams()) || !target.isCombine()) {
                                            target.setRarity(source.getRarity());
                                            for (final Item sub : target.getItems()) {
                                                if (sub == null) {
                                                    continue;
                                                }
                                                if (!sub.isComponentItem()) {
                                                    continue;
                                                }
                                                sub.setRarity(source.getRarity());
                                            }
                                            Items.destroyItem(source.getWurmId());
                                            ItemBehaviour.logger.info(performer.getName() + " using a " + MethodsItems.getRarityDesc(source.getRarity()) + " strange bone (" + source.getWurmId() + ") on a " + target.getName() + " (" + target.getWurmId() + ")");
                                        }
                                        else {
                                            performer.getCommunicator().sendNormalServerMessage("The " + target.getName() + " has too much material.");
                                        }
                                    }
                                    else {
                                        performer.getCommunicator().sendNormalServerMessage("Nothing happens.");
                                    }
                                }
                                else {
                                    performer.getCommunicator().sendNormalServerMessage("The rarity would not improve.");
                                }
                            }
                            else {
                                performer.getCommunicator().sendNormalServerMessage("Nothing happens.");
                            }
                        }
                        else {
                            performer.getCommunicator().sendNormalServerMessage("Nothing happens.");
                        }
                    }
                    else if (action == 54) {
                        done = true;
                        if (ttid == 765 && (stid == 62 || source.isWeaponCrush())) {
                            performer.getCommunicator().sendNormalServerMessage("You crush the " + target.getName() + ". The " + target.getName() + " crumbles to salt.");
                            try {
                                final Item salt = ItemFactory.createItem(764, 99.0f, "");
                                salt.setWeight(target.getWeightGrams(), true);
                                performer.getInventory().insertItem(salt, true);
                                Items.destroyItem(target.getWurmId());
                            }
                            catch (FailedException e2) {
                                ItemBehaviour.logger.log(Level.WARNING, e2.getMessage(), e2);
                            }
                            catch (NoSuchTemplateException e3) {
                                ItemBehaviour.logger.log(Level.WARNING, e3.getMessage(), e3);
                            }
                        }
                    }
                    else if (action == 911) {
                        done = identifyFragment(act, performer, source, target, action, counter);
                    }
                    else if (action == 912) {
                        done = combineFragment(act, performer, source, target, action, counter);
                    }
                    else if (action == 354) {
                        done = true;
                        if (source.isRoyal() && (stid == 535 || stid == 529 || stid == 532)) {
                            if (performer.isKing()) {
                                final AppointmentsQuestion question = new AppointmentsQuestion(performer, "Appointments", "Which appointments do you wish to do today?", source.getWurmId());
                                question.sendQuestion();
                            }
                            else {
                                performer.getCommunicator().sendNormalServerMessage("You laugh at yourself - you who probably couldn't even appoint a cat to catch mice, now wielding a mighty sceptre! How preposterous!");
                            }
                        }
                    }
                    else if (action == 3 && target.isHollow() && !target.isSealedByPlayer() && (!target.getTemplate().hasViewableSubItems() || target.getTemplate().isContainerWithSubItems() || performer.getPower() > 0)) {
                        if (target.isSealedByPlayer()) {
                            return true;
                        }
                        if (target.getTemplateId() == 1342 && !target.isPlanted()) {
                            return true;
                        }
                        done = true;
                        final boolean isTop = target.getWurmId() == target.getTopParent() || (target.getTopParentOrNull() != null && target.getTopParentOrNull().getTemplate().hasViewableSubItems() && (!target.getTopParentOrNull().getTemplate().isContainerWithSubItems() || target.isPlacedOnParent()));
                        if (!isTop) {
                            if (target.isLockable()) {
                                if (target.getLockId() == -10L || (target.isDraggable() && MethodsItems.mayUseInventoryOfVehicle(performer, target))) {
                                    performer.getCommunicator().sendOpenInventoryContainer(target.getWurmId());
                                }
                                else {
                                    try {
                                        final Item lock2 = Items.getItem(target.getLockId());
                                        if (!lock2.getLocked() || target.isOwner(performer)) {
                                            performer.getCommunicator().sendOpenInventoryContainer(target.getWurmId());
                                        }
                                        else {
                                            final long[] keys2 = lock2.getKeyIds();
                                            for (int i = 0; i < keys2.length; ++i) {
                                                final Item key = Items.getItem(keys2[i]);
                                                if (key.getTopParent() == performer.getInventory().getWurmId()) {
                                                    performer.getCommunicator().sendOpenInventoryContainer(target.getWurmId());
                                                    return true;
                                                }
                                            }
                                        }
                                        performer.getCommunicator().sendSafeServerMessage("The " + target.getName() + " is locked. Please use the key to unlock and open it.");
                                    }
                                    catch (NoSuchItemException nsi4) {
                                        performer.getCommunicator().sendSafeServerMessage("The " + target.getName() + " is locked. Please use the key to unlock and open it.");
                                    }
                                }
                            }
                            else if (target.getTemplateId() == 272 && target.getWasBrandedTo() != -10L) {
                                if (!target.mayCommand(performer)) {
                                    performer.getCommunicator().sendNormalServerMessage("You do not have permissions.");
                                    return true;
                                }
                                performer.getCommunicator().sendOpenInventoryContainer(target.getWurmId());
                            }
                            else {
                                performer.getCommunicator().sendOpenInventoryContainer(target.getWurmId());
                            }
                            return done;
                        }
                        if (target.getTemplateId() == 272 && target.getWasBrandedTo() != -10L && !target.mayCommand(performer)) {
                            performer.getCommunicator().sendNormalServerMessage("You do not have permissions.");
                            return true;
                        }
                        if (target.getWurmId() != performer.getVehicle() && !performer.isWithinDistanceTo(target.getPosX(), target.getPosY(), target.getPosZ(), (target.isVehicle() && !target.isTent()) ? ((float)Math.max(6, target.getSizeZ() / 100)) : 6.0f)) {
                            return done;
                        }
                        if ((target.getTemplateId() == 1239 || target.getTemplateId() == 1175) && target.hasQueen() && !WurmCalendar.isSeasonWinter() && performer.getBestBeeSmoker() == null && performer.getPower() < 2) {
                            performer.getCommunicator().sendSafeServerMessage("The bees get angry and defend the " + target.getName() + " by stinging you.");
                            performer.addWoundOfType(null, (byte)5, 2, true, 1.0f, false, 5000.0f + Server.rand.nextFloat() * 7000.0f, 0.0f, 20.0f, false, false);
                            return true;
                        }
                        if (target.getTemplateId() == 1239) {
                            Achievements.triggerAchievement(performer.getWurmId(), 552);
                        }
                        if (target.isLockable()) {
                            final long lockId = target.getLockId();
                            if (target.getLockId() == -10L || (target.isDraggable() && MethodsItems.mayUseInventoryOfVehicle(performer, target)) || (target.getTemplateId() == 850 && MethodsItems.mayUseInventoryOfVehicle(performer, target)) || (target.isLocked() && target.mayAccessHold(performer))) {
                                if (performer.addItemWatched(target)) {
                                    if (target.getTemplateId() == 995 && target.getAuxData() < 100) {
                                        performer.achievement(367);
                                        target.setAuxData((byte)100);
                                    }
                                    if (target.getDescription().isEmpty()) {
                                        performer.getCommunicator().sendOpenInventoryWindow(target.getWurmId(), target.getName());
                                    }
                                    else {
                                        performer.getCommunicator().sendOpenInventoryWindow(target.getWurmId(), target.getName() + " [" + target.getDescription() + "]");
                                    }
                                    target.addWatcher(target.getWurmId(), performer);
                                    target.sendContainedItems(target.getWurmId(), performer);
                                }
                            }
                            else {
                                try {
                                    final Item lock3 = Items.getItem(lockId);
                                    final boolean hasKey = performer.hasKeyForLock(lock3);
                                    if (hasKey) {
                                        if (performer.addItemWatched(target)) {
                                            if (target.getDescription().isEmpty()) {
                                                performer.getCommunicator().sendOpenInventoryWindow(target.getWurmId(), target.getName());
                                            }
                                            else {
                                                performer.getCommunicator().sendOpenInventoryWindow(target.getWurmId(), target.getName() + " [" + target.getDescription() + "]");
                                            }
                                            target.addWatcher(target.getWurmId(), performer);
                                            target.sendContainedItems(target.getWurmId(), performer);
                                        }
                                        return true;
                                    }
                                }
                                catch (NoSuchItemException nsi5) {
                                    ItemBehaviour.logger.log(Level.WARNING, "No lock with id " + lockId + ", although the item has that.");
                                }
                            }
                        }
                        else if (performer.addItemWatched(target)) {
                            if (target.getTemplateId() == 995 && target.getAuxData() < 100) {
                                performer.achievement(367);
                                target.setAuxData((byte)100);
                            }
                            if (target.getDescription().isEmpty()) {
                                performer.getCommunicator().sendOpenInventoryWindow(target.getWurmId(), target.getName());
                            }
                            else {
                                performer.getCommunicator().sendOpenInventoryWindow(target.getWurmId(), target.getName() + " [" + target.getDescription() + "]");
                            }
                            target.addWatcher(target.getWurmId(), performer);
                            target.sendContainedItems(target.getWurmId(), performer);
                        }
                    }
                    else if (action == 181 || action == 99 || action == 697 || action == 696) {
                        if (target.isMoveable(performer) && (!target.isRoadMarker() || !target.isPlanted())) {
                            done = MethodsItems.moveItem(performer, target, counter, action, act);
                        }
                        else {
                            performer.getCommunicator().sendNormalServerMessage("You may not move that item right now.");
                        }
                    }
                    else if (action == 177 || action == 178) {
                        if (target.isTurnable(performer) && (!target.isRoadMarker() || !target.isPlanted())) {
                            done = MethodsItems.moveItem(performer, target, counter, action, act);
                        }
                        else {
                            performer.getCommunicator().sendNormalServerMessage("You may not turn that item right now.");
                        }
                    }
                    else if (action == 926) {
                        done = MethodsItems.placeLargeItem(performer, target, act, counter);
                    }
                    else if (action == 180) {
                        done = true;
                        if (performer.getPower() < 3 && (target.isArtifact() || target.isHugeAltar())) {
                            performer.getCommunicator().sendNormalServerMessage("You are not allowed to destroy that item.");
                        }
                        else {
                            performer.getLogger().log(Level.INFO, performer.getName() + " destroyed " + target.toString());
                            if (target.isRoadMarker()) {
                                target.setWhatHappened("destroyed by " + performer.getName());
                            }
                            Items.destroyItem(target.getWurmId());
                            if (target.isHugeAltar() && Constants.loadEndGameItems) {
                                for (final EndGameItem eg : EndGameItems.altars.values()) {
                                    if (eg.getWurmid() == target.getWurmId()) {
                                        if (eg.isHoly() && EndGameItems.getEvilAltar() != null) {
                                            performer.getCommunicator().sendAlertServerMessage("You will also need to destroy the Bone Altar to trigger a respawn on the next server restart.");
                                        }
                                        else if (!eg.isHoly() && EndGameItems.getGoodAltar() != null) {
                                            performer.getCommunicator().sendAlertServerMessage("You will also need to destroy the Altar of Three to trigger a respawn on the next server restart.");
                                        }
                                        else {
                                            performer.getCommunicator().sendAlertServerMessage("The huge altars will respawn on the next server restart.");
                                        }
                                        performer.getCommunicator().sendRemoveEffect(eg.getWurmid());
                                        EndGameItems.deleteEndGameItem(eg);
                                        break;
                                    }
                                }
                            }
                            performer.getCommunicator().sendNormalServerMessage("You destroy " + target.getNameWithGenus() + ".");
                            Items.destroyItem(target.getWurmId());
                        }
                    }
                    else if (action == 486) {
                        done = true;
                        if (performer.getPower() < 2 || ttid != 1437) {
                            performer.getCommunicator().sendNormalServerMessage("Now now, none of that. You shouldn't be here.");
                        }
                        else {
                            target.addSnowmanItem();
                        }
                    }
                    else if (action == 519) {
                        if (stid == 788 && target.isMetal() && !target.isLiquid()) {
                            if (target.getTemplateId() != Materials.getTemplateIdForMaterial(target.getMaterial())) {
                                if (!target.isIndestructible() && !target.isArtifact() && !target.isUnique() && !target.getTemplate().isStatue() && !target.isRoyal() && !target.getTemplate().isRune() && target.getTemplateId() != 1307 && target.getTemplateId() != 1423) {
                                    if (target.getTemperature() > 6000) {
                                        if (target.getParentId() > -10L) {
                                            if (!target.isHollow() || target.isEmpty(false)) {
                                                if (target.getSpellEffects() == null || target.getSpellEffects().getEffects().length == 0) {
                                                    if (!MethodsItems.checkIfStealing(target, performer, act)) {
                                                        if (target.enchantment == 0) {
                                                            if (target.isComponentItem()) {
                                                                performer.getCommunicator().sendNormalServerMessage("You cannot smelt that.");
                                                                return true;
                                                            }
                                                            if (target.isUnfinished()) {
                                                                performer.getCommunicator().sendNormalServerMessage("You cannot smelt that, finish it first.");
                                                                return true;
                                                            }
                                                            if (target.getTemplateId() == 692 || target.getTemplateId() == 693 || target.getTemplateId() == 696 || target.getTemplateId() == 697) {
                                                                performer.getCommunicator().sendNormalServerMessage("The ore needs to be hotter.");
                                                                return true;
                                                            }
                                                            if (target.getTemplateId() == 1100) {
                                                                performer.getCommunicator().sendNormalServerMessage("The " + target.getName() + " seems to resist smelting!");
                                                                return true;
                                                            }
                                                            if (target.getTemplateId() == 143) {
                                                                target.setWeight(3, true);
                                                            }
                                                            else {
                                                                final CreationEntry e4 = CreationMatrix.getInstance().getCreationEntry(target.getTemplateId());
                                                                int weightToReturn = 0;
                                                                int weightToReduce = 0;
                                                                boolean isCombine = false;
                                                                if (e4 != null) {
                                                                    try {
                                                                        final ItemTemplate src = ItemTemplateFactory.getInstance().getTemplate(e4.getObjectSource());
                                                                        if (src.getMaterial() == target.getMaterial()) {
                                                                            if (src.isCombine()) {
                                                                                isCombine = true;
                                                                            }
                                                                            weightToReturn = Math.min(target.getWeightGrams(), src.getWeightGrams());
                                                                            ItemBehaviour.logger.log(Level.INFO, "1. Adding weightToReturn " + weightToReturn + " for " + src.getTemplateId() + " srcw=" + src.getWeightGrams() + " tgw=" + target.getWeightGrams() + " combine=" + isCombine);
                                                                        }
                                                                        else if (e4.depleteSource || e4.depleteEqually) {
                                                                            weightToReduce = Math.min(target.getWeightGrams(), src.getWeightGrams());
                                                                            ItemBehaviour.logger.log(Level.INFO, "2. Adding weightToReduce " + weightToReduce + " for " + src.getTemplateId() + " srcw=" + src.getWeightGrams() + " tgw=" + target.getWeightGrams());
                                                                        }
                                                                    }
                                                                    catch (NoSuchTemplateException ex6) {}
                                                                    try {
                                                                        final ItemTemplate src = ItemTemplateFactory.getInstance().getTemplate(e4.getObjectTarget());
                                                                        if (Materials.isMetal(src.getMaterial()) || src.getMaterial() == target.getMaterial() || (src.getMaterial() == 0 && src.isMetal())) {
                                                                            if (src.isCombine()) {
                                                                                isCombine = true;
                                                                            }
                                                                            weightToReturn += Math.min(target.getWeightGrams(), src.getWeightGrams());
                                                                            ItemBehaviour.logger.log(Level.INFO, "3. Adding weightToReturn " + weightToReturn + " for " + src.getTemplateId() + " srcw=" + src.getWeightGrams() + " tgw=" + target.getWeightGrams() + " combine=" + isCombine);
                                                                        }
                                                                        else {
                                                                            weightToReduce = Math.min(target.getWeightGrams(), src.getWeightGrams());
                                                                            ItemBehaviour.logger.log(Level.INFO, "4. Adding weightToReduce " + weightToReduce + " for " + src.getTemplateId() + " srcw=" + src.getWeightGrams() + " tgw=" + target.getWeightGrams());
                                                                        }
                                                                    }
                                                                    catch (NoSuchTemplateException ex7) {}
                                                                    if (!isCombine) {
                                                                        for (final CreationRequirement req : e4.getRequirements()) {
                                                                            if (req.willBeConsumed()) {
                                                                                try {
                                                                                    final ItemTemplate src2 = ItemTemplateFactory.getInstance().getTemplate(req.getResourceTemplateId());
                                                                                    if (src2.getMaterial() == target.getMaterial()) {
                                                                                        weightToReturn += src2.getWeightGrams() * req.getResourceNumber();
                                                                                    }
                                                                                }
                                                                                catch (NoSuchTemplateException ex8) {}
                                                                            }
                                                                        }
                                                                    }
                                                                    else {
                                                                        for (final CreationRequirement req : e4.getRequirements()) {
                                                                            if (req.willBeConsumed()) {
                                                                                try {
                                                                                    final ItemTemplate src2 = ItemTemplateFactory.getInstance().getTemplate(req.getResourceTemplateId());
                                                                                    if (src2.getMaterial() != target.getMaterial()) {
                                                                                        weightToReduce += src2.getWeightGrams() * req.getResourceNumber();
                                                                                    }
                                                                                }
                                                                                catch (NoSuchTemplateException ex9) {}
                                                                            }
                                                                        }
                                                                    }
                                                                }
                                                                else {
                                                                    weightToReturn = target.getWeightGrams();
                                                                    weightToReduce = target.getWeightGrams() / 10;
                                                                }
                                                                if (weightToReduce + weightToReturn == target.getWeightGrams()) {
                                                                    ItemBehaviour.logger.log(Level.INFO, "8. Setting weight " + (weightToReturn - weightToReturn / 10));
                                                                    target.setWeight(weightToReturn - weightToReturn / 10, true);
                                                                }
                                                                else if (!isCombine) {
                                                                    target.setWeight(weightToReturn - weightToReturn / 10, true);
                                                                    ItemBehaviour.logger.log(Level.INFO, "9. Setting weight " + (weightToReturn - weightToReturn / 10));
                                                                }
                                                                else if (weightToReduce + weightToReturn > target.getWeightGrams()) {
                                                                    ItemBehaviour.logger.log(Level.INFO, "10. Setting weight " + (target.getWeightGrams() - weightToReduce - weightToReturn / 10));
                                                                    target.setWeight(target.getWeightGrams() - weightToReduce - weightToReturn / 10, true);
                                                                }
                                                                else if (weightToReduce < target.getWeightGrams()) {
                                                                    target.setWeight(target.getWeightGrams() - weightToReduce - target.getWeightGrams() / 10, true);
                                                                }
                                                                else {
                                                                    target.setWeight(weightToReturn - weightToReturn / 10, true);
                                                                }
                                                            }
                                                            if (!target.deleted) {
                                                                target.setDamage(0.0f);
                                                                float qlMod = 1.0f;
                                                                if (!target.isRepairable()) {
                                                                    qlMod = 0.975f - (100.0f - source.getCurrentQualityLevel()) / 100.0f * 0.05f;
                                                                    if (target.isMoonMetal() || target.isAlloyMetal()) {
                                                                        qlMod -= 0.05f;
                                                                    }
                                                                }
                                                                target.setQualityLevel(Math.min(target.getCurrentQualityLevel() * qlMod, source.getCurrentQualityLevel()));
                                                                target.setCreationState((byte)0);
                                                                target.setCreator("");
                                                                target.setRealTemplate(-10);
                                                                target.setTemplateId(Materials.getTemplateIdForMaterial(target.getMaterial()));
                                                                if (target.getLockId() > 0L) {
                                                                    target.setLockId(-10L);
                                                                }
                                                            }
                                                            source.setDamage(source.getDamage() + source.getDamageModifier() * 0.002f);
                                                        }
                                                        else {
                                                            performer.getCommunicator().sendNormalServerMessage("The " + target.getName() + " resists smelting.");
                                                        }
                                                    }
                                                    else {
                                                        performer.getCommunicator().sendNormalServerMessage("You need to steal the " + target.getName() + " first.");
                                                    }
                                                }
                                                else {
                                                    performer.getCommunicator().sendNormalServerMessage("The " + target.getName() + " resists smelting.");
                                                }
                                            }
                                            else {
                                                performer.getCommunicator().sendNormalServerMessage("You'd want to empty the " + target.getName() + " first.");
                                            }
                                        }
                                        else {
                                            performer.getCommunicator().sendNormalServerMessage("The " + target.getName() + " needs to be in a container.");
                                        }
                                    }
                                    else {
                                        performer.getCommunicator().sendNormalServerMessage("The " + target.getName() + " is not hot enough.");
                                    }
                                }
                                else {
                                    performer.getCommunicator().sendNormalServerMessage("You can't bring yourself to smelt the " + target.getName() + ". What's up with you today?");
                                }
                            }
                            else {
                                performer.getCommunicator().sendNormalServerMessage("It makes no sense to smelt " + target.getNameWithGenus() + ". There would be no change.");
                            }
                        }
                    }
                    else if (action == 380) {
                        if (source.isCoin() && target.isSpringFilled() && target.getSpellCourierBonus() > 0.0f) {
                            final WishQuestion wq = new WishQuestion(performer, "Make a wish", "What do you wish?", target.getWurmId(), source.getWurmId());
                            wq.sendQuestion();
                        }
                    }
                    else if (action == 145) {
                        done = true;
                        if (stid == 20 && (ttid == 692 || ttid == 696)) {
                            done = MethodsItems.mine(performer, source, target, counter, act);
                        }
                        else if (stid == 20 && target.getTemplate().isRiftStoneDeco()) {
                            done = MethodsItems.gatherRiftResource(performer, source, target, counter, act);
                        }
                    }
                    else if (action == 96) {
                        if (source.isWeaponAxe() && target.getTemplate().isRiftPlantDeco()) {
                            done = MethodsItems.gatherRiftResource(performer, source, target, counter, act);
                        }
                    }
                    else if (action == 156) {
                        if (stid == 20 && target.getTemplate().isRiftCrystalDeco()) {
                            done = MethodsItems.gatherRiftResource(performer, source, target, counter, act);
                        }
                    }
                    else if (action >= 496 && action <= 502) {
                        done = MethodsReligion.performRitual(performer, source, target, counter, action, act);
                    }
                    else if (action == 184) {
                        done = true;
                        if (performer.getPower() >= 3) {
                            if (stid == 176) {
                                Methods.sendShutdownQuestion(performer, source);
                            }
                            else if (ttid == 176) {
                                Methods.sendShutdownQuestion(performer, target);
                            }
                        }
                        else {
                            performer.getCommunicator().sendNormalServerMessage("This option is currently unavailable to your level.");
                        }
                    }
                    else if (action == 135) {
                        done = true;
                        if (performer.getPower() >= 3) {
                            if (stid == 176) {
                                if (ttid != 176) {
                                    Methods.sendHideQuestion(performer, source, target);
                                }
                                else {
                                    performer.getCommunicator().sendNormalServerMessage("You can't hide a " + target.getName() + ".");
                                }
                            }
                            else if (ttid == 176) {
                                Methods.sendHideQuestion(performer, source, target);
                            }
                        }
                        else {
                            performer.getCommunicator().sendNormalServerMessage("This option is currently unavailable to your level.");
                        }
                    }
                    else if (action == 194) {
                        done = true;
                        if (performer.getPower() >= 3) {
                            if (stid == 176) {
                                Methods.sendPaymentQuestion(performer, source);
                            }
                            else if (ttid == 176) {
                                Methods.sendPaymentQuestion(performer, target);
                            }
                        }
                        else {
                            performer.getCommunicator().sendNormalServerMessage("This option is currently unavailable to your level.");
                        }
                    }
                    else if (action == 460) {
                        performer.getCommunicator().sendNormalServerMessage("Wrapping is disabled for now since it was mostly used to exploit.");
                        done = true;
                    }
                    else if (action == 463) {
                        done = true;
                        if (target.getOwnerId() != performer.getWurmId() || source.getOwnerId() != performer.getWurmId()) {
                            performer.getCommunicator().sendNormalServerMessage("You need to hold both the " + source.getName() + " and the " + target.getName() + ".");
                            return true;
                        }
                        if (ttid == 1076 && source.isGem()) {
                            if (target.getData1() <= 0) {
                                done = false;
                                if (counter == 1.0f) {
                                    performer.getCommunicator().sendNormalServerMessage("You start inserting the " + source.getName() + ".");
                                    Server.getInstance().broadCastAction(performer.getName() + " starts inserting " + source.getNameWithGenus() + " into " + target.getNameWithGenus() + ".", performer, 5);
                                    performer.sendActionControl(act.getActionString(), true, 400);
                                }
                                if (act.currentSecond() == 5) {
                                    performer.getCommunicator().sendNormalServerMessage("You investigate the " + source.getName() + " and the " + target.getName() + ".");
                                    Server.getInstance().broadCastAction(performer.getName() + " investigates the " + source.getName() + " and the " + target.getName() + ".", performer, 5);
                                }
                                if (act.currentSecond() == 10) {
                                    int skillRequired = 40;
                                    if (stid == 383 || stid == 375 || stid == 377 || stid == 379 || stid == 381) {
                                        skillRequired = 60;
                                    }
                                    boolean ok2 = false;
                                    try {
                                        final Skill jewelry = performer.getSkills().getSkill(10043);
                                        if (jewelry.getKnowledge(0.0) >= skillRequired) {
                                            ok2 = true;
                                        }
                                        if (ok2) {
                                            performer.getCommunicator().sendNormalServerMessage("You believe that this should be possible given your jewelry smithing skills.");
                                            Server.getInstance().broadCastAction(performer.getName() + " grunts and focuses on the task.", performer, 5);
                                        }
                                    }
                                    catch (NoSuchSkillException ex10) {}
                                    if (!ok2) {
                                        performer.getCommunicator().sendNormalServerMessage("You don't believe this will be possible given your relative inexperience with jewelry smithing.");
                                        Server.getInstance().broadCastAction(performer.getName() + " sighs and shrugs and gives up.", performer, 5);
                                        return true;
                                    }
                                }
                                if (act.currentSecond() == 40) {
                                    done = true;
                                    int qlset = (int)Math.max(1.0f, source.getQualityLevel() / 2.0f);
                                    int skillRequired2 = 40;
                                    if (stid == 383 || stid == 375 || stid == 377 || stid == 379 || stid == 381) {
                                        skillRequired2 = 60;
                                        qlset = (int)(50.0f + source.getQualityLevel() / 2.0f);
                                    }
                                    try {
                                        final Skill jewelry = performer.getSkills().getSkill(10043);
                                        jewelry.skillCheck(skillRequired2, source.getQualityLevel(), false, 40.0f);
                                    }
                                    catch (NoSuchSkillException ex11) {}
                                    performer.getCommunicator().sendNormalServerMessage("You skillfully insert the " + source.getName() + " into the " + target.getName() + ".");
                                    Server.getInstance().broadCastAction(performer.getName() + " rejoices as " + performer.getHeSheItString() + " inserts the " + source.getName() + " into the " + target.getName() + "!", performer, 5);
                                    target.setData2(qlset);
                                    int data1 = 0;
                                    switch (stid) {
                                        case 382:
                                        case 383: {
                                            data1 = 1;
                                            break;
                                        }
                                        case 374:
                                        case 375: {
                                            data1 = 2;
                                            break;
                                        }
                                        case 376:
                                        case 377: {
                                            data1 = 3;
                                            break;
                                        }
                                        case 378:
                                        case 379: {
                                            data1 = 4;
                                            break;
                                        }
                                        case 380:
                                        case 381: {
                                            data1 = 5;
                                            break;
                                        }
                                    }
                                    if (source.getRarity() > target.getRarity()) {
                                        target.setRarity(source.getRarity());
                                        if (target.getRarity() == 1) {
                                            target.setAuxData((byte)50);
                                        }
                                        else if (target.getRarity() == 2) {
                                            target.setAuxData((byte)80);
                                        }
                                        else if (target.getRarity() == 3) {
                                            target.setAuxData((byte)120);
                                        }
                                    }
                                    target.setData1(data1);
                                    Items.destroyItem(source.getWurmId());
                                }
                            }
                            else {
                                performer.getCommunicator().sendNormalServerMessage("The " + target.getName() + " already contains a gem.");
                            }
                        }
                    }
                    else if (action == 922) {
                        if (!performer.getInventory().mayCreatureInsertItem()) {
                            performer.getCommunicator().sendNormalServerMessage("Your inventory is now full. You would have no space to put whatever you find.");
                            return true;
                        }
                        if (target.getTemperature() > 200) {
                            performer.getCommunicator().sendNormalServerMessage("Ouch! Maybe you shouldn't do that while the " + target.getName() + " is lit.");
                            return true;
                        }
                        if (target.getTemplateId() == 180 || target.getTemplateId() == 178 || target.getTemplateId() == 1023 || target.getTemplateId() == 1028) {
                            if (source.getTemplateId() == 25) {
                                if (target.getParentId() != -10L) {
                                    performer.getCommunicator().sendNormalServerMessage("The " + target.getName() + " must be on the ground.");
                                    return true;
                                }
                                if (performer.getStatus().getStamina() < 10000) {
                                    performer.getCommunicator().sendNormalServerMessage("You are too exhausted.");
                                    return true;
                                }
                                done = false;
                                if (counter == 1.0f) {
                                    if (target.getAuxData() == 0) {
                                        performer.getCommunicator().sendNormalServerMessage("The " + target.getName() + " is already clean.");
                                        return true;
                                    }
                                    performer.getCommunicator().sendNormalServerMessage("You start removing the ash from the " + target.getName() + ".");
                                    Server.getInstance().broadCastAction(performer.getName() + " starts removing the ash from " + target.getNameWithGenus() + ".", performer, 5);
                                    performer.sendActionControl(act.getActionString(), true, 300);
                                    target.setAuxData((byte)0);
                                }
                                if (counter >= act.getNextTick()) {
                                    if (act.getRarity() != 0) {
                                        performer.playPersonalSound("sound.fx.drumroll");
                                    }
                                    final int searchCount = act.getTickCount();
                                    final int maxSearches = target.getAuxData() / 10;
                                    act.incTickCount();
                                    act.incNextTick(10.0f);
                                    try {
                                        final float power = (float)performer.getSkills().getSkill(1010).getKnowledge();
                                        final float min = Math.min(target.getQualityLevel(), power);
                                        final float max = Math.max(target.getQualityLevel(), power);
                                        final float ql = Server.rand.nextFloat() * (max - min) + min;
                                        final Item newItem = ItemFactory.createItem(141, ql, act.getRarity(), null);
                                        final Item inventory = performer.getInventory();
                                        inventory.insertItem(newItem);
                                        performer.getCommunicator().sendNormalServerMessage("You found some " + newItem.getName() + "!");
                                        Server.getInstance().broadCastAction(performer.getName() + " puts something in " + performer.getHisHerItsString() + " pocket.", performer, 5);
                                        target.setAuxData((byte)0);
                                    }
                                    catch (FailedException | NoSuchTemplateException | NoSuchSkillException ex12) {
                                        final WurmServerException ex3;
                                        final WurmServerException fe4 = ex3;
                                        ItemBehaviour.logger.log(Level.WARNING, performer.getName() + " " + fe4.getMessage(), fe4);
                                    }
                                    if (searchCount < maxSearches) {
                                        act.setRarity(performer.getRarity());
                                    }
                                }
                                if (act.currentSecond() >= 29) {
                                    try {
                                        final Skill fms = performer.getSkills().getSkillOrLearn(1010);
                                        final int knowledge = (int)fms.getKnowledge(0.0);
                                        fms.skillCheck(knowledge, 0.0, false, 1.0f);
                                        double power2 = fms.skillCheck(knowledge, 0.0, false, 1.0f);
                                        if (target.getQualityLevel() > power2) {
                                            power2 = (float)performer.getSkills().getSkill(1010).getKnowledge();
                                        }
                                        if (target.getQualityLevel() < power2) {
                                            power2 = (float)performer.getSkills().getSkill(1010).getKnowledge();
                                        }
                                        final float min2 = (float)Math.min(target.getQualityLevel(), power2);
                                        final float max2 = (float)Math.max(target.getQualityLevel(), power2);
                                        final float ql2 = Server.rand.nextFloat() * (max2 - min2) + min2;
                                        final Item newItem2 = ItemFactory.createItem(141, ql2, act.getRarity(), null);
                                        final Item inventory2 = performer.getInventory();
                                        inventory2.insertItem(newItem2);
                                        performer.getCommunicator().sendNormalServerMessage("You found some " + newItem2.getName() + "!");
                                        Server.getInstance().broadCastAction(performer.getName() + " puts something in " + performer.getHisHerItsString() + " pocket.", performer, 5);
                                        target.setAuxData((byte)0);
                                    }
                                    catch (Exception ex) {
                                        ex.printStackTrace();
                                    }
                                    return true;
                                }
                            }
                            else {
                                performer.getCommunicator().sendNormalServerMessage("You need to use a shovel to do that.");
                            }
                        }
                        else {
                            performer.getCommunicator().sendNormalServerMessage("You can't clean that.");
                        }
                    }
                    else if (action == 462) {
                        done = true;
                        if (stid == 654 && source.getAuxData() == 0) {
                            if (target.getOwnerId() == performer.getWurmId() || target.getOwnerId() == -10L) {
                                if (target.getOwnerId() == -10L && MethodsItems.checkIfStealing(target, performer, act)) {
                                    performer.getCommunicator().sendNormalServerMessage("You are not allowed to do that.");
                                    return true;
                                }
                                if (target.isTransmutable()) {
                                    int targetTemplateId = Materials.getTransmutedTemplate(target.getTemplateId());
                                    if (target.getTemplateId() == 204 && Server.rand.nextInt((performer.getPower() >= 5) ? 5 : 100) == 0) {
                                        targetTemplateId = 380;
                                    }
                                    final int nums = target.getWeightGrams() / target.getTemplate().getWeightGrams();
                                    done = false;
                                    if (counter == 1.0f) {
                                        performer.getCommunicator().sendNormalServerMessage("You notice that a reaction occurs!");
                                        Server.getInstance().broadCastAction(performer.getName() + " starts to transmutate " + target.getNameWithGenus() + ".", performer, 5);
                                        performer.sendActionControl(act.getActionString(), true, 200);
                                    }
                                    if (act.currentSecond() == 5) {
                                        if (nums <= 0) {
                                            performer.getCommunicator().sendNormalServerMessage("The " + target.getName() + " contains too little material to be transmutated properly.");
                                            return true;
                                        }
                                        if (nums > source.getWeightGrams()) {
                                            performer.getCommunicator().sendNormalServerMessage("You understand that your " + source.getName() + " will not suffice to transmutate all the material.");
                                            return true;
                                        }
                                    }
                                    if (act.currentSecond() == 10) {
                                        performer.getCommunicator().sendNormalServerMessage("You watch with interest as the reaction proceeds.");
                                        Server.getInstance().broadCastAction(performer.getName() + " intensely watches the " + target.getNameWithGenus() + " as it seems to change.", performer, 5);
                                    }
                                    if (act.currentSecond() == 20) {
                                        done = true;
                                        performer.getCommunicator().sendNormalServerMessage("The change is complete!");
                                        Server.getInstance().broadCastAction(performer.getName() + " rejoices as the " + target.getNameWithGenus() + " changes to something new!", performer, 5);
                                        try {
                                            final ItemTemplate targetTemplate = ItemTemplateFactory.getInstance().getTemplate(targetTemplateId);
                                            final int weight2 = Math.min(targetTemplate.getWeightGrams() * nums, target.getWeightGrams());
                                            target.setWeight(weight2, true);
                                            target.setTemplateId(targetTemplateId);
                                        }
                                        catch (NoSuchTemplateException nst3) {
                                            performer.getCommunicator().sendNormalServerMessage("Nothing happens after all.");
                                            return true;
                                        }
                                        source.setWeight(source.getWeightGrams() - nums, true);
                                    }
                                }
                                else {
                                    performer.getCommunicator().sendNormalServerMessage("A quick test reveals that no reaction occurs.");
                                }
                            }
                            else {
                                performer.getCommunicator().sendNormalServerMessage("You need to be in control of the " + target.getName() + ".");
                            }
                        }
                        else {
                            performer.getCommunicator().sendNormalServerMessage("You do a basic test and conclude that nothing will happen to the " + target.getName() + ".");
                        }
                    }
                    else if (action == 195) {
                        done = true;
                        if (performer.getPower() >= 3) {
                            if (stid == 176) {
                                Methods.sendPowerManagementQuestion(performer, source);
                            }
                            else if (ttid == 176) {
                                Methods.sendPowerManagementQuestion(performer, target);
                            }
                        }
                        else {
                            performer.getCommunicator().sendNormalServerMessage("This option is currently unavailable to your level.");
                        }
                    }
                    else if (action == 212) {
                        done = true;
                        if (WurmPermissions.maySetFaith(performer)) {
                            if (stid == 176) {
                                Methods.sendFaithManagementQuestion(performer, source);
                            }
                            else if (ttid == 176) {
                                Methods.sendFaithManagementQuestion(performer, target);
                            }
                        }
                        else {
                            performer.getCommunicator().sendNormalServerMessage("This option is currently unavailable to your level.");
                        }
                    }
                    else if (action == 481) {
                        done = true;
                        if (performer.getPower() >= 3) {
                            if (stid == 176) {
                                Methods.sendConfigureTwitter(performer, Servers.localServer.id, false, Servers.localServer.name);
                            }
                            else if (ttid == 176) {
                                Methods.sendConfigureTwitter(performer, Servers.localServer.id, false, Servers.localServer.name);
                            }
                        }
                        else {
                            performer.getCommunicator().sendNormalServerMessage("This option is currently unavailable to your level.");
                        }
                    }
                    else if (action == 503) {
                        done = true;
                        if (performer.getPower() >= 2 && (stid == 176 || stid == 315)) {
                            Methods.sendCreateZone(performer);
                        }
                    }
                    else if (action == 244) {
                        done = true;
                        if (performer.getPower() >= 1) {
                            if (stid == 176 || stid == 315 || stid == 1027 || stid == 174) {
                                Methods.sendServerManagementQuestion(performer, source.getWurmId());
                            }
                            else if (ttid == 176 || ttid == 315 || ttid == 1027 || stid == 174) {
                                Methods.sendServerManagementQuestion(performer, target.getWurmId());
                            }
                        }
                        else {
                            performer.getCommunicator().sendNormalServerMessage("This option is currently unavailable to your level.");
                        }
                    }
                    else if (action == 132) {
                        if (source.getTemplateId() == 457 && target.isBowUnstringed()) {
                            done = MethodsItems.string(performer, source, target, act);
                        }
                        else if ((stid == 150 || stid == 151) && ttid == 780) {
                            done = MethodsItems.stringRod(performer, source, target, act);
                        }
                    }
                    else if (action == 674) {
                        done = true;
                        if ((stid == 176 || stid == 315) && performer.getPower() >= 2) {
                            String name = target.getName();
                            if (target.getRarity() == 1) {
                                name = "rare " + name;
                            }
                            else if (target.getRarity() == 2) {
                                name = "supreme " + name;
                            }
                            else if (target.getRarity() == 3) {
                                name = "fantastic " + name;
                            }
                            performer.setTagItem(target.getWurmId(), name);
                        }
                    }
                    else if (action == 179) {
                        done = true;
                        if ((stid == 176 || stid == 315) && performer.getPower() >= 2) {
                            try {
                                final Zone currZone = Zones.getZone((int)target.getPosX() >> 2, (int)target.getPosY() >> 2, target.isOnSurface());
                                currZone.removeItem(target);
                                target.putItemInfrontof(performer);
                            }
                            catch (NoSuchZoneException nsz2) {
                                performer.getCommunicator().sendNormalServerMessage("Failed to locate the zone for that item. Failed to summon.");
                                ItemBehaviour.logger.log(Level.WARNING, target.getWurmId() + ": " + nsz2.getMessage(), nsz2);
                            }
                            catch (NoSuchCreatureException nsc2) {
                                performer.getCommunicator().sendNormalServerMessage("Failed to locate the creature for that request.. you! Failed to summon.");
                                ItemBehaviour.logger.log(Level.WARNING, target.getWurmId() + ": " + nsc2.getMessage(), nsc2);
                            }
                            catch (NoSuchItemException nsi) {
                                performer.getCommunicator().sendNormalServerMessage("Failed to locate the item for that request! Failed to summon.");
                                ItemBehaviour.logger.log(Level.WARNING, target.getWurmId() + ":" + nsi.getMessage(), nsi);
                            }
                            catch (NoSuchPlayerException nsp2) {
                                performer.getCommunicator().sendNormalServerMessage("Failed to locate the creature for that request.. you! Failed to summon.");
                                ItemBehaviour.logger.log(Level.WARNING, target.getWurmId() + ":" + nsp2.getMessage(), nsp2);
                            }
                        }
                    }
                    else if (action == 92) {
                        done = true;
                        if (performer.getPower() >= 3) {
                            if (source.getTemplateId() == 176) {
                                Methods.sendLearnSkillQuestion(performer, source, target.getWurmId());
                            }
                        }
                        else {
                            performer.getCommunicator().sendNormalServerMessage("This option is currently unavailable to your level.");
                        }
                    }
                    else if (action == 189) {
                        done = true;
                        if (source.isContainerLiquid() && target.isLiquid()) {
                            MethodsItems.fillContainer(act, source, target, performer, false);
                        }
                        else if (source.isOilConsuming() && target.isLiquidInflammable()) {
                            final byte already = source.getAuxData();
                            final byte avail = (byte)(126 - already);
                            if (avail == 0) {
                                performer.getCommunicator().sendNormalServerMessage("The " + source.getName() + " is full already.");
                            }
                            else if (avail > target.getWeightGrams()) {
                                source.setAuxData((byte)(already + target.getWeightGrams()));
                                performer.getCommunicator().sendNormalServerMessage("You fill the " + source.getName() + ".");
                                Items.destroyItem(target.getWurmId());
                            }
                            else {
                                source.setAuxData((byte)126);
                                target.setWeight(target.getWeightGrams() - avail, true);
                                performer.getCommunicator().sendNormalServerMessage("You fill the " + source.getName() + ".");
                            }
                        }
                        else if (source.isOilConsuming() && target.isLiquid()) {
                            performer.getCommunicator().sendNormalServerMessage("You must fill the " + source.getName() + " with a burning liquid.");
                        }
                        else if (target.isOilConsuming() && source.isLiquidInflammable()) {
                            final byte already = target.getAuxData();
                            final byte avail = (byte)(126 - already);
                            if (avail == 0) {
                                performer.getCommunicator().sendNormalServerMessage("The " + target.getName() + " is full already.");
                            }
                            else if (avail > source.getWeightGrams()) {
                                target.setAuxData((byte)(already + source.getWeightGrams()));
                                performer.getCommunicator().sendNormalServerMessage("You fill the " + target.getName() + ".");
                                Items.destroyItem(source.getWurmId());
                            }
                            else {
                                target.setAuxData((byte)126);
                                source.setWeight(source.getWeightGrams() - avail, true);
                                performer.getCommunicator().sendNormalServerMessage("You fill the " + target.getName() + ".");
                            }
                        }
                        else if (stid == 133 && target.isCandleHolder()) {
                            if (target.getAuxData() >= 126) {
                                performer.getCommunicator().sendNormalServerMessage("The " + target.getName() + " can hold no more candles.");
                            }
                            else {
                                final boolean nocandle = target.getAuxData() == 0;
                                performer.getCommunicator().sendNormalServerMessage("You put the " + source.getName() + " " + ((ttid == 729) ? "on" : "in") + " the " + target.getName() + ".");
                                final int toadd = (int)(source.getCurrentQualityLevel() / ((source.getRealTemplateId() == -1) ? 2.0f : 1.5f));
                                target.setAuxData((byte)Math.min(126, target.getAuxData() + Math.max(1, toadd)));
                                Items.destroyItem(source.getWurmId());
                                if (nocandle && ttid == 729) {
                                    performer.getCommunicator().sendNormalServerMessage("You changed the cake into a birthday cake!");
                                    performer.getCommunicator().sendUpdateInventoryItem(target);
                                    performer.getCommunicator().sendRename(target, target.getName(), target.getModelName());
                                }
                            }
                        }
                    }
                    else if (action == 89) {
                        done = true;
                        if (performer.getPower() <= 0) {
                            MethodsCreatures.sendSetKingdomQuestion(performer, target);
                        }
                        else if (performer.getPower() >= 2) {
                            MethodsCreatures.sendSetKingdomQuestion(performer, target);
                        }
                        else {
                            performer.getCommunicator().sendNormalServerMessage("This option is currently unavailable to your level.");
                        }
                    }
                    else if (action == 91) {
                        if ((source.getTemplateId() == 176 || source.getTemplateId() == 315) && performer.getPower() >= 2) {
                            final float nut = (50 + Server.rand.nextInt(49)) / 100.0f;
                            performer.getStatus().setMaxCCFP();
                            performer.getStatus().refresh(nut, false);
                            if (performer.getPower() >= 4 && performer.getDeity() != null && performer.isPriest()) {
                                try {
                                    performer.setFavor(performer.getFaith());
                                }
                                catch (IOException e5) {
                                    ItemBehaviour.logger.log(Level.WARNING, e5.getMessage(), e5);
                                }
                            }
                            done = true;
                        }
                        else {
                            done = this.action(act, performer, target, action, counter);
                        }
                    }
                    else if (action == 95) {
                        done = true;
                        MethodsCreatures.teleportCreature(performer, source);
                    }
                    else if (action == 94) {
                        done = true;
                        if ((source.getTemplateId() == 176 || source.getTemplateId() == 315 || source.getTemplateId() == 1027) && performer.getPower() >= 1) {
                            Methods.sendTeleportQuestion(performer, source);
                        }
                    }
                    else if (action == 539) {
                        done = true;
                        if (performer.getPower() >= 4 && Spell.mayBeEnchanted(target)) {
                            Methods.sendGmSetEnchantQuestion(performer, target);
                        }
                    }
                    else if (act.isSpell()) {
                        done = true;
                        final Spell spell = Spells.getSpell(action);
                        if (spell != null) {
                            if (spell.religious) {
                                if (performer.getDeity() != null) {
                                    if (source.isHolyItem(performer.getDeity())) {
                                        if (Methods.isActionAllowed(performer, (short)245)) {
                                            done = Methods.castSpell(performer, spell, target, counter);
                                        }
                                    }
                                    else {
                                        performer.getCommunicator().sendNormalServerMessage(performer.getDeity().name + " will not let you use that item.");
                                    }
                                }
                                else {
                                    performer.getCommunicator().sendNormalServerMessage("You have no deity and cannot cast the spell.");
                                }
                            }
                            else if (Methods.isActionAllowed(performer, (short)547)) {
                                done = Methods.castSpell(performer, spell, target, counter);
                            }
                        }
                        else {
                            ItemBehaviour.logger.log(Level.INFO, performer.getName() + " tries to cast unknown spell:" + Actions.actionEntrys[action].getActionString());
                            performer.getCommunicator().sendNormalServerMessage("That spell is unknown.");
                        }
                    }
                    else if (Features.Feature.MOVE_BULK_TO_BULK.isEnabled() && action == 914) {
                        done = moveBulkItemAsAction(act, performer, source, target, counter);
                    }
                    else if (action == 936 && target.isFish() && (stid == 258 || stid == 93 || stid == 126)) {
                        done = makeBait(act, performer, source, target, action, counter);
                    }
                    else {
                        done = this.action(act, performer, target, action, counter);
                    }
                }
            }
        }
        else {
            done = this.action(act, performer, target, action, counter);
        }
        return done;
    }
    
    private static List<ActionEntry> addCreationEntrys(final List<ActionEntry> toReturn, final Creature performer, final Item source, final Item target) {
        final Recipe recipe = Recipes.getRecipeFor(performer.getWurmId(), (byte)2, source, target, true, false);
        if (recipe != null) {
            Item realSource = source;
            Item realTarget = target;
            if (recipe.hasActiveItem() && source != null && recipe.getActiveItem().getTemplateId() != realSource.getTemplateId() && recipe.getActiveItem().getTemplateId() != 14) {
                realSource = target;
                realTarget = source;
            }
            toReturn.add(new ActionEntry((short)(-1), "Create", "creating"));
            if (recipe.getResultItem().isDrinkable()) {
                toReturn.add(new ActionEntry((short)(-1), "Drink", "creating"));
            }
            else if (recipe.getResultItem().isLiquid()) {
                toReturn.add(new ActionEntry((short)(-1), "Liquid", "creating"));
            }
            else {
                toReturn.add(new ActionEntry((short)(-1), "Food", "creating"));
            }
            final int chance = (int)recipe.getChanceFor(realSource, realTarget, performer);
            String dif = " (dif:" + recipe.getDifficulty(realTarget) + ")";
            if (performer.getPower() == 5) {
                dif = dif + " [" + recipe.getRecipeId() + "]";
            }
            toReturn.add(new ActionEntry(recipe.getMenuId(), recipe.getSubMenuName(realTarget) + ": " + chance + "%" + dif, "creating"));
        }
        final CreationEntry[] options = CreationMatrix.getInstance().getCreationOptionsFor(source, target);
        if (options.length > 0) {
            final Map<String, Map<CreationEntry, Integer>> map = generateMapfromOptions(performer, source, target, options);
            String key = "Miscellaneous";
            if (!map.isEmpty()) {
                final int sz = -map.size();
                toReturn.add(new ActionEntry((short)sz, "Create", "creating"));
                final Iterator<String> it = map.keySet().iterator();
                while (it.hasNext()) {
                    key = it.next();
                    final Map<CreationEntry, Integer> map2 = map.get(key);
                    toReturn.add(new ActionEntry((short)(-map2.size()), key, "creating " + key));
                    for (final CreationEntry entry : map2.keySet()) {
                        try {
                            final int difficulty = map2.get(entry);
                            final ItemTemplate template = ItemTemplateFactory.getInstance().getTemplate(entry.getObjectCreated());
                            toReturn.add(new ActionEntry((short)(10000 + entry.getObjectCreated()), template.sizeString + template.getName() + ": " + difficulty + "%", template.getName(), ItemBehaviour.emptyIntArr));
                        }
                        catch (NoSuchTemplateException nst) {
                            ItemBehaviour.logger.log(Level.WARNING, nst.getMessage(), nst);
                        }
                    }
                }
            }
        }
        return toReturn;
    }
    
    public static Map<String, Map<CreationEntry, Integer>> generateMapfromOptions(final Creature performer, final Item source, final Item target, final CreationEntry[] options) {
        final Map<String, Map<CreationEntry, Integer>> map = new HashMap<String, Map<CreationEntry, Integer>>();
        String key = "Miscellaneous";
        boolean add = true;
        for (final CreationEntry lOption : options) {
            if (lOption.meetsCreatureRestriction(source, target)) {
                add = true;
                try {
                    int chance = (int)lOption.getDifficultyFor(source, target, performer);
                    if ((chance <= 5 && !lOption.hasCustomCreationChanceCutOff()) || (lOption.hasCustomCreationChanceCutOff() && chance < lOption.getCustomCutOffChance())) {
                        chance = 0;
                    }
                    final ItemTemplate template = ItemTemplateFactory.getInstance().getTemplate(lOption.getObjectCreated());
                    key = "Miscellaneous";
                    if (template.isEpicTargetItem()) {
                        key = "Epic";
                        final MissionTrigger[] mtr = MissionTriggers.getMissionTriggersWith(lOption.getObjectCreated(), 148, -10L);
                        add = (mtr.length > 0 || !Servers.localServer.PVPSERVER);
                    }
                    else {
                        key = lOption.getCategory().getCategoryName();
                    }
                    if (chance == 0) {
                        key = "Unavailable";
                    }
                    if (add) {
                        Map<CreationEntry, Integer> map2 = map.get(key);
                        if (map2 == null) {
                            map2 = new HashMap<CreationEntry, Integer>();
                        }
                        map2.put(lOption, chance);
                        map.put(key, map2);
                    }
                }
                catch (NoSuchTemplateException nst) {
                    ItemBehaviour.logger.log(Level.WARNING, nst.getMessage(), nst);
                }
            }
        }
        return map;
    }
    
    private void addBedOptions(final Creature performer, final Item target, final List<ActionEntry> toReturn) {
        final VolaTile t = Zones.getTileOrNull(target.getTileX(), target.getTileY(), target.isOnSurface());
        if (t != null && t.getStructure() != null && t.getStructure().isTypeHouse()) {
            if (target.getData() > 0L && target.getWhenRented() < System.currentTimeMillis() - 86400000L) {
                target.setData(0L);
                target.setWhenRented(0L);
            }
            if (t.getStructure().isOwner(performer)) {
                toReturn.add(new ActionEntry((short)(-9), "Bed", "Bops"));
                toReturn.add(Actions.actionEntrys[325]);
                toReturn.add(Actions.actionEntrys[365]);
                toReturn.add(Actions.actionEntrys[366]);
                toReturn.add(Actions.actionEntrys[367]);
                toReturn.add(Actions.actionEntrys[319]);
                toReturn.add(Actions.actionEntrys[320]);
                toReturn.add(Actions.actionEntrys[321]);
                toReturn.add(Actions.actionEntrys[322]);
                toReturn.add(Actions.actionEntrys[323]);
            }
            else if (target.mayUseBed(performer)) {
                if (target.mayFreeSleep(performer) || target.getRentCost() == 0 || target.getData() == performer.getWurmId()) {
                    toReturn.add(Actions.actionEntrys[325]);
                }
                else {
                    try {
                        toReturn.add(new ActionEntry((short)324, "Hire for " + Economy.getEconomy().getChangeFor(target.getRentCost()).getChangeString(), "Bops"));
                    }
                    catch (Exception ex) {
                        ItemBehaviour.logger.log(Level.WARNING, ex.getMessage(), ex);
                    }
                }
            }
        }
    }
    
    private static void lightItem(final Item target, final Item tool, final Creature performer) {
        boolean validTool = false;
        if (target.getTemplateId() == 742) {
            validTool = true;
        }
        else if (tool != null && (tool.getTemplateId() == 143 || ((tool.getTemplateId() == 176 || target.getTemplateId() == 315) && performer.getPower() >= 2))) {
            validTool = true;
        }
        else if (tool != null && tool.isBurnable() && tool.getTemperature() > 1000 && !tool.isIndestructible() && !tool.deleted) {
            validTool = true;
        }
        if (target.isLight() || target.isFire() || target.getTemplateId() == 742) {
            if (!validTool) {
                return;
            }
            if (!target.isOnFire()) {
                if (target.getTemplate().isTransportable() && target.getTopParent() != target.getWurmId()) {
                    final String message = StringUtil.format("The %s must be on the ground before you can light it.", target.getName());
                    performer.getCommunicator().sendNormalServerMessage(message);
                    return;
                }
                if (target.isOilConsuming() && target.getAuxData() <= 0) {
                    performer.getCommunicator().sendNormalServerMessage("The " + target.getName() + " contains no oil.");
                    return;
                }
                if (target.isCandleHolder() && target.getAuxData() <= 0) {
                    if (target.getTemplateId() == 729) {
                        performer.getCommunicator().sendNormalServerMessage("The " + target.getName() + " is not a birthday cake.");
                    }
                    else {
                        performer.getCommunicator().sendNormalServerMessage("The " + target.getName() + " contains only candle stumps. Fill with new ones.");
                    }
                    return;
                }
                if ((target.isStreetLamp() || target.getTemplateId() == 1396) && !target.isPlanted()) {
                    performer.getCommunicator().sendNormalServerMessage("You need to plant the " + target.getName() + " first.");
                }
                else if (tool != null && tool.isBurnable() && tool.getTemperature() > 1000 && !tool.isIndestructible()) {
                    performer.getCommunicator().sendNormalServerMessage("You light the " + target.getName() + " using the burning remnants of " + tool.getNameWithGenus() + ".");
                    Server.getInstance().broadCastAction(performer.getName() + " lights " + target.getNameWithGenus() + " using the burning remnants of " + tool.getNameWithGenus() + ".", performer, 5);
                    target.setTemperature((short)10000);
                    Items.destroyItem(tool.getWurmId());
                }
                else {
                    performer.getCommunicator().sendNormalServerMessage("You light the " + target.getName() + ".");
                    Server.getInstance().broadCastAction(performer.getName() + " lights " + target.getNameWithGenus() + ".", performer, 5);
                    target.setTemperature((short)10000);
                }
            }
            else {
                performer.getCommunicator().sendNormalServerMessage("The " + target.getName() + " is already burning.");
            }
        }
    }
    
    public static void awardChristmasPresent(final Creature performer) {
        try {
            final Item present = ItemFactory.createItem(175, 99.0f, null);
            present.setAuxData((byte)11);
            performer.getInventory().insertItem(present, true);
            performer.setFlag(62, true);
        }
        catch (Exception ex) {
            ItemBehaviour.logger.log(Level.WARNING, performer.getName() + " no gift received: " + ex.getMessage(), ex);
        }
    }
    
    private static boolean doesRingMarkersExist(final int ringx, final int ringy) {
        final int xa = Zones.safeTileX(ringx - 20);
        final int xe = Zones.safeTileX(ringx + 20);
        final int ya = Zones.safeTileY(ringy - 20);
        final int ye = Zones.safeTileY(ringy + 20);
        for (int x = xa; x <= xe; ++x) {
            for (int y = ya; y <= ye; ++y) {
                try {
                    final Zone zone = Zones.getZone(x, y, true);
                    final VolaTile vtile = zone.getTileOrNull(x, y);
                    if (vtile != null) {
                        final Item[] items = vtile.getItems();
                        for (int i = 0; i < items.length; ++i) {
                            if (items[i].getTemplateId() == 727 || items[i].getTemplateId() == 728) {
                                return true;
                            }
                        }
                    }
                }
                catch (NoSuchZoneException ex) {}
            }
        }
        return false;
    }
    
    private static void createMarkers(final int tx, final int ty) {
        final int xa = Zones.safeTileX(tx - 20);
        final int xe = Zones.safeTileX(tx + 20);
        final int ya = Zones.safeTileY(ty - 20);
        final int ye = Zones.safeTileY(ty + 20);
        for (int x = xa; x <= xe; ++x) {
            for (int y = ya; y <= ye; ++y) {
                boolean create = false;
                boolean createCorner = false;
                if (x == xa) {
                    if (y == ya || y == ye) {
                        createCorner = true;
                    }
                    else if (y % 5 == 0) {
                        create = true;
                    }
                }
                else if (x == xe) {
                    if (y == ya || y == ye) {
                        createCorner = true;
                    }
                    else if (y % 5 == 0) {
                        create = true;
                    }
                }
                else if ((y == ya || y == ye) && x % 5 == 0) {
                    create = true;
                }
                if (create) {
                    try {
                        final Item i = ItemFactory.createItem(727, 80.0f + Server.rand.nextFloat() * 10.0f, null);
                        i.setPosXYZ((x << 2) + 2, (y << 2) + 2, Zones.calculateHeight((x << 2) + 2, (y << 2) + 2, true));
                        Zones.getZone(x, y, true).addItem(i);
                    }
                    catch (Exception ex) {
                        ItemBehaviour.logger.log(Level.INFO, ex.getMessage());
                    }
                }
                else if (createCorner) {
                    try {
                        final Item i = ItemFactory.createItem(728, 80.0f + Server.rand.nextFloat() * 10.0f, null);
                        i.setPosXYZ((x << 2) + 2, (y << 2) + 2, Zones.calculateHeight((x << 2) + 2, (y << 2) + 2, true));
                        Zones.getZone(x, y, true).addItem(i);
                    }
                    catch (Exception ex) {
                        ItemBehaviour.logger.log(Level.INFO, ex.getMessage());
                    }
                }
            }
        }
    }
    
    private List<ActionEntry> makeMoveSubMenu(final Creature performer, final Item target) {
        final List<ActionEntry> menulist = new LinkedList<ActionEntry>();
        if (target.isTurnable(performer) && (!target.isRoadMarker() || !target.isPlanted())) {
            menulist.add(Actions.actionEntrys[177]);
            menulist.add(Actions.actionEntrys[178]);
        }
        if (target.isMoveable(performer) && (!target.isRoadMarker() || !target.isPlanted())) {
            menulist.add(Actions.actionEntrys[99]);
            menulist.add(Actions.actionEntrys[696]);
            menulist.add(Actions.actionEntrys[864]);
            menulist.add(Actions.actionEntrys[926]);
            if (target.getTemplateId() != 931 || !Servers.localServer.PVPSERVER) {
                menulist.add(Actions.actionEntrys[181]);
                menulist.add(Actions.actionEntrys[697]);
            }
        }
        if (menulist.isEmpty()) {
            return menulist;
        }
        final List<ActionEntry> submenu = new LinkedList<ActionEntry>();
        submenu.add(new ActionEntry((short)(-menulist.size()), "Move", "Move item"));
        submenu.addAll(menulist);
        return submenu;
    }
    
    public static boolean isSignManipulationOk(final Item target, final Creature performer, final short action) {
        if (target.lastOwner == performer.getWurmId()) {
            return true;
        }
        if (performer.getPower() > 0) {
            return true;
        }
        if (Players.getInstance().getKingdomForPlayer(target.getLastOwnerId()) != performer.getKingdomId() && performer.isPaying() && CargoTransportationMethods.strengthCheck(performer, 21.0)) {
            return true;
        }
        final VolaTile t = Zones.getTileOrNull(target.getTileX(), target.getTileY(), target.isOnSurface());
        if (t != null) {
            final Structure structure = t.getStructure();
            final Village village = t.getVillage();
            if (target.isPlanted()) {
                boolean ok = false;
                if (structure != null && structure.isTypeHouse()) {
                    ok = structure.isActionAllowed(performer, (short)685);
                }
                else if (village != null) {
                    ok = village.isActionAllowed((short)685, performer);
                }
                if (!ok) {
                    return false;
                }
            }
            if (Actions.isActionDestroy(action) && village != null && village.isActionAllowed(action, performer)) {
                return true;
            }
            if (structure != null && structure.isTypeHouse() && structure.isFinished()) {
                return (!Actions.isActionBuildingPermission(action) && village != null && village.isActionAllowed(action, performer)) || structure.isActionAllowed(performer, action);
            }
            if (village != null) {
                return village.isActionAllowed(action, performer);
            }
        }
        return !target.isPlanted();
    }
    
    private static boolean examine(final Action act, final Creature performer, final Item target, final short action, final float counter) {
        if (target == null) {
            ItemBehaviour.logger.log(Level.WARNING, "target was null when trying to examine it in SignBehaviour.");
            return false;
        }
        final ItemTemplate template = target.getTemplate();
        if (template == null) {
            ItemBehaviour.logger.log(Level.WARNING, "item (" + target.getWurmId() + ") did not have a template when trying to examine it in SignBehaviour.");
            return true;
        }
        final StringBuilder sendString = new StringBuilder(template.getDescriptionLong());
        if (target.getTemplateId() == 850 && target.isWagonerWagon()) {
            sendString.append(" This is used by a wagoner to transport bulk goods around the server.");
            if (target.getItemCount() > 0) {
                sendString.append(" It is currently loaded with " + target.getItemCount() + " crates.");
            }
            else {
                sendString.append(" It is currently empty.");
            }
        }
        if (target.isPlanted()) {
            final PlayerInfo pInfo = PlayerInfoFactory.getPlayerInfoWithWurmId(target.lastOwner);
            String plantedBy = "someone";
            if (pInfo != null) {
                plantedBy = pInfo.getName();
            }
            sendString.append(" The " + target.getName() + " has been firmly secured to the ground by " + plantedBy + ".");
        }
        final String s = target.getSignature();
        if (s != null && !s.isEmpty() && !target.isDish()) {
            sendString.append(" You can barely make out the signature of its maker,  '" + s + "'.");
        }
        if (target.getRarity() > 0) {
            sendString.append(MethodsItems.getRarityDesc(target.rarity));
        }
        if (target.getColor() != -1 && (!target.isDragonArmour() || target.getColor2() == -1)) {
            sendString.append(" ");
            if (target.isWood()) {
                sendString.append("Wood ");
                sendString.append(MethodsItems.getColorDesc(target.getColor()).toLowerCase());
            }
            else {
                sendString.append(MethodsItems.getColorDesc(target.getColor()));
            }
        }
        if (target.supportsSecondryColor() && target.getColor2() != -1) {
            sendString.append(" ");
            if (target.isDragonArmour()) {
                sendString.append(MethodsItems.getColorDesc(target.getColor2()));
            }
            else {
                sendString.append(LoginHandler.raiseFirstLetter(target.getSecondryItemName()));
                sendString.append(MethodsItems.getColorDesc(target.getColor2()).toLowerCase());
            }
        }
        if (target.getLockId() != -10L) {
            try {
                final Item lock = Items.getItem(target.getLockId());
                sendString.append(" It is locked with a lock of " + lock.getLockStrength() + " quality.");
            }
            catch (NoSuchItemException nsi) {
                ItemBehaviour.logger.log(Level.WARNING, target.getWurmId() + " has a lock that can't be found: " + target.getLockId(), nsi);
            }
        }
        sendString.append(" Ql: " + target.getQualityLevel() + ", Dam: " + target.getDamage() + ".");
        if (target.getBless() != null && performer.getFaith() > 20.0f) {
            if (performer.getFaith() < 30.0f) {
                sendString.append(" It has an interesting aura.");
            }
            else if (performer.getFaith() < 40.0f) {
                if (target.getBless().isHateGod()) {
                    sendString.append(" It has a malevolent aura.");
                }
                else {
                    sendString.append(" It has a benevolent aura.");
                }
            }
            else {
                sendString.append(" It bears an aura of " + target.getBless().name + ".");
            }
        }
        if (target.getTemplateId() == 1112 && target.isWagonerCamp()) {
            final Wagoner wagoner = Wagoner.getWagoner(target.getData());
            if (wagoner != null) {
                sendString.append(" This is the center of the " + wagoner.getName() + "'s camp.");
            }
        }
        if (target.isOwnedByWagoner()) {
            final Wagoner wagoner = Wagoner.getWagoner(target.getLastOwnerId());
            if (wagoner != null) {
                sendString.append(" This is owned by " + wagoner.getName() + ".");
            }
        }
        if (target.isCrate() && target.isSealedByPlayer() && target.getData() > -1L) {
            final Delivery delivery = Delivery.getDelivery(target.getData());
            if (delivery != null) {
                final Wagoner wagoner2 = Wagoner.getWagoner(delivery.getWagonerId());
                final String appliedBy = (wagoner2 == null) ? "" : (" applied by " + wagoner2.getName() + ",");
                sendString.append(" It has a security seal," + appliedBy + " and was sent from " + delivery.getSenderName() + " to " + delivery.getReceiverName() + ".");
            }
            else {
                target.setData(-10L);
            }
        }
        if (target.getTemplateId() == 1309 && target.isSealedByPlayer()) {
            final Delivery delivery = Delivery.getDeliveryFrom(target.getWurmId());
            if (delivery != null) {
                final Wagoner wagoner2 = Wagoner.getWagoner(delivery.getWagonerId());
                final String appliedBy = (wagoner2 == null) ? "" : (" applied by " + wagoner2.getName() + ",");
                sendString.append(" It has a security seal," + appliedBy + " and is the collection container used for a delivery from " + delivery.getSenderName() + " to " + delivery.getReceiverName() + ".");
            }
        }
        performer.getCommunicator().sendNormalServerMessage(sendString.toString());
        target.sendEnchantmentStrings(performer.getCommunicator());
        target.sendExtraStrings(performer.getCommunicator());
        final String improvedBy = MethodsItems.getImpDesc(performer, target);
        if (!improvedBy.isEmpty()) {
            performer.getCommunicator().sendNormalServerMessage(improvedBy);
        }
        if (target.getTemplate().isRune()) {
            String runeDesc = "";
            if (RuneUtilities.isEnchantRune(target)) {
                runeDesc = runeDesc + "It can be attached to " + RuneUtilities.getAttachmentTargets(target) + " and will " + RuneUtilities.getRuneLongDesc(RuneUtilities.getEnchantForRune(target)) + ".";
            }
            else if (RuneUtilities.getModifier(RuneUtilities.getEnchantForRune(target), RuneUtilities.ModifierEffect.SINGLE_COLOR) > 0.0f || (RuneUtilities.getSpellForRune(target) != null && RuneUtilities.getSpellForRune(target).isTargetAnyItem() && !RuneUtilities.getSpellForRune(target).isTargetTile())) {
                runeDesc = runeDesc + "It can be used on " + RuneUtilities.getAttachmentTargets(target) + " and will " + RuneUtilities.getRuneLongDesc(RuneUtilities.getEnchantForRune(target)) + ".";
            }
            else {
                runeDesc = runeDesc + "It will " + RuneUtilities.getRuneLongDesc(RuneUtilities.getEnchantForRune(target)) + ".";
            }
            performer.getCommunicator().sendNormalServerMessage(runeDesc);
        }
        if (target.getTemplateId() == 1423 && target.getData() != -1L && target.getAuxData() != 0) {
            final DeadVillage dv = Villages.getDeadVillage(target.getData());
            String toReturn = dv.getDeedName();
            if (target.getAuxBit(1)) {
                toReturn = toReturn + " was founded by " + dv.getFounderName();
                if (target.getAuxBit(3)) {
                    toReturn = toReturn + " and was inhabited for about " + DeadVillage.getTimeString(dv.getTotalAge(), false) + ".";
                }
                else {
                    toReturn += ".";
                }
            }
            else if (target.getAuxBit(3)) {
                toReturn = toReturn + " was inhabited for about " + DeadVillage.getTimeString(dv.getTotalAge(), false) + ".";
            }
            if (target.getAuxBit(2)) {
                if (target.getAuxBit(1) || target.getAuxBit(3)) {
                    toReturn += " It";
                }
                toReturn = toReturn + " has been abandoned for roughly " + DeadVillage.getTimeString(dv.getTimeSinceDisband(), false);
                if (target.getAuxBit(0)) {
                    toReturn = toReturn + " and was last mayored by " + dv.getMayorName() + ".";
                }
                else {
                    toReturn += ".";
                }
            }
            else {
                if (target.getAuxBit(1) || target.getAuxBit(3)) {
                    toReturn += " It";
                }
                toReturn = toReturn + " was last mayored by " + dv.getMayorName() + ".";
            }
            performer.getCommunicator().sendNormalServerMessage(toReturn);
        }
        return true;
    }
    
    private static byte getTileTransmutationLiquidAuxData(final Item source, final Item target) {
        final Item[] contents = target.getAllItems(false);
        boolean allSame = false;
        if (contents.length >= 1) {
            allSame = true;
            final int lookingFor = contents[0].getTemplateId();
            for (final Item i : contents) {
                if (i.getTemplateId() != lookingFor) {
                    allSame = false;
                    break;
                }
            }
            if (allSame) {
                return MethodsItems.getTransmutationLiquidAuxByteFor(source, contents[0]);
            }
        }
        return 0;
    }
    
    public final int getRandomStatueFragment() {
        final int randomFragment = Server.rand.nextInt(8);
        switch (randomFragment) {
            case 0: {
                return 1329;
            }
            case 1: {
                return 1328;
            }
            case 2: {
                return 1327;
            }
            case 3: {
                return 1326;
            }
            case 4: {
                return 1325;
            }
            case 5: {
                return 1330;
            }
            case 6: {
                return 1323;
            }
            default: {
                return 1324;
            }
        }
    }
    
    public final int getRandomGem(final boolean starGemsPossible) {
        final int randomGem = Server.rand.nextInt(5);
        final boolean giveStarGem = starGemsPossible && Server.rand.nextFloat() * 100.0f < 1.0f;
        switch (randomGem) {
            case 0: {
                if (giveStarGem) {
                    return 375;
                }
                return 374;
            }
            case 1: {
                if (giveStarGem) {
                    return 381;
                }
                return 380;
            }
            case 2: {
                if (giveStarGem) {
                    return 379;
                }
                return 378;
            }
            case 3: {
                if (giveStarGem) {
                    return 377;
                }
                return 376;
            }
            case 4: {
                if (giveStarGem) {
                    return 383;
                }
                return 382;
            }
            default: {
                return 374;
            }
        }
    }
    
    public final int getRandomItemFromPack(final int packType) {
        int templateId = -1;
        switch (packType) {
            case 1097: {
                final float rand = Server.rand.nextFloat() * 100.0f;
                float chance = 1.0f;
                if (rand <= chance) {
                    switch (Server.rand.nextInt(5)) {
                        case 0: {
                            templateId = 375;
                            break;
                        }
                        case 1: {
                            templateId = 381;
                            break;
                        }
                        case 2: {
                            templateId = 379;
                            break;
                        }
                        case 3: {
                            templateId = 377;
                            break;
                        }
                        case 4: {
                            templateId = 383;
                            break;
                        }
                    }
                    return templateId;
                }
                chance += 10.0f;
                if (rand <= chance) {
                    return 837;
                }
                chance += 10.0f;
                if (rand <= chance) {
                    return 694;
                }
                chance += 10.0f;
                if (rand <= chance) {
                    return 698;
                }
                chance += 2.0f;
                if (rand <= chance) {
                    return Item.getRandomImbuePotionTemplateId();
                }
                chance += 0.1f;
                if (rand <= chance) {
                    return 867;
                }
                chance += 0.05f;
                if (rand <= chance) {
                    return 668;
                }
                chance += 0.01f;
                if (rand <= chance) {
                    return 986;
                }
                chance += 0.01f;
                if (rand <= chance) {
                    return 975;
                }
                chance += 0.025f;
                if (rand <= chance) {
                    return 980;
                }
                chance += 0.1f;
                if (rand <= chance) {
                    return 998;
                }
                chance += 0.1f;
                if (rand <= chance) {
                    return 977;
                }
                chance += 0.5f;
                if (rand <= chance) {
                    switch (Server.rand.nextInt(4)) {
                        case 0: {
                            return 983;
                        }
                        case 1: {
                            return 981;
                        }
                        case 2: {
                            return 984;
                        }
                        case 3: {
                            return 982;
                        }
                    }
                }
                ++chance;
                if (rand <= chance) {
                    return 666;
                }
                return 666;
            }
            case 1098: {
                switch (Server.rand.nextInt(8)) {
                    case 0: {
                        templateId = 27;
                        break;
                    }
                    case 1: {
                        templateId = 7;
                        break;
                    }
                    case 2: {
                        templateId = 62;
                        break;
                    }
                    case 3: {
                        templateId = 392;
                        break;
                    }
                    case 4: {
                        templateId = 24;
                        break;
                    }
                    case 5: {
                        templateId = 25;
                        break;
                    }
                    case 6: {
                        templateId = 20;
                        break;
                    }
                    case 7: {
                        templateId = 388;
                        break;
                    }
                }
                break;
            }
        }
        return templateId;
    }
    
    private static boolean handleRecipe(final Action act, final Creature performer, @Nullable final Item source, final Item target, final short action, final float counter, final Recipe recipe) {
        if (performer.getVehicle() != -10L) {
            performer.getCommunicator().sendNormalServerMessage("You need to be on solid ground to do that.");
            return true;
        }
        final int swapped = Recipes.isRecipeOk(performer.getWurmId(), recipe, source, target, true, true);
        if (swapped == 0) {
            performer.getCommunicator().sendNormalServerMessage("Insufficient materials to make " + recipe.getRecipeName() + ".");
            return true;
        }
        final Item realSource = (swapped != 2) ? (recipe.hasActiveItem() ? source : null) : target;
        final Item realTarget = (swapped != 2) ? target : source;
        final int chance = (int)recipe.getChanceFor(realSource, realTarget, performer);
        if (chance <= 5) {
            performer.getCommunicator().sendNormalServerMessage("This is impossible, perhaps you are not skilled enough.");
            return true;
        }
        if (!performer.getInventory().mayCreatureInsertItem()) {
            performer.getCommunicator().sendNormalServerMessage("Your inventory is full.");
            return true;
        }
        if (realSource != null && realSource.isCookingTool() && realTarget.isMilk() && realTarget.getWeightGrams() < 600) {
            performer.getCommunicator().sendNormalServerMessage("The " + realTarget.getNameWithGenus() + " contains too little material to create " + recipe.getResultNameWithGenus(realTarget) + ".  Try to combine it with a similar object to get a larger amount.");
            return true;
        }
        if (recipe.getResultItem().isLiquid() && !realTarget.isContainerLiquid() && !realSource.isContainerLiquid()) {
            final Item parent = realTarget.getParentOrNull();
            if (parent == null || !parent.isContainerLiquid()) {
                performer.getCommunicator().sendNormalServerMessage("The " + realTarget.getName() + " needs to be in a container that can hold liquids.");
                return true;
            }
        }
        if (realSource != null && (realSource.getTemplateId() == 169 || realSource.getTemplateId() == 1104) && realSource.getTopParent() != performer.getInventory().getWurmId()) {
            performer.getCommunicator().sendNormalServerMessage("You need to be carrying the " + realSource.getName() + " to use it.");
            return true;
        }
        final boolean noRarity = recipe.getResultItem().isLiquid();
        final Skills skills = performer.getSkills();
        final Skill primSkill = skills.getSkillOrLearn(recipe.getSkillId());
        final ItemTemplate template = recipe.getResultTemplate(realTarget);
        int time = 10;
        if (counter == 1.0f) {
            final int start = 150;
            realTarget.setBusy(true);
            if (realSource != null) {
                realSource.setBusy(true);
            }
            try {
                time = Actions.getRecipeCreationTime(150, performer, primSkill, recipe, realSource, realTarget, template.isMassProduction());
            }
            catch (NoSuchTemplateException nst) {
                if (realSource != null) {
                    ItemBehaviour.logger.log(Level.WARNING, "No template when creating with " + realSource.getName() + " and " + realTarget.getName() + "." + nst.getMessage(), nst);
                }
                else {
                    ItemBehaviour.logger.log(Level.WARNING, "No template when creating " + realTarget.getName() + "." + nst.getMessage(), nst);
                }
                performer.getCommunicator().sendSafeServerMessage("You cannot create that item right now. Please contact administrators.");
                return true;
            }
            act.setTimeLeft(time);
            performer.sendActionControl(Actions.actionEntrys[148].getVerbString() + " " + template.getName(), true, time);
            if (realSource != null) {
                performer.getCommunicator().sendNormalServerMessage("You start to work with the " + realSource.getName() + " on the " + realTarget.getName() + ".");
                Server.getInstance().broadCastAction(performer.getName() + " starts working with the " + realSource.getName() + " on the " + realTarget.getName() + ".", performer, 5);
                playToolSound(performer, realSource);
            }
            else {
                performer.getCommunicator().sendNormalServerMessage("You start to work on the " + realTarget.getName() + ".");
                Server.getInstance().broadCastAction(performer.getName() + " starts working on the " + realTarget.getName() + ".", performer, 5);
            }
            performer.getStatus().modifyStamina(-400.0f);
            if (realSource.isCookingTool()) {
                realSource.setDamage(realSource.getDamage() + 0.0025f * realSource.getDamageModifier());
            }
            return false;
        }
        if (act.currentSecond() % 5 == 0) {
            performer.getStatus().modifyStamina(-400.0f);
            if (realSource != null) {
                playToolSound(performer, realSource);
                if (realSource.isCookingTool()) {
                    realSource.setDamage(realSource.getDamage() + 0.0025f * realSource.getDamageModifier());
                }
            }
        }
        time = act.getTimeLeft();
        if (counter * 10.0f > time) {
            if (act.getRarity() != 0 && !noRarity) {
                performer.playPersonalSound("sound.fx.drumroll");
            }
            final double bonus = performer.getVillageSkillModifier();
            final float skillMultiplier = Math.min(Math.max(1.0f, counter / 3.0f), 20.0f);
            performer.sendToLoggers("Skill multiplier=" + skillMultiplier, (byte)3);
            float power = 1.0f;
            float alc = 0.0f;
            if (performer.isPlayer()) {
                alc = ((Player)performer).getAlcohol();
            }
            final int diff = recipe.getDifficulty(realTarget);
            if (realSource == null || realSource.isBodyPart()) {
                power = (float)primSkill.skillCheck(diff + alc, null, bonus, false, skillMultiplier);
            }
            else {
                power = (float)primSkill.skillCheck(diff + alc, realSource, bonus, false, skillMultiplier);
            }
            boolean chefMade = false;
            if (performer.isRoyalChef()) {
                chefMade = true;
            }
            final byte material = recipe.getResultMaterial(realTarget);
            Item newItem = null;
            try {
                final double avgQL = MethodsItems.getAverageQL(realSource, realTarget);
                double ql = Math.min(100.0, Math.max(1.0, avgQL + power / 10.0f));
                if (chefMade) {
                    ql = Math.max(30.0, ql);
                }
                float maxMod = 1.0f;
                if (template.isLowNutrition()) {
                    maxMod = 4.0f;
                }
                else if (template.isMediumNutrition()) {
                    maxMod = 3.0f;
                }
                else if (template.isGoodNutrition()) {
                    maxMod = 2.0f;
                }
                else if (template.isHighNutrition()) {
                    maxMod = 1.0f;
                }
                ql = Math.max(1.0, Math.min(primSkill.getKnowledge(0.0) * maxMod, ql));
                final boolean showOwner = primSkill.getKnowledge(0.0) > 70.0;
                final byte sourceRarity = (byte)((realSource == null) ? 0 : realSource.getRarity());
                if (act.getRarity() > 0 || sourceRarity > 0 || realTarget.getRarity() > 0) {
                    ql = GeneralUtilities.calcRareQuality(ql, act.getRarity(), sourceRarity, realTarget.getRarity());
                }
                final String owner = showOwner ? performer.getName() : null;
                newItem = ItemFactory.createItem(template.getTemplateId(), (float)ql, material, (byte)(noRarity ? 0 : act.getRarity()), owner);
                newItem.setIsSalted(getSalted(realSource, realTarget));
                int createdCount = 1;
                int newWeight = template.getWeightGrams();
                if (!recipe.useResultTemplateWeight()) {
                    if (realTarget.isFoodMaker()) {
                        newWeight = 0;
                        int liquid = 0;
                        for (final Item item : realTarget.getItemsAsArray()) {
                            if (item.isLiquid()) {
                                final Ingredient ii = recipe.findMatchingIngredient(item);
                                if (ii != null) {
                                    liquid += (int)(item.getWeightGrams() * ((100 - ii.getLoss()) / 100.0f));
                                }
                            }
                            else {
                                newWeight += item.getWeightGrams();
                            }
                        }
                        newWeight += liquid;
                        newItem.setWeight(newWeight, true);
                    }
                    else if (realSource == null || !realSource.isCookingTool() || !recipe.useResultTemplateWeight() || template.isLiquid()) {
                        if (realSource == null || realSource.getTemplateId() != 202 || realTarget.getTemplateId() != 1238) {
                            if (realSource != null && realSource.isCookingTool()) {
                                newWeight = (int)(realTarget.getWeightGrams() * ((100 - recipe.getTargetItem().getLoss()) / 100.0f));
                                newItem.setWeight(newWeight, true);
                            }
                            else if (realSource != null && realSource.getTemplateId() == 688 && realTarget.isCorpse()) {
                                newWeight = (int)(Math.sqrt((realSource.getWeightGrams() + realTarget.getWeightGrams()) / 1000) * 1000.0);
                                newItem.setWeight(newWeight, true);
                            }
                            else {
                                newWeight = realTarget.getWeightGrams();
                                if (realSource != null) {
                                    newWeight += recipe.getUsedActiveItemWeightGrams(realSource, realTarget);
                                }
                                newItem.setWeight(newWeight, true);
                            }
                        }
                    }
                }
                if (newWeight < 0) {
                    performer.getCommunicator().sendNormalServerMessage("Not enough of " + realTarget.getName() + " to make " + newItem.getName() + ".");
                    return true;
                }
                if (template.getWeightGrams() != newWeight) {
                    MethodsItems.setSizes(realTarget, newWeight, newItem);
                }
                if (RecipesByPlayer.saveRecipe(performer, recipe, performer.getWurmId(), realSource, realTarget)) {
                    performer.getCommunicator().sendServerMessage("Recipe \"" + recipe.getName() + "\" added to your cookbook.", 216, 165, 32, (byte)2);
                }
                newItem.calculateAndSaveNutrition(realSource, realTarget, recipe);
                recipe.addAchievements(performer, newItem);
                if (newItem.getRarity() > 2) {
                    performer.achievement(300);
                }
                else if (newItem.getRarity() > 1) {
                    performer.achievement(302);
                }
                else if (newItem.getRarity() > 0) {
                    performer.achievement(301);
                }
                newItem.setName(recipe.getResultName(realTarget));
                final ItemTemplate rit = recipe.getResultRealTemplate(realTarget);
                if (rit != null) {
                    newItem.setRealTemplate(rit.getTemplateId());
                }
                if (recipe.hasResultState()) {
                    newItem.setAuxData(recipe.getResultState());
                }
                final String newNameWithGenus = recipe.getResultNameWithGenus(realTarget);
                final String newName = recipe.getResultName(realTarget);
                if (newItem.isLiquid()) {
                    if (realSource != null && realTarget.getTemplateId() == 768 && (realSource.getTemplateId() == 169 || realSource.getTemplateId() == 1104)) {
                        realTarget.closeAll();
                        for (final Item item2 : realTarget.getItemsAsArray()) {
                            Items.destroyItem(item2.getWurmId());
                        }
                        MethodsItems.fillContainer(act, realTarget, newItem, performer, true);
                        if (!newItem.deleted && newItem.getParentId() == -10L) {
                            performer.getCommunicator().sendNormalServerMessage("Not all the " + newItem.getName() + " would fit in the " + realTarget.getName() + ".");
                            Items.decay(newItem.getWurmId(), newItem.getDbStrings());
                            newItem = null;
                        }
                        realSource.setAuxData((byte)primSkill.getKnowledge(0.0));
                        realTarget.insertItem(realSource, true);
                        realTarget.setIsSealedByPlayer(true);
                    }
                    else {
                        Item parent2 = realTarget;
                        if (realSource.isContainerLiquid()) {
                            parent2 = realSource;
                        }
                        if (!parent2.isContainerLiquid()) {
                            parent2 = parent2.getParentOrNull();
                        }
                        if (parent2 != null) {
                            if (realTarget.isFoodMaker()) {
                                for (final Item item3 : realTarget.getItemsAsArray()) {
                                    Items.destroyItem(item3.getWurmId());
                                }
                            }
                            else {
                                if (realSource != null && !realSource.isCookingTool() && !realSource.isRecipeItem()) {
                                    final int sWeight = recipe.getUsedActiveItemWeightGrams(realSource, realTarget);
                                    realSource.setWeight(realSource.getWeightGrams() - sWeight, true);
                                }
                                Items.destroyItem(realTarget.getWurmId());
                            }
                            MethodsItems.fillContainer(act, parent2, newItem, performer, true);
                            if (!newItem.deleted && newItem.getParentId() == -10L) {
                                performer.getCommunicator().sendNormalServerMessage("Not all the " + newItem.getName() + " would fit in the " + parent2.getName() + ".");
                                Items.decay(newItem.getWurmId(), newItem.getDbStrings());
                                newItem = null;
                            }
                        }
                        else {
                            performer.getInventory().insertItem(newItem, true);
                        }
                    }
                }
                else if (realTarget.isFoodMaker()) {
                    for (final Item item2 : realTarget.getItemsAsArray()) {
                        Items.destroyItem(item2.getWurmId());
                    }
                    newItem.setLastOwnerId(performer.getWurmId());
                    realTarget.insertItem(newItem);
                }
                else if (realSource != null && realSource.isCookingTool() && recipe.useResultTemplateWeight() && !template.isLiquid()) {
                    realTarget.setWeight(realTarget.getWeightGrams() - recipe.getTargetLossWeight(realTarget), true);
                    performer.getInventory().insertItem(newItem, true);
                }
                else if (realSource != null && realSource.isLiquid()) {
                    realSource.setWeight(realSource.getWeightGrams() - realTarget.getWeightGrams(), true);
                    Items.destroyItem(realTarget.getWurmId());
                    performer.getInventory().insertItem(newItem, true);
                }
                else {
                    Item c = realTarget.getParentOrNull();
                    if (c != null) {
                        Items.destroyItem(realTarget.getWurmId());
                        if (!c.insertItem(newItem)) {
                            c = c.getParentOrNull();
                            if (c == null || !c.insertItem(newItem)) {
                                c = performer.getInventory();
                                c.insertItem(newItem, true);
                            }
                        }
                        if (realSource != null && realSource.getTemplateId() == 688) {
                            Items.destroyItem(realSource.getWurmId());
                        }
                        if (realTarget.getTemplateId() == 1238 && newItem.getTemplateId() == 349) {
                            for (int x = 20; x < 100; x += 30) {
                                power = (float)primSkill.skillCheck(diff + alc, realSource, bonus, false, skillMultiplier);
                                ql = Math.min(99.0f, Math.max(1.0f, realTarget.getCurrentQualityLevel() + power / 2.0f));
                                ql = Math.max(1.0, Math.min(ql, realTarget.getCurrentQualityLevel()));
                                if (act.getRarity() > 0 || sourceRarity > 0 || realTarget.getRarity() > 0) {
                                    ql = GeneralUtilities.calcRareQuality(ql, act.getRarity(), sourceRarity, realTarget.getRarity());
                                }
                                if (performer.getInventory().getNumItemsNotCoins() >= 100) {
                                    performer.getCommunicator().sendNormalServerMessage("You do not have space for any more salt.");
                                    return true;
                                }
                                newItem = ItemFactory.createItem(template.getTemplateId(), (float)ql, material, act.getRarity(), performer.getName());
                                ++createdCount;
                                c.insertItem(newItem);
                                final int skillLevel = (int)primSkill.getKnowledge(0.0) - x;
                                if (Server.rand.nextInt(100) > skillLevel) {
                                    break;
                                }
                            }
                        }
                    }
                    else {
                        performer.getInventory().insertItem(newItem, true);
                    }
                }
                if (recipe.hasActiveItem() && recipe.getActiveItem().getTemplateId() != 14 && realSource != null && !realSource.isLiquid() && !realSource.isCookingTool() && !realSource.isRecipeItem()) {
                    realSource.setWeight(realSource.getWeightGrams() - realSource.getTemplate().getWeightGrams(), true);
                }
                if (createdCount > 1) {
                    performer.getCommunicator().sendNormalServerMessage("You created " + createdCount + " " + newName + ".");
                }
                else {
                    performer.getCommunicator().sendNormalServerMessage("You create " + newNameWithGenus + ".");
                }
            }
            catch (FailedException fe) {
                ItemBehaviour.logger.log(Level.WARNING, fe.getMessage(), fe);
            }
            catch (NoSuchTemplateException nste) {
                ItemBehaviour.logger.log(Level.WARNING, nste.getMessage(), nste);
            }
            return true;
        }
        return false;
    }
    
    static void playToolSound(final Creature performer, final Item source) {
        switch (source.getTemplateId()) {
            case 93: {
                SoundPlayer.playSound("sound.butcherKnife", performer, 1.0f);
                break;
            }
            case 259: {
                SoundPlayer.playSound("sound.forkMix", performer, 1.0f);
                break;
            }
            case 1237: {
                SoundPlayer.playSound("sound.grindSpice", performer, 1.0f);
                break;
            }
            case 202: {
                SoundPlayer.playSound("sound.grindstone", performer, 1.0f);
                break;
            }
            case 258: {
                SoundPlayer.playSound("sound.knifeChop", performer, 1.0f);
                break;
            }
            case 413:
            case 747: {
                SoundPlayer.playSound("sound.press", performer, 1.0f);
                break;
            }
        }
    }
    
    public static boolean getSalted(@Nullable final Item source, final Item target) {
        if (source != null && (source.isFood() || source.isLiquid()) && (source.isSalted() || source.getTemplateId() == 349)) {
            return true;
        }
        if (target.isFoodMaker()) {
            for (final Item item : target.getItemsAsArray()) {
                if (item.isSalted() || item.getTemplateId() == 349) {
                    return true;
                }
            }
        }
        else if ((target.isFood() || target.isLiquid()) && (target.isSalted() || target.getTemplateId() == 349)) {
            return true;
        }
        return false;
    }
    
    private void showRecipeInfo(final Creature performer, final Item source, final Item target) {
        if ((target.getTemplate().isCooker() || target.isFoodMaker()) && target.isEmpty(true)) {
            performer.getCommunicator().sendNormalServerMessage("This can be used for cooking many wonderful things.");
        }
        else if (target.getTemplateId() == 1178 && target.isEmpty(true)) {
            performer.getCommunicator().sendNormalServerMessage("This can be used for distilling many wonderful things.");
        }
        else {
            Item realTarget = target;
            if (target.getTemplateId() == 1178) {
                for (final Item item : target.getItemsAsArray()) {
                    if (item.getTemplateId() == 1284) {
                        realTarget = item;
                        break;
                    }
                }
            }
            boolean foundHeat = this.showRecipeInfo(performer, source, target, realTarget, (byte)1);
            boolean foundCreate = this.showRecipeInfo(performer, source, target, realTarget, (byte)2);
            boolean foundTime = this.showRecipeInfo(performer, source, target, realTarget, (byte)0);
            if (!foundHeat && !foundCreate && !foundTime) {
                ItemBehaviour.recipeRandom.setSeed(performer.getWurmId());
                if (ItemBehaviour.recipeRandom.nextBoolean()) {
                    foundHeat = this.showPartialRecipeInfo(performer, source, target, realTarget, (byte)1);
                    if (!foundHeat) {
                        foundCreate = this.showPartialRecipeInfo(performer, source, target, realTarget, (byte)2);
                    }
                }
                else {
                    foundCreate = this.showPartialRecipeInfo(performer, source, target, realTarget, (byte)2);
                    if (!foundCreate) {
                        foundHeat = this.showPartialRecipeInfo(performer, source, target, realTarget, (byte)1);
                    }
                }
                if (!foundHeat && !foundCreate) {
                    foundTime = this.showPartialRecipeInfo(performer, source, target, realTarget, (byte)0);
                }
                if (!foundHeat && !foundCreate && !foundTime) {
                    performer.getCommunicator().sendNormalServerMessage("The items inside do not make any known recipe.");
                }
            }
        }
    }
    
    private boolean showRecipeInfo(final Creature performer, @Nullable final Item source, final Item target, final Item realTarget, final byte wantedType) {
        final Recipe eRecipe = Recipes.getRecipeFor(performer.getWurmId(), wantedType, source, realTarget, false, true);
        if (eRecipe != null) {
            if (RecipesByPlayer.isKnownRecipe(performer.getWurmId(), eRecipe.getRecipeId())) {
                if (wantedType == 1) {
                    String needs = " when cooked.";
                    final Item cooker = target.getTopParentOrNull();
                    if (cooker != null && cooker.getTemplate().isCooker()) {
                        if (!eRecipe.hasCooker(cooker.getTemplateId())) {
                            needs = " when cooked in a different cooker.";
                        }
                    }
                    else {
                        needs = " when cooked in a cooker.";
                    }
                    performer.getCommunicator().sendNormalServerMessage("The ingredients in the " + target.getName() + " would make " + eRecipe.getResultNameWithGenus(realTarget) + needs);
                }
                else if (wantedType == 0) {
                    performer.getCommunicator().sendNormalServerMessage("The ingredients in the " + target.getName() + " would make " + eRecipe.getResultNameWithGenus(realTarget) + " given time.");
                }
                else {
                    performer.getCommunicator().sendNormalServerMessage("The ingredients in the " + target.getName() + " could make " + eRecipe.getResultNameWithGenus(realTarget) + ((eRecipe.hasActiveItem() && eRecipe.getActiveItem().getTemplateId() != 14) ? (" if you used " + StringUtilities.addGenus(eRecipe.getActiveItemName()) + ".") : "."));
                }
            }
            else if (wantedType == 1) {
                String needs = "when cooked.";
                final Item cooker = target.getTopParentOrNull();
                if (cooker != null && cooker.getTemplate().isCooker()) {
                    if (!eRecipe.hasCooker(cooker.getTemplateId())) {
                        needs = "when cooked in a different cooker.";
                    }
                }
                else {
                    needs = "when cooked in a cooker.";
                }
                performer.getCommunicator().sendNormalServerMessage("You think this may well work " + needs);
            }
            else if (wantedType == 0) {
                performer.getCommunicator().sendNormalServerMessage("You think this may well work given time.");
            }
            else {
                performer.getCommunicator().sendNormalServerMessage("You think this may well work" + ((eRecipe.hasActiveItem() && eRecipe.getActiveItem().getTemplateId() != 14) ? (" if you used " + StringUtilities.addGenus(eRecipe.getActiveItemName()) + ".") : "."));
            }
            if (performer.getPower() > 1 || performer.hasFlag(51)) {
                performer.getCommunicator().sendNormalServerMessage("(Recipe Id:" + eRecipe.getRecipeId() + ", Current difficulty:" + eRecipe.getDifficulty(target) + ")");
            }
            else {
                performer.getCommunicator().sendNormalServerMessage("Current difficulty:" + eRecipe.getDifficulty(target) + ".");
            }
            return true;
        }
        final Recipe lRecipe = Recipes.getRecipeFor(performer.getWurmId(), wantedType, source, realTarget, false, false);
        if (lRecipe != null) {
            final Recipe.LiquidResult liquidResult = lRecipe.getNewWeightGrams(target);
            if (!liquidResult.isSuccess()) {
                if (RecipesByPlayer.isKnownRecipe(performer.getWurmId(), lRecipe.getRecipeId())) {
                    if (wantedType == 1) {
                        performer.getCommunicator().sendNormalServerMessage("The ingredients in the " + target.getName() + " would make " + lRecipe.getResultNameWithGenus(realTarget) + " when cooked, but...");
                    }
                    else if (wantedType == 0) {
                        performer.getCommunicator().sendNormalServerMessage("The ingredients in the " + target.getName() + " would make " + lRecipe.getResultNameWithGenus(realTarget) + " given time, but...");
                    }
                    else {
                        performer.getCommunicator().sendNormalServerMessage("The ingredients in the " + target.getName() + " would make " + lRecipe.getResultNameWithGenus(realTarget) + ((lRecipe.hasActiveItem() && lRecipe.getActiveItem().getTemplateId() != 14) ? (" if you used " + StringUtilities.addGenus(lRecipe.getActiveItemName())) : "") + ", but...");
                    }
                }
                else if (wantedType == 1) {
                    performer.getCommunicator().sendNormalServerMessage("You think this may well work when cooked, but...");
                }
                else if (wantedType == 0) {
                    performer.getCommunicator().sendNormalServerMessage("You think this may well work given time, but...");
                }
                else {
                    performer.getCommunicator().sendNormalServerMessage("You think this may well work" + ((lRecipe.hasActiveItem() && lRecipe.getActiveItem().getTemplateId() != 14) ? (" if you used " + StringUtilities.addGenus(lRecipe.getActiveItemName())) : "") + ", but...");
                }
                for (final String error : liquidResult.getErrors().values()) {
                    performer.getCommunicator().sendNormalServerMessage(error);
                }
                if (performer.getPower() > 1 || performer.hasFlag(51)) {
                    performer.getCommunicator().sendNormalServerMessage("(Recipe Id:" + lRecipe.getRecipeId() + ", Current difficulty:" + lRecipe.getDifficulty(target) + ")");
                }
                else {
                    performer.getCommunicator().sendNormalServerMessage("Current difficulty:" + lRecipe.getDifficulty(target) + ".");
                }
                return true;
            }
        }
        return false;
    }
    
    private boolean showPartialRecipeInfo(final Creature performer, @Nullable final Item source, final Item target, final Item realTarget, final byte wantedType) {
        if (target.isFoodMaker()) {
            final Recipe[] recipes = Recipes.getPartialRecipeListFor(performer, wantedType, realTarget);
            if (recipes.length > 0) {
                final List<Recipe> recipesUnknown = new ArrayList<Recipe>();
                final List<Recipe> recipesUnNamed = new ArrayList<Recipe>();
                final List<Recipe> recipesKnown = new ArrayList<Recipe>();
                final List<Recipe> recipesNamed = new ArrayList<Recipe>();
                for (final Recipe recipe : recipes) {
                    if (RecipesByPlayer.isKnownRecipe(performer.getWurmId(), recipe.getRecipeId())) {
                        if (recipe.isNameable()) {
                            recipesNamed.add(recipe);
                        }
                        else {
                            recipesKnown.add(recipe);
                        }
                    }
                    else if (recipe.isNameable()) {
                        recipesUnNamed.add(recipe);
                    }
                    else if (!recipe.isLootable()) {
                        recipesUnknown.add(recipe);
                    }
                }
                if (recipesUnknown.size() > 0) {
                    final Recipe recipe2 = recipesUnknown.get(ItemBehaviour.recipeRandom.nextInt(recipesUnknown.size()));
                    if (this.pickRandomIngredient(performer, recipe2)) {
                        return true;
                    }
                }
                if (recipesUnNamed.size() > 0) {
                    final Recipe recipe2 = recipesUnNamed.get(ItemBehaviour.recipeRandom.nextInt(recipesUnNamed.size()));
                    if (this.pickRandomIngredient(performer, recipe2)) {
                        return true;
                    }
                }
                if (recipesKnown.size() > 0) {
                    final Recipe recipe2 = recipesKnown.get(ItemBehaviour.recipeRandom.nextInt(recipesKnown.size()));
                    if (this.pickRandomIngredient(performer, recipe2)) {
                        return true;
                    }
                }
                if (recipesNamed.size() > 0) {
                    final Recipe recipe2 = recipesNamed.get(ItemBehaviour.recipeRandom.nextInt(recipesNamed.size()));
                    if (this.pickRandomIngredient(performer, recipe2)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
    
    private boolean pickRandomIngredient(final Creature performer, final Recipe recipe) {
        final Ingredient[] ingredients = recipe.getWhatsMissing();
        if (ingredients.length > 0) {
            final Ingredient ingredient = ingredients[ItemBehaviour.recipeRandom.nextInt(ingredients.length)];
            final String name = Recipes.getIngredientName(ingredient, false);
            final StringBuilder buf = new StringBuilder("Have you tried ");
            if (!ingredient.isLiquid()) {
                if (ingredient.getFound() == 0) {
                    buf.append("adding " + StringUtilities.addGenus(name));
                }
                else if (ingredient.getFound() < ingredient.getAmount()) {
                    buf.append("adding more " + name);
                }
                else {
                    buf.append("removing " + StringUtilities.addGenus(name));
                }
            }
            else {
                buf.append(StringUtilities.addGenus(name));
                if (ingredient.getRatio() > 0) {
                    buf.append(" (" + ingredient.getRatio() + "% of solids)");
                }
            }
            performer.getCommunicator().sendNormalServerMessage(buf.toString() + "?" + ((performer.getPower() > 1 || performer.hasFlag(51)) ? (" (Could make recipe:" + recipe.getRecipeId() + ")") : ""));
            return true;
        }
        return false;
    }
    
    private void setVolume(final Creature performer, final Item target, final int auxbyte) {
        if (target.getTemplateId() == 1172) {
            if (target.getTopParentOrNull() != performer.getInventory()) {
                performer.getCommunicator().sendNormalServerMessage("You can only change volume when the " + target.getName() + " is in your inventory.");
            }
            else if (!target.isEmpty(false)) {
                performer.getCommunicator().sendNormalServerMessage("You can only change volume when the " + target.getName() + " is empty.");
            }
            else {
                target.setAuxData((byte)auxbyte);
                final int newVolume = target.setInternalVolumeFromAuxByte();
                final String vm = (newVolume >= 1000) ? (newVolume / 1000 + "kg") : (newVolume + "g");
                performer.getCommunicator().sendNormalServerMessage("You carefully adjust the " + target.getName() + " volume by utilising a small wheel on the bottom and set it to " + vm + ".");
            }
        }
    }
    
    private void taste(final Creature performer, final Item target) {
        final float qlevel = target.getCurrentQualityLevel();
        final int temp = target.getTemperature();
        final float nut = target.getNutritionLevel();
        if (target.getDamage() > 90.0f) {
            performer.getCommunicator().sendNormalServerMessage("The " + target.getName() + " does not taste good at all.");
            return;
        }
        if (temp > 2500) {
            performer.getCommunicator().sendNormalServerMessage("The " + target.getName() + " would burn your mouth.");
            return;
        }
        if (target.isCheese() && target.isZombiefied()) {
            if (performer.getKingdomTemplateId() != 3) {
                performer.getCommunicator().sendNormalServerMessage("The " + target.getName() + " is horrible, and you can't eat it.");
                return;
            }
            performer.getCommunicator().sendNormalServerMessage("The " + target.getName() + " tastes weird, but good.");
        }
        final String tasteString = target.getName().endsWith("s") ? " taste" : " tastes";
        String toSend = "The " + target.getName();
        if (qlevel > 50.0f) {
            toSend = toSend + tasteString + " " + target.getTasteString() + " It also seems";
        }
        else {
            toSend += tasteString;
        }
        if (nut > 0.9) {
            toSend += " extremely nutritional.";
        }
        else if (nut > 0.7) {
            toSend += " quite nutritional.";
        }
        else if (nut > 0.5) {
            toSend += " nutritional.";
        }
        else if (nut > 0.3) {
            toSend += " not very nutritional.";
        }
        else {
            toSend += " not at all nutritional.";
        }
        if (target.isSalted()) {
            toSend += " You think it might have some salt in it.";
        }
        performer.getCommunicator().sendNormalServerMessage(toSend);
        final SkillTemplate skill = AffinitiesTimed.getTimedAffinitySkill(performer, target);
        if (skill != null) {
            performer.getCommunicator().sendNormalServerMessage("You think the " + target.getName() + " might give you more of an insight about " + skill.getName().toLowerCase() + "!");
        }
    }
    
    private void tasteLiquid(final Creature performer, final Item target) {
        if (!target.isSealedByPlayer()) {
            performer.getCommunicator().sendNormalServerMessage("Its not sealed, just look inside!");
            return;
        }
        Item liquid = null;
        final Item[] itemsAsArray;
        final Item[] items = itemsAsArray = target.getItemsAsArray();
        for (final Item item : itemsAsArray) {
            if (item.isLiquid()) {
                liquid = item;
                break;
            }
        }
        if (liquid == null) {
            performer.getCommunicator().sendNormalServerMessage("There is nothing to taste!", (byte)1);
            return;
        }
        String rb = "";
        final Recipe recipe = liquid.getRecipe();
        final int skillId = (recipe == null) ? SkillSystem.getSkillByName("natural substances") : recipe.getSkillId();
        final Skill skill = performer.getSkills().getSkillOrLearn(skillId);
        final double knowledge = skill.getKnowledge(0.0);
        if (knowledge > 75.0) {
            rb = "it has a quality of " + String.format("%.2f", liquid.getCurrentQualityLevel()) + ".";
        }
        else if (knowledge > 40.0) {
            final int range = (int)liquid.getCurrentQualityLevel() / 5 * 5;
            rb = "it has a quality in the range of " + range + " to " + (range + 4) + ".";
        }
        else if (liquid.getTemplateId() == 128 || liquid.getTemplateId() == 634) {
            if (liquid.getCurrentQualityLevel() > 90.0f) {
                rb = "it is very clear.";
            }
            else if (liquid.getCurrentQualityLevel() > 70.0f) {
                rb = "it is clear.";
            }
            else if (liquid.getCurrentQualityLevel() > 45.0f) {
                rb = "it is slightly cloudy.";
            }
            else if (liquid.getCurrentQualityLevel() > 20.0f) {
                rb = "it is cloudy.";
            }
            else {
                rb = "it is very cloudy.";
            }
        }
        else if (liquid.isAlcohol()) {
            if (liquid.getCurrentQualityLevel() > 95.0f) {
                rb = "it is mind blowingly strong.";
            }
            else if (liquid.getCurrentQualityLevel() > 80.0f) {
                rb = "it is very strong.";
            }
            else if (liquid.getCurrentQualityLevel() > 65.0f) {
                rb = "it is strong.";
            }
            else if (liquid.getCurrentQualityLevel() > 50.0f) {
                rb = "it is mostly strong.";
            }
            else if (liquid.getCurrentQualityLevel() > 35.0f) {
                rb = "it has a medium strength.";
            }
            else if (liquid.getCurrentQualityLevel() > 20.0f) {
                rb = "it has a weak strength.";
            }
            else {
                rb = "it has a very weak strength.";
            }
        }
        else if (liquid.isMilk()) {
            if (liquid.getCurrentQualityLevel() > 90.0f) {
                rb = "it is creamy.";
            }
            else if (liquid.getCurrentQualityLevel() > 75.0f) {
                rb = "it is smooth.";
            }
            else if (liquid.getCurrentQualityLevel() > 60.0f) {
                rb = "it is more like semi-skimmed milk.";
            }
            else if (liquid.getCurrentQualityLevel() > 40.0f) {
                rb = "it is more like skimmed milk.";
            }
            else if (liquid.getCurrentQualityLevel() > 20.0f) {
                rb = "it tastes like it has been watered down.";
            }
            else {
                rb = "it tastes like it has passed its use by date.";
            }
        }
        else if (liquid.isDye()) {
            if (liquid.getCurrentQualityLevel() > 90.0f) {
                rb = "it colours your tongue, and strangely tastes excellent.";
            }
            else if (liquid.getCurrentQualityLevel() > 75.0f) {
                rb = "it colours your tongue, and strangely tastes very good.";
            }
            else if (liquid.getCurrentQualityLevel() > 60.0f) {
                rb = "it colours your tongue, and strangely tastes good.";
            }
            else if (liquid.getCurrentQualityLevel() > 40.0f) {
                rb = "it colours your tongue, and strangely tastes average.";
            }
            else if (liquid.getCurrentQualityLevel() > 20.0f) {
                rb = "it colours your tongue, and strangely tastes acceptable.";
            }
            else {
                rb = "it colours your tounge, and strangely tastes poor.";
            }
        }
        else if (liquid.isDrinkable() || liquid.isFood()) {
            if (liquid.getCurrentQualityLevel() > 90.0f) {
                rb = "it is excellent quality.";
            }
            else if (liquid.getCurrentQualityLevel() > 75.0f) {
                rb = "it is very good quality.";
            }
            else if (liquid.getCurrentQualityLevel() > 60.0f) {
                rb = "it is good quality.";
            }
            else if (liquid.getCurrentQualityLevel() > 40.0f) {
                rb = "it is average quality.";
            }
            else if (liquid.getCurrentQualityLevel() > 20.0f) {
                rb = "it is acceptable quality.";
            }
            else {
                rb = "it is poor quality.";
            }
        }
        else if (liquid.getCurrentQualityLevel() > 90.0f) {
            rb = "it tastes odd but strangely seems excellent.";
        }
        else if (liquid.getCurrentQualityLevel() > 75.0f) {
            rb = "it tastes odd but strangely seems very good.";
        }
        else if (liquid.getCurrentQualityLevel() > 60.0f) {
            rb = "it tastes odd but strangely seems good.";
        }
        else if (liquid.getCurrentQualityLevel() > 40.0f) {
            rb = "it tastes odd but strangely seems average.";
        }
        else if (liquid.getCurrentQualityLevel() > 20.0f) {
            rb = "it tastes odd but strangely seems acceptable.";
        }
        else {
            rb = "it tastes odd but strangely seems poor.";
        }
        if (recipe == null || !recipe.hasDescription()) {
            performer.getCommunicator().sendNormalServerMessage(liquid.examine(performer) + " Also " + rb);
        }
        else {
            performer.getCommunicator().sendNormalServerMessage(recipe.getResultDescription(target) + " Also " + rb);
        }
    }
    
    private void readVillageMessages(final Creature performer, final Item target) {
        if (target.mayAccessHold(performer)) {
            if (performer.getCurrentVillage() != null) {
                final VillageMessageBoard vmb = new VillageMessageBoard(performer, performer.getCurrentVillage(), target);
                vmb.sendQuestion();
            }
            else {
                performer.getCommunicator().sendNormalServerMessage("Village mesage board is not in a village.");
            }
        }
        else {
            performer.getCommunicator().sendNormalServerMessage("You dont have permission to read this Village mesage board.");
        }
    }
    
    private void postVillageMessage(final Creature performer, final Item source, final Item target) {
        final Village village = performer.getCurrentVillage();
        if (village == null) {
            performer.getCommunicator().sendNormalServerMessage("You are not stood in a village.");
            return;
        }
        if (!target.mayAddPMs(performer) && !target.mayPostNotices(performer)) {
            performer.getCommunicator().sendNormalServerMessage("You do not have permissions to put anything on this vilage notice board.");
            return;
        }
        if (source.canHaveInscription() && source.getAuxData() == 0) {
            final InscriptionData ins = source.getInscription();
            if (ins != null && ins.hasBeenInscribed()) {
                final VillageMessagePopup vmp = new VillageMessagePopup(performer, village, ins, source.getWurmId(), target);
                vmp.sendQuestion();
            }
            else {
                performer.getCommunicator().sendNormalServerMessage("The " + source.getName() + " needs to be inscribed before adding to the " + target.getName() + ".");
            }
        }
    }
    
    private static boolean useRuneOnItem(final Action act, final Creature performer, final Item source, final Item target, final short action, final float counter) {
        if (target.isVehicle() && !target.mayManage(performer)) {
            performer.getCommunicator().sendNormalServerMessage("You do not have permission to use the rune on that item.", (byte)3);
            return true;
        }
        if (!Methods.isActionAllowed(performer, (short)192, target.getTileX(), target.getTileY())) {
            performer.getCommunicator().sendNormalServerMessage("You are not allowed to use that here.", (byte)3);
            return true;
        }
        if ((RuneUtilities.isEnchantRune(source) || RuneUtilities.getModifier(RuneUtilities.getEnchantForRune(source), RuneUtilities.ModifierEffect.SINGLE_COLOR) > 0.0f) && !RuneUtilities.canApplyRuneTo(source, target)) {
            performer.getCommunicator().sendNormalServerMessage("You cannot use the rune on that item.", (byte)3);
            return true;
        }
        if (RuneUtilities.isSingleUseRune(source) && RuneUtilities.getSpellForRune(source) != null) {
            if (!Methods.isActionAllowed(performer, (short)245, target.getTileX(), target.getTileY())) {
                performer.getCommunicator().sendNormalServerMessage("You are not allowed to use that here.", (byte)3);
                return true;
            }
        }
        else {
            if (!RuneUtilities.isCorrectTarget(source, target)) {
                performer.getCommunicator().sendNormalServerMessage("That is the wrong type of rune for that item.", (byte)3);
                return true;
            }
            if (target.getOwnerId() != -10L && target.getOwnerId() != performer.getWurmId()) {
                performer.getCommunicator().sendNormalServerMessage("You are not allowed to use the rune on that item.", (byte)3);
                return true;
            }
            if (MethodsItems.checkIfStealing(target, performer, null)) {
                performer.getCommunicator().sendNormalServerMessage("You do not have permission to use the rune on that item.", (byte)3);
                return true;
            }
        }
        int time = act.getTimeLeft();
        if (counter == 1.0f) {
            String actionString = "use the rune on the ";
            if (RuneUtilities.isEnchantRune(source)) {
                actionString = "attach the rune to the ";
            }
            performer.getCommunicator().sendNormalServerMessage("You start to " + actionString + target.getName() + ".");
            time = Actions.getSlowActionTime(performer, performer.getSoulDepth(), null, 0.0);
            act.setTimeLeft(time);
            performer.sendActionControl(act.getActionString(), true, act.getTimeLeft());
            performer.getStatus().modifyStamina(-600.0f);
        }
        if (act.currentSecond() % 5 == 0) {
            performer.getStatus().modifyStamina(-300.0f);
        }
        if (counter * 10.0f > time) {
            final Skill soulDepth = performer.getSoulDepth();
            final double diff = 20.0f + source.getDamage() - (source.getCurrentQualityLevel() + source.getRarity() - 45.0);
            final double power = soulDepth.skillCheck(diff, source.getCurrentQualityLevel(), false, counter);
            if (power > 0.0) {
                if (RuneUtilities.isEnchantRune(source)) {
                    performer.getCommunicator().sendNormalServerMessage("You successfully attach the rune to the " + target.getName() + ".", (byte)2);
                    performer.achievement(491);
                    ItemSpellEffects effs = target.getSpellEffects();
                    if (effs == null) {
                        effs = new ItemSpellEffects(target.getWurmId());
                    }
                    for (byte runeEffect = effs.getRandomRuneEffect(); runeEffect != -10L; runeEffect = effs.getRandomRuneEffect()) {
                        if (RuneUtilities.getModifier(runeEffect, RuneUtilities.ModifierEffect.ENCH_GLOW) > 0.0f) {
                            target.setLightOverride(false);
                            target.setIsAlwaysLit(target.getTemplate().alwaysLit);
                        }
                        effs.removeSpellEffect(runeEffect);
                    }
                    final byte runeEnch = RuneUtilities.getEnchantForRune(source);
                    final SpellEffect eff = new SpellEffect(target.getWurmId(), runeEnch, 50.0f, 200000000);
                    effs.addSpellEffect(eff);
                    if (RuneUtilities.getModifier(runeEnch, RuneUtilities.ModifierEffect.ENCH_GLOW) > 0.0f) {
                        target.setLightOverride(true);
                        target.setIsAlwaysLit(true);
                    }
                }
                else if (RuneUtilities.getModifier(RuneUtilities.getEnchantForRune(source), RuneUtilities.ModifierEffect.SINGLE_COLOR) > 0.0f) {
                    if (RuneUtilities.canApplyRuneTo(source, target)) {
                        performer.getCommunicator().sendNormalServerMessage("The " + target.getName() + " is given a random color.");
                        performer.achievement(491);
                        int c;
                        if (source.getColor() == -1) {
                            c = WurmColor.createColor(1 + Server.rand.nextInt(255), 1 + Server.rand.nextInt(255), 1 + Server.rand.nextInt(255));
                        }
                        else {
                            c = source.getColor();
                        }
                        if (target.isDragonArmour()) {
                            target.setColor2(c);
                        }
                        else {
                            target.setColor(c);
                        }
                    }
                }
                else {
                    if (RuneUtilities.getSpellForRune(source) == null || !RuneUtilities.getSpellForRune(source).isTargetAnyItem()) {
                        performer.getCommunicator().sendNormalServerMessage("You can't use the rune on that.", (byte)3);
                        return true;
                    }
                    if (!RuneUtilities.isCorrectTarget(source, target)) {
                        performer.getCommunicator().sendNormalServerMessage("You can't use the rune on that.", (byte)3);
                        return true;
                    }
                    RuneUtilities.getSpellForRune(source).castSpell(50.0, performer, target);
                    performer.achievement(491);
                }
                target.sendUpdate();
            }
            else if (RuneUtilities.isEnchantRune(source)) {
                performer.getCommunicator().sendNormalServerMessage("You try to attach the rune to the " + target.getName() + " but fail.", (byte)3);
            }
            else {
                performer.getCommunicator().sendNormalServerMessage("You try to use the rune on the " + target.getName() + " but fail.", (byte)3);
            }
            if (Servers.isThisATestServer()) {
                performer.getCommunicator().sendNormalServerMessage("Diff: " + diff + ", bonus: " + source.getCurrentQualityLevel() + ", sd: " + soulDepth.getKnowledge() + ", power: " + power + ", chance: " + soulDepth.getChance(diff, null, source.getCurrentQualityLevel()));
            }
            Items.destroyItem(source.getWurmId());
            return true;
        }
        return false;
    }
    
    private static boolean combineFragment(final Action act, final Creature performer, final Item source, final Item target, final short action, final float counter) {
        if (source.getTemplateId() != 1307 || target.getTemplateId() != 1307) {
            return true;
        }
        if (source.getWurmId() == target.getWurmId()) {
            return true;
        }
        if (source.getOwnerId() != performer.getWurmId() || target.getOwnerId() != performer.getWurmId()) {
            performer.getCommunicator().sendNormalServerMessage("The fragments must be in your inventory in order to combine them.", (byte)3);
            return true;
        }
        if (source.getData1() <= 0 || target.getData1() <= 0) {
            final String fully = (source.getAuxData() >= 64 || target.getAuxData() >= 64) ? "fully " : "";
            performer.getCommunicator().sendNormalServerMessage("The fragments must be " + fully + "identified before you can combine them.", (byte)3);
            return true;
        }
        if (source.getRealTemplate() == null || target.getRealTemplate() == null) {
            performer.getCommunicator().sendNormalServerMessage("You're not quite sure how these fragments can go together.", (byte)3);
            return true;
        }
        if (source.getRealTemplateId() != target.getRealTemplateId()) {
            performer.getCommunicator().sendNormalServerMessage("You don't think these two fragments can be pieced together in any way.", (byte)3);
            return true;
        }
        if (source.getRealTemplate().isMetal() && !source.getRealTemplate().isOre && !source.getRealTemplate().isMetalLump()) {
            if (source.getMaterial() == 11) {
                source.setMaterial((byte)93);
                source.sendUpdate();
            }
            else if (source.getMaterial() == 9) {
                source.setMaterial((byte)94);
                source.sendUpdate();
            }
        }
        if (target.getRealTemplate().isMetal() && !source.getRealTemplate().isOre && !source.getRealTemplate().isMetalLump()) {
            if (target.getMaterial() == 11) {
                target.setMaterial((byte)93);
                target.sendUpdate();
            }
            else if (source.getMaterial() == 9) {
                target.setMaterial((byte)94);
                target.sendUpdate();
            }
        }
        if (source.getMaterial() != target.getMaterial()) {
            if (source.getMaterial() == 0 && source.getRealTemplate().isMetal() && !source.getRealTemplate().isOre && !source.getRealTemplate().isMetalLump()) {
                source.setMaterial((byte)93);
                source.sendUpdate();
            }
            else if (target.getMaterial() == 0 && target.getRealTemplate().isMetal() && !source.getRealTemplate().isOre && !source.getRealTemplate().isMetalLump()) {
                target.setMaterial((byte)93);
                target.sendUpdate();
            }
            if (source.getMaterial() != target.getMaterial()) {
                performer.getCommunicator().sendNormalServerMessage("The fragments look like they might fit together if they were made of the same material.", (byte)3);
                return true;
            }
        }
        int time = act.getTimeLeft();
        if (counter == 1.0f) {
            performer.getCommunicator().sendNormalServerMessage("You start to carefully piece together the fragments.");
            Server.getInstance().broadCastAction(performer.getName() + " starts to carefully piece together some fragments.", performer, 5);
            time = Actions.getStandardActionTime(performer, performer.getSkills().getSkillOrLearn(10095), null, 0.0);
            act.setTimeLeft(time);
            performer.sendActionControl(act.getActionString(), true, act.getTimeLeft());
            performer.getStatus().modifyStamina(-1000.0f);
        }
        if (act.currentSecond() % 5 == 0) {
            performer.getStatus().modifyStamina(-500.0f);
        }
        if (counter * 10.0f <= time) {
            return false;
        }
        if (act.getRarity() != 0) {
            performer.playPersonalSound("sound.fx.drumroll");
        }
        final Skill restoration = performer.getSkills().getSkillOrLearn(10095);
        final ItemTemplate combinedTemplate = ItemTemplateFactory.getInstance().getTemplateOrNull(source.getRealTemplateId());
        if (combinedTemplate == null) {
            performer.getCommunicator().sendNormalServerMessage("Something went wrong when piecing these two fragments together. You're not sure what item they create.");
            return true;
        }
        final double totalNeeded = combinedTemplate.getFragmentAmount();
        final int newTotal = source.getAuxData() + target.getAuxData();
        final boolean annGift = combinedTemplate.getTemplateId() == 651;
        if (performer.hasFlag(55) && annGift) {
            performer.getCommunicator().sendNormalServerMessage("You decide against completing this item, as you've already completed your gift for this year.");
            return true;
        }
        if (!performer.isPaying() && annGift) {
            performer.getCommunicator().sendNormalServerMessage("As a non-premium player you're not really sure what you should do to put this back together properly.");
            return true;
        }
        if (!WurmCalendar.isAnniversary() && annGift) {
            performer.getCommunicator().sendNormalServerMessage("The anniversary week is now over, you're unable to combine these fragments any longer.");
            return true;
        }
        final double difficulty = Math.min(90.0, FragmentUtilities.getDifficultyForItem(source.getRealTemplateId(), source.getMaterial()) * ((source.getAuxData() + target.getAuxData()) / totalNeeded));
        final double power = restoration.skillCheck(difficulty, 0.0, false, (float)difficulty);
        if (power >= 0.0) {
            if (newTotal >= totalNeeded) {
                try {
                    Item createdItem = null;
                    if (annGift) {
                        createdItem = ItemFactory.createItem(FragmentUtilities.getRandomAnniversaryGift(), 80.0f, act.getRarity(), performer.getName());
                        performer.setFlag(55, true);
                        if (source.getRarity() > createdItem.getRarity()) {
                            createdItem.setRarity(source.getRarity());
                        }
                        if (target.getRarity() > createdItem.getRarity()) {
                            createdItem.setRarity(target.getRarity());
                        }
                    }
                    else {
                        final int finalData2 = (source.getData2() * source.getAuxData() + target.getData2() * target.getAuxData()) / newTotal;
                        double newQl = (source.getCurrentQualityLevel() * source.getAuxData() + target.getCurrentQualityLevel() * target.getAuxData()) / newTotal;
                        newQl += (100.0 - newQl) * (finalData2 / 500.0f);
                        createdItem = ItemFactory.createItem(combinedTemplate.getTemplateId(), (float)Math.max(1.0, Math.min(100.0, newQl)), act.getRarity(), performer.getName());
                        if (source.getRarity() > createdItem.getRarity()) {
                            createdItem.setRarity(source.getRarity());
                        }
                        if (target.getRarity() > createdItem.getRarity()) {
                            createdItem.setRarity(target.getRarity());
                        }
                        if (source.getMaterial() != createdItem.getMaterial() && source.getMaterial() != 0) {
                            createdItem.setMaterial(source.getMaterial());
                        }
                        if (createdItem.getMaterial() == 0 && createdItem.isMetal()) {
                            createdItem.setMaterial((byte)11);
                        }
                        if (createdItem.isMetal()) {
                            if (createdItem.getMaterial() == 94) {
                                if (finalData2 >= 85) {
                                    createdItem.setMaterial(FragmentUtilities.getMetalMoonMaterial(finalData2));
                                }
                                else {
                                    createdItem.setMaterial(FragmentUtilities.getMetalAlloyMaterial(finalData2));
                                }
                            }
                            else if (createdItem.getMaterial() == 93) {
                                createdItem.setMaterial(FragmentUtilities.getMetalBaseMaterial(finalData2));
                            }
                        }
                        else if (createdItem.isWood()) {
                            createdItem.setMaterial(FragmentUtilities.getRandomWoodMaterial(finalData2));
                        }
                        if (finalData2 >= 50) {
                            int i;
                            for (int numEnchants = i = FragmentUtilities.getRandomEnchantNumber(finalData2); i > 0; --i) {
                                final float enchPower = Math.max(1.0f, Math.min(104.0f, (finalData2 - 50) * 2.0f - i * 10.0f));
                                FragmentUtilities.addRandomEnchantment(createdItem, i, enchPower);
                            }
                        }
                        if (createdItem.isAbility()) {
                            createdItem.setAuxData((byte)2);
                        }
                        performer.achievement(484);
                        if (createdItem.getTemplate().isStatue()) {
                            performer.achievement(486);
                        }
                        if (createdItem.getTemplate().isMask()) {
                            performer.achievement(487);
                        }
                        if (createdItem.getTemplate().isTool()) {
                            performer.achievement(488);
                        }
                    }
                    performer.getCommunicator().sendNormalServerMessage("You successfully recreate " + createdItem.getNameWithGenus() + " from the fragments.", (byte)2);
                    Server.getInstance().broadCastAction(performer.getName() + " looks pleased as they hold up a completed " + createdItem.getName() + ".", performer, 5);
                    performer.getInventory().insertItem(createdItem);
                    Items.destroyItem(source.getWurmId());
                    if (newTotal > totalNeeded) {
                        target.setRarity((byte)0);
                        target.setAuxData((byte)(newTotal - totalNeeded));
                        target.setData2((int)(target.getData2() * 0.75f));
                        target.setWeight(combinedTemplate.getWeightGrams() / combinedTemplate.getFragmentAmount() * target.getAuxData(), true);
                        try {
                            final Item parentItem = target.getParent();
                            if (parentItem != null) {
                                parentItem.dropItem(target.getWurmId(), false);
                                parentItem.insertItem(target);
                            }
                        }
                        catch (NoSuchItemException e) {
                            target.updateIfGroundItem();
                        }
                    }
                    else {
                        Items.destroyItem(target.getWurmId());
                    }
                }
                catch (FailedException | NoSuchTemplateException ex) {}
            }
            else {
                performer.getCommunicator().sendNormalServerMessage("You add a little bit more to the " + source.getName() + ".", (byte)2);
                Server.getInstance().broadCastAction(performer.getName() + " manages to combine the fragments.", performer, 5);
                source.setQualityLevel((source.getCurrentQualityLevel() * source.getAuxData() + target.getCurrentQualityLevel() * target.getAuxData()) / newTotal);
                source.setData2((source.getData2() * source.getAuxData() + target.getData2() * target.getAuxData()) / newTotal);
                source.setAuxData((byte)newTotal);
                source.setWeight(source.getWeightGrams() + target.getWeightGrams(), true);
                source.setDamage(0.0f);
                if (act.getRarity() > source.getRarity() && Server.rand.nextInt(source.getAuxData()) == 0) {
                    source.setRarity(act.getRarity());
                }
                if (target.getRarity() > source.getRarity()) {
                    source.setRarity(target.getRarity());
                }
                Items.destroyItem(target.getWurmId());
            }
        }
        else if (power > -30.0) {
            performer.getCommunicator().sendNormalServerMessage("You make a slight mistake combining the fragments and they fall apart but are untarnished.", (byte)2);
            Server.getInstance().broadCastAction(performer.getName() + " makes a bad move and the fragments fall back apart.", performer, 5);
        }
        else {
            performer.getCommunicator().sendNormalServerMessage("You use a bit too much pressure and slightly crack the fragments as they fall apart.", (byte)2);
            Server.getInstance().broadCastAction(performer.getName() + " grunts as a small crack appears on the fragments.", performer, 5);
            source.setDamage((float)(source.getDamage() + -power * 0.10000000149011612));
            target.setDamage((float)(target.getDamage() + -power * 0.10000000149011612));
        }
        return true;
    }
    
    private static boolean identifyFragment(final Action act, final Creature performer, final Item source, final Item target, final short action, final float counter) {
        if (target.getTemplateId() != 1307) {
            return true;
        }
        if (source.getTemplateId() != 441 && source.getTemplateId() != 97) {
            performer.getCommunicator().sendNormalServerMessage("The " + source.getName() + " might not be the best tool for identifying this fragment.", (byte)3);
            return true;
        }
        if (target.getData1() > 0) {
            performer.getCommunicator().sendNormalServerMessage("The " + target.getName() + " has already been fully identified.", (byte)3);
            return true;
        }
        int time = act.getTimeLeft();
        if (counter == 1.0f) {
            final String usage = (source.getTemplateId() == 441) ? "brush" : "chisel";
            performer.getCommunicator().sendNormalServerMessage("You carefully start " + usage + "ing away bits of dirt and rock from the " + target.getName() + ".");
            Server.getInstance().broadCastAction(performer.getName() + " starts to carefully " + usage + " away at a small item fragment.", performer, 5);
            time = Actions.getStandardActionTime(performer, performer.getSkills().getSkillOrLearn(10095), source, 0.0);
            act.setTimeLeft(time);
            performer.sendActionControl(act.getActionString(), true, act.getTimeLeft());
            performer.getStatus().modifyStamina(-1000.0f);
        }
        if (act.currentSecond() % 5 == 0) {
            performer.getStatus().modifyStamina(-500.0f);
        }
        if (counter * 10.0f > time) {
            if (act.getRarity() != 0) {
                performer.playPersonalSound("sound.fx.drumroll");
            }
            final boolean correctTool = (target.getAuxData() < 65) ? (source.getTemplateId() == 97) : (source.getTemplateId() == 441);
            final Skill restoration = performer.getSkills().getSkillOrLearn(10095);
            final float difficulty = FragmentUtilities.getDifficultyForItem(target.getRealTemplateId(), target.getMaterial()) / 2;
            final float power = (float)restoration.skillCheck(difficulty, source, 0.0, false, correctTool ? counter : (counter / 2.0f));
            try {
                performer.getSkills().getSkillOrLearn(source.getPrimarySkill()).skillCheck(difficulty, 0.0, false, correctTool ? counter : (counter / 2.0f));
            }
            catch (NoSuchSkillException ex) {}
            if (power >= 0.0f) {
                final ItemTemplate combinedTemplate = ItemTemplateFactory.getInstance().getTemplateOrNull(target.getRealTemplateId());
                if (combinedTemplate == null) {
                    performer.getCommunicator().sendNormalServerMessage("Something is wrong with this fragment. It may be a piece of old garbage.");
                    return true;
                }
                if (act.getRarity() > target.getRarity()) {
                    target.setRarity(act.getRarity());
                }
                final double bonus = 10.0f * (power / 20.0f + 1.0f) * (correctTool ? 1.0f : 0.5f);
                source.setDamage(source.getDamage() + (100.0f - power) * 1.0E-4f * source.getDamageModifier());
                final int newTotal = (int)Math.min(127.0, target.getAuxData() + bonus);
                final int finalFragmentWeight = combinedTemplate.getWeightGrams() / combinedTemplate.getFragmentAmount();
                if (newTotal == 127) {
                    target.setAuxData((byte)1);
                    target.setData1(1);
                    target.setData2((int)((target.getData2() + power) / 2.0f));
                    target.setWeight(finalFragmentWeight, false);
                    performer.getCommunicator().sendNormalServerMessage("You successfully identify the fragment as " + target.getNameWithGenus() + ".");
                    Server.getInstance().broadCastAction(performer.getName() + " looks pleased as they identify the item fragment.", performer, 5);
                    performer.achievement(482);
                    try {
                        final Item parentItem = target.getParent();
                        if (parentItem != null) {
                            parentItem.dropItem(target.getWurmId(), false);
                            parentItem.insertItem(target);
                        }
                    }
                    catch (NoSuchItemException e) {
                        target.updateIfGroundItem();
                    }
                }
                else {
                    target.setAuxData((byte)newTotal);
                    target.setData2((int)((target.getData2() + power) / 2.0f));
                    final int newWeight = (int)(target.getTemplate().getWeightGrams() - newTotal / 127.0f * (target.getTemplate().getWeightGrams() - finalFragmentWeight));
                    target.setWeight(newWeight, false);
                    performer.getCommunicator().sendNormalServerMessage("You successfully clear away a bit of dirt and rock from the " + target.getName() + " exposing a little more about its composition.");
                    Server.getInstance().broadCastAction(performer.getName() + " finishes clearing away a bit of dirt and rock from the small item fragment.", performer, 5);
                    if (newTotal >= 65) {
                        performer.getCommunicator().sendNormalServerMessage("You think a metal brush might work best for brushing away the last small bits.");
                    }
                    else {
                        performer.getCommunicator().sendNormalServerMessage("You think a chisel might work best for chipping away the larger bits of rock.");
                    }
                }
                performer.getCommunicator().sendUpdateInventoryItem(target);
            }
            else {
                performer.getCommunicator().sendNormalServerMessage("You make a bad move and almost ruin the " + target.getName() + ".");
                Server.getInstance().broadCastAction(performer.getName() + " slips and almost ruins the item fragment.", performer, 5);
            }
            return true;
        }
        return false;
    }
    
    private static boolean makeBait(final Action act, final Creature performer, final Item tool, final Item target, final short action, final float counter) {
        if (!target.isFish()) {
            performer.getCommunicator().sendNormalServerMessage("Cannot make bait from that, as its not a fish!");
            return true;
        }
        if (target.getWeightGrams() >= 300) {
            performer.getCommunicator().sendNormalServerMessage("Cannot make bait from that, as its too large.");
            return true;
        }
        if (tool.getTemplateId() != 258 && tool.getTemplateId() != 93 && tool.getTemplateId() != 8) {
            performer.getCommunicator().sendNormalServerMessage("Cannot make fish bait using " + tool.getNameWithGenus() + ".");
            return true;
        }
        int time = act.getTimeLeft();
        if (counter == 1.0f) {
            performer.getCommunicator().sendNormalServerMessage("You start making bait from the " + target.getName() + ".");
            Server.getInstance().broadCastAction(performer.getName() + " starts making bait from " + target.getName() + ".", performer, 5);
            time = Actions.getStandardActionTime(performer, performer.getSkills().getSkillOrLearn(10033), tool, 0.0);
            act.setTimeLeft(time);
            performer.sendActionControl(act.getActionString(), true, act.getTimeLeft());
            performer.getStatus().modifyStamina(-1000.0f);
        }
        if (act.currentSecond() % 5 == 0) {
            performer.getStatus().modifyStamina(-500.0f);
        }
        if (counter * 10.0f > time) {
            if (act.getRarity() != 0) {
                performer.playPersonalSound("sound.fx.drumroll");
            }
            final Skill fishing = performer.getSkills().getSkillOrLearn(10033);
            final float difficulty = getBaitDifficulty(target, tool);
            try {
                performer.getSkills().getSkillOrLearn(tool.getPrimarySkill()).skillCheck(difficulty, 0.0, false, counter / 2.0f);
            }
            catch (NoSuchSkillException ex) {}
            if (act.getRarity() != 0) {
                performer.playPersonalSound("sound.fx.drumroll");
            }
            try {
                float ql = target.getCurrentQualityLevel();
                final int knowledge = (int)fishing.getKnowledge(0.0);
                if (knowledge < ql) {
                    ql -= Server.rand.nextFloat() * ((ql - knowledge) / 2.0f);
                }
                ql = Math.min(100.0f, ql + tool.getRarity());
                int count = 0;
                int newWeight = target.getWeightGrams();
                final String baitName = "fish bits";
                float addDam = 0.0f;
                final int tempId = getBaitTemplateId(target);
                while (newWeight > 0 && performer.getInventory().mayCreatureInsertItem()) {
                    final float power = (float)fishing.skillCheck(difficulty, tool, 0.0, false, counter / 10.0f);
                    final float newql = Math.max(Math.min(ql + power / 50.0f, 100.0f), 1.0f);
                    final byte rarity = (byte)Math.max(act.getRarity(), target.getRarity());
                    final Item bait = ItemFactory.createItem(tempId, newql, rarity, null);
                    performer.getInventory().insertItem(bait);
                    if (target.getRarity() > 0) {
                        target.setRarity((byte)0);
                        target.sendUpdate();
                    }
                    ++count;
                    newWeight -= bait.getWeightGrams();
                    addDam += (100.0f - power) * 1.0E-5f * tool.getDamageModifier();
                }
                tool.setDamage(tool.getDamage() + addDam);
                if (!performer.getInventory().mayCreatureInsertItem()) {
                    performer.getCommunicator().sendNormalServerMessage("You have no space left in your inventory to put more fish bits, so you destroy the remainder.");
                }
                performer.getCommunicator().sendNormalServerMessage("You make " + count + " " + "fish bits" + " from the " + target.getName() + ".");
                Server.getInstance().broadCastAction(performer.getName() + " makes some " + "fish bits" + " from a " + target.getName() + ".", performer, 5);
                Items.destroyItem(target.getWurmId());
            }
            catch (NoSuchTemplateException nst) {
                ItemBehaviour.logger.log(Level.WARNING, "No template for 1364", nst);
                performer.getCommunicator().sendNormalServerMessage("You fail to make any bait. You realize something is wrong with the world.");
            }
            catch (FailedException fe) {
                ItemBehaviour.logger.log(Level.WARNING, fe.getMessage(), fe);
                performer.getCommunicator().sendNormalServerMessage("You fail to make any bait. You realize something is wrong with the world.");
            }
            return true;
        }
        return false;
    }
    
    private static boolean makeBait(final Action act, final Creature performer, final Item target, final short action, final float counter) {
        final int ttid = target.getTemplateId();
        if (ttid != 200 && ttid != 1192 && ttid != 69 && ttid != 66 && ttid != 68 && ttid != 29 && ttid != 32) {
            performer.getCommunicator().sendNormalServerMessage("You cannot make bait from that.");
            return true;
        }
        int time = act.getTimeLeft();
        if (counter == 1.0f) {
            performer.getCommunicator().sendNormalServerMessage("You start making bait from the " + target.getName() + ".");
            Server.getInstance().broadCastAction(performer.getName() + " starts making bait from " + target.getName() + ".", performer, 5);
            time = Actions.getStandardActionTime(performer, performer.getSkills().getSkillOrLearn(10033), null, 0.0);
            act.setTimeLeft(time);
            performer.sendActionControl(act.getActionString(), true, act.getTimeLeft());
            performer.getStatus().modifyStamina(-1000.0f);
        }
        if (act.currentSecond() % 5 == 0) {
            performer.getStatus().modifyStamina(-500.0f);
        }
        if (counter * 10.0f > time) {
            if (act.getRarity() != 0) {
                performer.playPersonalSound("sound.fx.drumroll");
            }
            final Skill fishing = performer.getSkills().getSkillOrLearn(10033);
            final float difficulty = getBaitDifficulty(target);
            try {
                final int knowledge = (int)fishing.getKnowledge(0.0);
                float ql = target.getCurrentQualityLevel();
                if (knowledge < ql) {
                    ql -= Server.rand.nextFloat() * ((ql - knowledge) / 2.0f);
                }
                int count = 0;
                int newWeight = target.getWeightGrams();
                final String baitName = getBaitName(target);
                final int tempId = getBaitTemplateId(target);
                Item bait;
                for (float extraLoss = getBaitExtraLossMultiplier(target); count < 10 && newWeight > 0 && performer.getInventory().mayCreatureInsertItem(); newWeight -= (int)(bait.getWeightGrams() * extraLoss)) {
                    ++count;
                    final float power = (float)fishing.skillCheck(difficulty, null, 0.0, false, counter / 100.0f);
                    final float newql = Math.max(Math.min(ql + power / 50.0f, 100.0f), 1.0f);
                    final byte rarity = (byte)Math.max(act.getRarity(), target.getRarity());
                    bait = ItemFactory.createItem(tempId, newql, rarity, null);
                    performer.getInventory().insertItem(bait);
                    if (target.getRarity() > 0) {
                        target.setRarity((byte)0);
                        target.sendUpdate();
                    }
                }
                target.setWeight(newWeight, true);
                performer.getCommunicator().sendNormalServerMessage("You make " + count + " " + baitName + " from a " + target.getName() + ".");
                Server.getInstance().broadCastAction(performer.getName() + " makes some " + baitName + " from a " + target.getName() + ".", performer, 5);
            }
            catch (NoSuchTemplateException nst) {
                ItemBehaviour.logger.log(Level.WARNING, "No template for 1364", nst);
                performer.getCommunicator().sendNormalServerMessage("You fail to make any bait. You realize something is wrong with the world.");
            }
            catch (FailedException fe) {
                ItemBehaviour.logger.log(Level.WARNING, fe.getMessage(), fe);
                performer.getCommunicator().sendNormalServerMessage("You fail to make any bait. You realize something is wrong with the world.");
            }
            return true;
        }
        return false;
    }
    
    private static boolean makeFloat(final Action act, final Creature performer, final Item target, final short action, final float counter) {
        final int ttid = target.getTemplateId();
        if (ttid != 479) {
            performer.getCommunicator().sendNormalServerMessage("You cannot make a float from that.");
            return true;
        }
        int time = act.getTimeLeft();
        if (counter == 1.0f) {
            performer.getCommunicator().sendNormalServerMessage("You start making a float from the " + target.getName() + ".");
            Server.getInstance().broadCastAction(performer.getName() + " starts making a float from " + target.getName() + ".", performer, 5);
            time = Actions.getStandardActionTime(performer, performer.getSkills().getSkillOrLearn(10033), null, 0.0);
            act.setTimeLeft(time);
            performer.sendActionControl(act.getActionString(), true, act.getTimeLeft());
            performer.getStatus().modifyStamina(-1000.0f);
        }
        if (act.currentSecond() % 5 == 0) {
            performer.getStatus().modifyStamina(-500.0f);
        }
        if (counter * 10.0f > time) {
            if (act.getRarity() != 0) {
                performer.playPersonalSound("sound.fx.drumroll");
            }
            final Skill fishing = performer.getSkills().getSkillOrLearn(10033);
            final float difficulty = target.getCurrentQualityLevel() / 2.0f;
            try {
                final int knowledge = (int)fishing.getKnowledge(0.0);
                float ql = target.getCurrentQualityLevel();
                if (knowledge < ql) {
                    ql -= Server.rand.nextFloat() * ((ql - knowledge) / 2.0f);
                }
                int count = 0;
                int newWeight = target.getWeightGrams();
                final String floatName = "small piece of moss";
                while (count < 10 && newWeight > 0 && performer.getInventory().mayCreatureInsertItem()) {
                    ++count;
                    final float power = (float)fishing.skillCheck(difficulty, null, 0.0, false, counter / 100.0f);
                    final float newql = Math.max(Math.min(ql + power / 50.0f, 100.0f), 1.0f);
                    final byte rarity = (byte)Math.max(act.getRarity(), target.getRarity());
                    final Item moss = ItemFactory.createItem(1354, newql, rarity, null);
                    performer.getInventory().insertItem(moss);
                    if (target.getRarity() > 0) {
                        target.setRarity((byte)0);
                        target.sendUpdate();
                    }
                    newWeight -= moss.getWeightGrams() * 2;
                }
                target.setWeight(newWeight, true);
                performer.getCommunicator().sendNormalServerMessage("You make " + count + " " + "small piece of moss" + " from the " + target.getName() + ".");
                Server.getInstance().broadCastAction(performer.getName() + " makes some " + "small piece of moss" + " from a " + target.getName() + ".", performer, 5);
            }
            catch (NoSuchTemplateException nst) {
                ItemBehaviour.logger.log(Level.WARNING, "No template for 1354", nst);
                performer.getCommunicator().sendNormalServerMessage("You fail to make any bait. You realize something is wrong with the world.");
            }
            catch (FailedException fe) {
                ItemBehaviour.logger.log(Level.WARNING, fe.getMessage(), fe);
                performer.getCommunicator().sendNormalServerMessage("You fail to make any bait. You realize something is wrong with the world.");
            }
            return true;
        }
        return false;
    }
    
    private static float getBaitDifficulty(final Item target) {
        switch (target.getTemplateId()) {
            case 66: {
                return 7.0f;
            }
            case 68: {
                return 5.0f;
            }
            case 69: {
                return 10.0f;
            }
            case 200: {
                return 3.0f;
            }
            case 29: {
                return 15.0f;
            }
            case 32: {
                return 20.0f;
            }
            default: {
                return 1.0f;
            }
        }
    }
    
    private static float getBaitDifficulty(final Item target, final Item knife) {
        if (target.isFish()) {
            switch (knife.getTemplateId()) {
                case 258: {
                    return 15.0f;
                }
                case 8: {
                    return 10.0f;
                }
                case 93: {
                    return 5.0f;
                }
            }
        }
        return 1.0f;
    }
    
    private static int getBaitTemplateId(final Item target) {
        switch (target.getTemplateId()) {
            case 66:
            case 68:
            case 69: {
                return 1360;
            }
            case 200: {
                return 1361;
            }
            case 29: {
                return 1365;
            }
            case 32: {
                return 1366;
            }
            default: {
                if (target.isFish()) {
                    return 1363;
                }
                return 0;
            }
        }
    }
    
    private static String getBaitName(final Item target) {
        switch (target.getTemplateId()) {
            case 66:
            case 68:
            case 69: {
                return "cheese piece";
            }
            case 200: {
                return "dough ball";
            }
            case 29: {
                return "grain of wheat";
            }
            case 32: {
                return "corn kernel";
            }
            default: {
                if (target.isFish()) {
                    return "bit of Fish";
                }
                return "";
            }
        }
    }
    
    private static float getBaitExtraLossMultiplier(final Item target) {
        switch (target.getTemplateId()) {
            case 66:
            case 68:
            case 69: {
                return 1.0f;
            }
            case 200: {
                return 1.5f;
            }
            case 29: {
                return 2.0f;
            }
            case 32: {
                return 2.0f;
            }
            default: {
                if (target.isFish()) {
                    return 2.0f;
                }
                return 1.0f;
            }
        }
    }
    
    private static boolean convertFishingEquipment(final Creature performer, final Item target) {
        switch (target.getTemplateId()) {
            case 94: {
                return convertFishingEquipment(performer, target, 1344, 1347, 1357);
            }
            case 152: {
                return convertFishingEquipment(performer, target, 1344, 1347, 1356);
            }
            case 780: {
                return convertFishingEquipment(performer, target, 1344, new int[0]);
            }
            case 95: {
                return convertFishingEquipment(performer, target, 1357, new int[0]);
            }
            case 150: {
                return convertFishingEquipment(performer, target, 1347, 1357);
            }
            case 96: {
                return convertFishingEquipment(performer, target, 1356, new int[0]);
            }
            case 151: {
                return convertFishingEquipment(performer, target, 1347, 1356);
            }
            default: {
                return true;
            }
        }
    }
    
    private static boolean convertFishingEquipment(final Creature performer, final Item target, final int newId, final int... makeIds) {
        target.setTemplateId(newId);
        try {
            final ItemTemplate newTemplate = ItemTemplateFactory.getInstance().getTemplate(newId);
            target.setWeight(newTemplate.getWeightGrams(), false);
        }
        catch (NoSuchTemplateException e3) {
            ItemBehaviour.logger.warning(String.format("Error: Could not find template ID for converted fishing equipment: %s", newId));
        }
        ItemFactory.createContainerRestrictions(target);
        Item parent = target;
        try {
            for (final int id : makeIds) {
                final Item newItem = ItemFactory.createItem(id, target.getQualityLevel(), (byte)0, (byte)0, target.getCreatorName());
                parent.insertItem(newItem, true);
                if (newItem.isHollow()) {
                    parent = newItem;
                }
            }
        }
        catch (FailedException e) {
            ItemBehaviour.logger.log(Level.WARNING, e.getMessage(), e);
        }
        catch (NoSuchTemplateException e2) {
            ItemBehaviour.logger.log(Level.WARNING, e2.getMessage(), e2);
        }
        return true;
    }
    
    private static boolean openClam(final Action act, final Creature performer, final Item tool, final Item target, final short action, final float counter) {
        int time = act.getTimeLeft();
        if (counter == 1.0f) {
            performer.getCommunicator().sendNormalServerMessage("You start prying open " + target.getNameWithGenus() + ".");
            Server.getInstance().broadCastAction(performer.getName() + " starts prying open something.", performer, 5);
            time = Actions.getVariableActionTime(performer, performer.getSkills().getSkillOrLearn(10059), tool, 0.0, 60, 20, 2500);
            act.setTimeLeft(time);
            performer.sendActionControl(act.getActionString(), true, act.getTimeLeft());
            performer.getStatus().modifyStamina(-1000.0f);
            return false;
        }
        if (counter * 10.0f > time) {
            if (act.getRarity() != 0) {
                performer.playPersonalSound("sound.fx.drumroll");
            }
            getClamItem(performer, target, act);
            Items.destroyItem(target.getWurmId());
            return true;
        }
        return false;
    }
    
    private static void getClamItem(final Creature performer, final Item target, final Action act) {
        final ClamLootEnum[] loot = ClamLootEnum.getLootTable();
        final Skill butchering = performer.getSkills().getSkillOrLearn(10059);
        final float knowledge = (float)butchering.getKnowledge(0.0);
        if (Server.rand.nextFloat() * knowledge * 10.0f < 1.0f) {
            performer.getCommunicator().sendNormalServerMessage("You mysteriously cut yourself when trying to open that and throw the clam away in disgust!");
            CombatEngine.addWound(performer, performer, (byte)1, 13, 2000 + Server.rand.nextInt(2000), 0.0f, "cut", null, 0.0f, 0.0f, false, false, false, false);
            return;
        }
        final byte lootptr = (byte)Server.rand.nextInt(256);
        final ClamLootEnum cle = loot[lootptr & 0xFF];
        float ql = target.getCurrentQualityLevel();
        int lootId = cle.getTemplateId();
        FragmentUtilities.Fragment frag = null;
        switch (lootId) {
            case 51: {
                if (performer.checkCoinAward(100)) {
                    performer.getCommunicator().sendSafeServerMessage("You find a rare coin!");
                }
                return;
            }
            case 1397: {
                if (Server.rand.nextFloat() * 100.0f < 1.0f) {
                    lootId = 1398;
                    break;
                }
                break;
            }
            case 1307: {
                frag = FragmentUtilities.getRandomFragmentForSkill(knowledge / 5.0f, true);
                if (frag == null) {
                    performer.getCommunicator().sendNormalServerMessage("You fail to find anything in the " + target.getName() + ".");
                    return;
                }
                break;
            }
        }
        if (lootId == ClamLootEnum.NONE.getTemplateId()) {
            performer.getCommunicator().sendNormalServerMessage("You fail to find anything in the " + target.getName() + ".");
            return;
        }
        if (knowledge < ql) {
            ql -= Server.rand.nextFloat() * (ql - knowledge);
        }
        final byte rarity = (byte)Math.max(act.getRarity(), target.getRarity());
        try {
            final Item foundItem = ItemFactory.createItem(lootId, ql, rarity, null);
            switch (lootId) {
                case 1307: {
                    foundItem.setRealTemplate(frag.getItemId());
                    if (foundItem.getRealTemplate().getMaterial() != frag.getMaterial()) {
                        foundItem.setMaterial((byte)frag.getMaterial());
                        break;
                    }
                    break;
                }
                case 92: {
                    foundItem.setMaterial((byte)85);
                    break;
                }
            }
            if (cle.canHaveDamage()) {
                final float rnd = Server.rand.nextFloat() * 100.0f - 75.0f;
                if (rnd > 0.0f) {
                    foundItem.setDamage(rnd);
                }
            }
            if (cle.randomMaterial()) {
                if (MaterialUtilities.isMetal(foundItem.getMaterial())) {
                    foundItem.setMaterial(getRandomMetalMaterial());
                }
                else if (MaterialUtilities.isWood(foundItem.getMaterial())) {
                    foundItem.setMaterial(getRandomWoodMaterial());
                }
            }
            performer.getInventory().insertItem(foundItem);
            if (rarity > 2) {
                performer.achievement(334);
            }
            performer.getCommunicator().sendNormalServerMessage("You found " + foundItem.getNameWithGenus() + " in the " + target.getName() + ".");
            Server.getInstance().broadCastAction(performer.getName() + " found something in a " + target.getName() + ".", performer, 5);
        }
        catch (FailedException e) {
            ItemBehaviour.logger.log(Level.WARNING, e.getMessage(), e);
        }
        catch (NoSuchTemplateException e2) {
            ItemBehaviour.logger.log(Level.WARNING, e2.getMessage(), e2);
        }
    }
    
    public static byte getRandomMetalMaterial() {
        switch (Server.rand.nextInt(20)) {
            case 0: {
                return 7;
            }
            case 1: {
                return 8;
            }
            case 2: {
                return 10;
            }
            case 3: {
                return 13;
            }
            case 4: {
                return 34;
            }
            case 5: {
                return 12;
            }
            default: {
                return 11;
            }
        }
    }
    
    public static byte getRandomWoodMaterial() {
        if (Server.rand.nextInt(10) > 1) {
            switch (Server.rand.nextInt(10)) {
                case 0: {
                    return 14;
                }
                case 1: {
                    return 37;
                }
                case 2: {
                    return 40;
                }
                case 3: {
                    return 38;
                }
                case 4: {
                    return 41;
                }
                case 5: {
                    return 63;
                }
                case 6: {
                    return 64;
                }
                case 7: {
                    return 65;
                }
                case 8: {
                    return 66;
                }
                default: {
                    return 39;
                }
            }
        }
        else {
            switch (Server.rand.nextInt(5)) {
                case 0: {
                    return 42;
                }
                case 1: {
                    return 45;
                }
                case 2: {
                    return 43;
                }
                case 3: {
                    return 88;
                }
                default: {
                    return 44;
                }
            }
        }
    }
    
    private static boolean moveBulkItemAsAction(final Action act, final Creature performer, final Item source, final Item target, final float counter) {
        final Item targetParent = target.getParentOrNull();
        final Item sourceParent = source.getParentOrNull();
        final byte sourceAuxByte = source.getAuxData();
        final byte sourceMaterialByte = source.getMaterial();
        final int sourceTemplateID = source.getRealTemplateId();
        final int playerAmountToTransfer = (int)act.getData();
        boolean done = false;
        boolean abortingEarly = false;
        if (!source.isBulkItem()) {
            performer.getCommunicator().sendNormalServerMessage("Uhh... this is for bulk items only.", (byte)3);
            if (Servers.localServer.testServer) {
                performer.getCommunicator().sendNormalServerMessage("Wrong source item! item=" + source);
            }
            return true;
        }
        Item moveTargetContainer;
        if (!target.isBulkContainer()) {
            if (targetParent == null || !targetParent.isBulkContainer()) {
                performer.getCommunicator().sendNormalServerMessage("Oh, that won't work at all.", (byte)3);
                if (Servers.localServer.testServer) {
                    performer.getCommunicator().sendNormalServerMessage("Wrong target of action! parent=" + targetParent);
                }
                return true;
            }
            moveTargetContainer = targetParent;
        }
        else {
            moveTargetContainer = target;
        }
        ItemTemplate sourceTemplate;
        try {
            sourceTemplate = ItemTemplateFactory.getInstance().getTemplate(sourceTemplateID);
        }
        catch (NoSuchTemplateException nst) {
            performer.getCommunicator().sendNormalServerMessage("ERROR: Could not find template for value " + sourceTemplateID + " source item = " + source);
            return true;
        }
        for (Item parent = target.getParentOrNull(); parent != null; parent = parent.getParentOrNull()) {
            if (parent != null && parent.getTemplateId() == 1315) {
                performer.getCommunicator().sendNormalServerMessage("The bulk storage bin is not allowed to contain items if it's in a Rack for Empty BSB");
                return true;
            }
        }
        if (sourceTemplateID < 0) {
            performer.getCommunicator().sendAlertServerMessage("ERROR! Source templace id is " + sourceTemplateID);
            return true;
        }
        if (target.isLocked()) {
            if (performer != null && !target.mayAccessHold(performer)) {
                performer.getCommunicator().sendNormalServerMessage("You're not allowed to put things into this " + target.getName() + ".");
                return true;
            }
        }
        else if (performer != null && !Methods.isActionAllowed(performer, (short)7, moveTargetContainer)) {
            return true;
        }
        final int mtcTemplateId = moveTargetContainer.getTemplateId();
        if (MethodsItems.checkIfStealing(source, performer, act) || MethodsItems.checkIfStealing(moveTargetContainer, performer, act)) {
            performer.getCommunicator().sendNormalServerMessage("You may not do that here.");
            abortingEarly = true;
            done = true;
        }
        else if (source.isOnSurface() != moveTargetContainer.isOnSurface() || performer.isOnSurface() != source.isOnSurface()) {
            performer.getCommunicator().sendNormalServerMessage("Everything needs to be on the same layer.");
            abortingEarly = true;
            done = true;
        }
        else if (sourceTemplate.isFood() && mtcTemplateId != 851 && mtcTemplateId != 852 && mtcTemplateId != 661) {
            performer.getCommunicator().sendNormalServerMessage("The " + source.getName() + " would be destroyed.");
            done = true;
        }
        else if (!sourceTemplate.isFood() && mtcTemplateId != 852 && mtcTemplateId != 851 && mtcTemplateId != 662 && mtcTemplateId != 1317) {
            performer.getCommunicator().sendNormalServerMessage("The " + source.getName() + " would be destroyed.");
            done = true;
        }
        else if (!moveTargetContainer.isCrate() && moveTargetContainer.getFreeVolume() < sourceTemplate.getVolume()) {
            performer.getCommunicator().sendNormalServerMessage("You can not even fit one of those.", (byte)3);
            abortingEarly = true;
            done = true;
        }
        else if (moveTargetContainer.isCrate() && moveTargetContainer.getRemainingCrateSpace() <= 0) {
            performer.getCommunicator().sendNormalServerMessage("You can not even fit one of those.", (byte)3);
            abortingEarly = true;
            done = true;
        }
        if (!done) {
            final int amountMayCarry = performer.getCarryingCapacityLeft() / sourceTemplate.getWeightGrams();
            final int maxMayFit = moveTargetContainer.isCrate() ? moveTargetContainer.getRemainingCrateSpace() : (moveTargetContainer.getFreeVolume() / sourceTemplate.getVolume());
            final int remainingItems = (int)Math.ceil(source.getBulkNumsFloat(false));
            boolean transferringTheLastPortion = false;
            if (amountMayCarry <= 0) {
                performer.getCommunicator().sendNormalServerMessage("You can not even carry one of those.", (byte)3);
                return true;
            }
            int amountToTransfer;
            if (remainingItems < amountMayCarry && remainingItems <= playerAmountToTransfer) {
                if (remainingItems > maxMayFit) {
                    amountToTransfer = maxMayFit;
                }
                else {
                    transferringTheLastPortion = true;
                    amountToTransfer = remainingItems;
                }
            }
            else if (remainingItems < amountMayCarry && remainingItems > playerAmountToTransfer) {
                amountToTransfer = playerAmountToTransfer;
            }
            else if (maxMayFit < amountMayCarry && maxMayFit <= playerAmountToTransfer) {
                amountToTransfer = maxMayFit;
            }
            else if (maxMayFit < amountMayCarry && maxMayFit > playerAmountToTransfer) {
                amountToTransfer = playerAmountToTransfer;
            }
            else if (playerAmountToTransfer < amountMayCarry) {
                amountToTransfer = playerAmountToTransfer;
            }
            else {
                amountToTransfer = amountMayCarry;
            }
            if ((moveTargetContainer.isCrate() && moveTargetContainer.getRemainingCrateSpace() < amountToTransfer) || (!moveTargetContainer.isCrate() && moveTargetContainer.getFreeVolume() < sourceTemplate.getWeightGrams())) {
                if (Servers.localServer.testServer) {
                    String msg = "";
                    if (moveTargetContainer.isCrate()) {
                        msg = moveTargetContainer.getRemainingCrateSpace() + " remaining crate spots, trying to move " + amountToTransfer + " items.";
                    }
                    else {
                        final String s1 = String.format("%,d", source.getWeightGrams());
                        final String m1 = String.format("%,d", moveTargetContainer.getFreeVolume());
                        msg = "Source Volume: " + s1 + ", Target free Volume: " + m1;
                    }
                    performer.getCommunicator().sendNormalServerMessage(msg);
                }
                performer.getCommunicator().sendNormalServerMessage("Target is full.");
                return true;
            }
            if (counter == 1.0f) {
                final String actionString = "move " + source.getName() + " from one bulk container to another";
                performer.getCommunicator().sendNormalServerMessage("You start to " + actionString + ".");
                final int actionTime = source.getBulkNums() / amountMayCarry * 10;
                act.setTimeLeft(actionTime);
                performer.sendActionControl(act.getActionString(), true, act.getTimeLeft());
                Server.getInstance().broadCastAction(performer.getName() + " starts to " + actionString, performer, 3);
                final String countString = (act.getData() == 2147483647L) ? ("a whole plethora (" + source.getBulkNums() + ") of") : ("" + act.getData());
                performer.getCommunicator().sendNormalServerMessage("Heave-ho, moving " + countString + " " + source.getName() + " at the rate of " + amountToTransfer + " per second!");
            }
            if (act.justTickedSecond()) {
                if (amountToTransfer < 1) {
                    if (Servers.localServer.testServer) {
                        performer.getCommunicator().sendAlertServerMessage("Something went wrong, amount to transfer is less than 1:  " + amountToTransfer);
                    }
                    performer.getCommunicator().sendNormalServerMessage("Not enough room in your inventory!");
                    return true;
                }
                Item tempItem = null;
                int countForMe = 0;
                for (final Item i : moveTargetContainer.getItemsAsArray()) {
                    if (i.getRealTemplateId() == sourceTemplateID && i.getData1() == source.getData1() && i.getData2() == source.getData2() && i.getAuxData() == sourceAuxByte && i.getMaterial() == sourceMaterialByte) {
                        tempItem = i;
                        ++countForMe;
                    }
                }
                if (countForMe > 1) {
                    performer.getCommunicator().sendAlertServerMessage("ERROR: Found more than one item of matching parameters, aborting.");
                    if (Servers.localServer.testServer) {
                        performer.getCommunicator().sendNormalServerMessage("Counter = " + countForMe);
                    }
                    return true;
                }
                final Item moveTargetItem = tempItem;
                performer.getStatus().modifyStamina(-600.0f);
                int totalVolume;
                if (transferringTheLastPortion) {
                    totalVolume = source.getWeightGrams();
                }
                else {
                    totalVolume = amountToTransfer * sourceTemplate.getVolume();
                }
                if ((moveTargetContainer.isCrate() || moveTargetContainer.getRemainingCrateSpace() < amountToTransfer) && (!moveTargetContainer.isBulkContainer() || moveTargetContainer.getFreeVolume() < totalVolume)) {
                    if (Servers.localServer.testServer) {
                        String msg2 = "Option 2: ";
                        if (moveTargetContainer.isCrate()) {
                            msg2 = moveTargetContainer.getRemainingCrateSpace() + " remaining crate spots, trying to move " + amountToTransfer + " items.";
                        }
                        else {
                            final String s2 = String.format("%,d", source.getWeightGrams());
                            final String m2 = String.format("%,d", moveTargetContainer.getFreeVolume());
                            msg2 = "Source Volume: " + s2 + ", Target free Volume: " + m2;
                        }
                        performer.getCommunicator().sendNormalServerMessage(msg2);
                    }
                    performer.getCommunicator().sendNormalServerMessage("Unnngh... not enough space!");
                    return true;
                }
                Label_2716: {
                    if (moveTargetItem == null) {
                        if (moveTargetItem == null) {
                            try {
                                if (amountToTransfer == 1) {
                                    int weightToSet;
                                    if (transferringTheLastPortion) {
                                        weightToSet = source.getWeightGrams();
                                    }
                                    else {
                                        weightToSet = sourceTemplate.getVolume();
                                    }
                                    final Item tempHolderItem = ItemFactory.createItem(669, source.getQualityLevel(), source.getMaterial(), (byte)0, performer.getName());
                                    source.setWeight(source.getWeightGrams() - weightToSet, true);
                                    tempHolderItem.setRealTemplate(sourceTemplateID);
                                    tempHolderItem.setWeight(weightToSet, true);
                                    tempHolderItem.setData1(source.getData1());
                                    tempHolderItem.setData2(source.getData2());
                                    tempHolderItem.setAuxData(source.getAuxData());
                                    if (source.usesFoodState()) {
                                        tempHolderItem.setName(source.getActualName());
                                        final ItemMealData imd = ItemMealData.getItemMealData(source.getWurmId());
                                        if (imd != null) {
                                            ItemMealData.save(source.getWurmId(), imd.getRecipeId(), imd.getCalories(), imd.getCarbs(), imd.getFats(), imd.getProteins(), imd.getBonus(), imd.getStages(), imd.getIngredients());
                                        }
                                    }
                                    tempHolderItem.setLastOwnerId(performer.getWurmId());
                                    moveTargetContainer.insertItem(tempHolderItem, true);
                                    moveTargetContainer.updateModelNameOnGroundItem();
                                    sourceParent.updateModelNameOnGroundItem();
                                    done = true;
                                }
                                else {
                                    if (source.getWeightGrams() < sourceTemplate.getVolume()) {
                                        performer.getCommunicator().sendAlertServerMessage("Something went wrong, source weight less than template even though transferring more than one item.......");
                                        return true;
                                    }
                                    final int modifiedTotalVolume = totalVolume - sourceTemplate.getVolume();
                                    final Item tempHolderItem = ItemFactory.createItem(669, source.getQualityLevel(), source.getMaterial(), (byte)0, performer.getName());
                                    tempHolderItem.setRealTemplate(sourceTemplateID);
                                    tempHolderItem.setWeight(sourceTemplate.getVolume(), true);
                                    tempHolderItem.setLastOwnerId(performer.getWurmId());
                                    final float sourceQL = source.getQualityLevel();
                                    final float targetQL = tempHolderItem.getQualityLevel();
                                    final float targetAmountItems = tempHolderItem.getBulkNumsFloat(false);
                                    float sourceAmountItems;
                                    if (transferringTheLastPortion) {
                                        sourceAmountItems = source.getBulkNumsFloat(false);
                                    }
                                    else {
                                        sourceAmountItems = amountToTransfer;
                                    }
                                    final float top = sourceQL * sourceAmountItems + targetQL * targetAmountItems;
                                    final float bottom = sourceAmountItems + targetAmountItems;
                                    final float finalQL = top / bottom;
                                    source.setWeight(source.getWeightGrams() - totalVolume, true);
                                    tempHolderItem.setWeight(tempHolderItem.getWeightGrams() + modifiedTotalVolume, true);
                                    tempHolderItem.setQualityLevel(finalQL);
                                    tempHolderItem.setData1(source.getData1());
                                    tempHolderItem.setData2(source.getData2());
                                    tempHolderItem.setAuxData(source.getAuxData());
                                    if (source.usesFoodState()) {
                                        tempHolderItem.setName(source.getActualName());
                                        final ItemMealData imd2 = ItemMealData.getItemMealData(source.getWurmId());
                                        if (imd2 != null) {
                                            ItemMealData.save(source.getWurmId(), imd2.getRecipeId(), imd2.getCalories(), imd2.getCarbs(), imd2.getFats(), imd2.getProteins(), imd2.getBonus(), imd2.getStages(), imd2.getIngredients());
                                        }
                                    }
                                    moveTargetContainer.insertItem(tempHolderItem, true);
                                    moveTargetContainer.updateModelNameOnGroundItem();
                                    sourceParent.updateModelNameOnGroundItem();
                                    if (transferringTheLastPortion) {
                                        done = true;
                                    }
                                }
                                break Label_2716;
                            }
                            catch (FailedException e) {
                                ItemBehaviour.logger.warning("FailEX on creating item for bulk transfer" + e);
                                performer.getCommunicator().sendAlertServerMessage("Uh-oh, something went wrong, if this persists, contact a developer.");
                                return true;
                            }
                            catch (NoSuchTemplateException ex) {
                                break Label_2716;
                            }
                        }
                        if (Servers.localServer.testServer) {
                            String msg2 = "Option 1: ";
                            if (moveTargetContainer.isCrate()) {
                                msg2 = moveTargetContainer.getRemainingCrateSpace() + " remaining crate spots, trying to move " + amountToTransfer + " items.";
                            }
                            else {
                                final String s2 = String.format("%,d", source.getWeightGrams());
                                final String m2 = String.format("%,d", moveTargetContainer.getFreeVolume());
                                msg2 = "Source Volume: " + s2 + ", Target free Volume: " + m2;
                            }
                            performer.getCommunicator().sendNormalServerMessage(msg2);
                        }
                        performer.getCommunicator().sendNormalServerMessage("Unnngh... not enough space!");
                        return true;
                    }
                    final float sourceQL2 = source.getQualityLevel();
                    final float targetQL2 = moveTargetItem.getQualityLevel();
                    final float targetAmountItems2 = moveTargetItem.getBulkNumsFloat(false);
                    float sourceAmountItems2;
                    if (transferringTheLastPortion) {
                        sourceAmountItems2 = source.getBulkNumsFloat(false);
                    }
                    else {
                        sourceAmountItems2 = amountToTransfer;
                    }
                    final float top2 = sourceQL2 * sourceAmountItems2 + targetQL2 * targetAmountItems2;
                    final float bottom2 = sourceAmountItems2 + targetAmountItems2;
                    final float finalQL2 = top2 / bottom2;
                    source.setWeight(source.getWeightGrams() - totalVolume, true);
                    moveTargetItem.setWeight(moveTargetItem.getWeightGrams() + totalVolume, true);
                    moveTargetItem.setQualityLevel(finalQL2);
                    moveTargetItem.setLastOwnerId(performer.getWurmId());
                    moveTargetContainer.updateModelNameOnGroundItem();
                    sourceParent.updateModelNameOnGroundItem();
                    if (transferringTheLastPortion) {
                        done = true;
                    }
                }
                act.setData(playerAmountToTransfer - amountToTransfer);
                if (done && moveTargetItem != null) {
                    moveTargetItem.setBusy(false);
                }
            }
        }
        if (act.getData() <= 0L || done) {
            if (source != null) {
                source.setBusy(false);
            }
            performer.getCommunicator().sendNormalServerMessage("Done!");
            return true;
        }
        return false;
    }
    
    static {
        logger = Logger.getLogger(ItemBehaviour.class.getName());
        conquers = new ConcurrentHashMap<Long, Long>();
        recipeRandom = new Random();
    }
}
