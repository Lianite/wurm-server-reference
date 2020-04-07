// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server;

import java.util.HashSet;
import java.util.logging.LogRecord;
import java.util.Collections;
import java.util.HashMap;
import java.io.File;
import com.wurmonline.server.console.CommandReader;
import com.wurmonline.server.utils.SimpleArgumentParser;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.ConsoleHandler;
import java.util.logging.Handler;
import java.util.logging.Formatter;
import java.util.logging.FileHandler;
import java.util.Arrays;
import java.util.Iterator;
import java.rmi.AlreadyBoundException;
import com.wurmonline.server.webinterface.RegistryStarter;
import com.wurmonline.server.webinterface.WebInterfaceImpl;
import java.net.InetAddress;
import com.wurmonline.server.items.CreationEntryCreator;
import java.io.IOException;
import java.util.Set;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import java.util.Map;

public final class ServerLauncher
{
    private static final int MAX_BYTES_PER_LOGFILE = 10240000;
    private static final int NUM_LOG_FILES = 200;
    Server server;
    private static final Map<String, String> SPECIALIST_LOGGER_FILES;
    private static final Map<String, SimpleFormatter> SPECIALIST_LOGGER_FORMATS;
    private static final Map<String, Logger> SPECIALIST_LOGGERS;
    boolean started;
    private static final String ARG_PLAYER_SERVER = "ps";
    private static final Set<String> ALLOWED_ARGUMENTS;
    
    public ServerLauncher() {
        this.started = false;
        createLoggers();
    }
    
    public final Server getServer() {
        return this.server;
    }
    
    public final boolean wasStarted() {
        return this.started;
    }
    
    public void runServer() throws IOException {
        this.runServer(false, false);
    }
    
    public void runServer(final boolean ps, final boolean isOfflineServer) throws IOException {
        (this.server = Server.getInstance()).setIsPS(ps);
        this.server.steamHandler.setIsOfflienServer(isOfflineServer);
        try {
            this.server.startRunning();
            this.started = true;
        }
        catch (Exception ex) {
            System.out.println(ex.getMessage());
            ex.printStackTrace();
            this.server.shutDown("Problem running the server - " + ex.getMessage(), ex);
        }
        CreationEntryCreator.createCreationEntries();
        if (Constants.useIncomingRMI) {
            try {
                final InetAddress byAddress = InetAddress.getByAddress(this.server.getInternalIp());
                RegistryStarter.startRegistry(new WebInterfaceImpl(Servers.localServer.REGISTRATION_PORT), byAddress, Servers.localServer.RMI_PORT);
                System.out.println("RMI listening on " + byAddress + ':' + Servers.localServer.RMI_PORT);
                System.out.println("RMI Registry listening on " + byAddress + ':' + Servers.localServer.REGISTRATION_PORT);
            }
            catch (AlreadyBoundException abe) {
                System.out.println("The port " + Servers.localServer.RMI_PORT + " is already bound./n Registry RMI communication won't work.");
            }
        }
        else {
            System.out.println("Incoming RMI is disabled");
        }
    }
    
    public static final void stopLoggers() {
        Logger logger = Logger.getLogger("com.wurmonline");
        if (logger != null) {
            removeLoggerHandlers(logger);
        }
        for (final String loggerName : ServerLauncher.SPECIALIST_LOGGERS.keySet()) {
            logger = Logger.getLogger(loggerName);
            if (logger != null) {
                removeLoggerHandlers(logger);
            }
        }
    }
    
    public static final void createLoggers() {
        final Logger logger = Logger.getLogger("com.wurmonline");
        final String loggingProperty = System.getProperty("java.util.logging.config.file", null);
        if (loggingProperty == null) {
            System.out.println("java.util.logging.config.file system property is not set so hardcoding logging");
            logger.setUseParentHandlers(false);
            final Handler[] h = logger.getHandlers();
            System.out.println("com.wurmonline logger handlers: " + Arrays.toString(h));
            for (int i = 0; i != h.length; ++i) {
                logger.removeHandler(h[i]);
            }
            try {
                final String logsPath = createLogPath();
                final FileHandler fh = new FileHandler(logsPath + "wurm.log", 10240000, 200, true);
                fh.setFormatter(new SimpleFormatter());
                logger.addHandler(fh);
                if (Constants.devmode) {
                    logger.addHandler(new ConsoleHandler());
                }
            }
            catch (IOException ie) {
                System.err.println("no redirection possible, stopping server");
                System.exit(1);
            }
        }
        else {
            System.out.println("java.util.logging.config.file system property is set to " + loggingProperty);
            System.out.println("com.wurmonline logger level: " + logger.getLevel());
            System.out.println("com.wurmonline logger UseParentHandlers: " + logger.getUseParentHandlers());
        }
        logger.log(Level.OFF, "\n----------------------------------------------------------------\nWurm Server logging started at " + new Date() + "\n----------------------------------------------------------------");
        for (final String loggerName : ServerLauncher.SPECIALIST_LOGGER_FILES.keySet()) {
            ServerLauncher.SPECIALIST_LOGGERS.put(loggerName, createLoggerFileHandlers(loggerName, ServerLauncher.SPECIALIST_LOGGER_FILES.get(loggerName)));
        }
    }
    
    private static void removeLoggerHandlers(final Logger logger) {
        for (final Handler handler : logger.getHandlers()) {
            if (handler != null) {
                try {
                    handler.flush();
                }
                catch (Exception ex) {}
                try {
                    handler.close();
                }
                catch (Exception ex2) {}
                logger.removeHandler(handler);
            }
        }
    }
    
    private static Logger createLoggerFileHandlers(final String loggerName, final String logFileName) {
        final Logger logger = Logger.getLogger(loggerName);
        logger.setUseParentHandlers(false);
        removeLoggerHandlers(logger);
        try {
            final String logsPath = createLogPath();
            final FileHandler fh = new FileHandler(logsPath + logFileName, 10240000, 200, true);
            fh.setFormatter(new SimpleFormatter());
            if (ServerLauncher.SPECIALIST_LOGGER_FORMATS.containsKey(loggerName)) {
                fh.setFormatter(ServerLauncher.SPECIALIST_LOGGER_FORMATS.get(loggerName));
            }
            logger.addHandler(fh);
            if (Constants.devmode) {
                logger.addHandler(new ConsoleHandler());
            }
        }
        catch (IOException ie) {
            System.err.println("no redirection possible, stopping server");
            System.exit(1);
        }
        return logger;
    }
    
    static String getServerStartBanner() {
        final StringBuilder lBuilder = new StringBuilder(1024);
        lBuilder.append("\n========================================================================================================\n\n");
        lBuilder.append("888       888                                     .d8888b. \n");
        lBuilder.append("888   o   888                                     d88P  Y88b  \n");
        lBuilder.append("888  d8b  888                                     Y88b.  \n");
        lBuilder.append("888 d888b 888 888  888 888d888 88888b.d88b.        'Y888b.    .d88b.  888d888 888  888  .d88b.  888d888 \n");
        lBuilder.append("888d88888b888 888  888 888P   888 '888 '88b          'Y88b. d8P  Y8b 888P'   888  888 d8P  Y8b 888P'   \n");
        lBuilder.append("88888P Y88888 888  888 888     888  888  888            '888 88888888 888     Y88  88P 88888888 888    \n");
        lBuilder.append("8888P   Y8888 Y88b 888 888     888  888  888      Y88b  d88P Y8b.     888      Y8bd8P  Y8b.     888   \n");
        lBuilder.append("888P     Y888  'Y88888 888     888  888  888       'Y8888P'   'Y8888  888       Y88P    'Y8888  888 \n");
        lBuilder.append("\n========================================================================================================\n");
        return lBuilder.toString();
    }
    
    public static void main(final String[] args) throws IOException {
        try {
            final SimpleArgumentParser parser = new SimpleArgumentParser(args, ServerLauncher.ALLOWED_ARGUMENTS);
            if (parser.hasUnknownOptions()) {
                System.exit(1);
            }
            final boolean isPs = parser.hasFlag("ps");
            final Runtime lRuntime = Runtime.getRuntime();
            System.out.println(getServerStartBanner());
            System.out.println("Wurm Server application started at " + new Date());
            System.out.println("Operating system: " + System.getProperty("os.name") + " (arch: " + System.getProperty("os.arch") + ", version: " + System.getProperty("os.version") + ")");
            System.out.println("Java version: " + System.getProperty("java.version"));
            System.out.println("Java home: " + System.getProperty("java.home"));
            System.out.println("Java vendor: " + System.getProperty("java.vendor") + " (" + System.getProperty("java.vendor.url") + ")");
            System.out.println("Available CPUs: " + lRuntime.availableProcessors());
            System.out.println("Java Classpath: " + System.getProperty("java.class.path"));
            System.out.println("Free memory: " + lRuntime.freeMemory() / 1048576L + " MB, Total memory: " + lRuntime.totalMemory() / 1048576L + " MB, Max memory: " + lRuntime.maxMemory() / 1048576L + " MB");
            System.out.println("\n==================================================================\n");
            final ServerLauncher lServerLauncher = new ServerLauncher();
            lServerLauncher.runServer(isPs, false);
            new Thread(new CommandReader(lServerLauncher.getServer(), System.in), "Console Command Reader").start();
        }
        catch (Exception ex) {
            throw ex;
        }
        finally {
            System.out.println("\n==================================================================\n");
            System.out.println("Wurm Server launcher finished at " + new Date());
            System.out.println("\n==================================================================\n");
        }
    }
    
    public static String createLogPath() {
        final String logsPath = Constants.dbHost + "/Logs/";
        final File newDirectory = new File(logsPath);
        if (!newDirectory.exists()) {
            newDirectory.mkdirs();
        }
        return logsPath;
    }
    
    static {
        final HashMap<String, String> specialistLoggers = new HashMap<String, String>();
        specialistLoggers.put("Cheaters", "cheaters.log");
        specialistLoggers.put("Money", "money.log");
        specialistLoggers.put("Chat", "chat.log");
        specialistLoggers.put("ca-help", "ca-help.log");
        specialistLoggers.put("IntraServer", "IntraServer.log");
        specialistLoggers.put("Reimbursements", "reimbursements.log");
        specialistLoggers.put("stacktraces", "stacktraces.log");
        specialistLoggers.put("deletions", "deletions.log");
        specialistLoggers.put("ItemDebug", "item-debug.log");
        specialistLoggers.put("affinities", "affinities.log");
        SPECIALIST_LOGGER_FILES = Collections.unmodifiableMap((Map<? extends String, ? extends String>)specialistLoggers);
        final SimpleFormatter chatFormat = new SimpleFormatter() {
            private static final String format = "[%1$tF %1$tT] %2$s %n";
            
            @Override
            public synchronized String format(final LogRecord record) {
                return String.format("[%1$tF %1$tT] %2$s %n", new Date(record.getMillis()), record.getMessage());
            }
        };
        final HashMap<String, SimpleFormatter> specialistFormatters = new HashMap<String, SimpleFormatter>();
        specialistFormatters.put("ca-help", chatFormat);
        specialistFormatters.put("Chat", chatFormat);
        SPECIALIST_LOGGER_FORMATS = Collections.unmodifiableMap((Map<? extends String, ? extends SimpleFormatter>)specialistFormatters);
        SPECIALIST_LOGGERS = new HashMap<String, Logger>(ServerLauncher.SPECIALIST_LOGGER_FILES.size());
        final HashSet<String> allowedArguments = new HashSet<String>();
        allowedArguments.add("ps");
        ALLOWED_ARGUMENTS = Collections.unmodifiableSet((Set<? extends String>)allowedArguments);
    }
}
