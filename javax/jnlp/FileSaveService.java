// 
// Decompiled by Procyon v0.5.30
// 

package javax.jnlp;

import java.io.IOException;
import java.io.InputStream;

public interface FileSaveService
{
    FileContents saveFileDialog(final String p0, final String[] p1, final InputStream p2, final String p3) throws IOException;
    
    FileContents saveAsFileDialog(final String p0, final String[] p1, final FileContents p2) throws IOException;
}
