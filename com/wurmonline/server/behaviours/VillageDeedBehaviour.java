// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.behaviours;

import com.wurmonline.server.Items;
import com.wurmonline.server.LoginServerWebConnection;
import com.wurmonline.server.questions.KingdomHistory;
import com.wurmonline.server.questions.KingdomStatusQuestion;
import com.wurmonline.server.villages.Village;
import com.wurmonline.server.villages.NoSuchVillageException;
import java.util.logging.Level;
import com.wurmonline.server.villages.Villages;
import com.wurmonline.server.Servers;
import java.util.LinkedList;
import java.util.Collection;
import java.util.List;
import com.wurmonline.server.items.Item;
import com.wurmonline.server.creatures.Creature;
import java.util.logging.Logger;

final class VillageDeedBehaviour extends ItemBehaviour
{
    private static final Logger logger;
    
    VillageDeedBehaviour() {
        super((short)24);
    }
    
    @Override
    public List<ActionEntry> getBehavioursFor(final Creature performer, final Item target) {
        final List<ActionEntry> toReturn = super.getBehavioursFor(performer, target);
        toReturn.addAll(this.getBehavioursForPapers(performer, target));
        return toReturn;
    }
    
    @Override
    public List<ActionEntry> getBehavioursFor(final Creature performer, final Item source, final Item target) {
        final List<ActionEntry> toReturn = super.getBehavioursFor(performer, source, target);
        toReturn.addAll(this.getBehavioursForPapers(performer, target));
        return toReturn;
    }
    
    List<ActionEntry> getBehavioursForPapers(final Creature performer, final Item target) {
        final List<ActionEntry> toReturn = new LinkedList<ActionEntry>();
        final long ownerId = target.getOwnerId();
        if (ownerId == performer.getWurmId()) {
            final int villageId = target.getData2();
            if (villageId <= 0) {
                if (target.getTemplateId() != 663 && target.getTemplateId() != 862) {
                    toReturn.add(new ActionEntry((short)(Servers.localServer.testServer ? -2 : -1), "Settlement", "Settlement options"));
                    toReturn.add(Actions.actionEntrys[466]);
                    if (Servers.localServer.testServer) {
                        toReturn.add(Actions.actionEntrys[65]);
                    }
                }
                toReturn.add(new ActionEntry((short)(-1), "Settlement", "Settlement options"));
                toReturn.add(Actions.actionEntrys[65]);
            }
            else if (target.getTemplateId() != 663) {
                final int nums = -2;
                toReturn.add(new ActionEntry((short)(-2), "Settlement", "Settlement options"));
                toReturn.add(Actions.actionEntrys[78]);
                toReturn.add(Actions.actionEntrys[466]);
            }
            else {
                try {
                    final Village curVill = Villages.getVillage(villageId);
                    toReturn.addAll(VillageTokenBehaviour.getSettlementMenu(performer, false, curVill, curVill));
                }
                catch (NoSuchVillageException e) {
                    VillageDeedBehaviour.logger.log(Level.WARNING, e.getMessage(), e);
                }
            }
        }
        return toReturn;
    }
    
    @Override
    public boolean action(final Action act, final Creature performer, final Item target, final short action, final float counter) {
        boolean done = true;
        if (action == 1) {
            if (!target.isNewDeed()) {
                if (target.isVillageDeed()) {
                    try {
                        final Village village = Villages.getVillage(target.getData2());
                        performer.getCommunicator().sendNormalServerMessage("This is the village deed for " + village.getName() + ". You should replace it with the new version.");
                    }
                    catch (NoSuchVillageException nss) {
                        final int templateId = target.getTemplateId();
                        final int size = Villages.getSizeForDeed(templateId);
                        performer.getCommunicator().sendNormalServerMessage("A paper giving the possessor the right to found a village of the size " + size + ". You should refund it and use the new version instead.");
                        if (target.getData2() >= 1) {
                            VillageDeedBehaviour.logger.log(Level.WARNING, nss.getMessage(), nss);
                        }
                    }
                }
                else if (target.isHomesteadDeed()) {
                    try {
                        final Village stead = Villages.getVillage(target.getData2());
                        performer.getCommunicator().sendNormalServerMessage("This is the homestead deed for " + stead.getName() + ". You should replace it with the new version.");
                    }
                    catch (NoSuchVillageException nss) {
                        final int size2 = Villages.getSizeForDeed(target.getTemplateId());
                        performer.getCommunicator().sendNormalServerMessage("A paper giving the possessor the right to found a homestead of the size " + size2 + ". You should refund it and use the new version instead.");
                        if (target.getData2() >= 1) {
                            VillageDeedBehaviour.logger.log(Level.WARNING, nss.getMessage(), nss);
                        }
                    }
                }
            }
            else {
                try {
                    final Village village = Villages.getVillage(target.getData2());
                    performer.getCommunicator().sendNormalServerMessage("This is the settlement deed for " + village.getName() + ".");
                }
                catch (NoSuchVillageException nss) {
                    if (target.getData2() >= 1) {
                        VillageDeedBehaviour.logger.log(Level.WARNING, nss.getMessage(), nss);
                    }
                }
            }
        }
        else if (action == 77) {
            done = true;
            if (this.checkPapersOk(performer, target)) {
                Methods.sendVillageInfo(performer, target);
            }
        }
        else if (action == 670) {
            done = true;
            if (this.checkPapersOk(performer, target)) {
                Methods.sendManageUpkeep(performer, target);
            }
        }
        else if (action == 71) {
            done = true;
            if (this.checkPapersOk(performer, target)) {
                Methods.sendVillageHistory(performer, target);
            }
        }
        else if (action == 72) {
            done = true;
            if (this.checkPapersOk(performer, target)) {
                Methods.sendAreaHistory(performer, target);
            }
        }
        else if (action == 68) {
            done = true;
            if (this.checkPapersOk(performer, target)) {
                Methods.sendManageVillageSettingsQuestion(performer, target);
            }
        }
        else if (action == 540) {
            done = true;
            if (this.checkPapersOk(performer, target)) {
                Methods.sendManageVillageRolesQuestion(performer, target);
            }
        }
        else if (action == 69) {
            done = true;
            if (this.checkPapersOk(performer, target)) {
                Methods.sendReputationManageQuestion(performer, target);
            }
        }
        else if (action == 67) {
            done = true;
            if (this.checkPapersOk(performer, target)) {
                Methods.sendManageVillageGuardsQuestion(performer, target);
            }
        }
        else if (action == 66) {
            done = true;
            if (this.checkPapersOk(performer, target)) {
                Methods.sendManageVillageCitizensQuestion(performer, target);
            }
        }
        else if (action == 70) {
            done = true;
            if (this.checkPapersOk(performer, target)) {
                Methods.sendManageVillageGatesQuestion(performer, target);
            }
        }
        else if (action == 355) {
            done = true;
            final KingdomStatusQuestion kq = new KingdomStatusQuestion(performer, "Kingdom status", "Kingdoms", performer.getWurmId());
            kq.sendQuestion();
        }
        else if (action == 356) {
            done = true;
            final KingdomHistory kq2 = new KingdomHistory(performer, "Kingdom history", "History of the kingdoms", performer.getWurmId());
            kq2.sendQuestion();
        }
        else if (action == 65) {
            if (!target.isNewDeed() && !Servers.localServer.testServer && target.getTemplateId() != 862) {
                performer.getCommunicator().sendSafeServerMessage("You need to refund this deed and purchase a new one instead.");
                return true;
            }
            final long ownerId = target.getOwnerId();
            if (ownerId == performer.getWurmId()) {
                final int villageId = target.getData2();
                if (villageId > 0) {
                    performer.getCommunicator().sendSafeServerMessage("This settlement is already founded!");
                }
                else {
                    Methods.sendFoundVillageQuestion(performer, target);
                }
            }
            else {
                VillageDeedBehaviour.logger.log(Level.WARNING, performer.getName() + " trying to manage deed which isn't his.");
            }
        }
        if (action == 76) {
            if (target.isNewDeed()) {
                Methods.sendExpandVillageQuestion(performer, target);
            }
            else {
                VillageDeedBehaviour.logger.log(Level.WARNING, performer.getName() + " shouldn't be able to do this with a " + target.getName() + ".");
            }
        }
        else if (action == 466) {
            done = true;
            if (!target.isNewDeed() && target.isOldDeed()) {
                final int villageId2 = target.getData2();
                if (villageId2 > 0) {
                    performer.getCommunicator().sendSafeServerMessage("This village/homestead is already founded. Disband first, then refund.");
                }
                else if (performer.getWurmId() == target.getOwnerId()) {
                    final long left = target.getValue();
                    if (left > 0L) {
                        final LoginServerWebConnection lsw = new LoginServerWebConnection();
                        if (!lsw.addMoney(performer.getWurmId(), performer.getName(), left, "Refund " + target.getWurmId())) {
                            performer.getCommunicator().sendSafeServerMessage("Failed to contact your bank. Please try later.");
                        }
                        else {
                            Items.destroyItem(target.getWurmId());
                        }
                    }
                }
            }
        }
        else if (action == 78) {
            done = true;
            final int villageId2 = target.getData2();
            if (villageId2 > 0 && target.getOwnerId() == performer.getWurmId()) {
                try {
                    final Village village2 = Villages.getVillage(villageId2);
                    village2.replaceDeed(performer, target);
                }
                catch (NoSuchVillageException nsv) {
                    performer.getCommunicator().sendSafeServerMessage("Failed to locate the village that this deed is for!");
                }
            }
        }
        else if (action == 80) {
            done = true;
            if (this.checkPapersOk(performer, target)) {
                Methods.sendManageAllianceQuestion(performer, target);
            }
        }
        else if (action == 481) {
            done = true;
            final int villageId2 = target.getData2();
            if (villageId2 <= 0) {
                performer.getCommunicator().sendSafeServerMessage("This settlement is not founded yet!");
                VillageDeedBehaviour.logger.log(Level.WARNING, performer.getName() + " managing deed with id " + target.getWurmId() + " but villageid=" + villageId2);
            }
            else {
                try {
                    final Village currVill = Villages.getVillage(target.getData2());
                    if (currVill.isActionAllowed((short)68, performer) || performer.getPower() >= 2) {
                        Methods.sendConfigureTwitter(performer, target.getData2(), true, currVill.getName());
                    }
                    else {
                        performer.getCommunicator().sendSafeServerMessage("Illegal option.");
                        VillageDeedBehaviour.logger.log(Level.WARNING, performer.getName() + " cheating? Illegal option for " + target.getWurmId() + " at villageid=" + villageId2);
                    }
                }
                catch (NoSuchVillageException nsv) {
                    VillageDeedBehaviour.logger.log(Level.WARNING, performer.getName() + " managing token with id " + target.getWurmId() + " but villageid=" + villageId2);
                }
            }
        }
        else {
            done = super.action(act, performer, target, action, counter);
        }
        return done;
    }
    
    private boolean checkPapersOk(final Creature performer, final Item target) {
        if (!target.isNewDeed()) {
            performer.getCommunicator().sendSafeServerMessage("You need to replace the deed first.");
        }
        else {
            final long ownerId = target.getOwnerId();
            if (ownerId == performer.getWurmId()) {
                final int villageId = target.getData2();
                if (villageId > 0) {
                    return true;
                }
                performer.getCommunicator().sendSafeServerMessage("This settlement is not founded yet!");
                VillageDeedBehaviour.logger.log(Level.WARNING, performer.getName() + " managing deed with id " + target.getWurmId() + " but tried to do illegal action since villageid=" + villageId);
            }
            else {
                VillageDeedBehaviour.logger.log(Level.WARNING, performer.getName() + " trying to manage deed which isn't theirs.");
            }
        }
        return false;
    }
    
    static {
        logger = Logger.getLogger(VillageDeedBehaviour.class.getName());
    }
}
