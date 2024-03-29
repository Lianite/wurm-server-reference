// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.behaviours;

import com.wurmonline.server.kingdom.King;
import java.util.LinkedList;
import com.wurmonline.server.villages.Guard;
import com.wurmonline.server.villages.Village;
import com.wurmonline.server.questions.ManageObjectList;
import com.wurmonline.server.zones.Zones;
import com.wurmonline.server.Players;
import com.wurmonline.server.Server;
import com.wurmonline.mesh.Tiles;
import com.wurmonline.server.questions.EconomicAdvisorInfo;
import com.wurmonline.server.questions.KingdomHistory;
import com.wurmonline.server.questions.KingdomStatusQuestion;
import com.wurmonline.server.Servers;
import com.wurmonline.server.banks.Bank;
import com.wurmonline.server.questions.BankManagementQuestion;
import com.wurmonline.server.players.Player;
import com.wurmonline.server.banks.Banks;
import com.wurmonline.server.questions.VillageShowPlan;
import com.wurmonline.server.NoSuchItemException;
import com.wurmonline.server.Items;
import com.wurmonline.server.villages.NoSuchVillageException;
import com.wurmonline.server.villages.Villages;
import java.util.logging.Level;
import java.util.Collection;
import java.util.List;
import com.wurmonline.server.items.Item;
import com.wurmonline.server.creatures.Creature;
import java.util.logging.Logger;

final class VillageTokenBehaviour extends ItemBehaviour
{
    private static final Logger logger;
    
    VillageTokenBehaviour() {
        super((short)25);
    }
    
    @Override
    public List<ActionEntry> getBehavioursFor(final Creature performer, final Item target) {
        final List<ActionEntry> toReturn = super.getBehavioursFor(performer, target);
        toReturn.addAll(this.getVillageBehaviours(performer, target));
        if (target.getOwnerId() != -10L) {
            toReturn.add(Actions.actionEntrys[176]);
        }
        return toReturn;
    }
    
    @Override
    public List<ActionEntry> getBehavioursFor(final Creature performer, final Item source, final Item target) {
        final List<ActionEntry> toReturn = super.getBehavioursFor(performer, source, target);
        toReturn.addAll(this.getVillageBehaviours(performer, target));
        if (target.getOwnerId() != -10L) {
            toReturn.add(Actions.actionEntrys[176]);
        }
        if (performer.getPower() > 0 && (source.getTemplateId() == 176 || source.getTemplateId() == 315)) {
            toReturn.add(Actions.actionEntrys[513]);
        }
        if (performer.getCurrentVillage() != null && performer.getCurrentVillage().kingdom == performer.getKingdomId() && !performer.getCurrentVillage().isEnemy(performer) && performer.isWithinTileDistanceTo(target.getTileX(), target.getTileY(), 0, 2) && !source.isNoDiscard() && !source.isInstaDiscard() && !source.isTemporary()) {
            toReturn.add(Actions.actionEntrys[31]);
        }
        return toReturn;
    }
    
    @Override
    public boolean action(final Action act, final Creature performer, final Item target, final short action, final float counter) {
        boolean done = true;
        if (action == 176) {
            Methods.setVillageToken(performer, target);
        }
        else if (action == 77) {
            Methods.sendVillageInfo(performer, target);
        }
        else if (action == 670) {
            Methods.sendManageUpkeep(performer, target);
        }
        else if (action == 71) {
            Methods.sendVillageHistory(performer, target);
        }
        else if (action == 72) {
            Methods.sendAreaHistory(performer, target);
        }
        else if (action == 80) {
            final Village village = performer.getCitizenVillage();
            if (village.mayDoDiplomacy(performer)) {
                Methods.sendManageAllianceQuestion(performer, target);
            }
            else {
                performer.getCommunicator().sendNormalServerMessage("You may not perform diplomacy for " + village.getName() + ".");
            }
        }
        else if (action == 67) {
            final int villageId = target.getData2();
            if (villageId <= 0) {
                performer.getCommunicator().sendSafeServerMessage("This settlement is not founded yet!");
                VillageTokenBehaviour.logger.log(Level.WARNING, performer.getName() + " managing token with id " + target.getWurmId() + " but villageid=" + villageId);
                return true;
            }
            try {
                final Village currVill = Villages.getVillage(villageId);
                if (currVill != null && currVill.isActionAllowed((short)67, performer)) {
                    Methods.sendManageVillageGuardsQuestion(performer, target);
                }
            }
            catch (NoSuchVillageException nsv2) {
                return true;
            }
        }
        else if (action == 348) {
            done = (performer.isGuest() || Methods.disbandVillage(performer, target, counter));
        }
        else if (action == 349) {
            done = (performer.isGuest() || Methods.preventDisbandVillage(performer, target, counter));
        }
        else if (action == 350) {
            if (performer.isPaying() && !performer.hasFlag(63)) {
                final int villageId = target.getData2();
                if (villageId <= 0) {
                    performer.getCommunicator().sendSafeServerMessage("This settlement is not founded yet!");
                    VillageTokenBehaviour.logger.log(Level.WARNING, performer.getName() + " managing token with id " + target.getWurmId() + " but villageid=" + villageId);
                    return true;
                }
                try {
                    final Village currVill = Villages.getVillage(target.getData2());
                    done = Methods.drainCoffers(performer, currVill, counter, target, act);
                }
                catch (NoSuchVillageException ex) {}
            }
        }
        else if (action == 513) {
            final int villageId = target.getData2();
            if (villageId <= 0) {
                performer.getCommunicator().sendSafeServerMessage("This settlement is not founded yet!");
                VillageTokenBehaviour.logger.log(Level.WARNING, performer.getName() + " managing token with id " + target.getWurmId() + " but villageid=" + villageId);
                return true;
            }
            try {
                final Village currVill = Villages.getVillage(villageId);
                if (currVill.getGuards().length == 1 || (performer.getPower() >= 2 && currVill.getGuards().length >= 1)) {
                    if (performer.isWithinDistanceTo(target.getPosX(), target.getPosY(), target.getPosZ(), 4.0f)) {
                        currVill.putGuardsAtToken();
                    }
                    else {
                        performer.getCommunicator().sendNormalServerMessage("You are now too far away to summon the guard.");
                    }
                }
                else if (currVill.getGuards().length > 0) {
                    performer.getCommunicator().sendNormalServerMessage("There are more than one guard left. You can not summon it now.");
                }
                else {
                    performer.getCommunicator().sendNormalServerMessage("There are no guards active.");
                }
            }
            catch (NoSuchVillageException ex2) {}
        }
        else if (action == 69) {
            done = true;
            final int villageId = target.getData2();
            if (villageId <= 0) {
                performer.getCommunicator().sendSafeServerMessage("This settlement is not founded yet!");
                VillageTokenBehaviour.logger.log(Level.WARNING, performer.getName() + " managing token with id " + target.getWurmId() + " but villageid=" + villageId);
            }
            else {
                try {
                    final Village currVill = Villages.getVillage(villageId);
                    if (currVill.isActionAllowed(action, performer)) {
                        Methods.sendReputationManageQuestion(performer, target);
                    }
                    else {
                        performer.getCommunicator().sendSafeServerMessage("Illegal option.");
                        VillageTokenBehaviour.logger.log(Level.WARNING, performer.getName() + " cheating? Illegal option for " + target.getWurmId() + " at villageid=" + villageId);
                    }
                }
                catch (NoSuchVillageException nsv2) {
                    VillageTokenBehaviour.logger.log(Level.WARNING, performer.getName() + " managing token with id " + target.getWurmId() + " but villageid=" + villageId);
                }
            }
        }
        else if (action == 68) {
            done = true;
            final int villageId = target.getData2();
            if (villageId <= 0) {
                performer.getCommunicator().sendSafeServerMessage("This settlement is not founded yet!");
                VillageTokenBehaviour.logger.log(Level.WARNING, performer.getName() + " managing token with id " + target.getWurmId() + " but villageid=" + villageId);
            }
            else {
                try {
                    final Village currVill = Villages.getVillage(villageId);
                    if (currVill.isActionAllowed(action, performer)) {
                        Methods.sendManageVillageSettingsQuestion(performer, target);
                    }
                    else {
                        performer.getCommunicator().sendSafeServerMessage("Illegal option.");
                        VillageTokenBehaviour.logger.log(Level.WARNING, performer.getName() + " cheating? Illegal option for " + target.getWurmId() + " at villageid=" + villageId);
                    }
                }
                catch (NoSuchVillageException nsv2) {
                    VillageTokenBehaviour.logger.log(Level.WARNING, performer.getName() + " managing token with id " + target.getWurmId() + " but villageid=" + villageId);
                }
            }
        }
        else if (action == 540) {
            done = true;
            final int villageId = target.getData2();
            if (villageId <= 0) {
                performer.getCommunicator().sendSafeServerMessage("This settlement is not founded yet!");
                VillageTokenBehaviour.logger.log(Level.WARNING, performer.getName() + " managing token with id " + target.getWurmId() + " but villageid=" + villageId);
            }
            else {
                try {
                    final Village currVill = Villages.getVillage(villageId);
                    if (currVill.isActionAllowed(action, performer)) {
                        Methods.sendManageVillageRolesQuestion(performer, target);
                    }
                    else {
                        performer.getCommunicator().sendSafeServerMessage("Illegal option.");
                        VillageTokenBehaviour.logger.log(Level.WARNING, performer.getName() + " cheating? Illegal option for " + target.getWurmId() + " at villageid=" + villageId);
                    }
                }
                catch (NoSuchVillageException nsv2) {
                    VillageTokenBehaviour.logger.log(Level.WARNING, performer.getName() + " managing token with id " + target.getWurmId() + " but villageid=" + villageId);
                }
            }
        }
        else if (action == 66) {
            done = true;
            final int villageId = target.getData2();
            if (villageId <= 0) {
                performer.getCommunicator().sendSafeServerMessage("This settlement is not founded yet!");
                VillageTokenBehaviour.logger.log(Level.WARNING, performer.getName() + " managing token with id " + target.getWurmId() + " but villageid=" + villageId);
            }
            else {
                try {
                    final Village currVill = Villages.getVillage(villageId);
                    if (currVill.isActionAllowed(action, performer)) {
                        Methods.sendManageVillageCitizensQuestion(performer, target);
                    }
                    else {
                        performer.getCommunicator().sendSafeServerMessage("Illegal option.");
                        VillageTokenBehaviour.logger.log(Level.WARNING, performer.getName() + " cheating? Illegal option for " + target.getWurmId() + " at villageid=" + villageId);
                    }
                }
                catch (NoSuchVillageException nsv2) {
                    VillageTokenBehaviour.logger.log(Level.WARNING, performer.getName() + " managing token with id " + target.getWurmId() + " but villageid=" + villageId);
                }
            }
        }
        else if (action == 70) {
            done = true;
            final int villageId = target.getData2();
            if (villageId <= 0) {
                performer.getCommunicator().sendSafeServerMessage("This settlement is not founded yet!");
                VillageTokenBehaviour.logger.log(Level.WARNING, performer.getName() + " managing token with id " + target.getWurmId() + " but villageid=" + villageId);
            }
            else {
                try {
                    final Village currVill = Villages.getVillage(target.getData2());
                    if (currVill.isActionAllowed(action, performer)) {
                        Methods.sendManageVillageGatesQuestion(performer, target);
                    }
                    else {
                        performer.getCommunicator().sendSafeServerMessage("Illegal option.");
                        VillageTokenBehaviour.logger.log(Level.WARNING, performer.getName() + " cheating? Illegal option for " + target.getWurmId() + " at villageid=" + villageId);
                    }
                }
                catch (NoSuchVillageException nsv2) {
                    VillageTokenBehaviour.logger.log(Level.WARNING, performer.getName() + " managing token with id " + target.getWurmId() + " but villageid=" + villageId);
                }
            }
        }
        else if (action == 76) {
            done = true;
            final int villageId = target.getData2();
            if (villageId <= 0) {
                performer.getCommunicator().sendSafeServerMessage("This settlement is not founded yet!");
                VillageTokenBehaviour.logger.log(Level.WARNING, performer.getName() + " managing token with id " + target.getWurmId() + " but villageid=" + villageId);
            }
            else {
                try {
                    final Village currVill = Villages.getVillage(target.getData2());
                    if (currVill.isActionAllowed(action, performer)) {
                        try {
                            final Item deed = Items.getItem(currVill.getDeedId());
                            if (deed.isNewDeed()) {
                                Methods.sendExpandVillageQuestion(performer, deed);
                            }
                            else {
                                performer.getCommunicator().sendNormalServerMessage("The mayor needs to replace the current deed first.");
                            }
                            return done;
                        }
                        catch (NoSuchItemException nsi) {
                            VillageTokenBehaviour.logger.log(Level.WARNING, "No deed for " + currVill.getName());
                            performer.getCommunicator().sendSafeServerMessage("Something went wrong as you tried to resize the settlement. The deed could not be located.");
                            return done;
                        }
                    }
                    performer.getCommunicator().sendSafeServerMessage("Illegal option.");
                    VillageTokenBehaviour.logger.log(Level.WARNING, performer.getName() + " cheating? Illegal option for " + target.getWurmId() + " at villageid=" + villageId);
                }
                catch (NoSuchVillageException nsv2) {
                    VillageTokenBehaviour.logger.log(Level.WARNING, performer.getName() + " managing token with id " + target.getWurmId() + " but villageid=" + villageId);
                }
            }
        }
        else if (action == 689) {
            done = true;
            final int villageId = target.getData2();
            if (villageId <= 0) {
                performer.getCommunicator().sendSafeServerMessage("This settlement is not founded yet!");
                VillageTokenBehaviour.logger.log(Level.WARNING, performer.getName() + " managing token with id " + target.getWurmId() + " but villageid=" + villageId);
            }
            else {
                try {
                    final Village currVill = Villages.getVillage(target.getData2());
                    if (currVill.isMayor(performer) || performer.getPower() >= 2) {
                        final VillageShowPlan vsp = new VillageShowPlan(performer, currVill);
                        vsp.sendQuestion();
                    }
                    else {
                        VillageTokenBehaviour.logger.log(Level.WARNING, performer.getName() + " managing token with id " + target.getWurmId() + " trying to see plan when not a mayor (" + villageId + ").");
                    }
                }
                catch (NoSuchVillageException nsv2) {
                    VillageTokenBehaviour.logger.log(Level.WARNING, performer.getName() + " managing token with id " + target.getWurmId() + " but villageid=" + villageId);
                }
            }
        }
        else if (action == 222) {
            final int villageId = target.getData2();
            final Bank bank = Banks.getBank(performer.getWurmId());
            if (bank != null) {
                if (!performer.isGuest()) {
                    if (villageId == bank.currentVillage) {
                        if (!bank.open) {
                            ((Player)performer).openBank();
                        }
                        else {
                            performer.getCommunicator().sendNormalServerMessage("Your bank account is already open.");
                        }
                    }
                    else {
                        final BankManagementQuestion bm = new BankManagementQuestion(performer, "Bank management", "Move bank", target.getWurmId(), bank);
                        bm.sendQuestion();
                    }
                }
                else {
                    performer.getCommunicator().sendNormalServerMessage("This feature is only available to paying players.");
                }
            }
            else if (!performer.isGuest()) {
                try {
                    final Village v = Villages.getVillage(villageId);
                    if (v.kingdom == performer.getKingdomId()) {
                        final BankManagementQuestion bm2 = new BankManagementQuestion(performer, "Bank management", "Opening bank account", target.getWurmId(), null);
                        bm2.sendQuestion();
                    }
                    else {
                        performer.getCommunicator().sendNormalServerMessage("You can't open a bank here.");
                    }
                }
                catch (NoSuchVillageException nsv) {
                    VillageTokenBehaviour.logger.log(Level.WARNING, performer.getName() + ":" + nsv.getMessage(), nsv);
                }
            }
            else {
                performer.getCommunicator().sendNormalServerMessage("This feature is only available to paying players.");
            }
        }
        else if (action == 226) {
            if (performer.isWithinDistanceTo(target.getPosX(), target.getPosY(), target.getPosZ(), 30.0f)) {
                Methods.sendWithdrawMoneyQuestion(performer, target);
                done = true;
            }
            else {
                performer.getCommunicator().sendNormalServerMessage("You are too far away to do that.");
            }
        }
        else if (action == 194) {
            done = true;
            Methods.sendPlayerPaymentQuestion(performer);
        }
        else if (action == 1) {
            final String descString = target.examine(performer);
            performer.getCommunicator().sendNormalServerMessage(descString);
            target.sendEnchantmentStrings(performer.getCommunicator());
            target.sendExtraStrings(performer.getCommunicator());
            if (target.getData2() > 0) {
                try {
                    final Village currVill = Villages.getVillage(target.getData2());
                    performer.getCommunicator().sendNormalServerMessage("You spot " + currVill.getGuards().length + " guards in the vicinity.");
                    if (performer.citizenVillage != null && performer.citizenVillage == currVill) {
                        final int numNpcTraders = currVill.getTraders().size();
                        if (numNpcTraders == 0) {
                            performer.getCommunicator().sendNormalServerMessage("There are no traders belonging to this settlement.");
                        }
                        else if (numNpcTraders == 1) {
                            performer.getCommunicator().sendNormalServerMessage("You spot " + numNpcTraders + " trader belonging to this settlement.");
                        }
                        else {
                            performer.getCommunicator().sendNormalServerMessage("You spot " + numNpcTraders + " traders belonging to this settlement.");
                        }
                    }
                }
                catch (NoSuchVillageException nsv2) {
                    VillageTokenBehaviour.logger.log(Level.WARNING, performer.getName() + " managing token with id " + target.getWurmId() + " but villageid=" + target.getData2());
                }
            }
        }
        else if (action == 334) {
            if (performer.isWithinDistanceTo(target.getPosX(), target.getPosY(), target.getPosZ(), 30.0f)) {
                if (Servers.loginServer.isAvailable(5, true)) {
                    if (performer.checkLoyaltyProgram()) {
                        performer.getCommunicator().sendSafeServerMessage("Any Legacy Loyalty Bonus items you are entitled to should arrive soon.");
                    }
                    else {
                        performer.getCommunicator().sendNormalServerMessage("You have already received any Legacy Loyalty Bonus items that you were entitled to.");
                    }
                }
                else {
                    performer.getCommunicator().sendNormalServerMessage("The login server is unavailable right now. Please try later.");
                }
            }
            else {
                performer.getCommunicator().sendNormalServerMessage("You are too far away to do that.");
            }
        }
        else if (action == 355) {
            final KingdomStatusQuestion kq = new KingdomStatusQuestion(performer, "Kingdom status", "Kingdoms", performer.getWurmId());
            kq.sendQuestion();
        }
        else if (action == 356) {
            final KingdomHistory kq2 = new KingdomHistory(performer, "Kingdom history", "History of the kingdoms", performer.getWurmId());
            kq2.sendQuestion();
        }
        else if (action == 34) {
            if (performer.getPower() >= 3) {
                try {
                    final Village currVill2 = Villages.getVillage(target.getData2());
                    final Guard[] guards = currVill2.getGuards();
                    if (guards.length == 0) {
                        performer.getCommunicator().sendNormalServerMessage("There are no guards!");
                    }
                    else {
                        for (int x = 0; x < guards.length; ++x) {
                            performer.getCommunicator().sendNormalServerMessage(guards[x].getCreature().getName() + " (" + guards[x].getCreature().getWurmId() + ") at " + ((int)guards[x].getCreature().getPosX() >> 2) + ", " + ((int)guards[x].getCreature().getPosY() >> 2) + ", surfaced=" + guards[x].getCreature().isOnSurface());
                        }
                    }
                }
                catch (NoSuchVillageException nsv3) {
                    VillageTokenBehaviour.logger.log(Level.WARNING, performer.getName() + " managing token with id " + target.getWurmId() + " but villageid=" + target.getData2());
                }
            }
        }
        else if (action == 371) {
            if (performer.isEconomicAdvisor() || performer.getPower() >= 3) {
                if (performer.isWithinDistanceTo(target.getPosX(), target.getPosY(), target.getPosZ(), 30.0f)) {
                    final EconomicAdvisorInfo question = new EconomicAdvisorInfo(performer, "Status sheet", "Economic breakdown", target.getWurmId());
                    question.sendQuestion();
                }
                else {
                    performer.getCommunicator().sendNormalServerMessage("You are too far away to do that.");
                }
            }
        }
        else if (action == 118 || action == 381 || action == 382) {
            if (performer.getPower() >= 4) {
                try {
                    final Village currVill2 = Villages.getVillage(target.getData2());
                    final byte newType = (currVill2.kingdom == 3) ? Tiles.Tile.TILE_MYCELIUM_LAWN.id : Tiles.Tile.TILE_LAWN.id;
                    for (int x = currVill2.getStartX(); x <= currVill2.getEndX(); ++x) {
                        for (int y = currVill2.getStartY(); y <= currVill2.getEndY(); ++y) {
                            if (action == 118) {
                                final int tile = Server.surfaceMesh.getTile(x, y);
                                final byte type = Tiles.decodeType(tile);
                                if (!Tiles.isRoadType(type)) {
                                    Server.surfaceMesh.setTile(x, y, Tiles.encode(Tiles.decodeHeight(tile), newType, (byte)0));
                                    Server.modifyFlagsByTileType(x, y, newType);
                                    Server.setWorldResource(x, y, 0);
                                    Players.getInstance().sendChangedTile(x, y, true, true);
                                }
                            }
                            else {
                                Zones.protectedTiles[x][y] = (action == 381);
                            }
                        }
                    }
                    if (action == 118) {
                        performer.getCommunicator().sendNormalServerMessage("You convert all non pavement tiles on settlement to lawn.");
                    }
                    else if (action == 381) {
                        performer.getCommunicator().sendNormalServerMessage("You protect the settlement tiles.");
                    }
                    else {
                        performer.getCommunicator().sendNormalServerMessage("You unprotect the settlement tiles.");
                    }
                }
                catch (NoSuchVillageException nsv3) {
                    VillageTokenBehaviour.logger.log(Level.WARNING, performer.getName() + " managing token with id " + target.getWurmId() + " but villageid=" + target.getData2());
                }
            }
        }
        else if (action == 481) {
            done = true;
            final int villageId = target.getData2();
            if (villageId <= 0) {
                performer.getCommunicator().sendSafeServerMessage("This settlement is not founded yet!");
                VillageTokenBehaviour.logger.log(Level.WARNING, performer.getName() + " managing token with id " + target.getWurmId() + " but villageid=" + villageId);
            }
            else {
                try {
                    final Village currVill = Villages.getVillage(villageId);
                    if (currVill.isActionAllowed((short)68, performer) || performer.getPower() >= 2) {
                        Methods.sendConfigureTwitter(performer, target.getData2(), true, currVill.getName());
                    }
                    else {
                        performer.getCommunicator().sendSafeServerMessage("Illegal option.");
                        VillageTokenBehaviour.logger.log(Level.WARNING, performer.getName() + " cheating? Illegal option for " + target.getWurmId() + " at villageid=" + villageId);
                    }
                }
                catch (NoSuchVillageException nsv2) {
                    VillageTokenBehaviour.logger.log(Level.WARNING, performer.getName() + " managing token with id " + target.getWurmId() + " but villageid=" + villageId);
                }
            }
        }
        else if (action == 209) {
            done = true;
            final int villageId = target.getData2();
            if (performer.getCitizenVillage() != null) {
                try {
                    final Village currVill = Villages.getVillage(villageId);
                    if (currVill != null) {
                        if (performer.getCitizenVillage().mayDeclareWarOn(currVill)) {
                            Methods.sendWarDeclarationQuestion(performer, currVill);
                        }
                        else {
                            performer.getCommunicator().sendAlertServerMessage(currVill.getName() + " is already at war with your village.");
                        }
                    }
                }
                catch (NoSuchVillageException nsv2) {
                    VillageTokenBehaviour.logger.log(Level.WARNING, performer.getName() + " managing token with id " + target.getWurmId() + " but villageid=" + villageId);
                }
            }
            else {
                performer.getCommunicator().sendAlertServerMessage("You are no longer a citizen of a village.");
            }
        }
        else if (action == 863) {
            final ManageObjectList mol = new ManageObjectList(performer, ManageObjectList.Type.WAGONER);
            mol.sendQuestion();
            done = true;
        }
        else {
            done = super.action(act, performer, target, action, counter);
        }
        return done;
    }
    
    @Override
    public boolean action(final Action act, final Creature performer, final Item source, final Item target, final short action, final float counter) {
        boolean done = true;
        if (action == 176) {
            Methods.setVillageToken(performer, target);
        }
        else if (action == 31) {
            if (performer.getCurrentVillage() != null && performer.getCurrentVillage().kingdom == performer.getKingdomId() && !performer.getCurrentVillage().isEnemy(performer) && performer.isWithinTileDistanceTo(target.getTileX(), target.getTileY(), 0, 2)) {
                done = Methods.discardSellItem(performer, act, source, counter);
            }
        }
        else if (action == 68 || action == 77 || action == 222 || action == 226 || action == 350 || action == 334 || action == 34 || action == 513 || action == 194 || action == 80 || action == 67 || action == 1 || action == 66 || action == 348 || action == 372 || action == 349 || action == 70 || action == 69 || action == 356 || action == 355 || action == 76 || action == 371 || action == 481 || action == 540 || action == 209) {
            done = this.action(act, performer, target, action, counter);
        }
        else {
            done = super.action(act, performer, source, target, action, counter);
        }
        return done;
    }
    
    private List<ActionEntry> getVillageBehaviours(final Creature performer, final Item target) {
        final List<ActionEntry> toReturn = new LinkedList<ActionEntry>();
        int size = -3;
        final Village village = performer.getCitizenVillage();
        try {
            final Village currVill = Villages.getVillage(target.getData2());
            toReturn.addAll(getSettlementMenu(performer, performer.isWithinDistanceTo(target.getPosX(), target.getPosY(), target.getPosZ(), 4.0f), village, currVill));
            size = -3;
            toReturn.add(new ActionEntry((short)size, "Bank", "banking"));
            toReturn.add(Actions.actionEntrys[222]);
            toReturn.add(Actions.actionEntrys[226]);
            toReturn.add(Actions.actionEntrys[194]);
            toReturn.add(Actions.actionEntrys[334]);
            if (performer.isEconomicAdvisor() || performer.getPower() >= 3) {
                toReturn.add(Actions.actionEntrys[371]);
            }
            if (performer.getPower() >= 4 && currVill.isPermanent) {
                size = -2;
                toReturn.add(new ActionEntry((short)size, "Settlement tiles", "special"));
                toReturn.add(new ActionEntry((short)118, "Set to Lawn", "growing"));
                if (Zones.protectedTiles[target.getTileX()][target.getTileY()]) {
                    toReturn.add(Actions.actionEntrys[382]);
                }
                else {
                    toReturn.add(Actions.actionEntrys[381]);
                }
            }
        }
        catch (NoSuchVillageException nsv) {
            VillageTokenBehaviour.logger.log(Level.WARNING, "No settlement for token with id " + target.getWurmId() + " at " + target.getPosX() + ", " + target.getPosY() + ", surf=" + target.isOnSurface());
        }
        return toReturn;
    }
    
    static List<ActionEntry> getSettlementMenu(final Creature performer, final boolean closeToToken, final Village citizenVillage, final Village currentVillage) {
        boolean diplomat = false;
        boolean guards = false;
        boolean destroy = false;
        boolean disbanding = false;
        boolean settings = false;
        boolean roles = false;
        boolean citizens = false;
        boolean reputations = false;
        boolean gates = false;
        boolean resize = false;
        boolean enemy = false;
        boolean findguards = false;
        boolean warmonger = false;
        boolean twitter = false;
        if (citizenVillage != null && citizenVillage.mayDoDiplomacy(performer)) {
            diplomat = true;
        }
        if (citizenVillage != null && citizenVillage.mayDoDiplomacy(performer) && currentVillage != null && citizenVillage != currentVillage) {
            final boolean atPeace = citizenVillage.mayDeclareWarOn(currentVillage);
            if (atPeace) {
                warmonger = true;
            }
        }
        if (citizenVillage != null && citizenVillage.equals(currentVillage) && currentVillage.isActionAllowed((short)67, performer)) {
            guards = true;
        }
        if (currentVillage.isDisbanding() && currentVillage.equals(citizenVillage) && !performer.isGuest()) {
            disbanding = true;
        }
        if (citizenVillage != null && citizenVillage.equals(currentVillage)) {
            settings = citizenVillage.isActionAllowed((short)68, performer);
            roles = citizenVillage.isActionAllowed((short)540, performer);
            citizens = citizenVillage.isActionAllowed((short)66, performer);
            reputations = citizenVillage.isActionAllowed((short)69, performer);
            gates = false;
            resize = citizenVillage.isActionAllowed((short)76, performer);
            twitter = citizenVillage.isActionAllowed((short)481, performer);
            if (citizenVillage.isActionAllowed((short)348, performer) && !disbanding && !performer.isGuest()) {
                destroy = true;
            }
        }
        if (performer.getPower() >= 3) {
            if (!disbanding) {
                destroy = true;
            }
            findguards = true;
        }
        if (performer.getPower() >= 4) {
            settings = true;
        }
        if ((performer.isPaying() && currentVillage.kingdom != performer.getKingdomId()) || currentVillage.isEnemy(performer.getCitizenVillage())) {
            enemy = true;
        }
        final List<ActionEntry> actionMenu = new LinkedList<ActionEntry>();
        actionMenu.add(Actions.actionEntrys[670]);
        actionMenu.add(Actions.actionEntrys[72]);
        if (destroy && closeToToken) {
            actionMenu.add(Actions.actionEntrys[348]);
        }
        if (enemy) {
            actionMenu.add(Actions.actionEntrys[350]);
        }
        if (findguards) {
            actionMenu.add(new ActionEntry((short)34, "Find guards", "finding"));
        }
        actionMenu.add(Actions.actionEntrys[77]);
        if (King.currentEra > 0) {
            actionMenu.add(Actions.actionEntrys[356]);
        }
        if (King.currentEra > 0) {
            actionMenu.add(Actions.actionEntrys[355]);
        }
        if (citizens) {
            actionMenu.add(Actions.actionEntrys[66]);
        }
        if (gates) {
            actionMenu.add(Actions.actionEntrys[70]);
        }
        if (guards) {
            actionMenu.add(Actions.actionEntrys[67]);
        }
        if (diplomat) {
            actionMenu.add(Actions.actionEntrys[80]);
        }
        if (reputations) {
            actionMenu.add(Actions.actionEntrys[69]);
        }
        if (roles) {
            actionMenu.add(Actions.actionEntrys[540]);
        }
        if (settings) {
            actionMenu.add(Actions.actionEntrys[68]);
        }
        if (enemy && currentVillage.kingdom == performer.getKingdomId() && diplomat) {
            actionMenu.add(Actions.actionEntrys[210]);
        }
        if (resize) {
            actionMenu.add(Actions.actionEntrys[76]);
        }
        if (closeToToken && (currentVillage.isMayor(performer) || performer.getPower() >= 2)) {
            actionMenu.add(Actions.actionEntrys[689]);
        }
        if (disbanding) {
            actionMenu.add(Actions.actionEntrys[349]);
        }
        actionMenu.add(Actions.actionEntrys[71]);
        if (enemy && currentVillage.getGuards().length == 1 && closeToToken) {
            actionMenu.add(Actions.actionEntrys[513]);
        }
        if (twitter || performer.getPower() >= 2) {
            actionMenu.add(Actions.actionEntrys[481]);
        }
        if (warmonger) {
            actionMenu.add(Actions.actionEntrys[209]);
        }
        final List<ActionEntry> toReturn = new LinkedList<ActionEntry>();
        toReturn.add(new ActionEntry((short)(-actionMenu.size()), "Settlement", "Settlement options"));
        toReturn.addAll(actionMenu);
        return toReturn;
    }
    
    static {
        logger = Logger.getLogger(VillageTokenBehaviour.class.getName());
    }
}
