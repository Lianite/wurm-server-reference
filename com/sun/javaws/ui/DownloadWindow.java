// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.javaws.ui;

import javax.swing.UIManager;
import java.awt.Frame;
import java.security.AccessController;
import com.sun.javaws.Main;
import java.security.PrivilegedAction;
import java.awt.event.WindowEvent;
import java.awt.Graphics;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import com.sun.javaws.Globals;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import com.sun.deploy.util.Trace;
import javax.swing.SwingUtilities;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Container;
import javax.swing.LookAndFeel;
import java.awt.Toolkit;
import javax.swing.BoundedRangeModel;
import java.awt.event.ActionEvent;
import java.awt.GridLayout;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.BevelBorder;
import javax.swing.BorderFactory;
import java.awt.Component;
import java.awt.LayoutManager;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.event.WindowListener;
import com.sun.deploy.resources.ResourceManager;
import com.sun.deploy.util.DeployUIManager;
import java.io.File;
import com.sun.javaws.jnl.IconDesc;
import com.sun.javaws.jnl.InformationDesc;
import com.sun.javaws.cache.CacheImageLoader;
import com.sun.javaws.jnl.LaunchDesc;
import javax.swing.DefaultBoundedRangeModel;
import javax.swing.JProgressBar;
import javax.swing.JLabel;
import javax.swing.JButton;
import java.awt.Image;
import javax.swing.Timer;
import java.net.URL;
import javax.swing.JFrame;
import com.sun.javaws.cache.CacheImageLoaderCallback;
import com.sun.javaws.LaunchDownload;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;

public class DownloadWindow extends WindowAdapter implements ActionListener, LaunchDownload.DownloadProgress, CacheImageLoaderCallback
{
    private JFrame _frame;
    private String _title;
    private String _vendor;
    private long _estimatedDownloadSize;
    private long _totalDownloadedBytes;
    private URL _currentUrl;
    static final int TIMER_UPDATE_RATE = 1000;
    static final int TIMER_INITIAL_DELAY = 10;
    static final int TIMER_AVERAGE_SIZE = 10;
    Timer _timerObject;
    long[] _timerDownloadAverage;
    int _timerCount;
    long _timerLastBytesCount;
    boolean _timerOn;
    static final int HEART_BEAT_RATE = 250;
    static final boolean[] HEART_BEAT_RYTHM;
    Timer _heartbeatTimer;
    Object _heartbeatLock;
    int _heartbeatCount;
    boolean _heartbeatOn;
    boolean _isCanceled;
    boolean _exitOnCancel;
    private Image _appImage;
    private JButton _cancelButton;
    private JLabel _titleLabel;
    private JLabel _vendorLabel;
    private JLabel _infoStatus;
    private JLabel _infoProgressTxt;
    private JLabel _infoEstimatedTime;
    private JProgressBar _infoProgressBar;
    private JLabel _imageLabel;
    private static final int _yRestriction = 20;
    private static final int MAX_DISPLAY = 20;
    private static final String LEAD = "...";
    private DefaultBoundedRangeModel _loadingModel;
    private ActionListener _cancelActionListener;
    
    public DownloadWindow(final LaunchDesc launchDesc, final boolean b) {
        this._frame = null;
        this._estimatedDownloadSize = 0L;
        this._totalDownloadedBytes = 0L;
        this._currentUrl = null;
        this._timerObject = null;
        this._timerDownloadAverage = new long[10];
        this._timerCount = 0;
        this._timerLastBytesCount = 0L;
        this._timerOn = false;
        this._heartbeatTimer = null;
        this._heartbeatLock = new Object();
        this._heartbeatCount = 0;
        this._heartbeatOn = false;
        this._isCanceled = false;
        this._exitOnCancel = true;
        this._cancelButton = null;
        this._titleLabel = null;
        this._vendorLabel = null;
        this._infoStatus = null;
        this._infoProgressTxt = null;
        this._infoEstimatedTime = null;
        this._infoProgressBar = null;
        this._imageLabel = null;
        this.setLaunchDesc(launchDesc, b);
    }
    
    public DownloadWindow() {
        this._frame = null;
        this._estimatedDownloadSize = 0L;
        this._totalDownloadedBytes = 0L;
        this._currentUrl = null;
        this._timerObject = null;
        this._timerDownloadAverage = new long[10];
        this._timerCount = 0;
        this._timerLastBytesCount = 0L;
        this._timerOn = false;
        this._heartbeatTimer = null;
        this._heartbeatLock = new Object();
        this._heartbeatCount = 0;
        this._heartbeatOn = false;
        this._isCanceled = false;
        this._exitOnCancel = true;
        this._cancelButton = null;
        this._titleLabel = null;
        this._vendorLabel = null;
        this._infoStatus = null;
        this._infoProgressTxt = null;
        this._infoEstimatedTime = null;
        this._infoProgressBar = null;
        this._imageLabel = null;
    }
    
    public void setLaunchDesc(final LaunchDesc launchDesc, final boolean exitOnCancel) {
        final InformationDesc information = launchDesc.getInformation();
        this._title = information.getTitle();
        this._vendor = information.getVendor();
        if (this._titleLabel != null) {
            this._titleLabel.setText(this._title);
            this._vendorLabel.setText(this._vendor);
        }
        this._isCanceled = false;
        this._exitOnCancel = exitOnCancel;
        if (information != null) {
            final IconDesc iconLocation = information.getIconLocation(2, 0);
            if (iconLocation != null) {
                CacheImageLoader.getInstance().loadImage(iconLocation, this);
            }
        }
    }
    
    public void imageAvailable(final IconDesc iconDesc, final Image image, final File file) {
        this.updateImage(image, true);
    }
    
    public void finalImageAvailable(final IconDesc iconDesc, final Image image, final File file) {
    }
    
    public JFrame getFrame() {
        return this._frame;
    }
    
    public void buildIntroScreen() {
        LookAndFeel setLookAndFeel = null;
        try {
            setLookAndFeel = DeployUIManager.setLookAndFeel();
            (this._frame = new JFrame(ResourceManager.getString("product.javaws.name", ""))).addWindowListener(this);
            final JPanel panel = new JPanel(new BorderLayout());
            final Container contentPane = this._frame.getContentPane();
            contentPane.setLayout(new BorderLayout());
            contentPane.add(panel, "Center");
            panel.setBorder(new CompoundBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8), new BevelBorder(1)));
            final JPanel panel2 = new JPanel(new BorderLayout());
            panel.add(panel2, "North");
            final JPanel panel3 = new JPanel(new BorderLayout());
            panel.add(panel3, "Center");
            final JPanel panel4 = new JPanel(new BorderLayout());
            panel4.add(this._imageLabel = new BLabel(), "Center");
            panel4.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
            this.updateImage(ResourceManager.getIcon("java48.image").getImage(), false);
            panel2.add(panel4, "West");
            final Font uiFont = ResourceManager.getUIFont();
            final Font deriveFont = uiFont.deriveFont(22.0f);
            final Font deriveFont2 = uiFont.deriveFont(18.0f);
            final JPanel panel5 = new JPanel(new GridLayout(2, 3));
            panel2.add(panel5, "Center");
            (this._titleLabel = new BLabel(this._title, 360, 0)).setBorder(BorderFactory.createEmptyBorder(8, 0, 0, 0));
            this._titleLabel.setFont(deriveFont);
            panel5.add(this._titleLabel);
            (this._vendorLabel = new BLabel(this._vendor, 0, 0)).setFont(deriveFont2);
            this._vendorLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 8, 0));
            panel5.add(this._vendorLabel);
            final JPanel panel6 = new JPanel(new BorderLayout());
            contentPane.add(panel6, "South");
            (this._cancelButton = new JButton(ResourceManager.getString("launch.cancel"))).addActionListener(new ActionListener() {
                public void actionPerformed(final ActionEvent actionEvent) {
                    DownloadWindow.this.cancelAction();
                }
            });
            this._infoStatus = new BLabel(ResourceManager.getString("launch.initializing", this._title, this._vendor), 0, 0);
            this._infoProgressTxt = new BLabel(" ", 0, 0);
            this._infoEstimatedTime = new BLabel(" ", 0, 0);
            this._loadingModel = new DefaultBoundedRangeModel(0, 1, 0, 100);
            (this._infoProgressBar = new JProgressBar(this._loadingModel)).setOpaque(true);
            this._infoProgressBar.setVisible(true);
            panel3.add(this._infoStatus, "North");
            panel3.add(this._infoProgressTxt, "Center");
            panel3.add(this._infoEstimatedTime, "South");
            panel3.setBorder(BorderFactory.createEmptyBorder(2, 8, 2, 8));
            this._infoProgressBar.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 5));
            panel6.add(this._infoProgressBar, "Center");
            panel6.add(this._cancelButton, "East");
            panel6.setBorder(BorderFactory.createEmptyBorder(0, 10, 8, 10));
            this._frame.pack();
            this.setIndeterminedProgressBar(true);
            final Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
            this._frame.setLocation((screenSize.width - this._frame.getWidth()) / 2, (screenSize.height - this._frame.getHeight()) / 2);
        }
        finally {
            DeployUIManager.restoreLookAndFeel(setLookAndFeel);
        }
    }
    
    public void showLoadingProgressScreen() {
        this.setStatus(ResourceManager.getString("launch.progressScreen"));
        (this._timerObject = new Timer(1000, this)).start();
    }
    
    public void setStatus(final String s) {
        final Runnable runnable = new Runnable() {
            public void run() {
                if (DownloadWindow.this._infoStatus != null) {
                    DownloadWindow.this._infoStatus.setText((s == null) ? " " : s);
                }
            }
        };
        if (this._infoStatus != null && this._infoStatus.isShowing()) {
            SwingUtilities.invokeLater(runnable);
        }
        else {
            runnable.run();
        }
    }
    
    public void setProgressText(final String s) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                if (DownloadWindow.this._infoProgressTxt != null) {
                    DownloadWindow.this._infoProgressTxt.setText((s == null) ? " " : s);
                }
            }
        });
    }
    
    public void setProgressBarVisible(final boolean b) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                if (DownloadWindow.this._infoProgressBar != null) {
                    DownloadWindow.this._infoProgressBar.setVisible(b);
                }
            }
        });
    }
    
    public void setProgressBarValue(final int value) {
        if (this._heartbeatOn) {
            this.setIndeterminedProgressBar(false);
        }
        if (this._loadingModel != null) {
            this._loadingModel.setValue(value);
        }
        this.setProgressBarVisible(value != 0);
    }
    
    public void setIndeterminedProgressBar(final boolean b) {
        if (this._heartbeatTimer == null) {
            this._heartbeatTimer = new Timer(250, new ActionListener() {
                public void actionPerformed(final ActionEvent actionEvent) {
                    synchronized (DownloadWindow.this._heartbeatLock) {
                        if (DownloadWindow.this._heartbeatOn && DownloadWindow.this._heartbeatTimer != null) {
                            DownloadWindow.this._heartbeatCount = (DownloadWindow.this._heartbeatCount + 1) % DownloadWindow.HEART_BEAT_RYTHM.length;
                            if (DownloadWindow.HEART_BEAT_RYTHM[DownloadWindow.this._heartbeatCount]) {
                                DownloadWindow.this._loadingModel.setValue(100);
                            }
                            else {
                                DownloadWindow.this._loadingModel.setValue(0);
                            }
                        }
                    }
                }
            });
        }
        synchronized (this._heartbeatLock) {
            if (b) {
                this.setProgressBarVisible(true);
                this._loadingModel.setValue(0);
                this._heartbeatTimer.start();
                this._heartbeatOn = true;
            }
            else {
                this.setProgressBarVisible(false);
                this._heartbeatTimer.stop();
                this._heartbeatOn = false;
            }
        }
    }
    
    public void showLaunchingApplication(final String s) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                if (DownloadWindow.this._loadingModel != null) {
                    DownloadWindow.this._infoStatus.setText(s);
                    DownloadWindow.this._infoProgressTxt.setText(" ");
                    DownloadWindow.this._infoEstimatedTime.setText(" ");
                    DownloadWindow.this._loadingModel.setValue(0);
                }
            }
        });
    }
    
    private void setEstimatedTime(final String s) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                if (DownloadWindow.this._infoEstimatedTime != null) {
                    DownloadWindow.this._infoEstimatedTime.setText((s == null) ? " " : s);
                }
            }
        });
    }
    
    public void clearWindow() {
        if (SwingUtilities.isEventDispatchThread()) {
            this.clearWindowHelper();
        }
        else {
            try {
                SwingUtilities.invokeAndWait(new Runnable() {
                    public void run() {
                        DownloadWindow.this.clearWindowHelper();
                    }
                });
            }
            catch (Exception ex) {
                Trace.ignoredException(ex);
            }
        }
    }
    
    private void clearWindowHelper() {
        if (this._timerObject != null) {
            this._timerObject.stop();
            this._timerObject = null;
            this._timerDownloadAverage = null;
        }
        if (this._heartbeatTimer != null) {
            synchronized (this._heartbeatLock) {
                this._heartbeatTimer.stop();
                this._heartbeatTimer = null;
            }
        }
        if (this._frame != null) {
            this._infoStatus = null;
            this._infoProgressTxt = null;
            this._infoProgressBar = null;
            this._loadingModel = null;
            this._infoEstimatedTime = null;
            this._cancelButton.removeActionListener(this._cancelActionListener);
            this._cancelButton = null;
            this._cancelActionListener = null;
            this._frame.getContentPane().removeAll();
        }
    }
    
    public void disposeWindow() {
        if (this._frame != null) {
            this.clearWindow();
            this._frame.removeWindowListener(this);
            this._frame.setVisible(false);
            this._frame.dispose();
            this._frame = null;
        }
    }
    
    public void reset() {
        this.setStatus(null);
        this.setProgressText(null);
        this.setProgressBarVisible(false);
    }
    
    public void actionPerformed(final ActionEvent actionEvent) {
        if (!this._timerOn) {
            return;
        }
        if (this._estimatedDownloadSize <= 0L) {
            return;
        }
        final long n = this._totalDownloadedBytes - this._timerLastBytesCount;
        this._timerLastBytesCount = this._totalDownloadedBytes;
        this._timerDownloadAverage[this._timerCount % 10] = n;
        if (this._totalDownloadedBytes > this._estimatedDownloadSize) {
            this._estimatedDownloadSize = this._totalDownloadedBytes;
        }
        if (this._timerCount > 10) {
            float n2 = 0.0f;
            for (int i = 0; i < 10; ++i) {
                n2 += this._timerDownloadAverage[i];
            }
            final float n3 = n2 / 10.0f / 1.0f;
            if (n3 == 0.0f) {
                this.setEstimatedTime(ResourceManager.getString("launch.stalledDownload"));
            }
            else if (this._estimatedDownloadSize > 0L) {
                final int n4 = (int)((this._estimatedDownloadSize - this._totalDownloadedBytes) / n3);
                final int n5 = n4 / 3600;
                final int n6 = n4 - n5 * 3600;
                final int n7 = n6 / 60;
                this.setEstimatedTime(ResourceManager.getString("launch.estimatedTimeLeft", n5, n7, n6 - n7 * 60));
            }
        }
        ++this._timerCount;
    }
    
    public void resetDownloadTimer() {
        this._timerCount = 0;
        this._timerLastBytesCount = 0L;
    }
    
    public void progress(final URL currentUrl, final String s, final long n, final long estimatedDownloadSize, final int progressBarValue) {
        this._timerOn = true;
        this._totalDownloadedBytes = Math.max(0L, n);
        this._estimatedDownloadSize = estimatedDownloadSize;
        if (currentUrl != this._currentUrl && currentUrl != null) {
            String s2 = currentUrl.getHost();
            String s3 = currentUrl.getFile();
            final int lastIndex = s3.lastIndexOf(47);
            if (lastIndex != -1) {
                s3 = s3.substring(lastIndex + 1);
            }
            if (s3.length() + s2.length() > 40) {
                s3 = this.maxDisplay(s3);
                s2 = this.maxDisplay(s2);
            }
            this.setStatus(ResourceManager.getString("launch.loadingNetStatus", s3, s2));
            this._currentUrl = currentUrl;
        }
        if (estimatedDownloadSize == -1L) {
            this.setProgressText(ResourceManager.getString("launch.loadingNetProgress", this.bytesToString(this._totalDownloadedBytes)));
        }
        else {
            this.setProgressText(ResourceManager.getString("launch.loadingNetProgressPercent", this.bytesToString(this._totalDownloadedBytes), this.bytesToString(estimatedDownloadSize), new Long(Math.max(0, progressBarValue)).toString()));
            this.setProgressBarValue(progressBarValue);
        }
    }
    
    public void patching(final URL currentUrl, final String s, final int n, final int progressBarValue) {
        this._timerOn = false;
        this.setEstimatedTime(null);
        if (this._currentUrl != currentUrl || n == 0) {
            String s2 = currentUrl.getHost();
            String s3 = currentUrl.getFile();
            final int lastIndex = s3.lastIndexOf(47);
            if (lastIndex != -1) {
                s3 = s3.substring(lastIndex + 1);
            }
            if (s3.length() + s2.length() > 40) {
                s3 = this.maxDisplay(s3);
                s2 = this.maxDisplay(s2);
            }
            this.setStatus(ResourceManager.getString("launch.patchingStatus", s3, s2));
            this._currentUrl = currentUrl;
        }
        this.setProgressText(null);
        this.setProgressBarValue(progressBarValue);
    }
    
    private String maxDisplay(String string) {
        final int length = string.length();
        if (length > 20) {
            string = "..." + string.substring(length - (20 - "...".length()), length);
        }
        return string;
    }
    
    public void validating(final URL currentUrl, final String s, final long n, final long n2, final int progressBarValue) {
        this._timerOn = false;
        this.setEstimatedTime(null);
        final long n3 = (n2 == 0L) ? 0L : (n * 100L / n2);
        if (this._currentUrl != currentUrl || n == 0L) {
            String s2 = currentUrl.getHost();
            String s3 = currentUrl.getFile();
            final int lastIndex = s3.lastIndexOf(47);
            if (lastIndex != -1) {
                s3 = s3.substring(lastIndex + 1);
            }
            if (s3.length() + s2.length() > 40) {
                s3 = this.maxDisplay(s3);
                s2 = this.maxDisplay(s2);
            }
            this.setStatus(ResourceManager.getString("launch.validatingStatus", s3, s2));
            this._currentUrl = currentUrl;
        }
        if (n != 0L) {
            this.setProgressText(ResourceManager.getString("launch.validatingProgress", (int)n3));
        }
        else {
            this.setProgressText(null);
        }
        this.setProgressBarValue(progressBarValue);
    }
    
    public void downloadFailed(final URL url, final String s) {
        this._timerOn = false;
        this.setEstimatedTime(null);
        this.setStatus(ResourceManager.getString("launch.loadingResourceFailedSts", url.toString()));
        this.setProgressText(ResourceManager.getString("launch.loadingResourceFailed"));
        this.setProgressBarVisible(false);
    }
    
    public void extensionDownload(final String s, final int n) {
        this._timerOn = false;
        this.setEstimatedTime(null);
        if (s != null) {
            this.setStatus(ResourceManager.getString("launch.extensiondownload-name", s, n));
        }
        else {
            this.setStatus(ResourceManager.getString("launch.extensiondownload", s, n));
        }
    }
    
    public void jreDownload(final String s, final URL url) {
        this._timerOn = false;
        this.setEstimatedTime(null);
        this.setStatus(ResourceManager.getString("launch.downloadingJRE", s, this.maxDisplay(url.getHost())));
    }
    
    private void loadingFromNet(final URL url, final int n, final int n2) {
    }
    
    private void setAppImage(final Image image) {
        this.updateImage(image, true);
    }
    
    private void updateImage(Image appImage, final boolean b) {
        if (appImage != null) {
            final int width = appImage.getWidth(null);
            final int height = appImage.getHeight(null);
            if (width > 64 || height > 64) {
                int n = 64;
                if (height > width && height < 2 * width) {
                    n = 64 * width / height;
                }
                final Image image = new BufferedImage(64, 64, 1);
                if (!Globals.isHeadless()) {
                    final Graphics graphics = image.getGraphics();
                    try {
                        if (this._imageLabel != null) {
                            graphics.setColor(this._imageLabel.getBackground());
                            graphics.fillRect(0, 0, 64, 64);
                        }
                        graphics.drawImage(appImage, (64 - n) / 2, 0, n, 64, null);
                    }
                    finally {
                        graphics.dispose();
                    }
                }
                appImage = image;
            }
            else if (width < 64 || height < 64) {
                final BufferedImage bufferedImage = new BufferedImage(64, 64, 1);
                final Graphics graphics2 = bufferedImage.getGraphics();
                try {
                    if (this._imageLabel != null) {
                        graphics2.setColor(this._imageLabel.getBackground());
                        graphics2.fillRect(0, 0, 64, 64);
                    }
                    graphics2.drawImage(appImage, (64 - width) / 2, (64 - height) / 2, width, height, null);
                }
                finally {
                    graphics2.dispose();
                }
                appImage = bufferedImage;
            }
        }
        synchronized (this) {
            if (this._appImage == null || b) {
                this._appImage = appImage;
            }
        }
        if (this._imageLabel != null) {
            if (this._appImage != null) {
                this._imageLabel.setIcon(new ImageIcon(this._appImage));
            }
            this._imageLabel.repaint();
        }
    }
    
    private String bytesToString(final long n) {
        String s = "";
        double n2 = n;
        int n3 = 0;
        if (n > 1073741824L) {
            n2 /= 1.073741824E9;
            s = "G";
            n3 = 1;
        }
        else if (n > 1048576L) {
            n2 /= 1048576.0;
            s = "M";
            n3 = 1;
        }
        else if (n > 1024L) {
            n2 /= 1024.0;
            s = "K";
            n3 = 0;
        }
        return ResourceManager.formatDouble(n2, n3) + s;
    }
    
    public void windowClosing(final WindowEvent windowEvent) {
        this.cancelAction();
    }
    
    private void cancelAction() {
        if (this._exitOnCancel) {
            AccessController.doPrivileged((PrivilegedAction<Object>)new PrivilegedAction() {
                public Object run() {
                    Main.systemExit(-1);
                    return null;
                }
            });
        }
        else {
            this._isCanceled = true;
        }
    }
    
    public boolean isCanceled() {
        return this._isCanceled;
    }
    
    public void resetCancled() {
        this._isCanceled = false;
    }
    
    public void setVisible(final boolean b) {
        final JFrame frame = this._frame;
        if (frame != null) {
            SwingUtilities.invokeLater(new Runnable() {
                private final /* synthetic */ Frame val$f = frame;
                
                public void run() {
                    this.val$f.setVisible(b);
                }
            });
        }
    }
    
    static {
        HEART_BEAT_RYTHM = new boolean[] { false, false, false, true, false, true };
    }
    
    class BLabel extends JLabel
    {
        int _w;
        int _h;
        
        public BLabel() {
            this._w = 0;
            this._h = 0;
            this.setOpaque(true);
            this.setForeground(UIManager.getColor("textText"));
        }
        
        public BLabel(final String s, final int w, final int h) {
            super(s);
            this._w = w;
            this._h = h;
            this.setOpaque(true);
            this.setForeground(UIManager.getColor("textText"));
        }
        
        public Dimension getPreferredSize() {
            final Dimension preferredSize = super.getPreferredSize();
            if (this._w > preferredSize.width) {
                preferredSize.width = this._w;
            }
            if (this._h > preferredSize.height) {
                preferredSize.height = this._h;
            }
            return preferredSize;
        }
    }
}
