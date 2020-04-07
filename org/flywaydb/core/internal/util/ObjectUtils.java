// 
// Decompiled by Procyon v0.5.30
// 

package org.flywaydb.core.internal.util;

public class ObjectUtils
{
    public static boolean nullSafeEquals(final Object o1, final Object o2) {
        return o1 == o2 || (o1 != null && o2 != null && o1.equals(o2));
    }
}
