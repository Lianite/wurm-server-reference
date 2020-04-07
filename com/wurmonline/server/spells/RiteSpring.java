// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.spells;

import com.wurmonline.server.HistoryManager;
import com.wurmonline.server.Server;
import com.wurmonline.server.deities.Deity;
import com.wurmonline.server.Servers;
import com.wurmonline.server.deities.Deities;
import com.wurmonline.server.items.Item;
import com.wurmonline.server.creatures.Creature;
import com.wurmonline.server.skills.Skill;

public class RiteSpring extends ReligiousSpell
{
    public static final int RANGE = 4;
    
    public RiteSpring() {
        super("Rite of Spring", 403, 100, 300, 60, 50, 43200000L);
        this.isRitual = true;
        this.targetItem = true;
        this.description = "followers of your god receives a permanent blessing";
        this.type = 0;
    }
    
    @Override
    boolean precondition(final Skill castSkill, final Creature performer, final Item target) {
        if (performer.getDeity() != null) {
            final Deity deity = performer.getDeity();
            final Deity templateDeity = Deities.getDeity(deity.getTemplateDeity());
            if (templateDeity.getFavor() < 100000 && !Servers.isThisATestServer()) {
                performer.getCommunicator().sendNormalServerMessage(deity.getName() + " can not grant that power right now.", (byte)3);
                return false;
            }
            if (target.getBless() == deity && target.isDomainItem()) {
                return true;
            }
            performer.getCommunicator().sendNormalServerMessage(String.format("You need to cast this spell at an altar of %s.", deity.getName()), (byte)3);
        }
        return false;
    }
    
    @Override
    void doEffect(final Skill castSkill, final double power, final Creature performer, final Item target) {
        final Deity deity = performer.getDeity();
        final Deity templateDeity = Deities.getDeity(deity.getTemplateDeity());
        performer.getCommunicator().sendNormalServerMessage("Followers of " + deity.getName() + " receive a blessing!", (byte)2);
        Server.getInstance().broadCastSafe("As the Rite of Spring is completed, followers of " + deity.getName() + " may now receive a blessing!");
        HistoryManager.addHistory(performer.getName(), "casts " + this.name + ". Followers of " + deity.getName() + " receive a blessing!");
        templateDeity.setFavor(templateDeity.getFavor() - 100000);
        performer.achievement(635);
        for (final Creature c : performer.getLinks()) {
            c.achievement(635);
        }
        new RiteEvent.RiteOfSpringEvent(-10, performer.getWurmId(), this.getNumber(), deity.getNumber(), System.currentTimeMillis(), 86400000L);
    }
}
