// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.javaws;

import com.sun.deploy.util.WinRegistry;
import java.io.IOException;
import com.sun.deploy.util.TraceLevel;
import com.sun.deploy.util.Trace;
import com.sun.deploy.config.Config;
import java.io.File;

public class WinOperaSupport extends OperaSupport
{
    private static final String OPERA_SUBKEY = "Software\\Microsoft\\Windows\\CurrentVersion\\App Paths\\Opera.exe";
    private static final String OPERA_PATH = "Path";
    private static final String USER_HOME = "user.home";
    private static final String USER_DATA_INFIX;
    private static final String USER_DATA_POSTFIX = "Profile";
    private static final String SYSTEM_PREFERENCES = "OperaDef6.ini";
    private static final String MULTI_USER_SECTION = "System";
    private static final String MULTI_USER_KEY = "Multi User";
    
    public boolean isInstalled() {
        return this.getInstallPath().length() != 0;
    }
    
    public void enableJnlp(final File file, final boolean b) {
        final String installPath = this.getInstallPath();
        if (installPath.length() > 0) {
            try {
                final File file2 = new File(installPath);
                File enableSystemJnlp = this.enableSystemJnlp(file2, file);
                if (enableSystemJnlp == null) {
                    enableSystemJnlp = new File(file2, "Opera6.ini");
                    if (!enableSystemJnlp.exists()) {
                        enableSystemJnlp = new File(file2, "Opera.ini");
                        if (!enableSystemJnlp.exists()) {
                            enableSystemJnlp = new File(Config.getOSHome(), "Opera.ini");
                        }
                    }
                }
                this.enableJnlp(null, enableSystemJnlp, file, b);
            }
            catch (Exception ex) {
                Trace.ignoredException(ex);
            }
        }
    }
    
    public WinOperaSupport(final boolean b) {
        super(b);
    }
    
    private File enableSystemJnlp(final File file, final File file2) throws IOException {
        File file3 = null;
        final File file4 = new File(file, "OperaDef6.ini");
        final OperaPreferences preferences = this.getPreferences(file4);
        if (preferences != null) {
            int n = 1;
            this.enableJnlp(preferences, file4, file2, true);
            if (preferences.containsKey("System", "Multi User")) {
                final String trim = preferences.get("System", "Multi User").trim();
                final String substring = trim.substring(0, trim.indexOf(32));
                try {
                    if (Integer.decode(substring) == 0) {
                        n = 0;
                        Trace.println("Multi-user support is turned off in the Opera system preference file (" + file4.getAbsolutePath() + ").", TraceLevel.BASIC);
                    }
                }
                catch (NumberFormatException ex) {
                    n = 0;
                    Trace.println("The Opera system preference file (" + file4.getAbsolutePath() + ") has '" + "Multi User" + "=" + substring + "' in the " + "System" + " section, so multi-user " + "support is not enabled.", TraceLevel.BASIC);
                }
            }
            if (n == 1) {
                final StringBuffer sb = new StringBuffer(512);
                sb.append(System.getProperty("user.home")).append(File.separator).append(WinOperaSupport.USER_DATA_INFIX).append(File.separator).append(file.getName()).append(File.separator).append("Profile").append(File.separator).append("Opera6.ini");
                file3 = new File(sb.toString());
            }
        }
        return file3;
    }
    
    private String getInstallPath() {
        final String string = WinRegistry.getString(-2147483646, "Software\\Microsoft\\Windows\\CurrentVersion\\App Paths\\Opera.exe", "Path");
        return (string != null) ? string : "";
    }
    
    static {
        USER_DATA_INFIX = "Application Data" + File.separator + "Opera";
    }
}
