// 
// Decompiled by Procyon v0.5.30
// 

package impl.org.controlsfx.version;

import com.sun.javafx.runtime.VersionInfo;
import java.io.IOException;
import java.io.Reader;
import java.io.FileReader;
import java.io.File;
import java.util.Properties;

public class VersionChecker
{
    private static final String javaFXVersion;
    private static final String controlsFXSpecTitle;
    private static final String controlsFXSpecVersion;
    private static final String controlsFXImpVersion;
    private static final Package controlsFX;
    private static Properties props;
    
    public static void doVersionCheck() {
        if (VersionChecker.controlsFXSpecVersion == null) {
            return;
        }
        final Comparable[] splitSpecVersion = toComparable(VersionChecker.controlsFXSpecVersion.split("\\."));
        final Comparable[] splitJavaVersion = toComparable(VersionChecker.javaFXVersion.replace('-', '.').split("\\."));
        boolean notSupportedVersion = false;
        if (splitSpecVersion[0].compareTo(splitJavaVersion[0]) > 0) {
            notSupportedVersion = true;
        }
        else if (splitSpecVersion[0].compareTo(splitJavaVersion[0]) == 0 && splitSpecVersion[1].compareTo(splitJavaVersion[2]) > 0) {
            notSupportedVersion = true;
        }
        if (notSupportedVersion) {
            throw new RuntimeException("ControlsFX Error: ControlsFX " + VersionChecker.controlsFXImpVersion + " requires at least " + VersionChecker.controlsFXSpecTitle);
        }
    }
    
    private static Comparable<Comparable>[] toComparable(final String[] tokens) {
        final Comparable[] ret = new Comparable[tokens.length];
        for (int i = 0; i < tokens.length; ++i) {
            final String token = tokens[i];
            try {
                ret[i] = new Integer(token);
            }
            catch (NumberFormatException e) {
                ret[i] = token;
            }
        }
        return (Comparable<Comparable>[])ret;
    }
    
    private static String getControlsFXSpecificationTitle() {
        try {
            return VersionChecker.controlsFX.getSpecificationTitle();
        }
        catch (NullPointerException ex) {
            return getPropertyValue("controlsfx_specification_title");
        }
    }
    
    private static String getControlsFXSpecificationVersion() {
        try {
            return VersionChecker.controlsFX.getSpecificationVersion();
        }
        catch (NullPointerException ex) {
            return getPropertyValue("controlsfx_specification_title");
        }
    }
    
    private static String getControlsFXImplementationVersion() {
        try {
            return VersionChecker.controlsFX.getImplementationVersion();
        }
        catch (NullPointerException ex) {
            return getPropertyValue("controlsfx_specification_title") + getPropertyValue("artifact_suffix");
        }
    }
    
    private static synchronized String getPropertyValue(final String key) {
        if (VersionChecker.props == null) {
            try {
                final File file = new File("../controlsfx-build.properties");
                if (file.exists()) {
                    VersionChecker.props.load(new FileReader(file));
                }
            }
            catch (IOException ex) {}
        }
        return VersionChecker.props.getProperty(key);
    }
    
    static {
        controlsFX = VersionChecker.class.getPackage();
        javaFXVersion = VersionInfo.getVersion();
        controlsFXSpecTitle = getControlsFXSpecificationTitle();
        controlsFXSpecVersion = getControlsFXSpecificationVersion();
        controlsFXImpVersion = getControlsFXImplementationVersion();
    }
}
