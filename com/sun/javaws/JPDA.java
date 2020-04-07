// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.javaws;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.UnknownHostException;
import java.net.InetAddress;
import com.sun.deploy.util.DialogFactory;
import com.sun.deploy.config.JREInfo;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;

public class JPDA
{
    private static final int NAPN = -1;
    public static final int JWS = 1;
    public static final int JWSJNL = 2;
    public static final int JNL = 3;
    private static String JWS_str;
    private static String JWSJNL_str;
    private static String JNL_str;
    private static String dbgNotificationTitle;
    private static JPDA o_envCurrentJRE;
    private static JPDA o_envNextJRE;
    private static String s_envCurrentJRE;
    private static int _debuggeeType;
    private static boolean _jpdaConfigIsFromCmdLine;
    private static String _portsList;
    private static int[] _portsPool;
    private int _selectedPort;
    private boolean _portIsAutoSelected;
    private String _excludedportsList;
    private int[] _excludedportsPool;
    private String _jreProductVersion;
    private int _jreNestingLevel;
    private static boolean _jreUsesDashClassic;
    private String _javaMainArgsList;
    private static boolean _nextJreRunsInJpdaMode;
    
    public JPDA() {
        this._selectedPort = -1;
        this._portIsAutoSelected = false;
        this._excludedportsList = null;
        this._excludedportsPool = null;
        this._jreProductVersion = null;
        this._jreNestingLevel = -1;
        this._javaMainArgsList = null;
    }
    
    public static int getDebuggeeType() {
        return JPDA._debuggeeType;
    }
    
    public static void setup() {
        JPDA.s_envCurrentJRE = getProperty("jnlpx.jpda.env");
        JPDA.o_envCurrentJRE = decodeJpdaEnv(JPDA.s_envCurrentJRE);
        if (getProperty("jpda.notification") != null) {
            showJpdaNotificationWindow(JPDA.o_envCurrentJRE);
            Main.systemExit(0);
        }
    }
    
    public static JPDA decodeJpdaEnv(final String s) {
        if (s == null || s.equals("")) {
            return null;
        }
        final JPDA jpda = new JPDA();
        final StringTokenizer stringTokenizer = new StringTokenizer(s, "&");
        final int countTokens = stringTokenizer.countTokens();
        final boolean[] array = new boolean[countTokens];
        for (int i = 0; i < countTokens; ++i) {
            array[i] = true;
        }
        try {
            while (stringTokenizer.hasMoreTokens()) {
                final String[] tokenizeJpdaEnvEntry = tokenizeJpdaEnvEntry(stringTokenizer.nextToken(), "=");
                if (array[0] && tokenizeJpdaEnvEntry[0].equals("debuggeeType")) {
                    array[0] = false;
                    if (tokenizeJpdaEnvEntry[1].equals(JPDA.JWS_str)) {
                        JPDA._debuggeeType = 1;
                    }
                    else if (tokenizeJpdaEnvEntry[1].equals(JPDA.JWSJNL_str)) {
                        JPDA._debuggeeType = 2;
                    }
                    else {
                        if (!tokenizeJpdaEnvEntry[1].equals(JPDA.JNL_str)) {
                            continue;
                        }
                        JPDA._debuggeeType = 3;
                    }
                }
                else if (array[1] && tokenizeJpdaEnvEntry[0].equals("jpdaConfigIsFromCmdLine")) {
                    array[1] = false;
                    if (!tokenizeJpdaEnvEntry[1].equals("1")) {
                        continue;
                    }
                    JPDA._jpdaConfigIsFromCmdLine = true;
                }
                else if (array[2] && tokenizeJpdaEnvEntry[0].equals("portsList")) {
                    array[2] = false;
                    JPDA._portsList = tokenizeJpdaEnvEntry[1];
                    if (JPDA._portsList.equals("NONE")) {
                        continue;
                    }
                    final String[] tokenizeJpdaEnvEntry2 = tokenizeJpdaEnvEntry(JPDA._portsList, ",");
                    JPDA._portsPool = new int[tokenizeJpdaEnvEntry2.length];
                    for (int j = 0; j < tokenizeJpdaEnvEntry2.length; ++j) {
                        JPDA._portsPool[j] = string2Int(tokenizeJpdaEnvEntry2[j]);
                    }
                }
                else if (array[3] && tokenizeJpdaEnvEntry[0].equals("selectedPort")) {
                    array[3] = false;
                    jpda._selectedPort = string2Int(tokenizeJpdaEnvEntry[1]);
                }
                else if (array[4] && tokenizeJpdaEnvEntry[0].equals("portIsAutoSelected")) {
                    array[4] = false;
                    if (!tokenizeJpdaEnvEntry[1].equals("1")) {
                        continue;
                    }
                    jpda._portIsAutoSelected = true;
                }
                else if (array[5] && tokenizeJpdaEnvEntry[0].equals("excludedportsList")) {
                    array[5] = false;
                    jpda._excludedportsList = tokenizeJpdaEnvEntry[1];
                    if (jpda._excludedportsList.equals("NONE")) {
                        continue;
                    }
                    final String[] tokenizeJpdaEnvEntry3 = tokenizeJpdaEnvEntry(jpda._excludedportsList, ",");
                    jpda._excludedportsPool = new int[tokenizeJpdaEnvEntry3.length];
                    for (int k = 0; k < tokenizeJpdaEnvEntry3.length; ++k) {
                        jpda._excludedportsPool[k] = string2Int(tokenizeJpdaEnvEntry3[k]);
                    }
                }
                else if (array[6] && tokenizeJpdaEnvEntry[0].equals("jreProductVersion")) {
                    array[6] = false;
                    jpda._jreProductVersion = tokenizeJpdaEnvEntry[1];
                }
                else if (array[7] && tokenizeJpdaEnvEntry[0].equals("jreNestingLevel")) {
                    array[7] = false;
                    jpda._jreNestingLevel = string2Int(tokenizeJpdaEnvEntry[1]);
                }
                else if (array[8] && tokenizeJpdaEnvEntry[0].equals("jreUsesDashClassic")) {
                    array[8] = false;
                    if (!tokenizeJpdaEnvEntry[1].equals("1")) {
                        continue;
                    }
                    JPDA._jreUsesDashClassic = true;
                }
                else {
                    if (!array[9] || !tokenizeJpdaEnvEntry[0].equals("javaMainArgsList")) {
                        continue;
                    }
                    array[9] = false;
                    jpda._javaMainArgsList = tokenizeJpdaEnvEntry[1];
                }
            }
        }
        catch (NoSuchElementException ex) {
            return null;
        }
        return jpda;
    }
    
    public static String encodeJpdaEnv(final JPDA jpda) {
        if (jpda == null) {
            return "-Djnlpx.jpda.env";
        }
        return "-Djnlpx.jpda.env=debuggeeType=" + JPDA._debuggeeType + "&jpdaConfigIsFromCmdLine=" + (JPDA._jpdaConfigIsFromCmdLine ? "1" : "0") + "&portsList=" + JPDA._portsList + "&selectedPort=" + jpda._selectedPort + "&portIsAutoSelected=" + (jpda._portIsAutoSelected ? "1" : "0") + "&excludedportsList=" + jpda._excludedportsList + "&jreProductVersion=" + jpda._jreProductVersion + "&jreNestingLevel=" + jpda._jreNestingLevel + "&jreUsesDashClassic=" + (JPDA._jreUsesDashClassic ? "1" : "0") + "&javaMainArgsList=" + jpda._javaMainArgsList;
    }
    
    private static void setJpdaEnvForNextJRE(final boolean b, final boolean b2, final String[] array, final JREInfo jreInfo) {
        if (JPDA._debuggeeType == 0 || JPDA._debuggeeType == 1) {
            JPDA.o_envNextJRE = JPDA.o_envCurrentJRE;
            JPDA._nextJreRunsInJpdaMode = false;
            return;
        }
        final JPDA o_envCurrentJRE = JPDA.o_envCurrentJRE;
        JPDA jpda = new JPDA();
        jpda._jreProductVersion = jreInfo.getProduct();
        jpda._jreNestingLevel = 1 + o_envCurrentJRE._jreNestingLevel;
        jpda._javaMainArgsList = o_envCurrentJRE._javaMainArgsList;
        if (array.length > 0) {
            jpda._javaMainArgsList = array[0];
        }
        for (int i = 1; i < array.length; ++i) {
            final StringBuffer sb = new StringBuffer();
            final JPDA jpda2 = jpda;
            jpda2._javaMainArgsList = sb.append(jpda2._javaMainArgsList).append(",").append(array[i]).toString();
        }
        JPDA._nextJreRunsInJpdaMode = true;
        if (JPDA._debuggeeType == 3) {
            jpda._selectedPort = o_envCurrentJRE._selectedPort;
            jpda._portIsAutoSelected = o_envCurrentJRE._portIsAutoSelected;
            jpda._excludedportsList = o_envCurrentJRE._excludedportsList;
            jpda._excludedportsPool = o_envCurrentJRE._excludedportsPool;
            JPDA.o_envNextJRE = jpda;
            return;
        }
        if (b) {
            if (o_envCurrentJRE._excludedportsPool == null) {
                jpda._excludedportsList = "" + o_envCurrentJRE._selectedPort;
                jpda._excludedportsPool = new int[] { o_envCurrentJRE._selectedPort };
            }
            else {
                jpda._excludedportsList = o_envCurrentJRE._excludedportsList + "," + o_envCurrentJRE._selectedPort;
                jpda._excludedportsPool = new int[o_envCurrentJRE._excludedportsPool.length + 1];
                int j;
                for (j = 0; j < o_envCurrentJRE._excludedportsPool.length; ++j) {
                    jpda._excludedportsPool[j] = o_envCurrentJRE._excludedportsPool[j];
                }
                jpda._excludedportsPool[j] = o_envCurrentJRE._selectedPort;
            }
        }
        jpda._selectedPort = jpda.getAvailableServerPort(b, b2);
        if (jpda._selectedPort < 0) {
            jpda = null;
            JPDA._nextJreRunsInJpdaMode = false;
        }
        JPDA.o_envNextJRE = jpda;
    }
    
    private static String[] tokenizeJpdaEnvEntry(final String s, final String s2) {
        final StringTokenizer stringTokenizer = new StringTokenizer(s, s2);
        final String[] array = new String[stringTokenizer.countTokens()];
        try {
            int n = 0;
            while (stringTokenizer.hasMoreTokens()) {
                array[n] = stringTokenizer.nextToken();
                ++n;
            }
        }
        catch (NoSuchElementException ex) {
            ex.printStackTrace();
            return null;
        }
        return array;
    }
    
    public static void showJpdaNotificationWindow(final JPDA jpda) {
        if (jpda == null) {
            DialogFactory.showErrorDialog("ERROR: No JPDA environment.", JPDA.dbgNotificationTitle);
        }
        else {
            DialogFactory.showInformationDialog((Object)("Starting JRE (version " + jpda._jreProductVersion + ") in JPDA debugging mode, trying server socket port " + jpda._selectedPort + " on this host (" + getLocalHostName() + ").\n\n        Main class  =  " + "com.sun.javaws.Main" + "\n        Arguments to main()  =  " + jpda._javaMainArgsList + "\n\nTo start debugging, please connect a JPDA debugging client to this host at indicated port.\n\n\nDiagnostics:\n\n     Debugging directive was obtained from\n     " + (JPDA._jpdaConfigIsFromCmdLine ? "command line:" : "\"javaws-jpda.cfg\" configuration file:") + "\n        - JRE " + (JPDA._jreUsesDashClassic ? "uses" : "doesn't use") + "  -classic  option.\n        - Port " + (jpda._portIsAutoSelected ? "automatically selected (by OS);\n          unable to find or use user-specified\n          ports list." : (" selected from user-specified list:\n          " + JPDA._portsList + "."))), JPDA.dbgNotificationTitle + " (" + ((jpda._jreNestingLevel < 1) ? "JWS" : "JNL") + ")");
        }
    }
    
    private static String getProperty(final String s) {
        String property = null;
        try {
            property = System.getProperty(s);
        }
        catch (SecurityException ex) {
            ex.printStackTrace();
            return property;
        }
        catch (NullPointerException ex2) {
            ex2.printStackTrace();
            return property;
        }
        catch (IllegalArgumentException ex3) {
            ex3.printStackTrace();
            return property;
        }
        return property;
    }
    
    private static int string2Int(final String s) {
        int intValue = -1;
        try {
            intValue = new Integer(s);
        }
        catch (NumberFormatException ex) {
            ex.printStackTrace();
            return intValue;
        }
        return intValue;
    }
    
    private static String getLocalHostName() {
        try {
            return InetAddress.getLocalHost().getHostName();
        }
        catch (UnknownHostException ex) {
            return "localhost";
        }
    }
    
    public int getAvailableServerPort(final boolean b, final boolean b2) {
        if (JPDA._portsPool == null) {
            return -1;
        }
        this._portIsAutoSelected = false;
        for (int i = 0; i < JPDA._portsPool.length; ++i) {
            final int n;
            if ((n = JPDA._portsPool[i]) != 0) {
                if (!b || !this.isExcludedPort(n)) {
                    try {
                        new ServerSocket(n).close();
                        return n;
                    }
                    catch (IOException ex) {}
                }
            }
        }
        if (b2) {
            try {
                int localPort;
                do {
                    final ServerSocket serverSocket = new ServerSocket(0);
                    localPort = serverSocket.getLocalPort();
                    serverSocket.close();
                } while (b && this.isExcludedPort(localPort));
                this._portIsAutoSelected = true;
                return localPort;
            }
            catch (IOException ex2) {}
        }
        return -1;
    }
    
    private boolean isExcludedPort(final int n) {
        if (this._excludedportsPool == null) {
            return false;
        }
        for (int i = 0; i < this._excludedportsPool.length; ++i) {
            if (n == this._excludedportsPool[i]) {
                return true;
            }
        }
        return false;
    }
    
    public static String[] JpdaSetup(final String[] array, final JREInfo jreInfo) {
        setJpdaEnvForNextJRE(true, true, array, jreInfo);
        if (JPDA._nextJreRunsInJpdaMode) {
            final String[] array2 = new String[array.length + (JPDA._jreUsesDashClassic ? 5 : 2)];
            int n = 0;
            array2[n++] = array[0];
            if (JPDA._jreUsesDashClassic) {
                array2[n++] = "-classic";
                array2[n++] = "-Xnoagent";
                array2[n++] = "-Djava.compiler=NONE";
            }
            array2[n++] = "-Xdebug";
            array2[n++] = "-Xrunjdwp:transport=dt_socket,server=y,address=" + JPDA.o_envNextJRE._selectedPort + ",suspend=y";
            for (int i = 1; i < array.length; array2[n++] = array[i++]) {}
            showJpdaNotificationWindow(JPDA.o_envNextJRE);
            return array2;
        }
        return array;
    }
    
    static {
        JPDA.JWS_str = "1";
        JPDA.JWSJNL_str = "2";
        JPDA.JNL_str = "3";
        JPDA.dbgNotificationTitle = "JPDA Notification";
        JPDA.o_envCurrentJRE = null;
        JPDA.o_envNextJRE = null;
        JPDA.s_envCurrentJRE = null;
        JPDA._debuggeeType = 0;
        JPDA._jpdaConfigIsFromCmdLine = false;
        JPDA._portsList = null;
        JPDA._portsPool = null;
        JPDA._jreUsesDashClassic = false;
        JPDA._nextJreRunsInJpdaMode = false;
    }
}
