// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.questions;

import com.wurmonline.server.Servers;
import java.util.Properties;
import com.wurmonline.server.creatures.Creature;

public final class ShutDownQuestion extends Question
{
    public ShutDownQuestion(final Creature aResponder, final String aTitle, final String aQuestion, final long aTarget) {
        super(aResponder, aTitle, aQuestion, 13, aTarget);
    }
    
    @Override
    public void answer(final Properties answers) {
        this.setAnswer(answers);
        QuestionParser.parseShutdownQuestion(this);
    }
    
    @Override
    public void sendQuestion() {
        final StringBuilder buf = new StringBuilder(this.getBmlHeader());
        if (Servers.localServer.testServer) {
            buf.append("harray{input{maxchars=\"2\";id=\"minutes\";text=\"0\"};label{text=\"minutes and\"}input{maxchars=\"2\";id=\"seconds\";text=\"30\"};label{text=\"seconds to shutdown\"}}");
            buf.append("label{text=\"Reason\"};");
            buf.append("input{id=\"reason\";text=\"Quick restart. Debugging.\"};");
        }
        else {
            buf.append("harray{input{maxchars=\"2\";id=\"minutes\";text=\"20\"};label{text=\"minutes and\"}input{maxchars=\"2\";id=\"seconds\";text=\"00\"};label{text=\"seconds to shutdown\"}}");
            buf.append("label{text=\"Reason\"};");
            buf.append("input{id=\"reason\";text=\"Maintenance restart. Up to thirty minutes downtime.\"};");
        }
        buf.append("checkbox{id=\"global\";text=\"Global\"};");
        String serverType = "local";
        if (Servers.isThisATestServer()) {
            if (Servers.isThisLoginServer()) {
                serverType = "test login";
            }
            else {
                serverType = "test";
            }
        }
        else if (Servers.isThisLoginServer()) {
            serverType = "live login";
        }
        else {
            serverType = "live";
        }
        buf.append("text{text=\"You are currently on a " + serverType + " server (" + Servers.getLocalServerName() + ").\"};");
        buf.append("text{type=\"bold\";text=\"----- help -----\"};");
        buf.append("text{text=\"If using global from any server other than login, then it just tell the login server to shutdown all servers.\"};");
        buf.append("text{text=\"If using global from login server, it tells all servers to shutdown.\"};");
        buf.append("text{text=\"In both cases the login server would be the last to start shutting down.\"};");
        buf.append(this.createAnswerButton2());
        this.getResponder().getCommunicator().sendBml(365, 320, true, true, buf.toString(), 200, 200, 200, this.title);
    }
}
