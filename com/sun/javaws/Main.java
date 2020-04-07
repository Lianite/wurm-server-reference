// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.javaws;

import java.util.Hashtable;
import com.sun.javaws.security.AppContextUtil;
import java.security.Security;
import java.util.Properties;
import com.sun.deploy.util.DialogListener;
import com.sun.javaws.util.JavawsDialogListener;
import javax.jnlp.ServiceManagerStub;
import com.sun.jnlp.JnlpLookupStub;
import java.net.Authenticator;
import com.sun.deploy.net.proxy.StaticProxyManager;
import com.sun.deploy.net.cookie.DeployCookieSelector;
import com.sun.deploy.net.proxy.DeployProxySelector;
import com.sun.deploy.services.ServiceManager;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.Reader;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.FileInputStream;
import com.sun.deploy.net.proxy.NSPreferences;
import java.awt.Component;
import com.sun.deploy.util.DialogFactory;
import java.net.Socket;
import com.sun.deploy.util.FileTraceListener;
import com.sun.deploy.util.SocketTraceListener;
import java.util.logging.Level;
import com.sun.deploy.util.LoggerTraceListener;
import com.sun.deploy.util.ConsoleHelper;
import com.sun.deploy.util.TraceListener;
import com.sun.deploy.util.ConsoleWindow;
import com.sun.deploy.util.ConsoleController;
import com.sun.deploy.util.ConsoleTraceListener;
import com.sun.javaws.util.JavawsConsoleController;
import java.util.ArrayList;
import java.io.File;
import com.sun.deploy.config.JREInfo;
import com.sun.javaws.jnl.LaunchDesc;
import com.sun.javaws.exceptions.LaunchDescException;
import com.sun.deploy.resources.ResourceManager;
import com.sun.javaws.exceptions.JNLPException;
import java.io.IOException;
import java.net.MalformedURLException;
import com.sun.javaws.exceptions.FailedDownloadingResourceException;
import java.net.URL;
import javax.net.ssl.SSLException;
import com.sun.javaws.exceptions.CouldNotLoadArgumentException;
import com.sun.javaws.jnl.LaunchDescFactory;
import com.sun.javaws.exceptions.TooManyArgumentsException;
import com.sun.javaws.exceptions.CacheAccessException;
import java.awt.Frame;
import com.sun.javaws.ui.CacheViewer;
import com.sun.deploy.util.Trace;
import com.sun.deploy.util.TraceLevel;
import com.sun.javaws.cache.Cache;
import com.sun.deploy.config.Config;
import java.awt.Toolkit;
import java.util.Date;
import com.sun.jnlp.JNLPClassLoader;
import com.sun.deploy.util.PerfLogger;
import java.io.DataInputStream;

public class Main
{
    private static boolean _isViewer;
    private static boolean _launchingAllowed;
    private static ThreadGroup _systemTG;
    private static ThreadGroup _securityTG;
    private static ThreadGroup _launchTG;
    private static String[] _tempfile;
    private static DataInputStream _tckStream;
    private static long t0;
    private static long t1;
    private static long t2;
    private static long t3;
    private static long t4;
    private static long t5;
    private static boolean _timeing;
    private static boolean uninstall;
    
    public static void main(String[] array) {
        PerfLogger.setStartTime("JavaWebStart main started");
        Thread.currentThread().setContextClassLoader(JNLPClassLoader.createClassLoader());
        if (Main._timeing) {
            Main.t0 = new Date().getTime();
        }
        Toolkit.getDefaultToolkit();
        if (Main._timeing) {
            Main.t1 = new Date().getTime();
        }
        Main._launchingAllowed = Config.isConfigValid();
        if (Main._timeing) {
            Main.t2 = new Date().getTime();
        }
        JPDA.setup();
        array = Globals.parseOptions(array);
        if (Main._timeing) {
            Main.t3 = new Date().getTime();
        }
        initTrace();
        if (Main._timeing) {
            Main.t4 = new Date().getTime();
        }
        updateCache();
        array = parseArgs(array);
        if (array.length > 0) {
            Main._tempfile[0] = array[0];
        }
        if (Cache.canWrite()) {
            setupBrowser();
            JnlpxArgs.verify();
            initializeExecutionEnvironment();
            if (Main.uninstall) {
                uninstallCache((array.length > 0) ? array[0] : null);
            }
            if (Globals.TCKHarnessRun) {
                tckprintln("Java Started");
            }
            if (array.length == 0) {
                Main._isViewer = true;
            }
            if (!Main._isViewer) {
                launchApp(array, true);
            }
            if (Main._isViewer) {
                JnlpxArgs.removeArgumentFile(array);
                try {
                    if (Main._timeing) {
                        Main.t5 = new Date().getTime();
                        Trace.println("startup times: \n      toolkit: " + (Main.t1 - Main.t0) + "\n" + "       config: " + (Main.t2 - Main.t1) + "\n" + "         args: " + (Main.t3 - Main.t2) + "\n" + "        trace: " + (Main.t4 - Main.t3) + "\n" + "     the rest: " + (Main.t5 - Main.t4) + "\n" + "", TraceLevel.BASIC);
                    }
                    Trace.println("Launching Cache Viewer", TraceLevel.BASIC);
                    Trace.flush();
                    CacheViewer.main(array);
                }
                catch (Exception ex) {
                    LaunchErrorDialog.show(null, ex, true);
                }
            }
        }
        else {
            LaunchErrorDialog.show(null, new CacheAccessException(Globals.isSystemCache()), true);
        }
        Trace.flush();
    }
    
    public static void launchApp(final String[] array, final boolean b) {
        if (array.length > 1) {
            JnlpxArgs.removeArgumentFile(array);
            LaunchErrorDialog.show(null, new TooManyArgumentsException(array), b);
            return;
        }
        LaunchDesc buildDescriptor;
        try {
            buildDescriptor = LaunchDescFactory.buildDescriptor(array[0]);
        }
        catch (IOException ex2) {
            JnlpxArgs.removeArgumentFile(array);
            JNLPException ex = new CouldNotLoadArgumentException(array[0], ex2);
            Label_0124: {
                if (Globals.isJavaVersionAtLeast14()) {
                    if (!(ex2 instanceof SSLException)) {
                        if (ex2.getMessage() == null || ex2.getMessage().toLowerCase().indexOf("https") == -1) {
                            break Label_0124;
                        }
                    }
                    try {
                        ex = new FailedDownloadingResourceException(new URL(array[0]), null, ex2);
                    }
                    catch (MalformedURLException ex3) {
                        Trace.ignoredException((Exception)ex3);
                    }
                }
            }
            LaunchErrorDialog.show(null, ex, b);
            return;
        }
        catch (JNLPException ex4) {
            JnlpxArgs.removeArgumentFile(array);
            LaunchErrorDialog.show(null, ex4, b);
            return;
        }
        Globals.setCodebase(buildDescriptor.getCodebase());
        if (buildDescriptor.getLaunchType() == 5) {
            JnlpxArgs.removeArgumentFile(array);
            final String internalCommand = buildDescriptor.getInternalCommand();
            if (internalCommand.equals("viewer")) {
                Main._isViewer = true;
            }
            else if (internalCommand.equals("player")) {
                Main._isViewer = true;
            }
            else {
                launchJavaControlPanel(internalCommand);
                systemExit(0);
            }
        }
        else if (Main._launchingAllowed) {
            new Launcher(buildDescriptor).launch(array, b);
        }
        else {
            LaunchErrorDialog.show(null, new LaunchDescException(buildDescriptor, ResourceManager.getString("enterprize.cfg.mandatory", Config.getEnterprizeString()), null), b);
        }
    }
    
    public static void importApp(final String s) {
        final String[] array = { s };
        Globals.setImportMode(true);
        Globals.setSilentMode(true);
        launchApp(array, false);
        Launcher.checkCacheMax();
    }
    
    public static void launchJavaControlPanel(final String s) {
        final String[] array = new String[7];
        String property = System.getProperty("javaplugin.user.profile");
        if (property == null) {
            property = "";
        }
        array[0] = Config.getInstance().toExecArg(JREInfo.getDefaultJavaPath());
        array[1] = "-cp";
        array[2] = Config.getInstance().toExecArg(Config.getJavaHome() + File.separator + "lib" + File.separator + "deploy.jar");
        array[3] = Config.getInstance().toExecArg("-Djavaplugin.user.profile=" + property);
        array[4] = "com.sun.deploy.panel.ControlPanel";
        array[5] = "-tab";
        array[6] = ((s == null) ? "general" : s);
        Trace.println("Launching Control Panel: " + array[0] + " " + array[1] + " " + array[2] + " " + array[3] + " " + array[4] + " " + array[5] + " ", TraceLevel.BASIC);
        try {
            Runtime.getRuntime().exec(array);
        }
        catch (IOException ex) {
            Trace.ignoredException((Exception)ex);
        }
    }
    
    private static void uninstallCache(final String s) {
        int uninstall = -1;
        try {
            uninstall = uninstall(s);
        }
        catch (Exception ex) {
            LaunchErrorDialog.show(null, ex, !Globals.isSilentMode());
        }
        systemExit(uninstall);
    }
    
    private static String[] parseArgs(final String[] array) {
        final ArrayList list = new ArrayList<String>();
        for (int i = 0; i < array.length; ++i) {
            if (!array[i].startsWith("-")) {
                list.add(array[i]);
            }
            else if (array[i].equals("-Xclearcache")) {
                try {
                    Cache.remove();
                    if (Cache.getCacheSize() > 0L) {
                        System.err.println("Could not clean all entries  in cache since they are in use");
                        if (Globals.TCKHarnessRun) {
                            tckprintln("Cache Clear Failed");
                        }
                        systemExit(-1);
                    }
                }
                catch (IOException ex) {
                    Trace.println("Clear cached failed: " + ex.getMessage());
                    if (Globals.TCKHarnessRun) {
                        tckprintln("Cache Clear Failed");
                    }
                    systemExit(-1);
                }
                if (Globals.TCKHarnessRun) {
                    tckprintln("Cache Clear Success");
                }
            }
            else if (array[i].equals("-offline")) {
                JnlpxArgs.SetIsOffline();
                Globals.setOffline(true);
            }
            else if (!array[i].equals("-online")) {
                if (!array[i].equals("-Xnosplash")) {
                    if (array[i].equals("-installer")) {
                        Globals.setInstallMode(true);
                    }
                    else if (array[i].equals("-uninstall")) {
                        Globals.setInstallMode(Main.uninstall = true);
                    }
                    else if (array[i].equals("-updateVersions")) {
                        systemExit(0);
                    }
                    else if (array[i].equals("-import")) {
                        Globals.setImportMode(true);
                    }
                    else if (array[i].equals("-silent")) {
                        Globals.setSilentMode(true);
                    }
                    else if (array[i].equals("-shortcut")) {
                        Globals.setCreateShortcut(true);
                    }
                    else if (array[i].equals("-association")) {
                        Globals.setCreateAssoc(true);
                    }
                    else if (array[i].equals("-codebase")) {
                        if (i + 1 < array.length) {
                            final String codebaseOverride = array[++i];
                            try {
                                new URL(codebaseOverride);
                            }
                            catch (MalformedURLException ex2) {
                                LaunchErrorDialog.show(null, ex2, true);
                            }
                            Globals.setCodebaseOverride(codebaseOverride);
                        }
                    }
                    else if (array[i].equals("-system")) {
                        Globals.setSystemCache(true);
                    }
                    else if (array[i].equals("-secure")) {
                        Globals.setSecureMode(true);
                    }
                    else if (array[i].equals("-open") || array[i].equals("-print")) {
                        if (i + 1 < array.length) {
                            Globals.setApplicationArgs(new String[] { array[i++], array[i] });
                        }
                    }
                    else if (array[i].equals("-viewer")) {
                        Main._isViewer = true;
                    }
                    else {
                        Trace.println("unsupported option: " + array[i], TraceLevel.BASIC);
                    }
                }
            }
        }
        final String[] array2 = new String[list.size()];
        for (int j = 0; j < array2.length; ++j) {
            array2[j] = list.get(j);
        }
        return array2;
    }
    
    private static void initTrace() {
        Trace.redirectStdioStderr();
        Trace.resetTraceLevel();
        Trace.setInitialTraceLevel();
        if (Globals.TraceBasic) {
            Trace.setBasicTrace(true);
        }
        if (Globals.TraceNetwork) {
            Trace.setNetTrace(true);
        }
        if (Globals.TraceCache) {
            Trace.setCacheTrace(true);
        }
        if (Globals.TraceSecurity) {
            Trace.setSecurityTrace(true);
        }
        if (Globals.TraceExtensions) {
            Trace.setExtTrace(true);
        }
        if (Globals.TraceTemp) {
            Trace.setTempTrace(true);
        }
        if (Config.getProperty("deployment.console.startup.mode").equals("SHOW") && !Globals.isHeadless()) {
            final JavawsConsoleController instance = JavawsConsoleController.getInstance();
            final ConsoleTraceListener consoleTraceListener = new ConsoleTraceListener((ConsoleController)instance);
            final ConsoleWindow create = ConsoleWindow.create((ConsoleController)instance);
            instance.setConsole(create);
            if (consoleTraceListener != null) {
                consoleTraceListener.setConsole(create);
                Trace.addTraceListener((TraceListener)consoleTraceListener);
                consoleTraceListener.print(ConsoleHelper.displayVersion() + "\n");
                consoleTraceListener.print(ConsoleHelper.displayHelp());
            }
        }
        final SocketTraceListener initSocketTrace = initSocketTrace();
        if (initSocketTrace != null) {
            Trace.addTraceListener((TraceListener)initSocketTrace);
        }
        final FileTraceListener initFileTrace = initFileTrace();
        if (initFileTrace != null) {
            Trace.addTraceListener((TraceListener)initFileTrace);
        }
        if (Globals.isJavaVersionAtLeast14() && Config.getBooleanProperty("deployment.log")) {
            try {
                String s = Config.getProperty("deployment.javaws.logFileName");
                File parentFile = new File(Config.getLogDirectory());
                if (s != null && s != "") {
                    if (s.compareToIgnoreCase("TEMP") != 0) {
                        final File file = new File(s);
                        if (file.isDirectory()) {
                            s = "";
                        }
                        else {
                            parentFile = file.getParentFile();
                            if (parentFile != null) {
                                parentFile.mkdirs();
                            }
                        }
                    }
                    else {
                        s = "";
                    }
                }
                if (s == "") {
                    parentFile.mkdirs();
                    s = Config.getLogDirectory() + File.separator + "javaws.log";
                }
                final LoggerTraceListener loggerTraceListener = new LoggerTraceListener("com.sun.deploy", s);
                if (loggerTraceListener != null) {
                    loggerTraceListener.getLogger().setLevel(Level.ALL);
                    JavawsConsoleController.getInstance().setLogger(loggerTraceListener.getLogger());
                    Trace.addTraceListener((TraceListener)loggerTraceListener);
                }
            }
            catch (Exception ex) {
                Trace.println("can not create log file in directory: " + Config.getLogDirectory(), TraceLevel.BASIC);
            }
        }
    }
    
    private static FileTraceListener initFileTrace() {
        if (Config.getBooleanProperty("deployment.trace")) {
            File tempFile = null;
            String s = Config.getProperty("deployment.user.logdir");
            final String property = Config.getProperty("deployment.javaws.traceFileName");
            try {
                if (property != null && property != "" && property.compareToIgnoreCase("TEMP") != 0) {
                    tempFile = new File(property);
                    if (!tempFile.isDirectory()) {
                        final int lastIndex = property.lastIndexOf(File.separator);
                        if (lastIndex != -1) {
                            s = property.substring(0, lastIndex);
                        }
                    }
                    else {
                        tempFile = null;
                    }
                }
                final File file = new File(s);
                file.mkdirs();
                if (tempFile == null) {
                    tempFile = File.createTempFile("javaws", ".trace", file);
                }
                return new FileTraceListener(tempFile, true);
            }
            catch (Exception ex) {
                Trace.println("cannot create trace file in Directory: " + s, TraceLevel.BASIC);
            }
        }
        return null;
    }
    
    private static SocketTraceListener initSocketTrace() {
        if (Globals.LogToHost == null) {
            return null;
        }
        final String logToHost = Globals.LogToHost;
        int n = 0;
        int n2;
        if (logToHost.charAt(0) == '[' && (n2 = logToHost.indexOf(1, 93)) != -1) {
            n = 1;
        }
        else {
            n2 = logToHost.indexOf(":");
        }
        final String substring = logToHost.substring(n, n2);
        if (substring == null) {
            return null;
        }
        int int1;
        try {
            int1 = Integer.parseInt(logToHost.substring(logToHost.lastIndexOf(58) + 1));
        }
        catch (NumberFormatException ex2) {
            int1 = -1;
        }
        if (int1 < 0) {
            return null;
        }
        final SocketTraceListener socketTraceListener = new SocketTraceListener(substring, int1);
        if (socketTraceListener != null) {
            final Socket socket = socketTraceListener.getSocket();
            if (Globals.TCKResponse && socket != null) {
                try {
                    Main._tckStream = new DataInputStream(socket.getInputStream());
                }
                catch (IOException ex) {
                    Trace.ignoredException((Exception)ex);
                }
            }
        }
        return socketTraceListener;
    }
    
    private static int uninstall(final String s) {
        if (s == null) {
            Trace.println("Uninstall all!", TraceLevel.BASIC);
            uninstallAll();
            if (Globals.TCKHarnessRun) {
                tckprintln("Cache Clear Success");
            }
        }
        else {
            Trace.println("Uninstall: " + s, TraceLevel.BASIC);
            LaunchDesc buildDescriptor = null;
            try {
                buildDescriptor = LaunchDescFactory.buildDescriptor(s);
            }
            catch (IOException ex) {
                Trace.ignoredException((Exception)ex);
            }
            catch (JNLPException ex2) {
                Trace.ignoredException((Exception)ex2);
            }
            if (buildDescriptor != null) {
                LocalApplicationProperties localApplicationProperties;
                if (buildDescriptor.isInstaller() || buildDescriptor.isLibrary()) {
                    localApplicationProperties = Cache.getLocalApplicationProperties(s, buildDescriptor);
                }
                else {
                    localApplicationProperties = Cache.getLocalApplicationProperties(buildDescriptor.getCanonicalHome(), buildDescriptor);
                }
                if (localApplicationProperties != null) {
                    Cache.remove(s, localApplicationProperties, buildDescriptor);
                    Cache.clean();
                    if (Globals.TCKHarnessRun) {
                        tckprintln("Cache Clear Success");
                    }
                    return 0;
                }
            }
            Trace.println("Error uninstalling!", TraceLevel.BASIC);
            if (Globals.TCKHarnessRun) {
                tckprintln("Cache Clear Failed");
            }
            if (!Globals.isSilentMode()) {
                SplashScreen.hide();
                DialogFactory.showErrorDialog((Component)null, ResourceManager.getMessage("uninstall.failedMessage"), ResourceManager.getMessage("uninstall.failedMessageTitle"));
            }
        }
        return 0;
    }
    
    private static void uninstallAll() {
        Cache.remove();
    }
    
    private static void setupBrowser() {
        if (Config.getBooleanProperty("deployment.capture.mime.types")) {
            setupNS6();
            setupOpera();
            Config.setBooleanProperty("deployment.capture.mime.types", false);
        }
    }
    
    private static void setupOpera() {
        final OperaSupport operaSupport = BrowserSupport.getInstance().getOperaSupport();
        if (operaSupport != null && operaSupport.isInstalled()) {
            operaSupport.enableJnlp(new File(JREInfo.getDefaultJavaPath()), Config.getBooleanProperty("deployment.update.mime.types"));
        }
    }
    
    private static void setupNS6() {
        final String ns6MailCapInfo = BrowserSupport.getInstance().getNS6MailCapInfo();
        final String s = "user_pref(\"browser.helperApps.neverAsk.openFile\", \"application%2Fx-java-jnlp-file\");\n";
        final File file = new File(System.getProperty("user.home") + "/.mozilla/appreg");
        File ns6PrefsFile = null;
        try {
            ns6PrefsFile = NSPreferences.getNS6PrefsFile(file);
        }
        catch (IOException ex5) {
            Trace.println("cannot determine NS6 prefs.js location", TraceLevel.BASIC);
        }
        if (ns6PrefsFile == null) {
            return;
        }
        try {
            final FileInputStream fileInputStream = new FileInputStream(ns6PrefsFile);
            final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(fileInputStream));
            String s2 = "";
            boolean b = true;
            boolean b2 = false;
        Label_0132:
            while (true) {
                if (ns6MailCapInfo == null) {
                    b2 = false;
                    break Label_0132;
                }
                b2 = true;
                while (true) {
                    try {
                        while (true) {
                            final String line = bufferedReader.readLine();
                            if (line == null) {
                                break;
                            }
                            s2 = s2 + line + "\n";
                            if (line.indexOf("x-java-jnlp-file") != -1) {
                                b = false;
                            }
                            if (ns6MailCapInfo == null || line.indexOf(".mime.types") == -1) {
                                continue;
                            }
                            b2 = false;
                        }
                        fileInputStream.close();
                        break;
                    }
                    catch (IOException ex) {
                        Trace.ignoredException((Exception)ex);
                        continue;
                    }
                    continue Label_0132;
                }
                break;
            }
            if (!b && !b2) {
                return;
            }
            if (b) {
                s2 += s;
            }
            if (ns6MailCapInfo != null && b2) {
                s2 += ns6MailCapInfo;
            }
            final FileOutputStream fileOutputStream = new FileOutputStream(ns6PrefsFile);
            try {
                fileOutputStream.write(s2.getBytes());
                fileOutputStream.close();
            }
            catch (IOException ex2) {
                Trace.ignoredException((Exception)ex2);
            }
        }
        catch (FileNotFoundException ex3) {
            Trace.ignoredException((Exception)ex3);
            String string = "";
            if (ns6MailCapInfo != null) {
                string += ns6MailCapInfo;
            }
            final String string2 = string + s;
            try {
                final FileOutputStream fileOutputStream2 = new FileOutputStream(ns6PrefsFile);
                fileOutputStream2.write(string2.getBytes());
                fileOutputStream2.close();
            }
            catch (IOException ex4) {
                Trace.ignoredException((Exception)ex4);
            }
        }
    }
    
    private static void updateCache() {
        if (Config.getProperty("deployment.javaws.cachedir") != null) {
            Cache.updateCache();
            Config.setProperty("deployment.javaws.cachedir", (String)null);
            Config.storeIfDirty();
        }
    }
    
    private static void initializeExecutionEnvironment() {
        final boolean b = Config.getOSName().indexOf("Windows") != -1;
        final boolean javaVersionAtLeast15 = Globals.isJavaVersionAtLeast15();
        if (b) {
            if (javaVersionAtLeast15) {
                ServiceManager.setService(33024);
            }
            else {
                ServiceManager.setService(16640);
            }
        }
        else if (javaVersionAtLeast15) {
            ServiceManager.setService(36864);
        }
        else {
            ServiceManager.setService(20480);
        }
        final Properties properties = System.getProperties();
        ((Hashtable<String, String>)properties).put("http.auth.serializeRequests", "true");
        if (Globals.isJavaVersionAtLeast14()) {
            final String s = ((Hashtable<String, String>)properties).get("java.protocol.handler.pkgs");
            if (s != null) {
                ((Hashtable<String, String>)properties).put("java.protocol.handler.pkgs", s + "|com.sun.deploy.net.protocol");
            }
            else {
                ((Hashtable<String, String>)properties).put("java.protocol.handler.pkgs", "com.sun.deploy.net.protocol");
            }
        }
        properties.setProperty("javawebstart.version", Globals.getComponentName());
        try {
            DeployProxySelector.reset();
            DeployCookieSelector.reset();
        }
        catch (Throwable t) {
            StaticProxyManager.reset();
        }
        if (Config.getBooleanProperty("deployment.security.authenticator")) {
            Authenticator.setDefault((Authenticator)JAuthenticator.getInstance(null));
        }
        javax.jnlp.ServiceManager.setServiceManagerStub(new JnlpLookupStub());
        addToSecurityProperty("package.access", "com.sun.javaws");
        addToSecurityProperty("package.access", "com.sun.deploy");
        addToSecurityProperty("package.definition", "com.sun.javaws");
        addToSecurityProperty("package.definition", "com.sun.deploy");
        addToSecurityProperty("package.definition", "com.sun.jnlp");
        addToSecurityProperty("package.access", "org.mozilla.jss");
        addToSecurityProperty("package.definition", "org.mozilla.jss");
        DialogFactory.addDialogListener((DialogListener)new JavawsDialogListener());
        if (((Hashtable<String, String>)properties).get("https.protocols") == null && !Config.getBooleanProperty("deployment.security.TLSv1")) {
            ((Hashtable<String, String>)properties).put("https.protocols", "SSLv3,SSLv2Hello");
        }
    }
    
    private static void addToSecurityProperty(final String s, final String s2) {
        final String property = Security.getProperty(s);
        Trace.println("property " + s + " value " + property, TraceLevel.SECURITY);
        String string;
        if (property != null) {
            string = property + "," + s2;
        }
        else {
            string = s2;
        }
        Security.setProperty(s, string);
        Trace.println("property " + s + " new value " + string, TraceLevel.SECURITY);
    }
    
    public static void systemExit(final int n) {
        JnlpxArgs.removeArgumentFile(Main._tempfile);
        SplashScreen.hide();
        Trace.flush();
        System.exit(n);
    }
    
    static boolean isViewer() {
        return Main._isViewer;
    }
    
    public static final ThreadGroup getLaunchThreadGroup() {
        initializeThreadGroups();
        return Main._launchTG;
    }
    
    public static final ThreadGroup getSecurityThreadGroup() {
        initializeThreadGroups();
        return Main._securityTG;
    }
    
    private static void initializeThreadGroups() {
        if (Main._securityTG == null) {
            Main._systemTG = Thread.currentThread().getThreadGroup();
            while (Main._systemTG.getParent() != null) {
                Main._systemTG = Main._systemTG.getParent();
            }
            Main._securityTG = new ThreadGroup(Main._systemTG, "javawsSecurityThreadGroup");
            new Thread(Main._securityTG, new Runnable() {
                public void run() {
                    AppContextUtil.createSecurityAppContext();
                }
            }).start();
            Main._launchTG = new ThreadGroup(Main._systemTG, "javawsApplicationThreadGroup");
        }
    }
    
    public static synchronized void tckprintln(final String s) {
        final long currentTimeMillis = System.currentTimeMillis();
        Trace.println("##TCKHarnesRun##:" + currentTimeMillis + ":" + Runtime.getRuntime().hashCode() + ":" + Thread.currentThread() + ":" + s);
        if (Main._tckStream != null) {
            try {
                while (Main._tckStream.readLong() < currentTimeMillis) {}
            }
            catch (IOException ex) {
                System.err.println("Warning:Exceptions occurred, while logging to logSocket");
                ex.printStackTrace(System.err);
            }
        }
    }
    
    static {
        Main._isViewer = false;
        Main._launchingAllowed = false;
        Main._tempfile = new String[1];
        Main._tckStream = null;
        Main._timeing = true;
        Main.uninstall = false;
    }
}
