// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.gui.folders;

import javax.annotation.Nullable;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.util.logging.Logger;

public class DistFolder extends Folder
{
    private static final Logger logger;
    
    public DistFolder(final Path path) {
        super(path);
    }
    
    @Nullable
    public static DistFolder fromPath(final Path path) {
        if (path == null) {
            return null;
        }
        if (!Files.isDirectory(path, new LinkOption[0])) {
            return null;
        }
        for (final DistEntity entity : DistEntity.values()) {
            if (entity.isRequired() && !entity.existsIn(path)) {
                DistFolder.logger.warning("Dist folder missing " + entity.filename());
                return null;
            }
        }
        return new DistFolder(path);
    }
    
    public final Path getPathFor(final DistEntity entity) {
        return this.getPath().resolve(entity.filename());
    }
    
    static {
        logger = Logger.getLogger(PresetFolder.class.getName());
    }
}
