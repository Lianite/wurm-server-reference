// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.questions;

import java.util.Set;
import com.wurmonline.server.items.Item;
import com.wurmonline.server.villages.Village;
import com.wurmonline.server.villages.NoSuchVillageException;
import com.wurmonline.server.NoSuchItemException;
import java.util.logging.Level;
import com.wurmonline.server.structures.FenceGate;
import com.wurmonline.server.villages.NoSuchRoleException;
import com.wurmonline.server.villages.Villages;
import com.wurmonline.server.Items;
import java.util.Properties;
import com.wurmonline.server.creatures.Creature;
import java.util.logging.Logger;
import com.wurmonline.server.TimeConstants;
import com.wurmonline.server.villages.VillageStatus;

public final class GateManagementQuestion extends Question implements VillageStatus, TimeConstants
{
    private static final Logger logger;
    private boolean confirmed;
    private int hundredCount;
    
    public GateManagementQuestion(final Creature aResponder, final String aTitle, final String aQuestion, final long aTarget) {
        super(aResponder, aTitle, aQuestion, 42, aTarget);
        this.confirmed = false;
        this.hundredCount = 0;
    }
    
    @Override
    public void answer(final Properties answers) {
        final String key = "gatenums";
        final String val = answers.getProperty("gatenums");
        if (val != null && val.length() > 0) {
            for (int x = 0; x < 10; ++x) {
                if (val.equals("g" + x)) {
                    final GateManagementQuestion vs = new GateManagementQuestion(this.getResponder(), "Manage gates", "Managing gates.", this.target);
                    if (vs != null) {
                        vs.confirmed = true;
                        vs.hundredCount = x + 1;
                        vs.sendQuestion();
                    }
                    return;
                }
            }
        }
        this.setAnswer(answers);
        QuestionParser.parseGateManageQuestion(this);
    }
    
    @Override
    public void sendQuestion() {
        try {
            Village village;
            if (this.target == -10L) {
                village = this.getResponder().getCitizenVillage();
            }
            else {
                final Item deed = Items.getItem(this.target);
                final int villageId = deed.getData2();
                village = Villages.getVillage(villageId);
            }
            final StringBuilder buf = new StringBuilder(this.getBmlHeader());
            buf.append("text{text=\"These settings only apply to gates with a gate lock.\"}");
            buf.append("text{type=\"bold\";text=\"When setting opening times, 0-23 means always open.\"}");
            buf.append("text{type=\"bold\";text=\"If the times are the same (such as 0-0 or 11-11) the gate will always be closed.\"}");
            final Set<FenceGate> gates = village.getGates();
            if (gates != null) {
                final int sz = gates.size();
                if (this.confirmed || sz < 100) {
                    final String rolename = "everybody";
                    try {
                        village.getRoleForStatus((byte)1);
                    }
                    catch (NoSuchRoleException ex) {}
                    final FenceGate[] gs = gates.toArray(new FenceGate[gates.size()]);
                    int max = gs.length;
                    int start = 0;
                    if (this.hundredCount > 0) {
                        max = Math.min(gs.length, this.hundredCount * 100 - 1);
                        start = Math.max(0, this.hundredCount - 1) * 100;
                    }
                    buf.append("table{rows=\"" + (max - start) + "\";cols=\"7\";");
                    for (int g = start; g < max; ++g) {
                        final long gateId = gs[g].getWurmId();
                        buf.append("label{text=\"Gate\"}input{id=\"gate" + gateId + "\";maxchars=\"40\";text=\"" + gs[g].getName() + "\"}label{text=\"Open to " + "everybody" + " from:\"}input{id=\"open" + gateId + "\";maxchars=\"2\";text=\"" + gs[g].getOpenTime() + "\"}label{text=\"(0-23) to:\"}input{id=\"close" + gateId + "\";maxchars=\"2\"; text=\"" + gs[g].getCloseTime() + "\"}label{text=\"(0-23)\"}");
                    }
                    buf.append("}");
                }
                else {
                    final int x = sz / 100;
                    buf.append("text{type=\"bold\";text=\"Select the range of gates to manage:\"}");
                    for (int y = 0; y <= x; ++y) {
                        buf.append("radio{ group=\"gatenums\"; id=\"g" + y + "\";text=\"" + y * 100 + "-" + y + "99\";selected=\"" + (y == 0) + "\"}");
                    }
                }
            }
            else {
                buf.append("text{text=\"No gates in this settlement found.\"}text{text=\"\"}");
            }
            buf.append(this.createAnswerButton2());
            this.getResponder().getCommunicator().sendBml(450, 300, true, true, buf.toString(), 200, 200, 200, this.title);
        }
        catch (NoSuchItemException nsi) {
            GateManagementQuestion.logger.log(Level.WARNING, "Failed to locate village deed with id " + this.target, nsi);
            this.getResponder().getCommunicator().sendNormalServerMessage("Failed to locate the deed item for that request. Please contact administration.");
        }
        catch (NoSuchVillageException nsv) {
            GateManagementQuestion.logger.log(Level.WARNING, "Failed to locate village for deed with id " + this.target, nsv);
            this.getResponder().getCommunicator().sendNormalServerMessage("Failed to locate the village for that request. Please contact administration.");
        }
    }
    
    static {
        logger = Logger.getLogger(GateManagementQuestion.class.getName());
    }
}
