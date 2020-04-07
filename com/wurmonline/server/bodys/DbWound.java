// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.bodys;

import com.wurmonline.server.creatures.Creature;
import com.wurmonline.server.items.Item;
import com.wurmonline.server.items.NoSpaceException;
import com.wurmonline.server.creatures.NoSuchCreatureException;
import java.sql.PreparedStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import com.wurmonline.server.utils.DbUtilities;
import java.sql.SQLException;
import java.util.logging.Level;
import com.wurmonline.server.DbConnector;
import java.util.logging.Logger;

public final class DbWound extends Wound
{
    private static final String CREATE_WOUND = "INSERT INTO WOUNDS( ID, OWNER,TYPE,LOCATION,SEVERITY, POISONSEVERITY,INFECTIONSEVERITY,BANDAGED,LASTPOLLED) VALUES(?,?,?,?,?,?,?,?,?)";
    private static final String DELETE_WOUND = "DELETE FROM WOUNDS WHERE ID=?";
    private static final String SET_SEVERITY = "update WOUNDS set SEVERITY=? where ID=?";
    private static final String SET_POISONSEVERITY = "update WOUNDS set POISONSEVERITY=? where ID=?";
    private static final String SET_INFECTIONSEVERITY = "update WOUNDS set INFECTIONSEVERITY=? where ID=?";
    private static final String SET_BANDAGED = "update WOUNDS set BANDAGED=? where ID=?";
    private static final String SET_HEALEFF = "update WOUNDS set HEALEFF=? where ID=?";
    private static final Logger logger;
    
    public DbWound(final byte aType, final byte aLocation, final float aSeverity, final long aOwner, final float aPoisonSeverity, final float aInfectionSeverity, final boolean pvp, final boolean spell) {
        super(aType, aLocation, aSeverity, aOwner, aPoisonSeverity, aInfectionSeverity, false, pvp, spell);
    }
    
    public DbWound(final long aId, final byte aType, final byte aLocation, final float aSeverity, final long aOwner, final float aPoisonSeverity, final float aInfectionSeverity, final long aLastPolled, final boolean aBandaged, final byte aHealEff) {
        super(aId, aType, aLocation, aSeverity, aOwner, aPoisonSeverity, aInfectionSeverity, aLastPolled, aBandaged, aHealEff);
    }
    
    @Override
    final void create() {
        Connection dbcon = null;
        PreparedStatement ps = null;
        try {
            dbcon = DbConnector.getPlayerDbCon();
            ps = dbcon.prepareStatement("INSERT INTO WOUNDS( ID, OWNER,TYPE,LOCATION,SEVERITY, POISONSEVERITY,INFECTIONSEVERITY,BANDAGED,LASTPOLLED) VALUES(?,?,?,?,?,?,?,?,?)");
            ps.setLong(1, this.getId());
            ps.setLong(2, this.getOwner());
            ps.setByte(3, this.getType());
            ps.setByte(4, this.getLocation());
            ps.setFloat(5, this.getSeverity());
            ps.setFloat(6, this.getPoisonSeverity());
            ps.setFloat(7, this.getInfectionSeverity());
            ps.setBoolean(8, this.isBandaged());
            ps.setLong(9, this.getLastPolled());
            ps.executeUpdate();
        }
        catch (SQLException sqex) {
            DbWound.logger.log(Level.WARNING, this.getId() + " " + sqex.getMessage(), sqex);
        }
        finally {
            DbUtilities.closeDatabaseObjects(ps, null);
            DbConnector.returnConnection(dbcon);
        }
    }
    
    @Override
    final void setSeverity(float sev) {
        sev = Math.max(0.0f, sev);
        if (this.getSeverity() != sev) {
            this.severity = sev;
            Connection dbcon = null;
            PreparedStatement ps = null;
            try {
                dbcon = DbConnector.getPlayerDbCon();
                ps = dbcon.prepareStatement("update WOUNDS set SEVERITY=? where ID=?");
                ps.setFloat(1, this.getSeverity());
                ps.setLong(2, this.getId());
                ps.executeUpdate();
            }
            catch (SQLException sqx) {
                DbWound.logger.log(Level.WARNING, "Failed to save wound " + this.getId() + ":" + sqx.getMessage(), sqx);
            }
            finally {
                DbUtilities.closeDatabaseObjects(ps, null);
                DbConnector.returnConnection(dbcon);
            }
        }
    }
    
    @Override
    public final void setPoisonSeverity(final float sev) {
        if (this.poisonSeverity != sev) {
            this.poisonSeverity = Math.max(0.0f, sev);
            this.poisonSeverity = Math.min(100.0f, this.poisonSeverity);
            if (this.creature != null && this.creature.isPlayer()) {
                if (sev == 0.0f) {
                    this.creature.poisonChanged(true, this);
                }
                else {
                    this.creature.poisonChanged(false, this);
                }
            }
            Connection dbcon = null;
            PreparedStatement ps = null;
            try {
                dbcon = DbConnector.getPlayerDbCon();
                ps = dbcon.prepareStatement("update WOUNDS set POISONSEVERITY=? where ID=?");
                ps.setFloat(1, this.getPoisonSeverity());
                ps.setLong(2, this.getId());
                ps.executeUpdate();
            }
            catch (SQLException sqx) {
                DbWound.logger.log(Level.WARNING, "Failed to save wound " + this.getId() + ":" + sqx.getMessage(), sqx);
            }
            finally {
                DbUtilities.closeDatabaseObjects(ps, null);
                DbConnector.returnConnection(dbcon);
            }
        }
    }
    
    @Override
    public final void setInfectionSeverity(final float sev) {
        if (this.infectionSeverity != sev) {
            this.infectionSeverity = Math.max(0.0f, sev);
            this.infectionSeverity = Math.min(100.0f, this.infectionSeverity);
            Connection dbcon = null;
            PreparedStatement ps = null;
            try {
                dbcon = DbConnector.getPlayerDbCon();
                ps = dbcon.prepareStatement("update WOUNDS set INFECTIONSEVERITY=? where ID=?");
                ps.setFloat(1, this.infectionSeverity);
                ps.setLong(2, this.getId());
                ps.executeUpdate();
            }
            catch (SQLException sqx) {
                DbWound.logger.log(Level.WARNING, "Failed to save wound " + this.getId() + ": " + sqx.getMessage(), sqx);
            }
            finally {
                DbUtilities.closeDatabaseObjects(ps, null);
                DbConnector.returnConnection(dbcon);
            }
        }
    }
    
    @Override
    public final void setBandaged(final boolean aBandaged) {
        if (this.isBandaged != aBandaged) {
            this.isBandaged = aBandaged;
            Connection dbcon = null;
            PreparedStatement ps = null;
            try {
                dbcon = DbConnector.getPlayerDbCon();
                ps = dbcon.prepareStatement("update WOUNDS set BANDAGED=? where ID=?");
                ps.setBoolean(1, this.isBandaged());
                ps.setLong(2, this.getId());
                ps.executeUpdate();
            }
            catch (SQLException sqx) {
                DbWound.logger.log(Level.WARNING, "Failed to set bandaged for wound " + this.getId() + ": " + sqx.getMessage(), sqx);
            }
            finally {
                DbUtilities.closeDatabaseObjects(ps, null);
                DbConnector.returnConnection(dbcon);
            }
        }
    }
    
    @Override
    public final void setHealeff(final byte healeff) {
        if (this.healEff < healeff) {
            this.healEff = healeff;
            Connection dbcon = null;
            PreparedStatement ps = null;
            try {
                dbcon = DbConnector.getPlayerDbCon();
                ps = dbcon.prepareStatement("update WOUNDS set HEALEFF=? where ID=?");
                ps.setByte(1, this.getHealEff());
                ps.setLong(2, this.getId());
                ps.executeUpdate();
            }
            catch (SQLException sqx) {
                DbWound.logger.log(Level.WARNING, "Failed to save wound " + this.getId() + ": " + sqx.getMessage(), sqx);
            }
            finally {
                DbUtilities.closeDatabaseObjects(ps, null);
                DbConnector.returnConnection(dbcon);
            }
            try {
                if (this.getCreature().getBody() != null) {
                    final Item bodypart = this.getCreature().getBody().getBodyPartForWound(this);
                    try {
                        final Creature[] watchers = bodypart.getWatchers();
                        for (int x = 0; x < watchers.length; ++x) {
                            watchers[x].getCommunicator().sendUpdateWound(this, bodypart);
                        }
                    }
                    catch (NoSuchCreatureException ex) {}
                }
                else if (this.getCreature() != null) {
                    DbWound.logger.log(Level.WARNING, this.getCreature().getName() + " body is null.", new Exception());
                }
                else {
                    DbWound.logger.log(Level.WARNING, "Wound: creature==null", new Exception());
                }
            }
            catch (NoSpaceException nsp) {
                DbWound.logger.log(Level.INFO, nsp.getMessage(), nsp);
            }
        }
    }
    
    @Override
    final void setLastPolled(final long lp) {
        if (this.lastPolled != lp) {
            this.lastPolled = lp;
        }
    }
    
    @Override
    final void delete() {
        Connection dbcon = null;
        PreparedStatement ps = null;
        try {
            dbcon = DbConnector.getPlayerDbCon();
            ps = dbcon.prepareStatement("DELETE FROM WOUNDS WHERE ID=?");
            ps.setLong(1, this.getId());
            ps.executeUpdate();
        }
        catch (SQLException sqx) {
            DbWound.logger.log(Level.WARNING, "Failed to delete wound " + this.getId() + ":" + sqx.getMessage(), sqx);
        }
        finally {
            DbUtilities.closeDatabaseObjects(ps, null);
            DbConnector.returnConnection(dbcon);
        }
        if (this.poisonSeverity > 0.0f && this.creature != null && this.creature.isPlayer()) {
            this.creature.poisonChanged(true, this);
        }
    }
    
    static {
        logger = Logger.getLogger(DbWound.class.getName());
    }
}
