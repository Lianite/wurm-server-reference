// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.questions;

import com.wurmonline.server.Server;
import com.wurmonline.server.players.PlayerInfo;
import com.wurmonline.server.kingdom.Appointment;
import com.wurmonline.server.players.Player;
import com.wurmonline.server.players.PlayerInfoFactory;
import com.wurmonline.server.LoginHandler;
import com.wurmonline.server.Players;
import com.wurmonline.server.kingdom.Appointments;
import com.wurmonline.server.kingdom.King;
import java.util.Properties;
import com.wurmonline.server.creatures.Creature;

public final class AppointmentsQuestion extends Question
{
    public AppointmentsQuestion(final Creature aResponder, final String aTitle, final String aQuestion, final long aTarget) {
        super(aResponder, aTitle, aQuestion, 63, aTarget);
    }
    
    @Override
    public void answer(final Properties answers) {
        final King k = King.getKing(this.getResponder().getKingdomId());
        if (k != null && k.kingid == this.getResponder().getWurmId()) {
            final Appointments a = Appointments.getAppointments(k.era);
            if (a != null) {
                this.addAppointments(a, k, answers);
            }
        }
    }
    
    public void addAppointments(final Appointments a, final King k, final Properties answers) {
        for (int x = 0; x < a.availableOrders.length; ++x) {
            final String val = answers.getProperty("order" + x);
            if (val != null) {
                if (val.length() > 0) {
                    final Player p = Players.getInstance().getPlayerOrNull(LoginHandler.raiseFirstLetter(val));
                    if (p == null) {
                        this.getResponder().getCommunicator().sendNormalServerMessage("There is no person with the name " + val + " present in your kingdom.");
                    }
                    else {
                        p.addAppointment(a.getAppointment(x + 30), this.getResponder());
                    }
                }
            }
        }
        for (int x = 0; x < a.availableTitles.length; ++x) {
            final String val = answers.getProperty("title" + x);
            if (val != null) {
                if (val.length() > 0) {
                    final Player p = Players.getInstance().getPlayerOrNull(LoginHandler.raiseFirstLetter(val));
                    if (p == null) {
                        this.getResponder().getCommunicator().sendNormalServerMessage("There is no person with the name " + val + " present in your kingdom.");
                    }
                    else {
                        p.addAppointment(a.getAppointment(x), this.getResponder());
                    }
                }
            }
        }
        for (int x = 0; x < a.officials.length; ++x) {
            final String val = answers.getProperty("official" + x);
            if (val == null || val.length() <= 0) {
                final Appointment app = a.getAppointment(x + 1500);
                if (app != null && a.officials[x] > 0L) {
                    final Player oldp = Players.getInstance().getPlayerOrNull(a.officials[x]);
                    if (oldp != null) {
                        oldp.getCommunicator().sendNormalServerMessage("You are hereby notified that you have been removed of the office as " + app.getNameForGender(oldp.getSex()) + ".", (byte)2);
                    }
                    else {
                        final PlayerInfo pinf = PlayerInfoFactory.getPlayerInfoWithWurmId(a.officials[x]);
                        if (pinf != null) {
                            this.getResponder().getCommunicator().sendNormalServerMessage("Failed to notify " + pinf.getName() + " that they have been removed from the office of " + app.getNameForGender((byte)0) + ".", (byte)3);
                        }
                    }
                    this.getResponder().getCommunicator().sendNormalServerMessage("You vacate the office of " + app.getNameForGender((byte)0) + ".", (byte)2);
                    a.setOfficial(x + 1500, 0L);
                }
            }
            else if (val.compareToIgnoreCase(PlayerInfoFactory.getPlayerName(a.officials[x])) != 0) {
                final Player p = Players.getInstance().getPlayerOrNull(LoginHandler.raiseFirstLetter(val));
                if (p == null) {
                    this.getResponder().getCommunicator().sendNormalServerMessage("There is no person with the name " + val + " present in your kingdom.");
                }
                else {
                    p.addAppointment(a.getAppointment(x + 1500), this.getResponder());
                }
            }
        }
    }
    
    private void addTitleStrings(final Appointments a, final King k, final StringBuilder buf) {
        buf.append("text{type='italic';text='Titles'}");
        for (int x = 0; x < a.availableTitles.length; ++x) {
            final String key = "title" + x;
            if (a.getAvailTitlesForId(x) > 0) {
                final Appointment app = a.getAppointment(x);
                if (app != null) {
                    buf.append("harray{label{text='" + app.getNameForGender((byte)0) + " (" + a.getAvailTitlesForId(x) + ")'}};input{id='" + key + "'; maxchars='40'; text=''}");
                }
            }
        }
        buf.append("text{text=''}");
    }
    
    private void addOrderStrings(final Appointments a, final King k, final StringBuilder buf) {
        buf.append("text{type='italic';text='Orders and decorations'}");
        for (int x = 0; x < a.availableOrders.length; ++x) {
            final String key = "order" + x;
            if (a.getAvailOrdersForId(x + 30) > 0) {
                final Appointment app = a.getAppointment(x + 30);
                if (app != null) {
                    buf.append("harray{label{text='" + app.getNameForGender((byte)0) + " (" + a.getAvailOrdersForId(x + 30) + ")'}};input{id='" + key + "'; maxchars='40'; text=''}");
                }
            }
        }
        buf.append("text{text=''}");
    }
    
    private void addOfficeStrings(final Appointments a, final King k, final StringBuilder buf) {
        buf.append("text{type='italic';text='Offices. Note: You can only set these once per week and only to players who are online.'}");
        for (int x = 0; x < a.officials.length; ++x) {
            final String key = "official" + x;
            String oldval = "";
            final long current = a.getOfficialForId(x + 1500);
            if (current > 0L) {
                final PlayerInfo pinf = PlayerInfoFactory.getPlayerInfoWithWurmId(current);
                if (pinf != null) {
                    oldval = pinf.getName();
                }
            }
            final Appointment app = a.getAppointment(x + 1500);
            if (app != null) {
                String set = "(available)";
                if (a.isOfficeSet(x + 1500)) {
                    set = "(not available)";
                }
                String aname = app.getNameForGender((byte)0);
                if (this.getResponder().getSex() == 0 && app.getId() == 1507) {
                    aname = app.getNameForGender((byte)1);
                }
                buf.append("harray{label{text='" + aname + " " + set + "'}};input{id='" + key + "'; maxchars='40'; text='" + oldval + "'}");
            }
        }
        buf.append("text{text=''}");
    }
    
    @Override
    public void sendQuestion() {
        final StringBuilder buf = new StringBuilder();
        buf.append(this.getBmlHeader());
        buf.append("header{text='Kingdom appointments:'}text{text=''}");
        final King k = King.getKing(this.getResponder().getKingdomId());
        if (k != null && k.kingid == this.getResponder().getWurmId()) {
            final Appointments a = Appointments.getAppointments(k.era);
            if (a == null) {
                return;
            }
            final long timeLeft = a.getResetTimeRemaining();
            if (timeLeft <= 0L) {
                buf.append("text{text='Titles and orders will refresh shortly.'}");
            }
            else {
                buf.append("text{text='Titles and orders will refresh in " + Server.getTimeFor(timeLeft) + ".'}");
            }
            buf.append("text{text=''}");
            this.addTitleStrings(a, k, buf);
            this.addOrderStrings(a, k, buf);
            this.addOfficeStrings(a, k, buf);
        }
        else {
            buf.append("text{text='You are not the current ruler.'}");
        }
        buf.append(this.createAnswerButton2());
        this.getResponder().getCommunicator().sendBml(600, 600, true, true, buf.toString(), 200, 200, 200, this.title);
    }
}
