// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.webinterface;

import com.wurmonline.server.players.Player;
import java.util.Map;
import com.wurmonline.server.NoSuchPlayerException;
import com.wurmonline.server.Players;
import com.wurmonline.server.players.PlayerVotes;
import java.util.concurrent.ConcurrentHashMap;
import com.wurmonline.server.Servers;
import com.wurmonline.server.support.VoteQuestions;
import java.io.IOException;
import java.io.InputStream;
import java.io.DataInputStream;
import java.io.ByteArrayInputStream;
import com.wurmonline.shared.util.StreamUtilities;
import java.util.logging.Level;
import java.io.OutputStream;
import java.io.DataOutputStream;
import java.io.ByteArrayOutputStream;
import com.wurmonline.server.WurmId;
import com.wurmonline.server.support.VoteQuestion;
import com.wurmonline.server.players.PlayerVote;
import java.util.logging.Logger;
import com.wurmonline.server.MiscConstants;

public class WcVoting extends WebCommand implements MiscConstants
{
    private static final Logger logger;
    public static final byte DO_NOTHING = 0;
    public static final byte VOTE_QUESTION = 1;
    public static final byte ASK_FOR_VOTES = 2;
    public static final byte PLAYER_VOTE = 3;
    public static final byte VOTE_SUMMARY = 4;
    public static final byte REMOVE_QUESTION = 5;
    public static final byte CLOSE_VOTING = 6;
    private byte type;
    private int questionId;
    private String questionTitle;
    private String questionText;
    private String option1;
    private String option2;
    private String option3;
    private String option4;
    private boolean allowMultiple;
    private boolean premOnly;
    private boolean jk;
    private boolean mr;
    private boolean hots;
    private boolean freedom;
    private long voteStart;
    private long voteEnd;
    private long playerId;
    private short voteCount;
    private short count1;
    private short count2;
    private short count3;
    private short count4;
    private int[] questionIds;
    private PlayerVote[] playerVotes;
    
    public WcVoting(final VoteQuestion voteQuestion) {
        super(WurmId.getNextWCCommandId(), (short)20);
        this.type = 0;
        this.type = 1;
        this.questionId = voteQuestion.getQuestionId();
        this.questionTitle = voteQuestion.getQuestionTitle();
        this.questionText = voteQuestion.getQuestionText();
        this.option1 = voteQuestion.getOption1Text();
        this.option2 = voteQuestion.getOption2Text();
        this.option3 = voteQuestion.getOption3Text();
        this.option4 = voteQuestion.getOption4Text();
        this.allowMultiple = voteQuestion.isAllowMultiple();
        this.premOnly = voteQuestion.isPremOnly();
        this.jk = voteQuestion.isJK();
        this.mr = voteQuestion.isMR();
        this.hots = voteQuestion.isHots();
        this.freedom = voteQuestion.isFreedom();
        this.voteStart = voteQuestion.getVoteStart();
        this.voteEnd = voteQuestion.getVoteEnd();
    }
    
    public WcVoting(final long aPlayerId, final int[] aQuestions) {
        super(WurmId.getNextWCCommandId(), (short)20);
        this.type = 0;
        this.type = 2;
        this.questionIds = aQuestions;
        this.playerId = aPlayerId;
    }
    
    public WcVoting(final PlayerVote pv) {
        super(WurmId.getNextWCCommandId(), (short)20);
        this.type = 0;
        this.type = 3;
        this.playerId = pv.getPlayerId();
        this.playerVotes = new PlayerVote[] { pv };
    }
    
    public WcVoting(final long aPlayerId, final PlayerVote[] pvs) {
        super(WurmId.getNextWCCommandId(), (short)20);
        this.type = 0;
        this.type = 3;
        this.playerId = aPlayerId;
        this.playerVotes = pvs;
    }
    
    public WcVoting(final int aQuestionId, final short aVoteCount, final short aCount1, final short aCount2, final short aCount3, final short aCount4) {
        super(WurmId.getNextWCCommandId(), (short)20);
        this.type = 0;
        this.type = 4;
        this.questionId = aQuestionId;
        this.voteCount = aVoteCount;
        this.count1 = aCount1;
        this.count2 = aCount2;
        this.count3 = aCount3;
        this.count4 = aCount4;
    }
    
    public WcVoting(final byte aAction, final int aQuestionId) {
        super(WurmId.getNextWCCommandId(), (short)20);
        this.type = 0;
        this.type = aAction;
        this.questionId = aQuestionId;
    }
    
    public WcVoting(final byte aAction, final int aQuestionId, final long when) {
        super(WurmId.getNextWCCommandId(), (short)20);
        this.type = 0;
        this.type = aAction;
        this.questionId = aQuestionId;
        this.voteEnd = when;
    }
    
    public WcVoting(final long aId, final byte[] aData) {
        super(aId, (short)20, aData);
        this.type = 0;
    }
    
    @Override
    public boolean autoForward() {
        return false;
    }
    
    @Override
    byte[] encode() {
        final ByteArrayOutputStream bos = new ByteArrayOutputStream();
        DataOutputStream dos = null;
        byte[] barr = null;
        try {
            dos = new DataOutputStream(bos);
            dos.writeByte(this.type);
            switch (this.type) {
                case 1: {
                    dos.writeInt(this.questionId);
                    dos.writeUTF(this.questionTitle);
                    dos.writeUTF(this.questionText);
                    dos.writeUTF(this.option1);
                    dos.writeUTF(this.option2);
                    dos.writeUTF(this.option3);
                    dos.writeUTF(this.option4);
                    dos.writeBoolean(this.allowMultiple);
                    dos.writeBoolean(this.premOnly);
                    dos.writeBoolean(this.jk);
                    dos.writeBoolean(this.mr);
                    dos.writeBoolean(this.hots);
                    dos.writeBoolean(this.freedom);
                    dos.writeLong(this.voteStart);
                    dos.writeLong(this.voteEnd);
                    break;
                }
                case 2: {
                    dos.writeLong(this.playerId);
                    dos.writeInt(this.questionIds.length);
                    for (final int qId : this.questionIds) {
                        dos.writeInt(qId);
                    }
                    break;
                }
                case 3: {
                    dos.writeLong(this.playerId);
                    dos.writeInt(this.playerVotes.length);
                    for (final PlayerVote pv : this.playerVotes) {
                        dos.writeInt(pv.getQuestionId());
                        dos.writeBoolean(pv.getOption1());
                        dos.writeBoolean(pv.getOption2());
                        dos.writeBoolean(pv.getOption3());
                        dos.writeBoolean(pv.getOption4());
                    }
                    break;
                }
                case 4: {
                    dos.writeInt(this.questionId);
                    dos.writeShort(this.voteCount);
                    dos.writeShort(this.count1);
                    dos.writeShort(this.count2);
                    dos.writeShort(this.count3);
                    dos.writeShort(this.count4);
                    break;
                }
                case 5: {
                    dos.writeInt(this.questionId);
                    break;
                }
                case 6: {
                    dos.writeInt(this.questionId);
                    dos.writeLong(this.voteEnd);
                    break;
                }
            }
            dos.flush();
            dos.close();
        }
        catch (Exception ex) {
            WcVoting.logger.log(Level.WARNING, ex.getMessage(), ex);
        }
        finally {
            StreamUtilities.closeOutputStreamIgnoreExceptions(dos);
            barr = bos.toByteArray();
            StreamUtilities.closeOutputStreamIgnoreExceptions(bos);
            this.setData(barr);
        }
        return barr;
    }
    
    @Override
    public void execute() {
        new Thread() {
            @Override
            public void run() {
                DataInputStream dis = null;
                try {
                    dis = new DataInputStream(new ByteArrayInputStream(WcVoting.this.getData()));
                    WcVoting.this.type = dis.readByte();
                    switch (WcVoting.this.type) {
                        case 1: {
                            WcVoting.this.questionId = dis.readInt();
                            WcVoting.this.questionTitle = dis.readUTF();
                            WcVoting.this.questionText = dis.readUTF();
                            WcVoting.this.option1 = dis.readUTF();
                            WcVoting.this.option2 = dis.readUTF();
                            WcVoting.this.option3 = dis.readUTF();
                            WcVoting.this.option4 = dis.readUTF();
                            WcVoting.this.allowMultiple = dis.readBoolean();
                            WcVoting.this.premOnly = dis.readBoolean();
                            WcVoting.this.jk = dis.readBoolean();
                            WcVoting.this.mr = dis.readBoolean();
                            WcVoting.this.hots = dis.readBoolean();
                            WcVoting.this.freedom = dis.readBoolean();
                            WcVoting.this.voteStart = dis.readLong();
                            WcVoting.this.voteEnd = dis.readLong();
                            break;
                        }
                        case 2: {
                            WcVoting.this.playerId = dis.readLong();
                            WcVoting.this.questionIds = new int[dis.readInt()];
                            for (int i = 0; i < WcVoting.this.questionIds.length; ++i) {
                                WcVoting.this.questionIds[i] = dis.readInt();
                            }
                            break;
                        }
                        case 3: {
                            WcVoting.this.playerId = dis.readLong();
                            WcVoting.this.playerVotes = new PlayerVote[dis.readInt()];
                            for (int i = 0; i < WcVoting.this.playerVotes.length; ++i) {
                                final PlayerVote pv = new PlayerVote(WcVoting.this.playerId, dis.readInt(), dis.readBoolean(), dis.readBoolean(), dis.readBoolean(), dis.readBoolean());
                                WcVoting.this.playerVotes[i] = pv;
                            }
                            break;
                        }
                        case 4: {
                            WcVoting.this.questionId = dis.readInt();
                            WcVoting.this.voteCount = dis.readShort();
                            WcVoting.this.count1 = dis.readShort();
                            WcVoting.this.count2 = dis.readShort();
                            WcVoting.this.count3 = dis.readShort();
                            WcVoting.this.count4 = dis.readShort();
                            break;
                        }
                        case 5: {
                            WcVoting.this.questionId = dis.readInt();
                            break;
                        }
                        case 6: {
                            WcVoting.this.questionId = dis.readInt();
                            WcVoting.this.voteEnd = dis.readLong();
                            break;
                        }
                    }
                }
                catch (IOException ex) {
                    WcVoting.logger.log(Level.WARNING, "Unpack exception " + ex.getMessage(), ex);
                    return;
                }
                finally {
                    StreamUtilities.closeInputStreamIgnoreExceptions(dis);
                }
                switch (WcVoting.this.type) {
                    case 1: {
                        VoteQuestions.queueAddVoteQuestion(WcVoting.this.questionId, WcVoting.this.questionTitle, WcVoting.this.questionText, WcVoting.this.option1, WcVoting.this.option2, WcVoting.this.option3, WcVoting.this.option4, WcVoting.this.allowMultiple, WcVoting.this.premOnly, WcVoting.this.jk, WcVoting.this.mr, WcVoting.this.hots, WcVoting.this.freedom, WcVoting.this.voteStart, WcVoting.this.voteEnd);
                        break;
                    }
                    case 2: {
                        if (Servers.isThisLoginServer()) {
                            final Map<Integer, PlayerVote> pVotes = new ConcurrentHashMap<Integer, PlayerVote>();
                            for (final int qId : WcVoting.this.questionIds) {
                                final PlayerVote pv2 = PlayerVotes.getPlayerVoteByQuestion(WcVoting.this.playerId, qId);
                                if (pv2 != null) {
                                    if (pv2.hasVoted()) {
                                        pVotes.put(qId, pv2);
                                    }
                                }
                            }
                            final WcVoting wv = new WcVoting(WcVoting.this.playerId, pVotes.values().toArray(new PlayerVote[pVotes.size()]));
                            wv.sendToServer(WurmId.getOrigin(WcVoting.this.getWurmId()));
                            break;
                        }
                        break;
                    }
                    case 3: {
                        if (Servers.isThisLoginServer()) {
                            for (final PlayerVote pv3 : WcVoting.this.playerVotes) {
                                PlayerVotes.addPlayerVote(pv3, true);
                            }
                        }
                        try {
                            final Player p = Players.getInstance().getPlayer(WcVoting.this.playerId);
                            p.setVotes(WcVoting.this.playerVotes);
                        }
                        catch (NoSuchPlayerException ex2) {}
                        break;
                    }
                    case 4: {
                        if (Servers.isThisLoginServer()) {
                            final VoteQuestion vq = VoteQuestions.getVoteQuestion(WcVoting.this.questionId);
                            final WcVoting wv = new WcVoting(vq.getQuestionId(), vq.getVoteCount(), vq.getOption1Count(), vq.getOption2Count(), vq.getOption3Count(), vq.getOption4Count());
                            wv.sendToServer(WurmId.getOrigin(WcVoting.this.getWurmId()));
                            break;
                        }
                        break;
                    }
                    case 5: {
                        VoteQuestions.queueRemoveVoteQuestion(WcVoting.this.questionId);
                        break;
                    }
                    case 6: {
                        VoteQuestions.queueCloseVoteQuestion(WcVoting.this.questionId, WcVoting.this.voteEnd);
                        break;
                    }
                }
            }
        }.start();
    }
    
    static {
        logger = Logger.getLogger(WcVoting.class.getName());
    }
}
