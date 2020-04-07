// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.spells;

import com.wurmonline.server.zones.VolaTile;
import com.wurmonline.server.structures.Structure;
import com.wurmonline.server.zones.AreaSpellEffect;
import com.wurmonline.server.zones.Zones;
import com.wurmonline.mesh.Tiles;
import com.wurmonline.server.Server;
import com.wurmonline.server.creatures.Creature;
import com.wurmonline.server.skills.Skill;

public class FungusTrap extends ReligiousSpell
{
    public static final int RANGE = 24;
    public static final double BASE_DAMAGE = 350.0;
    public static final double DAMAGE_PER_SECOND = 2.0;
    public static final int RADIUS = 2;
    
    public FungusTrap() {
        super("Fungus Trap", 433, 10, 23, 20, 33, 120000L);
        this.targetTile = true;
        this.offensive = true;
        this.description = "covers an area with fungus that deals damage to enemies over time";
        this.type = 1;
    }
    
    @Override
    boolean precondition(final Skill castSkill, final Creature performer, final int tilex, final int tiley, final int layer) {
        if (layer < 0) {
            final int tile = Server.caveMesh.getTile(tilex, tiley);
            if (Tiles.isSolidCave(Tiles.decodeType(tile))) {
                performer.getCommunicator().sendNormalServerMessage("The spell doesn't work there.", (byte)3);
                return false;
            }
        }
        return true;
    }
    
    @Override
    void doEffect(final Skill castSkill, final double power, final Creature performer, final int tilex, final int tiley, final int layer, final int heightOffset) {
        int tile = Server.surfaceMesh.getTile(tilex, tiley);
        if (layer < 0) {
            tile = Server.caveMesh.getTile(tilex, tiley);
        }
        final byte type = Tiles.decodeType(tile);
        if (Tiles.isSolidCave(type)) {
            performer.getCommunicator().sendNormalServerMessage("You fail to find a spot to direct the power to.", (byte)3);
            return;
        }
        performer.getCommunicator().sendNormalServerMessage("You call upon the mycelium!", (byte)2);
        final Structure currstr = performer.getCurrentTile().getStructure();
        final int sx = Zones.safeTileX(tilex - 2 - performer.getNumLinks());
        final int ex = Zones.safeTileX(tilex + 2 + performer.getNumLinks());
        final int sy = Zones.safeTileY(tiley - 2 - performer.getNumLinks());
        final int ey = Zones.safeTileY(tiley + 2 + performer.getNumLinks());
        final VolaTile tileTarget = Zones.getOrCreateTile(tilex, tiley, layer >= 0);
        Structure targetStructure = null;
        if (heightOffset != -1) {
            targetStructure = tileTarget.getStructure();
        }
        this.calculateAOE(sx, sy, ex, ey, tilex, tiley, layer, currstr, targetStructure, heightOffset);
        for (int x = sx; x <= ex; ++x) {
            for (int y = sy; y <= ey; ++y) {
                final int currAreaX = x - sx;
                final int currAreaY = y - sy;
                if (!this.area[currAreaX][currAreaY]) {
                    new AreaSpellEffect(performer.getWurmId(), x, y, layer, (byte)37, System.currentTimeMillis() + 1000L * (30 + (int)power / 10), (float)power * 1.5f, layer, this.offsets[currAreaX][currAreaY], true);
                }
            }
        }
    }
}
