// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.spells;

import com.wurmonline.server.zones.VirtualZone;
import com.wurmonline.server.zones.VolaTile;
import java.util.logging.Level;
import com.wurmonline.server.zones.Zones;
import com.wurmonline.server.Server;
import com.wurmonline.server.creatures.CreatureMove;
import com.wurmonline.server.players.MovementEntity;
import com.wurmonline.server.items.Item;
import com.wurmonline.server.creatures.Creature;
import com.wurmonline.server.skills.Skill;

public class MirroredSelf extends KarmaSpell
{
    public MirroredSelf() {
        super("Mirrored Self", 562, 5, 500, 20, 1, 900000L);
        this.targetTile = true;
        this.targetItem = true;
        this.description = "creates deceptive illusions of yourself around you";
    }
    
    @Override
    void doEffect(final Skill castSkill, final double power, final Creature performer, final int tilex, final int tiley, final int layer, final int heightOffset) {
        this.castMirroredSelf(performer, Math.max(10.0, power));
    }
    
    @Override
    void doEffect(final Skill castSkill, final double power, final Creature performer, final Item target) {
        this.castMirroredSelf(performer, Math.max(10.0, power));
    }
    
    private void castMirroredSelf(final Creature performer, final double power) {
        final int nums = 2 + (int)power / 10;
        final int x = 0;
        final int y = 0;
        for (int n = 0; n < nums; ++n) {
            final MovementEntity entity = new MovementEntity(performer.getWurmId(), System.currentTimeMillis() + 1000L * Math.max(20L, (long)power));
            final CreatureMove startPos = new CreatureMove();
            startPos.diffX = (byte)(-1 + Server.rand.nextInt(2));
            startPos.diffY = (byte)(-1 + Server.rand.nextInt(2));
            startPos.diffZ = 0.0f;
            entity.setMovePosition(startPos);
            performer.addIllusion(entity);
            final VolaTile tile = Zones.getOrCreateTile(performer.getTileX() + 0, performer.getTileY() + 0, performer.isOnSurface());
            for (final VirtualZone vz : tile.getWatchers()) {
                try {
                    final float posZ = Zones.calculatePosZ((performer.getTileX() + 0) * 4, (performer.getTileY() + 0) * 4, tile, performer.isOnSurface(), false, performer.getPositionZ(), performer, -10L);
                    final float diffZ = performer.getPositionZ() - posZ;
                    try {
                        vz.addCreature(performer.getWurmId(), false, entity.getWurmid(), 0.0f, 0.0f, diffZ);
                    }
                    catch (Exception ex) {}
                }
                catch (Exception e) {
                    MirroredSelf.logger.log(Level.WARNING, e.getMessage(), e);
                }
            }
        }
    }
}
