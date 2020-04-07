// 
// Decompiled by Procyon v0.5.30
// 

package org.sqlite;

import java.util.Hashtable;
import java.sql.DriverPropertyInfo;
import java.util.Iterator;
import java.sql.Statement;
import java.util.HashSet;
import java.sql.SQLException;
import java.sql.Connection;
import java.util.Properties;

public class SQLiteConfig
{
    private final Properties pragmaTable;
    private int openModeFlag;
    private TransactionMode transactionMode;
    public final int busyTimeout;
    public static final String DEFAULT_DATE_STRING_FORMAT = "yyyy-MM-dd HH:mm:ss.SSS";
    public DateClass dateClass;
    public DatePrecision datePrecision;
    public long dateMultiplier;
    public String dateStringFormat;
    private static final String[] OnOff;
    
    public SQLiteConfig() {
        this(new Properties());
    }
    
    public SQLiteConfig(final Properties prop) {
        this.openModeFlag = 0;
        this.pragmaTable = prop;
        String openMode = this.pragmaTable.getProperty(Pragma.OPEN_MODE.pragmaName);
        if (openMode != null) {
            this.openModeFlag = Integer.parseInt(openMode);
        }
        else {
            this.setOpenMode(SQLiteOpenMode.READWRITE);
            this.setOpenMode(SQLiteOpenMode.CREATE);
        }
        openMode = this.pragmaTable.getProperty(Pragma.SHARED_CACHE.pragmaName);
        this.setOpenMode(SQLiteOpenMode.OPEN_URI);
        this.transactionMode = TransactionMode.getMode(this.pragmaTable.getProperty(Pragma.TRANSACTION_MODE.pragmaName, TransactionMode.DEFFERED.name()));
        this.dateClass = DateClass.getDateClass(this.pragmaTable.getProperty(Pragma.DATE_CLASS.pragmaName, DateClass.INTEGER.name()));
        this.datePrecision = DatePrecision.getPrecision(this.pragmaTable.getProperty(Pragma.DATE_PRECISION.pragmaName, DatePrecision.MILLISECONDS.name()));
        this.dateMultiplier = ((this.datePrecision == DatePrecision.MILLISECONDS) ? 1L : 1000L);
        this.dateStringFormat = this.pragmaTable.getProperty(Pragma.DATE_STRING_FORMAT.pragmaName, "yyyy-MM-dd HH:mm:ss.SSS");
        this.busyTimeout = Integer.parseInt(this.pragmaTable.getProperty(Pragma.BUSY_TIMEOUT.pragmaName, "3000"));
    }
    
    public Connection createConnection(final String url) throws SQLException {
        return JDBC.createConnection(url, this.toProperties());
    }
    
    public void apply(final Connection conn) throws SQLException {
        final HashSet<String> pragmaParams = new HashSet<String>();
        for (final Pragma each : Pragma.values()) {
            pragmaParams.add(each.pragmaName);
        }
        pragmaParams.remove(Pragma.OPEN_MODE.pragmaName);
        pragmaParams.remove(Pragma.SHARED_CACHE.pragmaName);
        pragmaParams.remove(Pragma.LOAD_EXTENSION.pragmaName);
        pragmaParams.remove(Pragma.DATE_PRECISION.pragmaName);
        pragmaParams.remove(Pragma.DATE_CLASS.pragmaName);
        pragmaParams.remove(Pragma.DATE_STRING_FORMAT.pragmaName);
        final Statement stat = conn.createStatement();
        try {
            for (final Object each2 : ((Hashtable<Object, V>)this.pragmaTable).keySet()) {
                final String key = each2.toString();
                if (!pragmaParams.contains(key)) {
                    continue;
                }
                final String value = this.pragmaTable.getProperty(key);
                if (value == null) {
                    continue;
                }
                stat.execute(String.format("pragma %s=%s", key, value));
            }
        }
        finally {
            if (stat != null) {
                stat.close();
            }
        }
    }
    
    private void set(final Pragma pragma, final boolean flag) {
        this.setPragma(pragma, Boolean.toString(flag));
    }
    
    private void set(final Pragma pragma, final int num) {
        this.setPragma(pragma, Integer.toString(num));
    }
    
    private boolean getBoolean(final Pragma pragma, final String defaultValue) {
        return Boolean.parseBoolean(this.pragmaTable.getProperty(pragma.pragmaName, defaultValue));
    }
    
    public boolean isEnabledSharedCache() {
        return this.getBoolean(Pragma.SHARED_CACHE, "false");
    }
    
    public boolean isEnabledLoadExtension() {
        return this.getBoolean(Pragma.LOAD_EXTENSION, "false");
    }
    
    public int getOpenModeFlags() {
        return this.openModeFlag;
    }
    
    public void setPragma(final Pragma pragma, final String value) {
        ((Hashtable<String, String>)this.pragmaTable).put(pragma.pragmaName, value);
    }
    
    public Properties toProperties() {
        this.pragmaTable.setProperty(Pragma.OPEN_MODE.pragmaName, Integer.toString(this.openModeFlag));
        this.pragmaTable.setProperty(Pragma.TRANSACTION_MODE.pragmaName, this.transactionMode.getValue());
        this.pragmaTable.setProperty(Pragma.DATE_CLASS.pragmaName, this.dateClass.getValue());
        this.pragmaTable.setProperty(Pragma.DATE_PRECISION.pragmaName, this.datePrecision.getValue());
        this.pragmaTable.setProperty(Pragma.DATE_STRING_FORMAT.pragmaName, this.dateStringFormat);
        return this.pragmaTable;
    }
    
    static DriverPropertyInfo[] getDriverPropertyInfo() {
        final Pragma[] pragma = Pragma.values();
        final DriverPropertyInfo[] result = new DriverPropertyInfo[pragma.length];
        int index = 0;
        for (final Pragma p : Pragma.values()) {
            final DriverPropertyInfo di = new DriverPropertyInfo(p.pragmaName, null);
            di.choices = p.choices;
            di.description = p.description;
            di.required = false;
            result[index++] = di;
        }
        return result;
    }
    
    public void setOpenMode(final SQLiteOpenMode mode) {
        this.openModeFlag |= mode.flag;
    }
    
    public void resetOpenMode(final SQLiteOpenMode mode) {
        this.openModeFlag &= ~mode.flag;
    }
    
    public void setSharedCache(final boolean enable) {
        this.set(Pragma.SHARED_CACHE, enable);
    }
    
    public void enableLoadExtension(final boolean enable) {
        this.set(Pragma.LOAD_EXTENSION, enable);
    }
    
    public void setReadOnly(final boolean readOnly) {
        if (readOnly) {
            this.setOpenMode(SQLiteOpenMode.READONLY);
            this.resetOpenMode(SQLiteOpenMode.CREATE);
            this.resetOpenMode(SQLiteOpenMode.READWRITE);
        }
        else {
            this.setOpenMode(SQLiteOpenMode.READWRITE);
            this.setOpenMode(SQLiteOpenMode.CREATE);
            this.resetOpenMode(SQLiteOpenMode.READONLY);
        }
    }
    
    public void setCacheSize(final int numberOfPages) {
        this.set(Pragma.CACHE_SIZE, numberOfPages);
    }
    
    public void enableCaseSensitiveLike(final boolean enable) {
        this.set(Pragma.CASE_SENSITIVE_LIKE, enable);
    }
    
    public void enableCountChanges(final boolean enable) {
        this.set(Pragma.COUNT_CHANGES, enable);
    }
    
    public void setDefaultCacheSize(final int numberOfPages) {
        this.set(Pragma.DEFAULT_CACHE_SIZE, numberOfPages);
    }
    
    public void enableEmptyResultCallBacks(final boolean enable) {
        this.set(Pragma.EMPTY_RESULT_CALLBACKS, enable);
    }
    
    private static String[] toStringArray(final PragmaValue[] list) {
        final String[] result = new String[list.length];
        for (int i = 0; i < list.length; ++i) {
            result[i] = list[i].getValue();
        }
        return result;
    }
    
    public void setEncoding(final Encoding encoding) {
        this.setPragma(Pragma.ENCODING, encoding.typeName);
    }
    
    public void enforceForeignKeys(final boolean enforce) {
        this.set(Pragma.FOREIGN_KEYS, enforce);
    }
    
    public void enableFullColumnNames(final boolean enable) {
        this.set(Pragma.FULL_COLUMN_NAMES, enable);
    }
    
    public void enableFullSync(final boolean enable) {
        this.set(Pragma.FULL_SYNC, enable);
    }
    
    public void incrementalVacuum(final int numberOfPagesToBeRemoved) {
        this.set(Pragma.INCREMENTAL_VACUUM, numberOfPagesToBeRemoved);
    }
    
    public void setJournalMode(final JournalMode mode) {
        this.setPragma(Pragma.JOURNAL_MODE, mode.name());
    }
    
    public void setJounalSizeLimit(final int limit) {
        this.set(Pragma.JOURNAL_SIZE_LIMIT, limit);
    }
    
    public void useLegacyFileFormat(final boolean use) {
        this.set(Pragma.LEGACY_FILE_FORMAT, use);
    }
    
    public void setLockingMode(final LockingMode mode) {
        this.setPragma(Pragma.LOCKING_MODE, mode.name());
    }
    
    public void setPageSize(final int numBytes) {
        this.set(Pragma.PAGE_SIZE, numBytes);
    }
    
    public void setMaxPageCount(final int numPages) {
        this.set(Pragma.MAX_PAGE_COUNT, numPages);
    }
    
    public void setReadUncommited(final boolean useReadUncommitedIsolationMode) {
        this.set(Pragma.READ_UNCOMMITED, useReadUncommitedIsolationMode);
    }
    
    public void enableRecursiveTriggers(final boolean enable) {
        this.set(Pragma.RECURSIVE_TRIGGERS, enable);
    }
    
    public void enableReverseUnorderedSelects(final boolean enable) {
        this.set(Pragma.REVERSE_UNORDERED_SELECTS, enable);
    }
    
    public void enableShortColumnNames(final boolean enable) {
        this.set(Pragma.SHORT_COLUMN_NAMES, enable);
    }
    
    public void setSynchronous(final SynchronousMode mode) {
        this.setPragma(Pragma.SYNCHRONOUS, mode.name());
    }
    
    public void setTempStore(final TempStore storeType) {
        this.setPragma(Pragma.TEMP_STORE, storeType.name());
    }
    
    public void setTempStoreDirectory(final String directoryName) {
        this.setPragma(Pragma.TEMP_STORE_DIRECTORY, String.format("'%s'", directoryName));
    }
    
    public void setUserVersion(final int version) {
        this.set(Pragma.USER_VERSION, version);
    }
    
    public void setTransactionMode(final TransactionMode transactionMode) {
        this.transactionMode = transactionMode;
    }
    
    public void setTransactionMode(final String transactionMode) {
        this.setTransactionMode(TransactionMode.getMode(transactionMode));
    }
    
    public TransactionMode getTransactionMode() {
        return this.transactionMode;
    }
    
    public void setDatePrecision(final String datePrecision) throws SQLException {
        this.datePrecision = DatePrecision.getPrecision(datePrecision);
    }
    
    public void setDateClass(final String dateClass) {
        this.dateClass = DateClass.getDateClass(dateClass);
    }
    
    public void setDateStringFormat(final String dateStringFormat) {
        this.dateStringFormat = dateStringFormat;
    }
    
    public void setBusyTimeout(final String milliseconds) {
        this.setPragma(Pragma.BUSY_TIMEOUT, milliseconds);
    }
    
    static {
        OnOff = new String[] { "true", "false" };
    }
    
    public enum Pragma
    {
        OPEN_MODE("open_mode", "Database open-mode flag", (String[])null), 
        SHARED_CACHE("shared_cache", "Enable SQLite Shared-Cache mode, native driver only", SQLiteConfig.OnOff), 
        LOAD_EXTENSION("enable_load_extension", "Enable SQLite load_extention() function, native driver only", SQLiteConfig.OnOff), 
        CACHE_SIZE("cache_size"), 
        CASE_SENSITIVE_LIKE("case_sensitive_like", SQLiteConfig.OnOff), 
        COUNT_CHANGES("count_changes", SQLiteConfig.OnOff), 
        DEFAULT_CACHE_SIZE("default_cache_size"), 
        EMPTY_RESULT_CALLBACKS("empty_result_callback", SQLiteConfig.OnOff), 
        ENCODING("encoding", toStringArray(Encoding.values())), 
        FOREIGN_KEYS("foreign_keys", SQLiteConfig.OnOff), 
        FULL_COLUMN_NAMES("full_column_names", SQLiteConfig.OnOff), 
        FULL_SYNC("fullsync", SQLiteConfig.OnOff), 
        INCREMENTAL_VACUUM("incremental_vacuum"), 
        JOURNAL_MODE("journal_mode", toStringArray(JournalMode.values())), 
        JOURNAL_SIZE_LIMIT("journal_size_limit"), 
        LEGACY_FILE_FORMAT("legacy_file_format", SQLiteConfig.OnOff), 
        LOCKING_MODE("locking_mode", toStringArray(LockingMode.values())), 
        PAGE_SIZE("page_size"), 
        MAX_PAGE_COUNT("max_page_count"), 
        READ_UNCOMMITED("read_uncommited", SQLiteConfig.OnOff), 
        RECURSIVE_TRIGGERS("recursive_triggers", SQLiteConfig.OnOff), 
        REVERSE_UNORDERED_SELECTS("reverse_unordered_selects", SQLiteConfig.OnOff), 
        SHORT_COLUMN_NAMES("short_column_names", SQLiteConfig.OnOff), 
        SYNCHRONOUS("synchronous", toStringArray(SynchronousMode.values())), 
        TEMP_STORE("temp_store", toStringArray(TempStore.values())), 
        TEMP_STORE_DIRECTORY("temp_store_directory"), 
        USER_VERSION("user_version"), 
        TRANSACTION_MODE("transaction_mode", toStringArray(TransactionMode.values())), 
        DATE_PRECISION("date_precision", "\"seconds\": Read and store integer dates as seconds from the Unix Epoch (SQLite standard).\n\"milliseconds\": (DEFAULT) Read and store integer dates as milliseconds from the Unix Epoch (Java standard).", toStringArray(DatePrecision.values())), 
        DATE_CLASS("date_class", "\"integer\": (Default) store dates as number of seconds or milliseconds from the Unix Epoch\n\"text\": store dates as a string of text\n\"real\": store dates as Julian Dates", toStringArray(DateClass.values())), 
        DATE_STRING_FORMAT("date_string_format", "Format to store and retrieve dates stored as text. Defaults to \"yyyy-MM-dd HH:mm:ss.SSS\"", (String[])null), 
        BUSY_TIMEOUT("busy_timeout", (String[])null);
        
        public final String pragmaName;
        public final String[] choices;
        public final String description;
        
        private Pragma(final String pragmaName) {
            this(pragmaName, null);
        }
        
        private Pragma(final String pragmaName, final String[] choices) {
            this(pragmaName, null, choices);
        }
        
        private Pragma(final String pragmaName, final String description, final String[] choices) {
            this.pragmaName = pragmaName;
            this.description = description;
            this.choices = choices;
        }
        
        public final String getPragmaName() {
            return this.pragmaName;
        }
    }
    
    public enum Encoding implements PragmaValue
    {
        UTF8("'UTF-8'"), 
        UTF16("'UTF-16'"), 
        UTF16_LITTLE_ENDIAN("'UTF-16le'"), 
        UTF16_BIG_ENDIAN("'UTF-16be'"), 
        UTF_8(Encoding.UTF8), 
        UTF_16(Encoding.UTF16), 
        UTF_16LE(Encoding.UTF16_LITTLE_ENDIAN), 
        UTF_16BE(Encoding.UTF16_BIG_ENDIAN);
        
        public final String typeName;
        
        private Encoding(final String typeName) {
            this.typeName = typeName;
        }
        
        private Encoding(final Encoding encoding) {
            this.typeName = encoding.getValue();
        }
        
        @Override
        public String getValue() {
            return this.typeName;
        }
        
        public static Encoding getEncoding(final String value) {
            return valueOf(value.replaceAll("-", "_").toUpperCase());
        }
    }
    
    public enum JournalMode implements PragmaValue
    {
        DELETE, 
        TRUNCATE, 
        PERSIST, 
        MEMORY, 
        WAL, 
        OFF;
        
        @Override
        public String getValue() {
            return this.name();
        }
    }
    
    public enum LockingMode implements PragmaValue
    {
        NORMAL, 
        EXCLUSIVE;
        
        @Override
        public String getValue() {
            return this.name();
        }
    }
    
    public enum SynchronousMode implements PragmaValue
    {
        OFF, 
        NORMAL, 
        FULL;
        
        @Override
        public String getValue() {
            return this.name();
        }
    }
    
    public enum TempStore implements PragmaValue
    {
        DEFAULT, 
        FILE, 
        MEMORY;
        
        @Override
        public String getValue() {
            return this.name();
        }
    }
    
    public enum TransactionMode implements PragmaValue
    {
        DEFFERED, 
        IMMEDIATE, 
        EXCLUSIVE;
        
        @Override
        public String getValue() {
            return this.name();
        }
        
        public static TransactionMode getMode(final String mode) {
            return valueOf(mode.toUpperCase());
        }
    }
    
    public enum DatePrecision implements PragmaValue
    {
        SECONDS, 
        MILLISECONDS;
        
        @Override
        public String getValue() {
            return this.name();
        }
        
        public static DatePrecision getPrecision(final String precision) {
            return valueOf(precision.toUpperCase());
        }
    }
    
    public enum DateClass implements PragmaValue
    {
        INTEGER, 
        TEXT, 
        REAL;
        
        @Override
        public String getValue() {
            return this.name();
        }
        
        public static DateClass getDateClass(final String dateClass) {
            return valueOf(dateClass.toUpperCase());
        }
    }
    
    private interface PragmaValue
    {
        String getValue();
    }
}
