// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.players;

import java.util.concurrent.ConcurrentHashMap;
import java.sql.PreparedStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import com.wurmonline.server.utils.DbUtilities;
import java.sql.SQLException;
import com.wurmonline.server.DbConnector;
import com.wurmonline.server.Players;
import java.util.Iterator;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.Map;
import java.util.logging.Logger;

public class PlayerVotes
{
    private static Logger logger;
    private static final Map<Long, PlayerVotesByPlayer> playerVotes;
    private static final String LOADALLPLAYERVOTES = "SELECT * FROM VOTES";
    private static final String DELETEQUESTIONVOTES = "DELETE FROM VOTES WHERE QUESTIONID=?";
    
    public static void loadAllPlayerVotes() {
        final long start = System.nanoTime();
        try {
            dbLoadAllPlayerVotes();
        }
        catch (Exception ex) {
            PlayerVotes.logger.log(Level.WARNING, "Problems loading Player Votes.", ex);
        }
        final float lElapsedTime = (System.nanoTime() - start) / 1000000.0f;
        PlayerVotes.logger.log(Level.INFO, "Loaded " + PlayerVotes.playerVotes.size() + " Player Votes. It took " + lElapsedTime + " millis.");
    }
    
    public static PlayerVote addPlayerVote(final PlayerVote newPlayerVote, final boolean saveit) {
        final Long pId = newPlayerVote.getPlayerId();
        if (!PlayerVotes.playerVotes.containsKey(pId)) {
            PlayerVotes.playerVotes.put(pId, new PlayerVotesByPlayer());
        }
        final PlayerVotesByPlayer pvbp = PlayerVotes.playerVotes.get(pId);
        final PlayerVote oldPlayerVote = pvbp.get(newPlayerVote.getQuestionId());
        if (oldPlayerVote != null) {
            oldPlayerVote.update(newPlayerVote.getOption1(), newPlayerVote.getOption2(), newPlayerVote.getOption3(), newPlayerVote.getOption4());
            return oldPlayerVote;
        }
        pvbp.add(newPlayerVote);
        if (saveit) {
            newPlayerVote.save();
        }
        return newPlayerVote;
    }
    
    public static PlayerVote[] getPlayerVotes(final long aPlayerId) {
        final PlayerVotesByPlayer pvbp = PlayerVotes.playerVotes.get(aPlayerId);
        if (pvbp == null) {
            return new PlayerVote[0];
        }
        return pvbp.getVotes();
    }
    
    public static boolean hasPlayerVotedByQuestion(final long aPlayerId, final int aQuestionId) {
        final Long pId = aPlayerId;
        if (PlayerVotes.playerVotes.containsKey(pId)) {
            final PlayerVotesByPlayer pvbp = PlayerVotes.playerVotes.get(pId);
            if (pvbp.containsKey(aQuestionId)) {
                final PlayerVote pv = pvbp.get(aQuestionId);
                return pv.hasVoted();
            }
        }
        return false;
    }
    
    public static PlayerVote getPlayerVotesByQuestions(final long aPlayerId, final int aQuestionId) {
        final Long pId = aPlayerId;
        if (PlayerVotes.playerVotes.containsKey(pId)) {
            final PlayerVotesByPlayer pvbp = PlayerVotes.playerVotes.get(pId);
            if (pvbp.containsKey(aQuestionId)) {
                final PlayerVote pv = pvbp.get(aQuestionId);
                return pv;
            }
        }
        return null;
    }
    
    public static PlayerVote getPlayerVoteByQuestion(final long aPlayerId, final int aQuestionId) {
        final Long pId = aPlayerId;
        if (PlayerVotes.playerVotes.containsKey(pId)) {
            final PlayerVotesByPlayer pvbp = PlayerVotes.playerVotes.get(pId);
            if (pvbp.containsKey(aQuestionId)) {
                final PlayerVote pv = pvbp.get(aQuestionId);
                return pv;
            }
        }
        return null;
    }
    
    public static PlayerVote[] getPlayerVotesByQuestion(final int aQuestionId) {
        final Map<Long, PlayerVote> pVotes = new HashMap<Long, PlayerVote>();
        for (final Map.Entry<Long, PlayerVotesByPlayer> entry : PlayerVotes.playerVotes.entrySet()) {
            if (entry.getValue().containsKey(aQuestionId)) {
                final PlayerVote pv = entry.getValue().get(aQuestionId);
                if (!pv.hasVoted()) {
                    continue;
                }
                pVotes.put(entry.getKey(), pv);
            }
        }
        return pVotes.values().toArray(new PlayerVote[pVotes.size()]);
    }
    
    public static void deletePlayerVotes(final int questionId) {
        for (final Map.Entry<Long, PlayerVotesByPlayer> entry : PlayerVotes.playerVotes.entrySet()) {
            entry.getValue().remove(questionId);
        }
        for (final Player p : Players.getInstance().getPlayers()) {
            p.removePlayerVote(questionId);
        }
        Connection dbcon = null;
        PreparedStatement ps = null;
        try {
            dbcon = DbConnector.getPlayerDbCon();
            ps = dbcon.prepareStatement("DELETE FROM VOTES WHERE QUESTIONID=?");
            ps.setInt(1, questionId);
            ps.executeUpdate();
        }
        catch (SQLException sqx) {
            PlayerVotes.logger.log(Level.WARNING, sqx.getMessage(), sqx);
        }
        finally {
            DbUtilities.closeDatabaseObjects(ps, null);
            DbConnector.returnConnection(dbcon);
        }
    }
    
    private static void dbLoadAllPlayerVotes() {
        Connection dbcon = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            dbcon = DbConnector.getPlayerDbCon();
            ps = dbcon.prepareStatement("SELECT * FROM VOTES");
            rs = ps.executeQuery();
            while (rs.next()) {
                final long aPlayerId = rs.getLong("PLAYERID");
                final int aQuestionId = rs.getInt("QUESTIONID");
                final boolean aOption1 = rs.getBoolean("OPTION1");
                final boolean aOption2 = rs.getBoolean("OPTION2");
                final boolean aOption3 = rs.getBoolean("OPTION3");
                final boolean aOption4 = rs.getBoolean("OPTION4");
                addPlayerVote(new PlayerVote(aPlayerId, aQuestionId, aOption1, aOption2, aOption3, aOption4), false);
            }
        }
        catch (SQLException sqx) {
            PlayerVotes.logger.log(Level.WARNING, sqx.getMessage(), sqx);
        }
        finally {
            DbUtilities.closeDatabaseObjects(ps, rs);
            DbConnector.returnConnection(dbcon);
        }
    }
    
    static {
        PlayerVotes.logger = Logger.getLogger(PlayerVotes.class.getName());
        playerVotes = new ConcurrentHashMap<Long, PlayerVotesByPlayer>();
    }
}
