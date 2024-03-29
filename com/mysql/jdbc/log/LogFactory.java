// 
// Decompiled by Procyon v0.5.30
// 

package com.mysql.jdbc.log;

import java.sql.SQLException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import com.mysql.jdbc.SQLError;
import com.mysql.jdbc.ExceptionInterceptor;

public class LogFactory
{
    static /* synthetic */ Class class$com$mysql$jdbc$log$Log;
    static /* synthetic */ Class class$java$lang$String;
    
    public static Log getLogger(final String className, final String instanceName, final ExceptionInterceptor exceptionInterceptor) throws SQLException {
        if (className == null) {
            throw SQLError.createSQLException("Logger class can not be NULL", "S1009", exceptionInterceptor);
        }
        if (instanceName == null) {
            throw SQLError.createSQLException("Logger instance name can not be NULL", "S1009", exceptionInterceptor);
        }
        try {
            Class loggerClass = null;
            try {
                loggerClass = Class.forName(className);
            }
            catch (ClassNotFoundException nfe) {
                loggerClass = Class.forName(((LogFactory.class$com$mysql$jdbc$log$Log == null) ? (LogFactory.class$com$mysql$jdbc$log$Log = class$("com.mysql.jdbc.log.Log")) : LogFactory.class$com$mysql$jdbc$log$Log).getPackage().getName() + "." + className);
            }
            final Constructor constructor = loggerClass.getConstructor((LogFactory.class$java$lang$String == null) ? (LogFactory.class$java$lang$String = class$("java.lang.String")) : LogFactory.class$java$lang$String);
            return constructor.newInstance(instanceName);
        }
        catch (ClassNotFoundException cnfe) {
            final SQLException sqlEx = SQLError.createSQLException("Unable to load class for logger '" + className + "'", "S1009", exceptionInterceptor);
            sqlEx.initCause(cnfe);
            throw sqlEx;
        }
        catch (NoSuchMethodException nsme) {
            final SQLException sqlEx = SQLError.createSQLException("Logger class does not have a single-arg constructor that takes an instance name", "S1009", exceptionInterceptor);
            sqlEx.initCause(nsme);
            throw sqlEx;
        }
        catch (InstantiationException inse) {
            final SQLException sqlEx = SQLError.createSQLException("Unable to instantiate logger class '" + className + "', exception in constructor?", "S1009", exceptionInterceptor);
            sqlEx.initCause(inse);
            throw sqlEx;
        }
        catch (InvocationTargetException ite) {
            final SQLException sqlEx = SQLError.createSQLException("Unable to instantiate logger class '" + className + "', exception in constructor?", "S1009", exceptionInterceptor);
            sqlEx.initCause(ite);
            throw sqlEx;
        }
        catch (IllegalAccessException iae) {
            final SQLException sqlEx = SQLError.createSQLException("Unable to instantiate logger class '" + className + "', constructor not public", "S1009", exceptionInterceptor);
            sqlEx.initCause(iae);
            throw sqlEx;
        }
        catch (ClassCastException cce) {
            final SQLException sqlEx = SQLError.createSQLException("Logger class '" + className + "' does not implement the '" + ((LogFactory.class$com$mysql$jdbc$log$Log == null) ? (LogFactory.class$com$mysql$jdbc$log$Log = class$("com.mysql.jdbc.log.Log")) : LogFactory.class$com$mysql$jdbc$log$Log).getName() + "' interface", "S1009", exceptionInterceptor);
            sqlEx.initCause(cce);
            throw sqlEx;
        }
    }
    
    static /* synthetic */ Class class$(final String x0) {
        try {
            return Class.forName(x0);
        }
        catch (ClassNotFoundException x) {
            throw new NoClassDefFoundError(x.getMessage());
        }
    }
}
