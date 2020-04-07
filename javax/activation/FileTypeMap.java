// 
// Decompiled by Procyon v0.5.30
// 

package javax.activation;

import java.io.File;

public abstract class FileTypeMap
{
    private static FileTypeMap defaultMap;
    static /* synthetic */ Class class$javax$activation$FileTypeMap;
    
    public abstract String getContentType(final File p0);
    
    public abstract String getContentType(final String p0);
    
    public static void setDefaultFileTypeMap(final FileTypeMap map) {
        final SecurityManager security = System.getSecurityManager();
        if (security != null) {
            try {
                security.checkSetFactory();
            }
            catch (SecurityException ex) {
                if (((FileTypeMap.class$javax$activation$FileTypeMap == null) ? (FileTypeMap.class$javax$activation$FileTypeMap = class$("javax.activation.FileTypeMap")) : FileTypeMap.class$javax$activation$FileTypeMap).getClassLoader() != map.getClass().getClassLoader()) {
                    throw ex;
                }
            }
        }
        FileTypeMap.defaultMap = map;
    }
    
    public static FileTypeMap getDefaultFileTypeMap() {
        if (FileTypeMap.defaultMap == null) {
            FileTypeMap.defaultMap = new MimetypesFileTypeMap();
        }
        return FileTypeMap.defaultMap;
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
        FileTypeMap.defaultMap = null;
    }
}
