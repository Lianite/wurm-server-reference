// 
// Decompiled by Procyon v0.5.30
// 

package com.mysql.jdbc.integration.c3p0;

import java.sql.SQLException;
import com.mysql.jdbc.CommunicationsException;
import java.sql.Statement;
import com.mchange.v2.c3p0.C3P0ProxyConnection;
import java.sql.Connection;
import java.lang.reflect.Method;
import com.mchange.v2.c3p0.QueryConnectionTester;

public final class MysqlConnectionTester implements QueryConnectionTester
{
    private static final long serialVersionUID = 3256444690067896368L;
    private static final Object[] NO_ARGS_ARRAY;
    private Method pingMethod;
    static /* synthetic */ Class class$com$mysql$jdbc$Connection;
    
    public MysqlConnectionTester() {
        try {
            this.pingMethod = ((MysqlConnectionTester.class$com$mysql$jdbc$Connection == null) ? (MysqlConnectionTester.class$com$mysql$jdbc$Connection = class$("com.mysql.jdbc.Connection")) : MysqlConnectionTester.class$com$mysql$jdbc$Connection).getMethod("ping", (Class[])null);
        }
        catch (Exception ex) {}
    }
    
    public int activeCheckConnection(final Connection con) {
        try {
            if (this.pingMethod != null) {
                if (con instanceof com.mysql.jdbc.Connection) {
                    ((com.mysql.jdbc.Connection)con).ping();
                }
                else {
                    final C3P0ProxyConnection castCon = (C3P0ProxyConnection)con;
                    castCon.rawConnectionOperation(this.pingMethod, C3P0ProxyConnection.RAW_CONNECTION, MysqlConnectionTester.NO_ARGS_ARRAY);
                }
            }
            else {
                Statement pingStatement = null;
                try {
                    pingStatement = con.createStatement();
                    pingStatement.executeQuery("SELECT 1").close();
                }
                finally {
                    if (pingStatement != null) {
                        pingStatement.close();
                    }
                }
            }
            return 0;
        }
        catch (Exception ex) {
            return -1;
        }
    }
    
    public int statusOnException(final Connection arg0, final Throwable throwable) {
        if (throwable instanceof CommunicationsException || "com.mysql.jdbc.exceptions.jdbc4.CommunicationsException".equals(throwable.getClass().getName())) {
            return -1;
        }
        if (!(throwable instanceof SQLException)) {
            return -1;
        }
        final String sqlState = ((SQLException)throwable).getSQLState();
        if (sqlState != null && sqlState.startsWith("08")) {
            return -1;
        }
        return 0;
    }
    
    public int activeCheckConnection(final Connection arg0, final String arg1) {
        return 0;
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
        NO_ARGS_ARRAY = new Object[0];
    }
}
