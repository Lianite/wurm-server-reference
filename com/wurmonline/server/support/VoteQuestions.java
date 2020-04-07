// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.support;

import java.util.concurrent.ConcurrentHashMap;
import com.wurmonline.server.players.PlayerVote;
import com.wurmonline.server.Players;
import com.wurmonline.server.players.PlayerVotes;
import java.sql.ResultSet;
import java.sql.PreparedStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import com.wurmonline.server.utils.DbUtilities;
import com.wurmonline.server.DbConnector;
import com.wurmonline.server.players.Player;
import com.wurmonline.server.VoteServer;
import com.wurmonline.server.webinterface.WcVoting;
import com.wurmonline.server.Servers;
import java.util.Iterator;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.Map;
import java.util.logging.Logger;
import com.wurmonline.server.TimeConstants;
import com.wurmonline.shared.constants.CounterTypes;

public final class VoteQuestions implements CounterTypes, TimeConstants
{
    private static Logger logger;
    private static final Map<Integer, VoteQuestion> voteQuestions;
    private static final ConcurrentLinkedDeque<VoteQuestionsQueue> questionsQueue;
    private static int lastQuestionId;
    private static final String LOADALLQUESTIONS = "SELECT * FROM VOTINGQUESTIONS";
    private static final String LOADALLVOTINGSERVERS = "SELECT * FROM VOTINGSERVERS";
    
    public static void loadVoteQuestions() {
        final long start = System.nanoTime();
        try {
            dbLoadAllVoteQuestions();
        }
        catch (Exception ex) {
            VoteQuestions.logger.log(Level.WARNING, "Problems loading Vote Questions", ex);
        }
        final float lElapsedTime = (System.nanoTime() - start) / 1000000.0f;
        VoteQuestions.logger.log(Level.INFO, "Loaded " + VoteQuestions.voteQuestions.size() + " Vote Questions. It took " + lElapsedTime + " millis.");
    }
    
    public static VoteQuestion addVoteQuestion(final VoteQuestion newVoteQuestion, final boolean saveit) {
        if (VoteQuestions.voteQuestions.containsKey(newVoteQuestion.getQuestionId())) {
            final VoteQuestion oldVoteQuestion = VoteQuestions.voteQuestions.get(newVoteQuestion.getQuestionId());
            oldVoteQuestion.update(newVoteQuestion.getQuestionTitle(), newVoteQuestion.getQuestionText(), newVoteQuestion.getOption1Text(), newVoteQuestion.getOption2Text(), newVoteQuestion.getOption3Text(), newVoteQuestion.getOption4Text(), newVoteQuestion.isAllowMultiple(), newVoteQuestion.isPremOnly(), newVoteQuestion.isJK(), newVoteQuestion.isMR(), newVoteQuestion.isHots(), newVoteQuestion.isFreedom(), newVoteQuestion.getVoteStart(), newVoteQuestion.getVoteEnd(), newVoteQuestion.getServers());
            return oldVoteQuestion;
        }
        VoteQuestions.voteQuestions.put(newVoteQuestion.getQuestionId(), newVoteQuestion);
        if (saveit) {
            newVoteQuestion.save();
        }
        return newVoteQuestion;
    }
    
    public static VoteQuestion[] getArchiveVoteQuestions() {
        final Map<Integer, VoteQuestion> archiveVoteQuestions = new HashMap<Integer, VoteQuestion>();
        for (final Map.Entry<Integer, VoteQuestion> entry : VoteQuestions.voteQuestions.entrySet()) {
            final VoteQuestion voteQuestion = entry.getValue();
            if (voteQuestion.getArchiveState() == 2) {
                archiveVoteQuestions.put(entry.getKey(), entry.getValue());
            }
        }
        return archiveVoteQuestions.values().toArray(new VoteQuestion[archiveVoteQuestions.size()]);
    }
    
    public static VoteQuestion[] getFinishedQuestions() {
        final Map<Integer, VoteQuestion> finishedVoteQuestions = new HashMap<Integer, VoteQuestion>();
        for (final Map.Entry<Integer, VoteQuestion> entry : VoteQuestions.voteQuestions.entrySet()) {
            final VoteQuestion voteQuestion = entry.getValue();
            if (voteQuestion.hasSummary() && voteQuestion.getSent() == 4) {
                finishedVoteQuestions.put(entry.getKey(), entry.getValue());
            }
        }
        return finishedVoteQuestions.values().toArray(new VoteQuestion[finishedVoteQuestions.size()]);
    }
    
    public static void deleteVoteQuestion(final int aId) {
        final VoteQuestion vq = getVoteQuestion(aId);
        if (Servers.isThisLoginServer() && vq != null && vq.getSent() == 1) {
            final WcVoting wv = new WcVoting((byte)5, aId);
            for (final VoteServer vs : vq.getServers()) {
                if (vs.getServerId() != Servers.getLocalServerId()) {
                    wv.sendToServer(vs.getServerId());
                }
            }
        }
        VoteQuestions.voteQuestions.remove(aId);
        if (vq != null) {
            vq.delete();
        }
    }
    
    public static void closeVoteing(final int aId, final long aVoteEnd) {
        final VoteQuestion vq = getVoteQuestion(aId);
        if (Servers.isThisLoginServer() && vq != null && vq.getSent() == 1) {
            final WcVoting wv = new WcVoting((byte)6, aId, aVoteEnd);
            for (final VoteServer vs : vq.getServers()) {
                if (vs.getServerId() != Servers.getLocalServerId()) {
                    wv.sendToServer(vs.getServerId());
                }
            }
        }
        vq.closeVoting(aVoteEnd);
    }
    
    public static VoteQuestion getVoteQuestion(final int aId) {
        return VoteQuestions.voteQuestions.get(aId);
    }
    
    public static VoteQuestion[] getVoteQuestions(final Player player) {
        final Map<Integer, VoteQuestion> playerVoteQuestions = new HashMap<Integer, VoteQuestion>();
        for (final Map.Entry<Integer, VoteQuestion> entry : VoteQuestions.voteQuestions.entrySet()) {
            final VoteQuestion voteQuestion = entry.getValue();
            if (voteQuestion.canVote(player)) {
                playerVoteQuestions.put(entry.getKey(), entry.getValue());
            }
        }
        return playerVoteQuestions.values().toArray(new VoteQuestion[playerVoteQuestions.size()]);
    }
    
    public static int[] getVoteQuestionIds(final Player player) {
        final VoteQuestion[] vqs = getVoteQuestions(player);
        final int[] ids = new int[vqs.length];
        for (int i = 0; i < vqs.length; ++i) {
            ids[i] = vqs[i].getQuestionId();
        }
        return ids;
    }
    
    public static VoteQuestion[] getVoteQuestionsAboutToStart() {
        final Map<Integer, VoteQuestion> playerVoteQuestions = new HashMap<Integer, VoteQuestion>();
        for (final Map.Entry<Integer, VoteQuestion> entry : VoteQuestions.voteQuestions.entrySet()) {
            final VoteQuestion voteQuestion = entry.getValue();
            if (voteQuestion.aboutToStart()) {
                playerVoteQuestions.put(entry.getKey(), entry.getValue());
            }
        }
        return playerVoteQuestions.values().toArray(new VoteQuestion[playerVoteQuestions.size()]);
    }
    
    public static VoteQuestion[] getVoteQuestionsNeedingSummary() {
        final Map<Integer, VoteQuestion> playerVoteQuestions = new HashMap<Integer, VoteQuestion>();
        for (final Map.Entry<Integer, VoteQuestion> entry : VoteQuestions.voteQuestions.entrySet()) {
            final VoteQuestion voteQuestion = entry.getValue();
            if (voteQuestion.canMakeSummary()) {
                playerVoteQuestions.put(entry.getKey(), entry.getValue());
            }
        }
        return playerVoteQuestions.values().toArray(new VoteQuestion[playerVoteQuestions.size()]);
    }
    
    public static VoteQuestion[] getVoteQuestions() {
        return VoteQuestions.voteQuestions.values().toArray(new VoteQuestion[VoteQuestions.voteQuestions.size()]);
    }
    
    private static void dbLoadAllVoteQuestions() {
        Connection dbcon = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            dbcon = DbConnector.getLoginDbCon();
            ps = dbcon.prepareStatement("SELECT * FROM VOTINGQUESTIONS");
            rs = ps.executeQuery();
            while (rs.next()) {
                final int aQuestionId = rs.getInt("QUESTIONID");
                final String aQuestionTitle = rs.getString("QUESTIONTITLE");
                final String aQuestionText = rs.getString("QUESTIONTEXT");
                final String aOption1Text = rs.getString("OPTION1_TEXT");
                final String aOption2Text = rs.getString("OPTION2_TEXT");
                final String aOption3Text = rs.getString("OPTION3_TEXT");
                final String aOption4Text = rs.getString("OPTION4_TEXT");
                final boolean aAllowMultiple = rs.getBoolean("ALLOW_MULTIPLE");
                final boolean aPremOnly = rs.getBoolean("PREMIUM_ONLY");
                final boolean aJK = rs.getBoolean("JK");
                final boolean aMR = rs.getBoolean("MR");
                final boolean aHots = rs.getBoolean("HOTS");
                final boolean aFreedom = rs.getBoolean("FREEDOM");
                final long aStart = rs.getLong("VOTE_START");
                final long aEnd = rs.getLong("VOTE_END");
                final byte aSent = rs.getByte("SENT");
                final short aVotesTotal = rs.getShort("VOTES_TOTAL");
                final boolean aHasSummary = rs.getBoolean("HAS_SUMMARY");
                final short aOption1Count = rs.getShort("OPTION1_COUNT");
                final short aOption2Count = rs.getShort("OPTION2_COUNT");
                final short aOption3Count = rs.getShort("OPTION3_COUNT");
                final short aOption4Count = rs.getShort("OPTION4_COUNT");
                final String aTrelloCardId = rs.getString("TRELLOCARDID");
                final byte aArchiveState = rs.getByte("ARCHIVESTATECODE");
                if (aQuestionId > VoteQuestions.lastQuestionId) {
                    VoteQuestions.lastQuestionId = aQuestionId;
                }
                addVoteQuestion(new VoteQuestion(aQuestionId, aQuestionTitle, aQuestionText, aOption1Text, aOption2Text, aOption3Text, aOption4Text, aAllowMultiple, aPremOnly, aJK, aMR, aHots, aFreedom, aStart, aEnd, aSent, aVotesTotal, aHasSummary, aOption1Count, aOption2Count, aOption3Count, aOption4Count, aTrelloCardId, aArchiveState), false);
            }
            DbUtilities.closeDatabaseObjects(ps, rs);
            ps = dbcon.prepareStatement("SELECT * FROM VOTINGSERVERS");
            rs = ps.executeQuery();
            while (rs.next()) {
                final int aQuestionId = rs.getInt("QUESTIONID");
                final int aServerId = rs.getInt("SERVERID");
                final short aVotesTotal2 = rs.getShort("VOTES_TOTAL");
                final short aOption1Count2 = rs.getShort("OPTION1_COUNT");
                final short aOption2Count2 = rs.getShort("OPTION2_COUNT");
                final short aOption3Count2 = rs.getShort("OPTION3_COUNT");
                final short aOption4Count2 = rs.getShort("OPTION4_COUNT");
                getVoteQuestion(aQuestionId).addServer(aServerId, aVotesTotal2, aOption1Count2, aOption2Count2, aOption3Count2, aOption4Count2);
            }
        }
        catch (SQLException sqx) {
            VoteQuestions.logger.log(Level.WARNING, sqx.getMessage(), sqx);
        }
        finally {
            DbUtilities.closeDatabaseObjects(ps, rs);
            DbConnector.returnConnection(dbcon);
        }
    }
    
    public static final int getNextQuestionId() {
        return ++VoteQuestions.lastQuestionId;
    }
    
    public static final void queueAddVoteQuestion(final int aQuestionId, final String aQuestionTitle, final String aQuestionText, final String aOption1Text, final String aOption2Text, final String aOption3Text, final String aOption4Text, final boolean aAllowMultiple, final boolean aPremOnly, final boolean aJK, final boolean aMR, final boolean aHoTs, final boolean aFreedom, final long voteStart, final long voteEnd) {
        final VoteQuestion newVoteQuestion = new VoteQuestion(aQuestionId, aQuestionTitle, aQuestionText, aOption1Text, aOption2Text, aOption3Text, aOption4Text, aAllowMultiple, aPremOnly, aJK, aMR, aHoTs, aFreedom, voteStart, voteEnd);
        VoteQuestions.questionsQueue.add(new VoteQuestionsQueue((byte)0, newVoteQuestion));
    }
    
    public static final void queueRemoveVoteQuestion(final int aQuestionId) {
        VoteQuestions.questionsQueue.add(new VoteQuestionsQueue((byte)1, aQuestionId));
    }
    
    public static final void queueCloseVoteQuestion(final int aQuestionId, final long newEnd) {
        VoteQuestions.questionsQueue.add(new VoteQuestionsQueue((byte)2, aQuestionId, newEnd));
    }
    
    public static final void queueSetTrelloCardId(final int aQuestionId, final String aTrelloCardId) {
        VoteQuestions.questionsQueue.add(new VoteQuestionsQueue((byte)3, aQuestionId, aTrelloCardId));
    }
    
    public static final void queueSetArchiveState(final int aQuestionId, final byte newArchiveState) {
        VoteQuestions.questionsQueue.add(new VoteQuestionsQueue((byte)4, aQuestionId, newArchiveState));
    }
    
    public static final void handleVoting() {
        for (final Map.Entry<Integer, VoteQuestion> entry : VoteQuestions.voteQuestions.entrySet()) {
            entry.getValue().endVoting();
        }
        for (final Map.Entry<Integer, VoteQuestion> entry : VoteQuestions.voteQuestions.entrySet()) {
            entry.getValue().setArchive();
        }
        if (Servers.isThisLoginServer()) {
            final VoteQuestion[] vqStarting = getVoteQuestionsAboutToStart();
            if (vqStarting.length > 0) {
                for (final VoteQuestion vq : vqStarting) {
                    final WcVoting wv = new WcVoting(vq);
                    for (final VoteServer vs : vq.getServers()) {
                        if (vs.getServerId() != Servers.getLocalServerId()) {
                            wv.sendToServer(vs.getServerId());
                        }
                    }
                    vq.setSent((byte)1);
                }
                return;
            }
            final VoteQuestion[] vqEnding = getVoteQuestionsNeedingSummary();
            if (vqEnding.length > 0) {
                for (final VoteQuestion vq2 : vqEnding) {
                    short total = 0;
                    short count1 = 0;
                    short count2 = 0;
                    short count3 = 0;
                    short count4 = 0;
                    for (final PlayerVote pv : PlayerVotes.getPlayerVotesByQuestion(vq2.getQuestionId())) {
                        ++total;
                        if (pv.getOption1()) {
                            ++count1;
                        }
                        if (pv.getOption2()) {
                            ++count2;
                        }
                        if (pv.getOption3()) {
                            ++count3;
                        }
                        if (pv.getOption4()) {
                            ++count4;
                        }
                    }
                    vq2.saveSummary(total, count1, count2, count3, count4);
                    final WcVoting wv2 = new WcVoting(vq2.getQuestionId(), vq2.getVoteCount(), vq2.getOption1Count(), vq2.getOption2Count(), vq2.getOption3Count(), vq2.getOption4Count());
                    for (final VoteServer vs2 : vq2.getServers()) {
                        if (vs2.getServerId() != Servers.getLocalServerId()) {
                            wv2.sendToServer(vs2.getServerId());
                        }
                    }
                    vq2.setSent((byte)4);
                }
                return;
            }
        }
        for (VoteQuestionsQueue vqq = VoteQuestions.questionsQueue.pollFirst(); vqq != null; vqq = VoteQuestions.questionsQueue.pollFirst()) {
            vqq.action();
        }
        for (final Map.Entry<Integer, VoteQuestion> entry2 : VoteQuestions.voteQuestions.entrySet()) {
            final VoteQuestion voteQuestion = entry2.getValue();
            if (voteQuestion.justStarted()) {
                voteQuestion.setSent((byte)2);
                Players.sendVotingOpen(voteQuestion);
            }
        }
    }
    
    public static final void handleArchiveTickets() {
        for (final VoteQuestion vq : VoteQuestions.voteQuestions.values()) {
            if (vq.getArchiveState() == 3) {
                deleteVoteQuestion(vq.getQuestionId());
            }
        }
    }
    
    static {
        VoteQuestions.logger = Logger.getLogger(VoteQuestions.class.getName());
        voteQuestions = new ConcurrentHashMap<Integer, VoteQuestion>();
        questionsQueue = new ConcurrentLinkedDeque<VoteQuestionsQueue>();
        VoteQuestions.lastQuestionId = 0;
    }
}
