// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.villages;

import com.wurmonline.server.kingdom.Kingdom;
import com.wurmonline.server.kingdom.Kingdoms;
import com.wurmonline.server.HistoryEvent;
import java.util.LinkedList;
import com.wurmonline.server.Groups;
import com.wurmonline.server.Servers;
import com.wurmonline.server.creatures.Creatures;
import com.wurmonline.server.Players;
import com.wurmonline.server.WurmId;
import com.wurmonline.server.players.MapAnnotation;
import java.util.logging.Level;
import java.sql.ResultSet;
import java.sql.PreparedStatement;
import java.sql.Connection;
import java.sql.Statement;
import com.wurmonline.server.utils.DbUtilities;
import java.sql.SQLException;
import com.wurmonline.server.DbConnector;
import java.io.IOException;
import com.wurmonline.server.NoSuchPlayerException;
import com.wurmonline.server.creatures.NoSuchCreatureException;
import com.wurmonline.server.creatures.Creature;
import java.util.logging.Logger;

public final class DbVillage extends Village
{
    private static final Logger logger;
    private static final String CREATE_VILLAGE = "INSERT INTO VILLAGES (NAME,FOUNDER ,MAYOR ,CREATIONDATE, STARTX ,ENDX,STARTY ,ENDY, DEEDID, SURFACED, DEMOCRACY, DEVISE, HOMESTEAD, TOKEN,LASTLOGIN, KINGDOM,UPKEEP,ACCEPTSHOMESTEADS,PERMANENT,MERCHANTS,SPAWNKINGDOM,PERIMETER,MOTD) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
    private static final String GET_CITIZENS = "SELECT * FROM CITIZENS WHERE VILLAGEID=?";
    private static final String GET_ROLES = "SELECT * FROM VILLAGEROLE WHERE VILLAGEID=?";
    private static final String SET_MAYOR = "UPDATE VILLAGES SET MAYOR=? WHERE ID=?";
    private static final String SET_DEVISE = "UPDATE VILLAGES SET DEVISE=? WHERE ID=?";
    private static final String SET_STARTX = "UPDATE VILLAGES SET STARTX=? WHERE ID=?";
    private static final String SET_STARTY = "UPDATE VILLAGES SET STARTY=? WHERE ID=?";
    private static final String SET_ENDX = "UPDATE VILLAGES SET ENDX=? WHERE ID=?";
    private static final String SET_ENDY = "UPDATE VILLAGES SET ENDY=? WHERE ID=?";
    private static final String SET_DEEDID = "UPDATE VILLAGES SET DEEDID=? WHERE ID=?";
    private static final String SET_NAME = "UPDATE VILLAGES SET NAME=? WHERE ID=?";
    private static final String SET_DISBAND = "UPDATE VILLAGES SET DISBAND=? WHERE ID=?";
    private static final String SET_DISBANDER = "UPDATE VILLAGES SET DISBANDER=? WHERE ID=?";
    private static final String SET_LASTLOGIN = "UPDATE VILLAGES SET LASTLOGIN=? WHERE ID=?";
    private static final String SET_DEMOCRACY = "UPDATE VILLAGES SET DEMOCRACY=? WHERE ID=?";
    private static final String SET_TOKEN = "UPDATE VILLAGES SET TOKEN=? WHERE ID=?";
    private static final String SET_MAXCITIZENS = "UPDATE VILLAGES SET MAXCITIZENS=? WHERE ID=?";
    private static final String DELETE = "UPDATE VILLAGES SET DISBANDED=1 WHERE ID=?";
    private static final String SET_PERIMETER = "UPDATE VILLAGES SET PERIMETER=? WHERE ID=?";
    private static final String SET_UPKEEP = "UPDATE VILLAGES SET UPKEEP=? WHERE ID=?";
    private static final String SET_MAYPICKUP = "UPDATE VILLAGES SET MAYPICKUP=? WHERE ID=?";
    private static final String SET_UNLIMITEDCITIZENS = "UPDATE VILLAGES SET ACCEPTSHOMESTEADS=? WHERE ID=?";
    private static final String SET_ACCEPTSMERCHANTS = "UPDATE VILLAGES SET MERCHANTS=? WHERE ID=?";
    private static final String SET_ALLOWSAGGRO = "UPDATE VILLAGES SET AGGROS=? WHERE ID=?";
    private static final String GET_REPUTATIONS = "SELECT REPUTATION,PERMANENT, WURMID FROM REPUTATION WHERE VILLAGEID=?";
    private static final String GET_GUARDS = "SELECT * FROM GUARDS WHERE VILLAGEID=?";
    private static final String ADD_HISTORY = "INSERT INTO HISTORY(EVENTDATE,VILLAGEID,PERFORMER,EVENT) VALUES (?,?,?,?)";
    private static final String GET_HISTORY = "SELECT EVENTDATE, VILLAGEID, PERFORMER, EVENT FROM HISTORY WHERE VILLAGEID=? ORDER BY EVENTDATE DESC";
    private static final String SET_KINGDOM = "UPDATE VILLAGES SET KINGDOM=? WHERE ID=?";
    private static final String SET_TWITTER = "UPDATE VILLAGES SET TWITKEY=?,TWITSECRET=?,TWITAPP=?,TWITAPPSECRET=?,TWITCHAT=?,TWITENABLE=? WHERE ID=?";
    private static final String UPDATE_FAITHWAR = "UPDATE VILLAGES SET FAITHWAR=? WHERE ID=?";
    private static final String UPDATE_FAITHHEAL = "UPDATE VILLAGES SET FAITHHEAL=? WHERE ID=?";
    private static final String UPDATE_FAITHCREATE = "UPDATE VILLAGES SET FAITHCREATE=? WHERE ID=?";
    private static final String SET_SPAWNSITUATION = "UPDATE VILLAGES SET SPAWNSITUATION=? WHERE ID=?";
    private static final String SET_ALLIANCENUMBER = "UPDATE VILLAGES SET ALLIANCENUMBER=? WHERE ID=?";
    private static final String SET_HOTAWINS = "UPDATE VILLAGES SET HOTAWINS=? WHERE ID=?";
    private static final String SET_LASTCHANGENAMED = "UPDATE VILLAGES SET NAMECHANGED=? WHERE ID=?";
    private static final String SET_MOTD = "UPDATE VILLAGES SET MOTD=? WHERE ID=?";
    private static final String SET_VILLAGEREP = "UPDATE VILLAGES SET VILLAGEREP=? WHERE ID=?";
    private static final String GET_VILLAGE_MAP_POI = "SELECT * FROM MAP_ANNOTATIONS WHERE POITYPE=1 AND OWNERID=?";
    private static final String DELETE_VILLAGE_MAP_POIS = "DELETE FROM MAP_ANNOTATIONS WHERE OWNERID=? AND POITYPE=1;";
    private static final String GET_RECRUITEES = "SELECT RECRUITEEID, RECRUITEENAME FROM VILLAGERECRUITEES WHERE VILLAGEID =?;";
    private static final String INSERT_RECRUITEE = "INSERT INTO VILLAGERECRUITEES(VILLAGEID, RECRUITEEID, RECRUITEENAME) VALUES (?,?,?)";
    private static final String DELETE_RECRUITEE = "DELETE FROM VILLAGERECRUITEES WHERE(VILLAGEID=? AND RECRUITEEID=?);";
    
    DbVillage(final int aStartX, final int aEndX, final int aStartY, final int aEndY, final String aName, final Creature aFounder, final long aDeedid, final boolean aSurfaced, final boolean aDemocracy, final String aDevise, final boolean aPermanent, final byte aSpawnKingdom, final int initialPerimeter) throws NoSuchCreatureException, NoSuchPlayerException, IOException {
        super(aStartX, aEndX, aStartY, aEndY, aName, aFounder, aDeedid, aSurfaced, aDemocracy, aDevise, aPermanent, aSpawnKingdom, initialPerimeter);
    }
    
    DbVillage(final int aId, final int aStartX, final int aEndX, final int aStartY, final int aEndY, final String aName, final String aFounderName, final String aMayor, final long aDeedid, final boolean aSurfaced, final boolean aDemocracy, final String aDevise, final long aCreationDate, final boolean aHomestead, final long aTokenid, final long aDisband, final long aDisbander, final long aLastlogin, final byte aKingdom, final long aUpkeep, final byte aSettings, final boolean aAcceptsHomesteads, final boolean aAcceptsMerchants, final int aMaxcitiz, final boolean aPermanent, final byte aSpawnKingdom, final int perimetert, final boolean aggros, final String _consumerKeyToUse, final String _consumerSecretToUse, final String _applicationToken, final String _applicationSecret, final boolean _twitChat, final boolean _twitEnabled, final float _faithWar, final float _faithHeal, final float _faithCreate, final byte _spawnSituation, final int _allianceNumber, final short _hotaWins, final long lastChangeName, final String _motd) {
        super(aId, aStartX, aEndX, aStartY, aEndY, aName, aFounderName, aMayor, aDeedid, aSurfaced, aDemocracy, aDevise, aCreationDate, aHomestead, aTokenid, aDisband, aDisbander, aLastlogin, aKingdom, aUpkeep, aSettings, aAcceptsHomesteads, aAcceptsMerchants, aMaxcitiz, aPermanent, aSpawnKingdom, perimetert, aggros, _consumerKeyToUse, _consumerSecretToUse, _applicationToken, _applicationSecret, _twitChat, _twitEnabled, _faithWar, _faithHeal, _faithCreate, _spawnSituation, _allianceNumber, _hotaWins, lastChangeName, _motd);
    }
    
    @Override
    int create() throws IOException {
        Connection dbcon = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            dbcon = DbConnector.getZonesDbCon();
            ps = dbcon.prepareStatement("INSERT INTO VILLAGES (NAME,FOUNDER ,MAYOR ,CREATIONDATE, STARTX ,ENDX,STARTY ,ENDY, DEEDID, SURFACED, DEMOCRACY, DEVISE, HOMESTEAD, TOKEN,LASTLOGIN, KINGDOM,UPKEEP,ACCEPTSHOMESTEADS,PERMANENT,MERCHANTS,SPAWNKINGDOM,PERIMETER,MOTD) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)", 1);
            ps.setString(1, this.name);
            ps.setString(2, this.founderName);
            ps.setString(3, this.mayorName);
            ps.setLong(4, this.creationDate);
            ps.setInt(5, this.startx);
            ps.setInt(6, this.endx);
            ps.setInt(7, this.starty);
            ps.setInt(8, this.endy);
            ps.setLong(9, this.deedid);
            ps.setBoolean(10, this.surfaced);
            ps.setBoolean(11, this.democracy);
            ps.setString(12, this.motto);
            ps.setBoolean(13, false);
            ps.setLong(14, this.tokenId);
            ps.setLong(15, this.lastLogin);
            ps.setByte(16, this.kingdom);
            ps.setLong(17, this.upkeep);
            ps.setBoolean(18, this.unlimitedCitizens);
            ps.setBoolean(19, this.isPermanent);
            ps.setBoolean(20, this.acceptsMerchants);
            ps.setByte(21, this.spawnKingdom);
            ps.setInt(22, this.perimeterTiles);
            ps.setString(23, this.motd);
            ps.executeUpdate();
            rs = ps.getGeneratedKeys();
            if (rs.next()) {
                this.id = rs.getInt(1);
            }
        }
        catch (SQLException sqx) {
            throw new IOException(sqx);
        }
        finally {
            DbUtilities.closeDatabaseObjects(ps, rs);
            DbConnector.returnConnection(dbcon);
        }
        return this.id;
    }
    
    @Override
    void loadVillageRecruitees() {
        Connection dbcon = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            dbcon = DbConnector.getZonesDbCon();
            ps = dbcon.prepareStatement("SELECT RECRUITEEID, RECRUITEENAME FROM VILLAGERECRUITEES WHERE VILLAGEID =?;");
            ps.setInt(1, this.getId());
            rs = ps.executeQuery();
            while (rs.next()) {
                final long pid = rs.getLong("RECRUITEEID");
                final String sName = rs.getString("RECRUITEENAME");
                this.addVillageRecruitee(new VillageRecruitee(this.getId(), pid, sName));
            }
        }
        catch (SQLException sqx) {
            DbVillage.logger.log(Level.WARNING, "Problem loading all village recruitees for village: " + this.getId() + " - " + sqx.getMessage(), sqx);
        }
        finally {
            DbUtilities.closeDatabaseObjects(ps, rs);
            DbConnector.returnConnection(dbcon);
        }
    }
    
    @Override
    void deleteRecruitee(final VillageRecruitee vr) {
        Connection dbcon = null;
        PreparedStatement ps = null;
        try {
            dbcon = DbConnector.getZonesDbCon();
            ps = dbcon.prepareStatement("DELETE FROM VILLAGERECRUITEES WHERE(VILLAGEID=? AND RECRUITEEID=?);");
            ps.setInt(1, vr.getVillageId());
            ps.setLong(2, vr.getRecruiteeId());
            ps.executeUpdate();
        }
        catch (SQLException sqx) {
            DbVillage.logger.log(Level.WARNING, "Problem deleting from recruitees: " + this.getId() + " - " + sqx.getMessage(), sqx);
        }
        finally {
            DbUtilities.closeDatabaseObjects(ps, null);
            DbConnector.returnConnection(dbcon);
        }
    }
    
    @Override
    void loadVillageMapAnnotations() {
        Connection dbcon = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            dbcon = DbConnector.getPlayerDbCon();
            ps = dbcon.prepareStatement("SELECT * FROM MAP_ANNOTATIONS WHERE POITYPE=1 AND OWNERID=?");
            ps.setLong(1, this.getId());
            rs = ps.executeQuery();
            while (rs.next()) {
                final long wid = rs.getLong("ID");
                final String sName = rs.getString("NAME");
                final long position = rs.getLong("POSITION");
                final byte type = rs.getByte("POITYPE");
                final long ownerId = rs.getLong("OWNERID");
                final String server = rs.getString("SERVER");
                final byte icon = rs.getByte("ICON");
                this.addVillageMapAnnotation(new MapAnnotation(wid, sName, type, position, ownerId, server, icon), false);
            }
        }
        catch (SQLException sqx) {
            DbVillage.logger.log(Level.WARNING, "Problem loading all village POI's for village: " + this.getId() + " - " + sqx.getMessage(), sqx);
        }
        finally {
            DbUtilities.closeDatabaseObjects(ps, rs);
            DbConnector.returnConnection(dbcon);
        }
    }
    
    @Override
    void loadCitizens() {
        Connection dbcon = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            dbcon = DbConnector.getZonesDbCon();
            ps = dbcon.prepareStatement("SELECT * FROM CITIZENS WHERE VILLAGEID=?");
            ps.setInt(1, this.id);
            rs = ps.executeQuery();
            while (rs.next()) {
                final long wurmid = rs.getLong("WURMID");
                final int roleid = rs.getInt("ROLEID");
                long votedate = rs.getLong("VOTEDATE");
                if (votedate == 0L) {
                    votedate = -10L;
                }
                final long votedfor = rs.getLong("VOTEDFOR");
                Citizen citizen = null;
                if (WurmId.getType(wurmid) == 0) {
                    try {
                        final String n = Players.getInstance().getNameFor(wurmid);
                        citizen = new DbCitizen(wurmid, n, this.getRole(roleid), votedate, votedfor);
                    }
                    catch (NoSuchPlayerException nsp2) {
                        try {
                            Citizen.delete(wurmid);
                        }
                        catch (IOException iox2) {
                            DbVillage.logger.log(Level.WARNING, "Failed to remove citiz " + wurmid, iox2);
                        }
                    }
                    catch (IOException iox3) {
                        DbVillage.logger.log(Level.INFO, iox3.getMessage(), iox3);
                    }
                    catch (NoSuchRoleException nsr) {
                        DbVillage.logger.log(Level.WARNING, nsr.getMessage(), nsr);
                    }
                }
                else {
                    try {
                        final Creature c = Creatures.getInstance().getCreature(wurmid);
                        citizen = new DbCitizen(wurmid, c.getName(), this.getRole(roleid), votedate, votedfor);
                        c.setCitizenVillage(this);
                    }
                    catch (NoSuchCreatureException nsp) {
                        DbVillage.logger.log(Level.INFO, nsp.getMessage(), nsp);
                        try {
                            Citizen.delete(wurmid);
                        }
                        catch (IOException iox2) {
                            DbVillage.logger.log(Level.WARNING, "Failed to remove citiz " + wurmid, iox2);
                        }
                    }
                    catch (NoSuchRoleException nsr) {
                        DbVillage.logger.log(Level.WARNING, nsr.getMessage(), nsr);
                    }
                }
                if (citizen != null) {
                    this.citizens.put(new Long(citizen.getId()), citizen);
                }
            }
        }
        catch (SQLException sqx) {
            DbVillage.logger.log(Level.WARNING, "Failed to load citizens for village with id " + this.id, sqx);
        }
        finally {
            DbUtilities.closeDatabaseObjects(ps, rs);
            DbConnector.returnConnection(dbcon);
        }
    }
    
    @Override
    public void setMayor(final String aName) throws IOException {
        if (!this.mayorName.equals(aName)) {
            this.mayorName = aName;
            Connection dbcon = null;
            PreparedStatement ps = null;
            try {
                dbcon = DbConnector.getZonesDbCon();
                ps = dbcon.prepareStatement("UPDATE VILLAGES SET MAYOR=? WHERE ID=?");
                ps.setString(1, this.mayorName);
                ps.setInt(2, this.id);
                ps.executeUpdate();
            }
            catch (SQLException sqx) {
                DbVillage.logger.log(Level.WARNING, "Failed to set mayor name for village with id " + this.id, sqx);
                throw new IOException(sqx);
            }
            finally {
                DbUtilities.closeDatabaseObjects(ps, null);
                DbConnector.returnConnection(dbcon);
            }
            try {
                this.addHistory(this.mayorName, "new " + this.getRoleForStatus((byte)2).getName());
            }
            catch (NoSuchRoleException nsr) {
                DbVillage.logger.log(Level.WARNING, this.getName() + " this village doesn't have the correct roles: " + nsr.getMessage(), nsr);
            }
        }
    }
    
    @Override
    public void setMotto(final String devise) throws IOException {
        if (!this.motto.equals(devise)) {
            this.motto = devise;
            Connection dbcon = null;
            PreparedStatement ps = null;
            try {
                dbcon = DbConnector.getZonesDbCon();
                ps = dbcon.prepareStatement("UPDATE VILLAGES SET DEVISE=? WHERE ID=?");
                ps.setString(1, devise);
                ps.setInt(2, this.id);
                ps.executeUpdate();
            }
            catch (SQLException sqx) {
                DbVillage.logger.log(Level.WARNING, "Failed to set devise for village with id " + this.id, sqx);
                throw new IOException(sqx);
            }
            finally {
                DbUtilities.closeDatabaseObjects(ps, null);
                DbConnector.returnConnection(dbcon);
            }
        }
    }
    
    @Override
    public void setVillageRep(final int newRep) {
        if (Servers.localServer.HOMESERVER && Servers.localServer.EPIC && newRep != this.villageReputation && newRep <= 150) {
            this.villageReputation = newRep;
            Connection dbcon = null;
            PreparedStatement ps = null;
            try {
                dbcon = DbConnector.getZonesDbCon();
                ps = dbcon.prepareStatement("UPDATE VILLAGES SET VILLAGEREP=? WHERE ID=?");
                ps.setInt(1, this.villageReputation);
                ps.setInt(2, this.id);
                ps.executeUpdate();
            }
            catch (SQLException sqx) {
                DbVillage.logger.log(Level.WARNING, "Failed to set reputation for village with id " + this.id, sqx);
            }
            finally {
                DbUtilities.closeDatabaseObjects(ps, null);
                DbConnector.returnConnection(dbcon);
            }
            final boolean mayDeclare = this.villageReputation > 100;
            final String toSend = "Settlement reputation now at " + this.villageReputation + (mayDeclare ? "Other settlements may now declare war." : "");
            this.group.sendMessage(this.getRepMessage(toSend));
            if (this.twitChat()) {
                this.twit(toSend);
            }
        }
    }
    
    @Override
    public void setMotd(final String newMotd) throws IOException {
        if (!this.motd.equals(newMotd)) {
            this.motd = newMotd;
            Connection dbcon = null;
            PreparedStatement ps = null;
            try {
                dbcon = DbConnector.getZonesDbCon();
                ps = dbcon.prepareStatement("UPDATE VILLAGES SET MOTD=? WHERE ID=?");
                ps.setString(1, newMotd);
                ps.setInt(2, this.id);
                ps.executeUpdate();
            }
            catch (SQLException sqx) {
                DbVillage.logger.log(Level.WARNING, "Failed to set motd for village with id " + this.id, sqx);
                throw new IOException(sqx);
            }
            finally {
                DbUtilities.closeDatabaseObjects(ps, null);
                DbConnector.returnConnection(dbcon);
            }
            if (newMotd != null && newMotd.length() > 0) {
                this.group.sendMessage(this.getMotdMessage());
                if (this.twitChat()) {
                    this.twit("MOTD:" + this.motd);
                }
            }
        }
    }
    
    @Override
    void setStartX(final int aStartx) throws IOException {
        if (this.startx != aStartx) {
            this.startx = aStartx;
            Connection dbcon = null;
            PreparedStatement ps = null;
            try {
                dbcon = DbConnector.getZonesDbCon();
                ps = dbcon.prepareStatement("UPDATE VILLAGES SET STARTX=? WHERE ID=?");
                ps.setInt(1, aStartx);
                ps.setInt(2, this.id);
                ps.executeUpdate();
            }
            catch (SQLException sqx) {
                DbVillage.logger.log(Level.WARNING, "Failed to set startx for village with id " + this.id, sqx);
                throw new IOException(sqx);
            }
            finally {
                DbUtilities.closeDatabaseObjects(ps, null);
                DbConnector.returnConnection(dbcon);
            }
        }
    }
    
    @Override
    public void setName(final String aName) throws IOException {
        if (!this.name.equals(aName)) {
            Groups.renameGroup(this.name, aName);
            this.name = aName;
            Connection dbcon = null;
            PreparedStatement ps = null;
            try {
                dbcon = DbConnector.getZonesDbCon();
                ps = dbcon.prepareStatement("UPDATE VILLAGES SET NAME=? WHERE ID=?");
                ps.setString(1, aName);
                ps.setInt(2, this.id);
                ps.executeUpdate();
            }
            catch (SQLException sqx) {
                DbVillage.logger.log(Level.WARNING, "Failed to set name for village with id " + this.id, sqx);
                throw new IOException(sqx);
            }
            finally {
                DbUtilities.closeDatabaseObjects(ps, null);
                DbConnector.returnConnection(dbcon);
            }
        }
    }
    
    @Override
    void setEndX(final int aEndx) throws IOException {
        if (this.endx != aEndx) {
            this.endx = aEndx;
            Connection dbcon = null;
            PreparedStatement ps = null;
            try {
                dbcon = DbConnector.getZonesDbCon();
                ps = dbcon.prepareStatement("UPDATE VILLAGES SET ENDX=? WHERE ID=?");
                ps.setInt(1, aEndx);
                ps.setInt(2, this.id);
                ps.executeUpdate();
            }
            catch (SQLException sqx) {
                DbVillage.logger.log(Level.WARNING, "Failed to set endx for village with id " + this.id, sqx);
                throw new IOException(sqx);
            }
            finally {
                DbUtilities.closeDatabaseObjects(ps, null);
                DbConnector.returnConnection(dbcon);
            }
        }
    }
    
    @Override
    void setStartY(final int aStarty) throws IOException {
        if (this.starty != aStarty) {
            this.starty = aStarty;
            Connection dbcon = null;
            PreparedStatement ps = null;
            try {
                dbcon = DbConnector.getZonesDbCon();
                ps = dbcon.prepareStatement("UPDATE VILLAGES SET STARTY=? WHERE ID=?");
                ps.setInt(1, aStarty);
                ps.setInt(2, this.id);
                ps.executeUpdate();
            }
            catch (SQLException sqx) {
                DbVillage.logger.log(Level.WARNING, "Failed to set starty for village with id " + this.id, sqx);
                throw new IOException(sqx);
            }
            finally {
                DbUtilities.closeDatabaseObjects(ps, null);
                DbConnector.returnConnection(dbcon);
            }
        }
    }
    
    @Override
    void setEndY(final int aEndy) throws IOException {
        if (this.endy != aEndy) {
            this.endy = aEndy;
            Connection dbcon = null;
            PreparedStatement ps = null;
            try {
                dbcon = DbConnector.getZonesDbCon();
                ps = dbcon.prepareStatement("UPDATE VILLAGES SET ENDY=? WHERE ID=?");
                ps.setInt(1, aEndy);
                ps.setInt(2, this.id);
                ps.executeUpdate();
            }
            catch (SQLException sqx) {
                DbVillage.logger.log(Level.WARNING, "Failed to set endy for village with id " + this.id, sqx);
                throw new IOException(sqx);
            }
            finally {
                DbUtilities.closeDatabaseObjects(ps, null);
                DbConnector.returnConnection(dbcon);
            }
        }
    }
    
    @Override
    void setDeedId(final long aDeedid) throws IOException {
        if (this.deedid != aDeedid) {
            this.deedid = aDeedid;
            Connection dbcon = null;
            PreparedStatement ps = null;
            try {
                dbcon = DbConnector.getZonesDbCon();
                ps = dbcon.prepareStatement("UPDATE VILLAGES SET DEEDID=? WHERE ID=?");
                ps.setLong(1, aDeedid);
                ps.setInt(2, this.id);
                ps.executeUpdate();
            }
            catch (SQLException sqx) {
                DbVillage.logger.log(Level.WARNING, "Failed to set deedid=" + aDeedid + " for village with id " + this.id, sqx);
                throw new IOException(sqx);
            }
            finally {
                DbUtilities.closeDatabaseObjects(ps, null);
                DbConnector.returnConnection(dbcon);
            }
        }
    }
    
    @Override
    public void setDemocracy(final boolean aDemocracy) throws IOException {
        if (this.democracy != aDemocracy) {
            this.democracy = aDemocracy;
            Connection dbcon = null;
            PreparedStatement ps = null;
            try {
                dbcon = DbConnector.getZonesDbCon();
                ps = dbcon.prepareStatement("UPDATE VILLAGES SET DEMOCRACY=? WHERE ID=?");
                ps.setBoolean(1, aDemocracy);
                ps.setInt(2, this.id);
                ps.executeUpdate();
            }
            catch (SQLException sqx) {
                DbVillage.logger.log(Level.WARNING, "Failed to set democracy=" + aDemocracy + " for village with id " + this.id, sqx);
                throw new IOException(sqx);
            }
            finally {
                DbUtilities.closeDatabaseObjects(ps, null);
                DbConnector.returnConnection(dbcon);
            }
            if (aDemocracy) {
                this.addHistory(this.getName(), "is now a democracy");
            }
            else {
                this.addHistory(this.getName(), "is now a dictatorship");
            }
        }
    }
    
    @Override
    void loadRoles() {
        Connection dbcon = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            dbcon = DbConnector.getZonesDbCon();
            ps = dbcon.prepareStatement("SELECT * FROM VILLAGEROLE WHERE VILLAGEID=?");
            ps.setInt(1, this.id);
            rs = ps.executeQuery();
            while (rs.next()) {
                final int roleid = rs.getInt("ID");
                final String roleName = rs.getString("NAME");
                final boolean mayTerraform = rs.getBoolean("MAYTERRAFORM");
                final boolean mayCuttrees = rs.getBoolean("MAYCUTTREE");
                final boolean mayMine = rs.getBoolean("MAYMINE");
                final boolean mayFarm = rs.getBoolean("MAYFARM");
                final boolean mayBuild = rs.getBoolean("MAYBUILD");
                final boolean mayHire = rs.getBoolean("MAYHIRE");
                final boolean mayInvite = rs.getBoolean("MAYINVITE");
                final boolean mayDestroy = rs.getBoolean("MAYDESTROY");
                final boolean mayManageRoles = rs.getBoolean("MAYMANAGEROLES");
                final boolean mayExpand = rs.getBoolean("MAYEXPAND");
                final boolean mayPassAllFences = rs.getBoolean("MAYPASSFENCES");
                final boolean mayLockFences = rs.getBoolean("MAYLOCKFENCES");
                final boolean mayAttackCitizens = rs.getBoolean("MAYATTACKCITIZ");
                final boolean mayAttackNonCitizens = rs.getBoolean("MAYATTACKNONCITIZ");
                final boolean mayFish = rs.getBoolean("MAYFISH");
                final boolean mayCutOldTrees = rs.getBoolean("MAYCUTOLD");
                final boolean mayPushPullTurn = rs.getBoolean("MAYPUSHPULLTURN");
                final boolean diplomat = rs.getBoolean("DIPLOMAT");
                final byte status = rs.getByte("STATUS");
                final int villageAppliedTo = rs.getInt("VILLAGEAPPLIEDTO");
                final boolean mayUpdateMap = rs.getBoolean("MAYUPDATEMAP");
                final boolean mayLead = rs.getBoolean("MAYLEAD");
                final boolean mayPickup = rs.getBoolean("MAYPICKUP");
                final boolean mayTame = rs.getBoolean("MAYTAME");
                final boolean mayLoad = rs.getBoolean("MAYLOAD");
                final boolean mayButcher = rs.getBoolean("MAYBUTCHER");
                final boolean mayAttachLock = rs.getBoolean("MAYATTACHLOCK");
                final boolean mayPickLocks = rs.getBoolean("MAYPICKLOCKS");
                final long playerAppliedTo = rs.getLong("PLAYERAPPLIEDTO");
                final int settings = rs.getInt("SETTINGS");
                final int moreSettings = rs.getInt("MORESETTINGS");
                final int extraSettings = rs.getInt("EXTRASETTINGS");
                final VillageRole role = new DbVillageRole(roleid, this.id, roleName, mayTerraform, mayCuttrees, mayMine, mayFarm, mayBuild, mayHire, mayInvite, mayDestroy, mayManageRoles, mayExpand, mayPassAllFences, mayLockFences, mayAttackCitizens, mayAttackNonCitizens, mayFish, mayCutOldTrees, mayPushPullTurn, diplomat, status, villageAppliedTo, mayUpdateMap, mayLead, mayPickup, mayTame, mayLoad, mayButcher, mayAttachLock, mayPickLocks, playerAppliedTo, settings, moreSettings, extraSettings);
                if (role.getStatus() == 1 && (role.mayDestroy || role.mayDestroyAnyBuilding())) {
                    DbVillage.logger.warning("Loading RoleID " + this.id + ": mayDestroy/mayDestroyAnyBuilding set on ROLE_EVERYBODY, defaulting to false");
                    try {
                        role.setMayDestroy(false);
                        role.setCanDestroyAnyBuilding(false);
                    }
                    catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                boolean insert = true;
                if (role.getStatus() == 1) {
                    try {
                        if (this.getRoleForStatus((byte)1) != null) {
                            role.delete();
                            DbVillage.logger.log(Level.INFO, "Deleted everybody role for " + this.getName());
                            insert = false;
                        }
                    }
                    catch (Exception ex2) {}
                }
                if (insert) {
                    this.roles.put(roleid, role);
                }
            }
            try {
                this.everybody = this.getRoleForStatus((byte)1);
            }
            catch (Exception ex) {
                DbVillage.logger.log(Level.WARNING, this.getName() + " - role everybody doesn't exist. Creating.");
                this.createRoleEverybody();
            }
        }
        catch (SQLException sqx) {
            DbVillage.logger.log(Level.WARNING, "Failed to load roles for village with id " + this.id, sqx);
        }
        finally {
            DbUtilities.closeDatabaseObjects(ps, rs);
            DbConnector.returnConnection(dbcon);
        }
    }
    
    @Override
    public void setTokenId(final long tokenid) throws IOException {
        if (this.tokenId != tokenid) {
            this.tokenId = tokenid;
            Connection dbcon = null;
            PreparedStatement ps = null;
            try {
                dbcon = DbConnector.getZonesDbCon();
                ps = dbcon.prepareStatement("UPDATE VILLAGES SET TOKEN=? WHERE ID=?");
                ps.setLong(1, this.tokenId);
                ps.setInt(2, this.id);
                ps.executeUpdate();
            }
            catch (SQLException sqx) {
                DbVillage.logger.log(Level.WARNING, "Failed to set tokenid=" + this.tokenId + " for village with id " + this.id, sqx);
                throw new IOException(sqx);
            }
            finally {
                DbUtilities.closeDatabaseObjects(ps, null);
                DbConnector.returnConnection(dbcon);
            }
        }
    }
    
    @Override
    void setMaxcitizens(final int maxcitizens) {
        if (maxcitizens != this.maxCitizens) {
            this.maxCitizens = maxcitizens;
            Connection dbcon = null;
            PreparedStatement ps = null;
            try {
                dbcon = DbConnector.getZonesDbCon();
                ps = dbcon.prepareStatement("UPDATE VILLAGES SET MAXCITIZENS=? WHERE ID=?");
                ps.setInt(1, this.maxCitizens);
                ps.setInt(2, this.id);
                ps.executeUpdate();
            }
            catch (SQLException sqx) {
                DbVillage.logger.log(Level.WARNING, "Failed to set max citizens=" + this.maxCitizens + " for village with id " + this.id, sqx);
            }
            finally {
                DbUtilities.closeDatabaseObjects(ps, null);
                DbConnector.returnConnection(dbcon);
            }
        }
    }
    
    @Override
    public void setDisbandTime(final long disbandTime) throws IOException {
        if (this.disband != disbandTime) {
            this.disband = disbandTime;
            Connection dbcon = null;
            PreparedStatement ps = null;
            try {
                dbcon = DbConnector.getZonesDbCon();
                ps = dbcon.prepareStatement("UPDATE VILLAGES SET DISBAND=? WHERE ID=?");
                ps.setLong(1, this.disband);
                ps.setInt(2, this.id);
                ps.executeUpdate();
            }
            catch (SQLException sqx) {
                DbVillage.logger.log(Level.WARNING, "Failed to set disbanding=" + disbandTime + " for village with id " + this.id, sqx);
                throw new IOException(sqx);
            }
            finally {
                DbUtilities.closeDatabaseObjects(ps, null);
                DbConnector.returnConnection(dbcon);
            }
        }
    }
    
    @Override
    public void setDisbander(final long disb) throws IOException {
        if (this.disbander != disb) {
            this.disbander = disb;
            Connection dbcon = null;
            PreparedStatement ps = null;
            try {
                dbcon = DbConnector.getZonesDbCon();
                ps = dbcon.prepareStatement("UPDATE VILLAGES SET DISBANDER=? WHERE ID=?");
                ps.setLong(1, this.disbander);
                ps.setInt(2, this.id);
                ps.executeUpdate();
            }
            catch (SQLException sqx) {
                DbVillage.logger.log(Level.WARNING, "Failed to set disbander=" + this.disbander + " for village with id " + this.id, sqx);
                throw new IOException(sqx);
            }
            finally {
                DbUtilities.closeDatabaseObjects(ps, null);
                DbConnector.returnConnection(dbcon);
            }
        }
    }
    
    @Override
    public void saveSettings() throws IOException {
        Connection dbcon = null;
        PreparedStatement ps = null;
        try {
            dbcon = DbConnector.getZonesDbCon();
            ps = dbcon.prepareStatement("UPDATE VILLAGES SET MAYPICKUP=? WHERE ID=?");
            ps.setByte(1, (byte)this.settings.getPermissions());
            ps.setInt(2, this.id);
            ps.executeUpdate();
        }
        catch (SQLException sqx) {
            DbVillage.logger.log(Level.WARNING, "Failed to seve settings: for village with id " + this.id, sqx);
            throw new IOException(sqx);
        }
        finally {
            DbUtilities.closeDatabaseObjects(ps, null);
            DbConnector.returnConnection(dbcon);
        }
    }
    
    @Override
    public void setPerimeter(final int newPerimeter) throws IOException {
        if (this.perimeterTiles != newPerimeter) {
            this.perimeterTiles = newPerimeter;
            Connection dbcon = null;
            PreparedStatement ps = null;
            try {
                dbcon = DbConnector.getZonesDbCon();
                ps = dbcon.prepareStatement("UPDATE VILLAGES SET PERIMETER=? WHERE ID=?");
                ps.setInt(1, this.perimeterTiles);
                ps.setInt(2, this.id);
                ps.executeUpdate();
            }
            catch (SQLException sqx) {
                DbVillage.logger.log(Level.WARNING, "Failed to set perimeter=" + newPerimeter + " for village with id " + this.id, sqx);
                throw new IOException(sqx);
            }
            finally {
                DbUtilities.closeDatabaseObjects(ps, null);
                DbConnector.returnConnection(dbcon);
            }
        }
    }
    
    @Override
    public void setAcceptsMerchants(final boolean accepts) throws IOException {
        if (this.acceptsMerchants != accepts) {
            this.acceptsMerchants = accepts;
            Connection dbcon = null;
            PreparedStatement ps = null;
            try {
                dbcon = DbConnector.getZonesDbCon();
                ps = dbcon.prepareStatement("UPDATE VILLAGES SET MERCHANTS=? WHERE ID=?");
                ps.setBoolean(1, this.acceptsMerchants);
                ps.setInt(2, this.id);
                ps.executeUpdate();
            }
            catch (SQLException sqx) {
                DbVillage.logger.log(Level.WARNING, "Failed to set acceptsMerchants=" + this.acceptsMerchants + " for village with id " + this.id, sqx);
                throw new IOException(sqx);
            }
            finally {
                DbUtilities.closeDatabaseObjects(ps, null);
                DbConnector.returnConnection(dbcon);
            }
        }
    }
    
    @Override
    public void setAllowsAggroCreatures(final boolean allows) throws IOException {
        if (this.allowsAggCreatures != allows) {
            this.allowsAggCreatures = allows;
            Connection dbcon = null;
            PreparedStatement ps = null;
            try {
                dbcon = DbConnector.getZonesDbCon();
                ps = dbcon.prepareStatement("UPDATE VILLAGES SET AGGROS=? WHERE ID=?");
                ps.setBoolean(1, this.allowsAggCreatures);
                ps.setInt(2, this.id);
                ps.executeUpdate();
            }
            catch (SQLException sqx) {
                DbVillage.logger.log(Level.WARNING, "Failed to set acceptsMerchants=" + this.acceptsMerchants + " for village with id " + this.id, sqx);
                throw new IOException(sqx);
            }
            finally {
                DbUtilities.closeDatabaseObjects(ps, null);
                DbConnector.returnConnection(dbcon);
            }
        }
    }
    
    @Override
    public void setUnlimitedCitizens(final boolean accepts) throws IOException {
        if (this.unlimitedCitizens != accepts) {
            this.unlimitedCitizens = accepts;
            Connection dbcon = null;
            PreparedStatement ps = null;
            try {
                dbcon = DbConnector.getZonesDbCon();
                ps = dbcon.prepareStatement("UPDATE VILLAGES SET ACCEPTSHOMESTEADS=? WHERE ID=?");
                ps.setBoolean(1, this.unlimitedCitizens);
                ps.setInt(2, this.id);
                ps.executeUpdate();
            }
            catch (SQLException sqx) {
                DbVillage.logger.log(Level.WARNING, "Failed to set unlimitedcitizens=" + this.unlimitedCitizens + " for village with id " + this.id, sqx);
                throw new IOException(sqx);
            }
            finally {
                DbUtilities.closeDatabaseObjects(ps, null);
                DbConnector.returnConnection(dbcon);
            }
        }
    }
    
    @Override
    public void setLogin() {
        this.lastLogin = System.currentTimeMillis();
        Connection dbcon = null;
        PreparedStatement ps = null;
        try {
            dbcon = DbConnector.getZonesDbCon();
            ps = dbcon.prepareStatement("UPDATE VILLAGES SET LASTLOGIN=? WHERE ID=?");
            ps.setLong(1, this.lastLogin);
            ps.setInt(2, this.id);
            ps.executeUpdate();
        }
        catch (SQLException sqx) {
            DbVillage.logger.log(Level.WARNING, "Failed to set last login=now for village with id " + this.id, sqx);
        }
        finally {
            DbUtilities.closeDatabaseObjects(ps, null);
            DbConnector.returnConnection(dbcon);
        }
        if (!this.isCitizen(this.disbander)) {
            this.stopDisbanding();
        }
    }
    
    @Override
    void delete() throws IOException {
        Connection dbcon = null;
        PreparedStatement ps = null;
        try {
            dbcon = DbConnector.getZonesDbCon();
            ps = dbcon.prepareStatement("UPDATE VILLAGES SET DISBANDED=1 WHERE ID=?");
            ps.setInt(1, this.id);
            ps.executeUpdate();
        }
        catch (SQLException sqx) {
            DbVillage.logger.log(Level.WARNING, "Failed to delete village with id=" + this.id, sqx);
            throw new IOException(sqx);
        }
        finally {
            DbUtilities.closeDatabaseObjects(ps, null);
            DbConnector.returnConnection(dbcon);
        }
    }
    
    @Override
    void deleteVillageMapAnnotations() {
        Connection dbcon = null;
        PreparedStatement ps = null;
        try {
            dbcon = DbConnector.getPlayerDbCon();
            ps = dbcon.prepareStatement("DELETE FROM MAP_ANNOTATIONS WHERE OWNERID=? AND POITYPE=1;");
            ps.setLong(1, this.id);
            ps.executeUpdate();
        }
        catch (SQLException sqx) {
            DbVillage.logger.log(Level.WARNING, "Failed to delete village map annotations with id=" + this.id, sqx);
        }
        finally {
            DbUtilities.closeDatabaseObjects(ps, null);
            DbConnector.returnConnection(dbcon);
        }
    }
    
    public void save() {
    }
    
    @Override
    void loadReputations() {
        Connection dbcon = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            dbcon = DbConnector.getZonesDbCon();
            ps = dbcon.prepareStatement("SELECT REPUTATION,PERMANENT, WURMID FROM REPUTATION WHERE VILLAGEID=?");
            ps.setInt(1, this.id);
            rs = ps.executeQuery();
            while (rs.next()) {
                final long wurmId = rs.getLong("WURMID");
                final int reputation = rs.getByte("REPUTATION");
                final boolean perma = rs.getBoolean("PERMANENT");
                final Reputation rep = new Reputation(wurmId, this.id, perma, reputation, false, true);
                this.reputations.put(new Long(wurmId), rep);
            }
        }
        catch (SQLException sqx) {
            DbVillage.logger.log(Level.WARNING, "Failed to load reputations for village with id " + this.id, sqx);
        }
        finally {
            DbUtilities.closeDatabaseObjects(ps, rs);
            DbConnector.returnConnection(dbcon);
        }
    }
    
    @Override
    void setUpkeep(final long aUpk) throws IOException {
        final long upk = Math.max(0L, aUpk);
        if (upk != this.upkeep) {
            this.upkeep = upk;
            Connection dbcon = null;
            PreparedStatement ps = null;
            try {
                dbcon = DbConnector.getZonesDbCon();
                ps = dbcon.prepareStatement("UPDATE VILLAGES SET UPKEEP=? WHERE ID=?");
                ps.setLong(1, this.upkeep);
                ps.setInt(2, this.id);
                ps.executeUpdate();
            }
            catch (SQLException sqx) {
                DbVillage.logger.log(Level.WARNING, "Failed to set upkeep=" + this.upkeep + " for village with id " + this.id, sqx);
                throw new IOException(sqx);
            }
            finally {
                DbUtilities.closeDatabaseObjects(ps, null);
                DbConnector.returnConnection(dbcon);
            }
        }
    }
    
    public void loadGuards() {
        Connection dbcon = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            dbcon = DbConnector.getZonesDbCon();
            ps = dbcon.prepareStatement("SELECT * FROM GUARDS WHERE VILLAGEID=?");
            ps.setInt(1, this.id);
            rs = ps.executeQuery();
            while (rs.next()) {
                final long wurmId = rs.getLong("WURMID");
                final long expireDate = rs.getLong("EXPIREDATE");
                try {
                    final Creature creature = Creatures.getInstance().getCreature(wurmId);
                    final Guard guard = new DbGuard(this.id, creature, expireDate);
                    creature.setCitizenVillage(this);
                    this.guards.put(new Long(wurmId), guard);
                }
                catch (NoSuchCreatureException nsc) {
                    DbUtilities.closeDatabaseObjects(ps, null);
                    ps = dbcon.prepareStatement("DELETE FROM GUARDS WHERE WURMID=?");
                    ps.setLong(1, wurmId);
                    ps.executeUpdate();
                    DbVillage.logger.log(Level.WARNING, "Deleted guard with id " + wurmId + ". These messages should disappear over time since 040707.");
                }
            }
        }
        catch (SQLException sqx) {
            DbVillage.logger.log(Level.WARNING, "Failed to load guards for village with id " + this.id, sqx);
        }
        finally {
            DbUtilities.closeDatabaseObjects(ps, rs);
            DbConnector.returnConnection(dbcon);
        }
    }
    
    @Override
    final void loadHistory() {
        this.history = new LinkedList<HistoryEvent>();
        Connection dbcon = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            dbcon = DbConnector.getZonesDbCon();
            ps = dbcon.prepareStatement("SELECT EVENTDATE, VILLAGEID, PERFORMER, EVENT FROM HISTORY WHERE VILLAGEID=? ORDER BY EVENTDATE DESC");
            ps.setInt(1, this.id);
            rs = ps.executeQuery();
            while (rs.next()) {
                this.history.add(new HistoryEvent(rs.getLong("EVENTDATE"), rs.getString("PERFORMER"), rs.getString("EVENT"), this.id));
            }
        }
        catch (SQLException sqx) {
            DbVillage.logger.log(Level.WARNING, "Failed to load history for village with id " + this.id, sqx);
        }
        finally {
            DbUtilities.closeDatabaseObjects(ps, rs);
            DbConnector.returnConnection(dbcon);
        }
    }
    
    @Override
    public final void addHistory(final String performerName, final String event) {
        final HistoryEvent h = new HistoryEvent(System.currentTimeMillis(), performerName, event, this.id);
        this.history.addFirst(h);
        Connection dbcon = null;
        PreparedStatement ps = null;
        try {
            dbcon = DbConnector.getZonesDbCon();
            ps = dbcon.prepareStatement("INSERT INTO HISTORY(EVENTDATE,VILLAGEID,PERFORMER,EVENT) VALUES (?,?,?,?)");
            ps.setLong(1, h.time);
            ps.setInt(2, h.identifier);
            ps.setString(3, h.performer);
            ps.setString(4, h.event);
            ps.executeUpdate();
        }
        catch (SQLException sqx) {
            DbVillage.logger.log(Level.WARNING, sqx.getMessage(), sqx);
        }
        finally {
            DbUtilities.closeDatabaseObjects(ps, null);
            DbConnector.returnConnection(dbcon);
        }
    }
    
    @Override
    void saveRecruitee(final VillageRecruitee vr) {
        Connection dbcon = null;
        PreparedStatement ps = null;
        try {
            dbcon = DbConnector.getZonesDbCon();
            ps = dbcon.prepareStatement("INSERT INTO VILLAGERECRUITEES(VILLAGEID, RECRUITEEID, RECRUITEENAME) VALUES (?,?,?)");
            ps.setInt(1, vr.getVillageId());
            ps.setLong(2, vr.getRecruiteeId());
            ps.setString(3, vr.getRecruiteeName());
            ps.executeUpdate();
        }
        catch (SQLException sqx) {
            DbVillage.logger.log(Level.WARNING, sqx.getMessage(), sqx);
        }
        finally {
            DbUtilities.closeDatabaseObjects(ps, null);
            DbConnector.returnConnection(dbcon);
        }
    }
    
    @Override
    public void setKingdom(final byte newkingdom) throws IOException {
        this.setKingdom(newkingdom, true);
    }
    
    @Override
    public void setKingdom(final byte newkingdom, final boolean updateTimeStamp) throws IOException {
        if (this.kingdom != newkingdom) {
            this.kingdom = newkingdom;
            final Kingdom k = Kingdoms.getKingdom(this.kingdom);
            if (k != null) {
                k.setExistsHere(true);
            }
            this.convertOfflineCitizensToKingdom(newkingdom, updateTimeStamp);
            Connection dbcon = null;
            PreparedStatement ps = null;
            try {
                dbcon = DbConnector.getZonesDbCon();
                ps = dbcon.prepareStatement("UPDATE VILLAGES SET KINGDOM=? WHERE ID=?");
                ps.setByte(1, this.kingdom);
                ps.setInt(2, this.id);
                ps.executeUpdate();
            }
            catch (SQLException sqx) {
                DbVillage.logger.log(Level.WARNING, "Failed to set kingdom=" + newkingdom + " for village with id " + this.id, sqx);
                throw new IOException(sqx);
            }
            finally {
                DbUtilities.closeDatabaseObjects(ps, null);
                DbConnector.returnConnection(dbcon);
            }
        }
    }
    
    @Override
    public void setFaithCreate(final float newFaith) {
        if (this.faithCreate != newFaith) {
            this.faithCreate = newFaith;
            Connection dbcon = null;
            PreparedStatement ps = null;
            try {
                dbcon = DbConnector.getZonesDbCon();
                ps = dbcon.prepareStatement("UPDATE VILLAGES SET FAITHCREATE=? WHERE ID=?");
                ps.setFloat(1, this.faithCreate);
                ps.setInt(2, this.id);
                ps.executeUpdate();
            }
            catch (SQLException sqx) {
                DbVillage.logger.log(Level.WARNING, "Failed to set faithCreate=" + this.faithCreate + " for village with id " + this.id, sqx);
            }
            finally {
                DbUtilities.closeDatabaseObjects(ps, null);
                DbConnector.returnConnection(dbcon);
            }
        }
    }
    
    @Override
    public void setFaithWar(final float newFaith) {
        if (this.faithWar != newFaith) {
            this.faithWar = newFaith;
            Connection dbcon = null;
            PreparedStatement ps = null;
            try {
                dbcon = DbConnector.getZonesDbCon();
                ps = dbcon.prepareStatement("UPDATE VILLAGES SET FAITHWAR=? WHERE ID=?");
                ps.setFloat(1, this.faithWar);
                ps.setInt(2, this.id);
                ps.executeUpdate();
            }
            catch (SQLException sqx) {
                DbVillage.logger.log(Level.WARNING, "Failed to set faithCreate=" + this.faithWar + " for village with id " + this.id, sqx);
            }
            finally {
                DbUtilities.closeDatabaseObjects(ps, null);
                DbConnector.returnConnection(dbcon);
            }
        }
    }
    
    @Override
    public void setFaithHeal(final float newFaith) {
        if (this.faithHeal != newFaith) {
            this.faithHeal = newFaith;
            Connection dbcon = null;
            PreparedStatement ps = null;
            try {
                dbcon = DbConnector.getZonesDbCon();
                ps = dbcon.prepareStatement("UPDATE VILLAGES SET FAITHHEAL=? WHERE ID=?");
                ps.setFloat(1, this.faithHeal);
                ps.setInt(2, this.id);
                ps.executeUpdate();
            }
            catch (SQLException sqx) {
                DbVillage.logger.log(Level.WARNING, "Failed to set faithHeal=" + this.faithHeal + " for village with id " + this.id, sqx);
            }
            finally {
                DbUtilities.closeDatabaseObjects(ps, null);
                DbConnector.returnConnection(dbcon);
            }
        }
    }
    
    @Override
    public final void setTwitCredentials(final String _consumerKeyToUse, final String _consumerSecretToUse, final String _applicationToken, final String _applicationSecret, final boolean _twitChat, final boolean enabled) {
        this.consumerKeyToUse = _consumerKeyToUse;
        this.consumerSecretToUse = _consumerSecretToUse;
        this.applicationToken = _applicationToken;
        this.applicationSecret = _applicationSecret;
        this.twitChat = _twitChat;
        this.twitEnabled = enabled;
        Connection dbcon = null;
        PreparedStatement ps = null;
        try {
            dbcon = DbConnector.getZonesDbCon();
            ps = dbcon.prepareStatement("UPDATE VILLAGES SET TWITKEY=?,TWITSECRET=?,TWITAPP=?,TWITAPPSECRET=?,TWITCHAT=?,TWITENABLE=? WHERE ID=?");
            ps.setString(1, this.consumerKeyToUse);
            ps.setString(2, this.consumerSecretToUse);
            ps.setString(3, this.applicationToken);
            ps.setString(4, this.applicationSecret);
            ps.setBoolean(5, this.twitChat);
            ps.setBoolean(6, this.twitEnabled);
            ps.setInt(7, this.id);
            ps.executeUpdate();
        }
        catch (SQLException sqx) {
            DbVillage.logger.log(Level.WARNING, "Failed to set twitter info for village with id " + this.id, sqx);
        }
        finally {
            DbUtilities.closeDatabaseObjects(ps, null);
            DbConnector.returnConnection(dbcon);
        }
        this.canTwit();
    }
    
    @Override
    public final void setHotaWins(final short newWins) {
        if (this.hotaWins != newWins) {
            this.hotaWins = newWins;
            Connection dbcon = null;
            PreparedStatement ps = null;
            try {
                dbcon = DbConnector.getZonesDbCon();
                ps = dbcon.prepareStatement("UPDATE VILLAGES SET HOTAWINS=? WHERE ID=?");
                ps.setShort(1, this.hotaWins);
                ps.setInt(2, this.id);
                ps.executeUpdate();
            }
            catch (SQLException sqx) {
                DbVillage.logger.log(Level.WARNING, "Failed to set hotaWins=" + newWins + " for village with id " + this.id, sqx);
            }
            finally {
                DbUtilities.closeDatabaseObjects(ps, null);
                DbConnector.returnConnection(dbcon);
            }
        }
    }
    
    @Override
    public final void setLastChangedName(final long newDate) {
        if (this.lastChangedName != newDate) {
            this.lastChangedName = newDate;
            Connection dbcon = null;
            PreparedStatement ps = null;
            try {
                dbcon = DbConnector.getZonesDbCon();
                ps = dbcon.prepareStatement("UPDATE VILLAGES SET NAMECHANGED=? WHERE ID=?");
                ps.setLong(1, this.lastChangedName);
                ps.setInt(2, this.id);
                ps.executeUpdate();
            }
            catch (SQLException sqx) {
                DbVillage.logger.log(Level.WARNING, "Failed to set lastChangedName=" + newDate + " for village with id " + this.id, sqx);
            }
            finally {
                DbUtilities.closeDatabaseObjects(ps, null);
                DbConnector.returnConnection(dbcon);
            }
        }
    }
    
    @Override
    public final void setSpawnSituation(final byte newSpawnSituation) {
        if (this.spawnSituation != newSpawnSituation) {
            this.spawnSituation = newSpawnSituation;
            Connection dbcon = null;
            PreparedStatement ps = null;
            try {
                dbcon = DbConnector.getZonesDbCon();
                ps = dbcon.prepareStatement("UPDATE VILLAGES SET SPAWNSITUATION=? WHERE ID=?");
                ps.setByte(1, this.spawnSituation);
                ps.setInt(2, this.id);
                ps.executeUpdate();
            }
            catch (SQLException sqx) {
                DbVillage.logger.log(Level.WARNING, "Failed to set spawnSituation=" + this.spawnSituation + " for village with id " + this.id, sqx);
            }
            finally {
                DbUtilities.closeDatabaseObjects(ps, null);
                DbConnector.returnConnection(dbcon);
            }
        }
    }
    
    @Override
    public final void setAllianceNumber(final int newAllianceNumber) {
        if (this.allianceNumber != newAllianceNumber) {
            this.allianceNumber = newAllianceNumber;
            Connection dbcon = null;
            PreparedStatement ps = null;
            try {
                dbcon = DbConnector.getZonesDbCon();
                ps = dbcon.prepareStatement("UPDATE VILLAGES SET ALLIANCENUMBER=? WHERE ID=?");
                ps.setInt(1, this.allianceNumber);
                ps.setInt(2, this.id);
                ps.executeUpdate();
            }
            catch (SQLException sqx) {
                DbVillage.logger.log(Level.WARNING, "Failed to set allianceNumber=" + this.allianceNumber + " for village with id " + this.id, sqx);
            }
            finally {
                DbUtilities.closeDatabaseObjects(ps, null);
                DbConnector.returnConnection(dbcon);
            }
            if (this.allianceNumber > 0) {
                try {
                    this.getRoleForStatus((byte)5);
                }
                catch (NoSuchRoleException nsr) {
                    this.createRoleAlly();
                }
            }
        }
    }
    
    static {
        logger = Logger.getLogger(DbVillage.class.getName());
    }
}
