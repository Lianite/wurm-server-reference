// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.jnlp;

import javax.swing.JDialog;
import java.awt.event.ItemEvent;
import com.sun.javaws.Main;
import java.awt.Font;
import javax.swing.LookAndFeel;
import com.sun.deploy.util.DialogFactory;
import java.awt.Component;
import java.awt.event.ItemListener;
import javax.swing.JCheckBox;
import com.sun.deploy.resources.ResourceManager;
import javax.swing.JPanel;
import com.sun.deploy.util.DeployUIManager;
import java.awt.Rectangle;
import java.awt.Point;
import java.awt.event.WindowListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowAdapter;
import java.awt.Toolkit;
import com.sun.deploy.config.Config;
import java.security.AccessController;
import java.awt.Frame;
import java.security.PrivilegedAction;
import java.awt.EventQueue;

final class SmartSecurityDialog extends Thread
{
    private boolean _remembered;
    private int _lastResult;
    private boolean _cbChecked;
    private int _answer;
    private Object _signalObject;
    private String _message;
    private DummyDialog _dummyDialog;
    private EventQueue _sysEventQueue;
    private static final ThreadGroup _secureGroup;
    private Object[] _objs;
    
    SmartSecurityDialog() {
        this((String)null);
    }
    
    SmartSecurityDialog(final String message) {
        this._remembered = false;
        this._lastResult = -1;
        this._cbChecked = false;
        this._signalObject = null;
        this._message = null;
        this._sysEventQueue = null;
        this._objs = null;
        this._signalObject = new Object();
        this._message = message;
    }
    
    SmartSecurityDialog(final String s, final boolean cbChecked) {
        this(s);
        this._cbChecked = cbChecked;
    }
    
    boolean showDialog(final Object[] objs) {
        this._objs = objs;
        return this.showDialog();
    }
    
    boolean showDialog(final String message) {
        this._message = message;
        this._objs = null;
        return this.showDialog();
    }
    
    boolean showDialog() {
        return AccessController.doPrivileged((PrivilegedAction<Integer>)new PrivilegedAction() {
            public Object run() {
                return new Integer(SmartSecurityDialog.this.getUserDecision(null, SmartSecurityDialog.this._message));
            }
        }) == 0;
    }
    
    private int getUserDecision(final Frame frame, final String message) {
        if (this._remembered) {
            return this._answer = this._lastResult;
        }
        if (!Config.getBooleanProperty("deployment.security.sandbox.jnlp.enhanced")) {
            return 1;
        }
        synchronized (this._signalObject) {
            this._sysEventQueue = Toolkit.getDefaultToolkit().getSystemEventQueue();
            final Thread thread = new Thread(SmartSecurityDialog._secureGroup, this, "userDialog");
            this._message = message;
            (this._dummyDialog = new DummyDialog(null, true)).addWindowListener(new WindowAdapter() {
                public void windowOpened(final WindowEvent windowEvent) {
                    thread.start();
                }
                
                public void windowClosing(final WindowEvent windowEvent) {
                    SmartSecurityDialog.this._dummyDialog.hide();
                }
            });
            final Rectangle rectangle = new Rectangle(new Point(0, 0), Toolkit.getDefaultToolkit().getScreenSize());
            this._dummyDialog.setLocation(rectangle.x + rectangle.width / 2 - 50, rectangle.y + rectangle.height / 2);
            if (Config.getOSName().equals("Windows")) {
                this._dummyDialog.setLocation(-200, -200);
            }
            this._dummyDialog.setResizable(false);
            this._dummyDialog.toBack();
            this._dummyDialog.show();
            try {
                this._signalObject.wait();
            }
            catch (Exception ex) {
                ex.printStackTrace();
                this._dummyDialog.hide();
            }
            return this._answer;
        }
    }
    
    public void run() {
        final LookAndFeel setLookAndFeel = DeployUIManager.setLookAndFeel();
        try {
            final JPanel panel = new JPanel();
            final JCheckBox checkBox = new JCheckBox(ResourceManager.getString("APIImpl.securityDialog.remember"), this._cbChecked);
            final Font font = checkBox.getFont();
            if (font != null) {
                final Font deriveFont = font.deriveFont(0);
                if (deriveFont != null) {
                    checkBox.setFont(deriveFont);
                }
            }
            checkBox.addItemListener(new csiCheckBoxListener(checkBox));
            panel.add(checkBox, "Center");
            checkBox.setOpaque(false);
            panel.setOpaque(false);
            Object[] objs;
            if (this._objs == null) {
                objs = new Object[] { this._message, panel };
            }
            else {
                objs = this._objs;
            }
            final Object[] array = { ResourceManager.getString("APIImpl.securityDialog.yes"), ResourceManager.getString("APIImpl.securityDialog.no") };
            boolean b = true;
            try {
                b = (DialogFactory.showOptionDialog(4, (Object)objs, ResourceManager.getString("APIImpl.securityDialog.title"), array, array[0]) != 0);
                if (this._cbChecked) {
                    this._remembered = true;
                    this._lastResult = (b ? 1 : 0);
                }
            }
            finally {
                this._dummyDialog.secureHide();
                synchronized (this._signalObject) {
                    this._answer = (b ? 1 : 0);
                    this._signalObject.notify();
                }
            }
        }
        finally {
            DeployUIManager.restoreLookAndFeel(setLookAndFeel);
        }
    }
    
    private void setCBChecked(final boolean cbChecked) {
        this._cbChecked = cbChecked;
    }
    
    static {
        _secureGroup = Main.getSecurityThreadGroup();
    }
    
    class csiCheckBoxListener implements ItemListener
    {
        private final /* synthetic */ JCheckBox val$cb;
        
        csiCheckBoxListener(final JCheckBox val$cb) {
            this.val$cb = val$cb;
        }
        
        public void itemStateChanged(final ItemEvent itemEvent) {
            if (itemEvent.getItemSelectable() == this.val$cb) {
                if (itemEvent.getStateChange() == 2) {
                    SmartSecurityDialog.this.setCBChecked(false);
                }
                else if (itemEvent.getStateChange() == 1) {
                    SmartSecurityDialog.this.setCBChecked(true);
                }
            }
        }
    }
    
    private class DummyDialog extends JDialog
    {
        private ThreadGroup _unsecureGroup;
        
        DummyDialog(final Frame frame, final boolean b) {
            super(frame, b);
            this._unsecureGroup = Thread.currentThread().getThreadGroup();
        }
        
        public void secureHide() {
            new Thread(this._unsecureGroup, new Runnable() {
                public void run() {
                    DummyDialog.this.hide();
                }
            }).start();
        }
    }
}
