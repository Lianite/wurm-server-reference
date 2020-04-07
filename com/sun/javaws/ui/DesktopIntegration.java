// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.javaws.ui;

import javax.swing.LookAndFeel;
import com.sun.javaws.jnl.ShortcutDesc;
import com.sun.javaws.SplashScreen;
import com.sun.deploy.util.DialogFactory;
import com.sun.deploy.util.DeployUIManager;
import com.sun.javaws.jnl.LaunchDesc;
import com.sun.deploy.config.Config;
import java.awt.Dialog;
import javax.swing.JDialog;
import java.awt.Component;
import com.sun.javaws.Main;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import com.sun.deploy.resources.ResourceManager;
import javax.swing.JButton;
import java.awt.Frame;
import javax.swing.JOptionPane;

public class DesktopIntegration extends JOptionPane
{
    private int _answer;
    
    public DesktopIntegration(final Frame frame, final String s, final boolean b, final boolean b2) {
        this._answer = 2;
        this.initComponents(s, b, b2);
    }
    
    private void initComponents(final String s, final boolean b, final boolean b2) {
        final Object[] array = new Object[2];
        final JButton[] options = { new JButton(ResourceManager.getString("install.yesButton")), null, null };
        options[0].setMnemonic(ResourceManager.getVKCode("install.yesMnemonic"));
        final JButton button = options[0];
        (options[1] = new JButton(ResourceManager.getString("install.noButton"))).setMnemonic(ResourceManager.getVKCode("install.noMnemonic"));
        final JButton button2 = options[1];
        (options[2] = new JButton(ResourceManager.getString("install.configButton"))).setMnemonic(ResourceManager.getVKCode("install.configMnemonic"));
        final JButton button3 = options[2];
        for (int i = 0; i < 3; ++i) {
            options[i].addActionListener(new ActionListener() {
                public void actionPerformed(final ActionEvent actionEvent) {
                    final JButton button = (JButton)actionEvent.getSource();
                    if (button == button3) {
                        Main.launchJavaControlPanel("advanced");
                        return;
                    }
                    if (button == button) {
                        DesktopIntegration.this._answer = 1;
                    }
                    else if (button == button2) {
                        DesktopIntegration.this._answer = 0;
                    }
                    Component parent = (Component)actionEvent.getSource();
                    Dialog dialog = null;
                    while (parent.getParent() != null) {
                        if (parent instanceof JDialog) {
                            dialog = (JDialog)parent;
                        }
                        parent = parent.getParent();
                    }
                    if (dialog != null) {
                        dialog.setVisible(false);
                    }
                }
            });
        }
        Object message = null;
        if (Config.getOSName().equalsIgnoreCase("Windows")) {
            if (b && b2) {
                message = ResourceManager.getString("install.windows.both.message", s);
            }
            else if (b) {
                message = ResourceManager.getString("install.desktop.message", s);
            }
            else if (b2) {
                message = ResourceManager.getString("install.windows.menu.message", s);
            }
        }
        else if (b && b2) {
            message = ResourceManager.getString("install.gnome.both.message", s);
        }
        else if (b) {
            message = ResourceManager.getString("install.desktop.message", s);
        }
        else if (b2) {
            message = ResourceManager.getString("install.gnome.menu.message", s);
        }
        this.setOptions(options);
        this.setMessage(message);
        this.setMessageType(2);
        this.setInitialValue(options[0]);
    }
    
    public static int showDTIDialog(final Frame frame, final LaunchDesc launchDesc) {
        final String title = launchDesc.getInformation().getTitle();
        final ShortcutDesc shortcut = launchDesc.getInformation().getShortcut();
        final boolean b = shortcut == null || shortcut.getDesktop();
        final boolean b2 = shortcut == null || shortcut.getMenu();
        final LookAndFeel setLookAndFeel = DeployUIManager.setLookAndFeel();
        final DesktopIntegration desktopIntegration = new DesktopIntegration(frame, title, b, b2);
        final JDialog dialog = desktopIntegration.createDialog(frame, ResourceManager.getString("install.title", title));
        DialogFactory.positionDialog((Dialog)dialog);
        SplashScreen.hide();
        dialog.setVisible(true);
        DeployUIManager.restoreLookAndFeel(setLookAndFeel);
        return desktopIntegration._answer;
    }
}
