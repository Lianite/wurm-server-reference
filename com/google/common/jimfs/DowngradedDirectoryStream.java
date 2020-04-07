// 
// Decompiled by Procyon v0.5.30
// 

package com.google.common.jimfs;

import java.io.IOException;
import java.util.Iterator;
import com.google.common.base.Preconditions;
import java.nio.file.SecureDirectoryStream;
import java.nio.file.Path;
import java.nio.file.DirectoryStream;

final class DowngradedDirectoryStream implements DirectoryStream<Path>
{
    private final SecureDirectoryStream<Path> secureDirectoryStream;
    
    DowngradedDirectoryStream(final SecureDirectoryStream<Path> secureDirectoryStream) {
        this.secureDirectoryStream = Preconditions.checkNotNull(secureDirectoryStream);
    }
    
    @Override
    public Iterator<Path> iterator() {
        return this.secureDirectoryStream.iterator();
    }
    
    @Override
    public void close() throws IOException {
        this.secureDirectoryStream.close();
    }
}
