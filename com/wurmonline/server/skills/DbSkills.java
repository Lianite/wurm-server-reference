// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.skills;

import java.sql.SQLException;
import java.sql.ResultSet;
import java.sql.PreparedStatement;
import java.sql.Connection;
import java.sql.Statement;
import com.wurmonline.server.utils.DbUtilities;
import com.wurmonline.server.DbConnector;
import java.util.TreeMap;
import com.wurmonline.server.players.PlayerInfo;
import com.wurmonline.server.Servers;
import com.wurmonline.server.players.PlayerInfoFactory;
import com.wurmonline.server.WurmId;
import java.util.logging.Logger;
import com.wurmonline.shared.constants.CounterTypes;
import com.wurmonline.server.MiscConstants;

public class DbSkills extends Skills implements MiscConstants, CounterTypes
{
    private static Logger logger;
    private static final String loadPlayerSkills2 = "select * FROM SKILLS where OWNER=?";
    private static final String loadCreatureSkills2 = "select * FROM SKILLS where OWNER=?";
    private static final String deleteCreatureSkills = "delete from SKILLS where OWNER=?";
    
    DbSkills(final long aId) {
        this.id = aId;
        if (aId != -10L && WurmId.getType(aId) == 0) {
            final PlayerInfo p = PlayerInfoFactory.getPlayerInfoWithWurmId(aId);
            if (p != null) {
                if (!p.isPaying()) {
                    this.paying = false;
                }
                if (!p.hasSkillGain) {
                    this.hasSkillGain = false;
                }
                if (Servers.localServer.isChallengeOrEpicServer() && p.realdeath == 0) {
                    this.priest = p.isPriest;
                }
            }
        }
    }
    
    DbSkills(final String aTemplateName) {
        this.templateName = aTemplateName;
    }
    
    @Override
    public void load() throws Exception {
        if (this.id != -10L) {
            this.skills = new TreeMap<Integer, Skill>();
            Connection dbcon = null;
            PreparedStatement ps = null;
            ResultSet rs = null;
            try {
                if (WurmId.getType(this.id) == 0) {
                    dbcon = DbConnector.getPlayerDbCon();
                    ps = dbcon.prepareStatement("select * FROM SKILLS where OWNER=?");
                    ps.setLong(1, this.id);
                    rs = ps.executeQuery();
                    while (rs.next()) {
                        final DbSkill skill = new DbSkill(rs.getLong("ID"), this, rs.getInt("NUMBER"), rs.getDouble("VALUE"), rs.getDouble("MINVALUE"), rs.getLong("LASTUSED"));
                        if (!this.skills.containsKey(skill.getNumber()) || skill.getMinimumValue() > this.skills.get(skill.getNumber()).getMinimumValue()) {
                            this.skills.put(skill.getNumber(), skill);
                        }
                    }
                }
                else {
                    dbcon = DbConnector.getCreatureDbCon();
                    ps = dbcon.prepareStatement("select * FROM SKILLS where OWNER=?");
                    ps.setLong(1, this.id);
                    rs = ps.executeQuery();
                    while (rs.next()) {
                        final DbSkill skill = new DbSkill(rs.getLong("ID"), this, rs.getInt("NUMBER"), rs.getDouble("VALUE"), rs.getDouble("MINVALUE"), rs.getLong("LASTUSED"));
                        this.skills.put(skill.getNumber(), skill);
                    }
                }
                this.addTempSkills();
            }
            finally {
                DbUtilities.closeDatabaseObjects(ps, rs);
                DbConnector.returnConnection(dbcon);
            }
        }
        else if (this.templateName != null) {
            return;
        }
    }
    
    @Override
    public void delete() throws SQLException {
        Connection dbcon = null;
        PreparedStatement ps = null;
        try {
            if (this.id != -10L) {
                if (WurmId.getType(this.id) == 0) {
                    dbcon = DbConnector.getPlayerDbCon();
                }
                else {
                    if (WurmId.getType(this.id) != 1) {
                        DbSkills.logger.warning("Unexpected Counter Type: " + WurmId.getType(this.id) + " for WurmID: " + this.id);
                        return;
                    }
                    dbcon = DbConnector.getCreatureDbCon();
                }
                ps = dbcon.prepareStatement("delete from SKILLS where OWNER=?");
                ps.setLong(1, this.id);
                ps.executeUpdate();
            }
        }
        finally {
            DbUtilities.closeDatabaseObjects(ps, null);
            DbConnector.returnConnection(dbcon);
        }
    }
    
    static {
        DbSkills.logger = Logger.getLogger(DbSkills.class.getName());
    }
}
