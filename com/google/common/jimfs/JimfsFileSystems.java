// 
// Decompiled by Procyon v0.5.30
// 

package com.google.common.jimfs;

import java.util.Map;
import java.nio.file.attribute.FileAttribute;
import java.util.HashMap;
import java.io.IOException;
import java.nio.file.FileSystem;
import java.lang.reflect.Method;
import java.lang.reflect.InvocationTargetException;
import java.net.URI;
import java.util.Iterator;
import java.nio.file.spi.FileSystemProvider;

final class JimfsFileSystems
{
    private static final FileSystemProvider systemJimfsProvider;
    private static final Runnable DO_NOTHING;
    
    private static FileSystemProvider getSystemJimfsProvider() {
        for (final FileSystemProvider provider : FileSystemProvider.installedProviders()) {
            if (provider.getScheme().equals("jimfs")) {
                return provider;
            }
        }
        return null;
    }
    
    private static Runnable removeFileSystemRunnable(final URI uri) {
        if (JimfsFileSystems.systemJimfsProvider == null) {
            return JimfsFileSystems.DO_NOTHING;
        }
        try {
            final Method method = JimfsFileSystems.systemJimfsProvider.getClass().getDeclaredMethod("removeFileSystemRunnable", URI.class);
            return (Runnable)method.invoke(JimfsFileSystems.systemJimfsProvider, uri);
        }
        catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException ex2) {
            final ReflectiveOperationException ex;
            final ReflectiveOperationException e = ex;
            throw new RuntimeException("Unable to get Runnable for removing the FileSystem from the cache when it is closed", e);
        }
    }
    
    public static JimfsFileSystem newFileSystem(final JimfsFileSystemProvider provider, final URI uri, final Configuration config) throws IOException {
        final PathService pathService = new PathService(config);
        final FileSystemState state = new FileSystemState(removeFileSystemRunnable(uri));
        final JimfsFileStore fileStore = createFileStore(config, pathService, state);
        final FileSystemView defaultView = createDefaultView(config, fileStore, pathService);
        final WatchServiceConfiguration watchServiceConfig = config.watchServiceConfig;
        final JimfsFileSystem fileSystem = new JimfsFileSystem(provider, uri, fileStore, pathService, defaultView, watchServiceConfig);
        pathService.setFileSystem(fileSystem);
        return fileSystem;
    }
    
    private static JimfsFileStore createFileStore(final Configuration config, final PathService pathService, final FileSystemState state) {
        final AttributeService attributeService = new AttributeService(config);
        final HeapDisk disk = new HeapDisk(config);
        final FileFactory fileFactory = new FileFactory(disk);
        final Map<Name, Directory> roots = new HashMap<Name, Directory>();
        for (final String root : config.roots) {
            final JimfsPath path = pathService.parsePath(root, new String[0]);
            if (!path.isAbsolute() && path.getNameCount() == 0) {
                throw new IllegalArgumentException("Invalid root path: " + root);
            }
            final Name rootName = path.root();
            final Directory rootDir = fileFactory.createRootDirectory(rootName);
            attributeService.setInitialAttributes(rootDir, (FileAttribute<?>[])new FileAttribute[0]);
            roots.put(rootName, rootDir);
        }
        return new JimfsFileStore(new FileTree(roots), fileFactory, disk, attributeService, config.supportedFeatures, state);
    }
    
    private static FileSystemView createDefaultView(final Configuration config, final JimfsFileStore fileStore, final PathService pathService) throws IOException {
        final JimfsPath workingDirPath = pathService.parsePath(config.workingDirectory, new String[0]);
        Directory dir = fileStore.getRoot(workingDirPath.root());
        if (dir == null) {
            throw new IllegalArgumentException("Invalid working dir path: " + workingDirPath);
        }
        for (final Name name : workingDirPath.names()) {
            final Directory newDir = fileStore.directoryCreator().get();
            fileStore.setInitialAttributes(newDir, (FileAttribute<?>[])new FileAttribute[0]);
            dir.link(name, newDir);
            dir = newDir;
        }
        return new FileSystemView(fileStore, dir, workingDirPath);
    }
    
    static {
        systemJimfsProvider = getSystemJimfsProvider();
        DO_NOTHING = new Runnable() {
            @Override
            public void run() {
            }
        };
    }
}
