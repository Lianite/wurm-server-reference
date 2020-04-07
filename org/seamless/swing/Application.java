// 
// Decompiled by Procyon v0.5.30
// 

package org.seamless.swing;

import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.StringSelection;
import java.net.URL;
import javax.swing.ImageIcon;
import java.awt.Toolkit;
import java.awt.Window;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import java.awt.Dimension;
import javax.swing.JScrollPane;
import java.io.Writer;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.awt.Font;
import javax.swing.JTextArea;
import java.awt.Component;

public class Application
{
    public static void showError(final Component parent, final Throwable ex) {
        final JTextArea textArea = new JTextArea();
        textArea.setFont(new Font("Sans-Serif", 0, 10));
        textArea.setEditable(false);
        final StringWriter writer = new StringWriter();
        ex.printStackTrace(new PrintWriter(writer));
        textArea.setText(writer.toString());
        final JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(350, 150));
        JOptionPane.showMessageDialog(parent, scrollPane, "An Error Has Occurred", 0);
    }
    
    public static void showWarning(final Component parent, final String... warningLines) {
        final JTextArea textArea = new JTextArea();
        textArea.setFont(new Font("Sans-Serif", 0, 10));
        textArea.setEditable(false);
        for (final String s : warningLines) {
            textArea.append(s + "\n");
        }
        final JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(350, 150));
        JOptionPane.showMessageDialog(parent, scrollPane, "Warning", 0);
    }
    
    public static void showInfo(final Component parent, final String... infoLines) {
        final JTextArea textArea = new JTextArea();
        textArea.setFont(new Font("Sans-Serif", 0, 10));
        textArea.setEditable(false);
        for (final String s : infoLines) {
            textArea.append(s + "\n");
        }
        final JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(350, 150));
        JOptionPane.showMessageDialog(parent, scrollPane, "Info", 1);
    }
    
    public static void increaseFontSize(final JComponent l) {
        l.setFont(new Font(l.getFont().getFontName(), l.getFont().getStyle(), l.getFont().getSize() + 2));
    }
    
    public static void decreaseFontSize(final JComponent l) {
        l.setFont(new Font(l.getFont().getFontName(), l.getFont().getStyle(), l.getFont().getSize() - 2));
    }
    
    public static Window center(final Window w) {
        final Dimension us = w.getSize();
        final Dimension them = Toolkit.getDefaultToolkit().getScreenSize();
        int newX = (them.width - us.width) / 2;
        int newY = (them.height - us.height) / 2;
        if (newX < 0) {
            newX = 0;
        }
        if (newY < 0) {
            newY = 0;
        }
        w.setLocation(newX, newY);
        return w;
    }
    
    public static Window center(final Window w, final Window reference) {
        final double refCenterX = reference.getX() + reference.getSize().getWidth() / 2.0;
        final double refCenterY = reference.getY() + reference.getSize().getHeight() / 2.0;
        final int newX = (int)(refCenterX - w.getSize().getWidth() / 2.0);
        final int newY = (int)(refCenterY - w.getSize().getHeight() / 2.0);
        w.setLocation(newX, newY);
        return w;
    }
    
    public static Window maximize(final Window w) {
        final Dimension us = w.getSize();
        final Dimension them = Toolkit.getDefaultToolkit().getScreenSize();
        w.setBounds(0, 0, them.width, them.height);
        return w;
    }
    
    public static ImageIcon createImageIcon(final Class base, final String path, final String description) {
        final URL imgURL = base.getResource(path);
        if (imgURL != null) {
            return new ImageIcon(imgURL, description);
        }
        throw new RuntimeException("Couldn't find image icon on path: " + path);
    }
    
    public static ImageIcon createImageIcon(final Class base, final String path) {
        return createImageIcon(base, path, null);
    }
    
    public static void copyToClipboard(final String s) {
        final Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        final StringSelection data = new StringSelection(s);
        clipboard.setContents(data, data);
    }
}
