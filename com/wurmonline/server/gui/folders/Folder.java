// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.gui.folders;

import java.nio.file.CopyOption;
import java.util.Comparator;
import java.nio.file.FileVisitOption;
import java.nio.file.LinkOption;
import java.nio.file.attribute.FileAttribute;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.logging.Logger;

public class Folder
{
    private static final Logger logger;
    Path path;
    String name;
    
    public Folder(final Path path) {
        this.path = path;
        this.name = this.path.getName(path.getNameCount() - 1).toString();
    }
    
    public final Path getPath() {
        return this.path;
    }
    
    public final String getName() {
        return this.name;
    }
    
    public boolean isEmpty() throws IOException {
        try (final DirectoryStream<Path> dirStream = Files.newDirectoryStream(this.path)) {
            return !dirStream.iterator().hasNext();
        }
    }
    
    public boolean create() {
        if (this.exists()) {
            return true;
        }
        try {
            Files.createDirectory(this.path, (FileAttribute<?>[])new FileAttribute[0]);
        }
        catch (IOException ex) {
            Folder.logger.warning("Exception creating " + this.path.toString());
            ex.printStackTrace();
            return false;
        }
        return true;
    }
    
    public boolean exists() {
        return Files.exists(this.path, new LinkOption[0]);
    }
    
    public boolean delete() {
        if (!this.exists()) {
            return true;
        }
        try {
            Files.walk(this.path, new FileVisitOption[0]).sorted(Comparator.reverseOrder()).forEach(path -> {
                try {
                    Files.delete(path);
                }
                catch (IOException ex) {
                    Folder.logger.warning("Exception deleting " + this.path.toString());
                    ex.printStackTrace();
                }
                return;
            });
        }
        catch (IOException ex2) {
            Folder.logger.warning("Exception deleting " + this.path.toString());
            ex2.printStackTrace();
            return false;
        }
        return true;
    }
    
    public void move(final Path path) throws IOException {
        Files.move(this.path, path, new CopyOption[0]);
    }
    
    static {
        logger = Logger.getLogger(Folder.class.getName());
    }
}
