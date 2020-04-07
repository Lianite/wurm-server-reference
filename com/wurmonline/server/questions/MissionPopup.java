// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.questions;

import java.util.Properties;
import com.wurmonline.server.creatures.Creature;

public class MissionPopup extends Question
{
    private String top;
    private String toSend;
    private MissionManager root;
    
    public MissionPopup(final Creature _responder, final String _title, final String _question) {
        super(_responder, _title, _question, 92, -10L);
        this.top = "";
        this.toSend = "";
        this.root = null;
        this.windowSizeX = 300;
        this.windowSizeY = 300;
    }
    
    @Override
    public void answer(final Properties aAnswers) {
        if (this.root != null) {
            this.root.cloneAndSendManageEffect(null);
        }
    }
    
    @Override
    public void sendQuestion() {
        final StringBuilder buf = new StringBuilder();
        if (this.top.length() == 0) {
            buf.append("border{null;");
        }
        else if (this.top.contains("{") && this.toSend.contains("}")) {
            buf.append("border{" + this.top);
            if (!this.top.endsWith(";")) {
                buf.append(";");
            }
        }
        else {
            buf.append("border{center{text{type='bold';text=\"" + this.top + "\"}};");
        }
        buf.append("null;scroll{vertical='true';horizontal='true';varray{rescale='false';passthrough{id='id';text='" + this.getId() + "'}");
        buf.append("closebutton{id=\"submit\"};");
        buf.append("text{text=\"\"}");
        if (this.toSend.length() > 0) {
            if (this.toSend.contains("{") && this.toSend.contains("}")) {
                buf.append(this.toSend);
            }
            else {
                buf.append("text{size=\"" + this.windowSizeX + ",10\";text=\"" + this.toSend + "\"}");
            }
            buf.append("text{text=\"\"}");
        }
        buf.append("harray{button{text='Ok';id='submit'}}}};null;null;}");
        this.getResponder().getCommunicator().sendBml(this.windowSizeX, this.windowSizeY, true, true, buf.toString(), 200, 200, 200, this.title);
    }
    
    void setRoot(final MissionManager aRoot) {
        this.root = aRoot;
    }
    
    public String getToSend() {
        return this.toSend;
    }
    
    public void setToSend(final String aToSend) {
        this.toSend = aToSend;
    }
    
    public String getTop() {
        return this.top;
    }
    
    public void setTop(final String topString) {
        this.top = topString;
    }
}
