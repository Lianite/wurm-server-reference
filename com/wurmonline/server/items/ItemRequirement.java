// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.items;

import java.util.HashMap;
import java.util.Iterator;
import java.util.HashSet;
import java.sql.ResultSet;
import java.sql.PreparedStatement;
import java.sql.Connection;
import java.sql.Statement;
import com.wurmonline.server.utils.DbUtilities;
import java.sql.SQLException;
import java.util.logging.Level;
import com.wurmonline.server.DbConnector;
import java.util.Set;
import java.util.Map;
import java.util.logging.Logger;

public final class ItemRequirement
{
    private static final Logger logger;
    private static final String loadItemRequirements = "SELECT * FROM ITEMREQUIREMENTS";
    private static final String deleteItemRequirements = "DELETE FROM ITEMREQUIREMENTS WHERE WURMID=?";
    private static final String updateItemRequirements = "UPDATE ITEMREQUIREMENTS SET ITEMSDONE=? WHERE WURMID=? AND TEMPLATEID=?";
    private static final String createItemRequirements = "INSERT INTO ITEMREQUIREMENTS (ITEMSDONE, WURMID, TEMPLATEID) VALUES(?,?,?)";
    private final int templateId;
    private int numsDone;
    private static final Map<Long, Set<ItemRequirement>> requirements;
    private static boolean found;
    
    private ItemRequirement(final int aItemTemplateId, final int aNumbersDone) {
        this.templateId = aItemTemplateId;
        this.numsDone = aNumbersDone;
    }
    
    public static void loadAllItemRequirements() {
        Connection dbcon = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            dbcon = DbConnector.getItemDbCon();
            ps = dbcon.prepareStatement("SELECT * FROM ITEMREQUIREMENTS");
            rs = ps.executeQuery();
            while (rs.next()) {
                setRequirements(rs.getLong("WURMID"), rs.getInt("TEMPLATEID"), rs.getInt("ITEMSDONE"), false, false);
            }
        }
        catch (SQLException ex) {
            ItemRequirement.logger.log(Level.WARNING, "Failed loading item reqs " + ex.getMessage(), ex);
        }
        finally {
            DbUtilities.closeDatabaseObjects(ps, rs);
            DbConnector.returnConnection(dbcon);
        }
    }
    
    public static void setRequirements(final long _wurmid, final int _templateId, final int _numsDone, final boolean save, final boolean create) {
        ItemRequirement.found = false;
        Set<ItemRequirement> doneset = ItemRequirement.requirements.get(_wurmid);
        if (doneset == null) {
            doneset = new HashSet<ItemRequirement>();
            ItemRequirement.requirements.put(_wurmid, doneset);
        }
        for (final ItemRequirement next : doneset) {
            if (next.templateId == _templateId) {
                next.numsDone = _numsDone;
                ItemRequirement.found = true;
            }
        }
        if (!ItemRequirement.found) {
            final ItemRequirement newreq = new ItemRequirement(_templateId, _numsDone);
            doneset.add(newreq);
        }
        if (save) {
            updateDatabaseRequirements(_wurmid, _templateId, _numsDone, create);
        }
    }
    
    public final int getTemplateId() {
        return this.templateId;
    }
    
    public final int getNumsDone() {
        return this.numsDone;
    }
    
    public static void deleteRequirements(final long _wurmid) {
        ItemRequirement.requirements.remove(_wurmid);
        Connection dbcon = null;
        PreparedStatement ps = null;
        try {
            dbcon = DbConnector.getItemDbCon();
            ps = dbcon.prepareStatement("DELETE FROM ITEMREQUIREMENTS WHERE WURMID=?");
            ps.setLong(1, _wurmid);
            ps.executeUpdate();
        }
        catch (SQLException ex) {
            ItemRequirement.logger.log(Level.WARNING, "Failed to delete reqs " + _wurmid, ex);
        }
        finally {
            DbUtilities.closeDatabaseObjects(ps, null);
            DbConnector.returnConnection(dbcon);
        }
    }
    
    public static final Set<ItemRequirement> getRequirements(final long wurmid) {
        return ItemRequirement.requirements.get(wurmid);
    }
    
    public static void updateDatabaseRequirements(final long _wurmid, final int _templateId, final int numsDone, final boolean create) {
        Connection dbcon = null;
        PreparedStatement ps = null;
        try {
            dbcon = DbConnector.getItemDbCon();
            if (numsDone == 1 || create) {
                ps = dbcon.prepareStatement("INSERT INTO ITEMREQUIREMENTS (ITEMSDONE, WURMID, TEMPLATEID) VALUES(?,?,?)");
            }
            else {
                ps = dbcon.prepareStatement("UPDATE ITEMREQUIREMENTS SET ITEMSDONE=? WHERE WURMID=? AND TEMPLATEID=?");
            }
            ps.setInt(1, numsDone);
            ps.setLong(2, _wurmid);
            ps.setInt(3, _templateId);
            ps.executeUpdate();
        }
        catch (SQLException ex) {
            ItemRequirement.logger.log(Level.WARNING, "Failed to update reqs " + _wurmid + ",tid=" + _templateId + ", nums=" + numsDone, ex);
        }
        finally {
            DbUtilities.closeDatabaseObjects(ps, null);
            DbConnector.returnConnection(dbcon);
        }
    }
    
    static int getStateForRequirement(final int _templateId, final long _wurmId) {
        final Set<ItemRequirement> doneSet = ItemRequirement.requirements.get(_wurmId);
        if (doneSet != null) {
            for (final ItemRequirement next : doneSet) {
                if (next.templateId == _templateId) {
                    return next.numsDone;
                }
            }
        }
        return 0;
    }
    
    static {
        logger = Logger.getLogger(ItemRequirement.class.getName());
        requirements = new HashMap<Long, Set<ItemRequirement>>();
        ItemRequirement.found = false;
    }
}
