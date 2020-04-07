// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server;

import java.nio.file.Paths;
import com.wurmonline.server.gui.folders.GameEntity;
import java.io.File;
import java.nio.file.Path;

public class ServerDirInfo
{
    private static String constantsFileName;
    private static String fileDBPath;
    private static Path path;
    
    public static String getConstantsFileName() {
        return ServerDirInfo.constantsFileName;
    }
    
    public static String getFileDBPath() {
        return ServerDirInfo.fileDBPath;
    }
    
    public static void setFileDBPath(final String fileDBPath) {
        ServerDirInfo.fileDBPath = fileDBPath;
    }
    
    public static void setPath(final Path path) {
        ServerDirInfo.path = path;
        ServerDirInfo.fileDBPath = path.toString() + File.separator;
        ServerDirInfo.constantsFileName = path.resolve(GameEntity.WurmINI.filename()).toString();
    }
    
    public static Path getPath() {
        return ServerDirInfo.path;
    }
    
    static {
        ServerDirInfo.constantsFileName = "wurm.ini";
        ServerDirInfo.fileDBPath = "wurmDB" + File.separator;
        ServerDirInfo.path = Paths.get("wurmDB", new String[0]);
    }
}
