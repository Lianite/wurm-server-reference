// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.behaviours;

import com.wurmonline.server.questions.WagonerDismissQuestion;
import com.wurmonline.server.questions.WagonerHistory;
import com.wurmonline.server.questions.PermissionsHistory;
import com.wurmonline.server.creatures.NoSuchCreatureException;
import java.util.logging.Level;
import com.wurmonline.server.players.PermissionsPlayerList;
import com.wurmonline.server.questions.ManagePermissions;
import com.wurmonline.server.creatures.Creatures;
import com.wurmonline.server.questions.ManageObjectList;
import com.wurmonline.server.Servers;
import com.wurmonline.server.creatures.Wagoner;
import com.wurmonline.server.Features;
import java.util.LinkedList;
import javax.annotation.Nullable;
import java.util.Collection;
import java.util.List;
import com.wurmonline.server.items.Item;
import com.wurmonline.server.creatures.Creature;
import java.util.logging.Logger;

final class WagonerContractBehaviour extends ItemBehaviour
{
    private static final Logger logger;
    
    WagonerContractBehaviour() {
        super((short)59);
    }
    
    @Override
    public List<ActionEntry> getBehavioursFor(final Creature performer, final Item target) {
        final List<ActionEntry> toReturn = super.getBehavioursFor(performer, target);
        toReturn.addAll(this.getBehavioursForWagonerContract(performer, null, target));
        return toReturn;
    }
    
    @Override
    public List<ActionEntry> getBehavioursFor(final Creature performer, final Item source, final Item target) {
        final List<ActionEntry> toReturn = super.getBehavioursFor(performer, source, target);
        toReturn.addAll(this.getBehavioursForWagonerContract(performer, source, target));
        return toReturn;
    }
    
    @Override
    public boolean action(final Action act, final Creature performer, final Item target, final short action, final float counter) {
        final boolean[] ans = this.wagonerContractActions(act, performer, null, target, action, counter);
        if (ans[0]) {
            return ans[1];
        }
        return super.action(act, performer, target, action, counter);
    }
    
    @Override
    public boolean action(final Action act, final Creature performer, final Item source, final Item target, final short action, final float counter) {
        final boolean[] ans = this.wagonerContractActions(act, performer, source, target, action, counter);
        if (ans[0]) {
            return ans[1];
        }
        return super.action(act, performer, source, target, action, counter);
    }
    
    private List<ActionEntry> getBehavioursForWagonerContract(final Creature performer, @Nullable final Item source, final Item contract) {
        final List<ActionEntry> toReturn = new LinkedList<ActionEntry>();
        if (Features.Feature.WAGONER.isEnabled() && contract.getData() != -1L) {
            final Wagoner wagoner = Wagoner.getWagoner(contract.getData());
            if (wagoner != null && wagoner.getVillageId() != -1) {
                final List<ActionEntry> waglist = new LinkedList<ActionEntry>();
                waglist.add(new ActionEntry((short)(-2), "Permissions", "viewing"));
                waglist.add(Actions.actionEntrys[863]);
                waglist.add(new ActionEntry((short)691, "Permissions History", "viewing"));
                waglist.add(Actions.actionEntrys[919]);
                waglist.add(new ActionEntry((short)566, "Manage chat options", "managing"));
                waglist.add(Actions.actionEntrys[920]);
                if (!waglist.isEmpty()) {
                    toReturn.add(new ActionEntry((short)(-(waglist.size() - 2)), wagoner.getName(), "wagoner"));
                    toReturn.addAll(waglist);
                }
                if (Servers.isThisATestServer()) {
                    final List<ActionEntry> testlist = new LinkedList<ActionEntry>();
                    if (wagoner.getState() == 0) {
                        testlist.add(new ActionEntry((short)140, "Send to bed", "testing"));
                        testlist.add(new ActionEntry((short)111, "Test delivery", "testing"));
                    }
                    if (wagoner.getState() == 2) {
                        testlist.add(new ActionEntry((short)30, "Wake up", "testing"));
                    }
                    if (wagoner.getState() == 14) {
                        testlist.add(new ActionEntry((short)644, "Force Park", "parking"));
                        testlist.add(new ActionEntry((short)636, "Send Home", "parking"));
                    }
                    else if (wagoner.getState() == 15) {
                        testlist.add(new ActionEntry((short)917, "Cancel driving", "cancelling"));
                    }
                    testlist.add(new ActionEntry((short)185, "Show state", "checking"));
                    if (!testlist.isEmpty()) {
                        toReturn.add(new ActionEntry((short)(-testlist.size()), "Test only", "test only"));
                        toReturn.addAll(testlist);
                    }
                }
            }
        }
        return toReturn;
    }
    
    public boolean[] wagonerContractActions(final Action act, final Creature performer, @Nullable final Item source, final Item contract, final short action, final float counter) {
        if (Features.Feature.WAGONER.isEnabled() && contract.getTemplateId() == 1129 && contract.getData() != -1L) {
            final Wagoner wagoner = Wagoner.getWagoner(contract.getData());
            if (wagoner == null) {
                performer.getCommunicator().sendNormalServerMessage("Cannot find the wagoner associated with this contract.");
                contract.setData(-10L);
                contract.setDescription("");
                return new boolean[] { true, true };
            }
            if (wagoner.getVillageId() == -1) {
                performer.getCommunicator().sendNormalServerMessage("Wagoner is in progress of being dismissed..");
                return new boolean[] { true, true };
            }
            if (action == 863) {
                final ManageObjectList.Type molt = ManageObjectList.Type.WAGONER;
                try {
                    final Creature creature = Creatures.getInstance().getCreature(contract.getData());
                    final ManagePermissions mp = new ManagePermissions(performer, molt, creature, false, -10L, false, null, "");
                    mp.sendQuestion();
                }
                catch (NoSuchCreatureException e) {
                    WagonerContractBehaviour.logger.log(Level.WARNING, "Cannot find the wagoner (" + contract.getData() + ") associated with the contract." + e.getMessage(), e);
                    performer.getCommunicator().sendNormalServerMessage("Cannot find the wagoner associated with this contract.");
                }
                return new boolean[] { true, true };
            }
            if (action == 691) {
                final PermissionsHistory ph = new PermissionsHistory(performer, contract.getData());
                ph.sendQuestion();
                return new boolean[] { true, true };
            }
            if (action == 919) {
                final WagonerHistory wh = new WagonerHistory(performer, wagoner);
                wh.sendQuestion();
                return new boolean[] { true, true };
            }
            if (action == 920) {
                final WagonerDismissQuestion wdq = new WagonerDismissQuestion(performer, wagoner);
                wdq.sendQuestion();
                return new boolean[] { true, true };
            }
            if (action == 140) {
                if (Servers.isThisATestServer() && wagoner.getState() == 0) {
                    wagoner.forceStateChange((byte)1);
                }
                return new boolean[] { true, true };
            }
            if (action == 111) {
                if (Servers.isThisATestServer() && wagoner.getState() == 0) {
                    wagoner.forceStateChange((byte)4);
                }
                return new boolean[] { true, true };
            }
            if (action == 30) {
                if (Servers.isThisATestServer() && wagoner.getState() == 2) {
                    wagoner.forceStateChange((byte)3);
                }
                return new boolean[] { true, true };
            }
            if (action == 917) {
                if (Servers.isThisATestServer()) {
                    wagoner.forceStateChange((byte)14);
                }
                return new boolean[] { true, true };
            }
            if (action == 636) {
                if (Servers.isThisATestServer()) {
                    wagoner.setGoalWaystoneId(wagoner.getHomeWaystoneId());
                    wagoner.calculateRoute();
                    wagoner.forceStateChange((byte)9);
                }
                return new boolean[] { true, true };
            }
            if (action == 644) {
                if (Features.Feature.WAGONER.isEnabled()) {
                    wagoner.setGoalWaystoneId(wagoner.getHomeWaystoneId());
                    wagoner.forceStateChange((byte)10);
                }
                return new boolean[] { true, true };
            }
        }
        return new boolean[] { false, false };
    }
    
    static {
        logger = Logger.getLogger(WagonerContractBehaviour.class.getName());
    }
}
