// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.villages;

import java.util.HashMap;
import java.sql.PreparedStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import com.wurmonline.server.utils.DbUtilities;
import java.sql.SQLException;
import java.io.IOException;
import com.wurmonline.server.DbConnector;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

public final class VillagePerimeter
{
    private static final Logger logger;
    private final int villageId;
    private static final String INSERT_PERIMETERVALUES = "INSERT INTO VILLAGEPERIMETERS(SETTINGS,ID) VALUES (?,?)";
    private static final String UPDATE_PERIMETERVALUES = "UPDATE VILLAGEPERIMETERS SET SETTINGS=? WHERE ID=?";
    private static final String DELETE_PERIMETERVALUES = "DELETE FROM VILLAGEPERIMETERS WHERE ID=?";
    private static final String INSERT_PERIMETERFRIEND = "INSERT INTO PERIMETERFRIENDS(ID,NAME) VALUES (?,?)";
    private static final String DELETE_PERIMETERFRIEND = "DELETE FROM PERIMETERFRIENDS WHERE NAME=? AND ID=?";
    private static final String DELETE_PERIMETERFRIENDVILLAGE = "DELETE FROM PERIMETERFRIENDS WHERE ID=?";
    private final Set<String> perimeterFriends;
    private static final Map<Integer, VillagePerimeter> parmap;
    private long settings;
    private static final String[] emptyFriends;
    
    VillagePerimeter(final int aVillageId) {
        this.perimeterFriends = new HashSet<String>();
        this.villageId = aVillageId;
    }
    
    static VillagePerimeter getPerimeter(final int villageId) {
        return VillagePerimeter.parmap.get(villageId);
    }
    
    static void removePerimeter(final int villageId) {
        VillagePerimeter.parmap.remove(villageId);
    }
    
    void create() throws IOException {
        Connection dbcon = null;
        PreparedStatement ps = null;
        try {
            dbcon = DbConnector.getZonesDbCon();
            ps = dbcon.prepareStatement("INSERT INTO VILLAGEPERIMETERS(SETTINGS,ID) VALUES (?,?)");
            ps.setLong(1, this.settings);
            ps.setInt(2, this.villageId);
            ps.executeUpdate();
        }
        catch (SQLException sqx) {
            throw new IOException(sqx);
        }
        finally {
            DbUtilities.closeDatabaseObjects(ps, null);
            DbConnector.returnConnection(dbcon);
        }
    }
    
    void update() throws IOException {
        Connection dbcon = null;
        PreparedStatement ps = null;
        try {
            dbcon = DbConnector.getZonesDbCon();
            ps = dbcon.prepareStatement("UPDATE VILLAGEPERIMETERS SET SETTINGS=? WHERE ID=?");
            ps.setLong(1, this.settings);
            ps.setInt(2, this.villageId);
            ps.executeUpdate();
        }
        catch (SQLException sqx) {
            throw new IOException(sqx);
        }
        finally {
            DbUtilities.closeDatabaseObjects(ps, null);
            DbConnector.returnConnection(dbcon);
        }
    }
    
    void delete() throws IOException {
        Connection dbcon = null;
        PreparedStatement ps = null;
        try {
            dbcon = DbConnector.getZonesDbCon();
            ps = dbcon.prepareStatement("DELETE FROM VILLAGEPERIMETERS WHERE ID=?");
            ps.setInt(1, this.villageId);
            ps.executeUpdate();
        }
        catch (SQLException sqx) {
            throw new IOException(sqx);
        }
        finally {
            DbUtilities.closeDatabaseObjects(ps, null);
            DbConnector.returnConnection(dbcon);
        }
        this.deleteAllFriend();
    }
    
    void setAllowsFenceDestruction(final boolean allow) throws IOException {
        final boolean allowFenceDestruction = (this.settings & 0x1L) == 0x1L;
        if (allow != allowFenceDestruction) {
            if (allow) {
                ++this.settings;
            }
            else {
                --this.settings;
            }
            this.update();
        }
    }
    
    void setAllowsRoadDestruction(final boolean allow) throws IOException {
        final boolean allowRoadDestruction = (this.settings >> 1 & 0x1L) == 0x1L;
        if (allow != allowRoadDestruction) {
            if (allow) {
                this.settings += 2L;
            }
            else {
                this.settings -= 2L;
            }
            this.update();
        }
    }
    
    void setAllowsFenceBuilding(final boolean allow) throws IOException {
        final boolean allowFenceBuilding = (this.settings >> 2 & 0x1L) == 0x1L;
        if (allow != allowFenceBuilding) {
            if (allow) {
                this.settings += 4L;
            }
            else {
                this.settings -= 4L;
            }
            this.update();
        }
    }
    
    void setAllowsRoadBuilding(final boolean allow) throws IOException {
        final boolean allowRoadBuilding = (this.settings >> 3 & 0x1L) == 0x1L;
        if (allow != allowRoadBuilding) {
            if (allow) {
                this.settings += 8L;
            }
            else {
                this.settings -= 8L;
            }
            this.update();
        }
    }
    
    void setAllowsBuildings(final boolean allow) throws IOException {
        final boolean allowBuildings = (this.settings >> 4 & 0x1L) == 0x1L;
        if (allow != allowBuildings) {
            if (allow) {
                this.settings += 16L;
            }
            else {
                this.settings -= 16L;
            }
            this.update();
        }
    }
    
    void setAllowsPerimeterActionsForAllies(final boolean allow) throws IOException {
        final boolean allowPerimeterActionsForAllies = (this.settings >> 5 & 0x1L) == 0x1L;
        if (allow != allowPerimeterActionsForAllies) {
            if (allow) {
                this.settings += 32L;
            }
            else {
                this.settings -= 32L;
            }
            this.update();
        }
    }
    
    boolean allowsFenceDestruction() {
        return (this.settings & 0x1L) == 0x1L;
    }
    
    boolean allowsRoadDestruction() {
        return (this.settings >> 1 & 0x1L) == 0x1L;
    }
    
    boolean allowsFenceBuilding() {
        return (this.settings >> 2 & 0x1L) == 0x1L;
    }
    
    boolean allowsRoadBuilding() {
        return (this.settings >> 3 & 0x1L) == 0x1L;
    }
    
    boolean allowsBuildings() {
        return (this.settings >> 4 & 0x1L) == 0x1L;
    }
    
    boolean allowsPerimeterActionsForAllies() {
        return (this.settings >> 5 & 0x1L) == 0x1L;
    }
    
    void deleteAllFriend() throws IOException {
        Connection dbcon = null;
        PreparedStatement ps = null;
        try {
            dbcon = DbConnector.getZonesDbCon();
            ps = dbcon.prepareStatement("DELETE FROM PERIMETERFRIENDS WHERE ID=?");
            ps.setInt(1, this.villageId);
            ps.executeUpdate();
        }
        catch (SQLException sqx) {
            throw new IOException(sqx);
        }
        finally {
            DbUtilities.closeDatabaseObjects(ps, null);
            DbConnector.returnConnection(dbcon);
        }
    }
    
    boolean addFriend(final String friendName) throws IOException {
        if (!this.perimeterFriends.contains(friendName)) {
            Connection dbcon = null;
            PreparedStatement ps = null;
            try {
                dbcon = DbConnector.getZonesDbCon();
                ps = dbcon.prepareStatement("INSERT INTO PERIMETERFRIENDS(ID,NAME) VALUES (?,?)");
                ps.setInt(1, this.villageId);
                ps.setString(2, friendName);
                ps.executeUpdate();
                this.perimeterFriends.add(friendName);
                return true;
            }
            catch (SQLException sqx) {
                throw new IOException(sqx);
            }
            finally {
                DbUtilities.closeDatabaseObjects(ps, null);
                DbConnector.returnConnection(dbcon);
            }
        }
        return false;
    }
    
    boolean deleteFriend(final String friendName) throws IOException {
        if (this.perimeterFriends.contains(friendName)) {
            Connection dbcon = null;
            PreparedStatement ps = null;
            try {
                dbcon = DbConnector.getZonesDbCon();
                ps = dbcon.prepareStatement("DELETE FROM PERIMETERFRIENDS WHERE NAME=? AND ID=?");
                ps.setString(1, friendName);
                ps.setInt(2, this.villageId);
                ps.executeUpdate();
                this.perimeterFriends.remove(friendName);
                return true;
            }
            catch (SQLException sqx) {
                throw new IOException(sqx);
            }
            finally {
                DbUtilities.closeDatabaseObjects(ps, null);
                DbConnector.returnConnection(dbcon);
            }
        }
        return false;
    }
    
    boolean isFriend(final String name) {
        return this.perimeterFriends.contains(name);
    }
    
    public String[] getFriends() {
        if (this.perimeterFriends.isEmpty()) {
            return VillagePerimeter.emptyFriends;
        }
        return this.perimeterFriends.toArray(new String[this.perimeterFriends.size()]);
    }
    
    static {
        logger = Logger.getLogger(VillagePerimeter.class.getName());
        parmap = new HashMap<Integer, VillagePerimeter>();
        emptyFriends = new String[0];
    }
}
