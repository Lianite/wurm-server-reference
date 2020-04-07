// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.villages;

import com.wurmonline.server.creatures.Creature;
import java.sql.PreparedStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import com.wurmonline.server.utils.DbUtilities;
import java.sql.SQLException;
import java.util.logging.Level;
import com.wurmonline.server.DbConnector;
import java.io.IOException;
import com.wurmonline.server.WurmId;
import java.util.logging.Logger;
import com.wurmonline.shared.constants.CounterTypes;
import com.wurmonline.server.TimeConstants;
import com.wurmonline.server.MiscConstants;

public abstract class Citizen implements MiscConstants, TimeConstants, Comparable<Citizen>, CounterTypes
{
    public final long wurmId;
    final String name;
    private static final Logger logger;
    private static final String DELETE = "DELETE FROM CITIZENS WHERE WURMID=?";
    VillageRole role;
    long voteDate;
    long votedFor;
    
    Citizen(final long aWurmId, final String aName, final VillageRole aRole, final long aVotedate, final long aVotedfor) {
        this.role = null;
        this.voteDate = -10L;
        this.votedFor = -10L;
        this.wurmId = aWurmId;
        this.name = aName;
        this.role = aRole;
        this.voteDate = aVotedate;
        this.votedFor = aVotedfor;
    }
    
    public final VillageRole getRole() {
        return this.role;
    }
    
    public final String getName() {
        return this.name;
    }
    
    public final long getId() {
        return this.wurmId;
    }
    
    public final boolean isPlayer() {
        return WurmId.getType(this.wurmId) == 0;
    }
    
    public final boolean hasVoted() {
        return System.currentTimeMillis() - this.voteDate < 604800000L;
    }
    
    public final long getVoteDate() {
        return this.voteDate;
    }
    
    public final long getVotedFor() {
        return this.votedFor;
    }
    
    public abstract void setRole(final VillageRole p0) throws IOException;
    
    abstract void setVoteDate(final long p0) throws IOException;
    
    abstract void setVotedFor(final long p0) throws IOException;
    
    static final void delete(final long wid) throws IOException {
        Connection dbcon = null;
        PreparedStatement ps = null;
        try {
            dbcon = DbConnector.getZonesDbCon();
            ps = dbcon.prepareStatement("DELETE FROM CITIZENS WHERE WURMID=?");
            ps.setLong(1, wid);
            ps.executeUpdate();
        }
        catch (SQLException sqx) {
            Citizen.logger.log(Level.WARNING, "Failed to delete citizen " + wid + ": " + sqx.getMessage(), sqx);
            throw new IOException(sqx);
        }
        finally {
            DbUtilities.closeDatabaseObjects(ps, null);
            DbConnector.returnConnection(dbcon);
        }
    }
    
    @Override
    public int compareTo(final Citizen aCitizen) {
        return this.getName().compareTo(aCitizen.getName());
    }
    
    abstract void create(final Creature p0, final int p1) throws IOException;
    
    @Override
    public String toString() {
        return "Citizen [wurmId=" + this.wurmId + ", name=" + this.name + ", role=" + this.role + "]";
    }
    
    static {
        logger = Logger.getLogger(Citizen.class.getName());
    }
}
