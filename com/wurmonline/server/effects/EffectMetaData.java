// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.effects;

import java.sql.PreparedStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import com.wurmonline.server.utils.DbUtilities;
import java.sql.SQLException;
import java.io.IOException;
import com.wurmonline.server.DbConnector;

public final class EffectMetaData
{
    private final long owner;
    private final short type;
    private final float posX;
    private final float posY;
    private final float posZ;
    private final long startTime;
    private static final String CREATE_EFFECT = "INSERT INTO EFFECTS(  OWNER,TYPE,POSX,POSY,POSZ,STARTTIME) VALUES(?,?,?,?,?,?)";
    
    public EffectMetaData(final long aOwner, final short aType, final float aPosx, final float aPosy, final float aPosz, final long aStartTime) {
        this.owner = aOwner;
        this.type = aType;
        this.posX = aPosx;
        this.posY = aPosy;
        this.posZ = aPosz;
        this.startTime = aStartTime;
    }
    
    public void save() throws IOException {
        Connection dbcon = null;
        PreparedStatement ps = null;
        try {
            dbcon = DbConnector.getItemDbCon();
            ps = dbcon.prepareStatement("INSERT INTO EFFECTS(  OWNER,TYPE,POSX,POSY,POSZ,STARTTIME) VALUES(?,?,?,?,?,?)");
            ps.setLong(1, this.owner);
            ps.setShort(2, this.type);
            ps.setFloat(3, this.posX);
            ps.setFloat(4, this.posY);
            ps.setFloat(5, this.posZ);
            ps.setLong(6, this.startTime);
            ps.executeUpdate();
        }
        catch (SQLException sqex) {
            throw new IOException(this.owner + " " + sqex.getMessage(), sqex);
        }
        finally {
            DbUtilities.closeDatabaseObjects(ps, null);
            DbConnector.returnConnection(dbcon);
        }
    }
}
