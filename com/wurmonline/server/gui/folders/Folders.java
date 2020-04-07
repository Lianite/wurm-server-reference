// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.gui.folders;

import java.nio.file.attribute.FileAttribute;
import java.nio.file.LinkOption;
import java.util.Iterator;
import java.nio.file.DirectoryStream;
import java.io.IOException;
import java.nio.file.Files;
import javax.annotation.Nullable;
import java.util.Collection;
import java.util.ArrayList;
import java.nio.file.Paths;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.logging.Logger;

public enum Folders
{
    INSTANCE;
    
    private static final Logger logger;
    private HashMap<String, GameFolder> gameFolders;
    private HashMap<String, PresetFolder> presets;
    private GameFolder current;
    private DistFolder dist;
    private Path distPath;
    private Path gamesPath;
    private Path presetsPath;
    
    private Folders() {
        this.gameFolders = new HashMap<String, GameFolder>();
        this.presets = new HashMap<String, PresetFolder>();
        this.distPath = Paths.get(System.getProperty("wurm.distRoot", "./dist"), new String[0]);
        this.gamesPath = Paths.get(System.getProperty("wurm.gameFolderRoot", "."), new String[0]);
        this.presetsPath = Paths.get(System.getProperty("wurm.presetsRoot", "./presets"), new String[0]);
    }
    
    public static Folders getInstance() {
        return Folders.INSTANCE;
    }
    
    public static ArrayList<GameFolder> getGameFolders() {
        final ArrayList<GameFolder> gameFolders = new ArrayList<GameFolder>();
        gameFolders.addAll(getInstance().gameFolders.values());
        return gameFolders;
    }
    
    @Nullable
    public static GameFolder getGameFolder(final String folderName) {
        return getInstance().gameFolders.get(folderName);
    }
    
    public static boolean setCurrent(final GameFolder gameFolder) {
        if (getInstance().current != null && !getInstance().current.setCurrent(false)) {
            return false;
        }
        getInstance().current = gameFolder;
        if (!gameFolder.setCurrent(true)) {
            return false;
        }
        Folders.logger.info("Current game folder: " + gameFolder.getName());
        return true;
    }
    
    public static void clear() {
        getInstance().gameFolders.clear();
        getInstance().current = null;
        Folders.logger.info("Game folders cleared.");
    }
    
    public static GameFolder getCurrent() {
        return getInstance().current;
    }
    
    public static boolean loadGames() {
        return loadGamesFrom(getInstance().gamesPath);
    }
    
    public static boolean loadGamesFrom(final Path parent) {
        if (!getInstance().gameFolders.isEmpty()) {
            getInstance().gameFolders = new HashMap<String, GameFolder>();
        }
        try (final DirectoryStream<Path> directoryStream = Files.newDirectoryStream(parent)) {
            for (final Path path : directoryStream) {
                final GameFolder gameFolder = GameFolder.fromPath(path);
                if (gameFolder == null) {
                    continue;
                }
                getInstance().gameFolders.put(gameFolder.getName(), gameFolder);
                if (!gameFolder.isCurrent()) {
                    continue;
                }
                if (getInstance().current == null) {
                    getInstance().current = gameFolder;
                }
                else {
                    if (!gameFolder.setCurrent(false)) {
                        return false;
                    }
                    continue;
                }
            }
        }
        catch (IOException ex) {
            Folders.logger.warning("IOException while reading game folders");
            ex.printStackTrace();
            return false;
        }
        return true;
    }
    
    public static boolean loadDist() {
        getInstance().dist = DistFolder.fromPath(getInstance().distPath);
        return getInstance().dist != null;
    }
    
    public static DistFolder getDist() {
        if (getInstance().dist == null && !loadDist()) {
            Folders.logger.warning("Unable to load 'dist' folder, please run Steam validation");
            return new DistFolder(getInstance().distPath);
        }
        return getInstance().dist;
    }
    
    public static boolean loadPresets() {
        if (!getInstance().presets.isEmpty()) {
            getInstance().presets = new HashMap<String, PresetFolder>();
        }
        if (getInstance().dist == null && !loadDist()) {
            Folders.logger.warning("Unable to load 'dist' folder, please run Steam validation");
            return false;
        }
        if (!loadPresetsFrom(getInstance().dist.getPath())) {
            Folders.logger.warning("Unable to load presets from 'dist', please run Steam validation");
            return false;
        }
        if (!Files.exists(getInstance().presetsPath, new LinkOption[0])) {
            try {
                Files.createDirectory(getInstance().presetsPath, (FileAttribute<?>[])new FileAttribute[0]);
            }
            catch (IOException ex) {
                Folders.logger.warning("Could not create presets folder");
                return false;
            }
        }
        return loadPresetsFrom(getInstance().presetsPath);
    }
    
    private static boolean loadPresetsFrom(final Path parent) {
        try (final DirectoryStream<Path> directoryStream = Files.newDirectoryStream(parent)) {
            for (final Path path : directoryStream) {
                final PresetFolder folder = PresetFolder.fromPath(path);
                if (folder == null) {
                    continue;
                }
                getInstance().presets.put(folder.getName(), folder);
            }
        }
        catch (IOException ex) {
            Folders.logger.warning("IOException while reading game folders");
            ex.printStackTrace();
            return false;
        }
        return true;
    }
    
    public static Path getGamesPath() {
        return getInstance().gamesPath;
    }
    
    public static void addGame(final GameFolder folder) {
        getInstance().gameFolders.put(folder.getName(), folder);
    }
    
    public static void removeGame(final GameFolder folder) {
        getInstance().gameFolders.remove(folder.getName());
    }
    
    static {
        logger = Logger.getLogger(Folders.class.getName());
    }
}
