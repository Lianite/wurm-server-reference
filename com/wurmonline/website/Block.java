// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.website;

import java.io.PrintWriter;
import javax.servlet.http.HttpServletRequest;

public abstract class Block
{
    public abstract void write(final HttpServletRequest p0, final PrintWriter p1, final LoginInfo p2);
}
