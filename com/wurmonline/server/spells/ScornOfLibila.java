// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.spells;

import com.wurmonline.server.bodys.Wound;
import com.wurmonline.server.bodys.Wounds;
import com.wurmonline.server.zones.VolaTile;
import com.wurmonline.server.structures.Structure;
import com.wurmonline.server.zones.Zones;
import com.wurmonline.server.creatures.Creature;
import com.wurmonline.server.skills.Skill;

public class ScornOfLibila extends DamageSpell
{
    public static final int RANGE = 4;
    public static final double BASE_DAMAGE = 4000.0;
    public static final double DAMAGE_PER_POWER = 40.0;
    public static final int RADIUS = 3;
    
    public ScornOfLibila() {
        super("Scorn of Libila", 448, 15, 40, 50, 40, 120000L);
        this.targetTile = true;
        this.offensive = true;
        this.healing = true;
        this.description = "covers an area with draining energy, causing internal wounds on enemies and healing allies";
        this.type = 2;
    }
    
    @Override
    void doEffect(final Skill castSkill, final double power, final Creature performer, final int tilex, final int tiley, final int layer, final int heightOffset) {
        performer.getCommunicator().sendNormalServerMessage("You place the Mark of Libila where you stand, declaring a sanctuary.");
        final Structure currstr = performer.getCurrentTile().getStructure();
        final int radiusBonus = (int)(power / 40.0);
        final int sx = Zones.safeTileX(performer.getTileX() - 3 - radiusBonus - performer.getNumLinks());
        final int sy = Zones.safeTileY(performer.getTileY() - 3 - radiusBonus - performer.getNumLinks());
        final int ex = Zones.safeTileX(performer.getTileX() + 3 + radiusBonus + performer.getNumLinks());
        final int ey = Zones.safeTileY(performer.getTileY() + 3 + radiusBonus + performer.getNumLinks());
        this.calculateArea(sx, sy, ex, ey, tilex, tiley, layer, currstr);
        int damdealt = 3;
        final int maxRiftPart = 5;
        for (int x = sx; x <= ex; ++x) {
            for (int y = sy; y <= ey; ++y) {
                boolean isValidTargetTile = false;
                if (tilex == x && tiley == y) {
                    isValidTargetTile = true;
                }
                else {
                    final int currAreaX = x - sx;
                    final int currAreaY = y - sy;
                    if (!this.area[currAreaX][currAreaY]) {
                        isValidTargetTile = true;
                    }
                }
                if (isValidTargetTile) {
                    final VolaTile t = Zones.getTileOrNull(x, y, performer.isOnSurface());
                    if (t != null) {
                        final Creature[] creatures;
                        final Creature[] crets = creatures = t.getCreatures();
                        for (final Creature lCret : creatures) {
                            if (!lCret.isInvulnerable() && lCret.getAttitude(performer) == 2) {
                                t.sendAttachCreatureEffect(lCret, (byte)8, (byte)0, (byte)0, (byte)0, (byte)0);
                                damdealt += 3;
                                final double damage = this.calculateDamage(lCret, power, 4000.0, 40.0);
                                if (!lCret.addWoundOfType(performer, (byte)9, 1, false, 1.0f, false, damage, 0.0f, 0.0f, false, true)) {
                                    lCret.setTarget(performer.getWurmId(), false);
                                }
                            }
                        }
                    }
                }
            }
        }
        for (int x = sx; x <= ex && damdealt > 0; ++x) {
            for (int y = sy; y <= ey && damdealt > 0; ++y) {
                final VolaTile t2 = Zones.getTileOrNull(x, y, performer.isOnSurface());
                if (t2 != null) {
                    final Creature[] creatures2;
                    final Creature[] crets2 = creatures2 = t2.getCreatures();
                    for (final Creature lCret2 : creatures2) {
                        if ((lCret2.getAttitude(performer) == 1 || (lCret2.getAttitude(performer) == 0 && !lCret2.isAggHuman()) || lCret2.getKingdomId() == performer.getKingdomId()) && lCret2.getBody() != null) {
                            if (lCret2.getBody().getWounds() != null) {
                                final Wounds tWounds = lCret2.getBody().getWounds();
                                double healingPool = 58950.0;
                                healingPool += 58950.0 * (power / 100.0);
                                if (performer.getCultist() != null && performer.getCultist().healsFaster()) {
                                    healingPool *= 2.0;
                                }
                                final double resistance = SpellResist.getSpellResistance(lCret2, 249);
                                healingPool *= resistance;
                                int woundsHealed = 0;
                                final int maxWoundHeal = (int)(healingPool * 0.33);
                                for (final Wound w : tWounds.getWounds()) {
                                    if (woundsHealed >= 3) {
                                        break;
                                    }
                                    if (damdealt <= 0) {
                                        break;
                                    }
                                    if (w.getSeverity() >= maxWoundHeal) {
                                        healingPool -= maxWoundHeal;
                                        SpellResist.addSpellResistance(lCret2, 249, maxWoundHeal);
                                        w.modifySeverity(-maxWoundHeal);
                                        ++woundsHealed;
                                        --damdealt;
                                    }
                                }
                                while (woundsHealed < 3 && damdealt > 0 && tWounds.getWounds().length > 0) {
                                    Wound targetWound = tWounds.getWounds()[0];
                                    for (final Wound w2 : tWounds.getWounds()) {
                                        if (w2.getSeverity() > targetWound.getSeverity()) {
                                            targetWound = w2;
                                        }
                                    }
                                    SpellResist.addSpellResistance(lCret2, 249, targetWound.getSeverity());
                                    targetWound.heal();
                                    ++woundsHealed;
                                    --damdealt;
                                }
                                if (woundsHealed < 3 && damdealt > 0 && tWounds.getWounds().length > 0) {
                                    for (final Wound w : tWounds.getWounds()) {
                                        if (woundsHealed >= 3) {
                                            break;
                                        }
                                        if (damdealt <= 0) {
                                            break;
                                        }
                                        if (w.getSeverity() <= maxWoundHeal) {
                                            SpellResist.addSpellResistance(lCret2, 249, w.getSeverity());
                                            w.heal();
                                            ++woundsHealed;
                                            --damdealt;
                                        }
                                        else {
                                            SpellResist.addSpellResistance(lCret2, this.getNumber(), maxWoundHeal);
                                            w.modifySeverity(-maxWoundHeal);
                                            ++woundsHealed;
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
