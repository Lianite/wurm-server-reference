// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.spells;

import com.wurmonline.server.zones.Zone;
import com.wurmonline.server.zones.NoSuchZoneException;
import com.wurmonline.server.Server;
import com.wurmonline.server.structures.DbFence;
import com.wurmonline.shared.constants.StructureConstantsEnum;
import com.wurmonline.server.sounds.SoundPlayer;
import com.wurmonline.server.structures.Fence;
import com.wurmonline.server.structures.Wall;
import com.wurmonline.server.zones.VolaTile;
import com.wurmonline.server.zones.Zones;
import com.wurmonline.mesh.Tiles;
import com.wurmonline.server.creatures.Creature;
import com.wurmonline.server.skills.Skill;

public class WallOfIce extends KarmaSpell
{
    public static final int RANGE = 24;
    
    public WallOfIce() {
        super("Wall of Ice", 556, 10, 400, 10, 1, 0L);
        this.targetTileBorder = true;
        this.offensive = true;
        this.description = "creates a magical wall of ice on a tile border";
    }
    
    @Override
    boolean precondition(final Skill castSkill, final Creature performer, final int tileBorderx, final int tileBordery, final int layer, final int heightOffset, final Tiles.TileBorderDirection dir) {
        final VolaTile t = Zones.getTileOrNull(tileBorderx, tileBordery, layer == 0);
        if (t != null) {
            final Wall[] wallsForLevel;
            final Wall[] walls = wallsForLevel = t.getWallsForLevel(heightOffset / 30);
            for (final Wall wall : wallsForLevel) {
                if (wall.isHorizontal() == (dir == Tiles.TileBorderDirection.DIR_HORIZ) && wall.getStartX() == tileBorderx && wall.getStartY() == tileBordery) {
                    return false;
                }
            }
            final Fence[] fencesForDir;
            final Fence[] fences = fencesForDir = t.getFencesForDir(dir);
            for (final Fence f : fencesForDir) {
                if (f.getHeightOffset() == heightOffset) {
                    return false;
                }
            }
        }
        if (dir == Tiles.TileBorderDirection.DIR_DOWN) {
            final VolaTile t2 = Zones.getTileOrNull(tileBorderx, tileBordery, layer == 0);
            if (t2 != null) {
                for (final Creature c : t2.getCreatures()) {
                    if (c.isPlayer()) {
                        return false;
                    }
                }
            }
            final VolaTile t3 = Zones.getTileOrNull(tileBorderx - 1, tileBordery, layer == 0);
            if (t3 != null) {
                for (final Creature c2 : t3.getCreatures()) {
                    if (c2.isPlayer()) {
                        return false;
                    }
                }
            }
        }
        else {
            final VolaTile t2 = Zones.getTileOrNull(tileBorderx, tileBordery, layer == 0);
            if (t2 != null) {
                for (final Creature c : t2.getCreatures()) {
                    if (c.isPlayer()) {
                        return false;
                    }
                }
            }
            final VolaTile t3 = Zones.getTileOrNull(tileBorderx, tileBordery - 1, layer == 0);
            if (t3 != null) {
                for (final Creature c2 : t3.getCreatures()) {
                    if (c2.isPlayer()) {
                        return false;
                    }
                }
            }
        }
        return true;
    }
    
    @Override
    void doEffect(final Skill castSkill, final double power, final Creature performer, final int tilex, final int tiley, final int layer, final int heightOffset, final Tiles.TileBorderDirection dir) {
        SoundPlayer.playSound("sound.religion.channel", tilex, tiley, performer.isOnSurface(), 0.0f);
        try {
            final Zone zone = Zones.getZone(tilex, tiley, true);
            final Fence fence = new DbFence(StructureConstantsEnum.FENCE_MAGIC_ICE, tilex, tiley, heightOffset, (float)(1.0 + power / 5.0), dir, zone.getId(), layer);
            fence.setState(fence.getFinishState());
            fence.setQualityLevel((float)power);
            fence.improveOrigQualityLevel((float)power);
            zone.addFence(fence);
            performer.achievement(320);
            performer.getCommunicator().sendNormalServerMessage("You weave the source and create a wall.");
            Server.getInstance().broadCastAction(performer.getName() + " creates a wall.", performer, 5);
        }
        catch (NoSuchZoneException ex) {}
    }
}
