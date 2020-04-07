// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.javaws;

import java.lang.reflect.InvocationTargetException;
import javax.swing.SwingUtilities;
import java.net.URL;
import com.sun.javaws.jnl.RContentDesc;
import com.sun.javaws.ui.DesktopIntegration;
import com.sun.javaws.jnl.ShortcutDesc;
import com.sun.javaws.jnl.AssociationDesc;
import com.sun.deploy.config.Config;
import com.sun.deploy.util.Trace;
import com.sun.deploy.util.TraceLevel;
import java.util.StringTokenizer;
import com.sun.deploy.association.AssociationService;
import com.sun.deploy.association.Action;
import java.util.Iterator;
import java.awt.Component;
import com.sun.deploy.util.DialogFactory;
import com.sun.deploy.resources.ResourceManager;
import java.util.ArrayList;
import java.awt.Frame;
import java.io.File;
import java.io.IOException;
import com.sun.javaws.cache.Cache;
import com.sun.deploy.association.AssociationNotRegisteredException;
import com.sun.deploy.association.RegisterFailedException;
import com.sun.deploy.association.AssociationAlreadyRegisteredException;
import com.sun.deploy.association.Association;
import com.sun.javaws.jnl.LaunchDesc;

public abstract class LocalInstallHandler
{
    private static LocalInstallHandler _installHandler;
    
    public static synchronized LocalInstallHandler getInstance() {
        if (LocalInstallHandler._installHandler == null) {
            LocalInstallHandler._installHandler = LocalInstallHandlerFactory.newInstance();
        }
        return LocalInstallHandler._installHandler;
    }
    
    public abstract void install(final LaunchDesc p0, final LocalApplicationProperties p1);
    
    public abstract void uninstall(final LaunchDesc p0, final LocalApplicationProperties p1, final boolean p2);
    
    public abstract boolean isLocalInstallSupported();
    
    public abstract boolean isAssociationSupported();
    
    public abstract void associationCompleted();
    
    public abstract String getAssociationOpenCommand(final String p0);
    
    public abstract String getAssociationPrintCommand(final String p0);
    
    public abstract void registerAssociationInternal(final Association p0) throws AssociationAlreadyRegisteredException, RegisterFailedException;
    
    public abstract void unregisterAssociationInternal(final Association p0) throws AssociationNotRegisteredException, RegisterFailedException;
    
    public abstract String getDefaultIconPath();
    
    private String getJnlpLocation(final LaunchDesc launchDesc) {
        File cachedLaunchedFile = null;
        try {
            cachedLaunchedFile = Cache.getCachedLaunchedFile(launchDesc.getCanonicalHome());
        }
        catch (IOException ex) {}
        String s;
        if (cachedLaunchedFile != null) {
            s = cachedLaunchedFile.getAbsolutePath();
        }
        else {
            s = launchDesc.getLocation().toString();
        }
        return s;
    }
    
    private boolean promptUserAssociation(final LaunchDesc launchDesc, final Association association, final boolean b, final String s, final boolean b2, final Frame frame) {
        if (b2) {
            return true;
        }
        final String mimeType = association.getMimeType();
        final ArrayList list = (ArrayList)association.getFileExtList();
        String s2 = "";
        if (list != null) {
            final Iterator<Object> iterator = list.iterator();
            while (iterator.hasNext()) {
                s2 += iterator.next();
                if (iterator.hasNext()) {
                    s2 += ", ";
                }
            }
        }
        String s5;
        if (b) {
            String s3 = ResourceManager.getString("javaws.association.dialog.existAsk") + "\n\n";
            if (s2 != "") {
                s3 = s3 + ResourceManager.getString("javaws.association.dialog.ext", s2) + "\n";
            }
            if (mimeType != null) {
                s3 = s3 + ResourceManager.getString("javaws.association.dialog.mime", mimeType) + "\n";
            }
            String s4;
            if (s == null) {
                s4 = s3 + "\n" + ResourceManager.getString("javaws.association.dialog.exist");
            }
            else {
                s4 = s3 + "\n" + ResourceManager.getString("javaws.association.dialog.exist.command", s);
            }
            s5 = s4 + "\n" + ResourceManager.getString("javaws.association.dialog.askReplace", launchDesc.getInformation().getTitle());
        }
        else {
            s5 = ResourceManager.getString("javaws.association.dialog.ask", launchDesc.getInformation().getTitle()) + "\n";
            if (s2 != "") {
                s5 = s5 + ResourceManager.getString("javaws.association.dialog.ext", s2) + "\n";
            }
            if (mimeType != null) {
                s5 = s5 + ResourceManager.getString("javaws.association.dialog.mime", mimeType) + "\n";
            }
        }
        int showConfirmDialog = 1;
        if (!b2) {
            showConfirmDialog = DialogFactory.showConfirmDialog((Component)frame, (Object)s5, ResourceManager.getString("javaws.association.dialog.title"));
        }
        return showConfirmDialog == 0;
    }
    
    private String getOpenActionCommand(final Association association) {
        final Action actionByVerb = association.getActionByVerb("open");
        String command = null;
        if (actionByVerb != null) {
            command = actionByVerb.getCommand();
        }
        return command;
    }
    
    private boolean registerAssociation(final LaunchDesc launchDesc, final String s, final String mimeType, final boolean b, final Frame frame) {
        final AssociationService associationService = new AssociationService();
        final Association association = new Association();
        boolean b2 = false;
        String string = "";
        String s2 = null;
        if (s != null) {
            final StringTokenizer stringTokenizer = new StringTokenizer(s);
            while (stringTokenizer.hasMoreTokens()) {
                final String string2 = "." + stringTokenizer.nextToken();
                Trace.println("associate with ext: " + string2, TraceLevel.BASIC);
                if (string == "") {
                    string = string2 + " file";
                }
                final Association fileExtensionAssociation = associationService.getFileExtensionAssociation(string2);
                if (fileExtensionAssociation != null) {
                    Trace.println("associate with ext: " + string2 + " already EXIST", TraceLevel.BASIC);
                    if (s2 == null) {
                        s2 = this.getOpenActionCommand(fileExtensionAssociation);
                    }
                    b2 = true;
                }
                association.addFileExtension(string2);
            }
        }
        if (mimeType != null) {
            Trace.println("associate with mime: " + mimeType, TraceLevel.BASIC);
            final Association mimeTypeAssociation = associationService.getMimeTypeAssociation(mimeType);
            if (mimeTypeAssociation != null) {
                Trace.println("associate with mime: " + mimeType + " already EXIST", TraceLevel.BASIC);
                if (s2 == null) {
                    s2 = this.getOpenActionCommand(mimeTypeAssociation);
                }
                b2 = true;
            }
            association.setMimeType(mimeType);
        }
        association.setName(launchDesc.getInformation().getTitle());
        association.setDescription(string);
        String iconFileName = IcoEncoder.getIconPath(launchDesc);
        if (iconFileName == null) {
            iconFileName = this.getDefaultIconPath();
        }
        association.setIconFileName(iconFileName);
        final String jnlpLocation = this.getJnlpLocation(launchDesc);
        final String associationOpenCommand = this.getAssociationOpenCommand(jnlpLocation);
        final String associationPrintCommand = this.getAssociationPrintCommand(jnlpLocation);
        Trace.println("register OPEN using: " + associationOpenCommand, TraceLevel.BASIC);
        association.addAction(new Action("open", associationOpenCommand, "open the file"));
        if (associationPrintCommand != null) {
            Trace.println("register PRINT using: " + associationPrintCommand, TraceLevel.BASIC);
            association.addAction(new Action("print", associationPrintCommand, "print the file"));
        }
        try {
            if (!Globals.createAssoc()) {
                switch (Config.getAssociationValue()) {
                    case 0: {
                        return false;
                    }
                    case 1: {
                        if (b2) {
                            return false;
                        }
                        break;
                    }
                    case 2: {
                        if (!this.promptUserAssociation(launchDesc, association, b2, s2, b, frame)) {
                            return false;
                        }
                        break;
                    }
                    case 3: {
                        if (b2 && !this.promptUserAssociation(launchDesc, association, b2, s2, b, frame)) {
                            return false;
                        }
                        break;
                    }
                    default: {
                        if (!this.promptUserAssociation(launchDesc, association, b2, s2, b, frame)) {
                            return false;
                        }
                        break;
                    }
                }
            }
            this.registerAssociationInternal(association);
        }
        catch (AssociationAlreadyRegisteredException ex5) {
            try {
                this.unregisterAssociationInternal(association);
                this.registerAssociationInternal(association);
            }
            catch (AssociationNotRegisteredException ex) {
                Trace.ignoredException((Exception)ex);
                return false;
            }
            catch (AssociationAlreadyRegisteredException ex2) {
                Trace.ignoredException((Exception)ex2);
                return false;
            }
            catch (RegisterFailedException ex3) {
                Trace.ignoredException((Exception)ex3);
                return false;
            }
        }
        catch (RegisterFailedException ex4) {
            Trace.ignoredException((Exception)ex4);
            return false;
        }
        return true;
    }
    
    private void unregisterAssociation(final LaunchDesc launchDesc, final String s, final String s2) {
        final AssociationService associationService = new AssociationService();
        if (s2 != null) {
            final StringTokenizer stringTokenizer = new StringTokenizer(s2);
            while (stringTokenizer.hasMoreTokens()) {
                final String string = "." + stringTokenizer.nextToken();
                final Association fileExtensionAssociation = associationService.getFileExtensionAssociation(string);
                if (fileExtensionAssociation != null) {
                    fileExtensionAssociation.setName(launchDesc.getInformation().getTitle());
                    Trace.println("remove association with ext: " + string, TraceLevel.BASIC);
                    try {
                        this.unregisterAssociationInternal(fileExtensionAssociation);
                    }
                    catch (AssociationNotRegisteredException ex) {
                        Trace.ignoredException((Exception)ex);
                    }
                    catch (RegisterFailedException ex2) {
                        Trace.ignoredException((Exception)ex2);
                    }
                }
            }
        }
        if (s != null) {
            final Association mimeTypeAssociation = associationService.getMimeTypeAssociation(s);
            if (mimeTypeAssociation != null) {
                mimeTypeAssociation.setName(launchDesc.getInformation().getTitle());
                Trace.println("remove association with mime: " + s, TraceLevel.BASIC);
                try {
                    this.unregisterAssociationInternal(mimeTypeAssociation);
                }
                catch (AssociationNotRegisteredException ex3) {
                    Trace.ignoredException((Exception)ex3);
                }
                catch (RegisterFailedException ex4) {
                    Trace.ignoredException((Exception)ex4);
                }
            }
        }
    }
    
    public void removeAssociations(final LaunchDesc launchDesc, final LocalApplicationProperties localApplicationProperties) {
        if (this.isAssociationSupported()) {
            final AssociationDesc[] associations = localApplicationProperties.getAssociations();
            if (associations != null) {
                for (int i = 0; i < associations.length; ++i) {
                    this.unregisterAssociation(launchDesc, associations[i].getMimeType(), associations[i].getExtensions());
                }
                localApplicationProperties.setAssociations(null);
                this.associationCompleted();
            }
        }
    }
    
    public void createAssociations(final LaunchDesc launchDesc, final LocalApplicationProperties localApplicationProperties, final boolean b, final Frame frame) {
        if (Config.getAssociationValue() == 0) {
            return;
        }
        if (this.isAssociationSupported()) {
            final AssociationDesc[] associations = launchDesc.getInformation().getAssociations();
            for (int i = 0; i < associations.length; ++i) {
                if (this.registerAssociation(launchDesc, associations[i].getExtensions(), associations[i].getMimeType(), b, frame)) {
                    localApplicationProperties.addAssociation(associations[i]);
                    save(localApplicationProperties);
                }
            }
            this.associationCompleted();
        }
    }
    
    public void installFromLaunch(final LaunchDesc launchDesc, final LocalApplicationProperties localApplicationProperties, final boolean b, final Frame frame) {
        final ShortcutDesc shortcut = launchDesc.getInformation().getShortcut();
        if (shortcut != null && !shortcut.getDesktop() && !shortcut.getMenu()) {
            return;
        }
        if (b && Globals.createShortcut()) {
            this.doInstall(launchDesc, localApplicationProperties);
            return;
        }
        switch (Config.getShortcutValue()) {
            case 0: {
                return;
            }
            case 1: {
                this.doInstall(launchDesc, localApplicationProperties);
                return;
            }
            case 4: {
                if (shortcut != null) {
                    this.doInstall(launchDesc, localApplicationProperties);
                }
                return;
            }
            case 3: {
                if (shortcut == null) {
                    return;
                }
                break;
            }
        }
        if (localApplicationProperties.getAskedForInstall()) {
            return;
        }
        if (b) {
            return;
        }
        this.showDialog(launchDesc, localApplicationProperties, frame);
    }
    
    private void showDialog(final LaunchDesc launchDesc, final LocalApplicationProperties localApplicationProperties, final Frame frame) {
        switch (DesktopIntegration.showDTIDialog(frame, launchDesc)) {
            case 1: {
                this.doInstall(launchDesc, localApplicationProperties);
                break;
            }
            case 0: {
                localApplicationProperties.setAskedForInstall(true);
                break;
            }
            default: {
                localApplicationProperties.setAskedForInstall(false);
                break;
            }
        }
    }
    
    public void doInstall(final LaunchDesc launchDesc, final LocalApplicationProperties localApplicationProperties) {
        this.install(launchDesc, localApplicationProperties);
        localApplicationProperties.setAskedForInstall(true);
        final RContentDesc[] relatedContent = launchDesc.getInformation().getRelatedContent();
        if (relatedContent != null) {
            for (int i = 0; i < relatedContent.length; ++i) {
                final URL href = relatedContent[i].getHref();
                if (!"jar".equals(href.getProtocol()) && href.toString().endsWith(".jnlp")) {
                    try {
                        Main.importApp(href.toString());
                    }
                    catch (Exception ex) {
                        Trace.ignoredException(ex);
                    }
                }
            }
        }
    }
    
    public static boolean shouldInstallOverExisting(final LaunchDesc launchDesc) {
        final int[] array = { 1 };
        final Runnable runnable = new Runnable() {
            public void run() {
                array[0] = DialogFactory.showConfirmDialog((Object)ResourceManager.getString("install.alreadyInstalled", launchDesc.getInformation().getTitle()), ResourceManager.getString("install.alreadyInstalledTitle"));
            }
        };
        if (!Globals.isSilentMode()) {
            invokeRunnable(runnable);
        }
        return array[0] == 0;
    }
    
    public static void invokeRunnable(final Runnable runnable) {
        if (SwingUtilities.isEventDispatchThread()) {
            runnable.run();
        }
        else {
            try {
                SwingUtilities.invokeAndWait(runnable);
            }
            catch (InterruptedException ex) {}
            catch (InvocationTargetException ex2) {}
        }
    }
    
    public static void save(final LocalApplicationProperties localApplicationProperties) {
        try {
            localApplicationProperties.store();
        }
        catch (IOException ex) {
            Trace.ignoredException((Exception)ex);
        }
    }
    
    public boolean addUninstallShortcut() {
        return Config.getBooleanProperty("deployment.javaws.uninstall.shortcut") && !Globals.isSystemCache();
    }
}
