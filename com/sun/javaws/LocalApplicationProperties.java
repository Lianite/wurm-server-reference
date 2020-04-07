// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.javaws;

import java.io.IOException;
import com.sun.javaws.jnl.AssociationDesc;
import java.util.Date;
import com.sun.javaws.jnl.LaunchDesc;
import java.net.URL;

public interface LocalApplicationProperties
{
    URL getLocation();
    
    String getVersionId();
    
    LaunchDesc getLaunchDescriptor();
    
    void setLastAccessed(final Date p0);
    
    Date getLastAccessed();
    
    int getLaunchCount();
    
    void incrementLaunchCount();
    
    void setAskedForInstall(final boolean p0);
    
    boolean getAskedForInstall();
    
    void setRebootNeeded(final boolean p0);
    
    boolean isRebootNeeded();
    
    void setLocallyInstalled(final boolean p0);
    
    boolean isLocallyInstalled();
    
    boolean isLocallyInstalledSystem();
    
    boolean forceUpdateCheck();
    
    void setForceUpdateCheck(final boolean p0);
    
    boolean isApplicationDescriptor();
    
    boolean isExtensionDescriptor();
    
    AssociationDesc[] getAssociations();
    
    void addAssociation(final AssociationDesc p0);
    
    void setAssociations(final AssociationDesc[] p0);
    
    String getNativeLibDirectory();
    
    String getInstallDirectory();
    
    void setNativeLibDirectory(final String p0);
    
    void setInstallDirectory(final String p0);
    
    String getRegisteredTitle();
    
    void setRegisteredTitle(final String p0);
    
    void put(final String p0, final String p1);
    
    String get(final String p0);
    
    int getInteger(final String p0);
    
    boolean getBoolean(final String p0);
    
    Date getDate(final String p0);
    
    void store() throws IOException;
    
    void refreshIfNecessary();
    
    void refresh();
    
    boolean isShortcutSupported();
}
