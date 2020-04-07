// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.filesystems;

import java.io.File;

public class AlphabeticalFileSystem extends MajorFileSystem
{
    public AlphabeticalFileSystem(final String aRootDir) {
        super(aRootDir);
    }
    
    @Override
    public String getDir(final String fileName) {
        final String firstLetter = fileName.substring(0, 1);
        final String secondLetters = fileName.substring(0, 2);
        final String dir1 = firstLetter.toLowerCase();
        final String dir2 = secondLetters.toLowerCase();
        final String fileDir = this.rootDir + File.separator + dir1 + File.separator + dir2 + File.separator;
        final File saveDir = new File(fileDir);
        if (!saveDir.exists()) {
            saveDir.mkdirs();
        }
        return fileDir;
    }
}
