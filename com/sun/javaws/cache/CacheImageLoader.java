// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.javaws.cache;

import com.sun.javaws.exceptions.JNLPException;
import javax.swing.SwingUtilities;
import java.io.File;
import java.awt.Image;
import java.io.IOException;
import java.net.MalformedURLException;
import com.sun.deploy.util.Trace;
import java.net.URL;
import com.sun.javaws.jnl.IconDesc;
import java.util.ArrayList;

public class CacheImageLoader implements Runnable
{
    private static CacheImageLoader _instance;
    private final Object _imageLoadingLock;
    private boolean _running;
    private ArrayList _toLoad;
    
    private CacheImageLoader() {
        this._imageLoadingLock = new Object();
        this._running = false;
        this._toLoad = new ArrayList();
    }
    
    public static CacheImageLoader getInstance() {
        if (CacheImageLoader._instance == null) {
            CacheImageLoader._instance = new CacheImageLoader();
        }
        return CacheImageLoader._instance;
    }
    
    public void loadImage(final IconDesc iconDesc, final CacheImageLoaderCallback cacheImageLoaderCallback) {
        boolean b = false;
        synchronized (this._imageLoadingLock) {
            if (!this._running) {
                this._running = true;
                b = true;
            }
            this._toLoad.add(new LoadEntry(iconDesc, cacheImageLoaderCallback));
        }
        if (b) {
            new Thread(this).start();
        }
    }
    
    public void loadImage(final URL url, final CacheImageLoaderCallback cacheImageLoaderCallback) {
        boolean b = false;
        synchronized (this._imageLoadingLock) {
            if (!this._running) {
                this._running = true;
                b = true;
            }
            this._toLoad.add(new LoadEntry(url, cacheImageLoaderCallback));
        }
        if (b) {
            new Thread(this).start();
        }
    }
    
    public void run() {
        int i = 0;
        while (i == 0) {
            LoadEntry loadEntry = null;
            synchronized (this._imageLoadingLock) {
                if (this._toLoad.size() > 0) {
                    loadEntry = this._toLoad.remove(0);
                }
                else {
                    i = 1;
                    this._running = false;
                }
            }
            if (i == 0) {
                try {
                    DiskCacheEntry cachedVersion = null;
                    Image loadImage = null;
                    File file = null;
                    URL url = loadEntry._url;
                    if (url == null) {
                        cachedVersion = DownloadProtocol.getCachedVersion(loadEntry._id.getLocation(), loadEntry._id.getVersion(), 2);
                        if (cachedVersion != null) {
                            try {
                                file = cachedVersion.getFile();
                                url = file.toURL();
                            }
                            catch (Exception ex3) {}
                        }
                    }
                    if (url != null) {
                        loadImage = CacheUtilities.getSharedInstance().loadImage(url);
                    }
                    if (loadImage != null) {
                        publish(loadEntry, loadImage, file, false);
                    }
                    if (loadEntry._id == null) {
                        continue;
                    }
                    new DelayedImageLoader(loadEntry, loadImage, cachedVersion).start();
                }
                catch (MalformedURLException ex) {
                    Trace.ignoredException((Exception)ex);
                }
                catch (IOException ex2) {
                    Trace.ignoredException((Exception)ex2);
                }
            }
        }
    }
    
    private static void publish(final LoadEntry loadEntry, final Image image, final File file, final boolean b) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                if (b) {
                    loadEntry._cb.finalImageAvailable(loadEntry._id, image, file);
                }
                else {
                    loadEntry._cb.imageAvailable(loadEntry._id, image, file);
                }
            }
        });
    }
    
    static {
        CacheImageLoader._instance = null;
    }
    
    private class LoadEntry
    {
        public IconDesc _id;
        public URL _url;
        public CacheImageLoaderCallback _cb;
        
        public LoadEntry(final IconDesc id, final CacheImageLoaderCallback cb) {
            this._id = id;
            this._cb = cb;
            this._url = null;
        }
        
        public LoadEntry(final URL url, final CacheImageLoaderCallback cb) {
            this._url = url;
            this._cb = cb;
            this._id = null;
        }
    }
    
    private class DelayedImageLoader extends Thread
    {
        private LoadEntry _entry;
        private Image _image;
        private DiskCacheEntry _dce;
        
        public DelayedImageLoader(final LoadEntry entry, final Image image, final DiskCacheEntry dce) {
            this._entry = entry;
            this._image = image;
            this._dce = dce;
        }
        
        public void run() {
            try {
                File file = null;
                if (DownloadProtocol.isUpdateAvailable(this._entry._id.getLocation(), this._entry._id.getVersion(), 2)) {
                    this._dce = DownloadProtocol.getResource(this._entry._id.getLocation(), this._entry._id.getVersion(), 2, false, null);
                    if (this._dce != null) {
                        file = this._dce.getFile();
                    }
                    if (file != null) {
                        this._image = CacheUtilities.getSharedInstance().loadImage(file.getPath());
                    }
                    publish(this._entry, this._image, file, false);
                }
                else if (this._dce != null) {
                    file = this._dce.getFile();
                }
                publish(this._entry, this._image, file, true);
            }
            catch (MalformedURLException ex) {
                Trace.ignoredException((Exception)ex);
            }
            catch (IOException ex2) {
                Trace.ignoredException((Exception)ex2);
            }
            catch (JNLPException ex3) {
                Trace.ignoredException((Exception)ex3);
            }
        }
    }
}
