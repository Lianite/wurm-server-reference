// 
// Decompiled by Procyon v0.5.30
// 

package org.seamless.util.logging;

import java.util.logging.Logger;
import java.io.IOException;
import java.io.ByteArrayInputStream;
import java.util.Map;
import java.util.Properties;
import java.util.Iterator;
import java.util.logging.Handler;
import java.util.List;
import java.util.logging.LogManager;
import java.util.ArrayList;
import java.io.InputStream;

public class LoggingUtil
{
    public static final String DEFAULT_CONFIG = "default-logging.properties";
    
    public static void loadDefaultConfiguration() throws Exception {
        loadDefaultConfiguration(null);
    }
    
    public static void loadDefaultConfiguration(InputStream is) throws Exception {
        if (System.getProperty("java.util.logging.config.file") != null) {
            return;
        }
        if (is == null) {
            is = Thread.currentThread().getContextClassLoader().getResourceAsStream("default-logging.properties");
        }
        if (is == null) {
            return;
        }
        final List<String> handlerNames = new ArrayList<String>();
        LogManager.getLogManager().readConfiguration(spliceHandlers(is, handlerNames));
        final Handler[] handlers = instantiateHandlers(handlerNames);
        resetRootHandler(handlers);
    }
    
    public static Handler[] instantiateHandlers(final List<String> handlerNames) throws Exception {
        final List<Handler> list = new ArrayList<Handler>();
        for (final String handlerName : handlerNames) {
            list.add((Handler)Thread.currentThread().getContextClassLoader().loadClass(handlerName).newInstance());
        }
        return list.toArray(new Handler[list.size()]);
    }
    
    public static InputStream spliceHandlers(final InputStream is, final List<String> handlers) throws IOException {
        final Properties props = new Properties();
        props.load(is);
        final StringBuilder sb = new StringBuilder();
        final List<String> handlersProperties = new ArrayList<String>();
        for (final Map.Entry<Object, Object> entry : props.entrySet()) {
            if (entry.getKey().equals("handlers")) {
                handlersProperties.add(entry.getValue().toString());
            }
            else {
                sb.append(entry.getKey()).append("=").append(entry.getValue()).append("\n");
            }
        }
        for (final String handlersProperty : handlersProperties) {
            final String[] arr$;
            final String[] handlerClasses = arr$ = handlersProperty.trim().split(" ");
            for (final String handlerClass : arr$) {
                handlers.add(handlerClass.trim());
            }
        }
        return new ByteArrayInputStream(sb.toString().getBytes("ISO-8859-1"));
    }
    
    public static void resetRootHandler(final Handler... h) {
        final Logger rootLogger = LogManager.getLogManager().getLogger("");
        final Handler[] arr$;
        final Handler[] handlers = arr$ = rootLogger.getHandlers();
        for (final Handler handler : arr$) {
            rootLogger.removeHandler(handler);
        }
        for (final Handler handler : h) {
            if (handler != null) {
                LogManager.getLogManager().getLogger("").addHandler(handler);
            }
        }
    }
}
