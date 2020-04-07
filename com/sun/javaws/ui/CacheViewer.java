// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.javaws.ui;

import javax.swing.LookAndFeel;
import java.util.StringTokenizer;
import com.sun.javaws.util.JavawsConsoleController;
import com.sun.deploy.util.DeployUIManager;
import com.sun.deploy.si.SingleInstanceManager;
import com.sun.javaws.SplashScreen;
import java.util.Enumeration;
import java.util.Properties;
import java.awt.Dimension;
import javax.swing.table.TableModel;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import javax.swing.JPopupMenu;
import com.sun.deploy.util.AboutDialog;
import com.sun.deploy.util.DialogFactory;
import javax.swing.JTextArea;
import com.sun.javaws.BrowserSupport;
import java.io.IOException;
import java.awt.Rectangle;
import com.sun.javaws.LocalApplicationProperties;
import com.sun.javaws.jnl.InformationDesc;
import com.sun.javaws.jnl.LaunchDesc;
import com.sun.javaws.cache.Cache;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ListSelectionEvent;
import java.text.MessageFormat;
import javax.swing.JMenuBar;
import com.sun.deploy.util.Trace;
import java.net.URL;
import com.sun.deploy.config.Config;
import javax.swing.SwingUtilities;
import javax.swing.JCheckBoxMenuItem;
import com.sun.javaws.Main;
import java.awt.event.ActionEvent;
import javax.swing.Box;
import com.sun.javaws.Globals;
import javax.swing.border.Border;
import javax.swing.BorderFactory;
import java.awt.LayoutManager;
import java.awt.BorderLayout;
import javax.swing.JPanel;
import java.awt.event.WindowListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowAdapter;
import com.sun.deploy.resources.ResourceManager;
import java.awt.Component;
import com.sun.deploy.si.SingleInstanceImpl;
import com.sun.javaws.LocalInstallHandler;
import javax.swing.JLabel;
import javax.swing.border.TitledBorder;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JButton;
import com.sun.deploy.si.DeploySIListener;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.ChangeListener;
import java.awt.event.ActionListener;
import javax.swing.JFrame;

public class CacheViewer extends JFrame implements ActionListener, ChangeListener, ListSelectionListener, DeploySIListener
{
    private final JButton _removeBtn;
    private final JButton _launchOnlineBtn;
    private final JButton _launchOfflineBtn;
    private final JTabbedPane _tabbedPane;
    private final CacheTable _userTable;
    private final CacheTable _sysTable;
    private final JScrollPane _userTab;
    private final JScrollPane _systemTab;
    private static final String BOUNDS_PROPERTY_KEY = "deployment.javaws.viewer.bounds";
    private JMenuItem _launchOnlineMI;
    private JMenuItem _launchOfflineMI;
    private JMenuItem _removeMI;
    private JMenuItem _showMI;
    private JMenuItem _installMI;
    private JMenuItem _browseMI;
    private JMenu _fileMenu;
    private JMenu _editMenu;
    private JMenu _appMenu;
    private JMenu _viewMenu;
    private JMenu _helpMenu;
    private TitledBorder _titledBorder;
    public static final int STATUS_OK = 0;
    public static final int STATUS_REMOVING = 1;
    public static final int STATUS_LAUNCHING = 2;
    public static final int STATUS_BROWSING = 3;
    public static final int STATUS_SORTING = 4;
    public static final int STATUS_SEARCHING = 5;
    public static final int STATUS_INSTALLING = 6;
    private static int _status;
    private static JLabel _statusLabel;
    private static final JLabel _totalSize;
    private static final LocalInstallHandler _lih;
    private static final boolean _isLocalInstallSupported;
    private static long t0;
    private static long t1;
    private static long t2;
    private static long t3;
    private static long t4;
    private SingleInstanceImpl _sil;
    private static final String JAVAWS_CV_ID;
    private static final int SLEEP_DELAY = 2000;
    static /* synthetic */ Class class$java$lang$String;
    
    public CacheViewer() {
        (this._sil = new SingleInstanceImpl()).addSingleInstanceListener((DeploySIListener)this, CacheViewer.JAVAWS_CV_ID);
        this._removeBtn = this.makeButton("jnlp.viewer.remove.btn");
        this._launchOnlineBtn = this.makeButton("jnlp.viewer.launch.online.btn");
        this._launchOfflineBtn = this.makeButton("jnlp.viewer.launch.offline.btn");
        CacheViewer._statusLabel = new JLabel(" ");
        this._tabbedPane = new JTabbedPane();
        this._userTable = new CacheTable(this, false);
        this._sysTable = new CacheTable(this, true);
        this._userTab = new JScrollPane(this._userTable);
        this._systemTab = new JScrollPane(this._sysTable);
        this.initComponents();
    }
    
    private void initComponents() {
        this.setTitle(ResourceManager.getMessage("jnlp.viewer.title"));
        this.addWindowListener(new WindowAdapter() {
            public void windowClosing(final WindowEvent windowEvent) {
                CacheViewer.this.exitViewer();
            }
        });
        final JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        this._titledBorder = new TitledBorder(ResourceManager.getMessage("jnlp.viewer.all"));
        final Border emptyBorder = BorderFactory.createEmptyBorder(4, 4, 4, 4);
        panel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createCompoundBorder(emptyBorder, this._titledBorder), emptyBorder));
        if (Globals.isSystemCache()) {
            this._tabbedPane.addTab(ResourceManager.getMessage("cert.dialog.system.level"), this._userTab);
        }
        else {
            this._tabbedPane.addTab(ResourceManager.getMessage("cert.dialog.user.level"), this._userTab);
            this._tabbedPane.addTab(ResourceManager.getMessage("cert.dialog.system.level"), this._systemTab);
        }
        this._tabbedPane.setSelectedIndex(0);
        this._tabbedPane.addChangeListener(this);
        panel.add(this._tabbedPane, "Center");
        final Box horizontalBox = Box.createHorizontalBox();
        horizontalBox.setBorder(BorderFactory.createEmptyBorder(6, 0, 0, 0));
        horizontalBox.add(this._removeBtn);
        horizontalBox.add(Box.createHorizontalGlue());
        horizontalBox.add(this._launchOnlineBtn);
        horizontalBox.add(Box.createHorizontalStrut(5));
        horizontalBox.add(this._launchOfflineBtn);
        panel.add(horizontalBox, "South");
        final JPanel panel2 = new JPanel(new BorderLayout());
        CacheViewer._totalSize.setText(this.getAppMessage("jnlp.viewer.totalSize", ""));
        CacheViewer._totalSize.setHorizontalAlignment(0);
        CacheViewer._totalSize.setFont(ResourceManager.getUIFont());
        final JPanel panel3 = new JPanel(new BorderLayout());
        (CacheViewer._statusLabel = new JLabel(" ")).setBorder(BorderFactory.createEmptyBorder(0, 6, 0, 6));
        CacheViewer._statusLabel.setFont(ResourceManager.getUIFont());
        panel3.add(CacheViewer._statusLabel, "West");
        panel3.add(CacheViewer._totalSize, "Center");
        panel3.setBorder(BorderFactory.createEtchedBorder(1));
        panel2.add(panel3, "South");
        panel2.setBorder(BorderFactory.createEmptyBorder(6, 6, 6, 6));
        this.getContentPane().add(Box.createVerticalStrut(8), "North");
        this.getContentPane().add(panel, "Center");
        this.getContentPane().add(panel2, "South");
        this.createMenuBar();
        this.pack();
        this._userTable.getSelectionModel().addListSelectionListener(this);
        this._sysTable.getSelectionModel().addListSelectionListener(this);
    }
    
    private void createMenuBar() {
        (this._fileMenu = new JMenu(ResourceManager.getMessage("jnlp.viewer.file.menu"))).setMnemonic(ResourceManager.getVKCode("jnlp.viewer.file.menu.mnemonic"));
        final JMenuItem add = this._fileMenu.add(ResourceManager.getMessage("jnlp.viewer.exit.mi"));
        add.setMnemonic(ResourceManager.getVKCode("jnlp.viewer.exit.mi.mnemonic"));
        add.addActionListener(new ActionListener() {
            public void actionPerformed(final ActionEvent actionEvent) {
                CacheViewer.this.exitViewer();
            }
        });
        (this._editMenu = new JMenu(ResourceManager.getMessage("jnlp.viewer.edit.menu"))).setMnemonic(ResourceManager.getVKCode("jnlp.viewer.edit.menu.mnemonic"));
        final JMenuItem add2 = this._editMenu.add(ResourceManager.getMessage("jnlp.viewer.reinstall.mi"));
        add2.setMnemonic(ResourceManager.getVKCode("jnlp.viewer.reinstall.mi.mnemonic"));
        add2.addActionListener(new ActionListener() {
            public void actionPerformed(final ActionEvent actionEvent) {
                CacheViewer.this.showReInstallDialog();
            }
        });
        this._editMenu.addSeparator();
        final JMenuItem add3 = this._editMenu.add(ResourceManager.getMessage("jnlp.viewer.preferences.mi"));
        add3.setMnemonic(ResourceManager.getVKCode("jnlp.viewer.preferences.mi.mnemonic"));
        add3.addActionListener(new ActionListener() {
            public void actionPerformed(final ActionEvent actionEvent) {
                Main.launchJavaControlPanel("general");
            }
        });
        (this._appMenu = new JMenu(ResourceManager.getMessage("jnlp.viewer.app.menu"))).setMnemonic(ResourceManager.getVKCode("jnlp.viewer.app.menu.mnemonic"));
        this._appMenu.addActionListener(new ActionListener() {
            public void actionPerformed(final ActionEvent actionEvent) {
                CacheViewer.this.refresh();
            }
        });
        (this._launchOfflineMI = this._appMenu.add("")).setMnemonic(ResourceManager.getVKCode("jnlp.viewer.launch.offline.mi.mnemonic"));
        (this._launchOnlineMI = this._appMenu.add("")).setMnemonic(ResourceManager.getVKCode("jnlp.viewer.launch.online.mi.mnemonic"));
        this._appMenu.addSeparator();
        LocalInstallHandler.getInstance();
        if (CacheViewer._isLocalInstallSupported) {
            (this._installMI = this._appMenu.add("")).setMnemonic(ResourceManager.getVKCode("jnlp.viewer.install.mi.mnemonic"));
            this._installMI.addActionListener(new ActionListener() {
                public void actionPerformed(final ActionEvent actionEvent) {
                    CacheViewer.this.integrateApplication();
                }
            });
        }
        (this._showMI = this._appMenu.add("")).setMnemonic(ResourceManager.getVKCode("jnlp.viewer.show.mi.mnemonic"));
        (this._browseMI = this._appMenu.add("")).setMnemonic(ResourceManager.getVKCode("jnlp.viewer.browse.mi.mnemonic"));
        this._appMenu.addSeparator();
        (this._removeMI = this._appMenu.add("")).setMnemonic(ResourceManager.getVKCode("jnlp.viewer.remove.mi.mnemonic"));
        this._launchOfflineMI.addActionListener(new ActionListener() {
            public void actionPerformed(final ActionEvent actionEvent) {
                CacheViewer.this.launchApplication(false);
            }
        });
        this._launchOnlineMI.addActionListener(new ActionListener() {
            public void actionPerformed(final ActionEvent actionEvent) {
                CacheViewer.this.launchApplication(true);
            }
        });
        this._showMI.addActionListener(new ActionListener() {
            public void actionPerformed(final ActionEvent actionEvent) {
                CacheViewer.this.showApplication();
            }
        });
        this._browseMI.addActionListener(new ActionListener() {
            public void actionPerformed(final ActionEvent actionEvent) {
                CacheViewer.this.browseApplication();
            }
        });
        this._removeMI.addActionListener(new ActionListener() {
            public void actionPerformed(final ActionEvent actionEvent) {
                CacheViewer.this.removeApplications();
            }
        });
        (this._viewMenu = new JMenu(ResourceManager.getMessage("jnlp.viewer.view.menu"))).setMnemonic(ResourceManager.getVKCode("jnlp.viewer.view.menu.mnemonic"));
        for (int i = 0; i < 5; ++i) {
            final JMenuItem add4 = this._viewMenu.add(new JCheckBoxMenuItem(ResourceManager.getMessage("jnlp.viewer.view." + i + ".mi"), i == 0));
            add4.setMnemonic(ResourceManager.getVKCode("jnlp.viewer.view." + i + ".mi.mnemonic"));
            add4.addActionListener(new ActionListener() {
                public void actionPerformed(final ActionEvent actionEvent) {
                    SwingUtilities.invokeLater(new Runnable() {
                        private final /* synthetic */ Object val$source = actionEvent.getSource();
                        
                        public void run() {
                            for (int i = 0; i < 5; ++i) {
                                final JMenuItem item = CacheViewer.this._viewMenu.getItem(i);
                                if (item instanceof JCheckBoxMenuItem) {
                                    final JCheckBoxMenuItem checkBoxMenuItem = (JCheckBoxMenuItem)item;
                                    if (this.val$source.equals(checkBoxMenuItem)) {
                                        checkBoxMenuItem.setState(true);
                                        CacheViewer.this.setFilter(i);
                                    }
                                    else {
                                        checkBoxMenuItem.setState(false);
                                    }
                                }
                            }
                        }
                    });
                }
            });
        }
        (this._helpMenu = new JMenu(ResourceManager.getMessage("jnlp.viewer.help.menu"))).setMnemonic(ResourceManager.getVKCode("jnlp.viewer.help.menu.mnemonic"));
        final JMenuItem add5 = this._helpMenu.add(ResourceManager.getMessage("jnlp.viewer.help.java.mi"));
        add5.setMnemonic(ResourceManager.getVKCode("jnlp.viewer.help.java.mi.mnemonic"));
        add5.addActionListener(new ActionListener() {
            public void actionPerformed(final ActionEvent actionEvent) {
                final String property = Config.getProperty("deployment.home.j2se.url");
                try {
                    CacheViewer.this.showDocument(new URL(property));
                }
                catch (Exception ex) {
                    Trace.ignoredException(ex);
                }
            }
        });
        final JMenuItem add6 = this._helpMenu.add(ResourceManager.getMessage("jnlp.viewer.help.jnlp.mi"));
        add6.setMnemonic(ResourceManager.getVKCode("jnlp.viewer.help.jnlp.mi.mnemonic"));
        add6.addActionListener(new ActionListener() {
            public void actionPerformed(final ActionEvent actionEvent) {
                final String property = Config.getProperty("deployment.javaws.home.jnlp.url");
                try {
                    CacheViewer.this.showDocument(new URL(property));
                }
                catch (Exception ex) {
                    Trace.ignoredException(ex);
                }
            }
        });
        this._appMenu.addSeparator();
        final JMenuItem add7 = this._helpMenu.add(ResourceManager.getMessage("jnlp.viewer.about.mi"));
        add7.setMnemonic(ResourceManager.getVKCode("jnlp.viewer.about.mi.mnemonic"));
        add7.addActionListener(new ActionListener() {
            public void actionPerformed(final ActionEvent actionEvent) {
                CacheViewer.this.showAbout();
            }
        });
        final JMenuBar jMenuBar = new JMenuBar();
        jMenuBar.add(this._fileMenu);
        jMenuBar.add(this._editMenu);
        jMenuBar.add(this._appMenu);
        jMenuBar.add(this._viewMenu);
        jMenuBar.add(this._helpMenu);
        this.setJMenuBar(jMenuBar);
        this.resetSizes();
        this.refresh();
    }
    
    private void setFilter(final int filter) {
        String title;
        if (filter == 0) {
            title = ResourceManager.getMessage("jnlp.viewer.all");
        }
        else {
            title = new MessageFormat(ResourceManager.getMessage("jnlp.viewer.type")).format(new Object[] { ResourceManager.getMessage("jnlp.viewer.view." + filter) });
        }
        this._titledBorder.setTitle(title);
        this.getSelectedTable().setFilter(filter);
        this.getContentPane().repaint();
    }
    
    public JButton makeButton(final String s) {
        final JButton button = new JButton(ResourceManager.getMessage(s));
        button.setMnemonic(ResourceManager.getVKCode(s + ".mnemonic"));
        button.addActionListener(this);
        return button;
    }
    
    public void valueChanged(final ListSelectionEvent listSelectionEvent) {
        this.refresh();
    }
    
    public void stateChanged(final ChangeEvent changeEvent) {
        this.refresh();
        this.resetSizes();
    }
    
    private void resetSizes() {
        new Thread(new Runnable() {
            private final /* synthetic */ boolean val$system = !CacheViewer.this._tabbedPane.getSelectedComponent().equals(CacheViewer.this._userTab);
            
            public void run() {
                if (CacheViewer.getStatus() == 0) {
                    CacheViewer.setStatus(5);
                }
                try {
                    final long cacheSize = Cache.getCacheSize(this.val$system);
                    if (cacheSize > 0L) {
                        CacheViewer._totalSize.setText(CacheViewer.this.getAppMessage("jnlp.viewer.totalSize", tformat(cacheSize)));
                    }
                    else if (cacheSize < 0L) {
                        CacheViewer.this._tabbedPane.getTitleAt(CacheViewer.this._tabbedPane.getSelectedIndex());
                        CacheViewer._totalSize.setText(CacheViewer.this.getMessage("jnlp.viewer.noCache"));
                    }
                    else {
                        CacheViewer._totalSize.setText(CacheViewer.this.getAppMessage("jnlp.viewer.emptyCache", CacheViewer.this._tabbedPane.getTitleAt(CacheViewer.this._tabbedPane.getSelectedIndex())));
                    }
                }
                finally {
                    if (CacheViewer.getStatus() == 5) {
                        CacheViewer.setStatus(0);
                    }
                }
            }
        }).start();
    }
    
    private static String tformat(final long n) {
        if (n > 10240L) {
            return "" + n / 1024L + " KB";
        }
        return "" + n / 1024L + "." + n % 1024L / 102L + " KB";
    }
    
    public void refresh() {
        final boolean b = !this._tabbedPane.getSelectedComponent().equals(this._userTab);
        CacheTable cacheTable;
        if (b) {
            cacheTable = this._sysTable;
        }
        else {
            cacheTable = this._userTable;
        }
        final int[] selectedRows = cacheTable.getSelectedRows();
        boolean b2 = false;
        boolean b3 = false;
        boolean enabled = false;
        boolean enabled2 = false;
        boolean enabled3 = false;
        boolean locallyInstalled = false;
        final boolean b4 = !b && selectedRows.length > 0;
        String typeString = "";
        if (selectedRows.length == 1) {
            enabled2 = true;
            final CacheObject cacheObject = cacheTable.getCacheObject(selectedRows[0]);
            if (cacheObject != null) {
                final LaunchDesc launchDesc = cacheObject.getLaunchDesc();
                typeString = cacheObject.getTypeString();
                final InformationDesc information = launchDesc.getInformation();
                if (launchDesc.isApplication() || launchDesc.isApplet()) {
                    if (CacheViewer._isLocalInstallSupported) {
                        final LocalApplicationProperties localApplicationProperties = cacheObject.getLocalApplicationProperties();
                        locallyInstalled = localApplicationProperties.isLocallyInstalled();
                        enabled = (!b && !localApplicationProperties.isLocallyInstalledSystem());
                    }
                    if (information.supportsOfflineOperation()) {
                        b3 = true;
                    }
                    if (launchDesc.getLocation() != null) {
                        b2 = true;
                    }
                }
                if (information.getHome() != null) {
                    enabled3 = true;
                }
                this._removeBtn.setText(this.getAppMessage("jnlp.viewer.remove.1.btn", typeString));
            }
        }
        else if (selectedRows.length == 0) {
            this._removeBtn.setText(ResourceManager.getMessage("jnlp.viewer.remove.btn"));
        }
        else {
            this._removeBtn.setText(ResourceManager.getMessage("jnlp.viewer.remove.2.btn"));
        }
        this._launchOnlineBtn.setEnabled(b2);
        this._launchOnlineMI.setEnabled(b2);
        this._launchOnlineMI.setText(this.getMessage("jnlp.viewer.launch.online.mi"));
        this._launchOfflineBtn.setEnabled(b3);
        this._launchOfflineMI.setEnabled(b3);
        this._launchOfflineMI.setText(this.getMessage("jnlp.viewer.launch.offline.mi"));
        if (CacheViewer._isLocalInstallSupported) {
            this._installMI.setEnabled(enabled);
            if (locallyInstalled) {
                this._installMI.setText(this.getMessage("jnlp.viewer.uninstall.mi"));
                this._installMI.setMnemonic(ResourceManager.getVKCode("jnlp.viewer.uninstall.mi.mnemonic"));
            }
            else {
                this._installMI.setText(this.getMessage("jnlp.viewer.install.mi"));
                this._installMI.setMnemonic(ResourceManager.getVKCode("jnlp.viewer.install.mi.mnemonic"));
            }
        }
        this._showMI.setEnabled(enabled2);
        this._showMI.setText(this.getMessage("jnlp.viewer.show.mi"));
        this._browseMI.setEnabled(enabled3);
        this._browseMI.setText(this.getMessage("jnlp.viewer.browse.mi"));
        this._removeBtn.setEnabled(b4);
        this._removeMI.setEnabled(b4);
        if (selectedRows.length == 1) {
            this._removeMI.setText(this.getAppMessage("jnlp.viewer.remove.mi", typeString));
        }
        else {
            this._removeMI.setText(this.getMessage("jnlp.viewer.remove.0.mi"));
        }
    }
    
    private String getMessage(final String s) {
        return ResourceManager.getMessage(s);
    }
    
    private String getAppMessage(final String s, final String s2) {
        return new MessageFormat(ResourceManager.getMessage(s)).format(new Object[] { s2 });
    }
    
    private CacheObject getSelectedCacheObject() {
        CacheTable cacheTable;
        if (this._tabbedPane.getSelectedComponent().equals(this._userTab)) {
            cacheTable = this._userTable;
        }
        else {
            cacheTable = this._sysTable;
        }
        final int[] selectedRows = cacheTable.getSelectedRows();
        if (selectedRows.length == 1) {
            return cacheTable.getCacheObject(selectedRows[0]);
        }
        return null;
    }
    
    private void closeDialog(final WindowEvent windowEvent) {
        this.exitViewer();
    }
    
    private void exitViewer() {
        this._sil.removeSingleInstanceListener((DeploySIListener)this);
        this.setVisible(false);
        this.dispose();
        final Rectangle bounds = this.getBounds();
        Config.setProperty("deployment.javaws.viewer.bounds", "" + bounds.x + "," + bounds.y + "," + bounds.width + "," + bounds.height);
        Config.storeIfDirty();
        Main.systemExit(0);
    }
    
    public void actionPerformed(final ActionEvent actionEvent) {
        final JButton button = (JButton)actionEvent.getSource();
        if (button == this._removeBtn) {
            this.removeApplications();
        }
        else if (button == this._launchOnlineBtn) {
            this.launchApplication(true);
        }
        else if (button == this._launchOfflineBtn) {
            this.launchApplication(false);
        }
    }
    
    private CacheTable getSelectedTable() {
        return (this._tabbedPane.getSelectedComponent() == this._userTab) ? this._userTable : this._sysTable;
    }
    
    private void launchApplication(final boolean b) {
        if (getStatus() != 2) {
            if (getStatus() == 0) {
                setStatus(2);
            }
            try {
                final CacheObject selectedCacheObject = this.getSelectedCacheObject();
                if (selectedCacheObject != null) {
                    try {
                        Runtime.getRuntime().exec(new String[] { Config.getJavawsCommand(), b ? "-online" : "-offline", selectedCacheObject.getJnlpFile().getPath() });
                    }
                    catch (IOException ex) {
                        Trace.ignoredException((Exception)ex);
                    }
                }
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        CacheViewer.this.reset(CacheViewer.this._userTable);
                    }
                });
            }
            finally {
                if (getStatus() == 2) {
                    setStatus(0);
                }
            }
        }
    }
    
    public void launchApplication() {
        if (this._launchOnlineBtn.isEnabled()) {
            this.launchApplication(true);
        }
        else if (this._launchOfflineBtn.isEnabled()) {
            this.launchApplication(false);
        }
    }
    
    private void browseApplication() {
        final CacheObject selectedCacheObject = this.getSelectedCacheObject();
        if (selectedCacheObject != null) {
            final LaunchDesc launchDesc = selectedCacheObject.getLaunchDesc();
            if (launchDesc != null) {
                this.showDocument(launchDesc.getInformation().getHome());
            }
        }
    }
    
    private void showDocument(final URL url) {
        if (getStatus() != 3) {
            new Thread(new Runnable() {
                public void run() {
                    if (CacheViewer.getStatus() == 0) {
                        CacheViewer.setStatus(3);
                    }
                    try {
                        BrowserSupport.showDocument(url);
                    }
                    finally {
                        if (CacheViewer.getStatus() == 3) {
                            CacheViewer.setStatus(0);
                        }
                    }
                }
            }).start();
        }
    }
    
    private void showApplication() {
        final CacheObject selectedCacheObject = this.getSelectedCacheObject();
        if (selectedCacheObject != null) {
            final LaunchDesc launchDesc = selectedCacheObject.getLaunchDesc();
            final JTextArea textArea = new JTextArea(launchDesc.toString(), 24, 81);
            textArea.setEditable(false);
            DialogFactory.showMessageDialog((Component)this, 2, (Object)new JScrollPane(textArea, 20, 30), this.getAppMessage("jnlp.viewer.show.title", launchDesc.getInformation().getTitle()), false);
        }
    }
    
    private void showAbout() {
        new AboutDialog((JFrame)this, true).setVisible(true);
    }
    
    private void cleanCache() {
        if (getStatus() == 0) {
            new Thread(new Runnable() {
                public void run() {
                    CacheViewer.setStatus(1);
                    SwingUtilities.invokeLater(new Runnable() {
                        public void run() {
                            try {
                                Cache.clean();
                                CacheViewer.this.reset(CacheViewer.this._userTable);
                            }
                            finally {
                                if (CacheViewer.getStatus() == 1) {
                                    CacheViewer.setStatus(0);
                                }
                            }
                        }
                    });
                }
            }).start();
        }
    }
    
    private void removeApplications() {
        if (getStatus() == 0) {
            new Thread(new Runnable() {
                public void run() {
                    CacheViewer.setStatus(1);
                    final CacheTable cacheTable = CacheViewer.this._tabbedPane.getSelectedComponent().equals(CacheViewer.this._userTab) ? CacheViewer.this._userTable : CacheViewer.this._sysTable;
                    final int[] selectedRows = cacheTable.getSelectedRows();
                    for (int i = 0; i < selectedRows.length; ++i) {
                        final CacheObject cacheObject = cacheTable.getCacheObject(selectedRows[i]);
                        Cache.remove(cacheObject.getDCE(), cacheObject.getLocalApplicationProperties(), cacheObject.getLaunchDesc());
                    }
                    Cache.clean();
                    SwingUtilities.invokeLater(new Runnable() {
                        public void run() {
                            try {
                                CacheViewer.this.reset(CacheViewer.this._userTable);
                            }
                            finally {
                                if (CacheViewer.getStatus() == 1) {
                                    CacheViewer.setStatus(0);
                                }
                            }
                        }
                    });
                }
            }).start();
        }
    }
    
    public void popupApplicationMenu(final Component component, final int n, final int n2) {
        if (this.getSelectedCacheObject() != null) {
            final JPopupMenu popupMenu = new JPopupMenu();
            final Component[] menuComponents = this._appMenu.getMenuComponents();
            for (int i = 0; i < menuComponents.length; ++i) {
                if (menuComponents[i] instanceof JMenuItem) {
                    final JMenuItem menuItem = (JMenuItem)menuComponents[i];
                    final JMenuItem add = popupMenu.add(new JMenuItem(menuItem.getText(), menuItem.getMnemonic()));
                    add.setEnabled(menuItem.isEnabled());
                    final ActionListener[] actionListeners = menuItem.getActionListeners();
                    int j = 0;
                    while (j < actionListeners.length) {
                        add.addActionListener(actionListeners[j++]);
                    }
                }
                else {
                    popupMenu.addSeparator();
                }
            }
            popupMenu.show(component, n, n2);
        }
    }
    
    private void integrateApplication() {
        final CacheObject selectedCacheObject = this.getSelectedCacheObject();
        if (selectedCacheObject != null && CacheViewer._isLocalInstallSupported) {
            new Thread(new Installer(selectedCacheObject.getLaunchDesc(), selectedCacheObject.getLocalApplicationProperties(), this._tabbedPane.getSelectedComponent().equals(this._userTab) ? this._userTable : this._sysTable)).start();
        }
    }
    
    public void reset(final CacheTable cacheTable) {
        this.resetSizes();
        cacheTable.reset();
        this.refresh();
    }
    
    public static int getStatus() {
        return CacheViewer._status;
    }
    
    public static void setStatus(final int status) {
        String s = null;
        switch (CacheViewer._status = status) {
            case 1: {
                s = ResourceManager.getMessage("jnlp.viewer.removing");
                break;
            }
            case 2: {
                s = ResourceManager.getMessage("jnlp.viewer.launching");
                break;
            }
            case 3: {
                s = ResourceManager.getMessage("jnlp.viewer.browsing");
                break;
            }
            case 4: {
                s = ResourceManager.getMessage("jnlp.viewer.sorting");
                break;
            }
            case 5: {
                s = ResourceManager.getMessage("jnlp.viewer.searching");
                break;
            }
            case 6: {
                s = ResourceManager.getMessage("jnlp.viewer.installing");
                break;
            }
            default: {
                s = "";
                break;
            }
        }
        if (status == 0) {
            CacheViewer._statusLabel.setText(s);
            CacheViewer._totalSize.setVisible(true);
        }
        else {
            CacheViewer._totalSize.setVisible(false);
            CacheViewer._statusLabel.setText(s);
        }
    }
    
    private void showReInstallDialog() {
        final Properties removedApps = Cache.getRemovedApps();
        final String[] allHrefs = this._userTable.getAllHrefs();
        boolean b = false;
        for (int i = 0; i < allHrefs.length; ++i) {
            if (removedApps.getProperty(allHrefs[i]) != null) {
                removedApps.remove(allHrefs[i]);
                b = true;
            }
        }
        if (b) {
            Cache.setRemovedApps(removedApps);
        }
        final ArrayList<String> list = new ArrayList<String>();
        final ArrayList<String> list2 = new ArrayList<String>();
        final Enumeration<?> propertyNames = removedApps.propertyNames();
        while (propertyNames.hasMoreElements()) {
            final String s = (String)propertyNames.nextElement();
            list.add(s);
            list2.add(removedApps.getProperty(s));
        }
        final AbstractTableModel abstractTableModel = new AbstractTableModel() {
            private final /* synthetic */ String val$titleName = ResourceManager.getMessage("jnlp.viewer.reinstall.column.title");
            private final /* synthetic */ String val$hrefName = ResourceManager.getMessage("jnlp.viewer.reinstall.column.location");
            private final /* synthetic */ ArrayList val$titles = list2;
            private final /* synthetic */ ArrayList val$hrefs = list;
            
            public String getColumnName(final int n) {
                return (n == 0) ? this.val$titleName : this.val$hrefName;
            }
            
            public Object getValueAt(final int n, final int n2) {
                return (n2 == 0) ? this.val$titles.get(n) : this.val$hrefs.get(n);
            }
            
            public int getColumnCount() {
                return 2;
            }
            
            public int getRowCount() {
                return this.val$hrefs.size();
            }
            
            public Class getColumnClass(final int n) {
                return (CacheViewer.class$java$lang$String == null) ? (CacheViewer.class$java$lang$String = CacheViewer.class$("java.lang.String")) : CacheViewer.class$java$lang$String;
            }
        };
        final String s2 = "jnlp.viewer.reinstallBtn";
        final JButton button = new JButton(ResourceManager.getMessage(s2));
        button.setMnemonic(ResourceManager.getVKCode(s2 + ".mnemonic"));
        button.setEnabled(false);
        final String s3 = "jnlp.viewer.closeBtn";
        final JButton button2 = new JButton(ResourceManager.getMessage(s3));
        button2.setMnemonic(ResourceManager.getVKCode(s3 + ".mnemonic"));
        final Object[] array = { button, button2 };
        final JTable table = new JTable(abstractTableModel);
        button.addActionListener(new ActionListener() {
            private final /* synthetic */ ArrayList val$hrefs = list;
            
            public void actionPerformed(final ActionEvent actionEvent) {
                final int[] selectedRows = table.getSelectedRows();
                final String[] array = new String[selectedRows.length];
                for (int i = 0; i < array.length; ++i) {
                    array[i] = (String)this.val$hrefs.get(selectedRows[i]);
                }
                CacheViewer.this.do_reinstall(array);
            }
        });
        table.getColumnModel().getColumn(0).setPreferredWidth(200);
        table.getColumnModel().getColumn(1).setPreferredWidth(440);
        table.setPreferredScrollableViewportSize(new Dimension(640, 180));
        final JScrollPane scrollPane = new JScrollPane(table);
        table.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(final ListSelectionEvent listSelectionEvent) {
                button.setEnabled(table.getSelectedRowCount() > 0);
            }
        });
        DialogFactory.showOptionDialog((Component)this, 5, (Object)scrollPane, ResourceManager.getMessage("jnlp.viewer.reinstall.title"), array, (Object)button2);
    }
    
    public void do_reinstall(final String[] array) {
        new Thread(new Runnable() {
            public void run() {
                if (CacheViewer.getStatus() == 0) {
                    CacheViewer.setStatus(6);
                }
                try {
                    for (int i = 0; i < array.length; ++i) {
                        Main.importApp(array[i]);
                        int n = 0;
                        while (Main.getLaunchThreadGroup().activeCount() > 8) {
                            try {
                                Thread.sleep(2000L);
                            }
                            catch (Exception ex2) {}
                            if (++n > 5) {
                                break;
                            }
                        }
                        if (Main.getLaunchThreadGroup().activeCount() > 8) {
                            Trace.println("Warning: after waiting, still " + Main.getLaunchThreadGroup().activeCount() + " launching threads");
                        }
                    }
                }
                catch (Exception ex) {
                    Trace.ignoredException(ex);
                }
                finally {
                    for (int n2 = 10; n2 > 0 && Main.getLaunchThreadGroup().activeCount() > 0; --n2) {
                        try {
                            Thread.sleep(2000L);
                        }
                        catch (Exception ex3) {}
                    }
                    if (Main.getLaunchThreadGroup().activeCount() > 0) {
                        Trace.println("Warning: after waiting 20 sec., still " + Main.getLaunchThreadGroup().activeCount() + " launching threads");
                    }
                    if (CacheViewer.getStatus() == 6) {
                        CacheViewer.setStatus(0);
                    }
                }
            }
        }).start();
    }
    
    public void newActivation(final String[] array) {
        this._userTable.setFilter(0);
        this._sysTable.setFilter(0);
        this.setExtendedState(this.getExtendedState() & 0xFFFFFFFE);
        this.toFront();
    }
    
    public Object getSingleInstanceListener() {
        return this;
    }
    
    public static void main(final String[] array) {
        SplashScreen.hide();
        if (SingleInstanceManager.isServerRunning(CacheViewer.JAVAWS_CV_ID) && SingleInstanceManager.connectToServer("dummy")) {
            System.exit(0);
        }
        LookAndFeel setLookAndFeel = null;
        try {
            setLookAndFeel = DeployUIManager.setLookAndFeel();
            if (Config.getBooleanProperty("deployment.debug.console")) {
                JavawsConsoleController.showConsoleIfEnable();
            }
            final CacheViewer cacheViewer = new CacheViewer();
            final String property = Config.getProperty("deployment.javaws.viewer.bounds");
            if (property != null) {
                final StringTokenizer stringTokenizer = new StringTokenizer(property, ",");
                final int[] array2 = new int[4];
                int i;
                for (i = 0; i < 4; ++i) {
                    if (stringTokenizer.hasMoreTokens()) {
                        final String nextToken = stringTokenizer.nextToken();
                        try {
                            array2[i] = Integer.parseInt(nextToken);
                        }
                        catch (NumberFormatException ex) {}
                    }
                }
                if (i == 4) {
                    cacheViewer.setBounds(array2[0], array2[1], array2[2], array2[3]);
                }
            }
            cacheViewer.setVisible(true);
            long lastAccessed = Cache.getLastAccessed(false);
            long lastAccessed2 = Cache.getLastAccessed(true);
        Label_0225_Outer:
            while (true) {
                try {
                    Thread.sleep(2000L);
                }
                catch (InterruptedException ex2) {
                    DeployUIManager.restoreLookAndFeel(setLookAndFeel);
                    return;
                    // iftrue(Label_0256:, lastAccessed3 == lastAccessed2 || getStatus() != 0)
                    // iftrue(Label_0225:, lastAccessed4 == lastAccessed || getStatus() != 0)
                    long lastAccessed3 = 0L;
                Block_18:
                    while (true) {
                        break Block_18;
                        Label_0256: {
                            continue Label_0225_Outer;
                        }
                        final long lastAccessed4 = Cache.getLastAccessed(false);
                        lastAccessed3 = Cache.getLastAccessed(true);
                        lastAccessed = lastAccessed4;
                        SwingUtilities.invokeLater(new Runnable() {
                            public void run() {
                                cacheViewer.reset(cacheViewer._userTable);
                            }
                        });
                        continue;
                    }
                    lastAccessed2 = lastAccessed3;
                    SwingUtilities.invokeLater(new Runnable() {
                        public void run() {
                            cacheViewer.reset(cacheViewer._sysTable);
                        }
                    });
                }
                break;
            }
        }
        finally {}
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
        CacheViewer._status = 0;
        _totalSize = new JLabel();
        _lih = LocalInstallHandler.getInstance();
        _isLocalInstallSupported = CacheViewer._lih.isLocalInstallSupported();
        JAVAWS_CV_ID = "JNLP Cache Viewer" + Config.getInstance().getSessionSpecificString();
    }
    
    class Installer implements Runnable
    {
        private final LaunchDesc _ld;
        private final LocalApplicationProperties _lap;
        private final CacheTable _table;
        
        public Installer(final LaunchDesc ld, final LocalApplicationProperties lap, final CacheTable table) {
            this._ld = ld;
            this._lap = lap;
            this._table = table;
        }
        
        public void run() {
            this._lap.refreshIfNecessary();
            if (this._lap.isLocallyInstalled()) {
                CacheViewer._lih.uninstall(this._ld, this._lap, true);
            }
            else {
                CacheViewer._lih.doInstall(this._ld, this._lap);
            }
            this._lap.setAskedForInstall(true);
            try {
                this._lap.store();
            }
            catch (Exception ex) {
                Trace.ignoredException(ex);
            }
            CacheViewer.this.refresh();
        }
    }
}
