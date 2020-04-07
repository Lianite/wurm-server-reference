// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.javaws;

import com.sun.deploy.util.DialogFactory;
import java.security.GeneralSecurityException;
import javax.swing.LookAndFeel;
import com.sun.deploy.util.DeployUIManager;
import javax.swing.SwingUtilities;
import java.awt.Dimension;
import java.awt.Rectangle;
import com.sun.deploy.util.ConsoleWindow;
import javax.swing.text.Document;
import com.sun.javaws.jnl.LaunchDesc;
import java.awt.Toolkit;
import java.awt.event.WindowListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.Box;
import javax.swing.JButton;
import com.sun.javaws.util.JavawsConsoleController;
import java.io.Writer;
import java.io.PrintWriter;
import java.io.StringWriter;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import com.sun.deploy.resources.ResourceManager;
import javax.swing.BorderFactory;
import java.awt.Component;
import java.awt.LayoutManager;
import java.awt.BorderLayout;
import javax.swing.JTabbedPane;
import com.sun.javaws.exceptions.JNLPException;
import java.awt.Frame;
import javax.swing.JDialog;

public class LaunchErrorDialog extends JDialog
{
    private LaunchErrorDialog(final Frame frame, final Throwable t) {
        super(frame, true);
        JNLPException ex = null;
        if (t instanceof JNLPException) {
            ex = (JNLPException)t;
        }
        final JTabbedPane tabbedPane = new JTabbedPane();
        this.getContentPane().setLayout(new BorderLayout());
        this.getContentPane().add("Center", tabbedPane);
        tabbedPane.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
        final String errorCategory = getErrorCategory(t);
        this.setTitle(ResourceManager.getString("launcherrordialog.title", errorCategory));
        final String launchDescTitle = getLaunchDescTitle();
        final String launchDescVendor = getLaunchDescVendor();
        String s;
        if (Globals.isImportMode()) {
            s = ResourceManager.getString("launcherrordialog.import.errorintro");
        }
        else {
            s = ResourceManager.getString("launcherrordialog.errorintro");
        }
        if (launchDescTitle != null) {
            s += ResourceManager.getString("launcherrordialog.errortitle", launchDescTitle);
        }
        if (launchDescVendor != null) {
            s += ResourceManager.getString("launcherrordialog.errorvendor", launchDescVendor);
        }
        final String string = s + ResourceManager.getString("launcherrordialog.errorcategory", errorCategory) + getErrorDescription(t);
        final JTextArea textArea = new JTextArea();
        textArea.setFont(ResourceManager.getUIFont());
        textArea.setEditable(false);
        textArea.setLineWrap(true);
        textArea.setText(string);
        tabbedPane.add(ResourceManager.getString("launcherrordialog.generalTab"), new JScrollPane(textArea));
        String text = null;
        String source = null;
        if (ex != null) {
            text = ex.getLaunchDescSource();
            if (text == null) {
                final LaunchDesc defaultLaunchDesc = JNLPException.getDefaultLaunchDesc();
                if (defaultLaunchDesc != null) {
                    text = defaultLaunchDesc.getSource();
                }
            }
        }
        else if (JNLPException.getDefaultLaunchDesc() != null) {
            text = JNLPException.getDefaultLaunchDesc().getSource();
        }
        if (JNLPException.getDefaultLaunchDesc() != null) {
            source = JNLPException.getDefaultLaunchDesc().getSource();
        }
        if (source != null && source.equals(text)) {
            source = null;
        }
        if (text != null) {
            final JTextArea textArea2 = new JTextArea();
            textArea2.setFont(ResourceManager.getUIFont());
            textArea2.setEditable(false);
            textArea2.setLineWrap(true);
            textArea2.setText(text);
            tabbedPane.add(ResourceManager.getString("launcherrordialog.jnlpTab"), new JScrollPane(textArea2));
        }
        if (source != null) {
            final JTextArea textArea3 = new JTextArea();
            textArea3.setFont(ResourceManager.getUIFont());
            textArea3.setEditable(false);
            textArea3.setLineWrap(true);
            textArea3.setText(source);
            tabbedPane.add(ResourceManager.getString("launcherrordialog.jnlpMainTab"), new JScrollPane(textArea3));
        }
        if (t != null) {
            final JTextArea textArea4 = new JTextArea();
            textArea4.setFont(ResourceManager.getUIFont());
            textArea4.setEditable(false);
            textArea4.setLineWrap(true);
            textArea4.setWrapStyleWord(false);
            final StringWriter stringWriter = new StringWriter();
            t.printStackTrace(new PrintWriter(stringWriter));
            textArea4.setText(stringWriter.toString());
            tabbedPane.add(ResourceManager.getString("launcherrordialog.exceptionTab"), new JScrollPane(textArea4));
        }
        if (ex != null && ex.getWrappedException() != null) {
            final JTextArea textArea5 = new JTextArea();
            textArea5.setFont(ResourceManager.getUIFont());
            textArea5.setEditable(false);
            textArea5.setLineWrap(true);
            textArea5.setWrapStyleWord(false);
            final StringWriter stringWriter2 = new StringWriter();
            ex.getWrappedException().printStackTrace(new PrintWriter(stringWriter2));
            textArea5.setText(stringWriter2.toString());
            tabbedPane.add(ResourceManager.getString("launcherrordialog.wrappedExceptionTab"), new JScrollPane(textArea5));
        }
        Document document = null;
        final ConsoleWindow console = JavawsConsoleController.getInstance().getConsole();
        if (console != null) {
            document = console.getTextArea().getDocument();
        }
        if (document != null) {
            final JTextArea textArea6 = new JTextArea(document);
            textArea6.setFont(ResourceManager.getUIFont());
            tabbedPane.add(ResourceManager.getString("launcherrordialog.consoleTab"), new JScrollPane(textArea6));
        }
        final JButton defaultButton = new JButton(ResourceManager.getString("launcherrordialog.abort"));
        defaultButton.setMnemonic(ResourceManager.getVKCode("launcherrordialog.abortMnemonic"));
        final Box box = new Box(0);
        box.add(Box.createHorizontalGlue());
        box.add(defaultButton);
        box.add(Box.createHorizontalGlue());
        this.getContentPane().add("South", box);
        this.getRootPane().setDefaultButton(defaultButton);
        defaultButton.addActionListener(new ActionListener() {
            public void actionPerformed(final ActionEvent actionEvent) {
                LaunchErrorDialog.this.setVisible(false);
            }
        });
        this.addWindowListener(new WindowAdapter() {
            public void windowClosing(final WindowEvent windowEvent) {
                LaunchErrorDialog.this.setVisible(false);
            }
        });
        this.pack();
        this.setSize(450, 300);
        final Rectangle bounds = this.getBounds();
        final Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        bounds.width = Math.min(screenSize.width, bounds.width);
        bounds.height = Math.min(screenSize.height, bounds.height);
        this.setBounds((screenSize.width - bounds.width) / 2, (screenSize.height - bounds.height) / 2, bounds.width, bounds.height);
    }
    
    public static void show(final Frame frame, final Throwable t, final boolean b) {
        try {
            SwingUtilities.invokeAndWait(new Runnable() {
                public void run() {
                    showWarning(frame, t);
                }
            });
        }
        catch (Exception ex) {}
        if (b) {
            Main.systemExit(0);
        }
    }
    
    private static void showWarning(final Frame frame, final Throwable t) {
        LookAndFeel setLookAndFeel = null;
        try {
            setLookAndFeel = DeployUIManager.setLookAndFeel();
            SplashScreen.hide();
            System.err.println("#### Java Web Start Error:");
            System.err.println("#### " + t.getMessage());
            if (!Globals.TCKHarnessRun && (Globals.isSilentMode() || Main.isViewer()) && wantsDetails(frame, t)) {
                new LaunchErrorDialog(frame, t).setVisible(true);
            }
        }
        finally {
            DeployUIManager.restoreLookAndFeel(setLookAndFeel);
        }
    }
    
    private static String getErrorCategory(final Throwable t) {
        String s = ResourceManager.getString("launch.error.category.unexpected");
        if (t instanceof JNLPException) {
            s = ((JNLPException)t).getCategory();
        }
        else if (t instanceof SecurityException || t instanceof GeneralSecurityException) {
            s = ResourceManager.getString("launch.error.category.security");
        }
        else if (t instanceof OutOfMemoryError) {
            s = ResourceManager.getString("launch.error.category.memory");
        }
        return s;
    }
    
    private static String getErrorDescription(final Throwable t) {
        String s = t.getMessage();
        if (s == null) {
            s = ResourceManager.getString("launcherrordialog.genericerror", t.getClass().getName());
        }
        return s;
    }
    
    private static String getLaunchDescTitle() {
        final LaunchDesc defaultLaunchDesc = JNLPException.getDefaultLaunchDesc();
        return (defaultLaunchDesc == null) ? null : defaultLaunchDesc.getInformation().getTitle();
    }
    
    private static String getLaunchDescVendor() {
        final LaunchDesc defaultLaunchDesc = JNLPException.getDefaultLaunchDesc();
        return (defaultLaunchDesc == null) ? null : defaultLaunchDesc.getInformation().getVendor();
    }
    
    private static boolean wantsDetails(final Frame frame, final Throwable t) {
        String s = null;
        final String errorCategory = getErrorCategory(t);
        if (t instanceof JNLPException) {
            s = ((JNLPException)t).getBriefMessage();
        }
        if (s == null) {
            if (getLaunchDescTitle() == null) {
                if (Globals.isImportMode()) {
                    s = ResourceManager.getString("launcherrordialog.import.brief.message");
                }
                else {
                    s = ResourceManager.getString("launcherrordialog.brief.message");
                }
            }
            else if (Globals.isImportMode()) {
                s = ResourceManager.getString("launcherrordialog.import.brief.messageKnown", getLaunchDescTitle());
            }
            else {
                s = ResourceManager.getString("launcherrordialog.brief.messageKnown", getLaunchDescTitle());
            }
        }
        final String[] array = { ResourceManager.getString("launcherrordialog.brief.ok"), ResourceManager.getString("launcherrordialog.brief.details") };
        return DialogFactory.showOptionDialog((Component)frame, 1, (Object)s, ResourceManager.getString("launcherrordialog.brief.title", errorCategory), (Object[])array, (Object)array[0]) == 1;
    }
}
