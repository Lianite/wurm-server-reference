// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.villages;

import java.sql.ResultSet;
import java.sql.PreparedStatement;
import java.sql.Connection;
import java.sql.Statement;
import com.wurmonline.server.utils.DbUtilities;
import java.sql.SQLException;
import java.util.logging.Level;
import com.wurmonline.server.DbConnector;
import java.io.IOException;
import java.util.logging.Logger;

public final class DbVillageRole extends VillageRole implements VillageStatus, Comparable<DbVillageRole>
{
    private static final Logger logger;
    private static final String CREATE_ROLE = "INSERT INTO VILLAGEROLE (VILLAGEID,NAME ,MAYTERRAFORM ,MAYCUTTREE ,MAYMINE ,MAYFARM ,MAYBUILD ,MAYHIRE,MAYINVITE,MAYDESTROY,MAYMANAGEROLES, MAYEXPAND,MAYLOCKFENCES, MAYPASSFENCES,DIPLOMAT, MAYATTACKCITIZ, MAYATTACKNONCITIZ,MAYFISH,MAYCUTOLD, STATUS,VILLAGEAPPLIEDTO,MAYPUSHPULLTURN,MAYUPDATEMAP,MAYLEAD,MAYPICKUP,MAYTAME,MAYLOAD,MAYBUTCHER,MAYATTACHLOCK,MAYPICKLOCKS,PLAYERAPPLIEDTO,SETTINGS,MORESETTINGS,EXTRASETTINGS) values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
    private static final String UPDATE_ROLE = "UPDATE VILLAGEROLE SET VILLAGEID=?,NAME=?,MAYTERRAFORM=?,MAYCUTTREE=?,MAYMINE=?,MAYFARM=?,MAYBUILD=?,MAYHIRE=?,MAYINVITE=?,MAYDESTROY=?,MAYMANAGEROLES=?,MAYEXPAND=?,MAYLOCKFENCES=?,MAYPASSFENCES=?,DIPLOMAT=?,MAYATTACKCITIZ=?,MAYATTACKNONCITIZ=?,MAYFISH=?,MAYCUTOLD=?,STATUS=?,VILLAGEAPPLIEDTO=?,MAYPUSHPULLTURN=?,MAYUPDATEMAP=?,MAYLEAD=?,MAYPICKUP=?,MAYTAME=?,MAYLOAD=?,MAYBUTCHER=?,MAYATTACHLOCK=?,MAYPICKLOCKS=?,PLAYERAPPLIEDTO=?,SETTINGS=?,MORESETTINGS=?,EXTRASETTINGS=? WHERE ID=?";
    private static final String SET_NAME = "UPDATE VILLAGEROLE SET NAME=? WHERE ID=?";
    private static final String SET_MAYTERRAFORM = "UPDATE VILLAGEROLE SET MAYTERRAFORM=? WHERE ID=?";
    private static final String SET_MAYCUTTREE = "UPDATE VILLAGEROLE SET MAYCUTTREE=? WHERE ID=?";
    private static final String SET_MAYMINE = "UPDATE VILLAGEROLE SET MAYMINE=? WHERE ID=?";
    private static final String SET_MAYBUILD = "UPDATE VILLAGEROLE SET MAYBUILD=? WHERE ID=?";
    private static final String SET_MAYHIRE = "UPDATE VILLAGEROLE SET MAYHIRE=? WHERE ID=?";
    private static final String SET_MAYINVITE = "UPDATE VILLAGEROLE SET MAYINVITE=? WHERE ID=?";
    private static final String SET_MAYDESTROY = "UPDATE VILLAGEROLE SET MAYDESTROY=? WHERE ID=?";
    private static final String SET_MAYMANAGEROLES = "UPDATE VILLAGEROLE SET MAYMANAGEROLES=? WHERE ID=?";
    private static final String SET_MAYFARM = "UPDATE VILLAGEROLE SET MAYFARM=? WHERE ID=?";
    private static final String SET_MAYEXPAND = "UPDATE VILLAGEROLE SET MAYEXPAND=? WHERE ID=?";
    private static final String SET_MAYLOCKFENCES = "UPDATE VILLAGEROLE SET MAYLOCKFENCES=? WHERE ID=?";
    private static final String SET_MAYPASSFENCES = "UPDATE VILLAGEROLE SET MAYPASSFENCES=? WHERE ID=?";
    private static final String SET_MAYATTACKCITIZENS = "UPDATE VILLAGEROLE SET MAYATTACKCITIZ=? WHERE ID=?";
    private static final String SET_MAYATTACKNONCITIZENS = "UPDATE VILLAGEROLE SET MAYATTACKNONCITIZ=? WHERE ID=?";
    private static final String SET_MAYFISH = "UPDATE VILLAGEROLE SET MAYFISH=? WHERE ID=?";
    private static final String SET_MAYCUTOLD = "UPDATE VILLAGEROLE SET MAYCUTOLD=? WHERE ID=?";
    private static final String SET_DIPLOMAT = "UPDATE VILLAGEROLE SET DIPLOMAT=? WHERE ID=?";
    private static final String SET_VILLAGEAPPLIEDTO = "UPDATE VILLAGEROLE SET VILLAGEAPPLIEDTO=? WHERE ID=?";
    private static final String SET_MAYPUSHPULLTURN = "UPDATE VILLAGEROLE SET MAYPUSHPULLTURN=? WHERE ID=?";
    private static final String SET_MAYUPDATEMAP = "UPDATE VILLAGEROLE SET MAYUPDATEMAP=? WHERE ID=?";
    private static final String SET_MAYLEAD = "UPDATE VILLAGEROLE SET MAYLEAD=? WHERE ID=?";
    private static final String SET_MAYPICKUP = "UPDATE VILLAGEROLE SET MAYPICKUP=? WHERE ID=?";
    private static final String SET_MAYTAME = "UPDATE VILLAGEROLE SET MAYTAME=? WHERE ID=?";
    private static final String SET_MAYLOAD = "UPDATE VILLAGEROLE SET MAYLOAD=? WHERE ID=?";
    private static final String SET_MAYBUTCHER = "UPDATE VILLAGEROLE SET MAYBUTCHER=? WHERE ID=?";
    private static final String SET_MAYATTACHLOCK = "UPDATE VILLAGEROLE SET MAYATTACHLOCK=? WHERE ID=?";
    private static final String SET_MAYPICKLOCKS = "UPDATE VILLAGEROLE SET MAYPICKLOCKS=? WHERE ID=?";
    private static final String DELETE = "DELETE FROM VILLAGEROLE WHERE ID=?";
    
    public DbVillageRole(final int aVillageId, final String aName, final boolean aTerraform, final boolean aCutTrees, final boolean aMine, final boolean aFarm, final boolean aBuild, final boolean aHire, final boolean aMayInvite, final boolean aMayDestroy, final boolean aMayManageRoles, final boolean aMayExpand, final boolean aMayLockFences, final boolean aMayPassFences, final boolean aIsDiplomat, final boolean aMayAttackCitizens, final boolean aMayAttackNonCitizens, final boolean aMayFish, final boolean aMayCutOldTrees, final byte aStatus, final int appliedToVillage, final boolean aMayPushPullTurn, final boolean aMayUpdateMap, final boolean aMayLead, final boolean aMayPickup, final boolean aMayTame, final boolean aMayLoad, final boolean aMayButcher, final boolean aMayAttachLock, final boolean aMayPickLocks, final long appliedToPlayer, final int aSettings, final int aMoreSettings, final int aExtraSettings) throws IOException {
        super(aVillageId, aName, aTerraform, aCutTrees, aMine, aFarm, aBuild, aHire, aMayInvite, aMayDestroy, aMayManageRoles, aMayExpand, aMayLockFences, aMayPassFences, aIsDiplomat, aMayAttackCitizens, aMayAttackNonCitizens, aMayFish, aMayCutOldTrees, aStatus, appliedToVillage, aMayPushPullTurn, aMayUpdateMap, aMayLead, aMayPickup, aMayTame, aMayLoad, aMayButcher, aMayAttachLock, aMayPickLocks, appliedToPlayer, aSettings, aMoreSettings, aExtraSettings);
    }
    
    DbVillageRole(final int aId, final int aVillageId, final String aRoleName, final boolean aMayTerraform, final boolean aMayCuttrees, final boolean aMayMine, final boolean aMayFarm, final boolean aMayBuild, final boolean aMayHire, final boolean aMayInvite, final boolean aMayDestroy, final boolean aMayManageRoles, final boolean aMayExpand, final boolean aMayPassAllFences, final boolean aMayLockFences, final boolean aMayAttackCitizens, final boolean aMayAttackNonCitizens, final boolean aMayFish, final boolean aMayCutOldTrees, final boolean aMayPushPullTurn, final boolean aDiplomat, final byte aStatus, final int aVillageAppliedTo, final boolean aMayUpdateMap, final boolean aMayLead, final boolean aMayPickup, final boolean aMayTame, final boolean aMayLoad, final boolean aMayButcher, final boolean aMayAttachLock, final boolean aMayPickLocks, final long aPlayerAppliedTo, final int aSettings, final int aMoreSettings, final int aExtraSettings) {
        super(aId, aVillageId, aRoleName, aMayTerraform, aMayCuttrees, aMayMine, aMayFarm, aMayBuild, aMayHire, aMayInvite, aMayDestroy, aMayManageRoles, aMayExpand, aMayPassAllFences, aMayLockFences, aMayAttackCitizens, aMayAttackNonCitizens, aMayFish, aMayCutOldTrees, aMayPushPullTurn, aDiplomat, aStatus, aVillageAppliedTo, aMayUpdateMap, aMayLead, aMayPickup, aMayTame, aMayLoad, aMayButcher, aMayAttachLock, aMayPickLocks, aPlayerAppliedTo, aSettings, aMoreSettings, aExtraSettings);
    }
    
    @Override
    void create() throws IOException {
        Connection dbcon = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            dbcon = DbConnector.getZonesDbCon();
            ps = dbcon.prepareStatement("INSERT INTO VILLAGEROLE (VILLAGEID,NAME ,MAYTERRAFORM ,MAYCUTTREE ,MAYMINE ,MAYFARM ,MAYBUILD ,MAYHIRE,MAYINVITE,MAYDESTROY,MAYMANAGEROLES, MAYEXPAND,MAYLOCKFENCES, MAYPASSFENCES,DIPLOMAT, MAYATTACKCITIZ, MAYATTACKNONCITIZ,MAYFISH,MAYCUTOLD, STATUS,VILLAGEAPPLIEDTO,MAYPUSHPULLTURN,MAYUPDATEMAP,MAYLEAD,MAYPICKUP,MAYTAME,MAYLOAD,MAYBUTCHER,MAYATTACHLOCK,MAYPICKLOCKS,PLAYERAPPLIEDTO,SETTINGS,MORESETTINGS,EXTRASETTINGS) values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)", 1);
            ps.setInt(1, this.villageid);
            ps.setString(2, this.name);
            ps.setBoolean(3, this.mayTerraform);
            ps.setBoolean(4, this.mayCuttrees);
            ps.setBoolean(5, this.mayMine);
            ps.setBoolean(6, this.mayFarm);
            ps.setBoolean(7, this.mayBuild);
            ps.setBoolean(8, this.mayHire);
            ps.setBoolean(9, this.mayInvite);
            ps.setBoolean(10, this.mayDestroy);
            ps.setBoolean(11, this.mayManageRoles);
            ps.setBoolean(12, this.mayExpand);
            ps.setBoolean(13, this.mayLockFences);
            ps.setBoolean(14, this.mayPassAllFences);
            ps.setBoolean(15, this.diplomat);
            ps.setBoolean(16, this.mayAttackCitizens);
            ps.setBoolean(17, this.mayAttackNonCitizens);
            ps.setBoolean(18, this.mayFish);
            ps.setBoolean(19, this.mayCutOldTrees);
            ps.setByte(20, this.status);
            ps.setInt(21, this.villageAppliedTo);
            ps.setBoolean(22, this.mayPushPullTurn);
            ps.setBoolean(23, this.mayUpdateMap);
            ps.setBoolean(24, this.mayLead);
            ps.setBoolean(25, this.mayPickup);
            ps.setBoolean(26, this.mayTame);
            ps.setBoolean(27, this.mayLoad);
            ps.setBoolean(28, this.mayButcher);
            ps.setBoolean(29, this.mayAttachLock);
            ps.setBoolean(30, this.mayPickLocks);
            ps.setLong(31, this.playerAppliedTo);
            ps.setInt(32, this.settings.getPermissions());
            ps.setInt(33, this.moreSettings.getPermissions());
            ps.setInt(34, this.extraSettings.getPermissions());
            ps.executeUpdate();
            rs = ps.getGeneratedKeys();
            if (rs.next()) {
                this.id = rs.getInt(1);
            }
        }
        catch (SQLException sqx) {
            DbVillageRole.logger.log(Level.WARNING, "Failed to set status for citizen " + this.name + ": " + sqx.getMessage(), sqx);
            throw new IOException(sqx);
        }
        finally {
            DbUtilities.closeDatabaseObjects(ps, rs);
            DbConnector.returnConnection(dbcon);
        }
    }
    
    @Override
    public void save() throws IOException {
        Connection dbcon = null;
        PreparedStatement ps = null;
        final ResultSet rs = null;
        if (this.status == 1 && this.mayDestroy) {
            this.mayDestroy = false;
            DbVillageRole.logger.warning("Saving roleID " + this.id + ": mayDestroy set for ROLE_EVERYBODY");
            Thread.dumpStack();
        }
        try {
            dbcon = DbConnector.getZonesDbCon();
            ps = dbcon.prepareStatement("UPDATE VILLAGEROLE SET VILLAGEID=?,NAME=?,MAYTERRAFORM=?,MAYCUTTREE=?,MAYMINE=?,MAYFARM=?,MAYBUILD=?,MAYHIRE=?,MAYINVITE=?,MAYDESTROY=?,MAYMANAGEROLES=?,MAYEXPAND=?,MAYLOCKFENCES=?,MAYPASSFENCES=?,DIPLOMAT=?,MAYATTACKCITIZ=?,MAYATTACKNONCITIZ=?,MAYFISH=?,MAYCUTOLD=?,STATUS=?,VILLAGEAPPLIEDTO=?,MAYPUSHPULLTURN=?,MAYUPDATEMAP=?,MAYLEAD=?,MAYPICKUP=?,MAYTAME=?,MAYLOAD=?,MAYBUTCHER=?,MAYATTACHLOCK=?,MAYPICKLOCKS=?,PLAYERAPPLIEDTO=?,SETTINGS=?,MORESETTINGS=?,EXTRASETTINGS=? WHERE ID=?");
            ps.setInt(1, this.villageid);
            ps.setString(2, this.name);
            ps.setBoolean(3, this.mayTerraform);
            ps.setBoolean(4, this.mayCuttrees);
            ps.setBoolean(5, this.mayMine);
            ps.setBoolean(6, this.mayFarm);
            ps.setBoolean(7, this.mayBuild);
            ps.setBoolean(8, this.mayHire);
            ps.setBoolean(9, this.mayInvite);
            ps.setBoolean(10, this.mayDestroy);
            ps.setBoolean(11, this.mayManageRoles);
            ps.setBoolean(12, this.mayExpand);
            ps.setBoolean(13, this.mayLockFences);
            ps.setBoolean(14, this.mayPassAllFences);
            ps.setBoolean(15, this.diplomat);
            ps.setBoolean(16, this.mayAttackCitizens);
            ps.setBoolean(17, this.mayAttackNonCitizens);
            ps.setBoolean(18, this.mayFish);
            ps.setBoolean(19, this.mayCutOldTrees);
            ps.setByte(20, this.status);
            ps.setInt(21, this.villageAppliedTo);
            ps.setBoolean(22, this.mayPushPullTurn);
            ps.setBoolean(23, this.mayUpdateMap);
            ps.setBoolean(24, this.mayLead);
            ps.setBoolean(25, this.mayPickup);
            ps.setBoolean(26, this.mayTame);
            ps.setBoolean(27, this.mayLoad);
            ps.setBoolean(28, this.mayButcher);
            ps.setBoolean(29, this.mayAttachLock);
            ps.setBoolean(30, this.mayPickLocks);
            ps.setLong(31, this.playerAppliedTo);
            ps.setInt(32, this.settings.getPermissions());
            ps.setInt(33, this.moreSettings.getPermissions());
            ps.setInt(34, this.extraSettings.getPermissions());
            ps.setInt(35, this.id);
            ps.executeUpdate();
        }
        catch (SQLException sqx) {
            DbVillageRole.logger.log(Level.WARNING, "Failed to save role " + this.name + ": " + sqx.getMessage(), sqx);
            throw new IOException(sqx);
        }
        finally {
            DbUtilities.closeDatabaseObjects(ps, rs);
            DbConnector.returnConnection(dbcon);
        }
    }
    
    @Override
    public void setMayHire(final boolean hire) throws IOException {
        if (this.mayHire != hire) {
            this.mayHire = hire;
            Connection dbcon = null;
            PreparedStatement ps = null;
            try {
                dbcon = DbConnector.getZonesDbCon();
                ps = dbcon.prepareStatement("UPDATE VILLAGEROLE SET MAYHIRE=? WHERE ID=?");
                ps.setBoolean(1, this.mayHire);
                ps.setInt(2, this.id);
                ps.executeUpdate();
            }
            catch (SQLException sqx) {
                DbVillageRole.logger.log(Level.WARNING, "Failed to set data for role with id " + this.id, sqx);
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
            this.name = aName;
            Connection dbcon = null;
            PreparedStatement ps = null;
            try {
                dbcon = DbConnector.getZonesDbCon();
                ps = dbcon.prepareStatement("UPDATE VILLAGEROLE SET NAME=? WHERE ID=?");
                ps.setString(1, aName);
                ps.setInt(2, this.id);
                ps.executeUpdate();
            }
            catch (SQLException sqx) {
                DbVillageRole.logger.log(Level.WARNING, "Failed to set data for role with id " + this.id, sqx);
                throw new IOException(sqx);
            }
            finally {
                DbUtilities.closeDatabaseObjects(ps, null);
                DbConnector.returnConnection(dbcon);
            }
        }
    }
    
    @Override
    public void setMayBuild(final boolean build) throws IOException {
        if (this.mayBuild != build) {
            this.mayBuild = build;
            Connection dbcon = null;
            PreparedStatement ps = null;
            try {
                dbcon = DbConnector.getZonesDbCon();
                ps = dbcon.prepareStatement("UPDATE VILLAGEROLE SET MAYBUILD=? WHERE ID=?");
                ps.setBoolean(1, this.mayBuild);
                ps.setInt(2, this.id);
                ps.executeUpdate();
            }
            catch (SQLException sqx) {
                DbVillageRole.logger.log(Level.WARNING, "Failed to set data for role with id " + this.id, sqx);
                throw new IOException(sqx);
            }
            finally {
                DbUtilities.closeDatabaseObjects(ps, null);
                DbConnector.returnConnection(dbcon);
            }
        }
    }
    
    @Override
    public void setMayCuttrees(final boolean aCutTrees) throws IOException {
        if (this.mayCuttrees != aCutTrees) {
            this.mayCuttrees = aCutTrees;
            Connection dbcon = null;
            PreparedStatement ps = null;
            try {
                dbcon = DbConnector.getZonesDbCon();
                ps = dbcon.prepareStatement("UPDATE VILLAGEROLE SET MAYCUTTREE=? WHERE ID=?");
                ps.setBoolean(1, this.mayCuttrees);
                ps.setInt(2, this.id);
                ps.executeUpdate();
            }
            catch (SQLException sqx) {
                DbVillageRole.logger.log(Level.WARNING, "Failed to set data for role with id " + this.id, sqx);
                throw new IOException(sqx);
            }
            finally {
                DbUtilities.closeDatabaseObjects(ps, null);
                DbConnector.returnConnection(dbcon);
            }
        }
    }
    
    @Override
    public void setMayMine(final boolean mine) throws IOException {
        if (this.mayMine != mine) {
            this.mayMine = mine;
            Connection dbcon = null;
            PreparedStatement ps = null;
            try {
                dbcon = DbConnector.getZonesDbCon();
                ps = dbcon.prepareStatement("UPDATE VILLAGEROLE SET MAYMINE=? WHERE ID=?");
                ps.setBoolean(1, this.mayMine);
                ps.setInt(2, this.id);
                ps.executeUpdate();
            }
            catch (SQLException sqx) {
                DbVillageRole.logger.log(Level.WARNING, "Failed to set data for role with id " + this.id, sqx);
                throw new IOException(sqx);
            }
            finally {
                DbUtilities.closeDatabaseObjects(ps, null);
                DbConnector.returnConnection(dbcon);
            }
        }
    }
    
    @Override
    public void setMayFarm(final boolean farm) throws IOException {
        if (this.mayFarm != farm) {
            this.mayFarm = farm;
            Connection dbcon = null;
            PreparedStatement ps = null;
            try {
                dbcon = DbConnector.getZonesDbCon();
                ps = dbcon.prepareStatement("UPDATE VILLAGEROLE SET MAYFARM=? WHERE ID=?");
                ps.setBoolean(1, this.mayFarm);
                ps.setInt(2, this.id);
                ps.executeUpdate();
            }
            catch (SQLException sqx) {
                DbVillageRole.logger.log(Level.WARNING, "Failed to set data for role with id " + this.id, sqx);
                throw new IOException(sqx);
            }
            finally {
                DbUtilities.closeDatabaseObjects(ps, null);
                DbConnector.returnConnection(dbcon);
            }
        }
    }
    
    @Override
    public void setMayManageRoles(final boolean mayManage) throws IOException {
        if (this.mayManageRoles != mayManage) {
            this.mayManageRoles = mayManage;
            Connection dbcon = null;
            PreparedStatement ps = null;
            try {
                dbcon = DbConnector.getZonesDbCon();
                ps = dbcon.prepareStatement("UPDATE VILLAGEROLE SET MAYMANAGEROLES=? WHERE ID=?");
                ps.setBoolean(1, this.mayManageRoles);
                ps.setInt(2, this.id);
                ps.executeUpdate();
            }
            catch (SQLException sqx) {
                DbVillageRole.logger.log(Level.WARNING, "Failed to set data for role with id " + this.id, sqx);
                throw new IOException(sqx);
            }
            finally {
                DbUtilities.closeDatabaseObjects(ps, null);
                DbConnector.returnConnection(dbcon);
            }
        }
    }
    
    @Override
    public void setMayDestroy(final boolean destroy) throws IOException {
        if (this.status == 1 && destroy) {
            DbVillageRole.logger.warning("Attempting to set MayDestroy on RoleID " + this.id);
            Thread.dumpStack();
            return;
        }
        if (this.mayDestroy != destroy) {
            this.mayDestroy = destroy;
            Connection dbcon = null;
            PreparedStatement ps = null;
            try {
                dbcon = DbConnector.getZonesDbCon();
                ps = dbcon.prepareStatement("UPDATE VILLAGEROLE SET MAYDESTROY=? WHERE ID=?");
                ps.setBoolean(1, this.mayDestroy);
                ps.setInt(2, this.id);
                ps.executeUpdate();
            }
            catch (SQLException sqx) {
                DbVillageRole.logger.log(Level.WARNING, "Failed to set data for role with id " + this.id, sqx);
                throw new IOException(sqx);
            }
            finally {
                DbUtilities.closeDatabaseObjects(ps, null);
                DbConnector.returnConnection(dbcon);
            }
        }
    }
    
    @Override
    public void setMayTerraform(final boolean terraform) throws IOException {
        if (this.mayTerraform != terraform) {
            this.mayTerraform = terraform;
            Connection dbcon = null;
            PreparedStatement ps = null;
            try {
                dbcon = DbConnector.getZonesDbCon();
                ps = dbcon.prepareStatement("UPDATE VILLAGEROLE SET MAYTERRAFORM=? WHERE ID=?");
                ps.setBoolean(1, this.mayTerraform);
                ps.setInt(2, this.id);
                ps.executeUpdate();
            }
            catch (SQLException sqx) {
                DbVillageRole.logger.log(Level.WARNING, "Failed to set data for role with id " + this.id, sqx);
                throw new IOException(sqx);
            }
            finally {
                DbUtilities.closeDatabaseObjects(ps, null);
                DbConnector.returnConnection(dbcon);
            }
        }
    }
    
    @Override
    public void setMayUpdateMap(final boolean updateMap) throws IOException {
        if (this.mayUpdateMap != updateMap) {
            this.mayUpdateMap = updateMap;
            Connection dbcon = null;
            PreparedStatement ps = null;
            try {
                dbcon = DbConnector.getZonesDbCon();
                ps = dbcon.prepareStatement("UPDATE VILLAGEROLE SET MAYUPDATEMAP=? WHERE ID=?");
                ps.setBoolean(1, this.mayUpdateMap);
                ps.setInt(2, this.id);
                ps.executeUpdate();
            }
            catch (SQLException sqx) {
                DbVillageRole.logger.log(Level.WARNING, "Failed to set data for role with id " + this.id, sqx);
                throw new IOException(sqx);
            }
            finally {
                DbUtilities.closeDatabaseObjects(ps, null);
                DbConnector.returnConnection(dbcon);
            }
        }
    }
    
    @Override
    public void setMayInvite(final boolean invite) throws IOException {
        if (this.mayInvite != invite) {
            this.mayInvite = invite;
            Connection dbcon = null;
            PreparedStatement ps = null;
            try {
                dbcon = DbConnector.getZonesDbCon();
                ps = dbcon.prepareStatement("UPDATE VILLAGEROLE SET MAYINVITE=? WHERE ID=?");
                ps.setBoolean(1, this.mayInvite);
                ps.setInt(2, this.id);
                ps.executeUpdate();
            }
            catch (SQLException sqx) {
                DbVillageRole.logger.log(Level.WARNING, "Failed to set data for role with id " + this.id, sqx);
                throw new IOException(sqx);
            }
            finally {
                DbUtilities.closeDatabaseObjects(ps, null);
                DbConnector.returnConnection(dbcon);
            }
        }
    }
    
    @Override
    public void setMayExpand(final boolean expand) throws IOException {
        if (this.mayExpand != expand) {
            this.mayExpand = expand;
            Connection dbcon = null;
            PreparedStatement ps = null;
            try {
                dbcon = DbConnector.getZonesDbCon();
                ps = dbcon.prepareStatement("UPDATE VILLAGEROLE SET MAYEXPAND=? WHERE ID=?");
                ps.setBoolean(1, this.mayExpand);
                ps.setInt(2, this.id);
                ps.executeUpdate();
            }
            catch (SQLException sqx) {
                DbVillageRole.logger.log(Level.WARNING, "Failed to set data for role with id " + this.id, sqx);
                throw new IOException(sqx);
            }
            finally {
                DbUtilities.closeDatabaseObjects(ps, null);
                DbConnector.returnConnection(dbcon);
            }
        }
    }
    
    @Override
    public void setMayPassAllFences(final boolean maypass) throws IOException {
        if (this.mayPassAllFences != maypass) {
            this.mayPassAllFences = maypass;
            Connection dbcon = null;
            PreparedStatement ps = null;
            try {
                dbcon = DbConnector.getZonesDbCon();
                ps = dbcon.prepareStatement("UPDATE VILLAGEROLE SET MAYPASSFENCES=? WHERE ID=?");
                ps.setBoolean(1, this.mayPassAllFences);
                ps.setInt(2, this.id);
                ps.executeUpdate();
            }
            catch (SQLException sqx) {
                DbVillageRole.logger.log(Level.WARNING, "Failed to set data for role with id " + this.id, sqx);
                throw new IOException(sqx);
            }
            finally {
                DbUtilities.closeDatabaseObjects(ps, null);
                DbConnector.returnConnection(dbcon);
            }
        }
    }
    
    @Override
    public void setMayLockFences(final boolean maylock) throws IOException {
        if (this.mayLockFences != maylock) {
            this.mayLockFences = maylock;
            Connection dbcon = null;
            PreparedStatement ps = null;
            try {
                dbcon = DbConnector.getZonesDbCon();
                ps = dbcon.prepareStatement("UPDATE VILLAGEROLE SET MAYLOCKFENCES=? WHERE ID=?");
                ps.setBoolean(1, this.mayLockFences);
                ps.setInt(2, this.id);
                ps.executeUpdate();
            }
            catch (SQLException sqx) {
                DbVillageRole.logger.log(Level.WARNING, "Failed to set data for role with id " + this.id, sqx);
                throw new IOException(sqx);
            }
            finally {
                DbUtilities.closeDatabaseObjects(ps, null);
                DbConnector.returnConnection(dbcon);
            }
        }
    }
    
    @Override
    public void setVillageAppliedTo(final int newVillage) throws IOException {
        if (this.villageAppliedTo != newVillage) {
            this.villageAppliedTo = newVillage;
            Connection dbcon = null;
            PreparedStatement ps = null;
            try {
                dbcon = DbConnector.getZonesDbCon();
                ps = dbcon.prepareStatement("UPDATE VILLAGEROLE SET VILLAGEAPPLIEDTO=? WHERE ID=?");
                ps.setInt(1, this.villageAppliedTo);
                ps.setInt(2, this.id);
                ps.executeUpdate();
            }
            catch (SQLException sqx) {
                DbVillageRole.logger.log(Level.WARNING, "Failed to set data for role with id " + this.id, sqx);
                throw new IOException(sqx);
            }
            finally {
                DbUtilities.closeDatabaseObjects(ps, null);
                DbConnector.returnConnection(dbcon);
            }
        }
    }
    
    @Override
    public void setDiplomat(final boolean isDiplomat) throws IOException {
        if (this.diplomat != isDiplomat) {
            this.diplomat = isDiplomat;
            Connection dbcon = null;
            PreparedStatement ps = null;
            try {
                dbcon = DbConnector.getZonesDbCon();
                ps = dbcon.prepareStatement("UPDATE VILLAGEROLE SET DIPLOMAT=? WHERE ID=?");
                ps.setBoolean(1, this.diplomat);
                ps.setInt(2, this.id);
                ps.executeUpdate();
            }
            catch (SQLException sqx) {
                DbVillageRole.logger.log(Level.WARNING, "Failed to set data for role with id " + this.id, sqx);
                throw new IOException(sqx);
            }
            finally {
                DbUtilities.closeDatabaseObjects(ps, null);
                DbConnector.returnConnection(dbcon);
            }
        }
    }
    
    @Override
    public void setMayAttackCitizens(final boolean attack) throws IOException {
        if (this.mayAttackCitizens != attack) {
            this.mayAttackCitizens = attack;
            Connection dbcon = null;
            PreparedStatement ps = null;
            try {
                dbcon = DbConnector.getZonesDbCon();
                ps = dbcon.prepareStatement("UPDATE VILLAGEROLE SET MAYATTACKCITIZ=? WHERE ID=?");
                ps.setBoolean(1, this.mayAttackCitizens);
                ps.setInt(2, this.id);
                ps.executeUpdate();
            }
            catch (SQLException sqx) {
                DbVillageRole.logger.log(Level.WARNING, "Failed to set data for role with id " + this.id, sqx);
                throw new IOException(sqx);
            }
            finally {
                DbUtilities.closeDatabaseObjects(ps, null);
                DbConnector.returnConnection(dbcon);
            }
        }
    }
    
    @Override
    public void setMayAttackNonCitizens(final boolean attack) throws IOException {
        if (this.mayAttackNonCitizens != attack) {
            this.mayAttackNonCitizens = attack;
            Connection dbcon = null;
            PreparedStatement ps = null;
            try {
                dbcon = DbConnector.getZonesDbCon();
                ps = dbcon.prepareStatement("UPDATE VILLAGEROLE SET MAYATTACKNONCITIZ=? WHERE ID=?");
                ps.setBoolean(1, this.mayAttackNonCitizens);
                ps.setInt(2, this.id);
                ps.executeUpdate();
            }
            catch (SQLException sqx) {
                DbVillageRole.logger.log(Level.WARNING, "Failed to set data for role with id " + this.id, sqx);
                throw new IOException(sqx);
            }
            finally {
                DbUtilities.closeDatabaseObjects(ps, null);
                DbConnector.returnConnection(dbcon);
            }
        }
    }
    
    @Override
    public void setMayPushPullTurn(final boolean pushpullturn) throws IOException {
        if (this.mayPushPullTurn == pushpullturn) {
            return;
        }
        this.mayPushPullTurn = pushpullturn;
        Connection dbcon = null;
        PreparedStatement ps = null;
        try {
            dbcon = DbConnector.getZonesDbCon();
            ps = dbcon.prepareStatement("UPDATE VILLAGEROLE SET MAYPUSHPULLTURN=? WHERE ID=?");
            ps.setBoolean(1, pushpullturn);
            ps.setInt(2, this.id);
            ps.executeUpdate();
        }
        catch (SQLException sqx) {
            DbVillageRole.logger.log(Level.WARNING, "Failed to set data for role with id " + this.id, sqx);
            throw new IOException(sqx);
        }
        finally {
            DbUtilities.closeDatabaseObjects(ps, null);
            DbConnector.returnConnection(dbcon);
        }
    }
    
    @Override
    public void setMayLead(final boolean lead) throws IOException {
        if (this.mayLead == lead) {
            return;
        }
        this.mayLead = lead;
        Connection dbcon = null;
        PreparedStatement ps = null;
        try {
            dbcon = DbConnector.getZonesDbCon();
            ps = dbcon.prepareStatement("UPDATE VILLAGEROLE SET MAYLEAD=? WHERE ID=?");
            ps.setBoolean(1, lead);
            ps.setInt(2, this.id);
            ps.executeUpdate();
        }
        catch (SQLException sqx) {
            DbVillageRole.logger.log(Level.WARNING, "Failed to set data for role with id " + this.id, sqx);
            throw new IOException(sqx);
        }
        finally {
            DbUtilities.closeDatabaseObjects(ps, null);
            DbConnector.returnConnection(dbcon);
        }
    }
    
    @Override
    public void setMayPickup(final boolean pickup) throws IOException {
        if (this.mayPickup == pickup) {
            return;
        }
        this.mayPickup = pickup;
        Connection dbcon = null;
        PreparedStatement ps = null;
        try {
            dbcon = DbConnector.getZonesDbCon();
            ps = dbcon.prepareStatement("UPDATE VILLAGEROLE SET MAYPICKUP=? WHERE ID=?");
            ps.setBoolean(1, pickup);
            ps.setInt(2, this.id);
            ps.executeUpdate();
        }
        catch (SQLException sqx) {
            DbVillageRole.logger.log(Level.WARNING, "Failed to set data for role with id " + this.id, sqx);
            throw new IOException(sqx);
        }
        finally {
            DbUtilities.closeDatabaseObjects(ps, null);
            DbConnector.returnConnection(dbcon);
        }
    }
    
    @Override
    public void setMayTame(final boolean tame) throws IOException {
        if (this.mayTame == tame) {
            return;
        }
        this.mayTame = tame;
        Connection dbcon = null;
        PreparedStatement ps = null;
        try {
            dbcon = DbConnector.getZonesDbCon();
            ps = dbcon.prepareStatement("UPDATE VILLAGEROLE SET MAYTAME=? WHERE ID=?");
            ps.setBoolean(1, tame);
            ps.setInt(2, this.id);
            ps.executeUpdate();
        }
        catch (SQLException sqx) {
            DbVillageRole.logger.log(Level.WARNING, "Failed to set data for role with id " + this.id, sqx);
            throw new IOException(sqx);
        }
        finally {
            DbUtilities.closeDatabaseObjects(ps, null);
            DbConnector.returnConnection(dbcon);
        }
    }
    
    @Override
    public void setMayLoad(final boolean load) throws IOException {
        if (this.mayLoad == load) {
            return;
        }
        this.mayLoad = load;
        Connection dbcon = null;
        PreparedStatement ps = null;
        try {
            dbcon = DbConnector.getZonesDbCon();
            ps = dbcon.prepareStatement("UPDATE VILLAGEROLE SET MAYLOAD=? WHERE ID=?");
            ps.setBoolean(1, load);
            ps.setInt(2, this.id);
            ps.executeUpdate();
        }
        catch (SQLException sqx) {
            DbVillageRole.logger.log(Level.WARNING, "Failed to set data for role with id " + this.id, sqx);
            throw new IOException(sqx);
        }
        finally {
            DbUtilities.closeDatabaseObjects(ps, null);
            DbConnector.returnConnection(dbcon);
        }
    }
    
    @Override
    public void setMayButcher(final boolean butcher) throws IOException {
        if (this.mayButcher == butcher) {
            return;
        }
        this.mayButcher = butcher;
        Connection dbcon = null;
        PreparedStatement ps = null;
        try {
            dbcon = DbConnector.getZonesDbCon();
            ps = dbcon.prepareStatement("UPDATE VILLAGEROLE SET MAYBUTCHER=? WHERE ID=?");
            ps.setBoolean(1, butcher);
            ps.setInt(2, this.id);
            ps.executeUpdate();
        }
        catch (SQLException sqx) {
            DbVillageRole.logger.log(Level.WARNING, "Failed to set data for role with id " + this.id, sqx);
            throw new IOException(sqx);
        }
        finally {
            DbUtilities.closeDatabaseObjects(ps, null);
            DbConnector.returnConnection(dbcon);
        }
    }
    
    @Override
    public void setMayAttachLock(final boolean attachLock) throws IOException {
        if (this.mayAttachLock == attachLock) {
            return;
        }
        this.mayAttachLock = attachLock;
        Connection dbcon = null;
        PreparedStatement ps = null;
        try {
            dbcon = DbConnector.getZonesDbCon();
            ps = dbcon.prepareStatement("UPDATE VILLAGEROLE SET MAYATTACHLOCK=? WHERE ID=?");
            ps.setBoolean(1, attachLock);
            ps.setInt(2, this.id);
            ps.executeUpdate();
        }
        catch (SQLException sqx) {
            DbVillageRole.logger.log(Level.WARNING, "Failed to set data for role with id " + this.id, sqx);
            throw new IOException(sqx);
        }
        finally {
            DbUtilities.closeDatabaseObjects(ps, null);
            DbConnector.returnConnection(dbcon);
        }
    }
    
    @Override
    public void setMayPickLocks(final boolean pickLocks) throws IOException {
        if (this.mayPickLocks == pickLocks) {
            return;
        }
        this.mayPickLocks = pickLocks;
        Connection dbcon = null;
        PreparedStatement ps = null;
        try {
            dbcon = DbConnector.getZonesDbCon();
            ps = dbcon.prepareStatement("UPDATE VILLAGEROLE SET MAYPICKLOCKS=? WHERE ID=?");
            ps.setBoolean(1, pickLocks);
            ps.setInt(2, this.id);
            ps.executeUpdate();
        }
        catch (SQLException sqx) {
            DbVillageRole.logger.log(Level.WARNING, "Failed to set data for role with id " + this.id, sqx);
            throw new IOException(sqx);
        }
        finally {
            DbUtilities.closeDatabaseObjects(ps, null);
            DbConnector.returnConnection(dbcon);
        }
    }
    
    @Override
    public void setMayFish(final boolean fish) throws IOException {
        if (this.mayFish != fish) {
            this.mayFish = fish;
            Connection dbcon = null;
            PreparedStatement ps = null;
            try {
                dbcon = DbConnector.getZonesDbCon();
                ps = dbcon.prepareStatement("UPDATE VILLAGEROLE SET MAYFISH=? WHERE ID=?");
                ps.setBoolean(1, this.mayFish);
                ps.setInt(2, this.id);
                ps.executeUpdate();
            }
            catch (SQLException sqx) {
                DbVillageRole.logger.log(Level.WARNING, "Failed to set data for role with id " + this.id, sqx);
                throw new IOException(sqx);
            }
            finally {
                DbUtilities.closeDatabaseObjects(ps, null);
                DbConnector.returnConnection(dbcon);
            }
        }
    }
    
    @Override
    public void setCutOld(final boolean cutold) throws IOException {
        if (this.mayCutOldTrees != cutold) {
            this.mayCutOldTrees = cutold;
            Connection dbcon = null;
            PreparedStatement ps = null;
            try {
                dbcon = DbConnector.getZonesDbCon();
                ps = dbcon.prepareStatement("UPDATE VILLAGEROLE SET MAYCUTOLD=? WHERE ID=?");
                ps.setBoolean(1, this.mayCutOldTrees);
                ps.setInt(2, this.id);
                ps.executeUpdate();
            }
            catch (SQLException sqx) {
                DbVillageRole.logger.log(Level.WARNING, "Failed to set data for role with id " + this.id, sqx);
                throw new IOException(sqx);
            }
            finally {
                DbUtilities.closeDatabaseObjects(ps, null);
                DbConnector.returnConnection(dbcon);
            }
        }
    }
    
    @Override
    public void delete() throws IOException {
        Connection dbcon = null;
        PreparedStatement ps = null;
        try {
            dbcon = DbConnector.getZonesDbCon();
            ps = dbcon.prepareStatement("DELETE FROM VILLAGEROLE WHERE ID=?");
            ps.setInt(1, this.id);
            ps.executeUpdate();
        }
        catch (SQLException sqx) {
            DbVillageRole.logger.log(Level.WARNING, "Failed to delete role with id " + this.id, sqx);
            throw new IOException(sqx);
        }
        finally {
            DbUtilities.closeDatabaseObjects(ps, null);
            DbConnector.returnConnection(dbcon);
        }
    }
    
    @Override
    public int compareTo(final DbVillageRole otherDbVillageRole) {
        return this.getName().compareTo(otherDbVillageRole.getName());
    }
    
    static {
        logger = Logger.getLogger(DbVillageRole.class.getName());
    }
}