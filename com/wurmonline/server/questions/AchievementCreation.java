// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.questions;

import java.util.Iterator;
import com.wurmonline.server.players.Achievement;
import com.wurmonline.server.items.Item;
import com.wurmonline.server.NoSuchItemException;
import com.wurmonline.server.Items;
import java.util.Properties;
import com.wurmonline.server.creatures.Creature;
import com.wurmonline.server.players.AchievementTemplate;
import java.util.LinkedList;
import java.util.logging.Logger;

public class AchievementCreation extends Question
{
    private static final Logger logger;
    private LinkedList<AchievementTemplate> myAchievements;
    private AchievementTemplate toWorkOn;
    
    public AchievementCreation(final Creature aResponder, final String aTitle, final String aQuestion, final long aTarget) {
        super(aResponder, aTitle, aQuestion, 99, aResponder.getWurmId());
        this.myAchievements = null;
        this.toWorkOn = null;
    }
    
    @Override
    public void answer(final Properties answers) {
        final String edita = answers.getProperty("edita");
        boolean changing = this.toWorkOn != null;
        if (this.toWorkOn == null) {
            try {
                if (edita != null && edita.length() > 0 && this.myAchievements != null) {
                    final int index = Integer.parseInt(edita);
                    if (index < this.myAchievements.size()) {
                        this.toWorkOn = this.myAchievements.get(index);
                        final AchievementCreation m = new AchievementCreation(this.getResponder(), "Edit Achievement", "Achievement management", this.getTarget());
                        m.myAchievements = this.myAchievements;
                        m.toWorkOn = this.toWorkOn;
                        m.sendQuestion();
                        return;
                    }
                    changing = false;
                }
            }
            catch (NumberFormatException nfe) {
                this.getResponder().getCommunicator().sendNormalServerMessage("The values were incorrect.");
            }
        }
        final String name = answers.getProperty("newName");
        final String description = answers.getProperty("newDesc");
        final String triggerOn = answers.getProperty("newTriggeron");
        int triggerInt = 1;
        try {
            if (triggerOn != null && triggerOn.length() > 0) {
                triggerInt = Integer.parseInt(triggerOn);
            }
        }
        catch (NumberFormatException nfe2) {
            this.getResponder().getCommunicator().sendNormalServerMessage("The value for trigger on was incorrect.");
        }
        if (this.toWorkOn == null) {
            if (name == null || name.length() < 2) {
                this.getResponder().getCommunicator().sendAlertServerMessage("The name " + name + " needs at least 2 characters.");
                return;
            }
            if (description == null || description.length() < 10) {
                this.getResponder().getCommunicator().sendAlertServerMessage("The description " + description + " needs at least 10 characters.");
                return;
            }
            String creator = this.getResponder().getName();
            if (this.getResponder().getPower() > 0) {
                creator = "GM " + creator;
            }
            else {
                try {
                    final Item ruler = Items.getItem(this.getTarget());
                    if (ruler.getAuxData() < 10) {
                        this.getResponder().getCommunicator().sendAlertServerMessage("The " + ruler.getName() + " needs at least 10 charges in order to create an Achievement. It currently contains " + ruler.getAuxData() + " charges.");
                        return;
                    }
                    ruler.setAuxData((byte)(ruler.getAuxData() - 10));
                }
                catch (NoSuchItemException nsi) {
                    this.getResponder().getCommunicator().sendAlertServerMessage("Failed to locate the item for that request.");
                    return;
                }
            }
            this.toWorkOn = new AchievementTemplate(AchievementTemplate.getNextAchievementId(), name, false, triggerInt, description, creator, false, false);
            this.getResponder().getCommunicator().sendSafeServerMessage("You successfully create the Achievement " + this.toWorkOn.getName() + ".");
        }
        else {
            final String delete = answers.getProperty("deletecb");
            if (delete != null && delete.equals("true") && changing && this.toWorkOn != null) {
                this.toWorkOn.delete();
                this.getResponder().getCommunicator().sendSafeServerMessage("You successfully delete the Achievement " + this.toWorkOn.getName() + ".");
                return;
            }
            changing = false;
            if (name != null && name.length() > 0 && !name.equals(this.toWorkOn.getName())) {
                changing = true;
                this.toWorkOn.setName(name);
            }
            if (description != null && description.length() > 0 && !description.equals(this.toWorkOn.getDescription())) {
                changing = true;
                this.toWorkOn.setDescription(description);
            }
            if (triggerInt != this.toWorkOn.getTriggerOnCounter()) {
                changing = true;
                this.toWorkOn.setTriggerOnCounter(triggerInt);
            }
            if (changing) {
                this.getResponder().getCommunicator().sendSafeServerMessage("You successfully update the Achievement " + this.toWorkOn.getName() + ".");
            }
            else {
                this.getResponder().getCommunicator().sendSafeServerMessage("You change nothing on the Achievement " + this.toWorkOn.getName() + ".");
            }
        }
    }
    
    @Override
    public void sendQuestion() {
        final StringBuilder buf = new StringBuilder();
        buf.append(this.getBmlHeader());
        buf.append("text{text=\"\"}");
        if (this.toWorkOn == null) {
            this.myAchievements = Achievement.getSteelAchievements(this.getResponder());
            if (this.myAchievements != null && this.myAchievements.size() > 0) {
                buf.append("text{text=\"Select if you wish to edit an existing Achievement:\"};");
                buf.append("harray{label{text='Existing achievements'}dropdown{id='edita';options=\"");
                for (final AchievementTemplate template : this.myAchievements) {
                    buf.append(template.getName() + " (" + template.getCreator() + "),");
                }
                buf.append("None");
                buf.append("\"}}");
                buf.append("text{text=\"\"}");
            }
        }
        String oldName = "";
        if (this.toWorkOn != null) {
            oldName = this.toWorkOn.getName();
            buf.append("checkbox{id='deletecb';selected='false';text='Delete the selected achievement'}");
        }
        buf.append("text{text=\"What name should the Achievement have?\"};");
        buf.append("harray{input{maxchars='40';id='newName';text=\"" + oldName + "\"};label{text=\" Name\"}}");
        String oldDesc = "";
        if (this.toWorkOn != null) {
            oldDesc = this.toWorkOn.getDescription();
        }
        buf.append("text{text=\"What description should the Achievement show?\"};");
        buf.append("harray{input{maxchars='200';id='newDesc';text=\"" + oldDesc + "\"};label{text=\" Description\"}}");
        buf.append("text{text=\"\"}");
        int oldTriggerOn = 1;
        if (this.toWorkOn != null) {
            oldTriggerOn = this.toWorkOn.getTriggerOnCounter();
        }
        buf.append("text{text=\"On which count should the Achievement trigger? Usually the trigger should be 1.\"};");
        buf.append("harray{input{maxchars='5';id='newTriggeron';text=\"" + oldTriggerOn + "\"};label{text=\" Triggered effects\"}}");
        buf.append(this.createAnswerButton2());
        this.getResponder().getCommunicator().sendBml(500, 400, true, true, buf.toString(), 200, 200, 200, this.title);
    }
    
    static {
        logger = Logger.getLogger(CreateZoneQuestion.class.getName());
    }
}
