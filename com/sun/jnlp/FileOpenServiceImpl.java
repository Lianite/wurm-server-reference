// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.jnlp;

import java.util.Vector;
import java.lang.reflect.Method;
import java.security.AccessController;
import java.io.File;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.awt.Component;
import javax.swing.JFileChooser;
import java.security.PrivilegedAction;
import javax.jnlp.FileContents;
import com.sun.deploy.config.Config;
import javax.swing.filechooser.FileSystemView;
import javax.jnlp.FileOpenService;

public final class FileOpenServiceImpl implements FileOpenService
{
    static FileOpenServiceImpl _sharedInstance;
    static FileSaveServiceImpl _fileSaveServiceImpl;
    
    private FileOpenServiceImpl(final FileSaveServiceImpl fileSaveServiceImpl) {
        FileOpenServiceImpl._fileSaveServiceImpl = fileSaveServiceImpl;
    }
    
    public static synchronized FileOpenService getInstance() {
        if (FileOpenServiceImpl._sharedInstance == null) {
            FileOpenServiceImpl._sharedInstance = new FileOpenServiceImpl((FileSaveServiceImpl)FileSaveServiceImpl.getInstance());
        }
        return FileOpenServiceImpl._sharedInstance;
    }
    
    public static FileSystemView getFileSystemView() {
        FileSystemView fileSystemView = FileSystemView.getFileSystemView();
        if (Config.getInstance().useAltFileSystemView()) {
            final String property = System.getProperty("java.version");
            if (property.startsWith("1.2") || property.startsWith("1.3")) {
                fileSystemView = new WindowsAltFileSystemView();
            }
        }
        return fileSystemView;
    }
    
    public FileContents openFileDialog(final String s, final String[] array) throws IOException {
        try {
            if (!FileOpenServiceImpl._fileSaveServiceImpl.askUser()) {
                return null;
            }
            return AccessController.doPrivileged((PrivilegedAction<FileContents>)new PrivilegedAction() {
                public Object run() {
                    final FileSystemView fileSystemView = FileOpenServiceImpl.getFileSystemView();
                    JFileChooser fileChooser;
                    if (s != null) {
                        fileChooser = new JFileChooser(s, fileSystemView);
                    }
                    else {
                        fileChooser = new JFileChooser(FileOpenServiceImpl._fileSaveServiceImpl.getLastPath(), fileSystemView);
                    }
                    fileChooser.setFileSelectionMode(0);
                    fileChooser.setDialogType(0);
                    fileChooser.setMultiSelectionEnabled(false);
                    if (fileChooser.showOpenDialog(null) == 1) {
                        return null;
                    }
                    final File selectedFile = fileChooser.getSelectedFile();
                    if (selectedFile != null) {
                        try {
                            FileOpenServiceImpl._fileSaveServiceImpl.setLastPath(selectedFile.getPath());
                            return new FileContentsImpl(selectedFile, FileSaveServiceImpl.computeMaxLength(selectedFile.length()));
                        }
                        catch (FileNotFoundException ex) {}
                        catch (IOException ex2) {}
                    }
                    return null;
                }
            });
        }
        finally {}
    }
    
    public FileContents[] openMultiFileDialog(final String s, final String[] array) throws IOException {
        try {
            if (!FileOpenServiceImpl._fileSaveServiceImpl.askUser()) {
                return null;
            }
            return AccessController.doPrivileged((PrivilegedAction<FileContents[]>)new PrivilegedAction() {
                public Object run() {
                    final FileSystemView fileSystemView = FileOpenServiceImpl.getFileSystemView();
                    JFileChooser fileChooser;
                    if (s != null) {
                        fileChooser = new JFileChooser(s, fileSystemView);
                    }
                    else {
                        fileChooser = new JFileChooser(FileOpenServiceImpl._fileSaveServiceImpl.getLastPath(), fileSystemView);
                    }
                    fileChooser.setFileSelectionMode(0);
                    fileChooser.setDialogType(0);
                    fileChooser.setMultiSelectionEnabled(true);
                    if (fileChooser.showOpenDialog(null) == 1) {
                        return null;
                    }
                    final File[] selectedFiles = fileChooser.getSelectedFiles();
                    if (selectedFiles != null && selectedFiles.length > 0) {
                        final FileContents[] array = new FileContents[selectedFiles.length];
                        for (int i = 0; i < selectedFiles.length; ++i) {
                            try {
                                array[i] = new FileContentsImpl(selectedFiles[i], FileSaveServiceImpl.computeMaxLength(selectedFiles[i].length()));
                                FileOpenServiceImpl._fileSaveServiceImpl.setLastPath(selectedFiles[i].getPath());
                            }
                            catch (FileNotFoundException ex) {}
                            catch (IOException ex2) {}
                        }
                        return array;
                    }
                    return null;
                }
            });
        }
        finally {}
    }
    
    static {
        FileOpenServiceImpl._sharedInstance = null;
    }
    
    static class WindowsAltFileSystemView extends FileSystemView
    {
        private static final Object[] noArgs;
        private static final Class[] noArgTypes;
        private static Method listRootsMethod;
        private static boolean listRootsMethodChecked;
        
        public boolean isRoot(final File file) {
            if (!file.isAbsolute()) {
                return false;
            }
            final String parent = file.getParent();
            return parent == null || new File(parent).equals(file);
        }
        
        public File createNewFolder(final File file) throws IOException {
            if (file == null) {
                throw new IOException("Containing directory is null:");
            }
            File file2 = this.createFileObject(file, "New Folder");
            for (int n = 2; file2.exists() && n < 100; file2 = this.createFileObject(file, "New Folder (" + n + ")"), ++n) {}
            if (file2.exists()) {
                throw new IOException("Directory already exists:" + file2.getAbsolutePath());
            }
            file2.mkdirs();
            return file2;
        }
        
        public boolean isHiddenFile(final File file) {
            return false;
        }
        
        public File[] getRoots() {
            final Vector vector = new Vector<FileSystemRoot>();
            vector.addElement(new FileSystemRoot("A:\\"));
            for (char c = 'C'; c <= 'Z'; ++c) {
                final FileSystemRoot fileSystemRoot = new FileSystemRoot(new String(new char[] { c, ':', '\\' }));
                if (fileSystemRoot != null && fileSystemRoot.exists()) {
                    vector.addElement(fileSystemRoot);
                }
            }
            final File[] array = new File[vector.size()];
            vector.copyInto(array);
            return array;
        }
        
        static {
            noArgs = new Object[0];
            noArgTypes = new Class[0];
            WindowsAltFileSystemView.listRootsMethod = null;
            WindowsAltFileSystemView.listRootsMethodChecked = false;
        }
        
        class FileSystemRoot extends File
        {
            public FileSystemRoot(final File file) {
                super(file, "");
            }
            
            public FileSystemRoot(final String s) {
                super(s);
            }
            
            public boolean isDirectory() {
                return true;
            }
        }
    }
}
