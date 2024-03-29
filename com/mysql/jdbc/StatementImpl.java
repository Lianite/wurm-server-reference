// 
// Decompiled by Procyon v0.5.30
// 

package com.mysql.jdbc;

import com.mysql.jdbc.profiler.ProfilerEvent;
import java.math.BigInteger;
import java.util.GregorianCalendar;
import java.util.Calendar;
import java.sql.BatchUpdateException;
import com.mysql.jdbc.exceptions.MySQLStatementCancelledException;
import com.mysql.jdbc.exceptions.MySQLTimeoutException;
import java.util.TimerTask;
import java.sql.ResultSet;
import java.util.Iterator;
import java.sql.SQLException;
import com.mysql.jdbc.profiler.ProfilerEventHandlerFactory;
import java.util.HashSet;
import java.io.InputStream;
import java.util.ArrayList;
import java.sql.SQLWarning;
import java.util.Set;
import com.mysql.jdbc.profiler.ProfilerEventHandler;
import java.util.List;

public class StatementImpl implements Statement
{
    protected static final String PING_MARKER = "/* ping */";
    protected Object cancelTimeoutMutex;
    protected static int statementCounter;
    public static final byte USES_VARIABLES_FALSE = 0;
    public static final byte USES_VARIABLES_TRUE = 1;
    public static final byte USES_VARIABLES_UNKNOWN = -1;
    protected boolean wasCancelled;
    protected boolean wasCancelledByTimeout;
    protected List batchedArgs;
    protected SingleByteCharsetConverter charConverter;
    protected String charEncoding;
    protected ConnectionImpl connection;
    protected long connectionId;
    protected String currentCatalog;
    protected boolean doEscapeProcessing;
    protected ProfilerEventHandler eventSink;
    private int fetchSize;
    protected boolean isClosed;
    protected long lastInsertId;
    protected int maxFieldSize;
    protected int maxRows;
    protected boolean maxRowsChanged;
    protected Set openResults;
    protected boolean pedantic;
    protected Throwable pointOfOrigin;
    protected boolean profileSQL;
    protected ResultSetInternalMethods results;
    protected int resultSetConcurrency;
    protected int resultSetType;
    protected int statementId;
    protected int timeoutInMillis;
    protected long updateCount;
    protected boolean useUsageAdvisor;
    protected SQLWarning warningChain;
    protected boolean holdResultsOpenOverClose;
    protected ArrayList batchedGeneratedKeys;
    protected boolean retrieveGeneratedKeys;
    protected boolean continueBatchOnError;
    protected PingTarget pingTarget;
    protected boolean useLegacyDatetimeCode;
    private ExceptionInterceptor exceptionInterceptor;
    protected boolean lastQueryIsOnDupKeyUpdate;
    private int originalResultSetType;
    private int originalFetchSize;
    private boolean isPoolable;
    private InputStream localInfileInputStream;
    
    public StatementImpl(final ConnectionImpl c, final String catalog) throws SQLException {
        this.cancelTimeoutMutex = new Object();
        this.wasCancelled = false;
        this.wasCancelledByTimeout = false;
        this.charConverter = null;
        this.charEncoding = null;
        this.connection = null;
        this.connectionId = 0L;
        this.currentCatalog = null;
        this.doEscapeProcessing = true;
        this.eventSink = null;
        this.fetchSize = 0;
        this.isClosed = false;
        this.lastInsertId = -1L;
        this.maxFieldSize = MysqlIO.getMaxBuf();
        this.maxRows = -1;
        this.maxRowsChanged = false;
        this.openResults = new HashSet();
        this.pedantic = false;
        this.profileSQL = false;
        this.results = null;
        this.resultSetConcurrency = 0;
        this.resultSetType = 0;
        this.timeoutInMillis = 0;
        this.updateCount = -1L;
        this.useUsageAdvisor = false;
        this.warningChain = null;
        this.holdResultsOpenOverClose = false;
        this.batchedGeneratedKeys = null;
        this.retrieveGeneratedKeys = false;
        this.continueBatchOnError = false;
        this.pingTarget = null;
        this.lastQueryIsOnDupKeyUpdate = false;
        this.originalResultSetType = 0;
        this.originalFetchSize = 0;
        this.isPoolable = true;
        if (c == null || c.isClosed()) {
            throw SQLError.createSQLException(Messages.getString("Statement.0"), "08003", null);
        }
        this.connection = c;
        this.connectionId = this.connection.getId();
        this.exceptionInterceptor = c.getExceptionInterceptor();
        this.currentCatalog = catalog;
        this.pedantic = this.connection.getPedantic();
        this.continueBatchOnError = this.connection.getContinueBatchOnError();
        this.useLegacyDatetimeCode = this.connection.getUseLegacyDatetimeCode();
        if (!this.connection.getDontTrackOpenResources()) {
            this.connection.registerStatement(this);
        }
        if (this.connection != null) {
            this.maxFieldSize = this.connection.getMaxAllowedPacket();
            final int defaultFetchSize = this.connection.getDefaultFetchSize();
            if (defaultFetchSize != 0) {
                this.setFetchSize(defaultFetchSize);
            }
        }
        if (this.connection.getUseUnicode()) {
            this.charEncoding = this.connection.getEncoding();
            this.charConverter = this.connection.getCharsetConverter(this.charEncoding);
        }
        final boolean profiling = this.connection.getProfileSql() || this.connection.getUseUsageAdvisor() || this.connection.getLogSlowQueries();
        if (this.connection.getAutoGenerateTestcaseScript() || profiling) {
            this.statementId = StatementImpl.statementCounter++;
        }
        if (profiling) {
            this.pointOfOrigin = new Throwable();
            this.profileSQL = this.connection.getProfileSql();
            this.useUsageAdvisor = this.connection.getUseUsageAdvisor();
            this.eventSink = ProfilerEventHandlerFactory.getInstance(this.connection);
        }
        final int maxRowsConn = this.connection.getMaxRows();
        if (maxRowsConn != -1) {
            this.setMaxRows(maxRowsConn);
        }
        this.holdResultsOpenOverClose = this.connection.getHoldResultsOpenOverStatementClose();
    }
    
    public synchronized void addBatch(final String sql) throws SQLException {
        if (this.batchedArgs == null) {
            this.batchedArgs = new ArrayList();
        }
        if (sql != null) {
            this.batchedArgs.add(sql);
        }
    }
    
    public void cancel() throws SQLException {
        if (!this.isClosed && this.connection != null && this.connection.versionMeetsMinimum(5, 0, 0)) {
            Connection cancelConn = null;
            java.sql.Statement cancelStmt = null;
            try {
                cancelConn = this.connection.duplicate();
                cancelStmt = cancelConn.createStatement();
                cancelStmt.execute("KILL QUERY " + this.connection.getIO().getThreadId());
                this.wasCancelled = true;
            }
            finally {
                if (cancelStmt != null) {
                    cancelStmt.close();
                }
                if (cancelConn != null) {
                    cancelConn.close();
                }
            }
        }
    }
    
    protected void checkClosed() throws SQLException {
        if (this.isClosed) {
            throw SQLError.createSQLException(Messages.getString("Statement.49"), "08003", this.getExceptionInterceptor());
        }
    }
    
    protected void checkForDml(final String sql, final char firstStatementChar) throws SQLException {
        if (firstStatementChar == 'I' || firstStatementChar == 'U' || firstStatementChar == 'D' || firstStatementChar == 'A' || firstStatementChar == 'C') {
            final String noCommentSql = StringUtils.stripComments(sql, "'\"", "'\"", true, false, true, true);
            if (StringUtils.startsWithIgnoreCaseAndWs(noCommentSql, "INSERT") || StringUtils.startsWithIgnoreCaseAndWs(noCommentSql, "UPDATE") || StringUtils.startsWithIgnoreCaseAndWs(noCommentSql, "DELETE") || StringUtils.startsWithIgnoreCaseAndWs(noCommentSql, "DROP") || StringUtils.startsWithIgnoreCaseAndWs(noCommentSql, "CREATE") || StringUtils.startsWithIgnoreCaseAndWs(noCommentSql, "ALTER")) {
                throw SQLError.createSQLException(Messages.getString("Statement.57"), "S1009", this.getExceptionInterceptor());
            }
        }
    }
    
    protected void checkNullOrEmptyQuery(final String sql) throws SQLException {
        if (sql == null) {
            throw SQLError.createSQLException(Messages.getString("Statement.59"), "S1009", this.getExceptionInterceptor());
        }
        if (sql.length() == 0) {
            throw SQLError.createSQLException(Messages.getString("Statement.61"), "S1009", this.getExceptionInterceptor());
        }
    }
    
    public synchronized void clearBatch() throws SQLException {
        if (this.batchedArgs != null) {
            this.batchedArgs.clear();
        }
    }
    
    public void clearWarnings() throws SQLException {
        this.warningChain = null;
    }
    
    public synchronized void close() throws SQLException {
        this.realClose(true, true);
    }
    
    protected synchronized void closeAllOpenResults() {
        if (this.openResults != null) {
            for (final ResultSetInternalMethods element : this.openResults) {
                try {
                    element.realClose(false);
                }
                catch (SQLException sqlEx) {
                    AssertionFailedException.shouldNotHappen(sqlEx);
                }
            }
            this.openResults.clear();
        }
    }
    
    public synchronized void removeOpenResultSet(final ResultSet rs) {
        if (this.openResults != null) {
            this.openResults.remove(rs);
        }
    }
    
    public synchronized int getOpenResultSetCount() {
        if (this.openResults != null) {
            return this.openResults.size();
        }
        return 0;
    }
    
    private ResultSetInternalMethods createResultSetUsingServerFetch(final String sql) throws SQLException {
        final java.sql.PreparedStatement pStmt = this.connection.prepareStatement(sql, this.resultSetType, this.resultSetConcurrency);
        pStmt.setFetchSize(this.fetchSize);
        if (this.maxRows > -1) {
            pStmt.setMaxRows(this.maxRows);
        }
        pStmt.execute();
        final ResultSetInternalMethods rs = ((StatementImpl)pStmt).getResultSetInternal();
        rs.setStatementUsedForFetchingRows((PreparedStatement)pStmt);
        return this.results = rs;
    }
    
    protected boolean createStreamingResultSet() {
        return this.resultSetType == 1003 && this.resultSetConcurrency == 1007 && this.fetchSize == Integer.MIN_VALUE;
    }
    
    public void enableStreamingResults() throws SQLException {
        this.originalResultSetType = this.resultSetType;
        this.originalFetchSize = this.fetchSize;
        this.setFetchSize(Integer.MIN_VALUE);
        this.setResultSetType(1003);
    }
    
    public void disableStreamingResults() throws SQLException {
        if (this.fetchSize == Integer.MIN_VALUE && this.resultSetType == 1003) {
            this.setFetchSize(this.originalFetchSize);
            this.setResultSetType(this.originalResultSetType);
        }
    }
    
    public boolean execute(final String sql) throws SQLException {
        return this.execute(sql, false);
    }
    
    private boolean execute(String sql, final boolean returnGeneratedKeys) throws SQLException {
        this.checkClosed();
        final ConnectionImpl locallyScopedConn = this.connection;
        synchronized (locallyScopedConn.getMutex()) {
            this.retrieveGeneratedKeys = returnGeneratedKeys;
            this.lastQueryIsOnDupKeyUpdate = false;
            if (returnGeneratedKeys) {
                this.lastQueryIsOnDupKeyUpdate = this.containsOnDuplicateKeyInString(sql);
            }
            this.resetCancelledState();
            this.checkNullOrEmptyQuery(sql);
            this.checkClosed();
            final char firstNonWsChar = StringUtils.firstAlphaCharUc(sql, this.findStartOfStatement(sql));
            boolean isSelect = true;
            if (firstNonWsChar != 'S') {
                isSelect = false;
                if (locallyScopedConn.isReadOnly()) {
                    throw SQLError.createSQLException(Messages.getString("Statement.27") + Messages.getString("Statement.28"), "S1009", this.getExceptionInterceptor());
                }
            }
            final boolean doStreaming = this.createStreamingResultSet();
            if (doStreaming && this.connection.getNetTimeoutForStreamingResults() > 0) {
                this.executeSimpleNonQuery(locallyScopedConn, "SET net_write_timeout=" + this.connection.getNetTimeoutForStreamingResults());
            }
            if (this.doEscapeProcessing) {
                final Object escapedSqlResult = EscapeProcessor.escapeSQL(sql, locallyScopedConn.serverSupportsConvertFn(), locallyScopedConn);
                if (escapedSqlResult instanceof String) {
                    sql = (String)escapedSqlResult;
                }
                else {
                    sql = ((EscapeProcessorResult)escapedSqlResult).escapedSql;
                }
            }
            if (this.results != null && !locallyScopedConn.getHoldResultsOpenOverStatementClose()) {
                this.results.realClose(false);
            }
            if (sql.charAt(0) == '/' && sql.startsWith("/* ping */")) {
                this.doPingInstead();
                return true;
            }
            CachedResultSetMetaData cachedMetaData = null;
            ResultSetInternalMethods rs = null;
            this.batchedGeneratedKeys = null;
            if (this.useServerFetch()) {
                rs = this.createResultSetUsingServerFetch(sql);
            }
            else {
                CancelTask timeoutTask = null;
                String oldCatalog = null;
                try {
                    if (locallyScopedConn.getEnableQueryTimeouts() && this.timeoutInMillis != 0 && locallyScopedConn.versionMeetsMinimum(5, 0, 0)) {
                        timeoutTask = new CancelTask(this);
                        ConnectionImpl.getCancelTimer().schedule(timeoutTask, this.timeoutInMillis);
                    }
                    if (!locallyScopedConn.getCatalog().equals(this.currentCatalog)) {
                        oldCatalog = locallyScopedConn.getCatalog();
                        locallyScopedConn.setCatalog(this.currentCatalog);
                    }
                    Field[] cachedFields = null;
                    if (locallyScopedConn.getCacheResultSetMetadata()) {
                        cachedMetaData = locallyScopedConn.getCachedMetaData(sql);
                        if (cachedMetaData != null) {
                            cachedFields = cachedMetaData.fields;
                        }
                    }
                    if (locallyScopedConn.useMaxRows()) {
                        int rowLimit = -1;
                        if (isSelect) {
                            if (StringUtils.indexOfIgnoreCase(sql, "LIMIT") != -1) {
                                rowLimit = this.maxRows;
                            }
                            else if (this.maxRows <= 0) {
                                this.executeSimpleNonQuery(locallyScopedConn, "SET OPTION SQL_SELECT_LIMIT=DEFAULT");
                            }
                            else {
                                this.executeSimpleNonQuery(locallyScopedConn, "SET OPTION SQL_SELECT_LIMIT=" + this.maxRows);
                            }
                        }
                        else {
                            this.executeSimpleNonQuery(locallyScopedConn, "SET OPTION SQL_SELECT_LIMIT=DEFAULT");
                        }
                        rs = locallyScopedConn.execSQL(this, sql, rowLimit, null, this.resultSetType, this.resultSetConcurrency, doStreaming, this.currentCatalog, cachedFields);
                    }
                    else {
                        rs = locallyScopedConn.execSQL(this, sql, -1, null, this.resultSetType, this.resultSetConcurrency, doStreaming, this.currentCatalog, cachedFields);
                    }
                    if (timeoutTask != null) {
                        if (timeoutTask.caughtWhileCancelling != null) {
                            throw timeoutTask.caughtWhileCancelling;
                        }
                        timeoutTask.cancel();
                        timeoutTask = null;
                    }
                    synchronized (this.cancelTimeoutMutex) {
                        if (this.wasCancelled) {
                            SQLException cause = null;
                            if (this.wasCancelledByTimeout) {
                                cause = new MySQLTimeoutException();
                            }
                            else {
                                cause = new MySQLStatementCancelledException();
                            }
                            this.resetCancelledState();
                            throw cause;
                        }
                    }
                }
                finally {
                    if (timeoutTask != null) {
                        timeoutTask.cancel();
                    }
                    if (oldCatalog != null) {
                        locallyScopedConn.setCatalog(oldCatalog);
                    }
                }
            }
            if (rs != null) {
                this.lastInsertId = rs.getUpdateID();
                (this.results = rs).setFirstCharOfQuery(firstNonWsChar);
                if (rs.reallyResult()) {
                    if (cachedMetaData != null) {
                        locallyScopedConn.initializeResultsMetadataFromCache(sql, cachedMetaData, this.results);
                    }
                    else if (this.connection.getCacheResultSetMetadata()) {
                        locallyScopedConn.initializeResultsMetadataFromCache(sql, null, this.results);
                    }
                }
            }
            return rs != null && rs.reallyResult();
        }
    }
    
    protected synchronized void resetCancelledState() {
        if (this.cancelTimeoutMutex == null) {
            return;
        }
        synchronized (this.cancelTimeoutMutex) {
            this.wasCancelled = false;
            this.wasCancelledByTimeout = false;
        }
    }
    
    public boolean execute(final String sql, final int returnGeneratedKeys) throws SQLException {
        if (returnGeneratedKeys == 1) {
            this.checkClosed();
            final ConnectionImpl locallyScopedConn = this.connection;
            synchronized (locallyScopedConn.getMutex()) {
                final boolean readInfoMsgState = this.connection.isReadInfoMsgEnabled();
                locallyScopedConn.setReadInfoMsgEnabled(true);
                try {
                    return this.execute(sql, true);
                }
                finally {
                    locallyScopedConn.setReadInfoMsgEnabled(readInfoMsgState);
                }
            }
        }
        return this.execute(sql);
    }
    
    public boolean execute(final String sql, final int[] generatedKeyIndices) throws SQLException {
        if (generatedKeyIndices != null && generatedKeyIndices.length > 0) {
            this.checkClosed();
            final ConnectionImpl locallyScopedConn = this.connection;
            synchronized (locallyScopedConn.getMutex()) {
                this.retrieveGeneratedKeys = true;
                final boolean readInfoMsgState = locallyScopedConn.isReadInfoMsgEnabled();
                locallyScopedConn.setReadInfoMsgEnabled(true);
                try {
                    return this.execute(sql, true);
                }
                finally {
                    locallyScopedConn.setReadInfoMsgEnabled(readInfoMsgState);
                }
            }
        }
        return this.execute(sql);
    }
    
    public boolean execute(final String sql, final String[] generatedKeyNames) throws SQLException {
        if (generatedKeyNames != null && generatedKeyNames.length > 0) {
            this.checkClosed();
            final ConnectionImpl locallyScopedConn = this.connection;
            synchronized (locallyScopedConn.getMutex()) {
                this.retrieveGeneratedKeys = true;
                final boolean readInfoMsgState = this.connection.isReadInfoMsgEnabled();
                locallyScopedConn.setReadInfoMsgEnabled(true);
                try {
                    return this.execute(sql, true);
                }
                finally {
                    locallyScopedConn.setReadInfoMsgEnabled(readInfoMsgState);
                }
            }
        }
        return this.execute(sql);
    }
    
    public synchronized int[] executeBatch() throws SQLException {
        this.checkClosed();
        final ConnectionImpl locallyScopedConn = this.connection;
        if (locallyScopedConn.isReadOnly()) {
            throw SQLError.createSQLException(Messages.getString("Statement.34") + Messages.getString("Statement.35"), "S1009", this.getExceptionInterceptor());
        }
        if (this.results != null && !locallyScopedConn.getHoldResultsOpenOverStatementClose()) {
            this.results.realClose(false);
        }
        synchronized (locallyScopedConn.getMutex()) {
            if (this.batchedArgs == null || this.batchedArgs.size() == 0) {
                return new int[0];
            }
            final int individualStatementTimeout = this.timeoutInMillis;
            this.timeoutInMillis = 0;
            CancelTask timeoutTask = null;
            try {
                this.resetCancelledState();
                this.retrieveGeneratedKeys = true;
                int[] updateCounts = null;
                if (this.batchedArgs != null) {
                    final int nbrCommands = this.batchedArgs.size();
                    this.batchedGeneratedKeys = new ArrayList(this.batchedArgs.size());
                    final boolean multiQueriesEnabled = locallyScopedConn.getAllowMultiQueries();
                    if (locallyScopedConn.versionMeetsMinimum(4, 1, 1) && (multiQueriesEnabled || (locallyScopedConn.getRewriteBatchedStatements() && nbrCommands > 4))) {
                        return this.executeBatchUsingMultiQueries(multiQueriesEnabled, nbrCommands, individualStatementTimeout);
                    }
                    if (locallyScopedConn.getEnableQueryTimeouts() && individualStatementTimeout != 0 && locallyScopedConn.versionMeetsMinimum(5, 0, 0)) {
                        timeoutTask = new CancelTask(this);
                        ConnectionImpl.getCancelTimer().schedule(timeoutTask, individualStatementTimeout);
                    }
                    updateCounts = new int[nbrCommands];
                    for (int i = 0; i < nbrCommands; ++i) {
                        updateCounts[i] = -3;
                    }
                    SQLException sqlEx = null;
                    int commandIndex;
                    String sql;
                    int[] newUpdateCounts;
                    int j;
                    for (commandIndex = 0, commandIndex = 0; commandIndex < nbrCommands; ++commandIndex) {
                        try {
                            sql = this.batchedArgs.get(commandIndex);
                            updateCounts[commandIndex] = this.executeUpdate(sql, true, true);
                            this.getBatchedGeneratedKeys(this.containsOnDuplicateKeyInString(sql) ? 1 : 0);
                        }
                        catch (SQLException ex) {
                            updateCounts[commandIndex] = -3;
                            if (!this.continueBatchOnError || ex instanceof MySQLTimeoutException || ex instanceof MySQLStatementCancelledException || this.hasDeadlockOrTimeoutRolledBackTx(ex)) {
                                newUpdateCounts = new int[commandIndex];
                                if (this.hasDeadlockOrTimeoutRolledBackTx(ex)) {
                                    for (j = 0; j < newUpdateCounts.length; ++j) {
                                        newUpdateCounts[j] = -3;
                                    }
                                }
                                else {
                                    System.arraycopy(updateCounts, 0, newUpdateCounts, 0, commandIndex);
                                }
                                throw new BatchUpdateException(ex.getMessage(), ex.getSQLState(), ex.getErrorCode(), newUpdateCounts);
                            }
                            sqlEx = ex;
                        }
                    }
                    if (sqlEx != null) {
                        throw new BatchUpdateException(sqlEx.getMessage(), sqlEx.getSQLState(), sqlEx.getErrorCode(), updateCounts);
                    }
                }
                if (timeoutTask != null) {
                    if (timeoutTask.caughtWhileCancelling != null) {
                        throw timeoutTask.caughtWhileCancelling;
                    }
                    timeoutTask.cancel();
                    timeoutTask = null;
                }
                return (updateCounts != null) ? updateCounts : new int[0];
            }
            finally {
                if (timeoutTask != null) {
                    timeoutTask.cancel();
                }
                this.resetCancelledState();
                this.timeoutInMillis = individualStatementTimeout;
                this.clearBatch();
            }
        }
    }
    
    protected final boolean hasDeadlockOrTimeoutRolledBackTx(final SQLException ex) {
        final int vendorCode = ex.getErrorCode();
        switch (vendorCode) {
            case 1206:
            case 1213: {
                return true;
            }
            case 1205: {
                try {
                    return !this.connection.versionMeetsMinimum(5, 0, 13);
                }
                catch (SQLException sqlEx) {
                    return false;
                }
                break;
            }
        }
        return false;
    }
    
    private int[] executeBatchUsingMultiQueries(final boolean multiQueriesEnabled, final int nbrCommands, final int individualStatementTimeout) throws SQLException {
        final ConnectionImpl locallyScopedConn = this.connection;
        if (!multiQueriesEnabled) {
            locallyScopedConn.getIO().enableMultiQueries();
        }
        java.sql.Statement batchStmt = null;
        CancelTask timeoutTask = null;
        try {
            final int[] updateCounts = new int[nbrCommands];
            for (int i = 0; i < nbrCommands; ++i) {
                updateCounts[i] = -3;
            }
            int commandIndex = 0;
            StringBuffer queryBuf = new StringBuffer();
            batchStmt = locallyScopedConn.createStatement();
            if (locallyScopedConn.getEnableQueryTimeouts() && individualStatementTimeout != 0 && locallyScopedConn.versionMeetsMinimum(5, 0, 0)) {
                timeoutTask = new CancelTask((StatementImpl)batchStmt);
                ConnectionImpl.getCancelTimer().schedule(timeoutTask, individualStatementTimeout);
            }
            int counter = 0;
            int numberOfBytesPerChar = 1;
            final String connectionEncoding = locallyScopedConn.getEncoding();
            if (StringUtils.startsWithIgnoreCase(connectionEncoding, "utf")) {
                numberOfBytesPerChar = 3;
            }
            else if (CharsetMapping.isMultibyteCharset(connectionEncoding)) {
                numberOfBytesPerChar = 2;
            }
            int escapeAdjust = 1;
            if (this.doEscapeProcessing) {
                escapeAdjust = 2;
            }
            SQLException sqlEx = null;
            int argumentSetsInBatchSoFar = 0;
            for (commandIndex = 0; commandIndex < nbrCommands; ++commandIndex) {
                final String nextQuery = this.batchedArgs.get(commandIndex);
                if (((queryBuf.length() + nextQuery.length()) * numberOfBytesPerChar + 1 + 4) * escapeAdjust + 32 > this.connection.getMaxAllowedPacket()) {
                    try {
                        batchStmt.execute(queryBuf.toString(), 1);
                    }
                    catch (SQLException ex) {
                        sqlEx = this.handleExceptionForBatch(commandIndex, argumentSetsInBatchSoFar, updateCounts, ex);
                    }
                    counter = this.processMultiCountsAndKeys((StatementImpl)batchStmt, counter, updateCounts);
                    queryBuf = new StringBuffer();
                    argumentSetsInBatchSoFar = 0;
                }
                queryBuf.append(nextQuery);
                queryBuf.append(";");
                ++argumentSetsInBatchSoFar;
            }
            if (queryBuf.length() > 0) {
                try {
                    batchStmt.execute(queryBuf.toString(), 1);
                }
                catch (SQLException ex2) {
                    sqlEx = this.handleExceptionForBatch(commandIndex - 1, argumentSetsInBatchSoFar, updateCounts, ex2);
                }
                counter = this.processMultiCountsAndKeys((StatementImpl)batchStmt, counter, updateCounts);
            }
            if (timeoutTask != null) {
                if (timeoutTask.caughtWhileCancelling != null) {
                    throw timeoutTask.caughtWhileCancelling;
                }
                timeoutTask.cancel();
                timeoutTask = null;
            }
            if (sqlEx != null) {
                throw new BatchUpdateException(sqlEx.getMessage(), sqlEx.getSQLState(), sqlEx.getErrorCode(), updateCounts);
            }
            return (updateCounts != null) ? updateCounts : new int[0];
        }
        finally {
            if (timeoutTask != null) {
                timeoutTask.cancel();
            }
            this.resetCancelledState();
            try {
                if (batchStmt != null) {
                    batchStmt.close();
                }
            }
            finally {
                if (!multiQueriesEnabled) {
                    locallyScopedConn.getIO().disableMultiQueries();
                }
            }
        }
    }
    
    protected int processMultiCountsAndKeys(final StatementImpl batchedStatement, int updateCountCounter, final int[] updateCounts) throws SQLException {
        updateCounts[updateCountCounter++] = batchedStatement.getUpdateCount();
        final boolean doGenKeys = this.batchedGeneratedKeys != null;
        byte[][] row = null;
        if (doGenKeys) {
            final long generatedKey = batchedStatement.getLastInsertID();
            row = new byte[][] { Long.toString(generatedKey).getBytes() };
            this.batchedGeneratedKeys.add(new ByteArrayRow(row, this.getExceptionInterceptor()));
        }
        while (batchedStatement.getMoreResults() || batchedStatement.getUpdateCount() != -1) {
            updateCounts[updateCountCounter++] = batchedStatement.getUpdateCount();
            if (doGenKeys) {
                final long generatedKey = batchedStatement.getLastInsertID();
                row = new byte[][] { Long.toString(generatedKey).getBytes() };
                this.batchedGeneratedKeys.add(new ByteArrayRow(row, this.getExceptionInterceptor()));
            }
        }
        return updateCountCounter;
    }
    
    protected SQLException handleExceptionForBatch(final int endOfBatchIndex, final int numValuesPerBatch, final int[] updateCounts, final SQLException ex) throws BatchUpdateException {
        for (int j = endOfBatchIndex; j > endOfBatchIndex - numValuesPerBatch; --j) {
            updateCounts[j] = -3;
        }
        if (this.continueBatchOnError && !(ex instanceof MySQLTimeoutException) && !(ex instanceof MySQLStatementCancelledException) && !this.hasDeadlockOrTimeoutRolledBackTx(ex)) {
            return ex;
        }
        final int[] newUpdateCounts = new int[endOfBatchIndex];
        System.arraycopy(updateCounts, 0, newUpdateCounts, 0, endOfBatchIndex);
        throw new BatchUpdateException(ex.getMessage(), ex.getSQLState(), ex.getErrorCode(), newUpdateCounts);
    }
    
    public ResultSet executeQuery(String sql) throws SQLException {
        this.checkClosed();
        final ConnectionImpl locallyScopedConn = this.connection;
        synchronized (locallyScopedConn.getMutex()) {
            this.retrieveGeneratedKeys = false;
            this.resetCancelledState();
            this.checkNullOrEmptyQuery(sql);
            final boolean doStreaming = this.createStreamingResultSet();
            if (doStreaming && this.connection.getNetTimeoutForStreamingResults() > 0) {
                this.executeSimpleNonQuery(locallyScopedConn, "SET net_write_timeout=" + this.connection.getNetTimeoutForStreamingResults());
            }
            if (this.doEscapeProcessing) {
                final Object escapedSqlResult = EscapeProcessor.escapeSQL(sql, locallyScopedConn.serverSupportsConvertFn(), this.connection);
                if (escapedSqlResult instanceof String) {
                    sql = (String)escapedSqlResult;
                }
                else {
                    sql = ((EscapeProcessorResult)escapedSqlResult).escapedSql;
                }
            }
            final char firstStatementChar = StringUtils.firstNonWsCharUc(sql, this.findStartOfStatement(sql));
            if (sql.charAt(0) == '/' && sql.startsWith("/* ping */")) {
                this.doPingInstead();
                return this.results;
            }
            this.checkForDml(sql, firstStatementChar);
            if (this.results != null && !locallyScopedConn.getHoldResultsOpenOverStatementClose()) {
                this.results.realClose(false);
            }
            CachedResultSetMetaData cachedMetaData = null;
            if (this.useServerFetch()) {
                return this.results = this.createResultSetUsingServerFetch(sql);
            }
            CancelTask timeoutTask = null;
            String oldCatalog = null;
            try {
                if (locallyScopedConn.getEnableQueryTimeouts() && this.timeoutInMillis != 0 && locallyScopedConn.versionMeetsMinimum(5, 0, 0)) {
                    timeoutTask = new CancelTask(this);
                    ConnectionImpl.getCancelTimer().schedule(timeoutTask, this.timeoutInMillis);
                }
                if (!locallyScopedConn.getCatalog().equals(this.currentCatalog)) {
                    oldCatalog = locallyScopedConn.getCatalog();
                    locallyScopedConn.setCatalog(this.currentCatalog);
                }
                Field[] cachedFields = null;
                if (locallyScopedConn.getCacheResultSetMetadata()) {
                    cachedMetaData = locallyScopedConn.getCachedMetaData(sql);
                    if (cachedMetaData != null) {
                        cachedFields = cachedMetaData.fields;
                    }
                }
                if (locallyScopedConn.useMaxRows()) {
                    if (StringUtils.indexOfIgnoreCase(sql, "LIMIT") != -1) {
                        this.results = locallyScopedConn.execSQL(this, sql, this.maxRows, null, this.resultSetType, this.resultSetConcurrency, doStreaming, this.currentCatalog, cachedFields);
                    }
                    else {
                        if (this.maxRows <= 0) {
                            this.executeSimpleNonQuery(locallyScopedConn, "SET OPTION SQL_SELECT_LIMIT=DEFAULT");
                        }
                        else {
                            this.executeSimpleNonQuery(locallyScopedConn, "SET OPTION SQL_SELECT_LIMIT=" + this.maxRows);
                        }
                        this.results = locallyScopedConn.execSQL(this, sql, -1, null, this.resultSetType, this.resultSetConcurrency, doStreaming, this.currentCatalog, cachedFields);
                        if (oldCatalog != null) {
                            locallyScopedConn.setCatalog(oldCatalog);
                        }
                    }
                }
                else {
                    this.results = locallyScopedConn.execSQL(this, sql, -1, null, this.resultSetType, this.resultSetConcurrency, doStreaming, this.currentCatalog, cachedFields);
                }
                if (timeoutTask != null) {
                    if (timeoutTask.caughtWhileCancelling != null) {
                        throw timeoutTask.caughtWhileCancelling;
                    }
                    timeoutTask.cancel();
                    timeoutTask = null;
                }
                synchronized (this.cancelTimeoutMutex) {
                    if (this.wasCancelled) {
                        SQLException cause = null;
                        if (this.wasCancelledByTimeout) {
                            cause = new MySQLTimeoutException();
                        }
                        else {
                            cause = new MySQLStatementCancelledException();
                        }
                        this.resetCancelledState();
                        throw cause;
                    }
                }
            }
            finally {
                if (timeoutTask != null) {
                    timeoutTask.cancel();
                }
                if (oldCatalog != null) {
                    locallyScopedConn.setCatalog(oldCatalog);
                }
            }
            this.lastInsertId = this.results.getUpdateID();
            if (cachedMetaData != null) {
                locallyScopedConn.initializeResultsMetadataFromCache(sql, cachedMetaData, this.results);
            }
            else if (this.connection.getCacheResultSetMetadata()) {
                locallyScopedConn.initializeResultsMetadataFromCache(sql, null, this.results);
            }
            return this.results;
        }
    }
    
    protected void doPingInstead() throws SQLException {
        if (this.pingTarget != null) {
            this.pingTarget.doPing();
        }
        else {
            this.connection.ping();
        }
        final ResultSetInternalMethods fakeSelectOneResultSet = this.generatePingResultSet();
        this.results = fakeSelectOneResultSet;
    }
    
    protected ResultSetInternalMethods generatePingResultSet() throws SQLException {
        final Field[] fields = { new Field(null, "1", -5, 1) };
        final ArrayList rows = new ArrayList();
        final byte[] colVal = { 49 };
        rows.add(new ByteArrayRow(new byte[][] { colVal }, this.getExceptionInterceptor()));
        return (ResultSetInternalMethods)DatabaseMetaData.buildResultSet(fields, rows, this.connection);
    }
    
    protected void executeSimpleNonQuery(final ConnectionImpl c, final String nonQuery) throws SQLException {
        c.execSQL(this, nonQuery, -1, null, 1003, 1007, false, this.currentCatalog, null, false).close();
    }
    
    public int executeUpdate(final String sql) throws SQLException {
        return this.executeUpdate(sql, false, false);
    }
    
    protected int executeUpdate(String sql, final boolean isBatch, final boolean returnGeneratedKeys) throws SQLException {
        this.checkClosed();
        final ConnectionImpl locallyScopedConn = this.connection;
        final char firstStatementChar = StringUtils.firstAlphaCharUc(sql, this.findStartOfStatement(sql));
        ResultSetInternalMethods rs = null;
        synchronized (locallyScopedConn.getMutex()) {
            this.retrieveGeneratedKeys = returnGeneratedKeys;
            this.resetCancelledState();
            this.checkNullOrEmptyQuery(sql);
            if (this.doEscapeProcessing) {
                final Object escapedSqlResult = EscapeProcessor.escapeSQL(sql, this.connection.serverSupportsConvertFn(), this.connection);
                if (escapedSqlResult instanceof String) {
                    sql = (String)escapedSqlResult;
                }
                else {
                    sql = ((EscapeProcessorResult)escapedSqlResult).escapedSql;
                }
            }
            if (locallyScopedConn.isReadOnly()) {
                throw SQLError.createSQLException(Messages.getString("Statement.42") + Messages.getString("Statement.43"), "S1009", this.getExceptionInterceptor());
            }
            if (StringUtils.startsWithIgnoreCaseAndWs(sql, "select")) {
                throw SQLError.createSQLException(Messages.getString("Statement.46"), "01S03", this.getExceptionInterceptor());
            }
            if (this.results != null && !locallyScopedConn.getHoldResultsOpenOverStatementClose()) {
                this.results.realClose(false);
            }
            CancelTask timeoutTask = null;
            String oldCatalog = null;
            try {
                if (locallyScopedConn.getEnableQueryTimeouts() && this.timeoutInMillis != 0 && locallyScopedConn.versionMeetsMinimum(5, 0, 0)) {
                    timeoutTask = new CancelTask(this);
                    ConnectionImpl.getCancelTimer().schedule(timeoutTask, this.timeoutInMillis);
                }
                if (!locallyScopedConn.getCatalog().equals(this.currentCatalog)) {
                    oldCatalog = locallyScopedConn.getCatalog();
                    locallyScopedConn.setCatalog(this.currentCatalog);
                }
                if (locallyScopedConn.useMaxRows()) {
                    this.executeSimpleNonQuery(locallyScopedConn, "SET OPTION SQL_SELECT_LIMIT=DEFAULT");
                }
                rs = locallyScopedConn.execSQL(this, sql, -1, null, 1003, 1007, false, this.currentCatalog, null, isBatch);
                if (timeoutTask != null) {
                    if (timeoutTask.caughtWhileCancelling != null) {
                        throw timeoutTask.caughtWhileCancelling;
                    }
                    timeoutTask.cancel();
                    timeoutTask = null;
                }
                synchronized (this.cancelTimeoutMutex) {
                    if (this.wasCancelled) {
                        SQLException cause = null;
                        if (this.wasCancelledByTimeout) {
                            cause = new MySQLTimeoutException();
                        }
                        else {
                            cause = new MySQLStatementCancelledException();
                        }
                        this.resetCancelledState();
                        throw cause;
                    }
                }
            }
            finally {
                if (timeoutTask != null) {
                    timeoutTask.cancel();
                }
                if (oldCatalog != null) {
                    locallyScopedConn.setCatalog(oldCatalog);
                }
            }
        }
        (this.results = rs).setFirstCharOfQuery(firstStatementChar);
        this.updateCount = rs.getUpdateCount();
        int truncatedUpdateCount = 0;
        if (this.updateCount > 2147483647L) {
            truncatedUpdateCount = Integer.MAX_VALUE;
        }
        else {
            truncatedUpdateCount = (int)this.updateCount;
        }
        this.lastInsertId = rs.getUpdateID();
        return truncatedUpdateCount;
    }
    
    public int executeUpdate(final String sql, final int returnGeneratedKeys) throws SQLException {
        if (returnGeneratedKeys == 1) {
            this.checkClosed();
            final ConnectionImpl locallyScopedConn = this.connection;
            synchronized (locallyScopedConn.getMutex()) {
                final boolean readInfoMsgState = locallyScopedConn.isReadInfoMsgEnabled();
                locallyScopedConn.setReadInfoMsgEnabled(true);
                try {
                    return this.executeUpdate(sql, false, true);
                }
                finally {
                    locallyScopedConn.setReadInfoMsgEnabled(readInfoMsgState);
                }
            }
        }
        return this.executeUpdate(sql);
    }
    
    public int executeUpdate(final String sql, final int[] generatedKeyIndices) throws SQLException {
        if (generatedKeyIndices != null && generatedKeyIndices.length > 0) {
            this.checkClosed();
            final ConnectionImpl locallyScopedConn = this.connection;
            synchronized (locallyScopedConn.getMutex()) {
                final boolean readInfoMsgState = locallyScopedConn.isReadInfoMsgEnabled();
                locallyScopedConn.setReadInfoMsgEnabled(true);
                try {
                    return this.executeUpdate(sql, false, true);
                }
                finally {
                    locallyScopedConn.setReadInfoMsgEnabled(readInfoMsgState);
                }
            }
        }
        return this.executeUpdate(sql);
    }
    
    public int executeUpdate(final String sql, final String[] generatedKeyNames) throws SQLException {
        if (generatedKeyNames != null && generatedKeyNames.length > 0) {
            this.checkClosed();
            final ConnectionImpl locallyScopedConn = this.connection;
            synchronized (locallyScopedConn.getMutex()) {
                final boolean readInfoMsgState = this.connection.isReadInfoMsgEnabled();
                locallyScopedConn.setReadInfoMsgEnabled(true);
                try {
                    return this.executeUpdate(sql, false, true);
                }
                finally {
                    locallyScopedConn.setReadInfoMsgEnabled(readInfoMsgState);
                }
            }
        }
        return this.executeUpdate(sql);
    }
    
    protected Calendar getCalendarInstanceForSessionOrNew() {
        if (this.connection != null) {
            return this.connection.getCalendarInstanceForSessionOrNew();
        }
        return new GregorianCalendar();
    }
    
    public java.sql.Connection getConnection() throws SQLException {
        return this.connection;
    }
    
    public int getFetchDirection() throws SQLException {
        return 1000;
    }
    
    public int getFetchSize() throws SQLException {
        return this.fetchSize;
    }
    
    public synchronized ResultSet getGeneratedKeys() throws SQLException {
        if (!this.retrieveGeneratedKeys) {
            throw SQLError.createSQLException(Messages.getString("Statement.GeneratedKeysNotRequested"), "S1009", this.getExceptionInterceptor());
        }
        if (this.batchedGeneratedKeys != null) {
            final Field[] fields = { new Field("", "GENERATED_KEY", -5, 17) };
            fields[0].setConnection(this.connection);
            return ResultSetImpl.getInstance(this.currentCatalog, fields, new RowDataStatic(this.batchedGeneratedKeys), this.connection, this, false);
        }
        if (this.lastQueryIsOnDupKeyUpdate) {
            return this.getGeneratedKeysInternal(1);
        }
        return this.getGeneratedKeysInternal();
    }
    
    protected ResultSet getGeneratedKeysInternal() throws SQLException {
        final int numKeys = this.getUpdateCount();
        return this.getGeneratedKeysInternal(numKeys);
    }
    
    protected synchronized ResultSet getGeneratedKeysInternal(int numKeys) throws SQLException {
        final Field[] fields = { new Field("", "GENERATED_KEY", -5, 17) };
        fields[0].setConnection(this.connection);
        fields[0].setUseOldNameMetadata(true);
        final ArrayList rowSet = new ArrayList();
        long beginAt = this.getLastInsertID();
        if (beginAt < 0L) {
            fields[0].setUnsigned();
        }
        if (this.results != null) {
            final String serverInfo = this.results.getServerInfo();
            if (numKeys > 0 && this.results.getFirstCharOfQuery() == 'R' && serverInfo != null && serverInfo.length() > 0) {
                numKeys = this.getRecordCountFromInfo(serverInfo);
            }
            if (beginAt != 0L && numKeys > 0) {
                for (int i = 0; i < numKeys; ++i) {
                    final byte[][] row = { null };
                    if (beginAt > 0L) {
                        row[0] = Long.toString(beginAt).getBytes();
                    }
                    else {
                        final byte[] asBytes = { (byte)(beginAt >>> 56), (byte)(beginAt >>> 48), (byte)(beginAt >>> 40), (byte)(beginAt >>> 32), (byte)(beginAt >>> 24), (byte)(beginAt >>> 16), (byte)(beginAt >>> 8), (byte)(beginAt & 0xFFL) };
                        final BigInteger val = new BigInteger(1, asBytes);
                        row[0] = val.toString().getBytes();
                    }
                    rowSet.add(new ByteArrayRow(row, this.getExceptionInterceptor()));
                    beginAt += this.connection.getAutoIncrementIncrement();
                }
            }
        }
        final ResultSetImpl gkRs = ResultSetImpl.getInstance(this.currentCatalog, fields, new RowDataStatic(rowSet), this.connection, this, false);
        this.openResults.add(gkRs);
        return gkRs;
    }
    
    protected int getId() {
        return this.statementId;
    }
    
    public long getLastInsertID() {
        return this.lastInsertId;
    }
    
    public long getLongUpdateCount() {
        if (this.results == null) {
            return -1L;
        }
        if (this.results.reallyResult()) {
            return -1L;
        }
        return this.updateCount;
    }
    
    public int getMaxFieldSize() throws SQLException {
        return this.maxFieldSize;
    }
    
    public int getMaxRows() throws SQLException {
        if (this.maxRows <= 0) {
            return 0;
        }
        return this.maxRows;
    }
    
    public boolean getMoreResults() throws SQLException {
        return this.getMoreResults(1);
    }
    
    public synchronized boolean getMoreResults(final int current) throws SQLException {
        if (this.results == null) {
            return false;
        }
        final boolean streamingMode = this.createStreamingResultSet();
        if (streamingMode && this.results.reallyResult()) {
            while (this.results.next()) {}
        }
        final ResultSetInternalMethods nextResultSet = this.results.getNextResultSet();
        switch (current) {
            case 1: {
                if (this.results != null) {
                    if (!streamingMode) {
                        this.results.close();
                    }
                    this.results.clearNextResult();
                    break;
                }
                break;
            }
            case 3: {
                if (this.results != null) {
                    if (!streamingMode) {
                        this.results.close();
                    }
                    this.results.clearNextResult();
                }
                this.closeAllOpenResults();
                break;
            }
            case 2: {
                if (!this.connection.getDontTrackOpenResources()) {
                    this.openResults.add(this.results);
                }
                this.results.clearNextResult();
                break;
            }
            default: {
                throw SQLError.createSQLException(Messages.getString("Statement.19"), "S1009", this.getExceptionInterceptor());
            }
        }
        this.results = nextResultSet;
        if (this.results == null) {
            this.updateCount = -1L;
            this.lastInsertId = -1L;
        }
        else if (this.results.reallyResult()) {
            this.updateCount = -1L;
            this.lastInsertId = -1L;
        }
        else {
            this.updateCount = this.results.getUpdateCount();
            this.lastInsertId = this.results.getUpdateID();
        }
        return this.results != null && this.results.reallyResult();
    }
    
    public int getQueryTimeout() throws SQLException {
        return this.timeoutInMillis / 1000;
    }
    
    private int getRecordCountFromInfo(final String serverInfo) {
        final StringBuffer recordsBuf = new StringBuffer();
        int recordsCount = 0;
        int duplicatesCount = 0;
        char c = '\0';
        int length;
        int i;
        for (length = serverInfo.length(), i = 0; i < length; ++i) {
            c = serverInfo.charAt(i);
            if (Character.isDigit(c)) {
                break;
            }
        }
        recordsBuf.append(c);
        ++i;
        while (i < length) {
            c = serverInfo.charAt(i);
            if (!Character.isDigit(c)) {
                break;
            }
            recordsBuf.append(c);
            ++i;
        }
        recordsCount = Integer.parseInt(recordsBuf.toString());
        final StringBuffer duplicatesBuf = new StringBuffer();
        while (i < length) {
            c = serverInfo.charAt(i);
            if (Character.isDigit(c)) {
                break;
            }
            ++i;
        }
        duplicatesBuf.append(c);
        ++i;
        while (i < length) {
            c = serverInfo.charAt(i);
            if (!Character.isDigit(c)) {
                break;
            }
            duplicatesBuf.append(c);
            ++i;
        }
        duplicatesCount = Integer.parseInt(duplicatesBuf.toString());
        return recordsCount - duplicatesCount;
    }
    
    public ResultSet getResultSet() throws SQLException {
        return (this.results != null && this.results.reallyResult()) ? this.results : null;
    }
    
    public int getResultSetConcurrency() throws SQLException {
        return this.resultSetConcurrency;
    }
    
    public int getResultSetHoldability() throws SQLException {
        return 1;
    }
    
    protected ResultSetInternalMethods getResultSetInternal() {
        return this.results;
    }
    
    public int getResultSetType() throws SQLException {
        return this.resultSetType;
    }
    
    public int getUpdateCount() throws SQLException {
        if (this.results == null) {
            return -1;
        }
        if (this.results.reallyResult()) {
            return -1;
        }
        int truncatedUpdateCount = 0;
        if (this.results.getUpdateCount() > 2147483647L) {
            truncatedUpdateCount = Integer.MAX_VALUE;
        }
        else {
            truncatedUpdateCount = (int)this.results.getUpdateCount();
        }
        return truncatedUpdateCount;
    }
    
    public SQLWarning getWarnings() throws SQLException {
        this.checkClosed();
        if (this.connection != null && !this.connection.isClosed() && this.connection.versionMeetsMinimum(4, 1, 0)) {
            final SQLWarning pendingWarningsFromServer = SQLError.convertShowWarningsToSQLWarnings(this.connection);
            if (this.warningChain != null) {
                this.warningChain.setNextWarning(pendingWarningsFromServer);
            }
            else {
                this.warningChain = pendingWarningsFromServer;
            }
            return this.warningChain;
        }
        return this.warningChain;
    }
    
    protected synchronized void realClose(final boolean calledExplicitly, boolean closeOpenResults) throws SQLException {
        if (this.isClosed) {
            return;
        }
        if (this.useUsageAdvisor && !calledExplicitly) {
            final String message = Messages.getString("Statement.63") + Messages.getString("Statement.64");
            this.eventSink.consumeEvent(new ProfilerEvent((byte)0, "", this.currentCatalog, this.connectionId, this.getId(), -1, System.currentTimeMillis(), 0L, Constants.MILLIS_I18N, null, this.pointOfOrigin, message));
        }
        if (closeOpenResults) {
            closeOpenResults = !this.holdResultsOpenOverClose;
        }
        if (closeOpenResults) {
            if (this.results != null) {
                try {
                    this.results.close();
                }
                catch (Exception ex) {}
            }
            this.closeAllOpenResults();
        }
        if (this.connection != null) {
            if (this.maxRowsChanged) {
                this.connection.unsetMaxRows(this);
            }
            if (!this.connection.getDontTrackOpenResources()) {
                this.connection.unregisterStatement(this);
            }
        }
        this.isClosed = true;
        this.results = null;
        this.connection = null;
        this.warningChain = null;
        this.openResults = null;
        this.batchedGeneratedKeys = null;
        this.localInfileInputStream = null;
        this.pingTarget = null;
    }
    
    public void setCursorName(final String name) throws SQLException {
    }
    
    public void setEscapeProcessing(final boolean enable) throws SQLException {
        this.doEscapeProcessing = enable;
    }
    
    public void setFetchDirection(final int direction) throws SQLException {
        switch (direction) {
            case 1000:
            case 1001:
            case 1002: {}
            default: {
                throw SQLError.createSQLException(Messages.getString("Statement.5"), "S1009", this.getExceptionInterceptor());
            }
        }
    }
    
    public void setFetchSize(final int rows) throws SQLException {
        if ((rows < 0 && rows != Integer.MIN_VALUE) || (this.maxRows != 0 && this.maxRows != -1 && rows > this.getMaxRows())) {
            throw SQLError.createSQLException(Messages.getString("Statement.7"), "S1009", this.getExceptionInterceptor());
        }
        this.fetchSize = rows;
    }
    
    protected void setHoldResultsOpenOverClose(final boolean holdResultsOpenOverClose) {
        this.holdResultsOpenOverClose = holdResultsOpenOverClose;
    }
    
    public void setMaxFieldSize(final int max) throws SQLException {
        if (max < 0) {
            throw SQLError.createSQLException(Messages.getString("Statement.11"), "S1009", this.getExceptionInterceptor());
        }
        final int maxBuf = (this.connection != null) ? this.connection.getMaxAllowedPacket() : MysqlIO.getMaxBuf();
        if (max > maxBuf) {
            throw SQLError.createSQLException(Messages.getString("Statement.13", new Object[] { Constants.longValueOf(maxBuf) }), "S1009", this.getExceptionInterceptor());
        }
        this.maxFieldSize = max;
    }
    
    public void setMaxRows(int max) throws SQLException {
        if (max > 50000000 || max < 0) {
            throw SQLError.createSQLException(Messages.getString("Statement.15") + max + " > " + 50000000 + ".", "S1009", this.getExceptionInterceptor());
        }
        if (max == 0) {
            max = -1;
        }
        this.maxRows = max;
        this.maxRowsChanged = true;
        if (this.maxRows == -1) {
            this.connection.unsetMaxRows(this);
            this.maxRowsChanged = false;
        }
        else {
            this.connection.maxRowsChanged(this);
        }
    }
    
    public void setQueryTimeout(final int seconds) throws SQLException {
        if (seconds < 0) {
            throw SQLError.createSQLException(Messages.getString("Statement.21"), "S1009", this.getExceptionInterceptor());
        }
        this.timeoutInMillis = seconds * 1000;
    }
    
    void setResultSetConcurrency(final int concurrencyFlag) {
        this.resultSetConcurrency = concurrencyFlag;
    }
    
    void setResultSetType(final int typeFlag) {
        this.resultSetType = typeFlag;
    }
    
    protected void getBatchedGeneratedKeys(final java.sql.Statement batchedStatement) throws SQLException {
        if (this.retrieveGeneratedKeys) {
            ResultSet rs = null;
            try {
                rs = batchedStatement.getGeneratedKeys();
                while (rs.next()) {
                    this.batchedGeneratedKeys.add(new ByteArrayRow(new byte[][] { rs.getBytes(1) }, this.getExceptionInterceptor()));
                }
            }
            finally {
                if (rs != null) {
                    rs.close();
                }
            }
        }
    }
    
    protected void getBatchedGeneratedKeys(final int maxKeys) throws SQLException {
        if (this.retrieveGeneratedKeys) {
            ResultSet rs = null;
            try {
                if (maxKeys == 0) {
                    rs = this.getGeneratedKeysInternal();
                }
                else {
                    rs = this.getGeneratedKeysInternal(maxKeys);
                }
                while (rs.next()) {
                    this.batchedGeneratedKeys.add(new ByteArrayRow(new byte[][] { rs.getBytes(1) }, this.getExceptionInterceptor()));
                }
            }
            finally {
                if (rs != null) {
                    rs.close();
                }
            }
        }
    }
    
    private boolean useServerFetch() throws SQLException {
        return this.connection.isCursorFetchEnabled() && this.fetchSize > 0 && this.resultSetConcurrency == 1007 && this.resultSetType == 1003;
    }
    
    public synchronized boolean isClosed() throws SQLException {
        return this.isClosed;
    }
    
    public boolean isPoolable() throws SQLException {
        return this.isPoolable;
    }
    
    public void setPoolable(final boolean poolable) throws SQLException {
        this.isPoolable = poolable;
    }
    
    public boolean isWrapperFor(final Class iface) throws SQLException {
        this.checkClosed();
        return iface.isInstance(this);
    }
    
    public Object unwrap(final Class iface) throws SQLException {
        try {
            return Util.cast(iface, this);
        }
        catch (ClassCastException cce) {
            throw SQLError.createSQLException("Unable to unwrap to " + iface.toString(), "S1009", this.getExceptionInterceptor());
        }
    }
    
    protected int findStartOfStatement(final String sql) {
        int statementStartPos = 0;
        if (StringUtils.startsWithIgnoreCaseAndWs(sql, "/*")) {
            statementStartPos = sql.indexOf("*/");
            if (statementStartPos == -1) {
                statementStartPos = 0;
            }
            else {
                statementStartPos += 2;
            }
        }
        else if (StringUtils.startsWithIgnoreCaseAndWs(sql, "--") || StringUtils.startsWithIgnoreCaseAndWs(sql, "#")) {
            statementStartPos = sql.indexOf(10);
            if (statementStartPos == -1) {
                statementStartPos = sql.indexOf(13);
                if (statementStartPos == -1) {
                    statementStartPos = 0;
                }
            }
        }
        return statementStartPos;
    }
    
    public synchronized InputStream getLocalInfileInputStream() {
        return this.localInfileInputStream;
    }
    
    public synchronized void setLocalInfileInputStream(final InputStream stream) {
        this.localInfileInputStream = stream;
    }
    
    public synchronized void setPingTarget(final PingTarget pingTarget) {
        this.pingTarget = pingTarget;
    }
    
    public ExceptionInterceptor getExceptionInterceptor() {
        return this.exceptionInterceptor;
    }
    
    protected boolean containsOnDuplicateKeyInString(final String sql) {
        return this.getOnDuplicateKeyLocation(sql) != -1;
    }
    
    protected int getOnDuplicateKeyLocation(final String sql) {
        return StringUtils.indexOfIgnoreCaseRespectMarker(0, sql, " ON DUPLICATE KEY UPDATE ", "\"'`", "\"'`", !this.connection.isNoBackslashEscapesSet());
    }
    
    static {
        StatementImpl.statementCounter = 1;
    }
    
    class CancelTask extends TimerTask
    {
        long connectionId;
        SQLException caughtWhileCancelling;
        StatementImpl toCancel;
        private final /* synthetic */ StatementImpl this$0;
        
        CancelTask(final StatementImpl cancellee) throws SQLException {
            this.connectionId = 0L;
            this.caughtWhileCancelling = null;
            this.connectionId = StatementImpl.this.connection.getIO().getThreadId();
            this.toCancel = cancellee;
        }
        
        public void run() {
            final Thread cancelThread = new Thread() {
                private final /* synthetic */ CancelTask this$1 = this$1;
                
                public void run() {
                    if (this.this$1.this$0.connection.getQueryTimeoutKillsConnection()) {
                        try {
                            this.this$1.toCancel.wasCancelled = true;
                            this.this$1.toCancel.wasCancelledByTimeout = true;
                            this.this$1.this$0.connection.realClose(false, false, true, new MySQLStatementCancelledException(Messages.getString("Statement.ConnectionKilledDueToTimeout")));
                        }
                        catch (NullPointerException npe) {}
                        catch (SQLException sqlEx) {
                            this.this$1.caughtWhileCancelling = sqlEx;
                        }
                    }
                    else {
                        Connection cancelConn = null;
                        java.sql.Statement cancelStmt = null;
                        try {
                            synchronized (this.this$1.this$0.cancelTimeoutMutex) {
                                cancelConn = this.this$1.this$0.connection.duplicate();
                                cancelStmt = cancelConn.createStatement();
                                cancelStmt.execute("KILL QUERY " + this.this$1.connectionId);
                                this.this$1.toCancel.wasCancelled = true;
                                this.this$1.toCancel.wasCancelledByTimeout = true;
                            }
                        }
                        catch (SQLException sqlEx2) {
                            this.this$1.caughtWhileCancelling = sqlEx2;
                        }
                        catch (NullPointerException npe2) {}
                        finally {
                            if (cancelStmt != null) {
                                try {
                                    cancelStmt.close();
                                }
                                catch (SQLException sqlEx3) {
                                    throw new RuntimeException(sqlEx3.toString());
                                }
                            }
                            if (cancelConn != null) {
                                try {
                                    cancelConn.close();
                                }
                                catch (SQLException sqlEx3) {
                                    throw new RuntimeException(sqlEx3.toString());
                                }
                            }
                        }
                    }
                }
            };
            cancelThread.start();
        }
    }
}
