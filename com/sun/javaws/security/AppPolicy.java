// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.javaws.security;

import java.util.Hashtable;
import com.sun.deploy.config.Config;
import java.io.FilePermission;
import java.net.SocketPermission;
import java.awt.AWTPermission;
import com.sun.javaws.Main;
import com.sun.deploy.security.BadCertificateDialog;
import com.sun.deploy.security.TrustDecider;
import com.sun.javaws.Globals;
import java.util.Enumeration;
import java.util.Properties;
import com.sun.javaws.jnl.LaunchDesc;
import com.sun.javaws.jnl.JARDesc;
import java.security.AccessControlException;
import java.util.PropertyPermission;
import java.security.Permission;
import java.security.AllPermission;
import com.sun.deploy.security.CeilingPolicy;
import com.sun.jnlp.JNLPClassLoader;
import com.sun.deploy.util.Trace;
import com.sun.deploy.util.TraceLevel;
import java.security.CodeSource;
import java.security.PermissionCollection;
import java.io.File;

public class AppPolicy
{
    private String _host;
    private File _extensionDir;
    private static AppPolicy _instance;
    
    public static AppPolicy getInstance() {
        return AppPolicy._instance;
    }
    
    public static AppPolicy createInstance(final String s) {
        if (AppPolicy._instance == null) {
            AppPolicy._instance = new AppPolicy(s);
        }
        return AppPolicy._instance;
    }
    
    private AppPolicy(final String host) {
        this._host = null;
        this._extensionDir = null;
        this._host = host;
        this._extensionDir = new File(System.getProperty("java.home") + File.separator + "lib" + File.separator + "ext");
    }
    
    public void addPermissions(final PermissionCollection collection, final CodeSource codeSource) {
        Trace.println("Permission requested for: " + codeSource.getLocation(), TraceLevel.SECURITY);
        final JARDesc jarDescFromFileURL = JNLPClassLoader.getInstance().getJarDescFromFileURL(codeSource.getLocation());
        if (jarDescFromFileURL == null) {
            return;
        }
        final LaunchDesc parent = jarDescFromFileURL.getParent().getParent();
        final int securityModel = parent.getSecurityModel();
        if (securityModel != 0) {
            this.grantUnrestrictedAccess(parent, codeSource);
            if (securityModel == 1) {
                CeilingPolicy.addTrustedPermissions(collection);
            }
            else {
                this.addJ2EEApplicationClientPermissionsObject(collection);
            }
        }
        if (!collection.implies(new AllPermission())) {
            this.addSandboxPermissionsObject(collection, parent.getLaunchType() == 2);
        }
        if (!parent.arePropsSet()) {
            final Properties resourceProperties = parent.getResources().getResourceProperties();
            final Enumeration<String> keys = ((Hashtable<String, V>)resourceProperties).keys();
            while (keys.hasMoreElements()) {
                final String s = keys.nextElement();
                final String property = resourceProperties.getProperty(s);
                final PropertyPermission propertyPermission = new PropertyPermission(s, "write");
                if (collection.implies(propertyPermission)) {
                    System.setProperty(s, property);
                }
                else {
                    Trace.ignoredException((Exception)new AccessControlException("access denied " + propertyPermission, propertyPermission));
                }
            }
            parent.setPropsSet(true);
        }
    }
    
    private void setUnrestrictedProps(final LaunchDesc launchDesc) {
        if (!launchDesc.arePropsSet()) {
            final Properties resourceProperties = launchDesc.getResources().getResourceProperties();
            final Enumeration<String> keys = ((Hashtable<String, V>)resourceProperties).keys();
            while (keys.hasMoreElements()) {
                final String s = keys.nextElement();
                System.setProperty(s, resourceProperties.getProperty(s));
            }
            launchDesc.setPropsSet(true);
        }
    }
    
    public void grantUnrestrictedAccess(final LaunchDesc unrestrictedProps, final CodeSource codeSource) {
        String s = null;
        switch (unrestrictedProps.getLaunchType()) {
            default: {
                s = "trustdecider.code.type.application";
                break;
            }
            case 2: {
                s = "trustdecider.code.type.applet";
                break;
            }
            case 3: {
                s = "trustdecider.code.type.extension";
                break;
            }
            case 4: {
                s = "trustdecider.code.type.installer";
                break;
            }
        }
        try {
            if (Globals.isSecureMode() || TrustDecider.isAllPermissionGranted(codeSource, s)) {
                this.setUnrestrictedProps(unrestrictedProps);
                return;
            }
            Trace.println("We were not granted permission, exiting", TraceLevel.SECURITY);
        }
        catch (Exception ex) {
            BadCertificateDialog.show(codeSource, s, ex);
        }
        Main.systemExit(-1);
    }
    
    private void addJ2EEApplicationClientPermissionsObject(final PermissionCollection collection) {
        Trace.println("Creating J2EE-application-client-permisisons object", TraceLevel.SECURITY);
        collection.add(new AWTPermission("accessClipboard"));
        collection.add(new AWTPermission("accessEventQueue"));
        collection.add(new AWTPermission("showWindowWithoutWarningBanner"));
        collection.add(new RuntimePermission("exitVM"));
        collection.add(new RuntimePermission("loadLibrary"));
        collection.add(new RuntimePermission("queuePrintJob"));
        collection.add(new SocketPermission("*", "connect"));
        collection.add(new SocketPermission("localhost:1024-", "accept,listen"));
        collection.add(new FilePermission("*", "read,write"));
        collection.add(new PropertyPermission("*", "read"));
    }
    
    private void addSandboxPermissionsObject(final PermissionCollection collection, final boolean b) {
        Trace.println("Add sandbox permissions", TraceLevel.SECURITY);
        collection.add(new PropertyPermission("java.version", "read"));
        collection.add(new PropertyPermission("java.vendor", "read"));
        collection.add(new PropertyPermission("java.vendor.url", "read"));
        collection.add(new PropertyPermission("java.class.version", "read"));
        collection.add(new PropertyPermission("os.name", "read"));
        collection.add(new PropertyPermission("os.arch", "read"));
        collection.add(new PropertyPermission("os.version", "read"));
        collection.add(new PropertyPermission("file.separator", "read"));
        collection.add(new PropertyPermission("path.separator", "read"));
        collection.add(new PropertyPermission("line.separator", "read"));
        collection.add(new PropertyPermission("java.specification.version", "read"));
        collection.add(new PropertyPermission("java.specification.vendor", "read"));
        collection.add(new PropertyPermission("java.specification.name", "read"));
        collection.add(new PropertyPermission("java.vm.specification.version", "read"));
        collection.add(new PropertyPermission("java.vm.specification.vendor", "read"));
        collection.add(new PropertyPermission("java.vm.specification.name", "read"));
        collection.add(new PropertyPermission("java.vm.version", "read"));
        collection.add(new PropertyPermission("java.vm.vendor", "read"));
        collection.add(new PropertyPermission("java.vm.name", "read"));
        collection.add(new PropertyPermission("javawebstart.version", "read"));
        collection.add(new RuntimePermission("exitVM"));
        collection.add(new RuntimePermission("stopThread"));
        final String string = "Java " + (b ? "Applet" : "Application") + " Window";
        if (Config.getBooleanProperty("deployment.security.sandbox.awtwarningwindow")) {
            System.setProperty("awt.appletWarning", string);
        }
        else {
            collection.add(new AWTPermission("showWindowWithoutWarningBanner"));
        }
        collection.add(new SocketPermission("localhost:1024-", "listen"));
        collection.add(new SocketPermission(this._host, "connect, accept"));
        collection.add(new PropertyPermission("jnlp.*", "read,write"));
        collection.add(new PropertyPermission("javaws.*", "read,write"));
        final String[] secureProperties = Config.getSecureProperties();
        for (int i = 0; i < secureProperties.length; ++i) {
            collection.add(new PropertyPermission(secureProperties[i], "read,write"));
        }
    }
    
    static {
        AppPolicy._instance = null;
    }
}
