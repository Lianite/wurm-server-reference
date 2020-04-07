// 
// Decompiled by Procyon v0.5.30
// 

package javax.activation;

import java.net.URL;
import java.io.InputStream;
import java.io.IOException;
import java.io.File;
import com.sun.activation.registries.LogSupport;
import java.util.Vector;
import com.sun.activation.registries.MimeTypeFile;

public class MimetypesFileTypeMap extends FileTypeMap
{
    private static MimeTypeFile defDB;
    private MimeTypeFile[] DB;
    private static final int PROG = 0;
    private static String defaultType;
    static /* synthetic */ Class class$javax$activation$MimetypesFileTypeMap;
    
    public MimetypesFileTypeMap() {
        final Vector dbv = new Vector(5);
        MimeTypeFile mf = null;
        dbv.addElement(null);
        LogSupport.log("MimetypesFileTypeMap: load HOME");
        try {
            final String user_home = System.getProperty("user.home");
            if (user_home != null) {
                final String path = user_home + File.separator + ".mime.types";
                mf = this.loadFile(path);
                if (mf != null) {
                    dbv.addElement(mf);
                }
            }
        }
        catch (SecurityException ex) {}
        LogSupport.log("MimetypesFileTypeMap: load SYS");
        try {
            final String system_mimetypes = System.getProperty("java.home") + File.separator + "lib" + File.separator + "mime.types";
            mf = this.loadFile(system_mimetypes);
            if (mf != null) {
                dbv.addElement(mf);
            }
        }
        catch (SecurityException ex2) {}
        LogSupport.log("MimetypesFileTypeMap: load JAR");
        this.loadAllResources(dbv, "META-INF/mime.types");
        LogSupport.log("MimetypesFileTypeMap: load DEF");
        Class class$;
        Class class$javax$activation$MimetypesFileTypeMap;
        if (MimetypesFileTypeMap.class$javax$activation$MimetypesFileTypeMap == null) {
            class$javax$activation$MimetypesFileTypeMap = (MimetypesFileTypeMap.class$javax$activation$MimetypesFileTypeMap = (class$ = class$("javax.activation.MimetypesFileTypeMap")));
        }
        else {
            class$ = (class$javax$activation$MimetypesFileTypeMap = MimetypesFileTypeMap.class$javax$activation$MimetypesFileTypeMap);
        }
        final Class clazz = class$javax$activation$MimetypesFileTypeMap;
        synchronized (class$) {
            if (MimetypesFileTypeMap.defDB == null) {
                MimetypesFileTypeMap.defDB = this.loadResource("/META-INF/mimetypes.default");
            }
        }
        if (MimetypesFileTypeMap.defDB != null) {
            dbv.addElement(MimetypesFileTypeMap.defDB);
        }
        dbv.copyInto(this.DB = new MimeTypeFile[dbv.size()]);
    }
    
    private MimeTypeFile loadResource(final String name) {
        InputStream clis = null;
        try {
            clis = SecuritySupport.getResourceAsStream(this.getClass(), name);
            if (clis != null) {
                final MimeTypeFile mf = new MimeTypeFile(clis);
                if (LogSupport.isLoggable()) {
                    LogSupport.log("MimetypesFileTypeMap: successfully loaded mime types file: " + name);
                }
                return mf;
            }
            if (LogSupport.isLoggable()) {
                LogSupport.log("MimetypesFileTypeMap: not loading mime types file: " + name);
            }
        }
        catch (IOException e) {
            if (LogSupport.isLoggable()) {
                LogSupport.log("MimetypesFileTypeMap: can't load " + name, e);
            }
        }
        catch (SecurityException sex) {
            if (LogSupport.isLoggable()) {
                LogSupport.log("MimetypesFileTypeMap: can't load " + name, sex);
            }
        }
        finally {
            try {
                if (clis != null) {
                    clis.close();
                }
            }
            catch (IOException ex) {}
        }
        return null;
    }
    
    private void loadAllResources(final Vector v, final String name) {
        boolean anyLoaded = false;
        try {
            ClassLoader cld = null;
            cld = SecuritySupport.getContextClassLoader();
            if (cld == null) {
                cld = this.getClass().getClassLoader();
            }
            URL[] urls;
            if (cld != null) {
                urls = SecuritySupport.getResources(cld, name);
            }
            else {
                urls = SecuritySupport.getSystemResources(name);
            }
            if (urls != null) {
                if (LogSupport.isLoggable()) {
                    LogSupport.log("MimetypesFileTypeMap: getResources");
                }
                for (int i = 0; i < urls.length; ++i) {
                    final URL url = urls[i];
                    InputStream clis = null;
                    Label_0112: {
                        if (!LogSupport.isLoggable()) {
                            break Label_0112;
                        }
                        LogSupport.log("MimetypesFileTypeMap: URL " + url);
                        try {
                            clis = SecuritySupport.openStream(url);
                            if (clis != null) {
                                v.addElement(new MimeTypeFile(clis));
                                anyLoaded = true;
                                if (LogSupport.isLoggable()) {
                                    LogSupport.log("MimetypesFileTypeMap: successfully loaded mime types from URL: " + url);
                                }
                            }
                            else if (LogSupport.isLoggable()) {
                                LogSupport.log("MimetypesFileTypeMap: not loading mime types from URL: " + url);
                            }
                        }
                        catch (IOException ioex) {
                            if (LogSupport.isLoggable()) {
                                LogSupport.log("MimetypesFileTypeMap: can't load " + url, ioex);
                            }
                        }
                        catch (SecurityException sex) {
                            if (LogSupport.isLoggable()) {
                                LogSupport.log("MimetypesFileTypeMap: can't load " + url, sex);
                            }
                        }
                        finally {
                            try {
                                if (clis != null) {
                                    clis.close();
                                }
                            }
                            catch (IOException ex2) {}
                        }
                    }
                }
            }
        }
        catch (Exception ex) {
            if (LogSupport.isLoggable()) {
                LogSupport.log("MimetypesFileTypeMap: can't load " + name, ex);
            }
        }
        if (!anyLoaded) {
            LogSupport.log("MimetypesFileTypeMap: !anyLoaded");
            final MimeTypeFile mf = this.loadResource("/" + name);
            if (mf != null) {
                v.addElement(mf);
            }
        }
    }
    
    private MimeTypeFile loadFile(final String name) {
        MimeTypeFile mtf = null;
        try {
            mtf = new MimeTypeFile(name);
        }
        catch (IOException ex) {}
        return mtf;
    }
    
    public MimetypesFileTypeMap(final String mimeTypeFileName) throws IOException {
        this();
        this.DB[0] = new MimeTypeFile(mimeTypeFileName);
    }
    
    public MimetypesFileTypeMap(final InputStream is) {
        this();
        try {
            this.DB[0] = new MimeTypeFile(is);
        }
        catch (IOException ex) {}
    }
    
    public synchronized void addMimeTypes(final String mime_types) {
        if (this.DB[0] == null) {
            this.DB[0] = new MimeTypeFile();
        }
        this.DB[0].appendToRegistry(mime_types);
    }
    
    public String getContentType(final File f) {
        return this.getContentType(f.getName());
    }
    
    public synchronized String getContentType(final String filename) {
        final int dot_pos = filename.lastIndexOf(".");
        if (dot_pos < 0) {
            return MimetypesFileTypeMap.defaultType;
        }
        final String file_ext = filename.substring(dot_pos + 1);
        if (file_ext.length() == 0) {
            return MimetypesFileTypeMap.defaultType;
        }
        for (int i = 0; i < this.DB.length; ++i) {
            if (this.DB[i] != null) {
                final String result = this.DB[i].getMIMETypeString(file_ext);
                if (result != null) {
                    return result;
                }
            }
        }
        return MimetypesFileTypeMap.defaultType;
    }
    
    static /* synthetic */ Class class$(final String x0) {
        try {
            return Class.forName(x0);
        }
        catch (ClassNotFoundException x) {
            throw new NoClassDefFoundError().initCause(x);
        }
    }
    
    static {
        MimetypesFileTypeMap.defDB = null;
        MimetypesFileTypeMap.defaultType = "application/octet-stream";
    }
}
