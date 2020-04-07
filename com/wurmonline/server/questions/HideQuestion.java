// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.questions;

import com.wurmonline.server.zones.Zones;
import com.wurmonline.server.items.Item;
import com.wurmonline.server.NoSuchItemException;
import com.wurmonline.mesh.Tiles;
import com.wurmonline.server.Server;
import com.wurmonline.server.Items;
import java.util.Properties;
import com.wurmonline.server.creatures.Creature;

public final class HideQuestion extends Question
{
    public HideQuestion(final Creature aResponder, final String aTitle, final String aQuestion, final long aTarget) {
        super(aResponder, aTitle, aQuestion, 70, aTarget);
    }
    
    @Override
    public void answer(final Properties answers) {
        if (this.getResponder().getPower() >= 2) {
            boolean putOnSurface = false;
            final String key2 = "putonsurf";
            final String val2 = answers.getProperty("putonsurf");
            if (val2 != null && val2.equals("true")) {
                putOnSurface = true;
            }
            final String key3 = "height";
            final String val3 = answers.getProperty("height");
            if (val3 == null || val3.length() <= 0) {
                if (!putOnSurface) {
                    return;
                }
            }
            try {
                final int x = (val3 == null) ? 0 : Integer.parseInt(val3);
                try {
                    final Item i = Items.getItem(this.target);
                    final short rock = Tiles.decodeHeight(Server.rockMesh.getTile(this.getResponder().getCurrentTile().tilex, this.getResponder().getCurrentTile().tiley));
                    final short height = Tiles.decodeHeight(Server.surfaceMesh.getTile(this.getResponder().getCurrentTile().tilex, this.getResponder().getCurrentTile().tiley));
                    final int diff = height - rock;
                    if (i.getOwnerId() == -10L || i.getOwnerId() == this.getResponder().getWurmId()) {
                        if (x < diff || putOnSurface) {
                            Items.hideItem(this.getResponder(), i, (rock + x) / 10.0f, putOnSurface);
                            if (putOnSurface) {
                                this.getResponder().getCommunicator().sendNormalServerMessage("You carefully hide the " + i.getName() + " here.");
                            }
                            else {
                                this.getResponder().getCommunicator().sendNormalServerMessage("You carefully hide the " + i.getName() + " at " + (rock + x) / 10.0f + " meters.");
                            }
                        }
                        else {
                            this.getResponder().getCommunicator().sendNormalServerMessage("You can not hide the " + i.getName() + " at " + (rock + x) + ". Rock is at " + rock + ", and surface is at " + height + ".");
                        }
                    }
                }
                catch (NoSuchItemException nsi) {
                    this.getResponder().getCommunicator().sendNormalServerMessage("The item can no longer be found!");
                }
            }
            catch (NumberFormatException nf) {
                this.getResponder().getCommunicator().sendNormalServerMessage("Failed to parse " + val3 + " as an integer number.");
            }
        }
    }
    
    @Override
    public void sendQuestion() {
        final StringBuilder buf = new StringBuilder(this.getBmlHeader());
        try {
            final Item it = Items.getItem(this.target);
            buf.append("text{type='';text='Hiding " + it.getName() + ".'}");
            if (!this.getResponder().isOnSurface()) {
                buf.append("text{type='';text='You can only hide items on the surface now.'}");
            }
            else {
                final short rock = Tiles.decodeHeight(Server.rockMesh.getTile(this.getResponder().getCurrentTile().tilex, this.getResponder().getCurrentTile().tiley));
                final short height = Tiles.decodeHeight(Server.surfaceMesh.getTile(this.getResponder().getCurrentTile().tilex, this.getResponder().getCurrentTile().tiley));
                final int diff = height - rock;
                buf.append("text{type='';text='The rock is at " + rock + " decimeter, soil at " + height + " decimeter above sea level. Suggested height above rock is " + diff / 2 + " decimeter.'}");
                if (diff > 3) {
                    buf.append("harray{input{id='height'; maxchars='4'; text='" + diff / 2 + "'}label{text='Height in decimeters over rock layer'}}");
                }
                else {
                    buf.append("text{type='';text='The soil here is too shallow.'}");
                }
                buf.append("harray{label{text=\"Just put on surface \"};checkbox{id=\"putonsurf\";selected=\"false\"};}");
                buf.append("text{type='';text='Here is a random location position for treasure hunts:'}");
                this.findTreasureHuntLocation(buf);
            }
        }
        catch (NoSuchItemException ex) {}
        buf.append(this.createAnswerButton2());
        this.getResponder().getCommunicator().sendBml(300, 300, true, true, buf.toString(), 200, 200, 200, this.title);
    }
    
    private final void findTreasureHuntLocation(final StringBuilder buf) {
        for (int x = 0; x < 10; ++x) {
            final int suggx = Server.rand.nextInt(Zones.worldTileSizeX);
            final int suggy = Server.rand.nextInt(Zones.worldTileSizeY);
            final short rock = Tiles.decodeHeight(Server.rockMesh.getTile(suggx, suggy));
            final short height = Tiles.decodeHeight(Server.surfaceMesh.getTile(suggx, suggy));
            if (height > 0) {
                final int diff = height - rock;
                if (diff >= 2) {
                    buf.append("text{type='';text='Tile at " + suggx + ", " + suggy + " has depth " + diff + "'}");
                    break;
                }
            }
        }
    }
}
