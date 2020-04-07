// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.kingdom;

import com.wurmonline.server.creatures.Creatures;
import com.wurmonline.server.endgames.EndGameItem;
import com.wurmonline.server.Items;
import com.wurmonline.server.Servers;
import com.wurmonline.server.behaviours.Terraforming;
import com.wurmonline.server.NoSuchItemException;
import com.wurmonline.server.villages.Village;
import com.wurmonline.server.villages.Villages;
import com.wurmonline.server.Features;
import com.wurmonline.server.skills.Skill;
import com.wurmonline.server.skills.Skills;
import com.wurmonline.server.skills.NoSuchSkillException;
import com.wurmonline.server.LoginHandler;
import com.wurmonline.server.zones.Zone;
import com.wurmonline.server.structures.Fence;
import com.wurmonline.server.creatures.NoSuchCreatureException;
import com.wurmonline.server.zones.NoSuchZoneException;
import com.wurmonline.server.Server;
import java.sql.PreparedStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import com.wurmonline.server.utils.DbUtilities;
import java.sql.SQLException;
import com.wurmonline.server.DbConnector;
import java.util.logging.Level;
import java.util.Iterator;
import com.wurmonline.server.zones.VolaTile;
import com.wurmonline.server.endgames.EndGameItems;
import com.wurmonline.server.behaviours.MethodsCreatures;
import com.wurmonline.server.zones.Zones;
import com.wurmonline.server.NoSuchPlayerException;
import java.io.IOException;
import com.wurmonline.server.Players;
import java.util.HashSet;
import java.util.Random;
import java.util.LinkedList;
import com.wurmonline.server.creatures.Creature;
import java.util.Set;
import com.wurmonline.server.items.Item;
import java.util.logging.Logger;
import com.wurmonline.shared.constants.AttitudeConstants;
import com.wurmonline.server.TimeConstants;
import com.wurmonline.server.MiscConstants;
import com.wurmonline.server.creatures.CreatureTemplateIds;

public final class GuardTower implements CreatureTemplateIds, MiscConstants, TimeConstants, AttitudeConstants
{
    private static final String ADD_GUARD = "INSERT INTO TOWERGUARDS(TOWERID,CREATUREID) VALUES(?,?)";
    private static final String LOAD_GUARDS = "SELECT CREATUREID, RETURNED FROM TOWERGUARDS WHERE TOWERID=?";
    private static final String RETURN_GUARD = "UPDATE TOWERGUARDS SET RETURNED=? WHERE CREATUREID=?";
    private static final String DELETE_TOWER = "DELETE FROM TOWERGUARDS WHERE TOWERID=?";
    private static final String DELETE_CREATURE = "DELETE FROM TOWERGUARDS WHERE CREATUREID=?";
    private static Logger logger;
    private Item tower;
    private final Set<Creature> guards;
    private final LinkedList<Creature> freeGuards;
    private static final Random rand;
    private long lastSentAttackMessage;
    
    GuardTower(final Item item) {
        this.tower = null;
        this.guards = new HashSet<Creature>();
        this.freeGuards = new LinkedList<Creature>();
        this.lastSentAttackMessage = 0L;
        this.tower = item;
        this.load();
    }
    
    public Item getTower() {
        return this.tower;
    }
    
    public byte getKingdom() {
        return this.tower.getAuxData();
    }
    
    public final String getName() {
        String name = "unknown";
        try {
            name = Players.getInstance().getNameFor(this.tower.lastOwner);
        }
        catch (IOException ex) {}
        catch (NoSuchPlayerException ex2) {}
        return name + " " + this.tower.getWurmId() % 1000L;
    }
    
    public final long getLastSentWarning() {
        return this.lastSentAttackMessage;
    }
    
    public void sendAttackWarning() {
        if (System.currentTimeMillis() - this.lastSentAttackMessage > 180000L) {
            this.lastSentAttackMessage = System.currentTimeMillis();
            final Creature c = King.getOfficial(this.getKingdom(), 1502);
            if (c != null) {
                final StringBuilder buf = new StringBuilder();
                final String name = this.getName();
                c.getCommunicator().sendAlertServerMessage("Guard tower of " + name + " is under attack!", (byte)4);
                final int tilex = (int)this.tower.getPosX() >> 2;
                final int tiley = (int)this.tower.getPosY() >> 2;
                final VolaTile t = Zones.getTileOrNull(tilex, tiley, this.tower.isOnSurface());
                if (t != null) {
                    if (t.getVillage() != null) {
                        buf.append("The ");
                        buf.append(this.tower.getName());
                        buf.append(" is in the settlement of ");
                        buf.append(t.getVillage().getName());
                        buf.append(". ");
                    }
                    final VolaTile ct = c.getCurrentTile();
                    if (ct != null) {
                        final int ctx = ct.tilex;
                        final int cty = ct.tiley;
                        final int mindist = Math.max(Math.abs(tilex - ctx), Math.abs(tiley - cty));
                        final int dir = MethodsCreatures.getDir(c, tilex, tiley);
                        final String direction = MethodsCreatures.getLocationStringFor(c.getStatus().getRotation(), dir, "you");
                        buf.append(EndGameItems.getDistanceString(mindist, this.tower.getName(), direction, true));
                    }
                    c.getCommunicator().sendAlertServerMessage(buf.toString());
                }
            }
        }
    }
    
    boolean isMyTower(final Creature guard) {
        if (guard.getKingdomId() == this.tower.getAuxData()) {
            Iterator<Creature> it = this.guards.iterator();
            while (it.hasNext()) {
                if (it.next() == guard) {
                    return true;
                }
            }
            it = this.freeGuards.iterator();
            while (it.hasNext()) {
                if (it.next() == guard) {
                    return true;
                }
            }
        }
        return false;
    }
    
    void destroy() {
        this.destroyGuards();
        try {
            this.deleteTower();
        }
        catch (IOException iox) {
            GuardTower.logger.log(Level.WARNING, iox.getMessage(), iox);
        }
        this.tower = null;
    }
    
    void destroyGuards() {
        this.guards.clear();
        for (final Creature g : this.freeGuards) {
            try {
                this.destroyGuard(g);
            }
            catch (Exception e) {
                GuardTower.logger.log(Level.WARNING, "Problem destroying guard: " + g + " for tower: " + this + " due to " + e.getMessage(), e);
            }
        }
        this.freeGuards.clear();
    }
    
    public void destroyGuard(final Creature guard) throws IOException {
        if (GuardTower.logger.isLoggable(Level.FINER)) {
            GuardTower.logger.finer("Destroying guard " + guard);
        }
        Connection dbcon = null;
        PreparedStatement ps = null;
        try {
            dbcon = DbConnector.getZonesDbCon();
            ps = dbcon.prepareStatement("DELETE FROM TOWERGUARDS WHERE CREATUREID=?");
            ps.setLong(1, guard.getWurmId());
            ps.executeUpdate();
        }
        catch (SQLException ex) {
            GuardTower.logger.log(Level.INFO, "Failed to delete tower creature " + guard.getWurmId(), ex);
        }
        finally {
            DbUtilities.closeDatabaseObjects(ps, null);
            DbConnector.returnConnection(dbcon);
        }
    }
    
    public final boolean hasLiveGuards() {
        return this.guards.size() > 0;
    }
    
    public void returnGuard(final Creature guard) throws IOException {
        if (GuardTower.logger.isLoggable(Level.FINER)) {
            GuardTower.logger.finer("Returning guard " + guard);
        }
        this.guards.remove(guard);
        if (!this.freeGuards.contains(guard)) {
            if (this.guards.size() < this.getMaxGuards()) {
                this.freeGuards.add(guard);
                Connection dbcon = null;
                PreparedStatement ps = null;
                try {
                    dbcon = DbConnector.getZonesDbCon();
                    ps = dbcon.prepareStatement("UPDATE TOWERGUARDS SET RETURNED=? WHERE CREATUREID=?");
                    ps.setBoolean(1, true);
                    ps.setLong(2, guard.getWurmId());
                    ps.executeUpdate();
                }
                catch (SQLException ex) {
                    GuardTower.logger.log(Level.INFO, "Failed to return guard for " + this.tower.getWurmId());
                    throw new IOException("Failed to return guard for " + this.tower.getWurmId(), ex);
                }
                finally {
                    DbUtilities.closeDatabaseObjects(ps, null);
                    DbConnector.returnConnection(dbcon);
                }
            }
            else {
                guard.destroy();
                this.destroyGuard(guard);
            }
        }
    }
    
    private void setGuardInWorld(final Creature guard) {
        VolaTile t = Zones.getTileOrNull((int)this.tower.getPosX() >> 2, (int)this.tower.getPosY() >> 2, this.tower.isOnSurface());
        if (t != null) {
            final Fence[] fences = t.getFencesForLevel(0);
            if (fences != null) {
                for (final Fence f : fences) {
                    f.destroy();
                }
            }
        }
        t = Zones.getTileOrNull(((int)this.tower.getPosX() >> 2) + 1, (int)this.tower.getPosY() >> 2, this.tower.isOnSurface());
        if (t != null) {
            final Fence[] fencesForLevel;
            final Fence[] fences = fencesForLevel = t.getFencesForLevel(0);
            for (final Fence f : fencesForLevel) {
                if (!f.isHorizontal()) {
                    f.destroy();
                }
            }
        }
        t = Zones.getTileOrNull((int)this.tower.getPosX() >> 2, ((int)this.tower.getPosY() >> 2) + 1, this.tower.isOnSurface());
        if (t != null) {
            final Fence[] fencesForLevel2;
            final Fence[] fences = fencesForLevel2 = t.getFencesForLevel(0);
            for (final Fence f : fencesForLevel2) {
                if (f.isHorizontal()) {
                    f.destroy();
                }
            }
        }
        guard.setPositionX(this.tower.getPosX());
        guard.setPositionY(this.tower.getPosY());
        guard.setPositionZ(this.tower.getPosZ());
        guard.setRotation(1.0f + Server.rand.nextFloat() * 359.0f);
        guard.setLayer(this.tower.isOnSurface() ? 0 : -1, false);
        try {
            guard.respawn();
            final Zone zone = Zones.getZone((int)this.tower.getPosX() >> 2, (int)this.tower.getPosY() >> 2, this.tower.isOnSurface());
            zone.addCreature(guard.getWurmId());
            guard.savePosition(zone.getId());
        }
        catch (NoSuchZoneException nsz) {
            GuardTower.logger.log(Level.WARNING, "Guard: " + guard.getWurmId() + ": " + nsz.getMessage(), nsz);
        }
        catch (NoSuchCreatureException nsc) {
            GuardTower.logger.log(Level.WARNING, "Guard: " + guard.getWurmId() + ": " + nsc.getMessage(), nsc);
        }
        catch (NoSuchPlayerException nsp) {
            GuardTower.logger.log(Level.WARNING, "Guard: " + guard.getWurmId() + ": " + nsp.getMessage(), nsp);
        }
        catch (Exception ex) {
            GuardTower.logger.log(Level.WARNING, "Failed to return village guard: " + ex.getMessage(), ex);
        }
    }
    
    private void activateGuard(final Creature guard) throws IOException {
        this.freeGuards.remove(guard);
        if (!this.guards.contains(guard)) {
            this.guards.add(guard);
        }
        guard.setGuardTower(this);
        this.setGuardInWorld(guard);
        Connection dbcon = null;
        PreparedStatement ps = null;
        try {
            dbcon = DbConnector.getZonesDbCon();
            ps = dbcon.prepareStatement("UPDATE TOWERGUARDS SET RETURNED=? WHERE CREATUREID=?");
            ps.setBoolean(1, false);
            ps.setLong(2, guard.getWurmId());
            ps.executeUpdate();
        }
        catch (SQLException ex) {
            GuardTower.logger.log(Level.INFO, "Failed to activate guard for " + this.tower.getWurmId());
            throw new IOException("Failed to activate guard for " + this.tower.getWurmId(), ex);
        }
        finally {
            DbUtilities.closeDatabaseObjects(ps, null);
            DbConnector.returnConnection(dbcon);
        }
    }
    
    public boolean alertGuards(final Creature caller) {
        boolean helps = false;
        long target1 = -10L;
        long target2 = -10L;
        if ((caller.getReputation() >= 0 || caller.getKingdomTemplateId() == 3) && this.guards.size() > 0) {
            final int tpx = this.getTower().getTileX();
            final int tpy = this.getTower().getTileY();
            final int posz = (int)this.getTower().getPosZ();
            final Creature callerTarg = caller.getTarget();
            for (final Creature g : this.guards) {
                boolean hasTarget = false;
                if (g.target == -10L) {
                    if (callerTarg != null && callerTarg.isWithinTileDistanceTo(tpx, tpy, posz, 20) && target2 == -10L && callerTarg.getAttitude(g) == 2 && callerTarg.currentKingdom == this.getKingdom()) {
                        g.setTarget(callerTarg.getWurmId(), false);
                        if (g.target == callerTarg.getWurmId()) {
                            if (target1 == -10L) {
                                target1 = callerTarg.getWurmId();
                                yellHunt(g, callerTarg, false);
                            }
                            else if (target2 == -10L) {
                                target2 = callerTarg.getWurmId();
                                yellHunt(g, callerTarg, true);
                            }
                            else {
                                GuardTower.logger.log(Level.INFO, "This shouldn't happen? Three targets when yelling.");
                            }
                            yellHunt(g, callerTarg, true);
                            hasTarget = true;
                            helps = true;
                        }
                    }
                    if (hasTarget || caller.opponent == null || !caller.opponent.isWithinTileDistanceTo(tpx, tpy, posz, 20) || target2 != -10L || caller.opponent.getAttitude(g) != 2 || caller.opponent.currentKingdom != this.getKingdom()) {
                        continue;
                    }
                    g.setTarget(caller.opponent.getWurmId(), false);
                    if (g.target != caller.opponent.getWurmId()) {
                        continue;
                    }
                    if (target1 == -10L) {
                        target1 = caller.opponent.getWurmId();
                        yellHunt(g, caller.opponent, false);
                    }
                    else if (target2 == -10L) {
                        target2 = caller.opponent.getWurmId();
                        yellHunt(g, caller.opponent, true);
                    }
                    else {
                        GuardTower.logger.log(Level.INFO, "This shouldn't happen? Three targets when yelling.");
                        yellHunt(g, caller.opponent, true);
                    }
                    hasTarget = true;
                    helps = true;
                }
            }
        }
        return helps;
    }
    
    public static void spawnSoldier(final Item target, final byte kingdom) {
        try {
            final Creature c = Creature.doNew(7, target.getPosX() - 8.0f + Server.rand.nextFloat() * 16.0f, target.getPosY() - 8.0f + Server.rand.nextFloat() * 16.0f, Server.rand.nextFloat() * 360.0f, 0, LoginHandler.raiseFirstLetter(target.getName() + " guard"), (byte)(Server.rand.nextBoolean() ? 1 : 0), kingdom);
            c.checkForEnemies(true);
        }
        catch (Exception e) {
            GuardTower.logger.log(Level.WARNING, e.getMessage(), e);
        }
    }
    
    public static void spawnCommander(final Item target, final byte kingdom) {
        try {
            String name = target.getName() + " lieutenant";
            final boolean captain = Server.rand.nextBoolean();
            if (captain) {
                name = target.getName() + " captain";
            }
            final Creature c = Creature.doNew(8, target.getPosX() - 8.0f + Server.rand.nextFloat() * 16.0f, target.getPosY() - 8.0f + Server.rand.nextFloat() * 16.0f, Server.rand.nextFloat() * 360.0f, 0, LoginHandler.raiseFirstLetter(name), (byte)(Server.rand.nextBoolean() ? 1 : 0), kingdom);
            if (captain) {
                final Skills s = c.getSkills();
                try {
                    final Skill bc = s.getSkill(104);
                    bc.setKnowledge(bc.getKnowledge() + 10.0, false);
                    final Skill bs = s.getSkill(103);
                    bs.setKnowledge(bs.getKnowledge() + 10.0, false);
                    final Skill bst = s.getSkill(102);
                    bst.setKnowledge(bst.getKnowledge() + 10.0, false);
                    final Skill mst = s.getSkill(101);
                    mst.setKnowledge(mst.getKnowledge() + 10.0, false);
                }
                catch (NoSuchSkillException nss) {
                    GuardTower.logger.log(Level.WARNING, c.getWurmId() + ": " + nss.getMessage());
                }
            }
            c.checkForEnemies(true);
        }
        catch (Exception e) {
            GuardTower.logger.log(Level.WARNING, e.getMessage(), e);
        }
    }
    
    public static final void yellHunt(final Creature guard, final Creature target, final boolean aiding) {
        String toYell = "";
        if (!aiding) {
            switch (guard.getKingdomId()) {
                case 3: {
                    toYell = yellHotsHunter(guard, target);
                    break;
                }
                case 1: {
                    toYell = yellJennHunter(guard, target);
                    break;
                }
                case 2: {
                    toYell = yellMolrHunter(guard, target);
                    break;
                }
                case 4: {
                    toYell = yellFreedomHunter(guard, target);
                    break;
                }
                default: {
                    toYell = yellGenericHunter(guard, target);
                    break;
                }
            }
        }
        else {
            toYell = yellAidHunter(guard, target);
        }
        guard.say(toYell);
    }
    
    public static final String yellAidHunter(final Creature guard, final Creature target) {
        final StringBuilder sb = new StringBuilder();
        final int random = GuardTower.rand.nextInt(10);
        if (random < 1) {
            sb.append("Coming for ");
            if (!target.isPlayer()) {
                sb.append("the ");
            }
        }
        else if (random < 2) {
            sb.append("I'll help with ");
            if (!target.isPlayer()) {
                sb.append("the ");
            }
        }
        else if (random < 3) {
            sb.append("Joining in on ");
            if (!target.isPlayer()) {
                sb.append("the ");
            }
        }
        else if (random < 4) {
            sb.append("Beware of me as well, ");
        }
        else if (random < 5) {
            sb.append("Whoa! Here I come, ");
        }
        else if (random < 6) {
            sb.append("Now we are two, ");
        }
        else if (random < 7) {
            sb.append("You better believe it, ");
        }
        else if (random < 8) {
            sb.append("I come as well, ");
        }
        else if (random < 9) {
            sb.append("I also found ");
            if (!target.isPlayer()) {
                sb.append("the ");
            }
        }
        else if (random < 10) {
            sb.append("I see ");
            if (!target.isPlayer()) {
                sb.append("the ");
            }
        }
        if (target.isPlayer()) {
            sb.append(target.getName());
        }
        else {
            sb.append(target.getName().toLowerCase());
        }
        sb.append(".");
        return sb.toString();
    }
    
    public static final String yellHotsHunter(final Creature guard, final Creature target) {
        final StringBuilder sb = new StringBuilder();
        final int random = GuardTower.rand.nextInt(10);
        if (random < 1) {
            sb.append("You're one ugly bastard, ");
        }
        else if (random < 2) {
            sb.append("This will be fun, ");
        }
        else if (random < 3) {
            sb.append("I will enjoy killing you, ");
        }
        else if (random < 4) {
            sb.append("Eat my wrath, ");
        }
        else if (random < 5) {
            sb.append("I will shred you, ");
        }
        else if (random < 6) {
            sb.append("You will look bad torn to pieces, ");
        }
        else if (random < 7) {
            sb.append("Your corpse will rot away in silence, ");
        }
        else if (random < 8) {
            sb.append("I will drink your blood, ");
        }
        else if (random < 9) {
            sb.append("Die, die and die again, ");
        }
        else if (random < 10) {
            sb.append("Prepare to be exterminated, ");
        }
        if (target.isPlayer()) {
            sb.append(target.getName());
        }
        else {
            sb.append(target.getName().toLowerCase());
        }
        sb.append("!");
        return sb.toString();
    }
    
    public static final String yellJennHunter(final Creature guard, final Creature target) {
        final StringBuilder sb = new StringBuilder();
        final int random = GuardTower.rand.nextInt(10);
        if (random < 1) {
            sb.append("I'll hunt down ");
            if (!target.isPlayer()) {
                sb.append("this ");
            }
        }
        else if (random < 2) {
            sb.append("I'll take care of ");
            if (!target.isPlayer()) {
                sb.append("the ");
            }
        }
        else if (random < 3) {
            sb.append("You will soon be history, ");
        }
        else if (random < 4) {
            sb.append("Goodbye, ");
        }
        else if (random < 5) {
            sb.append("Quick, help me dispatch ");
            if (!target.isPlayer()) {
                sb.append("this ");
            }
        }
        else if (random < 6) {
            sb.append("I found ");
            if (!target.isPlayer()) {
                sb.append("the ");
            }
        }
        else if (random < 7) {
            sb.append("No soup for you, ");
        }
        else if (random < 8) {
            sb.append("Let me handle ");
            if (!target.isPlayer()) {
                sb.append("this ");
            }
        }
        else if (random < 9) {
            sb.append("Here is ");
            if (!target.isPlayer()) {
                sb.append("the ");
            }
        }
        else if (random < 10) {
            sb.append("This will hurt some, ");
        }
        if (target.isPlayer()) {
            sb.append(target.getName());
        }
        else {
            sb.append(target.getName().toLowerCase());
        }
        sb.append("!");
        return sb.toString();
    }
    
    public static final String yellMolrHunter(final Creature guard, final Creature target) {
        final StringBuilder sb = new StringBuilder();
        final int random = GuardTower.rand.nextInt(10);
        if (random < 1) {
            sb.append("By the fires of Magranon! Die, ");
        }
        else if (random < 2) {
            sb.append("I will rip you apart, ");
        }
        else if (random < 3) {
            sb.append("I will crush you, ");
        }
        else if (random < 4) {
            sb.append("Prepare to die, ");
        }
        else if (random < 5) {
            sb.append("This will hurt badly, ");
        }
        else if (random < 6) {
            sb.append("You receive no mercy, ");
        }
        else if (random < 7) {
            sb.append("Bleed, ");
        }
        else if (random < 8) {
            sb.append("This will be your last breath, ");
        }
        else if (random < 9) {
            sb.append("Pain will be served, ");
        }
        else if (random < 10) {
            sb.append("This is the end, ");
        }
        if (target.isPlayer()) {
            sb.append(target.getName());
        }
        else {
            sb.append(target.getName().toLowerCase());
        }
        sb.append("!");
        return sb.toString();
    }
    
    public static final String yellFreedomHunter(final Creature guard, final Creature target) {
        final StringBuilder sb = new StringBuilder();
        final int random = GuardTower.rand.nextInt(10);
        if (random < 1) {
            sb.append("Goodbye, ");
        }
        else if (random < 2) {
            sb.append("I have to slay you now, ");
        }
        else if (random < 3) {
            sb.append("Forgive me, ");
        }
        else if (random < 4) {
            sb.append("Stop that immediately, ");
        }
        else if (random < 5) {
            sb.append("I have to hurt you now, ");
        }
        else if (random < 6) {
            sb.append("Flee, ");
        }
        else if (random < 7) {
            sb.append("Run quickly now, ");
        }
        else if (random < 8) {
            sb.append("It is my duty to inform you that this is the end, ");
        }
        else if (random < 9) {
            sb.append("I will terminate ");
        }
        else if (random < 10) {
            sb.append("My pleasure, ");
        }
        if (target.isPlayer()) {
            sb.append(target.getName());
        }
        else {
            sb.append(target.getName().toLowerCase());
        }
        sb.append("!");
        return sb.toString();
    }
    
    public static final String yellGenericHunter(final Creature guard, final Creature target) {
        final StringBuilder sb = new StringBuilder();
        final int random = GuardTower.rand.nextInt(10);
        if (random < 1) {
            sb.append("I'll hunt down ");
            if (!target.isPlayer()) {
                sb.append("this ");
            }
        }
        else if (random < 2) {
            sb.append("I'll take care of ");
            if (!target.isPlayer()) {
                sb.append("the ");
            }
        }
        else if (random < 3) {
            sb.append("Prepare to meet your maker, ");
        }
        else if (random < 4) {
            sb.append("Goodbye, ");
        }
        else if (random < 5) {
            sb.append("This is the end, ");
        }
        else if (random < 6) {
            sb.append("I found ");
            if (!target.isPlayer()) {
                sb.append("this ");
            }
        }
        else if (random < 7) {
            sb.append("I will terminate you, ");
        }
        else if (random < 8) {
            sb.append("I attack ");
            if (!target.isPlayer()) {
                sb.append("this ");
            }
        }
        else if (random < 9) {
            sb.append("I will hunt ");
            if (!target.isPlayer()) {
                sb.append("the ");
            }
        }
        else if (random < 10) {
            sb.append("I get ");
            if (!target.isPlayer()) {
                sb.append("the ");
            }
        }
        if (target.isPlayer()) {
            sb.append(target.getName());
        }
        else {
            sb.append(target.getName().toLowerCase());
        }
        sb.append("!");
        return sb.toString();
    }
    
    private void newGuard(final Creature guard) throws IOException {
        guard.setGuardTower(this);
        this.guards.add(guard);
        Connection dbcon = null;
        PreparedStatement ps = null;
        try {
            dbcon = DbConnector.getZonesDbCon();
            ps = dbcon.prepareStatement("INSERT INTO TOWERGUARDS(TOWERID,CREATUREID) VALUES(?,?)");
            ps.setLong(1, this.tower.getWurmId());
            ps.setLong(2, guard.getWurmId());
            ps.executeUpdate();
        }
        catch (SQLException ex) {
            GuardTower.logger.log(Level.INFO, "Failed to insert guard for " + this.tower.getWurmId());
            throw new IOException("Failed to insert guard for " + this.tower.getWurmId(), ex);
        }
        finally {
            DbUtilities.closeDatabaseObjects(ps, null);
            DbConnector.returnConnection(dbcon);
        }
    }
    
    private void deleteTower() throws IOException {
        Connection dbcon = null;
        PreparedStatement ps = null;
        try {
            dbcon = DbConnector.getZonesDbCon();
            ps = dbcon.prepareStatement("DELETE FROM TOWERGUARDS WHERE TOWERID=?");
            ps.setLong(1, this.tower.getWurmId());
            ps.executeUpdate();
        }
        catch (SQLException ex) {
            GuardTower.logger.log(Level.INFO, "Failed to delete tower " + this.tower.getWurmId());
            throw new IOException("Failed to delete tower " + this.tower.getWurmId(), ex);
        }
        finally {
            DbUtilities.closeDatabaseObjects(ps, null);
            DbConnector.returnConnection(dbcon);
        }
    }
    
    void poll() {
        this.pollGuards();
        this.tower.attackEnemies(true);
    }
    
    public final int getGuardCount() {
        return this.guards.size();
    }
    
    public final int getMaxGuards() {
        if (Features.Feature.TOWER_CHAINING.isEnabled() && !this.tower.isChained()) {
            return 0;
        }
        return Math.min(5 + this.tower.getRarity(), (int)this.tower.getQualityLevel() / 10);
    }
    
    public final int getMaxPossibleGuards() {
        return Math.min(5 + this.tower.getRarity(), (int)this.tower.getQualityLevel() / 10);
    }
    
    private void pollGuards() {
        if (this.guards.size() < this.getMaxGuards()) {
            final Village v = Villages.getVillageWithPerimeterAt(this.getTower().getTileX(), this.getTower().getTileY(), true);
            if (v != null && this.getKingdom() != v.kingdom) {
                return;
            }
            byte sex = 0;
            if (Server.rand.nextInt(2) == 0) {
                sex = 1;
            }
            int templateId = 34;
            byte templateKingdom = this.tower.getAuxData();
            final Kingdom kingdom = Kingdoms.getKingdom(this.tower.getAuxData());
            if (kingdom != null) {
                templateKingdom = kingdom.getTemplate();
            }
            if (templateKingdom == 3) {
                templateId = 35;
            }
            else if (templateKingdom == 2) {
                templateId = 36;
            }
            else if (templateKingdom == 4) {
                templateId = 67;
            }
            try {
                if (this.freeGuards.isEmpty()) {
                    final Kingdom k = Kingdoms.getKingdom(this.tower.getAuxData());
                    if (k.getId() != 0) {
                        this.spawnGuard(templateId, "tower guard", false);
                    }
                    else if (this.tower.getTemplateId() == 996) {
                        this.spawnGuard(67, "Peacekeeper", false);
                    }
                }
                else {
                    final Creature toReturn = this.freeGuards.removeFirst();
                    this.activateGuard(toReturn);
                    if (GuardTower.logger.isLoggable(Level.FINER)) {
                        GuardTower.logger.finer("Activating " + toReturn.getWurmId());
                    }
                }
            }
            catch (Exception ex) {
                GuardTower.logger.log(Level.WARNING, "Problem while polling guards for tower: " + this.tower + ", " + ex.getMessage(), ex);
            }
        }
    }
    
    public int getBashAlertDamage() {
        return 90;
    }
    
    public void checkBashDamage(final float oldDam, final float newDam) {
        for (byte i = 1; i <= 3; ++i) {
            if (oldDam < 30 * i && newDam >= 30 * i) {
                this.spawnBashWave(i);
                break;
            }
        }
        if (oldDam < this.getBashAlertDamage() && newDam >= this.getBashAlertDamage()) {
            Players.getInstance().broadCastBashInfo(this.getTower(), this.getName() + " is under attack.");
        }
    }
    
    public int getTowerSpawnMod() {
        return Math.round(this.getTower().getQualityLevel() / 30.0f);
    }
    
    public int getTowerBashSpawnCount(final int spawnMod, final byte waveStrength) {
        return Math.max(1, spawnMod) * waveStrength;
    }
    
    public void spawnGuard(final int templateId, final String name, final boolean offSetSpawn) {
        try {
            final byte sex = (byte)((Server.rand.nextInt(2) == 0) ? 1 : 0);
            final Kingdom k = Kingdoms.getKingdom(this.tower.getAuxData());
            if (k.getId() != 0) {
                byte xOffSet = 0;
                byte yOffSet = 0;
                if (offSetSpawn) {
                    if (Server.rand.nextBoolean()) {
                        xOffSet = (byte)(Server.rand.nextBoolean() ? 1 : -1);
                    }
                    if (Server.rand.nextBoolean()) {
                        yOffSet = (byte)(Server.rand.nextBoolean() ? 1 : -1);
                    }
                }
                final String creatureName = k.getName() + " " + name;
                final Creature newc = Creature.doNew(templateId, this.tower.getPosX() + xOffSet, this.tower.getPosY() + yOffSet, Server.rand.nextInt(360), this.tower.isOnSurface() ? 0 : -1, creatureName, sex, this.tower.getAuxData());
                this.newGuard(newc);
                if (GuardTower.logger.isLoggable(Level.FINER)) {
                    GuardTower.logger.finer("WT Created guard " + newc.getName() + " now=" + this.guards.size() + " max=" + this.getMaxGuards());
                }
            }
        }
        catch (Exception ex) {
            GuardTower.logger.log(Level.WARNING, "Problem while spawning guard for tower: " + this.tower + ", " + ex.getMessage(), ex);
        }
    }
    
    public void spawnBashWave(final byte waveStrength) {
        final byte sex = 0;
        int templateId = 34;
        byte templateKingdom = this.tower.getAuxData();
        final Kingdom kingdom = Kingdoms.getKingdom(this.tower.getAuxData());
        if (kingdom != null) {
            templateKingdom = kingdom.getTemplate();
        }
        if (templateKingdom == 3) {
            templateId = 35;
        }
        else if (templateKingdom == 2) {
            templateId = 36;
        }
        else if (templateKingdom == 4) {
            templateId = 67;
        }
        if (this.guards.size() < this.getMaxPossibleGuards()) {
            for (int i = this.guards.size(); i < this.getMaxPossibleGuards(); ++i) {
                this.spawnGuard(templateId, "tower guard", true);
            }
        }
        for (int spawnCount = this.getTowerBashSpawnCount(this.getTowerSpawnMod(), waveStrength), j = 0; j < spawnCount; ++j) {
            this.spawnGuard(8, "Captain", true);
        }
    }
    
    public static boolean hasNearbyAlliedTower(final int tilex, final int tiley, final byte founderKingdom) {
        GuardTower closest = null;
        int minDist = Integer.MAX_VALUE;
        for (final GuardTower tower : Kingdoms.getTowers().values()) {
            final int distx = Math.abs(tower.getTower().getTileX() - tilex);
            final int disty = Math.abs(tower.getTower().getTileY() - tiley);
            final int tileDistance = Math.max(distx, disty);
            if (tileDistance <= minDist) {
                minDist = tileDistance;
                closest = tower;
            }
        }
        return closest != null && minDist <= 100 && closest.getTower().getKingdom() == founderKingdom;
    }
    
    public static void canConstructTower(final Creature performer, final Item realTarget) throws NoSuchItemException {
        if (!performer.isOnSurface() || !realTarget.isOnSurface()) {
            performer.getCommunicator().sendAlertServerMessage("You can't construct the tower now; you can't build this below surface.");
            throw new NoSuchItemException("Below surface.");
        }
        final VolaTile targTile = Zones.getTileOrNull(realTarget.getTileX(), realTarget.getTileY(), true);
        if (targTile != null && targTile.isTransition()) {
            performer.getCommunicator().sendAlertServerMessage("You can't construct the tower here - the foundation is not stable enough.");
            throw new NoSuchItemException("On cave opening.");
        }
        final int tilex = realTarget.getTileX();
        final int tiley = realTarget.getTileY();
        final boolean onSurface = realTarget.isOnSurface();
        if (Terraforming.isTileUnderWater(1, tilex, tiley, onSurface)) {
            performer.getCommunicator().sendAlertServerMessage("You can't construct the tower now; the ground is not solid here.");
            throw new NoSuchItemException("Too wet.");
        }
        final int mindist = Kingdoms.minKingdomDist;
        if (Kingdoms.isTowerTooNear(tilex, tiley, onSurface, false)) {
            performer.getCommunicator().sendAlertServerMessage("You can't construct the tower now; another tower is too near.");
            throw new NoSuchItemException("Too close to another tower.");
        }
        if (Features.Feature.TOWER_CHAINING.isEnabled() && !hasNearbyAlliedTower(tilex, tiley, performer.getKingdomId()) && performer.getKingdomId() != 4) {
            performer.getCommunicator().sendAlertServerMessage("You can't construct the tower now; it must be within range of another allied tower.");
            throw new NoSuchItemException("Not within range of an allied tower.");
        }
        if (!Zones.isKingdomBlocking(tilex - mindist, tiley - mindist, tilex + mindist, tiley + mindist, performer.getKingdomId())) {
            performer.getCommunicator().sendAlertServerMessage("You can't construct the tower now; another kingdom is too near.");
            throw new NoSuchItemException("Too close to another kingdom.");
        }
        if (!Servers.isThisAHomeServer()) {
            if (Terraforming.isTileModBlocked(performer, tilex, tiley, performer.isOnSurface())) {
                throw new NoSuchItemException("Tile protected by the deities in this area.");
            }
            for (final Item targ : Items.getWarTargets()) {
                final int maxnorth = Math.max(0, tiley - 60);
                final int maxsouth = Math.min(Zones.worldTileSizeY, tiley + 60);
                final int maxwest = Math.max(0, tilex - 60);
                final int maxeast = Math.min(Zones.worldTileSizeX, tilex + 60);
                if ((int)targ.getPosX() >> 2 > maxwest && (int)targ.getPosX() >> 2 < maxeast && (int)targ.getPosY() >> 2 < maxsouth && (int)targ.getPosY() >> 2 > maxnorth) {
                    performer.getCommunicator().sendSafeServerMessage("You cannot construct the tower here, since this is an active battle ground.");
                    throw new NoSuchItemException("Too close to a war target.");
                }
            }
            EndGameItem alt = EndGameItems.getEvilAltar();
            if (alt != null) {
                final int maxnorth2 = Math.max(0, tiley - 100);
                final int maxsouth2 = Math.min(Zones.worldTileSizeY, tiley + 100);
                final int maxeast2 = Math.max(0, tilex - 100);
                final int maxwest2 = Math.min(Zones.worldTileSizeX, tilex + 100);
                if (alt.getItem() != null && (int)alt.getItem().getPosX() >> 2 < maxwest2 && (int)alt.getItem().getPosX() >> 2 > maxeast2 && (int)alt.getItem().getPosY() >> 2 < maxsouth2 && (int)alt.getItem().getPosY() >> 2 > maxnorth2) {
                    throw new NoSuchItemException("You cannot construct a tower here, since this is unholy ground.");
                }
            }
            alt = EndGameItems.getGoodAltar();
            if (alt != null) {
                final int maxnorth2 = Math.max(0, tiley - 100);
                final int maxsouth2 = Math.min(Zones.worldTileSizeY, tiley + 100);
                final int maxeast2 = Math.max(0, tilex - 100);
                final int maxwest2 = Math.min(Zones.worldTileSizeX, tilex + 100);
                if (alt.getItem() != null && (int)alt.getItem().getPosX() >> 2 < maxwest2 && (int)alt.getItem().getPosX() >> 2 > maxeast2 && (int)alt.getItem().getPosY() >> 2 < maxsouth2 && (int)alt.getItem().getPosY() >> 2 > maxnorth2) {
                    throw new NoSuchItemException("You cannot construct a tower here, since this is holy ground.");
                }
            }
        }
    }
    
    private void load() {
        Connection dbcon = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            dbcon = DbConnector.getZonesDbCon();
            ps = dbcon.prepareStatement("SELECT CREATUREID, RETURNED FROM TOWERGUARDS WHERE TOWERID=?");
            ps.setLong(1, this.tower.getWurmId());
            rs = ps.executeQuery();
            while (rs.next()) {
                final long creatureid = rs.getLong("CREATUREID");
                final boolean returned = rs.getBoolean("RETURNED");
                try {
                    final Creature guard = Creatures.getInstance().getCreature(creatureid);
                    if (GuardTower.logger.isLoggable(Level.FINER)) {
                        GuardTower.logger.finer("GT Loaded " + guard.getName());
                    }
                    if (!returned) {
                        if (!this.guards.contains(guard)) {
                            this.guards.add(guard);
                        }
                        guard.setGuardTower(this);
                    }
                    else {
                        if (this.freeGuards.contains(guard)) {
                            continue;
                        }
                        this.freeGuards.add(guard);
                    }
                }
                catch (NoSuchCreatureException nsc) {
                    Connection dbcon2 = null;
                    PreparedStatement ps2 = null;
                    try {
                        GuardTower.logger.log(Level.WARNING, "Deleting from towerguards where creatureid=" + creatureid);
                        dbcon2 = DbConnector.getZonesDbCon();
                        ps2 = dbcon2.prepareStatement("DELETE FROM TOWERGUARDS WHERE CREATUREID=?");
                        ps2.setLong(1, creatureid);
                        ps2.executeUpdate();
                    }
                    catch (SQLException ex) {
                        GuardTower.logger.log(Level.INFO, "Failed to delete tower creature " + creatureid, ex);
                    }
                    finally {
                        DbUtilities.closeDatabaseObjects(ps2, null);
                        DbConnector.returnConnection(dbcon2);
                    }
                }
            }
        }
        catch (SQLException sqx) {
            GuardTower.logger.log(Level.WARNING, "Failed to load guards for tower with id " + this.tower.getWurmId(), sqx);
        }
        finally {
            DbUtilities.closeDatabaseObjects(ps, rs);
            DbConnector.returnConnection(dbcon);
        }
    }
    
    @Override
    public String toString() {
        final long lWurmID = (this.tower != null) ? this.tower.getWurmId() : -1L;
        return "GuardTower [WurmID: " + lWurmID + ", Kingdom: " + Kingdoms.getNameFor(this.getKingdom()) + ", #guards: " + this.guards.size() + ", #freeGuards: " + this.freeGuards.size() + ", Item: " + this.tower + ']';
    }
    
    static {
        GuardTower.logger = Logger.getLogger(GuardTower.class.getName());
        rand = new Random();
    }
}
