// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.questions;

import com.wurmonline.server.players.PlayerInfo;
import com.wurmonline.server.players.PlayerInfoFactory;
import com.wurmonline.server.villages.Citizen;
import java.util.Arrays;
import com.wurmonline.server.items.WurmColor;
import com.wurmonline.server.Items;
import com.wurmonline.server.villages.VillageMessages;
import java.util.Properties;
import java.util.HashMap;
import com.wurmonline.server.creatures.Creature;
import java.util.Map;
import com.wurmonline.server.items.Item;
import com.wurmonline.server.items.InscriptionData;
import com.wurmonline.server.villages.Village;
import java.util.logging.Logger;

public class VillageMessagePopup extends Question
{
    private static final Logger logger;
    private Village village;
    private InscriptionData papyrusData;
    private String message;
    private final Item messageBoard;
    private final Map<Integer, Long> idMap;
    private static final String red = "color=\"255,127,127\"";
    
    public VillageMessagePopup(final Creature aResponder, final Village aVillage, final InscriptionData ins, final long aSource, final Item noticeBoard) {
        super(aResponder, getTitle(aVillage), getQuestion(aVillage), 137, aSource);
        this.message = null;
        this.idMap = new HashMap<Integer, Long>();
        this.messageBoard = noticeBoard;
        this.village = aVillage;
        this.papyrusData = ins;
    }
    
    private static String getTitle(final Village village) {
        return village.getName() + " notice board";
    }
    
    private static String getQuestion(final Village village) {
        return "Add Note";
    }
    
    @Override
    public void answer(final Properties aAnswer) {
        this.setAnswer(aAnswer);
        final String selected = aAnswer.getProperty("select");
        final int select = Integer.parseInt(selected);
        if (select > 0) {
            final long cit = this.idMap.get(select);
            VillageMessages.create(this.village.getId(), this.getResponder().getWurmId(), cit, this.message, this.papyrusData.getPenColour(), cit == -1L);
            if (cit == -1L) {
                this.getResponder().getCommunicator().sendNormalServerMessage("You posted a public notice.");
            }
            else if (cit == -10L) {
                this.getResponder().getCommunicator().sendNormalServerMessage("You posted a notice.");
            }
            else {
                this.getResponder().getCommunicator().sendNormalServerMessage("You posted a note to " + this.getPlayerName(cit) + ".");
            }
            Items.destroyItem(this.target);
        }
    }
    
    @Override
    public void sendQuestion() {
        final StringBuilder buf = new StringBuilder();
        buf.append(this.getBmlHeader());
        final int msglen = this.papyrusData.getInscription().length();
        final int mlen = Math.min(msglen, 500);
        this.message = this.papyrusData.getInscription().substring(0, mlen);
        buf.append("input{id=\"answer\";enabled=\"false\";maxchars=\"" + mlen + "\";maxlines=\"-1\";bgcolor=\"200,200,200\";color=\"" + WurmColor.getColorRed(this.papyrusData.getPenColour()) + "," + WurmColor.getColorGreen(this.papyrusData.getPenColour()) + "," + WurmColor.getColorBlue(this.papyrusData.getPenColour()) + "\";text=\"" + this.message + "\"}");
        buf.append("text{text=\"\"}");
        if (mlen < msglen) {
            buf.append("label{color=\"255,127,127\"text=\"Message is too long, so will be truncated.\"};");
        }
        buf.append("harray{text{type=\"bold\";text=\"Post\"};dropdown{id=\"select\";options=\"");
        buf.append("no where");
        this.idMap.put(0, -10L);
        if (this.messageBoard.mayPostNotices(this.getResponder())) {
            if (this.getResponder().getCitizenVillage() == this.village) {
                this.idMap.put(this.idMap.size(), -10L);
                buf.append(",as village notice");
            }
            this.idMap.put(this.idMap.size(), -1L);
            buf.append(",as public notice");
        }
        if (this.messageBoard.mayAddPMs(this.getResponder())) {
            final Citizen[] citizens = this.village.getCitizens();
            Arrays.sort(citizens);
            for (final Citizen c : citizens) {
                if (c.isPlayer() && c.getId() != this.getResponder().getWurmId() && this.getPlayerName(c.getId()).length() > 0) {
                    this.idMap.put(this.idMap.size(), c.getId());
                    buf.append(",to " + c.getName());
                }
            }
        }
        buf.append("\"}}");
        buf.append(this.createAnswerButton2());
        this.getResponder().getCommunicator().sendBml(400, 300, true, true, buf.toString(), 200, 200, 200, this.title);
    }
    
    private final String getPlayerName(final long id) {
        final PlayerInfo info = PlayerInfoFactory.getPlayerInfoWithWurmId(id);
        if (info == null) {
            return "";
        }
        return info.getName();
    }
    
    static {
        logger = Logger.getLogger(VillageMessagePopup.class.getName());
    }
}
