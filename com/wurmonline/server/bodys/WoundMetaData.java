// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.bodys;

import java.sql.PreparedStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import com.wurmonline.server.utils.DbUtilities;
import java.sql.SQLException;
import java.io.IOException;
import com.wurmonline.server.DbConnector;
import java.util.logging.Logger;
import com.wurmonline.server.MiscConstants;

public final class WoundMetaData implements MiscConstants
{
    private static final Logger logger;
    private final byte location;
    private final long id;
    private final byte type;
    private final float severity;
    private final long owner;
    private final float poisonSeverity;
    private final float infectionSeverity;
    private final boolean isBandaged;
    private final long lastPolled;
    private final byte healEff;
    private static final String CREATE_WOUND = "INSERT INTO WOUNDS( ID, OWNER,TYPE,LOCATION,SEVERITY, POISONSEVERITY,INFECTIONSEVERITY,BANDAGED,LASTPOLLED, HEALEFF) VALUES(?,?,?,?,?,?,?,?,?,?)";
    
    public WoundMetaData(final long aId, final byte aType, final byte aLocation, final float aSeverity, final long aOwner, final float aPoisonSeverity, final float aInfectionSeverity, final boolean aBandaged, final long aLastPolled, final byte aHealEff) {
        this.id = aId;
        this.type = aType;
        this.location = aLocation;
        this.severity = aSeverity;
        this.owner = aOwner;
        this.poisonSeverity = aPoisonSeverity;
        this.infectionSeverity = aInfectionSeverity;
        this.lastPolled = aLastPolled;
        this.healEff = aHealEff;
        this.isBandaged = aBandaged;
    }
    
    public void save() throws IOException {
        Connection dbcon = null;
        PreparedStatement ps = null;
        try {
            dbcon = DbConnector.getPlayerDbCon();
            ps = dbcon.prepareStatement("INSERT INTO WOUNDS( ID, OWNER,TYPE,LOCATION,SEVERITY, POISONSEVERITY,INFECTIONSEVERITY,BANDAGED,LASTPOLLED, HEALEFF) VALUES(?,?,?,?,?,?,?,?,?,?)");
            ps.setLong(1, this.id);
            ps.setLong(2, this.owner);
            ps.setByte(3, this.type);
            ps.setByte(4, this.location);
            ps.setFloat(5, this.severity);
            ps.setFloat(6, this.poisonSeverity);
            ps.setFloat(7, this.infectionSeverity);
            ps.setBoolean(8, this.isBandaged);
            ps.setLong(9, this.lastPolled);
            ps.setByte(10, this.healEff);
            ps.executeUpdate();
        }
        catch (SQLException sqex) {
            throw new IOException(this.id + " " + sqex.getMessage(), sqex);
        }
        finally {
            DbUtilities.closeDatabaseObjects(ps, null);
            DbConnector.returnConnection(dbcon);
        }
    }
    
    static {
        logger = Logger.getLogger(WoundMetaData.class.getName());
    }
}
