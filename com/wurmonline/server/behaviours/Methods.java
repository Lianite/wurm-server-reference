// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.behaviours;

import java.util.HashSet;
import java.util.List;
import java.util.Arrays;
import java.util.HashMap;
import com.wurmonline.server.questions.PlanBridgeQuestion;
import com.wurmonline.server.Point;
import com.wurmonline.server.questions.GMBuildAllWallsQuestion;
import com.wurmonline.server.structures.Structure;
import com.wurmonline.server.questions.GmSetMedPath;
import com.wurmonline.server.questions.GmSetTraits;
import com.wurmonline.server.questions.GmSetEnchants;
import com.wurmonline.server.questions.VoiceChatQuestion;
import com.wurmonline.server.items.ItemFactory;
import com.wurmonline.server.skills.Skill;
import com.wurmonline.server.questions.NewKingQuestion;
import com.wurmonline.server.skills.NoSuchSkillException;
import com.wurmonline.server.kingdom.Kingdoms;
import com.wurmonline.server.kingdom.King;
import com.wurmonline.server.ServerEntry;
import com.wurmonline.server.sounds.SoundPlayer;
import com.wurmonline.server.bodys.Wound;
import com.wurmonline.server.spells.Spell;
import com.wurmonline.server.players.Player;
import com.wurmonline.server.Message;
import com.wurmonline.server.economy.Shop;
import com.wurmonline.server.questions.ReputationQuestion;
import com.wurmonline.server.questions.ItemRestrictionManagement;
import com.wurmonline.server.players.Permissions;
import com.wurmonline.server.questions.CreatureDataQuestion;
import com.wurmonline.server.questions.ItemDataQuestion;
import com.wurmonline.server.questions.SinglePriceManageQuestion;
import com.wurmonline.server.questions.ManageAllianceQuestion;
import com.wurmonline.server.questions.FriendQuestion;
import com.wurmonline.server.questions.AllianceQuestion;
import com.wurmonline.server.questions.LearnSkillQuestion;
import com.wurmonline.server.WurmId;
import com.wurmonline.server.questions.TileDataQuestion;
import com.wurmonline.server.questions.ServerQuestion;
import com.wurmonline.server.questions.CreateZoneQuestion;
import com.wurmonline.server.questions.TwitSetupQuestion;
import com.wurmonline.server.questions.SetDeityQuestion;
import com.wurmonline.server.questions.PowerManagementQuestion;
import com.wurmonline.server.questions.PlayerPaymentQuestion;
import com.wurmonline.server.questions.PaymentQuestion;
import com.wurmonline.server.questions.HideQuestion;
import com.wurmonline.server.questions.ShutDownQuestion;
import com.wurmonline.server.questions.DeclareWarQuestion;
import com.wurmonline.server.questions.PeaceQuestion;
import com.wurmonline.server.villages.VillageRole;
import com.wurmonline.server.villages.Citizen;
import com.wurmonline.server.NoSuchPlayerException;
import com.wurmonline.server.creatures.NoSuchCreatureException;
import com.wurmonline.server.questions.SimplePopup;
import com.wurmonline.server.questions.VillageJoinQuestion;
import com.wurmonline.server.questions.AreaHistoryQuestion;
import com.wurmonline.server.questions.VillageHistoryQuestion;
import com.wurmonline.server.questions.VillageUpkeep;
import com.wurmonline.server.questions.VillageInfo;
import com.wurmonline.server.economy.Change;
import com.wurmonline.server.villages.GuardPlan;
import com.wurmonline.server.tutorial.MissionTriggers;
import com.wurmonline.server.economy.Economy;
import com.wurmonline.server.zones.Zone;
import com.wurmonline.server.structures.Fence;
import com.wurmonline.server.Items;
import java.io.IOException;
import com.wurmonline.server.zones.NoSuchZoneException;
import com.wurmonline.server.zones.Zones;
import com.wurmonline.server.villages.NoSuchVillageException;
import com.wurmonline.server.NoSuchItemException;
import com.wurmonline.server.questions.VillageCitizenManageQuestion;
import com.wurmonline.server.questions.GuardManagementQuestion;
import com.wurmonline.server.questions.GateManagementQuestion;
import com.wurmonline.server.questions.VillageRolesManageQuestion;
import com.wurmonline.server.questions.VillageSettingsManageQuestion;
import javax.annotation.Nullable;
import java.util.Iterator;
import java.util.Map;
import com.wurmonline.server.zones.VolaTile;
import com.wurmonline.server.villages.NoSuchRoleException;
import com.wurmonline.server.questions.VillageFoundationQuestion;
import com.wurmonline.server.Servers;
import com.wurmonline.server.villages.Village;
import com.wurmonline.server.villages.Villages;
import java.util.logging.Level;
import com.wurmonline.mesh.Tiles;
import com.wurmonline.server.Server;
import com.wurmonline.server.questions.RealDeathQuestion;
import com.wurmonline.server.Players;
import com.wurmonline.server.questions.AltarConversionQuestion;
import com.wurmonline.server.deities.Deity;
import com.wurmonline.server.questions.CreatureCreationQuestion;
import com.wurmonline.server.questions.WithdrawMoneyQuestion;
import com.wurmonline.server.questions.TerrainQuestion;
import com.wurmonline.server.questions.ItemCreationQuestion;
import com.wurmonline.server.questions.TeleportQuestion;
import com.wurmonline.server.Constants;
import java.util.Set;
import com.wurmonline.server.items.Item;
import com.wurmonline.server.creatures.Creature;
import java.util.logging.Logger;
import com.wurmonline.server.economy.MonetaryConstants;
import com.wurmonline.server.TimeConstants;
import com.wurmonline.server.villages.VillageStatus;
import com.wurmonline.shared.constants.SoundNames;
import com.wurmonline.shared.constants.ItemMaterials;
import com.wurmonline.shared.constants.CounterTypes;
import com.wurmonline.server.items.ItemTypes;
import com.wurmonline.server.questions.QuestionTypes;
import com.wurmonline.server.MiscConstants;

public final class Methods implements MiscConstants, QuestionTypes, ItemTypes, CounterTypes, ItemMaterials, SoundNames, VillageStatus, TimeConstants, MonetaryConstants
{
    private static final Logger logger;
    private static Creature jennElector;
    private static Creature hotsElector;
    private static Item molrStone;
    private static Set<Long> kingAspirants;
    
    static void sendTeleportQuestion(final Creature performer, final Item source) {
        final TeleportQuestion dq = new TeleportQuestion(performer, "Teleportation coordinates", "Set coordinates: x, 0-" + ((1 << Constants.meshSize) - 1) + " y, 0-" + ((1 << Constants.meshSize) - 1) + " or provide a player name.", source.getWurmId());
        dq.sendQuestion();
    }
    
    static void sendCreateQuestion(final Creature performer, final Item source) {
        final ItemCreationQuestion dq = new ItemCreationQuestion(performer, "Create Item", "Create the item of your liking:", source.getWurmId());
        dq.sendQuestion();
    }
    
    static void sendTerraformingQuestion(final Creature performer, final Item source, final int tilex, final int tiley) {
        final TerrainQuestion dq = new TerrainQuestion(performer, source, tilex, tiley);
        dq.sendQuestion();
    }
    
    static void sendWithdrawMoneyQuestion(final Creature performer, final Item token) {
        final WithdrawMoneyQuestion dq = new WithdrawMoneyQuestion(performer, "Withdraw money", "Withdraw selected amount:", token.getWurmId());
        dq.sendQuestion();
    }
    
    static void sendSummonQuestion(final Creature performer, final Item source, final int tilex, final int tiley, final long structureId) {
        final CreatureCreationQuestion cq = new CreatureCreationQuestion(performer, "Summon creature", "Summon the creature of your liking:", source.getWurmId(), tilex, tiley, performer.getLayer(), structureId);
        cq.sendQuestion();
    }
    
    static void sendAltarConversion(final Creature performer, final Item altar, final Deity deity) {
        final AltarConversionQuestion cq = new AltarConversionQuestion(performer, "Inscription", "Ancient inscription:", altar.getWurmId(), deity);
        cq.sendQuestion();
    }
    
    static void sendRealDeathQuestion(final Creature performer, final Item altar, final Deity deity) {
        if (Players.getChampionsFromKingdom(performer.getKingdomId(), deity.getNumber()) < 1) {
            if (Players.getChampionsFromKingdom(performer.getKingdomId()) < 3) {
                final RealDeathQuestion cq = new RealDeathQuestion(performer, "Real death", "Offer to become a Champion:", altar.getWurmId(), deity);
                cq.sendQuestion();
            }
            else {
                performer.getCommunicator().sendNormalServerMessage("Your kingdom does not support more champions right now.");
            }
        }
        else {
            performer.getCommunicator().sendNormalServerMessage(deity.name + " can not support another champion from your kingdom right now.");
        }
    }
    
    static void sendFoundVillageQuestion(final Creature performer, final Item deed) {
        if (!performer.isOnSurface()) {
            performer.getCommunicator().sendSafeServerMessage("You cannot found a settlement here below the surface.");
            return;
        }
        final VolaTile tile = performer.getCurrentTile();
        if (tile != null) {
            final int tx = tile.tilex;
            final int ty = tile.tiley;
            final int tt = Server.surfaceMesh.getTile(tx, ty);
            if (Tiles.decodeType(tt) == Tiles.Tile.TILE_LAVA.id || Tiles.isMineDoor(Tiles.decodeType(tt))) {
                performer.getCommunicator().sendSafeServerMessage("You cannot found a settlement here.");
                return;
            }
            for (int x = -1; x <= 1; ++x) {
                for (int y = -1; y <= 1; ++y) {
                    final int t = Server.surfaceMesh.getTile(tx + x, ty + y);
                    if (Tiles.decodeHeight(t) < 0) {
                        performer.getCommunicator().sendSafeServerMessage("You cannot found a settlement here. Too close to water.");
                        return;
                    }
                }
            }
        }
        else if (tile == null) {
            performer.getCommunicator().sendSafeServerMessage("You cannot found a settlement here.");
            Methods.logger.log(Level.WARNING, performer.getName() + " no tile when founding deed.");
            return;
        }
        final Map<Village, String> decliners = Villages.canFoundVillage(5, 5, 5, 5, tile.tilex, tile.tiley, 0, true, null, performer);
        if (!decliners.isEmpty()) {
            performer.getCommunicator().sendSafeServerMessage("You cannot found the settlement here:");
            for (final Village vill : decliners.keySet()) {
                final String reason = decliners.get(vill);
                if (reason.startsWith("has perimeter")) {
                    performer.getCommunicator().sendSafeServerMessage(vill.getName() + " " + reason);
                }
                else {
                    performer.getCommunicator().sendSafeServerMessage("Some settlement nearby " + reason);
                }
            }
            return;
        }
        if (deed.isNewDeed() || Servers.localServer.testServer || deed.getTemplateId() == 862) {
            final Village village = performer.getCitizenVillage();
            try {
                if (village != null && village.getCitizen(performer.getWurmId()).getRole() == village.getRoleForStatus((byte)2)) {
                    performer.getCommunicator().sendNormalServerMessage("You cannot found another settlement while being mayor in one. Give away one of the deeds.");
                }
                else {
                    final VillageFoundationQuestion vf = new VillageFoundationQuestion(performer, "Settlement Application Form", "Welcome to the Settlement Application Form", deed.getWurmId());
                    if (vf != null) {
                        final float rot = Creature.normalizeAngle(performer.getStatus().getRotation() + 45.0f);
                        vf.dir = (byte)((int)(rot / 90.0f) * 2);
                        vf.sendIntro();
                    }
                }
            }
            catch (NoSuchRoleException nsr) {
                Methods.logger.log(Level.WARNING, nsr.getMessage(), nsr);
                performer.getCommunicator().sendNormalServerMessage("Failed to locate the mayor role for that request. Please contact administration.");
            }
        }
    }
    
    static void sendManageVillageSettingsQuestion(final Creature performer, @Nullable final Item deed) {
        final long wId = (deed == null) ? -10L : deed.getWurmId();
        final VillageSettingsManageQuestion vs = new VillageSettingsManageQuestion(performer, "Manage settings", "Managing settings.", wId);
        if (vs != null) {
            vs.sendQuestion();
        }
    }
    
    static void sendManageVillageRolesQuestion(final Creature performer, @Nullable final Item deed) {
        final long wId = (deed == null) ? -10L : deed.getWurmId();
        final VillageRolesManageQuestion vs = new VillageRolesManageQuestion(performer, "Manage roles", "Managing roles and titles.", wId);
        if (vs != null) {
            vs.sendQuestion();
        }
    }
    
    static void sendManageVillageGatesQuestion(final Creature performer, @Nullable final Item deed) {
        final long wId = (deed == null) ? -10L : deed.getWurmId();
        final GateManagementQuestion vs = new GateManagementQuestion(performer, "Manage gates", "Managing gates.", wId);
        if (vs != null) {
            vs.sendQuestion();
        }
    }
    
    static void sendManageVillageGuardsQuestion(final Creature performer, @Nullable final Item deed) {
        final long wId = (deed == null) ? -10L : deed.getWurmId();
        final GuardManagementQuestion gm = new GuardManagementQuestion(performer, "Guard management", "Manage guards", wId);
        gm.sendQuestion();
    }
    
    static void sendManageVillageCitizensQuestion(final Creature performer, @Nullable final Item deed) {
        final long wId = (deed == null) ? -10L : deed.getWurmId();
        final VillageCitizenManageQuestion vc = new VillageCitizenManageQuestion(performer, "Citizen management", "Set statuses of citizens.", wId);
        vc.setSelecting(true);
        vc.sendQuestion();
    }
    
    static void sendExpandVillageQuestion(final Creature performer, @Nullable final Item deed) {
        try {
            long dId = -10L;
            Village village;
            if (deed == null) {
                village = performer.getCitizenVillage();
                dId = village.getDeedId();
            }
            else {
                dId = deed.getWurmId();
                final int oldVill = deed.getData2();
                village = Villages.getVillage(oldVill);
            }
            try {
                final long coolDown = System.currentTimeMillis() - 3600000L;
                if (coolDown < village.getToken().getLastOwnerId()) {
                    performer.getCommunicator().sendNormalServerMessage("The settlement has been attacked, or been under siege recently. You need to wait " + Server.getTimeFor(village.getToken().getLastOwnerId() - coolDown) + ".");
                    return;
                }
            }
            catch (NoSuchItemException e) {
                e.printStackTrace();
            }
            if (village.isDisbanding()) {
                performer.getCommunicator().sendNormalServerMessage("This settlement is disbanding. You can not change these settings now.");
                return;
            }
            if (village.plan.isUnderSiege()) {
                performer.getCommunicator().sendNormalServerMessage("This settlement is under siege. You can not change these settings now.");
                return;
            }
            if (village.isActionAllowed((short)76, performer)) {
                final VillageFoundationQuestion vf = new VillageFoundationQuestion(performer, "Settlement Size", "Stage One - The size of your settlement", dId);
                if (vf != null) {
                    vf.setSequence(1);
                    vf.tokenx = village.getTokenX();
                    vf.tokeny = village.getTokenY();
                    vf.surfaced = village.isOnSurface();
                    vf.initialPerimeter = village.getPerimeterSize();
                    vf.democracy = village.isDemocracy();
                    vf.spawnKingdom = village.kingdom;
                    vf.motto = village.getMotto();
                    vf.villageName = village.getName();
                    vf.selectedWest = vf.tokenx - village.getStartX();
                    vf.selectedEast = village.getEndX() - vf.tokenx;
                    vf.selectedNorth = vf.tokeny - village.getStartY();
                    vf.selectedSouth = village.getEndY() - vf.tokeny;
                    final float rot = Creature.normalizeAngle(performer.getStatus().getRotation() + 45.0f);
                    vf.dir = (byte)((int)(rot / 90.0f) * 2);
                    vf.selectedGuards = village.plan.getNumHiredGuards();
                    vf.setSize();
                    vf.checkDeedItem();
                    vf.expanding = true;
                    vf.sendQuestion();
                }
            }
            else {
                performer.getCommunicator().sendNormalServerMessage("You are not allowed to use this deed here. Ask the mayor to set the permissions on the management deed to allow you to expand.");
            }
        }
        catch (NoSuchVillageException nsv) {
            performer.getCommunicator().sendNormalServerMessage("Failed to localize the settlement for that deed.");
        }
    }
    
    static void setVillageToken(final Creature performer, final Item token) {
        final int tilex = performer.getTileX();
        final int tiley = performer.getTileY();
        final Village village = Zones.getVillage(tilex, tiley, performer.isOnSurface());
        if (village == null) {
            performer.getCommunicator().sendNormalServerMessage("No settlement here. You cannot plant the token.");
        }
        else {
            final VolaTile tile = Zones.getTileOrNull(tilex, tiley, performer.isOnSurface());
            if (tile != null) {
                if (tile.getStructure() != null) {
                    performer.getCommunicator().sendNormalServerMessage("You cannot plant the token inside.");
                    return;
                }
                final Fence[] fences = tile.getFencesForLevel(0);
                if (fences.length > 0) {
                    performer.getCommunicator().sendNormalServerMessage("You cannot plant the token on fences and walls.");
                    return;
                }
            }
            Item oldToken = null;
            try {
                oldToken = village.getToken();
            }
            catch (NoSuchItemException ex) {}
            try {
                village.setTokenId(token.getWurmId());
                token.setData2(village.getId());
                final Item parent = token.getParent();
                parent.dropItem(token.getWurmId(), false);
                try {
                    token.setPosXY(performer.getStatus().getPositionX(), performer.getStatus().getPositionY());
                    final Zone zone = Zones.getZone(tilex, tiley, performer.isOnSurface());
                    zone.addItem(token);
                }
                catch (NoSuchZoneException nsz) {
                    Methods.logger.log(Level.WARNING, nsz.getMessage(), nsz);
                    performer.getCommunicator().sendNormalServerMessage("You can't place the token here.");
                    return;
                }
            }
            catch (IOException iox) {
                Methods.logger.log(Level.WARNING, "Village: " + village.getId() + ", token: " + token.getWurmId() + " - " + iox.getMessage(), iox);
                performer.getCommunicator().sendNormalServerMessage("A server error occured. Please report this.");
                return;
            }
            catch (NoSuchItemException nsi) {
                Methods.logger.log(Level.WARNING, "Village: " + village.getId() + ", token: " + token.getWurmId() + " - " + nsi.getMessage(), nsi);
                performer.getCommunicator().sendNormalServerMessage("A server error occured. Please report this.");
                return;
            }
            if (oldToken != null) {
                Items.destroyItem(oldToken.getWurmId());
            }
        }
    }
    
    static final boolean drainCoffers(final Creature performer, final Village village, final float counter, final Item token, final Action act) {
        boolean done = true;
        if (!performer.isFriendlyKingdom(village.kingdom) || village.isEnemy(performer.getCitizenVillage())) {
            if (village.guards != null && village.guards.size() > 0) {
                performer.getCommunicator().sendNormalServerMessage("The guards prevent you from draining the coffers.");
                done = true;
            }
            else {
                if (token.isOnSurface() != performer.isOnSurface()) {
                    performer.getCommunicator().sendNormalServerMessage("You can't reach the " + token.getName() + " now.");
                    return true;
                }
                if (!performer.isWithinDistanceTo(token.getPosX(), token.getPosY(), token.getPosZ(), 2.0f)) {
                    performer.getCommunicator().sendNormalServerMessage("You are too far away from the " + token.getName() + " to do that.");
                    return true;
                }
                if (token.getFloorLevel() != performer.getFloorLevel()) {
                    performer.getCommunicator().sendNormalServerMessage("You must be on the same floor level as the " + token.getName() + ".");
                    return true;
                }
                final GuardPlan plan = village.plan;
                if (plan != null) {
                    final long nextDrain = plan.getTimeToNextDrain();
                    if (nextDrain < 0L) {
                        final long moneyDrained = plan.getMoneyDrained();
                        if (moneyDrained > 1L) {
                            done = false;
                            final boolean insta = performer.getPower() >= 5;
                            int time = 300;
                            if (counter == 1.0f) {
                                act.setTimeLeft(time);
                                performer.getCommunicator().sendNormalServerMessage("You start to search for gold in the coffers of " + village.getName() + ".");
                                Server.getInstance().broadCastAction(performer.getName() + " starts to rummage through the coffers of " + village.getName() + ", looking for coins.", performer, 5);
                                performer.sendActionControl(Actions.actionEntrys[350].getVerbString(), true, time);
                            }
                            else {
                                time = act.getTimeLeft();
                            }
                            if (counter * 10.0f > time || insta) {
                                done = true;
                                final Change change = Economy.getEconomy().getChangeFor(moneyDrained / 2L);
                                performer.getCommunicator().sendNormalServerMessage("You find " + change.getChangeString() + " in the coffers of " + village.getName() + ".");
                                Server.getInstance().broadCastAction(performer.getName() + " proudly displays the " + change.getChangeString() + " " + performer.getHeSheItString() + " found in the coffers of " + village.getName() + ".", performer, 5);
                                final Item[] coins2;
                                final Item[] coins = coins2 = Economy.getEconomy().getCoinsFor(moneyDrained / 2L);
                                for (final Item lCoin : coins2) {
                                    performer.getInventory().insertItem(lCoin, true);
                                }
                                plan.drainMoney();
                                performer.achievement(45);
                                village.addHistory(performer.getName(), "drained " + change.getChangeString() + " from the coffers.");
                                if (village.plan != null && village.plan.getNumHiredGuards() >= 5 && performer.isChampion() && village.kingdom != performer.getKingdomId()) {
                                    performer.modifyChampionPoints(3);
                                    Servers.localServer.createChampTwit(performer.getName() + " drains " + village.getName() + " and gains 3 champion points");
                                }
                                final boolean enemyHomeServer = Servers.localServer.isChallengeOrEpicServer() && Servers.localServer.HOMESERVER && Servers.localServer.KINGDOM != performer.getKingdomId();
                                if (!Servers.localServer.HOMESERVER || enemyHomeServer) {
                                    MissionTriggers.activateTriggers(performer, -1, 350, token.getWurmId(), 1);
                                }
                            }
                        }
                        else {
                            performer.getCommunicator().sendNormalServerMessage("There is no money to steal in the coffers of " + village.getName() + ".");
                        }
                    }
                    else {
                        performer.getCommunicator().sendNormalServerMessage("The coffer timelock has been activated. Try again in " + Server.getTimeFor(nextDrain) + ".");
                    }
                }
                else {
                    performer.getCommunicator().sendNormalServerMessage("The coffers of " + village.getName() + " echo hollowly.");
                }
            }
        }
        else {
            performer.getCommunicator().sendNormalServerMessage("You are not an enemy of " + village.getName() + ".");
        }
        return done;
    }
    
    static void sendVillageInfo(final Creature performer, @Nullable final Item villageToken) {
        final long wId = (villageToken == null) ? -10L : villageToken.getWurmId();
        final VillageInfo info = new VillageInfo(performer, "Settlement billboard", "", wId);
        info.sendQuestion();
    }
    
    static void sendManageUpkeep(final Creature performer, @Nullable final Item villageToken) {
        final long wId = (villageToken == null) ? -10L : villageToken.getWurmId();
        final VillageUpkeep upkeep = new VillageUpkeep(performer, "Settlement Upkeep", "", wId);
        upkeep.sendQuestion();
    }
    
    static void sendVillageHistory(final Creature performer, @Nullable final Item villageToken) {
        final long wId = (villageToken == null) ? -10L : villageToken.getWurmId();
        final VillageHistoryQuestion info = new VillageHistoryQuestion(performer, "Settlement history", "", wId);
        info.sendQuestion();
    }
    
    static void sendAreaHistory(final Creature performer, @Nullable final Item villageToken) {
        final long wId = (villageToken == null) ? -10L : villageToken.getWurmId();
        final AreaHistoryQuestion info = new AreaHistoryQuestion(performer, "Area history", "", wId);
        info.sendQuestion();
    }
    
    public static void sendJoinVillageQuestion(final Creature performer, final Creature invited) {
        if (performer.getKingdomId() == invited.getKingdomId()) {
            final Village vill = invited.getCitizenVillage();
            if (vill != null) {
                final Citizen citiz = vill.getCitizen(invited.getWurmId());
                final VillageRole role = citiz.getRole();
                if (role.getStatus() == 2) {
                    performer.getCommunicator().sendNormalServerMessage(invited.getName() + " is the mayor of " + vill.getName() + ". He can't join another settlement.");
                    return;
                }
            }
            try {
                final Village village = performer.getCitizenVillage();
                if (village != null && village.acceptsNewCitizens()) {
                    if (village.kingdom != 3 && invited.getReputation() < 0) {
                        performer.getCommunicator().sendNormalServerMessage(invited.getName() + " has negative reputation and may not join a settlement now.");
                        return;
                    }
                    final VillageJoinQuestion vj = new VillageJoinQuestion(performer, "Settlement invitation", "Invitation to become citizen of a settlement.", invited.getWurmId());
                    vj.sendQuestion();
                    performer.getCommunicator().sendNormalServerMessage("You invite " + invited.getName() + " to join " + village.getName() + ".");
                }
                else {
                    final SimplePopup pp = new SimplePopup(performer, "Max citizens reached", "The settlement does not accept more citizens right now");
                    pp.setToSend("Every settlement has a maximum amount of citizens depending on their size. You may unlimit the amount of allowed citizens in the citizen management or settlement management forms. As long as " + village.getName() + " has more than " + village.getMaxCitizens() + " player citizens your upkeep is doubled.");
                    pp.sendQuestion();
                }
            }
            catch (NoSuchCreatureException nsc) {
                Methods.logger.log(Level.WARNING, "Failed to locate creature " + invited.getName(), nsc);
                performer.getCommunicator().sendNormalServerMessage("Failed to locate the creature for that invitation.");
            }
            catch (NoSuchPlayerException nsp) {
                Methods.logger.log(Level.WARNING, "Failed to locate player " + invited.getName(), nsp);
                performer.getCommunicator().sendNormalServerMessage("Failed to locate the player for that invitation.");
            }
        }
    }
    
    static void sendVillagePeaceQuestion(final Creature performer, final Creature invited) {
        final Village vill = invited.getCitizenVillage();
        if (vill != null) {
            if (!vill.mayDoDiplomacy(invited)) {
                performer.getCommunicator().sendNormalServerMessage(invited.getName() + " may not do diplomacy in the name of " + vill.getName() + ". He cannot give you peace.");
                return;
            }
            final Village village = performer.getCitizenVillage();
            if (village != null) {
                if (!village.mayDoDiplomacy(performer)) {
                    performer.getCommunicator().sendNormalServerMessage("You may not do diplomacy in the name of " + village.getName() + ". You cannot offer peace.");
                    return;
                }
                try {
                    final PeaceQuestion pq = new PeaceQuestion(performer, "Peace offer", "Will you accept peace?", invited.getWurmId());
                    pq.sendQuestion();
                }
                catch (NoSuchCreatureException nsc) {
                    Methods.logger.log(Level.WARNING, "Failed to locate creature " + invited.getName(), nsc);
                    performer.getCommunicator().sendNormalServerMessage("Failed to locate the creature for that invitation.");
                }
                catch (NoSuchPlayerException nsp) {
                    Methods.logger.log(Level.WARNING, "Failed to locate player " + invited.getName(), nsp);
                    performer.getCommunicator().sendNormalServerMessage("Failed to locate the player for that invitation.");
                }
            }
        }
    }
    
    static void sendWarDeclarationQuestion(final Creature performer, final Village targetVillage) {
        if (targetVillage != null) {
            final Village village = performer.getCitizenVillage();
            if (village != null) {
                if (!village.mayDoDiplomacy(performer)) {
                    performer.getCommunicator().sendNormalServerMessage("You may not do diplomacy in the name of " + village.getName() + ". You cannot declare war.");
                    return;
                }
                try {
                    final DeclareWarQuestion pq = new DeclareWarQuestion(performer, "War declaration", "Will you declare war?", targetVillage.getId());
                    pq.sendQuestion();
                }
                catch (NoSuchCreatureException nsc) {
                    Methods.logger.log(Level.WARNING, "Failed to locate creature " + performer.getName(), nsc);
                    performer.getCommunicator().sendNormalServerMessage("Failed to locate the creature for that declaration.");
                }
                catch (NoSuchPlayerException nsp) {
                    Methods.logger.log(Level.WARNING, "Failed to locate player " + performer.getName(), nsp);
                    performer.getCommunicator().sendNormalServerMessage("Failed to locate the player for that declaration.");
                }
                catch (NoSuchVillageException nsp2) {
                    Methods.logger.log(Level.WARNING, "Failed to locate village " + targetVillage.getName(), nsp2);
                    performer.getCommunicator().sendNormalServerMessage("Failed to locate the village for that declaration.");
                }
            }
        }
    }
    
    static void sendShutdownQuestion(final Creature performer, final Item wand) {
        final ShutDownQuestion vs = new ShutDownQuestion(performer, "Shutting down the server", "Select the number of minutes and seconds to shutdown as well as the reason for it.", wand.getWurmId());
        vs.sendQuestion();
    }
    
    static void sendHideQuestion(final Creature performer, final Item wand, final Item target) {
        final HideQuestion hs = new HideQuestion(performer, "Hiding " + target.getName(), "Do you wish to hide the " + target.getName() + "?", target.getWurmId());
        hs.sendQuestion();
    }
    
    static void sendPaymentQuestion(final Creature performer, final Item wand) {
        final PaymentQuestion vs = new PaymentQuestion(performer, "Setting payment expiretime for a player.", "Select the number of days and months before the subscription expires.", wand.getWurmId());
        vs.sendQuestion();
    }
    
    static void sendPlayerPaymentQuestion(final Creature performer) {
        final PlayerPaymentQuestion vs = new PlayerPaymentQuestion(performer);
        vs.sendQuestion();
    }
    
    static void sendPowerManagementQuestion(final Creature performer, final Item wand) {
        final PowerManagementQuestion vs = new PowerManagementQuestion(performer, "Setting the power status for a player.", "Set the power of the player to the selected level.", wand.getWurmId());
        vs.sendQuestion();
    }
    
    static void sendFaithManagementQuestion(final Creature performer, final Item wand) {
        final SetDeityQuestion dq = new SetDeityQuestion(performer, "Setting the deity for a player.", "Set the deity of the player.", wand.getWurmId());
        dq.sendQuestion();
    }
    
    static void sendConfigureTwitter(final Creature performer, final long target, final boolean village, final String name) {
        final TwitSetupQuestion twq = new TwitSetupQuestion(performer, "Twitter", "Configure Twitter for " + name, target, village);
        twq.sendQuestion();
    }
    
    static void sendCreateZone(final Creature performer) {
        final CreateZoneQuestion twq = new CreateZoneQuestion(performer);
        twq.sendQuestion();
    }
    
    static void sendServerManagementQuestion(final Creature performer, final long target) {
        final ServerQuestion dq = new ServerQuestion(performer, "Wurm servers.", "Wurm servers management", target);
        dq.sendQuestion();
    }
    
    static void sendTileDataQuestion(final Creature performer, final Item wand, final int tilex, final int tiley) {
        final TileDataQuestion dq = new TileDataQuestion(performer, "Setting data for tile at " + tilex + ", " + tiley, "Set the data:", tilex, tiley, wand.getWurmId());
        dq.sendQuestion();
    }
    
    static void sendLearnSkillQuestion(final Creature performer, final Item wand, final long target) {
        Creature creature = null;
        Label_0057: {
            if (WurmId.getType(target) != 1) {
                if (WurmId.getType(target) != 0) {
                    break Label_0057;
                }
            }
            try {
                creature = Server.getInstance().getCreature(target);
            }
            catch (NoSuchCreatureException nsc) {
                performer.getCommunicator().sendNormalServerMessage("Failed to locate the creature for that request!");
            }
            catch (NoSuchPlayerException nsp) {
                performer.getCommunicator().sendNormalServerMessage("Failed to locate the player for that request!");
            }
        }
        LearnSkillQuestion ls = null;
        if (creature != null) {
            ls = new LearnSkillQuestion(performer, "Imbue with skill", "Set the skill of " + creature.getName() + " to the value of your choice:", target);
        }
        else {
            ls = new LearnSkillQuestion(performer, "Set your skill", "Set or learn a skill:", target);
        }
        ls.sendQuestion();
    }
    
    static final void sendAllianceQuestion(final Creature performer, final Creature target) {
        if (!target.isFighting() && target.hasLink()) {
            final AllianceQuestion aq = new AllianceQuestion(target, "Alliance invitation", "Request to form a village alliance:", performer.getWurmId());
            aq.sendQuestion();
            performer.getCommunicator().sendNormalServerMessage("You send an elaborate invitation to form a high and mighty alliance to " + target.getName() + ".");
        }
        else {
            performer.getCommunicator().sendNormalServerMessage(target.getName() + " does not answer questions right now.");
        }
    }
    
    static final void sendFriendQuestion(final Creature performer, final Creature target) {
        if (performer.getKingdomId() == target.getKingdomId()) {
            if (!target.isFighting() && target.hasLink()) {
                final FriendQuestion fq = new FriendQuestion(target, "Friend list invitation", "Request to add you to the friend list:", performer.getWurmId());
                fq.sendQuestion();
                performer.getCommunicator().sendNormalServerMessage("You ask " + target.getName() + " for permission to add " + target.getHimHerItString() + " to your friends list.");
            }
            else {
                performer.getCommunicator().sendNormalServerMessage(target.getName() + " does not answer questions right now.");
            }
        }
    }
    
    static final void sendManageAllianceQuestion(final Creature performer, @Nullable final Item villageToken) {
        final long wId = (villageToken == null) ? -10L : villageToken.getWurmId();
        final ManageAllianceQuestion aq = new ManageAllianceQuestion(performer, "Manage alliances", "Select an alliance to break:", wId);
        aq.sendQuestion();
    }
    
    static final void sendSinglePriceQuestion(final Creature responder, final Item target) {
        final SinglePriceManageQuestion spm = new SinglePriceManageQuestion(responder, "Price management", "Set the desired price:", target.getWurmId());
        spm.sendQuestion();
    }
    
    static final void sendSetDataQuestion(final Creature responder, final Item target) {
        final ItemDataQuestion spm = new ItemDataQuestion(responder, "Item data", "Set the desired data:", target.getWurmId());
        spm.sendQuestion();
    }
    
    static final void sendSetDataQuestion(final Creature responder, final Creature target) {
        final CreatureDataQuestion spm = new CreatureDataQuestion(responder, target);
        spm.sendQuestion();
    }
    
    static final void sendItemRestrictionManagement(final Creature responder, final Permissions.IAllow target, final long wurmId) {
        final ItemRestrictionManagement irm = new ItemRestrictionManagement(responder, target, wurmId);
        irm.sendQuestion();
    }
    
    static final void sendReputationManageQuestion(final Creature responder, @Nullable final Item target) {
        final long wId = (target == null) ? -10L : target.getWurmId();
        final ReputationQuestion spm = new ReputationQuestion(responder, "Reputation management", "Set the reputation levels:", wId);
        spm.sendQuestion();
    }
    
    public static final boolean discardSellItem(final Creature performer, final Action act, final Item discardItem, final float counter) {
        boolean toReturn = false;
        String message = "That item cannot be sold this way.";
        if (discardItem.isNoDiscard() || discardItem.isTemporary()) {
            if (act.getNumber() == 600) {
                message = "That item cannot be discarded.";
            }
            performer.getCommunicator().sendNormalServerMessage(message);
            return true;
        }
        if (discardItem.getOwnerId() != performer.getWurmId()) {
            performer.getCommunicator().sendNormalServerMessage("You need to carry the item in order to sell it.");
            return true;
        }
        if (discardItem.isInstaDiscard()) {
            if (act.getNumber() == 600) {
                performer.getCommunicator().sendNormalServerMessage("You break it down in little pieces and throw it away.");
                Items.destroyItem(discardItem.getWurmId());
                return true;
            }
            performer.getCommunicator().sendNormalServerMessage(message);
            return true;
        }
        else {
            if (performer.getMoneyEarnedBySellingLastHour() > 500L) {
                performer.getCommunicator().sendNormalServerMessage("You have sold your quota for now.");
                return true;
            }
            if (counter == 1.0f) {
                final Shop kingsShop = Economy.getEconomy().getKingsShop();
                if (kingsShop.getMoney() <= 100000L) {
                    performer.getCommunicator().sendNormalServerMessage("There are apparently no coins in the coffers at the moment.");
                    return true;
                }
                final int time = 30;
                performer.sendActionControl("Selling", true, 30);
                if (Constants.maintaining) {
                    performer.getCommunicator().sendNormalServerMessage("The server is shutting down so the shop is closed for now.");
                    return true;
                }
            }
            if (counter > 3.0f) {
                toReturn = true;
                final Shop kingsShop = Economy.getEconomy().getKingsShop();
                if (kingsShop.getMoney() <= 100000L) {
                    performer.getCommunicator().sendNormalServerMessage("There are apparently no coins in the coffers at the moment.");
                    return true;
                }
                long percentMod = 0L;
                if (!Servers.localServer.HOMESERVER) {
                    if (Server.rand.nextFloat() < Zones.getPercentLandForKingdom(performer.getKingdomId()) / 100.0f) {
                        percentMod = 1L;
                    }
                    if (Server.rand.nextInt(10) < Items.getBattleCampControl(performer.getKingdomId())) {
                        ++percentMod;
                    }
                }
                final long value = (long)discardItem.getCurrentQualityLevel() / 10L + 1L + percentMod;
                performer.addMoneyEarnedBySellingLastHour(value);
                kingsShop.setMoney(kingsShop.getMoney() - value);
                Items.destroyItem(discardItem.getWurmId());
                if (performer.checkCoinAward(1000)) {
                    performer.getCommunicator().sendSafeServerMessage("The king awards you with a rare coin!");
                }
            }
            return toReturn;
        }
    }
    
    static final boolean disbandVillage(final Creature performer, final Item villageToken, final float counter) {
        boolean insta = false;
        boolean toReturn = true;
        boolean settings = false;
        if (performer.getPower() > 3) {
            insta = true;
        }
        try {
            final Village vill = Villages.getVillage(villageToken.getData2());
            if ((vill == performer.getCitizenVillage() && vill.isActionAllowed((short)348, performer)) || insta) {
                settings = true;
            }
            if (!vill.isDisbanding()) {
                if (settings) {
                    toReturn = false;
                    if (counter == 1.0f && !insta) {
                        final int time = 3000;
                        performer.sendActionControl(Actions.actionEntrys[348].getVerbString(), true, 3000);
                        performer.getCommunicator().sendNormalServerMessage("You start to disband the village of " + vill.getName() + ".");
                        Server.getInstance().broadCastAction(performer.getName() + " starts to disband the village of " + vill.getName() + ".", performer, 5);
                        vill.broadCastMessage(new Message(performer, (byte)3, "Village", "<" + performer.getName() + "> starts to disband the village of " + vill.getName() + "."));
                        vill.broadCastAlert("Any traders who are citizens of " + vill.getName() + " will disband without refund!");
                    }
                    if (counter > 300.0f || insta || vill.getMayor().getId() == performer.getWurmId()) {
                        toReturn = true;
                        if (insta) {
                            vill.disband(performer.getName());
                        }
                        else {
                            vill.startDisbanding(performer, performer.getName(), performer.getWurmId());
                            if (vill.getMayor().getId() == performer.getWurmId() && vill.getDiameterX() < 30 && vill.getDiameterY() < 30) {
                                performer.getCommunicator().sendNormalServerMessage("Your settlement is disbanding. It will be disbanded in about an hour.");
                                if (!Servers.localServer.isFreeDeeds() || Servers.localServer.isUpkeep()) {
                                    performer.getCommunicator().sendAlertServerMessage("Do not change server during this process. You may not receive the money from the coffers in that case.");
                                }
                                if (Servers.localServer.isFreeDeeds() && Servers.localServer.isUpkeep() && vill.getCreationDate() < System.currentTimeMillis() + 2419200000L) {
                                    performer.getCommunicator().sendAlertServerMessage("Free deeding is enabled and your settlement is less than 30 days old. If you disband now, you will not receive a refund.");
                                }
                                Server.getInstance().broadCastAction(performer.getName() + " has set " + vill.getName() + " to disband immediately.", performer, 5);
                                vill.broadCastMessage(new Message(performer, (byte)3, "Village", "<" + performer.getName() + "> has set " + vill.getName() + " to disband immediately."));
                                final Village[] allies3;
                                final Village[] allies = allies3 = vill.getAllies();
                                for (final Village lAllie : allies3) {
                                    lAllie.broadCastMessage(new Message(performer, (byte)3, "Village", "<" + performer.getName() + "> has set " + vill.getName() + " to disband immediately."));
                                }
                            }
                            else {
                                final String hours = "24 hours";
                                performer.getCommunicator().sendNormalServerMessage(vill.getName() + " will disband in " + "24 hours" + ".");
                                if (Servers.localServer.isUpkeep() || !Servers.localServer.isFreeDeeds()) {
                                    performer.getCommunicator().sendNormalServerMessage("If the mayor is still on the same server when the deed disbands he or she should receive part of the money that is left in the coffers.");
                                }
                                Server.getInstance().broadCastAction(performer.getName() + " has set " + vill.getName() + " to disband in " + "24 hours" + ".", performer, 5);
                                vill.broadCastMessage(new Message(performer, (byte)3, "Village", "<" + performer.getName() + "> has set " + vill.getName() + " to disband in " + "24 hours" + "."));
                                final Village[] allies4;
                                final Village[] allies2 = allies4 = vill.getAllies();
                                for (final Village lAllie2 : allies4) {
                                    lAllie2.broadCastMessage(new Message(performer, (byte)3, "Village", "<" + performer.getName() + "> has set " + vill.getName() + " to disband in " + "24 hours" + "."));
                                }
                            }
                        }
                    }
                }
                else {
                    performer.getCommunicator().sendNormalServerMessage(vill.getName() + " may not be disbanded right now.");
                }
            }
            else {
                performer.getCommunicator().sendNormalServerMessage(vill.getName() + " is already disbanding.");
            }
        }
        catch (NoSuchVillageException nsv) {
            performer.getCommunicator().sendAlertServerMessage("No village found for that request.");
            toReturn = true;
        }
        return toReturn;
    }
    
    static final boolean preventDisbandVillage(final Creature performer, final Item villageToken, final float counter) {
        boolean insta = false;
        boolean toReturn = false;
        if (performer.getPower() > 3) {
            insta = true;
        }
        try {
            final Village vill = Villages.getVillage(villageToken.getData2());
            if (vill.isDisbanding()) {
                if (counter == 1.0f) {
                    final Village citizVill = performer.getCitizenVillage();
                    if (citizVill != null && citizVill.equals(vill)) {
                        final int time = 300;
                        performer.sendActionControl(Actions.actionEntrys[349].getVerbString(), true, 300);
                        performer.getCommunicator().sendNormalServerMessage("You start to salvage the settlement of " + vill.getName() + ".");
                        Server.getInstance().broadCastAction(performer.getName() + " starts to salvage the settlement of " + vill.getName() + ".", performer, 5);
                        vill.broadCastMessage(new Message(performer, (byte)3, "Village", "<" + performer.getName() + "> starts to salvage the settlement of " + vill.getName() + "."));
                        try {
                            final Player player = Players.getInstance().getPlayer(vill.getDisbander());
                            player.getCommunicator().sendAlertServerMessage(performer.getName() + " is trying to salvage the settlement of " + vill.getName() + "!");
                        }
                        catch (NoSuchPlayerException ex) {}
                    }
                    else {
                        if (!insta) {
                            performer.getCommunicator().sendNormalServerMessage("You need to be citizen to salvage the settlement of " + vill.getName() + ".");
                        }
                        toReturn = true;
                    }
                }
                if (counter > 30.0f || insta) {
                    toReturn = true;
                    try {
                        try {
                            final Player player2 = Players.getInstance().getPlayer(vill.getDisbander());
                            player2.getCommunicator().sendAlertServerMessage(performer.getName() + " successfully salvaged the settlement of " + vill.getName() + "!");
                        }
                        catch (NoSuchPlayerException ex2) {}
                        vill.setDisbandTime(0L);
                        vill.setDisbander(-10L);
                        performer.getCommunicator().sendNormalServerMessage(vill.getName() + " is salvaged for now.");
                        Server.getInstance().broadCastAction(performer.getName() + " has salvaged " + vill.getName() + ".", performer, 5);
                        vill.broadCastMessage(new Message(performer, (byte)3, "Village", "<" + performer.getName() + "> has salvaged the settlement of " + vill.getName() + "."));
                    }
                    catch (IOException iox) {
                        Methods.logger.log(Level.WARNING, performer.getName() + " " + iox.getMessage(), iox);
                    }
                }
            }
            else {
                toReturn = true;
                performer.getCommunicator().sendNormalServerMessage(vill.getName() + " does not need salvaging right now.");
            }
        }
        catch (NoSuchVillageException nsv) {
            performer.getCommunicator().sendAlertServerMessage("No settlement found for that request.");
            toReturn = true;
        }
        return toReturn;
    }
    
    public static final String getTimeString(final long timeleft) {
        String times = "";
        if (timeleft < 60000L) {
            final long secs = timeleft / 1000L;
            times = times + secs + " seconds";
        }
        else {
            final long daysleft = timeleft / 86400000L;
            final long hoursleft = (timeleft - daysleft * 86400000L) / 3600000L;
            final long minutesleft = (timeleft - daysleft * 86400000L - hoursleft * 3600000L) / 60000L;
            if (daysleft > 0L) {
                times = times + daysleft + " days";
            }
            if (hoursleft > 0L) {
                String aft = "";
                if (daysleft > 0L && minutesleft > 0L) {
                    times += ", ";
                    aft += " and ";
                }
                else if (daysleft > 0L) {
                    times += " and ";
                }
                else if (minutesleft > 0L) {
                    aft += " and ";
                }
                times = times + hoursleft + " hours" + aft;
            }
            if (minutesleft > 0L) {
                String aft = "";
                if (daysleft > 0L && hoursleft == 0L) {
                    aft = " and ";
                }
                times = times + aft + minutesleft + " minutes";
            }
        }
        if (times.length() == 0) {
            times = "nothing";
        }
        return times;
    }
    
    static boolean castSpell(final Creature performer, final Spell spell, final Item item, final float counter) {
        return spell.run(performer, item, counter);
    }
    
    static boolean castSpell(final Creature performer, final Spell spell, final Creature target, final float counter) {
        return spell.run(performer, target, counter);
    }
    
    static boolean castSpell(final Creature performer, final Spell spell, final int tilex, final int tiley, final int layer, final int heightOffset, final Tiles.TileBorderDirection dir, final float counter) {
        return spell.run(performer, tilex, tiley, layer, heightOffset, dir, counter);
    }
    
    static boolean castSpell(final Creature performer, final Spell spell, final Wound target, final float counter) {
        return spell.run(performer, target, counter);
    }
    
    static boolean castSpell(final Creature performer, final Spell spell, final int tilex, final int tiley, final int layer, final int heightOffset, final float counter) {
        return spell.run(performer, tilex, tiley, layer, heightOffset, counter);
    }
    
    public static void sendSound(final Creature performer, final String soundId) {
        if (soundId.length() > 0) {
            SoundPlayer.playSound(soundId, performer, 1.6f);
        }
    }
    
    static boolean transferPlayer(final Creature performer, final Creature target, final Action act, final float counter) {
        boolean done = false;
        final int action = act.getNumber();
        if (performer.getPower() < 2) {
            return true;
        }
        ServerEntry targetserver = Servers.localServer;
        if (action == 241) {
            if (Servers.localServer.serverEast == null) {
                performer.getCommunicator().sendNormalServerMessage("No server east of here.");
                done = true;
            }
            else {
                targetserver = Servers.localServer.serverEast;
            }
        }
        else if (action == 240) {
            if (Servers.localServer.serverNorth == null) {
                performer.getCommunicator().sendNormalServerMessage("No server north of here. Using entryserver if one is available.");
                if (Servers.loginServer.entryServer) {
                    targetserver = Servers.loginServer;
                }
                else {
                    targetserver = Servers.getEntryServer();
                }
                if (targetserver == null) {
                    performer.getCommunicator().sendNormalServerMessage("No entryserver was found. Nothing happens.");
                    done = true;
                }
                else if (targetserver.id == Servers.localServer.id) {
                    performer.getCommunicator().sendNormalServerMessage("This option leads back here. Nothing happens.");
                    done = true;
                }
            }
            else {
                targetserver = Servers.localServer.serverNorth;
            }
        }
        else if (action == 242) {
            if (Servers.localServer.serverSouth == null) {
                performer.getCommunicator().sendNormalServerMessage("No server south of here.");
                done = true;
            }
            else {
                targetserver = Servers.localServer.serverSouth;
            }
        }
        else if (action == 243) {
            if (Servers.localServer.serverWest == null) {
                performer.getCommunicator().sendNormalServerMessage("No server west of here.");
                done = true;
            }
            else {
                targetserver = Servers.localServer.serverWest;
            }
        }
        if (!done) {
            if (counter == 1.0f) {
                if (!targetserver.isAvailable(5, true)) {
                    target.getCommunicator().sendNormalServerMessage(targetserver.name + " is no longer available.");
                    return true;
                }
                target.getCommunicator().sendNormalServerMessage("You transfer to " + targetserver.name + ".");
                Server.getInstance().broadCastAction(target.getName() + " transfers to " + targetserver.name + ".", target, 5);
                int tilex = targetserver.SPAWNPOINTJENNX;
                int tiley = targetserver.SPAWNPOINTJENNY;
                if (target.getKingdomId() == 1) {
                    tilex = targetserver.SPAWNPOINTJENNX;
                    tiley = targetserver.SPAWNPOINTJENNY;
                }
                else if (target.getKingdomId() == 3) {
                    tilex = targetserver.SPAWNPOINTLIBX;
                    tiley = targetserver.SPAWNPOINTLIBY;
                }
                else if (target.getKingdomId() == 2) {
                    tilex = targetserver.SPAWNPOINTMOLX;
                    tiley = targetserver.SPAWNPOINTMOLY;
                }
                ((Player)target).sendTransfer(Server.getInstance(), targetserver.INTRASERVERADDRESS, Integer.parseInt(targetserver.INTRASERVERPORT), targetserver.INTRASERVERPASSWORD, targetserver.id, tilex, tiley, true, false, target.getKingdomId());
                ((Player)target).transferCounter = 30;
                if (!target.equals(performer)) {
                    performer.getLogger().log(Level.INFO, performer.getName() + " transferred " + target.getName() + " to " + targetserver.name + ".");
                }
                done = true;
            }
            else if (!target.hasLink()) {
                done = true;
            }
        }
        return done;
    }
    
    public static final void resetAspirants() {
        Methods.kingAspirants.clear();
    }
    
    public static final boolean hasAspiredKing(final Creature performer) {
        return Methods.kingAspirants.contains(performer.getWurmId());
    }
    
    public static final void setAspiredKing(final Creature performer) {
        Methods.kingAspirants.add(performer.getWurmId());
    }
    
    public static final boolean aspireKing(final Creature performer, final byte kingdom, @Nullable final Item item, final Creature elector, final Action act, final float counter) {
        boolean done = false;
        final King current = King.getKing(kingdom);
        if (performer.isChampion()) {
            performer.getCommunicator().sendAlertServerMessage("Champions are not able to rule kingdoms.");
            done = true;
        }
        if (performer.getKingdomId() != kingdom) {
            performer.getCommunicator().sendNormalServerMessage("You may not aspire to become the " + King.getRulerTitle(performer.getSex() == 0, kingdom) + " of " + Kingdoms.getNameFor(kingdom) + ".");
            done = true;
        }
        else if (current != null) {
            performer.getCommunicator().sendNormalServerMessage("There is already a " + current.getRulerTitle() + " of " + Kingdoms.getNameFor(kingdom) + "!");
            done = true;
        }
        if ((hasAspiredKing(performer) || performer.getPower() > 0) && performer.getPower() < 5) {
            performer.getCommunicator().sendNormalServerMessage("You are not eligible to take the test right now.");
            done = true;
        }
        if (counter == 1.0f) {
            if (kingdom == 1 && Methods.jennElector != null) {
                performer.getCommunicator().sendNormalServerMessage("The " + elector.getName() + " is busy. You will have to wait for your turn.");
                done = true;
            }
            if (kingdom == 3 && Methods.hotsElector != null) {
                performer.getCommunicator().sendNormalServerMessage("The " + elector.getName() + " is busy. You will have to wait for your turn.");
                done = true;
            }
            if (kingdom == 2 && Methods.molrStone != null) {
                performer.getCommunicator().sendNormalServerMessage("The " + item.getName() + " is occupied. You will have to wait for your turn.");
                done = true;
            }
        }
        if (!done) {
            int skill1 = 102;
            int skill2 = 100;
            if (kingdom == 1) {
                skill1 = 105;
            }
            if (kingdom == 3) {
                skill2 = 105;
            }
            if (counter == 1.0f) {
                if (kingdom == 1) {
                    Methods.jennElector = elector;
                }
                if (kingdom == 3) {
                    Methods.hotsElector = elector;
                }
                if (kingdom == 2) {
                    Methods.molrStone = item;
                }
                String tname = "";
                if (elector != null) {
                    tname = elector.getName();
                }
                else if (item != null) {
                    tname = item.getName();
                }
                performer.getCommunicator().sendNormalServerMessage("You hesitantly approach the " + tname + ".");
                Server.getInstance().broadCastAction(performer.getName() + " hesitantly approaches the " + tname + ".", performer, 5);
                performer.sendActionControl(Actions.actionEntrys[353].getVerbString(), true, 300);
            }
            else {
                if (act.currentSecond() % 10 == 0 && elector != null) {
                    elector.playAnimation("regalia", false);
                }
                if (act.currentSecond() == 5) {
                    if (item != null) {
                        performer.getCommunicator().sendNormalServerMessage("You struggle with the " + item.getName() + ".");
                        Server.getInstance().broadCastAction(performer.getName() + " struggles with the " + item.getName() + ".", performer, 5);
                    }
                    if (elector != null) {
                        if (kingdom == 1) {
                            elector.almostSurface();
                        }
                        performer.getCommunicator().sendNormalServerMessage(elector.getName() + " watches you intensely.");
                        Server.getInstance().broadCastAction(elector.getName() + " watches " + performer.getName() + " intensely.", performer, 5);
                        elector.turnTowardsCreature(performer);
                    }
                }
                else if (act.currentSecond() == 10) {
                    done = true;
                    try {
                        final Skill sk = performer.getSkills().getSkill(skill1);
                        if (sk.getKnowledge(0.0) > 21.0) {
                            done = false;
                            if (item != null) {
                                performer.getCommunicator().sendNormalServerMessage("You feel the sword budge!");
                                Server.getInstance().broadCastAction(performer.getName() + " seems to make progress with the sword!", performer, 5);
                            }
                            if (elector != null) {
                                if (kingdom == 1) {
                                    performer.getCommunicator().sendNormalServerMessage(elector.getName() + " nods solemnly!");
                                    Server.getInstance().broadCastAction(elector.getName() + " nods faintly in approval!", performer, 5);
                                }
                                else if (kingdom == 3) {
                                    performer.getCommunicator().sendNormalServerMessage(elector.getName() + " hisses and sways " + elector.getHisHerItsString() + " head back and forth in excitement!");
                                    Server.getInstance().broadCastAction(elector.getName() + " hisses and sways " + elector.getHisHerItsString() + " head back and forth in excitement!", performer, 5);
                                }
                            }
                        }
                    }
                    catch (NoSuchSkillException ex) {}
                    if (done) {
                        setAspiredKing(performer);
                        if (item != null) {
                            performer.getCommunicator().sendNormalServerMessage("The sword is stuck and won't budge.");
                            Server.getInstance().broadCastAction(performer.getName() + " shrugs in disappointment as the sword does not budge.", performer, 5);
                        }
                        if (elector != null) {
                            if (kingdom == 1) {
                                elector.submerge();
                                performer.getCommunicator().sendNormalServerMessage(elector.getName() + " silently disappears into the depths.");
                                Server.getInstance().broadCastAction(elector.getName() + " disappears into the depths in silence.", performer, 5);
                            }
                            else if (kingdom == 3) {
                                performer.getCommunicator().sendNormalServerMessage(elector.getName() + " ushers you away with some really threatening moves.");
                                Server.getInstance().broadCastAction(elector.getName() + " thwarts " + performer.getName() + " with some threatening moves.", performer, 5);
                            }
                        }
                    }
                }
                else if (act.currentSecond() == 15) {
                    if (item != null) {
                        performer.getCommunicator().sendNormalServerMessage("You continue struggle with the sword.");
                        Server.getInstance().broadCastAction(performer.getName() + " continues to struggle with the sword.", performer, 5);
                    }
                    if (elector != null) {
                        performer.getCommunicator().sendNormalServerMessage(elector.getName() + " watches you intensely again.");
                        Server.getInstance().broadCastAction(elector.getName() + " watches " + performer.getName() + " intensely again.", performer, 5);
                        elector.turnTowardsCreature(performer);
                    }
                }
                else if (act.currentSecond() == 20) {
                    done = true;
                    try {
                        final Skill sk = performer.getSkills().getSkill(skill2);
                        if (sk.getKnowledge(0.0) > 21.0) {
                            done = false;
                            if (item != null) {
                                performer.getCommunicator().sendNormalServerMessage("You feel the sword budge even more!");
                                Server.getInstance().broadCastAction(performer.getName() + " seems to make even more progress with the sword!", performer, 5);
                            }
                            if (elector != null) {
                                if (kingdom == 1) {
                                    performer.getCommunicator().sendNormalServerMessage(elector.getName() + " nods solemnly!");
                                    Server.getInstance().broadCastAction(elector.getName() + " nods faintly in approval!", performer, 5);
                                }
                                else if (kingdom == 3) {
                                    performer.getCommunicator().sendNormalServerMessage(elector.getName() + " hisses and sways " + elector.getHisHerItsString() + " head back and forth in excitement!");
                                    Server.getInstance().broadCastAction(elector.getName() + " hisses and sways " + elector.getHisHerItsString() + " head back and forth in excitement!", performer, 5);
                                }
                            }
                        }
                    }
                    catch (NoSuchSkillException ex2) {}
                    if (done) {
                        setAspiredKing(performer);
                        if (item != null) {
                            performer.getCommunicator().sendNormalServerMessage("The sword is stuck and won't budge.");
                            Server.getInstance().broadCastAction(performer.getName() + " shrugs in disappointment as the sword does not budge.", performer, 5);
                        }
                        if (elector != null) {
                            if (kingdom == 1) {
                                elector.submerge();
                                performer.getCommunicator().sendNormalServerMessage(elector.getName() + " silently disappears into the depths.");
                                Server.getInstance().broadCastAction(elector.getName() + " disappears into the depths in silence.", performer, 5);
                            }
                            else if (kingdom == 3) {
                                performer.getCommunicator().sendNormalServerMessage(elector.getName() + " ushers you away with some really threatening moves.");
                                Server.getInstance().broadCastAction(elector.getName() + " thwarts " + performer.getName() + " with some threatening moves.", performer, 5);
                            }
                        }
                    }
                }
                else if (act.currentSecond() == 25) {
                    if (item != null) {
                        performer.getCommunicator().sendNormalServerMessage("You make one final push with the sword.");
                        Server.getInstance().broadCastAction(performer.getName() + " exerts all " + performer.getHisHerItsString() + " force on the sword.", performer, 5);
                    }
                    if (elector != null) {
                        performer.getCommunicator().sendNormalServerMessage(elector.getName() + " seems to make up " + elector.getHisHerItsString() + " mind about you.");
                        Server.getInstance().broadCastAction(elector.getName() + " watches " + performer.getName() + " intensely again.", performer, 5);
                        elector.turnTowardsCreature(performer);
                    }
                }
                else if (act.currentSecond() >= 30) {
                    done = true;
                    setAspiredKing(performer);
                    int randomint = 1000;
                    if (performer.getKingdomId() == 1) {
                        randomint = (int)(Kingdoms.activePremiumJenn * 1.5f);
                    }
                    else if (performer.getKingdomId() == 3) {
                        randomint = (int)(Kingdoms.activePremiumHots * 1.5f);
                    }
                    else if (performer.getKingdomId() == 2) {
                        randomint = (int)(Kingdoms.activePremiumMolr * 1.5f);
                    }
                    randomint = Math.max(10, randomint);
                    if (Server.rand.nextInt(randomint) == 0 || performer.getPower() >= 3) {
                        sendSound(performer, "sound.fx.ooh.male");
                        sendSound(performer, "sound.fx.ooh.female");
                        if (item != null) {
                            performer.getCommunicator().sendNormalServerMessage("The sword gets loose and disappears! You are the new " + King.getRulerTitle(performer.getSex() == 0, kingdom) + " of " + Kingdoms.getNameFor(kingdom) + "!");
                            Server.getInstance().broadCastAction("The sword gets loose! " + performer.getName() + " is the new " + King.getRulerTitle(performer.getSex() == 0, kingdom) + " of " + Kingdoms.getNameFor(kingdom) + "!", performer, 10);
                        }
                        if (elector != null) {
                            elector.turnTowardsCreature(performer);
                            if (kingdom == 1) {
                                elector.submerge();
                                elector.playAnimation("regalia", false);
                                performer.getCommunicator().sendNormalServerMessage(elector.getName() + " hands you the royal regalia! You are the new " + King.getRulerTitle(performer.getSex() == 0, kingdom) + " of " + Kingdoms.getNameFor(kingdom) + "!");
                                Server.getInstance().broadCastAction(elector.getName() + " hands the royal regalia to " + performer.getName() + "! " + performer.getHeSheItString() + " is the new " + King.getRulerTitle(performer.getSex() == 0, kingdom) + " of " + Kingdoms.getNameFor(kingdom) + "!", performer, 10);
                            }
                            else if (kingdom == 3) {
                                performer.getCommunicator().sendNormalServerMessage(elector.getName() + " hisses loudly and the royal regalia is handed to you from above! You are the new " + King.getRulerTitle(performer.getSex() == 0, kingdom) + " of " + Kingdoms.getNameFor(kingdom) + "!");
                                Server.getInstance().broadCastAction(elector.getName() + " hisses loudly in excitement! " + performer.getName() + " is the new " + King.getRulerTitle(performer.getSex() == 0, kingdom) + " of " + Kingdoms.getNameFor(kingdom) + "!", performer, 10);
                            }
                        }
                        final King k = King.createKing(kingdom, performer.getName(), performer.getWurmId(), performer.getSex());
                        if (performer.getCitizenVillage() != null) {
                            k.setCapital(performer.getCitizenVillage().getName(), false);
                        }
                        rewardRegalia(performer);
                        final NewKingQuestion nk = new NewKingQuestion(performer, "New ruler!", "Congratulations!", performer.getWurmId());
                        nk.sendQuestion();
                    }
                    else {
                        if (item != null) {
                            performer.getCommunicator().sendNormalServerMessage("The " + item.getName() + " is stuck and won't budge.");
                            Server.getInstance().broadCastAction(performer.getName() + " shrugs in disappointment as the " + item.getName() + " does not budge.", performer, 5);
                        }
                        if (elector != null) {
                            if (kingdom == 1) {
                                elector.submerge();
                                performer.getCommunicator().sendNormalServerMessage("For no obvious reason you are rejected. " + elector.getName() + " silently disappears into the depths.");
                                Server.getInstance().broadCastAction(elector.getName() + " disappears into the depths in silence.", performer, 5);
                            }
                            else if (kingdom == 3) {
                                performer.getCommunicator().sendNormalServerMessage("For no obvious reason you are rejected. " + elector.getName() + " ushers you away with some really threatening moves.");
                                Server.getInstance().broadCastAction(elector.getName() + " thwarts " + performer.getName() + " with some threatening moves.", performer, 5);
                            }
                        }
                    }
                }
            }
        }
        return done;
    }
    
    public static void rewardRegalia(final Creature creature) {
        final Item inventory = creature.getInventory();
        if (inventory != null) {
            try {
                final byte template = Kingdoms.getKingdom(creature.getKingdomId()).getTemplate();
                final byte kingdom = creature.getKingdomId();
                if (template == 1) {
                    final Item sceptre = ItemFactory.createItem(529, Server.rand.nextFloat() * 30.0f + 70.0f, creature.getName());
                    sceptre.setAuxData(kingdom);
                    inventory.insertItem(sceptre, true);
                    final Item crown = ItemFactory.createItem(530, Server.rand.nextFloat() * 30.0f + 70.0f, creature.getName());
                    crown.setAuxData(kingdom);
                    inventory.insertItem(crown, true);
                    final Item robes = ItemFactory.createItem(531, Server.rand.nextFloat() * 30.0f + 70.0f, creature.getName());
                    robes.setAuxData(kingdom);
                    inventory.insertItem(robes, true);
                }
                else if (template == 3) {
                    final Item sceptre = ItemFactory.createItem(535, Server.rand.nextFloat() * 30.0f + 70.0f, creature.getName());
                    sceptre.setAuxData(kingdom);
                    inventory.insertItem(sceptre, true);
                    final Item crown = ItemFactory.createItem(536, Server.rand.nextFloat() * 30.0f + 70.0f, creature.getName());
                    crown.setAuxData(kingdom);
                    inventory.insertItem(crown, true);
                    final Item robes = ItemFactory.createItem(537, Server.rand.nextFloat() * 30.0f + 70.0f, creature.getName());
                    robes.setAuxData(kingdom);
                    inventory.insertItem(robes, true);
                }
                else if (template == 2) {
                    final Item sceptre = ItemFactory.createItem(532, Server.rand.nextFloat() * 30.0f + 70.0f, creature.getName());
                    sceptre.setAuxData(kingdom);
                    inventory.insertItem(sceptre, true);
                    final Item crown = ItemFactory.createItem(533, Server.rand.nextFloat() * 30.0f + 70.0f, creature.getName());
                    crown.setAuxData(kingdom);
                    inventory.insertItem(crown, true);
                    final Item robes = ItemFactory.createItem(534, Server.rand.nextFloat() * 30.0f + 70.0f, creature.getName());
                    robes.setAuxData(kingdom);
                    inventory.insertItem(robes, true);
                }
            }
            catch (Exception ex) {
                Methods.logger.log(Level.WARNING, creature.getName() + " " + ex.getMessage(), ex);
            }
        }
    }
    
    public static Creature getJennElector() {
        return Methods.jennElector;
    }
    
    public static void resetJennElector() {
        Methods.jennElector = null;
    }
    
    public static Creature getHotsElector() {
        return Methods.hotsElector;
    }
    
    public static void resetHotsElector() {
        Methods.hotsElector = null;
    }
    
    public static Item getMolrStone() {
        return Methods.molrStone;
    }
    
    public static void resetMolrStone() {
        Methods.molrStone = null;
    }
    
    public static final void sendVoiceChatQuestion(final Creature player) {
        if (player != null) {
            if (Constants.isEigcEnabled) {
                final VoiceChatQuestion vcq = new VoiceChatQuestion(player);
                vcq.sendQuestion();
            }
            else {
                player.getCommunicator().sendNormalServerMessage("Voice chat is not enabled on this server.");
            }
        }
    }
    
    static void sendGmSetEnchantQuestion(final Creature performer, final Item target) {
        final GmSetEnchants gmse = new GmSetEnchants(performer, target);
        gmse.sendQuestion();
    }
    
    static void sendGmSetTraitsQuestion(final Creature performer, final Creature target) {
        final GmSetTraits gmst = new GmSetTraits(performer, target);
        gmst.sendQuestion();
    }
    
    static void sendGmSetMedpathQuestion(final Creature performer, final Creature target) {
        final GmSetMedPath gmsm = new GmSetMedPath(performer, target);
        gmsm.sendQuestion();
    }
    
    static void sendGmBuildAllWallsQuestion(final Creature performer, final Structure target) {
        final GMBuildAllWallsQuestion gmbawq = new GMBuildAllWallsQuestion(performer, target);
        gmbawq.sendQuestion();
    }
    
    static void sendPlanBridgeQuestion(final Creature performer, final int targetFloorLevel, final Point start, final Point end, final byte dir, final int width, final int length) {
        final PlanBridgeQuestion pbq = new PlanBridgeQuestion(performer, targetFloorLevel, start, end, dir, width, length);
        pbq.sendQuestion();
    }
    
    public static Item[] getBestReports(final Creature creature, @Nullable final Item container) {
        final Map<Byte, Item> reports = new HashMap<Byte, Item>();
        if (container == null) {
            for (final Item item : creature.getInventory().getAllItems(true)) {
                if (item.getTemplateId() == 1127) {
                    addAlmanacReports(reports, item);
                }
            }
        }
        else if (container.getTemplateId() == 1127) {
            addAlmanacReports(reports, container);
        }
        else if (container.getTemplateId() == 1128) {
            addAlmanacFolderReports(reports, container);
        }
        final Item[] reportArr = reports.values().toArray(new Item[reports.size()]);
        Arrays.sort(reportArr);
        return reportArr;
    }
    
    private static void addAlmanacReports(final Map<Byte, Item> reports, final Item almanac) {
        for (final Item item : almanac.getItems()) {
            if (item.getTemplateId() == 1128) {
                addAlmanacFolderReports(reports, item);
            }
            else {
                addReport(reports, item);
            }
        }
    }
    
    private static void addAlmanacFolderReports(final Map<Byte, Item> reports, final Item almanacFolder) {
        for (final Item report : almanacFolder.getItems()) {
            addReport(reports, report);
        }
    }
    
    private static void addReport(final Map<Byte, Item> reports, final Item report) {
        if (report.isHarvestReport()) {
            final Item oldReport = reports.get(report.getAuxData());
            if (oldReport == null) {
                reports.put(report.getAuxData(), report);
            }
            else if (oldReport.getCurrentQualityLevel() < report.getCurrentQualityLevel()) {
                reports.put(report.getAuxData(), report);
            }
        }
    }
    
    public static void addActionIfAbsent(final List<ActionEntry> actionEntries, final ActionEntry newActionEntry) {
        if (!actionEntries.contains(newActionEntry)) {
            actionEntries.add(newActionEntry);
        }
    }
    
    public static boolean isActionAllowed(final Creature performer, final short action) {
        return isActionAllowed(performer, action, performer.getTileX(), performer.getTileY());
    }
    
    public static boolean isActionAllowed(final Creature performer, final short action, final Item item) {
        return isActionAllowed(performer, action, false, item.getTileX(), item.getTileY(), item, 0, 0);
    }
    
    public static boolean isActionAllowed(final Creature performer, final short action, final int x, final int y) {
        return isActionAllowed(performer, action, false, x, y, null, 0, 0);
    }
    
    public static boolean isActionAllowed(final Creature performer, final short action, final boolean setHunted, final int tileX, final int tileY, final int encodedTile, final int dir) {
        return isActionAllowed(performer, action, false, tileX, tileY, null, encodedTile, dir);
    }
    
    public static boolean isActionAllowed(final Creature performer, final short action, final boolean setHunted, final int tileX, final int tileY, @Nullable final Item item, final int encodedTile, final int dir) {
        final VolaTile vt = Zones.getOrCreateTile(tileX, tileY, performer.isOnSurface());
        final Village village = (vt != null) ? vt.getVillage() : null;
        final Structure structure = (vt != null) ? vt.getStructure() : null;
        boolean canDo = true;
        if (Actions.isActionDestroy(action) && village != null && village.isActionAllowed(action, performer)) {
            canDo = true;
        }
        else if (structure != null && structure.isTypeHouse() && !structure.isFinished() && (Actions.isActionBuild(action) || Actions.isActionDestroy(action)) && structure.isActionAllowed(performer, action)) {
            canDo = true;
        }
        else if (structure != null && structure.isTypeHouse() && structure.isFinished()) {
            canDo = ((!Actions.isActionBuildingPermission(action) && village != null && village.isActionAllowed(action, performer)) || structure.isActionAllowed(performer, action) || isNotAllowedMessage(performer, village, structure, action, false));
        }
        else if (village != null) {
            canDo = (village.isActionAllowed(action, performer, false, encodedTile, dir) || isNotAllowedMessage(performer, village, structure, action, false));
        }
        if (village == null && Actions.actionEntrys[action].isPerimeterAction()) {
            final Village villagePerim = Villages.getVillageWithPerimeterAt(tileX, tileY, true);
            if (villagePerim != null && !villagePerim.isCitizen(performer) && !villagePerim.isAlly(performer)) {
                boolean skipOthers = false;
                try {
                    final Item token = villagePerim.getToken();
                    if (token != null && token.getWurmId() == 7689502046815490L) {
                        canDo = true;
                        skipOthers = true;
                    }
                }
                catch (NoSuchItemException ex) {}
                if (!skipOthers) {
                    canDo = (villagePerim.isActionAllowed(action, performer, false, 0, 0) || isNotAllowedMessage(performer, villagePerim, structure, action, true));
                }
            }
        }
        else if (village == null && item != null && item.isRoadMarker()) {
            Village twoCheck = null;
            twoCheck = Zones.getVillage(item.getTilePos(), item.isOnSurface());
            if (twoCheck == null) {
                final Village vill = Villages.getVillageWithPerimeterAt(item.getTileX(), item.getTileY(), item.isOnSurface());
                if (vill != null && vill.coversPlus(item.getTileX(), item.getTileY(), 2)) {
                    twoCheck = vill;
                }
            }
            if (twoCheck != null) {
                canDo = (twoCheck.isActionAllowed(action, performer, false, 0, 0) || isNotAllowedMessage(performer, twoCheck, structure, action, true));
            }
        }
        return canDo;
    }
    
    private static boolean isNotAllowedMessage(final Creature performer, final Village village, final Structure structure, final short action, final boolean inPerimeter) {
        if (!performer.isOnPvPServer() || Servers.isThisAChaosServer()) {
            String msg;
            if (inPerimeter) {
                msg = village.getName() + " does not allow that.";
            }
            else if (village != null && village.getGuards().length > 0) {
                msg = "The guards kindly inform you that you are not allowed to do that here.";
            }
            else if (village != null) {
                msg = "That would be very bad for your karma and is disallowed on this server.";
            }
            else {
                msg = "You do not have permission to do that here.";
            }
            performer.getCommunicator().sendNormalServerMessage(msg);
            return false;
        }
        if (village != null) {
            if (!village.isEnemy(performer)) {
                if (performer.isLegal()) {
                    performer.getCommunicator().sendNormalServerMessage("That would be illegal here. You can check the settlement token for the local laws.");
                    return false;
                }
                if (Actions.actionEntrys[action].isEnemyAllowedWhenNoGuards() && village.getGuards().length > 0) {
                    performer.getCommunicator().sendNormalServerMessage("A guard has noted you and stops you with a warning.");
                    return false;
                }
                if (Actions.actionEntrys[action].isEnemyNeverAllowed()) {
                    performer.getCommunicator().sendNormalServerMessage("That action makes no sense here.");
                    return false;
                }
            }
            else {
                if (Actions.actionEntrys[action].isEnemyAllowedWhenNoGuards() && village.getGuards().length > 0) {
                    performer.getCommunicator().sendNormalServerMessage("A guard has noted you and stops you with a warning.");
                    return false;
                }
                if (Actions.actionEntrys[action].isEnemyNeverAllowed()) {
                    performer.getCommunicator().sendNormalServerMessage("That action makes no sense here.");
                    return false;
                }
            }
            return true;
        }
        if (structure != null && structure.isFinished()) {
            if (!structure.isEnemy(performer)) {
                if (performer.isLegal()) {
                    performer.getCommunicator().sendNormalServerMessage("That would be illegal. ");
                    return false;
                }
            }
            else if (Actions.actionEntrys[action].isEnemyNeverAllowed()) {
                performer.getCommunicator().sendNormalServerMessage("That action makes no sense here.");
                return false;
            }
            return true;
        }
        return true;
    }
    
    static {
        logger = Logger.getLogger(Methods.class.getName());
        Methods.jennElector = null;
        Methods.hotsElector = null;
        Methods.molrStone = null;
        Methods.kingAspirants = new HashSet<Long>();
    }
}
