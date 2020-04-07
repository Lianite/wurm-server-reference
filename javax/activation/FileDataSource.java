// 
// Decompiled by Procyon v0.5.30
// 

package javax.activation;

import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.IOException;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.File;

public class FileDataSource implements DataSource
{
    private File _file;
    private FileTypeMap typeMap;
    
    public FileDataSource(final File file) {
        this._file = null;
        this.typeMap = null;
        this._file = file;
    }
    
    public FileDataSource(final String name) {
        this(new File(name));
    }
    
    public InputStream getInputStream() throws IOException {
        return new FileInputStream(this._file);
    }
    
    public OutputStream getOutputStream() throws IOException {
        return new FileOutputStream(this._file);
    }
    
    public String getContentType() {
        if (this.typeMap == null) {
            return FileTypeMap.getDefaultFileTypeMap().getContentType(this._file);
        }
        return this.typeMap.getContentType(this._file);
    }
    
    public String getName() {
        return this._file.getName();
    }
    
    public File getFile() {
        return this._file;
    }
    
    public void setFileTypeMap(final FileTypeMap map) {
        this.typeMap = map;
    }
}
