// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.questions;

import java.sql.ResultSet;
import java.sql.PreparedStatement;
import java.sql.Connection;
import java.sql.Statement;
import com.wurmonline.server.utils.DbUtilities;
import java.sql.SQLException;
import com.wurmonline.server.Servers;
import com.wurmonline.server.DbConnector;
import java.util.Collection;
import com.wurmonline.server.players.PlayerInfo;
import java.util.Arrays;
import com.wurmonline.server.skills.SkillTemplate;
import com.wurmonline.server.skills.SkillSystem;
import com.wurmonline.server.players.PlayerInfoFactory;
import com.wurmonline.server.LoginHandler;
import java.util.logging.Level;
import java.util.Properties;
import com.wurmonline.server.creatures.Creature;
import java.util.logging.Logger;

public class SkillProgressQuestion extends Question
{
    private static final String progressDBString = "SELECT * FROM SKILLS WHERE OWNER=? AND NUMBER=?";
    private static final Logger logger;
    boolean answering;
    String name;
    int skill;
    
    public SkillProgressQuestion(final Creature aResponder, final long wurmId, final String _name) {
        super(aResponder, "Skill progress check", "Progress for " + _name + ":", 124, wurmId);
        this.answering = false;
        this.name = "";
        this.skill = 0;
        this.name = _name;
        this.answering = true;
    }
    
    public SkillProgressQuestion(final Creature aResponder) {
        super(aResponder, "Skill progress check", "Select a player and skill to check latest progress", 124, -10L);
        this.answering = false;
        this.name = "";
        this.skill = 0;
    }
    
    @Override
    public void answer(final Properties answers) {
        if (this.getResponder().getPower() > 1) {
            String player = answers.getProperty("data1");
            if (player != null && player.length() > 0) {
                this.getResponder().getLogger().log(Level.INFO, this.getResponder().getName() + " checking " + player + " for skill progress.");
                SkillProgressQuestion.logger.log(Level.INFO, this.getResponder().getName() + " checking " + player + " for skill progress.");
                player = LoginHandler.raiseFirstLetter(player);
                final PlayerInfo pinf = PlayerInfoFactory.getPlayerInfoWithName(player);
                if (pinf == null) {
                    this.getResponder().getCommunicator().sendAlertServerMessage("No player found with name " + player + ".");
                    return;
                }
                final String sknums = answers.getProperty("data2");
                if (sknums != null && sknums.length() > 0) {
                    try {
                        final int sknum = Integer.parseInt(sknums);
                        final Collection<SkillTemplate> temps = SkillSystem.templates.values();
                        final SkillTemplate[] templates = temps.toArray(new SkillTemplate[temps.size()]);
                        Arrays.sort(templates);
                        final int sk = templates[sknum].getNumber();
                        final SkillProgressQuestion newq = new SkillProgressQuestion(this.getResponder(), pinf.wurmId, pinf.getName());
                        newq.skill = sk;
                        newq.sendQuestion();
                        return;
                    }
                    catch (Exception ex) {
                        this.getResponder().getCommunicator().sendAlertServerMessage("No skill found in array at " + sknums + ".");
                        return;
                    }
                }
                this.getResponder().getCommunicator().sendAlertServerMessage("No skill found in array.");
            }
        }
    }
    
    @Override
    public void sendQuestion() {
        final StringBuilder buf = new StringBuilder();
        buf.append(this.getBmlHeader());
        if (!this.answering) {
            buf.append("harray{label{text='Player name'};input{maxchars='40';id='data1'; text=''}}");
            buf.append("harray{label{text='Skill to check'}dropdown{id='data2';options='");
            final Collection<SkillTemplate> temps = SkillSystem.templates.values();
            final SkillTemplate[] templates = temps.toArray(new SkillTemplate[temps.size()]);
            Arrays.sort(templates);
            for (int x = 0; x < templates.length; ++x) {
                if (x > 0) {
                    buf.append(",");
                }
                buf.append(templates[x].getName());
            }
            buf.append("'}}");
        }
        else {
            Connection dbcon = null;
            PreparedStatement ps = null;
            ResultSet rs = null;
            try {
                dbcon = DbConnector.getPlayerDbCon();
                ps = dbcon.prepareStatement("SELECT * FROM SKILLS WHERE OWNER=? AND NUMBER=?");
                ps.setLong(1, this.getTarget());
                ps.setInt(2, this.skill);
                rs = ps.executeQuery();
                while (rs.next()) {
                    final String skname = SkillSystem.getNameFor(this.skill);
                    buf.append("harray{label{type=\"bolditalic\";text=\"" + skname + "\"}}");
                    final PlayerInfo pinf = PlayerInfoFactory.getPlayerInfoWithWurmId(this.getTarget());
                    if (pinf != null && pinf.currentServer != Servers.localServer.id) {
                        buf.append("text=\"" + pinf.getName() + " does not seem to currently be on this server!\"}");
                    }
                    buf.append("harray{label{text='Current value:'}label{text=\"" + String.valueOf(rs.getFloat("VALUE")) + "\"}}");
                    buf.append("harray{label{text='1 day ago:'};label{text=\"" + String.valueOf(rs.getFloat("DAY1")) + "\"}}");
                    buf.append("harray{label{text='2 days ago:'};label{text=\"" + String.valueOf(rs.getFloat("DAY2")) + "\"}}");
                    buf.append("harray{label{text='3 days ago:'};label{text=\"" + String.valueOf(rs.getFloat("DAY3")) + "\"}}");
                    buf.append("harray{label{text='4 days ago:'};label{text=\"" + String.valueOf(rs.getFloat("DAY4")) + "\"}}");
                    buf.append("harray{label{text='5 days ago:'};label{text=\"" + String.valueOf(rs.getFloat("DAY5")) + "\"}}");
                    buf.append("harray{label{text='6 days ago:'};label{text=\"" + String.valueOf(rs.getFloat("DAY6")) + "\"}}");
                    buf.append("harray{label{text='7 days ago:'};label{text=\"" + String.valueOf(rs.getFloat("DAY7")) + "\"}}");
                    buf.append("harray{label{text='2 weeks ago:'};label{text=\"" + String.valueOf(rs.getFloat("WEEK2")) + "\"}}");
                    buf.append("text{text=\"\"}");
                    buf.append("text{type=\"bolditalic\";text=\"Note that a 0 value usually means no change for the period or that the player was inactive.\"}");
                }
            }
            catch (SQLException ex) {
                SkillProgressQuestion.logger.log(Level.WARNING, "Failed to show skill " + this.skill + " for " + this.name + " " + ex.getMessage(), new Exception());
                this.getResponder().getCommunicator().sendAlertServerMessage("Error when checking skill.");
                return;
            }
            finally {
                DbUtilities.closeDatabaseObjects(ps, rs);
                DbConnector.returnConnection(dbcon);
            }
        }
        buf.append(this.createAnswerButton2());
        this.getResponder().getCommunicator().sendBml(300, 300, true, true, buf.toString(), 200, 200, 200, this.title);
    }
    
    static {
        logger = Logger.getLogger(SkillProgressQuestion.class.getName());
    }
}
