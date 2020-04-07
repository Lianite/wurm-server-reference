// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.jnlp;

import java.awt.Component;
import javax.swing.JScrollPane;
import com.sun.deploy.resources.ResourceManager;
import javax.swing.JTextArea;
import java.security.AccessController;
import java.io.IOException;
import java.security.PrivilegedAction;
import javax.jnlp.FileContents;
import java.io.File;
import javax.jnlp.ExtendedService;

public final class ExtendedServiceImpl implements ExtendedService
{
    private static ExtendedServiceImpl _sharedInstance;
    private static int DEFAULT_FILESIZE;
    
    public static synchronized ExtendedServiceImpl getInstance() {
        if (ExtendedServiceImpl._sharedInstance == null) {
            ExtendedServiceImpl._sharedInstance = new ExtendedServiceImpl();
        }
        return ExtendedServiceImpl._sharedInstance;
    }
    
    public FileContents openFile(final File file) throws IOException {
        if (!this.askUser(file.getPath())) {
            return null;
        }
        final FileContents doPrivileged = AccessController.doPrivileged((PrivilegedAction<FileContents>)new PrivilegedAction() {
            public Object run() {
                try {
                    return new FileContentsImpl(file, ExtendedServiceImpl.DEFAULT_FILESIZE);
                }
                catch (IOException ex) {
                    return ex;
                }
            }
        });
        if (doPrivileged instanceof IOException) {
            throw (IOException)doPrivileged;
        }
        return doPrivileged;
    }
    
    synchronized boolean askUser(final String s) {
        final SmartSecurityDialog smartSecurityDialog = new SmartSecurityDialog();
        final JTextArea textArea = new JTextArea(4, 30);
        textArea.setFont(ResourceManager.getUIFont());
        textArea.setEditable(false);
        textArea.append(s);
        return smartSecurityDialog.showDialog(new Object[] { ResourceManager.getString("APIImpl.extended.fileOpen.message1"), new JScrollPane(textArea), ResourceManager.getString("APIImpl.extended.fileOpen.message2") });
    }
    
    public FileContents[] openFiles(final File[] array) throws IOException {
        if (array == null || array.length <= 0) {
            return null;
        }
        String string = "";
        for (int i = 0; i < array.length; ++i) {
            string = string + array[i].getPath() + "\n";
        }
        if (!this.askUser(string)) {
            return null;
        }
        final FileContents[] array2 = AccessController.doPrivileged((PrivilegedAction<FileContents[]>)new PrivilegedAction() {
            public Object run() {
                final FileContents[] array = new FileContents[array.length];
                try {
                    for (int i = 0; i < array.length; ++i) {
                        array[i] = new FileContentsImpl(array[i], ExtendedServiceImpl.DEFAULT_FILESIZE);
                    }
                }
                catch (IOException ex) {
                    array[0] = (FileContents)ex;
                }
                return array;
            }
        });
        if (array2[0] instanceof IOException) {
            throw (IOException)array2[0];
        }
        return array2;
    }
    
    static {
        ExtendedServiceImpl._sharedInstance = null;
        ExtendedServiceImpl.DEFAULT_FILESIZE = Integer.MAX_VALUE;
    }
}
