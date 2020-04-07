// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.questions;

import com.wurmonline.server.ServerEntry;
import com.wurmonline.server.webinterface.WcCAHelpGroupMessage;
import com.wurmonline.server.Servers;
import java.util.Properties;
import com.wurmonline.server.creatures.Creature;
import java.util.logging.Logger;

public final class GroupCAHelpQuestion extends Question
{
    private static final Logger logger;
    
    public GroupCAHelpQuestion(final Creature aResponder) {
        super(aResponder, "Group CA Helps", "Setup Grouping of CA Helps?", 115, aResponder.getWurmId());
    }
    
    @Override
    public void answer(final Properties aAnswer) {
        this.setAnswer(aAnswer);
        final Creature responder = this.getResponder();
        for (final ServerEntry se : Servers.getAllServers()) {
            final byte newCAHelpGroup = Byte.parseByte(aAnswer.getProperty(se.getAbbreviation()));
            if (se.getCAHelpGroup() != newCAHelpGroup) {
                if (se.getId() != Servers.getLocalServerId()) {
                    final WcCAHelpGroupMessage wchgm = new WcCAHelpGroupMessage(newCAHelpGroup);
                    wchgm.sendToServer(se.getId());
                }
                se.updateCAHelpGroup(newCAHelpGroup);
            }
        }
        responder.getCommunicator().sendNormalServerMessage("CA Help Groups Updated.");
    }
    
    @Override
    public void sendQuestion() {
        final StringBuilder buf = new StringBuilder(this.getBmlHeader());
        buf.append("text{text=\"This allows you to combine CA Help tabs across servers.\"}");
        buf.append("text{text=\"\"}");
        buf.append("table{rows=\"" + (Servers.getAllServers().length + 1) + "\";cols=\"9\";text{type=\"bold\";text=\"Server\"};text{type=\"bold\";text=\"Single\"};text{type=\"bold\";text=\"Group0\"};text{type=\"bold\";text=\"Group1\"};text{type=\"bold\";text=\"Group2\"};text{type=\"bold\";text=\"Group3\"};text{type=\"bold\";text=\"Group4\"};text{type=\"bold\";text=\"Group5\"};text{type=\"bold\";text=\"Group6\"};");
        for (final ServerEntry se : Servers.getAllServers()) {
            final String sea = se.getAbbreviation();
            final byte seg = se.getCAHelpGroup();
            buf.append("label{text=\"" + sea + "\"};radio{group=\"" + sea + "\";id=\"-1\"" + ((seg == -1) ? ";selected=\"true\"" : "") + "};radio{group=\"" + sea + "\";id=\"0\"" + ((seg == 0) ? ";selected=\"true\"" : "") + "};radio{group=\"" + sea + "\";id=\"1\"" + ((seg == 1) ? ";selected=\"true\"" : "") + "};radio{group=\"" + sea + "\";id=\"2\"" + ((seg == 2) ? ";selected=\"true\"" : "") + "};radio{group=\"" + sea + "\";id=\"3\"" + ((seg == 3) ? ";selected=\"true\"" : "") + "};radio{group=\"" + sea + "\";id=\"4\"" + ((seg == 4) ? ";selected=\"true\"" : "") + "};radio{group=\"" + sea + "\";id=\"5\"" + ((seg == 5) ? ";selected=\"true\"" : "") + "};radio{group=\"" + sea + "\";id=\"6\"" + ((seg == 6) ? ";selected=\"true\"" : "") + "};");
        }
        buf.append("}");
        buf.append(this.createAnswerButton2());
        this.getResponder().getCommunicator().sendBml(450, 300, true, true, buf.toString(), 200, 200, 200, this.title);
    }
    
    static {
        logger = Logger.getLogger(GroupCAHelpQuestion.class.getName());
    }
}
