// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.javaws.ui;

import com.sun.deploy.util.DialogFactory;
import javax.swing.JButton;
import com.sun.deploy.resources.ResourceManager;
import com.sun.javaws.jnl.LaunchDesc;
import java.awt.Component;

public class AutoDownloadPrompt
{
    public static int _result;
    
    public static boolean prompt(final Component component, final LaunchDesc launchDesc) {
        if (AutoDownloadPrompt._result >= 0) {
            return AutoDownloadPrompt._result == 0;
        }
        final String title = launchDesc.getInformation().getTitle();
        final String version = launchDesc.getResources().getSelectedJRE().getVersion();
        final String string = ResourceManager.getString("download.jre.prompt.title");
        final String[] array = { ResourceManager.getString("download.jre.prompt.text1", title, version), "", ResourceManager.getString("download.jre.prompt.text2") };
        final JButton[] array2 = { new JButton(ResourceManager.getString("download.jre.prompt.okButton")), new JButton(ResourceManager.getString("download.jre.prompt.cancelButton")) };
        array2[0].setMnemonic(ResourceManager.getAcceleratorKey("download.jre.prompt.okButton"));
        array2[1].setMnemonic(ResourceManager.getAcceleratorKey("download.jre.prompt.cancelButton"));
        AutoDownloadPrompt._result = DialogFactory.showOptionDialog(component, 4, (Object)array, string, (Object[])array2, (Object)array2[0]);
        return AutoDownloadPrompt._result == 0;
    }
    
    static {
        AutoDownloadPrompt._result = -1;
    }
}
