// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.questions;

import java.util.Properties;
import com.wurmonline.server.creatures.Creature;
import com.wurmonline.server.items.Item;
import com.wurmonline.server.WurmHarvestables;

public final class GMSelectHarvestable extends Question
{
    private WurmHarvestables.Harvestable[] harvestables;
    private Item paper;
    
    public GMSelectHarvestable(final Creature aResponder, final Item apaper) {
        super(aResponder, "Select Harvestabke", "Select Harvestabke", 140, -10L);
        this.harvestables = null;
        this.paper = apaper;
    }
    
    @Override
    public void answer(final Properties answers) {
        this.setAnswer(answers);
        final String sel = answers.getProperty("harvestable");
        final int selId = Integer.parseInt(sel);
        final WurmHarvestables.Harvestable harvestable = this.harvestables[selId];
        this.paper.setAuxData((byte)(harvestable.getHarvestableId() + 8));
        this.paper.setData1(99);
        this.paper.setInscription(harvestable.getName() + " report", this.getResponder().getName(), 0);
        this.paper.setName(harvestable.getName() + " report", true);
        this.getResponder().getCommunicator().sendNormalServerMessage("You carefully finish writing the " + harvestable.getName() + " report and sign it.");
    }
    
    @Override
    public void sendQuestion() {
        this.harvestables = WurmHarvestables.getHarvestables();
        final StringBuilder buf = new StringBuilder(this.getBmlHeader());
        buf.append("harray{label{text=\"Harvestable\"};");
        buf.append("dropdown{id=\"harvestable\";default=\"0\";options=\"");
        for (int i = 0; i < this.harvestables.length; ++i) {
            if (i > 0) {
                buf.append(",");
            }
            final WurmHarvestables.Harvestable harvestable = this.harvestables[i];
            buf.append(harvestable.getName().replace(",", "") + " (" + harvestable.getHarvestableId() + ")");
        }
        buf.append("\"}}");
        buf.append("label{text=\"\"}");
        buf.append(this.createAnswerButton2());
        this.getResponder().getCommunicator().sendBml(300, 120, true, true, buf.toString(), 200, 200, 200, this.title);
    }
}
