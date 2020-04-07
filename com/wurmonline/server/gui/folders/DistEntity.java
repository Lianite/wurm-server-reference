// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.gui.folders;

import java.io.IOException;
import java.nio.file.Path;

public enum DistEntity
{
    Recipes("recipes", true), 
    Adventure("Adventure", true), 
    Creative("Creative", true), 
    Migrations("migrations", true);
    
    private FolderEntity entity;
    
    private DistEntity(final String name, final boolean required) {
        this.entity = new FolderEntity(name, required);
    }
    
    @Override
    public String toString() {
        return this.entity.toString();
    }
    
    public String filename() {
        return this.entity.getFilename();
    }
    
    public boolean isRequired() {
        return this.entity.isRequired();
    }
    
    public boolean existsIn(final DistFolder folder) {
        return this.entity.existsIn(folder.getPath());
    }
    
    public boolean existsIn(final Path path) {
        return this.entity.existsIn(path);
    }
    
    public void createIn(final DistFolder folder) throws IOException {
        this.entity.createIn(folder.getPath());
    }
    
    public void createIn(final Path path) throws IOException {
        this.entity.createIn(path);
    }
    
    public void deleteFrom(final DistFolder folder) throws IOException {
        this.entity.deleteFrom(folder.getPath());
    }
    
    public void deleteFrom(final Path path) throws IOException {
        this.entity.deleteFrom(path);
    }
}
