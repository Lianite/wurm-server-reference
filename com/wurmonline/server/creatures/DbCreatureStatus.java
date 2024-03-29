// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.creatures;

import com.wurmonline.server.WurmCalendar;
import com.wurmonline.server.Players;
import com.wurmonline.server.kingdom.Kingdoms;
import com.wurmonline.server.Server;
import com.wurmonline.server.behaviours.Seat;
import com.wurmonline.server.behaviours.Vehicle;
import com.wurmonline.server.items.Item;
import com.wurmonline.server.NoSuchItemException;
import com.wurmonline.server.behaviours.Vehicles;
import com.wurmonline.server.Items;
import com.wurmonline.server.structures.NoSuchStructureException;
import com.wurmonline.server.structures.Structure;
import com.wurmonline.server.structures.Structures;
import com.wurmonline.server.bodys.BodyFactory;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import com.wurmonline.server.utils.DbUtilities;
import java.sql.Connection;
import java.sql.SQLException;
import java.io.IOException;
import java.util.logging.Level;
import com.wurmonline.server.DbConnector;
import com.wurmonline.server.WurmId;
import java.util.logging.Logger;
import com.wurmonline.shared.constants.CounterTypes;

public final class DbCreatureStatus extends CreatureStatus implements CounterTypes
{
    private static final Logger logger;
    private static final String getPlayerStatus = "select * from PLAYERS where WURMID=?";
    private static final String getCreatureStatus = "select * from CREATURES where WURMID=?";
    private static final String savePlayerStatus = "update PLAYERS set TEMPLATENAME=?, SEX=?, CENTIMETERSHIGH=?, CENTIMETERSLONG=?, CENTIMETERSWIDE=?, INVENTORYID=?, BODYID=?, BUILDINGID=?, HUNGER=?, THIRST=?, STAMINA=?,KINGDOM=?,FAT=?,STEALTH=?,DETECTIONSECS=?,TRAITS=?,NUTRITION=?,CALORIES=?,CARBS=?,FATS=?,PROTEINS=? where WURMID=?";
    private static final String saveCreatureStatus = "update CREATURES set NAME=?, TEMPLATENAME=?,SEX=?, CENTIMETERSHIGH=?, CENTIMETERSLONG=?, CENTIMETERSWIDE=?, INVENTORYID=?, BODYID=?, BUILDINGID=?, HUNGER=?, THIRST=?, STAMINA=?,KINGDOM=?,FAT=?,STEALTH=?,DETECTIONSECS=?,TRAITS=?,NUTRITION=?,PETNAME=?,AGE=? where WURMID=?";
    private static final String createPlayerStatus = "insert into PLAYERS (TEMPLATENAME, SEX,CENTIMETERSHIGH, CENTIMETERSLONG, CENTIMETERSWIDE, INVENTORYID,BODYID,BUILDINGID,HUNGER, THIRST, STAMINA,KINGDOM,FAT,STEALTH,DETECTIONSECS,TRAITS,NUTRITION,CALORIES,CARBS,FATS,PROTEINS,WURMID ) values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
    private static final String createCreatureStatus = "insert into CREATURES (NAME, TEMPLATENAME, SEX,CENTIMETERSHIGH, CENTIMETERSLONG, CENTIMETERSWIDE, INVENTORYID,BODYID, BUILDINGID,HUNGER,THIRST, STAMINA,KINGDOM,FAT,STEALTH,DETECTIONSECS,TRAITS,NUTRITION,PETNAME, AGE, WURMID) values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
    private static final String SET_PLAYER_KINGDOM = "update PLAYERS set KINGDOM=? WHERE WURMID=?";
    private static final String SET_PLAYER_INVENTORYID = "update PLAYERS set INVENTORYID=? WHERE WURMID=?";
    private static final String SET_CREATURE_INVENTORYID = "update CREATURES set INVENTORYID=? WHERE WURMID=?";
    private static final String SET_CREATURE_KINGDOM = "update CREATURES set KINGDOM=? WHERE WURMID=?";
    private static final String SET_DEAD_CREATURE = "update CREATURES set DEAD=? WHERE WURMID=?";
    private static final String SET_DEAD_PLAYER = "update PLAYERS set DEAD=? WHERE WURMID=?";
    private static final String SET_AGE_CREATURE = "update CREATURES set AGE=?,LASTPOLLEDAGE=? WHERE WURMID=?";
    private static final String SET_AGE_PLAYER = "update PLAYERS set AGE=?,LASTPOLLEDAGE=? WHERE WURMID=?";
    private static final String SET_FAT_CREATURE = "update CREATURES set FAT=? WHERE WURMID=?";
    private static final String SET_FAT_PLAYER = "update PLAYERS set FAT=? WHERE WURMID=?";
    private static final String SET_DOMINATOR = "update CREATURES set DOMINATOR=? WHERE WURMID=?";
    private static final String SET_REBORN = "update CREATURES set REBORN=? WHERE WURMID=?";
    private static final String SET_LOYALTY = "update CREATURES set LOYALTY=? WHERE WURMID=?";
    private static final String SET_OFFLINE = "update CREATURES set OFFLINE=? WHERE WURMID=?";
    private static final String SET_STAYONLINE = "update CREATURES set STAYONLINE=? WHERE WURMID=?";
    private static final String SET_CREATURE_TYPE = "update CREATURES set TYPE=? WHERE WURMID=?";
    private static final String SET_PLAYER_TYPE = "update PLAYERS set TYPE=? WHERE WURMID=?";
    private static final String SET_CREATURE_NAME = "update CREATURES set NAME=? WHERE WURMID=?";
    private static final String SET_CREATURE_INHERITANCE = "update CREATURES set TRAITS=?,MOTHER=?,FATHER=? WHERE WURMID=?";
    private static final String SET_PLAYER_INHERITANCE = "update PLAYERS set TRAITS=?,MOTHER=?,FATHER=? WHERE WURMID=?";
    private static final String SET_LASTPOLLEDLOYALTY = "update CREATURES set LASTPOLLEDLOYALTY=? WHERE WURMID=?";
    private static final String SET_PLDETECTIONSECS = "update PLAYERS set DETECTIONSECS=? WHERE WURMID=?";
    private static final String SET_LASTGROOMED = "update CREATURES set LASTGROOMED=? WHERE WURMID=?";
    private static final String SET_CDISEASE = "update CREATURES set DISEASE=? WHERE WURMID=?";
    private static final String SET_VEHICLE = "update CREATURES set VEHICLE=?,SEAT_TYPE=? WHERE WURMID=?";
    private static final String SET_PDISEASE = "update PLAYERS set DISEASE=? WHERE WURMID=?";
    private static final String ISLOADED = "update CREATURES set ISLOADED=? WHERE WURMID=?";
    private static final String SET_NEWAGE = "update CREATURES set AGE=? WHERE WURMID=?";
    
    public DbCreatureStatus(final Creature aCreature, final float posx, final float posy, final float aRot, final int aLayer) throws Exception {
        super(aCreature, posx, posy, aRot, aLayer);
    }
    
    @Override
    public boolean save() throws IOException {
        final long now = System.nanoTime();
        final long id = this.statusHolder.getWurmId();
        this.inventoryId = this.statusHolder.getInventory().getWurmId();
        if (this.bodyId <= 0L) {
            this.bodyId = this.body.getId();
        }
        Connection dbcon = null;
        boolean toReturn = true;
        try {
            if (WurmId.getType(this.statusHolder.getWurmId()) == 0) {
                dbcon = DbConnector.getPlayerDbCon();
                toReturn = this.savePlayerStatus(id, dbcon);
            }
            else {
                dbcon = DbConnector.getCreatureDbCon();
                toReturn = this.saveCreatureStatus(id, dbcon);
            }
        }
        catch (SQLException sqex) {
            DbCreatureStatus.logger.log(Level.WARNING, "Failed to save status for creature with id " + id, sqex);
            throw new IOException("Failed to save status for creature with id " + id);
        }
        finally {
            dbcon = null;
            if (DbCreatureStatus.logger.isLoggable(Level.FINER)) {
                final float lElapsedTime = (System.nanoTime() - now) / 1000000.0f;
                if (lElapsedTime > 10.0f || (DbCreatureStatus.logger.isLoggable(Level.FINEST) && lElapsedTime > 1.0f)) {
                    DbCreatureStatus.logger.finer("Saving Status for " + ((WurmId.getType(this.statusHolder.getWurmId()) == 0) ? " player id, " : " Creature id, ") + this.statusHolder.getWurmId() + "," + this.statusHolder.getName() + ", which took " + lElapsedTime + " millis.");
                }
            }
        }
        return toReturn;
    }
    
    private boolean saveCreatureStatus(final long id, final Connection dbcon) throws SQLException {
        if (this.isChanged()) {
            PreparedStatement ps;
            if (this.isStatusExists()) {
                ps = dbcon.prepareStatement("update CREATURES set NAME=?, TEMPLATENAME=?,SEX=?, CENTIMETERSHIGH=?, CENTIMETERSLONG=?, CENTIMETERSWIDE=?, INVENTORYID=?, BODYID=?, BUILDINGID=?, HUNGER=?, THIRST=?, STAMINA=?,KINGDOM=?,FAT=?,STEALTH=?,DETECTIONSECS=?,TRAITS=?,NUTRITION=?,PETNAME=?,AGE=? where WURMID=?");
            }
            else {
                ps = dbcon.prepareStatement("insert into CREATURES (NAME, TEMPLATENAME, SEX,CENTIMETERSHIGH, CENTIMETERSLONG, CENTIMETERSWIDE, INVENTORYID,BODYID, BUILDINGID,HUNGER,THIRST, STAMINA,KINGDOM,FAT,STEALTH,DETECTIONSECS,TRAITS,NUTRITION,PETNAME, AGE, WURMID) values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
                this.setStatusExists(true);
            }
            ps.setString(1, this.statusHolder.name);
            ps.setString(2, this.template.getName());
            ps.setByte(3, this.sex);
            if (this.body.getCentimetersHigh() == 0) {
                this.body.setCentimetersHigh(this.template.getCentimetersHigh());
            }
            ps.setShort(4, this.body.getCentimetersHigh());
            if (this.body.getCentimetersLong() == 0) {
                this.body.setCentimetersLong(this.template.getCentimetersLong());
            }
            ps.setShort(5, this.body.getCentimetersLong());
            if (this.body.getCentimetersWide() == 0) {
                this.body.setCentimetersWide(this.template.getCentimetersWide());
            }
            ps.setShort(6, this.body.getCentimetersWide());
            ps.setLong(7, this.inventoryId);
            ps.setLong(8, this.bodyId);
            ps.setLong(9, this.buildingId);
            ps.setShort(10, (short)this.hunger);
            ps.setShort(11, (short)this.thirst);
            ps.setShort(12, (short)this.stamina);
            ps.setByte(13, this.kingdom);
            ps.setInt(14, this.fat);
            ps.setBoolean(15, this.stealth);
            ps.setShort(16, (short)this.detectInvisCounter);
            ps.setLong(17, this.getTraitBits());
            ps.setFloat(18, this.nutrition);
            ps.setString(19, this.statusHolder.petName);
            ps.setShort(20, (short)this.age);
            ps.setLong(21, id);
            ps.executeUpdate();
            DbUtilities.closeDatabaseObjects(ps, null);
            this.setChanged(false);
            return true;
        }
        return false;
    }
    
    private boolean savePlayerStatus(final long id, final Connection dbcon) throws SQLException {
        PreparedStatement ps = null;
        try {
            if (this.isStatusExists()) {
                ps = dbcon.prepareStatement("update PLAYERS set TEMPLATENAME=?, SEX=?, CENTIMETERSHIGH=?, CENTIMETERSLONG=?, CENTIMETERSWIDE=?, INVENTORYID=?, BODYID=?, BUILDINGID=?, HUNGER=?, THIRST=?, STAMINA=?,KINGDOM=?,FAT=?,STEALTH=?,DETECTIONSECS=?,TRAITS=?,NUTRITION=?,CALORIES=?,CARBS=?,FATS=?,PROTEINS=? where WURMID=?");
            }
            else {
                DbCreatureStatus.logger.log(Level.INFO, "Creating new status");
                ps = dbcon.prepareStatement("insert into PLAYERS (TEMPLATENAME, SEX,CENTIMETERSHIGH, CENTIMETERSLONG, CENTIMETERSWIDE, INVENTORYID,BODYID,BUILDINGID,HUNGER, THIRST, STAMINA,KINGDOM,FAT,STEALTH,DETECTIONSECS,TRAITS,NUTRITION,CALORIES,CARBS,FATS,PROTEINS,WURMID ) values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
                this.stamina = 65535;
                this.setStatusExists(true);
            }
            ps.setString(1, this.template.getName());
            ps.setByte(2, this.sex);
            ps.setShort(3, this.body.getCentimetersHigh());
            ps.setShort(4, this.body.getCentimetersLong());
            ps.setShort(5, this.body.getCentimetersWide());
            ps.setLong(6, this.inventoryId);
            ps.setLong(7, this.bodyId);
            ps.setLong(8, this.buildingId);
            ps.setShort(9, (short)this.hunger);
            ps.setShort(10, (short)this.thirst);
            ps.setShort(11, (short)this.stamina);
            ps.setByte(12, this.kingdom);
            ps.setByte(13, this.fat);
            ps.setBoolean(14, this.stealth);
            ps.setShort(15, (short)this.detectInvisCounter);
            ps.setLong(16, this.getTraitBits());
            ps.setFloat(17, this.nutrition);
            ps.setFloat(18, this.calories);
            ps.setFloat(19, this.carbs);
            ps.setFloat(20, this.fats);
            ps.setFloat(21, this.proteins);
            ps.setLong(22, id);
            ps.executeUpdate();
        }
        finally {
            DbUtilities.closeDatabaseObjects(ps, null);
        }
        return true;
    }
    
    @Override
    public void load() throws Exception {
        final long id = this.statusHolder.getWurmId();
        Connection dbcon = null;
        String loadString = "select * from CREATURES where WURMID=?";
        this.setPosition(CreaturePos.getPosition(id));
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            if (WurmId.getType(id) == 0) {
                if (DbCreatureStatus.logger.isLoggable(Level.FINEST)) {
                    DbCreatureStatus.logger.finest("Loading a player - id:" + id);
                }
                loadString = "select * from PLAYERS where WURMID=?";
                dbcon = DbConnector.getPlayerDbCon();
            }
            else {
                if (DbCreatureStatus.logger.isLoggable(Level.FINEST)) {
                    DbCreatureStatus.logger.finest("Loading a creature - id:" + id);
                }
                dbcon = DbConnector.getCreatureDbCon();
            }
            ps = dbcon.prepareStatement(loadString);
            ps.setLong(1, id);
            rs = ps.executeQuery();
            if (rs.next()) {
                final String templateName = rs.getString("TEMPLATENAME");
                this.template = CreatureTemplateFactory.getInstance().getTemplate(templateName);
                this.statusHolder.template = this.template;
                this.bodyId = rs.getLong("BODYID");
                (this.body = BodyFactory.getBody(this.statusHolder, this.template.getBodyType(), this.template.getCentimetersHigh(), this.template.getCentimetersLong(), this.template.getCentimetersWide())).setCentimetersLong(rs.getShort("CENTIMETERSLONG"));
                this.body.setCentimetersHigh(rs.getShort("CENTIMETERSHIGH"));
                this.body.setCentimetersWide(rs.getShort("CENTIMETERSWIDE"));
                this.sex = rs.getByte("SEX");
                this.modtype = rs.getByte("TYPE");
                final String name = rs.getString("NAME");
                this.statusHolder.setName(name);
                this.inventoryId = rs.getLong("INVENTORYID");
                this.stamina = (rs.getShort("STAMINA") & 0xFFFF);
                this.hunger = (rs.getShort("HUNGER") & 0xFFFF);
                this.thirst = (rs.getShort("THIRST") & 0xFFFF);
                this.buildingId = rs.getLong("BUILDINGID");
                this.kingdom = rs.getByte("KINGDOM");
                this.dead = rs.getBoolean("DEAD");
                this.stealth = rs.getBoolean("STEALTH");
                this.age = rs.getInt("AGE");
                this.fat = rs.getByte("FAT");
                this.lastPolledAge = rs.getLong("LASTPOLLEDAGE");
                this.statusHolder.dominator = rs.getLong("DOMINATOR");
                this.reborn = rs.getBoolean("REBORN");
                this.loyalty = rs.getFloat("LOYALTY");
                this.lastPolledLoyalty = rs.getLong("LASTPOLLEDLOYALTY");
                this.detectInvisCounter = rs.getShort("DETECTIONSECS");
                this.traits = rs.getLong("TRAITS");
                if (this.traits != 0L) {
                    this.setTraitBits(this.traits);
                }
                this.mother = rs.getLong("MOTHER");
                this.father = rs.getLong("FATHER");
                this.nutrition = rs.getFloat("NUTRITION");
                this.disease = rs.getByte("DISEASE");
                if (WurmId.getType(id) == 0) {
                    this.calories = rs.getFloat("CALORIES");
                    this.carbs = rs.getFloat("CARBS");
                    this.fats = rs.getFloat("FATS");
                    this.proteins = rs.getFloat("PROTEINS");
                }
                if (this.buildingId != -10L) {
                    try {
                        final Structure struct = Structures.getStructure(this.buildingId);
                        if (!struct.isFinalFinished()) {
                            this.statusHolder.setStructure(struct);
                        }
                        else {
                            this.buildingId = -10L;
                        }
                    }
                    catch (NoSuchStructureException nss) {
                        this.buildingId = -10L;
                        DbCreatureStatus.logger.log(Level.INFO, "Could not find structure for " + this.statusHolder.getName());
                        this.statusHolder.setStructure(null);
                    }
                }
                if (WurmId.getType(id) == 1) {
                    this.lastGroomed = rs.getLong("LASTGROOMED");
                    this.offline = rs.getBoolean("OFFLINE");
                    this.stayOnline = rs.getBoolean("STAYONLINE");
                }
                this.statusHolder.calculateSize();
                final long hitchedTo = rs.getLong("VEHICLE");
                if (hitchedTo > 0L) {
                    try {
                        final Item vehicle = Items.getItem(hitchedTo);
                        final Vehicle vehic = Vehicles.getVehicle(vehicle);
                        if (vehic.addDragger(this.statusHolder)) {
                            this.statusHolder.setHitched(vehic, true);
                            final Seat driverseat = vehic.getPilotSeat();
                            final float _r = (-vehicle.getRotation() + 180.0f) * 3.1415927f / 180.0f;
                            final float _s = (float)Math.sin(_r);
                            final float _c = (float)Math.cos(_r);
                            final float xo = _s * -driverseat.offx - _c * -driverseat.offy;
                            final float yo = _c * -driverseat.offx + _s * -driverseat.offy;
                            final float nPosX = this.getPositionX() - xo;
                            final float nPosY = this.getPositionY() - yo;
                            final float nPosZ = this.getPositionZ() - driverseat.offz;
                            this.setPositionX(nPosX);
                            this.setPositionY(nPosY);
                            this.setRotation(-vehicle.getRotation() + 180.0f);
                            this.statusHolder.getMovementScheme().setPosition(this.getPositionX(), this.getPositionY(), nPosZ, this.getRotation(), this.statusHolder.getLayer());
                        }
                    }
                    catch (NoSuchItemException nsi) {
                        DbCreatureStatus.logger.log(Level.INFO, "Item " + hitchedTo + " missing for hitched " + id + " " + name);
                    }
                }
                this.setStatusExists(true);
            }
        }
        catch (Exception sqex) {
            DbCreatureStatus.logger.log(Level.WARNING, "Failed to load status for creature with id " + id, sqex);
            throw new IOException("Failed to load status for creature with id " + id);
        }
        finally {
            DbUtilities.closeDatabaseObjects(ps, rs);
            DbConnector.returnConnection(dbcon);
        }
    }
    
    public static long getInventoryIdFor(final long creatureId) {
        Connection dbcon = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        long toReturn = -10L;
        try {
            String selectString = "select * from PLAYERS where WURMID=?";
            if (WurmId.getType(creatureId) == 1) {
                selectString = "select * from CREATURES where WURMID=?";
                dbcon = DbConnector.getCreatureDbCon();
            }
            else {
                dbcon = DbConnector.getPlayerDbCon();
            }
            ps = dbcon.prepareStatement(selectString);
            ps.setLong(1, creatureId);
            rs = ps.executeQuery();
            if (rs.next()) {
                toReturn = rs.getLong("INVENTORYID");
            }
        }
        catch (SQLException sqx) {
            DbCreatureStatus.logger.log(Level.WARNING, "Creature has no inventoryitem?" + creatureId, sqx);
        }
        finally {
            DbUtilities.closeDatabaseObjects(ps, rs);
            DbConnector.returnConnection(dbcon);
        }
        return toReturn;
    }
    
    @Override
    public void savePosition(final long id, final boolean player, final int zoneid, final boolean immediately) throws IOException {
        if (this.getPosition() == null) {
            DbCreatureStatus.logger.log(Level.WARNING, "Position is null for " + id + " at ", new Exception());
            return;
        }
        final long now = System.nanoTime();
        try {
            if (player) {
                this.getPosition().savePlayerPosition(zoneid, immediately);
            }
            else {
                this.getPosition().saveCreaturePosition(zoneid, immediately);
            }
        }
        catch (SQLException sqex) {
            DbCreatureStatus.logger.log(Level.WARNING, "Failed to save status for creature/player with id " + id, sqex);
            if (Server.getMillisToShutDown() == Long.MIN_VALUE) {
                Server.getInstance().startShutdown(5, "The server lost connection to the database. Shutting down ");
            }
            throw new IOException("Failed to save status for creature/player with id " + id, sqex);
        }
        finally {
            if (DbCreatureStatus.logger.isLoggable(Level.FINER)) {
                final float lElapsedTime = (System.nanoTime() - now) / 1000000.0f;
                if (lElapsedTime > 10.0f || (DbCreatureStatus.logger.isLoggable(Level.FINEST) && lElapsedTime > 1.0f)) {
                    DbCreatureStatus.logger.finer("Saving Position for " + (player ? " player id, " : " Creature id, ") + this.statusHolder.getWurmId() + "," + this.statusHolder.getName() + ", which took " + lElapsedTime + " millis.");
                }
            }
        }
    }
    
    @Override
    public void setKingdom(final byte kingd) throws IOException {
        final boolean send = this.kingdom != 0;
        if (this.kingdom != kingd && this.statusHolder.isPlayer()) {
            Kingdoms.getKingdom(this.kingdom).removeMember(this.statusHolder.getWurmId());
        }
        this.kingdom = kingd;
        if (this.statusHolder.isPlayer() && this.statusHolder.getPower() == 0) {
            Kingdoms.getKingdom(this.kingdom).addMember(this.statusHolder.getWurmId());
        }
        Connection dbcon = null;
        PreparedStatement ps = null;
        try {
            if (WurmId.getType(this.statusHolder.getWurmId()) == 0) {
                dbcon = DbConnector.getPlayerDbCon();
                ps = dbcon.prepareStatement("update PLAYERS set KINGDOM=? WHERE WURMID=?");
            }
            else {
                dbcon = DbConnector.getCreatureDbCon();
                ps = dbcon.prepareStatement("update CREATURES set KINGDOM=? WHERE WURMID=?");
            }
            ps.setByte(1, this.kingdom);
            ps.setLong(2, this.statusHolder.getWurmId());
            ps.executeUpdate();
            if (send) {
                this.statusHolder.sendAttitudeChange();
                this.statusHolder.refreshVisible();
            }
        }
        catch (SQLException sqex) {
            DbCreatureStatus.logger.log(Level.WARNING, this.statusHolder.getWurmId() + " " + sqex.getMessage(), sqex);
            throw new IOException("Failed to set kingdom for " + this.statusHolder.getWurmId() + " to " + Kingdoms.getNameFor(kingd) + ".");
        }
        finally {
            DbUtilities.closeDatabaseObjects(ps, null);
            DbConnector.returnConnection(dbcon);
        }
        if (this.statusHolder.isPlayer()) {
            Players.getInstance().registerNewKingdom(this.statusHolder.getKingdomId(), this.kingdom);
        }
    }
    
    @Override
    public void setInventoryId(final long newInventoryId) throws IOException {
        this.inventoryId = newInventoryId;
        Connection dbcon = null;
        PreparedStatement ps = null;
        try {
            dbcon = DbConnector.getPlayerDbCon();
            if (WurmId.getType(this.statusHolder.getWurmId()) == 0) {
                ps = dbcon.prepareStatement("update PLAYERS set INVENTORYID=? WHERE WURMID=?");
            }
            else {
                dbcon = DbConnector.getCreatureDbCon();
                ps = dbcon.prepareStatement("update CREATURES set INVENTORYID=? WHERE WURMID=?");
            }
            ps.setLong(1, this.inventoryId);
            ps.setLong(2, this.statusHolder.getWurmId());
            ps.executeUpdate();
        }
        catch (SQLException sqex) {
            DbCreatureStatus.logger.log(Level.WARNING, this.statusHolder.getWurmId() + " " + sqex.getMessage(), sqex);
            throw new IOException("Failed to set inventory id for " + this.statusHolder.getWurmId() + " to " + this.inventoryId + ".");
        }
        finally {
            DbUtilities.closeDatabaseObjects(ps, null);
            DbConnector.returnConnection(dbcon);
        }
    }
    
    @Override
    public void setDead(final boolean isdead) throws IOException {
        this.dead = isdead;
        Connection dbcon = null;
        PreparedStatement ps = null;
        try {
            if (this.statusHolder.isPlayer()) {
                dbcon = DbConnector.getPlayerDbCon();
                ps = dbcon.prepareStatement("update PLAYERS set DEAD=? WHERE WURMID=?");
            }
            else {
                dbcon = DbConnector.getCreatureDbCon();
                ps = dbcon.prepareStatement("update CREATURES set DEAD=? WHERE WURMID=?");
            }
            ps.setBoolean(1, this.dead);
            ps.setLong(2, this.statusHolder.getWurmId());
            ps.executeUpdate();
        }
        catch (SQLException sqex) {
            DbCreatureStatus.logger.log(Level.WARNING, this.statusHolder.getWurmId() + " " + sqex.getMessage(), sqex);
            throw new IOException("Failed to set dead to " + isdead + " for " + this.statusHolder.getWurmId() + '.');
        }
        finally {
            DbUtilities.closeDatabaseObjects(ps, null);
            DbConnector.returnConnection(dbcon);
        }
    }
    
    public void updateAge(final int newAge) throws IOException {
        this.age = newAge;
        Connection dbcon = null;
        PreparedStatement ps = null;
        try {
            this.lastPolledAge = WurmCalendar.currentTime;
            if (this.statusHolder.isPlayer()) {
                dbcon = DbConnector.getPlayerDbCon();
                ps = dbcon.prepareStatement("update PLAYERS set AGE=?,LASTPOLLEDAGE=? WHERE WURMID=?");
            }
            else {
                dbcon = DbConnector.getCreatureDbCon();
                ps = dbcon.prepareStatement("update CREATURES set AGE=?,LASTPOLLEDAGE=? WHERE WURMID=?");
            }
            ps.setShort(1, (short)this.age);
            ps.setLong(2, this.lastPolledAge);
            ps.setLong(3, this.statusHolder.getWurmId());
            ps.executeUpdate();
        }
        catch (SQLException sqex) {
            DbCreatureStatus.logger.log(Level.WARNING, "Problem setting age of creature ID " + this.statusHolder.getWurmId() + " to " + this.age + ", lastPolledAge: " + this.lastPolledAge + " " + sqex.getMessage(), sqex);
            throw new IOException("Failed to set age to " + this.age + " for " + this.statusHolder.getWurmId() + '.');
        }
        finally {
            DbUtilities.closeDatabaseObjects(ps, null);
            DbConnector.returnConnection(dbcon);
        }
    }
    
    public void updateFat() throws IOException {
        Connection dbcon = null;
        PreparedStatement ps = null;
        try {
            if (this.statusHolder.isPlayer()) {
                dbcon = DbConnector.getPlayerDbCon();
                ps = dbcon.prepareStatement("update PLAYERS set FAT=? WHERE WURMID=?");
            }
            else {
                dbcon = DbConnector.getCreatureDbCon();
                ps = dbcon.prepareStatement("update CREATURES set FAT=? WHERE WURMID=?");
            }
            ps.setByte(1, this.fat);
            ps.setLong(2, this.statusHolder.getWurmId());
            ps.executeUpdate();
        }
        catch (SQLException sqex) {
            DbCreatureStatus.logger.log(Level.WARNING, "Failed to set fat to " + this.fat + " for " + this.statusHolder.getWurmId() + " " + sqex.getMessage(), sqex);
            throw new IOException("Failed to set fat to " + this.fat + " for " + this.statusHolder.getWurmId() + '.');
        }
        finally {
            DbUtilities.closeDatabaseObjects(ps, null);
            DbConnector.returnConnection(dbcon);
        }
    }
    
    public void setDominator(final long dominator) {
        Connection dbcon = null;
        PreparedStatement ps = null;
        try {
            dbcon = DbConnector.getCreatureDbCon();
            ps = dbcon.prepareStatement("update CREATURES set DOMINATOR=? WHERE WURMID=?");
            ps.setLong(1, dominator);
            ps.setLong(2, this.statusHolder.getWurmId());
            ps.executeUpdate();
        }
        catch (SQLException sqex) {
            DbCreatureStatus.logger.log(Level.WARNING, "Failed to set dominator to " + dominator + " for " + this.statusHolder.getWurmId() + " " + sqex.getMessage(), sqex);
        }
        finally {
            DbUtilities.closeDatabaseObjects(ps, null);
            DbConnector.returnConnection(dbcon);
        }
    }
    
    @Override
    public void setReborn(final boolean reb) {
        this.reborn = reb;
        Connection dbcon = null;
        PreparedStatement ps = null;
        try {
            dbcon = DbConnector.getCreatureDbCon();
            ps = dbcon.prepareStatement("update CREATURES set REBORN=? WHERE WURMID=?");
            ps.setBoolean(1, this.reborn);
            ps.setLong(2, this.statusHolder.getWurmId());
            ps.executeUpdate();
        }
        catch (SQLException sqex) {
            DbCreatureStatus.logger.log(Level.WARNING, "Failed to set reborn to " + this.reborn + " for " + this.statusHolder.getWurmId() + " " + sqex.getMessage(), sqex);
        }
        finally {
            DbUtilities.closeDatabaseObjects(ps, null);
            DbConnector.returnConnection(dbcon);
        }
    }
    
    public void setLoyalty(float _loyalty) {
        _loyalty = Math.min(100.0f, _loyalty);
        _loyalty = Math.max(0.0f, _loyalty);
        if (_loyalty != this.loyalty) {
            this.loyalty = _loyalty;
            Connection dbcon = null;
            PreparedStatement ps = null;
            try {
                dbcon = DbConnector.getCreatureDbCon();
                ps = dbcon.prepareStatement("update CREATURES set LOYALTY=? WHERE WURMID=?");
                ps.setFloat(1, this.loyalty);
                ps.setLong(2, this.statusHolder.getWurmId());
                ps.executeUpdate();
            }
            catch (SQLException sqex) {
                DbCreatureStatus.logger.log(Level.WARNING, "Failed to set loyalty to " + this.loyalty + " for " + this.statusHolder.getWurmId() + " " + sqex.getMessage(), sqex);
            }
            finally {
                DbUtilities.closeDatabaseObjects(ps, null);
                DbConnector.returnConnection(dbcon);
            }
        }
    }
    
    @Override
    public void setLastPolledLoyalty() {
        this.lastPolledLoyalty = System.currentTimeMillis();
        Connection dbcon = null;
        PreparedStatement ps = null;
        try {
            dbcon = DbConnector.getCreatureDbCon();
            ps = dbcon.prepareStatement("update CREATURES set LASTPOLLEDLOYALTY=? WHERE WURMID=?");
            ps.setLong(1, this.lastPolledLoyalty);
            ps.setLong(2, this.statusHolder.getWurmId());
            ps.executeUpdate();
        }
        catch (SQLException sqex) {
            DbCreatureStatus.logger.log(Level.WARNING, "Failed to set lastPolledLoyalty to " + this.lastPolledLoyalty + " for " + this.statusHolder.getWurmId() + " " + sqex.getMessage(), sqex);
        }
        finally {
            DbUtilities.closeDatabaseObjects(ps, null);
            DbConnector.returnConnection(dbcon);
        }
    }
    
    public void setDetectionSecs() {
        if (this.statusHolder.isPlayer()) {
            Connection dbcon = null;
            PreparedStatement ps = null;
            try {
                dbcon = DbConnector.getPlayerDbCon();
                ps = dbcon.prepareStatement("update PLAYERS set DETECTIONSECS=? WHERE WURMID=?");
                ps.setShort(1, (short)this.detectInvisCounter);
                ps.setLong(2, this.statusHolder.getWurmId());
                ps.executeUpdate();
            }
            catch (SQLException sqex) {
                DbCreatureStatus.logger.log(Level.WARNING, "Failed to set detectInvisCounter to " + this.detectInvisCounter + " for " + this.statusHolder.getWurmId() + " " + sqex.getMessage(), sqex);
            }
            finally {
                DbUtilities.closeDatabaseObjects(ps, null);
                DbConnector.returnConnection(dbcon);
            }
        }
    }
    
    public void setOffline(final boolean _offline) {
        this.offline = _offline;
        Connection dbcon = null;
        PreparedStatement ps = null;
        try {
            dbcon = DbConnector.getCreatureDbCon();
            ps = dbcon.prepareStatement("update CREATURES set OFFLINE=? WHERE WURMID=?");
            ps.setBoolean(1, this.offline);
            ps.setLong(2, this.statusHolder.getWurmId());
            ps.executeUpdate();
        }
        catch (SQLException sqex) {
            DbCreatureStatus.logger.log(Level.WARNING, "Failed to set offline to " + this.offline + " for " + this.statusHolder.getWurmId() + " " + sqex.getMessage(), sqex);
        }
        finally {
            DbUtilities.closeDatabaseObjects(ps, null);
            DbConnector.returnConnection(dbcon);
        }
    }
    
    public boolean setStayOnline(final boolean _stayOnline) {
        this.stayOnline = _stayOnline;
        Connection dbcon = null;
        PreparedStatement ps = null;
        try {
            dbcon = DbConnector.getCreatureDbCon();
            ps = dbcon.prepareStatement("update CREATURES set STAYONLINE=? WHERE WURMID=?");
            ps.setBoolean(1, this.stayOnline);
            ps.setLong(2, this.statusHolder.getWurmId());
            ps.executeUpdate();
        }
        catch (SQLException sqex) {
            DbCreatureStatus.logger.log(Level.WARNING, "Failed to set stayOnline to " + this.stayOnline + " for " + this.statusHolder.getWurmId() + " " + sqex.getMessage(), sqex);
        }
        finally {
            DbUtilities.closeDatabaseObjects(ps, null);
            DbConnector.returnConnection(dbcon);
        }
        return this.stayOnline;
    }
    
    public void setType(final byte newtype) {
        this.modtype = newtype;
        if (this.modtype == 11) {
            this.disease = 1;
        }
        Connection dbcon = null;
        PreparedStatement ps = null;
        try {
            dbcon = DbConnector.getCreatureDbCon();
            if (this.statusHolder.isPlayer()) {
                dbcon = DbConnector.getPlayerDbCon();
                ps = dbcon.prepareStatement("update PLAYERS set TYPE=? WHERE WURMID=?");
            }
            else {
                ps = dbcon.prepareStatement("update CREATURES set TYPE=? WHERE WURMID=?");
            }
            ps.setByte(1, this.modtype);
            ps.setLong(2, this.statusHolder.getWurmId());
            ps.executeUpdate();
        }
        catch (SQLException sqex) {
            DbCreatureStatus.logger.log(Level.WARNING, "Failed to set type to " + this.modtype + " for " + this.statusHolder.getWurmId() + " " + sqex.getMessage(), sqex);
        }
        finally {
            DbUtilities.closeDatabaseObjects(ps, null);
            DbConnector.returnConnection(dbcon);
        }
    }
    
    protected void setInheritance(final long _traits, final long _mother, final long _father) throws IOException {
        this.traits = _traits;
        this.mother = _mother;
        this.father = _father;
        Connection dbcon = null;
        PreparedStatement ps = null;
        try {
            dbcon = DbConnector.getCreatureDbCon();
            if (this.statusHolder.isPlayer()) {
                dbcon = DbConnector.getPlayerDbCon();
                ps = dbcon.prepareStatement("update PLAYERS set TRAITS=?,MOTHER=?,FATHER=? WHERE WURMID=?");
            }
            else {
                ps = dbcon.prepareStatement("update CREATURES set TRAITS=?,MOTHER=?,FATHER=? WHERE WURMID=?");
            }
            ps.setLong(1, this.traits);
            ps.setLong(2, this.mother);
            ps.setLong(3, this.father);
            ps.setLong(4, this.statusHolder.getWurmId());
            ps.executeUpdate();
        }
        catch (SQLException sqex) {
            DbCreatureStatus.logger.log(Level.WARNING, "Failed to set type to " + this.modtype + " for " + this.statusHolder.getWurmId() + " " + sqex.getMessage(), sqex);
        }
        finally {
            DbUtilities.closeDatabaseObjects(ps, null);
            DbConnector.returnConnection(dbcon);
        }
    }
    
    public void saveCreatureName(final String name) {
        Connection dbcon = null;
        PreparedStatement ps = null;
        try {
            dbcon = DbConnector.getCreatureDbCon();
            ps = dbcon.prepareStatement("update CREATURES set NAME=? WHERE WURMID=?");
            ps.setString(1, name);
            ps.setLong(2, this.statusHolder.getWurmId());
            ps.executeUpdate();
        }
        catch (SQLException sqex) {
            DbCreatureStatus.logger.log(Level.WARNING, "Failed to save name for " + this.statusHolder.getName() + " to " + name + " ," + this.statusHolder.getWurmId() + " " + sqex.getMessage(), sqex);
        }
        finally {
            DbUtilities.closeDatabaseObjects(ps, null);
            DbConnector.returnConnection(dbcon);
        }
    }
    
    public void setLastGroomed(final long _lastGroomed) {
        this.lastGroomed = _lastGroomed;
        Connection dbcon = null;
        PreparedStatement ps = null;
        try {
            dbcon = DbConnector.getCreatureDbCon();
            ps = dbcon.prepareStatement("update CREATURES set LASTGROOMED=? WHERE WURMID=?");
            ps.setLong(1, this.lastGroomed);
            ps.setLong(2, this.statusHolder.getWurmId());
            ps.executeUpdate();
        }
        catch (SQLException sqex) {
            DbCreatureStatus.logger.log(Level.WARNING, "Failed to set lastgroomed for " + this.statusHolder.getName() + " ," + this.statusHolder.getWurmId() + " " + sqex.getMessage(), sqex);
        }
        finally {
            DbUtilities.closeDatabaseObjects(ps, null);
            DbConnector.returnConnection(dbcon);
        }
    }
    
    protected void setDisease(final byte _disease) {
        this.disease = _disease;
        Connection dbcon = null;
        PreparedStatement ps = null;
        try {
            dbcon = DbConnector.getCreatureDbCon();
            if (this.statusHolder.isPlayer()) {
                dbcon = DbConnector.getPlayerDbCon();
                ps = dbcon.prepareStatement("update PLAYERS set DISEASE=? WHERE WURMID=?");
            }
            else {
                ps = dbcon.prepareStatement("update CREATURES set DISEASE=? WHERE WURMID=?");
            }
            ps.setByte(1, this.disease);
            ps.setLong(2, this.statusHolder.getWurmId());
            ps.executeUpdate();
        }
        catch (SQLException sqex) {
            DbCreatureStatus.logger.log(Level.WARNING, "Failed to set disease for " + this.statusHolder.getWurmId() + " " + sqex.getMessage(), sqex);
        }
        finally {
            DbUtilities.closeDatabaseObjects(ps, null);
            DbConnector.returnConnection(dbcon);
        }
    }
    
    @Override
    public final void setVehicle(final long vehicleId, final byte seatType) {
        if (!this.statusHolder.isPlayer()) {
            Connection dbcon = null;
            PreparedStatement ps = null;
            try {
                dbcon = DbConnector.getCreatureDbCon();
                ps = dbcon.prepareStatement("update CREATURES set VEHICLE=?,SEAT_TYPE=? WHERE WURMID=?");
                ps.setLong(1, vehicleId);
                ps.setByte(2, seatType);
                ps.setLong(3, this.statusHolder.getWurmId());
                ps.executeUpdate();
            }
            catch (SQLException sqex) {
                DbCreatureStatus.logger.log(Level.WARNING, "Failed to set hitched to for " + this.statusHolder.getWurmId() + " " + sqex.getMessage(), sqex);
            }
            finally {
                DbUtilities.closeDatabaseObjects(ps, null);
                DbConnector.returnConnection(dbcon);
            }
        }
    }
    
    public static void setLoaded(final int loadstate, final long cretID) {
        Connection dbcon = null;
        PreparedStatement ps = null;
        try {
            dbcon = DbConnector.getCreatureDbCon();
            ps = dbcon.prepareStatement("update CREATURES set ISLOADED=? WHERE WURMID=?");
            ps.setInt(1, loadstate);
            ps.setLong(2, cretID);
            ps.executeUpdate();
        }
        catch (SQLException sqex) {
            DbCreatureStatus.logger.log(Level.WARNING, "Failed to set loadstate to for " + cretID + " " + sqex.getMessage(), sqex);
        }
        finally {
            DbUtilities.closeDatabaseObjects(ps, null);
            DbConnector.returnConnection(dbcon);
        }
    }
    
    public static int getIsLoaded(final long cretID) {
        Statement stmt = null;
        ResultSet rs = null;
        int isLoaded = 0;
        try {
            final Connection dbcon = DbConnector.getCreatureDbCon();
            stmt = dbcon.createStatement();
            rs = stmt.executeQuery("select * from CREATURES where WURMID=" + cretID + "");
            if (rs.next()) {
                isLoaded = rs.getInt("ISLOADED");
            }
        }
        catch (SQLException ex) {
            ex.printStackTrace();
        }
        finally {
            DbUtilities.closeDatabaseObjects(stmt, rs);
        }
        return isLoaded;
    }
    
    static {
        logger = Logger.getLogger(DbCreatureStatus.class.getName());
    }
}
