// 
// Decompiled by Procyon v0.5.30
// 

package org.fourthline.cling.support.shared;

import java.awt.Window;
import org.seamless.swing.Application;
import java.awt.Dimension;
import java.awt.Component;
import javax.swing.JLabel;
import javax.swing.JWindow;
import org.fourthline.cling.UpnpService;
import java.util.logging.Level;
import java.util.logging.LogManager;
import org.seamless.util.logging.LoggingUtil;
import java.util.logging.Handler;
import org.seamless.swing.logging.LoggingHandler;
import javax.swing.BorderFactory;
import java.awt.Frame;
import org.seamless.swing.Event;
import org.seamless.swing.logging.LogMessage;
import org.seamless.swing.Controller;
import javax.swing.UIManager;
import org.seamless.swing.logging.LogCategory;
import java.util.List;
import javax.swing.JPanel;
import org.seamless.swing.logging.LogController;
import javax.swing.JFrame;
import org.seamless.swing.AbstractController;

public abstract class MainController extends AbstractController<JFrame>
{
    private final LogController logController;
    private final JPanel logPanel;
    
    public MainController(final JFrame view, final List<LogCategory> logCategories) {
        super(view);
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        }
        catch (Exception ex) {
            System.out.println("Unable to load native look and feel: " + ex.toString());
        }
        System.setProperty("sun.awt.exception.handler", AWTExceptionHandler.class.getName());
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                if (MainController.this.getUpnpService() != null) {
                    MainController.this.getUpnpService().shutdown();
                }
            }
        });
        this.logController = new LogController(this, logCategories) {
            @Override
            protected void expand(final LogMessage logMessage) {
                this.fireEventGlobal(new TextExpandEvent(logMessage.getMessage()));
            }
            
            @Override
            protected Frame getParentWindow() {
                return ((AbstractController<Frame>)MainController.this).getView();
            }
        };
        (this.logPanel = this.logController.getView()).setBorder(BorderFactory.createEmptyBorder(5, 0, 0, 0));
        final Handler handler = new LoggingHandler() {
            @Override
            protected void log(final LogMessage msg) {
                MainController.this.logController.pushMessage(msg);
            }
        };
        if (System.getProperty("java.util.logging.config.file") == null) {
            LoggingUtil.resetRootHandler(handler);
        }
        else {
            LogManager.getLogManager().getLogger("").addHandler(handler);
        }
    }
    
    public LogController getLogController() {
        return this.logController;
    }
    
    public JPanel getLogPanel() {
        return this.logPanel;
    }
    
    public void log(final Level level, final String msg) {
        this.log(new LogMessage(level, msg));
    }
    
    public void log(final LogMessage message) {
        this.getLogController().pushMessage(message);
    }
    
    @Override
    public void dispose() {
        super.dispose();
        ShutdownWindow.INSTANCE.setVisible(true);
        new Thread() {
            @Override
            public void run() {
                System.exit(0);
            }
        }.start();
    }
    
    public abstract UpnpService getUpnpService();
    
    public static class ShutdownWindow extends JWindow
    {
        public static final JWindow INSTANCE;
        
        protected ShutdownWindow() {
            final JLabel shutdownLabel = new JLabel("Shutting down, please wait...");
            shutdownLabel.setHorizontalAlignment(0);
            this.getContentPane().add(shutdownLabel);
            this.setPreferredSize(new Dimension(300, 30));
            this.pack();
            Application.center(this);
        }
        
        static {
            INSTANCE = new ShutdownWindow();
        }
    }
}
