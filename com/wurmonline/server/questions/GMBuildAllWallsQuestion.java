// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.questions;

import com.wurmonline.server.structures.Door;
import com.wurmonline.server.structures.Wall;
import java.io.IOException;
import com.wurmonline.server.structures.DbDoor;
import com.wurmonline.shared.constants.StructureStateEnum;
import java.util.logging.Level;
import java.util.Properties;
import com.wurmonline.server.creatures.Creature;
import com.wurmonline.server.structures.WallEnum;
import java.util.List;
import com.wurmonline.server.items.Item;
import com.wurmonline.server.structures.Structure;
import java.util.logging.Logger;

public class GMBuildAllWallsQuestion extends Question
{
    private static final Logger logger;
    private final Structure targetStructure;
    private final Item buildItem;
    private final List<WallEnum> wallList;
    
    public GMBuildAllWallsQuestion(final Creature aResponder, final Structure aTarget) {
        super(aResponder, "Build all unfinished walls", "What type of walls?", 143, aTarget.getWurmId());
        this.targetStructure = aTarget;
        this.buildItem = aResponder.getCarriedItem(176);
        (this.wallList = WallEnum.getWallsByTool(this.getResponder(), this.buildItem, false, false)).sort((o1, o2) -> o1.getName().compareToIgnoreCase(o2.getName()));
    }
    
    @Override
    public void answer(final Properties aAnswer) {
        this.setAnswer(aAnswer);
        if (this.type == 0) {
            GMBuildAllWallsQuestion.logger.log(Level.INFO, "Received answer for a question with NOQUESTION.");
            return;
        }
        if (this.type == 143 && this.getResponder().getPower() >= 4) {
            try {
                final String prop = aAnswer.getProperty("walltype");
                if (prop != null) {
                    final short wallId = Short.parseShort(prop);
                    if (wallId >= this.wallList.size() || wallId < 0) {
                        GMBuildAllWallsQuestion.logger.fine("WallID was larger than WallList.size(), parsed value = " + wallId);
                        this.getResponder().getCommunicator().sendAlertServerMessage("ERROR: Something went wrong with parsing the input. Aborting.");
                        return;
                    }
                    this.getResponder().getLogger().log(Level.INFO, "Building all unfinished walls of structure with ID=" + this.targetStructure.getWurmId() + "to type " + this.wallList.get(wallId).getName());
                    for (final Wall w : this.targetStructure.getWalls()) {
                        if (w.isWallPlan()) {
                            w.setQualityLevel(80.0f);
                            w.setMaterial(this.wallList.get(wallId).getMaterial());
                            w.setType(this.wallList.get(wallId).getType());
                            w.setState(StructureStateEnum.FINISHED);
                            w.setDamage(0.0f);
                            if (w.isDoor()) {
                                final Door door = new DbDoor(w);
                                door.setStructureId(this.targetStructure.getOwnerId());
                                this.targetStructure.addDoor(door);
                                try {
                                    door.save();
                                    door.addToTiles();
                                }
                                catch (IOException e) {
                                    GMBuildAllWallsQuestion.logger.warning("Failed to save door! " + e);
                                    this.getResponder().getCommunicator().sendAlertServerMessage("ERROR: IOException. Aborting!");
                                    return;
                                }
                            }
                            w.getTile().updateWall(w);
                        }
                    }
                    this.targetStructure.updateStructureFinishFlag();
                }
            }
            catch (NumberFormatException nsf) {
                this.getResponder().getCommunicator().sendNormalServerMessage("Unable to set wall types with that answer");
            }
        }
    }
    
    @Override
    public void sendQuestion() {
        if (this.buildItem == null) {
            this.getResponder().getCommunicator().sendNormalServerMessage("You need to have at least one Ebony Wand in your inventory for this to work.");
            return;
        }
        final StringBuilder buf = new StringBuilder();
        buf.append(this.getBmlHeader());
        if (this.getResponder().getPower() >= 4) {
            buf.append("text{type=\"bold\";text=\"Choose the type of wall all wall plans will be turned into:\"}");
            final boolean isSelected = false;
            buf.append("harray{label{text='Tile type'}dropdown{id='walltype';options=\"");
            for (int i = 0; i < this.wallList.size(); ++i) {
                buf.append(this.wallList.get(i).getName() + ",");
            }
            buf.append("\"}}");
            buf.append(this.createAnswerButton2());
            this.getResponder().getCommunicator().sendBml(250, 150, true, true, buf.toString(), 200, 200, 200, this.title);
        }
    }
    
    static {
        logger = Logger.getLogger(GMBuildAllWallsQuestion.class.getName());
    }
}
