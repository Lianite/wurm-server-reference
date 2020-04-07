// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.javaws;

import com.sun.javaws.util.VersionID;
import com.sun.deploy.util.Trace;
import com.sun.deploy.util.TraceLevel;
import java.io.File;
import java.util.StringTokenizer;
import com.sun.deploy.config.Config;
import com.sun.javaws.util.VersionString;
import java.net.URL;
import com.sun.javaws.jnl.ResourcesDesc;
import com.sun.javaws.jnl.ExtensionDesc;
import com.sun.javaws.jnl.PackageDesc;
import com.sun.javaws.jnl.PropertyDesc;
import com.sun.javaws.jnl.JARDesc;
import com.sun.javaws.jnl.ResourceVisitor;
import com.sun.javaws.jnl.JREDesc;
import com.sun.deploy.config.JREInfo;
import com.sun.javaws.jnl.LaunchDesc;

public class LaunchSelection
{
    static JREInfo selectJRE(final LaunchDesc launchDesc) {
        final JREDesc[] array = { null };
        final JREInfo[] array2 = { null };
        final ResourcesDesc resources = launchDesc.getResources();
        resources.visit(new ResourceVisitor() {
            public void visitJARDesc(final JARDesc jarDesc) {
            }
            
            public void visitPropertyDesc(final PropertyDesc propertyDesc) {
            }
            
            public void visitPackageDesc(final PackageDesc packageDesc) {
            }
            
            public void visitExtensionDesc(final ExtensionDesc extensionDesc) {
            }
            
            public void visitJREDesc(final JREDesc jreDesc) {
                if (array2[0] == null) {
                    handleJREDesc(jreDesc, array2, array);
                }
            }
        });
        array[0].markAsSelected();
        resources.addNested(array[0].getNestedResources());
        return array2[0];
    }
    
    public static JREInfo selectJRE(final URL url, final String s) {
        final JREInfo[] value = JREInfo.get();
        if (value == null) {
            return null;
        }
        final VersionString versionString = new VersionString(s);
        for (int i = 0; i < value.length; ++i) {
            if (value[i].isOsInfoMatch(Config.getOSName(), Config.getOSArch()) && value[i].isEnabled()) {
                if (url == null) {
                    if (!isPlatformMatch(value[i], versionString)) {
                        continue;
                    }
                }
                else if (!isProductMatch(value[i], url, versionString)) {
                    continue;
                }
                return value[i];
            }
        }
        return null;
    }
    
    private static void handleJREDesc(final JREDesc jreDesc, final JREInfo[] array, final JREDesc[] array2) {
        jreDesc.getHref();
        final StringTokenizer stringTokenizer = new StringTokenizer(jreDesc.getVersion(), " ", false);
        final int countTokens = stringTokenizer.countTokens();
        if (countTokens > 0) {
            final String[] array3 = new String[countTokens];
            for (int i = 0; i < countTokens; ++i) {
                array3[i] = stringTokenizer.nextToken();
            }
            matchJRE(jreDesc, array3, array, array2);
            if (array[0] != null) {
                return;
            }
        }
    }
    
    private static void matchJRE(final JREDesc jreDesc, final String[] array, final JREInfo[] array2, final JREDesc[] array3) {
        final URL href = jreDesc.getHref();
        final JREInfo[] value = JREInfo.get();
        if (value == null) {
            return;
        }
        for (int i = 0; i < array.length; ++i) {
            final VersionString versionString = new VersionString(array[i]);
            for (int j = 0; j < value.length; ++j) {
                if (value[j].isOsInfoMatch(Config.getOSName(), Config.getOSArch()) && value[j].isEnabled()) {
                    final boolean b = (href == null) ? isPlatformMatch(value[j], versionString) : isProductMatch(value[j], href, versionString);
                    final boolean equals = JnlpxArgs.getJVMCommand().equals(new File(value[j].getPath()));
                    final boolean currentRunningJREHeap = JnlpxArgs.isCurrentRunningJREHeap(jreDesc.getMinHeap(), jreDesc.getMaxHeap());
                    if (b && equals && currentRunningJREHeap) {
                        Trace.println("LaunchSelection: findJRE: Match on current JRE", TraceLevel.BASIC);
                        array2[0] = value[j];
                        array3[0] = jreDesc;
                        return;
                    }
                    if (b) {
                        Trace.print("LaunchSelection: findJRE: No match on current JRE because ", TraceLevel.BASIC);
                        if (!b) {
                            Trace.print("versions dont match, ", TraceLevel.BASIC);
                        }
                        if (!equals) {
                            Trace.print("paths dont match, ", TraceLevel.BASIC);
                        }
                        if (!currentRunningJREHeap) {
                            Trace.print("heap sizes dont match", TraceLevel.BASIC);
                        }
                        Trace.println("", TraceLevel.BASIC);
                        final VersionID versionID = new VersionID(value[j].getProduct());
                        VersionID versionID2 = null;
                        if (array2[0] != null) {
                            versionID2 = new VersionID(array2[0].getProduct());
                        }
                        if (versionID2 == null || versionID.isGreaterThan(versionID2)) {
                            array2[0] = value[j];
                            array3[0] = jreDesc;
                        }
                    }
                }
            }
        }
        if (array3[0] == null) {
            array3[0] = jreDesc;
        }
    }
    
    private static boolean isPlatformMatch(final JREInfo jreInfo, final VersionString versionString) {
        final String product = jreInfo.getProduct();
        final boolean b = product == null || product.indexOf(45) == -1 || product.startsWith("1.2") || isInstallJRE(jreInfo);
        return new File(jreInfo.getPath()).exists() && versionString.contains(jreInfo.getPlatform()) && b;
    }
    
    private static boolean isProductMatch(final JREInfo jreInfo, final URL url, final VersionString versionString) {
        return new File(jreInfo.getPath()).exists() && jreInfo.getLocation().equals(url.toString()) && versionString.contains(jreInfo.getProduct());
    }
    
    private static boolean isInstallJRE(final JREInfo jreInfo) {
        return new File(Config.getJavaHome()).equals(new File(jreInfo.getPath()).getParentFile().getParentFile());
    }
}
