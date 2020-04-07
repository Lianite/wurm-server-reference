// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.questions;

import java.util.Iterator;
import com.wurmonline.server.players.AchievementTemplate;
import java.util.HashSet;
import com.wurmonline.server.players.Achievements;
import java.util.Properties;
import com.wurmonline.server.creatures.Creature;

public class PersonalGoalsListQuestion extends Question
{
    public PersonalGoalsListQuestion(final Creature aResponder, final long aTarget) {
        super(aResponder, "Personal Goals", "Personal Goals", 152, aTarget);
    }
    
    @Override
    public void answer(final Properties answers) {
    }
    
    @Override
    public void sendQuestion() {
        final StringBuilder buf = new StringBuilder();
        buf.append(this.getBmlHeader());
        if (this.getResponder().getPower() >= 4) {
            final Achievements achs = Achievements.getAchievementObject(this.getTarget());
            final HashSet<AchievementTemplate> goals = (HashSet<AchievementTemplate>)(HashSet)Achievements.getPersonalGoals(this.getTarget(), false);
            final HashSet<AchievementTemplate> oldGoals = (HashSet<AchievementTemplate>)(HashSet)Achievements.getOldPersonalGoals(this.getTarget());
            buf.append("text{text='Current Personal Goals for WurmId " + this.getTarget() + "'}");
            buf.append("text{text=''}");
            buf.append("table{rows='" + goals.size() + "';cols='2';");
            for (final AchievementTemplate t : goals) {
                boolean done = false;
                if (achs.getAchievement(t.getNumber()) != null) {
                    done = true;
                }
                buf.append("label{color=\"" + (done ? "20,255,20" : "200,200,200") + "\";text=\"" + t.getName() + "\"};");
                buf.append("label{color=\"" + (done ? "20,255,20" : "200,200,200") + "\";text=\"" + t.getRequirement() + "\"}");
            }
            buf.append("}");
            buf.append("text{text=''}");
            buf.append("text{text='Pre June 5 2018 Personal Goals for WurmId " + this.getTarget() + "'}");
            buf.append("text{text=''}");
            buf.append("table{rows='" + oldGoals.size() + "';cols='2';");
            for (final AchievementTemplate t : oldGoals) {
                buf.append("label{text=\"" + t.getName() + "\"};");
                buf.append("label{text=\"" + t.getRequirement() + "\"}");
            }
            buf.append("}");
            buf.append("}};null;null;}");
            this.getResponder().getCommunicator().sendBml(300, 600, true, true, buf.toString(), 200, 200, 200, this.title);
        }
    }
}
