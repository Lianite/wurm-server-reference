// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.jnlp;

import com.sun.deploy.util.DialogFactory;
import java.security.AccessController;
import java.io.File;
import javax.swing.filechooser.FileSystemView;
import java.io.BufferedInputStream;
import java.io.OutputStream;
import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.awt.Component;
import javax.swing.JFileChooser;
import java.security.PrivilegedAction;
import java.io.InputStream;
import java.io.IOException;
import javax.jnlp.FileContents;
import com.sun.deploy.resources.ResourceManager;
import javax.jnlp.FileSaveService;

public final class FileSaveServiceImpl implements FileSaveService
{
    static FileSaveServiceImpl _sharedInstance;
    private SmartSecurityDialog _securityDialog;
    private String _lastPath;
    
    private FileSaveServiceImpl() {
        this._securityDialog = null;
        this._securityDialog = new SmartSecurityDialog(ResourceManager.getString("APIImpl.file.save.message"));
    }
    
    public static synchronized FileSaveService getInstance() {
        if (FileSaveServiceImpl._sharedInstance == null) {
            FileSaveServiceImpl._sharedInstance = new FileSaveServiceImpl();
        }
        return FileSaveServiceImpl._sharedInstance;
    }
    
    String getLastPath() {
        return this._lastPath;
    }
    
    void setLastPath(final String lastPath) {
        this._lastPath = lastPath;
    }
    
    public FileContents saveAsFileDialog(final String s, final String[] array, final FileContents fileContents) throws IOException {
        return this.saveFileDialog(s, array, fileContents.getInputStream(), fileContents.getName());
    }
    
    public FileContents saveFileDialog(final String s, final String[] array, final InputStream inputStream, final String s2) throws IOException {
        try {
            if (!this.askUser()) {
                return null;
            }
            final FileContents doPrivileged = AccessController.doPrivileged((PrivilegedAction<FileContents>)new PrivilegedAction() {
                public Object run() {
                    final FileSystemView fileSystemView = FileOpenServiceImpl.getFileSystemView();
                    JFileChooser fileChooser;
                    if (s != null) {
                        fileChooser = new JFileChooser(s, fileSystemView);
                    }
                    else {
                        fileChooser = new JFileChooser(FileSaveServiceImpl.this.getLastPath(), fileSystemView);
                    }
                    fileChooser.setFileSelectionMode(0);
                    fileChooser.setDialogType(1);
                    fileChooser.setMultiSelectionEnabled(false);
                    if (fileChooser.showSaveDialog(null) == 1) {
                        return null;
                    }
                    final File selectedFile = fileChooser.getSelectedFile();
                    if (selectedFile != null) {
                        if (!FileSaveServiceImpl.fileChk(selectedFile)) {
                            return null;
                        }
                        try {
                            final byte[] array = new byte[8192];
                            final BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(new FileOutputStream(selectedFile));
                            final BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream);
                            for (int i = bufferedInputStream.read(array); i != -1; i = bufferedInputStream.read(array)) {
                                bufferedOutputStream.write(array, 0, i);
                            }
                            bufferedOutputStream.close();
                            FileSaveServiceImpl.this.setLastPath(selectedFile.getPath());
                            return new FileContentsImpl(selectedFile, FileSaveServiceImpl.computeMaxLength(selectedFile.length()));
                        }
                        catch (IOException ex) {
                            return ex;
                        }
                    }
                    return null;
                }
            });
            if (doPrivileged instanceof IOException) {
                throw (IOException)doPrivileged;
            }
            return doPrivileged;
        }
        finally {}
    }
    
    synchronized boolean askUser() {
        return CheckServicePermission.hasFileAccessPermissions() || this._securityDialog.showDialog();
    }
    
    static long computeMaxLength(final long n) {
        return n * 3L;
    }
    
    static boolean fileChk(final File file) {
        return !file.exists() || DialogFactory.showConfirmDialog((Object)ResourceManager.getString("APIImpl.file.save.fileExist", file.getPath()), ResourceManager.getMessage("APIImpl.file.save.fileExistTitle")) == 0;
    }
    
    static {
        FileSaveServiceImpl._sharedInstance = null;
    }
}
