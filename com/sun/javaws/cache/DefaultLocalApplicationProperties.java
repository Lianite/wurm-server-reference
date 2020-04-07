// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.javaws.cache;

import java.util.Hashtable;
import java.io.OutputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.ByteArrayInputStream;
import com.sun.javaws.jnl.ShortcutDesc;
import com.sun.deploy.util.Trace;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import com.sun.javaws.jnl.AssociationDesc;
import java.util.Date;
import java.net.URL;
import java.util.Properties;
import com.sun.javaws.jnl.LaunchDesc;
import java.text.DateFormat;
import com.sun.javaws.LocalApplicationProperties;

public class DefaultLocalApplicationProperties implements LocalApplicationProperties
{
    private static final String REBOOT_NEEDED_KEY = "_default.rebootNeeded";
    private static final String UPDATE_CHECK_KEY = "_default.forcedUpdateCheck";
    private static final String NATIVELIB_DIR_KEY = "_default.nativeLibDir";
    private static final String INSTALL_DIR_KEY = "_default.installDir";
    private static final String LAST_ACCESSED_KEY = "_default.lastAccessed";
    private static final String LAUNCH_COUNT_KEY = "_default.launchCount";
    private static final String ASK_INSTALL_KEY = "_default.askedInstall";
    private static final String SHORTCUT_KEY = "_default.locallyInstalled";
    private static final String INDIRECT_PATH_KEY = "_default.indirectPath";
    private static final String ASSOCIATION_MIME_KEY = "_default.mime.types.";
    private static final String REGISTERED_TITLE_KEY = "_default.title";
    private static final String ASSOCIATION_EXTENSIONS_KEY = "_default.extensions.";
    private static final DateFormat _df;
    private LaunchDesc _descriptor;
    private Properties _properties;
    private URL _location;
    private String _versionId;
    private long _lastAccessed;
    private boolean _isApplicationDescriptor;
    private boolean _dirty;
    private boolean _isLocallyInstalledSystem;
    
    public DefaultLocalApplicationProperties(final URL location, final String versionId, final LaunchDesc descriptor, final boolean isApplicationDescriptor) {
        this._descriptor = descriptor;
        this._location = location;
        this._versionId = versionId;
        this._isApplicationDescriptor = isApplicationDescriptor;
        this._properties = this.getLocalApplicationPropertiesStorage(this);
        this._isLocallyInstalledSystem = false;
    }
    
    public URL getLocation() {
        return this._location;
    }
    
    public String getVersionId() {
        return this._versionId;
    }
    
    public LaunchDesc getLaunchDescriptor() {
        return this._descriptor;
    }
    
    public void setLastAccessed(final Date date) {
        this.put("_default.lastAccessed", DefaultLocalApplicationProperties._df.format(date));
    }
    
    public Date getLastAccessed() {
        return this.getDate("_default.lastAccessed");
    }
    
    public void incrementLaunchCount() {
        int launchCount = this.getLaunchCount();
        this.put("_default.launchCount", Integer.toString(++launchCount));
    }
    
    public int getLaunchCount() {
        return this.getInteger("_default.launchCount");
    }
    
    public void setAskedForInstall(final boolean b) {
        this.put("_default.askedInstall", new Boolean(b).toString());
    }
    
    public boolean getAskedForInstall() {
        return this.getBoolean("_default.askedInstall");
    }
    
    public void setRebootNeeded(final boolean b) {
        this.put("_default.rebootNeeded", new Boolean(b).toString());
    }
    
    public boolean isRebootNeeded() {
        return this.getBoolean("_default.rebootNeeded");
    }
    
    public void setLocallyInstalled(final boolean b) {
        this.put("_default.locallyInstalled", new Boolean(b).toString());
    }
    
    public boolean isLocallyInstalled() {
        return this.getBoolean("_default.locallyInstalled");
    }
    
    public boolean isLocallyInstalledSystem() {
        return this._isLocallyInstalledSystem;
    }
    
    public boolean forceUpdateCheck() {
        return this.getBoolean("_default.forcedUpdateCheck");
    }
    
    public void setForceUpdateCheck(final boolean b) {
        this.put("_default.forcedUpdateCheck", new Boolean(b).toString());
    }
    
    public boolean isApplicationDescriptor() {
        return this._isApplicationDescriptor;
    }
    
    public boolean isExtensionDescriptor() {
        return !this._isApplicationDescriptor;
    }
    
    public String getInstallDirectory() {
        return this.get("_default.installDir");
    }
    
    public void setInstallDirectory(final String s) {
        this.put("_default.installDir", s);
    }
    
    public String getNativeLibDirectory() {
        return this.get("_default.nativeLibDir");
    }
    
    public String getRegisteredTitle() {
        return this.get("_default.title");
    }
    
    public void setRegisteredTitle(final String s) {
        this.put("_default.title", s);
    }
    
    public void setNativeLibDirectory(final String s) {
        this.put("_default.nativeLibDir", s);
    }
    
    public void setAssociations(final AssociationDesc[] array) {
        final int n = 0;
        if (array == null) {
            if (this.getAssociations() != null) {
                this.put("_default.mime.types." + n, null);
                this.put("_default.extensions." + n, null);
            }
        }
        else {
            int i;
            for (i = 0; i < array.length; ++i) {
                this.put("_default.mime.types." + i, array[i].getMimeType());
                this.put("_default.extensions." + i, array[i].getExtensions());
            }
            this.put("_default.mime.types." + i, null);
            this.put("_default.extensions." + i, null);
        }
    }
    
    public void addAssociation(final AssociationDesc associationDesc) {
        final AssociationDesc[] associations = this.getAssociations();
        int i = 0;
        AssociationDesc[] associations2;
        if (associations == null) {
            associations2 = new AssociationDesc[] { null };
        }
        else {
            associations2 = new AssociationDesc[associations.length + 1];
            while (i < associations.length) {
                associations2[i] = associations[i];
                ++i;
            }
        }
        associations2[i] = associationDesc;
        this.setAssociations(associations2);
    }
    
    public AssociationDesc[] getAssociations() {
        final ArrayList<AssociationDesc> list = new ArrayList<AssociationDesc>();
        int n = 0;
        while (true) {
            final String value = this.get("_default.mime.types." + n);
            final String value2 = this.get("_default.extensions." + n);
            if (value == null && value2 == null) {
                break;
            }
            list.add(new AssociationDesc(value2, value));
            ++n;
        }
        return list.toArray(new AssociationDesc[0]);
    }
    
    public void put(final String s, final String s2) {
        synchronized (this) {
            if (s2 == null) {
                this._properties.remove(s);
            }
            else {
                ((Hashtable<String, String>)this._properties).put(s, s2);
            }
            this._dirty = true;
        }
    }
    
    public String get(final String s) {
        synchronized (this) {
            return ((Hashtable<K, String>)this._properties).get(s);
        }
    }
    
    public int getInteger(final String s) {
        final String value = this.get(s);
        if (value == null) {
            return 0;
        }
        int int1;
        try {
            int1 = Integer.parseInt(value);
        }
        catch (NumberFormatException ex) {
            int1 = 0;
        }
        return int1;
    }
    
    public boolean getBoolean(final String s) {
        final String value = this.get(s);
        return value != null && Boolean.valueOf(value);
    }
    
    public Date getDate(final String s) {
        final String value = this.get(s);
        if (value == null) {
            return null;
        }
        try {
            return DefaultLocalApplicationProperties._df.parse(value);
        }
        catch (ParseException ex) {
            return null;
        }
    }
    
    public boolean doesNewVersionExist() {
        synchronized (this) {
            final long lastAccessed = Cache.getLastAccessed();
            if (lastAccessed == 0L) {
                return false;
            }
            if (lastAccessed > this._lastAccessed) {
                return true;
            }
        }
        return false;
    }
    
    public synchronized void store() throws IOException {
        this.putLocalApplicationPropertiesStorage(this, this._properties);
        this._dirty = false;
    }
    
    public void refreshIfNecessary() {
        synchronized (this) {
            if (!this._dirty && this.doesNewVersionExist()) {
                this.refresh();
            }
        }
    }
    
    public void refresh() {
        synchronized (this) {
            this._properties = this.getLocalApplicationPropertiesStorage(this);
            this._dirty = false;
        }
    }
    
    public boolean isShortcutSupported() {
        DiskCacheEntry cacheEntry;
        try {
            cacheEntry = Cache.getCacheEntry('A', this._location, null);
        }
        catch (IOException ex) {
            Trace.ignoredException((Exception)ex);
            return false;
        }
        if (cacheEntry == null || cacheEntry.isEmpty()) {
            return false;
        }
        final ShortcutDesc shortcut = this._descriptor.getInformation().getShortcut();
        return shortcut == null || shortcut.getDesktop() || shortcut.getMenu();
    }
    
    private Properties getLocalApplicationPropertiesStorage(final DefaultLocalApplicationProperties defaultLocalApplicationProperties) {
        final Properties properties = new Properties();
        try {
            final URL location = defaultLocalApplicationProperties.getLocation();
            final String versionId = defaultLocalApplicationProperties.getVersionId();
            if (location != null) {
                final char c = defaultLocalApplicationProperties.isApplicationDescriptor() ? 'A' : 'E';
                final byte[] lapData = Cache.getLapData(c, location, versionId, true);
                if (lapData != null) {
                    properties.load(new ByteArrayInputStream(lapData));
                    final String s = ((Hashtable<K, String>)properties).get("_default.locallyInstalled");
                    if (s != null) {
                        this._isLocallyInstalledSystem = Boolean.valueOf(s);
                    }
                }
                final byte[] lapData2 = Cache.getLapData(c, location, versionId, false);
                if (lapData2 != null) {
                    properties.load(new ByteArrayInputStream(lapData2));
                }
            }
        }
        catch (IOException ex) {
            Trace.ignoredException((Exception)ex);
        }
        return properties;
    }
    
    private void putLocalApplicationPropertiesStorage(final DefaultLocalApplicationProperties defaultLocalApplicationProperties, final Properties properties) throws IOException {
        final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        try {
            properties.store(byteArrayOutputStream, "LAP");
        }
        catch (IOException ex) {}
        byteArrayOutputStream.close();
        Cache.putLapData(defaultLocalApplicationProperties.isApplicationDescriptor() ? 'A' : 'E', defaultLocalApplicationProperties.getLocation(), defaultLocalApplicationProperties.getVersionId(), byteArrayOutputStream.toByteArray());
    }
    
    static {
        _df = DateFormat.getDateTimeInstance();
    }
}
