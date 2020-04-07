// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.players;

import com.wurmonline.server.Servers;
import java.util.logging.Logger;
import com.wurmonline.server.MiscConstants;

public final class AchievementTemplate implements MiscConstants
{
    private static final Logger logger;
    public static final String CREATOR_SYSTEM = "System";
    private final int number;
    private String name;
    private String creator;
    private String description;
    private boolean isForCooking;
    private boolean isInLiters;
    private boolean isPersonalGoal;
    private String requirement;
    private boolean playSoundOnUpdate;
    private final boolean invisible;
    private static int nextAchievementId;
    private boolean oneTimer;
    private int onTriggerOnCounter;
    private byte type;
    private int[] achievementsTriggered;
    private int[] requiredAchievements;
    private int[] triggeredByAchievements;
    
    public AchievementTemplate(final int identity, final String achName, final boolean isInvisible) {
        this.creator = "System";
        this.description = "";
        this.isForCooking = false;
        this.isInLiters = false;
        this.isPersonalGoal = false;
        this.requirement = "";
        this.playSoundOnUpdate = false;
        this.oneTimer = false;
        this.onTriggerOnCounter = 1;
        this.type = 3;
        this.achievementsTriggered = AchievementTemplate.EMPTY_INT_ARRAY;
        this.requiredAchievements = AchievementTemplate.EMPTY_INT_ARRAY;
        this.triggeredByAchievements = AchievementTemplate.EMPTY_INT_ARRAY;
        this.number = identity;
        this.name = achName;
        this.invisible = isInvisible;
    }
    
    public AchievementTemplate(final int identity, final String achName, final boolean isInvisible, final String requirementString) {
        this.creator = "System";
        this.description = "";
        this.isForCooking = false;
        this.isInLiters = false;
        this.isPersonalGoal = false;
        this.requirement = "";
        this.playSoundOnUpdate = false;
        this.oneTimer = false;
        this.onTriggerOnCounter = 1;
        this.type = 3;
        this.achievementsTriggered = AchievementTemplate.EMPTY_INT_ARRAY;
        this.requiredAchievements = AchievementTemplate.EMPTY_INT_ARRAY;
        this.triggeredByAchievements = AchievementTemplate.EMPTY_INT_ARRAY;
        this.number = identity;
        this.name = achName;
        this.invisible = isInvisible;
        this.isPersonalGoal = (identity < 335);
        this.requirement = requirementString;
    }
    
    public AchievementTemplate(final int identity, final String achName, final boolean isInvisible, final int triggerOn, final byte achievementType, final boolean playUpdateSound, final boolean isOneTimer) {
        this.creator = "System";
        this.description = "";
        this.isForCooking = false;
        this.isInLiters = false;
        this.isPersonalGoal = false;
        this.requirement = "";
        this.playSoundOnUpdate = false;
        this.oneTimer = false;
        this.onTriggerOnCounter = 1;
        this.type = 3;
        this.achievementsTriggered = AchievementTemplate.EMPTY_INT_ARRAY;
        this.requiredAchievements = AchievementTemplate.EMPTY_INT_ARRAY;
        this.triggeredByAchievements = AchievementTemplate.EMPTY_INT_ARRAY;
        this.number = identity;
        this.name = achName;
        this.invisible = isInvisible;
        this.onTriggerOnCounter = triggerOn;
        this.type = achievementType;
        this.playSoundOnUpdate = playUpdateSound;
        this.oneTimer = isOneTimer;
    }
    
    public AchievementTemplate(final int identity, final String achName, final boolean isInvisible, final int triggerOn, final byte achievementType, final boolean playUpdateSound, final boolean isOneTimer, final String requirementString) {
        this.creator = "System";
        this.description = "";
        this.isForCooking = false;
        this.isInLiters = false;
        this.isPersonalGoal = false;
        this.requirement = "";
        this.playSoundOnUpdate = false;
        this.oneTimer = false;
        this.onTriggerOnCounter = 1;
        this.type = 3;
        this.achievementsTriggered = AchievementTemplate.EMPTY_INT_ARRAY;
        this.requiredAchievements = AchievementTemplate.EMPTY_INT_ARRAY;
        this.triggeredByAchievements = AchievementTemplate.EMPTY_INT_ARRAY;
        this.number = identity;
        this.name = achName;
        this.invisible = isInvisible;
        this.onTriggerOnCounter = triggerOn;
        this.type = achievementType;
        this.playSoundOnUpdate = playUpdateSound;
        this.oneTimer = isOneTimer;
        this.isPersonalGoal = (identity < 335);
        this.requirement = requirementString;
    }
    
    public AchievementTemplate(final int identity, final String achName, final boolean isInvisible, final int triggerOn, final String myDescription, final String creatorName, final boolean playUpdateSound, final boolean loaded) {
        this.creator = "System";
        this.description = "";
        this.isForCooking = false;
        this.isInLiters = false;
        this.isPersonalGoal = false;
        this.requirement = "";
        this.playSoundOnUpdate = false;
        this.oneTimer = false;
        this.onTriggerOnCounter = 1;
        this.type = 3;
        this.achievementsTriggered = AchievementTemplate.EMPTY_INT_ARRAY;
        this.requiredAchievements = AchievementTemplate.EMPTY_INT_ARRAY;
        this.triggeredByAchievements = AchievementTemplate.EMPTY_INT_ARRAY;
        this.number = identity;
        this.name = achName;
        this.invisible = isInvisible;
        this.description = myDescription;
        this.onTriggerOnCounter = triggerOn;
        this.type = 2;
        this.creator = creatorName;
        this.playSoundOnUpdate = playUpdateSound;
        this.oneTimer = true;
        AchievementTemplate.nextAchievementId = Math.max(AchievementTemplate.nextAchievementId, this.number + 1);
        if (!loaded) {
            AchievementGenerator.insertAchievementTemplate(this);
        }
    }
    
    public void delete() {
        AchievementGenerator.deleteAchievementTemplate(this);
    }
    
    public static int getNextAchievementId() {
        return AchievementTemplate.nextAchievementId;
    }
    
    public int[] getAchievementsTriggered() {
        return this.achievementsTriggered;
    }
    
    public void setAchievementsTriggered(final int[] aAchievementsTriggered) {
        this.achievementsTriggered = aAchievementsTriggered;
    }
    
    public int[] getRequiredAchievements() {
        return this.requiredAchievements;
    }
    
    public void setRequiredAchievements(final int[] aRequiredAchievements) {
        this.requiredAchievements = aRequiredAchievements;
    }
    
    public String getDescription() {
        return this.description;
    }
    
    public void setDescription(final String aDescription) {
        this.description = aDescription;
    }
    
    public boolean isForCooking() {
        return this.isForCooking;
    }
    
    public void setIsForCooking(final boolean isForCooking) {
        this.isForCooking = isForCooking;
    }
    
    public boolean isInLiters() {
        return this.isInLiters;
    }
    
    public void setIsInLiters(final boolean isInLiters) {
        this.isInLiters = isInLiters;
    }
    
    public boolean isInvisible() {
        return this.invisible;
    }
    
    public int getNumber() {
        return this.number;
    }
    
    public String getName() {
        return this.name;
    }
    
    public void setName(final String aName) {
        this.name = aName;
    }
    
    public int getTriggerOnCounter() {
        return this.onTriggerOnCounter;
    }
    
    public void setTriggerOnCounter(final int triggerOnCounter) {
        this.onTriggerOnCounter = triggerOnCounter;
    }
    
    public String getCreator() {
        return this.creator;
    }
    
    public void setCreator(final String aCreator) {
        this.creator = aCreator;
    }
    
    public byte getType() {
        return this.type;
    }
    
    public void setType(final byte aType) {
        this.type = aType;
    }
    
    public boolean isPlaySoundOnUpdate() {
        return this.playSoundOnUpdate;
    }
    
    public void setPlaySoundOnUpdate(final boolean aPlaySoundOnUpdate) {
        this.playSoundOnUpdate = aPlaySoundOnUpdate;
    }
    
    public boolean isOneTimer() {
        return this.oneTimer;
    }
    
    public void setOneTimer(final boolean aOneTimer) {
        this.oneTimer = aOneTimer;
    }
    
    public String getRequirement() {
        return this.requirement;
    }
    
    public boolean isPersonalGoal() {
        return this.isPersonalGoal;
    }
    
    public void addTriggeredByAchievement(final int achievement) {
        if (this.triggeredByAchievements.length > 0) {
            final int[] newList = new int[this.triggeredByAchievements.length + 1];
            System.arraycopy(this.triggeredByAchievements, 0, newList, 0, this.triggeredByAchievements.length);
            newList[newList.length - 1] = achievement;
            this.triggeredByAchievements = newList;
        }
        else {
            this.triggeredByAchievements = new int[] { achievement };
        }
    }
    
    public int[] getTriggeredByAchievements() {
        return this.triggeredByAchievements;
    }
    
    public float getProgressFor(final long wurmId) {
        if (Achievements.getAchievementObject(wurmId).getAchievement(this.number) != null) {
            return 1.0f;
        }
        if (this.triggeredByAchievements.length == 0 || this.getTriggerOnCounter() == 0) {
            return 0.0f;
        }
        float totalCount = 0.0f;
        for (final int i : this.triggeredByAchievements) {
            final Achievement a = Achievements.getAchievementObject(wurmId).getAchievement(i);
            if (a != null) {
                totalCount += a.getCounter();
            }
        }
        return Math.max(0.0f, Math.min(1.0f, totalCount / this.getTriggerOnCounter()));
    }
    
    static {
        logger = Logger.getLogger(AchievementTemplate.class.getName());
        AchievementTemplate.nextAchievementId = Servers.localServer.id * 100000;
    }
}
