// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.questions;

import com.wurmonline.server.Servers;
import com.wurmonline.server.webinterface.WcGlobalAlarmMessage;
import com.wurmonline.server.Server;
import java.util.Properties;
import com.wurmonline.server.creatures.Creature;

public final class AlertServerMessageQuestion extends Question
{
    public AlertServerMessageQuestion(final Creature aResponder, final String aTitle, final String aQuestion, final long aTarget) {
        super(aResponder, aTitle, aQuestion, 45, aTarget);
    }
    
    @Override
    public void answer(final Properties answers) {
        final String time1 = answers.getProperty("alt1");
        if (time1 == null || time1.length() == 0) {
            Server.timeBetweenAlertMess1 = Long.MAX_VALUE;
        }
        else {
            try {
                final long seconds = Long.parseLong(time1);
                if (seconds <= 0L) {
                    Server.timeBetweenAlertMess1 = Long.MAX_VALUE;
                    Server.lastAlertMess1 = Long.MAX_VALUE;
                }
                else {
                    Server.timeBetweenAlertMess1 = Math.max(10L, seconds) * 1000L;
                    Server.lastAlertMess1 = 0L;
                }
            }
            catch (Exception e) {
                this.getResponder().getCommunicator().sendAlertServerMessage(time1 + " is not a number.");
            }
        }
        final String time2 = answers.getProperty("alt2");
        if (time2 == null || time2.length() == 0) {
            Server.timeBetweenAlertMess2 = Long.MAX_VALUE;
        }
        else {
            try {
                final long seconds2 = Long.parseLong(time2);
                if (seconds2 <= 0L) {
                    Server.timeBetweenAlertMess2 = Long.MAX_VALUE;
                    Server.lastAlertMess2 = Long.MAX_VALUE;
                }
                else {
                    Server.timeBetweenAlertMess2 = Math.max(10L, seconds2) * 1000L;
                    Server.lastAlertMess2 = 0L;
                }
            }
            catch (Exception e2) {
                this.getResponder().getCommunicator().sendAlertServerMessage(time2 + " is not a number.");
            }
        }
        final String time3 = answers.getProperty("alt3");
        if (time3 == null || time3.length() == 0) {
            Server.timeBetweenAlertMess3 = Long.MAX_VALUE;
        }
        else {
            try {
                final long seconds3 = Long.parseLong(time3);
                if (seconds3 <= 0L) {
                    Server.timeBetweenAlertMess3 = Long.MAX_VALUE;
                    Server.lastAlertMess3 = Long.MAX_VALUE;
                }
                else {
                    Server.timeBetweenAlertMess3 = Math.max(10L, seconds3) * 1000L;
                    Server.lastAlertMess3 = 0L;
                }
            }
            catch (Exception e3) {
                this.getResponder().getCommunicator().sendAlertServerMessage(time3 + " is not a number.");
            }
        }
        final String time4 = answers.getProperty("alt4");
        if (time4 == null || time4.length() == 0) {
            Server.timeBetweenAlertMess4 = Long.MAX_VALUE;
        }
        else {
            try {
                final long seconds4 = Long.parseLong(time3);
                if (seconds4 <= 0L) {
                    Server.timeBetweenAlertMess4 = Long.MAX_VALUE;
                    Server.lastAlertMess4 = Long.MAX_VALUE;
                }
                else {
                    Server.timeBetweenAlertMess4 = Math.max(10L, seconds4) * 1000L;
                    Server.lastAlertMess4 = 0L;
                }
            }
            catch (Exception e4) {
                this.getResponder().getCommunicator().sendAlertServerMessage(time4 + " is not a number.");
            }
        }
        final String mess1 = answers.getProperty("alm1");
        if (mess1 == null || mess1.length() == 0) {
            if (Server.alertMessage1.length() > 0) {
                this.getResponder().getCommunicator().sendSafeServerMessage("Reset message 1.");
            }
            Server.alertMessage1 = "";
            Server.timeBetweenAlertMess1 = Long.MAX_VALUE;
            Server.lastAlertMess1 = Long.MAX_VALUE;
        }
        else {
            final String msg1 = Server.alertMessage1 = mess1.replaceAll("\"", "");
            this.getResponder().getCommunicator().sendSafeServerMessage("Set message 1.");
        }
        final String mess2 = answers.getProperty("alm2");
        if (mess2 == null || mess2.length() == 0) {
            if (Server.alertMessage2.length() > 0) {
                this.getResponder().getCommunicator().sendSafeServerMessage("Reset message 2.");
            }
            Server.alertMessage2 = "";
            Server.timeBetweenAlertMess2 = Long.MAX_VALUE;
            Server.lastAlertMess2 = Long.MAX_VALUE;
        }
        else {
            final String msg2 = Server.alertMessage2 = mess2.replaceAll("\"", "");
            this.getResponder().getCommunicator().sendSafeServerMessage("Set message 2.");
        }
        final String mess3 = answers.getProperty("alm3");
        if (mess3 == null || mess3.length() == 0) {
            if (Server.alertMessage3.length() > 0) {
                this.getResponder().getCommunicator().sendSafeServerMessage("Reset global alert 3.");
            }
            Server.alertMessage3 = "";
            Server.timeBetweenAlertMess3 = Long.MAX_VALUE;
            Server.lastAlertMess3 = Long.MAX_VALUE;
        }
        else {
            final String msg3 = Server.alertMessage3 = mess3.replaceAll("\"", "");
            this.getResponder().getCommunicator().sendSafeServerMessage("Set message 3.");
        }
        final String mess4 = answers.getProperty("alm4");
        if (mess4 == null || mess4.length() == 0) {
            if (Server.alertMessage4.length() > 0) {
                this.getResponder().getCommunicator().sendSafeServerMessage("Reset global alert 4.");
            }
            Server.alertMessage4 = "";
            Server.timeBetweenAlertMess4 = Long.MAX_VALUE;
            Server.lastAlertMess4 = Long.MAX_VALUE;
        }
        else {
            final String msg4 = Server.alertMessage4 = mess4.replaceAll("\"", "");
            this.getResponder().getCommunicator().sendSafeServerMessage("Set message 4.");
        }
        final WcGlobalAlarmMessage wgam = new WcGlobalAlarmMessage(Server.alertMessage3, Server.timeBetweenAlertMess3, Server.alertMessage4, Server.timeBetweenAlertMess4);
        if (Servers.isThisLoginServer()) {
            wgam.sendFromLoginServer();
        }
        else {
            wgam.sendToLoginServer();
        }
    }
    
    @Override
    public void sendQuestion() {
        final StringBuilder sb = new StringBuilder();
        sb.append(this.getBmlHeader());
        sb.append("text{text='You may have 3 alert messages going, set at various intervals.'}");
        sb.append("text{text='If you omit the text or the time, or set time in seconds to 0 or less the message will not be displayed.'}");
        sb.append("text{text='The minimum number of seconds between alerts is 10.'}");
        sb.append("label{text='Alert message 1:'};input{id='alm1';text=\"" + Server.alertMessage1 + "\"}");
        sb.append("label{text='Seconds between polls:'};input{id='alt1';text='" + ((Server.timeBetweenAlertMess1 == Long.MAX_VALUE) ? 180L : (Server.timeBetweenAlertMess1 / 1000L)) + "'}");
        sb.append("label{text='Alert message 2:'};input{id='alm2';text=\"" + Server.alertMessage2 + "\"}");
        sb.append("label{text='Seconds between polls:'};input{id='alt2';text='" + ((Server.timeBetweenAlertMess2 == Long.MAX_VALUE) ? 180L : (Server.timeBetweenAlertMess2 / 1000L)) + "'}");
        sb.append("label{text=\"Global Alert message 3:\"};input{id='alm3';text=\"" + Server.alertMessage3 + "\"}");
        sb.append("label{text='Seconds between polls:'};input{id='alt3';text='" + ((Server.timeBetweenAlertMess3 == Long.MAX_VALUE) ? 180L : (Server.timeBetweenAlertMess3 / 1000L)) + "'}");
        sb.append("label{text=\"Global Alert message 4:\"};input{id='alm4';text=\"" + Server.alertMessage4 + "\"}");
        sb.append("label{text='Seconds between polls:'};input{id=\"alt4\";text='" + ((Server.timeBetweenAlertMess4 == Long.MAX_VALUE) ? 180L : (Server.timeBetweenAlertMess4 / 1000L)) + "'}");
        sb.append(this.createAnswerButton2());
        this.getResponder().getCommunicator().sendBml(300, 350, true, true, sb.toString(), 200, 200, 200, this.title);
    }
}
