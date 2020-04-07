// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.skills;

import com.wurmonline.server.items.Item;
import javax.annotation.Nullable;
import com.wurmonline.server.creatures.Creature;
import com.wurmonline.server.players.Player;
import com.wurmonline.server.creatures.NoSuchCreatureException;
import com.wurmonline.server.creatures.Creatures;
import com.wurmonline.server.NoSuchPlayerException;
import java.util.logging.Level;
import com.wurmonline.server.Players;
import com.wurmonline.server.WurmId;
import java.io.IOException;
import java.util.logging.Logger;

public final class TempSkill extends Skill
{
    private static Logger logger;
    
    public TempSkill(final int aNumber, final double aStartValue, final Skills aParent) {
        super(aNumber, aStartValue, aParent);
    }
    
    public TempSkill(final long aId, final Skills aParent, final int aNumber, final double aKnowledge, final double aMinimum, final long aLastused) {
        super(aId, aParent, aNumber, aKnowledge, aMinimum, aLastused);
    }
    
    public TempSkill(final long aId, final Skills aParent) throws IOException {
        super(aId, aParent);
    }
    
    @Override
    void save() throws IOException {
    }
    
    @Override
    void load() throws IOException {
    }
    
    @Override
    void saveValue(final boolean aPlayer) throws IOException {
    }
    
    @Override
    public void setJoat(final boolean aJoat) throws IOException {
    }
    
    @Override
    public void setNumber(final int newNumber) throws IOException {
        final long pid = this.parent.getId();
        if (WurmId.getType(pid) == 0) {
            try {
                final Player player = Players.getInstance().getPlayer(pid);
                final Skill realSkill = player.getSkills().learn(this.number, (float)this.knowledge, false);
                realSkill.setNumber(newNumber);
            }
            catch (NoSuchPlayerException nsp) {
                TempSkill.logger.log(Level.WARNING, "Unable to find owner for skill, parentid: " + pid, nsp);
            }
        }
        else {
            try {
                final Creature creature = Creatures.getInstance().getCreature(pid);
                final Skill realSkill = creature.getSkills().learn(this.number, (float)this.knowledge, false);
                realSkill.setNumber(newNumber);
            }
            catch (NoSuchCreatureException nsp2) {
                TempSkill.logger.log(Level.WARNING, "Unable to find owner for skill, parentid: " + pid, nsp2);
            }
        }
    }
    
    @Override
    protected void alterSkill(final double advanceMultiplicator, final boolean decay, final float times) {
        this.alterSkill(advanceMultiplicator, decay, times, false, 1.0);
    }
    
    @Override
    protected void alterSkill(final double advanceMultiplicator, final boolean decay, final float times, final boolean useNewSystem, final double skillDivider) {
        final long pid = this.parent.getId();
        if (WurmId.getType(pid) == 0) {
            try {
                final Player player = Players.getInstance().getPlayer(pid);
                final Skill realSkill = player.getSkills().learn(this.number, (float)this.knowledge, false);
                realSkill.alterSkill(advanceMultiplicator, decay, times, useNewSystem, skillDivider);
            }
            catch (NoSuchPlayerException nsp) {
                TempSkill.logger.log(Level.WARNING, "Unable to find owner for skill, parentid: " + pid, nsp);
            }
        }
        else {
            try {
                final Creature creature = Creatures.getInstance().getCreature(pid);
                final Skill realSkill = creature.getSkills().learn(this.number, (float)this.knowledge, false);
                realSkill.alterSkill(advanceMultiplicator, decay, times, useNewSystem, skillDivider);
            }
            catch (NoSuchCreatureException nsc) {
                TempSkill.logger.log(Level.WARNING, "Unable to find owner for skill, parentid: " + pid, nsc);
            }
        }
    }
    
    @Override
    public void setKnowledge(final double aKnowledge, final boolean load) {
        final long pid = this.parent.getId();
        if (WurmId.getType(pid) == 0) {
            try {
                final Player player = Players.getInstance().getPlayer(pid);
                final Skill realSkill = player.getSkills().learn(this.number, (float)this.knowledge, false);
                realSkill.setKnowledge(aKnowledge, load);
            }
            catch (NoSuchPlayerException nsp) {
                TempSkill.logger.log(Level.WARNING, "Unable to find owner for skill, parentid: " + pid, nsp);
            }
        }
        else {
            try {
                final Creature creature = Creatures.getInstance().getCreature(pid);
                final Skill realSkill = creature.getSkills().learn(this.number, (float)this.knowledge, false);
                realSkill.setKnowledge(aKnowledge, load);
            }
            catch (NoSuchCreatureException nsp2) {
                TempSkill.logger.log(Level.WARNING, "Unable to find owner for skill, parentid: " + pid, nsp2);
            }
        }
    }
    
    @Override
    public void setKnowledge(final double aKnowledge, final boolean load, final boolean setMinimum) {
        final long pid = this.parent.getId();
        if (WurmId.getType(pid) == 0) {
            try {
                final Player player = Players.getInstance().getPlayer(pid);
                final Skill realSkill = player.getSkills().learn(this.number, (float)this.knowledge, false);
                realSkill.setKnowledge(aKnowledge, load, setMinimum);
            }
            catch (NoSuchPlayerException nsp) {
                TempSkill.logger.log(Level.WARNING, "Unable to find owner for skill, parentid: " + pid, nsp);
            }
        }
        else {
            try {
                final Creature creature = Creatures.getInstance().getCreature(pid);
                final Skill realSkill = creature.getSkills().learn(this.number, (float)this.knowledge, false);
                realSkill.setKnowledge(aKnowledge, load, setMinimum);
            }
            catch (NoSuchCreatureException nsp2) {
                TempSkill.logger.log(Level.WARNING, "Unable to find owner for skill, parentid: " + pid, nsp2);
            }
        }
    }
    
    @Override
    public double skillCheck(final double check, final double bonus, final boolean test, final float times) {
        return this.skillCheck(check, bonus, test, 10.0f, true, 1.100000023841858, null, null);
    }
    
    @Override
    public double skillCheck(final double check, final double bonus, final boolean test, final float times, final boolean useNewSystem, final double skillDivider) {
        return this.skillCheck(check, bonus, test, 10.0f, true, 1.100000023841858, null, null);
    }
    
    @Override
    public double skillCheck(final double check, final double bonus, final boolean test, final float times, @Nullable final Creature skillowner, @Nullable final Creature opponent) {
        return this.skillCheck(check, bonus, test, 10.0f, true, 1.100000023841858, skillowner, opponent);
    }
    
    @Override
    public double skillCheck(final double check, final double bonus, final boolean test, final float times, final boolean useNewSystem, final double skillDivider, @Nullable final Creature skillowner, @Nullable final Creature opponent) {
        if (skillowner != null) {
            final Skill realSkill = skillowner.getSkills().learn(this.number, (float)this.knowledge, false);
            return realSkill.skillCheck(check, bonus, test, 10.0f, true, 1.100000023841858, skillowner, opponent);
        }
        final long pid = this.parent.getId();
        if (WurmId.getType(pid) == 0) {
            try {
                final Player player = Players.getInstance().getPlayer(pid);
                final Skill realSkill2 = player.getSkills().learn(this.number, (float)this.knowledge, false);
                return realSkill2.skillCheck(check, bonus, test, 10.0f, true, 1.100000023841858, skillowner, opponent);
            }
            catch (NoSuchPlayerException nsp) {
                TempSkill.logger.log(Level.WARNING, "Unable to find owner for skill, parentid: " + pid, nsp);
                return 0.0;
            }
        }
        try {
            final Creature creature = Creatures.getInstance().getCreature(pid);
            final Skill realSkill2 = creature.getSkills().learn(this.number, (float)this.knowledge, false);
            return realSkill2.skillCheck(check, bonus, test, 10.0f, true, 1.100000023841858, skillowner, opponent);
        }
        catch (NoSuchCreatureException nsp2) {
            TempSkill.logger.log(Level.WARNING, "Unable to find owner for skill, parentid: " + pid, nsp2);
            return 0.0;
        }
    }
    
    @Override
    public double skillCheck(final double check, final Item item, final double bonus, final boolean test, final float times, @Nullable final Creature skillowner, @Nullable final Creature opponent) {
        return this.skillCheck(check, item, bonus, test, 10.0f, true, 1.100000023841858, skillowner, opponent);
    }
    
    @Override
    public double skillCheck(final double check, final Item item, final double bonus, final boolean test, final float times, final boolean useNewSystem, final double skillDivider, @Nullable final Creature skillowner, @Nullable final Creature opponent) {
        if (skillowner != null) {
            final Skill realSkill = skillowner.getSkills().learn(this.number, (float)this.knowledge, false);
            return realSkill.skillCheck(check, item, bonus, test, 10.0f, true, 1.100000023841858, skillowner, opponent);
        }
        final long pid = this.parent.getId();
        if (WurmId.getType(pid) == 0) {
            try {
                final Player player = Players.getInstance().getPlayer(pid);
                final Skill realSkill2 = player.getSkills().learn(this.number, (float)this.knowledge, false);
                return realSkill2.skillCheck(check, item, bonus, test, 10.0f, true, 1.100000023841858, skillowner, opponent);
            }
            catch (NoSuchPlayerException nsp) {
                TempSkill.logger.log(Level.WARNING, "Unable to find owner for skill, parentid: " + pid, nsp);
                return 0.0;
            }
        }
        try {
            final Creature creature = Creatures.getInstance().getCreature(pid);
            final Skill realSkill2 = creature.getSkills().learn(this.number, (float)this.knowledge, false);
            return realSkill2.skillCheck(check, item, bonus, test, 10.0f, true, 1.100000023841858, skillowner, opponent);
        }
        catch (NoSuchCreatureException nsp2) {
            TempSkill.logger.log(Level.WARNING, "Unable to find owner for skill, parentid: " + pid, nsp2);
            return 0.0;
        }
    }
    
    @Override
    public double skillCheck(final double check, final Item item, final double bonus, final boolean test, final float times) {
        return this.skillCheck(check, item, bonus, test, 10.0f, true, 1.100000023841858, null, null);
    }
    
    @Override
    public double skillCheck(final double check, final Item item, final double bonus, final boolean test, final float times, final boolean useNewSystem, final double skillDivider) {
        return this.skillCheck(check, item, bonus, test, 10.0f, true, 1.100000023841858, null, null);
    }
    
    @Override
    public final boolean isTemporary() {
        return true;
    }
    
    static {
        TempSkill.logger = Logger.getLogger(TempSkill.class.getName());
    }
}
