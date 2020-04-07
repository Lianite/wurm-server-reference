// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.javaws.ui;

import java.awt.image.ImageObserver;
import java.awt.Image;
import com.sun.javaws.cache.CacheImageLoader;
import com.sun.javaws.cache.CacheImageLoaderCallback;
import java.awt.Stroke;
import java.awt.Graphics2D;
import java.awt.Graphics;
import javax.swing.BorderFactory;
import com.sun.javaws.LaunchDownload;
import com.sun.javaws.jnl.IconDesc;
import com.sun.javaws.cache.DownloadProtocol;
import java.net.URL;
import com.sun.javaws.cache.Cache;
import com.sun.deploy.util.Trace;
import com.sun.javaws.jnl.LaunchDescFactory;
import java.awt.Component;
import java.io.File;
import javax.swing.Icon;
import javax.swing.JLabel;
import com.sun.deploy.resources.ResourceManager;
import com.sun.javaws.LocalApplicationProperties;
import com.sun.javaws.jnl.LaunchDesc;
import java.awt.BasicStroke;
import java.util.Date;
import javax.swing.table.AbstractTableModel;
import com.sun.javaws.cache.DiskCacheEntry;
import javax.swing.ImageIcon;
import java.text.DateFormat;

class CacheObject
{
    private static final DateFormat _df;
    private static final String[] COLUMN_KEYS;
    private static final int _columns;
    private static TLabel _title;
    private static TLabel _vendor;
    private static TLabel _type;
    private static TLabel _date;
    private static TLabel _size;
    private static TLabel _status;
    private static ImageIcon _onlineIcon;
    private static ImageIcon _offlineIcon;
    private static ImageIcon _noLaunchIcon;
    private static ImageIcon _java32;
    private final DiskCacheEntry _dce;
    private final AbstractTableModel _model;
    private final int ICON_W = 32;
    private final int ICON_H = 32;
    private String _titleString;
    private ImageIcon _icon;
    private String _vendorString;
    private String _typeString;
    private Date _theDate;
    private String _dateString;
    private long _theSize;
    private String _sizeString;
    private int _statusInt;
    private ImageIcon _statusIcon;
    private String _statusText;
    private static final float[] dash;
    private static final BasicStroke _dashed;
    LaunchDesc _ld;
    LocalApplicationProperties _lap;
    static /* synthetic */ Class class$com$sun$deploy$resources$ResourceManager;
    static /* synthetic */ Class class$javax$swing$JLabel;
    
    public CacheObject(final DiskCacheEntry dce, final AbstractTableModel model) {
        this._titleString = null;
        this._icon = null;
        this._vendorString = null;
        this._typeString = null;
        this._theDate = null;
        this._dateString = null;
        this._theSize = 0L;
        this._sizeString = null;
        this._statusInt = -1;
        this._statusIcon = null;
        this._statusText = "";
        this._ld = null;
        this._lap = null;
        this._dce = dce;
        this._model = model;
        if (CacheObject._title == null) {
            CacheObject._title = new TLabel(2);
            CacheObject._vendor = new TLabel(2);
            CacheObject._type = new TLabel(0);
            CacheObject._date = new TLabel(4);
            CacheObject._size = new TLabel(4);
            CacheObject._status = new TLabel(0);
            CacheObject._java32 = new ViewerIcon(0, 0, ((CacheObject.class$com$sun$deploy$resources$ResourceManager == null) ? (CacheObject.class$com$sun$deploy$resources$ResourceManager = class$("com.sun.deploy.resources.ResourceManager")) : CacheObject.class$com$sun$deploy$resources$ResourceManager).getResource("image/java32.png"));
            CacheObject._onlineIcon = new ViewerIcon(0, 0, ((CacheObject.class$com$sun$deploy$resources$ResourceManager == null) ? (CacheObject.class$com$sun$deploy$resources$ResourceManager = class$("com.sun.deploy.resources.ResourceManager")) : CacheObject.class$com$sun$deploy$resources$ResourceManager).getResource("image/online.gif"));
            CacheObject._offlineIcon = new ViewerIcon(0, 0, ((CacheObject.class$com$sun$deploy$resources$ResourceManager == null) ? (CacheObject.class$com$sun$deploy$resources$ResourceManager = class$("com.sun.deploy.resources.ResourceManager")) : CacheObject.class$com$sun$deploy$resources$ResourceManager).getResource("image/offline.gif"));
            CacheObject._noLaunchIcon = null;
        }
    }
    
    public static String getColumnName(final int n) {
        return ResourceManager.getMessage(CacheObject.COLUMN_KEYS[n]);
    }
    
    public static int getColumnCount() {
        return CacheObject._columns;
    }
    
    public static String getHeaderToolTipText(final int n) {
        return ResourceManager.getString(CacheObject.COLUMN_KEYS[n] + ".tooltip");
    }
    
    public static int getPreferredWidth(final int n) {
        if (n < CacheObject._columns) {
            switch (n) {
                case 0: {
                    return 192;
                }
                case 1: {
                    return 140;
                }
                case 2: {
                    return 70;
                }
                case 3: {
                    return 70;
                }
                case 4: {
                    return 64;
                }
                case 5: {
                    return 64;
                }
            }
        }
        throw new ArrayIndexOutOfBoundsException("column index: " + n);
    }
    
    public static Class getClass(final int n) {
        if (n < CacheObject._columns) {
            switch (n) {
                case 0: {
                    return (CacheObject.class$javax$swing$JLabel == null) ? (CacheObject.class$javax$swing$JLabel = class$("javax.swing.JLabel")) : CacheObject.class$javax$swing$JLabel;
                }
                case 1: {
                    return (CacheObject.class$javax$swing$JLabel == null) ? (CacheObject.class$javax$swing$JLabel = class$("javax.swing.JLabel")) : CacheObject.class$javax$swing$JLabel;
                }
                case 2: {
                    return (CacheObject.class$javax$swing$JLabel == null) ? (CacheObject.class$javax$swing$JLabel = class$("javax.swing.JLabel")) : CacheObject.class$javax$swing$JLabel;
                }
                case 3: {
                    return (CacheObject.class$javax$swing$JLabel == null) ? (CacheObject.class$javax$swing$JLabel = class$("javax.swing.JLabel")) : CacheObject.class$javax$swing$JLabel;
                }
                case 4: {
                    return (CacheObject.class$javax$swing$JLabel == null) ? (CacheObject.class$javax$swing$JLabel = class$("javax.swing.JLabel")) : CacheObject.class$javax$swing$JLabel;
                }
                case 5: {
                    return (CacheObject.class$javax$swing$JLabel == null) ? (CacheObject.class$javax$swing$JLabel = class$("javax.swing.JLabel")) : CacheObject.class$javax$swing$JLabel;
                }
            }
        }
        throw new ArrayIndexOutOfBoundsException("column index: " + n);
    }
    
    public Object getObject(final int n) {
        if (n < CacheObject._columns) {
            switch (n) {
                case 0: {
                    return this.getTitleLabel();
                }
                case 1: {
                    return this.getVendorLabel();
                }
                case 2: {
                    return this.getTypeLabel();
                }
                case 3: {
                    return this.getDateLabel();
                }
                case 4: {
                    return this.getSizeLabel();
                }
                case 5: {
                    return this.getStatusLabel();
                }
            }
        }
        throw new ArrayIndexOutOfBoundsException("column index: " + n);
    }
    
    public boolean isEditable(final int n) {
        return false;
    }
    
    public void setValue(final int n, final Object o) {
    }
    
    public String getTitleString() {
        if (this._titleString == null) {
            this._titleString = this.getTitle();
        }
        return this._titleString;
    }
    
    private JLabel getTitleLabel() {
        if (this._icon == null) {
            final File iconFile = this.getIconFile();
            if (iconFile != null) {
                this._icon = new ViewerIcon(32, 32, iconFile.getPath());
            }
            if (this._icon == null) {
                this._icon = CacheObject._java32;
            }
        }
        if (this._icon != null && this._icon.getIconWidth() > 0 && this._icon.getIconHeight() > 0) {
            CacheObject._title.setIcon(this._icon);
        }
        CacheObject._title.setText(this.getTitleString());
        return CacheObject._title;
    }
    
    public String getVendorString() {
        if (this._vendorString == null) {
            this._vendorString = this.getVendor();
        }
        return this._vendorString;
    }
    
    private TLabel getVendorLabel() {
        CacheObject._vendor.setText(this.getVendorString());
        return CacheObject._vendor;
    }
    
    public String getTypeString() {
        if (this._typeString == null) {
            this._typeString = getLaunchTypeString(this.getLaunchDesc().getLaunchType());
        }
        return this._typeString;
    }
    
    public static String getLaunchTypeString(final int n) {
        switch (n) {
            case 1: {
                return ResourceManager.getMessage("jnlp.viewer.application");
            }
            case 2: {
                return ResourceManager.getMessage("jnlp.viewer.applet");
            }
            case 3: {
                return ResourceManager.getMessage("jnlp.viewer.extension");
            }
            case 4: {
                return ResourceManager.getMessage("jnlp.viewer.installer");
            }
            default: {
                return "";
            }
        }
    }
    
    private TLabel getTypeLabel() {
        CacheObject._type.setText(this.getTypeString());
        return CacheObject._type;
    }
    
    public Date getDate() {
        if (this._dateString == null) {
            this._theDate = this.getLastAccesed();
            if (this._theDate != null) {
                this._dateString = CacheObject._df.format(this._theDate);
            }
            else {
                this._dateString = "";
            }
        }
        return this._theDate;
    }
    
    private TLabel getDateLabel() {
        this.getDate();
        CacheObject._date.setText(this._dateString);
        return CacheObject._date;
    }
    
    public long getSize() {
        if (this._sizeString == null) {
            this._theSize = this.getResourceSize();
            if (this._theSize > 10240L) {
                this._sizeString = " " + this._theSize / 1024L + " KB";
            }
            else {
                this._sizeString = " " + this._theSize / 1024L + "." + this._theSize % 1024L / 102L + " KB";
            }
        }
        return this._theSize;
    }
    
    private TLabel getSizeLabel() {
        this.getSize();
        CacheObject._size.setText(this._sizeString);
        return CacheObject._size;
    }
    
    public int getStatus() {
        if (this._statusInt < 0) {
            if (this.canLaunchOffline()) {
                this._statusInt = 2;
            }
            else {
                this._statusInt = (this.hasHref() ? 1 : 0);
            }
            switch (this._statusInt) {
                case 0: {
                    this._statusIcon = CacheObject._noLaunchIcon;
                    if (this.getLaunchDesc().isApplicationDescriptor()) {
                        this._statusText = ResourceManager.getString("jnlp.viewer.norun1.tooltip", this.getTypeString());
                        break;
                    }
                    this._statusText = ResourceManager.getString("jnlp.viewer.norun2.tooltip");
                    break;
                }
                case 1: {
                    this._statusIcon = CacheObject._onlineIcon;
                    this._statusText = ResourceManager.getString("jnlp.viewer.online.tooltip", this.getTypeString());
                    break;
                }
                case 2: {
                    this._statusIcon = CacheObject._offlineIcon;
                    this._statusText = ResourceManager.getString("jnlp.viewer.offline.tooltip", this.getTypeString());
                    break;
                }
            }
        }
        return this._statusInt;
    }
    
    private TLabel getStatusLabel() {
        this.getStatus();
        if (this._statusIcon == null || (this._statusIcon.getIconWidth() > 0 && this._statusIcon.getIconHeight() > 0)) {
            CacheObject._status.setIcon(this._statusIcon);
            CacheObject._status.setToolTipText(this._statusText);
        }
        return CacheObject._status;
    }
    
    public static void hasFocus(final Component component, final boolean b) {
        if (component instanceof TLabel) {
            ((TLabel)component).hasFocus(b);
        }
    }
    
    public int compareColumns(final CacheObject cacheObject, final int n) {
        switch (n) {
            case 0: {
                return this.compareStrings(this.getTitleString(), cacheObject.getTitleString());
            }
            case 1: {
                return this.compareStrings(this.getVendorString(), cacheObject.getVendorString());
            }
            case 2: {
                return this.compareStrings(this.getTypeString(), cacheObject.getTypeString());
            }
            case 3: {
                return this.compareDates(this.getDate(), cacheObject.getDate());
            }
            case 4: {
                return this.compareLong(this.getSize(), cacheObject.getSize());
            }
            default: {
                return this.compareInt(this.getStatus(), cacheObject.getStatus());
            }
        }
    }
    
    private int compareStrings(final String s, final String s2) {
        if (s == s2) {
            return 0;
        }
        if (s == null) {
            return -1;
        }
        if (s2 == null) {
            return 1;
        }
        return s.compareTo(s2);
    }
    
    private int compareDates(final Date date, final Date date2) {
        if (date == date2) {
            return 0;
        }
        if (date == null) {
            return -1;
        }
        if (date2 == null) {
            return 1;
        }
        return this.compareLong(date.getTime(), date2.getTime());
    }
    
    private int compareLong(final long n, final long n2) {
        if (n == n2) {
            return 0;
        }
        return (n < n2) ? -1 : 1;
    }
    
    private int compareInt(final int n, final int n2) {
        if (n == n2) {
            return 0;
        }
        return (n < n2) ? -1 : 1;
    }
    
    public DiskCacheEntry getDCE() {
        return this._dce;
    }
    
    public LaunchDesc getLaunchDesc() {
        if (this._ld == null) {
            try {
                this._ld = LaunchDescFactory.buildDescriptor(this._dce.getFile());
            }
            catch (Exception ex) {
                Trace.ignoredException(ex);
            }
        }
        return this._ld;
    }
    
    public LocalApplicationProperties getLocalApplicationProperties() {
        if (this._lap == null) {
            this._lap = Cache.getLocalApplicationProperties(this._dce, this.getLaunchDesc());
        }
        return this._lap;
    }
    
    public File getJnlpFile() {
        return this._dce.getFile();
    }
    
    public String getTitle() {
        try {
            return this.getLaunchDesc().getInformation().getTitle();
        }
        catch (Exception ex) {
            return "";
        }
    }
    
    public String getVendor() {
        try {
            return this.getLaunchDesc().getInformation().getVendor();
        }
        catch (Exception ex) {
            return "";
        }
    }
    
    public String getHref() {
        final URL location = this.getLaunchDesc().getLocation();
        if (location != null) {
            return location.toString();
        }
        return null;
    }
    
    public File getIconFile() {
        try {
            final IconDesc iconLocation = this.getLaunchDesc().getInformation().getIconLocation(1, 0);
            final DiskCacheEntry cachedVersion = DownloadProtocol.getCachedVersion(iconLocation.getLocation(), iconLocation.getVersion(), 2);
            if (cachedVersion != null) {
                return cachedVersion.getFile();
            }
        }
        catch (Exception ex) {}
        return null;
    }
    
    public Date getLastAccesed() {
        return this.getLocalApplicationProperties().getLastAccessed();
    }
    
    public long getResourceSize() {
        return LaunchDownload.getCachedSize(this.getLaunchDesc());
    }
    
    public boolean inFilter(final int n) {
        return n == 0 || n == this.getLaunchDesc().getLaunchType();
    }
    
    public boolean hasHref() {
        return this.getLaunchDesc().isApplicationDescriptor() && this._ld.getLocation() != null;
    }
    
    public boolean canLaunchOffline() {
        return this.getLaunchDesc().isApplicationDescriptor() && this._ld.getInformation().supportsOfflineOperation();
    }
    
    static /* synthetic */ Class class$(final String s) {
        try {
            return Class.forName(s);
        }
        catch (ClassNotFoundException ex) {
            throw new NoClassDefFoundError(ex.getMessage());
        }
    }
    
    static {
        _df = DateFormat.getDateInstance();
        COLUMN_KEYS = new String[] { "jnlp.viewer.app.column", "jnlp.viewer.vendor.column", "jnlp.viewer.type.column", "jnlp.viewer.date.column", "jnlp.viewer.size.column", "jnlp.viewer.status.column" };
        _columns = CacheObject.COLUMN_KEYS.length;
        dash = new float[] { 1.0f, 2.0f };
        _dashed = new BasicStroke(1.0f, 2, 0, 10.0f, CacheObject.dash, 0.0f);
    }
    
    private class TLabel extends JLabel
    {
        boolean _focus;
        
        public TLabel(final int horizontalAlignment) {
            this._focus = false;
            this.setOpaque(true);
            this.setBorder(BorderFactory.createEmptyBorder(0, 4, 0, 4));
            this.setHorizontalAlignment(horizontalAlignment);
        }
        
        public void paint(final Graphics graphics) {
            super.paint(graphics);
            if (this._focus && graphics instanceof Graphics2D) {
                final Stroke stroke = ((Graphics2D)graphics).getStroke();
                ((Graphics2D)graphics).setStroke(CacheObject._dashed);
                graphics.drawRect(0, 0, this.getWidth() - 1, this.getHeight() - 1);
                ((Graphics2D)graphics).setStroke(stroke);
            }
        }
        
        public void hasFocus(final boolean focus) {
            this._focus = focus;
        }
    }
    
    private class ViewerIcon extends ImageIcon implements CacheImageLoaderCallback
    {
        private int _width;
        private int _height;
        
        public ViewerIcon(final int width, final int height, final String s) {
            this._width = width;
            this._height = height;
            try {
                final URL url = new File(s).toURL();
                if (url != null) {
                    CacheImageLoader.getInstance().loadImage(url, this);
                }
            }
            catch (Exception ex) {
                Trace.ignoredException(ex);
            }
        }
        
        public ViewerIcon(final int width, final int height, final URL url) {
            this._width = width;
            this._height = height;
            if (url != null) {
                CacheImageLoader.getInstance().loadImage(url, this);
            }
        }
        
        public void imageAvailable(final IconDesc iconDesc, final Image image, final File file) {
            new Thread(new Runnable() {
                private final /* synthetic */ int val$w = image.getWidth(null);
                private final /* synthetic */ int val$h = image.getHeight(null);
                
                public void run() {
                    Image image = image;
                    if (ViewerIcon.this._width > 0 && ViewerIcon.this._height > 0 && (ViewerIcon.this._width != this.val$w || ViewerIcon.this._height != this.val$h)) {
                        image = image.getScaledInstance(ViewerIcon.this._width, ViewerIcon.this._height, 1);
                    }
                    ViewerIcon.this.setImage(image);
                    CacheObject.this._model.fireTableDataChanged();
                }
            }).start();
        }
        
        public void finalImageAvailable(final IconDesc iconDesc, final Image image, final File file) {
        }
    }
}
