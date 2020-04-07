// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.jnlp;

import javax.jnlp.UnavailableServiceException;
import java.security.AccessController;
import java.security.PrivilegedAction;
import javax.jnlp.ServiceManagerStub;

public final class JnlpLookupStub implements ServiceManagerStub
{
    public Object lookup(final String s) throws UnavailableServiceException {
        final Object doPrivileged = AccessController.doPrivileged((PrivilegedAction<Object>)new PrivilegedAction() {
            public Object run() {
                return JnlpLookupStub.this.findService(s);
            }
        });
        if (doPrivileged == null) {
            throw new UnavailableServiceException(s);
        }
        return doPrivileged;
    }
    
    private Object findService(final String s) {
        if (s != null) {
            if (s.equals("javax.jnlp.BasicService")) {
                return BasicServiceImpl.getInstance();
            }
            if (s.equals("javax.jnlp.FileOpenService")) {
                return FileOpenServiceImpl.getInstance();
            }
            if (s.equals("javax.jnlp.FileSaveService")) {
                return FileSaveServiceImpl.getInstance();
            }
            if (s.equals("javax.jnlp.ExtensionInstallerService")) {
                return ExtensionInstallerServiceImpl.getInstance();
            }
            if (s.equals("javax.jnlp.DownloadService")) {
                return DownloadServiceImpl.getInstance();
            }
            if (s.equals("javax.jnlp.ClipboardService")) {
                return ClipboardServiceImpl.getInstance();
            }
            if (s.equals("javax.jnlp.PrintService")) {
                return PrintServiceImpl.getInstance();
            }
            if (s.equals("javax.jnlp.PersistenceService")) {
                return PersistenceServiceImpl.getInstance();
            }
            if (s.equals("javax.jnlp.ExtendedService")) {
                return ExtendedServiceImpl.getInstance();
            }
            if (s.equals("javax.jnlp.SingleInstanceService")) {
                return SingleInstanceServiceImpl.getInstance();
            }
        }
        return null;
    }
    
    public String[] getServiceNames() {
        if (ExtensionInstallerServiceImpl.getInstance() != null) {
            return new String[] { "javax.jnlp.BasicService", "javax.jnlp.FileOpenService", "javax.jnlp.FileSaveService", "javax.jnlp.ExtensionInstallerService", "javax.jnlp.DownloadService", "javax.jnlp.ClipboardService", "javax.jnlp.PersistenceService", "javax.jnlp.PrintService", "javax.jnlp.ExtendedService", "javax.jnlp.SingleInstanceService" };
        }
        return new String[] { "javax.jnlp.BasicService", "javax.jnlp.FileOpenService", "javax.jnlp.FileSaveService", "javax.jnlp.DownloadService", "javax.jnlp.ClipboardService", "javax.jnlp.PersistenceService", "javax.jnlp.PrintService", "javax.jnlp.ExtendedService", "javax.jnlp.SingleInstanceService" };
    }
}
