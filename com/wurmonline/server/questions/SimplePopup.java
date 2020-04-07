// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.questions;

import com.wurmonline.server.players.PlayerInfo;
import java.util.Iterator;
import com.wurmonline.server.items.WurmColor;
import com.wurmonline.server.players.PlayerInfoFactory;
import com.wurmonline.server.epic.MissionHelper;
import java.util.Properties;
import com.wurmonline.server.creatures.Creature;
import com.wurmonline.server.items.InscriptionData;

public final class SimplePopup extends Question
{
    private String toSend;
    private long missionId;
    private float maxHelps;
    private InscriptionData papyrusData;
    
    public SimplePopup(final Creature _responder, final String _title, final String _question) {
        super(_responder, _title, _question, 62, -10L);
        this.toSend = "";
        this.missionId = -10L;
        this.maxHelps = -10.0f;
        this.papyrusData = null;
        this.windowSizeX = 300;
        this.windowSizeY = 300;
    }
    
    public SimplePopup(final Creature _responder, final String _title, final String _question, final String _toSend) {
        super(_responder, _title, _question, 62, -10L);
        this.toSend = "";
        this.missionId = -10L;
        this.maxHelps = -10.0f;
        this.papyrusData = null;
        this.windowSizeX = 300;
        this.windowSizeY = 300;
        this.toSend = _toSend;
    }
    
    public SimplePopup(final Creature _responder, final String _title, final InscriptionData thePapyrusData) {
        super(_responder, _title, "You find a message inscribed on the papyrus.", 62, -10L);
        this.toSend = "";
        this.missionId = -10L;
        this.maxHelps = -10.0f;
        this.papyrusData = null;
        this.windowSizeX = 350;
        this.windowSizeY = 300;
        this.papyrusData = thePapyrusData;
    }
    
    public SimplePopup(final Creature _responder, final String _title, final String _question, final long _missionId, final float _maxHelps) {
        super(_responder, _title, _question, 62, -10L);
        this.toSend = "";
        this.missionId = -10L;
        this.maxHelps = -10.0f;
        this.papyrusData = null;
        this.windowSizeX = 300;
        this.windowSizeY = 300;
        this.missionId = _missionId;
        this.maxHelps = _maxHelps;
    }
    
    @Override
    public void answer(final Properties answers) {
    }
    
    public void sendQuestion(final String sendButtonText) {
        final StringBuilder buf = new StringBuilder();
        buf.append(this.getBmlHeader());
        buf.append("text{text=\"\"}");
        if (this.missionId > 0L) {
            for (final MissionHelper helper : MissionHelper.getHelpers().values()) {
                final int i = helper.getHelps(this.missionId);
                if (i > 0) {
                    final PlayerInfo pinf = PlayerInfoFactory.getPlayerInfoWithWurmId(helper.getPlayerId());
                    if (pinf != null) {
                        buf.append("text{text=\"" + pinf.getName() + ": " + i + " (" + i * 100 / this.maxHelps + "%) \"}");
                    }
                    else {
                        buf.append("text{text=\"Unknown: " + i + " (" + i * 100 / this.maxHelps + "%) \"}");
                    }
                }
            }
            buf.append("text{text=\"\"}");
        }
        else if (this.papyrusData != null) {
            buf.append("input{id=\"answer\";enabled=\"false\";maxchars=\"" + this.papyrusData.getInscription().length() + "\";maxlines=\"-1\";bgcolor=\"200,200,200\";color=\"" + WurmColor.getColorRed(this.papyrusData.getPenColour()) + "," + WurmColor.getColorGreen(this.papyrusData.getPenColour()) + "," + WurmColor.getColorBlue(this.papyrusData.getPenColour()) + "\";text=\"" + this.papyrusData.getInscription() + "\"}");
            buf.append("text{text=\"Signed " + this.papyrusData.getInscriber() + "\"};");
        }
        else if (this.toSend.length() > 0) {
            if (this.toSend.contains("{") && this.toSend.contains("}")) {
                buf.append(this.toSend);
            }
            else {
                buf.append("text{text=\"" + this.toSend + "\"}");
            }
            buf.append("text{text=\"\"}");
        }
        buf.append(this.createAnswerButton2(sendButtonText));
        this.getResponder().getCommunicator().sendBml(this.windowSizeX, this.windowSizeY, true, true, buf.toString(), 200, 200, 200, this.title);
    }
    
    @Override
    public void sendQuestion() {
        this.sendQuestion("Send");
    }
    
    public String getToSend() {
        return this.toSend;
    }
    
    public void setToSend(final String aToSend) {
        this.toSend = aToSend;
    }
}
