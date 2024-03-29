// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server;

import java.sql.PreparedStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import com.wurmonline.server.utils.DbUtilities;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class EigcClient
{
    private static final Logger logger;
    private final String eigcUserName;
    private String eigcUserPassword;
    private String currentPlayerName;
    private String serviceBundle;
    private String playerAccount;
    private long serviceExpirationTime;
    private static final String UPDATE_EIGC_ACCOUNT = "UPDATE EIGC SET PASSWORD=?,SERVICEBUNDLE=?,EXPIRATION=?,EMAIL=? WHERE USERNAME=?";
    private long lastUsed;
    
    public EigcClient(final String eigcUserId, final String clientPass, final String services, final long expirationTime, final String accountName) {
        this.currentPlayerName = "";
        this.serviceBundle = "";
        this.playerAccount = "";
        this.serviceExpirationTime = Long.MAX_VALUE;
        this.lastUsed = 0L;
        this.eigcUserName = eigcUserId;
        this.eigcUserPassword = clientPass;
        this.serviceBundle = services;
        this.serviceExpirationTime = expirationTime;
        this.playerAccount = accountName;
        if (EigcClient.logger.isLoggable(Level.FINER)) {
            EigcClient.logger.fine("Created EIGC Client for user ID: " + eigcUserId);
        }
    }
    
    public void setExpiration(final long expirationDate) {
        this.serviceExpirationTime = expirationDate;
    }
    
    public long getExpiration() {
        return this.serviceExpirationTime;
    }
    
    public boolean isExpired() {
        return System.currentTimeMillis() > this.serviceExpirationTime;
    }
    
    public void setPlayerName(final String newPlayerName, final String reason) {
        EigcClient.logger.log(Level.INFO, "Setting client " + this.getClientId() + " to player name " + newPlayerName + " reason=" + reason);
        this.currentPlayerName = newPlayerName;
        if (this.currentPlayerName == null || this.currentPlayerName.length() == 0) {
            this.setLastUsed(System.currentTimeMillis());
        }
        else {
            this.setLastUsed(Long.MAX_VALUE);
        }
        if (System.currentTimeMillis() > this.getExpiration()) {
            Eigc.modifyUser(this.getClientId(), "proximity", Long.MAX_VALUE);
        }
    }
    
    public String getPlayerName() {
        return this.currentPlayerName;
    }
    
    public String getAccountName() {
        return this.playerAccount;
    }
    
    public void setAccountName(final String newAccountName) {
        this.playerAccount = newAccountName;
    }
    
    public boolean isPermanent() {
        return this.playerAccount != null && this.playerAccount.length() > 0;
    }
    
    public void setServiceBundle(final String newServices) {
        this.serviceBundle = newServices;
    }
    
    public String getServiceBundle() {
        return this.serviceBundle;
    }
    
    public void setPassword(final String newPassword) {
        this.eigcUserPassword = newPassword;
    }
    
    public String getPassword() {
        return this.eigcUserPassword;
    }
    
    public String getClientId() {
        return this.eigcUserName;
    }
    
    public void updateAccount() {
        Connection dbcon = null;
        PreparedStatement ps = null;
        try {
            dbcon = DbConnector.getLoginDbCon();
            ps = dbcon.prepareStatement("UPDATE EIGC SET PASSWORD=?,SERVICEBUNDLE=?,EXPIRATION=?,EMAIL=? WHERE USERNAME=?");
            ps.setString(1, this.eigcUserPassword);
            ps.setString(2, this.serviceBundle);
            ps.setLong(3, this.serviceExpirationTime);
            ps.setString(4, this.playerAccount);
            ps.setString(5, this.eigcUserName);
            ps.executeUpdate();
        }
        catch (SQLException sqx) {
            EigcClient.logger.log(Level.WARNING, "Problem updating EIGC for username " + this.eigcUserName + " due to " + sqx, sqx);
        }
        finally {
            DbUtilities.closeDatabaseObjects(ps, null);
            DbConnector.returnConnection(dbcon);
        }
    }
    
    protected final long getLastUsed() {
        return this.lastUsed;
    }
    
    private final void setLastUsed(final long time) {
        this.lastUsed = time;
    }
    
    protected final boolean isUsed() {
        return this.lastUsed > System.currentTimeMillis();
    }
    
    protected final String timeSinceLastUse() {
        return Server.getTimeFor(System.currentTimeMillis() - this.lastUsed);
    }
    
    static {
        logger = Logger.getLogger(EigcClient.class.getName());
    }
}
