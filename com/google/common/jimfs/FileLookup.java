// 
// Decompiled by Procyon v0.5.30
// 

package com.google.common.jimfs;

import java.io.IOException;

public interface FileLookup
{
    File lookup() throws IOException;
}
