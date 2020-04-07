// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.spells;

import com.wurmonline.server.structures.Floor;
import com.wurmonline.server.zones.VolaTile;
import java.util.Iterator;
import java.util.Set;
import java.util.logging.Level;
import com.wurmonline.server.creatures.ai.PathTile;
import com.wurmonline.server.zones.Zones;
import com.wurmonline.server.creatures.Creature;
import com.wurmonline.server.skills.Skill;
import java.util.logging.Logger;

public class Fireball extends KarmaSpell
{
    private static final Logger logger;
    public static final int RANGE = 24;
    
    public Fireball() {
        super("Fireball", 549, 15, 1000, 30, 1, 180000L);
        this.targetCreature = true;
        this.offensive = true;
        this.description = "sends an exploding ball of fire towards the target";
    }
    
    @Override
    boolean precondition(final Skill castSkill, final Creature performer, final Creature target) {
        if ((target.isHuman() || target.isDominated()) && target.getAttitude(performer) != 2 && !performer.getDeity().isHateGod() && performer.faithful && !performer.isDuelOrSpar(target)) {
            performer.getCommunicator().sendNormalServerMessage(performer.getDeity().getName() + " would never accept your attack on " + target.getNameWithGenus() + ".", (byte)3);
            return false;
        }
        return true;
    }
    
    @Override
    void doEffect(final Skill castSkill, final double power, final Creature performer, final Creature target) {
        if (!target.isUnique() || power > 99.0) {
            final int diameter = (int)Math.max(power / 30.0, 1.0);
            final int tfloorlevel = target.getFloorLevel();
            final Set<PathTile> tiles = Zones.explode(target.getTileX(), target.getTileY(), tfloorlevel, true, diameter);
            boolean insideStructure = false;
            target.getCommunicator().sendNormalServerMessage(performer.getNameWithGenus() + " burns you.", (byte)4);
            if (target.getCurrentTile() != null) {
                target.getCurrentTile().sendAttachCreatureEffect(target, (byte)4, (byte)0, (byte)0, (byte)0, (byte)0);
                insideStructure = true;
            }
            for (int maxLevel = (tfloorlevel >= 0) ? 2 : tfloorlevel, fLevel = target.getFloorLevel(); fLevel <= maxLevel; ++fLevel) {
                if (fLevel <= 0 || insideStructure) {
                    for (final PathTile pathtile : tiles) {
                        final VolaTile targetVolaTile = Zones.getOrCreateTile(pathtile.getTileX(), pathtile.getTileY(), pathtile.getFloorLevel() >= 0);
                        if (targetVolaTile != null) {
                            final Creature[] creatures;
                            final Creature[] crets = creatures = targetVolaTile.getCreatures();
                            for (final Creature creature : creatures) {
                                if (creature.getWurmId() != performer.getWurmId() && creature.getKingdomId() != performer.getKingdomId()) {
                                    if (creature.getPower() < 2) {
                                        if (creature.getFloorLevel() == fLevel) {
                                            try {
                                                final double damage = 9000.0 + 6000.0 * (power / 100.0);
                                                final byte pos = creature.getBody().getRandomWoundPos();
                                                creature.addWoundOfType(performer, (byte)4, pos, false, 1.0f, true, damage, 0.0f, 0.0f, false, true);
                                            }
                                            catch (Exception ex) {
                                                Fireball.logger.log(Level.INFO, ex.getMessage(), ex);
                                            }
                                        }
                                    }
                                }
                            }
                            if (targetVolaTile.getStructure() != null) {
                                insideStructure = true;
                                final Floor f = targetVolaTile.getTopFloor();
                                if (f != null && f.getFloorLevel() <= fLevel) {
                                    continue;
                                }
                                targetVolaTile.sendAddQuickTileEffect((byte)65, fLevel);
                            }
                            else {
                                targetVolaTile.sendAddQuickTileEffect((byte)65, fLevel);
                            }
                        }
                    }
                }
            }
        }
        else {
            performer.getCommunicator().sendNormalServerMessage("You try to fireball " + target.getNameWithGenus() + " but fail.");
            target.getCommunicator().sendNormalServerMessage(performer.getNameWithGenus() + " tries to fireball you but fails.");
        }
    }
    
    @Override
    void doNegativeEffect(final Skill castSkill, final double power, final Creature performer, final Creature target) {
        performer.getCommunicator().sendNormalServerMessage("You try to set " + target.getNameWithGenus() + " on fire but fail.");
        target.getCommunicator().sendNormalServerMessage(performer.getNameWithGenus() + " tries to set you on fire but fails.");
    }
    
    static {
        logger = Logger.getLogger(Fireball.class.getName());
    }
}
