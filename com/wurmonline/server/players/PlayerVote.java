// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.players;

import com.wurmonline.server.support.VoteQuestion;
import java.sql.PreparedStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import com.wurmonline.server.utils.DbUtilities;
import java.sql.SQLException;
import java.util.logging.Level;
import com.wurmonline.server.DbConnector;
import com.wurmonline.server.Servers;
import java.util.logging.Logger;

public class PlayerVote
{
    private static final Logger logger;
    private static final String ADDVOTE = "INSERT INTO VOTES (OPTION1,OPTION2,OPTION3,OPTION4,PLAYERID,QUESTIONID) VALUES(?,?,?,?,?,?)";
    private static final String UPDATEVOTE = "UPDATE VOTES SET OPTION1=?,OPTION2=?,OPTION3=?,OPTION4=? WHERE PLAYERID=? AND QUESTIONID=?";
    private final long playerId;
    private final int questionId;
    private boolean option1;
    private boolean option2;
    private boolean option3;
    private boolean option4;
    
    public PlayerVote(final long aPlayerId, final int aQuestionId) {
        this(aPlayerId, aQuestionId, false, false, false, false);
    }
    
    public PlayerVote(final long aPlayerId, final int aQuestionId, final boolean aOption1, final boolean aOption2, final boolean aOption3, final boolean aOption4) {
        this.playerId = aPlayerId;
        this.questionId = aQuestionId;
        this.option1 = aOption1;
        this.option2 = aOption2;
        this.option3 = aOption3;
        this.option4 = aOption4;
    }
    
    public void update(final boolean aOption1, final boolean aOption2, final boolean aOption3, final boolean aOption4) {
        this.option1 = aOption1;
        this.option2 = aOption2;
        this.option3 = aOption3;
        this.option4 = aOption4;
        if (!Servers.isThisLoginServer()) {
            return;
        }
        Connection dbcon = null;
        PreparedStatement ps = null;
        try {
            dbcon = DbConnector.getPlayerDbCon();
            ps = dbcon.prepareStatement("UPDATE VOTES SET OPTION1=?,OPTION2=?,OPTION3=?,OPTION4=? WHERE PLAYERID=? AND QUESTIONID=?");
            ps.setBoolean(1, this.option1);
            ps.setBoolean(2, this.option2);
            ps.setBoolean(3, this.option3);
            ps.setBoolean(4, this.option4);
            ps.setLong(5, this.playerId);
            ps.setInt(6, this.questionId);
            ps.executeUpdate();
        }
        catch (SQLException sqx) {
            PlayerVote.logger.log(Level.WARNING, sqx.getMessage(), sqx);
        }
        finally {
            DbUtilities.closeDatabaseObjects(ps, null);
            DbConnector.returnConnection(dbcon);
        }
    }
    
    public void save() {
        if (!Servers.isThisLoginServer()) {
            return;
        }
        Connection dbcon = null;
        PreparedStatement ps = null;
        try {
            dbcon = DbConnector.getPlayerDbCon();
            ps = dbcon.prepareStatement("INSERT INTO VOTES (OPTION1,OPTION2,OPTION3,OPTION4,PLAYERID,QUESTIONID) VALUES(?,?,?,?,?,?)");
            ps.setBoolean(1, this.option1);
            ps.setBoolean(2, this.option2);
            ps.setBoolean(3, this.option3);
            ps.setBoolean(4, this.option4);
            ps.setLong(5, this.playerId);
            ps.setInt(6, this.questionId);
            ps.executeUpdate();
        }
        catch (SQLException sqx) {
            PlayerVote.logger.log(Level.WARNING, sqx.getMessage(), sqx);
        }
        finally {
            DbUtilities.closeDatabaseObjects(ps, null);
            DbConnector.returnConnection(dbcon);
        }
    }
    
    public long getPlayerId() {
        return this.playerId;
    }
    
    public int getQuestionId() {
        return this.questionId;
    }
    
    public boolean getOption1() {
        return this.option1;
    }
    
    public boolean getOption2() {
        return this.option2;
    }
    
    public boolean getOption3() {
        return this.option3;
    }
    
    public boolean getOption4() {
        return this.option4;
    }
    
    public boolean hasVoted() {
        return this.option1 || this.option2 || this.option3 || this.option4;
    }
    
    static {
        logger = Logger.getLogger(VoteQuestion.class.getName());
    }
}
