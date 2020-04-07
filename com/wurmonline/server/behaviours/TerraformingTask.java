// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.behaviours;

import com.wurmonline.server.meshgen.ImprovedNoise;
import java.util.HashMap;
import java.util.HashSet;
import com.wurmonline.server.zones.FocusZone;
import com.wurmonline.server.LoginHandler;
import com.wurmonline.server.zones.Den;
import com.wurmonline.server.kingdom.Kingdom;
import com.wurmonline.server.kingdom.Kingdoms;
import com.wurmonline.server.Servers;
import com.wurmonline.server.villages.Village;
import com.wurmonline.server.villages.Villages;
import com.wurmonline.server.structures.Fence;
import com.wurmonline.server.structures.Wall;
import com.wurmonline.server.zones.VolaTile;
import com.wurmonline.server.creatures.Creature;
import java.util.Iterator;
import com.wurmonline.server.items.ItemFactory;
import com.wurmonline.server.Players;
import java.util.Set;
import java.util.Map;
import java.util.logging.Level;
import com.wurmonline.server.meshgen.IslandAdder;
import com.wurmonline.mesh.Tiles;
import com.wurmonline.server.zones.Zones;
import com.wurmonline.server.Server;
import java.util.logging.Logger;
import com.wurmonline.mesh.MeshIO;
import java.util.Random;
import com.wurmonline.shared.constants.ItemMaterials;
import com.wurmonline.server.MiscConstants;

public class TerraformingTask implements MiscConstants, ItemMaterials
{
    private int counter;
    private final int task;
    private final byte kingdom;
    private final int entityId;
    private final String entityName;
    private int startX;
    private int startY;
    private int startHeight;
    private TerraformingTask next;
    private final int tasksRemaining;
    private int totalTasks;
    private final Random random;
    private final boolean firstTask;
    public static final int ERUPT = 0;
    public static final int INDENT = 1;
    public static final int PLATEAU = 2;
    public static final int CRATERS = 3;
    public static final int MULTIPLATEAU = 4;
    public static final int RAVINE = 5;
    public static final int ISLAND = 6;
    public static final int MULTIRAVINE = 7;
    private int radius;
    private int length;
    private int direction;
    private MeshIO topLayer;
    private MeshIO rockLayer;
    private final String[] prefixes;
    private final String[] suffixes;
    private static final Logger logger;
    
    public TerraformingTask(final int whatToDo, final byte targetKingdom, final String epicEntityName, final int epicEntityId, int tasksLeft, final boolean isFirstTask) {
        this.counter = 0;
        this.startX = 0;
        this.startY = 0;
        this.startHeight = 0;
        this.next = null;
        this.totalTasks = 0;
        this.random = new Random();
        this.radius = 0;
        this.length = 0;
        this.direction = 0;
        this.prefixes = new String[] { "Et", "De", "Old", "Gaz", "Mak", "Fir", "Fyre", "Eld", "Vagn", "Mag", "Lav", "Volc", "Rad", "Ash", "Ask" };
        this.suffixes = new String[] { "na", "cuse", "fir", "egap", "dire", "haul", "vann", "un", "lik", "ingan", "enken", "mosh", "kil", "atrask", "eskap" };
        this.task = whatToDo;
        this.kingdom = targetKingdom;
        this.entityName = epicEntityName;
        this.entityId = epicEntityId;
        if (tasksLeft < 0) {
            if (this.task == 3) {
                final int numCratersRoot = 1;
                this.totalTasks = 1 + 1 * this.random.nextInt(Math.max(1, 1));
                tasksLeft = this.totalTasks;
            }
            else if (this.task == 4 || this.task == 7) {
                this.totalTasks = 1 + this.random.nextInt(2);
                tasksLeft = this.totalTasks;
            }
        }
        this.firstTask = isFirstTask;
        this.tasksRemaining = tasksLeft;
        this.startX = 0;
        this.startY = 0;
        Server.getInstance().addTerraformingTask(this);
    }
    
    public void setSXY(final int sx, final int sy) {
        this.startX = sx;
        this.startY = sy;
    }
    
    private void setHeight(final int sheight) {
        this.startHeight = sheight;
    }
    
    private void setTotalTasks(final int total) {
        this.totalTasks = total;
    }
    
    public final boolean setCoordinates() {
        boolean toReturn = false;
        switch (this.task) {
            case 0: {
                toReturn = this.eruptCoord();
                break;
            }
            case 1: {
                toReturn = this.indentCoord();
                break;
            }
            case 2:
            case 4: {
                toReturn = this.plateauCoord();
                break;
            }
            case 3: {
                toReturn = this.craterCoord();
                break;
            }
            case 5:
            case 7: {
                toReturn = this.ravineCoord();
                break;
            }
            case 6: {
                toReturn = this.islandCoord();
                break;
            }
            default: {
                toReturn = false;
                break;
            }
        }
        return toReturn;
    }
    
    public boolean poll() {
        if (this.next != null) {
            return this.next.poll();
        }
        if (this.counter == 0) {
            if (this.startX != 0 && this.startY != 0) {
                this.sendEffect();
            }
            else {
                if (this.startX != 0 || this.startY != 0 || !this.setCoordinates()) {
                    return true;
                }
                this.sendEffect();
            }
        }
        if (this.counter == 60) {
            this.terraform();
            if (this.tasksRemaining == 0) {
                return true;
            }
        }
        if (this.counter == 65 && this.tasksRemaining > 0) {
            (this.next = new TerraformingTask(this.task, this.kingdom, this.entityName, this.entityId, this.tasksRemaining - 1, false)).setCoordinates();
            this.next.setSXY(this.startX, this.startY);
            if (this.task == 4 || this.task == 7) {
                if (this.random.nextBoolean()) {
                    int modx = (this.startX + this.radius - (this.startX - this.radius)) / (1 + this.random.nextInt(4));
                    int mody = (this.startY + this.radius - (this.startY - this.radius)) / (1 + this.random.nextInt(4));
                    if (this.random.nextBoolean()) {
                        modx = -modx;
                    }
                    if (this.random.nextBoolean()) {
                        mody = -mody;
                    }
                    if (this.startX + modx > Zones.worldTileSizeX - 200) {
                        this.startX -= 200;
                    }
                    if (this.startY + mody > Zones.worldTileSizeY - 200) {
                        this.startY -= 200;
                    }
                    if (this.startX + modx < 200) {
                        this.startX += 200;
                    }
                    if (this.startY + mody < 200) {
                        this.startY += 200;
                    }
                    this.next.setSXY(this.startX + modx, this.startY + mody);
                }
            }
            else if (this.task == 3) {
                final int modx = (int)(this.random.nextGaussian() * 3.0 * this.radius);
                final int mody = (int)(this.random.nextGaussian() * 3.0 * this.radius);
                if (this.startX + modx > Zones.worldTileSizeX - 200) {
                    this.startX -= modx;
                }
                if (this.startY + mody > Zones.worldTileSizeY - 200) {
                    this.startY -= mody;
                }
                if (this.startX + modx < 200) {
                    this.startX += modx;
                }
                if (this.startY + mody < 200) {
                    this.startY += mody;
                }
                this.next.setSXY(this.startX + modx, this.startY + mody);
            }
            this.next.setHeight(this.startHeight);
            this.next.setTotalTasks(this.totalTasks);
        }
        ++this.counter;
        return false;
    }
    
    private void terraform() {
        switch (this.task) {
            case 0: {
                this.erupt();
                break;
            }
            case 1: {
                this.indent();
                break;
            }
            case 2:
            case 4: {
                this.plateau();
                break;
            }
            case 3: {
                this.crater();
                break;
            }
            case 5:
            case 7: {
                this.ravine();
                break;
            }
            case 6: {
                this.island();
                break;
            }
        }
    }
    
    private final boolean ravineCoord() {
        boolean toReturn = false;
        this.radius = 5 + this.random.nextInt(5);
        this.length = 20 + this.random.nextInt(40);
        this.direction = this.random.nextInt(8);
        for (int runs = 0; runs < 20; ++runs) {
            if (!this.firstTask) {
                return true;
            }
            this.startX = this.random.nextInt(Zones.worldTileSizeX);
            this.startY = this.random.nextInt(Zones.worldTileSizeY);
            if (this.startX > Zones.worldTileSizeX - 200) {
                this.startX -= 200;
            }
            if (this.startY > Zones.worldTileSizeY - 200) {
                this.startY -= 200;
            }
            if (this.startX < 200) {
                this.startX += 200;
            }
            if (this.startY < 200) {
                this.startY += 200;
            }
            if (Tiles.decodeHeight(Server.surfaceMesh.getTile(this.startX, this.startY)) > 0 && this.isOutsideOwnKingdom(this.startX, this.startY)) {
                toReturn = true;
                break;
            }
        }
        return toReturn;
    }
    
    private void ravine() {
        if (this.totalTasks > 0 && this.totalTasks % 2 == 0) {
            this.direction = this.totalTasks;
        }
        final IslandAdder isl = new IslandAdder(Server.surfaceMesh, Server.rockMesh);
        Map<Integer, Set<Integer>> changes = null;
        changes = isl.createRavine(Zones.safeTileX(this.startX - this.radius), Zones.safeTileY(this.startY - this.radius), this.length, this.direction);
        TerraformingTask.logger.log(Level.INFO, "Ravine at " + this.startX + "," + this.startY);
        if (changes != null) {
            int minx = Zones.worldTileSizeX;
            int miny = Zones.worldTileSizeY;
            int maxx = 0;
            int maxy = 0;
            for (final Map.Entry<Integer, Set<Integer>> me : changes.entrySet()) {
                final Integer x = me.getKey();
                if (x < minx) {
                    minx = x;
                }
                if (x > maxx) {
                    maxx = x;
                }
                final Set<Integer> set = me.getValue();
                for (final Integer y : set) {
                    if (y < miny) {
                        miny = y;
                    }
                    if (y > maxy) {
                        maxy = y;
                    }
                    Terraforming.forceSetAsRock(x, y, Tiles.Tile.TILE_CAVE_WALL_ORE_GLIMMERSTEEL.id, 100);
                    this.changeTile(x, y);
                    Players.getInstance().sendChangedTile(x, y, true, true);
                    destroyStructures(x, y);
                }
            }
            try {
                ItemFactory.createItem(696, 99.0f, (minx + (maxx - minx) / 2) * 4 + 2, (miny + (maxy - miny) / 2) * 4 + 2, this.random.nextFloat() * 350.0f, true, (byte)57, (byte)0, -10L, null);
            }
            catch (Exception ex) {
                TerraformingTask.logger.log(Level.WARNING, ex.getMessage(), ex);
            }
        }
    }
    
    private static final void destroyStructures(final int x, final int y) {
        final VolaTile t = Zones.getTileOrNull(x, y, true);
        if (t != null) {
            final short[] steepness = Creature.getTileSteepness(x, y, true);
            if (t.getStructure() != null && steepness[1] > 40) {
                for (final Wall w : t.getWalls()) {
                    w.setAsPlan();
                }
            }
            for (final Fence f : t.getFences()) {
                if (steepness[1] > 40) {
                    f.destroy();
                }
            }
        }
    }
    
    private final boolean craterCoord() {
        boolean toReturn = false;
        this.radius = 10 + this.random.nextInt(20);
        for (int runs = 0; runs < 20; ++runs) {
            if (!this.firstTask || this.startX > 0 || this.startY > 0) {
                return true;
            }
            this.startX = this.random.nextInt(Zones.worldTileSizeX);
            this.startY = this.random.nextInt(Zones.worldTileSizeY);
            if (Tiles.decodeHeight(Server.surfaceMesh.getTile(this.startX, this.startY)) > 0 && this.isOutsideOwnKingdom(this.startX, this.startY)) {
                toReturn = true;
                break;
            }
        }
        return toReturn;
    }
    
    public final void changeTile(final int x, final int y) {
        final VolaTile tile1 = Zones.getTileOrNull(x, y, true);
        if (tile1 != null) {
            final Creature[] creatures;
            final Creature[] crets = creatures = tile1.getCreatures();
            for (final Creature lCret2 : creatures) {
                lCret2.setChangedTileCounter();
            }
            tile1.change();
        }
        final VolaTile tile2 = Zones.getTileOrNull(x, y, false);
        if (tile2 != null) {
            final Creature[] creatures2;
            final Creature[] crets2 = creatures2 = tile2.getCreatures();
            for (final Creature lCret3 : creatures2) {
                lCret3.setChangedTileCounter();
            }
            tile2.change();
        }
    }
    
    private void crater() {
        boolean ok = true;
        if (this.radius == 0) {
            this.radius = 10 + this.random.nextInt(20);
        }
        for (int x = 0; x < 10; ++x) {
            final int sx = Zones.safeTileX(this.startX - this.radius);
            final int sy = Zones.safeTileY(this.startY - this.radius);
            final int ex = Zones.safeTileX(this.startX + this.radius);
            final int ey = Zones.safeTileY(this.startY + this.radius);
            final Set<Village> blockers = Villages.getVillagesWithin(sx, sy, ex, ey);
            if (blockers == null || blockers.size() == 0) {
                ok = true;
                break;
            }
            for (final Village v : blockers) {
                TerraformingTask.logger.log(Level.WARNING, v.getName() + " is in the way at " + sx + "," + sy + " to " + ex + "," + ey);
            }
            ok = false;
            final int modx = (int)(this.random.nextGaussian() * this.radius);
            final int mody = (int)(this.random.nextGaussian() * this.radius);
            if (this.startX + modx > Zones.worldTileSizeX - 200) {
                this.startX -= modx;
            }
            if (this.startY + mody > Zones.worldTileSizeY - 200) {
                this.startY -= mody;
            }
            if (this.startX + modx < 200) {
                this.startX += modx;
            }
            if (this.startY + mody < 200) {
                this.startY += mody;
            }
            if (Servers.localServer.testServer) {
                TerraformingTask.logger.log(Level.INFO, "MOdx=" + modx + ", mody=" + mody + " radius=" + this.radius + " yields sx=" + (this.startX + modx) + " sy " + (this.startY + mody));
            }
            this.setSXY(this.startX + modx, this.startY + mody);
        }
        if (!ok) {
            TerraformingTask.logger.log(Level.INFO, "Avoiding Crater at " + this.startX + "," + this.startY + " radius=" + this.radius);
            return;
        }
        Map<Integer, Set<Integer>> changes = null;
        final IslandAdder isl = new IslandAdder(Server.surfaceMesh, Server.rockMesh);
        final int sx2 = Zones.safeTileX(this.startX - this.radius);
        final int sy2 = Zones.safeTileY(this.startY - this.radius);
        final int ex2 = Zones.safeTileX(this.startX + this.radius);
        final int ey2 = Zones.safeTileY(this.startY + this.radius);
        changes = isl.createCrater(sx2, sy2, ex2, ey2);
        TerraformingTask.logger.log(Level.INFO, "Crater at " + this.startX + "," + this.startY + " radius=" + this.radius);
        if (changes != null) {
            int minx = Zones.worldTileSizeX;
            int miny = Zones.worldTileSizeY;
            int maxx = 0;
            int maxy = 0;
            for (final Map.Entry<Integer, Set<Integer>> me : changes.entrySet()) {
                final Integer x2 = me.getKey();
                if (x2 < minx) {
                    minx = x2;
                }
                if (x2 > maxx) {
                    maxx = x2;
                }
                final Set<Integer> set = me.getValue();
                for (final Integer y : set) {
                    if (y < miny) {
                        miny = y;
                    }
                    if (y > maxy) {
                        maxy = y;
                    }
                    Terraforming.forceSetAsRock(x2, y, Tiles.Tile.TILE_CAVE_WALL_ORE_GLIMMERSTEEL.id, 100);
                    this.changeTile(x2, y);
                    Players.getInstance().sendChangedTile(x2, y, true, true);
                    destroyStructures(x2, y);
                }
            }
            try {
                ItemFactory.createItem(696, 99.0f, this.startX * 4 + 2, this.startY * 4 + 2, this.random.nextFloat() * 350.0f, true, (byte)57, (byte)0, -10L, null);
            }
            catch (Exception exs) {
                TerraformingTask.logger.log(Level.WARNING, exs.getMessage(), exs);
            }
        }
    }
    
    private final boolean indentCoord() {
        boolean toReturn = false;
        this.radius = 10 + this.random.nextInt(20);
        for (int runs = 0; runs < 20; ++runs) {
            this.startX = this.random.nextInt(Zones.worldTileSizeX);
            this.startY = this.random.nextInt(Zones.worldTileSizeY);
            if (Tiles.decodeHeight(Server.surfaceMesh.getTile(this.startX, this.startY)) > 0 && this.isOutsideOwnKingdom(this.startX, this.startY)) {
                toReturn = true;
                break;
            }
        }
        return toReturn;
    }
    
    private void indent() {
        Map<Integer, Set<Integer>> changes = null;
        final IslandAdder isl = new IslandAdder(Server.surfaceMesh, Server.rockMesh);
        changes = isl.createRockIndentation(Zones.safeTileX(this.startX - this.radius), Zones.safeTileY(this.startY - this.radius), Zones.safeTileX(this.startX + this.radius), Zones.safeTileY(this.startY + this.radius));
        TerraformingTask.logger.log(Level.INFO, "Rock Indentation at " + this.startX + "," + this.startY);
        if (changes != null) {
            int minx = Zones.worldTileSizeX;
            int miny = Zones.worldTileSizeY;
            int maxx = 0;
            int maxy = 0;
            for (final Map.Entry<Integer, Set<Integer>> me : changes.entrySet()) {
                final Integer x = me.getKey();
                if (x < minx) {
                    minx = x;
                }
                if (x > maxx) {
                    maxx = x;
                }
                final Set<Integer> set = me.getValue();
                for (final Integer y : set) {
                    if (y < miny) {
                        miny = y;
                    }
                    if (y > maxy) {
                        maxy = y;
                    }
                    Terraforming.forceSetAsRock(x, y, (byte)1, 100);
                    this.changeTile(x, y);
                    Players.getInstance().sendChangedTile(x, y, true, true);
                    destroyStructures(x, y);
                }
            }
        }
    }
    
    private final boolean isOutsideOwnKingdom(final int tilex, final int tiley) {
        final byte kingdomId = Zones.getKingdom(tilex, tiley);
        final Kingdom k = Kingdoms.getKingdom(kingdomId);
        return k == null || k.getTemplate() != this.kingdom;
    }
    
    private boolean eruptCoord() {
        boolean toReturn = false;
        final int maxTries = 20;
        for (int x = 0; x < 20; ++x) {
            final Den d = Zones.getRandomTop();
            this.radius = 10 + this.random.nextInt(20);
            if (this.startX > 0 || this.startY > 0) {
                break;
            }
            if (d != null && this.isOutsideOwnKingdom(d.getTilex(), d.getTiley())) {
                this.startX = d.getTilex();
                this.startY = d.getTiley();
                toReturn = true;
                break;
            }
        }
        return toReturn;
    }
    
    private void erupt() {
        final IslandAdder isl = new IslandAdder(Server.surfaceMesh, Server.rockMesh);
        Map<Integer, Set<Integer>> changes = null;
        changes = isl.createVolcano(Zones.safeTileX(this.startX - this.radius), Zones.safeTileY(this.startY - this.radius), Zones.safeTileX(this.startX + this.radius), Zones.safeTileY(this.startY + this.radius));
        TerraformingTask.logger.log(Level.INFO, "Volcano Eruption at " + this.startX + "," + this.startY);
        if (changes != null) {
            int minx = Zones.worldTileSizeX;
            int miny = Zones.worldTileSizeY;
            int maxx = 0;
            int maxy = 0;
            for (final Map.Entry<Integer, Set<Integer>> me : changes.entrySet()) {
                final Integer x = me.getKey();
                if (x < minx) {
                    minx = x;
                }
                if (x > maxx) {
                    maxx = x;
                }
                final Set<Integer> set = me.getValue();
                for (final Integer y : set) {
                    if (y < miny) {
                        miny = y;
                    }
                    if (y > maxy) {
                        maxy = y;
                    }
                    Terraforming.forceSetAsRock(x, y, Tiles.Tile.TILE_CAVE_WALL_ORE_ADAMANTINE.id, 100);
                    this.changeTile(x, y);
                    Players.getInstance().sendChangedTile(x, y, true, true);
                    Players.getInstance().sendChangedTile(x, y, false, true);
                    destroyStructures(x, y);
                }
            }
            String name = "Unknown";
            if (Server.rand.nextBoolean()) {
                name = this.prefixes[Server.rand.nextInt(this.prefixes.length)];
                if (Server.rand.nextInt(10) > 0) {
                    name += this.suffixes[Server.rand.nextInt(this.suffixes.length)];
                }
            }
            if (Server.rand.nextBoolean()) {
                name = this.suffixes[Server.rand.nextInt(this.suffixes.length)];
                if (Server.rand.nextInt(10) > 0) {
                    name += this.prefixes[Server.rand.nextInt(this.prefixes.length)];
                }
            }
            name = LoginHandler.raiseFirstLetter(name);
            new FocusZone(minx, maxx, miny, maxy, (byte)1, name, "", true);
        }
    }
    
    private final boolean plateauCoord() {
        boolean toReturn = false;
        this.radius = 10 + this.random.nextInt(20);
        for (int runs = 0; runs < 20; ++runs) {
            if (!this.firstTask) {
                return true;
            }
            this.startX = this.random.nextInt(Zones.worldTileSizeX);
            this.startY = this.random.nextInt(Zones.worldTileSizeY);
            this.startHeight = 200;
            if (Tiles.decodeHeight(Server.surfaceMesh.getTile(this.startX, this.startY)) > 0 && this.isOutsideOwnKingdom(this.startX, this.startY)) {
                toReturn = true;
                break;
            }
        }
        return toReturn;
    }
    
    private void plateau() {
        int modx = 0;
        int mody = 0;
        boolean ok = true;
        if (!this.firstTask) {
            for (int x = 0; x < 20; ++x) {
                modx = (this.startX + this.radius - (this.startX - this.radius)) / (1 + this.random.nextInt(4));
                mody = (this.startY + this.radius - (this.startY - this.radius)) / (1 + this.random.nextInt(4));
                if (this.random.nextBoolean()) {
                    modx = -modx;
                }
                if (this.random.nextBoolean()) {
                    mody = -mody;
                }
                final int sx = Zones.safeTileX(this.startX + modx - this.radius);
                final int ex = Zones.safeTileX(this.startX + modx + this.radius);
                final int sy = Zones.safeTileY(this.startY + mody - this.radius);
                final int ey = Zones.safeTileY(this.startY + mody + this.radius);
                final Set<Village> vills = Villages.getVillagesWithin(sx, sy, ex, ey);
                if (vills == null || vills.size() == 0) {
                    ok = true;
                    break;
                }
                ok = false;
            }
        }
        if (!ok) {
            TerraformingTask.logger.log(Level.INFO, "Skipping Plateu at " + this.startX + "," + this.startY);
            return;
        }
        final int sx2 = Zones.safeTileX(this.startX + modx - this.radius);
        final int ex2 = Zones.safeTileX(this.startX + modx + this.radius);
        final int sy2 = Zones.safeTileY(this.startY + mody - this.radius);
        final int ey2 = Zones.safeTileY(this.startY + mody + this.radius);
        final IslandAdder isl = new IslandAdder(Server.surfaceMesh, Server.rockMesh);
        Map<Integer, Set<Integer>> changes = null;
        changes = isl.createPlateau(sx2, sy2, ex2, ey2, this.startHeight + this.random.nextInt(150));
        TerraformingTask.logger.log(Level.INFO, "Plateu at " + this.startX + "," + this.startY);
        if (changes != null) {
            int minx = Zones.worldTileSizeX;
            int miny = Zones.worldTileSizeY;
            int maxx = 0;
            int maxy = 0;
            for (final Map.Entry<Integer, Set<Integer>> me : changes.entrySet()) {
                final Integer x2 = me.getKey();
                if (x2 < minx) {
                    minx = x2;
                }
                if (x2 > maxx) {
                    maxx = x2;
                }
                final Set<Integer> set = me.getValue();
                for (final Integer y : set) {
                    if (y < miny) {
                        miny = y;
                    }
                    if (y > maxy) {
                        maxy = y;
                    }
                    this.changeTile(x2, y);
                    Players.getInstance().sendChangedTile(x2, y, true, true);
                    destroyStructures(x2, y);
                }
            }
        }
    }
    
    public void sendEffect() {
        final int tx = 0;
        final int ty = 0;
        switch (this.task) {
            case 0: {
                Players.getInstance().sendGlobalNonPersistantComplexEffect(-10L, (short)12, this.startX, this.startY, Tiles.decodeHeightAsFloat(Server.surfaceMesh.getTile(this.startX, this.startY)), this.radius, this.direction, this.length, this.kingdom, (byte)this.entityId);
                break;
            }
            case 1: {
                Players.getInstance().sendGlobalNonPersistantComplexEffect(-10L, (short)13, this.startX, this.startY, Tiles.decodeHeightAsFloat(Server.surfaceMesh.getTile(this.startX, this.startY)), this.radius, this.direction, this.length, this.kingdom, (byte)this.entityId);
                break;
            }
            case 2:
            case 4: {
                Players.getInstance().sendGlobalNonPersistantComplexEffect(-10L, (short)11, this.startX, this.startY, Tiles.decodeHeightAsFloat(Server.surfaceMesh.getTile(this.startX, this.startY)), this.radius, this.direction, this.length, this.kingdom, (byte)this.entityId);
                break;
            }
            case 3: {
                Players.getInstance().sendGlobalNonPersistantComplexEffect(-10L, (short)10, this.startX, this.startY, Tiles.decodeHeightAsFloat(Server.surfaceMesh.getTile(0, 0)), this.radius, this.tasksRemaining, this.direction, this.kingdom, (byte)this.entityId);
                break;
            }
            case 5:
            case 7: {
                Players.getInstance().sendGlobalNonPersistantComplexEffect(-10L, (short)14, Zones.safeTileX(this.startX - this.radius), Zones.safeTileY(this.startY - this.radius), Tiles.decodeHeightAsFloat(Server.surfaceMesh.getTile(this.startX, this.startY)), this.radius, this.length, this.direction, this.kingdom, (byte)this.entityId);
                break;
            }
            case 6: {
                Players.getInstance().sendGlobalNonPersistantComplexEffect(-10L, (short)15, this.startX, this.startY, Tiles.decodeHeightAsFloat(Server.surfaceMesh.getTile(this.startX, this.startY)), this.radius, this.length, this.direction, this.kingdom, (byte)this.entityId);
                break;
            }
        }
    }
    
    private final boolean islandCoord() {
        this.rockLayer = Server.rockMesh;
        this.topLayer = Server.surfaceMesh;
        for (int minSize = Zones.worldTileSizeX / 15, i = 800; i >= minSize; --i) {
            for (int j = 0; j < 2; ++j) {
                final int height;
                final int width = height = i;
                final int x = this.random.nextInt(Zones.worldTileSizeX - width - 128) + 64;
                final int y = this.random.nextInt(Zones.worldTileSizeY - width - 128) + 64;
                if (this.isIslandOk(x, y, x + width, y + height)) {
                    this.startX = x + width / 2;
                    this.startY = y + width / 2;
                    this.length = width / 2;
                    this.radius = height / 2;
                    TerraformingTask.logger.info("Found island location " + i + " @ " + (x + width / 2) + ", " + (y + height / 2));
                    return true;
                }
            }
        }
        return false;
    }
    
    private void island() {
        Map<Integer, Set<Integer>> changes = null;
        changes = this.addIsland(this.startX - this.length, this.startX + this.length, this.startY - this.radius, this.startY + this.radius);
        if (changes != null) {
            for (final Map.Entry<Integer, Set<Integer>> me : changes.entrySet()) {
                final Integer x = me.getKey();
                final Set<Integer> set = me.getValue();
                for (final Integer y : set) {
                    this.changeTile(x, y);
                    Players.getInstance().sendChangedTile(x, y, true, true);
                    destroyStructures(x, y);
                }
            }
        }
    }
    
    public Map<Integer, Set<Integer>> addToChanges(final Map<Integer, Set<Integer>> changes, final int x, final int y) {
        Set<Integer> s = changes.get(x);
        if (s == null) {
            s = new HashSet<Integer>();
        }
        if (!s.contains(y)) {
            s.add(y);
        }
        changes.put(x, s);
        return changes;
    }
    
    public final boolean isIslandOk(final int x0, final int y0, final int x1, final int y1) {
        final int xm = (x1 + x0) / 2;
        final int ym = (y1 + y0) / 2;
        for (int x2 = x0; x2 < x1; ++x2) {
            final double xd = (x2 - xm) * 2.0 / (x1 - x0);
            for (int y2 = y0; y2 < y1; ++y2) {
                final double yd = (y2 - ym) * 2.0 / (y1 - y0);
                final double d = Math.sqrt(xd * xd + yd * yd);
                if (d < 1.0) {
                    final int height = Tiles.decodeHeight(this.topLayer.data[x2 | y2 << this.topLayer.getSizeLevel()]);
                    if (height > -2) {
                        return false;
                    }
                }
            }
        }
        return true;
    }
    
    public Map<Integer, Set<Integer>> addIsland(final int x0, final int y0, final int x1, final int y1) {
        final int xm = (x1 + x0) / 2;
        final int ym = (y1 + y0) / 2;
        final double dirOffs = this.random.nextDouble() * 3.141592653589793 * 2.0;
        final int branchCount = this.random.nextInt(7) + 3;
        Map<Integer, Set<Integer>> changes = new HashMap<Integer, Set<Integer>>();
        final float[] branches = new float[branchCount];
        for (int i = 0; i < branchCount; ++i) {
            branches[i] = this.random.nextFloat() * 0.25f + 0.75f;
        }
        final ImprovedNoise noise = new ImprovedNoise(this.random.nextLong());
        for (int x2 = x0; x2 < x1; ++x2) {
            final double xd = (x2 - xm) * 2.0 / (x1 - x0);
            for (int y2 = y0; y2 < y1; ++y2) {
                final double yd = (y2 - ym) * 2.0 / (y1 - y0);
                final double od = Math.sqrt(xd * xd + yd * yd);
                double dir;
                for (dir = (Math.atan2(yd, xd) + 3.141592653589793) / 6.283185307179586 + dirOffs; dir < 0.0; ++dir) {}
                while (dir >= 1.0) {
                    --dir;
                }
                final int branch = (int)(dir * branchCount);
                final float step = (float)dir * branchCount - branch;
                final float last = branches[branch];
                final float nextBranch = branches[(branch + 1) % branchCount];
                final float pow = last + (nextBranch - last) * step;
                double d = od;
                d /= pow;
                if (d < 1.0) {
                    d *= d;
                    d *= d;
                    d = 1.0 - d;
                    final int height = Tiles.decodeHeight(this.topLayer.data[x2 | y2 << this.topLayer.getSizeLevel()]);
                    float n = (float)(noise.perlinNoise(x2, y2) * 64.0) + 100.0f;
                    n *= 2.0f;
                    int hh = (int)(height + (n - height) * d);
                    byte type = Tiles.Tile.TILE_DIRT.id;
                    if (hh > 5 && this.random.nextInt(100) == 0) {
                        type = Tiles.Tile.TILE_GRASS.id;
                    }
                    if (hh > 0) {
                        hh += (int)0.07f;
                    }
                    else {
                        hh -= (int)0.07f;
                    }
                    this.topLayer.data[x2 | y2 << this.topLayer.getSizeLevel()] = Tiles.encode((short)hh, type, (byte)0);
                    changes = this.addToChanges(changes, x2, y2);
                }
            }
        }
        for (int x2 = x0; x2 < x1; ++x2) {
            final double xd = (x2 - xm) * 2.0 / (x1 - x0);
            for (int y2 = y0; y2 < y1; ++y2) {
                final double yd = (y2 - ym) * 2.0 / (y1 - y0);
                double d2 = Math.sqrt(xd * xd + yd * yd);
                final double od2 = d2 * (x1 - x0);
                double dir2;
                for (dir2 = (Math.atan2(yd, xd) + 3.141592653589793) / 6.283185307179586 + dirOffs; dir2 < 0.0; ++dir2) {}
                while (dir2 >= 1.0) {
                    --dir2;
                }
                final int branch2 = (int)(dir2 * branchCount);
                final float step2 = (float)dir2 * branchCount - branch2;
                final float last2 = branches[branch2];
                final float nextBranch2 = branches[(branch2 + 1) % branchCount];
                final float pow2 = last2 + (nextBranch2 - last2) * step2;
                d2 /= pow2;
                final int height = Tiles.decodeHeight(this.topLayer.data[x2 | y2 << this.topLayer.getSizeLevel()]);
                int dd = this.rockLayer.data[x2 | y2 << this.topLayer.getSizeLevel()];
                float hh2 = height / 10.0f - 8.0f;
                d2 = 1.0 - d2;
                if (d2 < 0.0) {
                    d2 = 0.0;
                }
                d2 = Math.sin(d2 * 3.141592653589793) * 2.0 - 1.0;
                if (d2 < 0.0) {
                    d2 = 0.0;
                }
                float n2 = (float)noise.perlinNoise(x2 / 2.0, y2 / 2.0);
                if (n2 > 0.5f) {
                    n2 -= (n2 - 0.5f) * 2.0f;
                }
                n2 /= 0.5f;
                if (n2 < 0.0f) {
                    n2 = 0.0f;
                }
                hh2 += (float)(n2 * (x1 - x0) / 8.0f * d2);
                this.rockLayer.data[x2 | y2 << this.topLayer.getSizeLevel()] = Tiles.encode(hh2, Tiles.decodeType(dd), Tiles.decodeData(dd));
                changes = this.addToChanges(changes, x2, y2);
                float ddd = (float)od2 / 16.0f;
                if (ddd < 1.0f) {
                    ddd = ddd * 2.0f - 1.0f;
                    if (ddd > 1.0f) {
                        ddd = 1.0f;
                    }
                    if (ddd < 0.0f) {
                        ddd = 0.0f;
                    }
                    dd = this.topLayer.data[x2 | y2 << this.topLayer.getSizeLevel()];
                    final float hh3 = Tiles.decodeHeightAsFloat(this.topLayer.data[x2 | y2 << this.topLayer.getSizeLevel()]);
                    hh2 = Tiles.decodeHeightAsFloat(this.rockLayer.data[x2 | y2 << this.topLayer.getSizeLevel()]);
                    hh2 += (hh3 - hh2) * ddd;
                    this.topLayer.data[x2 | y2 << this.topLayer.getSizeLevel()] = Tiles.encode(hh2, Tiles.decodeType(dd), Tiles.decodeData(dd));
                    changes = this.addToChanges(changes, x2, y2);
                }
                else {
                    dd = this.topLayer.data[x2 | y2 << this.topLayer.getSizeLevel()];
                    hh2 = Tiles.decodeHeightAsFloat(this.topLayer.data[x2 | y2 << this.topLayer.getSizeLevel()]);
                    hh2 = hh2 * 0.5f + (int)hh2 / 2 * 2 * 0.5f;
                    if (hh2 > 0.0f) {
                        hh2 += 0.07f;
                    }
                    else {
                        hh2 -= 0.07f;
                    }
                    this.topLayer.data[x2 | y2 << this.topLayer.getSizeLevel()] = Tiles.encode(hh2, Tiles.decodeType(dd), Tiles.decodeData(dd));
                    changes = this.addToChanges(changes, x2, y2);
                }
            }
        }
        for (int x2 = x0; x2 < x1; ++x2) {
            final double xd = (x2 - xm) * 2.0 / (x1 - x0);
            for (int y2 = y0; y2 < y1; ++y2) {
                final double yd = (y2 - ym) * 2.0 / (y1 - y0);
                final double d2 = Math.sqrt(xd * xd + yd * yd);
                final double od2 = d2 * (x1 - x0);
                boolean rock = true;
                for (int xx = 0; xx < 2; ++xx) {
                    for (int yy = 0; yy < 2; ++yy) {
                        final int height2 = Tiles.decodeHeight(this.topLayer.data[x2 | y2 << this.topLayer.getSizeLevel()]);
                        final int groundHeight = Tiles.decodeHeight(this.rockLayer.data[x2 | y2 << this.topLayer.getSizeLevel()]);
                        if (groundHeight < height2) {
                            rock = false;
                        }
                        else {
                            final int dd2 = this.topLayer.data[x2 | y2 << this.topLayer.getSizeLevel()];
                            this.topLayer.data[x2 | y2 << this.topLayer.getSizeLevel()] = Tiles.encode((short)groundHeight, Tiles.decodeType(dd2), Tiles.decodeData(dd2));
                            changes = this.addToChanges(changes, x2, y2);
                        }
                    }
                }
                if (rock) {
                    final float ddd2 = (float)od2 / 16.0f;
                    if (ddd2 < 1.0f) {
                        final int dd3 = this.topLayer.data[x2 | y2 << this.topLayer.getSizeLevel()];
                        this.topLayer.data[x2 | y2 << this.topLayer.getSizeLevel()] = Tiles.encode(Tiles.decodeHeight(dd3), Tiles.Tile.TILE_LAVA.id, (byte)(-1));
                        changes = this.addToChanges(changes, x2, y2);
                    }
                    else {
                        final int dd3 = this.topLayer.data[x2 | y2 << this.topLayer.getSizeLevel()];
                        this.topLayer.data[x2 | y2 << this.topLayer.getSizeLevel()] = Tiles.encode(Tiles.decodeHeight(dd3), Tiles.Tile.TILE_ROCK.id, (byte)0);
                        changes = this.addToChanges(changes, x2, y2);
                    }
                }
            }
        }
        return changes;
    }
    
    static {
        logger = Logger.getLogger(TerraformingTask.class.getName());
    }
}
