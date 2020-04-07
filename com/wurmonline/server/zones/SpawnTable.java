// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.zones;

import java.util.HashSet;
import java.util.logging.Level;
import com.wurmonline.server.Servers;
import com.wurmonline.mesh.Tiles;
import java.util.Iterator;
import java.util.Set;
import java.util.logging.Logger;
import com.wurmonline.server.creatures.CreatureTemplateIds;

public final class SpawnTable implements CreatureTemplateIds
{
    private static final Logger logger;
    private static final Set<EncounterType> land;
    private static final Set<EncounterType> water;
    private static final Set<EncounterType> beach;
    private static final Set<EncounterType> deepwater;
    private static final Set<EncounterType> flying;
    private static final Set<EncounterType> flyinghigh;
    private static final Set<EncounterType> caves;
    
    private static void addTileType(final EncounterType type) {
        if (type.getElev() == 0) {
            SpawnTable.land.add(type);
        }
        else if (type.getElev() == 2) {
            SpawnTable.deepwater.add(type);
        }
        else if (type.getElev() == 1) {
            SpawnTable.water.add(type);
        }
        else if (type.getElev() == 3) {
            SpawnTable.flying.add(type);
        }
        else if (type.getElev() == 4) {
            SpawnTable.flyinghigh.add(type);
        }
        else if (type.getElev() == -1) {
            SpawnTable.caves.add(type);
        }
        else if (type.getElev() == 5) {
            SpawnTable.beach.add(type);
        }
        else {
            SpawnTable.logger.warning("Cannot add unknown EncounterType: " + type);
        }
    }
    
    public static EncounterType getType(final byte tiletype, final byte elevation) {
        Set<EncounterType> types = SpawnTable.land;
        if (elevation == 2) {
            types = SpawnTable.deepwater;
        }
        else if (elevation == 1) {
            types = SpawnTable.water;
        }
        else if (elevation == 3) {
            types = SpawnTable.flying;
        }
        else if (elevation == 4) {
            types = SpawnTable.flyinghigh;
        }
        else if (elevation == -1) {
            types = SpawnTable.caves;
        }
        else if (elevation == 5) {
            types = SpawnTable.beach;
        }
        for (final EncounterType type : types) {
            if (type.getTiletype() == tiletype) {
                return type;
            }
        }
        return null;
    }
    
    public static Encounter getRandomEncounter(final byte tiletype, final byte elevation) {
        final EncounterType enc = getType(tiletype, elevation);
        if (enc == null) {
            return null;
        }
        final Encounter t = enc.getRandomEncounter();
        if (t == EncounterType.NULL_ENCOUNTER) {
            return null;
        }
        return t;
    }
    
    static void createEncounters() {
        SpawnTable.logger.info("Creating Encounters");
        final long now = System.nanoTime();
        final Encounter cow = new Encounter();
        cow.addType(3, 1);
        final Encounter sheep = new Encounter();
        sheep.addType(96, 1);
        final Encounter ram = new Encounter();
        sheep.addType(102, 1);
        final Encounter anaconda = new Encounter();
        anaconda.addType(38, 1);
        final Encounter horse = new Encounter();
        horse.addType(64, 2);
        final Encounter wolf = new Encounter();
        wolf.addType(10, 4);
        final Encounter bearbrown = new Encounter();
        bearbrown.addType(12, 2);
        final Encounter bearblack = new Encounter();
        bearblack.addType(42, 2);
        final Encounter rat = new Encounter();
        rat.addType(13, 3);
        final Encounter mountainlion = new Encounter();
        mountainlion.addType(14, 2);
        final Encounter wildcat = new Encounter();
        wildcat.addType(15, 2);
        final Encounter cavebugs = new Encounter();
        cavebugs.addType(43, 5);
        final Encounter pig = new Encounter();
        pig.addType(44, 3);
        final Encounter deer = new Encounter();
        deer.addType(54, 2);
        final Encounter bison = new Encounter();
        bison.addType(82, 10);
        final Encounter bull = new Encounter();
        bull.addType(49, 3);
        final Encounter calf = new Encounter();
        calf.addType(50, 1);
        final Encounter hen = new Encounter();
        hen.addType(45, 3);
        final Encounter rooster = new Encounter();
        rooster.addType(52, 1);
        final Encounter pheasant = new Encounter();
        pheasant.addType(55, 2);
        final Encounter dog = new Encounter();
        dog.addType(51, 2);
        final Encounter spider = new Encounter();
        spider.addType(25, 6);
        final Encounter lavaSpiderMulti = new Encounter();
        lavaSpiderMulti.addType(56, 10);
        final Encounter lavaSpiderSingle = new Encounter();
        lavaSpiderSingle.addType(56, 1);
        final Encounter scorpionSingle = new Encounter();
        scorpionSingle.addType(59, 3);
        final Encounter crocodileSingle = new Encounter();
        crocodileSingle.addType(58, 1);
        final Encounter lavaCreatureSingle = new Encounter();
        lavaCreatureSingle.addType(57, 1);
        final Encounter trollSingle = new Encounter();
        trollSingle.addType(11, 1);
        final Encounter hellHorseSingle = new Encounter();
        hellHorseSingle.addType(83, 1);
        final Encounter hellHoundSingle = new Encounter();
        hellHoundSingle.addType(84, 1);
        final Encounter hellScorpSingle = new Encounter();
        hellScorpSingle.addType(85, 1);
        final Encounter sealPair = new Encounter();
        sealPair.addType(93, 2);
        final Encounter crabSingle = new Encounter();
        crabSingle.addType(95, 1);
        final Encounter tortoiseSingle = new Encounter();
        tortoiseSingle.addType(94, 1);
        final Encounter uttacha = new Encounter();
        uttacha.addType(74, 1);
        final Encounter eagleSpirit = new Encounter();
        eagleSpirit.addType(77, 1);
        final Encounter crawler = new Encounter();
        crawler.addType(73, 1);
        final Encounter nogump = new Encounter();
        nogump.addType(75, 1);
        final Encounter drakeSpirit = new Encounter();
        drakeSpirit.addType(76, 1);
        final Encounter demonsol = new Encounter();
        demonsol.addType(72, 1);
        final Encounter unicorn = new Encounter();
        unicorn.addType(21, 1);
        final EncounterType grassGround = new EncounterType(Tiles.Tile.TILE_GRASS.id, (byte)0);
        grassGround.addEncounter(cow, 1);
        grassGround.addEncounter(wildcat, 2);
        grassGround.addEncounter(dog, 3);
        grassGround.addEncounter(hen, 1);
        grassGround.addEncounter(rooster, 1);
        grassGround.addEncounter(calf, 1);
        grassGround.addEncounter(bull, 1);
        grassGround.addEncounter(pheasant, 1);
        grassGround.addEncounter(horse, 2);
        grassGround.addEncounter(sheep, 3);
        grassGround.addEncounter(ram, 1);
        grassGround.addEncounter(unicorn, 1);
        addTileType(grassGround);
        final EncounterType beachSide = new EncounterType(Tiles.Tile.TILE_SAND.id, (byte)5);
        beachSide.addEncounter(crabSingle, 8);
        beachSide.addEncounter(tortoiseSingle, 1);
        beachSide.addEncounter(sealPair, 3);
        if (Servers.localServer.isChallengeServer()) {
            beachSide.addEncounter(crawler, 1);
            beachSide.addEncounter(uttacha, 1);
        }
        addTileType(beachSide);
        final EncounterType rockSide = new EncounterType(Tiles.Tile.TILE_ROCK.id, (byte)1);
        rockSide.addEncounter(rat, 2);
        rockSide.addEncounter(sealPair, 2);
        rockSide.addEncounter(lavaCreatureSingle, 1);
        rockSide.addEncounter(EncounterType.NULL_ENCOUNTER, 5);
        if (Servers.localServer.isChallengeServer()) {
            rockSide.addEncounter(uttacha, 1);
        }
        addTileType(rockSide);
        final EncounterType mycGround = new EncounterType(Tiles.Tile.TILE_MYCELIUM.id, (byte)0);
        mycGround.addEncounter(spider, 4);
        mycGround.addEncounter(rat, 2);
        mycGround.addEncounter(dog, 1);
        mycGround.addEncounter(wolf, 1);
        mycGround.addEncounter(unicorn, 1);
        mycGround.addEncounter(hellHorseSingle, 1);
        mycGround.addEncounter(hellHoundSingle, 1);
        if (Servers.localServer.isChallengeServer()) {
            mycGround.addEncounter(demonsol, 1);
            mycGround.addEncounter(crawler, 1);
        }
        addTileType(mycGround);
        final EncounterType marsh = new EncounterType(Tiles.Tile.TILE_MARSH.id, (byte)0);
        marsh.addEncounter(rat, 2);
        marsh.addEncounter(anaconda, 1);
        if (Servers.localServer.isChallengeServer()) {
            marsh.addEncounter(nogump, 1);
            marsh.addEncounter(demonsol, 1);
        }
        addTileType(marsh);
        final EncounterType steppe = new EncounterType(Tiles.Tile.TILE_STEPPE.id, (byte)0);
        steppe.addEncounter(pheasant, 1);
        steppe.addEncounter(horse, 4);
        steppe.addEncounter(wildcat, 1);
        steppe.addEncounter(hellHorseSingle, 1);
        steppe.addEncounter(bison, 1);
        steppe.addEncounter(sheep, 1);
        steppe.addEncounter(ram, 1);
        if (Servers.localServer.isChallengeServer()) {
            steppe.addEncounter(drakeSpirit, 1);
            steppe.addEncounter(eagleSpirit, 1);
        }
        addTileType(steppe);
        final EncounterType treeGround = new EncounterType(Tiles.Tile.TILE_TREE.id, (byte)0);
        treeGround.addEncounter(pig, 1);
        treeGround.addEncounter(wolf, 1);
        treeGround.addEncounter(bearbrown, 1);
        treeGround.addEncounter(hellHoundSingle, 1);
        treeGround.addEncounter(pheasant, 1);
        treeGround.addEncounter(deer, 1);
        treeGround.addEncounter(spider, 2);
        treeGround.addEncounter(trollSingle, 1);
        treeGround.addEncounter(mountainlion, 1);
        if (Servers.localServer.isChallengeServer()) {
            treeGround.addEncounter(demonsol, 1);
            treeGround.addEncounter(crawler, 1);
        }
        addTileType(treeGround);
        final EncounterType sandGround = new EncounterType(Tiles.Tile.TILE_SAND.id, (byte)0);
        sandGround.addEncounter(crocodileSingle, 10);
        sandGround.addEncounter(scorpionSingle, 10);
        sandGround.addEncounter(hellScorpSingle, 1);
        sandGround.addEncounter(anaconda, 1);
        addTileType(sandGround);
        final EncounterType clayGround = new EncounterType(Tiles.Tile.TILE_CLAY.id, (byte)0);
        clayGround.addEncounter(crocodileSingle, 10);
        clayGround.addEncounter(anaconda, 1);
        addTileType(clayGround);
        final EncounterType underGround = new EncounterType(Tiles.Tile.TILE_CAVE.id, (byte)(-1));
        underGround.addEncounter(bearblack, 4);
        underGround.addEncounter(rat, 2);
        underGround.addEncounter(cavebugs, 5);
        underGround.addEncounter(spider, 2);
        underGround.addEncounter(lavaSpiderSingle, 2);
        underGround.addEncounter(lavaCreatureSingle, 4);
        underGround.addEncounter(mountainlion, 1);
        addTileType(underGround);
        final EncounterType lavaGround = new EncounterType(Tiles.Tile.TILE_LAVA.id, (byte)0);
        lavaGround.addEncounter(lavaSpiderMulti, 10);
        lavaGround.addEncounter(lavaCreatureSingle, 10);
        addTileType(lavaGround);
        final EncounterType lavaRock = new EncounterType(Tiles.Tile.TILE_LAVA.id, (byte)(-1));
        lavaRock.addEncounter(lavaCreatureSingle, 10);
        addTileType(lavaRock);
        SpawnTable.logger.log(Level.INFO, "Created Encounters. It took " + (System.nanoTime() - now) / 1000000.0f + " ms.");
    }
    
    static {
        logger = Logger.getLogger(SpawnTable.class.getName());
        land = new HashSet<EncounterType>();
        water = new HashSet<EncounterType>();
        beach = new HashSet<EncounterType>();
        deepwater = new HashSet<EncounterType>();
        flying = new HashSet<EncounterType>();
        flyinghigh = new HashSet<EncounterType>();
        caves = new HashSet<EncounterType>();
    }
}
