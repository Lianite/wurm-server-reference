// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.villages;

import java.io.IOException;
import javax.annotation.Nullable;
import com.wurmonline.server.creatures.Creature;
import com.wurmonline.server.Message;
import java.sql.ResultSet;
import java.sql.PreparedStatement;
import java.sql.Connection;
import java.sql.Statement;
import com.wurmonline.server.utils.DbUtilities;
import java.sql.SQLException;
import com.wurmonline.server.DbConnector;
import java.util.logging.Level;
import java.util.Iterator;
import java.util.HashSet;
import com.wurmonline.server.players.MapAnnotation;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

public class PvPAlliance
{
    private static final Logger logger;
    private static final String LOAD_ENTRIES = "SELECT * FROM PVPALLIANCE";
    private static final String CREATE_ENTRY = "INSERT INTO PVPALLIANCE (ALLIANCENUMBER,NAME) VALUES(?,?)";
    private static final String DELETE_ENTRY = "DELETE FROM PVPALLIANCE WHERE ALLIANCENUMBER=?";
    private static final String UPDATE_NAME = "UPDATE PVPALLIANCE SET NAME=? WHERE ALLIANCENUMBER=?";
    private static final String UPDATE_WINS = "UPDATE PVPALLIANCE SET WINS=? WHERE ALLIANCENUMBER=?";
    private static final String UPDATE_DEITYONE = "UPDATE PVPALLIANCE SET DEITYONE=? WHERE ALLIANCENUMBER=?";
    private static final String UPDATE_DEITYTWO = "UPDATE PVPALLIANCE SET DEITYTWO=? WHERE ALLIANCENUMBER=?";
    private static final String UPDATE_MOTD = "UPDATE PVPALLIANCE SET MOTD=? WHERE ALLIANCENUMBER=?";
    private static final String UPDATE_ALLIANCENUMBER = "UPDATE PVPALLIANCE SET ALLIANCENUMBER=? WHERE ALLIANCENUMBER=?";
    private static final String GET_ALLIANCE_MAP_POI = "SELECT * FROM MAP_ANNOTATIONS WHERE POITYPE=2 AND OWNERID=?";
    private static final String DELETE_ALLIANCE_MAP_POIS = "DELETE FROM MAP_ANNOTATIONS WHERE OWNERID=? AND POITYPE=2;";
    private String name;
    private int idNumber;
    private int wins;
    private byte deityOneId;
    private byte deityTwoId;
    private static final ConcurrentHashMap<Integer, PvPAlliance> alliances;
    private final CopyOnWriteArraySet<AllianceWar> wars;
    private static final PvPAlliance[] emptyAlliances;
    private static final AllianceWar[] emptyWars;
    private String motd;
    private final Set<MapAnnotation> mapAnnotations;
    
    public PvPAlliance(final int id, final String allianceName) {
        this.name = "unknown";
        this.idNumber = 0;
        this.wins = 0;
        this.deityOneId = 0;
        this.deityTwoId = 0;
        this.wars = new CopyOnWriteArraySet<AllianceWar>();
        this.motd = "";
        this.mapAnnotations = new HashSet<MapAnnotation>();
        this.idNumber = id;
        this.name = allianceName.substring(0, Math.min(allianceName.length(), 20));
        this.create();
        addAlliance(this);
    }
    
    public final void addHotaWin() {
        this.setWins(this.wins + 1);
        for (final Village vill : this.getVillages()) {
            vill.addHotaWin();
            if (vill.getAllianceNumber() == vill.getId()) {
                vill.createHotaPrize(this.getNumberOfWins());
            }
        }
    }
    
    public void addAllianceWar(final AllianceWar toadd) {
        this.wars.add(toadd);
    }
    
    public final void removeWar(final AllianceWar war) {
        this.wars.remove(war);
    }
    
    public final AllianceWar getWarWith(final int allianceNum) {
        for (final AllianceWar war : this.wars) {
            if (war.getAggressor() == allianceNum || war.getDefender() == allianceNum) {
                return war;
            }
        }
        return null;
    }
    
    public final AllianceWar[] getWars() {
        if (this.wars.isEmpty()) {
            return PvPAlliance.emptyWars;
        }
        return this.wars.toArray(new AllianceWar[this.wars.size()]);
    }
    
    public static final PvPAlliance[] getAllAlliances() {
        if (PvPAlliance.alliances.size() > 0) {
            return PvPAlliance.alliances.values().toArray(new PvPAlliance[PvPAlliance.alliances.size()]);
        }
        return PvPAlliance.emptyAlliances;
    }
    
    public final boolean isAtWarWith(final int allianceNum) {
        if (allianceNum > 0) {
            for (final AllianceWar war : this.wars) {
                if (war.getAggressor() == allianceNum || war.getDefender() == allianceNum) {
                    return war.isActive();
                }
            }
        }
        return false;
    }
    
    public PvPAlliance(final int id, final String allianceName, final byte deityOne, final byte deityTwo, final int _wins, final String _motd) {
        this.name = "unknown";
        this.idNumber = 0;
        this.wins = 0;
        this.deityOneId = 0;
        this.deityTwoId = 0;
        this.wars = new CopyOnWriteArraySet<AllianceWar>();
        this.motd = "";
        this.mapAnnotations = new HashSet<MapAnnotation>();
        this.idNumber = id;
        this.name = allianceName.substring(0, Math.min(allianceName.length(), 20));
        this.deityOneId = deityOne;
        this.deityTwoId = deityTwo;
        this.wins = _wins;
        this.motd = _motd;
    }
    
    private static final void addAlliance(final PvPAlliance alliance) {
        PvPAlliance.alliances.put(alliance.getId(), alliance);
    }
    
    public static final PvPAlliance getPvPAlliance(final int allianceId) {
        return PvPAlliance.alliances.get(allianceId);
    }
    
    public static final void loadPvPAlliances() {
        PvPAlliance.logger.log(Level.INFO, "Loading all PVP Alliances.");
        final long start = System.nanoTime();
        Connection dbcon = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            dbcon = DbConnector.getZonesDbCon();
            ps = dbcon.prepareStatement("SELECT * FROM PVPALLIANCE");
            rs = ps.executeQuery();
            while (rs.next()) {
                final int allianceId = rs.getInt("ALLIANCENUMBER");
                final String name = rs.getString("NAME");
                final byte deityOne = rs.getByte("DEITYONE");
                final byte deityTwo = rs.getByte("DEITYTWO");
                final int wins = rs.getInt("WINS");
                final String motd = rs.getString("MOTD");
                final PvPAlliance toAdd = new PvPAlliance(allianceId, name, deityOne, deityTwo, wins, motd);
                toAdd.loadAllianceMapAnnotations();
                addAlliance(toAdd);
            }
        }
        catch (SQLException sqx) {
            PvPAlliance.logger.log(Level.WARNING, "Failed to load pvp alliances " + sqx.getMessage(), sqx);
        }
        finally {
            DbUtilities.closeDatabaseObjects(ps, rs);
            DbConnector.returnConnection(dbcon);
            final long end = System.nanoTime();
            PvPAlliance.logger.info("Loaded PVP Alliances from database took " + (end - start) / 1000000.0f + " ms");
        }
    }
    
    void loadAllianceMapAnnotations() {
        Connection dbcon = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            dbcon = DbConnector.getPlayerDbCon();
            ps = dbcon.prepareStatement("SELECT * FROM MAP_ANNOTATIONS WHERE POITYPE=2 AND OWNERID=?");
            ps.setLong(1, this.getId());
            rs = ps.executeQuery();
            while (rs.next()) {
                final long wid = rs.getLong("ID");
                final String poiName = rs.getString("NAME");
                final long position = rs.getLong("POSITION");
                final byte type = rs.getByte("POITYPE");
                final long ownerId = rs.getLong("OWNERID");
                final String server = rs.getString("SERVER");
                final byte icon = rs.getByte("ICON");
                this.addAllianceMapAnnotation(new MapAnnotation(wid, poiName, type, position, ownerId, server, icon), false);
            }
        }
        catch (SQLException sqx) {
            PvPAlliance.logger.log(Level.WARNING, "Problem loading all alliance POI's for alliance nr: " + this.getId() + " - " + sqx.getMessage(), sqx);
        }
        finally {
            DbUtilities.closeDatabaseObjects(ps, rs);
            DbConnector.returnConnection(dbcon);
        }
    }
    
    public int getId() {
        return this.idNumber;
    }
    
    public String getName() {
        return this.name;
    }
    
    public int getNumberOfWins() {
        return this.wins;
    }
    
    public byte getFavoredDeityOne() {
        return this.deityOneId;
    }
    
    public byte getFavoredDeityTwo() {
        return this.deityTwoId;
    }
    
    public String getMotd() {
        return this.motd;
    }
    
    public final Message getMotdMessage() {
        return new Message(null, (byte)15, "Alliance", "MOTD: " + this.motd, 250, 150, 250);
    }
    
    private final void create() {
        Connection dbcon = null;
        PreparedStatement ps = null;
        try {
            dbcon = DbConnector.getZonesDbCon();
            ps = dbcon.prepareStatement("INSERT INTO PVPALLIANCE (ALLIANCENUMBER,NAME) VALUES(?,?)");
            ps.setInt(1, this.idNumber);
            ps.setString(2, this.name);
            ps.executeUpdate();
        }
        catch (SQLException sqx) {
            PvPAlliance.logger.log(Level.WARNING, sqx.getMessage(), sqx);
        }
        finally {
            DbUtilities.closeDatabaseObjects(ps, null);
            DbConnector.returnConnection(dbcon);
        }
    }
    
    public final boolean exists() {
        for (final Village v : Villages.getVillages()) {
            if (v.getAllianceNumber() == this.getId() && v.getAllianceNumber() != v.getId()) {
                return true;
            }
        }
        return false;
    }
    
    public final Village[] getVillages() {
        final Set<Village> toReturn = new HashSet<Village>();
        for (final Village v : Villages.getVillages()) {
            if (v.getAllianceNumber() == this.getId()) {
                toReturn.add(v);
            }
        }
        return toReturn.toArray(new Village[toReturn.size()]);
    }
    
    public final Village getAllianceCapital() {
        final Village[] villages2;
        final Village[] villages = villages2 = this.getVillages();
        for (final Village village : villages2) {
            if (village.getId() == village.getAllianceNumber()) {
                return village;
            }
        }
        return null;
    }
    
    public final void disband() {
        for (final Village v : Villages.getVillages()) {
            if (v.getAllianceNumber() == this.getId()) {
                v.setAllianceNumber(0);
                v.sendClearMapAnnotationsOfType((byte)2);
            }
        }
        this.delete();
        this.deleteAllianceMapAnnotations();
    }
    
    public final void deleteAllianceMapAnnotations() {
        Connection dbcon = null;
        PreparedStatement ps = null;
        try {
            dbcon = DbConnector.getPlayerDbCon();
            ps = dbcon.prepareStatement("DELETE FROM MAP_ANNOTATIONS WHERE OWNERID=? AND POITYPE=2;");
            ps.setLong(1, this.idNumber);
            ps.executeUpdate();
        }
        catch (SQLException sqx) {
            PvPAlliance.logger.log(Level.WARNING, "Failed to delete alliance map annotations with alliance id=" + this.idNumber, sqx);
        }
        finally {
            DbUtilities.closeDatabaseObjects(ps, null);
            DbConnector.returnConnection(dbcon);
        }
    }
    
    public final void delete() {
        PvPAlliance.alliances.remove(this.idNumber);
        Connection dbcon = null;
        PreparedStatement ps = null;
        try {
            dbcon = DbConnector.getZonesDbCon();
            ps = dbcon.prepareStatement("DELETE FROM PVPALLIANCE WHERE ALLIANCENUMBER=?");
            ps.setInt(1, this.idNumber);
            ps.executeUpdate();
        }
        catch (SQLException sqx) {
            PvPAlliance.logger.log(Level.WARNING, sqx.getMessage(), sqx);
        }
        finally {
            DbUtilities.closeDatabaseObjects(ps, null);
            DbConnector.returnConnection(dbcon);
        }
    }
    
    public void setName(@Nullable final Creature changer, final String aName) {
        if (!this.name.equals(aName)) {
            this.name = aName.substring(0, Math.min(aName.length(), 20));
            Connection dbcon = null;
            PreparedStatement ps = null;
            try {
                dbcon = DbConnector.getZonesDbCon();
                ps = dbcon.prepareStatement("UPDATE PVPALLIANCE SET NAME=? WHERE ALLIANCENUMBER=?");
                ps.setString(1, aName);
                ps.setInt(2, this.idNumber);
                ps.executeUpdate();
            }
            catch (SQLException sqx) {
                PvPAlliance.logger.log(Level.WARNING, sqx.getMessage(), sqx);
            }
            finally {
                DbUtilities.closeDatabaseObjects(ps, null);
                DbConnector.returnConnection(dbcon);
            }
            if (changer != null) {
                this.broadCastAllianceMessage(changer, " changed the name of the alliance to " + this.getName() + ".");
            }
        }
    }
    
    public void setIdNumber(final int aIdNumber) {
        if (this.idNumber != aIdNumber) {
            Connection dbcon = null;
            PreparedStatement ps = null;
            try {
                dbcon = DbConnector.getZonesDbCon();
                ps = dbcon.prepareStatement("UPDATE PVPALLIANCE SET ALLIANCENUMBER=? WHERE ALLIANCENUMBER=?");
                ps.setInt(1, aIdNumber);
                ps.setInt(2, this.idNumber);
                ps.executeUpdate();
                PvPAlliance.alliances.remove(this.idNumber);
                this.idNumber = aIdNumber;
                addAlliance(this);
            }
            catch (SQLException sqx) {
                PvPAlliance.logger.log(Level.WARNING, sqx.getMessage(), sqx);
            }
            finally {
                DbUtilities.closeDatabaseObjects(ps, null);
                DbConnector.returnConnection(dbcon);
            }
        }
    }
    
    public void setWins(final int aWins) {
        if (this.wins != aWins) {
            this.wins = aWins;
            Connection dbcon = null;
            PreparedStatement ps = null;
            try {
                dbcon = DbConnector.getZonesDbCon();
                ps = dbcon.prepareStatement("UPDATE PVPALLIANCE SET WINS=? WHERE ALLIANCENUMBER=?");
                ps.setInt(1, aWins);
                ps.setInt(2, this.idNumber);
                ps.executeUpdate();
            }
            catch (SQLException sqx) {
                PvPAlliance.logger.log(Level.WARNING, sqx.getMessage(), sqx);
            }
            finally {
                DbUtilities.closeDatabaseObjects(ps, null);
                DbConnector.returnConnection(dbcon);
            }
        }
    }
    
    public void setDeityOneId(final byte aDeityOneId) {
        if (this.deityOneId != aDeityOneId) {
            this.deityOneId = aDeityOneId;
            Connection dbcon = null;
            PreparedStatement ps = null;
            try {
                dbcon = DbConnector.getZonesDbCon();
                ps = dbcon.prepareStatement("UPDATE PVPALLIANCE SET DEITYONE=? WHERE ALLIANCENUMBER=?");
                ps.setByte(1, aDeityOneId);
                ps.setInt(2, this.idNumber);
                ps.executeUpdate();
            }
            catch (SQLException sqx) {
                PvPAlliance.logger.log(Level.WARNING, sqx.getMessage(), sqx);
            }
            finally {
                DbUtilities.closeDatabaseObjects(ps, null);
                DbConnector.returnConnection(dbcon);
            }
        }
    }
    
    public final void broadCastMessage(final Message message) {
        for (final Village v : this.getVillages()) {
            v.broadCastMessage(message, false);
        }
    }
    
    public void setDeityTwoId(final byte aDeityTwoId) {
        if (this.deityTwoId != aDeityTwoId) {
            this.deityTwoId = aDeityTwoId;
            Connection dbcon = null;
            PreparedStatement ps = null;
            try {
                dbcon = DbConnector.getZonesDbCon();
                ps = dbcon.prepareStatement("UPDATE PVPALLIANCE SET DEITYTWO=? WHERE ALLIANCENUMBER=?");
                ps.setByte(1, aDeityTwoId);
                ps.setInt(2, this.idNumber);
                ps.executeUpdate();
            }
            catch (SQLException sqx) {
                PvPAlliance.logger.log(Level.WARNING, sqx.getMessage(), sqx);
            }
            finally {
                DbUtilities.closeDatabaseObjects(ps, null);
                DbConnector.returnConnection(dbcon);
            }
        }
    }
    
    public void setMotd(final String aMotd) {
        if (this.motd != null && !this.motd.equals(aMotd)) {
            this.motd = aMotd;
            Connection dbcon = null;
            PreparedStatement ps = null;
            try {
                dbcon = DbConnector.getZonesDbCon();
                ps = dbcon.prepareStatement("UPDATE PVPALLIANCE SET MOTD=? WHERE ALLIANCENUMBER=?");
                ps.setString(1, aMotd);
                ps.setInt(2, this.idNumber);
                ps.executeUpdate();
            }
            catch (SQLException sqx) {
                PvPAlliance.logger.log(Level.WARNING, sqx.getMessage(), sqx);
            }
            finally {
                DbUtilities.closeDatabaseObjects(ps, null);
                DbConnector.returnConnection(dbcon);
            }
            if (aMotd != null && aMotd.length() > 0) {
                this.broadCastMessage(this.getMotdMessage());
            }
        }
    }
    
    public final void broadCastAllianceMessage(final Creature sender, final String toSend) {
        this.broadCastMessage(this.createAllianceMessage(sender, toSend));
    }
    
    public final Message createAllianceMessage(final Creature sender, final String toSend) {
        return new Message(sender, (byte)15, "Alliance", "<" + sender.getName() + "> " + toSend);
    }
    
    public final Message createAllianceEventMessage(final Creature sender, final String toSend) {
        return new Message(sender, (byte)1, ":Event", "<" + sender.getName() + "> " + toSend);
    }
    
    public final void disband(final Creature disbander) {
        for (final Village v : this.getVillages()) {
            v.broadCastMessage(this.createAllianceEventMessage(disbander, "disbanded " + this.getName() + "."));
            v.setAllianceNumber(0);
            v.sendClearMapAnnotationsOfType((byte)2);
        }
        this.delete();
        this.deleteAllianceMapAnnotations();
    }
    
    public final void transferControl(final Creature disbander, final int newCapital) {
        try {
            final Village vill = Villages.getVillage(newCapital);
            for (final Village v : this.getVillages()) {
                v.broadCastMessage(this.createAllianceEventMessage(disbander, "transfers control of " + this.getName() + " to " + vill.getName() + "."));
                v.setAllianceNumber(newCapital);
            }
            this.setIdNumber(newCapital);
        }
        catch (NoSuchVillageException ex) {}
    }
    
    public final boolean addAllianceMapAnnotation(final MapAnnotation annotation, final boolean send) {
        if (this.mapAnnotations.size() < 500) {
            this.mapAnnotations.add(annotation);
            if (send) {
                this.sendAddAnnotations(new MapAnnotation[] { annotation });
            }
            return true;
        }
        return false;
    }
    
    public void removeAllianceMapAnnotation(final MapAnnotation annotation) {
        if (this.mapAnnotations.contains(annotation)) {
            try {
                this.mapAnnotations.remove(annotation);
                MapAnnotation.deleteAnnotation(annotation.getId());
                this.sendRemoveAnnotation(annotation);
            }
            catch (IOException iex) {
                PvPAlliance.logger.log(Level.WARNING, "Error deleting alliance map annotation: " + annotation.getId() + " " + iex.getMessage(), iex);
            }
        }
    }
    
    private final void sendAddAnnotations(final MapAnnotation[] annotations) {
        for (final Village v : this.getVillages()) {
            v.sendMapAnnotationsToVillagers(annotations);
        }
    }
    
    private final void sendRemoveAnnotation(final MapAnnotation annotation) {
        for (final Village v : this.getVillages()) {
            v.sendRemoveMapAnnotationToVillagers(annotation);
        }
    }
    
    public final void sendClearAllianceAnnotations() {
        for (final Village village : this.getVillages()) {
            village.sendClearMapAnnotationsOfType((byte)2);
        }
    }
    
    public final Set<MapAnnotation> getAllianceMapAnnotations() {
        return this.mapAnnotations;
    }
    
    public final MapAnnotation[] getAllianceMapAnnotationsArray() {
        if (this.mapAnnotations == null || this.mapAnnotations.size() == 0) {
            return null;
        }
        final MapAnnotation[] annos = new MapAnnotation[this.mapAnnotations.size()];
        this.mapAnnotations.toArray(annos);
        return annos;
    }
    
    static {
        logger = Logger.getLogger(PvPAlliance.class.getName());
        alliances = new ConcurrentHashMap<Integer, PvPAlliance>();
        emptyAlliances = new PvPAlliance[0];
        emptyWars = new AllianceWar[0];
    }
}
