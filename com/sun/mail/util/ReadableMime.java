// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.mail.util;

import javax.mail.MessagingException;
import java.io.InputStream;

public interface ReadableMime
{
    InputStream getMimeStream() throws MessagingException;
}
