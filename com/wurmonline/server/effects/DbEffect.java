// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.effects;

import java.util.logging.Level;
import java.sql.ResultSet;
import java.sql.PreparedStatement;
import java.sql.Connection;
import java.sql.Statement;
import com.wurmonline.server.utils.DbUtilities;
import java.sql.SQLException;
import com.wurmonline.server.Server;
import com.wurmonline.server.DbConnector;
import com.wurmonline.server.WurmId;
import java.io.IOException;
import java.util.logging.Logger;
import com.wurmonline.shared.constants.CounterTypes;

public final class DbEffect extends Effect implements CounterTypes
{
    private static final Logger logger;
    private static final long serialVersionUID = 1839666903728378027L;
    private static final String CREATE_EFFECT_SQL = "insert into EFFECTS(OWNER, TYPE, POSX, POSY, POSZ, STARTTIME) values(?,?,?,?,?,?)";
    private static final String UPDATE_EFFECT_SQL = "update EFFECTS set OWNER=?, TYPE=?, POSX=?, POSY=?, POSZ=? where ID=?";
    private static final String GET_EFFECT_SQL = "select * from EFFECTS where ID=?";
    private static final String DELETE_EFFECT_SQL = "delete from EFFECTS where ID=?";
    
    DbEffect(final long aOwner, final short aType, final float aPosX, final float aPosY, final float aPosZ, final boolean aSurfaced) {
        super(aOwner, aType, aPosX, aPosY, aPosZ, aSurfaced);
    }
    
    DbEffect(final long aOwner, final int aNumber) throws IOException {
        super(aOwner, aNumber);
    }
    
    DbEffect(final int num, final long ownerid, final short typ, final float posx, final float posy, final float posz, final long stime) {
        super(num, ownerid, typ, posx, posy, posz, stime);
    }
    
    @Override
    public void save() throws IOException {
        if (WurmId.getType(this.getOwner()) != 6) {
            Connection dbcon = null;
            PreparedStatement ps = null;
            ResultSet rs = null;
            try {
                if (WurmId.getType(this.getOwner()) == 2 || WurmId.getType(this.getOwner()) == 19 || WurmId.getType(this.getOwner()) == 20) {
                    dbcon = DbConnector.getItemDbCon();
                    if (this.exists(dbcon)) {
                        ps = dbcon.prepareStatement("update EFFECTS set OWNER=?, TYPE=?, POSX=?, POSY=?, POSZ=? where ID=?");
                        ps.setLong(1, this.getOwner());
                        ps.setShort(2, this.getType());
                        ps.setFloat(3, this.getPosX());
                        ps.setFloat(4, this.getPosY());
                        ps.setFloat(5, this.getPosZ());
                        ps.setInt(6, this.getId());
                        ps.executeUpdate();
                    }
                    else {
                        ps = dbcon.prepareStatement("insert into EFFECTS(OWNER, TYPE, POSX, POSY, POSZ, STARTTIME) values(?,?,?,?,?,?)", 1);
                        ps.setLong(1, this.getOwner());
                        ps.setShort(2, this.getType());
                        ps.setFloat(3, this.getPosX());
                        ps.setFloat(4, this.getPosY());
                        ps.setFloat(5, this.getPosZ());
                        ps.setLong(6, this.getStartTime());
                        ps.executeUpdate();
                        rs = ps.getGeneratedKeys();
                        if (rs.next()) {
                            this.setId(rs.getInt(1));
                        }
                    }
                }
                else if (this.getId() == 0) {
                    this.setId(-Math.abs(Server.rand.nextInt()));
                }
            }
            catch (SQLException sqx) {
                throw new IOException(sqx);
            }
            finally {
                DbUtilities.closeDatabaseObjects(ps, rs);
                DbConnector.returnConnection(dbcon);
            }
        }
        else if (this.getId() == 0) {
            this.setId(-Math.abs(Server.rand.nextInt()));
        }
    }
    
    @Override
    void load() throws IOException {
        if (WurmId.getType(this.getOwner()) != 6) {
            Connection dbcon = null;
            PreparedStatement ps = null;
            ResultSet rs = null;
            try {
                if (WurmId.getType(this.getOwner()) == 2 || WurmId.getType(this.getOwner()) == 19 || WurmId.getType(this.getOwner()) == 20) {
                    dbcon = DbConnector.getItemDbCon();
                    ps = dbcon.prepareStatement("select * from EFFECTS where ID=?");
                    ps.setInt(1, this.getId());
                    rs = ps.executeQuery();
                    if (rs.next()) {
                        this.setPosX(rs.getFloat("POSX"));
                        this.setPosY(rs.getFloat("POSY"));
                        this.setPosZ(rs.getFloat("POSZ"));
                        this.setType(rs.getShort("TYPE"));
                        this.setOwner(rs.getLong("OWNER"));
                        this.setStartTime(rs.getLong("STARTTIME"));
                    }
                    else {
                        DbEffect.logger.log(Level.WARNING, "Failed to find effect with number " + this.getId());
                    }
                }
            }
            catch (SQLException sqx) {
                throw new IOException(sqx);
            }
            finally {
                DbUtilities.closeDatabaseObjects(ps, rs);
                DbConnector.returnConnection(dbcon);
            }
        }
    }
    
    @Override
    void delete() {
        if (WurmId.getType(this.getOwner()) != 6) {
            Connection dbcon = null;
            PreparedStatement ps = null;
            try {
                if (WurmId.getType(this.getOwner()) == 2 || WurmId.getType(this.getOwner()) == 19 || WurmId.getType(this.getOwner()) == 20) {
                    dbcon = DbConnector.getItemDbCon();
                    ps = dbcon.prepareStatement("delete from EFFECTS where ID=?");
                    ps.setInt(1, this.getId());
                    ps.executeUpdate();
                }
            }
            catch (SQLException sqx) {
                DbEffect.logger.log(Level.WARNING, "Failed to delete effect with id " + this.getId(), sqx);
            }
            finally {
                DbUtilities.closeDatabaseObjects(ps, null);
                DbConnector.returnConnection(dbcon);
            }
        }
    }
    
    private boolean exists(final Connection dbcon) throws SQLException {
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = dbcon.prepareStatement("select * from EFFECTS where ID=?");
            ps.setInt(1, this.getId());
            rs = ps.executeQuery();
            return rs.next();
        }
        finally {
            DbUtilities.closeDatabaseObjects(ps, rs);
        }
    }
    
    static {
        logger = Logger.getLogger(DbEffect.class.getName());
    }
}
