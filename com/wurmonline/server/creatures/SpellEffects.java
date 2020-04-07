// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.creatures;

import com.wurmonline.server.WurmId;
import java.util.HashMap;
import com.wurmonline.server.NoSuchPlayerException;
import java.util.logging.Level;
import com.wurmonline.server.Server;
import java.util.logging.Logger;
import com.wurmonline.server.spells.SpellEffect;
import java.util.Map;

public final class SpellEffects
{
    private Creature creature;
    private final Map<Byte, SpellEffect> spellEffects;
    private static final Logger logger;
    private static final SpellEffect[] EMPTY_SPELLS;
    
    SpellEffects(final long _creatureId) {
        try {
            this.creature = Server.getInstance().getCreature(_creatureId);
        }
        catch (NoSuchCreatureException ex) {}
        catch (NoSuchPlayerException nsp) {
            SpellEffects.logger.log(Level.INFO, nsp.getMessage(), nsp);
        }
        this.spellEffects = new HashMap<Byte, SpellEffect>();
        if (WurmId.getType(_creatureId) == 0) {
            final SpellEffect[] speffs = SpellEffect.loadEffectsForPlayer(_creatureId);
            for (int x = 0; x < speffs.length; ++x) {
                this.addSpellEffect(speffs[x]);
            }
        }
    }
    
    public Creature getCreature() {
        return this.creature;
    }
    
    public void addSpellEffect(final SpellEffect effect) {
        final SpellEffect old = this.getSpellEffect(effect.type);
        if (old != null && old.power > effect.power) {
            effect.delete();
            return;
        }
        if (old != null) {
            old.delete();
            if (this.creature != null) {
                this.creature.sendUpdateSpellEffect(effect);
            }
        }
        else if (this.creature != null) {
            this.creature.sendAddSpellEffect(effect);
        }
        this.spellEffects.put(effect.type, effect);
        if (effect.type == 22 && this.creature.getCurrentTile() != null) {
            this.creature.getCurrentTile().setNewRarityShader(this.creature);
        }
    }
    
    public void sendAllSpellEffects() {
        if (this.creature != null) {
            for (final SpellEffect sp : this.getEffects()) {
                this.creature.sendAddSpellEffect(sp);
            }
        }
    }
    
    public SpellEffect getSpellEffect(final byte type) {
        final Byte key = type;
        if (this.spellEffects.containsKey(key)) {
            return this.spellEffects.get(key);
        }
        return null;
    }
    
    public SpellEffect[] getEffects() {
        if (this.spellEffects.size() > 0) {
            return this.spellEffects.values().toArray(new SpellEffect[this.spellEffects.size()]);
        }
        return SpellEffects.EMPTY_SPELLS;
    }
    
    public void poll() {
        final SpellEffect[] effects = this.getEffects();
        for (int x = 0; x < effects.length; ++x) {
            if (effects[x].type == 94 && Server.rand.nextInt(10) == 0) {
                final Creature c = this.getCreature();
                try {
                    c.addWoundOfType(null, (byte)4, c.getBody().getRandomWoundPos(), false, 0.0f, true, Math.max(20.0f, effects[x].getPower()) * 50.0f, 0.0f, 0.0f, false, true);
                    c.getCommunicator().sendAlertServerMessage("The pain from the heat is excruciating!");
                }
                catch (Exception e) {
                    SpellEffects.logger.log(Level.WARNING, c.getName() + ": " + e.getMessage());
                }
            }
            if (effects[x].poll(this) && effects[x].type == 22) {
                final Creature c = this.getCreature();
                if (c.getCurrentTile() != null) {
                    c.getCurrentTile().setNewRarityShader(c);
                }
            }
        }
    }
    
    public SpellEffect removeSpellEffect(final SpellEffect old) {
        if (old != null) {
            if (this.creature != null) {
                this.creature.removeSpellEffect(old);
            }
            old.delete();
            this.spellEffects.remove(old.type);
        }
        return old;
    }
    
    void destroy(final boolean keepHunted) {
        final SpellEffect[] effects = this.getEffects();
        SpellEffect hunted = null;
        for (int x = 0; x < effects.length; ++x) {
            if (effects[x].type != 64 || !keepHunted) {
                if (this.creature != null && this.creature.getCommunicator() != null) {
                    final SpellEffectsEnum spellEffect = SpellEffectsEnum.getEnumByName(effects[x].getName());
                    if (spellEffect != SpellEffectsEnum.NONE) {
                        this.creature.getCommunicator().sendRemoveSpellEffect(effects[x].id, spellEffect);
                    }
                }
                effects[x].delete();
            }
            else if (effects[x].type == 64) {
                hunted = effects[x];
            }
        }
        this.spellEffects.clear();
        if (hunted == null) {
            if (!keepHunted) {
                this.creature = null;
            }
        }
        else {
            this.spellEffects.put((byte)64, hunted);
        }
    }
    
    public void sleep() {
        this.spellEffects.clear();
        this.creature = null;
    }
    
    static {
        logger = Logger.getLogger(SpellEffects.class.getName());
        EMPTY_SPELLS = new SpellEffect[0];
    }
}
