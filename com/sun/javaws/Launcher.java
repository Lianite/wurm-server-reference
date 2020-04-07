// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.javaws;

import com.sun.javaws.exceptions.FailedDownloadingResourceException;
import javax.net.ssl.SSLHandshakeException;
import java.io.InputStream;
import java.awt.Container;
import com.sun.javaws.jnl.AppletDesc;
import java.awt.LayoutManager;
import java.awt.BorderLayout;
import java.awt.event.WindowEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowListener;
import com.sun.jnlp.AppletContainer;
import java.awt.Dimension;
import com.sun.jnlp.AppletContainerCallback;
import java.applet.Applet;
import java.lang.reflect.Method;
import com.sun.deploy.util.PerfLogger;
import java.lang.reflect.Modifier;
import com.sun.javaws.jnl.AssociationDesc;
import com.sun.jnlp.ExtensionInstallerServiceImpl;
import com.sun.jnlp.BasicServiceImpl;
import com.sun.deploy.util.URLUtil;
import com.sun.javaws.util.JavawsConsoleController;
import java.lang.reflect.InvocationTargetException;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.UIManager;
import com.sun.javaws.security.JavaWebStartSecurity;
import com.sun.jnlp.JNLPClassLoader;
import com.sun.javaws.security.AppPolicy;
import java.awt.Window;
import com.sun.javaws.jnl.LaunchDescFactory;
import javax.swing.SwingUtilities;
import com.sun.deploy.util.DialogFactory;
import java.util.Date;
import java.util.Properties;
import com.sun.javaws.jnl.JREDesc;
import com.sun.deploy.config.JREInfo;
import java.net.URL;
import javax.swing.JFrame;
import com.sun.javaws.exceptions.JreExecException;
import com.sun.deploy.si.SingleInstanceManager;
import java.io.IOException;
import java.util.ArrayList;
import com.sun.javaws.exceptions.OfflineLaunchException;
import java.awt.Component;
import com.sun.javaws.ui.AutoDownloadPrompt;
import com.sun.javaws.exceptions.NoLocalJREException;
import com.sun.javaws.exceptions.MissingFieldException;
import com.sun.javaws.cache.Cache;
import com.sun.javaws.exceptions.LaunchDescException;
import com.sun.deploy.resources.ResourceManager;
import com.sun.javaws.util.VersionID;
import com.sun.javaws.util.VersionString;
import com.sun.javaws.exceptions.ExitException;
import java.net.Authenticator;
import java.awt.Frame;
import com.sun.deploy.config.Config;
import com.sun.javaws.cache.DiskCacheEntry;
import java.io.File;
import com.sun.javaws.exceptions.JNLPException;
import com.sun.javaws.cache.DownloadProtocol;
import com.sun.deploy.util.Trace;
import com.sun.deploy.util.TraceLevel;
import com.sun.javaws.jnl.LaunchDesc;
import com.sun.javaws.ui.DownloadWindow;

public class Launcher implements Runnable
{
    private DownloadWindow _downloadWindow;
    private LaunchDesc _launchDesc;
    private String[] _args;
    private boolean _exit;
    private JAuthenticator _ja;
    private boolean _shownDownloadWindow;
    
    public Launcher(final LaunchDesc launchDesc) {
        this._downloadWindow = null;
        this._exit = true;
        this._shownDownloadWindow = false;
        this._launchDesc = launchDesc;
        this._downloadWindow = new DownloadWindow();
        Trace.println("new Launcher: " + launchDesc.toString(), TraceLevel.BASIC);
    }
    
    public void launch(final String[] args, final boolean exit) {
        this._args = args;
        this._exit = exit;
        new Thread(Main.getLaunchThreadGroup(), this, "javawsApplicationMain").start();
    }
    
    private void removeTempJnlpFile(final LaunchDesc launchDesc) {
        DiskCacheEntry cachedLaunchedFile = null;
        try {
            if (launchDesc.isApplicationDescriptor()) {
                cachedLaunchedFile = DownloadProtocol.getCachedLaunchedFile(launchDesc.getCanonicalHome());
            }
        }
        catch (JNLPException ex) {
            Trace.ignoredException((Exception)ex);
        }
        if (cachedLaunchedFile == null) {
            return;
        }
        final File file = cachedLaunchedFile.getFile();
        if (this._args != null && file != null && JnlpxArgs.shouldRemoveArgumentFile()) {
            new File(this._args[0]).delete();
            JnlpxArgs.setShouldRemoveArgumentFile(String.valueOf(false));
            this._args[0] = file.getPath();
        }
    }
    
    public void run() {
        LaunchDesc launchDesc = this._launchDesc;
        final boolean updateLaunchDescInCache = LaunchDownload.updateLaunchDescInCache(launchDesc);
        this.removeTempJnlpFile(launchDesc);
        if (launchDesc.getResources() != null) {
            Globals.getDebugOptionsFromProperties(launchDesc.getResources().getResourceProperties());
        }
        if (Config.getBooleanProperty("deployment.security.authenticator")) {
            Authenticator.setDefault((Authenticator)(this._ja = JAuthenticator.getInstance(this._downloadWindow.getFrame())));
        }
        int n = 0;
        final boolean silentMode = Globals.isSilentMode();
        final boolean b = Globals.isImportMode() || launchDesc.getLaunchType() == 3;
        try {
            boolean b2;
            do {
                b2 = (n == 3);
                this._downloadWindow.setLaunchDesc(launchDesc, true);
                launchDesc = this.handleLaunchFile(launchDesc, this._args, !b2, b, silentMode, updateLaunchDescInCache);
                ++n;
            } while (launchDesc != null && !b2);
        }
        catch (ExitException ex) {
            final int n2 = (ex.getReason() == 0) ? 0 : -1;
            if (ex.getReason() == 2) {
                LaunchErrorDialog.show((this._downloadWindow == null) ? null : this._downloadWindow.getFrame(), ex.getException(), this._exit);
            }
            if (this._exit) {
                Main.systemExit(n2);
            }
        }
    }
    
    private LaunchDesc handleLaunchFile(final LaunchDesc defaultLaunchDesc, final String[] array, final boolean b, final boolean b2, final boolean b3, final boolean b4) throws ExitException {
        final VersionString versionString = new VersionString(defaultLaunchDesc.getSpecVersion());
        final VersionID versionID = new VersionID("1.5");
        if (!versionString.contains(new VersionID("1.5")) && !versionString.contains(new VersionID("1.0"))) {
            JNLPException.setDefaultLaunchDesc(defaultLaunchDesc);
            this.handleJnlpFileException(defaultLaunchDesc, new LaunchDescException(defaultLaunchDesc, ResourceManager.getString("launch.error.badjnlversion", defaultLaunchDesc.getSpecVersion()), null));
        }
        if (defaultLaunchDesc.getResources() == null) {
            this.handleJnlpFileException(defaultLaunchDesc, new LaunchDescException(defaultLaunchDesc, ResourceManager.getString("launch.error.noappresources", defaultLaunchDesc.getSpecVersion()), null));
        }
        if (!b2 && !defaultLaunchDesc.isLibrary() && !defaultLaunchDesc.isJRESpecified()) {
            this.handleJnlpFileException(defaultLaunchDesc, new LaunchDescException(defaultLaunchDesc, ResourceManager.getString("launch.error.missingjreversion"), null));
        }
        return this.handleApplicationDesc(defaultLaunchDesc, array, defaultLaunchDesc.getLaunchType() == 4, b, b2, b3, b4);
    }
    
    private LaunchDesc handleApplicationDesc(final LaunchDesc defaultLaunchDesc, String[] insertApplicationArgs, final boolean b, final boolean b2, final boolean b3, final boolean b4, final boolean b5) throws ExitException {
        JNLPException.setDefaultLaunchDesc(defaultLaunchDesc);
        final JFrame frame = this._downloadWindow.getFrame();
        URL url = defaultLaunchDesc.getCanonicalHome();
        if (url == null) {
            throw new ExitException(new LaunchDescException(defaultLaunchDesc, ResourceManager.getString("launch.error.nomainjar"), null), 2);
        }
        LocalApplicationProperties localApplicationProperties;
        if (b) {
            localApplicationProperties = Cache.getLocalApplicationProperties(insertApplicationArgs[0], defaultLaunchDesc);
            if (localApplicationProperties == null || !Globals.isInstallMode()) {
                this.handleJnlpFileException(defaultLaunchDesc, new MissingFieldException(defaultLaunchDesc.getSource(), "<application-desc>|<applet-desc>"));
            }
            url = localApplicationProperties.getLocation();
        }
        else {
            localApplicationProperties = Cache.getLocalApplicationProperties(url, defaultLaunchDesc);
        }
        Trace.println("LaunchDesc location: " + url + ", version: " + localApplicationProperties.getVersionId(), TraceLevel.BASIC);
        final boolean inCache = LaunchDownload.isInCache(defaultLaunchDesc);
        final boolean b6 = inCache && Globals.isOffline();
        JREInfo jreInfo = null;
        if (!b3) {
            jreInfo = LaunchSelection.selectJRE(defaultLaunchDesc);
            if (jreInfo == null) {
                final String property = Config.getProperty("deployment.javaws.autodownload");
                if (property != null && property.equalsIgnoreCase("NEVER")) {
                    throw new ExitException(new NoLocalJREException(defaultLaunchDesc, defaultLaunchDesc.getResources().getSelectedJRE().getVersion(), false), 2);
                }
                if (property != null && property.equalsIgnoreCase("PROMPT") && !AutoDownloadPrompt.prompt(frame, defaultLaunchDesc)) {
                    throw new ExitException(new NoLocalJREException(defaultLaunchDesc, defaultLaunchDesc.getResources().getSelectedJRE().getVersion(), true), 2);
                }
            }
        }
        final int intProperty = Config.getIntProperty("deployment.javaws.update.timeout");
        final boolean b7 = !inCache || (!b3 && jreInfo == null) || (!b6 && (localApplicationProperties.forceUpdateCheck() || b || new RapidUpdateCheck().doUpdateCheck(defaultLaunchDesc, localApplicationProperties, intProperty)));
        Trace.println("Offline mode: " + b6 + "\nIsInCache: " + inCache + "\nforceUpdate: " + b7 + "\nInstalled JRE: " + jreInfo + "\nIsInstaller: " + b, TraceLevel.BASIC);
        if (b7 && b6) {
            throw new ExitException(new OfflineLaunchException(), 2);
        }
        final ArrayList list = new ArrayList();
        if (b7) {
            final LaunchDesc downloadResources = this.downloadResources(defaultLaunchDesc, !b3 && jreInfo == null, !b && b2, list, b4);
            if (downloadResources != null) {
                this.removeTempJnlpFile(defaultLaunchDesc);
                return downloadResources;
            }
            if (localApplicationProperties.forceUpdateCheck()) {
                localApplicationProperties.setForceUpdateCheck(false);
                try {
                    localApplicationProperties.store();
                }
                catch (IOException ex) {
                    Trace.ignoredException((Exception)ex);
                }
            }
            if (!b4) {
                checkCacheMax();
            }
        }
        if (SingleInstanceManager.isServerRunning(defaultLaunchDesc.getCanonicalHome().toString())) {
            final String[] applicationArgs = Globals.getApplicationArgs();
            if (applicationArgs != null) {
                defaultLaunchDesc.getApplicationDescriptor().setArguments(applicationArgs);
            }
            if (SingleInstanceManager.connectToServer(defaultLaunchDesc.toString())) {
                throw new ExitException(null, 0);
            }
        }
        if (!b4) {
            SplashScreen.generateCustomSplash(frame, defaultLaunchDesc, b7 || b5);
        }
        if (!b3 && !list.isEmpty()) {
            if (b) {}
            this.executeInstallers(list);
        }
        if (!b4 && this._downloadWindow.getFrame() != null) {
            String s = ResourceManager.getString("launch.launchApplication");
            if (defaultLaunchDesc.getLaunchType() == 4) {
                s = ResourceManager.getString("launch.launchInstaller");
            }
            this._downloadWindow.showLaunchingApplication(s);
        }
        Label_0962: {
            if (!b3) {
                if (jreInfo == null) {
                    Config.refreshProps();
                    jreInfo = LaunchSelection.selectJRE(defaultLaunchDesc);
                    if (jreInfo == null) {
                        throw new ExitException(new LaunchDescException(defaultLaunchDesc, ResourceManager.getString("launch.error.missingjreversion"), null), 2);
                    }
                }
                final JREDesc selectedJRE = defaultLaunchDesc.getResources().getSelectedJRE();
                final long minHeap = selectedJRE.getMinHeap();
                final long maxHeap = selectedJRE.getMaxHeap();
                final boolean currentRunningJREHeap = JnlpxArgs.isCurrentRunningJREHeap(minHeap, maxHeap);
                final Properties resourceProperties = defaultLaunchDesc.getResources().getResourceProperties();
                final String vmArgs = selectedJRE.getVmArgs();
                final boolean auxArgsMatch = JnlpxArgs.isAuxArgsMatch(resourceProperties, vmArgs);
                if (JPDA.getDebuggeeType() != 1 && JPDA.getDebuggeeType() != 3 && JnlpxArgs.getJVMCommand().equals(new File(jreInfo.getPath())) && currentRunningJREHeap) {
                    if (auxArgsMatch) {
                        break Label_0962;
                    }
                }
                try {
                    insertApplicationArgs = this.insertApplicationArgs(insertApplicationArgs);
                    execProgram(jreInfo, insertApplicationArgs, minHeap, maxHeap, resourceProperties, vmArgs);
                }
                catch (IOException ex2) {
                    throw new ExitException(new JreExecException(jreInfo.getPath(), ex2), 2);
                }
                if (JnlpxArgs.shouldRemoveArgumentFile()) {
                    JnlpxArgs.setShouldRemoveArgumentFile(String.valueOf(false));
                }
                throw new ExitException(null, 0);
            }
        }
        JnlpxArgs.removeArgumentFile(insertApplicationArgs);
        if (b3) {
            this._downloadWindow.disposeWindow();
            this.notifyLocalInstallHandler(defaultLaunchDesc, localApplicationProperties, b7 || b5, b3, b4, null);
            Trace.println("Exiting after import", TraceLevel.BASIC);
            throw new ExitException(null, 0);
        }
        Trace.println("continuing launch in this VM", TraceLevel.BASIC);
        this.continueLaunch(localApplicationProperties, b6, url, defaultLaunchDesc, b7 || b5, b3, b4);
        return null;
    }
    
    public static void checkCacheMax() {
        final long cacheSizeMax = Config.getCacheSizeMax();
        if (cacheSizeMax > 0L) {
            try {
                final long cacheSize = Cache.getCacheSize();
                if (cacheSize > cacheSizeMax * 90L / 100L) {
                    final File file = new File(Config.getTempDirectory() + File.separator + "cachemax.timestamp");
                    file.createNewFile();
                    final long lastModified = file.lastModified();
                    final long time = new Date().getTime();
                    if (time - lastModified > 60000L) {
                        file.setLastModified(time);
                        SwingUtilities.invokeAndWait(new Runnable() {
                            private final /* synthetic */ String val$message = ResourceManager.getString("jnlp.cache.warning.message", sizeString(cacheSize), sizeString(cacheSizeMax));
                            private final /* synthetic */ String val$title = ResourceManager.getString("jnlp.cache.warning.title");
                            
                            public void run() {
                                DialogFactory.showMessageDialog(3, (Object)this.val$message, this.val$title, true);
                            }
                        });
                    }
                }
            }
            catch (Exception ex) {
                Trace.ignoredException(ex);
            }
        }
    }
    
    private String[] insertApplicationArgs(final String[] array) {
        final String[] applicationArgs = Globals.getApplicationArgs();
        if (applicationArgs == null) {
            return array;
        }
        final String[] array2 = new String[applicationArgs.length + array.length];
        int i;
        for (i = 0; i < applicationArgs.length; ++i) {
            array2[i] = applicationArgs[i];
        }
        for (int j = 0; j < array.length; ++j) {
            array2[i++] = array[j];
        }
        return array2;
    }
    
    private static String sizeString(final long n) {
        if (n > 1048576L) {
            return "" + n / 1048576L + "Mb";
        }
        return "" + n + "bytes";
    }
    
    private void executeInstallers(final ArrayList list) throws ExitException {
        if (this._downloadWindow.getFrame() != null) {
            this._downloadWindow.showLaunchingApplication(ResourceManager.getString("launch.launchInstaller"));
            new Thread(new Runnable() {
                public void run() {
                    try {
                        Thread.sleep(5000L);
                    }
                    catch (Exception ex) {}
                    Launcher.this._downloadWindow.setVisible(false);
                }
            }).start();
        }
        for (int i = 0; i < list.size(); ++i) {
            final File file = list.get(i);
            try {
                final LaunchDesc buildDescriptor = LaunchDescFactory.buildDescriptor(file);
                final LocalApplicationProperties localApplicationProperties = Cache.getLocalApplicationProperties(file.getPath(), buildDescriptor);
                localApplicationProperties.setLocallyInstalled(false);
                localApplicationProperties.store();
                Trace.println("Installing extension: " + file, TraceLevel.EXTENSIONS);
                final String[] array = { "-installer", file.getAbsolutePath() };
                final JREInfo selectJRE = LaunchSelection.selectJRE(buildDescriptor);
                if (selectJRE == null) {
                    this._downloadWindow.setVisible(true);
                    throw new ExitException(new LaunchDescException(buildDescriptor, ResourceManager.getString("launch.error.missingjreversion"), null), 2);
                }
                final boolean shouldRemoveArgumentFile = JnlpxArgs.shouldRemoveArgumentFile();
                JnlpxArgs.setShouldRemoveArgumentFile("false");
                final Process execProgram = execProgram(selectJRE, array, -1L, -1L, buildDescriptor.getResources().getResourceProperties(), null);
                eatInput(execProgram.getErrorStream());
                eatInput(execProgram.getInputStream());
                execProgram.waitFor();
                JnlpxArgs.setShouldRemoveArgumentFile(String.valueOf(shouldRemoveArgumentFile));
                localApplicationProperties.refresh();
                if (localApplicationProperties.isRebootNeeded()) {
                    boolean b = false;
                    final ExtensionInstallHandler instance = ExtensionInstallHandler.getInstance();
                    if (instance != null && instance.doPreRebootActions(this._downloadWindow.getFrame())) {
                        b = true;
                    }
                    localApplicationProperties.setLocallyInstalled(true);
                    localApplicationProperties.setRebootNeeded(false);
                    localApplicationProperties.store();
                    if (b && instance.doReboot()) {
                        throw new ExitException(null, 1);
                    }
                }
                if (!localApplicationProperties.isLocallyInstalled()) {
                    this._downloadWindow.setVisible(true);
                    throw new ExitException(new LaunchDescException(buildDescriptor, ResourceManager.getString("Launch.error.installfailed"), null), 2);
                }
            }
            catch (JNLPException ex) {
                this._downloadWindow.setVisible(true);
                throw new ExitException(ex, 2);
            }
            catch (IOException ex2) {
                this._downloadWindow.setVisible(true);
                throw new ExitException(ex2, 2);
            }
            catch (InterruptedException ex3) {
                this._downloadWindow.setVisible(true);
                throw new ExitException(ex3, 2);
            }
        }
    }
    
    public static void executeUninstallers(final ArrayList list) throws ExitException {
        for (int i = 0; i < list.size(); ++i) {
            final File file = list.get(i);
            try {
                final LaunchDesc buildDescriptor = LaunchDescFactory.buildDescriptor(file);
                final LocalApplicationProperties localApplicationProperties = Cache.getLocalApplicationProperties(file.getPath(), buildDescriptor);
                Trace.println("uninstalling extension: " + file, TraceLevel.EXTENSIONS);
                final String[] array = { "-silent", "-secure", "-installer", file.getAbsolutePath() };
                final JREInfo selectJRE = LaunchSelection.selectJRE(buildDescriptor);
                if (selectJRE == null) {
                    throw new ExitException(new LaunchDescException(buildDescriptor, ResourceManager.getString("launch.error.missingjreversion"), null), 2);
                }
                final Process execProgram = execProgram(selectJRE, array, -1L, -1L, buildDescriptor.getResources().getResourceProperties(), null);
                eatInput(execProgram.getErrorStream());
                eatInput(execProgram.getInputStream());
                execProgram.waitFor();
                localApplicationProperties.refresh();
                if (localApplicationProperties.isRebootNeeded()) {
                    boolean b = false;
                    final ExtensionInstallHandler instance = ExtensionInstallHandler.getInstance();
                    if (instance != null && instance.doPreRebootActions(null)) {
                        b = true;
                    }
                    localApplicationProperties.setRebootNeeded(false);
                    localApplicationProperties.setLocallyInstalled(false);
                    localApplicationProperties.store();
                    if (b && instance.doReboot()) {
                        throw new ExitException(null, 1);
                    }
                }
            }
            catch (JNLPException ex) {
                throw new ExitException(ex, 2);
            }
            catch (IOException ex2) {
                throw new ExitException(ex2, 2);
            }
            catch (InterruptedException ex3) {
                throw new ExitException(ex3, 2);
            }
        }
    }
    
    private static Process execProgram(final JREInfo jreInfo, final String[] array, final long n, final long n2, final Properties properties, final String s) throws IOException {
        final String path = jreInfo.getPath();
        String s2;
        if (Config.isDebugMode() && Config.isDebugVMMode()) {
            s2 = jreInfo.getDebugJavaPath();
        }
        else {
            s2 = jreInfo.getPath();
        }
        if (s2.length() == 0 || path.length() == 0) {
            throw new IllegalArgumentException("must exist");
        }
        final String[] argumentList = JnlpxArgs.getArgumentList(path, n, n2, properties, s);
        final String[] array2 = new String[1 + argumentList.length + array.length];
        int n3 = 0;
        array2[n3++] = s2;
        for (int i = 0; i < argumentList.length; ++i) {
            array2[n3++] = argumentList[i];
        }
        for (int j = 0; j < array.length; ++j) {
            array2[n3++] = array[j];
        }
        final String[] jpdaSetup = JPDA.JpdaSetup(array2, jreInfo);
        Trace.println("Launching new JRE version: " + jreInfo, TraceLevel.BASIC);
        for (int k = 0; k < jpdaSetup.length; ++k) {
            Trace.println("cmd " + k + " : " + jpdaSetup[k], TraceLevel.BASIC);
        }
        if (Globals.TCKHarnessRun) {
            Main.tckprintln("JVM Starting");
        }
        Trace.flush();
        return Runtime.getRuntime().exec(jpdaSetup);
    }
    
    private void continueLaunch(final LocalApplicationProperties localApplicationProperties, final boolean b, final URL url, final LaunchDesc launchDesc, final boolean b2, final boolean b3, final boolean b4) throws ExitException {
        final AppPolicy instance = AppPolicy.createInstance(launchDesc.getCanonicalHome().getHost());
        try {
            LaunchDownload.checkSignedResources(launchDesc);
            LaunchDownload.checkSignedLaunchDesc(launchDesc);
        }
        catch (JNLPException ex) {
            throw new ExitException(ex, 2);
        }
        catch (IOException ex2) {
            throw new ExitException(ex2, 2);
        }
        final JNLPClassLoader classLoader = JNLPClassLoader.createClassLoader(launchDesc, instance);
        Thread.currentThread().setContextClassLoader(classLoader);
        System.setSecurityManager(new JavaWebStartSecurity());
        try {
            SwingUtilities.invokeAndWait(new Runnable() {
                public void run() {
                    Thread.currentThread().setContextClassLoader(classLoader);
                    try {
                        UIManager.setLookAndFeel(UIManager.getLookAndFeel());
                    }
                    catch (UnsupportedLookAndFeelException ex) {
                        ex.printStackTrace();
                        Trace.ignoredException((Exception)ex);
                    }
                }
            });
        }
        catch (InterruptedException ex3) {
            Trace.ignoredException((Exception)ex3);
        }
        catch (InvocationTargetException ex4) {
            Trace.ignoredException((Exception)ex4);
        }
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                JavawsConsoleController.showConsoleIfEnable();
            }
        });
        Class<?> loadClass;
        try {
            final String mainClassName = LaunchDownload.getMainClassName(launchDesc, true);
            Trace.println("Main-class: " + mainClassName, TraceLevel.BASIC);
            if (mainClassName == null) {
                throw new ClassNotFoundException(mainClassName);
            }
            loadClass = classLoader.loadClass(mainClassName);
            if (this.getClass().getPackage().equals(loadClass.getPackage())) {
                throw new ClassNotFoundException(mainClassName);
            }
        }
        catch (ClassNotFoundException ex5) {
            throw new ExitException(ex5, 2);
        }
        catch (IOException ex6) {
            throw new ExitException(ex6, 2);
        }
        catch (JNLPException ex7) {
            throw new ExitException(ex7, 2);
        }
        catch (Exception ex8) {
            throw new ExitException(ex8, 2);
        }
        catch (Throwable t) {
            t.printStackTrace();
            throw new ExitException(new Exception(), 2);
        }
        URL url2 = launchDesc.getCodebase();
        if (url2 == null) {
            url2 = URLUtil.getBase(url);
        }
        try {
            BasicServiceImpl.initialize(url2, b, BrowserSupport.isWebBrowserSupported());
            if (launchDesc.getLaunchType() == 4) {
                String installDirectory = localApplicationProperties.getInstallDirectory();
                if (installDirectory == null) {
                    installDirectory = Cache.getNewExtensionInstallDirectory();
                    localApplicationProperties.setInstallDirectory(installDirectory);
                }
                ExtensionInstallerServiceImpl.initialize(installDirectory, localApplicationProperties, this._downloadWindow);
            }
        }
        catch (IOException ex9) {
            throw new ExitException(ex9, 2);
        }
        try {
            final DownloadWindow downloadWindow = this._downloadWindow;
            this._downloadWindow = null;
            this.notifyLocalInstallHandler(launchDesc, localApplicationProperties, b2, b3, b4, downloadWindow.getFrame());
            if (Globals.TCKHarnessRun) {
                Main.tckprintln("JNLP Launching");
            }
            this.executeMainClass(launchDesc, localApplicationProperties, loadClass, downloadWindow);
        }
        catch (SecurityException ex10) {
            throw new ExitException(ex10, 2);
        }
        catch (IllegalAccessException ex11) {
            throw new ExitException(ex11, 2);
        }
        catch (IllegalArgumentException ex12) {
            throw new ExitException(ex12, 2);
        }
        catch (InstantiationException ex13) {
            throw new ExitException(ex13, 2);
        }
        catch (InvocationTargetException ex15) {
            Exception ex14 = ex15;
            final Throwable targetException = ex15.getTargetException();
            if (targetException instanceof Exception) {
                ex14 = (Exception)ex15.getTargetException();
            }
            else {
                targetException.printStackTrace();
            }
            throw new ExitException(ex14, 2);
        }
        catch (NoSuchMethodException ex16) {
            throw new ExitException(ex16, 2);
        }
        catch (Exception ex17) {
            Trace.ignoredException(ex17);
        }
        if (launchDesc.getLaunchType() == 4) {
            throw new ExitException(null, 0);
        }
    }
    
    private LaunchDesc downloadResources(final LaunchDesc launchDesc, final boolean b, final boolean b2, final ArrayList list, final boolean b3) throws ExitException {
        if (!this._shownDownloadWindow && !b3) {
            this._shownDownloadWindow = true;
            this._downloadWindow.buildIntroScreen();
            this._downloadWindow.showLoadingProgressScreen();
            this._downloadWindow.setVisible(true);
            SplashScreen.hide();
        }
        try {
            if (b2) {
                final LaunchDesc updatedLaunchDesc = LaunchDownload.getUpdatedLaunchDesc(launchDesc);
                if (updatedLaunchDesc != null) {
                    return updatedLaunchDesc;
                }
            }
            LaunchDownload.downloadExtensions(launchDesc, this._downloadWindow, 0, list);
            if (b) {
                LaunchDownload.downloadJRE(launchDesc, this._downloadWindow, list);
            }
            LaunchDownload.checkJNLPSecurity(launchDesc);
            LaunchDownload.downloadEagerorAll(launchDesc, false, this._downloadWindow, false);
        }
        catch (SecurityException ex) {
            throw new ExitException(ex, 2);
        }
        catch (JNLPException ex2) {
            throw new ExitException(ex2, 2);
        }
        catch (IOException ex3) {
            throw new ExitException(ex3, 2);
        }
        return null;
    }
    
    private void notifyLocalInstallHandler(final LaunchDesc launchDesc, final LocalApplicationProperties localApplicationProperties, final boolean b, final boolean b2, final boolean b3, final Frame frame) {
        if (localApplicationProperties == null) {
            return;
        }
        localApplicationProperties.setLastAccessed(new Date());
        localApplicationProperties.incrementLaunchCount();
        final LocalInstallHandler instance = LocalInstallHandler.getInstance();
        if (launchDesc.isApplicationDescriptor() && (launchDesc.getLocation() != null || launchDesc.getInformation().supportsOfflineOperation())) {
            if (instance != null && instance.isLocalInstallSupported()) {
                final AssociationDesc[] associations = localApplicationProperties.getAssociations();
                if (associations != null && associations.length > 0) {
                    if (b) {
                        instance.removeAssociations(launchDesc, localApplicationProperties);
                        instance.createAssociations(launchDesc, localApplicationProperties, true, frame);
                    }
                }
                else {
                    instance.createAssociations(launchDesc, localApplicationProperties, b3, frame);
                }
                if (localApplicationProperties.isLocallyInstalled()) {
                    if (b && !localApplicationProperties.isLocallyInstalledSystem()) {
                        instance.uninstall(launchDesc, localApplicationProperties, true);
                        instance.install(launchDesc, localApplicationProperties);
                    }
                }
                else {
                    instance.installFromLaunch(launchDesc, localApplicationProperties, b3, frame);
                }
            }
            if (b) {
                final String title = launchDesc.getInformation().getTitle();
                final String string = launchDesc.getCanonicalHome().toString();
                final String registeredTitle = localApplicationProperties.getRegisteredTitle();
                if (registeredTitle != null && registeredTitle.length() != 0) {
                    Config.getInstance().addRemoveProgramsRemove(registeredTitle, Globals.isSystemCache());
                }
                localApplicationProperties.setRegisteredTitle(title);
                Config.getInstance().addRemoveProgramsAdd(Config.getInstance().toExecArg(string), title, Globals.isSystemCache());
            }
        }
        try {
            localApplicationProperties.store();
        }
        catch (IOException ex) {
            Trace.println("Couldn't save LAP: " + ex, TraceLevel.BASIC);
        }
    }
    
    private void executeMainClass(final LaunchDesc launchDesc, final LocalApplicationProperties localApplicationProperties, final Class clazz, final DownloadWindow downloadWindow) throws IllegalAccessException, InstantiationException, InvocationTargetException, NoSuchMethodException {
        if (launchDesc.getLaunchType() == 2) {
            this.executeApplet(launchDesc, clazz, downloadWindow);
        }
        else {
            this.executeApplication(launchDesc, localApplicationProperties, clazz, downloadWindow);
        }
    }
    
    private void executeApplication(final LaunchDesc launchDesc, final LocalApplicationProperties localApplicationProperties, final Class clazz, final DownloadWindow downloadWindow) throws IllegalAccessException, InstantiationException, NoSuchMethodException, InvocationTargetException {
        String[] array;
        if (launchDesc.getLaunchType() == 4) {
            downloadWindow.reset();
            array = new String[] { localApplicationProperties.isLocallyInstalled() ? "uninstall" : "install" };
            localApplicationProperties.setLocallyInstalled(false);
            localApplicationProperties.setRebootNeeded(false);
            try {
                localApplicationProperties.store();
            }
            catch (IOException ex) {
                Trace.ignoredException((Exception)ex);
            }
        }
        else {
            downloadWindow.disposeWindow();
            SplashScreen.hide();
            if (Globals.getApplicationArgs() != null) {
                array = Globals.getApplicationArgs();
            }
            else {
                array = launchDesc.getApplicationDescriptor().getArguments();
            }
        }
        final Object[] array2 = { array };
        final Method method = clazz.getMethod("main", new String[0].getClass());
        if (!Modifier.isStatic(method.getModifiers())) {
            throw new NoSuchMethodException(ResourceManager.getString("launch.error.nonstaticmainmethod"));
        }
        method.setAccessible(true);
        PerfLogger.setEndTime("Calling Application main");
        PerfLogger.outputLog();
        method.invoke(null, array2);
    }
    
    private void executeApplet(final LaunchDesc launchDesc, final Class clazz, final DownloadWindow downloadWindow) throws IllegalAccessException, InstantiationException {
        final AppletDesc appletDescriptor = launchDesc.getAppletDescriptor();
        final int width = appletDescriptor.getWidth();
        final int height = appletDescriptor.getHeight();
        final Applet applet = clazz.newInstance();
        SplashScreen.hide();
        if (downloadWindow.getFrame() == null) {
            downloadWindow.buildIntroScreen();
            downloadWindow.showLaunchingApplication(launchDesc.getInformation().getTitle());
        }
        final JFrame frame = downloadWindow.getFrame();
        BrowserSupport.isWebBrowserSupported();
        final AppletContainerCallback appletContainerCallback = new AppletContainerCallback() {
            public void showDocument(final URL url) {
                BrowserSupport.showDocument(url);
            }
            
            public void relativeResize(final Dimension dimension) {
                final Dimension size2;
                final Dimension size = size2 = frame.getSize();
                size2.width += dimension.width;
                final Dimension dimension2 = size;
                dimension2.height += dimension.height;
                frame.setSize(size);
            }
        };
        final URL codeBase = BasicServiceImpl.getInstance().getCodeBase();
        URL documentBase = appletDescriptor.getDocumentBase();
        if (documentBase == null) {
            documentBase = codeBase;
        }
        final AppletContainer appletContainer = new AppletContainer(appletContainerCallback, applet, appletDescriptor.getName(), documentBase, codeBase, width, height, appletDescriptor.getParameters());
        frame.removeWindowListener(downloadWindow);
        frame.addWindowListener(new WindowAdapter() {
            public void windowClosing(final WindowEvent windowEvent) {
                appletContainer.stopApplet();
            }
        });
        downloadWindow.clearWindow();
        frame.setTitle(launchDesc.getInformation().getTitle());
        final Container contentPane = frame.getContentPane();
        contentPane.setLayout(new BorderLayout());
        contentPane.add("Center", appletContainer);
        frame.pack();
        frame.setSize(appletContainer.getPreferredFrameSize(frame));
        frame.getRootPane().revalidate();
        frame.getRootPane().repaint();
        frame.setResizable(false);
        if (!frame.isVisible()) {
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    frame.setVisible(true);
                }
            });
        }
        appletContainer.startApplet();
    }
    
    private void handleJnlpFileException(final LaunchDesc launchDesc, final Exception ex) throws ExitException {
        try {
            final DiskCacheEntry cachedLaunchedFile = DownloadProtocol.getCachedLaunchedFile(launchDesc.getCanonicalHome());
            if (cachedLaunchedFile != null) {
                Cache.removeEntry(cachedLaunchedFile);
            }
        }
        catch (JNLPException ex2) {
            Trace.ignoredException((Exception)ex2);
        }
        final JFrame frame = (this._downloadWindow == null) ? null : this._downloadWindow.getFrame();
        throw new ExitException(ex, 2);
    }
    
    private static class EatInput implements Runnable
    {
        private InputStream _is;
        
        EatInput(final InputStream is) {
            this._is = is;
        }
        
        public void run() {
            final byte[] array = new byte[1024];
            try {
                for (int i = 0; i != -1; i = this._is.read(array)) {}
            }
            catch (IOException ex) {}
        }
        
        private static void eatInput(final InputStream inputStream) {
            new Thread(new EatInput(inputStream)).start();
        }
    }
    
    private class RapidUpdateCheck extends Thread
    {
        private LaunchDesc _ld;
        private LocalApplicationProperties _lap;
        private boolean _updateAvailable;
        private boolean _checkCompleted;
        private Object _signalObject;
        
        public RapidUpdateCheck() {
            this._signalObject = null;
            this._ld = null;
            this._signalObject = new Object();
        }
        
        private boolean doUpdateCheck(final LaunchDesc ld, final LocalApplicationProperties lap, final int n) {
            this._ld = ld;
            this._lap = lap;
            boolean updateAvailable = false;
            synchronized (this._signalObject) {
                this._updateAvailable = false;
                this._checkCompleted = false;
                this.start();
                do {
                    if (ld.getInformation().supportsOfflineOperation()) {
                        try {
                            this._signalObject.wait(n);
                            updateAvailable = this._updateAvailable;
                        }
                        catch (InterruptedException ex) {
                            updateAvailable = false;
                        }
                    }
                    else {
                        try {
                            this._signalObject.wait(n);
                            updateAvailable = (this._updateAvailable || !this._checkCompleted);
                        }
                        catch (InterruptedException ex2) {
                            updateAvailable = true;
                        }
                    }
                } while ((this._ld.isHttps() && !this._checkCompleted) || (Launcher.this._ja != null && Launcher.this._ja.isChallanging()));
            }
            return updateAvailable;
        }
        
        public void run() {
            boolean updateAvailable = false;
            try {
                updateAvailable = LaunchDownload.isUpdateAvailable(this._ld);
            }
            catch (FailedDownloadingResourceException ex) {
                if (this._ld.isHttps()) {
                    final Throwable wrappedException = ex.getWrappedException();
                    if (wrappedException != null && wrappedException instanceof SSLHandshakeException) {
                        Main.systemExit(0);
                    }
                }
                Trace.ignoredException((Exception)ex);
            }
            catch (JNLPException ex2) {
                Trace.ignoredException((Exception)ex2);
            }
            synchronized (this._signalObject) {
                this._updateAvailable = updateAvailable;
                this._checkCompleted = true;
                this._signalObject.notify();
            }
            if (this._updateAvailable) {
                this._lap.setForceUpdateCheck(true);
                try {
                    this._lap.store();
                }
                catch (IOException ex3) {
                    Trace.ignoredException((Exception)ex3);
                }
            }
        }
    }
}
