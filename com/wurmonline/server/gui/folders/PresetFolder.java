// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.gui.folders;

import java.io.IOException;
import java.nio.file.FileVisitor;
import java.nio.file.CopyOption;
import java.nio.file.StandardCopyOption;
import javax.annotation.Nullable;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.util.logging.Logger;

public class PresetFolder extends Folder
{
    private static final Logger logger;
    private boolean original;
    
    public PresetFolder(final Path path, final boolean original) {
        this(path);
        this.original = original;
    }
    
    public PresetFolder(final Path path) {
        super(path);
    }
    
    @Nullable
    public static PresetFolder fromPath(final Path path) {
        if (path == null) {
            return null;
        }
        if (!Files.isDirectory(path, new LinkOption[0])) {
            return null;
        }
        for (final PresetEntity entity : PresetEntity.values()) {
            if (entity.isRequired() && !entity.existsIn(path)) {
                return null;
            }
        }
        return new PresetFolder(path, PresetEntity.OriginalDir.existsIn(path));
    }
    
    public final String getError() {
        for (final PresetEntity entity : PresetEntity.values()) {
            if (entity.isRequired() && !entity.existsIn(this)) {
                return "Preset folder missing: " + entity.filename();
            }
        }
        return "";
    }
    
    @Override
    public boolean delete() {
        return !this.original && super.delete();
    }
    
    public boolean isOriginal() {
        return this.original;
    }
    
    public boolean copyTo(final Path path) {
        if (!Files.exists(path, new LinkOption[0]) || !this.exists()) {
            return false;
        }
        for (final PresetEntity entity : PresetEntity.values()) {
            if (entity != PresetEntity.OriginalDir && entity.existsIn(this.path)) {
                try {
                    if (Files.isDirectory(this.path.resolve(entity.filename()), new LinkOption[0])) {
                        Files.walkFileTree(this.path.resolve(entity.filename()), new CopyDirVisitor(this.path.resolve(entity.filename()), path.resolve(entity.filename()), StandardCopyOption.REPLACE_EXISTING));
                    }
                    else {
                        Files.copy(this.path.resolve(entity.filename()), path.resolve(entity.filename()), StandardCopyOption.REPLACE_EXISTING);
                    }
                }
                catch (IOException e) {
                    PresetFolder.logger.warning("Unable to copy " + entity.filename() + " from " + this.path.toString() + " to " + path.toString());
                    e.printStackTrace();
                    return false;
                }
            }
        }
        return true;
    }
    
    static {
        logger = Logger.getLogger(PresetFolder.class.getName());
    }
}
