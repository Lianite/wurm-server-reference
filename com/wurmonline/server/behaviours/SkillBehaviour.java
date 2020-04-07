// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.behaviours;

import java.util.LinkedList;
import java.util.List;
import com.wurmonline.server.skills.Skill;
import com.wurmonline.server.items.Item;
import com.wurmonline.server.creatures.Creature;

public class SkillBehaviour extends Behaviour
{
    public SkillBehaviour() {
        super((short)42);
    }
    
    @Override
    public List<ActionEntry> getBehavioursFor(final Creature performer, final Item subject, final Skill skill) {
        final List<ActionEntry> toReturn = new LinkedList<ActionEntry>();
        return toReturn;
    }
    
    @Override
    public List<ActionEntry> getBehavioursFor(final Creature performer, final Skill skill) {
        final List<ActionEntry> toReturn = new LinkedList<ActionEntry>();
        return toReturn;
    }
    
    @Override
    public boolean action(final Action act, final Creature performer, final Item source, final Skill skill, final short action, final float counter) {
        return this.action(act, performer, skill, action, counter);
    }
    
    @Override
    public boolean action(final Action act, final Creature performer, final Skill skill, final short action, final float counter) {
        if (action == 1) {
            performer.getCommunicator().sendNormalServerMessage("This is the skill " + skill.getName() + ". Use 'Find on Wurmpedia' to see an explanation.");
        }
        return true;
    }
}
