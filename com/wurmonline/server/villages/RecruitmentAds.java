// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.villages;

import java.util.Set;
import java.util.Collection;
import java.util.HashSet;
import com.wurmonline.server.creatures.Creature;
import com.wurmonline.server.players.Player;
import java.sql.PreparedStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import com.wurmonline.server.utils.DbUtilities;
import java.sql.SQLException;
import java.io.IOException;
import java.util.logging.Level;
import com.wurmonline.server.DbConnector;
import java.sql.Date;
import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;
import java.util.logging.Logger;

public final class RecruitmentAds
{
    private static final Logger logger;
    private static final Map<Integer, Map<Integer, RecruitmentAd>> recruitmentAds;
    private static final String loadAllAds = "SELECT * FROM VILLAGERECRUITMENT";
    private static final String deleteAd = "DELETE FROM VILLAGERECRUITMENT WHERE VILLAGE=?";
    private static final String createNewAdd = "INSERT INTO VILLAGERECRUITMENT (VILLAGE, DESCRIPTION, CONTACT, CREATED, KINGDOM) VALUES ( ?, ?, ?, ?, ?);";
    private static final String updateAd = "UPDATE VILLAGERECRUITMENT SET DESCRIPTION =?, CONTACT =?, CREATED =? WHERE VILLAGE=?;";
    
    public static void add(final RecruitmentAd ad) {
        Map<Integer, RecruitmentAd> ads = RecruitmentAds.recruitmentAds.get(ad.getKingdom());
        if (ads == null) {
            ads = new ConcurrentHashMap<Integer, RecruitmentAd>();
            RecruitmentAds.recruitmentAds.put(ad.getKingdom(), ads);
        }
        ads.put(ad.getVillageId(), ad);
    }
    
    public static final boolean containsAdForVillage(final int villageId) {
        final Integer key = villageId;
        boolean exists = false;
        for (final Map<Integer, RecruitmentAd> ads : RecruitmentAds.recruitmentAds.values()) {
            exists = ads.containsKey(key);
            if (exists) {
                break;
            }
        }
        return exists;
    }
    
    public static final RecruitmentAd create(final int villageId, final String description, final long contactId, final int kingdom) throws IOException {
        if (containsAdForVillage(villageId)) {
            return null;
        }
        Connection dbcon = null;
        PreparedStatement ps = null;
        try {
            final Date created = new Date(System.currentTimeMillis());
            dbcon = DbConnector.getZonesDbCon();
            ps = dbcon.prepareStatement("INSERT INTO VILLAGERECRUITMENT (VILLAGE, DESCRIPTION, CONTACT, CREATED, KINGDOM) VALUES ( ?, ?, ?, ?, ?);");
            ps.setInt(1, villageId);
            ps.setString(2, description);
            ps.setLong(3, contactId);
            ps.setDate(4, created);
            ps.setInt(5, kingdom);
            ps.executeUpdate();
            final RecruitmentAd ad = new RecruitmentAd(villageId, contactId, description, created, kingdom);
            add(ad);
            return ad;
        }
        catch (SQLException sqx) {
            RecruitmentAds.logger.log(Level.WARNING, "Failed to create new recruitment ad for village: " + villageId + ": " + sqx.getMessage(), sqx);
            throw new IOException(sqx);
        }
        finally {
            DbUtilities.closeDatabaseObjects(ps, null);
            DbConnector.returnConnection(dbcon);
        }
    }
    
    private static void deleteAd(final RecruitmentAd ad) {
        Connection dbcon = null;
        PreparedStatement ps = null;
        try {
            dbcon = DbConnector.getZonesDbCon();
            ps = dbcon.prepareStatement("DELETE FROM VILLAGERECRUITMENT WHERE VILLAGE=?");
            ps.setInt(1, ad.getVillageId());
            ps.executeUpdate();
        }
        catch (SQLException sqex) {
            RecruitmentAds.logger.log(Level.WARNING, "Failed to delete recruitment ad due to " + sqex.getMessage(), sqex);
        }
        finally {
            DbUtilities.closeDatabaseObjects(ps, null);
            DbConnector.returnConnection(dbcon);
        }
    }
    
    public static final void deleteVillageAd(final Player player) {
        final Village village = Villages.getVillageForCreature(player);
        if (village == null) {
            player.getCommunicator().sendNormalServerMessage("You are not part of a village and can not delete any recruitment ads.");
            return;
        }
        final RecruitmentAd ad = getVillageAd(village.getId(), player.getKingdomId());
        remove(ad);
    }
    
    public static final void deleteVillageAd(final Village village) {
        final RecruitmentAd ad = getVillageAd(village, village.kingdom);
        if (ad != null) {
            remove(ad);
        }
    }
    
    public static final RecruitmentAd[] getAllRecruitmentAds() {
        final Set<RecruitmentAd> adSet = new HashSet<RecruitmentAd>();
        for (final Map<Integer, RecruitmentAd> ads : RecruitmentAds.recruitmentAds.values()) {
            adSet.addAll(ads.values());
        }
        final RecruitmentAd[] adsArray = new RecruitmentAd[adSet.size()];
        adSet.toArray(adsArray);
        return adsArray;
    }
    
    public static final RecruitmentAd[] getKingdomAds(final int kingdom) {
        final Map<Integer, RecruitmentAd> ads = RecruitmentAds.recruitmentAds.get(kingdom);
        if (ads == null) {
            return null;
        }
        final RecruitmentAd[] rad = new RecruitmentAd[ads.size()];
        return ads.values().toArray(rad);
    }
    
    public static final RecruitmentAd getVillageAd(final int villageId, final int kingdom) {
        final Map<Integer, RecruitmentAd> ads = RecruitmentAds.recruitmentAds.get(kingdom);
        if (ads == null) {
            return null;
        }
        return ads.get(villageId);
    }
    
    public static final RecruitmentAd getVillageAd(final Village village, final int kingdom) {
        return getVillageAd(village.getId(), kingdom);
    }
    
    public static void loadRecruitmentAds() throws IOException {
        final long start = System.nanoTime();
        int loadedAds = 0;
        Connection dbcon = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            dbcon = DbConnector.getZonesDbCon();
            ps = dbcon.prepareStatement("SELECT * FROM VILLAGERECRUITMENT");
            rs = ps.executeQuery();
            while (rs.next()) {
                final RecruitmentAd ad = new RecruitmentAd(rs.getInt("VILLAGE"), rs.getLong("CONTACT"), rs.getString("DESCRIPTION"), rs.getDate("CREATED"), rs.getInt("KINGDOM"));
                add(ad);
                ++loadedAds;
            }
        }
        catch (SQLException sqex) {
            RecruitmentAds.logger.log(Level.WARNING, "Failed to load recruitment ads due to " + sqex.getMessage(), sqex);
            throw new IOException("Failed to load recruitment ads", sqex);
        }
        finally {
            DbUtilities.closeDatabaseObjects(ps, rs);
            DbConnector.returnConnection(dbcon);
            final long end = System.nanoTime();
            RecruitmentAds.logger.info("Loaded " + loadedAds + " ads from the database took " + (end - start) / 1000000.0f + " ms");
        }
    }
    
    public static void poll() {
        final long now = System.currentTimeMillis();
        final Date nowDate = new Date(now);
        final long divider = 86400000L;
        for (final Map<Integer, RecruitmentAd> kList : RecruitmentAds.recruitmentAds.values()) {
            for (final RecruitmentAd ad : kList.values()) {
                final long daysAfterCreation = (nowDate.getTime() - ad.getCreated().getTime()) / 86400000L;
                if (daysAfterCreation > 60L) {
                    remove(ad);
                }
            }
        }
    }
    
    public static void remove(final RecruitmentAd ad) {
        final Integer kingKey = ad.getKingdom();
        final Integer villKey = ad.getVillageId();
        if (!RecruitmentAds.recruitmentAds.containsKey(kingKey)) {
            return;
        }
        final Map<Integer, RecruitmentAd> kList = RecruitmentAds.recruitmentAds.get(kingKey);
        if (kList.containsKey(villKey)) {
            kList.remove(villKey);
            deleteAd(ad);
        }
    }
    
    public static final void update(final int villageId, final String description, final long contact, final Date updated, final byte kingdomId) throws IOException {
        Connection dbcon = null;
        PreparedStatement ps = null;
        try {
            dbcon = DbConnector.getZonesDbCon();
            ps = dbcon.prepareStatement("UPDATE VILLAGERECRUITMENT SET DESCRIPTION =?, CONTACT =?, CREATED =? WHERE VILLAGE=?;");
            ps.setString(1, description);
            ps.setLong(2, contact);
            ps.setDate(3, updated);
            ps.setInt(4, villageId);
            ps.executeUpdate();
            final RecruitmentAd ad = getVillageAd(villageId, kingdomId);
            ad.setDescription(description);
            ad.setCreated(updated);
            ad.setContactId(contact);
        }
        catch (SQLException sqx) {
            RecruitmentAds.logger.log(Level.WARNING, "Failed to create new recruitment ad for village: " + villageId + ": " + sqx.getMessage(), sqx);
            throw new IOException(sqx);
        }
        finally {
            DbUtilities.closeDatabaseObjects(ps, null);
            DbConnector.returnConnection(dbcon);
        }
    }
    
    static {
        logger = Logger.getLogger(RecruitmentAds.class.getName());
        recruitmentAds = new ConcurrentHashMap<Integer, Map<Integer, RecruitmentAd>>();
    }
}
