// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.javaws;

import java.io.InputStream;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.io.FileOutputStream;
import java.text.MessageFormat;
import java.io.IOException;
import com.sun.deploy.util.Trace;
import com.sun.deploy.util.TraceLevel;
import java.io.File;

public abstract class OperaSupport
{
    protected static final String OPERA_PREFERENCES = "Opera.ini";
    protected static final String OPERA_6_PREFERENCES = "Opera6.ini";
    protected boolean useDefault;
    private static final String INSTALL_SECTION = "INSTALL";
    private static final String VERSION_KEY = "OVER";
    private static final float OPERA_2_PREFERENCE_VERSION = 5.0f;
    private static final float LAST_TESTED_OPERA_PREFERENCE_VERSION = 7.11f;
    private static final String FILE_TYPES_SECTION_INFO = "File Types Section Info";
    private static final String FILE_TYPES_VERSION_KEY = "Version";
    private static final String FILE_TYPES = "File Types";
    private static final String FILE_TYPES_KEY = "application/x-java-jnlp-file";
    private static final String FILE_TYPES_VALUE = "{0},{1},,,jnlp,|";
    private static final String EXPLICIT_PATH = "3";
    private static final String IMPLICIT_PATH = "4";
    private static final String FILE_TYPES_EXTENSION = "File Types Extension";
    private static final String FILE_TYPES_EXTENSION_KEY = "application/x-java-jnlp-file";
    private static final String FILE_TYPES_EXTENSION_VALUE = ",0";
    
    public abstract boolean isInstalled();
    
    public abstract void enableJnlp(final File p0, final boolean p1);
    
    protected void enableJnlp(OperaPreferences preferences, final File file, final File file2, final boolean b) throws IOException {
        if (preferences == null) {
            preferences = this.getPreferences(file);
        }
        if (preferences != null) {
            float float1 = 5.0f;
            final String value = preferences.get("INSTALL", "OVER");
            if (value != null) {
                try {
                    float1 = Float.parseFloat(value.trim());
                }
                catch (NumberFormatException ex) {
                    Trace.println("Unable to determine Opera version from the preference file; assuming 5.0 or higher.", TraceLevel.BASIC);
                }
            }
            if (float1 < 5.0f) {
                preferences.put("File Types Section Info", "Version", "1");
            }
            else if (!preferences.containsKey("File Types Section Info", "Version")) {
                if (float1 > 7.11f) {
                    Trace.println("Setting '[File Types Section Info]Version=2' in the Opera preference file.", TraceLevel.BASIC);
                }
                preferences.put("File Types Section Info", "Version", "2");
            }
            if (b || !preferences.containsKey("File Types", "application/x-java-jnlp-file")) {
                final Object[] array = { null, null };
                if (float1 < 5.0f || !this.useDefault) {
                    array[0] = "3";
                    try {
                        array[1] = file2.getCanonicalPath();
                    }
                    catch (IOException ex2) {
                        array[1] = file2.getAbsolutePath();
                    }
                }
                else {
                    array[0] = "4";
                    array[1] = "";
                }
                preferences.put("File Types", "application/x-java-jnlp-file", MessageFormat.format("{0},{1},,,jnlp,|", array));
            }
            if (float1 >= 5.0f && !preferences.containsKey("File Types Extension", "application/x-java-jnlp-file")) {
                preferences.put("File Types Extension", "application/x-java-jnlp-file", ",0");
            }
            preferences.store(new FileOutputStream(file));
        }
    }
    
    protected OperaPreferences getPreferences(final File file) throws IOException {
        OperaPreferences operaPreferences = null;
        if (file.exists()) {
            if (file.canRead()) {
                if (file.canWrite()) {
                    operaPreferences = new OperaPreferences();
                    operaPreferences.load(new FileInputStream(file));
                }
                else {
                    Trace.println("No write access to the Opera preference file (" + file.getAbsolutePath() + ").", TraceLevel.BASIC);
                }
            }
            else {
                Trace.println("No read access to the Opera preference file (" + file.getAbsolutePath() + ").", TraceLevel.BASIC);
            }
        }
        else {
            Trace.println("The Opera preference file (" + file.getAbsolutePath() + ") does not exist.", TraceLevel.BASIC);
        }
        return operaPreferences;
    }
    
    protected OperaSupport(final boolean useDefault) {
        this.useDefault = useDefault;
    }
}
