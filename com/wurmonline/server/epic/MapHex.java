// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.epic;

import java.io.ObjectInputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ListIterator;
import com.wurmonline.server.webinterface.WCValreiMapUpdater;
import com.wurmonline.server.WurmId;
import com.wurmonline.server.Servers;
import com.wurmonline.server.Server;
import java.util.Map;
import java.util.HashMap;
import java.sql.PreparedStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import com.wurmonline.server.utils.DbUtilities;
import java.sql.SQLException;
import java.util.logging.Level;
import com.wurmonline.server.DbConnector;
import java.util.Iterator;
import java.util.HashSet;
import java.util.Set;
import java.util.LinkedList;
import java.util.Random;
import java.util.logging.Logger;
import com.wurmonline.server.MiscConstants;

public class MapHex implements MiscConstants
{
    private static final Logger logger;
    private final int id;
    private final int type;
    private final String name;
    private final float moveCost;
    private String presenceStringOne;
    private String prepositionString;
    private String leavesStringOne;
    private static final Random rand;
    private final LinkedList<Integer> nearHexes;
    private final LinkedList<EpicEntity> entities;
    private final Set<EpicEntity> visitedBy;
    private long spawnEntityId;
    private long homeEntityId;
    public static final int TYPE_STANDARD = 0;
    public static final int TYPE_TRAP = 1;
    public static final int TYPE_SLOW = 2;
    public static final int TYPE_ENHANCE_STRENGTH = 3;
    public static final int TYPE_ENHANCE_VITALITY = 4;
    public static final int TYPE_TELEPORT = 5;
    private final HexMap myMap;
    private static final String addVisitedBy = "INSERT INTO VISITED(ENTITYID,HEXID) VALUES (?,?)";
    private static final String clearVisitedHex = "DELETE FROM VISITED WHERE HEXID=?";
    
    MapHex(final HexMap map, final int hexNumber, final String hexName, final float hexMoveCost, final int hexType) {
        this.presenceStringOne = " is in ";
        this.prepositionString = " in ";
        this.leavesStringOne = " leaves ";
        this.nearHexes = new LinkedList<Integer>();
        this.entities = new LinkedList<EpicEntity>();
        this.visitedBy = new HashSet<EpicEntity>();
        this.spawnEntityId = 0L;
        this.homeEntityId = 0L;
        this.id = hexNumber;
        this.name = hexName;
        this.moveCost = Math.max(0.5f, hexMoveCost);
        this.type = hexType;
        (this.myMap = map).addMapHex(this);
    }
    
    public final int getId() {
        return this.id;
    }
    
    public final String getName() {
        return this.name;
    }
    
    final String getEnemyStatus(final EpicEntity entity) {
        final StringBuilder build = new StringBuilder();
        if (entity.isCollectable() || entity.isSource()) {
            return "";
        }
        for (final EpicEntity e : this.entities) {
            if (e != entity && !e.isCollectable() && !e.isSource()) {
                if (e.isWurm()) {
                    if (build.length() > 0) {
                        build.append(' ');
                    }
                    build.append(entity.getName() + " is battling the Wurm.");
                }
                else if (e.isSentinelMonster()) {
                    if (build.length() > 0) {
                        build.append(' ');
                    }
                    build.append(entity.getName() + " is trying to defeat the " + e.getName() + ".");
                }
                else if (e.isEnemy(entity)) {
                    if (build.length() > 0) {
                        build.append(' ');
                    }
                    build.append(entity.getName() + " is fighting " + e.getName() + ".");
                }
                else if (entity.getCompanion() == e) {
                    if (build.length() > 0) {
                        build.append(' ');
                    }
                    build.append(entity.getName() + " is meeting with " + e.getName() + ".");
                }
                if (!e.isAlly()) {
                    continue;
                }
                if (build.length() > 0) {
                    build.append(' ');
                }
                build.append(entity.getName() + " visits the " + e.getName() + ".");
            }
        }
        return build.toString();
    }
    
    long getSpawnEntityId() {
        return this.spawnEntityId;
    }
    
    long getHomeEntityId() {
        return this.homeEntityId;
    }
    
    final String getOwnPresenceString() {
        return " is home" + this.getFullPrepositionString();
    }
    
    final String getFullPresenceString() {
        return this.getPresenceStringOne() + this.name + ".";
    }
    
    final String getFullPrepositionString() {
        return this.getPrepositionString() + this.name + ".";
    }
    
    final float getMoveCost() {
        return this.moveCost;
    }
    
    HexMap getMyMap() {
        return this.myMap;
    }
    
    final void setPresenceStringOne(final String ps) {
        this.presenceStringOne = ps;
    }
    
    final String getPresenceStringOne() {
        return this.presenceStringOne;
    }
    
    final void setPrepositionString(final String ps) {
        this.prepositionString = ps;
    }
    
    final String getPrepositionString() {
        return this.prepositionString;
    }
    
    final void setLeavesStringOne(final String ps) {
        this.leavesStringOne = ps;
    }
    
    final String getLeavesStringOne() {
        return this.leavesStringOne;
    }
    
    final int getType() {
        return this.type;
    }
    
    final void addEntity(final EpicEntity entity) {
        if (!this.entities.contains(entity)) {
            this.entities.add(entity);
            entity.setMapHex(this);
            if (entity.isWurm() || entity.isDeity()) {
                if (entity.getAttack() > entity.getInitialAttack()) {
                    entity.setAttack(entity.getAttack() - 0.1f);
                }
                if (entity.getVitality() > entity.getInitialVitality()) {
                    entity.setVitality(entity.getVitality() - 0.1f);
                }
                else if (entity.getVitality() < entity.getInitialVitality()) {
                    entity.setVitality(entity.getVitality() + 0.1f);
                }
            }
            else if (entity.isCollectable() || entity.isSource()) {
                this.clearVisitedBy();
            }
        }
    }
    
    final void removeEntity(final EpicEntity entity, final boolean load) {
        if (this.entities.contains(entity)) {
            this.entities.remove(entity);
            entity.setMapHex(null);
        }
    }
    
    boolean checkLeaveStatus(final EpicEntity entity) {
        return this.setEntityEffects(entity);
    }
    
    public final Integer[] getNearMapHexes() {
        return this.nearHexes.toArray(new Integer[this.nearHexes.size()]);
    }
    
    final void addNearHex(final int hexId) {
        this.nearHexes.add(hexId);
    }
    
    final void addNearHexes(final int hexId1, final int hexId2, final int hexId3, final int hexId4, final int hexId5, final int hexId6) {
        this.nearHexes.add(hexId1);
        this.nearHexes.add(hexId2);
        this.nearHexes.add(hexId3);
        this.nearHexes.add(hexId4);
        this.nearHexes.add(hexId5);
        this.nearHexes.add(hexId6);
    }
    
    final boolean isVisitedBy(final EpicEntity entity) {
        for (final EpicEntity ent : this.entities) {
            if (ent.isCollectable() || ent.isSource()) {
                return false;
            }
        }
        return this.visitedBy.contains(entity);
    }
    
    final void addVisitedBy(final EpicEntity entity, final boolean load) {
        if (this.visitedBy != null && !this.visitedBy.contains(entity)) {
            this.visitedBy.add(entity);
            if (!load) {
                Connection dbcon = null;
                PreparedStatement ps = null;
                try {
                    dbcon = DbConnector.getDeityDbCon();
                    ps = dbcon.prepareStatement("INSERT INTO VISITED(ENTITYID,HEXID) VALUES (?,?)");
                    ps.setLong(1, entity.getId());
                    ps.setInt(2, this.getId());
                    ps.executeUpdate();
                }
                catch (SQLException sqx) {
                    MapHex.logger.log(Level.WARNING, sqx.getMessage(), sqx);
                }
                finally {
                    DbUtilities.closeDatabaseObjects(ps, null);
                    DbConnector.returnConnection(dbcon);
                }
            }
        }
    }
    
    final void clearVisitedBy() {
        this.visitedBy.clear();
        Connection dbcon = null;
        PreparedStatement ps = null;
        try {
            dbcon = DbConnector.getDeityDbCon();
            ps = dbcon.prepareStatement("DELETE FROM VISITED WHERE HEXID=?");
            ps.setInt(1, this.getId());
            ps.executeUpdate();
        }
        catch (SQLException sqx) {
            MapHex.logger.log(Level.WARNING, sqx.getMessage(), sqx);
        }
        finally {
            DbUtilities.closeDatabaseObjects(ps, null);
            DbConnector.returnConnection(dbcon);
        }
    }
    
    LinkedList<Integer> cloneNearHexes() {
        final LinkedList<Integer> clone = new LinkedList<Integer>();
        for (final Integer i : this.nearHexes) {
            clone.add(i);
        }
        return clone;
    }
    
    final boolean containsWurm() {
        for (final EpicEntity e : this.entities) {
            if (e.isWurm()) {
                return true;
            }
        }
        return false;
    }
    
    final boolean containsEnemy(final EpicEntity toCheck) {
        for (final EpicEntity e : this.entities) {
            if (e.isEnemy(toCheck)) {
                return true;
            }
        }
        return false;
    }
    
    final boolean containsMonsterOrHelper() {
        for (final EpicEntity e : this.entities) {
            if (e.isSentinelMonster() || e.isAlly()) {
                return true;
            }
        }
        return false;
    }
    
    final boolean containsDeity() {
        for (final EpicEntity e : this.entities) {
            if (e.isDeity()) {
                return true;
            }
        }
        return false;
    }
    
    boolean mayEnter(final EpicEntity entity) {
        return !entity.isWurm() || !this.containsMonsterOrHelper() || this.containsDeity();
    }
    
    int getNextHexToWinPoint(final EpicEntity entity) {
        if (!entity.mustReturnHomeToWin()) {
            return this.findClosestHexTo(this.myMap.getHexNumRequiredToWin(), entity, true);
        }
        final MapHex home = this.myMap.getSpawnHex(entity);
        if (home != null && home != this) {
            return this.findClosestHexTo(home.getId(), entity, true);
        }
        return this.getId();
    }
    
    int findClosestHexTo(final int target, final EpicEntity entity, final boolean avoidEnemies) {
        MapHex.logger.log(Level.INFO, entity.getName() + " at " + this.getId() + " pathing to " + target);
        final Map<Integer, Integer> steps = new HashMap<Integer, Integer>();
        final LinkedList<Integer> copy = this.cloneNearHexes();
        while (copy.size() > 0) {
            final Integer i = copy.remove(MapHex.rand.nextInt(copy.size()));
            if (i == target) {
                return target;
            }
            final MapHex hex = this.myMap.getMapHex(i);
            if (!hex.mayEnter(entity) || (avoidEnemies && hex.containsEnemy(entity))) {
                continue;
            }
            final Set<Integer> checked = new HashSet<Integer>();
            checked.add(i);
            final int numSteps = this.findNextHex(checked, hex, target, entity, avoidEnemies, 0);
            steps.put(hex.getId(), numSteps);
        }
        int minSteps = 100;
        int hexNum = 0;
        for (final Map.Entry<Integer, Integer> entry : steps.entrySet()) {
            final int csteps = entry.getValue();
            if (csteps < minSteps) {
                minSteps = csteps;
                hexNum = entry.getKey();
            }
        }
        return hexNum;
    }
    
    int findNextHex(final Set<Integer> checked, final MapHex startHex, final int targetHexId, final EpicEntity entity, final boolean avoidEnemies, int counter) {
        final LinkedList<Integer> nearClone = startHex.cloneNearHexes();
        int minNum = 100;
        while (nearClone.size() > 0) {
            final Integer ni = nearClone.remove(MapHex.rand.nextInt(nearClone.size()));
            if (ni == targetHexId) {
                return counter;
            }
            if (checked.contains(ni)) {
                continue;
            }
            checked.add(ni);
            if (counter >= 6) {
                continue;
            }
            final MapHex nearhex = this.myMap.getMapHex(ni);
            if (!nearhex.mayEnter(entity) || (avoidEnemies && nearhex.containsEnemy(entity))) {
                continue;
            }
            final int steps = this.findNextHex(checked, nearhex, targetHexId, entity, avoidEnemies, ++counter);
            if (steps >= minNum) {
                continue;
            }
            minNum = steps;
        }
        return minNum;
    }
    
    int findNextHex(final EpicEntity entity) {
        if (this.nearHexes.isEmpty()) {
            MapHex.logger.log(Level.WARNING, "Near hexes is empty for map " + this.getId());
            return 0;
        }
        if (!entity.hasEnoughCollectablesToWin()) {
            LinkedList<Integer> copy = this.cloneNearHexes();
            while (copy.size() > 0) {
                final Integer i = copy.remove(MapHex.rand.nextInt(copy.size()));
                final MapHex hex = this.myMap.getMapHex(i);
                if (hex.mayEnter(entity)) {
                    if (entity.isWurm()) {
                        return hex.getId();
                    }
                    if (!hex.isVisitedBy(entity)) {
                        return hex.getId();
                    }
                    continue;
                }
            }
            copy = this.cloneNearHexes();
            while (copy.size() > 0) {
                final Integer i = copy.remove(MapHex.rand.nextInt(copy.size()));
                final MapHex hex = this.myMap.getMapHex(i);
                if (hex.mayEnter(entity)) {
                    final LinkedList<Integer> nearClone = hex.cloneNearHexes();
                    while (nearClone.size() > 0) {
                        final Integer ni = nearClone.remove(MapHex.rand.nextInt(nearClone.size()));
                        final MapHex nearhex = this.myMap.getMapHex(ni);
                        if (!nearhex.isVisitedBy(entity)) {
                            return hex.getId();
                        }
                    }
                }
            }
            copy = this.cloneNearHexes();
            while (copy.size() > 0) {
                final Integer i = copy.remove(MapHex.rand.nextInt(copy.size()));
                final MapHex hex = this.myMap.getMapHex(i);
                if (hex.mayEnter(entity)) {
                    return i;
                }
            }
            MapHex.logger.log(Level.INFO, entity.getName() + " Failed to take random step to neighbour.");
            return 0;
        }
        if (this.getId() == this.myMap.getHexNumRequiredToWin()) {
            return this.getId();
        }
        return this.getNextHexToWinPoint(entity);
    }
    
    public boolean isTrap() {
        return this.type == 1;
    }
    
    public boolean isTeleport() {
        return this.type == 5;
    }
    
    public boolean isSlow() {
        return this.type == 2;
    }
    
    int getSlowModifier() {
        return this.isSlow() ? 2 : 1;
    }
    
    private final boolean resolveDispute(final EpicEntity entity) {
        EpicEntity enemy = null;
        for (final EpicEntity e : this.entities) {
            if (e != entity && e.isEnemy(entity)) {
                if (enemy == null) {
                    enemy = e;
                }
                else {
                    if (!Server.rand.nextBoolean()) {
                        continue;
                    }
                    enemy = e;
                }
            }
        }
        if (enemy == null) {
            return true;
        }
        final ValreiFight vFight = new ValreiFight(this, entity, enemy);
        final ValreiFightHistory fightHistory = vFight.completeFight(false);
        ValreiFightHistoryManager.getInstance().addFight(fightHistory.getFightId(), fightHistory);
        if (Servers.localServer.LOGINSERVER) {
            final WCValreiMapUpdater updater = new WCValreiMapUpdater(WurmId.getNextWCCommandId(), (byte)5);
            updater.sendFromLoginServer();
        }
        if (fightHistory.getFightWinner() == entity.getId()) {
            this.fightEndEffects(entity, enemy);
            return true;
        }
        this.fightEndEffects(enemy, entity);
        return false;
    }
    
    private final void fightEndEffects(final EpicEntity winner, final EpicEntity loser) {
        if (loser.isWurm()) {
            winner.broadCastWithName(" wards off " + loser.getName() + this.getFullPrepositionString());
        }
        else if (winner.isWurm()) {
            loser.broadCastWithName(" is defeated by " + winner.getName() + this.getFullPrepositionString());
        }
        else if (loser.isSentinelMonster()) {
            winner.broadCastWithName(" prevails against " + loser.getName() + this.getFullPrepositionString());
        }
        else {
            loser.broadCastWithName(" is vanquished by " + winner.getName() + this.getFullPrepositionString());
        }
        loser.dropAll(winner.isDemigod());
        this.removeEntity(loser, false);
        this.addVisitedBy(loser, false);
        if (loser.isDemigod()) {
            this.myMap.destroyEntity(loser);
        }
    }
    
    private final boolean resolveDisputeDeprecated(final EpicEntity entity) {
        EpicEntity enemy = null;
        EpicEntity enemy2 = null;
        EpicEntity helper = null;
        EpicEntity friend = null;
        for (final EpicEntity e : this.entities) {
            if (e != entity) {
                if (e.isEnemy(entity)) {
                    if (enemy == null) {
                        enemy = e;
                    }
                    else {
                        enemy2 = e;
                    }
                }
                else if (e.isAlly() && e.isFriend(entity)) {
                    helper = e;
                }
                if (!e.isDeity() && !e.isDemigod() && !entity.isFriend(e)) {
                    continue;
                }
                friend = e;
            }
        }
        if (friend != null && friend.countCollectables() > 0 && entity.countCollectables() > 0 && entity.isDeity()) {
            friend.giveCollectables(entity);
        }
        if (enemy == null) {
            return true;
        }
        while (true) {
            if (enemy != null) {
                if (this.attack(enemy, entity)) {
                    return false;
                }
                if (this.attack(entity, enemy)) {
                    enemy = null;
                    if (enemy2 == null) {
                        return true;
                    }
                }
                if (helper != null && this.attack(helper, enemy)) {
                    enemy = null;
                    if (enemy2 == null) {
                        return true;
                    }
                }
            }
            if (enemy2 != null) {
                if (this.attack(entity, enemy2)) {
                    enemy2 = null;
                    if (enemy == null) {
                        return true;
                    }
                    continue;
                }
                else {
                    if (this.attack(enemy2, entity)) {
                        return false;
                    }
                    continue;
                }
            }
        }
    }
    
    private final boolean attack(final EpicEntity entity, final EpicEntity enemy) {
        if (entity.rollAttack() && enemy.setVitality(enemy.getVitality() - 1.0f)) {
            if (enemy.isWurm()) {
                entity.broadCastWithName(" wards off " + enemy.getName() + this.getFullPrepositionString());
            }
            else if (entity.isWurm()) {
                enemy.broadCastWithName(" is defeated by " + entity.getName() + this.getFullPrepositionString());
            }
            else if (enemy.isSentinelMonster()) {
                entity.broadCastWithName(" prevails against " + enemy.getName() + this.getFullPrepositionString());
            }
            else {
                enemy.broadCastWithName(" is vanquished by " + entity.getName() + this.getFullPrepositionString());
            }
            enemy.dropAll(entity.isDemigod());
            this.removeEntity(enemy, false);
            this.addVisitedBy(enemy, false);
            if (enemy.isDemigod()) {
                this.myMap.destroyEntity(enemy);
            }
            return true;
        }
        return false;
    }
    
    protected final String getCollectibleName() {
        final ListIterator<EpicEntity> lit = this.entities.listIterator();
        while (lit.hasNext()) {
            final EpicEntity next = lit.next();
            if (next.isCollectable()) {
                return next.getName();
            }
        }
        return "";
    }
    
    protected final int countCollectibles() {
        int toret = 0;
        final ListIterator<EpicEntity> lit = this.entities.listIterator();
        while (lit.hasNext()) {
            final EpicEntity next = lit.next();
            if (next.isCollectable()) {
                ++toret;
            }
        }
        return toret;
    }
    
    private final void pickupStuff(final EpicEntity entity) {
        final ListIterator<EpicEntity> lit = this.entities.listIterator();
        while (lit.hasNext()) {
            final EpicEntity next = lit.next();
            if (next.isCollectable() || next.isSource()) {
                entity.logWithName(" found " + next.getName() + ".");
                lit.remove();
                next.setMapHex(null);
                next.setCarrier(entity, true, false, false);
            }
        }
    }
    
    public boolean isStrength() {
        return this.type == 3;
    }
    
    public boolean isVitality() {
        return this.type == 4;
    }
    
    final boolean setEntityEffects(final EpicEntity entity) {
        if (this.resolveDispute(entity)) {
            switch (this.type) {
                case 1: {}
                case 3: {
                    if (entity.isDeity() || entity.isWurm()) {
                        float current = entity.getCurrentSkill(102);
                        entity.setSkill(102, current + (100.0f - current) / 1250.0f);
                        current = entity.getCurrentSkill(104);
                        entity.setSkill(104, current + (100.0f - current) / 1250.0f);
                        current = entity.getCurrentSkill(105);
                        entity.setSkill(105, current + (100.0f - current) / 1250.0f);
                        entity.broadCastWithName(" is strengthened by the influence of " + this.getName() + ".");
                        break;
                    }
                    break;
                }
                case 4: {
                    if (entity.isDeity() || entity.isWurm()) {
                        float current = entity.getCurrentSkill(100);
                        entity.setSkill(100, current + (100.0f - current) / 1250.0f);
                        current = entity.getCurrentSkill(103);
                        entity.setSkill(103, current + (100.0f - current) / 1250.0f);
                        current = entity.getCurrentSkill(101);
                        entity.setSkill(101, current + (100.0f - current) / 1250.0f);
                        entity.broadCastWithName(" is vitalized by the influence of " + this.getName() + ".");
                        break;
                    }
                    break;
                }
            }
            entity.setVitality(Math.max(entity.getInitialVitality() / 2.0f, entity.getVitality()), false);
            this.pickupStuff(entity);
            this.addVisitedBy(entity, false);
            return true;
        }
        return false;
    }
    
    long getEntitySpawn() {
        return this.spawnEntityId;
    }
    
    boolean isSpawnFor(final long entityId) {
        return this.spawnEntityId == entityId;
    }
    
    void setSpawnEntityId(final long entityId) {
        this.spawnEntityId = entityId;
    }
    
    boolean isSpawn() {
        return this.spawnEntityId != 0L;
    }
    
    boolean isHomeFor(final long entityId) {
        return this.homeEntityId == entityId;
    }
    
    void setHomeEntityId(final long entityId) {
        this.homeEntityId = entityId;
    }
    
    static {
        logger = Logger.getLogger(MapHex.class.getName());
        rand = new Random();
    }
}
