// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.questions;

import com.wurmonline.server.zones.Zones;
import java.util.Properties;
import com.wurmonline.server.structures.NoSuchStructureException;
import java.util.logging.Level;
import com.wurmonline.server.structures.Structures;
import com.wurmonline.server.creatures.Creature;
import com.wurmonline.server.structures.Structure;
import java.util.logging.Logger;

public final class DemolishCheckQuestion extends Question
{
    private static final Logger logger;
    private Structure building;
    
    public DemolishCheckQuestion(final Creature aResponder, final String aTitle, final String aQuestion, final long aTarget) {
        super(aResponder, aTitle, aQuestion, 127, aTarget);
        this.building = null;
        try {
            this.building = Structures.getStructure(aTarget);
        }
        catch (NoSuchStructureException nss) {
            DemolishCheckQuestion.logger.log(Level.WARNING, nss.getMessage(), nss);
        }
    }
    
    @Override
    public void answer(final Properties answers) {
        final String val = answers.getProperty("demolish");
        if (val != null && val.equals("true")) {
            if (this.getResponder().getPower() >= 2) {
                this.getResponder().getLogger().log(Level.INFO, this.getResponder().getName() + " destroyed structure " + this.building.getName() + " at " + this.building.getCenterX() + ", " + this.building.getCenterY());
            }
            if (this.building.isOnSurface()) {
                Zones.flash(this.building.getCenterX(), this.building.getCenterY(), false);
            }
            if (!this.building.hasWalls() || this.getResponder().getPower() >= 2 || this.building.isOwner(this.getResponder())) {
                this.building.totallyDestroy();
            }
        }
    }
    
    @Override
    public void sendQuestion() {
        final StringBuilder buf = new StringBuilder();
        buf.append(this.getBmlHeader());
        buf.append("header{type=\"bold\";text=\"Demolish warning:\"}");
        buf.append("text{text=\"\"}");
        buf.append("text{text=\"You are about to demolish a building.\"}");
        buf.append("text{text=\"Are you really sure you want to?\"}");
        buf.append("harray{button{text=\"Demolish it!\";id=\"demolish\"}label{text=\"  \"}button{text=\"No, was a mistake\";id=\"no\"}}}};null;null;}");
        this.getResponder().getCommunicator().sendBml(200, 200, true, true, buf.toString(), 200, 200, 200, this.title);
    }
    
    static {
        logger = Logger.getLogger(GmTool.class.getName());
    }
}
