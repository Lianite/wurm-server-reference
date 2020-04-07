// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.skills;

import com.wurmonline.server.Servers;
import com.wurmonline.server.MiscConstants;
import javax.annotation.Nonnull;
import com.wurmonline.server.TimeConstants;

public class SkillTemplate implements TimeConstants, Comparable<SkillTemplate>
{
    private long decayTime;
    @Nonnull
    private int[] dependencies;
    String name;
    private float difficulty;
    private final int number;
    private final short type;
    boolean fightSkill;
    boolean thieverySkill;
    boolean ignoresEnemies;
    boolean isPriestSlowskillgain;
    long tickTime;
    public static final long TICKTIME_ZERO = 0L;
    public static final long TICKTIME_FIVE = 300000L;
    public static final long TICKTIME_ONE = 60000L;
    public static final long TICKTIME_TEN = 600000L;
    public static final long TICKTIME_TWENTY = 1200000L;
    public static final long TICKTIME_HOUR = 3600000L;
    private final float difficultyDivider;
    
    SkillTemplate(final int aNumber, final String aName, final float aDifficulty, @Nonnull final int[] aDependencies, final long aDecayTime, final short aType) {
        this.decayTime = 86400000L;
        this.dependencies = MiscConstants.EMPTY_INT_ARRAY;
        this.name = "Unknown skill";
        this.difficulty = 1.0f;
        this.fightSkill = false;
        this.thieverySkill = false;
        this.ignoresEnemies = false;
        this.isPriestSlowskillgain = false;
        this.tickTime = 0L;
        this.difficultyDivider = (Servers.localServer.isChallengeServer() ? 50.0f : 1.0f);
        this.number = aNumber;
        this.name = aName;
        this.difficulty = aDifficulty / this.difficultyDivider;
        this.dependencies = aDependencies;
        this.decayTime = Math.max(aDecayTime, 1L);
        this.type = aType;
    }
    
    SkillTemplate(final int aNumber, final String aName, final float aDifficulty, @Nonnull final int[] aDependencies, final long aDecayTime, final short aType, final boolean aFightingSkill, final boolean aIgnoreEnemy) {
        this(aNumber, aName, aDifficulty, aDependencies, aDecayTime, aType);
        this.fightSkill = aFightingSkill;
        this.ignoresEnemies = aIgnoreEnemy;
    }
    
    SkillTemplate(final int aNumber, final String aName, final float aDifficulty, @Nonnull final int[] aDependencies, final long aDecayTime, final short aType, final boolean aThieverySkill, final long _tickTime) {
        this(aNumber, aName, aDifficulty, aDependencies, aDecayTime, aType);
        this.thieverySkill = aThieverySkill;
        if (this.thieverySkill) {
            this.ignoresEnemies = true;
        }
        this.tickTime = _tickTime;
    }
    
    public String getName() {
        return this.name;
    }
    
    @Nonnull
    int[] getDependencies() {
        return this.dependencies;
    }
    
    public float getDifficulty() {
        return this.difficulty;
    }
    
    public void setDifficulty(final float newDifficulty) {
        this.difficulty = newDifficulty;
    }
    
    public long getDecayTime() {
        return this.decayTime;
    }
    
    public int getNumber() {
        return this.number;
    }
    
    public boolean isMission() {
        return (this.number >= 10001 && this.number <= 10040) || this.number == 1005;
    }
    
    public short getType() {
        return this.type;
    }
    
    @Override
    public int compareTo(final SkillTemplate aTemplate) {
        return this.getName().compareTo(aTemplate.getName());
    }
    
    public long getTickTime() {
        return this.tickTime;
    }
    
    public boolean isSlowForPriests() {
        return this.isPriestSlowskillgain;
    }
    
    public void setIsSlowForPriests(final boolean isSlow) {
        this.isPriestSlowskillgain = isSlow;
    }
}
