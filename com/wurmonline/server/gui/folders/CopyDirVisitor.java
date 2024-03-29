// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.gui.folders;

import java.io.IOException;
import java.nio.file.attribute.FileAttribute;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.FileVisitResult;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.CopyOption;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;

public class CopyDirVisitor extends SimpleFileVisitor<Path>
{
    private final Path fromPath;
    private final Path toPath;
    private final CopyOption copyOption;
    
    public CopyDirVisitor(final Path fromPath, final Path toPath, final CopyOption copyOption) {
        this.fromPath = fromPath;
        this.toPath = toPath;
        this.copyOption = copyOption;
    }
    
    @Override
    public FileVisitResult preVisitDirectory(final Path dir, final BasicFileAttributes attrs) throws IOException {
        final Path target = this.toPath.resolve(this.fromPath.relativize(dir));
        if (!Files.exists(target, new LinkOption[0])) {
            Files.createDirectory(target, (FileAttribute<?>[])new FileAttribute[0]);
        }
        return FileVisitResult.CONTINUE;
    }
    
    @Override
    public FileVisitResult visitFile(final Path file, final BasicFileAttributes attrs) throws IOException {
        Files.copy(file, this.toPath.resolve(this.fromPath.relativize(file)), this.copyOption);
        return FileVisitResult.CONTINUE;
    }
}
