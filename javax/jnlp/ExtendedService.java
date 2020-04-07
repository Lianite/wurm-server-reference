// 
// Decompiled by Procyon v0.5.30
// 

package javax.jnlp;

import java.io.IOException;
import java.io.File;

public interface ExtendedService
{
    FileContents openFile(final File p0) throws IOException;
    
    FileContents[] openFiles(final File[] p0) throws IOException;
}
