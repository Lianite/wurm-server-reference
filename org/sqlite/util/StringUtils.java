// 
// Decompiled by Procyon v0.5.30
// 

package org.sqlite.util;

import java.util.Iterator;
import java.util.List;

public class StringUtils
{
    public static String join(final List<String> list, final String separator) {
        final StringBuilder sb = new StringBuilder();
        boolean first = true;
        for (final String item : list) {
            if (first) {
                first = false;
            }
            else {
                sb.append(separator);
            }
            sb.append(item);
        }
        return sb.toString();
    }
}
