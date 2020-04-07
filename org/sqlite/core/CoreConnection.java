// 
// Decompiled by Procyon v0.5.30
// 

package org.sqlite.core;

import java.util.TreeSet;
import java.util.HashMap;
import java.io.InputStream;
import java.io.FileOutputStream;
import java.net.URISyntaxException;
import org.sqlite.SQLiteConnection;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.SQLException;
import java.sql.Connection;
import java.util.Properties;
import org.sqlite.date.FastDateFormat;
import java.util.Set;
import java.util.Map;
import org.sqlite.SQLiteConfig;

public abstract class CoreConnection
{
    private static final String RESOURCE_NAME_PREFIX = ":resource:";
    private final String url;
    private String fileName;
    protected DB db;
    protected CoreDatabaseMetaData meta;
    protected boolean autoCommit;
    protected int transactionIsolation;
    private int busyTimeout;
    protected final int openModeFlags;
    protected SQLiteConfig.TransactionMode transactionMode;
    protected static final Map<SQLiteConfig.TransactionMode, String> beginCommandMap;
    private static final Set<String> pragmaSet;
    public final SQLiteConfig.DateClass dateClass;
    public final SQLiteConfig.DatePrecision datePrecision;
    public final long dateMultiplier;
    public final FastDateFormat dateFormat;
    public final String dateStringFormat;
    
    protected CoreConnection(final String url, final String fileName, final Properties prop) throws SQLException {
        this.db = null;
        this.meta = null;
        this.autoCommit = true;
        this.transactionIsolation = 8;
        this.busyTimeout = 0;
        this.transactionMode = SQLiteConfig.TransactionMode.DEFFERED;
        this.url = url;
        this.fileName = this.extractPragmasFromFilename(fileName, prop);
        final SQLiteConfig config = new SQLiteConfig(prop);
        this.dateClass = config.dateClass;
        this.dateMultiplier = config.dateMultiplier;
        this.dateFormat = FastDateFormat.getInstance(config.dateStringFormat);
        this.dateStringFormat = config.dateStringFormat;
        this.datePrecision = config.datePrecision;
        this.transactionMode = config.getTransactionMode();
        this.open(this.openModeFlags = config.getOpenModeFlags(), config.busyTimeout);
        if (fileName.startsWith("file:") && !fileName.contains("cache=")) {
            this.db.shared_cache(config.isEnabledSharedCache());
        }
        this.db.enable_load_extension(config.isEnabledLoadExtension());
        config.apply((Connection)this);
    }
    
    private String extractPragmasFromFilename(final String filename, final Properties prop) throws SQLException {
        final int parameterDelimiter = filename.indexOf(63);
        if (parameterDelimiter == -1) {
            return filename;
        }
        final StringBuilder sb = new StringBuilder();
        sb.append(filename.substring(0, parameterDelimiter));
        int nonPragmaCount = 0;
        final String[] parameters = filename.substring(parameterDelimiter + 1).split("&");
        for (int i = 0; i < parameters.length; ++i) {
            final String parameter = parameters[parameters.length - 1 - i].trim();
            if (!parameter.isEmpty()) {
                final String[] kvp = parameter.split("=");
                final String key = kvp[0].trim().toLowerCase();
                if (CoreConnection.pragmaSet.contains(key)) {
                    if (kvp.length == 1) {
                        throw new SQLException(String.format("Please specify a value for PRAGMA %s in URL %s", key, this.url));
                    }
                    final String value = kvp[1].trim();
                    if (!value.isEmpty()) {
                        if (!prop.containsKey(key)) {
                            prop.setProperty(key, value);
                        }
                    }
                }
                else {
                    sb.append((nonPragmaCount == 0) ? '?' : '&');
                    sb.append(parameter);
                    ++nonPragmaCount;
                }
            }
        }
        final String newFilename = sb.toString();
        return newFilename;
    }
    
    private void open(final int openModeFlags, final int busyTimeout) throws SQLException {
        if (!":memory:".equals(this.fileName) && !this.fileName.startsWith("file:") && !this.fileName.contains("mode=memory")) {
            if (this.fileName.startsWith(":resource:")) {
                final String resourceName = this.fileName.substring(":resource:".length());
                final ClassLoader contextCL = Thread.currentThread().getContextClassLoader();
                URL resourceAddr = contextCL.getResource(resourceName);
                if (resourceAddr == null) {
                    try {
                        resourceAddr = new URL(resourceName);
                    }
                    catch (MalformedURLException e) {
                        throw new SQLException(String.format("resource %s not found: %s", resourceName, e));
                    }
                }
                try {
                    this.fileName = this.extractResource(resourceAddr).getAbsolutePath();
                }
                catch (IOException e2) {
                    throw new SQLException(String.format("failed to load %s: %s", resourceName, e2));
                }
            }
            else {
                final File file = new File(this.fileName).getAbsoluteFile();
                File parent = file.getParentFile();
                if (parent != null && !parent.exists()) {
                    for (File up = parent; up != null && !up.exists(); up = up.getParentFile()) {
                        parent = up;
                    }
                    throw new SQLException("path to '" + this.fileName + "': '" + parent + "' does not exist");
                }
                try {
                    if (!file.exists() && file.createNewFile()) {
                        file.delete();
                    }
                }
                catch (Exception e3) {
                    throw new SQLException("opening db: '" + this.fileName + "': " + e3.getMessage());
                }
                this.fileName = file.getAbsolutePath();
            }
        }
        try {
            NativeDB.load();
            this.db = new NativeDB();
        }
        catch (Exception e4) {
            final SQLException err = new SQLException("Error opening connection");
            err.initCause(e4);
            throw err;
        }
        this.db.open((SQLiteConnection)this, this.fileName, openModeFlags);
        this.setBusyTimeout(busyTimeout);
    }
    
    private File extractResource(final URL resourceAddr) throws IOException {
        if (resourceAddr.getProtocol().equals("file")) {
            try {
                return new File(resourceAddr.toURI());
            }
            catch (URISyntaxException e) {
                throw new IOException(e.getMessage());
            }
        }
        final String tempFolder = new File(System.getProperty("java.io.tmpdir")).getAbsolutePath();
        final String dbFileName = String.format("sqlite-jdbc-tmp-%d.db", resourceAddr.hashCode());
        final File dbFile = new File(tempFolder, dbFileName);
        if (dbFile.exists()) {
            final long resourceLastModified = resourceAddr.openConnection().getLastModified();
            final long tmpFileLastModified = dbFile.lastModified();
            if (resourceLastModified < tmpFileLastModified) {
                return dbFile;
            }
            final boolean deletionSucceeded = dbFile.delete();
            if (!deletionSucceeded) {
                throw new IOException("failed to remove existing DB file: " + dbFile.getAbsolutePath());
            }
        }
        final byte[] buffer = new byte[8192];
        final FileOutputStream writer = new FileOutputStream(dbFile);
        final InputStream reader = resourceAddr.openStream();
        try {
            int bytesRead = 0;
            while ((bytesRead = reader.read(buffer)) != -1) {
                writer.write(buffer, 0, bytesRead);
            }
            return dbFile;
        }
        finally {
            writer.close();
            reader.close();
        }
    }
    
    public int getBusyTimeout() {
        return this.busyTimeout;
    }
    
    public void setBusyTimeout(final int milliseconds) throws SQLException {
        this.busyTimeout = milliseconds;
        this.db.busy_timeout(this.busyTimeout);
    }
    
    public String url() {
        return this.url;
    }
    
    public String libversion() throws SQLException {
        this.checkOpen();
        return this.db.libversion();
    }
    
    public DB db() {
        return this.db;
    }
    
    protected void checkOpen() throws SQLException {
        if (this.db == null) {
            throw new SQLException("database connection closed");
        }
    }
    
    protected void checkCursor(final int rst, final int rsc, final int rsh) throws SQLException {
        if (rst != 1003) {
            throw new SQLException("SQLite only supports TYPE_FORWARD_ONLY cursors");
        }
        if (rsc != 1007) {
            throw new SQLException("SQLite only supports CONCUR_READ_ONLY cursors");
        }
        if (rsh != 2) {
            throw new SQLException("SQLite only supports closing cursors at commit");
        }
    }
    
    protected void setTransactionMode(final SQLiteConfig.TransactionMode mode) {
        this.transactionMode = mode;
    }
    
    public String getDriverVersion() {
        return (this.db != null) ? "native" : "unloaded";
    }
    
    public void finalize() throws SQLException {
        this.close();
    }
    
    public void close() throws SQLException {
        if (this.db == null) {
            return;
        }
        if (this.meta != null) {
            this.meta.close();
        }
        this.db.close();
        this.db = null;
    }
    
    static {
        beginCommandMap = new HashMap<SQLiteConfig.TransactionMode, String>();
        pragmaSet = new TreeSet<String>();
        CoreConnection.beginCommandMap.put(SQLiteConfig.TransactionMode.DEFFERED, "begin;");
        CoreConnection.beginCommandMap.put(SQLiteConfig.TransactionMode.IMMEDIATE, "begin immediate;");
        CoreConnection.beginCommandMap.put(SQLiteConfig.TransactionMode.EXCLUSIVE, "begin exclusive;");
        for (final SQLiteConfig.Pragma pragma : SQLiteConfig.Pragma.values()) {
            CoreConnection.pragmaSet.add(pragma.pragmaName);
        }
    }
}
