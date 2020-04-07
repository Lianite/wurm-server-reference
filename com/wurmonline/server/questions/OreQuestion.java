// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.questions;

import com.wurmonline.server.Items;
import com.wurmonline.server.zones.Zones;
import com.wurmonline.server.Players;
import com.wurmonline.mesh.Tiles;
import com.wurmonline.server.Server;
import java.util.logging.Level;
import java.util.Properties;
import java.util.HashMap;
import com.wurmonline.server.creatures.Creature;
import java.util.Map;
import com.wurmonline.server.items.Item;

public class OreQuestion extends Question
{
    private final int tilex;
    private final int tiley;
    private int numtiles;
    private static final String NOCHANGE = "No change";
    private final Item rod;
    private final Map<Integer, Integer> oretiles;
    
    public OreQuestion(final Creature _responder, final int _tilex, final int _tiley, final Item _rod) {
        super(_responder, "Selecting Ore Type", "Which ore type should this tile have?", 82, _rod.getWurmId());
        this.oretiles = new HashMap<Integer, Integer>();
        this.tilex = _tilex;
        this.tiley = _tiley;
        this.rod = _rod;
    }
    
    @Override
    public void answer(final Properties answers) {
        if (this.type == 82) {
            if (this.rod.deleted) {
                this.getResponder().getCommunicator().sendNormalServerMessage("The rod has been destroyed.");
                return;
            }
            if (this.rod.getOwnerId() != this.getResponder().getWurmId()) {
                this.getResponder().getCommunicator().sendNormalServerMessage("You need to be in possession of the rod.");
                return;
            }
            if (this.rod.getWurmId() == this.target) {
                final String d1 = answers.getProperty("data1");
                if (d1 != null) {
                    int index = 0;
                    try {
                        index = Integer.parseInt(d1);
                    }
                    catch (Exception ex) {
                        this.getResponder().getCommunicator().sendNormalServerMessage(d1 + " was selected - Error. No change.");
                        return;
                    }
                    if (index == this.numtiles) {
                        this.getResponder().getCommunicator().sendNormalServerMessage("You decide to change nothing.");
                        return;
                    }
                    if (this.getResponder().getLogger() != null) {
                        this.getResponder().getLogger().log(Level.INFO, this.getResponder() + " setting ore " + this.tilex + ", " + this.tiley + " to : " + d1);
                    }
                    final Integer newType = this.oretiles.get(index);
                    if (newType == null) {
                        this.getResponder().getCommunicator().sendNormalServerMessage("Invalid choice " + index + ".");
                        return;
                    }
                    final byte nt = (byte)(int)newType;
                    if (this.getResponder().isOnSurface()) {
                        this.getResponder().getCommunicator().sendNormalServerMessage("Please enter the cave.");
                        return;
                    }
                    if (Tiles.decodeType(Server.caveMesh.getTile(this.tilex, this.tiley)) == nt) {
                        this.getResponder().getCommunicator().sendNormalServerMessage("The terrain is already of that type.");
                        return;
                    }
                    if (!Tiles.isOreCave(nt)) {
                        this.getResponder().getCommunicator().sendNormalServerMessage("The rod must set to ore of some kind.");
                        return;
                    }
                    if (Tiles.decodeType(Server.caveMesh.getTile(this.tilex, this.tiley)) == Tiles.Tile.TILE_CAVE_WALL.id) {
                        Server.caveMesh.setTile(this.tilex, this.tiley, Tiles.encode(Tiles.decodeHeight(Server.caveMesh.getTile(this.tilex, this.tiley)), nt, Tiles.decodeData(Server.caveMesh.getTile(this.tilex, this.tiley))));
                        Players.getInstance().sendChangedTile(this.tilex, this.tiley, false, true);
                        this.getResponder().getCommunicator().sendNormalServerMessage("The wall changes to purest " + Tiles.getTile(nt).tiledesc + " and the rod vanishes!");
                        Server.setCaveResource(this.tilex, this.tiley, 10000);
                        Zones.setMiningState(this.tilex, this.tiley, (byte)(-1), false);
                        Items.destroyItem(this.rod.getWurmId());
                    }
                    else {
                        this.getResponder().getCommunicator().sendNormalServerMessage("You need to use this on a cave wall.");
                    }
                }
            }
        }
    }
    
    @Override
    public void sendQuestion() {
        final StringBuilder buf = new StringBuilder(this.getBmlHeader());
        buf.append("harray{label{text='Tile type'}dropdown{id='data1';options=\"");
        final Tiles.Tile[] tiles = Tiles.Tile.getTiles();
        for (int x = 0; x < tiles.length; ++x) {
            if (tiles[x] != null && Tiles.isOreCave(tiles[x].id) && tiles[x].id != Tiles.Tile.TILE_CAVE_WALL_ORE_ADAMANTINE.id && tiles[x].id != Tiles.Tile.TILE_CAVE_WALL_ORE_GLIMMERSTEEL.id) {
                this.oretiles.put(this.numtiles, x);
                ++this.numtiles;
                buf.append(tiles[x].tiledesc);
                buf.append(",");
            }
        }
        buf.append("No change");
        buf.append("\"}}");
        buf.append(this.createAnswerButton2());
        this.getResponder().getCommunicator().sendBml(300, 300, true, true, buf.toString(), 200, 200, 200, this.title);
    }
}
