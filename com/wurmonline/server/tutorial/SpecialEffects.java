// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.tutorial;

import com.wurmonline.server.items.Item;
import com.wurmonline.server.zones.VolaTile;
import com.wurmonline.server.Items;
import com.wurmonline.server.combat.Battle;
import com.wurmonline.server.combat.CombatEngine;
import com.wurmonline.server.zones.Zones;
import com.wurmonline.server.creatures.Creature;
import com.wurmonline.server.MiscConstants;
import com.wurmonline.shared.constants.CounterTypes;

public final class SpecialEffects implements CounterTypes, MiscConstants
{
    private final String name;
    private final int id;
    private byte requiredPower;
    private static final int numEffects = 6;
    private static final SpecialEffects[] effects;
    public static final int NO_EFFECT = 0;
    public static final int OPEN_DOOR = 1;
    public static final int HEAL = 2;
    public static final int WOUND = 3;
    public static final int DELETE_TILE_ITEMS = 4;
    public static final int SEND_PLONK = 5;
    
    private SpecialEffects(final int _id, final String _name) {
        this.requiredPower = 0;
        this.id = _id;
        this.name = _name;
    }
    
    public void setPowerRequired(final byte power) {
        this.requiredPower = power;
    }
    
    public byte getPowerRequired() {
        return this.requiredPower;
    }
    
    public String getName() {
        return this.name;
    }
    
    public int getId() {
        return this.id;
    }
    
    public static final SpecialEffects[] getEffects() {
        return SpecialEffects.effects;
    }
    
    public static final SpecialEffects getEffect(final int number) {
        try {
            return SpecialEffects.effects[number];
        }
        catch (Exception ex) {
            return null;
        }
    }
    
    public boolean run(final Creature performer, final int tilex, final int tiley, final int layer) {
        final boolean toReturn = false;
        switch (this.id) {
            case 0: {}
            case 2: {
                final VolaTile t = Zones.getTileOrNull(tilex, tiley, layer >= 0);
                if (t != null) {
                    final Creature[] creatures3;
                    final Creature[] creatures = creatures3 = t.getCreatures();
                    for (final Creature c : creatures3) {
                        c.getBody().healFully();
                    }
                    break;
                }
                break;
            }
            case 3: {
                final VolaTile t2 = Zones.getTileOrNull(tilex, tiley, layer >= 0);
                if (t2 != null) {
                    final Creature[] creatures4;
                    final Creature[] creatures2 = creatures4 = t2.getCreatures();
                    for (final Creature c2 : creatures4) {
                        CombatEngine.addWound(c2, c2, (byte)3, 13, 1000.0, 1.0f, "bite", null, 0.0f, 0.0f, false, false, false, false);
                    }
                    break;
                }
                break;
            }
            case 4: {
                final VolaTile t3 = Zones.getTileOrNull(tilex, tiley, layer >= 0);
                if (t3 != null) {
                    final Item[] items2;
                    final Item[] items = items2 = t3.getItems();
                    for (final Item i : items2) {
                        if (!i.isIndestructible()) {
                            Items.destroyItem(i.getWurmId());
                        }
                    }
                    break;
                }
                break;
            }
        }
        return false;
    }
    
    public boolean run(final Creature performer, final long target) {
        final boolean toReturn = false;
        switch (this.id) {
            case 0: {}
            case 2: {
                performer.getBody().healFully();
                break;
            }
            case 3: {
                CombatEngine.addWound(performer, performer, (byte)3, 13, 1000.0, 1.0f, "bite", null, 0.0f, 0.0f, false, false, false, false);
                break;
            }
        }
        return false;
    }
    
    public boolean run(final Creature performer, final long target, final int numbers) {
        final boolean toReturn = false;
        switch (this.id) {
            case 0: {
                break;
            }
            case 5: {
                performer.getCommunicator().sendPlonk((short)numbers);
                break;
            }
            default: {
                return this.run(performer, target);
            }
        }
        return false;
    }
    
    static {
        (effects = new SpecialEffects[6])[0] = new SpecialEffects(0, "Do nothing");
        SpecialEffects.effects[0].requiredPower = 0;
        SpecialEffects.effects[1] = new SpecialEffects(1, "Open door or gate");
        SpecialEffects.effects[1].requiredPower = 2;
        SpecialEffects.effects[2] = new SpecialEffects(2, "Heal all wounds");
        SpecialEffects.effects[2].requiredPower = 2;
        SpecialEffects.effects[3] = new SpecialEffects(3, "Create a wound");
        SpecialEffects.effects[3].requiredPower = 2;
        SpecialEffects.effects[4] = new SpecialEffects(4, "Delete items on tile");
        SpecialEffects.effects[4].requiredPower = 2;
        SpecialEffects.effects[5] = new SpecialEffects(5, "Send a notification");
        SpecialEffects.effects[5].requiredPower = 2;
    }
}
