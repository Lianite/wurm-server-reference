// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.skills;

import java.util.logging.Level;
import java.sql.PreparedStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import com.wurmonline.server.utils.DbUtilities;
import java.sql.SQLException;
import com.wurmonline.server.DbConnector;
import com.wurmonline.server.WurmId;
import java.io.IOException;
import java.util.logging.Logger;

class DbSkill extends Skill
{
    private static Logger logger;
    private static final String createCreatureSkill = "insert into SKILLS (VALUE, LASTUSED, MINVALUE, NUMBER, OWNER,ID) values(?,?,?,?,?,?)";
    private static final String updateCreatureSkill = "update SKILLS set VALUE=?, LASTUSED=?, MINVALUE=? where ID=?";
    private static final String updateNumber = "update SKILLS set NUMBER=? where ID=?";
    private static final String setJoat = "update SKILLS set JOAT=? where ID=?";
    private static final String getCreatureSkill = "select * from SKILLS where ID=?";
    private static final String createSkillChance = "insert into SKILLCHANCES (SKILL,DIFFICULTY,CHANCE) values(?,?,?)";
    private static final String loadSkillChance = "select * from SKILLCHANCES";
    
    DbSkill(final int aNumber, final double aStartValue, final Skills aParent) {
        super(aNumber, aStartValue, aParent);
    }
    
    DbSkill(final long aId, final Skills aParent) throws IOException {
        super(aId, aParent);
    }
    
    DbSkill(final long aId, final Skills aParent, final int aNumber, final double aKnowledge, final double aMinimum, final long aLastused) {
        super(aId, aParent, aNumber, aKnowledge, aMinimum, aLastused);
    }
    
    DbSkill(final long aId, final int aNumber, final double aKnowledge, final double aMinimum, final long aLastused) {
        super(aId, aNumber, aKnowledge, aMinimum, aLastused);
    }
    
    @Override
    void save() throws IOException {
        if (this.parent.isPersonal()) {
            Connection dbcon = null;
            PreparedStatement ps = null;
            final long wurmId = this.parent.getId();
            try {
                if (WurmId.getType(wurmId) == 1) {
                    dbcon = DbConnector.getCreatureDbCon();
                }
                else {
                    dbcon = DbConnector.getPlayerDbCon();
                }
                if (this.exists(dbcon, this.id)) {
                    ps = dbcon.prepareStatement("update SKILLS set VALUE=?, LASTUSED=?, MINVALUE=? where ID=?");
                    ps.setDouble(1, this.knowledge);
                    ps.setLong(2, this.lastUsed);
                    ps.setDouble(3, this.minimum);
                    ps.setLong(4, this.id);
                    ps.executeUpdate();
                }
                else {
                    ps = dbcon.prepareStatement("insert into SKILLS (VALUE, LASTUSED, MINVALUE, NUMBER, OWNER,ID) values(?,?,?,?,?,?)");
                    ps.setDouble(1, this.knowledge);
                    ps.setLong(2, this.lastUsed);
                    ps.setDouble(3, this.minimum);
                    ps.setInt(4, this.number);
                    ps.setLong(5, wurmId);
                    ps.setLong(6, this.id);
                    ps.executeUpdate();
                }
            }
            catch (SQLException e) {
                throw new IOException("Problem updating or creating Creature skills, ID: " + this.id + ", wurmID: " + wurmId, e);
            }
            finally {
                DbUtilities.closeDatabaseObjects(ps, null);
                DbConnector.returnConnection(dbcon);
            }
        }
    }
    
    @Override
    void saveValue(final boolean player) throws IOException {
        Connection dbcon = null;
        PreparedStatement ps = null;
        try {
            if (player) {
                dbcon = DbConnector.getPlayerDbCon();
            }
            else {
                dbcon = DbConnector.getCreatureDbCon();
            }
            ps = dbcon.prepareStatement("update SKILLS set VALUE=?, LASTUSED=?, MINVALUE=? where ID=?");
            ps.setDouble(1, this.knowledge);
            ps.setLong(2, this.lastUsed);
            ps.setDouble(3, this.minimum);
            ps.setLong(4, this.id);
            ps.executeUpdate();
        }
        catch (SQLException sql) {
            throw new IOException("Problem updating or creating Creature skills, ID: " + this.id, sql);
        }
        finally {
            DbUtilities.closeDatabaseObjects(ps, null);
            DbConnector.returnConnection(dbcon);
        }
    }
    
    @Override
    public void setJoat(final boolean _joat) throws IOException {
        if (_joat != this.joat) {
            this.joat = _joat;
            Connection dbcon = null;
            PreparedStatement ps = null;
            try {
                dbcon = DbConnector.getPlayerDbCon();
                ps = dbcon.prepareStatement("update SKILLS set JOAT=? where ID=?");
                ps.setBoolean(1, this.joat);
                ps.setLong(2, this.id);
                ps.executeUpdate();
            }
            catch (SQLException sql) {
                throw new IOException("Problem setting JOAT, ID: " + this.id, sql);
            }
            finally {
                DbUtilities.closeDatabaseObjects(ps, null);
                DbConnector.returnConnection(dbcon);
            }
        }
    }
    
    @Override
    public void setNumber(final int newNumber) throws IOException {
        this.number = newNumber;
        Connection dbcon = null;
        PreparedStatement ps = null;
        try {
            dbcon = DbConnector.getPlayerDbCon();
            ps = dbcon.prepareStatement("update SKILLS set NUMBER=? where ID=?");
            ps.setInt(1, this.number);
            ps.setLong(2, this.id);
            ps.executeUpdate();
        }
        catch (SQLException sql) {
            throw new IOException("Problem setting Number, ID: " + this.id, sql);
        }
        finally {
            DbUtilities.closeDatabaseObjects(ps, null);
            DbConnector.returnConnection(dbcon);
        }
    }
    
    @Override
    void load() throws IOException {
        Connection dbcon = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            if (this.parent.isPersonal()) {
                final long wurmId = this.parent.getId();
                if (WurmId.getType(wurmId) == 1) {
                    dbcon = DbConnector.getCreatureDbCon();
                }
                else {
                    dbcon = DbConnector.getPlayerDbCon();
                }
                ps = dbcon.prepareStatement("select * from SKILLS where ID=?");
                ps.setLong(1, this.id);
                rs = ps.executeQuery();
                if (rs.next()) {
                    this.number = rs.getInt("NUMBER");
                    this.knowledge = rs.getDouble("VALUE");
                    this.minimum = rs.getDouble("MINVALUE");
                    this.lastUsed = rs.getLong("LASTUSED");
                }
            }
        }
        catch (SQLException sqx) {
            throw new IOException("Problem updating or creating Creature/Player skills, ID: " + this.id, sqx);
        }
        finally {
            DbUtilities.closeDatabaseObjects(ps, rs);
            DbConnector.returnConnection(dbcon);
        }
    }
    
    private boolean exists(final Connection aDbcon, final long aId) throws SQLException {
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = aDbcon.prepareStatement("select * from SKILLS where ID=?");
            ps.setLong(1, aId);
            rs = ps.executeQuery();
            return rs.next();
        }
        catch (SQLException sqx) {
            if (DbSkill.logger.isLoggable(Level.FINE)) {
                DbSkill.logger.log(Level.FINE, "Problem checking if creature skill ID exists, ID: " + aId, sqx);
            }
            throw sqx;
        }
        finally {
            DbUtilities.closeDatabaseObjects(ps, rs);
        }
    }
    
    static byte[][] loadSkillChances() throws Exception {
        Connection dbcon = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        byte[][] toReturn = null;
        try {
            dbcon = DbConnector.getTemplateDbCon();
            ps = dbcon.prepareStatement("select * from SKILLCHANCES");
            rs = ps.executeQuery();
            while (rs.next()) {
                if (toReturn == null) {
                    toReturn = new byte[101][101];
                }
                final byte sk = rs.getByte("SKILL");
                final byte diff = rs.getByte("DIFFICULTY");
                final byte chance = rs.getByte("CHANCE");
                toReturn[sk][diff] = chance;
            }
        }
        catch (SQLException sqx) {
            if (DbSkill.logger.isLoggable(Level.FINE)) {
                DbSkill.logger.log(Level.FINE, "Problem loading skill chances", sqx);
            }
            throw sqx;
        }
        finally {
            DbUtilities.closeDatabaseObjects(ps, rs);
            DbConnector.returnConnection(dbcon);
        }
        return toReturn;
    }
    
    static void saveSkillChances(final byte[][] chances) throws Exception {
        Connection dbcon = null;
        PreparedStatement ps = null;
        try {
            dbcon = DbConnector.getTemplateDbCon();
            for (int x = 0; x < 101; ++x) {
                for (int y = 0; y < 101; ++y) {
                    ps = dbcon.prepareStatement("insert into SKILLCHANCES (SKILL,DIFFICULTY,CHANCE) values(?,?,?)");
                    ps.setByte(1, (byte)x);
                    ps.setByte(2, (byte)y);
                    ps.setByte(3, chances[x][y]);
                    ps.executeUpdate();
                }
            }
        }
        catch (SQLException sqx) {
            if (DbSkill.logger.isLoggable(Level.FINE)) {
                DbSkill.logger.log(Level.FINE, "Problem saving skill chances", sqx);
            }
            throw sqx;
        }
        finally {
            DbUtilities.closeDatabaseObjects(ps, null);
            DbConnector.returnConnection(dbcon);
        }
    }
    
    static {
        DbSkill.logger = Logger.getLogger(DbSkill.class.getName());
    }
}
