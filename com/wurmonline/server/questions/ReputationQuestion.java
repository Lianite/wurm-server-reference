// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.questions;

import com.wurmonline.server.villages.Reputation;
import com.wurmonline.server.items.Item;
import com.wurmonline.server.villages.Village;
import com.wurmonline.server.villages.NoSuchVillageException;
import com.wurmonline.server.NoSuchItemException;
import java.util.logging.Level;
import com.wurmonline.server.NoSuchPlayerException;
import com.wurmonline.server.Features;
import com.wurmonline.server.villages.Villages;
import com.wurmonline.server.Items;
import java.util.Properties;
import java.util.HashMap;
import com.wurmonline.server.creatures.Creature;
import java.util.Map;
import java.util.logging.Logger;

public final class ReputationQuestion extends Question
{
    private static final Logger logger;
    private final Map<Long, Integer> itemMap;
    
    public ReputationQuestion(final Creature aResponder, final String aTitle, final String aQuestion, final long aTarget) {
        super(aResponder, aTitle, aQuestion, 24, aTarget);
        this.itemMap = new HashMap<Long, Integer>();
    }
    
    @Override
    public void answer(final Properties answers) {
        this.setAnswer(answers);
        QuestionParser.parseReputationQuestion(this);
    }
    
    public Map<Long, Integer> getItemMap() {
        return this.itemMap;
    }
    
    @Override
    public void sendQuestion() {
        try {
            int ids = 0;
            Village village;
            if (this.target == -10L) {
                village = this.getResponder().getCitizenVillage();
            }
            else {
                final Item deed = Items.getItem(this.target);
                final int villageId = deed.getData2();
                village = Villages.getVillage(villageId);
            }
            if (village == null) {
                this.getResponder().getCommunicator().sendNormalServerMessage("No settlement found.");
            }
            else {
                final Reputation[] reputations = village.getReputations();
                final boolean kos = village.isKosAllowed();
                final StringBuilder buf = new StringBuilder(this.getBmlHeader());
                buf.append("text{type=\"bold\";text=\"Reputations for " + village.getName() + "\"}text{text=\"\"}");
                buf.append("text{text=\"Permanent means that it will not change. If set to 0 it will go away though.\"}");
                buf.append("text{text=\"Use permanent with care and normally to point out enemies, since it effectively overrides any settlement role settings.\"}");
                buf.append("text{text=\"Max is 100 and min is -100. The guards attack at -30.\"}");
                buf.append("text{text=\"\"}");
                boolean showlist = true;
                if (Features.Feature.HIGHWAYS.isEnabled() && !kos) {
                    if (village.hasHighway()) {
                        buf.append("text{color=\"155,155,50\";text=\"KOS is disabled for this settlement as there is highway in it.\"}");
                    }
                    else {
                        buf.append("text{text=\"Note: KOS is not enabled for this settlement, to change this, use settlement settings.\"}");
                    }
                    buf.append("text{text=\"\"}");
                    showlist = (reputations.length > 0);
                }
                if (Features.Feature.HIGHWAYS.isEnabled() && kos && reputations.length == 0) {
                    buf.append("text{color=\"155,155,50\";text=\"If you add anyone to KOS, you will not be able to add a highway in this village.\"}");
                }
                int szy = 300;
                if (showlist) {
                    buf.append("table{rows=\"" + (Math.min(100, reputations.length) + 2) + "\";cols=\"3\";");
                    buf.append("label{text=\"Creature name\"};label{text=\"Reputation\"};label{text=\"Permanent\"}");
                    if (!Features.Feature.HIGHWAYS.isEnabled() || kos) {
                        buf.append("harray{input{maxchars=\"40\";id=\"nn\"};label{text=\" \"}}");
                        buf.append("harray{input{maxchars=\"4\"; id=\"nr\";text=\"-100\"};label{text=\" \"}}");
                        buf.append("checkbox{id=\"np\";selected=\"false\";text=\" \"}");
                    }
                    szy = 400;
                    if (reputations.length > 10) {
                        szy = 500;
                    }
                    for (int x = 0; x < reputations.length; ++x) {
                        if (ids < 100) {
                            final long wid = reputations[x].getWurmId();
                            try {
                                ++ids;
                                buf.append("label{text=\"" + reputations[x].getNameFor() + "\"};");
                                buf.append("harray{input{maxchars=\"4\"; id=\"" + ids + "r\"; text=\"" + reputations[x].getValue() + "\"};label{text=\" \"}}");
                                final String ch = reputations[x].isPermanent() ? "selected=\"true\";" : "";
                                buf.append("checkbox{id=\"" + ids + "p\";" + ch + "text= \" \"}");
                                this.itemMap.put(new Long(wid), ids);
                            }
                            catch (NoSuchPlayerException nsp2) {
                                village.removeReputation(wid);
                            }
                        }
                    }
                    buf.append("}");
                    if (ids >= 99) {
                        buf.append("text{text=\"The list was truncated. Some reputations are missing.\"}");
                    }
                    buf.append("text{text=\"\"}");
                }
                buf.append(this.createAnswerButton2());
                this.getResponder().getCommunicator().sendBml(500, szy, true, true, buf.toString(), 200, 200, 200, this.title);
            }
        }
        catch (NoSuchItemException nsi) {
            this.getResponder().getCommunicator().sendNormalServerMessage("No such item.");
            ReputationQuestion.logger.log(Level.WARNING, this.getResponder().getName(), nsi);
        }
        catch (NoSuchVillageException nsp) {
            this.getResponder().getCommunicator().sendNormalServerMessage("No such settlement.");
            ReputationQuestion.logger.log(Level.WARNING, this.getResponder().getName(), nsp);
        }
    }
    
    static {
        logger = Logger.getLogger(ReputationQuestion.class.getName());
    }
}
