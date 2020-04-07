// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.statistics;

public class ScoreNamePair implements Comparable<ScoreNamePair>
{
    public final String name;
    public final ChallengeScore score;
    
    public ScoreNamePair(final String owner, final ChallengeScore score) {
        this.name = owner;
        this.score = score;
    }
    
    @Override
    public int compareTo(final ScoreNamePair namePair) {
        if (this.score.getPoints() > namePair.score.getPoints()) {
            return -1;
        }
        if (this.name.toLowerCase().equals(namePair.name.toLowerCase()) && this.score.getPoints() == namePair.score.getPoints()) {
            return 0;
        }
        return 1;
    }
}
