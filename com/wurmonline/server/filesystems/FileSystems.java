// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.filesystems;

import com.wurmonline.server.Constants;
import com.wurmonline.server.ServerDirInfo;

public final class FileSystems
{
    public static final AlphabeticalFileSystem creatureTemplates;
    public static final AlphabeticalFileSystem skillTemplates;
    public static final AlphabeticalFileSystem itemTemplates;
    public static final AlphaCreationalFileSystem playerStats;
    public static final MajorFileSystem creatureStats;
    public static final MajorFileSystem itemStats;
    public static final MajorFileSystem zoneStats;
    public static final MajorFileSystem itemOldStats;
    public static final MajorFileSystem creatureOldStats;
    public static final MajorFileSystem tileStats;
    
    public static String getCreatureTemplateDirFor(final String fileName) {
        return FileSystems.creatureTemplates.getDir(fileName);
    }
    
    public static String getSkillTemplateDirFor(final String fileName) {
        return FileSystems.skillTemplates.getDir(fileName);
    }
    
    public static String getItemTemplateDirFor(final String fileName) {
        return FileSystems.itemTemplates.getDir(fileName);
    }
    
    public static String getCreatureStateDirFor(final String fileName) {
        return FileSystems.creatureStats.getDir(fileName);
    }
    
    public static String getPlayerStateDirFor(final String fileName) {
        return FileSystems.playerStats.getDir(fileName);
    }
    
    public static String getItemStateDirFor(final String fileName) {
        return FileSystems.itemStats.getDir(fileName);
    }
    
    public static String getItemOldStateDirFor(final String fileName) {
        return FileSystems.itemOldStats.getDir(fileName);
    }
    
    public static String getTileStateDirFor(final String fileName) {
        return FileSystems.tileStats.getDir(fileName);
    }
    
    public static String getZoneStateDirFor(final String fileName) {
        return FileSystems.zoneStats.getDir(fileName);
    }
    
    static {
        creatureTemplates = new AlphabeticalFileSystem(ServerDirInfo.getFileDBPath() + Constants.creatureTemplatesDBPath);
        skillTemplates = new AlphabeticalFileSystem(ServerDirInfo.getFileDBPath() + Constants.skillTemplatesDBPath);
        itemTemplates = new AlphabeticalFileSystem(ServerDirInfo.getFileDBPath() + Constants.itemTemplatesDBPath);
        playerStats = new AlphaCreationalFileSystem(ServerDirInfo.getFileDBPath() + Constants.playerStatsDBPath);
        creatureStats = new MajorFileSystem(ServerDirInfo.getFileDBPath() + Constants.creatureStatsDBPath);
        itemStats = new MajorFileSystem(ServerDirInfo.getFileDBPath() + Constants.itemStatsDBPath);
        zoneStats = new MajorFileSystem(ServerDirInfo.getFileDBPath() + Constants.zonesDBPath);
        itemOldStats = new MajorFileSystem(ServerDirInfo.getFileDBPath() + Constants.itemOldStatsDBPath);
        creatureOldStats = new MajorFileSystem(ServerDirInfo.getFileDBPath() + Constants.creatureOldStatsDBPath);
        tileStats = new MajorFileSystem(ServerDirInfo.getFileDBPath() + Constants.tileStatsDBPath);
    }
}
