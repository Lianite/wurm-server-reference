// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.jnlp;

import java.util.Iterator;
import java.io.InputStream;
import java.security.AccessController;
import java.awt.Image;
import java.applet.AudioClip;
import java.util.Vector;
import java.util.Enumeration;
import javax.swing.SwingUtilities;
import javax.swing.JFrame;
import com.sun.javaws.Main;
import java.awt.Insets;
import java.awt.Frame;
import java.awt.Dimension;
import java.awt.Component;
import java.awt.LayoutManager;
import java.awt.BorderLayout;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;
import java.applet.AppletStub;
import java.applet.AppletContext;
import java.security.PrivilegedAction;
import javax.swing.JLabel;
import java.util.Properties;
import java.net.URL;
import java.applet.Applet;
import javax.swing.JPanel;

public final class AppletContainer extends JPanel
{
    final AppletContainerCallback callback;
    final Applet applet;
    final String appletName;
    final URL documentBase;
    final URL codeBase;
    final Properties parameters;
    final boolean[] isActive;
    int appletWidth;
    int appletHeight;
    final JLabel statusLabel;
    static PrivilegedAction loadImageActionDummy;
    
    public AppletContainer(final AppletContainerCallback callback, final Applet applet, final String appletName, final URL documentBase, final URL codeBase, final int appletWidth, final int appletHeight, final Properties parameters) {
        this.isActive = new boolean[] { false };
        this.statusLabel = new JLabel("");
        this.callback = callback;
        this.applet = applet;
        this.appletName = appletName;
        this.documentBase = documentBase;
        this.codeBase = codeBase;
        this.parameters = parameters;
        this.isActive[0] = false;
        this.appletWidth = appletWidth;
        this.appletHeight = appletHeight;
        applet.setStub(new AppletContainerStub(new AppletContainerContext()));
        this.statusLabel.setBorder(new EtchedBorder());
        this.statusLabel.setText("Loading...");
        this.setLayout(new BorderLayout());
        this.add("Center", applet);
        this.add("South", this.statusLabel);
        this.setPreferredSize(new Dimension(this.appletWidth, this.appletHeight + (int)this.statusLabel.getPreferredSize().getHeight()));
    }
    
    public Applet getApplet() {
        return this.applet;
    }
    
    public void setStatus(final String text) {
        this.statusLabel.setText(text);
    }
    
    public void resizeApplet(final int appletWidth, final int appletHeight) {
        if (appletWidth < 0 || appletHeight < 0) {
            return;
        }
        final int n = appletWidth - this.appletWidth;
        final int n2 = appletHeight - this.appletHeight;
        final Dimension size = this.getSize();
        this.setSize(new Dimension((int)size.getWidth() + n, (int)size.getHeight() + n2));
        this.callback.relativeResize(new Dimension(n, n2));
        this.appletWidth = appletWidth;
        this.appletHeight = appletHeight;
    }
    
    public Dimension getPreferredFrameSize(final Frame frame) {
        final Insets insets = frame.getInsets();
        return new Dimension(this.appletWidth + (insets.left + insets.right), this.appletHeight + this.statusLabel.getHeight() + (insets.top + insets.bottom));
    }
    
    public void startApplet() {
        ImageCache.initialize();
        new AppletAudioClip();
        new Thread() {
            public void run() {
                try {
                    AppletContainer.this.setStatus("Initializing Applet");
                    AppletContainer.this.applet.init();
                    try {
                        AppletContainer.this.isActive[0] = true;
                        AppletContainer.this.applet.start();
                        AppletContainer.this.setStatus("Applet running...");
                    }
                    catch (Throwable t) {
                        AppletContainer.this.setStatus("Failed to start Applet: " + t.toString());
                        t.printStackTrace(System.out);
                        AppletContainer.this.isActive[0] = false;
                    }
                }
                catch (Throwable t2) {
                    AppletContainer.this.setStatus("Failed to initialize: " + t2.toString());
                    t2.printStackTrace(System.out);
                }
            }
        }.start();
    }
    
    public void stopApplet() {
        this.applet.stop();
        this.applet.destroy();
        Main.systemExit(0);
    }
    
    static void showApplet(final AppletContainerCallback appletContainerCallback, final Applet applet, final String s, final URL url, final URL url2, final int n, final int n2, final Properties properties) {
        final JFrame frame = new JFrame("Applet Window");
        final AppletContainer appletContainer = new AppletContainer(appletContainerCallback, applet, s, url, url2, n, n2, properties);
        frame.getContentPane().setLayout(new BorderLayout());
        frame.getContentPane().add("Center", appletContainer);
        frame.pack();
        frame.show();
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                try {
                    appletContainer.setStatus("Initializing Applet");
                    applet.init();
                    applet.start();
                    appletContainer.setStatus("Applet Running");
                }
                catch (Throwable t) {
                    appletContainer.setStatus("Failed to start Applet");
                }
            }
        });
    }
    
    static {
        AppletContainer.loadImageActionDummy = new LoadImageAction(null);
    }
    
    static class LoadImageAction implements PrivilegedAction
    {
        URL _url;
        
        public LoadImageAction(final URL url) {
            this._url = url;
        }
        
        public Object run() {
            return ImageCache.getImage(this._url);
        }
    }
    
    class AppletContainerContext implements AppletContext
    {
        public Applet getApplet(final String s) {
            return s.equals(AppletContainer.this.appletName) ? AppletContainer.this.applet : null;
        }
        
        public Enumeration getApplets() {
            final Vector<Applet> vector = new Vector<Applet>();
            vector.add(AppletContainer.this.applet);
            return vector.elements();
        }
        
        public AudioClip getAudioClip(final URL url) {
            return AppletAudioClip.get(url);
        }
        
        public Image getImage(final URL url) {
            return AccessController.doPrivileged((PrivilegedAction<Image>)new LoadImageAction(url));
        }
        
        public void showDocument(final URL url) {
            AccessController.doPrivileged((PrivilegedAction<Object>)new PrivilegedAction() {
                public Object run() {
                    new Thread() {
                        public void run() {
                            AppletContainer.this.callback.showDocument(url);
                        }
                    }.start();
                    return null;
                }
            });
        }
        
        public void showDocument(final URL url, final String s) {
            this.showDocument(url);
        }
        
        public void showStatus(final String text) {
            AppletContainer.this.statusLabel.setText(text);
        }
        
        public void setStream(final String s, final InputStream inputStream) {
        }
        
        public InputStream getStream(final String s) {
            return null;
        }
        
        public Iterator getStreamKeys() {
            return null;
        }
    }
    
    final class AppletContainerStub implements AppletStub
    {
        AppletContext context;
        
        AppletContainerStub(final AppletContext context) {
            this.context = context;
        }
        
        public void appletResize(final int n, final int n2) {
            AppletContainer.this.resizeApplet(n, n2);
        }
        
        public AppletContext getAppletContext() {
            return this.context;
        }
        
        public URL getCodeBase() {
            return AppletContainer.this.codeBase;
        }
        
        public URL getDocumentBase() {
            return AppletContainer.this.documentBase;
        }
        
        public String getParameter(final String s) {
            return AppletContainer.this.parameters.getProperty(s);
        }
        
        public boolean isActive() {
            return AppletContainer.this.isActive[0];
        }
    }
}
