// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.jnlp;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import com.sun.deploy.util.Trace;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.awt.datatransfer.Transferable;
import java.awt.Toolkit;
import com.sun.deploy.resources.ResourceManager;
import java.awt.datatransfer.Clipboard;
import javax.jnlp.ClipboardService;

public final class ClipboardServiceImpl implements ClipboardService
{
    private static ClipboardServiceImpl _sharedInstance;
    private Clipboard _sysClipboard;
    private SmartSecurityDialog _readDialog;
    private SmartSecurityDialog _writeDialog;
    
    private ClipboardServiceImpl() {
        this._sysClipboard = null;
        this._readDialog = null;
        this._writeDialog = null;
        this._readDialog = new SmartSecurityDialog(ResourceManager.getString("APIImpl.clipboard.message.read"));
        this._writeDialog = new SmartSecurityDialog(ResourceManager.getString("APIImpl.clipboard.message.write"));
        final Toolkit defaultToolkit = Toolkit.getDefaultToolkit();
        if (defaultToolkit != null) {
            this._sysClipboard = defaultToolkit.getSystemClipboard();
        }
    }
    
    public static synchronized ClipboardServiceImpl getInstance() {
        if (ClipboardServiceImpl._sharedInstance == null) {
            ClipboardServiceImpl._sharedInstance = new ClipboardServiceImpl();
        }
        return ClipboardServiceImpl._sharedInstance;
    }
    
    public Transferable getContents() {
        if (!this.askUser(false)) {
            return null;
        }
        return AccessController.doPrivileged((PrivilegedAction<Transferable>)new PrivilegedAction() {
            public Object run() {
                return ClipboardServiceImpl.this._sysClipboard.getContents(null);
            }
        });
    }
    
    public void setContents(final Transferable transferable) {
        if (!this.askUser(true)) {
            return;
        }
        AccessController.doPrivileged((PrivilegedAction<Object>)new PrivilegedAction() {
            public Object run() {
                if (transferable != null) {
                    final DataFlavor[] transferDataFlavors = transferable.getTransferDataFlavors();
                    if (transferDataFlavors == null || transferDataFlavors[0] == null) {
                        return null;
                    }
                    try {
                        if (transferable.getTransferData(transferDataFlavors[0]) == null) {
                            return null;
                        }
                    }
                    catch (IOException ex) {
                        Trace.ignoredException((Exception)ex);
                    }
                    catch (UnsupportedFlavorException ex2) {
                        Trace.ignoredException((Exception)ex2);
                    }
                }
                ClipboardServiceImpl.this._sysClipboard.setContents(transferable, null);
                return null;
            }
        });
    }
    
    private synchronized boolean askUser(final boolean b) {
        return this.hasClipboard() && (CheckServicePermission.hasClipboardPermissions() || (b ? this._writeDialog.showDialog() : this._readDialog.showDialog()));
    }
    
    private boolean hasClipboard() {
        return this._sysClipboard != null;
    }
    
    static {
        ClipboardServiceImpl._sharedInstance = null;
    }
}
