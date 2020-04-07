// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.behaviours;

import com.wurmonline.server.questions.WagonerDeliveriesQuestion;
import com.wurmonline.server.questions.WagonerSetupDeliveryQuestion;
import com.wurmonline.server.creatures.Delivery;
import com.wurmonline.server.Features;
import java.util.LinkedList;
import javax.annotation.Nullable;
import java.util.Collection;
import java.util.List;
import com.wurmonline.server.items.Item;
import com.wurmonline.server.creatures.Creature;
import java.util.logging.Logger;

final class WagonerContainerBehaviour extends ItemBehaviour
{
    private static final Logger logger;
    
    WagonerContainerBehaviour() {
        super((short)61);
    }
    
    @Override
    public List<ActionEntry> getBehavioursFor(final Creature performer, final Item target) {
        final List<ActionEntry> toReturn = super.getBehavioursFor(performer, target);
        toReturn.addAll(this.getBehavioursForWagonerContainer(performer, null, target));
        return toReturn;
    }
    
    @Override
    public List<ActionEntry> getBehavioursFor(final Creature performer, final Item source, final Item target) {
        final List<ActionEntry> toReturn = super.getBehavioursFor(performer, source, target);
        toReturn.addAll(this.getBehavioursForWagonerContainer(performer, source, target));
        return toReturn;
    }
    
    @Override
    public boolean action(final Action act, final Creature performer, final Item target, final short action, final float counter) {
        final boolean[] ans = this.wagonerContainerActions(act, performer, null, target, action, counter);
        if (ans[0]) {
            return ans[1];
        }
        return super.action(act, performer, target, action, counter);
    }
    
    @Override
    public boolean action(final Action act, final Creature performer, final Item source, final Item target, final short action, final float counter) {
        final boolean[] ans = this.wagonerContainerActions(act, performer, source, target, action, counter);
        if (ans[0]) {
            return ans[1];
        }
        return super.action(act, performer, source, target, action, counter);
    }
    
    private List<ActionEntry> getBehavioursForWagonerContainer(final Creature performer, @Nullable final Item source, final Item container) {
        final List<ActionEntry> toReturn = new LinkedList<ActionEntry>();
        if (Features.Feature.WAGONER.isEnabled()) {
            if (container.isPlanted() && !container.isSealedByPlayer() && !container.isEmpty(false)) {
                toReturn.add(Actions.actionEntrys[915]);
            }
            if (container.isSealedByPlayer()) {
                final Delivery delivery = Delivery.canViewDelivery(container, performer);
                if (delivery != null) {
                    toReturn.add(Actions.actionEntrys[918]);
                }
                if (Delivery.canUnSealContainer(container, performer)) {
                    toReturn.add(Actions.actionEntrys[740]);
                }
            }
        }
        return toReturn;
    }
    
    public boolean[] wagonerContainerActions(final Action act, final Creature performer, @Nullable final Item source, final Item container, final short action, final float counter) {
        if (Features.Feature.WAGONER.isEnabled()) {
            if (action == 915 && container.isPlanted() && !container.isSealedByPlayer() && !container.isEmpty(false)) {
                final WagonerSetupDeliveryQuestion wsdq = new WagonerSetupDeliveryQuestion(performer, container);
                wsdq.sendQuestion();
                return new boolean[] { true, true };
            }
            final Delivery delivery = Delivery.canViewDelivery(container, performer);
            if (delivery != null && action == 918 && container.isSealedByPlayer()) {
                final WagonerDeliveriesQuestion wdq = new WagonerDeliveriesQuestion(performer, delivery.getDeliveryId(), false);
                wdq.sendQuestion2();
                return new boolean[] { true, true };
            }
            if (Delivery.canUnSealContainer(container, performer)) {
                container.setIsSealedByPlayer(false);
            }
        }
        return new boolean[] { false, false };
    }
    
    static {
        logger = Logger.getLogger(WagonerContainerBehaviour.class.getName());
    }
}
