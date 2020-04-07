// 
// Decompiled by Procyon v0.5.30
// 

package org.apache.http;

import org.apache.http.util.CharArrayBuffer;

public interface FormattedHeader extends Header
{
    CharArrayBuffer getBuffer();
    
    int getValuePos();
}
