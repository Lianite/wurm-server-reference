// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.spells;

import com.wurmonline.server.bodys.Wound;
import com.wurmonline.server.bodys.Wounds;
import com.wurmonline.server.villages.Village;
import com.wurmonline.server.zones.VolaTile;
import java.util.List;
import com.wurmonline.server.utils.CreatureLineSegment;
import com.wurmonline.shared.util.MulticolorLineSegment;
import java.util.ArrayList;
import com.wurmonline.server.zones.Zones;
import com.wurmonline.server.creatures.Creature;
import com.wurmonline.server.skills.Skill;

public class LightOfFo extends ReligiousSpell
{
    public static final int RANGE = 4;
    
    public LightOfFo() {
        super("Light of Fo", 438, 15, 60, 40, 33, 120000L);
        this.targetTile = true;
        this.healing = true;
        this.description = "covers an area with healing energy, healing multiple wounds from allies";
        this.type = 2;
    }
    
    @Override
    void doEffect(final Skill castSkill, final double power, final Creature performer, final int tilex, final int tiley, final int layer, final int heightOffset) {
        performer.getCommunicator().sendNormalServerMessage("You place the Mark of Fo in the area, declaring a sanctuary.");
        final int sx = Zones.safeTileX(tilex - (int)Math.max(1.0, power / 10.0 + performer.getNumLinks()));
        final int sy = Zones.safeTileY(tiley - (int)Math.max(1.0, power / 10.0 + performer.getNumLinks()));
        final int ex = Zones.safeTileX(tilex + (int)Math.max(1.0, power / 10.0 + performer.getNumLinks()));
        final int ey = Zones.safeTileY(tiley + (int)Math.max(1.0, power / 10.0 + performer.getNumLinks()));
        int totalHealed = 0;
        for (int x = sx; x <= ex; ++x) {
            for (int y = sy; y <= ey; ++y) {
                final VolaTile t = Zones.getTileOrNull(x, y, performer.isOnSurface());
                if (t != null) {
                    for (final Creature lCret : t.getCreatures()) {
                        boolean doHeal = false;
                        if (lCret.getKingdomId() == performer.getKingdomId() || lCret.getAttitude(performer) == 1) {
                            doHeal = true;
                        }
                        final Village lVill = lCret.getCitizenVillage();
                        if (lVill != null && lVill.isEnemy(performer)) {
                            doHeal = false;
                        }
                        final Village pVill = performer.getCitizenVillage();
                        if (pVill != null && pVill.isEnemy(lCret)) {
                            doHeal = false;
                        }
                        if (doHeal) {
                            if (lCret.getBody() != null) {
                                if (lCret.getBody().getWounds() != null) {
                                    final Wounds tWounds = lCret.getBody().getWounds();
                                    double healingPool = 16375.0;
                                    healingPool += 98250.0 * (power / 100.0);
                                    if (performer.getCultist() != null && performer.getCultist().healsFaster()) {
                                        healingPool *= 2.0;
                                    }
                                    final double resistance = SpellResist.getSpellResistance(lCret, this.getNumber());
                                    healingPool *= resistance;
                                    int woundsHealed = 0;
                                    final int maxWoundHeal = (int)(healingPool * 0.2);
                                    for (final Wound w : tWounds.getWounds()) {
                                        if (woundsHealed >= 5) {
                                            break;
                                        }
                                        if (w.getSeverity() >= maxWoundHeal) {
                                            healingPool -= maxWoundHeal;
                                            SpellResist.addSpellResistance(lCret, this.getNumber(), maxWoundHeal);
                                            w.modifySeverity(-maxWoundHeal);
                                            ++woundsHealed;
                                        }
                                    }
                                    while (woundsHealed < 5 && tWounds.getWounds().length > 0) {
                                        Wound targetWound = tWounds.getWounds()[0];
                                        for (final Wound w2 : tWounds.getWounds()) {
                                            if (w2.getSeverity() > targetWound.getSeverity()) {
                                                targetWound = w2;
                                            }
                                        }
                                        SpellResist.addSpellResistance(lCret, 249, targetWound.getSeverity());
                                        targetWound.heal();
                                        ++woundsHealed;
                                    }
                                    if (woundsHealed < 5) {
                                        for (final Wound w : tWounds.getWounds()) {
                                            if (woundsHealed >= 5) {
                                                break;
                                            }
                                            if (w.getSeverity() <= maxWoundHeal) {
                                                SpellResist.addSpellResistance(lCret, this.getNumber(), w.getSeverity());
                                                w.heal();
                                                ++woundsHealed;
                                            }
                                            else {
                                                SpellResist.addSpellResistance(lCret, this.getNumber(), maxWoundHeal);
                                                w.modifySeverity(-maxWoundHeal);
                                                ++woundsHealed;
                                            }
                                        }
                                    }
                                    final VolaTile tt = Zones.getTileOrNull(lCret.getTileX(), lCret.getTileY(), lCret.isOnSurface());
                                    if (tt != null) {
                                        tt.sendAttachCreatureEffect(lCret, (byte)11, (byte)0, (byte)0, (byte)0, (byte)0);
                                    }
                                    ++totalHealed;
                                    final String heal = (performer == lCret) ? "heal" : "heals";
                                    final ArrayList<MulticolorLineSegment> segments = new ArrayList<MulticolorLineSegment>();
                                    segments.add(new CreatureLineSegment(performer));
                                    segments.add(new MulticolorLineSegment(" " + heal + " some of your wounds with " + this.getName() + ".", (byte)0));
                                    lCret.getCommunicator().sendColoredMessageCombat(segments);
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
