// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.behaviours;

import com.wurmonline.server.questions.KingdomHistory;
import com.wurmonline.server.questions.KingdomStatusQuestion;
import com.wurmonline.server.questions.PlayerProfileQuestion;
import com.wurmonline.server.questions.ManageFriends;
import com.wurmonline.server.items.Item;
import com.wurmonline.server.questions.WagonerDeliveriesQuestion;
import com.wurmonline.server.questions.ManageObjectList;
import com.wurmonline.server.villages.Village;
import com.wurmonline.server.creatures.Creatures;
import com.wurmonline.server.players.Player;
import java.util.Collection;
import com.wurmonline.server.Features;
import java.util.LinkedList;
import java.util.List;
import com.wurmonline.server.creatures.Creature;
import com.wurmonline.server.MiscConstants;

public final class ManageMenu implements MiscConstants
{
    static List<ActionEntry> getBehavioursFor(final Creature performer) {
        final List<ActionEntry> alist = new LinkedList<ActionEntry>();
        final List<ActionEntry> slist = new LinkedList<ActionEntry>();
        alist.add(new ActionEntry((short)663, "Animals", "managing"));
        alist.add(new ActionEntry((short)664, "Buildings", "managing"));
        alist.add(new ActionEntry((short)665, "Carts and Wagons", "managing"));
        if (Features.Feature.WAGONER.isEnabled()) {
            alist.add(Actions.actionEntrys[916]);
        }
        alist.add(Actions.actionEntrys[661]);
        alist.add(new ActionEntry((short)667, "Gates", "managing"));
        alist.add(new ActionEntry((short)364, "MineDoors", "managing"));
        alist.add(Actions.actionEntrys[566]);
        alist.add(Actions.actionEntrys[690]);
        if (performer.getCitizenVillage() != null) {
            final Village village = performer.getCitizenVillage();
            slist.addAll(VillageTokenBehaviour.getSettlementMenu(performer, false, village, village));
            alist.addAll(slist);
        }
        alist.add(new ActionEntry((short)668, "Ships", "managing"));
        if (Features.Feature.WAGONER.isEnabled() && Creatures.getManagedWagonersFor((Player)performer, -1).length > 0) {
            alist.add(Actions.actionEntrys[863]);
        }
        final int sz = (slist.size() > 0) ? (alist.size() - slist.size() + 1) : alist.size();
        final List<ActionEntry> toReturn = new LinkedList<ActionEntry>();
        toReturn.add(new ActionEntry((short)(-sz), "Manage", "Manage"));
        toReturn.addAll(alist);
        return toReturn;
    }
    
    static boolean isManageAction(final Creature performer, final short action) {
        return action == 663 || action == 664 || action == 665 || action == 687 || action == 667 || action == 364 || action == 668 || action == 669 || action == 670 || action == 690 || action == 661 || action == 566 || action == 77 || action == 71 || action == 72 || action == 355 || action == 356 || action == 80 || action == 67 || action == 68 || action == 540 || action == 66 || action == 69 || action == 70 || action == 76 || action == 481 || action == 863 || action == 916 || action == 738;
    }
    
    static boolean action(final Action act, final Creature performer, final short action, final float counter) {
        if (action == 663) {
            final ManageObjectList mol = new ManageObjectList(performer, ManageObjectList.Type.ANIMAL1);
            mol.sendQuestion();
            return true;
        }
        if (action == 664) {
            final ManageObjectList mol = new ManageObjectList(performer, ManageObjectList.Type.BUILDING);
            mol.sendQuestion();
            return true;
        }
        if (action == 665) {
            final ManageObjectList mol = new ManageObjectList(performer, ManageObjectList.Type.LARGE_CART);
            mol.sendQuestion();
            return true;
        }
        if (action == 667) {
            final ManageObjectList mol = new ManageObjectList(performer, ManageObjectList.Type.GATE);
            mol.sendQuestion();
            return true;
        }
        if (action == 364) {
            final ManageObjectList mol = new ManageObjectList(performer, ManageObjectList.Type.MINEDOOR);
            mol.sendQuestion();
            return true;
        }
        if (action == 668) {
            final ManageObjectList mol = new ManageObjectList(performer, ManageObjectList.Type.SHIP);
            mol.sendQuestion();
            return true;
        }
        if (action == 863) {
            final ManageObjectList mol = new ManageObjectList(performer, ManageObjectList.Type.WAGONER);
            mol.sendQuestion();
            return true;
        }
        if (action == 916) {
            final WagonerDeliveriesQuestion mdq = new WagonerDeliveriesQuestion(performer, -10L, true);
            mdq.sendQuestion();
            return true;
        }
        if (action == 670 && performer.getCitizenVillage() != null) {
            Methods.sendManageUpkeep(performer, null);
            return true;
        }
        if (action == 661) {
            final ManageFriends mf = new ManageFriends(performer);
            mf.sendQuestion();
            return true;
        }
        if (action == 566) {
            final PlayerProfileQuestion kq = new PlayerProfileQuestion(performer);
            kq.sendQuestion();
            return true;
        }
        if (action == 690) {
            final ManageObjectList mol = new ManageObjectList(performer, ManageObjectList.Type.SEARCH);
            mol.sendQuestion();
            return true;
        }
        final Village village = performer.getCitizenVillage();
        if (action == 77) {
            Methods.sendVillageInfo(performer, null);
            return true;
        }
        if (village != null && action == 71) {
            Methods.sendVillageHistory(performer, null);
            return true;
        }
        if (village != null && action == 72) {
            Methods.sendAreaHistory(performer, null);
            return true;
        }
        if (village != null && action == 355) {
            final KingdomStatusQuestion kq2 = new KingdomStatusQuestion(performer, "Kingdom status", "Kingdoms", performer.getWurmId());
            kq2.sendQuestion();
            return true;
        }
        if (village != null && action == 356) {
            final KingdomHistory kq3 = new KingdomHistory(performer, "Kingdom history", "History of the kingdoms", performer.getWurmId());
            kq3.sendQuestion();
            return true;
        }
        if (village != null && action == 80) {
            if (village.mayDoDiplomacy(performer)) {
                Methods.sendManageAllianceQuestion(performer, null);
            }
            else {
                performer.getCommunicator().sendNormalServerMessage("You may not perform diplomacy for " + village.getName() + ".");
            }
            return true;
        }
        if (village != null && action == 67) {
            if (village.isActionAllowed((short)67, performer)) {
                Methods.sendManageVillageGuardsQuestion(performer, null);
            }
            else {
                performer.getCommunicator().sendNormalServerMessage("You may not manage guards for " + village.getName() + ".");
            }
            return true;
        }
        if (village != null && action == 68) {
            if (village.isActionAllowed((short)68, performer)) {
                Methods.sendManageVillageSettingsQuestion(performer, null);
            }
            else {
                performer.getCommunicator().sendNormalServerMessage("You may not manage settings for " + village.getName() + ".");
            }
            return true;
        }
        if (village != null && action == 540) {
            if (village.isActionAllowed((short)540, performer)) {
                Methods.sendManageVillageRolesQuestion(performer, null);
            }
            else {
                performer.getCommunicator().sendNormalServerMessage("You may not manage roles for " + village.getName() + ".");
            }
            return true;
        }
        if (village != null && action == 66) {
            if (village.isActionAllowed((short)66, performer)) {
                Methods.sendManageVillageCitizensQuestion(performer, null);
            }
            else {
                performer.getCommunicator().sendNormalServerMessage("You may not manage citizens for " + village.getName() + ".");
            }
            return true;
        }
        if (village != null && action == 69) {
            final short laction = 69;
            if (village.isActionAllowed((short)69, performer)) {
                Methods.sendReputationManageQuestion(performer, null);
            }
            else {
                performer.getCommunicator().sendNormalServerMessage("You may not manage reputations for " + village.getName() + ".");
            }
            return true;
        }
        if (village != null && action == 70) {
            final short laction = 70;
            if (village.isActionAllowed((short)70, performer)) {
                Methods.sendManageVillageGatesQuestion(performer, null);
            }
            else {
                performer.getCommunicator().sendNormalServerMessage("You may not manage gates for " + village.getName() + ".");
            }
            return true;
        }
        if (village != null && action == 76) {
            if (village.isActionAllowed((short)76, performer)) {
                Methods.sendExpandVillageQuestion(performer, null);
            }
            else {
                performer.getCommunicator().sendNormalServerMessage("You may not resize the settlement " + village.getName() + ".");
            }
            return true;
        }
        if (village != null && action == 481) {
            final short laction = 481;
            if (village.isActionAllowed((short)481, performer)) {
                Methods.sendConfigureTwitter(performer, -10L, true, village.getName());
            }
            else {
                performer.getCommunicator().sendNormalServerMessage("You may not configure twitter for " + village.getName() + ".");
            }
            return true;
        }
        return true;
    }
}
