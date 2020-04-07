// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.epic;

import com.wurmonline.server.Servers;
import com.wurmonline.server.Server;

public enum EpicMissionEnum
{
    BUILDSTRUCTURE_SP((byte)101, 1, 3, 4, 4, true, false, true, true, false, 250, 250, 0, 60, false, true, new String[] { "creation", "building" }), 
    BUILDSTRUCTURE_TO((byte)102, 1, 4, 5, 5, true, false, true, true, false, 250, 500, 0, 60, false, true, new String[] { "creation", "building" }), 
    BUILDSTRUCTURE_SG((byte)103, 1, 5, 6, 6, true, false, true, true, false, 250, 750, 0, 60, false, true, new String[] { "creation", "building" }), 
    RITUALMS_FRIENDLY((byte)104, 4, 1, 7, 4, true, false, true, true, false, 0, 500, 30, 45, false, true, new String[] { "proximity", "humility" }), 
    RITUALMS_ENEMY((byte)105, 4, 1, 7, 5, false, true, true, false, true, 0, 500, 30, 45, false, true, new String[] { "proximity", "humility" }), 
    CUTTREE_FRIENDLY((byte)106, 4, 1, 5, 5, true, false, true, true, false, 500, 0, 30, 30, false, true, new String[] { "revenge", "entrapment" }), 
    CUTTREE_ENEMY((byte)107, 4, 1, 6, 6, false, true, true, false, true, 750, 0, 30, 30, false, true, new String[] { "revenge", "entrapment" }), 
    RITUALGT((byte)108, 4, 1, 5, 5, false, true, true, false, true, 0, 300, 30, 30, false, true, new String[] { "submission", "danger" }), 
    SACMISSIONITEMS((byte)109, 5, 1, 7, 7, true, true, true, true, false, 100, 500, 20, 30, true, false, new String[] { "wealth", "sacrifice" }), 
    SACITEMS((byte)110, 5, 1, 7, 7, true, false, true, true, false, 0, 500, 20, 30, true, false, new String[] { "wealth", "sacrifice" }), 
    CREATEITEMS((byte)111, 6, 1, 7, 7, true, false, true, true, false, 0, 500, 20, 30, true, false, new String[] { "construction", "thought" }), 
    GIVEITEMS_FRIENDLY((byte)112, 4, 1, 6, 4, true, false, true, true, false, 100, 500, 20, 20, true, false, new String[] { "gifts", "concession" }), 
    GIVEITEMS_ENEMY((byte)113, 3, 3, 7, 5, true, true, true, true, false, 150, 600, 30, 40, true, false, new String[] { "gifts", "concession" }), 
    SLAYCREATURE_PASSIVE((byte)114, 4, 1, 3, 3, true, false, true, true, false, 0, 500, 15, 30, true, false, new String[] { "annihilation", "treason" }), 
    SLAYCREATURE_HOSTILELOW((byte)115, 5, 2, 6, 6, true, true, true, true, true, 0, 500, 15, 30, true, false, new String[] { "annihilation", "treason" }), 
    SLAYCREATURE_HOSTILEHIGH((byte)116, 4, 5, 7, 7, true, true, true, true, true, 0, 500, 15, 30, true, false, new String[] { "annihilation", "treason" }), 
    SLAYTRAITOR_PASSIVE((byte)117, 4, 1, 3, 3, true, false, true, true, false, 0, 500, 15, 30, true, false, new String[] { "annihilation", "treason" }), 
    SLAYTRAITOR_HOSTILELOW((byte)118, 5, 2, 5, 5, true, true, true, true, true, 0, 500, 15, 30, true, false, new String[] { "annihilation", "treason" }), 
    SLAYTRAITOR_HOSTILEHIGH((byte)119, 4, 5, 7, 7, true, true, true, true, true, 0, 500, 15, 30, true, false, new String[] { "annihilation", "treason" }), 
    DESTROYGT((byte)120, 3, 3, 6, 6, false, true, true, false, true, 750, 0, 30, 30, false, true, new String[] { "destruction", "devastation" }), 
    SACCREATURE_PASSIVE((byte)121, 3, 1, 3, 3, true, false, true, true, false, 0, 500, 15, 30, true, false, new String[] { "annihilation", "treason" }), 
    SACCREATURE_HOSTILELOW((byte)122, 4, 4, 5, 5, true, true, true, true, true, 0, 500, 15, 30, true, false, new String[] { "annihilation", "treason" }), 
    SACCREATURE_HOSTILEHIGH((byte)123, 3, 6, 7, 7, true, true, true, true, true, 0, 500, 15, 30, true, false, new String[] { "annihilation", "treason" }), 
    SLAYTOWERGUARDS((byte)124, 3, 1, 7, 7, false, true, true, false, true, 0, 500, 15, 30, true, false, new String[] { "cleansing", "attack" });
    
    private byte missionType;
    private int missionChance;
    private int minDifficulty;
    private int maxDifficulty;
    private boolean friendlyTerritory;
    private boolean enemyTerritory;
    private boolean battlegroundServer;
    private boolean homeServer;
    private boolean enemyHomeServer;
    private int baseKarma;
    private int karmaBonusDiffMult;
    private int baseSleep;
    private int sleepBonusDiffMult;
    private boolean isKarmaMultProgress;
    private boolean isSleepMultNearby;
    private String[] missionNames;
    
    public static EpicMissionEnum getRandomMission(final int difficulty, final boolean battlegroundServer, final boolean homeServer, final boolean enemyHomeServer) {
        int totalChance = 0;
        for (final EpicMissionEnum f : values()) {
            if (f.minDifficulty <= difficulty && f.maxDifficulty >= difficulty && ((battlegroundServer && f.battlegroundServer) || (homeServer && f.homeServer) || (enemyHomeServer && f.enemyHomeServer))) {
                totalChance += f.getMissionChance();
            }
        }
        if (totalChance == 0) {
            return null;
        }
        final int winningVal = Server.rand.nextInt(totalChance);
        int thisVal = 0;
        for (final EpicMissionEnum f2 : values()) {
            if (f2.minDifficulty <= difficulty && f2.maxDifficulty >= difficulty && ((battlegroundServer && f2.battlegroundServer) || (homeServer && f2.homeServer) || (enemyHomeServer && f2.enemyHomeServer))) {
                if (thisVal + f2.getMissionChance() > winningVal) {
                    return f2;
                }
                thisVal += f2.getMissionChance();
            }
        }
        return null;
    }
    
    public static EpicMissionEnum getMissionForType(final byte missionType) {
        for (final EpicMissionEnum f : values()) {
            if (f.getMissionType() == missionType) {
                return f;
            }
        }
        return null;
    }
    
    public static boolean isMissionItem(final EpicMissionEnum mission) {
        switch (mission.getMissionType()) {
            case 109:
            case 110:
            case 111:
            case 112:
            case 113: {
                return true;
            }
            default: {
                return false;
            }
        }
    }
    
    public static boolean isNumReqItemEffected(final EpicMissionEnum mission) {
        switch (mission.getMissionType()) {
            case 110:
            case 111: {
                return true;
            }
            default: {
                return false;
            }
        }
    }
    
    public static boolean isMissionCreature(final EpicMissionEnum mission) {
        switch (mission.getMissionType()) {
            case 114:
            case 115:
            case 116:
            case 117:
            case 118:
            case 119:
            case 121:
            case 122:
            case 123:
            case 124: {
                return true;
            }
            default: {
                return false;
            }
        }
    }
    
    public static boolean isMissionKarmaGivenOnKill(final EpicMissionEnum mission) {
        switch (mission.getMissionType()) {
            case 114:
            case 115:
            case 116:
            case 117:
            case 118:
            case 119:
            case 124: {
                return true;
            }
            default: {
                return false;
            }
        }
    }
    
    public static boolean isKarmaSplitNearby(final EpicMissionEnum mission) {
        switch (mission.getMissionType()) {
            case 101:
            case 102:
            case 103: {
                return true;
            }
            default: {
                return false;
            }
        }
    }
    
    public static boolean isRitualMission(final EpicMissionEnum mission) {
        switch (mission.getMissionType()) {
            case 104:
            case 105:
            case 108: {
                return true;
            }
            default: {
                return false;
            }
        }
    }
    
    private EpicMissionEnum(final byte missionType, final int missionChance, final int minDifficulty, final int maxDifficulty, final int maxDifficultyPvp, final boolean friendlyTerritory, final boolean enemyTerritory, final boolean battlegroundServer, final boolean homeServer, final boolean enemyHomeServer, final int baseKarma, final int karmaBonusDiffMult, final int baseSleep, final int sleepBonusDiffMult, final boolean isKarmaMultProgress, final boolean isSleepMultNearby, final String[] missionNames) {
        this.missionType = missionType;
        this.missionChance = missionChance;
        this.minDifficulty = minDifficulty;
        this.maxDifficulty = maxDifficulty;
        if (Servers.localServer.PVPSERVER) {
            this.maxDifficulty = maxDifficultyPvp;
        }
        this.friendlyTerritory = friendlyTerritory;
        this.enemyTerritory = enemyTerritory;
        this.battlegroundServer = battlegroundServer;
        this.homeServer = homeServer;
        this.enemyHomeServer = enemyHomeServer;
        this.baseKarma = baseKarma;
        this.karmaBonusDiffMult = karmaBonusDiffMult;
        this.baseSleep = baseSleep;
        this.sleepBonusDiffMult = sleepBonusDiffMult;
        this.isKarmaMultProgress = isKarmaMultProgress;
        this.isSleepMultNearby = isSleepMultNearby;
        this.missionNames = missionNames;
    }
    
    public byte getMissionType() {
        return this.missionType;
    }
    
    public int getMissionChance() {
        return this.missionChance;
    }
    
    public int getMinDifficulty() {
        return this.minDifficulty;
    }
    
    public int getMaxDifficulty() {
        return this.maxDifficulty;
    }
    
    public boolean isFriendlyTerritory() {
        return this.friendlyTerritory;
    }
    
    public boolean isEnemyTerritory() {
        return this.enemyTerritory;
    }
    
    public boolean isBattlegroundServer() {
        return this.battlegroundServer;
    }
    
    public boolean isHomeServer() {
        return this.homeServer;
    }
    
    public boolean isEnemyHomeServer() {
        return this.enemyHomeServer;
    }
    
    public int getBaseKarma() {
        return this.baseKarma;
    }
    
    public int getKarmaBonusDiffMult() {
        return this.karmaBonusDiffMult;
    }
    
    public int getBaseSleep() {
        return this.baseSleep;
    }
    
    public int getSleepBonusDiffMult() {
        return this.sleepBonusDiffMult;
    }
    
    public boolean isKarmaMultProgress() {
        return this.isKarmaMultProgress;
    }
    
    public boolean isSleepMultNearby() {
        return this.isSleepMultNearby;
    }
    
    public String[] getMissionNames() {
        return this.missionNames;
    }
    
    public String getRandomMissionName() {
        return this.missionNames[Server.rand.nextInt(this.missionNames.length)];
    }
    
    public static final long getTimeReductionForMission(final byte missionType, final int missionDifficulty) {
        long toReturn = 14400000L;
        toReturn += 7200000L * missionDifficulty;
        return toReturn;
    }
}
