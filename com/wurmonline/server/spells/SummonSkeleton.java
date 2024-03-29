// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.spells;

import com.wurmonline.server.creatures.CreatureTemplate;
import com.wurmonline.server.creatures.NoSuchCreatureTemplateException;
import java.util.logging.Level;
import com.wurmonline.server.creatures.CreatureTemplateFactory;
import com.wurmonline.server.zones.Zones;
import com.wurmonline.mesh.Tiles;
import com.wurmonline.server.Server;
import com.wurmonline.server.creatures.Creature;
import com.wurmonline.server.skills.Skill;
import java.util.logging.Logger;
import com.wurmonline.server.creatures.CreatureTemplateIds;

public class SummonSkeleton extends KarmaSpell implements CreatureTemplateIds
{
    private static final Logger logger;
    public static final int RANGE = 24;
    
    public SummonSkeleton() {
        super("Summon Skeleton", 631, 30, 500, 30, 1, 180000L);
    }
    
    @Override
    boolean precondition(final Skill castSkill, final Creature performer, final int tilex, final int tiley, final int layer) {
        if (performer.getFollowers().length > 0) {
            performer.getCommunicator().sendNormalServerMessage("You are too busy leading other creatures and can not focus on summoning.", (byte)3);
            return false;
        }
        if (layer < 0 && Tiles.isSolidCave(Tiles.decodeType(Server.caveMesh.getTile(tilex, tiley)))) {
            performer.getCommunicator().sendNormalServerMessage("You can not summon there.", (byte)3);
            return false;
        }
        try {
            if (Zones.calculateHeight((tilex << 2) + 2, (tiley << 2) + 2, performer.isOnSurface()) < 0.0f) {
                performer.getCommunicator().sendNormalServerMessage("You can not summon there.", (byte)3);
                return false;
            }
        }
        catch (Exception ex) {
            performer.getCommunicator().sendNormalServerMessage("You can not summon there.", (byte)3);
            return false;
        }
        return true;
    }
    
    @Override
    void doEffect(final Skill castSkill, final double power, final Creature performer, final int tilex, final int tiley, final int layer, final int heightOffset) {
        if (performer.knowsKarmaSpell(631)) {
            for (int nums = 0; nums < Math.max(2.0, power / 10.0); ++nums) {
                this.spawnCreature(87, performer);
            }
        }
    }
    
    private final void spawnCreature(final int templateId, final Creature performer) {
        try {
            final CreatureTemplate ct = CreatureTemplateFactory.getInstance().getTemplate(templateId);
            byte sex = 0;
            if (Server.rand.nextInt(2) == 0) {
                sex = 1;
            }
            byte ctype = (byte)Math.max(0, Server.rand.nextInt(22) - 10);
            if (Server.rand.nextInt(20) == 0) {
                ctype = 99;
            }
            final Creature c = Creature.doNew(templateId, true, performer.getPosX() - 4.0f + Server.rand.nextFloat() * 9.0f, performer.getPosY() - 4.0f + Server.rand.nextFloat() * 9.0f, Server.rand.nextFloat() * 360.0f, performer.getLayer(), ct.getName(), sex, performer.getKingdomId(), ctype, true);
            c.setLoyalty(100.0f);
            c.setDominator(performer.getWurmId());
            performer.setPet(c.getWurmId());
            c.setLeader(performer);
        }
        catch (NoSuchCreatureTemplateException nst) {
            SummonSkeleton.logger.log(Level.WARNING, nst.getMessage(), nst);
        }
        catch (Exception ex) {
            SummonSkeleton.logger.log(Level.WARNING, ex.getMessage(), ex);
        }
    }
    
    static {
        logger = Logger.getLogger(SummonSkeleton.class.getName());
    }
}
