// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server;

import java.sql.ResultSet;
import java.sql.PreparedStatement;
import java.sql.Connection;
import java.sql.Statement;
import com.wurmonline.server.utils.DbUtilities;
import java.sql.SQLException;
import java.util.logging.Level;
import java.math.BigInteger;
import java.util.logging.Logger;
import com.wurmonline.shared.constants.CounterTypes;
import java.io.Serializable;

public final class WurmId implements Serializable, CounterTypes
{
    private static final long serialVersionUID = -1805883548433788244L;
    private static long playerIdCounter;
    private static long creatureIdCounter;
    private static long itemIdCounter;
    private static long structureIdCounter;
    private static long tempIdCounter;
    private static long illusionIdCounter;
    private static long woundIdCounter;
    private static long temporaryWoundIdCounter;
    private static long spellIdCounter;
    private static long creatureSkillsIdCounter;
    private static long templateSkillsIdCounter;
    private static long playerSkillsIdCounter;
    private static long temporarySkillsIdCounter;
    private static long planIdCounter;
    private static long bankIdCounter;
    private static long bodyIdCounter;
    private static long coinIdCounter;
    private static long poiIdCounter;
    private static long couponIdCounter;
    private static long wccommandCounter;
    private static int savecounter;
    private static final Logger logger;
    private static final String getMaxPlayerId = "SELECT MAX(WURMID) FROM PLAYERS";
    private static final String getMaxCreatureId = "SELECT MAX(WURMID) FROM CREATURES";
    private static final String getMaxItemId = "SELECT MAX(WURMID) FROM ITEMS";
    private static final String getMaxStructureId = "SELECT MAX(WURMID) FROM STRUCTURES";
    private static final String getMaxWoundId = "SELECT MAX(ID) FROM WOUNDS";
    private static final String getMaxSkillId = "SELECT MAX(ID) FROM SKILLS";
    private static final String getMaxBankId = "SELECT MAX(WURMID) FROM BANKS";
    private static final String getMaxSpellId = "SELECT MAX(WURMID) FROM SPELLEFFECTS";
    private static final String getMaxBodyId = "SELECT MAX(WURMID) FROM BODYPARTS";
    private static final String getMaxCoinId = "SELECT MAX(WURMID) FROM COINS";
    private static final String getMaxPoiId = "SELECT MAX(ID) FROM MAP_ANNOTATIONS";
    private static final String getMaxCouponId = "SELECT MAX(CODEID) FROM REDEEMCODE";
    private static final String getIds = "SELECT * FROM IDS WHERE SERVER=?";
    private static final String createIds = "INSERT INTO IDS (SERVER,PLAYERIDS,CREATUREIDS,ITEMIDS,STRUCTUREIDS,WOUNDIDS,PLAYERSKILLIDS,CREATURESKILLIDS,BANKIDS,SPELLIDS,PLANIDS,BODYIDS,COINIDS,WCCOMMANDS, POIIDS, REDEEMIDS) VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
    private static final String updateIds = "UPDATE IDS SET PLAYERIDS=?,CREATUREIDS=?,ITEMIDS=?,STRUCTUREIDS=?,WOUNDIDS=?,PLAYERSKILLIDS=?,CREATURESKILLIDS=?,BANKIDS=?,SPELLIDS=?,PLANIDS=?,BODYIDS=?,COINIDS=?,WCCOMMANDS=?, POIIDS=?, REDEEMIDS=? WHERE SERVER=?";
    
    public static final int getType(final long id) {
        return (int)(id & 0xFFL);
    }
    
    public static final int getOrigin(final long id) {
        return (int)(id >> 8) & 0xFFFF;
    }
    
    public static final long getNumber(final long id) {
        return id >> 24;
    }
    
    public static final long getId(final long id) {
        return id;
    }
    
    public static final long getNextItemId() {
        ++WurmId.itemIdCounter;
        ++WurmId.savecounter;
        checkSave();
        return BigInteger.valueOf(WurmId.itemIdCounter).shiftLeft(24).longValue() + (Servers.localServer.id << 8) + 2L;
    }
    
    public static final long getNextPlayerId() {
        ++WurmId.playerIdCounter;
        ++WurmId.savecounter;
        checkSave();
        return BigInteger.valueOf(WurmId.playerIdCounter).shiftLeft(24).longValue() + (Servers.localServer.id << 8) + 0L;
    }
    
    public static final long getNextBodyPartId(final long creatureId, final byte bodyplace, final boolean isPlayer) {
        return BigInteger.valueOf(BigInteger.valueOf(creatureId >> 8).shiftLeft(1).longValue() + (long)(isPlayer ? 1 : 0)).shiftLeft(16).longValue() + (bodyplace << 8) + 19L;
    }
    
    public static final long getCreatureIdForBodyPart(final long bodypartId) {
        final boolean isPlayer = (BigInteger.valueOf(bodypartId).shiftRight(16).longValue() & 0x1L) == 0x1L;
        return (bodypartId >> 17) + (isPlayer ? 0 : 1);
    }
    
    public static final int getBodyPlaceForBodyPart(final long bodypartId) {
        return (int)(bodypartId >> 8 & 0xFFL);
    }
    
    public static final long getNextCreatureId() {
        ++WurmId.creatureIdCounter;
        ++WurmId.savecounter;
        checkSave();
        return BigInteger.valueOf(WurmId.creatureIdCounter).shiftLeft(24).longValue() + (Servers.localServer.id << 8) + 1L;
    }
    
    public static final long getNextStructureId() {
        ++WurmId.structureIdCounter;
        ++WurmId.savecounter;
        checkSave();
        return BigInteger.valueOf(WurmId.structureIdCounter).shiftLeft(24).longValue() + (Servers.localServer.id << 8) + 4L;
    }
    
    public static final long getNextTempItemId() {
        ++WurmId.tempIdCounter;
        return BigInteger.valueOf(WurmId.tempIdCounter).shiftLeft(24).longValue() + (Servers.localServer.id << 8) + 6L;
    }
    
    public static final long getNextIllusionId() {
        ++WurmId.illusionIdCounter;
        return BigInteger.valueOf(WurmId.illusionIdCounter).shiftLeft(24).longValue() + 24L;
    }
    
    public static final long getNextTemporaryWoundId() {
        ++WurmId.temporaryWoundIdCounter;
        return BigInteger.valueOf(WurmId.temporaryWoundIdCounter).shiftLeft(24).longValue() + (Servers.localServer.id << 8) + 32L;
    }
    
    public static final long getNextWoundId() {
        ++WurmId.woundIdCounter;
        ++WurmId.savecounter;
        checkSave();
        return BigInteger.valueOf(WurmId.woundIdCounter).shiftLeft(24).longValue() + (Servers.localServer.id << 8) + 8L;
    }
    
    public static final long getNextTemporarySkillId() {
        ++WurmId.temporarySkillsIdCounter;
        return BigInteger.valueOf(WurmId.temporarySkillsIdCounter).shiftLeft(24).longValue() + (Servers.localServer.id << 8) + 31L;
    }
    
    public static final long getNextPlayerSkillId() {
        ++WurmId.playerSkillsIdCounter;
        ++WurmId.savecounter;
        checkSave();
        return BigInteger.valueOf(WurmId.playerSkillsIdCounter).shiftLeft(24).longValue() + (Servers.localServer.id << 8) + 10L;
    }
    
    public static final long getNextCreatureSkillId() {
        ++WurmId.creatureSkillsIdCounter;
        ++WurmId.savecounter;
        checkSave();
        return BigInteger.valueOf(WurmId.creatureSkillsIdCounter).shiftLeft(24).longValue() + (Servers.localServer.id << 8) + 9L;
    }
    
    public static final long getNextBankId() {
        ++WurmId.bankIdCounter;
        ++WurmId.savecounter;
        checkSave();
        return BigInteger.valueOf(WurmId.bankIdCounter).shiftLeft(24).longValue() + (Servers.localServer.id << 8) + 13L;
    }
    
    public static final long getNextSpellId() {
        ++WurmId.spellIdCounter;
        ++WurmId.savecounter;
        checkSave();
        return BigInteger.valueOf(WurmId.spellIdCounter).shiftLeft(24).longValue() + (Servers.localServer.id << 8) + 15L;
    }
    
    public static final long getNextWCCommandId() {
        ++WurmId.wccommandCounter;
        ++WurmId.savecounter;
        checkSave();
        return BigInteger.valueOf(WurmId.wccommandCounter).shiftLeft(24).longValue() + (Servers.localServer.id << 8) + 21L;
    }
    
    public static final long getNextPlanId() {
        ++WurmId.planIdCounter;
        ++WurmId.savecounter;
        checkSave();
        return BigInteger.valueOf(WurmId.planIdCounter).shiftLeft(24).longValue() + (Servers.localServer.id << 8) + 16L;
    }
    
    public static final long getNextBodyId() {
        ++WurmId.bodyIdCounter;
        ++WurmId.savecounter;
        checkSave();
        return BigInteger.valueOf(WurmId.bodyIdCounter).shiftLeft(24).longValue() + (Servers.localServer.id << 8) + 19L;
    }
    
    public static final long getNextCoinId() {
        ++WurmId.coinIdCounter;
        ++WurmId.savecounter;
        checkSave();
        return BigInteger.valueOf(WurmId.coinIdCounter).shiftLeft(24).longValue() + (Servers.localServer.id << 8) + 20L;
    }
    
    public static final long getNextPoiId() {
        ++WurmId.poiIdCounter;
        ++WurmId.savecounter;
        checkSave();
        return BigInteger.valueOf(WurmId.poiIdCounter).shiftLeft(24).longValue() + (Servers.localServer.id << 8) + 26L;
    }
    
    public static final long getNextCouponId() {
        ++WurmId.couponIdCounter;
        ++WurmId.savecounter;
        checkSave();
        return BigInteger.valueOf(WurmId.couponIdCounter).shiftLeft(24).longValue() + (Servers.localServer.id << 8) + 29L;
    }
    
    private static final void loadIdNumbers() {
        final long start = System.nanoTime();
        Connection dbcon = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            dbcon = DbConnector.getLoginDbCon();
            ps = dbcon.prepareStatement("SELECT * FROM IDS WHERE SERVER=?");
            ps.setInt(1, Servers.localServer.id);
            rs = ps.executeQuery();
            if (rs.next()) {
                WurmId.logger.log(Level.INFO, "Loading ids.");
                WurmId.playerIdCounter = rs.getLong("PLAYERIDS");
                WurmId.woundIdCounter = rs.getLong("WOUNDIDS");
                WurmId.playerSkillsIdCounter = rs.getLong("PLAYERSKILLIDS");
                WurmId.creatureSkillsIdCounter = rs.getLong("CREATURESKILLIDS");
                WurmId.creatureIdCounter = rs.getLong("CREATUREIDS");
                WurmId.structureIdCounter = rs.getLong("STRUCTUREIDS");
                WurmId.itemIdCounter = rs.getLong("ITEMIDS");
                WurmId.bankIdCounter = rs.getLong("BANKIDS");
                WurmId.spellIdCounter = rs.getLong("SPELLIDS");
                WurmId.wccommandCounter = rs.getLong("WCCOMMANDS");
                WurmId.planIdCounter = rs.getLong("PLANIDS");
                WurmId.bodyIdCounter = rs.getLong("BODYIDS");
                WurmId.coinIdCounter = rs.getLong("COINIDS");
                WurmId.poiIdCounter = rs.getLong("POIIDS");
                WurmId.couponIdCounter = rs.getLong("REDEEMIDS");
            }
            rs.close();
            ps.close();
            if (WurmId.itemIdCounter == 0L) {
                loadIdNumbers(true);
            }
            else {
                WurmId.itemIdCounter += 3000L;
                WurmId.playerIdCounter += 3000L;
                WurmId.woundIdCounter += 3000L;
                WurmId.playerSkillsIdCounter += 3000L;
                WurmId.creatureSkillsIdCounter += 3000L;
                WurmId.creatureIdCounter += 3000L;
                WurmId.structureIdCounter += 3000L;
                WurmId.itemIdCounter += 3000L;
                WurmId.bankIdCounter += 3000L;
                WurmId.spellIdCounter += 3000L;
                WurmId.wccommandCounter += 3000L;
                WurmId.planIdCounter += 3000L;
                WurmId.bodyIdCounter += 3000L;
                WurmId.coinIdCounter += 3000L;
                WurmId.poiIdCounter += 1000L;
                WurmId.couponIdCounter += 100L;
                updateNumbers();
                WurmId.logger.log(Level.INFO, "Added to ids, creatrureIdcounter is now " + WurmId.creatureIdCounter);
            }
        }
        catch (SQLException sqx) {
            WurmId.logger.log(Level.WARNING, "Failed to load max playerid: " + sqx.getMessage(), sqx);
        }
        finally {
            DbUtilities.closeDatabaseObjects(ps, rs);
            DbConnector.returnConnection(dbcon);
        }
        WurmId.logger.info("Finished loading Wurm IDs, that took " + (System.nanoTime() - start) / 1000000.0f + " millis.");
    }
    
    public static final void checkSave() {
        if (WurmId.savecounter >= 1000) {
            updateNumbers();
            WurmId.savecounter = 0;
        }
    }
    
    public static final void loadIdNumbers(final boolean create) {
        WurmId.logger.log(Level.WARNING, "LOADING WURMIDS 'MANUALLY'. This should only happen at convert or on a new server.");
        Connection dbcon = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            dbcon = DbConnector.getPlayerDbCon();
            ps = dbcon.prepareStatement("SELECT MAX(WURMID) FROM PLAYERS");
            rs = ps.executeQuery();
            if (rs.next()) {
                WurmId.playerIdCounter = rs.getLong("MAX(WURMID)") >> 24;
            }
            rs.close();
            ps.close();
        }
        catch (SQLException sqx) {
            WurmId.logger.log(Level.WARNING, "Failed to load max playerid: " + sqx.getMessage(), sqx);
        }
        finally {
            DbUtilities.closeDatabaseObjects(ps, rs);
            DbConnector.returnConnection(dbcon);
        }
        try {
            dbcon = DbConnector.getPlayerDbCon();
            ps = dbcon.prepareStatement("SELECT MAX(ID) FROM WOUNDS");
            rs = ps.executeQuery();
            if (rs.next()) {
                WurmId.woundIdCounter = rs.getLong("MAX(ID)") >> 24;
            }
            rs.close();
            ps.close();
        }
        catch (SQLException sqx) {
            WurmId.logger.log(Level.WARNING, "Failed to load max woundid: " + sqx.getMessage(), sqx);
        }
        finally {
            DbUtilities.closeDatabaseObjects(ps, rs);
            DbConnector.returnConnection(dbcon);
        }
        try {
            dbcon = DbConnector.getPlayerDbCon();
            ps = dbcon.prepareStatement("SELECT MAX(ID) FROM SKILLS");
            rs = ps.executeQuery();
            if (rs.next()) {
                WurmId.playerSkillsIdCounter = rs.getLong("MAX(ID)") >> 24;
            }
            rs.close();
            ps.close();
        }
        catch (SQLException sqx) {
            WurmId.logger.log(Level.WARNING, "Failed to load max player skill id: " + sqx.getMessage(), sqx);
        }
        finally {
            DbUtilities.closeDatabaseObjects(ps, rs);
            DbConnector.returnConnection(dbcon);
        }
        try {
            dbcon = DbConnector.getCreatureDbCon();
            ps = dbcon.prepareStatement("SELECT MAX(ID) FROM SKILLS");
            rs = ps.executeQuery();
            if (rs.next()) {
                WurmId.creatureSkillsIdCounter = rs.getLong("MAX(ID)") >> 24;
            }
            rs.close();
            ps.close();
        }
        catch (SQLException sqx) {
            WurmId.logger.log(Level.WARNING, "Failed to load max creature skill id: " + sqx.getMessage(), sqx);
        }
        finally {
            DbUtilities.closeDatabaseObjects(ps, rs);
            DbConnector.returnConnection(dbcon);
        }
        try {
            dbcon = DbConnector.getTemplateDbCon();
            ps = dbcon.prepareStatement("SELECT MAX(ID) FROM SKILLS");
            rs = ps.executeQuery();
            if (rs.next()) {
                WurmId.templateSkillsIdCounter = rs.getLong("MAX(ID)") >> 24;
            }
            rs.close();
            ps.close();
        }
        catch (SQLException sqx) {
            WurmId.logger.log(Level.WARNING, "Failed to load max templateid: " + sqx.getMessage(), sqx);
        }
        finally {
            DbUtilities.closeDatabaseObjects(ps, rs);
            DbConnector.returnConnection(dbcon);
        }
        try {
            dbcon = DbConnector.getCreatureDbCon();
            ps = dbcon.prepareStatement("SELECT MAX(WURMID) FROM CREATURES");
            rs = ps.executeQuery();
            if (rs.next()) {
                WurmId.creatureIdCounter = rs.getLong("MAX(WURMID)") >> 24;
            }
            WurmId.logger.log(Level.WARNING, "Max creatureid: " + WurmId.creatureIdCounter + " when loading manually");
            rs.close();
            ps.close();
        }
        catch (SQLException sqx) {
            WurmId.logger.log(Level.WARNING, "Failed to load max creatureid: " + sqx.getMessage(), sqx);
        }
        finally {
            DbUtilities.closeDatabaseObjects(ps, rs);
            DbConnector.returnConnection(dbcon);
        }
        try {
            dbcon = DbConnector.getZonesDbCon();
            ps = dbcon.prepareStatement("SELECT MAX(WURMID) FROM STRUCTURES");
            rs = ps.executeQuery();
            if (rs.next()) {
                WurmId.structureIdCounter = rs.getLong("MAX(WURMID)") >> 24;
            }
            rs.close();
            ps.close();
        }
        catch (SQLException sqx) {
            WurmId.logger.log(Level.WARNING, "Failed to load max structureid: " + sqx.getMessage(), sqx);
        }
        finally {
            DbUtilities.closeDatabaseObjects(ps, rs);
            DbConnector.returnConnection(dbcon);
        }
        try {
            dbcon = DbConnector.getItemDbCon();
            ps = dbcon.prepareStatement("SELECT MAX(WURMID) FROM ITEMS");
            rs = ps.executeQuery();
            if (rs.next()) {
                WurmId.itemIdCounter = rs.getLong("MAX(WURMID)") >> 24;
            }
            rs.close();
            ps.close();
        }
        catch (SQLException sqx) {
            WurmId.logger.log(Level.WARNING, "Failed to load max itemid: " + sqx.getMessage(), sqx);
        }
        finally {
            DbUtilities.closeDatabaseObjects(ps, rs);
            DbConnector.returnConnection(dbcon);
        }
        try {
            dbcon = DbConnector.getItemDbCon();
            ps = dbcon.prepareStatement("SELECT MAX(WURMID) FROM BODYPARTS");
            rs = ps.executeQuery();
            if (rs.next()) {
                WurmId.bodyIdCounter = rs.getLong("MAX(WURMID)") >> 24;
            }
            rs.close();
            ps.close();
        }
        catch (SQLException sqx) {
            WurmId.logger.log(Level.WARNING, "Failed to load max body id: " + sqx.getMessage(), sqx);
        }
        finally {
            DbUtilities.closeDatabaseObjects(ps, rs);
            DbConnector.returnConnection(dbcon);
        }
        try {
            dbcon = DbConnector.getItemDbCon();
            ps = dbcon.prepareStatement("SELECT MAX(WURMID) FROM COINS");
            rs = ps.executeQuery();
            if (rs.next()) {
                WurmId.coinIdCounter = rs.getLong("MAX(WURMID)") >> 24;
            }
            rs.close();
            ps.close();
        }
        catch (SQLException sqx) {
            WurmId.logger.log(Level.WARNING, "Failed to load max coin id: " + sqx.getMessage(), sqx);
        }
        finally {
            DbUtilities.closeDatabaseObjects(ps, rs);
            DbConnector.returnConnection(dbcon);
        }
        try {
            dbcon = DbConnector.getPlayerDbCon();
            ps = dbcon.prepareStatement("SELECT MAX(ID) FROM MAP_ANNOTATIONS");
            rs = ps.executeQuery();
            if (rs.next()) {
                WurmId.poiIdCounter = rs.getLong("MAX(ID)") >> 24;
            }
            rs.close();
            ps.close();
        }
        catch (SQLException sqx) {
            WurmId.logger.log(Level.WARNING, "Failed to load max poi id: " + sqx.getMessage(), sqx);
        }
        finally {
            DbUtilities.closeDatabaseObjects(ps, rs);
            DbConnector.returnConnection(dbcon);
        }
        try {
            dbcon = DbConnector.getEconomyDbCon();
            ps = dbcon.prepareStatement("SELECT MAX(WURMID) FROM BANKS");
            rs = ps.executeQuery();
            if (rs.next()) {
                WurmId.bankIdCounter = rs.getLong("MAX(WURMID)") >> 24;
            }
            rs.close();
            ps.close();
        }
        catch (SQLException sqx) {
            WurmId.logger.log(Level.WARNING, "Failed to load max bank id: " + sqx.getMessage(), sqx);
        }
        finally {
            DbUtilities.closeDatabaseObjects(ps, rs);
            DbConnector.returnConnection(dbcon);
        }
        try {
            dbcon = DbConnector.getPlayerDbCon();
            ps = dbcon.prepareStatement("SELECT MAX(WURMID) FROM SPELLEFFECTS");
            rs = ps.executeQuery();
            if (rs.next()) {
                WurmId.spellIdCounter = rs.getLong("MAX(WURMID)") >> 24;
            }
            rs.close();
            ps.close();
        }
        catch (SQLException sqx) {
            WurmId.logger.log(Level.WARNING, "Failed to load max spell id: " + sqx.getMessage(), sqx);
        }
        finally {
            DbUtilities.closeDatabaseObjects(ps, rs);
            DbConnector.returnConnection(dbcon);
        }
        if (create) {
            saveNumbers();
        }
        WurmId.logger.info("Loaded id numbers from database, playerids:" + WurmId.playerIdCounter + ", creatureids:" + WurmId.creatureIdCounter + ", itemids:" + WurmId.itemIdCounter + ", structureIds:" + WurmId.structureIdCounter + ", woundids:" + WurmId.woundIdCounter + ", playerSkillIds: " + WurmId.playerSkillsIdCounter + ", creatureSkillIds: " + WurmId.creatureSkillsIdCounter + ", templateSkillIds: " + WurmId.templateSkillsIdCounter + ", bankIds: " + WurmId.bankIdCounter + ", spellIds: " + WurmId.spellIdCounter + ", planIds: " + WurmId.planIdCounter + ", bodyIds: " + WurmId.bodyIdCounter + ", coinIds: " + WurmId.coinIdCounter + ", wccommandCounter: " + WurmId.wccommandCounter + ", poiIdCounter: " + WurmId.poiIdCounter);
    }
    
    public static final void updateNumbers() {
        final long start = System.nanoTime();
        Connection dbcon = null;
        PreparedStatement ps = null;
        try {
            dbcon = DbConnector.getLoginDbCon();
            ps = dbcon.prepareStatement("UPDATE IDS SET PLAYERIDS=?,CREATUREIDS=?,ITEMIDS=?,STRUCTUREIDS=?,WOUNDIDS=?,PLAYERSKILLIDS=?,CREATURESKILLIDS=?,BANKIDS=?,SPELLIDS=?,PLANIDS=?,BODYIDS=?,COINIDS=?,WCCOMMANDS=?, POIIDS=?, REDEEMIDS=? WHERE SERVER=?");
            ps.setLong(1, WurmId.playerIdCounter);
            ps.setLong(2, WurmId.creatureIdCounter);
            ps.setLong(3, WurmId.itemIdCounter);
            ps.setLong(4, WurmId.structureIdCounter);
            ps.setLong(5, WurmId.woundIdCounter);
            ps.setLong(6, WurmId.playerSkillsIdCounter);
            ps.setLong(7, WurmId.creatureSkillsIdCounter);
            ps.setLong(8, WurmId.bankIdCounter);
            ps.setLong(9, WurmId.spellIdCounter);
            ps.setLong(10, WurmId.planIdCounter);
            ps.setLong(11, WurmId.bodyIdCounter);
            ps.setLong(12, WurmId.coinIdCounter);
            ps.setLong(13, WurmId.wccommandCounter);
            ps.setLong(14, WurmId.poiIdCounter);
            ps.setLong(15, WurmId.couponIdCounter);
            ps.setInt(16, Servers.localServer.id);
            ps.executeUpdate();
            ps.close();
        }
        catch (SQLException sqex) {
            WurmId.logger.log(Level.WARNING, "Failed to update idnums into logindb! " + sqex.getMessage(), sqex);
        }
        finally {
            DbUtilities.closeDatabaseObjects(ps, null);
            DbConnector.returnConnection(dbcon);
            if (WurmId.logger.isLoggable(Level.FINE)) {
                WurmId.logger.fine("Finished updating Wurm IDs, that took " + (System.nanoTime() - start) / 1000000.0f + " millis.");
                WurmId.logger.fine("Saved id numbers to database, playerids:" + WurmId.playerIdCounter + ", creatureids:" + WurmId.creatureIdCounter + ", itemids:" + WurmId.itemIdCounter + ", structureIds:" + WurmId.structureIdCounter + ", woundids:" + WurmId.woundIdCounter + ", playerSkillIds: " + WurmId.playerSkillsIdCounter + ", creatureSkillIds: " + WurmId.creatureSkillsIdCounter + ", bankIds: " + WurmId.bankIdCounter + ", spellIds: " + WurmId.spellIdCounter + ", planIds: " + WurmId.planIdCounter + ", bodyIds: " + WurmId.bodyIdCounter + ", coinIds: " + WurmId.coinIdCounter + ", wccommandCounter: " + WurmId.wccommandCounter + ", poiIdCounter: " + WurmId.poiIdCounter);
            }
        }
    }
    
    private static final void saveNumbers() {
        final long start = System.nanoTime();
        PreparedStatement ps = null;
        Connection dbcon = null;
        try {
            dbcon = DbConnector.getLoginDbCon();
            ps = dbcon.prepareStatement("INSERT INTO IDS (SERVER,PLAYERIDS,CREATUREIDS,ITEMIDS,STRUCTUREIDS,WOUNDIDS,PLAYERSKILLIDS,CREATURESKILLIDS,BANKIDS,SPELLIDS,PLANIDS,BODYIDS,COINIDS,WCCOMMANDS, POIIDS, REDEEMIDS) VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
            ps.setInt(1, Servers.localServer.id);
            ps.setLong(2, WurmId.playerIdCounter);
            ps.setLong(3, WurmId.creatureIdCounter);
            ps.setLong(4, WurmId.itemIdCounter);
            ps.setLong(5, WurmId.structureIdCounter);
            ps.setLong(6, WurmId.woundIdCounter);
            ps.setLong(7, WurmId.playerSkillsIdCounter);
            ps.setLong(8, WurmId.creatureSkillsIdCounter);
            ps.setLong(9, WurmId.bankIdCounter);
            ps.setLong(10, WurmId.spellIdCounter);
            ps.setLong(11, WurmId.planIdCounter);
            ps.setLong(12, WurmId.bodyIdCounter);
            ps.setLong(13, WurmId.coinIdCounter);
            ps.setLong(14, WurmId.wccommandCounter);
            ps.setLong(15, WurmId.poiIdCounter);
            ps.setLong(16, WurmId.couponIdCounter);
            ps.executeUpdate();
            ps.close();
        }
        catch (SQLException sqex) {
            WurmId.logger.log(Level.WARNING, "Failed to insert idnums into logindb! Trying update instead." + sqex.getMessage(), sqex);
            updateNumbers();
        }
        finally {
            DbUtilities.closeDatabaseObjects(ps, null);
            DbConnector.returnConnection(dbcon);
            WurmId.logger.info("Finished saving Wurm IDs, that took " + (System.nanoTime() - start) / 1000000.0f + " millis.");
        }
    }
    
    static {
        WurmId.playerIdCounter = 0L;
        WurmId.creatureIdCounter = 0L;
        WurmId.itemIdCounter = 0L;
        WurmId.structureIdCounter = 0L;
        WurmId.tempIdCounter = 0L;
        WurmId.illusionIdCounter = 0L;
        WurmId.woundIdCounter = 0L;
        WurmId.temporaryWoundIdCounter = 0L;
        WurmId.spellIdCounter = 0L;
        WurmId.creatureSkillsIdCounter = 0L;
        WurmId.templateSkillsIdCounter = 0L;
        WurmId.playerSkillsIdCounter = 0L;
        WurmId.temporarySkillsIdCounter = 0L;
        WurmId.planIdCounter = 0L;
        WurmId.bankIdCounter = 0L;
        WurmId.bodyIdCounter = 0L;
        WurmId.coinIdCounter = 0L;
        WurmId.poiIdCounter = 0L;
        WurmId.couponIdCounter = 0L;
        WurmId.wccommandCounter = 0L;
        WurmId.savecounter = 0;
        logger = Logger.getLogger(WurmId.class.getName());
        loadIdNumbers();
    }
}
