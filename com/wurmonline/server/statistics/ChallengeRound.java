// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.statistics;

import java.util.concurrent.ConcurrentHashMap;

public class ChallengeRound
{
    private final int round;
    private final ConcurrentHashMap<Integer, ChallengeScore> privateScores;
    
    ChallengeRound(final int roundval) {
        this.privateScores = new ConcurrentHashMap<Integer, ChallengeScore>();
        this.round = roundval;
    }
    
    protected final void setScore(final ChallengeScore score) {
        this.privateScores.put(score.getType(), score);
    }
    
    protected final ChallengeScore getCurrentScoreForType(final int type) {
        return this.privateScores.get(type);
    }
    
    protected final ChallengeScore[] getScores() {
        return this.privateScores.values().toArray(new ChallengeScore[this.privateScores.size()]);
    }
    
    public int getRound() {
        return this.round;
    }
    
    public final String getRoundName() {
        return ChallengePointEnum.ChallengeScenario.fromInt(this.round).getName();
    }
    
    public final String getRoundDescription() {
        return ChallengePointEnum.ChallengeScenario.fromInt(this.round).getDesc();
    }
    
    public final String getRoundIcon() {
        return ChallengePointEnum.ChallengeScenario.fromInt(this.round).getUrl();
    }
    
    public final boolean isCurrent() {
        return this.round == ChallengePointEnum.ChallengeScenario.current.getNum();
    }
}
