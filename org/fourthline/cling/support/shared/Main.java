// 
// Decompiled by Procyon v0.5.30
// 

package org.fourthline.cling.support.shared;

import java.io.Writer;
import java.awt.Window;
import org.seamless.swing.Application;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import java.awt.Component;
import javax.swing.JScrollPane;
import java.io.PrintWriter;
import java.io.StringWriter;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import java.util.logging.LogManager;
import org.seamless.util.logging.LoggingUtil;
import java.util.logging.Handler;
import java.awt.event.WindowListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowAdapter;
import java.awt.Dimension;
import javax.swing.UIManager;
import org.seamless.util.OS;
import org.seamless.swing.logging.LogMessage;
import org.seamless.swing.logging.LoggingHandler;
import javax.swing.JFrame;
import javax.inject.Inject;
import org.fourthline.cling.support.shared.log.LogView;

public abstract class Main implements ShutdownHandler, Thread.UncaughtExceptionHandler
{
    @Inject
    LogView.Presenter logPresenter;
    protected final JFrame errorWindow;
    protected final LoggingHandler loggingHandler;
    protected boolean isRegularShutdown;
    
    public Main() {
        this.errorWindow = new JFrame();
        this.loggingHandler = new LoggingHandler() {
            @Override
            protected void log(final LogMessage msg) {
                Main.this.logPresenter.pushMessage(msg);
            }
        };
    }
    
    public void init() {
        try {
            if (OS.checkForMac()) {
                NewPlatformApple.setup(this, this.getAppName());
            }
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        }
        catch (Exception ex) {}
        this.errorWindow.setPreferredSize(new Dimension(900, 400));
        this.errorWindow.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(final WindowEvent windowEvent) {
                Main.this.errorWindow.dispose();
            }
        });
        Thread.setDefaultUncaughtExceptionHandler(this);
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                if (!Main.this.isRegularShutdown) {
                    Main.this.shutdown();
                }
            }
        });
        if (System.getProperty("java.util.logging.config.file") == null) {
            LoggingUtil.resetRootHandler(this.loggingHandler);
        }
        else {
            LogManager.getLogManager().getLogger("").addHandler(this.loggingHandler);
        }
    }
    
    @Override
    public void shutdown() {
        this.isRegularShutdown = true;
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                Main.this.errorWindow.dispose();
            }
        });
    }
    
    @Override
    public void uncaughtException(final Thread thread, final Throwable throwable) {
        System.err.println("In thread '" + thread + "' uncaught exception: " + throwable);
        throwable.printStackTrace(System.err);
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                Main.this.errorWindow.getContentPane().removeAll();
                final JTextArea textArea = new JTextArea();
                textArea.setEditable(false);
                final StringBuilder text = new StringBuilder();
                text.append("An exceptional error occurred!\nYou can try to continue or exit the application.\n\n");
                text.append("Please tell us about this here:\nhttp://www.4thline.org/projects/mailinglists-cling.html\n\n");
                text.append("-------------------------------------------------------------------------------------------------------------\n\n");
                final Writer stackTrace = new StringWriter();
                throwable.printStackTrace(new PrintWriter(stackTrace));
                text.append(stackTrace.toString());
                textArea.setText(text.toString());
                final JScrollPane pane = new JScrollPane(textArea);
                Main.this.errorWindow.getContentPane().add(pane, "Center");
                final JButton exitButton = new JButton("Exit Application");
                exitButton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(final ActionEvent e) {
                        System.exit(1);
                    }
                });
                Main.this.errorWindow.getContentPane().add(exitButton, "South");
                Main.this.errorWindow.pack();
                Application.center(Main.this.errorWindow);
                textArea.setCaretPosition(0);
                Main.this.errorWindow.setVisible(true);
            }
        });
    }
    
    protected void removeLoggingHandler() {
        LogManager.getLogManager().getLogger("").removeHandler(this.loggingHandler);
    }
    
    protected abstract String getAppName();
}
