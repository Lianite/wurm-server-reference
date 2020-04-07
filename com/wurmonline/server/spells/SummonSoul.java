// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.spells;

import com.wurmonline.server.questions.SummonSoulQuestion;
import com.wurmonline.server.items.Item;
import com.wurmonline.mesh.Tiles;
import com.wurmonline.server.skills.Skill;
import com.wurmonline.server.Servers;
import com.wurmonline.server.creatures.Creature;

public class SummonSoul extends ReligiousSpell
{
    public static final int RANGE = 40;
    
    public SummonSoul() {
        super("Summon Soul", 934, 30, 100, 10, 80, 0L);
        this.targetCreature = true;
        this.targetItem = true;
        this.targetTile = true;
        this.description = "summons a willing player to your location";
        this.type = 2;
    }
    
    public static boolean mayCastSummonSoul(final Creature performer) {
        if (Servers.isThisAPvpServer() && performer.getEnemyPresense() > 0) {
            performer.getCommunicator().sendNormalServerMessage("Enemies are nearby, you cannot cast Summon Soul right now.");
            return false;
        }
        return true;
    }
    
    @Override
    boolean precondition(final Skill castSkill, final Creature performer, final int tilex, final int tiley, final int layer) {
        return mayCastSummonSoul(performer);
    }
    
    @Override
    boolean precondition(final Skill castSkill, final Creature performer, final int tilex, final int tiley, final int layer, final int heightOffset, final Tiles.TileBorderDirection dir) {
        return mayCastSummonSoul(performer);
    }
    
    @Override
    boolean precondition(final Skill castSkill, final Creature performer, final Creature target) {
        return mayCastSummonSoul(performer);
    }
    
    @Override
    boolean precondition(final Skill castSkill, final Creature performer, final Item target) {
        return mayCastSummonSoul(performer);
    }
    
    @Override
    void doEffect(final Skill castSkill, final double power, final Creature performer, final int tilex, final int tiley, final int layer, final int heightOffset) {
        final SummonSoulQuestion ssq = new SummonSoulQuestion(performer, "Summon Soul", "Which soul do you wish to summon?", performer.getWurmId());
        ssq.sendQuestion();
    }
    
    @Override
    void doEffect(final Skill castSkill, final double power, final Creature performer, final Item target) {
        final SummonSoulQuestion ssq = new SummonSoulQuestion(performer, "Summon Soul", "Which soul do you wish to summon?", performer.getWurmId());
        ssq.sendQuestion();
    }
    
    @Override
    void doEffect(final Skill castSkill, final double power, final Creature performer, final Creature target) {
        final SummonSoulQuestion ssq = new SummonSoulQuestion(performer, "Summon Soul", "Which soul do you wish to summon?", performer.getWurmId());
        ssq.sendQuestion();
    }
}
