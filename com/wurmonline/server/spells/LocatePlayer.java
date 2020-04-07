// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.spells;

import com.wurmonline.server.items.Item;
import com.wurmonline.server.questions.LocatePlayerQuestion;
import com.wurmonline.server.creatures.Creature;
import com.wurmonline.server.skills.Skill;

public class LocatePlayer extends ReligiousSpell
{
    public static final int RANGE = 40;
    
    public LocatePlayer() {
        super("Locate Soul", 419, 10, 20, 10, 20, 120000L);
        this.targetCreature = true;
        this.targetItem = true;
        this.targetTile = true;
        this.description = "locates a player and corpses";
        this.type = 2;
    }
    
    @Override
    void doEffect(final Skill castSkill, final double power, final Creature performer, final int tilex, final int tiley, final int layer, final int heightOffset) {
        final LocatePlayerQuestion lpq = new LocatePlayerQuestion(performer, "Locate a soul", "Which soul do you wish to locate?", performer.getWurmId(), false, power);
        lpq.sendQuestion();
    }
    
    @Override
    void doEffect(final Skill castSkill, final double power, final Creature performer, final Item target) {
        final LocatePlayerQuestion lpq = new LocatePlayerQuestion(performer, "Locate a soul", "Which soul do you wish to locate?", performer.getWurmId(), false, power);
        lpq.sendQuestion();
    }
    
    @Override
    void doEffect(final Skill castSkill, final double power, final Creature performer, final Creature target) {
        final LocatePlayerQuestion lpq = new LocatePlayerQuestion(performer, "Locate a soul", "Which soul do you wish to locate?", performer.getWurmId(), false, power);
        lpq.sendQuestion();
    }
}
