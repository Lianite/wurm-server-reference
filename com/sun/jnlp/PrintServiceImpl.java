// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.jnlp;

import com.sun.deploy.resources.ResourceManager;
import java.awt.print.PrinterException;
import com.sun.deploy.util.Trace;
import java.awt.print.Printable;
import java.awt.print.Pageable;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.awt.print.PrinterJob;
import java.awt.print.PageFormat;
import javax.jnlp.PrintService;

public final class PrintServiceImpl implements PrintService
{
    private static PrintServiceImpl _sharedInstance;
    private static SmartSecurityDialog _securityDialog;
    private PageFormat _pageFormat;
    
    private PrintServiceImpl() {
        this._pageFormat = null;
    }
    
    public static synchronized PrintServiceImpl getInstance() {
        if (PrintServiceImpl._sharedInstance == null) {
            PrintServiceImpl._sharedInstance = new PrintServiceImpl();
        }
        return PrintServiceImpl._sharedInstance;
    }
    
    public PageFormat getDefaultPage() {
        final PrinterJob printerJob = PrinterJob.getPrinterJob();
        if (printerJob != null) {
            return AccessController.doPrivileged((PrivilegedAction<PageFormat>)new PrivilegedAction() {
                public Object run() {
                    return printerJob.defaultPage();
                }
            });
        }
        return null;
    }
    
    public PageFormat showPageFormatDialog(final PageFormat pageFormat) {
        final PrinterJob printerJob = PrinterJob.getPrinterJob();
        if (printerJob != null) {
            return AccessController.doPrivileged((PrivilegedAction<PageFormat>)new PrivilegedAction() {
                public Object run() {
                    PrintServiceImpl.this._pageFormat = printerJob.pageDialog(pageFormat);
                    return PrintServiceImpl.this._pageFormat;
                }
            });
        }
        return null;
    }
    
    public synchronized boolean print(final Pageable pageable) {
        return this.doPrinting(null, pageable);
    }
    
    public synchronized boolean print(final Printable printable) {
        return this.doPrinting(printable, null);
    }
    
    private boolean doPrinting(final Printable printable, final Pageable pageable) {
        if (!this.askUser()) {
            return false;
        }
        final PrinterJob printerJob = PrinterJob.getPrinterJob();
        if (printerJob == null) {
            return false;
        }
        try {
            return AccessController.doPrivileged((PrivilegedAction<Boolean>)new PrivilegedAction() {
                public Object run() {
                    if (pageable != null) {
                        printerJob.setPageable(pageable);
                    }
                    else if (PrintServiceImpl.this._pageFormat == null) {
                        printerJob.setPrintable(printable);
                    }
                    else {
                        printerJob.setPrintable(printable, PrintServiceImpl.this._pageFormat);
                    }
                    if (printerJob.printDialog()) {
                        new Thread(new Runnable() {
                            public void run() {
                                try {
                                    printerJob.print();
                                }
                                catch (PrinterException ex) {
                                    Trace.ignoredException((Exception)ex);
                                }
                            }
                        }).start();
                        return Boolean.TRUE;
                    }
                    return Boolean.FALSE;
                }
            });
        }
        finally {}
    }
    
    private synchronized boolean askUser() {
        return CheckServicePermission.hasPrintAccessPermissions() || requestPrintPermission();
    }
    
    public static boolean requestPrintPermission() {
        if (PrintServiceImpl._securityDialog == null) {
            PrintServiceImpl._securityDialog = new SmartSecurityDialog(ResourceManager.getString("APIImpl.print.message"), true);
        }
        return PrintServiceImpl._securityDialog.showDialog();
    }
    
    static {
        PrintServiceImpl._sharedInstance = null;
        PrintServiceImpl._securityDialog = null;
    }
}
