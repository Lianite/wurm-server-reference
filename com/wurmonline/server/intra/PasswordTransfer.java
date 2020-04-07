// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.intra;

import java.util.LinkedList;
import java.nio.ByteBuffer;
import java.io.IOException;
import com.wurmonline.server.ServerEntry;
import com.wurmonline.server.players.PlayerInfo;
import com.wurmonline.server.LoginServerWebConnection;
import com.wurmonline.server.Servers;
import com.wurmonline.server.players.PlayerInfoFactory;
import java.sql.PreparedStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import com.wurmonline.server.utils.DbUtilities;
import java.sql.SQLException;
import java.util.logging.Level;
import com.wurmonline.server.DbConnector;
import java.util.List;
import java.util.logging.Logger;
import com.wurmonline.server.MiscConstants;

public final class PasswordTransfer extends IntraCommand implements MiscConstants
{
    private static final String CREATE_PASSWORD_TRANSFER = "INSERT INTO PASSWORDTRANSFERS(NAME,WURMID,TIMESTAMP,PASSWORD) VALUES (?,?,?,?)";
    private static final String DELETE_PASSWORD_TRANSFER = "DELETE FROM PASSWORDTRANSFERS WHERE NAME=? AND WURMID=? AND TIMESTAMP=? AND PASSWORD=?";
    private static final String GET_ALL_PASSWORDTRANSFERS = "SELECT * FROM PASSWORDTRANSFERS";
    private static Logger logger;
    private final String name;
    private final long wurmid;
    private final String newPassword;
    private final long timestamp;
    private boolean done;
    private IntraClient client;
    private boolean started;
    public boolean deleted;
    private boolean sentTransfer;
    public static final List<PasswordTransfer> transfers;
    
    public PasswordTransfer(final String aName, final long playerId, final String password, final long _timestamp, final boolean load) {
        this.done = false;
        this.client = null;
        this.started = false;
        this.deleted = false;
        this.sentTransfer = false;
        this.name = aName;
        this.wurmid = playerId;
        this.newPassword = password;
        this.timestamp = _timestamp;
        this.timeOutTime = 30000L;
        if (!load) {
            this.save();
        }
        PasswordTransfer.transfers.add(this);
    }
    
    private void save() {
        Connection dbcon = null;
        PreparedStatement ps = null;
        try {
            dbcon = DbConnector.getLoginDbCon();
            ps = dbcon.prepareStatement("INSERT INTO PASSWORDTRANSFERS(NAME,WURMID,TIMESTAMP,PASSWORD) VALUES (?,?,?,?)");
            ps.setString(1, this.name);
            ps.setLong(2, this.wurmid);
            ps.setLong(3, this.timestamp);
            ps.setString(4, this.newPassword);
            ps.executeUpdate();
        }
        catch (SQLException sqex) {
            PasswordTransfer.logger.log(Level.WARNING, this.name + " " + sqex.getMessage(), sqex);
        }
        finally {
            DbUtilities.closeDatabaseObjects(ps, null);
            DbConnector.returnConnection(dbcon);
        }
    }
    
    private void delete() {
        Connection dbcon = null;
        PreparedStatement ps = null;
        try {
            dbcon = DbConnector.getLoginDbCon();
            ps = dbcon.prepareStatement("DELETE FROM PASSWORDTRANSFERS WHERE NAME=? AND WURMID=? AND TIMESTAMP=? AND PASSWORD=?");
            ps.setString(1, this.name);
            ps.setLong(2, this.wurmid);
            ps.setLong(3, this.timestamp);
            ps.setString(4, this.newPassword);
            ps.executeUpdate();
            this.deleted = true;
        }
        catch (SQLException sqex) {
            PasswordTransfer.logger.log(Level.WARNING, this.name + " " + sqex.getMessage(), sqex);
        }
        finally {
            DbUtilities.closeDatabaseObjects(ps, null);
            DbConnector.returnConnection(dbcon);
        }
    }
    
    public static void loadAllPasswordTransfers() {
        final long start = System.nanoTime();
        int loadedPasswordTransfers = 0;
        Connection dbcon = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            dbcon = DbConnector.getLoginDbCon();
            ps = dbcon.prepareStatement("SELECT * FROM PASSWORDTRANSFERS");
            rs = ps.executeQuery();
            while (rs.next()) {
                new PasswordTransfer(rs.getString("Name"), rs.getLong("WURMID"), rs.getString("PASSWORD"), rs.getLong("TIMESTAMP"), true);
                ++loadedPasswordTransfers;
            }
        }
        catch (SQLException sqex) {
            PasswordTransfer.logger.log(Level.WARNING, sqex.getMessage(), sqex);
        }
        finally {
            DbUtilities.closeDatabaseObjects(ps, rs);
            DbConnector.returnConnection(dbcon);
            final long end = System.nanoTime();
            PasswordTransfer.logger.info("Loaded " + loadedPasswordTransfers + " PasswordTransfers from the database took " + (end - start) / 1000000.0f + " ms");
        }
    }
    
    @Override
    public boolean poll() {
        if (System.currentTimeMillis() > this.timeOutAt) {
            final PlayerInfo info = PlayerInfoFactory.createPlayerInfo(this.name);
            try {
                info.load();
            }
            catch (Exception eex) {
                PasswordTransfer.logger.log(Level.WARNING, "Failed to load info for wurmid " + this.wurmid + ".", eex);
                this.delete();
                this.done = true;
            }
            if (info.wurmId <= 0L) {
                PasswordTransfer.logger.log(Level.WARNING, "Failed to load info for wurmid " + this.wurmid + ". No info available.");
                this.delete();
                this.done = true;
            }
            else if (info.currentServer == Servers.localServer.id) {
                this.delete();
                this.done = true;
            }
            if (!this.done) {
                this.timeOutAt = System.currentTimeMillis() + this.timeOutTime;
                final ServerEntry entry = Servers.getServerWithId(info.currentServer);
                if (entry != null) {
                    if (new LoginServerWebConnection(info.currentServer).changePassword(this.wurmid, this.newPassword)) {
                        this.sentTransfer = true;
                        this.done = true;
                        this.delete();
                        return true;
                    }
                }
                else {
                    PasswordTransfer.logger.log(Level.INFO, this.wurmid + " for currentserver " + info.currentServer + ": the server does not exist.");
                    this.delete();
                    this.done = true;
                }
            }
        }
        return false;
    }
    
    public boolean pollOld() {
        PasswordTransfer.logger2.log(Level.INFO, "PT poll " + PasswordTransfer.num + ", name: " + this.name + ", wurmid: " + this.wurmid + ", timestamp: " + this.timestamp);
        final PlayerInfo info = PlayerInfoFactory.createPlayerInfo(this.name);
        try {
            info.load();
        }
        catch (Exception eex) {
            PasswordTransfer.logger.log(Level.WARNING, "Failed to load info for wurmid " + this.wurmid + ".", eex);
            this.delete();
            this.done = true;
        }
        if (info.wurmId <= 0L) {
            PasswordTransfer.logger.log(Level.WARNING, "Failed to load info for wurmid " + this.wurmid + ". No info available.");
            this.delete();
            this.done = true;
        }
        else if (info.currentServer == Servers.localServer.id) {
            this.delete();
            this.done = true;
        }
        else if (this.client == null && (System.currentTimeMillis() > this.timeOutAt || !this.started)) {
            PasswordTransfer.logger2.log(Level.INFO, "PT starting " + PasswordTransfer.num + ", name: " + this.name + ", wurmid: " + this.wurmid + ", timestamp: " + this.timestamp);
            final ServerEntry entry = Servers.getServerWithId(info.currentServer);
            if (entry != null) {
                try {
                    this.startTime = System.currentTimeMillis();
                    this.timeOutAt = this.startTime + this.timeOutTime;
                    this.started = true;
                    (this.client = new IntraClient(entry.INTRASERVERADDRESS, Integer.parseInt(entry.INTRASERVERPORT), this)).login(entry.INTRASERVERPASSWORD, true);
                    this.done = false;
                }
                catch (IOException iox2) {
                    this.done = true;
                }
            }
            else {
                this.delete();
                this.done = true;
                PasswordTransfer.logger.log(Level.WARNING, "No server entry for server with id " + info.currentServer);
            }
        }
        if (this.client != null && !this.done) {
            if (System.currentTimeMillis() > this.timeOutAt) {
                PasswordTransfer.logger2.log(Level.INFO, "PT timeout " + PasswordTransfer.num + ", name: " + this.name + ", wurmid: " + this.wurmid + ", timestamp: " + this.timestamp);
                this.done = true;
            }
            if (this.client.loggedIn && !this.done && !this.sentTransfer) {
                try {
                    this.client.executePasswordUpdate(this.wurmid, this.newPassword, this.timestamp);
                    this.timeOutAt = System.currentTimeMillis() + this.timeOutTime;
                    this.sentTransfer = true;
                }
                catch (IOException iox) {
                    PasswordTransfer.logger.log(Level.WARNING, iox.getMessage(), iox);
                    this.done = true;
                }
            }
            if (!this.done) {
                try {
                    this.client.update();
                }
                catch (Exception ex) {
                    this.done = true;
                }
            }
        }
        if (this.done && this.client != null) {
            this.sentTransfer = false;
            this.client.disconnect("Done");
            this.client = null;
            PasswordTransfer.logger2.log(Level.INFO, "PT Disconnected " + PasswordTransfer.num + ", name: " + this.name + ", wurmid: " + this.wurmid + ", timestamp: " + this.timestamp);
        }
        if (this.done) {
            PasswordTransfer.logger2.log(Level.INFO, "PT finishing " + PasswordTransfer.num + ", name: " + this.name + ", wurmid: " + this.wurmid + ", timestamp: " + this.timestamp);
        }
        return this.done;
    }
    
    @Override
    public void reschedule(final IntraClient aClient) {
        this.done = true;
    }
    
    @Override
    public void remove(final IntraClient aClient) {
        this.done = true;
    }
    
    @Override
    public void commandExecuted(final IntraClient aClient) {
        this.delete();
        PasswordTransfer.logger2.log(Level.INFO, "PT accepted " + PasswordTransfer.num + ", name: " + this.name + ", wurmid: " + this.wurmid + ", timestamp: " + this.timestamp);
        this.done = true;
    }
    
    @Override
    public void commandFailed(final IntraClient aClient) {
        this.done = true;
    }
    
    @Override
    public void dataReceived(final IntraClient aClient) {
        this.done = true;
    }
    
    @Override
    public void receivingData(final ByteBuffer buffer) {
        this.done = true;
    }
    
    static {
        PasswordTransfer.logger = Logger.getLogger(PasswordTransfer.class.getName());
        transfers = new LinkedList<PasswordTransfer>();
    }
}
