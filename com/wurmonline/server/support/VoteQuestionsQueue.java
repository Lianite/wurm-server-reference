// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.support;

import com.wurmonline.server.players.Player;
import com.wurmonline.server.players.PlayerVote;
import com.wurmonline.server.Players;

public class VoteQuestionsQueue
{
    public static final byte ADD = 0;
    public static final byte DELETE = 1;
    public static final byte CLOSE = 2;
    public static final byte SETCARDID = 3;
    public static final byte SETARCHIVESTATE = 4;
    private final byte action;
    private final int questionId;
    private VoteQuestion voteQuestion;
    private long newVoteEnd;
    private String newTrelloCardId;
    private byte newArchiveState;
    
    public VoteQuestionsQueue(final byte aAction, final VoteQuestion aVoteQuestion) {
        this.questionId = aVoteQuestion.getQuestionId();
        this.voteQuestion = aVoteQuestion;
        this.action = aAction;
    }
    
    public VoteQuestionsQueue(final byte aAction, final int aQuestionId) {
        this.questionId = aQuestionId;
        this.action = aAction;
    }
    
    public VoteQuestionsQueue(final byte aAction, final int aQuestionId, final long aNewEnd) {
        this.questionId = aQuestionId;
        this.action = aAction;
        this.newVoteEnd = aNewEnd;
    }
    
    public VoteQuestionsQueue(final byte aAction, final int aQuestionId, final String aNewTrelloCardId) {
        this.questionId = aQuestionId;
        this.action = aAction;
        this.newTrelloCardId = aNewTrelloCardId;
    }
    
    public VoteQuestionsQueue(final byte aAction, final int aQuestionId, final byte aNewArchiveState) {
        this.questionId = aQuestionId;
        this.action = aAction;
        this.newArchiveState = aNewArchiveState;
    }
    
    public void action() {
        switch (this.action) {
            case 0: {
                VoteQuestions.addVoteQuestion(this.voteQuestion, true);
                for (final Player p : Players.getInstance().getPlayers()) {
                    if (this.voteQuestion.canVote(p)) {
                        p.addPlayerVote(new PlayerVote(p.getWurmId(), this.voteQuestion.getQuestionId(), false, false, false, false));
                        p.gotVotes(true);
                    }
                }
                break;
            }
            case 1: {
                VoteQuestions.deleteVoteQuestion(this.questionId);
                break;
            }
            case 2: {
                final VoteQuestion vq = VoteQuestions.getVoteQuestion(this.questionId);
                if (vq != null) {
                    vq.closeVoting(this.newVoteEnd);
                    break;
                }
                break;
            }
            case 3: {
                final VoteQuestion vq2 = VoteQuestions.getVoteQuestion(this.questionId);
                if (vq2 != null) {
                    vq2.setTrelloCardId(this.newTrelloCardId);
                    break;
                }
                break;
            }
            case 4: {
                final VoteQuestion vq3 = VoteQuestions.getVoteQuestion(this.questionId);
                if (vq3 != null) {
                    vq3.setArchiveState(this.newArchiveState);
                    break;
                }
                break;
            }
        }
    }
}
