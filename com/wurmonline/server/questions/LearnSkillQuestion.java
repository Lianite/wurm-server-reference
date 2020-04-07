// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.questions;

import com.wurmonline.server.skills.Skill;
import com.wurmonline.server.skills.Skills;
import java.util.Collection;
import com.wurmonline.server.players.Cultist;
import com.wurmonline.server.Servers;
import com.wurmonline.server.creatures.NoSuchCreatureException;
import com.wurmonline.server.NoSuchPlayerException;
import com.wurmonline.server.skills.NoSuchSkillException;
import com.wurmonline.server.Server;
import com.wurmonline.server.WurmId;
import java.util.Arrays;
import com.wurmonline.server.skills.SkillTemplate;
import com.wurmonline.server.skills.SkillSystem;
import java.util.Properties;
import com.wurmonline.server.creatures.Creature;

public final class LearnSkillQuestion extends Question
{
    public LearnSkillQuestion(final Creature aResponder, final String aTitle, final String aQuestion, final long aTarget) {
        super(aResponder, aTitle, aQuestion, 16, aTarget);
    }
    
    @Override
    public void answer(final Properties answers) {
        this.setAnswer(answers);
        QuestionParser.parseLearnSkillQuestion(this);
    }
    
    @Override
    public void sendQuestion() {
        final StringBuilder buf = new StringBuilder(this.getBmlHeader());
        buf.append("harray{label{text='Skill'}dropdown{id='data1';options='");
        final Collection<SkillTemplate> temps = SkillSystem.templates.values();
        final SkillTemplate[] templates = temps.toArray(new SkillTemplate[temps.size()]);
        Arrays.sort(templates);
        Creature receiver = null;
        boolean hadError = false;
        try {
            if (WurmId.getType(this.target) == 1 || WurmId.getType(this.target) == 0) {
                receiver = Server.getInstance().getCreature(this.target);
            }
            else {
                receiver = this.getResponder();
            }
            final Skills skills = receiver.getSkills();
            for (int x = 0; x < templates.length; ++x) {
                if (x > 0) {
                    buf.append(",");
                }
                final int sk = templates[x].getNumber();
                try {
                    final Skill skill = skills.getSkill(sk);
                    final String affs = "*****".substring(0, skill.affinity);
                    buf.append(templates[x].getName() + " " + affs + " (" + skill.getKnowledge() + ")");
                }
                catch (NoSuchSkillException e) {
                    buf.append(templates[x].getName());
                }
            }
        }
        catch (NoSuchPlayerException e2) {
            hadError = true;
        }
        catch (NoSuchCreatureException e3) {
            hadError = true;
        }
        if (hadError) {
            for (int x2 = 0; x2 < templates.length; ++x2) {
                if (x2 > 0) {
                    buf.append(",");
                }
                buf.append(templates[x2].getName());
            }
        }
        buf.append("'}}");
        buf.append("label{type=\"bolditalic\";text=\"Skill of 0 = no change\"}");
        buf.append("harray{label{text=\"Skill level\"}input{maxchars=\"3\"; id=\"val\"; text=\"0\"}label{text=\".\"}input{maxchars=\"6\"; id=\"dec\"; text=\"000000\"}}");
        buf.append("harray{label{text=\"Affinities\"}radio{group=\"aff\";id=\"-1\";text=\"Leave as is\";selected=\"true\"};radio{group=\"aff\";id=\"0\";text=\"None\"};radio{group=\"aff\";id=\"1\";text=\"One\"};radio{group=\"aff\";id=\"2\";text=\"Two\"};radio{group=\"aff\";id=\"3\";text=\"Three\"};radio{group=\"aff\";id=\"4\";text=\"Four\"};radio{group=\"aff\";id=\"5\";text=\"Five\"}}");
        final float align = this.getResponder().getAlignment();
        buf.append("text{text=\"\"}");
        buf.append("label{type=\"bolditalic\";text=\"Alignment, leave blank for no change\"}");
        buf.append("harray{label{text=\"Alignment (" + align + ")\"}input{maxchars=\"4\"; id=\"align\"; text=\"\"}}");
        final int karma = this.getResponder().getKarma();
        buf.append("label{type=\"bolditalic\";text=\"Karma, leave blank for no change\"}");
        buf.append("harray{label{text=\"Karma (" + karma + ")\"}input{maxchars=\"5\"; id=\"karma\"; text=\"\"}}");
        int height = 270;
        if (WurmId.getType(this.target) == 0 && Servers.isThisATestServer() && (this.getResponder().getPower() == 5 || this.getResponder().getName().equals("Hestia"))) {
            height += 70;
            buf.append("label{text=\"----- Cultist --- Test Server Only -----\"}");
            final Cultist cultist = Cultist.getCultist(this.target);
            byte path = 0;
            byte level = 0;
            if (cultist != null) {
                path = cultist.getPath();
                level = cultist.getLevel();
            }
            final String pathName = this.getShortPathName(path);
            buf.append("harray{label{text=\"Path (" + pathName + ")\"}dropdown{id=\"path\";options=\"none,Love,Hate,Knowledge,Insanity,Power\";default=\"" + path + "\"}}");
            buf.append("harray{label{text=\"Level (" + level + ") leave blank for no change\"}input{maxchars=\"2\"; id=\"level\"; text=\"\"}}");
        }
        buf.append(this.createAnswerButton2());
        this.getResponder().getCommunicator().sendBml(360, height, true, true, buf.toString(), 200, 200, 200, this.title);
    }
    
    private String getShortPathName(final byte path) {
        switch (path) {
            case 2: {
                return "Hate";
            }
            case 1: {
                return "Love";
            }
            case 4: {
                return "Insanity";
            }
            case 3: {
                return "Knowledge";
            }
            case 5: {
                return "Power";
            }
            default: {
                return "none";
            }
        }
    }
}
