// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.questions;

import java.util.Set;
import java.util.HashSet;
import com.wurmonline.server.players.Player;
import com.wurmonline.server.LoginServerWebConnection;
import java.util.Properties;
import com.wurmonline.server.creatures.Creature;

public final class ReimbursementQuestion extends Question
{
    private String[] nameArr;
    
    public ReimbursementQuestion(final Creature aResponder, final long aTarget) {
        super(aResponder, "Reimbursements", "These are your available reimbursements:", 50, aTarget);
        this.nameArr = new String[0];
    }
    
    @Override
    public void answer(final Properties answers) {
        String key = "";
        String value = "";
        for (int x = 0; x < this.nameArr.length; ++x) {
            int days = 0;
            int trinkets = 0;
            int silver = 0;
            boolean boktitle = false;
            boolean mbok = false;
            key = "silver" + this.nameArr[x];
            value = answers.getProperty(key);
            if (value != null) {
                try {
                    silver = Integer.parseInt(value);
                }
                catch (Exception ex) {
                    this.getResponder().getCommunicator().sendAlertServerMessage("Wrong amount of silver for " + this.nameArr[x]);
                    return;
                }
            }
            key = "days" + this.nameArr[x];
            value = answers.getProperty(key);
            if (value != null) {
                try {
                    days = Integer.parseInt(value);
                }
                catch (Exception ex) {
                    this.getResponder().getCommunicator().sendAlertServerMessage("Wrong amount of days for " + this.nameArr[x]);
                    return;
                }
            }
            key = "trinket" + this.nameArr[x];
            value = answers.getProperty(key);
            if (value != null) {
                try {
                    trinkets = Integer.parseInt(value);
                }
                catch (Exception ex) {
                    this.getResponder().getCommunicator().sendAlertServerMessage("Wrong amount of trinkets for " + this.nameArr[x]);
                    return;
                }
            }
            key = "mbok" + this.nameArr[x];
            value = answers.getProperty(key);
            if (value != null) {
                try {
                    boktitle = Boolean.parseBoolean(value);
                    if (boktitle) {
                        mbok = true;
                    }
                }
                catch (Exception ex) {
                    this.getResponder().getCommunicator().sendAlertServerMessage("Unable to parse the MBoK/Title answer for " + this.nameArr[x]);
                    return;
                }
            }
            if (!boktitle) {
                key = "bok" + this.nameArr[x];
                value = answers.getProperty(key);
                if (value != null) {
                    try {
                        boktitle = Boolean.parseBoolean(value);
                    }
                    catch (Exception ex) {
                        this.getResponder().getCommunicator().sendAlertServerMessage("Unable to parse the BoK/Title answer for " + this.nameArr[x]);
                        return;
                    }
                }
            }
            if (days > 0 || trinkets > 0 || silver > 0 || boktitle) {
                if (days < 0 || trinkets < 0 || silver < 0) {
                    this.getResponder().getCommunicator().sendAlertServerMessage("Less than 0 value entered for " + this.nameArr[x]);
                }
                else {
                    final LoginServerWebConnection lsw = new LoginServerWebConnection();
                    this.getResponder().getCommunicator().sendNormalServerMessage(lsw.withDraw((Player)this.getResponder(), this.nameArr[x], ((Player)this.getResponder()).getSaveFile().emailAddress, trinkets, silver, boktitle, mbok, days));
                }
            }
        }
    }
    
    @Override
    public void sendQuestion() {
        final LoginServerWebConnection lsw = new LoginServerWebConnection();
        final StringBuilder buf = new StringBuilder();
        buf.append(this.getBmlHeader());
        final String s = lsw.getReimburseInfo((Player)this.getResponder());
        if (s.equals("text{text='You have no reimbursements pending.'}")) {
            ((Player)this.getResponder()).getSaveFile().setHasNoReimbursementLeft(true);
        }
        else {
            String ttext = s;
            String newName = "";
            final Set<String> names = new HashSet<String>();
            boolean keepGoing = true;
            while (keepGoing) {
                newName = this.getNextName(ttext);
                if (newName.equals("")) {
                    keepGoing = false;
                }
                else {
                    names.add(newName);
                    ttext = ttext.substring(ttext.indexOf(" - '}") + 5, ttext.length());
                }
            }
            this.nameArr = names.toArray(new String[names.size()]);
        }
        buf.append(s);
        buf.append(this.createAnswerButton2());
        this.getResponder().getCommunicator().sendBml(400, 300, true, true, buf.toString(), 200, 200, 200, this.title);
    }
    
    private String getNextName(final String ttext) {
        final int place = ttext.indexOf("Name=");
        if (place > 0) {
            return ttext.substring(place + 5, ttext.indexOf(" - '}"));
        }
        return "";
    }
}
