// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.zones;

import com.wurmonline.server.highways.MethodsHighways;
import com.wurmonline.server.items.Item;
import com.wurmonline.server.villages.Village;
import com.wurmonline.server.highways.Routes;
import com.wurmonline.server.Items;
import com.wurmonline.server.Server;
import com.wurmonline.server.creatures.Creature;
import com.wurmonline.server.creatures.Creatures;
import java.awt.Graphics;
import com.wurmonline.mesh.Tiles;
import java.awt.image.BufferedImage;
import com.wurmonline.server.Point;
import java.io.IOException;
import java.awt.image.RenderedImage;
import javax.imageio.ImageIO;
import java.io.File;
import com.wurmonline.server.behaviours.MethodsFishing;
import java.util.logging.Level;
import com.wurmonline.mesh.MeshIO;
import java.awt.Color;
import java.util.logging.Logger;

public final class ZonesUtility
{
    private static final Logger logger;
    static final Color waystoneSurface;
    static final Color waystoneCave;
    static final Color catseye0Surface;
    static final Color catseye0Cave;
    static final Color catseye1Surface;
    static final Color catseye1Cave;
    static final Color catseye2Surface;
    static final Color catseye2Cave;
    static final Color catseye3Surface;
    static final Color catseye3Cave;
    
    public static void saveFishSpots(final MeshIO mesh) {
        ZonesUtility.logger.log(Level.INFO, "[FISH_SPOTS]: Started");
        final int size = 256;
        final Color[][] colours = new Color[256][256];
        for (int season = 0; season < 4; ++season) {
            final Point offset = MethodsFishing.getSeasonOffset(season);
            final Color bgColour = MethodsFishing.getBgColour(season);
            for (int x = 0; x < 128; ++x) {
                for (int y = 0; y < 128; ++y) {
                    colours[offset.getX() + x][offset.getY() + y] = bgColour;
                }
            }
            final Point[] specialSpots;
            final Point[] spots = specialSpots = MethodsFishing.getSpecialSpots(0, 0, season);
            for (final Point spot : specialSpots) {
                final Color fishColour = MethodsFishing.getFishColour(spot.getH());
                for (int x2 = spot.getX() - 5; x2 <= spot.getX() + 5; ++x2) {
                    for (int y2 = spot.getY() - 5; y2 <= spot.getY() + 5; ++y2) {
                        colours[offset.getX() + x2][offset.getY() + y2] = fishColour;
                    }
                }
            }
        }
        final BufferedImage bmi = makeBufferedImage(colours, 256);
        final String filename = "fishSpots.png";
        final File f = new File("fishSpots.png");
        try {
            ImageIO.write(bmi, "png", f);
            ZonesUtility.logger.log(Level.INFO, "[FISH_SPOTS]: Finished");
        }
        catch (IOException e) {
            ZonesUtility.logger.log(Level.WARNING, "[FISH_SPOTS]: Failed to produce fishSpots.png", e);
        }
    }
    
    public static void saveMapDump(final MeshIO mesh) {
        final int size = mesh.getSize();
        final Color[][] colors = new Color[size][size];
        int maxH = 0;
        int minH = 0;
        final float divider = 10.0f;
        for (int x = 0; x < size; ++x) {
            for (int y = 0; y < size; ++y) {
                final int tileId = mesh.getTile(x, y);
                final int height = Tiles.decodeHeight(tileId);
                if (height > maxH) {
                    maxH = height;
                }
                if (height < minH) {
                    minH = height;
                }
            }
        }
        maxH /= (int)10.0f;
        minH /= (int)10.0f;
        for (int x = 0; x < size; ++x) {
            for (int y = 0; y < size; ++y) {
                final int tileId = mesh.getTile(x, y);
                final byte type = Tiles.decodeType(tileId);
                final Tiles.Tile tile = Tiles.getTile(type);
                final int height2 = Tiles.decodeHeight(tileId);
                if (height2 > 0) {
                    if (type == 4) {
                        final float tenth = height2 / 10.0f;
                        final float percent = tenth / maxH;
                        final int step = (int)(150.0f * percent);
                        colors[x][y] = new Color(Math.min(200, 10 + step), Math.min(200, 10 + step), Math.min(200, 10 + step));
                    }
                    else if (type == 12) {
                        colors[x][y] = Color.MAGENTA;
                    }
                    else {
                        final float tenth = height2 / 10.0f;
                        final float percent = tenth / maxH;
                        final int step = (int)(190.0f * percent);
                        final Color c = tile.getColor();
                        colors[x][y] = new Color(c.getRed(), Math.min(255, c.getGreen() + step), c.getBlue());
                    }
                }
                else {
                    final float tenth = height2 / 10.0f;
                    final float percent = tenth / minH;
                    final int step = (int)(255.0f * percent);
                    colors[x][y] = new Color(0, 0, Math.max(20, 255 - Math.abs(step)));
                }
            }
        }
        final BufferedImage bmi = new BufferedImage(size * 4, size * 4, 2);
        final Graphics g = bmi.createGraphics();
        for (int x2 = 0; x2 < size; ++x2) {
            for (int y2 = 0; y2 < size; ++y2) {
                g.setColor(colors[x2][y2]);
                g.fillRect(x2 * 4, y2 * 4, 4, 4);
            }
        }
        final File f = new File("mapdump.png");
        try {
            ImageIO.write(bmi, "png", f);
        }
        catch (IOException e) {
            ZonesUtility.logger.log(Level.WARNING, "Failed to produce mapdump.png", e);
        }
    }
    
    public static void saveCreatureDistributionAsImg(final MeshIO mesh) {
        ZonesUtility.logger.log(Level.INFO, "[CREATURE_DUMP]: Started");
        final int size = mesh.getSize();
        final Color[][] colors = getHeightMap(size);
        final Creature[] creatures;
        final Creature[] crets = creatures = Creatures.getInstance().getCreatures();
        for (final Creature c : creatures) {
            if (c.isGuard()) {
                colors[c.getTileX()][c.getTileY()] = Color.decode("#006000");
            }
            else if (c.isBred()) {
                colors[c.getTileX()][c.getTileY()] = Color.CYAN;
            }
            else if (c.isDomestic()) {
                colors[c.getTileX()][c.getTileY()] = Color.YELLOW;
            }
            else {
                colors[c.getTileX()][c.getTileY()] = Color.RED;
            }
        }
        final BufferedImage bmi = makeBufferedImage(colors, size);
        final File f = new File("creatures.png");
        try {
            ImageIO.write(bmi, "png", f);
            ZonesUtility.logger.log(Level.INFO, "[CREATURE_DUMP]: Finished");
        }
        catch (IOException e) {
            ZonesUtility.logger.log(Level.WARNING, "[CREATURE_DUMP]: Failed to produce creatures.png", e);
        }
    }
    
    public static void saveMapWithMarkersAsImg() {
        ZonesUtility.logger.log(Level.INFO, "[MAP WITH MARKERS DUMP]: Started");
        final int size = Server.surfaceMesh.getSize();
        final Color[][] colours = getHeightMap(size);
        AddOptedInVillages(size, colours);
        AddMarkers(Items.getMarkers(), colours);
        final BufferedImage bmi = makeBufferedImage(colours, size);
        final File f = new File("markersdump.png");
        try {
            ImageIO.write(bmi, "png", f);
            ZonesUtility.logger.log(Level.INFO, "[MAP WITH MARKERS DUMP]: Finished");
        }
        catch (IOException e) {
            ZonesUtility.logger.log(Level.WARNING, "[MAP WITH MARKERS DUMP]: Failed to produce markers.png", e);
        }
    }
    
    public static void saveRoutesAsImg() {
        ZonesUtility.logger.log(Level.INFO, "[ROUTES_DUMP]: Started");
        final int size = Server.surfaceMesh.getSize();
        final Color[][] colours = getHeightMap(size);
        AddMarkers(Routes.getMarkers(), colours);
        final BufferedImage bmi = makeBufferedImage(colours, size);
        final File f = new File("routes.png");
        try {
            ImageIO.write(bmi, "png", f);
            ZonesUtility.logger.log(Level.INFO, "[ROUTES_DUMP]: Finished");
        }
        catch (IOException e) {
            ZonesUtility.logger.log(Level.WARNING, "[ROUTES_DUMP]: Failed to produce routes.png", e);
        }
    }
    
    public static void saveWaterTypesAsImg(final boolean onSurface) {
        ZonesUtility.logger.log(Level.INFO, "[WATER_TYPES_DUMP]: Started");
        final int size = Server.surfaceMesh.getSize();
        final Color[][] colours = getHeightMap(size);
        for (int x = 0; x < size; ++x) {
            for (int y = 0; y < size; ++y) {
                switch (WaterType.getWaterType(x, y, onSurface)) {
                    case 1: {
                        colours[x][y] = new Color(174, 168, 253);
                        break;
                    }
                    case 2: {
                        colours[x][y] = new Color(137, 128, 252);
                        break;
                    }
                    case 3: {
                        colours[x][y] = new Color(115, 150, 165);
                        break;
                    }
                    case 4: {
                        colours[x][y] = new Color(163, 200, 176);
                        break;
                    }
                    case 5: {
                        colours[x][y] = new Color(235, 200, 176);
                        break;
                    }
                    case 6: {
                        colours[x][y] = new Color(185, 150, 165);
                        break;
                    }
                }
            }
        }
        final BufferedImage bmi = makeBufferedImage(colours, size);
        final String filename = onSurface ? "waterSurfaceTypes.png" : "waterCaveTypes.png";
        final File f = new File(filename);
        try {
            ImageIO.write(bmi, "png", f);
            ZonesUtility.logger.log(Level.INFO, "[WATER_TYPES_DUMP]: Finished");
        }
        catch (IOException e) {
            ZonesUtility.logger.log(Level.WARNING, "[WATER_TYPES_DUMP]: Failed to produce (" + onSurface + ") waterTypes.png", e);
        }
    }
    
    private static Color[][] getHeightMap(final int size) {
        final Color[][] colours = new Color[size][size];
        int maxH = 0;
        int minH = 0;
        final float divider = 10.0f;
        for (int x = 0; x < size; ++x) {
            for (int y = 0; y < size; ++y) {
                final int tileId = Server.surfaceMesh.getTile(x, y);
                final int height = Tiles.decodeHeight(tileId);
                if (height > maxH) {
                    maxH = height;
                }
                if (height < minH) {
                    minH = height;
                }
            }
        }
        maxH /= (int)10.0f;
        minH /= (int)10.0f;
        for (int x = 0; x < size; ++x) {
            for (int y = 0; y < size; ++y) {
                final int tileId = Server.surfaceMesh.getTile(x, y);
                final byte type = Tiles.decodeType(tileId);
                final Tiles.Tile tile = Tiles.getTile(type);
                final int height2 = Tiles.decodeHeight(tileId);
                if (height2 > 0) {
                    if (type == 4) {
                        final float tenth = height2 / 10.0f;
                        final float percent = tenth / maxH;
                        final int step = (int)(150.0f * percent);
                        colours[x][y] = new Color(Math.min(200, 10 + step), Math.min(200, 10 + step), Math.min(200, 10 + step));
                    }
                    else if (type == 12) {
                        colours[x][y] = Color.MAGENTA;
                    }
                    else {
                        final float tenth = height2 / 10.0f;
                        final float percent = tenth / maxH;
                        final int step = (int)(190.0f * percent);
                        final Color c = tile.getColor();
                        colours[x][y] = new Color(c.getRed(), Math.min(255, c.getGreen() + step), c.getBlue());
                    }
                }
                else {
                    final float tenth = height2 / 10.0f;
                    final float percent = tenth / minH;
                    final int step = (int)(255.0f * percent);
                    colours[x][y] = new Color(0, 0, Math.max(20, 255 - Math.abs(step)));
                }
            }
        }
        return colours;
    }
    
    private static void AddOptedInVillages(final int size, final Color[][] colours) {
        for (int x = 0; x < size; ++x) {
            for (int y = 0; y < size; ++y) {
                final VolaTile tt = Zones.getOrCreateTile(x, y, true);
                final Village vill = (tt == null) ? null : tt.getVillage();
                if (vill != null && vill.isHighwayFound() && (x == vill.getStartX() || x == vill.getEndX() || y == vill.getStartY() || y == vill.getEndY())) {
                    colours[x][y] = new Color(255, 127, 39);
                }
            }
        }
    }
    
    private static void AddMarkers(final Item[] markers, final Color[][] colours) {
        for (final Item marker : markers) {
            final int id = marker.getTemplateId();
            Label_0364: {
                switch (id) {
                    case 1112: {
                        if (marker.isOnSurface()) {
                            addMarker(colours, marker, ZonesUtility.waystoneSurface);
                            break;
                        }
                        final Color colour = colours[marker.getTileX()][marker.getTileY()];
                        if (colour != ZonesUtility.waystoneSurface) {
                            addMarker(colours, marker, ZonesUtility.waystoneCave);
                        }
                        break;
                    }
                    case 1114: {
                        final Color colour = colours[marker.getTileX()][marker.getTileY()];
                        if (colour == ZonesUtility.waystoneSurface || colour == ZonesUtility.waystoneCave) {}
                        if (marker.isOnSurface()) {
                            switch (MethodsHighways.numberOfSetBits(marker.getAuxData())) {
                                case 0: {
                                    addMarker(colours, marker, ZonesUtility.catseye0Surface);
                                    break Label_0364;
                                }
                                case 1: {
                                    addMarker(colours, marker, ZonesUtility.catseye1Surface);
                                    break Label_0364;
                                }
                                default: {
                                    if (Routes.isCatseyeUsed(marker)) {
                                        addMarker(colours, marker, ZonesUtility.catseye3Surface);
                                        break Label_0364;
                                    }
                                    addMarker(colours, marker, ZonesUtility.catseye2Surface);
                                    break Label_0364;
                                }
                            }
                        }
                        else {
                            if (colour == ZonesUtility.catseye0Surface || colour == ZonesUtility.catseye2Surface || colour == ZonesUtility.catseye2Surface || colour == ZonesUtility.catseye3Surface) {
                                break;
                            }
                            switch (MethodsHighways.numberOfSetBits(marker.getAuxData())) {
                                case 0: {
                                    addMarker(colours, marker, ZonesUtility.catseye0Cave);
                                    break Label_0364;
                                }
                                case 1: {
                                    addMarker(colours, marker, ZonesUtility.catseye1Cave);
                                    break Label_0364;
                                }
                                default: {
                                    if (Routes.isCatseyeUsed(marker)) {
                                        addMarker(colours, marker, ZonesUtility.catseye3Cave);
                                        break Label_0364;
                                    }
                                    addMarker(colours, marker, ZonesUtility.catseye2Cave);
                                    break Label_0364;
                                }
                            }
                        }
                        break;
                    }
                }
            }
        }
    }
    
    private static void addMarker(final Color[][] colours, final Item marker, final Color colour) {
        final int x = marker.getTileX();
        final int y = marker.getTileY();
        addColour(colours, x - 1, y, colours[x][y] = colour);
        addColour(colours, x, y - 1, colour);
        addColour(colours, x - 1, y - 1, colour);
        final MeshIO mesh = marker.isOnSurface() ? Server.surfaceMesh : Server.caveMesh;
        for (int xx = 0; xx <= 1; ++xx) {
            for (int yy = 0; yy <= 1; ++yy) {
                if (xx != 0 && yy != 0) {
                    final int tileId = mesh.getTile(x + xx, y + yy);
                    final byte type = Tiles.decodeType(tileId);
                    if (Tiles.isRoadType(type) || type == 201) {
                        addColour(colours, x + xx, y + yy, colour);
                    }
                }
            }
        }
    }
    
    private static void addColour(final Color[][] colours, final int x, final int y, final Color colour) {
        if (colours[x][y] != ZonesUtility.waystoneSurface && colours[x][y] != ZonesUtility.waystoneCave) {
            colours[x][y] = colour;
        }
    }
    
    private static BufferedImage makeBufferedImage(final Color[][] colors, final int size) {
        final BufferedImage bmi = new BufferedImage(size, size, 2);
        final Graphics g = bmi.createGraphics();
        for (int x = 0; x < size; ++x) {
            for (int y = 0; y < size; ++y) {
                g.setColor(colors[x][y]);
                g.fillRect(x, y, 1, 1);
            }
        }
        return bmi;
    }
    
    public static void saveAsImg(final MeshIO mesh) {
        final long lStart = System.nanoTime();
        final int size = mesh.getSize();
        ZonesUtility.logger.info("Size:" + size);
        ZonesUtility.logger.info("Data Size: " + mesh.data.length);
        final byte[][] tiles = new byte[size][size];
        for (int x = 0; x < size; ++x) {
            for (int y = 0; y < size; ++y) {
                final int tileId = mesh.getTile(x, y);
                final byte type = Tiles.decodeType(tileId);
                tiles[x][y] = type;
            }
        }
        final BufferedImage bmi = new BufferedImage(size, size, 2);
        final Graphics g = bmi.createGraphics();
        for (int x2 = 0; x2 < size; ++x2) {
            for (int y2 = 0; y2 < size; ++y2) {
                boolean paint = true;
                if (tiles[x2][y2] == -34) {
                    g.setColor(Color.darkGray);
                }
                else if (tiles[x2][y2] == -50) {
                    g.setColor(Color.white);
                }
                else if (tiles[x2][y2] == -52) {
                    g.setColor(Color.ORANGE);
                }
                else if (tiles[x2][y2] == -36) {
                    g.setColor(Color.YELLOW);
                }
                else if (tiles[x2][y2] == -35) {
                    g.setColor(Color.GREEN);
                }
                else if (tiles[x2][y2] == -32) {
                    g.setColor(Color.CYAN);
                }
                else if (tiles[x2][y2] == -51) {
                    g.setColor(Color.MAGENTA);
                }
                else if (tiles[x2][y2] == -33) {
                    g.setColor(Color.PINK);
                }
                else if (tiles[x2][y2] == -30) {
                    g.setColor(Color.BLUE);
                }
                else if (tiles[x2][y2] == -31) {
                    g.setColor(Color.RED);
                }
                else {
                    paint = false;
                }
                if (paint) {
                    g.fillRect(x2, y2, 1, 1);
                }
            }
        }
        final File f = new File("Ore.png");
        try {
            ImageIO.write(bmi, "png", f);
        }
        catch (IOException e) {
            ZonesUtility.logger.log(Level.WARNING, "Failed to produce ore.png", e);
        }
        finally {
            final long lElapsedTime = System.nanoTime() - lStart;
            ZonesUtility.logger.info("Saved Mesh to '" + f.getAbsoluteFile() + "', that took " + lElapsedTime / 1000000.0f + ", millis.");
        }
    }
    
    static {
        logger = Logger.getLogger(ZonesUtility.class.getName());
        waystoneSurface = Color.CYAN;
        waystoneCave = new Color(0, 153, 153);
        catseye0Surface = new Color(255, 255, 255);
        catseye0Cave = new Color(224, 224, 224);
        catseye1Surface = new Color(255, 0, 0);
        catseye1Cave = new Color(204, 0, 0);
        catseye2Surface = new Color(0, 0, 255);
        catseye2Cave = new Color(0, 0, 153);
        catseye3Surface = new Color(0, 255, 0);
        catseye3Cave = new Color(0, 153, 0);
    }
}
