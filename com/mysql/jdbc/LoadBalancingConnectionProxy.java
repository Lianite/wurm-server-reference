// 
// Decompiled by Procyon v0.5.30
// 

package com.mysql.jdbc;

import java.util.Set;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.lang.reflect.Proxy;
import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Properties;
import java.util.Map;
import java.util.List;
import java.lang.reflect.Method;
import java.lang.reflect.InvocationHandler;

public class LoadBalancingConnectionProxy implements InvocationHandler, PingTarget
{
    private static Method getLocalTimeMethod;
    public static final String BLACKLIST_TIMEOUT_PROPERTY_KEY = "loadBalanceBlacklistTimeout";
    private Connection currentConn;
    private List hostList;
    private Map liveConnections;
    private Map connectionsToHostsMap;
    private long[] responseTimes;
    private Map hostsToListIndexMap;
    private boolean inTransaction;
    private long transactionStartTime;
    private Properties localProps;
    private boolean isClosed;
    private BalanceStrategy balancer;
    private int retriesAllDown;
    private static Map globalBlacklist;
    private int globalBlacklistTimeout;
    static /* synthetic */ Class class$java$lang$System;
    
    LoadBalancingConnectionProxy(final List hosts, final Properties props) throws SQLException {
        this.inTransaction = false;
        this.transactionStartTime = 0L;
        this.isClosed = false;
        this.globalBlacklistTimeout = 0;
        this.hostList = hosts;
        final int numHosts = this.hostList.size();
        this.liveConnections = new HashMap(numHosts);
        this.connectionsToHostsMap = new HashMap(numHosts);
        this.responseTimes = new long[numHosts];
        this.hostsToListIndexMap = new HashMap(numHosts);
        for (int i = 0; i < numHosts; ++i) {
            this.hostsToListIndexMap.put(this.hostList.get(i), new Integer(i));
        }
        (this.localProps = (Properties)props.clone()).remove("HOST");
        this.localProps.remove("PORT");
        this.localProps.setProperty("useLocalSessionState", "true");
        final String strategy = this.localProps.getProperty("loadBalanceStrategy", "random");
        final String retriesAllDownAsString = this.localProps.getProperty("retriesAllDown", "120");
        try {
            this.retriesAllDown = Integer.parseInt(retriesAllDownAsString);
        }
        catch (NumberFormatException nfe) {
            throw SQLError.createSQLException(Messages.getString("LoadBalancingConnectionProxy.badValueForRetriesAllDown", new Object[] { retriesAllDownAsString }), "S1009", null);
        }
        final String blacklistTimeoutAsString = this.localProps.getProperty("loadBalanceBlacklistTimeout", "0");
        try {
            this.globalBlacklistTimeout = Integer.parseInt(blacklistTimeoutAsString);
        }
        catch (NumberFormatException nfe2) {
            throw SQLError.createSQLException(Messages.getString("LoadBalancingConnectionProxy.badValueForLoadBalanceBlacklistTimeout", new Object[] { retriesAllDownAsString }), "S1009", null);
        }
        if ("random".equals(strategy)) {
            this.balancer = Util.loadExtensions(null, props, "com.mysql.jdbc.RandomBalanceStrategy", "InvalidLoadBalanceStrategy", null).get(0);
        }
        else if ("bestResponseTime".equals(strategy)) {
            this.balancer = Util.loadExtensions(null, props, "com.mysql.jdbc.BestResponseTimeBalanceStrategy", "InvalidLoadBalanceStrategy", null).get(0);
        }
        else {
            this.balancer = Util.loadExtensions(null, props, strategy, "InvalidLoadBalanceStrategy", null).get(0);
        }
        this.balancer.init(null, props);
        this.pickNewConnection();
    }
    
    public synchronized Connection createConnectionForHost(final String hostPortSpec) throws SQLException {
        final Properties connProps = (Properties)this.localProps.clone();
        final String[] hostPortPair = NonRegisteringDriver.parseHostPortPair(hostPortSpec);
        if (hostPortPair[1] == null) {
            hostPortPair[1] = "3306";
        }
        connProps.setProperty("HOST", hostPortSpec);
        connProps.setProperty("PORT", hostPortPair[1]);
        final Connection conn = ConnectionImpl.getInstance(hostPortSpec, Integer.parseInt(hostPortPair[1]), connProps, connProps.getProperty("DBNAME"), "jdbc:mysql://" + hostPortPair[0] + ":" + hostPortPair[1] + "/");
        this.liveConnections.put(hostPortSpec, conn);
        this.connectionsToHostsMap.put(conn, hostPortSpec);
        return conn;
    }
    
    void dealWithInvocationException(final InvocationTargetException e) throws SQLException, Throwable, InvocationTargetException {
        final Throwable t = e.getTargetException();
        if (t != null) {
            if (t instanceof SQLException) {
                final String sqlState = ((SQLException)t).getSQLState();
                if (sqlState != null && sqlState.startsWith("08")) {
                    this.invalidateCurrentConnection();
                }
            }
            throw t;
        }
        throw e;
    }
    
    synchronized void invalidateCurrentConnection() throws SQLException {
        try {
            if (!this.currentConn.isClosed()) {
                this.currentConn.close();
            }
        }
        finally {
            if (this.isGlobalBlacklistEnabled()) {
                this.addToGlobalBlacklist(this.connectionsToHostsMap.get(this.currentConn));
            }
            this.liveConnections.remove(this.connectionsToHostsMap.get(this.currentConn));
            final int hostIndex = this.hostsToListIndexMap.get(this.connectionsToHostsMap.get(this.currentConn));
            synchronized (this.responseTimes) {
                this.responseTimes[hostIndex] = 0L;
            }
            this.connectionsToHostsMap.remove(this.currentConn);
        }
    }
    
    public Object invoke(final Object proxy, final Method method, final Object[] args) throws Throwable {
        final String methodName = method.getName();
        if ("equals".equals(methodName) && args.length == 1) {
            if (args[0] instanceof Proxy) {
                return ((Proxy)args[0]).equals(this);
            }
            return this.equals(args[0]);
        }
        else {
            if ("close".equals(methodName)) {
                synchronized (this) {
                    final Iterator allConnections = this.liveConnections.values().iterator();
                    while (allConnections.hasNext()) {
                        allConnections.next().close();
                    }
                    if (!this.isClosed) {
                        this.balancer.destroy();
                    }
                    this.liveConnections.clear();
                    this.connectionsToHostsMap.clear();
                }
                return null;
            }
            if ("isClosed".equals(methodName)) {
                return this.isClosed;
            }
            if (this.isClosed) {
                throw SQLError.createSQLException("No operations allowed after connection closed.", "08003", null);
            }
            if (!this.inTransaction) {
                this.inTransaction = true;
                this.transactionStartTime = getLocalTimeBestResolution();
            }
            Object result = null;
            try {
                result = method.invoke(this.currentConn, args);
                if (result != null) {
                    if (result instanceof Statement) {
                        ((Statement)result).setPingTarget(this);
                    }
                    result = this.proxyIfInterfaceIsJdbc(result, result.getClass());
                }
            }
            catch (InvocationTargetException e) {
                this.dealWithInvocationException(e);
            }
            finally {
                if ("commit".equals(methodName) || "rollback".equals(methodName)) {
                    this.inTransaction = false;
                    final String host = this.connectionsToHostsMap.get(this.currentConn);
                    if (host != null) {
                        final int hostIndex = this.hostsToListIndexMap.get(host);
                        synchronized (this.responseTimes) {
                            this.responseTimes[hostIndex] = getLocalTimeBestResolution() - this.transactionStartTime;
                        }
                    }
                    this.pickNewConnection();
                }
            }
            return result;
        }
    }
    
    private synchronized void pickNewConnection() throws SQLException {
        if (this.currentConn == null) {
            this.currentConn = this.balancer.pickConnection(this, Collections.unmodifiableList((List<?>)this.hostList), Collections.unmodifiableMap((Map<?, ?>)this.liveConnections), this.responseTimes.clone(), this.retriesAllDown);
            return;
        }
        final Connection newConn = this.balancer.pickConnection(this, Collections.unmodifiableList((List<?>)this.hostList), Collections.unmodifiableMap((Map<?, ?>)this.liveConnections), this.responseTimes.clone(), this.retriesAllDown);
        newConn.setTransactionIsolation(this.currentConn.getTransactionIsolation());
        newConn.setAutoCommit(this.currentConn.getAutoCommit());
        this.currentConn = newConn;
    }
    
    Object proxyIfInterfaceIsJdbc(final Object toProxy, final Class clazz) {
        final Class[] interfaces = clazz.getInterfaces();
        final int i = 0;
        if (i >= interfaces.length) {
            return toProxy;
        }
        final String packageName = interfaces[i].getPackage().getName();
        if ("java.sql".equals(packageName) || "javax.sql".equals(packageName)) {
            return Proxy.newProxyInstance(toProxy.getClass().getClassLoader(), interfaces, new ConnectionErrorFiringInvocationHandler(toProxy));
        }
        return this.proxyIfInterfaceIsJdbc(toProxy, interfaces[i]);
    }
    
    private static long getLocalTimeBestResolution() {
        if (LoadBalancingConnectionProxy.getLocalTimeMethod != null) {
            try {
                return (long)LoadBalancingConnectionProxy.getLocalTimeMethod.invoke(null, (Object[])null);
            }
            catch (IllegalArgumentException e) {}
            catch (IllegalAccessException e2) {}
            catch (InvocationTargetException ex) {}
        }
        return System.currentTimeMillis();
    }
    
    public synchronized void doPing() throws SQLException {
        if (this.isGlobalBlacklistEnabled()) {
            SQLException se = null;
            boolean foundHost = false;
            synchronized (this) {
                for (final String host : this.hostList) {
                    final Connection conn = this.liveConnections.get(host);
                    if (conn == null) {
                        continue;
                    }
                    try {
                        conn.ping();
                        foundHost = true;
                    }
                    catch (SQLException e) {
                        se = e;
                        this.addToGlobalBlacklist(host);
                    }
                }
            }
            if (!foundHost) {
                throw se;
            }
        }
        else {
            final Iterator allConns = this.liveConnections.values().iterator();
            while (allConns.hasNext()) {
                allConns.next().ping();
            }
        }
    }
    
    public void addToGlobalBlacklist(final String host) {
        if (this.isGlobalBlacklistEnabled()) {
            synchronized (LoadBalancingConnectionProxy.globalBlacklist) {
                LoadBalancingConnectionProxy.globalBlacklist.put(host, new Long(System.currentTimeMillis() + this.globalBlacklistTimeout));
            }
        }
    }
    
    public boolean isGlobalBlacklistEnabled() {
        return this.globalBlacklistTimeout > 0;
    }
    
    public Map getGlobalBlacklist() {
        if (!this.isGlobalBlacklistEnabled()) {
            return new HashMap(1);
        }
        final Map blacklistClone = new HashMap(LoadBalancingConnectionProxy.globalBlacklist.size());
        synchronized (LoadBalancingConnectionProxy.globalBlacklist) {
            blacklistClone.putAll(LoadBalancingConnectionProxy.globalBlacklist);
        }
        final Set keys = blacklistClone.keySet();
        keys.retainAll(this.hostList);
        if (keys.size() == this.hostList.size()) {
            return new HashMap(1);
        }
        final Iterator i = keys.iterator();
        while (i.hasNext()) {
            final String host = i.next();
            final Long timeout = LoadBalancingConnectionProxy.globalBlacklist.get(host);
            if (timeout != null && timeout < System.currentTimeMillis()) {
                synchronized (LoadBalancingConnectionProxy.globalBlacklist) {
                    LoadBalancingConnectionProxy.globalBlacklist.remove(host);
                }
                i.remove();
            }
        }
        return blacklistClone;
    }
    
    static /* synthetic */ Class class$(final String x0) {
        try {
            return Class.forName(x0);
        }
        catch (ClassNotFoundException x) {
            throw new NoClassDefFoundError(x.getMessage());
        }
    }
    
    static {
        try {
            LoadBalancingConnectionProxy.getLocalTimeMethod = ((LoadBalancingConnectionProxy.class$java$lang$System == null) ? (LoadBalancingConnectionProxy.class$java$lang$System = class$("java.lang.System")) : LoadBalancingConnectionProxy.class$java$lang$System).getMethod("nanoTime", (Class[])new Class[0]);
        }
        catch (SecurityException e) {}
        catch (NoSuchMethodException ex) {}
        LoadBalancingConnectionProxy.globalBlacklist = new HashMap();
    }
    
    protected class ConnectionErrorFiringInvocationHandler implements InvocationHandler
    {
        Object invokeOn;
        
        public ConnectionErrorFiringInvocationHandler(final Object toInvokeOn) {
            this.invokeOn = null;
            this.invokeOn = toInvokeOn;
        }
        
        public Object invoke(final Object proxy, final Method method, final Object[] args) throws Throwable {
            Object result = null;
            try {
                result = method.invoke(this.invokeOn, args);
                if (result != null) {
                    result = LoadBalancingConnectionProxy.this.proxyIfInterfaceIsJdbc(result, result.getClass());
                }
            }
            catch (InvocationTargetException e) {
                LoadBalancingConnectionProxy.this.dealWithInvocationException(e);
            }
            return result;
        }
    }
}
