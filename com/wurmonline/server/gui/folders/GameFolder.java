// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.gui.folders;

import java.nio.file.FileVisitor;
import java.nio.file.CopyOption;
import java.nio.file.StandardCopyOption;
import java.io.IOException;
import javax.annotation.Nullable;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.util.logging.Logger;

public class GameFolder extends Folder
{
    private static final Logger logger;
    boolean current;
    
    public GameFolder(final Path path, final boolean current) {
        this(path);
        this.current = current;
    }
    
    public GameFolder(final Path path) {
        super(path);
    }
    
    @Nullable
    public static GameFolder fromPath(final Path path) {
        if (path == null) {
            return null;
        }
        if (!Files.isDirectory(path, new LinkOption[0])) {
            return null;
        }
        if (!GameEntity.GameDir.existsIn(path)) {
            return null;
        }
        return new GameFolder(path, GameEntity.CurrentDir.existsIn(path));
    }
    
    @Override
    public boolean create() {
        if (!super.create()) {
            return false;
        }
        if (GameEntity.GameDir.existsIn(this.path)) {
            return true;
        }
        try {
            GameEntity.GameDir.createIn(this.path);
        }
        catch (IOException ex) {
            GameFolder.logger.warning("Could not create gamedir in " + this.path.toString());
            ex.printStackTrace();
            return false;
        }
        return true;
    }
    
    public final String getError() {
        for (final GameEntity entity : GameEntity.values()) {
            if (entity.isRequired() && !entity.existsIn(this)) {
                return "Game folder missing: " + entity.filename();
            }
        }
        return "";
    }
    
    public final boolean isCurrent() {
        return this.current;
    }
    
    public final boolean setCurrent(final boolean isCurrent) {
        try {
            this.current = isCurrent;
            if (isCurrent && !GameEntity.CurrentDir.existsIn(this)) {
                GameEntity.CurrentDir.createIn(this);
            }
            else if (!isCurrent && GameEntity.CurrentDir.existsIn(this)) {
                GameEntity.CurrentDir.deleteFrom(this);
            }
        }
        catch (IOException ex) {
            GameFolder.logger.warning("Unable to set current game folder");
            ex.printStackTrace();
            return false;
        }
        return true;
    }
    
    public boolean copyTo(final Path path) {
        if (!Files.exists(path, new LinkOption[0]) || !this.exists()) {
            return false;
        }
        for (final GameEntity entity : GameEntity.values()) {
            if (entity != GameEntity.CurrentDir && entity != GameEntity.GameDir && entity.existsIn(this.path)) {
                try {
                    if (Files.isDirectory(this.path.resolve(entity.filename()), new LinkOption[0])) {
                        Files.walkFileTree(this.path.resolve(entity.filename()), new CopyDirVisitor(this.path.resolve(entity.filename()), path.resolve(entity.filename()), StandardCopyOption.REPLACE_EXISTING));
                    }
                    else {
                        Files.copy(this.path.resolve(entity.filename()), path.resolve(entity.filename()), StandardCopyOption.REPLACE_EXISTING);
                    }
                }
                catch (IOException e) {
                    GameFolder.logger.warning("Unable to copy " + entity.filename() + " from " + this.path.toString() + " to " + path.toString());
                    e.printStackTrace();
                    return false;
                }
            }
        }
        return true;
    }
    
    public Path getPathFor(final GameEntity entity) {
        return this.getPath().resolve(entity.filename());
    }
    
    static {
        logger = Logger.getLogger(GameFolder.class.getName());
    }
}
