// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.spells;

import com.wurmonline.server.structures.Structure;
import com.wurmonline.server.zones.AreaSpellEffect;
import com.wurmonline.server.zones.Zones;
import com.wurmonline.mesh.Tiles;
import com.wurmonline.server.Server;
import com.wurmonline.server.creatures.Creature;
import com.wurmonline.server.skills.Skill;

public class DeepTentacles extends ReligiousSpell
{
    public static final int RANGE = 24;
    public static final double BASE_DAMAGE = 400.0;
    public static final double DAMAGE_PER_SECOND = 1.0;
    public static final int RADIUS = 1;
    
    public DeepTentacles() {
        super("Tentacles", 418, 10, 30, 20, 33, 120000L);
        this.targetTile = true;
        this.offensive = true;
        this.description = "covers an area with bludgeoning tentacles that damage enemies over time";
        this.type = 2;
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
            final byte type = Tiles.decodeType(tile);
            if (Tiles.isSolidCave(type)) {
                performer.getCommunicator().sendNormalServerMessage("You fail to find a spot to direct the power to.", (byte)3);
                return;
            }
        }
        final Structure currstr = performer.getCurrentTile().getStructure();
        performer.getCommunicator().sendNormalServerMessage("Waving tentacles appear around the " + Tiles.getTile(Tiles.decodeType(tile)).tiledesc.toLowerCase() + ".");
        final int sx = Zones.safeTileX(tilex - 1 - performer.getNumLinks());
        final int ex = Zones.safeTileX(tilex + 1 + performer.getNumLinks());
        final int sy = Zones.safeTileY(tiley - 1 - performer.getNumLinks());
        final int ey = Zones.safeTileY(tiley + 1 + performer.getNumLinks());
        this.calculateArea(sx, sy, ex, ey, tilex, tiley, layer, currstr);
        for (int x = sx; x <= ex; ++x) {
            for (int y = sy; y <= ey; ++y) {
                final int currAreaX = x - sx;
                final int currAreaY = y - sy;
                if (!this.area[currAreaX][currAreaY]) {
                    new AreaSpellEffect(performer.getWurmId(), x, y, layer, (byte)34, System.currentTimeMillis() + 1000L * (30 + (int)power / 10), (float)power * 1.5f, layer, 0, true);
                }
            }
        }
    }
}
