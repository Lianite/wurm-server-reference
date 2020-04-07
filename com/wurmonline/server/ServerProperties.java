// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server;

import java.util.Hashtable;
import com.wurmonline.server.steam.SteamHandler;
import java.sql.ResultSet;
import java.sql.PreparedStatement;
import java.sql.Connection;
import java.sql.Statement;
import com.wurmonline.server.utils.DbUtilities;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.Properties;

public class ServerProperties
{
    private static final String loadAll = "SELECT * FROM SERVERPROPERTIES";
    private static final String insert = "INSERT INTO SERVERPROPERTIES(PROPVAL,PROPKEY) VALUES (?,?)";
    private static final String update = "UPDATE SERVERPROPERTIES SET PROPVAL=? WHERE PROPKEY=?";
    private static final String createTable = "CREATE TABLE IF NOT EXISTS SERVERPROPERTIES        (            PROPKEY                 VARCHAR(50)   NOT NULL DEFAULT '',            PROPVAL                 VARCHAR(50)   NOT NULL DEFAULT ''        )";
    private static final Properties props;
    public static final String STEAMQUERY = "STEAMQUERYPORT";
    public static final String NPCS = "NPCS";
    public static final String ADMIN_PASSWORD = "ADMINPASSWORD";
    public static final String ENDGAMEITEMS = "ENDGAMEITEMS";
    public static final String SPY_PREVENTION = "SPYPREVENTION";
    public static final String AUTO_NETWORKING = "AUTO_NETWORKING";
    public static final String ENABLE_PNP_PORT_FORWARD = "ENABLE_PNP_PORT_FORWARD";
    public static final String NEWBIE_FRIENDLY = "NEWBIEFRIENDLY";
    private static final Logger logger;
    
    public static final void loadProperties() {
        checkIfCreateTable();
        Connection dbcon = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            dbcon = DbConnector.getLoginDbCon();
            ps = dbcon.prepareStatement("SELECT * FROM SERVERPROPERTIES");
            rs = ps.executeQuery();
            while (rs.next()) {
                final String key = rs.getString("PROPKEY");
                final String value = rs.getString("PROPVAL");
                ((Hashtable<String, String>)ServerProperties.props).put(key, value);
            }
        }
        catch (SQLException sqex) {
            ServerProperties.logger.log(Level.WARNING, "Failed to load properties!" + sqex.getMessage(), sqex);
        }
        finally {
            DbUtilities.closeDatabaseObjects(ps, rs);
            DbConnector.returnConnection(dbcon);
        }
        checkProperties();
    }
    
    public static final void checkProperties() {
        final String sqp = ServerProperties.props.getProperty("STEAMQUERYPORT");
        if (sqp == null) {
            setValue("STEAMQUERYPORT", Short.toString(SteamHandler.steamQueryPort));
        }
        else {
            SteamHandler.steamQueryPort = getShort("STEAMQUERYPORT", SteamHandler.steamQueryPort);
        }
        final String npcs = ServerProperties.props.getProperty("NPCS");
        if (npcs == null) {
            setValue("NPCS", Boolean.toString(Constants.loadNpcs));
        }
        else {
            Constants.loadNpcs = getBoolean("NPCS", Constants.loadNpcs);
        }
        final String egi = ServerProperties.props.getProperty("ENDGAMEITEMS");
        if (egi == null) {
            setValue("ENDGAMEITEMS", Boolean.toString(Constants.loadEndGameItems));
        }
        else {
            Constants.loadEndGameItems = getBoolean("ENDGAMEITEMS", Constants.loadEndGameItems);
        }
        final String spy = ServerProperties.props.getProperty("SPYPREVENTION");
        if (spy == null) {
            setValue("SPYPREVENTION", Boolean.toString(Constants.enableSpyPrevention));
        }
        else {
            Constants.enableSpyPrevention = getBoolean("SPYPREVENTION", Constants.enableSpyPrevention);
        }
        final String newbie = ServerProperties.props.getProperty("NEWBIEFRIENDLY");
        if (newbie == null) {
            setValue("NEWBIEFRIENDLY", Boolean.toString(Constants.isNewbieFriendly));
        }
        else {
            Constants.isNewbieFriendly = getBoolean("NEWBIEFRIENDLY", Constants.isNewbieFriendly);
        }
        final String autoNet = ServerProperties.props.getProperty("AUTO_NETWORKING");
        if (autoNet == null) {
            setValue("AUTO_NETWORKING", Boolean.toString(Constants.enableAutoNetworking));
        }
        else {
            Constants.enableAutoNetworking = getBoolean("AUTO_NETWORKING", Constants.enableAutoNetworking);
        }
        final String pnpPF = ServerProperties.props.getProperty("ENABLE_PNP_PORT_FORWARD");
        if (pnpPF == null) {
            setValue("ENABLE_PNP_PORT_FORWARD", Boolean.toString(Constants.enablePnpPortForward));
        }
        else {
            Constants.enablePnpPortForward = getBoolean("ENABLE_PNP_PORT_FORWARD", Constants.enablePnpPortForward);
        }
    }
    
    private static final void checkIfCreateTable() {
        Connection dbcon = null;
        PreparedStatement ps = null;
        try {
            dbcon = DbConnector.getLoginDbCon();
            ps = dbcon.prepareStatement("CREATE TABLE IF NOT EXISTS SERVERPROPERTIES        (            PROPKEY                 VARCHAR(50)   NOT NULL DEFAULT '',            PROPVAL                 VARCHAR(50)   NOT NULL DEFAULT ''        )");
            ps.execute();
            ServerProperties.logger.info("Created properties table in the database");
        }
        catch (SQLException sqex) {
            ServerProperties.logger.log(Level.WARNING, "Failed to create properties table!" + sqex.getMessage(), sqex);
        }
        finally {
            DbUtilities.closeDatabaseObjects(ps, null);
            DbConnector.returnConnection(dbcon);
        }
    }
    
    public static final void setValue(final String key, final String value) {
        Connection dbcon = null;
        PreparedStatement ps = null;
        try {
            dbcon = DbConnector.getLoginDbCon();
            if (ServerProperties.props.containsKey(key)) {
                ps = dbcon.prepareStatement("UPDATE SERVERPROPERTIES SET PROPVAL=? WHERE PROPKEY=?");
            }
            else {
                ps = dbcon.prepareStatement("INSERT INTO SERVERPROPERTIES(PROPVAL,PROPKEY) VALUES (?,?)");
            }
            ps.setString(1, value);
            ps.setString(2, key);
            ps.execute();
        }
        catch (SQLException sqex) {
            ServerProperties.logger.log(Level.WARNING, "Failed to update property " + key + ":" + value + ", " + sqex.getMessage(), sqex);
        }
        finally {
            DbUtilities.closeDatabaseObjects(ps, null);
            DbConnector.returnConnection(dbcon);
        }
        ((Hashtable<String, String>)ServerProperties.props).put(key, value);
    }
    
    public static boolean getBoolean(final String key, final boolean defaultValue) {
        final String maybeBoolean = ServerProperties.props.getProperty(key);
        return (maybeBoolean == null) ? defaultValue : Boolean.parseBoolean(maybeBoolean);
    }
    
    public static int getInt(final String key, final int defaultValue) {
        final String maybeInt = ServerProperties.props.getProperty(key);
        if (maybeInt == null) {
            return defaultValue;
        }
        try {
            return Integer.parseInt(maybeInt);
        }
        catch (NumberFormatException e) {
            return defaultValue;
        }
    }
    
    public static long getLong(final String key, final long defaultValue) {
        final String maybeLong = ServerProperties.props.getProperty(key);
        if (maybeLong == null) {
            return defaultValue;
        }
        try {
            return Long.parseLong(maybeLong);
        }
        catch (NumberFormatException e) {
            return defaultValue;
        }
    }
    
    public static short getShort(final String key, final short defaultValue) {
        final String maybeShort = ServerProperties.props.getProperty(key);
        if (maybeShort == null) {
            return defaultValue;
        }
        try {
            return Short.parseShort(maybeShort);
        }
        catch (NumberFormatException e) {
            return defaultValue;
        }
    }
    
    public static String getString(final String key, final String defaultValue) {
        final String maybeString = ServerProperties.props.getProperty(key);
        return (maybeString == null) ? defaultValue : maybeString;
    }
    
    static {
        props = new Properties();
        logger = Logger.getLogger(Servers.class.getName());
    }
}
