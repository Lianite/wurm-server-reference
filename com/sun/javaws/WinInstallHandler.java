// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.javaws;

import com.sun.deploy.util.WinRegistry;
import java.net.URL;
import com.sun.javaws.jnl.RContentDesc;
import com.sun.deploy.util.DialogFactory;
import com.sun.deploy.resources.ResourceManager;
import com.sun.javaws.jnl.ShortcutDesc;
import java.io.IOException;
import com.sun.javaws.cache.Cache;
import com.sun.javaws.jnl.InformationDesc;
import java.util.StringTokenizer;
import java.io.File;
import com.sun.deploy.util.Trace;
import com.sun.deploy.util.TraceLevel;
import com.sun.javaws.jnl.LaunchDesc;
import com.sun.deploy.association.AssociationNotRegisteredException;
import com.sun.deploy.association.RegisterFailedException;
import com.sun.deploy.association.AssociationAlreadyRegisteredException;
import com.sun.deploy.association.AssociationService;
import com.sun.deploy.association.Association;
import com.sun.deploy.config.Config;

public class WinInstallHandler extends LocalInstallHandler
{
    private static final String INSTALLED_DESKTOP_SHORTCUT_KEY = "windows.installedDesktopShortcut";
    private static final String INSTALLED_START_MENU_KEY = "windows.installedStartMenuShortcut";
    private static final String UNINSTALLED_START_MENU_KEY = "windows.uninstalledStartMenuShortcut";
    private static final String RCONTENT_START_MENU_KEY = "windows.RContent.shortcuts";
    public static final int TYPE_DESKTOP = 1;
    public static final int TYPE_START_MENU = 2;
    private static final String REG_SHORTCUT_PATH = "Software\\Microsoft\\Windows\\CurrentVersion\\Explorer\\Shell Folders";
    private static final String REG_DESKTOP_PATH_KEY = "Desktop";
    private static final String REG_START_MENU_PATH_KEY = "Programs";
    private static final String SHORTCUT_EXTENSION = ".lnk";
    private static final int MAX_PATH = 200;
    private boolean _loadedPaths;
    private String _desktopPath;
    private String _startMenuPath;
    private static boolean useSystem;
    
    public WinInstallHandler() {
        this._loadedPaths = false;
    }
    
    public String getDefaultIconPath() {
        return Config.getInstance().getSystemJavawsPath();
    }
    
    public String getAssociationOpenCommand(final String s) {
        return "\"" + Config.getJavawsCommand() + "\"" + " \"-open\" \"%1\" " + "\"" + s + "\"";
    }
    
    public String getAssociationPrintCommand(final String s) {
        return "\"" + Config.getJavawsCommand() + "\"" + " \"-print\" \"%1\" " + "\"" + s + "\"";
    }
    
    public void registerAssociationInternal(final Association association) throws AssociationAlreadyRegisteredException, RegisterFailedException {
        final AssociationService associationService = new AssociationService();
        if (Globals.isSystemCache() || WinInstallHandler.useSystem) {
            associationService.registerSystemAssociation(association);
        }
        else {
            associationService.registerUserAssociation(association);
        }
    }
    
    public void unregisterAssociationInternal(final Association association) throws AssociationNotRegisteredException, RegisterFailedException {
        final AssociationService associationService = new AssociationService();
        if (Globals.isSystemCache() || WinInstallHandler.useSystem) {
            associationService.unregisterSystemAssociation(association);
        }
        else {
            associationService.unregisterUserAssociation(association);
        }
    }
    
    public boolean isLocalInstallSupported() {
        return true;
    }
    
    public boolean isAssociationSupported() {
        return true;
    }
    
    public void associationCompleted() {
    }
    
    public void uninstall(final LaunchDesc launchDesc, final LocalApplicationProperties localApplicationProperties, final boolean b) {
        if (localApplicationProperties == null) {
            Trace.println("No LAP for uninstall, bailing!", TraceLevel.TEMP);
            return;
        }
        String s = null;
        boolean b2 = false;
        final String value;
        if ((value = localApplicationProperties.get("windows.installedStartMenuShortcut")) != null) {
            if (!this.uninstallShortcut(value)) {
                b2 = true;
            }
            else {
                localApplicationProperties.put("windows.installedStartMenuShortcut", null);
            }
            s = value;
        }
        final String value2;
        if ((value2 = localApplicationProperties.get("windows.uninstalledStartMenuShortcut")) != null) {
            if (!this.uninstallShortcut(value2)) {
                b2 = true;
            }
            else {
                localApplicationProperties.put("windows.uninstalledStartMenuShortcut", null);
            }
            s = value2;
        }
        final String value3 = localApplicationProperties.get("windows.RContent.shortcuts");
        if (value3 != null) {
            final StringTokenizer stringTokenizer = new StringTokenizer(value3, File.pathSeparator);
            while (stringTokenizer.hasMoreElements()) {
                final String nextToken = stringTokenizer.nextToken();
                if (nextToken != null) {
                    if (!this.uninstallShortcut(nextToken)) {
                        b2 = true;
                    }
                    s = nextToken;
                }
            }
            localApplicationProperties.put("windows.RContent.shortcuts", null);
        }
        if (s != null) {
            this.checkEmpty(s);
        }
        final String value4;
        if (b && (value4 = localApplicationProperties.get("windows.installedDesktopShortcut")) != null) {
            if (!this.uninstallShortcut(value4)) {
                b2 = true;
            }
            else {
                localApplicationProperties.put("windows.installedDesktopShortcut", null);
            }
        }
        if (b2) {
            Trace.println("uninstall shortcut failed", TraceLevel.TEMP);
        }
        localApplicationProperties.setLocallyInstalled(false);
        LocalInstallHandler.save(localApplicationProperties);
    }
    
    private void checkEmpty(final String s) {
        try {
            final File parentFile = new File(s).getParentFile();
            if (parentFile != null && parentFile.isDirectory() && parentFile.list().length == 0) {
                parentFile.delete();
            }
        }
        catch (Exception ex) {}
    }
    
    private boolean hasValidTitle(final LaunchDesc launchDesc) {
        if (launchDesc == null) {
            return false;
        }
        final InformationDesc information = launchDesc.getInformation();
        if (information == null || information.getTitle().trim() == null) {
            Trace.println("Invalid: No title!", TraceLevel.TEMP);
            return false;
        }
        return true;
    }
    
    public void install(final LaunchDesc launchDesc, final LocalApplicationProperties localApplicationProperties) {
        if (!this.hasValidTitle(launchDesc)) {
            return;
        }
        if (this.isApplicationInstalled(launchDesc) && !LocalInstallHandler.shouldInstallOverExisting(launchDesc)) {
            return;
        }
        String absolutePath = null;
        try {
            absolutePath = Cache.getCachedLaunchedFile(launchDesc.getCanonicalHome()).getAbsolutePath();
        }
        catch (IOException ex) {
            Trace.ignoredException((Exception)ex);
        }
        if (absolutePath == null) {
            this.installFailed(launchDesc);
            return;
        }
        final ShortcutDesc shortcut = launchDesc.getInformation().getShortcut();
        final boolean b = shortcut == null || shortcut.getDesktop();
        if (b && !this.handleInstall(launchDesc, localApplicationProperties, absolutePath, 1)) {
            this.installFailed(launchDesc);
            return;
        }
        final boolean b2 = shortcut == null || shortcut.getMenu();
        if (b2 && !this.handleInstall(launchDesc, localApplicationProperties, absolutePath, 2)) {
            this.uninstall(launchDesc, localApplicationProperties, b);
            this.installFailed(launchDesc);
            return;
        }
        if (b2 || b) {
            localApplicationProperties.setLocallyInstalled(true);
            LocalInstallHandler.save(localApplicationProperties);
        }
    }
    
    private void installFailed(final LaunchDesc launchDesc) {
        LocalInstallHandler.invokeRunnable(new Runnable() {
            public void run() {
                DialogFactory.showErrorDialog(ResourceManager.getString("install.installFailed", WinInstallHandler.this.getInstallName(launchDesc)), ResourceManager.getString("install.installFailedTitle"));
            }
        });
    }
    
    private void uninstallFailed(final LaunchDesc launchDesc) {
        LocalInstallHandler.invokeRunnable(new Runnable() {
            public void run() {
                DialogFactory.showErrorDialog(ResourceManager.getString("install.uninstallFailed", WinInstallHandler.this.getInstallName(launchDesc)), ResourceManager.getString("install.uninstallFailedTitle"));
            }
        });
    }
    
    private boolean handleInstall(final LaunchDesc launchDesc, final LocalApplicationProperties localApplicationProperties, final String s, final int n) {
        final InformationDesc information = launchDesc.getInformation();
        final ShortcutDesc shortcut = information.getShortcut();
        final String s2 = null;
        String s3 = IcoEncoder.getIconPath(launchDesc);
        final String systemJavawsPath = Config.getInstance().getSystemJavawsPath();
        final String description = information.getDescription(1);
        boolean b = true;
        if (s3 == null) {
            s3 = this.getDefaultIconPath();
        }
        final String string = ((!information.supportsOfflineOperation() || shortcut == null || shortcut.getOnline()) ? "" : "-offline ") + "\"" + s + "\"";
        if (n == 1) {
            final String desktopPath = this.getDesktopPath(launchDesc);
            final String desktopName = this.getDesktopName(launchDesc);
            final int installWrapper = this.installWrapper(desktopPath, desktopName, description, systemJavawsPath, string, null, s3);
            if (installWrapper == 0) {
                localApplicationProperties.put("windows.installedDesktopShortcut", desktopPath);
                Trace.println("Installed desktop shortcut for: " + desktopName + ".", TraceLevel.TEMP);
            }
            else {
                b = false;
                Trace.println("Installed desktop shortcut for: " + desktopName + " failed (" + installWrapper + ")!!!", TraceLevel.TEMP);
            }
        }
        else {
            final File file = new File(this.getSubMenuPath(launchDesc));
            if (file.exists() || file.mkdirs()) {
                final String startMenuPath = this.getStartMenuPath(launchDesc);
                final String startMenuName = this.getStartMenuName(launchDesc);
                final int installWrapper2 = this.installWrapper(startMenuPath, startMenuName, description, systemJavawsPath, string, null, s3);
                if (installWrapper2 == 0) {
                    localApplicationProperties.put("windows.installedStartMenuShortcut", startMenuPath);
                    Trace.println("Installed menu shortcut for: " + startMenuName + ".", TraceLevel.TEMP);
                }
                else {
                    b = false;
                    Trace.println("Installed menu shortcut for: " + startMenuName + " failed (" + installWrapper2 + ")!!!", TraceLevel.TEMP);
                }
                final String subMenuDir = this.getSubMenuDir(launchDesc);
                if ((subMenuDir == null || !subMenuDir.equals("Startup")) && this.addUninstallShortcut()) {
                    final String string2 = "-uninstall \"" + s + "\"";
                    final String uninstallPath = this.getUninstallPath(launchDesc);
                    final String string3 = ResourceManager.getString("install.startMenuUninstallShortcutName", startMenuName);
                    final int installWrapper3 = this.installWrapper(uninstallPath, string3, description, systemJavawsPath, string2, null, s3);
                    if (installWrapper3 == 0) {
                        localApplicationProperties.put("windows.uninstalledStartMenuShortcut", uninstallPath);
                        Trace.println("Installed menu shortcut for: " + string3 + ".", TraceLevel.TEMP);
                    }
                    else {
                        b = false;
                        Trace.println("Installed menu shortcut for: " + string3 + " failed (" + installWrapper3 + ")!!!", TraceLevel.TEMP);
                    }
                }
                final RContentDesc[] relatedContent = information.getRelatedContent();
                final StringBuffer sb = new StringBuffer(200 * relatedContent.length);
                if (relatedContent != null) {
                    for (int i = 0; i < relatedContent.length; ++i) {
                        String s4 = relatedContent[i].getTitle().trim();
                        if (s4 == null || s4.length() == 0) {
                            s4 = this.getStartMenuName(launchDesc) + " #" + i;
                        }
                        final String name = this.getName(s4);
                        final URL href = relatedContent[i].getHref();
                        if (!href.toString().endsWith("jnlp")) {
                            final String description2 = relatedContent[i].getDescription();
                            final URL icon = relatedContent[i].getIcon();
                            String iconPath = null;
                            if (icon != null) {
                                iconPath = IcoEncoder.getIconPath(icon, null);
                            }
                            if (iconPath == null) {
                                iconPath = s3;
                            }
                            final String rcPath = this.getRCPath(launchDesc, name);
                            final File cachedFile = Cache.getCachedFile(href);
                            final String defaultHandler = new WinBrowserSupport().getDefaultHandler(href);
                            if (cachedFile != null) {
                                final int installWrapper4 = this.installWrapper(rcPath, name, description2, defaultHandler, "\"file:" + cachedFile.getAbsolutePath() + "\"", null, iconPath);
                                if (installWrapper4 == 0) {
                                    sb.append(rcPath);
                                    sb.append(File.pathSeparator);
                                    Trace.println("Installed menu shortcut for: " + name + ".", TraceLevel.TEMP);
                                }
                                else {
                                    b = false;
                                    Trace.println("Installed menu shortcut for: " + name + " failed (" + installWrapper4 + ")!!!", TraceLevel.TEMP);
                                }
                            }
                            else {
                                final int installWrapper5 = this.installWrapper(rcPath, name, description2, defaultHandler, href.toString(), null, iconPath);
                                if (installWrapper5 == 0) {
                                    sb.append(rcPath);
                                    sb.append(File.pathSeparator);
                                    Trace.println("Installed menu shortcut for: " + name + ".", TraceLevel.TEMP);
                                }
                                else {
                                    b = false;
                                    Trace.println("Installed menu shortcut for: " + name + " failed (" + installWrapper5 + ")!!!", TraceLevel.TEMP);
                                }
                            }
                        }
                    }
                }
                if (sb.length() > 0) {
                    localApplicationProperties.put("windows.RContent.shortcuts", sb.toString());
                }
                else {
                    localApplicationProperties.put("windows.RContent.shortcuts", null);
                }
            }
            else {
                b = false;
                Trace.println("Installed menu shortcut for: " + s2 + " failed (can't create directory \"" + file.getAbsolutePath() + "\")!!!", TraceLevel.TEMP);
            }
        }
        return b;
    }
    
    private boolean isApplicationInstalled(final LaunchDesc launchDesc) {
        final String desktopPath = this.getDesktopPath(launchDesc);
        Trace.println("getDesktopPath(" + desktopPath + ").exists() = " + ((desktopPath == null) ? "N/A" : ("" + new File(desktopPath).exists())), TraceLevel.TEMP);
        final boolean b = desktopPath == null || new File(desktopPath).exists();
        final String startMenuPath = this.getStartMenuPath(launchDesc);
        Trace.println("startMenuInstalled(" + startMenuPath + ").exists() = " + ((startMenuPath == null) ? "N/A" : ("" + new File(startMenuPath).exists())), TraceLevel.TEMP);
        final boolean b2 = startMenuPath == null || new File(startMenuPath).exists();
        return b && b2;
    }
    
    private String getInstallName(final LaunchDesc launchDesc) {
        return this.getName(launchDesc.getInformation().getTitle().trim());
    }
    
    private String getName(String substring) {
        if (substring.length() > 32) {
            substring = substring.substring(0, 32);
        }
        return substring;
    }
    
    private String getDesktopName(final LaunchDesc launchDesc) {
        return ResourceManager.getString("install.desktopShortcutName", this.getInstallName(launchDesc));
    }
    
    private String getStartMenuName(final LaunchDesc launchDesc) {
        return ResourceManager.getString("install.startMenuShortcutName", this.getInstallName(launchDesc));
    }
    
    private String getDesktopPath(final LaunchDesc launchDesc) {
        String s = this.getDesktopPath();
        if (s != null) {
            final String desktopName = this.getDesktopName(launchDesc);
            if (desktopName != null) {
                s += desktopName;
            }
            if (s.length() > 192) {
                s = s.substring(0, 192);
            }
            s += ".lnk";
        }
        return s;
    }
    
    private String getStartMenuPath(final LaunchDesc launchDesc) {
        String s = this.getSubMenuPath(launchDesc);
        if (s != null) {
            final String startMenuName = this.getStartMenuName(launchDesc);
            if (startMenuName != null) {
                s += startMenuName;
            }
            if (s.length() > 192) {
                s = s.substring(0, 192);
            }
            s += ".lnk";
        }
        return s;
    }
    
    private String getRCPath(final LaunchDesc launchDesc, final String s) {
        String s2 = this.getSubMenuPath(launchDesc);
        if (s2 != null) {
            String s3 = s2 + s;
            if (s3.length() > 192) {
                s3 = s3.substring(0, 192);
            }
            s2 = s3 + ".lnk";
        }
        return s2;
    }
    
    private String getUninstallPath(final LaunchDesc launchDesc) {
        String s = this.getSubMenuPath(launchDesc);
        if (s != null) {
            String s2 = s + ("uninstall  " + this.getStartMenuName(launchDesc));
            if (s2.length() > 192) {
                s2 = s2.substring(0, 192);
            }
            s = s2 + ".lnk";
        }
        return s;
    }
    
    private String getSubMenuPath(final LaunchDesc launchDesc) {
        String s = this.getStartMenuPath();
        if (s != null) {
            final String subMenuDir = this.getSubMenuDir(launchDesc);
            if (subMenuDir != null) {
                s = s + subMenuDir + File.separator;
            }
        }
        return s;
    }
    
    private String getSubMenuDir(final LaunchDesc launchDesc) {
        String startMenuName = this.getStartMenuName(launchDesc);
        final ShortcutDesc shortcut = launchDesc.getInformation().getShortcut();
        if (shortcut != null) {
            final String submenu = shortcut.getSubmenu();
            if (submenu != null) {
                startMenuName = submenu;
            }
        }
        if (startMenuName != null && startMenuName.equalsIgnoreCase("startup")) {
            startMenuName = "Startup";
        }
        return startMenuName;
    }
    
    private String getDesktopPath() {
        this.loadPathsIfNecessary();
        return this._desktopPath;
    }
    
    private String getStartMenuPath() {
        this.loadPathsIfNecessary();
        return this._startMenuPath;
    }
    
    private void loadPathsIfNecessary() {
        int n = -2147483647;
        String s = "";
        if (Globals.isSystemCache()) {
            n = -2147483646;
            s = "Common ";
        }
        if (!this._loadedPaths) {
            this._desktopPath = WinRegistry.getString(n, "Software\\Microsoft\\Windows\\CurrentVersion\\Explorer\\Shell Folders", s + "Desktop");
            if (this._desktopPath != null && this._desktopPath.length() > 0 && this._desktopPath.charAt(this._desktopPath.length() - 1) != '\\') {
                this._desktopPath += '\\';
            }
            this._startMenuPath = WinRegistry.getString(n, "Software\\Microsoft\\Windows\\CurrentVersion\\Explorer\\Shell Folders", s + "Programs");
            if (this._startMenuPath != null && this._startMenuPath.length() > 0 && this._startMenuPath.charAt(this._startMenuPath.length() - 1) != '\\') {
                this._startMenuPath += '\\';
            }
            this._loadedPaths = true;
            Trace.println("Start path: " + this._startMenuPath + " desktop " + this._desktopPath, TraceLevel.TEMP);
        }
    }
    
    private boolean uninstallShortcut(final String s) {
        final File file = new File(s);
        return !file.exists() || file.delete();
    }
    
    private int installWrapper(final String s, final String s2, final String s3, final String s4, final String s5, final String s6, final String s7) {
        Trace.println("installshortcut with args:", TraceLevel.TEMP);
        Trace.println("    path: " + s, TraceLevel.TEMP);
        Trace.println("    name: " + s2, TraceLevel.TEMP);
        Trace.println("    desc: " + s3, TraceLevel.TEMP);
        Trace.println("    appP: " + s4, TraceLevel.TEMP);
        Trace.println("    args: " + s5, TraceLevel.TEMP);
        Trace.println("    dir : " + s6, TraceLevel.TEMP);
        Trace.println("    icon: " + s7, TraceLevel.TEMP);
        Trace.flush();
        return Config.getInstance().installShortcut(s, s2, s3, s4, s5, s6, s7);
    }
    
    static {
        NativeLibrary.getInstance().load();
        final String property = System.getProperty("os.name");
        if (property.indexOf("2000") != -1 || property.indexOf("XP") != -1) {
            WinInstallHandler.useSystem = false;
        }
        else {
            WinInstallHandler.useSystem = true;
        }
    }
}
