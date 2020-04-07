// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.javaws;

import java.util.Hashtable;
import java.util.Vector;
import java.net.URL;
import java.util.StringTokenizer;
import com.sun.deploy.config.Config;
import java.util.Properties;
import com.sun.deploy.util.Trace;
import com.sun.deploy.util.TraceLevel;
import com.sun.javaws.util.GeneralUtil;
import com.sun.deploy.config.JREInfo;
import java.io.File;

public class JnlpxArgs
{
    private static final String ARG_JVM = "jnlpx.jvm";
    private static final String ARG_SPLASHPORT = "jnlpx.splashport";
    private static final String ARG_REMOVE = "jnlpx.remove";
    private static final String ARG_OFFLINE = "jnlpx.offline";
    private static final String ARG_HEAPSIZE = "jnlpx.heapsize";
    private static final String ARG_VMARGS = "jnlpx.vmargs";
    private static final String ARG_HOME = "jnlpx.home";
    private static File _currentJVMCommand;
    private static final String JAVAWS_JAR;
    private static final String DEPLOY_JAR;
    
    public static int getSplashPort() {
        try {
            return Integer.parseInt(System.getProperty("jnlpx.splashport", "-1"));
        }
        catch (NumberFormatException ex) {
            return -1;
        }
    }
    
    public static String getVMArgs() {
        return System.getProperty("jnlpx.vmargs");
    }
    
    static File getJVMCommand() {
        if (JnlpxArgs._currentJVMCommand == null) {
            String s = System.getProperty("jnlpx.jvm", "").trim();
            if (s.startsWith("X")) {
                s = JREInfo.getDefaultJavaPath();
            }
            if (s.startsWith("\"")) {
                s = s.substring(1);
            }
            if (s.endsWith("\"")) {
                s = s.substring(0, s.length() - 1);
            }
            JnlpxArgs._currentJVMCommand = new File(s);
        }
        return JnlpxArgs._currentJVMCommand;
    }
    
    public static boolean shouldRemoveArgumentFile() {
        return getBooleanProperty("jnlpx.remove");
    }
    
    public static void setShouldRemoveArgumentFile(final String s) {
        System.setProperty("jnlpx.remove", s);
    }
    
    public static boolean isOffline() {
        return getBooleanProperty("jnlpx.offline");
    }
    
    public static void SetIsOffline() {
        System.setProperty("jnlpx.offline", "true");
    }
    
    public static String getHeapSize() {
        return System.getProperty("jnlpx.heapsize");
    }
    
    public static long getInitialHeapSize() {
        final String heapSize = getHeapSize();
        if (heapSize == null) {
            return -1L;
        }
        final String substring = heapSize.substring(heapSize.lastIndexOf(61) + 1);
        return GeneralUtil.heapValToLong(substring.substring(0, substring.lastIndexOf(44)));
    }
    
    public static long getMaxHeapSize() {
        final String heapSize = getHeapSize();
        if (heapSize == null) {
            return -1L;
        }
        final String substring = heapSize.substring(heapSize.lastIndexOf(61) + 1);
        return GeneralUtil.heapValToLong(substring.substring(substring.lastIndexOf(44) + 1, substring.length()));
    }
    
    public static boolean isCurrentRunningJREHeap(final long n, final long n2) {
        final long initialHeapSize = getInitialHeapSize();
        final long maxHeapSize = getMaxHeapSize();
        Trace.println("isCurrentRunningJREHeap: passed args: " + n + ", " + n2, TraceLevel.BASIC);
        Trace.println("JnlpxArgs is " + initialHeapSize + ", " + maxHeapSize, TraceLevel.BASIC);
        return initialHeapSize == n && maxHeapSize == n2;
    }
    
    public static boolean isAuxArgsMatch(final Properties properties, final String s) {
        final String[] secureProperties = Config.getSecureProperties();
        for (int i = 0; i < secureProperties.length; ++i) {
            final String s2 = secureProperties[i];
            if (properties.containsKey(s2)) {
                final Object value = ((Hashtable<K, Object>)properties).get(s2);
                if (value != null && !value.equals(System.getProperty(s2))) {
                    return false;
                }
            }
        }
        if (s != null && getVMArgs() == null) {
            final StringTokenizer stringTokenizer = new StringTokenizer(s);
            while (stringTokenizer.hasMoreTokens()) {
                if (Config.isSecureVmArg(stringTokenizer.nextToken())) {
                    return false;
                }
            }
        }
        return true;
    }
    
    private static boolean heapSizesValid(final long n, final long n2) {
        return n != -1L || n2 != -1L;
    }
    
    public static String[] getArgumentList(final String s, final long n, final long n2, final Properties properties, final String s2) {
        String string = "-Djnlpx.heapsize=NULL,NULL";
        String string2 = "";
        String string3 = "";
        if (heapSizesValid(n, n2)) {
            string = "-Djnlpx.heapsize=" + n + "," + n2;
            if (n > 0L) {
                string2 = "-Xms" + n;
            }
            if (n2 > 0L) {
                string3 = "-Xmx" + n2;
            }
        }
        final String desiredVMArgs = getDesiredVMArgs(getVMArgs(), s2);
        final String[] array = { "-Xbootclasspath/a:" + Config.getJavaHome() + File.separator + "lib" + File.separator + JnlpxArgs.JAVAWS_JAR + File.pathSeparator + Config.getJavaHome() + File.separator + "lib" + File.separator + JnlpxArgs.DEPLOY_JAR, "-classpath", File.pathSeparator + Config.getJavaHome() + File.separator + "lib" + File.separator + JnlpxArgs.DEPLOY_JAR, string2, string3, (desiredVMArgs != null) ? ("-Djnlpx.vmargs=" + desiredVMArgs) : "", "-Djnlpx.jvm=" + s, "-Djnlpx.splashport=" + getSplashPort(), "-Djnlpx.home=" + Config.getJavaHome() + File.separator + "bin", "-Djnlpx.remove=" + (shouldRemoveArgumentFile() ? "true" : "false"), "-Djnlpx.offline=" + (isOffline() ? "true" : "false"), string, "-Djava.security.policy=" + getPolicyURLString(), "-DtrustProxy=true", "-Xverify:remote", useJCOV(), useBootClassPath(), useJpiProfile(), useDebugMode(), useDebugVMMode(), "com.sun.javaws.Main", setTCKHarnessOption(), useLogToHost() };
        int n3 = 0;
        for (int i = 0; i < array.length; ++i) {
            if (!array[i].equals("")) {
                ++n3;
            }
        }
        final String[] vmArgList = getVMArgList(properties, s2);
        final int length = vmArgList.length;
        final String[] array2 = new String[n3 + length];
        int j;
        for (j = 0; j < length; ++j) {
            array2[j] = vmArgList[j];
        }
        for (int k = 0; k < array.length; ++k) {
            if (!array[k].equals("")) {
                array2[j++] = array[k];
            }
        }
        return array2;
    }
    
    static String getPolicyURLString() {
        String s = Config.getJavaHome() + File.separator + "lib" + File.separator + "security" + File.separator + "javaws.policy";
        try {
            s = new URL("file", "", s).toString();
        }
        catch (Exception ex) {}
        return s;
    }
    
    private static String getDesiredVMArgs(final String s, final String s2) {
        if (s == null && s2 != null) {
            String string = "";
            final StringTokenizer stringTokenizer = new StringTokenizer(s2, " \t\n\r\f\"");
            while (stringTokenizer.hasMoreTokens()) {
                final String nextToken = stringTokenizer.nextToken();
                if (Config.isSecureVmArg(nextToken)) {
                    if (string.length() == 0) {
                        string = nextToken;
                    }
                    else {
                        string = string + " " + nextToken;
                    }
                }
            }
            if (string.length() > 0) {
                return string;
            }
        }
        return s;
    }
    
    private static String[] getVMArgList(final Properties properties, final String s) {
        final Vector vector = new Vector<String>();
        final String vmArgs;
        if ((vmArgs = getVMArgs()) != null) {
            final StringTokenizer stringTokenizer = new StringTokenizer(vmArgs, " \t\n\r\f\"");
            while (stringTokenizer.hasMoreTokens()) {
                vector.add(stringTokenizer.nextToken());
            }
        }
        if (s != null) {
            final StringTokenizer stringTokenizer2 = new StringTokenizer(s, " \t\n\r\f\"");
            while (stringTokenizer2.hasMoreTokens()) {
                final String nextToken = stringTokenizer2.nextToken();
                if (Config.isSecureVmArg(nextToken) && !vector.contains(nextToken)) {
                    vector.add(nextToken);
                }
            }
        }
        final String[] secureProperties = Config.getSecureProperties();
        for (int i = 0; i < secureProperties.length; ++i) {
            final String s2 = secureProperties[i];
            if (properties.containsKey(s2)) {
                final String string = "-D" + s2 + "=" + ((Hashtable<K, Object>)properties).get(s2);
                if (!vector.contains(string)) {
                    vector.add(string);
                }
            }
        }
        final String[] array = new String[vector.size()];
        for (int j = 0; j < vector.size(); ++j) {
            array[j] = new String(vector.elementAt(j));
        }
        return array;
    }
    
    public static String useLogToHost() {
        if (Globals.LogToHost != null) {
            return "-XX:LogToHost=" + Globals.LogToHost;
        }
        return "";
    }
    
    public static String setTCKHarnessOption() {
        if (Globals.TCKHarnessRun) {
            return "-XX:TCKHarnessRun=true";
        }
        return "";
    }
    
    public static String useBootClassPath() {
        if (Globals.BootClassPath.equals("NONE")) {
            return "";
        }
        return "-Xbootclasspath" + Globals.BootClassPath;
    }
    
    public static String useJpiProfile() {
        final String property = System.getProperty("javaplugin.user.profile");
        if (property != null) {
            return "-Djavaplugin.user.profile=" + property;
        }
        return "";
    }
    
    public static String useJCOV() {
        if (Globals.JCOV.equals("NONE")) {
            return "";
        }
        return "-Xrunjcov:file=" + Globals.JCOV;
    }
    
    public static String useDebugMode() {
        if (Config.isDebugMode()) {
            return "-Ddeploy.debugMode=true";
        }
        return "";
    }
    
    public static String useDebugVMMode() {
        if (Config.isDebugVMMode()) {
            return "-Ddeploy.useDebugJavaVM=true";
        }
        return "";
    }
    
    public static void removeArgumentFile(final String[] array) {
        if (shouldRemoveArgumentFile() && array != null && array.length > 0) {
            new File(array[0]).delete();
        }
    }
    
    static void verify() {
        Trace.println("Java part started", TraceLevel.BASIC);
        Trace.println("jnlpx.jvm: " + getJVMCommand(), TraceLevel.BASIC);
        Trace.println("jnlpx.splashport: " + getSplashPort(), TraceLevel.BASIC);
        Trace.println("jnlpx.remove: " + shouldRemoveArgumentFile(), TraceLevel.BASIC);
        Trace.println("jnlpx.heapsize: " + getHeapSize(), TraceLevel.BASIC);
    }
    
    private static boolean getBooleanProperty(final String s) {
        final String property = System.getProperty(s, "false");
        return property != null && property.equals("true");
    }
    
    static {
        JnlpxArgs._currentJVMCommand = null;
        JAVAWS_JAR = (Config.isDebugMode() ? "javaws_g.jar" : "javaws.jar");
        DEPLOY_JAR = (Config.isDebugMode() ? "deploy_g.jar" : "deploy.jar");
    }
}
