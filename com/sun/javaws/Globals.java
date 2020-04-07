// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.javaws;

import java.util.Enumeration;
import java.io.FileNotFoundException;
import java.io.FileInputStream;
import java.lang.reflect.Field;
import com.sun.deploy.util.Trace;
import java.util.Properties;
import java.util.ArrayList;
import java.io.InputStream;
import java.io.IOException;
import java.io.Reader;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.awt.GraphicsEnvironment;
import java.io.File;
import java.util.Locale;
import java.net.URL;

public class Globals
{
    private static final String JAVAWS_NAME = "javaws-1.5.0_04";
    public static final String JAVAWS_VERSION = "1.5.0_04";
    private static final String JNLP_VERSION = "1.5";
    private static final String WIN_ID = "Windows";
    private static boolean _isOffline;
    private static boolean _isImportMode;
    private static boolean _isSilentMode;
    private static boolean _isInstallMode;
    private static boolean _isSystemCache;
    private static boolean _isSecureMode;
    private static String _codebaseOverride;
    private static String[] _applicationArgs;
    private static boolean _createShortcut;
    private static boolean _createAssoc;
    private static URL _codebase;
    private static final String DEFAULT_LOGHOST = "localhost:8205";
    public static String BootClassPath;
    public static String JCOV;
    public static boolean TraceDefault;
    public static boolean TraceBasic;
    public static boolean TraceNetwork;
    public static boolean TraceSecurity;
    public static boolean TraceCache;
    public static boolean TraceExtensions;
    public static boolean TraceTemp;
    public static String LogToHost;
    public static boolean SupportJREinstallation;
    public static boolean OverrideSystemClassLoader;
    public static boolean TCKHarnessRun;
    public static boolean TCKResponse;
    public static final String JAVA_STARTED = "Java Started";
    public static final String JNLP_LAUNCHING = "JNLP Launching";
    public static final String NEW_VM_STARTING = "JVM Starting";
    public static final String JAVA_SHUTDOWN = "JVM Shutdown";
    public static final String CACHE_CLEAR_OK = "Cache Clear Success";
    public static final String CACHE_CLEAR_FAILED = "Cache Clear Failed";
    private static final Locale defaultLocale;
    private static final String defaultLocaleString;
    private static final String _javaVersionProperty;
    private static final boolean _atLeast14;
    private static final boolean _atLeast15;
    static /* synthetic */ Class class$com$sun$javaws$Globals;
    
    public static String getDefaultLocaleString() {
        return Globals.defaultLocaleString;
    }
    
    public static Locale getDefaultLocale() {
        return Globals.defaultLocale;
    }
    
    public static boolean isOffline() {
        return Globals._isOffline;
    }
    
    public static boolean createShortcut() {
        return Globals._createShortcut;
    }
    
    public static boolean createAssoc() {
        return Globals._createAssoc;
    }
    
    public static boolean isImportMode() {
        return Globals._isImportMode;
    }
    
    public static boolean isInstallMode() {
        return Globals._isInstallMode;
    }
    
    public static boolean isSilentMode() {
        return Globals._isSilentMode && (Globals._isImportMode || Globals._isInstallMode);
    }
    
    public static boolean isSystemCache() {
        return Globals._isSystemCache;
    }
    
    public static boolean isSecureMode() {
        return Globals._isSecureMode;
    }
    
    public static String getCodebaseOverride() {
        return Globals._codebaseOverride;
    }
    
    public static String[] getApplicationArgs() {
        return Globals._applicationArgs;
    }
    
    public static URL getCodebase() {
        return Globals._codebase;
    }
    
    public static void setCodebase(final URL codebase) {
        Globals._codebase = codebase;
    }
    
    public static void setCreateShortcut(final boolean createShortcut) {
        Globals._createShortcut = createShortcut;
    }
    
    public static void setCreateAssoc(final boolean createAssoc) {
        Globals._createAssoc = createAssoc;
    }
    
    public static void setOffline(final boolean isOffline) {
        Globals._isOffline = isOffline;
    }
    
    public static void setImportMode(final boolean isImportMode) {
        Globals._isImportMode = isImportMode;
    }
    
    public static void setSilentMode(final boolean isSilentMode) {
        Globals._isSilentMode = isSilentMode;
    }
    
    public static void setInstallMode(final boolean isInstallMode) {
        Globals._isInstallMode = isInstallMode;
    }
    
    public static void setSystemCache(final boolean isSystemCache) {
        Globals._isSystemCache = isSystemCache;
    }
    
    public static void setSecureMode(final boolean isSecureMode) {
        Globals._isSecureMode = isSecureMode;
    }
    
    public static void setCodebaseOverride(String string) {
        if (string != null && !string.endsWith(File.separator)) {
            string += File.separator;
        }
        Globals._codebaseOverride = string;
    }
    
    public static void setApplicationArgs(final String[] applicationArgs) {
        Globals._applicationArgs = applicationArgs;
    }
    
    public static boolean isHeadless() {
        return isJavaVersionAtLeast14() && GraphicsEnvironment.isHeadless();
    }
    
    public static boolean havePack200() {
        return isJavaVersionAtLeast15();
    }
    
    public static boolean isJavaVersionAtLeast15() {
        return Globals._atLeast15;
    }
    
    public static boolean isJavaVersionAtLeast14() {
        return Globals._atLeast14;
    }
    
    public static String getBuildID() {
        String line = null;
        final InputStream resourceAsStream = ((Globals.class$com$sun$javaws$Globals == null) ? (Globals.class$com$sun$javaws$Globals = class$("com.sun.javaws.Globals")) : Globals.class$com$sun$javaws$Globals).getResourceAsStream("/build.id");
        if (resourceAsStream != null) {
            final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(resourceAsStream));
            try {
                line = bufferedReader.readLine();
            }
            catch (IOException ex) {}
        }
        return (line == null || line.length() == 0) ? "<internal>" : line;
    }
    
    public static String getJavaVersion() {
        return Globals._javaVersionProperty;
    }
    
    public static String getComponentName() {
        return "javaws-1.5.0_04";
    }
    
    public static String getUserAgent() {
        return "JNLP/1.5 javaws/1.5.0_04 (" + getBuildID() + ")" + " J2SE/" + System.getProperty("java.version");
    }
    
    public static String[] parseOptions(final String[] array) {
        readOptionFile();
        final ArrayList list = new ArrayList<String>();
        int i = 0;
        int n = 0;
        while (i < array.length) {
            final String s = array[i++];
            if (s.startsWith("-XX:") && n == 0) {
                parseOption(s.substring(4), false);
            }
            else {
                list.add(s);
            }
            if (!s.startsWith("-")) {
                n = 1;
            }
        }
        setTCKOptions();
        return list.toArray(new String[list.size()]);
    }
    
    public static void getDebugOptionsFromProperties(final Properties properties) {
        int n = 0;
        while (true) {
            final String property = properties.getProperty("javaws.debug." + n);
            if (property == null) {
                break;
            }
            parseOption(property, true);
            ++n;
        }
    }
    
    private static void setTCKOptions() {
        if (Globals.TCKHarnessRun && Globals.LogToHost == null) {
            Trace.println("Warning: LogHost = null");
        }
    }
    
    private static void parseOption(final String s, final boolean b) {
        final int index = s.indexOf(61);
        String s2;
        String substring;
        if (index == -1) {
            s2 = s;
            substring = null;
        }
        else {
            s2 = s.substring(0, index);
            substring = s.substring(index + 1);
        }
        if (s2.length() > 0 && (s2.startsWith("-") || s2.startsWith("+"))) {
            s2 = s2.substring(1);
            substring = (s.startsWith("+") ? "true" : "false");
        }
        if (b && !s2.startsWith("x") && !s2.startsWith("Trace")) {
            s2 = null;
        }
        if (s2 != null && setOption(s2, substring)) {
            System.out.println("# Option: " + s2 + "=" + substring);
        }
        else {
            System.out.println("# Ignoring option: " + s);
        }
    }
    
    private static boolean setOption(final String s, final String s2) {
        final Class<? extends String> class1 = new String().getClass();
        final boolean b = true;
        try {
            final Field declaredField = new Globals().getClass().getDeclaredField(s);
            if ((declaredField.getModifiers() & 0x8) == 0x0) {
                return false;
            }
            final Class<?> type = declaredField.getType();
            if (type == class1) {
                declaredField.set(null, s2);
            }
            else if (type == Boolean.TYPE) {
                declaredField.setBoolean(null, Boolean.valueOf(s2));
            }
            else if (type == Integer.TYPE) {
                declaredField.setInt(null, Integer.parseInt(s2));
            }
            else if (type == Float.TYPE) {
                declaredField.setFloat(null, Float.parseFloat(s2));
            }
            else if (type == Double.TYPE) {
                declaredField.setDouble(null, Double.parseDouble(s2));
            }
            else {
                if (type != Long.TYPE) {
                    return false;
                }
                declaredField.setLong(null, Long.parseLong(s2));
            }
        }
        catch (IllegalAccessException ex) {
            return false;
        }
        catch (NoSuchFieldException ex2) {
            return false;
        }
        return b;
    }
    
    private static void readOptionFile() {
        FileInputStream fileInputStream;
        try {
            fileInputStream = new FileInputStream(".javawsrc");
        }
        catch (FileNotFoundException ex) {
            try {
                fileInputStream = new FileInputStream(System.getProperty("user.home") + File.separator + ".javawsrc");
            }
            catch (FileNotFoundException ex2) {
                return;
            }
        }
        try {
            final Properties properties = new Properties();
            properties.load(fileInputStream);
            final Enumeration<?> propertyNames = properties.propertyNames();
            if (propertyNames.hasMoreElements()) {
                System.out.println("\nSetting options from .javawsrc file:");
            }
            while (propertyNames.hasMoreElements()) {
                final String s = (String)propertyNames.nextElement();
                parseOption(s + "=" + properties.getProperty(s), false);
            }
        }
        catch (IOException ex3) {}
    }
    
    static /* synthetic */ Class class$(final String s) {
        try {
            return Class.forName(s);
        }
        catch (ClassNotFoundException ex) {
            throw new NoClassDefFoundError(ex.getMessage());
        }
    }
    
    static {
        Globals._isOffline = false;
        Globals._isImportMode = false;
        Globals._isSilentMode = false;
        Globals._isInstallMode = false;
        Globals._isSystemCache = false;
        Globals._isSecureMode = false;
        Globals._codebaseOverride = null;
        Globals._applicationArgs = null;
        Globals._createShortcut = false;
        Globals._createAssoc = false;
        Globals._codebase = null;
        Globals.BootClassPath = "NONE";
        Globals.JCOV = "NONE";
        Globals.TraceDefault = true;
        Globals.TraceBasic = false;
        Globals.TraceNetwork = false;
        Globals.TraceSecurity = false;
        Globals.TraceCache = false;
        Globals.TraceExtensions = false;
        Globals.TraceTemp = false;
        Globals.LogToHost = null;
        Globals.SupportJREinstallation = true;
        Globals.OverrideSystemClassLoader = true;
        Globals.TCKHarnessRun = false;
        Globals.TCKResponse = false;
        defaultLocale = Locale.getDefault();
        defaultLocaleString = getDefaultLocale().toString();
        _javaVersionProperty = System.getProperty("java.version");
        _atLeast14 = (!Globals._javaVersionProperty.startsWith("1.2") && !Globals._javaVersionProperty.startsWith("1.3"));
        _atLeast15 = (Globals._atLeast14 && !Globals._javaVersionProperty.startsWith("1.4"));
    }
}
