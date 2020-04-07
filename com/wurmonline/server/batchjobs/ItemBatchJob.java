// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.batchjobs;

import com.wurmonline.server.items.ItemTemplate;
import com.wurmonline.shared.exceptions.WurmServerException;
import com.wurmonline.server.items.ItemTemplateFactory;
import com.wurmonline.server.items.CoinDbStrings;
import com.wurmonline.server.items.BodyDbStrings;
import com.wurmonline.server.items.DbStrings;
import com.wurmonline.server.items.ItemDbStrings;
import java.sql.PreparedStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import com.wurmonline.server.utils.DbUtilities;
import java.sql.SQLException;
import com.wurmonline.server.DbConnector;
import java.util.logging.Level;
import java.util.logging.Logger;
import com.wurmonline.shared.constants.ItemMaterials;
import com.wurmonline.server.MiscConstants;
import com.wurmonline.server.items.ItemTypes;

public final class ItemBatchJob implements ItemTypes, MiscConstants, ItemMaterials
{
    private static final String deleteLegs = "DELETE FROM BODYITEMS WHERE TEMPLATEID=10";
    private static final String deleteFeet = "DELETE FROM BODYITEMS WHERE TEMPLATEID=15";
    private static final String deleteLeg = "DELETE FROM BODYITEMS WHERE TEMPLATEID=19";
    private static Logger logger;
    
    public static void fixStructureGuests() {
        ItemBatchJob.logger.log(Level.INFO, "Fixing structure guests.");
        Connection dbcon = null;
        PreparedStatement ps = null;
        PreparedStatement ps2 = null;
        ResultSet rs = null;
        try {
            dbcon = DbConnector.getZonesDbCon();
            final String getAll = "select WURMID,GUESTS from STRUCTURES";
            ps = dbcon.prepareStatement("select WURMID,GUESTS from STRUCTURES");
            rs = ps.executeQuery();
            while (rs.next()) {
                final long wurmid = rs.getLong("WURMID");
                final long[] array;
                final long[] guestArr = array = (long[])rs.getObject("GUESTS");
                for (final long lGuest : array) {
                    ps2 = dbcon.prepareStatement("INSERT INTO STRUCTUREGUESTS (STRUCTUREID,GUESTID)VALUES(?,?)");
                    ps2.setLong(1, wurmid);
                    ps2.setLong(2, lGuest);
                    ps2.executeUpdate();
                    ps2.close();
                }
            }
        }
        catch (SQLException ex) {
            ItemBatchJob.logger.log(Level.WARNING, "Failed to move structure guests.", ex);
        }
        finally {
            DbUtilities.closeDatabaseObjects(ps, rs);
            DbUtilities.closeDatabaseObjects(ps2, null);
            DbConnector.returnConnection(dbcon);
        }
    }
    
    public static void fixZonesStructure() {
        ItemBatchJob.logger.log(Level.INFO, "Fixing zone structures.");
        Connection dbcon = null;
        PreparedStatement ps = null;
        PreparedStatement ps2 = null;
        ResultSet rs = null;
        try {
            dbcon = DbConnector.getZonesDbCon();
            final String getAll = "select ZONEID, STRUCTURES FROM ZONES";
            ps = dbcon.prepareStatement("select ZONEID, STRUCTURES FROM ZONES");
            rs = ps.executeQuery();
            while (rs.next()) {
                final int zoneid = rs.getInt("ZONEID");
                final long[] array;
                final long[] structArr = array = (long[])rs.getObject("STRUCTURES");
                for (final long lStructure : array) {
                    ps2 = dbcon.prepareStatement("INSERT INTO ZONESTRUCTURES (ZONEID,STRUCTUREID)VALUES(?,?)");
                    ps2.setInt(1, zoneid);
                    ps2.setLong(2, lStructure);
                    ps2.executeUpdate();
                    ps2.close();
                }
            }
        }
        catch (SQLException ex) {
            ItemBatchJob.logger.log(Level.WARNING, "Failed to move zone structure.", ex);
        }
        finally {
            DbUtilities.closeDatabaseObjects(ps, rs);
            DbUtilities.closeDatabaseObjects(ps2, null);
            DbConnector.returnConnection(dbcon);
        }
    }
    
    public static void setNames() {
        Connection dbcon = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            dbcon = DbConnector.getItemDbCon();
            final String getAll = "select * from ITEMS";
            ps = dbcon.prepareStatement("select * from ITEMS");
            rs = ps.executeQuery();
            while (rs.next()) {
                final long wurmid = rs.getLong("WURMID");
                final String description = rs.getString("NAME");
                setDescription(dbcon, wurmid, description);
            }
        }
        catch (SQLException ex) {
            ItemBatchJob.logger.log(Level.WARNING, "Failed to check if item exists.", ex);
        }
        finally {
            DbUtilities.closeDatabaseObjects(ps, rs);
            DbConnector.returnConnection(dbcon);
        }
    }
    
    public static void setDescription(final Connection dbcon, final long wurmId, final String desc) {
        PreparedStatement ps = null;
        try {
            final String setAll = "update ITEMS set DESCRIPTION=?, NAME=\"\" where WURMID=?";
            ps = dbcon.prepareStatement("update ITEMS set DESCRIPTION=?, NAME=\"\" where WURMID=?");
            ps.setString(1, desc);
            ps.setLong(2, wurmId);
            ps.executeUpdate();
            ps.close();
        }
        catch (SQLException ex) {
            ItemBatchJob.logger.log(Level.WARNING, "Failed to save item " + wurmId, ex);
        }
        finally {
            DbUtilities.closeDatabaseObjects(ps, null);
        }
    }
    
    public static final void deleteFeet() {
        Connection dbcon = null;
        PreparedStatement ps = null;
        try {
            dbcon = DbConnector.getItemDbCon();
            ps = dbcon.prepareStatement("DELETE FROM BODYITEMS WHERE TEMPLATEID=10");
            ps.executeUpdate();
            ps.close();
            ps = dbcon.prepareStatement("DELETE FROM BODYITEMS WHERE TEMPLATEID=19");
            ps.executeUpdate();
            ps.close();
            ps = dbcon.prepareStatement("DELETE FROM BODYITEMS WHERE TEMPLATEID=15");
            ps.executeUpdate();
            ps.close();
        }
        catch (SQLException sqx) {
            ItemBatchJob.logger.log(Level.WARNING, sqx.getMessage(), sqx);
        }
        finally {
            DbUtilities.closeDatabaseObjects(ps, null);
            DbConnector.returnConnection(dbcon);
        }
    }
    
    public static boolean isFish(final int templateId) {
        return templateId == 158 || templateId == 164 || templateId == 160 || templateId == 159 || templateId == 163 || templateId == 157 || templateId == 162 || templateId == 161 || templateId == 165;
    }
    
    public static final void trimSizes() {
        trimSizes(ItemDbStrings.getInstance());
        trimSizes(BodyDbStrings.getInstance());
        trimSizes(CoinDbStrings.getInstance());
    }
    
    public static final void trimSizes(final DbStrings instance) {
        Connection dbcon = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            dbcon = DbConnector.getItemDbCon();
            ps = dbcon.prepareStatement(instance.getItemWeights());
            rs = ps.executeQuery();
            final int maxSizeMod = 5;
            while (rs.next()) {
                try {
                    final long id = rs.getLong("WURMID");
                    final ItemTemplate template = ItemTemplateFactory.getInstance().getTemplate(rs.getInt("TEMPLATEID"));
                    if (template == null) {
                        throw new WurmServerException("No template.");
                    }
                    final int weight = rs.getInt("WEIGHT");
                    if ((template.isCombine() && !template.isLiquid()) || isFish(template.getTemplateId())) {
                        float mod = weight / template.getWeightGrams();
                        if (mod > 125.0f) {
                            setSizeZ(id, template.getSizeZ() * 5, dbcon, instance);
                            setSizeY(id, template.getSizeY() * 5, dbcon, instance);
                            setSizeX(id, template.getSizeX() * 5, dbcon, instance);
                        }
                        else if (mod > 25.0f) {
                            setSizeZ(id, template.getSizeZ() * 5, dbcon, instance);
                            setSizeY(id, template.getSizeY() * 5, dbcon, instance);
                            mod /= 25.0f;
                            setSizeX(id, (int)(template.getSizeX() * mod), dbcon, instance);
                        }
                        else if (mod > 5.0f) {
                            setSizeZ(id, template.getSizeZ() * 5, dbcon, instance);
                            mod /= 5.0f;
                            setSizeY(id, (int)(template.getSizeY() * mod), dbcon, instance);
                            setSizeX(id, template.getSizeX(), dbcon, instance);
                        }
                        else {
                            setSizeZ(id, Math.max(1, (int)(template.getSizeZ() * mod)), dbcon, instance);
                            setSizeY(id, Math.max(1, (int)(template.getSizeY() * mod)), dbcon, instance);
                            setSizeX(id, Math.max(1, (int)(template.getSizeX() * mod)), dbcon, instance);
                        }
                    }
                    else {
                        if (template.isLiquid()) {
                            continue;
                        }
                        setSizeX(id, template.getSizeX(), dbcon, instance);
                        setSizeY(id, template.getSizeY(), dbcon, instance);
                        setSizeZ(id, template.getSizeZ(), dbcon, instance);
                    }
                }
                catch (Exception ex) {
                    if (!ItemBatchJob.logger.isLoggable(Level.FINE)) {
                        continue;
                    }
                    ItemBatchJob.logger.log(Level.FINE, "Problem: " + ex.getMessage(), ex);
                }
            }
        }
        catch (SQLException sqx) {
            ItemBatchJob.logger.log(Level.WARNING, sqx.getMessage(), sqx);
        }
        finally {
            DbUtilities.closeDatabaseObjects(ps, rs);
            DbConnector.returnConnection(dbcon);
        }
    }
    
    public static void setSizeX(final long id, final int sizex, final Connection dbcon, final DbStrings instance) {
        PreparedStatement ps = null;
        try {
            ps = dbcon.prepareStatement(instance.setSizeX());
            ps.setInt(1, sizex);
            ps.setLong(2, id);
            ps.executeUpdate();
            ps.close();
        }
        catch (SQLException sqx) {
            ItemBatchJob.logger.log(Level.WARNING, "Failed to save item " + id, sqx);
        }
        finally {
            DbUtilities.closeDatabaseObjects(ps, null);
        }
    }
    
    public static void setSizeY(final long id, final int sizey, final Connection dbcon, final DbStrings instance) {
        PreparedStatement ps = null;
        try {
            ps = dbcon.prepareStatement(instance.setSizeY());
            ps.setInt(1, sizey);
            ps.setLong(2, id);
            ps.executeUpdate();
            ps.close();
        }
        catch (SQLException sqx) {
            ItemBatchJob.logger.log(Level.WARNING, "Failed to save item " + id, sqx);
        }
        finally {
            DbUtilities.closeDatabaseObjects(ps, null);
        }
    }
    
    public static void setSizeZ(final long id, final int sizez, final Connection dbcon, final DbStrings instance) {
        PreparedStatement ps = null;
        try {
            ps = dbcon.prepareStatement(instance.setSizeZ());
            ps.setInt(1, sizez);
            ps.setLong(2, id);
            ps.executeUpdate();
            ps.close();
        }
        catch (SQLException sqx) {
            ItemBatchJob.logger.log(Level.WARNING, "Failed to save item " + id, sqx);
        }
        finally {
            DbUtilities.closeDatabaseObjects(ps, null);
        }
    }
    
    public static void setMat(final long id, final byte material, final DbStrings instance) {
        Connection dbcon = null;
        PreparedStatement ps = null;
        try {
            dbcon = DbConnector.getItemDbCon();
            ps = dbcon.prepareStatement(instance.setMaterial());
            ps.setByte(1, material);
            ps.setLong(2, id);
            ps.executeUpdate();
            ps.close();
        }
        catch (SQLException sqx) {
            ItemBatchJob.logger.log(Level.WARNING, "Failed to save item " + id, sqx);
        }
        finally {
            DbUtilities.closeDatabaseObjects(ps, null);
            DbConnector.returnConnection(dbcon);
        }
    }
    
    public static void setPar(final long id, final long pid, final DbStrings instance) {
        Connection dbcon = null;
        PreparedStatement ps = null;
        try {
            dbcon = DbConnector.getItemDbCon();
            ps = dbcon.prepareStatement(instance.setParentId());
            ps.setLong(1, pid);
            ps.setLong(2, id);
            ps.executeUpdate();
        }
        catch (SQLException sqx) {
            ItemBatchJob.logger.log(Level.WARNING, "Failed to save item " + id, sqx);
        }
        finally {
            DbUtilities.closeDatabaseObjects(ps, null);
            DbConnector.returnConnection(dbcon);
        }
    }
    
    public static void setDesc(final long id, final String name, final DbStrings instance) {
        Connection dbcon = null;
        PreparedStatement ps = null;
        try {
            dbcon = DbConnector.getItemDbCon();
            ps = dbcon.prepareStatement(instance.setName());
            ps.setString(1, name);
            ps.setLong(2, id);
            ps.executeUpdate();
        }
        catch (SQLException sqx) {
            ItemBatchJob.logger.log(Level.WARNING, "Failed to save item " + id, sqx);
        }
        finally {
            DbUtilities.closeDatabaseObjects(ps, null);
            DbConnector.returnConnection(dbcon);
        }
    }
    
    public static void setW(final long id, final int w, final DbStrings instance) {
        Connection dbcon = null;
        PreparedStatement ps = null;
        try {
            dbcon = DbConnector.getItemDbCon();
            ps = dbcon.prepareStatement(instance.setWeight());
            ps.setInt(1, w);
            ps.setLong(2, id);
            ps.executeUpdate();
        }
        catch (SQLException sqx) {
            ItemBatchJob.logger.log(Level.WARNING, "Failed to save item " + id, sqx);
        }
        finally {
            DbUtilities.closeDatabaseObjects(ps, null);
            DbConnector.returnConnection(dbcon);
        }
    }
    
    static {
        ItemBatchJob.logger = Logger.getLogger(ItemBatchJob.class.getName());
    }
}
