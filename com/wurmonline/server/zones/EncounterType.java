// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.zones;

import com.wurmonline.server.Server;
import com.wurmonline.server.creatures.Creature;
import java.util.LinkedList;
import java.util.logging.Logger;

public final class EncounterType
{
    private static final Logger logger;
    private final byte tiletype;
    public static final byte ELEVATION_GROUND = 0;
    public static final byte ELEVATION_WATER = 1;
    public static final byte ELEVATION_DEEP_WATER = 2;
    public static final byte ELEVATION_FLYING = 3;
    public static final byte ELEVATION_FLYING_HIGH = 4;
    public static final byte ELEVATION_BEACH = 5;
    public static final byte ELEVATION_CAVES = -1;
    public static final Encounter NULL_ENCOUNTER;
    private final byte elev;
    private final LinkedList<Integer> chances;
    private final LinkedList<Encounter> encounters;
    private int sumchance;
    
    public EncounterType(final byte aTiletype, final byte aElevation) {
        this.chances = new LinkedList<Integer>();
        this.encounters = new LinkedList<Encounter>();
        this.sumchance = 0;
        this.tiletype = aTiletype;
        this.elev = aElevation;
    }
    
    public void addEncounter(final Encounter enc, final int chance) {
        this.chances.addLast(chance + this.sumchance);
        this.encounters.addLast(enc);
        this.sumchance += chance;
    }
    
    public Encounter getRandomEncounter(final Creature loggerCret) {
        assert this.sumchance > 0 : "sumchance was 0, which means that no Encounters have been added to this EncounterType - " + this;
        loggerCret.getCommunicator().sendNormalServerMessage("Sumchance=" + this.sumchance + " for elevation " + this.elev);
        if (this.sumchance > 0) {
            final int rand = Server.rand.nextInt(this.sumchance) + 1;
            loggerCret.getCommunicator().sendNormalServerMessage("Rand=" + rand);
            for (int x = 0; x < this.chances.size(); ++x) {
                final Integer ii = this.chances.get(x);
                loggerCret.getCommunicator().sendNormalServerMessage("Chance integer=" + ii + " for " + this.encounters.get(x).getTypes());
                if (rand <= ii) {
                    loggerCret.getCommunicator().sendNormalServerMessage("Returning " + x);
                    return this.encounters.get(x);
                }
            }
        }
        else {
            EncounterType.logger.warning("sumchance was 0, which means that no Encounters have been added to this EncounterType - " + this);
        }
        return null;
    }
    
    Encounter getRandomEncounter() {
        assert this.sumchance > 0 : "sumchance was 0, which means that no Encounters have been added to this EncounterType - " + this;
        if (this.sumchance > 0) {
            final int rand = Server.rand.nextInt(this.sumchance) + 1;
            for (int x = 0; x < this.chances.size(); ++x) {
                final Integer ii = this.chances.get(x);
                if (rand <= ii) {
                    return this.encounters.get(x);
                }
            }
        }
        else {
            EncounterType.logger.warning("sumchance was 0, which means that no Encounters have been added to this EncounterType - " + this);
        }
        return null;
    }
    
    public byte getTiletype() {
        return this.tiletype;
    }
    
    public byte getElev() {
        return this.elev;
    }
    
    public int getNumberOfEncounters() {
        return this.encounters.size();
    }
    
    public int getSumchance() {
        return this.sumchance;
    }
    
    @Override
    public String toString() {
        return "EncounterType [tiletype=" + this.tiletype + ", elev=" + this.elev + ", encounters=" + this.getNumberOfEncounters() + ", sumchance=" + this.sumchance + "]";
    }
    
    static {
        logger = Logger.getLogger(EncounterType.class.getName());
        (NULL_ENCOUNTER = new Encounter()).addType(-10, 0);
    }
}
