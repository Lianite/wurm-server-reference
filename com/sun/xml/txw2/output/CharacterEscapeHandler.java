// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.xml.txw2.output;

import java.io.IOException;
import java.io.Writer;

public interface CharacterEscapeHandler
{
    void escape(final char[] p0, final int p1, final int p2, final boolean p3, final Writer p4) throws IOException;
}
