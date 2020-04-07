// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.questions;

import com.wurmonline.server.kingdom.Appointments;
import com.wurmonline.server.HistoryManager;
import com.wurmonline.server.Server;
import com.wurmonline.server.players.Player;
import com.wurmonline.server.kingdom.King;
import java.util.Properties;
import com.wurmonline.server.creatures.Creature;
import com.wurmonline.server.TimeConstants;

public final class AreaHistoryQuestion extends Question implements TimeConstants
{
    private static final long waittime = 21600000L;
    
    public AreaHistoryQuestion(final Creature aResponder, final String aTitle, final String aQuestion, final long aTarget) {
        super(aResponder, aTitle, aQuestion, 41, aTarget);
    }
    
    @Override
    public void answer(final Properties answers) {
        String newevent = answers.getProperty("newevent");
        if (newevent != null && newevent.length() > 0) {
            final Appointments app = King.getCurrentAppointments(this.getResponder().getKingdomId());
            if (app != null && app.getOfficialForId(1510) == this.getResponder().getWurmId()) {
                if (System.currentTimeMillis() - 21600000L < ((Player)this.getResponder()).getSaveFile().lastCreatedHistoryEvent) {
                    this.getResponder().getCommunicator().sendNormalServerMessage("You need to wait " + Server.getTimeFor(21600000L + ((Player)this.getResponder()).getSaveFile().lastCreatedHistoryEvent - System.currentTimeMillis()) + " before writing history again.");
                }
                else {
                    newevent = newevent.replace("\"", "'");
                    newevent = newevent.replace("\\", "");
                    newevent = newevent.replace("/", "");
                    newevent = newevent.replace(";", "");
                    newevent = newevent.replace("#", "");
                    this.getResponder().getCommunicator().sendNormalServerMessage("You write some history.");
                    HistoryManager.addHistory(this.getResponder().getName() + " note:", newevent);
                    ((Player)this.getResponder()).getSaveFile().lastCreatedHistoryEvent = System.currentTimeMillis();
                }
            }
        }
    }
    
    @Override
    public void sendQuestion() {
        final StringBuilder buf = new StringBuilder();
        buf.append(this.getBmlHeader());
        buf.append("header{text='Area Events:'}text{text=''}");
        final Appointments app = King.getCurrentAppointments(this.getResponder().getKingdomId());
        if (app != null && app.getOfficialForId(1510) == this.getResponder().getWurmId()) {
            buf.append("text{text='Your office allows you to record historic events:'}");
            if (System.currentTimeMillis() - 21600000L < ((Player)this.getResponder()).getSaveFile().lastCreatedHistoryEvent) {
                buf.append("text{text='You need to wait " + Server.getTimeFor(21600000L + ((Player)this.getResponder()).getSaveFile().lastCreatedHistoryEvent - System.currentTimeMillis()) + " before writing history again.'}");
            }
            else {
                buf.append("input{id='newevent';maxchars='200';text=''}");
                buf.append("text{type=\"italic\";text=\"Please note that event texts are governed by game conduct rules so make sure they are not inappropriate.\"}");
            }
        }
        final String[] list = HistoryManager.getHistory(200);
        if (list.length > 0) {
            for (int x = 0; x < list.length; ++x) {
                buf.append("text{text=\"" + list[x] + "\"}");
            }
        }
        else {
            buf.append("text{text='No events recorded.'}");
        }
        buf.append(this.createAnswerButton2());
        this.getResponder().getCommunicator().sendBml(400, 400, true, true, buf.toString(), 200, 200, 200, this.title);
    }
}
