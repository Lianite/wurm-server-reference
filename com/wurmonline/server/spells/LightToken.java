// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.spells;

import com.wurmonline.server.FailedException;
import com.wurmonline.server.items.NoSuchTemplateException;
import com.wurmonline.server.items.ItemFactory;
import com.wurmonline.server.items.Item;
import com.wurmonline.server.creatures.Creature;
import com.wurmonline.server.skills.Skill;

public class LightToken extends ReligiousSpell
{
    public static final int RANGE = 4;
    
    public LightToken() {
        super("Light Token", 421, 10, 5, 10, 20, 0L);
        this.targetItem = true;
        this.targetTile = true;
        this.targetCreature = true;
        this.description = "creates a bright light item";
        this.type = 0;
    }
    
    @Override
    void doEffect(final Skill castSkill, final double power, final Creature performer, final int tilex, final int tiley, final int layer, final int heightOffset) {
        this.createToken(performer, power);
    }
    
    @Override
    void doEffect(final Skill castSkill, final double power, final Creature performer, final Item target) {
        this.createToken(performer, power);
    }
    
    @Override
    void doEffect(final Skill castSkill, final double power, final Creature performer, final Creature target) {
        this.createToken(performer, power);
    }
    
    void createToken(final Creature performer, final double power) {
        try {
            final Item token = ItemFactory.createItem(649, (float)Math.max(50.0, power), performer.getName());
            performer.getInventory().insertItem(token);
            performer.getCommunicator().sendNormalServerMessage("Something starts shining in your pocket.", (byte)2);
        }
        catch (NoSuchTemplateException ex) {}
        catch (FailedException ex2) {}
    }
}
