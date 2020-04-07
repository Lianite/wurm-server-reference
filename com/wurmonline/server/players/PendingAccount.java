// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.players;

import java.util.HashMap;
import com.wurmonline.server.webinterface.WebInterfaceImpl;
import com.wurmonline.server.GeneralUtilities;
import java.net.URLEncoder;
import com.wurmonline.server.Mailer;
import java.util.Iterator;
import java.util.Set;
import java.util.HashSet;
import java.sql.ResultSet;
import java.sql.PreparedStatement;
import java.sql.Connection;
import java.sql.Statement;
import com.wurmonline.server.utils.DbUtilities;
import java.sql.SQLException;
import java.io.IOException;
import java.util.logging.Level;
import com.wurmonline.server.DbConnector;
import java.util.Map;
import java.util.logging.Logger;

public class PendingAccount
{
    private static final String GET_ALL_PENDING_ACCOUNTS = "SELECT * FROM PENDINGACCOUNTS";
    private static final String CREATE_PENDING_ACCOUNT = "INSERT INTO PENDINGACCOUNTS(NAME,EMAIL,EXPIRATIONDATE,HASH) VALUES(?,?,?,?)";
    private static final String DELETE_PENDING_ACCOUNT = "DELETE FROM PENDINGACCOUNTS WHERE NAME=?";
    private static final Logger logger;
    public static final Map<String, PendingAccount> accounts;
    public String accountName;
    public String emailAddress;
    public long expiration;
    public String password;
    
    public PendingAccount() {
        this.accountName = "Unknown";
        this.emailAddress = "";
        this.expiration = 0L;
        this.password = "";
    }
    
    public static void loadAllPendingAccounts() throws IOException {
        final long start = System.nanoTime();
        Connection dbcon = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            dbcon = DbConnector.getLoginDbCon();
            ps = dbcon.prepareStatement("SELECT * FROM PENDINGACCOUNTS");
            rs = ps.executeQuery();
            while (rs.next()) {
                final PendingAccount pacc = new PendingAccount();
                pacc.accountName = rs.getString("NAME");
                pacc.emailAddress = rs.getString("EMAIL");
                pacc.expiration = rs.getLong("EXPIRATIONDATE");
                pacc.password = rs.getString("HASH");
                if (System.currentTimeMillis() > pacc.expiration) {
                    pacc.delete(dbcon);
                }
                else {
                    addPendingAccount(pacc);
                }
            }
        }
        catch (SQLException sqex) {
            PendingAccount.logger.log(Level.WARNING, sqex.getMessage(), sqex);
            throw new IOException(sqex);
        }
        finally {
            DbUtilities.closeDatabaseObjects(ps, rs);
            DbConnector.returnConnection(dbcon);
            final long end = System.nanoTime();
            PendingAccount.logger.info("Loaded " + PendingAccount.accounts.size() + " pending accounts from the database took " + (end - start) / 1000000.0f + " ms");
        }
    }
    
    public void delete() {
        Connection dbcon = null;
        try {
            dbcon = DbConnector.getLoginDbCon();
            this.delete(dbcon);
        }
        catch (SQLException sqex) {
            PendingAccount.logger.log(Level.WARNING, "Failed to delete pending account " + this.accountName, sqex);
        }
        finally {
            DbConnector.returnConnection(dbcon);
        }
    }
    
    private void delete(final Connection dbcon) {
        PreparedStatement ps = null;
        try {
            ps = dbcon.prepareStatement("DELETE FROM PENDINGACCOUNTS WHERE NAME=?");
            ps.setString(1, this.accountName);
            ps.executeUpdate();
        }
        catch (SQLException sqex) {
            PendingAccount.logger.log(Level.WARNING, "Failed to delete pending account " + this.accountName, sqex);
        }
        finally {
            DbUtilities.closeDatabaseObjects(ps, null);
        }
        PendingAccount.accounts.remove(this.accountName);
    }
    
    private static boolean addPendingAccount(final PendingAccount acc) {
        if (PendingAccount.accounts.containsKey(acc.accountName)) {
            return false;
        }
        PendingAccount.accounts.put(acc.accountName, acc);
        return true;
    }
    
    public boolean create() {
        Connection dbcon = null;
        PreparedStatement ps = null;
        try {
            dbcon = DbConnector.getLoginDbCon();
            ps = dbcon.prepareStatement("INSERT INTO PENDINGACCOUNTS(NAME,EMAIL,EXPIRATIONDATE,HASH) VALUES(?,?,?,?)");
            ps.setString(1, this.accountName);
            ps.setString(2, this.emailAddress);
            ps.setLong(3, this.expiration);
            ps.setString(4, this.password);
            ps.executeUpdate();
        }
        catch (SQLException sqex) {
            PendingAccount.logger.log(Level.WARNING, "Failed to add pending account " + this.accountName, sqex);
            return false;
        }
        finally {
            DbUtilities.closeDatabaseObjects(ps, null);
            DbConnector.returnConnection(dbcon);
        }
        return addPendingAccount(this);
    }
    
    public static boolean doesPlayerExist(final String name) {
        return PendingAccount.accounts.containsKey(name);
    }
    
    public static void poll() {
        Connection dbcon = null;
        try {
            dbcon = DbConnector.getLoginDbCon();
            final PendingAccount[] paddarr = PendingAccount.accounts.values().toArray(new PendingAccount[PendingAccount.accounts.size()]);
            for (int x = 0; x < paddarr.length; ++x) {
                if (paddarr[x].expiration < System.currentTimeMillis()) {
                    paddarr[x].delete(dbcon);
                }
            }
        }
        catch (SQLException sqx) {
            PendingAccount.logger.log(Level.WARNING, "Failed to delete pending accounts. " + sqx.getMessage(), sqx);
        }
        finally {
            DbConnector.returnConnection(dbcon);
        }
    }
    
    public static String[] getAccountsForEmail(final String email) {
        final Set<String> set = new HashSet<String>();
        for (final PendingAccount info : PendingAccount.accounts.values()) {
            if (info.emailAddress.equals(email)) {
                set.add(info.accountName);
            }
        }
        return set.toArray(new String[set.size()]);
    }
    
    public static PendingAccount getAccount(final String name) {
        return PendingAccount.accounts.get(name);
    }
    
    public static final void resendMails(final String contains) {
        final PendingAccount[] paddarr = PendingAccount.accounts.values().toArray(new PendingAccount[PendingAccount.accounts.size()]);
        for (int x = 0; x < paddarr.length; ++x) {
            if (contains != null) {
                if (!paddarr[x].emailAddress.contains(contains)) {
                    continue;
                }
            }
            try {
                String email = Mailer.getPhaseOneMail();
                email = email.replace("@pname", paddarr[x].accountName);
                email = email.replace("@email", URLEncoder.encode(paddarr[x].emailAddress, "UTF-8"));
                email = email.replace("@expiration", GeneralUtilities.toGMTString(paddarr[x].expiration));
                email = email.replace("@password", paddarr[x].password);
                Mailer.sendMail(WebInterfaceImpl.mailAccount, paddarr[x].emailAddress, "Wurm Online character creation request", email);
                PendingAccount.logger.log(Level.INFO, "Resent " + paddarr[x].emailAddress + " for " + paddarr[x].accountName);
            }
            catch (Exception ex) {}
        }
    }
    
    static {
        logger = Logger.getLogger(PendingAccount.class.getName());
        accounts = new HashMap<String, PendingAccount>();
    }
}
