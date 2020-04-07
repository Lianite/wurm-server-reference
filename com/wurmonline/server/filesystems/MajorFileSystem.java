// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.filesystems;

import java.util.List;
import java.util.Collection;
import java.util.LinkedList;
import java.io.File;

public class MajorFileSystem
{
    protected String rootDir;
    
    public MajorFileSystem(final String aRootDir) {
        this.rootDir = null;
        this.rootDir = aRootDir;
        if (this.rootDir.length() == 0) {
            this.rootDir = ".";
        }
    }
    
    public String getDir(final String fileName) {
        final int hashCode = fileName.hashCode();
        final int dir1 = hashCode >> 24 & 0xFF;
        final int dir2 = hashCode >> 16 & 0xFF;
        final int dir3 = hashCode >> 8 & 0xFF;
        final String fileDir = this.rootDir + File.separator + dir1 + File.separator + dir2 + File.separator + dir3 + File.separator;
        final File saveDir = new File(fileDir);
        if (!saveDir.exists()) {
            saveDir.mkdirs();
        }
        return fileDir;
    }
    
    public File[] getAllFiles() {
        final List<File> files = new LinkedList<File>();
        final File[] dirFiles = new File(this.rootDir).listFiles();
        for (int x = 0; x < dirFiles.length; ++x) {
            if (dirFiles[x].isDirectory()) {
                files.addAll(this.getAllFiles(dirFiles[x]));
            }
            else {
                files.add(dirFiles[x]);
            }
        }
        final File[] toReturn = new File[files.size()];
        return files.toArray(toReturn);
    }
    
    private List<File> getAllFiles(final File dir) {
        final List<File> files = new LinkedList<File>();
        final File[] dirFiles = dir.listFiles();
        for (int x = 0; x < dirFiles.length; ++x) {
            if (dirFiles[x].isDirectory()) {
                files.addAll(this.getAllFiles(dirFiles[x]));
            }
            else {
                files.add(dirFiles[x]);
            }
        }
        return files;
    }
}
