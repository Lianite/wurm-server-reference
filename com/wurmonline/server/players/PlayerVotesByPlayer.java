// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.players;

import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;
import java.util.logging.Logger;

public class PlayerVotesByPlayer
{
    private static Logger logger;
    private final Map<Integer, PlayerVote> playerQuestionVotes;
    
    public PlayerVotesByPlayer() {
        this.playerQuestionVotes = new ConcurrentHashMap<Integer, PlayerVote>();
    }
    
    public PlayerVotesByPlayer(final PlayerVote pv) {
        this.playerQuestionVotes = new ConcurrentHashMap<Integer, PlayerVote>();
        this.add(pv);
    }
    
    public void add(final PlayerVote pv) {
        this.playerQuestionVotes.put(pv.getQuestionId(), pv);
    }
    
    public void remove(final int questionId) {
        if (this.playerQuestionVotes.containsKey(questionId)) {
            this.playerQuestionVotes.remove(questionId);
        }
    }
    
    public PlayerVote get(final int qId) {
        return this.playerQuestionVotes.get(qId);
    }
    
    public boolean containsKey(final int qId) {
        return this.playerQuestionVotes.containsKey(qId);
    }
    
    public PlayerVote[] getVotes() {
        return this.playerQuestionVotes.values().toArray(new PlayerVote[this.playerQuestionVotes.size()]);
    }
    
    static {
        PlayerVotesByPlayer.logger = Logger.getLogger(PlayerVotesByPlayer.class.getName());
    }
}
