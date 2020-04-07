// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.gui.folders;

import java.io.IOException;
import java.nio.file.attribute.FileAttribute;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;

public class FolderEntity
{
    boolean required;
    String name;
    
    FolderEntity(final String name, final boolean required) {
        this.required = required;
        this.name = name;
    }
    
    @Override
    public String toString() {
        return this.name;
    }
    
    public String getFilename() {
        return this.name;
    }
    
    public boolean isRequired() {
        return this.required;
    }
    
    public boolean existsIn(final Path path) {
        return Files.exists(path.resolve(this.getFilename()), new LinkOption[0]);
    }
    
    public void createIn(final Path path) throws IOException {
        Files.createFile(path.resolve(this.getFilename()), (FileAttribute<?>[])new FileAttribute[0]);
    }
    
    public void deleteFrom(final Path path) throws IOException {
        Files.delete(path.resolve(this.getFilename()));
    }
}
