// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.batchjobs;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import com.wurmonline.server.utils.DbUtilities;
import java.sql.SQLException;
import java.util.logging.Level;
import com.wurmonline.server.DbConnector;
import java.util.logging.Logger;

public final class Skillbatchjob
{
    private static final String setSkillKnow = "UPDATE SKILLS SET VALUE=? WHERE ID=?";
    private static final String changeNumber = "UPDATE SKILLS SET NUMBER=10049 WHERE NUMBER=1004";
    private static final String getIdsForFarming = "SELECT OWNER FROM SKILLS WHERE NUMBER=10049";
    private static final String createCreatureSkill = "insert into SKILLS (VALUE, LASTUSED, MINVALUE, NUMBER, OWNER ) values(?,?,?,?,?)";
    private static final Logger logger;
    
    public static void runbatch() {
        PreparedStatement ps = null;
        Connection dbcon = null;
        try {
            dbcon = DbConnector.getPlayerDbCon();
            ps = dbcon.prepareStatement("UPDATE SKILLS SET NUMBER=10049 WHERE NUMBER=1004");
            ps.executeUpdate();
            ps.close();
            DbConnector.returnConnection(dbcon);
            dbcon = DbConnector.getCreatureDbCon();
            ps = dbcon.prepareStatement("UPDATE SKILLS SET NUMBER=10049 WHERE NUMBER=1004");
            ps.executeUpdate();
            ps.close();
        }
        catch (SQLException sqx) {
            Skillbatchjob.logger.log(Level.WARNING, sqx.getMessage(), sqx);
        }
        finally {
            DbUtilities.closeDatabaseObjects(ps, null);
            DbConnector.returnConnection(dbcon);
        }
        addNature();
    }
    
    public static void addNature() {
        PreparedStatement ps = null;
        ResultSet rs = null;
        Connection dbcon = null;
        try {
            dbcon = DbConnector.getPlayerDbCon();
            ps = dbcon.prepareStatement("SELECT OWNER FROM SKILLS WHERE NUMBER=10049");
            rs = ps.executeQuery();
            final long time = System.currentTimeMillis();
            while (rs.next()) {
                final long owner = rs.getLong("OWNER");
                final PreparedStatement ps2 = dbcon.prepareStatement("insert into SKILLS (VALUE, LASTUSED, MINVALUE, NUMBER, OWNER ) values(?,?,?,?,?)");
                ps2.setDouble(1, 1.0);
                ps2.setLong(2, time);
                ps2.setDouble(3, 1.0);
                ps2.setInt(4, 1019);
                ps2.setLong(5, owner);
                ps2.executeUpdate();
                ps2.close();
            }
            rs.close();
            ps.close();
            DbConnector.returnConnection(dbcon);
            dbcon = DbConnector.getCreatureDbCon();
            ps = dbcon.prepareStatement("SELECT OWNER FROM SKILLS WHERE NUMBER=10049");
            rs = ps.executeQuery();
            while (rs.next()) {
                final long owner = rs.getLong("OWNER");
                final PreparedStatement ps2 = dbcon.prepareStatement("insert into SKILLS (VALUE, LASTUSED, MINVALUE, NUMBER, OWNER ) values(?,?,?,?,?)");
                ps2.setDouble(1, 1.0);
                ps2.setLong(2, time);
                ps2.setDouble(3, 1.0);
                ps2.setInt(4, 1019);
                ps2.setLong(5, owner);
                ps2.executeUpdate();
                ps2.close();
            }
        }
        catch (SQLException sqx) {
            Skillbatchjob.logger.log(Level.WARNING, sqx.getMessage(), sqx);
        }
        finally {
            DbUtilities.closeDatabaseObjects(ps, rs);
            DbConnector.returnConnection(dbcon);
        }
    }
    
    public static final void fixPlayer(final long wurmid) {
    }
    
    public static final void modifySkillKnowledge(final int id, final int number, double knowledge) {
        if (number == 104 || number == 103 || number == 102 || number == 100 || number == 101 || number == 106 || number == 105) {
            PreparedStatement ps = null;
            Connection dbcon = null;
            try {
                knowledge += 10.0;
                dbcon = DbConnector.getPlayerDbCon();
                ps = dbcon.prepareStatement("UPDATE SKILLS SET VALUE=? WHERE ID=?");
                ps.setDouble(1, knowledge);
                ps.setInt(2, id);
                ps.executeUpdate();
                ps.close();
            }
            catch (SQLException sqx) {
                Skillbatchjob.logger.log(Level.WARNING, sqx.getMessage(), sqx);
            }
            finally {
                DbUtilities.closeDatabaseObjects(ps, null);
                DbConnector.returnConnection(dbcon);
            }
        }
    }
    
    static {
        logger = Logger.getLogger(Skillbatchjob.class.getName());
    }
}
