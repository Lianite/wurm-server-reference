// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.behaviours;

import java.util.NoSuchElementException;
import java.util.Iterator;
import com.wurmonline.server.creatures.Creature;
import java.util.ListIterator;
import java.util.LinkedList;
import com.wurmonline.server.TimeConstants;

public final class ActionStack implements TimeConstants
{
    private final LinkedList<Action> quickActions;
    private final LinkedList<Action> slowActions;
    private boolean clearing;
    private long lastPolledStunned;
    
    public ActionStack() {
        this.clearing = false;
        this.lastPolledStunned = 0L;
        this.quickActions = new LinkedList<Action>();
        this.slowActions = new LinkedList<Action>();
    }
    
    public void addAction(final Action action) {
        int maxPrio = 1;
        if (action.isQuick()) {
            if (this.quickActions.size() < 10) {
                this.quickActions.addLast(action);
            }
            else {
                action.getPerformer().getCommunicator().sendSafeServerMessage("You can't remember that many things to do in advance.");
            }
        }
        else if (!this.slowActions.isEmpty()) {
            if (!Action.isStackable(action.getNumber())) {
                action.getPerformer().getCommunicator().sendNormalServerMessage("You're too busy.");
                return;
            }
            if (this.slowActions.size() > 1 && !Action.isStackableFight(action.getNumber())) {
                final ListIterator<Action> it = this.slowActions.listIterator();
                while (it.hasNext()) {
                    final Action curr = it.next();
                    if (curr.getNumber() == action.getNumber()) {
                        action.getPerformer().getCommunicator().sendNormalServerMessage("You're too busy.");
                        return;
                    }
                }
            }
            boolean insertedAndShouldPoll = false;
            final ListIterator<Action> it2 = this.slowActions.listIterator();
            while (it2.hasNext()) {
                final Action curr2 = it2.next();
                if (maxPrio < curr2.getPriority()) {
                    maxPrio = curr2.getPriority();
                    if (action.getPriority() <= maxPrio) {
                        continue;
                    }
                    it2.previous();
                    if (action.getNumber() == 114) {
                        insertedAndShouldPoll = true;
                        it2.add(action);
                        break;
                    }
                    it2.add(action);
                    return;
                }
            }
            if (insertedAndShouldPoll) {
                action.poll();
                return;
            }
            if (action.getPerformer().isPlayer() && this.slowActions.size() > action.getPerformer().getMaxNumActions()) {
                if (!Action.isActionAttack(action.getNumber())) {
                    action.getPerformer().getCommunicator().sendNormalServerMessage("You're too busy.");
                }
            }
            else {
                if (Actions.actionEntrys[this.slowActions.getLast().getNumber()] == Actions.actionEntrys[action.getNumber()] && !Action.isActionAttack(action.getNumber())) {
                    action.getPerformer().getCommunicator().sendNormalServerMessage("After you " + Actions.actionEntrys[this.slowActions.getLast().getNumber()].getVerbFinishString() + " you will " + Actions.actionEntrys[action.getNumber()].getVerbStartString() + " again.");
                }
                else if (!this.slowActions.getLast().isOffensive() && !Action.isActionAttack(action.getNumber())) {
                    action.getPerformer().getCommunicator().sendNormalServerMessage("After you " + Actions.actionEntrys[this.slowActions.getLast().getNumber()].getVerbFinishString() + " you will " + Actions.actionEntrys[action.getNumber()].getVerbStartString() + ".");
                }
                else if (this.slowActions.getLast().isOffensive() && action.isSpell()) {
                    action.getPerformer().getCommunicator().sendCombatNormalMessage("After you " + Actions.actionEntrys[this.slowActions.getLast().getNumber()].getVerbFinishString() + " you will " + Actions.actionEntrys[action.getNumber()].getVerbStartString() + ".");
                }
                this.slowActions.addLast(action);
            }
        }
        else if (action.getNumber() == 114) {
            if (!action.poll()) {
                this.slowActions.add(action);
            }
        }
        else {
            this.slowActions.add(action);
        }
    }
    
    private void removeAction(final Action action) {
        this.quickActions.remove(action);
        this.slowActions.remove(action);
    }
    
    public String stopCurrentAction(final boolean farAway) throws NoSuchActionException {
        String toReturn = "";
        final Action current = this.getCurrentAction();
        if (current.getNumber() == 136) {
            current.getPerformer().setStealth(current.getPerformer().isStealth());
        }
        toReturn = current.stop(farAway);
        if (current.getNumber() == 160) {
            MethodsFishing.playerOutOfRange(current.getPerformer(), current);
        }
        if (current.getNumber() == 925 || current.getNumber() == 926) {
            current.getPerformer().getCommunicator().sendCancelPlacingItem();
            toReturn = "";
        }
        this.removeAction(current);
        return toReturn;
    }
    
    public Action getCurrentAction() throws NoSuchActionException {
        if (!this.quickActions.isEmpty()) {
            return this.quickActions.getFirst();
        }
        if (!this.slowActions.isEmpty()) {
            return this.slowActions.getFirst();
        }
        throw new NoSuchActionException("No Current Action");
    }
    
    public boolean poll(final Creature owner) {
        boolean toReturn = true;
        if (owner.getStatus().getStunned() > 0.0f && !owner.isDead()) {
            if (this.lastPolledStunned == 0L) {
                this.lastPolledStunned = System.currentTimeMillis();
            }
            toReturn = false;
            final float delta = (System.currentTimeMillis() - this.lastPolledStunned) / 1000.0f;
            owner.getStatus().setStunned(owner.getStatus().getStunned() - delta, false);
            if (owner.getStatus().getStunned() <= 0.0f) {
                this.lastPolledStunned = 0L;
            }
            else {
                this.lastPolledStunned = System.currentTimeMillis();
            }
        }
        else if (!this.quickActions.isEmpty()) {
            while (!this.quickActions.isEmpty()) {
                if (this.quickActions.getFirst().poll()) {
                    this.quickActions.removeFirst();
                }
            }
        }
        else if (!this.slowActions.isEmpty()) {
            Action first = this.slowActions.getFirst();
            if (first.poll()) {
                if (!this.slowActions.isEmpty()) {
                    this.slowActions.removeFirst();
                }
                if (!this.slowActions.isEmpty()) {
                    first = this.slowActions.getFirst();
                    if (first.getCounterAsFloat() >= 1.0f && first.getNumber() != 114 && first.getNumber() != 160) {
                        owner.sendActionControl(first.getActionString(), true, first.getTimeLeft());
                    }
                    else if (first.getNumber() != 160) {
                        owner.sendActionControl("", false, 0);
                    }
                }
                else {
                    owner.sendActionControl("", false, 0);
                }
            }
            else {
                toReturn = false;
            }
        }
        return toReturn;
    }
    
    public void removeAttacks(final Creature owner) {
        if (!this.clearing) {
            final ListIterator<Action> lit = this.slowActions.listIterator();
            while (lit.hasNext()) {
                final Action act = lit.next();
                if (act.getNumber() == 114) {
                    lit.remove();
                }
            }
        }
    }
    
    public void removeTarget(final long wurmid) {
        if (!this.clearing) {
            final ListIterator<Action> lit = this.slowActions.listIterator();
            while (lit.hasNext()) {
                final Action act = lit.next();
                if (act.getTarget() == wurmid) {
                    try {
                        if (act == this.getCurrentAction()) {
                            act.getPerformer().getCommunicator().sendNormalServerMessage(act.stop(false));
                            act.getPerformer().sendActionControl("", false, 0);
                        }
                    }
                    catch (NoSuchActionException ex) {}
                    lit.remove();
                }
            }
        }
    }
    
    public void replaceTarget(final long wurmid) {
        if (!this.clearing) {
            final ListIterator<Action> lit = this.slowActions.listIterator();
            while (lit.hasNext()) {
                final Action act = lit.next();
                if (act.isOffensive()) {
                    act.setTarget(wurmid);
                }
            }
        }
    }
    
    public void clear() {
        this.clearing = true;
        this.quickActions.clear();
        for (final Action actionToStop : this.slowActions) {
            actionToStop.stop(false);
        }
        this.slowActions.clear();
        this.clearing = false;
    }
    
    public Action getLastSlowAction() {
        if (!this.clearing && !this.slowActions.isEmpty()) {
            try {
                return this.slowActions.getLast();
            }
            catch (NoSuchElementException nse) {
                return null;
            }
        }
        return null;
    }
}
